// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.opt.sync;

import io.humainary.substrates.ext.serventis.opt.sync.Locks.Lock;
import io.humainary.substrates.ext.serventis.opt.sync.Locks.Sign;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.humainary.substrates.api.Substrates.*;
import static io.humainary.substrates.ext.serventis.opt.sync.Locks.Sign.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


/// Tests for the [Locks] API.
///
/// @author William David Louth
/// @since 1.0

final class LocksTest {

  private static final Cortex             CORTEX = cortex ();
  private static final Name               NAME   = CORTEX.name ( "cache.mutex" );
  private              Circuit            circuit;
  private              Reservoir < Sign > reservoir;
  private              Lock               lock;

  @BeforeEach
  void setup () {

    circuit =
      CORTEX.circuit ();

    final var conduit =
      circuit.conduit (
        Locks::composer
      );

    lock =
      conduit.percept (
        NAME
      );

    reservoir =
      conduit.reservoir ();

  }

  @Test
  void testAbandon () {

    lock.abandon ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( ABANDON, signs.getFirst () );

  }

  @Test
  void testAbandonedLock () {

    lock.acquire ();
    lock.grant ();
    lock.abandon ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 3, signs.size () );
    assertEquals ( ACQUIRE, signs.get ( 0 ) );
    assertEquals ( GRANT, signs.get ( 1 ) );
    assertEquals ( ABANDON, signs.get ( 2 ) );

  }

  @Test
  void testAcquire () {

    lock.acquire ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( ACQUIRE, signs.getFirst () );

  }

  @Test
  void testAttempt () {

    lock.attempt ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( ATTEMPT, signs.getFirst () );

  }

  @Test
  void testBlockingLockSuccess () {

    lock.acquire ();
    lock.grant ();
    lock.release ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 3, signs.size () );
    assertEquals ( ACQUIRE, signs.get ( 0 ) );
    assertEquals ( GRANT, signs.get ( 1 ) );
    assertEquals ( RELEASE, signs.get ( 2 ) );

  }

  @Test
  void testBlockingLockTimeout () {

    lock.acquire ();
    lock.timeout ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 2, signs.size () );
    assertEquals ( ACQUIRE, signs.get ( 0 ) );
    assertEquals ( TIMEOUT, signs.get ( 1 ) );

  }

  @Test
  void testCASWithContention () {

    lock.attempt ();
    lock.contest ();
    lock.contest ();
    lock.grant ();
    lock.release ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 5, signs.size () );
    assertEquals ( ATTEMPT, signs.get ( 0 ) );
    assertEquals ( CONTEST, signs.get ( 1 ) );
    assertEquals ( CONTEST, signs.get ( 2 ) );
    assertEquals ( GRANT, signs.get ( 3 ) );
    assertEquals ( RELEASE, signs.get ( 4 ) );

  }

  @Test
  void testContest () {

    lock.contest ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( CONTEST, signs.getFirst () );

  }

  @Test
  void testDeny () {

    lock.deny ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( DENY, signs.getFirst () );

  }

  @Test
  void testDowngrade () {

    lock.downgrade ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( DOWNGRADE, signs.getFirst () );

  }

  @Test
  void testFailedTryLock () {

    lock.attempt ();
    lock.deny ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 2, signs.size () );
    assertEquals ( ATTEMPT, signs.get ( 0 ) );
    assertEquals ( DENY, signs.get ( 1 ) );

  }

  @Test
  void testGrant () {

    lock.grant ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( GRANT, signs.getFirst () );

  }

  @Test
  void testLockUpgradeDowngrade () {

    lock.attempt ();
    lock.grant ();
    lock.upgrade ();
    lock.downgrade ();
    lock.release ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 5, signs.size () );
    assertEquals ( ATTEMPT, signs.get ( 0 ) );
    assertEquals ( GRANT, signs.get ( 1 ) );
    assertEquals ( UPGRADE, signs.get ( 2 ) );
    assertEquals ( DOWNGRADE, signs.get ( 3 ) );
    assertEquals ( RELEASE, signs.get ( 4 ) );

  }

  @Test
  void testRelease () {

    lock.release ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( RELEASE, signs.getFirst () );

  }

  /// Tests that [Sign] enum ordinals remain stable for compatibility.
  ///
  /// This test ensures that the ordinal values of [Sign] enum constants
  /// do not change, which is critical for serialization and external integrations.

  @Test
  void testSignEnumOrdinals () {

    assertEquals ( 0, ATTEMPT.ordinal () );
    assertEquals ( 1, ACQUIRE.ordinal () );
    assertEquals ( 2, GRANT.ordinal () );
    assertEquals ( 3, DENY.ordinal () );
    assertEquals ( 4, TIMEOUT.ordinal () );
    assertEquals ( 5, RELEASE.ordinal () );
    assertEquals ( 6, UPGRADE.ordinal () );
    assertEquals ( 7, DOWNGRADE.ordinal () );
    assertEquals ( 8, CONTEST.ordinal () );
    assertEquals ( 9, ABANDON.ordinal () );

  }

  @Test
  void testSignMethod () {

    // Test direct sign() method
    lock.sign ( ATTEMPT );
    lock.sign ( GRANT );
    lock.sign ( RELEASE );

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 3, signs.size () );
    assertEquals ( ATTEMPT, signs.get ( 0 ) );
    assertEquals ( GRANT, signs.get ( 1 ) );
    assertEquals ( RELEASE, signs.get ( 2 ) );

  }

  @Test
  void testSubjectAssociation () {

    lock.attempt ();

    circuit.await ();

    assertEquals (
      NAME,
      reservoir
        .drain ()
        .map ( Capture::subject )
        .map ( Subject::name )
        .findFirst ()
        .orElseThrow ()
    );

  }

  @Test
  void testSuccessfulTryLock () {

    lock.attempt ();
    lock.grant ();
    lock.release ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 3, signs.size () );
    assertEquals ( ATTEMPT, signs.get ( 0 ) );
    assertEquals ( GRANT, signs.get ( 1 ) );
    assertEquals ( RELEASE, signs.get ( 2 ) );

  }

  @Test
  void testTimeout () {

    lock.timeout ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( TIMEOUT, signs.getFirst () );

  }

  @Test
  void testUpgrade () {

    lock.upgrade ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( UPGRADE, signs.getFirst () );

  }

}
