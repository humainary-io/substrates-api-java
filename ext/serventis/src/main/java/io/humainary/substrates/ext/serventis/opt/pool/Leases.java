// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.opt.pool;

import io.humainary.substrates.ext.serventis.api.Serventis;
import io.humainary.substrates.ext.serventis.sdk.SignalSet;

/// # Leases API
///
/// The `Leases` API provides a comprehensive framework for observing time-bounded resource
/// ownership and distributed coordination through **semantic signal emission**. It enables
/// fine-grained instrumentation of lease acquisition, renewal, expiration, and revocation
/// patterns in distributed systems.
///
/// ## Purpose
///
/// This API enables systems to emit **rich semantic signals** about lease lifecycle events,
/// capturing the full lifecycle of time-bounded ownership: acquisition requests, grants,
/// denials, renewals, extensions, releases, expirations, and forced revocations. The dual-dimension
/// model (LESSOR/LESSEE) enables both authority perspective and client perspective observation.
///
/// ## Important: Reporting vs Implementation
///
/// This API is for **reporting lease semantics**, not implementing lease managers.
/// If you have an actual lease implementation (etcd leases, Zookeeper ephemeral nodes,
/// coordination service, distributed lock manager, etc.), use this API to emit observability
/// signals about lease operations. Observer agents can then reason about lease patterns,
/// contention, failure modes, and coordination effectiveness without coupling to your
/// implementation details.
///
/// **Example**: When your coordination service grants a leadership lease, call
/// `lease.grant(LESSOR)`. When a client acquires the lease, call `lease.acquire(LESSEE)`.
/// When the TTL expires, call `lease.expire(LESSOR)`. The signals enable meta-observability
/// of lease-based coordination patterns.
///
/// ## Key Concepts
///
/// - **Lease**: A time-bounded ownership grant with automatic expiration (TTL)
/// - **Signal**: A semantic event combining a **Sign** (what happened) and **Dimension** (perspective)
/// - **Sign**: The type of lease operation (ACQUIRE, GRANT, RENEW, EXPIRE, etc.)
/// - **Dimension**: The lease perspective (LESSOR = authority, LESSEE = client)
/// - **TTL**: Time-to-live duration after which the lease automatically expires
/// - **Lessor**: The authority granting leases (coordination service, lock manager)
/// - **Lessee**: The client holding or requesting leases (application instance)
///
/// ## Dual-Dimension Model
///
/// Every sign has two dimensions representing different perspectives in the lease relationship:
///
/// | Dimension | Perspective         | Role                    | Example Signals            |
/// |-----------|---------------------|-------------------------|----------------------------|
/// | LESSOR    | Authority (acting)  | Grants/revokes leases   | GRANT, DENY, REVOKE, EXPIRE|
/// | LESSEE    | Client (requesting) | Acquires/holds leases   | ACQUIRE, RENEW, RELEASE    |
///
/// **LESSOR** signals indicate actions by the lease authority (granting, denying, revoking)
/// while **LESSEE** signals indicate actions by the lease holder (acquiring, renewing, releasing).
/// This enables observation of both authority decisions and client behavior.
///
/// ## Lease Lifecycle
///
/// ```
/// Request:  ACQUIRE (lessee) → GRANT (lessor) | DENY (lessor)
///              ↓
/// Holding:  [lease active with TTL]
///              ↓
/// Extension: RENEW (lessee) → EXTEND (lessor)
///              ↓
/// Termination: RELEASE (lessee)     [voluntary]
///          OR: EXPIRE (lessor)       [TTL exhausted]
///          OR: REVOKE (lessor)       [forced revocation]
///              ↓
/// Monitoring: PROBE (lessor/lessee) [status check]
/// ```
///
/// ## Signal Categories
///
/// The API defines signals across lease lifecycle phases:
///
/// ### Acquisition Phase
/// - **ACQUIRE**: Client attempts to obtain new lease
/// - **GRANT**: Lease successfully granted by authority
/// - **DENY**: Lease request denied (resource locked, policy violation)
///
/// ### Renewal Phase
/// - **RENEW**: Holder attempts to extend lease duration
/// - **EXTEND**: Lease duration successfully extended by authority
///
/// ### Termination Phase
/// - **RELEASE**: Holder voluntarily terminates lease before expiration
/// - **EXPIRE**: Lease automatically terminated (TTL exhausted)
/// - **REVOKE**: Lease forcefully revoked by authority or system
///
/// ### Monitoring Phase
/// - **PROBE**: Status check on lease validity or holder identity
///
/// ## The 9 Lease Signs
///
/// | Sign      | Description                                                      |
/// |-----------|------------------------------------------------------------------|
/// | `ACQUIRE` | Client attempting to obtain new lease                            |
/// | `GRANT`   | Lease successfully granted (positive ACQUIRE outcome)            |
/// | `DENY`    | Lease request denied (negative ACQUIRE outcome)                  |
/// | `RENEW`   | Holder attempting to extend lease duration                       |
/// | `EXTEND`  | Lease duration successfully extended (positive RENEW outcome)    |
/// | `RELEASE` | Holder voluntarily terminated lease before expiration            |
/// | `EXPIRE`  | Lease automatically terminated (TTL exhausted)                   |
/// | `REVOKE`  | Lease forcefully revoked by authority                            |
/// | `PROBE`   | Status check on lease (validity, holder identity)                |
///
/// ## Use Cases
///
/// ### Leader Election
/// ```java
/// // Candidate requests leadership lease
/// lease.acquire(LESSEE);
/// lease.grant(LESSOR);     // Authority grants lease
/// lease.renew(LESSEE);     // Leader maintains lease via heartbeat
/// lease.extend(LESSOR);    // Authority extends TTL
/// lease.release(LESSEE);   // Leader steps down voluntarily
/// ```
///
/// ### Distributed Locking
/// ```java
/// // Client requests exclusive lock
/// lease.acquire(LESSEE);
/// lease.deny(LESSOR);      // Lock held by another
/// lease.acquire(LESSEE);   // Retry
/// lease.grant(LESSOR);     // Lock acquired
/// lease.release(LESSEE);   // Work complete, unlock
/// ```
///
/// ### Resource Reservation
/// ```java
/// // Reserve resource for duration
/// lease.acquire(LESSEE);
/// lease.grant(LESSOR);
/// lease.renew(LESSEE);     // Need more time
/// lease.extend(LESSOR);    // Extended
/// lease.expire(LESSOR);    // Reservation timeout
/// ```
///
/// ### Split-Brain Prevention
/// ```java
/// lease.grant(LESSOR);      // Node A becomes leader
/// // Network partition
/// lease.expire(LESSOR);     // Node A lease expires (no heartbeat)
/// lease.grant(LESSOR);      // Node B becomes new leader
/// lease.revoke(LESSOR);     // System detects Node A still claims leadership
/// ```
///
/// ## Relationship to Other APIs
///
/// `Leases` integrates with other Serventis APIs:
///
/// - **Services API**: Service availability may require active lease (GRANT → service can START)
/// - **Resources API**: Lease controls resource access (GRANT lease → ACQUIRE resource)
/// - **Locks API**: Distributed locks often backed by leases (lease EXPIRE → lock ABANDON)
/// - **Latches API**: Distributed latches may use leases for participant tracking (lease EXPIRE → latch ABANDON)
/// - **Transactions API**: Leases can protect transaction resources (EXPIRE → ROLLBACK)
/// - **Statuses API**: Lease expiration patterns inform conditions (many EXPIRE → DEGRADED)
/// - **Agents API**: Agents may promise lease availability, depend on lease grants
///
/// ## Request/Response Pairing
///
/// The API uses elegant request/response pairs:
///
/// - **ACQUIRE** (request) → **GRANT** (success) | **DENY** (failure)
/// - **RENEW** (request) → **EXTEND** (success)
///
/// This makes causality explicit: every GRANT/DENY follows an ACQUIRE,
/// every EXTEND follows a RENEW.
///
/// ## Performance Considerations
///
/// Lease sign emissions operate at coordination timescales (milliseconds to seconds).
/// Heartbeat-based renewals may be frequent (e.g., every 5 seconds), so emission
/// overhead should be minimal. Zero-allocation enum emission with ~10-20ns cost
/// for non-transit emits. Signs flow asynchronously through the circuit's event queue.
///
/// @author William David Louth
/// @since 1.0

public final class Leases
  implements Serventis {

  private Leases () { }

  /// A static composer function for creating Lease instruments.
  ///
  /// This method can be used as a method reference with conduits as follows:
  ///
  /// Example usage:
  /// ```java
  /// final var cortex = Substrates.cortex();
  /// var lease = circuit.conduit(Leases::composer).percept(cortex.name("leadership"));
  /// ```
  ///
  /// @param channel the channel from which to create the lease
  /// @return a new Lease instrument for the specified channel
  /// @throws NullPointerException if the channel param is `null`

  @New
  @NotNull
  public static Lease composer (
    @NotNull final Channel < ? super Signal > channel
  ) {

    return
      new Lease (
        channel.pipe ()
      );

  }

  /// A [Dimension] represents the perspective or role in a lease relationship.
  ///
  /// These dimensions distinguish between the authority granting leases (LESSOR)
  /// and the client requesting/holding leases (LESSEE).

  public enum Dimension
    implements Category {

    /// The lease authority perspective.
    ///
    /// LESSOR represents the coordination service, lock manager, or other
    /// authority that grants, denies, extends, and revokes leases.
    /// LESSOR signals indicate actions by the lease authority.

    LESSOR,

    /// The lease client perspective.
    ///
    /// LESSEE represents the application instance, service node, or other
    /// client that requests, holds, renews, and releases leases.
    /// LESSEE signals indicate actions by the lease holder.

    LESSEE

  }

  /// A [Sign] represents the kind of operation being performed in a lease lifecycle.
  ///
  /// These signs distinguish between acquisition (ACQUIRE, GRANT, DENY),
  /// renewal (RENEW, EXTEND), termination (RELEASE, EXPIRE, REVOKE),
  /// and monitoring (PROBE) operations.

  public enum Sign
    implements Serventis.Sign {

    /// Indicates a client is attempting to obtain a new lease.
    ///
    /// ACQUIRE represents the start of a lease request. The client is
    /// requesting time-bounded ownership of a resource or role.
    /// This is typically followed by GRANT or DENY from the lessor.

    ACQUIRE,

    /// Indicates a lease request has been denied.
    ///
    /// DENY is the negative outcome of an ACQUIRE attempt. The resource
    /// may already be locked by another lessee, or the request may violate
    /// policy constraints. This informs the client to retry or back off.

    DENY,

    /// Indicates the lease duration has been successfully extended.
    ///
    /// EXTEND is the positive outcome of a RENEW attempt. The lease authority
    /// has granted additional time to the holder, extending the TTL. The lease
    /// remains active with a refreshed expiration time.

    EXTEND,

    /// Indicates the lease has automatically terminated due to TTL exhaustion.
    ///
    /// EXPIRE represents timeout-based termination. The holder failed to renew
    /// before the TTL expired, causing automatic release. This is different from
    /// RELEASE (voluntary) and REVOKE (forced).

    EXPIRE,

    /// Indicates the lease has been successfully granted.
    ///
    /// GRANT is the positive outcome of an ACQUIRE attempt. The lease authority
    /// has approved the request and granted time-bounded ownership. The lessee
    /// now holds the lease for the specified TTL.

    GRANT,

    /// Indicates a status check is being performed on a lease.
    ///
    /// PROBE represents a query about lease validity, holder identity, or
    /// remaining TTL. This can be lessor-initiated (checking liveness) or
    /// lessee-initiated (confirming lease status).

    PROBE,

    /// Indicates the holder voluntarily terminated the lease before expiration.
    ///
    /// RELEASE represents intentional lease return by the holder. The work
    /// is complete, and the holder no longer needs the resource. This is
    /// different from EXPIRE (timeout) and REVOKE (forced).

    RELEASE,

    /// Indicates the holder is attempting to extend the lease duration.
    ///
    /// RENEW represents a request to refresh the TTL before expiration.
    /// This is typically a heartbeat mechanism to maintain leadership or
    /// lock ownership. Usually followed by EXTEND from the lessor.

    RENEW,

    /// Indicates the lease authority has forcefully revoked a lease.
    ///
    /// REVOKE represents involuntary termination by the system or authority,
    /// not by the holder. This occurs during split-brain scenarios, policy
    /// violations, or administrative actions. Different from EXPIRE (timeout)
    /// and RELEASE (voluntary).

    REVOKE

  }

  /// The [Lease] class represents a named, observable lease from which signals are emitted.
  ///
  /// ## Usage
  ///
  /// Use domain-specific methods with dimensions: `lease.acquire(LESSEE)`, `lease.grant(LESSOR)`, etc.
  ///
  /// Leases provide semantic methods for reporting lease operation events from both
  /// lessor (authority) and lessee (client) perspectives.

  @Queued
  @Provided
  public static final class Lease
    implements Signaler < Sign, Dimension > {

    private static final SignalSet < Sign, Dimension, Signal > SIGNALS =
      new SignalSet <> (
        Sign.class,
        Dimension.class,
        Signal::new
      );

    private final Pipe < ? super Signal > pipe;

    private Lease (
      final Pipe < ? super Signal > pipe
    ) {

      this.pipe = pipe;

    }

    /// Emits an acquire sign from this lease.
    ///
    /// @param dimension the perspective (LESSOR or LESSEE)

    public void acquire (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.ACQUIRE,
          dimension
        )
      );

    }

    /// Emits a deny sign from this lease.
    ///
    /// @param dimension the perspective (LESSOR or LESSEE)

    public void deny (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.DENY,
          dimension
        )
      );

    }

    /// Emits an expire sign from this lease.
    ///
    /// @param dimension the perspective (LESSOR or LESSEE)

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

    /// Emits an extend sign from this lease.
    ///
    /// @param dimension the perspective (LESSOR or LESSEE)

    public void extend (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.EXTEND,
          dimension
        )
      );

    }

    /// Emits a grant sign from this lease.
    ///
    /// @param dimension the perspective (LESSOR or LESSEE)

    public void grant (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.GRANT,
          dimension
        )
      );

    }

    /// Emits a probe sign from this lease.
    ///
    /// @param dimension the perspective (LESSOR or LESSEE)

    public void probe (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.PROBE,
          dimension
        )
      );

    }

    /// Emits a release sign from this lease.
    ///
    /// @param dimension the perspective (LESSOR or LESSEE)

    public void release (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.RELEASE,
          dimension
        )
      );

    }

    /// Emits a renew sign from this lease.
    ///
    /// @param dimension the perspective (LESSOR or LESSEE)

    public void renew (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.RENEW,
          dimension
        )
      );

    }

    /// Emits a revoke sign from this lease.
    ///
    /// @param dimension the perspective (LESSOR or LESSEE)

    public void revoke (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.REVOKE,
          dimension
        )
      );

    }

    /// Signals a lease event.
    ///
    /// @param sign      the sign to make
    /// @param dimension the dimension from which to sign

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

  }

  /// Represents a signal combining a [Sign] and [Dimension].
  ///
  /// Signal instances are created by combining signs with dimensions to
  /// represent specific lease events from specific perspectives.

  public record Signal(
    Sign sign,
    Dimension dimension
  ) implements Serventis.Signal < Sign, Dimension > { }

}
