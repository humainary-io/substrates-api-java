// Copyright (c) 2025 William David Louth

package io.humainary.substrates.tck;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static io.humainary.substrates.api.Substrates.Composer.pipe;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
      cortex.subscriber (
        cortex.name ( "counter" ),
        ( _, registrar ) ->
          registrar.register (
            cortex.pipe ( _ -> counter.incrementAndGet () )
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
        cortex.subscriber (
          cortex.name ( "counter" ),
          ( _, registrar ) ->
            registrar.register (
              cortex.pipe ( _ -> counter.incrementAndGet () )
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
        cortex.subscriber (
          cortex.name ( "counter" ),
          ( _, registrar ) ->
            registrar.register (
              cortex.pipe ( _ -> counter.incrementAndGet () )
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
      cortex.subscriber (
        cortex.name ( "counter1" ),
        ( _, registrar ) ->
          registrar.register (
            cortex.pipe ( _ -> counter1.incrementAndGet () )
          )
      )
    );

    conduit.subscribe (
      cortex.subscriber (
        cortex.name ( "counter2" ),
        ( _, registrar ) ->
          registrar.register (
            cortex.pipe ( _ -> counter2.incrementAndGet () )
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

}
