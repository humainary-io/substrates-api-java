// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.sdk;

import io.humainary.substrates.ext.serventis.api.Serventis;

/// # Situations API
///
/// The `Situations` API provides a structured framework for expressing situational assessments
/// of operational significance within the Serventis observability framework.
///
/// ## Purpose
///
/// This API enables systems to emit **interpretive judgments** about the operational urgency
/// or significance of a subject's current state. Unlike [Statuses], which report objective
/// conditions, [Situations] communicate **how seriously** a situation should be treated by
/// responders, automation layers, or human operators.
///
/// ## Important: Assessment vs Implementation
///
/// This API is for **reporting situational significance assessments**, not implementing alerting systems.
/// If you have assessment logic that evaluates conditions and determines operational urgency
/// (incident severity classification, escalation policies, SLA evaluation, etc.), use this API
/// to emit observability signals about the situations you assess. Observer agents can then
/// reason about operational priorities, response patterns, and incident dynamics without
/// coupling to your assessment rules or alerting infrastructure.
///
/// **Example**: Your incident management system observes a service with 15% error rate for
/// 3 hours that isn't changing. It assesses this as a CRITICAL situation with CONSTANT
/// variability. It calls `situation.critical(Dimension.CONSTANT)`, which signals both
/// the urgency (immediate intervention needed) and the variability (unchanging, stuck).
/// The signals enable meta-observability: tracking situational assessments to understand
/// severity patterns, stability trends, and operational dynamics across your system.
///
/// ## Key Concepts
///
/// - **Situation**: An instrument that emits situational assessments for a named subject
/// - **Signal**: A composition of operational significance (Sign) and variability (Dimension)
/// - **Sign**: A judgment of operational urgency (NORMAL, WARNING, CRITICAL)
/// - **Dimension**: A variability characteristic (CONSTANT, VARIABLE, VOLATILE)
/// - **Assessment**: The interpretive layer that translates conditions into actionable priorities with variability context
///
/// ## Relationship to Other APIs
///
/// [Situations] sits above [Statuses] in the observability hierarchy:
///
/// ```
/// Statuses (objective conditions) → Situations (subjective assessments) → Actions/Responses
/// ```
///
/// A [Statuses.Status] might report a DEGRADED condition with MEASURED confidence, while a [Situation]
/// translates this into a WARNING situation requiring attention but not immediate intervention.
///
/// ## Semiotic Ascent: Domain Signs → Status → Situation
///
/// The Situations API represents the highest level of abstraction in the semiotic ascent hierarchy,
/// now enriched with causal architecture classification:
///
/// ### Domain Signs → Status Translation (with confidence)
/// - Lock CONTEST → Monitor DEGRADED × MEASURED
/// - Resource TIMEOUT → Monitor DEGRADED × TENTATIVE
/// - Service FAIL → Monitor DEFECTIVE × CONFIRMED
///
/// ### Status → Situation Translation (with variability dimension)
/// - Monitor STABLE × CONFIRMED (unchanging) → Situation NORMAL × CONSTANT
/// - Monitor DEGRADED × MEASURED (fluctuating) → Situation WARNING × VARIABLE
/// - Monitor DEFECTIVE × CONFIRMED (chaotic) → Situation CRITICAL × VOLATILE
///
/// ### Variability Dimension Classification
///
/// The Dimension adds a second axis of meaning—**variability characteristic**—enabling
/// appropriate response strategies:
///
/// - **CONSTANT**: No variation → predictable, allows planned response
/// - **VARIABLE**: Moderate variation → adaptive response, some unpredictability
/// - **VOLATILE**: High variation → continuous monitoring, highly unpredictable
///
/// This two-dimensional model (Sign × Dimension) enables cross-domain reasoning about both
/// **urgency** (how seriously to treat it) and **variability** (how much it's changing),
/// guiding appropriate response strategies and operational decision-making without requiring domain-specific expertise.
///
/// ## Use Cases
///
/// - **Translating monitoring data into operational priorities**: Combine severity and causal depth
/// - **Implementing escalation policies**: Route based on both urgency and remediation complexity
/// - **Driving alerting systems**: Context-aware thresholds incorporating causal architecture
/// - **Coordinating response strategies**: Match responders to problem type (tactical vs architectural)
/// - **Incident learning**: Track causal dimension distribution to identify systemic weaknesses
/// - **Resource allocation**: Prioritize architectural work (STRUCTURAL) vs tactical fixes (LOCAL)
///
/// @author William David Louth
/// @since 1.0

public final class Situations
  implements Serventis {

  private Situations () { }

  /// A static composer function for creating Situation instruments.
  ///
  /// This method can be used as a method reference with conduits as follows:
  ///
  /// Example usage:
  /// ```java
  /// final var cortex = Substrates.cortex();
  /// var situation = circuit.conduit(Situations::composer).percept(cortex.name("db.pool"));
  /// ```
  ///
  /// @param channel the channel from which to create the situation
  /// @return a new Situation instrument for the specified channel
  /// @throws NullPointerException if the channel parameter is `null`

  @New
  @NotNull
  public static Situation composer (
    @NotNull final Channel < ? super Signal > channel
  ) {

    return
      new Situation (
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
  /// While context-dependent, typical mappings from [Statuses.Status] conditions might be:
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


  /// The [Dimension] enum represents the variability characteristic of a situation.
  ///
  /// Dimension describes **how much the situation is changing or fluctuating**, capturing
  /// the stability or volatility of the situation's behavior. This variability classification
  /// is value-neutral—high variability is neither inherently good nor bad—and complements
  /// the severity assessment (Sign) by describing the dynamic nature of the situation.
  ///
  /// ## Variability Spectrum
  ///
  /// The dimension captures the degree of variation in the situation's behavior:
  ///
  /// | Dimension | Variability Level | Operational Meaning                       |
  /// |-----------|-------------------|-------------------------------------------|
  /// | CONSTANT  | No variation      | Unchanging, fixed level                   |
  /// | VARIABLE  | Moderate variation| Fluctuating, changing, inconsistent       |
  /// | VOLATILE  | High variation    | Highly unstable, chaotic, rapid changes   |
  ///
  /// ## Usage Examples
  ///
  /// - **NORMAL × CONSTANT**: Baseline normal, unchanging
  /// - **WARNING × CONSTANT**: Sustained warning level, not changing
  /// - **CRITICAL × CONSTANT**: Stuck at critical level, not improving
  /// - **NORMAL × VARIABLE**: Normal with minor fluctuations
  /// - **WARNING × VARIABLE**: Warning level with fluctuation
  /// - **CRITICAL × VARIABLE**: Critical with moderate swings
  /// - **NORMAL × VOLATILE**: Normal baseline with chaotic spikes
  /// - **WARNING × VOLATILE**: Warning with unpredictable, rapid changes
  /// - **CRITICAL × VOLATILE**: Critical with chaotic, extreme variation
  ///
  /// ## Relationship to Operational Response
  ///
  /// Dimension guides **response approach and predictability**:
  ///
  /// - CONSTANT: Predictable, allows measured response planning
  /// - VARIABLE: Requires adaptive response, some unpredictability
  /// - VOLATILE: Demands continuous monitoring, highly unpredictable

  public enum Dimension
    implements Spectrum {

    /// The situation exhibits no variation, maintaining an unchanging level.
    ///
    /// Constant situations remain fixed at a particular level without fluctuation. The
    /// situation is predictable and unchanging, whether at normal, warning, or critical
    /// severity. This lack of variation allows for measured, planned responses without
    /// urgency driven by unpredictability. Note that constant is value-neutral—a situation
    /// can be constantly critical (bad) or constantly normal (good).
    ///
    /// **Example**: Error rate constant at 15% for hours. **Response**: Predictable state, plan measured intervention.

    CONSTANT,

    /// The situation exhibits moderate variation and fluctuation.
    ///
    /// Variable situations show inconsistent behavior with noticeable changes over time.
    /// The situation fluctuates between states but not to extreme degrees. This variability
    /// requires adaptive response strategies and closer monitoring than steady situations,
    /// but remains somewhat predictable.
    ///
    /// **Example**: Error rate fluctuating between 5-15%. **Response**: Monitor patterns, adapt response to current state.

    VARIABLE,

    /// The situation exhibits high instability with rapid, chaotic changes.
    ///
    /// Volatile situations demonstrate extreme variability with unpredictable, rapid swings.
    /// The situation is highly unstable and difficult to predict, characterized by chaotic
    /// behavior patterns. This volatility demands continuous monitoring and readiness for
    /// rapid response as conditions shift dramatically.
    ///
    /// **Example**: Error rate spiking chaotically between 0-50%. **Response**: Continuous monitoring, immediate response capability.

    VOLATILE

  }


  /// A [Signal] represents a situational assessment composed of operational significance (Sign)
  /// and variability characteristic (Dimension).
  ///
  /// Signals combine **how seriously** a situation should be treated (NORMAL, WARNING, CRITICAL)
  /// with **how much the situation varies** (CONSTANT, VARIABLE, VOLATILE).
  ///
  /// This composition enables nuanced incident response:
  /// - **CRITICAL × CONSTANT**: Critical level, unchanging (stuck, not improving)
  /// - **WARNING × VARIABLE**: Warning level with fluctuation (requires adaptive response)
  /// - **CRITICAL × VOLATILE**: Critical with chaotic swings (demands continuous monitoring)
  /// - **NORMAL × CONSTANT**: Stable baseline (predictable, good)
  /// - **WARNING × CONSTANT**: Sustained warning (not getting worse, but not improving)
  ///
  /// @param sign      the operational significance assessment
  /// @param dimension the variability characteristic

  public record Signal(
    @NotNull Sign sign,
    @NotNull Dimension dimension
  )
    implements Serventis.Signal < Sign, Dimension > { }


  /// A [Situation] emits a [Signal] to express the assessed operational urgency
  /// and variability characteristic of a subject's current state.
  ///
  /// A Situation interprets input from lower layers (such as Statuses)
  /// and publishes a distilled, context-aware judgment about how the
  /// current situation should be treated by responders or automation layers.
  ///
  /// ## Usage
  ///
  /// Use domain-specific methods with variability dimension:
  /// ```java
  /// situation.critical(Dimension.CONSTANT);   // Critical, unchanging
  /// situation.warning(Dimension.VARIABLE);    // Warning with fluctuation
  /// situation.critical(Dimension.VOLATILE);   // Critical with chaotic swings
  /// situation.normal(Dimension.CONSTANT);     // Normal, stable baseline
  /// ```
  ///
  /// Or use the generic signal method:
  /// ```java
  /// situation.signal(Sign.CRITICAL, Dimension.VOLATILE);
  /// ```
  ///
  /// Situations provide semantic methods for assessing operational significance
  /// combined with variability characteristic.

  @Queued
  @Provided
  public static final class Situation
    implements Signaler < Sign, Dimension > {

    private static final SignalSet < Sign, Dimension, Signal > SIGNALS =
      new SignalSet <> (
        Sign.class,
        Dimension.class,
        Signal::new
      );

    private final Pipe < ? super Signal > pipe;

    private Situation (
      final Pipe < ? super Signal > pipe
    ) {

      this.pipe = pipe;

    }

    /// Emits a `CRITICAL` signal with the specified variability dimension.
    ///
    /// Assesses the situation as serious and demanding prompt intervention. The dimension
    /// indicates the variability of the situation, guiding response strategy based on
    /// predictability (CONSTANT) versus unpredictability (VOLATILE).
    ///
    /// @param dimension the variability characteristic
    /// @throws NullPointerException if the dimension is `null`

    public void critical (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.CRITICAL,
          dimension
        )
      );

    }

    /// Emits a `NORMAL` signal with the specified variability dimension.
    ///
    /// Assesses the situation as posing no immediate concern and requiring no intervention.
    /// The dimension indicates the variability of the normal state (CONSTANT baseline,
    /// VARIABLE fluctuation within normal bounds, etc.).
    ///
    /// @param dimension the variability characteristic
    /// @throws NullPointerException if the dimension is `null`

    public void normal (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.NORMAL,
          dimension
        )
      );

    }

    /// Signals a situational assessment event.
    ///
    /// Generic method for emitting any Sign × Dimension combination. Prefer using
    /// the domain-specific methods (`critical()`, `warning()`, `normal()`) for clarity.
    ///
    /// @param sign      the operational significance assessment
    /// @param dimension the variability characteristic

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

    /// Emits a `WARNING` signal with the specified variability dimension.
    ///
    /// Assesses the situation as requiring attention but not yet critical. The dimension
    /// indicates the variability—whether it's unchanging (CONSTANT), fluctuating (VARIABLE),
    /// or highly unstable (VOLATILE).
    ///
    /// @param dimension the variability characteristic
    /// @throws NullPointerException if the dimension is `null`

    public void warning (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.WARNING,
          dimension
        )
      );

    }

  }

}
