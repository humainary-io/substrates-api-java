// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis;

import io.humainary.substrates.ext.serventis.ext.Reporters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.humainary.substrates.api.Substrates.*;
import static io.humainary.substrates.ext.serventis.ext.Reporters.Reporter;
import static io.humainary.substrates.ext.serventis.ext.Reporters.Sign;
import static io.humainary.substrates.ext.serventis.ext.Reporters.Sign.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


/// The test class for the [Reporter] interface.
///
/// @author William David Louth
/// @since 1.0
final class ReportersTest {

  private static final Cortex             cortex = cortex ();
  private static final Name               NAME   = cortex.name ( "reporter.1" );
  private              Circuit            circuit;
  private              Reservoir < Sign > reservoir;
  private              Reporter           reporter;

  private void assertSign (
    final Sign sign
  ) {

    circuit
      .await ();

    assertEquals (
      1L, reservoir
        .drain ()
        .filter ( capture -> capture.emission () == sign )
        .filter ( capture -> capture.subject ().name () == NAME )
        .count ()
    );

  }


  @BeforeEach
  void setup () {

    circuit =
      cortex ().circuit ();

    final var conduit =
      circuit.conduit (
        Reporters::composer
      );

    reporter =
      conduit.percept (
        NAME
      );

    reservoir =
      cortex ().reservoir (
        conduit
      );

  }


  @Test
  void testCritical () {

    reporter.critical ();

    assertSign (
      CRITICAL
    );

  }


  @Test
  void testMultipleEmissions () {

    reporter.normal ();
    reporter.warning ();
    reporter.critical ();

    circuit
      .await ();

    assertEquals (
      3L,
      reservoir
        .drain ()
        .count ()
    );

  }


  @Test
  void testNormal () {

    reporter.normal ();

    assertSign (
      NORMAL
    );

  }

  @Test
  void testSign () {

    // Test direct sign() method for all sign values
    reporter.sign ( NORMAL );
    reporter.sign ( WARNING );
    reporter.sign ( CRITICAL );

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 3, signs.size () );
    assertEquals ( NORMAL, signs.get ( 0 ) );
    assertEquals ( WARNING, signs.get ( 1 ) );
    assertEquals ( CRITICAL, signs.get ( 2 ) );

  }

  /// Tests that [Sign] enum ordinals remain stable for compatibility.
  ///
  /// This test ensures that the ordinal values of [Sign] enum constants
  /// do not change, which is critical for serialization and external integrations.

  @Test
  void testSignEnumOrdinals () {

    assertEquals ( 0, Sign.NORMAL.ordinal () );
    assertEquals ( 1, Sign.WARNING.ordinal () );
    assertEquals ( 2, Sign.CRITICAL.ordinal () );

  }

  @Test
  void testSubjectAssociation () {

    reporter.normal ();

    circuit
      .await ();

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
  void testWarning () {

    reporter.warning ();

    assertSign (
      WARNING
    );

  }

}
