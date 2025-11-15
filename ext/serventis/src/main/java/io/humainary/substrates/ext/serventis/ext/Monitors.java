// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.ext;

import io.humainary.substrates.ext.serventis.api.Serventis;
import io.humainary.substrates.ext.serventis.sdk.SignalSet;

import static io.humainary.substrates.ext.serventis.ext.Monitors.Sign.*;


/// # Monitors API
///
/// The `Monitors` API provides a framework for reporting **objective assessments** of operational
/// conditions based on signal pattern analysis. It enables systems to emit structured status reports
/// that classify a subject's operational state and the statistical confidence of that classification.
///
/// ## Purpose
///
/// This API enables systems to emit **factual observations** about operational conditions
/// without making subjective judgments about urgency or response priorities. Monitors report
/// **what is happening**, not how seriously it should be treated (see [Reporters] for that).
///
/// ## Important: Reporting vs Implementation
///
/// This API is for **reporting operational condition assessments**, not implementing monitoring systems.
/// If you have monitoring logic that analyzes metrics to determine operational state (statistical
/// analysis, anomaly detection, health checks, etc.), use this API to emit observability signals
/// about the conditions you assess. Observer agents can then reason about system health trends,
/// stability patterns, and operational trajectories without coupling to your analysis algorithms.
///
/// **Example**: Your monitoring agent analyzes error rates and latency metrics. When it determines
/// the system is DEGRADED with MEASURED confidence, call `monitor.emit(DEGRADED, MEASURED)`.
/// The signals enable meta-observability: observing the monitoring assessments themselves to
/// understand how operational conditions evolve and propagate through complex systems.
///
/// ## Key Concepts
///
/// - **Monitor**: An instrument that emits signal assessments for a named subject
/// - **Signal**: A pairing of operational sign with statistical confidence
/// - **Sign**: The operational state classification (7 distinct states)
/// - **Confidence**: The statistical certainty of the sign assessment
///
/// ## Operational Signs
///
/// The API defines 7 operational signs forming a state space:
///
/// | Sign         | Stability | Meaning                                           |
/// |--------------|-----------|---------------------------------------------------|
/// | CONVERGING   | Improving | Stabilizing toward reliable operation             |
/// | STABLE       | Nominal   | Operating within expected parameters              |
/// | DIVERGING    | Degrading | Destabilizing with increasing variations          |
/// | ERRATIC      | Chaotic   | Unpredictable behavior, irregular transitions     |
/// | DEGRADED     | Impaired  | Reduced performance, elevated errors              |
/// | DEFECTIVE    | Failing   | Predominantly failed operations                   |
/// | DOWN         | Failed    | Entirely non-operational                          |
///
/// ## Relationship to Other APIs
///
/// `Monitors` sits at the foundation of the observability hierarchy:
///
/// ```
/// Raw signals → Monitors (conditions) → Reporters (situations) → Actions
/// ```
///
/// - **Probes API**: Provides raw observation data that feeds into condition analysis
/// - **Reporters API**: Consumes monitor conditions to produce situational assessments
/// - **Services API**: Service signals can inform condition classification
///
/// ## Confidence Progression
///
/// Confidence typically progresses over time as evidence accumulates:
///
/// ```
/// TENTATIVE (initial observations) → MEASURED (strong evidence) → CONFIRMED (definitive)
/// ```
///
/// This progression allows systems to react differently based on certainty—delaying
/// action for tentative assessments while responding immediately to confirmed conditions.
///
/// ## Performance Considerations
///
/// Monitor emissions are designed for high-frequency operation. Conditions are typically
/// computed from aggregated metrics rather than per-request signals, resulting in
/// moderate emission rates (1-1000 Hz typical). Emissions flow asynchronously through
/// the circuit to avoid blocking condition analysis.
///
/// @author William David Louth
/// @since 1.0

public final class Monitors
  implements Serventis {

  private Monitors () { }

  /// A static composer function for creating Monitor instruments.
  ///
  /// This method can be used as a method reference with conduits as follows:
  ///
  /// Example usage:
  /// ```java
  /// final var cortex = Substrates.cortex();
  /// var monitor = circuit.conduit(Monitors::composer).percept(cortex.name("service"));
  /// ```
  ///
  /// @param channel the channel provided by the conduit for emitting monitor signals
  /// @return a new Monitor instrument for the specified channel
  /// @throws NullPointerException if the channel param is `null`

  @New
  @NotNull
  public static Monitor composer (
    @NotNull final Channel < ? super Signal > channel
  ) {

    return
      new Monitor (
        channel.pipe ()
      );

  }


  /// The [Sign] enum represents the operational condition of a subject.

  public enum Sign
    implements Serventis.Sign {

    /// Indicates the subject of observation is stabilizing towards reliable operation,
    /// typically following initialization, scaling, or recovery

    CONVERGING,

    /// Indicates the subject of observation is operating within expected parameters,
    /// characterized by consistent response patterns and acceptable success rates

    STABLE,

    /// Indicates the subject of observation is destabilizing, with increasing variations
    /// in response times, error rates, or other operational metrics

    DIVERGING,

    /// Indicates the subject of observation is exhibiting unpredictable behavior with
    /// irregular transitions between different operational conditions

    ERRATIC,

    /// Indicates the subject of observation is partially operational, with reduced
    /// performance, elevated error rates, or delayed responses

    DEGRADED,

    /// Indicates the subject of observation is unreliable, with predominantly failed
    /// operations and significant inability to meet service level objectives.

    DEFECTIVE,

    /// Indicates the subject of observation is entirely non-operational and unable to process any requests

    DOWN

  }


  /// The [Dimension] enum represents the statistical certainty of an operational condition classification.
  ///
  /// Dimension levels (confidence) enable graduated responses to condition changes. Systems can choose to:
  /// - **Observe** tentative conditions (log, collect more data)
  /// - **Alert** on measured conditions (notify operators, prepare responses)
  /// - **Act** on confirmed conditions (trigger automated remediation)
  ///
  /// This progression reduces false positives while maintaining responsiveness to genuine issues.

  public enum Dimension
    implements Serventis.Dimension {

    /// Indicates a preliminary assessment based on initial behavioral patterns.
    ///
    /// Tentative confidence suggests the condition assessment is based on limited observations
    /// or recent changes. The system has detected a pattern but lacks sufficient evidence for
    /// stronger certainty. Typical sources: first few samples after condition change, sparse
    /// data, or high variance in observations.
    ///
    /// **Recommended response**: Observe, increase sampling, defer action.

    TENTATIVE,

    /// Indicates an established assessment with strong evidence for the condition classification.
    ///
    /// Measured confidence suggests the condition assessment is well-supported by consistent
    /// observations over time or volume. The system has collected sufficient evidence to be
    /// reasonably certain of the classification. Typical sources: sustained patterns over
    /// multiple time windows, statistically significant sample sizes, low variance.
    ///
    /// **Recommended response**: Alert operators, prepare response plans, continue monitoring.

    MEASURED,

    /// Indicates a definitive assessment with unambiguous evidence for the condition classification.
    ///
    /// Confirmed confidence indicates the condition assessment is beyond reasonable doubt,
    /// backed by overwhelming evidence or direct observation of definitive indicators.
    /// Typical sources: hard failures (service down), threshold breaches with large margins,
    /// sustained critical conditions across all metrics.
    ///
    /// **Recommended response**: Execute automated remediation, escalate immediately, take action.

    CONFIRMED

  }

  /// The [Monitor] class is used to emit an observer's assessment of a subject's
  /// operational condition as well as the statistical certainty of that assessment.
  ///
  /// ## Usage
  ///
  /// Use domain-specific methods: `monitor.stable(Confidence.CONFIRMED)`, `monitor.degraded(Confidence.TENTATIVE)`
  ///
  /// Or use the generic method: `monitor.emit(Sign.STABLE, Confidence.CONFIRMED)`
  ///
  /// Monitors provide semantic methods for reporting operational conditions as [Signal] emissions.

  @Provided
  public static final class Monitor
    implements Signaler < Sign, Dimension > {

    private static final SignalSet < Sign, Dimension, Signal > SIGNALS =
      new SignalSet <> (
        Sign.class,
        Dimension.class,
        Signal::new
      );

    private final Pipe < ? super Signal > pipe;

    private Monitor (
      final Pipe < ? super Signal > pipe
    ) {

      this.pipe = pipe;

    }

    /// Emits a `CONVERGING` sign with the specified dimension.
    ///
    /// Convenience method for reporting that the subject is stabilizing toward reliable operation.
    /// Equivalent to calling `emit(Sign.CONVERGING, dimension)`.
    ///
    /// @param dimension the statistical certainty of the assessment
    /// @throws NullPointerException if the dimension is `null`

    public void converging (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          CONVERGING,
          dimension
        )
      );

    }

    /// Emits a `DEFECTIVE` sign with the specified dimension.
    ///
    /// Convenience method for reporting that the subject is unreliable with predominantly failed operations.
    /// Equivalent to calling `emit(Sign.DEFECTIVE, dimension)`.
    ///
    /// @param dimension the statistical certainty of the assessment
    /// @throws NullPointerException if the dimension is `null`

    public void defective (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          DEFECTIVE,
          dimension
        )
      );

    }

    /// Emits a `DEGRADED` sign with the specified dimension.
    ///
    /// Convenience method for reporting that the subject is partially operational with reduced performance.
    /// Equivalent to calling `emit(Sign.DEGRADED, dimension)`.
    ///
    /// @param dimension the statistical certainty of the assessment
    /// @throws NullPointerException if the dimension is `null`

    public void degraded (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          DEGRADED,
          dimension
        )
      );

    }

    /// Emits a `DIVERGING` sign with the specified dimension.
    ///
    /// Convenience method for reporting that the subject is destabilizing with increasing variations.
    /// Equivalent to calling `emit(Sign.DIVERGING, dimension)`.
    ///
    /// @param dimension the statistical certainty of the assessment
    /// @throws NullPointerException if the dimension is `null`

    public void diverging (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          DIVERGING,
          dimension
        )
      );

    }

    /// Emits a `DOWN` sign with the specified dimension.
    ///
    /// Convenience method for reporting that the subject is entirely non-operational.
    /// Equivalent to calling `emit(Sign.DOWN, dimension)`.
    ///
    /// @param dimension the statistical certainty of the assessment
    /// @throws NullPointerException if the dimension is `null`

    public void down (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          DOWN,
          dimension
        )
      );

    }

    /// Emits an `ERRATIC` sign with the specified dimension.
    ///
    /// Convenience method for reporting that the subject is exhibiting unpredictable behavior.
    /// Equivalent to calling `emit(Sign.ERRATIC, dimension)`.
    ///
    /// @param dimension the statistical certainty of the assessment
    /// @throws NullPointerException if the dimension is `null`

    public void erratic (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          ERRATIC,
          dimension
        )
      );

    }

    /// Signals an operational condition by composing sign and dimension.
    ///
    /// @param sign      the sign component
    /// @param dimension the dimension component

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

    /// Emits a `STABLE` sign with the specified dimension.
    ///
    /// Convenience method for reporting that the subject is operating within expected parameters.
    /// Equivalent to calling `emit(Sign.STABLE, dimension)`.
    ///
    /// @param dimension the statistical certainty of the assessment
    /// @throws NullPointerException if the dimension is `null`

    public void stable (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          STABLE,
          dimension
        )
      );

    }

  }

  /// The [Signal] record represents the assessed operational condition of a subject within some context.
  /// It includes the sign classification as well as the statistical certainty of that classification.
  ///
  /// @param sign      the operational sign classification
  /// @param dimension the statistical certainty of the sign assessment

  @Provided
  public record Signal(
    Sign sign,
    Dimension dimension
  ) implements Serventis.Signal < Sign, Dimension > { }

}
