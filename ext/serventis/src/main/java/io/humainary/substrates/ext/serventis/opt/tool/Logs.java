// Copyright (c) 2025 William David Louth
package io.humainary.substrates.ext.serventis.opt.tool;

import io.humainary.substrates.ext.serventis.api.Serventis;

import static io.humainary.substrates.ext.serventis.opt.tool.Logs.Sign.*;

/// # Logs API
///
/// The `Logs` API provides a structured framework for observing logging activity from the
/// client perspective (application code using loggers). It enables emission of semantic signals
/// representing log levels, making logging behavior observable for system health analysis,
/// error pattern detection, and diagnostic volume tracking.
///
/// ## Purpose
///
/// This API enables systems to emit **semantic health signals** through logging activity,
/// capturing not the log content but the fact that logging occurred at specific severity levels.
/// By modeling log levels as observable signs, it enables pattern recognition, status translation,
/// and situational awareness based on logging patterns.
///
/// ## Design Philosophy
///
/// Styled after `java.util.logging` (JUL) with simplified, industry-standard levels. The API
/// captures what application code (the client) directly observes: semantic conditions that
/// warrant logging at specific severity levels.
///
/// ## Important: Observability vs Content
///
/// This API is for **observing logging activity**, not capturing log content or implementing
/// logging frameworks. When your application logs messages (via JUL, slf4j, log4j, etc.),
/// use this API to emit corresponding observability signals about the logging activity itself.
/// Observer agents can then reason about error rates, warning patterns, and log volume without
/// coupling to log message content or specific logging implementations.
///
/// **Example**: When your application logs an error, you would call `log.severe()` to emit
/// a signal. The signal enables meta-observability: observing the logging instrumentation
/// itself to understand system health through logging patterns.
///
/// ## Key Concepts
///
/// - **Log**: A named subject that emits signs describing logging activity
/// - **Sign**: Log level representing semantic severity: `SEVERE`, `WARNING`, `INFO`, `DEBUG`
/// - **Client Perspective**: Application code that uses loggers to report conditions
///
/// ## Signs and Semantics
///
/// | Sign      | Description                                      | Maps to            |
/// |-----------|--------------------------------------------------|--------------------|
/// | `SEVERE`  | Serious failures/errors requiring attention      | JUL SEVERE, slf4j ERROR |
/// | `WARNING` | Potential problems/concerns worth noting         | JUL WARNING, slf4j WARN |
/// | `INFO`    | Informational messages about normal operation    | JUL INFO, slf4j INFO |
/// | `DEBUG`   | Diagnostic/tracing output for troubleshooting    | JUL FINE/FINER/FINEST, slf4j DEBUG/TRACE |
///
/// ## Mapping from java.util.logging
///
/// | JUL Level | Serventis Sign | Rationale |
/// |-----------|----------------|-----------|
/// | SEVERE    | SEVERE         | Direct mapping |
/// | WARNING   | WARNING        | Direct mapping |
/// | INFO      | INFO           | Direct mapping |
/// | CONFIG    | INFO           | Configuration is informational |
/// | FINE      | DEBUG          | Basic diagnostic tracing |
/// | FINER     | DEBUG          | Detailed tracing (merged) |
/// | FINEST    | DEBUG          | Finest tracing (merged) |
///
/// ## Mapping from slf4j/logback
///
/// | slf4j Level | Serventis Sign | Rationale |
/// |-------------|----------------|-----------|
/// | ERROR       | SEVERE         | Errors are severe |
/// | WARN        | WARNING        | Direct mapping |
/// | INFO        | INFO           | Direct mapping |
/// | DEBUG       | DEBUG          | Direct mapping |
/// | TRACE       | DEBUG          | Merged with debug |
///
/// ## Use Cases
///
/// - Error rate tracking and alerting
/// - Warning pattern detection (crescendos indicating degradation)
/// - Log volume analysis (spikes, silence detection)
/// - System health assessment through logging patterns
/// - Debug overhead monitoring (excessive diagnostic output)
///
/// ## Relationship to Other APIs
///
/// `Logs` signals correlate with other Serventis APIs:
///
/// - **Services API**: Service FAIL signals often correlate with Log SEVERE signals
/// - **Tasks API**: Task FAIL signals typically generate Log SEVERE signals
/// - **Processes API**: Process CRASH signals produce Log SEVERE signals
/// - **Statuses API**: Log error patterns inform Status conditions (many SEVERE → DEGRADED)
/// - **Situations API**: Log patterns influence Situation assessments (SEVERE spike → OUTAGE)
///
///
/// ## Performance Considerations
///
/// Log sign emissions operate at varying frequencies depending on environment:
/// - Production (INFO and above): 100-10K logs/sec typical
/// - Debug mode (DEBUG included): 10K-1M logs/sec possible
/// - Error conditions: 1-100 errors/sec
///
/// Zero-allocation enum emission with ~10-20ns cost for non-transit emits.
/// Signs flow asynchronously through the circuit's event queue.
/// Overhead is negligible compared to actual logging operations (microseconds to milliseconds).
///
/// ## Semiotic Ascent: Logs → Status → Situation
///
/// Log signs translate upward into universal languages:
///
/// ### Logs → Status Translation
/// - High SEVERE rate → CRITICAL status (system experiencing failures)
/// - SEVERE spike → CRITICAL status (sudden failure cascade)
/// - High WARNING rate → WARNING status (accumulating concerns)
/// - WARNING crescendo → WARNING status (degradation in progress)
/// - High DEBUG volume → VERBOSE status (excessive diagnostic output)
/// - No INFO for period → STALLED status (system not processing)
///
/// ### Status → Situation Assessment
/// - CRITICAL (SEVERE spike) → OUTAGE situation (service failing)
/// - WARNING (crescendo) → CAPACITY situation (approaching limits)
/// - VERBOSE (DEBUG flood) → PERFORMANCE situation (diagnostic overhead)
///
/// This hierarchical meaning-making enables cross-domain reasoning: observers understand
/// logging patterns' impact on service reliability and system capacity without needing
/// to parse log content or understand application-specific semantics.
///
/// @author William David Louth
/// @since 1.0

public final class Logs
  implements Serventis {

  private Logs () { }

  /// A static composer function for creating Log instruments.
  ///
  /// This method can be used as a method reference with conduits as follows:
  ///
  /// Example usage:
  /// ```java
  /// final var cortex = Substrates.cortex();
  /// var log = circuit.conduit(Logs::composer).percept(cortex.name("com.acme.PaymentService"));
  /// ```
  ///
  /// @param channel the channel from which to create the log
  /// @return a new Log instrument for the specified channel
  /// @throws NullPointerException if the channel param is `null`

  @New
  @NotNull
  public static Log composer (
    @NotNull final Channel < ? super Sign > channel
  ) {

    return
      new Log (
        channel.pipe ()
      );

  }

  /// A [Sign] represents the severity level of a logging event.
  ///
  /// Signs correspond to standard logging levels used across frameworks, enabling
  /// observation of logging activity at different semantic severities. These signs
  /// form the foundation for pattern recognition, error rate tracking, and system
  /// health assessment through logging behavior.
  ///
  /// ## Sign Semantics
  ///
  /// Signs are ordered by severity from most severe (SEVERE) to least severe (DEBUG):
  ///
  /// - **SEVERE**: Serious failures requiring immediate attention
  /// - **WARNING**: Potential problems worth noting
  /// - **INFO**: Normal operational milestones
  /// - **DEBUG**: Diagnostic information for troubleshooting

  public enum Sign
    implements Serventis.Sign {

    /// Indicates a serious failure or error condition.
    ///
    /// SEVERE represents failures, exceptions, and error conditions that require
    /// attention. High SEVERE rates indicate system instability, bugs, or infrastructure
    /// problems. SEVERE signs translate to CRITICAL or DEGRADED status depending on
    /// frequency and pattern.
    ///
    /// **Typical usage**: Unhandled exceptions, infrastructure failures, critical errors
    ///
    /// **Pattern analysis**: SEVERE spike → CRITICAL, sustained SEVERE → DEGRADED

    SEVERE,

    /// Indicates a potential problem or concerning condition.
    ///
    /// WARNING represents conditions that are concerning but not immediately critical.
    /// Warnings often indicate approaching capacity limits, degrading performance, or
    /// recoverable errors. High WARNING rates or crescendo patterns indicate system
    /// degradation in progress.
    ///
    /// **Typical usage**: Retries, fallbacks, approaching limits, validation issues
    ///
    /// **Pattern analysis**: WARNING crescendo → WARNING status, many WARNING → degrading

    WARNING,

    /// Indicates normal operational information.
    ///
    /// INFO represents significant operational milestones and normal system activity.
    /// INFO signs provide baseline context and indicate healthy system operation.
    /// Absence of INFO may indicate system stalling or processing cessation.
    ///
    /// **Typical usage**: Request completion, transaction success, lifecycle events
    ///
    /// **Pattern analysis**: Regular INFO → healthy, no INFO → STALLED status

    INFO,

    /// Indicates diagnostic or tracing information.
    ///
    /// DEBUG represents detailed diagnostic output used for troubleshooting. High DEBUG
    /// volume in production may indicate configuration issues or excessive diagnostic
    /// overhead. DEBUG is typically filtered in production environments.
    ///
    /// **Typical usage**: Variable values, execution flow, detailed state information
    ///
    /// **Pattern analysis**: High DEBUG volume → VERBOSE status (performance impact)

    DEBUG

  }

  /// The `Log` class represents a named, observable log from which signs are emitted.
  ///
  /// A log is an observable entity that emits signs corresponding to logging activity
  /// at different severity levels. Log signs make logging behavior observable, enabling
  /// error rate tracking, warning pattern detection, and system health assessment
  /// through logging patterns.
  ///
  /// ## Usage
  ///
  /// Use domain-specific methods for all logging events:
  ///
  /// ```java
  /// // Normal operation
  /// log.info();
  ///
  /// // Concerning condition
  /// log.warning();
  ///
  /// // Serious failure
  /// log.severe();
  ///
  /// // Diagnostic information
  /// log.debug();
  /// ```

  @Queued
  @Provided
  public static final class Log
    implements Signer < Sign > {

    private final Pipe < ? super Sign > pipe;

    private Log (
      final Pipe < ? super Sign > pipe
    ) {

      this.pipe = pipe;

    }

    /// Emits a `DEBUG` sign from this log.
    ///
    /// Represents diagnostic or tracing information being logged.

    public void debug () {

      pipe.emit (
        DEBUG
      );

    }

    /// Emits an `INFO` sign from this log.
    ///
    /// Represents informational message about normal operation.

    public void info () {

      pipe.emit (
        INFO
      );

    }

    /// Emits a `SEVERE` sign from this log.
    ///
    /// Represents serious failure or error condition.

    public void severe () {

      pipe.emit (
        SEVERE
      );

    }

    /// Signs a log event.
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

    /// Emits a `WARNING` sign from this log.
    ///
    /// Represents potential problem or concerning condition.

    public void warning () {

      pipe.emit (
        WARNING
      );

    }

  }

}
