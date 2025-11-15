// Copyright (c) 2025 William David Louth

package io.humainary.serventis.jmh;

import io.humainary.substrates.api.Substrates;
import io.humainary.substrates.ext.serventis.ext.Actors;
import io.humainary.substrates.ext.serventis.ext.Actors.Actor;
import io.humainary.substrates.ext.serventis.ext.Actors.Sign;
import org.openjdk.jmh.annotations.*;

import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for Actors.Actor operations.
///
/// Measures performance of actor creation and sign emissions for Speech Act Theory
/// coordination, including ASK, ASSERT, EXPLAIN, REPORT, REQUEST, COMMAND,
/// ACKNOWLEDGE, DENY, CLARIFY, PROMISE, and DELIVER.
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class ActorOps
  implements Substrates {

  private static final String ACTOR_NAME = "user.william";
  private static final int    BATCH_SIZE = 1000;

  private Cortex                  cortex;
  private Circuit                 circuit;
  private Conduit < Actor, Sign > conduit;
  private Actor                   actor;
  private Name                    name;

  ///
  /// Benchmark actor retrieval from conduit.
  ///

  @Benchmark
  public Actor actor_from_conduit () {

    return
      conduit.percept (
        name
      );

  }

  ///
  /// Benchmark batched actor retrieval from conduit.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Actor actor_from_conduit_batch () {

    Actor result = null;

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      result =
        conduit.percept (
          name
        );
    }

    return
      result;

  }

  ///
  /// Benchmark emitting an ACKNOWLEDGE sign.
  ///

  @Benchmark
  public void emit_acknowledge () {

    actor.acknowledge ();

  }

  ///
  /// Benchmark batched ACKNOWLEDGE emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_acknowledge_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      actor.acknowledge ();
    }

  }

  ///
  /// Benchmark emitting an ASSERT sign.
  ///

  @Benchmark
  public void emit_affirm () {

    actor.affirm ();

  }

  ///
  /// Benchmark batched ASSERT emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_affirm_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      actor.affirm ();
    }

  }

  ///
  /// Benchmark emitting an ASK sign.
  ///

  @Benchmark
  public void emit_ask () {

    actor.ask ();

  }

  ///
  /// Benchmark batched ASK emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_ask_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      actor.ask ();
    }

  }

  ///
  /// Benchmark emitting a CLARIFY sign.
  ///

  @Benchmark
  public void emit_clarify () {

    actor.clarify ();

  }

  ///
  /// Benchmark batched CLARIFY emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_clarify_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      actor.clarify ();
    }

  }

  ///
  /// Benchmark emitting a COMMAND sign.
  ///

  @Benchmark
  public void emit_command () {

    actor.command ();

  }

  ///
  /// Benchmark batched COMMAND emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_command_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      actor.command ();
    }

  }

  ///
  /// Benchmark emitting a DELIVER sign.
  ///

  @Benchmark
  public void emit_deliver () {

    actor.deliver ();

  }

  ///
  /// Benchmark batched DELIVER emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_deliver_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      actor.deliver ();
    }

  }

  ///
  /// Benchmark emitting a DENY sign.
  ///

  @Benchmark
  public void emit_deny () {

    actor.deny ();

  }

  ///
  /// Benchmark batched DENY emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_deny_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      actor.deny ();
    }

  }

  ///
  /// Benchmark emitting an EXPLAIN sign.
  ///

  @Benchmark
  public void emit_explain () {

    actor.explain ();

  }

  ///
  /// Benchmark batched EXPLAIN emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_explain_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      actor.explain ();
    }

  }

  ///
  /// Benchmark emitting a PROMISE sign.
  ///

  @Benchmark
  public void emit_promise () {

    actor.promise ();

  }

  ///
  /// Benchmark batched PROMISE emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_promise_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      actor.promise ();
    }

  }

  ///
  /// Benchmark emitting a REPORT sign.
  ///

  @Benchmark
  public void emit_report () {

    actor.report ();

  }

  ///
  /// Benchmark batched REPORT emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_report_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      actor.report ();
    }

  }

  ///
  /// Benchmark emitting a REQUEST sign.
  ///

  @Benchmark
  public void emit_request () {

    actor.request ();

  }

  ///
  /// Benchmark batched REQUEST emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_request_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      actor.request ();
    }

  }

  ///
  /// Benchmark generic sign emission.
  ///

  @Benchmark
  public void emit_sign () {

    actor.sign (
      Sign.ACKNOWLEDGE
    );

  }

  ///
  /// Benchmark batched generic sign emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_sign_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      actor.sign (
        Sign.ACKNOWLEDGE
      );
    }

  }

  @Setup ( Level.Iteration )
  public void setupIteration () {

    circuit =
      cortex.circuit ();

    conduit =
      circuit.conduit (
        Actors::composer
      );

    actor =
      conduit.percept (
        name
      );

  }

  @Setup ( Level.Trial )
  public void setupTrial () {

    cortex =
      Substrates.cortex ();

    name =
      cortex.name (
        ACTOR_NAME
      );

  }

  @TearDown ( Level.Iteration )
  public void tearDownIteration () {

    circuit.close ();

  }

}
