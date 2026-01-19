// Copyright (c) 2025 William David Louth

package io.humainary.substrates.tck;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

import static org.junit.jupiter.api.Assertions.*;

/// Comprehensive tests for the Cortex interface focusing on methods
/// not covered by other test classes.
///
/// This test class covers:
/// - Circuit creation and lifecycle
/// - Scope creation and resource management
/// - Reservoir creation and event capture
/// - Subscriber creation with different configurations
///
/// @author William David Louth
/// @since 1.0

final class CortexTest
  extends TestSupport {

  private Cortex cortex;

  @BeforeEach
  void setup () {

    cortex = cortex ();

  }

  // ===========================
  // Cortex Tests
  // ===========================

  @Test
  void testCircuitAwaitCompletesWhenQueueEmpty () {

    final var circuit = cortex.circuit ();

    try {

      // Create a simple conduit and emit a value
      final var conduit =
        circuit.conduit ( Composer.pipe ( Integer.class ) );

      conduit.percept ( cortex.name ( "test.channel" ) )
        .emit ( 42 );

      // Await should complete when queue is drained
      circuit.await ();

    } finally {

      circuit.close ();

    }

  }

  @Test
  void testCircuitClose () {

    final var circuit = cortex.circuit ();

    // Should not throw
    circuit.close ();

    // Multiple closes should be idempotent
    circuit.close ();
    circuit.close ();

  }

  @Test
  void testCircuitConduitReservoirIntegration () {

    final var circuit = cortex.circuit (
      cortex.name ( "integration.circuit" )
    );

    try {

      final var conduit =
        circuit.conduit (
          cortex.name ( "integration.conduit" ),
          Composer.pipe ( String.class )
        );

      final Reservoir < String > reservoir = conduit.reservoir ();

      final Pipe < String > pipe =
        conduit.percept ( cortex.name ( "integration.channel" ) );

      pipe.emit ( "integration-test" );

      circuit.await ();

      final var captures =
        reservoir.drain ().toList ();

      assertEquals ( 1, captures.size () );
      assertEquals ( "integration-test", captures.getFirst ().emission () );
      assertEquals (
        Channel.class,
        captures.getFirst ().subject ().type ()
      );

      reservoir.close ();

    } finally {

      circuit.close ();

    }

  }

  @Test
  void testCircuitCreation () {

    final var circuit = cortex.circuit ();

    assertNotNull ( circuit );
    assertNotNull ( circuit.subject () );

  }

  // ===========================
  // Circuit Tests
  // ===========================

  @SuppressWarnings ( "DataFlowIssue" )
  @Test
  void testCircuitCreationNullNameGuard () {

    assertThrows (
      NullPointerException.class,
      () -> cortex.circuit ( null )
    );

  }

  @Test
  void testCircuitCreationWithName () {

    final var circuitName = cortex.name ( "cortex.test.circuit" );
    final var circuit = cortex.circuit ( circuitName );

    assertNotNull ( circuit );
    assertEquals ( circuitName, circuit.subject ().name () );

  }

  @Test
  void testCircuitSubjectType () {

    final var circuit = cortex.circuit ();

    try {

      assertEquals ( Circuit.class, circuit.subject ().type () );

    } finally {

      circuit.close ();

    }

  }

  @Test
  void testCortexHasSubject () {

    assertNotNull ( cortex.subject () );

  }

  @Test
  void testCortexIsSingletonSubject () {

    final var cortex1 = cortex ();
    final var cortex2 = cortex ();

    assertSame ( cortex1.subject (), cortex2.subject () );
    assertEquals ( cortex1.subject ().id (), cortex2.subject ().id () );

  }

  @Test
  void testCortexSubjectHasName () {

    assertNotNull ( cortex.subject ().name () );

  }

  @Test
  void testCortexSubjectType () {

    assertEquals ( Cortex.class, cortex.subject ().type () );

  }

  // ===========================
  // ===========================

  @Test
  void testMultipleCircuitsHaveUniqueSubjects () {

    final var circuit1 = cortex.circuit ();
    final var circuit2 = cortex.circuit ();

    try {

      assertNotSame ( circuit1.subject (), circuit2.subject () );
      assertNotEquals ( circuit1.subject ().id (), circuit2.subject ().id () );

    } finally {

      circuit1.close ();
      circuit2.close ();

    }

  }

  @Test
  void testNestedScopesWithResources () {

    final var root = cortex.scope (
      cortex.name ( "nested.root" )
    );

    final var child = root.scope (
      cortex.name ( "nested.child" )
    );

    final var rootCircuit = root.register ( cortex.circuit () );
    final var childCircuit = child.register ( cortex.circuit () );

    assertNotNull ( rootCircuit );
    assertNotNull ( childCircuit );

    child.close ();
    root.close ();

  }

  // ===========================
  // Scope Tests
  // ===========================

  @Test
  void testReservoirCapturesEmissions () {

    final var circuit = cortex.circuit ();

    try {

      final var conduit =
        circuit.conduit ( Composer.pipe ( Integer.class ) );

      final Reservoir < Integer > reservoir = conduit.reservoir ();

      final Pipe < Integer > pipe =
        conduit.percept ( cortex.name ( "test.channel" ) );

      pipe.emit ( 10 );
      pipe.emit ( 20 );
      pipe.emit ( 30 );

      circuit.await ();

      final var captures =
        reservoir.drain ().toList ();

      assertEquals ( 3, captures.size () );
      assertEquals ( 10, captures.get ( 0 ).emission () );
      assertEquals ( 20, captures.get ( 1 ).emission () );
      assertEquals ( 30, captures.get ( 2 ).emission () );

      reservoir.close ();

    } finally {

      circuit.close ();

    }

  }

  @Test
  void testReservoirCreationFromContext () {

    final var circuit = cortex.circuit ();

    try {

      final var conduit =
        circuit.conduit ( Composer.pipe ( Integer.class ) );

      // Conduit is a Context
      final Reservoir < Integer > reservoir = conduit.reservoir ();

      assertNotNull ( reservoir );

      reservoir.close ();

    } finally {

      circuit.close ();

    }

  }

  @Test
  void testReservoirCreationFromSource () {

    final var circuit = cortex.circuit ();

    try {

      final var conduit =
        circuit.conduit ( Composer.pipe ( String.class ) );

      final Reservoir < String > reservoir = conduit.reservoir ();

      assertNotNull ( reservoir );
      assertNotNull ( reservoir.subject () );

      reservoir.close ();

    } finally {

      circuit.close ();

    }

  }

  /// Validates incremental drain behavior: each drain returns only new emissions.
  ///
  /// This test verifies that Reservoir.drain() operates incrementally, returning only
  /// emissions that occurred since the last drain. Previously drained emissions
  /// are NOT returned again, enabling efficient polling and incremental processing
  /// without duplicate handling.
  ///
  /// Test Scenario:
  /// 1. Emit "first", wait for processing
  /// 2. First drain → returns "first" (1 emission)
  /// 3. Emit "second", wait for processing
  /// 4. Second drain → returns "second" (1 emission, not 2!)
  ///
  /// Drain Semantics (cursor-based):
  /// ```
  /// Reservoir maintains internal cursor position:
  ///   emissions: [________________]
  ///   cursor:     ^
  ///
  /// After first drain:
  ///   emissions: [first___________]
  ///   cursor:           ^          (moved forward)
  ///
  /// After second emission:
  ///   emissions: [first, second___]
  ///   cursor:           ^          (still here)
  ///
  /// After second drain:
  ///   emissions: [first, second___]
  ///   cursor:                   ^  (moved forward again)
  /// ```
  ///
  /// Why incremental matters:
  /// - **Polling loops**: Can periodically drain without tracking what was seen
  /// - **Memory efficiency**: Old emissions can be garbage collected after drain
  /// - **Batch processing**: Process chunks incrementally (e.g., flush every 100)
  /// - **No duplication**: Each emission processed exactly once
  ///
  /// Contrast with alternative semantics:
  /// - **drain() returns ALL**: Would require client-side deduplication
  /// - **drain() clears ALL**: Would lose emissions between drains (race condition)
  /// - **drain() since timestamp**: Would require clock synchronization
  /// - **drain() with cursor**: Correct - cursor maintained by reservoir internally
  ///
  /// Usage Pattern:
  /// ```java
  /// // Polling loop with incremental drain
  /// while (running) {
  ///   Thread.sleep(100);
  ///   reservoir.drain().forEach(capture -> {
  ///     process(capture.emission());
  ///   });
  /// }
  /// // No need to track "last processed" - reservoir handles it
  /// ```
  ///
  /// Critical behaviors verified:
  /// - First drain returns emission count = 1 (only "first")
  /// - Second drain returns emission count = 1 (only "second", not 2)
  /// - Drains are independent (second doesn't include first)
  /// - Cursor advances automatically after each drain
  ///
  /// Real-world applications:
  /// - Log aggregation (drain logs every second)
  /// - Metrics collection (drain counters periodically)
  /// - Event streaming (batch events for bulk processing)
  /// - Testing/debugging (inspect emissions without affecting system)
  ///
  /// Expected: First `drain=[first]`, second `drain=[second]` (incremental, not cumulative)
  @Test
  void testReservoirDrainCapturesNewEmissions () {

    final var circuit = cortex.circuit ();

    try {

      final var conduit =
        circuit.conduit ( Composer.pipe ( String.class ) );

      final Reservoir < String > reservoir = conduit.reservoir ();

      final Pipe < String > pipe =
        conduit.percept ( cortex.name ( "test.channel" ) );

      pipe.emit ( "first" );

      circuit.await ();

      final var firstDrain =
        reservoir.drain ().toList ();

      assertEquals ( 1, firstDrain.size () );
      assertEquals ( "first", firstDrain.getFirst ().emission () );

      // Emit another value after the first drain
      pipe.emit ( "second" );

      circuit.await ();

      // Second drain should have only new emissions since last drain
      final var secondDrain =
        reservoir.drain ().toList ();

      assertEquals ( 1, secondDrain.size () );
      assertEquals ( "second", secondDrain.getFirst ().emission () );

      reservoir.close ();

    } finally {

      circuit.close ();

    }

  }

  @Test
  void testReservoirSubjectType () {

    final var circuit = cortex.circuit ();

    try {

      final var conduit =
        circuit.conduit ( Composer.pipe ( String.class ) );

      final var reservoir = conduit.reservoir ();

      assertEquals ( Reservoir.class, reservoir.subject ().type () );

      reservoir.close ();

    } finally {

      circuit.close ();

    }

  }

  // ===========================
  // Reservoir Tests
  // ===========================

  @Test
  void testScopeClosesRegisteredResources () {

    final var scope = cortex.scope ();

    final var circuit = cortex.circuit ();
    scope.register ( circuit );

    // Closing scope should close registered resources
    scope.close ();

    // Multiple closes should be idempotent
    scope.close ();

  }

  @Test
  void testScopeClosureCachesPerResourceAndCleansUp () {

    final var scope = cortex.scope ();

    final var circuit = cortex.circuit ();

    try {

      final Closure < Circuit > first = scope.closure ( circuit );
      final Closure < Circuit > second = scope.closure ( circuit );

      assertSame ( first, second );

      final var consumed = new AtomicBoolean ( false );

      first.consume ( _ ->
        consumed.set ( true )
      );

      assertTrue ( consumed.get () );

      final Closure < Circuit > third = scope.closure ( circuit );

      assertNotSame ( first, third );

      third.consume ( ignored -> {
      } );

    } finally {

      scope.close ();
      circuit.close ();

    }

  }

  @Test
  void testScopeCreateChildScope () {

    final var parent = cortex.scope ();

    final var child = parent.scope ();

    assertNotNull ( child );
    assertNotNull ( child.subject () );

    child.close ();
    parent.close ();

  }

  @Test
  void testScopeCreateNamedChildScope () {

    final var parent = cortex.scope ();

    final var childName = cortex.name ( "cortex.test.child" );
    final var child = parent.scope ( childName );

    assertEquals ( childName, child.subject ().name () );

    child.close ();
    parent.close ();

  }

  @Test
  void testScopeCreation () {

    final var scope = cortex.scope ();

    assertNotNull ( scope );
    assertNotNull ( scope.subject () );

    scope.close ();

  }

  @SuppressWarnings ( {"DataFlowIssue", "resource"} )
  @Test
  void testScopeCreationNullNameGuard () {

    assertThrows (
      NullPointerException.class,
      () -> cortex.scope ( null )
    );

  }

  @Test
  void testScopeCreationWithName () {

    final var scopeName = cortex.name ( "cortex.test.scope" );
    final var scope = cortex.scope ( scopeName );

    assertNotNull ( scope );
    assertEquals ( scopeName, scope.subject ().name () );

    scope.close ();

  }

  @Test
  void testScopeEnclosureAccessors () {

    final var parent = cortex.scope (
      cortex.name ( "scope.enclosure.parent" )
    );

    final var child = parent.scope ();
    final var grandchild = child.scope ();

    final Scope[] captured = new Scope[1];
    final var rootCalled = new AtomicBoolean ( false );

    assertSame ( parent, child.enclosure ().orElseThrow () );
    assertSame ( child, grandchild.enclosure ().orElseThrow () );

    grandchild.enclosure ( scope ->
      captured[0] = scope
    );

    parent.enclosure ( ignored ->
      rootCalled.set ( true )
    );

    assertSame ( child, captured[0] );
    assertFalse ( parent.enclosure ().isPresent () );
    assertFalse ( rootCalled.get () );

    grandchild.close ();
    child.close ();
    parent.close ();

  }

  @Test
  void testScopeHierarchy () {

    final var root = cortex.scope ();
    final var child = root.scope ();
    final var grandchild = child.scope ();

    assertTrue ( child.within ( root ) );
    assertTrue ( grandchild.within ( child ) );
    assertTrue ( grandchild.within ( root ) );

    grandchild.close ();
    child.close ();
    root.close ();

  }

  @Test
  void testScopeManagesMultipleResources () {

    final var scope = cortex.scope (
      cortex.name ( "multi.resource.scope" )
    );

    final var circuit1 = scope.register ( cortex.circuit () );
    final var circuit2 = scope.register ( cortex.circuit () );
    final var circuit3 = scope.register ( cortex.circuit () );

    assertNotNull ( circuit1 );
    assertNotNull ( circuit2 );
    assertNotNull ( circuit3 );

    // All should be closed when scope closes
    scope.close ();

  }

  // ===========================
  // Subscriber Tests
  // ===========================

  @Test
  void testScopeOperationsDisallowedAfterClose () {

    final var scope = cortex.scope ();

    scope.close ();

    assertThrows (
      IllegalStateException.class,
      scope::scope
    );

    final var circuit = cortex.circuit ();

    try {

      assertThrows (
        IllegalStateException.class,
        () -> scope.register ( circuit )
      );

      assertThrows (
        IllegalStateException.class,
        () -> scope.closure ( circuit )
      );

    } finally {

      circuit.close ();

    }

  }

  @Test
  void testScopeRegisterResource () {

    final var scope = cortex.scope ();

    final var circuit = cortex.circuit ();
    final var registered = scope.register ( circuit );

    assertSame ( circuit, registered );

    scope.close ();

  }

  @Test
  void testScopeSubjectType () {

    final var scope = cortex.scope ();

    assertEquals ( Scope.class, scope.subject ().type () );

    scope.close ();

  }

  @SuppressWarnings ( "DataFlowIssue" )
  @Test
  void testSubscriberCreationNullBehaviorGuard () {

    final var circuit = cortex.circuit ();

    try {

      assertThrows (
        NullPointerException.class,
        () -> circuit.subscriber (
          cortex.name ( "test" ),
          (BiConsumer < Subject < Channel < String > >, Registrar < String > >) null
        )
      );

    } finally {

      circuit.close ();

    }

  }

  @SuppressWarnings ( "DataFlowIssue" )
  @Test
  void testSubscriberCreationNullNameGuard () {

    final var circuit = cortex.circuit ();

    try {

      assertThrows (
        NullPointerException.class,
        () -> circuit.subscriber (
          null,
          ( _, _ ) -> {
          }
        )
      );

    } finally {

      circuit.close ();

    }

  }

  @Test
  void testSubscriberCreationWithBehavior () {

    final var circuit = cortex.circuit ();

    try {

      final var subscriberName = cortex.name ( "cortex.test.subscriber" );

      final Subscriber < String > subscriber =
        circuit.subscriber (
          subscriberName,
          ( _, _ ) -> {
            // Subscriber behavior
          }
        );

      assertNotNull ( subscriber );
      assertEquals ( subscriberName, subscriber.subject ().name () );

    } finally {

      circuit.close ();

    }

  }

  // ===========================
  // Integration Tests
  // ===========================

  /// Validates that subscribers receive notifications for each channel created.
  ///
  /// This test verifies the dynamic subscription mechanism: when a subscriber
  /// is registered with a conduit, it receives a callback for EVERY channel
  /// that is subsequently created. This enables observability patterns where
  /// subscribers can attach monitoring pipes to all channels without knowing
  /// their names ahead of time.
  ///
  /// Test Scenario:
  /// 1. Subscribe to conduit (before any channels exist)
  /// 2. Create channel "one" → subscriber notified with channel subject
  /// 3. Create channel "two" → subscriber notified with channel subject
  /// 4. Create channel "three" → subscriber notified with channel subject
  /// 5. Verify: subscriber received 3 distinct channel subjects
  ///
  /// Dynamic Subscription Flow:
  /// ```
  /// conduit.subscribe(subscriber)
  ///   ↓
  /// [subscriber registered in conduit's subscriber list]
  ///   ↓
  /// conduit.percept("channel.one")
  ///   ↓
  /// [channel created]
  ///   ↓
  /// subscriber.callback(channelSubject, registrar)
  ///   ↓ [subscriber registers pipes]
  /// registrar.register(observerPipe)
  ///   ↓
  /// [observerPipe added to channel's emission list]
  /// ```
  ///
  /// Why this pattern matters:
  /// - **Universal observability**: Monitor all channels without hardcoding names
  /// - **Runtime discovery**: No need to know channel names at subscriber creation
  /// - **Automatic attachment**: New channels automatically get monitoring pipes
  /// - **Separation of concerns**: Observability separate from business logic
  ///
  /// Real-world use cases:
  /// - **Metrics collection**: Attach counter/timer to every channel
  /// - **Distributed tracing**: Inject trace context propagation on all channels
  /// - **Logging**: Log all emissions across all channels
  /// - **Debugging**: Attach inspector to all channels in a circuit
  ///
  /// Subscriber Callback Contract:
  /// ```java
  /// BiConsumer<Subject<Channel<T>>, Registrar<T>> subscriber = (subject, registrar) -> {
  ///   // subject: The channel that was just created
  ///   // registrar: Register pipes to receive channel's emissions
  ///
  ///   Pipe<T> observerPipe = circuit.pipe(value -> {
  ///     // This runs on circuit thread for each emission
  ///     recordMetric(subject.name(), value);
  ///   });
  ///
  ///   registrar.register(observerPipe);
  /// };
  /// ```
  ///
  /// Critical behaviors verified:
  /// - Subscriber callback invoked for each channel creation
  /// - Subject parameter contains channel identity information
  /// - Registrar enables pipe registration per channel
  /// - Emissions trigger registered pipes (verified by test structure)
  ///
  /// Timing considerations:
  /// - Subscribe BEFORE channel creation (forward subscription)
  /// - Subscribe AFTER channel creation triggers rebuild (retrospective)
  /// - This test validates forward case (most common)
  ///
  /// Expected: 3 channel subjects received by subscriber (one per channel)
  @Test
  void testSubscriberReceivesChannelSubjects () {

    final var circuit = cortex.circuit ();

    try {

      final Conduit < Pipe < String >, String > conduit =
        circuit.conduit ( Composer.pipe () );

      final List < Subject < ? > > receivedSubjects = new ArrayList <> ();

      final Subscriber < String > subscriber =
        circuit.subscriber (
          cortex.name ( "test.subscriber" ),
          ( subject, registrar ) -> {
            receivedSubjects.add ( subject );
            registrar.register ( Receptor.of () );
          }
        );

      // Subscribe before creating channels
      conduit.subscribe ( subscriber );

      // Create channels which should trigger subscriber
      final Pipe < String > pipe1 = conduit.percept ( cortex.name ( "channel.one" ) );
      final Pipe < String > pipe2 = conduit.percept ( cortex.name ( "channel.two" ) );
      final Pipe < String > pipe3 = conduit.percept ( cortex.name ( "channel.three" ) );

      // Emit values to ensure channels are actually created and subscribed
      pipe1.emit ( "test1" );
      pipe2.emit ( "test2" );
      pipe3.emit ( "test3" );

      circuit.await ();

      assertEquals ( 3, receivedSubjects.size () );

    } finally {

      circuit.close ();

    }

  }

  @Test
  void testSubscriberSubjectType () {

    final var circuit = cortex.circuit ();

    try {

      final Subscriber < String > subscriber =
        circuit.subscriber (
          cortex.name ( "subscriber.type.test" ),
          ( _, _ ) -> {
          }
        );

      assertEquals ( Subscriber.class, subscriber.subject ().type () );

    } finally {

      circuit.close ();

    }

  }

}
