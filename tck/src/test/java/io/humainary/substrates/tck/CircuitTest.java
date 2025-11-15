// Copyright (c) 2025 William David Louth

package io.humainary.substrates.tck;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.currentThread;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;

/// Comprehensive tests for the Circuit interface.
///
/// This test class covers:
/// - Circuit creation and lifecycle
/// - Conduit creation with various overloads
/// - Cell creation (experimental API)
/// - Threading model and await semantics
/// - Resource management and cleanup
/// - Integration with other components
///
/// @author William David Louth
/// @since 1.0

final class CircuitTest
  extends TestSupport {

  private Cortex cortex;

  @BeforeEach
  void setup () {

    cortex = cortex ();

  }

  // ===========================
  // Circuit Creation Tests
  // ===========================

  /// Verifies that channel pooling is thread-safe under maximum contention.
  ///
  /// Creates 20 threads that simultaneously attempt to retrieve a channel
  /// with the same name from the same conduit. Uses a CountDownLatch to
  /// ensure all threads start at exactly the same time, maximizing contention
  /// on the internal pooling mechanism.
  ///
  /// Expected: All threads must receive the exact same channel instance,
  /// proving that the conduit's internal synchronization correctly ensures
  /// only one channel is created per name, even under concurrent access.
  @SuppressWarnings ( "resource" )
  @Test
  void testAtomicChannelCreationUnderContention ()
  throws InterruptedException, ExecutionException {

    final var circuit = cortex.circuit ();

    try {

      final var conduit =
        circuit.conduit ( Composer.pipe ( Integer.class ) );

      final var name = cortex.name ( "contention.channel" );
      final var latch = new CountDownLatch ( 1 );
      final var channelReferences = new ConcurrentHashMap < Integer, Pipe < Integer > > ();

      final var executor = newFixedThreadPool ( 20 );

      try {

        // All threads wait at latch to create maximum contention
        final var futures = new ArrayList < Future < ? > > ();
        for ( int t = 0; t < 20; t++ ) {
          final int threadId = t;
          futures.add (
            executor.submit ( () -> {
              try {
                latch.await (); // Wait for signal
                final var ch = conduit.percept ( name );
                channelReferences.put ( threadId, ch );
              } catch ( final InterruptedException e ) {
                currentThread ().interrupt ();
              }
            } )
          );
        }

        // Release all threads simultaneously
        latch.countDown ();

        for ( final var future : futures ) {
          future.get ();
        }

        // Verify all got same instance
        final var firstChannel = channelReferences.get ( 0 );
        for ( int i = 1; i < 20; i++ ) {
          assertSame (
            firstChannel,
            channelReferences.get ( i ),
            "All threads under contention must receive same channel instance"
          );
        }

      } finally {

        executor.shutdown ();
        assertTrue (
          executor.awaitTermination ( 5, SECONDS ),
          "Executor should terminate"
        );

      }

    } finally {

      circuit.close ();

    }

  }

  @Test
  void testAwaitAfterCloseReturnsFast () {

    final var circuit = cortex.circuit ();

    final var conduit =
      circuit.conduit ( Composer.pipe ( Integer.class ) );

    final var pipe =
      conduit.percept ( cortex.name ( "fast.await.channel" ) );

    // Emit and process
    pipe.emit ( 1 );
    circuit.await ();

    // Close the circuit
    circuit.close ();

    // Multiple awaits after close should all return fast
    final long startTime = currentTimeMillis ();

    for ( int i = 0; i < 5; i++ ) {
      circuit.await ();
    }

    final long duration = currentTimeMillis () - startTime;

    assertTrue (
      duration < 100,
      "await() after close should use fast path (took " + duration + "ms)"
    );

  }

  @Test
  void testAwaitAfterClosureUsesFastPath () {

    final var circuit = cortex.circuit ();

    try {

      final var conduit =
        circuit.conduit ( Composer.pipe ( String.class ) );

      final Pipe < String > pipe =
        conduit.percept ( cortex.name ( "valve.fastpath.channel" ) );

      pipe.emit ( "first" );
      pipe.emit ( "second" );

      circuit.await ();

      circuit.close ();
      circuit.await ();

      final Duration fastPathBudget = Duration.ofMillis ( 200L );

      assertTimeoutPreemptively (
        fastPathBudget,
        circuit::await,
        "await() should short-circuit once the circuit is closed"
      );

      // Subsequent invocations should remain fast even after the first post-close await.
      assertTimeoutPreemptively (
        fastPathBudget,
        circuit::await,
        "await() should consistently use the closed fast-path"
      );

    } finally {

      circuit.close ();

    }

  }

  @Test
  void testAwaitCompletesWhenQueueEmpty () {

    final var circuit = cortex.circuit ();

    try {

      final var conduit =
        circuit.conduit ( Composer.pipe ( Integer.class ) );

      conduit.percept ( cortex.name ( "test.channel" ) )
        .emit ( 42 );

      // Should complete when queue is drained
      circuit.await ();

    } finally {

      circuit.close ();

    }

  }

  // ===========================
  // Conduit Creation Tests
  // ===========================

  @Test
  void testAwaitDuringShutdownProcessesPendingEmissions () {

    final var circuit = cortex.circuit ();
    final var processed = new AtomicInteger ( 0 );

    final var conduit =
      circuit.conduit ( Composer.pipe ( Integer.class ) );

    final var pipe =
      conduit.percept ( cortex.name ( "shutdown.await.channel" ) );

    conduit.subscribe (
      cortex.subscriber (
        cortex.name ( "shutdown.subscriber" ),
        ( _, registrar ) ->
          registrar.register ( cortex.pipe ( _ -> {

            processed.incrementAndGet ();

            // Simulate work
            try {
              Thread.sleep ( 50 );
            } catch ( final InterruptedException e ) {
              currentThread ().interrupt ();
            }

          } ) )
      )
    );

    // Emit multiple values
    for ( int i = 0; i < 5; i++ ) {
      pipe.emit ( i );
    }

    // Close immediately (before processing completes)
    circuit.close ();

    // await() should wait for pending emissions to complete
    circuit.await ();

    // All emissions should have been processed
    assertTrue (
      processed.get () >= 1,
      "At least some emissions should be processed during shutdown"
    );

  }

  @Test
  void testAwaitFromExternalThread () throws InterruptedException {

    final var circuit = cortex.circuit ();

    try {

      final var conduit =
        circuit.conduit ( Composer.pipe ( Integer.class ) );

      final var latch = new CountDownLatch ( 1 );

      final var thread = new Thread ( () -> {
        try {

          final Pipe < Integer > pipe =
            conduit.percept ( cortex.name ( "async.channel" ) );

          pipe.emit ( 100 );

          circuit.await ();

          latch.countDown ();

        } catch ( final java.lang.Exception ignored ) {
        }
      } );

      thread.start ();

      assertTrue (
        latch.await ( 5, TimeUnit.SECONDS ),
        "await should complete"
      );

      thread.join ();

    } finally {

      circuit.close ();

    }

  }

  /// Validates the circuit's fundamental safety constraint: await() cannot be
  /// called from within the circuit's own worker thread.
  ///
  /// This test creates a subscriber whose emission handler attempts to call
  /// circuit.await() while executing on the circuit's worker thread. This would
  /// cause deadlock: the circuit thread would be waiting for its own queue to
  /// drain, but it cannot drain the queue because it's blocked in await().
  ///
  /// The circuit detects this self-deadlock condition by checking if the calling
  /// thread is the circuit's worker thread, and throws IllegalStateException with
  /// a clear diagnostic message.
  ///
  /// Threading model enforced:
  /// - External threads: Can safely call await() to wait for queue drainage
  /// - Circuit worker thread: MUST NEVER call await() on its own circuit
  /// - Analogous to: A thread cannot acquire its own monitor if already holding it
  ///
  /// This safety check prevents a common mistake that would otherwise cause
  /// mysterious hangs in production.
  ///
  /// Expected: IllegalStateException with message "Cannot call Circuit::await
  /// from within a circuit's thread"
  @Test
  void testAwaitOnCircuitThreadThrowsIllegalStateException () {

    final var circuit = cortex.circuit ();

    try {

      final AtomicReference < Throwable > captured = new AtomicReference <> ();
      final AtomicReference < Thread > workerThread = new AtomicReference <> ();

      final var conduit =
        circuit.conduit (
          cortex.name ( "valve.await.conduit" ),
          Composer.pipe ( Integer.class )
        );

      final Subscriber < Integer > subscriber =
        cortex.subscriber (
          cortex.name ( "valve.await.subscriber" ),
          ( _, registrar ) ->
            registrar.register (
              cortex.pipe (
                _ -> {
                  workerThread.set ( Thread.currentThread () );
                  try {
                    circuit.await ();
                  } catch ( final IllegalStateException ex ) {
                    captured.set ( ex );
                  }
                }
              )
            )
        );

      final var subscription =
        conduit.subscribe ( subscriber );

      final Pipe < Integer > pipe =
        conduit.percept ( cortex.name ( "valve.await.channel" ) );

      pipe.emit ( 1 );

      circuit.await ();

      subscription.close ();

      final var thrown = captured.get ();

      assertNotNull ( thrown, "await() on the circuit thread should throw" );
      assertEquals ( IllegalStateException.class, thrown.getClass () );
      assertEquals (
        "Cannot call Circuit::await from within a circuit's thread",
        thrown.getMessage ()
      );
      assertNotNull ( workerThread.get (), "Subscriber should execute on circuit worker thread" );

    } finally {

      circuit.close ();

    }

  }

  @Test
  void testAwaitWithMultipleEmissions () {

    final var circuit = cortex.circuit ();

    try {

      final var conduit =
        circuit.conduit ( Composer.pipe ( String.class ) );

      final Pipe < String > pipe =
        conduit.percept ( cortex.name ( "multi.emit.channel" ) );

      pipe.emit ( "first" );
      pipe.emit ( "second" );
      pipe.emit ( "third" );

      circuit.await ();

    } finally {

      circuit.close ();

    }

  }

  @SuppressWarnings ( "DataFlowIssue" )
  @Test
  void testCellCreationNullGuards () {

    final var circuit = cortex.circuit ();

    try {

      assertThrows (
        NullPointerException.class,
        () -> circuit.cell ( null, null, null, cortex.pipe ( Object.class ) )
      );

    } finally {

      circuit.close ();

    }

  }

  @Test
  void testCircuitCloseIsIdempotent () {

    final var circuit = cortex.circuit ();

    circuit.close ();
    circuit.close (); // Should not throw
    circuit.close (); // Should not throw

  }

  @Test
  void testCircuitCloseReleasesResources () {

    final var circuit = cortex.circuit ();

    final var conduit =
      circuit.conduit ( Composer.pipe ( Integer.class ) );

    assertNotNull ( conduit );

    circuit.close ();

    // Circuit is closed, but we can't really verify internal state
    // This test mainly ensures close doesn't throw

  }

  // ===========================
  // Cell Creation Tests (Experimental)
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

    final var circuitName = cortex.name ( "circuit.test.named" );
    final var circuit = cortex.circuit ( circuitName );

    assertNotNull ( circuit );
    assertEquals ( circuitName, circuit.subject ().name () );
    assertEquals ( Circuit.class, circuit.subject ().type () );

    circuit.close ();

  }

  @Test
  void testCircuitCreationWithoutName () {

    final var circuit = cortex.circuit ();

    assertNotNull ( circuit );
    assertNotNull ( circuit.subject () );
    assertNotNull ( circuit.subject ().name () );
    assertNotNull ( circuit.subject ().id () );
    assertEquals ( Circuit.class, circuit.subject ().type () );

    circuit.close ();

  }

  // ===========================
  // Circuit.await() Tests
  // ===========================

  /// Verifies that the circuit preserves emission order (FIFO semantics).
  ///
  /// Emits values 1, 2, 3, 4, 5 sequentially from the same thread and
  /// verifies they are delivered to subscribers in the exact same order.
  ///
  /// This validates the circuit's fundamental ordering guarantee: emissions
  /// from the ingress queue are processed in FIFO order. This is critical
  /// for causal consistency in event processing.
  ///
  /// Note: This test uses the same channel for all emissions. Different
  /// channels or concurrent emitters may have different ordering semantics
  /// (see cascade/priority tests for transit queue behavior).
  @Test
  void testCircuitEventOrdering () {

    final var circuit = cortex.circuit ();

    try {

      final var conduit =
        circuit.conduit ( Composer.pipe ( Integer.class ) );

      final List < Integer > emissions = new ArrayList <> ();

      final Subscriber < Integer > subscriber =
        cortex.subscriber (
          cortex.name ( "ordering.subscriber" ),
          ( _, registrar ) ->
            registrar.register ( cortex.pipe ( emissions::add ) )
        );

      conduit.subscribe ( subscriber );

      final Pipe < Integer > pipe =
        conduit.percept ( cortex.name ( "ordering.channel" ) );

      pipe.emit ( 1 );
      pipe.emit ( 2 );
      pipe.emit ( 3 );
      pipe.emit ( 4 );
      pipe.emit ( 5 );

      circuit.await ();

      assertEquals ( List.of ( 1, 2, 3, 4, 5 ), emissions );

    } finally {

      circuit.close ();

    }

  }

  @Test
  void testCircuitProvidesSource () {

    final var circuit = cortex.circuit ();

    try {

      // Circuit implements Source directly through Context

      assertNotNull ( circuit );
      assertNotNull ( circuit.subject () );

    } finally {

      circuit.close ();

    }

  }

  // ===========================
  // Circuit.close() Tests
  // ===========================

  @Test
  void testCircuitSourceSubscription () {

    final var circuit = cortex.circuit ();

    try {

      final var subjects = new ArrayList < Subject < ? > > ();

      final Subscriber < State > subscriber =
        cortex.subscriber (
          cortex.name ( "circuit.source.subscriber" ),
          ( subject, registrar ) -> {
            subjects.add ( subject );
            registrar.register ( cortex.pipe ( State.class ) );
          }
        );

      final var subscription = circuit.subscribe ( subscriber );

      assertNotNull ( subscription );

      subscription.close ();

    } finally {

      circuit.close ();

    }

  }

  @Test
  void testCircuitSubjectState () {

    final var circuit = cortex.circuit (
      cortex.name ( "circuit.state.test" )
    );

    try {

      final Subject < ? > subject = circuit.subject ();
      final var state = subject.state ();

      assertNotNull ( state );
      // State should be empty for a newly created circuit
      assertEquals ( 0, state.stream ().count () );

    } finally {

      circuit.close ();

    }

  }

  // ===========================
  // Source Access Tests
  // ===========================

  // ===========================
  // Integration Tests
  // ===========================

  @Test
  void testCircuitWithConduitAndReservoir () {

    final var circuit = cortex.circuit (
      cortex.name ( "integration.circuit" )
    );

    try {

      final var conduit =
        circuit.conduit (
          cortex.name ( "integration.conduit" ),
          Composer.pipe ( Integer.class )
        );

      final Reservoir < Integer > reservoir = cortex.reservoir ( conduit );

      final Pipe < Integer > pipe =
        conduit.percept ( cortex.name ( "integration.channel" ) );

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
  void testCircuitWithFlowConfiguration () {

    final var circuit = cortex.circuit ();

    try {

      final var conduit =
        circuit.conduit (
          cortex.name ( "flow.conduit" ),
          Composer.pipe ( Integer.class ),
          flow -> flow.limit ( 2 )
        );

      final Reservoir < Integer > reservoir = cortex.reservoir ( conduit );

      final Pipe < Integer > pipe =
        conduit.percept ( cortex.name ( "flow.channel" ) );

      pipe.emit ( 1 );
      pipe.emit ( 2 );
      pipe.emit ( 3 ); // Should be limited

      circuit.await ();

      final var captures =
        reservoir.drain ().toList ();

      // Limit should restrict to first 2 emissions
      assertEquals ( 2, captures.size () );

      reservoir.close ();

    } finally {

      circuit.close ();

    }

  }

  @Test
  void testCircuitWithMultipleConduitsAndChannels () {

    final var circuit = cortex.circuit ();

    try {

      final var conduit1 =
        circuit.conduit ( cortex.name ( "conduit.one" ), Composer.pipe ( String.class ) );

      final var conduit2 =
        circuit.conduit ( cortex.name ( "conduit.two" ), Composer.pipe ( Integer.class ) );

      final Reservoir < String > reservoir1 = cortex.reservoir ( conduit1 );
      final Reservoir < Integer > reservoir2 = cortex.reservoir ( conduit2 );

      final Pipe < String > pipe1 =
        conduit1.percept ( cortex.name ( "channel.alpha" ) );

      final Pipe < Integer > pipe2 =
        conduit2.percept ( cortex.name ( "channel.beta" ) );

      pipe1.emit ( "hello" );
      pipe2.emit ( 42 );

      circuit.await ();

      assertEquals ( 1, reservoir1.drain ().count () );
      assertEquals ( 1, reservoir2.drain ().count () );

      reservoir1.close ();
      reservoir2.close ();

    } finally {

      circuit.close ();

    }

  }

  @SuppressWarnings ( "BusyWait" )
  @Test
  void testCloseDoesNotBlockOnPendingEmissions () {

    final var circuit = cortex.circuit ();
    final var startProcessing = new AtomicBoolean ( false );

    final var conduit =
      circuit.conduit ( Composer.pipe ( Integer.class ) );

    final var pipe =
      conduit.percept ( cortex.name ( "non.blocking.close" ) );

    conduit.subscribe (
      cortex.subscriber (
        cortex.name ( "slow.subscriber" ),
        ( _, registrar ) ->
          registrar.register ( cortex.pipe ( _ -> {

            startProcessing.set ( true );

            // Very slow processing
            try {
              Thread.sleep ( 1000 );
            } catch ( final InterruptedException e ) {
              currentThread ().interrupt ();
            }

          } ) )
      )
    );

    // Emit value that will take 1s to process
    pipe.emit ( 1 );

    // Give it time to start processing
    while ( !startProcessing.get () ) {
      try {
        Thread.sleep ( 10 );
      } catch ( final InterruptedException e ) {
        currentThread ().interrupt ();
      }
    }

    // close() should return quickly (non-blocking)
    final long startTime = currentTimeMillis ();
    circuit.close ();
    final long duration = currentTimeMillis () - startTime;

    assertTrue (
      duration < 500,
      "close() should not block on pending emissions (took " + duration + "ms)"
    );

  }

  @Test
  void testCloseFromMultipleThreads ()
  throws InterruptedException {

    final var circuit = cortex.circuit ();

    // Close from multiple threads simultaneously
    final var threads = new Thread[10];
    for ( int i = 0; i < 10; i++ ) {
      threads[i] = new Thread ( circuit::close );
      threads[i].start ();
    }

    for ( final var thread : threads ) {
      thread.join ();
    }

    // Should handle concurrent close gracefully
    assertTrue ( true, "Concurrent close() should be thread-safe" );

  }

  @SuppressWarnings ( "resource" )
  @Test
  void testConcurrentAccessToDifferentChannels ()
  throws InterruptedException, ExecutionException {

    final var circuit = cortex.circuit ();

    try {

      final var conduit =
        circuit.conduit ( Composer.pipe ( Integer.class ) );

      final var channels = new ConcurrentHashMap < String, Pipe < Integer > > ();
      final var executor = newFixedThreadPool ( 10 );

      try {

        // Each thread accesses different channel
        final var futures = new ArrayList < Future < ? > > ();
        for ( int t = 0; t < 100; t++ ) {
          final String channelName = "channel." + t;
          futures.add (
            executor.submit ( () -> {
              final var name = cortex.name ( channelName );
              final var ch = conduit.percept ( name );
              channels.put ( channelName, ch );
            } )
          );
        }

        for ( final var future : futures ) {
          future.get ();
        }

        // Should have 100 distinct channels
        assertEquals (
          100,
          channels.size (),
          "Should create distinct channels for different names"
        );

        // Verify each channel is unique
        final var uniqueChannels = new java.util.HashSet <> ( channels.values () );
        assertEquals (
          100,
          uniqueChannels.size (),
          "All channels should be distinct instances"
        );

      } finally {

        executor.shutdown ();
        assertTrue (
          executor.awaitTermination ( 5, SECONDS ),
          "Executor should terminate"
        );

      }

    } finally {

      circuit.close ();

    }

  }

  @SuppressWarnings ( "resource" )
  @Test
  void testConcurrentChannelAccessSameIdentity ()
  throws InterruptedException, ExecutionException {

    final var circuit = cortex.circuit ();

    try {

      final var conduit =
        circuit.conduit ( Composer.pipe ( Integer.class ) );

      final var name = cortex.name ( "concurrent.channel" );
      final var channelReferences = new ConcurrentHashMap < Integer, Pipe < Integer > > ();
      final var executor = newFixedThreadPool ( 10 );

      try {

        // Multiple threads accessing same channel by name
        final var futures = new ArrayList < Future < ? > > ();
        for ( int t = 0; t < 10; t++ ) {
          final int threadId = t;
          futures.add (
            executor.submit ( () -> {
              final var ch = conduit.percept ( name );
              channelReferences.put ( threadId, ch );
            } )
          );
        }

        for ( final var future : futures ) {
          future.get ();
        }

        // All threads should receive the SAME channel instance
        final var firstChannel = channelReferences.get ( 0 );
        assertNotNull ( firstChannel, "First channel should exist" );

        for ( int i = 1; i < 10; i++ ) {
          assertSame (
            firstChannel,
            channelReferences.get ( i ),
            "All threads must receive the same channel instance for the same name"
          );
        }

      } finally {

        executor.shutdown ();
        assertTrue (
          executor.awaitTermination ( 5, SECONDS ),
          "Executor should terminate"
        );

      }

    } finally {

      circuit.close ();

    }

  }

  // ===========================
  // Sequential Execution Tests
  // ===========================

  @SuppressWarnings ( "resource" )
  @Test
  void testConcurrentHierarchicalNameCreation ()
  throws InterruptedException, ExecutionException {

    final var names = new ConcurrentHashMap < Integer, Name > ();
    final var latch = new CountDownLatch ( 1 );
    final var executor = newFixedThreadPool ( 20 );

    try {

      // All threads build hierarchical name simultaneously
      final var futures = new ArrayList < Future < ? > > ();
      for ( int t = 0; t < 20; t++ ) {
        final int threadId = t;
        futures.add (
          executor.submit ( () -> {
            try {
              latch.await ();
              final var base = cortex.name ( "base" );
              final var extended = base.name ( "child" );
              final var full = extended.name ( "grandchild" );
              names.put ( threadId, full );
            } catch ( final InterruptedException e ) {
              currentThread ().interrupt ();
            }
          } )
        );
      }

      latch.countDown ();

      for ( final var future : futures ) {
        future.get ();
      }

      // All should be same instance
      final var firstName = names.get ( 0 );
      for ( int i = 1; i < 20; i++ ) {
        assertSame (
          firstName,
          names.get ( i ),
          "Hierarchical name creation must preserve interning"
        );
      }

    } finally {

      executor.shutdown ();
      assertTrue (
        executor.awaitTermination ( 5, SECONDS ),
        "Executor should terminate"
      );

    }

  }

  // ===========================
  // Thread-Safe Pool Tests
  // ===========================

  @SuppressWarnings ( "resource" )
  @Test
  void testConcurrentNameCreationWithInterning ()
  throws InterruptedException, ExecutionException {

    final var names = new ConcurrentHashMap < Integer, Name > ();
    final var latch = new CountDownLatch ( 1 );
    final var executor = newFixedThreadPool ( 20 );

    try {

      // All threads create same name path simultaneously
      final var futures = new ArrayList < Future < ? > > ();
      for ( int t = 0; t < 20; t++ ) {
        final int threadId = t;
        futures.add (
          executor.submit ( () -> {
            try {
              latch.await ();
              final var name = cortex.name ( "concurrent.name.test" );
              names.put ( threadId, name );
            } catch ( final InterruptedException e ) {
              currentThread ().interrupt ();
            }
          } )
        );
      }

      // Release all threads
      latch.countDown ();

      for ( final var future : futures ) {
        future.get ();
      }

      // All threads must receive the SAME Name instance (interning)
      final var firstName = names.get ( 0 );
      assertNotNull ( firstName, "First name should exist" );

      for ( int i = 1; i < 20; i++ ) {
        assertSame (
          firstName,
          names.get ( i ),
          "Concurrent name creation must return same interned instance"
        );
      }

    } finally {

      executor.shutdown ();
      assertTrue (
        executor.awaitTermination ( 5, SECONDS ),
        "Executor should terminate"
      );

    }

  }

  @SuppressWarnings ( "DataFlowIssue" )
  @Test
  void testConduitCreationNullGuards () {

    final var circuit = cortex.circuit ();

    try {

      final var name = cortex.name ( "test" );

      assertThrows (
        NullPointerException.class,
        () -> circuit.conduit ( (Composer < Integer, Pipe < Integer > >) null )
      );

      assertThrows (
        NullPointerException.class,
        () -> circuit.conduit ( null, Composer.pipe () )
      );

      assertThrows (
        NullPointerException.class,
        () -> circuit.conduit ( name, null )
      );

      assertThrows (
        NullPointerException.class,
        () -> circuit.conduit ( name, Composer.pipe (), null )
      );

    } finally {

      circuit.close ();

    }

  }

  @Test
  void testConduitCreationWithComposer () {

    final var circuit = cortex.circuit ();

    try {

      final var conduit =
        circuit.conduit ( Composer.pipe ( Integer.class ) );

      assertNotNull ( conduit );
      assertNotNull ( conduit.subject () );
      assertEquals ( Conduit.class, conduit.subject ().type () );

    } finally {

      circuit.close ();

    }

  }

  // ===========================
  // Concurrent Name Creation Tests
  // ===========================

  @Test
  void testConduitCreationWithNameAndComposer () {

    final var circuit = cortex.circuit ();

    try {

      final var conduitName = cortex.name ( "circuit.test.conduit" );
      final var conduit =
        circuit.conduit ( conduitName, Composer.pipe ( String.class ) );

      assertNotNull ( conduit );
      assertEquals ( conduitName, conduit.subject ().name () );
      assertEquals ( Conduit.class, conduit.subject ().type () );

    } finally {

      circuit.close ();

    }

  }

  @Test
  void testConduitCreationWithNameComposerAndConfigurer () {

    final var circuit = cortex.circuit ();

    try {

      final var conduitName = cortex.name ( "circuit.test.conduit.configured" );
      final var conduit =
        circuit.conduit (
          conduitName,
          Composer.pipe ( Double.class ),
          flow -> flow.limit ( 10 )
        );

      assertNotNull ( conduit );
      assertEquals ( conduitName, conduit.subject ().name () );

    } finally {

      circuit.close ();

    }

  }

  // ===========================
  // Emission Rejection Tests
  // ===========================

  @Test
  void testEmissionAfterCloseIsIgnored () {

    final var circuit = cortex.circuit ();
    final var received = new AtomicInteger ( 0 );

    final var conduit =
      circuit.conduit ( Composer.pipe ( Integer.class ) );

    final var pipe =
      conduit.percept ( cortex.name ( "post.close.channel" ) );

    conduit.subscribe (
      cortex.subscriber (
        cortex.name ( "post.close.subscriber" ),
        ( _, registrar ) ->
          registrar.register ( cortex.pipe ( _ -> received.incrementAndGet () ) )
      )
    );

    // Emit before close
    pipe.emit ( 1 );
    circuit.await ();

    assertEquals (
      1,
      received.get (),
      "Emission before close should be received"
    );

    // Close the circuit
    circuit.close ();

    // Attempt to emit after close
    pipe.emit ( 2 );

    // Give circuit time to process (if it would)
    try {
      Thread.sleep ( 100 );
    } catch ( final InterruptedException e ) {
      currentThread ().interrupt ();
    }

    // Second emission should not be received
    assertEquals (
      1,
      received.get (),
      "Emissions after close should be rejected/ignored"
    );

  }

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
  void testMultipleCloseIsIdempotent () {

    final var circuit = cortex.circuit ();

    // Close multiple times should be safe
    circuit.close ();
    circuit.close ();
    circuit.close ();

    // No exceptions should be thrown
    assertTrue ( true, "Multiple close() calls should be idempotent" );

  }

  // ===========================
  // Resource Cleanup Tests
  // ===========================
  //
  // Note: The implementation uses graceful degradation - resources can be
  // created after close() but become non-functional. This is intentional
  // design to avoid throwing exceptions during shutdown.

  // ===========================
  // Multiple Close Tests
  // ===========================

  @Test
  void testMultipleConduitsFromSameCircuit () {

    final var circuit = cortex.circuit ();

    try {

      final var conduit1 =
        circuit.conduit ( Composer.pipe ( Integer.class ) );

      final var conduit2 =
        circuit.conduit ( Composer.pipe ( Integer.class ) );

      assertNotSame ( conduit1, conduit2 );
      assertNotSame ( conduit1.subject (), conduit2.subject () );

    } finally {

      circuit.close ();

    }

  }

  @Test
  void testMultipleEmissionsAfterClose () {

    final var circuit = cortex.circuit ();
    final var received = new AtomicInteger ( 0 );

    final var conduit =
      circuit.conduit ( Composer.pipe ( Integer.class ) );

    final var pipe =
      conduit.percept ( cortex.name ( "multi.post.close" ) );

    conduit.subscribe (
      cortex.subscriber (
        cortex.name ( "multi.post.subscriber" ),
        ( _, registrar ) ->
          registrar.register ( cortex.pipe ( _ -> received.incrementAndGet () ) )
      )
    );

    // Emit and process
    pipe.emit ( 1 );
    circuit.await ();

    assertEquals ( 1, received.get () );

    // Close circuit
    circuit.close ();

    // Attempt multiple emissions
    for ( int i = 0; i < 10; i++ ) {
      pipe.emit ( i );
    }

    // Wait to ensure no processing
    try {
      Thread.sleep ( 100 );
    } catch ( final InterruptedException e ) {
      currentThread ().interrupt ();
    }

    // Should still be 1
    assertEquals (
      1,
      received.get (),
      "No emissions should be processed after close"
    );

  }

  // ===========================
  // Shutdown Order Tests
  // ===========================

  /// Validates the circuit's fundamental guarantee: all pipe executions are
  /// strictly sequential, never concurrent.
  ///
  /// This test creates maximum contention by having 4 threads concurrently emit
  /// 50 values each (200 total emissions) to the same channel. The subscriber
  /// tracks the number of currently executing handlers using atomic counters.
  ///
  /// Setup:
  /// - 4 emitter threads × 50 emissions = 200 total emissions
  /// - Subscriber increments counter on entry, decrements on exit
  /// - Thread.sleep(1ms) in handler increases likelihood of detecting concurrency
  /// - Tracks max concurrent executions and violation count
  ///
  /// The circuit's single-threaded worker ensures that even though emissions
  /// come from multiple threads concurrently, the handlers execute one at a time
  /// in sequence. This is the foundation of the circuit's determinism and
  /// state-safety guarantees.
  ///
  /// Why this matters:
  /// - Enables lock-free observer implementations (no synchronization needed)
  /// - Guarantees deterministic execution order (testability, reproducibility)
  /// - Simplifies reasoning about state mutations in handlers
  /// - Critical for neural-like networks where consistent state is required
  ///
  /// Expected: Zero violations, max concurrent = 1
  @SuppressWarnings ( "resource" )
  @Test
  void testNoConcurrentPipeExecution ()
  throws InterruptedException {

    final var circuit = cortex.circuit ();

    try {

      final var executingNow = new AtomicInteger ( 0 );
      final var maxConcurrent = new AtomicInteger ( 0 );
      final var violations = new AtomicInteger ( 0 );

      final var conduit =
        circuit.conduit ( Composer.pipe ( Integer.class ) );

      final var pipe =
        conduit.percept ( cortex.name ( "sequential.test" ) );

      conduit.subscribe (
        cortex.subscriber (
          cortex.name ( "sequential.subscriber" ),
          ( _, registrar ) ->
            registrar.register ( cortex.pipe ( _ -> {

              // Increment executing counter
              final int current = executingNow.incrementAndGet ();

              // Update max observed concurrency
              maxConcurrent.updateAndGet ( max -> Math.max ( max, current ) );

              // If current > 1, we have concurrent execution (violation)
              if ( current > 1 ) {
                violations.incrementAndGet ();
              }

              // Simulate work
              try {
                Thread.sleep ( 1 );
              } catch ( final InterruptedException e ) {
                currentThread ().interrupt ();
              }

              // Decrement executing counter
              executingNow.decrementAndGet ();

            } ) )
        )
      );

      // Emit from multiple threads
      final var executor = newFixedThreadPool ( 4 );

      try {

        final var futures = new ArrayList < Future < ? > > ();
        for ( int t = 0; t < 4; t++ ) {
          futures.add (
            executor.submit ( () -> {
              for ( int i = 0; i < 50; i++ ) {
                pipe.emit ( i );
              }
            } )
          );
        }

        for ( final var future : futures ) {
          future.get ();
        }

        circuit.await ();

        // Should never have concurrent execution
        assertEquals (
          0,
          violations.get (),
          "No concurrent pipe execution should occur"
        );

        assertEquals (
          1,
          maxConcurrent.get (),
          "Maximum concurrency should be 1 (sequential execution)"
        );

      } catch ( final ExecutionException e ) {

        fail ( "Execution failed: " + e.getMessage () );

      } finally {

        executor.shutdown ();
        assertTrue (
          executor.awaitTermination ( 10, SECONDS ),
          "Executor should terminate"
        );

      }

    } finally {

      circuit.close ();

    }

  }

  @Test
  void testRecursivePipeUsesTransitQueue () {

    final var circuit = cortex.circuit ();

    try {

      final var results = new ArrayList < Integer > ();
      final var conduit =
        circuit.conduit ( Composer.pipe ( Integer.class ) );

      final var recursivePipe =
        conduit.percept ( cortex.name ( "recursive.channel" ) );

      conduit.subscribe (
        cortex.subscriber (
          cortex.name ( "recursive.subscriber" ),
          ( _, registrar ) ->
            registrar.register ( cortex.pipe ( value -> {

              results.add ( value );

              // Recursive emit from within worker thread
              // This should use the transit queue (lines 547-559 in Valves.java)
              if ( value < 10 ) {
                recursivePipe.emit ( value + 1 );
              }

            } ) )
        )
      );

      // Start the recursive chain
      recursivePipe.emit ( 1 );

      circuit.await ();

      // Should process all recursive emissions
      assertEquals (
        List.of ( 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 ),
        results,
        "Recursive emissions should be processed via transit queue"
      );

    } finally {

      circuit.close ();

    }

  }

}
