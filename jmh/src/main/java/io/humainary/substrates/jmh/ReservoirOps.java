// Copyright (c) 2025 William David Louth

package io.humainary.substrates.jmh;

import io.humainary.substrates.api.Substrates;
import org.openjdk.jmh.annotations.*;

import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Level.Iteration;
import static org.openjdk.jmh.annotations.Level.Trial;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for Reservoir operations.
///
/// Measures both the hot path impact of reservoir capture during emissions and the
/// cost of draining/processing captures. Reservoirs subscribe to sources and buffer
/// all emissions for testing/inspection, so we benchmark the overhead this adds
/// to the critical emission path as well as the retrieval operations.
///
///

@SuppressWarnings ( "DuplicatedCode" )
@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class ReservoirOps
  implements Substrates {

  private static final String NAME_STR   = "test";
  private static final int    VALUE      = 42;
  private static final int    COUNT      = 100;
  private static final int    BATCH_SIZE = 1000;

  private Cortex  cortex;
  private Circuit circuit;
  private Name    name;

  ///
  /// BASELINE: 100 emissions without reservoir (pure emission cost).
  ///

  @Benchmark
  @OperationsPerInvocation ( COUNT )
  public void baseline_emit_no_reservoir_await () {

    final var
      conduit =
      circuit.conduit (
        Composer.pipe ()
      );

    final var
      pipe =
      conduit.percept (
        name
      );

    for (
      int i = 0;
      i < COUNT;
      i++
    ) {
      pipe.emit (
        VALUE + i
      );
    }

    circuit.await ();

  }

  ///
  /// BATCHED BASELINE: 1000 emissions without reservoir.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void baseline_emit_no_reservoir_await_batch () {

    final var
      conduit =
      circuit.conduit (
        Composer.pipe ()
      );

    final var
      pipe =
      conduit.percept (
        name
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
  /// PATTERN: 100 emissions burst, then single drain.
  /// Tests batch emission + stream processing pattern.
  ///

  @Benchmark
  @OperationsPerInvocation ( COUNT )
  public long reservoir_burst_then_drain_await () {

    final var
      conduit =
      circuit.conduit (
        Composer.pipe ()
      );

    final var
      reservoir =
      cortex.reservoir (
        conduit
      );

    final var
      pipe =
      conduit.percept (
        name
      );

    // Burst
    for (
      int i = 0;
      i < COUNT;
      i++
    ) {
      pipe.emit (
        VALUE + i
      );
    }

    circuit.await ();

    // Single drain
    final var
      count =
      reservoir
        .drain ()
        .count ();

    reservoir.close ();

    return
      count;

  }

  ///
  /// BATCHED: 1000 emissions burst, then single drain.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public long reservoir_burst_then_drain_await_batch () {

    final var
      conduit =
      circuit.conduit (
        Composer.pipe ()
      );

    final var
      reservoir =
      cortex.reservoir (
        conduit
      );

    final var
      pipe =
      conduit.percept (
        name
      );

    // Burst
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

    // Single drain
    final var
      count =
      reservoir
        .drain ()
        .count ();

    reservoir.close ();

    return
      count;

  }

  ///
  /// DRAIN: 100 emissions + stream drain.
  ///

  @Benchmark
  @OperationsPerInvocation ( COUNT )
  public long reservoir_drain_await () {

    final var
      conduit =
      circuit.conduit (
        Composer.pipe ()
      );

    final var
      reservoir =
      cortex.reservoir (
        conduit
      );

    final var
      pipe =
      conduit.percept (
        name
      );

    for (
      int i = 0;
      i < COUNT;
      i++
    ) {
      pipe.emit (
        VALUE + i
      );
    }

    circuit.await ();

    final var
      count =
      reservoir
        .drain ()
        .count ();

    reservoir.close ();

    return
      count;

  }

  ///
  /// BATCHED: 1000 emissions with reservoir + drain.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public long reservoir_drain_await_batch () {

    final var
      conduit =
      circuit.conduit (
        Composer.pipe ()
      );

    final var
      reservoir =
      cortex.reservoir (
        conduit
      );

    final var
      pipe =
      conduit.percept (
        name
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

    final var
      count =
      reservoir
        .drain ()
        .count ();

    reservoir.close ();

    return
      count;

  }

  ///
  /// PATTERN: 5 cycles of (20 emissions + drain).
  /// Total: 100 emissions across 5 drain operations.
  ///

  @Benchmark
  @OperationsPerInvocation ( COUNT )
  public long reservoir_emit_drain_cycles_await () {

    final var
      conduit =
      circuit.conduit (
        Composer.pipe ()
      );

    final var
      reservoir =
      cortex.reservoir (
        conduit
      );

    final var
      pipe =
      conduit.percept (
        name
      );

    long
      total =
      0;

    for (
      int cycle = 0;
      cycle < 5;
      cycle++
    ) {

      for (
        int i = 0;
        i < 20;
        i++
      ) {
        pipe.emit (
          VALUE + i
        );
      }

      circuit.await ();

      total +=
        reservoir
          .drain ()
          .count ();

    }

    reservoir.close ();

    return
      total;

  }

  ///
  /// HOT PATH: 100 emissions with reservoir attached.
  /// Measures emission overhead when sink is capturing.
  ///

  @Benchmark
  @OperationsPerInvocation ( COUNT )
  public void reservoir_emit_with_capture_await () {

    final var
      conduit =
      circuit.conduit (
        Composer.pipe ()
      );

    final var
      reservoir =
      cortex.reservoir (
        conduit
      );

    final var
      pipe =
      conduit.percept (
        name
      );

    for (
      int i = 0;
      i < COUNT;
      i++
    ) {
      pipe.emit (
        VALUE + i
      );
    }

    circuit.await ();

    reservoir.close ();

  }

  ///
  /// BATCHED HOT PATH: 1000 emissions with reservoir attached.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void reservoir_emit_with_capture_await_batch () {

    final var
      conduit =
      circuit.conduit (
        Composer.pipe ()
      );

    final var
      reservoir =
      cortex.reservoir (
        conduit
      );

    final var
      pipe =
      conduit.percept (
        name
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

    reservoir.close ();

  }

  //
  // BATCHED BENCHMARKS (1000 emissions)
  // These benchmarks use a larger batch size for measuring amortized cost
  // with reduced measurement noise compared to the COUNT=100 benchmarks above.
  //

  ///
  /// PROCESS: 100 emissions + stream processing (sum values).
  /// Tests drain + stream operation overhead.
  ///

  @Benchmark
  @OperationsPerInvocation ( COUNT )
  public int reservoir_process_emissions_await () {

    final var
      conduit =
      circuit.conduit (
        Composer.pipe ()
      );

    final var
      reservoir =
      cortex.reservoir (
        conduit
      );

    final var
      pipe =
      conduit.percept (
        name
      );

    for (
      int i = 0;
      i < COUNT;
      i++
    ) {
      pipe.emit (
        VALUE + i
      );
    }

    circuit.await ();

    final var
      sum =
      reservoir
        .drain ()
        .mapToInt (
          c -> (Integer) c.emission ()
        )
        .sum ();

    reservoir.close ();

    return
      sum;

  }

  ///
  /// BATCHED PROCESS: 1000 emissions + stream processing (sum).
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public int reservoir_process_emissions_await_batch () {

    final var
      conduit =
      circuit.conduit (
        Composer.pipe ()
      );

    final var
      reservoir =
      cortex.reservoir (
        conduit
      );

    final var
      pipe =
      conduit.percept (
        name
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

    final var
      sum =
      reservoir
        .drain ()
        .mapToInt (
          c -> (Integer) c.emission ()
        )
        .sum ();

    reservoir.close ();

    return
      sum;

  }

  ///
  /// PROCESS: 100 emissions + stream processing (count subjects).
  /// Tests drain + stream operation overhead.
  ///

  @SuppressWarnings ( "MappingBeforeCount" )
  @Benchmark
  @OperationsPerInvocation ( COUNT )
  public long reservoir_process_subjects_await () {

    final var
      conduit =
      circuit.conduit (
        Composer.pipe ()
      );

    final var
      reservoir =
      cortex.reservoir (
        conduit
      );

    final var
      pipe =
      conduit.percept (
        name
      );

    for (
      int i = 0;
      i < COUNT;
      i++
    ) {
      pipe.emit (
        VALUE + i
      );
    }

    circuit.await ();

    final var
      count =
      reservoir
        .drain ()
        .map ( Capture::subject )
        .count ();

    reservoir.close ();

    return
      count;

  }

  @Setup ( Iteration )
  public void setupIteration () {

    circuit =
      cortex.circuit (
        name
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
