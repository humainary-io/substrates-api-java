// Copyright (c) 2025 William David Louth

package io.humainary.serventis.jmh.opt.sync;

import io.humainary.substrates.api.Substrates;
import io.humainary.substrates.ext.serventis.opt.sync.Locks;
import io.humainary.substrates.ext.serventis.opt.sync.Locks.Lock;
import io.humainary.substrates.ext.serventis.opt.sync.Locks.Sign;
import org.openjdk.jmh.annotations.*;

import static io.humainary.substrates.ext.serventis.opt.sync.Locks.Sign.ATTEMPT;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for Locks.Lock operations.
///
/// Measures performance of lock creation and sign emissions for synchronization
/// primitives: ATTEMPT, ACQUIRE, GRANT, DENY, TIMEOUT, RELEASE, UPGRADE,
/// DOWNGRADE, CONTEST, and ABANDON.
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class LockOps implements Substrates {

  private static final String LOCK_NAME  = "cache.mutex";
  private static final int    BATCH_SIZE = 1000;

  private Cortex                 cortex;
  private Circuit                circuit;
  private Conduit < Lock, Sign > conduit;
  private Lock                   lock;
  private Name                   name;

  ///
  /// Benchmark emitting an ABANDON sign.
  ///

  @Benchmark
  public void emit_abandon () {

    lock.abandon ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_abandon_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) lock.abandon ();
  }

  ///
  /// Benchmark emitting an ACQUIRE sign.
  ///

  @Benchmark
  public void emit_acquire () {

    lock.acquire ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_acquire_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) lock.acquire ();
  }

  ///
  /// Benchmark emitting an ATTEMPT sign.
  ///

  @Benchmark
  public void emit_attempt () {

    lock.attempt ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_attempt_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) lock.attempt ();
  }

  ///
  /// Benchmark emitting a CONTEST sign.
  ///

  @Benchmark
  public void emit_contest () {

    lock.contest ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_contest_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) lock.contest ();
  }

  ///
  /// Benchmark emitting a DENY sign.
  ///

  @Benchmark
  public void emit_deny () {

    lock.deny ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_deny_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) lock.deny ();
  }

  ///
  /// Benchmark emitting a DOWNGRADE sign.
  ///

  @Benchmark
  public void emit_downgrade () {

    lock.downgrade ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_downgrade_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) lock.downgrade ();
  }

  ///
  /// Benchmark emitting a GRANT sign.
  ///

  @Benchmark
  public void emit_grant () {

    lock.grant ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_grant_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) lock.grant ();
  }

  ///
  /// Benchmark emitting a RELEASE sign.
  ///

  @Benchmark
  public void emit_release () {

    lock.release ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_release_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) lock.release ();
  }

  ///
  /// Benchmark generic sign emission.
  ///

  @Benchmark
  public void emit_sign () {

    lock.sign (
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
      lock.sign (
        ATTEMPT
      );
    }

  }

  ///
  /// Benchmark emitting a TIMEOUT sign.
  ///

  @Benchmark
  public void emit_timeout () {

    lock.timeout ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_timeout_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) lock.timeout ();
  }

  ///
  /// Benchmark emitting an UPGRADE sign.
  ///

  @Benchmark
  public void emit_upgrade () {

    lock.upgrade ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_upgrade_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) lock.upgrade ();
  }

  ///
  /// Benchmark lock retrieval from conduit.
  ///

  @Benchmark
  public Lock lock_from_conduit () {

    return
      conduit.percept (
        name
      );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Lock lock_from_conduit_batch () {
    Lock result = null;
    for ( var i = 0; i < BATCH_SIZE; i++ ) result = conduit.percept ( name );
    return result;
  }

  @Setup ( Level.Iteration )
  public void setupIteration () {

    circuit =
      cortex.circuit ();

    conduit =
      circuit.conduit (
        Locks::composer
      );

    lock =
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
        LOCK_NAME
      );

  }

  @TearDown ( Level.Iteration )
  public void tearDownIteration () {

    circuit.close ();

  }

}
