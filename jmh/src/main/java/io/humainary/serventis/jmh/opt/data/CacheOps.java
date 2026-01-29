// Copyright (c) 2025 William David Louth

package io.humainary.serventis.jmh.opt.data;

import io.humainary.substrates.api.Substrates;
import io.humainary.substrates.ext.serventis.opt.data.Caches;
import io.humainary.substrates.ext.serventis.opt.data.Caches.Cache;
import io.humainary.substrates.ext.serventis.opt.data.Caches.Sign;
import org.openjdk.jmh.annotations.*;

import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for Caches.Cache operations.
///
/// Measures performance of cache creation and sign emissions for cache
/// lifecycle operations: LOOKUP, HIT, MISS, STORE, EVICT, EXPIRE, and REMOVE.
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class CacheOps implements Substrates {

  private static final String CACHE_NAME = "user.sessions";
  private static final int    BATCH_SIZE = 1000;

  private Cortex                  cortex;
  private Circuit                 circuit;
  private Conduit < Cache, Sign > conduit;
  private Cache                   cache;
  private Name                    name;

  ///
  /// Benchmark cache retrieval from conduit.
  ///

  @Benchmark
  public Cache cache_from_conduit () {

    return
      conduit.percept (
        name
      );

  }

  ///
  /// Benchmark batched cache retrieval from conduit.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Cache cache_from_conduit_batch () {

    Cache result = null;

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

  ///
  /// Benchmark emitting an EVICT sign.
  ///

  @Benchmark
  public void emit_evict () {

    cache.evict ();

  }

  ///
  /// Benchmark batched EVICT emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_evict_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      cache.evict ();
    }

  }

  ///
  /// Benchmark emitting an EXPIRE sign.
  ///

  @Benchmark
  public void emit_expire () {

    cache.expire ();

  }

  ///
  /// Benchmark batched EXPIRE emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_expire_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      cache.expire ();
    }

  }

  ///
  /// Benchmark emitting a HIT sign.
  ///

  @Benchmark
  public void emit_hit () {

    cache.hit ();

  }

  ///
  /// Benchmark batched HIT emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_hit_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      cache.hit ();
    }

  }

  ///
  /// Benchmark emitting a LOOKUP sign.
  ///

  @Benchmark
  public void emit_lookup () {

    cache.lookup ();

  }

  ///
  /// Benchmark batched LOOKUP emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_lookup_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      cache.lookup ();
    }

  }

  ///
  /// Benchmark emitting a MISS sign.
  ///

  @Benchmark
  public void emit_miss () {

    cache.miss ();

  }

  ///
  /// Benchmark batched MISS emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_miss_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      cache.miss ();
    }

  }

  ///
  /// Benchmark emitting a REMOVE sign.
  ///

  @Benchmark
  public void emit_remove () {

    cache.remove ();

  }

  ///
  /// Benchmark batched REMOVE emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_remove_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      cache.remove ();
    }

  }

  ///
  /// Benchmark generic sign emission.
  ///

  @Benchmark
  public void emit_sign () {

    cache.sign (
      Sign.HIT
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
      cache.sign (
        Sign.HIT
      );
    }

  }

  ///
  /// Benchmark emitting a STORE sign.
  ///

  @Benchmark
  public void emit_store () {

    cache.store ();

  }

  ///
  /// Benchmark batched STORE emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_store_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      cache.store ();
    }

  }

  @Setup ( Level.Iteration )
  public void setupIteration () {

    circuit =
      cortex.circuit ();

    conduit =
      circuit.conduit (
        Caches::composer
      );

    cache =
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
        CACHE_NAME
      );

  }

  @TearDown ( Level.Iteration )
  public void tearDownIteration () {

    circuit.close ();

  }

}
