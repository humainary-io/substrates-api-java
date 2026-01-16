// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.opt.tool;

import io.humainary.substrates.ext.serventis.api.Serventis;
import io.humainary.substrates.ext.serventis.sdk.SignalSet;
import io.humainary.substrates.ext.serventis.sdk.Situations;
import io.humainary.substrates.ext.serventis.sdk.Statuses;

/// # Sensors API
///
/// The `Sensors` API provides a framework for reporting **positional relationships** between
/// observed metrics and configured reference values. It enables systems to emit structured signals
/// that classify where a measured value stands relative to operational setpoints.
///
/// ## Purpose
///
/// This API enables systems to emit **objective measurements** about metric positions relative
/// to configured reference points (baselines, thresholds, targets). sensors report **where we are**
/// relative to setpoints without making judgments about urgency or required actions (see [Statuses]
/// and [Situations] for those assessments).
///
/// ## Important: Measurement vs Assessment
///
/// This API is for **reporting metric positions relative to setpoints**, not implementing
/// monitoring or alerting systems. If you have measurement logic that compares observed values
/// against reference points (queue depth vs capacity, latency vs SLO, error rate vs target),
/// use this API to emit observability signals about the positional relationships you observe.
/// Observer agents can then translate these measurements into operational assessments without
/// coupling to your metric collection infrastructure.
///
/// **Example**: Your metrics collector observes queue depth at 850 items with a baseline of 100
/// and threshold of 1000. It determines the position is ABOVE the BASELINE setpoint, so it calls
/// `sensor.above(Dimension.BASELINE)`. Separately, it may also emit `sensor.below(Dimension.THRESHOLD)`
/// since 850 < 1000. These signals enable downstream systems to reason about operational state
/// without knowing specific metric values or thresholds.
///
/// ## Key Concepts
///
/// - **Setpoint**: A configured reference value (baseline, threshold, or target)
/// - **Position**: Where a measured value stands relative to a setpoint (BELOW, NOMINAL, ABOVE)
/// - **Signal**: A composition of position and setpoint type
/// - **Measurement**: The act of comparing observed values to setpoints
///
/// ## Positional Signs
///
/// The API defines 3 positional signs representing measurement positions:
///
/// | Sign    | Position       | Meaning                                      |
/// |---------|----------------|----------------------------------------------|
/// | BELOW   | Under setpoint | Operating below the reference value          |
/// | NOMINAL | At setpoint    | Operating at or near the reference value     |
/// | ABOVE   | Over setpoint  | Operating above the reference value          |
///
/// ## Setpoint Types (Dimensions)
///
/// The API defines 3 setpoint types that measurements can be relative to:
///
/// | Dimension | Type          | Meaning                                      |
/// |-----------|---------------|----------------------------------------------|
/// | BASELINE  | Normal level  | Expected operating point under normal load   |
/// | THRESHOLD | Limit         | Boundary that should not be exceeded         |
/// | TARGET    | Ideal goal    | Optimal or desired operating point           |
///
/// ## Relationship to Other APIs
///
/// `Sensors` forms the foundation for measurement-based observability:
///
/// ```
/// Metrics → Sensors (positions) → Statuses (assessments) → Situations (urgency)
/// ```
///
/// - **Domain APIs** (Counters, Gauges, etc.): Raw operational signals
/// - **Sensors API**: Positional measurements relative to references
/// - **Statuses API**: Translates positions into operational assessments (converging, stable, diverging)
/// - **Situations API**: Assesses urgency and required response (normal, warning, critical)
///
/// ## Example Signal Combinations
///
/// The API produces 9 meaningful combinations (3 signs × 3 dimensions):
///
/// ### Queue Depth Example
/// Configuration: baseline=100, threshold=1000
///
/// - Current depth: 50 items
///   - `BELOW × BASELINE` (under normal level)
///   - `BELOW × THRESHOLD` (safely under limit)
///
/// - Current depth: 850 items
///   - `ABOVE × BASELINE` (exceeding normal)
///   - `BELOW × THRESHOLD` (still under limit, but approaching)
///
/// - Current depth: 1200 items
///   - `ABOVE × BASELINE` (far above normal)
///   - `ABOVE × THRESHOLD` (breached limit!)
///
/// ### Latency Example
/// Configuration: target=20ms, baseline=50ms, threshold=500ms
///
/// - Current latency: 15ms
///   - `BELOW × TARGET` (better than ideal)
///   - `BELOW × BASELINE` (better than normal)
///
/// - Current latency: 450ms
///   - `ABOVE × BASELINE` (worse than normal)
///   - `BELOW × THRESHOLD` (approaching limit)
///
/// ### Error Rate Example
/// Configuration: target=0%, baseline=1%, threshold=5%
///
/// - Current rate: 0.5%
///   - `ABOVE × TARGET` (not perfect, but close)
///   - `BELOW × BASELINE` (better than normal)
///
/// - Current rate: 7%
///   - `ABOVE × THRESHOLD` (exceeded acceptable limit)
///
/// ## Multiple Simultaneous Signals
///
/// Systems typically emit multiple setpoint signals simultaneously to provide complete
/// positional context. For example, a queue depth measurement might emit:
///
/// ```java
/// sensor.above(Dimension.BASELINE);    // above normal
/// sensor.below(Dimension.THRESHOLD);   // but still under limit
/// ```
///
/// This allows downstream subscribers to reason about both "where we are" and "where we're
/// heading" relative to multiple reference points.
///
/// ## Mapping to Process Control
///
/// This API is inspired by process control systems where:
/// - **Setpoint** = desired value (SP)
/// - **Process Variable** = measured value (PV)
/// - **Error** = deviation from setpoint (PV - SP)
///
/// The signs (BELOW, NOMINAL, ABOVE) represent the sign of the error term, while dimensions
/// identify which setpoint is being referenced.
///
/// ## Performance Considerations
///
/// Sensor emissions are designed for moderate to high-frequency operation. Measurements
/// are typically derived from aggregated metrics (rolling averages, percentiles) rather
/// than individual operations, resulting in moderate emission rates (10-10000 Hz typical).
/// Emissions flow asynchronously through the circuit to avoid blocking measurement collection.
///
/// ## Future Extensions
///
/// The Sensors API focuses on **positional** measurements. Future APIs may add:
/// - **Drift API**: Rate-of-change measurements (increasing, steady, decreasing)
/// - **Trajectory API**: Directional movement relative to setpoints (approaching, receding)
/// - **Range API**: Position within bounded regions
///
/// These would complement Sensors by adding temporal dynamics to the spatial measurements.
///
/// @author William David Louth
/// @since 1.0

public final class Sensors
  implements Serventis {

  private Sensors () { }

  /// A static composer function for creating Setpoint instruments.
  ///
  /// This method can be used as a method reference with conduits as follows:
  ///
  /// Example usage:
  /// ```java
  /// final var cortex = Substrates.cortex();
  /// var setpoint = circuit.conduit(Sensors::composer).percept(cortex.name("queue.depth"));
  /// ```
  ///
  /// @param channel the channel provided by the conduit for emitting setpoint signals
  /// @return a new Setpoint instrument for the specified channel
  /// @throws NullPointerException if the channel param is `null`

  @New
  @NotNull
  public static Sensor composer (
    @NotNull final Channel < ? super Signal > channel
  ) {

    return
      new Sensor (
        channel.pipe ()
      );

  }


  /// The [Sign] enum represents the positional relationship between a measured value
  /// and a configured sensor.
  ///
  /// Signs are **value-neutral**—they report position without judgment about whether
  /// that position is good or bad. ABOVE a TARGET might be undesirable, while ABOVE
  /// a THRESHOLD is definitely problematic. The assessment of "goodness" happens at
  /// higher layers (Monitors, Situations).

  public enum Sign
    implements Serventis.Sign {

    /// Indicates the measured value is below the setpoint reference.
    ///
    /// The observed metric is operating under the reference value. Whether this is
    /// desirable depends on the setpoint type: BELOW a THRESHOLD is good (safe),
    /// BELOW a TARGET might indicate underutilization or lost opportunity, and
    /// BELOW a BASELINE suggests lighter than normal load.

    BELOW,

    /// Indicates the measured value is at or near the setpoint reference.
    ///
    /// The observed metric is operating at the expected reference value, within
    /// some acceptable tolerance. NOMINAL represents the "hitting the mark" state—
    /// at the BASELINE (normal), at the TARGET (ideal), or dangerously at the
    /// THRESHOLD (boundary condition).

    NOMINAL,

    /// Indicates the measured value is above the setpoint reference.
    ///
    /// The observed metric is operating over the reference value. Whether this is
    /// problematic depends on the setpoint type: ABOVE a THRESHOLD is bad (breach),
    /// ABOVE a TARGET might be acceptable or wasteful, and ABOVE a BASELINE
    /// indicates heavier than normal load.

    ABOVE

  }


  /// The [Dimension] enum represents the type of setpoint being referenced in a measurement.
  ///
  /// Dimensions identify **which reference point** the position is relative to. Different
  /// setpoint types serve different purposes in operational management:
  ///
  /// - **BASELINE**: "What is normal?" - establishes expected operating levels
  /// - **THRESHOLD**: "What is the limit?" - defines boundaries not to exceed
  /// - **TARGET**: "What is ideal?" - specifies optimization goals
  ///
  /// A single metric often has multiple setpoints, enabling rich positional reasoning:
  /// queue depth might be ABOVE × BASELINE (busier than normal) but BELOW × THRESHOLD
  /// (still within safe limits).

  public enum Dimension
    implements Category {

    /// The baseline setpoint represents normal or expected operating level.
    ///
    /// Baselines capture typical behavior under standard conditions. They serve as
    /// the reference point for "normal" and help detect deviations from expected
    /// patterns. Measurements relative to BASELINE answer "Are we operating normally?"
    ///
    /// **Example**: Queue depth baseline of 100 items represents typical steady-state
    /// load. ABOVE × BASELINE indicates busier than usual, BELOW × BASELINE indicates
    /// lighter than usual.

    BASELINE,

    /// The threshold setpoint represents a limit or boundary that should not be exceeded.
    ///
    /// Thresholds define operational constraints—capacity limits, SLO boundaries,
    /// safety margins, or resource exhaustion points. They represent "lines not to cross"
    /// in system operation. Measurements relative to THRESHOLD answer "Are we within
    /// acceptable operational bounds?"
    ///
    /// **Example**: Queue depth threshold of 1000 items represents queue capacity.
    /// ABOVE × THRESHOLD indicates queue overflow risk or actual overflow.

    THRESHOLD,

    /// The target setpoint represents an ideal or optimal operating point.
    ///
    /// Targets define desired goals for optimization—ideal latency, target throughput,
    /// perfect error rate. They represent aspirational values that may not always be
    /// achievable but guide optimization efforts. Measurements relative to TARGET
    /// answer "How close are we to ideal?"
    ///
    /// **Example**: Latency target of 20ms represents optimal user experience.
    /// NOMINAL × TARGET indicates excellent performance, ABOVE × TARGET indicates
    /// room for improvement.

    TARGET

  }

  /// The [Sensor] class is used to emit positional measurements of a metric relative
  /// to configured setpoints (baseline, threshold, target).
  ///
  /// ## Usage
  ///
  /// Use domain-specific methods: `sensor.above(Dimension.BASELINE)`, `sensor.below(Dimension.THRESHOLD)`
  ///
  /// Or use the generic method: `sensor.signal(Sign.ABOVE, Dimension.BASELINE)`
  ///
  /// Sensors provide semantic methods for reporting positional measurements as [Signal] emissions.

  @Queued
  @Provided
  public static final class Sensor
    implements Signaler < Sign, Dimension > {

    private static final SignalSet < Sign, Dimension, Signal > SIGNALS =
      new SignalSet <> (
        Sign.class,
        Dimension.class,
        Signal::new
      );

    private final Pipe < ? super Signal > pipe;

    private Sensor (
      final Pipe < ? super Signal > pipe
    ) {

      this.pipe = pipe;

    }

    /// Emits an `ABOVE` sign with the specified setpoint dimension.
    ///
    /// Convenience method for reporting that the measured value is above the sensor.
    /// Equivalent to calling `signal(Sign.ABOVE, dimension)`.
    ///
    /// @param dimension the setpoint type being referenced
    /// @throws NullPointerException if the dimension is `null`

    public void above (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.ABOVE,
          dimension
        )
      );

    }

    /// Emits a `BELOW` sign with the specified setpoint dimension.
    ///
    /// Convenience method for reporting that the measured value is below the sensor.
    /// Equivalent to calling `signal(Sign.BELOW, dimension)`.
    ///
    /// @param dimension the setpoint type being referenced
    /// @throws NullPointerException if the dimension is `null`

    public void below (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.BELOW,
          dimension
        )
      );

    }

    /// Emits a `NOMINAL` sign with the specified setpoint dimension.
    ///
    /// Convenience method for reporting that the measured value is at the sensor.
    /// Equivalent to calling `signal(Sign.NOMINAL, dimension)`.
    ///
    /// @param dimension the setpoint type being referenced
    /// @throws NullPointerException if the dimension is `null`

    public void nominal (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.NOMINAL,
          dimension
        )
      );

    }

    /// Signals a setpoint measurement by composing sign and dimension.
    ///
    /// @param sign      the positional sign component
    /// @param dimension the setpoint type dimension component

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

  }

  /// The [Signal] record represents a measured position relative to a configured sensor.
  /// It includes the positional sign (where we are) and the setpoint type (relative to what).
  ///
  /// @param sign      the positional relationship to the setpoint
  /// @param dimension the type of setpoint being referenced

  @Queued
  @Provided
  public record Signal(
    Sign sign,
    Dimension dimension
  ) implements Serventis.Signal < Sign, Dimension > { }

}
