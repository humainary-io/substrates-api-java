// Copyright (c) 2025 William David Louth

package io.humainary.substrates.jmh;

import io.humainary.substrates.api.Substrates;
import io.humainary.substrates.api.Substrates.*;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import static io.humainary.substrates.api.Substrates.Composer.pipe;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Level.Invocation;
import static org.openjdk.jmh.annotations.Level.Trial;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for Subscriber operations.
///
/// Measures performance of subscriber lifecycle operations, particularly close()
/// under different resource usage patterns. The close() operation submits a job
/// to the circuit's valve that iterates and closes all tracked subscriptions.
///
/// ## Benchmark Categories
///
/// 1. **Close with varying subscription counts**: 0, 1, 5, 10 subscriptions
/// 2. **Close with multiple conduits**: Subscriber subscribed to many conduits
/// 3. **Idempotent close**: Repeated close calls on already-closed subscriber
/// 4. **Close under emission load**: Close while emissions are in flight
///
/// ## Performance Considerations
///
/// The close() operation:
/// - Submits a job to the valve (async dispatch)
/// - Iterates the internal subscription list
/// - Calls close() on each subscription (triggers rebuild)
///
/// Cost scales with number of subscriptions and conduit complexity.
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class SubscriberOps {

  private static final int BATCH_SIZE = 1000;

  private Cortex cortex;

  // ===========================
  // CLOSE WITH NO SUBSCRIPTIONS
  // ===========================

  ///
  /// Benchmark closing a subscriber with zero subscriptions.
  /// This measures the baseline cost of the close() dispatch.
  ///

  @Benchmark
  public static void close_no_subscriptions_await (
    final NoSubscriptionsState state
  ) {

    state.subscriber.close ();

    state.circuit.await ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public static void close_no_subscriptions_batch_await (
    final NoSubscriptionsBatchState state
  ) {

    for (
      int i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      state.subscribers[i].close ();
    }

    state.circuit.await ();

  }

  // ===========================
  // CLOSE WITH ONE SUBSCRIPTION
  // ===========================

  ///
  /// Benchmark closing a subscriber with one subscription.
  /// This measures the cost of close() with minimal subscription overhead.
  ///

  @Benchmark
  public static void close_one_subscription_await (
    final OneSubscriptionState state
  ) {

    state.subscriber.close ();

    state.circuit.await ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public static void close_one_subscription_batch_await (
    final OneSubscriptionBatchState state
  ) {

    for (
      int i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      state.subscribers[i].close ();
    }

    state.circuit.await ();

  }

  // ===========================
  // CLOSE WITH FIVE SUBSCRIPTIONS
  // ===========================

  ///
  /// Benchmark closing a subscriber with five subscriptions.
  /// This measures scaling behavior with moderate subscription count.
  ///

  @Benchmark
  public static void close_five_subscriptions_await (
    final FiveSubscriptionsState state
  ) {

    state.subscriber.close ();

    state.circuit.await ();

  }

  // ===========================
  // CLOSE WITH TEN SUBSCRIPTIONS
  // ===========================

  ///
  /// Benchmark closing a subscriber with ten subscriptions.
  /// This measures scaling behavior with higher subscription count.
  ///

  @Benchmark
  public static void close_ten_subscriptions_await (
    final TenSubscriptionsState state
  ) {

    state.subscriber.close ();

    state.circuit.await ();

  }

  // ===========================
  // CLOSE ACROSS MULTIPLE CONDUITS
  // ===========================

  ///
  /// Benchmark closing a subscriber subscribed to five different conduits.
  /// This measures the cost when subscriptions span multiple conduits.
  ///

  @Benchmark
  public static void close_five_conduits_await (
    final FiveConduitsState state
  ) {

    state.subscriber.close ();

    state.circuit.await ();

  }

  ///
  /// Benchmark closing a subscriber subscribed to ten different conduits.
  ///

  @Benchmark
  public static void close_ten_conduits_await (
    final TenConduitsState state
  ) {

    state.subscriber.close ();

    state.circuit.await ();

  }

  // ===========================
  // IDEMPOTENT CLOSE
  // ===========================

  ///
  /// Benchmark repeated close calls on an already-closed subscriber.
  /// This measures the cost of the idempotent close path.
  ///

  @Benchmark
  public static void close_idempotent_await (
    final IdempotentCloseState state
  ) {

    // Subscriber was already closed in setup
    state.subscriber.close ();

    state.circuit.await ();

  }

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public static void close_idempotent_batch_await (
    final IdempotentCloseState state
  ) {

    for (
      int i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      state.subscriber.close ();
    }

    state.circuit.await ();

  }

  // ===========================
  // CLOSE UNDER EMISSION LOAD
  // ===========================

  ///
  /// Benchmark close while emissions are queued.
  /// This measures interaction between close and active emission processing.
  ///

  @Benchmark
  public static void close_with_pending_emissions_await (
    final EmissionLoadState state
  ) {

    // Queue some emissions
    for ( int i = 0; i < 10; i++ ) {
      state.pipe.emit ( i );
    }

    // Close while emissions may be in flight
    state.subscriber.close ();

    state.circuit.await ();

  }

  // ===========================
  // TRIAL SETUP
  // ===========================

  @Setup ( Trial )
  public void setupTrial () {

    cortex =
      Substrates.cortex ();

  }

  // ===========================
  // STATE CLASSES
  // ===========================

  @State ( Scope.Thread )
  public static class NoSubscriptionsState {

    Circuit             circuit;
    Subscriber < Long > subscriber;

    @Setup ( Invocation )
    public void setup (
      final SubscriberOps ops
    ) {

      circuit =
        ops.cortex.circuit ();

      subscriber =
        circuit.subscriber (
          ops.cortex.name ( "test" ),
          ( _, _ ) -> {
          }
        );

    }

    @TearDown ( Invocation )
    public void teardown () {

      circuit.close ();

    }

  }

  @State ( Scope.Thread )
  public static class NoSubscriptionsBatchState {

    Circuit               circuit;
    Subscriber < Long >[] subscribers;

    @SuppressWarnings ( "unchecked" )
    @Setup ( Invocation )
    public void setup (
      final SubscriberOps ops
    ) {

      circuit =
        ops.cortex.circuit ();

      subscribers =
        new Subscriber[BATCH_SIZE];

      for ( int i = 0; i < BATCH_SIZE; i++ ) {
        subscribers[i] =
          circuit.subscriber (
            ops.cortex.name ( "test" + i ),
            ( _, _ ) -> {
            }
          );
      }

    }

    @TearDown ( Invocation )
    public void teardown () {

      circuit.close ();

    }

  }

  @State ( Scope.Thread )
  public static class OneSubscriptionState {

    Circuit                         circuit;
    Conduit < Pipe < Long >, Long > conduit;
    Subscriber < Long >             subscriber;

    @Setup ( Invocation )
    public void setup (
      final SubscriberOps ops
    ) {

      circuit =
        ops.cortex.circuit ();

      conduit =
        circuit.conduit (
          pipe ( Long.class )
        );

      subscriber =
        circuit.subscriber (
          ops.cortex.name ( "test" ),
          ( _, registrar ) ->
            registrar.register ( _ -> {
            } )
        );

      conduit.subscribe ( subscriber );

    }

    @TearDown ( Invocation )
    public void teardown () {

      circuit.close ();

    }

  }

  @State ( Scope.Thread )
  public static class OneSubscriptionBatchState {

    Circuit                         circuit;
    Conduit < Pipe < Long >, Long > conduit;
    Subscriber < Long >[]           subscribers;

    @SuppressWarnings ( "unchecked" )
    @Setup ( Invocation )
    public void setup (
      final SubscriberOps ops
    ) {

      circuit =
        ops.cortex.circuit ();

      conduit =
        circuit.conduit (
          pipe ( Long.class )
        );

      subscribers =
        new Subscriber[BATCH_SIZE];

      for ( int i = 0; i < BATCH_SIZE; i++ ) {
        subscribers[i] =
          circuit.subscriber (
            ops.cortex.name ( "test" + i ),
            ( _, registrar ) ->
              registrar.register ( _ -> {
              } )
          );
        conduit.subscribe ( subscribers[i] );
      }

    }

    @TearDown ( Invocation )
    public void teardown () {

      circuit.close ();

    }

  }

  @State ( Scope.Thread )
  public static class FiveSubscriptionsState {

    Circuit                           circuit;
    Conduit < Pipe < Long >, Long >[] conduits;
    Subscriber < Long >               subscriber;

    @SuppressWarnings ( "unchecked" )
    @Setup ( Invocation )
    public void setup (
      final SubscriberOps ops
    ) {

      circuit =
        ops.cortex.circuit ();

      conduits =
        new Conduit[5];

      subscriber =
        circuit.subscriber (
          ops.cortex.name ( "test" ),
          ( _, registrar ) ->
            registrar.register ( _ -> {
            } )
        );

      for ( int i = 0; i < 5; i++ ) {
        conduits[i] =
          circuit.conduit (
            pipe ( Long.class )
          );
        conduits[i].subscribe ( subscriber );
      }

    }

    @TearDown ( Invocation )
    public void teardown () {

      circuit.close ();

    }

  }

  @State ( Scope.Thread )
  public static class TenSubscriptionsState {

    Circuit                           circuit;
    Conduit < Pipe < Long >, Long >[] conduits;
    Subscriber < Long >               subscriber;

    @SuppressWarnings ( "unchecked" )
    @Setup ( Invocation )
    public void setup (
      final SubscriberOps ops
    ) {

      circuit =
        ops.cortex.circuit ();

      conduits =
        new Conduit[10];

      subscriber =
        circuit.subscriber (
          ops.cortex.name ( "test" ),
          ( _, registrar ) ->
            registrar.register ( _ -> {
            } )
        );

      for ( int i = 0; i < 10; i++ ) {
        conduits[i] =
          circuit.conduit (
            pipe ( Long.class )
          );
        conduits[i].subscribe ( subscriber );
      }

    }

    @TearDown ( Invocation )
    public void teardown () {

      circuit.close ();

    }

  }

  @State ( Scope.Thread )
  public static class FiveConduitsState {

    Circuit                           circuit;
    Conduit < Pipe < Long >, Long >[] conduits;
    Subscriber < Long >               subscriber;

    @SuppressWarnings ( "unchecked" )
    @Setup ( Invocation )
    public void setup (
      final SubscriberOps ops
    ) {

      circuit =
        ops.cortex.circuit ();

      conduits =
        new Conduit[5];

      subscriber =
        circuit.subscriber (
          ops.cortex.name ( "test" ),
          ( _, registrar ) ->
            registrar.register ( _ -> {
            } )
        );

      for ( int i = 0; i < 5; i++ ) {
        conduits[i] =
          circuit.conduit (
            pipe ( Long.class )
          );
        conduits[i].subscribe ( subscriber );
      }

    }

    @TearDown ( Invocation )
    public void teardown () {

      circuit.close ();

    }

  }

  @State ( Scope.Thread )
  public static class TenConduitsState {

    Circuit                           circuit;
    Conduit < Pipe < Long >, Long >[] conduits;
    Subscriber < Long >               subscriber;

    @SuppressWarnings ( "unchecked" )
    @Setup ( Invocation )
    public void setup (
      final SubscriberOps ops
    ) {

      circuit =
        ops.cortex.circuit ();

      conduits =
        new Conduit[10];

      subscriber =
        circuit.subscriber (
          ops.cortex.name ( "test" ),
          ( _, registrar ) ->
            registrar.register ( _ -> {
            } )
        );

      for ( int i = 0; i < 10; i++ ) {
        conduits[i] =
          circuit.conduit (
            pipe ( Long.class )
          );
        conduits[i].subscribe ( subscriber );
      }

    }

    @TearDown ( Invocation )
    public void teardown () {

      circuit.close ();

    }

  }

  @State ( Scope.Thread )
  public static class IdempotentCloseState {

    Circuit                         circuit;
    Conduit < Pipe < Long >, Long > conduit;
    Subscriber < Long >             subscriber;

    @Setup ( Invocation )
    public void setup (
      final SubscriberOps ops
    ) {

      circuit =
        ops.cortex.circuit ();

      conduit =
        circuit.conduit (
          pipe ( Long.class )
        );

      subscriber =
        circuit.subscriber (
          ops.cortex.name ( "test" ),
          ( _, registrar ) ->
            registrar.register ( _ -> {
            } )
        );

      conduit.subscribe ( subscriber );

      // Close once in setup - benchmark measures subsequent closes
      subscriber.close ();

      circuit.await ();

    }

    @TearDown ( Invocation )
    public void teardown () {

      circuit.close ();

    }

  }

  @State ( Scope.Thread )
  public static class EmissionLoadState {

    Circuit                               circuit;
    Conduit < Pipe < Integer >, Integer > conduit;
    Pipe < Integer >                      pipe;
    Subscriber < Integer >                subscriber;

    @Setup ( Invocation )
    public void setup (
      final SubscriberOps ops
    ) {

      circuit =
        ops.cortex.circuit ();

      conduit =
        circuit.conduit (
          pipe ( Integer.class )
        );

      subscriber =
        circuit.subscriber (
          ops.cortex.name ( "test" ),
          ( _, registrar ) ->
            registrar.register ( _ -> {
            } )
        );

      conduit.subscribe ( subscriber );

      pipe =
        conduit.percept (
          ops.cortex.name ( "channel" )
        );

    }

    @TearDown ( Invocation )
    public void teardown () {

      circuit.close ();

    }

  }

}
