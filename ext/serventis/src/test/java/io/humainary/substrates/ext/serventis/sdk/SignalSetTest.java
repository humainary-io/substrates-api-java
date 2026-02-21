// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.sdk;

import io.humainary.substrates.ext.serventis.api.Serventis;
import io.humainary.substrates.ext.serventis.api.Serventis.Category;
import org.junit.jupiter.api.Test;

import static io.humainary.substrates.ext.serventis.sdk.SignalSetTest.TestDimension.CLIENT;
import static io.humainary.substrates.ext.serventis.sdk.SignalSetTest.TestDimension.SERVER;
import static io.humainary.substrates.ext.serventis.sdk.SignalSetTest.TestSign.*;
import static org.junit.jupiter.api.Assertions.*;

/// Tests for the [SignalSet] utility class.
///
/// @author William David Louth
/// @since 1.0

final class SignalSetTest {

  @Test
  void testAllSignDimensionCombinations () {

    final var signalSet =
      new SignalSet <> (
        TestSign.class,
        TestDimension.class,
        TestSignal::new
      );

    // Test all 6 combinations
    final var combinations = new TestSignal[]{
      signalSet.get ( CONNECT, CLIENT ),
      signalSet.get ( CONNECT, SERVER ),
      signalSet.get ( SEND, CLIENT ),
      signalSet.get ( SEND, SERVER ),
      signalSet.get ( RECEIVE, CLIENT ),
      signalSet.get ( RECEIVE, SERVER )
    };

    // Verify each combination has correct sign and dimension
    assertEquals ( CONNECT, combinations[0].sign () );
    assertEquals ( CLIENT, combinations[0].dimension () );

    assertEquals ( CONNECT, combinations[1].sign () );
    assertEquals ( SERVER, combinations[1].dimension () );

    assertEquals ( SEND, combinations[2].sign () );
    assertEquals ( CLIENT, combinations[2].dimension () );

    assertEquals ( SEND, combinations[3].sign () );
    assertEquals ( SERVER, combinations[3].dimension () );

    assertEquals ( RECEIVE, combinations[4].sign () );
    assertEquals ( CLIENT, combinations[4].dimension () );

    assertEquals ( RECEIVE, combinations[5].sign () );
    assertEquals ( SERVER, combinations[5].dimension () );

    // All should be distinct instances
    for ( int i = 0; i < combinations.length; i++ ) {
      for ( int j = i + 1; j < combinations.length; j++ ) {
        assertNotSame ( combinations[i], combinations[j] );
      }
    }

  }

  @Test
  void testConstructorCreatesAllCombinations () {

    final var signalSet =
      new SignalSet <> (
        TestSign.class,
        TestDimension.class,
        TestSignal::new
      );

    // Should create 3 signs × 2 dimensions = 6 signals
    assertNotNull ( signalSet );

  }

  @Test
  void testFactoryCalledCorrectNumberOfTimes () {

    final var callCount = new int[]{0};

    final var signalSet =
      new SignalSet <> (
        TestSign.class,
        TestDimension.class,
        ( sign, dimension ) -> {
          callCount[0]++;
          return new TestSignal ( sign, dimension );
        }
      );

    // Factory should have been called exactly 3 × 2 = 6 times during construction
    assertEquals ( 6, callCount[0] );

    // Getting signals should NOT call factory (pre-allocated)
    signalSet.get ( CONNECT, CLIENT );
    signalSet.get ( SEND, SERVER );
    signalSet.get ( RECEIVE, CLIENT );

    // Count should still be 6 (no additional calls)
    assertEquals ( 6, callCount[0] );

  }

  @Test
  void testGetReturnsCorrectSignal () {

    final var signalSet =
      new SignalSet <> (
        TestSign.class,
        TestDimension.class,
        TestSignal::new
      );

    final var signal =
      signalSet.get (
        CONNECT,
        CLIENT
      );

    assertNotNull ( signal );
    assertEquals ( CONNECT, signal.sign () );
    assertEquals ( CLIENT, signal.dimension () );

  }

  @Test
  void testGetReturnsDistinctSignals () {

    final var signalSet =
      new SignalSet <> (
        TestSign.class,
        TestDimension.class,
        TestSignal::new
      );

    final var signal1 =
      signalSet.get (
        CONNECT,
        CLIENT
      );

    final var signal2 =
      signalSet.get (
        CONNECT,
        SERVER
      );

    final var signal3 =
      signalSet.get (
        SEND,
        CLIENT
      );

    assertNotSame ( signal1, signal2 );
    assertNotSame ( signal1, signal3 );
    assertNotSame ( signal2, signal3 );

  }

  @Test
  void testGetReturnsPreAllocatedSignals () {

    final var signalSet =
      new SignalSet <> (
        TestSign.class,
        TestDimension.class,
        TestSignal::new
      );

    final var signal1 =
      signalSet.get (
        CONNECT,
        CLIENT
      );

    final var signal2 =
      signalSet.get (
        CONNECT,
        CLIENT
      );

    // Should return same pre-allocated instance (no new allocation)
    assertSame ( signal1, signal2 );

  }

  @Test
  void testLookupPerformancePattern () {

    final var signalSet =
      new SignalSet <> (
        TestSign.class,
        TestDimension.class,
        TestSignal::new
      );

    // Simulate real-world usage pattern: repeated lookups
    for ( int i = 0; i < 1000; i++ ) {

      final var signal1 = signalSet.get ( CONNECT, CLIENT );
      final var signal2 = signalSet.get ( SEND, SERVER );
      final var signal3 = signalSet.get ( RECEIVE, CLIENT );

      assertNotNull ( signal1 );
      assertNotNull ( signal2 );
      assertNotNull ( signal3 );

    }

    // Should still return same pre-allocated instances
    final var finalSignal = signalSet.get ( CONNECT, CLIENT );
    final var firstSignal = signalSet.get ( CONNECT, CLIENT );
    assertSame ( finalSignal, firstSignal );

  }

  @Test
  void testRepeatedGetsSameInstance () {

    final var signalSet =
      new SignalSet <> (
        TestSign.class,
        TestDimension.class,
        TestSignal::new
      );

    // Get same signal multiple times
    final var signal1 = signalSet.get ( SEND, SERVER );
    final var signal2 = signalSet.get ( SEND, SERVER );
    final var signal3 = signalSet.get ( SEND, SERVER );
    final var signal4 = signalSet.get ( SEND, SERVER );

    // All should be same instance (zero allocation)
    assertSame ( signal1, signal2 );
    assertSame ( signal2, signal3 );
    assertSame ( signal3, signal4 );

  }

  @Test
  void testSignOrdinalsRespected () {

    final var signalSet =
      new SignalSet <> (
        TestSign.class,
        TestDimension.class,
        TestSignal::new
      );

    // Verify ordinals are used correctly for lookup
    assertEquals ( 0, CONNECT.ordinal () );
    assertEquals ( 1, SEND.ordinal () );
    assertEquals ( 2, RECEIVE.ordinal () );

    assertEquals ( 0, CLIENT.ordinal () );
    assertEquals ( 1, SERVER.ordinal () );

    // Get signals in different ordinal order
    final var signal_0_0 = signalSet.get ( CONNECT, CLIENT );
    final var signal_0_1 = signalSet.get ( CONNECT, SERVER );
    final var signal_1_0 = signalSet.get ( SEND, CLIENT );
    final var signal_1_1 = signalSet.get ( SEND, SERVER );
    final var signal_2_0 = signalSet.get ( RECEIVE, CLIENT );
    final var signal_2_1 = signalSet.get ( RECEIVE, SERVER );

    // Verify correct sign/dimension pairings
    assertEquals ( CONNECT, signal_0_0.sign () );
    assertEquals ( CLIENT, signal_0_0.dimension () );

    assertEquals ( CONNECT, signal_0_1.sign () );
    assertEquals ( SERVER, signal_0_1.dimension () );

    assertEquals ( SEND, signal_1_0.sign () );
    assertEquals ( CLIENT, signal_1_0.dimension () );

    assertEquals ( SEND, signal_1_1.sign () );
    assertEquals ( SERVER, signal_1_1.dimension () );

    assertEquals ( RECEIVE, signal_2_0.sign () );
    assertEquals ( CLIENT, signal_2_0.dimension () );

    assertEquals ( RECEIVE, signal_2_1.sign () );
    assertEquals ( SERVER, signal_2_1.dimension () );

  }

  @Test
  void testSignalEquality () {

    final var signalSet =
      new SignalSet <> (
        TestSign.class,
        TestDimension.class,
        TestSignal::new
      );

    final var signal1 = signalSet.get ( CONNECT, CLIENT );
    final var signal2 = signalSet.get ( CONNECT, CLIENT );

    // Same instance, so should be equal
    assertEquals ( signal1, signal2 );

    // Records define equals based on components
    final var manualSignal = new TestSignal ( CONNECT, CLIENT );
    assertEquals ( signal1, manualSignal );

  }

  @Test
  void testSignalHashCode () {

    final var signalSet =
      new SignalSet <> (
        TestSign.class,
        TestDimension.class,
        TestSignal::new
      );

    final var signal = signalSet.get ( SEND, CLIENT );
    final var manualSignal = new TestSignal ( SEND, CLIENT );

    // Records define hashCode based on components
    assertEquals ( signal.hashCode (), manualSignal.hashCode () );

  }

  @Test
  void testSingleDimensionSignalSet () {

    final var signalSet =
      new SignalSet <> (
        SingleSign.class,
        SingleDimension.class,
        SingleSignal::new
      );

    final var signal = signalSet.get ( SingleSign.SINGLE, SingleDimension.ONLY );

    assertNotNull ( signal );
    assertEquals ( SingleSign.SINGLE, signal.sign () );
    assertEquals ( SingleDimension.ONLY, signal.dimension () );

    // Repeated gets should return same instance
    final var signal2 = signalSet.get ( SingleSign.SINGLE, SingleDimension.ONLY );
    assertSame ( signal, signal2 );

  }

  /// Test sign enum with 3 values
  enum TestSign implements Serventis.Sign {
    CONNECT,
    SEND,
    RECEIVE
  }

  /// Test dimension enum with 2 values
  enum TestDimension implements Category {
    CLIENT,
    SERVER
  }

  /// Test dimension enum with single value (edge case)
  enum SingleDimension implements Category {
    ONLY
  }

  /// Test sign enum with single value (edge case)
  enum SingleSign implements Serventis.Sign {
    SINGLE
  }

  /// Single signal record
  record SingleSignal(
    SingleSign sign,
    SingleDimension dimension
  ) implements Serventis.Signal < SingleSign, SingleDimension > { }

  /// Test signal record
  record TestSignal(
    TestSign sign,
    TestDimension dimension
  ) implements Serventis.Signal < TestSign, TestDimension > { }

}
