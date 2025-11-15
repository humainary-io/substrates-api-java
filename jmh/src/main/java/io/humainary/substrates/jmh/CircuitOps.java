// Copyright (c) 2025 William David Louth

package io.humainary.substrates.jmh;

import io.humainary.substrates.api.Substrates;
import org.openjdk.jmh.annotations.*;

import static io.humainary.substrates.api.Substrates.Composer.pipe;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Level.Trial;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for Circuit operations.
///
/// Measures performance of circuit operations including creation, conduit creation,
/// async pipes, and coordination primitives. Each circuit maintains its own
/// event processing queue with a dedicated virtual thread.
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class CircuitOps
  implements Substrates {

  private static final String NAME_STR = "test";

  private Cortex  cortex;
  private Name    name;
  private Circuit hotCircuit;

  ///
  /// Benchmark conduit creation with circuit's name.
  ///

  @Benchmark
  public Conduit < Pipe < Integer >, Integer > conduit_create_close () {

    final var
      circuit =
      cortex.circuit ();

    final var
      result =
      circuit.conduit (
        pipe (
          Integer.class
        )
      );

    circuit.close ();

    return
      result;

  }

  ///
  /// Benchmark conduit creation with explicit name.
  ///

  @Benchmark
  public Conduit < Pipe < Integer >, Integer > conduit_create_named () {

    final var
      circuit =
      cortex.circuit ();

    final var
      result =
      circuit.conduit (
        name,
        pipe (
          Integer.class
        )
      );

    circuit.close ();

    return
      result;

  }

  ///
  /// Benchmark conduit creation with flow configurer.
  ///

  @Benchmark
  public Conduit < Pipe < Integer >, Integer > conduit_create_with_flow () {

    final var
      circuit =
      cortex.circuit ();

    final var
      result =
      circuit.conduit (
        name,
        pipe (
          Integer.class
        ),
        Flow::diff
      );

    circuit.close ();

    return
      result;

  }

  ///
  /// Benchmark circuit creation and close (full lifecycle).
  ///

  @Benchmark
  public void create_and_close () {

    final var
      circuit =
      cortex.circuit ();

    circuit.close ();

  }

  ///
  /// Benchmark circuit await (queue drain).
  ///

  @Benchmark
  public void create_await_close () {

    final var
      circuit =
      cortex.circuit ();

    circuit.await ();
    circuit.close ();

  }

  ///
  /// Benchmark hot circuit await (queue drain on running circuit).
  ///

  @Benchmark
  public void hot_await_queue_drain () {

    hotCircuit.await ();

  }

  ///
  /// Benchmark hot conduit creation (circuit already running).
  ///

  @Benchmark
  public Conduit < Pipe < Integer >, Integer > hot_conduit_create () {

    return
      hotCircuit.conduit (
        pipe (
          Integer.class
        )
      );

  }

  //
  // HOT PATH BENCHMARKS
  // These benchmarks measure operations on an already-running circuit,
  // isolating the actual operation cost from lifecycle overhead.
  //

  ///
  /// Benchmark hot conduit creation with explicit name.
  ///

  @Benchmark
  public Conduit < Pipe < Integer >, Integer > hot_conduit_create_named () {

    return
      hotCircuit.conduit (
        name,
        pipe (
          Integer.class
        )
      );

  }

  ///
  /// Benchmark hot conduit creation with flow configurer.
  ///

  @Benchmark
  public Conduit < Pipe < Integer >, Integer > hot_conduit_create_with_flow () {

    return
      hotCircuit.conduit (
        name,
        pipe (
          Integer.class
        ),
        Flow::diff
      );

  }

  ///
  /// Benchmark hot async pipe creation.
  ///

  @Benchmark
  public Pipe < Integer > hot_pipe_async () {

    final var
      target =
      cortex.pipe (
        Integer.class
      );

    return
      hotCircuit.pipe (
        target
      );

  }

  ///
  /// Benchmark hot async pipe creation with flow.
  ///

  @Benchmark
  public Pipe < Integer > hot_pipe_async_with_flow () {

    final var
      target =
      cortex.pipe (
        Integer.class
      );

    return
      hotCircuit.pipe (
        target,
        flow -> flow.guard ( v -> v > 0 )
      );

  }

  ///
  /// Benchmark async pipe creation.
  ///

  @Benchmark
  public Pipe < Integer > pipe_async () {

    final var
      circuit =
      cortex.circuit ();

    final var
      target =
      cortex.pipe (
        Integer.class
      );

    final var
      result =
      circuit.pipe (
        target
      );

    circuit.close ();

    return
      result;

  }

  ///
  /// Benchmark async pipe creation with flow.
  ///

  @Benchmark
  public Pipe < Integer > pipe_async_with_flow () {

    final var
      circuit =
      cortex.circuit ();

    final var
      target =
      cortex.pipe (
        Integer.class
      );

    final var
      result =
      circuit.pipe (
        target,
        flow -> flow.guard ( v -> v > 0 )
      );

    circuit.close ();

    return
      result;

  }

  @Setup ( Level.Iteration )
  public void setupIteration () {

    hotCircuit =
      cortex.circuit ();

  }

  @Setup ( Trial )
  public void setupTrial () {

    cortex =
      Substrates.cortex ();

    name =
      cortex.name (
        NAME_STR
      );

  }

  @TearDown ( Level.Iteration )
  public void tearDownIteration () {

    hotCircuit.close ();

  }

}
