// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis;

import io.humainary.substrates.ext.serventis.ext.Counters;
import io.humainary.substrates.ext.serventis.ext.Counters.Counter;
import io.humainary.substrates.ext.serventis.ext.Counters.Sign;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.humainary.substrates.api.Substrates.*;
import static io.humainary.substrates.ext.serventis.ext.Counters.Sign.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


/// Tests for the [Counters] API.
///
/// @author William David Louth
/// @since 1.0

final class CountersTest {

  private static final Cortex             CORTEX = cortex ();
  private static final Name               NAME   = CORTEX.name ( "requests.total" );
  private              Circuit            circuit;
  private              Reservoir < Sign > reservoir;
  private              Counter            counter;

  @BeforeEach
  void setup () {

    circuit =
      CORTEX.circuit ();

    final var conduit =
      circuit.conduit (
        Counters::composer
      );

    counter =
      conduit.percept (
        NAME
      );

    reservoir =
      CORTEX.reservoir (
        conduit
      );

  }

  @Test
  void testIncrement () {

    counter.increment ();

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
  void testMixedOperations () {

    counter.increment ();
    counter.increment ();
    counter.overflow ();
    counter.reset ();
    counter.increment ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 5, signs.size () );
    assertEquals ( INCREMENT, signs.getFirst () );
    assertEquals ( INCREMENT, signs.get ( 1 ) );
    assertEquals ( OVERFLOW, signs.get ( 2 ) );
    assertEquals ( RESET, signs.get ( 3 ) );
    assertEquals ( INCREMENT, signs.get ( 4 ) );

  }

  @Test
  void testOverflow () {

    counter.overflow ();

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

    counter.reset ();

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
    counter.sign ( INCREMENT );
    counter.sign ( OVERFLOW );
    counter.sign ( RESET );

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 3, signs.size () );
    assertEquals ( INCREMENT, signs.get ( 0 ) );
    assertEquals ( OVERFLOW, signs.get ( 1 ) );
    assertEquals ( RESET, signs.get ( 2 ) );

  }

  @Test
  void testSignEnumOrdinals () {

    // Ensure ordinals remain stable for compatibility
    assertEquals ( 0, INCREMENT.ordinal () );
    assertEquals ( 1, OVERFLOW.ordinal () );
    assertEquals ( 2, RESET.ordinal () );

  }

  @Test
  void testSignEnumValues () {

    final var values = Sign.values ();

    assertEquals ( 3, values.length );
    assertEquals ( INCREMENT, values[0] );
    assertEquals ( OVERFLOW, values[1] );
    assertEquals ( RESET, values[2] );

  }

  @Test
  void testSubjectAttachment () {

    counter.increment ();

    circuit.await ();

    final var capture =
      reservoir
        .drain ()
        .findFirst ()
        .orElseThrow ();

    assertEquals ( NAME, capture.subject ().name () );
    assertEquals ( INCREMENT, capture.emission () );

  }

}
