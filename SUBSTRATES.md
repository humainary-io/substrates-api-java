# Substrates: Design Rationale

Why the API is shaped the way it is.

## Abstract

Substrates is a deterministic emission network for Java. It is not a reactive stream, not an actor
framework, not a message queue, and not a channel abstraction. It is a computational fabric where
values flow through circuits, conduits, and pipes with strict ordering guarantees, dynamic
subscription-based wiring, and sub-3ns emission latency.

The API defines 25 interfaces. Each one exists for a specific structural reason — performance,
composability, lifecycle clarity, or all three. This document explains those reasons. Where the
formal specification (SPEC.md) defines *what* a conformant implementation must do, and the
specification rationale (RATIONALE.md) positions those requirements against related work, this
document explains the design in accessible terms for developers evaluating, using, or reviewing the
API.

## Concept Map: Core vs Advanced

The 25 interfaces are not equally central. Most users need only the core layer to build with
Substrates. The remaining types support dynamic topologies, lifecycle management, identity
internals, and framework extension — learn them when you need them.

**Core** — the types most users interact with directly:

| Type    | Role                                                                          |
|---------|-------------------------------------------------------------------------------|
| Cortex  | Factory entry point                                                           |
| Circuit | Deterministic execution engine                                                |
| Conduit | Named pipe pool with subscription support                                     |
| Pipe    | Emission carrier — `emit(value)`                                              |
| Name    | Hierarchical address for routing                                              |
| Fiber   | Type-preserving operator pipeline (guard, diff, limit, every, chance, reduce) |
| Flow    | Type-changing composition pipeline (map, scan, window, fiber, flow)           |
| Window  | Callback-scoped rolling view emitted by Flow.window                           |

**Observation** — monitoring and testing:

| Type      | Role                                            |
|-----------|-------------------------------------------------|
| Receptor  | Emission callback (non-null contract)           |
| Reservoir | Buffered emission recorder                      |
| Capture   | Emission + subject pair (produced by Reservoir) |

**Identity** — metadata and hierarchy (when you need more than Name):

| Type    | Role                             |
|---------|----------------------------------|
| Subject | Composition of Name + Id + State |
| Id      | Unique instance identity         |
| State   | Immutable typed-slot collection  |
| Slot    | Typed variable within a State    |

**Wiring** — dynamic topology construction:

| Type         | Role                                                            |
|--------------|-----------------------------------------------------------------|
| Subscriber   | Discovers channels and registers pipes dynamically              |
| Subscription | Lifecycle handle for canceling subscriptions                    |
| Registrar    | Temporary handle for attaching pipes during subscriber callback |
| Source       | Subscription management interface (Circuit, Conduit, Tap)       |
| Lookup       | Composable name-based retrieval with caching                    |

**Lifecycle** — resource and scope management:

| Type     | Role                                    |
|----------|-----------------------------------------|
| Resource | Base lifecycle interface (`close()`)    |
| Scope    | Structured RAII-like resource manager   |
| Closure  | Block-scoped ARM with scope-aware no-op |
| Tap      | Transformed source view with lifecycle  |

**Framework** — extension points and structural types:

| Type      | Role                                         |
|-----------|----------------------------------------------|
| Substrate | Base interface for all subject-bearing types |
| Extent    | Hierarchy traversal (parent/child)           |
| Current   | Execution context identity                   |

The rest of this document explains *why* each layer exists.

## 1. The Determinism Tradeoff

Most concurrent frameworks — thread pools, actor runtimes, async task schedulers — optimize for
throughput by dispatching work to any available execution context. This makes processing order
dependent on OS thread scheduling, garbage collection pauses, and cache coherency delays. Two runs
of the same program with the same inputs may produce different intermediate states and different
observable behaviors.

Substrates makes the opposite choice. Every emission within a circuit is processed sequentially in
strict admission order. Earlier emissions complete before later ones begin. All observers see the
same sequence. This eliminates an entire class of concurrency bugs — not by managing them with locks
and barriers, but by making them structurally impossible.

The cost is that a single circuit cannot exploit parallelism across its emissions. The design
addresses this through two mechanisms:

1. **Multiple circuits**: Independent circuits execute concurrently on separate virtual threads.
   Parallelism is achieved by partitioning work across circuits, not by parallelizing within one.
2. **Caller-side work shifting**: Expensive computation, serialization, and preparation should occur
   in the caller's thread before emission. The circuit thread handles only routing, filtering, and
   lightweight state updates — minimizing the sequential bottleneck.

### Replay as a Structural Consequence

Deterministic ordering makes replay a free architectural property. If the ingress queue is captured
as an ordered log, replaying that log into the same circuit topology reproduces the exact same state
transitions. This enables deterministic testing without flaky timing-dependent assertions, digital
twin synchronization via ordered event streams rather than continuous state broadcasts, and forensic
debugging by replaying the input log up to the point of failure.

The Reservoir is the concrete mechanism: it subscribes to a source and captures every emission
alongside the channel subject that produced it, preserving circuit-thread processing order. Its
incremental drain returns only captures accumulated since the last drain, with snapshot semantics.

## 2. Circuit-Context Confinement

Each circuit owns exactly one sequential execution context. All emissions, flow operations,
subscriber callbacks, and state transitions execute exclusively within that context.

A data race requires two concurrent operations accessing the same memory location where at least one
is a write and there is no happens-before ordering. Circuit-context confinement eliminates data
races structurally: if only one operation can execute at a time within a circuit, concurrent
mutation is impossible. State accessed within the circuit context requires no synchronization.
Developers write sequential logic; the framework guarantees sequential execution.

### How This Differs from the Actor Model

The Actor Model shares isolated state with message-passing communication. However, traditional actor
implementations (Erlang/OTP, Akka) have two properties that Substrates rejects:

1. **Non-deterministic mailbox ordering**: When multiple actors send messages to the same target
   concurrently, the processing order depends on arrival timing. Substrates mandates a total order
   on the ingress queue — each run sees a single, consistent sequence.

2. **Interleaved side effects**: Actors process messages independently. Message A's cascading side
   effects may interleave with message B's processing. In Substrates, the transit queue ensures that
   all cascading effects of emission A complete atomically before emission B begins.

### Virtual Threads

The Java implementation uses a virtual thread per circuit — a lightweight execution context managed
by the runtime. Virtual threads are cheap to create (one per circuit is practical with thousands of
circuits), don't consume OS thread resources while parked, and the sequential execution guarantee
means no coordination with other threads for internal state access.

## 3. The Ingress / Transit Ordering Model

The specification defines two logical classes of work scheduled against a circuit:

- **Ingress work**: Emissions and other queued operations admitted from external threads
  (caller-side)
- **Transit work**: Emissions originating from the circuit thread itself during processing

When processing an ingress item triggers additional emissions from within the circuit thread, those
cascading emissions are admitted as transit work, which takes strict priority over the next ingress
item. The reference implementation realizes this with two physical queues (the historical
"dual-queue" name); the specification only requires the logical ordering and permits other
mechanisms (single mailbox with priority markers, actor turns, etc.).

### Problem 1: Causal Fragmentation

In a single-queue model, an external emission E1 that triggers internal emissions I1 and I2 would
interleave with the next external emission E2: `E1, E2, I1, I2`. The transit queue ensures causal
completion: `E1, I1, I2, E2`. External observers see atomic state transitions — E2 observes the
fully resolved state of E1, never a partial intermediate state.

### Problem 2: Stack Overflow in Cyclic Topologies

In naive event-driven architectures, cascading emissions execute via recursive function calls. In
cyclic topologies (feedback loops, recurrent networks), this produces unbounded recursion and stack
overflow. Transit ordering converts recursion to iteration — cascading emissions are admitted as
transit work, not immediately invoked. Processing remains iterative regardless of cascade depth.
This is essential for neural-like computational networks where cyclic signal propagation is the
norm.

### Problem 3: Coordination Without Locks

Transit work is produced and consumed only by the circuit thread — no synchronization required. The
ingress admission path requires synchronization only at the admission boundary. Once work enters the
circuit context, all processing including cascading is lock-free.

### Relationship to Berkeley Reactor

Both Berkeley Reactor (Lohstroh & Lee, 2019) and Substrates seek to tame actor-model non-determinism
through controlled event scheduling. The key difference is structural: Reactors focus on scheduling
via a global logical clock. Substrates introduces physical routing boundaries (Cortex, Circuit,
Conduit, Pipe) that make topology itself an architectural artifact. The routing path of a signal is
as observable and constrained as its timing.

## 4. Why Not Backpressure

Reactive Streams addresses producer-consumer speed mismatch through demand-based backpressure:
consumers signal how many elements they can accept via `request(n)`, and producers must not exceed
that demand.

Substrates rejects queue-level backpressure for two reasons:

1. **Blocking breaks determinism**: If a caller blocks because the queue is full, the order in which
   callers resume depends on the runtime scheduler — introducing non-determinism into the ingress
   sequence.
2. **Dropping breaks replay**: If emissions are discarded under load, the replay log is incomplete
   and the deterministic replay guarantee fails.

### Fiber-Level Admission Control

Instead, admission control happens *inside* the circuit, after dequeue, where it does not affect
ingress ordering:

- **Fiber.limit ()**: Caps the number of emissions processed by a Fiber stage. After the limit, the
  stage is permanently closed.
- **Fiber.every () / Fiber.chance ()**: Filter emissions by interval (every Nth) or probability
  respectively. Reduce downstream volume without affecting ingress.

These operators are deterministic — given the same input sequence, they produce the same output.
Combined with the rest of the Fiber operator vocabulary (high/low watermarks via `high`/`low`, range
filtering via `range`/`clamp`/`above`/`below`/`min`/`max`/`deadband`, change detection via
`diff`/`change`, accumulation via `reduce`/`integrate`/`relate`, transition detection via
`edge`/`pulse`, temporal confirmation via `steady`/`streak` (stable-value runs and predicate-match
runs respectively), gating via `hysteresis`/`inhibit`, temporal displacement via `delay`, windowed
aggregation via `rolling`/`tumble`, and lifecycle windowing via `dropWhile`/`takeWhile`/`skip`),
they compose into complex event processing patterns without requiring external stream processing
engines. The consecutive-vs-cumulative threshold pattern — central to sign-to-status lifting — falls
out of this vocabulary directly: `streak(N, p)` for "N in a row" and `guard(p).every(N)` for
"N total", with no parallel threshold abstraction required.

## 5. Interface Segregation

The API defines 25 interfaces. This section explains why.

### The Sealed Hierarchy

Three sealed interfaces form the backbone:

```
Substrate  (identity — has subject())
  └── Resource  (lifecycle — has close())
        └── Source  (wiring — has subscribe())
```

Each level adds exactly one capability. Not every substrate owns resources (Pipe is GC-collected),
and not every resource manages subscriptions (Scope has lifecycle but no subscription model). The
sealed permits enforce this:

- `Substrate` permits: Cortex, Current, Pipe, Scope, Reservoir, Source, Subscriber, Subscription
- `Resource` permits: Source, Reservoir, Subscriber, Subscription
- `Source` permits: Circuit, Conduit, Tap

### Why Not Collapse Them?

Forcing `close()` onto Pipe would add a method that does nothing — misleading callers into thinking
cleanup is required when pipes are GC-collected. Forcing `subscribe()` onto Scope would add
subscription machinery to a pure lifecycle manager. Each unnecessary method is a lie about
capability that produces confusion and potential misuse.

Java's sealed interfaces make this precise: the compiler enforces which types can extend which
capabilities. Collapsing the hierarchy would either break the sealed permits or require every type
to carry capabilities it does not use.

### Identity Types

The identity types illustrate the same principle:

| Type    | Responsibility                     | Hot-Path Role                                  |
|---------|------------------------------------|------------------------------------------------|
| Name    | Interned hierarchical path         | Routing key (O(1) reference equality)          |
| Id      | Unique instance identity           | Identity check (never shared across instances) |
| Subject | Composition of Name + Id + State   | Subscriber callbacks (full context)            |
| Extent  | Hierarchy traversal (parent/child) | Structural queries (depth, enclosure)          |

On the emission path, only Name is needed for routing. Subject is only needed in subscriber
callbacks. Id is only needed for identity checks. Keeping them separate means the emission hot path
never touches Subject or Id — it routes purely on Name reference equality.

Merging these into a single "Context" or "Scope" abstraction would force every emission to carry
full identity, hierarchy, and state information through the dispatch path. At 336M emissions/sec,
every unnecessary field access matters.

### Composition Builds Capability

Richer types compose from these primitives:

- **Conduit** extends Pool + Source: genuinely needs both pipe retrieval and subscription management
- **Circuit** extends Source: manages subscriber registration and lifecycle events
- **Tap** extends Source + Resource: transformed view with subscription and lifecycle

This is not accidental — it is the Interface Segregation Principle applied to a performance-critical
domain. Each interface carries one responsibility. Composition builds exactly the capability each
type needs.

## 6. The Performance Model

The SPI implementation achieves sub-3ns emission latency (336M ops/sec). This target drives
optimization decisions throughout the design.

### The Circuit Thread Bottleneck

The circuit's single thread processes all emissions sequentially — it is the system's bottleneck.
The fundamental performance principle is: **shift work to caller threads**. Expensive computation,
serialization, and data preparation should happen before emission. The circuit thread handles only
routing, filtering, and lightweight state updates.

### Why Small Types Matter

Every field access in the emission hot path costs cache-line bandwidth. The emission dispatch path
touches:

1. The pipe's cached head pointer (field access, not method call)
2. The name for routing (reference equality, not string comparison)
3. The receptor for delivery (direct invocation)

Types that bundle unnecessary state force the CPU to load cache lines that the hot path never reads.
Separate Name, Id, Subject types ensure that emission dispatch touches only what it needs.

### Lifecycle Discipline Enables Zero-Allocation

`Registrar` carries a temporal contract — it is valid only during the subscriber callback. `Window`
is also temporal — it is valid only during the callback that observes a rolling window emission.
These contracts let implementations reuse small framework objects and rolling storage rather than
allocating or copying on every emission. `Flow`, by contrast, is an immutable builder value: a
single Flow configuration can be shared across many materializations and across threads, so the
configuration itself is allocated once and never copied. In all cases the goal is the same — keep
allocation off the hot path. In a system processing hundreds of millions of emissions per second,
eliminating even one allocation per emission is significant.

### Static Nested Classes

The SPI uses static nested classes with explicit references rather than inner classes with implicit
outer-class captures. This eliminates an indirection on every field access. For example, the Emit
job class takes an explicit Inlet reference rather than capturing `Emitter.this`.

### Field Caching

Fields accessed in tight loops are cached in local variables. The Inlet implementation caches
`pipes.head` to avoid a field dereference on every emission. The Valve implementation inlines
`drain()` into `process()` to eliminate method-call overhead.

## 7. Temporal Contracts

The API defines temporal contracts for a small set of types: objects that are valid only within a
specific scope and must not be retained beyond it.

| Type      | Scope                      | Rationale                                            |
|-----------|----------------------------|------------------------------------------------------|
| Registrar | Subscriber callback        | Enables object reuse; prevents dangling registration |
| Window    | Receptor or Fiber callback | Enables rolling storage reuse; prevents stale reads  |
| Closure   | Single consume call        | Ensures block-scoped resource cleanup                |

### The Design Intent

Temporal contracts serve three purposes:

1. **Object reuse**: Implementations may reuse callback-scoped objects across callbacks, eliminating
   allocation in the emission hot path.
2. **Lifecycle clarity**: The temporal contract makes ownership explicit. A Registrar belongs to the
   subscriber callback, and a Window belongs to the callback that observes it. Retaining either one
   creates a dangling reference to internal framework state.
3. **Enforcement flexibility**: The specification allows implementations to choose between detection
   (throwing on misuse) and undefined behavior, depending on whether detection imposes overhead on
   the valid path.

### The Rust Analogy

Temporal contracts are conceptually similar to Rust's lifetime system, where references are valid
only within a defined scope. The difference is enforcement: Rust verifies lifetimes at compile time;
Substrates defines them in the specification and relies on projection-specific enforcement. In Java,
the implementation detects `Registrar` violations via sentinel state — invoking `register` outside
the subscriber callback is caught and reported. Window temporal violations follow the general
SHOULD-detect rule; implementations may instead treat retained Window use as undefined behavior when
detection would add hot-path overhead.

## 8. Lazy Rebuild Subscriptions

The subscription model uses version-tracked lazy rebuild rather than eager synchronization.

### Why Not Eager Rebuild

An eager model would rebuild every channel's pipe list immediately when a subscription changes. In a
conduit with thousands of channels, a single subscribe operation would trigger thousands of
rebuilds — most for channels that may never emit again. This creates a latency spike proportional to
the number of channels.

### How Lazy Rebuild Works

Subscription changes increment a version counter. Each channel caches its own version. On emission,
the channel compares its cached version to the source's current version. If they differ, it rebuilds
its pipe list. If they match, it proceeds directly — O (1) per emission.

This means:

- **Subscription cost is O (1)**: Register the subscription and increment the version counter.
- **Rebuild cost is amortized**: Each channel rebuilds at most once per subscription change, and
  only when it actually emits.
- **Idle channels never rebuild**: Channels that never emit after a subscription change incur zero
  cost.

The tradeoff is eventual consistency: subscription changes are not immediately visible to all
channels. Each channel discovers the change on its next emission. This is acceptable because the
rebuild happens within the circuit context — it is causally ordered with respect to the emission
that triggers it.

## 9. Containment and Hierarchy

Components organize in a strict containment hierarchy:

```
Cortex (root, depth=1)
  └── Circuit (depth=2)
        └── Conduit (depth=3)
              └── Pipe (depth=4)
```

This hierarchy is not merely organizational — it has structural consequences:

- **Hierarchical resource management**: Closing a circuit closes its conduits and their pipes.
  Teardown mirrors construction order.
- **Scoped determinism**: Each circuit is an independent deterministic domain. Cross-circuit
  communication goes through emission (admission to the target's ingress path), preserving
  determinism within each circuit while allowing concurrency across circuits.
- **Stable routing**: Pipe pooling within conduits guarantees that the same name always resolves to
  the same pipe. Once a name-to-pipe mapping exists, it persists for the conduit's lifetime.
  Subscribers can rely on this stability for dynamic topology construction.

## 10. Terminology

The API uses terminology inspired by neuroscience: cortex, circuit, conduit, receptor. This is
deliberate but should not be over-interpreted.

The names were chosen for conceptual clarity, not as claims about biological simulation:

- **Cortex**: The outermost boundary — a factory and root context. Named for the cerebral cortex as
  the outermost layer of neural organization.
- **Circuit**: A self-contained processing unit with internal state and deterministic execution.
  Named for neural circuits — localized networks with specific computational functions.
- **Conduit**: A structured pathway that organizes and routes signals to named destinations. Named
  for physical conduits — channels that guide flow.
- **Receptor**: A callback that responds to received signals. Named for synaptic receptors —
  endpoints that trigger responses to incoming signals.

The specification does not claim to model biological neural networks. However, the design intent
includes exploration of neural-like computational networks: spiking network implementations,
recurrent topologies with feedback loops, and hierarchical organization with different timescales.
The specification's architectural properties — deterministic ordering, cyclic safety, dynamic
topology via subscriptions, sub-3ns emission latency — were shaped by these aspirations.

---

*This document accompanies the Substrates API and explains the reasoning behind its design. For the
formal specification, see SPEC.md. For the specification-level rationale positioned against related
work, see RATIONALE.md. For term definitions, see GLOSSARY.md.*
