// Copyright (c) 2025 William David Louth

package io.humainary.substrates.jmh;

import io.humainary.substrates.api.Substrates;
import org.openjdk.jmh.annotations.*;

import static io.humainary.substrates.api.Substrates.Composer.pipe;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Level.Trial;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for Subject operations.
///
/// Measures performance of subject comparison with optimized compareTo implementations.
/// Subjects are accessed from conduits to test real-world comparison scenarios.
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class SubjectOps
  implements Substrates {

  private static final int BATCH_SIZE = 1000;

  private Cortex                                cortex;
  private Circuit                               circuit;
  private Conduit < Pipe < Integer >, Integer > conduitA;
  private Conduit < Pipe < Integer >, Integer > conduitB;
  private Conduit < Pipe < Integer >, Integer > conduitC;

  private Subject < ? > subjectA;
  private Subject < ? > subjectB;
  private Subject < ? > subjectC;

  @Setup ( Trial )
  public void setup () {

    cortex =
      Substrates.cortex ();

    circuit =
      cortex.circuit ();

    // Create conduits with different names to get subjects at different levels
    conduitA =
      circuit.conduit (
        cortex.name ( "conduitA" ),
        pipe ()
      );

    conduitB =
      circuit.conduit (
        cortex.name ( "conduitB" ),
        pipe ()
      );

    conduitC =
      circuit.conduit (
        cortex.name ( "conduitC" ),
        pipe ()
      );

    // Get subjects from conduits
    subjectA = conduitA.subject ();
    subjectB = conduitB.subject ();
    subjectC = conduitC.subject ();

  }

  ///
  /// COMPARISON: Compare subjects from different conduits.
  ///

  @Benchmark
  public int subject_compare () {

    return
      subjectA.compareTo (
        subjectB
      );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public int subject_compare_batch () {

    int result = 0;
    for ( var i = 0; i < BATCH_SIZE; i++ ) result = subjectA.compareTo ( subjectB );
    return result;

  }

  @SuppressWarnings ( "EqualsWithItself" )
  @Benchmark
  public int subject_compare_same () {

    return
      subjectA.compareTo (
        subjectA
      );

  }

  @SuppressWarnings ( "EqualsWithItself" )
  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public int subject_compare_same_batch () {

    int result = 0;
    for ( var i = 0; i < BATCH_SIZE; i++ ) result = subjectA.compareTo ( subjectA );
    return result;

  }

  @Benchmark
  public int subject_compare_three_way () {

    return
      subjectA.compareTo ( subjectB ) +
        subjectB.compareTo ( subjectC ) +
        subjectC.compareTo ( subjectA );

  }

}
