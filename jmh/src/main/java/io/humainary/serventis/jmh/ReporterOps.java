// Copyright (c) 2025 William David Louth

package io.humainary.serventis.jmh;

import io.humainary.substrates.api.Substrates;
import io.humainary.substrates.ext.serventis.ext.Reporters;
import io.humainary.substrates.ext.serventis.ext.Reporters.Reporter;
import io.humainary.substrates.ext.serventis.ext.Reporters.Sign;
import org.openjdk.jmh.annotations.*;

import static io.humainary.substrates.ext.serventis.ext.Reporters.Sign.NORMAL;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for Reporters.Reporter operations.
///
/// Measures performance of reporter creation and sign emissions for situational
/// assessments: NORMAL, WARNING, and CRITICAL.
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class ReporterOps implements Substrates {

  private static final String REPORTER_NAME = "db.pool";
  private static final int    BATCH_SIZE    = 1000;

  private Cortex                     cortex;
  private Circuit                    circuit;
  private Conduit < Reporter, Sign > conduit;
  private Reporter                   reporter;
  private Name                       name;

  ///
  /// Benchmark emitting a CRITICAL sign.
  ///

  @Benchmark
  public void emit_critical () {

    reporter.critical ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_critical_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) reporter.critical ();
  }

  ///
  /// Benchmark emitting a NORMAL sign.
  ///

  @Benchmark
  public void emit_normal () {

    reporter.normal ();

  }


  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_normal_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) reporter.normal ();
  }

  ///
  /// Benchmark generic sign emission.
  ///

  @Benchmark
  public void emit_sign () {

    reporter.sign (
      NORMAL
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
      reporter.sign (
        NORMAL
      );
    }

  }

  ///
  /// Benchmark emitting a WARNING sign.
  ///

  @Benchmark
  public void emit_warning () {

    reporter.warning ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_warning_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) reporter.warning ();
  }

  ///
  /// Benchmark reporter retrieval from conduit.
  ///

  @Benchmark
  public Reporter reporter_from_conduit () {

    return
      conduit.percept (
        name
      );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Reporter reporter_from_conduit_batch () {
    Reporter result = null;
    for ( var i = 0; i < BATCH_SIZE; i++ ) result = conduit.percept ( name );
    return result;
  }

  @Setup ( Level.Iteration )
  public void setupIteration () {

    circuit =
      cortex.circuit ();

    conduit =
      circuit.conduit (
        Reporters::composer
      );

    reporter =
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
        REPORTER_NAME
      );

  }

  @TearDown ( Level.Iteration )
  public void tearDownIteration () {

    circuit.close ();

  }

}
