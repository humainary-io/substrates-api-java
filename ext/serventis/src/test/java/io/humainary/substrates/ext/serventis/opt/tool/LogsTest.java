// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.opt.tool;

import io.humainary.substrates.ext.serventis.opt.tool.Logs.Log;
import io.humainary.substrates.ext.serventis.opt.tool.Logs.Sign;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.humainary.substrates.api.Substrates.*;
import static io.humainary.substrates.ext.serventis.opt.tool.Logs.Sign.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


/// Tests for the [Logs] API.
///
/// @author William David Louth
/// @since 1.0

final class LogsTest {

  private static final Cortex             CORTEX = cortex ();
  private static final Name               NAME   = CORTEX.name ( "com.acme.PaymentService" );
  private              Circuit            circuit;
  private              Reservoir < Sign > reservoir;
  private              Log                log;

  @BeforeEach
  void setup () {

    circuit =
      CORTEX.circuit ();

    final var conduit =
      circuit.conduit (
        Logs::composer
      );

    log =
      conduit.percept (
        NAME
      );

    reservoir =
      conduit.reservoir ();

  }

  @Test
  void testAllLevels () {

    log.severe ();
    log.warning ();
    log.info ();
    log.debug ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 4, signs.size () );
    assertEquals ( SEVERE, signs.getFirst () );
    assertEquals ( WARNING, signs.get ( 1 ) );
    assertEquals ( INFO, signs.get ( 2 ) );
    assertEquals ( DEBUG, signs.get ( 3 ) );

  }

  @Test
  void testDebug () {

    log.debug ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( DEBUG, signs.getFirst () );

  }

  @Test
  void testErrorPattern () {

    // Simulate error pattern
    log.info ();
    log.warning ();
    log.severe ();
    log.severe ();
    log.severe ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 5, signs.size () );
    assertEquals ( INFO, signs.getFirst () );
    assertEquals ( WARNING, signs.get ( 1 ) );
    assertEquals ( SEVERE, signs.get ( 2 ) );
    assertEquals ( SEVERE, signs.get ( 3 ) );
    assertEquals ( SEVERE, signs.get ( 4 ) );

    // Count severe errors
    final var severeCount =
      signs
        .stream ()
        .filter ( s -> s == SEVERE )
        .count ();

    assertEquals ( 3, severeCount );

  }

  @Test
  void testInfo () {

    log.info ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( INFO, signs.getFirst () );

  }

  @Test
  void testNormalOperationPattern () {

    // Simulate normal operation with occasional warnings
    log.info ();
    log.info ();
    log.warning ();
    log.info ();
    log.info ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 5, signs.size () );

    final var infoCount =
      signs
        .stream ()
        .filter ( s -> s == INFO )
        .count ();

    final var warningCount =
      signs
        .stream ()
        .filter ( s -> s == WARNING )
        .count ();

    assertEquals ( 4, infoCount );
    assertEquals ( 1, warningCount );

  }

  @Test
  void testSevere () {

    log.severe ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( SEVERE, signs.getFirst () );

  }

  @Test
  void testSign () {

    // Test direct sign() method for all sign values
    log.sign ( SEVERE );
    log.sign ( WARNING );
    log.sign ( INFO );
    log.sign ( DEBUG );

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 4, signs.size () );
    assertEquals ( SEVERE, signs.get ( 0 ) );
    assertEquals ( WARNING, signs.get ( 1 ) );
    assertEquals ( INFO, signs.get ( 2 ) );
    assertEquals ( DEBUG, signs.get ( 3 ) );

  }

  @Test
  void testSignEnumOrdinals () {

    // Ensure ordinals remain stable for compatibility
    assertEquals ( 0, SEVERE.ordinal () );
    assertEquals ( 1, WARNING.ordinal () );
    assertEquals ( 2, INFO.ordinal () );
    assertEquals ( 3, DEBUG.ordinal () );

  }

  @Test
  void testSignEnumValues () {

    final var values = Sign.values ();

    assertEquals ( 4, values.length );
    assertEquals ( SEVERE, values[0] );
    assertEquals ( WARNING, values[1] );
    assertEquals ( INFO, values[2] );
    assertEquals ( DEBUG, values[3] );

  }

  @Test
  void testSubjectAttachment () {

    log.info ();

    circuit.await ();

    final var capture =
      reservoir
        .drain ()
        .findFirst ()
        .orElseThrow ();

    assertEquals ( NAME, capture.subject ().name () );
    assertEquals ( INFO, capture.emission () );

  }

  @Test
  void testVerboseLoggingPattern () {

    // Simulate verbose debug logging
    log.debug ();
    log.debug ();
    log.debug ();
    log.debug ();
    log.debug ();
    log.info ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 6, signs.size () );

    final var debugCount =
      signs
        .stream ()
        .filter ( s -> s == DEBUG )
        .count ();

    assertEquals ( 5, debugCount );

  }

  @Test
  void testWarning () {

    log.warning ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( WARNING, signs.getFirst () );

  }

  @Test
  void testWarningCrescendo () {

    // Simulate warning crescendo pattern (increasing warnings)
    log.info ();
    log.warning ();
    log.warning ();
    log.warning ();
    log.severe ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 5, signs.size () );

    final var warningCount =
      signs
        .stream ()
        .filter ( s -> s == WARNING )
        .count ();

    assertEquals ( 3, warningCount );

  }

}
