// Copyright (c) 2025 William David Louth

package io.humainary.serventis.jmh.opt.data;

import io.humainary.substrates.api.Substrates;
import io.humainary.substrates.ext.serventis.opt.data.Queues;
import io.humainary.substrates.ext.serventis.opt.data.Queues.Queue;
import io.humainary.substrates.ext.serventis.opt.data.Queues.Sign;
import org.openjdk.jmh.annotations.*;

import static io.humainary.substrates.ext.serventis.opt.data.Queues.Sign.ENQUEUE;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for Queues.Queue operations.
///
/// Measures performance of queue creation and sign emissions for queue
/// operations: ENQUEUE, DEQUEUE, OVERFLOW, and UNDERFLOW.
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class QueueOps implements Substrates {

  private static final String QUEUE_NAME = "worker.queue";
  private static final int    BATCH_SIZE = 1000;

  private Cortex                  cortex;
  private Circuit                 circuit;
  private Conduit < Queue, Sign > conduit;
  private Queue                   queue;
  private Name                    name;

  ///
  /// Benchmark emitting a DEQUEUE sign.
  ///

  @Benchmark
  public void emit_dequeue () {

    queue.dequeue ();

  }

  ///
  /// Benchmark batched DEQUEUE emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_dequeue_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      queue.dequeue ();
    }

  }

  ///
  /// Benchmark emitting an ENQUEUE sign.
  ///

  @Benchmark
  public void emit_enqueue () {

    queue.enqueue ();

  }

  ///
  /// Benchmark batched ENQUEUE emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_enqueue_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      queue.enqueue ();
    }

  }

  ///
  /// Benchmark emitting an OVERFLOW sign.
  ///

  @Benchmark
  public void emit_overflow () {

    queue.overflow ();

  }


  ///
  /// Benchmark batched OVERFLOW emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_overflow_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      queue.overflow ();
    }

  }

  ///
  /// Benchmark generic sign emission.
  ///

  @Benchmark
  public void emit_sign () {

    queue.sign (
      ENQUEUE
    );

  }

  ///
  /// Benchmark batched generic sign emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_sign_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      queue.sign (
        ENQUEUE
      );
    }

  }

  ///
  /// Benchmark emitting an UNDERFLOW sign.
  ///

  @Benchmark
  public void emit_underflow () {

    queue.underflow ();

  }

  ///
  /// Benchmark batched UNDERFLOW emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_underflow_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      queue.underflow ();
    }

  }

  ///
  /// Benchmark queue retrieval from conduit.
  ///

  @Benchmark
  public Queue queue_from_conduit () {

    return
      conduit.percept (
        name
      );

  }

  ///
  /// Benchmark batched queue retrieval from conduit.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Queue queue_from_conduit_batch () {

    Queue result = null;

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
        Queues::composer
      );

    queue =
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
        QUEUE_NAME
      );

  }

  @TearDown ( Level.Iteration )
  public void tearDownIteration () {

    circuit.close ();

  }

}
