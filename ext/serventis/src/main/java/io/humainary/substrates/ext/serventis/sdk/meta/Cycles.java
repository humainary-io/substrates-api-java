// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.sdk.meta;

import io.humainary.substrates.ext.serventis.api.Serventis;
import io.humainary.substrates.ext.serventis.sdk.SignalSet;

/// # Cycles API
///
/// The `Cycles` API provides a structured framework for expressing observations about
/// the repetition patterns of signs within a signal stream. It enables systems to emit
/// semantic signals that describe whether a sign is appearing for the first time,
/// repeating consecutively, or returning after an absence.
///
/// ## Purpose
///
/// This API provides vocabulary for **meta-level observation** of sign streams.
/// Unlike domain APIs that emit raw operations/outcomes, Cycles expresses analytical
/// observations about patterns in those streams. Something else (subscriber, analyzer,
/// Flow operator) performs the actual pattern detection; this API provides the
/// vocabulary to express what was detected.
///
/// ## Important: Expression vs Analysis
///
/// This API is for **expressing cycle observations**, not implementing cycle detection.
/// When your analyzer detects that a sign is repeating consecutively or returning after
/// other signs, use this API to emit that observation. The API is the vocabulary for
/// expression, not the intelligence that performs the analysis.
///
/// ## Key Concepts
///
/// - **Cycle**: An instrument that emits cycle observations for signs from a source API
/// - **Signal**: A pairing of any sign type with a cycle dimension
/// - **Dimension**: The repetition characteristic (SINGLE, REPEAT, RETURN)
///
/// ## Generic Over Sign Types
///
/// Unlike domain APIs that define their own signs, Cycles is **generic over any Sign type**.
/// The Sign comes from the source API being observed (Resources, Tasks, Gauges, etc.).
/// Cycles defines only the Dimension, which describes the repetition pattern.
///
/// ## Dimensions
///
/// | Dimension | Meaning                                      |
/// |-----------|----------------------------------------------|
/// | `SINGLE`  | First occurrence of this sign in the stream  |
/// | `REPEAT`  | Same sign as immediately previous emission   |
/// | `RETURN`  | Seen before, but not immediately previous    |
///
/// ## Usage Example
///
/// ```java
/// // Create a cycle observer for Resources.Sign
/// var cycle = circuit
///     .conduit(Cycles.composer(Resources.Sign.class))
///     .percept(cortex.name("db.pool.cycles"));
///
/// // Stream: GRANT, GRANT, DENY, GRANT
/// cycle.signal(GRANT, SINGLE);    // First GRANT
/// cycle.signal(GRANT, REPEAT);    // GRANT immediately after GRANT
/// cycle.signal(DENY, SINGLE);     // First DENY
/// cycle.signal(GRANT, RETURN);    // GRANT returns after DENY
/// ```
///
/// ## Relationship to Other APIs
///
/// Cycles is a **meta-level** API in the `sdk/meta` package:
///
/// - **Domain APIs** (opt/*): Emit raw signs (GRANT, DENY, INCREMENT, etc.)
/// - **Meta APIs** (sdk/meta): Express observations about sign patterns
/// - **Universal APIs** (sdk): Translation targets (Statuses, Situations, Systems)
///
/// The semiotic flow:
/// ```
/// Domain signs → Meta observations (Cycles) → Universal vocabularies → Actions
/// ```
///
/// ## Performance Considerations
///
/// Cycle signals are pre-allocated using SignalSet for zero-allocation emission.
/// The SignalSet is constructed with the source Sign class, enabling pre-computation
/// of all Sign × Dimension combinations at instrument creation time.
///
/// @author William David Louth
/// @since 1.0

public final class Cycles
  implements Serventis {

  private Cycles () { }

  /// Returns a composer that creates Cycle instruments for the specified Sign type.
  ///
  /// This method returns a composer function suitable for use with Circuit.conduit().
  /// The Sign class is required to enable SignalSet pre-allocation of all
  /// Sign × Dimension combinations.
  ///
  /// Example usage:
  /// ```java
  /// var cycle = circuit
  ///     .conduit(Cycles.composer(Resources.Sign.class))
  ///     .percept(cortex.name("resource.cycles"));
  /// ```
  ///
  /// @param <S>       the Sign enum type from the source API
  /// @param signClass the class object for the sign enum
  /// @return a composer that creates Cycle instruments
  /// @throws NullPointerException if signClass is `null`

  @NotNull
  public static < S extends Enum < S > & Sign > Composer < Signal < S >, Cycle < S > > composer (
    @NotNull final Class < S > signClass
  ) {

    return
      channel ->
        new Cycle <> (
          signClass,
          channel.pipe ()
        );

  }


  /// The [Dimension] enum represents the repetition characteristic of a sign
  /// within a signal stream.
  ///
  /// Dimensions describe the temporal relationship of a sign occurrence to
  /// previous occurrences of the same sign in the stream.

  public enum Dimension
    implements Serventis.Category {

    /// First occurrence of this sign in the stream.
    ///
    /// SINGLE indicates this sign has not been seen before in the observation
    /// window. It represents a novel appearance of this particular sign value.

    SINGLE,

    /// Same sign as immediately previous emission.
    ///
    /// REPEAT indicates consecutive occurrence - this sign was also the most
    /// recent emission. Back-to-back repetition of the same sign.

    REPEAT,

    /// Seen before, but not immediately previous.
    ///
    /// RETURN indicates the sign has appeared previously in the stream, but
    /// other signs occurred between then and now. The sign has cycled back
    /// after an absence.

    RETURN

  }

  /// The [Cycle] class emits cycle observations for signs from a source API.
  ///
  /// A Cycle instrument is generic over the Sign type of the source API being
  /// observed. It provides vocabulary for expressing whether signs are appearing
  /// for the first time, repeating consecutively, or returning after absence.
  ///
  /// ## Usage
  ///
  /// ```java
  /// cycle.signal(Resources.Sign.GRANT, Dimension.SINGLE);
  /// cycle.signal(Resources.Sign.GRANT, Dimension.REPEAT);
  /// cycle.signal(Resources.Sign.DENY, Dimension.SINGLE);
  /// cycle.signal(Resources.Sign.GRANT, Dimension.RETURN);
  /// ```

  @Queued
  @Provided
  public static final class Cycle < S extends Enum < S > & Sign >
    implements Signaler < S, Dimension > {

    private final SignalSet < S, Dimension, Signal < S > > signals;
    private final Pipe < ? super Signal < S > >            pipe;

    private Cycle (
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

    /// Emits a cycle observation for the specified sign and dimension.
    ///
    /// @param sign      the sign from the source API
    /// @param dimension the repetition characteristic
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

  /// The [Signal] record represents a cycle observation for any sign type.
  ///
  /// Unlike domain API signals that pair domain-specific signs with dimensions,
  /// Cycle signals are generic over the sign type. The sign comes from whatever
  /// source API is being observed; the dimension comes from this API.
  ///
  /// @param <S>       the Sign type from the source API
  /// @param sign      the observed sign from the source API
  /// @param dimension the repetition characteristic

  @Queued
  @Provided
  public record Signal < S extends Sign >(
    @NotNull S sign,
    @NotNull Dimension dimension
  ) implements Serventis.Signal < S, Dimension > { }

}
