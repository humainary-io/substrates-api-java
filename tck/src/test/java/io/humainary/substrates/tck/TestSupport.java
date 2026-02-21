package io.humainary.substrates.tck;

import io.humainary.substrates.api.Substrates;

/// Common support for substrate TCK classes.
///
/// Provides access to the singleton Cortex and common utility methods
/// for test implementations. All TCK test classes extend this base class
/// to inherit Substrates types and helper methods.
///
/// The SPI provider is configured in the module pom.xml.
///
/// @author William David Louth
/// @since 1.0
abstract class TestSupport
  implements Substrates {

  /// Returns the singleton Cortex instance for test use.
  ///
  /// @return the cortex instance
  static Cortex cortex () {
    return Substrates.cortex ();
  }

  /// Creates an identity composer that returns the channel's pipe.
  ///
  /// Useful for creating simple conduits that emit values directly
  /// to observer pipes without transformation.
  ///
  /// @param <T> the emission type
  /// @return a composer that maps channels to their pipes
  static < T > Composer < T, Pipe < T > > identity () {
    return Channel::pipe;
  }

}
