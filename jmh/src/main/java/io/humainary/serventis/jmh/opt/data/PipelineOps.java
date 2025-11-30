// Copyright (c) 2025 William David Louth

package io.humainary.serventis.jmh.opt.data;

import io.humainary.substrates.api.Substrates;
import io.humainary.substrates.ext.serventis.opt.data.Pipelines;
import io.humainary.substrates.ext.serventis.opt.data.Pipelines.Pipeline;
import io.humainary.substrates.ext.serventis.opt.data.Pipelines.Sign;
import org.openjdk.jmh.annotations.*;

import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for Pipelines.Pipeline operations.
///
/// Measures performance of pipeline creation and sign emissions for data pipeline
/// operations, including INPUT, OUTPUT, TRANSFORM, FILTER, AGGREGATE, BUFFER,
/// BACKPRESSURE, OVERFLOW, CHECKPOINT, WATERMARK, LAG, and SKIP.
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class PipelineOps implements Substrates {

  private static final String PIPELINE_NAME = "etl.customer.enrichment";
  private static final int    BATCH_SIZE    = 1000;

  private Cortex                     cortex;
  private Circuit                    circuit;
  private Conduit < Pipeline, Sign > conduit;
  private Pipeline                   pipeline;
  private Name                       name;

  ///
  /// Benchmark emitting an AGGREGATE sign.
  ///

  @Benchmark
  public void emit_aggregate () {

    pipeline.aggregate ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_aggregate_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) pipeline.aggregate ();
  }

  ///
  /// Benchmark emitting a BACKPRESSURE sign.
  ///

  @Benchmark
  public void emit_backpressure () {

    pipeline.backpressure ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_backpressure_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) pipeline.backpressure ();
  }

  ///
  /// Benchmark emitting a BUFFER sign.
  ///

  @Benchmark
  public void emit_buffer () {

    pipeline.buffer ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_buffer_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) pipeline.buffer ();
  }

  ///
  /// Benchmark emitting a CHECKPOINT sign.
  ///

  @Benchmark
  public void emit_checkpoint () {

    pipeline.checkpoint ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_checkpoint_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) pipeline.checkpoint ();
  }

  ///
  /// Benchmark emitting a FILTER sign.
  ///

  @Benchmark
  public void emit_filter () {

    pipeline.filter ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_filter_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) pipeline.filter ();
  }

  ///
  /// Benchmark emitting an INPUT sign.
  ///

  @Benchmark
  public void emit_input () {

    pipeline.input ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_input_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) pipeline.input ();
  }

  ///
  /// Benchmark emitting a LAG sign.
  ///

  @Benchmark
  public void emit_lag () {

    pipeline.lag ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_lag_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) pipeline.lag ();
  }

  ///
  /// Benchmark emitting an OUTPUT sign.
  ///

  @Benchmark
  public void emit_output () {

    pipeline.output ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_output_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) pipeline.output ();
  }

  ///
  /// Benchmark emitting an OVERFLOW sign.
  ///

  @Benchmark
  public void emit_overflow () {

    pipeline.overflow ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_overflow_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) pipeline.overflow ();
  }

  ///
  /// Benchmark generic sign emission.
  ///

  @Benchmark
  public void emit_sign () {

    pipeline.sign (
      Sign.TRANSFORM
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
      pipeline.sign (
        Sign.TRANSFORM
      );
    }

  }

  ///
  /// Benchmark emitting a SKIP sign.
  ///

  @Benchmark
  public void emit_skip () {

    pipeline.skip ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_skip_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) pipeline.skip ();
  }

  ///
  /// Benchmark emitting a TRANSFORM sign.
  ///

  @Benchmark
  public void emit_transform () {

    pipeline.transform ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_transform_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) pipeline.transform ();
  }

  ///
  /// Benchmark emitting a WATERMARK sign.
  ///

  @Benchmark
  public void emit_watermark () {

    pipeline.watermark ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_watermark_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) pipeline.watermark ();
  }

  ///
  /// Benchmark simulating typical ETL pipeline flow.
  ///

  @Benchmark
  public void pipeline_flow_etl () {

    // Typical ETL flow: input -> transform -> filter -> aggregate -> output
    pipeline.input ();
    pipeline.transform ();
    pipeline.filter ();
    pipeline.aggregate ();
    pipeline.output ();

  }

  ///
  /// Benchmark simulating stream processing with backpressure.
  ///

  @Benchmark
  public void pipeline_flow_stream () {

    // Stream processing flow: input -> buffer -> transform -> backpressure -> output
    pipeline.input ();
    pipeline.buffer ();
    pipeline.transform ();
    pipeline.backpressure ();
    pipeline.output ();

  }

  ///
  /// Benchmark simulating windowed aggregation with checkpoints.
  ///

  @Benchmark
  public void pipeline_flow_windowed () {

    // Windowed aggregation: input -> aggregate -> watermark -> checkpoint -> output
    pipeline.input ();
    pipeline.aggregate ();
    pipeline.watermark ();
    pipeline.checkpoint ();
    pipeline.output ();

  }

  ///
  /// Benchmark pipeline retrieval from conduit.
  ///

  @Benchmark
  public Pipeline pipeline_from_conduit () {

    return
      conduit.percept (
        name
      );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Pipeline pipeline_from_conduit_batch () {
    Pipeline result = null;
    for ( var i = 0; i < BATCH_SIZE; i++ ) result = conduit.percept ( name );
    return result;
  }

  @Setup ( Level.Iteration )
  public void setupIteration () {

    circuit =
      cortex.circuit ();

    conduit =
      circuit.conduit (
        Pipelines::composer
      );

    pipeline =
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
        PIPELINE_NAME
      );

  }

  @TearDown ( Level.Iteration )
  public void tearDownIteration () {

    circuit.close ();

  }

}