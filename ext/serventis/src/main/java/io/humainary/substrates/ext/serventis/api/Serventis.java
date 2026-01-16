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
/// - **Statuses**: Sign (STABLE, DEGRADED, etc.) × Dimension (TENTATIVE, MEASURED, CONFIRMED)
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

  /// The [Category] interface represents discrete, unordered dimensional classifications.
  ///
  /// Category dimensions classify signals into distinct kinds or aspects without implying
  /// any ordering, ranking, or progression between values. Each category value represents
  /// a qualitatively different classification.
  ///
  /// ## Examples
  ///
  /// - **Systems.Dimension**: SPACE, FLOW, LINK, TIME (what kind of constraint?)
  /// - **Services.Dimension**: RELEASE, RECEIPT (whose perspective?)
  /// - **Transactions.Dimension**: COORDINATOR, PARTICIPANT (what role?)
  /// - **Cycles.Dimension**: SINGLE, REPEAT, RETURN (what recurrence pattern?)
  ///
  /// ## Contrast with Spectrum
  ///
  /// Unlike [Spectrum] dimensions where values form an ordered progression (e.g., low→medium→high),
  /// Category values are peers with no inherent ordering. SPACE is not "more than" FLOW;
  /// RELEASE is not "greater than" RECEIPT.

  non-sealed interface Category
    extends Dimension { }

  /// The [Dimension] interface represents the secondary qualifier for an observable event.
  ///
  /// Dimension enums in each Serventis API implement this interface to provide domain-specific
  /// qualifiers while maintaining a common protocol. Dimensions add context to signs:
  ///
  /// - **Perspective**: RELEASE vs RECEIPT (self vs observed perspective)
  /// - **Promise Perspective**: PROMISER vs PROMISEE (agent role in coordination)
  /// - **Confidence**: TENTATIVE vs MEASURED vs CONFIRMED (statistical certainty)
  ///
  /// ## Dimension Subtypes
  ///
  /// Dimensions fall into two categories:
  ///
  /// - **[Category]**: Discrete, unordered classifications (e.g., SPACE/FLOW/LINK/TIME,
  ///   RELEASE/RECEIPT, SINGLE/REPEAT/RETURN). Values represent different kinds or aspects
  ///   without implying any ordering or progression.
  ///
  /// - **[Spectrum]**: Ordered, ranked progressions (e.g., TENTATIVE→MEASURED→CONFIRMED,
  ///   CONSTANT→VARIABLE→VOLATILE). Values represent points along a scale where relative
  ///   position is meaningful.

  sealed interface Dimension
    extends Symbol
    permits Category, Spectrum { }

  /// The [Sign] interface represents the primary semantic classification of an observable event.
  ///
  /// Sign enums in each Serventis API implement this interface to provide domain-specific
  /// classifications (e.g., CONNECT, START, OFFER, STABLE) while maintaining a common protocol.

  non-sealed interface Sign
    extends Symbol { }

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
  /// - **Statuses**: (STABLE, DEGRADED, etc.) × (TENTATIVE, MEASURED, CONFIRMED)
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

    @Queued
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

    @Queued
    void sign (
      @NotNull S sign
    );

  }

  /// The [Spectrum] interface represents ordered, ranked dimensional progressions.
  ///
  /// Spectrum dimensions classify signals along a scale where the relative position
  /// of values is meaningful. Values form a progression from one end to another,
  /// enabling comparison and directional reasoning.
  ///
  /// ## Examples
  ///
  /// - **Statuses.Dimension**: TENTATIVE → MEASURED → CONFIRMED (increasing confidence)
  /// - **Situations.Dimension**: CONSTANT → VARIABLE → VOLATILE (increasing variability)
  ///
  /// ## Contrast with Category
  ///
  /// Unlike [Category] dimensions where values are discrete peers, Spectrum values
  /// have meaningful ordering. CONFIRMED represents more confidence than TENTATIVE;
  /// VOLATILE represents more variability than CONSTANT.
  ///
  /// ## Ordering Convention
  ///
  /// Spectrum enums should be declared in ascending order, where the first value
  /// represents the "low" end and the last value represents the "high" end.
  /// The enum ordinal() reflects this ordering.

  non-sealed interface Spectrum
    extends Dimension { }

  /// Base interface for enum-based classification markers in the Serventis semiotic framework.
  ///
  /// Both [Sign] and [Dimension] extend this interface to provide the fundamental enum
  /// protocol (name and ordinal) that enables generic handling across different classification
  /// types without requiring explicit generics.
  ///
  /// In semiotic terms, a symbol is a sign whose relationship to its referent is established
  /// by convention rather than resemblance or causation. The enum constants that implement
  /// this interface are symbols in exactly this sense: their meaning derives from their
  /// defined role within the Serventis vocabulary, not from any intrinsic property.
  ///
  /// This interface leverages the methods already provided by Java enums to enable
  /// polymorphic handling of classification markers across different Serventis APIs.

  sealed interface Symbol
    permits Dimension,
            Sign {

    /// Returns the name of this symbol.
    ///
    /// @return the name of this enum constant, exactly as declared

    String name ();


    /// Returns the ordinal of this symbol.
    ///
    /// @return the ordinal of this enum constant (its position in the enum declaration)

    int ordinal ();

  }

}
