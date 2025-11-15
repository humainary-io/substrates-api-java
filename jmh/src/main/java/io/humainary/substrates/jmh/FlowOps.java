// Copyright (c) 2025 William David Louth

package io.humainary.substrates.jmh;

import io.humainary.substrates.api.Substrates;
import org.openjdk.jmh.annotations.*;

import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Level.Iteration;
import static org.openjdk.jmh.annotations.Level.Trial;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for Flow operations.
///
/// Measures performance impact of flow operations including filtering (guard, sift),
/// stateful operations (diff, reduce), rate limiting (sample), and backpressure (limit).
/// Each benchmark compares against a baseline of no flow operations.
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class FlowOps
  implements Substrates {

  private static final String NAME_STR   = "test";
  private static final int    VALUE      = 42;
  private static final int    BATCH_SIZE = 1000;

  private Cortex           cortex;
  private Circuit          circuit;
  private Name             name;
  private Pipe < Integer > sink;

  ///
  /// BASELINE: 1000000 emissions without flow operations.
  /// Total time = 1000000 emissions + circuit processing + await.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void baseline_no_flow_await () {

    final var
      pipe =
      circuit.pipe (
        sink
      );

    for (
      int i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      pipe.emit (
        VALUE + i
      );
    }

    circuit.await ();

  }

  ///
  /// FLOW: 1000000 emissions through diff + guard.
  /// Deduplication + filtering - measures combined flow overhead.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void flow_combined_diff_guard_await () {

    final var
      pipe =
      circuit.pipe (
        sink,
        flow -> flow.diff ().guard ( v -> v > 0 )
      );

    for (
      int i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      pipe.emit (
        VALUE + i
      );
    }

    circuit.await ();

  }

  ///
  /// FLOW: 1000000 emissions through diff + sample(10).
  /// Deduplication + rate limiting - reduces downstream load.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void flow_combined_diff_sample_await () {

    final var
      pipe =
      circuit.pipe (
        sink,
        flow -> flow.diff ().sample ( 10 )
      );

    for (
      int i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      pipe.emit (
        VALUE + i
      );
    }

    circuit.await ();

  }

  ///
  /// FLOW: 1000000 emissions through guard + limit(1000).
  /// Filtering + backpressure - combines condition and capacity.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void flow_combined_guard_limit_await () {

    final var
      pipe =
      circuit.pipe (
        sink,
        flow -> flow.guard ( v -> v > 0 ).limit ( 1000 )
      );

    for (
      int i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      pipe.emit (
        VALUE + i
      );
    }

    circuit.await ();

  }

  ///
  /// FLOW: 1000000 emissions through diff (deduplication).
  /// Stateful filtering - only passes changed values.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void flow_diff_await () {

    final var
      pipe =
      circuit.pipe (
        sink,
        Flow::diff
      );

    for (
      int i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      pipe.emit (
        VALUE + i
      );
    }

    circuit.await ();

  }

  ///
  /// FLOW: 1000000 emissions through guard (predicate filtering).
  /// Stateless filtering - tests each value against condition.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void flow_guard_await () {

    final var
      pipe =
      circuit.pipe (
        sink,
        flow -> flow.guard ( v -> v > 0 )
      );

    for (
      int i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      pipe.emit (
        VALUE + i
      );
    }

    circuit.await ();

  }

  ///
  /// FLOW: 1000000 emissions through limit(1000) (backpressure).
  /// Capacity limiting - tracks queue depth and applies backpressure.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void flow_limit_await () {

    final var
      pipe =
      circuit.pipe (
        sink,
        flow -> flow.limit ( 1000 )
      );

    for (
      int i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      pipe.emit (
        VALUE + i
      );
    }

    circuit.await ();

  }

  ///
  /// FLOW: 1000000 emissions through sample(10) (rate limiting).
  /// Downsampling - passes every 10th value, reducing throughput.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void flow_sample_await () {

    final var
      pipe =
      circuit.pipe (
        sink,
        flow -> flow.sample ( 10 )
      );

    for (
      int i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      pipe.emit (
        VALUE + i
      );
    }

    circuit.await ();

  }

  ///
  /// FLOW: 1000000 emissions through sift (range filtering).
  /// Comparator-based filtering - passes values in range [40, 200].
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void flow_sift_await () {

    final var
      pipe =
      circuit.pipe (
        sink,
        flow -> flow.sift (
          Integer::compareTo,
          sift -> sift.range ( 40, 200 )
        )
      );

    for (
      int i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      pipe.emit (
        VALUE + i
      );
    }

    circuit.await ();

  }

  @Setup ( Iteration )
  public void setupIteration () {

    circuit =
      cortex.circuit (
        name
      );

    sink =
      cortex.pipe (
        Integer.class
      );

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
