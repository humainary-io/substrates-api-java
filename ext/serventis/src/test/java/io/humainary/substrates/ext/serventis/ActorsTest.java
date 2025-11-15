// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis;

import io.humainary.substrates.ext.serventis.ext.Actors;
import io.humainary.substrates.ext.serventis.ext.Actors.Actor;
import io.humainary.substrates.ext.serventis.ext.Actors.Sign;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.humainary.substrates.api.Substrates.*;
import static io.humainary.substrates.ext.serventis.ext.Actors.Sign.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


/// Tests for the [Actors] API.
///
/// @author William David Louth
/// @since 1.0

final class ActorsTest {

  private static final Cortex             CORTEX = cortex ();
  private static final Name               NAME   = CORTEX.name ( "user.william" );
  private              Circuit            circuit;
  private              Reservoir < Sign > reservoir;
  private              Actor              actor;

  @BeforeEach
  void setup () {

    circuit =
      CORTEX.circuit ();

    final var conduit =
      circuit.conduit (
        Actors::composer
      );

    actor =
      conduit.percept (
        NAME
      );

    reservoir =
      CORTEX.reservoir (
        conduit
      );

  }

  // ========== INDIVIDUAL SIGN TESTS ==========

  @Test
  void testAcknowledge () {

    actor.acknowledge ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( ACKNOWLEDGE, signs.getFirst () );

  }

  @Test
  void testAffirm () {

    actor.affirm ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( AFFIRM, signs.getFirst () );

  }

  @Test
  void testAsk () {

    actor.ask ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( ASK, signs.getFirst () );

  }

  @Test
  void testClarify () {

    actor.clarify ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( CLARIFY, signs.getFirst () );

  }

  @Test
  void testCollaborativeRefinementPattern () {

    // Simulate collaborative refinement
    final var aiName = CORTEX.name ( "assistant.claude" );

    final var conduit = circuit.conduit ( Actors::composer );
    final var human = conduit.percept ( NAME );
    final var ai = conduit.percept ( aiName );
    final var signReservoir = CORTEX.reservoir ( conduit );

    // Human requests design
    human.request ();

    // AI explains approach
    ai.explain ();

    // Human denies and clarifies different direction
    human.deny ();
    human.clarify ();

    // AI acknowledges and delivers refined design
    ai.acknowledge ();
    ai.deliver ();

    // Human acknowledges
    human.acknowledge ();

    circuit.await ();

    final var signs =
      signReservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 7, signs.size () );
    assertEquals ( REQUEST, signs.get ( 0 ) );
    assertEquals ( EXPLAIN, signs.get ( 1 ) );
    assertEquals ( DENY, signs.get ( 2 ) );
    assertEquals ( CLARIFY, signs.get ( 3 ) );
    assertEquals ( ACKNOWLEDGE, signs.get ( 4 ) );
    assertEquals ( DELIVER, signs.get ( 5 ) );
    assertEquals ( ACKNOWLEDGE, signs.get ( 6 ) );

  }

  @Test
  void testCommand () {

    actor.command ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( COMMAND, signs.getFirst () );

  }

  @Test
  void testCommandAuthority () {

    // Test command-based interaction
    actor.command ();
    actor.acknowledge ();
    actor.deliver ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 3, signs.size () );
    assertEquals ( COMMAND, signs.get ( 0 ) );
    assertEquals ( ACKNOWLEDGE, signs.get ( 1 ) );
    assertEquals ( DELIVER, signs.get ( 2 ) );

  }

  @Test
  void testCommitmentFulfillment () {

    // Test PROMISE → DELIVER arc
    actor.promise ();
    actor.deliver ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 2, signs.size () );
    assertEquals ( PROMISE, signs.get ( 0 ) );
    assertEquals ( DELIVER, signs.get ( 1 ) );

  }

  @Test
  void testComplexHumanAIDialogue () {

    // Simulate the full example from JavaDoc
    final var aiName = CORTEX.name ( "assistant.claude" );

    final var conduit = circuit.conduit ( Actors::composer );
    final var human = conduit.percept ( NAME );
    final var ai = conduit.percept ( aiName );
    final var signReservoir = CORTEX.reservoir ( conduit );

    // Phase 1: Question-Answer
    human.ask ();           // "What about packet networks?"
    ai.explain ();          // network semantic spaces
    ai.affirm ();           // Routers API would be valuable
    human.acknowledge ();

    // Phase 2: Request-Delivery
    human.request ();       // write Routers API
    ai.acknowledge ();
    ai.promise ();          // will deliver
    ai.deliver ();          // presents Routers.java
    human.acknowledge ();

    // Phase 3: Correction
    human.deny ();          // Router not right term
    human.ask ();           // is Actor better?
    ai.explain ();          // terminology analysis
    ai.affirm ();           // Actor is preferable
    human.acknowledge ();

    // Phase 4: Command-Revision
    human.command ();       // rewrite with Actor
    ai.acknowledge ();
    ai.clarify ();          // same 11 signs?
    human.acknowledge ();
    ai.deliver ();          // revised Actors API

    circuit.await ();

    final var signs =
      signReservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 19, signs.size () );

    // Verify key transition points
    assertEquals ( ASK, signs.get ( 0 ) );      // Start Phase 1
    assertEquals ( REQUEST, signs.get ( 4 ) );  // Start Phase 2
    assertEquals ( DENY, signs.get ( 9 ) );     // Start Phase 3
    assertEquals ( COMMAND, signs.get ( 14 ) ); // Start Phase 4
    assertEquals ( DELIVER, signs.get ( 18 ) ); // Final delivery

  }

  @Test
  void testCoordinationFlow () {

    // Test coordination sequence: REQUEST → ACKNOWLEDGE → PROMISE → DELIVER
    actor.request ();
    actor.acknowledge ();
    actor.promise ();
    actor.deliver ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 4, signs.size () );
    assertEquals ( REQUEST, signs.get ( 0 ) );
    assertEquals ( ACKNOWLEDGE, signs.get ( 1 ) );
    assertEquals ( PROMISE, signs.get ( 2 ) );
    assertEquals ( DELIVER, signs.get ( 3 ) );

  }

  // ========== GENERIC TESTS ==========

  @Test
  void testCorrectionClarificationPattern () {

    // Simulate correction and clarification
    final var aiName = CORTEX.name ( "assistant.claude" );

    final var conduit = circuit.conduit ( Actors::composer );
    final var human = conduit.percept ( NAME );
    final var ai = conduit.percept ( aiName );
    final var signReservoir = CORTEX.reservoir ( conduit );

    // AI makes assertion
    ai.affirm ();

    // Human denies
    human.deny ();

    // AI clarifies
    ai.clarify ();

    // Human acknowledges
    human.acknowledge ();

    circuit.await ();

    final var signs =
      signReservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 4, signs.size () );
    assertEquals ( AFFIRM, signs.get ( 0 ) );
    assertEquals ( DENY, signs.get ( 1 ) );
    assertEquals ( CLARIFY, signs.get ( 2 ) );
    assertEquals ( ACKNOWLEDGE, signs.get ( 3 ) );

  }

  @Test
  void testDeliver () {

    actor.deliver ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( DELIVER, signs.getFirst () );

  }

  @Test
  void testDeny () {

    actor.deny ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( DENY, signs.getFirst () );

  }

  @Test
  void testDisagreementResolution () {

    // Test disagreement flow: AFFIRM → DENY → CLARIFY → ACKNOWLEDGE
    actor.affirm ();
    actor.deny ();
    actor.clarify ();
    actor.acknowledge ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 4, signs.size () );
    assertEquals ( AFFIRM, signs.get ( 0 ) );
    assertEquals ( DENY, signs.get ( 1 ) );
    assertEquals ( CLARIFY, signs.get ( 2 ) );
    assertEquals ( ACKNOWLEDGE, signs.get ( 3 ) );

  }

  // ========== DIALOGUE PATTERN TESTS ==========

  @Test
  void testExplain () {

    actor.explain ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( EXPLAIN, signs.getFirst () );

  }

  @Test
  void testInformationExchange () {

    // Test information flow: ASK → REPORT/EXPLAIN/AFFIRM
    actor.ask ();
    actor.report ();
    actor.explain ();
    actor.affirm ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 4, signs.size () );
    assertEquals ( ASK, signs.get ( 0 ) );
    assertEquals ( REPORT, signs.get ( 1 ) );
    assertEquals ( EXPLAIN, signs.get ( 2 ) );
    assertEquals ( AFFIRM, signs.get ( 3 ) );

  }

  @Test
  void testPromise () {

    actor.promise ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( PROMISE, signs.getFirst () );

  }

  @Test
  void testQuestionAnswerPattern () {

    // Simulate question-answer dialogue
    final var aiName = CORTEX.name ( "assistant.claude" );

    final var conduit = circuit.conduit ( Actors::composer );
    final var human = conduit.percept ( NAME );
    final var ai = conduit.percept ( aiName );
    final var signReservoir = CORTEX.reservoir ( conduit );

    // Human asks question
    human.ask ();

    // AI explains and affirms
    ai.explain ();
    ai.affirm ();

    // Human acknowledges
    human.acknowledge ();

    circuit.await ();

    final var allSigns = signReservoir.drain ().toList ();

    assertEquals ( 4, allSigns.size () );

    // Verify sequence
    assertEquals ( ASK, allSigns.get ( 0 ).emission () );
    assertEquals ( EXPLAIN, allSigns.get ( 1 ).emission () );
    assertEquals ( AFFIRM, allSigns.get ( 2 ).emission () );
    assertEquals ( ACKNOWLEDGE, allSigns.get ( 3 ).emission () );

    // Verify actors
    assertEquals ( NAME, allSigns.get ( 0 ).subject ().name () );
    assertEquals ( aiName, allSigns.get ( 1 ).subject ().name () );
    assertEquals ( aiName, allSigns.get ( 2 ).subject ().name () );
    assertEquals ( NAME, allSigns.get ( 3 ).subject ().name () );

  }

  @Test
  void testReport () {

    actor.report ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( REPORT, signs.getFirst () );

  }

  @Test
  void testRequest () {

    actor.request ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( REQUEST, signs.getFirst () );

  }

  @Test
  void testRequestDeliveryPattern () {

    // Simulate request-delivery collaboration
    final var aiName = CORTEX.name ( "assistant.claude" );

    final var conduit = circuit.conduit ( Actors::composer );
    final var human = conduit.percept ( NAME );
    final var ai = conduit.percept ( aiName );
    final var signReservoir = CORTEX.reservoir ( conduit );

    // Human requests work
    human.request ();

    // AI acknowledges and promises
    ai.acknowledge ();
    ai.promise ();

    // AI delivers work
    ai.deliver ();

    // Human acknowledges
    human.acknowledge ();

    circuit.await ();

    final var signs =
      signReservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 5, signs.size () );
    assertEquals ( REQUEST, signs.get ( 0 ) );
    assertEquals ( ACKNOWLEDGE, signs.get ( 1 ) );
    assertEquals ( PROMISE, signs.get ( 2 ) );
    assertEquals ( DELIVER, signs.get ( 3 ) );
    assertEquals ( ACKNOWLEDGE, signs.get ( 4 ) );

  }

  @Test
  void testSign () {

    // Test direct sign() method for all sign values
    actor.sign ( ASK );
    actor.sign ( AFFIRM );
    actor.sign ( EXPLAIN );
    actor.sign ( REPORT );
    actor.sign ( REQUEST );
    actor.sign ( COMMAND );
    actor.sign ( ACKNOWLEDGE );
    actor.sign ( DENY );
    actor.sign ( CLARIFY );
    actor.sign ( PROMISE );
    actor.sign ( DELIVER );

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 11, signs.size () );
    assertEquals ( ASK, signs.get ( 0 ) );
    assertEquals ( AFFIRM, signs.get ( 1 ) );
    assertEquals ( EXPLAIN, signs.get ( 2 ) );
    assertEquals ( REPORT, signs.get ( 3 ) );
    assertEquals ( REQUEST, signs.get ( 4 ) );
    assertEquals ( COMMAND, signs.get ( 5 ) );
    assertEquals ( ACKNOWLEDGE, signs.get ( 6 ) );
    assertEquals ( DENY, signs.get ( 7 ) );
    assertEquals ( CLARIFY, signs.get ( 8 ) );
    assertEquals ( PROMISE, signs.get ( 9 ) );
    assertEquals ( DELIVER, signs.get ( 10 ) );

  }

  @Test
  void testSignEnumOrdinals () {

    // Ensure ordinals remain stable for compatibility
    assertEquals ( 0, ASK.ordinal () );
    assertEquals ( 1, AFFIRM.ordinal () );
    assertEquals ( 2, EXPLAIN.ordinal () );
    assertEquals ( 3, REPORT.ordinal () );
    assertEquals ( 4, REQUEST.ordinal () );
    assertEquals ( 5, COMMAND.ordinal () );
    assertEquals ( 6, ACKNOWLEDGE.ordinal () );
    assertEquals ( 7, DENY.ordinal () );
    assertEquals ( 8, CLARIFY.ordinal () );
    assertEquals ( 9, PROMISE.ordinal () );
    assertEquals ( 10, DELIVER.ordinal () );

  }

  @Test
  void testSignEnumValues () {

    final var values = Sign.values ();

    assertEquals ( 11, values.length );

  }

  @Test
  void testSubjectAttachment () {

    actor.ask ();

    circuit.await ();

    final var capture =
      reservoir
        .drain ()
        .findFirst ()
        .orElseThrow ();

    assertEquals ( NAME, capture.subject ().name () );
    assertEquals ( ASK, capture.emission () );

  }

}
