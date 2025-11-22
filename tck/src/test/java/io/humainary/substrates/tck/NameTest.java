// Copyright (c) 2025 William David Louth

package io.humainary.substrates.tck;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

/// A comprehensive test kit for the [Name] interface.
///
/// This test kit covers all aspects of Name functionality including:
/// - Name creation from various sources (strings, enums, classes, members, iterables)
/// - Hierarchical structure and enclosure relationships
/// - Name composition and extension
/// - Extent operations (fold, foldTo, stream, iteration)
/// - Path representation with different separators
/// - Comparison and equality
/// - Edge cases and null handling
///
/// @author William David Louth
/// @since 1.0

final class NameTest
  extends TestSupport {

  private Cortex cortex;

  @BeforeEach
  void setup () {

    cortex = cortex ();

  }

  @Test
  void testCombinedFoldOperations () {

    final var name = cortex.name ( "a.b.c.d" );

    // Count total characters
    final var totalChars = name.fold (
      n -> n.value ().length (),
      ( acc, n ) -> acc + n.value ().length ()
    );

    assertEquals ( 4, totalChars );

    // Build reversed path
    final var reversedPath = name.fold (
      Name::value,
      ( acc, n ) -> n.value () + "/" + acc
    );

    assertEquals ( "a/b/c/d", reversedPath );

  }

  @SuppressWarnings ( "EqualsWithItself" )
  @Test
  void testComparison () {

    final var a = cortex.name ( "a" );
    final var b = cortex.name ( "b" );
    final var c = cortex.name ( "c" );

    assertTrue ( a.compareTo ( b ) < 0 );
    assertTrue ( c.compareTo ( b ) > 0 );
    assertEquals ( 0, a.compareTo ( a ) );

  }

  // ===========================
  // Basic Name Creation Tests
  // ===========================

  @Test
  void testComparisonWithHierarchy () {

    final var root = cortex.name ( "root" );
    final var rootChild = cortex.name ( "root.child" );

    assertTrue ( root.compareTo ( rootChild ) < 0 );

  }

  @Test
  void testComplexHierarchyNavigation () {

    final var name = cortex.name ( "root.level1.level2.level3.level4" );

    assertEquals ( 5, name.depth () );
    assertEquals ( "level4", name.value () );
    assertEquals ( "root", name.extremity ().value () );

    final var collected = name.stream ()
      .map ( Name::value )
      .toList ();

    assertEquals (
      List.of ( "level4", "level3", "level2", "level1", "root" ),
      collected
    );

  }

  @Test
  void testConsistentHashCode () {

    final var name1 = cortex.name ( "test.name" );
    final var name2 = cortex.name ( "test.name" );

    assertEquals ( name1.hashCode (), name2.hashCode () );

  }

  @Test
  void testCreateNameFromClass () {

    final var name = cortex.name ( String.class );

    assertEquals ( "java.lang.String", name.path ().toString () );

  }

  @Test
  void testCreateNameFromEnum () {

    enum TestEnum {FIRST, SECOND}

    final var name = cortex.name ( TestEnum.FIRST );

    assertEquals ( "FIRST", name.value () );

  }

  @Test
  void testCreateNameFromIterable () {

    final var parts = List.of ( "first", "second", "third" );
    final var name = cortex.name ( parts );

    assertEquals ( "third", name.value () );
    assertEquals ( 3, name.depth () );

  }

  @Test
  void testCreateNameFromIterableContainingNullThrows () {

    final var parts = new ArrayList < String > ();

    parts.add ( "root" );
    parts.add ( null );

    assertThrows (
      NullPointerException.class,
      () -> cortex.name ( parts )
    );

  }

  @Test
  void testCreateNameFromIterableWithMapper () {

    final var numbers = List.of ( 1, 2, 3 );
    final var name = cortex.name ( numbers, Object::toString );

    assertEquals ( "3", name.value () );
    assertEquals ( 3, name.depth () );

  }

  @Test
  void testCreateNameFromIterator () {

    final var parts = List.of ( "alpha", "beta", "gamma" );
    final var name = cortex.name ( parts.iterator () );

    assertEquals ( "gamma", name.value () );

  }

  @Test
  void testCreateNameFromIteratorContainingNullThrows () {

    final var parts = new ArrayList < String > ();

    parts.add ( "root" );
    parts.add ( null );

    assertThrows (
      NullPointerException.class,
      () -> cortex.name ( parts.iterator () )
    );

  }

  @Test
  void testCreateNameFromIteratorWithMapper () {

    final var numbers = List.of ( 10, 20, 30 );
    final var name = cortex.name ( numbers.iterator (), Object::toString );

    assertEquals ( "30", name.value () );

  }

  // ===========================
  // Name Extension Tests
  // ===========================

  @Test
  void testCreateNameFromMember () throws java.lang.Exception {

    final var method = String.class.getMethod ( "length" );
    final var name = cortex.name ( method );

    assertNotNull ( name );
    assertTrue ( name.value ().contains ( "length" ) );

  }

  @Test
  void testCreateNestedName () {

    final var name = cortex.name ( "root.child.grandchild" );

    assertEquals ( "grandchild", name.value () );
    assertTrue ( name.enclosure ().isPresent () );

  }

  @Test
  void testCreateRootName () {

    final var name = cortex.name ( "root" );

    assertEquals ( "root", name.value () );
    assertFalse ( name.enclosure ().isPresent () );

  }

  @Test
  void testDepth () {

    assertEquals ( 1, cortex.name ( "root" ).depth () );
    assertEquals ( 2, cortex.name ( "root.child" ).depth () );
    assertEquals ( 4, cortex.name ( "a.b.c.d" ).depth () );

  }

  @Test
  void testEmptyStringName () {

    // Empty string is not a valid name
    assertThrows (
      IllegalArgumentException.class,
      () -> cortex.name ( "" )
    );

  }

  @Test
  void testEnclosureChain () {

    final var name = cortex.name ( "a.b.c.d" );

    final var c = name.enclosure ().orElseThrow ();
    assertEquals ( "c", c.value () );

    final var b = c.enclosure ().orElseThrow ();
    assertEquals ( "b", b.value () );

    final var a = b.enclosure ().orElseThrow ();
    assertEquals ( "a", a.value () );

    assertFalse ( a.enclosure ().isPresent () );

  }

  @Test
  void testEnclosureConsumer () {

    final var name = cortex.name ( "parent.child" );
    final var result = new String[1];

    name.enclosure ( parent -> result[0] = parent.value () );

    assertEquals ( "parent", result[0] );

  }

  @Test
  void testEnclosureConsumerOnRoot () {

    final var name = cortex.name ( "root" );
    final var called = new boolean[1];

    name.enclosure ( _ -> called[0] = true );

    assertFalse ( called[0] );

  }

  @Test
  void testEnumConstantPathUsesDeclaringType () {

    final var name = cortex.name ( EnumWithBody.SPECIAL );

    assertEquals (
      "io.humainary.substrates.tck.NameTest.EnumWithBody.SPECIAL",
      name.path ().toString ()
    );

  }

  @Test
  void testEquality () {

    final var name1 = cortex.name ( "test.name" );
    final var name2 = cortex.name ( "test.name" );

    assertEquals ( name1, name2 );

  }

  @Test
  void testExtendNameWithClass () {

    final var root = cortex.name ( "packages" );
    final var extended = root.name ( Integer.class );

    assertTrue ( extended.value ().contains ( "Integer" ) );

  }

  @Test
  void testExtendNameWithCompositeName () {

    final var root = cortex.name ( "root" );
    final var suffix = cortex.name ( "child.grandchild" );
    final var extended = root.name ( suffix );

    assertEquals ( "root.child.grandchild", extended.path ().toString () );
    assertEquals ( "grandchild", extended.value () );
    assertEquals ( "root.child", extended.enclosure ().orElseThrow ().path ().toString () );

  }

  @Test
  void testExtendNameWithEnum () {

    enum Status {ACTIVE, INACTIVE}

    final var root = cortex.name ( "system" );
    final var extended = root.name ( Status.ACTIVE );

    assertEquals ( "ACTIVE", extended.value () );

  }

  // ===========================
  // Enclosure and Hierarchy Tests
  // ===========================

  @Test
  void testExtendNameWithInnerClassViaClassOverload () {

    final var base = cortex.name ( NameTest.class );
    final var extended = base.name ( Outer.Inner.class );

    assertEquals (
      "io.humainary.substrates.tck.NameTest.io.humainary.substrates.tck.NameTest.Outer.Inner",
      extended.path ().toString ()
    );

  }

  @Test
  void testExtendNameWithIterable () {

    final var root = cortex.name ( "root" );
    final var parts = List.of ( "level1", "level2" );
    final var extended = root.name ( parts );

    assertEquals ( "level2", extended.value () );

  }

  @Test
  void testExtendNameWithIterableAndMapper () {

    final var root = cortex.name ( "root" );
    final var numbers = List.of ( 1, 2 );
    final var extended = root.name ( numbers, n -> "item" + n );

    assertEquals ( "item2", extended.value () );

  }

  @Test
  void testExtendNameWithIterableContainingNullThrows () {

    final var name = cortex.name ( "base" );
    final var parts = new ArrayList < String > ();

    parts.add ( "child" );
    parts.add ( null );

    assertThrows (
      NullPointerException.class,
      () -> name.name ( parts )
    );

  }

  @Test
  void testExtendNameWithIterator () {

    final var root = cortex.name ( "root" );
    final var parts = List.of ( "a", "b" ).iterator ();
    final var extended = root.name ( parts );

    assertEquals ( "b", extended.value () );

  }

  @Test
  void testExtendNameWithIteratorAndMapper () {

    final var root = cortex.name ( "root" );
    final var numbers = List.of ( 5, 10 ).iterator ();
    final var extended = root.name ( numbers, n -> "val" + n );

    assertEquals ( "val10", extended.value () );

  }

  @Test
  void testExtendNameWithIteratorContainingNullThrows () {

    final var name = cortex.name ( "base" );
    final var parts = new ArrayList < String > ();

    parts.add ( "child" );
    parts.add ( null );

    assertThrows (
      NullPointerException.class,
      () -> name.name ( parts.iterator () )
    );

  }

  @Test
  void testExtendNameWithMember () throws java.lang.Exception {

    final var root = cortex.name ( "methods" );
    final var method = String.class.getMethod ( "isEmpty" );
    final var extended = root.name ( method );

    assertNotNull ( extended );

  }

  @Test
  void testExtendNameWithMultipartString () {

    final var root = cortex.name ( "root" );
    final var extended = root.name ( "child.grandchild" );

    assertEquals ( "grandchild", extended.value () );
    assertTrue ( extended.enclosure ().isPresent () );

  }

  // ===========================
  // Iteration Tests
  // ===========================

  @Test
  void testExtendNameWithName () {

    final var root = cortex.name ( "root" );
    final var suffix = cortex.name ( "suffix" );
    final var extended = root.name ( suffix );

    assertTrue ( extended.enclosure ().isPresent () );

  }

  @Test
  void testExtendNameWithString () {

    final var root = cortex.name ( "root" );
    final var extended = root.name ( "child" );

    assertEquals ( "child", extended.value () );
    assertEquals ( root, extended.enclosure ().orElseThrow () );

  }

  @Test
  void testExtensionPreservesIdentity () {

    final var root = cortex.name ( "root" );
    final var child1 = root.name ( "child" );
    final var child2 = root.name ( "child" );

    assertSame ( child1, child2 );

  }

  @Test
  void testExtent () {

    final var name = cortex.name ( "test" );
    assertSame ( name, name.extent () );

  }

  // ===========================
  // Fold Operations Tests
  // ===========================

  @Test
  void testExtentOperationsConsistency () {

    final var name = cortex.name ( "one.two.three.four" );

    // Verify depth equals stream count
    assertEquals ( name.depth (), name.stream ().count () );

    // Verify fold and stream produce same count
    final var foldCount = name.fold (
      _ -> 1,
      ( acc, _ ) -> acc + 1
    );

    assertEquals ( (long) foldCount, name.stream ().count () );

  }

  @Test
  void testExtremity () {

    final var name = cortex.name ( "a.b.c.d" );
    final var extremity = name.extremity ();

    assertEquals ( "a", extremity.value () );
    assertFalse ( extremity.enclosure ().isPresent () );

  }

  @Test
  void testExtremityOnRoot () {

    final var name = cortex.name ( "root" );
    assertSame ( name, name.extremity () );

  }

  @Test
  void testFold () {

    final var name = cortex.name ( "a.b.c" );

    final var result = name.fold (
      n -> n.value ().length (),
      ( acc, n ) -> acc + n.value ().length ()
    );

    assertEquals ( 3, result ); // c(1) + b(1) + a(1)

  }

  @Test
  void testFoldSingleElement () {

    final var name = cortex.name ( "test" );

    final var result = name.fold (
      _ -> 1,
      ( acc, _ ) -> acc + 1
    );

    assertEquals ( 1, result );

  }

  // ===========================
  // Path Representation Tests
  // ===========================

  @Test
  void testFoldTo () {

    final var name = cortex.name ( "a.b.c" );

    final var result = name.foldTo (
      Name::value,
      ( acc, n ) -> acc + "." + n.value ()
    );

    assertEquals ( "a.b.c", result );

  }

  @Test
  void testFoldToSingleElement () {

    final var name = cortex.name ( "solo" );

    final var result = name.foldTo (
      Name::value,
      ( acc, n ) -> acc + "." + n.value ()
    );

    assertEquals ( "solo", result );

  }

  /// Demonstrates the directional difference between fold (RTL) and foldTo (LTR).
  ///
  /// Tests hierarchical name "first.second.third" with both fold operations,
  /// showing that they traverse the hierarchy in opposite directions but can
  /// produce the same result when operations are symmetric.
  ///
  /// Traversal directions:
  /// - fold():   Right-to-left (leaf → root):  third → second → first
  /// - foldTo(): Left-to-right (root → leaf):  first → second → third
  ///
  /// In this test:
  /// - fold builds "third.second.first" by prepending each value
  /// - foldTo builds "first.second.third" by appending each value
  /// - Both produce same result due to symmetric string concatenation
  ///
  /// When to use each:
  /// - fold: Natural for reducing from most specific to least specific
  ///         (e.g., computing depth, building paths from leaf)
  /// - foldTo: Natural for accumulating from general to specific
  ///           (e.g., applying nested transformations, building paths from root)
  ///
  /// This is analogous to List.foldRight vs List.foldLeft in functional
  /// programming, where direction matters for non-commutative operations.
  ///
  /// Expected: Both produce "first.second.third" (though via different orders)
  @Test
  void testFoldVsFoldToOrder () {

    final var name = cortex.name ( "first.second.third" );

    // fold goes right-to-left (third -> second -> first)
    final var foldResult = name.fold (
      Name::value,
      ( acc, n ) -> n.value () + "." + acc
    );

    // foldTo goes left-to-right (first -> second -> third)
    final var foldToResult = name.foldTo (
      Name::value,
      ( acc, n ) -> acc + "." + n.value ()
    );

    assertEquals ( foldResult, foldToResult );

  }

  @Test
  void testIdentity () {

    final var name1 = cortex.name ( "test.name" );
    final var name2 = cortex.name ( "test.name" );

    assertSame ( name1, name2 ); // Names should be interned

  }

  @Test
  void testInequality () {

    final var name1 = cortex.name ( "first" );
    final var name2 = cortex.name ( "second" );

    assertNotEquals ( name1, name2 );

  }

  @Test
  void testInnerClassPathUsesDots () {

    final var name = cortex.name ( Outer.Inner.class );

    assertEquals (
      "io.humainary.substrates.tck.NameTest.Outer.Inner",
      name.path ().toString ()
    );

  }

  @Test
  void testIterableWithEmptyList () {

    final var name = cortex.name ( "base" );
    final var extended = name.name ( List.of () );

    // With empty iterable, should return same name
    assertSame ( name, extended );

  }

  // ===========================
  // Equality and Comparison Tests
  // ===========================

  @Test
  void testIterator () {

    final var name = cortex.name ( "a.b.c" );
    final Iterator < Name > iterator = name.iterator ();

    assertTrue ( iterator.hasNext () );
    assertEquals ( "c", iterator.next ().value () );

    assertTrue ( iterator.hasNext () );
    assertEquals ( "b", iterator.next ().value () );

    assertTrue ( iterator.hasNext () );
    assertEquals ( "a", iterator.next ().value () );

    assertFalse ( iterator.hasNext () );

  }

  @Test
  void testIteratorThrowsWhenExhausted () {

    final var name = cortex.name ( "single" );
    final Iterator < Name > iterator = name.iterator ();

    iterator.next (); // consume the only element

    assertThrows ( NoSuchElementException.class, iterator::next );

  }

  @Test
  void testLeadingDotSeparatorThrows () {

    // Leading dot creates empty segment which should be rejected
    assertThrows (
      IllegalArgumentException.class,
      () -> cortex.name ( ".a" )
    );

  }

  @Test
  void testMultipleConsecutiveDotsThrows () {

    // Various patterns with empty segments should all be rejected
    assertThrows (
      IllegalArgumentException.class,
      () -> cortex.name ( "a...b" )
    );

    assertThrows (
      IllegalArgumentException.class,
      () -> cortex.name ( "a..b..c" )
    );

    assertThrows (
      IllegalArgumentException.class,
      () -> cortex.name ( "..a" )
    );

    assertThrows (
      IllegalArgumentException.class,
      () -> cortex.name ( "a.." )
    );

  }

  @Test
  void testMultipleDotSeparators () {

    // Multiple consecutive separators create empty segments which should be rejected
    assertThrows (
      IllegalArgumentException.class,
      () -> cortex.name ( "a..b" )
    );

  }

  @Test
  void testMultipleExtensionPaths () {

    final var root = cortex.name ( "root" );
    final var path1 = root.name ( "a" ).name ( "b" );
    final var path2 = cortex.name ( "root.a.b" );

    assertSame ( path1, path2 );

  }

  // ===========================
  // Edge Cases and Null Tests
  // ===========================

  @Test
  void testNameAsNamePart () {

    final var root = cortex.name ( "root" );
    final var suffix = cortex.name ( "child.grandchild" );
    final var combined = root.name ( suffix );

    assertTrue ( combined.path ().toString ().contains ( "root" ) );
    assertEquals ( "grandchild", combined.value () );

  }

  @SuppressWarnings ( "DataFlowIssue" )
  @Test
  void testNullClassThrows () {

    assertThrows (
      NullPointerException.class,
      () -> cortex.name ( (Class < ? >) null )
    );

  }

  @SuppressWarnings ( "DataFlowIssue" )
  @Test
  void testNullEnumThrows () {

    assertThrows (
      NullPointerException.class,
      () -> cortex.name ( (Enum < ? >) null )
    );

  }

  @SuppressWarnings ( "DataFlowIssue" )
  @Test
  void testNullIterableThrows () {

    assertThrows (
      NullPointerException.class,
      () -> cortex.name ( (Iterable < String >) null )
    );

  }

  @SuppressWarnings ( "DataFlowIssue" )
  @Test
  void testNullIteratorThrows () {

    assertThrows (
      NullPointerException.class,
      () -> cortex.name ( (Iterator < String >) null )
    );

  }

  @SuppressWarnings ( "DataFlowIssue" )
  @Test
  void testNullStringThrows () {

    assertThrows (
      NullPointerException.class,
      () -> cortex.name ( (String) null )
    );

  }

  @Test
  void testPartVsPath () {

    final var name = cortex.name ( "root.child" );

    assertEquals ( "child", name.part () );
    assertEquals ( "root.child", name.path ().toString () );

  }

  @Test
  void testPathDefaultSeparator () {

    final var name = cortex.name ( "a.b.c" );
    assertEquals ( "a.b.c", name.path ().toString () );

  }

  @Test
  void testPathWithCustomCharSeparator () {

    final var name = cortex.name ( "x.y.z" );
    assertEquals ( "x-y-z", name.path ( '-' ).toString () );

  }

  @Test
  void testPathWithCustomStringSeparator () {

    final var name = cortex.name ( "one.two.three" );
    assertEquals ( "one::two::three", name.path ( "::" ).toString () );

  }

  @Test
  void testPathWithMapper () {

    final var name = cortex.name ( "a.b.c" );

    final Function < String, String > upperMapper = String::toUpperCase;
    assertEquals ( "A.B.C", name.path ( upperMapper ).toString () );

  }

  @Test
  void testPathWithMapperAndCharSeparator () {

    final var name = cortex.name ( "test.name" );

    assertEquals (
      "TEST|NAME",
      name.path ( n -> n.value ().toUpperCase (), '|' ).toString ()
    );

  }

  @Test
  void testPathWithMapperAndStringSeparator () {

    final var name = cortex.name ( "foo.bar" );

    assertEquals (
      "FOO -> BAR",
      name.path ( n -> n.value ().toUpperCase (), " -> " ).toString ()
    );

  }

  @Test
  void testSingleCharacterName () {

    final var name = cortex.name ( "x" );
    assertEquals ( "x", name.value () );

  }

  // ===========================
  // Integration Tests
  // ===========================

  @Test
  void testSpecialCharactersInName () {

    final var name = cortex.name ( "test-name_123" );
    assertEquals ( "test-name_123", name.value () );

  }

  @Test
  void testStream () {

    final var name = cortex.name ( "a.b.c.d" );
    final var values = name.stream ()
      .map ( Name::value )
      .toList ();

    assertEquals ( List.of ( "d", "c", "b", "a" ), values );

  }

  @Test
  void testStreamCount () {

    final var name = cortex.name ( "one.two.three" );
    assertEquals ( 3L, name.stream ().count () );

  }

  @Test
  void testStreamOperations () {

    final var name = cortex.name ( "alpha.beta.gamma.delta" );

    final var maxLength = name.stream ()
      .map ( Name::value )
      .mapToInt ( String::length )
      .max ()
      .orElse ( 0 );

    assertEquals ( 5, maxLength ); // "alpha", "gamma", "delta" are all 5 chars

    final var hasShortName = name.stream ()
      .map ( Name::value )
      .anyMatch ( v -> v.length () < 5 );

    assertTrue ( hasShortName ); // "beta" is 4 chars

  }

  @Test
  void testToString () {

    final var name = cortex.name ( "test.example" );
    final var str = name.toString ();

    assertNotNull ( str );
    assertFalse ( str.isEmpty () );

  }

  @Test
  void testTrailingDotSeparatorThrows () {

    // Trailing dot creates empty segment which should be rejected
    assertThrows (
      IllegalArgumentException.class,
      () -> cortex.name ( "a." )
    );

  }

  @Test
  void testVeryLongName () {

    final var longPart = "a".repeat ( 1000 );
    final var name = cortex.name ( longPart );

    assertEquals ( 1000, name.value ().length () );

  }

  @Test
  void testWithin () {

    final var root = cortex.name ( "root" );
    final var child = cortex.name ( "root.child" );
    final var grandchild = cortex.name ( "root.child.grandchild" );

    assertTrue ( child.within ( root ) );
    assertTrue ( grandchild.within ( root ) );
    assertTrue ( grandchild.within ( child ) );
    assertFalse ( root.within ( child ) );
    assertFalse ( root.within ( root ) );

  }

  enum EnumWithBody {

    BASIC,

    SPECIAL {
      @Override
      public String toString () {

        return
          "SPECIAL";

      }
    }

  }

  private static final class Outer {

    private static final class Inner {
    }

  }

}
