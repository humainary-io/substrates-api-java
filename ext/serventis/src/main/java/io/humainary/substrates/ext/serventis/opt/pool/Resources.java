// Copyright (c) 2025 William David Louth
package io.humainary.substrates.ext.serventis.opt.pool;

import io.humainary.substrates.ext.serventis.api.Serventis;

import static io.humainary.substrates.ext.serventis.opt.pool.Resources.Sign.*;

/// # Resources API
///
/// The `Resources` API provides a structured framework for observing resource acquisition and release
/// patterns within systems. It enables emission of semantic signals that describe interactions with
/// **named resources** such as connections, semaphores, permits, locks, or any bounded capacity.
///
/// ## Purpose
///
/// This API enables systems to **observe** resource interactions without controlling them. Signals
/// report what is happening to or around a resource from either the **requester's perspective**
/// (requesting access) or the **provider's perspective** (granting or denying access).
///
/// ## Important: Reporting vs Implementation
///
/// This API is for **reporting resource interaction semantics**, not implementing resource managers.
/// If you have actual resource management implementations (connection pools, semaphores, rate limiters,
/// permit systems, etc.), use this API to emit observability signals about operations performed.
/// Observer agents can then reason about resource contention, utilization patterns, and capacity
/// dynamics without coupling to your resource management implementation details.
///
/// **Example**: Your connection pool attempts to acquire a connection. If no connections are
/// available, call `resource.attempt()` followed by `resource.deny()`. When a connection is
/// later released back to the pool, call `resource.release()`. The signals enable meta-observability:
/// observing resource interactions themselves to understand contention patterns and capacity planning needs.
///
/// ## Key Concepts
///
/// - **Resource**: A named entity with bounded capacity that can be requested, granted, and released
/// - **Sign**: The type of interaction (ATTEMPT, ACQUIRE, GRANT, DENY, TIMEOUT, RELEASE)
///
/// ## Resource Interaction Patterns
///
/// Resources support both **non-blocking** and **blocking** acquisition patterns:
///
/// | Pattern      | Request Sign | Success Sign | Failure Signs    | Typical Use Case          |
/// |--------------|--------------|--------------|------------------|---------------------------|
/// | Non-blocking | ATTEMPT      | GRANT        | DENY             | Try-acquire, fast-fail    |
/// | Blocking     | ACQUIRE      | GRANT        | TIMEOUT          | Wait-acquire, guaranteed  |
/// | Release      | RELEASE      | -            | -                | Return units to pool      |
///
/// ## Relationship to Other APIs
///
/// `Resources` signals inform higher-level observability:
///
/// - **Locks API**: Locks ARE resources - vocabulary aligned (ATTEMPT, ACQUIRE, GRANT, DENY, TIMEOUT, RELEASE)
/// - **Latches API**: Latches ARE coordination resources - vocabulary aligned (AWAIT, RELEASE, TIMEOUT)
/// - **Queues API**: Resource pools share similar capacity/demand dynamics with bounded queues
/// - **Statuses API**: High DENY or TIMEOUT rates may indicate DEGRADED or DEFECTIVE conditions
/// - **Services API**: Resource exhaustion can trigger DELAY, REJECT, or FAIL service signals
/// - **Situations API**: Resource pressure patterns can elevate situations from NORMAL to WARNING
///
/// ## Semantic Perspectives
///
/// Signals can be emitted from different perspectives:
///
/// - **Requester perspective**: ATTEMPT, ACQUIRE, RELEASE (what I'm doing)
/// - **Provider perspective**: GRANT, DENY, TIMEOUT (what happened to the request)
/// - **Complete story**: Emit both request and outcome for full observability
///
/// ## Performance Considerations
///
/// Resource sign emissions are designed for high-frequency operation (10M-50M Hz).
/// Zero-allocation enum emission with ~10-20ns cost for non-transit emits.
/// Signs flow asynchronously through the circuit's event queue.
///
/// @author William David Louth
/// @since 1.0

public final class Resources
  implements Serventis {

  private Resources () { }

  /// A static composer function for creating Resource instruments.
  ///
  /// This method can be used as a method reference with conduits as follows:
  ///
  /// Example usage:
  /// ```java
  /// final var cortex = Substrates.cortex();
  /// var resource = circuit.conduit(Resources::composer).percept(cortex.name("db.connections"));
  /// ```
  ///
  /// @param channel the channel from which to create the resource
  /// @return a new Resource instrument for the specified channel
  /// @throws NullPointerException if the channel param is `null`

  @New
  @NotNull
  public static Resource composer (
    @NotNull final Channel < ? super Sign > channel
  ) {

    return
      new Resource (
        channel.pipe ()
      );

  }

  /// A [Sign] represents the kind of action being observed in a resource interaction.
  ///
  /// Signs form a lifecycle describing resource acquisition and release:
  ///
  /// ```
  /// Request Phase: ATTEMPT or ACQUIRE
  ///       ↓
  /// Outcome Phase: GRANT (success) or DENY/TIMEOUT (failure)
  ///       ↓
  /// Release Phase: RELEASE
  /// ```
  ///
  /// The choice between ATTEMPT and ACQUIRE signals different concurrency strategies,
  /// while outcome signs reveal capacity pressure and availability.

  public enum Sign
    implements Serventis.Sign {

    /// A non-blocking request for units from a resource.
    ///
    /// ATTEMPT represents an optimistic acquisition strategy where the caller is
    /// prepared to handle immediate denial without waiting. Common in latency-sensitive
    /// paths, fallback strategies, or when alternative actions exist on denial.
    ///
    /// **Expected outcomes**: GRANT (immediate success) or DENY (immediate failure)

    ATTEMPT,

    /// A blocking or wait-based request for units from a resource.
    ///
    /// ACQUIRE represents a committed acquisition strategy where the caller will wait
    /// for resource availability, possibly with a timeout. Common when the resource is
    /// essential for operation and no alternative exists.
    ///
    /// **Expected outcomes**: GRANT (success after wait) or TIMEOUT (wait exceeded limit)

    ACQUIRE,

    /// The successful granting of a request for units from a resource.
    ///
    /// GRANT indicates resource units were successfully allocated to the requester.
    /// This can be immediate (after ATTEMPT) or delayed (after ACQUIRE). High grant
    /// rates indicate healthy resource availability. The time between request and grant
    /// signals reveals wait times.

    GRANT,

    /// The denial of a request due to insufficient resource capacity.
    ///
    /// DENY indicates resource units were unavailable and the non-blocking request
    /// (ATTEMPT) was immediately rejected. High denial rates indicate resource saturation,
    /// undersized capacity, or bursty demand patterns. Sustained denials may trigger
    /// capacity scaling or request throttling.

    DENY,

    /// The expiration of a blocking request due to wait time exceeding configured limits.
    ///
    /// TIMEOUT indicates resource units did not become available within the acceptable
    /// wait period. Timeouts often indicate sustained resource exhaustion, deadlock
    /// conditions, or aggressive timeout configurations. Timeouts may trigger circuit
    /// breakers or service degradation modes.

    TIMEOUT,

    /// The return of previously granted resource units back to the resource pool.
    ///
    /// RELEASE indicates resource units are being returned and becoming available for
    /// other requesters. Tracking time between GRANT and RELEASE reveals resource hold
    /// times. Low release rates compared to grants indicate resource leaks or long-held
    /// resources.

    RELEASE

  }

  /// A [Resource] represents an observable entity whose state can be influenced through acquisition
  /// and release patterns. A resource emits [Sign] values describing those patterns.
  ///
  /// ## Usage
  ///
  /// Use domain-specific methods: `resource.acquire()`, `resource.grant()`, `resource.release()`
  ///
  /// Resources provide semantic methods for tracking resource lifecycle events.

  @Queued
  @Provided
  public static final class Resource
    implements Signer < Sign > {

    private final Pipe < ? super Sign > pipe;

    private Resource (
      final Pipe < ? super Sign > pipe
    ) {

      this.pipe = pipe;

    }

    /// Emits an `ACQUIRE` sign from this resource.
    ///
    /// Represents a blocking or wait-based request from the resource.

    public void acquire () {

      pipe.emit (
        ACQUIRE
      );

    }

    /// Emits an `ATTEMPT` sign from this resource.
    ///
    /// Represents a non-blocking request from the resource.

    public void attempt () {

      pipe.emit (
        ATTEMPT
      );

    }

    /// Emits a `DENY` sign from this resource.
    ///
    /// Represents the denial of a request from the resource.

    public void deny () {

      pipe.emit (
        DENY
      );

    }

    /// Emits a `GRANT` sign from this resource.
    ///
    /// Represents the granting of a request from this resource.

    public void grant () {

      pipe.emit (
        GRANT
      );

    }

    /// Emits a `RELEASE` sign from this resource.
    ///
    /// Represents the returning of units previously granted by this resource.

    public void release () {

      pipe.emit (
        RELEASE
      );

    }

    /// Signs a resource event.
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

    /// Emits a `TIMEOUT` sign from this resource.
    ///
    /// Represents the timing out of a request to this resource.

    public void timeout () {

      pipe.emit (
        TIMEOUT
      );

    }

  }

}
