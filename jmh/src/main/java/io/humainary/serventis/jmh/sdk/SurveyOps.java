// Copyright (c) 2025 William David Louth

package io.humainary.serventis.jmh.sdk;

import io.humainary.substrates.api.Substrates;
import io.humainary.substrates.ext.serventis.sdk.Statuses;
import io.humainary.substrates.ext.serventis.sdk.Surveys;
import io.humainary.substrates.ext.serventis.sdk.Surveys.Signal;
import io.humainary.substrates.ext.serventis.sdk.Surveys.Survey;
import org.openjdk.jmh.annotations.*;

import static io.humainary.substrates.ext.serventis.sdk.Statuses.Sign.DEGRADED;
import static io.humainary.substrates.ext.serventis.sdk.Surveys.Dimension.*;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for Surveys.Survey operations.
/// <p>
/// Measures performance of survey signal emissions with various
/// dimensions (DIVIDED, MAJORITY, UNANIMOUS) using generic Sign types.
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class SurveyOps implements Substrates {

  private static final String SURVEY_NAME = "cluster.health";
  private static final int    BATCH_SIZE  = 1000;

  private Cortex                                                         cortex;
  private Circuit                                                        circuit;
  private Conduit < Survey < Statuses.Sign >, Signal < Statuses.Sign > > conduit;
  private Survey < Statuses.Sign >                                       survey;
  private Name                                                           name;

  ///
  /// Benchmark survey retrieval from conduit.
  ///

  @Benchmark
  public Survey < Statuses.Sign > survey_from_conduit () {

    return
      conduit.percept (
        name
      );

  }

  ///
  /// Benchmark batched survey retrieval from conduit.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Survey < Statuses.Sign > survey_from_conduit_batch () {

    Survey < Statuses.Sign > result = null;

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
  /// Benchmark emitting a DIVIDED dimension signal.
  ///

  @Benchmark
  public void emit_divided () {

    survey.signal (
      DEGRADED,
      DIVIDED
    );

  }

  ///
  /// Benchmark batched DIVIDED emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_divided_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      survey.signal (
        DEGRADED,
        DIVIDED
      );
    }

  }

  ///
  /// Benchmark emitting a MAJORITY dimension signal.
  ///

  @Benchmark
  public void emit_majority () {

    survey.signal (
      DEGRADED,
      MAJORITY
    );

  }

  ///
  /// Benchmark batched MAJORITY emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_majority_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      survey.signal (
        DEGRADED,
        MAJORITY
      );
    }

  }

  ///
  /// Benchmark emitting a UNANIMOUS dimension signal.
  ///

  @Benchmark
  public void emit_unanimous () {

    survey.signal (
      DEGRADED,
      UNANIMOUS
    );

  }

  ///
  /// Benchmark batched UNANIMOUS emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_unanimous_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      survey.signal (
        DEGRADED,
        UNANIMOUS
      );
    }

  }

  ///
  /// Benchmark generic signal emission.
  ///

  @Benchmark
  public void emit_signal () {

    survey.signal (
      DEGRADED,
      MAJORITY
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
      survey.signal (
        DEGRADED,
        MAJORITY
      );
    }

  }

  @Setup ( Level.Iteration )
  public void setupIteration () {

    circuit =
      cortex.circuit ();

    conduit =
      circuit.conduit (
        Surveys.composer (
          Statuses.Sign.class
        )
      );

    survey =
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
        SURVEY_NAME
      );

  }

  @TearDown ( Level.Iteration )
  public void tearDownIteration () {

    circuit.close ();

  }

}
