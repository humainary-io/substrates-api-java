// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.opt.exec;

import io.humainary.substrates.ext.serventis.api.Serventis;

import static io.humainary.substrates.ext.serventis.opt.exec.Processes.Sign.*;

/// # Processes API
///
/// The `Processes` API provides a structured framework for observing OS-level process lifecycle
/// in systems that spawn, manage, supervise, or coordinate operating system processes. It enables
/// emission of semantic signals about process creation, execution, termination, failures, and
/// restarts, making process management observable for reliability analysis, supervision patterns,
/// and operational reasoning.
///
/// ## Purpose
///
/// This API enables systems to emit **rich semantic signals** about process operations, capturing
/// the complete lifecycle of OS-level execution: spawning, starting, stopping, crashing, restarting,
/// killing, and suspension. Process observability reveals supervision effectiveness, failure modes,
/// restart patterns, and stability metrics critical for understanding system-level behavior.
///
/// ## Important: Observability vs Implementation
///
/// This API is for **reporting process semantics**, not implementing process managers.
/// When your system manages OS processes (process supervisors, init systems, container runtimes,
/// daemon managers, systemd services, Docker containers, Kubernetes pods), use this API to emit
/// observability signals about process operations. Meta-level observers can then reason about
/// process stability, supervision effectiveness, and failure patterns without coupling to your
/// process management implementation details.
///
/// **Example**: When your supervisor spawns a new process, call `process.spawn()`. When the
/// process begins execution, call `process.start()`. If it crashes, call `process.crash()`.
/// If your supervision policy restarts it, call `process.restart()`. These signals enable
/// meta-observability of process management patterns, revealing stability issues and supervision
/// effectiveness.
///
/// ## Key Concepts
///
/// - **Process**: An OS-level execution unit with observable lifecycle (PID, executable, environment)
/// - **Sign**: The type of process operation (SPAWN, START, STOP, CRASH, RESTART, etc.)
/// - **Lifecycle**: The progression from spawn through execution to termination
/// - **Supervision**: Automatic restart and recovery of failed processes
/// - **Signals**: OS-level signals (SIGTERM, SIGKILL, SIGSTOP, SIGCONT)
///
/// ## Process Lifecycle
///
/// ### Successful Execution
/// ```
/// SPAWN → START → STOP
/// ```
///
/// ### Failed Execution
/// ```
/// SPAWN → START → FAIL (non-zero exit code)
/// ```
///
/// ### Crash with Restart
/// ```
/// SPAWN → START → CRASH (segfault, uncaught exception)
/// → RESTART → START → STOP
/// ```
///
/// ### Failed Process with Supervision
/// ```
/// SPAWN → START → FAIL → RESTART → START → STOP
/// ```
///
/// ### Forcible Termination
/// ```
/// START → KILL (SIGKILL, force termination)
/// ```
///
/// ### Suspension Pattern
/// ```
/// START → SUSPEND (SIGSTOP, pause) → RESUME (SIGCONT) → STOP
/// ```
///
/// ### Multiple Restart Attempts
/// ```
/// SPAWN → START → CRASH → RESTART → START → CRASH → RESTART → START → STOP
/// ```
///
/// ## Signal Categories
///
/// The API defines signals across process lifecycle phases:
///
/// ### Creation Phase
/// - **SPAWN**: New process created (fork, exec, subprocess creation)
///
/// ### Execution Phase
/// - **START**: Process execution begins (after spawn, or after restart)
///
/// ### Termination Phase
/// - **STOP**: Process stopped cleanly (exit 0, graceful shutdown)
/// - **FAIL**: Process failed (non-zero exit code)
/// - **CRASH**: Process crashed (segfault, uncaught exception, abnormal termination)
/// - **KILL**: Process forcibly terminated (SIGKILL, forced shutdown)
///
/// ### Recovery Phase
/// - **RESTART**: Process being restarted after termination
///
/// ### Suspension Phase
/// - **SUSPEND**: Process paused (SIGSTOP, container pause)
/// - **RESUME**: Process resumed after suspension (SIGCONT, container unpause)
///
/// ## The 9 Process Signs
///
/// | Sign       | Description                                                     |
/// |------------|-----------------------------------------------------------------|
/// | `SPAWN`    | New process created (fork/exec, subprocess spawn)               |
/// | `START`    | Process execution begins (after spawn or restart)               |
/// | `STOP`     | Process stopped cleanly (exit 0, graceful shutdown)             |
/// | `FAIL`     | Process failed (non-zero exit code, error condition)            |
/// | `CRASH`    | Process crashed (segfault, panic, abnormal termination)         |
/// | `KILL`     | Process forcibly terminated (SIGKILL, forced shutdown)          |
/// | `RESTART`  | Process being restarted by supervisor after termination         |
/// | `SUSPEND`  | Process suspended/paused (SIGSTOP, container pause)             |
/// | `RESUME`   | Process resumed after suspension (SIGCONT, container unpause)   |
///
/// ## Process Patterns and Use Cases
///
/// ### Simple Process Execution
/// ```java
/// process.spawn();        // fork/exec new process
/// process.start();        // Execution begins
/// process.stop();         // Clean exit (exit code 0)
/// ```
///
/// ### Failed Process
/// ```java
/// process.spawn();
/// process.start();
/// process.fail();         // Exit code 1 (or any non-zero)
/// ```
///
/// ### Crashed Process with Restart
/// ```java
/// process.spawn();
/// process.start();
/// process.crash();        // Segfault, panic, uncaught exception
/// process.restart();      // Supervisor restarting process
/// process.start();        // Second execution begins
/// process.stop();         // Clean exit on retry
/// ```
///
/// ### Supervisor with Multiple Restart Attempts
/// ```java
/// process.spawn();
/// process.start();
/// process.crash();        // First crash
/// process.restart();
/// process.start();
/// process.crash();        // Second crash
/// process.restart();
/// process.start();
/// process.stop();         // Eventually succeeds
/// ```
///
/// ### Forcible Termination
/// ```java
/// process.start();
/// process.kill();         // SIGKILL - immediate forced termination
/// ```
///
/// ### Graceful Shutdown Timeout → Kill
/// ```java
/// process.start();
/// // Attempt graceful shutdown (SIGTERM)
/// // ... timeout waiting for clean stop ...
/// process.kill();         // Escalate to SIGKILL
/// ```
///
/// ### Process Suspension (Debug/Resource Control)
/// ```java
/// process.start();
/// process.suspend();      // SIGSTOP - freeze process
/// // ... debugging, resource constraint ...
/// process.resume();       // SIGCONT - unfreeze process
/// process.stop();
/// ```
///
/// ### Container Lifecycle
/// ```java
/// // Container created and started
/// process.spawn();
/// process.start();
///
/// // Container paused for checkpoint
/// process.suspend();
/// // ... create checkpoint ...
/// process.resume();
///
/// // Container stopped
/// process.stop();
/// ```
///
/// ### Systemd Service Pattern
/// ```java
/// // systemd starting service
/// process.spawn();
/// process.start();
///
/// // Service crashes
/// process.crash();
///
/// // systemd restarts (Restart=always)
/// process.restart();
/// process.start();
/// ```
///
/// ## Relationship to Other APIs
///
/// `Processes` integrates with other Serventis APIs:
///
/// - **Tasks API**: Processes contain tasks (process START → task SUBMIT)
/// - **Services API**: Processes host services (process CRASH → service FAIL/DISCONNECT)
/// - **Resources API**: Process creation requires resources (resource GRANT → process SPAWN)
/// - **Statuses API**: Process crash patterns inform conditions (many CRASH → DEGRADED)
/// - **Situations API**: Process instability influences situations (crash loop → CRITICAL)
/// - **Locks API**: Process termination may abandon locks (process CRASH → lock ABANDON)
/// - **Leases API**: Process crashes may cause lease expiration (process CRASH → lease EXPIRE)
///
/// ## Performance Considerations
///
/// Process sign emissions operate at system/coordination timescales (milliseconds to seconds).
/// OS process lifecycle events are relatively infrequent compared to in-process operations
/// (locks, resources). Emission overhead (~10-20ns) is negligible compared to actual process
/// operations (fork: ~100μs, exec: ~1ms, container start: ~100ms).
///
/// Unlike low-level primitives (Locks, Resources at nanosecond scale), processes operate
/// at coarse granularity (milliseconds to hours per lifecycle). Sign emissions are asynchronous,
/// flowing through the circuit's event queue without blocking process management operations.
///
/// ## Supervision and Restart Analysis
///
/// Process signals enable supervision pattern analysis:
///
/// - **Restart rate**: Frequency of RESTART signals
/// - **Crash rate**: Ratio of CRASH to STOP (stability metric)
/// - **Kill rate**: Frequency of KILL (indicates forced termination need)
/// - **Uptime**: Time from START to STOP/FAIL/CRASH
/// - **Restart latency**: Time from CRASH/FAIL to START
/// - **Success rate**: Ratio of STOP to (FAIL + CRASH + KILL)
///
/// High restart rates (many RESTART signals) indicate instability requiring investigation.
/// Crash loops (CRASH → RESTART → START → CRASH) suggest fundamental process issues.
/// Frequent KILL signals indicate processes not responding to graceful shutdown (SIGTERM).
///
/// ## Exit Codes and Termination Reasons
///
/// While this API focuses on **semantic lifecycle signals**, implementations can correlate
/// signals with specific exit codes or termination reasons:
///
/// - **STOP**: exit code 0 (success)
/// - **FAIL**: exit codes 1-255 (application error)
/// - **CRASH**: signals 11 (SIGSEGV), 6 (SIGABRT), 8 (SIGFPE), etc.
/// - **KILL**: signal 9 (SIGKILL)
///
/// The observability layer can emit additional context via separate channels while maintaining
/// clean semantic separation in the core lifecycle signals.
///
/// ## Process Hierarchies and Relationships
///
/// SPAWN signals can represent both:
/// - **Direct spawning**: Parent process creates child (fork/exec)
/// - **Supervisor spawning**: Supervisor creates managed process
/// - **Container spawning**: Runtime creates container process
///
/// Process hierarchies create observability patterns:
/// ```java
/// // Parent process
/// parentProcess.start();
///
/// // Parent spawns children
/// childProcess1.spawn();
/// childProcess1.start();
///
/// childProcess2.spawn();
/// childProcess2.start();
///
/// // Child crashes, parent restarts it
/// childProcess1.crash();
/// childProcess1.restart();
/// childProcess1.start();
/// ```
///
/// ## Semiotic Ascent: Process Signs → Status → Situation
///
/// Process signs translate upward into universal languages:
///
/// ### Process → Status Translation
/// - High CRASH rate → DEGRADED status (stability issues)
/// - RESTART pattern → WARNING status (supervision active)
/// - Frequent KILL → DEFECTIVE status (processes unresponsive)
/// - Crash loop (rapid RESTART) → CRITICAL status (failing to recover)
///
/// ### Process → Situation Assessment
/// - Crash loop → OUTAGE situation (service unavailable)
/// - Many KILL signals → CAPACITY situation (resource exhaustion, OOM)
/// - CRASH during transaction → CONSISTENCY situation (potential data corruption)
/// - Supervisor exhausted restarts → CRITICAL situation (requires intervention)
///
/// This hierarchical meaning-making enables cross-domain reasoning: observers understand
/// process behavior's impact on service reliability, system capacity, and data consistency
/// without needing process-specific expertise.
///
/// ## Container and Orchestration Integration
///
/// While focused on OS processes, these signs apply naturally to containerized environments:
///
/// ### Docker Container Lifecycle
/// ```java
/// container.spawn();      // docker run
/// container.start();      // Container entrypoint executes
/// container.stop();       // docker stop (SIGTERM → graceful shutdown)
///
/// // Or forcible stop
/// container.kill();       // docker kill (SIGKILL)
/// ```
///
/// ### Kubernetes Pod Lifecycle
/// ```java
/// pod.spawn();            // Pod scheduled and created
/// pod.start();            // Container starts
/// pod.crash();            // Container exits with error
/// pod.restart();          // Restart policy triggers
/// pod.start();            // Container restarted
/// ```
///
/// ### Health Check Patterns
/// ```java
/// pod.start();
/// // Liveness probe fails
/// pod.crash();
/// pod.restart();          // Kubernetes restarts container
/// pod.start();
/// ```
///
/// @author William David Louth
/// @since 1.0

public final class Processes
  implements Serventis {

  private Processes () { }

  /// A static composer function for creating Process instruments.
  ///
  /// This method can be used as a method reference with conduits as follows:
  ///
  /// Example usage:
  /// ```java
  /// final var cortex = Substrates.cortex();
  /// var process = circuit.conduit(Processes::composer).percept(cortex.name("worker.process"));
  /// ```
  ///
  /// @param channel the channel from which to create the process
  /// @return a new Process instrument for the specified channel
  /// @throws NullPointerException if the channel param is `null`

  @New
  @NotNull
  public static Process composer (
    @NotNull final Channel < ? super Sign > channel
  ) {

    return
      new Process (
        channel.pipe ()
      );

  }

  /// A [Sign] classifies process operations that occur during OS-level execution lifecycle.
  /// These classifications enable analysis of process stability, supervision effectiveness,
  /// failure modes, and recovery patterns in systems managing OS processes.
  ///
  /// ## Sign Categories
  ///
  /// Signs are organized into functional categories representing different aspects
  /// of process lifecycle:
  ///
  /// - **Creation**: SPAWN
  /// - **Execution**: START
  /// - **Termination**: STOP, FAIL, CRASH, KILL
  /// - **Recovery**: RESTART
  /// - **Suspension**: SUSPEND, RESUME

  public enum Sign
    implements Serventis.Sign {

    /// Indicates new process created.
    ///
    /// SPAWN marks process creation through fork/exec, subprocess spawn, or container
    /// creation. This is the entry point of a new process into the system. The process
    /// has been allocated but may not yet be executing. Tracking SPAWN counts reveals
    /// process creation rate and system churn.
    ///
    /// **Typical usage**: fork(), exec(), subprocess.Popen(), docker run, container create
    ///
    /// **Next states**: START (execution begins)

    SPAWN,

    /// Indicates process execution begins.
    ///
    /// START marks the beginning of actual process execution. The process is now actively
    /// running its entry point (main function, container entrypoint). This may occur after
    /// SPAWN (initial start) or after RESTART (supervised restart). Tracking START counts
    /// reveals process activation rate. Time from SPAWN to START reveals creation overhead.
    ///
    /// **Typical usage**: Process begins executing after spawn, service starts, container
    /// entrypoint runs
    ///
    /// **Next states**: STOP (success), FAIL (error), CRASH (abnormal), KILL (forced),
    /// SUSPEND (paused)

    START,

    /// Indicates process stopped cleanly.
    ///
    /// STOP represents successful process termination with exit code 0. The process completed
    /// its work normally and shut down gracefully. This is the positive terminal state for
    /// process lifecycle. Tracking STOP counts reveals successful completion rate. Time from
    /// START to STOP reveals process lifetime.
    ///
    /// **Typical usage**: exit(0), graceful shutdown, clean termination, successful completion
    ///
    /// **Next states**: (process lifecycle complete, may see new SPAWN for new instance)

    STOP,

    /// Indicates process failed.
    ///
    /// FAIL represents process termination with non-zero exit code. The process encountered
    /// an error condition and terminated unsuccessfully. This differs from CRASH (abnormal
    /// termination) - FAIL is controlled error termination. May trigger supervisor restart.
    ///
    /// **Typical usage**: exit(1), application error, validation failure, configuration error
    ///
    /// **Next states**: RESTART (if supervised), or terminal failure

    FAIL,

    /// Indicates process crashed.
    ///
    /// CRASH represents abnormal process termination due to unhandled signals, segmentation
    /// faults, uncaught exceptions, or panic conditions. The process did not exit cleanly.
    /// This is the negative terminal state indicating serious process failure. High crash
    /// rates indicate bugs, resource issues, or stability problems. May trigger supervisor
    /// restart or core dump generation.
    ///
    /// **Typical usage**: SIGSEGV, SIGABRT, panic, uncaught exception, core dump
    ///
    /// **Next states**: RESTART (if supervised), or terminal crash

    CRASH,

    /// Indicates process forcibly terminated.
    ///
    /// KILL represents immediate forced termination via SIGKILL or equivalent. The process
    /// had no opportunity for cleanup or graceful shutdown. Typically used when graceful
    /// shutdown (SIGTERM) fails or times out. High kill rates indicate processes not
    /// responding to shutdown signals or aggressive timeout policies.
    ///
    /// **Typical usage**: SIGKILL, docker kill, forced termination, timeout escalation
    ///
    /// **Next states**: (process lifecycle complete, may see RESTART if supervised)

    KILL,

    /// Indicates process being restarted.
    ///
    /// RESTART represents supervision policy triggering process restart after STOP, FAIL,
    /// CRASH, or KILL. The supervisor has decided to create a new instance of the process.
    /// This bridges termination and new execution. High restart rates indicate instability
    /// requiring investigation. Restart loops (rapid RESTART cycles) suggest fundamental issues.
    ///
    /// **Typical usage**: systemd Restart=always, supervisor restart, container restart policy
    ///
    /// **Next states**: START (new execution begins)

    RESTART,

    /// Indicates process suspended.
    ///
    /// SUSPEND represents process pause via SIGSTOP or container pause. The process is
    /// frozen - not executing but state preserved. Common for debugging, resource management,
    /// or checkpoint creation. The process will not make progress until resumed. Time in
    /// SUSPEND state reveals suspension duration.
    ///
    /// **Typical usage**: SIGSTOP, docker pause, checkpoint creation, resource constraint
    ///
    /// **Next states**: RESUME (unfrozen), KILL (terminated while suspended)

    SUSPEND,

    /// Indicates process resumed after suspension.
    ///
    /// RESUME represents process unfreezing via SIGCONT or container unpause. The process
    /// continues execution from exactly where it was suspended. State fully preserved across
    /// suspension. Multiple SUSPEND/RESUME cycles common in resource-constrained environments
    /// or debugging scenarios.
    ///
    /// **Typical usage**: SIGCONT, docker unpause, checkpoint restoration, resource available
    ///
    /// **Next states**: STOP, FAIL, CRASH, KILL, SUSPEND (suspend again)

    RESUME

  }

  /// The `Process` class represents a named, observable OS process from which signs are emitted.
  ///
  /// A process is an OS-level execution unit with observable lifecycle. Process signs make
  /// execution behavior observable, enabling supervision analysis, stability measurement,
  /// crash detection, and capacity planning.
  ///
  /// ## Usage
  ///
  /// Use domain-specific methods for all process lifecycle events:
  ///
  /// ```java
  /// // Successful execution
  /// process.spawn();
  /// process.start();
  /// process.stop();
  ///
  /// // Failed execution
  /// process.spawn();
  /// process.start();
  /// process.fail();
  ///
  /// // Crash with restart
  /// process.start();
  /// process.crash();
  /// process.restart();
  /// process.start();
  ///
  /// // Forcible termination
  /// process.start();
  /// process.kill();
  ///
  /// // Suspension
  /// process.start();
  /// process.suspend();
  /// process.resume();
  /// process.stop();
  /// ```

  @Queued
  @Provided
  public static final class Process
    implements Signer < Sign > {

    private final Pipe < ? super Sign > pipe;

    private Process (
      final Pipe < ? super Sign > pipe
    ) {

      this.pipe = pipe;

    }

    /// Emits a `CRASH` sign from this process.
    ///
    /// Represents abnormal process termination.

    public void crash () {

      pipe.emit (
        CRASH
      );

    }

    /// Emits a `FAIL` sign from this process.
    ///
    /// Represents process failure (non-zero exit).

    public void fail () {

      pipe.emit (
        FAIL
      );

    }

    /// Emits a `KILL` sign from this process.
    ///
    /// Represents forcible process termination.

    public void kill () {

      pipe.emit (
        KILL
      );

    }

    /// Emits a `RESTART` sign from this process.
    ///
    /// Represents process restart after termination.

    public void restart () {

      pipe.emit (
        RESTART
      );

    }

    /// Emits a `RESUME` sign from this process.
    ///
    /// Represents process resumption after suspension.

    public void resume () {

      pipe.emit (
        RESUME
      );

    }

    /// Signs a process event.
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

    /// Emits a `SPAWN` sign from this process.
    ///
    /// Represents new process creation.

    public void spawn () {

      pipe.emit (
        SPAWN
      );

    }

    /// Emits a `START` sign from this process.
    ///
    /// Represents process execution start.

    public void start () {

      pipe.emit (
        START
      );

    }

    /// Emits a `STOP` sign from this process.
    ///
    /// Represents clean process stop.

    public void stop () {

      pipe.emit (
        STOP
      );

    }

    /// Emits a `SUSPEND` sign from this process.
    ///
    /// Represents process suspension.

    public void suspend () {

      pipe.emit (
        SUSPEND
      );

    }

  }

}
