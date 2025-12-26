// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.opt.tool;

import io.humainary.substrates.ext.serventis.opt.tool.Probes.Dimension;
import io.humainary.substrates.ext.serventis.opt.tool.Probes.Probe;
import io.humainary.substrates.ext.serventis.opt.tool.Probes.Sign;
import io.humainary.substrates.ext.serventis.opt.tool.Probes.Signal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.humainary.substrates.api.Substrates.*;
import static io.humainary.substrates.ext.serventis.opt.tool.Probes.Dimension.INBOUND;
import static io.humainary.substrates.ext.serventis.opt.tool.Probes.Dimension.OUTBOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;


/// The test class for the [Probe] interface.
///
/// @author William David Louth
/// @since 1.0
final class ProbesTest {

  private static final Cortex               cortex = cortex ();
  private static final Name                 NAME   = cortex.name ( "service.1" );
  private              Circuit              circuit;
  private              Reservoir < Signal > reservoir;
  private              Probe                probe;

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
        Probes::composer
      );

    probe =
      conduit.percept (
        NAME
      );

    reservoir =
      conduit.reservoir ();

  }

  @Test
  void testConnect () {

    emit ( new Signal ( Sign.CONNECT, OUTBOUND ), () -> probe.connect ( OUTBOUND ) );

  }

  @Test
  void testConnected () {

    emit ( new Signal ( Sign.CONNECT, INBOUND ), () -> probe.connect ( INBOUND ) );

  }

  @Test
  void testDisconnect () {

    emit ( new Signal ( Sign.DISCONNECT, OUTBOUND ), () -> probe.disconnect ( OUTBOUND ) );

  }

  @Test
  void testDisconnected () {

    emit ( new Signal ( Sign.DISCONNECT, INBOUND ), () -> probe.disconnect ( INBOUND ) );

  }

  @Test
  void testFail () {

    emit ( new Signal ( Sign.FAIL, OUTBOUND ), () -> probe.fail ( OUTBOUND ) );

  }

  @Test
  void testFailed () {

    emit ( new Signal ( Sign.FAIL, INBOUND ), () -> probe.fail ( INBOUND ) );

  }

  @Test
  void testMultipleEmissions () {

    probe.connect ( OUTBOUND );
    probe.transfer ( OUTBOUND );
    probe.succeed ( OUTBOUND );
    probe.disconnect ( OUTBOUND );

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
  void testOrientationEnumOrdinals () {

    assertEquals ( 0, OUTBOUND.ordinal () );
    assertEquals ( 1, INBOUND.ordinal () );

  }

  @Test
  void testOrientationEnumValues () {

    final var values = Dimension.values ();

    assertEquals ( 2, values.length );

  }

  @Test
  void testProcess () {

    emit ( new Signal ( Sign.PROCESS, OUTBOUND ), () -> probe.process ( OUTBOUND ) );

  }

  @Test
  void testProcessed () {

    emit ( new Signal ( Sign.PROCESS, INBOUND ), () -> probe.process ( INBOUND ) );

  }

  @Test
  void testSignEnumOrdinals () {

    // Ensure ordinals remain stable for compatibility
    assertEquals ( 0, Sign.CONNECT.ordinal () );
    assertEquals ( 1, Sign.DISCONNECT.ordinal () );
    assertEquals ( 2, Sign.TRANSFER.ordinal () );
    assertEquals ( 3, Sign.PROCESS.ordinal () );
    assertEquals ( 4, Sign.SUCCEED.ordinal () );
    assertEquals ( 5, Sign.FAIL.ordinal () );

  }

  @Test
  void testSignEnumValues () {

    final var values = Sign.values ();

    assertEquals ( 6, values.length );

  }

  @Test
  void testSignal () {

    // Test direct signal() method for all sign and dimension combinations
    probe.signal ( Sign.CONNECT, OUTBOUND );
    probe.signal ( Sign.CONNECT, INBOUND );
    probe.signal ( Sign.DISCONNECT, OUTBOUND );
    probe.signal ( Sign.DISCONNECT, INBOUND );
    probe.signal ( Sign.TRANSFER, OUTBOUND );
    probe.signal ( Sign.TRANSFER, INBOUND );
    probe.signal ( Sign.TRANSFER, OUTBOUND );
    probe.signal ( Sign.TRANSFER, INBOUND );
    probe.signal ( Sign.PROCESS, OUTBOUND );
    probe.signal ( Sign.PROCESS, INBOUND );
    probe.signal ( Sign.SUCCEED, OUTBOUND );
    probe.signal ( Sign.SUCCEED, INBOUND );
    probe.signal ( Sign.FAIL, OUTBOUND );
    probe.signal ( Sign.FAIL, INBOUND );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 14, signals.size () );
    assertEquals ( new Signal ( Sign.CONNECT, OUTBOUND ), signals.get ( 0 ) );
    assertEquals ( new Signal ( Sign.CONNECT, INBOUND ), signals.get ( 1 ) );
    assertEquals ( new Signal ( Sign.DISCONNECT, OUTBOUND ), signals.get ( 2 ) );
    assertEquals ( new Signal ( Sign.DISCONNECT, INBOUND ), signals.get ( 3 ) );
    assertEquals ( new Signal ( Sign.TRANSFER, OUTBOUND ), signals.get ( 4 ) );
    assertEquals ( new Signal ( Sign.TRANSFER, INBOUND ), signals.get ( 5 ) );
    assertEquals ( new Signal ( Sign.TRANSFER, OUTBOUND ), signals.get ( 6 ) );
    assertEquals ( new Signal ( Sign.TRANSFER, INBOUND ), signals.get ( 7 ) );
    assertEquals ( new Signal ( Sign.PROCESS, OUTBOUND ), signals.get ( 8 ) );
    assertEquals ( new Signal ( Sign.PROCESS, INBOUND ), signals.get ( 9 ) );
    assertEquals ( new Signal ( Sign.SUCCEED, OUTBOUND ), signals.get ( 10 ) );
    assertEquals ( new Signal ( Sign.SUCCEED, INBOUND ), signals.get ( 11 ) );
    assertEquals ( new Signal ( Sign.FAIL, OUTBOUND ), signals.get ( 12 ) );
    assertEquals ( new Signal ( Sign.FAIL, INBOUND ), signals.get ( 13 ) );

  }

  @Test
  void testSignalAccessors () {

    final var connect = new Signal ( Sign.CONNECT, OUTBOUND );
    assertEquals ( Sign.CONNECT, connect.sign () );
    assertEquals ( OUTBOUND, connect.dimension () );

    final var connected = new Signal ( Sign.CONNECT, INBOUND );
    assertEquals ( Sign.CONNECT, connected.sign () );
    assertEquals ( INBOUND, connected.dimension () );

    final var succeed = new Signal ( Sign.SUCCEED, OUTBOUND );
    assertEquals ( Sign.SUCCEED, succeed.sign () );
    assertEquals ( OUTBOUND, succeed.dimension () );

    final var failed = new Signal ( Sign.FAIL, INBOUND );
    assertEquals ( Sign.FAIL, failed.sign () );
    assertEquals ( INBOUND, failed.dimension () );

  }

  @Test
  void testSignalCoverage () {

    // 6 signs × 2 dimensions = 12 signal combinations
    assertEquals ( 6, Sign.values ().length );
    assertEquals ( 2, Dimension.values ().length );

  }

  @Test
  void testSubjectAttachment () {

    probe.connect ( OUTBOUND );

    circuit.await ();

    final var capture =
      reservoir
        .drain ()
        .findFirst ()
        .orElseThrow ();

    assertEquals ( NAME, capture.subject ().name () );
    assertEquals ( new Signal ( Sign.CONNECT, OUTBOUND ), capture.emission () );

  }

  @Test
  void testSucceed () {

    emit ( new Signal ( Sign.SUCCEED, OUTBOUND ), () -> probe.succeed ( OUTBOUND ) );

  }

  @Test
  void testSucceeded () {

    emit ( new Signal ( Sign.SUCCEED, INBOUND ), () -> probe.succeed ( INBOUND ) );

  }

  @Test
  void testTransfer () {

    emit ( new Signal ( Sign.TRANSFER, OUTBOUND ), () -> probe.transfer ( OUTBOUND ) );

  }

  @Test
  void testTransferInbound () {

    emit ( new Signal ( Sign.TRANSFER, INBOUND ), () -> probe.transfer ( INBOUND ) );

  }

  @Test
  void testTransferOutbound () {

    emit ( new Signal ( Sign.TRANSFER, OUTBOUND ), () -> probe.transfer ( OUTBOUND ) );

  }

  @Test
  void testTransferred () {

    emit ( new Signal ( Sign.TRANSFER, INBOUND ), () -> probe.transfer ( INBOUND ) );

  }

}
