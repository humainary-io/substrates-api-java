// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.opt.pool;

import io.humainary.substrates.ext.serventis.opt.pool.Exchanges.Dimension;
import io.humainary.substrates.ext.serventis.opt.pool.Exchanges.Exchange;
import io.humainary.substrates.ext.serventis.opt.pool.Exchanges.Sign;
import io.humainary.substrates.ext.serventis.opt.pool.Exchanges.Signal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.humainary.substrates.api.Substrates.*;
import static io.humainary.substrates.ext.serventis.opt.pool.Exchanges.Dimension.PROVIDER;
import static io.humainary.substrates.ext.serventis.opt.pool.Exchanges.Dimension.RECEIVER;
import static io.humainary.substrates.ext.serventis.opt.pool.Exchanges.Sign.CONTRACT;
import static io.humainary.substrates.ext.serventis.opt.pool.Exchanges.Sign.TRANSFER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;


/// Tests for the [Exchanges] API.
///
/// @author William David Louth
/// @since 1.0

final class ExchangesTest {

  private static final Cortex CORTEX = cortex ();
  private static final Name   NAME   = CORTEX.name ( "trade.orders" );

  private Circuit              circuit;
  private Reservoir < Signal > reservoir;
  private Exchange             exchange;

  @BeforeEach
  void setup () {

    circuit =
      CORTEX.circuit ();

    final var conduit =
      circuit.conduit (
        Exchanges::composer
      );

    exchange =
      conduit.percept (
        NAME
      );

    reservoir =
      conduit.reservoir ();

  }

  // ========== SIGN TESTS ==========

  @Test
  void testAllSignalCombinationsCached () {

    // First pass
    for ( final var sign : Sign.values () ) {
      for ( final var dimension : Dimension.values () ) {
        exchange.signal ( sign, dimension );
      }
    }

    circuit.await ();

    final var firstPass =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    // Second pass
    for ( final var sign : Sign.values () ) {
      for ( final var dimension : Dimension.values () ) {
        exchange.signal ( sign, dimension );
      }
    }

    circuit.await ();

    final var secondPass =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    // Verify caching
    assertEquals ( firstPass.size (), secondPass.size () );
    for ( var i = 0; i < firstPass.size (); i++ ) {
      assertSame ( firstPass.get ( i ), secondPass.get ( i ) );
    }

  }

  @Test
  void testContract () {

    exchange.contract ( PROVIDER );
    exchange.contract ( RECEIVER );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 2, signals.size () );
    assertEquals ( CONTRACT, signals.get ( 0 ).sign () );
    assertEquals ( PROVIDER, signals.get ( 0 ).dimension () );
    assertEquals ( CONTRACT, signals.get ( 1 ).sign () );
    assertEquals ( RECEIVER, signals.get ( 1 ).dimension () );

  }

  // ========== PATTERN TESTS ==========

  @Test
  void testDimensionEnumOrdinals () {

    assertEquals ( 0, PROVIDER.ordinal () );
    assertEquals ( 1, RECEIVER.ordinal () );

  }

  @Test
  void testDimensionEnumValues () {

    assertEquals ( 2, Dimension.values ().length );

  }

  // ========== GENERIC SIGNAL TESTS ==========

  @Test
  void testExchangerRendezvousPattern () {

    // Java Exchanger pattern: both threads contract, then swap
    // Thread 1
    exchange.contract ( PROVIDER );
    // Thread 2
    exchange.contract ( PROVIDER );
    // Swap occurs
    exchange.transfer ( PROVIDER );
    exchange.transfer ( RECEIVER );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 4, signals.size () );

  }

  // ========== ENUM TESTS ==========

  @Test
  void testREAExchangePattern () {

    // REA pattern: both sides contract, then transfer
    exchange.contract ( PROVIDER );   // Provider commits to give
    exchange.contract ( RECEIVER );   // Receiver commits to take
    exchange.transfer ( PROVIDER );   // Provider transfers out
    exchange.transfer ( RECEIVER );   // Receiver receives

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 4, signals.size () );
    assertEquals ( CONTRACT, signals.get ( 0 ).sign () );
    assertEquals ( PROVIDER, signals.get ( 0 ).dimension () );
    assertEquals ( CONTRACT, signals.get ( 1 ).sign () );
    assertEquals ( RECEIVER, signals.get ( 1 ).dimension () );
    assertEquals ( TRANSFER, signals.get ( 2 ).sign () );
    assertEquals ( PROVIDER, signals.get ( 2 ).dimension () );
    assertEquals ( TRANSFER, signals.get ( 3 ).sign () );
    assertEquals ( RECEIVER, signals.get ( 3 ).dimension () );

  }

  @Test
  void testSignEnumOrdinals () {

    assertEquals ( 0, CONTRACT.ordinal () );
    assertEquals ( 1, TRANSFER.ordinal () );

  }

  @Test
  void testSignEnumValues () {

    assertEquals ( 2, Sign.values ().length );

  }

  @Test
  void testSignalCaching () {

    exchange.contract ( PROVIDER );
    exchange.contract ( PROVIDER );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 2, signals.size () );
    assertSame ( signals.get ( 0 ), signals.get ( 1 ) );

  }

  // ========== SIGNAL CACHING TESTS ==========

  @Test
  void testSignalMethod () {

    exchange.signal ( CONTRACT, PROVIDER );
    exchange.signal ( TRANSFER, RECEIVER );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 2, signals.size () );
    assertEquals ( CONTRACT, signals.get ( 0 ).sign () );
    assertEquals ( PROVIDER, signals.get ( 0 ).dimension () );
    assertEquals ( TRANSFER, signals.get ( 1 ).sign () );
    assertEquals ( RECEIVER, signals.get ( 1 ).dimension () );

  }

  @Test
  void testSubjectAttachment () {

    exchange.contract ( PROVIDER );

    circuit.await ();

    final var capture =
      reservoir
        .drain ()
        .findFirst ()
        .orElseThrow ();

    assertEquals ( NAME, capture.subject ().name () );
    assertEquals ( CONTRACT, capture.emission ().sign () );
    assertEquals ( PROVIDER, capture.emission ().dimension () );

  }

  // ========== SUBJECT TESTS ==========

  @Test
  void testTransfer () {

    exchange.transfer ( PROVIDER );
    exchange.transfer ( RECEIVER );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 2, signals.size () );
    assertEquals ( TRANSFER, signals.get ( 0 ).sign () );
    assertEquals ( PROVIDER, signals.get ( 0 ).dimension () );
    assertEquals ( TRANSFER, signals.get ( 1 ).sign () );
    assertEquals ( RECEIVER, signals.get ( 1 ).dimension () );

  }

}
