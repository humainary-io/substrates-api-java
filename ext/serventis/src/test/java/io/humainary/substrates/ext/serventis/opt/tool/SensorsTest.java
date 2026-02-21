// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.opt.tool;

import io.humainary.substrates.ext.serventis.opt.tool.Sensors.Dimension;
import io.humainary.substrates.ext.serventis.opt.tool.Sensors.Sensor;
import io.humainary.substrates.ext.serventis.opt.tool.Sensors.Sign;
import io.humainary.substrates.ext.serventis.opt.tool.Sensors.Signal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.humainary.substrates.api.Substrates.*;
import static io.humainary.substrates.ext.serventis.opt.tool.Sensors.Dimension.*;
import static io.humainary.substrates.ext.serventis.opt.tool.Sensors.Sign.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;


/// Tests for the [Sensors] API.
///
/// @author William David Louth
/// @since 1.0

final class SensorsTest {

  private static final Cortex               CORTEX = cortex ();
  private static final Name                 NAME   = CORTEX.name ( "latency.p95" );
  private              Circuit              circuit;
  private              Reservoir < Signal > reservoir;
  private              Sensor               sensor;

  private void emitSign (
    final Sign sign,
    final Dimension dimension
  ) {

    switch ( sign ) {
      case BELOW -> sensor.below ( dimension );
      case NOMINAL -> sensor.nominal ( dimension );
      case ABOVE -> sensor.above ( dimension );
    }

  }

  // ========== INDIVIDUAL SIGN TESTS ==========

  @BeforeEach
  void setup () {

    circuit =
      CORTEX.circuit ();

    final var conduit =
      circuit.conduit (
        Sensors::composer
      );

    sensor =
      conduit.percept (
        NAME
      );

    reservoir =
      conduit.reservoir ();

  }

  @Test
  void testAbove () {

    sensor.above ( BASELINE );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signals.size () );
    assertEquals ( ABOVE, signals.getFirst ().sign () );
    assertEquals ( BASELINE, signals.getFirst ().dimension () );

  }

  @Test
  void testAboveAllDimensions () {

    // Test ABOVE with all three dimension types
    sensor.above ( BASELINE );
    sensor.above ( THRESHOLD );
    sensor.above ( TARGET );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 3, signals.size () );
    assertEquals ( ABOVE, signals.get ( 0 ).sign () );
    assertEquals ( BASELINE, signals.get ( 0 ).dimension () );
    assertEquals ( ABOVE, signals.get ( 1 ).sign () );
    assertEquals ( THRESHOLD, signals.get ( 1 ).dimension () );
    assertEquals ( ABOVE, signals.get ( 2 ).sign () );
    assertEquals ( TARGET, signals.get ( 2 ).dimension () );

  }

  @Test
  void testBelow () {

    sensor.below ( THRESHOLD );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signals.size () );
    assertEquals ( BELOW, signals.getFirst ().sign () );
    assertEquals ( THRESHOLD, signals.getFirst ().dimension () );

  }

  @Test
  void testBelowAllDimensions () {

    // Test BELOW with all three dimension types
    sensor.below ( BASELINE );
    sensor.below ( THRESHOLD );
    sensor.below ( TARGET );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 3, signals.size () );
    assertEquals ( BELOW, signals.get ( 0 ).sign () );
    assertEquals ( BASELINE, signals.get ( 0 ).dimension () );
    assertEquals ( BELOW, signals.get ( 1 ).sign () );
    assertEquals ( THRESHOLD, signals.get ( 1 ).dimension () );
    assertEquals ( BELOW, signals.get ( 2 ).sign () );
    assertEquals ( TARGET, signals.get ( 2 ).dimension () );

  }

  @Test
  void testDimensionEnumOrdinals () {

    // Ensure ordinals remain stable for compatibility
    assertEquals ( 0, BASELINE.ordinal () );
    assertEquals ( 1, THRESHOLD.ordinal () );
    assertEquals ( 2, TARGET.ordinal () );

  }

  // ========== DIMENSION TESTS ==========

  @Test
  void testDimensionEnumValues () {

    final var values = Dimension.values ();

    assertEquals ( 3, values.length );
    assertEquals ( BASELINE, values[0] );
    assertEquals ( THRESHOLD, values[1] );
    assertEquals ( TARGET, values[2] );

  }

  // ========== PATTERN TESTS ==========

  @Test
  void testLatencyOptimizationPattern () {

    // Scenario: Latency improving over time
    // target=20ms, baseline=50ms, threshold=500ms
    // Current latency: 450ms → 100ms → 45ms → 15ms

    // 450ms: approaching threshold but above baseline
    sensor.above ( BASELINE );
    sensor.below ( THRESHOLD );

    // 100ms: still above baseline, well below threshold
    sensor.above ( BASELINE );
    sensor.below ( THRESHOLD );

    // 45ms: near baseline
    sensor.nominal ( BASELINE );
    sensor.below ( THRESHOLD );

    // 15ms: below target (excellent!)
    sensor.below ( TARGET );
    sensor.below ( BASELINE );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 8, signals.size () );

  }

  @Test
  void testMultiServiceMonitoring () {

    // Simulate monitoring multiple metrics
    final var latencyName = CORTEX.name ( "latency.p95" );
    final var queueName = CORTEX.name ( "queue.depth" );

    final var conduit = circuit.conduit ( Sensors::composer );
    final var latencySensor = conduit.percept ( latencyName );
    final var queueSensor = conduit.percept ( queueName );
    final var signalReservoir = conduit.reservoir ();

    // Latency is above baseline but below threshold
    latencySensor.above ( BASELINE );
    latencySensor.below ( THRESHOLD );

    // Queue depth exceeds threshold (critical!)
    queueSensor.above ( THRESHOLD );

    circuit.await ();

    final var allSignals = signalReservoir.drain ().toList ();

    assertEquals ( 3, allSignals.size () );

    // Verify subjects
    assertEquals ( latencyName, allSignals.get ( 0 ).subject ().name () );
    assertEquals ( latencyName, allSignals.get ( 1 ).subject ().name () );
    assertEquals ( queueName, allSignals.get ( 2 ).subject ().name () );

    // Verify emissions
    assertEquals ( ABOVE, allSignals.get ( 0 ).emission ().sign () );
    assertEquals ( BASELINE, allSignals.get ( 0 ).emission ().dimension () );
    assertEquals ( BELOW, allSignals.get ( 1 ).emission ().sign () );
    assertEquals ( THRESHOLD, allSignals.get ( 1 ).emission ().dimension () );
    assertEquals ( ABOVE, allSignals.get ( 2 ).emission ().sign () );
    assertEquals ( THRESHOLD, allSignals.get ( 2 ).emission ().dimension () );

  }

  @Test
  void testNominal () {

    sensor.nominal ( TARGET );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signals.size () );
    assertEquals ( NOMINAL, signals.getFirst ().sign () );
    assertEquals ( TARGET, signals.getFirst ().dimension () );

  }

  @Test
  void testNominalAllDimensions () {

    // Test NOMINAL with all three dimension types
    sensor.nominal ( BASELINE );
    sensor.nominal ( THRESHOLD );
    sensor.nominal ( TARGET );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 3, signals.size () );
    assertEquals ( NOMINAL, signals.get ( 0 ).sign () );
    assertEquals ( BASELINE, signals.get ( 0 ).dimension () );
    assertEquals ( NOMINAL, signals.get ( 1 ).sign () );
    assertEquals ( THRESHOLD, signals.get ( 1 ).dimension () );
    assertEquals ( NOMINAL, signals.get ( 2 ).sign () );
    assertEquals ( TARGET, signals.get ( 2 ).dimension () );

  }

  @Test
  void testQueueDepthPattern () {

    // Scenario: Queue depth increasing over time
    // baseline=100, threshold=1000
    // Depth progression: 50 → 850 → 1200

    // 50 items: below both baseline and threshold (light load)
    sensor.below ( BASELINE );
    sensor.below ( THRESHOLD );

    // 850 items: above baseline, below threshold (busy but safe)
    sensor.above ( BASELINE );
    sensor.below ( THRESHOLD );

    // 1200 items: exceeds threshold (overflow!)
    sensor.above ( BASELINE );
    sensor.above ( THRESHOLD );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 6, signals.size () );
    // First measurement
    assertEquals ( BELOW, signals.get ( 0 ).sign () );
    assertEquals ( BASELINE, signals.get ( 0 ).dimension () );
    assertEquals ( BELOW, signals.get ( 1 ).sign () );
    assertEquals ( THRESHOLD, signals.get ( 1 ).dimension () );
    // Second measurement
    assertEquals ( ABOVE, signals.get ( 2 ).sign () );
    assertEquals ( BASELINE, signals.get ( 2 ).dimension () );
    assertEquals ( BELOW, signals.get ( 3 ).sign () );
    assertEquals ( THRESHOLD, signals.get ( 3 ).dimension () );
    // Third measurement
    assertEquals ( ABOVE, signals.get ( 4 ).sign () );
    assertEquals ( BASELINE, signals.get ( 4 ).dimension () );
    assertEquals ( ABOVE, signals.get ( 5 ).sign () );
    assertEquals ( THRESHOLD, signals.get ( 5 ).dimension () );

  }

  // ========== ENUM STABILITY TESTS ==========

  @Test
  void testSignEnumOrdinals () {

    // Ensure ordinals remain stable for compatibility
    assertEquals ( 0, BELOW.ordinal () );
    assertEquals ( 1, NOMINAL.ordinal () );
    assertEquals ( 2, ABOVE.ordinal () );

  }

  @Test
  void testSignEnumValues () {

    final var values = Sign.values ();

    assertEquals ( 3, values.length );
    assertEquals ( BELOW, values[0] );
    assertEquals ( NOMINAL, values[1] );
    assertEquals ( ABOVE, values[2] );

  }

  // ========== SIGNAL CACHING TESTS ==========

  @Test
  void testSignal () {

    // Test direct signal() method for all sign and dimension combinations
    sensor.signal ( BELOW, BASELINE );
    sensor.signal ( BELOW, THRESHOLD );
    sensor.signal ( BELOW, TARGET );
    sensor.signal ( NOMINAL, BASELINE );
    sensor.signal ( NOMINAL, THRESHOLD );
    sensor.signal ( NOMINAL, TARGET );
    sensor.signal ( ABOVE, BASELINE );
    sensor.signal ( ABOVE, THRESHOLD );
    sensor.signal ( ABOVE, TARGET );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 9, signals.size () );
    assertEquals ( new Signal ( BELOW, BASELINE ), signals.get ( 0 ) );
    assertEquals ( new Signal ( BELOW, THRESHOLD ), signals.get ( 1 ) );
    assertEquals ( new Signal ( BELOW, TARGET ), signals.get ( 2 ) );
    assertEquals ( new Signal ( NOMINAL, BASELINE ), signals.get ( 3 ) );
    assertEquals ( new Signal ( NOMINAL, THRESHOLD ), signals.get ( 4 ) );
    assertEquals ( new Signal ( NOMINAL, TARGET ), signals.get ( 5 ) );
    assertEquals ( new Signal ( ABOVE, BASELINE ), signals.get ( 6 ) );
    assertEquals ( new Signal ( ABOVE, THRESHOLD ), signals.get ( 7 ) );
    assertEquals ( new Signal ( ABOVE, TARGET ), signals.get ( 8 ) );

  }

  @Test
  void testSignalCaching () {

    // Emit same signal twice
    sensor.above ( BASELINE );
    sensor.above ( BASELINE );

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

    // Test that all 9 combinations (3 signs × 3 dimensions) use cached instances
    final var firstPass = new Signal[3][3];
    final var secondPass = new Signal[3][3];

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

  // ========== SUBJECT TESTS ==========

  @Test
  void testSubjectAttachment () {

    sensor.above ( BASELINE );

    circuit.await ();

    final var capture =
      reservoir
        .drain ()
        .findFirst ()
        .orElseThrow ();

    assertEquals ( NAME, capture.subject ().name () );
    assertEquals ( ABOVE, capture.emission ().sign () );
    assertEquals ( BASELINE, capture.emission ().dimension () );

  }

}
