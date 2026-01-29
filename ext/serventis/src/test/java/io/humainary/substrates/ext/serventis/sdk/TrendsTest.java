// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.sdk;

import io.humainary.substrates.ext.serventis.sdk.Trends.Sign;
import io.humainary.substrates.ext.serventis.sdk.Trends.Trend;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.humainary.substrates.api.Substrates.*;
import static io.humainary.substrates.ext.serventis.sdk.Trends.Sign.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


/// Tests for the [Trends] API.
///
/// @author William David Louth
/// @since 1.0

final class TrendsTest {

  private static final Cortex             CORTEX = cortex ();
  private static final Name               NAME   = CORTEX.name ( "api.latency" );
  private              Circuit            circuit;
  private              Reservoir < Sign > reservoir;
  private              Trend              trend;

  @BeforeEach
  void setup () {

    circuit =
      CORTEX.circuit ();

    final var conduit =
      circuit.conduit (
        Trends::composer
      );

    trend =
      conduit.percept (
        NAME
      );

    reservoir =
      conduit.reservoir ();

  }

  @Test
  void testChaos () {

    trend.chaos ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( CHAOS, signs.getFirst () );

  }

  @Test
  void testCycle () {

    trend.cycle ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( CYCLE, signs.getFirst () );

  }

  @Test
  void testDrift () {

    trend.drift ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( DRIFT, signs.getFirst () );

  }

  @Test
  void testMultipleTrendInstruments () {

    final var latencyName = CORTEX.name ( "api.latency" );
    final var throughputName = CORTEX.name ( "api.throughput" );

    final var conduit = circuit.conduit ( Trends::composer );
    final var latencyTrend = conduit.percept ( latencyName );
    final var throughputTrend = conduit.percept ( throughputName );
    final var signReservoir = conduit.reservoir ();

    latencyTrend.stable ();
    throughputTrend.drift ();
    latencyTrend.spike ();

    circuit.await ();

    final var allCaptures = signReservoir.drain ().toList ();

    assertEquals ( 3, allCaptures.size () );

    assertEquals ( latencyName, allCaptures.get ( 0 ).subject ().name () );
    assertEquals ( STABLE, allCaptures.get ( 0 ).emission () );

    assertEquals ( throughputName, allCaptures.get ( 1 ).subject ().name () );
    assertEquals ( DRIFT, allCaptures.get ( 1 ).emission () );

    assertEquals ( latencyName, allCaptures.get ( 2 ).subject ().name () );
    assertEquals ( SPIKE, allCaptures.get ( 2 ).emission () );

  }

  @Test
  void testPatternSequence () {

    // Simulate a process going from stable to problematic
    trend.stable ();
    trend.drift ();
    trend.spike ();
    trend.chaos ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 4, signs.size () );
    assertEquals ( STABLE, signs.get ( 0 ) );
    assertEquals ( DRIFT, signs.get ( 1 ) );
    assertEquals ( SPIKE, signs.get ( 2 ) );
    assertEquals ( CHAOS, signs.get ( 3 ) );

  }

  @Test
  void testSign () {

    trend.sign ( STABLE );
    trend.sign ( DRIFT );
    trend.sign ( SPIKE );
    trend.sign ( CYCLE );
    trend.sign ( CHAOS );

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 5, signs.size () );
    assertEquals ( STABLE, signs.get ( 0 ) );
    assertEquals ( DRIFT, signs.get ( 1 ) );
    assertEquals ( SPIKE, signs.get ( 2 ) );
    assertEquals ( CYCLE, signs.get ( 3 ) );
    assertEquals ( CHAOS, signs.get ( 4 ) );

  }

  @Test
  void testSignEnumOrdinals () {

    assertEquals ( 0, STABLE.ordinal () );
    assertEquals ( 1, DRIFT.ordinal () );
    assertEquals ( 2, SPIKE.ordinal () );
    assertEquals ( 3, CYCLE.ordinal () );
    assertEquals ( 4, CHAOS.ordinal () );

  }

  @Test
  void testSignEnumValues () {

    final var values = Sign.values ();

    assertEquals ( 5, values.length );
    assertEquals ( STABLE, values[0] );
    assertEquals ( DRIFT, values[1] );
    assertEquals ( SPIKE, values[2] );
    assertEquals ( CYCLE, values[3] );
    assertEquals ( CHAOS, values[4] );

  }

  @Test
  void testSpike () {

    trend.spike ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( SPIKE, signs.getFirst () );

  }

  @Test
  void testStable () {

    trend.stable ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( STABLE, signs.getFirst () );

  }

  @Test
  void testSubjectAttachment () {

    trend.stable ();

    circuit.await ();

    final var capture =
      reservoir
        .drain ()
        .findFirst ()
        .orElseThrow ();

    assertEquals ( NAME, capture.subject ().name () );
    assertEquals ( STABLE, capture.emission () );

  }

}
