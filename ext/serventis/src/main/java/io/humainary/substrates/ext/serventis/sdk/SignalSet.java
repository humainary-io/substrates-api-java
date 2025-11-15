// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.sdk;

import io.humainary.substrates.api.Substrates.NotNull;
import io.humainary.substrates.ext.serventis.api.Serventis.Dimension;
import io.humainary.substrates.ext.serventis.api.Serventis.Sign;
import io.humainary.substrates.ext.serventis.api.Serventis.Signal;

/// The [SignalSet] class provides a pre-allocated lookup table for all Signal combinations
/// in a Sign × Dimension Cartesian product.
///
/// This internal utility eliminates boilerplate initialization code by providing a generic,
/// reusable pattern for creating and accessing the complete signal set. All possible
/// signal combinations are constructed once during initialization, ensuring zero runtime
/// allocation and optimal lookup performance.
///
/// ## Usage Example
///
/// ```java
/// private static final SignalSet<Sign, Dimension, Signal> SIGNALS =
///   new SignalSet<>(Sign.class, Dimension.class, Signal::new);
///
/// public void connect() {
///   pipe.emit(SIGNALS.get(Sign.CONNECT, RELEASE));
/// }
/// ```
///
/// ## Type Parameters
///
/// @param <S> the Sign enum type implementing Serventis.Sign
/// @param <D> the Dimension enum type implementing Serventis.Dimension
/// @param <T> the Signal record type implementing Serventis.Signal
/// @author William David Louth
/// @since 1.0

public final class SignalSet <
  S extends Enum < S > & Sign,
  D extends Enum < D > & Dimension,
  T extends Record & Signal < S, D >
  > {

  private final T[][] signals;

  /// Constructs a new signal set with all Sign × Dimension combinations pre-allocated.
  ///
  /// @param signClass      the class object for the sign enum
  /// @param dimensionClass the class object for the dimension enum
  /// @param factory        a function that creates signal instances from sign and dimension

  @SuppressWarnings ( "unchecked" )
  public SignalSet (
    @NotNull final Class < S > signClass,
    @NotNull final Class < D > dimensionClass,
    @NotNull final Factory < S, D, T > factory
  ) {

    final var signs =
      signClass.getEnumConstants ();

    final var dimensions =
      dimensionClass.getEnumConstants ();

    signals =
      (T[][]) new Record[signs.length][dimensions.length];

    for ( final var sign : signs ) {

      for ( final var dimension : dimensions ) {

        signals[sign.ordinal ()][dimension.ordinal ()] =
          factory.create (
            sign,
            dimension
          );

      }

    }

  }

  /// A factory function for creating signal instances from sign and dimension components.
  ///
  /// @param <S> the Sign enum type
  /// @param <D> the Dimension enum type
  /// @param <T> the Signal type

  @FunctionalInterface
  public interface Factory < S, D, T > {

    /// Creates a signal instance from the given sign and dimension.
    ///
    /// @param sign      the sign component
    /// @param dimension the dimension component
    /// @return a new signal instance

    T create (
      S sign,
      D dimension
    );

  }

  /// Retrieves the pre-allocated signal for the given sign and dimension.
  ///
  /// This method provides O(1) lookup with no allocation overhead, as all signals
  /// are constructed during set initialization.
  ///
  /// @param sign      the sign component
  /// @param dimension the dimension component
  /// @return the pre-allocated signal for this sign/dimension combination

  public T get (
    @NotNull final S sign,
    @NotNull final D dimension
  ) {

    return
      signals[sign.ordinal ()][dimension.ordinal ()];

  }

}
