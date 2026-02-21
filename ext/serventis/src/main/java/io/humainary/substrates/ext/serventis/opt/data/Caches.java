// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.opt.data;

import io.humainary.substrates.ext.serventis.api.Serventis;

import static io.humainary.substrates.ext.serventis.opt.data.Caches.Sign.*;

/// # Caches API
///
/// The `Caches` API provides a structured and minimal interface for observing cache
/// interactions within systems. It enables systems to emit **semantic signs** representing
/// lookup operations, hit/miss outcomes, storage operations, and removal conditions.
///
/// ## Purpose
///
/// This API is designed to support **observability and reasoning** about cache behavior
/// in systems. By modeling cache interactions as composable signs, it enables introspection
/// of cache effectiveness, capacity utilization, and operational patterns without coupling
/// to specific implementation details.
///
/// ## Important: Reporting vs Implementation
///
/// This API is for **reporting cache semantics**, not implementing caches.
/// If you have an actual cache implementation (Guava Cache, Caffeine, Redis, etc.),
/// use this API to emit observability signs about operations performed on it.
/// Observer agents can then reason about hit ratios, capacity pressure, and effectiveness
/// patterns without coupling to your implementation details.
///
/// **Example**: When your cache lookup succeeds, call `cache.hit()` to emit a sign.
/// When it misses, call `cache.miss()`. The signs enable meta-observability: observing
/// the observability instrumentation itself to understand cache behavior and effectiveness.
///
/// ## Key Concepts
///
/// - **Cache**: A named subject that emits signs describing operations performed against it
/// - **Sign**: An enumeration of distinct operation types representing cache lifecycle events
///
/// ## Cache Interaction Patterns
///
/// Caches exhibit a lifecycle of lookup, storage, and removal:
///
/// ```
/// Lookup Phase: LOOKUP
///       ↓
/// Outcome Phase: HIT (found) or MISS (not found)
///       ↓
/// Storage Phase: STORE (on miss or update)
///       ↓
/// Removal Phase: EVICT (automatic) or EXPIRE (TTL) or REMOVE (explicit)
/// ```
///
/// ## Signs and Semantics
///
/// | Sign        | Description                                               |
/// |-------------|-----------------------------------------------------------|
/// | `LOOKUP`    | An attempt to retrieve an entry from the cache            |
/// | `HIT`       | A lookup succeeded - entry was found in cache             |
/// | `MISS`      | A lookup failed - entry was not found in cache            |
/// | `STORE`     | An entry was added or updated in the cache                |
/// | `EVICT`     | An entry was automatically removed due to capacity/policy |
/// | `EXPIRE`    | An entry was removed due to TTL/expiration                |
/// | `REMOVE`    | An entry was explicitly invalidated/removed               |
///
/// ## Semantic Distinctions
///
/// - **LOOKUP**: Informational sign - cache access before outcome is determined
/// - **HIT/MISS**: Outcome signs - result of cache lookup operation
/// - **STORE**: Operational sign - cache population or update
/// - **EVICT**: Automatic capacity-driven removal
/// - **EXPIRE**: Automatic time-driven removal
/// - **REMOVE**: Explicit intentional removal
///
/// ## Use Cases
///
/// - Tracking cache effectiveness through hit/miss patterns
/// - Monitoring cache capacity pressure via eviction frequency
/// - Detecting staleness issues through expiration patterns
/// - Understanding cache churn through removal and store frequency
///
/// ## Relationship to Other APIs
///
/// `Caches` signs can inform higher-level abstractions:
///
/// - **Gauges API**: Cache size can be modeled as a gauge (INCREMENT on STORE, DECREMENT on EVICT/EXPIRE/REMOVE)
/// - **Counters API**: Hit/miss totals can be tracked as monotonic counters
/// - **Statuses API**: Cache patterns may indicate DEGRADED or DIVERGING conditions
/// - Observer agents translate cache signs into effectiveness, capacity, or health signs
///
/// ## Performance Considerations
///
/// Cache sign emissions are designed for high-frequency operation (10M-50M Hz).
/// Zero-allocation enum emission with ~10-20ns cost for non-transit emits.
/// Signs flow asynchronously through the circuit's event queue.
///
/// @author William David Louth
/// @since 1.0

public final class Caches
  implements Serventis {

  private Caches () { }

  /// A static composer function for creating Cache instruments.
  ///
  /// This method can be used as a method reference with conduits as follows:
  ///
  /// Example usage:
  /// ```java
  /// final var cortex = Substrates.cortex();
  /// var cache = circuit.conduit(Caches::composer).percept(cortex.name("user.sessions"));
  /// ```
  ///
  /// @param channel the channel from which to create the cache
  /// @return a new Cache instrument for the specified channel
  /// @throws NullPointerException if the channel param is `null`

  @New
  @NotNull
  public static Cache composer (
    @NotNull final Channel < ? super Sign > channel
  ) {

    return
      new Cache (
        channel.pipe ()
      );

  }

  /// A [Sign] represents the kind of action being observed in a cache interaction.
  ///
  /// These signs distinguish between lookup operations (LOOKUP), their outcomes
  /// (HIT/MISS), storage operations (STORE), and different removal conditions
  /// (EVICT/EXPIRE/REMOVE).

  public enum Sign
    implements Serventis.Sign {

    /// Indicates an attempt to retrieve an entry from the cache.
    ///
    /// This sign represents a cache access operation before the outcome is known.

    LOOKUP,

    /// Indicates a cache lookup succeeded - the requested entry was found.
    ///
    /// This sign represents successful cache access, avoiding more expensive fallback
    /// operations like database queries or computation.

    HIT,

    /// Indicates a cache lookup failed - the requested entry was not found.
    ///
    /// This sign represents cache miss, requiring fallback to the authoritative source.

    MISS,

    /// Indicates an entry was added to or updated in the cache.
    ///
    /// This sign represents cache population or refresh operations, typically following
    /// MISS events when loading from authoritative sources.

    STORE,

    /// Indicates an entry was automatically removed due to capacity or policy.
    ///
    /// Eviction reveals capacity boundaries where the cache reached limits (capacity,
    /// LRU/LFU policy) and removed entries to make space.

    EVICT,

    /// Indicates an entry was removed because it reached its time-to-live (TTL).
    ///
    /// Expiration represents time-based invalidation, distinct from capacity-driven
    /// eviction.

    EXPIRE,

    /// Indicates an entry was explicitly removed or invalidated.
    ///
    /// This sign represents intentional cache invalidation, distinct from automatic
    /// EVICT or EXPIRE.

    REMOVE

  }

  /// The [Cache] class represents a named, observable cache from which signs are emitted.
  ///
  /// ## Usage
  ///
  /// Use domain-specific methods: `cache.lookup()`, `cache.hit()`, `cache.miss()`
  ///
  /// Caches provide semantic methods for reporting cache lifecycle events.

  @Queued
  @Provided
  public static final class Cache
    implements Signer < Sign > {

    private final Pipe < ? super Sign > pipe;

    private Cache (
      final Pipe < ? super Sign > pipe
    ) {

      this.pipe = pipe;

    }

    /// Emits an evict sign from this cache.

    public void evict () {

      pipe.emit (
        EVICT
      );

    }

    /// Emits an expire sign from this cache.

    public void expire () {

      pipe.emit (
        EXPIRE
      );

    }

    /// Emits a hit sign from this cache.

    public void hit () {

      pipe.emit (
        HIT
      );

    }

    /// Emits a lookup sign from this cache.

    public void lookup () {

      pipe.emit (
        LOOKUP
      );

    }

    /// Emits a miss sign from this cache.

    public void miss () {

      pipe.emit (
        MISS
      );

    }

    /// Emits a remove sign from this cache.

    public void remove () {

      pipe.emit (
        REMOVE
      );

    }

    /// Signs a cache event.
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

    /// Emits a store sign from this cache.

    public void store () {

      pipe.emit (
        STORE
      );

    }

  }

}
