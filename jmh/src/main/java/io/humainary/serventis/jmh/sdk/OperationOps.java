// Copyright (c) 2025 William David Louth

package io.humainary.serventis.jmh.sdk;

import io.humainary.substrates.api.Substrates;
import io.humainary.substrates.ext.serventis.sdk.Operations;
import io.humainary.substrates.ext.serventis.sdk.Operations.Operation;
import io.humainary.substrates.ext.serventis.sdk.Operations.Sign;
import org.openjdk.jmh.annotations.*;

import static io.humainary.substrates.ext.serventis.sdk.Operations.Sign.BEGIN;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for Operations.Operation methods.
///
/// Measures performance of operation creation and sign emissions for
/// action bracketing: BEGIN and END.
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class OperationOps implements Substrates {

  private static final String OPERATION_NAME = "db.query";
  private static final int    BATCH_SIZE     = 1000;

  private Cortex                      cortex;
  private Circuit                     circuit;
  private Conduit < Operation, Sign > conduit;
  private Operation                   operation;
  private Name                        name;

  ///
  /// Benchmark emitting a BEGIN sign.
  ///

  @Benchmark
  public void emit_begin () {

    operation.begin ();

  }

  ///
  /// Benchmark batched BEGIN emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_begin_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      operation.begin ();
    }

  }

  ///
  /// Benchmark emitting an END sign.
  ///

  @Benchmark
  public void emit_end () {

    operation.end ();

  }

  ///
  /// Benchmark batched END emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_end_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      operation.end ();
    }

  }

  ///
  /// Benchmark generic sign emission.
  ///

  @Benchmark
  public void emit_sign () {

    operation.sign (
      BEGIN
    );

  }

  ///
  /// Benchmark batched generic sign emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_sign_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      operation.sign (
        BEGIN
      );
    }

  }

  ///
  /// Benchmark operation retrieval from conduit.
  ///

  @Benchmark
  public Operation operation_from_conduit () {

    return
      conduit.percept (
        name
      );

  }

  ///
  /// Benchmark batched operation retrieval from conduit.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Operation operation_from_conduit_batch () {

    Operation result = null;

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      result =
        conduit.percept (
          name
        );
    }

    return
      result;

  }

  @Setup ( Level.Iteration )
  public void setupIteration () {

    circuit =
      cortex.circuit ();

    conduit =
      circuit.conduit (
        Operations::composer
      );

    operation =
      conduit.percept (
        name
      );

    circuit.await ();

  }

  @Setup ( Level.Trial )
  public void setupTrial () {

    cortex =
      Substrates.cortex ();

    name =
      cortex.name (
        OPERATION_NAME
      );

  }

  @TearDown ( Level.Iteration )
  public void tearDownIteration () {

    circuit.close ();

  }

}
