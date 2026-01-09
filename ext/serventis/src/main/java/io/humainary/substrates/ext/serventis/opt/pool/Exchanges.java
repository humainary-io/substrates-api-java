// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.opt.pool;

import io.humainary.substrates.ext.serventis.api.Serventis;
import io.humainary.substrates.ext.serventis.sdk.SignalSet;

/// # Exchanges API
///
/// The `Exchanges` API provides a structured framework for observing resource exchanges
/// between parties. It enables emission of semantic signals that describe bilateral
/// transfers of resources, supporting both economic exchange patterns (REA model) and
/// synchronization rendezvous patterns (Java Exchanger).
///
/// ## Purpose
///
/// This API enables systems to **observe** exchange interactions where resources move
/// between a provider and receiver. The dual-perspective model captures both sides of
/// the exchange, enabling complete observability of bilateral transfers.
///
/// ## Important: Reporting vs Implementation
///
/// This API is for **reporting exchange semantics**, not implementing exchange mechanisms.
/// When your system performs exchanges (trades, swaps, transfers between parties), use
/// this API to emit observability signals. Observer agents can then reason about exchange
/// patterns, completion rates, and transfer dynamics.
///
/// ## Theoretical Foundation
///
/// The API is grounded in the **REA (Resource-Event-Agent)** accounting model:
///
/// - **Resource**: Economic things of value being exchanged
/// - **Event**: The exchange event (CONTRACT, TRANSFER)
/// - **Agent**: Parties participating (PROVIDER, RECEIVER)
///
/// The fundamental insight is that economic activity consists of **dual exchanges**:
/// every give implies a take (conservation), creating reciprocal flows between agents.
///
/// ## Key Concepts
///
/// - **Exchange**: A named bilateral transfer of resources between parties
/// - **Sign**: The phase of exchange (CONTRACT, TRANSFER)
/// - **Dimension**: The perspective (PROVIDER giving, RECEIVER taking)
///
/// ## Exchange Patterns
///
/// ### REA Economic Exchange
///
/// ```
/// CONTRACT × PROVIDER  →  CONTRACT × RECEIVER    (commit to exchange)
/// TRANSFER × PROVIDER  →  TRANSFER × RECEIVER    (fulfill exchange)
/// ```
///
/// ### Java Exchanger Rendezvous
///
/// Both threads arrive at the exchange point and swap data:
///
/// ```
/// Thread 1: CONTRACT × PROVIDER  (arrive with data)
/// Thread 2: CONTRACT × PROVIDER  (arrive with data)
/// Thread 1: TRANSFER × PROVIDER, TRANSFER × RECEIVER  (swap)
/// Thread 2: TRANSFER × PROVIDER, TRANSFER × RECEIVER  (swap)
/// ```
///
/// ## Signal Matrix
///
/// | Sign       | PROVIDER              | RECEIVER                |
/// |------------|-----------------------|-------------------------|
/// | CONTRACT   | I contract to provide | I contract to receive   |
/// | TRANSFER   | I transfer out        | I receive transfer      |
///
/// ## Relationship to Other APIs
///
/// - **Resources API**: Exchanges transfer resources; Resources tracks acquisition/release
/// - **Agents API**: Agents make promises; Exchanges fulfill transfers
/// - **Leases API**: Leases grant time-bounded access; Exchanges transfer ownership
/// - **Statuses API**: Exchange failure patterns may indicate DEGRADED conditions
///
/// ## Performance Considerations
///
/// Exchange signal emissions are designed for high-frequency operation.
/// Zero-allocation via SignalSet caching with ~10-20ns cost for non-transit emits.
/// Signals flow asynchronously through the circuit's event queue.
///
/// @author William David Louth
/// @since 1.0

public final class Exchanges
  implements Serventis {

  private Exchanges () { }

  /// A static composer function for creating Exchange instruments.
  ///
  /// This method can be used as a method reference with conduits as follows:
  ///
  /// Example usage:
  /// ```java
  /// final var cortex = Substrates.cortex();
  /// var exchange = circuit.conduit(Exchanges::composer).percept(cortex.name("trade.orders"));
  /// ```
  ///
  /// @param channel the channel from which to create the exchange
  /// @return a new Exchange instrument for the specified channel
  /// @throws NullPointerException if the channel param is `null`

  @New
  @NotNull
  public static Exchange composer (
    @NotNull final Channel < ? super Signal > channel
  ) {

    return
      new Exchange (
        channel.pipe ()
      );

  }


  /// The [Dimension] enum represents the perspective in an exchange.
  ///
  /// Every exchange has two sides: the provider (giving) and the receiver (taking).
  /// This dual-perspective model enables complete observability of bilateral transfers.

  public enum Dimension
    implements Category {

    /// The giving perspective in an exchange.
    ///
    /// PROVIDER signals indicate actions from the party transferring resources out.
    /// Use PROVIDER when reporting "I am providing/giving/transferring out."

    PROVIDER,

    /// The receiving perspective in an exchange.
    ///
    /// RECEIVER signals indicate actions from the party receiving resources in.
    /// Use RECEIVER when reporting "I am receiving/taking/accepting transfer."

    RECEIVER

  }


  /// The [Sign] enum represents the phases of an exchange.
  ///
  /// Exchanges proceed through phases: parties first CONTRACT (commit to exchange),
  /// then TRANSFER (resources change hands). This minimal vocabulary captures the
  /// essential exchange lifecycle.

  public enum Sign
    implements Serventis.Sign {

    /// Commit to participate in an exchange.
    ///
    /// CONTRACT signals indicate a party has committed to the exchange.
    /// For REA: agreeing to terms. For Exchanger: arriving at rendezvous.
    ///
    /// - CONTRACT × PROVIDER: "I contract to provide"
    /// - CONTRACT × RECEIVER: "I contract to receive"

    CONTRACT,

    /// Resource changes hands.
    ///
    /// TRANSFER signals indicate resources are moving between parties.
    /// For REA: fulfilling the exchange. For Exchanger: the swap occurs.
    ///
    /// - TRANSFER × PROVIDER: "I transfer out"
    /// - TRANSFER × RECEIVER: "I receive transfer"

    TRANSFER

  }

  /// The [Exchange] class emits exchange observations.
  ///
  /// An Exchange instrument provides methods for signaling exchange phases
  /// from both provider and receiver perspectives.
  ///
  /// ## Usage
  ///
  /// ```java
  /// // Provider commits to exchange
  /// exchange.contract(PROVIDER);
  ///
  /// // Receiver commits to exchange
  /// exchange.contract(RECEIVER);
  ///
  /// // Provider transfers out
  /// exchange.transfer(PROVIDER);
  ///
  /// // Receiver receives transfer
  /// exchange.transfer(RECEIVER);
  /// ```

  @Queued
  @Provided
  public static final class Exchange
    implements Signaler < Sign, Dimension > {

    private static final SignalSet < Sign, Dimension, Signal > SIGNALS =
      new SignalSet <> (
        Sign.class,
        Dimension.class,
        Signal::new
      );

    private final Pipe < ? super Signal > pipe;

    private Exchange (
      final Pipe < ? super Signal > pipe
    ) {

      this.pipe =
        pipe;

    }

    /// Signals a CONTRACT phase from the specified perspective.
    ///
    /// @param dimension the party perspective (PROVIDER or RECEIVER)

    public void contract (
      @NotNull final Dimension dimension
    ) {

      signal (
        Sign.CONTRACT,
        dimension
      );

    }

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

    /// Signals a TRANSFER phase from the specified perspective.
    ///
    /// @param dimension the party perspective (PROVIDER or RECEIVER)

    public void transfer (
      @NotNull final Dimension dimension
    ) {

      signal (
        Sign.TRANSFER,
        dimension
      );

    }

  }

  /// The [Signal] record represents an exchange observation.
  ///
  /// Each signal combines a sign (CONTRACT, TRANSFER) with a dimension
  /// (PROVIDER, RECEIVER) to capture the complete exchange event.
  ///
  /// @param sign      the exchange phase
  /// @param dimension the party perspective

  @Queued
  @Provided
  public record Signal(
    @NotNull Sign sign,
    @NotNull Dimension dimension
  ) implements Serventis.Signal < Sign, Dimension > { }

}
