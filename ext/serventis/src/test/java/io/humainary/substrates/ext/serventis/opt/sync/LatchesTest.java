// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.opt.sync;

import io.humainary.substrates.ext.serventis.opt.sync.Latches.Latch;
import io.humainary.substrates.ext.serventis.opt.sync.Latches.Sign;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.humainary.substrates.api.Substrates.*;
import static io.humainary.substrates.ext.serventis.opt.sync.Latches.Sign.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


/// Tests for the [Latches] API.
///
/// @author William David Louth
/// @since 1.0

final class LatchesTest {

  private static final Cortex             CORTEX = cortex ();
  private static final Name               NAME   = CORTEX.name ( "startup.barrier" );
  private              Circuit            circuit;
  private              Reservoir < Sign > reservoir;
  private              Latch              latch;

  @BeforeEach
  void setup () {

    circuit =
      CORTEX.circuit ();

    final var conduit =
      circuit.conduit (
        Latches::composer
      );

    latch =
      conduit.percept (
        NAME
      );

    reservoir =
      conduit.reservoir ();

  }

  @Test
  void testAbandon () {

    latch.abandon ();

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
  void testArrive () {

    latch.arrive ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( ARRIVE, signs.getFirst () );

  }

  @Test
  void testAwait () {

    latch.await ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( AWAIT, signs.getFirst () );

  }

  @Test
  void testCountDownLatchPattern () {

    latch.await ();
    latch.await ();
    latch.arrive ();
    latch.arrive ();
    latch.arrive ();
    latch.release ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 6, signs.size () );
    assertEquals ( AWAIT, signs.get ( 0 ) );
    assertEquals ( AWAIT, signs.get ( 1 ) );
    assertEquals ( ARRIVE, signs.get ( 2 ) );
    assertEquals ( ARRIVE, signs.get ( 3 ) );
    assertEquals ( ARRIVE, signs.get ( 4 ) );
    assertEquals ( RELEASE, signs.get ( 5 ) );

  }

  @Test
  void testCyclicBarrierMultiplePhases () {

    // Phase 1
    latch.await ();
    latch.await ();
    latch.release ();
    latch.reset ();

    // Phase 2
    latch.await ();
    latch.await ();
    latch.release ();
    latch.reset ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 8, signs.size () );
    // Phase 1
    assertEquals ( AWAIT, signs.get ( 0 ) );
    assertEquals ( AWAIT, signs.get ( 1 ) );
    assertEquals ( RELEASE, signs.get ( 2 ) );
    assertEquals ( RESET, signs.get ( 3 ) );
    // Phase 2
    assertEquals ( AWAIT, signs.get ( 4 ) );
    assertEquals ( AWAIT, signs.get ( 5 ) );
    assertEquals ( RELEASE, signs.get ( 6 ) );
    assertEquals ( RESET, signs.get ( 7 ) );

  }

  @Test
  void testCyclicBarrierPattern () {

    latch.await ();
    latch.await ();
    latch.await ();
    latch.release ();
    latch.reset ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 5, signs.size () );
    assertEquals ( AWAIT, signs.get ( 0 ) );
    assertEquals ( AWAIT, signs.get ( 1 ) );
    assertEquals ( AWAIT, signs.get ( 2 ) );
    assertEquals ( RELEASE, signs.get ( 3 ) );
    assertEquals ( RESET, signs.get ( 4 ) );

  }

  @Test
  void testParticipantFailure () {

    latch.await ();
    latch.await ();
    latch.arrive ();
    latch.abandon ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 4, signs.size () );
    assertEquals ( AWAIT, signs.get ( 0 ) );
    assertEquals ( AWAIT, signs.get ( 1 ) );
    assertEquals ( ARRIVE, signs.get ( 2 ) );
    assertEquals ( ABANDON, signs.get ( 3 ) );

  }

  @Test
  void testRelease () {

    latch.release ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( RELEASE, signs.getFirst () );

  }

  @Test
  void testReset () {

    latch.reset ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( RESET, signs.getFirst () );

  }

  /// Tests that [Sign] enum ordinals remain stable for compatibility.
  ///
  /// This test ensures that the ordinal values of [Sign] enum constants
  /// do not change, which is critical for serialization and external integrations.

  @Test
  void testSignEnumOrdinals () {

    assertEquals ( 0, AWAIT.ordinal () );
    assertEquals ( 1, ARRIVE.ordinal () );
    assertEquals ( 2, RELEASE.ordinal () );
    assertEquals ( 3, TIMEOUT.ordinal () );
    assertEquals ( 4, RESET.ordinal () );
    assertEquals ( 5, ABANDON.ordinal () );

  }

  @Test
  void testSignMethod () {

    // Test direct sign() method
    latch.sign ( AWAIT );
    latch.sign ( ARRIVE );
    latch.sign ( RELEASE );

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 3, signs.size () );
    assertEquals ( AWAIT, signs.get ( 0 ) );
    assertEquals ( ARRIVE, signs.get ( 1 ) );
    assertEquals ( RELEASE, signs.get ( 2 ) );

  }

  @Test
  void testStartSignalPattern () {

    // Worker threads wait
    latch.await ();
    latch.await ();
    latch.await ();
    // Main thread signals start
    latch.arrive ();
    latch.release ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 5, signs.size () );
    assertEquals ( AWAIT, signs.get ( 0 ) );
    assertEquals ( AWAIT, signs.get ( 1 ) );
    assertEquals ( AWAIT, signs.get ( 2 ) );
    assertEquals ( ARRIVE, signs.get ( 3 ) );
    assertEquals ( RELEASE, signs.get ( 4 ) );

  }

  @Test
  void testSubjectAssociation () {

    latch.await ();

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
  void testTimeout () {

    latch.timeout ();

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
  void testTimeoutWaitPattern () {

    latch.await ();
    latch.timeout ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 2, signs.size () );
    assertEquals ( AWAIT, signs.get ( 0 ) );
    assertEquals ( TIMEOUT, signs.get ( 1 ) );

  }

}
