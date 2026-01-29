// Copyright (c) 2025 William David Louth
package io.humainary.substrates.ext.serventis.opt.flow;

import io.humainary.substrates.ext.serventis.api.Serventis;

import static io.humainary.substrates.ext.serventis.opt.flow.Valves.Sign.*;

/// # Valves API
///
/// The `Valves` API provides a structured framework for observing adaptive flow control
/// mechanisms from the instrument perspective. It enables emission of semantic signals
/// representing flow decisions, capacity adaptations, and overload responses, making
/// adaptive control behavior observable for understanding system dynamics, throttling
/// patterns, and capacity management.
///
/// ## Purpose
///
/// This API enables systems to emit **flow control signals** representing decisions made
/// by adaptive capacity controllers. By modeling valve operations as observable signs,
/// it enables pattern recognition for throttling intensity, adaptation effectiveness,
/// saturation detection, and control stability analysis.
///
/// ## Design Philosophy
///
/// Valves represents the instrument level - what flow control mechanisms (rate limiters,
/// auto-scalers, semaphores, bulkheads, token buckets) directly observe and do. The API
/// captures both flow decisions (PASS/DENY) and adaptive capacity changes (EXPAND/CONTRACT),
/// enabling observation of control loops without requiring pattern recognition at emission time.
///
/// ## Important: Observability vs Implementation
///
/// This API is for **observing flow control activity**, not implementing rate limiters or
/// admission controllers. When your system performs adaptive flow control (rate limiting,
/// auto-scaling, concurrency limiting), use this API to emit corresponding observability
/// signals about control decisions and adaptations. Observer agents can then reason about
/// throttling patterns, capacity dynamics, and control effectiveness without coupling to
/// your implementation details.
///
/// **Example**: When your rate limiter allows a request, call `valve.pass()`. When it
/// adapts by increasing the rate limit, call `valve.expand()`. These signals enable
/// meta-observability of adaptive control behavior.
///
/// ## Key Concepts
///
/// - **Valve**: A flow control mechanism that makes admission decisions and adapts capacity
/// - **Sign**: Operation or decision the valve makes: `PASS`, `DENY`, `EXPAND`, `CONTRACT`, `DROP`, `DRAIN`
/// - **Adaptive Control**: The valve adjusts its own capacity based on feedback (load, latency, errors)
/// - **Flow Decision**: Per-request outcome (pass or deny)
/// - **Capacity Adaptation**: Control action (expand or contract capacity)
///
/// ## Signs and Semantics
///
/// | Sign      | Description                                           | Category |
/// |-----------|-------------------------------------------------------|----------|
/// | `PASS`    | Request allowed through valve (flow decision)         | Flow     |
/// | `DENY`    | Request blocked by valve (flow decision)              | Flow     |
/// | `EXPAND`  | Capacity increased - valve opened more (adaptation)   | Control  |
/// | `CONTRACT`| Capacity decreased - valve closed more (adaptation)   | Control  |
/// | `DROP`    | Request dropped due to overload (emergency response)  | Overload |
/// | `DRAIN`   | Clearing backlog during recovery (recovery response)  | Recovery |
///
/// ## Use Cases
///
/// - Rate limiters (token bucket, leaky bucket, sliding window)
/// - Auto-scalers (Kubernetes HPA, AWS Auto Scaling)
/// - Concurrency limiters (semaphores, bulkheads)
/// - Adaptive admission control (load dropding, backpressure)
/// - Circuit breakers with dynamic thresholds
/// - Traffic shapers with adaptive rates
///
/// ## Relationship to Other APIs
///
/// `Valves` complements other Serventis APIs:
///
/// - **Resources API**: Resources manages pools of units (connections, permits); Valves manages flow rates
/// - **Services API**: Service failures may trigger Valves CONTRACT; recovery may trigger EXPAND
/// - **Statuses API**: Valve patterns (high DENY rate) inform Status conditions (SATURATED)
/// - **Queues API**: Queue OVERFLOW may trigger Valve DROP; Valve DRAIN correlates with Queue DEQUEUE
/// - **Tasks API**: High Task REJECT rate may trigger Valve CONTRACT
///
/// ## Adaptive Control Patterns
///
/// ### Auto-Scaler (Kubernetes HPA)
///
/// ```java
/// var valve = circuit.conduit(Valves::composer)
///   .percept(cortex.name("k8s.deployment.replicas"));
///
/// // Monitoring loop detects high CPU
/// if (avgCPU > 80%) {
///   scaleUp();
///   valve.expand();  // Adapted: increased replica count
/// }
///
/// // New requests
/// if (hasCapacity) {
///   valve.pass();    // Request served by available replica
/// } else {
///   valve.deny();    // No capacity available
/// }
/// ```
///
/// ### Adaptive Rate Limiter
///
/// ```java
/// var valve = circuit.conduit(Valves::composer)
///   .percept(cortex.name("api.ratelimit"));
///
/// // Monitors system health, adapts rate
/// if (p99Latency < target && errorRate < 1%) {
///   increaseRateLimit();
///   valve.expand();     // Healthy - allow more traffic
/// }
///
/// if (p99Latency > threshold) {
///   decreaseRateLimit();
///   valve.contract();   // Struggling - reduce load
/// }
///
/// // Per request
/// if (withinRateLimit) {
///   valve.pass();       // Request allowed
/// } else {
///   valve.deny();       // Rate limited
/// }
/// ```
///
/// ### Token Bucket with Dynamic Rate
///
/// ```java
/// var valve = circuit.conduit(Valves::composer)
///   .percept(cortex.name("gateway.tokens"));
///
/// // Adapt token generation rate
/// if (lowErrorRate && lowLatency) {
///   increaseTokenRate();
///   valve.expand();     // Generating tokens faster
/// }
///
/// // Per request
/// if (bucket.tryConsume()) {
///   valve.pass();       // Token consumed, request allowed
/// } else {
///   valve.deny();       // No tokens, request denied
/// }
/// ```
///
/// ### Circuit Breaker with Dynamic Thresholds
///
/// ```java
/// var valve = circuit.conduit(Valves::composer)
///   .percept(cortex.name("service.breaker"));
///
/// // Adapt error threshold based on history
/// if (recentStability) {
///   increaseThreshold();
///   valve.expand();     // More tolerant of errors
/// }
///
/// // Per request
/// if (circuitClosed && requestSucceeds) {
///   valve.pass();       // Request succeeded
/// } else if (circuitOpen) {
///   valve.deny();       // Circuit blocking all requests
///   if (errorThresholdExceeded) {
///     valve.contract(); // Reducing tolerance
///   }
/// }
/// ```
///
/// ### Bulkhead with Adaptive Partitions
///
/// ```java
/// var valve = circuit.conduit(Valves::composer)
///   .percept(cortex.name("bulkhead.critical"));
///
/// // Rebalance partition sizes based on usage
/// if (criticalUnderutilized && normalOverutilized) {
///   shrinkCriticalPartition();
///   valve.contract();   // Reduced critical partition
/// }
///
/// // Per request
/// if (partitionHasCapacity) {
///   valve.pass();       // Request accepted into partition
/// } else {
///   valve.deny();       // Partition full
/// }
/// ```
///
/// ### Load Shedder with Adaptive Thresholds
///
/// ```java
/// var valve = circuit.conduit(Valves::composer)
///   .percept(cortex.name("gateway.dropder"));
///
/// // Monitor queue depth, adapt drop threshold
/// if (sustainedLowQueue) {
///   decreaseShedThreshold();
///   valve.expand();     // Less aggressive dropding
/// }
///
/// if (queueExploding) {
///   increaseShedThreshold();
///   valve.contract();   // More aggressive dropding
/// }
///
/// // Per request
/// if (shouldShed) {
///   valve.drop();       // Dropping request
/// } else if (withinCapacity) {
///   valve.pass();       // Processing request
/// }
///
/// // Recovery
/// if (clearingBacklog) {
///   valve.drain();      // Processing queued requests
/// }
/// ```
///
/// ## Performance Considerations
///
/// Valve sign emissions operate at varying frequencies:
/// - **Flow decisions** (PASS/DENY): High frequency (100-100K ops/sec depending on system)
/// - **Adaptations** (EXPAND/CONTRACT): Low frequency (seconds to minutes between changes)
/// - **Emergency responses** (DROP/DRAIN): Variable (only during overload/recovery)
///
/// Zero-allocation enum emission with ~10-20ns cost for non-transit emits.
/// Signs flow asynchronously through the circuit's event queue.
/// Overhead is negligible compared to actual flow control operations.
///
/// ## Semiotic Ascent: Valves → Status → Situation
///
/// Valve signs translate upward into universal languages:
///
/// ### Valves → Status Translation
///
/// Pattern-based translation through subscriber observation:
///
/// | Valve Pattern | Status | Rationale |
/// |--------------|--------|-----------|
/// | High DENY rate | SATURATED | Capacity insufficient for demand |
/// | EXPAND activity | SCALING | System adapting to increased load |
/// | CONTRACT activity | CONSTRAINING | System protecting itself |
/// | EXPAND + PASS↑ | SCALING_EFFECTIVE | Adaptation meeting demand |
/// | EXPAND + DENY↑ | SCALING_INEFFECTIVE | Adaptation not meeting demand |
/// | Rapid EXPAND/CONTRACT | UNSTABLE | Control oscillating (hunting) |
/// | DROP activity | OVERLOAD | Emergency load dropding active |
/// | DRAIN activity | RECOVERING | Processing backlog |
///
/// ### Status → Situation Assessment
///
/// | Status Pattern | Situation | Example |
/// |---------------|-----------|---------|
/// | SATURATED (sustained) | CAPACITY | Insufficient capacity for demand |
/// | OVERLOAD (DROP active) | OUTAGE | System unable to serve traffic |
/// | UNSTABLE (oscillating) | INSTABILITY | Control loop hunting/thrashing |
/// | SCALING_EFFECTIVE | NORMAL | System adapting successfully |
///
/// This hierarchical meaning-making enables cross-domain reasoning: observers understand
/// adaptive control behavior's impact on service capacity and system stability without
/// needing to understand valve implementation details or control algorithms.
///
/// @author William David Louth
/// @since 1.0

public final class Valves
  implements Serventis {

  private Valves () { }

  /// A static composer function for creating Valve instruments.
  ///
  /// This method can be used as a method reference with conduits as follows:
  ///
  /// Example usage:
  /// ```java
  /// final var cortex = Substrates.cortex();
  /// var valve = circuit.conduit(Valves::composer).percept(cortex.name("api.ratelimit"));
  /// ```
  ///
  /// @param channel the channel from which to create the valve
  /// @return a new Valve instrument for the specified channel
  /// @throws NullPointerException if the channel param is `null`

  @New
  @NotNull
  public static Valve composer (
    @NotNull final Channel < ? super Sign > channel
  ) {

    return
      new Valve (
        channel.pipe ()
      );

  }

  /// A [Sign] represents an operation or decision made by an adaptive flow control valve.
  ///
  /// Signs are organized into functional categories representing different aspects
  /// of valve behavior:
  ///
  /// - **Flow Decisions**: PASS, DENY (per-request outcomes)
  /// - **Capacity Adaptations**: EXPAND, CONTRACT (control actions)
  /// - **Emergency Responses**: DROP (overload), DRAIN (recovery)
  ///
  /// ## Sign Categories
  ///
  /// Flow control valves make immediate decisions (PASS/DENY) while continuously adapting
  /// their capacity (EXPAND/CONTRACT) based on feedback from system conditions. During
  /// extreme conditions, they may drop load (DROP) or clear backlogs (DRAIN).

  public enum Sign
    implements Serventis.Sign {

    /// Indicates a request was allowed through the valve.
    ///
    /// PASS represents a flow decision where the valve permitted the request to proceed.
    /// This occurs when capacity is available, rate limits are not exceeded, and the
    /// valve is in an accepting state. High PASS rates indicate healthy flow through
    /// the valve. The ratio of PASS to DENY signals reveals valve utilization.
    ///
    /// **Typical usage**: Rate limit not exceeded, semaphore permit available, capacity exists
    ///
    /// **Pattern analysis**: Sustained PASS → healthy flow, PASS rate declining → capacity issues

    PASS,

    /// Indicates a request was blocked by the valve.
    ///
    /// DENY represents a flow decision where the valve rejected the request due to capacity
    /// limits, rate limits being exceeded, or protective throttling. High DENY rates indicate
    /// saturation or protective throttling. Rising DENY rates signal increasing backpressure.
    /// DENY followed by EXPAND indicates capacity adaptation in response to demand.
    ///
    /// **Typical usage**: Rate limit exceeded, no semaphore permits, capacity full
    ///
    /// **Pattern analysis**: High DENY rate → SATURATED, DENY + EXPAND → adapting to demand

    DENY,

    /// Indicates the valve increased its capacity.
    ///
    /// EXPAND represents an adaptive control decision where the valve opened further,
    /// increasing flow capacity. This occurs in response to sustained demand, healthy
    /// system metrics, or policy changes. EXPAND signals scaling actions such as increasing
    /// rate limits, adding semaphore permits, or scaling up resources. Multiple rapid
    /// EXPAND signals may indicate hunting behavior or aggressive adaptation.
    ///
    /// **Typical usage**: Auto-scaler adds instances, rate limiter increases limit,
    /// semaphore adds permits
    ///
    /// **Pattern analysis**: EXPAND + DENY↓ → effective adaptation, EXPAND + DENY↑ → insufficient

    EXPAND,

    /// Indicates the valve decreased its capacity.
    ///
    /// CONTRACT represents an adaptive control decision where the valve closed further,
    /// decreasing flow capacity. This occurs in response to system stress (high latency,
    /// errors), protective throttling, or policy changes. CONTRACT signals scaling actions
    /// such as decreasing rate limits, removing permits, or scaling down resources. CONTRACT
    /// followed by DENY indicates protective throttling is active.
    ///
    /// **Typical usage**: Auto-scaler removes instances, rate limiter decreases limit,
    /// circuit breaker trips
    ///
    /// **Pattern analysis**: CONTRACT + DENY↑ → protective throttling, rapid CONTRACT/EXPAND → hunting

    CONTRACT,

    /// Indicates a request was dropped due to overload.
    ///
    /// DROP represents an emergency response where the valve actively dropped a request
    /// rather than attempting to queue or process it. This occurs during severe overload
    /// conditions when even denying with potential retry would be insufficient. DROP is
    /// more extreme than DENY - it indicates load shedding is active. High DROP rates
    /// signal critical overload requiring immediate intervention.
    ///
    /// **Typical usage**: Queue full and dropping requests, aggressive load shedding active,
    /// system critically overloaded
    ///
    /// **Pattern analysis**: DROP activity → OVERLOAD status, DROP + CONTRACT → protecting system

    DROP,

    /// Indicates the valve is clearing backlog during recovery.
    ///
    /// DRAIN represents recovery activity where the valve is processing previously queued
    /// or deferred requests. This occurs after overload conditions subside and the system
    /// begins working through accumulated backlog. DRAIN signals recovery in progress.
    /// Sustained DRAIN followed by increasing PASS indicates successful recovery.
    ///
    /// **Typical usage**: Processing queued requests after overload, clearing circuit breaker
    /// half-open test queue, working through retry backlog
    ///
    /// **Pattern analysis**: DRAIN activity → RECOVERING status, DRAIN→PASS → recovery successful

    DRAIN

  }

  /// The `Valve` class represents a named, observable flow control mechanism from which
  /// signs are emitted.
  ///
  /// A valve is an adaptive flow control instrument that makes admission decisions (PASS/DENY),
  /// adapts its capacity (EXPAND/CONTRACT), and responds to extreme conditions (DROP/DRAIN).
  /// Valve signs make adaptive control behavior observable, enabling pattern recognition for
  /// throttling, saturation, control stability, and capacity management.
  ///
  /// ## Usage
  ///
  /// Use domain-specific methods for all valve operations:
  ///
  /// ```java
  /// // Flow decisions
  /// valve.pass();    // Request allowed
  /// valve.deny();    // Request blocked
  ///
  /// // Capacity adaptations
  /// valve.expand();   // Increased capacity
  /// valve.contract(); // Decreased capacity
  ///
  /// // Emergency responses
  /// valve.drop();     // Load dropding
  /// valve.drain();    // Clearing backlog
  /// ```

  @Queued
  @Provided
  public static final class Valve
    implements Signer < Sign > {

    private final Pipe < ? super Sign > pipe;

    private Valve (
      final Pipe < ? super Sign > pipe
    ) {

      this.pipe = pipe;

    }

    /// Emits a `CONTRACT` sign from this valve.
    ///
    /// Represents capacity decrease (adaptive control).

    public void contract () {

      pipe.emit (
        CONTRACT
      );

    }

    /// Emits a `DENY` sign from this valve.
    ///
    /// Represents request blocked (flow decision).

    public void deny () {

      pipe.emit (
        DENY
      );

    }

    /// Emits a `DRAIN` sign from this valve.
    ///
    /// Represents clearing backlog (recovery response).

    public void drain () {

      pipe.emit (
        DRAIN
      );

    }

    /// Emits a `DROP` sign from this valve.
    ///
    /// Represents load dropding (overload response).

    public void drop () {

      pipe.emit (
        DROP
      );

    }

    /// Emits an `EXPAND` sign from this valve.
    ///
    /// Represents capacity increase (adaptive control).

    public void expand () {

      pipe.emit (
        EXPAND
      );

    }

    /// Emits a `PASS` sign from this valve.
    ///
    /// Represents request allowed through (flow decision).

    public void pass () {

      pipe.emit (
        PASS
      );

    }

    /// Signs a valve event.
    ///
    /// @param sign the sign to make

    @Override
    public void sign (
      @NotNull final Sign sign
    ) {

      pipe.emit (
        sign
      );

    }

  }

}
