// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.opt.data;

import io.humainary.substrates.ext.serventis.opt.data.Stacks.Sign;
import io.humainary.substrates.ext.serventis.opt.data.Stacks.Stack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.humainary.substrates.api.Substrates.*;
import static io.humainary.substrates.ext.serventis.opt.data.Stacks.Sign.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


/// Tests for the [Stacks] API.
///
/// @author William David Louth
/// @since 1.0

final class StacksTest {

  private static final Cortex             CORTEX = cortex ();
  private static final Name               NAME   = CORTEX.name ( "parser.states" );
  private              Circuit            circuit;
  private              Reservoir < Sign > reservoir;
  private              Stack              stack;

  @BeforeEach
  void setup () {

    circuit =
      CORTEX.circuit ();

    final var conduit =
      circuit.conduit (
        Stacks::composer
      );

    stack =
      conduit.percept (
        NAME
      );

    reservoir =
      conduit.reservoir ();

  }

  @Test
  void testBoundaryConditions () {

    stack.push ();
    stack.overflow ();
    stack.pop ();
    stack.underflow ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 4, signs.size () );
    assertEquals ( PUSH, signs.getFirst () );
    assertEquals ( OVERFLOW, signs.get ( 1 ) );
    assertEquals ( POP, signs.get ( 2 ) );
    assertEquals ( UNDERFLOW, signs.get ( 3 ) );

  }

  @Test
  void testOverflow () {

    stack.overflow ();

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
  void testOverflowPattern () {

    stack.push ();
    stack.push ();
    stack.overflow ();
    stack.overflow ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 4, signs.size () );
    assertEquals ( PUSH, signs.getFirst () );
    assertEquals ( PUSH, signs.get ( 1 ) );
    assertEquals ( OVERFLOW, signs.get ( 2 ) );
    assertEquals ( OVERFLOW, signs.get ( 3 ) );

  }

  @Test
  void testPop () {

    stack.pop ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( POP, signs.getFirst () );

  }

  @Test
  void testPush () {

    stack.push ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( PUSH, signs.getFirst () );

  }

  @Test
  void testSign () {

    // Test direct sign() method for all sign values
    stack.sign ( PUSH );
    stack.sign ( POP );
    stack.sign ( OVERFLOW );
    stack.sign ( UNDERFLOW );

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 4, signs.size () );
    assertEquals ( PUSH, signs.get ( 0 ) );
    assertEquals ( POP, signs.get ( 1 ) );
    assertEquals ( OVERFLOW, signs.get ( 2 ) );
    assertEquals ( UNDERFLOW, signs.get ( 3 ) );

  }

  @Test
  void testSignEnumOrdinals () {

    // Ensure ordinals remain stable for compatibility
    assertEquals ( 0, PUSH.ordinal () );
    assertEquals ( 1, POP.ordinal () );
    assertEquals ( 2, OVERFLOW.ordinal () );
    assertEquals ( 3, UNDERFLOW.ordinal () );

  }

  @Test
  void testSignEnumValues () {

    final var values = Sign.values ();

    assertEquals ( 4, values.length );
    assertEquals ( PUSH, values[0] );
    assertEquals ( POP, values[1] );
    assertEquals ( OVERFLOW, values[2] );
    assertEquals ( UNDERFLOW, values[3] );

  }

  @Test
  void testStackLifecycle () {

    stack.push ();
    stack.push ();
    stack.pop ();
    stack.push ();
    stack.pop ();
    stack.pop ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 6, signs.size () );
    assertEquals ( PUSH, signs.getFirst () );
    assertEquals ( PUSH, signs.get ( 1 ) );
    assertEquals ( POP, signs.get ( 2 ) );
    assertEquals ( PUSH, signs.get ( 3 ) );
    assertEquals ( POP, signs.get ( 4 ) );
    assertEquals ( POP, signs.get ( 5 ) );

  }

  @Test
  void testSubjectAttachment () {

    stack.push ();

    circuit.await ();

    final var capture =
      reservoir
        .drain ()
        .findFirst ()
        .orElseThrow ();

    assertEquals ( NAME, capture.subject ().name () );
    assertEquals ( PUSH, capture.emission () );

  }

  @Test
  void testUnderflow () {

    stack.underflow ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( UNDERFLOW, signs.getFirst () );

  }

  @Test
  void testUnderflowPattern () {

    stack.pop ();
    stack.pop ();
    stack.underflow ();
    stack.underflow ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 4, signs.size () );
    assertEquals ( POP, signs.getFirst () );
    assertEquals ( POP, signs.get ( 1 ) );
    assertEquals ( UNDERFLOW, signs.get ( 2 ) );
    assertEquals ( UNDERFLOW, signs.get ( 3 ) );

  }

}
