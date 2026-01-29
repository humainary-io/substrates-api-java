// Copyright (c) 2025 William David Louth

package io.humainary.substrates.jmh;

import io.humainary.substrates.api.Substrates;
import org.openjdk.jmh.annotations.*;

import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Level.Trial;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for Id operations.
///
/// Measures performance of Id creation and string conversion.
/// Id generation uses ThreadLocalRandom for UUID v4 compliance.
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class IdOps
  implements Substrates {

  private static final int BATCH_SIZE = 1000;

  private Cortex                                cortex;
  private Circuit                               circuit;
  private Conduit < Pipe < Integer >, Integer > conduit;
  private Subject < ? >                         subject;
  private Id                                    id;

  @Setup ( Trial )
  public void setup () {

    cortex =
      Substrates.cortex ();

    circuit =
      cortex.circuit ();

    conduit =
      circuit.conduit (
        cortex.name ( "benchmark" ),
        Composer.pipe ()
      );

    subject =
      conduit.subject ();

    id =
      subject.id ();

  }

  ///
  /// Benchmark Id.toString() conversion.
  ///

  @Benchmark
  public String id_toString () {

    return
      id.toString ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public String id_toString_batch () {

    String result = null;
    for ( var i = 0; i < BATCH_SIZE; i++ ) result = id.toString ();
    return result;

  }

  ///
  /// Benchmark Id retrieval from subject.
  ///

  @Benchmark
  public Id id_from_subject () {

    return
      subject.id ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Id id_from_subject_batch () {

    Id result = null;
    for ( var i = 0; i < BATCH_SIZE; i++ ) result = subject.id ();
    return result;

  }

}
