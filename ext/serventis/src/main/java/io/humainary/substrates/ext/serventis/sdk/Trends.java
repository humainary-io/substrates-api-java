// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.sdk;

import io.humainary.substrates.ext.serventis.api.Serventis;

import static io.humainary.substrates.ext.serventis.sdk.Trends.Sign.*;

/// # Trends API
///
/// The `Trends` API provides a vocabulary for expressing statistical pattern detection
/// in time-series observations. It enables systems to emit signals describing what
/// patterns have been detected in a process, based on techniques like Nelson's 8 Rules
/// for Statistical Process Control (SPC).
///
/// ## Purpose
///
/// This API offers a universal vocabulary for trend/pattern detection that abstracts
/// over specific detection algorithms. When an analyzer observes a measurement stream
/// and detects patterns (shifts, spikes, cycles, chaos), it emits signals using this
/// vocabulary to drive higher-level condition assessment (Statuses) and urgency
/// classification (Situations).
///
/// ## Key Insight: Pattern Detection
///
/// We are always watching processes unfold - whether services, tasks, queues, or
/// situations. Trend detection answers: **how is it behaving over time?**
///
/// Combined with other universal vocabularies:
/// - **Operations**: BEGIN/END (when did it run?)
/// - **Outcomes**: SUCCESS/FAIL (did it work?)
/// - **Trends**: STABLE/DRIFT/SPIKE/CYCLE/CHAOS (how is it behaving?)
///
/// ## Key Concepts
///
/// - **Trend**: An instrument that emits pattern detection signals
/// - **Sign**: The detected pattern type
///
/// ## Signs and Nelson's Rules Mapping
///
/// | Sign   | Nelson Rules | Pattern Description                    |
/// |--------|--------------|----------------------------------------|
/// | STABLE | None         | In statistical control, no pattern     |
/// | DRIFT  | 2, 3, 5, 6   | Sustained movement (shift or trend)    |
/// | SPIKE  | 1            | Sudden extreme deviation (outlier)     |
/// | CYCLE  | 4            | Oscillating pattern (alternating)      |
/// | CHAOS  | 7, 8         | Erratic variation (stratified/mixture) |
///
/// ## Usage Example
///
/// ```java
/// // Create a trend instrument
/// var trend = circuit
///     .conduit(Trends::composer)
///     .percept(cortex.name("api.latency"));
///
/// // Analyzer detects patterns and emits
/// trend.stable();   // process in control
/// trend.drift();    // sustained movement detected
/// trend.spike();    // outlier detected
/// ```
///
/// ## Relationship to Other APIs
///
/// The semiotic flow:
/// ```
/// Measurements → Trends (what pattern?) → Statuses (what condition?) → Situations (how urgent?)
/// ```
///
/// - **Domain APIs**: Provide raw measurements/signals
/// - **Trends**: Pattern detection vocabulary
/// - **Statuses**: Condition assessment (STABLE, DEGRADED, etc.)
/// - **Situations**: Urgency classification (NORMAL, WARNING, CRITICAL)
///
/// @author William David Louth
/// @since 1.0

public final class Trends
  implements Serventis {

  private Trends () { }

  /// A static composer function for creating Trend instruments.
  ///
  /// This method can be used as a method reference with conduits:
  ///
  /// Example usage:
  /// ```java
  /// var trend = circuit.conduit(Trends::composer).percept(cortex.name("api.latency"));
  /// ```
  ///
  /// @param channel the channel from which to create the trend instrument
  /// @return a new Trend instrument for the specified channel
  /// @throws NullPointerException if the channel param is `null`

  @New
  @NotNull
  public static Trend composer (
    @NotNull final Channel < ? super Sign > channel
  ) {

    return
      new Trend (
        channel.pipe ()
      );

  }


  /// A [Sign] represents the detected pattern type in a time-series.
  ///
  /// These signs form the universal pattern detection vocabulary, abstracting
  /// over specific detection algorithms like Nelson's 8 Rules for SPC.

  public enum Sign
    implements Serventis.Sign {

    /// Indicates the process is in statistical control with no significant pattern.
    ///
    /// No Nelson rules triggered. The process is stable and predictable.

    STABLE,

    /// Indicates sustained movement away from the baseline.
    ///
    /// Covers Nelson Rules 2 (9 points same side), 3 (6 points trending),
    /// 5 (2 of 3 beyond 2σ), and 6 (4 of 5 beyond 1σ). The mean has shifted
    /// or there is a systematic trend.

    DRIFT,

    /// Indicates a sudden extreme deviation (outlier).
    ///
    /// Covers Nelson Rule 1 (point beyond 3σ). A single point significantly
    /// outside normal variation.

    SPIKE,

    /// Indicates an oscillating pattern.
    ///
    /// Covers Nelson Rule 4 (14 points alternating up/down). The process
    /// is cycling rather than stable or drifting.

    CYCLE,

    /// Indicates erratic, chaotic variation.
    ///
    /// Covers Nelson Rules 7 (15 within 1σ - stratification) and
    /// 8 (8 beyond 1σ - mixture). The variation pattern is abnormal.

    CHAOS

  }


  /// The [Trend] class emits pattern detection signals.
  ///
  /// ## Usage
  ///
  /// Use the semantic methods: `trend.stable()`, `trend.drift()`, etc.
  ///
  /// Trends provide the universal pattern detection vocabulary for
  /// time-series observation and statistical process control.

  @Queued
  @Provided
  public static final class Trend
    implements Signer < Sign > {

    private final Pipe < ? super Sign > pipe;

    private Trend (
      final Pipe < ? super Sign > pipe
    ) {

      this.pipe = pipe;

    }

    /// Emits a CHAOS sign indicating erratic variation.

    public void chaos () {

      pipe.emit (
        CHAOS
      );

    }

    /// Emits a CYCLE sign indicating oscillating pattern.

    public void cycle () {

      pipe.emit (
        CYCLE
      );

    }

    /// Emits a DRIFT sign indicating sustained movement.

    public void drift () {

      pipe.emit (
        DRIFT
      );

    }

    /// Emits the specified sign.
    ///
    /// @param sign the sign to emit

    @Override
    public void sign (
      @NotNull final Sign sign
    ) {

      pipe.emit (
        sign
      );

    }

    /// Emits a SPIKE sign indicating sudden deviation.

    public void spike () {

      pipe.emit (
        SPIKE
      );

    }

    /// Emits a STABLE sign indicating process in control.

    public void stable () {

      pipe.emit (
        STABLE
      );

    }

  }

}
