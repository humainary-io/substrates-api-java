// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.opt.exec;

import io.humainary.substrates.ext.serventis.opt.exec.Timers.Dimension;
import io.humainary.substrates.ext.serventis.opt.exec.Timers.Sign;
import io.humainary.substrates.ext.serventis.opt.exec.Timers.Signal;
import io.humainary.substrates.ext.serventis.opt.exec.Timers.Timer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.humainary.substrates.api.Substrates.*;
import static io.humainary.substrates.ext.serventis.opt.exec.Timers.Dimension.DEADLINE;
import static io.humainary.substrates.ext.serventis.opt.exec.Timers.Dimension.THRESHOLD;
import static org.junit.jupiter.api.Assertions.assertEquals;


/// The test class for the [Timer] interface.
///
/// @author William David Louth
/// @since 1.0
final class TimersTest {

  private static final Cortex               cortex = cortex ();
  private static final Name                 NAME   = cortex.name ( "api.latency" );
  private              Circuit              circuit;
  private              Reservoir < Signal > reservoir;
  private              Timer                timer;

  private void assertSignal (
    final Signal signal
  ) {

    circuit
      .await ();

    assertEquals (
      1L, reservoir
        .drain ()
        .filter ( capture -> capture.subject ().name () == NAME )
        .map ( Capture::emission )
        .filter ( s -> s.equals ( signal ) )
        .count ()

    );

  }

  private void emit (
    final Signal signal,
    final Runnable emitter
  ) {

    emitter.run ();

    assertSignal (
      signal
    );

  }

  @BeforeEach
  void setup () {

    circuit =
      cortex ().circuit ();

    final var conduit =
      circuit.conduit (
        Timers::composer
      );

    timer =
      conduit.percept (
        NAME
      );

    reservoir =
      conduit.reservoir ();

  }

  @Test
  void testDimensionEnumOrdinals () {

    assertEquals ( 0, DEADLINE.ordinal () );
    assertEquals ( 1, THRESHOLD.ordinal () );

  }

  @Test
  void testDimensionEnumValues () {

    final var values = Dimension.values ();

    assertEquals ( 2, values.length );

  }

  @Test
  void testMeetDeadline () {

    emit ( new Signal ( Sign.MEET, DEADLINE ), () -> timer.meet ( DEADLINE ) );

  }

  @Test
  void testMeetThreshold () {

    emit ( new Signal ( Sign.MEET, THRESHOLD ), () -> timer.meet ( THRESHOLD ) );

  }

  @Test
  void testMissDeadline () {

    emit ( new Signal ( Sign.MISS, DEADLINE ), () -> timer.miss ( DEADLINE ) );

  }

  @Test
  void testMissThreshold () {

    emit ( new Signal ( Sign.MISS, THRESHOLD ), () -> timer.miss ( THRESHOLD ) );

  }

  @Test
  void testMultipleEmissions () {

    timer.meet ( THRESHOLD );
    timer.meet ( THRESHOLD );
    timer.miss ( THRESHOLD );
    timer.meet ( DEADLINE );

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
  void testSignal () {

    // Test direct signal() method for all sign and dimension combinations
    timer.signal ( Sign.MEET, DEADLINE );
    timer.signal ( Sign.MEET, THRESHOLD );
    timer.signal ( Sign.MISS, DEADLINE );
    timer.signal ( Sign.MISS, THRESHOLD );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 4, signals.size () );
    assertEquals ( new Signal ( Sign.MEET, DEADLINE ), signals.get ( 0 ) );
    assertEquals ( new Signal ( Sign.MEET, THRESHOLD ), signals.get ( 1 ) );
    assertEquals ( new Signal ( Sign.MISS, DEADLINE ), signals.get ( 2 ) );
    assertEquals ( new Signal ( Sign.MISS, THRESHOLD ), signals.get ( 3 ) );

  }

  @Test
  void testSignalAccessors () {

    final var meetDeadline = new Signal ( Sign.MEET, DEADLINE );
    assertEquals ( Sign.MEET, meetDeadline.sign () );
    assertEquals ( DEADLINE, meetDeadline.dimension () );

    final var meetThreshold = new Signal ( Sign.MEET, THRESHOLD );
    assertEquals ( Sign.MEET, meetThreshold.sign () );
    assertEquals ( THRESHOLD, meetThreshold.dimension () );

    final var missDeadline = new Signal ( Sign.MISS, DEADLINE );
    assertEquals ( Sign.MISS, missDeadline.sign () );
    assertEquals ( DEADLINE, missDeadline.dimension () );

    final var missThreshold = new Signal ( Sign.MISS, THRESHOLD );
    assertEquals ( Sign.MISS, missThreshold.sign () );
    assertEquals ( THRESHOLD, missThreshold.dimension () );

  }

  @Test
  void testSignalCoverage () {

    // 2 signs × 2 dimensions = 4 signal combinations
    assertEquals ( 2, Sign.values ().length );
    assertEquals ( 2, Dimension.values ().length );

  }

  @Test
  void testSignEnumOrdinals () {

    // Ensure ordinals remain stable for compatibility
    assertEquals ( 0, Sign.MEET.ordinal () );
    assertEquals ( 1, Sign.MISS.ordinal () );

  }

  @Test
  void testSignEnumValues () {

    final var values = Sign.values ();

    assertEquals ( 2, values.length );

  }

  @Test
  void testSubjectAttachment () {

    timer.meet ( THRESHOLD );

    circuit.await ();

    final var capture =
      reservoir
        .drain ()
        .findFirst ()
        .orElseThrow ();

    assertEquals ( NAME, capture.subject ().name () );
    assertEquals ( new Signal ( Sign.MEET, THRESHOLD ), capture.emission () );

  }

}
