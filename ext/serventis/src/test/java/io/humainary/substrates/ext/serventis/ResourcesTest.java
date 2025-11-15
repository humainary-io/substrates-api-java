// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis;

import io.humainary.substrates.ext.serventis.ext.Resources;
import io.humainary.substrates.ext.serventis.ext.Resources.Resource;
import io.humainary.substrates.ext.serventis.ext.Resources.Sign;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.humainary.substrates.api.Substrates.*;
import static io.humainary.substrates.ext.serventis.ext.Resources.Sign.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


/// Tests for the [Resources] API.
///
/// @author William David Louth
/// @since 1.0

final class ResourcesTest {

  private static final Cortex             CORTEX = cortex ();
  private static final Name               NAME   = CORTEX.name ( "db.connections" );
  private              Circuit            circuit;
  private              Reservoir < Sign > reservoir;
  private              Resource           resource;

  @BeforeEach
  void setup () {

    circuit =
      CORTEX.circuit ();

    final var conduit =
      circuit.conduit (
        Resources::composer
      );

    resource =
      conduit.percept (
        NAME
      );

    reservoir =
      CORTEX.reservoir (
        conduit
      );

  }

  @Test
  void testAcquire () {

    resource.acquire ();

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

    resource.attempt ();

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
  void testBlockingAcquisitionSuccess () {

    resource.acquire ();
    resource.grant ();
    resource.release ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 3, signs.size () );
    assertEquals ( ACQUIRE, signs.getFirst () );
    assertEquals ( GRANT, signs.get ( 1 ) );
    assertEquals ( RELEASE, signs.get ( 2 ) );

  }

  @Test
  void testBlockingAcquisitionTimeout () {

    resource.acquire ();
    resource.timeout ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 2, signs.size () );
    assertEquals ( ACQUIRE, signs.getFirst () );
    assertEquals ( TIMEOUT, signs.get ( 1 ) );

  }

  @Test
  void testContentionPattern () {

    resource.attempt ();
    resource.deny ();
    resource.attempt ();
    resource.deny ();
    resource.attempt ();
    resource.grant ();
    resource.release ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 7, signs.size () );
    assertEquals ( ATTEMPT, signs.getFirst () );
    assertEquals ( DENY, signs.get ( 1 ) );
    assertEquals ( ATTEMPT, signs.get ( 2 ) );
    assertEquals ( DENY, signs.get ( 3 ) );
    assertEquals ( ATTEMPT, signs.get ( 4 ) );
    assertEquals ( GRANT, signs.get ( 5 ) );
    assertEquals ( RELEASE, signs.get ( 6 ) );

  }

  @Test
  void testDeny () {

    resource.deny ();

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
  void testGrant () {

    resource.grant ();

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
  void testMultipleAcquisitions () {

    resource.attempt ();
    resource.grant ();
    resource.attempt ();
    resource.grant ();
    resource.release ();
    resource.release ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 6, signs.size () );
    assertEquals ( ATTEMPT, signs.getFirst () );
    assertEquals ( GRANT, signs.get ( 1 ) );
    assertEquals ( ATTEMPT, signs.get ( 2 ) );
    assertEquals ( GRANT, signs.get ( 3 ) );
    assertEquals ( RELEASE, signs.get ( 4 ) );
    assertEquals ( RELEASE, signs.get ( 5 ) );

  }

  @Test
  void testNonBlockingAcquisitionFailure () {

    resource.attempt ();
    resource.deny ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 2, signs.size () );
    assertEquals ( ATTEMPT, signs.getFirst () );
    assertEquals ( DENY, signs.get ( 1 ) );

  }

  @Test
  void testNonBlockingAcquisitionSuccess () {

    resource.attempt ();
    resource.grant ();
    resource.release ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 3, signs.size () );
    assertEquals ( ATTEMPT, signs.getFirst () );
    assertEquals ( GRANT, signs.get ( 1 ) );
    assertEquals ( RELEASE, signs.get ( 2 ) );

  }

  @Test
  void testRelease () {

    resource.release ();

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
  void testSign () {

    // Test direct sign() method for all sign values
    resource.sign ( ATTEMPT );
    resource.sign ( ACQUIRE );
    resource.sign ( GRANT );
    resource.sign ( DENY );
    resource.sign ( TIMEOUT );
    resource.sign ( RELEASE );

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 6, signs.size () );
    assertEquals ( ATTEMPT, signs.get ( 0 ) );
    assertEquals ( ACQUIRE, signs.get ( 1 ) );
    assertEquals ( GRANT, signs.get ( 2 ) );
    assertEquals ( DENY, signs.get ( 3 ) );
    assertEquals ( TIMEOUT, signs.get ( 4 ) );
    assertEquals ( RELEASE, signs.get ( 5 ) );

  }

  @Test
  void testSignEnumOrdinals () {

    // Ensure ordinals remain stable for compatibility
    assertEquals ( 0, ATTEMPT.ordinal () );
    assertEquals ( 1, ACQUIRE.ordinal () );
    assertEquals ( 2, GRANT.ordinal () );
    assertEquals ( 3, DENY.ordinal () );
    assertEquals ( 4, TIMEOUT.ordinal () );
    assertEquals ( 5, RELEASE.ordinal () );

  }

  @Test
  void testSignEnumValues () {

    final var values = Sign.values ();

    assertEquals ( 6, values.length );
    assertEquals ( ATTEMPT, values[0] );
    assertEquals ( ACQUIRE, values[1] );
    assertEquals ( GRANT, values[2] );
    assertEquals ( DENY, values[3] );
    assertEquals ( TIMEOUT, values[4] );
    assertEquals ( RELEASE, values[5] );

  }

  @Test
  void testSubjectAttachment () {

    resource.attempt ();

    circuit.await ();

    final var capture =
      reservoir
        .drain ()
        .findFirst ()
        .orElseThrow ();

    assertEquals ( NAME, capture.subject ().name () );
    assertEquals ( ATTEMPT, capture.emission () );

  }

  @Test
  void testTimeout () {

    resource.timeout ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( TIMEOUT, signs.getFirst () );

  }

}
