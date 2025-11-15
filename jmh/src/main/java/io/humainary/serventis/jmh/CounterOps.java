// Copyright (c) 2025 William David Louth

package io.humainary.serventis.jmh;

import io.humainary.substrates.api.Substrates;
import io.humainary.substrates.ext.serventis.ext.Counters;
import io.humainary.substrates.ext.serventis.ext.Counters.Counter;
import io.humainary.substrates.ext.serventis.ext.Counters.Sign;
import org.openjdk.jmh.annotations.*;

import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for Counters.Counter operations.
///
/// Measures performance of counter creation and sign emissions for monotonically
/// increasing counter operations: INCREMENT, OVERFLOW, and RESET.
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class CounterOps implements Substrates {

  private static final String COUNTER_NAME = "requests";
  private static final int    BATCH_SIZE   = 1000;

  private Cortex                    cortex;
  private Circuit                   circuit;
  private Conduit < Counter, Sign > conduit;
  private Counter                   counter;
  private Name                      name;

  ///
  /// Benchmark counter retrieval from conduit.
  ///

  @Benchmark
  public Counter counter_from_conduit () {

    return
      conduit.percept (
        name
      );

  }

  ///
  /// Benchmark batched counter retrieval from conduit.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Counter counter_from_conduit_batch () {

    Counter result = null;

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
  /// Benchmark emitting an INCREMENT sign.
  ///

  @Benchmark
  public void emit_increment () {

    counter.increment ();

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
      counter.increment ();
    }

  }

  ///
  /// Benchmark emitting an OVERFLOW sign.
  ///

  @Benchmark
  public void emit_overflow () {

    counter.overflow ();

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
      counter.overflow ();
    }

  }

  ///
  /// Benchmark emitting a RESET sign.
  ///

  @Benchmark
  public void emit_reset () {

    counter.reset ();

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
      counter.reset ();
    }

  }

  ///
  /// Benchmark generic sign emission.
  ///

  @Benchmark
  public void emit_sign () {

    counter.sign (
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
      counter.sign (
        Sign.INCREMENT
      );
    }

  }

  @Setup ( Level.Iteration )
  public void setupIteration () {

    circuit =
      cortex.circuit ();

    conduit =
      circuit.conduit (
        Counters::composer
      );

    counter =
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
        COUNTER_NAME
      );

  }

  @TearDown ( Level.Iteration )
  public void tearDownIteration () {

    circuit.close ();

  }

}
