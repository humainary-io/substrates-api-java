# Substrates Glossary

This glossary provides definitions of core concepts in the Humainary Substrates API, organized by
functional area.

## Entry Point

* **Cortex**: The factory entry point for the Substrates framework. Provides methods for creating
  circuits (`cortex()`), hierarchical names (`name()`), and accessing the current execution
  context (
  `current()`). Access via `Substrates.cortex()`.

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

* **Channel**: A subject-based port that serves as an entry point into a conduit's pipeline.
  Channels are created lazily on first access by name. Channels emit values that flow through the
  conduit's pipeline to registered subscriber pipes. **Temporal contract**: Channel references are
  only valid during the `Composer.compose()` callback and must not be retained. Instead, retain the
  pipe returned by `channel.pipe()`.

* **Pipe**: An emission carrier responsible for passing typed values through pipelines. Extends the
  `Percept` interface. Two types of pipes:
    * **Receptor Pipes**: Created via `Cortex.pipe(Receptor)` - wrap callbacks for receiving
      emissions
    * **Transformed Pipes**: Created via `Cortex.pipe(Function, Pipe)` - apply type transformations
      before forwarding to target pipe

* **Flow**: A type-preserving processing pipeline for filtering and stateful operations. Supports
  operations like `diff` (deduplication), `guard` (filtering), `limit` (backpressure), `sample` (
  rate limiting), `sift` (range checking), `reduce` (aggregation), `peek` (observation), and
  `replace` (value mapping). Flow operations maintain the same type throughout (e.g., Integer →
  Integer) unlike pipe transformations which can change types.

## Observation & Subscription

* **Receptor**: A callback interface for receiving emissions. Domain-specific alternative to
  `java.util.function.Consumer` with a guaranteed non-null contract. Used to create receptor pipes
  that receive emitted values.

* **Percept**: A marker interface for all observable entities, such as pipes and domain-specific
  instruments (counters, gauges, monitors, etc.). Used to enforce type-safe constraints in composers
  and enable framework extensibility.

* **Subscriber**: A component that dynamically subscribes to sources and registers pipes with
  channel subjects via a `Registrar`. Invoked lazily on first emission to discovered channels,
  enabling adaptive topologies that respond to runtime structure.

* **Subscription**: A lifecycle handle returned when subscribing to a source. Provides `cancel()` to
  unregister pipes from channels. Cancellation uses lazy rebuild - changes take effect on the next
  emission, not immediately.

* **Registrar**: A temporary handle passed to subscribers during their callback, allowing them to
  attach pipes to channels. **Temporal contract**: Only valid during the subscriber callback -
  attempting to use it afterwards violates the temporal contract.

* **Source**: An interface that allows components to expose their events for subscription. Manages
  the subscription model and connects subscribers to channels with lazy rebuild synchronization.

## Identity & Hierarchy

* **Subject**: A hierarchical reference system that provides identity, name, and state for every
  component in the Substrates framework. Every substrate component (circuit, conduit, channel, pipe)
  has a subject enabling precision targeting and observability.

* **Id**: A unique identifier component of a Subject. Ensures each component can be distinguished
  even if names are identical.

* **Name**: A hierarchical naming system using dot-separated segments (e.g.,
  `parent.child.grandchild`). Names are immutable and optimized for comparison. Created via
  `Cortex.name()` methods from strings, classes, enums, or iterables.

* **State**: The lifecycle state component of a Subject, indicating whether a component is active,
  closing, or closed.

## Resource Management

* **Scope**: A structured resource manager that automatically closes registered assets in reverse
  order (LIFO), enabling RAII-like lifetime control. Ensures graceful degradation and predictable
  teardown in systems requiring operational safety.

* **Reservoir**: An in-memory buffer that captures emissions along with their subjects. Primarily
  used
  for testing and inspection purposes, allowing verification of emission sequences and values.

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

## Type Transformation vs Flow Operations

Understanding the distinction between type transformation and flow operations is critical:

### Type Transformation: `Cortex.pipe(Function, Pipe)`

* **Purpose**: Change value types
* **Type behavior**: Can change types (e.g., `String` → `Integer`)
* **Execution**: Synchronous, stateless, on caller's thread
* **Example**: `cortex.pipe(Integer::parseInt, target)`

### Flow Operations: `Circuit.pipe(Pipe, Function<Flow, Flow>)`

* **Purpose**: Type-preserving filtering and stateful operations
* **Type behavior**: Maintains same type (e.g., `Integer` → `Integer`)
* **Execution**: Stateful, with lifecycle management
* **Example**: `circuit.pipe(target, flow -> flow.guard(x -> x > 0).diff())`

Both approaches complement each other and can be composed together.

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

For development guidelines, see `CLAUDE.md` in the project root.
