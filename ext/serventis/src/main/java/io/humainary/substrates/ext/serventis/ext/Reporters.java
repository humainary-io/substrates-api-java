// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.ext;

import io.humainary.substrates.ext.serventis.api.Serventis;

/// # Reporters API
///
/// The `Reporters` API provides a structured framework for expressing situational assessments
/// of operational significance within the Serventis observability framework.
///
/// ## Purpose
///
/// This API enables systems to emit **interpretive judgments** about the operational urgency
/// or significance of a subject's current state. Unlike [Monitors], which report objective
/// conditions, [Reporters] communicate **how seriously** a situation should be treated by
/// responders, automation layers, or human operators.
///
/// ## Important: Reporting vs Implementation
///
/// This API is for **reporting situational significance assessments**, not implementing alerting systems.
/// If you have assessment logic that evaluates conditions and determines operational urgency
/// (incident severity classification, escalation policies, SLA evaluation, etc.), use this API
/// to emit observability signals about the situations you assess. Observer agents can then
/// reason about operational priorities, response patterns, and incident dynamics without
/// coupling to your assessment rules or alerting infrastructure.
///
/// **Example**: Your incident management system evaluates a DEGRADED condition with multiple
/// service failures. Based on business impact, it assesses this as a CRITICAL situation and
/// calls `reporter.situation(CRITICAL)`. The signals enable meta-observability: observing
/// situational assessments themselves to understand how urgency propagates and responses evolve.
///
/// ## Key Concepts
///
/// - **Reporter**: An instrument that emits situational assessments for a named subject
/// - **Sign**: A judgment of operational significance (NORMAL, WARNING, CRITICAL)
/// - **Assessment**: The interpretive layer that translates conditions into actionable priorities
///
/// ## Relationship to Other APIs
///
/// [Reporters] sits above [Monitors] in the observability hierarchy:
///
/// ```
/// Monitors (objective conditions) → Reporters (subjective assessments) → Actions/Responses
/// ```
///
/// A [Monitors.Monitor] might report a DEGRADED condition with MEASURED confidence, while a [Reporter]
/// translates this into a WARNING situation requiring attention but not immediate intervention.
///
/// ## Use Cases
///
/// - Translating monitoring data into operational priorities
/// - Implementing escalation policies based on situational severity
/// - Driving alerting systems with context-aware thresholds
/// - Coordinating human and automated response strategies
///
/// @author William David Louth
/// @since 1.0

public final class Reporters
  implements Serventis {

  private Reporters () { }

  /// A static composer function for creating Reporter instruments.
  ///
  /// This method can be used as a method reference with conduits as follows:
  ///
  /// Example usage:
  /// ```java
  /// final var cortex = Substrates.cortex();
  /// var reporter = circuit.conduit(Reporters::composer).percept(cortex.name("db.pool"));
  /// ```
  ///
  /// @param channel the channel from which to create the reporter
  /// @return a new Reporter instrument for the specified channel
  /// @throws NullPointerException if the channel parameter is `null`

  @New
  @NotNull
  public static Reporter composer (
    @NotNull final Channel < ? super Sign > channel
  ) {

    return
      new Reporter (
        channel.pipe ()
      );

  }


  /// A [Sign] represents the assessed operational significance of a subject's current state.
  ///
  /// Signs express **how seriously** the current context should be treated—not just what
  /// is happening, but what priority it deserves in terms of attention and response.
  ///
  /// These values guide response decisions, escalation policies, and visibility within an
  /// adaptive observability framework. They form the bridge between technical conditions
  /// and operational actions.
  ///
  /// ## Typical Mappings
  ///
  /// While context-dependent, typical mappings from [Monitors.Monitor] conditions might be:
  ///
  /// - NORMAL: Stable, Converging conditions
  /// - WARNING: Diverging, Erratic, or Degraded conditions
  /// - CRITICAL: Defective or Down conditions
  ///
  /// The actual mapping depends on system criticality, SLOs, and operational policies.

  public enum Sign
    implements Serventis.Sign {

    /// The situation poses no immediate concern and requires no intervention.
    ///
    /// Normal situations indicate the subject is operating within acceptable parameters
    /// and no action is required. This is the default state for healthy systems.

    NORMAL,

    /// The situation requires attention but is not yet critical.
    ///
    /// Warning situations indicate the subject should be monitored more closely or
    /// investigated during normal business hours. Conditions are degrading but the
    /// system remains operational. Examples: elevated error rates, resource pressure,
    /// or performance degradation within tolerable bounds.

    WARNING,

    /// The situation is serious and demands prompt intervention.
    ///
    /// Critical situations indicate immediate action is required to prevent or mitigate
    /// significant service impact. The subject is in or approaching a failure state that
    /// threatens SLO compliance or user experience. Examples: service down, data loss risk,
    /// or cascading failures.

    CRITICAL

  }

  /// A [Reporter] emits a [Sign] to express an observer's
  /// current assessment of a subject's operational urgency or significance.
  ///
  /// A Reporter interprets input from lower layers (such as Monitors)
  /// and publishes a distilled, context-aware judgment about how the
  /// current situation should be treated by responders or automation layers.
  ///
  /// ## Usage
  ///
  /// Use domain-specific methods: `reporter.normal()`, `reporter.warning()`, `reporter.critical()`
  ///
  /// Reporters provide semantic methods for reporting situational assessments.

  @Provided
  public static final class Reporter
    implements Signer < Sign > {

    private final Pipe < ? super Sign > pipe;

    private Reporter (
      final Pipe < ? super Sign > pipe
    ) {

      this.pipe = pipe;

    }

    /// Emits a `CRITICAL` sign.
    ///
    /// Reports that the situation is serious and demands prompt intervention. Use this method
    /// when the subject is in or approaching a failure state that threatens SLO compliance.

    public void critical () {

      pipe.emit (
        Sign.CRITICAL
      );

    }

    /// Emits a `NORMAL` sign.
    ///
    /// Reports that the situation poses no immediate concern and requires no intervention.
    /// Use this method when the subject is operating within acceptable parameters.

    public void normal () {

      pipe.emit (
        Sign.NORMAL
      );

    }

    /// Signs a situational assessment event.
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

    /// Emits a `WARNING` sign.
    ///
    /// Reports that the situation requires attention but is not yet critical. Use this method
    /// when conditions are degrading but the system remains operational.

    public void warning () {

      pipe.emit (
        Sign.WARNING
      );

    }

  }

}
