// Copyright (c) 2025 William David Louth

package io.humainary.substrates.jmh;

import io.humainary.substrates.api.Substrates;
import org.openjdk.jmh.annotations.*;

import java.util.List;

import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Level.Trial;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for Name operations.
///
/// Measures performance of name creation, parsing, and hierarchy operations.
/// Names are interned and cached, providing identity-based equality for equivalent hierarchies.
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class NameOps
  implements Substrates {

  private static final String FIRST              = "first";
  private static final String SECOND             = "second";
  private static final String THIRD              = "third";
  private static final String FIRST_SECOND_THIRD = "first.second.third";
  private static final int    BATCH_SIZE         = 1000;

  private Cortex            cortex;
  private Name              firstName;
  private Name              deepName;
  private List < String >   nameParts;
  private List < TestEnum > enumList;

  ///
  /// NAME CONSTRUCTION: Deep chaining (5 levels).
  ///

  @Benchmark
  public Name name_chained_deep () {

    return
      cortex.name ( "a" )
        .name ( "b" )
        .name ( "c" )
        .name ( "d" )
        .name ( "e" );

  }

  ///
  /// Benchmark name creation by chaining.
  ///

  @Benchmark
  public Name name_chaining () {

    return
      cortex.name (
        FIRST
      ).name (
        SECOND
      ).name (
        THIRD
      );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Name name_chaining_batch () {
    Name result = null;
    for ( var i = 0; i < BATCH_SIZE; i++ ) result = cortex.name ( FIRST ).name ( SECOND ).name ( THIRD );
    return result;
  }

  ///
  /// TRAVERSAL: Compare names hierarchically.
  ///

  @Benchmark
  public int name_compare () {

    return
      deepName.compareTo (
        firstName
      );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public int name_compare_batch () {
    int result = 0;
    for ( var i = 0; i < BATCH_SIZE; i++ ) result = deepName.compareTo ( firstName );
    return result;
  }

  ///
  /// TRAVERSAL: Measure depth() calculation.
  ///

  @Benchmark
  public int name_depth () {

    return
      deepName.depth ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public int name_depth_batch () {
    int result = 0;
    for ( var i = 0; i < BATCH_SIZE; i++ ) result = deepName.depth ();
    return result;
  }

  ///
  /// TRAVERSAL: Navigate to parent via enclosure().
  ///

  @Benchmark
  public Name name_enclosure () {

    return
      deepName
        .enclosure ()
        .orElse (
          null
        );

  }

  ///
  /// NAME CONSTRUCTION: From enum constant.
  ///

  @Benchmark
  public Name name_from_enum () {

    return
      cortex.name (
        TestEnum.SECOND
      );

  }

  ///
  /// NAME CONSTRUCTION: From Iterable<>.
  ///

  @Benchmark
  public Name name_from_iterable () {

    return
      cortex.name (
        nameParts
      );

  }

  ///
  /// NAME CONSTRUCTION: From Iterator<>.
  ///

  @Benchmark
  public Name name_from_iterator () {

    return
      cortex.name (
        nameParts.iterator ()
      );

  }

  ///
  /// NAME CONSTRUCTION: From Iterable with mapper function.
  ///

  @Benchmark
  public Name name_from_mapped_iterable () {

    return
      cortex.name (
        enumList,
        Enum::name
      );

  }

  ///
  /// NAME CONSTRUCTION: Append Name to existing Name.
  ///

  @Benchmark
  public Name name_from_name () {

    return
      firstName.name (
        cortex.name (
          SECOND
        )
      );

  }

  ///
  /// Benchmark name creation from a string.
  ///

  @Benchmark
  public Name name_from_string () {

    return
      cortex.name (
        FIRST
      );

  }

  ///
  /// BATCHED BENCHMARKS
  /// These benchmarks measure amortized per-operation cost by executing
  /// operations in tight loops, reducing measurement noise for fast operations.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Name name_from_string_batch () {
    Name result = null;
    for ( var i = 0; i < BATCH_SIZE; i++ ) result = cortex.name ( FIRST );
    return result;
  }

  ///
  /// INTERNING: Chained construction identity.
  ///

  @Benchmark
  public boolean name_interning_chained () {

    final var
      parsed =
      cortex.name (
        FIRST_SECOND_THIRD
      );

    final var
      chained =
      cortex.name ( FIRST )
        .name ( SECOND )
        .name ( THIRD );

    return
      parsed == chained;

  }

  ///
  /// INTERNING: Identity check for same path.
  ///

  @Benchmark
  public boolean name_interning_same_path () {

    final var
      name1 =
      cortex.name (
        FIRST_SECOND_THIRD
      );

    final var
      name2 =
      cortex.name (
        FIRST_SECOND_THIRD
      );

    return
      name1 == name2;

  }

  ///
  /// INTERNING: Verify segment reuse in chaining.
  ///

  @Benchmark
  public boolean name_interning_segments () {

    final var
      first1 =
      cortex.name (
        FIRST
      );

    final var
      first2 =
      cortex.name (
          FIRST
        ).name (
          SECOND
        ).enclosure ()
        .orElse (
          null
        );

    return
      first1 == first2;

  }

  ///
  /// TRAVERSAL: Iterate through hierarchy.
  ///

  @Benchmark
  public long name_iterate_hierarchy () {

    long
      count =
      0;

    for (
      final var
        _ :
      deepName
    ) {
      count++;
    }

    return
      count;

  }

  ///
  /// Benchmark name creation by parsing a dotted string.
  ///

  @Benchmark
  public Name name_parsing () {

    return
      cortex.name (
        FIRST_SECOND_THIRD
      );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Name name_parsing_batch () {
    Name result = null;
    for ( var i = 0; i < BATCH_SIZE; i++ ) result = cortex.name ( FIRST_SECOND_THIRD );
    return result;
  }

  ///
  /// TRAVERSAL: Generate path string.
  ///

  @Benchmark
  public CharSequence name_path_generation () {

    return
      deepName.path (
        '.'
      );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public CharSequence name_path_generation_batch () {
    CharSequence result = null;
    for ( var i = 0; i < BATCH_SIZE; i++ ) result = deepName.path ( '.' );
    return result;
  }

  @Setup ( Trial )
  public void setupTrial () {

    cortex =
      Substrates.cortex ();

    firstName =
      cortex.name (
        FIRST
      );

    deepName =
      cortex.name ( "root" )
        .name ( "level1" )
        .name ( "level2" )
        .name ( "level3" )
        .name ( "level4" );

    nameParts =
      List.of (
        FIRST,
        SECOND,
        THIRD
      );

    enumList =
      List.of (
        TestEnum.FIRST,
        TestEnum.SECOND,
        TestEnum.THIRD
      );

  }

  private enum TestEnum {
    FIRST,
    SECOND,
    THIRD
  }

}
