// Copyright (c) 2025 William David Louth

package io.humainary.serventis.jmh.opt.sync;

import io.humainary.substrates.api.Substrates;
import io.humainary.substrates.ext.serventis.opt.sync.Atomics;
import io.humainary.substrates.ext.serventis.opt.sync.Atomics.Atomic;
import io.humainary.substrates.ext.serventis.opt.sync.Atomics.Sign;
import org.openjdk.jmh.annotations.*;

import static io.humainary.substrates.ext.serventis.opt.sync.Atomics.Sign.ATTEMPT;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for Atomics.Atomic operations.
///
/// Measures performance of atomic instrument creation and sign emissions for CAS
/// contention dynamics: ATTEMPT, SUCCESS, FAIL, SPIN, YIELD, BACKOFF, PARK, and EXHAUST.
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class AtomicOps implements Substrates {

  private static final String ATOMIC_NAME = "queue.head.cas";
  private static final int    BATCH_SIZE  = 1000;

  private Cortex                   cortex;
  private Circuit                  circuit;
  private Conduit < Atomic, Sign > conduit;
  private Atomic                   atomic;
  private Name                     name;

  ///
  /// Benchmark emitting an ATTEMPT sign.
  ///

  @Benchmark
  public void emit_attempt () {

    atomic.attempt ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_attempt_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) atomic.attempt ();
  }

  ///
  /// Benchmark emitting a BACKOFF sign.
  ///

  @Benchmark
  public void emit_backoff () {

    atomic.backoff ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_backoff_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) atomic.backoff ();
  }

  ///
  /// Benchmark emitting an EXHAUST sign.
  ///

  @Benchmark
  public void emit_exhaust () {

    atomic.exhaust ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_exhaust_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) atomic.exhaust ();
  }

  ///
  /// Benchmark emitting a FAIL sign.
  ///

  @Benchmark
  public void emit_fail () {

    atomic.fail ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_fail_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) atomic.fail ();
  }

  ///
  /// Benchmark emitting a PARK sign.
  ///

  @Benchmark
  public void emit_park () {

    atomic.park ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_park_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) atomic.park ();
  }

  ///
  /// Benchmark generic sign emission.
  ///

  @Benchmark
  public void emit_sign () {

    atomic.sign (
      ATTEMPT
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
      atomic.sign (
        ATTEMPT
      );
    }

  }

  ///
  /// Benchmark emitting a SPIN sign.
  ///

  @Benchmark
  public void emit_spin () {

    atomic.spin ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_spin_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) atomic.spin ();
  }

  ///
  /// Benchmark emitting a SUCCESS sign.
  ///

  @Benchmark
  public void emit_success () {

    atomic.success ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_success_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) atomic.success ();
  }

  ///
  /// Benchmark emitting a YIELD sign.
  ///

  @Benchmark
  public void emit_yield () {

    atomic.yield ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_yield_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) atomic.yield ();
  }

  ///
  /// Benchmark atomic retrieval from conduit.
  ///

  @Benchmark
  public Atomic atomic_from_conduit () {

    return
      conduit.percept (
        name
      );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Atomic atomic_from_conduit_batch () {
    Atomic result = null;
    for ( var i = 0; i < BATCH_SIZE; i++ ) result = conduit.percept ( name );
    return result;
  }

  @Setup ( Level.Iteration )
  public void setupIteration () {

    circuit =
      cortex.circuit ();

    conduit =
      circuit.conduit (
        Atomics::composer
      );

    atomic =
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
        ATOMIC_NAME
      );

  }

  @TearDown ( Level.Iteration )
  public void tearDownIteration () {

    circuit.close ();

  }

}
