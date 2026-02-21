// Copyright (c) 2025 William David Louth

package io.humainary.substrates.jmh;

import io.humainary.substrates.api.Substrates;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

import static org.openjdk.jmh.annotations.Level.Trial;

///
/// Benchmark for State and Slot operations.
///
/// Measures performance of state transformations (adding slots, compaction, value retrieval)
/// and slot accessor methods (name, type, value). State is an immutable collection of
/// named, typed values (slots) used for metadata and context.
///

@State ( Scope.Benchmark )
@BenchmarkMode ( Mode.AverageTime )
@OutputTimeUnit ( TimeUnit.NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class StateOps
  implements Substrates {

  private static final String SLOT_NAME_1 = "slot1";
  private static final String SLOT_NAME_2 = "slot2";
  private static final String SLOT_NAME_3 = "slot3";
  private static final int    INT_VAL     = 42;
  private static final long   LONG_VAL    = 42L;
  private static final String STR_VAL     = "value";
  private static final int    BATCH_SIZE  = 1000;

  private Name             name1;
  private Name             name2;
  private Name             name3;
  private State            emptyState;
  private State            multiSlotState;
  private Slot < Integer > intSlot;

  @Setup ( Trial )
  public void setupTrial () {

    final Cortex cortex = Substrates.cortex ();

    name1 =
      cortex.name (
        SLOT_NAME_1
      );

    name2 =
      cortex.name (
        SLOT_NAME_2
      );

    name3 =
      cortex.name (
        SLOT_NAME_3
      );

    emptyState =
      cortex.state ();

    intSlot =
      cortex.slot (
        name1,
        INT_VAL
      );

    // Create state with multiple slots including duplicates for compaction testing
    multiSlotState =
      emptyState
        .state ( intSlot )
        .state (
          cortex.slot (
            name2,
            LONG_VAL
          )
        ).state (
          cortex.slot (
            name3,
            STR_VAL
          )
        ).state (
          name1,
          INT_VAL + 1
        ); // Duplicate name, different value

  }

  ///
  /// Benchmark slot name accessor.
  ///

  @Benchmark
  public Name slot_name () {

    return
      intSlot.name ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Name slot_name_batch () {
    Name result = null;
    for ( var i = 0; i < BATCH_SIZE; i++ ) result = intSlot.name ();
    return result;
  }

  ///
  /// Benchmark slot type accessor.
  ///

  @Benchmark
  public Class < Integer > slot_type () {

    return
      intSlot.type ();

  }

  ///
  /// Benchmark slot value accessor.
  ///

  @Benchmark
  public Integer slot_value () {

    return
      intSlot.value ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Integer slot_value_batch () {
    Integer result = null;
    for ( var i = 0; i < BATCH_SIZE; i++ ) result = intSlot.value ();
    return result;
  }

  ///
  /// Benchmark state compaction (removing duplicates).
  ///

  @Benchmark
  public State state_compact () {

    return
      multiSlotState.compact ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public State state_compact_batch () {
    State result = null;
    for ( var i = 0; i < BATCH_SIZE; i++ ) result = multiSlotState.compact ();
    return result;
  }

  ///
  /// Benchmark state iteration over slots.
  ///

  @Benchmark
  public int state_iterate_slots () {

    int
      count =
      0;

    for (
      final var _ : multiSlotState
    ) {
      count++;
    }

    return
      count;

  }

  ///
  /// Benchmark adding an int slot to state.
  ///

  @Benchmark
  public State state_slot_add_int () {

    return
      emptyState.state (
        name1,
        INT_VAL
      );

  }

  ///
  /// BATCHED BENCHMARKS
  /// These benchmarks measure amortized per-operation cost by executing
  /// operations in tight loops, reducing measurement noise for fast operations.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public State state_slot_add_int_batch () {
    State result = null;
    for ( var i = 0; i < BATCH_SIZE; i++ ) result = emptyState.state ( name1, INT_VAL );
    return result;
  }

  ///
  /// Benchmark adding a long slot to state.
  ///

  @Benchmark
  public State state_slot_add_long () {

    return
      emptyState.state (
        name2,
        LONG_VAL
      );

  }

  ///
  /// Benchmark adding a Slot object to state.
  ///

  @Benchmark
  public State state_slot_add_object () {

    return
      emptyState.state (
        intSlot
      );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public State state_slot_add_object_batch () {
    State result = null;
    for ( var i = 0; i < BATCH_SIZE; i++ ) result = emptyState.state ( intSlot );
    return result;
  }

  ///
  /// Benchmark adding a string slot to state.
  ///

  @Benchmark
  public State state_slot_add_string () {

    return
      emptyState.state (
        name3,
        STR_VAL
      );

  }

  ///
  /// Benchmark reading value from state (with default fallback).
  ///

  @Benchmark
  public Integer state_value_read () {

    return
      multiSlotState.value (
        intSlot
      );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Integer state_value_read_batch () {
    Integer result = null;
    for ( var i = 0; i < BATCH_SIZE; i++ ) result = multiSlotState.value ( intSlot );
    return result;
  }

  ///
  /// Benchmark reading multiple values from state (stream).
  ///

  @Benchmark
  public long state_values_stream () {

    return
      multiSlotState.values (
        intSlot
      ).count ();

  }

}
