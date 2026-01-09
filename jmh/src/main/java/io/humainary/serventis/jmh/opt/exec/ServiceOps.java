// Copyright (c) 2025 William David Louth

package io.humainary.serventis.jmh.opt.exec;

import io.humainary.substrates.api.Substrates;
import io.humainary.substrates.ext.serventis.opt.exec.Services;
import io.humainary.substrates.ext.serventis.opt.exec.Services.Service;
import io.humainary.substrates.ext.serventis.opt.exec.Services.Signal;
import org.openjdk.jmh.annotations.*;

import static io.humainary.substrates.ext.serventis.opt.exec.Services.Dimension.CALLEE;
import static io.humainary.substrates.ext.serventis.opt.exec.Services.Dimension.CALLER;
import static io.humainary.substrates.ext.serventis.opt.exec.Services.Sign.START;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for Services.Service operations.
/// <p>
/// Measures performance of service creation and signal emissions for service
/// lifecycle operations from both CALLER (self-perspective) and CALLEE
/// (observed-perspective) dimensions, including START, STOP, CALL, SUCCESS,
/// FAIL, RETRY, DELAY, SCHEDULE, SUSPEND, RESUME, REJECT, DISCARD, and more.
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class ServiceOps implements Substrates {

  private static final String SERVICE_NAME = "order.processor";
  private static final int    BATCH_SIZE   = 1000;

  private Cortex                      cortex;
  private Circuit                     circuit;
  private Conduit < Service, Signal > conduit;
  private Service                     service;
  private Name                        name;

  ///
  /// Benchmark emitting a CALL signal (CALLER).
  ///

  @Benchmark
  public void emit_call () {

    service.call ( CALLER );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_call_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.call ( CALLER );
  }

  ///
  /// Benchmark emitting a CALLED signal (CALLEE).
  ///

  @Benchmark
  public void emit_called () {

    service.call ( CALLEE );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_called_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.call ( CALLEE );
  }

  ///
  /// Benchmark emitting a DELAY signal (CALLER).
  ///

  @Benchmark
  public void emit_delay () {

    service.delay ( CALLER );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_delay_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.delay ( CALLER );
  }

  ///
  /// Benchmark emitting a DELAYED signal (CALLEE).
  ///

  @Benchmark
  public void emit_delayed () {

    service.delay ( CALLEE );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_delayed_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.delay ( CALLEE );
  }

  ///
  /// Benchmark emitting a DISCARD signal (CALLER).
  ///

  @Benchmark
  public void emit_discard () {

    service.discard ( CALLER );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_discard_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.discard ( CALLER );
  }

  ///
  /// Benchmark emitting a DISCARDED signal (CALLEE).
  ///

  @Benchmark
  public void emit_discarded () {

    service.discard ( CALLEE );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_discarded_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.discard ( CALLEE );
  }

  ///
  /// Benchmark emitting a DISCONNECT signal (CALLER).
  ///

  @Benchmark
  public void emit_disconnect () {

    service.disconnect ( CALLER );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_disconnect_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.disconnect ( CALLER );
  }

  ///
  /// Benchmark emitting a DISCONNECTED signal (CALLEE).
  ///

  @Benchmark
  public void emit_disconnected () {

    service.disconnect ( CALLEE );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_disconnected_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.disconnect ( CALLEE );
  }

  ///
  /// Benchmark emitting an EXPIRE signal (CALLER).
  ///

  @Benchmark
  public void emit_expire () {

    service.expire ( CALLER );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_expire_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.expire ( CALLER );
  }

  ///
  /// Benchmark emitting an EXPIRED signal (CALLEE).
  ///

  @Benchmark
  public void emit_expired () {

    service.expire ( CALLEE );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_expired_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.expire ( CALLEE );
  }

  ///
  /// Benchmark emitting a FAIL signal (CALLER).
  ///

  @Benchmark
  public void emit_fail () {

    service.fail ( CALLER );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_fail_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.fail ( CALLER );
  }

  ///
  /// Benchmark emitting a FAILED signal (CALLEE).
  ///

  @Benchmark
  public void emit_failed () {

    service.fail ( CALLEE );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_failed_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.fail ( CALLEE );
  }

  ///
  /// Benchmark emitting a RECOURSE signal (CALLER).
  ///

  @Benchmark
  public void emit_recourse () {

    service.recourse ( CALLER );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_recourse_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.recourse ( CALLER );
  }

  ///
  /// Benchmark emitting a RECOURSED signal (CALLEE).
  ///

  @Benchmark
  public void emit_recoursed () {

    service.recourse ( CALLEE );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_recoursed_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.recourse ( CALLEE );
  }

  ///
  /// Benchmark emitting a REDIRECT signal (CALLER).
  ///

  @Benchmark
  public void emit_redirect () {

    service.redirect ( CALLER );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_redirect_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.redirect ( CALLER );
  }

  ///
  /// Benchmark emitting a REDIRECTED signal (CALLEE).
  ///

  @Benchmark
  public void emit_redirected () {

    service.redirect ( CALLEE );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_redirected_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.redirect ( CALLEE );
  }

  ///
  /// Benchmark emitting a REJECT signal (CALLER).
  ///

  @Benchmark
  public void emit_reject () {

    service.reject ( CALLER );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_reject_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.reject ( CALLER );
  }

  ///
  /// Benchmark emitting a REJECTED signal (CALLEE).
  ///

  @Benchmark
  public void emit_rejected () {

    service.reject ( CALLEE );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_rejected_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.reject ( CALLEE );
  }

  ///
  /// Benchmark emitting a RESUME signal (CALLER).
  ///

  @Benchmark
  public void emit_resume () {

    service.resume ( CALLER );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_resume_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.resume ( CALLER );
  }

  ///
  /// Benchmark emitting a RESUMED signal (CALLEE).
  ///

  @Benchmark
  public void emit_resumed () {

    service.resume ( CALLEE );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_resumed_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.resume ( CALLEE );
  }

  ///
  /// Benchmark emitting a RETRIED signal (CALLEE).
  ///

  @Benchmark
  public void emit_retried () {

    service.retry ( CALLEE );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_retried_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.retry ( CALLEE );
  }

  ///
  /// Benchmark emitting a RETRY signal (CALLER).
  ///

  @Benchmark
  public void emit_retry () {

    service.retry ( CALLER );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_retry_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.retry ( CALLER );
  }

  ///
  /// Benchmark emitting a SCHEDULE signal (CALLER).
  ///

  @Benchmark
  public void emit_schedule () {

    service.schedule ( CALLER );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_schedule_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.schedule ( CALLER );
  }

  ///
  /// Benchmark emitting a SCHEDULED signal (CALLEE).
  ///

  @Benchmark
  public void emit_scheduled () {

    service.schedule ( CALLEE );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_scheduled_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.schedule ( CALLEE );
  }

  ///
  /// Benchmark generic signal emission.
  ///

  @Benchmark
  public void emit_signal () {

    service.signal (
      START,
      CALLER
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
      service.signal (
        START,
        CALLER
      );
    }

  }

  ///
  /// Benchmark emitting a START signal (CALLER).
  ///

  @Benchmark
  public void emit_start () {

    service.start ( CALLER );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_start_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.start ( CALLER );
  }

  ///
  /// Benchmark emitting a STARTED signal (CALLEE).
  ///

  @Benchmark
  public void emit_started () {

    service.start ( CALLEE );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_started_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.start ( CALLEE );
  }

  ///
  /// Benchmark emitting a STOP signal (CALLER).
  ///

  @Benchmark
  public void emit_stop () {

    service.stop ( CALLER );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_stop_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.stop ( CALLER );
  }

  ///
  /// Benchmark emitting a STOPPED signal (CALLEE).
  ///

  @Benchmark
  public void emit_stopped () {

    service.stop ( CALLEE );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_stopped_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.stop ( CALLEE );
  }

  ///
  /// Benchmark emitting a SUCCEEDED signal (CALLEE).
  ///

  @Benchmark
  public void emit_succeeded () {

    service.success ( CALLEE );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_succeeded_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.success ( CALLEE );
  }

  ///
  /// Benchmark emitting a SUCCESS signal (CALLER).
  ///

  @Benchmark
  public void emit_success () {

    service.success ( CALLER );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_success_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.success ( CALLER );
  }

  ///
  /// Benchmark emitting a SUSPEND signal (CALLER).
  ///

  @Benchmark
  public void emit_suspend () {

    service.suspend ( CALLER );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_suspend_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.suspend ( CALLER );
  }

  ///
  /// Benchmark emitting a SUSPENDED signal (CALLEE).
  ///

  @Benchmark
  public void emit_suspended () {

    service.suspend ( CALLEE );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_suspended_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) service.suspend ( CALLEE );
  }

  ///
  /// Benchmark service retrieval from conduit.
  ///

  @Benchmark
  public Service service_from_conduit () {

    return
      conduit.percept (
        name
      );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Service service_from_conduit_batch () {
    Service result = null;
    for ( var i = 0; i < BATCH_SIZE; i++ ) result = conduit.percept ( name );
    return result;
  }

  @Setup ( Level.Iteration )
  public void setupIteration () {

    circuit =
      cortex.circuit ();

    conduit =
      circuit.conduit (
        Services::composer
      );

    service =
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
        SERVICE_NAME
      );

  }

  @TearDown ( Level.Iteration )
  public void tearDownIteration () {

    circuit.close ();

  }

}
