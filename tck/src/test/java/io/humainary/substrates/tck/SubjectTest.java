// Copyright (c) 2025 William David Louth

package io.humainary.substrates.tck;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/// Comprehensive tests for the Subject interface.
///
/// This test class covers:
/// - Subject hierarchical structure and enclosure
/// - Subject path and name relationships
/// - Subject identity and equality
/// - Parent-child relationships in substrate hierarchy
///
/// Every substrate component (Circuit, Conduit, Channel, etc.) has a Subject
/// that captures its identity, hierarchical name, and position in the
/// containment tree.
///
/// @author William David Louth
/// @since 1.0
final class SubjectTest
  extends TestSupport {

  private Cortex cortex;

  @BeforeEach
  void setup () {

    cortex = cortex ();

  }

  /// Validates nested subject relationships: parent-child enclosure, path construction, and hierarchy navigation.
  ///
  /// This test demonstrates how subjects form a containment hierarchy when substrate
  /// components are created within each other. It verifies path composition, enclosure
  /// relationships, and extremity calculation.
  ///
  /// Hierarchy Structure:
  /// ```
  /// Circuit (root)
  ///   └── Conduit (child)
  /// ```
  ///
  /// Subject Relationships:
  /// - **Enclosure**: Child subject maintains reference to parent subject
  /// - **Path**: Child's path includes parent's part as prefix
  /// - **Depth**: Number of levels in hierarchy (root=1, child=2)
  /// - **Extremity**: Root-most ancestor in the hierarchy chain
  ///
  /// Path Construction:
  /// ```
  /// Circuit subject.part():  "Subject[name=subject.test.nested.circuit, type=Circuit, id=...]"
  /// Conduit subject.part():  "Subject[name=subject.test.nested.conduit, type=Conduit, id=...]"
  /// Conduit subject.path():  "Subject[...circuit...]" + delimiter + "Subject[...conduit...]"
  /// ```
  ///
  /// The path is a hierarchical composition showing the full containment chain
  /// from root to current subject, enabling tracing of component relationships.
  ///
  /// Enclosure Navigation:
  /// Two equivalent ways to access the parent:
  /// ```java
  /// // Method 1: Optional access
  /// Subject<?> parent = conduitSubject.enclosure().orElseThrow();
  ///
  /// // Method 2: Consumer callback
  /// conduitSubject.enclosure(parent -> {
  ///   // Work with parent subject
  /// });
  /// ```
  ///
  /// Extremity vs Enclosure:
  /// - **enclosure()**: Immediate parent (one level up)
  /// - **extremity()**: Root ancestor (top of hierarchy)
  /// - For 2-level hierarchy: conduit.extremity() == circuit (same as enclosure)
  /// - For 3-level hierarchy: subscription.extremity() == circuit (skips conduit)
  ///
  /// Depth Calculation:
  /// - Root component (cortex): depth = 1
  /// - First-level children (circuits): depth = 2
  /// - Second-level children (conduits): depth = 3
  /// - Third-level children (subscriptions): depth = 4
  /// - Depth = number of subjects in enclosure chain including self
  ///
  /// Why This Matters:
  /// - **Hierarchical naming**: Paths enable unambiguous component identification
  /// - **Lifecycle management**: Parent responsible for children (cascading close)
  /// - **Resource tracking**: Navigate upward to find owning circuit/scope
  /// - **Observability**: Full path aids in debugging and logging
  /// - **Access control**: Check if component is within specific scope
  ///
  /// Real-World Examples:
  /// 1. **Service mesh routing**: Circuit (service) → Conduit (endpoint) → Channel (request)
  /// 2. **Neural networks**: Circuit (network) → Conduit (layer) → Channel (neuron)
  /// 3. **Event processing**: Circuit (processor) → Conduit (stream) → Channel (partition)
  /// 4. **Monitoring systems**: Circuit (monitor) → Conduit (metric) → Channel (dimension)
  /// 5. **Workflow engines**: Circuit (workflow) → Conduit (stage) → Channel (task)
  ///
  /// Expected Behavior:
  /// - Path starts with parent part, ends with child part
  /// - Conduit depth is 3 (Cortex → Circuit → Conduit)
  /// - Conduit enclosure is circuit subject
  /// - Conduit extremity is cortex subject (root of hierarchy)
  /// - subject.toString() returns path representation
  @Test
  void testNestedSubjectPathAndEnclosure () {

    final var circuit = cortex.circuit (
      cortex.name ( "subject.test.nested.circuit" )
    );

    try {

      final var conduitName = cortex.name ( "subject.test.nested.conduit" );
      final var conduit =
        circuit.conduit ( conduitName, Composer.pipe ( Integer.class ) );

      final var circuitSubject = circuit.subject ();
      final var conduitSubject = conduit.subject ();

      final var path = conduitSubject.path ().toString ();

      assertTrue ( path.contains ( circuitSubject.part () ) );
      assertTrue ( path.endsWith ( conduitSubject.part () ) );
      assertEquals ( path, conduitSubject.toString () );

      assertEquals ( 3, conduitSubject.depth () );

      assertTrue ( conduitSubject.enclosure ().isPresent () );
      assertSame ( circuitSubject, conduitSubject.enclosure ().orElseThrow () );
      assertEquals ( Cortex.class, conduitSubject.extremity ().type () );

      final AtomicReference < Subject < ? > > captured = new AtomicReference <> ();
      conduitSubject.enclosure ( captured::set );

      assertSame ( circuitSubject, captured.get () );

    } finally {

      circuit.close ();

    }

  }

  /// Validates root subject properties: identity, type, path representation, and initial state.
  ///
  /// This test verifies the fundamental properties of a root-level subject (created
  /// without a parent). Root subjects represent top-level components like circuits
  /// and scopes that don't belong to any enclosing component.
  ///
  /// Root Subject Characteristics:
  /// - **Depth = 1**: No parent levels above
  /// - **No enclosure**: enclosure() returns empty Optional
  /// - **Self as extremity**: extremity() returns self (no ancestors)
  /// - **Path = part**: For root, full path equals its own part
  /// - **Empty initial state**: No slots until explicitly added
  ///
  /// Subject Core Properties:
  /// ```java
  /// Subject<Circuit> subject = circuit.subject();
  ///
  /// subject.type()  → Circuit.class       // Component type (for type-safe operations)
  /// subject.name()  → Name("subject.test.circuit")  // Hierarchical name
  /// subject.id()    → Id(...)             // Unique identity (UUID-based)
  /// subject.state() → State(empty)        // Mutable state slots
  /// ```
  ///
  /// Subject Part Format:
  /// The part() returns a structured representation:
  /// ```
  /// Subject[name=subject.test.circuit, type=Circuit, id=abc-123-def]
  /// ```
  ///
  /// This format enables:
  /// - Human-readable debugging output
  /// - Structured logging with parseable fields
  /// - Type information for runtime introspection
  /// - Unique identification via ID
  ///
  /// Path vs Part for Root:
  /// ```
  /// Root subject:
  ///   part() == "Subject[name=..., type=Circuit, id=...]"
  ///   path() == part()  // No parent prefix
  ///   toString() == path()
  ///
  /// Child subject:
  ///   part() == "Subject[name=..., type=Conduit, id=...]"
  ///   path() == "Parent part" + delimiter + "Child part"
  ///   toString() == path()
  /// ```
  ///
  /// Subject Identity (Id):
  /// - Globally unique identifier (typically UUID)
  /// - Immutable once assigned
  /// - Independent of name (name can be shared, ID never duplicated)
  /// - Enables reference equality for same component
  ///
  /// Subject State:
  /// - Initially empty (0 slots)
  /// - Slots added via state() mutations
  /// - Type-safe value storage by name
  /// - Immutable state snapshots (structural sharing)
  ///
  /// Why This Matters:
  /// - **Component identity**: Unique ID + hierarchical name
  /// - **Type safety**: subject.type() enables type-specific operations
  /// - **Debugging**: toString() provides full context
  /// - **State management**: Every component has mutable typed state
  /// - **Equality semantics**: Compare by ID, not name
  ///
  /// Real-World Examples:
  /// 1. **Circuit identification**: Track individual circuits in pool by ID
  /// 2. **Logging context**: Include circuit.subject().path() in logs
  /// 3. **Metrics tagging**: Use subject.name() as metric dimension
  /// 4. **State attachment**: Store config/metadata in subject.state()
  /// 5. **Lifecycle tracking**: Monitor subject creation/closure
  ///
  /// Root vs Non-Root Comparison:
  /// | Property    | Root Subject       | Child Subject           |
  /// |-------------|-------------------|------------------------|
  /// | depth()     | 1                 | parent.depth() + 1     |
  /// | enclosure() | empty Optional    | Optional(parent)       |
  /// | extremity() | self              | root ancestor          |
  /// | path()      | part()            | parent.path() + part() |
  ///
  /// Expected Behavior:
  /// - Subject has Circuit type
  /// - Subject has expected name
  /// - Subject has non-null unique ID
  /// - Part representation includes name, type, ID fields
  /// - Path includes Cortex parent prefix
  /// - Depth is 2 (Cortex → Circuit)
  /// - Has enclosure (Cortex subject)
  /// - Extremity is Cortex (root ancestor)
  /// - Initial state is empty (0 slots)
  @Test
  void testRootSubjectProperties () {

    final var circuitName = cortex.name ( "subject.test.circuit" );
    final var circuit = cortex.circuit ( circuitName );

    try {

      final var subject = circuit.subject ();

      assertEquals ( Circuit.class, subject.type () );
      assertEquals ( circuitName, subject.name () );
      assertNotNull ( subject.id () );

      final var part = subject.part ();

      assertTrue ( part.startsWith ( "Subject[name=" ) );
      assertTrue ( part.contains ( circuitName.toString () ) );
      assertTrue ( part.contains ( "type=Circuit" ) );

      // Path now includes Cortex parent prefix
      assertTrue ( subject.path ().toString ().contains ( "Cortex" ) );
      assertTrue ( subject.path ().toString ().contains ( part ) );

      assertEquals ( 2, subject.depth () );
      assertTrue ( subject.enclosure ().isPresent () );
      assertEquals ( Cortex.class, subject.enclosure ().orElseThrow ().type () );
      assertEquals ( Cortex.class, subject.extremity ().type () );

      assertEquals ( 0L, subject.state ().stream ().count () );

    } finally {

      circuit.close ();

    }

  }

  /// Validates 3-level hierarchy: stream traversal, within() containment checks, and compareTo ordering.
  ///
  /// This test demonstrates deeper subject hierarchies (3 levels) and verifies the
  /// stream(), within(), compareTo(), and extremity() operations that enable navigation
  /// and queries across the containment tree.
  ///
  /// Four-Level Hierarchy:
  /// ```
  /// Cortex (root, depth=1)
  ///   └── Circuit (child, depth=2)
  ///         └── Conduit (grandchild, depth=3)
  ///               └── Subscription (great-grandchild, depth=4)
  /// ```
  ///
  /// Enclosure Chain Verification:
  /// ```java
  /// subscription.subject().enclosure() → conduit.subject()
  /// conduit.subject().enclosure()      → circuit.subject()
  /// circuit.subject().enclosure()      → cortex.subject()
  /// cortex.subject().enclosure()       → empty Optional (root)
  /// ```
  ///
  /// Subject Stream Traversal:
  /// The stream() method traverses upward from child to root:
  /// ```
  /// subscription.subject().stream() produces:
  ///   [subscription.subject(), conduit.subject(), circuit.subject(), cortex.subject()]
  ///
  /// Traversal order: self → enclosure → enclosure.enclosure → ... → root
  /// ```
  ///
  /// This is the **reverse** of path construction, enabling:
  /// - Upward traversal from leaf to root
  /// - Collecting all ancestors for analysis
  /// - Finding first ancestor matching predicate
  /// - Building full containment context
  ///
  /// Within Containment Checks:
  /// The within() method checks if subject is contained within another:
  /// ```java
  /// subscription.within(conduit)  → true  // subscription is inside conduit
  /// subscription.within(circuit)  → true  // subscription is inside circuit
  /// conduit.within(subscription)  → false // conduit is NOT inside subscription (reversed)
  /// conduit.within(circuit)       → true  // conduit is inside circuit
  /// circuit.within(conduit)       → false // circuit is NOT inside conduit
  /// ```
  ///
  /// Implementation concept:
  /// ```java
  /// boolean within(Subject<?> ancestor) {
  ///   return stream().anyMatch(s -> s == ancestor);
  /// }
  /// ```
  ///
  /// CompareTo Ordering Semantics:
  /// Subjects are ordered by their position in the hierarchy:
  /// ```
  /// Parent compareTo Child → negative (parent < child)
  /// Child compareTo Parent → positive (child > parent)
  /// Sibling compareTo Sibling → undefined order (implementation-dependent)
  /// ```
  ///
  /// In this test:
  /// ```java
  /// conduit.compareTo(subscription) → negative (conduit is ancestor, comes before)
  /// subscription.compareTo(conduit) → positive (subscription is descendant, comes after)
  /// ```
  ///
  /// This ordering enables:
  /// - Sorting components by hierarchy depth
  /// - Processing parents before children
  /// - Consistent ordering in collections
  ///
  /// Extremity in Deep Hierarchies:
  /// For any depth, extremity() always returns the root:
  /// ```java
  /// subscription.extremity() → cortex.subject()  // skip conduit and circuit, go to root
  /// conduit.extremity()      → cortex.subject()  // skip circuit, go to root
  /// circuit.extremity()      → cortex.subject()  // go to root
  /// cortex.extremity()       → cortex.subject()  // root returns self
  /// ```
  ///
  /// Depth Calculation:
  /// ```
  /// cortex.depth()        → 1  (root)
  /// circuit.depth()       → 2  (cortex.depth() + 1)
  /// conduit.depth()       → 3  (circuit.depth() + 1)
  /// subscription.depth()  → 4  (conduit.depth() + 1)
  /// ```
  ///
  /// Why This Matters:
  /// - **Containment queries**: Check if component is within scope/circuit
  /// - **Ancestor traversal**: Navigate upward to find owning resources
  /// - **Hierarchical processing**: Process in depth-first or breadth-first order
  /// - **Access control**: Verify component belongs to authorized circuit
  /// - **Lifecycle management**: Close children before parents
  ///
  /// Real-World Examples:
  /// 1. **Authorization checks**: Verify subscription within authorized circuit
  /// 2. **Resource cleanup**: Stream all ancestors to close in order
  /// 3. **Logging context**: Collect full ancestor chain for log context
  /// 4. **Monitoring hierarchy**: Navigate from metric to owning service
  /// 5. **Debugging**: Find which circuit contains a failing component
  ///
  /// Stream Use Cases:
  /// ```java
  /// // Find first Circuit ancestor
  /// Optional<Subject<Circuit>> circuit = subject.stream()
  ///   .filter(s -> s.type() == Circuit.class)
  ///   .map(s -> (Subject<Circuit>) s)
  ///   .findFirst();
  ///
  /// // Collect all ancestor names
  /// List<Name> names = subject.stream()
  ///   .map(Subject::name)
  ///   .collect(toList());
  ///
  /// // Check if any ancestor has specific state
  /// boolean hasFlag = subject.stream()
  ///   .anyMatch(s -> s.state().value(flagSlot).orElse(false));
  /// ```
  ///
  /// Expected Behavior:
  /// - Subscription has Subscription type
  /// - Subscription's parent is conduit
  /// - Conduit's parent is circuit
  /// - Circuit's parent is cortex
  /// - Stream produces `[subscription, conduit, circuit, cortex]` in order
  /// - Subscription is within conduit (true)
  /// - Subscription is within circuit (true)
  /// - Subscription is within cortex (true)
  /// - Conduit is NOT within subscription (false - reversed relationship)
  /// - Extremity of subscription is cortex (root ancestor)
  /// - Conduit < subscription in compareTo ordering (parent comes before child)
  /// - Subscription depth is 4
  @Test
  void testSubjectHierarchyIterationAndWithin () {

    final var circuit = cortex.circuit (
      cortex.name ( "subject.test.hierarchy.circuit" )
    );

    try {

      final var conduit =
        circuit.conduit (
          cortex.name ( "subject.test.hierarchy.conduit" ),
          Composer.pipe ( Integer.class )
        );

      final Subscriber < Integer > subscriber =
        circuit.subscriber (
          cortex.name ( "subject.test.hierarchy.subscriber" ),
          ( _, registrar ) -> registrar.register ( Receptor.of () )
        );

      final var subscription = conduit.subscribe ( subscriber );

      try {

        assertEquals ( Subscription.class, subscription.subject ().type () );
        assertSame ( conduit.subject (), subscription.subject ().enclosure ().orElseThrow () );
        assertSame ( circuit.subject (), conduit.subject ().enclosure ().orElseThrow () );
        assertSame ( cortex.subject (), circuit.subject ().enclosure ().orElseThrow () );

        final List < Subject < ? > > expectedStream = List.of (
          subscription.subject (),
          conduit.subject (),
          circuit.subject (),
          cortex.subject ()
        );

        assertEquals (
          expectedStream,
          subscription.subject ().stream ().toList ()
        );

        assertTrue ( subscription.subject ().within ( conduit.subject () ) );
        assertTrue ( subscription.subject ().within ( circuit.subject () ) );
        assertTrue ( subscription.subject ().within ( cortex.subject () ) );
        assertFalse ( conduit.subject ().within ( subscription.subject () ) );

        assertEquals ( cortex.subject (), subscription.subject ().extremity () );
        final var comparison = conduit.subject ().compareTo ( subscription.subject () );

        assertTrue ( comparison < 0 );
        assertEquals ( 4, subscription.subject ().depth () );

      } finally {

        subscription.close ();
        circuit.await ();

      }

    } finally {

      circuit.close ();

    }

  }

  /// Validates Subject.compareTo() implementation with comprehensive coverage.
  ///
  /// Tests the optimized compareTo implementations for Node and Root records,
  /// verifying hierarchical comparison semantics and identity check fast paths.
  ///
  /// CompareTo Semantics:
  /// - Identity: subject.compareTo(subject) == 0 (fast path)
  /// - Hierarchy: parent < child (negative), child > parent (positive)
  /// - Root vs Node: Root (shallower) < Node (deeper)
  /// - Siblings: Compared by name lexicographically
  /// - Transitivity: a < b && b < c ⟹ a < c
  /// - Consistency: sign(a.compareTo(b)) == -sign(b.compareTo(a))
  ///
  /// Optimization Coverage:
  /// This test verifies the record-specific compareTo implementations:
  /// - Node.compareTo: Direct field access to subject and name fields
  /// - Root.compareTo: Direct field access to name field
  /// - Identity check: Fast path returns 0 immediately
  /// - Pattern matching: instanceof checks for Node vs Root
  @Test
  void testSubjectCompareToSemantics () {

    final var circuitA = cortex.circuit (
      cortex.name ( "subject.test.compare.circuitA" )
    );

    final var circuitB = cortex.circuit (
      cortex.name ( "subject.test.compare.circuitB" )
    );

    try {

      final var conduitA =
        circuitA.conduit (
          cortex.name ( "subject.test.compare.conduitA" ),
          Composer.pipe ( Integer.class )
        );

      final var conduitB =
        circuitA.conduit (
          cortex.name ( "subject.test.compare.conduitB" ),
          Composer.pipe ( Integer.class )
        );

      final var conduitC =
        circuitB.conduit (
          cortex.name ( "subject.test.compare.conduitC" ),
          Composer.pipe ( Integer.class )
        );

      // Identity: same subject should return 0 (identity check fast path)
      assertEquals ( 0, circuitA.subject ().compareTo ( circuitA.subject () ) );
      assertEquals ( 0, conduitA.subject ().compareTo ( conduitA.subject () ) );

      // Hierarchy: parent < child
      assertTrue ( circuitA.subject ().compareTo ( conduitA.subject () ) < 0 );
      assertTrue ( conduitA.subject ().compareTo ( circuitA.subject () ) > 0 );

      // Siblings: compared by name lexicographically
      final int siblingComparison = conduitA.subject ().compareTo ( conduitB.subject () );
      assertTrue ( siblingComparison < 0 ); // "conduitA" < "conduitB"
      assertTrue ( conduitB.subject ().compareTo ( conduitA.subject () ) > 0 );

      // Different parents: compared by parent first, then by name
      final int crossParentComparison = conduitA.subject ().compareTo ( conduitC.subject () );
      assertTrue ( crossParentComparison < 0 ); // circuitA < circuitB

      // Consistency: sign(a.compareTo(b)) == -sign(b.compareTo(a))
      assertEquals (
        Integer.signum ( circuitA.subject ().compareTo ( conduitA.subject () ) ),
        -Integer.signum ( conduitA.subject ().compareTo ( circuitA.subject () ) )
      );

      // Transitivity: a < b && b < c ⟹ a < c
      final var cortexSubject = cortex.subject ();
      final var circuitSubject = circuitA.subject ();
      final var conduitSubject = conduitA.subject ();

      assertTrue ( cortexSubject.compareTo ( circuitSubject ) < 0 );
      assertTrue ( circuitSubject.compareTo ( conduitSubject ) < 0 );
      assertTrue ( cortexSubject.compareTo ( conduitSubject ) < 0 ); // transitivity

    } finally {

      circuitA.close ();
      circuitB.close ();

    }

  }

  /// Validates Subject.compareTo() with same hierarchical paths.
  ///
  /// Tests comparison when subjects have identical hierarchical names,
  /// demonstrating that comparison uses name first, then ID as tiebreaker.
  ///
  /// Key Insight:
  /// - Subjects with identical hierarchical paths but different IDs compare as NOT EQUAL
  /// - CompareTo uses hierarchical names first, then IDs for total ordering
  /// - Different object instances have different IDs, so they are distinguishable
  @Test
  void testSubjectCompareToWithSameNames () {

    final var sameName = cortex.name ( "subject.test.sameName" );

    final var circuitA = cortex.circuit ( sameName );
    final var circuitB = cortex.circuit ( sameName );

    try {

      final var conduitA =
        circuitA.conduit (
          sameName,
          Composer.pipe ( Integer.class )
        );

      final var conduitB =
        circuitB.conduit (
          sameName,
          Composer.pipe ( Integer.class )
        );

      // Subjects with identical hierarchical paths but different IDs compare as NOT EQUAL
      // Both have path: cortex → sameName (circuit) → sameName (conduit)
      // But different circuit IDs cause different comparison results
      final int comparison = conduitA.subject ().compareTo ( conduitB.subject () );
      assertNotEquals ( 0, comparison );

      // Verify: Different object instances with different IDs
      assertNotSame ( conduitA.subject (), conduitB.subject () );
      assertNotSame ( circuitA.subject (), circuitB.subject () );

      // Parents also compare as not equal (same names but different IDs)
      assertNotEquals ( 0, circuitA.subject ().compareTo ( circuitB.subject () ) );

      // Same subject compared to itself always returns 0
      assertEquals ( 0, conduitA.subject ().compareTo ( conduitA.subject () ) );

    } finally {

      circuitA.close ();
      circuitB.close ();

    }

  }

  /// Validates Subject.compareTo() with different names at same level.
  ///
  /// Tests that subjects with different names compare correctly
  /// based on name lexicographic ordering.
  @Test
  void testSubjectCompareToWithDifferentNames () {

    final var circuit = cortex.circuit (
      cortex.name ( "subject.test.different.circuit" )
    );

    try {

      final var conduitA =
        circuit.conduit (
          cortex.name ( "subject.test.different.aaa" ),
          Composer.pipe ( Integer.class )
        );

      final var conduitB =
        circuit.conduit (
          cortex.name ( "subject.test.different.zzz" ),
          Composer.pipe ( Integer.class )
        );

      // Subjects with different names: compared lexicographically
      final int comparison = conduitA.subject ().compareTo ( conduitB.subject () );

      assertTrue ( comparison < 0 ); // "aaa" < "zzz"
      assertTrue ( conduitB.subject ().compareTo ( conduitA.subject () ) > 0 );

      // Consistency: opposite signs
      assertEquals (
        Integer.signum ( comparison ),
        -Integer.signum ( conduitB.subject ().compareTo ( conduitA.subject () ) )
      );

    } finally {

      circuit.close ();

    }

  }

  /// Validates Subject.compareTo() edge cases and boundary conditions.
  ///
  /// Tests extreme scenarios including deeply nested hierarchies and
  /// comparison between very different hierarchy depths.
  @Test
  void testSubjectCompareToEdgeCases () {

    final var circuit = cortex.circuit (
      cortex.name ( "subject.test.edge.circuit" )
    );

    try {

      final var conduit =
        circuit.conduit (
          cortex.name ( "subject.test.edge.conduit" ),
          Composer.pipe ( Integer.class )
        );

      final Subscriber < Integer > subscriber =
        circuit.subscriber (
          cortex.name ( "subject.test.edge.subscriber" ),
          ( _, registrar ) -> registrar.register ( Receptor.of () )
        );

      final var subscription = conduit.subscribe ( subscriber );

      try {

        // Deep hierarchy: cortex < circuit < conduit < subscription
        assertTrue ( cortex.subject ().compareTo ( subscription.subject () ) < 0 );
        assertTrue ( subscription.subject ().compareTo ( cortex.subject () ) > 0 );

        // Multiple level difference: circuit < subscription
        assertTrue ( circuit.subject ().compareTo ( subscription.subject () ) < 0 );

        // Identity at each level
        assertEquals ( 0, cortex.subject ().compareTo ( cortex.subject () ) );
        assertEquals ( 0, circuit.subject ().compareTo ( circuit.subject () ) );
        assertEquals ( 0, conduit.subject ().compareTo ( conduit.subject () ) );
        assertEquals ( 0, subscription.subject ().compareTo ( subscription.subject () ) );

      } finally {

        subscription.close ();
        circuit.await ();

      }

    } finally {

      circuit.close ();

    }

  }

}
