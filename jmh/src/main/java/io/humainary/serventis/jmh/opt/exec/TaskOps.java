// Copyright (c) 2025 William David Louth

package io.humainary.serventis.jmh.opt.exec;

import io.humainary.substrates.api.Substrates;
import io.humainary.substrates.ext.serventis.opt.exec.Tasks;
import io.humainary.substrates.ext.serventis.opt.exec.Tasks.Sign;
import io.humainary.substrates.ext.serventis.opt.exec.Tasks.Task;
import org.openjdk.jmh.annotations.*;

import static io.humainary.substrates.ext.serventis.opt.exec.Tasks.Sign.SUBMIT;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for Tasks.Task operations.
///
/// Measures performance of task creation and sign emissions for asynchronous
/// work unit lifecycle: SUBMIT, REJECT, SCHEDULE, START, PROGRESS, SUSPEND,
/// RESUME, COMPLETE, FAIL, CANCEL, and TIMEOUT.
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class TaskOps implements Substrates {

  private static final String TASK_NAME  = "worker.task";
  private static final int    BATCH_SIZE = 1000;

  private Cortex                 cortex;
  private Circuit                circuit;
  private Conduit < Task, Sign > conduit;
  private Task                   task;
  private Name                   name;

  ///
  /// Benchmark emitting a CANCEL sign.
  ///

  @Benchmark
  public void emit_cancel () {

    task.cancel ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_cancel_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) task.cancel ();
  }

  ///
  /// Benchmark emitting a COMPLETE sign.
  ///

  @Benchmark
  public void emit_complete () {

    task.complete ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_complete_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) task.complete ();
  }

  ///
  /// Benchmark emitting a FAIL sign.
  ///

  @Benchmark
  public void emit_fail () {

    task.fail ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_fail_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) task.fail ();
  }

  ///
  /// Benchmark emitting a PROGRESS sign.
  ///

  @Benchmark
  public void emit_progress () {

    task.progress ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_progress_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) task.progress ();
  }

  ///
  /// Benchmark emitting a REJECT sign.
  ///

  @Benchmark
  public void emit_reject () {

    task.reject ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_reject_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) task.reject ();
  }

  ///
  /// Benchmark emitting a RESUME sign.
  ///

  @Benchmark
  public void emit_resume () {

    task.resume ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_resume_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) task.resume ();
  }

  ///
  /// Benchmark emitting a SCHEDULE sign.
  ///

  @Benchmark
  public void emit_schedule () {

    task.schedule ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_schedule_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) task.schedule ();
  }

  ///
  /// Benchmark generic sign emission.
  ///

  @Benchmark
  public void emit_sign () {

    task.sign (
      SUBMIT
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
      task.sign (
        SUBMIT
      );
    }

  }

  ///
  /// Benchmark emitting a START sign.
  ///

  @Benchmark
  public void emit_start () {

    task.start ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_start_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) task.start ();
  }

  ///
  /// Benchmark emitting a SUBMIT sign.
  ///

  @Benchmark
  public void emit_submit () {

    task.submit ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_submit_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) task.submit ();
  }

  ///
  /// Benchmark emitting a SUSPEND sign.
  ///

  @Benchmark
  public void emit_suspend () {

    task.suspend ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_suspend_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) task.suspend ();
  }

  ///
  /// Benchmark emitting a TIMEOUT sign.
  ///

  @Benchmark
  public void emit_timeout () {

    task.timeout ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_timeout_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) task.timeout ();
  }

  @Setup ( Level.Iteration )
  public void setupIteration () {

    circuit =
      cortex.circuit ();

    conduit =
      circuit.conduit (
        Tasks::composer
      );

    task =
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
        TASK_NAME
      );

  }

  ///
  /// Benchmark task retrieval from conduit.
  ///

  @Benchmark
  public Task task_from_conduit () {

    return
      conduit.percept (
        name
      );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Task task_from_conduit_batch () {
    Task result = null;
    for ( var i = 0; i < BATCH_SIZE; i++ ) result = conduit.percept ( name );
    return result;
  }

  @TearDown ( Level.Iteration )
  public void tearDownIteration () {

    circuit.close ();

  }

}
