// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis;

import io.humainary.substrates.ext.serventis.ext.Monitors;
import io.humainary.substrates.ext.serventis.ext.Monitors.Dimension;
import io.humainary.substrates.ext.serventis.ext.Monitors.Monitor;
import io.humainary.substrates.ext.serventis.ext.Monitors.Sign;
import io.humainary.substrates.ext.serventis.ext.Monitors.Signal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.humainary.substrates.api.Substrates.*;
import static io.humainary.substrates.ext.serventis.ext.Monitors.Dimension.*;
import static io.humainary.substrates.ext.serventis.ext.Monitors.Sign.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;


/// Tests for the [Monitors] API.
///
/// @author William David Louth
/// @since 1.0

final class MonitorsTest {

  private static final Cortex               CORTEX = cortex ();
  private static final Name                 NAME   = CORTEX.name ( "service.database" );
  private              Circuit              circuit;
  private              Reservoir < Signal > reservoir;
  private              Monitor              monitor;

  private void emitSign (
    final Sign sign,
    final Dimension dimension
  ) {

    switch ( sign ) {
      case CONVERGING -> monitor.converging ( dimension );
      case STABLE -> monitor.stable ( dimension );
      case DIVERGING -> monitor.diverging ( dimension );
      case ERRATIC -> monitor.erratic ( dimension );
      case DEGRADED -> monitor.degraded ( dimension );
      case DEFECTIVE -> monitor.defective ( dimension );
      case DOWN -> monitor.down ( dimension );
    }

  }

  // ========== INDIVIDUAL SIGN TESTS ==========

  @BeforeEach
  void setup () {

    circuit =
      CORTEX.circuit ();

    final var conduit =
      circuit.conduit (
        Monitors::composer
      );

    monitor =
      conduit.percept (
        NAME
      );

    reservoir =
      CORTEX.reservoir (
        conduit
      );

  }

  @Test
  void testConfidenceProgression () {

    // Test progression from TENTATIVE → MEASURED → CONFIRMED
    monitor.degraded ( TENTATIVE );
    monitor.degraded ( MEASURED );
    monitor.degraded ( CONFIRMED );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 3, signals.size () );
    assertEquals ( DEGRADED, signals.get ( 0 ).sign () );
    assertEquals ( TENTATIVE, signals.get ( 0 ).dimension () );
    assertEquals ( DEGRADED, signals.get ( 1 ).sign () );
    assertEquals ( MEASURED, signals.get ( 1 ).dimension () );
    assertEquals ( DEGRADED, signals.get ( 2 ).sign () );
    assertEquals ( CONFIRMED, signals.get ( 2 ).dimension () );

  }

  @Test
  void testConverging () {

    monitor.converging ( MEASURED );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signals.size () );
    assertEquals ( CONVERGING, signals.getFirst ().sign () );
    assertEquals ( MEASURED, signals.getFirst ().dimension () );

  }

  @Test
  void testDefective () {

    monitor.defective ( CONFIRMED );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signals.size () );
    assertEquals ( DEFECTIVE, signals.getFirst ().sign () );
    assertEquals ( CONFIRMED, signals.getFirst ().dimension () );

  }

  @Test
  void testDegradationSequence () {

    // Test operational degradation: STABLE → DIVERGING → DEGRADED → DEFECTIVE → DOWN
    monitor.stable ( CONFIRMED );
    monitor.diverging ( TENTATIVE );
    monitor.diverging ( MEASURED );
    monitor.degraded ( MEASURED );
    monitor.defective ( CONFIRMED );
    monitor.down ( CONFIRMED );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 6, signals.size () );
    assertEquals ( STABLE, signals.get ( 0 ).sign () );
    assertEquals ( DIVERGING, signals.get ( 1 ).sign () );
    assertEquals ( DEGRADED, signals.get ( 3 ).sign () );
    assertEquals ( DEFECTIVE, signals.get ( 4 ).sign () );
    assertEquals ( DOWN, signals.get ( 5 ).sign () );

  }

  @Test
  void testDegraded () {

    monitor.degraded ( TENTATIVE );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signals.size () );
    assertEquals ( DEGRADED, signals.getFirst ().sign () );
    assertEquals ( TENTATIVE, signals.getFirst ().dimension () );

  }

  @Test
  void testDimensionEnumOrdinals () {

    // Ensure ordinals remain stable for compatibility
    assertEquals ( 0, TENTATIVE.ordinal () );
    assertEquals ( 1, MEASURED.ordinal () );
    assertEquals ( 2, CONFIRMED.ordinal () );

  }

  // ========== DIMENSION PROGRESSION TESTS ==========

  @Test
  void testDimensionEnumValues () {

    final var values = Dimension.values ();

    assertEquals ( 3, values.length );

  }

  @Test
  void testDiverging () {

    monitor.diverging ( MEASURED );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signals.size () );
    assertEquals ( DIVERGING, signals.getFirst ().sign () );
    assertEquals ( MEASURED, signals.getFirst ().dimension () );

  }

  // ========== PATTERN TESTS ==========

  @Test
  void testDown () {

    monitor.down ( CONFIRMED );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signals.size () );
    assertEquals ( DOWN, signals.getFirst ().sign () );
    assertEquals ( CONFIRMED, signals.getFirst ().dimension () );

  }

  @Test
  void testErratic () {

    monitor.erratic ( TENTATIVE );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signals.size () );
    assertEquals ( ERRATIC, signals.getFirst ().sign () );
    assertEquals ( TENTATIVE, signals.getFirst ().dimension () );

  }

  // ========== SIGNAL CACHING TESTS ==========

  @Test
  void testMultiServiceMonitoring () {

    // Simulate monitoring multiple services
    final var dbName = CORTEX.name ( "service.database" );
    final var apiName = CORTEX.name ( "service.api" );

    final var conduit = circuit.conduit ( Monitors::composer );
    final var dbMonitor = conduit.percept ( dbName );
    final var apiMonitor = conduit.percept ( apiName );
    final var signalReservoir = CORTEX.reservoir ( conduit );

    // Database degrades
    dbMonitor.stable ( CONFIRMED );
    dbMonitor.degraded ( MEASURED );

    // API remains stable
    apiMonitor.stable ( CONFIRMED );

    circuit.await ();

    final var allSignals = signalReservoir.drain ().toList ();

    assertEquals ( 3, allSignals.size () );

    // Verify subjects
    assertEquals ( dbName, allSignals.get ( 0 ).subject ().name () );
    assertEquals ( dbName, allSignals.get ( 1 ).subject ().name () );
    assertEquals ( apiName, allSignals.get ( 2 ).subject ().name () );

    // Verify emissions
    assertEquals ( STABLE, allSignals.get ( 0 ).emission ().sign () );
    assertEquals ( DEGRADED, allSignals.get ( 1 ).emission ().sign () );
    assertEquals ( STABLE, allSignals.get ( 2 ).emission ().sign () );

  }

  @Test
  void testRecoverySequence () {

    // Test recovery: DEFECTIVE → DEGRADED → CONVERGING → STABLE
    monitor.defective ( CONFIRMED );
    monitor.degraded ( MEASURED );
    monitor.converging ( MEASURED );
    monitor.stable ( CONFIRMED );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 4, signals.size () );
    assertEquals ( DEFECTIVE, signals.get ( 0 ).sign () );
    assertEquals ( DEGRADED, signals.get ( 1 ).sign () );
    assertEquals ( CONVERGING, signals.get ( 2 ).sign () );
    assertEquals ( STABLE, signals.get ( 3 ).sign () );

  }

  // ========== ENUM STABILITY TESTS ==========

  @Test
  void testSignEnumOrdinals () {

    // Ensure ordinals remain stable for compatibility
    assertEquals ( 0, CONVERGING.ordinal () );
    assertEquals ( 1, STABLE.ordinal () );
    assertEquals ( 2, DIVERGING.ordinal () );
    assertEquals ( 3, ERRATIC.ordinal () );
    assertEquals ( 4, DEGRADED.ordinal () );
    assertEquals ( 5, DEFECTIVE.ordinal () );
    assertEquals ( 6, DOWN.ordinal () );

  }

  @Test
  void testSignEnumValues () {

    final var values = Sign.values ();

    assertEquals ( 7, values.length );

  }

  @Test
  void testSignal () {

    // Test direct signal() method for all sign and dimension combinations
    monitor.signal ( CONVERGING, TENTATIVE );
    monitor.signal ( CONVERGING, MEASURED );
    monitor.signal ( CONVERGING, CONFIRMED );
    monitor.signal ( STABLE, TENTATIVE );
    monitor.signal ( STABLE, MEASURED );
    monitor.signal ( STABLE, CONFIRMED );
    monitor.signal ( DIVERGING, TENTATIVE );
    monitor.signal ( DIVERGING, MEASURED );
    monitor.signal ( DIVERGING, CONFIRMED );
    monitor.signal ( ERRATIC, TENTATIVE );
    monitor.signal ( ERRATIC, MEASURED );
    monitor.signal ( ERRATIC, CONFIRMED );
    monitor.signal ( DEGRADED, TENTATIVE );
    monitor.signal ( DEGRADED, MEASURED );
    monitor.signal ( DEGRADED, CONFIRMED );
    monitor.signal ( DEFECTIVE, TENTATIVE );
    monitor.signal ( DEFECTIVE, MEASURED );
    monitor.signal ( DEFECTIVE, CONFIRMED );
    monitor.signal ( DOWN, TENTATIVE );
    monitor.signal ( DOWN, MEASURED );
    monitor.signal ( DOWN, CONFIRMED );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 21, signals.size () );
    assertEquals ( new Signal ( CONVERGING, TENTATIVE ), signals.get ( 0 ) );
    assertEquals ( new Signal ( CONVERGING, MEASURED ), signals.get ( 1 ) );
    assertEquals ( new Signal ( CONVERGING, CONFIRMED ), signals.get ( 2 ) );
    assertEquals ( new Signal ( STABLE, TENTATIVE ), signals.get ( 3 ) );
    assertEquals ( new Signal ( STABLE, MEASURED ), signals.get ( 4 ) );
    assertEquals ( new Signal ( STABLE, CONFIRMED ), signals.get ( 5 ) );
    assertEquals ( new Signal ( DIVERGING, TENTATIVE ), signals.get ( 6 ) );
    assertEquals ( new Signal ( DIVERGING, MEASURED ), signals.get ( 7 ) );
    assertEquals ( new Signal ( DIVERGING, CONFIRMED ), signals.get ( 8 ) );
    assertEquals ( new Signal ( ERRATIC, TENTATIVE ), signals.get ( 9 ) );
    assertEquals ( new Signal ( ERRATIC, MEASURED ), signals.get ( 10 ) );
    assertEquals ( new Signal ( ERRATIC, CONFIRMED ), signals.get ( 11 ) );
    assertEquals ( new Signal ( DEGRADED, TENTATIVE ), signals.get ( 12 ) );
    assertEquals ( new Signal ( DEGRADED, MEASURED ), signals.get ( 13 ) );
    assertEquals ( new Signal ( DEGRADED, CONFIRMED ), signals.get ( 14 ) );
    assertEquals ( new Signal ( DEFECTIVE, TENTATIVE ), signals.get ( 15 ) );
    assertEquals ( new Signal ( DEFECTIVE, MEASURED ), signals.get ( 16 ) );
    assertEquals ( new Signal ( DEFECTIVE, CONFIRMED ), signals.get ( 17 ) );
    assertEquals ( new Signal ( DOWN, TENTATIVE ), signals.get ( 18 ) );
    assertEquals ( new Signal ( DOWN, MEASURED ), signals.get ( 19 ) );
    assertEquals ( new Signal ( DOWN, CONFIRMED ), signals.get ( 20 ) );

  }

  @Test
  void testSignalCaching () {

    // Emit same signal twice
    monitor.stable ( CONFIRMED );
    monitor.stable ( CONFIRMED );

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

  // ========== SUBJECT TESTS ==========

  @Test
  void testSignalCachingAllCombinations () {

    // Test that all 21 combinations (7 signs × 3 dimensions) use cached instances
    final var firstPass = new Signal[7][3];
    final var secondPass = new Signal[7][3];

    final var signs = Sign.values ();
    final var dimensions = Dimension.values ();

    // First pass - collect signals
    for ( final var sign : signs ) {
      for ( final var dimension : dimensions ) {
        emitSign ( sign, dimension );
      }
    }

    circuit.await ();

    var captures = reservoir.drain ().toList ();
    var index = 0;
    for ( final var sign : signs ) {
      for ( final var dimension : dimensions ) {
        firstPass[sign.ordinal ()][dimension.ordinal ()] = captures.get ( index++ ).emission ();
      }
    }

    // Second pass - collect signals again
    for ( final var sign : signs ) {
      for ( final var dimension : dimensions ) {
        emitSign ( sign, dimension );
      }
    }

    circuit.await ();

    captures = reservoir.drain ().toList ();
    index = 0;
    for ( final var sign : signs ) {
      for ( final var dimension : dimensions ) {
        secondPass[sign.ordinal ()][dimension.ordinal ()] = captures.get ( index++ ).emission ();
      }
    }

    // Verify all instances are cached (same reference)
    for ( final var sign : signs ) {
      for ( final var dimension : dimensions ) {
        assertSame (
          firstPass[sign.ordinal ()][dimension.ordinal ()],
          secondPass[sign.ordinal ()][dimension.ordinal ()],
          "Signal should be cached for " + sign + " × " + dimension
        );
      }
    }

  }

  // ========== HELPER METHODS ==========

  @Test
  void testStable () {

    monitor.stable ( CONFIRMED );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signals.size () );
    assertEquals ( STABLE, signals.getFirst ().sign () );
    assertEquals ( CONFIRMED, signals.getFirst ().dimension () );

  }

  @Test
  void testSubjectAttachment () {

    monitor.stable ( CONFIRMED );

    circuit.await ();

    final var capture =
      reservoir
        .drain ()
        .findFirst ()
        .orElseThrow ();

    assertEquals ( NAME, capture.subject ().name () );
    assertEquals ( STABLE, capture.emission ().sign () );
    assertEquals ( CONFIRMED, capture.emission ().dimension () );

  }

}
