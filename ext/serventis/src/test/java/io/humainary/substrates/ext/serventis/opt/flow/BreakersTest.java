// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.opt.flow;

import io.humainary.substrates.ext.serventis.opt.flow.Breakers.Breaker;
import io.humainary.substrates.ext.serventis.opt.flow.Breakers.Sign;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.humainary.substrates.api.Substrates.*;
import static io.humainary.substrates.ext.serventis.opt.flow.Breakers.Sign.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


/// Tests for the [Breakers] API.
///
/// @author William David Louth
/// @since 1.0

final class BreakersTest {

  private static final Cortex             CORTEX = cortex ();
  private static final Name               NAME   = CORTEX.name ( "api.breaker" );
  private              Circuit            circuit;
  private              Reservoir < Sign > reservoir;
  private              Breaker            breaker;

  @BeforeEach
  void setup () {

    circuit =
      CORTEX.circuit ();

    final var conduit =
      circuit.conduit (
        Breakers::composer
      );

    breaker =
      conduit.percept (
        NAME
      );

    reservoir =
      conduit.reservoir ();

  }

  @Test
  void testClose () {

    breaker.close ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( CLOSE, signs.getFirst () );

  }

  @Test
  void testHalfOpen () {

    breaker.halfOpen ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( HALF_OPEN, signs.getFirst () );

  }

  @Test
  void testOpen () {

    breaker.open ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( OPEN, signs.getFirst () );

  }

  @Test
  void testProbe () {

    breaker.probe ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( PROBE, signs.getFirst () );

  }

  @Test
  void testReset () {

    breaker.reset ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( RESET, signs.getFirst () );

  }

  @Test
  void testSign () {

    // Test direct sign() method for all sign values
    breaker.sign ( CLOSE );
    breaker.sign ( OPEN );
    breaker.sign ( HALF_OPEN );
    breaker.sign ( TRIP );
    breaker.sign ( PROBE );
    breaker.sign ( RESET );

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 6, signs.size () );
    assertEquals ( CLOSE, signs.get ( 0 ) );
    assertEquals ( OPEN, signs.get ( 1 ) );
    assertEquals ( HALF_OPEN, signs.get ( 2 ) );
    assertEquals ( TRIP, signs.get ( 3 ) );
    assertEquals ( PROBE, signs.get ( 4 ) );
    assertEquals ( RESET, signs.get ( 5 ) );

  }

  @Test
  void testSignEnumOrdinals () {

    // Ensure ordinals remain stable for compatibility
    assertEquals ( 0, CLOSE.ordinal () );
    assertEquals ( 1, OPEN.ordinal () );
    assertEquals ( 2, HALF_OPEN.ordinal () );
    assertEquals ( 3, TRIP.ordinal () );
    assertEquals ( 4, PROBE.ordinal () );
    assertEquals ( 5, RESET.ordinal () );

  }

  @Test
  void testSignEnumValues () {

    final var values = Sign.values ();

    assertEquals ( 6, values.length );
    assertEquals ( CLOSE, values[0] );
    assertEquals ( OPEN, values[1] );
    assertEquals ( HALF_OPEN, values[2] );
    assertEquals ( TRIP, values[3] );
    assertEquals ( PROBE, values[4] );
    assertEquals ( RESET, values[5] );

  }

  @Test
  void testStateTransitionPattern () {

    // Simulate typical circuit breaker lifecycle
    breaker.close ();     // Normal operation
    breaker.trip ();      // Failures detected
    breaker.open ();      // Circuit opens
    breaker.halfOpen ();  // Testing recovery
    breaker.probe ();     // Test request
    breaker.close ();     // Recovery successful

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 6, signs.size () );
    assertEquals ( CLOSE, signs.get ( 0 ) );
    assertEquals ( TRIP, signs.get ( 1 ) );
    assertEquals ( OPEN, signs.get ( 2 ) );
    assertEquals ( HALF_OPEN, signs.get ( 3 ) );
    assertEquals ( PROBE, signs.get ( 4 ) );
    assertEquals ( CLOSE, signs.get ( 5 ) );

  }

  @Test
  void testSubjectAttachment () {

    breaker.close ();

    circuit.await ();

    final var capture =
      reservoir
        .drain ()
        .findFirst ()
        .orElseThrow ();

    assertEquals ( NAME, capture.subject ().name () );
    assertEquals ( CLOSE, capture.emission () );

  }

  @Test
  void testTrip () {

    breaker.trip ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( TRIP, signs.getFirst () );

  }

}
