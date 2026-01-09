// Copyright (c) 2025 William David Louth

package io.humainary.serventis.jmh.opt.flow;

import io.humainary.substrates.api.Substrates;
import io.humainary.substrates.ext.serventis.opt.flow.Breakers;
import io.humainary.substrates.ext.serventis.opt.flow.Breakers.Breaker;
import io.humainary.substrates.ext.serventis.opt.flow.Breakers.Sign;
import org.openjdk.jmh.annotations.*;

import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for Breakers.Breaker operations.
///
/// Measures performance of breaker creation and sign emissions for circuit breaker
/// state transitions: CLOSE, OPEN, HALF_OPEN, TRIP, PROBE, and RESET.
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class BreakerOps implements Substrates {

  private static final String BREAKER_NAME = "api.breaker";
  private static final int    BATCH_SIZE   = 1000;

  private Cortex                    cortex;
  private Circuit                   circuit;
  private Conduit < Breaker, Sign > conduit;
  private Breaker                   breaker;
  private Name                      name;

  ///
  /// Benchmark breaker retrieval from conduit.
  ///

  @Benchmark
  public Breaker breaker_from_conduit () {

    return
      conduit.percept (
        name
      );

  }

  ///
  /// Benchmark batched breaker retrieval from conduit.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Breaker breaker_from_conduit_batch () {

    Breaker result = null;

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

  ///
  /// Benchmark emitting a CLOSE sign.
  ///

  @Benchmark
  public void emit_close () {

    breaker.close ();

  }

  ///
  /// Benchmark batched CLOSE emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_close_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      breaker.close ();
    }

  }

  ///
  /// Benchmark emitting a HALF_OPEN sign.
  ///

  @Benchmark
  public void emit_half_open () {

    breaker.halfOpen ();

  }

  ///
  /// Benchmark batched HALF_OPEN emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_half_open_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      breaker.halfOpen ();
    }

  }

  ///
  /// Benchmark emitting an OPEN sign.
  ///

  @Benchmark
  public void emit_open () {

    breaker.open ();

  }

  ///
  /// Benchmark batched OPEN emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_open_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      breaker.open ();
    }

  }

  ///
  /// Benchmark emitting a PROBE sign.
  ///

  @Benchmark
  public void emit_probe () {

    breaker.probe ();

  }

  ///
  /// Benchmark batched PROBE emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_probe_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      breaker.probe ();
    }

  }

  ///
  /// Benchmark emitting a RESET sign.
  ///

  @Benchmark
  public void emit_reset () {

    breaker.reset ();

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
      breaker.reset ();
    }

  }

  ///
  /// Benchmark generic sign emission.
  ///

  @Benchmark
  public void emit_sign () {

    breaker.sign (
      Sign.CLOSE
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
      breaker.sign (
        Sign.CLOSE
      );
    }

  }

  ///
  /// Benchmark emitting a TRIP sign.
  ///

  @Benchmark
  public void emit_trip () {

    breaker.trip ();

  }

  ///
  /// Benchmark batched TRIP emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_trip_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      breaker.trip ();
    }

  }

  @Setup ( Level.Iteration )
  public void setupIteration () {

    circuit =
      cortex.circuit ();

    conduit =
      circuit.conduit (
        Breakers::composer
      );

    breaker =
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
        BREAKER_NAME
      );

  }

  @TearDown ( Level.Iteration )
  public void tearDownIteration () {

    circuit.close ();

  }

}
