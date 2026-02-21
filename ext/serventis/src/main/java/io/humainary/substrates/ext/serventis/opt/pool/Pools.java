// Copyright (c) 2025 William David Louth
package io.humainary.substrates.ext.serventis.opt.pool;

import io.humainary.substrates.ext.serventis.api.Serventis;

import static io.humainary.substrates.ext.serventis.opt.pool.Pools.Sign.*;

/// # Pools API
///
/// The `Pools` API provides a structured framework for observing resource pool dynamics
/// from the pool's perspective. It enables emission of semantic signals representing
/// capacity changes and utilization patterns, making pool behavior observable for
/// understanding resource management, capacity planning, and leak detection.
///
/// ## Purpose
///
/// This API enables systems to emit **pool management signals** representing capacity
/// adjustments and resource utilization. By modeling pool operations as observable signs,
/// it enables pattern recognition for pool saturation, resource leaks, capacity oscillation,
/// and utilization efficiency analysis.
///
/// ## Design Philosophy
///
/// Pools represents the pool's perspective - what the pool directly observes about its own
/// capacity and utilization. The API captures both capacity management (EXPAND/CONTRACT) and
/// resource movement (BORROW/RECLAIM), enabling observation of pool dynamics without
/// requiring pattern recognition at emission time.
///
/// ## Important: Observability vs Implementation
///
/// This API is for **observing pool activity**, not implementing connection pools or
/// resource managers. When your system manages a pool of resources (JDBC connections,
/// threads, objects, buffers), use this API to emit corresponding observability signals
/// about capacity changes and resource utilization. Observer agents can then reason about
/// pool health, efficiency, and potential issues without coupling to your implementation
/// details.
///
/// **Example**: When your connection pool creates new connections, call `pool.expand()`.
/// When a connection is borrowed, call `pool.borrow()`. These signals enable meta-
/// observability of pool behavior.
///
/// ## Key Concepts
///
/// - **Pool**: A managed collection of reusable resources with dynamic capacity
/// - **Sign**: Operation the pool observes: `EXPAND`, `CONTRACT`, `BORROW`, `RECLAIM`
/// - **Capacity**: Total number of resources the pool can manage (changes via EXPAND/CONTRACT)
/// - **Utilization**: Number of resources currently borrowed (changes via BORROW/RECLAIM)
/// - **Availability**: Capacity minus utilization (derived by subscribers)
///
/// ## Signs and Semantics
///
/// | Sign      | Description                                      | Category    |
/// |-----------|--------------------------------------------------|-------------|
/// | `EXPAND`  | Pool capacity increased (new resources created)  | Capacity    |
/// | `CONTRACT`| Pool capacity decreased (resources destroyed)    | Capacity    |
/// | `BORROW`  | Resource borrowed from pool (utilization ↑)      | Utilization |
/// | `RECLAIM` | Resource reclaimed by pool (utilization ↓)       | Utilization |
///
/// ## Use Cases
///
/// - JDBC connection pools (HikariCP, c3p0, DBCP)
/// - Thread pools (ExecutorService, ForkJoinPool)
/// - Object pools (Apache Commons Pool)
/// - Memory pools (buffer pools, arena allocators)
/// - Socket pools (HTTP client connection pools)
/// - Channel pools (gRPC, Netty)
///
/// ## Relationship to Other APIs
///
/// `Pools` complements other Serventis APIs:
///
/// - **Resources API**: Resources manages acquisition requests (ATTEMPT/GRANT/DENY); Pools manages capacity and utilization
/// - **Tasks API**: Task REJECT may correlate with Pool BORROW when pool is saturated
/// - **Statuses API**: Pool patterns (high BORROW) inform Status conditions (SATURATED)
/// - **Valves API**: Pool capacity changes may trigger Valve adaptations (EXPAND/CONTRACT)
/// - **Processes API**: Process SPAWN may trigger Pool EXPAND; Process STOP may trigger Pool CONTRACT
///
/// ## Performance Considerations
///
/// Pool sign emissions operate at varying frequencies:
/// - **Utilization changes** (BORROW/RECLAIM): High frequency (10-10K ops/sec per pool)
/// - **Capacity changes** (EXPAND/CONTRACT): Low frequency (seconds to minutes between changes)
///
/// Zero-allocation enum emission with ~10-20ns cost for non-transit emits.
/// Signs flow asynchronously through the circuit's event queue.
/// Overhead is negligible compared to actual pool operations (connection creation, validation).
///
/// ## Semiotic Ascent: Pools → Status → Situation
///
/// Pool signs translate upward into universal languages:
///
/// ### Pools → Status Translation
///
/// Pattern-based translation through subscriber observation:
///
/// | Pool Pattern              | Status      | Rationale                              |
/// |---------------------------|-------------|----------------------------------------|
/// | High BORROW, low RECLAIM  | SATURATED   | High utilization, limited availability |
/// | BORROW without RECLAIM    | LEAKING     | Resources not returned (leak)          |
/// | Rapid EXPAND/CONTRACT     | UNSTABLE    | Capacity oscillating                   |
/// | EXPAND activity           | SCALING     | Pool adapting to demand                |
/// | High BORROW rate          | BUSY        | Pool heavily utilized                  |
/// | CONTRACT to minimum       | IDLE        | Pool at minimum capacity               |
/// | EXPAND failures           | CONSTRAINED | Cannot expand (resource limits)        |
///
/// ### Status → Situation Assessment
///
/// | Status Pattern       | Situation   | Example                                    |
/// |----------------------|-------------|--------------------------------------------|
/// | SATURATED (sustained)| CAPACITY    | Pool too small for demand                  |
/// | LEAKING (detected)   | DEFECT      | Resource leak in application               |
/// | UNSTABLE (oscillating)| INSTABILITY | Pool sizing algorithm hunting              |
/// | SCALING (effective)  | NORMAL      | Pool adapting to load successfully         |
///
/// This hierarchical meaning-making enables cross-domain reasoning: observers understand
/// pool health and efficiency without needing to understand pool implementation details
/// or resource management algorithms.
///
/// @author William David Louth
/// @since 1.0

public final class Pools
  implements Serventis {

  private Pools () { }

  /// A static composer function for creating Pool instruments.
  ///
  /// This method can be used as a method reference with conduits as follows:
  ///
  /// Example usage:
  /// ```java
  /// final var cortex = Substrates.cortex();
  /// var pool = circuit.conduit(Pools::composer).percept(cortex.name("db.connections"));
  /// ```
  ///
  /// @param channel the channel from which to create the pool
  /// @return a new Pool instrument for the specified channel
  /// @throws NullPointerException if the channel param is `null`

  @New
  @NotNull
  public static Pool composer (
    @NotNull final Channel < ? super Sign > channel
  ) {

    return
      new Pool (
        channel.pipe ()
      );

  }

  /// A [Sign] represents a capacity or utilization change in a resource pool.
  ///
  /// Signs are organized into two functional categories:
  ///
  /// - **Capacity Changes**: EXPAND, CONTRACT (pool size adjustments)
  /// - **Utilization Changes**: BORROW, RECLAIM (resource movement)
  ///
  /// ## Sign Categories
  ///
  /// Pools adjust their capacity (EXPAND/CONTRACT) based on demand, configuration, or
  /// policy while tracking utilization as resources are borrowed and reclaimed.
  /// Subscribers analyze patterns to detect saturation, leaks, and efficiency issues.

  public enum Sign
    implements Serventis.Sign {

    /// Indicates the pool's capacity increased.
    ///
    /// EXPAND represents a capacity change where the pool created new resources,
    /// increasing its total capacity. This occurs during initialization, scale-up,
    /// on-demand allocation, or manual expansion. EXPAND signals may indicate adaptive
    /// scaling in response to demand. Multiple EXPAND signals without corresponding
    /// utilization increases may indicate over-provisioning.
    ///
    /// **Typical usage**: Creating connections on pool init, adding threads to executor,
    /// allocating new buffers, scaling up on high utilization
    ///
    /// **Pattern analysis**: EXPAND + BORROW↑ → demand-driven scaling, EXPAND + BORROW→ → over-provisioning

    EXPAND,

    /// Indicates the pool's capacity decreased.
    ///
    /// CONTRACT represents a capacity change where the pool destroyed resources,
    /// decreasing its total capacity. This occurs during idle timeout, scale-down,
    /// resource eviction, or manual contraction. CONTRACT signals may indicate adaptive
    /// scaling in response to low utilization. Rapid EXPAND/CONTRACT oscillation suggests
    /// unstable capacity management.
    ///
    /// **Typical usage**: Evicting idle connections, removing excess threads,
    /// releasing cached buffers, scaling down on low utilization
    ///
    /// **Pattern analysis**: CONTRACT + utilization low → efficient scaling, rapid EXPAND/CONTRACT → unstable

    CONTRACT,

    /// Indicates a resource was borrowed from the pool.
    ///
    /// BORROW represents a utilization increase where a resource moved from available
    /// to in-use state. This occurs when clients request resources from the pool.
    /// High BORROW rates indicate heavy pool usage. BORROW without corresponding
    /// RECLAIM suggests resource leaks. BORROW when capacity is saturated may trigger
    /// EXPAND in adaptive pools.
    ///
    /// **Typical usage**: Connection borrowed, thread assigned to task, buffer allocated,
    /// object checked out from pool
    ///
    /// **Pattern analysis**: High BORROW rate → busy pool, BORROW without RECLAIM → leak

    BORROW,

    /// Indicates a resource was reclaimed by the pool.
    ///
    /// RECLAIM represents a utilization decrease where a resource moved from in-use
    /// back to available state. This occurs when clients return resources to the pool.
    /// Healthy pools show balanced BORROW/RECLAIM rates. Low RECLAIM relative to
    /// BORROW indicates resource leaks or long-held resources. RECLAIM of invalid
    /// resources may be followed by CONTRACT.
    ///
    /// **Typical usage**: Connection returned, thread completed task, buffer released,
    /// object checked back into pool
    ///
    /// **Pattern analysis**: BORROW/RECLAIM balanced → healthy, RECLAIM < BORROW → potential leak

    RECLAIM

  }

  /// The `Pool` class represents a named, observable resource pool from which
  /// signs are emitted.
  ///
  /// A pool is a resource management instrument that tracks capacity adjustments
  /// (EXPAND/CONTRACT) and utilization changes (BORROW/RECLAIM). Pool signs make
  /// resource management behavior observable, enabling pattern recognition for
  /// saturation, leaks, efficiency, and capacity planning.
  ///
  /// ## Usage
  ///
  /// Use domain-specific methods for all pool operations:
  ///
  /// ```java
  /// // Capacity management
  /// pool.expand();    // Capacity increased
  /// pool.contract();  // Capacity decreased
  ///
  /// // Utilization tracking
  /// pool.borrow();    // Resource borrowed
  /// pool.reclaim();   // Resource reclaimed
  /// ```

  @Queued
  @Provided
  public static final class Pool
    implements Signer < Sign > {

    private final Pipe < ? super Sign > pipe;

    private Pool (
      final Pipe < ? super Sign > pipe
    ) {

      this.pipe = pipe;

    }

    /// Emits a `BORROW` sign from this pool.
    ///
    /// Represents utilization increase (resource borrowed).

    public void borrow () {

      pipe.emit (
        BORROW
      );

    }

    /// Emits a `CONTRACT` sign from this pool.
    ///
    /// Represents capacity decrease (resource destroyed).

    public void contract () {

      pipe.emit (
        CONTRACT
      );

    }

    /// Emits an `EXPAND` sign from this pool.
    ///
    /// Represents capacity increase (resource created).

    public void expand () {

      pipe.emit (
        EXPAND
      );

    }

    /// Emits a `RECLAIM` sign from this pool.
    ///
    /// Represents utilization decrease (resource reclaimed).

    public void reclaim () {

      pipe.emit (
        RECLAIM
      );

    }

    /// Signs a pool event.
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
