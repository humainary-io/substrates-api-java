/// Copyright © 2025–2026 William David Louth
package io.humainary.substrates.spi;

import io.humainary.specs.api.Specs.SpecDoc;
import io.humainary.specs.api.Specs.SpecRef;
import io.humainary.substrates.api.Substrates;
import io.humainary.substrates.api.Substrates.Cortex;
import io.humainary.substrates.api.Substrates.NotNull;
import io.humainary.substrates.api.Substrates.Utility;

import java.util.ServiceLoader;

import static java.util.Objects.requireNonNull;

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
/// The singleton is held in a private holder class initialized by the first call to
/// [Substrates#cortex()]. Java class initialization provides the required thread-safety
/// without additional synchronization in this class. The holder is separate so that
/// initializing this class — which every provider subclass's initialization requires —
/// never loads a provider: a thread initializing a provider class while another loads the
/// singleton would otherwise wait on each other forever.
///
/// @see Cortex
/// @see ServiceLoader
/// @see Substrates#cortex()

@SpecDoc ( "https://github.com/humainary-io/substrates-api-spec/blob/3.12.0/SPEC.md" )
@Utility
@SpecRef ( "3" )
public abstract class CortexProvider {

  /// Protected constructor for subclasses.
  ///
  /// SPI providers must provide a public no-arg constructor that delegates to this.
  @SpecRef ( "3" )
  protected CortexProvider () { }

  /// Returns the singleton Cortex instance.
  ///
  /// This method is called by [Substrates#cortex()] to obtain the Cortex instance.
  ///
  /// @return The singleton Cortex instance
  /// @throws IllegalStateException if called from within the provider's own [#create()]

  @NotNull
  @SpecRef ( "3" )
  public static Cortex cortex () {

    final var cortex = Singleton.INSTANCE;

    // Only a thread still inside the holder's own initialization sees it unset:
    // a provider whose create() asks for the cortex it is creating.
    if ( cortex == null ) {
      throw new IllegalStateException (
        "The cortex was requested while its provider was still creating it"
      );
    }

    return cortex;

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
  /// @throws NullPointerException                if the selected provider's [#create()] returns `null`
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

        // Checked before construction, so a mistyped name never runs an
        // unrelated class's constructor.
        final var type = find ( name );

        if ( !CortexProvider.class.isAssignableFrom ( type ) ) {
          throw new IllegalStateException (
            "Provider class '%s' does not extend CortexProvider".formatted ( name )
          );
        }

        return create (
          type
            .asSubclass ( CortexProvider.class )
            .getDeclaredConstructor ()
            .newInstance ()
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

      case 1 -> create (
        providers
          .getFirst ()
          .get ()
      );

      default -> throw new IllegalStateException (
        "Multiple providers found (%d). Set system property '%s' to select one explicitly."
          .formatted ( providers.size (), Substrates.PROVIDER_PROPERTY )
      );

    };

  }

  /// Resolves a provider class by name the way [ServiceLoader] resolves the fallback, through the
  /// thread context class loader, so both mechanisms see the same classes. The loader that defined
  /// this class is tried after it, for a context loader that cannot see the provider.
  ///
  /// The class is not initialized here: construction does that, once it is known to be a provider.

  private static Class < ? > find (
    final String name
  ) throws ClassNotFoundException {

    final var context = Thread.currentThread ().getContextClassLoader ();

    if ( context != null ) {

      try {
        return Class.forName ( name, false, context );
      } catch ( final ClassNotFoundException _ ) {
        // fall through to this class's own loader
      }

    }

    return Class.forName ( name, false, CortexProvider.class.getClassLoader () );

  }

  @NotNull
  private static Cortex create (
    final CortexProvider provider
  ) {

    return requireNonNull (
      provider.create (),
      () -> "Provider '%s' created no Cortex".formatted ( provider.getClass ().getName () )
    );

  }

  /// Holds the singleton apart from [CortexProvider] itself; see the class documentation.

  private static final class Singleton {

    private static final Cortex INSTANCE = load ();

  }

  /// Creates the Cortex implementation provided by this provider.
  ///
  /// SPI providers must implement this method to return their Cortex instance.
  /// This method is called once during provider initialization.
  ///
  /// @return The Cortex implementation

  @NotNull
  @SpecRef ( "3" )
  protected abstract Cortex create ();

}
