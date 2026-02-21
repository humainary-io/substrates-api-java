// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.opt.exec;

import io.humainary.substrates.ext.serventis.api.Serventis;
import io.humainary.substrates.ext.serventis.sdk.SignalSet;

import static java.util.Objects.requireNonNull;


/// # Services API
///
/// The `Services` API provides a comprehensive framework for observing service-to-service and
/// intra-service interactions through **semantic signal emission**. It enables fine-grained
/// instrumentation of work execution, coordination, and outcome reporting based on signaling
/// theory and social systems regulated by local and remote status assessment.
///
/// ## Purpose
///
/// This API enables systems to emit **rich semantic signals** about service interactions,
/// capturing not just success/failure but the full lifecycle of work: scheduling, delays,
/// retries, redirections, suspensions, and more. The dual-dimension model (CALLER/CALLEE)
/// enables tracking from both client and server perspectives.
///
/// ## Important: Reporting vs Implementation
///
/// This API is for **reporting service interaction semantics**, not implementing services.
/// If you have actual service implementations (REST endpoints, RPC handlers, workflow engines, etc.),
/// use this API to emit observability signals about operations performed by or on them.
/// Observer agents can then reason about service health, interaction patterns, and distributed
/// system behavior without coupling to your implementation details.
///
/// **Example**: When your service receives a request, call `service.start(CALLEE)` before
/// processing begins. When complete, call `service.success(CALLEE)` or `service.fail(CALLEE)`.
/// The signals enable meta-observability: observing the observability instrumentation itself
/// to understand service lifecycle, coordination patterns, and system-wide interaction dynamics.
///
/// ## Key Concepts
///
/// - **Service**: An observable entity that emits signals about work execution and coordination
/// - **Signal**: A semantic event combining a **Sign** (what happened) and **Dimension** (perspective)
/// - **Sign**: The type of interaction (START, CALL, SUCCESS, FAIL, RETRY, etc.)
/// - **Dimension**: The perspective (CALLER = calling service, CALLEE = called service)
/// - **Work**: Either local execution or remote calling of operations or functions
///
/// ## Dual-Dimension Model
///
/// Every sign has two dimensions representing different perspectives:
///
/// | Dimension | Perspective        | Example Signals                      |
/// |-----------|--------------------|------------------------------------- |
/// | CALLER    | Calling service    | CALL, START, SUCCESS, FAIL           |
/// | CALLEE    | Called service     | CALL, START, SUCCESS, FAIL           |
///
/// **CALLER** signals indicate actions taken when calling another service while **CALLEE** signals
/// indicate actions taken when serving requests. This enables distributed systems to coordinate
/// based on both client and server perspectives.
///
/// ## Signal Categories
///
/// The API defines signals across several semantic categories:
///
/// ### Execution Lifecycle
/// - **START/STARTED**: Work execution begins
/// - **STOP/STOPPED**: Work execution completes (regardless of outcome)
/// - **CALL/CALLED**: Remote work request issued
///
/// ### Outcomes
/// - **SUCCESS/SUCCEEDED**: Work completed successfully
/// - **FAIL/FAILED**: Work failed to complete
///
/// ### Flow Control
/// - **DELAY/DELAYED**: Work postponed
/// - **SCHEDULE/SCHEDULED**: Work queued for future execution
/// - **SUSPEND/SUSPENDED**: Work paused, may resume
/// - **RESUME/RESUMED**: Suspended work restarted
///
/// ### Error Handling
/// - **RETRY/RETRIED**: Failed work being reattempted
/// - **RECOURSE/RECOURSED**: Degraded mode activated
/// - **REDIRECT/REDIRECTED**: Work forwarded to alternative service
///
/// ### Rejection
/// - **REJECT/REJECTED**: Work declined (e.g., overload, policy)
/// - **DISCARD/DISCARDED**: Work dropped (e.g., expired, invalid)
/// - **DISCONNECT/DISCONNECTED**: Unable to reach service
///
/// ### Temporal
/// - **EXPIRE/EXPIRED**: Work exceeded time budget
///
/// ## Relationship to Other APIs
///
/// `Services` integrates with other Serventis APIs to form a complete observability picture:
///
/// - **Probes API**: Service failures (FAIL) may correspond to probe.fail() / probe.failed() signals
/// - **Resources API**: Service delays (DELAY) may be caused by resource denials (DENY)
/// - **Statuses API**: Service signal patterns inform condition assessment (many FAILs → DEGRADED)
/// - **Situations API**: Service conditions influence situational priority (sustained FAILs → CRITICAL)
///
/// ## Orientation Usage Patterns
///
/// ### Calling Services (CALLER)
/// ```java
/// service.call(CALLER);      // I am calling remote service
/// service.success(CALLER);   // My call completed successfully
/// service.retry(CALLER);     // I am retrying my call after failure
/// ```
///
/// ### Serving Requests (CALLEE)
/// ```java
/// // Handling incoming request
/// service.start(CALLEE);     // I am starting work on received request
/// service.success(CALLEE);   // I completed the work successfully
/// service.fail(CALLEE);      // I failed to complete the work
/// ```
///
/// ## Performance Considerations
///
/// Service signal emissions are designed for per-request operation at moderate to high frequency
/// (100-100K signals/sec typical). Signals flow asynchronously through the circuit's event queue,
/// adding minimal overhead to service operations. The convenience methods (`execute`, `dispatch`)
/// add negligible overhead (~10-20ns) compared to manual signal emission.
///
/// For extremely high-frequency services (>1M requests/sec), consider:
/// - Using manual signal emission to avoid lambda allocation overhead
/// - Sampling signals rather than emitting for every request
/// - Aggregating signals before emission
///
/// @author William David Louth
/// @since 1.0

public final class Services
  implements Serventis {

  private Services () { }

  /// A static composer function for creating Service instruments.
  ///
  /// This method can be used as a method reference with conduits as follows:
  ///
  /// Example usage:
  /// ```java
  /// final var cortex = Substrates.cortex();
  /// var service = circuit.conduit(Services::composer).percept(cortex.name("order.processor"));
  /// ```
  ///
  /// @param channel the channel from which to create the service
  /// @return a new Service instrument for the specified channel
  /// @throws NullPointerException if the channel param is `null`

  @New
  @NotNull
  public static Service composer (
    @NotNull final Channel < ? super Signal > channel
  ) {

    return
      new Service (
        channel.pipe ()
      );

  }

  /// A [Sign] classifies operations, transitions, and outcomes that occur during service request
  /// execution and inter-service calling. These classifications enable analysis of service behavior,
  /// coordination patterns, and resilience strategies.
  ///
  /// Note: We use the term `work` here to mean either (remote) call or (local) execution.
  ///
  /// ## Sign Categories
  ///
  /// Signs are organized into functional categories representing different aspects of service behavior:
  ///
  /// - **Lifecycle**: START, STOP, CALL
  /// - **Outcomes**: SUCCESS, FAIL
  /// - **Flow Control**: DELAY, SCHEDULE, SUSPEND, RESUME
  /// - **Error Handling**: RETRY, RECOURSE, REDIRECT
  /// - **Rejection**: REJECT, DISCARD, DISCONNECT
  /// - **Temporal**: EXPIRE

  public enum Sign
    implements Serventis.Sign {

    /// Indicates the start of work execution.
    ///
    /// START marks the beginning of actual work processing, after scheduling and before outcomes.
    /// Used for tracking execution timing, concurrency levels, and work-in-progress metrics.
    ///
    /// **Typical usage**: Local execution start, thread/coroutine activation, batch processing start

    START,

    /// Indicates the completion of work execution (regardless of outcome).
    ///
    /// STOP marks the end of work processing, whether successful or failed. The time between
    /// START and STOP represents actual execution duration. Always emitted in finally blocks
    /// to ensure accurate execution timing.
    ///
    /// **Typical usage**: Execution completion, resource cleanup, duration measurement

    STOP,

    /// Indicates a request (call) for work to be done by another service.
    ///
    /// CALL represents the invocation of remote or external work, distinguishing it from local
    /// execution (START). Used to track inter-service dependencies, call graphs, and distributed
    /// trace initiation.
    ///
    /// **Typical usage**: RPC calls, HTTP requests, message sends, async job submission

    CALL,

    /// Indicates successful completion of work.
    ///
    /// SUCCESS represents work that completed as intended, meeting its contract and producing
    /// valid results. Forms the baseline for success rate calculations and SLO compliance.
    ///
    /// **Typical usage**: Successful execution, valid results returned, contract fulfilled

    SUCCESS,

    /// Indicates failure to complete work.
    ///
    /// FAIL represents work that could not complete successfully due to errors, exceptions,
    /// invalid inputs, or violated constraints. Forms the basis for error rate calculations
    /// and degradation detection.
    ///
    /// **Typical usage**: Exceptions thrown, validation failures, constraint violations, errors

    FAIL,

    /// Indicates activation of a degraded operational mode after failure.
    ///
    /// RECOURSE represents fallback strategies activated when primary paths fail. Unlike RETRY
    /// (trying again) or REDIRECT (going elsewhere), RECOURSE means degraded functionality.
    ///
    /// **Typical usage**: Circuit breaker fallbacks, cached responses, degraded mode, default values

    RECOURSE,

    /// Indicates forwarding of work to an alternative service or endpoint.
    ///
    /// REDIRECT represents work being routed to a different destination, often for load balancing,
    /// failover, or service mesh routing. Preserves work identity while changing destination.
    ///
    /// **Typical usage**: Load balancer redirects, failover routing, service mesh splits, A/B tests

    REDIRECT,

    /// Indicates work exceeded its time budget.
    ///
    /// EXPIRE represents work that ran too long or missed its deadline. Different from TIMEOUT
    /// (waiting for response) - EXPIRE means the work itself took too long.
    ///
    /// **Typical usage**: Deadline exceeded, SLA violation, budget exhaustion, TTL expiration

    EXPIRE,

    /// Indicates automatic retry of work after failure.
    ///
    /// RETRY represents reattempting work that previously failed, typically for transient errors.
    /// Forms the basis for retry rate analysis, backoff effectiveness, and eventual success tracking.
    ///
    /// **Typical usage**: Transient error recovery, network retry, idempotent operation repeat

    RETRY,

    /// Indicates refusal to accept work.
    ///
    /// REJECT represents work being turned away at the boundary, typically due to overload,
    /// policy violations, or capacity limits. Work is never started. Enables admission control.
    ///
    /// **Typical usage**: Rate limiting, circuit breaker open, overload protection, policy denial

    REJECT,

    /// Indicates deliberate dropping of work.
    ///
    /// DISCARD represents work being intentionally abandoned, typically due to invalidity,
    /// irrelevance, or changed priorities. Different from REJECT (not accepted) - DISCARD means
    /// accepted but then dropped.
    ///
    /// **Typical usage**: Invalid requests, expired messages, priority shedding, queue overflow

    DISCARD,

    /// Indicates postponement of work.
    ///
    /// DELAY represents work being intentionally slowed or deferred, often for backpressure,
    /// rate limiting, or coordination. Work will eventually proceed.
    ///
    /// **Typical usage**: Backpressure delays, rate limiting pauses, exponential backoff, throttling

    DELAY,

    /// Indicates work queued for future execution.
    ///
    /// SCHEDULE represents work being placed in a queue or scheduled for later processing.
    /// Forms the basis for queue depth, scheduling lag, and backlog analysis.
    ///
    /// **Typical usage**: Task queuing, delayed execution, batch scheduling, async job queuing

    SCHEDULE,

    /// Indicates work paused awaiting resumption.
    ///
    /// SUSPEND represents work being temporarily halted, preserving state for later resumption.
    /// Different from DELAY (brief pause) - SUSPEND means potentially long-term suspension.
    ///
    /// **Typical usage**: Long-running workflows, saga compensation, manual intervention, resource wait

    SUSPEND,

    /// Indicates previously suspended work restarting.
    ///
    /// RESUME represents suspended work being reactivated. Forms pairs with SUSPEND signals
    /// to track suspension duration and resumption patterns.
    ///
    /// **Typical usage**: Workflow continuation, saga resume, manual restart, resource availability

    RESUME,

    /// Indicates inability to reach or communicate with a service.
    ///
    /// DISCONNECT represents communication failures preventing work submission. Different from
    /// FAIL (work attempted and failed) - DISCONNECT means work couldn't be attempted.
    ///
    /// **Typical usage**: Connection failures, network partitions, service unreachable, DNS failures

    DISCONNECT

  }

  /// The [Dimension] enum classifies the role in service interactions.
  ///
  /// Every sign in the Services API has two dimensions, representing the two fundamental
  /// roles in service-to-service interactions. This dual-dimension model enables tracking
  /// of both calling and being-called perspectives.
  ///
  /// ## The Two Dimensions
  ///
  /// | Dimension | Role              | Example                              |
  /// |-----------|-------------------|--------------------------------------|
  /// | CALLER    | Calling service   | "I am calling the remote service"    |
  /// | CALLEE    | Called service    | "I am serving the incoming request"  |
  ///
  /// ## CALLER vs CALLEE
  ///
  /// **CALLER** signals represent **actions taken when calling another service**:
  /// - Generated by the service initiating requests
  /// - Represents the client/requester role
  /// - Used for tracking outbound calls and dependencies
  /// - Forms the basis for distributed tracing client spans
  ///
  /// **CALLEE** signals represent **actions taken when serving requests**:
  /// - Generated by the service receiving and processing requests
  /// - Represents the server/handler role
  /// - Used for tracking inbound requests and work execution
  /// - Forms the basis for distributed tracing server spans
  ///
  /// ## Practical Examples
  ///
  /// ### Client Making a Call (CALLER perspective)
  /// ```java
  /// service.call(CALLER);         // I am calling the remote service
  /// var response = remoteService.process(request);
  /// if (response.status == 200) {
  ///   service.success(CALLER);    // My call completed successfully
  /// } else {
  ///   service.fail(CALLER);       // My call failed
  /// }
  /// ```
  ///
  /// ### Server Handling Request (CALLEE perspective)
  /// ```java
  /// public Response handleRequest(Request req) {
  ///   service.start(CALLEE);      // I am starting work on incoming request
  ///   try {
  ///     var result = processRequest(req);
  ///     service.success(CALLEE);  // I completed the work successfully
  ///     return result;
  ///   } catch (Exception e) {
  ///     service.fail(CALLEE);     // I failed to complete the work
  ///     throw e;
  ///   }
  /// }
  /// ```
  ///
  /// ### Full Request-Response Cycle
  /// ```java
  /// // Client side (CALLER)
  /// service.call(CALLER);              // Initiating call
  /// if (isOverloaded()) {
  ///   service.delay(CALLER);           // Delaying my call due to backpressure
  /// }
  ///
  /// // Server side (CALLEE)
  /// service.start(CALLEE);             // Starting work on received request
  /// service.success(CALLEE);           // Completed serving the request
  ///
  /// // Client side (CALLER)
  /// service.success(CALLER);           // My call succeeded
  /// ```
  ///
  /// ## Use in Distributed Systems
  ///
  /// The dual-dimension model enables:
  /// - **Distributed tracing**: CALLER spans for outbound calls, CALLEE spans for inbound requests
  /// - **Dependency tracking**: CALLER signals reveal service dependencies
  /// - **Service mesh observability**: Track both sides of service-to-service interactions
  /// - **Load balancing**: CALLEE signals show server-side load, CALLER signals show client-side behavior
  /// - **Circuit breakers**: Track CALLER failures to open circuit, track CALLEE rejections for backpressure

  public enum Dimension
    implements Category {

    /// Signals emitted by the service in the caller role (initiating requests).
    ///
    /// **CALLER** is used when the reporting service is making calls to other services.
    /// The service is acting as a client, initiating outbound requests and tracking
    /// the lifecycle of those calls.
    ///
    /// **Who is reporting**: The service making the outbound call
    /// **Mental model**: "I am calling another service"
    /// **Examples**: call(CALLER), retry(CALLER), success(CALLER) - tracking my outbound call
    /// **Usage**: Client-side spans, dependency tracking, circuit breaker metrics

    CALLER,

    /// Signals emitted by the service in the callee role (serving requests).
    ///
    /// **CALLEE** is used when the reporting service is handling incoming requests.
    /// The service is acting as a server, receiving inbound requests and tracking
    /// the work performed to serve them.
    ///
    /// **Who is reporting**: The service receiving and processing the inbound request
    /// **Mental model**: "I am handling an incoming request"
    /// **Examples**: start(CALLEE), success(CALLEE), fail(CALLEE) - tracking work I'm serving
    /// **Usage**: Server-side spans, load metrics, request handling

    CALLEE

  }


  /// A functional interface representing a function that returns a result and may throw a checked exception.
  ///
  /// @param <R> the return type of the function
  /// @param <T> the throwable class type that may be thrown

  @FunctionalInterface
  public interface Fn < R, T extends Throwable > {

    /// Creates a Fn instance, useful for resolving ambiguity with overloaded methods
    /// or for explicitly typing lambda expressions at compile-time.
    ///
    /// @param fn  the function to wrap
    /// @param <R> the return type of the [#eval()]
    /// @param <T> the throwable class type thrown by the operation
    /// @return The specified function as a Fn instance
    /// @throws NullPointerException if `fn` param is `null`

    static < R, T extends Throwable > Fn < R, T > of (
      @NotNull final Fn < R, T > fn
    ) {

      requireNonNull ( fn );

      return fn;

    }


    /// Invokes the underlying function.
    ///
    /// @return The result from calling of the function call
    /// @throws T The derived throwable type thrown

    R eval () throws T;

  }

  /// A functional interface representing an operation that returns no result and may throw a checked exception.
  ///
  /// @param <T> the throwable class type that may be thrown

  @FunctionalInterface
  public interface Op < T extends Throwable > {

    /// Creates an Op instance, useful for resolving ambiguity with overloaded methods
    /// or for explicitly typing lambda expressions at compile-time.
    ///
    /// @param op  the operation to wrap
    /// @param <T> the throwable class type thrown by the operation
    /// @return The specified operation as an Op instance
    /// @throws NullPointerException if `op` param is `null`

    static < T extends Throwable > Op < T > of (
      @NotNull final Op < T > op
    ) {

      requireNonNull ( op );

      return op;

    }


    /// Converts a [Fn] into an Op.
    ///
    /// @param fn  the [Fn] to be transformed
    /// @param <R> the return type of the function being converted
    /// @param <T> the throwable class type thrown by the operation
    /// @return An Op that wraps the function
    /// @throws NullPointerException if `fn` param is `null`

    @New
    @NotNull
    static < R, T extends Throwable > Op < T > of (
      @NotNull final Fn < R, ? extends T > fn
    ) {

      requireNonNull ( fn );

      return fn::eval;

    }


    /// Invokes the underlying operation.
    ///
    /// @throws T The derived throwable type thrown

    void exec () throws T;

  }

  /// The `Service` class represents a composition of one or more functions or operations.
  ///
  /// A service is a subject precept (instrument) that emits signals.
  ///
  /// ## Usage
  ///
  /// Use dimension-parameterized methods for all service lifecycle events:
  /// ```java
  /// service.start(CALLEE);     // Lifecycle signals with dimension
  /// service.success(CALLEE);
  /// service.fail(CALLEE);
  /// ```
  ///
  /// Services provide semantic methods that reflect the service lifecycle,
  /// making code more expressive and self-documenting.

  @Queued
  @Provided
  public static final class Service
    implements Signaler < Sign, Dimension > {

    private static final SignalSet < Sign, Dimension, Signal > SIGNALS =
      new SignalSet <> (
        Sign.class,
        Dimension.class,
        Signal::new
      );

    private final Pipe < ? super Signal > pipe;

    private Service (
      final Pipe < ? super Signal > pipe
    ) {

      this.pipe = pipe;

    }

    /// Emits a call sign from this service.
    ///
    /// @param dimension the role (CALLER or CALLEE)

    public void call (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.CALL,
          dimension
        )
      );

    }

    /// Emits a delay sign from this service.
    ///
    /// @param dimension the role (CALLER or CALLEE)

    public void delay (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.DELAY,
          dimension
        )
      );

    }

    /// Emits a discard sign from this service.
    ///
    /// @param dimension the role (CALLER or CALLEE)

    public void discard (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.DISCARD,
          dimension
        )
      );

    }

    /// Emits a disconnect sign from this service.
    ///
    /// @param dimension the role (CALLER or CALLEE)

    public void disconnect (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.DISCONNECT,
          dimension
        )
      );

    }

    /// A method that emits the appropriate signals for this service in the calling of a function.
    ///
    /// @param dimension the role (CALLER or CALLEE)
    /// @param fn        the function to be called
    /// @param <R>       the return type of the function
    /// @param <T>       the throwable class type
    /// @return The return value of the function
    /// @throws T                    the checked exception type of the function
    /// @throws NullPointerException if the function param is `null`

    public < R, T extends Throwable > R dispatch (
      @NotNull final Dimension dimension,
      @NotNull final Fn < R, T > fn
    ) throws T {

      requireNonNull ( fn );

      call ( dimension );

      try {

        final var result =
          fn.eval ();

        success ( dimension );

        return
          result;

      } catch (
        final Throwable t
      ) {

        fail ( dimension );

        throw t;

      }

    }

    /// A method that emits the appropriate signals for this service in the calling of an operation.
    ///
    /// @param dimension the role (CALLER or CALLEE)
    /// @param op        the operation to be called
    /// @param <T>       the throwable class type
    /// @throws T                    the checked exception type of the operation
    /// @throws NullPointerException if the operation param is `null`

    public < T extends Throwable > void dispatch (
      @NotNull final Dimension dimension,
      @NotNull final Op < T > op
    ) throws T {

      requireNonNull ( op );

      call ( dimension );

      try {

        op.exec ();

        success ( dimension );

      } catch (
        final Throwable t
      ) {

        fail ( dimension );

        throw t;

      }


    }

    /// A method that emits the appropriate signals for this service in the execution of a function.
    ///
    /// @param dimension the role (CALLER or CALLEE)
    /// @param fn        the function to be executed
    /// @param <R>       the return type of the function
    /// @param <T>       the throwable class type
    /// @return The return value of the function
    /// @throws T                    the checked exception type of the function
    /// @throws NullPointerException if the function param is `null`

    public < R, T extends Throwable > R execute (
      @NotNull final Dimension dimension,
      @NotNull final Fn < R, T > fn
    ) throws T {

      requireNonNull ( fn );

      start ( dimension );

      try {

        final var result =
          fn.eval ();

        success ( dimension );

        return
          result;

      } catch (
        final Throwable t
      ) {

        fail ( dimension );

        throw t;

      } finally {

        stop ( dimension );

      }

    }

    /// A method that emits the appropriate signals for this service in the execution of an operation.
    ///
    /// @param dimension the role (CALLER or CALLEE)
    /// @param op        the operation to be executed
    /// @param <T>       the throwable class type
    /// @throws T                    the checked exception type of the operation
    /// @throws NullPointerException if the operation param is `null`

    public < T extends Throwable > void execute (
      @NotNull final Dimension dimension,
      @NotNull final Op < T > op
    ) throws T {

      requireNonNull ( op );

      start ( dimension );

      try {

        op.exec ();

        success ( dimension );

      } catch (
        final Throwable t
      ) {

        fail ( dimension );

        throw t;

      } finally {

        stop ( dimension );

      }

    }

    /// Emits an expire sign from this service.
    ///
    /// @param dimension the role (CALLER or CALLEE)

    public void expire (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.EXPIRE,
          dimension
        )
      );

    }

    /// Emits a fail sign from this service.
    ///
    /// @param dimension the role (CALLER or CALLEE)

    public void fail (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.FAIL,
          dimension
        )
      );

    }

    /// Emits a recourse sign from this service.
    ///
    /// @param dimension the role (CALLER or CALLEE)

    public void recourse (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.RECOURSE,
          dimension
        )
      );

    }

    /// Emits a redirect sign from this service.
    ///
    /// @param dimension the role (CALLER or CALLEE)

    public void redirect (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.REDIRECT,
          dimension
        )
      );

    }

    /// Emits a reject sign from this service.
    ///
    /// @param dimension the role (CALLER or CALLEE)

    public void reject (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.REJECT,
          dimension
        )
      );

    }

    /// Emits a resume sign from this service.
    ///
    /// @param dimension the role (CALLER or CALLEE)

    public void resume (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.RESUME,
          dimension
        )
      );

    }

    /// Emits a retry sign from this service.
    ///
    /// @param dimension the role (CALLER or CALLEE)

    public void retry (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.RETRY,
          dimension
        )
      );

    }

    /// Emits a schedule sign from this service.
    ///
    /// @param dimension the role (CALLER or CALLEE)

    public void schedule (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.SCHEDULE,
          dimension
        )
      );

    }

    /// Signals a service event by composing sign and dimension.
    ///
    /// @param sign      the sign component
    /// @param dimension the dimension component

    @Override
    public void signal (
      @NotNull final Sign sign,
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          sign,
          dimension
        )
      );

    }

    /// Emits a start sign from this service.
    ///
    /// @param dimension the role (CALLER or CALLEE)

    public void start (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.START,
          dimension
        )
      );

    }


    /// Emits a stop sign from this service.
    ///
    /// @param dimension the role (CALLER or CALLEE)

    public void stop (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.STOP,
          dimension
        )
      );

    }

    /// Emits a success sign from this service.
    ///
    /// @param dimension the role (CALLER or CALLEE)

    public void success (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.SUCCESS,
          dimension
        )
      );

    }

    /// Emits a suspend sign from this service.
    ///
    /// @param dimension the role (CALLER or CALLEE)

    public void suspend (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.SUSPEND,
          dimension
        )
      );

    }

  }

  /// The [Signal] record represents a service signal composed of a sign and dimension.
  ///
  /// Signals are the composition of Sign (what happened) and Dimension (from whose perspective),
  /// enabling observation of service interactions from both self and observed perspectives.
  ///
  /// Note: We use the term `work` here to mean either (remote) call or (local) execution.
  ///
  /// @param sign      the service interaction classification
  /// @param dimension the perspective from which the signal is emitted

  @Provided
  public record Signal(
    Sign sign,
    Dimension dimension
  ) implements Serventis.Signal < Sign, Dimension > { }

}
