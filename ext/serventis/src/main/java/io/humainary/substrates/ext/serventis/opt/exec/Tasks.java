// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.opt.exec;

import io.humainary.substrates.ext.serventis.api.Serventis;

import static io.humainary.substrates.ext.serventis.opt.exec.Tasks.Sign.*;

/// # Tasks API
///
/// The `Tasks` API provides a structured framework for observing asynchronous work unit
/// lifecycle in concurrent and distributed systems. It enables emission of semantic signals
/// about task submission, scheduling, execution, completion, and failure patterns, making
/// asynchronous computation observable for performance analysis, capacity planning, and
/// reliability reasoning.
///
/// ## Purpose
///
/// This API enables systems to emit **rich semantic signals** about task operations, capturing
/// the complete lifecycle of computational work: submission, scheduling, execution start,
/// progress reporting, suspension, resumption, completion, failure, cancellation, and timeout.
/// Task observability reveals queue depths, execution patterns, failure modes, and throughput
/// bottlenecks critical for understanding asynchronous system behavior.
///
/// ## Important: Observability vs Implementation
///
/// This API is for **reporting task semantics**, not implementing task executors.
/// When your system executes asynchronous work (thread pools, actor systems, job queues,
/// distributed task frameworks, promises, futures, reactive streams), use this API to emit
/// observability signals about task operations. Meta-level observers can then reason about
/// execution patterns, queue pressure, failure rates, and system capacity without coupling
/// to your task implementation details.
///
/// **Example**: When a task is submitted to a thread pool, call `task.submit()`. When it
/// begins executing, call `task.start()`. If it completes successfully, call `task.complete()`.
/// If it fails, call `task.fail()`. These signals enable meta-observability of asynchronous
/// execution patterns, revealing bottlenecks and reliability issues.
///
/// ## Key Concepts
///
/// - **Task**: A unit of asynchronous work with observable lifecycle
/// - **Sign**: The type of task operation (SUBMIT, START, COMPLETE, FAIL, etc.)
/// - **Queue**: Tasks await execution in queues (thread pool queues, message queues, etc.)
/// - **Executor**: The system component executing tasks (thread pool, actor, worker process)
/// - **Lifecycle**: The progression from submission through completion or failure
/// - **Backpressure**: System response to excessive task submission rate
///
/// ## Task Lifecycle
///
/// ### Successful Execution
/// ```
/// SUBMIT → SCHEDULE → START → COMPLETE
/// ```
///
/// ### Failed Execution
/// ```
/// SUBMIT → SCHEDULE → START → FAIL
/// ```
///
/// ### Long-Running Task with Progress
/// ```
/// SUBMIT → SCHEDULE → START → PROGRESS → PROGRESS → COMPLETE
/// ```
///
/// ### Cancelled Task
/// ```
/// SUBMIT → SCHEDULE → CANCEL (before start)
/// or
/// SUBMIT → SCHEDULE → START → CANCEL (during execution)
/// ```
///
/// ### Task Timeout
/// ```
/// SUBMIT → SCHEDULE → START → TIMEOUT
/// ```
///
/// ### Suspended/Resumed Task
/// ```
/// SUBMIT → SCHEDULE → START → SUSPEND → RESUME → COMPLETE
/// ```
///
/// ### Rejected Task (Queue Full)
/// ```
/// SUBMIT → REJECT
/// ```
///
/// ## Signal Categories
///
/// The API defines signals across task lifecycle phases:
///
/// ### Submission Phase
/// - **SUBMIT**: Task submitted to executor/queue
/// - **REJECT**: Task submission rejected (queue full, policy violation)
/// - **SCHEDULE**: Task scheduled for execution (moved from queue to ready state)
///
/// ### Execution Phase
/// - **START**: Task execution begins
/// - **PROGRESS**: Task reports progress (optional, for long-running tasks)
/// - **SUSPEND**: Task paused/suspended (cooperative or preemptive)
/// - **RESUME**: Task resumed after suspension
///
/// ### Completion Phase
/// - **COMPLETE**: Task completed successfully
/// - **FAIL**: Task failed with error
/// - **CANCEL**: Task cancelled before or during execution
/// - **TIMEOUT**: Task exceeded execution time budget
///
/// ## The 11 Task Signs
///
/// | Sign       | Description                                                     |
/// |------------|-----------------------------------------------------------------|
/// | `SUBMIT`   | Task submitted to executor/queue for future execution           |
/// | `REJECT`   | Task submission rejected (queue full, rate limit, policy)       |
/// | `SCHEDULE` | Task scheduled for execution (dequeued, ready to run)           |
/// | `START`    | Task execution begins (worker thread assigned)                  |
/// | `PROGRESS` | Task reports progress indicator (optional, long-running)        |
/// | `SUSPEND`  | Task paused/suspended (cooperative yield or preemption)         |
/// | `RESUME`   | Task resumed after suspension                                   |
/// | `COMPLETE` | Task completed successfully (normal termination)                |
/// | `FAIL`     | Task failed with error (exception, business logic failure)      |
/// | `CANCEL`   | Task cancelled (explicit cancellation request)                  |
/// | `TIMEOUT`  | Task exceeded execution time budget                             |
///
/// ## Task Patterns and Use Cases
///
/// ### Thread Pool Execution
/// ```java
/// task.submit();      // Submitted to executor queue
/// task.schedule();    // Thread available, dequeued
/// task.start();       // Worker thread begins execution
/// task.complete();    // Work finished successfully
/// ```
///
/// ### Task Rejection (Backpressure)
/// ```java
/// task.submit();      // Queue already full
/// task.reject();      // Submission rejected, apply backpressure
/// ```
///
/// ### Long-Running Task with Progress
/// ```java
/// task.submit();
/// task.schedule();
/// task.start();
/// task.progress();    // 25% complete
/// task.progress();    // 50% complete
/// task.progress();    // 75% complete
/// task.complete();    // 100% complete
/// ```
///
/// ### Task Failure
/// ```java
/// task.submit();
/// task.schedule();
/// task.start();
/// task.fail();        // Exception thrown, task failed
/// ```
///
/// ### Task Cancellation
/// ```java
/// // Cancel before execution
/// task.submit();
/// task.schedule();
/// task.cancel();      // Cancelled before worker assigned
///
/// // Cancel during execution
/// task.submit();
/// task.schedule();
/// task.start();
/// task.cancel();      // Cancellation requested, task stopping
/// ```
///
/// ### Task Timeout
/// ```java
/// task.submit();
/// task.schedule();
/// task.start();
/// task.timeout();     // Execution exceeded time budget
/// ```
///
/// ### Suspended/Resumed Task (Cooperative Multitasking)
/// ```java
/// task.submit();
/// task.schedule();
/// task.start();
/// task.suspend();     // Task yields (await, I/O wait)
/// task.resume();      // Task ready again (data available)
/// task.complete();
/// ```
///
/// ### Distributed Task Execution
/// ```java
/// // Coordinator
/// task.submit();      // Task submitted to distributed queue
/// task.schedule();    // Worker node claims task
///
/// // Worker Node
/// task.start();       // Worker begins execution
/// task.progress();    // Heartbeat/progress update
/// task.complete();    // Work finished, result returned
/// ```
///
/// ## Relationship to Other APIs
///
/// `Tasks` integrates with other Serventis APIs:
///
/// - **Queues API**: Tasks wait in queues (queue ENQUEUE → task SUBMIT, queue DEQUEUE → task SCHEDULE)
/// - **Resources API**: Tasks consume resources (resource GRANT → task START, resource DENY → task SUSPEND)
/// - **Locks API**: Tasks acquire locks during execution (task START → lock REQUEST → lock ACQUIRE)
/// - **Leases API**: Task execution may require lease (lease EXPIRE → task CANCEL)
/// - **Transactions API**: Tasks may execute transactional work (task START → transaction START)
/// - **Statuses API**: Task failure patterns inform conditions (many FAIL → DEGRADED)
/// - **Services API**: Tasks implement service operations (service REQUEST → task SUBMIT)
///
/// ## Performance Considerations
///
/// Task sign emissions operate at computational/coordination timescales (microseconds to seconds).
/// High-throughput task systems (millions of tasks per second) require minimal emission overhead.
/// Zero-allocation enum emission with ~10-20ns cost for non-transit emits ensures task
/// observability scales with system throughput.
///
/// Unlike low-level primitives (Locks, Resources at nanosecond scale), tasks typically operate
/// at higher granularity (milliseconds to seconds per task). Sign emissions are asynchronous,
/// flowing through the circuit's event queue without blocking task execution.
///
/// ## Queue Depth and Backpressure Analysis
///
/// Task signals enable queue and backpressure analysis:
///
/// - **Queue depth**: Count of SUBMIT - SCHEDULE (tasks awaiting execution)
/// - **Throughput**: Rate of COMPLETE + FAIL (completed tasks per second)
/// - **Latency**: Time from SUBMIT to START (queue wait time)
/// - **Execution time**: Time from START to COMPLETE/FAIL
/// - **Rejection rate**: Frequency of REJECT (backpressure indicator)
/// - **Failure rate**: Ratio of FAIL to COMPLETE (reliability metric)
/// - **Cancellation rate**: Frequency of CANCEL (coordination overhead)
///
/// Rising queue depth (SUBMIT >> SCHEDULE) indicates insufficient capacity. High REJECT rates
/// indicate the system is applying backpressure. Many TIMEOUT signals suggest execution budgets
/// are too aggressive or work is blocking unexpectedly.
///
/// ## Retry and Circuit Breaker Patterns
///
/// Task signals inform retry and circuit breaker logic:
///
/// ```java
/// // Retry pattern
/// task.submit();
/// task.start();
/// task.fail();        // First attempt failed
/// task.submit();      // Retry
/// task.start();
/// task.complete();    // Retry succeeded
///
/// // Circuit breaker opens
/// task.submit(); task.fail();
/// task.submit(); task.fail();
/// task.submit(); task.fail();
/// // Circuit breaker detects pattern
/// task.submit(); task.reject();  // Circuit open, fast-fail
/// ```
///
/// Observers tracking FAIL patterns can implement circuit breakers, preventing cascading
/// failures by rejecting tasks when downstream systems are unhealthy.
///
/// ## Semiotic Ascent: Task Signs → Status → Situation
///
/// Task signs translate upward into universal languages:
///
/// ### Task → Status Translation
/// - High REJECT rate → SATURATED status (capacity exhausted)
/// - Many FAIL signals → DEGRADED status (reliability issues)
/// - TIMEOUT pattern → WARNING status (performance degradation)
/// - Rising queue depth → PRESSURED status (approaching limits)
///
/// ### Task → Situation Assessment
/// - Queue depth explosion + REJECT → CAPACITY situation (scale out needed)
/// - Cascading FAIL pattern → OUTAGE situation (downstream dependency failed)
/// - TIMEOUT during transaction → CONSISTENCY situation (partial updates risk)
/// - High CANCEL rate → COORDINATION situation (excessive interruption)
///
/// This hierarchical meaning-making enables cross-domain reasoning: observers understand
/// task execution patterns' impact on service reliability, system capacity, and coordination
/// effectiveness without needing task-specific expertise.
///
/// ## Actor Systems and Reactive Streams
///
/// Task signs apply naturally to actor systems and reactive stream processing:
///
/// ### Actor Message Processing
/// ```java
/// // Actor mailbox receives message
/// task.submit();      // Message enqueued in actor mailbox
/// task.schedule();    // Actor ready to process message
/// task.start();       // Actor processes message
/// task.complete();    // Message processing complete
/// ```
///
/// ### Reactive Stream Backpressure
/// ```java
/// // Publisher emits faster than subscriber can consume
/// task.submit();      // Item submitted to stream
/// task.reject();      // Subscriber applies backpressure
///
/// // Subscriber ready
/// task.submit();      // Item submitted
/// task.schedule();    // Item scheduled for processing
/// task.start();       // Processing begins
/// task.complete();    // Processing complete, request next
/// ```
///
/// @author William David Louth
/// @since 1.0

public final class Tasks
  implements Serventis {

  private Tasks () { }

  /// A static composer function for creating Task instruments.
  ///
  /// This method can be used as a method reference with conduits as follows:
  ///
  /// Example usage:
  /// ```java
  /// final var cortex = Substrates.cortex();
  /// var task = circuit.conduit(Tasks::composer).percept(cortex.name("worker.task"));
  /// ```
  ///
  /// @param channel the channel from which to create the task
  /// @return a new Task instrument for the specified channel
  /// @throws NullPointerException if the channel param is `null`

  @New
  @NotNull
  public static Task composer (
    @NotNull final Channel < ? super Sign > channel
  ) {

    return
      new Task (
        channel.pipe ()
      );

  }

  /// A [Sign] classifies task operations that occur during asynchronous execution.
  /// These classifications enable analysis of execution patterns, queue dynamics,
  /// failure modes, and throughput in concurrent and distributed systems.
  ///
  /// ## Sign Categories
  ///
  /// Signs are organized into functional categories representing different aspects
  /// of task execution:
  ///
  /// - **Submission**: SUBMIT, REJECT, SCHEDULE
  /// - **Execution**: START, PROGRESS, SUSPEND, RESUME
  /// - **Completion**: COMPLETE, FAIL, CANCEL, TIMEOUT

  public enum Sign
    implements Serventis.Sign {

    /// Indicates task submitted to executor or queue.
    ///
    /// SUBMIT marks the entry point of asynchronous work into the system. The task
    /// enters a queue (thread pool queue, message queue, actor mailbox) awaiting
    /// execution. This is the first signal in the task lifecycle. Tracking SUBMIT
    /// counts reveals task arrival rate and system load.
    ///
    /// **Typical usage**: ExecutorService.submit(), CompletableFuture.supplyAsync(),
    /// actor mailbox enqueue, distributed task submission
    ///
    /// **Next states**: SCHEDULE (accepted), REJECT (queue full or policy violation)

    SUBMIT,

    /// Indicates task submission rejected.
    ///
    /// REJECT represents failure to accept the task for execution. This may occur
    /// due to queue saturation, rate limiting, policy violations, or capacity protection.
    /// REJECT is a backpressure signal indicating the system cannot accept additional
    /// work. High reject rates indicate insufficient capacity or excessive submission rate.
    ///
    /// **Typical usage**: Queue full, rate limiter triggered, circuit breaker open,
    /// RejectedExecutionException
    ///
    /// **Next states**: (task not accepted, may retry or fail fast)

    REJECT,

    /// Indicates task scheduled for execution.
    ///
    /// SCHEDULE represents the task being dequeued and prepared for execution. A worker
    /// (thread, actor, process) has claimed the task and will soon begin processing.
    /// The task transitions from waiting to ready state. Time between SUBMIT and SCHEDULE
    /// reveals queue wait time (latency).
    ///
    /// **Typical usage**: Task dequeued from executor queue, actor begins processing message,
    /// distributed worker claims task
    ///
    /// **Next states**: START (execution begins), CANCEL (cancelled before start)

    SCHEDULE,

    /// Indicates task execution begins.
    ///
    /// START marks the beginning of actual task execution. A worker is now actively
    /// processing the task. The task moves from ready to running state. Tracking START
    /// counts reveals concurrency level (how many tasks executing simultaneously). Time
    /// from START to completion reveals execution duration.
    ///
    /// **Typical usage**: Task callable/runnable begins execution, actor processes message,
    /// worker thread starts computation
    ///
    /// **Next states**: COMPLETE (success), FAIL (error), CANCEL (interrupted),
    /// TIMEOUT (exceeded budget), SUSPEND (yielded), PROGRESS (long-running)

    START,

    /// Indicates task reports progress.
    ///
    /// PROGRESS represents a heartbeat or progress indicator from a long-running task.
    /// This may carry semantic meaning (percentage complete) or simply indicate the task
    /// is still alive and making progress. Useful for monitoring long-running operations
    /// and detecting stuck tasks (no PROGRESS signals for extended period).
    ///
    /// **Typical usage**: Long-running computations, batch processing, distributed task
    /// heartbeat, progress callbacks
    ///
    /// **Analysis**: Absence of PROGRESS may indicate task is stuck or deadlocked

    PROGRESS,

    /// Indicates task paused or suspended.
    ///
    /// SUSPEND represents the task yielding execution, either cooperatively (await, I/O wait)
    /// or preemptively (scheduler preemption, resource unavailable). The task is not
    /// complete but is not actively executing. Common in cooperative multitasking, async/await
    /// patterns, and systems with resource constraints. SUSPEND followed by RESUME shows
    /// task is making intermittent progress.
    ///
    /// **Typical usage**: await in async code, blocking I/O, resource wait, cooperative yield,
    /// virtual thread parking
    ///
    /// **Next states**: RESUME (ready again), CANCEL (cancelled while suspended), TIMEOUT

    SUSPEND,

    /// Indicates task resumed after suspension.
    ///
    /// RESUME represents the task becoming ready to continue execution after being suspended.
    /// The awaited resource is now available, I/O completed, or scheduler has re-selected
    /// the task. RESUME transitions the task from suspended back to running state. Multiple
    /// SUSPEND/RESUME cycles common in I/O-bound or resource-constrained systems.
    ///
    /// **Typical usage**: async/await continuation, I/O completion, resource granted,
    /// virtual thread unparked
    ///
    /// **Next states**: COMPLETE, FAIL, SUSPEND (yield again), CANCEL, TIMEOUT

    RESUME,

    /// Indicates task completed successfully.
    ///
    /// COMPLETE represents successful task termination. The work is done, result is
    /// available, and the task exits normally. This is the positive terminal state.
    /// Tracking COMPLETE counts reveals throughput (tasks per second). Time from SUBMIT
    /// to COMPLETE reveals total task latency (queue + execution).
    ///
    /// **Typical usage**: Task finished without error, result returned, future completed,
    /// actor message processed successfully
    ///
    /// **Next states**: (task lifecycle complete)

    COMPLETE,

    /// Indicates task failed with error.
    ///
    /// FAIL represents task termination due to error. An exception was thrown, a business
    /// rule was violated, or the task otherwise could not complete its work. This is the
    /// negative terminal state. High fail rates indicate reliability issues, bugs, or
    /// systemic problems (downstream service failures). FAIL may trigger retries, circuit
    /// breakers, or error escalation.
    ///
    /// **Typical usage**: Exception thrown, validation failure, downstream service error,
    /// business logic rejection
    ///
    /// **Next states**: (task lifecycle complete, may trigger retry as new SUBMIT)

    FAIL,

    /// Indicates task cancelled.
    ///
    /// CANCEL represents explicit task cancellation, either before execution (while queued)
    /// or during execution (interruption). The task will not complete its work. Cancellation
    /// may be user-initiated, timeout-driven, or system-initiated (shutdown, dependency failure).
    /// High cancel rates may indicate coordination overhead or excessive timeouts.
    ///
    /// **Typical usage**: Future.cancel(), thread interrupt, graceful shutdown, upstream
    /// cancellation propagation
    ///
    /// **Next states**: (task lifecycle complete)

    CANCEL,

    /// Indicates task exceeded execution time budget.
    ///
    /// TIMEOUT represents task termination due to excessive execution duration. The task
    /// was allowed a maximum execution time and exceeded it. TIMEOUT is distinct from
    /// FAIL (explicit error) and CANCEL (explicit cancellation) - it represents a policy
    /// violation. High timeout rates indicate either aggressive budgets or unexpectedly
    /// slow execution (blocking, resource contention, downstream latency).
    ///
    /// **Typical usage**: Execution deadline exceeded, timed get() expired, watchdog timeout,
    /// SLA violation
    ///
    /// **Next states**: (task lifecycle complete, may trigger retry or circuit breaker)

    TIMEOUT

  }

  /// The `Task` class represents a named, observable task from which signs are emitted.
  ///
  /// A task is a unit of asynchronous work with observable lifecycle. Task signs make
  /// execution behavior observable, enabling queue analysis, throughput measurement,
  /// failure detection, and capacity planning.
  ///
  /// ## Usage
  ///
  /// Use domain-specific methods for all task lifecycle events:
  ///
  /// ```java
  /// // Successful execution
  /// task.submit();
  /// task.schedule();
  /// task.start();
  /// task.complete();
  ///
  /// // Failed execution
  /// task.submit();
  /// task.schedule();
  /// task.start();
  /// task.fail();
  ///
  /// // Long-running with progress
  /// task.submit();
  /// task.schedule();
  /// task.start();
  /// task.progress();
  /// task.progress();
  /// task.complete();
  ///
  /// // Rejected submission
  /// task.submit();
  /// task.reject();
  /// ```

  @Queued
  @Provided
  public static final class Task
    implements Signer < Sign > {

    private final Pipe < ? super Sign > pipe;

    private Task (
      final Pipe < ? super Sign > pipe
    ) {

      this.pipe = pipe;

    }

    /// Emits a `CANCEL` sign from this task.
    ///
    /// Represents task cancellation.

    public void cancel () {

      pipe.emit (
        CANCEL
      );

    }

    /// Emits a `COMPLETE` sign from this task.
    ///
    /// Represents successful task completion.

    public void complete () {

      pipe.emit (
        COMPLETE
      );

    }

    /// Emits a `FAIL` sign from this task.
    ///
    /// Represents task failure.

    public void fail () {

      pipe.emit (
        FAIL
      );

    }

    /// Emits a `PROGRESS` sign from this task.
    ///
    /// Represents task progress indicator.

    public void progress () {

      pipe.emit (
        PROGRESS
      );

    }

    /// Emits a `REJECT` sign from this task.
    ///
    /// Represents task submission rejection.

    public void reject () {

      pipe.emit (
        REJECT
      );

    }

    /// Emits a `RESUME` sign from this task.
    ///
    /// Represents task resumption after suspension.

    public void resume () {

      pipe.emit (
        RESUME
      );

    }

    /// Emits a `SCHEDULE` sign from this task.
    ///
    /// Represents task scheduling for execution.

    public void schedule () {

      pipe.emit (
        SCHEDULE
      );

    }

    /// Signs a task event.
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

    /// Emits a `START` sign from this task.
    ///
    /// Represents task execution start.

    public void start () {

      pipe.emit (
        START
      );

    }

    /// Emits a `SUBMIT` sign from this task.
    ///
    /// Represents task submission.

    public void submit () {

      pipe.emit (
        SUBMIT
      );

    }

    /// Emits a `SUSPEND` sign from this task.
    ///
    /// Represents task suspension.

    public void suspend () {

      pipe.emit (
        SUSPEND
      );

    }

    /// Emits a `TIMEOUT` sign from this task.
    ///
    /// Represents task timeout.

    public void timeout () {

      pipe.emit (
        TIMEOUT
      );

    }

  }

}
