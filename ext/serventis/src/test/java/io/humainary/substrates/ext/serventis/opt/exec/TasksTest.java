// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.opt.exec;

import io.humainary.substrates.ext.serventis.opt.exec.Tasks.Sign;
import io.humainary.substrates.ext.serventis.opt.exec.Tasks.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.humainary.substrates.api.Substrates.*;
import static io.humainary.substrates.ext.serventis.opt.exec.Tasks.Sign.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


/// Tests for the [Tasks] API.
///
/// @author William David Louth
/// @since 1.0

final class TasksTest {

  private static final Cortex             CORTEX = cortex ();
  private static final Name               NAME   = CORTEX.name ( "worker.task" );
  private              Circuit            circuit;
  private              Reservoir < Sign > reservoir;
  private              Task               task;

  @BeforeEach
  void setup () {

    circuit =
      CORTEX.circuit ();

    final var conduit =
      circuit.conduit (
        Tasks::composer
      );

    task =
      conduit.percept (
        NAME
      );

    reservoir =
      conduit.reservoir ();

  }

  @Test
  void testCancel () {

    task.cancel ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( CANCEL, signs.getFirst () );

  }

  @Test
  void testCancelledTask () {

    task.submit ();
    task.schedule ();
    task.cancel ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 3, signs.size () );
    assertEquals ( SUBMIT, signs.get ( 0 ) );
    assertEquals ( SCHEDULE, signs.get ( 1 ) );
    assertEquals ( CANCEL, signs.get ( 2 ) );

  }

  @Test
  void testComplete () {

    task.complete ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( COMPLETE, signs.getFirst () );

  }

  @Test
  void testFail () {

    task.fail ();

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
  void testFailedTaskLifecycle () {

    task.submit ();
    task.schedule ();
    task.start ();
    task.fail ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 4, signs.size () );
    assertEquals ( SUBMIT, signs.get ( 0 ) );
    assertEquals ( SCHEDULE, signs.get ( 1 ) );
    assertEquals ( START, signs.get ( 2 ) );
    assertEquals ( FAIL, signs.get ( 3 ) );

  }

  @Test
  void testProgress () {

    task.progress ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( PROGRESS, signs.getFirst () );

  }

  @Test
  void testReject () {

    task.reject ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( REJECT, signs.getFirst () );

  }

  @Test
  void testRejectedTask () {

    task.submit ();
    task.reject ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 2, signs.size () );
    assertEquals ( SUBMIT, signs.get ( 0 ) );
    assertEquals ( REJECT, signs.get ( 1 ) );

  }

  @Test
  void testResume () {

    task.resume ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( RESUME, signs.getFirst () );

  }

  @Test
  void testSchedule () {

    task.schedule ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( SCHEDULE, signs.getFirst () );

  }

  /// Tests that [Sign] enum ordinals remain stable for compatibility.
  ///
  /// This test ensures that the ordinal values of [Sign] enum constants
  /// do not change, which is critical for serialization and external integrations.

  @Test
  void testSignEnumOrdinals () {

    assertEquals ( 0, SUBMIT.ordinal () );
    assertEquals ( 1, REJECT.ordinal () );
    assertEquals ( 2, SCHEDULE.ordinal () );
    assertEquals ( 3, START.ordinal () );
    assertEquals ( 4, PROGRESS.ordinal () );
    assertEquals ( 5, SUSPEND.ordinal () );
    assertEquals ( 6, RESUME.ordinal () );
    assertEquals ( 7, COMPLETE.ordinal () );
    assertEquals ( 8, FAIL.ordinal () );
    assertEquals ( 9, CANCEL.ordinal () );
    assertEquals ( 10, TIMEOUT.ordinal () );

  }

  @Test
  void testSignMethod () {

    // Test direct sign() method
    task.sign ( SUBMIT );
    task.sign ( COMPLETE );

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 2, signs.size () );
    assertEquals ( SUBMIT, signs.get ( 0 ) );
    assertEquals ( COMPLETE, signs.get ( 1 ) );

  }

  @Test
  void testStart () {

    task.start ();

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
  void testSubjectAssociation () {

    task.submit ();

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
  void testSubmit () {

    task.submit ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( SUBMIT, signs.getFirst () );

  }

  @Test
  void testSuccessfulTaskLifecycle () {

    task.submit ();
    task.schedule ();
    task.start ();
    task.complete ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 4, signs.size () );
    assertEquals ( SUBMIT, signs.get ( 0 ) );
    assertEquals ( SCHEDULE, signs.get ( 1 ) );
    assertEquals ( START, signs.get ( 2 ) );
    assertEquals ( COMPLETE, signs.get ( 3 ) );

  }

  @Test
  void testSuspend () {

    task.suspend ();

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
  void testSuspendedResumedTask () {

    task.submit ();
    task.schedule ();
    task.start ();
    task.suspend ();
    task.resume ();
    task.complete ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 6, signs.size () );
    assertEquals ( SUBMIT, signs.get ( 0 ) );
    assertEquals ( SCHEDULE, signs.get ( 1 ) );
    assertEquals ( START, signs.get ( 2 ) );
    assertEquals ( SUSPEND, signs.get ( 3 ) );
    assertEquals ( RESUME, signs.get ( 4 ) );
    assertEquals ( COMPLETE, signs.get ( 5 ) );

  }

  @Test
  void testTaskWithProgress () {

    task.submit ();
    task.schedule ();
    task.start ();
    task.progress ();
    task.progress ();
    task.complete ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 6, signs.size () );
    assertEquals ( SUBMIT, signs.get ( 0 ) );
    assertEquals ( SCHEDULE, signs.get ( 1 ) );
    assertEquals ( START, signs.get ( 2 ) );
    assertEquals ( PROGRESS, signs.get ( 3 ) );
    assertEquals ( PROGRESS, signs.get ( 4 ) );
    assertEquals ( COMPLETE, signs.get ( 5 ) );

  }

  @Test
  void testTimedOutTask () {

    task.submit ();
    task.schedule ();
    task.start ();
    task.timeout ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 4, signs.size () );
    assertEquals ( SUBMIT, signs.get ( 0 ) );
    assertEquals ( SCHEDULE, signs.get ( 1 ) );
    assertEquals ( START, signs.get ( 2 ) );
    assertEquals ( TIMEOUT, signs.get ( 3 ) );

  }

  @Test
  void testTimeout () {

    task.timeout ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( TIMEOUT, signs.getFirst () );

  }

}
