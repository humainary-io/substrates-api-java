/// Copyright © 2026 William David Louth
package io.humainary.substrates.api;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Member;
import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.SOURCE;
import static java.util.Objects.requireNonNull;
import static java.util.Spliterator.*;

/// The Substrates API is Humainary's core runtime for deterministic emissions, adaptive control,
/// and mirrored state coordination. Circuits, conduits, channels, pipes, subscribers, subjects,
/// and scopes compose into a nanosecond-latency fabric for observability, adaptability,
/// controllability, and operability workloads.
///
/// Substrates enables building **neural-like computational networks** where values flow through
/// circuits, conduits, and channels in deterministic order, with dynamic subscription-based
/// wiring that adapts to runtime topology without stopping the system.
///
///
/// ## Architecture Overview
///
/// **Data Flow Path**:
///
/// 1. **Circuit** creates **Conduit**
/// 2. **Conduit** creates **Pipe** (lazily, on first access by name)
/// 3. **Pool** provides name-based access to pipes, with composable `pool(fn)` for derived views
/// 4. **Subscriber** attaches **Pipe** to named pipes via **Registrar**
/// 5. Emissions flow: `Pipe → [circuit thread] → Flow/Fiber → Subscriber Pipes`
///
/// **Key Insight**: Pipes are discovered dynamically by subscribers, enabling adaptive
/// topologies that respond to runtime structure without prior knowledge of pipe names.
///
/// ## Threading Model (Foundational)
///
/// **Single-threaded circuit execution** is the foundation of Substrates' design:
///
/// - Every circuit owns exactly **one processing thread** (virtual thread)
/// - All emissions, flows, and subscriber callbacks execute **exclusively on that thread**
/// - **Deterministic ordering**: Emissions observed in the order they were enqueued
/// - **No synchronization needed**: State touched only from circuit thread requires no locks
/// - **Sequential execution**: Only one operation executes at a time per circuit
///
/// **Caller vs Circuit Thread**:
///
/// - **Caller threads** (your code): Enqueue emissions, return immediately
/// - **Circuit thread** (executor): Dequeue and process emissions sequentially
/// - **Performance principle**: Balance work between caller (before enqueue) and circuit
///   (after dequeue). The circuit thread is the bottleneck - shift work to callers when possible.
///
/// This model enables:
/// - Low-latency emissions
/// - Elimination of race conditions and synchronization overhead
/// - Deterministic replay and digital twin synchronization
/// - Safe cyclic topologies (neural-like recurrent networks)
///
/// ## Core Guarantees
///
/// **Deterministic Ordering**:
/// - Emissions are observed in strict enqueue order
/// - Earlier emissions complete before later ones begin
/// - All subscribers see emissions in the same order
///
/// **Eventual Consistency**:
/// - Subscription changes (add/remove) use **lazy rebuild** with version tracking
/// - Pipes detect changes on next emission (not immediately)
/// - No blocking or global coordination required
/// - Lock-free operation with minimal overhead
///
/// **State Isolation**:
/// - Fiber operators maintain independent state per materialization
/// - Subscriber state accessed only from circuit thread (no sync needed)
/// - No shared mutable state between circuits
///
/// ## Performance Considerations
///
/// The Substrates API is designed for low-latency emission processing. Actual performance
/// characteristics depend on the SPI provider implementation.
///
/// **Best practices**:
/// - Keep fiber/flow pipelines short (each stage adds overhead)
/// - Avoid I/O and blocking in subscriber callbacks
/// - Prefer stateless fiber operators over stateful when possible
///
/// ## Runtime Pillars
///
/// - **[Circuit]**: Single-threaded execution engine that drains emissions deterministically.
///     Provides `await()` for external coordination and guarantees memory visibility.
///
/// - **[Conduit]**: Pipe pool factory that routes emissions through named channels.
///     Routes emissions through flow pipelines to registered subscribers.
///
/// - **[Pipe]**: Emission carrier abstraction that routes typed values through flows.
///     Pipes pool by name within a conduit, ensuring stable routing and identity guarantees.
///     Register receptors via [Registrar#register(Receptor)].
///
/// - **[Subscriber]**: Callback interface invoked lazily on first emission to channels. Dynamically attaches
///     pipes to channels, enabling adaptive topologies.
///
/// - **[Subscription]**: Lifecycle handle for canceling subscriber interest. Unregisters
///     pipes from channels via lazy rebuild on next emission.
///
/// - **[Source]**: Event source managing subscription model. Connects subscribers to channels
///     with lazy rebuild synchronization.
///
/// - **[Fiber]**: Same-type per-emission processing recipe. Operators: above, below,
///     chance, change, clamp, deadband, delay, diff, dropWhile, edge, every, guard, high,
///     hysteresis, inhibit, integrate, limit, low, max, min, peek, pulse, range, reduce,
///     relate, replace, rolling, skip, steady, takeWhile, tumble.
///
/// - **[Flow]**: Left-to-right composition surface (optionally type-changing) for assembling pipeline segments.
///     Operators: `map` (appends a type-changing transform),
///     `fiber` (attaches a same-type Fiber at the output side), `flow` (composes with another Flow).
///
/// - **[Subject]**: Hierarchical identity system with unique ID, name, and state. Every
///     substrate component has a subject for precision targeting.
///
/// - **[Scope]**: Structured resource manager that auto-closes registered assets in reverse
///     order, enabling RAII-like lifetime control.
///
/// - **[Registrar]**: Temporary handle for attaching pipes during subscriber callbacks.
///     **Only valid during the callback** - temporal contract.
///
/// ## Design Philosophy
///
/// The Substrates API is built around three principles:
///
/// 1. **Determinism over throughput**: Predictable ordering enables replay, testing, digital twins
/// 2. **Composition over inheritance**: Small interfaces compose into complex behaviors
/// 3. **Explicitness over magic**: No hidden frameworks, clear data flow, no reflection-based wiring
///
/// The API is designed for **exploration of neural-like computational networks** where:
/// - Circuits act as processing nodes with different timescales
/// - Pipes emit signals through dynamic connections
/// - Subscribers rewire topology in response to runtime structure
/// - Fiber operators create temporal dynamics (diff, every, chance, limit)
///
/// This enables emergent computation from substrate primitives with deterministic behavior
/// suitable for production systems.
///
/// ## Interface Segregation
///
/// The API defines 25 interfaces. This is deliberate interface segregation, not fragmentation.
/// Each interface carries exactly one responsibility, and composition builds capability:
///
/// - **[Substrate]**: Identity (has a [Subject]). All observable components are substrates.
/// - **[Resource]**: Lifecycle (has `close()`). Only components that own resources extend Resource.
///   [Pipe] does not — pipes are GC-collected, not explicitly closed. Forcing `close()` onto Pipe
///   would mislead callers into thinking cleanup is required.
/// - **[Source]**: Wiring (has `subscribe()`). Only components that manage subscriptions extend Source.
///   [Pool] does not — pools retrieve; they do not manage subscription lifecycles.
///
/// Composition then builds richer types from these primitives: [Conduit] extends both [Pool]
/// and [Source] because conduits genuinely need both pooling and subscription capabilities. Collapsing
/// Pool and Source into a single interface would force subscription machinery onto every type that
/// only needs name-based retrieval.
///
/// The identity types illustrate the same principle:
///
/// - **[Name]**: Interned hierarchical path. O(1) reference-equality comparison. Immutable.
/// - **[Id]**: Unique instance identity. Two circuits may share a name but never an Id.
/// - **[Subject]**: Composes Name + Id + [State]. The unifier, not a replacement for its parts.
/// - **[Extent]**: Hierarchy traversal (parent/child). Shared by Name, Subject, and [Scope].
///
/// These are not overlapping abstractions — they are orthogonal concerns. Merging them creates a
/// god-object that drags unnecessary state through hot paths. On the emission path, only Name is
/// needed for routing; Subject is only needed for subscriber callbacks; Id is only needed for
/// identity checks. Keeping them separate enables the sub-3ns emission target.
///
/// ## What Substrates Is Not
///
/// **Not Reactive Streams.** Substrates has no backpressure protocol. Reactive Streams uses
/// `request(n)` demand signaling to throttle producers. Substrates rejects this for two reasons:
/// blocking the caller when the queue is full introduces scheduling-dependent ordering (breaking
/// determinism), and dropping emissions under load creates gaps in the replay log (breaking
/// replay). Instead, admission control happens *inside* the circuit via [Fiber] operators —
/// `limit()` caps downstream volume, `every()` and `chance()` reduce by interval or probability — where
/// it does not affect ingress ordering. See the specification rationale (RATIONALE.md §4) for
/// the full analysis.
///
/// **Not an Actor Framework.** The Actor Model shares isolated state with message passing, but
/// traditional actor implementations (Erlang/OTP, Akka) do not guarantee processing order when
/// multiple senders target the same actor — mailbox order depends on arrival timing. Substrates
/// mandates strict enqueue order within a circuit. Additionally, actors process messages
/// independently: message A's side effects may interleave with message B's processing. In
/// Substrates, the transit queue ensures that all cascading effects of emission A complete
/// atomically before emission B begins. See RATIONALE.md §2.
///
/// **Not a Channel Abstraction.** There is no `Channel` type in the API. "Channel" and "inlet"
/// describe *roles* that pipes play relative to the circuit boundary — positional, not structural.
/// [Registrar] has a temporal contract: it exists only during the subscriber callback, enabling
/// zero-allocation object reuse on the emission path. A unified subscribe/publish abstraction
/// would hide this critical lifecycle constraint.
///
/// **Not Standard Patterns.** The sub-3ns emission latency target (336M ops/sec) requires that
/// types on the hot path carry minimal state. Separate [Name], [Id], [Subject] types enable O(1)
/// identity checks without dragging state through emission dispatch. [Registrar]'s temporal
/// contract enables zero-allocation pipe registration during subscriber callbacks, while [Flow]'s
/// immutable builder model lets a single configuration be shared across materializations without
/// hot-path allocation. These are structural requirements that consolidated abstractions cannot
/// satisfy.
///
/// ## Further Reading
///
/// - **SUBSTRATES.md** (this module): Accessible design rationale — why determinism, why ISP,
///   the performance model, temporal contracts, and the dual-queue model explained.
/// - **RATIONALE.md** (substrates-api-spec): Specification companion — formal design decisions
///   positioned against related work (Reactive Streams, Actor Model, Berkeley Reactor).
/// - **SPEC.md** (substrates-api-spec): Formal specification with conformance requirements.
/// - **GLOSSARY.md** (this module): Term definitions for all core concepts.
///
/// ## Practical Applications
///
/// Substrates powers:
///
/// - **Observability & Instrumentation**: Nanosecond-latency telemetry pipelines capturing
///   metrics, traces, and logs without impacting application performance
///
/// - **Adaptive Service Control**: Closed-loop automation adjusting to runtime conditions with
///   deterministic state machines and feedback loops
///
/// - **Digital Twin Synchronization**: Mirrored state coordination with deterministic replay
///   for simulation, testing, and what-if analysis
///
/// - **Operational Safety Systems**: Predictable teardown and composable lifetimes via Scope
///   for systems demanding graceful degradation
///
/// - **Neural Network Experimentation**: Substrate primitives compose into spiking networks,
///   recurrent topologies, and adaptive learning systems
///
/// ## When to Use Substrates
///
/// **Good fit**:
/// - High-frequency event processing (millions+ events/sec)
/// - Deterministic ordering required (replay, testing, digital twins)
/// - Dynamic topology adaptation (auto-wiring, runtime discovery)
/// - Low-latency requirements (sub-microsecond overhead)
/// - Cyclic data flow (feedback loops, recurrent networks)
///
/// **Not a good fit**:
/// - Simple request/response patterns (use standard frameworks)
/// - Heavy I/O-bound workloads (circuit thread blocks)
/// - Distributed consensus (single-circuit design, use external coordination)
/// - Trivial transformations (overhead not justified)
///
/// ## Provider Bootstrapping
///
/// The Substrates framework uses a two-tier discovery mechanism to locate and load the
/// implementation provider at runtime:
///
/// 1. **System Property (Primary)**: Set the system property `io.humainary.substrates.spi.provider`
///    to the fully qualified class name of your provider implementation. The class must extend
///    [io.humainary.substrates.spi.CortexProvider] and have a public no-arg constructor.
///
/// 2. **ServiceLoader (Fallback)**: If no system property is set, provider discovery falls back
///    to Java's [ServiceLoader] mechanism. Create a file at
///    `META-INF/services/io.humainary.substrates.spi.CortexProvider` containing the
///    fully qualified class name of your provider implementation.
///
/// The provider is loaded lazily on the first call to [#cortex()] using the initialization-on-demand
/// holder idiom, ensuring thread-safe singleton initialization without synchronization overhead.
///
/// See [io.humainary.substrates.spi.CortexProvider] for implementation details.
///
/// ## Entry Points
///
/// - **[Cortex#circuit()]**: Create a circuit to begin
/// - **[Circuit#conduit(Class)]**: Create pipe lookups with emission routing
/// - **[Pool#get(Name)]**: Obtain pipes by name
/// - **[Source#subscribe(Subscriber)]**: Dynamically discover channels
///
/// @author William David Louth
/// @since 1.0

public interface Substrates {

  /// The system property key used to specify the [io.humainary.substrates.spi.CortexProvider] implementation class.
  /// The specified class must extend [io.humainary.substrates.spi.CortexProvider] and have a no-arg constructor.
  /// If not set, provider discovery falls back to [ServiceLoader].
  String PROVIDER_PROPERTY = "io.humainary.substrates.spi.provider";


  /// Indicates a type that serves primarily as an abstraction for other types.
  @SuppressWarnings ( "WeakerAccess" )
  @Documented
  @Retention ( SOURCE )
  @Target ( TYPE )
  @interface Abstract {
  }


  /// A capture of an emitted value from a channel with its associated subject.
  ///
  /// Captures are produced by [Reservoir]s, which subscribe to sources and record emissions
  /// along with the channel subject that produced them. Each capture pairs:
  /// - The emitted value (of type E)
  /// - The channel's subject that emitted it
  ///
  /// This allows inspection of both what was emitted and where it came from, which is
  /// essential for testing, debugging, and telemetry scenarios.
  ///
  /// ## Primary Use Cases
  ///
  /// 1. **Testing**: Assert that expected emissions occurred with correct values
  /// 2. **Debugging**: Inspect emission history to trace data flow
  /// 3. **Telemetry**: Record and analyze emission patterns
  /// 4. **Replay**: Capture emissions for later replay or analysis
  ///
  ///
  /// ## Memory Considerations
  ///
  /// Reservoirs buffer captures in memory. For long-running scenarios:
  /// - Call [Reservoir#drain()] periodically to clear buffered captures
  /// - Use stream filtering on drain() results to selectively process captures
  /// - Consider direct [Pipe] subscription for real-time processing without buffering
  ///
  /// @param <E> the class type of emitted values
  /// @see Reservoir#drain()
  /// @see Pipe
  /// @see Subject

  @Tenure ( Tenure.EPHEMERAL )
  @Provided
  interface Capture < E > {

    /// Returns the emitted value.
    ///
    /// @return The emitted value

    @NotNull
    E emission ();


    /// Returns the subject of the pipe that emitted the value.
    ///
    /// The subject identifies which pipe produced this emission, providing access to
    /// the pipe's name, ID, and state.
    ///
    /// @return The subject of the pipe that emitted this value
    /// @see Subject
    /// @see Pipe
    /// @since 2.0

    @NotNull
    Subject < Pipe < E > > subject ();

  }


  /// A computational network of conduits, pipes, and subscribers.
  /// Circuit serves as the central processing engine that manages data flow across
  /// the system, providing precise ordering guarantees for emitted events and
  /// coordinating the interaction between various components.
  ///
  /// Circuits can create and manage conduits, allowing for
  /// complex event processing pipelines to be constructed and maintained.
  ///
  /// ## Subject Hierarchy
  ///
  /// Circuits are created by [Cortex#circuit()] and have the cortex subject as their parent,
  /// making them depth=2 in the subject hierarchy (Cortex → Circuit). All conduits
  /// created by a circuit have the circuit subject as their parent, establishing a clear
  /// ownership hierarchy for lifecycle management and identity tracking.
  ///
  /// ## Terminology: Inlet and Outlet Roles
  ///
  /// **All pipes are consumers** (implementing the [Pipe] interface with `emit()`).
  /// The terms **"inlet"** and **"outlet"** describe **roles that pipes play relative
  /// to the circuit boundary**, not distinct types:
  ///
  /// - **Inlet role**: Pipes on the ingress side receiving external data into the circuit
  ///   (e.g., channel pipes, derived pool results)
  /// - **Outlet role**: Pipes on the egress side receiving processed data from the circuit
  ///   (e.g., subscriber pipes)
  ///
  /// In documentation, "inlet" and "outlet" refer to these contextual roles, not
  /// separate interfaces or types. A pipe's role is determined by its position in
  /// the data flow topology.
  ///
  /// ## Threading and Execution Model
  ///
  /// Circuits guarantee that every emission is processed on a single execution thread owned
  /// by the circuit. Callers may emit from any thread, but observable effects always occur
  /// on that circuit thread. Implementations must provide:
  ///
  /// - **Deterministic ordering**: Emissions are observed in the order they were accepted
  ///       by the circuit. Earlier emissions complete before later ones begin execution.
  ///       When multiple caller threads emit concurrently, the relative ordering of their
  ///       emissions depends on the ingress queue's synchronization and may differ across
  ///       runs; determinism applies to the total observed sequence within a single run.
  /// - **Circuit-thread confinement**: State touched only from the circuit thread requires
  ///       no additional synchronization. The circuit thread is the sole accessor of circuit-confined state.
  /// - **Bounded enqueue cost**: Caller threads do not execute circuit work but may briefly
  ///       coordinate with the circuit while enqueueing work; implementations must keep this
  ///       path short and responsive under contention. The enqueue operation itself must not
  ///       block the caller or discard the emission while the circuit is open — the ingress
  ///       queue is effectively unbounded (see SPEC §5.6)
  /// - **Sequential execution**: Only one emission executes at a time per circuit. No
  ///       concurrent execution occurs within a single circuit.
  /// - **Memory visibility**: The circuit thread guarantees visibility of all state updates
  ///       made during emission processing. Coordination with external threads requires
  ///       explicit barriers via [#await()].
  ///
  /// ## Cascading Emission Ordering (Dual Queue Model)
  ///
  /// Circuits use **two queues** to manage emissions with deterministic priority ordering:
  ///
  /// - **Ingress queue**: Shared queue for emissions from external threads (caller-side)
  /// - **Transit queue**: Local queue for emissions originating from circuit thread during processing
  ///
  /// When processing an emission from the ingress queue triggers additional emissions from within
  /// the circuit thread, those cascading emissions are added to the **transit queue**, which has
  /// **priority** over the ingress queue:
  ///
  /// **Key characteristics**:
  /// - **No call stack recursion**: All emissions enqueue rather than invoke nested calls
  /// - **Stack safe**: Even deeply cascading chains don't overflow the stack
  /// - **Priority processing**: Transit queue drains completely before returning to ingress queue
  /// - **Causality preservation**: Cascading effects complete before processing next external input
  /// - **Atomic computations**: Cascading chains appear atomic to external observers
  /// - **Neural-like dynamics**: Enables proper signal propagation in feedback networks
  ///
  /// When processing transit work that itself emits, those emissions are added to the **back of
  /// the transit queue** (not recursively invoked), maintaining the iterative queue-based model.
  ///
  /// ## Performance Expectations
  ///
  /// The API is designed for ultra-low-latency emission paths. Implementations should minimize
  /// overhead on the caller side and avoid heavy computation on the circuit thread so the queue
  /// continues to drain predictably. The circuit thread is the performance bottleneck - balance
  /// work between caller threads (emission side) and the circuit thread (processing side).
  ///
  /// @see Cortex#circuit()
  /// @see Conduit

  @Tenure ( Tenure.EPHEMERAL )
  @Provided
  non-sealed interface Circuit
    extends Source < State, Circuit > {

    /// Blocks until the circuit's queue is empty, establishing a happens-before relationship
    /// with all previously enqueued emissions.
    ///
    /// ## Synchronization
    ///
    /// Ensures all emissions enqueued before await() complete before await() returns, making
    /// their state updates visible to the caller. This is the primary coordination mechanism
    /// between external threads and the circuit thread.
    ///
    /// ## Thread Safety
    ///
    /// Multiple threads may call await() concurrently, each blocking independently until the
    /// queue drains. Cannot be called from the circuit thread (would deadlock).
    ///
    /// ## Shutdown
    ///
    /// After circuit closure, returns immediately to enable clean shutdown without indefinite blocking.
    ///
    /// @throws IllegalStateException if called from within the circuit's thread

    void await ();


    /// Closes the circuit, releasing its processing thread and associated resources.
    ///
    /// ## Shutdown Process
    ///
    /// Closing a circuit initiates a three-phase shutdown (Active → Closing → Closed):
    /// 1. **Closing**: New emissions from caller threads are no longer accepted, and
    ///    the processing thread is signaled to terminate.
    /// 2. Queued emissions drain (implementation-specific).
    /// 3. **Closed**: Conduits, channels, and all associated resources are released.
    ///
    /// ## Non-Blocking Semantics
    ///
    /// This method is **non-blocking** - it marks the circuit for closure and returns
    /// immediately. The processing thread drains the queue and terminates asynchronously.
    /// Use [#await()] before close() to ensure all pending work completes:
    ///
    ///
    /// ## Post-Close Behavior
    ///
    /// After close():
    /// - [#await()] returns immediately (won't block indefinitely)
    /// - Queued operations — emissions, subscribe, and subscription close — whose
    ///   ingress-queue position falls at-or-after the circuit's own close job are
    ///   silently dropped on the circuit thread. Per the post-close operation
    ///   semantics in SPEC §9.1, such operations **MUST NOT** throw an error in
    ///   the caller context. Implementations may log or report them via a side
    ///   channel, but the caller cannot expect synchronous error delivery.
    ///   Callers that need to confirm no further operations will take effect
    ///   should use [#await()] for synchronization.
    /// - [#conduit(Name, Class)] may throw [IllegalStateException]; conduits created
    ///   after close are effectively inert since the processing thread has terminated.
    ///   (Factory operations like `conduit()` are synchronous and therefore may
    ///   raise synchronously — this is a distinct category from queued operations.)
    /// - Repeated close() calls are safe (idempotent)
    ///
    /// ## Thread Safety
    ///
    /// Thread-safe — can be called concurrently from multiple threads. Only the first
    /// invocation performs shutdown; subsequent calls are no-ops.
    ///
    /// @see #await()

    @Queued
    @Idempotent
    @Override
    void close ();


    /// Returns a conduit that will use this circuit to process and transfer values emitted.
    ///
    /// Uses this circuit's subject name for the conduit. The emission type is inferred
    /// from the target context (assignment, method argument, or explicit witness). For
    /// an explicit conduit name, use [#conduit(Name, Class)]; for an explicit type
    /// witness, use [#conduit(Class)].
    ///
    /// The conduit returns raw pipes via [Pool#get(Name)]. Use [Pool#pool(Function)]
    /// to create derived views with flow processing or domain-specific wrappers.
    ///
    /// @param <E> the class type of emitted values
    /// @return A conduit with the circuit's name, using this circuit for emission processing
    /// @see Conduit
    /// @see Pool
    /// @see Pipe
    /// @see #conduit(Class)
    /// @see #conduit(Name, Class)
    /// @since 2.1

    @New
    @NotNull
    < E > Conduit < E > conduit ();


    /// Returns a conduit that will use this circuit to process and transfer values emitted.
    ///
    /// Convenience overload that uses the class parameter to drive type inference at the
    /// call site. Equivalent to [#conduit()] with an explicit type witness.
    ///
    /// @param type the class of the emission type (used for type inference)
    /// @param <E>  the class type of emitted values
    /// @return A conduit with the circuit's name, using this circuit for emission processing
    /// @throws NullPointerException if the specified type is `null`
    /// @see Conduit
    /// @see Pool
    /// @see Pipe
    /// @see #conduit()
    /// @see #conduit(Name, Class)
    /// @since 2.0

    @New
    @NotNull
    default < E > Conduit < E > conduit (
      @NotNull final Class < E > type
    ) {

      requireNonNull ( type );

      return conduit ();

    }


    /// Returns a conduit that will use this circuit to process and transfer values emitted.
    ///
    /// @param name the name given to the conduit's subject
    /// @param type the class type of emitted values (used for type inference)
    /// @param <E>  the class type of emitted values
    /// @return A conduit using this circuit for emission processing
    /// @throws NullPointerException if the specified name or type is `null`
    /// @throws Fault                if the name parameter is not a runtime-provided implementation
    /// @since 2.0

    @New
    @NotNull
    < E > Conduit < E > conduit (
      @NotNull Name name,
      @NotNull Class < E > type
    );


    /// Returns a conduit with the specified routing behavior.
    ///
    /// Convenience method that uses this circuit's subject name for the conduit.
    ///
    /// @param type    the class type of emitted values (used for type inference)
    /// @param routing controls how emissions are dispatched within the conduit
    /// @param <E>     the class type of emitted values
    /// @return A conduit with the circuit's name and specified routing
    /// @throws NullPointerException if any parameter is `null`
    /// @see Routing
    /// @see #conduit(Name, Class, Routing)
    /// @since 2.0

    @New
    @NotNull
    default < E > Conduit < E > conduit (
      @NotNull final Class < E > type,
      @NotNull final Routing routing
    ) {

      requireNonNull ( type );
      requireNonNull ( routing );

      return conduit (
        subject ().name (),
        type,
        routing
      );

    }


    /// Returns a conduit with the specified name and routing behavior.
    ///
    /// @param name    the name given to the conduit's subject
    /// @param type    the class type of emitted values (used for type inference)
    /// @param routing controls how emissions are dispatched within the conduit
    /// @param <E>     the class type of emitted values
    /// @return A conduit with the specified name and routing
    /// @throws NullPointerException if any parameter is `null`
    /// @throws Fault                if the name parameter is not a runtime-provided implementation
    /// @see Routing
    /// @since 2.0

    @New
    @NotNull
    < E > Conduit < E > conduit (
      @NotNull Name name,
      @NotNull Class < E > type,
      @NotNull Routing routing
    );


    /// Returns a pipe that queues emissions on this circuit but performs no work
    /// when they are processed.
    ///
    /// The returned pipe adheres to the circuit's queue processing: each call to
    /// [Pipe#emit(Object)] enqueues a job on the circuit's queue. When the job is
    /// dequeued on the circuit thread it is a no-op — the emission is discarded.
    ///
    /// This is the concise form of `circuit.pipe(Receptor.of())` — same timing,
    /// ordering, and `await()` semantics, without allocating a receptor wrapper.
    ///
    /// ## Use Cases
    ///
    /// - Sink / black-hole pipe in tests and benchmarks
    /// - Placeholder where a circuit-owned pipe is required but no processing is needed
    /// - Back-pressure point: scheduled work still consumes queue capacity
    ///
    /// @param <E> the class type of emitted values
    /// @return A pipe that queues emissions on this circuit and discards them
    /// @see Pipe
    /// @see Receptor#NOOP
    /// @since 2.3

    @New
    @NotNull
    < E > Pipe < E > pipe ();


    /// Returns a pipe that asynchronously dispatches emissions to the target pipe
    /// via this circuit's event queue.
    ///
    /// This method creates a pipe wrapper that breaks synchronous call chains by
    /// routing emissions through the circuit's queue. This prevents stack overflow
    /// in deep pipe chains and enables cyclic pipe connections, which are essential
    /// for neural-like network topologies with recurrent connections.
    ///
    /// ## Queued Dispatch
    ///
    /// Each emission requires one enqueue operation and one circuit-thread execution:
    ///
    /// 1. Caller thread invokes `wrappedPipe.emit(value)` from any thread
    /// 2. Value is enqueued to the circuit's queue (non-blocking enqueue)
    /// 3. Caller thread returns immediately
    /// 4. Circuit thread later dequeues and invokes `target.emit(value)`
    ///
    /// The target pipe receives emissions on the circuit's processing thread, ensuring
    /// sequential execution and deterministic ordering. **No synchronization is required
    /// within the target pipe** as it executes exclusively on the circuit thread.
    ///
    /// ## Enabling Cyclic Topologies
    ///
    /// Without async dispatch, cyclic pipe connections would cause infinite recursion:
    ///
    /// The queue breaks the synchronous call chain, enabling recurrent networks.
    ///
    /// ## Use Cases
    ///
    /// - Deep hierarchical structures where synchronous propagation risks stack overflow
    /// - Cyclic pipe networks for feedback loops and recurrent processing
    /// - Chaining multiple transformation stages without nesting calls
    /// - Offloading work from caller thread to circuit thread
    ///
    /// ## Same-Circuit Optimization
    ///
    /// If the target pipe already belongs to this circuit, it is returned as-is
    /// without wrapping. No additional allocation or async indirection is added.
    ///
    /// @param target the pipe that will receive emissions asynchronously
    /// @param <E>    the class type of emitted values
    /// @return A pipe that asynchronously dispatches to the target pipe, or the target itself if already on this circuit
    /// @throws NullPointerException if the target is `null`
    /// @throws Fault                if the target parameter is not a runtime-provided implementation
    /// @see Pipe

    @New ( conditional = true )
    @NotNull
    < E > Pipe < E > pipe (
      @NotNull Pipe < E > target
    );


    /// Returns a pipe that asynchronously dispatches emissions to a receptor
    /// via this circuit's event queue.
    ///
    /// This is the primary method for creating async pipes from receptors. The receptor
    /// receives emissions on the circuit's processing thread, ensuring sequential
    /// execution and deterministic ordering.
    ///
    /// @param receptor the receptor that will receive emissions asynchronously
    /// @param <E>      the class type of emitted values
    /// @return A pipe that asynchronously dispatches to the receptor
    /// @throws NullPointerException if the receptor is `null`
    /// @see Receptor
    /// @since 2.0

    @New
    @NotNull
    < E > Pipe < E > pipe (
      @NotNull @Queued Receptor < ? super E > receptor
    );


    /// Creates a subscriber with the specified name and subscribing behavior, scoped to this circuit.
    ///
    /// The subscriber is a child of this circuit in the subject hierarchy, with subject path
    /// `circuit-name.subscriber-name`. This establishes that the subscriber observes the ordered
    /// view provided by this circuit's single-threaded processing.
    ///
    /// When subscribed to a source, the subscriber's behavior is invoked lazily for each
    /// channel when it receives its first emission after subscription registration. The behavior
    /// receives the channel's subject and a registrar, allowing it to dynamically register pipes
    /// to receive emissions from that specific channel.
    ///
    /// ## Exception Handling
    ///
    /// The subscriber callback is external code as defined by SPEC §15.4. If it
    /// throws an uncaught exception during a channel rebuild, the exception does
    /// not propagate to the circuit's dispatch loop. Other subscriptions on the
    /// same channel, and the rebuild of other channels on this subscriber, are
    /// unaffected. Whether a partial registration performed before the throw is
    /// retained or discarded is implementation-defined, but the circuit MUST NOT
    /// present a partial registration as a completed one (see §15.4 invariant 3).
    ///
    /// @param <E>        the emission type
    /// @param name       the name to be used by the subject assigned to the subscriber
    /// @param subscriber the subscribing behavior to be applied when a channel receives its first emission after subscription
    /// @return A new subscriber scoped to this circuit
    /// @throws NullPointerException if the name or subscriber is `null`
    /// @throws Fault                if the name parameter is not a runtime-provided implementation
    /// @see Source#subscribe(Subscriber)
    /// @see Registrar#register(Pipe)
    /// @since 2.0

    @New
    @NotNull
    < E > Subscriber < E > subscriber (
      @NotNull Name name,
      @NotNull @Queued BiConsumer < ? super Subject < Pipe < E > >, ? super Registrar < E > > subscriber
    );

  }


  /// A utility interface for scoping the work performed against a resource.
  ///
  /// Closures provide a single-use, block-scoped view over a [Resource]. They are typically
  /// acquired from a [Scope] via [Scope#closure(Resource)] and give callers a way to
  /// execute work while guaranteeing the resource is closed when the block exits.
  ///
  /// ## Contract
  ///
  /// - The supplied resource remains open for the duration of [#consume(Consumer)] and is closed
  ///   exactly once when the call finishes (successfully or exceptionally).
  /// - Each closure is single-use; once `consume` returns, the resource must be considered closed and
  ///   callers should request a fresh closure for further work.
  /// - Exceptions thrown by the consumer propagate to the caller after the resource has been closed;
  ///   implementations must not swallow unchecked errors.
  /// - Invoking `consume` after the owning scope has been closed results in a no-op; the consumer is
  ///   not called and the resource remains (or becomes) closed.
  /// - Implementations are not required to be thread-safe. Callers should confine each closure to the
  ///   thread that acquired it.
  ///
  /// @param <R> the resource type managed by this closure

  @Tenure ( Tenure.EPHEMERAL )
  @Utility
  @Temporal
  @Provided
  interface Closure < R extends Resource > {

    /// Calls a consumer, with an acquired resource, within an automatic resource management (ARM) scope.
    ///
    /// Implementations must invoke the consumer exactly once while the resource is open, then close
    /// the resource in a `finally` block. If the consumer throws, the resource is still closed and
    /// the original exception is rethrown to the caller. If the owning scope is already closed, the
    /// consumer is not invoked and the method returns immediately.
    ///
    /// @param consumer the consumer to be executed within an automatic resource (ARM) management scope.
    ///

    void consume (
      @NotNull Consumer < ? super R > consumer
    );

  }


  /// A factory for pooled pipes that emit events through named channels.
  ///
  /// Conduit combines two capabilities:
  /// - **Pool**: Creates and caches pipe instances by name, with composable `pool(fn)` for derived views
  /// - **Source**: Emits events and allows subscription to channel creation
  ///
  /// Conduits serve as the primary mechanism for creating emission abstractions
  /// that route values through the circuit's processing engine.
  ///
  /// ## Pipe Pool
  ///
  /// A conduit returns raw pipes via [Pool#get(Name)]. All transformations — flow
  /// processing and domain-specific wrapping — are expressed as [Pool#pool(Function)]
  /// composition:
  ///
  /// ```java
  /// var conduit    = circuit.conduit(Signal.class);              // Conduit<Signal>
  /// var pipe       = conduit.get(name);                          // Pipe<Signal>
  /// var processed  = conduit.pool(flow::pipe);                   // Pool<Pipe<String>>
  /// var situations = conduit.pool(Situation::new);               // Pool<Situation>
  /// ```
  ///
  /// ## Pool Semantics (via Pool)
  ///
  /// Conduits provide name-based pipe pooling with caching:
  /// - [Pool#get(Name)] retrieves or creates a pipe for the given name
  /// - [Pool#get(Subject)] convenience method extracts name from subject
  /// - [Pool#get(Substrate)] convenience method extracts name from substrate
  /// - Same name always returns the same pipe instance (cached)
  /// - Different names create different pipes with separate channels
  /// - Each pipe has its own unique channel and subject
  ///
  /// **Identity guarantee**: Pipes with the same name are identical objects.
  ///
  /// ## Derived Pools (via Pool)
  ///
  /// [Pool#pool(Function)] creates cached derived views:
  /// - **Domain wrappers**: `conduit.pool(Situation::new)` — wrap each pipe in a domain type
  /// - **Flow processing**: `conduit.pool(flow::pipe)` — apply flow pipeline to each pipe
  /// - **Chaining**: Derived pools support further `pool(fn)` composition
  ///
  /// ## Lazy Subscriber Callbacks (via Source)
  ///
  /// Subscriber callbacks are invoked lazily during emission processing:
  /// - First call to [Pool#get(Name)] creates new channel (no callbacks invoked)
  /// - First emission to that channel triggers rebuild on circuit thread
  /// - During rebuild, subscriber callback is invoked for newly encountered channels
  /// - Subscriber receives [Subject] of the channel and [Registrar] to attach pipes
  ///
  /// ## Threading Model
  ///
  /// Conduit operations are split between caller thread and circuit thread:
  ///
  /// **Caller thread** (synchronous):
  /// - [Pool#get(Name)] creates and caches channels immediately (thread-safe)
  /// - `subscribe(subscriber)` enqueues registration to circuit thread
  ///
  /// **Circuit thread** (asynchronous):
  /// - Subscriber registration completes and increments version
  /// - Emissions trigger lazy rebuild when version mismatch detected
  /// - Rebuild invokes subscriber callbacks for newly encountered channels
  /// - Emissions dispatched to registered pipes
  ///
  /// ## Lifecycle
  ///
  /// Conduits are created via [Circuit#conduit(Class)]:
  /// 1. **Created**: Circuit creates conduit with emission type
  /// 2. **Active**: Pipes created on-demand via `get()`
  /// 3. **Closed**: Via [Resource#close()]
  ///
  /// Pipe references held by callers remain valid after [Resource#close()]
  /// and are reclaimed by the garbage collector once unreachable.
  ///
  /// @param <E> the class type of emitted values (what flows through channels)
  /// @see Pipe
  /// @see Pool
  /// @see Source
  /// @see Circuit#conduit(Class)
  /// @see Subscriber

  @Tenure ( Tenure.EPHEMERAL )
  @Provided
  non-sealed interface Conduit < E >
    extends Pool < Pipe < E > >,
            Source < E, Conduit < E > > {

    /// Returns a derived pool that prepends a flow to each channel's pipe.
    ///
    /// This is the concise form of `pool(flow::pipe)` — each named pipe in
    /// the conduit receives a flow-wrapped upstream pipe, so emissions of
    /// type T flow through the operators and land in the conduit's Pipe<E>
    /// after transformation.
    ///
    /// ```java
    /// // Conduit<String> receives Strings; the pool lets you emit Integers
    /// // that are mapped to String before reaching the conduit.
    /// Pool<Pipe<Integer>> inputs =
    ///   conduit.pool(cortex.flow(Integer.class).map(Object::toString));
    /// ```
    ///
    /// @param <T>  the input emission type (upstream of the flow)
    /// @param flow the flow that transforms emissions from T into E
    /// @return A derived pool whose pipes accept T and forward E into this conduit
    /// @throws NullPointerException if flow is `null`
    /// @throws Fault                if the flow is not a runtime-provided implementation
    /// @see Pool#pool(Function)
    /// @see Flow
    /// @since 2.3

    @New
    @NotNull
    < T > Pool < Pipe < T > > pool (
      @NotNull Flow < T, E > flow
    );


    /// Returns a derived pool that applies a fiber to each channel's pipe.
    ///
    /// This is the concise form of `pool(fiber::pipe)` — each named pipe in
    /// the conduit is wrapped with the fiber, yielding a pool of pipes that
    /// apply per-emission operators (`diff`, `guard`, `limit`, `above`, `below`,
    /// etc.) without changing the emission type.
    ///
    /// ```java
    /// Pool<Pipe<Integer>> deduped =
    ///   conduit.pool(cortex.fiber(Integer.class).diff().limit(100));
    /// ```
    ///
    /// @param fiber the fiber that processes emissions of type E
    /// @return A derived pool whose pipes apply the fiber before the conduit's pipes
    /// @throws NullPointerException if fiber is `null`
    /// @throws Fault                if the fiber is not a runtime-provided implementation
    /// @see Pool#pool(Function)
    /// @see Fiber
    /// @since 2.3

    @New
    @NotNull
    Pool < Pipe < E > > pool (
      @NotNull Fiber < E > fiber
    );

  }


  /// The main entry point into the underlying substrates runtime.
  ///
  /// The Cortex serves as the primary factory for creating runtime components including
  /// circuits, names, scopes, reservoirs, slots, states, and subscribers. Each component type
  /// can be created with or without an explicit name, and the Cortex manages the interning
  /// of names to ensure identity-based equality for equivalent hierarchies.
  ///
  /// ## Subject Hierarchy
  ///
  /// Cortex extends [Substrate] and serves as the **root of the subject hierarchy** for all
  /// runtime components. All circuits created by a cortex have the cortex subject as their
  /// parent, establishing the hierarchy:
  ///
  /// ```
  /// Cortex (root, depth=1)
  ///   └── Circuit (depth=2)
  ///         └── Conduit (depth=3)
  ///               └── Pipe, Subscription, etc. (depth=4)
  /// ```
  ///
  /// This hierarchical structure enables:
  /// - **Containment queries**: Verify components belong to the same cortex
  /// - **Ancestor traversal**: Navigate upward to find owning cortex
  /// - **Lifecycle management**: Track all components created by a cortex
  /// - **Identity tracking**: All components have fully-qualified subject paths
  ///
  /// @see Circuit
  /// @see Name
  /// @see Scope
  /// @see Reservoir
  /// @see Slot
  /// @see State
  /// @see Subscriber
  /// @see Substrate

  @Tenure ( Tenure.INTERNED )
  @Provided
  non-sealed interface Cortex
    extends Substrate < Cortex > {

    /// Returns a newly created circuit instance with a generated name.
    ///
    /// Each circuit maintains its own event processing queue and guarantees ordering
    /// of emissions. The returned circuit must be closed when no longer needed to
    /// release resources.
    ///
    /// @return A new circuit
    /// @see Circuit#close()
    /// @see Circuit#await()

    @New
    @NotNull
    Circuit circuit ();


    /// Returns a newly created circuit instance with the specified name.
    ///
    /// Each circuit maintains its own event processing queue and guarantees ordering
    /// of emissions. The returned circuit must be closed when no longer needed to
    /// release resources.
    ///
    /// @param name the name assigned to the circuit's subject
    /// @return A new circuit
    /// @throws NullPointerException if the specified name is `null`
    /// @throws Fault                if the name parameter is not a runtime-provided implementation
    /// @see Circuit#close()
    /// @see Circuit#await()

    @New
    @NotNull
    Circuit circuit (
      @NotNull Name name
    );


    /// Returns the [Current] representing the execution context.
    ///
    /// This method returns the execution context from which this method is invoked,
    /// analogous to `Thread.currentThread()` in Java. The returned [Current] is a
    /// [Substrate] that provides access to its underlying [Subject], which includes
    /// identity, naming, and state information about the execution context.
    ///
    /// Example usage:
    ///
    /// ```java
    /// var current = cortex.current ();
    /// var id = current.subject ().id ();
    /// var name = current.subject ().name ();
    /// ```
    ///
    /// @return The current execution context
    /// @see Current
    /// @see Subject

    @NotNull
    Current current ();


    /// Returns an empty identity fiber.
    ///
    /// The returned fiber passes all values of type `E` through unchanged.
    /// Chain operators to build it, then attach via [Fiber#pipe(Pipe)]
    /// or wrap as a Flow via [#flow(Fiber)].
    ///
    /// @param <E> the emission type
    /// @return An empty identity fiber
    /// @see Fiber
    /// @see #fiber(Class)
    /// @since 2.2

    @New
    @NotNull
    < E > Fiber < E > fiber ();


    /// Returns an empty identity fiber for the specified emission type.
    ///
    /// Convenience overload that uses the class parameter to drive type inference
    /// at the call site.
    ///
    /// @param type the class of the emission type (used for type inference)
    /// @param <E>  the emission type
    /// @return An empty identity fiber
    /// @throws NullPointerException if type is `null`
    /// @see Fiber
    /// @see #fiber()
    /// @since 2.2

    @New
    @NotNull
    default < E > Fiber < E > fiber (
      @NotNull final Class < E > type
    ) {

      requireNonNull ( type );

      return fiber ();

    }


    /// Returns an empty identity flow.
    ///
    /// The returned flow passes all values through unchanged (`I == O`).
    /// Chain operators to build the pipeline, then pass the flow to
    /// [Flow#pipe(Pipe)]. The emission type is inferred from the
    /// target context (assignment, method argument, or explicit witness).
    ///
    /// @param <E> the emission type
    /// @return An empty identity flow
    /// @see Flow
    /// @see #flow(Class)
    /// @since 2.0

    @New
    @NotNull
    < E > Flow < E, E > flow ();


    /// Returns an empty identity flow for the specified input type.
    ///
    /// Convenience overload that uses the class parameter to drive type inference
    /// at the call site. The class pins the flow's **input** type `E` (what callers
    /// emit); the output type starts as `E` and is extended by chained operators
    /// (`map` appends output transformations; `fiber` appends a Fiber).
    ///
    /// @param type the class of the input emission type (used for type inference)
    /// @param <E>  the input emission type
    /// @return An empty identity flow on type `E`
    /// @throws NullPointerException if type is `null`
    /// @see Flow
    /// @see #flow()
    /// @since 2.0

    @New
    @NotNull
    default < E > Flow < E, E > flow (
      @NotNull final Class < E > type
    ) {

      requireNonNull ( type );

      return flow ();

    }


    /// Returns an identity flow wrapping the supplied fiber.
    ///
    /// Equivalent to [#flow()] with the fiber already attached — a convenience
    /// for transitioning from a Fiber recipe to a Flow that can be further
    /// composed with type-changing operators (`map`, `flow`) or attached via
    /// [Flow#pipe(Pipe)].
    ///
    /// @param fiber the fiber to wrap
    /// @param <E>   the emission type
    /// @return An identity flow that applies the fiber on every emission
    /// @throws NullPointerException if fiber is `null`
    /// @see Fiber
    /// @see Flow#fiber(Fiber)
    /// @since 2.2

    @New
    @NotNull
    < E > Flow < E, E > flow (
      @NotNull Fiber < E > fiber
    );


    /// Parses the supplied path into an interned name.
    /// The path uses `.` as the separator; empty segments are rejected and cached segments are reused.
    /// Paths must not begin or end with `.` nor contain consecutive separators (for example: `.foo`, `foo.`, `foo..bar`).
    ///
    /// @param path the string to be parsed into one or more name parts
    /// @return A name representing the parsed hierarchy
    /// @throws NullPointerException     if the path is `null`
    /// @throws IllegalArgumentException if the path is empty, begins or ends with `.`, or contains consecutive separators (empty segments)

    @NotNull
    Name name (
      @NotNull String path
    );


    /// Creates a hierarchical name from the enum's declaring class followed by the constant's name.
    /// For example, `TimeUnit.SECONDS` produces a name equivalent to `"java.util.concurrent.TimeUnit.SECONDS"`.
    ///
    /// @param constant the enum constant
    /// @return A hierarchical name representing the fully-qualified enum constant
    /// @throws NullPointerException if the constant is `null`

    @NotNull
    Name name (
      @NotNull Enum < ? > constant
    );


    /// Returns an interned name from iterating over string values.
    /// Each value may contain dot-separated segments; at least one value must be provided.
    ///
    /// This factory variant is constructor-shaped and requires a non-empty
    /// input. For the extender-shaped variant — which returns the receiver
    /// unchanged when the iterable is empty — see [Name#name(Iterable)].
    ///
    /// @param parts the iterable to be iterated over
    /// @return The constructed name
    /// @throws NullPointerException     if the iterable is `null` or one of the values returned is `null`
    /// @throws IllegalArgumentException if the iterable yields no values or an invalid path segment

    @NotNull
    Name name (
      @NotNull Iterable < String > parts
    );


    /// Returns an interned name from iterating over values mapped to strings.
    /// Each mapped value may contain dot-separated segments; at least one value must be provided.
    ///
    /// This factory variant is constructor-shaped and requires a non-empty
    /// input. For the extender-shaped variant — which returns the receiver
    /// unchanged when the iterable is empty — see [Name#name(Iterable, Function)].
    ///
    /// @param <T>    the type of each value iterated over
    /// @param parts  the iterable to be iterated over
    /// @param mapper the function to be used to map the iterable value type to a string
    /// @return The constructed name
    /// @throws NullPointerException     if the iterable, mapper, or one of the mapped values is `null`
    /// @throws IllegalArgumentException if the iterable yields no values or an invalid path segment

    @NotNull
    < T > Name name (
      @NotNull Iterable < ? extends T > parts,
      @NotNull Function < ? super T, String > mapper
    );


    /// Returns an interned name from iterating over string values.
    /// Each value may contain dot-separated segments; at least one value must be provided.
    ///
    /// @param parts the [Iterator] to be iterated over
    /// @return The constructed name
    /// @throws NullPointerException     if the iterator is `null` or one of the values returned is `null`
    /// @throws IllegalArgumentException if the iterator yields no values or an invalid path segment
    /// @see #name(Iterable)

    @NotNull
    Name name (
      @NotNull Iterator < String > parts
    );


    /// Returns an interned name from iterating over values mapped to strings.
    /// Each mapped value may contain dot-separated segments; at least one value must be provided.
    ///
    /// @param <T>    the type of each value iterated over
    /// @param parts  the iterator to be iterated over
    /// @param mapper the function to be used to map the iterator value type to a string
    /// @return The constructed name
    /// @throws NullPointerException     if the iterator, mapper, or one of the mapped values is `null`
    /// @throws IllegalArgumentException if the iterator yields no values or an invalid path segment
    /// @see #name(Iterable, Function)

    @NotNull
    < T > Name name (
      @NotNull Iterator < ? extends T > parts,
      @NotNull Function < ? super T, String > mapper
    );


    /// Creates an interned name from a class.
    /// The package and enclosing class simple names are joined with dots (matching [Class#getCanonicalName()]
    /// when available); local or anonymous classes fall back to the runtime name.
    ///
    /// @param type the class to be mapped to a name
    /// @return A name whose string representation matches `type.getCanonicalName()` when available, otherwise `type.getName()`
    /// @throws NullPointerException if the type is `null`

    @NotNull
    Name name (
      @NotNull Class < ? > type
    );


    /// Creates an interned name from a member.
    /// The declaring class hierarchy is extended with the member name.
    ///
    /// @param member the member to be mapped to a name
    /// @return A name mapped to the member
    /// @throws NullPointerException if the member is `null`

    @NotNull
    Name name (
      @NotNull Member member
    );


    /// Returns a new scope instance with a generated name for managing resources.
    ///
    /// Scopes provide hierarchical resource lifecycle management. When a scope is closed,
    /// all resources registered with it (via [Scope#register(Resource)]) are automatically
    /// closed. Child scopes can be created from parent scopes, forming a tree structure.
    ///
    /// @return A new scope
    /// @see Scope#register(Resource)
    /// @see Scope#close()

    @New
    @NotNull
    Scope scope ();


    /// Returns a new scope instance with the specified name for managing resources.
    ///
    /// Scopes provide hierarchical resource lifecycle management. When a scope is closed,
    /// all resources registered with it (via [Scope#register(Resource)]) are automatically
    /// closed. Child scopes can be created from parent scopes, forming a tree structure.
    ///
    /// @param name the name assigned to the scope's subject
    /// @return A new scope
    /// @throws NullPointerException if the name is `null`
    /// @throws Fault                if the name is not a runtime-provided implementation
    /// @see Scope#register(Resource)
    /// @see Scope#close()

    @New
    @NotNull
    Scope scope (
      @NotNull Name name
    );


    /// Creates a boolean slot.
    ///
    /// @param name  slot name
    /// @param value slot value
    /// @return A boolean slot
    /// @throws NullPointerException if the name is `null`
    /// @throws Fault                if the name is not a runtime-provided implementation

    @New
    @NotNull
    Slot < Boolean > slot (
      @NotNull Name name,
      boolean value
    );


    /// Creates an int slot.
    ///
    /// @param name  slot name
    /// @param value slot value
    /// @return An int slot
    /// @throws NullPointerException if the name is `null`
    /// @throws Fault                if the name is not a runtime-provided implementation

    @New
    @NotNull
    Slot < Integer > slot (
      @NotNull Name name,
      int value
    );


    /// Creates a long slot.
    ///
    /// @param name  slot name
    /// @param value slot value
    /// @return A long slot
    /// @throws NullPointerException if the name is `null`
    /// @throws Fault                if the name is not a runtime-provided implementation

    @New
    @NotNull
    Slot < Long > slot (
      @NotNull Name name,
      long value
    );


    /// Creates a double slot.
    ///
    /// @param name  slot name
    /// @param value slot value
    /// @return A double slot
    /// @throws NullPointerException if the name is `null`
    /// @throws Fault                if the name is not a runtime-provided implementation

    @New
    @NotNull
    Slot < Double > slot (
      @NotNull Name name,
      double value
    );


    /// Creates a float slot.
    ///
    /// @param name  slot name
    /// @param value slot value
    /// @return A float slot
    /// @throws NullPointerException if the name is `null`
    /// @throws Fault                if the name is not a runtime-provided implementation

    @New
    @NotNull
    Slot < Float > slot (
      @NotNull Name name,
      float value
    );


    /// Creates a String slot.
    ///
    /// @param name  slot name
    /// @param value slot value
    /// @return A String slot
    /// @throws NullPointerException if name or value is `null`
    /// @throws Fault                if the name is not a runtime-provided implementation

    @New
    @NotNull
    Slot < String > slot (
      @NotNull Name name,
      @NotNull String value
    );


    /// Creates a Name slot from an enum.
    ///
    /// The slot's name derives from the enum's declaring class, and the value is the enum constant's name.
    ///
    /// @param value the enum to create a slot from
    /// @return A Name slot containing the enum constant's name
    /// @throws NullPointerException if value is `null`

    @New
    @NotNull
    Slot < Name > slot (
      @NotNull Enum < ? > value
    );


    /// Creates a Name slot.
    ///
    /// @param name  slot name
    /// @param value slot value
    /// @return A Name slot
    /// @throws NullPointerException if name or value is `null`
    /// @throws Fault                if the name or value parameters are not runtime-provided implementations

    @New
    @NotNull
    Slot < Name > slot (
      @NotNull Name name,
      @NotNull Name value
    );


    /// Creates a State slot.
    ///
    /// @param name  slot name
    /// @param value slot value
    /// @return A State slot
    /// @throws NullPointerException if name or value is `null`
    /// @throws Fault                if the name or value parameters are not runtime-provided implementations

    @New
    @NotNull
    Slot < State > slot (
      @NotNull Name name,
      @NotNull State value
    );


    /// Creates an empty state.
    ///
    /// @return An empty state.

    @New
    @NotNull
    State state ();


  }


  /// Represents the execution context from which substrate operations originate.
  ///
  /// A [Current] identifies the execution context (thread, coroutine, fiber, etc.)
  /// that invokes substrate operations. It is obtained via [Cortex#current()] in
  /// a manner analogous to `Thread.currentThread()` in Java:
  ///
  /// ```java
  /// var current = cortex.current ();
  /// ```
  ///
  /// ## Temporal Contract
  ///
  /// **IMPORTANT**: Current follows a temporal contract - it is only valid within the
  /// execution context (thread) that obtained it. The Current reference represents the
  /// **thread-local execution state** and **must not be retained** or used from a different
  /// thread or execution context.
  ///
  /// Like `Thread.currentThread()`, Current is intrinsically tied to its originating context.
  /// Using it from another thread leads to incorrect behavior. Always call [Cortex#current()]
  /// from the context where you need the current execution reference.
  ///
  /// Violating this contract by storing Current references and accessing them from different
  /// threads or after the execution context has changed leads to undefined behavior.
  ///
  /// ## Subject Identity
  ///
  /// As a [Substrate], Current provides access to its underlying [Subject] which includes:
  ///
  /// - **Identity** via [Subject#id()] - unique identifier for this execution context
  /// - **Hierarchical naming** via [Subject#name()] - derived from the thread or context type
  /// - **Context properties** via [Subject#state()] - metadata about the execution context
  ///
  /// ## Use Cases
  ///
  /// - **Correlation**: Link emissions and operations back to their originating context
  /// - **Tracing**: Track execution flow across substrate boundaries
  /// - **Diagnostics**: Identify which contexts are invoking substrate operations
  ///
  /// ## Language Mapping
  ///
  /// The abstraction is language-agnostic and maps to platform-specific concepts:
  ///
  /// - **Java**: Platform/virtual threads
  /// - **Go**: Goroutines
  /// - **Kotlin**: Coroutines
  /// - **Other**: Implementation-specific concurrency primitives
  ///
  /// @see Cortex#current()
  /// @see Subject
  /// @since 1.0

  @Tenure ( Tenure.INTERNED )
  @Temporal
  @Provided
  non-sealed interface Current
    extends Substrate < Current > {

  }


  /// Indicates an API that is experimental and subject to change in future releases.
  ///
  /// Experimental APIs are not considered stable and may be modified or removed
  /// without following normal deprecation policies. Use with caution in production code.
  ///
  /// @since 1.0

  @SuppressWarnings ( "unused" )
  @Documented
  @Retention ( SOURCE )
  @Target ( {TYPE, METHOD, CONSTRUCTOR, FIELD} )
  @interface Experimental {
  }

  /// Indicates a type used by callers or instrumentation kits to extend capabilities.

  @SuppressWarnings ( "WeakerAccess" )
  @Documented
  @Retention ( SOURCE )
  @Target ( TYPE )
  @interface Extension {
  }

  /// An abstraction of a hierarchically nested structure of enclosed whole-parts.
  ///
  /// @param <S> the concrete extent type (usually `this`) returned from [#extent()]
  /// @param <P> the enclosing extent type iterated during traversal and comparison

  @Abstract
  @Extension
  interface Extent < S extends Extent < S, P >, P extends Extent < ?, P > >
    extends Iterable < P >,
            Comparable < P > {


    /// {@inheritDoc}
    ///
    /// @throws Fault if the other parameter is not a runtime-provided implementation

    @Override
    default int compareTo (
      @NotNull final P other
    ) {

      requireNonNull ( other );

      // Fast path: identity check
      if ( this == other ) {

        return 0;

      }

      // Compare enclosures first (root to leaf comparison)
      final var thisEnc = enclosure ();
      final var otherEnc = other.enclosure ();

      if ( thisEnc.isPresent () ) {

        if ( otherEnc.isPresent () ) {

          final int cmp
            = thisEnc
            .get ()
            .compareTo (
              otherEnc.get ()
            );

          if ( cmp != 0 ) {

            return cmp;

          }

        } else {

          // this is deeper, comes after
          return 1;

        }

      } else if ( otherEnc.isPresent () ) {

        // other is deeper, this comes before
        return -1;

      }

      // Enclosures are equal, compare this level's parts
      return
        part ()
          .compareTo (
            other.part ()
          );

    }


    /// Returns the depth of this extent within all enclosures.
    ///
    /// @return The depth of this extent within all enclosures.

    default int depth () {

      return
        fold (
          _ -> 1,
          ( depth, _ ) -> depth + 1
        );

    }


    /// Returns the (parent/prefix) extent that encloses this extent.
    ///
    /// @return An optional holding the enclosing extent, or empty if none

    @NotNull
    default Optional < P > enclosure () {

      return Optional.empty ();

    }


    /// Applies the given consumer to the enclosing extent if it exists.
    ///
    /// @param consumer the consumer to be applied to the enclosing extent
    /// @throws NullPointerException if the consumer is `null`

    default void enclosure (
      @NotNull final Consumer < ? super P > consumer
    ) {

      enclosure ()
        .ifPresent ( consumer );

    }


    /// Returns the extent instance referenced by `this`.
    ///
    /// @return A reference to this extent instance

    @NotNull
    @SuppressWarnings ( "unchecked" )
    default S extent () {
      return (S) this;
    }


    /// Returns the outermost (root) extent in the enclosure hierarchy.
    ///
    /// Traverses the enclosure chain from this extent to find the root - the extent
    /// that has no enclosure. For a single-level extent (no enclosure), returns `this`.
    ///
    /// This is the opposite endpoint from `this` in the hierarchy:
    /// - [#iterator()] traverses from `this` toward [#extremity()]
    /// - [#extremity()] returns the final element that iterator would reach
    ///
    /// ## Example
    ///
    /// For a name hierarchy `com.example.service`:
    /// - `service.extremity()` returns the `com` extent
    /// - `com.extremity()` returns `com` (itself, as it has no enclosure)
    ///
    /// ## Performance
    ///
    /// This method traverses the entire enclosure chain, so cost is O(depth).
    /// For deep hierarchies, consider caching the result if called frequently.
    ///
    /// @return The root extent (outermost enclosure) in the hierarchy
    /// @see #enclosure()
    /// @see #iterator()
    /// @see #depth()

    @NotNull
    @SuppressWarnings ( "unchecked" )
    default P extremity () {

      P prev;

      var current
        = (P) extent ();

      do {

        prev = current;

        current
          = current
          .enclosure ()
          .orElse ( null );

      } while ( current != null );

      return prev;

    }


    /// Produces an accumulated value moving from the right (this) to the left (root) in the extent.
    ///
    /// @param <R>         the return type of the accumulated value
    /// @param initializer the function called to initialize the accumulator
    /// @param accumulator the function used to add a value to the accumulator
    /// @return The accumulated result
    /// @throws NullPointerException if either initializer or accumulator is `null`

    default < R > R fold (
      @NotNull final Function < ? super P, ? extends R > initializer,
      @NotNull final BiFunction < ? super R, ? super P, R > accumulator
    ) {

      requireNonNull ( initializer );
      requireNonNull ( accumulator );

      final var it
        = iterator ();

      var result
        = initializer.apply (
        it.next ()
      );

      while ( it.hasNext () ) {

        result
          = accumulator.apply (
          result,
          it.next ()
        );

      }

      return result;

    }


    /// Produces an accumulated value moving from the left (root) to the right (this) in the extent.
    ///
    /// @param <R>         the return type of the accumulated value
    /// @param initializer the function called to seed the accumulator
    /// @param accumulator the function used to add a value to the accumulator
    /// @return The accumulated result
    /// @throws NullPointerException if either initializer or accumulator is `null`

    @SuppressWarnings ( "unchecked" )
    default < R > R foldTo (
      @NotNull final Function < ? super P, ? extends R > initializer,
      @NotNull final BiFunction < ? super R, ? super P, R > accumulator
    ) {

      requireNonNull ( initializer );
      requireNonNull ( accumulator );

      return enclosure ()
        .map (
          ( P enclosure ) ->
            accumulator.apply (
              enclosure.foldTo (
                initializer,
                accumulator
              ),
              (P) extent ()
            )
        ).orElse (
          initializer.apply (
            (P) extent ()
          )
        );

    }


    /// Returns an iterator over the extents moving from the right (`this`) to the left ([#extremity()]).
    ///
    /// @return An iterator for traversing over the nested extents, starting with `this` extent instance.

    @New
    @NotNull
    @Override
    default Iterator < P > iterator () {

      //noinspection ReturnOfInnerClass,unchecked
      return new Iterator <> () {

        private P extent
          = (P) extent ();

        @Override
        public boolean hasNext () {

          return extent != null;

        }

        @Override
        public P next () {

          final var result = extent;

          if ( result == null ) {
            throw new NoSuchElementException ();
          }

          extent
            = result
            .enclosure ()
            .orElse ( null );

          return result;

        }
      };

    }


    /// Returns a `String` representation of just this extent.
    ///
    /// @return A non-`null` `String` representation.

    @NotNull
    String part ();


    /// Returns a `CharSequence` representation of this extent, including enclosing extents.
    /// The default implementation uses '/' as a separator. Extensions of this interface
    /// may override this method to use a different default separator (e.g., [Name] uses '.').
    ///
    /// @return A non-`null` `CharSequence` representation of this extent and its enclosures.
    /// @see #path(char)

    @NotNull
    default CharSequence path () {

      return
        path ( '/' );

    }


    /// Returns a `CharSequence` representation of this extent and its enclosures.
    ///
    /// @param separator the character to use as separator between extent parts
    /// @return A non-`null` `String` representation.

    @NotNull
    default CharSequence path (
      final char separator
    ) {

      return
        path (
          Extent::part,
          separator
        );

    }


    /// Returns a `CharSequence` representation of this extent and its enclosures.
    ///
    /// @param separator the string to use as separator between extent parts
    /// @return A non-`null` `String` representation.
    /// @throws NullPointerException if the separator is `null`

    @NotNull
    default CharSequence path (
      @NotNull final String separator
    ) {

      requireNonNull ( separator );

      return
        path (
          Extent::part,
          separator
        );

    }


    /// Returns a `CharSequence` representation of this extent and its enclosures.
    ///
    /// @param mapper    the function to map extent parts to character sequences
    /// @param separator the character to use as separator between extent parts
    /// @return A non-`null` `String` representation.
    /// @throws NullPointerException if the mapper is `null`

    @NotNull
    default CharSequence path (
      @NotNull final Function < ? super P, ? extends CharSequence > mapper,
      final char separator
    ) {

      requireNonNull ( mapper );

      return foldTo (
        first
          -> new StringBuilder ()
          .append (
            mapper.apply (
              first
            )
          ),
        ( result, next )
          -> result.append ( separator )
          .append (
            mapper.apply (
              next
            )
          )
      );

    }


    /// Returns a `CharSequence` representation of this extent and its enclosures.
    ///
    /// @param mapper    the function to map extent parts to character sequences
    /// @param separator the string to use as separator between extent parts
    /// @return A non-`null` `String` representation.
    /// @throws NullPointerException if either mapper or separator is `null`

    @NotNull
    default CharSequence path (
      @NotNull final Function < ? super P, ? extends CharSequence > mapper,
      @NotNull final String separator
    ) {

      requireNonNull ( mapper );
      requireNonNull ( separator );

      return foldTo (
        first
          -> new StringBuilder ()
          .append (
            mapper.apply (
              first
            )
          ),
        ( result, next )
          -> result.append ( separator )
          .append (
            mapper.apply (
              next
            )
          )
      );

    }


    /// Returns a `Stream` containing each enclosing extent starting with `this`.
    ///
    /// @return A `Stream` in which the first element is `this` extent instance, and the
    /// last is the value returned from the [#extremity()] method.
    /// @see #iterator()
    /// @see #extremity()

    @New
    @NotNull
    default Stream < P > stream () {

      return StreamSupport.stream (
        Spliterators.spliterator (
          iterator (),
          fold (
            _ -> 1,
            ( sum, _ )
              -> sum + 1
          ).longValue (),
          DISTINCT | NONNULL | IMMUTABLE
        ),
        false
      );

    }


    /// Returns true if this `Extent` is directly or indirectly enclosed within the supplied extent.
    ///
    /// @param enclosure the extent to check against, regardless of its generic specialization
    /// @return `true` if the supplied extent encloses this extent, directly or indirectly
    /// @throws NullPointerException if the enclosure is `null`

    default boolean within (
      @NotNull final Extent < ?, ? > enclosure
    ) {

      requireNonNull ( enclosure );

      for (
        var current = enclosure ();
        current.isPresent ();
        current = current.get ().enclosure ()
      ) {
        if ( current.get () == enclosure ) {
          return true;
        }
      }

      return false;

    }

  }


  /// Represents a same-type processing fiber — a chain of per-emission stages
  /// that filter, transform, or observe values of a single type `E` without
  /// changing the type on its way through.
  ///
  /// Fibers carry the type-preserving operators — `guard`, `diff`, `peek`,
  /// `reduce`, `tumble`, `rolling`, and the rest of the per-emission family.
  /// They are standalone, reusable values, independent of `Flow`: any sink that
  /// consumes an `E` can consume a `Fiber<E>` too.
  ///
  /// ## Execution Context
  ///
  /// Every fiber stage executes on the circuit thread that materialized it.
  /// Emissions are processed sequentially; each stage observes values in
  /// deterministic order.
  ///
  /// **Circuit-thread execution guarantee**: All fiber operations execute
  /// exclusively on the circuit thread. This means:
  ///
  /// - Fiber operators execute sequentially for each emission
  /// - No concurrent execution of stages for a given materialization
  /// - State within operators requires no synchronization
  /// - Deterministic ordering of emissions through the fiber
  ///
  /// **User-provided functions**: All predicates, comparators, binary operators,
  /// mappers, and peek receptors passed to Fiber methods execute on the
  /// circuit's processing thread: thread-safe, sequential, and free of
  /// synchronization for local state. Access to external shared mutable state
  /// remains the caller's responsibility.
  ///
  /// ## State Management
  ///
  /// **Stateful operators** maintain internal state across emissions:
  /// `change`, `chance`, `diff`, `dropWhile`, `every`, `guard(initial, BiPredicate)`,
  /// `high`, `low`, `integrate`, `limit`, `pulse`, `reduce`, `relate`, `skip`,
  /// `steady`, `takeWhile`, `tumble`, `rolling`, `delay`, `inhibit`, `hysteresis`,
  /// `edge`.
  ///
  /// **Stateless operators** process each emission independently:
  /// `above`, `below`, `clamp`, `deadband`, `guard(Predicate)`, `max`, `min`,
  /// `peek`, `range`, `replace`.
  ///
  /// Stateful operators store mutable state without external synchronization —
  /// they execute exclusively on the circuit thread. Each materialization of a
  /// fiber carries its own state instance, isolated from other
  /// materializations of the same value.
  ///
  /// ## Composition and Chaining
  ///
  /// Fibers are a **forward** builder: operators chained later run later.
  /// Reading order always equals execution order — no pivots, no inversion:
  ///
  /// ```
  /// fiber.diff().guard(p).peek(o)
  ///   emit(E) → diff → guard → peek → downstream
  /// ```
  ///
  /// Fibers preserve type, so there is no type-boundary concern to reason
  /// about. For type-changing composition (`map`, `flow`) see [Flow].
  ///
  /// **Performance tips**:
  /// - Avoid allocations in operator bodies (hot path)
  /// - Keep fibers short — each stage adds overhead
  /// - Stateful operators (diff, guard-with-state, reduce) are costlier than
  ///   stateless (replace, peek)
  /// - Place cheap filters early to reduce downstream work
  ///
  /// ## Lifecycle
  ///
  /// Fibers are **immutable standalone values**. They are obtained from
  /// [Cortex#fiber()] / [Cortex#fiber(Class)], composed via fluent
  /// chaining, and may be retained, shared, and attached to many sinks
  /// ([Fiber#pipe(Pipe)], or wrapped in a [Flow] via [Cortex#flow(Fiber)]
  /// or [Flow#fiber(Fiber)]). Execution state for stateful operators is
  /// allocated at materialization, not at configuration — a single fiber
  /// value may be materialized many times to produce independent operator
  /// chains, each with its own state.
  ///
  /// **Thread safety**: Fiber values are immutable and safe to share across
  /// threads. Materialized chains, however, run on the circuit thread of the
  /// sink they are attached to.
  ///
  /// **Stateless operator functions**: Client-supplied functions captured by a
  /// Fiber value are shared by every materialization. Do not fold additional
  /// mutable state into those functions — the framework owns any state the
  /// operator logically implies (`reduce` accumulator, `relate` previous input,
  /// `diff` last-emitted value, etc.) on a per-materialization basis.
  ///
  /// ## Exception Handling
  ///
  /// A Fiber operator function — any predicate, comparator, binary operator,
  /// mapper, peek receptor, fire predicate — is external code per SPEC §15.4
  /// (External Callback Isolation). If such a function throws:
  ///
  /// - The exception MUST NOT propagate to the circuit's dispatch loop.
  /// - The emission is considered dropped for the affected receptor chain.
  /// - Other receptors on the same channel (with their own chains) still
  ///   receive the emission.
  /// - Subsequent emissions continue to flow; stateful state is not rolled
  ///   back, but a partial result is not treated as a success.
  /// - How (or whether) the failure is surfaced to observers is
  ///   implementation-defined.
  ///
  /// Supervisory strategies — restart, breaker, fallback, dead-letter — are not
  /// part of this contract.
  ///
  /// @param <E> the emission type
  /// @see Cortex#fiber()
  /// @see Fiber#pipe(Pipe)
  /// @see Flow
  /// @since 2.2

  @Provided
  interface Fiber < E > {

    /// Resolves [Comparator#naturalOrder()] against a pair of probe values so
    /// that the natural-ordering default methods fail fast at chain-build time
    /// — via [ClassCastException] — when `E` is not [Comparable] or the two
    /// probes are of incompatible types. The probe evaluates `a.compareTo(b)`,
    /// exercising cast dispatch on both sides.
    ///
    /// Single-bound callers ([#above], [#below], [#max], [#min]) pass the same
    /// value for both arguments, which reduces to `bound.compareTo(bound)` and
    /// surfaces non-[Comparable] `E` at the call site.
    ///
    /// Two-bound callers ([#clamp], [#deadband], [#range]) pass `lower` and
    /// `upper`, which additionally validates that the two bounds are of
    /// mutually [Comparable] types.
    ///
    /// @param a   the first probe value
    /// @param b   the second probe value
    /// @param <E> the fiber's emission type
    /// @return A comparator backed by [Comparator#naturalOrder()]
    /// @throws NullPointerException if either probe is `null`
    /// @throws ClassCastException   if `E` is not [Comparable] or the probes
    ///                              are of incompatible types
    /// @since 2.3

    @NotNull
    @SuppressWarnings ( {"unchecked", "rawtypes"} )
    private static < E > Comparator < E > naturalOrder (
      @NotNull final E a,
      @NotNull final E b
    ) {

      requireNonNull ( a );
      requireNonNull ( b );

      final Comparator < E > comparator =
        (Comparator) Comparator.naturalOrder ();

      comparator.compare (
        a,
        b
      );

      return
        comparator;

    }


    /// Returns a fiber that passes only values above the specified lower bound (exclusive).
    ///
    /// A value passes if `comparator.compare(value, lower) > 0`. Stateless.
    ///
    /// @param comparator the comparator defining value ordering
    /// @param lower      the exclusive lower bound
    /// @return A fiber that filters values at or below the lower bound
    /// @throws NullPointerException if comparator or lower is `null`
    /// @since 2.2

    @NotNull
    Fiber < E > above (
      @NotNull Comparator < ? super E > comparator,
      @NotNull E lower
    );


    /// Returns a fiber that passes only values above `lower` (exclusive), using
    /// natural ordering.
    ///
    /// Sugar for [#above(Comparator, Object)] with [Comparator#naturalOrder()].
    /// The lower bound is probed against itself at chain-build time, so a
    /// [ClassCastException] is raised here — not at a later emission — if `E`
    /// is not [Comparable].
    ///
    /// @param lower the exclusive lower bound
    /// @return A fiber that filters values at or below the lower bound
    /// @throws NullPointerException if lower is `null`
    /// @throws ClassCastException   if `E` is not [Comparable]
    /// @see #above(Comparator, Object)
    /// @since 2.3

    @NotNull
    default Fiber < E > above (
      @NotNull final E lower
    ) {

      return
        above (
          naturalOrder ( lower, lower ),
          lower
        );

    }


    /// Returns a fiber that passes only values below the specified upper bound (exclusive).
    ///
    /// A value passes if `comparator.compare(value, upper) < 0`. Stateless.
    ///
    /// @param comparator the comparator defining value ordering
    /// @param upper      the exclusive upper bound
    /// @return A fiber that filters values at or above the upper bound
    /// @throws NullPointerException if comparator or upper is `null`
    /// @since 2.2

    @NotNull
    Fiber < E > below (
      @NotNull Comparator < ? super E > comparator,
      @NotNull E upper
    );


    /// Returns a fiber that passes only values below `upper` (exclusive), using
    /// natural ordering.
    ///
    /// Sugar for [#below(Comparator, Object)] with [Comparator#naturalOrder()].
    /// The upper bound is probed against itself at chain-build time, so a
    /// [ClassCastException] is raised here — not at a later emission — if `E`
    /// is not [Comparable].
    ///
    /// @param upper the exclusive upper bound
    /// @return A fiber that filters values at or above the upper bound
    /// @throws NullPointerException if upper is `null`
    /// @throws ClassCastException   if `E` is not [Comparable]
    /// @see #below(Comparator, Object)
    /// @since 2.3

    @NotNull
    default Fiber < E > below (
      @NotNull final E upper
    ) {

      return
        below (
          naturalOrder ( upper, upper ),
          upper
        );

    }


    /// Returns a fiber that probabilistically passes emissions. Each emission
    /// passes with the supplied probability. Stateful.
    ///
    /// @param probability the pass probability (in `[0.0, 1.0]`)
    /// @return A stateful fiber with probabilistic sampling
    /// @throws IllegalArgumentException if probability is outside `[0.0, 1.0]`
    /// @since 2.3

    @NotNull
    Fiber < E > chance (
      double probability
    );


    /// Returns a fiber that emits only when a derived key differs from the
    /// previous emission's key. Key-based distinct: projection-aware variant of
    /// [#diff()]. Stateful — tracks the previously extracted key.
    ///
    /// @param key the function used to extract the comparison key
    /// @return A stateful fiber that filters out emissions with unchanged keys
    /// @throws NullPointerException if the key function is `null`
    /// @see #diff()
    /// @since 2.2

    @NotNull
    Fiber < E > change (
      @NotNull Function < ? super E, ? > key
    );


    /// Returns a fiber that clamps values to `[lower, upper]`.
    /// Below-lower → lower; above-upper → upper; in-range → unchanged. Stateless.
    ///
    /// @param comparator the comparator defining value ordering
    /// @param lower      the inclusive lower bound
    /// @param upper      the inclusive upper bound
    /// @return A fiber that clamps values to the specified range
    /// @throws NullPointerException     if any argument is `null`
    /// @throws IllegalArgumentException if `comparator.compare(lower, upper) > 0`
    /// @since 2.2

    @NotNull
    Fiber < E > clamp (
      @NotNull Comparator < ? super E > comparator,
      @NotNull E lower,
      @NotNull E upper
    );


    /// Returns a fiber that clamps values to `[lower, upper]`, using natural ordering.
    /// Below-lower → lower; above-upper → upper; in-range → unchanged.
    ///
    /// Sugar for [#clamp(Comparator, Object, Object)] with [Comparator#naturalOrder()].
    /// Both bounds are probed against each other at chain-build time, so a
    /// [ClassCastException] is raised here — not at a later emission — if `E`
    /// is not [Comparable] or the bounds are of incompatible types.
    ///
    /// @param lower the inclusive lower bound
    /// @param upper the inclusive upper bound
    /// @return A fiber that clamps values to the specified range
    /// @throws NullPointerException     if any argument is `null`
    /// @throws ClassCastException       if `E` is not [Comparable] or the
    ///                                  bounds are of incompatible types
    /// @throws IllegalArgumentException if `lower` is greater than `upper`
    /// @see #clamp(Comparator, Object, Object)
    /// @since 2.3

    @NotNull
    default Fiber < E > clamp (
      @NotNull final E lower,
      @NotNull final E upper
    ) {

      return
        clamp (
          naturalOrder ( lower, upper ),
          lower,
          upper
        );

    }


    /// Returns a fiber that suppresses values within a dead zone and passes values outside it.
    /// A value passes if below `lower` or above `upper`; in-band values (inclusive edges) drop.
    /// Stateless.
    ///
    /// @param comparator the comparator defining value ordering
    /// @param lower      the inclusive lower edge of the suppressed band
    /// @param upper      the inclusive upper edge of the suppressed band
    /// @return A fiber that drops in-band values and passes out-of-band
    /// @throws NullPointerException     if any argument is `null`
    /// @throws IllegalArgumentException if `comparator.compare(lower, upper) > 0`
    /// @since 2.2

    @NotNull
    Fiber < E > deadband (
      @NotNull Comparator < ? super E > comparator,
      @NotNull E lower,
      @NotNull E upper
    );


    /// Returns a fiber that suppresses values within a dead zone and passes values outside it,
    /// using natural ordering. A value passes if below `lower` or above `upper`; in-band
    /// values (inclusive edges) drop.
    ///
    /// Sugar for [#deadband(Comparator, Object, Object)] with [Comparator#naturalOrder()].
    /// Both bounds are probed against each other at chain-build time, so a
    /// [ClassCastException] is raised here — not at a later emission — if `E`
    /// is not [Comparable] or the bounds are of incompatible types.
    ///
    /// @param lower the inclusive lower edge of the suppressed band
    /// @param upper the inclusive upper edge of the suppressed band
    /// @return A fiber that drops in-band values and passes out-of-band
    /// @throws NullPointerException     if any argument is `null`
    /// @throws ClassCastException       if `E` is not [Comparable] or the
    ///                                  bounds are of incompatible types
    /// @throws IllegalArgumentException if `lower` is greater than `upper`
    /// @see #deadband(Comparator, Object, Object)
    /// @since 2.3

    @NotNull
    default Fiber < E > deadband (
      @NotNull final E lower,
      @NotNull final E upper
    ) {

      return
        deadband (
          naturalOrder ( lower, upper ),
          lower,
          upper
        );

    }


    /// Returns a fiber that emits the value received `depth` emissions earlier.
    /// A ring-buffer lookback primitive. The first `depth` emissions emit `initial`.
    ///
    /// @param depth   the lookback depth (must be positive)
    /// @param initial the value emitted before `depth` emissions have accumulated
    /// @return A stateful fiber with temporal displacement
    /// @throws IllegalArgumentException if depth is not positive
    /// @throws NullPointerException     if initial is `null`
    /// @since 2.2

    @NotNull
    Fiber < E > delay (
      int depth,
      @NotNull E initial
    );


    /// Returns a fiber that drops consecutive duplicate emissions.
    /// Compares each emission against the previously emitted value using
    /// [Objects#equals]. First emission always passes. Stateful.
    ///
    /// @return A stateful fiber that filters out consecutive duplicates
    /// @since 2.2

    @NotNull
    Fiber < E > diff ();


    /// Returns a fiber that drops consecutive duplicates, seeded by `initial`.
    /// The first emission is compared against `initial`; if equal, it is dropped.
    ///
    /// @param initial the seed value against which the first emission is compared
    /// @return A stateful fiber with seeded dedup
    /// @throws NullPointerException if initial is `null`
    /// @since 2.2

    @NotNull
    Fiber < E > diff (
      @NotNull E initial
    );


    /// Returns a fiber that drops leading emissions as long as `predicate` holds,
    /// then passes all subsequent emissions. Stateful latch.
    ///
    /// @param predicate the predicate controlling the drop window
    /// @return A stateful fiber with an initial drop window
    /// @throws NullPointerException if predicate is `null`
    /// @since 2.2

    @NotNull
    Fiber < E > dropWhile (
      @NotNull Predicate < ? super E > predicate
    );


    /// Returns a fiber that emits on a transition between consecutive values.
    /// The `transition` bi-predicate decides whether `(prev, curr)` is an edge;
    /// `initial` seeds `prev` for the first emission. Stateful.
    ///
    /// @param initial    the seed value for the previous emission
    /// @param transition the bi-predicate deciding edge-ness
    /// @return A stateful fiber with edge detection
    /// @throws NullPointerException if initial or transition is `null`
    /// @since 2.2

    @NotNull
    Fiber < E > edge (
      @NotNull E initial,
      @NotNull BiPredicate < ? super E, ? super E > transition
    );


    /// Returns a fiber that emits every Nth value and drops the rest (interval sampling). Stateful.
    ///
    /// @param interval the sample interval (positive)
    /// @return A stateful fiber with interval sampling
    /// @throws IllegalArgumentException if interval is not positive
    /// @since 2.3

    @NotNull
    Fiber < E > every (
      int interval
    );


    /// Returns a fiber that applies `this` first and then forwards each surviving
    /// emission through `next`. First-class composition of two same-type recipes
    /// into a single reusable recipe.
    ///
    /// ```
    /// emit(E) → this (E → E) → next (E → E) → downstream
    /// ```
    ///
    /// Both fibers remain independent values; materialization allocates fresh
    /// execution state for the stateful operators of each at attachment time
    /// ([Fiber#pipe(Pipe)]). Composing the same fiber more than once, or with
    /// itself, is safe — each attachment gets its own state instance.
    ///
    /// @param next the fiber that runs after this one
    /// @return A fiber equivalent to `this` followed by `next`
    /// @throws NullPointerException if `next` is `null`
    /// @see Fiber#pipe(Pipe)
    /// @see Flow#flow(Flow)
    /// @since 2.3

    @NotNull
    Fiber < E > fiber (
      @NotNull Fiber < E > next
    );


    /// Returns a fiber that conditionally passes emissions based on a predicate.
    /// Stateless — each emission is evaluated independently.
    ///
    /// @param predicate the predicate that determines whether to pass each emission
    /// @return A fiber that filters based on the predicate
    /// @throws NullPointerException if predicate is `null`
    /// @since 2.2

    @NotNull
    Fiber < E > guard (
      @NotNull Predicate < ? super E > predicate
    );


    /// Returns a fiber that passes only when the `(prev, curr)` bi-predicate holds.
    /// `initial` seeds `prev`; on pass, `prev` advances to `curr`. Stateful.
    ///
    /// @param initial   the seed for the previous value
    /// @param predicate the bi-predicate controlling pass-through
    /// @return A stateful fiber with pairwise filtering
    /// @throws NullPointerException if initial or predicate is `null`
    /// @since 2.2

    @NotNull
    Fiber < E > guard (
      @NotNull E initial,
      @NotNull BiPredicate < ? super E, ? super E > predicate
    );


    /// Returns a fiber that passes only values that are new running highs.
    /// First emission always passes; subsequent emissions pass only if strictly
    /// greater than the prior high. Stateful.
    ///
    /// @param comparator the comparator defining value ordering
    /// @return A stateful fiber that filters to new highs
    /// @throws NullPointerException if comparator is `null`
    /// @since 2.2

    @NotNull
    Fiber < E > high (
      @NotNull Comparator < ? super E > comparator
    );


    /// Returns a fiber that latches on above an enter threshold and latches off
    /// below an exit threshold. Emits only within the latched-on regime. Stateful.
    ///
    /// @param enter the predicate that opens the gate
    /// @param exit  the predicate that closes the gate
    /// @return A stateful fiber with two-threshold hysteresis
    /// @throws NullPointerException if either predicate is `null`
    /// @since 2.2

    @NotNull
    Fiber < E > hysteresis (
      @NotNull Predicate < ? super E > enter,
      @NotNull Predicate < ? super E > exit
    );


    /// Returns a fiber that passes the first emission then suppresses the next
    /// `refractory` emissions, then re-arms. Stateful refractory filter.
    ///
    /// @param refractory the number of emissions to suppress after a pass (non-negative)
    /// @return A stateful fiber with refractory suppression
    /// @throws IllegalArgumentException if refractory is negative
    /// @since 2.2

    @NotNull
    Fiber < E > inhibit (
      int refractory
    );


    /// Returns a fiber that accumulates values and fires (emits + resets) when
    /// the `fire` predicate holds on the accumulator. Stateful.
    ///
    /// `initial` may be `null`: it is treated as the fold's starting state and
    /// passed through to `accumulator` on the first emission. Callers using a
    /// nullable accumulator must ensure `accumulator` and `fire` handle `null`
    /// input without throwing.
    ///
    /// @param initial     the starting and reset value of the accumulator (may be `null`)
    /// @param accumulator the binary operation `(state, input) -> new state`
    /// @param fire        the predicate deciding when to emit and reset
    /// @return A stateful fiber with accumulate-and-fire semantics
    /// @throws NullPointerException if accumulator or fire is `null`
    /// @since 2.2

    @NotNull
    Fiber < E > integrate (
      E initial,
      @NotNull BinaryOperator < E > accumulator,
      @NotNull Predicate < ? super E > fire
    );


    /// Returns a fiber that passes at most N emissions then blocks all subsequent values.
    /// Stateful.
    ///
    /// @param limit the maximum number of emissions to pass through (non-negative)
    /// @return A stateful fiber that enforces an emission cap
    /// @throws IllegalArgumentException if limit is negative
    /// @since 2.2

    @NotNull
    Fiber < E > limit (
      long limit
    );


    /// Returns a fiber that passes only values that are new running lows.
    /// First emission always passes; subsequent emissions pass only if strictly
    /// less than the prior low. Stateful.
    ///
    /// @param comparator the comparator defining value ordering
    /// @return A stateful fiber that filters to new lows
    /// @throws NullPointerException if comparator is `null`
    /// @since 2.2

    @NotNull
    Fiber < E > low (
      @NotNull Comparator < ? super E > comparator
    );


    /// Returns a fiber that passes only values at or below `max` (inclusive). Stateless.
    ///
    /// @param comparator the comparator defining value ordering
    /// @param max        the inclusive upper bound
    /// @return A fiber with an inclusive upper bound
    /// @throws NullPointerException if any argument is `null`
    /// @since 2.2

    @NotNull
    Fiber < E > max (
      @NotNull Comparator < ? super E > comparator,
      @NotNull E max
    );


    /// Returns a fiber that passes only values at or below `max` (inclusive), using
    /// natural ordering.
    ///
    /// Sugar for [#max(Comparator, Object)] with [Comparator#naturalOrder()].
    /// The bound is probed against itself at chain-build time, so a
    /// [ClassCastException] is raised here — not at a later emission — if `E`
    /// is not [Comparable].
    ///
    /// @param max the inclusive upper bound
    /// @return A fiber with an inclusive upper bound
    /// @throws NullPointerException if max is `null`
    /// @throws ClassCastException   if `E` is not [Comparable]
    /// @see #max(Comparator, Object)
    /// @since 2.3

    @NotNull
    default Fiber < E > max (
      @NotNull final E max
    ) {

      return
        max (
          naturalOrder ( max, max ),
          max
        );

    }


    /// Returns a fiber that passes only values at or above `min` (inclusive). Stateless.
    ///
    /// @param comparator the comparator defining value ordering
    /// @param min        the inclusive lower bound
    /// @return A fiber with an inclusive lower bound
    /// @throws NullPointerException if any argument is `null`
    /// @since 2.2

    @NotNull
    Fiber < E > min (
      @NotNull Comparator < ? super E > comparator,
      @NotNull E min
    );


    /// Returns a fiber that passes only values at or above `min` (inclusive), using
    /// natural ordering.
    ///
    /// Sugar for [#min(Comparator, Object)] with [Comparator#naturalOrder()].
    /// The bound is probed against itself at chain-build time, so a
    /// [ClassCastException] is raised here — not at a later emission — if `E`
    /// is not [Comparable].
    ///
    /// @param min the inclusive lower bound
    /// @return A fiber with an inclusive lower bound
    /// @throws NullPointerException if min is `null`
    /// @throws ClassCastException   if `E` is not [Comparable]
    /// @see #min(Comparator, Object)
    /// @since 2.3

    @NotNull
    default Fiber < E > min (
      @NotNull final E min
    ) {

      return
        min (
          naturalOrder ( min, min ),
          min
        );

    }


    /// Returns a fiber that forwards each emission through a side-effect receptor.
    /// The receptor is invoked with each emission, then the value is passed to
    /// downstream. Stateless.
    ///
    /// @param receptor the receptor invoked for each emission
    /// @return A fiber with the peek observer added
    /// @throws NullPointerException if receptor is `null`
    /// @since 2.2

    @NotNull
    Fiber < E > peek (
      @NotNull Receptor < ? super E > receptor
    );


    /// Returns a pipe that applies this fiber's operator chain ahead of the
    /// supplied pipe on that pipe's circuit.
    ///
    /// This is the primary attachment point for a fiber. Since a fiber
    /// preserves the emission type `E`, the returned pipe carries the
    /// same `E` — no widening through [Flow] is required.
    ///
    /// ## Usage
    ///
    /// ```java
    /// final var fiber = cortex.fiber ( Integer.class )
    ///   .guard ( v -> v > 0 )
    ///   .diff ();
    /// final Pipe < Integer > filtered = fiber.pipe ( sink );
    /// ```
    ///
    /// The fiber materializes on `pipe`'s circuit. For cross-circuit
    /// parallelism, first hop circuits with [Circuit#pipe(Pipe)], then
    /// attach the fiber:
    ///
    /// ```java
    /// // terminal receptor on circuit-0
    /// Pipe < E > sink = circuit0.pipe ( receptor );
    /// // fiber1 runs on circuit-1, hands off to circuit-0
    /// Pipe < E > stage1 = fiber1.pipe ( circuit1.pipe ( sink ) );
    /// // fiber2 runs on circuit-2, hands off to circuit-1, then circuit-0
    /// Pipe < E > input  = fiber2.pipe ( circuit2.pipe ( stage1 ) );
    /// ```
    ///
    /// Each circuit is single-threaded; stages on different circuits run
    /// concurrently, but the terminal receptor always observes a
    /// single-threaded view.
    ///
    /// @param pipe the pipe to prepend this fiber to
    /// @return A pipe that applies this fiber before forwarding to `pipe`
    /// @throws NullPointerException if pipe is `null`
    /// @throws Fault                if the pipe is not a runtime-provided implementation
    /// @see Flow#pipe(Pipe)
    /// @see Circuit#pipe(Pipe)
    /// @since 2.2

    @New
    @NotNull
    Pipe < E > pipe (
      @NotNull Pipe < E > pipe
    );


    /// Returns a fiber that emits only on the rising edge of a predicate —
    /// the first emission where the predicate transitions from false to true.
    /// Stateful.
    ///
    /// @param predicate the predicate tracked for rising edges
    /// @return A stateful fiber with rising-edge detection
    /// @throws NullPointerException if predicate is `null`
    /// @since 2.2

    @NotNull
    Fiber < E > pulse (
      @NotNull Predicate < ? super E > predicate
    );


    /// Returns a fiber that passes only values within `[lower, upper]` (inclusive). Stateless.
    ///
    /// @param comparator the comparator defining value ordering
    /// @param lower      the inclusive lower bound
    /// @param upper      the inclusive upper bound
    /// @return A fiber that filters to values within the range
    /// @throws NullPointerException     if any argument is `null`
    /// @throws IllegalArgumentException if `comparator.compare(lower, upper) > 0`
    /// @since 2.2

    @NotNull
    Fiber < E > range (
      @NotNull Comparator < ? super E > comparator,
      @NotNull E lower,
      @NotNull E upper
    );


    /// Returns a fiber that passes only values within `[lower, upper]` (inclusive),
    /// using natural ordering.
    ///
    /// Sugar for [#range(Comparator, Object, Object)] with [Comparator#naturalOrder()].
    /// Both bounds are probed against each other at chain-build time, so a
    /// [ClassCastException] is raised here — not at a later emission — if `E`
    /// is not [Comparable] or the bounds are of incompatible types.
    ///
    /// @param lower the inclusive lower bound
    /// @param upper the inclusive upper bound
    /// @return A fiber that filters to values within the range
    /// @throws NullPointerException     if any argument is `null`
    /// @throws ClassCastException       if `E` is not [Comparable] or the
    ///                                  bounds are of incompatible types
    /// @throws IllegalArgumentException if `lower` is greater than `upper`
    /// @see #range(Comparator, Object, Object)
    /// @since 2.3

    @NotNull
    default Fiber < E > range (
      @NotNull final E lower,
      @NotNull final E upper
    ) {

      return
        range (
          naturalOrder ( lower, upper ),
          lower,
          upper
        );

    }


    /// Returns a fiber that folds each emission into a running accumulator and
    /// emits the updated accumulator. Stateful.
    ///
    /// `initial` may be `null`: it is passed through to `op` on the first
    /// emission. Callers using a nullable accumulator must ensure `op`
    /// handles `null` input without throwing.
    ///
    /// @param initial the starting accumulator value (may be `null`)
    /// @param op      the binary operation `(accumulator, input) -> new accumulator`
    /// @return A stateful fiber with running fold
    /// @throws NullPointerException if op is `null`
    /// @since 2.2

    @NotNull
    Fiber < E > reduce (
      E initial,
      @NotNull BinaryOperator < E > op
    );


    /// Returns a fiber that emits `op(prev_input, curr_input)`. `initial` seeds
    /// `prev`. The state tracks the previous input, not the output. Stateful.
    ///
    /// `initial` may be `null`: it is passed to `op` as `prev` on the first
    /// emission. Callers seeding with `null` must ensure `op` handles a
    /// `null` previous input without throwing.
    ///
    /// @param initial the seed for the previous input (may be `null`)
    /// @param op      the binary operation over `(prev_input, input)`
    /// @return A stateful fiber with lookback relation
    /// @throws NullPointerException if op is `null`
    /// @since 2.2

    @NotNull
    Fiber < E > relate (
      E initial,
      @NotNull BinaryOperator < E > op
    );


    /// Returns a fiber that transforms each value via a unary operator. Stateless.
    /// Null returns filter the emission.
    ///
    /// @param op the unary operator
    /// @return A fiber that replaces each value by applying `op`
    /// @throws NullPointerException if op is `null`
    /// @since 2.2

    @NotNull
    Fiber < E > replace (
      @NotNull UnaryOperator < E > op
    );


    /// Returns a fiber that folds each emission into a sliding window of fixed
    /// size, emitting the windowed aggregate on each tick once warmed up. Stateful.
    ///
    /// @param size     the sliding-window size (positive)
    /// @param combiner the binary operation `(accumulator, input) -> new accumulator`
    /// @param identity the fold identity (seed for each window reconstruction)
    /// @return A stateful fiber with rolling aggregation
    /// @throws IllegalArgumentException if size is not positive
    /// @throws NullPointerException     if combiner or identity is `null`
    /// @since 2.2

    @NotNull
    Fiber < E > rolling (
      int size,
      @NotNull BinaryOperator < E > combiner,
      @NotNull E identity
    );


    /// Returns a fiber that drops the first N emissions and passes all subsequent values. Stateful.
    ///
    /// @param count the number of initial emissions to skip (non-negative)
    /// @return A stateful fiber that skips initial emissions
    /// @throws IllegalArgumentException if count is negative
    /// @since 2.2

    @NotNull
    Fiber < E > skip (
      long count
    );


    /// Returns a fiber that emits the Nth confirmation of a stable value using
    /// [Objects#equals] for equality; after firing, suppresses the rest of the run. Stateful.
    ///
    /// @param count the confirmation count (positive)
    /// @return A stateful fiber with temporal confirmation
    /// @throws IllegalArgumentException if count is not positive
    /// @since 2.2

    @NotNull
    Fiber < E > steady (
      int count
    );


    /// Returns a fiber that emits the Nth confirmation of a stable value using
    /// a custom equality bi-predicate; after firing, suppresses the rest of the run. Stateful.
    ///
    /// @param count     the confirmation count (positive)
    /// @param predicate the equality bi-predicate
    /// @return A stateful fiber with custom-equality confirmation
    /// @throws IllegalArgumentException if count is not positive
    /// @throws NullPointerException     if predicate is `null`
    /// @since 2.2

    @NotNull
    Fiber < E > steady (
      int count,
      @NotNull BiPredicate < ? super E, ? super E > predicate
    );


    /// Returns a fiber that passes emissions as long as `predicate` holds and
    /// drops all subsequent emissions once the predicate fails once. Stateful.
    ///
    /// @param predicate the pass-through predicate
    /// @return A stateful fiber with an early-termination window
    /// @throws NullPointerException if predicate is `null`
    /// @since 2.2

    @NotNull
    Fiber < E > takeWhile (
      @NotNull Predicate < ? super E > predicate
    );


    /// Returns a fiber that aggregates fixed-size batches of emissions into
    /// single outputs. After `size` emissions the accumulator is emitted and reset. Stateful.
    ///
    /// @param size     the batch size (positive)
    /// @param combiner the binary operation `(accumulator, input) -> new accumulator`
    /// @param identity the fold identity (seed for each batch)
    /// @return A stateful fiber with tumbling aggregation
    /// @throws IllegalArgumentException if size is not positive
    /// @throws NullPointerException     if combiner or identity is `null`
    /// @since 2.2

    @NotNull
    Fiber < E > tumble (
      int size,
      @NotNull BinaryOperator < E > combiner,
      @NotNull E identity
    );

  }


  /// Left-to-right composition surface (optionally type-changing) for assembling pipeline segments.
  ///
  /// `Flow<I,O>` is the narrow vocabulary for operators that cross type
  /// boundaries. Per-emission, same-type operators (diff, guard, peek, limit,
  /// every, chance, reduce, ...) live on [Fiber] and can be attached to a Flow at the
  /// output side via [#fiber(Fiber)].
  ///
  /// ## Operator Vocabulary
  ///
  /// - [#map(Function)] — append a type-changing transformation (`O → P`)
  /// - [#fiber(Fiber)] — append a same-type [Fiber] at the output side
  /// - [#flow(Flow)] — compose with another Flow (optionally type-changing)
  ///
  /// ## Execution Context
  ///
  /// Every stage executes on the circuit thread that owns the receiving pipe. Emissions
  /// are processed sequentially, so each stage observes values in deterministic order.
  ///
  /// **User-provided functions** (the mapper passed to [#map(Function)], or functions
  /// captured by an attached Fiber) execute on the circuit's processing thread:
  ///
  /// - Thread-safe execution — no concurrent invocation
  /// - Sequential processing — one emission at a time
  ///
  /// Being single-threaded at the *per-materialization* call site does not
  /// make captured functions safe to hold mutable state: see **Stateless
  /// functions** under [Lifecycle](#Lifecycle) below. The single-threaded
  /// guarantee covers framework-managed per-materialization state (a Fiber's
  /// `reduce` accumulator, `diff`'s last-emitted value, etc.), which is
  /// allocated per attachment and therefore never crosses materialization
  /// boundaries.
  ///
  /// **CRITICAL — External state access**: While your functions execute thread-safely on
  /// the circuit thread, you must ensure thread-safe access to any external shared
  /// mutable state they reference.
  ///
  /// ## Composition
  ///
  /// Flow is a **forward builder** for type-changing composition. [#map(Function)]
  /// appends a transformation at the output side: its signature
  /// `Function<? super O, ? extends P>` maps the existing output type `O` into a
  /// new downstream type `P`, producing a `Flow<I, P>`.
  ///
  /// The textual chain order matches execution order:
  ///
  /// ```
  /// flow.map(o -> fn(o))
  ///   emit(I) → ... → O → fn → P → downstream
  /// ```
  ///
  /// When a Flow carries an attached [Fiber], the fiber's operators run **last** on the
  /// outgoing O-valued emission, after any `map` transformation:
  ///
  /// ```
  /// cortex.flow(I.class).map(i -> toO(i)).fiber(fiber<O>)
  ///   emit(I) → toO → fiber(O → O) → downstream
  /// ```
  ///
  /// ## Lifecycle
  ///
  /// Flow instances are **standalone immutable values**. Each operator returns a new
  /// Flow representing the extended pipeline; parents are not mutated. Values obtained
  /// from [Cortex#flow()] (or equivalent factories) may be retained, shared across
  /// threads, and attached to multiple [Flow#pipe(Pipe)] call sites.
  ///
  /// **Materialization**: A Flow is materialized into an actual receptor chain when
  /// attached to a pipe. Any per-materialization state introduced by an attached Fiber
  /// is allocated at materialization time, so a single Flow value can be materialized
  /// repeatedly to produce independent chains each with their own state.
  ///
  /// **Stateless functions**: Client-supplied functions passed to Flow (the `map`
  /// mapper, any function inside an attached Fiber) are captured by the Flow/Fiber value
  /// and shared by every materialization — the same function instance is invoked
  /// from every attached pipeline. They MUST therefore be stateless (pure
  /// functions of their arguments): capturing mutable state in a lambda or
  /// method reference is forbidden, even though each individual materialization
  /// executes single-threaded, because the captured state would be aliased
  /// across all materializations. Per-emission state belongs to the framework (a
  /// Fiber's `reduce` accumulator, `diff`'s last-emitted value, etc.) and is held
  /// per-materialization, so it is never aliased.
  ///
  /// ## Exception Handling
  ///
  /// A client-supplied function — the `map` mapper, or any predicate / mapper /
  /// receptor inside an attached Fiber — is external code as defined by SPEC §15.4
  /// (External Callback Isolation). If such a function throws an uncaught exception
  /// while the circuit is processing an emission:
  ///
  /// - The exception MUST NOT propagate to the circuit's dispatch loop.
  /// - The emission is considered dropped for the affected receptor chain.
  /// - Other receptors registered on the same channel still receive the emission.
  /// - Subsequent emissions continue to flow through the pipeline.
  /// - How (or whether) the failure is surfaced to observers is implementation-defined.
  ///
  /// @param <I> the input type of emitted values
  /// @param <O> the output type of emitted values
  /// @see Fiber
  /// @see Flow#pipe(Pipe)
  /// @since 2.0

  @Provided
  interface Flow < I, O > {

    /// Returns a flow that attaches `fiber` at the output side of this flow.
    ///
    /// This flow runs first (converting `I` to `O`); the surviving `O` value is
    /// then fed into the fiber, whose surviving output is forwarded to downstream.
    /// A same-type fiber is a per-emission recipe; attaching it to a Flow lets
    /// the fiber decorate the flow's output without going through Flow's own
    /// operator vocabulary.
    ///
    /// ```
    /// emit(I) → this flow (I → O) → fiber (O → O) → downstream
    /// ```
    ///
    /// Use case: apply a reusable fiber recipe (dedupe, guard, rate-limit,
    /// etc.) as a suffix to a transformation flow.
    ///
    /// @param fiber the fiber that runs at the output side
    /// @return A flow whose pipeline is this flow followed by `fiber`
    /// @throws NullPointerException if the specified fiber is `null`
    /// @see Fiber
    /// @see Cortex#flow(Fiber)
    /// @since 2.3

    @NotNull
    Flow < I, O > fiber (
      @NotNull Fiber < O > fiber
    );


    /// Returns a flow that applies `this` first and then forwards each surviving
    /// emission through `next`. First-class composition of two flow recipes into
    /// a single reusable recipe, bridging across the output type of `this` and
    /// the input type of `next`.
    ///
    /// ```
    /// emit(I) → this (I → O) → next (O → P) → downstream
    /// ```
    ///
    /// Both flows remain independent values; materialization allocates fresh
    /// execution state for any stateful operators (e.g., inside an attached
    /// [Fiber]) of each at attachment time ([Flow#pipe(Pipe)]). Composing the
    /// same flow more than once is safe — each attachment gets its own state
    /// instance.
    ///
    /// @param next the flow that runs after this one
    /// @param <P>  the output type produced by `next`
    /// @return A flow equivalent to `this` followed by `next`
    /// @throws NullPointerException if `next` is `null`
    /// @see Flow#pipe(Pipe)
    /// @see Fiber#fiber(Fiber)
    /// @since 2.3

    @NotNull
    < P > Flow < I, P > flow (
      @NotNull Flow < ? super O, ? extends P > next
    );


    /// Returns a flow that appends a type transformation to the pipeline.
    ///
    /// The mapper converts outgoing values from type `O` to type `P`, at the output
    /// side of this flow. The returned `Flow<I, P>` still accepts `I`-typed emissions
    /// at the input side; each emission flows through the existing pipeline to produce
    /// `O`, which is then mapped to `P` for the downstream receptor.
    ///
    /// ## Data Flow
    ///
    /// ```
    /// emit(I) → ... → O → fn(O) → P → downstream
    /// ```
    ///
    /// ## Null Filtering
    ///
    /// If the function returns `null`, the emission is **filtered out** (dropped)
    /// rather than forwarded to the downstream receptor. This allows the function
    /// to serve as a combined transform-and-filter operation.
    ///
    /// ## Same-Type Transformation
    ///
    /// For same-type transforms (`O == P`), `map` also serves as a value replacement
    /// operator. Null returns filter the emission:
    ///
    /// ```java
    /// flow.map(v -> v * 2)              // double each value
    /// flow.map(v -> v > 0 ? v : null)   // transform + filter
    /// ```
    ///
    /// ## Combining with Fiber
    ///
    /// Per-emission operators (diff, guard, peek, limit, ...) live on [Fiber]
    /// and are attached at the output side via [#fiber(Fiber)]:
    ///
    /// ```java
    /// // Map I → O, and dedupe on the O side (post-map) via an attached Fiber:
    /// cortex.flow(I.class)
    ///   .map(i -> toO(i))
    ///   .fiber(cortex.fiber(O.class).diff())
    /// ```
    ///
    /// @param fn  the function to transform emissions from O to P; returns `null` to filter
    /// @param <P> the output type produced by the transformation
    /// @return A flow with output type `P` that can be chained further
    /// @throws NullPointerException if fn is `null`
    /// @see Flow#pipe(Pipe)
    /// @since 2.0

    @NotNull
    < P > Flow < I, P > map (
      @NotNull Function < ? super O, ? extends P > fn
    );


    /// Returns a pipe that applies this flow's pipeline ahead of the supplied pipe
    /// on that pipe's circuit.
    ///
    /// This is the cross-type attachment point for Flow. The flow's output type `O`
    /// must match the supplied pipe's emission type; the returned pipe carries the
    /// flow's input type `I`.
    ///
    /// ## Usage Patterns
    ///
    /// ```java
    /// // Type transformation:
    /// cortex.flow ( Integer.class ).map ( i -> "value:" + i ).pipe ( sink )
    ///
    /// // Flow with an attached per-emission Fiber recipe:
    /// cortex.flow ( Integer.class )
    ///   .map ( i -> "value:" + i )
    ///   .fiber ( cortex.fiber ( String.class ).guard ( v -> !v.isEmpty () ).diff () )
    ///   .pipe ( sink )
    /// ```
    ///
    /// ## Data Flow
    ///
    /// `map` in a Flow **appends** a downstream transformation. Reading from the
    /// emit side toward the downstream pipe's receptor:
    ///
    /// ```
    /// Caller thread:       Circuit thread:
    ///   result.emit(I) ──→ [flow stages, I → ... → O] → pipe's receptor
    /// ```
    ///
    /// When the flow contains no `map` (pure identity / `fiber`), `I` and
    /// `O` are the same type and the attached Fiber (if any) runs on that shared type.
    ///
    /// ## Threading Guarantee
    ///
    /// All stages in the Flow execute on the circuit's single worker thread.
    /// Stateful operators (inside an attached Fiber) are safe without synchronization.
    ///
    /// @param pipe the pipe to prepend this flow to
    /// @return A pipe that applies this flow before forwarding to `pipe`
    /// @throws NullPointerException if pipe is `null`
    /// @throws Fault                if the pipe is not a runtime-provided implementation
    /// @see Fiber#pipe(Pipe)
    /// @see Circuit#pipe(Pipe)
    /// @since 2.2

    @New
    @NotNull
    Pipe < I > pipe (
      @NotNull Pipe < O > pipe
    );

  }


  /// A unique identifier that distinguishes subject instances within a runtime.
  ///
  /// Each [Subject] has an associated [Id] that uniquely identifies that specific
  /// subject instance. Two subjects with identical names but different instances will
  /// have different Ids.
  ///
  /// ## Uniqueness Guarantees
  ///
  /// IDs are unique within a single JVM/runtime instance:
  /// - Each subject instance receives a distinct [Id]
  /// - IDs remain stable for the lifetime of the subject
  /// - IDs are NOT guaranteed unique across JVM restarts or distributed processes
  ///
  /// ## Equality Semantics
  ///
  /// Marked with `@Identity`, [Id] implementations use **reference equality**:
  /// - Two [Id] references are equal if and only if they refer to the same object
  /// - Use `==` for comparison, not `.equals()`
  /// - Hash codes are inherited from [Object] (identity hash code)
  /// - This provides O(1) comparison performance
  ///
  /// ## Usage
  ///
  /// IDs are primarily used internally for subject tracking and discrimination. API
  /// users typically rely on [Name] for hierarchical identification and subject
  /// lookup. Use [Id] when you need to verify exact subject instance identity.
  ///
  /// ## Implementation Note
  ///
  /// This is a marker interface - the SPI provides concrete implementations. API users
  /// never construct [Id] instances directly; they are obtained via [Subject#id()].
  ///
  /// @see Subject#id()
  /// @see Name
  /// @see Substrate

  @Tenure ( Tenure.INTERNED )
  @Provided
  @Identity
  interface Id {
  }

  /// Indicates a method expected to be idempotent.
  @SuppressWarnings ( "WeakerAccess" )
  @Documented
  @Retention ( SOURCE )
  @Target ( METHOD )
  @interface Idempotent {
  }

  /// Indicates a type whose instances can be compared by (object) reference for equality.

  @SuppressWarnings ( "WeakerAccess" )
  @Documented
  @Retention ( SOURCE )
  @Target ( TYPE )
  @interface Identity {
  }

  /// Represents one or more name (string) parts, much like a namespace.
  /// Instances are interned so equal hierarchies share the same reference and
  /// can be compared by identity.
  ///
  /// ## Identity-Based Equality
  ///
  /// **Critical for implementers and users**: Names use **reference equality** (==), not .equals().
  /// The interning mechanism ensures that:
  ///
  /// - Equivalent name hierarchies are represented by the same object instance
  /// - Name comparison is an O(1) reference check, not string comparison
  /// - Name instances can be used as identity-based map keys
  /// - Memory is conserved by reusing name segments across the hierarchy
  ///
  /// **Interning guarantee**:
  ///
  /// This identity guarantee is **critical for pooling semantics** in [Conduit]
  /// and [Pipe] — the same name always routes to the same pipe instance.
  ///
  /// ## Implementation Requirements
  ///
  /// Clean-room implementations must preserve these semantics by interning name segments,
  /// ensuring thread-safe construction, and reusing existing name instances whenever a hierarchy
  /// is extended. Implementations should minimize redundant parsing and allocation so creation
  /// remains inexpensive.
  ///
  /// **Thread safety**: Name creation is thread-safe. Multiple threads can concurrently
  /// create names with the same path, and all will receive the same interned instance.
  ///
  /// **Depth limits**: Implementations may impose a maximum depth on name hierarchies.
  /// Exceeding this limit results in an [IllegalArgumentException].
  ///
  /// ## Performance Characteristics
  ///
  /// Name lookup and creation operations should be highly optimized as they occur
  /// frequently during subject creation and channel resolution. The interning
  /// overhead is paid once during name creation but saves time on every comparison.
  ///
  /// **Comparison performance**: Name comparison is O(1) reference equality (==),
  /// not O(n) string comparison. This enables fast lookups in pools and maps.
  ///
  /// @see Subject#name()
  /// @see Extent
  /// @see Cortex#name(String)

  @Tenure ( Tenure.INTERNED )
  @Identity
  @Provided
  interface Name
    extends Extent < Name, Name > {

    /// The separator used for parsing a string into name tokens.
    char SEPARATOR = '.';


    /// Returns a name that has this name as a direct or indirect prefix.
    /// Reuses interned segments so invoking this method with the same suffix
    /// yields the same instance.
    ///
    /// @param suffix the name to be appended to this name
    /// @return A name with the suffix appended
    /// @throws NullPointerException if the suffix is `null`
    /// @throws Fault                if the suffix parameter is not a runtime-provided implementation

    @NotNull
    Name name (
      @NotNull Name suffix
    );


    /// Returns a name whose hierarchy includes this name and the supplied path.
    /// The path is parsed using the `.` separator; empty segments are rejected
    /// and interned segments are reused when possible. Paths must not begin or
    /// end with `.` nor contain consecutive separators (for example: `.foo`,
    /// `foo.`, `foo..bar`).
    ///
    /// @param path the string to be parsed and appended to this name
    /// @return A name with the path appended as one or more name parts
    /// @throws NullPointerException     if the path is `null`
    /// @throws IllegalArgumentException if the path is empty or contains empty segments

    @NotNull
    Name name (
      @NotNull String path
    );


    /// Returns a name that has this name as a direct prefix and appends the enum's [Enum#name()] value.
    /// The declaring type of the enum constant is used (per [Enum#getDeclaringClass()]);
    /// cached segments ensure repeated extensions with the same enum reuse the same instance.
    ///
    /// @param constant the enum to be appended to this name
    /// @return A name with the enum name appended as a name part
    /// @throws NullPointerException if the constant is `null`

    @NotNull
    Name name (
      @NotNull Enum < ? > constant
    );


    /// Returns an extension of this name from iterating over a specified [Iterable] of [String] values.
    /// When the iterable is empty this name is returned unchanged; interned segments are reused when possible.
    ///
    /// This extender-shaped variant treats an empty input as the identity
    /// operation. For the constructor-shaped factory — which rejects empty
    /// input with [IllegalArgumentException] — see [Cortex#name(Iterable)].
    ///
    /// @param parts the [Iterable] to be iterated over
    /// @return The extended name
    /// @throws NullPointerException if the [Iterable] is `null` or one of the values returned is `null`

    @NotNull
    Name name (
      @NotNull Iterable < String > parts
    );


    /// Returns an extension of this name from iterating over the specified [Iterable] and applying a transformation function.
    /// When the iterable is empty this name is returned unchanged; interned segments are reused when possible.
    ///
    /// This extender-shaped variant treats an empty input as the identity
    /// operation. For the constructor-shaped factory — which rejects empty
    /// input with [IllegalArgumentException] — see [Cortex#name(Iterable, Function)].
    ///
    /// @param <T>    the type of each value iterated over
    /// @param parts  the [Iterable] to be iterated over
    /// @param mapper the function to be used to map the iterable value type to a String type
    /// @return The extended name
    /// @throws NullPointerException if the [Iterable], mapper, or one of the mapped values is `null`

    @NotNull
    < T > Name name (
      @NotNull Iterable < ? extends T > parts,
      @NotNull Function < ? super T, String > mapper
    );


    /// Returns an extension of this name from iterating over the specified [Iterator] of [String] values.
    /// When the iterator is empty this name is returned unchanged; interned segments are reused when possible.
    ///
    /// @param parts the [Iterator] to be iterated over
    /// @return The extended name
    /// @throws NullPointerException if the [Iterator] is `null` or one of the values returned is `null`

    @NotNull
    Name name (
      @NotNull Iterator < String > parts
    );


    /// Returns an extension of this name from iterating over the specified [Iterator] and applying a transformation function.
    /// When the iterator is empty this name is returned unchanged; interned segments are reused when possible.
    ///
    /// @param <T>    the type of each value iterated over
    /// @param parts  the [Iterator] to be iterated over
    /// @param mapper the function to be used to map the iterator value type
    /// @return The extended name
    /// @throws NullPointerException if the [Iterator], mapper, or one of the mapped values is `null`
    /// @see #name(Iterable, Function)

    @NotNull
    < T > Name name (
      @NotNull Iterator < ? extends T > parts,
      @NotNull Function < ? super T, String > mapper
    );


    /// Creates a `Name` from a [Class].
    /// The package and enclosing class simple names are used for the appended segments
    /// (matching [Class#getCanonicalName()] when available); local or anonymous classes
    /// fall back to the runtime name for their appended value.
    ///
    /// @param type the `Class` to be mapped to a `Name`
    /// @return A name extended with the class hierarchy
    /// @throws NullPointerException if the type is `null`

    @NotNull
    Name name (
      @NotNull Class < ? > type
    );


    /// Creates a `Name` from a `Member`.
    /// The declaring class hierarchy and member name are appended, reusing interned segments.
    ///
    /// @param member the `Member` to be mapped to a `Name`
    /// @return A name extended with the member hierarchy
    /// @throws NullPointerException if the member is `null`

    @NotNull
    Name name (
      @NotNull Member member
    );


    /// Returns a `CharSequence` representation of this name, including enclosing names.
    /// Overrides the default [Extent#path()] to use the dot separator instead of '/'.
    ///
    /// @return A non-`null` `CharSequence` representation with dot-delimited names.

    @Override
    @NotNull
    default CharSequence path () {

      return
        path ( SEPARATOR );

    }


    /// Returns a `CharSequence` representation of this name and its enclosing names.
    /// Applies the mapper to each [#part()] and joins results using the dot separator.
    ///
    /// @param mapper the function used to convert the [#part()] to a character sequence
    /// @return A non-`null` `CharSequence` representation.
    /// @throws NullPointerException if the mapper is `null`

    @NotNull
    CharSequence path (
      @NotNull Function < ? super String, ? extends CharSequence > mapper
    );

  }

  /// Indicates a method that allocates a new instance on invocation.
  ///
  /// Methods marked with this annotation create fresh objects rather than returning
  /// cached or pooled instances. In performance-critical code, the result should be
  /// hoisted outside hot loops to avoid repeated allocations.
  ///
  /// ## Conditional Allocation
  ///
  /// When [#conditional()] is `false` (the default), the method **always** allocates
  /// a new instance. When `true`, allocation depends on input — the method may return
  /// an existing instance when the operation would produce an equivalent result.
  ///
  /// For example, [State#state(Name, int)] returns `this` when an equal slot already
  /// exists, avoiding allocation. Such methods are marked `@New(conditional = true)`.

  @Documented
  @Retention ( SOURCE )
  @Target ( METHOD )
  @interface New {

    /// Whether allocation is conditional on the input.
    ///
    /// - `false` (default): Always allocates a new instance.
    /// - `true`: May return an existing instance when the result would be equivalent.
    ///
    /// @return whether allocation is conditional

    @SuppressWarnings ( "BooleanMethodIsAlwaysInverted" )
    boolean conditional () default false;

  }


  /// Indicates a parameter should never be passed a null argument value

  @Documented
  @Retention ( SOURCE )
  @Target ( {PARAMETER, METHOD, FIELD} )
  @interface NotNull {
  }

  /// A consumer abstraction for receiving typed emissions in a data flow pipeline.
  ///
  /// Pipe is the fundamental building block for processing values as they flow through
  /// the substrate system. Pipes consume emissions via [#emit(Object)] and can be composed,
  /// transformed, registered with channels, and dispatched through circuits.
  ///
  /// ## Creating Pipes
  ///
  /// Pipes are created exclusively through framework-provided factory methods:
  ///
  /// - [Circuit#pipe(Pipe)] - Wrap an existing pipe with asynchronous circuit dispatch
  /// - [Circuit#pipe(Receptor)] - Create a pipe that asynchronously dispatches emissions to a receptor
  /// - [Fiber#pipe(Pipe)] - Attach a same-type Fiber recipe before a pipe
  /// - [Flow#pipe(Pipe)] - Attach a Flow before a pipe (with optional type change)
  ///
  /// For receiving emissions, register receptors directly via [Registrar#register(Receptor)].
  ///
  /// **User code should not implement this interface directly.** All Pipe implementations
  /// are controlled by the framework, which enables future optimizations like sealed types
  /// and monomorphic call sites.
  ///
  /// ## Threading Model
  ///
  /// `emit()` is a [Queued] operation: callers enqueue emissions and return
  /// synchronously, and the owning [Circuit] dispatches them on its processing
  /// thread. Pipe implementations differ only in what sits between the enqueue
  /// and the dispatch:
  ///
  /// - **Circuit-dispatched pipes**: Enqueue on the circuit's ingress queue; the
  ///   downstream receptor runs on the circuit thread
  /// - **Conduit pipes**: Route through any attached fiber/flow, then dispatch on
  ///   the circuit thread
  /// - **Registered pipes**: Invoked on the circuit thread when their channel emits
  ///
  /// In all cases the caller does not observe synchronous execution of the
  /// downstream receptor, and does not observe exceptions raised downstream
  /// (see [#emit(Object)]).
  ///
  /// ## Performance
  ///
  /// Pipe emission is a **critical hot-path operation**. For high throughput:
  /// - Minimize allocations in `emit()` implementations
  /// - Avoid I/O or blocking in hot pipes
  /// - Keep `emit()` logic simple and allocation-free
  ///
  /// The circuit thread is the bottleneck (single-threaded, processes all events sequentially).
  /// Balance work between caller threads (before enqueue) and circuit thread (after dequeue).
  ///
  /// ## Null Handling Contract
  ///
  /// The `@NotNull` annotation on [#emit(Object)] indicates:
  /// - Callers must not pass `null` to `emit()`
  /// - Implementations may assume emissions are non-null
  /// - Passing `null` may throw [NullPointerException] (implementation-dependent)
  ///
  /// For pipelines requiring null-safety, implementations may wrap pipes with guards
  /// that validate emissions before dispatch.
  ///
  /// ## Synchronization Contract
  ///
  /// `emit()` is safe to call concurrently from any thread. Emissions enqueue
  /// against the circuit's ingress queue, and the downstream receptor always
  /// runs on the circuit thread — eliminating concurrent access to receptor
  /// state. No external synchronization is required at the emit site.
  ///
  /// ## Lifecycle
  ///
  /// Pipes have no explicit lifecycle or cleanup:
  /// - No `close()` or `dispose()` methods
  /// - Lifetime managed by owning context (channel, subscription, circuit)
  /// - Registered pipes become inactive when subscription closes
  /// - No resources to release for simple function-based pipes
  ///
  /// @param <E> the class type of emitted values
  /// @see Circuit#pipe(Pipe)
  /// @see Registrar#register(Pipe)
  /// @see Registrar#register(Receptor)

  @Tenure ( Tenure.ANCHORED )
  @Provided
  non-sealed interface Pipe < E >
    extends Substrate < Pipe < E > > {

    /// A method for passing a data value along a pipeline.
    ///
    /// Emissions are queued by the owning circuit and processed sequentially
    /// on the circuit's processing thread, ensuring no concurrent execution
    /// of downstream receptors occurs. The call returns synchronously after
    /// enqueue; it does not wait for dispatch.
    ///
    /// Use [Flow#pipe(Pipe)] for flow processing.
    ///
    /// ## Exception Handling
    ///
    /// Callers of `emit` do **not** observe exceptions thrown by downstream
    /// receptors, flow operators, or other registered pipes — the emission is
    /// enqueued and returns synchronously. Per SPEC §15.4, any exception
    /// raised while the circuit dispatches this emission is isolated from
    /// both the caller and the dispatch loop; sibling receptors and
    /// subsequent emissions continue to be processed.
    ///
    /// @param emission the value to be emitted
    /// @throws NullPointerException if the emission is `null`

    @Queued
    void emit (
      @NotNull E emission
    );

  }

  /// A composable interface for looking up instances by name.
  ///
  /// Pool provides name-based retrieval with composable derived views:
  /// - **[#get(Name)]** - Direct retrieval by name
  /// - **[#get(Subject)]** - Pool using subject's name
  /// - **[#get(Substrate)]** - Pool using substrate's subject name
  /// - **[#pool(Function)]** - Create a cached derived pool
  ///
  /// ## Implementations
  ///
  /// Pool is implemented by substrate components that provide instance retrieval:
  /// - **[Conduit]**: Returns pipes, supports derived pools for domain types and flow processing
  ///
  /// ## Convenience Overloads
  ///
  /// Three ways to retrieve instances (all delegate to [#get(Name)]):
  ///
  /// ```java
  /// // 1. By Name - Direct lookup
  /// Pipe<Integer> pipe = conduit.get(name);
  ///
  /// // 2. By Subject - Extracts name from subject
  /// Pipe<Integer> pipe = conduit.get(otherPipe.subject());
  ///
  /// // 3. By Substrate - Extracts name from substrate's subject
  /// Pipe<Integer> pipe = conduit.get(circuit);
  /// ```
  ///
  /// ## Thread Safety
  ///
  /// Pool implementations must be thread-safe for concurrent retrieval.
  /// The specific caching and creation semantics depend on the implementation.
  ///
  /// @param <T> the type of instances returned by this pool
  /// @see Conduit
  /// @see Name
  /// @since 2.0

  @Abstract
  interface Pool < T > {

    /// Returns the instance for the given substrate.
    ///
    /// Convenience method that extracts the Name from the substrate's subject
    /// and delegates to [#get(Name)]. Equivalent to:
    /// `get(substrate.subject().name())`
    ///
    /// @param substrate the substrate whose subject name identifies the instance
    /// @return The instance for this substrate's name (never null)
    /// @throws NullPointerException if substrate is null
    /// @throws Fault                if the substrate parameter is not a runtime-provided implementation
    /// @see #get(Name)
    /// @see Substrate#subject()

    @NotNull
    default T get (
      @NotNull final Substrate < ? > substrate
    ) {

      requireNonNull ( substrate );

      return get (
        substrate
          .subject ()
          .name ()
      );

    }


    /// Returns the instance for the given subject.
    ///
    /// Convenience method that extracts the Name from the subject and delegates
    /// to [#get(Name)]. Equivalent to: `get(subject.name())`
    ///
    /// @param subject the subject whose name identifies the instance
    /// @return The instance for this subject's name (never null)
    /// @throws NullPointerException if subject is null
    /// @throws Fault                if the subject parameter is not a runtime-provided implementation
    /// @see #get(Name)
    /// @see Subject#name()

    @NotNull
    default T get (
      @NotNull final Subject < ? > subject
    ) {

      requireNonNull ( subject );

      return get (
        subject.name ()
      );

    }


    /// Returns the instance for the given name.
    ///
    /// This is the core retrieval method. The specific creation and caching semantics
    /// depend on the implementation:
    /// - **[Conduit]**: Creates and caches a pipe for the given name
    ///
    /// @param name the name identifying the desired instance
    /// @return The instance for this name (never null)
    /// @throws NullPointerException if name is null
    /// @throws Fault                if the name parameter is not a runtime-provided implementation
    /// @see Name

    @NotNull
    T get (
      @NotNull Name name
    );


    /// Returns a derived pool that applies a transformation function to each result.
    ///
    /// The derived pool caches transformed results by name, applying the function
    /// exactly once per name. This enables composable transformations:
    ///
    /// ```java
    /// // Domain wrapping:
    /// Pool<Situation> situations = conduit.pool(Situation::new);
    ///
    /// // Flow processing:
    /// Pool<Pipe<String>> processed = conduit.pool(flow::pipe);
    /// ```
    ///
    /// @param fn  the transformation function to apply
    /// @param <U> the result type of the derived pool
    /// @return A derived pool that caches transformed results
    /// @throws NullPointerException if fn is null
    /// @since 2.0

    @New
    @NotNull
    < U > Pool < U > pool (
      @NotNull Function < ? super T, ? extends U > fn
    );

  }


  /// Indicates a type exclusively provided by the runtime.

  @Documented
  @Retention ( SOURCE )
  @Target ( TYPE )
  @interface Provided {
  }

  /// Indicates a method or type whose execution is queued to the circuit's processing thread.
  ///
  /// For circuit-managed resources, operations marked with this annotation submit
  /// work to the circuit's processing thread and return immediately. The actual
  /// effect occurs later when the circuit thread processes the queued job.
  ///
  /// ## Annotation Targets
  ///
  /// - **Method-level**: Marks individual methods that queue work (e.g., `Pipe#emit()`)
  /// - **Type-level**: Marks classes/interfaces where all methods queue work (e.g., instrument implementations)
  ///
  /// ## Usage Pattern
  ///
  /// When calling a `@Queued` method, use [Circuit#await()] if you need to
  /// ensure the operation has completed before proceeding:
  ///
  ///     resource.close();    // @Queued - submits job, returns immediately
  ///     circuit.await();     // Wait for close to complete on circuit thread
  ///
  /// ## Semantics
  ///
  /// - Method returns immediately (non-blocking to caller)
  /// - Actual work executes on circuit's processing thread
  /// - Effects are not visible until circuit processes the job
  /// - Ordering is deterministic relative to other circuit operations
  ///
  /// @see Circuit#await()
  /// @see Resource#close()

  @SuppressWarnings ( "WeakerAccess" )
  @Documented
  @Retention ( SOURCE )
  @Target ( {METHOD, TYPE, PARAMETER, FIELD} )
  @interface Queued {
  }


  /// Functional interface for receiving emissions within the substrate.
  ///
  /// A receptor receives emissions and processes them, typically as part of a pipe
  /// or subscription callback. This interface provides a domain-specific alternative
  /// to [java.util.function.Consumer] for emission reception, offering clearer
  /// semantics and avoiding IDE contract warnings when used in neural circuitry contexts.
  ///
  /// ## Purpose
  ///
  /// Receptors serve as the fundamental callback mechanism for emission processing:
  ///
  /// - **Registration**: Register receptors with channels via [Registrar#register(Receptor)]
  /// - **Value processing**: Transform, filter, aggregate, or side effect on emissions
  ///
  /// ## Receptor vs Consumer
  ///
  /// While functionally equivalent to [java.util.function.Consumer], [Receptor] provides:
  ///
  /// - **Semantic clarity**: "receive" is domain-appropriate for signal reception in neural circuitry
  /// - **Contract independence**: Avoids inheriting Consumer's general-purpose contract expectations
  /// - **IDE-friendly**: Custom interface works better with annotations and tooling
  /// - **Architectural consistency**: Pairs with [Pipe] — pipes emit, receptors receive
  ///
  /// ## Thread Safety
  ///
  /// Receptors invoked via pipes or registrations execute on the circuit's processing thread,
  /// providing single-threaded guarantees. Receptors can safely use mutable state,
  /// access shared data structures, and perform stateful operations without
  /// synchronization.
  ///
  /// ## Null Handling
  ///
  /// The substrate framework guarantees that emissions are never `null`. All pipe entry
  /// points validate emissions, so the [#receive(Object)] parameter is always non-null.
  /// Implementations can safely omit null checks.
  ///
  /// @param <E> the type of emission to receive
  /// @author William David Louth
  /// @see Pipe
  /// @see Registrar#register(Receptor)
  /// @since 1.0

  @FunctionalInterface
  interface Receptor < E > {

    /// A shared no-op receptor that discards all emissions.
    ///
    /// Use this for testing, benchmarking, or as a placeholder where a receptor
    /// is required but no processing is needed.
    ///
    /// Example:
    /// ```java
    /// registrar.register(Receptor.NOOP);
    /// ```

    @SuppressWarnings ( "rawtypes" )
    Receptor NOOP = _ -> {
    };


    /// Creates a receptor with explicit type information.
    ///
    /// This factory method provides type information for cleaner usage with `var` declarations.
    /// The type parameter helps Java's type inference determine the receptor's generic type
    /// when using `var`, avoiding the need for explicit type witnesses or verbose
    /// type declarations.
    ///
    /// Example:
    /// ```java
    /// var receptor = Receptor.of(Integer.class, value -> process(value));
    /// registrar.register(receptor);
    /// ```
    ///
    /// The type class is used only for type inference and is not stored or used at runtime.
    ///
    /// @param type     the class representing the emission type (for type inference only)
    /// @param receptor the receptor to return
    /// @param <E>      the class type of emitted values
    /// @return the receptor unchanged
    /// @throws NullPointerException if the type or receptor is `null`

    @NotNull
    @Utility
    static < E > Receptor < E > of (
      @NotNull final Class < E > type,
      @NotNull final Receptor < E > receptor
    ) {

      requireNonNull ( type );
      requireNonNull ( receptor );

      return receptor;

    }


    /// Returns a typed no-op receptor that discards all emissions.
    ///
    /// This factory method provides type information for cleaner usage with `var` declarations.
    /// The type parameter helps Java's type inference determine the receptor's generic type
    /// when using `var`, avoiding the need for explicit type witnesses or verbose
    /// type declarations.
    ///
    /// The returned receptor is the shared [#NOOP] instance cast to the appropriate type.
    ///
    /// Example:
    /// ```java
    /// var receptor = Receptor.of(Integer.class);
    /// registrar.register(receptor);  // Compiles - receptor is Receptor<Integer>
    /// ```
    ///
    /// @param type the class representing the emission type (for type inference only)
    /// @param <E>  the class type of emitted values
    /// @return A typed no-op receptor that discards all emissions
    /// @throws NullPointerException if the type is `null`
    /// @see #NOOP
    /// @see #of()

    @NotNull
    @Utility
    @SuppressWarnings ( "unchecked" )
    static < E > Receptor < E > of (
      @NotNull final Class < E > type
    ) {

      requireNonNull ( type );

      return (Receptor < E >) NOOP;

    }


    /// Returns a no-op receptor that discards all emissions.
    ///
    /// This is the simplest way to obtain a typed no-op receptor when the type
    /// can be inferred from context. Unlike [#of(Class)], this method relies
    /// entirely on type inference.
    ///
    /// The returned receptor is the shared [#NOOP] instance cast to the appropriate type.
    ///
    /// Example:
    /// ```java
    /// Receptor<Integer> receptor = Receptor.of();
    /// registrar.register(Receptor.<Integer>of());
    /// ```
    ///
    /// @param <E> the class type of emitted values
    /// @return A typed no-op receptor that discards all emissions
    /// @see #NOOP
    /// @see #of(Class)

    @NotNull
    @Utility
    @SuppressWarnings ( "unchecked" )
    static < E > Receptor < E > of () {

      return (Receptor < E >) NOOP;

    }


    /// Receives an emission.
    ///
    /// This method is invoked when a value is emitted to the receptor. The method
    /// executes on the circuit's processing thread with single-threaded guarantees
    /// when invoked through registered receptors or circuit-dispatched pipes.
    ///
    /// ## Execution Context
    ///
    /// - **Thread**: Circuit's processing thread (for registered/dispatched receptors)
    /// - **Timing**: Synchronous within emission processing
    /// - **Ordering**: Deterministic with respect to other emissions and circuit events
    ///
    /// ## Implementation Guidelines
    ///
    /// - Avoid blocking operations (network I/O, thread sleeps, etc.)
    /// - Keep processing fast to maintain circuit throughput
    /// - Stateful operations are safe (single-threaded execution on circuit thread)
    ///
    /// ## Exception Handling
    ///
    /// If a receptor throws an uncaught exception, the circuit's dispatch loop is
    /// **not** affected. Per SPEC §15.4 (External Callback Isolation):
    ///
    /// - The exception does not propagate to the circuit's dispatch thread.
    /// - Sibling receptors registered on the same channel still receive the
    ///   current emission.
    /// - Subsequent queued emissions continue to be processed.
    /// - The emission is considered to have failed for this receptor only; the
    ///   specification does not require it to be retried or rerouted.
    /// - How (or whether) the failure is surfaced to observers is
    ///   implementation-defined. Applications needing structured fault reporting
    ///   should consult their implementation's documentation.
    ///
    /// Recovery strategies — quarantining a repeatedly-failing receptor, tripping
    /// a breaker, routing to a dead-letter channel — are the responsibility of
    /// layers built on top of the substrate and are not part of this contract.
    ///
    /// ## Null Contract
    ///
    /// **The emission parameter will never be `null`**. The substrate framework guarantees
    /// that only non-null values are passed to receptors. Implementations can safely assume
    /// the emission is non-null without defensive checks.
    ///
    /// @param emission the emitted value to receive (guaranteed non-null by framework)
    /// @see Circuit

    void receive ( @NotNull E emission );

  }


  /// A temporary registration handle for attaching pipes to a channel during subscription.
  ///
  /// Registrar instances are provided to subscriber callbacks
  /// and allow subscribers to register [Pipe]s that will receive emissions from the
  /// channel identified by the subject.
  ///
  /// ## Temporal Validity
  ///
  /// **CRITICAL**: Registrar instances are **only valid during the
  /// subscriber callback** in which they were provided.
  ///
  /// - **Valid**: Inside the `(subject, registrar) -> {...}` callback body
  /// - **Invalid**: After the callback returns to the framework
  /// - **Invalid**: If stored and called later (asynchronously)
  ///
  /// Calling [#register(Pipe)] after the callback returns is an illegal temporal
  /// use. Implementations MUST detect this violation and signal an
  /// [IllegalStateException]. Registration is not a hot-path operation, so the
  /// performance escape clause that permits undefined behavior for hot-path
  /// temporal types (Current, Closure) does not apply to `Registrar`.
  ///
  /// ## Registration Semantics
  ///
  /// Each call to [#register(Pipe)] adds a pipe to the channel's emission list:
  /// - Multiple pipes can be registered for the same channel
  /// - All registered pipes receive every emission (fan-out)
  /// - Registration order determines pipe invocation order
  /// - Pipes are invoked sequentially on the circuit's processing thread
  ///
  /// ## Execution Model
  ///
  /// All registered pipes execute on the circuit's processing thread:
  /// - Deterministic ordering with circuit events
  /// - Sequential execution (no concurrent pipe invocation)
  /// - No synchronization needed in pipe logic
  /// - Emissions occur in circuit's queue order
  ///
  /// ## Lifecycle
  ///
  /// Registered pipes remain active until:
  /// - The owning [Subscription] is closed
  /// - The channel's source is closed
  /// - The owning circuit is closed
  ///
  /// When a subscription is closed, the source lazily removes the registered pipes
  /// from each channel on the channel's next emission (lazy rebuild).
  ///
  /// ## Multiple Registrations
  ///
  /// The same pipe instance can be registered multiple times:
  /// - Each registration creates an independent callback
  /// - The pipe's `emit()` is called once per registration per emission
  /// - Useful for weighted voting or sampling patterns
  ///
  /// ## Performance Considerations
  ///
  /// Registration is **not** a hot-path operation:
  /// - Occurs only during subscription and channel creation
  /// - Can involve list allocations and coordination
  /// - Not optimized for high-frequency calls
  ///
  /// In contrast, pipe emission (via `pipe.emit()`) **is** hot-path and highly optimized.
  ///
  /// @param <E> the class type of emitted values
  /// @see Subscriber
  /// @see Pipe
  /// @see Subscription

  @Temporal
  @Provided
  interface Registrar < E > {

    /// Registers a pipe to receive emissions from the channel represented by this registrar.
    ///
    /// The pipe's [Pipe#emit(Object)] method will be invoked on the circuit's processing
    /// thread each time a value is emitted to this channel. Multiple pipes can be registered;
    /// all receive each emission in registration order.
    ///
    /// ## Temporal Constraint
    ///
    /// **MUST be called only within the subscriber callback** that provided this registrar.
    /// Calling after the callback returns is an illegal temporal use; implementations MUST
    /// detect this violation and signal an [IllegalStateException]. The performance escape
    /// clause that permits undefined behavior for hot-path temporal types does not apply to
    /// `Registrar` — registration is not a hot-path operation, so detection is mandatory.
    ///
    /// ## Execution Guarantee
    ///
    /// The registered pipe will be invoked:
    /// - On the circuit's processing thread (no synchronization needed)
    /// - In deterministic order with other pipes and circuit events
    /// - Sequentially (never concurrently with other pipes on same channel)
    ///
    /// ## Null Handling
    ///
    /// The pipe parameter must not be `null`. However, implementations may wrap or transform
    /// the pipe before registration (e.g., adding null-checking guards), so the exact pipe
    /// instance stored may differ from the parameter.
    ///
    /// @param pipe the pipe to receive emissions from this channel
    /// @throws NullPointerException  if the pipe is `null`
    /// @throws Fault                 if the pipe parameter is not a runtime-provided implementation
    /// @throws IllegalStateException if called outside the valid subscriber callback
    /// @see Subscriber
    /// @see Pipe#emit(Object)

    void register (
      @NotNull Pipe < ? super E > pipe
    );


    /// Registers a receptor to receive emissions from the channel represented by this registrar.
    ///
    /// This is a convenience method equivalent to:
    ///
    /// The receptor's [Receptor#receive(Object)] method will be invoked on the circuit's
    /// processing thread each time a value is emitted to this channel. This method creates
    /// a pipe internally and registers it using [#register(Pipe)].
    ///
    /// ## Temporal Constraint
    ///
    /// **MUST be called only within the subscriber callback** that provided this registrar.
    /// Calling after the callback returns is an illegal temporal use; implementations MUST
    /// detect this violation and signal an [IllegalStateException]. See [#register(Pipe)] for
    /// the rationale.
    ///
    /// ## Common Use Cases
    ///
    /// This method is ideal for simple emission handling:
    /// - Collection operations: `registrar.register(list::add)`
    /// - Logging: `registrar.register(logger::info)`
    /// - Side effects: `registrar.register(value -> doSomething(value))`
    ///
    /// ## Null Handling
    ///
    /// The receptor parameter must not be `null`. The created pipe will include null-checking
    /// guards, preventing `null` emissions from reaching the receptor.
    ///
    /// @param receptor the receptor to invoke for each emission
    /// @throws NullPointerException  if the receptor is `null`
    /// @throws IllegalStateException if called outside the valid subscriber callback
    /// @see #register(Pipe)
    /// @see Receptor#of(Class, Receptor)
    /// @see Subscriber

    void register (
      @NotNull Receptor < ? super E > receptor
    );

  }


  /// An in-memory buffer of captures that is also a Substrate.
  ///
  /// A Reservoir is created from a Source and is given its own identity (Subject)
  /// that is a child of the Source it was created from. It subscribes to its
  /// source to capture all emissions into an internal buffer.
  ///
  /// @param <E> the class type of the emitted value
  /// @see Capture
  /// @see Source#reservoir()
  /// @see Resource

  @Tenure ( Tenure.EPHEMERAL )
  @Provided
  non-sealed interface Reservoir < E >
    extends Substrate < Reservoir < E > >,
            Resource {

    /// Returns a stream representing the events that have accumulated since the
    /// reservoir was created or the last call to this method.
    ///
    /// **Thread Safety**: This method must be called from a single thread only.
    /// Emissions from the circuit thread may occur concurrently without data loss;
    /// callers can drain repeatedly to retrieve all captured items.
    ///
    /// @return A stream consisting of stored events captured from channels.
    /// @see Capture

    @New
    @NotNull
    Stream < Capture < E > > drain ();

  }

  /// A lifecycle interface for explicitly releasing resources and terminating operations.
  ///
  /// Resource represents objects with explicit cleanup requirements - circuits,
  /// subscriptions, and reservoirs that hold system resources, maintain background operations,
  /// or need deterministic teardown.
  ///
  /// ## Core Implementations
  ///
  /// Types that implement Resource:
  /// - **[Circuit]**: Stops processing thread, releases conduits, closes channels
  /// - **[Subscription]**: Unregisters subscriber, removes pipes from channels
  /// - **[Reservoir]**: Releases captured emissions buffer
  ///
  /// ## Idempotent Close
  ///
  /// The [#close()] method is marked `@Idempotent`:
  /// - First call performs cleanup and releases resources
  /// - Subsequent calls have no effect (safe no-op)
  /// - No exceptions thrown on repeated close
  /// - State transitions: Active → Closing → Closed (terminal, see SPEC §7.1.1)
  ///
  /// This idempotency contract enables safe usage patterns:
  ///
  /// ## Not [AutoCloseable]
  ///
  /// Resource does NOT extend AutoCloseable because:
  /// - Most resources have indefinite lifetimes (circuit runs until shutdown)
  /// - Try-with-resources implies short-lived scoped usage
  /// - Exceptions on close would complicate circuit shutdown
  ///
  /// For scoped resource management, use [Scope] which provides ARM semantics.
  ///
  /// ## Default Implementation
  ///
  /// The default `close()` is a no-op, allowing types without cleanup needs to
  /// implement Resource without boilerplate. Concrete implementations override when
  /// actual cleanup is required.
  ///
  /// ## Thread Safety
  ///
  /// Resource implementations must make `close()` thread-safe:
  /// - Can be called from any thread
  /// - Concurrent calls must be safe (idempotency helps)
  /// - Typically uses atomic state transitions
  ///
  /// Example from Circuit:
  /// - Close can be called from any thread
  /// - Processing thread checks closed state and terminates
  /// - Safe concurrent access to close flag
  ///
  /// ## Lifecycle Guarantees
  ///
  /// After `close()` returns:
  /// - Background operations terminated or marked for termination
  /// - System resources released (threads, timers, handles)
  /// - Pending operations may complete (eventual consistency)
  ///
  /// Post-close rejection of new operations depends on the operation kind,
  /// and each subtype refines the behavior for its own API:
  ///
  /// - **Synchronous factory / mutator methods** (e.g. `Circuit.conduit(Name, Class)`)
  ///   MAY throw [IllegalStateException], or MAY return inert objects whose
  ///   queued operations will themselves be dropped. The specific choice is
  ///   defined by each subtype.
  /// - **Queued operations** — those annotated [Queued] on the subtype — are
  ///   silently dropped on the executor thread rather than raising in the
  ///   caller context. Per SPEC §9.1, such operations **MUST NOT** throw
  ///   synchronously in the caller. Callers that need to confirm no further
  ///   operations will take effect should synchronize via the subtype's own
  ///   mechanism (e.g. `Circuit.await()`).
  ///
  /// For example, after `circuit.close()`:
  /// - Processing thread drains queue then terminates
  /// - Already-queued emissions still process
  /// - See [Circuit#close()] for the subtype-specific refinement.
  ///
  /// ## [Scope] Integration
  ///
  /// A [Scope] manages Resource lifetimes via two patterns:
  /// - [Scope#register(Resource)]: ties a resource's lifetime to the scope,
  ///   so closing the scope also closes the resource.
  /// - [Scope#closure(Resource)]: wraps a resource in a [Closure] for
  ///   ARM-style scoped use — the resource is closed when the closure's
  ///   `consume` block exits.
  ///
  /// @see Circuit#close()
  /// @see Subscription#close()
  /// @see Scope
  /// @see Closure

  @Abstract
  sealed interface Resource
    permits Source,
            Reservoir,
            Subscriber,
            Subscription {

    /// Releases resources and terminates operations associated with this resource.
    ///
    /// Implementations must ensure idempotency - this method can be called multiple times
    /// safely. The first call performs cleanup; subsequent calls are no-ops.
    ///
    /// ## Typical Cleanup Actions
    ///
    /// Depending on the resource type:
    /// - **Circuit**: Stop processing thread, close conduits/channels
    /// - **Subscription**: Unregister subscriber, remove pipes
    /// - **Reservoir**: Release emission buffer
    ///
    /// ## Thread Safety
    ///
    /// Must be thread-safe - can be called concurrently from multiple threads.
    /// Implementations use atomic state or synchronization to ensure only one
    /// cleanup execution occurs.
    ///
    /// ## Queued Execution Model
    ///
    /// **IMPORTANT**: For circuit-managed resources (Circuit, Subscriber, Subscription),
    /// close operations are **queued** and execute on the circuit's processing thread:
    ///
    /// - `close()` **submits** a cleanup job and returns immediately (non-blocking)
    /// - Actual cleanup executes **later** on the circuit thread (deterministic ordering)
    /// - Effects are **not immediate** - cleanup is queued with other circuit operations
    ///
    /// This design ensures:
    /// - **Thread safety**: All resource modifications happen on the circuit thread
    /// - **Deterministic ordering**: Close operations are sequenced with emissions
    /// - **Lock-free hot paths**: Avoids synchronization overhead in emission paths
    ///
    /// ## Ensuring Completion
    ///
    /// If you need to guarantee cleanup has completed before proceeding:
    ///
    ///     subscriber.close();      // Submits queued close job
    ///     circuit.await();         // Wait for job to complete
    ///
    /// Without `await()`, the close effect occurs eventually (typically microseconds)
    /// but is not guaranteed to be visible immediately to the caller.
    ///
    /// ## Non-Circuit Resources
    ///
    /// Resources not managed by a circuit (e.g., [Reservoir]) may use
    /// synchronous close semantics where cleanup completes before returning.
    ///
    /// ## Post-Close Behavior
    ///
    /// After close:
    /// - New operations may throw [IllegalStateException]
    /// - Pending operations may complete (implementation-specific)
    /// - Repeated close() calls are safe (idempotent)
    ///
    /// ## Default Implementation
    ///
    /// The default is a no-op for resources without cleanup requirements.
    /// Concrete types override when actual cleanup is needed.
    ///
    /// @see Scope#close()

    @Idempotent
    @Queued
    default void close () {
    }

  }


  /// An automatic resource management (ARM) scope with hierarchical lifecycle control.
  ///
  /// Scope provides try-with-resources semantics for [Resource]s, automatically
  /// closing registered resources when the scope closes. Scopes can be nested to create
  /// hierarchical resource management trees, where closing a parent scope closes all
  /// child scopes.
  ///
  /// ## Lifecycle States
  ///
  /// Scopes transition through two states:
  /// 1. **Open**: Resources can be registered, closures created, children spawned
  /// 2. **Closed**: All operations throw [IllegalStateException]
  ///
  /// Once closed, a scope cannot be reopened - the state is terminal.
  ///
  /// ## Close Semantics
  ///
  /// When a scope closes:
  /// 1. All registered resources are closed (in reverse registration order)
  /// 2. All child scopes are closed (if not already closed)
  /// 3. The scope transitions to Closed state
  /// 4. Further operations throw [IllegalStateException]
  ///
  /// Close is `@Idempotent` - repeated calls are safe no-ops.
  ///
  /// ## Closure vs Register
  ///
  /// Two lifecycle management patterns:
  ///
  /// **[#register(Resource)]** - Scope-scoped:
  /// - Resource lives for entire scope duration
  /// - Closed when scope closes
  /// - Use for long-lived resources within scope
  ///
  /// **[#closure(Resource)]** - Block-scoped:
  /// - Resource lives only during [Closure#consume(Consumer)] call
  /// - Closed when block exits
  /// - Use for short-lived, localized resource usage
  ///
  /// ## Thread Safety
  ///
  /// Operations must be confined to a single thread:
  /// - **Not thread-safe** - implementations are not required to support concurrent access
  /// - Typical usage: single thread with try-with-resources
  /// - Closing from multiple threads may result in undefined behavior
  ///
  /// ## Error Handling
  ///
  /// If resource close throws during scope close:
  /// - Exception is suppressed
  /// - Remaining resources still closed
  /// - Follows AutoCloseable conventions
  ///
  /// All registered resources are closed even if some fail.
  ///
  /// ## Use Cases
  ///
  /// Scopes excel at:
  /// - **Structured concurrency**: Circuit per request, auto-cleanup
  /// - **Test fixtures**: Setup/teardown resources automatically
  /// - **Resource grouping**: Batch multiple resources for coordinated lifecycle
  /// - **Hierarchical cleanup**: Parent-child resource relationships
  /// - **Exception safety**: Guaranteed cleanup even on exceptions
  ///
  /// @see Resource
  /// @see Closure
  /// @see Cortex#scope()
  /// @see AutoCloseable

  @Tenure ( Tenure.ANCHORED )
  @Provided
  non-sealed interface Scope
    extends Substrate < Scope >,
            Extent < Scope, Scope >,
            AutoCloseable {

    /// Closes this scope and all resources registered with it.
    ///
    /// Performs cleanup in this order:
    /// 1. Closes all registered resources (reverse registration order)
    /// 2. Closes all child scopes (if not already closed)
    /// 3. Transitions scope to Closed state
    ///
    /// After close:
    /// - [#register(Resource)] throws [IllegalStateException]
    /// - [#closure(Resource)] throws [IllegalStateException]
    /// - [#scope()] throws [IllegalStateException]
    /// - Repeated `close()` calls are safe (idempotent)
    ///
    /// ## Idempotency
    ///
    /// Multiple calls to close are safe:
    /// - First call performs cleanup
    /// - Subsequent calls are no-ops
    /// - No exceptions thrown on repeated close
    ///
    /// ## Error Handling
    ///
    /// If a resource throws during close:
    /// - Exception is caught and suppressed
    /// - Remaining resources are still closed
    /// - All resources guaranteed cleanup attempt
    ///
    /// Scope overrides `AutoCloseable.close()` to avoid checked exceptions,
    /// making it safe to use without explicit exception handling in try-with-resources.
    ///
    /// @see Resource#close()

    @Idempotent
    @Override
    void close ();


    /// Creates a closure that manages a resource for the duration of a block.
    ///
    /// Closures provide block-scoped resource management - the resource is automatically
    /// closed when [Closure#consume(Consumer)] returns (or throws).
    ///
    /// ## Repeated Calls
    ///
    /// The returned closure is single-use: once [Closure#consume(Consumer)] completes,
    /// the closure must be considered closed and callers should request a new closure for any
    /// subsequent work against the same resource.
    ///
    /// ## vs Register
    ///
    /// Use `closure()` for short-lived, block-scoped usage.
    /// Use [#register(Resource)] for scope-lifetime resources.
    ///
    /// @param <R>      the type of resource
    /// @param resource the resource to be managed within the closure
    /// @return A closure that manages the resource lifecycle
    /// @throws NullPointerException  if resource is null
    /// @throws IllegalStateException if this scope is closed
    /// @see Closure
    /// @see #register(Resource)

    @New
    @NotNull
    < R extends Resource > Closure < R > closure (
      @NotNull R resource
    );


    /// Registers a resource to be automatically closed when this scope closes.
    ///
    /// The resource will be closed when:
    /// - [#close()] is called explicitly
    /// - The scope exits (if used with try-with-resources)
    /// - An exception causes early scope termination
    ///
    /// Resources are closed in **reverse registration order** - last registered is
    /// first closed (LIFO), ensuring proper dependency cleanup.
    ///
    /// ## Idempotent Registration
    ///
    /// Registering the same resource instance (by identity) more than once is a
    /// safe no-op — the resource retains its original position in the close order
    /// and will only be closed once. This prevents accidental double-close when
    /// multiple code paths register the same resource defensively.
    ///
    /// ## Return Value
    ///
    /// Returns the same resource instance, enabling fluent registration:
    /// `Circuit c = scope.register(cortex.circuit());`
    ///
    /// @param <R>      the type of resource
    /// @param resource the resource to register for lifecycle management
    /// @return The same resource instance (for fluent usage)
    /// @throws NullPointerException  if resource is null
    /// @throws IllegalStateException if this scope is closed
    /// @see #closure(Resource)

    @NotNull
    < R extends Resource > R register (
      @NotNull R resource
    );


    /// Creates a new anonymous child scope within this scope.
    ///
    /// The child scope:
    /// - Is a child of this scope (via [Extent] hierarchy)
    /// - Closes before its parent when parent closes
    /// - Has its own independent resource registrations
    /// - Can have its own children
    ///
    /// @return A new child scope
    /// @throws IllegalStateException if this scope is closed
    /// @see #scope(Name)

    @New
    @NotNull
    Scope scope ();


    /// Creates a new named child scope within this scope.
    ///
    /// Same as [#scope()] but with an explicit name for identification.
    /// The name is incorporated into the child scope's [Subject].
    ///
    /// @param name the name for the new child scope
    /// @return A new named child scope
    /// @throws NullPointerException  if name is null
    /// @throws Fault                 if the name parameter is not a runtime-provided implementation
    /// @throws IllegalStateException if this scope is closed
    /// @see #scope()

    @New
    @NotNull
    Scope scope ( @NotNull Name name );

  }


  /// An opaque interface representing a variable (slot) within a state chain.
  ///
  /// Slots serve as templates for state lookups, providing both a query key
  /// (name + type) and a fallback value. Matching occurs on slot name identity
  /// AND slot type - multiple slots may share the same name if they have different types.
  ///
  /// For primitive types (boolean, int, long, float, double), the [#type()] method
  /// returns the primitive class (e.g., `int.class`, not `Integer.class`).
  ///
  /// Slot instances are immutable - the value returned by [#value()] never changes
  /// across multiple invocations on the same slot instance.
  ///
  /// @param <T> the class type of the value extracted from the target
  /// @see State#value(Slot)
  /// @see Name
  /// @see Cortex#slot(Name, int)

  @Tenure ( Tenure.ANCHORED )
  @Utility
  @Provided
  interface Slot < T > {

    /// Returns the name of this slot.
    ///
    /// @return The name that identifies this slot

    @NotNull
    Name name ();


    /// Returns the class type of this slot's value.
    ///
    /// @return The class type of the value stored in this slot

    @NotNull
    Class < T > type ();


    /// Returns the value stored in this slot.
    ///
    /// @return The value stored in this slot

    @NotNull
    T value ();

  }

  /// An interface for subscribing to source events.
  ///
  /// The fundamental abstraction for event-emitting components with identity.
  ///
  /// Source combines two essential capabilities:
  ///
  /// - **Substrate**: Every source has a unique [Subject] providing identity,
  ///       hierarchical naming, and associated state
  /// - **Subscription**: Sources can be subscribed to, enabling dynamic discovery of
  ///       channels and adaptive topology construction
  ///
  /// Source is implemented by Circuit and Conduit.
  ///
  /// ## Subscription Lifecycle
  ///
  /// When a [Subscriber] is registered with a source, the subscriber receives callbacks lazily
  /// when channels receive their first emission after the subscription is registered. Each callback
  /// supplies a [Registrar] that the subscriber can use to register pipes.
  ///
  /// ## Dynamic Subscription Model
  ///
  /// Sources enable **dynamic discovery** of channels:
  /// 1. Subscriber calls [#subscribe(Subscriber)] to register interest
  /// 2. Source creates channels lazily as they're accessed (e.g., via [Pool#get(Name)])
  /// 3. When a channel receives its first emission, rebuild is triggered
  /// 4. During rebuild, source invokes the subscriber callback for newly encountered channels
  /// 5. Subscriber registers pipes via [Registrar#register(Pipe)] during the callback
  /// 6. Registered pipes receive the current emission and all subsequent emissions on that channel
  ///
  /// Subscriptions may be added or removed while emissions are in flight, and new channels
  /// may appear without halting the circuit. API implementations should ensure these
  /// operations are coordinated on the circuit thread so dispatch remains deterministic for
  /// all observers.
  ///
  /// ## Thread Safety
  ///
  /// [#subscribe(Subscriber)] can be called from any thread. The subscription is
  /// registered asynchronously on the circuit thread. Subscriber callbacks are guaranteed
  /// to execute on the circuit thread, ensuring deterministic ordering and eliminating
  /// the need for synchronization within subscriber logic.
  ///
  /// ## Lazy Rebuild Synchronization
  ///
  /// Sources use a **lazy rebuild** mechanism to synchronize subscription changes:
  /// - When subscriptions are added or removed, a version counter is incremented
  /// - Pipes detect version changes on their next emission
  /// - Pipes rebuild their subscriber lists only when needed (lazy evaluation)
  /// - This avoids blocking emissions during subscription changes
  ///
  /// **Eventual consistency**: Subscription changes are not immediately visible to
  /// all channels. Each channel discovers the change on its next emission and rebuilds
  /// its pipe list. This provides lock-free operation and minimal coordination overhead.
  ///
  /// ## Core Implementations
  ///
  /// Concrete types that implement Source:
  ///
  /// - **[Circuit]**: The central processing engine managing event flow with ordering
  ///       guarantees. Supports subscription for [State] values representing circuit
  ///       lifecycle and status (emission is implementation-dependent; see SPEC §7.1.1).
  /// - **[Conduit]**: Pipe pool factory that emits events and manages channel lifecycle.
  /// - **[Tap]**: Combines a pipe and source for advanced data flow patterns.
  ///
  /// ## Self-Referential Typing
  ///
  /// The type parameter `S extends Source<E, S>` enables type-safe subject extraction:
  ///
  /// This pattern ensures `source.subject().type()` returns the concrete source class,
  /// enabling pattern matching and runtime type discrimination.
  ///
  /// ## Threading Model
  ///
  /// All subscriber callbacks execute on the source's associated circuit processing thread:
  /// - Subscriber callbacks are invoked on the circuit thread
  /// - Deterministic ordering with other circuit events
  /// - No synchronization needed in subscriber logic
  /// - Subscription registration and unregistration are thread-safe
  ///
  /// Multiple threads may call [#subscribe(Subscriber)] concurrently. The source
  /// coordinates subscription changes via the circuit's event queue, ensuring thread-safe
  /// access to internal subscription lists.
  ///
  /// ## Lifecycle
  ///
  /// A subscription progresses through these states:
  /// 1. **Registered**: [#subscribe(Subscriber)] returns [Subscription]
  /// 2. **Active**: Subscriber receives callbacks for newly created channels
  /// 3. **Closed**: After [Subscription#close()], no more callbacks occur
  ///
  /// Subscriptions remain active until explicitly closed or the source is closed.
  ///
  /// ## Ordering Guarantees
  ///
  /// - Subscriber callbacks occur on the first emission to each channel after subscription
  /// - All callbacks for a single subscription are serialized (circuit thread execution)
  /// - Multiple subscribers receive callbacks in registration order
  /// - Emissions to registered pipes follow circuit's deterministic ordering
  ///
  /// ## Use Cases
  ///
  /// Sources excel at:
  /// - **Auto-wiring**: Automatically attach observers to all channels
  /// - **Instrumentation**: Monitor all emission points without explicit wiring
  /// - **Dynamic topologies**: Build networks that adapt to runtime structure
  /// - **Telemetry**: Capture emissions from channels discovered at runtime
  ///
  /// @param <E> the class type of emitted values
  /// @param <S> the self-referential source type (the concrete implementing class)
  /// @see Substrate
  /// @see Subject
  /// @see Subscriber
  /// @see Subscription
  /// @see Pipe
  /// @see Circuit
  /// @see Conduit
  @Abstract
  sealed interface Source < E, S extends Source < E, S > >
    extends Substrate < S >,
            Resource
    permits Circuit,
            Conduit,
            Tap {

    /// Creates a Reservoir to capture emissions from this source.
    ///
    /// A reservoir is an in-memory buffer that captures all emissions from this source's
    /// channels. Use [Reservoir#drain()] to retrieve accumulated emissions as a stream
    /// of [Capture] objects containing both the emission value and its source channel subject.
    ///
    /// ## Usage Pattern
    ///
    /// ```java
    /// // Create reservoir for a conduit
    /// Reservoir<Integer> reservoir = conduit.reservoir();
    ///
    /// // Emit some values
    /// pipe.emit(1);
    /// pipe.emit(2);
    /// circuit.await();
    ///
    /// // Drain captured emissions
    /// reservoir.drain().forEach(capture -> {
    ///     System.out.println(capture.subject().name() + ": " + capture.emission());
    /// });
    /// ```
    ///
    /// ## Incremental Draining
    ///
    /// The `drain()` method is **incremental** - each call returns only emissions
    /// captured since the last drain, not all emissions ever captured.
    ///
    /// ## Resource Lifecycle
    ///
    /// The returned reservoir extends [Resource] and should be closed when no longer
    /// needed to release internal resources and unsubscribe from the source.
    ///
    /// @return A new Reservoir instance for capturing emissions from this source
    /// @see Reservoir
    /// @see Reservoir#drain()
    /// @see Capture

    @New
    @NotNull
    Reservoir < E > reservoir ();


    /// Subscribes a [Subscriber] to receive lazy callbacks during channel rebuild.
    ///
    /// The subscriber callback is invoked on the circuit's processing thread during rebuild,
    /// which occurs when a channel receives its first emission after the subscription is
    /// registered. This allows the subscriber to dynamically register pipes to receive
    /// emissions from that channel.
    ///
    /// ## Subscription Lifecycle
    ///
    /// The returned [Subscription] remains active until:
    /// - [Subscription#close()] is called explicitly
    /// - The source itself is closed — via [Conduit#close()], [Tap#close()], or
    ///   [Circuit#close()] for subscriptions to a circuit's state changes
    ///
    /// ## Registration Timing
    ///
    /// The subscription is registered asynchronously on the circuit's processing thread:
    /// - This method enqueues a registration job and returns immediately
    /// - The subscription becomes active once the circuit processes the job
    /// - **Lazy callback invocation**: Subscriber callbacks are invoked lazily during rebuild,
    ///   which occurs on the first emission to a channel that falls within the subscription's
    ///   visibility window (see [Subscription] and SPEC §7.6.1)
    /// - **Pipe creation vs. emission**: Creating a pipe (e.g., via [Pool#get(Name)])
    ///   does NOT invoke callbacks; callbacks fire when the channel receives its first emission
    ///   within the window
    /// - **Visibility window**: A subscription receives exactly those emissions whose
    ///   ingress-queue enqueue position falls in `[subscribe_enqueue, close_enqueue)`.
    ///   Emissions enqueued before subscribe or at-or-after close are not visible.
    /// - **Implementation note**: The current SPI rebuilds pipelines lazily on emission,
    ///   invoking subscriber callbacks for newly encountered channels during the rebuild phase
    ///
    /// ## Threading Guarantees
    ///
    /// - This method can be called from any thread (thread-safe)
    /// - The subscribe call itself is **asynchronous** - it returns immediately with a
    ///   [Subscription] handle, but the actual registration is submitted as a job to the
    ///   circuit's processing thread
    /// - Use [Circuit#await()] after subscribe if you need to guarantee the subscription
    ///   is registered before proceeding (e.g., before emitting values)
    /// - Subscriber callbacks always occur on the circuit's processing thread
    /// - No synchronization needed in subscriber callbacks
    /// - Multiple concurrent subscriptions are safely coordinated
    ///
    /// ## Multiple Subscriptions
    ///
    /// Multiple subscribers can subscribe to the same source:
    /// - Each receives independent callbacks when channels emit (lazy rebuild)
    /// - Callbacks occur in subscription registration order during rebuild
    /// - Each subscription is independently closeable
    ///
    /// The same subscriber can be subscribed multiple times, creating independent
    /// subscription instances.
    ///
    /// ## Circuit Affinity
    ///
    /// Subscribers are bound at construction time to the circuit that created them.
    /// A [Subscriber] can only be passed to `subscribe` on a source owned by that same
    /// circuit. Passing a foreign subscriber is a caller-misuse condition, not a timing
    /// race: the binding is established when the subscriber is created, so the mismatch
    /// is deterministically knowable at the call site on the caller's thread.
    ///
    /// Per SPEC §7.2, a foreign-subscriber `subscribe` call:
    /// - is detected **synchronously on the caller thread** at the moment of the call,
    /// - is signaled as a [Fault] **before any subscription is registered or queued**,
    /// - leaves **no observable side effects** on either circuit — no registration, no
    ///   pipe allocation, no subscriber callback invocation, no close notification.
    ///
    /// This is a deliberate departure from the queued-operation semantics of SPEC §9.1
    /// (post-close silent drop): those exist because close/emit races are inherent and
    /// cannot be diagnosed at call time, whereas cross-circuit misuse is a programming
    /// error whose silent handling would be impossible for callers to distinguish from a
    /// subscription that simply has not yet delivered an emission (see SPEC §5.5 on
    /// the caller-side return vs. effect-at-caller distinction).
    ///
    /// The enforcement also prevents threading bugs that would otherwise occur if a
    /// subscriber's pipes were invoked on the wrong circuit's worker thread.
    ///
    /// ## Unsubscription
    ///
    /// Both the returned [Subscription] and the [Subscriber] parameter support `close()`:
    /// - [Subscription#close()] closes only this specific subscription
    /// - [Subscriber#close()] closes all subscriptions created by that subscriber
    ///
    /// Both close operations are **asynchronous** - they submit jobs to the circuit's
    /// processing thread. Use [Circuit#await()] after close if you need to guarantee
    /// the unsubscription has completed before proceeding.
    ///
    /// @param subscriber the subscriber to receive lazy callbacks during channel rebuild
    /// @return A subscription handle for controlling future callback delivery
    /// @throws NullPointerException if subscriber is `null`
    /// @throws Fault                if the subscriber belongs to a different circuit than this source — the check runs synchronously on the caller thread before any subscription work is queued, and no registration occurs (see Circuit Affinity above and SPEC §7.2)
    /// @see Subscriber
    /// @see Subscription
    /// @see Registrar
    /// @see #subscribe(Subscriber, Consumer)

    @New
    @NotNull
    @Queued
    default Subscription subscribe (
      @NotNull final Subscriber < E > subscriber
    ) {

      return subscribe (
        subscriber,
        _ -> {
        }
      );

    }


    /// Subscribes a [Subscriber] with an atomic close-notification callback.
    ///
    /// Identical to [#subscribe(Subscriber)] except that the provided `onClose`
    /// callback fires exactly once when the returned subscription is terminated,
    /// regardless of how termination occurs:
    ///
    /// - [Subscription#close()] called explicitly by the caller
    /// - [Subscriber#close()] cascading to this subscription
    /// - The source itself closed (e.g., via [Tap#close()] or [Conduit#close()])
    ///
    /// ## Callback Guarantees
    ///
    /// - **Fire-once**: the callback is invoked exactly once per subscription,
    ///   even if multiple close paths race.
    /// - **Circuit thread**: the callback runs on the circuit's processing thread,
    ///   consistent with all other subscriber callbacks. Exception: when a source
    ///   ([Conduit], [Tap], [Subscriber]) is closed *after* its owning circuit has
    ///   already been closed, the circuit's worker thread no longer exists, so
    ///   the callback runs synchronously on the caller's thread.
    /// - **Identity**: the callback receives the [Subscription] being closed, so
    ///   callers don't need to forward-reference the subscription handle they are
    ///   about to receive from this method.
    /// - **Isolation**: per SPEC §15.4 (External Callback Isolation), an
    ///   exception thrown by the onClose callback does not propagate to the
    ///   circuit's dispatch loop. Cleanup of other subscriptions, and the
    ///   circuit's forward progress, are unaffected. How the failure is surfaced
    ///   to observers is implementation-defined.
    ///
    /// ## Typical Uses
    ///
    /// - Logging subscription lifetimes and termination causes
    /// - Removing the subscription's identity from an external registry
    /// - Releasing resources tied to the subscription's scope
    /// - Triggering reconnect / resubscribe logic when a source closes
    ///
    /// Subscribers that do not need termination notifications should use the
    /// simpler [#subscribe(Subscriber)] overload.
    ///
    /// @param subscriber the subscriber to receive lazy callbacks during channel rebuild
    /// @param onClose    callback invoked exactly once on the circuit thread when the subscription is terminated
    /// @return A subscription handle for controlling future callback delivery
    /// @throws NullPointerException if subscriber or onClose is `null`
    /// @throws Fault                if the subscriber belongs to a different circuit than this source — the check runs synchronously on the caller thread before any subscription work is queued, and the supplied `onClose` callback is NOT invoked on rejection (no subscription exists to close) (see Circuit Affinity on [#subscribe(Subscriber)] and SPEC §7.2)
    /// @see Subscriber
    /// @see Subscription
    /// @see Registrar
    /// @since 2.0

    @New
    @NotNull
    @Queued
    Subscription subscribe (
      @NotNull Subscriber < E > subscriber,
      @NotNull @Queued Consumer < ? super Subscription > onClose
    );


    /// Creates a tap into this source that transforms emissions via a pipe function.
    ///
    /// A Tap follows the structure of this source — channels and subject
    /// hierarchy — but transforms emissions from type E to type T. The function
    /// receives the tap's target pipe and returns a source-compatible pipe.
    ///
    /// ## Usage Pattern
    ///
    /// ```java
    /// // Create tap that transforms integers to strings via flow
    /// var flow = cortex.flow(Integer.class).map(i -> i.toString());
    /// Tap<String> strings = source.tap(flow::pipe);
    ///
    /// // Subscribe to the tap
    /// strings.subscribe(subscriber);
    ///
    /// // Later, clean up
    /// strings.close();
    /// ```
    ///
    /// ## Threading Guarantee
    ///
    /// The transform function executes on the circuit's single worker thread.
    /// Stateful transforms are safe without synchronization.
    ///
    /// ## Resource Lifecycle
    ///
    /// The returned Tap extends [Resource] and should be closed when no longer
    /// needed to unsubscribe from this source and release its downstream
    /// subscriptions.
    ///
    /// @param <T> the transformed emission type
    /// @param fn  receives the tap's target pipe, returns a source-compatible pipe
    /// @return a new Tap that mirrors this source with pipe transformations
    /// @throws NullPointerException if fn is `null`
    /// @see Tap
    /// @see Flow#pipe(Pipe)
    /// @since 2.0

    @New
    @NotNull
    < T > Tap < T > tap (
      @NotNull Function < Pipe < T >, Pipe < E > > fn
    );


    /// Creates a tap into this source that transforms emissions via a flow.
    ///
    /// A Tap follows the structure of this source — channels and subject
    /// hierarchy — but transforms emissions from type E to type T. This is
    /// the concise form of `tap(flow::pipe)`.
    ///
    /// ## Usage Pattern
    ///
    /// ```java
    /// Tap<String> strings =
    ///   conduit.tap(
    ///     cortex.flow(Integer.class).map(Object::toString)
    ///   );
    /// ```
    ///
    /// ## Threading Guarantee
    ///
    /// The flow executes on the circuit's single worker thread. Stateful
    /// operators are safe without synchronization.
    ///
    /// @param <T>  the transformed emission type
    /// @param flow the flow that transforms emissions from E to T
    /// @return a new Tap that mirrors this source with flow-based transformations
    /// @throws NullPointerException if flow is `null`
    /// @throws Fault                if the flow is not a runtime-provided implementation
    /// @see Tap
    /// @see Flow
    /// @see #tap(Function)
    /// @since 2.3

    @New
    @NotNull
    < T > Tap < T > tap (
      @NotNull Flow < E, T > flow
    );


    /// Creates a type-preserving tap into this source that applies per-emission
    /// operators via a fiber.
    ///
    /// A Tap follows the structure of this source — channels and subject
    /// hierarchy — and applies the fiber's stateful or filtering operators
    /// (such as `diff`, `guard`, `limit`, `above`, `below`) to each emission
    /// without changing its type. This is the concise form of `tap(fiber::pipe)`.
    ///
    /// ## Usage Pattern
    ///
    /// ```java
    /// Tap<Integer> deduped =
    ///   conduit.tap(
    ///     cortex.fiber(Integer.class).diff().limit(100)
    ///   );
    /// ```
    ///
    /// ## Threading Guarantee
    ///
    /// The fiber executes on the circuit's single worker thread. Stateful
    /// operators are safe without synchronization.
    ///
    /// @param fiber the fiber that processes emissions of type E
    /// @return a new Tap that mirrors this source with fiber-based per-emission processing
    /// @throws NullPointerException if fiber is `null`
    /// @throws Fault                if the fiber is not a runtime-provided implementation
    /// @see Tap
    /// @see Fiber
    /// @see #tap(Function)
    /// @see #tap(Flow)
    /// @since 2.3

    @New
    @NotNull
    Tap < E > tap (
      @NotNull Fiber < E > fiber
    );

  }

  /// Represents an immutable collection of named slots containing typed values.
  /// Each (name, type) pair maps to at most one slot, and slots iterate from the
  /// most recently written entry to the oldest.
  ///
  /// ## Persistence Semantics
  ///
  /// State is immutable. Operations such as [#state(Name, int)] return new instances
  /// with the supplied slot as the head and any prior slot with the same (name, type)
  /// removed, leaving prior instances unchanged. Writing a slot whose (name, type, value)
  /// already matches the existing entry returns the same instance.
  ///
  /// Because every write upserts in place, the number of slots in a state is bounded
  /// by the number of unique (name, type) pairs ever written, not by the number of
  /// writes. Iteration and streaming views observe slots from the most recently
  /// written entry to the oldest.
  ///
  /// ## Slot Matching
  ///
  /// Slot lookup matches on **both name identity and type**:
  /// - [Name]s are compared by reference (==) due to interning
  /// - Types are compared by Class reference (==)
  /// - Multiple slots with the same name but different types can coexist
  ///
  /// @see Slot
  /// @see Subject#state()
  /// @see Cortex#state()

  @Tenure ( Tenure.ANCHORED )
  @Provided
  interface State
    extends Iterable < Slot < ? > > {

    /// Returns a state that includes a [Slot][Slot] mapped to the specified value.
    /// The new slot becomes the most recently written entry, and any prior slot
    /// with the same name and type is removed. If an equal slot (same name, type,
    /// and value) already exists this state instance is returned.
    ///
    /// @param name  the name of the slot
    /// @param value the value of the slot
    /// @return A state containing the specified slot
    /// @throws NullPointerException if name is `null`
    /// @throws Fault                if the name parameter is not a runtime-provided implementation

    @New ( conditional = true )
    @NotNull
    State state (
      @NotNull Name name,
      int value
    );


    /// Returns a state that includes a [Slot][Slot] mapped to the specified value.
    /// Semantics match [#state(Name, int)] for a `long` value.
    ///
    /// @param name  the name of the slot
    /// @param value the value of the slot
    /// @return A state containing the specified slot
    /// @throws NullPointerException if name is `null`
    /// @throws Fault                if the name parameter is not a runtime-provided implementation

    @New ( conditional = true )
    @NotNull
    State state (
      @NotNull Name name,
      long value
    );


    /// Returns a state that includes a [Slot][Slot] mapped to the specified value.
    /// Semantics match [#state(Name, int)] for a `float` value.
    ///
    /// @param name  the name of the slot
    /// @param value the value of the slot
    /// @return A state containing the specified slot
    /// @throws NullPointerException if name is `null`
    /// @throws Fault                if the name parameter is not a runtime-provided implementation

    @New ( conditional = true )
    @NotNull
    State state (
      @NotNull Name name,
      float value
    );


    /// Returns a state that includes a [Slot][Slot] mapped to the specified value.
    /// Semantics match [#state(Name, int)] for a `double` value.
    ///
    /// @param name  the name of the slot
    /// @param value the value of the slot
    /// @return A state containing the specified slot
    /// @throws NullPointerException if name is `null`
    /// @throws Fault                if the name parameter is not a runtime-provided implementation

    @New ( conditional = true )
    @NotNull
    State state (
      @NotNull Name name,
      double value
    );


    /// Returns a state that includes a [Slot][Slot] mapped to the specified value.
    /// Semantics match [#state(Name, int)] for a `boolean` value.
    ///
    /// @param name  the name of the slot
    /// @param value the value of the slot
    /// @return A state containing the specified slot
    /// @throws NullPointerException if name is `null`
    /// @throws Fault                if the name parameter is not a runtime-provided implementation

    @New ( conditional = true )
    @NotNull
    State state (
      @NotNull Name name,
      boolean value
    );


    /// Returns a state that includes a [Slot][Slot] mapped to the specified value.
    /// Semantics match [#state(Name, int)] for a `String` value.
    ///
    /// @param name  the name of the slot
    /// @param value the value of the slot
    /// @return A state containing the specified slot
    /// @throws NullPointerException if name or value is `null`
    /// @throws Fault                if the name parameter is not a runtime-provided implementation

    @New ( conditional = true )
    @NotNull
    State state (
      @NotNull Name name,
      @NotNull String value
    );


    /// Returns a state that includes a [Slot][Slot] mapped to the specified value.
    /// Semantics match [#state(Name, int)] for a [Name] value.
    ///
    /// @param name  the name of the slot
    /// @param value the value of the slot
    /// @return A state containing the specified slot
    /// @throws NullPointerException if name or value is `null`
    /// @throws Fault                if the name or value parameters are not runtime-provided implementations

    @New ( conditional = true )
    @NotNull
    State state (
      @NotNull Name name,
      @NotNull Name value
    );


    /// Returns a state that includes a [Slot][Slot] mapped to the specified value.
    /// Semantics match [#state(Name, int)] for a nested [State] value.
    ///
    /// @param name  the name of the slot
    /// @param value the value of the slot
    /// @return A state containing the specified slot
    /// @throws NullPointerException if name or value is `null`
    /// @throws Fault                if the name or value parameters are not runtime-provided implementations

    @New ( conditional = true )
    @NotNull
    State state (
      @NotNull Name name,
      @NotNull State value
    );


    /// Returns a state that includes the [Slot][Slot] specified.
    /// The new slot becomes the most recently written entry, and any prior slot
    /// with the same name and type is removed. If an equal slot (same name, type,
    /// and value) already exists this state instance is returned.
    ///
    /// @param slot the slot to be added
    /// @return A state containing the specified slot
    /// @throws NullPointerException if slot is `null`
    /// @throws Fault                if the slot parameter is not a runtime-provided implementation

    @New ( conditional = true )
    @NotNull
    State state (
      @NotNull Slot < ? > slot
    );


    /// Returns a state that includes a [Slot][Slot] with a [Name] value derived from an enum.
    /// The slot's name is derived from the enum's declaring class, and the slot's
    /// value is a [Name] created from the enum constant's name. The new slot
    /// becomes the most recently written entry, and any prior slot with the same
    /// name and type is removed. If an equal slot (same name, type, and value)
    /// already exists this state instance is returned.
    ///
    /// @param value the enum to create a slot from
    /// @return A state containing the specified slot
    /// @throws NullPointerException if value is `null`

    @New ( conditional = true )
    @NotNull
    State state (
      @NotNull Enum < ? > value
    );


    /// Returns a sequential [Stream][Stream] of all [Slots][Slot] in this state,
    /// iterating from the most recently added slot to the oldest.
    ///
    /// @return A non-`null` sequential stream of slots

    @New
    @NotNull
    Stream < Slot < ? > > stream ();


    /// Returns the value of a slot matching the specified template
    /// or the template's own value when no slot is present.
    /// Matching occurs on slot name identity and slot type.
    ///
    /// @param <T>  the value type
    /// @param slot the slot to lookup and fallback onto for the source of a value
    /// @return The stored value when present; otherwise the value returned by the supplied slot
    /// @throws NullPointerException if slot is `null`
    /// @throws Fault                if the slot parameter is not a runtime-provided implementation

    < T > T value (
      @NotNull Slot < T > slot
    );

  }

  /// A [Subject][Subject] represents a referent that maintains an identity as well as [State][State].
  /// Subject is parameterized by its owning substrate type, enabling type-safe subject extraction.
  ///
  /// @param <S> the substrate type this subject represents
  /// @see Id
  /// @see Name
  /// @see State
  /// @see Substrate

  @Tenure ( Tenure.ANCHORED )
  @Identity
  @Provided
  interface Subject < S extends Substrate < S > >
    extends Extent < Subject < S >, Subject < ? > > {

    /// Returns a unique identifier for this subject.
    ///
    /// @return A unique identifier for this subject
    /// @see Id

    @NotNull
    Id id ();


    /// The Name associated with this reference.
    ///
    /// @return A non-null name reference
    /// @see Name

    @NotNull
    Name name ();


    /// Returns a `String` representation of just this subject.
    ///
    /// @return A non-`null` `String` representation.

    @Override
    @NotNull
    default String part () {

      return "Subject[name=%s,type=%s,id=%s]"
        .formatted (
          name (),
          type ().getSimpleName (),
          id ()
        );

    }


    /// Returns the current state of this subject.
    ///
    /// @return the current state
    /// @see State

    @NotNull
    State state ();


    /// Returns the string representation returned from [#path()].
    ///
    /// @return A non-`null` string representation.

    @Override
    @NotNull
    String toString ();


    /// Returns the class of the substrate this subject represents.
    /// This enables type-safe pattern matching and discrimination.
    ///
    /// @return The substrate class type
    /// @see Substrate

    @NotNull
    Class < S > type ();

  }

  /// Connects outlet pipes with emitting subjects within a source.
  ///
  /// Subscriber is a runtime-provided type that invokes user-supplied callbacks when new
  /// channels are created in a [Source]. Users create subscribers via [Circuit#subscriber]
  /// factory methods, providing a [BiConsumer] callback that receives a [Subject] identifying
  /// the channel and a [Registrar] for attaching [Pipe]s to receive emissions.
  ///
  /// ## Circuit-Scoped Subscribers
  ///
  /// Subscribers are created from a **Circuit** and become children of that circuit in the
  /// subject hierarchy. The subscriber's subject path becomes `circuit-name.subscriber-name`.
  /// This establishes that the subscriber observes the ordered view provided by the circuit's
  /// single-threaded processing.
  ///
  /// A subscriber is **permanently bound** to its creating circuit. It may only be passed to
  /// [Source#subscribe(Subscriber)] (or the `onClose` overload) on sources owned by that
  /// *same* circuit — that is, a [Circuit] (as a source of lifecycle state), or any
  /// [Conduit] or [Tap] whose owning circuit matches the subscriber's. Subscribing to a
  /// source from a *different* circuit is a caller-misuse condition and is rejected
  /// synchronously on the caller thread with a [Fault] before any subscription registration
  /// or queuing takes place; no side effects occur on either circuit. See SPEC §7.2 and the
  /// Circuit Affinity section on [Source#subscribe(Subscriber)] for the full enforcement
  /// contract. This differs from the queued post-close drop semantics of SPEC §9.1:
  /// cross-circuit misuse is a deterministic programming error, not a close/emit race, and
  /// silent handling would be indistinguishable to the caller from a subscription that has
  /// simply not yet delivered an emission.
  ///
  /// ## Dynamic Discovery Model
  ///
  /// Subscribers enable **dynamic wiring** of pipes to channels:
  /// 1. Create a subscriber via [Circuit#subscriber(Name, BiConsumer)] with your callback
  /// 2. Subscribe to a source via [Source#subscribe(Subscriber)]
  /// 3. When channels are created, your callback is invoked with subject and registrar
  /// 4. Register pipes via [Registrar#register(Pipe)] during the callback
  /// 5. Registered pipes receive all future emissions from that channel
  ///
  /// This allows building adaptive topologies that respond to runtime structure without
  /// prior knowledge of channel names.
  ///
  /// ## Threading Model
  ///
  /// Subscriber callbacks are always executed on the circuit's processing thread,
  /// ensuring sequential processing and deterministic ordering. This means:
  ///
  /// - **Circuit-thread execution**: Your callback BiConsumer is always invoked on
  ///   the circuit's processing thread, never concurrently.
  /// - **No synchronization needed**: State touched only from the circuit thread
  ///   does not require additional synchronization. The circuit thread is the sole
  ///   accessor of circuit-confined state.
  /// - **Sequential invocations**: All subscriber invocations for a given circuit
  ///   happen sequentially. No two callbacks execute concurrently.
  /// - **No interruption**: The callback cannot be interrupted or executed concurrently
  ///   by multiple threads.
  /// - **External coordination**: Coordination with other threads must still be handled
  ///   explicitly by callers (e.g., via [Circuit#await()]).
  ///
  /// ## Callback Timing and Contract
  ///
  /// **When callbacks occur**:
  ///
  /// - **Lazy callback invocation**: Subscriber callbacks are invoked lazily during pipeline rebuild,
  ///   which occurs on the first emission to a channel after the subscription is registered.
  /// - **Pipe creation vs. emission**: Creating a pipe (e.g., via [Pool#get(Name)])
  ///   does NOT invoke callbacks; callbacks fire when the channel receives its first emission.
  /// - **Implementation note**: The current SPI rebuilds pipelines lazily on emission,
  ///   invoking subscriber callbacks for newly encountered channels during the rebuild phase.
  /// - **Sequential ordering**: All callbacks occur sequentially on the circuit thread.
  ///
  /// **Callback guarantee**: For each channel that receives an emission while a subscription
  /// is active, your callback BiConsumer is invoked exactly once (during the first emission
  /// after subscription) to register pipes for that channel.
  ///
  /// ## Registrar Temporal Contract
  ///
  /// The [Registrar] parameter is **only valid during your callback**:
  ///
  /// Calling `registrar.register()` after your callback returns is an illegal
  /// temporal use. Implementations MUST detect this violation and signal an
  /// [IllegalStateException]. Detection is typically implemented via sentinel
  /// state (no synchronization overhead required), so this contract does not
  /// compromise registrar performance.
  ///
  /// ## Subject Inspection
  ///
  /// The [Subject] parameter provides:
  /// - **Identity**: Unique [Subject#id()] for this channel
  /// - **Name**: Hierarchical [Subject#name()] identifying the channel
  /// - **State**: Associated [Subject#state()] metadata
  /// - **Type**: [Subject#type()] for pattern matching (always `Pipe.class`)
  ///
  /// Use the subject to make decisions about which pipes to register:
  ///
  /// ## Implementation Guidelines
  ///
  /// **Keep callbacks short**:
  /// - Callbacks execute on the circuit's processing thread (the bottleneck)
  /// - Avoid expensive computations or I/O in your callback
  /// - Simple pipe registration should be ~nanoseconds
  /// - Complex initialization should happen asynchronously
  ///
  /// **State management**:
  /// - Your callback can capture variables from the enclosing scope
  /// - No synchronization needed for state accessed only from the callback
  /// - For shared state with other threads, use external synchronization
  ///
  /// **Error handling**:
  /// - Exceptions thrown from your callback are isolated — they do NOT propagate
  ///   to the circuit's dispatch loop
  /// - Sibling subscriber callbacks on the same emission still run; the circuit
  ///   context remains live
  /// - Prefer defensive programming (validate subject, catch exceptions)
  ///
  /// ## Use Cases
  ///
  /// Subscribers excel at:
  /// - **Instrumentation**: Auto-attach metrics/logging to all channels
  /// - **Dynamic routing**: Route emissions based on channel names/metadata
  /// - **Topology discovery**: Build views of runtime structure
  /// - **Testing**: Capture all emissions for validation
  ///
  /// ## Resource Lifecycle
  ///
  /// Subscriber extends [Resource]. Calling [Resource#close()] on a subscriber
  /// unsubscribes from all sources by closing all subscriptions created by
  /// this subscriber. For scoped lifetime management, register the subscriber
  /// with a [Scope] — [Resource] itself is not [AutoCloseable], so direct
  /// use in try-with-resources is not supported.
  ///
  /// ## Threading
  ///
  /// Subscribers can be closed from any thread:
  /// - Thread-safe for concurrent close() calls
  /// - May be closed from within subscriber callbacks
  /// - May be closed from external threads
  ///
  /// The close operation is **asynchronous** - it submits a job to the circuit's
  /// processing thread that iterates and closes all tracked subscriptions.
  /// This ensures deterministic removal without blocking the caller.
  ///
  /// ## Timing Considerations
  ///
  /// After calling `close()`:
  /// - The unsubscription is coordinated asynchronously on the circuit thread
  /// - Use [Circuit#await()] if you need to guarantee all subscriptions are closed
  /// - Already-queued callbacks may still execute before close completes
  /// - Each subscription's close follows the same lazy rebuild semantics
  ///
  /// ## Idempotency
  ///
  /// Calling `close()` multiple times is safe and has no additional effect.
  /// Closing a subscriber with no subscriptions is also safe.
  ///
  /// @param <E> the class type of the emitted value
  /// @see Source
  /// @see Source#subscribe(Subscriber)
  /// @see Subject
  /// @see Registrar
  /// @see Pipe
  /// @see Resource
  /// @see Circuit#subscriber(Name, BiConsumer)

  @Tenure ( Tenure.ANCHORED )
  @Provided
  non-sealed interface Subscriber < E >
    extends Substrate < Subscriber < E > >,
            Resource {

  }

  /// A cancellable handle representing an active subscription to a source.
  ///
  /// Subscription is returned by [Source#subscribe(Subscriber)] and allows
  /// the subscriber to cancel interest in future events. Each subscription has its
  /// own identity (via [Substrate]) and can be closed to unregister.
  ///
  /// ## Lifecycle
  ///
  /// A subscription progresses through these states:
  /// 1. **Active**: Created by `subscribe()`, subscriber receives callbacks
  /// 2. **Closed**: After `close()` is called, no more callbacks occur
  ///
  /// Subscriptions remain active until explicitly closed or the source itself is closed.
  ///
  /// ## Cancellation Semantics
  ///
  /// Calling [Resource#close()] on a subscription:
  /// - Unregisters the subscriber from the source
  /// - Stops all future subscriber callbacks
  /// - Removes all pipes registered by this subscriber from active channels
  /// - Is **idempotent** - repeated calls are safe and have no effect
  ///
  /// ## Threading
  ///
  /// Subscriptions can be closed from any thread:
  /// - Thread-safe for concurrent close() calls
  /// - May be closed from within subscriber callbacks
  /// - May be closed from external threads
  ///
  /// ## Asynchronous Semantics
  ///
  /// `close()` is an asynchronous operation: it enqueues a close job on the
  /// circuit's ingress queue and returns to the caller immediately. The close
  /// does not take effect at the moment of return. Its effect becomes observable
  /// only after the circuit context has processed the close job. A caller that
  /// needs to confirm the subscription has been torn down — for example, to
  /// verify no further emissions will reach its receptors — MUST call
  /// [Circuit#await()] to synchronize.
  ///
  /// ## Visibility Window
  ///
  /// A subscription's **visibility window** is the half-open interval of
  /// ingress-queue positions `[subscribe_enqueue, close_enqueue)`. The
  /// subscription receives exactly those emissions whose ingress-queue enqueue
  /// position falls within this window:
  ///
  /// - Emissions enqueued **before** the subscribe job are not visible — the
  ///   registration has not yet been installed when they are processed.
  /// - Emissions enqueued **within the window** are delivered in order.
  /// - Emissions enqueued **at or after** the close job are not visible — the
  ///   unsubscription is processed in ingress-queue order, and subsequent
  ///   emissions trigger a lazy rebuild that excludes the closed subscription.
  ///
  /// For a single caller thread, per-caller FIFO makes the window rule
  /// deterministic: `subscribe(); emit(X); close(); emit(Y); await()` delivers
  /// `X` to the subscription but not `Y`. Across multiple concurrent caller
  /// threads, the relative enqueue positions are determined by the ingress
  /// queue's synchronization mechanism and may differ across runs, but each
  /// subscription's window is still defined by its own enqueue positions.
  ///
  /// See SPEC §7.6.1 for the normative statement.
  ///
  /// **Lazy unsubscription**: The implementation uses version-tracked lazy
  /// rebuild. The close job increments a version counter within the circuit
  /// context; named pipes detect the version mismatch on their next emission
  /// and rebuild their downstream pipe lists. This is a performance optimization
  /// that preserves the visibility window rule — the version increment itself
  /// is processed in ingress-queue order.
  ///
  /// ## Resubscription
  ///
  /// After closing a subscription, the same subscriber can be resubscribed to the
  /// same or different sources. Each call to `subscribe()` creates a new,
  /// independent subscription instance.
  ///
  /// @see Source#subscribe(Subscriber)
  /// @see Subscriber
  /// @see Resource#close()
  /// @see Scope

  @Tenure ( Tenure.ANCHORED )
  @Provided
  non-sealed interface Subscription
    extends Substrate < Subscription >,
            Resource {

  }

  /// Base interface for all substrate components that have an associated subject.
  /// Substrate is self-referential and parameterized by its own type, enabling
  /// typed subject extraction where `substrate.subject()` returns `Subject<ThisSubstrateType>`.
  ///
  /// @param <S> the self-referential substrate type
  /// @see Subject

  @Abstract
  @Extension
  sealed interface Substrate < S extends Substrate < S > >
    permits Cortex,
            Current,
            Pipe,
            Scope,
            Reservoir,
            Source,
            Subscriber,
            Subscription {

    /// Returns the typed subject identifying this substrate.
    /// The subject is parameterized by this substrate's type for type-safe access.
    ///
    /// @return The typed subject associated with this substrate
    /// @see Subject

    @NotNull
    Subject < S > subject ();

  }

  /// A tap into a source that transforms emissions.
  ///
  /// A Tap follows the structure of its underlying source — pipes and subject
  /// hierarchy — but transforms emissions from one type to another. Pipes
  /// are created automatically as subjects appear in the source.
  ///
  /// ## Purpose
  ///
  /// Tap enables type transformation across source boundaries while preserving
  /// the structural relationship between channels and subjects. This is particularly
  /// useful for:
  ///
  /// - Translating domain-specific signals to universal tokens (e.g., Serventis → Signetics)
  /// - Projecting typed emissions to serializable forms for distribution
  /// - Creating derived views with different emission types
  ///
  /// ## Resource Lifecycle
  ///
  /// As a [Resource], a Tap manages its subscription to the underlying source.
  /// Closing a tap unsubscribes from the source and releases its downstream
  /// subscriptions so emissions stop flowing through the tap. Mirrored channel
  /// references held by callers remain valid and are reclaimed by the garbage
  /// collector once unreachable. Taps should be closed when no longer needed.
  ///
  /// ## Example
  ///
  /// ```java
  /// // Create tap that transforms signals to names
  /// var flow = cortex.flow(Signal.class).map(signal -> name(signal.sign()));
  /// Tap<Name> names = conduit.tap(flow::pipe);
  ///
  /// // Subscribe to receive transformed emissions
  /// Subscription sub = names.subscribe(subscriber);
  ///
  /// // Clean up when done
  /// names.close();
  /// ```
  ///
  /// @param <T> the transformed emission type
  /// @see Source#tap(Function)
  /// @see Flow#pipe(Pipe)
  /// @see Resource
  /// @since 2.0

  @Tenure ( Tenure.EPHEMERAL )
  @Provided
  non-sealed interface Tap < T >
    extends Source < T, Tap < T > > {

  }

  /// Indicates a type that is transient and whose reference should not be retained.

  @SuppressWarnings ( "WeakerAccess" )
  @Documented
  @Retention ( SOURCE )
  @Target ( {PARAMETER, TYPE} )
  @interface Temporal {
  }


  /// Indicates the retention behavior of instances of this type.
  ///
  /// This annotation documents whether instances are retained by their creating container
  /// or exist only while externally referenced:
  ///
  /// - **INTERNED**: Instances are pooled/cached by key. Same key returns same instance.
  ///   The owning container maintains a reference (memory footprint in container).
  ///
  /// - **EPHEMERAL**: Instances are not retained by the creator. Each creation is independent.
  ///   Only caller references keep them alive (reference-counting style lifecycle).
  ///
  /// ## Examples
  ///
  /// ```java
  /// // Names are interned - same string returns same instance
  /// @Tenure(INTERNED)
  /// interface Name { ... }
  ///
  /// // Cortex doesn't cache circuits - each call creates independent instance
  /// @Tenure(EPHEMERAL)
  /// interface Circuit { ... }
  /// ```
  ///
  /// ## Relationship to @New
  ///
  /// [New] is a method-level annotation indicating allocation behavior.
  /// [Tenure] is a type-level annotation indicating retention semantics.
  /// They are complementary: a `@New` method may return either `INTERNED` or `EPHEMERAL` types.
  ///
  /// @see New

  @Documented
  @Retention ( SOURCE )
  @Target ( TYPE )
  @interface Tenure {

    /// Instance is pooled by key. Same key returns same instance.
    /// The owning container maintains a reference (memory footprint).
    ///
    /// Examples: [Name] (interned)
    int INTERNED = 1;

    /// Instance is not cached by creator. Each creation is independent.
    /// Only the caller's reference keeps it alive.
    ///
    /// Examples: [Circuit] (not cached by Cortex), [Conduit] (not cached by Circuit)
    int EPHEMERAL = 2;

    /// Instance tenure depends on attachment to another substrate.
    /// Standalone instances are ephemeral; attached instances are retained (interned) by
    /// the substrate they are attached to via subscription, nesting, or registration.
    ///
    /// Examples: [Subscription] (anchored to source), [Pipe] (anchored to conduit)
    int ANCHORED = INTERNED | EPHEMERAL;


    /// The tenure type for instances of this type.
    ///
    /// @return the tenure type
    int value ();

  }


  /// Indicates a type that serves a utility purpose.

  @SuppressWarnings ( "WeakerAccess" )
  @Documented
  @Retention ( SOURCE )
  @Target ( {TYPE, METHOD} )
  @interface Utility {
  }


  /// Returns the singleton Cortex instance, lazily initialized on first access.
  ///
  /// The Cortex provides factory methods for creating circuits, names, pipes, and other
  /// substrate components. This is the primary entry point for the Substrates API.
  ///
  /// @return The singleton Cortex instance
  /// @throws IllegalStateException if the provider cannot be loaded
  /// @see Cortex
  /// @see io.humainary.substrates.spi.CortexProvider

  @NotNull
  static Cortex cortex () {

    return
      io.humainary.substrates.spi.CortexProvider.cortex ();

  }


  /// Controls how emissions are dispatched within a conduit.
  ///
  /// When creating a conduit via [Circuit#conduit(Class, Routing)], the routing
  /// determines whether emissions stay local to the target channel or propagate
  /// upward through the name hierarchy.
  ///
  /// @see Circuit#conduit(Class, Routing)
  /// @see Circuit#conduit(Name, Class, Routing)
  /// @since 2.0

  enum Routing {

    /// Emissions are dispatched only to the target channel's subscribers.
    /// This is the default behavior used by [Circuit#conduit(Class)].

    PIPE,

    /// Emissions propagate from the target channel upward through all ancestor
    /// channels in the name hierarchy, leaf-first. This enables hierarchical
    /// dispatch patterns — such as logging — where a single emission at a leaf
    /// channel ripples upward without requiring explicit subscriber wiring at
    /// each level.
    ///
    /// Ancestor channels are created lazily if they do not already exist and
    /// do not require pipes — they serve purely as subscriber attachment
    /// points for hierarchical observation.

    STEM

  }

  /// Base fault for provider-related runtime errors.
  ///
  /// The Substrates framework allows multiple provider implementations (SPI) to coexist.
  /// This fault is thrown when provider-related errors occur at runtime, such as
  /// mixing objects from different providers or other provider-specific failures.
  ///
  /// SPI implementations must extend this fault to provide concrete fault types
  /// specific to their provider implementation.
  ///
  /// ## Common Causes
  ///
  /// - Creating a [Name] from one [Cortex] and passing it to a [Circuit] from a different Cortex
  /// - Creating a [Subscriber] from one provider and subscribing to a [Source] from another provider
  /// - Mixing objects across providers
  ///
  /// ## Resolution
  ///
  /// Ensure all API objects used together come from the same provider instance:
  ///
  /// @see Cortex

  abstract class Fault
    extends RuntimeException {

    /// Constructs a new fault with the specified detail message.
    ///
    /// @param message the detail message

    protected Fault ( final String message ) {

      super ( message );

    }


    /// Constructs a new fault with a detail message and cause.
    ///
    /// @param message the detail message
    /// @param cause   the underlying cause

    protected Fault (
      final String message,
      final Throwable cause
    ) {

      super (
        message,
        cause
      );

    }

  }

}
