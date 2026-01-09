// Copyright (c) 2025 William David Louth

package io.humainary.substrates.tck;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.*;

/// Comprehensive tests for the State interface.
///
/// This test class covers:
/// - State creation and composition
/// - Slot-based value storage and retrieval
/// - State compaction (duplicate removal)
/// - Chaining and immutability
/// - Streaming and iteration
/// - Error handling for missing slots
///
/// State is an immutable linked list of slots, enabling compositional
/// configuration and metadata through chaining. Compaction removes
/// duplicates while preserving the most recent value for each name.
///
/// @author William David Louth
/// @since 1.0
final class StateTest
  extends TestSupport {

  private Cortex cortex;

  @BeforeEach
  void setup () {

    cortex = cortex ();

  }

  /// Validates compact removes duplicate (name,type) pairs while retaining most recent values.
  ///
  /// This test demonstrates State compaction: converting a linked-list state with
  /// duplicates into an optimized state containing only UNIQUE (name, type) pairs,
  /// keeping the MOST RECENT value for each. Compaction is essential for long-lived
  /// states that accumulate many updates, reducing memory and improving lookup performance.
  ///
  /// Test Scenario (chronological order):
  /// ```
  /// state = empty
  ///   .state(alpha, 1)     // alpha first appears
  ///   .state(beta, 2)      // beta first appears
  ///   .state(gamma, 3)     // gamma first appears
  ///   .state(beta, 20)     // beta updated (duplicate name+type)
  ///   .state(alpha, 10);   // alpha updated (duplicate name+type)
  ///
  /// Linked list: [alpha=10] → [beta=20] → [gamma=3] → [beta=2] → [alpha=1]
  ///                newest                                          oldest
  ///
  /// compact() removes older duplicates:
  ///   (alpha,int) appears twice → keep 10 (most recent), discard 1
  ///   (beta,int) appears twice → keep 20 (most recent), discard 2
  ///   (gamma,int) appears once → keep 3
  ///
  /// Result: [alpha=10, beta=20, gamma=3] (order undefined, 3 unique slots)
  /// ```
  ///
  /// Compaction Algorithm:
  /// ```
  /// compact() {
  ///   Map<(Name,Type), Slot> seen = new HashMap<>();
  ///
  ///   // Traverse from most recent to oldest
  ///   for (Slot slot : state) {
  ///     Key key = (slot.name(), slot.type());
  ///     if (!seen.containsKey(key)) {
  ///       seen.put(key, slot);  // First occurrence (most recent)
  ///     }
  ///     // Ignore subsequent occurrences (older values)
  ///   }
  ///
  ///   return newState(seen.values());
  /// }
  /// ```
  ///
  /// Why compaction matters:
  /// - **Memory reduction**: Removes duplicate entries from long chains
  /// - **Lookup performance**: Shorter chains → faster O(n) linear search
  /// - **Deduplication**: Eliminates historical values, keeps current
  /// - **Preparation**: Optimizes state before persistence/serialization
  ///
  /// When to use compact():
  ///
  /// 1. **Long-lived accumulated state**:
  /// ```java
  /// // State accumulates many updates over time
  /// State config = loadBaseConfig();
  /// for (ConfigUpdate update : updates) {
  ///   config = config.state(update.name(), update.value());
  /// }
  /// // Compact before storing
  /// State optimized = config.compact();  // Remove duplicate keys
  /// persist(optimized);
  /// ```
  ///
  /// 2. **Metrics aggregation**:
  /// ```java
  /// // Accumulate metric updates
  /// State metrics = cortex.state();
  /// for (Metric m : stream) {
  ///   metrics = metrics.state(m.name(), m.value());
  /// }
  /// // Compact before reporting (latest values only)
  /// report(metrics.compact());
  /// ```
  ///
  /// 3. **Configuration merge**:
  /// ```java
  /// State defaults = loadDefaults();  // 50 entries
  /// State overrides = loadOverrides(); // 10 entries (some overlap)
  /// State merged = defaults.merge(overrides);  // 60 entries (duplicates!)
  /// State final = merged.compact();   // ~55 entries (duplicates removed)
  /// ```
  ///
  /// Important: Compact loses historical values:
  /// ```
  /// State history = cortex.state()
  ///   .state(name("counter"), 1)
  ///   .state(name("counter"), 2)
  ///   .state(name("counter"), 3);
  ///
  /// history.values(counterSlot);        // [3, 2, 1] (all history)
  /// history.compact().values(counterSlot); // [3] (only latest)
  /// ```
  ///
  /// Type-specific compaction:
  /// - Duplicate detection uses (Name, Type) as key
  /// - Same name with different types NOT considered duplicates
  /// - See testCompactWithTypedDuplicates for details
  ///
  /// Performance characteristics:
  /// - **Time**: O(n) where n = state chain length
  /// - **Space**: O(u) where u = unique (name, type) pairs
  /// - **Benefit**: u << n when many duplicates exist
  ///
  /// Critical behaviors verified:
  /// - Original state has 5 slots (alpha, beta, gamma, beta, alpha)
  /// - Compacted state has 3 slots (unique name+type pairs)
  /// - Most recent values retained (alpha=10, beta=20, gamma=3)
  /// - Older values discarded (alpha=1, beta=2 not present)
  ///
  /// Expected: compact() returns 3 unique slots with latest values
  @Test
  void testCompactRemovesDuplicatesAndRetainsMostRecent () {

    final var alpha = cortex.name ( "state.order.alpha" );
    final var beta = cortex.name ( "state.order.beta" );
    final var gamma = cortex.name ( "state.order.gamma" );

    final var state = cortex.state ()
      .state ( alpha, 1 )
      .state ( beta, 2 )
      .state ( gamma, 3 )
      .state ( beta, 20 )
      .state ( alpha, 10 );

    final var compact = state.compact ();

    // Verify correct number of unique slots
    assertEquals ( 3, compact.stream ().count () );

    // Verify most recent values are retained (order is undefined)
    assertEquals ( 10, compact.value ( cortex.slot ( alpha, 0 ) ) );
    assertEquals ( 20, compact.value ( cortex.slot ( beta, 0 ) ) );
    assertEquals ( 3, compact.value ( cortex.slot ( gamma, 0 ) ) );

  }

  @Test
  void testCompactRetainsLatestValues () {

    final var alpha = cortex.name ( "state.compact.alpha" );
    final var beta = cortex.name ( "state.compact.beta" );
    final var gamma = cortex.name ( "state.compact.gamma" );

    final var state = cortex.state ()
      .state ( alpha, 1 )
      .state ( beta, 10 )
      .state ( alpha, 2 )
      .state ( gamma, 3 );

    final var compact = state.compact ();

    // Verify correct number of unique slots
    assertEquals ( 3, compact.stream ().count () );

    // Verify most recent values are retained (order is undefined)
    assertEquals ( 2, compact.value ( cortex.slot ( alpha, 0 ) ) );
    assertEquals ( 10, compact.value ( cortex.slot ( beta, 0 ) ) );
    assertEquals ( 3, compact.value ( cortex.slot ( gamma, 0 ) ) );

  }

  @Test
  void testCompactWithMultipleDuplicates () {

    final var counter = cortex.name ( "state.compact.counter" );

    final var state = cortex.state ()
      .state ( counter, 1 )
      .state ( counter, 2 )
      .state ( counter, 3 )
      .state ( counter, 4 )
      .state ( counter, 5 );

    final var compact = state.compact ();

    assertEquals ( 1, compact.stream ().count () );
    assertEquals ( 5, compact.value ( cortex.slot ( counter, 0 ) ) );

  }

  @Test
  void testCompactWithTypedDuplicates () {

    final var value = cortex.name ( "state.typed.value" );

    final var state = cortex.state ()
      .state ( value, 1 )
      .state ( value, 2 )
      .state ( value, 1.0f )
      .state ( value, 2.0f );

    final var compact = state.compact ();

    assertEquals ( 2, compact.stream ().count () );
    assertEquals ( 2, compact.value ( cortex.slot ( value, 0 ) ) );
    assertEquals ( 2.0f, compact.value ( cortex.slot ( value, 0f ) ), 0.001f );

  }

  @Test
  void testCortexStateFactoryMethods () {

    final var testName = cortex.name ( "state.factory.test" );

    assertEquals ( 10, cortex.state ().state ( testName, 10 ).value ( cortex.slot ( testName, 0 ) ) );
    assertEquals ( 20L, cortex.state ().state ( testName, 20L ).value ( cortex.slot ( testName, 0L ) ) );
    assertEquals ( 1.5f, cortex.state ().state ( testName, 1.5f ).value ( cortex.slot ( testName, 0f ) ), 0.001f );
    assertEquals ( 2.5, cortex.state ().state ( testName, 2.5 ).value ( cortex.slot ( testName, 0.0 ) ), 0.001 );
    assertTrue ( cortex.state ().state ( testName, true ).value ( cortex.slot ( testName, false ) ) );
    assertEquals ( "test", cortex.state ().state ( testName, "test" ).value ( cortex.slot ( testName, "" ) ) );

  }

  @Test
  void testEmptyCompactIsIdempotent () {

    final var empty = cortex.state ();
    final var compact = empty.compact ();

    assertTrue ( compact.stream ().toList ().isEmpty () );

  }

  @Test
  void testEmptyStateHasNoSlots () {

    final var empty = cortex.state ();

    assertTrue ( empty.stream ().toList ().isEmpty () );
    assertFalse ( empty.iterator ().hasNext () );

  }

  @Test
  void testForEachTraversesSlotsInOrder () {

    final var alpha = cortex.name ( "state.foreach.alpha" );
    final var beta = cortex.name ( "state.foreach.beta" );
    final var gamma = cortex.name ( "state.foreach.gamma" );

    final var state = cortex.state ()
      .state ( alpha, 1 )
      .state ( beta, 2 )
      .state ( gamma, 3 );

    final List < String > names = new java.util.ArrayList <> ();

    state.forEach ( slot -> names.add ( slot.name ().path ().toString () ) );

    assertEquals (
      List.of (
        "state.foreach.gamma",
        "state.foreach.beta",
        "state.foreach.alpha"
      ),
      names
    );

  }

  @Test
  void testIteratorConsistency () {

    final var first = cortex.name ( "state.iter.first" );
    final var second = cortex.name ( "state.iter.second" );

    final var state = cortex.state ()
      .state ( first, 1 )
      .state ( second, 2 );

    final var itr = new java.util.ArrayList < String > ();
    state.iterator ().forEachRemaining ( slot ->
      itr.add ( slot.name ().path ().toString () )
    );

    final var streamList = state.stream ()
      .map ( slot -> slot.name ().path ().toString () )
      .toList ();

    assertEquals ( streamList, itr );

  }

  @Test
  void testSlotProperties () {

    final var slotName = cortex.name ( "state.slot.test" );
    final Slot < Integer > slot = cortex.slot ( slotName, 42 );

    assertEquals ( slotName, slot.name () );
    assertEquals ( int.class, slot.type () );
    assertEquals ( 42, slot.value () );

  }

  @Test
  void testSpliteratorTraversalMatchesStream () {

    final var alpha = cortex.name ( "state.spliterator.alpha" );
    final var beta = cortex.name ( "state.spliterator.beta" );
    final var gamma = cortex.name ( "state.spliterator.gamma" );

    final var state = cortex.state ()
      .state ( alpha, 1 )
      .state ( beta, 2 )
      .state ( gamma, 3 );

    final var viaStream = state.stream ()
      .map ( slot -> slot.name ().path ().toString () )
      .toList ();

    final var viaSpliterator = java.util.stream.StreamSupport.stream (
        state.spliterator (),
        false
      ).map ( slot -> slot.name ().path ().toString () )
      .toList ();

    assertEquals ( viaStream, viaSpliterator );
    assertEquals ( 3, state.spliterator ().getExactSizeIfKnown () );

  }

  @Test
  void testStateDifferentTypesSameName () {

    final var counter = cortex.name ( "state.typed.counter" );

    final var state = cortex.state ()
      .state ( counter, 10 )
      .state ( counter, 20L )
      .state ( counter, 30.0f );

    assertEquals ( 10, state.value ( cortex.slot ( counter, 0 ) ) );
    assertEquals ( 20L, state.value ( cortex.slot ( counter, 0L ) ) );
    assertEquals ( 30.0f, state.value ( cortex.slot ( counter, 0f ) ), 0.001f );

    assertEquals ( 3, state.stream ().count () );

  }

  @Test
  void testStateFactoryMethodsForReferenceTypes () {

    final var nameKey = cortex.name ( "state.factory.name" );
    final var stateKey = cortex.name ( "state.factory.state" );

    final var storedName = cortex.name ( "state.factory.stored" );
    final var storedState = cortex.state ().state ( cortex.name ( "state.factory.nested" ), 123 );

    final var nameState = cortex.state ().state ( nameKey, storedName );
    final var nestedState = cortex.state ().state ( stateKey, storedState );

    assertEquals (
      storedName,
      nameState.value ( cortex.slot ( nameKey, cortex.name ( "fallback" ) ) )
    );

    assertEquals (
      storedState,
      nestedState.value ( cortex.slot ( stateKey, cortex.state () ) )
    );

  }

  /// Validates idempotent update optimization: adding identical slot returns same state instance.
  ///
  /// This test demonstrates a critical performance optimization: when state.state(slot)
  /// is called with a slot identical to the head slot (same name, type, AND value),
  /// State returns the SAME instance rather than creating a new linked-list node.
  /// This optimization prevents unnecessary allocations and maintains reference equality
  /// for truly unchanged states.
  ///
  /// Test Scenario:
  /// ```
  /// withValue = state().state(counter, 5)   // [counter=5]
  /// unchanged = withValue.state(counter, 5) // Same slot!
  /// updated   = withValue.state(counter, 6) // Different value
  ///
  /// unchanged === withValue  (same reference!)
  /// updated !== withValue    (different reference)
  /// ```
  ///
  /// Idempotency Check Logic:
  /// ```
  /// State state(Name name, T value) {
  ///   Slot<T> newSlot = slot(name, value);
  ///
  ///   // Check if head slot matches exactly
  ///   if (head != null &&
  ///       head.name().equals(name) &&
  ///       head.type().equals(value.getClass()) &&
  ///       head.value().equals(value)) {
  ///     return this;  // Idempotent: return existing state
  ///   }
  ///
  ///   // Non-idempotent: prepend new node
  ///   return new State(newSlot, this);
  /// }
  /// ```
  ///
  /// Why idempotency optimization matters:
  /// - **Memory efficiency**: Avoids allocating duplicate identical nodes
  /// - **Reference equality**: Unchanged states maintain === identity
  /// - **GC pressure**: Fewer allocations → less garbage collection
  /// - **Comparison shortcuts**: Can use == instead of deep equality
  ///
  /// Real-world benefits:
  ///
  /// 1. **Reactive updates with unchanged values**:
  /// ```java
  /// State currentConfig = loadConfig();
  ///
  /// // Periodic refresh that may not change anything
  /// while (running) {
  ///   State newConfig = loadConfig();
  ///   if (newConfig == currentConfig) {  // Fast reference check!
  ///     // No change, skip expensive reconfiguration
  ///     continue;
  ///   }
  ///   reconfigure(newConfig);
  ///   currentConfig = newConfig;
  /// }
  /// ```
  ///
  /// 2. **Conditional state updates**:
  /// ```java
  /// State updateIfChanged(State state, Name key, int newValue) {
  ///   State updated = state.state(key, newValue);
  ///   if (updated == state) {
  ///     // Value unchanged, no downstream notification needed
  ///     return state;
  ///   }
  ///   // Value changed, trigger observers
  ///   notifyObservers(updated);
  ///   return updated;
  /// }
  /// ```
  ///
  /// 3. **Merge operations**:
  /// ```java
  /// State mergeIfNeeded(State base, State overlay) {
  ///   State merged = base;
  ///   for (Slot slot : overlay) {
  ///     merged = merged.state(slot);
  ///     // Idempotent slots don't create new nodes
  ///   }
  ///   return merged;  // May be === base if overlay was redundant
  /// }
  /// ```
  ///
  /// Optimization constraints:
  /// - **Only checks head**: Idempotency optimization only applies to most recent slot
  /// - **Requires equality**: Value must be .equals() identical
  /// - **Type-specific**: Must match name, type, AND value
  ///
  /// Example showing head-only optimization:
  /// ```
  /// state = empty.state(counter, 5).state(flag, true);
  /// // state = [flag=true] → [counter=5]
  ///
  /// result = state.state(counter, 5);  // NOT idempotent!
  /// // Head is (flag=true), not (counter=5)
  /// // Creates: [counter=5] → [flag=true] → [counter=5]
  /// ```
  ///
  /// Performance implications:
  /// - **Best case**: O(1) idempotent check, no allocation
  /// - **Worst case**: O(1) node creation (same as without optimization)
  /// - **Common case**: Saves allocations in update-heavy scenarios
  ///
  /// Critical behaviors verified:
  /// - state.state(counter, 5) creates new state
  /// - withValue.state(counter, 5) returns withValue (idempotent!)
  /// - withValue.state(counter, 6) creates new state (different value)
  /// - Reference equality (==) confirms optimization
  ///
  /// Contrast with non-optimized approach:
  /// ```
  /// // Without optimization (always creates new node):
  /// state.state(counter, 5).state(counter, 5).state(counter, 5)
  /// // Creates: [counter=5] → [counter=5] → [counter=5] → ...
  /// // Memory waste!
  ///
  /// // With optimization (idempotent):
  /// state.state(counter, 5).state(counter, 5).state(counter, 5)
  /// // Returns: same instance (no duplicates)
  /// ```
  ///
  /// Expected: unchanged === withValue (reference equality), updated !== withValue
  @Test
  void testStateIdempotentUpdateWithSameValue () {

    final var counter = cortex.name ( "state.idempotent.counter" );

    final var base = cortex.state ();
    final var withValue = base.state ( counter, 5 );
    final var unchanged = withValue.state ( counter, 5 );
    final var updated = withValue.state ( counter, 6 );

    assertSame ( withValue, unchanged );
    assertNotSame ( withValue, updated );
    assertEquals ( 6, updated.value ( cortex.slot ( counter, 0 ) ) );

  }

  @Test
  void testStateIteratorThrowsWhenExhausted () {

    final var metric = cortex.name ( "state.base.iterator" );

    final var state = cortex.state ()
      .state ( metric, 10 );

    final var iterator = state.iterator ();

    assertTrue ( iterator.hasNext () );
    assertEquals ( metric, iterator.next ().name () );
    assertFalse ( iterator.hasNext () );
    assertThrows ( NoSuchElementException.class, iterator::next );

  }

  @SuppressWarnings ( "DataFlowIssue" )
  @Test
  void testStateNullGuards () {

    final var name = cortex.name ( "state.null.guard" );

    assertThrows (
      NullPointerException.class,
      () -> cortex.state ().state ( null, 1 )
    );

    assertThrows (
      NullPointerException.class,
      () -> cortex.state ().state ( name, (String) null )
    );

    assertThrows (
      NullPointerException.class,
      () -> cortex.state ().state ( null, cortex.state () )
    );

    assertThrows (
      NullPointerException.class,
      () -> cortex.state ().state (
        name,
        (Name) null
      )
    );

    assertThrows (
      NullPointerException.class,
      () -> cortex.state ().state (
        name,
        (State) null
      )
    );

  }

  @Test
  void testStateStreamMostRecentFirst () {

    final var first = cortex.name ( "state.order.first" );
    final var second = cortex.name ( "state.order.second" );

    final var state = cortex.state ()
      .state ( first, 1 )
      .state ( second, 2 );

    final var order = state.stream ()
      .map ( slot -> slot.name ().path ().toString () )
      .toList ();

    assertEquals (
      List.of ( "state.order.second", "state.order.first" ),
      order
    );

  }

  @Test
  void testStateWithAllPrimitiveTypes () {

    final var intName = cortex.name ( "state.primitives.int" );
    final var longName = cortex.name ( "state.primitives.long" );
    final var floatName = cortex.name ( "state.primitives.float" );
    final var doubleName = cortex.name ( "state.primitives.double" );
    final var boolName = cortex.name ( "state.primitives.bool" );

    final var state = cortex.state ()
      .state ( intName, 42 )
      .state ( longName, 123456789L )
      .state ( floatName, 3.14f )
      .state ( doubleName, 2.718281828 )
      .state ( boolName, true );

    assertEquals ( 42, state.value ( cortex.slot ( intName, 0 ) ) );
    assertEquals ( 123456789L, state.value ( cortex.slot ( longName, 0L ) ) );
    assertEquals ( 3.14f, state.value ( cortex.slot ( floatName, 0f ) ), 0.001f );
    assertEquals ( 2.718281828, state.value ( cortex.slot ( doubleName, 0.0 ) ), 0.00001 );
    assertTrue ( state.value ( cortex.slot ( boolName, false ) ) );

  }

  @Test
  void testStateWithDirectEnumAddsNameSlot () {

    final var state = cortex.state ()
      .state ( TestMode.DEBUG );

    final var enumName = cortex.name ( TestMode.DEBUG.getDeclaringClass () );
    final var slot = cortex.slot ( enumName, cortex.name ( "fallback" ) );

    assertEquals ( cortex.name ( TestMode.DEBUG ), state.value ( slot ) );

  }

  @Test
  void testStateWithDirectEnumChaining () {

    final var state = cortex.state ()
      .state ( TestMode.DEBUG )
      .state ( Level.MEDIUM )
      .state ( TestMode.RELEASE );

    assertEquals ( 3, state.stream ().count () );

  }

  @Test
  void testStateWithDirectEnumIdempotency () {

    final var state1 = cortex.state ()
      .state ( TestMode.PRODUCTION );

    final var state2 = state1.state ( TestMode.PRODUCTION );

    assertSame ( state1, state2 );

  }

  @Test
  void testStateWithDirectEnumInCompact () {

    final var state = cortex.state ()
      .state ( TestMode.DEBUG )
      .state ( Level.MEDIUM )
      .state ( TestMode.DEBUG );

    final var compact = state.compact ();

    assertEquals ( 2, compact.stream ().count () );

  }

  @Test
  void testStateWithDirectEnumMultipleValues () {

    final var state = cortex.state ()
      .state ( TestMode.DEBUG )
      .state ( Level.LOW )
      .state ( TestMode.RELEASE );

    final var modeSlot = cortex.slot ( cortex.name ( TestMode.class ), cortex.name ( "fallback" ) );
    final var levelSlot = cortex.slot ( cortex.name ( Level.class ), cortex.name ( "fallback" ) );

    assertEquals ( cortex.name ( TestMode.RELEASE ), state.value ( modeSlot ) );
    assertEquals ( cortex.name ( Level.LOW ), state.value ( levelSlot ) );

  }

  @Test
  void testStateWithDirectEnumNameDerivation () {

    final var state = cortex.state ()
      .state ( Level.HIGH );

    final var expectedName = cortex.name ( Level.class );

    assertTrue (
      state.stream ()
        .anyMatch ( s ->
          s.name ().equals ( expectedName ) &&
            s.value ().equals ( cortex.name ( Level.HIGH ) ) &&
            s.type ().equals ( Name.class )
        )
    );

  }

  @SuppressWarnings ( "DataFlowIssue" )
  @Test
  void testStateWithDirectEnumNullGuard () {

    assertThrows (
      NullPointerException.class,
      () -> cortex.state ().state ( (Enum < ? >) null )
    );

  }

  @Test
  void testStateWithDirectEnumOverride () {

    final var enumName = cortex.name ( Level.LOW.getDeclaringClass () );

    final var state = cortex.state ()
      .state ( Level.LOW )
      .state ( enumName, cortex.name ( "CUSTOM_LOW" ) );

    final var slot = cortex.slot ( enumName, cortex.name ( "fallback" ) );

    assertEquals ( cortex.name ( "CUSTOM_LOW" ), state.value ( slot ) );

  }

  @Test
  void testStateWithDirectEnumTypeSafety () {

    final var state = cortex.state ()
      .state ( Level.MEDIUM );

    final var slot = state.stream ()
      .findFirst ()
      .orElseThrow ();

    assertEquals ( Name.class, slot.type () );
    assertFalse ( slot.type ().isPrimitive () );

  }

  @Test
  void testStateWithDirectEnumValueRetrieval () {

    final var state = cortex.state ()
      .state ( Level.HIGH );

    final var template = cortex.slot ( cortex.name ( Level.class ), cortex.name ( "fallback" ) );

    assertEquals ( cortex.name ( Level.HIGH ), state.value ( template ) );

  }

  @Test
  void testStateWithEnumSlot () {

    final var enumSlot = cortex.slot ( TestMode.DEBUG );
    final var state = cortex.state ()
      .state ( enumSlot );

    assertEquals ( cortex.name ( TestMode.DEBUG ), state.value ( enumSlot ) );

  }

  @Test
  void testStateWithEnumSlotChaining () {

    final var state = cortex.state ()
      .state ( cortex.slot ( TestMode.DEBUG ) )
      .state ( cortex.slot ( Level.MEDIUM ) )
      .state ( cortex.slot ( TestMode.RELEASE ) );

    assertEquals ( 3, state.stream ().count () );

  }

  @Test
  void testStateWithEnumSlotCreatesCorrectSlot () {

    final var enumSlot = cortex.slot ( TestMode.RELEASE );
    final var state = cortex.state ()
      .state ( enumSlot );

    final var expectedName = cortex.name ( TestMode.RELEASE.getDeclaringClass () );

    assertTrue (
      state.stream ()
        .anyMatch ( slot ->
          slot.name ().equals ( expectedName ) &&
            slot.value ().equals ( cortex.name ( TestMode.RELEASE ) )
        )
    );

  }

  @Test
  void testStateWithEnumSlotIdempotency () {

    final var enumSlot = cortex.slot ( TestMode.DEBUG );

    final var state1 = cortex.state ()
      .state ( enumSlot );

    final var state2 = state1.state ( enumSlot );

    assertSame ( state1, state2 );

  }

  @Test
  void testStateWithEnumSlotInCompact () {

    final var debugSlot = cortex.slot ( TestMode.DEBUG );
    final var releaseSlot = cortex.slot ( TestMode.RELEASE );
    final var levelSlot = cortex.slot ( Level.LOW );

    final var state = cortex.state ()
      .state ( debugSlot )
      .state ( levelSlot )
      .state ( releaseSlot )
      .state ( debugSlot );

    final var compact = state.compact ();

    // DEBUG and RELEASE share same name (TestMode.class), so compact keeps only the most recent (DEBUG)
    // Level.LOW has different name (Level.class), so it's kept
    assertEquals ( 2, compact.stream ().count () );

  }

  @Test
  void testStateWithEnumSlotOverride () {

    final var enumSlot = cortex.slot ( TestMode.DEBUG );

    final var state = cortex.state ()
      .state ( enumSlot )
      .state ( cortex.name ( TestMode.DEBUG.getDeclaringClass () ), cortex.name ( "CUSTOM" ) );

    assertEquals ( cortex.name ( "CUSTOM" ), state.value ( enumSlot ) );

  }

  @Test
  void testStateWithEnumSlotTypeIsName () {

    final var enumSlot = cortex.slot ( Level.MEDIUM );

    final var state = cortex.state ()
      .state ( enumSlot );

    final var slot = state.stream ()
      .findFirst ()
      .orElseThrow ();

    assertEquals ( Name.class, slot.type () );

  }

  @Test
  void testStateWithEnumSlotValueRetrieval () {

    final var template = cortex.slot ( Level.LOW );

    final var state = cortex.state ()
      .state ( template );

    assertEquals ( cortex.name ( Level.LOW ), state.value ( template ) );

  }

  @Test
  void testStateWithMultipleEnumSlots () {

    final var modeSlot = cortex.slot ( TestMode.PRODUCTION );
    final var levelSlot = cortex.slot ( Level.HIGH );

    final var state = cortex.state ()
      .state ( modeSlot )
      .state ( levelSlot );

    assertEquals ( cortex.name ( TestMode.PRODUCTION ), state.value ( modeSlot ) );
    assertEquals ( cortex.name ( Level.HIGH ), state.value ( levelSlot ) );

  }

  @Test
  void testStateWithReferenceTypes () {

    final var stringName = cortex.name ( "state.ref.string" );
    final var nameName = cortex.name ( "state.ref.name" );
    final var stateName = cortex.name ( "state.ref.state" );

    final var nestedName = cortex.name ( "nested.value" );
    final var nestedState = cortex.state ().state ( nestedName, 99 );

    final var state = cortex.state ()
      .state ( stringName, "hello" )
      .state ( nameName, nestedName )
      .state ( stateName, nestedState );

    assertEquals ( "hello", state.value ( cortex.slot ( stringName, "" ) ) );
    assertEquals ( nestedName, state.value ( cortex.slot ( nameName, cortex.name ( "default" ) ) ) );
    assertEquals ( nestedState, state.value ( cortex.slot ( stateName, cortex.state () ) ) );

  }

  @Test
  void testStateWithSlotCompact () {

    final var key = cortex.name ( "state.slot.compact" );

    final var slot1 = cortex.slot ( key, 1 );
    final var slot2 = cortex.slot ( key, 2 );
    final var slot3 = cortex.slot ( key, 3 );

    final var state = cortex.state ()
      .state ( slot1 )
      .state ( slot2 )
      .state ( slot3 )
      .state ( slot1 );

    final var compacted = state.compact ();

    final var values = compacted.values ( cortex.slot ( key, 0 ) ).collect ( toList () );
    assertEquals ( List.of ( 1 ), values, "Compact should retain only the most recent occurrence" );

  }

  @Test
  void testStateWithSlotIdempotence () {

    final var counter = cortex.name ( "state.slot.idempotent" );
    final var slot = cortex.slot ( counter, 100 );

    final var state = cortex.state ().state ( slot );
    final var unchanged = state.state ( slot );

    assertSame ( state, unchanged, "Adding same slot should return same state instance" );

  }

  @Test
  void testStateWithSlotMixedTypes () {

    final var name = cortex.name ( "state.slot.mixed" );

    final var intSlot = cortex.slot ( name, 42 );
    final var stringSlot = cortex.slot ( name, "test" );

    final var state = cortex.state ()
      .state ( intSlot )
      .state ( stringSlot );

    // Different types on same name should coexist
    assertEquals ( 42, state.value ( cortex.slot ( name, 0 ) ) );
    assertEquals ( "test", state.value ( cortex.slot ( name, "" ) ) );

  }

  @SuppressWarnings ( "DataFlowIssue" )
  @Test
  void testStateWithSlotNullGuard () {

    assertThrows (
      NullPointerException.class,
      () -> cortex.state ().state ( (Slot < ? >) null )
    );

  }

  @Test
  void testStateWithSlotOverride () {

    final var key = cortex.name ( "state.slot.override" );

    final var slot1 = cortex.slot ( key, 10 );
    final var slot2 = cortex.slot ( key, 20 );

    final var state = cortex.state ()
      .state ( slot1 )
      .state ( slot2 );

    assertEquals ( 20, state.value ( cortex.slot ( key, 0 ) ), "Most recent slot value should be used" );

    final var values = state.values ( cortex.slot ( key, 0 ) ).collect ( toList () );
    assertEquals ( List.of ( 20, 10 ), values, "Should contain both values in reverse chronological order" );

  }

  @Test
  void testStateWithSlotParameter () {

    final var name = cortex.name ( "state.slot.name" );
    final var age = cortex.name ( "state.slot.age" );

    final var nameSlot = cortex.slot ( name, "Alice" );
    final var ageSlot = cortex.slot ( age, 30 );

    final var state = cortex.state ()
      .state ( nameSlot )
      .state ( ageSlot );

    assertEquals ( "Alice", state.value ( cortex.slot ( name, "" ) ) );
    assertEquals ( 30, state.value ( cortex.slot ( age, 0 ) ) );

  }

  /// Validates immutability through structural sharing: base state unaffected by derivation.
  ///
  /// This test demonstrates State's fundamental immutability guarantee: when a new
  /// state is derived from an existing state by adding a slot, the original state
  /// remains completely unchanged. State achieves this through STRUCTURAL SHARING:
  /// derived states prepend new slots to existing chains without copying, creating
  /// an efficient persistent data structure.
  ///
  /// Test Scenario:
  /// ```
  /// base = state().state(alpha, 1)        // [alpha=1]
  /// derived = base.state(beta, 2)         // [beta=2, alpha=1]
  ///
  /// base unchanged:    [alpha=1]          (still has 1 slot)
  /// derived extended:  [beta=2, alpha=1] (has 2 slots)
  /// ```
  ///
  /// Structural Sharing Implementation:
  /// ```
  /// State is an immutable linked list:
  ///
  /// base state:
  ///   [alpha=1] → null
  ///
  /// derived state (shares structure):
  ///   [beta=2] → [alpha=1] → null
  ///              ^
  ///              └─ Shares base's node (no copy!)
  ///
  /// Adding to base does NOT affect derived:
  ///   base2 = base.state(gamma, 3)
  ///   [gamma=3] → [alpha=1] → null
  ///
  /// All three states coexist:
  ///   base:     [alpha=1]
  ///   derived:  [beta=2] → [alpha=1]
  ///   base2:    [gamma=3] → [alpha=1]
  /// ```
  ///
  /// Why structural sharing matters:
  /// - **True immutability**: Original state never changes (safe to share)
  /// - **Memory efficiency**: No copying of entire state (O(1) space per addition)
  /// - **Performance**: O(1) time to add slot (no iteration/copying)
  /// - **Versioning**: Multiple versions coexist efficiently (like Git commits)
  ///
  /// Real-world implications:
  ///
  /// 1. **Configuration inheritance**:
  /// ```java
  /// // Base production config
  /// State prodConfig = cortex.state()
  ///   .state(name("server.port"), 8080)
  ///   .state(name("db.host"), "prod.db");
  ///
  /// // Override for testing (prod config unchanged)
  /// State testConfig = prodConfig
  ///   .state(name("db.host"), "test.db");
  ///
  /// // Both coexist independently
  /// prodConfig.value(dbHost); // "prod.db"
  /// testConfig.value(dbHost); // "test.db"
  /// ```
  ///
  /// 2. **Request context layering**:
  /// ```java
  /// // Base request context
  /// State requestContext = cortex.state()
  ///   .state(name("user.id"), userId);
  ///
  /// // Add trace info (request context unchanged)
  /// State withTrace = requestContext
  ///   .state(name("trace.id"), traceId);
  ///
  /// // Fork for async work (both independent)
  /// State async1 = withTrace.state(name("worker"), "1");
  /// State async2 = withTrace.state(name("worker"), "2");
  /// ```
  ///
  /// 3. **Temporal state snapshots**:
  /// ```java
  /// State t0 = cortex.state().state(name("counter"), 0);
  /// State t1 = t0.state(name("counter"), 1);
  /// State t2 = t1.state(name("counter"), 2);
  ///
  /// // All versions accessible (time-travel)
  /// t0.value(counter); // 0
  /// t1.value(counter); // 1
  /// t2.value(counter); // 2
  /// ```
  ///
  /// Performance characteristics:
  /// - **Add slot**: O(1) time, O(1) space (prepend to linked list)
  /// - **Lookup**: O(n) time where n = chain length (linear search)
  /// - **Memory**: Shared nodes → minimal duplication
  /// - **GC-friendly**: Old unreferenced states collected naturally
  ///
  /// Critical behaviors verified:
  /// - Base state has 1 slot (alpha=1)
  /// - Derived state has 2 slots (beta=2, alpha=1)
  /// - Base state unchanged after derivation (still 1 slot)
  /// - Values independent (base doesn't see beta)
  /// - No defensive copying (efficient)
  ///
  /// Contrast with mutable approach:
  /// ```
  /// // BAD: Mutable state (unsafe to share)
  /// MutableState state = new MutableState();
  /// state.put("alpha", 1);
  ///
  /// MutableState shared = state;  // Same reference!
  /// shared.put("beta", 2);        // Modifies original!
  ///
  /// state.get("beta");  // 2 (unexpected side effect!)
  /// ```
  ///
  /// Expected: base=`[alpha=1]`, derived=`[beta=2, alpha=1]` (independent states)
  @Test
  void testStructuralSharingOriginalStateUnaffected () {

    final var alpha = cortex.name ( "state.share.alpha" );
    final var beta = cortex.name ( "state.share.beta" );

    final var base = cortex.state ()
      .state ( alpha, 1 );

    final var derived = base.state ( beta, 2 );

    assertEquals ( 1, base.value ( cortex.slot ( alpha, 0 ) ) );
    assertEquals ( 1, base.stream ().count () );

    assertEquals ( 2, derived.stream ().count () );
    assertEquals ( 2, derived.value ( cortex.slot ( beta, 0 ) ) );

  }

  @Test
  void testValuesFallbackWhenNoMatch () {

    final var empty = cortex.state ();
    final var missing = cortex.name ( "state.missing.key" );

    assertEquals ( 999, empty.value ( cortex.slot ( missing, 999 ) ) );
    assertEquals ( "default", empty.value ( cortex.slot ( missing, "default" ) ) );

  }

  @Test
  void testValuesIteratorThrowsWhenExhausted () {

    final var metric = cortex.name ( "state.iterator.metric" );

    final var state = cortex.state ()
      .state ( metric, 1 )
      .state ( metric, 2 );

    final var iterator = state.values ( cortex.slot ( metric, 0 ) )
      .iterator ();

    assertTrue ( iterator.hasNext () );
    assertEquals ( 2, iterator.next () );
    assertTrue ( iterator.hasNext () );
    assertEquals ( 1, iterator.next () );
    assertFalse ( iterator.hasNext () );
    assertThrows ( NoSuchElementException.class, iterator::next );

  }

  @Test
  void testValuesStreamOrderingAndFallbacks () {

    final var counter = cortex.name ( "state.values.counter" );

    final var state = cortex.state ()
      .state ( counter, 1 )
      .state ( counter, 2 )
      .state ( counter, 3 );

    final Slot < Integer > counterSlot = cortex.slot ( counter, 0 );
    final Slot < Integer > missingSlot = cortex.slot (
      cortex.name ( "state.values.missing" ),
      42
    );

    assertEquals ( List.of ( 3, 2, 1 ), state.values ( counterSlot ).collect ( toList () ) );
    assertEquals ( 3, state.value ( counterSlot ) );
    assertEquals ( 42, state.value ( missingSlot ) );
    assertTrue ( state.values ( missingSlot ).findAny ().isEmpty () );

  }

  @Test
  void testValuesStreamWithReferenceSlots () {

    final var nameSlot = cortex.name ( "state.values.name" );
    final var stateSlot = cortex.name ( "state.values.state" );

    final var firstName = cortex.name ( "state.values.name.first" );
    final var secondName = cortex.name ( "state.values.name.second" );

    final var nestedA = cortex.state ().state ( cortex.name ( "nested.a" ), 1 );
    final var nestedB = cortex.state ().state ( cortex.name ( "nested.b" ), 2 );

    final var state = cortex.state ()
      .state ( nameSlot, firstName )
      .state ( nameSlot, secondName )
      .state ( stateSlot, nestedA )
      .state ( stateSlot, nestedB );

    assertEquals (
      List.of ( secondName, firstName ),
      state.values ( cortex.slot ( nameSlot, cortex.name ( "default" ) ) ).collect ( toList () )
    );

    assertEquals (
      List.of ( nestedB, nestedA ),
      state.values ( cortex.slot ( stateSlot, cortex.state () ) ).collect ( toList () )
    );

  }

  @Test
  void testValuesWithMultipleMatchingSlots () {

    final var metric = cortex.name ( "state.metrics.value" );

    final var state = cortex.state ()
      .state ( metric, 100 )
      .state ( metric, 200 )
      .state ( metric, 300 );

    final var values = state.values ( cortex.slot ( metric, 0 ) )
      .collect ( toList () );

    assertEquals ( List.of ( 300, 200, 100 ), values );

  }

  enum TestMode {
    DEBUG,
    RELEASE,
    PRODUCTION
  }

  enum Level {
    LOW,
    MEDIUM,
    HIGH
  }

}
