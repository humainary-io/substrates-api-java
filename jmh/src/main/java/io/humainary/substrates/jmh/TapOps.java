// Copyright (c) 2025 William David Louth

package io.humainary.substrates.jmh;

import io.humainary.substrates.api.Substrates;
import org.openjdk.jmh.annotations.*;

import static io.humainary.substrates.api.Substrates.Composer.pipe;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Level.Iteration;
import static org.openjdk.jmh.annotations.Level.Trial;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for Tap operations.
///
/// Measures performance of:
/// 1. Tap creation - conduit.tap(mapper) overhead
/// 2. Emission through tap - transformation overhead vs baseline conduit
/// 3. Multiple taps - fan-out through multiple transformations
///
/// Taps provide source-to-source transformation with structure preservation,
/// enabling type transformation (e.g., Serventis signals → Signetics names).
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class TapOps
  implements Substrates {

  private static final String NAME_STR   = "test";
  private static final int    VALUE      = 42;
  private static final int    BATCH_SIZE = 1000;

  private Cortex  cortex;
  private Name    name;
  private Circuit circuit;

  // Conduit for tap creation benchmarks (no subscribers attached)
  private Conduit < Pipe < Integer >, Integer > conduit;

  // Baseline: conduit with 1 direct subscriber (no tap)
  private Conduit < Pipe < Integer >, Integer > baselineConduit;
  private Pipe < Integer >                      baselinePipe;

  // Identity tap: conduit → tap → 1 subscriber
  private Conduit < Pipe < Integer >, Integer > identityConduit;
  private Tap < Integer >                       identityTap;
  private Pipe < Integer >                      identityTapPipe;

  // String tap: conduit → tap (with transform) → 1 subscriber
  private Conduit < Pipe < Integer >, Integer > stringConduit;
  private Tap < String >                        stringTap;
  private Pipe < Integer >                      stringTapPipe;

  // Multi-tap: conduit → 2 taps → 2 subscribers (fan-out)
  private Conduit < Pipe < Integer >, Integer > multiConduit;
  private Tap < Integer >                       multiTap1;
  private Tap < Integer >                       multiTap2;
  private Pipe < Integer >                      multiTapPipe;

  //
  // TAP CREATION BENCHMARKS
  //

  ///
  /// Tap creation with identity transformation.
  /// Measures baseline tap creation overhead without transformation cost.
  ///

  @Benchmark
  public Tap < Integer > tap_create_identity () {

    return
      conduit.tap (
        i -> i
      );

  }

  ///
  /// Tap creation with String transformation.
  /// Measures tap creation with typical transformation function.
  ///

  @Benchmark
  public Tap < String > tap_create_string () {

    return
      conduit.tap (
        Object::toString
      );

  }

  ///
  /// Batched tap creation - amortized overhead.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Tap < Integer > tap_create_batch () {

    Tap < Integer > result = null;

    for ( int i = 0; i < BATCH_SIZE; i++ ) {
      result = conduit.tap ( v -> v );
    }

    return result;

  }

  //
  // BASELINE EMISSION BENCHMARKS
  //

  ///
  /// Baseline emission through conduit without tap.
  /// Provides comparison baseline for tap overhead measurement.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void baseline_emit_batch_await () {

    for ( int i = 0; i < BATCH_SIZE; i++ ) {
      baselinePipe.emit ( VALUE + i );
    }

    circuit.await ();

  }

  //
  // TAPPED EMISSION BENCHMARKS
  //

  ///
  /// Emission through identity tap (minimal transformation).
  /// Measures tap routing overhead without transformation cost.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void tap_emit_identity_batch_await () {

    for ( int i = 0; i < BATCH_SIZE; i++ ) {
      identityTapPipe.emit ( VALUE + i );
    }

    circuit.await ();

  }

  ///
  /// Emission through String transformation tap.
  /// Measures tap overhead with allocating transformation.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void tap_emit_string_batch_await () {

    for ( int i = 0; i < BATCH_SIZE; i++ ) {
      stringTapPipe.emit ( VALUE + i );
    }

    circuit.await ();

  }

  ///
  /// Single emission through identity tap.
  ///

  @Benchmark
  public void tap_emit_identity_single () {

    identityTapPipe.emit ( VALUE );

  }

  ///
  /// Single emission through identity tap with await.
  ///

  @Benchmark
  public void tap_emit_identity_single_await () {

    identityTapPipe.emit ( VALUE );
    circuit.await ();

  }

  //
  // MULTI-TAP BENCHMARKS
  //

  ///
  /// Emission with multiple taps on same conduit.
  /// Measures fan-out overhead through 2 taps (2x baseline work).
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void tap_emit_multi_batch_await () {

    for ( int i = 0; i < BATCH_SIZE; i++ ) {
      multiTapPipe.emit ( VALUE + i );
    }

    circuit.await ();

  }

  //
  // TAP CLOSE BENCHMARKS
  //

  ///
  /// Tap close operation.
  /// Measures resource cleanup overhead.
  ///

  @Benchmark
  public void tap_close () {

    final var tap = conduit.tap ( i -> i );
    tap.close ();
    circuit.await ();

  }

  ///
  /// Tap lifecycle - create, use, close.
  /// Measures full tap lifecycle overhead.
  ///

  @Benchmark
  public void tap_lifecycle () {

    final var tap = conduit.tap ( Object::toString );

    tap.subscribe (
      circuit.subscriber (
        name,
        ( _, registrar ) ->
          registrar.register ( Receptor.of ( String.class ) )
      )
    );

    // Emit through conduit (which tap is subscribed to)
    conduit.percept ( name ).emit ( VALUE );
    circuit.await ();

    tap.close ();
    circuit.await ();

  }

  @Setup ( Iteration )
  public void setupIteration () {

    circuit =
      cortex.circuit (
        name
      );

    // Conduit for tap creation benchmarks (no subscribers)
    conduit =
      circuit.conduit (
        pipe ( Integer.class )
      );

    // ─────────────────────────────────────────────────────────────────
    // BASELINE: conduit → 1 direct subscriber (no tap in path)
    // This measures pure conduit emission overhead
    // ─────────────────────────────────────────────────────────────────
    baselineConduit =
      circuit.conduit (
        pipe ( Integer.class )
      );

    baselineConduit.subscribe (
      circuit.subscriber (
        cortex.name ( "baseline-sub" ),
        ( _, registrar ) ->
          registrar.register ( Receptor.of ( Integer.class ) )
      )
    );

    baselinePipe =
      baselineConduit.percept ( name );

    // ─────────────────────────────────────────────────────────────────
    // IDENTITY TAP: conduit → identity tap → 1 subscriber
    // Measures tap routing overhead (no transformation cost)
    // ─────────────────────────────────────────────────────────────────
    identityConduit =
      circuit.conduit (
        pipe ( Integer.class )
      );

    identityTap =
      identityConduit.tap ( i -> i );

    identityTap.subscribe (
      circuit.subscriber (
        cortex.name ( "identity-sub" ),
        ( _, registrar ) ->
          registrar.register ( Receptor.of ( Integer.class ) )
      )
    );

    identityTapPipe =
      identityConduit.percept ( name );

    // ─────────────────────────────────────────────────────────────────
    // STRING TAP: conduit → string tap → 1 subscriber
    // Measures tap overhead with allocating transformation
    // ─────────────────────────────────────────────────────────────────
    stringConduit =
      circuit.conduit (
        pipe ( Integer.class )
      );

    stringTap =
      stringConduit.tap ( Object::toString );

    stringTap.subscribe (
      circuit.subscriber (
        cortex.name ( "string-sub" ),
        ( _, registrar ) ->
          registrar.register ( Receptor.of ( String.class ) )
      )
    );

    stringTapPipe =
      stringConduit.percept ( name );

    // ─────────────────────────────────────────────────────────────────
    // MULTI-TAP: conduit → 2 identity taps → 2 subscribers
    // Measures fan-out overhead (2x baseline work expected)
    // ─────────────────────────────────────────────────────────────────
    multiConduit =
      circuit.conduit (
        pipe ( Integer.class )
      );

    multiTap1 =
      multiConduit.tap ( i -> i );

    multiTap1.subscribe (
      circuit.subscriber (
        cortex.name ( "multi-sub-1" ),
        ( _, registrar ) ->
          registrar.register ( Receptor.of ( Integer.class ) )
      )
    );

    multiTap2 =
      multiConduit.tap ( i -> i );

    multiTap2.subscribe (
      circuit.subscriber (
        cortex.name ( "multi-sub-2" ),
        ( _, registrar ) ->
          registrar.register ( Receptor.of ( Integer.class ) )
      )
    );

    multiTapPipe =
      multiConduit.percept ( name );

    // Warm up - ensure all subscriptions are registered
    circuit.await ();

  }

  @Setup ( Trial )
  public void setupTrial () {

    cortex =
      Substrates.cortex ();

    name =
      cortex.name (
        NAME_STR
      );

  }

  @TearDown ( Iteration )
  public void tearDownIteration () {

    identityTap.close ();
    stringTap.close ();
    multiTap1.close ();
    multiTap2.close ();
    circuit.close ();

  }

}
