// Copyright (c) 2025 William David Louth

package io.humainary.substrates.tck;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/// Comprehensive tests for the Scope interface.
///
/// This test class covers:
/// - Scope resource registration and tracking
/// - Closure creation and reuse semantics
/// - Resource consumption and cleanup
/// - Scope lifecycle and close behavior
///
/// Scopes manage lifecycle of resources (circuits, closures) and ensure
/// proper cleanup when the scope is closed. Resources can be explicitly
/// registered or wrapped in closures for lazy initialization.
///
/// @author William David Louth
/// @since 1.0
final class ScopeTest
  extends TestSupport {

  private Cortex cortex;

  @BeforeEach
  void setup () {

    cortex = cortex ();

  }

  /// Validates closure lifecycle: reuse semantics and one-time consumption behavior.
  ///
  /// This test demonstrates the complete lifecycle of closures within a scope:
  ///
  /// Phase 1 - Closure Creation and Reuse:
  /// - Creates closure for a circuit resource
  /// - Verifies repeated calls to scope.closure(circuit) return SAME instance
  /// - This pooling/caching enables efficient resource wrapping
  ///
  /// Phase 2 - Consumption:
  /// - Calls closure.consume() to access the wrapped resource
  /// - Consumer executes immediately while scope is open
  /// - Resource is passed to consumer for one-time use
  ///
  /// Phase 3 - Post-Consumption:
  /// - After consumption, scope.closure(circuit) returns NEW instance
  /// - First closure is "spent" - cannot be reused
  /// - New closure can be consumed again
  ///
  /// This pattern enables safe lazy resource initialization:
  /// - Resources wrapped in closures aren't created until consumed
  /// - Consumption is one-time, preventing accidental reuse
  /// - Scope tracks all closures for cleanup
  /// - After consumption, closure is released and new one can be created
  ///
  /// Critical for resource management:
  /// - Prevents resource leaks (scope closes all)
  /// - Lazy initialization (defer creation until needed)
  /// - One-time consumption prevents double-use bugs
  /// - Efficient reuse before consumption (same closure returned)
  ///
  /// Expected behavior:
  /// 1. scope.closure(x) == scope.closure(x) before consumption
  /// 2. Consumer executes when scope is open
  /// 3. scope.closure(x) != previous after consumption (new closure)
  @Test
  void testClosureReuseAndConsumption () {

    final var scope = cortex.scope ();
    final var circuit = cortex.circuit ();

    final var registered = cortex.circuit ();

    try {

      assertSame ( registered, scope.register ( registered ) );

      final var first =
        scope.closure ( circuit );

      assertSame ( first, scope.closure ( circuit ) );

      final var invoked = new AtomicBoolean ( false );

      first.consume ( resource -> {
        invoked.set ( true );
        assertSame ( circuit, resource );
      } );

      assertTrue ( invoked.get (), "closure should invoke consumer while scope open" );

      final var second =
        scope.closure ( circuit );

      assertNotSame ( first, second );

      second.consume ( _ -> {
      } );

    } finally {

      scope.close ();
      circuit.close ();
      registered.close ();

    }

  }

  /// Validates that closing a scope prevents all further operations and consumption.
  ///
  /// This test verifies the safety guarantees of scope closure, ensuring that
  /// once a scope is closed, it becomes inert and rejects all operations:
  ///
  /// Setup:
  /// - Creates scope with a closure wrapping a circuit
  /// - Closes the scope while closure is unconsumed
  ///
  /// Closure Consumption After Close:
  /// - Attempts to consume the closure after scope is closed
  /// - Consumer MUST NOT execute (invoked flag remains false)
  /// - This prevents use-after-close bugs where closed resources are accessed
  /// - Closure silently ignores consumption rather than throwing (fail-safe)
  ///
  /// Registration After Close:
  /// - Attempts to register new resource to closed scope
  /// - MUST throw IllegalStateException (fail-fast for invalid operation)
  /// - Prevents accumulating resources in dead scope
  ///
  /// Child Scope Creation After Close:
  /// - Attempts to create child scope from closed parent
  /// - MUST throw IllegalStateException
  /// - Prevents building hierarchy from dead root
  ///
  /// Why this matters:
  /// - Prevents resource leaks (no new registrations after close)
  /// - Prevents use-after-free bugs (closures don't execute after scope close)
  /// - Clear error messages for programming mistakes (IllegalStateException)
  /// - Fail-safe for closures (silent ignore) vs fail-fast for operations (throw)
  ///
  /// Design rationale:
  /// - Closures use silent failure (common in cleanup paths)
  /// - Registration/creation use exceptions (programming errors)
  /// - Once closed, scope is permanently disabled (no reopen)
  ///
  /// Expected: Closure doesn't execute, operations throw IllegalStateException
  @Test
  void testScopeClosePreventsFurtherOperations () {

    final var scope = cortex.scope ();
    final var circuit = cortex.circuit ();

    try {

      final var closure =
        scope.closure ( circuit );

      scope.close ();

      final var invoked = new AtomicBoolean ( false );

      closure.consume ( _ -> invoked.set ( true ) );

      assertFalse ( invoked.get (), "closure should not run after scope is closed" );

      final var extra = cortex.circuit ();

      try {
        assertThrows (
          IllegalStateException.class,
          () -> scope.register ( extra )
        );
      } finally {
        extra.close ();
      }

      assertThrows (
        IllegalStateException.class,
        scope::scope
      );

    } finally {

      scope.close ();
      circuit.close ();

    }

  }

  /// Validates scope hierarchical structure and parent-child relationships.
  ///
  /// This test verifies the hierarchical nature of scopes, including parent-child
  /// relationships, enclosure navigation, and cascading closure behavior:
  ///
  /// Hierarchy Setup:
  /// ```
  ///     root (try-with-resources)
  ///      ├── named ("scope.test.named")
  ///      └── anonymous (auto-generated name)
  /// ```
  ///
  /// Parent-Child Relationships:
  /// - Child scopes created via parent.scope() or parent.scope(name)
  /// - Children maintain reference to parent (enclosure)
  /// - Named children have explicit hierarchical names
  /// - Anonymous children get auto-generated names
  ///
  /// Enclosure Navigation:
  /// - child.enclosure() returns Optional<Scope> of parent
  /// - child.enclosure(Consumer) invokes consumer with parent
  /// - Enables traversal up the scope tree
  /// - Root scope has no enclosure (empty Optional)
  ///
  /// Subject and Path:
  /// - Each scope has a Subject with hierarchical Name
  /// - scope.path() returns full hierarchical path
  /// - scope.toString() returns path string representation
  /// - Enables tracing scope relationships
  ///
  /// Cascading Closure:
  /// - Closing parent scope closes all children
  /// - Closed children reject further operations (IllegalStateException)
  /// - Try-with-resources on root ensures proper cleanup
  /// - Prevents partial cleanup bugs
  ///
  /// Critical for resource management:
  /// - Hierarchical scoping (like try-with-resources nesting)
  /// - Automatic cleanup of entire scope tree
  /// - Parent responsible for children's lifecycle
  /// - Prevents orphaned child scopes
  ///
  /// Expected: Children closed when parent closes, enclosure accessible,
  /// hierarchical naming preserved
  @Test
  void testScopeHierarchyAndEnclosure () {

    try ( final var root = cortex.scope () ) {

      final var named =
        root.scope ( cortex.name ( "scope.test.named" ) );

      final var anonymous =
        root.scope ();

      assertSame ( root, named.enclosure ().orElseThrow () );

      final var captured = new AtomicReference < Scope > ();
      named.enclosure ( captured::set );

      assertSame ( root, captured.get () );

      assertEquals ( named.path ().toString (), named.toString () );
      assertNotNull ( anonymous.subject () );

      root.close ();

      assertThrows ( IllegalStateException.class, named::scope );
      assertThrows ( IllegalStateException.class, anonymous::scope );

    }

  }

}
