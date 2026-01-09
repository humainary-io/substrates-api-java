// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.sdk;

import io.humainary.substrates.ext.serventis.sdk.Surveys.Dimension;
import io.humainary.substrates.ext.serventis.sdk.Surveys.Signal;
import io.humainary.substrates.ext.serventis.sdk.Surveys.Survey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.humainary.substrates.api.Substrates.*;
import static io.humainary.substrates.ext.serventis.sdk.Statuses.Sign.*;
import static io.humainary.substrates.ext.serventis.sdk.Surveys.Dimension.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;


/// Tests for the [Surveys] API.
///
/// @author William David Louth
/// @since 1.0

final class SurveysTest {

  private static final Cortex CORTEX = cortex ();
  private static final Name   NAME   = CORTEX.name ( "cluster.health" );

  private Circuit                                circuit;
  private Reservoir < Signal < Statuses.Sign > > reservoir;
  private Survey < Statuses.Sign >               survey;

  @BeforeEach
  void setup () {

    circuit =
      CORTEX.circuit ();

    final var conduit =
      circuit.conduit (
        Surveys.composer (
          Statuses.Sign.class
        )
      );

    survey =
      conduit.percept (
        NAME
      );

    reservoir =
      conduit.reservoir ();

  }

  // ========== INDIVIDUAL DIMENSION TESTS ==========

  @Test
  void testDivided () {

    survey.signal ( DEGRADED, DIVIDED );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signals.size () );
    assertEquals ( DEGRADED, signals.getFirst ().sign () );
    assertEquals ( DIVIDED, signals.getFirst ().dimension () );

  }

  @Test
  void testMajority () {

    survey.signal ( STABLE, MAJORITY );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signals.size () );
    assertEquals ( STABLE, signals.getFirst ().sign () );
    assertEquals ( MAJORITY, signals.getFirst ().dimension () );

  }

  @Test
  void testUnanimous () {

    survey.signal ( DOWN, UNANIMOUS );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signals.size () );
    assertEquals ( DOWN, signals.getFirst ().sign () );
    assertEquals ( UNANIMOUS, signals.getFirst ().dimension () );

  }

  // ========== ENUM STABILITY TESTS ==========

  @Test
  void testDimensionEnumOrdinals () {

    // Ensure ordinals remain stable for compatibility
    // Spectrum: DIVIDED → MAJORITY → UNANIMOUS (increasing agreement)
    assertEquals ( 0, DIVIDED.ordinal () );
    assertEquals ( 1, MAJORITY.ordinal () );
    assertEquals ( 2, UNANIMOUS.ordinal () );

  }

  @Test
  void testDimensionEnumValues () {

    final var values = Dimension.values ();

    assertEquals ( 3, values.length );

  }

  // ========== PATTERN TESTS ==========

  @Test
  void testAgreementProgression () {

    // Simulate agreement improving over time
    survey.signal ( DEGRADED, DIVIDED );    // Initially split
    survey.signal ( DEGRADED, MAJORITY );   // Converging to majority
    survey.signal ( DEGRADED, UNANIMOUS );  // Full consensus

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 3, signals.size () );
    assertEquals ( DEGRADED, signals.get ( 0 ).sign () );
    assertEquals ( DIVIDED, signals.get ( 0 ).dimension () );
    assertEquals ( DEGRADED, signals.get ( 1 ).sign () );
    assertEquals ( MAJORITY, signals.get ( 1 ).dimension () );
    assertEquals ( DEGRADED, signals.get ( 2 ).sign () );
    assertEquals ( UNANIMOUS, signals.get ( 2 ).dimension () );

  }

  @Test
  void testCollectiveStatusChanges () {

    // Simulate collective assessment changing over time
    survey.signal ( STABLE, UNANIMOUS );     // All agree stable
    survey.signal ( DIVERGING, MAJORITY );   // Most see diverging
    survey.signal ( DEGRADED, UNANIMOUS );   // All agree degraded

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 3, signals.size () );
    assertEquals ( STABLE, signals.get ( 0 ).sign () );
    assertEquals ( UNANIMOUS, signals.get ( 0 ).dimension () );
    assertEquals ( DIVERGING, signals.get ( 1 ).sign () );
    assertEquals ( MAJORITY, signals.get ( 1 ).dimension () );
    assertEquals ( DEGRADED, signals.get ( 2 ).sign () );
    assertEquals ( UNANIMOUS, signals.get ( 2 ).dimension () );

  }

  @Test
  void testDividedOutcomes () {

    // Simulate divided assessments across different signs
    survey.signal ( STABLE, DIVIDED );
    survey.signal ( DEGRADED, DIVIDED );
    survey.signal ( DIVERGING, DIVIDED );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 3, signals.size () );
    // All divided - no clear consensus on any sign
    for ( final var signal : signals ) {
      assertEquals ( DIVIDED, signal.dimension () );
    }

  }

  // ========== SIGNAL CACHING TESTS ==========

  @Test
  void testSignalCaching () {

    // Emit same signal twice
    survey.signal ( DEGRADED, MAJORITY );
    survey.signal ( DEGRADED, MAJORITY );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 2, signals.size () );

    // Verify same Signal instance is reused (cached)
    assertSame ( signals.get ( 0 ), signals.get ( 1 ) );

  }

  @Test
  void testSignalCachingAllCombinations () {

    // Test caching for multiple sign/dimension combinations
    final var dimensions = Dimension.values ();

    // First pass - emit for DEGRADED
    for ( final var dimension : dimensions ) {
      survey.signal ( DEGRADED, dimension );
    }

    circuit.await ();

    final var firstPass =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    // Second pass - emit same signals
    for ( final var dimension : dimensions ) {
      survey.signal ( DEGRADED, dimension );
    }

    circuit.await ();

    final var secondPass =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    // Verify all instances are cached (same reference)
    for ( var i = 0; i < dimensions.length; i++ ) {
      assertSame (
        firstPass.get ( i ),
        secondPass.get ( i ),
        "Signal should be cached for DEGRADED × " + dimensions[i]
      );
    }

  }

  // ========== SUBJECT TESTS ==========

  @Test
  void testSubjectAttachment () {

    survey.signal ( STABLE, UNANIMOUS );

    circuit.await ();

    final var capture =
      reservoir
        .drain ()
        .findFirst ()
        .orElseThrow ();

    assertEquals ( NAME, capture.subject ().name () );
    assertEquals ( STABLE, capture.emission ().sign () );
    assertEquals ( UNANIMOUS, capture.emission ().dimension () );

  }

  @Test
  void testMultipleSubjects () {

    final var name1 = CORTEX.name ( "cluster.region1.health" );
    final var name2 = CORTEX.name ( "cluster.region2.health" );

    final var conduit = circuit.conduit ( Surveys.composer ( Statuses.Sign.class ) );
    final var survey1 = conduit.percept ( name1 );
    final var survey2 = conduit.percept ( name2 );
    final var signalReservoir = conduit.reservoir ();

    survey1.signal ( STABLE, UNANIMOUS );
    survey2.signal ( DEGRADED, MAJORITY );
    survey1.signal ( DIVERGING, DIVIDED );

    circuit.await ();

    final var allSignals = signalReservoir.drain ().toList ();

    assertEquals ( 3, allSignals.size () );
    assertEquals ( name1, allSignals.get ( 0 ).subject ().name () );
    assertEquals ( name2, allSignals.get ( 1 ).subject ().name () );
    assertEquals ( name1, allSignals.get ( 2 ).subject ().name () );

  }

  // ========== GENERIC SIGN TYPE TESTS ==========

  @Test
  void testWithDifferentSignType () {

    // Test that Surveys works with different Sign types (like Trends)
    final var trendsConduit =
      circuit.conduit (
        Surveys.composer (
          Trends.Sign.class
        )
      );

    final var trendsSurvey =
      trendsConduit.percept (
        CORTEX.name ( "cluster.trends" )
      );

    final var trendsReservoir =
      trendsConduit.reservoir ();

    trendsSurvey.signal (
      Trends.Sign.STABLE,
      UNANIMOUS
    );

    trendsSurvey.signal (
      Trends.Sign.DRIFT,
      MAJORITY
    );

    trendsSurvey.signal (
      Trends.Sign.SPIKE,
      DIVIDED
    );

    circuit.await ();

    final var signals =
      trendsReservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 3, signals.size () );
    assertEquals ( Trends.Sign.STABLE, signals.get ( 0 ).sign () );
    assertEquals ( UNANIMOUS, signals.get ( 0 ).dimension () );
    assertEquals ( Trends.Sign.DRIFT, signals.get ( 1 ).sign () );
    assertEquals ( MAJORITY, signals.get ( 1 ).dimension () );
    assertEquals ( Trends.Sign.SPIKE, signals.get ( 2 ).sign () );
    assertEquals ( DIVIDED, signals.get ( 2 ).dimension () );

  }

  // ========== CLUSTER SCENARIO TESTS ==========

  @Test
  void testClusterHealthScenario () {

    // Realistic scenario: 5-node cluster monitoring shared resource
    // Time 0: All nodes report STABLE
    survey.signal ( STABLE, UNANIMOUS );

    // Time 1: 3 nodes see DEGRADED, 2 still see STABLE
    survey.signal ( DEGRADED, MAJORITY );

    // Time 2: 4 nodes see DEGRADED, 1 sees DEFECTIVE
    survey.signal ( DEGRADED, MAJORITY );

    // Time 3: All nodes agree on DEGRADED
    survey.signal ( DEGRADED, UNANIMOUS );

    // Time 4: Recovery - split between DEGRADED and CONVERGING
    survey.signal ( CONVERGING, DIVIDED );

    // Time 5: Most see CONVERGING
    survey.signal ( CONVERGING, MAJORITY );

    // Time 6: All agree STABLE again
    survey.signal ( STABLE, UNANIMOUS );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 7, signals.size () );

    // Verify the progression
    assertEquals ( STABLE, signals.get ( 0 ).sign () );
    assertEquals ( UNANIMOUS, signals.get ( 0 ).dimension () );

    assertEquals ( DEGRADED, signals.get ( 3 ).sign () );
    assertEquals ( UNANIMOUS, signals.get ( 3 ).dimension () );

    assertEquals ( STABLE, signals.get ( 6 ).sign () );
    assertEquals ( UNANIMOUS, signals.get ( 6 ).dimension () );

  }

}
