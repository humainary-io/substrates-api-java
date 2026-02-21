// Copyright (c) 2025 William David Louth

package io.humainary.substrates.tck;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/// Comprehensive tests for the Slot interface.
///
/// This test class covers:
/// - Slot creation with various types (boolean, int, long, double, Object)
/// - Slot identity and name preservation
/// - Type-specific slot behavior
/// - Slot equality and value semantics
///
/// Slots are immutable named values with type information, used for
/// configuration and metadata throughout the substrate framework.
///
/// @author William David Louth
/// @since 1.0
final class SlotTest
  extends TestSupport {

  private Cortex cortex;

  @BeforeEach
  void setup () {

    cortex = cortex ();

  }

  @Test
  void testBooleanSlotProperties () {

    final var slotName = cortex.name ( "slot.bool.test" );
    final var slot = cortex.slot ( slotName, true );

    assertEquals ( slotName, slot.name () );
    assertEquals ( boolean.class, slot.type () );
    assertTrue ( slot.value () );

  }

  @Test
  void testDifferentSlotsSameName () {

    final var sharedName = cortex.name ( "slot.shared.name" );

    final var intSlot = cortex.slot ( sharedName, 42 );
    final var stringSlot = cortex.slot ( sharedName, "test" );

    assertEquals ( sharedName, intSlot.name () );
    assertEquals ( sharedName, stringSlot.name () );

    assertEquals ( int.class, intSlot.type () );
    assertEquals ( String.class, stringSlot.type () );

    assertEquals ( 42, intSlot.value () );
    assertEquals ( "test", stringSlot.value () );

  }

  @Test
  void testDoubleSlotProperties () {

    final var slotName = cortex.name ( "slot.double.test" );
    final var slot = cortex.slot ( slotName, 2.718281828 );

    assertEquals ( slotName, slot.name () );
    assertEquals ( double.class, slot.type () );
    assertEquals ( 2.718281828, slot.value (), 0.00001 );

  }

  @Test
  void testEnumSlotAsTemplateWithFallback () {

    final var template = cortex.slot ( TestStatus.INACTIVE );
    final var emptyState = cortex.state ();

    assertEquals ( cortex.name ( TestStatus.INACTIVE ), emptyState.value ( template ) );

  }

  /// Validates enum slot override: state value takes precedence over enum default.
  ///
  /// This test demonstrates enum-based configuration with runtime overrides. Enum
  /// slots provide type-safe, self-documenting configuration keys with compile-time
  /// defaults (the enum constant's name), while allowing runtime state to override
  /// these defaults. This pattern combines enum safety with configuration flexibility.
  ///
  /// Test Scenario:
  /// ```
  /// enumSlot = slot(TestStatus.ACTIVE);  // Default: name(ACTIVE)
  ///
  /// state contains:
  ///   enumSlot.name() → name("OVERRIDDEN")  // Runtime override
  ///
  /// state.value(enumSlot) → name("OVERRIDDEN")  (state wins)
  /// ```
  ///
  /// Enum Slot Semantics:
  /// ```
  /// cortex.slot(TestStatus.ACTIVE) creates:
  ///   name: name(TestStatus.class)  // e.g., "io.humainary...TestStatus"
  ///   type: Name.class
  ///   value: name(TestStatus.ACTIVE)  // "ACTIVE"
  ///
  /// Lookup: state.value(enumSlot)
  ///   1. Search state for (name=TestStatus.class, type=Name.class)
  ///   2. If found → return state value (override)
  ///   3. If not found → return name(ACTIVE) (default)
  /// ```
  ///
  /// Why enum slots with overrides matter:
  /// - **Type-safe keys**: Enum constants as configuration keys (no typos)
  /// - **Compile-time defaults**: Enum value is sensible default
  /// - **Runtime flexibility**: Can override with different enum value
  /// - **Self-documenting**: Enum names describe configuration options
  ///
  /// Real-world enum configuration examples:
  ///
  /// 1. **Log level configuration**:
  /// ```java
  /// enum LogLevel { DEBUG, INFO, WARN, ERROR }
  ///
  /// // Compile-time default: INFO
  /// Slot<Name> logLevel = cortex.slot(LogLevel.INFO);
  ///
  /// // Runtime override to DEBUG
  /// State config = cortex.state()
  ///   .state(logLevel.name(), cortex.name(LogLevel.DEBUG));
  ///
  /// Name level = config.value(logLevel);  // name(DEBUG)
  /// ```
  ///
  /// 2. **Circuit breaker state**:
  /// ```java
  /// enum CircuitState { CLOSED, HALF_OPEN, OPEN }
  ///
  /// // Default: CLOSED (healthy)
  /// Slot<Name> circuitState = cortex.slot(CircuitState.CLOSED);
  ///
  /// // Runtime: Circuit opened due to failures
  /// State runtime = cortex.state()
  ///   .state(circuitState.name(), cortex.name(CircuitState.OPEN));
  ///
  /// Name state = runtime.value(circuitState);  // name(OPEN)
  /// ```
  ///
  /// 3. **Feature flag with enum states**:
  /// ```java
  /// enum FeatureState { DISABLED, BETA, ENABLED }
  ///
  /// // Default: DISABLED (safe)
  /// Slot<Name> cacheFeature = cortex.slot(FeatureState.DISABLED);
  ///
  /// // Runtime: Enable for beta users
  /// State features = cortex.state()
  ///   .state(cacheFeature.name(), cortex.name(FeatureState.BETA));
  ///
  /// Name featureState = features.value(cacheFeature);  // name(BETA)
  /// ```
  ///
  /// Enum slot pattern benefits:
  /// - **IDE autocomplete**: Enum constants discoverable
  /// - **Refactoring safe**: Renaming enum updates all references
  /// - **Restricted values**: Only valid enum constants (or overrides)
  /// - **Domain vocabulary**: Enums express business concepts
  ///
  /// Override mechanics:
  /// ```
  /// // Slot created from enum
  /// Slot<Name> slot = cortex.slot(TestStatus.ACTIVE);
  ///
  /// // slot.name() = name(TestStatus.class)
  /// // slot.value() = name(ACTIVE)  // Default
  ///
  /// // State overrides with different value
  /// state.state(slot.name(), name("OVERRIDDEN"));
  ///
  /// // Lookup finds override
  /// state.value(slot) → name("OVERRIDDEN")
  /// ```
  ///
  /// Critical behaviors verified:
  /// - Enum slot created with TestStatus.ACTIVE default
  /// - State contains override value ("OVERRIDDEN")
  /// - state.value(slot) returns override (not default)
  /// - State value takes precedence over slot default
  ///
  /// Use cases for overriding enum defaults:
  /// - **Testing**: Force specific states for test scenarios
  /// - **Dynamic config**: Change behavior without redeployment
  /// - **Gradual rollout**: Override default state per instance
  /// - **Emergency switches**: Override to safe/degraded state
  ///
  /// Expected: state.value(enumSlot) = name("OVERRIDDEN") (state overrides default)
  @Test
  void testEnumSlotAsTemplateWithOverride () {

    final var enumSlot = cortex.slot ( TestStatus.ACTIVE );

    final var state = cortex.state ()
      .state ( enumSlot.name (), cortex.name ( "OVERRIDDEN" ) );

    assertEquals ( cortex.name ( "OVERRIDDEN" ), state.value ( enumSlot ) );

  }

  @Test
  void testEnumSlotDifferentEnumsSameName () {

    final var status1 = cortex.slot ( TestStatus.ACTIVE );
    final var status2 = cortex.slot ( TestStatus.INACTIVE );

    assertEquals ( cortex.name ( TestStatus.ACTIVE ), status1.value () );
    assertEquals ( cortex.name ( TestStatus.INACTIVE ), status2.value () );

  }

  @Test
  void testEnumSlotInState () {

    final var statusSlot = cortex.slot ( TestStatus.ACTIVE );

    final var state = cortex.state ()
      .state ( statusSlot );

    assertEquals ( cortex.name ( TestStatus.ACTIVE ), state.value ( statusSlot ) );

  }

  @Test
  void testEnumSlotMultipleEnumsInState () {

    final var statusSlot = cortex.slot ( TestStatus.ACTIVE );
    final var prioritySlot = cortex.slot ( Priority.HIGH );

    final var state = cortex.state ()
      .state ( statusSlot )
      .state ( prioritySlot );

    assertEquals ( cortex.name ( TestStatus.ACTIVE ), state.value ( statusSlot ) );
    assertEquals ( cortex.name ( Priority.HIGH ), state.value ( prioritySlot ) );

  }

  @Test
  void testEnumSlotNameDerivedFromEnum () {

    final var slot = cortex.slot ( TestStatus.INACTIVE );
    final var expectedName = cortex.name ( TestStatus.INACTIVE.getDeclaringClass () );

    assertEquals ( expectedName, slot.name () );

  }

  @SuppressWarnings ( "DataFlowIssue" )
  @Test
  void testEnumSlotNullGuard () {

    assertThrows (
      NullPointerException.class,
      () -> cortex.slot ( null )
    );

  }

  @Test
  void testEnumSlotProperties () {

    final var slot = cortex.slot ( TestStatus.ACTIVE );

    assertNotNull ( slot.name () );
    assertEquals ( Name.class, slot.type () );
    assertEquals ( cortex.name ( TestStatus.ACTIVE ), slot.value () );

  }

  @Test
  void testEnumSlotTypeIsName () {

    final var slot = cortex.slot ( Priority.HIGH );

    assertEquals ( Name.class, slot.type () );
    assertFalse ( slot.type ().isPrimitive () );

  }

  @Test
  void testEnumSlotValueIsEnumName () {

    final var activeSlot = cortex.slot ( TestStatus.ACTIVE );
    final var pendingSlot = cortex.slot ( TestStatus.PENDING );

    assertEquals ( cortex.name ( TestStatus.ACTIVE ), activeSlot.value () );
    assertEquals ( cortex.name ( TestStatus.PENDING ), pendingSlot.value () );

  }

  @Test
  void testFloatSlotProperties () {

    final var slotName = cortex.name ( "slot.float.test" );
    final var slot = cortex.slot ( slotName, 3.14f );

    assertEquals ( slotName, slot.name () );
    assertEquals ( float.class, slot.type () );
    assertEquals ( 3.14f, slot.value (), 0.001f );

  }

  @Test
  void testIntSlotProperties () {

    final var slotName = cortex.name ( "slot.int.test" );
    final var slot = cortex.slot ( slotName, 42 );

    assertEquals ( slotName, slot.name () );
    assertEquals ( int.class, slot.type () );
    assertEquals ( 42, slot.value () );

  }

  @Test
  void testLongSlotProperties () {

    final var slotName = cortex.name ( "slot.long.test" );
    final var slot = cortex.slot ( slotName, 123456789L );

    assertEquals ( slotName, slot.name () );
    assertEquals ( long.class, slot.type () );
    assertEquals ( 123456789L, slot.value () );

  }

  @Test
  void testNameSlotProperties () {

    final var slotName = cortex.name ( "slot.name.test" );
    final var slotValue = cortex.name ( "slot.value.nested" );
    final var slot = cortex.slot ( slotName, slotValue );

    assertEquals ( slotName, slot.name () );
    assertEquals ( Name.class, slot.type () );
    assertEquals ( slotValue, slot.value () );

  }

  @Test
  void testSlotAsStateTemplate () {

    final var counterName = cortex.name ( "slot.template.counter" );
    final var template = cortex.slot ( counterName, 0 );

    final var state = cortex.state ()
      .state ( counterName, 10 )
      .state ( counterName, 20 );

    assertEquals ( 20, state.value ( template ) );

  }

  /// Validates type-specific slot matching: same name, different types in state.
  ///
  /// This test demonstrates a sophisticated feature of the slot-as-template pattern:
  /// State can hold MULTIPLE values for the SAME name, distinguished by TYPE.
  /// Slots act as typed queries, retrieving the value matching both name AND type.
  /// This enables polymorphic state storage without name collisions.
  ///
  /// Test Scenario:
  /// ```
  /// State contains:
  ///   "slot.matching.metric" → 100 (int)
  ///   "slot.matching.metric" → 200L (long)
  ///
  /// intSlot = slot("slot.matching.metric", 0)    // type: int
  /// longSlot = slot("slot.matching.metric", 0L)  // type: long
  ///
  /// state.value(intSlot)  → 100  (matches name + int type)
  /// state.value(longSlot) → 200L (matches name + long type)
  /// ```
  ///
  /// Type-Specific Lookup Mechanism:
  /// ```
  /// State lookup: (Name, Type) → Value
  ///
  /// state.value(slot) {
  ///   Name slotName = slot.name();
  ///   Class<?> slotType = slot.type();
  ///
  ///   // Search state for entry matching BOTH name and type
  ///   for (Entry entry : state.entries) {
  ///     if (entry.name().equals(slotName) &&
  ///         entry.type().equals(slotType)) {
  ///       return entry.value();
  ///     }
  ///   }
  ///   return slot.value();  // Fallback to slot's default
  /// }
  /// ```
  ///
  /// Why type-specific matching matters:
  /// - **Type safety**: Retrieve value with correct type without casting
  /// - **Polymorphism**: Same logical name, different interpretations by type
  /// - **No name conflicts**: int counter vs long counter for same metric
  /// - **Compile-time guarantees**: Slot type determines return type
  ///
  /// Real-world examples:
  ///
  /// 1. **Multi-unit metrics**:
  /// ```java
  /// // Same metric in different units
  /// Slot<Integer> bytesSlot = cortex.slot(name("size"), 0);
  /// Slot<Long> kilobytesSlot = cortex.slot(name("size"), 0L);
  ///
  /// State metrics = cortex.state()
  ///   .state(name("size"), 1024)      // bytes
  ///   .state(name("size"), 1L);       // kilobytes
  ///
  /// int bytes = metrics.value(bytesSlot);      // 1024
  /// long kb = metrics.value(kilobytesSlot);    // 1L
  /// ```
  ///
  /// 2. **Configuration with type variants**:
  /// ```java
  /// // Timeout as int (seconds) vs long (milliseconds)
  /// Slot<Integer> timeoutSec = cortex.slot(name("timeout"), 30);
  /// Slot<Long> timeoutMs = cortex.slot(name("timeout"), 30000L);
  ///
  /// State config = loadConfig();
  /// int seconds = config.value(timeoutSec);
  /// long millis = config.value(timeoutMs);
  /// ```
  ///
  /// 3. **Resolution-specific counters**:
  /// ```java
  /// // Request count at different granularities
  /// Slot<Integer> hourlyRequests = cortex.slot(name("requests"), 0);
  /// Slot<Long> totalRequests = cortex.slot(name("requests"), 0L);
  ///
  /// State stats = cortex.state()
  ///   .state(name("requests"), 1500)     // this hour
  ///   .state(name("requests"), 1500000L); // all time
  /// ```
  ///
  /// Critical behaviors verified:
  /// - State stores two values with same name ("slot.matching.metric")
  /// - Values have different types (int vs long)
  /// - intSlot retrieves int value (100)
  /// - longSlot retrieves long value (200L)
  /// - No type confusion or value collision
  ///
  /// Contrast with name-only lookup:
  /// ```
  /// // BAD: If state used name-only lookup
  /// state.put("metric", 100);
  /// state.put("metric", 200L);  // Overwrites int with long!
  /// int value = state.get("metric");  // ClassCastException!
  /// ```
  ///
  /// Type signature preservation:
  /// - Slot<Integer> → state returns Integer
  /// - Slot<Long> → state returns Long
  /// - No casting required at call site
  /// - Compile-time type safety maintained
  ///
  /// Expected: intSlot returns 100, longSlot returns 200L (type-specific retrieval)
  @Test
  void testSlotInStateMatching () {

    final var metricName = cortex.name ( "slot.matching.metric" );

    final var intSlot = cortex.slot ( metricName, 0 );
    final var longSlot = cortex.slot ( metricName, 0L );

    final var state = cortex.state ()
      .state ( metricName, 100 )
      .state ( metricName, 200L );

    assertEquals ( 100, state.value ( intSlot ) );
    assertEquals ( 200L, state.value ( longSlot ) );

  }

  @Test
  void testSlotNameIdentity () {

    final var name1 = cortex.name ( "slot.identity.test" );
    final var name2 = cortex.name ( "slot.identity.test" );
    final var nameFromParts = cortex.name ( List.of ( "slot", "identity", "test" ) );

    final var slot = cortex.slot ( name1, 42 );

    assertSame ( name1, name2 );
    assertSame ( name1, nameFromParts );
    assertSame ( name1, slot.name () );

  }

  @SuppressWarnings ( "DataFlowIssue" )
  @Test
  void testSlotNullNameGuard () {

    assertThrows (
      NullPointerException.class,
      () -> cortex.slot ( null, 42 )
    );

  }

  @SuppressWarnings ( "DataFlowIssue" )
  @Test
  void testSlotNullValueGuardForReferenceTypes () {

    final var slotName = cortex.name ( "slot.null.guard" );

    assertThrows (
      NullPointerException.class,
      () -> cortex.slot ( slotName, (String) null )
    );

    assertThrows (
      NullPointerException.class,
      () -> cortex.slot ( slotName, (Name) null )
    );

    assertThrows (
      NullPointerException.class,
      () -> cortex.slot ( slotName, (State) null )
    );

  }

  @Test
  void testSlotPrimitiveTypeReflection () {

    final var boolName = cortex.name ( "slot.primitive.bool" );
    final var intName = cortex.name ( "slot.primitive.int" );
    final var longName = cortex.name ( "slot.primitive.long" );
    final var floatName = cortex.name ( "slot.primitive.float" );
    final var doubleName = cortex.name ( "slot.primitive.double" );

    assertTrue ( cortex.slot ( boolName, true ).type ().isPrimitive () );
    assertTrue ( cortex.slot ( intName, 0 ).type ().isPrimitive () );
    assertTrue ( cortex.slot ( longName, 0L ).type ().isPrimitive () );
    assertTrue ( cortex.slot ( floatName, 0f ).type ().isPrimitive () );
    assertTrue ( cortex.slot ( doubleName, 0.0 ).type ().isPrimitive () );

  }

  @Test
  void testSlotReferenceTypeReflection () {

    final var stringName = cortex.name ( "slot.reference.string" );
    final var nameName = cortex.name ( "slot.reference.name" );
    final var stateName = cortex.name ( "slot.reference.state" );

    assertFalse ( cortex.slot ( stringName, "" ).type ().isPrimitive () );
    assertFalse ( cortex.slot ( nameName, cortex.name ( "test" ) ).type ().isPrimitive () );
    assertFalse ( cortex.slot ( stateName, cortex.state () ).type ().isPrimitive () );

  }

  @Test
  void testSlotTemplateForReferenceTypes () {

    final var nameSlot = cortex.name ( "slot.template.name" );
    final var stateSlot = cortex.name ( "slot.template.state" );

    final var storedName = cortex.name ( "slot.template.stored" );
    final var storedState = cortex.state ().state ( cortex.name ( "nested.template" ), 1 );

    final var state = cortex.state ()
      .state ( nameSlot, storedName )
      .state ( stateSlot, storedState );

    assertEquals (
      storedName,
      state.value ( cortex.slot ( nameSlot, cortex.name ( "fallback" ) ) )
    );

    assertEquals (
      storedState,
      state.value ( cortex.slot ( stateSlot, cortex.state () ) )
    );

  }

  /// Validates slot fallback mechanism: returns slot's default when value not in state.
  ///
  /// This test demonstrates the template pattern's fallback behavior: when state
  /// doesn't contain a value for the slot's name+type combination, state.value(slot)
  /// returns the slot's OWN default value. This enables configuration with safe
  /// defaults, eliminating null checks and providing sensible fallback behavior
  /// without explicit conditional logic.
  ///
  /// Test Scenario:
  /// ```
  /// emptyState = cortex.state();  // No entries
  ///
  /// template = slot("slot.template.missing", 999);  // Default = 999
  ///
  /// emptyState.value(template) → 999  (fallback to slot's default)
  /// ```
  ///
  /// Fallback Lookup Mechanism:
  /// ```
  /// state.value(slot) {
  ///   // Try to find value in state
  ///   Optional<Value> found = state.find(slot.name(), slot.type());
  ///
  ///   if (found.isPresent()) {
  ///     return found.get();  // State value takes precedence
  ///   } else {
  ///     return slot.value();  // Fallback to slot's default
  ///   }
  /// }
  /// ```
  ///
  /// Why fallback pattern matters:
  /// - **Safe defaults**: No null pointer exceptions from missing config
  /// - **No null checks**: Code assumes value is always present
  /// - **Configuration hierarchy**: Runtime state overrides compile-time defaults
  /// - **Self-documenting**: Slot definition shows default value
  ///
  /// Real-world configuration examples:
  ///
  /// 1. **Server configuration**:
  /// ```java
  /// // Slots define sensible defaults
  /// Slot<Integer> port = cortex.slot(name("server.port"), 8080);
  /// Slot<Integer> timeout = cortex.slot(name("server.timeout"), 30);
  /// Slot<String> host = cortex.slot(name("server.host"), "localhost");
  ///
  /// // Load runtime configuration (may be empty or partial)
  /// State config = loadConfig();
  ///
  /// // Always get valid values (default or overridden)
  /// int serverPort = config.value(port);     // 8080 if not configured
  /// int serverTimeout = config.value(timeout); // 30 if not configured
  /// String serverHost = config.value(host);    // "localhost" if not configured
  /// ```
  ///
  /// 2. **Feature flags with defaults**:
  /// ```java
  /// Slot<Boolean> enableCache = cortex.slot(name("features.cache"), true);
  /// Slot<Boolean> enableMetrics = cortex.slot(name("features.metrics"), false);
  ///
  /// State features = loadFeatureFlags();  // Might be empty
  ///
  /// if (features.value(enableCache)) {    // true by default
  ///   initializeCache();
  /// }
  /// ```
  ///
  /// 3. **Neural network hyperparameters**:
  /// ```java
  /// Slot<Double> learningRate = cortex.slot(name("nn.learningRate"), 0.001);
  /// Slot<Integer> epochs = cortex.slot(name("nn.epochs"), 100);
  /// Slot<Integer> batchSize = cortex.slot(name("nn.batchSize"), 32);
  ///
  /// State hyperparams = loadOrDefault();
  ///
  /// // Train with configured or default values
  /// train(
  ///   hyperparams.value(learningRate),  // 0.001 if not set
  ///   hyperparams.value(epochs),        // 100 if not set
  ///   hyperparams.value(batchSize)      // 32 if not set
  /// );
  /// ```
  ///
  /// Benefits of fallback pattern:
  /// - **No Optional**: Direct value retrieval, not Optional<T>
  /// - **Type-safe**: Slot type guarantees return type
  /// - **Fail-safe**: Always returns valid value (default or configured)
  /// - **Layered config**: Production config overrides defaults selectively
  ///
  /// Contrast with explicit null handling:
  /// ```
  /// // BAD: Manual null checking
  /// Integer port = config.get("server.port");
  /// if (port == null) {
  ///   port = 8080;  // Default
  /// }
  ///
  /// // GOOD: Automatic fallback
  /// int port = config.value(portSlot);  // 8080 if not configured
  /// ```
  ///
  /// Configuration precedence with fallback:
  /// ```
  /// 1. Explicit state value (if present) → highest priority
  /// 2. Slot default value (always present) → fallback
  /// ```
  ///
  /// Critical behaviors verified:
  /// - Empty state has no entries
  /// - Slot has default value (999)
  /// - state.value(slot) returns 999 (slot's default)
  /// - No exception thrown for missing value
  /// - Fallback automatic, no conditional logic needed
  ///
  /// Expected: emptyState.value(template) = 999 (fallback to slot default)
  @Test
  void testSlotTemplateWithFallback () {

    final var missingName = cortex.name ( "slot.template.missing" );
    final var template = cortex.slot ( missingName, 999 );

    final var emptyState = cortex.state ();

    assertEquals ( 999, emptyState.value ( template ) );

  }

  @Test
  void testSlotValueImmutability () {

    final var slotName = cortex.name ( "slot.immutable.value" );
    final var slot = cortex.slot ( slotName, 100 );

    assertEquals ( 100, slot.value () );
    assertEquals ( 100, slot.value () );
    assertEquals ( 100, slot.value () );

  }

  @Test
  void testSlotWithDifferentValues () {

    final var slotName = cortex.name ( "slot.different.values" );

    final var slot1 = cortex.slot ( slotName, 100 );
    final var slot2 = cortex.slot ( slotName, 200 );

    assertEquals ( slotName, slot1.name () );
    assertEquals ( slotName, slot2.name () );

    assertEquals ( 100, slot1.value () );
    assertEquals ( 200, slot2.value () );

  }

  @Test
  void testStateSlotProperties () {

    final var slotName = cortex.name ( "slot.state.test" );
    final var slotValue = cortex.state ().state ( cortex.name ( "nested" ), 99 );
    final var slot = cortex.slot ( slotName, slotValue );

    assertEquals ( slotName, slot.name () );
    assertEquals ( State.class, slot.type () );
    assertEquals ( slotValue, slot.value () );

  }

  @Test
  void testStringSlotProperties () {

    final var slotName = cortex.name ( "slot.string.test" );
    final var slot = cortex.slot ( slotName, "hello" );

    assertEquals ( slotName, slot.name () );
    assertEquals ( String.class, slot.type () );
    assertEquals ( "hello", slot.value () );

  }

  enum TestStatus {
    ACTIVE,
    INACTIVE,
    PENDING
  }

  enum Priority {
    LOW,
    MEDIUM,
    HIGH
  }

}
