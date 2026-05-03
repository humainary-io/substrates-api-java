# Substrates API Glossary

Definitions of core concepts in the Humainary Substrates API.

This glossary is organized into two layers. **Core Concepts** are what most users need to build
with Substrates. **Advanced Concepts** cover dynamic topology, lifecycle management, identity
internals, and framework extension — learn these when you need them, not before.

---

## Core Concepts

These are the types most users interact with directly.

* **Cortex**: The factory entry point for the Substrates framework. Provides methods for creating
  circuits (`circuit()`), hierarchical names (`name()`), scopes (`scope()`), slots (`slot()`),
  and states (`state()`). Access via `Substrates.cortex()`.

* **Circuit**: The central processing engine that manages data flow with strict ordering guarantees.
  Each circuit owns exactly one processing thread (virtual thread) that sequentially processes all
  emissions. Manages two internal queues:
    * **Ingress Queue**: Shared queue for emissions from external threads (requires synchronization)
    * **Transit Queue**: Local queue for emissions from within the circuit thread itself (lock-free,
      priority over ingress)

  The transit queue enables **cascading emission ordering** where nested emissions complete before
  processing the next external emission, ensuring causality preservation and enabling neural-like
  signal propagation.

* **Conduit**: A pipe factory and source. Created using `Circuit.conduit()` (type inferred from
  context) or `Circuit.conduit(Class)` (explicit type witness). Pools named pipes by name, ensuring
  stable identity and routing guarantees. Implements `Pool<Pipe<E>>` — use `conduit.get(name)` to
  retrieve a named pipe and `conduit.pool(fn)` to create cached derived views (domain-specific
  wrappers or flow-enabled pipes).

* **Pipe**: An emission carrier responsible for passing typed values through pipelines. Extends the
  `Substrate` interface, providing identity via `subject()`. Created via
  `Circuit.pipe(Receptor)` to wrap callbacks for receiving emissions. Provides `emit(E)` for
  sending values.

* **Name**: A hierarchical naming system using dot-separated segments (e.g.,
  `parent.child.grandchild`). Names are immutable, interned, and use identity-based equality (`==`).
  Created via `Cortex.name()` methods from strings, classes, enums, members, or iterables. Extends
  `Extent` for hierarchical traversal.

* **Subject**: A hierarchical reference system that provides identity, name, and state for every
  component in the Substrates framework. Every substrate component (circuit, conduit, pipe) has a
  subject enabling precision targeting and observability. Parameterized by its owning substrate
  type for type-safe subject extraction.

* **Fiber**: A per-emission operator pipeline preserving emission type. Created via
  `Cortex.fiber()` or `Cortex.fiber(Class)`. Supports filtering (`guard`, `dropWhile`,
  `takeWhile`, `change`, `edge`), comparison-based filtering (`above`, `below`, `min`, `max`,
  `range`, `clamp`, `deadband`, `high`, `low`, `hysteresis`), sampling and rate control
  (`every`, `chance`, `skip`, `limit`, `inhibit`, `delay`), stateful operators (`diff`,
  `reduce`, `integrate`, `rolling`, `pulse`, `relate`, `steady`, `tumble`), and observation
  (`peek`, `replace`). Composes with another fiber via `fiber(Fiber<E>)` and materializes to
  a target via `pipe(Pipe<E>)`.

* **Flow**: A type-changing transformation pipeline that composes Fibers with mappers. Created
  via `Cortex.flow()`, `Cortex.flow(Class)`, or `Cortex.flow(Fiber)`. Provides
  `map(Function<O,P>)` to append a downstream type transformation (returning `null` filters
  the emission), `fiber(Fiber<O>)` to attach a same-type per-emission recipe at the output
  side, `fiber(Function<Subject<?>,Fiber<O>>)` for a subject-aware fiber factory evaluated
  per attachment, `flow(Flow<O,P>)` for first-class composition with another flow, and
  `pipe(Pipe<O>)` to materialize the pipeline against a downstream pipe.

* **Receptor**: A callback interface for receiving emissions. Domain-specific alternative to
  `java.util.function.Consumer` with a guaranteed non-null contract. Used to create receptor pipes
  that receive emitted values. Provides factory methods like `Receptor.of()` and `Receptor.NOOP`.

* **Reservoir**: An in-memory buffer that captures emissions along with their subjects. Created via
  `Source.reservoir()`. Provides `drain()` to retrieve accumulated emissions as a stream of
  `Capture` objects. Extends `Resource` for lifecycle management. Primary tool for testing and
  diagnostic observation.

---

## Advanced Concepts

### Dynamic Topology and Wiring

* **Subscriber**: A component that dynamically subscribes to sources and registers downstream pipes
  via a `Registrar`. Created via `Circuit.subscriber()`, making it a child of the circuit in the
  subject hierarchy (subject path: `circuit-name.subscriber-name`). This establishes that the
  subscriber observes the ordered view provided by that circuit's single-threaded processing.
  Invoked lazily on first emission to discovered named pipes, enabling adaptive topologies that
  respond to runtime structure. Extends `Resource` for lifecycle management.

* **Subscription**: A lifecycle handle returned when subscribing to a source. Provides `close()` to
  unregister downstream pipes. Cancellation uses lazy rebuild — changes take effect on the next
  emission, not immediately.

* **Registrar**: A temporary handle passed to subscribers during their callback, allowing them to
  attach downstream pipes. Provides `register(Pipe)` and `register(Receptor)`. **Temporal
  contract**: Only valid during the subscriber callback — attempting to use it afterwards violates
  the temporal contract.

* **Source**: An interface that allows components to expose their events for subscription. Manages
  the subscription model and connects subscribers to named pipes with lazy rebuild synchronization.
  Implemented by Circuit, Conduit, and Tap.

* **Pool**: A composable interface for name-based instance retrieval with caching. Provides
  `get(Name)`, `get(Subject)`, and `get(Substrate)` for direct lookup, and `pool(fn)` for
  creating cached derived views. `Conduit<E>` extends `Pool<Pipe<E>>`.

* **Tap**: A transformed view of a conduit's emissions. Mirrors the named pipe structure of its
  source. Three overloads: `Conduit.tap(Function)` (mapper receives the tap's target pipe and
  returns a source-compatible pipe), `Conduit.tap(Flow)` (concise form of `tap(flow::pipe)` for
  type-changing transformations), and `Conduit.tap(Fiber)` (concise form of `tap(fiber::pipe)`
  for type-preserving per-emission processing). Extends `Source` for subscription and `Resource`
  for lifecycle management. Must be closed when no longer needed to unsubscribe from the source
  conduit.

### Identity and Hierarchy

* **Substrate**: Base interface for all substrate components that have an associated subject.
  Self-referential and parameterized by its own type, enabling typed subject extraction where
  `substrate.subject()` returns `Subject<ThisSubstrateType>`.

* **Id**: A unique identifier component of a Subject. Ensures each component can be distinguished
  even if names are identical. Uses reference equality (`==`) for O(1) comparison.

* **Extent**: An abstraction of hierarchically nested structures. Provides traversal methods like
  `enclosure()` (parent), `extremity()` (root), `depth()`, `fold()`/`foldTo()`, `path()`, and
  `within()`. Implemented by Name, Subject, and Scope.

* **State**: An immutable collection of named slots containing typed values. Supports persistent
  updates where operations return new instances. Provides `state(Name, value)` for adding slots,
  `value(Slot)` for lookup, `compact()` for deduplication, and iteration/streaming over slots.

* **Slot**: An opaque interface representing a variable within a State. Provides both a query key
  (name + type) and a fallback value. Matching occurs on slot name identity AND slot type. Created
  via `Cortex.slot()` methods for boolean, int, long, float, double, String, Name, State, and Enum.

### Resource Management

* **Resource**: A lifecycle interface for explicitly releasing resources and terminating operations.
  Provides an idempotent `close()` method. Implemented by Circuit, Subscription, Reservoir, and
  Subscriber. Does not extend `AutoCloseable` as most resources have indefinite lifetimes.

* **Scope**: A structured resource manager that automatically closes registered assets in reverse
  order (LIFO), enabling RAII-like lifetime control. Implements `AutoCloseable` for
  try-with-resources. Provides `register(Resource)` for scope-lifetime resources,
  `closure(Resource)` for block-scoped resources, and hierarchical child scopes via `scope()`.
  Extends `Extent` for parent-child relationships.

* **Closure**: A utility interface for scoping work performed against a resource. Provides
  block-scoped resource management where `consume(Consumer)` executes the consumer with the resource
  then automatically closes it. Single-use - once consumed, the closure is exhausted. **Temporal
  contract**: Only valid during the consume callback.

* **Capture**: A record of an emitted value from a named pipe with its associated subject. Produced
  by Reservoirs. Provides `emission()` for the value and `subject()` for the pipe identity. Used
  for testing, debugging, telemetry, and replay.

### Execution Context

* **Current**: Represents the execution context (thread, coroutine, fiber) from which substrate
  operations originate. Obtained via `Cortex.current()` in a manner analogous to
  `Thread.currentThread()`. **Temporal contract**: Only valid within the execution context (thread)
  that obtained it. Must not be retained or used from different threads.

---

## Temporal Contracts

Several Substrates interfaces follow **temporal contracts** — they are only valid within specific
execution scopes and must not be retained or used outside those scopes:

### Callback-Scoped

* **Registrar**: Valid only during `Subscriber` callback. Retain the pipes you attach, not the
  Registrar.
* **Closure**: Valid only during `Closure.consume(Consumer)` callback. Single-use, resource closed
  when callback returns.

### Thread-Scoped

* **Current**: Valid only within the execution context (thread) that obtained it. Call
  `Cortex.current()` when needed rather than retaining.

Violating temporal contracts leads to undefined behavior. The framework marks temporal interfaces
with the `@Temporal` annotation.

## Fiber and Flow Operations

Per-emission operators live on **Fiber**; type-changing composition lives on **Flow**. Both are
first-class values built from the cortex and materialized against a target pipe via `pipe(...)`.

### Fiber: per-emission, type-preserving

```java
// Build a recipe and attach it to a target pipe
cortex.fiber ( Integer.class )
      .guard ( x -> x > 0 )   // filter
      .diff ()                // deduplicate
      .limit ( 10 )           // backpressure
      .pipe ( target );
```

* **Purpose**: filtering, sampling, comparison checks, and stateful per-emission processing
* **Type behavior**: input and output share the same type (e.g., `Integer` → `Integer`)
* **Materialization**: `fiber.pipe(target)` produces a `Pipe<E>` whose emissions feed `target`

### Flow: type-changing composition

```java
// Map I → O on the output side, then attach a same-type fiber recipe
cortex.flow ( Integer.class )
      .map ( i -> "value:" + i )
      .fiber ( cortex.fiber ( String.class ).diff () )
      .pipe ( sink );
```

* **Purpose**: append a downstream type transformation, attach Fiber recipes at the output side,
  or compose two flows
* **Operations**: `map`, `fiber(Fiber)`, `fiber(Function<Subject,Fiber>)`, `flow(Flow)`, `pipe`
* **Materialization**: `flow.pipe(target)` produces a `Pipe<I>` that runs the pipeline ahead of
  `target` on `target`'s circuit

All stages execute on the circuit's worker thread after emissions are dequeued, providing
single-threaded execution guarantees. Stateful Fiber operators (`diff`, `reduce`, `limit`,
`integrate`, `rolling`, ...) maintain per-attachment state without synchronization.
