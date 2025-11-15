# Substrates API User Guide

**A comprehensive guide for building neural-like computational networks with the Substrates API**

## Table of Contents

1. [Introduction](#introduction)
2. [Core Concepts](#core-concepts)
3. [Getting Started](#getting-started)
4. [Architecture Deep Dive](#architecture-deep-dive)
5. [Working with Circuits](#working-with-circuits)
6. [Channels and Conduits](#channels-and-conduits)
7. [Subscribers and Reservoirs](#subscribers-and-reservoirs)
8. [Flow Operators](#flow-operators)
9. [Building Neural-Like Networks](#building-neural-like-networks)
10. [Observability with Serventis](#observability-with-serventis)
11. [Best Practices](#best-practices)
12. [Common Pitfalls](#common-pitfalls)
13. [Advanced Patterns](#advanced-patterns)
14. [Performance Considerations](#performance-considerations)

---

## Introduction

Substrates is a high-performance Java framework for building **neural-like computational networks**
where values flow through circuits, conduits, and channels with deterministic ordering and dynamic
topology adaptation. At its core, Substrates provides:

- **Single-digit nanosecond emission latency** for high-frequency event processing
- **Deterministic execution** through single-threaded circuit workers
- **Lock-free concurrency** via async queuing
- **Dynamic topology** with runtime subscription changes
- **Stack-safe deep networks** enabling 1000+ layer hierarchies

### Who Should Use This Guide?

This guide is for developers who want to:

- Build event-driven systems with deterministic ordering guarantees
- Implement observability frameworks with high-frequency instrumentation
- Create adaptive systems with dynamic topology reconfiguration
- Explore neural-like network architectures in Java

---

## Core Concepts

Understanding Substrates requires grasping four fundamental pillars:

### 1. Single-Threaded Circuits

Each **Circuit** owns exactly one processing thread (virtual thread). This design eliminates:

- Lock contention
- Race conditions
- Complex synchronization logic

**Threading Model:**

```
Caller Thread          Circuit Thread
     |                      |
emit(value) --------→  [Ingress Queue]
     |                      |
   return                dequeue
immediately              process
                         notify observers
```

Caller threads enqueue emissions and return immediately (non-blocking). The circuit thread
sequentially processes all emissions, ensuring deterministic execution.

### 2. Deterministic Ordering

Substrates uses a **dual-queue architecture** for depth-first execution:

```
External thread: emit(A), emit(B), emit(C) → [Ingress Queue]
Circuit processes A, which triggers: emit(A1), emit(A2) → [Transit Queue]

Execution order: A → A1 → A2 → B → C (depth-first)
NOT: A → B → C → A1 → A2 (breadth-first)
```

The **transit queue has priority** over the ingress queue, ensuring:

- **Causality preservation**: Cascading effects complete before next input
- **Atomic computations**: Recursive chains appear atomic to observers
- **Neural-like dynamics**: Proper signal propagation in feedback networks

### 3. Dynamic Topology

Substrates supports **runtime subscription changes** without stopping the system:

- Subscribers discover channels **on-demand** by name
- Add/remove subscribers dynamically
- Lazy rebuild mechanism updates topology only when needed
- Version tracking ensures efficient synchronization

### 4. Extreme Performance

Key optimizations enable **10M-50M Hz** emission rates:

- **Lock-free lazy rebuild**: Version-based subscription synchronization (~2.98ns overhead)
- **O(1) name pooling**: Interned names enable reference-based identity
- **Zero-allocation paths**: Enum-based signals reuse cached instances
- **Transit queue priority**: In-circuit emissions avoid queue roundtrip (~3ns vs ~20ns)

---

## Getting Started

### Prerequisites

- **Java 25+** (virtual threads required)
- **Maven 3.9.11+**

### Basic Example: Hello Substrates

```
import io.humainary.substrates.api.Substrates.*;
import io.humainary.substrates.api.Substrates.Composer;

public class HelloSubstrates {
  public static void main(String[] args) {
    // 1. Get the Cortex (singleton factory)
    final Cortex cortex = Substrates.cortex();

    // 2. Create a Circuit (processing engine)
    final Circuit circuit = cortex.circuit();

    try {
      // 3. Create a Conduit (channel pool) with a composer
      final Conduit<Pipe<String>> conduit =
        circuit.conduit(Composer.pipe(String.class));

      // 4. Get a named channel (creates or reuses)
      final Name name = cortex.name("greeting");
      final Pipe<String> pipe = conduit.percept(name);

      // 5. Create a reservoir to observe emissions
      final Reservoir<String> reservoir = cortex.reservoir(conduit);

      // 6. Emit values
      pipe.emit("Hello");
      pipe.emit("Substrates");

      // 7. Wait for all emissions to process
      circuit.await();

      // 8. Drain observations
      reservoir.drain().forEach(capture -> {
        System.out.println(
          capture.subject().name() + ": " + capture.emission()
        );
      });
      // Output:
      // greeting: Hello
      // greeting: Substrates

    } finally {
      // 9. Always close resources
      circuit.close();
    }
  }
}
```

**Key Takeaways:**

1. **Cortex** is the entry point (singleton factory)
2. **Circuit** processes emissions in order
3. **Conduit** pools channels by name
4. **Pipe** emits typed values
5. **Reservoir** captures emissions for observation
6. **await()** blocks until queue is empty
7. **Always close resources** in finally blocks

---

## Architecture Deep Dive

### The 29 Core Interfaces

Substrates defines 29 interfaces organized into 7 categories:

#### Data Flow Interfaces

- **Pipe**: Primary emission carrier with `emit(E)` method
- **Channel**: Subject-based port into conduit pipeline, creates pipes (@Temporal - only valid in
  Composer callback)
- **Flow**: Type-preserving transformation operators (filter, map, reduce)
- **Sift**: Selective multicasting based on criteria (@Temporal - don't retain)

#### Component Interfaces

- **Circuit**: Central processing engine, single-threaded event loop
- **Conduit**: Caches percepts by name (Lookup), applies flow operators
- **Cell**: Async boundary for stack-safe hierarchies
- **Cortex**: Factory for creating circuits, pools, scopes

#### Identity Interfaces

- **Subject**: Hierarchical reference with identity, name, state
- **Name**: Hierarchical identifier (e.g., "service.api.latency")
- **Id**: Unique identity token
- **Substrate**: Base abstraction for all typed entities

#### Resource Interfaces

- **Resource**: Closeable lifecycle management
- **Scope**: Hierarchical resource container with automatic cleanup
- **Closure**: Ordered sequence of resources
- **Reservoir**: Incremental observer for conduit emissions

#### Discovery Interfaces

- **Source**: Provides lookup capability for subjects
- **Subscriber**: Dynamically subscribes to channels
- **Subscription**: Handle for unsubscribing
- **Registrar**: Callback interface for registering pipes (@Temporal)

#### Utility Interfaces

- **State**: Immutable linked-list composition
- **Slot**: Type-safe name-value pair
- **Current**: Thread-local context carrier (@Temporal - only valid in originating thread)

#### Receptor Pattern

- **Percept**: Marker interface for observable entities (pipes, instruments)
- **Receptor**: Domain alternative to Consumer<T> for receiving emissions
- **Capture**: Name-value pair representing a captured emission
- **Extent**: Spatial/temporal boundaries (experimental)

### Type Hierarchy

```
Substrate (typed entity)
  ↓
Subject (identity + name + state)
  ↓
Channel (creates pipes for emissions)
  ↓
Pipe extends Percept (emission carrier)
```

All instruments (Counter, Monitor, Probe) implement **Percept**, enabling:

- Type-safe composer functions
- Generic utilities across signal types
- Intentional design (not arbitrary objects)

---

## Working with Circuits

### Circuit Lifecycle

```
// Create circuit
final Circuit circuit = cortex.circuit ();

// Create circuit with name (for debugging/monitoring)
final Circuit circuit = cortex.circuit ( cortex.name ( "main.circuit" ) );

// Use circuit
pipe.emit ( value );
circuit.await ();  // Block until queue empty

// Close circuit (stops processing)
circuit.close ();

```

### The await() Method

**Purpose**: Block until all queued emissions are processed

**IMPORTANT**: `await()` is primarily a **testing and benchmarking tool** - it's **rarely used in
production** because emissions are asynchronous by design. Production code typically emits values
and continues without waiting for processing.

**Use Cases:**

1. **Testing**: Verify emissions were processed before assertions
2. **Benchmarking**: Synchronize for round-trip measurements
3. **Batch Processing**: Coordinate groups of events (rare in production)
4. **Shutdown**: Ensure queue is drained before closing circuit

**Production Pattern** (no await):

```java
// Emit and continue immediately (typical production)
pipe.emit ( value );

// Caller continues - circuit processes asynchronously
doOtherWork ();
```

**Testing Pattern** (with await):

```java
// Emit and wait for processing (testing only)
pipe.emit ( value );
circuit.

await ();  // Block until processed

assertEquals ( expected, result );  // Safe to assert
```

**Important Constraint:**

```
// NEVER call await() from within circuit thread
pipe.emit(
  cortex.pipe(value -> {
    circuit.await();  // ❌ IllegalStateException!
    // Would deadlock: circuit waiting for itself
  })
);
```

**Solution for recursive synchronization:**

```
// Use transit queue priority instead
pipe.emit(
  cortex.pipe(value -> {
    // Emit recursively
    otherPipe.emit(derivedValue);
    // Transit queue ensures it processes before ingress queue
  })
);
```

### Creating Conduits

Conduits pool channels by name and apply transformations:

```
// Simple pipe conduit
final Conduit<Pipe<Integer>> conduit =
  circuit.conduit(Composer.pipe(Integer.class));

// Custom percept conduit
final Conduit<Counter> conduit =
  circuit.conduit(Counters::composer);

// With flow operators
final Conduit<Pipe<Integer>> conduit = circuit.conduit(
  cortex.name("filtered"),
  Composer.pipe(Integer.class),
  flow -> flow.guard(value -> value > 0).limit(100)
);
```

### Multiple Circuits

Circuits are **isolated** - each has its own thread and queues:

```
final Circuit c1 = cortex.circuit(cortex.name("circuit.1"));
final Circuit c2 = cortex.circuit(cortex.name("circuit.2"));

// c1 and c2 process independently in parallel
// Coordination requires explicit cross-circuit emissions
```

---

## Channels and Conduits

### Channel Pooling

Channels are **pooled by name** - same name returns same instance:

```
final Name name = cortex.name("requests");
final Pipe<Integer> pipe1 = conduit.percept(name);
final Pipe<Integer> pipe2 = conduit.percept(name);

assert pipe1 == pipe2;  // ✓ Same instance (identity-based)
```

**Benefits:**

- **No manual registration**: Channels created on-demand
- **Automatic deduplication**: Same name = same instance
- **Thread-safe**: Concurrent access to same name is safe

### Hierarchical Names

Names support **hierarchical composition**:

```
final Name root = cortex.name("service");
final Name api = root.name("api");       // "service.api"
final Name latency = api.name("latency"); // "service.api.latency"

// Names are interned (same path = same instance)
final Name api2 = cortex.name("service.api");
assert api == api2;  // ✓ Same instance
```

### Name Operations

```
// Get path representation
name.path();  // "service.api.latency"

// Get enclosure chain
name.enclosure();  // Optional<Name> parent

// Fold over hierarchy
name.fold(0, (depth, n) -> depth + 1);  // Count levels

// Extension
name.name("segment");  // Append segment
```

---

## Subscribers and Reservoirs

### Dynamic Subscription Model

Subscribers are notified when channels are **created or rebuilt**:

```
final Subscriber<Integer> subscriber = cortex.subscriber(
  cortex.name("my.subscriber"),
  (subject, registrar) -> {
    System.out.println("Channel discovered: " + subject.name());

    // Register a pipe to receive emissions
    registrar.register(
      cortex.pipe(value -> System.out.println("Received: " + value))
    );
  }
);

// Subscribe to conduit
final Subscription subscription = conduit.subscribe(subscriber);

// Later: unsubscribe
subscription.unsubscribe();
```

**Important: @Temporal Constraint**

The `Registrar` parameter is marked `@Temporal` - **don't retain it beyond the callback**:

```
// ❌ BAD: Storing registrar for later use
private Registrar savedRegistrar;

subscriber = cortex.subscriber(name, (subject, registrar) -> {
  savedRegistrar = registrar;  // Don't do this!
});

// ✓ GOOD: Use registrar immediately
subscriber = cortex.subscriber(name, (subject, registrar) -> {
  registrar.register(pipe);  // Use within callback
});
```

### Reservoir Observations

Reservoirs provide **incremental observation** of emissions:

```
final Reservoir<Integer> reservoir = cortex.reservoir(conduit);

pipe.emit(1);
pipe.emit(2);
circuit.await();

// First drain
List<Capture<Integer>> captures1 = reservoir.drain().toList();
// captures1 = [1, 2]

pipe.emit(3);
circuit.await();

// Second drain (incremental!)
List<Capture<Integer>> captures2 = reservoir.drain().toList();
// captures2 = [3]  (NOT [1, 2, 3])
```

**Key Point**: `drain()` is **incremental**, returning only new emissions since last drain.

**Capture Structure:**

```
capture.subject();   // Name - which channel emitted
capture.emission();  // E - the emitted value
```

---

## Flow Operators

Flow operators enable **type-preserving transformations** on emissions:

### Available Operators

```
// Filtering
flow.guard(value -> value > 0)           // Only positive values
flow.sift((value, sift) -> {             // Selective multicasting
  if (value > 100) sift.accept(value);
})

// Sampling
flow.sample(2)                           // Every 2nd emission
flow.limit(100)                          // First 100 emissions
flow.skip(10)                            // Skip first 10

// Stateful
flow.diff()                              // Only emit if changed
flow.reduce(0, Integer::sum)             // Accumulate values

// Transformation
flow.replace(x -> x * 2)                 // Map values

// Observation
flow.peek(System.out::println)           // Observe without modifying
flow.forward(otherPipe::emit)            // Tee emissions
```

### Operator Composition

Operators compose left-to-right:

```
final Conduit<Pipe<Integer>> conduit = circuit.conduit(
  cortex.name("pipeline"),
  Composer.pipe(Integer.class),
  flow -> flow
    .diff()                    // 1. Only changed values
    .guard(v -> v > 0)         // 2. Only positive
    .sample(2)                 // 3. Every 2nd emission
    .limit(100)                // 4. Max 100 total
    .reduce(0, Integer::sum)   // 5. Accumulate
    .peek(System.out::println) // 6. Observe
);
```

### Stateful Operator Behavior

**Important**: Operator state is **shared across all channels** in the conduit:

```
final Conduit<Pipe<Integer>> conduit = circuit.conduit(
  Composer.pipe(Integer.class),
  flow -> flow.diff()  // Shared diff state
);

final Pipe<Integer> pipe1 = conduit.percept(cortex.name("channel1"));
final Pipe<Integer> pipe2 = conduit.percept(cortex.name("channel2"));

pipe1.emit(1);  // Emitted (first value)
pipe1.emit(1);  // Dropped (duplicate)
pipe2.emit(1);  // Dropped (duplicate - shared state!)
pipe2.emit(2);  // Emitted (changed)
```

**Workaround**: Use separate conduits for independent state:

```
final Conduit<Pipe<Integer>> conduit1 = circuit.conduit(
  cortex.name("conduit1"),
  Composer.pipe(Integer.class),
  flow -> flow.diff()
);

final Conduit<Pipe<Integer>> conduit2 = circuit.conduit(
  cortex.name("conduit2"),
  Composer.pipe(Integer.class),
  flow -> flow.diff()
);

// Now pipe1 and pipe2 have independent diff state
```

---

## Building Neural-Like Networks

Substrates enables **stack-safe deep networks** and **feedback loops**:

### Deep Hierarchical Chains

```
// Create 1000-deep chain without stack overflow
Pipe<Integer> tail = cortex.pipe(value ->
  System.out.println("Reached depth 1000: " + value)
);

for (int i = 0; i < 1000; i++) {
  final Pipe<Integer> next = tail;
  tail = circuit.pipe(next);  // Async boundary prevents stack overflow
}

tail.emit(42);
circuit.await();
// Output: Reached depth 1000: 42
```

**How it works:**

- `circuit.pipe(pipe)` creates an **async boundary**
- Each emission is **queued**, not called recursively
- Prevents stack overflow even with arbitrary depth

### Cyclic Networks

```
// Self-referential pipe (feedback loop)
final Pipe<Integer>[] cycle = new Pipe[1];
cycle[0] = circuit.pipe(
  cortex.pipe(value -> {
    System.out.println("Value: " + value);
    if (value < 10) {
      cycle[0].emit(value + 1);  // Recursive emission
    }
  })
);

cycle[0].emit(1);
circuit.await();
// Output: Value: 1, Value: 2, ..., Value: 10
```

**Transit Queue Priority** prevents deadlock:

- Emissions from within circuit thread go to **transit queue**
- Transit queue processed **before** ingress queue
- Enables depth-first recursive processing

### Multi-Node Cycles

```
// A → B → C → A feedback loop
final AtomicInteger counter = new AtomicInteger(0);

final Pipe<Integer>[] pipes = new Pipe[3];

pipes[0] = circuit.pipe(cortex.pipe(value -> {
  if (counter.incrementAndGet() < 10) {
    pipes[1].emit(value + 1);  // A → B
  }
}));

pipes[1] = circuit.pipe(cortex.pipe(value -> {
  pipes[2].emit(value + 1);  // B → C
}));

pipes[2] = circuit.pipe(cortex.pipe(value -> {
  pipes[0].emit(value + 1);  // C → A (cycle!)
}));

pipes[0].emit(0);
circuit.await();
// Produces: 0→1→2→3→4→5→6→7→8→9→10
```

### Cells for Complex Hierarchies

**Cell** provides async boundaries for nested structures:

```
final Cell cell = circuit.cell(
  cortex.name("nested.cell"),
  cortex.pipe(value -> {
    // Complex processing
    // Can emit to other pipes safely
  })
);

cell.emit(value);  // Queued to cell's internal circuit
```

Cells enable:

- **Nested circuits** with independent threads
- **Hierarchical decomposition** of complex systems
- **Isolation** between subsystems

---

## Observability with Serventis

**Serventis** is a comprehensive observability extension providing **semantic signal emission** for
monitoring and coordination.

### Overview: Semantic Signals

Rather than raw metrics, Serventis emits **signals with domain meaning**:

```
Raw Signals (Counters, Gauges, Probes)
    ↓
Monitors (STABLE, DEGRADED, DOWN)
    ↓
Reporters (NORMAL, WARNING, CRITICAL)
    ↓
Actions (automated responses)
```

All Serventis signals follow a **uniform pattern**:

```
Signal = Sign × Dimension

Where:
- Sign: Semantic classification (CONNECT, STABLE, OFFER, etc.)
- Dimension: Qualifying context (RELEASE/RECEIPT, confidence, perspective)
```

### The 12 Semantic Instruments

#### 1. Counters - Monotonic Accumulation

**Signs**: `INCREMENT`, `OVERFLOW`, `RESET`

```
import io.humainary.substrates.ext.serventis.Counters;

final var conduit = circuit.conduit(Counters::composer);
final var requests = conduit.percept(cortex.name("requests.total"));

// Emit signals
requests.increment();  // Track request
requests.overflow();   // Capacity exceeded
requests.reset();      // Counter reset

circuit.await();
```

**Use Cases:**

- Request counts
- Event totals
- Monotonic metrics

#### 2. Gauges - Bidirectional Values

**Signs**: `INCREMENT`, `DECREMENT`, `OVERFLOW`, `UNDERFLOW`, `RESET`

```
import io.humainary.substrates.ext.serventis.Gauges;

final var conduit = circuit.conduit(Gauges::composer);
final var connections = conduit.percept(cortex.name("active.connections"));

connections.increment();  // Connection opened
connections.decrement();  // Connection closed
connections.overflow();   // Pool exhausted
connections.underflow();  // Pool underutilized
```

**Use Cases:**

- Connection pools
- Queue depth
- Thread utilization

#### 3. Caches - Cache Interactions

**Signs**: `LOOKUP`, `HIT`, `MISS`, `STORE`, `EVICT`, `EXPIRE`, `REMOVE`

```
import io.humainary.substrates.ext.serventis.Caches;

final var conduit = circuit.conduit(Caches::composer);
final var cache = conduit.percept(cortex.name("user.cache"));

cache.lookup();   // Cache access
cache.hit();      // Found in cache
cache.miss();     // Not found
cache.store();    // Add to cache
cache.evict();    // Removed due to capacity
cache.expire();   // Removed due to TTL
cache.remove();   // Explicitly removed
```

**Lifecycle**: `LOOKUP → (HIT | MISS) → STORE → (EVICT | EXPIRE | REMOVE)`

#### 4. Probes - Communication Outcomes

**Signs**: `CONNECT`, `DISCONNECT`, `TRANSMIT`, `RECEIVE`, `PROCESS`, `SUCCEED`, `FAIL`
**Dimensions**: `RELEASE` (self), `RECEIPT` (observed)

```
import io.humainary.substrates.ext.serventis.Probes;

final var conduit = circuit.conduit(Probes::composer);
final var probe = conduit.percept(cortex.name("api.client"));

// Self-perspective (RELEASE)
probe.connect();      // I am connecting
probe.transmit();     // I am sending
probe.succeed();      // I succeeded

// Observed-perspective (RECEIPT)
probe.connected();    // Remote connected
probe.transmitted();  // Remote sent
probe.succeeded();    // Remote succeeded

// Error handling
probe.fail();         // I failed
probe.failed();       // Remote failed
```

**Use Cases:**

- RPC instrumentation
- Network diagnostics
- Distributed tracing

#### 5. Monitors - Condition Assessment

**Signs**: `CONVERGING`, `STABLE`, `DIVERGING`, `ERRATIC`, `DEGRADED`, `DEFECTIVE`, `DOWN`
**Dimensions**: `TENTATIVE`, `MEASURED`, `CONFIRMED` (confidence)

```
import io.humainary.substrates.ext.serventis.Monitors;
import static io.humainary.substrates.ext.serventis.Monitors.Dimension.*;

final var conduit = circuit.conduit(Monitors::composer);
final var monitor = conduit.percept(cortex.name("database"));

// Initial stable state
monitor.stable(CONFIRMED);

// Degradation with confidence progression
monitor.diverging(TENTATIVE);   // Initial observation
monitor.diverging(MEASURED);    // Strong evidence
monitor.degraded(CONFIRMED);    // Definitive

// Recovery
monitor.converging(MEASURED);   // Stabilizing
monitor.stable(CONFIRMED);      // Back to normal
```

**Confidence Progression** enables graduated response:

- `TENTATIVE`: Observe, increase sampling, defer action
- `MEASURED`: Alert operators, prepare response
- `CONFIRMED`: Execute automated remediation

**Use Cases:**

- Service health dashboards
- Anomaly detection
- Adaptive circuit breakers

#### 6. Services - Service Lifecycle

**Signs**: `START`, `STOP`, `CALL`, `SUCCESS`, `FAIL`, `DELAY`, `SCHEDULE`, `SUSPEND`, `RESUME`,
`RETRY`, `RECOURSE`, `REDIRECT`, `REJECT`, `DISCARD`, `DISCONNECT`, `EXPIRE`
**Dimensions**: `RELEASE` (self), `RECEIPT` (observed)

```
import io.humainary.substrates.ext.serventis.Services;

final var conduit = circuit.conduit(Services::composer);
final var service = conduit.percept(cortex.name("payment.service"));

service.start();      // Service starting
service.call();       // Calling remote
service.delay();      // Experiencing latency
service.retry();      // Retrying failed call
service.success();    // Completed successfully

// Or failure path
service.fail();       // Failed
service.recourse();   // Attempting recovery
service.redirect();   // Redirecting to fallback
```

**Use Cases:**

- Distributed tracing
- Request lifecycle tracking
- Work execution monitoring

#### Other Instruments

**7. Routers** - Packet operations (SEND, RECEIVE, FORWARD, ROUTE, DROP, etc.)
**8. Queues** - Queue flow (ENQUEUE, DEQUEUE, OVERFLOW, UNDERFLOW)
**9. Resources** - Resource acquisition (ATTEMPT, ACQUIRE, GRANT, DENY, TIMEOUT, RELEASE)
**10. Reporters** - Situational assessment (NORMAL, WARNING, CRITICAL)
**11. Actors** - Speech act communication (ASK, AFFIRM, REQUEST, COMMAND, PROMISE, etc.)
**12. Agents** - Promise-based coordination (OFFER, PROMISE, ACCEPT, FULFILL, BREACH, etc.)

### Complete Observability Example

```
import io.humainary.substrates.ext.serventis.*;
import static io.humainary.substrates.ext.serventis.Monitors.Dimension.*;

// Create instrumentation conduits
final var counterConduit = circuit.conduit(Counters::composer);
final var probeConduit = circuit.conduit(Probes::composer);
final var monitorConduit = circuit.conduit(Monitors::composer);

// Create instruments
final var requestCounter = counterConduit.percept(cortex.name("requests"));
final var errorCounter = counterConduit.percept(cortex.name("errors"));
final var apiProbe = probeConduit.percept(cortex.name("api.client"));
final var dbMonitor = monitorConduit.percept(cortex.name("database"));

// Create observer
final var observer = cortex.subscriber(
  cortex.name("observer"),
  (subject, registrar) -> {
    registrar.register(
      cortex.pipe(signal -> {
        // Aggregate raw signals
        if (errorRate() > 0.05) {
          dbMonitor.degraded(TENTATIVE);
        }
      })
    );
  }
);

counterConduit.subscribe(observer);
probeConduit.subscribe(observer);

// Application code emits signals
void handleRequest() {
  requestCounter.increment();

  apiProbe.connect();
  try {
    apiProbe.transmit();
    // ... call remote service ...
    apiProbe.received();
    apiProbe.succeed();

  } catch (Exception e) {
    errorCounter.increment();
    apiProbe.fail();
    dbMonitor.degraded(CONFIRMED);
  } finally {
    apiProbe.disconnect();
  }
}

// Process signals
circuit.await();

// Observe conditions
final var monitorReservoir = cortex.reservoir(monitorConduit);
monitorReservoir.drain().forEach(capture -> {
  if (capture.emission().sign() == Monitors.Sign.DEGRADED) {
    triggerCircuitBreaker();
  }
});
```

---

## Best Practices

### 1. Always Close Resources

```
// ✓ GOOD: try-finally
final Circuit circuit = cortex.circuit();
try {
  // Use circuit
} finally {
  circuit.close();
}

// ✓ GOOD: try-with-resources (if Circuit implements AutoCloseable)
try (final Circuit circuit = cortex.circuit()) {
  // Use circuit
}

// ❌ BAD: No cleanup
final Circuit circuit = cortex.circuit();
// Resource leak!
```

### 2. Use Scopes for Lifecycle Management

```
final Scope scope = cortex.scope(cortex.name("app.scope"));

final Circuit c1 = scope.register(cortex.circuit());
final Circuit c2 = scope.register(cortex.circuit());

// Single call closes all registered resources
scope.close();
```

### 3. Subscribe Before Creating Channels

**Forward subscription** ensures subscribers see all channels:

```
// ✓ GOOD: Subscribe first
conduit.subscribe(subscriber);
final Pipe<Integer> pipe = conduit.percept(name);  // Subscriber notified

// ⚠ CAUTION: Subscribe after
final Pipe<Integer> pipe = conduit.percept(name);  // Subscriber not notified
conduit.subscribe(subscriber);
// Subscriber sees future channels only
```

### 4. Use await() for Synchronization

```
// Emit multiple values
pipe.emit(1);
pipe.emit(2);
pipe.emit(3);

// Wait for processing before assertions
circuit.await();

// Now safe to check results
assertEquals(3, reservoir.drain().count());
```

### 5. Leverage Name Pooling

```
// Names are interned - reuse them
final Name name = cortex.name("requests.total");

// Same name = same channel instance
final Pipe<Integer> pipe1 = conduit.percept(name);
final Pipe<Integer> pipe2 = conduit.percept(name);
assert pipe1 == pipe2;  // ✓ Identity-based equality
```

### 6. Use Separate Conduits for Independent State

```
// Shared state (single conduit)
final Conduit<Pipe<Integer>> sharedConduit = circuit.conduit(
  Composer.pipe(Integer.class),
  flow -> flow.diff()  // Shared diff state
);

// Independent state (multiple conduits)
final Conduit<Pipe<Integer>> conduit1 = circuit.conduit(
  cortex.name("conduit1"),
  Composer.pipe(Integer.class),
  flow -> flow.diff()
);

final Conduit<Pipe<Integer>> conduit2 = circuit.conduit(
  cortex.name("conduit2"),
  Composer.pipe(Integer.class),
  flow -> flow.diff()
);
```

### 7. Respect @Temporal Contracts

Several interfaces are marked `@Temporal` and must not be retained outside their valid scope:

**Callback-Scoped Temporals:**

```
// ❌ BAD: Storing Channel from Composer
private Channel<?> savedChannel;

conduit = circuit.conduit(channel -> {
  savedChannel = channel;  // Undefined behavior!
  return channel.pipe();
});

// ✓ GOOD: Use Channel immediately, retain the Pipe
conduit = circuit.conduit(channel -> {
  Pipe<?> pipe = channel.pipe();  // Retain this, not channel
  return pipe;
});
```

```
// ❌ BAD: Storing Registrar from Subscriber
private Registrar<?> savedRegistrar;

subscriber = cortex.subscriber(name, (subject, registrar) -> {
  savedRegistrar = registrar;  // Undefined behavior!
});

// ✓ GOOD: Use Registrar immediately
subscriber = cortex.subscriber(name, (subject, registrar) -> {
  registrar.register(pipe);  // Use within callback
});
```

**Thread-Scoped Temporals:**

```
// ❌ BAD: Storing Current for use from other threads
private Current savedCurrent;

void threadA() {
  savedCurrent = cortex.current();  // Don't retain!
}

void threadB() {
  Subject subject = savedCurrent.subject();  // Undefined behavior!
}

// ✓ GOOD: Call current() from each thread
void threadA() {
  Current current = cortex.current();  // Use in this thread
  processInThreadA(current);
}

void threadB() {
  Current current = cortex.current();  // Get fresh reference
  processInThreadB(current);
}
```

**Summary of Temporal Contracts:**

- **Channel**: Valid only during `Composer.compose()` callback
- **Registrar**: Valid only during `Subscriber` callback
- **Current**: Valid only within the thread that obtained it
- **Sift**: Valid only during sift operation callback

### 8. Handle Errors Gracefully

```
pipe.emit(
  cortex.pipe(value -> {
    try {
      processValue(value);
    } catch (Exception e) {
      // Log error, emit error signal, etc.
      errorCounter.increment();
      // Don't let exceptions escape - circuit may be affected
    }
  })
);
```

---

## Common Pitfalls

### 1. Calling await() from Circuit Thread

**Problem:**

```
pipe.emit(
  cortex.pipe(value -> {
    circuit.await();  // ❌ IllegalStateException!
  })
);
```

**Why**: Circuit thread would wait for itself (deadlock)

**Solution**: Use transit queue priority for recursive synchronization

### 2. Expecting Cumulative Reservoir Drains

**Problem:**

```
pipe.emit(1);
circuit.await();
reservoir.drain();  // [1]

pipe.emit(2);
circuit.await();
reservoir.drain();  // Expected [1, 2], got [2]!
```

**Why**: `drain()` is **incremental**, not cumulative

**Solution**: Accumulate manually if needed:

```
final List<Integer> all = new ArrayList<>();
all.addAll(reservoir.drain().map(Capture::emission).toList());
// Repeat...
all.addAll(reservoir.drain().map(Capture::emission).toList());
```

### 3. Forgetting to Call await()

**Problem:**

```
pipe.emit(value);
// Check results immediately - queue not processed yet!
assertFalse(reservoir.drain().isEmpty());  // ❌ May fail!
```

**Solution**: Always call `await()` before observations

```
pipe.emit(value);
circuit.await();  // ✓ Wait for processing
assertFalse(reservoir.drain().isEmpty());
```

### 4. Sharing Stateful Operators

**Problem:**

```
final Conduit<Pipe<Integer>> conduit = circuit.conduit(
  Composer.pipe(Integer.class),
  flow -> flow.diff()  // Shared state!
);

final Pipe<Integer> pipe1 = conduit.percept(cortex.name("ch1"));
final Pipe<Integer> pipe2 = conduit.percept(cortex.name("ch2"));

pipe1.emit(1);  // Emitted
pipe2.emit(1);  // ❌ Dropped (diff sees duplicate)!
```

**Solution**: Use separate conduits or understand shared state semantics

### 5. Emitting After Circuit Close

**Problem:**

```
circuit.close();
pipe.emit(value);  // ❌ Silently dropped!
```

**Solution**: Check `isClosed()` or ensure emissions complete before close:

```
pipe.emit(value);
circuit.await();
circuit.close();
```

### 6. Invalid Name Segments

**Problem:**

```
cortex.name("a..b");   // ❌ Empty segment
cortex.name(".start"); // ❌ Leading dot
cortex.name("end.");   // ❌ Trailing dot
```

**Solution**: Use valid identifiers:

```
cortex.name("a.b");        // ✓
cortex.name("valid-name"); // ✓
cortex.name("with_under"); // ✓
```

### 7. Not Closing Resources

**Problem:**

```
final Circuit c = cortex.circuit();
// ... use circuit ...
// ❌ Never closed - resource leak!
```

**Solution**: Always use try-finally or scopes

---

## Advanced Patterns

### 1. Dynamic Topology Reconfiguration

```
// Add subscriber dynamically
final Subscription sub = conduit.subscribe(newSubscriber);

// Emit values (subscriber receives)
pipe.emit(value);

// Remove subscriber dynamically
sub.unsubscribe();

// Emit values (subscriber no longer receives)
pipe.emit(value);
```

### 2. Multi-Circuit Coordination

```
final Circuit c1 = cortex.circuit(cortex.name("circuit1"));
final Circuit c2 = cortex.circuit(cortex.name("circuit2"));

final Conduit<Pipe<Integer>> conduit1 = c1.conduit(Composer.pipe(Integer.class));
final Conduit<Pipe<Integer>> conduit2 = c2.conduit(Composer.pipe(Integer.class));

final Pipe<Integer> pipe1 = conduit1.percept(cortex.name("ch1"));
final Pipe<Integer> pipe2 = conduit2.percept(cortex.name("ch2"));

// Cross-circuit emission
pipe1.emit(
  cortex.pipe(value -> {
    pipe2.emit(value * 2);  // c1 → c2
  })
);

// Wait for both circuits
c1.await();
c2.await();
```

### 3. Immutable State Composition

```
// Build state incrementally
State state = cortex.state()
  .state(cortex.name("alpha"), 1)
  .state(cortex.name("beta"), 2)
  .state(cortex.name("gamma"), 3);

// Query state
state.state(cortex.name("alpha"), Integer.class);  // Optional[1]

// Update (returns new state, original unchanged)
State updated = state.state(cortex.name("alpha"), 10);

// Compact (remove duplicates, keep latest)
State compacted = updated.compact();
```

### 4. Conditional Flow Processing

```
final Conduit<Pipe<Integer>> conduit = circuit.conduit(
  Composer.pipe(Integer.class),
  flow -> flow.sift((value, sift) -> {
    if (value > 100) {
      // Emit to high-priority channel
      highPriorityPipe.emit(value);
    } else if (value > 10) {
      // Emit to normal channel
      normalPipe.emit(value);
    }
    // Else: drop (don't call sift.accept)
  })
);
```

### 5. Observability Stack

```
// Layer 1: Raw signals
final var counterConduit = circuit.conduit(Counters::composer);
final var requests = counterConduit.percept(cortex.name("requests"));
final var errors = counterConduit.percept(cortex.name("errors"));

// Layer 2: Condition assessment
final var monitorConduit = circuit.conduit(Monitors::composer);
final var monitor = monitorConduit.percept(cortex.name("service"));

// Layer 3: Observer aggregation
final var observer = cortex.subscriber(
  cortex.name("aggregator"),
  (subject, registrar) -> {
    registrar.register(cortex.pipe(sign -> {
      // Aggregate counters → monitor
      double errorRate = errorCount / (double) requestCount;
      if (errorRate > 0.05) {
        monitor.degraded(Monitors.Dimension.MEASURED);
      } else {
        monitor.stable(Monitors.Dimension.CONFIRMED);
      }
    }));
  }
);

counterConduit.subscribe(observer);

// Layer 4: Automated actions
final var actionObserver = cortex.subscriber(
  cortex.name("actions"),
  (subject, registrar) -> {
    registrar.register(cortex.pipe(signal -> {
      if (signal.sign() == Monitors.Sign.DEGRADED) {
        circuitBreaker.open();
      }
    }));
  }
);

monitorConduit.subscribe(actionObserver);
```

---

## Performance Considerations

### Benchmarking

Substrates provides comprehensive JMH benchmarks for all components. Understanding benchmark types
helps you measure and optimize your code effectively.

#### Benchmark Types

**1. Hot-Path Benchmarks**

Hot-path benchmarks isolate operation costs from lifecycle overhead:

```bash
# Measure conduit creation on already-running circuit
substrates/jmh.sh CircuitOps.hot_conduit_create
```

These use `@Setup(Level.Iteration)` to create resources once per iteration, measuring only the
operation without including setup/teardown costs. Perfect for understanding steady-state performance
in long-lived circuits.

**2. Batched Benchmarks**

Batched benchmarks measure amortized per-operation cost:

```bash
# Measure amortized cost over 1000 operations
substrates/jmh.sh CounterOps.emit_increment_batch
```

These use `@OperationsPerInvocation(1000)` to execute operations in tight loops, reducing
measurement noise for fast operations (< 10ns). The reported time is the average per operation.

**3. Single-Operation Benchmarks**

Single-operation benchmarks measure full cost including all overhead:

```bash
# Measure individual operation including overhead
substrates/jmh.sh CounterOps.emit_increment
```

Use these to establish baselines and compare against batched results.

#### Running Benchmarks

```bash
# Run all benchmarks
substrates/jmh.sh

# List available benchmarks
substrates/jmh.sh -l

# Run specific benchmark
substrates/jmh.sh PipeOps.emit_to_empty_pipe

# Run all batched benchmarks
substrates/jmh.sh ".*batch"

# Custom JMH parameters (5 warmup iterations, 10 measurement iterations, 2 forks)
substrates/jmh.sh -wi 5 -i 10 -f 2
```

#### Interpreting Results

**Hot-Path vs. Cold-Path**:

```
CircuitOps.create_and_close:        290 ns/op    (full lifecycle)
CircuitOps.hot_conduit_create:      ~4 ns/op     (operation only)
```

The 286ns difference is circuit creation/teardown overhead.

**Batched vs. Single**:

```
CounterOps.emit_increment:          8.1 ns/op    (single)
CounterOps.emit_increment_batch:    ~7.5 ns/op   (amortized over 1000)
```

Batching reduces per-operation overhead through better CPU cache utilization.

See `BENCHMARKS.md` for complete results and detailed analysis.

### Emission Latency

Substrates achieves **single-digit nanosecond latency** through:

1. **Transit Queue Priority**: In-circuit emissions bypass ingress queue (~3ns)
2. **Lock-Free Operations**: No synchronization in hot paths (~20ns for ingress)
3. **Zero Allocation**: Enum-based signals reuse cached instances
4. **Name Interning**: O(1) reference-based identity

**Measured Latency** (from JMH benchmarks):

**IMPORTANT**: These are **emission costs** (what the caller pays), not round-trip times. In
production, emissions are asynchronous - you don't wait for processing. The `await()` call is
primarily for testing/synchronization.

**Emission Cost** (Production):

- **Empty pipe**: ~0.44ns (PipeOps.emit_to_empty_pipe) - Synchronous discard
- **Receptor pipe**: ~0.65ns (PipeOps.emit_to_receptor_pipe) - Synchronous callback
- **Async pipe**: ~7.9ns (PipeOps.emit_to_async_pipe) - Enqueue to circuit (**typical production
  path**)
- **Counter signal**: ~8.1ns (CounterOps.emit_increment) - Serventis emission

**Round-Trip Cost** (Testing only):

- **Emission + await**: ~5,837ns (PipeOps.emit_with_await) - Includes queue drain and
  synchronization

The ~7.9ns async pipe emission is what you pay in production for crossing the circuit boundary.
The ~5,837ns round-trip includes `await()` overhead that doesn't occur in normal production flow
where emissions are fire-and-forget.

### Throughput Optimization

**Target Rates:**

- **High-frequency**: Counters, Gauges, Caches (10M-50M Hz)
- **Request-level**: Probes, Services (100-100K Hz)
- **Condition-level**: Monitors (100K-1M Hz)

**Tips for Maximum Throughput:**

1. **Use Transit Queue**: Emit recursively from circuit thread
2. **Batch Processing**: Process groups before `await()`
3. **Minimize Flow Operators**: Each operator adds overhead
4. **Reuse Names**: Name interning enables O(1) lookup
5. **Use Virtual Threads**: Each circuit uses one virtual thread (cheap)

### Memory Optimization

1. **Signal Caching**: Pre-compute all Sign × Dimension combinations

```
// Monitors caches all 7 signs × 3 dimensions = 21 signals
private static final Signal[][] SIGNALS = /* ... */;

public void stable(Dimension dimension) {
  pipe.emit(SIGNALS[STABLE.ordinal()][dimension.ordinal()]);
  // Zero allocation
}
```

2. **State Compaction**: Remove duplicates from long-lived states

```
State compacted = state.compact();  // Deduplicate
```

3. **Scoped Resources**: Automatic cleanup prevents leaks

```
final Scope scope = cortex.scope(name);
// Register resources...
scope.close();  // All cleaned up
```

### Concurrency

1. **Single-Threaded Circuits**: No locks needed within circuit
2. **Multiple Circuits**: Parallel processing via independent circuits
3. **Virtual Threads**: Cheap to create thousands of circuits

---

## Summary

Substrates provides a powerful foundation for building **neural-like computational networks** with:

- **Deterministic execution** through single-threaded circuits
- **Lock-free concurrency** via async queuing
- **Dynamic topology** with runtime subscription changes
- **Stack-safe deep networks** enabling arbitrary hierarchies
- **Extreme performance** with single-digit nanosecond latency

The **Serventis extension** adds comprehensive observability with:

- **12 semantic instruments** for domain-specific signals
- **Layered hierarchy** from raw signals to automated actions
- **Dual-perspective signaling** for self-reporting and observation
- **Confidence-based response** for graduated remediation

By understanding the core concepts, following best practices, and avoiding common pitfalls, you can
build high-performance, event-driven systems with rich observability and adaptive behavior.

---

## Next Steps

1. **Build the project**: `./mvnw clean install`
2. **Run TCK tests**: `./mvnw clean install -Dtck`
3. **Explore examples**: Check `tck/src/test/java` for comprehensive usage examples
5. **Build your own**: Create custom percepts and composers

For more information, visit [Humainary.io](https://humainary.io)
