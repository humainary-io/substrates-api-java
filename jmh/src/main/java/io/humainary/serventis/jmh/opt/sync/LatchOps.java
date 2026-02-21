// Copyright (c) 2025 William David Louth

package io.humainary.serventis.jmh.opt.sync;

import io.humainary.substrates.api.Substrates;
import io.humainary.substrates.ext.serventis.opt.sync.Latches;
import io.humainary.substrates.ext.serventis.opt.sync.Latches.Latch;
import io.humainary.substrates.ext.serventis.opt.sync.Latches.Sign;
import org.openjdk.jmh.annotations.*;

import static io.humainary.substrates.ext.serventis.opt.sync.Latches.Sign.AWAIT;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for Latches.Latch operations.
///
/// Measures performance of latch creation and sign emissions for coordination
/// barriers: AWAIT, ARRIVE, RELEASE, TIMEOUT, RESET, and ABANDON.
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class LatchOps implements Substrates {

  private static final String LATCH_NAME = "startup.barrier";
  private static final int    BATCH_SIZE = 1000;

  private Cortex                  cortex;
  private Circuit                 circuit;
  private Conduit < Latch, Sign > conduit;
  private Latch                   latch;
  private Name                    name;

  ///
  /// Benchmark emitting an ABANDON sign.
  ///

  @Benchmark
  public void emit_abandon () {

    latch.abandon ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_abandon_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) latch.abandon ();
  }

  ///
  /// Benchmark emitting an ARRIVE sign.
  ///

  @Benchmark
  public void emit_arrive () {

    latch.arrive ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_arrive_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) latch.arrive ();
  }

  ///
  /// Benchmark emitting an AWAIT sign.
  ///

  @Benchmark
  public void emit_await () {

    latch.await ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_await_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) latch.await ();
  }

  ///
  /// Benchmark emitting a RELEASE sign.
  ///

  @Benchmark
  public void emit_release () {

    latch.release ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_release_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) latch.release ();
  }

  ///
  /// Benchmark emitting a RESET sign.
  ///

  @Benchmark
  public void emit_reset () {

    latch.reset ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_reset_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) latch.reset ();
  }

  ///
  /// Benchmark generic sign emission.
  ///

  @Benchmark
  public void emit_sign () {

    latch.sign (
      AWAIT
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
      latch.sign (
        AWAIT
      );
    }

  }

  ///
  /// Benchmark emitting a TIMEOUT sign.
  ///

  @Benchmark
  public void emit_timeout () {

    latch.timeout ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_timeout_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) latch.timeout ();
  }

  ///
  /// Benchmark latch retrieval from conduit.
  ///

  @Benchmark
  public Latch latch_from_conduit () {

    return
      conduit.percept (
        name
      );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Latch latch_from_conduit_batch () {
    Latch result = null;
    for ( var i = 0; i < BATCH_SIZE; i++ ) result = conduit.percept ( name );
    return result;
  }

  @Setup ( Level.Iteration )
  public void setupIteration () {

    circuit =
      cortex.circuit ();

    conduit =
      circuit.conduit (
        Latches::composer
      );

    latch =
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
        LATCH_NAME
      );

  }

  @TearDown ( Level.Iteration )
  public void tearDownIteration () {

    circuit.close ();

  }

}
