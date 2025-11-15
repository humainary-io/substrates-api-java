// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.api;

import io.humainary.substrates.api.Substrates;

/// # Serventis API
///
/// The `Serventis` API provides common abstractions for all signal-based observability
/// interfaces in the Serventis framework. It defines the structural pattern that all
/// signal types follow: a composition of Sign × Dimension.
///
/// ## Purpose
///
/// This API establishes a uniform protocol for signal-based communication across all
/// Serventis observability instruments. By providing common interfaces, it enables:
///
/// - Polymorphic handling of signals from different APIs
/// - Generic utilities that work across all signal types
/// - Consistent structural patterns across the framework
/// - Type-safe composition of signs and dimensions
///
/// ## Core Abstractions
///
/// - **Signal**: A composition of Sign and Dimension representing an observable event
/// - **Sign**: The primary semantic classification of what is being observed
/// - **Dimension**: The secondary qualifier providing perspective, confidence, or directionality
///
/// ## Design Pattern
///
/// All Serventis APIs that emit signals follow this structural pattern:
///
/// ```
/// Signal = Sign × Dimension
/// ```
///
/// Where each API defines domain-specific enums for Sign and Dimension that implement
/// the common interfaces defined here.
///
/// ## Example Implementations
///
/// - **Probes**: Sign (CONNECT, TRANSMIT, etc.) × Dimension (RELEASE, RECEIPT)
/// - **Services**: Sign (START, CALL, SUCCESS, etc.) × Dimension (RELEASE, RECEIPT)
/// - **Agents**: Sign (OFFER, PROMISE, FULFILL, etc.) × Dimension (PROMISER, PROMISEE)
/// - **Monitors**: Sign (STABLE, DEGRADED, etc.) × Dimension (TENTATIVE, MEASURED, CONFIRMED)
///
/// ## Benefits
///
/// 1. **Architectural Consistency**: All signal-based APIs share the same structure
/// 2. **Code Reuse**: Generic utilities can process any signal type
/// 3. **Type Safety**: Each API maintains its own strongly-typed enums
/// 4. **Extensibility**: New signal-based APIs can easily adopt this pattern
/// 5. **Clarity**: The structural pattern is explicit and self-documenting
///
/// @author William David Louth
/// @since 1.0

public interface Serventis
  extends Substrates {

  /// The [Dimension] interface represents the secondary qualifier for an observable event.
  ///
  /// Dimension enums in each Serventis API implement this interface to provide domain-specific
  /// qualifiers while maintaining a common protocol. Dimensions add context to signs:
  ///
  /// - **Perspective**: RELEASE vs RECEIPT (self vs observed perspective)
  /// - **Promise Perspective**: PROMISER vs PROMISEE (agent role in coordination)
  /// - **Confidence**: TENTATIVE vs MEASURED vs CONFIRMED (statistical certainty)
  ///
  /// This interface leverages the methods already provided by Java enums (name() and ordinal())
  /// to enable generic handling of dimensions across different APIs without requiring generics.

  interface Dimension {

    /// Returns the name of this dimension.
    ///
    /// @return the name of this enum constant, exactly as declared

    String name ();


    /// Returns the ordinal of this dimension.
    ///
    /// @return the ordinal of this enum constant (its position in the enum declaration)

    int ordinal ();

  }

  /// The [Sign] interface represents the primary semantic classification of an observable event.
  ///
  /// Sign enums in each Serventis API implement this interface to provide domain-specific
  /// classifications (e.g., CONNECT, START, OFFER, STABLE) while maintaining a common protocol.
  ///
  /// This interface leverages the methods already provided by Java enums (name() and ordinal())
  /// to enable generic handling of signs across different APIs without requiring generics.

  interface Sign {

    /// Returns the name of this sign.
    ///
    /// @return the name of this enum constant, exactly as declared

    String name ();


    /// Returns the ordinal of this sign.
    ///
    /// @return the ordinal of this enum constant (its position in the enum declaration)

    int ordinal ();

  }

  /// The [Signal] interface represents an observable event composed of a sign and dimension.
  ///
  /// This interface provides the common protocol for all signal types across Serventis APIs.
  /// Each signal combines a semantic sign (what is being observed) with a qualifying dimension
  /// (perspective, confidence, directionality, or other contextual qualifier).
  ///
  /// Implementations are typically enums or records that provide domain-specific sign and
  /// dimension values while maintaining this common structural pattern.
  ///
  /// @param <S> the Sign type implementing Serventis.Sign
  /// @param <D> the Dimension type implementing Serventis.Dimension

  interface Signal < S extends Sign, D extends Dimension > {

    /// Returns the dimension component of this signal.
    ///
    /// The dimension provides the secondary qualifier that gives context to the sign,
    /// such as perspective (self vs observed), confidence level, or directionality.
    ///
    /// @return the dimension of this signal

    @NotNull
    D dimension ();


    /// Returns the sign component of this signal.
    ///
    /// The sign represents the primary semantic classification of the observable event.
    ///
    /// @return the sign of this signal

    @NotNull
    S sign ();

  }

  /// Marker interface for percepts that signal two-dimensional events.
  ///
  /// Signalers make signals composed of Sign × Dimension, combining semantic signs
  /// with qualifying dimensions. The dimension adds essential context such as perspective
  /// (self vs observed), confidence level (tentative vs confirmed), or role (promiser vs promisee).
  ///
  /// Examples of Signalers include:
  /// - **Probes**: (CONNECT, TRANSMIT, etc.) × (RELEASE, RECEIPT)
  /// - **Services**: (START, CALL, SUCCESS, etc.) × (RELEASE, RECEIPT)
  /// - **Monitors**: (STABLE, DEGRADED, etc.) × (TENTATIVE, MEASURED, CONFIRMED)
  /// - **Agents**: (OFFER, PROMISE, FULFILL, etc.) × (PROMISER, PROMISEE)
  ///
  /// @param <S> the Sign enum type implementing Serventis.Sign
  /// @param <D> the Dimension enum type implementing Serventis.Dimension

  interface Signaler <
    S extends Enum < S > & Sign,
    D extends Enum < D > & Dimension
    >
    extends Percept {

    /// Signals a two-dimensional event by composing sign and dimension.
    ///
    /// This method makes a signal where the sign provides the primary
    /// semantic classification and the dimension provides qualifying context.
    ///
    /// @param sign      the sign component
    /// @param dimension the dimension component

    void signal (
      @NotNull S sign,
      @NotNull D dimension
    );

  }

  /// Marker interface for percepts that sign single-dimensional events.
  ///
  /// Signers make signs without additional qualifiers such as perspective,
  /// confidence, or directionality. The sign itself carries the complete semantic meaning.
  ///
  /// Examples of Signers include:
  /// - **Counters**: INCREMENT, DECREMENT, OVERFLOW, RESET
  /// - **Gauges**: INCREMENT, DECREMENT, OVERFLOW, UNDERFLOW
  /// - **Resources**: GRANT, DENY, EXHAUST, RESTORE
  /// - **Queues**: ENQUEUE, DEQUEUE, OVERFLOW, UNDERFLOW
  ///
  /// @param <S> the Sign enum type implementing Serventis.Sign

  interface Signer < S extends Enum < S > & Sign >
    extends Percept {

    /// Signs a single-dimensional event.
    ///
    /// This method makes a sign representing an observable occurrence
    /// without additional qualifying dimensions.
    ///
    /// @param sign the sign to make

    void sign (
      @NotNull S sign
    );

  }

}
