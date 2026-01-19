// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.opt.sync;

import io.humainary.substrates.ext.serventis.api.Serventis;

import static io.humainary.substrates.ext.serventis.opt.sync.Latches.Sign.*;

/// # Latches API
///
/// The `Latches` API provides a structured framework for observing synchronization barriers
/// and countdown coordination in concurrent systems. It enables emission of semantic signals
/// about latch operations, making thread coordination and phase transitions observable for
/// concurrency analysis, deadlock detection, and coordination pattern reasoning.
///
/// ## Purpose
///
/// This API enables systems to emit **rich semantic signals** about latch coordination,
/// capturing the complete lifecycle of barrier-based synchronization: threads waiting at
/// barriers, threads arriving/decrementing counts, barrier releases, timeouts, and resets.
/// Latch observability reveals coordination patterns, phase transition dynamics, and
/// synchronization bottlenecks critical for concurrent system understanding.
///
/// **Design Note**: Latches are coordination primitives. This API aligns with Resources
/// vocabulary where appropriate, using AWAIT (blocking wait), RELEASE (barrier satisfied),
/// and TIMEOUT (wait failure). Latch-specific extensions include ARRIVE (count decrement),
/// RESET (barrier reset for reuse), and ABANDON (participant crashed).
///
/// ## Important: Observability vs Implementation
///
/// This API is for **reporting latch semantics**, not implementing synchronization primitives.
/// When your system uses latches (CountDownLatch, CyclicBarrier, Phaser, custom barriers),
/// use this API to emit observability signals about coordination operations. Meta-level
/// observers can then reason about synchronization patterns, phase transitions, and
/// coordination effectiveness without coupling to your latch implementation details.
///
/// **Example**: When a thread awaits on a CountDownLatch, call `latch.await()`. When another
/// thread calls `countDown()`, call `latch.arrive()`. When the count reaches zero and all
/// waiting threads unblock, call `latch.release()`. These signals enable meta-observability
/// of coordination patterns, revealing synchronization phases and potential bottlenecks.
///
/// ## Key Concepts
///
/// - **Latch**: A synchronization barrier that coordinates multiple threads through counting
/// - **Sign**: The type of coordination operation (AWAIT, ARRIVE, RELEASE, TIMEOUT, etc.)
/// - **Barrier**: A synchronization point where threads wait until a condition is satisfied
/// - **Count**: The number of events (arrivals) required before the barrier releases
/// - **Phase**: A coordination cycle in barriers that reset (CyclicBarrier, Phaser)
/// - **Participant**: A thread involved in the coordination (waiting or arriving)
///
/// ## Latch Lifecycle
///
/// ### CountDownLatch Pattern (Single-use)
/// ```
/// Thread 1: AWAIT (blocks until count reaches zero)
/// Thread 2: AWAIT (blocks)
/// Thread 3: ARRIVE (decrements count: 3→2)
/// Thread 4: ARRIVE (decrements count: 2→1)
/// Thread 5: ARRIVE (decrements count: 1→0) → RELEASE
/// Thread 1, 2: (unblock and proceed)
/// ```
///
/// ### CyclicBarrier Pattern (Reusable)
/// ```
/// Phase 1:
///   Thread 1: AWAIT (1/3 arrived)
///   Thread 2: AWAIT (2/3 arrived)
///   Thread 3: AWAIT (3/3 arrived) → RELEASE
///   All threads: (unblock)
///   Barrier: RESET (automatic, ready for next phase)
///
/// Phase 2:
///   Thread 1: AWAIT (1/3 arrived)
///   ...
/// ```
///
/// ### Timed Wait Pattern
/// ```
/// Thread 1: AWAIT (waits with timeout)
/// [time passes, count not reached]
/// Thread 1: TIMEOUT (wait exceeded limit)
/// ```
///
/// ### Participant Failure Pattern
/// ```
/// Thread 1: AWAIT (blocks)
/// Thread 2: AWAIT (blocks)
/// Thread 3: ARRIVE (decrements count)
/// Thread 4: (crashes without arriving) → ABANDON
/// Remaining threads: TIMEOUT (or coordinator detects abandonment)
/// ```
///
/// ## Signal Categories
///
/// The API defines signals across latch coordination phases:
///
/// ### Waiting Phase
/// - **AWAIT**: Thread waiting at barrier (blocking until condition satisfied)
///
/// ### Arrival Phase
/// - **ARRIVE**: Thread reached barrier / decremented count
///
/// ### Release Phase
/// - **RELEASE**: Barrier satisfied, all waiting threads unblocked
///
/// ### Error Phase
/// - **TIMEOUT**: Wait exceeded configured time limit
/// - **ABANDON**: Participant terminated without arriving
///
/// ### Reset Phase
/// - **RESET**: Barrier reset for reuse (cyclic barriers)
///
/// ## The 6 Latch Signs
///
/// | Sign      | Description                                              | Lifecycle Phase |
/// |-----------|----------------------------------------------------------|-----------------|
/// | `AWAIT`   | Thread waiting at barrier (blocking)                     | Waiting         |
/// | `ARRIVE`  | Thread reached barrier / decremented count               | Arrival         |
/// | `RELEASE` | Barrier satisfied, waiting threads unblocked             | Release         |
/// | `TIMEOUT` | Wait exceeded time limit                                 | Error           |
/// | `RESET`   | Barrier reset for next phase (cyclic)                    | Reset           |
/// | `ABANDON` | Participant terminated without arriving                  | Error           |
///
/// ## Latch Types and Patterns
///
/// ### CountDownLatch (One-shot Coordination)
/// ```java
/// // Initialize with count=3
/// latch.await();         // Thread 1 blocks
/// latch.await();         // Thread 2 blocks
/// latch.arrive();        // Thread 3 decrements (3→2)
/// latch.arrive();        // Thread 4 decrements (2→1)
/// latch.arrive();        // Thread 5 decrements (1→0)
/// latch.release();       // Threads 1 & 2 unblock
/// ```
///
/// ### CyclicBarrier (Reusable Phase Coordination)
/// ```java
/// // Phase 1
/// latch.await();         // Thread 1 arrives (1/3)
/// latch.await();         // Thread 2 arrives (2/3)
/// latch.await();         // Thread 3 arrives (3/3)
/// latch.release();       // All threads proceed
/// latch.reset();         // Barrier ready for phase 2
///
/// // Phase 2
/// latch.await();         // Thread 1 arrives (1/3)
/// // ... repeat pattern
/// ```
///
/// ### Phaser (Dynamic Participant Count)
/// ```java
/// latch.arrive();        // Thread 1 arrives
/// latch.await();         // Thread 2 arrives and waits
/// latch.arrive();        // Thread 3 arrives (phase complete)
/// latch.release();       // Waiting threads proceed
/// latch.reset();         // Next phase begins
/// ```
///
/// ### Timed Wait
/// ```java
/// latch.await();         // Thread waits with timeout
/// // ... time passes without count reaching zero
/// latch.timeout();       // Wait abandoned
/// ```
///
/// ### Start Signal Pattern (CountDownLatch)
/// ```java
/// // Main thread prepares work
/// latch.await();         // Worker threads wait
/// latch.await();
/// latch.await();
/// // Main thread ready
/// latch.arrive();        // Release signal
/// latch.release();       // All workers start simultaneously
/// ```
///
/// ### Completion Aggregation Pattern
/// ```java
/// // Parallel tasks report completion
/// latch.await();         // Main thread waits for all tasks
/// task1.complete();
/// latch.arrive();        // Task 1 signals completion
/// task2.complete();
/// latch.arrive();        // Task 2 signals completion
/// task3.complete();
/// latch.arrive();        // Task 3 signals completion
/// latch.release();       // Main thread proceeds
/// ```
///
/// ## Relationship to Other APIs
///
/// `Latches` integrates with other Serventis APIs:
///
/// - **Locks API**: Latches complement locks - locks provide mutual exclusion, latches provide coordination
/// - **Resources API**: Latches ARE coordination resources - vocabulary aligned (AWAIT, RELEASE, TIMEOUT)
/// - **Leases API**: Distributed latches may use leases for participant tracking (lease EXPIRE → latch ABANDON)
/// - **Tasks API**: Task groups may use latches for completion coordination (all tasks complete → latch RELEASE)
/// - **Processes API**: Process coordination may use latches (all processes START → latch RELEASE)
/// - **Statuses API**: Latch timeout patterns inform conditions (many TIMEOUT → DEGRADED)
/// - **Services API**: Service startup may use latches for initialization phases
///
/// ## Performance Considerations
///
/// Latch sign emissions operate at coordination timescales (microseconds to milliseconds).
/// Coordination barriers are typically at phase boundaries, not in tight loops, so emission
/// frequency is moderate (1K-100K Hz). Zero-allocation enum emission with ~10-20ns cost for
/// non-transit emits ensures latch observability doesn't become a coordination bottleneck.
///
/// Unlike high-frequency operations (Resources, Locks at 10M-50M Hz), latches operate at
/// coarser-grained coordination boundaries. Signs flow asynchronously through the circuit's
/// event queue, decoupling observation from execution.
///
/// ## Coordination Analysis
///
/// Latch signals enable sophisticated coordination analysis:
///
/// - **Barrier duration**: Time from first AWAIT to RELEASE
/// - **Arrival pattern**: Distribution of ARRIVE events over time
/// - **Participation rate**: Ratio of ARRIVE to expected participants
/// - **Timeout rate**: Frequency of TIMEOUT (indicates coordination failure)
/// - **Abandonment rate**: Frequency of ABANDON (indicates participant crashes)
/// - **Phase frequency**: Rate of RESET events (cyclic barrier throughput)
/// - **Coordination efficiency**: Ratio of barrier duration to work duration
///
/// High timeout or abandonment rates indicate coordination problems requiring investigation:
/// slow participants, participant crashes, or incorrect participant counts.
///
/// ## Distributed Coordination
///
/// While this API primarily targets in-process coordination, the signs apply equally to
/// distributed coordination primitives (etcd barriers, Zookeeper barriers, Redis coordination):
///
/// ```java
/// distributedLatch.await();     // Node waits for coordination signal
/// // ... network round-trip ...
/// distributedLatch.arrive();    // Node signals readiness
/// // ... all nodes ready ...
/// distributedLatch.release();   // Coordinator releases all nodes
/// ```
///
/// Distributed latches have higher latencies (milliseconds to seconds) but same semantic
/// lifecycle. Integration with Leases API common: distributed latches often implemented via
/// participant registration with leases (lease EXPIRE → latch ABANDON).
///
/// ## Semiotic Ascent: Latch Signs → Resource → Status → Situation
///
/// Latch signs translate upward through the semiotic hierarchy:
///
/// ### Latch → Resource Translation
/// Latches ARE coordination resources, sharing vocabulary:
/// - Latch AWAIT → Resource ACQUIRE (blocking request)
/// - Latch RELEASE → Resource RELEASE
/// - Latch TIMEOUT → Resource TIMEOUT
///
/// Latch-specific signs (ARRIVE, RESET, ABANDON) provide additional semantic richness
/// beyond generic resource patterns.
///
/// ### Resource → Status Translation
/// - High TIMEOUT rate → DEGRADED status (coordination failing)
/// - ABANDON signals → DEFECTIVE status (participant crashes)
/// - Long AWAIT durations → WARNING status (slow coordination)
/// - Frequent RESET with short phases → UNSTABLE status (thrashing)
///
/// ### Status → Situation Assessment
/// - Timeout pattern (many blocked AWAITs) → CRITICAL situation (coordination deadlock)
/// - ABANDON during critical phase → CONSISTENCY situation (incomplete coordination)
/// - Rising barrier durations → CAPACITY situation (coordination bottleneck)
///
/// This hierarchical meaning-making enables cross-domain reasoning: latches translate to
/// resources, which translate to status, which inform situations. Observers understand
/// coordination behavior's impact on service quality without needing latch-specific expertise.
///
/// @author William David Louth
/// @since 1.0

public final class Latches
  implements Serventis {

  private Latches () { }

  /// A static composer function for creating Latch instruments.
  ///
  /// This method can be used as a method reference with conduits as follows:
  ///
  /// Example usage:
  /// ```java
  /// final var cortex = Substrates.cortex();
  /// var latch = circuit.conduit(Latches::composer).percept(cortex.name("startup.barrier"));
  /// ```
  ///
  /// @param channel the channel from which to create the latch
  /// @return a new Latch instrument for the specified channel
  /// @throws NullPointerException if the channel param is `null`

  @New
  @NotNull
  public static Latch composer (
    @NotNull final Channel < ? super Sign > channel
  ) {

    return
      new Latch (
        channel.pipe ()
      );

  }

  /// A [Sign] classifies coordination operations that occur during barrier-based
  /// synchronization. These classifications enable analysis of phase transitions,
  /// coordination patterns, and synchronization effectiveness in concurrent systems.
  ///
  /// ## Sign Categories
  ///
  /// Signs are organized into functional categories representing different aspects
  /// of latch coordination:
  ///
  /// - **Waiting**: AWAIT
  /// - **Arrival**: ARRIVE
  /// - **Release**: RELEASE
  /// - **Error**: TIMEOUT, ABANDON
  /// - **Reset**: RESET
  ///
  /// ## Alignment with Resources API
  ///
  /// Core signs (AWAIT, RELEASE, TIMEOUT) align with Resources API vocabulary.
  /// Latch-specific extensions (ARRIVE, RESET, ABANDON) provide coordination-specific
  /// semantics.

  public enum Sign
    implements Serventis.Sign {

    /// Indicates a thread is waiting at the barrier (blocking).
    ///
    /// AWAIT represents a thread that has reached the synchronization point and is
    /// blocking until the barrier condition is satisfied (count reaches zero, all
    /// parties arrive, etc.). Common in phase-based coordination where threads must
    /// synchronize before proceeding. Aligns with Resources ACQUIRE (blocking wait).
    ///
    /// **Typical usage**: CountDownLatch.await(), CyclicBarrier.await(), Phaser.awaitAdvance()
    ///
    /// **Next states**: RELEASE (barrier satisfied), TIMEOUT (wait exceeded limit)

    AWAIT,

    /// Indicates a thread/participant has reached the barrier or decremented the count.
    ///
    /// ARRIVE represents a participant signaling its readiness or completion of a phase.
    /// For CountDownLatch, this is countDown() decreasing the count. For CyclicBarrier/Phaser,
    /// this is a party arriving at the barrier. When the count reaches zero or all parties
    /// arrive, RELEASE follows. Latch-specific extension.
    ///
    /// **Typical usage**: CountDownLatch.countDown(), CyclicBarrier.await() (arrival aspect)
    ///
    /// **Next states**: RELEASE (if count/parties threshold reached), or more ARRIVE events

    ARRIVE,

    /// Indicates the barrier condition is satisfied and waiting threads are unblocked.
    ///
    /// RELEASE represents successful barrier coordination. The count reached zero, all
    /// required parties arrived, or the phase condition was met. All threads blocked on
    /// AWAIT now proceed. Aligns with Resources RELEASE. Tracking time from first AWAIT
    /// to RELEASE reveals barrier duration.
    ///
    /// **Typical usage**: After count reaches zero or all parties arrive
    ///
    /// **Next states**: RESET (cyclic barrier), or barrier lifecycle complete (one-shot)

    RELEASE,

    /// Indicates a waiting thread's timeout was exceeded before barrier satisfaction.
    ///
    /// TIMEOUT represents failure to achieve coordination within acceptable time. The
    /// barrier condition was not satisfied before the wait deadline. Aligns with Resources
    /// TIMEOUT. High timeout rates indicate coordination problems: slow participants,
    /// incorrect participant counts, or participant crashes.
    ///
    /// **Typical usage**: CountDownLatch.await(timeout) returns false, CyclicBarrier timeout
    ///
    /// **Next states**: Thread may retry, fail operation, or take fallback path

    TIMEOUT,

    /// Indicates the barrier was reset for reuse in cyclic coordination.
    ///
    /// RESET represents barrier reset to initial state, ready for the next coordination
    /// phase. Common in CyclicBarrier and Phaser where the same barrier coordinates
    /// multiple phases. Latch-specific extension. Tracking time between RELEASE and
    /// next AWAIT reveals inter-phase duration.
    ///
    /// **Typical usage**: CyclicBarrier automatic reset, Phaser.arriveAndAdvance()
    ///
    /// **Next states**: AWAIT (threads enter next phase)

    RESET,

    /// Indicates a participant terminated without arriving at the barrier.
    ///
    /// ABANDON represents exceptional coordination failure when a participant crashes,
    /// is killed, or otherwise terminates before signaling readiness. This may leave
    /// other threads permanently blocked unless the coordinator detects abandonment.
    /// Latch-specific extension. High abandonment rates indicate stability issues.
    ///
    /// **Typical usage**: Thread death during coordination, process crash, forced termination
    ///
    /// **Recovery**: May trigger TIMEOUT for waiting threads, or coordinator may RESET

    ABANDON

  }

  /// The `Latch` class represents a named, observable latch from which signs are emitted.
  ///
  /// A latch is a coordination primitive providing barrier-based synchronization where
  /// threads wait until a condition is satisfied (count reaches zero, all parties arrive).
  /// Latch signs make coordination behavior observable, enabling phase transition analysis,
  /// coordination pattern detection, and synchronization bottleneck identification.
  ///
  /// ## Usage
  ///
  /// Use domain-specific methods for all latch lifecycle events:
  ///
  /// ```java
  /// // CountDownLatch pattern
  /// latch.await();         // Thread waits
  /// latch.arrive();        // Another thread signals
  /// latch.arrive();        // Count decrements
  /// latch.arrive();        // Count reaches zero
  /// latch.release();       // Waiting threads proceed
  ///
  /// // CyclicBarrier pattern
  /// latch.await();         // Thread 1 arrives
  /// latch.await();         // Thread 2 arrives
  /// latch.await();         // Thread 3 arrives (all present)
  /// latch.release();       // All threads proceed
  /// latch.reset();         // Ready for next phase
  ///
  /// // Timeout pattern
  /// latch.await();         // Thread waits with timeout
  /// latch.timeout();       // Wait abandoned
  ///
  /// // Failure pattern
  /// latch.await();         // Threads waiting
  /// latch.abandon();       // Participant crashed
  /// ```

  @Queued
  @Provided
  public static final class Latch
    implements Signer < Sign > {

    private final Pipe < ? super Sign > pipe;

    private Latch (
      final Pipe < ? super Sign > pipe
    ) {

      this.pipe = pipe;

    }

    /// Emits an `ABANDON` sign from this latch.
    ///
    /// Represents participant termination without arriving at barrier.

    public void abandon () {

      pipe.emit (
        ABANDON
      );

    }

    /// Emits an `ARRIVE` sign from this latch.
    ///
    /// Represents thread reaching barrier or decrementing count.

    public void arrive () {

      pipe.emit (
        ARRIVE
      );

    }

    /// Emits an `AWAIT` sign from this latch.
    ///
    /// Represents thread waiting at barrier (blocking).

    public void await () {

      pipe.emit (
        AWAIT
      );

    }

    /// Emits a `RELEASE` sign from this latch.
    ///
    /// Represents barrier satisfied, waiting threads unblocked.

    public void release () {

      pipe.emit (
        RELEASE
      );

    }

    /// Emits a `RESET` sign from this latch.
    ///
    /// Represents barrier reset for next coordination phase.

    public void reset () {

      pipe.emit (
        RESET
      );

    }

    /// Signs a latch event.
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

    /// Emits a `TIMEOUT` sign from this latch.
    ///
    /// Represents wait timeout exceeded before barrier satisfaction.

    public void timeout () {

      pipe.emit (
        TIMEOUT
      );

    }

  }

}
