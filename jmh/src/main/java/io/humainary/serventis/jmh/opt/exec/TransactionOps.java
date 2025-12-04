// Copyright (c) 2025 William David Louth

package io.humainary.serventis.jmh.opt.exec;

import io.humainary.substrates.api.Substrates;
import io.humainary.substrates.ext.serventis.opt.exec.Transactions;
import io.humainary.substrates.ext.serventis.opt.exec.Transactions.Signal;
import io.humainary.substrates.ext.serventis.opt.exec.Transactions.Transaction;
import org.openjdk.jmh.annotations.*;

import static io.humainary.substrates.ext.serventis.opt.exec.Transactions.Dimension.COORDINATOR;
import static io.humainary.substrates.ext.serventis.opt.exec.Transactions.Dimension.PARTICIPANT;
import static io.humainary.substrates.ext.serventis.opt.exec.Transactions.Sign.START;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for Transactions.Transaction operations.
///
/// Measures performance of transaction creation and signal emissions for distributed
/// transaction coordination from both COORDINATOR (transaction manager) and PARTICIPANT
/// (client) perspectives, including START, PREPARE, COMMIT, ROLLBACK, ABORT, EXPIRE,
/// CONFLICT, and COMPENSATE.
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class TransactionOps
  implements Substrates {

  private static final String TRANSACTION_NAME = "db.transaction";
  private static final int    BATCH_SIZE       = 1000;

  private Cortex                          cortex;
  private Circuit                         circuit;
  private Conduit < Transaction, Signal > conduit;
  private Transaction                     transaction;
  private Name                            name;

  ///
  /// Benchmark emitting an ABORT signal (COORDINATOR).
  ///

  @Benchmark
  public void emit_abort_coordinator () {

    transaction.abort (
      COORDINATOR
    );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_abort_coordinator_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) transaction.abort ( COORDINATOR );
  }

  ///
  /// Benchmark emitting an ABORT signal (PARTICIPANT).
  ///

  @Benchmark
  public void emit_abort_participant () {

    transaction.abort (
      PARTICIPANT
    );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_abort_participant_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) transaction.abort ( PARTICIPANT );
  }

  ///
  /// Benchmark emitting a COMMIT signal (COORDINATOR).
  ///

  @Benchmark
  public void emit_commit_coordinator () {

    transaction.commit (
      COORDINATOR
    );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_commit_coordinator_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) transaction.commit ( COORDINATOR );
  }

  ///
  /// Benchmark emitting a COMMIT signal (PARTICIPANT).
  ///

  @Benchmark
  public void emit_commit_participant () {

    transaction.commit (
      PARTICIPANT
    );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_commit_participant_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) transaction.commit ( PARTICIPANT );
  }

  ///
  /// Benchmark emitting a COMPENSATE signal (COORDINATOR).
  ///

  @Benchmark
  public void emit_compensate_coordinator () {

    transaction.compensate (
      COORDINATOR
    );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_compensate_coordinator_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) transaction.compensate ( COORDINATOR );
  }

  ///
  /// Benchmark emitting a COMPENSATE signal (PARTICIPANT).
  ///

  @Benchmark
  public void emit_compensate_participant () {

    transaction.compensate (
      PARTICIPANT
    );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_compensate_participant_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) transaction.compensate ( PARTICIPANT );
  }

  ///
  /// Benchmark emitting a CONFLICT signal (COORDINATOR).
  ///

  @Benchmark
  public void emit_conflict_coordinator () {

    transaction.conflict (
      COORDINATOR
    );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_conflict_coordinator_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) transaction.conflict ( COORDINATOR );
  }

  ///
  /// Benchmark emitting a CONFLICT signal (PARTICIPANT).
  ///

  @Benchmark
  public void emit_conflict_participant () {

    transaction.conflict (
      PARTICIPANT
    );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_conflict_participant_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) transaction.conflict ( PARTICIPANT );
  }

  ///
  /// Benchmark emitting an EXPIRE signal (COORDINATOR).
  ///

  @Benchmark
  public void emit_expire_coordinator () {

    transaction.expire (
      COORDINATOR
    );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_expire_coordinator_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) transaction.expire ( COORDINATOR );
  }

  ///
  /// Benchmark emitting an EXPIRE signal (PARTICIPANT).
  ///

  @Benchmark
  public void emit_expire_participant () {

    transaction.expire (
      PARTICIPANT
    );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_expire_participant_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) transaction.expire ( PARTICIPANT );
  }

  ///
  /// Benchmark emitting a PREPARE signal (COORDINATOR).
  ///

  @Benchmark
  public void emit_prepare_coordinator () {

    transaction.prepare (
      COORDINATOR
    );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_prepare_coordinator_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) transaction.prepare ( COORDINATOR );
  }

  ///
  /// Benchmark emitting a PREPARE signal (PARTICIPANT).
  ///

  @Benchmark
  public void emit_prepare_participant () {

    transaction.prepare (
      PARTICIPANT
    );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_prepare_participant_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) transaction.prepare ( PARTICIPANT );
  }

  ///
  /// Benchmark emitting a ROLLBACK signal (COORDINATOR).
  ///

  @Benchmark
  public void emit_rollback_coordinator () {

    transaction.rollback (
      COORDINATOR
    );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_rollback_coordinator_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) transaction.rollback ( COORDINATOR );
  }

  ///
  /// Benchmark emitting a ROLLBACK signal (PARTICIPANT).
  ///

  @Benchmark
  public void emit_rollback_participant () {

    transaction.rollback (
      PARTICIPANT
    );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_rollback_participant_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) transaction.rollback ( PARTICIPANT );
  }

  ///
  /// Benchmark generic signal emission.
  ///

  @Benchmark
  public void emit_signal () {

    transaction.signal (
      START,
      COORDINATOR
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
      transaction.signal (
        START,
        COORDINATOR
      );
    }

  }

  ///
  /// Benchmark emitting a START signal (COORDINATOR).
  ///

  @Benchmark
  public void emit_start_coordinator () {

    transaction.start (
      COORDINATOR
    );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_start_coordinator_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) transaction.start ( COORDINATOR );
  }

  ///
  /// Benchmark emitting a START signal (PARTICIPANT).
  ///

  @Benchmark
  public void emit_start_participant () {

    transaction.start (
      PARTICIPANT
    );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_start_participant_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) transaction.start ( PARTICIPANT );
  }

  @Setup ( Level.Iteration )
  public void setupIteration () {

    circuit =
      cortex.circuit ();

    conduit =
      circuit.conduit (
        Transactions::composer
      );

    transaction =
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
        TRANSACTION_NAME
      );

  }

  @TearDown ( Level.Iteration )
  public void tearDownIteration () {

    circuit.close ();

  }

  ///
  /// Benchmark transaction retrieval from conduit.
  ///

  @Benchmark
  public Transaction transaction_from_conduit () {

    return
      conduit.percept (
        name
      );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Transaction transaction_from_conduit_batch () {
    Transaction result = null;
    for ( var i = 0; i < BATCH_SIZE; i++ ) result = conduit.percept ( name );
    return result;
  }

}
