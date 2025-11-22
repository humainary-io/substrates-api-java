// Copyright (c) 2025 William David Louth

package io.humainary.substrates.jmh;

import io.humainary.substrates.api.Substrates;
import org.openjdk.jmh.annotations.*;

import java.util.List;

import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Level.Trial;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for Cortex factory operations.
///
/// Measures performance of Cortex factory methods for creating circuits, names, pipes,
/// pools, scopes, sinks, slots, and states. Cortex is the entry point for creating
/// runtime substrate instances.
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class CortexOps
  implements Substrates {

  private static final String          NAME_STR   = "test";
  private static final String          PATH       = "parent.child.leaf";
  private static final List < String > NAME_LIST  = List.of ( "parent", "child", "leaf" );
  private static final int             INT_VAL    = 42;
  private static final long            LONG_VAL   = 42L;
  private static final double          DBL_VAL    = 42.0;
  private static final String          STR_VAL    = "value";
  private static final int             BATCH_SIZE = 1000;

  private Cortex cortex;
  private Name   name;

  ///
  /// Benchmark creating a circuit with generated name.
  ///

  @Benchmark
  public Circuit circuit () {

    final var
      result =
      cortex.circuit ();

    result.close ();

    return
      result;

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Circuit circuit_batch () {
    Circuit result = null;
    for ( var i = 0; i < BATCH_SIZE; i++ ) {
      result = cortex.circuit ();
      result.close ();
    }
    return result;
  }

  ///
  /// Benchmark creating a circuit with explicit name.
  ///

  @Benchmark
  public Circuit circuit_named () {

    final var
      result =
      cortex.circuit (
        name
      );

    result.close ();

    return
      result;

  }

  ///
  /// Benchmark getting current execution context.
  ///

  @Benchmark
  public Current current () {

    return
      cortex.current ();

  }

  ///
  /// Benchmark creating name from Class.
  ///

  @Benchmark
  public Name name_class () {

    return
      cortex.name (
        String.class
      );

  }

  ///
  /// Benchmark creating name from enum.
  ///

  @Benchmark
  public Name name_enum () {

    return
      cortex.name (
        TimeUnit.SECONDS
      );

  }

  ///
  /// Benchmark creating name from Iterable.
  ///

  @Benchmark
  public Name name_iterable () {

    return
      cortex.name (
        NAME_LIST
      );

  }

  ///
  /// Benchmark creating name from path string.
  ///

  @Benchmark
  public Name name_path () {

    return
      cortex.name (
        PATH
      );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Name name_path_batch () {
    Name result = null;
    for ( var i = 0; i < BATCH_SIZE; i++ ) result = cortex.name ( PATH );
    return result;
  }

  ///
  /// Benchmark creating name from simple string.
  ///

  @Benchmark
  public Name name_string () {

    return
      cortex.name (
        NAME_STR
      );

  }

  ///
  /// BATCHED BENCHMARKS
  /// These benchmarks measure amortized per-operation cost by executing
  /// operations in tight loops, reducing measurement noise for fast operations.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Name name_string_batch () {
    Name result = null;
    for ( var i = 0; i < BATCH_SIZE; i++ ) result = cortex.name ( NAME_STR );
    return result;
  }

  ///
  /// Benchmark creating empty pipe (discarding).
  ///

  @Benchmark
  public Pipe < Integer > pipe_empty () {

    return
      cortex.pipe (
        Integer.class
      );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Pipe < Integer > pipe_empty_batch () {
    Pipe < Integer > result = null;
    for ( var i = 0; i < BATCH_SIZE; i++ ) result = cortex.pipe ( Integer.class );
    return result;
  }

  ///
  /// Benchmark creating pipe from observer.
  ///

  @Benchmark
  public Pipe < Integer > pipe_observer () {

    return
      cortex.pipe (
        _ -> {
        }
      );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Pipe < Integer > pipe_observer_batch () {
    Pipe < Integer > result = null;
    for ( var i = 0; i < BATCH_SIZE; i++ )
      result = cortex.pipe ( _ -> {
      } );
    return result;
  }

  ///
  /// Benchmark creating transforming pipe.
  ///

  @Benchmark
  public Pipe < Integer > pipe_transform () {

    final var
      target =
      cortex.pipe (
        Integer.class
      );

    return
      cortex.pipe (
        x -> x * 2,
        target
      );

  }

  ///
  /// Benchmark creating scope with generated name.
  ///

  @Benchmark
  public Scope scope () {

    final var
      result =
      cortex.scope ();

    result.close ();

    return
      result;

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Scope scope_batch () {
    Scope result = null;
    for ( var i = 0; i < BATCH_SIZE; i++ ) {
      result = cortex.scope ();
      result.close ();
    }
    return result;
  }

  ///
  /// Benchmark creating scope with explicit name.
  ///

  @Benchmark
  public Scope scope_named () {

    final var
      result =
      cortex.scope (
        name
      );

    result.close ();

    return
      result;

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

  ///
  /// Benchmark creating a slot with boolean value.
  ///

  @Benchmark
  public Slot < Boolean > slot_boolean () {

    return
      cortex.slot (
        name,
        true
      );

  }

  ///
  /// Benchmark creating a slot with double value.
  ///

  @Benchmark
  public Slot < Double > slot_double () {

    return
      cortex.slot (
        name,
        DBL_VAL
      );

  }

  ///
  /// Benchmark creating a slot with int value.
  ///

  @Benchmark
  public Slot < Integer > slot_int () {

    return
      cortex.slot (
        name,
        INT_VAL
      );

  }

  ///
  /// Benchmark creating a slot with long value.
  ///

  @Benchmark
  public Slot < Long > slot_long () {

    return
      cortex.slot (
        name,
        LONG_VAL
      );

  }

  ///
  /// Benchmark creating a slot with string value.
  ///

  @Benchmark
  public Slot < String > slot_string () {

    return
      cortex.slot (
        name,
        STR_VAL
      );

  }

  ///
  /// Benchmark creating an empty state.
  ///

  @Benchmark
  public State state_empty () {

    return
      cortex.state ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public State state_empty_batch () {
    State result = null;
    for ( var i = 0; i < BATCH_SIZE; i++ ) result = cortex.state ();
    return result;
  }

  private enum TimeUnit {
    SECONDS
  }

}
