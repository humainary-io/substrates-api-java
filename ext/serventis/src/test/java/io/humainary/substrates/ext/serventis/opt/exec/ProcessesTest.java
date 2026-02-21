// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.opt.exec;

import io.humainary.substrates.ext.serventis.opt.exec.Processes.Process;
import io.humainary.substrates.ext.serventis.opt.exec.Processes.Sign;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.humainary.substrates.api.Substrates.*;
import static io.humainary.substrates.ext.serventis.opt.exec.Processes.Sign.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


/// Tests for the [Processes] API.
///
/// @author William David Louth
/// @since 1.0

final class ProcessesTest {

  private static final Cortex             CORTEX = cortex ();
  private static final Name               NAME   = CORTEX.name ( "worker.process" );
  private              Circuit            circuit;
  private              Reservoir < Sign > reservoir;
  private              Process            process;

  @BeforeEach
  void setup () {

    circuit =
      CORTEX.circuit ();

    final var conduit =
      circuit.conduit (
        Processes::composer
      );

    process =
      conduit.percept (
        NAME
      );

    reservoir =
      conduit.reservoir ();

  }

  @Test
  void testCleanProcess () {

    process.spawn ();
    process.start ();
    process.stop ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 3, signs.size () );
    assertEquals ( SPAWN, signs.get ( 0 ) );
    assertEquals ( START, signs.get ( 1 ) );
    assertEquals ( STOP, signs.get ( 2 ) );

  }

  @Test
  void testCrash () {

    process.crash ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( CRASH, signs.getFirst () );

  }

  @Test
  void testCrashedProcess () {

    process.spawn ();
    process.start ();
    process.crash ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 3, signs.size () );
    assertEquals ( SPAWN, signs.get ( 0 ) );
    assertEquals ( START, signs.get ( 1 ) );
    assertEquals ( CRASH, signs.get ( 2 ) );

  }

  @Test
  void testCrashedProcessWithRestart () {

    process.spawn ();
    process.start ();
    process.crash ();
    process.restart ();
    process.start ();
    process.stop ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 6, signs.size () );
    assertEquals ( SPAWN, signs.get ( 0 ) );
    assertEquals ( START, signs.get ( 1 ) );
    assertEquals ( CRASH, signs.get ( 2 ) );
    assertEquals ( RESTART, signs.get ( 3 ) );
    assertEquals ( START, signs.get ( 4 ) );
    assertEquals ( STOP, signs.get ( 5 ) );

  }

  @Test
  void testFail () {

    process.fail ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( FAIL, signs.getFirst () );

  }

  @Test
  void testFailedProcess () {

    process.spawn ();
    process.start ();
    process.fail ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 3, signs.size () );
    assertEquals ( SPAWN, signs.get ( 0 ) );
    assertEquals ( START, signs.get ( 1 ) );
    assertEquals ( FAIL, signs.get ( 2 ) );

  }

  @Test
  void testFailedProcessWithRestart () {

    process.spawn ();
    process.start ();
    process.fail ();
    process.restart ();
    process.start ();
    process.stop ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 6, signs.size () );
    assertEquals ( SPAWN, signs.get ( 0 ) );
    assertEquals ( START, signs.get ( 1 ) );
    assertEquals ( FAIL, signs.get ( 2 ) );
    assertEquals ( RESTART, signs.get ( 3 ) );
    assertEquals ( START, signs.get ( 4 ) );
    assertEquals ( STOP, signs.get ( 5 ) );

  }

  @Test
  void testKill () {

    process.kill ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( KILL, signs.getFirst () );

  }

  @Test
  void testKilledProcess () {

    process.start ();
    process.kill ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 2, signs.size () );
    assertEquals ( START, signs.get ( 0 ) );
    assertEquals ( KILL, signs.get ( 1 ) );

  }

  @Test
  void testRestart () {

    process.restart ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( RESTART, signs.getFirst () );

  }

  @Test
  void testResume () {

    process.resume ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( RESUME, signs.getFirst () );

  }

  /// Tests that [Sign] enum ordinals remain stable for compatibility.
  ///
  /// This test ensures that the ordinal values of [Sign] enum constants
  /// do not change, which is critical for serialization and external integrations.

  @Test
  void testSignEnumOrdinals () {

    assertEquals ( 0, SPAWN.ordinal () );
    assertEquals ( 1, START.ordinal () );
    assertEquals ( 2, STOP.ordinal () );
    assertEquals ( 3, FAIL.ordinal () );
    assertEquals ( 4, CRASH.ordinal () );
    assertEquals ( 5, KILL.ordinal () );
    assertEquals ( 6, RESTART.ordinal () );
    assertEquals ( 7, SUSPEND.ordinal () );
    assertEquals ( 8, RESUME.ordinal () );

  }

  @Test
  void testSignMethod () {

    // Test direct sign() method
    process.sign ( SPAWN );
    process.sign ( START );
    process.sign ( STOP );

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 3, signs.size () );
    assertEquals ( SPAWN, signs.get ( 0 ) );
    assertEquals ( START, signs.get ( 1 ) );
    assertEquals ( STOP, signs.get ( 2 ) );

  }

  @Test
  void testSpawn () {

    process.spawn ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( SPAWN, signs.getFirst () );

  }

  @Test
  void testStart () {

    process.start ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( START, signs.getFirst () );

  }

  @Test
  void testStop () {

    process.stop ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( STOP, signs.getFirst () );

  }

  @Test
  void testSubjectAssociation () {

    process.spawn ();

    circuit.await ();

    assertEquals (
      NAME,
      reservoir
        .drain ()
        .map ( Capture::subject )
        .map ( Subject::name )
        .findFirst ()
        .orElseThrow ()
    );

  }

  @Test
  void testSuspend () {

    process.suspend ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( SUSPEND, signs.getFirst () );

  }

  @Test
  void testSuspendedResumedProcess () {

    process.start ();
    process.suspend ();
    process.resume ();
    process.stop ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 4, signs.size () );
    assertEquals ( START, signs.get ( 0 ) );
    assertEquals ( SUSPEND, signs.get ( 1 ) );
    assertEquals ( RESUME, signs.get ( 2 ) );
    assertEquals ( STOP, signs.get ( 3 ) );

  }

}
