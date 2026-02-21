// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.opt.sync;

import io.humainary.substrates.ext.serventis.api.Serventis;

import static io.humainary.substrates.ext.serventis.opt.sync.Atomics.Sign.*;

/// # Atomics API
///
/// The `Atomics` API provides a structured framework for observing Compare-And-Swap (CAS)
/// operations and contention management behaviors in lock-free and wait-free algorithms.
/// It enables emission of semantic signals about atomic operation attempts, failures,
/// retry strategies, and backoff dynamics, making contention patterns observable for
/// performance tuning and algorithmic adaptation.
///
/// ## Purpose
///
/// This API enables systems to emit **rich semantic signals** about CAS contention dynamics,
/// capturing not just success/failure but the behavioral responses to contention: spinning,
/// yielding, backing off, and escalation to blocking. Atomics observability reveals contention
/// hotspots, retry patterns, and backoff effectiveness critical for lock-free algorithm tuning.
///
/// **Design Note**: While the Locks API covers CAS as one acquisition mechanism (via CONTEST),
/// Atomics focuses specifically on **contention management behaviors**. Locks is about ownership
/// (GRANT → RELEASE). Atomics is about contention dynamics (FAIL → SPIN → BACKOFF → SUCCESS).
/// These APIs complement each other: CAS-based lock acquisition can emit both Atomics signs
/// (for contention dynamics) and Locks signs (for ownership semantics).
///
/// ## Important: Observability vs Implementation
///
/// This API is for **reporting CAS contention semantics**, not implementing atomic operations.
/// When your system performs CAS operations (AtomicReference, VarHandle, Unsafe), use this API
/// to emit observability signals about contention and retry behavior. Meta-level observers can
/// then reason about contention patterns, backoff effectiveness, and spin-to-park transitions
/// without coupling to your implementation details.
///
/// **Example**: When a CAS fails, call `atomic.fail()`. If spinning, call `atomic.spin()`.
/// When applying exponential backoff, call `atomic.backoff()`. When transitioning from spinning
/// to parking, call `atomic.park()`. These signals enable meta-observability of contention
/// dynamics, revealing hotspots and tuning opportunities.
///
/// ## Key Concepts
///
/// - **Atomic**: A CAS-based operation attempting lock-free state modification
/// - **Sign**: The type of contention event (ATTEMPT, SUCCESS, FAIL, SPIN, YIELD, BACKOFF, PARK, EXHAUST)
/// - **Spinning**: Busy-wait retry loop consuming CPU cycles
/// - **Yielding**: Cooperative scheduling hint (Thread.yield)
/// - **Backoff**: Deliberate delay between retries (linear, exponential, jitter)
/// - **Parking**: Escalation from spinning to blocking (thread suspension)
/// - **Exhaustion**: Retry budget depleted, operation abandoned
///
/// ## Atomic Operation Lifecycle
///
/// ### Simple CAS Success
/// ```
/// ATTEMPT → SUCCESS
/// ```
///
/// ### CAS with Spin Retry
/// ```
/// ATTEMPT → FAIL → SPIN → FAIL → SPIN → SUCCESS
/// ```
///
/// ### CAS with Yield
/// ```
/// ATTEMPT → FAIL → SPIN → FAIL → YIELD → FAIL → SUCCESS
/// ```
///
/// ### CAS with Adaptive Backoff
/// ```
/// ATTEMPT → FAIL → SPIN → FAIL → BACKOFF → FAIL → BACKOFF → SUCCESS
/// ```
///
/// ### CAS Spin-to-Park Escalation
/// ```
/// ATTEMPT → FAIL → SPIN → FAIL → SPIN → FAIL → PARK → SUCCESS
/// ```
///
/// ### Retry Budget Exhaustion
/// ```
/// ATTEMPT → FAIL → SPIN → FAIL → BACKOFF → FAIL → EXHAUST
/// ```
///
/// ## Signal Categories
///
/// The API defines signals across contention management phases:
///
/// ### Operation Phase
/// - **ATTEMPT**: CAS operation initiated
/// - **SUCCESS**: CAS succeeded (value atomically updated)
/// - **FAIL**: CAS failed (concurrent modification detected)
///
/// ### Retry Strategy Phase
/// - **SPIN**: Busy-wait retry (CPU-intensive, low latency)
/// - **YIELD**: Thread.yield() hint to scheduler
/// - **BACKOFF**: Deliberate delay applied (exponential, jitter, etc.)
///
/// ### Escalation Phase
/// - **PARK**: Transition from spinning to blocking (thread suspension)
/// - **EXHAUST**: Retry budget depleted, operation abandoned
///
/// ## The 8 Atomics Signs
///
/// | Sign      | Description                                    | Category   |
/// |-----------|------------------------------------------------|------------|
/// | `ATTEMPT` | CAS operation initiated                        | Operation  |
/// | `SUCCESS` | CAS succeeded                                  | Operation  |
/// | `FAIL`    | CAS failed (contention detected)               | Operation  |
/// | `SPIN`    | Busy-wait retry loop                           | Retry      |
/// | `YIELD`   | Thread.yield() applied                         | Retry      |
/// | `BACKOFF` | Deliberate delay applied                       | Retry      |
/// | `PARK`    | Transition to blocking (thread suspension)     | Escalation |
/// | `EXHAUST` | Retry budget exceeded                          | Escalation |
///
/// ## Common Patterns
///
/// ### Simple Atomic Update
/// ```java
/// atomic.attempt();
/// if (cas.compareAndSet(expected, newValue)) {
///   atomic.success();
/// } else {
///   atomic.fail();
/// }
/// ```
///
/// ### Spin Loop with Backoff
/// ```java
/// atomic.attempt();
/// int spins = 0;
/// while (!cas.compareAndSet(expected, newValue)) {
///   atomic.fail();
///   if (spins++ < SPIN_LIMIT) {
///     atomic.spin();
///     Thread.onSpinWait();
///   } else {
///     atomic.backoff();
///     LockSupport.parkNanos(backoffNanos);
///   }
///   expected = cas.get();
/// }
/// atomic.success();
/// ```
///
/// ### Adaptive Spin-to-Park
/// ```java
/// atomic.attempt();
/// int spins = 0;
/// while (!cas.compareAndSet(expected, newValue)) {
///   atomic.fail();
///   if (spins++ < SPIN_THRESHOLD) {
///     atomic.spin();
///     Thread.onSpinWait();
///   } else {
///     atomic.park();
///     LockSupport.park();
///   }
///   expected = cas.get();
/// }
/// atomic.success();
/// ```
///
/// ### Bounded Retry with Exhaustion
/// ```java
/// atomic.attempt();
/// for (int i = 0; i < MAX_RETRIES; i++) {
///   if (cas.compareAndSet(expected, newValue)) {
///     atomic.success();
///     return true;
///   }
///   atomic.fail();
///   atomic.backoff();
///   LockSupport.parkNanos(backoffNanos(i));
///   expected = cas.get();
/// }
/// atomic.exhaust();
/// return false;
/// ```
///
/// ## Contention Analysis
///
/// Atomics signals enable sophisticated contention analysis:
///
/// - **Contention rate**: Frequency of FAIL signals per ATTEMPT
/// - **Spin efficiency**: Ratio of SUCCESS after SPIN vs escalation to BACKOFF/PARK
/// - **Backoff effectiveness**: Reduction in FAIL rate after BACKOFF signals
/// - **Escalation frequency**: Rate of PARK signals (spinning insufficient)
/// - **Exhaustion rate**: Frequency of EXHAUST (severe contention or misconfiguration)
/// - **Retry depth**: Average FAIL count between ATTEMPT and SUCCESS/EXHAUST
///
/// High contention patterns (many FAIL, frequent PARK/EXHAUST) indicate:
/// - Hot data structure requiring partitioning or redesign
/// - Insufficient backoff (too aggressive retry)
/// - Need for lock-based fallback under high contention
///
/// ## Relationship to Other APIs
///
/// `Atomics` integrates with other Serventis APIs:
///
/// - **Locks API**: CAS-based lock acquisition uses both APIs:
///   - Atomics for contention dynamics (FAIL → SPIN → BACKOFF)
///   - Locks for ownership semantics (ATTEMPT → GRANT → RELEASE)
/// - **Resources API**: Atomic pool operations (CAS on availability counter)
/// - **Queues API**: Lock-free queue implementations (CAS on head/tail)
/// - **Statuses API**: Contention patterns inform conditions:
///   - High FAIL rate → SATURATED status
///   - Frequent EXHAUST → OVERLOADED status
///   - Many PARK → DEGRADED status
///
/// ## Performance Considerations
///
/// Atomics operate at nanosecond timescales. CAS operations complete in single-digit
/// nanoseconds on modern CPUs. Sign emission must be extremely lightweight to avoid
/// perturbing the very behavior being observed. Zero-allocation enum emission with
/// sub-10ns overhead ensures atomics observability doesn't become a contention source.
///
/// Consider adaptive instrumentation: emit ATTEMPT/SUCCESS only, adding FAIL/SPIN/BACKOFF
/// signals only when contention analysis is actively needed. This minimizes overhead
/// during normal low-contention operation.
///
/// ## Lock-Free Algorithm Patterns
///
/// ### Treiber Stack Push
/// ```java
/// atomic.attempt();
/// do {
///   Node oldHead = head.get();
///   newNode.next = oldHead;
///   if (head.compareAndSet(oldHead, newNode)) {
///     atomic.success();
///     return;
///   }
///   atomic.fail();
///   atomic.spin();
/// } while (true);
/// ```
///
/// ### Michael-Scott Queue Enqueue
/// ```java
/// atomic.attempt();
/// while (true) {
///   Node tail = this.tail.get();
///   Node next = tail.next.get();
///   if (next == null) {
///     if (tail.next.compareAndSet(null, newNode)) {
///       atomic.success();
///       this.tail.compareAndSet(tail, newNode); // best-effort advance
///       return;
///     }
///     atomic.fail();
///     atomic.spin();
///   } else {
///     this.tail.compareAndSet(tail, next); // help advance
///   }
/// }
/// ```
///
/// ## Semiotic Ascent: Atomics → Status → Situation
///
/// Atomics signs translate upward through the semiotic hierarchy:
///
/// ### Atomics → Status Translation
/// - High FAIL/ATTEMPT ratio → SATURATED status (severe contention)
/// - Frequent PARK signals → DEGRADED status (spinning insufficient)
/// - EXHAUST signals → OVERLOADED status (retry budget exceeded)
/// - Low FAIL rate → OPERATIONAL status (healthy contention levels)
///
/// ### Status → Situation Assessment
/// - Sustained SATURATED from atomics → CAPACITY situation (redesign needed)
/// - Rising EXHAUST rate → AVAILABILITY situation (operations failing)
/// - Sudden FAIL spike → ANOMALY situation (contention burst)
///
/// This hierarchical meaning-making enables cross-domain reasoning: atomic contention
/// patterns translate to status assessments, informing situational awareness without
/// requiring lock-free algorithm expertise from observers.
///
/// @author William David Louth
/// @since 1.0

public final class Atomics
  implements Serventis {

  private Atomics () { }

  /// A static composer function for creating Atomic instruments.
  ///
  /// This method can be used as a method reference with conduits as follows:
  ///
  /// Example usage:
  /// ```java
  /// final var cortex = Substrates.cortex();
  /// var atomic = circuit.conduit(Atomics::composer).percept(cortex.name("queue.head.cas"));
  /// ```
  ///
  /// @param channel the channel from which to create the atomic
  /// @return a new Atomic instrument for the specified channel
  /// @throws NullPointerException if the channel param is `null`

  @New
  @NotNull
  public static Atomic composer (
    @NotNull final Channel < ? super Sign > channel
  ) {

    return
      new Atomic (
        channel.pipe ()
      );

  }

  /// A [Sign] classifies CAS contention events that occur during atomic operations.
  /// These classifications enable analysis of contention patterns, retry strategies,
  /// and backoff effectiveness in lock-free algorithms.
  ///
  /// ## Sign Categories
  ///
  /// Signs are organized into functional categories representing different aspects
  /// of contention management:
  ///
  /// - **Operation**: ATTEMPT, SUCCESS, FAIL
  /// - **Retry**: SPIN, YIELD, BACKOFF
  /// - **Escalation**: PARK, EXHAUST

  public enum Sign
    implements Serventis.Sign {

    /// Indicates a CAS operation has been initiated.
    ///
    /// ATTEMPT represents the start of an atomic update attempt. This is the entry
    /// point for contention analysis: every atomic operation begins with ATTEMPT
    /// and ends with either SUCCESS or EXHAUST. Tracking ATTEMPT frequency reveals
    /// hot atomic variables.
    ///
    /// **Typical usage**: Before compareAndSet(), getAndUpdate(), updateAndGet()
    ///
    /// **Next states**: SUCCESS (immediate), FAIL (contention detected)

    ATTEMPT,

    /// Indicates the CAS operation succeeded.
    ///
    /// SUCCESS represents successful atomic state modification. The expected value
    /// matched and the new value was atomically installed. This terminates the
    /// atomic operation lifecycle. Low SUCCESS/ATTEMPT ratio indicates high contention.
    ///
    /// **Typical usage**: After successful compareAndSet()
    ///
    /// **Analysis**: Track latency between ATTEMPT and SUCCESS for contention impact

    SUCCESS,

    /// Indicates the CAS operation failed due to contention.
    ///
    /// FAIL represents CAS failure: the current value did not match the expected
    /// value, indicating concurrent modification by another thread. This triggers
    /// retry behavior (SPIN, YIELD, BACKOFF) or escalation (PARK, EXHAUST).
    /// High FAIL rates indicate contention hotspots.
    ///
    /// **Typical usage**: After failed compareAndSet()
    ///
    /// **Next states**: SPIN (retry), YIELD (hint), BACKOFF (delay), PARK (block), EXHAUST (give up)

    FAIL,

    /// Indicates busy-wait retry in a spin loop.
    ///
    /// SPIN represents active spinning: the thread retries the CAS immediately
    /// without yielding or sleeping. Spinning is CPU-intensive but provides lowest
    /// latency for brief contention. Excessive spinning wastes CPU and may delay
    /// the thread holding the contended resource. Modern CPUs support spin hints
    /// (Thread.onSpinWait, PAUSE instruction) to reduce power and allow hyperthreads.
    ///
    /// **Typical usage**: Thread.onSpinWait(), tight retry loop, spin lock
    ///
    /// **Next states**: SUCCESS (retry succeeded), FAIL (still contended)

    SPIN,

    /// Indicates Thread.yield() applied as scheduling hint.
    ///
    /// YIELD represents cooperative yielding: the thread hints to the scheduler
    /// that it can give up its timeslice. This is between SPIN (no yielding) and
    /// BACKOFF (explicit delay). Yielding may allow the contending thread to complete,
    /// but provides no guarantee the scheduler will act on the hint.
    ///
    /// **Typical usage**: Thread.yield() between CAS attempts
    ///
    /// **Next states**: SUCCESS (retry succeeded), FAIL (still contended)

    YIELD,

    /// Indicates deliberate delay applied between retries.
    ///
    /// BACKOFF represents intentional waiting: the thread sleeps or parks for a
    /// calculated duration before retrying. Common strategies include linear backoff,
    /// exponential backoff, and jittered backoff. Backoff reduces contention pressure
    /// by spreading retry attempts over time, but increases latency.
    ///
    /// **Typical usage**: LockSupport.parkNanos(), Thread.sleep(), exponential delay
    ///
    /// **Analysis**: Track backoff duration and effectiveness (SUCCESS after BACKOFF vs continued FAIL)

    BACKOFF,

    /// Indicates transition from spinning to blocking.
    ///
    /// PARK represents escalation from active spinning to passive waiting. When
    /// spinning exceeds a threshold without success, the thread parks (blocks)
    /// to avoid wasting CPU. This is adaptive behavior common in hybrid locks
    /// (spin briefly, then block). PARK signals indicate contention severe enough
    /// that spinning is ineffective.
    ///
    /// **Typical usage**: LockSupport.park() after spin threshold exceeded
    ///
    /// **Analysis**: High PARK rate indicates spinning thresholds may need tuning

    PARK,

    /// Indicates retry budget exceeded, operation abandoned.
    ///
    /// EXHAUST represents giving up: the thread exhausted its retry budget without
    /// achieving SUCCESS. This may trigger fallback behavior (lock acquisition,
    /// operation failure, exception). EXHAUST signals indicate severe contention
    /// or misconfigured retry limits. High EXHAUST rates demand architectural
    /// intervention: partitioning, lock fallback, or algorithm change.
    ///
    /// **Typical usage**: Max retries exceeded, timeout reached
    ///
    /// **Analysis**: EXHAUST is a critical signal requiring immediate attention

    EXHAUST

  }

  /// The `Atomic` class represents a named, observable atomic operation site from which
  /// contention signs are emitted.
  ///
  /// An atomic represents a CAS-based state modification point where contention may
  /// occur. Atomic signs make contention dynamics observable, enabling performance
  /// analysis, backoff tuning, and contention pattern recognition.
  ///
  /// ## Usage
  ///
  /// Use domain-specific methods for all contention events:
  ///
  /// ```java
  /// // Simple CAS
  /// atomic.attempt();
  /// atomic.success();  // or atomic.fail()
  ///
  /// // Spin retry
  /// atomic.attempt();
  /// atomic.fail();
  /// atomic.spin();
  /// atomic.success();
  ///
  /// // Backoff
  /// atomic.attempt();
  /// atomic.fail();
  /// atomic.backoff();
  /// atomic.success();
  ///
  /// // Exhaustion
  /// atomic.attempt();
  /// atomic.fail();
  /// atomic.spin();
  /// atomic.fail();
  /// atomic.exhaust();
  /// ```

  @Queued
  @Provided
  public static final class Atomic
    implements Signer < Sign > {

    private final Pipe < ? super Sign > pipe;

    private Atomic (
      final Pipe < ? super Sign > pipe
    ) {

      this.pipe = pipe;

    }

    /// Emits an `ATTEMPT` sign from this atomic.
    ///
    /// Represents CAS operation initiation.

    public void attempt () {

      pipe.emit (
        ATTEMPT
      );

    }

    /// Emits a `BACKOFF` sign from this atomic.
    ///
    /// Represents deliberate delay applied between retries.

    public void backoff () {

      pipe.emit (
        BACKOFF
      );

    }

    /// Emits an `EXHAUST` sign from this atomic.
    ///
    /// Represents retry budget exceeded.

    public void exhaust () {

      pipe.emit (
        EXHAUST
      );

    }

    /// Emits a `FAIL` sign from this atomic.
    ///
    /// Represents CAS failure due to contention.

    public void fail () {

      pipe.emit (
        FAIL
      );

    }

    /// Emits a `PARK` sign from this atomic.
    ///
    /// Represents transition from spinning to blocking.

    public void park () {

      pipe.emit (
        PARK
      );

    }

    /// Signs an atomic contention event.
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

    /// Emits a `SPIN` sign from this atomic.
    ///
    /// Represents busy-wait retry in spin loop.

    public void spin () {

      pipe.emit (
        SPIN
      );

    }

    /// Emits a `SUCCESS` sign from this atomic.
    ///
    /// Represents successful CAS completion.

    public void success () {

      pipe.emit (
        SUCCESS
      );

    }

    /// Emits a `YIELD` sign from this atomic.
    ///
    /// Represents Thread.yield() scheduling hint.

    public void yield () {

      pipe.emit (
        YIELD
      );

    }

  }

}
