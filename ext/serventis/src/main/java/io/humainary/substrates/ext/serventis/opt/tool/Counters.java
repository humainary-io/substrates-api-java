// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.opt.tool;

import io.humainary.substrates.ext.serventis.api.Serventis;

import static io.humainary.substrates.ext.serventis.opt.tool.Counters.Sign.*;

/// # Counters API
///
/// The `Counters` API provides a structured and minimal interface for observing
/// monotonically increasing numeric values in systems. It enables systems to emit
/// **semantic signals** representing counter operations and boundary conditions.
///
/// ## Purpose
///
/// This API is designed to support **observability and reasoning** about cumulative
/// metrics in systems. By modeling counter interactions as composable signals, it
/// enables introspection of accumulation patterns, boundary violations, and operational
/// events without coupling to specific implementation details.
///
/// ## Important: Reporting vs Implementation
///
/// This API is for **reporting counter semantics**, not implementing counters.
/// If you have an actual counter implementation (AtomicLong, database column, etc.),
/// use this API to emit observability signals about operations performed on it.
/// Observer agents can then reason about patterns, detect anomalies, and derive
/// higher-level metrics without coupling to your implementation details.
///
/// **Example**: When your code increments an AtomicLong request counter, you would
/// also call `counter.increment()` to emit a signal that observers can process.
/// The signal enables meta-observability: observing the observability instrumentation
/// itself to understand system behavior.
///
/// ## Key Concepts
///
/// - **Counter**: A named subject that emits signs describing operations performed against it
/// - **Sign**: An enumeration of distinct operation types: `INCREMENT`, `OVERFLOW`, `RESET`
///
/// ## Signs and Semantics
///
/// | Sign        | Description                                               |
/// |-------------|-----------------------------------------------------------|
/// | `INCREMENT` | The counter increased                                     |
/// | `OVERFLOW`  | The counter exceeded its maximum and wrapped/reset        |
/// | `RESET`     | The counter was explicitly reset to zero                  |
///
/// ## Semantic Distinctions
///
/// - **INCREMENT**: Normal operational sign - expected accumulation
/// - **OVERFLOW**: Exceptional boundary sign - automatic wrap due to numeric limits
/// - **RESET**: Intentional operational sign - explicit zeroing by operator/agent
///
/// ## Use Cases
///
/// - Tracking request counts, bytes processed, events handled
/// - Monitoring cumulative system metrics over time
/// - Detecting boundary violations and numeric domain issues
/// - Building rate and velocity calculations through observer agents
///
/// ## Relationship to Other APIs
///
/// `Counters` signals can inform higher-level abstractions:
///
/// - **Statuses API**: Overflow patterns may indicate DEGRADED or ERRATIC conditions
/// - **Gauges API**: Counters represent the **monotonic subset** of gauge behavior.
///   Use Counters for metrics that never legitimately decrease (requests, bytes, events).
///   Use Gauges for bidirectional metrics (connections, queue depth, utilization).
///   Key difference: Counters only increment, while Gauges support both INCREMENT and DECREMENT.
/// - Observer agents translate counter signals into capacity, rate, or health signs
///
/// ## Performance Considerations
///
/// Counter sign emissions are designed for high-frequency operation (10M-50M Hz).
/// Zero-allocation enum emission with ~10-20ns cost for non-transit emits.
/// Signs flow asynchronously through the circuit's event queue.
///
/// @author William David Louth
/// @since 1.0

public final class Counters
  implements Serventis {

  private Counters () { }

  /// A static composer function for creating Counter instruments.
  ///
  /// This method can be used as a method reference with conduits as follows:
  ///
  /// Example usage:
  /// ```java
  /// final var cortex = Substrates.cortex();
  /// var counter = circuit.conduit(Counters::composer).percept(cortex.name("requests"));
  /// ```
  ///
  /// @param channel the channel from which to create the counter
  /// @return a new Counter instrument for the specified channel
  /// @throws NullPointerException if the channel param is `null`

  @New
  @NotNull
  public static Counter composer (
    @NotNull final Channel < ? super Sign > channel
  ) {

    return
      new Counter (
        channel.pipe ()
      );

  }

  /// A [Sign] represents the kind of action being observed in a counter interaction.
  ///
  /// These signs distinguish between normal operations (INCREMENT), intentional
  /// control (RESET), and exceptional conditions (OVERFLOW).

  public enum Sign
    implements Serventis.Sign {

    /// Indicates the counter was incremented.
    ///
    /// This sign represents normal counter accumulation.

    INCREMENT,

    /// Indicates the counter exceeded its maximum value and wrapped.
    ///
    /// Overflow reveals boundary violations where the counter's numeric domain
    /// was exceeded. This is typically an exceptional condition that may require
    /// architectural attention (wider numeric type, reset policies, etc.).

    OVERFLOW,

    /// Indicates the counter was explicitly reset to zero.
    ///
    /// This sign represents intentional reset operations, distinct from overflow
    /// which is an automatic boundary condition.

    RESET

  }

  /// The [Counter] class represents a named, observable counter from which signs are emitted.
  ///
  /// ## Usage
  ///
  /// Use domain-specific methods: `counter.increment()`, `counter.overflow()`, `counter.reset()`
  ///
  /// Counters provide semantic methods for reporting counter operation events.

  @Queued
  @Provided
  public static final class Counter
    implements Signer < Sign > {

    private final Pipe < ? super Sign > pipe;

    private Counter (
      final Pipe < ? super Sign > pipe
    ) {

      this.pipe = pipe;

    }

    /// Emits an increment sign from this counter.

    public void increment () {

      pipe.emit (
        INCREMENT
      );

    }

    /// Emits an overflow sign from this counter.

    public void overflow () {

      pipe.emit (
        OVERFLOW
      );

    }

    /// Emits a reset sign from this counter.

    public void reset () {

      pipe.emit (
        RESET
      );

    }

    /// Signs a counter event.
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

  }

}
