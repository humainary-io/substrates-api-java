// Copyright (c) 2025 William David Louth

package io.humainary.substrates.tck;

import io.humainary.substrates.api.Substrates.Extent;
import io.humainary.substrates.api.Substrates.NotNull;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/// A comprehensive test kit for the [Extent] interface.
///
/// This test kit validates all default methods of the Extent interface using
/// a custom test implementation to ensure the default behavior works correctly
/// without relying on existing implementations like Name or Subject that might
/// override these methods.
///
/// Tests cover:
/// - Basic extent operations (extent, enclosure, extremity)
/// - Depth calculation
/// - Iteration (forward and reverse)
/// - Folding operations (fold and foldTo)
/// - Path representation with various separators and mappers
/// - Comparison operations
/// - Stream operations
/// - Within (containment) checks
/// - Edge cases and null handling
///
/// @author William David Louth
/// @since 1.0

final class ExtentTest {

  // ===========================
  // Test Implementation
  // ===========================

  @SuppressWarnings ( "EqualsWithItself" )
  @Test
  void testCompareTo () {

    final var a = TestExtent.root ( "a" );
    final var b = TestExtent.root ( "b" );
    final var c = TestExtent.root ( "c" );

    assertTrue ( a.compareTo ( b ) < 0 );
    assertTrue ( c.compareTo ( b ) > 0 );
    assertEquals ( 0, a.compareTo ( a ) );

  }

  // ===========================
  // Basic Extent Tests
  // ===========================

  @SuppressWarnings ( "DataFlowIssue" )
  @Test
  void testCompareToNullThrows () {

    final var extent = TestExtent.root ( "test" );

    assertThrows (
      NullPointerException.class,
      () -> extent.compareTo ( null )
    );

  }

  @Test
  void testCompareToSameHierarchy () {

    final var root1 = TestExtent.root ( "root" );
    final var child1 = root1.child ( "child" );

    final var root2 = TestExtent.root ( "root" );
    final var child2 = root2.child ( "child" );

    assertEquals ( 0, child1.compareTo ( child2 ) );

  }

  @Test
  void testCompareToWithHierarchy () {

    final var root = TestExtent.root ( "root" );
    final var child = root.child ( "child" );

    assertTrue ( root.compareTo ( child ) < 0 );
    assertTrue ( child.compareTo ( root ) > 0 );

  }

  /// Validates consistency across all traversal mechanisms: depth, stream, fold, iterator.
  ///
  /// This test verifies a fundamental invariant: all traversal operations on an Extent
  /// must visit the SAME number of elements. Whether using depth(), stream(), fold(),
  /// or iterator(), the count must be consistent. This consistency guarantee enables
  /// developers to choose traversal methods based on convenience without worrying
  /// about semantic differences.
  ///
  /// Hierarchy Structure:
  /// ```
  /// a (root) → b → c → d (depth = 4)
  /// ```
  ///
  /// Traversal Operation Equivalence:
  /// ```
  /// depth()           → 4 (constant-time calculation)
  /// stream().count()  → 4 (lazy traversal, terminal operation)
  /// fold(counting)    → 4 (eager traversal with accumulation)
  /// iterator + loop   → 4 (manual traversal with external counter)
  /// ```
  ///
  /// Why consistency matters:
  /// - **Interchangeability**: Switch between operations without behavior change
  /// - **Predictability**: Count is invariant regardless of traversal method
  /// - **Correctness**: All methods represent the same logical hierarchy
  /// - **Testing**: Can verify one method against another
  ///
  /// Performance characteristics (though all yield same count):
  /// - **depth()**: O(n) recursive parent chain traversal, no heap allocation
  /// - **stream()**: O(n) lazy iteration, allocates stream infrastructure
  /// - **fold()**: O(n) eager traversal, custom accumulator logic
  /// - **iterator()**: O(n) manual traversal, no intermediate allocations
  ///
  /// Real-world implications:
  /// ```java
  /// // All equivalent for counting:
  /// int depth = extent.depth();
  /// long streamCount = extent.stream().count();
  /// int foldCount = extent.fold(_ -> 1, (acc, _) -> acc + 1);
  ///
  /// // Choose based on use case:
  /// if (extent.depth() > 10) { ... }           // Simplest for threshold checks
  /// extent.stream().filter(...).count();        // Best for filtering
  /// extent.fold(0, (sum, e) -> sum + e.cost()); // Best for custom aggregation
  /// for (Extent e : extent) { ... }            // Best for early termination
  /// ```
  ///
  /// Critical behaviors verified:
  /// - depth() returns 4 (hierarchy length calculation)
  /// - stream().count() returns 4 (lazy stream traversal)
  /// - fold counting returns 4 (eager accumulation)
  /// - Iterator manual counting returns 4 (explicit traversal)
  /// - All four methods produce IDENTICAL counts
  ///
  /// Relationship to other invariants:
  /// - `depth()` represents hierarchy length from root to current
  /// - `stream().count()` materializes all elements
  /// - `fold()` processes all elements with accumulator
  /// - `iterator()` exposes all elements for manual iteration
  /// - All traverse the SAME logical elements
  ///
  /// Expected: All traversal operations count 4 elements consistently
  @Test
  void testConsistencyBetweenOperations () {

    final var a = TestExtent.root ( "a" );
    final var b = a.child ( "b" );
    final var c = b.child ( "c" );
    final var d = c.child ( "d" );

    // Depth should match stream count
    assertEquals ( d.depth (), d.stream ().count () );

    // Fold count should match depth
    final var foldCount = d.fold (
      _ -> 1,
      ( acc, _ ) -> acc + 1
    );
    assertEquals ( d.depth (), foldCount );

    // Iterator count should match depth
    final Iterator < TestExtent > iterator = d.iterator ();
    var iteratorCount = 0;
    while ( iterator.hasNext () ) {
      iterator.next ();
      iteratorCount++;
    }
    assertEquals ( d.depth (), iteratorCount );

  }

  @Test
  void testDepthRoot () {

    final var root = TestExtent.root ( "root" );
    assertEquals ( 1, root.depth () );

  }

  // ===========================
  // Depth Tests
  // ===========================

  @Test
  void testDepthWithChildren () {

    final var root = TestExtent.root ( "a" );
    final var b = root.child ( "b" );
    final var c = b.child ( "c" );
    final var d = c.child ( "d" );

    assertEquals ( 1, root.depth () );
    assertEquals ( 2, b.depth () );
    assertEquals ( 3, c.depth () );
    assertEquals ( 4, d.depth () );

  }

  @Test
  void testEnclosureConsumer () {

    final var root = TestExtent.root ( "root" );
    final var child = root.child ( "child" );
    final var result = new String[1];

    child.enclosure ( parent -> result[0] = parent.part ().toString () );

    assertEquals ( "root", result[0] );

  }

  // ===========================
  // Extremity Tests
  // ===========================

  @Test
  void testEnclosureConsumerNotCalledMultipleTimes () {

    final var root = TestExtent.root ( "root" );
    final var child = root.child ( "child" );

    final var counter = new AtomicInteger ( 0 );
    child.enclosure ( _ -> counter.incrementAndGet () );

    assertEquals ( 1, counter.get () );

  }

  @Test
  void testEnclosureConsumerOnRoot () {

    final var root = TestExtent.root ( "root" );
    final var called = new boolean[1];

    root.enclosure ( _ -> called[0] = true );

    assertFalse ( called[0] );

  }

  @Test
  void testEnclosureOnChild () {

    final var root = TestExtent.root ( "root" );
    final var child = root.child ( "child" );

    final var enclosure = child.enclosure ();
    assertTrue ( enclosure.isPresent () );
    assertSame ( root, enclosure.get () );

  }

  // ===========================
  // Iterator Tests
  // ===========================

  @Test
  void testEnclosureOnRoot () {

    final var root = TestExtent.root ( "root" );
    assertTrue ( root.enclosure ().isEmpty () );

  }

  @Test
  void testExtent () {

    final var extent = TestExtent.root ( "test" );
    assertSame ( extent, extent.extent () );

  }

  @Test
  void testExtentWithEmptyString () {

    final var root = TestExtent.root ( "" );
    assertEquals ( "", root.part () );
    assertEquals ( "", root.path ().toString () );

  }

  @Test
  void testExtentWithSpecialCharacters () {

    final var root = TestExtent.root ( "test-name_123" );
    final var child = root.child ( "child@#$" );

    assertEquals ( "test-name_123/child@#$", child.path ().toString () );

  }

  // ===========================
  // Fold Tests
  // ===========================

  @Test
  void testExtremity () {

    final var root = TestExtent.root ( "root" );
    final var child = root.child ( "child" );
    final var grandchild = child.child ( "grandchild" );

    assertSame ( root, grandchild.extremity () );
    assertSame ( root, child.extremity () );
    assertSame ( root, root.extremity () );

  }

  @Test
  void testExtremityDeepHierarchy () {

    var current = TestExtent.root ( "level0" );

    for ( var i = 1; i <= 10; i++ ) {
      current = current.child ( "level" + i );
    }

    final var root = current.extremity ();
    assertEquals ( "level0", root.part () );

  }

  @Test
  void testExtremityOnRoot () {

    final var root = TestExtent.root ( "root" );
    assertSame ( root, root.extremity () );

  }

  @Test
  void testFoldAccumulation () {

    final var root = TestExtent.root ( "10" );
    final var child = root.child ( "20" );
    final var grandchild = child.child ( "30" );

    final var sum = grandchild.fold (
      e -> Integer.parseInt ( e.part ().toString () ),
      ( acc, e ) -> acc + Integer.parseInt ( e.part ().toString () )
    );

    assertEquals ( 60, sum );

  }

  // ===========================
  // FoldTo Tests
  // ===========================

  @Test
  void testFoldCount () {

    final var root = TestExtent.root ( "a" );
    final var child = root.child ( "b" );
    final var grandchild = child.child ( "c" );

    final var count = grandchild.fold (
      _ -> 1,
      ( acc, _ ) -> acc + 1
    );

    assertEquals ( 3, count );

  }

  @Test
  void testFoldOnRoot () {

    final var root = TestExtent.root ( "test" );

    final var result = root.fold (
      e -> e.part ().toString (),
      ( acc, e ) -> acc + "/" + e.part ()
    );

    assertEquals ( "test", result );

  }

  @Test
  void testFoldRightToLeft () {

    final var a = TestExtent.root ( "a" );
    final var b = a.child ( "b" );
    final var c = b.child ( "c" );

    // fold goes from right (c) to left (a)
    final var result = c.fold (
      e -> e.part ().toString (),
      ( acc, e ) -> e.part () + "/" + acc
    );

    assertEquals ( "a/b/c", result );

  }

  @Test
  void testFoldToLeftToRight () {

    final var a = TestExtent.root ( "a" );
    final var b = a.child ( "b" );
    final var c = b.child ( "c" );

    // foldTo goes from left (a) to right (c)
    final var result = c.foldTo (
      e -> e.part ().toString (),
      ( acc, e ) -> acc + "." + e.part ()
    );

    assertEquals ( "a.b.c", result );

  }

  // ===========================
  // Path Tests
  // ===========================

  @Test
  void testFoldToOnRoot () {

    final var root = TestExtent.root ( "single" );

    final var result = root.foldTo (
      e -> e.part ().toString (),
      ( acc, e ) -> acc + "." + e.part ()
    );

    assertEquals ( "single", result );

  }

  /// Validates contrasting traversal directions: fold (right-to-left) vs foldTo (left-to-right).
  ///
  /// This is THE critical test for understanding Extent traversal semantics. It demonstrates
  /// that fold and foldTo traverse the hierarchy in OPPOSITE directions but can produce the
  /// SAME result when the accumulator function is adjusted appropriately. This bidirectional
  /// capability enables efficient operations regardless of natural data flow direction.
  ///
  /// Hierarchy Structure:
  /// ```
  /// first (root) → second → third (current extent)
  /// ```
  ///
  /// fold Traversal (right-to-left):
  /// ```
  /// Step 1: initializer(third)  → acc = "third"
  /// Step 2: accumulator(acc="third", second) → acc = "second.third"
  /// Step 3: accumulator(acc="second.third", first) → acc = "first.second.third"
  /// Result: "first.second.third"
  /// ```
  ///
  /// foldTo Traversal (left-to-right):
  /// ```
  /// Step 1: initializer(first)  → acc = "first"
  /// Step 2: accumulator(acc="first", second) → acc = "first.second"
  /// Step 3: accumulator(acc="first.second", third) → acc = "first.second.third"
  /// Result: "first.second.third"
  /// ```
  ///
  /// Key Insight - Symmetric Operations:
  /// Both operations reach the SAME result through OPPOSITE traversal:
  /// - **fold**: Right-to-left, accumulator prepends: `e.part() + "." + acc`
  /// - **foldTo**: Left-to-right, accumulator appends: `acc + "." + e.part()`
  ///
  /// This symmetry enables choosing the most efficient direction:
  /// - **fold** preferred when: natural recursion, tail-call optimization, immediate parent access
  /// - **foldTo** preferred when: left-associative operations, streaming, progressive computation
  ///
  /// Real-world examples:
  ///
  /// Path construction (naturally left-to-right):
  /// ```java
  /// String path = extent.foldTo(
  ///   e -> e.part(),
  ///   (acc, e) -> acc + "/" + e.part()
  /// );
  /// // Produces: "root/parent/child"
  /// ```
  ///
  /// Type inference (naturally right-to-left):
  /// ```java
  /// Type resolvedType = extent.fold(
  ///   e -> e.localType(),
  ///   (childType, parent) -> parent.resolveType(childType)
  /// );
  /// // Resolves from most specific (child) to general (root)
  /// ```
  ///
  /// Why bidirectional matters:
  /// - **Performance**: Choose traversal matching data dependency direction
  /// - **Expressiveness**: Write accumulator naturally for problem domain
  /// - **Composition**: Combine fold/foldTo operations in same hierarchy
  /// - **Flexibility**: Same abstraction for opposite traversal needs
  ///
  /// Critical behaviors verified:
  /// - fold with prepend accumulator produces correct result
  /// - foldTo with append accumulator produces correct result
  /// - Both produce IDENTICAL final result ("first.second.third")
  /// - Symmetry: direction + accumulator strategy = same output
  ///
  /// Expected: Both fold and foldTo produce "first.second.third"
  @Test
  void testFoldToVsFoldOrder () {

    final var a = TestExtent.root ( "first" );
    final var b = a.child ( "second" );
    final var c = b.child ( "third" );

    // fold builds right-to-left
    final var foldResult = c.fold (
      e -> e.part ().toString (),
      ( acc, e ) -> e.part () + "." + acc
    );

    // foldTo builds left-to-right
    final var foldToResult = c.foldTo (
      e -> e.part ().toString (),
      ( acc, e ) -> acc + "." + e.part ()
    );

    assertEquals ( foldResult, foldToResult );
    assertEquals ( "first.second.third", foldResult );

  }

  @SuppressWarnings ( "DataFlowIssue" )
  @Test
  void testFoldToWithNullAccumulatorThrows () {

    final var extent = TestExtent.root ( "test" );

    assertThrows (
      NullPointerException.class,
      () -> extent.foldTo ( _ -> 1, null )
    );

  }

  @SuppressWarnings ( "DataFlowIssue" )
  @Test
  void testFoldToWithNullInitializerThrows () {

    final var extent = TestExtent.root ( "test" );

    assertThrows (
      NullPointerException.class,
      () -> extent.foldTo ( null, ( acc, _ ) -> acc )
    );

  }

  @Test
  void testFoldToWithTransformation () {

    final var root = TestExtent.root ( "one" );
    final var child = root.child ( "two" );

    final var result = child.foldTo (
      e -> e.part ().toString ().toUpperCase (),
      ( acc, e ) -> acc + "-" + e.part ().toString ().toUpperCase ()
    );

    assertEquals ( "ONE-TWO", result );

  }

  @SuppressWarnings ( "DataFlowIssue" )
  @Test
  void testFoldWithNullAccumulatorThrows () {

    final var extent = TestExtent.root ( "test" );

    assertThrows (
      NullPointerException.class,
      () -> extent.fold ( _ -> 1, null )
    );

  }

  // ===========================
  // Stream Tests
  // ===========================

  @SuppressWarnings ( "DataFlowIssue" )
  @Test
  void testFoldWithNullInitializerThrows () {

    final var extent = TestExtent.root ( "test" );

    assertThrows (
      NullPointerException.class,
      () -> extent.fold ( null, ( acc, _ ) -> acc )
    );

  }

  /// Validates iterator traversal order: right-to-left (child to root).
  ///
  /// This test verifies the fundamental iteration direction of Extent hierarchies:
  /// iteration proceeds from the current extent (rightmost/leaf) toward the root
  /// (leftmost/extremity). This right-to-left traversal is consistent across all
  /// Extent traversal operations (iterator, fold, stream).
  ///
  /// Hierarchy Structure:
  /// ```
  /// a (root/extremity) → b → c (current extent)
  /// ```
  ///
  /// Iterator Traversal Order:
  /// ```
  /// iterator.next() → c (start at current extent)
  /// iterator.next() → b (move toward root)
  /// iterator.next() → a (reach root/extremity)
  /// iterator.hasNext() → false (exhausted)
  /// ```
  ///
  /// Why right-to-left matters:
  /// - **Natural path construction**: Build paths from most specific to least specific
  /// - **Efficient enclosure access**: Immediate parent always first
  /// - **Consistent with fold**: Both iterate in same direction (right-to-left)
  /// - **Memory locality**: Access recently created (child) extents first
  ///
  /// Real-world analogy:
  /// File path "/home/user/documents/file.txt":
  /// - Iterator starts at "file.txt" (most specific)
  /// - Moves to "documents", then "user", then "home"
  /// - Reaches "/" (root/extremity)
  ///
  /// Usage Pattern:
  /// ```java
  /// // Find first ancestor matching predicate
  /// for (Extent<E> extent : hierarchy) {
  ///   if (predicate.test(extent)) {
  ///     return extent;  // Found closest match
  ///   }
  /// }
  /// ```
  ///
  /// Critical behaviors verified:
  /// - First element is current extent (c)
  /// - Second element is immediate parent (b)
  /// - Last element is root (a)
  /// - Iterator exhausted after root (hasNext = false)
  ///
  /// Expected: Traversal order `[c, b, a]` (right-to-left)
  @Test
  void testIterator () {

    final var a = TestExtent.root ( "a" );
    final var b = a.child ( "b" );
    final var c = b.child ( "c" );

    final var iterator = c.iterator ();

    assertTrue ( iterator.hasNext () );
    assertSame ( c, iterator.next () );

    assertTrue ( iterator.hasNext () );
    assertSame ( b, iterator.next () );

    assertTrue ( iterator.hasNext () );
    assertSame ( a, iterator.next () );

    assertFalse ( iterator.hasNext () );

  }

  @Test
  void testIteratorMultiplePasses () {

    final var root = TestExtent.root ( "a" );
    final var child = root.child ( "b" );

    // First iteration
    final var values1 = new ArrayList < String > ();
    child.iterator ().forEachRemaining (
      e -> values1.add ( e.part ().toString () )
    );

    // Second iteration
    final var values2 = new ArrayList < String > ();
    child.iterator ().forEachRemaining (
      e -> values2.add ( e.part ().toString () )
    );

    assertEquals ( values1, values2 );
    assertEquals ( List.of ( "b", "a" ), values1 );

  }

  @Test
  void testIteratorOnRoot () {

    final var root = TestExtent.root ( "root" );
    final var iterator = root.iterator ();

    assertTrue ( iterator.hasNext () );
    assertSame ( root, iterator.next () );
    assertFalse ( iterator.hasNext () );

  }

  @Test
  void testIteratorThrowsWhenExhausted () {

    final var root = TestExtent.root ( "root" );
    final var iterator = root.iterator ();

    iterator.next (); // consume the only element

    assertThrows ( NoSuchElementException.class, iterator::next );

  }

  // ===========================
  // CompareTo Tests
  // ===========================

  @Test
  void testLongHierarchyChain () {

    var current = TestExtent.root ( "level0" );

    for ( var i = 1; i <= 100; i++ ) {
      current = current.child ( "level" + i );
    }

    assertEquals ( 101, current.depth () );
    assertEquals ( 101L, current.stream ().count () );
    assertEquals ( "level0", current.extremity ().part () );

  }

  @Test
  void testMultipleChildrenNotInIterator () {

    final var root = TestExtent.root ( "root" );
    final var child1 = root.child ( "child1" );
    final var child2 = root.child ( "child2" );

    // child1's iterator should only include child1 and root
    final var values1 = child1.stream ()
      .map ( e -> e.part ().toString () )
      .toList ();

    assertEquals ( List.of ( "child1", "root" ), values1 );

    // child2's iterator should only include child2 and root
    final var values2 = child2.stream ()
      .map ( e -> e.part ().toString () )
      .toList ();

    assertEquals ( List.of ( "child2", "root" ), values2 );

  }

  @Test
  void testPathDefaultSeparator () {

    final var a = TestExtent.root ( "a" );
    final var b = a.child ( "b" );
    final var c = b.child ( "c" );

    assertEquals ( "a/b/c", c.path ().toString () );

  }

  @Test
  void testPathOnRoot () {

    final var root = TestExtent.root ( "root" );
    assertEquals ( "root", root.path ().toString () );

  }

  // ===========================
  // Within Tests
  // ===========================

  @Test
  void testPathWithCharSeparator () {

    final var root = TestExtent.root ( "x" );
    final var child = root.child ( "y" );

    assertEquals ( "x-y", child.path ( '-' ).toString () );

  }

  @Test
  void testPathWithMapper () {

    final var root = TestExtent.root ( "hello" );
    final var child = root.child ( "world" );

    final var result = child.path (
      e -> e.part ().toString ().toUpperCase (),
      '/'
    );

    assertEquals ( "HELLO/WORLD", result.toString () );

  }

  @Test
  void testPathWithMapperAndStringSeparator () {

    final var a = TestExtent.root ( "foo" );
    final var b = a.child ( "bar" );

    final var result = b.path (
      e -> e.part ().toString ().toUpperCase (),
      " -> "
    );

    assertEquals ( "FOO -> BAR", result.toString () );

  }

  @SuppressWarnings ( "DataFlowIssue" )
  @Test
  void testPathWithNullMapperAndStringSeparatorThrows () {

    final var extent = TestExtent.root ( "test" );

    assertThrows (
      NullPointerException.class,
      () -> extent.path ( null, "::" )
    );

    assertThrows (
      NullPointerException.class,
      () -> extent.path ( Extent::part, null )
    );

  }

  @SuppressWarnings ( "DataFlowIssue" )
  @Test
  void testPathWithNullMapperThrows () {

    final var extent = TestExtent.root ( "test" );

    assertThrows (
      NullPointerException.class,
      () -> extent.path ( null, '/' )
    );

  }

  @SuppressWarnings ( "DataFlowIssue" )
  @Test
  void testPathWithNullStringSeparatorThrows () {

    final var extent = TestExtent.root ( "test" );

    assertThrows (
      NullPointerException.class,
      () -> extent.path ( null )
    );

  }

  // ===========================
  // Edge Cases and Integration Tests
  // ===========================

  @Test
  void testPathWithStringSeparator () {

    final var a = TestExtent.root ( "a" );
    final var b = a.child ( "b" );
    final var c = b.child ( "c" );

    assertEquals ( "a::b::c", c.path ( "::" ).toString () );

  }

  @Test
  void testStream () {

    final var a = TestExtent.root ( "a" );
    final var b = a.child ( "b" );
    final var c = b.child ( "c" );

    final var values = c.stream ()
      .map ( e -> e.part ().toString () )
      .toList ();

    assertEquals ( List.of ( "c", "b", "a" ), values );

  }

  @Test
  void testStreamCount () {

    final var root = TestExtent.root ( "a" );
    final var child = root.child ( "b" );
    final var grandchild = child.child ( "c" );

    assertEquals ( 3L, grandchild.stream ().count () );
    assertEquals ( 2L, child.stream ().count () );
    assertEquals ( 1L, root.stream ().count () );

  }

  @Test
  void testStreamMatchesDepth () {

    final var root = TestExtent.root ( "a" );
    final var child = root.child ( "b" );
    final var grandchild = child.child ( "c" );

    assertEquals ( grandchild.depth (), grandchild.stream ().count () );
    assertEquals ( child.depth (), child.stream ().count () );
    assertEquals ( root.depth (), root.stream ().count () );

  }

  @Test
  void testStreamMatchesFold () {

    final var root = TestExtent.root ( "a" );
    final var child = root.child ( "b" );
    final var grandchild = child.child ( "c" );

    final var streamCount = grandchild.stream ().count ();

    final var foldCount = grandchild.fold (
      _ -> 1,
      ( acc, _ ) -> acc + 1
    );

    assertEquals ( streamCount, (long) foldCount );

  }

  @Test
  void testStreamOperations () {

    final var a = TestExtent.root ( "alpha" );
    final var b = a.child ( "beta" );
    final var c = b.child ( "gamma" );

    final var maxLength = c.stream ()
      .map ( e -> e.part ().toString () )
      .mapToInt ( String::length )
      .max ()
      .orElse ( 0 );

    assertEquals ( 5, maxLength ); // "alpha" and "gamma" are 5 chars

    final var hasShortName = c.stream ()
      .anyMatch ( e -> e.part ().length () < 5 );

    assertTrue ( hasShortName ); // "beta" is 4 chars

  }

  @Test
  void testWithin () {

    final var root = TestExtent.root ( "root" );
    final var child = root.child ( "child" );
    final var grandchild = child.child ( "grandchild" );

    assertTrue ( child.within ( root ) );
    assertTrue ( grandchild.within ( root ) );
    assertTrue ( grandchild.within ( child ) );

  }

  @Test
  void testWithinDeepHierarchy () {

    var current = TestExtent.root ( "level0" );
    final var root = current;

    for ( var i = 1; i <= 10; i++ ) {
      current = current.child ( "level" + i );
    }

    assertTrue ( current.within ( root ) );

  }

  @SuppressWarnings ( "DataFlowIssue" )
  @Test
  void testWithinNullThrows () {

    final var extent = TestExtent.root ( "test" );

    assertThrows (
      NullPointerException.class,
      () -> extent.within ( null )
    );

  }

  @Test
  void testWithinReverse () {

    final var root = TestExtent.root ( "root" );
    final var child = root.child ( "child" );

    assertFalse ( root.within ( child ) );

  }

  @Test
  void testWithinSelf () {

    final var root = TestExtent.root ( "root" );
    assertFalse ( root.within ( root ) );

  }

  @Test
  void testWithinUnrelated () {

    final var tree1 = TestExtent.root ( "tree1" );
    final var tree2 = TestExtent.root ( "tree2" );

    assertFalse ( tree1.within ( tree2 ) );
    assertFalse ( tree2.within ( tree1 ) );

  }

  /// A simple test implementation of Extent for testing default methods.
  /// Represents a hierarchical structure of string values.

  private record TestExtent( String value, TestExtent parent )
    implements Extent < TestExtent, TestExtent > {

    private TestExtent (
      final String value
    ) {

      this ( value, null );

    }

    /// Creates a root extent with no parent

    private static TestExtent root (
      final String value
    ) {

      return new TestExtent ( value );

    }

    /// Returns the parent (enclosure) of this extent

    @NotNull
    @Override
    public Optional < TestExtent > enclosure () {

      return Optional.ofNullable ( parent );

    }

    /// Returns the part (value) of this extent

    @NotNull
    @Override
    public CharSequence part () {

      return value;

    }

    /// Creates a child extent

    private TestExtent child (
      final String value
    ) {

      return new TestExtent ( value, this );

    }

  }

}
