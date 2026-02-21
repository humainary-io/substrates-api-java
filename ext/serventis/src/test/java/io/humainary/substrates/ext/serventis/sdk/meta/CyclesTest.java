// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.sdk.meta;

import io.humainary.substrates.ext.serventis.opt.exec.Tasks;
import io.humainary.substrates.ext.serventis.opt.pool.Resources;
import io.humainary.substrates.ext.serventis.sdk.meta.Cycles.Cycle;
import io.humainary.substrates.ext.serventis.sdk.meta.Cycles.Dimension;
import io.humainary.substrates.ext.serventis.sdk.meta.Cycles.Signal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.humainary.substrates.api.Substrates.*;
import static io.humainary.substrates.ext.serventis.opt.pool.Resources.Sign.*;
import static io.humainary.substrates.ext.serventis.sdk.meta.Cycles.Dimension.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;


/// Tests for the [Cycles] API.
///
/// @author William David Louth
/// @since 1.0

final class CyclesTest {

  private static final Cortex CORTEX = cortex ();
  private static final Name   NAME   = CORTEX.name ( "resource.cycles" );

  private Circuit                                 circuit;
  private Reservoir < Signal < Resources.Sign > > reservoir;
  private Cycle < Resources.Sign >                cycle;

  @BeforeEach
  void setup () {

    circuit =
      CORTEX.circuit ();

    final var conduit =
      circuit.conduit (
        Cycles.composer (
          Resources.Sign.class
        )
      );

    cycle =
      conduit.percept (
        NAME
      );

    reservoir =
      conduit.reservoir ();

  }

  // ========== INDIVIDUAL DIMENSION TESTS ==========

  @Test
  void testConsecutiveRepeats () {

    // Simulate: DENY, DENY, DENY, DENY
    cycle.signal ( DENY, SINGLE );
    cycle.signal ( DENY, REPEAT );
    cycle.signal ( DENY, REPEAT );
    cycle.signal ( DENY, REPEAT );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 4, signals.size () );
    assertEquals ( DENY, signals.get ( 0 ).sign () );
    assertEquals ( SINGLE, signals.get ( 0 ).dimension () );
    assertEquals ( DENY, signals.get ( 1 ).sign () );
    assertEquals ( REPEAT, signals.get ( 1 ).dimension () );
    assertEquals ( DENY, signals.get ( 2 ).sign () );
    assertEquals ( REPEAT, signals.get ( 2 ).dimension () );
    assertEquals ( DENY, signals.get ( 3 ).sign () );
    assertEquals ( REPEAT, signals.get ( 3 ).dimension () );

  }

  @Test
  void testCyclePattern () {

    // Simulate: GRANT, GRANT, DENY, GRANT
    cycle.signal ( GRANT, SINGLE );   // First GRANT
    cycle.signal ( GRANT, REPEAT );   // GRANT immediately after GRANT
    cycle.signal ( DENY, SINGLE );    // First DENY
    cycle.signal ( GRANT, RETURN );   // GRANT returns after DENY

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 4, signals.size () );
    assertEquals ( GRANT, signals.get ( 0 ).sign () );
    assertEquals ( SINGLE, signals.get ( 0 ).dimension () );
    assertEquals ( GRANT, signals.get ( 1 ).sign () );
    assertEquals ( REPEAT, signals.get ( 1 ).dimension () );
    assertEquals ( DENY, signals.get ( 2 ).sign () );
    assertEquals ( SINGLE, signals.get ( 2 ).dimension () );
    assertEquals ( GRANT, signals.get ( 3 ).sign () );
    assertEquals ( RETURN, signals.get ( 3 ).dimension () );

  }

  @Test
  void testDimensionEnumOrdinals () {

    // Ensure ordinals remain stable for compatibility
    assertEquals ( 0, SINGLE.ordinal () );
    assertEquals ( 1, REPEAT.ordinal () );
    assertEquals ( 2, RETURN.ordinal () );

  }

  // ========== PATTERN TESTS ==========

  @Test
  void testDimensionEnumValues () {

    final var values = Dimension.values ();

    assertEquals ( 3, values.length );

  }

  @Test
  void testMultipleReturns () {

    // Simulate: GRANT, DENY, GRANT, TIMEOUT, GRANT
    cycle.signal ( GRANT, SINGLE );
    cycle.signal ( DENY, SINGLE );
    cycle.signal ( GRANT, RETURN );
    cycle.signal ( TIMEOUT, SINGLE );
    cycle.signal ( GRANT, RETURN );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 5, signals.size () );
    assertEquals ( GRANT, signals.get ( 0 ).sign () );
    assertEquals ( SINGLE, signals.get ( 0 ).dimension () );
    assertEquals ( DENY, signals.get ( 1 ).sign () );
    assertEquals ( SINGLE, signals.get ( 1 ).dimension () );
    assertEquals ( GRANT, signals.get ( 2 ).sign () );
    assertEquals ( RETURN, signals.get ( 2 ).dimension () );
    assertEquals ( TIMEOUT, signals.get ( 3 ).sign () );
    assertEquals ( SINGLE, signals.get ( 3 ).dimension () );
    assertEquals ( GRANT, signals.get ( 4 ).sign () );
    assertEquals ( RETURN, signals.get ( 4 ).dimension () );

  }

  @Test
  void testMultipleSubjects () {

    final var name1 = CORTEX.name ( "resource.pool1.cycles" );
    final var name2 = CORTEX.name ( "resource.pool2.cycles" );

    final var conduit = circuit.conduit ( Cycles.composer ( Resources.Sign.class ) );
    final var cycle1 = conduit.percept ( name1 );
    final var cycle2 = conduit.percept ( name2 );
    final var signalReservoir = conduit.reservoir ();

    cycle1.signal ( GRANT, SINGLE );
    cycle2.signal ( DENY, SINGLE );
    cycle1.signal ( GRANT, REPEAT );

    circuit.await ();

    final var allSignals = signalReservoir.drain ().toList ();

    assertEquals ( 3, allSignals.size () );
    assertEquals ( name1, allSignals.get ( 0 ).subject ().name () );
    assertEquals ( name2, allSignals.get ( 1 ).subject ().name () );
    assertEquals ( name1, allSignals.get ( 2 ).subject ().name () );

  }

  // ========== ENUM STABILITY TESTS ==========

  @Test
  void testRepeat () {

    cycle.signal ( GRANT, REPEAT );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signals.size () );
    assertEquals ( GRANT, signals.getFirst ().sign () );
    assertEquals ( REPEAT, signals.getFirst ().dimension () );

  }

  @Test
  void testReturn () {

    cycle.signal ( GRANT, RETURN );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signals.size () );
    assertEquals ( GRANT, signals.getFirst ().sign () );
    assertEquals ( RETURN, signals.getFirst ().dimension () );

  }

  // ========== SIGNAL CACHING TESTS ==========

  @Test
  void testSignalCaching () {

    // Emit same signal twice
    cycle.signal ( GRANT, SINGLE );
    cycle.signal ( GRANT, SINGLE );

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

    // First pass - emit for GRANT
    for ( final var dimension : dimensions ) {
      cycle.signal ( GRANT, dimension );
    }

    circuit.await ();

    final var firstPass =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    // Second pass - emit same signals
    for ( final var dimension : dimensions ) {
      cycle.signal ( GRANT, dimension );
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
        "Signal should be cached for GRANT × " + dimensions[i]
      );
    }

  }

  // ========== SUBJECT TESTS ==========

  @Test
  void testSingle () {

    cycle.signal ( GRANT, SINGLE );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signals.size () );
    assertEquals ( GRANT, signals.getFirst ().sign () );
    assertEquals ( SINGLE, signals.getFirst ().dimension () );

  }

  @Test
  void testSubjectAttachment () {

    cycle.signal ( GRANT, SINGLE );

    circuit.await ();

    final var capture =
      reservoir
        .drain ()
        .findFirst ()
        .orElseThrow ();

    assertEquals ( NAME, capture.subject ().name () );
    assertEquals ( GRANT, capture.emission ().sign () );
    assertEquals ( SINGLE, capture.emission ().dimension () );

  }

  // ========== GENERIC SIGN TYPE TESTS ==========

  @Test
  void testWithDifferentSignType () {

    // Test that Cycles works with different Sign types
    final var taskConduit =
      circuit.conduit (
        Cycles.composer (
          Tasks.Sign.class
        )
      );

    final var taskCycle =
      taskConduit.percept (
        CORTEX.name ( "task.cycles" )
      );

    final var taskReservoir =
      taskConduit.reservoir ();

    taskCycle.signal (
      Tasks.Sign.SUBMIT,
      SINGLE
    );

    taskCycle.signal (
      Tasks.Sign.COMPLETE,
      SINGLE
    );

    taskCycle.signal (
      Tasks.Sign.SUBMIT,
      RETURN
    );

    circuit.await ();

    final var signals =
      taskReservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 3, signals.size () );
    assertEquals (
      Tasks.Sign.SUBMIT,
      signals.get ( 0 ).sign ()
    );
    assertEquals (
      Tasks.Sign.COMPLETE,
      signals.get ( 1 ).sign ()
    );
    assertEquals (
      Tasks.Sign.SUBMIT,
      signals.get ( 2 ).sign ()
    );

  }

}
