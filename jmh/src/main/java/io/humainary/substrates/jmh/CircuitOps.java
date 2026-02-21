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
/// ## Circuit Creation Cost
///
/// Circuit creation (~290ns) is dominated by virtual thread startup in the Valve:
/// ```java
/// this.worker = Thread.ofVirtual().start(this);
/// ```
///
/// This cost includes:
/// - Platform thread pool scheduling
/// - Virtual thread stack allocation
/// - Thread-local storage initialization
///
/// The `create_and_close` benchmarks isolate this lifecycle cost.
/// The `hot_*` benchmarks measure operations on already-running circuits.
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class CircuitOps
  implements Substrates {

  private static final String NAME_STR   = "test";
  private static final int    BATCH_SIZE = 1000;

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

  //
  // CIRCUIT LIFECYCLE BENCHMARKS
  // These benchmarks measure the cost of circuit creation, which is dominated
  // by virtual thread startup (~290ns). This cost applies whenever a new
  // circuit is created, regardless of what operations are performed on it.
  //

  ///
  /// Benchmark circuit creation and close (full lifecycle).
  /// This measures the baseline cost of creating a circuit, which includes
  /// starting a virtual thread for the circuit's valve worker.
  ///

  @Benchmark
  public void create_and_close () {

    final var
      circuit =
      cortex.circuit ();

    circuit.close ();

  }

  ///
  /// Benchmark batched circuit creation to measure amortized cost.
  /// The per-operation cost should be similar to non-batched (~290ns)
  /// since each circuit requires its own virtual thread.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void create_and_close_batch () {

    for (
      int i = 0;
      i < BATCH_SIZE;
      i++
    ) {

      final var
        circuit =
        cortex.circuit ();

      circuit.close ();

    }

  }

  ///
  /// Benchmark named circuit creation.
  ///

  @Benchmark
  public void create_named_and_close () {

    final var
      circuit =
      cortex.circuit (
        name
      );

    circuit.close ();

  }

  ///
  /// Benchmark creating multiple circuits to show linear scaling.
  /// Expected: ~290ns × 5 = ~1450ns
  ///

  @Benchmark
  public void create_multiple_and_close () {

    final var c1 = cortex.circuit ();
    final var c2 = cortex.circuit ();
    final var c3 = cortex.circuit ();
    final var c4 = cortex.circuit ();
    final var c5 = cortex.circuit ();

    c1.close ();
    c2.close ();
    c3.close ();
    c4.close ();
    c5.close ();

  }

  //
  // HOT PATH BENCHMARKS
  // These benchmarks measure operations on an already-running circuit,
  // isolating the actual operation cost from lifecycle overhead.
  //

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

    return
      hotCircuit.pipe (
        Receptor.of ( Integer.class )
      );

  }

  ///
  /// Benchmark hot async pipe creation with flow.
  ///

  @Benchmark
  public Pipe < Integer > hot_pipe_async_with_flow () {

    return
      hotCircuit.pipe (
        Receptor.of ( Integer.class ),
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
      result =
      circuit.pipe (
        Receptor.of (
          Integer.class
        )
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
      result =
      circuit.pipe (
        Receptor.of ( Integer.class ),
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
