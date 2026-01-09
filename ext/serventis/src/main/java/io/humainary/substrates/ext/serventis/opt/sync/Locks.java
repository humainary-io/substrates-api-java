// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.opt.sync;

import io.humainary.substrates.ext.serventis.api.Serventis;

import static io.humainary.substrates.ext.serventis.opt.sync.Locks.Sign.*;

/// # Locks API
///
/// The `Locks` API provides a structured framework for observing mutual exclusion and
/// synchronization primitives in concurrent systems. It enables emission of semantic signals
/// about lock acquisition, contention, and release patterns, making synchronization behavior
/// observable for deadlock detection, performance analysis, and coordination reasoning.
///
/// ## Purpose
///
/// This API enables systems to emit **rich semantic signals** about lock operations, capturing
/// the complete lifecycle of synchronization: acquisition attempts, grants, denials, timeouts,
/// releases, and exceptional conditions like abandonment. Lock observability reveals contention
/// patterns, hold times, and coordination bottlenecks critical for concurrent system understanding.
///
/// **Design Note**: Locks are resources. This API aligns with the Resources API vocabulary,
/// using ATTEMPT (non-blocking), ACQUIRE (blocking), GRANT (success), DENY (immediate failure),
/// and TIMEOUT (wait failure). Lock-specific extensions include CONTEST (CAS contention),
/// UPGRADE/DOWNGRADE (RW locks), and ABANDON (crash detection).
///
/// ## Important: Observability vs Implementation
///
/// This API is for **reporting lock semantics**, not implementing synchronization primitives.
/// When your system uses locks (mutexes, semaphores, read-write locks, distributed locks,
/// optimistic locks), use this API to emit observability signals about lock operations.
/// Meta-level observers can then reason about contention patterns, deadlock risks, and
/// synchronization effectiveness without coupling to your lock implementation details.
///
/// **Example**: When a thread attempts a non-blocking lock acquisition, call `lock.attempt()`.
/// If unavailable, call `lock.deny()`. For blocking acquisition, call `lock.acquire()` then
/// later `lock.grant()` when obtained. When released, call `lock.release()`. These signals
/// enable meta-observability of synchronization patterns, revealing contention hotspots and
/// potential deadlocks.
///
/// ## Key Concepts
///
/// - **Lock**: A synchronization primitive providing mutual exclusion or controlled access
/// - **Sign**: The type of lock operation (ATTEMPT, ACQUIRE, GRANT, DENY, RELEASE, etc.)
/// - **Mutual Exclusion**: Only one holder can possess the lock at a time (mutex, write lock)
/// - **Shared Access**: Multiple holders can possess the lock simultaneously (read lock, semaphore)
/// - **Contention**: Multiple threads competing for the same lock (detected via CONTEST)
/// - **Deadlock**: Circular dependency where threads block each other indefinitely
///
/// ## Lock Lifecycle
///
/// ### Successful Acquisition (Non-blocking, Try-lock)
/// ```
/// ATTEMPT → GRANT → [lock held] → RELEASE
/// ```
///
/// ### Failed Acquisition (Try-lock)
/// ```
/// ATTEMPT → DENY
/// ```
///
/// ### Successful Acquisition (Blocking)
/// ```
/// ACQUIRE → GRANT → [lock held] → RELEASE
/// ```
///
/// ### Timed Acquisition Failure
/// ```
/// ACQUIRE → TIMEOUT
/// ```
///
/// ### CAS-based Optimistic Lock (with retries)
/// ```
/// ATTEMPT → CONTEST (CAS failed) → CONTEST (CAS failed) → GRANT (CAS succeeded)
/// ```
///
/// ### Read-Write Lock Pattern
/// ```
/// Reader 1: ATTEMPT → GRANT (read, shared access)
/// Reader 2: ATTEMPT → GRANT (read, shared access)
/// Writer:   ATTEMPT → CONTEST (blocked by readers)
/// Reader 1: RELEASE
/// Reader 2: RELEASE
/// Writer:   GRANT (exclusive access) → RELEASE
/// ```
///
/// ### Lock Upgrade/Downgrade (RW Locks)
/// ```
/// GRANT (read) → UPGRADE → [write lock] → DOWNGRADE → [read lock] → RELEASE
/// ```
///
/// ## Signal Categories
///
/// The API defines signals across lock lifecycle phases:
///
/// ### Acquisition Phase (aligned with Resources API)
/// - **ATTEMPT**: Non-blocking lock request (try-lock)
/// - **ACQUIRE**: Blocking lock request (willing to wait)
/// - **GRANT**: Lock successfully obtained
/// - **DENY**: Try-lock failed immediately (lock held by another)
/// - **TIMEOUT**: Timed acquisition exceeded limit
///
/// ### Holding Phase (lock-specific)
/// - **UPGRADE**: Read lock promoted to write lock (RW locks)
/// - **DOWNGRADE**: Write lock demoted to read lock (RW locks)
///
/// ### Release Phase
/// - **RELEASE**: Lock voluntarily released by holder
/// - **ABANDON**: Lock holder terminated without release (crash, thread death)
///
/// ### Contention Detection (lock-specific)
/// - **CONTEST**: CAS failure or contention detected
///
/// ## The 9 Lock Signs
///
/// | Sign        | Description                                                    | Source       |
/// |-------------|----------------------------------------------------------------|--------------|
/// | `ATTEMPT`   | Non-blocking lock request (try-lock)                           | Resources    |
/// | `ACQUIRE`   | Blocking lock request (willing to wait)                        | Resources    |
/// | `GRANT`     | Lock successfully obtained                                     | Resources    |
/// | `DENY`      | Try-lock failed immediately                                    | Resources    |
/// | `TIMEOUT`   | Timed acquisition exceeded limit                               | Resources    |
/// | `RELEASE`   | Lock voluntarily released                                      | Resources    |
/// | `UPGRADE`   | Read lock → write lock (RW locks)                              | Lock-specific|
/// | `DOWNGRADE` | Write lock → read lock (RW locks)                              | Lock-specific|
/// | `CONTEST`   | CAS failed / contention detected                               | Lock-specific|
/// | `ABANDON`   | Lock holder crashed without release                            | Lock-specific|
///
/// ## Lock Types and Patterns
///
/// ### Mutex (Mutual Exclusion Lock) - Blocking
/// ```java
/// lock.acquire();    // Blocking request, willing to wait
/// lock.grant();      // Lock obtained
/// // ... critical section ...
/// lock.release();    // Lock released
/// ```
///
/// ### Try-Lock (Non-blocking)
/// ```java
/// lock.attempt();    // Non-blocking try
/// if (available) {
///   lock.grant();    // Lock obtained immediately
/// } else {
///   lock.deny();     // Immediate failure, no wait
/// }
/// ```
///
/// ### Timed Lock
/// ```java
/// lock.acquire();    // Blocking request with timeout
/// if (timeout) {
///   lock.timeout();  // Wait exceeded limit
/// } else {
///   lock.grant();    // Lock obtained before timeout
/// }
/// ```
///
/// ### CAS-based Optimistic Lock
/// ```java
/// lock.attempt();    // Try CAS
/// lock.contest();    // CAS failed (contention)
/// lock.contest();    // CAS failed again
/// lock.grant();      // CAS succeeded
/// ```
///
/// ### Read-Write Lock
/// ```java
/// // Multiple readers (shared)
/// readerLock.attempt();
/// readerLock.grant();    // Shared access granted
///
/// // Writer (exclusive)
/// writerLock.attempt();
/// writerLock.contest();  // Blocked by readers
/// // ... readers release ...
/// writerLock.grant();    // Exclusive access granted
/// ```
///
/// ### Lock Upgrade Pattern
/// ```java
/// lock.attempt();
/// lock.grant();          // Read lock obtained
/// // ... reading ...
/// lock.upgrade();        // Promote to write lock
/// // ... writing ...
/// lock.downgrade();      // Demote to read lock
/// // ... reading ...
/// lock.release();
/// ```
///
/// ### Semaphore (Counting)
/// ```java
/// semaphore.attempt();   // Try to acquire permit
/// semaphore.grant();     // Permit granted (count decremented)
/// // ... use resource ...
/// semaphore.release();   // Permit returned (count incremented)
/// ```
///
/// ## Deadlock Detection Through Signals
///
/// Lock signals enable deadlock pattern detection:
///
/// ### Classic Deadlock Scenario
/// ```
/// Thread A: lockX.grant() → lockY.acquire() → (blocks waiting)
/// Thread B: lockY.grant() → lockX.acquire() → (blocks waiting)
///
/// Result: Both threads blocked waiting for each other, circular dependency
/// ```
///
/// Observers can detect deadlocks by analyzing dependency graphs constructed from
/// GRANT and blocked ACQUIRE signals across multiple locks. Timeouts on both
/// acquisitions would eventually emit TIMEOUT signals, breaking the deadlock.
///
/// ## Relationship to Other APIs
///
/// `Locks` integrates with other Serventis APIs:
///
/// - **Resources API**: Locks ARE resources - vocabulary aligned (ATTEMPT, ACQUIRE, GRANT, DENY, TIMEOUT, RELEASE)
/// - **Latches API**: Latches complement locks - locks provide mutual exclusion, latches provide coordination barriers
/// - **Leases API**: Distributed locks often backed by leases (lease EXPIRE → lock ABANDON)
/// - **Transactions API**: Locks provide transaction isolation (transaction START → lock ACQUIRE/GRANT)
/// - **Tasks API**: Tasks may acquire locks during execution (task START → lock ATTEMPT/ACQUIRE)
/// - **Statuses API**: Lock contention patterns inform conditions (many CONTEST → DEGRADED)
/// - **Services API**: Lock availability affects service capacity (lock TIMEOUT → service DELAY)
///
/// ## Performance Considerations
///
/// Lock sign emissions operate at synchronization timescales (nanoseconds to milliseconds).
/// Critical sections are typically brief (microseconds), so emission overhead must be minimal.
/// Zero-allocation enum emission with ~10-20ns cost for non-transit emits ensures lock
/// observability doesn't become a synchronization bottleneck itself.
///
/// Unlike coordination primitives (Leases, Transactions at millisecond scale), locks operate
/// at computational timescales similar to Resources (10M-50M Hz potential). Signs flow
/// asynchronously through the circuit's event queue, decoupling observation from execution.
///
/// ## Contention Analysis
///
/// Lock signals enable sophisticated contention analysis:
///
/// - **Hold time**: Time between GRANT and RELEASE
/// - **Acquisition time**: Time between ACQUIRE and GRANT (or TIMEOUT)
/// - **Contention rate**: Frequency of CONTEST signals
/// - **Success rate**: Ratio of GRANT to ATTEMPT/ACQUIRE
/// - **Lock utilization**: Ratio of held time to total time
/// - **Abandonment rate**: Frequency of ABANDON (indicates crashes)
///
/// High contention (many CONTEST, frequent DENY/TIMEOUT) indicates synchronization bottlenecks
/// requiring redesign: finer-grained locking, lock-free algorithms, or architectural changes.
///
/// ## Distributed Locks
///
/// While this API primarily targets in-process synchronization, the signs apply equally
/// to distributed locks (etcd, Zookeeper, Redis, Consul):
///
/// ```java
/// distributedLock.acquire();     // Request from coordination service
/// // ... network round-trip delay ...
/// distributedLock.grant();       // Lock granted by coordinator
/// // ... distributed critical section ...
/// distributedLock.release();     // Return to coordination service
/// ```
///
/// Distributed locks have higher latencies (milliseconds) but same semantic lifecycle.
/// Integration with Leases API common: distributed locks often implemented via lease grants
/// (lease GRANT → lock GRANT, lease EXPIRE → lock ABANDON).
///
/// ## Semiotic Ascent: Lock Signs → Resource → Status → Situation
///
/// Lock signs translate upward through the semiotic hierarchy:
///
/// ### Lock → Resource Translation
/// Locks ARE resources, sharing vocabulary:
/// - Lock ATTEMPT/ACQUIRE → Resource ATTEMPT/ACQUIRE
/// - Lock GRANT → Resource GRANT
/// - Lock DENY → Resource DENY
/// - Lock TIMEOUT → Resource TIMEOUT
/// - Lock RELEASE → Resource RELEASE
///
/// Lock-specific signs (UPGRADE, DOWNGRADE, CONTEST, ABANDON) provide additional
/// semantic richness beyond generic resource patterns.
///
/// ### Resource → Status Translation
/// - High CONTEST/DENY rate → SATURATED status (lock heavily contended)
/// - TIMEOUT pattern → WARNING status (synchronization delays)
/// - ABANDON signals → DEFECTIVE status (process crashes)
/// - Many CONTEST → DEGRADED status (contention hotspot)
///
/// ### Status → Situation Assessment
/// - Deadlock pattern (blocked ACQUIREs) → CRITICAL situation (immediate intervention)
/// - Rising hold times + contention → CAPACITY situation (redesign required)
/// - ABANDON during transaction → CONSISTENCY situation (potential data corruption)
///
/// This hierarchical meaning-making enables cross-domain reasoning: locks translate to
/// resources, which translate to status, which inform situations. Observers understand
/// lock behavior's impact on service quality without needing lock-specific expertise.
///
/// @author William David Louth
/// @since 1.0

public final class Locks
  implements Serventis {

  private Locks () { }

  /// A static composer function for creating Lock instruments.
  ///
  /// This method can be used as a method reference with conduits as follows:
  ///
  /// Example usage:
  /// ```java
  /// final var cortex = Substrates.cortex();
  /// var lock = circuit.conduit(Locks::composer).percept(cortex.name("cache.mutex"));
  /// ```
  ///
  /// @param channel the channel from which to create the lock
  /// @return a new Lock instrument for the specified channel
  /// @throws NullPointerException if the channel param is `null`

  @New
  @NotNull
  public static Lock composer (
    @NotNull final Channel < ? super Sign > channel
  ) {

    return
      new Lock (
        channel.pipe ()
      );

  }

  /// A [Sign] classifies lock operations that occur during synchronization and
  /// mutual exclusion. These classifications enable analysis of contention patterns,
  /// deadlock risks, and coordination effectiveness in concurrent systems.
  ///
  /// ## Sign Categories
  ///
  /// Signs are organized into functional categories representing different aspects
  /// of lock coordination:
  ///
  /// - **Acquisition**: ATTEMPT, ACQUIRE, GRANT, DENY, TIMEOUT
  /// - **Modification**: UPGRADE, DOWNGRADE
  /// - **Release**: RELEASE, ABANDON
  /// - **Contention**: CONTEST
  ///
  /// ## Alignment with Resources API
  ///
  /// Core signs (ATTEMPT, ACQUIRE, GRANT, DENY, TIMEOUT, RELEASE) align with
  /// Resources API vocabulary. Lock-specific extensions (UPGRADE, DOWNGRADE,
  /// CONTEST, ABANDON) provide synchronization-specific semantics.

  public enum Sign
    implements Serventis.Sign {

    /// Indicates a non-blocking lock acquisition attempt (try-lock).
    ///
    /// ATTEMPT represents an optimistic acquisition strategy where the caller is
    /// prepared to handle immediate denial without waiting. Common in latency-sensitive
    /// paths, fallback strategies, or CAS-based optimistic locking. Aligns with
    /// Resources API ATTEMPT.
    ///
    /// **Typical usage**: tryLock(), tryAcquire(), CAS operations
    ///
    /// **Next states**: GRANT (immediate success), DENY (immediate failure), CONTEST (CAS retry)

    ATTEMPT,

    /// Indicates a blocking lock acquisition request (willing to wait).
    ///
    /// ACQUIRE represents a committed acquisition strategy where the caller will wait
    /// for lock availability, possibly with a timeout. Common when the lock is essential
    /// for operation and no alternative exists. Aligns with Resources API ACQUIRE.
    ///
    /// **Typical usage**: lock(), acquire() with wait, blocking semaphore acquisition
    ///
    /// **Next states**: GRANT (success after wait), TIMEOUT (wait exceeded limit)

    ACQUIRE,

    /// Indicates lock successfully obtained.
    ///
    /// GRANT represents successful lock ownership. The thread now has exclusive or
    /// shared access to the protected resource. This may be immediate (after ATTEMPT)
    /// or delayed (after ACQUIRE). The critical section begins. Aligns with Resources
    /// API GRANT. Tracking time between ATTEMPT/ACQUIRE and GRANT reveals acquisition latency.
    ///
    /// **Typical usage**: After successful lock acquisition
    ///
    /// **Next states**: RELEASE (normal), UPGRADE (RW lock), DOWNGRADE (RW lock), ABANDON (crash)

    GRANT,

    /// Indicates non-blocking acquisition denied (try-lock failed).
    ///
    /// DENY represents immediate failure of an ATTEMPT operation. The lock is held by
    /// another thread and the caller chose non-blocking semantics. No waiting occurred.
    /// Caller typically retries later or takes alternative path. Aligns with Resources
    /// API DENY. High deny rates indicate saturation.
    ///
    /// **Typical usage**: tryLock() returns false, tryAcquire() fails
    ///
    /// **Next states**: ATTEMPT (retry), or alternative execution path

    DENY,

    /// Indicates blocking acquisition exceeded time limit.
    ///
    /// TIMEOUT represents failure to acquire lock within acceptable time window after
    /// ACQUIRE. The thread gave up waiting and will likely either retry, fail the
    /// operation, or take a fallback path. Aligns with Resources API TIMEOUT. High
    /// timeout rates indicate severe contention or potential deadlock.
    ///
    /// **Typical usage**: tryLock(timeout) returns false, timed acquire() fails
    ///
    /// **Next states**: ATTEMPT/ACQUIRE (retry), or failure handling

    TIMEOUT,

    /// Indicates lock voluntarily released by holder.
    ///
    /// RELEASE represents normal termination of lock ownership. The critical section
    /// is complete, and the lock is available for other waiters. Aligns with Resources
    /// API RELEASE. Tracking time between GRANT and RELEASE reveals hold times. Lock
    /// is now available for contested waiters.
    ///
    /// **Typical usage**: unlock(), release(), close() on lock resources
    ///
    /// **Next states**: (lock lifecycle complete, may see new ATTEMPT/ACQUIRE from waiters)

    RELEASE,

    /// Indicates read lock upgraded to write lock (RW locks).
    ///
    /// UPGRADE represents promotion from shared access to exclusive access in read-write
    /// locks. This is often a contention point as all other readers must release before
    /// the upgrade completes. May require releasing read lock and acquiring write lock
    /// (risking lost updates) or atomic upgrade (risking deadlock if multiple readers
    /// upgrade simultaneously). Lock-specific extension.
    ///
    /// **Typical usage**: ReadWriteLock upgrade pattern, optimistic → pessimistic lock
    ///
    /// **Next states**: RELEASE (normal), DOWNGRADE (demote back), ABANDON (crash)

    UPGRADE,

    /// Indicates write lock downgraded to read lock (RW locks).
    ///
    /// DOWNGRADE represents demotion from exclusive access to shared access in read-write
    /// locks. This allows other readers to proceed while maintaining some level of lock
    /// ownership. Common in write-then-read patterns where exclusive access is no longer
    /// needed but continued observation is desired. Lock-specific extension.
    ///
    /// **Typical usage**: ReadWriteLock downgrade, pessimistic → optimistic lock
    ///
    /// **Next states**: RELEASE (normal), UPGRADE (promote again), ABANDON (crash)

    DOWNGRADE,

    /// Indicates CAS failure or contention detected.
    ///
    /// CONTEST represents CAS (Compare-And-Swap) operation failure due to concurrent
    /// modification, or system detection of multiple threads competing for the same lock.
    /// Common in optimistic locking, spin locks, and lock-free algorithms. High contest
    /// rates indicate synchronization bottlenecks requiring architectural attention:
    /// finer-grained locking, lock-free algorithms, or redesign. Lock-specific extension.
    ///
    /// **Typical usage**: CAS retry, contention detection, adaptive spinning, backoff
    ///
    /// **Analysis**: Track CONTEST frequency and duration for hotspot identification

    CONTEST,

    /// Indicates lock holder terminated without releasing.
    ///
    /// ABANDON represents exceptional lock termination when the holder crashes, is killed,
    /// or otherwise terminates without proper cleanup. This may leave the system in an
    /// inconsistent state if the critical section was interrupted. Distributed locks detect
    /// abandonment via lease expiration. In-process locks detect it via thread death. High
    /// abandonment rates indicate stability issues. Lock-specific extension.
    ///
    /// **Typical usage**: Thread death during critical section, process crash, forced termination
    ///
    /// **Recovery**: May trigger lock recovery, transaction rollback, or resource cleanup

    ABANDON

  }

  /// The `Lock` class represents a named, observable lock from which signs are emitted.
  ///
  /// A lock is a synchronization primitive providing mutual exclusion or controlled
  /// access to shared resources. Lock signs make synchronization behavior observable,
  /// enabling contention analysis, deadlock detection, and performance optimization.
  ///
  /// ## Usage
  ///
  /// Use domain-specific methods for all lock lifecycle events:
  ///
  /// ```java
  /// // Try-lock (non-blocking)
  /// lock.attempt();
  /// lock.grant();      // or lock.deny()
  /// // ... critical section ...
  /// lock.release();
  ///
  /// // Blocking acquisition
  /// lock.acquire();
  /// lock.grant();      // or lock.timeout()
  /// // ... critical section ...
  /// lock.release();
  ///
  /// // CAS-based
  /// lock.attempt();
  /// lock.contest();    // CAS failed
  /// lock.grant();      // CAS succeeded
  ///
  /// // Lock upgrade
  /// lock.grant();      // read lock
  /// lock.upgrade();    // promote to write lock
  /// lock.downgrade();  // demote to read lock
  /// lock.release();
  /// ```

  @Queued
  @Provided
  public static final class Lock
    implements Signer < Sign > {

    private final Pipe < ? super Sign > pipe;

    private Lock (
      final Pipe < ? super Sign > pipe
    ) {

      this.pipe = pipe;

    }

    /// Emits an `ABANDON` sign from this lock.
    ///
    /// Represents lock holder termination without release.

    public void abandon () {

      pipe.emit (
        ABANDON
      );

    }

    /// Emits an `ACQUIRE` sign from this lock.
    ///
    /// Represents blocking lock acquisition request (willing to wait).

    public void acquire () {

      pipe.emit (
        ACQUIRE
      );

    }

    /// Emits an `ATTEMPT` sign from this lock.
    ///
    /// Represents non-blocking lock acquisition attempt (try-lock).

    public void attempt () {

      pipe.emit (
        ATTEMPT
      );

    }

    /// Emits a `CONTEST` sign from this lock.
    ///
    /// Represents CAS failure or contention detection.

    public void contest () {

      pipe.emit (
        CONTEST
      );

    }

    /// Emits a `DENY` sign from this lock.
    ///
    /// Represents try-lock failure (non-blocking denial).

    public void deny () {

      pipe.emit (
        DENY
      );

    }

    /// Emits a `DOWNGRADE` sign from this lock.
    ///
    /// Represents write lock demotion to read lock.

    public void downgrade () {

      pipe.emit (
        DOWNGRADE
      );

    }

    /// Emits a `GRANT` sign from this lock.
    ///
    /// Represents successful lock acquisition.

    public void grant () {

      pipe.emit (
        GRANT
      );

    }

    /// Emits a `RELEASE` sign from this lock.
    ///
    /// Represents voluntary lock release.

    public void release () {

      pipe.emit (
        RELEASE
      );

    }

    /// Signs a lock event.
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

    /// Emits a `TIMEOUT` sign from this lock.
    ///
    /// Represents blocking acquisition timeout.

    public void timeout () {

      pipe.emit (
        TIMEOUT
      );

    }

    /// Emits an `UPGRADE` sign from this lock.
    ///
    /// Represents read lock promotion to write lock.

    public void upgrade () {

      pipe.emit (
        UPGRADE
      );

    }

  }

}
