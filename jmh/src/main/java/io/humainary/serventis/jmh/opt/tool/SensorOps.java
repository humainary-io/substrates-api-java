// Copyright (c) 2025 William David Louth

package io.humainary.serventis.jmh.opt.tool;

import io.humainary.substrates.api.Substrates;
import io.humainary.substrates.ext.serventis.opt.tool.Sensors;
import io.humainary.substrates.ext.serventis.opt.tool.Sensors.Sensor;
import io.humainary.substrates.ext.serventis.opt.tool.Sensors.Signal;
import org.openjdk.jmh.annotations.*;

import static io.humainary.substrates.ext.serventis.opt.tool.Sensors.Dimension.*;
import static io.humainary.substrates.ext.serventis.opt.tool.Sensors.Sign.ABOVE;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for Sensors.Sensor operations.
/// <p>
/// Measures performance of sensor creation and signal emissions with various
/// positional signs (BELOW, NOMINAL, ABOVE) and reference dimensions (BASELINE, THRESHOLD, TARGET).
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class SensorOps implements Substrates {

  private static final String SENSOR_NAME = "latency.p95";
  private static final int    BATCH_SIZE  = 1000;

  private Cortex                     cortex;
  private Circuit                    circuit;
  private Conduit < Sensor, Signal > conduit;
  private Sensor                     sensor;
  private Name                       name;

  ///
  /// Benchmark emitting an ABOVE signal with BASELINE dimension.
  ///

  @Benchmark
  public void emit_above_baseline () {

    sensor.above (
      BASELINE
    );

  }

  ///
  /// Benchmark batched ABOVE × BASELINE emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_above_baseline_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      sensor.above (
        BASELINE
      );
    }

  }

  ///
  /// Benchmark emitting an ABOVE signal with TARGET dimension.
  ///

  @Benchmark
  public void emit_above_target () {

    sensor.above (
      TARGET
    );

  }

  ///
  /// Benchmark batched ABOVE × TARGET emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_above_target_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      sensor.above (
        TARGET
      );
    }

  }

  ///
  /// Benchmark emitting an ABOVE signal with THRESHOLD dimension.
  ///

  @Benchmark
  public void emit_above_threshold () {

    sensor.above (
      THRESHOLD
    );

  }

  ///
  /// Benchmark batched ABOVE × THRESHOLD emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_above_threshold_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      sensor.above (
        THRESHOLD
      );
    }

  }

  ///
  /// Benchmark emitting a BELOW signal with BASELINE dimension.
  ///

  @Benchmark
  public void emit_below_baseline () {

    sensor.below (
      BASELINE
    );

  }

  ///
  /// Benchmark batched BELOW × BASELINE emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_below_baseline_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      sensor.below (
        BASELINE
      );
    }

  }

  ///
  /// Benchmark emitting a BELOW signal with TARGET dimension.
  ///

  @Benchmark
  public void emit_below_target () {

    sensor.below (
      TARGET
    );

  }

  ///
  /// Benchmark batched BELOW × TARGET emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_below_target_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      sensor.below (
        TARGET
      );
    }

  }

  ///
  /// Benchmark emitting a BELOW signal with THRESHOLD dimension.
  ///

  @Benchmark
  public void emit_below_threshold () {

    sensor.below (
      THRESHOLD
    );

  }

  ///
  /// Benchmark batched BELOW × THRESHOLD emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_below_threshold_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      sensor.below (
        THRESHOLD
      );
    }

  }

  ///
  /// Benchmark emitting a NOMINAL signal with BASELINE dimension.
  ///

  @Benchmark
  public void emit_nominal_baseline () {

    sensor.nominal (
      BASELINE
    );

  }

  ///
  /// Benchmark batched NOMINAL × BASELINE emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_nominal_baseline_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      sensor.nominal (
        BASELINE
      );
    }

  }

  ///
  /// Benchmark emitting a NOMINAL signal with TARGET dimension.
  ///

  @Benchmark
  public void emit_nominal_target () {

    sensor.nominal (
      TARGET
    );

  }

  ///
  /// Benchmark batched NOMINAL × TARGET emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_nominal_target_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      sensor.nominal (
        TARGET
      );
    }

  }

  ///
  /// Benchmark emitting a NOMINAL signal with THRESHOLD dimension.
  ///

  @Benchmark
  public void emit_nominal_threshold () {

    sensor.nominal (
      THRESHOLD
    );

  }

  ///
  /// Benchmark batched NOMINAL × THRESHOLD emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_nominal_threshold_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      sensor.nominal (
        THRESHOLD
      );
    }

  }

  ///
  /// Benchmark generic signal emission.
  ///

  @Benchmark
  public void emit_signal () {

    sensor.signal (
      ABOVE,
      BASELINE
    );

  }

  ///
  /// Benchmark batched generic signal emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_signal_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      sensor.signal (
        ABOVE,
        BASELINE
      );
    }

  }

  ///
  /// Benchmark setpoint retrieval from conduit.
  ///

  @Benchmark
  public Sensor sensor_from_conduit () {

    return
      conduit.percept (
        name
      );

  }

  ///
  /// Benchmark batched setpoint retrieval from conduit.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Sensor sensor_from_conduit_batch () {

    Sensor result = null;

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      result =
        conduit.percept (
          name
        );
    }

    return
      result;

  }

  @Setup ( Level.Iteration )
  public void setupIteration () {

    circuit =
      cortex.circuit ();

    conduit =
      circuit.conduit (
        Sensors::composer
      );

    sensor =
      conduit.percept (
        name
      );

    circuit.await ();

  }

  @Setup ( Level.Trial )
  public void setupTrial () {

    cortex =
      Substrates.cortex ();

    name =
      cortex.name (
        SENSOR_NAME
      );

  }

  @TearDown ( Level.Iteration )
  public void tearDownIteration () {

    circuit.close ();

  }

}
