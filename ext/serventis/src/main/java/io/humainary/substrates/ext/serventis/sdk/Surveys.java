// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.sdk;

import io.humainary.substrates.ext.serventis.api.Serventis;
import io.humainary.substrates.ext.serventis.sdk.meta.Cycles;

/// # Surveys API
///
/// The `Surveys` API provides a structured framework for expressing collective assessment
/// outcomes when multiple observers or cluster members evaluate a subject's status.
/// It captures the degree of agreement among observers, complementing the individual
/// confidence expressed in [Statuses].
///
/// ## Purpose
///
/// This API provides the **collective agreement vocabulary** in the semiotic ascent architecture.
/// While [Statuses] expresses individual assessment with confidence (how sure am I?),
/// Surveys expresses collective assessment with agreement (how much do we agree?).
///
/// ## Important: Collective Assessment Layer
///
/// This API is for **reporting collective assessment outcomes**, not implementing voting systems.
/// When your consensus mechanism, cluster coordinator, or polling system aggregates assessments
/// from multiple observers and determines the collective judgment, use this API to emit
/// signals about that collective outcome. Observer agents can then reason about collective
/// health patterns and agreement dynamics without coupling to your consensus implementation.
///
/// **Example**: Your cluster has 5 nodes monitoring a shared resource. After polling all nodes,
/// 4 report DEGRADED and 1 reports STABLE. Call `survey.signal(DEGRADED, MAJORITY)` to express
/// that the collective assessment is DEGRADED by majority agreement.
///
/// ## Key Concepts
///
/// - **Survey**: An instrument that emits collective assessment signals for a named subject
/// - **Signal**: A pairing of any sign type with an agreement dimension
/// - **Dimension**: The degree of collective agreement (DIVIDED, MAJORITY, UNANIMOUS)
///
/// ## Generic Over Sign Types
///
/// Like [Cycles], Surveys is **generic over any Sign type**. The Sign comes from the
/// source API being surveyed (typically [Statuses.Sign], but could be any domain sign).
/// Surveys defines only the Dimension, which describes the agreement level.
///
/// ## Dimensions
///
/// | Dimension  | Meaning                           | Observable From                    |
/// |------------|-----------------------------------|------------------------------------|
/// | `DIVIDED`  | No clear majority                 | Highest vote share ≤ threshold     |
/// | `MAJORITY` | Clear majority but not unanimous  | Highest vote share > threshold, < 100% |
/// | `UNANIMOUS`| Complete agreement                | One sign has 100%                  |
///
/// ## Usage Example
///
/// ```java
/// // Create a survey instrument for Status signs
/// var survey = circuit
///     .conduit(Surveys.composer(Statuses.Sign.class))
///     .percept(cortex.name("cluster.health"));
///
/// // After polling/aggregating observer status assessments:
/// survey.signal(DEGRADED, MAJORITY);   // Most observers say DEGRADED
/// survey.signal(STABLE, UNANIMOUS);    // All observers agree STABLE
/// survey.signal(DIVERGING, DIVIDED);   // Observers split on DIVERGING
/// ```
///
/// ## Relationship to Other APIs
///
/// Surveys complements Statuses in the observability hierarchy:
///
/// ```
/// Individual: Status (what I assess) × Confidence (how sure I am)
/// Collective: Survey (what we assess) × Agreement (how much we agree)
/// ```
///
/// - **Statuses API**: Individual assessment with confidence dimension
/// - **Surveys API**: Collective assessment with agreement dimension
/// - **Situations API**: Consumes both to produce situational assessments
///
/// ## Semiotic Relationship
///
/// The parallel structure enables rich observability:
///
/// | Layer      | Sign Source    | Dimension        | Question Answered          |
/// |------------|----------------|------------------|----------------------------|
/// | Status     | Status signs   | Confidence       | How sure am I?             |
/// | Survey     | Any signs      | Agreement        | How much do we agree?      |
///
/// A subject might have:
/// - Individual status: DEGRADED × MEASURED (I'm fairly sure it's degraded)
/// - Collective survey: DEGRADED × MAJORITY (most of us agree it's degraded)
///
/// ## Performance Considerations
///
/// Survey signals are pre-allocated using SignalSet for zero-allocation emission.
/// The SignalSet is constructed with the source Sign class, enabling pre-computation
/// of all Sign × Dimension combinations at instrument creation time.
///
/// @author William David Louth
/// @since 1.0

public final class Surveys
  implements Serventis {

  private Surveys () { }

  /// Returns a composer that creates Survey instruments for the specified Sign type.
  ///
  /// This method returns a composer function suitable for use with Circuit.conduit().
  /// The Sign class is required to enable SignalSet pre-allocation of all
  /// Sign × Dimension combinations.
  ///
  /// Example usage:
  /// ```java
  /// var survey = circuit
  ///     .conduit(Surveys.composer(Statuses.Sign.class))
  ///     .percept(cortex.name("cluster.health"));
  /// ```
  ///
  /// @param <S>       the Sign enum type from the source API
  /// @param signClass the class object for the sign enum
  /// @return a composer that creates Survey instruments
  /// @throws NullPointerException if signClass is `null`

  @NotNull
  public static < S extends Enum < S > & Sign > Composer < Signal < S >, Survey < S > > composer (
    @NotNull final Class < S > signClass
  ) {

    return
      channel ->
        new Survey <> (
          signClass,
          channel.pipe ()
        );

  }


  /// The [Dimension] enum represents the degree of collective agreement
  /// among observers assessing a subject.
  ///
  /// Dimensions describe the level of consensus reached when multiple
  /// observers or cluster members evaluate the same subject.

  public enum Dimension
    implements Spectrum {

    /// No clear majority among observers.
    ///
    /// DIVIDED indicates that observers are split with no single sign
    /// achieving majority agreement. The collective assessment is
    /// inconclusive, requiring either more data or resolution mechanisms.

    DIVIDED,

    /// Clear majority but not complete agreement.
    ///
    /// MAJORITY indicates that most observers agree on a particular sign,
    /// but some dissenting assessments exist. The collective judgment is
    /// reasonably confident but not unanimous.

    MAJORITY,

    /// Complete agreement among all observers.
    ///
    /// UNANIMOUS indicates that every observer agrees on the same sign.
    /// The collective assessment is definitive with no dissent.

    UNANIMOUS

  }


  /// The [Survey] class emits collective assessment signals for signs from a source API.
  ///
  /// A Survey instrument is generic over the Sign type of the source API being
  /// surveyed. It provides vocabulary for expressing the degree of agreement
  /// among multiple observers assessing the same subject.
  ///
  /// ## Usage
  ///
  /// ```java
  /// survey.signal(Statuses.Sign.DEGRADED, Dimension.MAJORITY);
  /// survey.signal(Statuses.Sign.STABLE, Dimension.UNANIMOUS);
  /// survey.signal(Statuses.Sign.DIVERGING, Dimension.DIVIDED);
  /// ```

  @Queued
  @Provided
  public static final class Survey < S extends Enum < S > & Sign >
    implements Signaler < S, Dimension > {

    private final SignalSet < S, Dimension, Signal < S > > signals;
    private final Pipe < ? super Signal < S > >            pipe;

    private Survey (
      final Class < S > signClass,
      final Pipe < ? super Signal < S > > pipe
    ) {

      this.signals =
        new SignalSet <> (
          signClass,
          Dimension.class,
          Signal::new
        );

      this.pipe =
        pipe;

    }

    /// Emits a collective assessment signal for the specified sign and agreement level.
    ///
    /// @param sign      the sign from the source API representing the collective judgment
    /// @param dimension the degree of agreement among observers
    /// @throws NullPointerException if sign or dimension is `null`

    @Override
    public void signal (
      @NotNull final S sign,
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        signals.get (
          sign,
          dimension
        )
      );

    }

  }


  /// The [Signal] record represents a collective assessment for any sign type.
  ///
  /// Unlike domain API signals that pair domain-specific signs with dimensions,
  /// Survey signals are generic over the sign type. The sign comes from whatever
  /// source API is being surveyed (typically Statuses); the dimension comes from
  /// this API representing the agreement level.
  ///
  /// @param <S>       the Sign type from the source API
  /// @param sign      the collective judgment (the sign most observers reported)
  /// @param dimension the degree of agreement among observers

  @Queued
  @Provided
  public record Signal < S extends Sign >(
    @NotNull S sign,
    @NotNull Dimension dimension
  ) implements Serventis.Signal < S, Dimension > { }

}
