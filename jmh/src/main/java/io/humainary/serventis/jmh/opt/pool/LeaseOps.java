// Copyright (c) 2025 William David Louth

package io.humainary.serventis.jmh.opt.pool;

import io.humainary.substrates.api.Substrates;
import io.humainary.substrates.ext.serventis.opt.pool.Leases;
import io.humainary.substrates.ext.serventis.opt.pool.Leases.Lease;
import io.humainary.substrates.ext.serventis.opt.pool.Leases.Signal;
import org.openjdk.jmh.annotations.*;

import static io.humainary.substrates.ext.serventis.opt.pool.Leases.Dimension.LESSEE;
import static io.humainary.substrates.ext.serventis.opt.pool.Leases.Dimension.LESSOR;
import static io.humainary.substrates.ext.serventis.opt.pool.Leases.Sign.ACQUIRE;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for Leases.Lease operations.
/// <p>
/// Measures performance of lease creation and signal emissions for lease
/// lifecycle operations from both LESSOR (authority-perspective) and LESSEE
/// (client-perspective) dimensions, including ACQUIRE, GRANT, DENY, RENEW,
/// EXTEND, RELEASE, EXPIRE, REVOKE, and PROBE.
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class LeaseOps implements Substrates {

  private static final String LEASE_NAME = "leadership";
  private static final int    BATCH_SIZE = 1000;

  private Cortex                    cortex;
  private Circuit                   circuit;
  private Conduit < Lease, Signal > conduit;
  private Lease                     lease;
  private Name                      name;

  ///
  /// Benchmark emitting an ACQUIRE signal (LESSEE).
  ///

  @Benchmark
  public void emit_acquire () {

    lease.acquire ( LESSEE );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_acquire_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) lease.acquire ( LESSEE );
  }

  ///
  /// Benchmark emitting an ACQUIRED signal (LESSOR).
  ///

  @Benchmark
  public void emit_acquired () {

    lease.acquire ( LESSOR );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_acquired_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) lease.acquire ( LESSOR );
  }

  ///
  /// Benchmark emitting a DENIED signal (LESSEE).
  ///

  @Benchmark
  public void emit_denied () {

    lease.deny ( LESSEE );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_denied_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) lease.deny ( LESSEE );
  }

  ///
  /// Benchmark emitting a DENY signal (LESSOR).
  ///

  @Benchmark
  public void emit_deny () {

    lease.deny ( LESSOR );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_deny_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) lease.deny ( LESSOR );
  }

  ///
  /// Benchmark emitting an EXPIRE signal (LESSOR).
  ///

  @Benchmark
  public void emit_expire () {

    lease.expire ( LESSOR );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_expire_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) lease.expire ( LESSOR );
  }

  ///
  /// Benchmark emitting an EXPIRED signal (LESSEE).
  ///

  @Benchmark
  public void emit_expired () {

    lease.expire ( LESSEE );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_expired_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) lease.expire ( LESSEE );
  }

  ///
  /// Benchmark emitting an EXTEND signal (LESSOR).
  ///

  @Benchmark
  public void emit_extend () {

    lease.extend ( LESSOR );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_extend_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) lease.extend ( LESSOR );
  }

  ///
  /// Benchmark emitting an EXTENDED signal (LESSEE).
  ///

  @Benchmark
  public void emit_extended () {

    lease.extend ( LESSEE );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_extended_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) lease.extend ( LESSEE );
  }

  ///
  /// Benchmark emitting a GRANT signal (LESSOR).
  ///

  @Benchmark
  public void emit_grant () {

    lease.grant ( LESSOR );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_grant_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) lease.grant ( LESSOR );
  }

  ///
  /// Benchmark emitting a GRANTED signal (LESSEE).
  ///

  @Benchmark
  public void emit_granted () {

    lease.grant ( LESSEE );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_granted_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) lease.grant ( LESSEE );
  }

  ///
  /// Benchmark emitting a PROBE signal (LESSOR).
  ///

  @Benchmark
  public void emit_probe () {

    lease.probe ( LESSOR );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_probe_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) lease.probe ( LESSOR );
  }

  ///
  /// Benchmark emitting a PROBED signal (LESSEE).
  ///

  @Benchmark
  public void emit_probed () {

    lease.probe ( LESSEE );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_probed_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) lease.probe ( LESSEE );
  }

  ///
  /// Benchmark emitting a RELEASE signal (LESSEE).
  ///

  @Benchmark
  public void emit_release () {

    lease.release ( LESSEE );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_release_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) lease.release ( LESSEE );
  }

  ///
  /// Benchmark emitting a RELEASED signal (LESSOR).
  ///

  @Benchmark
  public void emit_released () {

    lease.release ( LESSOR );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_released_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) lease.release ( LESSOR );
  }

  ///
  /// Benchmark emitting a RENEW signal (LESSEE).
  ///

  @Benchmark
  public void emit_renew () {

    lease.renew ( LESSEE );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_renew_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) lease.renew ( LESSEE );
  }

  ///
  /// Benchmark emitting a RENEWED signal (LESSOR).
  ///

  @Benchmark
  public void emit_renewed () {

    lease.renew ( LESSOR );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_renewed_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) lease.renew ( LESSOR );
  }

  ///
  /// Benchmark emitting a REVOKE signal (LESSOR).
  ///

  @Benchmark
  public void emit_revoke () {

    lease.revoke ( LESSOR );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_revoke_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) lease.revoke ( LESSOR );
  }

  ///
  /// Benchmark emitting a REVOKED signal (LESSEE).
  ///

  @Benchmark
  public void emit_revoked () {

    lease.revoke ( LESSEE );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_revoked_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) lease.revoke ( LESSEE );
  }

  ///
  /// Benchmark generic signal emission.
  ///

  @Benchmark
  public void emit_signal () {

    lease.signal (
      ACQUIRE,
      LESSEE
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
      lease.signal (
        ACQUIRE,
        LESSEE
      );

    }

  }

  ///
  /// Benchmark lease retrieval from conduit.
  ///

  @Benchmark
  public Lease lease_from_conduit () {

    return
      conduit.percept (
        name
      );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Lease lease_from_conduit_batch () {
    Lease result = null;
    for ( var i = 0; i < BATCH_SIZE; i++ ) result = conduit.percept ( name );
    return result;
  }

  @Setup ( Level.Iteration )
  public void setupIteration () {

    circuit =
      cortex.circuit ();

    conduit =
      circuit.conduit (
        Leases::composer
      );

    lease =
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
        LEASE_NAME
      );

  }

  @TearDown ( Level.Iteration )
  public void tearDownIteration () {

    circuit.close ();

  }

}
