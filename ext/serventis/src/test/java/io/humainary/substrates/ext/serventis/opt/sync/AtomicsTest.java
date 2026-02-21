// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.opt.sync;

import io.humainary.substrates.ext.serventis.opt.sync.Atomics.Atomic;
import io.humainary.substrates.ext.serventis.opt.sync.Atomics.Sign;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.humainary.substrates.api.Substrates.*;
import static io.humainary.substrates.ext.serventis.opt.sync.Atomics.Sign.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


/// Tests for the [Atomics] API.
///
/// @author William David Louth
/// @since 1.0

final class AtomicsTest {

  private static final Cortex             CORTEX = cortex ();
  private static final Name               NAME   = CORTEX.name ( "queue.head.cas" );
  private              Circuit            circuit;
  private              Reservoir < Sign > reservoir;
  private              Atomic             atomic;

  @BeforeEach
  void setup () {

    circuit =
      CORTEX.circuit ();

    final var conduit =
      circuit.conduit (
        Atomics::composer
      );

    atomic =
      conduit.percept (
        NAME
      );

    reservoir =
      conduit.reservoir ();

  }

  @Test
  void testAttempt () {

    atomic.attempt ();

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
  void testBackoff () {

    atomic.backoff ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( BACKOFF, signs.getFirst () );

  }

  @Test
  void testCASWithAdaptiveBackoff () {

    atomic.attempt ();
    atomic.fail ();
    atomic.spin ();
    atomic.fail ();
    atomic.backoff ();
    atomic.fail ();
    atomic.backoff ();
    atomic.success ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 8, signs.size () );
    assertEquals ( ATTEMPT, signs.get ( 0 ) );
    assertEquals ( FAIL, signs.get ( 1 ) );
    assertEquals ( SPIN, signs.get ( 2 ) );
    assertEquals ( FAIL, signs.get ( 3 ) );
    assertEquals ( BACKOFF, signs.get ( 4 ) );
    assertEquals ( FAIL, signs.get ( 5 ) );
    assertEquals ( BACKOFF, signs.get ( 6 ) );
    assertEquals ( SUCCESS, signs.get ( 7 ) );

  }

  @Test
  void testCASWithSpinRetry () {

    atomic.attempt ();
    atomic.fail ();
    atomic.spin ();
    atomic.fail ();
    atomic.spin ();
    atomic.success ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 6, signs.size () );
    assertEquals ( ATTEMPT, signs.get ( 0 ) );
    assertEquals ( FAIL, signs.get ( 1 ) );
    assertEquals ( SPIN, signs.get ( 2 ) );
    assertEquals ( FAIL, signs.get ( 3 ) );
    assertEquals ( SPIN, signs.get ( 4 ) );
    assertEquals ( SUCCESS, signs.get ( 5 ) );

  }

  @Test
  void testCASWithYield () {

    atomic.attempt ();
    atomic.fail ();
    atomic.spin ();
    atomic.fail ();
    atomic.yield ();
    atomic.fail ();
    atomic.success ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 7, signs.size () );
    assertEquals ( ATTEMPT, signs.get ( 0 ) );
    assertEquals ( FAIL, signs.get ( 1 ) );
    assertEquals ( SPIN, signs.get ( 2 ) );
    assertEquals ( FAIL, signs.get ( 3 ) );
    assertEquals ( YIELD, signs.get ( 4 ) );
    assertEquals ( FAIL, signs.get ( 5 ) );
    assertEquals ( SUCCESS, signs.get ( 6 ) );

  }

  @Test
  void testExhaust () {

    atomic.exhaust ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( EXHAUST, signs.getFirst () );

  }

  @Test
  void testFail () {

    atomic.fail ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( FAIL, signs.getFirst () );

  }

  @Test
  void testPark () {

    atomic.park ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( PARK, signs.getFirst () );

  }

  @Test
  void testRetryExhaustion () {

    atomic.attempt ();
    atomic.fail ();
    atomic.spin ();
    atomic.fail ();
    atomic.backoff ();
    atomic.fail ();
    atomic.exhaust ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 7, signs.size () );
    assertEquals ( ATTEMPT, signs.get ( 0 ) );
    assertEquals ( FAIL, signs.get ( 1 ) );
    assertEquals ( SPIN, signs.get ( 2 ) );
    assertEquals ( FAIL, signs.get ( 3 ) );
    assertEquals ( BACKOFF, signs.get ( 4 ) );
    assertEquals ( FAIL, signs.get ( 5 ) );
    assertEquals ( EXHAUST, signs.get ( 6 ) );

  }

  /// Tests that [Sign] enum ordinals remain stable for compatibility.
  ///
  /// This test ensures that the ordinal values of [Sign] enum constants
  /// do not change, which is critical for serialization and external integrations.

  @Test
  void testSignEnumOrdinals () {

    assertEquals ( 0, ATTEMPT.ordinal () );
    assertEquals ( 1, SUCCESS.ordinal () );
    assertEquals ( 2, FAIL.ordinal () );
    assertEquals ( 3, SPIN.ordinal () );
    assertEquals ( 4, YIELD.ordinal () );
    assertEquals ( 5, BACKOFF.ordinal () );
    assertEquals ( 6, PARK.ordinal () );
    assertEquals ( 7, EXHAUST.ordinal () );

  }

  @Test
  void testSignMethod () {

    // Test direct sign() method
    atomic.sign ( ATTEMPT );
    atomic.sign ( SUCCESS );

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 2, signs.size () );
    assertEquals ( ATTEMPT, signs.get ( 0 ) );
    assertEquals ( SUCCESS, signs.get ( 1 ) );

  }

  @Test
  void testSimpleCASSuccess () {

    atomic.attempt ();
    atomic.success ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 2, signs.size () );
    assertEquals ( ATTEMPT, signs.get ( 0 ) );
    assertEquals ( SUCCESS, signs.get ( 1 ) );

  }

  @Test
  void testSpin () {

    atomic.spin ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( SPIN, signs.getFirst () );

  }

  @Test
  void testSpinToParkEscalation () {

    atomic.attempt ();
    atomic.fail ();
    atomic.spin ();
    atomic.fail ();
    atomic.spin ();
    atomic.fail ();
    atomic.park ();
    atomic.success ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 8, signs.size () );
    assertEquals ( ATTEMPT, signs.get ( 0 ) );
    assertEquals ( FAIL, signs.get ( 1 ) );
    assertEquals ( SPIN, signs.get ( 2 ) );
    assertEquals ( FAIL, signs.get ( 3 ) );
    assertEquals ( SPIN, signs.get ( 4 ) );
    assertEquals ( FAIL, signs.get ( 5 ) );
    assertEquals ( PARK, signs.get ( 6 ) );
    assertEquals ( SUCCESS, signs.get ( 7 ) );

  }

  @Test
  void testSubjectAssociation () {

    atomic.attempt ();

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
  void testSuccess () {

    atomic.success ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( SUCCESS, signs.getFirst () );

  }

  @Test
  void testYield () {

    atomic.yield ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( YIELD, signs.getFirst () );

  }

}
