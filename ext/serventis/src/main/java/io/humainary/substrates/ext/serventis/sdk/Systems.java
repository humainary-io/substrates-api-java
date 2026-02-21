// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.sdk;

import io.humainary.substrates.ext.serventis.api.Serventis;

/// # Systems API
///
/// The `Systems` API provides a structured framework for observing fundamental system constraints
/// using process control vocabulary. It enables emission of semantic signals that describe the
/// operational state of core system resources: space, flow, link, and time.
///
/// ## Purpose
///
/// This API provides the **universal constraint language** in the semiotic ascent architecture.
/// Domain-specific signs (Resources, Queues, Services, etc.) translate upward into constraint
/// state assessments, enabling cross-domain reasoning about fundamental system health.
///
/// ## Design Philosophy
///
/// The API models four fundamental constraints that apply universally to any system:
///
/// - **SPACE**: Container constraint — capacity, volume, room
/// - **FLOW**: Movement constraint — throughput, bandwidth, rate
/// - **LINK**: Structural constraint — connectivity, reachability, edges
/// - **TIME**: Temporal constraint — latency, responsiveness, duration
///
/// Signs use process control vocabulary to describe constraint state:
///
/// - **NORMAL**: Operating within parameters
/// - **LIMIT**: At boundary
/// - **ALARM**: Beyond boundary, attention required
/// - **FAULT**: Failed state
///
/// ## Signal Matrix
///
/// | | SPACE | FLOW | LINK | TIME |
/// |---|---|---|---|---|
/// | **NORMAL** | Adequate room | Steady throughput | Connections stable | Response times normal |
/// | **LIMIT** | Running tight | Flow restricted | Links saturated | Timing tight |
/// | **ALARM** | Space critical | Flow blocked | Links failing | Latency critical |
/// | **FAULT** | Space exhausted | Flow stopped | Links down | Timeouts total |
///
/// ## Relationship to Other APIs
///
/// `Systems` complements [Statuses] and [Situations] in the universal SDK layer:
///
/// - **Systems**: Constraint state (NORMAL, LIMIT, ALARM, FAULT) × Constraint type (SPACE, FLOW, LINK, TIME)
/// - **Statuses**: Behavioral condition (STABLE, DEGRADED, etc.) × Confidence (TENTATIVE, MEASURED, CONFIRMED)
/// - **Situations**: Urgency (NORMAL, WARNING, CRITICAL) × Variability (CONSTANT, VARIABLE, VOLATILE)
///
/// ```
/// Domain signs → Systems (what constraint?) → Statuses (how behaving?) → Situations (how urgent?)
/// ```
///
/// ## Translation Examples
///
/// | Domain Pattern | Systems Signal | Statuses | Situations |
/// |----------------|----------------|----------|------------|
/// | Pool 90% utilized | LIMIT × SPACE | STABLE × MEASURED | NORMAL × CONSTANT |
/// | High DENY rate | ALARM × SPACE | DEGRADED × MEASURED | WARNING × CONSTANT |
/// | Services.DISCONNECT | ALARM × LINK | DEFECTIVE × CONFIRMED | CRITICAL × CONSTANT |
/// | High TIMEOUT rate | LIMIT × TIME | DIVERGING × TENTATIVE | WARNING × VARIABLE |
///
/// ## Performance Considerations
///
/// Systems signals are emitted at observation timescales. Typical emission rates are
/// moderate (1-1000 Hz) as they represent constraint assessments. Signals flow
/// asynchronously through the circuit's event queue.
///
/// @author William David Louth
/// @since 1.0

public final class Systems
  implements Serventis {

  private Systems () { }

  /// A static composer function for creating System instruments.
  ///
  /// This method can be used as a method reference with conduits as follows:
  ///
  /// Example usage:
  /// ```java
  /// final var cortex = Substrates.cortex();
  /// var system = circuit.conduit(Systems::composer).percept(cortex.name("db.pool"));
  /// ```
  ///
  /// @param channel the channel provided by the conduit for emitting system signals
  /// @return a new System instrument for the specified channel
  /// @throws NullPointerException if the channel param is `null`

  @New
  @NotNull
  public static System composer (
    @NotNull final Channel < ? super Signal > channel
  ) {

    return
      new System (
        channel.pipe ()
      );

  }


  /// The [Sign] enum represents the operational state of a system constraint
  /// using process control vocabulary.
  ///
  /// Signs form a severity progression describing constraint health:
  ///
  /// ```
  /// NORMAL → LIMIT → ALARM → FAULT
  /// (healthy)  (boundary)  (violated)  (failed)
  /// ```

  public enum Sign
    implements Serventis.Sign {

    /// Operating within standard parameters.
    ///
    /// The constraint is comfortably satisfied with adequate headroom.
    /// No action required.

    NORMAL,

    /// At constraint boundary.
    ///
    /// The constraint is being met but margins are thin. Prepare to act.

    LIMIT,

    /// Beyond constraint boundary.
    ///
    /// The constraint is violated. Attention required.

    ALARM,

    /// Constraint failed.
    ///
    /// The constraint cannot be met. Failover or recovery required.

    FAULT

  }


  /// The [Dimension] enum represents the fundamental constraint types
  /// that apply universally to any system.

  public enum Dimension
    implements Category {

    /// Container constraint — capacity, volume, room.
    ///
    /// "Running out of space."

    SPACE,

    /// Movement constraint — throughput, bandwidth, rate.
    ///
    /// "Flow is restricted."

    FLOW,

    /// Structural constraint — connectivity, reachability, edges.
    ///
    /// "The link is down."

    LINK,

    /// Temporal constraint — latency, responsiveness, duration.
    ///
    /// "Time is critical."

    TIME

  }


  /// The [Signal] record represents a system constraint assessment.
  ///
  /// @param sign      the operational state of the constraint
  /// @param dimension the type of constraint being assessed

  @Queued
  @Provided
  public record Signal(
    Sign sign,
    Dimension dimension
  ) implements Serventis.Signal < Sign, Dimension > { }


  /// The [System] class emits constraint state assessments for a named subject.
  ///
  /// ## Usage
  ///
  /// ```java
  /// system.normal(SPACE);    // Space constraint normal
  /// system.limit(TIME);      // Time constraint at limit
  /// system.alarm(FLOW);      // Flow constraint alarm
  /// system.fault(LINK);      // Link constraint fault
  /// ```

  @Queued
  @Provided
  public static final class System
    implements Signaler < Sign, Dimension > {

    private static final SignalSet < Sign, Dimension, Signal > SIGNALS =
      new SignalSet <> (
        Sign.class,
        Dimension.class,
        Signal::new
      );

    private final Pipe < ? super Signal > pipe;

    private System (
      final Pipe < ? super Signal > pipe
    ) {

      this.pipe = pipe;

    }

    /// Emits an `ALARM` signal for the specified constraint dimension.
    ///
    /// @param dimension the constraint type
    /// @throws NullPointerException if the dimension is `null`

    public void alarm (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.ALARM,
          dimension
        )
      );

    }

    /// Emits a `FAULT` signal for the specified constraint dimension.
    ///
    /// @param dimension the constraint type
    /// @throws NullPointerException if the dimension is `null`

    public void fault (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.FAULT,
          dimension
        )
      );

    }

    /// Emits a `LIMIT` signal for the specified constraint dimension.
    ///
    /// @param dimension the constraint type
    /// @throws NullPointerException if the dimension is `null`

    public void limit (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.LIMIT,
          dimension
        )
      );

    }

    /// Emits a `NORMAL` signal for the specified constraint dimension.
    ///
    /// @param dimension the constraint type
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

    /// Signals a system constraint assessment.
    ///
    /// @param sign      the operational state
    /// @param dimension the constraint type

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

}
