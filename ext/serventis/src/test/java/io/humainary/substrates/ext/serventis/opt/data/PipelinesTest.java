// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.opt.data;

import io.humainary.substrates.ext.serventis.opt.data.Pipelines.Pipeline;
import io.humainary.substrates.ext.serventis.opt.data.Pipelines.Sign;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.humainary.substrates.api.Substrates.*;
import static io.humainary.substrates.ext.serventis.opt.data.Pipelines.Sign.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


/// Tests for the [Pipelines] API.
///
/// @author William David Louth
/// @since 1.0

final class PipelinesTest {

  private static final Cortex             CORTEX = cortex ();
  private static final Name               NAME   = CORTEX.name ( "etl.customer.enrichment" );
  private              Circuit            circuit;
  private              Reservoir < Sign > reservoir;
  private              Pipeline           pipeline;

  @BeforeEach
  void setup () {

    circuit =
      CORTEX.circuit ();

    final var conduit =
      circuit.conduit (
        Pipelines::composer
      );

    pipeline =
      conduit.percept (
        NAME
      );

    reservoir =
      conduit.reservoir ();

  }


  @Test
  void testAggregate () {

    pipeline.aggregate ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( AGGREGATE, signs.getFirst () );

  }

  @Test
  void testBackpressure () {

    pipeline.backpressure ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( BACKPRESSURE, signs.getFirst () );

  }

  @Test
  void testBuffer () {

    pipeline.buffer ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( BUFFER, signs.getFirst () );

  }

  @Test
  void testCheckpoint () {

    pipeline.checkpoint ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( CHECKPOINT, signs.getFirst () );

  }

  @Test
  void testFilter () {

    pipeline.filter ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( FILTER, signs.getFirst () );

  }

  @Test
  void testInput () {

    pipeline.input ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( INPUT, signs.getFirst () );

  }

  @Test
  void testLag () {

    pipeline.lag ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( LAG, signs.getFirst () );

  }

  @Test
  void testOutput () {

    pipeline.output ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( OUTPUT, signs.getFirst () );

  }

  @Test
  void testOverflow () {

    pipeline.overflow ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( OVERFLOW, signs.getFirst () );

  }

  @Test
  void testSign () {

    // Test direct sign() method for all sign values
    pipeline.sign ( INPUT );
    pipeline.sign ( OUTPUT );
    pipeline.sign ( TRANSFORM );
    pipeline.sign ( FILTER );
    pipeline.sign ( AGGREGATE );
    pipeline.sign ( BUFFER );
    pipeline.sign ( BACKPRESSURE );
    pipeline.sign ( OVERFLOW );
    pipeline.sign ( CHECKPOINT );
    pipeline.sign ( WATERMARK );
    pipeline.sign ( LAG );
    pipeline.sign ( SKIP );

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 12, signs.size () );
    assertEquals ( INPUT, signs.get ( 0 ) );
    assertEquals ( OUTPUT, signs.get ( 1 ) );
    assertEquals ( TRANSFORM, signs.get ( 2 ) );
    assertEquals ( FILTER, signs.get ( 3 ) );
    assertEquals ( AGGREGATE, signs.get ( 4 ) );
    assertEquals ( BUFFER, signs.get ( 5 ) );
    assertEquals ( BACKPRESSURE, signs.get ( 6 ) );
    assertEquals ( OVERFLOW, signs.get ( 7 ) );
    assertEquals ( CHECKPOINT, signs.get ( 8 ) );
    assertEquals ( WATERMARK, signs.get ( 9 ) );
    assertEquals ( LAG, signs.get ( 10 ) );
    assertEquals ( SKIP, signs.get ( 11 ) );

  }

  @Test
  void testSignEnumOrdinals () {

    // Ensure ordinals remain stable for compatibility
    assertEquals ( 0, INPUT.ordinal () );
    assertEquals ( 1, OUTPUT.ordinal () );
    assertEquals ( 2, TRANSFORM.ordinal () );
    assertEquals ( 3, FILTER.ordinal () );
    assertEquals ( 4, AGGREGATE.ordinal () );
    assertEquals ( 5, BUFFER.ordinal () );
    assertEquals ( 6, BACKPRESSURE.ordinal () );
    assertEquals ( 7, OVERFLOW.ordinal () );
    assertEquals ( 8, CHECKPOINT.ordinal () );
    assertEquals ( 9, WATERMARK.ordinal () );
    assertEquals ( 10, LAG.ordinal () );
    assertEquals ( 11, SKIP.ordinal () );

  }

  @Test
  void testSignEnumValues () {

    final var values = Sign.values ();

    assertEquals ( 12, values.length );
    assertEquals ( INPUT, values[0] );
    assertEquals ( OUTPUT, values[1] );
    assertEquals ( TRANSFORM, values[2] );
    assertEquals ( FILTER, values[3] );
    assertEquals ( AGGREGATE, values[4] );
    assertEquals ( BUFFER, values[5] );
    assertEquals ( BACKPRESSURE, values[6] );
    assertEquals ( OVERFLOW, values[7] );
    assertEquals ( CHECKPOINT, values[8] );
    assertEquals ( WATERMARK, values[9] );
    assertEquals ( LAG, values[10] );
    assertEquals ( SKIP, values[11] );

  }

  @Test
  void testSimpleDataFlow () {

    // Normal data flow: input -> transform -> filter -> output
    pipeline.input ();
    pipeline.transform ();
    pipeline.filter ();
    pipeline.output ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 4, signs.size () );
    assertEquals ( INPUT, signs.get ( 0 ) );
    assertEquals ( TRANSFORM, signs.get ( 1 ) );
    assertEquals ( FILTER, signs.get ( 2 ) );
    assertEquals ( OUTPUT, signs.get ( 3 ) );

  }

  @Test
  void testSkip () {

    pipeline.skip ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( SKIP, signs.getFirst () );

  }

  @Test
  void testStreamProcessingWithBackpressure () {

    // Simulate stream processing with backpressure
    pipeline.input ();
    pipeline.buffer ();
    pipeline.transform ();
    pipeline.backpressure ();
    pipeline.buffer ();
    pipeline.transform ();
    pipeline.output ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 7, signs.size () );
    assertEquals ( INPUT, signs.get ( 0 ) );
    assertEquals ( BUFFER, signs.get ( 1 ) );
    assertEquals ( TRANSFORM, signs.get ( 2 ) );
    assertEquals ( BACKPRESSURE, signs.get ( 3 ) );
    assertEquals ( BUFFER, signs.get ( 4 ) );
    assertEquals ( TRANSFORM, signs.get ( 5 ) );
    assertEquals ( OUTPUT, signs.get ( 6 ) );

  }

  @Test
  void testStreamProcessingWithCheckpoints () {

    // Simulate stream processing with checkpoints and watermarks
    pipeline.input ();
    pipeline.transform ();
    pipeline.checkpoint ();
    pipeline.aggregate ();
    pipeline.watermark ();
    pipeline.output ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 6, signs.size () );
    assertEquals ( INPUT, signs.get ( 0 ) );
    assertEquals ( TRANSFORM, signs.get ( 1 ) );
    assertEquals ( CHECKPOINT, signs.get ( 2 ) );
    assertEquals ( AGGREGATE, signs.get ( 3 ) );
    assertEquals ( WATERMARK, signs.get ( 4 ) );
    assertEquals ( OUTPUT, signs.get ( 5 ) );

  }

  @Test
  void testStreamProcessingWithLag () {

    // Simulate stream processing with consumer lag
    pipeline.output ();
    pipeline.output ();
    pipeline.output ();
    pipeline.lag ();
    pipeline.input ();
    pipeline.lag ();
    pipeline.input ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 7, signs.size () );

    // Count different sign types
    final var outputs = signs.stream ().filter ( s -> s == OUTPUT ).count ();
    final var inputs = signs.stream ().filter ( s -> s == INPUT ).count ();
    final var lags = signs.stream ().filter ( s -> s == LAG ).count ();

    assertEquals ( 3, outputs );
    assertEquals ( 2, inputs );
    assertEquals ( 2, lags );

  }

  @Test
  void testStreamProcessingWithOverflow () {

    // Simulate stream processing with overflow condition
    pipeline.input ();
    pipeline.buffer ();
    pipeline.buffer ();
    pipeline.overflow ();
    pipeline.skip ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 5, signs.size () );
    assertEquals ( INPUT, signs.get ( 0 ) );
    assertEquals ( BUFFER, signs.get ( 1 ) );
    assertEquals ( BUFFER, signs.get ( 2 ) );
    assertEquals ( OVERFLOW, signs.get ( 3 ) );
    assertEquals ( SKIP, signs.get ( 4 ) );

  }

  @Test
  void testSubjectAttachment () {

    pipeline.output ();

    circuit.await ();

    final var capture =
      reservoir
        .drain ()
        .findFirst ()
        .orElseThrow ();

    assertEquals ( NAME, capture.subject ().name () );
    assertEquals ( OUTPUT, capture.emission () );

  }

  @Test
  void testTransform () {

    pipeline.transform ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( TRANSFORM, signs.getFirst () );

  }

  @Test
  void testWatermark () {

    pipeline.watermark ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 1, signs.size () );
    assertEquals ( WATERMARK, signs.getFirst () );

  }

  @Test
  void testWindowedAggregationFlow () {

    // Simulate windowed aggregation: input -> aggregate -> watermark -> checkpoint -> output
    pipeline.input ();
    pipeline.input ();
    pipeline.input ();
    pipeline.aggregate ();
    pipeline.watermark ();
    pipeline.checkpoint ();
    pipeline.output ();

    circuit.await ();

    final var signs =
      reservoir
        .drain ()
        .map ( Capture::emission )
        .toList ();

    assertEquals ( 7, signs.size () );
    assertEquals ( INPUT, signs.get ( 0 ) );
    assertEquals ( INPUT, signs.get ( 1 ) );
    assertEquals ( INPUT, signs.get ( 2 ) );
    assertEquals ( AGGREGATE, signs.get ( 3 ) );
    assertEquals ( WATERMARK, signs.get ( 4 ) );
    assertEquals ( CHECKPOINT, signs.get ( 5 ) );
    assertEquals ( OUTPUT, signs.get ( 6 ) );

  }

}