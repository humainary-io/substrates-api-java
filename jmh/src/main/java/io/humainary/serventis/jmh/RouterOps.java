// Copyright (c) 2025 William David Louth

package io.humainary.serventis.jmh;

import io.humainary.substrates.api.Substrates;
import io.humainary.substrates.ext.serventis.ext.Routers;
import io.humainary.substrates.ext.serventis.ext.Routers.Router;
import io.humainary.substrates.ext.serventis.ext.Routers.Sign;
import org.openjdk.jmh.annotations.*;

import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for Routers.Router operations.
///
/// Measures performance of router creation and sign emissions for packet routing
/// operations, including SEND, RECEIVE, FORWARD, ROUTE, DROP, FRAGMENT,
/// REASSEMBLE, CORRUPT, and REORDER.
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class RouterOps implements Substrates {

  private static final String ROUTER_NAME = "edge01.eth0";
  private static final int    BATCH_SIZE  = 1000;

  private Cortex                   cortex;
  private Circuit                  circuit;
  private Conduit < Router, Sign > conduit;
  private Router                   router;
  private Name                     name;

  ///
  /// Benchmark emitting a CORRUPT sign.
  ///

  @Benchmark
  public void emit_corrupt () {

    router.corrupt ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_corrupt_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) router.corrupt ();
  }

  ///
  /// Benchmark emitting a DROP sign.
  ///

  @Benchmark
  public void emit_drop () {

    router.drop ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_drop_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) router.drop ();
  }

  ///
  /// Benchmark emitting a FORWARD sign.
  ///

  @Benchmark
  public void emit_forward () {

    router.forward ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_forward_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) router.forward ();
  }

  ///
  /// Benchmark emitting a FRAGMENT sign.
  ///

  @Benchmark
  public void emit_fragment () {

    router.fragment ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_fragment_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) router.fragment ();
  }

  ///
  /// Benchmark emitting a REASSEMBLE sign.
  ///

  @Benchmark
  public void emit_reassemble () {

    router.reassemble ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_reassemble_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) router.reassemble ();
  }

  ///
  /// Benchmark emitting a RECEIVE sign.
  ///

  @Benchmark
  public void emit_receive () {

    router.receive ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_receive_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) router.receive ();
  }

  ///
  /// Benchmark emitting a REORDER sign.
  ///

  @Benchmark
  public void emit_reorder () {

    router.reorder ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_reorder_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) router.reorder ();
  }

  ///
  /// Benchmark emitting a ROUTE sign.
  ///

  @Benchmark
  public void emit_route () {

    router.route ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_route_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) router.route ();
  }

  ///
  /// Benchmark emitting a SEND sign.
  ///

  @Benchmark
  public void emit_send () {

    router.send ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_send_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) router.send ();
  }

  ///
  /// Benchmark generic sign emission.
  ///

  @Benchmark
  public void emit_sign () {

    router.sign (
      Sign.ROUTE
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
      router.sign (
        Sign.ROUTE
      );
    }

  }

  ///
  /// Benchmark router retrieval from conduit.
  ///

  @Benchmark
  public Router router_from_conduit () {

    return
      conduit.percept (
        name
      );

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Router router_from_conduit_batch () {
    Router result = null;
    for ( var i = 0; i < BATCH_SIZE; i++ ) result = conduit.percept ( name );
    return result;
  }

  @Setup ( Level.Iteration )
  public void setupIteration () {

    circuit =
      cortex.circuit ();

    conduit =
      circuit.conduit (
        Routers::composer
      );

    router =
      conduit.percept (
        name
      );

  }

  @Setup ( Level.Trial )
  public void setupTrial () {

    cortex =
      Substrates.cortex ();

    name =
      cortex.name (
        ROUTER_NAME
      );

  }

  @TearDown ( Level.Iteration )
  public void tearDownIteration () {

    circuit.close ();

  }

}
