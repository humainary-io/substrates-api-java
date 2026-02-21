// Copyright (c) 2025 William David Louth

package io.humainary.serventis.jmh.sdk;

import io.humainary.substrates.api.Substrates;
import io.humainary.substrates.ext.serventis.sdk.Systems;
import io.humainary.substrates.ext.serventis.sdk.Systems.Signal;
import io.humainary.substrates.ext.serventis.sdk.Systems.System;
import org.openjdk.jmh.annotations.*;

import static io.humainary.substrates.ext.serventis.sdk.Systems.Dimension.*;
import static io.humainary.substrates.ext.serventis.sdk.Systems.Sign.NORMAL;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for Systems.System operations.
/// <p>
/// Measures performance of system constraint signal emissions with various
/// process control signs (NORMAL, LIMIT, ALARM, FAULT) and constraint
/// dimensions (SPACE, FLOW, LINK, TIME).
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class SystemOps implements Substrates {

  private static final String SYSTEM_NAME = "db.pool";
  private static final int    BATCH_SIZE  = 1000;

  private Cortex                     cortex;
  private Circuit                    circuit;
  private Conduit < System, Signal > conduit;
  private System                     system;
  private Name                       name;

  ///
  /// Benchmark emitting an ALARM signal with FLOW dimension.
  ///

  @Benchmark
  public void emit_alarm_flow () {

    system.alarm (
      FLOW
    );

  }

  ///
  /// Benchmark batched ALARM FLOW emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_alarm_flow_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      system.alarm (
        FLOW
      );
    }

  }

  ///
  /// Benchmark emitting a FAULT signal with LINK dimension.
  ///

  @Benchmark
  public void emit_fault_link () {

    system.fault (
      LINK
    );

  }

  ///
  /// Benchmark batched FAULT LINK emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_fault_link_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      system.fault (
        LINK
      );
    }

  }

  ///
  /// Benchmark emitting a LIMIT signal with TIME dimension.
  ///

  @Benchmark
  public void emit_limit_time () {

    system.limit (
      TIME
    );

  }

  ///
  /// Benchmark batched LIMIT TIME emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_limit_time_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      system.limit (
        TIME
      );
    }

  }

  ///
  /// Benchmark emitting a NORMAL signal with SPACE dimension.
  ///

  @Benchmark
  public void emit_normal_space () {

    system.normal (
      SPACE
    );

  }

  ///
  /// Benchmark batched NORMAL SPACE emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_normal_space_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      system.normal (
        SPACE
      );
    }

  }

  ///
  /// Benchmark generic signal emission.
  ///

  @Benchmark
  public void emit_signal () {

    system.signal (
      NORMAL,
      SPACE
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
      system.signal (
        NORMAL,
        SPACE
      );
    }

  }

  @Setup ( Level.Iteration )
  public void setupIteration () {

    circuit =
      cortex.circuit ();

    conduit =
      circuit.conduit (
        Systems::composer
      );

    system =
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
        SYSTEM_NAME
      );

  }

  ///
  /// Benchmark system retrieval from conduit.
  ///

  @Benchmark
  public System system_from_conduit () {

    return
      conduit.percept (
        name
      );

  }

  ///
  /// Benchmark batched system retrieval from conduit.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public System system_from_conduit_batch () {

    System result = null;

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

  @TearDown ( Level.Iteration )
  public void tearDownIteration () {

    circuit.close ();

  }

}
