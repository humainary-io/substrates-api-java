// Copyright (c) 2025 William David Louth

package io.humainary.substrates.tck;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/// Comprehensive tests for the Cell interface (experimental API).
///
/// This test class covers:
/// - Cell creation and lifecycle
/// - Cell pooling semantics
/// - Transformer and aggregator data flow
/// - Hierarchical cell structures
/// - Subscription model (Cell as Source)
/// - Cell as Pipe (emit behavior)
/// - Subject identity and naming
///
/// @author William David Louth
/// @since 1.0

@Disabled
final class CellTest
  extends TestSupport {

  private Cortex cortex;

  @BeforeEach
  void setup () {
    cortex = cortex ();
  }

  // ===========================
  // Cell Creation Tests
  // ===========================

  /// Validates stateful aggregation: cell maintains running sum across multiple emissions.
  ///
  /// This test demonstrates the Cell's egress aggregator stage, which can maintain
  /// state and transform values as they flow through the cell's output channel.
  ///
  /// Cell Transformation Pipeline:
  /// ```
  /// Cell<Integer, Integer>
  ///   ↓ emit(10)
  ///   → Ingress: identity (passes 10 unchanged)
  ///   → Egress: aggregator (sum = 0 + 10 = 10, emit 10)
  ///   ↓ emit(20)
  ///   → Ingress: identity (passes 20 unchanged)
  ///   → Egress: aggregator (sum = 10 + 20 = 30, emit 30)
  ///   ↓ emit(30)
  ///   → Ingress: identity (passes 30 unchanged)
  ///   → Egress: aggregator (sum = 30 + 30 = 60, emit 60)
  ///
  /// Output: [10, 30, 60] (running sum)
  /// ```
  ///
  /// Cell Stages:
  /// 1. **Ingress transformer**: Channel<O> → Pipe<I> (creates inlet from outlet)
  ///    - In this test: identity() - passes values unchanged
  /// 2. **Egress aggregator**: Channel<O> → Pipe<O> (transforms/aggregates output)
  ///    - In this test: stateful lambda maintaining running sum
  ///
  /// Aggregator Implementation:
  /// ```java
  /// final AtomicInteger sum = new AtomicInteger(0);
  /// channel -> cortex.pipe(n -> {
  ///   int current = sum.addAndGet(n);  // Stateful: accumulate sum
  ///   emissions.add(current);           // Track output
  ///   return current;                   // Emit running total
  /// }, channel.pipe());
  /// ```
  ///
  /// Key Characteristics:
  /// - **Stateful**: Aggregator maintains state (sum) across emissions
  /// - **Sequential**: Circuit thread ensures no race conditions on state
  /// - **Transformative**: Input values transformed to running totals
  /// - **Side effects**: Can track, log, or modify values in-flight
  ///
  /// Contrast with Identity Aggregator:
  /// ```java
  /// // Identity: passes values unchanged
  /// identity()  → [10, 20, 30]
  ///
  /// // Running sum: accumulates and emits totals
  /// aggregator  → [10, 30, 60]
  /// ```
  ///
  /// Why This Matters:
  /// - **Stream processing**: Running aggregates (sum, count, average)
  /// - **Event correlation**: Combine related events over time
  /// - **State machines**: Track state transitions through cell
  /// - **Metrics**: Compute derived metrics from raw events
  /// - **Neural networks**: Accumulate weighted inputs (integration)
  ///
  /// Real-World Examples:
  /// 1. **Request counting**: Maintain running count of requests per endpoint
  /// 2. **Latency tracking**: Compute rolling average latency
  /// 3. **Event windowing**: Aggregate events within time windows
  /// 4. **Neural activation**: Sum weighted inputs to compute activation level
  /// 5. **Financial totals**: Running balance from transaction stream
  ///
  /// Expected: [10, 30, 60] (running sum after each emission)
  @Test
  void testAggregatorCombinesMultipleEmissions () {

    final var circuit = cortex.circuit ();

    try {

      final var sum = new AtomicInteger ( 0 );
      final var emissions = new ArrayList < Integer > ();

      // Aggregator that tracks sum
      final Cell < Integer, Integer > cell =
        circuit.cell (
          cortex.name ( "aggregator.cell" ),
          identity (),
          channel -> cortex.pipe ( n -> {
            final int current = sum.addAndGet ( n );
            emissions.add ( current );
            return current;
          }, channel.pipe () ),
          cortex.pipe ( Integer.class )
        );

      cell.emit ( 10 );
      cell.emit ( 20 );
      cell.emit ( 30 );

      circuit.await ();

      assertEquals (
        List.of ( 10, 30, 60 ),
        emissions,
        "Aggregator should compute running sum"
      );

    } finally {

      circuit.close ();

    }

  }

  /// Validates basic cell creation with identity transformations (no-op pipeline).
  ///
  /// This test establishes the fundamental Cell abstraction: a bidirectional
  /// component that can receive emissions (as Pipe) and broadcast to subscribers
  /// (as Source), with optional transformation stages.
  ///
  /// Cell<I, O> Type Parameters:
  /// - **I**: Input type (what the cell receives via emit())
  /// - **O**: Output type (what the cell emits to subscribers/outlet)
  ///
  /// In this test: Cell<Integer, Integer> (same type in and out)
  ///
  /// Cell Creation Signature:
  /// ```java
  /// Cell<I, O> circuit.cell(
  ///   Name name,                      // Cell identity
  ///   Composer<I, O> ingress,         // Channel<O> → Pipe<I> (inlet creation)
  ///   Composer<O, O> egress,          // Channel<O> → Pipe<O> (outlet processing)
  ///   Pipe<O> outlet                  // Final destination for emissions
  /// )
  /// ```
  ///
  /// Identity Pipeline (No Transformation):
  /// ```
  /// cell.emit(42)
  ///   ↓
  /// Ingress: identity() → passes 42 unchanged
  ///   ↓
  /// Egress: identity() → passes 42 unchanged
  ///   ↓
  /// Outlet: cortex.pipe(emissions::add) → captures 42
  ///
  /// Result: emissions = [42]
  /// ```
  ///
  /// Ingress Composer (Transformer):
  /// - Signature: Channel<O> → Pipe<I>
  /// - Purpose: Create the cell's inlet (what it receives) from the outlet channel
  /// - Identity: Returns channel.pipe() directly (no transformation)
  /// - Custom: Can transform types (e.g., Integer → String)
  ///
  /// Egress Composer (Aggregator):
  /// - Signature: Channel<O> → Pipe<O>
  /// - Purpose: Process values before emitting to outlet
  /// - Identity: Returns channel.pipe() directly (no aggregation)
  /// - Custom: Can maintain state (running sum, average, etc.)
  ///
  /// Cell as Pipe:
  /// The returned Cell<I, O> implements Pipe<I>, so it can receive emissions:
  /// ```java
  /// cell.emit(value);  // Cell acts as emission receiver
  /// ```
  ///
  /// Cell as Source:
  /// Cell also implements Source<O>, so subscribers can observe emissions:
  /// ```java
  /// cell.subscribe(subscriber);  // Cell broadcasts to observers
  /// ```
  ///
  /// Why Identity Matters:
  /// The identity() composer is fundamental:
  /// - Zero overhead (no transformation cost)
  /// - Type-preserving (I = O)
  /// - Common case for simple routing
  /// - Baseline for comparison with custom transformers
  ///
  /// Contrast with Transformation:
  /// ```java
  /// // Identity: Cell<Integer, Integer>
  /// identity() → emit(42) → receive 42
  ///
  /// // Type transformation: Cell<Integer, String>
  /// transformer → emit(42) → receive "42"
  ///
  /// // Stateful aggregation: Cell<Integer, Integer>
  /// aggregator → emit(10), emit(20) → receive 10, 30 (running sum)
  /// ```
  ///
  /// Why This Matters:
  /// - **Simple routing**: Pass events through without modification
  /// - **Type preservation**: No conversion overhead
  /// - **Hierarchical structure**: Cells can have child cells (get method)
  /// - **Observable**: Cells broadcast to multiple subscribers
  /// - **Foundation**: Base case before adding transformation/aggregation
  ///
  /// Real-World Examples:
  /// 1. **Event forwarding**: Route events unchanged to multiple listeners
  /// 2. **Neural network input**: Receive external signals unchanged
  /// 3. **Message broker**: Simple pub-sub without transformation
  /// 4. **Monitoring pass-through**: Observe without affecting data
  /// 5. **Hierarchical composition**: Build complex structures from simple cells
  ///
  /// Expected: Single emission [42] passes through unchanged
  @Test
  void testCellCreationWithIdentityTransformerAndAggregator () {

    final var circuit = cortex.circuit ();

    try {

      final var emissions = new ArrayList < Integer > ();

      // Identity transformer and aggregator
      final Cell < Integer, Integer > cell =
        circuit.cell (
          cortex.name ( "identity.cell" ),
          identity (),  // ingress: output -> input (identity)
          identity (),  // egress: output -> output (identity)
          cortex.pipe ( emissions::add )
        );

      assertNotNull (
        cell,
        "Cell should be created successfully"
      );

      // Emit value through cell
      cell.emit ( 42 );

      circuit.await ();

      assertEquals (
        List.of ( 42 ),
        emissions,
        "Identity transformer/aggregator should pass value unchanged"
      );

    } finally {

      circuit.close ();

    }

  }

  // ===========================
  // Cell Pooling Tests
  // ===========================

  /// Validates child cell pooling: same name returns same instance (identity semantics).
  ///
  /// This test verifies that cells maintain a pool of child cells indexed by name,
  /// ensuring that repeated requests for the same name return the identical instance
  /// rather than creating duplicates. This is critical for maintaining hierarchical
  /// structure and avoiding resource duplication.
  ///
  /// Cell Hierarchy and Pooling:
  /// ```
  /// rootCell (parent)
  ///   └── childCell (name="child.cell")
  ///
  /// rootCell.percept("child.cell") → returns childCell instance
  /// rootCell.percept("child.cell") → returns SAME childCell instance (pooled)
  /// rootCell.percept("child.cell") → returns SAME childCell instance (pooled)
  /// ```
  ///
  /// Pooling Implementation (conceptual):
  /// ```java
  /// class Cell<I, O> {
  ///   private final Map<Name, Cell<O, O>> children = new ConcurrentHashMap<>();
  ///
  ///   Cell<O, O> percept(Name name) {
  ///     return children.computeIfAbsent(name, n -> createChildCell(n));
  ///   }
  /// }
  /// ```
  ///
  /// Key Characteristics:
  /// - **Name-based pooling**: Name is the key for identity
  /// - **Lazy creation**: Child created on first percept() call
  /// - **Instance reuse**: Subsequent percept() returns existing instance
  /// - **Thread-safe**: computeIfAbsent ensures no race conditions
  /// - **Type constraint**: Child cells have type Cell<O, O> (parent's output becomes child's input/output)
  ///
  /// Why Pooling Matters:
  /// Without pooling, every percept() call would create a new cell:
  /// ```java
  /// // Without pooling (WRONG):
  /// Cell child1 = parent.percept("x");
  /// Cell child2 = parent.percept("x");
  /// // child1 != child2 → duplicates! Emissions split!
  ///
  /// // With pooling (CORRECT):
  /// Cell child1 = parent.percept("x");
  /// Cell child2 = parent.percept("x");
  /// // child1 == child2 → same instance, consistent routing
  /// ```
  ///
  /// Contrast with Different Names:
  /// ```java
  /// Cell child1 = parent.percept(name("alpha"));  // Creates new cell
  /// Cell child2 = parent.percept(name("beta"));   // Creates different cell
  /// Cell child3 = parent.percept(name("alpha"));  // Returns child1 (pooled)
  ///
  /// // child1 == child3 (same name)
  /// // child1 != child2 (different names)
  /// ```
  ///
  /// Impact on Subscribers:
  /// Pooling ensures subscribers see consistent topology:
  /// ```java
  /// Cell child = parent.percept("x");
  /// child.subscribe(subscriber);  // Subscriber observes child
  ///
  /// Cell same = parent.percept("x");  // Returns existing child
  /// same.emit(value);             // Subscriber receives emission (same cell)
  /// ```
  ///
  /// Hierarchical Emission Flow:
  /// ```
  /// grandchild.emit(value)
  ///   ↓
  /// child (pooled instance)
  ///   ↓
  /// parent (pooled instance)
  ///   ↓
  /// subscribers
  /// ```
  ///
  /// Why This Matters:
  /// - **Topology consistency**: Same name always resolves to same cell
  /// - **Subscriber expectations**: Subscribers see stable structure
  /// - **Resource efficiency**: Avoid duplicate cells for same name
  /// - **Emission routing**: Predictable path from leaf to root
  /// - **Hierarchical integrity**: Maintain tree structure invariants
  ///
  /// Real-World Examples:
  /// 1. **Neural networks**: Same neuron name returns same neuron instance
  /// 2. **Service mesh**: Same endpoint name returns same endpoint cell
  /// 3. **Event routing**: Same topic name returns same topic cell
  /// 4. **Hierarchical metrics**: Same metric name returns same metric cell
  /// 5. **State machines**: Same state name returns same state cell
  ///
  /// Expected: Three get() calls return identical instance (reference equality)
  @Test
  void testCellPoolingIdentity () {

    final var circuit = cortex.circuit ();

    try {

      final var rootCell =
        circuit.cell (
          cortex.name ( "pooled.cell" ),
          identity (),
          identity (),
          cortex.pipe ( Object.class )
        );

      final var name = cortex.name ( "child.cell" );

      // Get child cell multiple times with same name
      final var child1 = rootCell.percept ( name );
      final var child2 = rootCell.percept ( name );
      final var child3 = rootCell.percept ( name );

      // All should be the SAME instance (pooling)
      assertSame (
        child1,
        child2,
        "Same name must return same child cell instance (1st vs 2nd)"
      );

      assertSame (
        child2,
        child3,
        "Same name must return same child cell instance (2nd vs 3rd)"
      );

      assertSame (
        child1,
        child3,
        "Same name must return same child cell instance (1st vs 3rd)"
      );

    } finally {

      circuit.close ();

    }

  }

  @Test
  void testCellSubjectHasCorrectName () {

    final var circuit = cortex.circuit ();

    try {

      final var cellName = cortex.name ( "named.cell" );

      final var cell =
        circuit.cell (
          cellName,
          identity (),
          identity (),
          cortex.pipe ( Object.class )
        );

      assertSame (
        cellName,
        cell.subject ().name (),
        "Cell subject name should match provided name"
      );

    } finally {

      circuit.close ();

    }

  }

  // ===========================
  // Hierarchical Cell Structure Tests
  // ===========================

  /// Validates Cell as Source: subscribers receive emissions from all child cells.
  ///
  /// This test demonstrates that Cell implements Source<O>, enabling dynamic
  /// subscription to observe emissions from all current and future child cells.
  /// This is THE mechanism for fan-out observation in hierarchical cell structures.
  ///
  /// Cell as Source Pattern:
  /// ```
  /// Cell<I, O> implements Source<O>
  ///   - Can have subscribers (observers)
  ///   - Subscribers notified of new child cells
  ///   - Subscribers receive emissions from children
  /// ```
  ///
  /// Subscription Timeline:
  /// ```
  /// 1. Create root cell
  /// 2. Subscribe to root (before children exist)
  /// 3. Create child cell
  ///    └─> Subscriber notified of new child channel
  ///    └─> Subscriber registers pipe to observe child emissions
  /// 4. Child emits value
  ///    └─> Subscriber's pipe receives emission
  /// ```
  ///
  /// Subscription Mechanism:
  /// ```java
  /// rootCell.subscribe(
  ///   subscriber(
  ///     name,
  ///     (channel, registrar) -> {
  ///       // Called when child cell created
  ///       // channel: Child cell's output channel
  ///       registrar.register(
  ///         pipe(value -> {
  ///           // Receives emissions from this child
  ///         })
  ///       );
  ///     }
  ///   )
  /// );
  /// ```
  ///
  /// Key Insight - Dynamic Topology:
  /// Subscribers can attach **before** children are created:
  /// - Subscriber sees all existing children (retrospective)
  /// - Subscriber notified of new children (prospective)
  /// - Subscriber receives emissions from all children
  ///
  /// Emission Flow with Subscription:
  /// ```
  /// child.emit(999)
  ///   ↓
  /// child ingress → egress
  ///   ↓
  /// root's outlet channel
  ///   ├─> root outlet pipe (if any)
  ///   └─> subscriber pipes (registered via subscribe)
  ///         └─> subscriptionEmissions.add(999)
  /// ```
  ///
  /// Contrast: Outlet vs Subscription:
  /// ```java
  /// // Outlet: Single destination (constructor)
  /// Cell<I, O> cell = circuit.cell(name, ingress, egress,
  ///   outlet  // Single pipe
  /// );
  ///
  /// // Subscription: Multiple observers (runtime)
  /// cell.subscribe(subscriber1);  // Observe all children
  /// cell.subscribe(subscriber2);  // Multiple subscribers
  /// cell.subscribe(subscriber3);  // Fan-out to many
  /// ```
  ///
  /// Why Cell Subscriptions Matter:
  /// - **Dynamic observation**: Attach observers at runtime
  /// - **Multiple consumers**: Fan-out to many subscribers
  /// - **Hierarchical monitoring**: Observe entire subtree
  /// - **Separation of concerns**: Outlet vs observers
  /// - **Live topology tracking**: See structure as it evolves
  ///
  /// Real-World Examples:
  /// 1. **Metrics collection**: Subscribe to service cell, observe all endpoints
  /// 2. **Distributed tracing**: Subscribe to operation cell, trace all sub-operations
  /// 3. **Neural network observation**: Subscribe to layer, observe all neurons
  /// 4. **Event logging**: Subscribe to topic cell, log all sub-topic events
  /// 5. **Live debugging**: Attach debugger to running cell hierarchy
  ///
  /// Multiple Subscribers (Fan-Out):
  /// ```java
  /// rootCell.subscribe(metricsCollector);
  /// rootCell.subscribe(traceRecorder);
  /// rootCell.subscribe(anomalyDetector);
  ///
  /// child.emit(value);
  /// // All three subscribers receive emission independently
  /// ```
  ///
  /// Subscriber Lifecycle:
  /// ```java
  /// Subscription sub = cell.subscribe(subscriber);
  /// // ... observe emissions ...
  /// sub.close();  // Unsubscribe (stop receiving emissions)
  /// ```
  ///
  /// Subscriber Callback Signature:
  /// ```java
  /// Subscriber<O> = (Channel<O> channel, Registrar registrar) -> {
  ///   // Called for each existing and future child
  ///   // Register pipe(s) to observe this child's emissions
  ///   registrar.register(pipe);
  /// }
  /// ```
  ///
  /// Multiple Children with Subscription:
  /// ```
  /// rootCell.subscribe(subscriber);
  /// child1 = root.percept("alpha");  // Subscriber notified
  /// child2 = root.percept("beta");   // Subscriber notified
  /// child3 = root.percept("gamma");  // Subscriber notified
  ///
  /// child1.emit(10);  // Subscriber receives
  /// child2.emit(20);  // Subscriber receives
  /// child3.emit(30);  // Subscriber receives
  ///
  /// subscriptionEmissions = [10, 20, 30]
  /// ```
  ///
  /// Expected: Subscriber receives emission [999] from child cell
  @Test
  void testCellSubscriptionReceivesEmissions () {

    final var circuit = cortex.circuit ();

    try {

      final Cell < Integer, Integer > rootCell =
        circuit.cell (
          cortex.name ( "subscribable.cell" ),
          identity (),
          identity (),
          cortex.pipe ( Integer.class )
        );

      final var subscriptionEmissions = new ArrayList < Integer > ();

      // Subscribe to root cell
      rootCell.subscribe (
        cortex.subscriber (
          cortex.name ( "cell.subscriber" ),
          ( _, registrar ) ->
            registrar.register ( cortex.pipe ( subscriptionEmissions::add ) )
        )
      );

      // Create child cell (triggers subscription notification)
      final var child = rootCell.percept ( cortex.name ( "child" ) );

      // Emit through child
      child.emit ( 999 );

      circuit.await ();

      assertEquals (
        List.of ( 999 ),
        subscriptionEmissions,
        "Subscriber should receive emissions from child cells"
      );

    } finally {

      circuit.close ();

    }

  }

  @Test
  void testCellSubscriptionToMultipleChildren () {

    final var circuit = cortex.circuit ();

    try {

      final Cell < Integer, Integer > rootCell =
        circuit.cell (
          cortex.name ( "multi.subscriber.cell" ),
          identity (),
          identity (),
          cortex.pipe ( Integer.class )
        );

      final var allEmissions = new ArrayList < Integer > ();

      // Subscribe before creating children
      rootCell.subscribe (
        cortex.subscriber (
          cortex.name ( "multi.subscriber" ),
          ( _, registrar ) ->
            registrar.register ( cortex.pipe ( allEmissions::add ) )
        )
      );

      // Create multiple children
      final var child1 = rootCell.percept ( cortex.name ( "alpha" ) );
      final var child2 = rootCell.percept ( cortex.name ( "beta" ) );
      final var child3 = rootCell.percept ( cortex.name ( "gamma" ) );

      // Each child emits
      child1.emit ( 10 );
      child2.emit ( 20 );
      child3.emit ( 30 );

      circuit.await ();

      assertEquals (
        List.of ( 10, 20, 30 ),
        allEmissions,
        "Subscriber should receive emissions from all children"
      );

    } finally {

      circuit.close ();

    }

  }

  // ===========================
  // Cell Subscription Tests (Cell as Source)
  // ===========================

  @Test
  void testCellWithCircuitPipe () {

    final var circuit = cortex.circuit ();

    try {

      final var emissions = new ArrayList < Integer > ();

      final Cell < Integer, Integer > cell = circuit.cell (
        cortex.name ( "async.cell" ),
        identity (),
        identity (),
        circuit.pipe ( cortex.pipe ( emissions::add ) )
      );

      cell.emit ( 123 );

      circuit.await ();

      assertEquals (
        List.of ( 123 ),
        emissions,
        "Cell should work with circuit.pipe() for async dispatch"
      );

    } finally {

      circuit.close ();

    }

  }

  /// Validates ingress type transformation: Cell<Integer, String> converts input type to output type.
  ///
  /// This test demonstrates the Cell's ingress transformer stage, which enables type
  /// conversion between what the cell receives (I) and what it emits (O). This is
  /// THE critical feature that distinguishes cells from simple pipes.
  ///
  /// Type Transformation Pipeline:
  /// ```
  /// Cell<Integer, String>
  ///   ↓ cell.emit(10)                    [Input: Integer]
  ///   → Ingress: n -> String.valueOf(n * 2)
  ///       10 * 2 = 20 → "20"            [Transform: Integer → String]
  ///   → Egress: identity()
  ///       "20" → "20"                    [Pass through: String → String]
  ///   → Outlet: emissions.add("20")
  ///       Result: ["20"]                 [Output: String]
  ///
  /// Full sequence:
  ///   emit(10) → "20"
  ///   emit(20) → "40"
  ///   emit(30) → "60"
  /// ```
  ///
  /// Ingress Transformer Signature:
  /// ```java
  /// Composer<I, O> ingress = channel -> {
  ///   // channel: Channel<O> (outlet type: String)
  ///   // return: Pipe<I> (inlet type: Integer)
  ///   return cortex.pipe(
  ///     (Integer n) -> String.valueOf(n * 2),  // I → O transformation
  ///     channel.pipe()                         // Pipe<O> destination
  ///   );
  /// };
  /// ```
  ///
  /// Type Flow:
  /// ```
  /// Cell<I, O> where I=Integer, O=String
  ///
  /// Ingress: Channel<String> → Pipe<Integer>
  ///   - Receives outlet channel (String type)
  ///   - Creates inlet pipe (Integer type)
  ///   - Transformer function: Integer → String
  ///
  /// Egress: Channel<String> → Pipe<String>
  ///   - Receives outlet channel (String type)
  ///   - Creates outlet pipe (String type)
  ///   - Identity: no further transformation
  /// ```
  ///
  /// Why Ingress Transformation Matters:
  /// Without ingress transformation, input and output types must match:
  /// ```java
  /// // Without transformation (identity):
  /// Cell<Integer, Integer>  // I = O (same type)
  ///
  /// // With ingress transformation:
  /// Cell<Integer, String>   // I ≠ O (type conversion)
  /// Cell<String, Integer>   // Parse strings to integers
  /// Cell<Event, Metric>     // Convert events to metrics
  /// ```
  ///
  /// Contrast with Egress Aggregation:
  /// ```java
  /// // Ingress: Type transformation (I → O)
  /// Integer → String conversion
  ///
  /// // Egress: Same-type aggregation (O → O)
  /// String → String (but stateful: running concatenation)
  /// ```
  ///
  /// Transformation + Aggregation:
  /// ```java
  /// Cell<Integer, String> cell = circuit.cell(
  ///   name,
  ///   // Ingress: Integer → String
  ///   channel -> pipe(n -> String.valueOf(n * 2), channel.pipe()),
  ///   // Egress: String → String (concatenate)
  ///   channel -> pipe(s -> accumulated + s, channel.pipe()),
  ///   outlet
  /// );
  /// ```
  ///
  /// Why This Matters:
  /// - **Type safety**: Compiler enforces I → O transformation
  /// - **Adaptation layer**: Convert between incompatible types
  /// - **Data normalization**: Transform raw input to standard format
  /// - **Protocol conversion**: Translate between different formats
  /// - **Neural networks**: Weight multiplication with type conversion
  ///
  /// Real-World Examples:
  /// 1. **Sensor data**: Cell<RawBytes, Temperature> parses sensor readings
  /// 2. **Protocol gateway**: Cell<HttpRequest, GrpcRequest> converts protocols
  /// 3. **Metric aggregation**: Cell<Event, Counter> counts events
  /// 4. **String parsing**: Cell<String, Integer> parses numbers with validation
  /// 5. **Neural synapses**: Cell<Double, Double> applies weights (w * input)
  ///
  /// Type Constraint on Child Cells:
  /// ```java
  /// Cell<Integer, String> parent = ...;
  /// Cell<String, String> child = parent.percept(name);
  /// // Child input type = Parent output type (String)
  /// ```
  ///
  /// Expected: [20, 40, 60] (doubled values converted to strings)
  @Test
  void testCellWithTransformingPipe () {

    final var circuit = cortex.circuit ();

    try {

      final var emissions = new ArrayList < String > ();

      // Cell<Integer, String>: receives Integer, outputs String
      // Transformer creates inlet (Pipe<Integer>) from outlet (Pipe<String>)
      // It multiplies by 2 and converts to String
      final Cell < Integer, String > cell =
        circuit.cell (
          cortex.name ( "transform.cell" ),
          channel -> cortex.pipe ( ( Integer n ) -> String.valueOf ( n * 2 ), channel.pipe () ),
          identity (),  // egress: identity
          cortex.pipe ( emissions::add )
        );

      cell.emit ( 10 );
      cell.emit ( 20 );
      cell.emit ( 30 );

      circuit.await ();

      assertEquals (
        List.of ( "20", "40", "60" ),
        emissions,
        "Transformer should double values before aggregator converts to string"
      );

    } finally {

      circuit.close ();

    }

  }

  @Test
  void testChildCellSubjectHasHierarchicalName () {

    final var circuit = cortex.circuit ();

    try {

      final var parentCell =
        circuit.cell (
          cortex.name ( "parent" ),
          identity (),
          identity (),
          cortex.pipe ( Object.class )
        );

      final var childName = cortex.name ( "child" );
      final var childCell = parentCell.percept ( childName );

      // Child subject name should just be "child" (not "parent.child")
      assertSame (
        childName,
        childCell.subject ().name (),
        "Child cell subject name should be the provided name"
      );

    } finally {

      circuit.close ();

    }

  }

  // ===========================
  // Cell Subject Tests
  // ===========================

  @Test
  void testDeepHierarchy () {

    final var circuit = cortex.circuit ();

    try {

      final var emissions = new ArrayList < Integer > ();

      Cell < Integer, Integer > currentCell = circuit.cell (
        cortex.name ( "level0" ),
        identity (),
        identity (),
        cortex.pipe ( emissions::add )
      );

      // Create 10 levels of hierarchy
      for ( int i = 1; i <= 10; i++ ) {
        currentCell = currentCell.percept (
          cortex.name ( "level" + i )
        );
      }

      // Emit from the deepest level
      currentCell.emit ( 42 );

      circuit.await ();

      assertEquals (
        List.of ( 42 ),
        emissions,
        "Deep hierarchy should propagate emissions to root"
      );

    } finally {

      circuit.close ();

    }

  }

  @Test
  void testDifferentNamesProduceDifferentCells () {

    final var circuit = cortex.circuit ();

    try {

      final var rootCell =
        circuit.cell (
          cortex.name ( "root" ),
          identity (),
          identity (),
          cortex.pipe ( Object.class )
        );

      final var name1 = cortex.name ( "child.one" );
      final var name2 = cortex.name ( "child.two" );

      final var child1 = rootCell.percept ( name1 );
      final var child2 = rootCell.percept ( name2 );

      // Different names should produce different cells
      assertNotSame (
        child1,
        child2,
        "Different names must return different cell instances"
      );

    } finally {

      circuit.close ();

    }

  }

  // ===========================
  // Cell Transformation Pipeline Tests
  // ===========================

  @Test
  void testEmptyHierarchy () {

    final var circuit = cortex.circuit ();

    try {

      final var emissions = new ArrayList < Integer > ();

      final Cell < Integer, Integer > cell = circuit.cell (
        cortex.name ( "lonely.cell" ),
        identity (),
        identity (),
        cortex.pipe ( emissions::add )
      );

      // Root cell with no children
      cell.emit ( 777 );

      circuit.await ();

      assertEquals (
        List.of ( 777 ),
        emissions,
        "Root cell without children should emit directly"
      );

    } finally {

      circuit.close ();

    }

  }

  /// Validates hierarchical emission propagation: grandchild → child → root (3-level tree).
  ///
  /// This test demonstrates that cells can form deep hierarchical structures where
  /// emissions at any level flow upward through all ancestors to the root, enabling
  /// tree-structured data aggregation and fan-in patterns.
  ///
  /// Three-Level Cell Hierarchy:
  /// ```
  /// root (circuit.cell)
  ///   └── child (root.get)
  ///         └── grandchild (child.get)
  /// ```
  ///
  /// Emission Propagation Flow:
  /// ```
  /// grandchild.emit(100)
  ///   ↓
  /// grandchild ingress (identity)
  ///   ↓
  /// grandchild egress (identity)
  ///   ↓
  /// child's outlet channel
  ///   ↓
  /// child ingress (identity)
  ///   ↓
  /// child egress (identity)
  ///   ↓
  /// root's outlet channel
  ///   ↓
  /// root ingress (identity)
  ///   ↓
  /// root egress (identity)
  ///   ↓
  /// root outlet (emissions::add)
  ///   ↓
  /// Result: emissions = [100]
  /// ```
  ///
  /// Key Insight - Upward Flow:
  /// In cell hierarchies, data flows **UP** from leaves to root:
  /// - Leaf cells receive external emissions
  /// - Intermediate cells aggregate from children
  /// - Root cell collects all descendant emissions
  ///
  /// This is the **opposite** of typical tree traversal (root → leaves).
  ///
  /// Type Constraints in Hierarchy:
  /// ```java
  /// Cell<Integer, Integer> root;           // I=Integer, O=Integer
  /// Cell<Integer, Integer> child;          // Must match: I=root.O, O=root.O
  /// Cell<Integer, Integer> grandchild;     // Must match: I=child.O, O=child.O
  ///
  /// // Type constraint: child.I = parent.O
  /// // For identity cells: all levels have same type
  /// ```
  ///
  /// Contrast with Type-Transforming Hierarchy:
  /// ```java
  /// Cell<String, Integer> root;            // Receives Integer, outputs Integer
  /// Cell<Double, String> child;            // Receives String, outputs String
  /// Cell<Event, Double> grandchild;        // Receives Double, outputs Double
  ///
  /// grandchild.emit(event) → converts to Double
  ///   ↓
  /// child receives Double → converts to String
  ///   ↓
  /// root receives String → converts to Integer
  /// ```
  ///
  /// Why Hierarchical Cells Matter:
  /// - **Tree aggregation**: Collect data from multiple sources
  /// - **Fan-in pattern**: Many children → one parent
  /// - **Compositional structure**: Build complex from simple
  /// - **Modular organization**: Separate concerns by level
  /// - **Neural networks**: Layered structure with signal propagation
  ///
  /// Real-World Examples:
  /// 1. **Service mesh**: Request → Endpoint → Service → Global metrics
  /// 2. **Neural networks**: Input → Hidden layers → Output layer
  /// 3. **Organizational hierarchy**: Employee → Team → Department → Company
  /// 4. **File system**: File → Directory → Volume → Total usage
  /// 5. **Event aggregation**: Event → Topic → Category → Global counter
  ///
  /// Emission Routing:
  /// Each cell's outlet becomes the inlet for its parent:
  /// ```java
  /// // Conceptual implementation
  /// class Cell<I, O> {
  ///   Cell<O, O> get(Name name) {
  ///     return children.computeIfAbsent(name, n ->
  ///       createChild(n, this.outletChannel)  // Child emits to parent's inlet
  ///     );
  ///   }
  /// }
  /// ```
  ///
  /// Multiple Children Fan-In:
  /// ```
  /// root
  ///   ├── child1.emit(10)
  ///   ├── child2.emit(20)
  ///   └── child3.emit(30)
  ///
  /// root receives: [10, 20, 30] (order preserved by circuit)
  /// ```
  ///
  /// Depth Limit:
  /// No inherent depth limit - can create arbitrarily deep hierarchies:
  /// - testDeepHierarchy verifies 10 levels
  /// - Limited only by stack space and memory
  /// - Each level adds minimal overhead
  ///
  /// Expected: Single emission [100] flows from grandchild up to root
  @Test
  void testHierarchicalCellStructure () {

    final var circuit = cortex.circuit ();

    try {

      final var emissions = new ArrayList < Integer > ();

      final var rootCell =
        circuit.cell (
          cortex.name ( "root" ),
          identity (),
          identity (),
          cortex.pipe ( Integer.class, emissions::add )
        );

      // Create child cell
      final var childCell =
        rootCell.percept ( cortex.name ( "child" ) );

      // Create grandchild cell
      final var grandchildCell =
        childCell.percept ( cortex.name ( "grandchild" ) );

      // Emit through grandchild - should flow up to root
      grandchildCell.emit ( 100 );

      circuit.await ();

      assertEquals (
        List.of ( 100 ),
        emissions,
        "Grandchild emissions should flow up to root cell"
      );

    } finally {

      circuit.close ();

    }

  }

  // ===========================
  // Cell Edge Cases
  // ===========================

  @Test
  void testMultipleChildrenEmitToParent () {

    final var circuit = cortex.circuit ();

    try {

      final var emissions = new ArrayList < Integer > ();

      final var parentCell =
        circuit.cell (
          cortex.name ( "parent" ),
          identity (),
          identity (),
          cortex.pipe ( Integer.class, emissions::add )
        );

      final var child1 = parentCell.percept ( cortex.name ( "child1" ) );
      final var child2 = parentCell.percept ( cortex.name ( "child2" ) );
      final var child3 = parentCell.percept ( cortex.name ( "child3" ) );

      // All children emit
      child1.emit ( 1 );
      child2.emit ( 2 );
      child3.emit ( 3 );

      circuit.await ();

      assertEquals (
        List.of ( 1, 2, 3 ),
        emissions,
        "All child emissions should aggregate to parent"
      );

    } finally {

      circuit.close ();

    }

  }

  @Test
  void testTransformerAppliedBeforeAggregator () {

    final var circuit = cortex.circuit ();

    try {

      final var emissions = new ArrayList < String > ();

      // Cell<Integer, String>: receives Integer, outputs String
      // Transformer: adds 100 then formats as "Value: X"
      // Aggregator: identity
      final Cell < Integer, String > cell =
        circuit.cell (
          cortex.name ( "pipeline.cell" ),
          channel -> cortex.pipe ( ( Integer n ) -> "Value: " + ( n + 100 ), channel.pipe () ),
          identity (),  // egress: identity
          cortex.pipe ( emissions::add )
        );

      cell.emit ( 5 );

      circuit.await ();

      assertEquals (
        List.of ( "Value: 105" ),
        emissions,
        "Transformer should apply first, then aggregator"
      );

    } finally {

      circuit.close ();

    }

  }

  // ===========================
  // Cell Integration Tests
  // ===========================

  @Test
  void testUnsubscribeStopsReceivingEmissions () {

    final var circuit = cortex.circuit ();

    try {

      final Cell < Integer, Integer > rootCell =
        circuit.cell (
          cortex.name ( "unsubscribe.cell" ),
          identity (),
          identity (),
          cortex.pipe ( Integer.class )
        );

      final var emissions = new ArrayList < Integer > ();

      final var subscription =
        rootCell.subscribe (
          cortex.subscriber (
            cortex.name ( "temp.subscriber" ),
            ( _, registrar ) ->
              registrar.register ( cortex.pipe ( emissions::add ) )
          )
        );

      final var child = rootCell.percept ( cortex.name ( "child" ) );

      child.emit ( 100 );
      circuit.await ();

      assertEquals (
        List.of ( 100 ),
        emissions,
        "Should receive emission before unsubscribe"
      );

      // Unsubscribe
      subscription.close ();
      circuit.await ();

      // Emit after unsubscribe
      child.emit ( 200 );
      circuit.await ();

      // Should not receive second emission
      assertEquals (
        List.of ( 100 ),
        emissions,
        "Should not receive emissions after unsubscribe"
      );

    } finally {

      circuit.close ();

    }

  }

}
