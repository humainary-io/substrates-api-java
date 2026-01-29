// Copyright (c) 2025 William David Louth

package io.humainary.substrates.tck;

import io.humainary.substrates.api.Substrates;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

/// Tests for the [Current] interface and [Substrates.Cortex#current()] method.
///
/// This test class verifies:
/// - Identity semantics: same thread returns same instance
/// - Different threads get different instances
/// - Subject properties are accessible
/// - Platform vs virtual thread naming
///
/// @author William David Louth
/// @since 1.0

final class CurrentTest
  extends TestSupport {

  @Test
  void testCurrentHasSubject () {

    final var cortex =
      cortex ();

    final var current =
      cortex.current ();

    final var subject =
      current.subject ();

    assertNotNull ( subject );

  }

  /// Tests that [Current] honors identity semantics.
  /// The same thread must always get the same [Current] instance.

  @Test
  void testCurrentIdentitySemantics () {

    final var cortex =
      cortex ();

    final var current1 =
      cortex.current ();

    final var current2 =
      cortex.current ();

    assertSame (
      current1,
      current2,
      "Same thread should return same Current instance"
    );

  }

  /// Tests that Current subject name contains thread class information.

  @Test
  void testCurrentNameContainsThreadClass () {

    final var cortex =
      cortex ();

    final var current =
      cortex.current ();

    final var name =
      current.subject ().name ();

    final var nameString =
      name.toString ();

    // Name should be based on thread class
    // Could be something like "java.lang.Thread" or similar
    assertNotNull ( nameString );
    assertFalse (
      nameString.isEmpty (),
      "Current name should not be empty"
    );

  }

  @Test
  void testCurrentReturnsNonNull () {

    final var cortex =
      cortex ();

    final var current =
      cortex.current ();

    assertNotNull ( current );

  }

  /// Tests that [Current] instances have stable identities.
  /// Multiple calls should return instances with the same ID.

  @Test
  void testCurrentStableId () {

    final var cortex =
      cortex ();

    final var id1 =
      cortex.current ().subject ().id ();

    final var id2 =
      cortex.current ().subject ().id ();

    assertEquals (
      id1,
      id2,
      "Current ID should be stable across calls"
    );

  }

  @Test
  void testCurrentSubjectHasId () {

    final var cortex =
      cortex ();

    final var current =
      cortex.current ();

    final var id =
      current.subject ().id ();

    assertNotNull ( id );

  }

  @Test
  void testCurrentSubjectHasName () {

    final var cortex =
      cortex ();

    final var current =
      cortex.current ();

    final var name =
      current.subject ().name ();

    assertNotNull ( name );

  }

  /// Validates that Current instances are thread-local: each thread gets its own.
  ///
  /// Creates a platform thread and verifies that cortex.current() returns a
  /// different Current instance than the main thread's. This tests the
  /// thread-local semantics of the Current abstraction.
  ///
  /// Current provides thread-local context, similar to ThreadLocal<T> but with
  /// substrate semantics (Subject, Name, etc.). Each thread has its own unique
  /// Current instance that persists for the lifetime of that thread.
  ///
  /// Why this matters:
  /// - Enables thread-scoped context without explicit parameter passing
  /// - Provides unique identity per thread (for tracing, correlation)
  /// - Allows thread-specific configuration and state
  /// - Critical for observability: track which thread performed actions
  ///
  /// Uses CountDownLatch to ensure both threads have called cortex.current()
  /// before comparing instances.
  ///
  /// Expected: Main thread and spawned thread have different Current instances
  @Test
  void testDifferentThreadsGetDifferentCurrent ()
  throws InterruptedException {

    final var cortex =
      cortex ();

    final var mainCurrent =
      cortex.current ();

    final var latch =
      new CountDownLatch ( 1 );

    final var otherCurrent =
      new AtomicReference < Current > ();

    final var thread =
      Thread.ofPlatform ().start ( () -> {

        otherCurrent.set (
          cortex.current ()
        );

        latch.countDown ();

      } );

    latch.await ();
    thread.join ();

    assertNotNull ( otherCurrent.get () );
    assertNotSame (
      mainCurrent,
      otherCurrent.get (),
      "Different threads should have different Current instances"
    );

  }

  /// Tests that different threads have different IDs.

  @Test
  void testDifferentThreadsHaveDifferentIds ()
  throws InterruptedException {

    final var cortex =
      cortex ();

    final var mainId =
      cortex.current ().subject ().id ();

    final var latch =
      new CountDownLatch ( 1 );

    final var otherId =
      new AtomicReference < Id > ();

    final var thread =
      Thread.ofPlatform ().start ( () -> {

        otherId.set (
          cortex.current ().subject ().id ()
        );

        latch.countDown ();

      } );

    latch.await ();
    thread.join ();

    assertNotNull ( otherId.get () );
    assertNotEquals (
      mainId,
      otherId.get (),
      "Different threads should have different IDs"
    );

  }

  /// Validates that platform threads and virtual threads get different Current instances.
  ///
  /// Spawns both a platform thread and a virtual thread, verifying that each
  /// receives its own unique Current instance from cortex.current(). This tests
  /// that the thread-local semantics work correctly across both types of Java
  /// threads (traditional platform threads and lightweight virtual threads from
  /// Project Loom).
  ///
  /// Java's virtual threads (JEP 444) are lightweight threads managed by the JVM
  /// rather than the OS. They enable massive concurrency (millions of threads)
  /// with low overhead. The substrate's Current abstraction must work correctly
  /// with both thread types.
  ///
  /// This test ensures:
  /// - Platform threads get unique Current instances
  /// - Virtual threads get unique Current instances
  /// - Platform and virtual threads do NOT share Current instances
  ///
  /// Critical for modern concurrent applications using virtual threads:
  /// - Thread-per-request patterns with virtual threads
  /// - Structured concurrency with scoped values
  /// - Tracing and correlation across heterogeneous thread types
  ///
  /// Expected: Three different Current instances (main, platform, virtual)
  @Test
  void testPlatformAndVirtualThreadsHaveDifferentCurrent ()
  throws InterruptedException {

    final var cortex =
      cortex ();

    final var platformLatch =
      new CountDownLatch ( 1 );

    final var virtualLatch =
      new CountDownLatch ( 1 );

    final var platformCurrent =
      new AtomicReference < Current > ();

    final var virtualCurrent =
      new AtomicReference < Current > ();

    final var platformThread =
      Thread.ofPlatform ().start ( () -> {

        platformCurrent.set (
          cortex.current ()
        );

        platformLatch.countDown ();

      } );

    final var virtualThread =
      Thread.ofVirtual ().start ( () -> {

        virtualCurrent.set (
          cortex.current ()
        );

        virtualLatch.countDown ();

      } );

    platformLatch.await ();
    virtualLatch.await ();

    platformThread.join ();
    virtualThread.join ();

    assertNotNull ( platformCurrent.get () );
    assertNotNull ( virtualCurrent.get () );

    assertNotSame (
      platformCurrent.get (),
      virtualCurrent.get (),
      "Platform and virtual threads should have different Current instances"
    );

  }

  /// Tests that virtual threads get Current instances.

  @Test
  void testVirtualThreadCurrent ()
  throws InterruptedException {

    final var cortex =
      cortex ();

    final var latch =
      new CountDownLatch ( 1 );

    final var virtualCurrent =
      new AtomicReference < Current > ();

    final var thread =
      Thread.ofVirtual ().start ( () -> {

        virtualCurrent.set (
          cortex.current ()
        );

        latch.countDown ();

      } );

    latch.await ();
    thread.join ();

    assertNotNull (
      virtualCurrent.get (),
      "Virtual threads should have Current instances"
    );

    assertNotNull (
      virtualCurrent.get ().subject ().id (),
      "Virtual thread Current should have an ID"
    );

    assertNotNull (
      virtualCurrent.get ().subject ().name (),
      "Virtual thread Current should have a name"
    );

  }

}
