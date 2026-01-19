# Substrates Glossary

This glossary provides definitions of core concepts in the Humainary Substrates API, organized by
functional area.

## Entry Point

* **Cortex**: The factory entry point for the Substrates framework. Provides methods for creating
  circuits (`circuit()`), hierarchical names (`name()`), scopes (`scope()`), slots (`slot()`),
  and states (`state()`). Access via `Substrates.cortex()`.

* **Current**: Represents the execution context (thread, coroutine, fiber) from which substrate
  operations originate. Obtained via `Cortex.current()` in a manner analogous to
  `Thread.currentThread()`. **Temporal contract**: Only valid within the execution context (thread)
  that obtained it. Must not be retained or used from different threads.

## Runtime Engine

* **Circuit**: The central processing engine that manages data flow with strict ordering guarantees.
  Each circuit owns exactly one processing thread (virtual thread) that sequentially processes all
  emissions. Manages two internal queues:
    * **Ingress Queue**: Shared queue for emissions from external threads (requires synchronization)
    * **Transit Queue**: Local queue for emissions from within the circuit thread itself (lock-free,
      priority over ingress)

  The transit queue enables **cascading emission ordering** where nested emissions complete before
  processing the next external emission, ensuring causality preservation and enabling neural-like
  signal propagation.

* **Valve**: Single-threaded executor managing the circuit's job queue with a virtual thread worker.
  Responsible for draining emissions deterministically from both ingress and transit queues.

## Data Flow Components

* **Conduit**: A percept factory that routes emitted values from channels to pipes. Created
  using `Circuit.conduit(Composer)`. Caches percepts by name (via Lookup interface), ensuring
  stable identity and routing guarantees. Acts as the bridge between raw channels and
  domain-specific instruments.

* **Tap**: A transformed view of a conduit's emissions. Created via `Conduit.tap(Function)` or
  `Conduit.tap(Function, Configurer)`. Mirrors the channel structure of its source conduit but
  transforms emissions from type E to type T using the provided mapper function. Optionally accepts
  a Flow configurer for filtering and stateful operations on the transformed emissions. Extends
  `Source` for subscription and `Resource` for lifecycle management. Must be closed when no longer
  needed to unsubscribe from the source conduit.

* **Channel**: A subject-based port that serves as an entry point into a conduit's pipeline.
  Channels are created lazily on first access by name. Channels emit values that flow through the
  conduit's pipeline to registered subscriber pipes. **Temporal contract**: Channel references are
  only valid during the `Composer.compose()` callback and must not be retained. Instead, retain the
  pipe returned by `channel.pipe()`.

* **Cell**: A hierarchical computational unit supporting arbitrarily deep nested structures.
  Combines
  four capabilities: `Pipe` (receives typed input), `Lookup` (creates/caches child cells by name),
  `Source` (emits child outputs for observation), and `Extent` (iterates over child cells). Cells
  enable stack-safe deep hierarchies (100+ levels) using async dispatch through hub subscriptions,
  breaking synchronous call chains. Created via `Circuit.cell(Composer, Composer, Receptor)`.

* **Pipe**: An emission carrier responsible for passing typed values through pipelines. Extends the
  `Percept` and `Substrate` interfaces, providing identity via `subject()`. Created via
  `Circuit.pipe(Receptor)` to wrap callbacks for receiving emissions. Provides `emit(E)` for
  sending values.

* **Flow**: A type-preserving processing pipeline for filtering and stateful operations. Supports
  operations like `diff` (deduplication), `guard` (filtering), `limit` (backpressure), `sample` (
  rate limiting), `sift` (range checking), `reduce` (aggregation), `peek` (observation), `replace`
  (value mapping), and `skip` (drop first N). Flow operations maintain the same type throughout
  (e.g., Integer → Integer). **Temporal contract**: Flow instances are only valid during
  configuration callbacks.

* **Sift**: A temporal builder for comparison-based filtering operations within a Flow pipeline.
  Provides filters based on value comparisons: `above`/`below` (exclusive bounds), `min`/`max`
  (inclusive bounds), `range` (inclusive range), and `high`/`low` (extrema detection for new
  records). Configured via `Flow.sift(Comparator, Configurer)`.

## Observation & Subscription

* **Receptor**: A callback interface for receiving emissions. Domain-specific alternative to
  `java.util.function.Consumer` with a guaranteed non-null contract. Used to create receptor pipes
  that receive emitted values. Provides factory methods like `Receptor.of()` and `Receptor.NOOP`.

* **Percept**: A marker interface for all observable entities, such as pipes and domain-specific
  instruments (counters, gauges, monitors, etc.). Used to enforce type-safe constraints in composers
  and enable framework extensibility.

* **Composer**: A functional interface that composes a percept from a channel. The core mechanism
  for creating domain-specific abstractions around channels in a conduit. Called exactly once per
  channel name (channels are pooled).

* **Configurer**: A callback functional interface for configuring temporal objects during their
  initialization phase. Primarily used with Flow configuration. The target is only valid during
  callback execution and must not be retained.

* **Subscriber**: A component that dynamically subscribes to sources and registers pipes with
  channel subjects via a `Registrar`. Created via `Circuit.subscriber()`, making it a child of
  the circuit in the subject hierarchy (subject path: `circuit-name.subscriber-name`). This
  establishes that the subscriber observes the ordered view provided by that circuit's
  single-threaded processing. Invoked lazily on first emission to discovered channels, enabling
  adaptive topologies that respond to runtime structure. Extends `Resource` for lifecycle
  management.

* **Subscription**: A lifecycle handle returned when subscribing to a source. Provides `close()` to
  unregister pipes from channels. Cancellation uses lazy rebuild - changes take effect on the next
  emission, not immediately.

* **Registrar**: A temporary handle passed to subscribers during their callback, allowing them to
  attach pipes to channels. Provides `register(Pipe)` and `register(Receptor)`. **Temporal
  contract**: Only valid during the subscriber callback - attempting to use it afterwards violates
  the temporal contract.

* **Source**: An interface that allows components to expose their events for subscription. Manages
  the subscription model and connects subscribers to channels with lazy rebuild synchronization.
  Implemented by Circuit, Conduit, Cell, and Tap.

* **Lookup**: A read-only interface for looking up percept instances by name. Provides
  `percept(Name)`,
  `percept(Subject)`, and `percept(Substrate)` methods. Implemented by Conduit and Cell.

## Identity & Hierarchy

* **Subject**: A hierarchical reference system that provides identity, name, and state for every
  component in the Substrates framework. Every substrate component (circuit, conduit, channel, pipe)
  has a subject enabling precision targeting and observability. Parameterized by its owning
  substrate type for type-safe subject extraction.

* **Substrate**: Base interface for all substrate components that have an associated subject.
  Self-referential and parameterized by its own type, enabling typed subject extraction where
  `substrate.subject()` returns `Subject<ThisSubstrateType>`.

* **Id**: A unique identifier component of a Subject. Ensures each component can be distinguished
  even if names are identical. Uses reference equality (`==`) for O(1) comparison.

* **Name**: A hierarchical naming system using dot-separated segments (e.g.,
  `parent.child.grandchild`). Names are immutable, interned, and use identity-based equality (`==`).
  Created via `Cortex.name()` methods from strings, classes, enums, members, or iterables. Extends
  `Extent` for hierarchical traversal.

* **Extent**: An abstraction of hierarchically nested structures. Provides traversal methods like
  `enclosure()` (parent), `extremity()` (root), `depth()`, `fold()`/`foldTo()`, `path()`, and
  `within()`. Implemented by Name, Subject, Cell, and Scope.

* **State**: An immutable collection of named slots containing typed values. Supports persistent
  updates where operations return new instances. Provides `state(Name, value)` for adding slots,
  `value(Slot)` for lookup, `compact()` for deduplication, and iteration/streaming over slots.

* **Slot**: An opaque interface representing a variable within a State. Provides both a query key
  (name + type) and a fallback value. Matching occurs on slot name identity AND slot type. Created
  via `Cortex.slot()` methods for boolean, int, long, float, double, String, Name, State, and Enum.

## Resource Management

* **Resource**: A lifecycle interface for explicitly releasing resources and terminating operations.
  Provides an idempotent `close()` method. Implemented by Circuit, Subscription, Reservoir, and
  Subscriber. Does not extend `AutoCloseable` as most resources have indefinite lifetimes.

* **Scope**: A structured resource manager that automatically closes registered assets in reverse
  order (LIFO), enabling RAII-like lifetime control. Implements `AutoCloseable` for
  try-with-resources.
  Provides `register(Resource)` for scope-lifetime resources, `closure(Resource)` for block-scoped
  resources, and hierarchical child scopes via `scope()`. Extends `Extent` for parent-child
  relationships.

* **Closure**: A utility interface for scoping work performed against a resource. Provides
  block-scoped
  resource management where `consume(Consumer)` executes the consumer with the resource then
  automatically closes it. Single-use - once consumed, the closure is exhausted. **Temporal
  contract**: Only valid during the consume callback.

* **Reservoir**: An in-memory buffer that captures emissions along with their subjects. Created via
  `Source.reservoir()`. Provides `drain()` to retrieve accumulated emissions as a stream of
  `Capture` objects. Extends `Resource` for lifecycle management.

* **Capture**: A record of an emitted value from a channel with its associated subject. Produced by
  Reservoirs. Provides `emission()` for the value and `subject()` for the channel identity. Used
  for testing, debugging, telemetry, and replay.

## Threading Model

The Substrates framework uses a **single-threaded circuit execution** model:

* Each circuit owns exactly **one processing thread** (virtual thread)
* All emissions, flows, and subscriber callbacks execute **exclusively on that thread**
* **Deterministic ordering**: Emissions observed in strict enqueue order
* **No synchronization needed**: State touched only from circuit thread requires no locks
* **Sequential execution**: Only one operation executes at a time per circuit

**Caller vs Circuit Thread**:

* **Caller threads** (your code): Enqueue emissions, return immediately
* **Circuit thread** (executor): Dequeue and process emissions sequentially
* **Performance principle**: Balance work between caller (before enqueue) and circuit (after
  dequeue). The circuit thread is the bottleneck.

## Performance Characteristics

The Substrates API is designed for extreme performance:

* **~2.98ns emission latency** (approximately 336M operations/sec)
* **Zero-allocation hot paths** for enum-based sign emissions
* **Lock-free transit queue** for cascading emissions
* **Lazy rebuild** for subscription changes to avoid blocking

This enables:

* Billions of emissions through cyclic networks
* Real-time adaptive topologies
* Spiking neural network implementations
* Massive parallelism via multiple circuits with virtual threads

## Design Intent

The Substrates API enables **exploration of neural-like computational networks** where:

* **Circuits** act as processing nodes with different timescales
* **Channels** emit signals through dynamic connections
* **Subscribers** rewire topology in response to runtime structure
* **Flow operators** create temporal dynamics (diff, sample, limit)
* **Deterministic ordering** enables replay, testing, and digital twins

This supports emergent computation from substrate primitives with deterministic behavior suitable
for production systems.

## Flow Operations

### Flow Configuration: `Circuit.pipe(Pipe, Configurer<Flow>)` or

`Circuit.pipe(Receptor, Configurer<Flow>)`

* **Purpose**: Type-preserving filtering and stateful operations
* **Type behavior**: Maintains same type (e.g., `Integer` → `Integer`)
* **Execution**: Stateful, with lifecycle management on circuit thread
* **Example**: `circuit.pipe(target, flow -> flow.guard(x -> x > 0).diff())`

Flow operations execute on the circuit's worker thread after emissions are dequeued, providing
single-threaded execution guarantees. Stateful operators (diff, limit, reduce) can use mutable
state without synchronization.

## Temporal Contracts

Several Substrates interfaces follow **temporal contracts** - they are only valid within specific
execution scopes and must not be retained or used outside those scopes:

### Callback-Scoped Temporal Contracts

* **Channel**: Valid only during `Composer.compose(Channel)` callback
    * **Retain**: The `Pipe` returned by `channel.pipe()`, not the Channel itself
    * **Rationale**: Channels are framework-internal routing constructs

* **Registrar**: Valid only during `Subscriber` callback
    * **Retain**: The pipes you attach, not the Registrar itself
    * **Rationale**: Registrar provides a temporary attachment window

* **Flow**: Valid only during `Configurer<Flow>` callback
    * **Retain**: Nothing - configure the flow inline and let it go
    * **Rationale**: Flow builders may be pooled and reused; storing breaks optimizations

* **Sift**: Valid only during `Configurer<Sift>` callback (within Flow.sift())
    * **Retain**: Nothing - configure the sift inline and let it go
    * **Rationale**: Same as Flow - temporal builders with callback-scoped lifetime

* **Closure**: Valid only during `Closure.consume(Consumer)` callback
    * **Retain**: Nothing - single-use, resource closed when callback returns
    * **Rationale**: Provides ARM (Automatic Resource Management) semantics

### Thread-Scoped Temporal Contract

* **Current**: Valid only within the execution context (thread) that obtained it
    * **Retain**: Don't retain - call `Cortex.current()` when needed
    * **Rationale**: Represents thread-local execution state, like `Thread.currentThread()`

Violating temporal contracts by retaining and using these references outside their valid scope leads
to undefined behavior. The framework marks temporal interfaces with the `@Temporal` annotation.

## When to Use Substrates

**Good fit**:

* High-frequency event processing (millions+ events/sec)
* Deterministic ordering required (replay, testing, digital twins)
* Dynamic topology adaptation (auto-wiring, runtime discovery)
* Low-latency requirements (sub-microsecond overhead)
* Cyclic data flow (feedback loops, recurrent networks)

**Not a good fit**:

* Simple request/response patterns (use standard frameworks)
* Heavy I/O-bound workloads (circuit thread blocks)
* Distributed consensus (single-circuit design, use external coordination)
* Trivial transformations (overhead not justified)

## See Also

For implementation details, see the
`substrates/api/src/main/java/io/humainary/substrates/api/Substrates.java` documentation.
