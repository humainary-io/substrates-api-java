// Copyright (c) 2025 William David Louth

package io.humainary.serventis.jmh.opt.pool;

import io.humainary.substrates.api.Substrates;
import io.humainary.substrates.ext.serventis.opt.pool.Exchanges;
import io.humainary.substrates.ext.serventis.opt.pool.Exchanges.Exchange;
import io.humainary.substrates.ext.serventis.opt.pool.Exchanges.Signal;
import org.openjdk.jmh.annotations.*;

import static io.humainary.substrates.ext.serventis.opt.pool.Exchanges.Dimension.PROVIDER;
import static io.humainary.substrates.ext.serventis.opt.pool.Exchanges.Dimension.RECEIVER;
import static io.humainary.substrates.ext.serventis.opt.pool.Exchanges.Sign.CONTRACT;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.openjdk.jmh.annotations.Mode.AverageTime;

///
/// Benchmark for Exchanges.Exchange operations.
/// <p>
/// Measures performance of exchange signal emissions for bilateral
/// resource transfers: CONTRACT and TRANSFER with PROVIDER/RECEIVER dimensions.
///

@State ( Scope.Benchmark )
@BenchmarkMode ( AverageTime )
@OutputTimeUnit ( NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )

public class ExchangeOps implements Substrates {

  private static final String EXCHANGE_NAME = "trade.orders";
  private static final int    BATCH_SIZE    = 1000;

  private Cortex                       cortex;
  private Circuit                      circuit;
  private Conduit < Exchange, Signal > conduit;
  private Exchange                     exchange;
  private Name                         name;

  ///
  /// Benchmark emitting a CONTRACT signal with PROVIDER dimension.
  ///

  @Benchmark
  public void emit_contract_provider () {

    exchange.contract (
      PROVIDER
    );

  }

  ///
  /// Benchmark batched CONTRACT PROVIDER emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_contract_provider_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      exchange.contract (
        PROVIDER
      );
    }

  }

  ///
  /// Benchmark emitting a CONTRACT signal with RECEIVER dimension.
  ///

  @Benchmark
  public void emit_contract_receiver () {

    exchange.contract (
      RECEIVER
    );

  }

  ///
  /// Benchmark batched CONTRACT RECEIVER emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_contract_receiver_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      exchange.contract (
        RECEIVER
      );
    }

  }

  ///
  /// Benchmark full exchange pattern (contract + transfer for both sides).
  ///

  @Benchmark
  @OperationsPerInvocation ( 4 )
  public void emit_full_exchange () {

    exchange.contract ( PROVIDER );
    exchange.contract ( RECEIVER );
    exchange.transfer ( PROVIDER );
    exchange.transfer ( RECEIVER );

  }

  ///
  /// Benchmark batched full exchange patterns.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE * 4 )
  public void emit_full_exchange_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      exchange.contract ( PROVIDER );
      exchange.contract ( RECEIVER );
      exchange.transfer ( PROVIDER );
      exchange.transfer ( RECEIVER );
    }

  }

  ///
  /// Benchmark generic signal emission.
  ///

  @Benchmark
  public void emit_signal () {

    exchange.signal (
      CONTRACT,
      PROVIDER
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
      exchange.signal (
        CONTRACT,
        PROVIDER
      );
    }

  }

  ///
  /// Benchmark emitting a TRANSFER signal with PROVIDER dimension.
  ///

  @Benchmark
  public void emit_transfer_provider () {

    exchange.transfer (
      PROVIDER
    );

  }

  ///
  /// Benchmark batched TRANSFER PROVIDER emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_transfer_provider_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      exchange.transfer (
        PROVIDER
      );
    }

  }

  ///
  /// Benchmark emitting a TRANSFER signal with RECEIVER dimension.
  ///

  @Benchmark
  public void emit_transfer_receiver () {

    exchange.transfer (
      RECEIVER
    );

  }

  ///
  /// Benchmark batched TRANSFER RECEIVER emissions.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public void emit_transfer_receiver_batch () {

    for (
      var i = 0;
      i < BATCH_SIZE;
      i++
    ) {
      exchange.transfer (
        RECEIVER
      );
    }

  }

  ///
  /// Benchmark exchange retrieval from conduit.
  ///

  @Benchmark
  public Exchange exchange_from_conduit () {

    return
      conduit.percept (
        name
      );

  }

  ///
  /// Benchmark batched exchange retrieval from conduit.
  ///

  @Benchmark
  @OperationsPerInvocation ( BATCH_SIZE )
  public Exchange exchange_from_conduit_batch () {

    Exchange result = null;

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
        Exchanges::composer
      );

    exchange =
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
        EXCHANGE_NAME
      );

  }

  @TearDown ( Level.Iteration )
  public void tearDownIteration () {

    circuit.close ();

  }

}
