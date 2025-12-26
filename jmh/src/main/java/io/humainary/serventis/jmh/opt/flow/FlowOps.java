// Copyright (c) 2025 William David Louth

package io.humainary.serventis.jmh.opt.flow;

import io.humainary.substrates.api.Substrates;
import io.humainary.substrates.ext.serventis.opt.flow.Flows;
import io.humainary.substrates.ext.serventis.opt.flow.Flows.Signal;
import org.openjdk.jmh.annotations.*;

import static io.humainary.substrates.ext.serventis.opt.flow.Flows.Dimension.*;
import static io.humainary.substrates.ext.serventis.opt.flow.Flows.Sign.SUCCESS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for Flows.Flow operations.
///
/// Measures performance of flow creation and signal emissions for flow
/// stage transitions across INGRESS, TRANSIT, and EGRESS dimensions
/// with SUCCESS and FAIL outcomes.
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class FlowOps implements Substrates {

  private static final String FLOW_NAME  = "pipeline.orders";
  private static final int    BATCH_SIZE = 1000;

  private Cortex                         cortex;
  private Circuit                        circuit;
  private Conduit < Flows.Flow, Signal > conduit;
  private Flows.Flow                     flow;
  private Name                           name;

  ///
  /// Benchmark emitting a FAIL signal at EGRESS.
  ///

  @Benchmark
  public void emit_fail_egress () {

    flow.fail ( EGRESS );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_fail_egress_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) flow.fail ( EGRESS );
  }

  ///
  /// Benchmark emitting a FAIL signal at INGRESS.
  ///

  @Benchmark
  public void emit_fail_ingress () {

    flow.fail ( INGRESS );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_fail_ingress_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) flow.fail ( INGRESS );
  }

  ///
  /// Benchmark emitting a FAIL signal at TRANSIT.
  ///

  @Benchmark
  public void emit_fail_transit () {

    flow.fail ( TRANSIT );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_fail_transit_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) flow.fail ( TRANSIT );
  }

  ///
  /// Benchmark generic signal emission.
  ///

  @Benchmark
  public void emit_signal () {

    flow.signal (
      SUCCESS,
      INGRESS
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
      flow.signal (
        SUCCESS,
        INGRESS
      );
    }

  }

  ///
  /// Benchmark emitting a SUCCESS signal at EGRESS.
  ///

  @Benchmark
  public void emit_success_egress () {

    flow.success ( EGRESS );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_success_egress_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) flow.success ( EGRESS );
  }

  ///
  /// Benchmark emitting a SUCCESS signal at INGRESS.
  ///

  @Benchmark
  public void emit_success_ingress () {

    flow.success ( INGRESS );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_success_ingress_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) flow.success ( INGRESS );
  }

  ///
  /// Benchmark emitting a SUCCESS signal at TRANSIT.
  ///

  @Benchmark
  public void emit_success_transit () {

    flow.success ( TRANSIT );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_success_transit_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) flow.success ( TRANSIT );
  }

  ///
  /// Benchmark flow retrieval from conduit.
  ///

  @Benchmark
  public Flows.Flow flow_from_conduit () {

    return
      conduit.percept (
        name
      );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Flows.Flow flow_from_conduit_batch () {
    Flows.Flow result = null;
    for ( var i = 0; i < BATCH_SIZE; i++ ) result = conduit.percept ( name );
    return result;
  }

  @Setup ( Level.Iteration )
  public void setupIteration () {

    circuit =
      cortex.circuit ();

    conduit =
      circuit.conduit (
        Flows::composer
      );

    flow =
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
        FLOW_NAME
      );

  }

  @TearDown ( Level.Iteration )
  public void tearDownIteration () {

    circuit.close ();

  }

}
