// Copyright (c) 2025 William David Louth

package io.humainary.serventis.jmh.sdk;

import io.humainary.substrates.api.Substrates;
import io.humainary.substrates.ext.serventis.sdk.Situations;
import io.humainary.substrates.ext.serventis.sdk.Situations.Signal;
import io.humainary.substrates.ext.serventis.sdk.Situations.Situation;
import org.openjdk.jmh.annotations.*;

import static io.humainary.substrates.ext.serventis.sdk.Situations.Dimension.CONSTANT;
import static io.humainary.substrates.ext.serventis.sdk.Situations.Sign.NORMAL;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for Situations.Situation operations.
///
/// Measures performance of situation creation and signal emissions (Sign × Dimension)
/// for situational assessments: NORMAL, WARNING, CRITICAL combined with CONSTANT variability.
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class SituationOps implements Substrates {

  private static final String SITUATION_NAME = "db.pool";
  private static final int    BATCH_SIZE     = 1000;

  private Cortex                        cortex;
  private Circuit                       circuit;
  private Conduit < Situation, Signal > conduit;
  private Situation                     situation;
  private Name                          name;

  ///
  /// Benchmark emitting a CRITICAL signal with CONSTANT dimension.
  ///

  @Benchmark
  public void emit_critical () {

    situation.critical (
      CONSTANT
    );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_critical_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) situation.critical ( CONSTANT );
  }

  ///
  /// Benchmark emitting a NORMAL signal with CONSTANT dimension.
  ///

  @Benchmark
  public void emit_normal () {

    situation.normal (
      CONSTANT
    );

  }


  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_normal_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) situation.normal ( CONSTANT );
  }

  ///
  /// Benchmark generic signal emission.
  ///

  @Benchmark
  public void emit_signal () {

    situation.signal (
      NORMAL,
      CONSTANT
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
      situation.signal (
        NORMAL,
        CONSTANT
      );
    }

  }

  ///
  /// Benchmark emitting a WARNING signal with CONSTANT dimension.
  ///

  @Benchmark
  public void emit_warning () {

    situation.warning (
      CONSTANT
    );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_warning_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) situation.warning ( CONSTANT );
  }

  @Setup ( Level.Iteration )
  public void setupIteration () {

    circuit =
      cortex.circuit ();

    conduit =
      circuit.conduit (
        Situations::composer
      );

    situation =
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
        SITUATION_NAME
      );

  }

  ///
  /// Benchmark situation retrieval from conduit.
  ///

  @Benchmark
  public Situation situation_from_conduit () {

    return
      conduit.percept (
        name
      );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Situation situation_from_conduit_batch () {
    Situation result = null;
    for ( var i = 0; i < BATCH_SIZE; i++ ) result = conduit.percept ( name );
    return result;
  }

  @TearDown ( Level.Iteration )
  public void tearDownIteration () {

    circuit.close ();

  }

}
