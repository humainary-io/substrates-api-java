// Copyright (c) 2025 William David Louth

package io.humainary.substrates.tck;

import org.junit.jupiter.api.Test;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static io.humainary.substrates.api.Substrates.Composer.pipe;
import static org.junit.jupiter.api.Assertions.assertEquals;

/// Tests for the Reservoir interface.
///
/// This test class covers:
/// - Memory retention after drain operations
/// - Proper cleanup of drained items
///
/// @author William David Louth
/// @since 1.0
final class ReservoirTest
  extends TestSupport {

  /// Validates that drained items are released for garbage collection.
  ///
  /// This test verifies that after draining a reservoir, the captured
  /// items are properly released and eligible for garbage collection.
  /// Without proper cleanup, drained items would remain reachable until
  /// the next emission, causing memory retention issues.
  ///
  /// Test Scenario:
  /// 1. Create reservoir capturing emissions
  /// 2. Emit large objects to detect retention
  /// 3. Drain the reservoir
  /// 4. Verify objects become eligible for GC
  @Test
  void testMemoryRetention () throws InterruptedException {

    final var cortex = cortex ();
    final var circuit = cortex.circuit ();

    try {

      final var conduit =
        circuit.conduit (
          pipe ( Object.class )
        );

      final var reservoir = conduit.reservoir ();

      final var name = cortex.name ( "test.channel" );
      final var pipe = conduit.percept ( name );

      final int count = 100;
      final List < WeakReference < Object > > refs = new ArrayList <> ();

      for ( int i = 0; i < count; i++ ) {
        final var obj = new byte[1024 * 1024]; // 1MB
        refs.add ( new WeakReference <> ( obj ) );
        pipe.emit ( obj );
      }

      circuit.await ();

      var drained = reservoir.drain ().toList ();
      assertEquals ( count, drained.size () );

      // Wait for cleanup job to execute
      circuit.await ();

      // Release strong refs
      //noinspection UnusedAssignment
      drained = null;

      for ( int i = 0; i < 10; i++ ) {
        System.gc ();
        Thread.sleep ( 100 );
      }

      final long retainedCount =
        refs.stream ()
          .filter ( r -> r.get () != null )
          .count ();

      if ( retainedCount > 0 ) {
        throw new AssertionError (
          "Memory leak detected! " + retainedCount + " objects retained."
        );
      }

    } finally {

      circuit.close ();

    }

  }

}
