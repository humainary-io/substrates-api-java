// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.opt.role;

import io.humainary.substrates.ext.serventis.opt.role.Agents.Agent;
import io.humainary.substrates.ext.serventis.opt.role.Agents.Dimension;
import io.humainary.substrates.ext.serventis.opt.role.Agents.Sign;
import io.humainary.substrates.ext.serventis.opt.role.Agents.Signal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.humainary.substrates.api.Substrates.*;
import static io.humainary.substrates.ext.serventis.opt.role.Agents.Dimension.PROMISEE;
import static io.humainary.substrates.ext.serventis.opt.role.Agents.Dimension.PROMISER;
import static org.junit.jupiter.api.Assertions.assertEquals;


/// Tests for the [Agents] API.
///
/// @author William David Louth
/// @since 1.0

final class AgentsTest {

  private static final Cortex               CORTEX = cortex ();
  private static final Name                 NAME   = CORTEX.name ( "scaling.agent" );
  private              Circuit              circuit;
  private              Reservoir < Signal > reservoir;
  private              Agent                agent;

  @BeforeEach
  void setup () {

    circuit =
      CORTEX.circuit ();

    final var conduit =
      circuit.conduit (
        Agents::composer
      );

    agent =
      conduit.percept (
        NAME
      );

    reservoir =
      conduit.reservoir ();

  }

  // ========== PROMISER SIGNAL TESTS (Self-Perspective) ==========

  @Test
  void testAccept () {

    agent.accept ( PROMISER );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    final var ACCEPT = new Signal ( Sign.ACCEPT, PROMISER );
    assertEquals ( 1, signals.size () );
    assertEquals ( ACCEPT, signals.getFirst () );
    assertEquals ( Agents.Sign.ACCEPT, signals.getFirst ().sign () );
    assertEquals ( PROMISER, signals.getFirst ().dimension () );

  }

  @Test
  void testAccepted () {

    agent.accept ( PROMISEE );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    final var ACCEPTED = new Signal ( Sign.ACCEPT, PROMISEE );
    assertEquals ( 1, signals.size () );
    assertEquals ( ACCEPTED, signals.getFirst () );
    assertEquals ( Agents.Sign.ACCEPT, signals.getFirst ().sign () );
    assertEquals ( PROMISEE, signals.getFirst ().dimension () );

  }

  @Test
  void testBreach () {

    agent.breach ( PROMISER );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    final var BREACH = new Signal ( Sign.BREACH, PROMISER );
    assertEquals ( 1, signals.size () );
    assertEquals ( BREACH, signals.getFirst () );
    assertEquals ( Agents.Sign.BREACH, signals.getFirst ().sign () );
    assertEquals ( PROMISER, signals.getFirst ().dimension () );

  }

  @Test
  void testBreachPattern () {

    // Breach: BREACH → BREACHED (failed)
    agent.breach ( PROMISER );
    agent.breach ( PROMISEE );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    final var BREACH = new Signal ( Sign.BREACH, PROMISER );
    final var BREACHED = new Signal ( Sign.BREACH, PROMISEE );
    assertEquals ( 2, signals.size () );
    assertEquals ( BREACH, signals.get ( 0 ) );
    assertEquals ( BREACHED, signals.get ( 1 ) );

  }

  @Test
  void testBreached () {

    agent.breach ( PROMISEE );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    final var BREACHED = new Signal ( Sign.BREACH, PROMISEE );
    assertEquals ( 1, signals.size () );
    assertEquals ( BREACHED, signals.getFirst () );
    assertEquals ( Agents.Sign.BREACH, signals.getFirst ().sign () );
    assertEquals ( PROMISEE, signals.getFirst ().dimension () );

  }

  @Test
  void testCommitmentPattern () {

    // Commitment: PROMISE → PROMISED (observed)
    agent.promise ( PROMISER );
    agent.promise ( PROMISEE );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    final var PROMISE = new Signal ( Sign.PROMISE, PROMISER );
    final var PROMISED = new Signal ( Sign.PROMISE, PROMISEE );
    assertEquals ( 2, signals.size () );
    assertEquals ( PROMISE, signals.get ( 0 ) );
    assertEquals ( PROMISED, signals.get ( 1 ) );

  }

  @Test
  void testCompletePromiseLifecycle () {

    // Complete lifecycle from JavaDoc
    agent.inquire ( PROMISER );      // Discovery
    agent.offer ( PROMISEE );

    agent.promise ( PROMISER );      // Commitment
    agent.promise ( PROMISEE );

    agent.accept ( PROMISER );       // Dependency
    agent.accept ( PROMISEE );

    agent.depend ( PROMISER );       // Tracking
    agent.depend ( PROMISEE );

    agent.validate ( PROMISER );     // Validation
    agent.validate ( PROMISEE );

    agent.fulfill ( PROMISER );      // Fulfillment
    agent.fulfill ( PROMISEE );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 12, signals.size () );

    // Verify lifecycle progression
    final var INQUIRE = new Signal ( Sign.INQUIRE, PROMISER );
    final var OFFERED = new Signal ( Sign.OFFER, PROMISEE );
    final var PROMISE = new Signal ( Sign.PROMISE, PROMISER );
    final var PROMISED = new Signal ( Sign.PROMISE, PROMISEE );
    final var ACCEPT = new Signal ( Sign.ACCEPT, PROMISER );
    final var ACCEPTED = new Signal ( Sign.ACCEPT, PROMISEE );
    final var DEPEND = new Signal ( Sign.DEPEND, PROMISER );
    final var DEPENDED = new Signal ( Sign.DEPEND, PROMISEE );
    final var VALIDATE = new Signal ( Sign.VALIDATE, PROMISER );
    final var VALIDATED = new Signal ( Sign.VALIDATE, PROMISEE );
    final var FULFILL = new Signal ( Sign.FULFILL, PROMISER );
    final var FULFILLED = new Signal ( Sign.FULFILL, PROMISEE );
    assertEquals ( INQUIRE, signals.get ( 0 ) );
    assertEquals ( OFFERED, signals.get ( 1 ) );
    assertEquals ( PROMISE, signals.get ( 2 ) );
    assertEquals ( PROMISED, signals.get ( 3 ) );
    assertEquals ( ACCEPT, signals.get ( 4 ) );
    assertEquals ( ACCEPTED, signals.get ( 5 ) );
    assertEquals ( DEPEND, signals.get ( 6 ) );
    assertEquals ( DEPENDED, signals.get ( 7 ) );
    assertEquals ( VALIDATE, signals.get ( 8 ) );
    assertEquals ( VALIDATED, signals.get ( 9 ) );
    assertEquals ( FULFILL, signals.get ( 10 ) );
    assertEquals ( FULFILLED, signals.get ( 11 ) );

  }

  @Test
  void testDepend () {

    agent.depend ( PROMISER );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    final var DEPEND = new Signal ( Sign.DEPEND, PROMISER );
    assertEquals ( 1, signals.size () );
    assertEquals ( DEPEND, signals.getFirst () );
    assertEquals ( Agents.Sign.DEPEND, signals.getFirst ().sign () );
    assertEquals ( PROMISER, signals.getFirst ().dimension () );

  }

  @Test
  void testDepended () {

    agent.depend ( PROMISEE );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    final var DEPENDED = new Signal ( Sign.DEPEND, PROMISEE );
    assertEquals ( 1, signals.size () );
    assertEquals ( DEPENDED, signals.getFirst () );
    assertEquals ( Agents.Sign.DEPEND, signals.getFirst ().sign () );
    assertEquals ( PROMISEE, signals.getFirst ().dimension () );

  }

  @Test
  void testDependencyPattern () {

    // Dependency: ACCEPT → ACCEPTED (mutual)
    agent.accept ( PROMISER );
    agent.accept ( PROMISEE );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    final var ACCEPT = new Signal ( Sign.ACCEPT, PROMISER );
    final var ACCEPTED = new Signal ( Sign.ACCEPT, PROMISEE );
    assertEquals ( 2, signals.size () );
    assertEquals ( ACCEPT, signals.get ( 0 ) );
    assertEquals ( ACCEPTED, signals.get ( 1 ) );

  }

  // ========== PROMISEE SIGNAL TESTS (Other-Perspective) ==========

  @Test
  void testDimensionEnumValues () {

    final var dimensions = Dimension.values ();

    assertEquals ( 2, dimensions.length );
    assertEquals ( PROMISER, dimensions[0] );
    assertEquals ( PROMISEE, dimensions[1] );

  }

  @Test
  void testDiscoveryPattern () {

    // Discovery: INQUIRE → OFFERED
    agent.inquire ( PROMISER );
    agent.offer ( PROMISEE );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    final var INQUIRE = new Signal ( Sign.INQUIRE, PROMISER );
    final var OFFERED = new Signal ( Sign.OFFER, PROMISEE );
    assertEquals ( 2, signals.size () );
    assertEquals ( INQUIRE, signals.get ( 0 ) );
    assertEquals ( OFFERED, signals.get ( 1 ) );

  }

  @Test
  void testFulfill () {

    agent.fulfill ( PROMISER );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    final var FULFILL = new Signal ( Sign.FULFILL, PROMISER );
    assertEquals ( 1, signals.size () );
    assertEquals ( FULFILL, signals.getFirst () );
    assertEquals ( Agents.Sign.FULFILL, signals.getFirst ().sign () );
    assertEquals ( PROMISER, signals.getFirst ().dimension () );

  }

  @Test
  void testFulfilled () {

    agent.fulfill ( PROMISEE );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    final var FULFILLED = new Signal ( Sign.FULFILL, PROMISEE );
    assertEquals ( 1, signals.size () );
    assertEquals ( FULFILLED, signals.getFirst () );
    assertEquals ( Agents.Sign.FULFILL, signals.getFirst ().sign () );
    assertEquals ( PROMISEE, signals.getFirst ().dimension () );

  }

  @Test
  void testFulfillmentPattern () {

    // Fulfillment: FULFILL → FULFILLED (kept)
    agent.fulfill ( PROMISER );
    agent.fulfill ( PROMISEE );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    final var FULFILL = new Signal ( Sign.FULFILL, PROMISER );
    final var FULFILLED = new Signal ( Sign.FULFILL, PROMISEE );
    assertEquals ( 2, signals.size () );
    assertEquals ( FULFILL, signals.get ( 0 ) );
    assertEquals ( FULFILLED, signals.get ( 1 ) );

  }

  @Test
  void testInquire () {

    agent.inquire ( PROMISER );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    final var INQUIRE = new Signal ( Sign.INQUIRE, PROMISER );
    assertEquals ( 1, signals.size () );
    assertEquals ( INQUIRE, signals.getFirst () );
    assertEquals ( Agents.Sign.INQUIRE, signals.getFirst ().sign () );
    assertEquals ( PROMISER, signals.getFirst ().dimension () );

  }

  @Test
  void testInquired () {

    agent.inquire ( PROMISEE );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    final var INQUIRED = new Signal ( Sign.INQUIRE, PROMISEE );
    assertEquals ( 1, signals.size () );
    assertEquals ( INQUIRED, signals.getFirst () );
    assertEquals ( Agents.Sign.INQUIRE, signals.getFirst ().sign () );
    assertEquals ( PROMISEE, signals.getFirst ().dimension () );

  }

  @Test
  void testObservationPattern () {

    // Promise with observation
    agent.promise ( PROMISER );
    agent.observe ( PROMISER );
    agent.observe ( PROMISEE );
    agent.fulfill ( PROMISER );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    final var PROMISE = new Signal ( Sign.PROMISE, PROMISER );
    final var OBSERVE = new Signal ( Sign.OBSERVE, PROMISER );
    final var OBSERVED = new Signal ( Sign.OBSERVE, PROMISEE );
    final var FULFILL = new Signal ( Sign.FULFILL, PROMISER );
    assertEquals ( 4, signals.size () );
    assertEquals ( PROMISE, signals.get ( 0 ) );
    assertEquals ( OBSERVE, signals.get ( 1 ) );
    assertEquals ( OBSERVED, signals.get ( 2 ) );
    assertEquals ( FULFILL, signals.get ( 3 ) );

  }

  @Test
  void testObserve () {

    agent.observe ( PROMISER );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    final var OBSERVE = new Signal ( Sign.OBSERVE, PROMISER );
    assertEquals ( 1, signals.size () );
    assertEquals ( OBSERVE, signals.getFirst () );
    assertEquals ( Agents.Sign.OBSERVE, signals.getFirst ().sign () );
    assertEquals ( PROMISER, signals.getFirst ().dimension () );

  }

  @Test
  void testObserved () {

    agent.observe ( PROMISEE );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    final var OBSERVED = new Signal ( Sign.OBSERVE, PROMISEE );
    assertEquals ( 1, signals.size () );
    assertEquals ( OBSERVED, signals.getFirst () );
    assertEquals ( Agents.Sign.OBSERVE, signals.getFirst ().sign () );
    assertEquals ( PROMISEE, signals.getFirst ().dimension () );

  }

  // ========== ENUM TESTS ==========

  @Test
  void testOffer () {

    agent.offer ( PROMISER );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    final var OFFER = new Signal ( Sign.OFFER, PROMISER );
    assertEquals ( 1, signals.size () );
    assertEquals ( OFFER, signals.getFirst () );
    assertEquals ( Agents.Sign.OFFER, signals.getFirst ().sign () );
    assertEquals ( PROMISER, signals.getFirst ().dimension () );

  }

  @Test
  void testOffered () {

    agent.offer ( PROMISEE );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    final var OFFERED = new Signal ( Sign.OFFER, PROMISEE );
    assertEquals ( 1, signals.size () );
    assertEquals ( OFFERED, signals.getFirst () );
    assertEquals ( Agents.Sign.OFFER, signals.getFirst ().sign () );
    assertEquals ( PROMISEE, signals.getFirst ().dimension () );

  }

  @Test
  void testPromise () {

    agent.promise ( PROMISER );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    final var PROMISE = new Signal ( Sign.PROMISE, PROMISER );
    assertEquals ( 1, signals.size () );
    assertEquals ( PROMISE, signals.getFirst () );
    assertEquals ( Agents.Sign.PROMISE, signals.getFirst ().sign () );
    assertEquals ( PROMISER, signals.getFirst ().dimension () );

  }

  @Test
  void testPromiseBreachRecovery () {

    // Promise made, then breached, then retracted
    agent.promise ( PROMISER );
    agent.breach ( PROMISER );
    agent.retract ( PROMISER );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    final var PROMISE = new Signal ( Sign.PROMISE, PROMISER );
    final var BREACH = new Signal ( Sign.BREACH, PROMISER );
    final var RETRACT = new Signal ( Sign.RETRACT, PROMISER );
    assertEquals ( 3, signals.size () );
    assertEquals ( PROMISE, signals.get ( 0 ) );
    assertEquals ( BREACH, signals.get ( 1 ) );
    assertEquals ( RETRACT, signals.get ( 2 ) );

  }

  // ========== PROMISE LIFECYCLE PATTERN TESTS ==========

  @Test
  void testPromiseNetworkFormation () {

    // Agent A: Capacity Monitor
    final var monitorName = CORTEX.name ( "capacity.monitor" );

    // Agent B: Scaler
    final var scalerName = CORTEX.name ( "scaler" );

    final var conduit = circuit.conduit ( Agents::composer );
    final var monitor = conduit.percept ( monitorName );
    final var scaler = conduit.percept ( scalerName );
    final var signalReservoir = conduit.reservoir ();

    // Monitor inquires, scaler offers
    monitor.inquire ( PROMISER );         // PROMISER: Who can provide scaling?
    monitor.offer ( PROMISEE );         // PROMISEE: Scaler offered capability

    // Scaler's perspective
    scaler.offer ( PROMISER );            // PROMISER: I offer scaling
    scaler.inquire ( PROMISEE );         // PROMISEE: Monitor asked

    // Promise formation
    scaler.promise ( PROMISER );          // PROMISER: I promise to scale
    monitor.promise ( PROMISEE );        // PROMISEE: Scaler promised

    // Dependency formation
    monitor.accept ( PROMISER );          // PROMISER: I accept scaler's promise
    scaler.accept ( PROMISEE );         // PROMISEE: Monitor accepted my promise

    monitor.depend ( PROMISER );          // PROMISER: I depend on scaler
    scaler.depend ( PROMISEE );         // PROMISEE: Monitor depends on me

    // Fulfillment
    scaler.fulfill ( PROMISER );          // PROMISER: I fulfilled my promise
    monitor.fulfill ( PROMISEE );       // PROMISEE: Scaler fulfilled

    circuit.await ();

    final var allSignals = signalReservoir.drain ().toList ();

    assertEquals ( 12, allSignals.size () );

    // Verify monitor signals
    final var monitorSignals =
      allSignals
        .stream ()
        .filter ( c -> c.subject ().name ().equals ( monitorName ) )
        .map ( Capture::emission )
        .toList ();

    final var INQUIRE = new Signal ( Sign.INQUIRE, PROMISER );
    final var OFFERED = new Signal ( Sign.OFFER, PROMISEE );
    final var PROMISED = new Signal ( Sign.PROMISE, PROMISEE );
    final var ACCEPT = new Signal ( Sign.ACCEPT, PROMISER );
    final var DEPEND = new Signal ( Sign.DEPEND, PROMISER );
    final var FULFILLED = new Signal ( Sign.FULFILL, PROMISEE );
    assertEquals ( 6, monitorSignals.size () );
    assertEquals ( INQUIRE, monitorSignals.get ( 0 ) );
    assertEquals ( OFFERED, monitorSignals.get ( 1 ) );
    assertEquals ( PROMISED, monitorSignals.get ( 2 ) );
    assertEquals ( ACCEPT, monitorSignals.get ( 3 ) );
    assertEquals ( DEPEND, monitorSignals.get ( 4 ) );
    assertEquals ( FULFILLED, monitorSignals.get ( 5 ) );

    // Verify scaler signals
    final var scalerSignals =
      allSignals
        .stream ()
        .filter ( c -> c.subject ().name ().equals ( scalerName ) )
        .map ( Capture::emission )
        .toList ();

    final var OFFER = new Signal ( Sign.OFFER, PROMISER );
    final var INQUIRED = new Signal ( Sign.INQUIRE, PROMISEE );
    final var PROMISE = new Signal ( Sign.PROMISE, PROMISER );
    final var ACCEPTED = new Signal ( Sign.ACCEPT, PROMISEE );
    final var DEPENDED = new Signal ( Sign.DEPEND, PROMISEE );
    final var FULFILL = new Signal ( Sign.FULFILL, PROMISER );
    assertEquals ( 6, scalerSignals.size () );
    assertEquals ( OFFER, scalerSignals.get ( 0 ) );
    assertEquals ( INQUIRED, scalerSignals.get ( 1 ) );
    assertEquals ( PROMISE, scalerSignals.get ( 2 ) );
    assertEquals ( ACCEPTED, scalerSignals.get ( 3 ) );
    assertEquals ( DEPENDED, scalerSignals.get ( 4 ) );
    assertEquals ( FULFILL, scalerSignals.get ( 5 ) );

  }

  @Test
  void testPromised () {

    agent.promise ( PROMISEE );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    final var PROMISED = new Signal ( Sign.PROMISE, PROMISEE );
    assertEquals ( 1, signals.size () );
    assertEquals ( PROMISED, signals.getFirst () );
    assertEquals ( Agents.Sign.PROMISE, signals.getFirst ().sign () );
    assertEquals ( PROMISEE, signals.getFirst ().dimension () );

  }

  @Test
  void testRetract () {

    agent.retract ( PROMISER );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    final var RETRACT = new Signal ( Sign.RETRACT, PROMISER );
    assertEquals ( 1, signals.size () );
    assertEquals ( RETRACT, signals.getFirst () );
    assertEquals ( Agents.Sign.RETRACT, signals.getFirst ().sign () );
    assertEquals ( PROMISER, signals.getFirst ().dimension () );

  }

  @Test
  void testRetracted () {

    agent.retract ( PROMISEE );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    final var RETRACTED = new Signal ( Sign.RETRACT, PROMISEE );
    assertEquals ( 1, signals.size () );
    assertEquals ( RETRACTED, signals.getFirst () );
    assertEquals ( Agents.Sign.RETRACT, signals.getFirst ().sign () );
    assertEquals ( PROMISEE, signals.getFirst ().dimension () );

  }

  @Test
  void testRetractionPattern () {

    // Retraction: RETRACT → RETRACTED (withdrawn)
    agent.retract ( PROMISER );
    agent.retract ( PROMISEE );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    final var RETRACT = new Signal ( Sign.RETRACT, PROMISER );
    final var RETRACTED = new Signal ( Sign.RETRACT, PROMISEE );
    assertEquals ( 2, signals.size () );
    assertEquals ( RETRACT, signals.get ( 0 ) );
    assertEquals ( RETRACTED, signals.get ( 1 ) );

  }

  @Test
  void testSignEnumValues () {

    final var signs = Sign.values ();

    assertEquals ( 10, signs.length );
    assertEquals ( Agents.Sign.OFFER, signs[0] );
    assertEquals ( Agents.Sign.PROMISE, signs[1] );
    assertEquals ( Agents.Sign.ACCEPT, signs[2] );
    assertEquals ( Agents.Sign.FULFILL, signs[3] );
    assertEquals ( Agents.Sign.RETRACT, signs[4] );
    assertEquals ( Agents.Sign.BREACH, signs[5] );
    assertEquals ( Agents.Sign.INQUIRE, signs[6] );
    assertEquals ( Agents.Sign.OBSERVE, signs[7] );
    assertEquals ( Agents.Sign.DEPEND, signs[8] );
    assertEquals ( Agents.Sign.VALIDATE, signs[9] );

  }

  @Test
  void testSignal () {

    // Test direct signal() method for all sign and dimension combinations
    agent.signal ( Sign.OFFER, PROMISER );
    agent.signal ( Sign.OFFER, PROMISEE );
    agent.signal ( Sign.PROMISE, PROMISER );
    agent.signal ( Sign.PROMISE, PROMISEE );
    agent.signal ( Sign.ACCEPT, PROMISER );
    agent.signal ( Sign.ACCEPT, PROMISEE );
    agent.signal ( Sign.FULFILL, PROMISER );
    agent.signal ( Sign.FULFILL, PROMISEE );
    agent.signal ( Sign.BREACH, PROMISER );
    agent.signal ( Sign.BREACH, PROMISEE );
    agent.signal ( Sign.INQUIRE, PROMISER );
    agent.signal ( Sign.INQUIRE, PROMISEE );
    agent.signal ( Sign.DEPEND, PROMISER );
    agent.signal ( Sign.DEPEND, PROMISEE );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 14, signals.size () );
    assertEquals ( new Signal ( Sign.OFFER, PROMISER ), signals.get ( 0 ) );
    assertEquals ( new Signal ( Sign.OFFER, PROMISEE ), signals.get ( 1 ) );
    assertEquals ( new Signal ( Sign.PROMISE, PROMISER ), signals.get ( 2 ) );
    assertEquals ( new Signal ( Sign.PROMISE, PROMISEE ), signals.get ( 3 ) );
    assertEquals ( new Signal ( Sign.ACCEPT, PROMISER ), signals.get ( 4 ) );
    assertEquals ( new Signal ( Sign.ACCEPT, PROMISEE ), signals.get ( 5 ) );
    assertEquals ( new Signal ( Sign.FULFILL, PROMISER ), signals.get ( 6 ) );
    assertEquals ( new Signal ( Sign.FULFILL, PROMISEE ), signals.get ( 7 ) );
    assertEquals ( new Signal ( Sign.BREACH, PROMISER ), signals.get ( 8 ) );
    assertEquals ( new Signal ( Sign.BREACH, PROMISEE ), signals.get ( 9 ) );
    assertEquals ( new Signal ( Sign.INQUIRE, PROMISER ), signals.get ( 10 ) );
    assertEquals ( new Signal ( Sign.INQUIRE, PROMISEE ), signals.get ( 11 ) );
    assertEquals ( new Signal ( Sign.DEPEND, PROMISER ), signals.get ( 12 ) );
    assertEquals ( new Signal ( Sign.DEPEND, PROMISEE ), signals.get ( 13 ) );

  }

  // ========== PROMISE NETWORK TESTS (Multi-Agent) ==========

  @Test
  void testSignalCoverage () {

    // 10 signs × 2 dimensions = 20 signal combinations
    assertEquals ( 10, Sign.values ().length );
    assertEquals ( 2, Dimension.values ().length );

    // Verify all signals exist with correct sign/direction pairings
    final var OFFER = new Signal ( Sign.OFFER, PROMISER );
    final var OFFERED = new Signal ( Sign.OFFER, PROMISEE );
    assertEquals ( Agents.Sign.OFFER, OFFER.sign () );
    assertEquals ( PROMISER, OFFER.dimension () );

    assertEquals ( Agents.Sign.OFFER, OFFERED.sign () );
    assertEquals ( PROMISEE, OFFERED.dimension () );

  }

  @Test
  void testSubjectAttachment () {

    agent.promise ( PROMISER );

    circuit.await ();

    final var capture =
      reservoir
        .drain ()
        .findFirst ()
        .orElseThrow ();

    final var PROMISE = new Signal ( Sign.PROMISE, PROMISER );
    assertEquals ( NAME, capture.subject ().name () );
    assertEquals ( PROMISE, capture.emission () );

  }

  @Test
  void testValidate () {

    agent.validate ( PROMISER );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    final var VALIDATE = new Signal ( Sign.VALIDATE, PROMISER );
    assertEquals ( 1, signals.size () );
    assertEquals ( VALIDATE, signals.getFirst () );
    assertEquals ( Agents.Sign.VALIDATE, signals.getFirst ().sign () );
    assertEquals ( PROMISER, signals.getFirst ().dimension () );

  }

  @Test
  void testValidated () {

    agent.validate ( PROMISEE );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    final var VALIDATED = new Signal ( Sign.VALIDATE, PROMISEE );
    assertEquals ( 1, signals.size () );
    assertEquals ( VALIDATED, signals.getFirst () );
    assertEquals ( Agents.Sign.VALIDATE, signals.getFirst ().sign () );
    assertEquals ( PROMISEE, signals.getFirst ().dimension () );

  }

  @Test
  void testValidationCycle () {

    // Promise with ongoing validation
    agent.promise ( PROMISER );
    agent.validate ( PROMISER );
    agent.validate ( PROMISEE );
    agent.fulfill ( PROMISER );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    final var PROMISE = new Signal ( Sign.PROMISE, PROMISER );
    final var VALIDATE = new Signal ( Sign.VALIDATE, PROMISER );
    final var VALIDATED = new Signal ( Sign.VALIDATE, PROMISEE );
    final var FULFILL = new Signal ( Sign.FULFILL, PROMISER );
    assertEquals ( 4, signals.size () );
    assertEquals ( PROMISE, signals.get ( 0 ) );
    assertEquals ( VALIDATE, signals.get ( 1 ) );
    assertEquals ( VALIDATED, signals.get ( 2 ) );
    assertEquals ( FULFILL, signals.get ( 3 ) );

  }

}
