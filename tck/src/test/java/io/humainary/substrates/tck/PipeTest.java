// Copyright (c) 2025 William David Louth

package io.humainary.substrates.tck;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.currentThread;
import static org.junit.jupiter.api.Assertions.*;

/// Comprehensive tests for async pipe functionality.
///
/// This test class covers:
/// - Circuit.pipe() creation and basic emission
/// - Deep chain stack safety (prevents overflow)
/// - Cyclic pipe connections (feedback loops)
/// - Ordering guarantees in async dispatch
/// - Integration with conduits and subscribers
/// - Null pointer guards
///
/// The async pipe primitive is essential for building neural-like
/// network topologies with deep hierarchies and recurrent connections.
///
/// @author William David Louth
/// @since 1.0

final class PipeTest
  extends TestSupport {

  private Cortex cortex;

  @BeforeEach
  void setup () {

    cortex = cortex ();

  }

  // ===========================
  // Basic Creation and Emission
  // ===========================

  @Test
  void testAsyncPipeBroadcast () {

    final var circuit = cortex.circuit ();

    try {

      final List < Integer > target1 = new ArrayList <> ();
      final List < Integer > target2 = new ArrayList <> ();
      final List < Integer > target3 = new ArrayList <> ();

      final Pipe < Integer > async =
        circuit.pipe (
          value -> {
            target1.add ( value );
            target2.add ( value );
            target3.add ( value );
          }
        );

      async.emit ( 42 );
      circuit.await ();

      assertEquals ( List.of ( 42 ), target1 );
      assertEquals ( List.of ( 42 ), target2 );
      assertEquals ( List.of ( 42 ), target3 );

    } finally {

      circuit.close ();

    }

  }

  @Test
  void testAsyncPipeChainWithTransformation () {

    final var circuit = cortex.circuit ();

    try {

      final List < Integer > results = new ArrayList <> ();

      // Create async chain: source -> async1 -> async2 -> results
      // Transformation: (value + 1) * 2

      final Pipe < Integer > async2 =
        circuit.pipe (
          value -> results.add ( value * 2 )
        );

      final Pipe < Integer > async1 =
        circuit.pipe (
          value -> async2.emit ( value + 1 )
        );

      async1.emit ( 5 );  // (5 + 1) * 2 = 12
      async1.emit ( 10 ); // (10 + 1) * 2 = 22

      circuit.await ();

      assertEquals ( 2, results.size () );
      assertEquals ( 12, results.get ( 0 ) );
      assertEquals ( 22, results.get ( 1 ) );

    } finally {

      circuit.close ();

    }

  }

  @Test
  void testAsyncPipeCreation () {

    final var circuit = cortex.circuit ();

    try {

      final List < Integer > emissions = new ArrayList <> ();

      final Pipe < Integer > target = circuit.pipe ( emissions::add );
      final Pipe < Integer > async = circuit.pipe ( target );

      assertNotNull ( async );

      async.emit ( 42 );
      circuit.await ();

      assertEquals ( List.of ( 42 ), emissions );

    } finally {

      circuit.close ();

    }

  }

  @Test
  void testAsyncPipeDoesNotBlockCaller () {

    final var circuit = cortex.circuit ();

    try {

      final AtomicInteger counter = new AtomicInteger ( 0 );

      final Pipe < Integer > slowPipe =
        circuit.pipe (
          _ -> {
            try {
              Thread.sleep ( 100 );
            } catch ( final InterruptedException ignored ) {
            }
            counter.incrementAndGet ();
          }
        );

      final long start = currentTimeMillis ();

      // Emit should return immediately without blocking
      slowPipe.emit ( 1 );

      final long elapsed = currentTimeMillis () - start;

      // Should complete in much less than 100ms
      assertTrue ( elapsed < 50, "emit() should not block caller" );

      assertEquals ( 0, counter.get (), "Target not yet executed" );

      circuit.await ();

      assertEquals ( 1, counter.get (), "Target executed after await" );

    } finally {

      circuit.close ();

    }

  }

  // ===========================
  // Deep Chain Stack Safety
  // ===========================

  @Test
  void testAsyncPipeEmitsToTarget () {

    final var circuit = cortex.circuit ();

    try {

      final List < String > emissions = new ArrayList <> ();

      final Pipe < String > target = circuit.pipe ( emissions::add );
      final Pipe < String > async = circuit.pipe ( target );

      async.emit ( "first" );
      async.emit ( "second" );
      async.emit ( "third" );

      circuit.await ();

      assertEquals ( List.of ( "first", "second", "third" ), emissions );

    } finally {

      circuit.close ();

    }

  }

  /// Verifies that async pipes enable arbitrarily deep chains without stack overflow.
  ///
  /// Builds a chain of 1000 async pipes, each forwarding to the next, ending
  /// with a counter. If pipes used recursive invocation (synchronous emit),
  /// this would overflow the call stack. Instead, async pipes enqueue emissions
  /// to the circuit's queue, making deep chains stack-safe.
  ///
  /// This is critical for neural-like network topologies where signals may
  /// propagate through many layers. The queue-based model prevents stack
  /// overflow regardless of chain depth.
  ///
  /// Expected: A single emission at the head reaches the tail through all
  /// 1000 intermediate pipes without any stack overflow.
  @Test
  void testAsyncPipeEnablesDeepChains () {

    final var circuit = cortex.circuit ();

    try {

      final AtomicInteger counter = new AtomicInteger ( 0 );

      // Build a deep chain: pipe0 -> pipe1 -> ... -> pipe999 -> counter
      // Without async, this would risk stack overflow

      Pipe < Integer > tail =
        circuit.pipe ( _ -> counter.incrementAndGet () );

      for ( int i = 0; i < 1000; i++ ) {
        final Pipe < Integer > next = tail;
        tail = circuit.pipe ( next );
      }

      final Pipe < Integer > head = tail;

      head.emit ( 42 );
      circuit.await ();

      assertEquals ( 1, counter.get () );

    } finally {

      circuit.close ();

    }

  }

  // ===========================
  // Cyclic Pipe Connections
  // ===========================

  @Test
  void testAsyncPipeExecutesOnCircuitThread () {

    final var circuit = cortex.circuit ();

    try {

      final List < Thread > threads = new ArrayList <> ();
      final var callerThread = currentThread ();

      final Pipe < Integer > target =
        circuit.pipe ( _ -> threads.add ( currentThread () ) );

      final Pipe < Integer > async = circuit.pipe ( target );

      async.emit ( 1 );
      circuit.await ();

      assertEquals ( 1, threads.size () );
      assertNotSame ( callerThread, threads.getFirst () );

    } finally {

      circuit.close ();

    }

  }

  @SuppressWarnings ( "DataFlowIssue" )
  @Test
  void testAsyncPipeNullTargetGuard () {

    final var circuit = cortex.circuit ();

    try {

      assertThrows (
        NullPointerException.class,
        () -> circuit.pipe ( (Receptor < Integer >) null )
      );

    } finally {

      circuit.close ();

    }

  }

  // ===========================
  // Integration Tests
  // ===========================

  /// Validates that async pipes preserve FIFO ordering across the async boundary.
  ///
  /// Emits 100 sequential values (0..99) to an async pipe from a single thread,
  /// then verifies they arrive at the target in the exact same order. This tests
  /// that the circuit's queueing mechanism maintains order when crossing the
  /// thread boundary from caller to circuit worker.
  ///
  /// Flow:
  /// 1. Caller thread: emit(0), emit(1), ..., emit(99) → ingress queue
  /// 2. Circuit worker: process queue in FIFO order
  /// 3. Target receives: 0, 1, 2, ..., 99 (same order)
  ///
  /// While this test uses a single emitter thread, the ordering guarantee extends
  /// to the ingress queue: emissions enqueued first are processed first, regardless
  /// of which thread enqueued them. For concurrent emitters, the order depends on
  /// which thread's emit() completes first (arrival order at the queue).
  ///
  /// This FIFO guarantee is fundamental to:
  /// - Causal consistency (if A happens-before B in caller, A processed before B)
  /// - Predictable behavior in single-threaded emission scenarios
  /// - Testability (reproducible execution order)
  ///
  /// Note: Different from transit queue (see testCyclicPipeConnection) which has
  /// priority over ingress for cascading emissions.
  ///
  /// Expected: All 100 values arrive in order 0, 1, 2, ..., 99
  @Test
  void testAsyncPipePreservesOrdering () {

    final var circuit = cortex.circuit ();

    try {

      final List < Integer > emissions = new ArrayList <> ();

      final Pipe < Integer > async =
        circuit.pipe ( emissions::add );

      for ( int i = 0; i < 100; i++ ) {
        async.emit ( i );
      }

      circuit.await ();

      assertEquals ( 100, emissions.size () );

      for ( int i = 0; i < 100; i++ ) {
        assertEquals ( i, emissions.get ( i ) );
      }

    } finally {

      circuit.close ();

    }

  }

  @Test
  void testAsyncPipeWithConduit () {

    final var circuit = cortex.circuit ();

    try {

      final var conduit =
        circuit.conduit ( Composer.pipe ( Integer.class ) );

      final List < Integer > emissions = new ArrayList <> ();

      final Pipe < Integer > async =
        circuit.pipe ( emissions::add );

      final Subscriber < Integer > subscriber =
        circuit.subscriber (
          cortex.name ( "pipe.test.subscriber" ),
          ( _, registrar ) -> registrar.register ( async )
        );

      conduit.subscribe ( subscriber );

      final Pipe < Integer > pipe =
        conduit.percept ( cortex.name ( "pipe.test.channel" ) );

      pipe.emit ( 10 );
      pipe.emit ( 20 );
      pipe.emit ( 30 );

      circuit.await ();

      assertEquals ( List.of ( 10, 20, 30 ), emissions );

    } finally {

      circuit.close ();

    }

  }

  @Test
  void testAsyncPipesFromDifferentCircuits () {

    final var circuit1 = cortex.circuit ();
    final var circuit2 = cortex.circuit ();

    try {

      final List < Integer > emissions1 = new ArrayList <> ();
      final List < Integer > emissions2 = new ArrayList <> ();

      final Pipe < Integer > async1 =
        circuit1.pipe ( emissions1::add );

      final Pipe < Integer > async2 =
        circuit2.pipe ( emissions2::add );

      async1.emit ( 1 );
      async2.emit ( 2 );

      circuit1.await ();
      circuit2.await ();

      assertEquals ( List.of ( 1 ), emissions1 );
      assertEquals ( List.of ( 2 ), emissions2 );

    } finally {

      circuit1.close ();
      circuit2.close ();

    }

  }

  // ===========================
  // Null Guards
  // ===========================

  /// Verifies that cyclic pipe connections enable feedback loops without deadlock.
  ///
  /// Creates a pipe that emits back to itself, forming a feedback loop. Each
  /// emission increments the value and re-emits until reaching a threshold (10).
  /// This tests the circuit's ability to handle self-referential connections.
  ///
  /// This is critical for neural-like network topologies that require recurrent
  /// connections and feedback dynamics. The async nature of circuit.pipe() ensures
  /// that feedback emissions are queued rather than recursively invoked, preventing
  /// both stack overflow and deadlock.
  ///
  /// Expected behavior follows the transit queue priority model: cascading emissions
  /// (from the circuit thread itself) are processed before new ingress emissions.
  /// This ensures the feedback loop completes (1→2→3...→10) before any external
  /// emissions would be processed.
  @SuppressWarnings ( "unchecked" )
  @Test
  void testCyclicPipeConnection () {

    final var circuit = cortex.circuit ();

    try {

      final List < Integer > emissions = new ArrayList <> ();
      final int maxCount = 10;

      // Create a cyclic pipe that feeds back to itself
      // but terminates after maxCount iterations

      final Pipe < Integer >[] cycle = new Pipe[1];

      cycle[0] = circuit.pipe (
        value -> {
          emissions.add ( value );
          if ( value < maxCount ) {
            cycle[0].emit ( value + 1 );
          }
        }
      );

      cycle[0].emit ( 1 );
      circuit.await ();

      assertEquals (
        List.of ( 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 ),
        emissions
      );

    } finally {

      circuit.close ();

    }

  }

  // ===========================
  // Multiple Circuits
  // ===========================

  @Test
  void testDeepChainWithTransformations () {

    final var circuit = cortex.circuit ();

    try {

      final List < Integer > result = new ArrayList <> ();

      // Build chain with transformations
      Pipe < Integer > tail = circuit.pipe ( result::add );

      for ( int i = 0; i < 100; i++ ) {
        final Pipe < Integer > next = tail;
        final int increment = i;

        tail = circuit.pipe (
          value -> next.emit ( value + increment )
        );
      }

      final Pipe < Integer > head = tail;

      head.emit ( 0 );
      circuit.await ();

      // Sum of 0..99 = 4950
      assertEquals ( List.of ( 4950 ), result );

    } finally {

      circuit.close ();

    }

  }

  @Test
  void testFlowPipeBasicGuard () {

    final var circuit = cortex.circuit ();

    try {

      final List < Integer > results = new ArrayList <> ();
      final Pipe < Integer > target = circuit.pipe ( results::add );

      final Pipe < Integer > filtered =
        circuit.pipe (
          target,
          flow -> flow.guard ( x -> x > 0 )
        );

      filtered.emit ( -1 );
      filtered.emit ( 0 );
      filtered.emit ( 1 );
      filtered.emit ( 5 );

      circuit.await ();

      assertEquals ( List.of ( 1, 5 ), results );

    } finally {

      circuit.close ();

    }

  }

  // ===========================
  // Transformation Pipes
  // ===========================

  @Test
  void testFlowPipeDiff () {

    final var circuit = cortex.circuit ();

    try {

      final List < Integer > results = new ArrayList <> ();
      final Pipe < Integer > target = circuit.pipe ( results::add );

      final Pipe < Integer > diffed =
        circuit.pipe (
          target,
          Flow::diff
        );

      diffed.emit ( 1 );
      diffed.emit ( 1 );
      diffed.emit ( 2 );
      diffed.emit ( 2 );
      diffed.emit ( 3 );

      circuit.await ();

      assertEquals ( List.of ( 1, 2, 3 ), results );

    } finally {

      circuit.close ();

    }

  }

  @Test
  void testFlowPipeLimit () {

    final var circuit = cortex.circuit ();

    try {

      final List < Integer > results = new ArrayList <> ();
      final Pipe < Integer > target = circuit.pipe ( results::add );

      final Pipe < Integer > limited =
        circuit.pipe (
          target,
          flow -> flow.limit ( 3 )
        );

      for ( int i = 0; i < 10; i++ ) {
        limited.emit ( i );
      }

      circuit.await ();

      assertEquals ( List.of ( 0, 1, 2 ), results );

    } finally {

      circuit.close ();

    }

  }

  @Test
  void testFlowPipeMultipleOperators () {

    final var circuit = cortex.circuit ();

    try {

      final List < Integer > results = new ArrayList <> ();
      final Pipe < Integer > target = circuit.pipe ( results::add );

      final Pipe < Integer > pipeline =
        circuit.pipe (
          target,
          flow ->
            flow
              .guard ( x -> x > 0 )
              .diff ()
              .limit ( 3 )
        );

      pipeline.emit ( -1 );  // filtered by guard
      pipeline.emit ( 1 );   // passes (diff)
      pipeline.emit ( 1 );   // filtered by diff
      pipeline.emit ( 2 );   // passes (diff)
      pipeline.emit ( 3 );   // passes (diff)
      pipeline.emit ( 4 );   // filtered by limit
      pipeline.emit ( 5 );   // filtered by limit

      circuit.await ();

      assertEquals ( List.of ( 1, 2, 3 ), results );

    } finally {

      circuit.close ();

    }

  }

  @SuppressWarnings ( "DataFlowIssue" )
  @Test
  void testFlowPipeNullGuards () {

    final var circuit = cortex.circuit ();

    try {

      final Pipe < Integer > target = circuit.pipe ( Receptor.of () );

      assertThrows (
        NullPointerException.class,
        () -> circuit.pipe ( (Receptor < Integer >) null, Flow::diff )
      );

      assertThrows (
        NullPointerException.class,
        () -> circuit.pipe ( target, null )
      );

    } finally {

      circuit.close ();

    }

  }

  @Test
  void testFlowPipeReduce () {

    final var circuit = cortex.circuit ();

    try {

      final List < Integer > results = new ArrayList <> ();
      final Pipe < Integer > target = circuit.pipe ( results::add );

      final Pipe < Integer > accumulator =
        circuit.pipe (
          target,
          flow -> flow.reduce ( 0, Integer::sum )
        );

      accumulator.emit ( 1 );
      accumulator.emit ( 2 );
      accumulator.emit ( 3 );

      circuit.await ();

      assertEquals ( List.of ( 1, 3, 6 ), results );

    } finally {

      circuit.close ();

    }

  }

  @Test
  void testFlowPipeSample () {

    final var circuit = cortex.circuit ();

    try {

      final List < Integer > results = new ArrayList <> ();
      final Pipe < Integer > target = circuit.pipe ( results::add );

      final Pipe < Integer > sampled =
        circuit.pipe (
          target,
          flow -> flow.sample ( 3 )
        );

      for ( int i = 0; i < 10; i++ ) {
        sampled.emit ( i );
      }

      circuit.await ();

      // Sample every 3rd: indices 2, 5, 8 (0-based, skips first 2)
      assertEquals ( List.of ( 2, 5, 8 ), results );

    } finally {

      circuit.close ();

    }

  }

  // ===========================
  // Pipe Utilities
  // ===========================

  @Test
  void testFlowPipeSift () {

    final var circuit = cortex.circuit ();

    try {

      final List < Integer > results = new ArrayList <> ();
      final Pipe < Integer > target = circuit.pipe ( results::add );

      final Pipe < Integer > filtered =
        circuit.pipe (
          target,
          flow ->
            flow.sift (
              Integer::compareTo,
              sift -> sift.above ( 5 ).below ( 15 )
            )
        );

      for ( int i = 0; i < 20; i++ ) {
        filtered.emit ( i );
      }

      circuit.await ();

      // Values > 5 and < 15: 6, 7, 8, 9, 10, 11, 12, 13, 14
      assertEquals ( List.of ( 6, 7, 8, 9, 10, 11, 12, 13, 14 ), results );

    } finally {

      circuit.close ();

    }

  }

  /// Verifies that multi-node feedback cycles work correctly with proper ordering.
  ///
  /// Creates a 3-pipe cycle: A → B → C → A, where each pipe forwards to the
  /// next in sequence. Pipe C closes the loop by emitting back to A, with an
  /// iteration counter to prevent infinite loops.
  ///
  /// Topology:
  /// ```
  ///   ┌─────┐
  ///   │  A  │──→ B ──→ C
  ///   └──▲──┘           │
  ///      └──────────────┘
  /// ```
  ///
  /// Execution flow demonstrates transit queue priority:
  /// 1. External emit to A (ingress queue)
  /// 2. A emits to B (transit queue - takes priority)
  /// 3. B emits to C (transit queue - cascading)
  /// 4. C emits back to A (transit queue - completes cycle)
  /// 5. Process repeats until iteration limit
  ///
  /// The trace shows execution order: A→B→C→A→B→C→... proving that the entire
  /// cycle completes before any new external emissions would be processed.
  /// This transit queue priority ensures that feedback loops run to completion
  /// atomically from an external observer's perspective.
  ///
  /// Critical for neural networks:
  /// - Enables recurrent connections across multiple nodes
  /// - Ensures signal propagates through entire cycle before new inputs
  /// - Provides deterministic execution in cyclic topologies
  /// - No deadlock despite circular dependencies
  ///
  /// Expected: 5 iterations × 3 nodes = 15 trace entries in strict A→B→C order
  @SuppressWarnings ( "unchecked" )
  @Test
  void testMultiNodeCycle () {

    final var circuit = cortex.circuit ();

    try {

      final List < String > trace = new ArrayList <> ();
      final int maxIterations = 5;
      final AtomicInteger iterations = new AtomicInteger ( 0 );

      // Create A -> B -> C -> A cycle

      final Pipe < String >[] pipes = new Pipe[3];

      pipes[0] = circuit.pipe (
        value -> {
          trace.add ( "A:" + value );
          pipes[1].emit ( value );
        }
      );

      pipes[1] = circuit.pipe (
        value -> {
          trace.add ( "B:" + value );
          pipes[2].emit ( value );
        }
      );

      pipes[2] = circuit.pipe (
        value -> {
          trace.add ( "C:" + value );
          if ( iterations.incrementAndGet () < maxIterations ) {
            pipes[0].emit ( value );
          }
        }
      );

      pipes[0].emit ( "start" );
      circuit.await ();

      assertEquals ( maxIterations * 3, trace.size () );
      assertTrue ( trace.get ( 0 ).startsWith ( "A:" ) );
      assertTrue ( trace.get ( 1 ).startsWith ( "B:" ) );
      assertTrue ( trace.get ( 2 ).startsWith ( "C:" ) );

    } finally {

      circuit.close ();

    }

  }

  // ===========================
  // Flow-Configured Pipes
  // ===========================

  @Test
  void testPipesOfConsumer () {

    final var circuit = cortex.circuit ();

    try {

      final List < Integer > results = new ArrayList <> ();

      // Using method reference
      final Pipe < Integer > pipe =
        circuit.pipe ( results::add );

      pipe.emit ( 1 );
      pipe.emit ( 2 );
      pipe.emit ( 3 );

      circuit.await ();

      assertEquals ( List.of ( 1, 2, 3 ), results );

    } finally {

      circuit.close ();

    }

  }

  @Test
  void testPipesOfConsumerWithCounter () {

    final var circuit = cortex.circuit ();

    try {

      final AtomicInteger counter = new AtomicInteger ( 0 );

      // Explicit lambda shows intent: ignoring value, counting emissions
      final Pipe < String > pipe =
        circuit.pipe (
          _ -> counter.incrementAndGet ()
        );

      pipe.emit ( "a" );
      pipe.emit ( "b" );
      pipe.emit ( "c" );

      circuit.await ();

      assertEquals ( 3, counter.get () );

    } finally {

      circuit.close ();

    }

  }

  // ===========================
  // Pipe Subject Tests
  // ===========================

  /// Verifies that a circuit-created pipe has a subject with the circuit as parent.
  @Test
  void testCircuitPipeSubjectHasCircuitAsParent () {

    final var circuit = cortex.circuit ( cortex.name ( "test.circuit" ) );

    try {

      final Pipe < Integer > pipe =
        circuit.pipe ( Receptor.of ( Integer.class ) );

      final var subject = pipe.subject ();

      assertNotNull ( subject );
      assertNotNull ( subject.id () );

      // Subject's enclosure should be the circuit's subject
      assertTrue ( subject.enclosure ().isPresent () );

      subject.enclosure (
        parent -> assertEquals (
          circuit.subject ().id (),
          parent.id ()
        )
      );

    } finally {

      circuit.close ();

    }

  }

  /// Verifies that a circuit-created pipe inherits the circuit's name by default.
  @Test
  void testCircuitPipeSubjectInheritsCircuitName () {

    final var circuitName = cortex.name ( "my.circuit" );
    final var circuit = cortex.circuit ( circuitName );

    try {

      final Pipe < Integer > pipe =
        circuit.pipe ( Receptor.of ( Integer.class ) );

      assertEquals (
        circuitName.toString (),
        pipe.subject ().name ().toString ()
      );

    } finally {

      circuit.close ();

    }

  }

  /// Verifies that pipes created with explicit names use that name.
  @Test
  void testCircuitPipeWithCustomName () {

    final var circuit = cortex.circuit ( cortex.name ( "test.circuit" ) );

    try {

      final var customName = cortex.name ( "custom.pipe.name" );

      final Pipe < Integer > pipe =
        circuit.pipe (
          customName,
          Receptor.of ( Integer.class )
        );

      assertEquals (
        customName.toString (),
        pipe.subject ().name ().toString ()
      );

    } finally {

      circuit.close ();

    }

  }

  /// Verifies that multiple pipes from same circuit have distinct subjects.
  @Test
  void testMultiplePipesHaveDistinctSubjects () {

    final var circuit = cortex.circuit ();

    try {

      final Pipe < Integer > pipe1 =
        circuit.pipe ( Receptor.of ( Integer.class ) );

      final Pipe < Integer > pipe2 =
        circuit.pipe ( Receptor.of ( Integer.class ) );

      // Different subject instances
      assertNotSame ( pipe1.subject (), pipe2.subject () );

      // Different IDs
      assertNotEquals (
        pipe1.subject ().id (),
        pipe2.subject ().id ()
      );

    } finally {

      circuit.close ();

    }

  }

  /// Verifies that a conduit-created pipe (via channel) has the channel as parent.
  @Test
  void testConduitPipeSubjectHasChannelAsParent () {

    final var circuit = cortex.circuit ();

    try {

      final var channelName = cortex.name ( "test.channel" );

      final var conduit =
        circuit.conduit ( Composer.pipe ( Integer.class ) );

      final Pipe < Integer > pipe =
        conduit.percept ( channelName );

      final var subject = pipe.subject ();

      assertNotNull ( subject );

      // Subject's enclosure should be the channel's subject
      assertTrue ( subject.enclosure ().isPresent () );

      // The pipe's parent should have the channel name
      subject.enclosure (
        parent -> assertEquals (
          channelName.toString (),
          parent.name ().toString ()
        )
      );

    } finally {

      circuit.close ();

    }

  }

  /// Verifies that a conduit-created pipe inherits the channel's name.
  @Test
  void testConduitPipeSubjectInheritsChannelName () {

    final var circuit = cortex.circuit ();

    try {

      final var channelName = cortex.name ( "orders" );

      final var conduit =
        circuit.conduit ( Composer.pipe ( Integer.class ) );

      final Pipe < Integer > pipe =
        conduit.percept ( channelName );

      assertEquals (
        channelName.toString (),
        pipe.subject ().name ().toString ()
      );

    } finally {

      circuit.close ();

    }

  }

  /// Verifies that pipes with flow configuration still have proper subjects.
  @Test
  void testFlowPipeHasSubject () {

    final var circuit = cortex.circuit ( cortex.name ( "flow.circuit" ) );

    try {

      final Pipe < Integer > pipe =
        circuit.pipe (
          Receptor.of ( Integer.class ),
          flow -> flow.guard ( x -> x > 0 ).diff ()
        );

      final var subject = pipe.subject ();

      assertNotNull ( subject );
      assertNotNull ( subject.id () );

      // Parent is the circuit
      assertTrue ( subject.enclosure ().isPresent () );

    } finally {

      circuit.close ();

    }

  }

  /// Verifies that flow pipes with custom names use that name.
  @Test
  void testFlowPipeWithCustomName () {

    final var circuit = cortex.circuit ();

    try {

      final var customName = cortex.name ( "filtered.pipe" );

      final Pipe < Integer > pipe =
        circuit.pipe (
          customName,
          Receptor.of ( Integer.class ),
          flow -> flow.guard ( x -> x > 0 )
        );

      assertEquals (
        customName.toString (),
        pipe.subject ().name ().toString ()
      );

    } finally {

      circuit.close ();

    }

  }

  /// Verifies the subject hierarchy for a conduit pipe: circuit → conduit → channel → pipe
  @Test
  void testConduitPipeSubjectHierarchy () {

    final var circuitName = cortex.name ( "root.circuit" );
    final var conduitName = cortex.name ( "events.conduit" );
    final var channelName = cortex.name ( "orders.channel" );

    final var circuit = cortex.circuit ( circuitName );

    try {

      final var conduit =
        circuit.conduit (
          conduitName,
          Composer.pipe ( Integer.class )
        );

      final Pipe < Integer > pipe =
        conduit.percept ( channelName );

      final var pipeSubject = pipe.subject ();

      // Pipe name is channel name
      assertEquals ( channelName.toString (), pipeSubject.name ().toString () );

      // Walk up the hierarchy
      pipeSubject.enclosure ( channelSubject -> {
        assertEquals ( channelName.toString (), channelSubject.name ().toString () );

        channelSubject.enclosure ( conduitSubject -> {
          assertEquals ( conduitName.toString (), conduitSubject.name ().toString () );

          conduitSubject.enclosure ( circuitSubject ->
            assertEquals ( circuitName.toString (), circuitSubject.name ().toString () )
          );
        } );
      } );

    } finally {

      circuit.close ();

    }

  }

  // ===========================
  // Cross-Circuit Threading Tests
  // ===========================

  /// Verifies that when pipes from different circuits are chained,
  /// each pipe's receptor executes on its own circuit's thread.
  ///
  /// Creates two circuits with their own pipes. Circuit1's pipe
  /// forwards emissions to circuit2's pipe. This tests that the
  /// async boundary is correctly maintained: circuit1's receptor
  /// runs on circuit1's thread, and circuit2's receptor runs on
  /// circuit2's thread.
  ///
  /// Flow:
  /// 1. Caller thread: pipe1.emit(value) → submits to circuit1's valve
  /// 2. Circuit1's worker: executes pipe1's receptor (calls pipe2.emit)
  /// 3. Circuit1's worker: pipe2.emit(value) → submits to circuit2's valve
  /// 4. Circuit2's worker: executes pipe2's receptor (captures thread)
  ///
  /// This is critical for neural-like networks where circuits represent
  /// independent processing nodes with isolated execution contexts.
  @Test
  void testCrossCircuitPipeChainExecutesOnRespectiveThreads () {

    final var circuit1 = cortex.circuit ();
    final var circuit2 = cortex.circuit ();

    try {

      final var callerThread = currentThread ();
      final List < Thread > circuit1Threads = new ArrayList <> ();
      final List < Thread > circuit2Threads = new ArrayList <> ();

      // Circuit2's pipe captures its executing thread
      final Pipe < Integer > pipe2 =
        circuit2.pipe ( _ -> circuit2Threads.add ( currentThread () ) );

      // Circuit1's pipe forwards to pipe2, capturing its own thread
      final Pipe < Integer > pipe1 =
        circuit1.pipe ( value -> {
          circuit1Threads.add ( currentThread () );
          pipe2.emit ( value );
        } );

      // Emit from caller thread
      pipe1.emit ( 1 );

      // Wait for both circuits to complete
      circuit1.await ();
      circuit2.await ();

      // Verify circuit1's receptor ran on circuit1's thread (not caller)
      assertEquals ( 1, circuit1Threads.size () );
      assertNotSame ( callerThread, circuit1Threads.getFirst () );

      // Verify circuit2's receptor ran on circuit2's thread
      assertEquals ( 1, circuit2Threads.size () );
      assertNotSame ( callerThread, circuit2Threads.getFirst () );

      // Verify the two circuits use DIFFERENT threads
      assertNotSame ( circuit1Threads.getFirst (), circuit2Threads.getFirst () );

    } finally {

      circuit1.close ();
      circuit2.close ();

    }

  }

  /// Verifies that multiple emissions through cross-circuit pipe chains
  /// maintain correct thread affinity for each circuit.
  ///
  /// Extends the basic cross-circuit test to verify that thread affinity
  /// is consistent across multiple emissions, not just a single one.
  @Test
  void testCrossCircuitPipeChainThreadAffinityAcrossMultipleEmissions () {

    final var circuit1 = cortex.circuit ();
    final var circuit2 = cortex.circuit ();

    try {

      final List < Thread > circuit1Threads = new ArrayList <> ();
      final List < Thread > circuit2Threads = new ArrayList <> ();
      final int emissionCount = 10;

      // Circuit2's pipe captures its executing thread
      final Pipe < Integer > pipe2 =
        circuit2.pipe ( _ -> circuit2Threads.add ( currentThread () ) );

      // Circuit1's pipe forwards to pipe2, capturing its own thread
      final Pipe < Integer > pipe1 =
        circuit1.pipe ( value -> {
          circuit1Threads.add ( currentThread () );
          pipe2.emit ( value );
        } );

      // Emit multiple values
      for ( int i = 0; i < emissionCount; i++ ) {
        pipe1.emit ( i );
      }

      // Wait for both circuits to complete
      circuit1.await ();
      circuit2.await ();

      // Verify all emissions processed by both circuits
      assertEquals ( emissionCount, circuit1Threads.size () );
      assertEquals ( emissionCount, circuit2Threads.size () );

      // Verify each circuit uses a single consistent thread
      final Thread circuit1Thread = circuit1Threads.getFirst ();
      final Thread circuit2Thread = circuit2Threads.getFirst ();

      for ( final Thread t : circuit1Threads ) {
        assertSame ( circuit1Thread, t );
      }

      for ( final Thread t : circuit2Threads ) {
        assertSame ( circuit2Thread, t );
      }

      // Verify the two circuits use DIFFERENT threads
      assertNotSame ( circuit1Thread, circuit2Thread );

    } finally {

      circuit1.close ();
      circuit2.close ();

    }

  }

  /// Verifies that a three-circuit chain maintains correct thread
  /// affinity at each hop.
  ///
  /// Creates a chain: circuit1 → circuit2 → circuit3
  /// Each circuit should execute its receptor on its own thread.
  @Test
  void testThreeCircuitPipeChainThreadAffinity () {

    final var circuit1 = cortex.circuit ();
    final var circuit2 = cortex.circuit ();
    final var circuit3 = cortex.circuit ();

    try {

      final List < Thread > threads1 = new ArrayList <> ();
      final List < Thread > threads2 = new ArrayList <> ();
      final List < Thread > threads3 = new ArrayList <> ();

      // Circuit3's pipe (final destination)
      final Pipe < Integer > pipe3 =
        circuit3.pipe ( _ -> threads3.add ( currentThread () ) );

      // Circuit2's pipe (intermediate)
      final Pipe < Integer > pipe2 =
        circuit2.pipe ( value -> {
          threads2.add ( currentThread () );
          pipe3.emit ( value );
        } );

      // Circuit1's pipe (entry point)
      final Pipe < Integer > pipe1 =
        circuit1.pipe ( value -> {
          threads1.add ( currentThread () );
          pipe2.emit ( value );
        } );

      pipe1.emit ( 1 );

      circuit1.await ();
      circuit2.await ();
      circuit3.await ();

      // All three circuits should have processed exactly one emission
      assertEquals ( 1, threads1.size () );
      assertEquals ( 1, threads2.size () );
      assertEquals ( 1, threads3.size () );

      // All three should use different threads
      final Thread t1 = threads1.getFirst ();
      final Thread t2 = threads2.getFirst ();
      final Thread t3 = threads3.getFirst ();

      assertNotSame ( t1, t2 );
      assertNotSame ( t2, t3 );
      assertNotSame ( t1, t3 );

    } finally {

      circuit1.close ();
      circuit2.close ();
      circuit3.close ();

    }

  }

}
