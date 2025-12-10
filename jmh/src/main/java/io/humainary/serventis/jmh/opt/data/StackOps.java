// Copyright (c) 2025 William David Louth

package io.humainary.serventis.jmh.opt.data;

import io.humainary.substrates.api.Substrates;
import io.humainary.substrates.ext.serventis.opt.data.Stacks;
import io.humainary.substrates.ext.serventis.opt.data.Stacks.Sign;
import io.humainary.substrates.ext.serventis.opt.data.Stacks.Stack;
import org.openjdk.jmh.annotations.*;

import static io.humainary.substrates.ext.serventis.opt.data.Stacks.Sign.PUSH;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for Stacks.Stack operations.
///
/// Measures performance of stack creation and sign emissions for stack
/// operations (PUSH, POP) and boundary violations (OVERFLOW, UNDERFLOW).
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class StackOps implements Substrates {

  private static final String STACK_NAME = "parser.states";
  private static final int    BATCH_SIZE = 1000;

  private Cortex                  cortex;
  private Circuit                 circuit;
  private Conduit < Stack, Sign > conduit;
  private Stack                   stack;
  private Name                    name;

  ///
  /// Benchmark emitting an OVERFLOW sign.
  ///

  @Benchmark
  public void emit_overflow () {

    stack.overflow ();

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
      stack.overflow ();
    }

  }

  ///
  /// Benchmark emitting a POP sign.
  ///

  @Benchmark
  public void emit_pop () {

    stack.pop ();

  }

  ///
  /// Benchmark batched POP emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_pop_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      stack.pop ();
    }

  }

  ///
  /// Benchmark emitting a PUSH sign.
  ///

  @Benchmark
  public void emit_push () {

    stack.push ();

  }

  ///
  /// Benchmark batched PUSH emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_push_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      stack.push ();
    }

  }

  ///
  /// Benchmark generic sign emission.
  ///

  @Benchmark
  public void emit_sign () {

    stack.sign (
      PUSH
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
      stack.sign (
        PUSH
      );
    }

  }

  ///
  /// Benchmark emitting an UNDERFLOW sign.
  ///

  @Benchmark
  public void emit_underflow () {

    stack.underflow ();

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
      stack.underflow ();
    }

  }

  @Setup ( Level.Iteration )
  public void setupIteration () {

    circuit =
      cortex.circuit ();

    conduit =
      circuit.conduit (
        Stacks::composer
      );

    stack =
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
        STACK_NAME
      );

  }

  ///
  /// Benchmark stack retrieval from conduit.
  ///

  @Benchmark
  public Stack stack_from_conduit () {

    return
      conduit.percept (
        name
      );

  }

  ///
  /// Benchmark batched stack retrieval from conduit.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Stack stack_from_conduit_batch () {

    Stack result = null;

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

  @TearDown ( Level.Iteration )
  public void tearDownIteration () {

    circuit.close ();

  }

}
