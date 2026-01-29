// Copyright (c) 2025 William David Louth

package io.humainary.substrates.jmh;

import io.humainary.substrates.api.Substrates;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.atomic.AtomicInteger;

import static io.humainary.substrates.api.Substrates.Composer.pipe;
import static io.humainary.substrates.api.Substrates.Receptor.NOOP;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Level.Iteration;
import static org.openjdk.jmh.annotations.Level.Trial;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for Pipe operations.
///
/// Measures the core emission hot path - the most performance-critical operation
/// in the substrate framework. Async pipes dispatch through the circuit's queue
/// to its virtual thread.
///
/// Target performance: ~3ns per emission for async pipes.
///
/// ## Benchmark Categories
///
/// 1. **Single emission**: Measures individual emit() call cost
/// 2. **Batch emission**: Measures throughput with amortized overhead
/// 3. **Fan-out**: Measures emission to multiple receptors
/// 4. **Chained pipes**: Measures pipe-to-pipe forwarding
/// 5. **Flow operations**: Measures overhead of flow operators on hot path
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class PipeOps
  implements Substrates {

  private static final String NAME_STR   = "test";
  private static final int    VALUE      = 42;
  private static final int    BATCH_SIZE = 1000;

  private Cortex  cortex;
  private Name    name;
  private Circuit circuit;

  // Pre-created pipes for hot path measurement
  private Pipe < Integer > asyncPipe;
  private Pipe < Integer > asyncPipeWithFlow;
  private Pipe < Integer > chainedPipe;
  private Pipe < Integer > fanOutPipe;

  // Sink for collecting emissions (no-op receptor)
  private Pipe < Integer > sink;

  // Counter for verifying emissions
  private AtomicInteger counter;

  //
  // ASYNC PIPE BENCHMARKS
  // Async pipes dispatch through the circuit's queue to its virtual thread.
  // This is the standard hot path for the substrate framework.
  //

  ///
  /// Blackhole baseline - measures Blackhole.consume() cost.
  /// Use this to subtract framework overhead from pipe measurements.
  ///

  @Benchmark
  public static void baseline_blackhole (
    final Blackhole bh
  ) {

    bh.consume ( VALUE );

  }

  ///
  /// No-op receptor baseline - measures receptor invocation overhead.
  ///

  @SuppressWarnings ( "unchecked" )
  @Benchmark
  public static void baseline_receptor () {

    NOOP.receive ( VALUE );

  }

  ///
  /// Batch async emissions - measures throughput of queue dispatch.
  /// All emissions queued before await - tests queue batching behavior.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void async_emit_batch () {

    for (
      int i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      asyncPipe.emit ( VALUE + i );
    }

  }

  ///
  /// Batch async emissions with await - full throughput measurement.
  /// This is the key benchmark for validating sub-3ns emission target.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void async_emit_batch_await () {

    for (
      int i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      asyncPipe.emit ( VALUE + i );
    }

    circuit.await ();

  }

  //
  // FLOW OPERATION BENCHMARKS
  // Measures overhead of flow operators in the hot path.
  //

  ///
  /// Chained pipe emission - measures pipe forwarding cost.
  /// Source pipe emits to intermediate pipe, which emits to sink.
  /// Tests transit queue behavior (cascading emissions).
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void async_emit_chained_await () {

    for (
      int i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      chainedPipe.emit ( VALUE + i );
    }

    circuit.await ();

  }

  //
  // CHAINED PIPE BENCHMARKS
  // Measures pipe-to-pipe forwarding for neural-like networks.
  //

  ///
  /// Fan-out emission - measures multi-receptor dispatch.
  /// Single emission triggers 3 receptor invocations.
  /// Tests inlet iteration and receptor caching.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void async_emit_fanout_await () {

    for (
      int i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      fanOutPipe.emit ( VALUE + i );
    }

    circuit.await ();

  }

  //
  // FAN-OUT BENCHMARKS
  // Measures emission to multiple receptors (pub-sub pattern).
  //

  ///
  /// Single async emission - measures queue dispatch cost.
  /// Includes: requireNonNull, Job allocation, valve.receive().
  /// Does NOT wait for processing - measures only submission cost.
  ///

  @Benchmark
  public void async_emit_single () {

    asyncPipe.emit ( VALUE );

  }

  //
  // PIPE CREATION BENCHMARKS
  // Measures cost of creating new pipes (not hot path, but affects startup).
  //

  ///
  /// Single async emission with await - full round-trip cost.
  /// Measures: submission + queue processing + thread synchronization.
  ///

  @Benchmark
  public void async_emit_single_await () {

    asyncPipe.emit ( VALUE );
    circuit.await ();

  }

  ///
  /// Async emission through flow (guard + diff).
  /// Measures cost of flow operations during emission processing.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void async_emit_with_flow_await () {

    for (
      int i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      asyncPipeWithFlow.emit ( VALUE + i );
    }

    circuit.await ();

  }

  ///
  /// Counter baseline - measures AtomicInteger.incrementAndGet() cost.
  /// Represents minimal "real work" in a receptor.
  ///

  @Benchmark
  public int baseline_counter () {

    return counter.incrementAndGet ();

  }

  //
  // COMPARISON BENCHMARKS
  // Measures baseline costs for comparison.
  //

  ///
  /// Async pipe creation - measures circuit.pipe() factory cost.
  ///

  @Benchmark
  public Pipe < Integer > pipe_create () {

    return
      circuit.pipe (
        Receptor.of ( Integer.class )
      );

  }

  ///
  /// Pipe creation with target pipe - measures pipe chaining factory cost.
  ///

  @Benchmark
  public Pipe < Integer > pipe_create_chained () {

    return
      circuit.pipe (
        sink
      );

  }

  ///
  /// Async pipe creation with flow - measures flow configuration overhead.
  ///

  @Benchmark
  public Pipe < Integer > pipe_create_with_flow () {

    return
      circuit.pipe (
        Receptor.of ( Integer.class ),
        flow -> flow.guard ( v -> v > 0 ).diff ()
      );

  }

  @Setup ( Iteration )
  public void setupIteration () {

    counter =
      new AtomicInteger ();

    circuit =
      cortex.circuit (
        name
      );

    // No-op sink for collecting emissions
    sink =
      circuit.pipe (
        Receptor.of ( Integer.class )
      );

    // Async pipe - queue dispatch
    asyncPipe =
      circuit.pipe (
        Receptor.of ( Integer.class )
      );

    // Async pipe with flow operations
    asyncPipeWithFlow =
      circuit.pipe (
        Receptor.of ( Integer.class ),
        flow -> flow.guard ( v -> v > 0 ).diff ()
      );

    // Chained pipe - pipe to pipe forwarding
    final var intermediate =
      circuit.pipe (
        sink
      );

    chainedPipe =
      circuit.pipe (
        intermediate
      );

    // Fan-out pipe via conduit with multiple subscribers
    final var conduit =
      circuit.conduit (
        pipe ( Integer.class )
      );

    // Subscribe 3 receptors to create fan-out
    conduit.subscribe (
      circuit.subscriber (
        cortex.name ( "fanout" ),
        ( subject, registrar ) -> {
          registrar.register ( Receptor.of ( Integer.class ) );
          registrar.register ( Receptor.of ( Integer.class ) );
          registrar.register ( Receptor.of ( Integer.class ) );
        }
      )
    );

    // Get the channel's pipe for emission via the conduit
    fanOutPipe =
      conduit.percept ( name );

    // Warm up the circuit
    circuit.await ();

  }

  @Setup ( Trial )
  public void setupTrial () {

    cortex =
      Substrates.cortex ();

    name =
      cortex.name (
        NAME_STR
      );

  }

  @TearDown ( Iteration )
  public void tearDownIteration () {

    circuit.close ();

  }

}
