// Copyright (c) 2025 William David Louth

package io.humainary.serventis.jmh.opt.tool;

import io.humainary.substrates.api.Substrates;
import io.humainary.substrates.ext.serventis.opt.tool.Logs;
import io.humainary.substrates.ext.serventis.opt.tool.Logs.Log;
import io.humainary.substrates.ext.serventis.opt.tool.Logs.Sign;
import org.openjdk.jmh.annotations.*;

import static io.humainary.substrates.ext.serventis.opt.tool.Logs.Sign.INFO;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for Logs.Log operations.
///
/// Measures performance of log creation and sign emissions for logging
/// operations: SEVERE, WARNING, INFO, and DEBUG.
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class LogOps implements Substrates {

  private static final String LOG_NAME   = "com.acme.PaymentService";
  private static final int    BATCH_SIZE = 1000;

  private Cortex                cortex;
  private Circuit               circuit;
  private Conduit < Log, Sign > conduit;
  private Log                   log;
  private Name                  name;

  ///
  /// Benchmark emitting a DEBUG sign.
  ///

  @Benchmark
  public void emit_debug () {

    log.debug ();

  }

  ///
  /// Benchmark batched DEBUG emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_debug_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      log.debug ();
    }

  }

  ///
  /// Benchmark emitting an INFO sign.
  ///

  @Benchmark
  public void emit_info () {

    log.info ();

  }

  ///
  /// Benchmark batched INFO emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_info_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      log.info ();
    }

  }

  ///
  /// Benchmark emitting a SEVERE sign.
  ///

  @Benchmark
  public void emit_severe () {

    log.severe ();

  }

  ///
  /// Benchmark batched SEVERE emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_severe_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      log.severe ();
    }

  }

  ///
  /// Benchmark generic sign emission.
  ///

  @Benchmark
  public void emit_sign () {

    log.sign (
      INFO
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
      log.sign (
        INFO
      );
    }

  }

  ///
  /// Benchmark emitting a WARNING sign.
  ///

  @Benchmark
  public void emit_warning () {

    log.warning ();

  }

  ///
  /// Benchmark batched WARNING emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_warning_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      log.warning ();
    }

  }

  ///
  /// Benchmark log retrieval from conduit.
  ///

  @Benchmark
  public Log log_from_conduit () {

    return
      conduit.percept (
        name
      );

  }

  ///
  /// Benchmark batched log retrieval from conduit.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Log log_from_conduit_batch () {

    Log result = null;

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
        Logs::composer
      );

    log =
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
        LOG_NAME
      );

  }

  @TearDown ( Level.Iteration )
  public void tearDownIteration () {

    circuit.close ();

  }

}
