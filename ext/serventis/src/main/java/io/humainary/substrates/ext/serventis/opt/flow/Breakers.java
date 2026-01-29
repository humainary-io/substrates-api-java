// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.opt.flow;

import io.humainary.substrates.ext.serventis.api.Serventis;

import static io.humainary.substrates.ext.serventis.opt.flow.Breakers.Sign.*;

/// # Breakers API
///
/// The `Breakers` API provides a structured interface for observing circuit breaker
/// state transitions and behavior. It enables systems to emit **semantic signals**
/// representing breaker state changes, failure detection, and recovery testing.
///
/// ## Purpose
///
/// This API is designed to support **observability and reasoning** about resilience
/// patterns in distributed systems. By modeling circuit breaker behavior as composable
/// signals, it enables introspection of failure cascades, recovery patterns, and
/// protection mechanisms without coupling to specific implementation details.
///
/// ## Important: Reporting vs Implementation
///
/// This API is for **reporting circuit breaker semantics**, not implementing circuit
/// breakers. If you have an actual circuit breaker implementation (Resilience4j, Hystrix,
/// custom breaker, etc.), use this API to emit observability signals about state
/// transitions and operations. Observer agents can then reason about failure patterns,
/// cascading failures, and recovery effectiveness without coupling to your implementation
/// details.
///
/// **Example**: When your circuit breaker opens due to failures, call `breaker.open()`.
/// When testing recovery, call `breaker.halfOpen()`. When returning to normal, call
/// `breaker.close()`. The signals enable meta-observability: observing the resilience
/// mechanisms to understand system stability and failure propagation.
///
/// ## Key Concepts
///
/// - **Breaker**: A named subject that emits signs describing circuit breaker state transitions
/// - **Sign**: An enumeration of distinct states and events: `CLOSE`, `OPEN`, `HALF_OPEN`, etc.
/// - **Circuit Breaker Pattern**: A resilience pattern that prevents cascading failures by
///   breaking the circuit when failure thresholds are exceeded
///
/// ## Circuit Breaker State Machine
///
/// ```
/// CLOSED ────[failures exceed threshold]────> OPEN
///   ▲                                           │
///   │                                           │
///   │                                     [timeout expires]
///   │                                           │
///   │                                           ▼
///   └────[success]──── HALF_OPEN ◄────[test request]
///         │
///         └────[failure]────> OPEN
/// ```
///
/// ## Signs and Semantics
///
/// | Sign        | Description                                                    |
/// |-------------|----------------------------------------------------------------|
/// | `CLOSE`     | Circuit closed - traffic flowing normally                      |
/// | `OPEN`      | Circuit opened - traffic blocked, fail fast                    |
/// | `HALF_OPEN` | Circuit testing recovery - limited traffic allowed             |
/// | `TRIP`      | Failure threshold exceeded - circuit breaking now              |
/// | `PROBE`     | Test request sent in half-open state                           |
/// | `RESET`     | Circuit manually reset to closed state                         |
///
/// ## Semantic Distinctions
///
/// - **CLOSE**: Normal operational state - requests pass through
/// - **OPEN**: Protection state - requests fail immediately without attempting backend
/// - **HALF_OPEN**: Recovery testing state - sampling requests to detect recovery
/// - **TRIP**: The moment of transition when failure threshold is exceeded
/// - **PROBE**: Individual test attempt during half-open period
/// - **RESET**: Manual intervention to restore normal operation
///
/// ## Use Cases
///
/// - Monitoring cascading failure prevention
/// - Tracking service degradation and recovery
/// - Analyzing failure thresholds and recovery times
/// - Observing circuit breaker effectiveness in distributed systems
/// - Building adaptive resilience policies through observer agents
///
/// ## Relationship to Other APIs
///
/// `Breakers` signals integrate with other Serventis APIs:
///
/// - **Services API**: Service failures (FAIL) may trigger breaker TRIP and OPEN
/// - **Statuses API**: Breaker state (OPEN) may indicate DEGRADED or DOWN conditions
/// - **Resources API**: Resource DENY or TIMEOUT may correlate with breaker OPEN
/// - **Situations API**: Sustained OPEN state may escalate to WARNING or CRITICAL situations
/// - Observer agents translate breaker signals into health assessments and adaptive policies
///
/// ## Performance Considerations
///
/// Breaker sign emissions operate at coordination timescales (milliseconds to seconds).
/// State transitions are relatively infrequent compared to request rates, so emission
/// overhead is negligible. Zero-allocation enum emission with ~10-20ns cost for
/// non-transit emits. Signs flow asynchronously through the circuit's event queue.
///
/// @author William David Louth
/// @since 1.0

public final class Breakers
  implements Serventis {

  private Breakers () { }

  /// A static composer function for creating Breaker instruments.
  ///
  /// This method can be used as a method reference with conduits as follows:
  ///
  /// Example usage:
  /// ```java
  /// final var cortex = Substrates.cortex();
  /// var breaker = circuit.conduit(Breakers::composer).percept(cortex.name("api.breaker"));
  /// ```
  ///
  /// @param channel the channel from which to create the breaker
  /// @return a new Breaker instrument for the specified channel
  /// @throws NullPointerException if the channel param is `null`

  @New
  @NotNull
  public static Breaker composer (
    @NotNull final Channel < ? super Sign > channel
  ) {

    return
      new Breaker (
        channel.pipe ()
      );

  }

  /// A [Sign] represents the kind of state or event being observed in a circuit breaker.
  ///
  /// These signs distinguish between states (CLOSE, OPEN, HALF_OPEN) and events
  /// (TRIP, PROBE, RESET) in the circuit breaker lifecycle.

  public enum Sign
    implements Serventis.Sign {

    /// Indicates the circuit is closed and operating normally.
    ///
    /// In the CLOSE state, requests flow through to the backend service.
    /// This is the normal operational state where the protected resource
    /// is considered healthy and available.

    CLOSE,

    /// Indicates the circuit is open and blocking requests.
    ///
    /// In the OPEN state, requests fail immediately without attempting
    /// to reach the backend. This prevents cascading failures by failing
    /// fast when the protected resource is known to be unhealthy.

    OPEN,

    /// Indicates the circuit is half-open and testing recovery.
    ///
    /// In the HALF_OPEN state, a limited number of test requests are
    /// allowed through to determine if the backend has recovered.
    /// Success transitions to CLOSE; failure returns to OPEN.

    HALF_OPEN,

    /// Indicates the failure threshold was exceeded, triggering circuit break.
    ///
    /// TRIP represents the moment when accumulated failures exceed the
    /// configured threshold, causing the circuit to transition from CLOSE
    /// to OPEN. This is the critical event that activates protection.

    TRIP,

    /// Indicates a test request is being sent in half-open state.
    ///
    /// PROBE represents an individual request attempt during the HALF_OPEN
    /// phase. Multiple probes may be sent to determine with confidence
    /// whether the backend has recovered.

    PROBE,

    /// Indicates the circuit was manually reset to closed state.
    ///
    /// RESET represents operator intervention to force the circuit back
    /// to CLOSE state, bypassing normal recovery mechanisms. This is
    /// typically used when the operator knows the backend is healthy.

    RESET

  }

  /// The [Breaker] class represents a named, observable circuit breaker from which signs are emitted.
  ///
  /// ## Usage
  ///
  /// Use domain-specific methods: `breaker.close()`, `breaker.open()`, `breaker.halfOpen()`, etc.
  ///
  /// Breakers provide semantic methods for reporting circuit breaker state transition events.

  @Queued
  @Provided
  public static final class Breaker
    implements Signer < Sign > {

    private final Pipe < ? super Sign > pipe;

    private Breaker (
      final Pipe < ? super Sign > pipe
    ) {

      this.pipe = pipe;

    }

    /// Emits a close sign from this breaker.
    ///
    /// Indicates the circuit has transitioned to or remains in the closed state,
    /// allowing normal traffic flow.

    public void close () {

      pipe.emit (
        CLOSE
      );

    }

    /// Emits a half-open sign from this breaker.
    ///
    /// Indicates the circuit has entered the half-open state to test
    /// recovery by allowing limited traffic through.

    public void halfOpen () {

      pipe.emit (
        HALF_OPEN
      );

    }

    /// Emits an open sign from this breaker.
    ///
    /// Indicates the circuit has transitioned to the open state,
    /// blocking traffic to prevent cascading failures.

    public void open () {

      pipe.emit (
        OPEN
      );

    }

    /// Emits a probe sign from this breaker.
    ///
    /// Indicates a test request is being sent during the half-open
    /// state to determine if the backend has recovered.

    public void probe () {

      pipe.emit (
        PROBE
      );

    }

    /// Emits a reset sign from this breaker.
    ///
    /// Indicates the circuit has been manually reset to the closed state
    /// by operator intervention.

    public void reset () {

      pipe.emit (
        RESET
      );

    }

    /// Signs a breaker event.
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

    /// Emits a trip sign from this breaker.
    ///
    /// Indicates the failure threshold has been exceeded, triggering
    /// the circuit to break and transition to the open state.

    public void trip () {

      pipe.emit (
        TRIP
      );

    }

  }

}
