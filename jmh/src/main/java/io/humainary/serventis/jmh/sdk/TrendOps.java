// Copyright (c) 2025 William David Louth

package io.humainary.serventis.jmh.sdk;

import io.humainary.substrates.api.Substrates;
import io.humainary.substrates.ext.serventis.sdk.Trends;
import io.humainary.substrates.ext.serventis.sdk.Trends.Sign;
import io.humainary.substrates.ext.serventis.sdk.Trends.Trend;
import org.openjdk.jmh.annotations.*;

import static io.humainary.substrates.ext.serventis.sdk.Trends.Sign.STABLE;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for Trends.Trend methods.
///
/// Measures performance of trend creation and sign emissions for
/// pattern detection: STABLE, DRIFT, SPIKE, CYCLE, CHAOS.
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class TrendOps implements Substrates {

  private static final String TREND_NAME = "api.latency";
  private static final int    BATCH_SIZE = 1000;

  private Cortex                  cortex;
  private Circuit                 circuit;
  private Conduit < Trend, Sign > conduit;
  private Trend                   trend;
  private Name                    name;

  ///
  /// Benchmark emitting a CHAOS sign.
  ///

  @Benchmark
  public void emit_chaos () {

    trend.chaos ();

  }

  ///
  /// Benchmark batched CHAOS emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_chaos_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      trend.chaos ();
    }

  }

  ///
  /// Benchmark emitting a CYCLE sign.
  ///

  @Benchmark
  public void emit_cycle () {

    trend.cycle ();

  }

  ///
  /// Benchmark batched CYCLE emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_cycle_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      trend.cycle ();
    }

  }

  ///
  /// Benchmark emitting a DRIFT sign.
  ///

  @Benchmark
  public void emit_drift () {

    trend.drift ();

  }

  ///
  /// Benchmark batched DRIFT emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_drift_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      trend.drift ();
    }

  }

  ///
  /// Benchmark generic sign emission.
  ///

  @Benchmark
  public void emit_sign () {

    trend.sign (
      STABLE
    );

  }

  ///
  /// Benchmark batched generic sign emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_sign_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      trend.sign (
        STABLE
      );
    }

  }

  ///
  /// Benchmark emitting a SPIKE sign.
  ///

  @Benchmark
  public void emit_spike () {

    trend.spike ();

  }

  ///
  /// Benchmark batched SPIKE emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_spike_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      trend.spike ();
    }

  }

  ///
  /// Benchmark emitting a STABLE sign.
  ///

  @Benchmark
  public void emit_stable () {

    trend.stable ();

  }

  ///
  /// Benchmark batched STABLE emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_stable_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      trend.stable ();
    }

  }

  ///
  /// Benchmark trend retrieval from conduit.
  ///

  @Benchmark
  public Trend trend_from_conduit () {

    return
      conduit.percept (
        name
      );

  }

  ///
  /// Benchmark batched trend retrieval from conduit.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Trend trend_from_conduit_batch () {

    Trend result = null;

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
        Trends::composer
      );

    trend =
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
        TREND_NAME
      );

  }

  @TearDown ( Level.Iteration )
  public void tearDownIteration () {

    circuit.close ();

  }

}
