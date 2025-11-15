// Copyright (c) 2025 William David Louth

package io.humainary.serventis.jmh;

import io.humainary.substrates.api.Substrates;
import io.humainary.substrates.ext.serventis.ext.Monitors;
import io.humainary.substrates.ext.serventis.ext.Monitors.Monitor;
import io.humainary.substrates.ext.serventis.ext.Monitors.Signal;
import org.openjdk.jmh.annotations.*;

import static io.humainary.substrates.ext.serventis.ext.Monitors.Dimension.*;
import static io.humainary.substrates.ext.serventis.ext.Monitors.Sign.STABLE;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for Monitors.Monitor operations.
/// <p>
/// Measures performance of monitor creation and signal emissions with various
/// operational signs (STABLE, DEGRADED, DEFECTIVE, CONVERGING, DOWN) and
/// confidence dimensions (CONFIRMED, MEASURED, TENTATIVE).
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class MonitorOps implements Substrates {

  private static final String MONITOR_NAME = "service";
  private static final int    BATCH_SIZE   = 1000;

  private Cortex                      cortex;
  private Circuit                     circuit;
  private Conduit < Monitor, Signal > conduit;
  private Monitor                     monitor;
  private Name                        name;

  ///
  /// Benchmark emitting a CONVERGING signal with CONFIRMED dimension.
  ///

  @Benchmark
  public void emit_converging_confirmed () {

    monitor.converging (
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
      monitor.converging (
        CONFIRMED
      );
    }

  }

  ///
  /// Benchmark emitting a DEFECTIVE signal with TENTATIVE dimension.
  ///

  @Benchmark
  public void emit_defective_tentative () {

    monitor.defective (
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
      monitor.defective (
        TENTATIVE
      );
    }

  }

  ///
  /// Benchmark emitting a DEGRADED signal with MEASURED dimension.
  ///

  @Benchmark
  public void emit_degraded_measured () {

    monitor.degraded (
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
      monitor.degraded (
        MEASURED
      );
    }

  }

  ///
  /// Benchmark emitting a DOWN signal with CONFIRMED dimension.
  ///

  @Benchmark
  public void emit_down_confirmed () {

    monitor.down (
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
      monitor.down (
        CONFIRMED
      );
    }

  }

  ///
  /// Benchmark generic signal emission.
  ///

  @Benchmark
  public void emit_signal () {

    monitor.signal (
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
      monitor.signal (
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

    monitor.stable (
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
      monitor.stable (
        CONFIRMED
      );
    }

  }

  ///
  /// Benchmark monitor retrieval from conduit.
  ///

  @Benchmark
  public Monitor monitor_from_conduit () {

    return
      conduit.percept (
        name
      );

  }

  ///
  /// Benchmark batched monitor retrieval from conduit.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Monitor monitor_from_conduit_batch () {

    Monitor result = null;

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

  @Setup ( Level.Iteration )
  public void setupIteration () {

    circuit =
      cortex.circuit ();

    conduit =
      circuit.conduit (
        Monitors::composer
      );

    monitor =
      conduit.percept (
        name
      );

  }

  @Setup ( Level.Trial )
  public void setupTrial () {

    cortex =
      Substrates.cortex ();

    name =
      cortex.name (
        MONITOR_NAME
      );

  }

  @TearDown ( Level.Iteration )
  public void tearDownIteration () {

    circuit.close ();

  }

}
