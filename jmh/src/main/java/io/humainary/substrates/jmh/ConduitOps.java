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
/// Benchmark for Conduit operations.
///
/// Measures performance of conduit percept lookup (percept operations from Lookup interface) and
/// subscription (subscribe operations from Source interface). Conduits cache percepts
/// by name, providing consistent identity for named channels, and support dynamic
/// subscription to channel emissions.
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class ConduitOps
  implements Substrates {

  private static final String NAME_STR   = "test";
  private static final int    BATCH_SIZE = 1000;

  private Cortex                                cortex;
  private Circuit                               circuit;
  private Conduit < Pipe < Integer >, Integer > conduit;
  private Name                                  name;
  private Scope                                 scope;

  ///
  /// Benchmark percept retrieval via percept(Name) - Lookup interface.
  ///

  @Benchmark
  public Pipe < Integer > get_by_name () {

    return
      conduit.percept (
        name
      );

  }

  ///
  /// BATCHED BENCHMARKS
  /// These benchmarks measure amortized per-operation cost by executing
  /// operations in tight loops, reducing measurement noise for fast operations.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Pipe < Integer > get_by_name_batch () {
    Pipe < Integer > result = null;
    for ( var i = 0; i < BATCH_SIZE; i++ ) result = conduit.percept ( name );
    return result;
  }

  ///
  /// Benchmark percept retrieval via percept(Substrate) - Lookup interface.
  ///

  @Benchmark
  public Pipe < Integer > get_by_substrate () {

    return
      conduit.percept (
        scope
      );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Pipe < Integer > get_by_substrate_batch () {
    Pipe < Integer > result = null;
    for ( var i = 0; i < BATCH_SIZE; i++ ) result = conduit.percept ( scope );
    return result;
  }

  ///
  /// Benchmark cached percept retrieval (pooling behavior).
  ///

  @Benchmark
  public boolean get_cached () {

    final var
      first =
      conduit.percept (
        name
      );

    final var
      second =
      conduit.percept (
        name
      );

    return
      first == second;

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public boolean get_cached_batch () {
    boolean result = false;
    for ( var i = 0; i < BATCH_SIZE; i++ ) {
      final var first = conduit.percept ( name );
      final var second = conduit.percept ( name );
      result = first == second;
    }
    return result;
  }

  @Setup ( Iteration )
  public void setupIteration () {

    circuit =
      cortex.circuit ();

    conduit =
      circuit.conduit (
        pipe ()
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

    scope =
      cortex.scope (
        name
      );

  }

  ///
  /// Benchmark subscription to conduit.
  ///

  @Benchmark
  public Subscription subscribe () {

    final var
      subscriber =
      cortex. < Integer > subscriber (
        name,
        ( _, _ ) -> {
        }
      );

    return
      conduit.subscribe (
        subscriber
      );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Subscription subscribe_batch () {
    Subscription result = null;
    for ( var i = 0; i < BATCH_SIZE; i++ ) {
      final var subscriber = cortex. < Integer > subscriber ( name, ( _, _ ) -> {
      } );
      result = conduit.subscribe ( subscriber );
    }
    return result;
  }

  ///
  /// Benchmark subscription with emission (triggers subscriber callback).
  ///

  @Benchmark
  public void subscribe_with_emission_await () {

    final var
      subscriber =
      cortex. < Integer > subscriber (
        name,
        ( _, _ ) -> {
        }
      );

    final var
      subscription =
      conduit.subscribe (
        subscriber
      );

    final var
      channel =
      conduit.percept (
        name
      );

    channel.emit (
      42
    );

    circuit.await ();

    subscription.close ();

  }

  @TearDown ( Iteration )
  public void tearDownIteration () {

    circuit.close ();

  }

  @TearDown ( Trial )
  public void tearDownTrial () {

    scope.close ();

  }

}
