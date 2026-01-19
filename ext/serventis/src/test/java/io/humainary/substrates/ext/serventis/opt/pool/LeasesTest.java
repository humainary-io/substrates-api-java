// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.opt.pool;

import io.humainary.substrates.ext.serventis.opt.pool.Leases.Dimension;
import io.humainary.substrates.ext.serventis.opt.pool.Leases.Lease;
import io.humainary.substrates.ext.serventis.opt.pool.Leases.Sign;
import io.humainary.substrates.ext.serventis.opt.pool.Leases.Signal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.humainary.substrates.api.Substrates.*;
import static io.humainary.substrates.ext.serventis.opt.pool.Leases.Dimension.LESSEE;
import static io.humainary.substrates.ext.serventis.opt.pool.Leases.Dimension.LESSOR;
import static org.junit.jupiter.api.Assertions.assertEquals;


/// The test class for the [Lease] interface.
///
/// @author William David Louth
/// @since 1.0
final class LeasesTest {

  private static final Cortex               cortex = cortex ();
  private static final Name                 NAME   = cortex.name ( "leadership" );
  private              Circuit              circuit;
  private              Reservoir < Signal > reservoir;
  private              Lease                lease;

  private void assertSignal (
    final Signal signal
  ) {

    circuit
      .await ();

    assertEquals (
      1L, reservoir
        .drain ()
        .filter ( capture -> capture.subject ().name () == NAME )
        .map ( Capture::emission )
        .filter ( s -> s.equals ( signal ) )
        .count ()

    );

  }

  private void emit (
    final Signal signal,
    final Runnable emitter
  ) {

    emitter.run ();

    assertSignal (
      signal
    );

  }

  @BeforeEach
  void setup () {

    circuit =
      cortex ().circuit ();

    final var conduit =
      circuit.conduit (
        Leases::composer
      );

    lease =
      conduit.percept (
        NAME
      );

    reservoir =
      conduit.reservoir ();

  }

  @Test
  void testAcquire () {

    emit ( new Signal ( Sign.ACQUIRE, LESSEE ), () -> lease.acquire ( LESSEE ) );

  }

  @Test
  void testAcquired () {

    emit ( new Signal ( Sign.ACQUIRE, LESSOR ), () -> lease.acquire ( LESSOR ) );

  }

  @Test
  void testDenied () {

    emit ( new Signal ( Sign.DENY, LESSEE ), () -> lease.deny ( LESSEE ) );

  }

  @Test
  void testDeny () {

    emit ( new Signal ( Sign.DENY, LESSOR ), () -> lease.deny ( LESSOR ) );

  }

  @Test
  void testDimensionEnumOrdinals () {

    assertEquals ( 0, LESSOR.ordinal () );
    assertEquals ( 1, LESSEE.ordinal () );

  }

  @Test
  void testDimensionEnumValues () {

    final var values = Dimension.values ();

    assertEquals ( 2, values.length );

  }

  @Test
  void testExpire () {

    emit ( new Signal ( Sign.EXPIRE, LESSOR ), () -> lease.expire ( LESSOR ) );

  }

  @Test
  void testExpired () {

    emit ( new Signal ( Sign.EXPIRE, LESSEE ), () -> lease.expire ( LESSEE ) );

  }

  @Test
  void testExtend () {

    emit ( new Signal ( Sign.EXTEND, LESSOR ), () -> lease.extend ( LESSOR ) );

  }

  @Test
  void testExtended () {

    emit ( new Signal ( Sign.EXTEND, LESSEE ), () -> lease.extend ( LESSEE ) );

  }

  @Test
  void testGrant () {

    emit ( new Signal ( Sign.GRANT, LESSOR ), () -> lease.grant ( LESSOR ) );

  }

  @Test
  void testGranted () {

    emit ( new Signal ( Sign.GRANT, LESSEE ), () -> lease.grant ( LESSEE ) );

  }

  @Test
  void testMultipleEmissions () {

    lease.acquire ( LESSEE );
    lease.grant ( LESSOR );
    lease.renew ( LESSEE );
    lease.extend ( LESSOR );

    circuit
      .await ();

    assertEquals (
      4L,
      reservoir
        .drain ()
        .count ()
    );

  }

  @Test
  void testProbe () {

    emit ( new Signal ( Sign.PROBE, LESSOR ), () -> lease.probe ( LESSOR ) );

  }

  @Test
  void testProbed () {

    emit ( new Signal ( Sign.PROBE, LESSEE ), () -> lease.probe ( LESSEE ) );

  }

  @Test
  void testRelease () {

    emit ( new Signal ( Sign.RELEASE, LESSEE ), () -> lease.release ( LESSEE ) );

  }

  @Test
  void testReleased () {

    emit ( new Signal ( Sign.RELEASE, LESSOR ), () -> lease.release ( LESSOR ) );

  }

  @Test
  void testRenew () {

    emit ( new Signal ( Sign.RENEW, LESSEE ), () -> lease.renew ( LESSEE ) );

  }

  @Test
  void testRenewed () {

    emit ( new Signal ( Sign.RENEW, LESSOR ), () -> lease.renew ( LESSOR ) );

  }

  @Test
  void testRevoke () {

    emit ( new Signal ( Sign.REVOKE, LESSOR ), () -> lease.revoke ( LESSOR ) );

  }

  @Test
  void testRevoked () {

    emit ( new Signal ( Sign.REVOKE, LESSEE ), () -> lease.revoke ( LESSEE ) );

  }

  @Test
  void testSignEnumOrdinals () {

    // Ensure ordinals remain stable for compatibility
    assertEquals ( 0, Sign.ACQUIRE.ordinal () );
    assertEquals ( 1, Sign.DENY.ordinal () );
    assertEquals ( 2, Sign.EXTEND.ordinal () );
    assertEquals ( 3, Sign.EXPIRE.ordinal () );
    assertEquals ( 4, Sign.GRANT.ordinal () );
    assertEquals ( 5, Sign.PROBE.ordinal () );
    assertEquals ( 6, Sign.RELEASE.ordinal () );
    assertEquals ( 7, Sign.RENEW.ordinal () );
    assertEquals ( 8, Sign.REVOKE.ordinal () );

  }

  @Test
  void testSignEnumValues () {

    final var values = Sign.values ();

    assertEquals ( 9, values.length );

  }

  @Test
  void testSignal () {

    // Test direct signal() method for all sign and dimension combinations
    lease.signal ( Sign.ACQUIRE, LESSEE );
    lease.signal ( Sign.ACQUIRE, LESSOR );
    lease.signal ( Sign.DENY, LESSOR );
    lease.signal ( Sign.DENY, LESSEE );
    lease.signal ( Sign.EXTEND, LESSOR );
    lease.signal ( Sign.EXTEND, LESSEE );
    lease.signal ( Sign.EXPIRE, LESSOR );
    lease.signal ( Sign.EXPIRE, LESSEE );
    lease.signal ( Sign.GRANT, LESSOR );
    lease.signal ( Sign.GRANT, LESSEE );
    lease.signal ( Sign.PROBE, LESSOR );
    lease.signal ( Sign.PROBE, LESSEE );
    lease.signal ( Sign.RELEASE, LESSEE );
    lease.signal ( Sign.RELEASE, LESSOR );
    lease.signal ( Sign.RENEW, LESSEE );
    lease.signal ( Sign.RENEW, LESSOR );
    lease.signal ( Sign.REVOKE, LESSOR );
    lease.signal ( Sign.REVOKE, LESSEE );

    circuit.await ();

    final var signals =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 18, signals.size () );
    assertEquals ( new Signal ( Sign.ACQUIRE, LESSEE ), signals.get ( 0 ) );
    assertEquals ( new Signal ( Sign.ACQUIRE, LESSOR ), signals.get ( 1 ) );
    assertEquals ( new Signal ( Sign.DENY, LESSOR ), signals.get ( 2 ) );
    assertEquals ( new Signal ( Sign.DENY, LESSEE ), signals.get ( 3 ) );
    assertEquals ( new Signal ( Sign.EXTEND, LESSOR ), signals.get ( 4 ) );
    assertEquals ( new Signal ( Sign.EXTEND, LESSEE ), signals.get ( 5 ) );
    assertEquals ( new Signal ( Sign.EXPIRE, LESSOR ), signals.get ( 6 ) );
    assertEquals ( new Signal ( Sign.EXPIRE, LESSEE ), signals.get ( 7 ) );
    assertEquals ( new Signal ( Sign.GRANT, LESSOR ), signals.get ( 8 ) );
    assertEquals ( new Signal ( Sign.GRANT, LESSEE ), signals.get ( 9 ) );
    assertEquals ( new Signal ( Sign.PROBE, LESSOR ), signals.get ( 10 ) );
    assertEquals ( new Signal ( Sign.PROBE, LESSEE ), signals.get ( 11 ) );
    assertEquals ( new Signal ( Sign.RELEASE, LESSEE ), signals.get ( 12 ) );
    assertEquals ( new Signal ( Sign.RELEASE, LESSOR ), signals.get ( 13 ) );
    assertEquals ( new Signal ( Sign.RENEW, LESSEE ), signals.get ( 14 ) );
    assertEquals ( new Signal ( Sign.RENEW, LESSOR ), signals.get ( 15 ) );
    assertEquals ( new Signal ( Sign.REVOKE, LESSOR ), signals.get ( 16 ) );
    assertEquals ( new Signal ( Sign.REVOKE, LESSEE ), signals.get ( 17 ) );

  }

  @Test
  void testSignalAccessors () {

    final var acquire = new Signal ( Sign.ACQUIRE, LESSEE );
    assertEquals ( Sign.ACQUIRE, acquire.sign () );
    assertEquals ( LESSEE, acquire.dimension () );

    final var grant = new Signal ( Sign.GRANT, LESSOR );
    assertEquals ( Sign.GRANT, grant.sign () );
    assertEquals ( LESSOR, grant.dimension () );

    final var renew = new Signal ( Sign.RENEW, LESSEE );
    assertEquals ( Sign.RENEW, renew.sign () );
    assertEquals ( LESSEE, renew.dimension () );

    final var expire = new Signal ( Sign.EXPIRE, LESSOR );
    assertEquals ( Sign.EXPIRE, expire.sign () );
    assertEquals ( LESSOR, expire.dimension () );

  }

  @Test
  void testSignalCoverage () {

    // 9 signs × 2 dimensions = 18 signal combinations
    assertEquals ( 9, Sign.values ().length );
    assertEquals ( 2, Dimension.values ().length );

  }

  @Test
  void testSubjectAttachment () {

    lease.acquire ( LESSEE );

    circuit.await ();

    final var capture =
      reservoir
        .drain ()
        .findFirst ()
        .orElseThrow ();

    assertEquals ( NAME, capture.subject ().name () );
    assertEquals ( new Signal ( Sign.ACQUIRE, LESSEE ), capture.emission () );

  }

}
