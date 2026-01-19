// Copyright (c) 2025 William David Louth

package io.humainary.serventis.jmh.opt.tool;

import io.humainary.substrates.api.Substrates;
import io.humainary.substrates.ext.serventis.opt.tool.Probes;
import io.humainary.substrates.ext.serventis.opt.tool.Probes.Probe;
import io.humainary.substrates.ext.serventis.opt.tool.Probes.Signal;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

import static io.humainary.substrates.ext.serventis.opt.tool.Probes.Dimension.INBOUND;
import static io.humainary.substrates.ext.serventis.opt.tool.Probes.Dimension.OUTBOUND;
import static io.humainary.substrates.ext.serventis.opt.tool.Probes.Sign.CONNECT;

///
/// Benchmark for Probes.Probe operations.
///
/// Measures performance of probe creation and signal emissions for communication
/// operations from both OUTBOUND (self-perspective) and INBOUND (observed-perspective)
/// dimensions: CONNECT, DISCONNECT, TRANSMIT, RECEIVE, PROCESS, SUCCEED, and FAIL.
///

@State ( Scope.Benchmark )
@BenchmarkMode ( Mode.AverageTime )
@OutputTimeUnit ( TimeUnit.NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class ProbeOps implements Substrates {

  private static final String PROBE_NAME = "rpc.client";
  private static final int    BATCH_SIZE = 1000;

  private Cortex                    cortex;
  private Circuit                   circuit;
  private Conduit < Probe, Signal > conduit;
  private Probe                     probe;
  private Name                      name;

  ///
  /// Benchmark emitting a CONNECT signal (OUTBOUND).
  ///

  @Benchmark
  public void emit_connect () {

    probe.connect ( OUTBOUND );

  }


  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_connect_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) probe.connect ( OUTBOUND );
  }

  ///
  /// Benchmark emitting a CONNECTED signal (INBOUND).
  ///

  @Benchmark
  public void emit_connected () {

    probe.connect ( INBOUND );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_connected_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) probe.connect ( INBOUND );
  }

  ///
  /// Benchmark emitting a DISCONNECT signal (OUTBOUND).
  ///

  @Benchmark
  public void emit_disconnect () {

    probe.disconnect ( OUTBOUND );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_disconnect_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) probe.disconnect ( OUTBOUND );
  }

  ///
  /// Benchmark emitting a DISCONNECTED signal (INBOUND).
  ///

  @Benchmark
  public void emit_disconnected () {

    probe.disconnect ( INBOUND );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_disconnected_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) probe.disconnect ( INBOUND );
  }

  ///
  /// Benchmark emitting a FAIL signal (OUTBOUND).
  ///

  @Benchmark
  public void emit_fail () {

    probe.fail ( OUTBOUND );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_fail_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) probe.fail ( OUTBOUND );
  }

  ///
  /// Benchmark emitting a FAILED signal (INBOUND).
  ///

  @Benchmark
  public void emit_failed () {

    probe.fail ( INBOUND );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_failed_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) probe.fail ( INBOUND );
  }

  ///
  /// Benchmark emitting a PROCESS signal (OUTBOUND).
  ///

  @Benchmark
  public void emit_process () {

    probe.process ( OUTBOUND );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_process_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) probe.process ( OUTBOUND );
  }

  ///
  /// Benchmark emitting a PROCESSED signal (INBOUND).
  ///

  @Benchmark
  public void emit_processed () {

    probe.process ( INBOUND );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_processed_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) probe.process ( INBOUND );
  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_receive_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) probe.transfer ( OUTBOUND );
  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_received_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) probe.transfer ( INBOUND );
  }

  ///
  /// Benchmark generic signal emission.
  ///

  @Benchmark
  public void emit_signal () {

    probe.signal (
      CONNECT,
      OUTBOUND
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
      probe.signal (
        CONNECT,
        OUTBOUND
      );

    }

  }

  ///
  /// Benchmark emitting a SUCCEED signal (OUTBOUND).
  ///

  @Benchmark
  public void emit_succeed () {

    probe.succeed ( OUTBOUND );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_succeed_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) probe.succeed ( OUTBOUND );
  }

  ///
  /// Benchmark emitting a SUCCEEDED signal (INBOUND).
  ///

  @Benchmark
  public void emit_succeeded () {

    probe.succeed ( INBOUND );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_succeeded_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) probe.succeed ( INBOUND );
  }

  ///
  /// Benchmark emitting a TRANSFER signal (OUTBOUND).
  ///

  @Benchmark
  public void emit_transfer () {

    probe.transfer ( OUTBOUND );

  }

  ///
  /// Benchmark emitting a TRANSFER signal (INBOUND).
  ///

  @Benchmark
  public void emit_transfer_inbound () {

    probe.transfer ( INBOUND );

  }

  ///
  /// Benchmark emitting a TRANSFER signal (OUTBOUND).
  ///

  @Benchmark
  public void emit_transfer_outbound () {

    probe.transfer ( OUTBOUND );

  }

  ///
  /// Benchmark emitting a TRANSFER signal (INBOUND).
  ///

  @Benchmark
  public void emit_transferred () {

    probe.transfer ( INBOUND );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_transmit_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) probe.transfer ( OUTBOUND );
  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_transmitted_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) probe.transfer ( INBOUND );
  }

  ///
  /// Benchmark probe retrieval from conduit.
  ///

  @Benchmark
  public Probe probe_from_conduit () {

    return
      conduit.percept (
        name
      );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Probe probe_from_conduit_batch () {
    Probe result = null;
    for ( var i = 0; i < BATCH_SIZE; i++ ) result = conduit.percept ( name );
    return result;
  }

  @Setup ( Level.Iteration )
  public void setupIteration () {

    circuit =
      cortex.circuit ();

    conduit =
      circuit.conduit (
        Probes::composer
      );

    probe =
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
        PROBE_NAME
      );

  }

  @TearDown ( Level.Iteration )
  public void tearDownIteration () {

    circuit.close ();

  }

}
