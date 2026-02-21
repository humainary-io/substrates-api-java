// Copyright (c) 2025 William David Louth

package io.humainary.serventis.jmh.opt.pool;

import io.humainary.substrates.api.Substrates;
import io.humainary.substrates.ext.serventis.opt.pool.Resources;
import io.humainary.substrates.ext.serventis.opt.pool.Resources.Sign;
import org.openjdk.jmh.annotations.*;

import static io.humainary.substrates.ext.serventis.opt.pool.Resources.Sign.GRANT;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for Resources.Resource operations.
/// <p>
/// Measures performance of resource creation and sign emissions for resource
/// lifecycle operations: ATTEMPT, ACQUIRE, GRANT, DENY, TIMEOUT, and RELEASE.
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class ResourceOps implements Substrates {

  private static final String RESOURCE_NAME = "db.connections";
  private static final int    BATCH_SIZE    = 1000;

  private Cortex                               cortex;
  private Circuit                              circuit;
  private Conduit < Resources.Resource, Sign > conduit;
  private Resources.Resource                   resource;
  private Name                                 name;

  ///
  /// Benchmark emitting an ACQUIRE sign.
  ///

  @Benchmark
  public void emit_acquire () {

    resource.acquire ();

  }

  ///
  /// Benchmark batched ACQUIRE emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_acquire_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      resource.acquire ();
    }

  }

  ///
  /// Benchmark emitting an ATTEMPT sign.
  ///

  @Benchmark
  public void emit_attempt () {

    resource.attempt ();

  }

  ///
  /// Benchmark batched ATTEMPT emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_attempt_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      resource.attempt ();
    }

  }

  ///
  /// Benchmark emitting a DENY sign.
  ///

  @Benchmark
  public void emit_deny () {

    resource.deny ();

  }

  ///
  /// Benchmark batched DENY emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_deny_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      resource.deny ();
    }

  }

  ///
  /// Benchmark emitting a GRANT sign.
  ///

  @Benchmark
  public void emit_grant () {

    resource.grant ();

  }

  ///
  /// Benchmark batched GRANT emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_grant_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      resource.grant ();
    }

  }

  ///
  /// Benchmark emitting a RELEASE sign.
  ///

  @Benchmark
  public void emit_release () {

    resource.release ();

  }

  ///
  /// Benchmark batched RELEASE emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_release_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      resource.release ();
    }

  }

  ///
  /// Benchmark generic sign emission.
  ///

  @Benchmark
  public void emit_sign () {

    resource.sign (
      GRANT
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
      resource.sign (
        GRANT
      );
    }

  }

  ///
  /// Benchmark emitting a TIMEOUT sign.
  ///

  @Benchmark
  public void emit_timeout () {

    resource.timeout ();

  }

  ///
  /// Benchmark batched TIMEOUT emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_timeout_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      resource.timeout ();
    }

  }

  ///
  /// Benchmark resource retrieval from conduit.
  ///

  @Benchmark
  public Resources.Resource resource_from_conduit () {

    return
      conduit.percept (
        name
      );

  }

  ///
  /// Benchmark batched resource retrieval from conduit.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Resources.Resource resource_from_conduit_batch () {

    Resources.Resource result = null;

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
        Resources::composer
      );

    resource =
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
        RESOURCE_NAME
      );

  }

  @TearDown ( Level.Iteration )
  public void tearDownIteration () {

    circuit.close ();

  }

}
