// Copyright (c) 2025 William David Louth

package io.humainary.serventis.jmh.sdk;

import io.humainary.substrates.api.Substrates;
import io.humainary.substrates.ext.serventis.sdk.Statuses;
import io.humainary.substrates.ext.serventis.sdk.Statuses.Signal;
import io.humainary.substrates.ext.serventis.sdk.Statuses.Status;
import org.openjdk.jmh.annotations.*;

import static io.humainary.substrates.ext.serventis.sdk.Statuses.Dimension.*;
import static io.humainary.substrates.ext.serventis.sdk.Statuses.Sign.STABLE;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for Statuses.Status operations.
/// <p>
/// Measures performance of status creation and signal emissions with various
/// operational signs (STABLE, DEGRADED, DEFECTIVE, CONVERGING, DOWN) and
/// confidence dimensions (CONFIRMED, MEASURED, TENTATIVE).
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class StatusOps implements Substrates {

  private static final String STATUS_NAME = "service";
  private static final int    BATCH_SIZE  = 1000;

  private Cortex                     cortex;
  private Circuit                    circuit;
  private Conduit < Status, Signal > conduit;
  private Status                     status;
  private Name                       name;

  ///
  /// Benchmark emitting a CONVERGING signal with CONFIRMED dimension.
  ///

  @Benchmark
  public void emit_converging_confirmed () {

    status.converging (
      CONFIRMED
    );

  }

  ///
  /// Benchmark batched CONVERGING emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_converging_confirmed_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      status.converging (
        CONFIRMED
      );
    }

  }

  ///
  /// Benchmark emitting a DEFECTIVE signal with TENTATIVE dimension.
  ///

  @Benchmark
  public void emit_defective_tentative () {

    status.defective (
      TENTATIVE
    );

  }

  ///
  /// Benchmark batched DEFECTIVE emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_defective_tentative_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      status.defective (
        TENTATIVE
      );
    }

  }

  ///
  /// Benchmark emitting a DEGRADED signal with MEASURED dimension.
  ///

  @Benchmark
  public void emit_degraded_measured () {

    status.degraded (
      MEASURED
    );

  }

  ///
  /// Benchmark batched DEGRADED emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_degraded_measured_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      status.degraded (
        MEASURED
      );
    }

  }

  ///
  /// Benchmark emitting a DOWN signal with CONFIRMED dimension.
  ///

  @Benchmark
  public void emit_down_confirmed () {

    status.down (
      CONFIRMED
    );

  }

  ///
  /// Benchmark batched DOWN emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_down_confirmed_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      status.down (
        CONFIRMED
      );
    }

  }

  ///
  /// Benchmark generic signal emission.
  ///

  @Benchmark
  public void emit_signal () {

    status.signal (
      STABLE,
      CONFIRMED
    );

  }

  ///
  /// Benchmark batched generic signal emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_signal_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      status.signal (
        STABLE,
        CONFIRMED
      );
    }

  }

  ///
  /// Benchmark emitting a STABLE signal with CONFIRMED dimension.
  ///

  @Benchmark
  public void emit_stable_confirmed () {

    status.stable (
      CONFIRMED
    );

  }

  ///
  /// Benchmark batched STABLE emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_stable_confirmed_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      status.stable (
        CONFIRMED
      );
    }

  }

  @Setup ( Level.Iteration )
  public void setupIteration () {

    circuit =
      cortex.circuit ();

    conduit =
      circuit.conduit (
        Statuses::composer
      );

    status =
      conduit.percept (
        name
      );

    circuit.await ();

  }

  @Setup ( Level.Trial )
  public void setupTrial () {

    cortex =
      Substrates.cortex ();

    name =
      cortex.name (
        STATUS_NAME
      );

  }

  ///
  /// Benchmark status retrieval from conduit.
  ///

  @Benchmark
  public Status status_from_conduit () {

    return
      conduit.percept (
        name
      );

  }

  ///
  /// Benchmark batched status retrieval from conduit.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Status status_from_conduit_batch () {

    Status result = null;

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      result =
        conduit.percept (
          name
        );
    }

    return
      result;

  }

  @TearDown ( Level.Iteration )
  public void tearDownIteration () {

    circuit.close ();

  }

}
