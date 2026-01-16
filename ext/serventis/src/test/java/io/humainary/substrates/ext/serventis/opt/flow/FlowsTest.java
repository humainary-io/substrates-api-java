// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.opt.flow;

import io.humainary.substrates.ext.serventis.opt.flow.Flows.Dimension;
import io.humainary.substrates.ext.serventis.opt.flow.Flows.Sign;
import io.humainary.substrates.ext.serventis.opt.flow.Flows.Signal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.humainary.substrates.api.Substrates.*;
import static io.humainary.substrates.ext.serventis.opt.flow.Flows.Dimension.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


/// The test class for the [Flows.Flow] interface.
///
/// @author William David Louth
/// @since 1.0
final class FlowsTest {

  private static final Cortex               cortex = cortex ();
  private static final Name                 NAME   = cortex.name ( "flow.1" );
  private              Circuit              circuit;
  private              Reservoir < Signal > reservoir;
  private              Flows.Flow           flow;

  private void assertSignal (
    final Signal signal
  ) {

    circuit
      .await ();

    assertEquals (
      1L, reservoir
        .drain ()
        .filter ( capture -> capture.emission ().equals ( signal ) )
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
        Flows::composer
      );

    flow =
      conduit.percept (
        NAME
      );

    reservoir =
      conduit.reservoir ();

  }

  // Individual tests for each sign + dimension combination

  @Test
  void testSuccessIngress () {

    flow.success ( INGRESS );

    assertSignal (
      new Signal ( Sign.SUCCESS, INGRESS )
    );

  }

  @Test
  void testSuccessTransit () {

    flow.success ( TRANSIT );

    assertSignal (
      new Signal ( Sign.SUCCESS, TRANSIT )
    );

  }

  @Test
  void testSuccessEgress () {

    flow.success ( EGRESS );

    assertSignal (
      new Signal ( Sign.SUCCESS, EGRESS )
    );

  }

  @Test
  void testFailIngress () {

    flow.fail ( INGRESS );

    assertSignal (
      new Signal ( Sign.FAIL, INGRESS )
    );

  }

  @Test
  void testFailTransit () {

    flow.fail ( TRANSIT );

    assertSignal (
      new Signal ( Sign.FAIL, TRANSIT )
    );

  }

  @Test
  void testFailEgress () {

    flow.fail ( EGRESS );

    assertSignal (
      new Signal ( Sign.FAIL, EGRESS )
    );

  }

  /// Tests that [Dimension] enum ordinals remain stable for compatibility.
  ///
  /// This test ensures that the ordinal values of [Dimension] enum constants
  /// do not change, which is critical for serialization and external integrations.

  @Test
  void testDimensionEnumOrdinals () {

    assertEquals ( 0, Dimension.INGRESS.ordinal () );
    assertEquals ( 1, Dimension.TRANSIT.ordinal () );
    assertEquals ( 2, Dimension.EGRESS.ordinal () );

  }

  /// Tests that [Sign] enum ordinals remain stable for compatibility.
  ///
  /// This test ensures that the ordinal values of [Sign] enum constants
  /// do not change, which is critical for serialization and external integrations.

  @Test
  void testSignEnumOrdinals () {

    assertEquals ( 0, Sign.SUCCESS.ordinal () );
    assertEquals ( 1, Sign.FAIL.ordinal () );

  }

  @Test
  void testSignal () {

    // Test direct signal() method for all sign and dimension combinations
    flow.signal ( Sign.SUCCESS, INGRESS );
    flow.signal ( Sign.SUCCESS, TRANSIT );
    flow.signal ( Sign.SUCCESS, EGRESS );
    flow.signal ( Sign.FAIL, INGRESS );
    flow.signal ( Sign.FAIL, TRANSIT );
    flow.signal ( Sign.FAIL, EGRESS );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 6, signals.size () );
    assertEquals ( new Signal ( Sign.SUCCESS, INGRESS ), signals.get ( 0 ) );
    assertEquals ( new Signal ( Sign.SUCCESS, TRANSIT ), signals.get ( 1 ) );
    assertEquals ( new Signal ( Sign.SUCCESS, EGRESS ), signals.get ( 2 ) );
    assertEquals ( new Signal ( Sign.FAIL, INGRESS ), signals.get ( 3 ) );
    assertEquals ( new Signal ( Sign.FAIL, TRANSIT ), signals.get ( 4 ) );
    assertEquals ( new Signal ( Sign.FAIL, EGRESS ), signals.get ( 5 ) );

  }

  @Test
  void testSignalAccessors () {

    final var SUCCESS_INGRESS = new Signal ( Sign.SUCCESS, INGRESS );
    final var SUCCESS_TRANSIT = new Signal ( Sign.SUCCESS, TRANSIT );
    final var SUCCESS_EGRESS = new Signal ( Sign.SUCCESS, EGRESS );
    final var FAIL_INGRESS = new Signal ( Sign.FAIL, INGRESS );
    final var FAIL_TRANSIT = new Signal ( Sign.FAIL, TRANSIT );
    final var FAIL_EGRESS = new Signal ( Sign.FAIL, EGRESS );

    assertEquals ( Sign.SUCCESS, SUCCESS_INGRESS.sign () );
    assertEquals ( Dimension.INGRESS, SUCCESS_INGRESS.dimension () );

    assertEquals ( Sign.SUCCESS, SUCCESS_TRANSIT.sign () );
    assertEquals ( Dimension.TRANSIT, SUCCESS_TRANSIT.dimension () );

    assertEquals ( Sign.SUCCESS, SUCCESS_EGRESS.sign () );
    assertEquals ( Dimension.EGRESS, SUCCESS_EGRESS.dimension () );

    assertEquals ( Sign.FAIL, FAIL_INGRESS.sign () );
    assertEquals ( Dimension.INGRESS, FAIL_INGRESS.dimension () );

    assertEquals ( Sign.FAIL, FAIL_TRANSIT.sign () );
    assertEquals ( Dimension.TRANSIT, FAIL_TRANSIT.dimension () );

    assertEquals ( Sign.FAIL, FAIL_EGRESS.sign () );
    assertEquals ( Dimension.EGRESS, FAIL_EGRESS.dimension () );

  }

  /// Tests that [Sign] and [Dimension] enum ordinals remain stable for compatibility.
  ///
  /// This test ensures that the ordinal values of [Sign] and [Dimension] enum constants
  /// do not change, which is critical for serialization and external integrations.
  /// Signal is now a record composed of Sign and Dimension.

  @Test
  void testSignalComponentOrdinals () {

    // Verify Sign ordinals remain stable
    assertEquals ( 0, Sign.SUCCESS.ordinal () );
    assertEquals ( 1, Sign.FAIL.ordinal () );

    // Verify Dimension ordinals remain stable
    assertEquals ( 0, Dimension.INGRESS.ordinal () );
    assertEquals ( 1, Dimension.TRANSIT.ordinal () );
    assertEquals ( 2, Dimension.EGRESS.ordinal () );

    // Verify Signal composition works correctly
    final var signal = new Signal ( Sign.SUCCESS, Dimension.INGRESS );
    assertEquals ( Sign.SUCCESS, signal.sign () );
    assertEquals ( Dimension.INGRESS, signal.dimension () );

  }

  @Test
  void testSignalDimensionMappings () {

    // Verify all Sign x Dimension combinations can be constructed
    for ( final var sign : Sign.values () ) {

      for ( final var dimension : Dimension.values () ) {

        final var signal = new Signal ( sign, dimension );

        assertEquals (
          sign,
          signal.sign ()
        );

        assertEquals (
          dimension,
          signal.dimension ()
        );

      }

    }

    // Verify all dimensions exist
    assertEquals ( 3, Dimension.values ().length );
    assertEquals ( Dimension.INGRESS, Dimension.values ()[0] );
    assertEquals ( Dimension.TRANSIT, Dimension.values ()[1] );
    assertEquals ( Dimension.EGRESS, Dimension.values ()[2] );

  }

  @Test
  void testMultipleEmissions () {

    flow.success ( INGRESS );
    flow.success ( TRANSIT );
    flow.success ( EGRESS );
    flow.fail ( INGRESS );

    circuit
      .await ();

    assertEquals (
      4L,
      reservoir
        .drain ()
        .count ()
    );

  }

  @Test
  void testSubjectAssociation () {

    flow.success ( INGRESS );

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

  /// Tests the typical flow pattern: ingress -> transit -> egress

  @Test
  void testTypicalFlowPattern () {

    // Simulate successful flow through all stages
    flow.success ( INGRESS );  // entered
    flow.success ( TRANSIT );  // moved
    flow.success ( EGRESS );   // exited

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 3, signals.size () );
    assertEquals ( new Signal ( Sign.SUCCESS, INGRESS ), signals.get ( 0 ) );
    assertEquals ( new Signal ( Sign.SUCCESS, TRANSIT ), signals.get ( 1 ) );
    assertEquals ( new Signal ( Sign.SUCCESS, EGRESS ), signals.get ( 2 ) );

  }

  /// Tests failure at transit stage (dropped)

  @Test
  void testTransitFailurePattern () {

    // Simulate flow that enters but fails during processing
    flow.success ( INGRESS );  // entered
    flow.fail ( TRANSIT );     // dropped

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 2, signals.size () );
    assertEquals ( new Signal ( Sign.SUCCESS, INGRESS ), signals.get ( 0 ) );
    assertEquals ( new Signal ( Sign.FAIL, TRANSIT ), signals.get ( 1 ) );

  }

  /// Tests failure at ingress stage (refused)

  @Test
  void testIngressFailurePattern () {

    // Simulate flow that is refused entry
    flow.fail ( INGRESS );  // refused

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signals.size () );
    assertEquals ( new Signal ( Sign.FAIL, INGRESS ), signals.getFirst () );

  }

}
