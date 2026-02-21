// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.opt.pool;

import io.humainary.substrates.ext.serventis.opt.pool.Pools.Pool;
import io.humainary.substrates.ext.serventis.opt.pool.Pools.Sign;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.humainary.substrates.api.Substrates.*;
import static io.humainary.substrates.ext.serventis.opt.pool.Pools.Sign.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


/// Tests for the [Pools] API.
///
/// @author William David Louth
/// @since 1.0

final class PoolsTest {

  private static final Cortex             CORTEX = cortex ();
  private static final Name               NAME   = CORTEX.name ( "db.connections" );
  private              Circuit            circuit;
  private              Reservoir < Sign > reservoir;
  private              Pool               pool;

  @BeforeEach
  void setup () {

    circuit =
      CORTEX.circuit ();

    final var conduit =
      circuit.conduit (
        Pools::composer
      );

    pool =
      conduit.percept (
        NAME
      );

    reservoir =
      conduit.reservoir ();

  }

  @Test
  void testAdaptiveScalingPattern () {

    // Simulate adaptive scaling: grow on demand, shrink when idle
    pool.expand ();
    pool.expand ();
    pool.borrow ();
    pool.borrow ();
    pool.reclaim ();
    pool.reclaim ();
    pool.contract ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 7, signs.size () );
    assertEquals ( EXPAND, signs.get ( 0 ) );
    assertEquals ( EXPAND, signs.get ( 1 ) );
    assertEquals ( BORROW, signs.get ( 2 ) );
    assertEquals ( BORROW, signs.get ( 3 ) );
    assertEquals ( RECLAIM, signs.get ( 4 ) );
    assertEquals ( RECLAIM, signs.get ( 5 ) );
    assertEquals ( CONTRACT, signs.get ( 6 ) );

  }

  @Test
  void testAllSigns () {

    pool.expand ();
    pool.contract ();
    pool.borrow ();
    pool.reclaim ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 4, signs.size () );
    assertEquals ( EXPAND, signs.getFirst () );
    assertEquals ( CONTRACT, signs.get ( 1 ) );
    assertEquals ( BORROW, signs.get ( 2 ) );
    assertEquals ( RECLAIM, signs.get ( 3 ) );

  }

  @Test
  void testBorrow () {

    pool.borrow ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( BORROW, signs.getFirst () );

  }

  @Test
  void testCapacityOscillationPattern () {

    // Simulate capacity oscillation (unstable sizing)
    pool.expand ();
    pool.contract ();
    pool.expand ();
    pool.contract ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 4, signs.size () );

    final var expandCount =
      signs
        .stream ()
        .filter ( s -> s == EXPAND )
        .count ();

    final var contractCount =
      signs
        .stream ()
        .filter ( s -> s == CONTRACT )
        .count ();

    assertEquals ( 2, expandCount );
    assertEquals ( 2, contractCount );

  }

  @Test
  void testContract () {

    pool.contract ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( CONTRACT, signs.getFirst () );

  }

  @Test
  void testExpand () {

    pool.expand ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( EXPAND, signs.getFirst () );

  }

  @Test
  void testHealthyUtilizationPattern () {

    // Simulate healthy utilization (balanced borrow/reclaim)
    pool.borrow ();
    pool.reclaim ();
    pool.borrow ();
    pool.reclaim ();
    pool.borrow ();
    pool.reclaim ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 6, signs.size () );

    final var borrowCount =
      signs
        .stream ()
        .filter ( s -> s == BORROW )
        .count ();

    final var reclaimCount =
      signs
        .stream ()
        .filter ( s -> s == RECLAIM )
        .count ();

    assertEquals ( 3, borrowCount );
    assertEquals ( 3, reclaimCount );

  }

  @Test
  void testPoolInitializationPattern () {

    // Simulate pool initialization (multiple grows)
    pool.expand ();
    pool.expand ();
    pool.expand ();
    pool.expand ();
    pool.expand ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 5, signs.size () );

    final var expandCount =
      signs
        .stream ()
        .filter ( s -> s == EXPAND )
        .count ();

    assertEquals ( 5, expandCount );

  }

  @Test
  void testReclaim () {

    pool.reclaim ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( RECLAIM, signs.getFirst () );

  }

  @Test
  void testResourceLeakPattern () {

    // Simulate resource leak (borrows without reclaims)
    pool.borrow ();
    pool.borrow ();
    pool.borrow ();
    pool.borrow ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 4, signs.size () );

    final var borrowCount =
      signs
        .stream ()
        .filter ( s -> s == BORROW )
        .count ();

    final var reclaimCount =
      signs
        .stream ()
        .filter ( s -> s == RECLAIM )
        .count ();

    assertEquals ( 4, borrowCount );
    assertEquals ( 0, reclaimCount );

  }

  @Test
  void testSaturationPattern () {

    // Simulate saturation (high borrow rate, capacity limit)
    pool.borrow ();
    pool.borrow ();
    pool.borrow ();
    pool.expand ();
    pool.borrow ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 5, signs.size () );
    assertEquals ( BORROW, signs.get ( 0 ) );
    assertEquals ( BORROW, signs.get ( 1 ) );
    assertEquals ( BORROW, signs.get ( 2 ) );
    assertEquals ( EXPAND, signs.get ( 3 ) );
    assertEquals ( BORROW, signs.get ( 4 ) );

  }

  @Test
  void testSign () {

    // Test direct sign() method for all sign values
    pool.sign ( EXPAND );
    pool.sign ( CONTRACT );
    pool.sign ( BORROW );
    pool.sign ( RECLAIM );

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 4, signs.size () );
    assertEquals ( EXPAND, signs.get ( 0 ) );
    assertEquals ( CONTRACT, signs.get ( 1 ) );
    assertEquals ( BORROW, signs.get ( 2 ) );
    assertEquals ( RECLAIM, signs.get ( 3 ) );

  }

  @Test
  void testSignEnumOrdinals () {

    // Ensure ordinals remain stable for compatibility
    assertEquals ( 0, EXPAND.ordinal () );
    assertEquals ( 1, CONTRACT.ordinal () );
    assertEquals ( 2, BORROW.ordinal () );
    assertEquals ( 3, RECLAIM.ordinal () );

  }

  @Test
  void testSignEnumValues () {

    final var values = Sign.values ();

    assertEquals ( 4, values.length );
    assertEquals ( EXPAND, values[0] );
    assertEquals ( CONTRACT, values[1] );
    assertEquals ( BORROW, values[2] );
    assertEquals ( RECLAIM, values[3] );

  }

  @Test
  void testSubjectAttachment () {

    pool.expand ();

    circuit.await ();

    final var capture =
      reservoir
        .drain ()
        .findFirst ()
        .orElseThrow ();

    assertEquals ( NAME, capture.subject ().name () );
    assertEquals ( EXPAND, capture.emission () );

  }

}
