// Copyright (c) 2025 William David Louth

package io.humainary.substrates.jmh;

import io.humainary.substrates.api.Substrates;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.openjdk.jmh.annotations.Level.Iteration;
import static org.openjdk.jmh.annotations.Level.Trial;

///
/// Benchmark for Pipe operations (HOT PATH).
///
/// Measures performance of pipe emission operations - the most critical hot path
/// in the substrate with a sub-3ns target. Benchmarks cover different pipe types
/// (empty, observer, transform), topologies (single, chained, fan-out), and
/// emission patterns. Emissions enqueue to the circuit's event queue and are
/// processed sequentially on the circuit thread.
///

@State ( Scope.Benchmark )
@BenchmarkMode ( Mode.AverageTime )
@OutputTimeUnit ( TimeUnit.NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class PipeOps
  implements Substrates {

  private static final String  NAME_STR = "test";
  private static final Integer VALUE    = 42;
  private static final int     COUNT    = 10;

  private Cortex           cortex;
  private Circuit          circuit;
  private Name             name;
  private Pipe < Integer > emptyPipe;
  private Pipe < Integer > observerPipe;
  private Pipe < Integer > transformPipe;
  private Pipe < Integer > asyncPipe;
  private Pipe < Integer > chainedPipe;
  private Pipe < Integer > fanoutPipe;
  private AtomicInteger    counter;

  ///
  /// PARAMETERIZED: Pipe chain depth (1, 5, 10, 20 pipes).
  /// Measures how emission cost scales with chain length.
  ///

  @Benchmark
  @OperationsPerInvocation ( COUNT )
  public void emit_chain_depth_1 () {

    final var
      pipe =
      buildChain ( 1 );

    for (
      int i = 0;
      i < COUNT;
      i++
    ) {
      pipe.emit (
        VALUE + i
      );
    }

  }

  @Benchmark
  @OperationsPerInvocation ( COUNT )
  public void emit_chain_depth_10 () {

    final var
      pipe =
      buildChain ( 10 );

    for (
      int i = 0;
      i < COUNT;
      i++
    ) {
      pipe.emit (
        VALUE + i
      );
    }

  }

  @Benchmark
  @OperationsPerInvocation ( COUNT )
  public void emit_chain_depth_20 () {

    final var
      pipe =
      buildChain ( 20 );

    for (
      int i = 0;
      i < COUNT;
      i++
    ) {
      pipe.emit (
        VALUE + i
      );
    }

  }

  @Benchmark
  @OperationsPerInvocation ( COUNT )
  public void emit_chain_depth_5 () {

    final var
      pipe =
      buildChain ( 5 );

    for (
      int i = 0;
      i < COUNT;
      i++
    ) {
      pipe.emit (
        VALUE + i
      );
    }

  }

  ///
  /// PARAMETERIZED: Fan-out width (1, 5, 10, 20 targets).
  /// Measures how emission cost scales with broadcast width.
  ///

  @Benchmark
  @OperationsPerInvocation ( COUNT )
  public void emit_fanout_width_1 () {

    final var
      pipe =
      buildFanout ( 1 );

    for (
      int i = 0;
      i < COUNT;
      i++
    ) {
      pipe.emit (
        VALUE + i
      );
    }

  }

  @Benchmark
  @OperationsPerInvocation ( COUNT )
  public void emit_fanout_width_10 () {

    final var
      pipe =
      buildFanout ( 10 );

    for (
      int i = 0;
      i < COUNT;
      i++
    ) {
      pipe.emit (
        VALUE + i
      );
    }

  }

  @Benchmark
  @OperationsPerInvocation ( COUNT )
  public void emit_fanout_width_20 () {

    final var
      pipe =
      buildFanout ( 20 );

    for (
      int i = 0;
      i < COUNT;
      i++
    ) {
      pipe.emit (
        VALUE + i
      );
    }

  }

  @Benchmark
  @OperationsPerInvocation ( COUNT )
  public void emit_fanout_width_5 () {

    final var
      pipe =
      buildFanout ( 5 );

    for (
      int i = 0;
      i < COUNT;
      i++
    ) {
      pipe.emit (
        VALUE + i
      );
    }

  }

  ///
  /// HOT PATH: 10 consecutive emissions (no await).
  /// Measures pure emission cost without synchronization.
  ///

  @Benchmark
  @OperationsPerInvocation ( COUNT )
  public void emit_no_await () {

    for (
      int i = 0;
      i < COUNT;
      i++
    ) {
      emptyPipe.emit (
        VALUE + i
      );
    }

  }

  ///
  /// Benchmark emission to async pipe (through circuit queue).
  ///

  @Benchmark
  public void emit_to_async_pipe () {

    asyncPipe.emit (
      VALUE
    );

  }

  ///
  /// Benchmark emission through chained pipes.
  ///

  @Benchmark
  public void emit_to_chained_pipes () {

    chainedPipe.emit (
      VALUE
    );

  }

  ///
  /// Benchmark double transformation (composed transformations).
  ///

  @Benchmark
  public void emit_to_double_transform () {

    final var
      target =
      cortex.pipe (
        Integer.class
      );

    final var
      inner =
      cortex.pipe (
        ( Integer v ) -> v * 2,
        target
      );

    final var
      pipe =
      cortex.pipe (
        ( Integer v ) -> v + 1,
        inner
      );

    pipe.emit (
      VALUE
    );

  }

  ///
  /// Baseline: emission to empty pipe (discard - fastest path).
  ///

  @Benchmark
  public void emit_to_empty_pipe () {

    emptyPipe.emit (
      VALUE
    );

  }

  ///
  /// Benchmark emission to fan-out topology (one to many).
  ///

  @Benchmark
  public void emit_to_fanout () {

    fanoutPipe.emit (
      VALUE
    );

  }

  ///
  /// Benchmark emission to receptor pipe (callback on circuit thread).
  ///

  @Benchmark
  public void emit_to_receptor_pipe () {

    observerPipe.emit (
      VALUE
    );

  }

  ///
  /// Benchmark emission to transforming pipe.
  ///

  @Benchmark
  public void emit_to_transform_pipe () {

    transformPipe.emit (
      VALUE
    );

  }

  ///
  /// ROUND-TRIP: Single emission + await (full synchronization).
  /// Measures complete emission-to-processing cycle time.
  ///

  @Benchmark
  public void emit_with_await () {

    observerPipe.emit (
      VALUE
    );

    circuit.await ();

  }

  ///
  /// ROUND-TRIP: Single emission + await with counter observer.
  /// Measures emission + async processing + counter increment.
  ///

  @Benchmark
  public void emit_with_counter_await () {

    final var
      pipe =
      cortex.pipe (
        _ -> counter.incrementAndGet ()
      );

    pipe.emit (
      VALUE
    );

    circuit.await ();

  }

  @Setup ( Iteration )
  public void setupIteration () {

    circuit =
      cortex.circuit (
        name
      );

    counter =
      new AtomicInteger ( 0 );

    // Empty pipe (discarding)
    emptyPipe =
      cortex.pipe (
        Integer.class
      );

    // Receptor pipe (no-op callback)
    observerPipe =
      cortex.pipe (
        _ -> {
        }
      );

    // Transform pipe
    final var
      transformTarget =
      cortex.pipe (
        Integer.class
      );

    transformPipe =
      cortex.pipe (
        ( Integer v ) -> v * 2,
        transformTarget
      );

    // Async pipe (through circuit)
    final var
      asyncTarget =
      cortex.pipe (
        Integer.class
      );

    asyncPipe =
      circuit.pipe (
        asyncTarget
      );

    // Chained pipes (3 levels of transformation)
    final var
      chain3 =
      cortex.pipe (
        Integer.class
      );

    final var
      chain2 =
      cortex.pipe (
        ( Integer v ) -> v * 2,
        chain3
      );

    chainedPipe =
      cortex.pipe (
        ( Integer v ) -> v + 1,
        chain2
      );

    // Fan-out pipe (one to three via observer)
    fanoutPipe =
      cortex.pipe (
        ( final Integer v ) -> {
          cortex.pipe ( Integer.class ).emit ( v );
          cortex.pipe ( Integer.class ).emit ( v );
          cortex.pipe ( Integer.class ).emit ( v );
        }
      );

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

  @TearDown ( Iteration )
  public void tearDownIteration () {

    circuit.close ();

  }

  ///
  /// Helper: Build chain of specified depth.
  ///

  private Pipe < Integer > buildChain (
    final int depth
  ) {

    Pipe < Integer >
      current =
      cortex.pipe (
        Integer.class
      );

    for (
      int i = 0;
      i < depth;
      i++
    ) {

      final var
        target =
        current;

      current =
        cortex.pipe (
          ( Integer v ) -> v,
          target
        );

    }

    return
      current;

  }

  ///
  /// Helper: Build fan-out of specified width.
  ///

  @SuppressWarnings ( "unchecked" )
  private Pipe < Integer > buildFanout (
    final int width
  ) {

    final Pipe < Integer >[] targets =
      new Pipe[width];

    for (
      int i = 0;
      i < width;
      i++
    ) {
      targets[i] =
        cortex.pipe (
          Integer.class
        );
    }

    return
      cortex.pipe (
        ( final Integer v ) -> {
          for (
            final var target : targets
          ) {
            target.emit ( v );
          }
        }
      );

  }

}
