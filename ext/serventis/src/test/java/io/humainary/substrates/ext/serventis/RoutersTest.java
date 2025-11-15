// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis;

import io.humainary.substrates.ext.serventis.ext.Routers;
import io.humainary.substrates.ext.serventis.ext.Routers.Router;
import io.humainary.substrates.ext.serventis.ext.Routers.Sign;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.humainary.substrates.api.Substrates.*;
import static io.humainary.substrates.ext.serventis.ext.Routers.Sign.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


/// Tests for the [Routers] API.
///
/// @author William David Louth
/// @since 1.0

final class RoutersTest {

  private static final Cortex             CORTEX = cortex ();
  private static final Name               NAME   = CORTEX.name ( "edge01.eth0" );
  private              Circuit            circuit;
  private              Reservoir < Sign > reservoir;
  private              Router             router;

  @BeforeEach
  void setup () {

    circuit =
      CORTEX.circuit ();

    final var conduit =
      circuit.conduit (
        Routers::composer
      );

    router =
      conduit.percept (
        NAME
      );

    reservoir =
      CORTEX.reservoir (
        conduit
      );

  }


  @Test
  void testCorrupt () {

    router.corrupt ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( CORRUPT, signs.getFirst () );

  }

  @Test
  void testDrop () {

    router.drop ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( DROP, signs.getFirst () );

  }

  @Test
  void testForward () {

    router.forward ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( FORWARD, signs.getFirst () );

  }

  @Test
  void testFragment () {

    router.fragment ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( FRAGMENT, signs.getFirst () );

  }

  @Test
  void testPacketLifecycle () {

    // Normal packet flow: receive -> route -> forward
    router.receive ();
    router.route ();
    router.forward ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 3, signs.size () );
    assertEquals ( RECEIVE, signs.get ( 0 ) );
    assertEquals ( ROUTE, signs.get ( 1 ) );
    assertEquals ( FORWARD, signs.get ( 2 ) );

  }

  @Test
  void testPacketLifecycleWithFragmentation () {

    // Packet flow with fragmentation: receive -> route -> fragment -> send
    router.receive ();
    router.route ();
    router.fragment ();
    router.send ();
    router.send ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 5, signs.size () );
    assertEquals ( RECEIVE, signs.get ( 0 ) );
    assertEquals ( ROUTE, signs.get ( 1 ) );
    assertEquals ( FRAGMENT, signs.get ( 2 ) );
    assertEquals ( SEND, signs.get ( 3 ) );
    assertEquals ( SEND, signs.get ( 4 ) );

  }

  @Test
  void testReassemble () {

    router.reassemble ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( REASSEMBLE, signs.getFirst () );

  }

  @Test
  void testReceive () {

    router.receive ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( RECEIVE, signs.getFirst () );

  }

  @Test
  void testReorder () {

    router.reorder ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( REORDER, signs.getFirst () );

  }

  @Test
  void testRoute () {

    router.route ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( ROUTE, signs.getFirst () );

  }

  @Test
  void testSend () {

    router.send ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( SEND, signs.getFirst () );

  }

  @Test
  void testSign () {

    // Test direct sign() method for all sign values
    router.sign ( SEND );
    router.sign ( RECEIVE );
    router.sign ( FORWARD );
    router.sign ( ROUTE );
    router.sign ( DROP );
    router.sign ( FRAGMENT );
    router.sign ( REASSEMBLE );
    router.sign ( CORRUPT );
    router.sign ( REORDER );

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 9, signs.size () );
    assertEquals ( SEND, signs.get ( 0 ) );
    assertEquals ( RECEIVE, signs.get ( 1 ) );
    assertEquals ( FORWARD, signs.get ( 2 ) );
    assertEquals ( ROUTE, signs.get ( 3 ) );
    assertEquals ( DROP, signs.get ( 4 ) );
    assertEquals ( FRAGMENT, signs.get ( 5 ) );
    assertEquals ( REASSEMBLE, signs.get ( 6 ) );
    assertEquals ( CORRUPT, signs.get ( 7 ) );
    assertEquals ( REORDER, signs.get ( 8 ) );

  }

  @Test
  void testSignEnumOrdinals () {

    // Ensure ordinals remain stable for compatibility
    assertEquals ( 0, SEND.ordinal () );
    assertEquals ( 1, RECEIVE.ordinal () );
    assertEquals ( 2, FORWARD.ordinal () );
    assertEquals ( 3, ROUTE.ordinal () );
    assertEquals ( 4, DROP.ordinal () );
    assertEquals ( 5, FRAGMENT.ordinal () );
    assertEquals ( 6, REASSEMBLE.ordinal () );
    assertEquals ( 7, CORRUPT.ordinal () );
    assertEquals ( 8, REORDER.ordinal () );

  }

  @Test
  void testSignEnumValues () {

    final var values = Sign.values ();

    assertEquals ( 9, values.length );
    assertEquals ( SEND, values[0] );
    assertEquals ( RECEIVE, values[1] );
    assertEquals ( FORWARD, values[2] );
    assertEquals ( ROUTE, values[3] );
    assertEquals ( DROP, values[4] );
    assertEquals ( FRAGMENT, values[5] );
    assertEquals ( REASSEMBLE, values[6] );
    assertEquals ( CORRUPT, values[7] );
    assertEquals ( REORDER, values[8] );

  }

  @Test
  void testSubjectAttachment () {

    router.send ();

    circuit.await ();

    final var capture =
      reservoir
        .drain ()
        .findFirst ()
        .orElseThrow ();

    assertEquals ( NAME, capture.subject ().name () );
    assertEquals ( SEND, capture.emission () );

  }

  @Test
  void testTrafficWithCongestion () {

    // Simulate traffic with congestion: receive packets, some forwarded, some dropped
    router.receive ();
    router.route ();
    router.forward ();

    router.receive ();
    router.route ();
    router.forward ();

    router.receive ();
    router.route ();
    router.drop ();

    router.receive ();
    router.route ();
    router.drop ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 12, signs.size () );

    // Count different sign types
    final var receives = signs.stream ().filter ( s -> s == RECEIVE ).count ();
    final var routes = signs.stream ().filter ( s -> s == ROUTE ).count ();
    final var forwards = signs.stream ().filter ( s -> s == FORWARD ).count ();
    final var drops = signs.stream ().filter ( s -> s == DROP ).count ();

    assertEquals ( 4, receives );
    assertEquals ( 4, routes );
    assertEquals ( 2, forwards );
    assertEquals ( 2, drops );

  }

  @Test
  void testTrafficWithCorruption () {

    // Simulate receiving corrupted packets
    router.receive ();
    router.corrupt ();
    router.drop ();

    router.receive ();
    router.route ();
    router.forward ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 6, signs.size () );
    assertEquals ( RECEIVE, signs.get ( 0 ) );
    assertEquals ( CORRUPT, signs.get ( 1 ) );
    assertEquals ( DROP, signs.get ( 2 ) );
    assertEquals ( RECEIVE, signs.get ( 3 ) );
    assertEquals ( ROUTE, signs.get ( 4 ) );
    assertEquals ( FORWARD, signs.get ( 5 ) );

  }

  @Test
  void testTrafficWithReordering () {

    // Simulate out-of-order packet arrival
    router.receive ();
    router.reorder ();
    router.route ();
    router.forward ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 4, signs.size () );
    assertEquals ( RECEIVE, signs.get ( 0 ) );
    assertEquals ( REORDER, signs.get ( 1 ) );
    assertEquals ( ROUTE, signs.get ( 2 ) );
    assertEquals ( FORWARD, signs.get ( 3 ) );

  }

}
