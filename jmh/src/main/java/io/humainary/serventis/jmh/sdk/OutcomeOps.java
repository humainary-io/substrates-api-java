// Copyright (c) 2025 William David Louth

package io.humainary.serventis.jmh.sdk;

import io.humainary.substrates.api.Substrates;
import io.humainary.substrates.ext.serventis.sdk.Outcomes;
import io.humainary.substrates.ext.serventis.sdk.Outcomes.Outcome;
import io.humainary.substrates.ext.serventis.sdk.Outcomes.Sign;
import org.openjdk.jmh.annotations.*;

import static io.humainary.substrates.ext.serventis.sdk.Outcomes.Sign.SUCCESS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for Outcomes.Outcome operations.
///
/// Measures performance of outcome creation and sign emissions for
/// binary verdict operations: SUCCESS and FAIL.
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class OutcomeOps implements Substrates {

  private static final String OUTCOME_NAME = "payment.outcomes";
  private static final int    BATCH_SIZE   = 1000;

  private Cortex                    cortex;
  private Circuit                   circuit;
  private Conduit < Outcome, Sign > conduit;
  private Outcome                   outcome;
  private Name                      name;

  ///
  /// Benchmark emitting a FAIL sign.
  ///

  @Benchmark
  public void emit_fail () {

    outcome.fail ();

  }

  ///
  /// Benchmark batched FAIL emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_fail_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      outcome.fail ();
    }

  }

  ///
  /// Benchmark generic sign emission.
  ///

  @Benchmark
  public void emit_sign () {

    outcome.sign (
      SUCCESS
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
      outcome.sign (
        SUCCESS
      );
    }

  }

  ///
  /// Benchmark emitting a SUCCESS sign.
  ///

  @Benchmark
  public void emit_success () {

    outcome.success ();

  }

  ///
  /// Benchmark batched SUCCESS emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_success_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      outcome.success ();
    }

  }

  ///
  /// Benchmark outcome retrieval from conduit.
  ///

  @Benchmark
  public Outcome outcome_from_conduit () {

    return
      conduit.percept (
        name
      );

  }

  ///
  /// Benchmark batched outcome retrieval from conduit.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Outcome outcome_from_conduit_batch () {

    Outcome result = null;

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
        Outcomes::composer
      );

    outcome =
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
        OUTCOME_NAME
      );

  }

  @TearDown ( Level.Iteration )
  public void tearDownIteration () {

    circuit.close ();

  }

}
