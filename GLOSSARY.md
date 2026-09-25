# Substrates API Glossary

Definitions of core concepts in the Humainary Substrates API.

This glossary is organized into two layers. **Core Concepts** are what most users need to build with
Substrates. **Advanced Concepts** cover dynamic topology, lifecycle management, identity internals,
and framework extension — learn these when you need them, not before.

---

## Core Concepts

These are the types most users interact with directly.

* **Cortex**: The factory entry point for the Substrates framework. Provides methods for creating
  circuits (`circuit()`), hierarchical names (`name()`), scopes (`scope()`), root pools
  (`pool(fn)`), slots (`slot()`), states (`state()`), fibers (`fiber()`), and flows (`flow()`).
  Access via `Substrates.cortex()`.

* **Circuit**: The central processing engine that manages data flow with strict ordering guarantees.
  Each circuit owns exactly one sequential execution context that processes all emissions in turn.
  Queued work falls into two ordering classes:
  * **Ingress**: work submitted from any context other than this circuit's own
  * **Transit**: work raised from within this circuit's context during processing, which drains
    completely before the circuit returns to the next ingress item

  Transit priority produces **cascading emission ordering**, where nested emissions complete before
  the next external emission is processed, preserving causality and enabling neural-like signal
  propagation. These are ordering roles, not a required data structure — a provider may realize
  them with two queues, one mailbox with priority markers, local slots, or actor turns (SPEC §5.3).

* **Conduit**: A pipe factory and source. Created using `Circuit.conduit()` (type inferred from
  context) or `Circuit.conduit(Class)` (explicit type witness). Pools named pipes by name, ensuring
  stable identity and routing guarantees. Implements `Pool<Pipe<E>>` — use `conduit.get(name)` to
  retrieve a named pipe and `conduit.pool(fn)` to create cached derived views (domain-specific
  wrappers or flow-enabled pipes).

* **Pipe**: An emission carrier responsible for passing typed values through pipelines. Extends the
  `Substrate` interface, providing identity via `subject()`. Created via
  `Circuit.pipe(Receptor)` or `Circuit.pipe(Name, Receptor)` to wrap callbacks for receiving
  emissions. Provides `emit(E)` for sending values.

* **Cell**: A circuit-owned, initialized, single-slot state holder with safe publication. Created
  via `Circuit.cell(E)` with a non-null seed value.
  Exposes `pipe()` for queued updates and `get()` for the current published value; `get()` never
  returns `null`. Updates execute on the owning circuit's worker thread; readers may call `get()`
  from any thread. Use Fiber/Flow upstream to compute the value to publish — Cell is the end of the
  pipeline, not a transformation stage. Applications that need "no value yet" semantics should model
  that as a domain value such as `Optional<E>` or an explicit sentinel.

* **Port**: A circuit-owned, initialized state slot that grants queued mutation authority
  without exposing direct read access. Created via `Circuit.port(E)` or `Circuit.port(Name, E)`.
  Exposes `replace(E)`, `update(UnaryOperator<E>)`, `update(A, BiFunction<E, A, E>)`, and
  `emit(Pipe<? super E>)` — all queued, all callable from any context. `Port.emit` is not
  `Pipe.emit`: it takes a destination and queues a forward of the value the port already holds,
  where `Pipe.emit` takes a value and admits it. Holders can evolve state but
  never observe the current value. Transform functions run inside the owning circuit context; a
  null/absent return is treated as a framework-detected contract violation and surfaced as a
  `Fault` carrying the port's subject. Use Port when a module needs mutation authority without read
  authority (principle of least authority).

* **Pin**: A circuit-owned, initialized state handle that grants immediate inspect/mutate
  access confined to the owning circuit context. Created via `Circuit.pin(E)` or
  `Circuit.pin(Name, E)`. Exposes `get()` and `set(E)`, both synchronous and both raising
  `IllegalStateException` when invoked from any context other than the owning circuit's worker
  thread. Safety comes from confinement, not synchronization. Use Pin when circuit-local code needs
  synchronous "set X, then read X on the next line" semantics, or when mutable in-place objects must
  be updated under circuit-context confinement. A Pin handle may be emitted as a value through any
  pipe, but only the owning circuit can dereference it.

* **Ticker**: A circuit-owned periodic emitter. Created via `Circuit.ticker(Duration, Pipe)` or
  `Circuit.ticker(Name, Duration, Pipe)`. Drives a gap-free, monotonically increasing tick sequence
  (starting at zero) into the target pipe at a fixed rate: scheduling error does not accumulate, and
  a stall longer than one interval re-anchors the schedule rather than firing a catch-up burst.
  Extends `Resource` for lifecycle management; closing the ticker (or its owning circuit) stops
  emissions.

* **Pulse**: A four-timestamp probe of a circuit's queue latency, returned by `Circuit.pulse()`
  as an `Optional` (empty when the circuit cannot serve one). Exposes `start`, `enqueued`,
  `dequeued`, and `stop` as raw `System.nanoTime()` readings — meaningful only for difference
  arithmetic, never as absolute instants. `dequeued - enqueued` is queue wait, the primary signal
  that the circuit's execution context is processing-bound. Unrelated to the `Fiber.pulse`
  operator, which passes the rising edge of a predicate.

* **Name**: A hierarchical naming system using dot-separated segments (e.g.,
  `parent.child.grandchild`). Names are immutable, interned, and use identity-based equality (`==`).
  Created via `Cortex.name()` methods from strings, classes, enums, members, or iterables. Extends
  `Extent` for hierarchical traversal.

* **Subject**: A hierarchical reference system that provides identity, name, and state for every
  component in the Substrates framework. Every substrate component (circuit, conduit, pipe) has a
  subject enabling precision targeting and observability. Parameterized by its owning substrate type
  for type-safe subject extraction.

* **Fiber**: A per-emission operator pipeline preserving emission type. Created via
  `Cortex.fiber()` or `Cortex.fiber(Class)`. Supports filtering (`guard`, `dropWhile`,
  `takeWhile`, `change`, `edge`, `route`, `when`), comparison-based filtering (`above`, `below`,
  `min`, `max`, `range`, `clamp`, `deadband`, `high`, `low`, `hysteresis`), sampling and rate
  control (`every(int)`, `every(Duration)`, `chance`, `skip`, `limit`, `inhibit`, `delay`), stateful
  operators (`diff`,
  `distinct`, `distinct(int)`, `heartbeat`, `reduce`, `integrate`, `rolling`, `pulse`, `relate`,
  `steady`, `streak`, `tumble`), and observation (`peek`, `replace`, `tee`). Composes with another fiber via
  `fiber(Fiber<E>)` and materializes to a consumer target via `pipe(Pipe<? super E>)` or
  `pipe(Cell<? super E>)`.

* **Flow**: A left-to-right composition surface for type-changing stages and output-side recipes.
  Created via `Cortex.flow()`, `Cortex.flow(Class)`, or `Cortex.flow(Fiber)`. Provides
  `map(Function<O,P>)` to append a downstream type transformation (returning `null` filters the
  emission), `scan(Supplier, BiFunction)` for state-as-output folds over immutable/effectively
  immutable state, `scan(Supplier, BiFunction, Function)` for projected stateful type-changing
  folds,
  `relate(O, BiFunction)` for a fold over consecutive output pairs,
  `window(int)` for count-bounded and `window(Duration, int)` for time-bounded rolling temporal
  windows over surviving output values,
  `run()` to turn each admission into a `Run` carrying its consecutive-run length,
  `change()` to emit a `Change` at each run boundary,
  `fiber(Fiber<O>)` to attach a same-type per-emission recipe at the output side,
  `fiber(Function<Subject<?>,Fiber<O>>)` for a subject-aware fiber factory evaluated per attachment,
  `flow(Flow<O,P>)` and `flow(Function<Subject<?>,Flow<O,P>>)` for first-class composition with
  another flow or a subject-aware flow segment, and
  `pipe(Pipe<? super O>)` or `pipe(Cell<? super O>)` to materialize the pipeline against a
  downstream consumer.

* **Window**: A callback-scoped, read-only rolling view emitted by `Flow.window(int)` or
  `Flow.window(Duration, int)`. Exposes the most recent surviving output values in encounter order
  and supports `first`, `last`, `prefix`, `suffix`, `slice`, `skip`, `trim`, `reverse`, `size`,
  `isEmpty`, and eager terminal operations (`forEach`, `all`, `any`, `none`, `count`, `fold`,
  `reduce`). Iterators, spliterators, and streams are intentionally not exposed — a Window must be
  consumed during the callback that observes it; copy values if they must be retained or forwarded.

* **Receptor**: A callback interface for receiving emissions. Domain-specific alternative to
  `java.util.function.Consumer` with a guaranteed non-null contract. Used to create receptor pipes
  that receive emitted values. Provides factory methods like `Receptor.of()` and `Receptor.NOOP`.

* **Basin**: A circuit-owned, bounded in-memory buffer of values fed through `Basin.pipe()`; the
  multi-value sibling of `Cell`. Created via `Circuit.basin(capacity)`. Retains the most recent
  `capacity` values and provides `drain(Pipe<? super E>)` to forward them to a target pipe
  (consuming the buffer) on the circuit worker thread. Holds plain values; to capture a source's
  emissions with their subjects, feed a `Basin<Capture<E>>` from a `Sink`.

* **Sink**: A pool of named pipes (`Pool<Pipe<E>>`) whose destination is a single endpoint pipe
  fixed at creation — the output-closed dual of a `Conduit`. Each emission into a channel is minted
  into a `Capture` at that channel and forwarded to the endpoint. A `Substrate`, not a `Resource`
  (no `close()`). Created via `Circuit.sink(endpoint)`.

---

## Advanced Concepts

### Dynamic Topology and Wiring

* **Subscriber**: A component that dynamically subscribes to sources and registers downstream pipes
  via a `Registrar`. Created via `Circuit.subscriber(Name, callback)` or
  `Circuit.subscriber(Name, pool)`, making it a child of the circuit in the subject hierarchy
  (subject path: `circuit-name.subscriber-name`). This establishes that the subscriber observes the
  ordered view provided by that circuit's single-threaded processing. Invoked lazily on first
  emission to discovered named pipes, enabling adaptive topologies that respond to runtime
  structure. Extends `Resource` for lifecycle management.

* **Subscription**: A lifecycle handle returned when subscribing to a source. Provides `close()` to
  unregister downstream pipes. Cancellation uses lazy rebuild — changes take effect on the next
  emission, not immediately.

* **Registrar**: A temporary handle passed to subscribers during their callback, allowing them to
  attach downstream pipes. Provides `register(Pipe)` and `register(Receptor)`. **Temporal contract**:
  Only valid during the subscriber callback — attempting to use it afterward violates the
  temporal contract.

* **Source**: An interface that allows components to expose their events for subscription. Manages
  the subscription model and connects subscribers to named pipes with lazy rebuild synchronization.
  Implemented by Conduit.

* **Lookup**: The abstract base for name-indexed retrieval. Defines the single operation
  `get(Name)`: return the instance for the given name (creating it on first access for caching
  implementations). Implementations are thread-safe for concurrent retrieval. Extended by both
  `Pool` (composable derived views) and `Bank` (ownership semantics).

* **Pool**: A composable name-based view extending `Lookup`. Inherits `get(Name)` and adds
  `get(Subject)`, `get(Substrate)` convenience overloads and `pool(fn)` for creating cached derived
  views that apply a transformation function per name. Root pools are created with
  `Cortex.pool(fn)`; `Conduit<E>` and `Sink<E>` are pools of named pipes.

* **Bank**: A closeable name-indexed holder for same-kind named resources. Created via
  `Circuit.bank(Class)` or `Circuit.bank(Class, Routing)`. Extends `Lookup` and `Resource`:
  `get(name)` materializes a conduit on first access and caches it; closing the bank closes all
  conduits it has materialized. Unlike a Pool (name-indexed retrieval, whose entries it does not
  own), a Bank creates and owns its conduits. `get` is open-required — calling it after the bank has been
  closed throws `Fault`.

* **Routing**: An enum selecting how a conduit dispatches an emission. `PIPE` (the default)
  dispatches only to the target channel's subscribers; `STEM` propagates leaf-first from the target
  upward through every ancestor channel in the name hierarchy, creating ancestor channels lazily as
  subscriber attachment points. Supplied at creation via `Circuit.conduit(Class, Routing)` or
  `Circuit.bank(Class, Routing)`.

* **Mirror** (pattern, not a type): a conduit fed from another source through a pool-backed
  subscriber — `source.subscribe(circuit.subscriber(name, mirror.pool(flow)))`. Channels appear in
  the mirror under the source channels' names while the pooled flow, fiber, or function transforms
  emissions on the way in. Closing the bridging `Subscription` stops the feed; the mirror itself is
  an ordinary `Conduit` with its own lifecycle.

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
  Provides an idempotent `close()` (queued, non-blocking — submits cleanup and returns immediately)
  and `closeAwait()` (blocking — suspends the caller until cleanup has executed; MUST NOT be called
  from the circuit thread). Implemented by Circuit, Conduit, Bank, Ticker, Subscription, and
  Subscriber.
  Does not extend `AutoCloseable` as most resources have indefinite lifetimes.

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
  by Sinks. Provides `emission()` for the value and `subject()` for the pipe identity. Used for
  testing, debugging, telemetry, and replay.

* **Run**: An immutable envelope emitted per admission by `Flow.run()`, carrying the repeated
  `emission()` and the `length()` of the consecutive value-equal run ending at that admission
  (`1` on the first admission and after every change). Out-of-execution-scope — safe to retain,
  compare, and forward.

* **Change**: An immutable envelope emitted by `Flow.change()` at a run boundary, carrying the
  closed run's value (`from()`), the value opening the next run (`to()`), and the terminal
  `length()` the closed run reached. Out-of-execution-scope, like `Run`. Unrelated to the
  `Fiber.change` operator, which filters to emissions whose derived key differs from the previous
  one.

### Execution Context

* **Current**: Represents the execution context (thread, coroutine, fiber) from which substrate
  operations originate. Obtained via `Cortex.current()` in a manner analogous to
  `Thread.currentThread()`. Current is an interned identity token for an execution context and may
  be retained for identity comparison and immutable subject inspection.

---

## Temporal Contracts

Several Substrates interfaces follow **temporal contracts** — they are only valid within specific
execution scopes and must not be retained or used outside those scopes:

### Callback-Scoped

* **Registrar**: Valid only during `Subscriber` callback. Retain the pipes you attach, not the
  Registrar.
* **Window**: Valid only during the receptor or Fiber callback that observes it. Copy values during
  the callback if they must outlive it.

### Single-Use

* **Closure**: Valid only during `Closure.consume(Consumer)` callback. Single-use, resource closed
  when callback returns.

Violating temporal contracts leads to undefined behavior. The framework marks temporal interfaces
with the `@Temporal` annotation.

## Fiber and Flow Operations

Type-preserving per-emission operators live on **Fiber**; type-changing composition, including
stateful folds via `scan` and rolling windows via `window`, lives on **Flow**. Both are first-class
values built from the cortex and materialized against a target pipe via `pipe(...)`.

### Fiber: per-emission, type-preserving

```java
// Build a recipe and attach it to a target pipe
cortex.fiber(Integer.class)
  .guard(x -> x > 0)  // filter
  .diff()             // deduplicate
  .limit(10)          // backpressure
  .pipe(target);
```

* **Purpose**: filtering, routing, fan-out, deduplication, sampling, comparison, and stateful
  per-emission processing
* **Type behavior**: input and output share the same type (e.g., `Integer` → `Integer`)
* **Materialization**: `fiber.pipe(target)` produces a `Pipe<E>` whose emissions feed `target`

### Flow: type-changing composition

```java
// Map I → O on the output side, then attach a same-type fiber recipe
cortex.flow(Integer.class)
  .map(i -> "value:" + i)
  .fiber(cortex.fiber(String.class).diff())
  .pipe(sink);
```

* **Purpose**: append a downstream type transformation, run a stateful type-changing fold, publish
  an immutable running state directly, attach Fiber recipes at the output side, emit rolling
  windows, or compose flows
* **Operations**: `map`, `scan`, `relate`, `window(int)`, `window(Duration, int)`, `run`,
  `change`, `fiber(Fiber)`, `fiber(Function<Subject,Fiber>)`,
  `flow(Flow)`, `flow(Function<Subject,Flow>)`, `pipe`
* **Materialization**: `flow.pipe(target)` produces a `Pipe<I>` that runs the pipeline ahead of
  `target` on `target`'s circuit

All stages execute on the circuit's worker thread after emissions are dequeued, providing
single-threaded execution guarantees. Stateful Fiber operators (`diff`, `distinct`, `reduce`,
`limit`, `integrate`, `rolling`, ...), Flow `scan` state slots, and Flow `window` rolling buffers
maintain per-attachment state without synchronization.
