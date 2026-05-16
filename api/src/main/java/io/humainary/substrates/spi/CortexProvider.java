/// Copyright © 2025 William David Louth
package io.humainary.substrates.spi;

import io.humainary.substrates.api.Substrates;
import io.humainary.substrates.api.Substrates.Cortex;
import io.humainary.substrates.api.Substrates.NotNull;
import io.humainary.substrates.api.Substrates.Utility;

import java.util.ServiceLoader;

/// Abstract provider class for loading the Cortex singleton instance.
///
/// SPI providers must extend this class and implement the [#create()] method to return
/// their Cortex implementation. Provider discovery uses two mechanisms:
///
/// 1. **System property** (primary): Specify via [Substrates#PROVIDER_PROPERTY]
/// 2. **ServiceLoader** (fallback): Configure via `META-INF/services/` resource
///
/// All providers must have a public no-arg constructor.
///
/// The singleton is held in a static final field initialized when this class is
/// initialized. In normal API use that initialization is triggered by the first
/// call to [Substrates#cortex()]. Java class initialization provides the required
/// thread-safety without additional synchronization in this class.
///
/// @see Cortex
/// @see ServiceLoader
/// @see Substrates#cortex()

@Utility
public abstract class CortexProvider {

  /// The singleton Cortex instance, initialized on first access to this class.
  private static final Cortex INSTANCE = load ();

  /// Protected constructor for subclasses.
  ///
  /// SPI providers must provide a public no-arg constructor that delegates to this.
  protected CortexProvider () { }

  /// Returns the singleton Cortex instance.
  ///
  /// This method is called by [Substrates#cortex()] to obtain the Cortex instance.
  ///
  /// @return The singleton Cortex instance

  @NotNull
  public static Cortex cortex () {

    return INSTANCE;

  }

  /// Loads the Cortex instance by instantiating the provider subclass.
  /// First attempts to load via system property, then falls back to ServiceLoader.
  /// If multiple ServiceLoader providers are found, an exception is thrown requiring
  /// the system property to be set for explicit selection.
  ///
  /// @return A Cortex instance from the provider
  /// @throws IllegalStateException               if no provider is found, the configured provider
  ///                               class cannot be reflectively instantiated, the
  ///                               configured class is not a CortexProvider, or
  ///                               multiple ServiceLoader providers are found
  /// @throws java.util.ServiceConfigurationError if ServiceLoader provider discovery
  ///                                            or provider instantiation fails
  /// @throws RuntimeException                    if the selected provider's [#create()] method fails

  @NotNull
  private static Cortex load () {

    final var name =
      System.getProperty (
        Substrates.PROVIDER_PROPERTY
      );

    // Try system property first (primary mechanism)
    if ( name != null && !name.isBlank () ) {

      try {

        final var instance =
          Class
            .forName ( name )
            .getDeclaredConstructor ()
            .newInstance ();

        if ( instance instanceof final CortexProvider provider ) {

          return provider.create ();

        }

        throw new IllegalStateException (
          "Provider class '%s' does not extend CortexProvider".formatted ( name )
        );

      } catch (
        final ReflectiveOperationException e
      ) {

        throw new IllegalStateException (
          "Failed to load cortex provider class '%s': %s".formatted (
            name,
            e.getMessage ()
          ),
          e
        );

      }

    }

    // Fall back to ServiceLoader discovery
    final var providers =
      ServiceLoader
        .load ( CortexProvider.class )
        .stream ()
        .toList ();

    return switch ( providers.size () ) {

      case 0 -> throw new IllegalStateException (
        "No Provider found. Either set system property '%s' or provide a ServiceLoader configuration."
          .formatted ( Substrates.PROVIDER_PROPERTY )
      );

      case 1 -> providers
        .getFirst ()
        .get ()
        .create ();

      default -> throw new IllegalStateException (
        "Multiple providers found (%d). Set system property '%s' to select one explicitly."
          .formatted ( providers.size (), Substrates.PROVIDER_PROPERTY )
      );

    };

  }

  /// Creates the Cortex implementation provided by this provider.
  ///
  /// SPI providers must implement this method to return their Cortex instance.
  /// This method is called once during provider initialization.
  ///
  /// @return The Cortex implementation

  @NotNull
  protected abstract Cortex create ();

}
