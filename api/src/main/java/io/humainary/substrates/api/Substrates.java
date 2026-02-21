/// Copyright © 2025 William David Louth
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
/// 2. **Conduit** creates **Channel** (lazily, on first access by name)
/// 3. **Channel** exposes **Pipe** for emissions
/// 4. **Subscriber** attaches **Pipe** to channel via **Registrar**
/// 5. Emissions flow: `Pipe → [circuit thread] → Flow → Channel → Subscriber Pipes`
///
/// **Key Insight**: Channels are discovered dynamically by subscribers, enabling adaptive
/// topologies that respond to runtime structure without prior knowledge of channel names.
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
/// - low-latency emissions
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
/// - Channels detect changes on next emission (not immediately)
/// - No blocking or global coordination required
/// - Lock-free operation with minimal overhead
///
/// **State Isolation**:
/// - Flow operators maintain independent state per channel
/// - Subscriber state accessed only from circuit thread (no sync needed)
/// - No shared mutable state between circuits
///
/// ## Performance Profile
///
/// The Substrates API is designed for **extreme performance** to enable neural-like network exploration,
/// achieving ~2.98ns emission latency (≈336M ops/sec). This performance enables:
/// - Billions of emissions through cyclic networks
/// - Real-time adaptive topologies
/// - Spiking neural network implementations
/// - Massive parallelism via multiple circuits with virtual threads
///
/// **Performance best practices**:
/// - Keep flow pipelines short (each stage adds overhead)
/// - Avoid I/O and blocking in subscriber callbacks
/// - Prefer stateless flow operators over stateful when possible
///
/// ## Runtime Pillars
///
/// - **[Circuit]**: Single-threaded execution engine that drains emissions deterministically.
///     Provides `await()` for external coordination and guarantees memory visibility.
///
/// - **[Conduit]**: Pooled percept factory that creates domain-specific wrappers around channels.
///     Routes emissions through flow pipelines to registered subscribers.
///
/// - **[Channel]**: Subject-backed emission port owned by a conduit. Channels pool by name
///     ensuring stable routing and identity guarantees.
///
/// - **[Pipe]**: Emission carrier abstraction that routes typed values through flows.
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
/// - **[Flow]**: Configurable processing pipeline for data transformation. Operators include
///     diff, guard, limit, sample, sift, reduce, replace, peek.
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
/// - Channels emit signals through dynamic connections
/// - Subscribers rewire topology in response to runtime structure
/// - Flow operators create temporal dynamics (diff, sample, limit)
///
/// This enables emergent computation from substrate primitives with deterministic behavior
/// suitable for production systems.
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
/// - **[Circuit#conduit(Composer)]**: Create domain-specific percepts
/// - **[Lookup#percept(Name)]**: Obtain percepts by name
/// - **[Channel#pipe()]**: Get pipe for emissions
/// - **[Source#subscribe(Subscriber)]**: Dynamically discover channels
///
/// @author William David Louth
/// @since 1.0

public interface Substrates {

  /// The system property key used to specify the Provider class.
  /// The specified class must extend [io.humainary.substrates.spi.CortexProvider] and have a no-arg constructor.
  /// If not set, provider discovery falls back to [ServiceLoader].
  String PROVIDER_PROPERTY = "io.humainary.substrates.spi.provider";


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


  /// Indicates a type that serves primarily as an abstraction for other types.
  @SuppressWarnings ( "WeakerAccess" )
  @Documented
  @Retention ( SOURCE )
  @Target ( TYPE )
  @interface Abstract {
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
  /// - **Type-level**: Marks classes/interfaces where all methods queue work (e.g., percept implementations)
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
  @Target ( {METHOD, TYPE} )
  @interface Queued {
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
  /// @see Channel
  /// @see Subject

  @Tenure ( Tenure.EPHEMERAL )
  @Provided
  interface Capture < E > {

    /// Returns the emitted value.
    ///
    /// @return The emitted value

    @NotNull
    E emission ();


    /// Returns the subject of the channel that emitted the value.
    ///
    /// The subject identifies which channel produced this emission, providing access to
    /// the channel's name, ID, and state.
    ///
    /// @return The subject of the channel that emitted this value
    /// @see Subject
    /// @see Channel

    @NotNull
    Subject < Channel < E > > subject ();

  }

  /// A hierarchical computational unit supporting arbitrarily deep nested structures.
  ///
  /// Cell combines four capabilities to enable complex hierarchical data flow:
  /// - **[Pipe]**: Receives typed input emissions
  /// - **[Lookup]**: Creates and caches child cells by name
  /// - **[Source]**: Emits child outputs for observation
  /// - **[Extent]**: Provides iteration over child cells
  ///
  /// ## Architecture
  ///
  /// Each cell has:
  /// - **Transformer**: Converts output pipe to input pipe (defines cell's processing)
  /// - **Aggregator**: Transforms output before routing upward
  /// - **Hub**: Internal channel system for child emission routing
  /// - **Outlet**: External pipe receiving aggregated outputs
  ///
  /// ## Data Flow
  ///
  /// **Downward (input to cell)**:
  /// 1. `cell.pipe().emit(input)` called by external code
  /// 2. Input flows through ingress to inlet
  /// 3. Ingress determines how input reaches children
  ///
  /// **Upward (child to parent)**:
  /// 1. Child emits to its outlet
  /// 2. Outlet pipes to parent hub's channel
  /// 3. Hub subscription forwards to parent outlet (async dispatch)
  /// 4. Parent outlet flows to grandparent hub, etc.
  ///
  /// **Key insight**: Upward flow uses hub subscriptions rather than direct pipes,
  /// enabling **stack-safe arbitrarily deep hierarchies** by breaking synchronous call chains.
  ///
  /// Subscriptions observe what's happening within child cells but don't interfere
  /// with the primary upward data flow to the outlet.
  ///
  /// ## Stack Safety
  ///
  /// Unlike direct pipe chains, cells use async dispatch through hub subscriptions:
  /// - Direct pipes: `child → parent → grandparent` (synchronous, stack depth limited)
  /// - Cell hubs: `child → [circuit queue] → parent → [circuit queue] → grandparent` (async, unlimited depth)
  ///
  /// This enables deep hierarchies (100+ levels) without stack overflow, essential for
  /// neural-like networks and deep computational graphs.
  ///
  /// ## Threading Model
  ///
  /// All cell operations execute on the owning circuit's thread:
  /// - Child creation happens on circuit thread
  /// - Upward emission routing happens on circuit thread
  /// - Subscriber callbacks happen on circuit thread
  ///
  /// However, `cell.pipe().emit()` can be called from any thread - emissions are enqueued
  /// and processed on the circuit thread.
  ///
  /// ## Use Cases
  ///
  /// Cells excel at:
  /// - **Deep computational hierarchies**: Multi-level processing without stack overflow
  /// - **Tree-structured data flow**: Hierarchical aggregation and distribution
  /// - **Neural network topologies**: Deep networks with recurrent connections
  /// - **Organizational structures**: Modeling hierarchies with upward reporting
  /// - **Compositional computation**: Building complex behavior from simple primitives
  ///
  /// @param <I> the class type of input values received by the cell
  /// @param <E> the class type of output values emitted by the cell
  /// @see Circuit#cell(Composer, Composer, Receptor)
  /// @see Pipe
  /// @see Lookup
  /// @see Source
  /// @since 1.0

  @Tenure ( Tenure.ANCHORED )
  @Provided
  non-sealed interface Cell < I, E >
    extends Percept,
            Receptor < I >,
            Lookup < Cell < I, E > >,
            Source < E, Cell < I, E > >,
            Extent < Cell < I, E >, Cell < I, E > > {

  }


  /// A (subject) named port in a conduit that provides a pipe for emission.
  ///
  /// Channels serve as named entry points into a conduit's processing pipeline. Each
  /// channel has a unique Subject with an associated Name. Emissions pass through
  /// the conduit's [Flow] pipeline on the circuit thread before reaching the channel,
  /// which then dispatches to registered subscribers.
  ///
  /// ## Temporal Contract
  ///
  /// **IMPORTANT**: Channels follow a temporal contract - they are only valid during
  /// the [Composer#compose(Channel)] callback. The channel reference **must not be
  /// retained** or used outside the composer callback. Instead, retain the pipe
  /// returned by [#pipe()] or [#pipe(Configurer)], which remains valid for the lifetime
  /// of the conduit.
  ///
  /// Violating this contract by storing channel references leads to undefined behavior.
  /// Always call [#pipe()] within the composer callback and store/use the returned pipe.
  ///
  /// ## Pooling and Identity
  ///
  /// Channels are pooled by name within a conduit. **Channels with the same name are
  /// identical objects** - repeated requests with the same name return the same channel
  /// instance internally.
  ///
  /// Note: Channels are internal framework constructs accessed indirectly through
  /// percepts created by composers. The conduit's `percept(Name)` method returns percepts,
  /// not channels. Each percept has an associated channel created by the framework.
  ///
  /// This identity guarantee enables stable routing - all emissions for a given name
  /// are routed to the same channel and thus the same subscribers.
  ///
  /// ## Thread Safety
  ///
  /// Channels can be accessed from any thread. The [#pipe()] method returns
  /// a pipe that can be called from any thread. Emissions are enqueued to the owning
  /// circuit and processed on the circuit thread.
  ///
  /// @param <E> the class type of emitted values
  /// @see Conduit
  /// @see Circuit#conduit(Composer)

  @Tenure ( Tenure.INTERNED )
  @Provided
  non-sealed interface Channel < E >
    extends Substrate < Channel < E > > {

    /// Returns a pipe that this channel holds.
    ///
    /// The returned pipe enqueues emissions to the circuit. On the circuit thread,
    /// emissions pass through the conduit's flow pipeline before reaching this channel,
    /// which then dispatches to all registered subscribers. Each call may return a
    /// different pipe wrapper instance, but all route to the same underlying channel.
    ///
    /// The pipe can be called from any thread - emissions are enqueued to the owning
    /// circuit and processed on the circuit thread.
    ///
    /// @return A pipe routing to this channel
    /// @see Pipe

    @New
    @NotNull
    Pipe < E > pipe ();


    /// Returns a pipe with per-instance flow processing on the circuit thread.
    ///
    /// This is the mechanism for percepts to offload stateful work to the circuit's
    /// single-threaded execution queue. Since a percept's methods run on the caller's
    /// thread, any mutable state (deduplication, accumulation, rate limiting) would
    /// require synchronization. By configuring per-pipe flow operators here, that
    /// stateful logic executes on the circuit thread instead — no synchronization needed.
    ///
    /// Each call creates independent flow operator instances with their own state. This
    /// differs from conduit-level flow, where a single set of operators is shared across
    /// all channels. For stateful operators like {@link Flow#diff}, {@link Flow#reduce},
    /// and {@link Flow#limit}, per-pipe flow ensures each percept maintains its own state.
    ///
    /// On the circuit thread, the per-pipe flow is applied first, followed by the
    /// conduit-level flow, before emissions reach the channel's subscribers.
    ///
    /// The configurer is invoked eagerly during this call. Any exceptions thrown by
    /// [Flow] or [Sift] methods are wrapped in an [Exception] and propagated to the caller.
    ///
    /// @param configurer A configurer responsible for configuring flow in the conduit.
    /// @return A pipe instance that will use this channel to emit values
    /// @throws NullPointerException if the specified configurer is `null`
    /// @throws Exception            if the configurer throws
    /// @see Pipe
    /// @see Flow

    @New
    @NotNull
    Pipe < E > pipe (
      @NotNull Configurer < ? super Flow < E > > configurer
    );

  }


  /// A computational network of conduits, cells, channels, and pipes.
  /// Circuit serves as the central processing engine that manages data flow across
  /// the system, providing precise ordering guarantees for emitted events and
  /// coordinating the interaction between various components.
  ///
  /// Circuits can create and manage conduits and cells, allowing for
  /// complex event processing pipelines to be constructed and maintained.
  ///
  /// ## Subject Hierarchy
  ///
  /// Circuits are created by [Cortex#circuit()] and have the cortex subject as their parent,
  /// making them depth=2 in the subject hierarchy (Cortex → Circuit). All conduits and cells
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
  ///   (e.g., percepts created by [Composer], channel pipes)
  /// - **Outlet role**: Pipes on the egress side receiving processed data from the circuit
  ///   (e.g., subscriber pipes, cell egress)
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
  ///       by the circuit. This guarantee is absolute - earlier emissions complete before
  ///       later ones begin execution.
  /// - **Circuit-thread confinement**: State touched only from the circuit thread requires
  ///       no additional synchronization. The circuit thread is the sole accessor of circuit-confined state.
  /// - **Bounded enqueue**: Caller threads do not execute circuit work but may briefly
  ///       coordinate with the circuit while enqueueing work; implementations must keep this
  ///       path short and responsive under contention
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
  /// @see Cell

  @Tenure ( Tenure.EPHEMERAL )
  @Provided
  non-sealed interface Circuit
    extends Source < State, Circuit >,
            Resource {

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


    /// Creates a hierarchical cell using the circuit's name.
    ///
    /// Convenience method that uses this circuit's subject name for the cell. Multiple calls
    /// to this method will create cells with the same name (the circuit's name). For distinct
    /// cell names, use [#cell(Name, Composer, Composer, Receptor)] with explicit names.
    ///
    /// Cells enable arbitrarily deep computational hierarchies with stack-safe upward flow.
    /// Each cell combines input processing (ingress), output transformation (egress),
    /// and child cell management into a unified structure.
    ///
    /// ## Parameters
    ///
    /// - **ingress**: Creates input pipe from output pipe with subject context. Defines how emissions
    ///   flow downward through the cell hierarchy. Common patterns:
    ///   - Identity: `(subject, pipe) -> pipe` (pass through unchanged)
    ///   - Transformation: `(subject, outlet) -> cortex.pipe(x -> transform(x), outlet)`
    ///   - Name-aware: `(subject, outlet) -> cortex.pipe(x -> process(subject.name(), x), outlet)`
    ///
    /// - **egress**: Transforms output before upward routing with subject context. Defines how child
    ///   emissions are processed before flowing to parent. Common patterns:
    ///   - Identity: `(subject, pipe) -> pipe` (forward as-is)
    ///   - Accumulation: `(subject, outlet) -> cortex.pipe(x -> runningTotal += x, outlet)`
    ///   - Filtering: `(subject, outlet) -> cortex.pipe(x -> x > threshold ? x : null, outlet)`
    ///
    /// - **receptor**: Receptor for aggregated emissions flowing upward from this cell and its children.
    ///   Since [Receptor] is a functional interface, a lambda can be passed directly:
    ///   `circuit.cell(ingress, egress, value -> process(value))`
    ///
    /// ## Stack Safety
    ///
    /// Upward flow uses async dispatch through hub subscriptions, enabling unlimited
    /// hierarchy depth without stack overflow. Deep structures (100+ levels) are
    /// supported efficiently.
    ///
    /// @param ingress  function creating input pipe from output pipe
    /// @param egress   function transforming output before upward routing
    /// @param receptor receptor for aggregated upward emissions
    /// @param <I>      the class type of input values received by the cell
    /// @param <E>      the class type of output values emitted by the cell
    /// @return A new cell with the circuit's name
    /// @throws NullPointerException if any parameter is `null`
    /// @see Cell
    /// @see Receptor
    /// @see #cell(Name, Composer, Composer, Receptor)

    @New
    @NotNull
    default < I, E > Cell < I, E > cell (
      @NotNull final Composer < E, Pipe < I > > ingress,
      @NotNull final Composer < E, Pipe < E > > egress,
      @NotNull final Receptor < ? super E > receptor
    ) {

      return
        cell (
          subject ().name (),
          ingress,
          egress,
          receptor
        );

    }


    /// Creates a hierarchical cell with specified name.
    ///
    /// Cells enable arbitrarily deep computational hierarchies with stack-safe upward flow.
    /// Each cell combines input processing (ingress), output transformation (egress),
    /// and child cell management into a unified structure.
    ///
    /// ## Parameters
    ///
    /// - **name**: Subject name for this cell, used for identification and debugging
    ///
    /// - **ingress**: Creates input pipe from output pipe with subject context. Defines how emissions
    ///   flow downward through the cell hierarchy. Common patterns:
    ///   - Identity: `(subject, pipe) -> pipe` (pass through unchanged)
    ///   - Transformation: `(subject, outlet) -> cortex.pipe(x -> transform(x), outlet)`
    ///   - Name-aware: `(subject, outlet) -> cortex.pipe(x -> process(subject.name(), x), outlet)`
    ///
    /// - **egress**: Transforms output before upward routing with subject context. Defines how child
    ///   emissions are processed before flowing to parent. Common patterns:
    ///   - Identity: `(subject, pipe) -> pipe` (forward as-is)
    ///   - Accumulation: `(subject, outlet) -> cortex.pipe(x -> runningTotal += x, outlet)`
    ///   - Filtering: `(subject, outlet) -> cortex.pipe(x -> x > threshold ? x : null, outlet)`
    ///
    /// - **receptor**: Receptor for aggregated emissions flowing upward from this cell and its children.
    ///   Since [Receptor] is a functional interface, a lambda can be passed directly:
    ///   `circuit.cell(name, ingress, egress, value -> process(value))`
    ///
    /// ## Stack Safety
    ///
    /// Upward flow uses async dispatch through hub subscriptions, enabling unlimited
    /// hierarchy depth without stack overflow. Deep structures (100+ levels) are
    /// supported efficiently.
    ///
    /// @param name     the name assigned to the cell's subject
    /// @param ingress  function creating input pipe from output pipe
    /// @param egress   function transforming output before upward routing
    /// @param receptor receptor for aggregated upward emissions
    /// @param <I>      the class type of input values received by the cell
    /// @param <E>      the class type of output values emitted by the cell
    /// @return A new cell with the specified name
    /// @throws NullPointerException if any parameter is `null`
    /// @throws Exception            if the name parameter is not a runtime-provided implementation
    /// @see Cell
    /// @see Receptor
    /// @see #cell(Composer, Composer, Receptor)

    @New
    @NotNull
    < I, E > Cell < I, E > cell (
      @NotNull Name name,
      @NotNull final Composer < E, Pipe < I > > ingress,
      @NotNull final Composer < E, Pipe < E > > egress,
      @NotNull Receptor < ? super E > receptor
    );


    /// Closes the circuit, releasing its processing thread and associated resources.
    ///
    /// ## Shutdown Process
    ///
    /// Closing a circuit initiates shutdown by:
    /// 1. Marking the circuit as closed (idempotent flag)
    /// 2. Signaling the processing thread to terminate
    /// 3. Allowing queued emissions to drain (implementation-specific)
    /// 4. Releasing conduits and their channels
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
    /// - New emissions may be rejected (implementation-specific)
    /// - [#conduit(Name, Composer)] may throw [IllegalStateException]
    /// - Repeated close() calls are safe (idempotent)
    ///
    /// ## Thread Safety
    ///
    /// Thread-safe - can be called concurrently from multiple threads. Only the first
    /// invocation performs shutdown; subsequent calls are no-ops.
    ///
    /// @see #await()

    @Queued
    @Idempotent
    @Override
    void close ();


    /// Returns a conduit that will use this circuit to process and transfer values emitted.
    ///
    /// Convenience method that uses this circuit's subject name for the conduit. For an
    /// explicit conduit name, use [#conduit(Name, Composer)].
    ///
    /// @param composer a function that composes percepts around a channel
    /// @param <P>      the class type of the percept (must extend Percept)
    /// @param <E>      the class type of emitted values
    /// @return A conduit with the circuit's name, using this circuit for emission processing
    /// @throws NullPointerException if the specified composer is `null`
    /// @see Conduit
    /// @see Composer
    /// @see Channel
    /// @see #conduit(Name, Composer)

    @New
    @NotNull
    default < P extends Percept, E > Conduit < P, E > conduit (
      @NotNull final Composer < E, ? extends P > composer
    ) {

      requireNonNull ( composer );

      return conduit (
        subject ().name (),
        composer
      );

    }


    /// Returns a conduit that will use this circuit to process and transfer values emitted.
    ///
    /// Convenience method that uses this circuit's subject name for the conduit. For an
    /// explicit conduit name, use [#conduit(Name, Composer, Configurer)].
    ///
    /// The configurer is invoked eagerly during this call. Any exceptions thrown by
    /// [Flow] or [Sift] methods are wrapped in an [Exception] and propagated to the caller.
    ///
    /// @param composer   the function that composes percepts around a channel
    /// @param configurer the consumer responsible for configuring the flow in the conduit
    /// @param <P>        the class type of the percept (must extend Percept)
    /// @param <E>        the class type of emitted values
    /// @return A conduit with the circuit's name, using this circuit for emission processing
    /// @throws NullPointerException if the specified composer or configurer is `null`
    /// @throws Exception            if the configurer throws
    /// @see Conduit
    /// @see Composer
    /// @see Flow
    /// @see #conduit(Name, Composer, Configurer)

    @New
    @NotNull
    default < P extends Percept, E > Conduit < P, E > conduit (
      @NotNull final Composer < E, ? extends P > composer,
      @NotNull final Configurer < ? super Flow < E > > configurer
    ) {

      requireNonNull ( composer );
      requireNonNull ( configurer );

      return conduit (
        subject ().name (),
        composer,
        configurer
      );

    }


    /// Returns a conduit that will use this circuit to process and transfer values emitted.
    ///
    /// @param name     the name given to the conduit's subject
    /// @param composer the function that composes percepts around a channel
    /// @param <P>      the class type of the percept (must extend Percept)
    /// @param <E>      the class type of emitted values
    /// @return A conduit using this circuit for emission processing
    /// @throws NullPointerException if the specified name or composer is `null`
    /// @throws Exception            if the name parameter is not a runtime-provided implementation

    @New
    @NotNull
    < P extends Percept, E > Conduit < P, E > conduit (
      @NotNull Name name,
      @NotNull Composer < E, ? extends P > composer
    );


    /// Returns a conduit that will use this circuit to process and transfer values emitted.
    ///
    /// The configurer is invoked eagerly during this call. Any exceptions thrown by
    /// [Flow] or [Sift] methods are wrapped in an [Exception] and propagated to the caller.
    ///
    /// @param name       the name given to the conduit's subject
    /// @param composer   the function that composes percepts around a channel
    /// @param configurer the consumer responsible for configuring the flow in the conduit
    /// @param <P>        the class type of the percept (must extend Percept)
    /// @param <E>        the class type of emitted values
    /// @return A conduit using this circuit for emission processing
    /// @throws NullPointerException if the specified name, composer, or configurer is `null`
    /// @throws Exception            if the name parameter is not a runtime-provided implementation, or the configurer throws

    @New
    @NotNull
    < P extends Percept, E > Conduit < P, E > conduit (
      @NotNull Name name,
      @NotNull Composer < E, ? extends P > composer,
      @NotNull Configurer < ? super Flow < E > > configurer
    );


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
    /// @throws Exception            if the target parameter is not a runtime-provided implementation
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

    @New
    @NotNull
    < E > Pipe < E > pipe (
      @NotNull Receptor < ? super E > receptor
    );


    /// Returns a pipe that applies Flow transformations before asynchronously
    /// dispatching emissions to the target pipe via this circuit's event queue.
    ///
    /// This method extends the framework's extensibility mechanism to circuit-level
    /// async pipes, providing the same Flow configuration capabilities available in
    /// [Channel#pipe(Configurer)]. The Flow operations (diff, guard, limit, sample,
    /// sift, etc.) execute on the circuit's worker thread after emissions are dequeued
    /// for async dispatch.
    ///
    /// Flow provides type-preserving operations with stateful lifecycle management,
    /// including filtering, deduplication, backpressure, and aggregation. Extensible
    /// with custom operators.
    ///
    /// Threading note: Flow operations execute on the circuit's worker thread, providing
    /// single-threaded execution guarantees. This enables stateful operators (diff, limit,
    /// reduce, etc.) to use mutable state without synchronization, simplifying operator
    /// implementation and eliminating concurrency issues. The caller's thread only enqueues
    /// the emission to the circuit's queue, then returns immediately (non-blocking async).
    ///
    /// The configurer is invoked eagerly during this call. Any exceptions thrown by
    /// [Flow] or [Sift] methods are wrapped in an [Exception] and propagated to the caller.
    ///
    /// @param target     the pipe that will receive emissions asynchronously
    /// @param configurer the configurer responsible for configuring the flow pipeline
    /// @param <E>        the class type of emitted values
    /// @return A pipe that applies Flow transformations then asynchronously dispatches to the target pipe
    /// @throws NullPointerException if the target or configurer is `null`
    /// @throws Exception            if the target parameter is not a runtime-provided implementation, or the configurer throws
    /// @see Pipe
    /// @see Flow
    /// @see Channel#pipe(Configurer)

    @New
    @NotNull
    < E > Pipe < E > pipe (
      @NotNull Pipe < ? super E > target,
      @NotNull Configurer < ? super Flow < E > > configurer
    );


    /// Returns a pipe that applies Flow transformations before asynchronously
    /// dispatching emissions to a receptor via this circuit's event queue.
    ///
    /// The configurer is invoked eagerly during this call. Any exceptions thrown by
    /// [Flow] or [Sift] methods are wrapped in an [Exception] and propagated to the caller.
    ///
    /// @param receptor   the receptor that will receive emissions asynchronously
    /// @param configurer the configurer responsible for configuring the flow pipeline
    /// @param <E>        the class type of emitted values
    /// @return A pipe that applies Flow transformations then asynchronously dispatches to the receptor
    /// @throws NullPointerException if the receptor or configurer is `null`
    /// @throws Exception            if the configurer throws
    /// @see Receptor
    /// @see Flow

    @New
    @NotNull
    < E > Pipe < E > pipe (
      @NotNull Receptor < ? super E > receptor,
      @NotNull Configurer < ? super Flow < E > > configurer
    );


    /// Returns a pipe with a custom name that asynchronously dispatches emissions to the target pipe
    /// via this circuit's event queue.
    ///
    /// If the target pipe already belongs to this circuit, it is returned as-is
    /// without wrapping — the name parameter is ignored in this case.
    ///
    /// @param name   the name for this pipe's subject
    /// @param target the pipe that will receive emissions asynchronously
    /// @param <E>    the class type of emitted values
    /// @return A pipe with the specified name that asynchronously dispatches to the target pipe, or the target itself if already on this circuit
    /// @throws NullPointerException if the name or target is `null`
    /// @throws Exception            if the name or target parameter is not a runtime-provided implementation
    /// @see Pipe
    /// @see #pipe(Pipe)

    @New ( conditional = true )
    @NotNull
    < E > Pipe < E > pipe (
      @NotNull Name name,
      @NotNull Pipe < E > target
    );


    /// Returns a pipe with a custom name that asynchronously dispatches emissions to a receptor
    /// via this circuit's event queue.
    ///
    /// @param name     the name for this pipe's subject
    /// @param receptor the receptor that will receive emissions asynchronously
    /// @param <E>      the class type of emitted values
    /// @return A pipe with the specified name that asynchronously dispatches to the receptor
    /// @throws NullPointerException if the name or receptor is `null`
    /// @throws Exception            if the name parameter is not a runtime-provided implementation
    /// @see Receptor
    /// @see #pipe(Receptor)

    @New
    @NotNull
    < E > Pipe < E > pipe (
      @NotNull Name name,
      @NotNull Receptor < ? super E > receptor
    );


    /// Returns a pipe with a custom name that applies Flow transformations before asynchronously
    /// dispatching emissions to the target pipe via this circuit's event queue.
    ///
    /// The configurer is invoked eagerly during this call. Any exceptions thrown by
    /// [Flow] or [Sift] methods are wrapped in an [Exception] and propagated to the caller.
    ///
    /// @param name       the name for this pipe's subject
    /// @param target     the pipe that will receive emissions asynchronously
    /// @param configurer the configurer responsible for configuring the flow pipeline
    /// @param <E>        the class type of emitted values
    /// @return A pipe with the specified name that applies Flow transformations then asynchronously dispatches
    /// @throws NullPointerException if any parameter is `null`
    /// @throws Exception            if the name or target parameter is not a runtime-provided implementation, or the configurer throws
    /// @see Pipe
    /// @see Flow
    /// @see #pipe(Pipe, Configurer)

    @New
    @NotNull
    < E > Pipe < E > pipe (
      @NotNull Name name,
      @NotNull Pipe < ? super E > target,
      @NotNull Configurer < ? super Flow < E > > configurer
    );


    /// Returns a pipe with a custom name that applies Flow transformations before asynchronously
    /// dispatching emissions to a receptor via this circuit's event queue.
    ///
    /// The configurer is invoked eagerly during this call. Any exceptions thrown by
    /// [Flow] or [Sift] methods are wrapped in an [Exception] and propagated to the caller.
    ///
    /// @param name       the name for this pipe's subject
    /// @param receptor   the receptor that will receive emissions asynchronously
    /// @param configurer the configurer responsible for configuring the flow pipeline
    /// @param <E>        the class type of emitted values
    /// @return A pipe with the specified name that applies Flow transformations then asynchronously dispatches
    /// @throws NullPointerException if any parameter is `null`
    /// @throws Exception            if the name parameter is not a runtime-provided implementation, or the configurer throws
    /// @see Receptor
    /// @see Flow
    /// @see #pipe(Receptor, Configurer)

    @New
    @NotNull
    < E > Pipe < E > pipe (
      @NotNull Name name,
      @NotNull Receptor < ? super E > receptor,
      @NotNull Configurer < ? super Flow < E > > configurer
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
    /// @param <E>        the emission type
    /// @param name       the name to be used by the subject assigned to the subscriber
    /// @param subscriber the subscribing behavior to be applied when a channel receives its first emission after subscription
    /// @return A new subscriber scoped to this circuit
    /// @throws NullPointerException if the name or subscriber params are `null`
    /// @throws Exception            if the name parameter is not a runtime-provided implementation
    /// @see Source#subscribe(Subscriber)
    /// @see Registrar#register(Pipe)

    @New
    @NotNull
    < E > Subscriber < E > subscriber (
      @NotNull Name name,
      @NotNull BiConsumer < ? super Subject < Channel < E > >, ? super Registrar < E > > subscriber
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

  /// A function that composes a percept from a channel.
  ///
  /// Composers are the core mechanism for creating domain-specific abstractions (percepts)
  /// around channels in a conduit. When a conduit creates a new channel, it passes the
  /// channel to the composer, which wraps it in a percept that provides domain-specific
  /// operations while delegating emissions to the underlying channel's pipe.
  ///
  /// ## Framework Contract
  ///
  /// The framework guarantees that:
  /// - The channel parameter is **never null**
  /// - The composer is called **exactly once per channel** (channels are pooled by name)
  /// - The composer must return a **non-null percept**
  ///
  /// ## Type Parameter Ordering
  ///
  /// Composer uses `<E, P>` ordering (emission type first, percept type second) to reflect
  /// the transformation direction: `Channel<E> → P`. This follows the functional convention
  /// where input types precede output types. Note that [Conduit] uses the inverse ordering
  /// `<P, E>` to emphasize the percept as the primary type parameter since conduits are
  /// primarily accessed as percept containers.
  ///
  /// ## Typical Pattern
  ///
  /// @param <E> the class type of values emitted through the channel
  /// @param <P> the class type of the composed percept (must extend Percept)
  /// @see Conduit
  /// @see Channel
  /// @see Circuit#conduit(Composer)
  /// @since 1.0

  @FunctionalInterface
  interface Composer < E, P extends Percept > {

    /// Returns a composer that returns a channel's pipe with flow configuration.
    ///
    /// @param <E>        the class type of emitted values
    /// @param configurer a consumer responsible for configuring a flow in the conduit
    /// @return A composer that returns a channel's pipe
    /// @throws NullPointerException if the specified configurer is `null`

    @NotNull
    @Utility
    static < E > Composer < E, Pipe < E > > pipe (
      @NotNull final Configurer < ? super Flow < E > > configurer
    ) {

      requireNonNull ( configurer );

      return channel ->
        channel.pipe (
          configurer
        );

    }


    /// Returns a composer that returns a channel's pipe
    ///
    /// @param <E>  the class type of emitted values
    /// @param type the class type of the emitted value
    /// @return A composer that returns a typed channel's pipe
    /// @throws NullPointerException if the specified type param is `null`

    @NotNull
    @Utility
    static < E > Composer < E, Pipe < E > > pipe (
      @NotNull final Class < E > type
    ) {

      requireNonNull ( type );

      return Channel::pipe;

    }


    /// Returns a composer that returns a channel's pipe
    ///
    /// @param <E> the class type of emitted values
    /// @return A composer that returns a channel's pipe

    @NotNull
    @Utility
    static < E > Composer < E, Pipe < E > > pipe () {

      return Channel::pipe;

    }


    /// Composes a percept from the provided channel.
    ///
    /// The implementation should wrap the channel (typically by calling [Channel#pipe()])
    /// and return a domain-specific object that provides operations appropriate for the
    /// percept type. The percept typically delegates emissions to the channel's pipe.
    ///
    /// This method is called **exactly once per channel name** by the conduit, as channels
    /// are pooled. The returned percept is cached and returned for all subsequent requests
    /// for the same channel name.
    ///
    /// @param channel the channel to compose around (guaranteed non-null)
    /// @return the composed percept (must not be null)
    /// @throws NullPointerException if the returned percept is null
    /// @throws Exception            if the channel parameter is not a runtime-provided implementation
    /// @see Channel#pipe()

    @NotNull
    P compose (
      @NotNull Channel < E > channel
    );


    /// Returns a composer that first applies this composer and then applies the after function.
    ///
    /// @param <R>   the type of output of the after function, and of the composed composer (must extend Percept)
    /// @param after the function to apply after this composer is applied
    /// @return a composed composer that first applies this function and then applies the after function
    /// @throws NullPointerException if after is null

    @NotNull
    default < R extends Percept > Composer < E, R > map (
      @NotNull final Function < ? super P, ? extends R > after
    ) {

      requireNonNull ( after );

      return channel ->
        after.apply (
          compose ( channel )
        );

    }

  }


  /// A factory for pooled percepts that emit events through named channels.
  ///
  /// Conduit combines two capabilities:
  /// - **Lookup**: Creates and caches percept instances by name
  /// - **Source**: Emits events and allows subscription to channel creation
  ///
  /// Conduits serve as the primary mechanism for creating domain-specific emission
  /// abstractions (sensors, monitors, probes) that route values through the circuit's
  /// processing engine.
  ///
  /// ## Percept Pattern
  ///
  /// A conduit creates a **percept** (P) by transforming each [Channel] via the composer function: `Channel<E> → P`.
  /// Common patterns: identity (return channel's pipe), wrapper (percept wraps pipe), or facade (custom API wrapping pipe).
  ///
  /// ## Lookup Semantics (via Lookup)
  ///
  /// Conduits provide name-based percept lookup with caching:
  /// - [Lookup#percept(Name)] retrieves or creates percept for the given name
  /// - [Lookup#percept(Subject)] convenience method extracts name from subject
  /// - [Lookup#percept(Substrate)] convenience method extracts name from substrate
  /// - Same name always returns the same percept instance (cached)
  /// - Different names create different percepts with separate channels
  /// - Each percept has its own unique channel and subject
  ///
  /// **Identity guarantee**: Percepts with the same name are identical objects.
  ///
  /// Lookup enables efficient reuse - calling `conduit.percept(name)` multiple
  /// times returns the cached instance, avoiding repeated allocations. This identity
  /// guarantee also ensures stable routing - all emissions for a given percept name
  /// flow through the same channel to the same subscribers.
  ///
  /// ## Lazy Subscriber Callbacks (via Source)
  ///
  /// Subscriber callbacks are invoked lazily during emission processing:
  /// - First call to [Lookup#percept(Name)] creates new channel (no callbacks invoked)
  /// - First emission to that channel triggers rebuild on circuit thread
  /// - During rebuild, subscriber callback is invoked for newly encountered channels
  /// - Subscriber receives [Subject] of the channel and [Registrar] to attach pipes
  /// - No events of type `E` are emitted - callback provides registration mechanism
  ///
  /// This lazy callback model enables dynamic discovery - subscribers can attach pipes
  /// to channels on-demand without knowing channel names in advance, and without overhead
  /// until emissions actually occur.
  ///
  /// **Thread safety**: Subscriber callbacks always execute on the circuit thread,
  /// providing single-threaded guarantees. This means callbacks can safely use mutable
  /// state, access shared data structures, and perform stateful operations without any
  /// synchronization, locks, or atomic operations.
  ///
  /// ## Subscription Model (via Source)
  ///
  /// Subscribe to be notified of channels when they receive their first emission:
  ///
  /// ## Threading Model
  ///
  /// Conduit operations are split between caller thread and circuit thread:
  ///
  /// **Caller thread** (synchronous):
  /// - [Lookup#percept(Name)] creates and caches channels immediately (thread-safe)
  /// - `subscribe(subscriber)` enqueues registration to circuit thread
  ///
  /// **Circuit thread** (asynchronous):
  /// - Subscriber registration completes and increments version
  /// - Emissions trigger lazy rebuild when version mismatch detected
  /// - Rebuild invokes subscriber callbacks for newly encountered channels
  /// - Emissions dispatched to registered pipes
  ///
  /// This model allows [Lookup#percept(Name)] to be called from any thread without blocking,
  /// while ensuring all subscriber callbacks execute single-threaded on the circuit.
  ///
  /// ## Lifecycle
  ///
  /// Conduits are created via [Circuit#conduit(Composer)]:
  /// 1. **Created**: Circuit creates conduit with composer function
  /// 2. **Active**: Percepts created on-demand via `percept()`
  /// 3. **Closed**: When circuit closes, conduit is also closed
  ///
  /// Conduits inherit the circuit's lifecycle - when the circuit closes, all conduits
  /// close automatically.
  ///
  /// ## Use Cases
  ///
  /// Conduits excel at:
  /// - **Domain modeling**: Create typed abstractions (Sensor, Monitor, Metric)
  /// - **Name-based routing**: Cache instances by logical names
  /// - **Dynamic discovery**: Subscribe to new channels as they're created
  /// - **API hiding**: Provide domain API while hiding raw channels
  /// - **Instrumentation**: Automatically attach observers to all channels
  ///
  /// ## Type Parameter Ordering
  ///
  /// Conduit uses `<P, E>` ordering (percept type first, emission type second) to emphasize
  /// the percept as the primary type parameter, since conduits are primarily accessed as
  /// percept containers via [Lookup#percept(Name)]. Note that [Composer] uses the inverse
  /// ordering `<E, P>` to reflect the transformation direction (`Channel<E> → P`), following
  /// the functional convention where input types precede output types.
  ///
  /// @param <P> the class type of the percept (must extend Percept)
  /// @param <E> the class type of emitted values (what flows through channels)
  /// @see Channel
  /// @see Lookup
  /// @see Source
  /// @see Circuit#conduit(Composer)
  /// @see Subscriber

  @Tenure ( Tenure.EPHEMERAL )
  @Provided
  non-sealed interface Conduit < P extends Percept, E >
    extends Lookup < P >,
            Source < E, Conduit < P, E > > {

    /// Creates a tap into this conduit with transformed emissions.
    ///
    /// A Tap follows the structure of this conduit - channels and subject
    /// hierarchy - but transforms emissions from type E to type T. Channels
    /// are created automatically as subjects appear in this conduit.
    ///
    /// ## Usage Pattern
    ///
    /// ```java
    /// // Create tap that transforms signals to names
    /// Tap<Name> names = conduit.tap(
    ///   signal -> name(signal.sign(), signal.dimension())
    /// );
    ///
    /// // Subscribe to the tap
    /// names.subscribe(subscriber);
    ///
    /// // Later, clean up
    /// names.close();
    /// ```
    ///
    /// ## Threading Guarantee
    ///
    /// The mapper function is always invoked on the circuit's single worker thread.
    /// This means stateful mappers are safe to use without synchronization.
    ///
    /// ## Null Filtering
    ///
    /// If the mapper returns `null`, the emission is **filtered out** (dropped)
    /// rather than forwarded downstream. This allows the mapper to serve as a
    /// combined transform-and-filter operation.
    ///
    /// ## Resource Lifecycle
    ///
    /// The returned Tap extends [Resource] and should be closed when no longer
    /// needed to unsubscribe from this conduit and release mirrored channels.
    ///
    /// @param <T>    the transformed emission type
    /// @param mapper the function to transform emissions from E to T; returns `null` to filter
    /// @return a new Tap that mirrors this conduit with transformed emissions
    /// @throws NullPointerException if the mapper is `null`
    /// @see Tap

    @New
    @NotNull
    < T > Tap < T > tap (
      @NotNull Function < ? super E, ? extends T > mapper
    );


    /// Creates a tap into this conduit with transformed emissions and flow configuration.
    ///
    /// This method extends [#tap(Function)] by allowing a [Flow] pipeline to be configured
    /// on the transformed emissions. The flow operations (diff, guard, limit, sample, etc.)
    /// execute on the circuit's worker thread after the mapper transforms each emission.
    ///
    /// ## Usage Pattern
    ///
    /// ```java
    /// // Create tap with transformation and deduplication
    /// Tap<Name> names = conduit.tap(
    ///   signal -> name(signal.sign(), signal.dimension()),
    ///   flow -> flow.diff().limit(1000)
    /// );
    ///
    /// // Subscribe to receive deduplicated transformed emissions
    /// names.subscribe(subscriber);
    ///
    /// // Later, clean up
    /// names.close();
    /// ```
    ///
    /// ## Threading Guarantee
    ///
    /// Both the mapper function and flow operations execute on the circuit's single
    /// worker thread. This means stateful mappers and flow operators are safe to use
    /// without synchronization.
    ///
    /// ## Null Filtering
    ///
    /// If the mapper returns `null`, the emission is **filtered out** (dropped)
    /// before reaching the flow pipeline. This allows the mapper to serve as a
    /// combined transform-and-filter operation, complementing the flow's own
    /// filtering capabilities (guard, diff, etc.).
    ///
    /// ## Resource Lifecycle
    ///
    /// The returned Tap extends [Resource] and should be closed when no longer
    /// needed to unsubscribe from this conduit and release mirrored channels.
    ///
    /// The configurer is invoked eagerly during this call. Any exceptions thrown by
    /// [Flow] or [Sift] methods are wrapped in an [Exception] and propagated to the caller.
    ///
    /// @param <T>        the transformed emission type
    /// @param mapper     the function to transform emissions from E to T; returns `null` to filter
    /// @param configurer the configurer responsible for configuring the flow pipeline
    /// @return a new Tap that mirrors this conduit with transformed and filtered emissions
    /// @throws NullPointerException if the mapper or configurer is `null`
    /// @throws Exception            if the configurer throws
    /// @see Tap
    /// @see Flow
    /// @see #tap(Function)

    @New
    @NotNull
    < T > Tap < T > tap (
      @NotNull Function < ? super E, ? extends T > mapper,
      @NotNull Configurer < ? super Flow < T > > configurer
    );

  }


  /// A callback for configuring temporal objects during their initialization phase.
  ///
  /// Configurer is a generic functional interface for configuration callbacks where the
  /// target object has a limited lifetime. The target is only valid during the callback
  /// execution and must not be retained for later use.
  ///
  /// ## Temporal Contract
  ///
  /// **CRITICAL**: The target parameter is a temporal object with **callback-scoped lifetime**.
  /// It is provided during configuration and **MUST only be accessed during callback execution**.
  /// Storing or using the target after the callback returns results in undefined behavior.
  ///
  /// ## Common Usage
  ///
  /// Configurer is primarily used with [Flow] configuration:
  ///
  /// ```java
  /// // Configure a flow pipeline
  /// Pipe<Integer> pipe = circuit.pipe(
  ///   target,
  ///   flow -> flow
  ///     .guard(x -> x > 0)
  ///     .diff()
  ///     .limit(100)
  /// );
  /// ```
  ///
  /// ## Thread Safety
  ///
  /// Configurers execute on the caller's thread during object construction.
  /// The temporal constraint prevents sharing across threads by restricting
  /// usage to callback scope.
  ///
  /// @param <T> the type of temporal object to configure
  /// @author William David Louth
  /// @see Flow
  /// @see Temporal
  /// @since 1.0

  @FunctionalInterface
  interface Configurer < T > {

    /// Configures the temporal target object.
    ///
    /// This method is invoked exactly once during construction of the owning object.
    /// The target is only valid during this callback and must not be retained.
    ///
    /// ## Implementation Guidelines
    ///
    /// - Configure the target immediately; do not defer operations
    /// - Do not store the target reference for later use
    /// - Keep configuration fast and synchronous
    /// - Exceptions propagate to the caller
    ///
    /// @param target the temporal object to configure (valid only during this callback)

    void configure ( @NotNull @Temporal T target );

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
  ///               └── Channel, Subscription, etc. (depth=4)
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
    /// @throws Exception            if the name parameter is not a runtime-provided implementation
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


    /// Parses the supplied path into an interned name.
    /// The path uses `.` as the separator; empty segments are rejected and cached segments are reused.
    /// Paths must not begin or end with `.` nor contain consecutive separators (for example: `.foo`, `foo.`, `foo..bar`).
    ///
    /// @param path the string to be parsed into one or more name parts
    /// @return A name representing the parsed hierarchy
    /// @throws NullPointerException     if the path parameter is `null`
    /// @throws IllegalArgumentException if the path is empty, begins or ends with `.`, or contains consecutive separators (empty segments)

    @NotNull
    Name name (
      @NotNull String path
    );


    /// Creates a hierarchical name from the enum's declaring class followed by the constant's name.
    /// For example, `TimeUnit.SECONDS` produces a name equivalent to `"java.util.concurrent.TimeUnit.SECONDS"`.
    ///
    /// @param path the enum constant
    /// @return A hierarchical name representing the fully-qualified enum constant
    /// @throws NullPointerException if the path parameter is `null`

    @NotNull
    Name name (
      @NotNull Enum < ? > path
    );


    /// Returns an interned name from iterating over string values.
    /// Each value may contain dot-separated segments; at least one value must be provided.
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
    /// @param <T>    the type of each value iterated over
    /// @param it     the iterable to be iterated over
    /// @param mapper the function to be used to map the iterable value type to a string
    /// @return The constructed name
    /// @throws NullPointerException     if the iterable, mapper, or one of the mapped values is `null`
    /// @throws IllegalArgumentException if the iterable yields no values or an invalid path segment

    @NotNull
    < T > Name name (
      @NotNull Iterable < ? extends T > it,
      @NotNull Function < ? super T, String > mapper
    );


    /// Returns an interned name from iterating over string values.
    /// Each value may contain dot-separated segments; at least one value must be provided.
    ///
    /// @param it the [Iterator] to be iterated over
    /// @return The constructed name
    /// @throws NullPointerException     if the iterator is `null` or one of the values returned is `null`
    /// @throws IllegalArgumentException if the iterator yields no values or an invalid path segment
    /// @see #name(Iterable)

    @NotNull
    Name name (
      @NotNull Iterator < String > it
    );


    /// Returns an interned name from iterating over values mapped to strings.
    /// Each mapped value may contain dot-separated segments; at least one value must be provided.
    ///
    /// @param <T>    the type of each value iterated over
    /// @param it     the iterator to be iterated over
    /// @param mapper the function to be used to map the iterator value type to a string
    /// @return The constructed name
    /// @throws NullPointerException     if the iterator, mapper, or one of the mapped values is `null`
    /// @throws IllegalArgumentException if the iterator yields no values or an invalid path segment
    /// @see #name(Iterable, Function)

    @NotNull
    < T > Name name (
      @NotNull Iterator < ? extends T > it,
      @NotNull Function < ? super T, String > mapper
    );


    /// Creates an interned name from a class.
    /// The package and enclosing class simple names are joined with dots (matching [Class#getCanonicalName()]
    /// when available); local or anonymous classes fall back to the runtime name.
    ///
    /// @param type the class to be mapped to a name
    /// @return A name whose string representation matches `type.getCanonicalName()` when available, otherwise `type.getName()`
    /// @throws NullPointerException if the type param is `null`

    @NotNull
    Name name (
      @NotNull Class < ? > type
    );


    /// Creates an interned name from a member.
    /// The declaring class hierarchy is extended with the member name.
    ///
    /// @param member the member to be mapped to a name
    /// @return A name mapped to the member
    /// @throws NullPointerException if the member param is `null`

    @NotNull
    Name name (
      @NotNull Member member
    );


    /// Returns a new scope instance with the specified name for managing resources.
    ///
    /// Scopes provide hierarchical resource lifecycle management. When a scope is closed,
    /// all resources registered with it (via [Scope#register(Resource)]) are automatically
    /// closed. Child scopes can be created from parent scopes, forming a tree structure.
    ///
    /// @param name the name assigned to the scope's subject
    /// @return A new scope
    /// @throws NullPointerException if the name param is `null`
    /// @throws Exception            if the name parameter is not a runtime-provided implementation
    /// @see Scope#register(Resource)
    /// @see Scope#close()

    @New
    @NotNull
    Scope scope (
      @NotNull Name name
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


    /// Creates a boolean slot.
    ///
    /// @param name  slot name
    /// @param value slot value
    /// @return A boolean slot
    /// @throws NullPointerException if the name param is `null`
    /// @throws Exception            if the name parameter is not a runtime-provided implementation

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
    /// @throws NullPointerException if the name param is `null`
    /// @throws Exception            if the name parameter is not a runtime-provided implementation

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
    /// @throws NullPointerException if the name param is `null`
    /// @throws Exception            if the name parameter is not a runtime-provided implementation

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
    /// @throws NullPointerException if the name param is `null`
    /// @throws Exception            if the name parameter is not a runtime-provided implementation

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
    /// @throws NullPointerException if the name param is `null`
    /// @throws Exception            if the name parameter is not a runtime-provided implementation

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
    /// @throws NullPointerException if name or value params are `null`
    /// @throws Exception            if the name parameter is not a runtime-provided implementation

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
    /// @throws NullPointerException if name or value params are `null`
    /// @throws Exception            if the name or value parameters are not runtime-provided implementations

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
    /// @throws NullPointerException if name or value params are `null`
    /// @throws Exception            if the name or value parameters are not runtime-provided implementations

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
    /// @throws NullPointerException if the other parameter is `null`
    /// @throws Exception            if the other parameter is not a runtime-provided implementation

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
    /// @throws NullPointerException if either initializer or accumulator params are `null`

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
    /// @throws NullPointerException if either initializer or accumulator params are `null`

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
    /// @throws NullPointerException if separator param is `null`

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
    /// @throws NullPointerException if mapper param is `null`

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
    /// @throws NullPointerException if either mapper or separator params are `null`

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
    /// @throws NullPointerException if `enclosure` param is `null`

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

  /// Represents a configurable processing pipeline for data transformation.
  ///
  /// Flow operations create a chain of transformations that run between a channel and
  /// the subscribers attached to that channel. Flows are configured via method chaining,
  /// with each operator returning a new Flow representing the extended pipeline.
  ///
  /// ## Execution Context
  ///
  /// Every flow stage executes on the circuit thread that owns the conduit. Emissions are
  /// processed sequentially, so each stage observes values in deterministic order.
  ///
  /// **Circuit-thread execution guarantee**: All flow operations execute exclusively on the
  /// circuit thread. This means:
  ///
  /// - Flow operators execute sequentially for each emission
  /// - No concurrent execution of flow stages for a given channel
  /// - State within flow operators requires no synchronization
  /// - Deterministic ordering of emissions through the pipeline
  ///
  /// **User-provided functions**: All functions, predicates, pipes, and operators passed to
  /// Flow methods (e.g., `guard(Predicate)`, `replace(Function)`, `peek(Receptor)`) will be
  /// executed on the circuit's processing thread:
  ///
  /// - **Thread-safe execution** - No concurrent invocation of your lambdas/functions
  /// - **Sequential processing** - Each emission processes through your functions one at a time
  /// - **No synchronization needed** - For local state within the function
  ///
  /// **CRITICAL - External state access**: While your functions execute thread-safely on the
  /// circuit thread, **you must ensure thread-safe access to any external shared mutable state**
  /// they reference. If your functions access fields, collections, or other state shared across
  /// threads, you are responsible for proper synchronization.
  ///
  /// ## State Management
  ///
  /// Flow operators fall into two categories based on state requirements:
  ///
  /// **Stateful operators** (maintain internal state across emissions):
  /// - **diff()**: Tracks last emitted value for duplicate detection
  /// - **guard(Object, BiPredicate)**: Tracks last value for comparison
  /// - **limit()**: Counts emissions to enforce limit
  /// - **reduce()**: Maintains accumulator state
  /// - **sample()**: Counts emissions for interval-based sampling
  /// - **sift()**: Maintains complex filtering state
  /// - **skip()**: Counts emissions to skip initial values
  ///
  /// **Stateless operators** (process each emission independently):
  /// - **guard(Predicate)**: Evaluates predicate without state
  /// - **peek()**: Observes emissions without modification
  /// - **replace()**: Transforms each value independently
  ///
  /// Stateful operators store mutable state without external synchronization since they
  /// execute exclusively on the circuit thread. Each flow maintains its own state instance,
  /// isolated from other flows.
  ///
  /// **State isolation guarantee**: Flow state is private to the flow instance. Multiple
  /// channels with separate flow configurations maintain independent state. Two channels
  /// with the same flow configuration (e.g., both using `diff()`) maintain separate state
  /// instances - changes in one channel's state do not affect the other.
  ///
  /// ## Composition and Chaining
  ///
  /// Flow operations are **composable** via method chaining:
  ///
  /// Each method returns a new Flow instance representing the pipeline extended with
  /// that operator. The order of operators matters - they execute left-to-right in
  /// the order chained.
  ///
  /// **Builder pattern**: Flow uses the builder pattern (fluent interface) for pipeline
  /// construction. Most methods return `this` or a new Flow instance, allowing continued
  /// chaining.
  ///
  /// ## Operator Ordering
  ///
  /// The order of flow operators affects both **correctness** and **performance**:
  ///
  /// **Correctness**: Some operator orders change semantics:
  ///
  /// **Performance**: Place cheap filters early to reduce downstream work:
  ///
  /// ## Performance Guidance
  ///
  /// Flow pipelines run on the circuit thread and therefore contribute directly to emission
  /// latency. Keep pipelines concise and avoid expensive computations.
  ///
  /// **Performance tips**:
  ///
  /// - Avoid allocations in flow operators (especially in hot paths)
  /// - Keep flow pipelines short - each stage adds overhead
  /// - Stateful operators (diff, guard, reduce) are more expensive than stateless (replace, peek)
  /// - Place cheap filters early to reduce downstream processing
  /// - Consider batching or buffering for expensive operations
  ///
  /// ## Lifecycle and Builder Pattern
  ///
  /// Flow instances follow a **builder-like pattern** for pipeline configuration:
  /// - Methods support fluent chaining via `@Fluent` returning Flow instances
  /// - Configuration builds up a pipeline of transformation stages
  /// - The Flow is materialized into an actual pipe chain when passed to downstream components
  /// - Execution state (for stateful operators) is created during materialization,
  ///   not during Flow configuration
  ///
  /// **Implementation note**: Flow instances may be mutable builders (accumulating
  /// pipeline stages) or immutable functional builders (returning new instances).
  /// The interface does not guarantee either approach - implementations are free
  /// to optimize as needed.
  ///
  /// **Thread safety**: Flow instances are **not guaranteed thread-safe**. However,
  /// the `@Temporal` constraint (see below) restricts Flow usage to callback scope,
  /// which naturally prevents concurrent access and sharing across threads.
  ///
  /// ## Temporal Constraint
  ///
  /// **CRITICAL**: Flow instances are temporal objects with **callback-scoped lifetime**.
  /// They are provided to configuration callbacks and **MUST only be accessed during
  /// callback execution**. Using a Flow instance after its callback returns results in
  /// undefined behavior.
  ///
  /// **VALID usage** - Flow used only within callback scope:
  ///
  /// **INVALID usage** - Flow stored or escaped from callback:
  ///
  /// **Why this matters**: The Flow interface serves as a **configuration builder** during
  /// pipe construction. The implementation may reuse Flow instances across multiple callbacks
  /// (object pooling), or may become invalid after materialization into the actual pipe chain.
  /// Storing Flow references breaks these implementation optimizations and leads to subtle bugs.
  ///
  /// **Design intent**: This temporal constraint enables:
  /// - **Object pooling** - Flow builders can be pooled and reused for reduced allocation
  /// - **Clear lifecycle** - Separation between configuration phase and execution phase
  /// - **Thread safety** - Callback scope prevents sharing across threads (no concurrent access)
  /// - **Mutable optimization** - Implementations can use mutable builders without synchronization
  /// - **Compile-time contract** - The `@Temporal` annotation documents lifecycle at the type level
  ///
  /// By restricting Flow to callback scope, the temporal constraint **solves the sharing problem**:
  /// you cannot accidentally share a Flow across threads because you cannot store or escape it
  /// from the callback. This allows implementations to use efficient mutable builders without
  /// any thread-safety concerns.
  ///
  /// Flow instances provided to `Channel.pipe(Configurer)` callbacks, `Circuit.conduit(..., Configurer)`
  /// callbacks, and similar configuration methods are **all subject to this constraint**.
  ///
  /// @param <E> the class type of emitted values
  /// @see Channel#pipe(Configurer)
  /// @see Circuit#pipe(Pipe, Configurer)
  /// @see Configurer
  /// @see Sift

  @Temporal
  @Provided
  interface Flow < E > {

    /// Returns a flow that emits values only when they differ from the previous emission.
    ///
    /// Emits only when values differ from the previous emission using equals() comparison.
    /// The first emission always passes through.
    ///
    /// Use case: Emit only on state changes.
    ///
    /// @return A stateful flow that filters consecutive duplicate values
    /// @see #diff(Object)

    @NotNull
    @Fluent
    Flow < E > diff ();


    /// Returns a flow that emits values only when they differ from the previous emission.
    ///
    /// Like [#diff()] but compares the first emission against the supplied initial value.
    ///
    /// @param initial the initial value to compare the first emission against
    /// @return A stateful flow that filters consecutive duplicate values
    /// @throws NullPointerException if the specified initial value is `null`
    /// @see #diff()

    @NotNull
    @Fluent
    Flow < E > diff (
      @NotNull E initial
    );


    /// Returns a flow that conditionally passes emissions based on a predicate.
    ///
    /// Emissions are forwarded only when the predicate returns `true`.
    /// Stateless - each emission is evaluated independently.
    ///
    /// Use case: Filter values by criteria.
    ///
    /// @param predicate the predicate that determines whether to pass each emission
    /// @return A flow that filters based on the predicate
    /// @throws NullPointerException if the specified predicate is `null`
    /// @see #guard(Object, BiPredicate)

    @NotNull
    @Fluent
    Flow < E > guard (
      @NotNull Predicate < ? super E > predicate
    );


    /// Returns a flow that conditionally passes emissions based on comparison with previous value.
    ///
    /// Maintains state tracking the last emitted value. Each emission is compared with the
    /// previous value (or initial for first emission) using the bi-predicate. Only emissions
    /// where the predicate returns `true` are forwarded and become the new previous value.
    ///
    /// Use case: Emit on threshold crossing or minimum delta change.
    ///
    /// @param initial   the initial value to compare the first emission against (maybe null)
    /// @param predicate the bi-predicate (previous, current) that determines passage
    /// @return A stateful flow that filters based on comparison with previous value
    /// @throws NullPointerException if the specified predicate is `null`
    /// @see #guard(Predicate)

    @NotNull
    @Fluent
    Flow < E > guard (
      E initial,
      @NotNull BiPredicate < ? super E, ? super E > predicate
    );


    /// Returns a flow that passes at most N emissions then blocks all subsequent values.
    ///
    /// Once the limit is reached, all further emissions are dropped.
    ///
    /// Use case: Cap total emissions, take first N values.
    ///
    /// @param limit the maximum number of emissions to pass through
    /// @return A stateful flow that enforces an emission limit
    /// @see #limit(long)

    @NotNull
    @Fluent
    Flow < E > limit (
      int limit
    );


    /// Returns a flow that passes at most N emissions then blocks all subsequent values.
    ///
    /// Once the limit is reached, all further emissions are dropped.
    ///
    /// Use case: Cap total emissions, take first N values.
    ///
    /// @param limit the maximum number of emissions to pass through
    /// @return A stateful flow that enforces an emission limit
    /// @see #limit(int)

    @NotNull
    @Fluent
    Flow < E > limit (
      long limit
    );


    /// Returns a flow that allows inspection of emissions without modifying them.
    ///
    /// The receptor is invoked on the circuit thread for each emission that reaches this
    /// point in the flow pipeline. This provides a thread-safe callback for observing
    /// emissions and synchronizing percept-local state without additional synchronization.
    ///
    /// ## Use Cases
    ///
    /// - **State synchronization**: Percepts can use peek to update internal state when
    ///   emissions pass through, knowing the callback executes on the circuit thread
    /// - **Logging/debugging**: Observe values at specific points in the pipeline
    /// - **Metrics**: Count or measure emissions without affecting downstream processing
    ///
    /// ## Flow Pipeline Position
    ///
    /// When using [Channel#pipe(Configurer)], the per-pipe flow (including peek) executes
    /// **before** the conduit-level flow. This means:
    ///
    /// - Peek sees values **before** conduit-level filtering
    /// - Values observed by peek may be filtered out by the conduit flow
    /// - To observe values after all filtering, place peek at the end of the conduit flow
    ///   configuration in [Circuit#conduit(Composer, Configurer)]
    ///
    /// @param receptor the receptor that will receive each emission passing through
    /// @return A flow with the peek operation added
    /// @throws NullPointerException if the specified receptor is `null`
    /// @see Channel#pipe(Configurer)
    /// @see Circuit#conduit(Composer, Configurer)

    @NotNull
    @Fluent
    Flow < E > peek (
      @NotNull Receptor < ? super E > receptor
    );


    /// Returns a flow that maintains a running accumulation of all emissions.
    ///
    /// Each emission is combined with the current accumulator using the binary operator.
    /// The result becomes both the new accumulator state and the emitted value.
    ///
    /// Use case: Running sum, concatenation, incremental merging.
    ///
    /// @param initial  the initial accumulator value (used before first emission)
    /// @param operator the binary operation (accumulator, emission) -> new accumulator
    /// @return A stateful flow that emits running accumulations
    /// @throws NullPointerException if the specified operator is `null`

    @NotNull
    @Fluent
    Flow < E > reduce (
      E initial,
      @NotNull BinaryOperator < E > operator
    );


    /// Returns a flow that transforms each emission using a mapping function.
    ///
    /// Stateless transformation - each emission is independently transformed by the unary operator.
    /// If the transformer returns `null`, the emission is **filtered out** (dropped) rather than
    /// forwarded downstream. This enables combined map-and-filter in a single operator.
    ///
    /// Use case: Convert units, normalize values, conditionally suppress emissions.
    ///
    /// @param transformer the unary operation that transforms each emission; returning `null` drops the emission
    /// @return A flow that maps values using the transformer
    /// @throws NullPointerException if the specified transformer is `null`

    @NotNull
    @Fluent
    Flow < E > replace (
      @NotNull UnaryOperator < E > transformer
    );


    /// Returns a flow that emits every Nth value, dropping others (interval-based sampling).
    ///
    /// Drops the first N-1 emissions, then emits the Nth. For example, `sample(3)` emits
    /// the 3rd, 6th, 9th values. A value of 1 is an identity (all emissions pass through).
    ///
    /// Use case: Periodic sampling of high-frequency signals.
    ///
    /// @param sample the interval - emit every Nth value (must be positive)
    /// @return A stateful flow that samples at regular intervals
    /// @throws IllegalArgumentException if the specified sample is not positive
    /// @see #sample(double)

    @NotNull
    @Fluent
    Flow < E > sample (
      int sample
    );


    /// Returns a flow that probabilistically passes emissions (probability-based sampling).
    ///
    /// For values strictly between 0.0 and 1.0, each emission passes with the specified
    /// probability. For example, `sample(0.1)` passes ~10% of emissions. The boundary values
    /// `0.0` and `1.0` are identity (all emissions pass through, no operator is added to the
    /// pipeline); only values strictly between 0.0 and 1.0 enable probabilistic filtering.
    ///
    /// Use case: Load shedding, random sampling.
    ///
    /// @param sample the probability (0.0 to 1.0) that each emission passes through
    /// @return A stateful flow that randomly samples emissions
    /// @throws IllegalArgumentException if the specified sample is not in the range [0.0,1.0]
    /// @see #sample(int)

    @NotNull
    @Fluent
    Flow < E > sample (
      double sample
    );


    /// Returns a flow that filters emissions based on range and extrema conditions.
    ///
    /// Delegates to the [Sift] sub-assembly for specialized filtering: range boundaries (above/below),
    /// extrema detection (high/low), and comparisons.
    ///
    /// Use case: Filter by range, detect new highs/lows.
    ///
    /// @param comparator the comparator defining value ordering for sift operations
    /// @param configurer the consumer that configures the sift sub-assembly
    /// @return A flow with sift-based filtering
    /// @throws NullPointerException if the specified comparator or configurer is `null`
    /// @see Sift

    @NotNull
    @Fluent
    Flow < E > sift (
      @NotNull Comparator < ? super E > comparator,
      @NotNull Configurer < ? super Sift < E > > configurer
    );


    /// Returns a flow that drops the first N emissions then passes all subsequent values.
    ///
    /// The first N emissions are dropped; all later emissions pass through unchanged. Opposite of [#limit].
    ///
    /// Use case: Ignore warm-up period, discard initial transients.
    ///
    /// @param n the number of initial emissions to skip (must be non-negative)
    /// @return A stateful flow that skips the first N emissions
    /// @throws IllegalArgumentException if the specified count is negative
    /// @see #limit(long)

    @NotNull
    @Fluent
    Flow < E > skip (
      long n
    );

  }

  /// Indicates a method that returns `this` instance for method chaining.

  @Documented
  @Retention ( SOURCE )
  @Target ( METHOD )
  @interface Fluent {
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


  /// A read-only interface for looking up percept instances by name.
  ///
  /// Lookup provides a simple retrieval abstraction for accessing percepts using:
  /// - **[Name]** - Direct lookup by name
  /// - **[Subject]** - Lookup using subject's name
  /// - **[Substrate]** - Lookup using substrate's subject name
  ///
  /// ## Type Constraint
  ///
  /// Lookup is specifically designed for [Percept] types, ensuring type safety
  /// and enforcing the semantic purpose of retrieving observable entities.
  ///
  /// ## Implementations
  ///
  /// Lookup is implemented by substrate components that provide percept retrieval:
  /// - **[Conduit]**: Looks up percepts using composer function `Channel<E> → P`
  /// - **[Cell]**: Looks up child cells by name
  ///
  /// ## Convenience Overloads
  ///
  /// Three ways to retrieve instances (all delegate to [#percept(Name)]):
  ///
  /// ```java
  /// // 1. By Name - Direct lookup
  /// Pipe<Integer> pipe = conduit.percept(name);
  ///
  /// // 2. By Subject - Extracts name from subject
  /// Pipe<Integer> pipe = conduit.percept(channel.subject());
  ///
  /// // 3. By Substrate - Extracts name from substrate's subject
  /// Pipe<Integer> pipe = conduit.percept(circuit);
  /// ```
  ///
  /// ## Thread Safety
  ///
  /// Lookup implementations must be thread-safe for concurrent percept retrieval.
  /// The specific caching and creation semantics depend on the implementation.
  ///
  /// @param <P> the percept type of instances (must extend [Percept])
  /// @see Percept
  /// @see Conduit
  /// @see Cell
  /// @see Name
  /// @since 1.0

  @Abstract
  interface Lookup < P extends Percept > {

    /// Returns the instance for the given substrate.
    ///
    /// Convenience method that extracts the Name from the substrate's subject
    /// and delegates to [#percept(Name)]. Equivalent to:
    /// `percept(substrate.subject().name())`
    ///
    /// @param substrate the substrate whose subject name identifies the instance
    /// @return The instance for this substrate's name (never null)
    /// @throws NullPointerException if substrate is null
    /// @throws Exception            if the substrate parameter is not a runtime-provided implementation
    /// @see #percept(Name)
    /// @see Substrate#subject()

    @NotNull
    default P percept (
      @NotNull final Substrate < ? > substrate
    ) {

      requireNonNull ( substrate );

      return percept (
        substrate
          .subject ()
          .name ()
      );

    }


    /// Returns the instance for the given subject.
    ///
    /// Convenience method that extracts the Name from the subject and delegates
    /// to [#percept(Name)]. Equivalent to: `percept(subject.name())`
    ///
    /// @param subject the subject whose name identifies the instance
    /// @return The instance for this subject's name (never null)
    /// @throws NullPointerException if subject is null
    /// @throws Exception            if the subject parameter is not a runtime-provided implementation
    /// @see #percept(Name)
    /// @see Subject#name()

    @NotNull
    default P percept (
      @NotNull final Subject < ? > subject
    ) {

      requireNonNull ( subject );

      return percept (
        subject.name ()
      );

    }


    /// Returns the instance for the given name.
    ///
    /// This is the core lookup method. The specific creation and caching semantics
    /// depend on the implementation:
    /// - **[Conduit]**: Composes a new percept using the composer function
    /// - **[Cell]**: Creates or retrieves a child cell
    ///
    /// When used with a [Conduit], the composer is invoked lazily on first lookup
    /// for a name. If the composer calls [Channel#pipe(Configurer)], any exceptions
    /// thrown by [Flow] or [Sift] methods are wrapped in an [Exception] and propagated
    /// to the caller.
    ///
    /// @param name the name identifying the desired instance
    /// @return The instance for this name (never null)
    /// @throws NullPointerException if name is null
    /// @throws Exception            if the name parameter is not a runtime-provided implementation, or the composer throws
    /// @see Name

    @NotNull
    P percept (
      @NotNull Name name
    );

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
  /// and [Channel] - the same name always routes to the same percept/channel instance.
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
    /// @throws NullPointerException if the suffix parameter is `null`
    /// @throws Exception            if the suffix parameter is not a runtime-provided implementation

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
    /// @throws NullPointerException     if the path parameter is `null`
    /// @throws IllegalArgumentException if the path is empty or contains empty segments

    @NotNull
    Name name (
      @NotNull String path
    );


    /// Returns a name that has this name as a direct prefix and appends the enum's [Enum#name()] value.
    /// The declaring type of the enum constant is used (per [Enum#getDeclaringClass()]);
    /// cached segments ensure repeated extensions with the same enum reuse the same instance.
    ///
    /// @param path the enum to be appended to this name
    /// @return A name with the enum name appended as a name part
    /// @throws NullPointerException if the path parameter is `null`

    @NotNull
    Name name (
      @NotNull Enum < ? > path
    );


    /// Returns an extension of this name from iterating over a specified [Iterable] of [String] values.
    /// When the iterable is empty this name is returned unchanged; interned segments are reused when possible.
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
    /// @throws NullPointerException if the `Class` typed parameter is `null`

    @NotNull
    Name name (
      @NotNull Class < ? > type
    );


    /// Creates a `Name` from a `Member`.
    /// The declaring class hierarchy and member name are appended, reusing interned segments.
    ///
    /// @param member the `Member` to be mapped to a `Name`
    /// @return A name extended with the member hierarchy
    /// @throws NullPointerException if the `Member` typed parameter is `null`

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

    @SuppressWarnings ( "BooleanMethodIsAlwaysInverted" )
    boolean conditional () default false;

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
  /// // Conduit pools channels by name - same name returns same channel
  /// @Tenure(INTERNED)
  /// interface Channel<E> { ... }
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
    /// Examples: [Channel] (pooled by name in Conduit), [Name] (interned)
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
    /// Examples: [Cell] (root ephemeral, children anchored by parent),
    ///           [Subscription] (anchored to source), [Pipe] (anchored when via Channel)
    int ANCHORED = INTERNED | EPHEMERAL;


    /// The tenure type for instances of this type.
    int value ();

  }


  /// Indicates a parameter should never be passed a null argument value

  @Documented
  @Retention ( SOURCE )
  @Target ( {PARAMETER, METHOD, FIELD} )
  @interface NotNull {
  }

  /// Marker interface for substrate entities that can emit observations.
  ///
  /// A percept is any entity capable of emitting values into the substrate's event flow.
  /// This represents the fundamental capability of producing observable data that can be
  /// processed, transformed, and consumed within the substrates' framework.
  ///
  /// ## Percept Hierarchy
  ///
  /// The substrate defines percepts at different abstraction levels:
  ///
  /// - **[Pipe]**: Primitive percept providing raw emission capability
  /// - **Serventis Instruments**: Semantic percepts providing domain-specific emission
  ///
  /// ## Design Rationale
  ///
  /// This marker interface establishes "emittability" as a first-class substrate concept.
  /// By marking both primitive pipes and higher-level instruments as percepts, the
  /// architecture makes explicit what can produce observable events, enabling generic
  /// tooling, meta-observability, and compositional patterns while maintaining zero
  /// performance overhead.
  ///
  /// ## Primitive vs Semantic Percepts
  ///
  /// - **Primitive Percept** ([Pipe]): Type-parameterized emission of any value
  /// - **Semantic Percepts** (Instruments): Domain-specific facades that emit semantic signs/signals
  ///
  /// Both share the fundamental capability: emitting observations into the substrate.
  ///
  /// @author William David Louth
  /// @since 1.0

  interface Percept {
    // Marker interface - no methods
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
  /// - [Channel#pipe()] - Obtains the channel's pipe for direct emission
  /// - [Circuit#pipe(Receptor)] - Create a pipe that asynchronously dispatches emissions to a receptor
  ///
  /// For receiving emissions, register receptors directly via [Registrar#register(Receptor)].
  ///
  /// **User code should not implement this interface directly.** All Pipe implementations
  /// are controlled by the framework, which enables future optimizations like sealed types
  /// and monomorphic call sites.
  ///
  /// ## Threading Model
  ///
  /// Pipes have **no inherent threading contract** - execution context depends on the source:
  ///
  /// - **Direct pipes**: Execute on caller's thread (synchronous)
  /// - **Circuit-dispatched pipes**: Execute on circuit thread (async enqueue/dequeue)
  /// - **Channel pipes**: Route through flow, then dispatch to circuit thread
  /// - **Registered pipes**: Execute on circuit thread when channel emits
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
  /// Pipes themselves provide **no thread-safety guarantees**:
  /// - Implementations are not required to be thread-safe
  /// - Concurrent calls to `emit()` from multiple threads may require external synchronization
  /// - Circuit-dispatched and registered pipes avoid this problem - they execute exclusively
  ///   on the circuit thread, eliminating concurrent access
  ///
  /// For direct pipes called from multiple threads, use external synchronization or
  /// circuit-dispatch to serialize access.
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
  /// @see Channel#pipe()
  /// @see Registrar#register(Pipe)
  /// @see Registrar#register(Receptor)

  @Tenure ( Tenure.ANCHORED )
  @Provided
  non-sealed interface Pipe < E >
    extends Percept,
            Substrate < Pipe < E > > {

    /// A method for passing a data value along a pipeline.
    ///
    /// **Threading behavior depends on pipe type:**
    ///
    /// - **Circuit-dispatched pipes** (channels, conduits): Emissions are queued by the
    ///   circuit and processed sequentially on the circuit's processing thread, ensuring
    ///   no concurrent execution occurs.
    ///
    /// Use [Circuit#pipe(Pipe, Configurer)] to create circuit-dispatched pipes with flows.
    ///
    /// @param emission the value to be emitted
    /// @throws NullPointerException if the emission is `null`

    @Queued
    void emit (
      @NotNull E emission
    );

  }


  /// Indicates a type exclusively provided by the runtime.

  @Documented
  @Retention ( SOURCE )
  @Target ( TYPE )
  @interface Provided {
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
  /// - **Architectural consistency**: Pairs with [Percept] - percepts emit, receptors receive
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
  /// Receptors should generally not receive null emissions, as the substrate framework
  /// validates emissions at pipe entry points. However, receptors should handle null
  /// gracefully if defensive programming is desired.
  ///
  /// @param <E> the type of emission to receive
  /// @author William David Louth
  /// @see Percept
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
    /// Receptor<String> receptor = Receptor.of();
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
    /// - Exceptions propagate to circuit's error handler
    /// - Stateful operations are safe (single-threaded execution on circuit thread)
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
  /// Calling [#register(Pipe)] after the callback returns results in **undefined behavior**.
  /// The registrar may throw exceptions, silently fail, or register to the wrong channel.
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
    /// **MUST be called only within the subscriber callback**
    /// that provided this registrar. Calling after the callback returns results in undefined
    /// behavior.
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
    /// @throws Exception             if the pipe parameter is not a runtime-provided implementation
    /// @throws IllegalStateException if called outside the valid subscriber callback (implementation-dependent)
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
    /// **MUST be called only within the subscriber callback**
    /// that provided this registrar. Calling after the callback returns results in undefined
    /// behavior.
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
    /// @throws IllegalStateException if called outside the valid subscriber callback (implementation-dependent)
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
  /// - State transitions: Active → Closed (terminal)
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
  /// - No new operations accepted (may throw [IllegalStateException])
  /// - Background operations terminated or marked for termination
  /// - System resources released (threads, timers, handles)
  /// - Pending operations may complete (eventual consistency)
  ///
  /// For example, after `circuit.close()`:
  /// - New conduit/channel creation throws
  /// - Processing thread drains queue then terminates
  /// - Already-queued emissions still process
  ///
  /// ## [Scope] Integration
  ///
  /// Resources can be managed via Scope:
  ///
  /// @see Circuit#close()
  /// @see Subscription#close()
  /// @see Scope
  /// @see Closure

  @Abstract
  sealed interface Resource
    permits Circuit,
            Reservoir,
            Subscriber,
            Subscription,
            Tap {

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
    /// Resources not managed by a circuit (e.g., standalone Scope) may use
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
    /// @throws Exception             if the name parameter is not a runtime-provided implementation
    /// @throws IllegalStateException if this scope is closed
    /// @see #scope()

    @NotNull
    Scope scope ( @NotNull Name name );

  }

  /// A temporal builder for comparison-based filtering operations in a flow pipeline.
  ///
  /// Sift provides a fluent API for configuring filters based on value comparisons,
  /// ranges, and extrema detection. It is provided as a callback parameter to
  /// [Flow#sift(Comparator, Configurer)] and is only valid during that callback.
  ///
  /// ## Temporal Lifetime
  ///
  /// Marked with `@Temporal`, Sift instances have **strictly scoped validity**:
  /// - Valid ONLY during the [Flow#sift(Comparator, Configurer)] configurer callback
  /// - Must NOT be retained beyond the callback scope
  /// - Behavior is undefined if used after callback returns
  ///
  /// ## Comparison Semantics
  ///
  /// All comparison operations use the `java.util.Comparator` provided to
  /// [Flow#sift(Comparator, Configurer)]. The comparator defines the ordering
  /// used for "above", "below", "high", "low", and range operations.
  ///
  /// ## Usage Pattern
  ///
  /// ## Method Chaining
  ///
  /// Sift methods return the same Sift instance to enable fluent chaining. Multiple
  /// filter conditions can be combined; all must be satisfied for a value to pass.
  ///
  /// @param <E> the type of elements being filtered
  /// @see Flow#sift(Comparator, Configurer)

  @Temporal
  @Provided
  interface Sift < E > {

    /// Filters to pass only values above the specified lower bound (exclusive).
    ///
    /// Uses the comparator from [Flow#sift(Comparator, Configurer)] to determine
    /// ordering. A value passes if `comparator.compare(value, lower) > 0`.
    ///
    /// Stateless - each emission is independently compared against the bound.
    ///
    /// @param lower the exclusive lower bound
    /// @return This sift instance for method chaining
    /// @throws NullPointerException if the specified lower bound is `null`
    /// @see #below(Object)
    /// @see #range(Object, Object)

    @NotNull
    @Fluent
    Sift < E > above (
      @NotNull E lower
    );


    /// Filters to pass only values below the specified upper bound (exclusive).
    ///
    /// Uses the comparator from [Flow#sift(Comparator, Configurer)] to determine
    /// ordering. A value passes if `comparator.compare(value, upper) < 0`.
    ///
    /// Stateless - each emission is independently compared against the bound.
    ///
    /// @param upper the exclusive upper bound
    /// @return This sift instance for method chaining
    /// @throws NullPointerException if the specified upper bound is `null`
    /// @see #above(Object)
    /// @see #range(Object, Object)

    @NotNull
    @Fluent
    Sift < E > below (
      @NotNull E upper
    );


    /// Filters to pass only values that represent a new high (maximum seen so far).
    ///
    /// Maintains state tracking the highest value seen. Each emission is compared
    /// against the current high using the comparator. Only emissions that exceed
    /// the current high are passed through, and the high is updated.
    ///
    /// Stateful - tracks the running maximum value.
    ///
    /// Use case: Detect peak values, new record highs, monotonic increases.
    ///
    /// @return This sift instance for method chaining
    /// @see #low()

    @NotNull
    @Fluent
    Sift < E > high ();


    /// Filters to pass only values that represent a new low (minimum seen so far).
    ///
    /// Maintains state tracking the lowest value seen. Each emission is compared
    /// against the current low using the comparator. Only emissions that fall below
    /// the current low are passed through, and the low is updated.
    ///
    /// Stateful - tracks the running minimum value.
    ///
    /// Use case: Detect trough values, new record lows, monotonic decreases.
    ///
    /// @return This sift instance for method chaining
    /// @see #high()

    @NotNull
    @Fluent
    Sift < E > low ();


    /// Filters to pass only values at or below the specified maximum (inclusive).
    ///
    /// Uses the comparator from [Flow#sift(Comparator, Configurer)] to determine
    /// ordering. A value passes if `comparator.compare(value, max) <= 0`.
    ///
    /// Stateless - each emission is independently compared against the bound.
    ///
    /// @param max the inclusive maximum value
    /// @return This sift instance for method chaining
    /// @throws NullPointerException if the specified maximum is `null`
    /// @see #min(Object)
    /// @see #range(Object, Object)

    @NotNull
    @Fluent
    Sift < E > max (
      @NotNull E max
    );


    /// Filters to pass only values at or above the specified minimum (inclusive).
    ///
    /// Uses the comparator from [Flow#sift(Comparator, Configurer)] to determine
    /// ordering. A value passes if `comparator.compare(value, min) >= 0`.
    ///
    /// Stateless - each emission is independently compared against the bound.
    ///
    /// @param min the inclusive minimum value
    /// @return This sift instance for method chaining
    /// @throws NullPointerException if the specified minimum is `null`
    /// @see #max(Object)
    /// @see #range(Object, Object)

    @NotNull
    @Fluent
    Sift < E > min (
      @NotNull E min
    );


    /// Filters to pass only values within the specified range (inclusive on both ends).
    ///
    /// Uses the comparator from [Flow#sift(Comparator, Configurer)] to determine
    /// ordering. A value passes if `comparator.compare(value, lower) >= 0 &&
    /// comparator.compare(value, upper) <= 0`.
    ///
    /// Stateless - each emission is independently compared against the bounds.
    ///
    /// @param lower the inclusive lower bound
    /// @param upper the inclusive upper bound
    /// @return This sift instance for method chaining
    /// @throws NullPointerException if the specified lower or upper bound is `null`
    /// @see #above(Object)
    /// @see #below(Object)

    @NotNull
    @Fluent
    Sift < E > range (
      @NotNull E lower,
      @NotNull E upper
    );

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
  /// @see State#values(Slot)
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
  /// Source is implemented by Circuit, Conduit, and Cell.
  ///
  /// ## Subscription Lifecycle
  ///
  /// When a [Subscriber] is registered with a source, the subscriber receives callbacks lazily
  /// when channels receive their first emission after the subscription is registered. Each callback
  /// supplies a [Registrar] that the subscriber can use to attach or detach pipes.
  ///
  /// ## Dynamic Subscription Model
  ///
  /// Sources enable **dynamic discovery** of channels:
  /// 1. Subscriber calls [#subscribe(Subscriber)] to register interest
  /// 2. Source creates channels lazily as they're accessed (e.g., via [Lookup#percept(Name)])
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
  /// - Channels detect version changes on their next emission
  /// - Channels rebuild their pipe lists only when needed (lazy evaluation)
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
  ///       guarantees. Emits [State] values representing circuit lifecycle and status.
  /// - **[Conduit]**: Pooled percept factory that emits events and manages channel lifecycle.
  /// - **[Cell]**: Combines a pipe and source for advanced data flow patterns.
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
  /// - Subscriber callbacks occur in channel creation order
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
  /// @see Channel
  /// @see Circuit
  /// @see Conduit
  /// @see Cell

  @Abstract
  sealed interface Source < E, S extends Source < E, S > >
    extends Substrate < S >
    permits Cell,
            Circuit,
            Conduit,
            Tap {

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
    /// - The source itself is closed (e.g., when owning circuit closes)
    ///
    /// ## Registration Timing
    ///
    /// The subscription is registered asynchronously on the circuit's processing thread:
    /// - This method enqueues a registration job and returns immediately
    /// - The subscription becomes active once the circuit processes the job
    /// - **Lazy callback invocation**: Subscriber callbacks are invoked lazily during rebuild,
    ///   which occurs on the first emission to a channel after the subscription is registered
    /// - **Channel creation vs. emission**: Creating a channel (e.g., via [Lookup#percept(Name)])
    ///   does NOT invoke callbacks; callbacks fire when the channel receives its first emission
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
    /// Subscribers are bound to the circuit that created them. Attempting to subscribe
    /// a subscriber to a source from a different circuit will throw an exception. This
    /// prevents threading bugs that would occur if a subscriber's pipes were invoked on
    /// the wrong circuit's worker thread.
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
    /// @throws Exception            if the subscriber belongs to a different circuit than this source
    /// @see Subscriber
    /// @see Subscription
    /// @see Registrar

    @New
    @NotNull
    @Queued
    Subscription subscribe (
      @NotNull Subscriber < E > subscriber
    );


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

  }

  /// A tap into a conduit that transforms emissions.
  ///
  /// A Tap follows the structure of its source conduit - channels and subject
  /// hierarchy - but transforms emissions from one type to another. Channels
  /// are created automatically as subjects appear in the source conduit.
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
  /// As a [Resource], a Tap manages its subscription to the source conduit.
  /// Closing a tap unsubscribes from the source and releases mirrored channels.
  /// Taps should be closed when no longer needed to prevent resource leaks.
  ///
  /// ## Example
  ///
  /// ```java
  /// // Create tap that transforms signals to names
  /// Tap<Name> names = conduit.tap(
  ///   signal -> name(signal.sign(), signal.dimension())
  /// );
  ///
  /// // Subscribe to receive transformed emissions
  /// Subscription sub = names.subscribe(subscriber);
  ///
  /// // Clean up when done
  /// names.close();
  /// ```
  ///
  /// @param <T> the transformed emission type
  /// @see Conduit#tap(Function)
  /// @see Resource

  @Tenure ( Tenure.EPHEMERAL )
  @Provided
  non-sealed interface Tap < T >
    extends Source < T, Tap < T > >,
            Resource {

  }

  /// Represents an immutable collection of named slots containing typed values.
  /// Slots iterate from the most recently added entry to the oldest, supporting persistent updates.
  ///
  /// ## Persistence Semantics
  ///
  /// State is immutable. Operations such as [#state(Name, int)] return new instances with the
  /// supplied slot as the most recent entry, leaving prior instances unchanged. Iteration and
  /// streaming views observe slots from the most recently added entry to the oldest.
  ///
  /// The [#compact()] method removes duplicates so the retained value for each
  /// (name, type) pair reflects the most recent assignment. The iteration order of the
  /// compacted state is undefined.
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

    /// Reduces state history by removing duplicate entries for the same (name, type) pair.
    /// For each unique (name, type) combination, only the most recently added slot is retained.
    ///
    /// **The iteration order of the returned state is undefined.** If a specific order is required,
    /// iterate over the compacted state and rebuild it accordingly.
    ///
    /// This is useful for compacting accumulated state history when only the current value
    /// of each slot is needed.
    ///
    /// @return A new compacted state with duplicates removed and undefined iteration order

    @New
    @NotNull
    State compact ();


    /// Returns a state that includes a [Slot][Slot] mapped to the specified value.
    /// The new slot becomes the most recent entry; if an equal slot (same name and value)
    /// already exists this state instance is returned.
    ///
    /// @param name  the name of the slot
    /// @param value the value of the slot
    /// @return A state containing the specified slot
    /// @throws NullPointerException if name is `null`
    /// @throws Exception            if the name parameter is not a runtime-provided implementation

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
    /// @throws Exception            if the name parameter is not a runtime-provided implementation

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
    /// @throws Exception            if the name parameter is not a runtime-provided implementation

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
    /// @throws Exception            if the name parameter is not a runtime-provided implementation

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
    /// @throws Exception            if the name parameter is not a runtime-provided implementation

    @New ( conditional = true )
    @NotNull
    State state (
      @NotNull Name name,
      boolean value
    );


    /// Returns a state that includes a [Slot][Slot] mapped to the specified value.
    /// The new slot becomes the most recent entry; if an equal slot (same name and value)
    /// already exists this state instance is returned.
    ///
    /// @param name  the name of the slot
    /// @param value the value of the slot
    /// @return A state containing the specified slot
    /// @throws NullPointerException if name or value is `null`
    /// @throws Exception            if the name parameter is not a runtime-provided implementation

    @New ( conditional = true )
    @NotNull
    State state (
      @NotNull Name name,
      @NotNull String value
    );


    /// Returns a state that includes a [Slot][Slot] mapped to the specified value.
    /// The new slot becomes the most recent entry; if an equal slot (same name and value)
    /// already exists this state instance is returned.
    ///
    /// @param name  the name of the slot
    /// @param value the value of the slot
    /// @return A state containing the specified slot
    /// @throws NullPointerException if name or value is `null`
    /// @throws Exception            if the name or value parameters are not runtime-provided implementations

    @New ( conditional = true )
    @NotNull
    State state (
      @NotNull Name name,
      @NotNull Name value
    );


    /// Returns a state that includes a [Slot][Slot] mapped to the specified value.
    /// The new slot becomes the most recent entry; if an equal slot (same name and value)
    /// already exists this state instance is returned.
    ///
    /// @param name  the name of the slot
    /// @param value the value of the slot
    /// @return A state containing the specified slot
    /// @throws NullPointerException if name or value is `null`
    /// @throws Exception            if the name or value parameters are not runtime-provided implementations

    @New ( conditional = true )
    @NotNull
    State state (
      @NotNull Name name,
      @NotNull State value
    );


    /// Returns a state that includes the [Slot][Slot] specified.
    /// The new slot becomes the most recent entry; if an equal slot (same name and value)
    /// already exists this state instance is returned.
    ///
    /// @param slot the slot to be added
    /// @return A state containing the specified slot
    /// @throws NullPointerException if slot is `null`
    /// @throws Exception            if the slot parameter is not a runtime-provided implementation

    @New ( conditional = true )
    @NotNull
    State state (
      @NotNull Slot < ? > slot
    );


    /// Returns a state that includes a [Slot][Slot] with a [Name] value derived from an enum.
    /// The slot's name is derived from the enum's declaring class,
    /// and the slot's value is a [Name] created from the enum constant's name.
    /// The new slot becomes the most recent entry; if an equal slot (same name and value)
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
    /// @throws Exception            if the slot parameter is not a runtime-provided implementation

    < T > T value (
      @NotNull Slot < T > slot
    );


    /// Returns a sequential [Stream][Stream] of all values from slots that match the specified template.
    /// Matching occurs on slot name identity and slot type, and results are ordered from the most recent slot to the oldest.
    ///
    /// @param <T>  the value type
    /// @param slot the slot to lookup
    /// @return A non-`null` stream of matching values
    /// @throws NullPointerException if slot is `null`
    /// @throws Exception            if the slot parameter is not a runtime-provided implementation

    @NotNull
    < T > Stream < T > values (
      @NotNull Slot < ? extends T > slot
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
  /// - **Channel creation vs. emission**: Creating a channel (e.g., via [Lookup#percept(Name)])
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
  /// Calling `registrar.register()` after your callback returns results in undefined behavior.
  /// This temporal restriction allows efficient registrar implementation without
  /// synchronization overhead.
  ///
  /// ## Subject Inspection
  ///
  /// The [Subject] parameter provides:
  /// - **Identity**: Unique [Subject#id()] for this channel
  /// - **Name**: Hierarchical [Subject#name()] identifying the channel
  /// - **State**: Associated [Subject#state()] metadata
  /// - **Type**: [Subject#type()] for pattern matching (always `Channel.class`)
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
  /// - Exceptions thrown from your callback propagate to the circuit
  /// - Circuit may terminate or handle errors per implementation
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
  /// Subscriber extends [Resource], enabling try-with-resources for automatic cleanup.
  /// Calling [Resource#close()] on a subscriber will unsubscribe from all sources
  /// by closing all subscriptions created by this subscriber.
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
  /// The unregistration happens asynchronously on the circuit's processing thread,
  /// ensuring deterministic removal without blocking the caller.
  ///
  /// ## Timing Considerations
  ///
  /// After calling `close()`:
  /// - The unsubscription is coordinated asynchronously on the circuit thread
  /// - Already-queued callbacks may still execute (circuit-thread delivery)
  /// - No NEW channels will trigger callbacks after unsubscription completes
  /// - Pipes registered before close() continue receiving emissions until rebuild
  /// - Next emission on each channel triggers lazy rebuild, removing the pipes
  ///
  /// This provides "eventual consistency" - the circuit processes the unsubscription
  /// in sequence with other events. The unsubscription is not immediately visible to
  /// all channels - it becomes visible when each channel next emits (lazy rebuild).
  ///
  /// **Lazy unsubscription**: Like subscription, unsubscription uses lazy rebuild.
  /// Channels detect the unsubscription on their next emission and rebuild their
  /// pipe lists to exclude the unsubscribed pipes. This avoids global coordination
  /// and blocking.
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
    permits Channel,
            Cortex,
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


  /// Indicates a type that is transient and whose reference should not be retained.

  @SuppressWarnings ( "WeakerAccess" )
  @Documented
  @Retention ( SOURCE )
  @Target ( {PARAMETER, TYPE} )
  @interface Temporal {
  }


  /// Indicates a type that serves a utility purpose.

  @SuppressWarnings ( "WeakerAccess" )
  @Documented
  @Retention ( SOURCE )
  @Target ( {TYPE, METHOD} )
  @interface Utility {
  }

  /// Base exception for provider-related runtime errors.
  ///
  /// The Substrates framework allows multiple provider implementations (SPI) to coexist.
  /// This exception is thrown when provider-related errors occur at runtime, such as
  /// mixing objects from different providers or other provider-specific failures.
  ///
  /// SPI implementations must extend this exception to provide concrete exception types
  /// specific to their provider implementation.
  ///
  /// ## Common Causes
  ///
  /// - Creating a [Name] from one [Cortex] and passing it to a [Circuit] from a different Cortex
  /// - Creating a [Subscriber] from one provider and subscribing to a [Source] from another provider
  /// - Mixing [Composer] instances across providers
  ///
  /// ## Resolution
  ///
  /// Ensure all API objects used together come from the same provider instance:
  ///
  /// @see Cortex

  abstract class Exception extends RuntimeException {

    /// Constructs a new exception with the specified detail message.
    ///
    /// @param message the detail message

    protected Exception ( final String message ) {

      super ( message );

    }


    /// Constructs a new exception with a detail message and cause.
    ///
    /// @param message the detail message
    /// @param cause   the underlying cause

    protected Exception (
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
