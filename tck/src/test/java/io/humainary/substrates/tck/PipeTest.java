// Copyright (c) 2025 William David Louth

package io.humainary.substrates.tck;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

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
          cortex.pipe ( value -> {
            target1.add ( value );
            target2.add ( value );
            target3.add ( value );
          } )
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
          cortex.pipe ( value -> results.add ( value * 2 ) )
        );

      final Pipe < Integer > async1 =
        circuit.pipe (
          cortex.pipe ( value -> async2.emit ( value + 1 ) )
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

      final Pipe < Integer > target = cortex.pipe ( emissions::add );
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
          cortex.pipe (
            _ -> {
              try {
                Thread.sleep ( 100 );
              } catch ( final InterruptedException ignored ) {
              }
              counter.incrementAndGet ();
            }
          )
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

      final Pipe < String > target = cortex.pipe ( emissions::add );
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
        cortex.pipe ( _ -> counter.incrementAndGet () );

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
        cortex.pipe ( _ -> threads.add ( currentThread () ) );

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
        () -> circuit.pipe ( null )
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
        circuit.pipe ( cortex.pipe ( emissions::add ) );

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
        circuit.pipe ( cortex.pipe ( emissions::add ) );

      final Subscriber < Integer > subscriber =
        cortex.subscriber (
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
        circuit1.pipe ( cortex.pipe ( emissions1::add ) );

      final Pipe < Integer > async2 =
        circuit2.pipe ( cortex.pipe ( emissions2::add ) );

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
        cortex.pipe (
          value -> {
            emissions.add ( value );
            if ( value < maxCount ) {
              cycle[0].emit ( value + 1 );
            }
          }
        )
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
      Pipe < Integer > tail = cortex.pipe ( result::add );

      for ( int i = 0; i < 100; i++ ) {
        final Pipe < Integer > next = tail;
        final int increment = i;

        tail = circuit.pipe (
          cortex.pipe ( value -> next.emit ( value + increment ) )
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
      final Pipe < Integer > target = cortex.pipe ( results::add );

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
      final Pipe < Integer > target = cortex.pipe ( results::add );

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
      final Pipe < Integer > target = cortex.pipe ( results::add );

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
      final Pipe < Integer > target = cortex.pipe ( results::add );

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

      final Pipe < Integer > target = cortex.pipe ( Integer.class );

      assertThrows (
        NullPointerException.class,
        () -> circuit.pipe ( null, Flow::diff )
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
      final Pipe < Integer > target = cortex.pipe ( results::add );

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
      final Pipe < Integer > target = cortex.pipe ( results::add );

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
      final Pipe < Integer > target = cortex.pipe ( results::add );

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
        cortex.pipe (
          value -> {
            trace.add ( "A:" + value );
            pipes[1].emit ( value );
          }
        )
      );

      pipes[1] = circuit.pipe (
        cortex.pipe (
          value -> {
            trace.add ( "B:" + value );
            pipes[2].emit ( value );
          }
        )
      );

      pipes[2] = circuit.pipe (
        cortex.pipe (
          value -> {
            trace.add ( "C:" + value );
            if ( iterations.incrementAndGet () < maxIterations ) {
              pipes[0].emit ( value );
            }
          }
        )
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

      // Using cortex.pipe instead of lambda
      final Pipe < Integer > pipe =
        circuit.pipe ( cortex.pipe ( results::add ) );

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
          cortex.pipe ( _ -> counter.incrementAndGet () )
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

  @Test
  void testTransformPipeBasic () {

    final var circuit = cortex.circuit ();

    try {

      final List < Integer > results = new ArrayList <> ();

      // Compose transformation before async dispatch
      final Function < Integer, Integer > doubler = x -> x * 2;
      final Pipe < Integer > target = cortex.pipe ( results::add );

      final var transform =
        circuit.pipe ( cortex.pipe ( doubler, target ) );

      transform.emit ( 5 );
      transform.emit ( 10 );
      transform.emit ( 15 );

      circuit.await ();

      assertEquals ( List.of ( 10, 20, 30 ), results );

    } finally {

      circuit.close ();

    }

  }

  @Test
  void testTransformPipeNeuralWeightedConnection () {

    final var circuit = cortex.circuit ();

    try {

      final List < Double > activations = new ArrayList <> ();
      final double weight = 0.5;

      final Function < Double, Double > synapse = signal -> signal * weight;
      final Pipe < Double > target = cortex.pipe ( activations::add );

      final Pipe < Double > pipe =
        circuit.pipe ( cortex.pipe ( synapse, target ) );

      pipe.emit ( 1.0 );
      pipe.emit ( 2.0 );
      pipe.emit ( 4.0 );

      circuit.await ();

      assertEquals ( 3, activations.size () );
      assertEquals ( 0.5, activations.get ( 0 ), 0.001 );
      assertEquals ( 1.0, activations.get ( 1 ), 0.001 );
      assertEquals ( 2.0, activations.get ( 2 ), 0.001 );

    } finally {

      circuit.close ();

    }

  }

  @SuppressWarnings ( "DataFlowIssue" )
  @Test
  void testTransformPipeNullGuards () {

    final var circuit = cortex.circuit ();

    try {

      final Pipe < Integer > target = cortex.pipe ( Integer.class );

      assertThrows (
        NullPointerException.class,
        () -> cortex.pipe ( null, target )
      );

      assertThrows (
        NullPointerException.class,
        () -> circuit.pipe ( null )
      );

    } finally {

      circuit.close ();

    }

  }

  @Test
  void testTransformPipeOrdering () {

    final var circuit = cortex.circuit ();

    try {

      final List < Integer > results = new ArrayList <> ();

      final Function < Integer, Integer > times10 = x -> x * 10;
      final Pipe < Integer > target = cortex.pipe ( results::add );

      final Pipe < Integer > transform =
        circuit.pipe ( cortex.pipe ( times10, target ) );

      for ( int i = 0; i < 50; i++ ) {
        transform.emit ( i );
      }

      circuit.await ();

      assertEquals ( 50, results.size () );

      for ( int i = 0; i < 50; i++ ) {
        assertEquals ( i * 10, results.get ( i ) );
      }

    } finally {

      circuit.close ();

    }

  }

  @Test
  void testTransformPipeStatefulTransformation () {

    final var circuit = cortex.circuit ();

    try {

      final List < Integer > results = new ArrayList <> ();
      final AtomicInteger counter = new AtomicInteger ( 0 );

      final Function < Integer, Integer > addCounter =
        x -> x + counter.incrementAndGet ();
      final Pipe < Integer > target = cortex.pipe ( results::add );

      final Pipe < Integer > transform =
        circuit.pipe ( cortex.pipe ( addCounter, target ) );

      transform.emit ( 10 );
      transform.emit ( 20 );
      transform.emit ( 30 );

      circuit.await ();

      assertEquals ( List.of ( 11, 22, 33 ), results );

    } finally {

      circuit.close ();

    }

  }

  @Test
  void testTransformPipeWithMethodReference () {

    final var circuit = cortex.circuit ();

    try {

      final List < Double > results = new ArrayList <> ();

      final Function < Double, Double > activation = Math::tanh;
      final Pipe < Double > target = cortex.pipe ( results::add );

      final Pipe < Double > neuron =
        circuit.pipe ( cortex.pipe ( activation, target ) );

      neuron.emit ( 0.0 );
      neuron.emit ( 1.0 );
      neuron.emit ( -1.0 );

      circuit.await ();

      assertEquals ( 3, results.size () );
      assertEquals ( 0.0, results.get ( 0 ), 0.001 );
      assertEquals ( Math.tanh ( 1.0 ), results.get ( 1 ), 0.001 );
      assertEquals ( Math.tanh ( -1.0 ), results.get ( 2 ), 0.001 );

    } finally {

      circuit.close ();

    }

  }

}
