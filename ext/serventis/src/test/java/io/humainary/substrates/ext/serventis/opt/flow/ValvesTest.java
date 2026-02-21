// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.opt.flow;

import io.humainary.substrates.ext.serventis.opt.flow.Valves.Sign;
import io.humainary.substrates.ext.serventis.opt.flow.Valves.Valve;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.humainary.substrates.api.Substrates.*;
import static io.humainary.substrates.ext.serventis.opt.flow.Valves.Sign.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


/// Tests for the [Valves] API.
///
/// @author William David Louth
/// @since 1.0

final class ValvesTest {

  private static final Cortex             CORTEX = cortex ();
  private static final Name               NAME   = CORTEX.name ( "api.ratelimit" );
  private              Circuit            circuit;
  private              Reservoir < Sign > reservoir;
  private              Valve              valve;

  @BeforeEach
  void setup () {

    circuit =
      CORTEX.circuit ();

    final var conduit =
      circuit.conduit (
        Valves::composer
      );

    valve =
      conduit.percept (
        NAME
      );

    reservoir =
      conduit.reservoir ();

  }

  @Test
  void testAdaptiveControlPattern () {

    // Simulate adaptive control: expand, then flow, then contract
    valve.expand ();
    valve.pass ();
    valve.pass ();
    valve.deny ();
    valve.contract ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 5, signs.size () );
    assertEquals ( EXPAND, signs.getFirst () );
    assertEquals ( PASS, signs.get ( 1 ) );
    assertEquals ( PASS, signs.get ( 2 ) );
    assertEquals ( DENY, signs.get ( 3 ) );
    assertEquals ( CONTRACT, signs.get ( 4 ) );

  }

  @Test
  void testAllSigns () {

    valve.pass ();
    valve.deny ();
    valve.expand ();
    valve.contract ();
    valve.drop ();
    valve.drain ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 6, signs.size () );
    assertEquals ( PASS, signs.getFirst () );
    assertEquals ( DENY, signs.get ( 1 ) );
    assertEquals ( EXPAND, signs.get ( 2 ) );
    assertEquals ( CONTRACT, signs.get ( 3 ) );
    assertEquals ( DROP, signs.get ( 4 ) );
    assertEquals ( DRAIN, signs.get ( 5 ) );

  }

  @Test
  void testContract () {

    valve.contract ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( CONTRACT, signs.getFirst () );

  }

  @Test
  void testDeny () {

    valve.deny ();

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
  void testDrain () {

    valve.drain ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( DRAIN, signs.getFirst () );

  }

  @Test
  void testExpand () {

    valve.expand ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( EXPAND, signs.getFirst () );

  }

  @Test
  void testFlowPattern () {

    // Simulate normal flow with some denials
    valve.pass ();
    valve.pass ();
    valve.deny ();
    valve.pass ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 4, signs.size () );

    final var passCount =
      signs
        .stream ()
        .filter ( s -> s == PASS )
        .count ();

    final var denyCount =
      signs
        .stream ()
        .filter ( s -> s == DENY )
        .count ();

    assertEquals ( 3, passCount );
    assertEquals ( 1, denyCount );

  }

  @Test
  void testOverloadRecoveryPattern () {

    // Simulate overload and recovery
    valve.drop ();
    valve.drop ();
    valve.contract ();
    valve.drain ();
    valve.drain ();
    valve.expand ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 6, signs.size () );
    assertEquals ( DROP, signs.get ( 0 ) );
    assertEquals ( DROP, signs.get ( 1 ) );
    assertEquals ( CONTRACT, signs.get ( 2 ) );
    assertEquals ( DRAIN, signs.get ( 3 ) );
    assertEquals ( DRAIN, signs.get ( 4 ) );
    assertEquals ( EXPAND, signs.get ( 5 ) );

  }

  @Test
  void testPass () {

    valve.pass ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( PASS, signs.getFirst () );

  }

  @Test
  void testSaturationPattern () {

    // Simulate saturation: many denials, no expansion
    valve.deny ();
    valve.deny ();
    valve.deny ();
    valve.deny ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 4, signs.size () );

    final var denyCount =
      signs
        .stream ()
        .filter ( s -> s == DENY )
        .count ();

    assertEquals ( 4, denyCount );

  }

  @Test
  void testShed () {

    valve.drop ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( DROP, signs.getFirst () );

  }

  @Test
  void testSign () {

    // Test direct sign() method for all sign values
    valve.sign ( PASS );
    valve.sign ( DENY );
    valve.sign ( EXPAND );
    valve.sign ( CONTRACT );
    valve.sign ( DROP );
    valve.sign ( DRAIN );

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 6, signs.size () );
    assertEquals ( PASS, signs.get ( 0 ) );
    assertEquals ( DENY, signs.get ( 1 ) );
    assertEquals ( EXPAND, signs.get ( 2 ) );
    assertEquals ( CONTRACT, signs.get ( 3 ) );
    assertEquals ( DROP, signs.get ( 4 ) );
    assertEquals ( DRAIN, signs.get ( 5 ) );

  }

  @Test
  void testSignEnumOrdinals () {

    // Ensure ordinals remain stable for compatibility
    assertEquals ( 0, PASS.ordinal () );
    assertEquals ( 1, DENY.ordinal () );
    assertEquals ( 2, EXPAND.ordinal () );
    assertEquals ( 3, CONTRACT.ordinal () );
    assertEquals ( 4, DROP.ordinal () );
    assertEquals ( 5, DRAIN.ordinal () );

  }

  @Test
  void testSignEnumValues () {

    final var values = Sign.values ();

    assertEquals ( 6, values.length );
    assertEquals ( PASS, values[0] );
    assertEquals ( DENY, values[1] );
    assertEquals ( EXPAND, values[2] );
    assertEquals ( CONTRACT, values[3] );
    assertEquals ( DROP, values[4] );
    assertEquals ( DRAIN, values[5] );

  }

  @Test
  void testSubjectAttachment () {

    valve.pass ();

    circuit.await ();

    final var capture =
      reservoir
        .drain ()
        .findFirst ()
        .orElseThrow ();

    assertEquals ( NAME, capture.subject ().name () );
    assertEquals ( PASS, capture.emission () );

  }

}
