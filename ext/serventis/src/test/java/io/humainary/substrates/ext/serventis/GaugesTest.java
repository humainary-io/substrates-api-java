// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis;

import io.humainary.substrates.ext.serventis.ext.Gauges;
import io.humainary.substrates.ext.serventis.ext.Gauges.Gauge;
import io.humainary.substrates.ext.serventis.ext.Gauges.Sign;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.humainary.substrates.api.Substrates.*;
import static io.humainary.substrates.ext.serventis.ext.Gauges.Sign.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


/// Tests for the [Gauges] API.
///
/// @author William David Louth
/// @since 1.0

final class GaugesTest {

  private static final Cortex             CORTEX = cortex ();
  private static final Name               NAME   = CORTEX.name ( "connections.active" );
  private              Circuit            circuit;
  private              Reservoir < Sign > reservoir;
  private              Gauge              gauge;

  @BeforeEach
  void setup () {

    circuit =
      CORTEX.circuit ();

    final var conduit =
      circuit.conduit (
        Gauges::composer
      );

    gauge =
      conduit.percept (
        NAME
      );

    reservoir =
      CORTEX.reservoir (
        conduit
      );

  }

  @Test
  void testBidirectionalOperations () {

    gauge.increment ();
    gauge.increment ();
    gauge.decrement ();
    gauge.increment ();
    gauge.decrement ();
    gauge.decrement ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 6, signs.size () );
    assertEquals ( INCREMENT, signs.getFirst () );
    assertEquals ( INCREMENT, signs.get ( 1 ) );
    assertEquals ( DECREMENT, signs.get ( 2 ) );
    assertEquals ( INCREMENT, signs.get ( 3 ) );
    assertEquals ( DECREMENT, signs.get ( 4 ) );
    assertEquals ( DECREMENT, signs.get ( 5 ) );

  }

  @Test
  void testBoundaryConditions () {

    gauge.increment ();
    gauge.overflow ();
    gauge.decrement ();
    gauge.underflow ();
    gauge.reset ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 5, signs.size () );
    assertEquals ( INCREMENT, signs.getFirst () );
    assertEquals ( OVERFLOW, signs.get ( 1 ) );
    assertEquals ( DECREMENT, signs.get ( 2 ) );
    assertEquals ( UNDERFLOW, signs.get ( 3 ) );
    assertEquals ( RESET, signs.get ( 4 ) );

  }

  @Test
  void testDecrement () {

    gauge.decrement ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( DECREMENT, signs.getFirst () );

  }

  @Test
  void testIncrement () {

    gauge.increment ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( INCREMENT, signs.getFirst () );

  }

  @Test
  void testOverflow () {

    gauge.overflow ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( OVERFLOW, signs.getFirst () );

  }

  @Test
  void testReset () {

    gauge.reset ();

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
    gauge.sign ( INCREMENT );
    gauge.sign ( DECREMENT );
    gauge.sign ( OVERFLOW );
    gauge.sign ( UNDERFLOW );
    gauge.sign ( RESET );

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 5, signs.size () );
    assertEquals ( INCREMENT, signs.get ( 0 ) );
    assertEquals ( DECREMENT, signs.get ( 1 ) );
    assertEquals ( OVERFLOW, signs.get ( 2 ) );
    assertEquals ( UNDERFLOW, signs.get ( 3 ) );
    assertEquals ( RESET, signs.get ( 4 ) );

  }

  @Test
  void testSignEnumOrdinals () {

    // Ensure ordinals remain stable for compatibility
    assertEquals ( 0, INCREMENT.ordinal () );
    assertEquals ( 1, DECREMENT.ordinal () );
    assertEquals ( 2, OVERFLOW.ordinal () );
    assertEquals ( 3, UNDERFLOW.ordinal () );
    assertEquals ( 4, RESET.ordinal () );

  }

  @Test
  void testSignEnumValues () {

    final var values = Sign.values ();

    assertEquals ( 5, values.length );
    assertEquals ( INCREMENT, values[0] );
    assertEquals ( DECREMENT, values[1] );
    assertEquals ( OVERFLOW, values[2] );
    assertEquals ( UNDERFLOW, values[3] );
    assertEquals ( RESET, values[4] );

  }

  @Test
  void testSubjectAttachment () {

    gauge.increment ();

    circuit.await ();

    final var capture =
      reservoir
        .drain ()
        .findFirst ()
        .orElseThrow ();

    assertEquals ( NAME, capture.subject ().name () );
    assertEquals ( INCREMENT, capture.emission () );

  }

  @Test
  void testUnderflow () {

    gauge.underflow ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( UNDERFLOW, signs.getFirst () );

  }

}
