// Copyright (c) 2025 William David Louth

package io.humainary.serventis.jmh.opt.flow;

import io.humainary.substrates.api.Substrates;
import io.humainary.substrates.ext.serventis.opt.flow.Valves;
import io.humainary.substrates.ext.serventis.opt.flow.Valves.Sign;
import io.humainary.substrates.ext.serventis.opt.flow.Valves.Valve;
import org.openjdk.jmh.annotations.*;

import static io.humainary.substrates.ext.serventis.opt.flow.Valves.Sign.PASS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for Valves.Valve operations.
///
/// Measures performance of valve creation and sign emissions for adaptive
/// flow control operations: PASS, DENY, EXPAND, CONTRACT, DROP, and DRAIN.
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class ValveOps implements Substrates {

  private static final String VALVE_NAME = "api.ratelimit";
  private static final int    BATCH_SIZE = 1000;

  private Cortex                  cortex;
  private Circuit                 circuit;
  private Conduit < Valve, Sign > conduit;
  private Valve                   valve;
  private Name                    name;

  ///
  /// Benchmark emitting a CONTRACT sign.
  ///

  @Benchmark
  public void emit_contract () {

    valve.contract ();

  }

  ///
  /// Benchmark batched CONTRACT emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_contract_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      valve.contract ();
    }

  }

  ///
  /// Benchmark emitting a DENY sign.
  ///

  @Benchmark
  public void emit_deny () {

    valve.deny ();

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
      valve.deny ();
    }

  }

  ///
  /// Benchmark emitting a DRAIN sign.
  ///

  @Benchmark
  public void emit_drain () {

    valve.drain ();

  }

  ///
  /// Benchmark batched DRAIN emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_drain_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      valve.drain ();
    }

  }

  ///
  /// Benchmark emitting a DROP sign.
  ///

  @Benchmark
  public void emit_drop () {

    valve.drop ();

  }

  ///
  /// Benchmark batched DROP emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_drop_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      valve.drop ();
    }

  }

  ///
  /// Benchmark emitting an EXPAND sign.
  ///

  @Benchmark
  public void emit_expand () {

    valve.expand ();

  }

  ///
  /// Benchmark batched EXPAND emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_expand_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      valve.expand ();
    }

  }

  ///
  /// Benchmark emitting a PASS sign.
  ///

  @Benchmark
  public void emit_pass () {

    valve.pass ();

  }

  ///
  /// Benchmark batched PASS emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_pass_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      valve.pass ();
    }

  }

  ///
  /// Benchmark generic sign emission.
  ///

  @Benchmark
  public void emit_sign () {

    valve.sign (
      PASS
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
      valve.sign (
        PASS
      );
    }

  }

  @Setup ( Level.Iteration )
  public void setupIteration () {

    circuit =
      cortex.circuit ();

    conduit =
      circuit.conduit (
        Valves::composer
      );

    valve =
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
        VALVE_NAME
      );

  }

  @TearDown ( Level.Iteration )
  public void tearDownIteration () {

    circuit.close ();

  }

  ///
  /// Benchmark valve retrieval from conduit.
  ///

  @Benchmark
  public Valve valve_from_conduit () {

    return
      conduit.percept (
        name
      );

  }

  ///
  /// Benchmark batched valve retrieval from conduit.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Valve valve_from_conduit_batch () {

    Valve result = null;

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

}
