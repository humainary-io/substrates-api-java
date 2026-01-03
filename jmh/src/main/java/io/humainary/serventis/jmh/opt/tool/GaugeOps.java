// Copyright (c) 2025 William David Louth

package io.humainary.serventis.jmh.opt.tool;

import io.humainary.substrates.api.Substrates;
import io.humainary.substrates.ext.serventis.opt.tool.Gauges;
import io.humainary.substrates.ext.serventis.opt.tool.Gauges.Gauge;
import io.humainary.substrates.ext.serventis.opt.tool.Gauges.Sign;
import org.openjdk.jmh.annotations.*;

import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for Gauges.Gauge operations.
///
/// Measures performance of gauge creation and sign emissions for bidirectional
/// gauge operations: INCREMENT, DECREMENT, OVERFLOW, UNDERFLOW, and RESET.
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class GaugeOps implements Substrates {

  private static final String GAUGE_NAME = "connections.active";
  private static final int    BATCH_SIZE = 1000;

  private Cortex                  cortex;
  private Circuit                 circuit;
  private Conduit < Gauge, Sign > conduit;
  private Gauge                   gauge;
  private Name                    name;

  ///
  /// Benchmark emitting a DECREMENT sign.
  ///

  @Benchmark
  public void emit_decrement () {

    gauge.decrement ();

  }

  ///
  /// Benchmark batched DECREMENT emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_decrement_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      gauge.decrement ();
    }

  }

  ///
  /// Benchmark emitting an INCREMENT sign.
  ///

  @Benchmark
  public void emit_increment () {

    gauge.increment ();

  }

  ///
  /// Benchmark batched INCREMENT emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_increment_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      gauge.increment ();
    }

  }

  ///
  /// Benchmark emitting an OVERFLOW sign.
  ///

  @Benchmark
  public void emit_overflow () {

    gauge.overflow ();

  }

  ///
  /// Benchmark batched OVERFLOW emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_overflow_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      gauge.overflow ();
    }

  }


  ///
  /// Benchmark emitting a RESET sign.
  ///

  @Benchmark
  public void emit_reset () {

    gauge.reset ();

  }

  ///
  /// Benchmark batched RESET emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_reset_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      gauge.reset ();
    }

  }

  ///
  /// Benchmark generic sign emission.
  ///

  @Benchmark
  public void emit_sign () {

    gauge.sign (
      Sign.INCREMENT
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
      gauge.sign (
        Sign.INCREMENT
      );
    }

  }

  ///
  /// Benchmark emitting an UNDERFLOW sign.
  ///

  @Benchmark
  public void emit_underflow () {

    gauge.underflow ();

  }

  ///
  /// Benchmark batched UNDERFLOW emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_underflow_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      gauge.underflow ();
    }

  }

  ///
  /// Benchmark gauge retrieval from conduit.
  ///

  @Benchmark
  public Gauge gauge_from_conduit () {

    return
      conduit.percept (
        name
      );

  }

  ///
  /// Benchmark batched gauge retrieval from conduit.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Gauge gauge_from_conduit_batch () {

    Gauge result = null;

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
        Gauges::composer
      );

    gauge =
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
        GAUGE_NAME
      );

  }

  @TearDown ( Level.Iteration )
  public void tearDownIteration () {

    circuit.close ();

  }

}
