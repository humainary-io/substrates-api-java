// Copyright (c) 2025 William David Louth

package io.humainary.serventis.jmh.opt.exec;

import io.humainary.substrates.api.Substrates;
import io.humainary.substrates.ext.serventis.opt.exec.Timers;
import io.humainary.substrates.ext.serventis.opt.exec.Timers.Signal;
import io.humainary.substrates.ext.serventis.opt.exec.Timers.Timer;
import org.openjdk.jmh.annotations.*;

import static io.humainary.substrates.ext.serventis.opt.exec.Timers.Dimension.DEADLINE;
import static io.humainary.substrates.ext.serventis.opt.exec.Timers.Dimension.THRESHOLD;
import static io.humainary.substrates.ext.serventis.opt.exec.Timers.Sign.MEET;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for Timers.Timer operations.
/// <p>
/// Measures performance of timer creation and signal emissions for time
/// constraint outcomes. Tests both MEET and MISS signs across DEADLINE
/// and THRESHOLD dimensions.
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class TimerOps implements Substrates {

  private static final String TIMER_NAME = "api.latency";
  private static final int    BATCH_SIZE = 1000;

  private Cortex                    cortex;
  private Circuit                   circuit;
  private Conduit < Timer, Signal > conduit;
  private Timer                     timer;
  private Name                      name;

  ///
  /// Benchmark emitting a MEET signal for DEADLINE.
  ///

  @Benchmark
  public void emit_meet_deadline () {

    timer.meet ( DEADLINE );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_meet_deadline_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) timer.meet ( DEADLINE );
  }

  ///
  /// Benchmark emitting a MEET signal for THRESHOLD.
  ///

  @Benchmark
  public void emit_meet_threshold () {

    timer.meet ( THRESHOLD );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_meet_threshold_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) timer.meet ( THRESHOLD );
  }

  ///
  /// Benchmark emitting a MISS signal for DEADLINE.
  ///

  @Benchmark
  public void emit_miss_deadline () {

    timer.miss ( DEADLINE );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_miss_deadline_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) timer.miss ( DEADLINE );
  }

  ///
  /// Benchmark emitting a MISS signal for THRESHOLD.
  ///

  @Benchmark
  public void emit_miss_threshold () {

    timer.miss ( THRESHOLD );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_miss_threshold_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) timer.miss ( THRESHOLD );
  }

  ///
  /// Benchmark generic signal emission.
  ///

  @Benchmark
  public void emit_signal () {

    timer.signal (
      MEET,
      THRESHOLD
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
      timer.signal (
        MEET,
        THRESHOLD
      );

    }

  }

  ///
  /// Benchmark timer retrieval from conduit.
  ///

  @Benchmark
  public Timer timer_from_conduit () {

    return
      conduit.percept (
        name
      );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Timer timer_from_conduit_batch () {
    Timer result = null;
    for ( var i = 0; i < BATCH_SIZE; i++ ) result = conduit.percept ( name );
    return result;
  }

  @Setup ( Level.Iteration )
  public void setupIteration () {

    circuit =
      cortex.circuit ();

    conduit =
      circuit.conduit (
        Timers::composer
      );

    timer =
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
        TIMER_NAME
      );

  }

  @TearDown ( Level.Iteration )
  public void tearDownIteration () {

    circuit.close ();

  }

}
