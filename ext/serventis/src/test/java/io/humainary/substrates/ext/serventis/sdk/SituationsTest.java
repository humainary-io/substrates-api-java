// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.sdk;

import io.humainary.substrates.ext.serventis.sdk.Situations.Dimension;
import io.humainary.substrates.ext.serventis.sdk.Situations.Sign;
import io.humainary.substrates.ext.serventis.sdk.Situations.Signal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.humainary.substrates.api.Substrates.*;
import static io.humainary.substrates.ext.serventis.sdk.Situations.Dimension.*;
import static io.humainary.substrates.ext.serventis.sdk.Situations.Sign.*;
import static io.humainary.substrates.ext.serventis.sdk.Situations.Situation;
import static org.junit.jupiter.api.Assertions.assertEquals;


/// The test class for the [Situation] interface.
///
/// @author William David Louth
/// @since 1.0
final class SituationsTest {

  private static final Cortex               cortex = cortex ();
  private static final Name                 NAME   = cortex.name ( "situation.1" );
  private              Circuit              circuit;
  private              Reservoir < Signal > reservoir;
  private              Situation            situation;

  private void assertSignal (
    final Sign sign,
    final Dimension dimension
  ) {

    circuit
      .await ();

    assertEquals (
      1L, reservoir
        .drain ()
        .filter ( capture -> capture.emission ().sign () == sign )
        .filter ( capture -> capture.emission ().dimension () == dimension )
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
        Situations::composer
      );

    situation =
      conduit.percept (
        NAME
      );

    reservoir =
      conduit.reservoir ();

  }


  @Test
  void testCritical () {

    situation.critical ( CONSTANT );

    assertSignal (
      CRITICAL,
      CONSTANT
    );

  }

  /// Tests that [Dimension] enum ordinals remain stable for compatibility.
  ///
  /// This test ensures that the ordinal values of [Dimension] enum constants
  /// do not change, which is critical for serialization and external integrations.

  @Test
  void testDimensionEnumOrdinals () {

    assertEquals ( 0, Dimension.CONSTANT.ordinal () );
    assertEquals ( 1, Dimension.VARIABLE.ordinal () );
    assertEquals ( 2, Dimension.VOLATILE.ordinal () );

  }

  @Test
  void testMultipleEmissions () {

    situation.normal ( CONSTANT );
    situation.warning ( VARIABLE );
    situation.critical ( VOLATILE );

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

    situation.normal ( CONSTANT );

    assertSignal (
      NORMAL,
      CONSTANT
    );

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
  void testSignal () {

    // Test direct signal() method for all sign and dimension combinations
    situation.signal ( NORMAL, CONSTANT );
    situation.signal ( WARNING, VARIABLE );
    situation.signal ( CRITICAL, VOLATILE );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 3, signals.size () );
    assertEquals ( NORMAL, signals.get ( 0 ).sign () );
    assertEquals ( CONSTANT, signals.get ( 0 ).dimension () );
    assertEquals ( WARNING, signals.get ( 1 ).sign () );
    assertEquals ( VARIABLE, signals.get ( 1 ).dimension () );
    assertEquals ( CRITICAL, signals.get ( 2 ).sign () );
    assertEquals ( VOLATILE, signals.get ( 2 ).dimension () );

  }

  @Test
  void testSubjectAssociation () {

    situation.normal ( CONSTANT );

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

  /// Tests the variability dimension spectrum from CONSTANT to VOLATILE.

  @Test
  void testVariabilityDimensionSpectrum () {

    // Test all three variability dimensions
    situation.critical ( CONSTANT );
    situation.critical ( VARIABLE );
    situation.critical ( VOLATILE );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 3, signals.size () );
    assertEquals ( CONSTANT, signals.get ( 0 ).dimension () );
    assertEquals ( VARIABLE, signals.get ( 1 ).dimension () );
    assertEquals ( VOLATILE, signals.get ( 2 ).dimension () );

    // All should have CRITICAL sign
    signals.forEach ( signal ->
      assertEquals ( CRITICAL, signal.sign () )
    );

  }

  @Test
  void testWarning () {

    situation.warning ( VARIABLE );

    assertSignal (
      WARNING,
      VARIABLE
    );

  }

}
