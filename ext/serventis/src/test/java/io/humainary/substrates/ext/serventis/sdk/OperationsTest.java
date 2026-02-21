// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.sdk;

import io.humainary.substrates.ext.serventis.sdk.Operations.Operation;
import io.humainary.substrates.ext.serventis.sdk.Operations.Sign;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.humainary.substrates.api.Substrates.*;
import static io.humainary.substrates.ext.serventis.sdk.Operations.Sign.BEGIN;
import static io.humainary.substrates.ext.serventis.sdk.Operations.Sign.END;
import static org.junit.jupiter.api.Assertions.assertEquals;


/// Tests for the [Operations] API.
///
/// @author William David Louth
/// @since 1.0

final class OperationsTest {

  private static final Cortex             CORTEX = cortex ();
  private static final Name               NAME   = CORTEX.name ( "db.query" );
  private              Circuit            circuit;
  private              Reservoir < Sign > reservoir;
  private              Operation          operation;

  @BeforeEach
  void setup () {

    circuit =
      CORTEX.circuit ();

    final var conduit =
      circuit.conduit (
        Operations::composer
      );

    operation =
      conduit.percept (
        NAME
      );

    reservoir =
      conduit.reservoir ();

  }

  @Test
  void testBegin () {

    operation.begin ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( BEGIN, signs.getFirst () );

  }

  @Test
  void testBeginEndPattern () {

    operation.begin ();
    operation.end ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 2, signs.size () );
    assertEquals ( BEGIN, signs.getFirst () );
    assertEquals ( END, signs.get ( 1 ) );

  }

  @Test
  void testEnd () {

    operation.end ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( END, signs.getFirst () );

  }

  @Test
  void testMultipleOperationInstruments () {

    final var queryName = CORTEX.name ( "db.query" );
    final var txName = CORTEX.name ( "db.transaction" );

    final var conduit = circuit.conduit ( Operations::composer );
    final var queryOp = conduit.percept ( queryName );
    final var txOp = conduit.percept ( txName );
    final var signReservoir = conduit.reservoir ();

    txOp.begin ();
    queryOp.begin ();
    queryOp.end ();
    txOp.end ();

    circuit.await ();

    final var allCaptures = signReservoir.drain ().toList ();

    assertEquals ( 4, allCaptures.size () );

    assertEquals ( txName, allCaptures.get ( 0 ).subject ().name () );
    assertEquals ( BEGIN, allCaptures.get ( 0 ).emission () );

    assertEquals ( queryName, allCaptures.get ( 1 ).subject ().name () );
    assertEquals ( BEGIN, allCaptures.get ( 1 ).emission () );

    assertEquals ( queryName, allCaptures.get ( 2 ).subject ().name () );
    assertEquals ( END, allCaptures.get ( 2 ).emission () );

    assertEquals ( txName, allCaptures.get ( 3 ).subject ().name () );
    assertEquals ( END, allCaptures.get ( 3 ).emission () );

  }

  @Test
  void testNestedOperations () {

    operation.begin ();
    operation.begin ();
    operation.end ();
    operation.begin ();
    operation.end ();
    operation.end ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 6, signs.size () );
    assertEquals ( BEGIN, signs.get ( 0 ) );
    assertEquals ( BEGIN, signs.get ( 1 ) );
    assertEquals ( END, signs.get ( 2 ) );
    assertEquals ( BEGIN, signs.get ( 3 ) );
    assertEquals ( END, signs.get ( 4 ) );
    assertEquals ( END, signs.get ( 5 ) );

  }

  @Test
  void testSign () {

    operation.sign ( BEGIN );
    operation.sign ( END );

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 2, signs.size () );
    assertEquals ( BEGIN, signs.get ( 0 ) );
    assertEquals ( END, signs.get ( 1 ) );

  }

  @Test
  void testSignEnumOrdinals () {

    assertEquals ( 0, BEGIN.ordinal () );
    assertEquals ( 1, END.ordinal () );

  }

  @Test
  void testSignEnumValues () {

    final var values = Sign.values ();

    assertEquals ( 2, values.length );
    assertEquals ( BEGIN, values[0] );
    assertEquals ( END, values[1] );

  }

  @Test
  void testSubjectAttachment () {

    operation.begin ();

    circuit.await ();

    final var capture =
      reservoir
        .drain ()
        .findFirst ()
        .orElseThrow ();

    assertEquals ( NAME, capture.subject ().name () );
    assertEquals ( BEGIN, capture.emission () );

  }

}
