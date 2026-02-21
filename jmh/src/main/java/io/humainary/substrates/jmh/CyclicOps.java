// Copyright (c) 2025 William David Louth

package io.humainary.substrates.jmh;

import io.humainary.substrates.api.Substrates;
import org.openjdk.jmh.annotations.*;

import static io.humainary.substrates.api.Substrates.Composer.pipe;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Level.Iteration;
import static org.openjdk.jmh.annotations.Level.Trial;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for cyclic emission patterns.
///
/// Measures the performance of self-reinforcing emission cycles where a subscriber
/// re-registers the same pipe on each emission, creating a feedback loop that
/// continues until a limit is reached.
///
/// This benchmark tests:
/// - Transit queue priority behavior (cascading emissions complete before next external input)
/// - Stack safety for deeply cascading chains (queue-based, not recursive)
/// - Neural-like signal propagation dynamics
///
/// The pattern mirrors the exp/Cycles.java example.
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class CyclicOps
  implements Substrates {

  private static final int CYCLE_LIMIT = 1000;
  private static final int BATCH_SIZE  = 1000;

  private Cortex  cortex;
  private Name    pipesName;
  private Name    cyclicName;
  private Circuit circuit;

  ///
  /// Benchmark cyclic emission setup and trigger (no await).
  ///
  /// Measures conduit creation, subscription, and initial emit cost.
  /// Does not wait for the cycle to complete.
  ///

  @Benchmark
  @OperationsPerInvocation ( CYCLE_LIMIT )
  public void cyclic_emit () {

    final var
      conduit =
      circuit.conduit (
        pipe (
          flow ->
            flow.limit (
              CYCLE_LIMIT
            )
        )
      );

    conduit.subscribe (
      circuit.subscriber (
        pipesName,
        ( subject, registrar ) ->
          registrar.register (
            conduit.percept (
              subject
            )
          )
      )
    );

    conduit
      .percept (
        cyclicName
      )
      .emit (
        new Object ()
      );

  }

  ///
  /// Benchmark full cyclic emission chain with await.
  ///
  /// Creates a self-reinforcing cycle where each emission triggers
  /// the subscriber to re-register the same pipe, causing another
  /// emission. The cycle continues until the limit flow operator
  /// stops propagation after CYCLE_LIMIT emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( CYCLE_LIMIT )
  public void cyclic_emit_await () {

    final var
      conduit =
      circuit.conduit (
        pipe (
          flow ->
            flow.limit (
              CYCLE_LIMIT
            )
        )
      );

    conduit.subscribe (
      circuit.subscriber (
        pipesName,
        ( subject, registrar ) ->
          registrar.register (
            conduit.percept (
              subject
            )
          )
      )
    );

    conduit
      .percept (
        cyclicName
      )
      .emit (
        new Object ()
      );

    circuit.await ();

  }

  ///
  /// Benchmark deep cyclic emission chain with await.
  ///
  /// Tests performance with longer cascading chains (10x limit) to validate
  /// that queue-based processing scales linearly.
  ///

  @Benchmark
  @OperationsPerInvocation ( CYCLE_LIMIT * 10 )
  public void cyclic_emit_deep_await () {

    final var
      conduit =
      circuit.conduit (
        pipe (
          flow ->
            flow.limit (
              CYCLE_LIMIT * 10
            )
        )
      );

    conduit.subscribe (
      circuit.subscriber (
        pipesName,
        ( subject, registrar ) ->
          registrar.register (
            conduit.percept (
              subject
            )
          )
      )
    );

    conduit
      .percept (
        cyclicName
      )
      .emit (
        new Object ()
      );

    circuit.await ();

  }

  ///
  /// Batch cyclic emission setup and trigger (no await).
  ///
  /// Amortizes JMH per-invocation overhead across BATCH_SIZE iterations.
  /// Each iteration creates a fresh conduit since flow.limit() is exhausted after one cycle.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE * CYCLE_LIMIT )
  public void cyclic_emit_batch () {

    for (
      int i = 0;
      i < BATCH_SIZE;
      i++
    ) {

      final var
        conduit =
        circuit.conduit (
          pipe (
            flow ->
              flow.limit (
                CYCLE_LIMIT
              )
          )
        );

      conduit.subscribe (
        circuit.subscriber (
          pipesName,
          ( subject, registrar ) ->
            registrar.register (
              conduit.percept (
                subject
              )
            )
        )
      );

      conduit
        .percept (
          cyclicName
        )
        .emit (
          new Object ()
        );

    }

  }

  ///
  /// Batch full cyclic emission chain with await.
  ///
  /// Amortizes JMH per-invocation overhead across BATCH_SIZE iterations.
  /// Each iteration creates a fresh conduit and awaits completion of the cycle.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE * CYCLE_LIMIT )
  public void cyclic_emit_await_batch () {

    for (
      int i = 0;
      i < BATCH_SIZE;
      i++
    ) {

      final var
        conduit =
        circuit.conduit (
          pipe (
            flow ->
              flow.limit (
                CYCLE_LIMIT
              )
          )
        );

      conduit.subscribe (
        circuit.subscriber (
          pipesName,
          ( subject, registrar ) ->
            registrar.register (
              conduit.percept (
                subject
              )
            )
        )
      );

      conduit
        .percept (
          cyclicName
        )
        .emit (
          new Object ()
        );

      circuit.await ();

    }

  }

  ///
  /// Batch deep cyclic emission chain with await.
  ///
  /// Amortizes JMH per-invocation overhead across BATCH_SIZE iterations
  /// with 10x cycle limit per iteration.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE * CYCLE_LIMIT * 10 )
  public void cyclic_emit_deep_await_batch () {

    for (
      int i = 0;
      i < BATCH_SIZE;
      i++
    ) {

      final var
        conduit =
        circuit.conduit (
          pipe (
            flow ->
              flow.limit (
                CYCLE_LIMIT * 10
              )
          )
        );

      conduit.subscribe (
        circuit.subscriber (
          pipesName,
          ( subject, registrar ) ->
            registrar.register (
              conduit.percept (
                subject
              )
            )
        )
      );

      conduit
        .percept (
          cyclicName
        )
        .emit (
          new Object ()
        );

      circuit.await ();

    }

  }

  @Setup ( Iteration )
  public void setupIteration () {

    circuit =
      cortex.circuit ();

  }

  @Setup ( Trial )
  public void setupTrial () {

    cortex =
      Substrates.cortex ();

    pipesName =
      cortex.name (
        "pipes"
      );

    cyclicName =
      cortex.name (
        "cyclic"
      );

  }

  @TearDown ( Iteration )
  public void tearDownIteration () {

    circuit.await ();
    circuit.close ();

  }

}
