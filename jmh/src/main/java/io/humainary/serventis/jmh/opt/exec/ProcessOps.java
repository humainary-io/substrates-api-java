// Copyright (c) 2025 William David Louth

package io.humainary.serventis.jmh.opt.exec;

import io.humainary.substrates.api.Substrates;
import io.humainary.substrates.ext.serventis.opt.exec.Processes;
import io.humainary.substrates.ext.serventis.opt.exec.Processes.Process;
import io.humainary.substrates.ext.serventis.opt.exec.Processes.Sign;
import org.openjdk.jmh.annotations.*;

import static io.humainary.substrates.ext.serventis.opt.exec.Processes.Sign.SPAWN;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for Processes.Process operations.
///
/// Measures performance of process creation and sign emissions for OS-level
/// process lifecycle: SPAWN, START, STOP, FAIL, CRASH, KILL, RESTART,
/// SUSPEND, and RESUME.
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class ProcessOps implements Substrates {

  private static final String PROCESS_NAME = "worker.process";
  private static final int    BATCH_SIZE   = 1000;

  private Cortex                    cortex;
  private Circuit                   circuit;
  private Conduit < Process, Sign > conduit;
  private Process                   process;
  private Name                      name;

  ///
  /// Benchmark emitting a CRASH sign.
  ///

  @Benchmark
  public void emit_crash () {

    process.crash ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_crash_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) process.crash ();
  }

  ///
  /// Benchmark emitting a FAIL sign.
  ///

  @Benchmark
  public void emit_fail () {

    process.fail ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_fail_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) process.fail ();
  }

  ///
  /// Benchmark emitting a KILL sign.
  ///

  @Benchmark
  public void emit_kill () {

    process.kill ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_kill_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) process.kill ();
  }

  ///
  /// Benchmark emitting a RESTART sign.
  ///

  @Benchmark
  public void emit_restart () {

    process.restart ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_restart_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) process.restart ();
  }

  ///
  /// Benchmark emitting a RESUME sign.
  ///

  @Benchmark
  public void emit_resume () {

    process.resume ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_resume_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) process.resume ();
  }

  ///
  /// Benchmark generic sign emission.
  ///

  @Benchmark
  public void emit_sign () {

    process.sign (
      SPAWN
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
      process.sign (
        SPAWN
      );
    }

  }

  ///
  /// Benchmark emitting a SPAWN sign.
  ///

  @Benchmark
  public void emit_spawn () {

    process.spawn ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_spawn_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) process.spawn ();
  }

  ///
  /// Benchmark emitting a START sign.
  ///

  @Benchmark
  public void emit_start () {

    process.start ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_start_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) process.start ();
  }

  ///
  /// Benchmark emitting a STOP sign.
  ///

  @Benchmark
  public void emit_stop () {

    process.stop ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_stop_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) process.stop ();
  }

  ///
  /// Benchmark emitting a SUSPEND sign.
  ///

  @Benchmark
  public void emit_suspend () {

    process.suspend ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_suspend_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) process.suspend ();
  }

  ///
  /// Benchmark process retrieval from conduit.
  ///

  @Benchmark
  public Process process_from_conduit () {

    return
      conduit.percept (
        name
      );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Process process_from_conduit_batch () {
    Process result = null;
    for ( var i = 0; i < BATCH_SIZE; i++ ) result = conduit.percept ( name );
    return result;
  }

  @Setup ( Level.Iteration )
  public void setupIteration () {

    circuit =
      cortex.circuit ();

    conduit =
      circuit.conduit (
        Processes::composer
      );

    process =
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
        PROCESS_NAME
      );

  }

  @TearDown ( Level.Iteration )
  public void tearDownIteration () {

    circuit.close ();

  }

}
