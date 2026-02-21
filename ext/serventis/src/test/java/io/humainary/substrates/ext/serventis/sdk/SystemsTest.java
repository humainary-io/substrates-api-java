// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.sdk;

import io.humainary.substrates.ext.serventis.sdk.Systems.Dimension;
import io.humainary.substrates.ext.serventis.sdk.Systems.Sign;
import io.humainary.substrates.ext.serventis.sdk.Systems.Signal;
import io.humainary.substrates.ext.serventis.sdk.Systems.System;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.humainary.substrates.api.Substrates.*;
import static io.humainary.substrates.ext.serventis.sdk.Systems.Dimension.*;
import static io.humainary.substrates.ext.serventis.sdk.Systems.Sign.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;


/// Tests for the [Systems] API.
///
/// @author William David Louth
/// @since 1.0

final class SystemsTest {

  private static final Cortex               CORTEX = cortex ();
  private static final Name                 NAME   = CORTEX.name ( "db.pool" );
  private              Circuit              circuit;
  private              Reservoir < Signal > reservoir;
  private              System               system;

  @BeforeEach
  void setup () {

    circuit =
      CORTEX.circuit ();

    final var conduit =
      circuit.conduit (
        Systems::composer
      );

    system =
      conduit.percept (
        NAME
      );

    reservoir =
      conduit.reservoir ();

  }

  // ========== INDIVIDUAL SIGN TESTS ==========

  @Test
  void testAlarm () {

    system.alarm ( FLOW );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signals.size () );
    assertEquals ( ALARM, signals.getFirst ().sign () );
    assertEquals ( FLOW, signals.getFirst ().dimension () );

  }

  @Test
  void testAllDimensions () {

    system.normal ( SPACE );
    system.normal ( FLOW );
    system.normal ( LINK );
    system.normal ( TIME );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 4, signals.size () );
    assertEquals ( SPACE, signals.get ( 0 ).dimension () );
    assertEquals ( FLOW, signals.get ( 1 ).dimension () );
    assertEquals ( LINK, signals.get ( 2 ).dimension () );
    assertEquals ( TIME, signals.get ( 3 ).dimension () );

  }

  @Test
  void testAllSignDimensionCombinations () {

    // Test all 16 combinations (4 signs × 4 dimensions)
    for ( final var sign : Sign.values () ) {
      for ( final var dimension : Dimension.values () ) {
        system.signal ( sign, dimension );
      }
    }

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 16, signals.size () );

    var index = 0;
    for ( final var sign : Sign.values () ) {
      for ( final var dimension : Dimension.values () ) {
        assertEquals ( new Signal ( sign, dimension ), signals.get ( index++ ) );
      }
    }

  }

  @Test
  void testDimensionEnumOrdinals () {

    assertEquals ( 0, SPACE.ordinal () );
    assertEquals ( 1, FLOW.ordinal () );
    assertEquals ( 2, LINK.ordinal () );
    assertEquals ( 3, TIME.ordinal () );

  }

  // ========== ALL DIMENSIONS TESTS ==========

  @Test
  void testDimensionEnumValues () {

    assertEquals ( 4, Dimension.values ().length );

  }

  // ========== SEVERITY PROGRESSION TESTS ==========

  @Test
  void testFault () {

    system.fault ( LINK );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signals.size () );
    assertEquals ( FAULT, signals.getFirst ().sign () );
    assertEquals ( LINK, signals.getFirst ().dimension () );

  }

  // ========== SIGNAL METHOD TEST ==========

  @Test
  void testLimit () {

    system.limit ( TIME );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signals.size () );
    assertEquals ( LIMIT, signals.getFirst ().sign () );
    assertEquals ( TIME, signals.getFirst ().dimension () );

  }

  // ========== ALL COMBINATIONS TEST ==========

  @Test
  void testMultiSystemMonitoring () {

    final var poolName = CORTEX.name ( "db.pool" );
    final var cacheName = CORTEX.name ( "cache.redis" );

    final var conduit = circuit.conduit ( Systems::composer );
    final var poolSystem = conduit.percept ( poolName );
    final var cacheSystem = conduit.percept ( cacheName );
    final var signalReservoir = conduit.reservoir ();

    // Pool space is tight, cache link is down
    poolSystem.limit ( SPACE );
    cacheSystem.fault ( LINK );

    circuit.await ();

    final var allSignals = signalReservoir.drain ().toList ();

    assertEquals ( 2, allSignals.size () );

    // Verify subjects
    assertEquals ( poolName, allSignals.get ( 0 ).subject ().name () );
    assertEquals ( cacheName, allSignals.get ( 1 ).subject ().name () );

    // Verify emissions
    assertEquals ( LIMIT, allSignals.get ( 0 ).emission ().sign () );
    assertEquals ( SPACE, allSignals.get ( 0 ).emission ().dimension () );
    assertEquals ( FAULT, allSignals.get ( 1 ).emission ().sign () );
    assertEquals ( LINK, allSignals.get ( 1 ).emission ().dimension () );

  }

  // ========== SIGNAL CACHING TESTS ==========

  @Test
  void testNormal () {

    system.normal ( SPACE );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signals.size () );
    assertEquals ( NORMAL, signals.getFirst ().sign () );
    assertEquals ( SPACE, signals.getFirst ().dimension () );

  }

  // ========== ENUM STABILITY TESTS ==========

  @Test
  void testSeverityProgression () {

    // Test progression: NORMAL → LIMIT → ALARM → FAULT
    system.normal ( SPACE );
    system.limit ( SPACE );
    system.alarm ( SPACE );
    system.fault ( SPACE );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 4, signals.size () );
    assertEquals ( NORMAL, signals.get ( 0 ).sign () );
    assertEquals ( LIMIT, signals.get ( 1 ).sign () );
    assertEquals ( ALARM, signals.get ( 2 ).sign () );
    assertEquals ( FAULT, signals.get ( 3 ).sign () );

  }

  @Test
  void testSignEnumOrdinals () {

    assertEquals ( 0, NORMAL.ordinal () );
    assertEquals ( 1, LIMIT.ordinal () );
    assertEquals ( 2, ALARM.ordinal () );
    assertEquals ( 3, FAULT.ordinal () );

  }

  @Test
  void testSignEnumValues () {

    assertEquals ( 4, Sign.values ().length );

  }

  @Test
  void testSignal () {

    system.signal ( ALARM, SPACE );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signals.size () );
    assertEquals ( ALARM, signals.getFirst ().sign () );
    assertEquals ( SPACE, signals.getFirst ().dimension () );

  }

  // ========== SUBJECT TESTS ==========

  @Test
  void testSignalCaching () {

    // Emit same signal twice
    system.alarm ( SPACE );
    system.alarm ( SPACE );

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

  // ========== MULTI-SYSTEM TEST ==========

  @Test
  void testSubjectAttachment () {

    system.alarm ( FLOW );

    circuit.await ();

    final var capture =
      reservoir
        .drain ()
        .findFirst ()
        .orElseThrow ();

    assertEquals ( NAME, capture.subject ().name () );
    assertEquals ( ALARM, capture.emission ().sign () );
    assertEquals ( FLOW, capture.emission ().dimension () );

  }

}
