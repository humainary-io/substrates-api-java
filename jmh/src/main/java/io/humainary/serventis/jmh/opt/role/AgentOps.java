// Copyright (c) 2025 William David Louth

package io.humainary.serventis.jmh.opt.role;

import io.humainary.substrates.api.Substrates;
import io.humainary.substrates.ext.serventis.opt.role.Agents;
import io.humainary.substrates.ext.serventis.opt.role.Agents.Agent;
import io.humainary.substrates.ext.serventis.opt.role.Agents.Signal;
import org.openjdk.jmh.annotations.*;

import static io.humainary.substrates.ext.serventis.opt.role.Agents.Dimension.PROMISEE;
import static io.humainary.substrates.ext.serventis.opt.role.Agents.Dimension.PROMISER;
import static io.humainary.substrates.ext.serventis.opt.role.Agents.Sign.PROMISE;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for Agents.Agent operations.
///
/// Measures performance of agent creation and signal emissions for Promise Theory
/// coordination from both PROMISER (self-perspective) and PROMISEE (observed-perspective)
/// dimensions, including OFFER, PROMISE, ACCEPT, FULFILL, BREACH, RETRACT, INQUIRE,
/// OBSERVE, DEPEND, and VALIDATE.
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class AgentOps
  implements Substrates {

  private static final String AGENT_NAME = "capacity.monitor";
  private static final int    BATCH_SIZE = 1000;

  private Cortex                    cortex;
  private Circuit                   circuit;
  private Conduit < Agent, Signal > conduit;
  private Agent                     agent;
  private Name                      name;

  ///
  /// Benchmark agent retrieval from conduit.
  ///

  @Benchmark
  public Agent agent_from_conduit () {

    return
      conduit.percept (
        name
      );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Agent agent_from_conduit_batch () {
    Agent result = null;
    for ( var i = 0; i < BATCH_SIZE; i++ ) result = conduit.percept ( name );
    return result;
  }

  ///
  /// Benchmark emitting an ACCEPT signal (PROMISER).
  ///

  @Benchmark
  public void emit_accept () {

    agent.accept ( PROMISER );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_accept_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) agent.accept ( PROMISER );
  }

  ///
  /// Benchmark emitting an ACCEPTED signal (PROMISEE).
  ///

  @Benchmark
  public void emit_accepted () {

    agent.accept ( PROMISEE );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_accepted_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) agent.accept ( PROMISEE );
  }

  ///
  /// Benchmark emitting a BREACH signal (PROMISER).
  ///

  @Benchmark
  public void emit_breach () {

    agent.breach ( PROMISER );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_breach_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) agent.breach ( PROMISER );
  }

  ///
  /// Benchmark emitting a BREACHED signal (PROMISEE).
  ///

  @Benchmark
  public void emit_breached () {

    agent.breach ( PROMISEE );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_breached_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) agent.breach ( PROMISEE );
  }

  ///
  /// Benchmark emitting a DEPEND signal (PROMISER).
  ///

  @Benchmark
  public void emit_depend () {

    agent.depend ( PROMISER );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_depend_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) agent.depend ( PROMISER );
  }

  ///
  /// Benchmark emitting a DEPENDED signal (PROMISEE).
  ///

  @Benchmark
  public void emit_depended () {

    agent.depend ( PROMISEE );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_depended_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) agent.depend ( PROMISEE );
  }

  ///
  /// Benchmark emitting a FULFILL signal (PROMISER).
  ///

  @Benchmark
  public void emit_fulfill () {

    agent.fulfill ( PROMISER );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_fulfill_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) agent.fulfill ( PROMISER );
  }

  ///
  /// Benchmark emitting a FULFILLED signal (PROMISEE).
  ///

  @Benchmark
  public void emit_fulfilled () {

    agent.fulfill ( PROMISEE );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_fulfilled_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) agent.fulfill ( PROMISEE );
  }

  ///
  /// Benchmark emitting an INQUIRE signal (PROMISER).
  ///

  @Benchmark
  public void emit_inquire () {

    agent.inquire ( PROMISER );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_inquire_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) agent.inquire ( PROMISER );
  }

  ///
  /// Benchmark emitting an INQUIRED signal (PROMISEE).
  ///

  @Benchmark
  public void emit_inquired () {

    agent.inquire ( PROMISEE );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_inquired_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) agent.inquire ( PROMISEE );
  }

  ///
  /// Benchmark emitting an OBSERVE signal (PROMISER).
  ///

  @Benchmark
  public void emit_observe () {

    agent.observe ( PROMISER );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_observe_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) agent.observe ( PROMISER );
  }

  ///
  /// Benchmark emitting an OBSERVED signal (PROMISEE).
  ///

  @Benchmark
  public void emit_observed () {

    agent.observe ( PROMISEE );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_observed_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) agent.observe ( PROMISEE );
  }

  ///
  /// Benchmark emitting an OFFER signal (PROMISER).
  ///

  @Benchmark
  public void emit_offer () {

    agent.offer ( PROMISER );

  }


  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_offer_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) agent.offer ( PROMISER );
  }

  ///
  /// Benchmark emitting an OFFERED signal (PROMISEE).
  ///

  @Benchmark
  public void emit_offered () {

    agent.offer ( PROMISEE );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_offered_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) agent.offer ( PROMISEE );
  }

  ///
  /// Benchmark emitting a PROMISE signal (PROMISER).
  ///

  @Benchmark
  public void emit_promise () {

    agent.promise ( PROMISER );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_promise_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) agent.promise ( PROMISER );
  }

  ///
  /// Benchmark emitting a PROMISED signal (PROMISEE).
  ///

  @Benchmark
  public void emit_promised () {

    agent.promise ( PROMISEE );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_promised_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) agent.promise ( PROMISEE );
  }

  ///
  /// Benchmark emitting a RETRACT signal (PROMISER).
  ///

  @Benchmark
  public void emit_retract () {

    agent.retract ( PROMISER );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_retract_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) agent.retract ( PROMISER );
  }

  ///
  /// Benchmark emitting a RETRACTED signal (PROMISEE).
  ///

  @Benchmark
  public void emit_retracted () {

    agent.retract ( PROMISEE );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_retracted_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) agent.retract ( PROMISEE );
  }

  ///
  /// Benchmark generic signal emission.
  ///

  @Benchmark
  public void emit_signal () {

    agent.signal (
      PROMISE,
      PROMISER
    );

  }

  ///
  /// Benchmark batched generic signal emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_signal_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      agent.signal (
        PROMISE,
        PROMISER
      );

    }

  }

  ///
  /// Benchmark emitting a VALIDATE signal (PROMISER).
  ///

  @Benchmark
  public void emit_validate () {

    agent.validate ( PROMISER );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_validate_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) agent.validate ( PROMISER );
  }

  ///
  /// Benchmark emitting a VALIDATED signal (PROMISEE).
  ///

  @Benchmark
  public void emit_validated () {

    agent.validate ( PROMISEE );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_validated_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) agent.validate ( PROMISEE );
  }

  @Setup ( Level.Iteration )
  public void setupIteration () {

    circuit =
      cortex.circuit ();

    conduit =
      circuit.conduit (
        Agents::composer
      );

    agent =
      conduit.percept (
        name
      );

    circuit.await ();

  }

  @Setup ( Level.Trial )
  public void setupTrial () {

    cortex =
      Substrates.cortex ();

    name =
      cortex.name (
        AGENT_NAME
      );

  }

  @TearDown ( Level.Iteration )
  public void tearDownIteration () {

    circuit.close ();

  }

}
