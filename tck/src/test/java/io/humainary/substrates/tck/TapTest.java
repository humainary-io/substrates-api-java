// Copyright (c) 2025 William David Louth

package io.humainary.substrates.tck;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static io.humainary.substrates.api.Substrates.Composer.pipe;
import static org.junit.jupiter.api.Assertions.*;

/// Tests for the Tap interface.
///
/// This test class covers:
/// - Basic tap creation and transformation
/// - Channel structure mirroring
/// - Subscription to tap source
/// - Resource lifecycle (close)
///
/// @author William David Louth
/// @since 1.0
final class TapTest
  extends TestSupport {

  private Cortex  cortex;
  private Circuit circuit;

  @BeforeEach
  void setup () {

    cortex = cortex ();
    circuit = cortex.circuit ();

  }

  @AfterEach
  void teardown () {

    circuit.close ();

  }

  /// Tests that a tap correctly transforms emissions from one type to another.
  ///
  /// Scenario:
  /// 1. Create a conduit emitting integers
  /// 2. Create a tap that transforms integers to strings
  /// 3. Subscribe to the tap
  /// 4. Emit integers through the conduit
  /// 5. Verify strings are received through the tap
  @Test
  void testBasicTransformation () {

    final var conduit =
      circuit.conduit (
        pipe ( Integer.class )
      );

    final Tap < String > tap =
      conduit.tap (
        i -> "value:" + i
      );

    assertNotNull ( tap );
    assertNotNull ( tap.subject () );

    final List < String > results = new ArrayList <> ();

    tap.subscribe (
      circuit.subscriber (
        cortex.name ( "collector" ),
        ( _, registrar ) ->
          registrar.register ( results::add )
      )
    );

    final var name = cortex.name ( "test.channel" );
    final var pipe = conduit.percept ( name );

    pipe.emit ( 1 );
    pipe.emit ( 2 );
    pipe.emit ( 3 );

    circuit.await ();

    assertEquals ( 3, results.size () );
    assertEquals ( "value:1", results.get ( 0 ) );
    assertEquals ( "value:2", results.get ( 1 ) );
    assertEquals ( "value:3", results.get ( 2 ) );

    tap.close ();

  }


  /// Tests that a tap mirrors the channel structure of its source.
  ///
  /// Scenario:
  /// 1. Create a conduit with multiple channels
  /// 2. Create a tap
  /// 3. Subscribe to the tap
  /// 4. Emit through different channels
  /// 5. Verify emissions come from corresponding tap channels
  @Test
  void testChannelMirroring () {

    final var conduit =
      circuit.conduit (
        pipe ( Integer.class )
      );

    final Tap < String > tap =
      conduit.tap (
        i -> "num:" + i
      );

    final List < String > channelAResults = new ArrayList <> ();
    final List < String > channelBResults = new ArrayList <> ();

    final var channelA = cortex.name ( "channel.a" );
    final var channelB = cortex.name ( "channel.b" );

    tap.subscribe (
      circuit.subscriber (
        cortex.name ( "collector" ),
        ( subject, registrar ) -> {
          if ( subject.name ().equals ( channelA ) ) {
            registrar.register ( channelAResults::add );
          } else if ( subject.name ().equals ( channelB ) ) {
            registrar.register ( channelBResults::add );
          }
        }
      )
    );

    final var pipeA = conduit.percept ( channelA );
    final var pipeB = conduit.percept ( channelB );

    pipeA.emit ( 1 );
    pipeB.emit ( 2 );
    pipeA.emit ( 3 );

    circuit.await ();

    assertEquals ( 2, channelAResults.size () );
    assertEquals ( 1, channelBResults.size () );

    assertEquals ( "num:1", channelAResults.get ( 0 ) );
    assertEquals ( "num:3", channelAResults.get ( 1 ) );
    assertEquals ( "num:2", channelBResults.getFirst () );

    tap.close ();

  }


  /// Tests that closing a tap stops receiving emissions.
  ///
  /// Scenario:
  /// 1. Create conduit and tap
  /// 2. Subscribe to tap
  /// 3. Emit some values
  /// 4. Close the tap
  /// 5. Emit more values
  /// 6. Verify only pre-close emissions were received
  @Test
  void testCloseStopsEmissions () {

    final var conduit =
      circuit.conduit (
        pipe ( Integer.class )
      );

    final Tap < String > tap =
      conduit.tap (
        Object::toString
      );

    final List < String > results = new ArrayList <> ();

    tap.subscribe (
      circuit.subscriber (
        cortex.name ( "collector" ),
        ( _, registrar ) ->
          registrar.register ( results::add )
      )
    );

    final var name = cortex.name ( "test" );
    final var pipe = conduit.percept ( name );

    pipe.emit ( 1 );
    pipe.emit ( 2 );

    circuit.await ();

    tap.close ();

    pipe.emit ( 3 );
    pipe.emit ( 4 );

    circuit.await ();

    assertEquals ( 2, results.size () );
    assertEquals ( "1", results.get ( 0 ) );
    assertEquals ( "2", results.get ( 1 ) );

  }


  /// Tests that a tap can create a reservoir for its emissions.
  @Test
  void testTapReservoir () {

    final var conduit =
      circuit.conduit (
        pipe ( Integer.class )
      );

    final Tap < String > tap =
      conduit.tap (
        i -> "val:" + i
      );

    final var reservoir = tap.reservoir ();

    assertNotNull ( reservoir );

    final var name = cortex.name ( "test" );
    final var pipe = conduit.percept ( name );

    pipe.emit ( 10 );
    pipe.emit ( 20 );

    circuit.await ();

    final var captures = reservoir.drain ().toList ();

    assertEquals ( 2, captures.size () );
    assertEquals ( "val:10", captures.get ( 0 ).emission () );
    assertEquals ( "val:20", captures.get ( 1 ).emission () );

    reservoir.close ();
    tap.close ();

  }


  /// Tests that a tap with configurer correctly applies flow operations.
  ///
  /// Scenario:
  /// 1. Create a conduit emitting integers
  /// 2. Create a tap with diff() to deduplicate
  /// 3. Emit duplicate values
  /// 4. Verify only unique consecutive values are received
  @Test
  void testTapWithConfigurerDiff () {

    final var conduit =
      circuit.conduit (
        pipe ( Integer.class )
      );

    final Tap < String > tap =
      conduit.tap (
        i -> "v:" + i,
        Flow::diff
      );

    final List < String > results = new ArrayList <> ();

    tap.subscribe (
      circuit.subscriber (
        cortex.name ( "collector" ),
        ( _, registrar ) ->
          registrar.register ( results::add )
      )
    );

    final var name = cortex.name ( "test.channel" );
    final var pipe = conduit.percept ( name );

    pipe.emit ( 1 );
    pipe.emit ( 1 );  // duplicate - should be filtered
    pipe.emit ( 2 );
    pipe.emit ( 2 );  // duplicate - should be filtered
    pipe.emit ( 1 );  // not consecutive duplicate - should pass

    circuit.await ();

    assertEquals ( 3, results.size () );
    assertEquals ( "v:1", results.get ( 0 ) );
    assertEquals ( "v:2", results.get ( 1 ) );
    assertEquals ( "v:1", results.get ( 2 ) );

    tap.close ();

  }


  /// Tests that a tap with configurer correctly applies guard filtering.
  ///
  /// Scenario:
  /// 1. Create a conduit emitting integers
  /// 2. Create a tap with guard to filter values
  /// 3. Emit various values
  /// 4. Verify only values passing guard are received
  @Test
  void testTapWithConfigurerGuard () {

    final var conduit =
      circuit.conduit (
        pipe ( Integer.class )
      );

    final Tap < Integer > tap =
      conduit.tap (
        i -> i * 2,
        flow -> flow.guard ( v -> v > 5 )
      );

    final List < Integer > results = new ArrayList <> ();

    tap.subscribe (
      circuit.subscriber (
        cortex.name ( "collector" ),
        ( _, registrar ) ->
          registrar.register ( results::add )
      )
    );

    final var name = cortex.name ( "test.channel" );
    final var pipe = conduit.percept ( name );

    pipe.emit ( 1 );  // 2 - filtered out
    pipe.emit ( 2 );  // 4 - filtered out
    pipe.emit ( 3 );  // 6 - passes
    pipe.emit ( 4 );  // 8 - passes
    pipe.emit ( 5 );  // 10 - passes

    circuit.await ();

    assertEquals ( 3, results.size () );
    assertEquals ( 6, results.get ( 0 ) );
    assertEquals ( 8, results.get ( 1 ) );
    assertEquals ( 10, results.get ( 2 ) );

    tap.close ();

  }


  /// Tests that a tap with configurer correctly applies limit.
  ///
  /// Scenario:
  /// 1. Create a conduit emitting integers
  /// 2. Create a tap with limit(3)
  /// 3. Emit more values than limit
  /// 4. Verify only limited values are received
  @Test
  void testTapWithConfigurerLimit () {

    final var conduit =
      circuit.conduit (
        pipe ( Integer.class )
      );

    final Tap < String > tap =
      conduit.tap (
        Object::toString,
        flow -> flow.limit ( 3 )
      );

    final List < String > results = new ArrayList <> ();

    tap.subscribe (
      circuit.subscriber (
        cortex.name ( "collector" ),
        ( _, registrar ) ->
          registrar.register ( results::add )
      )
    );

    final var name = cortex.name ( "test.channel" );
    final var pipe = conduit.percept ( name );

    pipe.emit ( 1 );
    pipe.emit ( 2 );
    pipe.emit ( 3 );
    pipe.emit ( 4 );  // should be limited
    pipe.emit ( 5 );  // should be limited

    circuit.await ();

    assertEquals ( 3, results.size () );
    assertEquals ( "1", results.get ( 0 ) );
    assertEquals ( "2", results.get ( 1 ) );
    assertEquals ( "3", results.get ( 2 ) );

    tap.close ();

  }


  /// Tests that a mapper returning null filters out the emission.
  ///
  /// Scenario:
  /// 1. Create a conduit emitting integers
  /// 2. Create a tap with a mapper that returns null for even numbers
  /// 3. Emit a mix of odd and even values
  /// 4. Verify only odd values (non-null mapped) are received
  @Test
  void testMapperNullFilters () {

    final var conduit =
      circuit.conduit (
        pipe ( Integer.class )
      );

    final Tap < String > tap =
      conduit.tap (
        i -> i % 2 != 0
             ? "odd:" + i
             : null
      );

    final List < String > results = new ArrayList <> ();

    tap.subscribe (
      circuit.subscriber (
        cortex.name ( "collector" ),
        ( _, registrar ) ->
          registrar.register ( results::add )
      )
    );

    final var name = cortex.name ( "test.channel" );
    final var pipe = conduit.percept ( name );

    pipe.emit ( 1 );  // odd  → "odd:1"
    pipe.emit ( 2 );  // even → null (filtered)
    pipe.emit ( 3 );  // odd  → "odd:3"
    pipe.emit ( 4 );  // even → null (filtered)
    pipe.emit ( 5 );  // odd  → "odd:5"

    circuit.await ();

    assertEquals ( 3, results.size () );
    assertEquals ( "odd:1", results.get ( 0 ) );
    assertEquals ( "odd:3", results.get ( 1 ) );
    assertEquals ( "odd:5", results.get ( 2 ) );

    tap.close ();

  }


  /// Tests that subscribing with a null subscriber throws NullPointerException.
  @Test
  void testSubscribeNullThrows () {

    final var conduit =
      circuit.conduit (
        pipe ( Integer.class )
      );

    final Tap < String > tap =
      conduit.tap (
        Object::toString
      );

    assertThrows (
      NullPointerException.class,
      () -> tap.subscribe ( null )
    );

    tap.close ();

  }

}
