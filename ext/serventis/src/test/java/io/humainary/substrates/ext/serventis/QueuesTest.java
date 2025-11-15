// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis;

import io.humainary.substrates.ext.serventis.ext.Queues;
import io.humainary.substrates.ext.serventis.ext.Queues.Queue;
import io.humainary.substrates.ext.serventis.ext.Queues.Sign;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.humainary.substrates.api.Substrates.*;
import static io.humainary.substrates.ext.serventis.ext.Queues.Sign.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


/// Tests for the [Queues] API.
///
/// @author William David Louth
/// @since 1.0

final class QueuesTest {

  private static final Cortex             CORTEX = cortex ();
  private static final Name               NAME   = CORTEX.name ( "worker.queue" );
  private              Circuit            circuit;
  private              Reservoir < Sign > reservoir;
  private              Queue              queue;

  @BeforeEach
  void setup () {

    circuit =
      CORTEX.circuit ();

    final var conduit =
      circuit.conduit (
        Queues::composer
      );

    queue =
      conduit.percept (
        NAME
      );

    reservoir =
      CORTEX.reservoir (
        conduit
      );

  }

  @Test
  void testBoundaryConditions () {

    queue.enqueue ();
    queue.overflow ();
    queue.dequeue ();
    queue.underflow ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 4, signs.size () );
    assertEquals ( ENQUEUE, signs.getFirst () );
    assertEquals ( OVERFLOW, signs.get ( 1 ) );
    assertEquals ( DEQUEUE, signs.get ( 2 ) );
    assertEquals ( UNDERFLOW, signs.get ( 3 ) );

  }

  @Test
  void testDequeue () {

    queue.dequeue ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( DEQUEUE, signs.getFirst () );

  }

  @Test
  void testEnqueue () {

    queue.enqueue ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( ENQUEUE, signs.getFirst () );

  }

  @Test
  void testOverflow () {

    queue.overflow ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( OVERFLOW, signs.getFirst () );

  }

  @Test
  void testOverflowPattern () {

    queue.enqueue ();
    queue.enqueue ();
    queue.overflow ();
    queue.overflow ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 4, signs.size () );
    assertEquals ( ENQUEUE, signs.getFirst () );
    assertEquals ( ENQUEUE, signs.get ( 1 ) );
    assertEquals ( OVERFLOW, signs.get ( 2 ) );
    assertEquals ( OVERFLOW, signs.get ( 3 ) );

  }

  @Test
  void testQueueLifecycle () {

    queue.enqueue ();
    queue.enqueue ();
    queue.dequeue ();
    queue.enqueue ();
    queue.dequeue ();
    queue.dequeue ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 6, signs.size () );
    assertEquals ( ENQUEUE, signs.getFirst () );
    assertEquals ( ENQUEUE, signs.get ( 1 ) );
    assertEquals ( DEQUEUE, signs.get ( 2 ) );
    assertEquals ( ENQUEUE, signs.get ( 3 ) );
    assertEquals ( DEQUEUE, signs.get ( 4 ) );
    assertEquals ( DEQUEUE, signs.get ( 5 ) );

  }

  @Test
  void testSign () {

    // Test direct sign() method for all sign values
    queue.sign ( ENQUEUE );
    queue.sign ( DEQUEUE );
    queue.sign ( OVERFLOW );
    queue.sign ( UNDERFLOW );

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 4, signs.size () );
    assertEquals ( ENQUEUE, signs.get ( 0 ) );
    assertEquals ( DEQUEUE, signs.get ( 1 ) );
    assertEquals ( OVERFLOW, signs.get ( 2 ) );
    assertEquals ( UNDERFLOW, signs.get ( 3 ) );

  }

  @Test
  void testSignEnumOrdinals () {

    // Ensure ordinals remain stable for compatibility
    assertEquals ( 0, ENQUEUE.ordinal () );
    assertEquals ( 1, DEQUEUE.ordinal () );
    assertEquals ( 2, OVERFLOW.ordinal () );
    assertEquals ( 3, UNDERFLOW.ordinal () );

  }

  @Test
  void testSignEnumValues () {

    final var values = Sign.values ();

    assertEquals ( 4, values.length );
    assertEquals ( ENQUEUE, values[0] );
    assertEquals ( DEQUEUE, values[1] );
    assertEquals ( OVERFLOW, values[2] );
    assertEquals ( UNDERFLOW, values[3] );

  }

  @Test
  void testSubjectAttachment () {

    queue.enqueue ();

    circuit.await ();

    final var capture =
      reservoir
        .drain ()
        .findFirst ()
        .orElseThrow ();

    assertEquals ( NAME, capture.subject ().name () );
    assertEquals ( ENQUEUE, capture.emission () );

  }

  @Test
  void testUnderflow () {

    queue.underflow ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( UNDERFLOW, signs.getFirst () );

  }

  @Test
  void testUnderflowPattern () {

    queue.dequeue ();
    queue.dequeue ();
    queue.underflow ();
    queue.underflow ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 4, signs.size () );
    assertEquals ( DEQUEUE, signs.getFirst () );
    assertEquals ( DEQUEUE, signs.get ( 1 ) );
    assertEquals ( UNDERFLOW, signs.get ( 2 ) );
    assertEquals ( UNDERFLOW, signs.get ( 3 ) );

  }

}
