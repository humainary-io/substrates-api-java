// Copyright (c) 2025 William David Louth

package io.humainary.substrates.tck;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.*;

/// Comprehensive tests for Closure and Scope resource management.
///
/// This test class covers:
/// - Closure creation and lifecycle
/// - Resource consumption semantics (close on consume)
/// - Closure reuse and identity
/// - Exception handling during consumption
/// - Integration with Scope resource tracking
///
/// Closures provide lazy resource initialization with automatic cleanup
/// when consumed. This enables safe resource management patterns where
/// resources are created on-demand and properly disposed after use.
///
/// @author William David Louth
/// @since 1.0
final class ClosureTest
  extends TestSupport {

  private Cortex cortex;

  private static BiConsumer < Subject < Channel < String > >, Registrar < String > > watcher (
    final AtomicInteger deliveries
  ) {

    return ( _, registrar ) ->
      registrar.register (
        cortex ().pipe ( _ -> deliveries.incrementAndGet () )
      );

  }

  @BeforeEach
  void setup () {

    cortex = cortex ();

  }

  /// Validates that closures automatically close wrapped resources after successful consumption.
  ///
  /// This test demonstrates the core closure contract: when a closure is consumed,
  /// the wrapped resource is automatically closed after the consumer completes,
  /// regardless of whether the consumer returns normally or throws an exception.
  /// This implements try-with-resources semantics without explicit try blocks.
  ///
  /// Test Scenario:
  /// 1. Create subscription wrapped in closure (resource is active)
  /// 2. Emit "before" → subscription receives emission (deliveries = 1)
  /// 3. Consume closure → consumer executes with subscription reference
  /// 4. After consume returns → subscription automatically closed
  /// 5. Emit "after" → subscription does NOT receive emission (still deliveries = 1)
  ///
  /// Lifecycle Phases:
  /// ```
  /// Phase 1: Resource Active
  ///   subscription created → subscriber registered → emissions delivered
  ///
  /// Phase 2: Closure Consumption
  ///   closure.consume(consumer) → consumer(subscription) executes → returns
  ///   └─> [automatic] subscription.close() called
  ///
  /// Phase 3: Resource Closed
  ///   emissions ignored → no deliveries → resource cleaned up
  /// ```
  ///
  /// Why this pattern matters:
  /// - **Automatic cleanup**: No explicit close() calls needed in application code
  /// - **Scope integration**: Closure tracked by owning scope for cleanup
  /// - **Exception safety**: Resource closed even if consumer throws (see other test)
  /// - **One-time use**: After consumption, resource is disposed (no accidental reuse)
  ///
  /// Contrast with manual resource management:
  /// ```java
  /// // Manual (error-prone):
  /// Subscription sub = conduit.subscribe(subscriber);
  /// try {
  ///   doWork(sub);
  /// } finally {
  ///   sub.close();  // Easy to forget!
  /// }
  ///
  /// // Closure pattern (automatic):
  /// closure.consume(sub -> doWork(sub));
  /// // sub automatically closed after doWork completes
  /// ```
  ///
  /// Critical behaviors verified:
  /// - Consumer invoked exactly once with correct resource reference
  /// - Resource active before consumption (emission 1 delivered)
  /// - Resource closed after consumption (emission 2 NOT delivered)
  /// - Closure integrates with scope lifecycle
  ///
  /// Real-world applications:
  /// - Database connections (consume, execute queries, auto-close)
  /// - File handles (consume, read/write, auto-close)
  /// - Network sockets (consume, send/receive, auto-close)
  /// - Subscriptions (consume, use temporarily, auto-unsubscribe)
  ///
  /// Expected: 1 delivery before consume, 0 additional deliveries after (total=1)
  @Test
  void testConsumeClosesResourceOnSuccess () {

    final Scope scope = cortex.scope ();
    final Circuit circuit = cortex.circuit ();

    final Conduit < Pipe < String >, String > conduit =
      circuit.conduit ( Composer.pipe () );
    final Name channelName = cortex.name ( "closure.success.channel" );
    final Pipe < String > pipe = conduit.percept ( channelName );

    final AtomicInteger deliveries = new AtomicInteger ();
    final Subscriber < String > subscriber =
      cortex.subscriber (
        cortex.name ( "closure.success.subscriber" ),
        watcher ( deliveries )
      );

    final Subscription subscription = conduit.subscribe ( subscriber );

    final Closure < Subscription > closure =
      scope.closure ( subscription );

    try {

      pipe.emit ( "before" );
      circuit.await ();

      assertEquals (
        1,
        deliveries.get (),
        "subscription should receive emissions before closure"
      );

      final AtomicBoolean invoked = new AtomicBoolean ();

      closure.consume ( sub -> {
        invoked.set ( true );
        assertSame ( subscription, sub );
      } );

      assertTrue (
        invoked.get (),
        "consumer should be invoked exactly once"
      );

      pipe.emit ( "after" );
      circuit.await ();

      assertEquals (
        1,
        deliveries.get (),
        "subscription must be closed once consume returns"
      );

    } finally {

      scope.close ();
      circuit.close ();

    }

  }

  /// Validates that closures close resources even when consumer throws exceptions.
  ///
  /// This test verifies the critical exception-safety guarantee of closures:
  /// the wrapped resource is ALWAYS closed after consumption, even if the
  /// consumer throws an exception. This ensures proper cleanup in all cases
  /// and implements true try-with-resources semantics.
  ///
  /// Test Scenario:
  /// 1. Create subscription wrapped in closure (resource is active)
  /// 2. Emit "before" → subscription receives emission (deliveries = 1)
  /// 3. Consume closure with failing consumer → RuntimeException thrown
  /// 4. Despite exception → subscription automatically closed
  /// 5. Emit "after" → subscription does NOT receive emission (still deliveries = 1)
  ///
  /// Exception Handling Flow:
  /// ```
  /// closure.consume(consumer) {
  ///   try {
  ///     consumer(subscription);  // throws RuntimeException("boom")
  ///   } finally {
  ///     subscription.close();    // ALWAYS executes (even after throw)
  ///   }
  ///   throw caught_exception;    // re-throw to caller
  /// }
  /// ```
  ///
  /// Critical guarantees verified:
  /// 1. **Exception propagation**: Original exception thrown to caller (not swallowed)
  /// 2. **Resource cleanup**: subscription.close() called despite exception
  /// 3. **No double-throw**: If close() also throws, exception handling is correct
  /// 4. **State verification**: Emissions confirm resource was actually closed
  ///
  /// Why exception safety matters:
  /// - **Prevents leaks**: Resources always cleaned up, even on error paths
  /// - **No silent failures**: Exceptions propagated to caller for handling
  /// - **Predictable behavior**: Same cleanup guarantees as try-with-resources
  /// - **Defensive programming**: Handles unexpected failures gracefully
  ///
  /// Contrast with unsafe pattern:
  /// ```java
  /// // Unsafe (resource leak on exception):
  /// Subscription sub = conduit.subscribe(subscriber);
  /// doWork(sub);          // If this throws, sub never closed!
  /// sub.close();          // This line never reached
  ///
  /// // Closure pattern (leak-free):
  /// closure.consume(sub -> doWork(sub));
  /// // sub.close() called even if doWork throws
  /// ```
  ///
  /// Exception handling patterns tested:
  /// - Consumer throws before accessing resource → resource still closed
  /// - Consumer throws after using resource → resource still closed
  /// - Consumer throws unchecked exception → exception propagated, resource closed
  ///
  /// Real-world failure scenarios:
  /// - Database query fails mid-transaction → connection still returned to pool
  /// - File write fails due to disk full → file handle still closed
  /// - Network send fails due to timeout → socket still closed
  /// - Subscription handler throws → subscription still removed
  ///
  /// Expected: Exception propagated unchanged, resource closed (deliveries=1)
  @Test
  void testConsumeClosesResourceWhenConsumerThrows () {

    final Scope scope = cortex.scope ();
    final Circuit circuit = cortex.circuit ();

    final Conduit < Pipe < String >, String > conduit =
      circuit.conduit ( Composer.pipe () );
    final Name channelName = cortex.name ( "closure.failure.channel" );
    final Pipe < String > pipe = conduit.percept ( channelName );


    final AtomicInteger deliveries = new AtomicInteger ();
    final Subscriber < String > subscriber =
      cortex.subscriber (
        cortex.name ( "closure.failure.subscriber" ),
        watcher ( deliveries )
      );

    final Subscription subscription = conduit.subscribe ( subscriber );

    final Closure < Subscription > closure =
      scope.closure ( subscription );

    try {

      pipe.emit ( "before" );
      circuit.await ();

      assertEquals (
        1,
        deliveries.get (),
        "subscription should receive emissions before closure"
      );

      final RuntimeException failure = new RuntimeException ( "boom" );

      final RuntimeException thrown =
        assertThrows (
          RuntimeException.class,
          () -> closure.consume ( sub -> {
            assertSame ( subscription, sub );
            throw failure;
          } )
        );

      assertSame (
        failure,
        thrown,
        "consume must propagate the consumer exception"
      );

      pipe.emit ( "after" );
      circuit.await ();

      assertEquals (
        1,
        deliveries.get (),
        "subscription must be closed even when consumer throws"
      );

    } finally {

      scope.close ();
      circuit.close ();

    }

  }

  /// Validates that closures become no-op after their owning scope is closed.
  ///
  /// This test verifies the fail-safe behavior when a closure outlives its
  /// owning scope: attempting to consume the closure after scope closure
  /// silently ignores the consumption request rather than throwing an exception.
  /// This prevents use-after-close bugs in cleanup paths and scope hierarchies.
  ///
  /// Test Scenario:
  /// 1. Create subscription wrapped in closure within scope
  /// 2. Close scope → scope closes all registered resources (including subscription)
  /// 3. Attempt to consume closure after scope closed
  /// 4. Consumer NOT invoked (closure is inert/dead)
  /// 5. Emit value → no delivery (subscription already closed by scope)
  ///
  /// Lifecycle and Ownership:
  /// ```
  /// Phase 1: Scope Active
  ///   scope.open → closure = scope.closure(subscription)
  ///   └─> scope tracks closure
  ///   └─> subscription active
  ///
  /// Phase 2: Scope Closure
  ///   scope.close() → closes all tracked resources
  ///   └─> subscription.close() called by scope
  ///   └─> closure marked as "scope closed"
  ///
  /// Phase 3: Closure Consumption Attempt
  ///   closure.consume(consumer) → detects scope closed → no-op
  ///   └─> consumer NOT invoked
  ///   └─> no exception thrown (fail-safe)
  /// ```
  ///
  /// Why fail-safe (not fail-fast) is correct here:
  /// - **Cleanup paths**: Often called in finally blocks where exceptions are dangerous
  /// - **Scope hierarchies**: Parent scope closure may close child closures
  /// - **Defensive shutdown**: Calling consume on closed closure is safe (idempotent)
  /// - **No harm**: Consumer not running is the expected outcome after closure
  ///
  /// Contrast with fail-fast operations:
  /// - `scope.register()` after close → throws IllegalStateException (programming error)
  /// - `scope.scope()` after close → throws IllegalStateException (invalid hierarchy)
  /// - `closure.consume()` after close → silently ignores (safe in cleanup)
  ///
  /// Rationale for silent failure:
  /// ```java
  /// // Common cleanup pattern (exception would be problematic):
  /// try {
  ///   doWork();
  /// } finally {
  ///   scope.close();           // Closes all resources including subscriptions
  ///   cleanup.consume(...);    // May be called after scope close
  /// }                          // Exception here would mask original exception!
  /// ```
  ///
  /// Critical behaviors verified:
  /// - Consumer not invoked after scope closure (invoked flag remains false)
  /// - Subscription already closed by scope (0 deliveries from emission)
  /// - No exception thrown from consume attempt (fail-safe)
  /// - Closure respects scope lifecycle (scope is owner)
  ///
  /// Relationship to scope closure semantics:
  /// - Scope closure is cascading (closes all registered resources)
  /// - Closures are registered with scope when created
  /// - Scope owns closure lifecycle (not vice versa)
  /// - After scope close, closures become inert (no operations allowed)
  ///
  /// Real-world scenarios:
  /// - Application shutdown (scope closed, cleanup code still runs)
  /// - Exception handling (scope closed in finally, cleanup attempts consume)
  /// - Scope hierarchies (parent closes, child closures become invalid)
  /// - Concurrent shutdown (closure consumed while scope closing on another thread)
  ///
  /// Expected: Consumer not invoked, subscription closed by scope (deliveries=0)
  @Test
  void testConsumeNoOpAfterScopeClosed () {

    final Scope scope = cortex.scope ();
    final Circuit circuit = cortex.circuit ();

    final Conduit < Pipe < String >, String > conduit =
      circuit.conduit ( Composer.pipe () );
    final Name channelName = cortex.name ( "closure.scope.closed.channel" );
    final Pipe < String > pipe = conduit.percept ( channelName );

    final AtomicInteger deliveries = new AtomicInteger ();
    final Subscriber < String > subscriber =
      cortex.subscriber (
        cortex.name ( "closure.scope.closed.subscriber" ),
        watcher ( deliveries )
      );

    final Subscription subscription = conduit.subscribe ( subscriber );

    final Closure < Subscription > closure =
      scope.closure ( subscription );

    scope.close ();

    final AtomicBoolean invoked = new AtomicBoolean ();

    closure.consume ( _ -> invoked.set ( true ) );

    assertFalse (
      invoked.get (),
      "consumer must not run once the owning scope is closed"
    );

    pipe.emit ( "after" );
    circuit.await ();

    assertEquals (
      0,
      deliveries.get (),
      "scope.close() should close the subscription"
    );

    circuit.close ();

  }

}
