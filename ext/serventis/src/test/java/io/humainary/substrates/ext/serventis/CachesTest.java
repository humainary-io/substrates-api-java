// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis;

import io.humainary.substrates.ext.serventis.ext.Caches;
import io.humainary.substrates.ext.serventis.ext.Caches.Cache;
import io.humainary.substrates.ext.serventis.ext.Caches.Sign;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.humainary.substrates.api.Substrates.*;
import static io.humainary.substrates.ext.serventis.ext.Caches.Sign.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


/// Tests for the [Caches] API.
///
/// @author William David Louth
/// @since 1.0

final class CachesTest {

  private static final Cortex             CORTEX = cortex ();
  private static final Name               NAME   = CORTEX.name ( "user.sessions" );
  private              Circuit            circuit;
  private              Reservoir < Sign > reservoir;
  private              Cache              cache;

  @BeforeEach
  void setup () {

    circuit =
      CORTEX.circuit ();

    final var conduit =
      circuit.conduit (
        Caches::composer
      );

    cache =
      conduit.percept (
        NAME
      );

    reservoir =
      CORTEX.reservoir (
        conduit
      );

  }

  @Test
  void testCacheHitScenario () {

    cache.lookup ();
    cache.hit ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 2, signs.size () );
    assertEquals ( LOOKUP, signs.getFirst () );
    assertEquals ( HIT, signs.get ( 1 ) );

  }

  @Test
  void testCacheMissScenario () {

    cache.lookup ();
    cache.miss ();
    cache.store ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 3, signs.size () );
    assertEquals ( LOOKUP, signs.getFirst () );
    assertEquals ( MISS, signs.get ( 1 ) );
    assertEquals ( STORE, signs.get ( 2 ) );

  }

  @Test
  void testEvict () {

    cache.evict ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( EVICT, signs.getFirst () );

  }

  @Test
  void testEvictionPattern () {

    cache.store ();
    cache.store ();
    cache.evict ();
    cache.store ();
    cache.evict ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 5, signs.size () );
    assertEquals ( STORE, signs.getFirst () );
    assertEquals ( STORE, signs.get ( 1 ) );
    assertEquals ( EVICT, signs.get ( 2 ) );
    assertEquals ( STORE, signs.get ( 3 ) );
    assertEquals ( EVICT, signs.get ( 4 ) );

  }

  @Test
  void testExpirationPattern () {

    cache.store ();
    cache.expire ();
    cache.store ();
    cache.expire ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 4, signs.size () );
    assertEquals ( STORE, signs.getFirst () );
    assertEquals ( EXPIRE, signs.get ( 1 ) );
    assertEquals ( STORE, signs.get ( 2 ) );
    assertEquals ( EXPIRE, signs.get ( 3 ) );

  }

  @Test
  void testExpire () {

    cache.expire ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( EXPIRE, signs.getFirst () );

  }

  @Test
  void testHit () {

    cache.hit ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( HIT, signs.getFirst () );

  }

  @Test
  void testLookup () {

    cache.lookup ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( LOOKUP, signs.getFirst () );

  }

  @Test
  void testMiss () {

    cache.miss ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( MISS, signs.getFirst () );

  }

  @Test
  void testMixedCacheOperations () {

    cache.lookup ();
    cache.hit ();
    cache.lookup ();
    cache.miss ();
    cache.store ();
    cache.lookup ();
    cache.hit ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 7, signs.size () );
    assertEquals ( LOOKUP, signs.getFirst () );
    assertEquals ( HIT, signs.get ( 1 ) );
    assertEquals ( LOOKUP, signs.get ( 2 ) );
    assertEquals ( MISS, signs.get ( 3 ) );
    assertEquals ( STORE, signs.get ( 4 ) );
    assertEquals ( LOOKUP, signs.get ( 5 ) );
    assertEquals ( HIT, signs.get ( 6 ) );

  }

  @Test
  void testMixedRemovalTypes () {

    cache.store ();
    cache.evict ();
    cache.store ();
    cache.expire ();
    cache.store ();
    cache.remove ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 6, signs.size () );
    assertEquals ( STORE, signs.getFirst () );
    assertEquals ( EVICT, signs.get ( 1 ) );
    assertEquals ( STORE, signs.get ( 2 ) );
    assertEquals ( EXPIRE, signs.get ( 3 ) );
    assertEquals ( STORE, signs.get ( 4 ) );
    assertEquals ( REMOVE, signs.get ( 5 ) );

  }

  @Test
  void testRemovalPattern () {

    cache.store ();
    cache.remove ();
    cache.store ();
    cache.remove ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 4, signs.size () );
    assertEquals ( STORE, signs.getFirst () );
    assertEquals ( REMOVE, signs.get ( 1 ) );
    assertEquals ( STORE, signs.get ( 2 ) );
    assertEquals ( REMOVE, signs.get ( 3 ) );

  }

  @Test
  void testRemove () {

    cache.remove ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( REMOVE, signs.getFirst () );

  }

  @Test
  void testSign () {

    // Test direct sign() method for all sign values
    cache.sign ( LOOKUP );
    cache.sign ( HIT );
    cache.sign ( MISS );
    cache.sign ( STORE );
    cache.sign ( EVICT );
    cache.sign ( EXPIRE );
    cache.sign ( REMOVE );

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 7, signs.size () );
    assertEquals ( LOOKUP, signs.get ( 0 ) );
    assertEquals ( HIT, signs.get ( 1 ) );
    assertEquals ( MISS, signs.get ( 2 ) );
    assertEquals ( STORE, signs.get ( 3 ) );
    assertEquals ( EVICT, signs.get ( 4 ) );
    assertEquals ( EXPIRE, signs.get ( 5 ) );
    assertEquals ( REMOVE, signs.get ( 6 ) );

  }

  @Test
  void testSignEnumOrdinals () {

    // Ensure ordinals remain stable for compatibility
    assertEquals ( 0, LOOKUP.ordinal () );
    assertEquals ( 1, HIT.ordinal () );
    assertEquals ( 2, MISS.ordinal () );
    assertEquals ( 3, STORE.ordinal () );
    assertEquals ( 4, EVICT.ordinal () );
    assertEquals ( 5, EXPIRE.ordinal () );
    assertEquals ( 6, REMOVE.ordinal () );

  }

  @Test
  void testSignEnumValues () {

    final var values = Sign.values ();

    assertEquals ( 7, values.length );
    assertEquals ( LOOKUP, values[0] );
    assertEquals ( HIT, values[1] );
    assertEquals ( MISS, values[2] );
    assertEquals ( STORE, values[3] );
    assertEquals ( EVICT, values[4] );
    assertEquals ( EXPIRE, values[5] );
    assertEquals ( REMOVE, values[6] );

  }

  @Test
  void testStore () {

    cache.store ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( STORE, signs.getFirst () );

  }

  @Test
  void testSubjectAttachment () {

    cache.lookup ();

    circuit.await ();

    final var capture =
      reservoir
        .drain ()
        .findFirst ()
        .orElseThrow ();

    assertEquals ( NAME, capture.subject ().name () );
    assertEquals ( LOOKUP, capture.emission () );

  }

}
