// Copyright (c) 2025 William David Louth

package io.humainary.serventis.jmh.opt.pool;

import io.humainary.substrates.api.Substrates;
import io.humainary.substrates.ext.serventis.opt.pool.Pools;
import io.humainary.substrates.ext.serventis.opt.pool.Pools.Pool;
import io.humainary.substrates.ext.serventis.opt.pool.Pools.Sign;
import org.openjdk.jmh.annotations.*;

import static io.humainary.substrates.ext.serventis.opt.pool.Pools.Sign.EXPAND;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for Pools.Pool operations.
///
/// Measures performance of pool creation and sign emissions for capacity
/// management (EXPAND, CONTRACT) and utilization tracking (BORROW, RECLAIM).
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class PoolOps implements Substrates {

  private static final String POOL_NAME  = "db.connections";
  private static final int    BATCH_SIZE = 1000;

  private Cortex                 cortex;
  private Circuit                circuit;
  private Conduit < Pool, Sign > conduit;
  private Pool                   pool;
  private Name                   name;

  ///
  /// Benchmark emitting a BORROW sign.
  ///

  @Benchmark
  public void emit_borrow () {

    pool.borrow ();

  }

  ///
  /// Benchmark batched BORROW emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_borrow_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      pool.borrow ();
    }

  }

  ///
  /// Benchmark emitting a CONTRACT sign.
  ///

  @Benchmark
  public void emit_contract () {

    pool.contract ();

  }

  ///
  /// Benchmark batched CONTRACT emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_contract_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      pool.contract ();
    }

  }

  ///
  /// Benchmark emitting a EXPAND sign.
  ///

  @Benchmark
  public void emit_expand () {

    pool.expand ();

  }

  ///
  /// Benchmark batched EXPAND emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_expand_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      pool.expand ();
    }

  }

  ///
  /// Benchmark emitting a RECLAIM sign.
  ///

  @Benchmark
  public void emit_reclaim () {

    pool.reclaim ();

  }

  ///
  /// Benchmark batched RECLAIM emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_reclaim_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      pool.reclaim ();
    }

  }

  ///
  /// Benchmark generic sign emission.
  ///

  @Benchmark
  public void emit_sign () {

    pool.sign (
      EXPAND
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
      pool.sign (
        EXPAND
      );
    }

  }

  ///
  /// Benchmark pool retrieval from conduit.
  ///

  @Benchmark
  public Pool pool_from_conduit () {

    return
      conduit.percept (
        name
      );

  }

  ///
  /// Benchmark batched pool retrieval from conduit.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Pool pool_from_conduit_batch () {

    Pool result = null;

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
        Pools::composer
      );

    pool =
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
        POOL_NAME
      );

  }

  @TearDown ( Level.Iteration )
  public void tearDownIteration () {

    circuit.close ();

  }

}
