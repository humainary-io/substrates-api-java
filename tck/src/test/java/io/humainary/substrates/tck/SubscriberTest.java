// Copyright (c) 2025 William David Louth

package io.humainary.substrates.tck;

import io.humainary.substrates.api.Substrates;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static io.humainary.substrates.api.Substrates.Composer.pipe;
import static org.junit.jupiter.api.Assertions.*;

/// Tests to verify that the rebuild mechanism works correctly
/// when subscriptions are added and removed.
///
/// @author William David Louth
/// @since 1.0

final class SubscriberTest
  extends TestSupport {

  private Cortex  cortex;
  private Circuit circuit;

  @BeforeEach
  void setup () {

    cortex = cortex ();

    circuit = cortex.circuit ();

  }

  @AfterEach
  void teardown () {

    circuit.close ();

  }

  /// Validates dynamic subscription: subscribers can be added after channel creation.
  ///
  /// This test verifies a critical feature of the substrate: subscribers can be
  /// added to conduits at runtime, and will immediately start receiving emissions
  /// from existing channels. This enables runtime topology reconfiguration without
  /// stopping the system.
  ///
  /// Timeline:
  /// 1. Create conduit and get channel
  /// 2. Emit 50 values with NO subscribers
  /// 3. Add subscriber (registers counter pipe)
  /// 4. Emit 50 values with subscriber active
  ///
  /// The Rebuild Mechanism:
  /// When a subscriber is added via conduit.subscribe(), the conduit triggers
  /// a rebuild of all existing channels' pipe lists. The subscriber is called
  /// for each existing channel, allowing it to register pipes that will receive
  /// future emissions.
  ///
  /// Key behaviors verified:
  /// - Emissions before subscription are dropped (counter = 0 after phase 2)
  /// - Subscriber sees existing channel via callback
  /// - Emissions after subscription are delivered (counter = 50 after phase 4)
  /// - No emissions are retroactively delivered (only future ones)
  ///
  /// Why this matters:
  /// - Hot-swappable observability (add metrics/tracing without restart)
  /// - Dynamic monitoring (attach debuggers to running circuits)
  /// - Runtime topology changes (rewire neural networks on the fly)
  /// - Gradual system evolution (add features without downtime)
  ///
  /// This is analogous to hot module replacement or live reloading, but for
  /// event-driven data flows.
  ///
  /// Expected: 0 emissions before subscription, 50 after subscription
  @Test
  void testDynamicSubscription () {

    final var conduit =
      circuit.conduit (
        pipe ( Long.class )
      );

    final var counter = new AtomicInteger ( 0 );

    final var pipe =
      conduit.percept (
        cortex.name ( "test" )
      );

    // Emit before subscription
    for ( int i = 0; i < 50; i++ ) {
      pipe.emit ( (long) i );
    }

    circuit.await ();

    assertEquals ( 0, counter.get () );

    // Add subscription
    conduit.subscribe (
      circuit.subscriber (
        cortex.name ( "counter" ),
        ( _, registrar ) ->
          registrar.register (
            _ -> counter.incrementAndGet ()
          )
      )
    );

    // Emit after subscription
    for ( int i = 0; i < 50; i++ ) {
      pipe.emit ( (long) i );
    }

    circuit.await ();

    assertEquals ( 50, counter.get () );

  }

  /// Validates that closing a subscription stops emission delivery immediately.
  ///
  /// This test verifies the complementary operation to dynamic subscription:
  /// subscriptions can be removed at runtime, and the subscriber will immediately
  /// stop receiving emissions. This enables clean detachment of observers without
  /// affecting the rest of the system.
  ///
  /// Timeline:
  /// 1. Subscribe counter to conduit
  /// 2. Emit 50 values → counter receives all 50 (counter = 50)
  /// 3. Close subscription via subscription.close()
  /// 4. Emit another 50 values → counter receives NONE (counter still = 50)
  ///
  /// The Rebuild Mechanism (Removal):
  /// When subscription.close() is called, the conduit triggers another rebuild
  /// of all channels' pipe lists. The closed subscriber is removed from the
  /// subscription registry, so it is NOT called during rebuild. Its pipes are
  /// removed from all channels, and future emissions bypass it entirely.
  ///
  /// Key behaviors verified:
  /// - Emissions while subscribed are delivered (first 50)
  /// - subscription.close() cleanly detaches subscriber
  /// - Emissions after unsubscribe are NOT delivered (second 50 ignored)
  /// - Counter value frozen at 50 (proving no emissions after close)
  /// - No errors or exceptions from emitting to channels with removed subscribers
  ///
  /// Why this matters:
  /// - Memory leak prevention (remove unused observers)
  /// - Clean shutdown (detach monitoring before stopping service)
  /// - Dynamic topology changes (rewire connections without restart)
  /// - Resource management (release expensive subscribers)
  /// - Testing/debugging (attach observer, collect data, detach)
  ///
  /// This is critical for long-running systems where observers may be ephemeral
  /// (e.g., temporary debuggers, time-limited metrics collectors).
  ///
  /// Expected: 50 emissions while subscribed, 0 after unsubscribe
  @Test
  void testEmissionAfterSubscriberRemoved () {

    final var conduit =
      circuit.conduit (
        pipe ( Long.class )
      );

    final var counter = new AtomicInteger ( 0 );

    final var subscription =
      conduit.subscribe (
        circuit.subscriber (
          cortex.name ( "counter" ),
          ( _, registrar ) ->
            registrar.register (
              _ -> counter.incrementAndGet ()
            )
        )
      );

    final var pipe =
      conduit.percept (
        cortex.name ( "test" )
      );

    // Emit with subscriber
    for ( int i = 0; i < 50; i++ ) {
      pipe.emit ( (long) i );
    }

    circuit.await ();

    assertEquals ( 50, counter.get () );

    // Remove subscription
    subscription.close ();

    circuit.await ();

    // Emit after subscriber removed
    for ( int i = 0; i < 50; i++ ) {
      pipe.emit ( (long) i );
    }

    circuit.await ();

    // Counter should not have changed
    assertEquals ( 50, counter.get () );

  }

  @Test
  void testEmissionWithSubscriber () {

    final var conduit =
      circuit.conduit (
        pipe ( Long.class )
      );

    final var counter = new AtomicInteger ( 0 );

    final var subscription =
      conduit.subscribe (
        circuit.subscriber (
          cortex.name ( "counter" ),
          ( _, registrar ) ->
            registrar.register (
              _ -> counter.incrementAndGet ()
            )
        )
      );

    final var pipe =
      conduit.percept (
        cortex.name ( "test" )
      );

    // Emit values with subscriber registered
    for ( int i = 0; i < 100; i++ ) {
      pipe.emit ( (long) i );
    }

    circuit.await ();

    assertEquals ( 100, counter.get () );

    subscription.close ();

  }

  /// Validates that emissions without subscribers are safe (no-op behavior).
  ///
  /// This test verifies a critical robustness property: channels can emit values
  /// even when no subscribers are registered, and the system handles this gracefully
  /// without errors, exceptions, or performance degradation.
  ///
  /// Scenario:
  /// - Create conduit and channel with ZERO subscribers
  /// - Emit 1000 values into the void
  /// - Verify no exceptions or errors occur
  ///
  /// Why This is Important:
  /// In real systems, subscribers may be:
  /// - Not yet attached (startup race condition)
  /// - Temporarily removed (dynamic reconfiguration)
  /// - Conditionally absent (optional observability)
  ///
  /// The substrate MUST handle "emitting to nobody" gracefully rather than:
  /// - Throwing NullPointerException
  /// - Requiring null checks everywhere
  /// - Forcing sentinel/dummy subscribers
  ///
  /// Implementation Detail:
  /// When a channel has no subscribers, its pipe list is empty. The emission
  /// loop simply iterates over zero pipes and completes immediately. This is
  /// more efficient than checking "if subscribers.isEmpty()" on every emit.
  ///
  /// Performance characteristics:
  /// - Emissions without subscribers have minimal overhead (empty loop)
  /// - No allocations or heap pressure
  /// - Circuit queue processes and discards quickly
  /// - Safe for high-frequency emissions during startup
  ///
  /// Real-world scenarios:
  /// - Application starting before monitoring connects
  /// - Testing/debugging with selective instrumentation
  /// - Feature flags disabling certain observers
  /// - Graceful degradation when monitoring service is down
  ///
  /// This design choice (no-op vs error) enables:
  /// - Simpler emission code (no defensive checks needed)
  /// - More robust systems (degrades gracefully)
  /// - Easier testing (no mock subscribers required)
  ///
  /// Expected: 1000 emissions complete without errors or exceptions
  @Test
  void testEmissionWithoutSubscribers () {

    final var conduit =
      circuit.conduit (
        pipe ( Long.class )
      );

    final var pipe =
      conduit.percept (
        cortex.name ( "test" )
      );

    // Emit many values without any subscribers
    for ( int i = 0; i < 1000; i++ ) {
      pipe.emit ( (long) i );
    }

    circuit.await ();

    // No assertions needed - just verify no exceptions

  }

  /// Validates fan-out behavior: multiple subscribers receive all emissions.
  ///
  /// This test verifies that a single channel can broadcast to multiple
  /// subscribers simultaneously, with each subscriber receiving every emission.
  /// This is the foundation of the observer pattern and enables separation
  /// of concerns across different observability dimensions.
  ///
  /// Setup:
  /// - Create conduit with two subscribers (counter1 and counter2)
  /// - Each subscriber registers its own counting pipe
  /// - Get channel and emit 100 values
  ///
  /// Fan-Out Semantics:
  /// When a channel emits a value, it forwards to ALL pipes registered by
  /// ALL subscribers. The pipes are called sequentially (not concurrently)
  /// on the circuit's worker thread, ensuring deterministic order.
  ///
  /// Execution flow for each emission:
  /// ```
  /// emit(value)
  ///   ↓
  /// channel.pipe.emit(value)  // enters circuit queue
  ///   ↓
  /// [circuit thread processes]
  ///   ↓
  /// counter1.pipe.emit(value) → counter1++
  ///   ↓
  /// counter2.pipe.emit(value) → counter2++
  /// ```
  ///
  /// Key behaviors verified:
  /// - Both subscribers receive ALL emissions (100 each)
  /// - No emissions are lost or duplicated
  /// - Subscribers execute independently (counter1 doesn't affect counter2)
  /// - Order is deterministic (sequential on circuit thread)
  ///
  /// Why this matters:
  /// - Separation of concerns (metrics, logging, tracing as separate subscribers)
  /// - Independent observability (add/remove observers without affecting others)
  /// - Fan-out pattern (one source, many sinks)
  /// - Modular monitoring (compose different observers)
  ///
  /// Real-world example:
  /// One HTTP request channel broadcasting to:
  /// - Latency metrics subscriber
  /// - Error logging subscriber
  /// - Distributed tracing subscriber
  /// - Request counting subscriber
  ///
  /// Expected: Both counters reach 100 (proving fan-out works)
  @Test
  void testMultipleSubscribers () {

    final var conduit =
      circuit.conduit (
        pipe ( Long.class )
      );

    final var counter1 = new AtomicInteger ( 0 );
    final var counter2 = new AtomicInteger ( 0 );

    conduit.subscribe (
      circuit.subscriber (
        cortex.name ( "counter1" ),
        ( _, registrar ) ->
          registrar.register (
            _ -> counter1.incrementAndGet ()
          )
      )
    );

    conduit.subscribe (
      circuit.subscriber (
        cortex.name ( "counter2" ),
        ( _, registrar ) ->
          registrar.register (
            _ -> counter2.incrementAndGet ()
          )
      )
    );

    final var pipe =
      conduit.percept (
        cortex.name ( "test" )
      );

    // Emit values - both subscribers should receive
    for ( int i = 0; i < 100; i++ ) {
      pipe.emit ( (long) i );
    }

    circuit.await ();

    assertEquals ( 100, counter1.get () );
    assertEquals ( 100, counter2.get () );

  }


  /// Validates that subscribing with a subscriber from a different circuit throws an exception.
  ///
  /// This test verifies a critical safety mechanism: subscribers are bound to the circuit
  /// that created them and cannot be used with a different circuit. This prevents subtle
  /// threading bugs that would occur if a subscriber's pipes were invoked on the wrong
  /// circuit's worker thread.
  ///
  /// Why this matters:
  /// - Each circuit has its own valve (single-threaded executor)
  /// - Subscribers register pipes that execute on the circuit's worker thread
  /// - Cross-circuit subscription would cause pipes to be invoked on wrong thread
  /// - This could lead to race conditions, data corruption, or deadlocks
  ///
  /// The check happens at subscription time (fail-fast) rather than at emit time,
  /// making the error immediately obvious during development.
  ///
  /// Expected: Substrates.Exception thrown with descriptive message
  @Test
  void testCrossCircuitSubscriptionRejected () {

    final var circuit2 = cortex.circuit ();

    try {

      final var conduit =
        circuit.conduit (
          pipe ( Long.class )
        );

      // Create subscriber from circuit2

      // Attempt to subscribe to conduit from circuit1
      final var exception =
        assertThrows (
          Substrates.Exception.class,
          () -> conduit.subscribe (
            circuit2.subscriber (
              cortex.name ( "cross-circuit" ),
              ( _, registrar ) ->
                registrar.register ( _ -> {
                } )
            ) )
        );

      assertEquals (
        "Subscriber belongs to a different circuit",
        exception.getMessage ()
      );

    } finally {

      circuit2.close ();

    }

  }


  /// Validates that subscribing with the same circuit's subscriber works correctly.
  ///
  /// This is the complementary test to testCrossCircuitSubscriptionRejected.
  /// It verifies that the valve check correctly identifies matching circuits
  /// and allows subscription to proceed normally.
  ///
  /// Expected: No exception, subscription succeeds, emissions are received
  @Test
  void testSameCircuitSubscriptionAllowed () {

    final var conduit =
      circuit.conduit (
        pipe ( Long.class )
      );

    final var counter = new AtomicInteger ( 0 );

    // Create subscriber from same circuit - should work

    // This should NOT throw
    assertDoesNotThrow (
      () -> conduit.subscribe (
        circuit.subscriber (
          cortex.name ( "same-circuit" ),
          ( _, registrar ) ->
            registrar.register ( _ -> counter.incrementAndGet () )
        )
      )
    );

    final var pipe =
      conduit.percept (
        cortex.name ( "test" )
      );

    pipe.emit ( 1L );
    pipe.emit ( 2L );
    pipe.emit ( 3L );

    circuit.await ();

    assertEquals ( 3, counter.get () );

  }


  /// Validates cross-circuit rejection works for the circuit's own source.
  ///
  /// Circuits themselves implement Source, so subscribers can subscribe directly
  /// to the circuit to receive state change notifications. This test verifies
  /// that the valve check also applies to circuit-level subscriptions.
  ///
  /// Expected: Substrates.Exception thrown when subscribing to circuit with
  /// a subscriber from a different circuit
  @Test
  void testCrossCircuitSubscriptionRejectedForCircuitSource () {

    final var circuit2 = cortex.circuit ();

    try {

      // Create subscriber from circuit2
      final var subscriber =
        circuit2. < State > subscriber (
          cortex.name ( "cross-circuit" ),
          ( _, registrar ) ->
            registrar.register ( _ -> {
            } )
        );

      // Attempt to subscribe to circuit1's state source
      final var exception =
        assertThrows (
          Substrates.Exception.class,
          () -> circuit.subscribe ( subscriber )
        );

      assertEquals (
        "Subscriber belongs to a different circuit",
        exception.getMessage ()
      );

    } finally {

      circuit2.close ();

    }

  }

  // ===========================
  // Subscriber.close() Tests - Unregister Subscriptions Across Hubs
  // ===========================

  /// Validates that closing a subscriber closes all its subscriptions across multiple conduits.
  ///
  /// This test verifies the new subscriber lifecycle management: when subscriber.close()
  /// is called, ALL subscriptions created by that subscriber across ALL conduits are
  /// automatically closed. This enables clean subscriber shutdown without tracking
  /// individual subscriptions.
  ///
  /// Timeline:
  /// 1. Create two conduits
  /// 2. Subscribe same subscriber to both conduits
  /// 3. Emit values and verify both receive (counter = 100, 50 from each)
  /// 4. Close subscriber (not individual subscriptions)
  /// 5. Emit more values
  /// 6. Verify counter unchanged (both subscriptions were closed)
  ///
  /// Key behaviors verified:
  /// - Subscriber tracks all its subscriptions internally
  /// - subscriber.close() closes ALL subscriptions atomically
  /// - Emissions after close are NOT delivered to any conduit
  /// - No exceptions from emitting to channels with closed subscriber
  ///
  /// Why this matters:
  /// - Simplified cleanup (close one thing, not many)
  /// - Resource leak prevention (no orphaned subscriptions)
  /// - Clean component shutdown (subscriber represents component boundary)
  /// - Avoids subscription tracking burden on caller
  ///
  /// Expected: 100 total before close (50 each), 100 total after close (no change)
  @Test
  void testSubscriberCloseUnregistersAllSubscriptions () {

    final var conduit1 =
      circuit.conduit (
        pipe ( Long.class )
      );

    final var conduit2 =
      circuit.conduit (
        pipe ( Long.class )
      );

    final var counter = new AtomicInteger ( 0 );

    final var subscriber =
      circuit. < Long > subscriber (
        cortex.name ( "shared" ),
        ( _, registrar ) ->
          registrar.register (
            _ -> counter.incrementAndGet ()
          )
      );

    // Subscribe to both conduits
    conduit1.subscribe ( subscriber );
    conduit2.subscribe ( subscriber );

    final var pipe1 =
      conduit1.percept (
        cortex.name ( "test1" )
      );

    final var pipe2 =
      conduit2.percept (
        cortex.name ( "test2" )
      );

    // Emit to both conduits
    for ( int i = 0; i < 50; i++ ) {
      pipe1.emit ( (long) i );
      pipe2.emit ( (long) i );
    }

    circuit.await ();

    assertEquals ( 100, counter.get () );

    // Close subscriber (should close ALL subscriptions)
    subscriber.close ();

    circuit.await ();

    // Emit after subscriber closed
    for ( int i = 0; i < 50; i++ ) {
      pipe1.emit ( (long) i );
      pipe2.emit ( (long) i );
    }

    circuit.await ();

    // Counter should not have changed
    assertEquals ( 100, counter.get () );

  }


  /// Validates that closing a subscriber is safe when called multiple times.
  ///
  /// This test verifies idempotency: calling subscriber.close() multiple times
  /// does not cause errors or unexpected behavior. This is important for
  /// defensive programming patterns where close() may be called in finally
  /// blocks or by multiple cleanup paths.
  ///
  /// Expected: No exceptions on repeated close calls
  @Test
  void testSubscriberCloseIsIdempotent () {

    final var conduit =
      circuit.conduit (
        pipe ( Long.class )
      );

    final var counter = new AtomicInteger ( 0 );

    final var subscriber =
      circuit. < Long > subscriber (
        cortex.name ( "idempotent" ),
        ( _, registrar ) ->
          registrar.register (
            _ -> counter.incrementAndGet ()
          )
      );

    conduit.subscribe ( subscriber );

    final var pipe =
      conduit.percept (
        cortex.name ( "test" )
      );

    // Emit some values
    for ( int i = 0; i < 10; i++ ) {
      pipe.emit ( (long) i );
    }

    circuit.await ();

    assertEquals ( 10, counter.get () );

    // Close multiple times - should not throw
    assertDoesNotThrow ( subscriber::close );

    circuit.await ();

    assertDoesNotThrow ( subscriber::close );

    circuit.await ();

    assertDoesNotThrow ( subscriber::close );

    circuit.await ();

    // Emit after multiple closes
    for ( int i = 0; i < 10; i++ ) {
      pipe.emit ( (long) i );
    }

    circuit.await ();

    // Counter should not have changed after first close
    assertEquals ( 10, counter.get () );

  }


  /// Validates that subscription.close() and subscriber.close() interact correctly.
  ///
  /// This test verifies behavior when individual subscriptions are closed before
  /// the subscriber is closed. The subscriber should handle already-closed
  /// subscriptions gracefully.
  ///
  /// Timeline:
  /// 1. Subscribe to two conduits
  /// 2. Emit values (counter = 100)
  /// 3. Close subscription1 individually
  /// 4. Emit values (counter = 150, only conduit2 receives)
  /// 5. Close subscriber (should close remaining subscription2)
  /// 6. Emit values (counter stays 150)
  ///
  /// Expected: Partial close via subscription, full cleanup via subscriber.close()
  @Test
  void testMixedSubscriptionAndSubscriberClose () {

    final var conduit1 =
      circuit.conduit (
        pipe ( Long.class )
      );

    final var conduit2 =
      circuit.conduit (
        pipe ( Long.class )
      );

    final var counter = new AtomicInteger ( 0 );

    final var subscriber =
      circuit. < Long > subscriber (
        cortex.name ( "mixed" ),
        ( _, registrar ) ->
          registrar.register (
            _ -> counter.incrementAndGet ()
          )
      );

    final var subscription1 = conduit1.subscribe ( subscriber );
    conduit2.subscribe ( subscriber );

    final var pipe1 =
      conduit1.percept (
        cortex.name ( "test1" )
      );

    final var pipe2 =
      conduit2.percept (
        cortex.name ( "test2" )
      );

    // Emit to both
    for ( int i = 0; i < 50; i++ ) {
      pipe1.emit ( (long) i );
      pipe2.emit ( (long) i );
    }

    circuit.await ();

    assertEquals ( 100, counter.get () );

    // Close only subscription1
    subscription1.close ();

    circuit.await ();

    // Emit again - only conduit2 should receive
    for ( int i = 0; i < 50; i++ ) {
      pipe1.emit ( (long) i );
      pipe2.emit ( (long) i );
    }

    circuit.await ();

    assertEquals ( 150, counter.get () );

    // Close subscriber - should handle already-closed subscription1
    subscriber.close ();

    circuit.await ();

    // Emit again - neither should receive
    for ( int i = 0; i < 50; i++ ) {
      pipe1.emit ( (long) i );
      pipe2.emit ( (long) i );
    }

    circuit.await ();

    assertEquals ( 150, counter.get () );

  }


  /// Validates that closing a subscriber with no subscriptions is safe.
  ///
  /// This test verifies that a newly created subscriber (with no subscriptions)
  /// can be closed without errors. This is an edge case but important for
  /// error handling paths where a subscriber might be created but never used.
  ///
  /// Expected: No exceptions
  @Test
  void testCloseSubscriberWithNoSubscriptions () {

    final var subscriber =
      circuit. < Long > subscriber (
        cortex.name ( "unused" ),
        ( _, registrar ) ->
          registrar.register (
            _ -> {
            }
          )
      );

    // Close without ever subscribing
    assertDoesNotThrow ( subscriber::close );

    circuit.await ();

  }


  /// Validates subscriber close with multiple subscribers and mixed operations.
  ///
  /// This test verifies that closing one subscriber does not affect other
  /// subscribers to the same conduit. Each subscriber maintains its own
  /// subscription list independently.
  ///
  /// Timeline:
  /// 1. Two subscribers subscribe to same conduit
  /// 2. Emit values (both counters = 50)
  /// 3. Close subscriber1
  /// 4. Emit values (counter1 stays 50, counter2 = 100)
  /// 5. Close subscriber2
  /// 6. Emit values (both stay same)
  ///
  /// Expected: Subscriber isolation - closing one doesn't affect others
  @Test
  void testMultipleSubscribersIndependentClose () {

    final var conduit =
      circuit.conduit (
        pipe ( Long.class )
      );

    final var counter1 = new AtomicInteger ( 0 );
    final var counter2 = new AtomicInteger ( 0 );

    final var subscriber1 =
      circuit. < Long > subscriber (
        cortex.name ( "subscriber1" ),
        ( _, registrar ) ->
          registrar.register (
            _ -> counter1.incrementAndGet ()
          )
      );

    final var subscriber2 =
      circuit. < Long > subscriber (
        cortex.name ( "subscriber2" ),
        ( _, registrar ) ->
          registrar.register (
            _ -> counter2.incrementAndGet ()
          )
      );

    conduit.subscribe ( subscriber1 );
    conduit.subscribe ( subscriber2 );

    final var pipe =
      conduit.percept (
        cortex.name ( "test" )
      );

    // Emit values - both should receive
    for ( int i = 0; i < 50; i++ ) {
      pipe.emit ( (long) i );
    }

    circuit.await ();

    assertEquals ( 50, counter1.get () );
    assertEquals ( 50, counter2.get () );

    // Close subscriber1
    subscriber1.close ();

    circuit.await ();

    // Emit again - only subscriber2 should receive
    for ( int i = 0; i < 50; i++ ) {
      pipe.emit ( (long) i );
    }

    circuit.await ();

    assertEquals ( 50, counter1.get () );
    assertEquals ( 100, counter2.get () );

    // Close subscriber2
    subscriber2.close ();

    circuit.await ();

    // Emit again - neither should receive
    for ( int i = 0; i < 50; i++ ) {
      pipe.emit ( (long) i );
    }

    circuit.await ();

    assertEquals ( 50, counter1.get () );
    assertEquals ( 100, counter2.get () );

  }


  /// Validates subscriber close with subscriptions across many conduits.
  ///
  /// This stress test verifies that subscriber.close() correctly handles
  /// subscriptions across many conduits. This exercises the list iteration
  /// and re-entrancy safety in the implementation.
  ///
  /// Expected: All 10 subscriptions closed, no emissions after close
  @SuppressWarnings ( "unchecked" )
  @Test
  void testSubscriberCloseWithManyConduits () {

    final var counter = new AtomicInteger ( 0 );

    final var subscriber =
      circuit. < Long > subscriber (
        cortex.name ( "many" ),
        ( _, registrar ) ->
          registrar.register (
            _ -> counter.incrementAndGet ()
          )
      );

    // Create 10 conduits and subscribe to each
    final Conduit < Pipe < Long >, Long >[] conduits =
      new Conduit[10];

    final Pipe < Long >[] pipes =
      new Pipe[10];

    for ( int i = 0; i < 10; i++ ) {
      conduits[i] = circuit.conduit ( pipe ( Long.class ) );
      conduits[i].subscribe ( subscriber );
      pipes[i] = conduits[i].percept ( cortex.name ( "test" + i ) );
    }

    // Emit to all conduits
    for ( int i = 0; i < 10; i++ ) {
      for ( int j = 0; j < 10; j++ ) {
        pipes[j].emit ( (long) i );
      }
    }

    circuit.await ();

    assertEquals ( 100, counter.get () );

    // Close subscriber
    subscriber.close ();

    circuit.await ();

    // Emit to all conduits again
    for ( int i = 0; i < 10; i++ ) {
      for ( int j = 0; j < 10; j++ ) {
        pipes[j].emit ( (long) i );
      }
    }

    circuit.await ();

    // Counter should not have changed
    assertEquals ( 100, counter.get () );

  }


  /// Validates that registrar enforces temporal constraint.
  ///
  /// The registrar is only valid during the subscriber callback. Calling
  /// register() after the callback has returned must throw IllegalStateException.
  /// This prevents silent mutations that would have no effect until the next
  /// rebuild, enforcing the @Temporal contract documented in the API.
  ///
  /// Expected: IllegalStateException when register() called after callback
  @Test
  void testRegistrarTemporalEnforcement () {

    final var conduit =
      circuit.conduit (
        pipe ( Long.class )
      );

    final var captured =
      new AtomicReference < Substrates.Registrar < Long > > ();

    conduit.subscribe (
      circuit.subscriber (
        cortex.name ( "capture" ),
        ( _, registrar ) -> {
          registrar.register ( _ -> {
          } );
          captured.set ( registrar );
        }
      )
    );

    final var pipe =
      conduit.percept (
        cortex.name ( "test" )
      );

    // Emit to trigger rebuild, which fires the subscriber callback
    pipe.emit ( 1L );

    // Ensure the callback has completed on the circuit thread
    circuit.await ();

    final var registrar = captured.get ();

    assertNotNull ( registrar );

    // Registrar should be closed — calling register must throw
    assertThrows (
      IllegalStateException.class,
      () -> registrar.register (
        _ -> {
        }
      )
    );

    assertThrows (
      IllegalStateException.class,
      () -> registrar.register (
        (Substrates.Pipe < ? super Long >) null
      )
    );

  }

}
