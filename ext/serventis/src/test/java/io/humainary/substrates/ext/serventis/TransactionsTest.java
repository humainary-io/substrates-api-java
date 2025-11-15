// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis;

import io.humainary.substrates.ext.serventis.ext.Transactions;
import io.humainary.substrates.ext.serventis.ext.Transactions.Dimension;
import io.humainary.substrates.ext.serventis.ext.Transactions.Sign;
import io.humainary.substrates.ext.serventis.ext.Transactions.Signal;
import io.humainary.substrates.ext.serventis.ext.Transactions.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.humainary.substrates.api.Substrates.*;
import static io.humainary.substrates.ext.serventis.ext.Transactions.Dimension.COORDINATOR;
import static io.humainary.substrates.ext.serventis.ext.Transactions.Dimension.PARTICIPANT;
import static org.junit.jupiter.api.Assertions.assertEquals;


/// Tests for the [Transactions] API.
///
/// @author William David Louth
/// @since 1.0

final class TransactionsTest {

  private static final Cortex               CORTEX = cortex ();
  private static final Name                 NAME   = CORTEX.name ( "db.transaction" );
  private              Circuit              circuit;
  private              Reservoir < Signal > reservoir;
  private              Transaction          transaction;

  @BeforeEach
  void setup () {

    circuit =
      CORTEX.circuit ();

    final var conduit =
      circuit.conduit (
        Transactions::composer
      );

    transaction =
      conduit.percept (
        NAME
      );

    reservoir =
      CORTEX.reservoir (
        conduit
      );

  }

  // ========== COORDINATOR SIGNAL TESTS ==========

  @Test
  void testAbortCoordinator () {

    transaction.abort ( COORDINATOR );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signals.size () );
    assertEquals ( Sign.ABORT, signals.getFirst ().sign () );
    assertEquals ( COORDINATOR, signals.getFirst ().dimension () );

  }

  @Test
  void testAbortParticipant () {

    transaction.abort ( PARTICIPANT );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signals.size () );
    assertEquals ( Sign.ABORT, signals.getFirst ().sign () );
    assertEquals ( PARTICIPANT, signals.getFirst ().dimension () );

  }

  @Test
  void testCommitCoordinator () {

    transaction.commit ( COORDINATOR );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signals.size () );
    assertEquals ( Sign.COMMIT, signals.getFirst ().sign () );
    assertEquals ( COORDINATOR, signals.getFirst ().dimension () );

  }

  @Test
  void testCommitParticipant () {

    transaction.commit ( PARTICIPANT );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signals.size () );
    assertEquals ( Sign.COMMIT, signals.getFirst ().sign () );
    assertEquals ( PARTICIPANT, signals.getFirst ().dimension () );

  }

  @Test
  void testCompensateCoordinator () {

    transaction.compensate ( COORDINATOR );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signals.size () );
    assertEquals ( Sign.COMPENSATE, signals.getFirst ().sign () );
    assertEquals ( COORDINATOR, signals.getFirst ().dimension () );

  }

  @Test
  void testCompensateParticipant () {

    transaction.compensate ( PARTICIPANT );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signals.size () );
    assertEquals ( Sign.COMPENSATE, signals.getFirst ().sign () );
    assertEquals ( PARTICIPANT, signals.getFirst ().dimension () );

  }

  @Test
  void testConflictCoordinator () {

    transaction.conflict ( COORDINATOR );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signals.size () );
    assertEquals ( Sign.CONFLICT, signals.getFirst ().sign () );
    assertEquals ( COORDINATOR, signals.getFirst ().dimension () );

  }

  @Test
  void testConflictParticipant () {

    transaction.conflict ( PARTICIPANT );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signals.size () );
    assertEquals ( Sign.CONFLICT, signals.getFirst ().sign () );
    assertEquals ( PARTICIPANT, signals.getFirst ().dimension () );

  }

  @Test
  void testDimensionEnumValues () {

    final var dimensions = Dimension.values ();

    assertEquals ( 2, dimensions.length );
    assertEquals ( COORDINATOR, dimensions[0] );
    assertEquals ( PARTICIPANT, dimensions[1] );

  }

  @Test
  void testDistributedTransactionMultipleParticipants () {

    // Coordinator perspective with multiple participants
    final var coordinatorName = CORTEX.name ( "coordinator" );
    final var participant1Name = CORTEX.name ( "participant.1" );
    final var participant2Name = CORTEX.name ( "participant.2" );

    final var conduit = circuit.conduit ( Transactions::composer );
    final var coordinator = conduit.percept ( coordinatorName );
    final var participant1 = conduit.percept ( participant1Name );
    final var participant2 = conduit.percept ( participant2Name );
    final var signalReservoir = CORTEX.reservoir ( conduit );

    // Coordinator starts transaction
    coordinator.start ( COORDINATOR );
    participant1.start ( PARTICIPANT );
    participant2.start ( PARTICIPANT );

    // Coordinator sends prepare
    coordinator.prepare ( COORDINATOR );
    participant1.prepare ( PARTICIPANT );    // P1 votes yes
    participant2.prepare ( PARTICIPANT );    // P2 votes yes

    // Coordinator commits
    coordinator.commit ( COORDINATOR );
    participant1.commit ( PARTICIPANT );
    participant2.commit ( PARTICIPANT );

    circuit.await ();

    final var allSignals = signalReservoir.drain ().toList ();

    assertEquals ( 9, allSignals.size () );

    // Verify coordinator signals
    final var coordinatorSignals =
      allSignals
        .stream ()
        .filter ( c -> c.subject ().name ().equals ( coordinatorName ) )
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 3, coordinatorSignals.size () );
    assertEquals ( Sign.START, coordinatorSignals.get ( 0 ).sign () );
    assertEquals ( COORDINATOR, coordinatorSignals.get ( 0 ).dimension () );
    assertEquals ( Sign.PREPARE, coordinatorSignals.get ( 1 ).sign () );
    assertEquals ( Sign.COMMIT, coordinatorSignals.get ( 2 ).sign () );

    // Verify participant 1 signals
    final var p1Signals =
      allSignals
        .stream ()
        .filter ( c -> c.subject ().name ().equals ( participant1Name ) )
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 3, p1Signals.size () );
    assertEquals ( Sign.START, p1Signals.get ( 0 ).sign () );
    assertEquals ( PARTICIPANT, p1Signals.get ( 0 ).dimension () );
    assertEquals ( Sign.PREPARE, p1Signals.get ( 1 ).sign () );
    assertEquals ( Sign.COMMIT, p1Signals.get ( 2 ).sign () );

    // Verify participant 2 signals
    final var p2Signals =
      allSignals
        .stream ()
        .filter ( c -> c.subject ().name ().equals ( participant2Name ) )
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 3, p2Signals.size () );
    assertEquals ( Sign.START, p2Signals.get ( 0 ).sign () );
    assertEquals ( PARTICIPANT, p2Signals.get ( 0 ).dimension () );
    assertEquals ( Sign.PREPARE, p2Signals.get ( 1 ).sign () );
    assertEquals ( Sign.COMMIT, p2Signals.get ( 2 ).sign () );

  }

  // ========== ENUM TESTS ==========

  @Test
  void testDistributedTransactionPartialFailure () {

    // Coordinator with one participant voting no
    final var coordinatorName = CORTEX.name ( "coordinator" );
    final var participant1Name = CORTEX.name ( "participant.1" );
    final var participant2Name = CORTEX.name ( "participant.2" );

    final var conduit = circuit.conduit ( Transactions::composer );
    final var coordinator = conduit.percept ( coordinatorName );
    final var participant1 = conduit.percept ( participant1Name );
    final var participant2 = conduit.percept ( participant2Name );
    final var signalReservoir = CORTEX.reservoir ( conduit );

    // Coordinator starts transaction
    coordinator.start ( COORDINATOR );
    participant1.start ( PARTICIPANT );
    participant2.start ( PARTICIPANT );

    // Coordinator sends prepare
    coordinator.prepare ( COORDINATOR );
    participant1.prepare ( PARTICIPANT );      // P1 votes yes
    participant2.conflict ( PARTICIPANT );     // P2 has conflict, votes no

    // Coordinator rolls back
    coordinator.rollback ( COORDINATOR );
    participant1.rollback ( PARTICIPANT );
    participant2.rollback ( PARTICIPANT );

    circuit.await ();

    final var allSignals = signalReservoir.drain ().toList ();

    assertEquals ( 9, allSignals.size () );

    // Verify rollback was issued
    final var coordinatorSignals =
      allSignals
        .stream ()
        .filter ( c -> c.subject ().name ().equals ( coordinatorName ) )
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 3, coordinatorSignals.size () );
    assertEquals ( Sign.ROLLBACK, coordinatorSignals.get ( 2 ).sign () );

    // Verify P2 conflicted
    final var p2Signals =
      allSignals
        .stream ()
        .filter ( c -> c.subject ().name ().equals ( participant2Name ) )
        .map ( Capture::emission )
        .toList ();

    assertEquals ( Sign.CONFLICT, p2Signals.get ( 1 ).sign () );

  }

  @Test
  void testExpireCoordinator () {

    transaction.expire ( COORDINATOR );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signals.size () );
    assertEquals ( Sign.EXPIRE, signals.getFirst ().sign () );
    assertEquals ( COORDINATOR, signals.getFirst ().dimension () );

  }

  @Test
  void testExpireParticipant () {

    transaction.expire ( PARTICIPANT );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signals.size () );
    assertEquals ( Sign.EXPIRE, signals.getFirst ().sign () );
    assertEquals ( PARTICIPANT, signals.getFirst ().dimension () );

  }

  @Test
  void testMultipleTransactionOperations () {

    // Multiple operations in sequence
    transaction.start ( COORDINATOR );
    transaction.prepare ( COORDINATOR );
    transaction.commit ( COORDINATOR );
    transaction.start ( COORDINATOR );
    transaction.rollback ( COORDINATOR );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 5, signals.size () );
    assertEquals ( Sign.START, signals.get ( 0 ).sign () );
    assertEquals ( Sign.PREPARE, signals.get ( 1 ).sign () );
    assertEquals ( Sign.COMMIT, signals.get ( 2 ).sign () );
    assertEquals ( Sign.START, signals.get ( 3 ).sign () );
    assertEquals ( Sign.ROLLBACK, signals.get ( 4 ).sign () );

  }

  @Test
  void testPrepareCoordinator () {

    transaction.prepare ( COORDINATOR );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signals.size () );
    assertEquals ( Sign.PREPARE, signals.getFirst ().sign () );
    assertEquals ( COORDINATOR, signals.getFirst ().dimension () );

  }

  @Test
  void testPrepareParticipant () {

    transaction.prepare ( PARTICIPANT );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signals.size () );
    assertEquals ( Sign.PREPARE, signals.getFirst ().sign () );
    assertEquals ( PARTICIPANT, signals.getFirst ().dimension () );

  }

  @Test
  void testRollbackCoordinator () {

    transaction.rollback ( COORDINATOR );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signals.size () );
    assertEquals ( Sign.ROLLBACK, signals.getFirst ().sign () );
    assertEquals ( COORDINATOR, signals.getFirst ().dimension () );

  }

  @Test
  void testRollbackParticipant () {

    transaction.rollback ( PARTICIPANT );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signals.size () );
    assertEquals ( Sign.ROLLBACK, signals.getFirst ().sign () );
    assertEquals ( PARTICIPANT, signals.getFirst ().dimension () );

  }

  @Test
  void testSagaPattern () {

    // Saga with compensation
    transaction.start ( COORDINATOR );
    transaction.compensate ( COORDINATOR );
    transaction.rollback ( COORDINATOR );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 3, signals.size () );
    assertEquals ( Sign.START, signals.get ( 0 ).sign () );
    assertEquals ( Sign.COMPENSATE, signals.get ( 1 ).sign () );
    assertEquals ( Sign.ROLLBACK, signals.get ( 2 ).sign () );

  }

  @Test
  void testSignEnumValues () {

    final var signs = Sign.values ();

    assertEquals ( 8, signs.length );
    assertEquals ( Sign.START, signs[0] );
    assertEquals ( Sign.PREPARE, signs[1] );
    assertEquals ( Sign.COMMIT, signs[2] );
    assertEquals ( Sign.ROLLBACK, signs[3] );
    assertEquals ( Sign.ABORT, signs[4] );
    assertEquals ( Sign.EXPIRE, signs[5] );
    assertEquals ( Sign.CONFLICT, signs[6] );
    assertEquals ( Sign.COMPENSATE, signs[7] );

  }

  // ========== TRANSACTION LIFECYCLE PATTERN TESTS ==========

  @Test
  void testSignal () {

    // Test direct signal() method for all sign and dimension combinations
    transaction.signal ( Sign.START, COORDINATOR );
    transaction.signal ( Sign.START, PARTICIPANT );
    transaction.signal ( Sign.PREPARE, COORDINATOR );
    transaction.signal ( Sign.PREPARE, PARTICIPANT );
    transaction.signal ( Sign.COMMIT, COORDINATOR );
    transaction.signal ( Sign.COMMIT, PARTICIPANT );
    transaction.signal ( Sign.ROLLBACK, COORDINATOR );
    transaction.signal ( Sign.ROLLBACK, PARTICIPANT );
    transaction.signal ( Sign.ABORT, COORDINATOR );
    transaction.signal ( Sign.ABORT, PARTICIPANT );
    transaction.signal ( Sign.EXPIRE, COORDINATOR );
    transaction.signal ( Sign.EXPIRE, PARTICIPANT );
    transaction.signal ( Sign.CONFLICT, COORDINATOR );
    transaction.signal ( Sign.CONFLICT, PARTICIPANT );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 14, signals.size () );
    assertEquals ( new Signal ( Sign.START, COORDINATOR ), signals.get ( 0 ) );
    assertEquals ( new Signal ( Sign.START, PARTICIPANT ), signals.get ( 1 ) );
    assertEquals ( new Signal ( Sign.PREPARE, COORDINATOR ), signals.get ( 2 ) );
    assertEquals ( new Signal ( Sign.PREPARE, PARTICIPANT ), signals.get ( 3 ) );
    assertEquals ( new Signal ( Sign.COMMIT, COORDINATOR ), signals.get ( 4 ) );
    assertEquals ( new Signal ( Sign.COMMIT, PARTICIPANT ), signals.get ( 5 ) );
    assertEquals ( new Signal ( Sign.ROLLBACK, COORDINATOR ), signals.get ( 6 ) );
    assertEquals ( new Signal ( Sign.ROLLBACK, PARTICIPANT ), signals.get ( 7 ) );
    assertEquals ( new Signal ( Sign.ABORT, COORDINATOR ), signals.get ( 8 ) );
    assertEquals ( new Signal ( Sign.ABORT, PARTICIPANT ), signals.get ( 9 ) );
    assertEquals ( new Signal ( Sign.EXPIRE, COORDINATOR ), signals.get ( 10 ) );
    assertEquals ( new Signal ( Sign.EXPIRE, PARTICIPANT ), signals.get ( 11 ) );
    assertEquals ( new Signal ( Sign.CONFLICT, COORDINATOR ), signals.get ( 12 ) );
    assertEquals ( new Signal ( Sign.CONFLICT, PARTICIPANT ), signals.get ( 13 ) );

  }

  @Test
  void testSignalRecord () {

    // Test that Signal is a proper record with sign and dimension
    final var signal = new Signal ( Sign.COMMIT, COORDINATOR );

    assertEquals ( Sign.COMMIT, signal.sign () );
    assertEquals ( COORDINATOR, signal.dimension () );

  }

  @Test
  void testStartCoordinator () {

    transaction.start ( COORDINATOR );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signals.size () );
    assertEquals ( Sign.START, signals.getFirst ().sign () );
    assertEquals ( COORDINATOR, signals.getFirst ().dimension () );

  }

  @Test
  void testStartParticipant () {

    transaction.start ( PARTICIPANT );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signals.size () );
    assertEquals ( Sign.START, signals.getFirst ().sign () );
    assertEquals ( PARTICIPANT, signals.getFirst ().dimension () );

  }

  @Test
  void testSubjectAttachment () {

    transaction.start ( COORDINATOR );

    circuit.await ();

    final var capture =
      reservoir
        .drain ()
        .findFirst ()
        .orElseThrow ();

    assertEquals ( NAME, capture.subject ().name () );
    assertEquals ( Sign.START, capture.emission ().sign () );

  }

  @Test
  void testTransactionAbort () {

    // Explicit abort (e.g., deadlock detected)
    transaction.start ( COORDINATOR );
    transaction.prepare ( COORDINATOR );
    transaction.abort ( COORDINATOR );
    transaction.rollback ( COORDINATOR );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 4, signals.size () );
    assertEquals ( Sign.START, signals.get ( 0 ).sign () );
    assertEquals ( Sign.PREPARE, signals.get ( 1 ).sign () );
    assertEquals ( Sign.ABORT, signals.get ( 2 ).sign () );
    assertEquals ( Sign.ROLLBACK, signals.get ( 3 ).sign () );

  }

  @Test
  void testTransactionExpire () {

    // Transaction with expiration
    transaction.start ( COORDINATOR );
    transaction.prepare ( COORDINATOR );
    transaction.expire ( COORDINATOR );
    transaction.rollback ( COORDINATOR );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 4, signals.size () );
    assertEquals ( Sign.START, signals.get ( 0 ).sign () );
    assertEquals ( Sign.PREPARE, signals.get ( 1 ).sign () );
    assertEquals ( Sign.EXPIRE, signals.get ( 2 ).sign () );
    assertEquals ( Sign.ROLLBACK, signals.get ( 3 ).sign () );

  }

  @Test
  void testTwoPhaseCommitAbort () {

    // 2PC abort flow (participant votes no via conflict)
    transaction.start ( COORDINATOR );
    transaction.prepare ( COORDINATOR );
    transaction.conflict ( PARTICIPANT );
    transaction.rollback ( COORDINATOR );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 4, signals.size () );
    assertEquals ( Sign.START, signals.get ( 0 ).sign () );
    assertEquals ( Sign.PREPARE, signals.get ( 1 ).sign () );
    assertEquals ( Sign.CONFLICT, signals.get ( 2 ).sign () );
    assertEquals ( PARTICIPANT, signals.get ( 2 ).dimension () );
    assertEquals ( Sign.ROLLBACK, signals.get ( 3 ).sign () );

  }

  @Test
  void testTwoPhaseCommitSuccess () {

    // Standard 2PC success flow from coordinator perspective
    transaction.start ( COORDINATOR );
    transaction.prepare ( COORDINATOR );
    transaction.commit ( COORDINATOR );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 3, signals.size () );
    assertEquals ( Sign.START, signals.get ( 0 ).sign () );
    assertEquals ( COORDINATOR, signals.get ( 0 ).dimension () );
    assertEquals ( Sign.PREPARE, signals.get ( 1 ).sign () );
    assertEquals ( COORDINATOR, signals.get ( 1 ).dimension () );
    assertEquals ( Sign.COMMIT, signals.get ( 2 ).sign () );
    assertEquals ( COORDINATOR, signals.get ( 2 ).dimension () );

  }

}
