// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.sdk;

import io.humainary.substrates.ext.serventis.sdk.Outcomes.Outcome;
import io.humainary.substrates.ext.serventis.sdk.Outcomes.Sign;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.humainary.substrates.api.Substrates.*;
import static io.humainary.substrates.ext.serventis.sdk.Outcomes.Sign.FAIL;
import static io.humainary.substrates.ext.serventis.sdk.Outcomes.Sign.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;


/// Tests for the [Outcomes] API.
///
/// @author William David Louth
/// @since 1.0

final class OutcomesTest {

  private static final Cortex             CORTEX = cortex ();
  private static final Name               NAME   = CORTEX.name ( "payment.outcomes" );
  private              Circuit            circuit;
  private              Reservoir < Sign > reservoir;
  private              Outcome            outcome;

  @BeforeEach
  void setup () {

    circuit =
      CORTEX.circuit ();

    final var conduit =
      circuit.conduit (
        Outcomes::composer
      );

    outcome =
      conduit.percept (
        NAME
      );

    reservoir =
      conduit.reservoir ();

  }

  @Test
  void testFail () {

    outcome.fail ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( FAIL, signs.getFirst () );

  }

  @Test
  void testMixedOutcomes () {

    outcome.success ();
    outcome.success ();
    outcome.fail ();
    outcome.success ();
    outcome.fail ();
    outcome.fail ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 6, signs.size () );
    assertEquals ( SUCCESS, signs.get ( 0 ) );
    assertEquals ( SUCCESS, signs.get ( 1 ) );
    assertEquals ( FAIL, signs.get ( 2 ) );
    assertEquals ( SUCCESS, signs.get ( 3 ) );
    assertEquals ( FAIL, signs.get ( 4 ) );
    assertEquals ( FAIL, signs.get ( 5 ) );

  }

  @Test
  void testMultipleOutcomeInstruments () {

    final var paymentName = CORTEX.name ( "payment.outcomes" );
    final var orderName = CORTEX.name ( "order.outcomes" );

    final var conduit = circuit.conduit ( Outcomes::composer );
    final var paymentOutcome = conduit.percept ( paymentName );
    final var orderOutcome = conduit.percept ( orderName );
    final var signReservoir = conduit.reservoir ();

    paymentOutcome.success ();
    orderOutcome.fail ();
    paymentOutcome.fail ();

    circuit.await ();

    final var allCaptures = signReservoir.drain ().toList ();

    assertEquals ( 3, allCaptures.size () );

    assertEquals ( paymentName, allCaptures.get ( 0 ).subject ().name () );
    assertEquals ( SUCCESS, allCaptures.get ( 0 ).emission () );

    assertEquals ( orderName, allCaptures.get ( 1 ).subject ().name () );
    assertEquals ( FAIL, allCaptures.get ( 1 ).emission () );

    assertEquals ( paymentName, allCaptures.get ( 2 ).subject ().name () );
    assertEquals ( FAIL, allCaptures.get ( 2 ).emission () );

  }

  @Test
  void testSign () {

    outcome.sign ( SUCCESS );
    outcome.sign ( FAIL );

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 2, signs.size () );
    assertEquals ( SUCCESS, signs.get ( 0 ) );
    assertEquals ( FAIL, signs.get ( 1 ) );

  }

  @Test
  void testSignEnumOrdinals () {

    assertEquals ( 0, SUCCESS.ordinal () );
    assertEquals ( 1, FAIL.ordinal () );

  }

  @Test
  void testSignEnumValues () {

    final var values = Sign.values ();

    assertEquals ( 2, values.length );
    assertEquals ( SUCCESS, values[0] );
    assertEquals ( FAIL, values[1] );

  }

  @Test
  void testSubjectAttachment () {

    outcome.success ();

    circuit.await ();

    final var capture =
      reservoir
        .drain ()
        .findFirst ()
        .orElseThrow ();

    assertEquals ( NAME, capture.subject ().name () );
    assertEquals ( SUCCESS, capture.emission () );

  }

  @Test
  void testSuccess () {

    outcome.success ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( SUCCESS, signs.getFirst () );

  }

  @Test
  void testSuccessFailPattern () {

    outcome.success ();
    outcome.fail ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 2, signs.size () );
    assertEquals ( SUCCESS, signs.getFirst () );
    assertEquals ( FAIL, signs.get ( 1 ) );

  }

}
