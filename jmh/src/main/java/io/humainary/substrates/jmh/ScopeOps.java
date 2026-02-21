// Copyright (c) 2025 William David Louth

package io.humainary.substrates.jmh;

import io.humainary.substrates.api.Substrates;
import org.openjdk.jmh.annotations.*;

import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Level.Trial;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for Scope operations.
///
/// Measures performance of scope lifecycle management, resource registration,
/// hierarchical scoping, and cleanup operations. Scopes provide hierarchical
/// resource grouping with automatic lifecycle management and guaranteed cleanup.
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class ScopeOps
  implements Substrates {

  private static final String NAME_STR   = "test";
  private static final int    COUNT      = 5;
  private static final int    BATCH_SIZE = 1000;

  private Cortex cortex;
  private Name   name;

  ///
  /// Benchmark creating a child scope.
  ///

  @Benchmark
  public void scope_child_anonymous () {

    final var
      parent =
      cortex.scope ();

    final var
      child =
      parent.scope ();

    child.close ();
    parent.close ();

  }

  ///
  /// Benchmark batched child scope creation.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void scope_child_anonymous_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) {
      final var parent = cortex.scope ();
      final var child = parent.scope ();
      child.close ();
      parent.close ();
    }
  }

  ///
  /// Benchmark creating a named child scope.
  ///

  @Benchmark
  public void scope_child_named () {

    final var
      parent =
      cortex.scope ();

    final var
      child =
      parent.scope (
        name
      );

    child.close ();
    parent.close ();

  }

  ///
  /// Benchmark batched named child scope creation.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void scope_child_named_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) {
      final var parent = cortex.scope ();
      final var child = parent.scope ( name );
      child.close ();
      parent.close ();
    }
  }

  ///
  /// Benchmark idempotent close.
  ///

  @Benchmark
  public void scope_close_idempotent () {

    final var
      scope =
      cortex.scope ();

    scope.close ();
    scope.close ();
    scope.close ();

  }

  ///
  /// Benchmark batched idempotent close.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void scope_close_idempotent_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) {
      final var scope = cortex.scope ();
      scope.close ();
      scope.close ();
      scope.close ();
    }
  }

  ///
  /// Benchmark closure pattern (block-scoped resource).
  ///

  @Benchmark
  public void scope_closure () {

    final var
      scope =
      cortex.scope ();

    final var
      circuit =
      cortex.circuit ();

    scope
      .closure (
        circuit
      )
      .consume (
        _ -> {
        }
      );

    scope.close ();

  }

  ///
  /// Benchmark batched closure pattern.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void scope_closure_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) {
      final var scope = cortex.scope ();
      final var circuit = cortex.circuit ();
      scope.closure ( circuit ).consume ( _ -> {
      } );
      scope.close ();
    }
  }

  ///
  /// Benchmark complex hierarchy with resources.
  ///

  @Benchmark
  public void scope_complex () {

    final var
      root =
      cortex.scope ();

    root.register (
      cortex.circuit ()
    );

    final var
      child1 =
      root.scope (
        name
      );

    child1.register (
      cortex.circuit ()
    );

    final var
      child2 =
      root.scope ();

    child2.register (
      cortex.circuit ()
    );

    // Root close should close all descendants and resources
    root.close ();

  }

  ///
  /// Benchmark creating and closing an empty scope.
  ///

  @Benchmark
  public void scope_create_and_close () {

    final var
      scope =
      cortex.scope ();

    scope.close ();

  }

  ///
  /// Benchmark batched scope creation and closing.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void scope_create_and_close_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) {
      final var scope = cortex.scope ();
      scope.close ();
    }
  }

  ///
  /// Benchmark creating and closing a named scope.
  ///

  @Benchmark
  public void scope_create_named () {

    final var
      scope =
      cortex.scope (
        name
      );

    scope.close ();

  }

  //
  // BATCHED BENCHMARKS
  // These benchmarks measure amortized per-operation cost by executing
  // operations in tight loops, reducing measurement noise for fast operations.
  //

  ///
  /// Benchmark batched named scope creation and closing.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void scope_create_named_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) {
      final var scope = cortex.scope ( name );
      scope.close ();
    }
  }

  ///
  /// Benchmark hierarchical scope tree (3 levels).
  ///

  @Benchmark
  public void scope_hierarchy () {

    final var
      root =
      cortex.scope ();

    final var
      level1 =
      root.scope ();

    final var
      level2 =
      level1.scope ();

    level2.close ();
    level1.close ();
    root.close ();

  }

  ///
  /// Benchmark batched hierarchical scopes (3 levels).
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void scope_hierarchy_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) {
      final var root = cortex.scope ();
      final var level1 = root.scope ();
      final var level2 = level1.scope ();
      level2.close ();
      level1.close ();
      root.close ();
    }
  }

  ///
  /// Benchmark parent scope closing children.
  ///

  @Benchmark
  public void scope_parent_closes_children () {

    final var
      parent =
      cortex.scope ();

    parent.scope ();
    parent.scope ();
    parent.scope ();

    // Parent close should close all children
    parent.close ();

  }

  ///
  /// Benchmark batched parent closing children.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void scope_parent_closes_children_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) {
      final var parent = cortex.scope ();
      parent.scope ();
      parent.scope ();
      parent.scope ();
      parent.close (); // Should close all children
    }
  }

  ///
  /// Benchmark registering multiple resources.
  ///

  @Benchmark
  public void scope_register_multiple () {

    final var
      scope =
      cortex.scope ();

    for (
      int i = 0;
      i < COUNT;
      i++
    ) {
      scope.register (
        cortex.circuit ()
      );
    }

    scope.close ();

  }

  ///
  /// Benchmark batched scope with multiple resources.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void scope_register_multiple_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) {
      final var scope = cortex.scope ();
      for ( int j = 0; j < COUNT; j++ ) {
        scope.register ( cortex.circuit () );
      }
      scope.close ();
    }
  }

  ///
  /// Benchmark registering a single resource.
  ///

  @Benchmark
  public void scope_register_single () {

    final var
      scope =
      cortex.scope ();

    final var
      circuit =
      cortex.circuit ();

    scope.register (
      circuit
    );

    scope.close ();

  }

  ///
  /// Benchmark batched scope with single resource.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void scope_register_single_batch () {
    for ( var i = 0; i < BATCH_SIZE; i++ ) {
      final var scope = cortex.scope ();
      final var circuit = cortex.circuit ();
      scope.register ( circuit );
      scope.close ();
    }
  }

  ///
  /// Benchmark scope with registered resources closing.
  ///

  @Benchmark
  public void scope_with_resources () {

    final var
      scope =
      cortex.scope ();

    scope.register (
      cortex.circuit ()
    );

    scope.register (
      cortex.circuit ()
    );

    scope.close ();

  }

  @Setup ( Trial )
  public void setupTrial () {

    cortex =
      Substrates.cortex ();

    name =
      cortex.name (
        NAME_STR
      );

  }

}
