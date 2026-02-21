// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.opt.tool;

import io.humainary.substrates.ext.serventis.api.Serventis;
import io.humainary.substrates.ext.serventis.sdk.SignalSet;

/// The `Probes` API provides a structured framework for monitoring and reporting
/// communication operations and outcomes in distributed systems. It enables observation
/// of operations from both self-perspective and observed-perspective.
///
/// ## Important: Reporting vs Implementation
///
/// This API is for **reporting communication operation outcomes**, not implementing network protocols.
/// If you have actual networking code (HTTP clients, RPC frameworks, message brokers, etc.),
/// use this API to emit observability signals about operations performed. Observer agents can
/// then reason about communication patterns, failure modes, and distributed system reliability
/// without coupling to your protocol or transport implementation details.
///
/// **Example**: Your HTTP client connects to a remote server. Call `probe.connect()` to emit
/// a RELEASE signal ("I am connecting"). When you observe the server has connected, call
/// `probe.connected()` to emit a RECEIPT signal ("It connected"). The signals enable
/// meta-observability: observing the communication operations themselves to understand
/// network behavior and identify failure patterns.
///
/// ## Key Concepts
///
/// This API is built around two core dimensions:
/// - **Sign**: What happened (operation or outcome: CONNECT, TRANSMIT, SUCCEED, FAIL, etc.)
/// - **Dimension**: Communication direction (OUTBOUND = sending out, INBOUND = receiving in)
///
/// By combining sign and dimension into signals, the API enables detailed diagnostics and
/// monitoring of distributed communication patterns from both directional perspectives.
///
/// ## Usage Example
///
/// ```java
/// final var cortex = Substrates.cortex();
/// // Create a probe for an RPC call
/// var probe = circuit.conduit(Probes::composer).percept(cortex.name("rpc"));
///
/// // Outbound communication - "I am initiating this"
/// probe.connect(OUTBOUND);      // I am connecting outward
/// probe.transfer(OUTBOUND);     // I am sending data out
/// probe.process(OUTBOUND);      // I am processing outbound request
/// probe.succeed(OUTBOUND);      // My outbound operation succeeded
/// probe.disconnect(OUTBOUND);   // I am disconnecting
///
/// // Inbound communication - "I received this"
/// probe.connect(INBOUND);       // I received an inbound connection
/// probe.transfer(INBOUND);      // I am receiving data in
/// probe.process(INBOUND);       // I am processing inbound request
/// probe.succeed(INBOUND);       // Inbound operation succeeded
/// probe.disconnect(INBOUND);    // I received inbound disconnection
///
/// // Failure reporting
/// probe.fail(OUTBOUND);         // My outbound call failed
/// probe.fail(INBOUND);          // Inbound request failed
/// ```
///
/// ## Relationship to Other APIs
///
/// `Probes` provides foundational observation data for higher-level APIs:
///
/// - **Statuses API**: Aggregates probe observations to assess operational conditions
///   - Many FAIL signals may indicate DEGRADED or DEFECTIVE conditions
///   - Connection failures suggest DIVERGING or DOWN states
/// - **Services API**: Probe observations inform service lifecycle
///   - DISCONNECT maps to service DISCONNECT
///   - FAIL maps to service FAIL
/// - **Situations API**: Signal patterns inform situational assessments
///   - Sustained failures elevate from NORMAL to WARNING or CRITICAL situations
///
/// ## Dual-Dimension Model
///
/// The dual-dimension model enables observing communication from both flow directions:
///
/// | Dimension | Direction | Example                     |
/// |-----------|-----------|----------------------------|
/// | OUTBOUND  | Sending   | "I am connecting outward"  |
/// | INBOUND   | Receiving | "I received connection in" |
///
/// **Example**: `probe.connect(OUTBOUND)` emits (Sign.CONNECT, OUTBOUND) indicating
/// an outbound connection attempt, while `probe.connect(INBOUND)` emits (Sign.CONNECT, INBOUND)
/// indicating an inbound connection was received.
///
/// ## Performance Considerations
///
/// Probe emissions are designed for high-frequency operation at request granularity.
/// Typical rates: 100-100K signals/sec per probe. Signals flow asynchronously
/// through the circuit's event queue, adding minimal overhead (<50ns) to instrumented
/// operations. For extremely high-frequency operations (>1M ops/sec), consider sampling
/// or aggregating signals before emission.
///
/// @author William David Louth
/// @since 1.0

public final class Probes
  implements Serventis {

  private Probes () { }

  /// A static composer function for creating Probe instruments.
  ///
  /// This method can be used as a method reference with conduits as follows:
  ///
  /// Example usage:
  /// ```java
  /// final var cortex = Substrates.cortex();
  /// var probe = circuit.conduit(Probes::composer).percept(cortex.name("rpc.client"));
  /// ```
  ///
  /// @param channel the channel from which to create the probe
  /// @return a new Probe instrument for the specified channel
  /// @throws NullPointerException if the channel param is `null`

  @New
  @NotNull
  public static Probe composer (
    @NotNull final Channel < ? super Signal > channel
  ) {

    return
      new Probe (
        channel.pipe ()
      );

  }

  /// The [Sign] enum represents the type of communication event.
  ///
  /// Signs include both operations (CONNECT, DISCONNECT, TRANSFER, PROCESS) and outcomes (SUCCEED, FAIL).
  /// The dimension (OUTBOUND/INBOUND) specifies the direction of the operation.

  public enum Sign
    implements Serventis.Sign {

    /// Indicates connection establishment
    CONNECT,

    /// Indicates connection closure
    DISCONNECT,

    /// Indicates data transfer (sending or receiving, direction specified by dimension)
    TRANSFER,

    /// Indicates data processing
    PROCESS,

    /// Indicates successful completion
    SUCCEED,

    /// Indicates failed completion
    FAIL

  }

  /// The [Dimension] enum represents the direction of communication flow.
  ///
  /// Every sign has two dimensions representing outbound and inbound communication directions.

  public enum Dimension
    implements Category {

    /// Outbound communication (initiated by self, sending outward)
    OUTBOUND,

    /// Inbound communication (received from other, coming inward)
    INBOUND

  }

  /// A [Probe] is an instrument that emits signals about communication operations.
  /// It serves as the primary reporting mechanism within the Probes API.
  ///
  /// Probes can be attached to various components within a distributed system to monitor
  /// and report on communication operations and outcomes.
  ///
  /// ## Usage
  ///
  /// Use semantic methods for all communication events:
  /// ```java
  /// probe.connect(OUTBOUND);      // Outbound connection attempt
  /// probe.connect(INBOUND);       // Inbound connection received
  /// probe.transfer(OUTBOUND);     // Sending data out
  /// probe.transfer(INBOUND);      // Receiving data in
  /// probe.succeed(OUTBOUND);      // Outbound operation succeeded
  /// probe.fail(INBOUND);          // Inbound operation failed
  /// ```
  ///
  /// Probes provide simple, direct methods that make communication monitoring expressive.

  @Queued
  @Provided
  public static final class Probe
    implements Signaler < Sign, Dimension > {

    private static final SignalSet < Sign, Dimension, Signal > SIGNALS =
      new SignalSet <> (
        Sign.class,
        Dimension.class,
        Signal::new
      );

    private final Pipe < ? super Signal > pipe;

    private Probe (
      final Pipe < ? super Signal > pipe
    ) {

      this.pipe = pipe;

    }

    /// Emits a connect sign from this probe.
    ///
    /// @param dimension the direction (OUTBOUND or INBOUND)

    public void connect (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.CONNECT,
          dimension
        )
      );

    }

    /// Emits a disconnect sign from this probe.
    ///
    /// @param dimension the direction (OUTBOUND or INBOUND)

    public void disconnect (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.DISCONNECT,
          dimension
        )
      );

    }

    /// Emits a fail sign from this probe.
    ///
    /// @param dimension the direction (OUTBOUND or INBOUND)

    public void fail (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.FAIL,
          dimension
        )
      );

    }

    /// Emits a process sign from this probe.
    ///
    /// @param dimension the direction (OUTBOUND or INBOUND)

    public void process (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.PROCESS,
          dimension
        )
      );

    }

    /// Signals a communication event by composing sign and dimension.
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

    /// Emits a succeed sign from this probe.
    ///
    /// @param dimension the direction (OUTBOUND or INBOUND)

    public void succeed (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.SUCCEED,
          dimension
        )
      );

    }

    /// Emits a transfer sign from this probe.
    ///
    /// @param dimension the direction (OUTBOUND or INBOUND)

    public void transfer (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.TRANSFER,
          dimension
        )
      );

    }

  }

  /// The [Signal] record represents a communication signal composed of a sign and dimension.
  ///
  /// Signals are the composition of Sign (what happened) and Dimension (from whose perspective),
  /// enabling observation of communication operations from both self and observed perspectives.
  ///
  /// @param sign      the communication event classification
  /// @param dimension the perspective from which the signal is emitted

  @Queued
  @Provided
  public record Signal(
    Sign sign,
    Dimension dimension
  ) implements Serventis.Signal < Sign, Dimension > { }

}
