# Substrates JMH Benchmarks

This document contains JMH benchmark results and documentation for the Substrates framework.

## Overview

The Substrates framework provides comprehensive JMH benchmarks measuring performance across all core
components. Benchmarks are organized into three categories:

1. **Core Substrates Benchmarks** - Framework primitives (Circuit, Conduit, Pipe, Flow, Name, State,
   Scope, Reservoir)
2. **Serventis Extension Benchmarks** - Observability instruments (Counter, Monitor, Gauge, Queue,
   Cache, Resource, Actor, Agent, Service, Router, Probe, Reporter, Transaction)
3. **Specialized Patterns** - Hot-path benchmarks and batched operations

## Running Benchmarks

```bash
# Run all benchmarks
jmh.sh

# List available benchmarks
jmh.sh -l

# Run specific benchmark
jmh.sh PipeOps.emit_to_empty_pipe

# Run benchmarks matching pattern
jmh.sh ".*batch"

# Custom JMH parameters
jmh.sh -wi 5 -i 10 -f 2
```

## Benchmark Categories

### Hot-Path Benchmarks

Hot-path benchmarks isolate operation costs from lifecycle overhead by using
`@Setup(Level.Iteration)` to reuse resources across invocations. These measure the performance of
already-running circuits without amortizing creation/teardown costs.

**Example**: `CircuitOps.hot_conduit_create()` measures conduit creation on an already-running
circuit.

### Batched Benchmarks

Batched benchmarks use `@OperationsPerInvocation(BATCH_SIZE)` to measure amortized per-operation
cost by executing operations in tight loops. This reduces measurement noise for fast operations (<
10ns) by spreading fixed benchmark overhead across many operations.

**Standard batch size**: 1000 operations per invocation

**Example**: `CounterOps.emit_increment_batch()` executes 1000 counter increments per benchmark
invocation.

### Single-Operation Benchmarks

Single-operation benchmarks measure the full cost of individual operations including any
per-invocation overhead. These provide baseline measurements for comparing against batched results.

## Benchmark Results

```

Humainary's (Alpha) SPI: io.humainary.substrates.spi.alpha.Provider 

Model Name: Mac mini
Model Identifier: Mac16,10
Model Number: MU9D3LL/A

Chip: Apple M4
Total Number of Cores: 10 (4 performance and 6 efficiency)
Memory: 16 GB

java version "25.0.1" 2025-10-21 LTS
Java(TM) SE Runtime Environment (build 25.0.1+8-LTS-27)
Java HotSpot(TM) 64-Bit Server VM (build 25.0.1+8-LTS-27, mixed mode, sharing)

Benchmark                                                                    Mode  Cnt      Score      Error  Units
i.h.serventis.jmh.opt.data.CacheOps.cache_from_conduit                       avgt    5      1.810 ±    0.059  ns/op
i.h.serventis.jmh.opt.data.CacheOps.cache_from_conduit_batch                 avgt    5      1.627 ±    0.019  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_evict                               avgt    5      9.455 ±    5.682  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_evict_batch                         avgt    5      7.925 ±    8.936  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_expire                              avgt    5      9.782 ±    0.732  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_expire_batch                        avgt    5      8.712 ±    5.068  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_hit                                 avgt    5      9.678 ±    6.011  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_hit_batch                           avgt    5     10.178 ±    0.568  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_lookup                              avgt    5     11.489 ±    0.989  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_lookup_batch                        avgt    5     10.496 ±    1.909  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_miss                                avgt    5      9.161 ±    3.655  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_miss_batch                          avgt    5     10.074 ±    0.864  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_remove                              avgt    5     12.822 ±    0.718  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_remove_batch                        avgt    5      8.472 ±    5.737  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_sign                                avgt    5      8.953 ±    2.967  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_sign_batch                          avgt    5      9.189 ±    3.386  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_store                               avgt    5     10.987 ±    1.109  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_store_batch                         avgt    5      8.788 ±    0.697  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_aggregate                        avgt    5      8.697 ±    4.457  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_aggregate_batch                  avgt    5      9.073 ±    3.977  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_backpressure                     avgt    5     10.769 ±    0.816  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_backpressure_batch               avgt    5      9.182 ±    0.514  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_buffer                           avgt    5      9.581 ±    1.020  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_buffer_batch                     avgt    5      8.478 ±    5.225  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_checkpoint                       avgt    5     10.764 ±    1.090  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_checkpoint_batch                 avgt    5      9.171 ±    0.691  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_filter                           avgt    5      8.547 ±    2.783  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_filter_batch                     avgt    5      8.446 ±    2.902  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_input                            avgt    5      8.936 ±    5.801  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_input_batch                      avgt    5     10.638 ±    0.762  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_lag                              avgt    5      9.093 ±    4.179  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_lag_batch                        avgt    5     10.816 ±    0.411  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_output                           avgt    5     11.116 ±    0.825  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_output_batch                     avgt    5      9.321 ±    0.638  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_overflow                         avgt    5      8.838 ±    3.871  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_overflow_batch                   avgt    5      8.931 ±    7.318  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_sign                             avgt    5      8.295 ±    2.849  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_sign_batch                       avgt    5      8.551 ±    6.229  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_skip                             avgt    5      8.424 ±    3.532  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_skip_batch                       avgt    5      8.385 ±    7.056  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_transform                        avgt    5      8.716 ±    1.342  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_transform_batch                  avgt    5      8.532 ±    8.752  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_watermark                        avgt    5      8.921 ±    5.647  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_watermark_batch                  avgt    5      8.812 ±    4.383  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_flow_etl                     avgt    5     47.284 ±   17.281  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_flow_stream                  avgt    5     51.533 ±    4.069  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_flow_windowed                avgt    5     48.256 ±   14.564  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_from_conduit                 avgt    5      1.872 ±    0.059  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_from_conduit_batch           avgt    5      1.661 ±    0.009  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_dequeue                             avgt    5      8.944 ±    3.256  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_dequeue_batch                       avgt    5      9.659 ±    0.636  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_enqueue                             avgt    5      9.437 ±    5.373  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_enqueue_batch                       avgt    5      9.365 ±    0.385  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_overflow                            avgt    5      9.384 ±    3.536  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_overflow_batch                      avgt    5     10.434 ±    1.480  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_sign                                avgt    5      9.738 ±    0.428  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_sign_batch                          avgt    5      8.802 ±    3.654  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_underflow                           avgt    5     10.821 ±    0.334  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_underflow_batch                     avgt    5      8.253 ±    6.142  ns/op
i.h.serventis.jmh.opt.data.QueueOps.queue_from_conduit                       avgt    5      1.862 ±    0.042  ns/op
i.h.serventis.jmh.opt.data.QueueOps.queue_from_conduit_batch                 avgt    5      1.661 ±    0.010  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_overflow                            avgt    5      9.077 ±    4.517  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_overflow_batch                      avgt    5      8.825 ±    3.269  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_pop                                 avgt    5      8.651 ±    3.533  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_pop_batch                           avgt    5      8.135 ±    6.909  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_push                                avgt    5      9.148 ±    1.083  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_push_batch                          avgt    5      8.582 ±    3.810  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_sign                                avgt    5     10.912 ±    0.699  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_sign_batch                          avgt    5      9.002 ±    3.711  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_underflow                           avgt    5      8.762 ±    4.287  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_underflow_batch                     avgt    5      8.739 ±    3.559  ns/op
i.h.serventis.jmh.opt.data.StackOps.stack_from_conduit                       avgt    5      1.865 ±    0.054  ns/op
i.h.serventis.jmh.opt.data.StackOps.stack_from_conduit_batch                 avgt    5      1.661 ±    0.008  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_crash                             avgt    5     10.733 ±    6.998  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_crash_batch                       avgt    5      7.805 ±    6.030  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_fail                              avgt    5      8.017 ±    3.685  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_fail_batch                        avgt    5      8.368 ±    5.142  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_kill                              avgt    5      9.214 ±    0.437  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_kill_batch                        avgt    5      8.109 ±    3.754  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_restart                           avgt    5      8.452 ±    3.807  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_restart_batch                     avgt    5     10.502 ±    0.552  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_resume                            avgt    5      9.127 ±    3.724  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_resume_batch                      avgt    5     10.276 ±    0.463  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_sign                              avgt    5      8.539 ±    3.570  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_sign_batch                        avgt    5      8.226 ±    4.485  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_spawn                             avgt    5      8.972 ±    0.728  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_spawn_batch                       avgt    5      8.893 ±    0.555  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_start                             avgt    5     10.862 ±    0.955  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_start_batch                       avgt    5      8.270 ±    7.149  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_stop                              avgt    5      8.791 ±    7.973  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_stop_batch                        avgt    5      8.328 ±    8.036  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_suspend                           avgt    5      8.658 ±    4.858  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_suspend_batch                     avgt    5      8.592 ±    5.523  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.process_from_conduit                   avgt    5      1.884 ±    0.087  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.process_from_conduit_batch             avgt    5      1.661 ±    0.010  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_call                              avgt    5      9.960 ±    0.717  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_call_batch                        avgt    5      9.758 ±    3.805  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_called                            avgt    5     10.761 ±    0.628  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_called_batch                      avgt    5      8.714 ±    4.291  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_delay                             avgt    5      8.148 ±    3.168  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_delay_batch                       avgt    5      9.142 ±    5.529  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_delayed                           avgt    5      9.040 ±    3.437  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_delayed_batch                     avgt    5      7.321 ±    9.216  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_discard                           avgt    5      8.306 ±    3.988  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_discard_batch                     avgt    5     10.522 ±    0.665  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_discarded                         avgt    5      9.733 ±    0.507  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_discarded_batch                   avgt    5      9.850 ±    2.573  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_disconnect                        avgt    5     10.656 ±    0.807  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_disconnect_batch                  avgt    5      9.530 ±    3.749  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_disconnected                      avgt    5      9.911 ±    0.800  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_disconnected_batch                avgt    5      8.709 ±    4.104  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_expire                            avgt    5      9.430 ±    0.609  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_expire_batch                      avgt    5     11.246 ±    0.946  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_expired                           avgt    5     10.909 ±    0.773  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_expired_batch                     avgt    5      8.497 ±    3.862  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_fail                              avgt    5      8.736 ±    5.064  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_fail_batch                        avgt    5      8.726 ±    4.270  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_failed                            avgt    5      9.953 ±    0.631  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_failed_batch                      avgt    5      8.522 ±    5.041  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_recourse                          avgt    5      8.698 ±    2.457  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_recourse_batch                    avgt    5     11.132 ±    1.410  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_recoursed                         avgt    5      9.189 ±    3.210  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_recoursed_batch                   avgt    5      9.947 ±    3.667  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_redirect                          avgt    5      9.126 ±    0.286  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_redirect_batch                    avgt    5      9.411 ±    0.740  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_redirected                        avgt    5     10.038 ±    0.406  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_redirected_batch                  avgt    5     10.896 ±    0.342  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_reject                            avgt    5      9.590 ±    0.855  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_reject_batch                      avgt    5     10.644 ±    2.484  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_rejected                          avgt    5      8.942 ±    4.913  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_rejected_batch                    avgt    5     10.233 ±    5.753  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_resume                            avgt    5      9.019 ±    3.799  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_resume_batch                      avgt    5      9.801 ±    0.918  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_resumed                           avgt    5      9.110 ±    3.284  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_resumed_batch                     avgt    5     10.273 ±    0.746  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_retried                           avgt    5      8.845 ±    3.341  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_retried_batch                     avgt    5     13.308 ±    0.863  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_retry                             avgt    5      8.907 ±    4.230  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_retry_batch                       avgt    5      8.186 ±    4.021  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_schedule                          avgt    5      8.534 ±    4.120  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_schedule_batch                    avgt    5     10.231 ±    5.758  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_scheduled                         avgt    5      8.780 ±    3.168  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_scheduled_batch                   avgt    5      8.433 ±    4.094  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_signal                            avgt    5      8.957 ±    2.507  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_signal_batch                      avgt    5      8.289 ±    4.200  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_start                             avgt    5      8.771 ±    4.228  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_start_batch                       avgt    5      8.777 ±    3.786  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_started                           avgt    5      9.046 ±    3.549  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_started_batch                     avgt    5     10.134 ±    0.684  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_stop                              avgt    5      9.484 ±    0.434  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_stop_batch                        avgt    5     10.577 ±    1.233  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_stopped                           avgt    5     10.473 ±    0.927  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_stopped_batch                     avgt    5     11.316 ±    0.392  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_succeeded                         avgt    5     10.109 ±    1.246  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_succeeded_batch                   avgt    5      9.822 ±    5.884  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_success                           avgt    5      9.052 ±    3.860  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_success_batch                     avgt    5      8.218 ±    3.108  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_suspend                           avgt    5      9.291 ±    0.774  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_suspend_batch                     avgt    5      9.671 ±    4.357  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_suspended                         avgt    5      8.682 ±    2.885  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_suspended_batch                   avgt    5      8.417 ±    3.667  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.service_from_conduit                   avgt    5      1.876 ±    0.055  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.service_from_conduit_batch             avgt    5      1.665 ±    0.011  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_cancel                               avgt    5      8.288 ±    3.716  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_cancel_batch                         avgt    5      8.236 ±    3.792  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_complete                             avgt    5      9.749 ±    0.178  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_complete_batch                       avgt    5      8.113 ±    3.315  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_fail                                 avgt    5     10.974 ±    1.306  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_fail_batch                           avgt    5      8.231 ±    6.808  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_progress                             avgt    5      8.712 ±    0.866  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_progress_batch                       avgt    5      9.507 ±    0.443  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_reject                               avgt    5      8.869 ±    3.826  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_reject_batch                         avgt    5     11.593 ±    1.681  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_resume                               avgt    5      8.701 ±    4.771  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_resume_batch                         avgt    5      8.233 ±    6.526  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_schedule                             avgt    5      8.143 ±    4.888  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_schedule_batch                       avgt    5      8.575 ±    4.817  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_sign                                 avgt    5      9.441 ±    0.621  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_sign_batch                           avgt    5      9.754 ±    0.403  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_start                                avgt    5      8.233 ±    3.158  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_start_batch                          avgt    5      8.482 ±    4.491  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_submit                               avgt    5      9.714 ±    0.620  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_submit_batch                         avgt    5      7.888 ±    9.046  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_suspend                              avgt    5     10.039 ±    1.713  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_suspend_batch                        avgt    5      8.214 ±    4.539  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_timeout                              avgt    5      8.620 ±    3.052  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_timeout_batch                        avgt    5     10.566 ±    1.593  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.task_from_conduit                         avgt    5      1.871 ±    0.067  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.task_from_conduit_batch                   avgt    5      1.663 ±    0.003  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_abort_coordinator             avgt    5      9.958 ±    0.472  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_abort_coordinator_batch       avgt    5      9.945 ±    2.406  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_abort_participant             avgt    5      8.864 ±    3.419  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_abort_participant_batch       avgt    5     10.761 ±    0.632  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_commit_coordinator            avgt    5      8.586 ±    4.883  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_commit_coordinator_batch      avgt    5      9.198 ±    9.197  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_commit_participant            avgt    5      8.803 ±    3.790  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_commit_participant_batch      avgt    5      8.790 ±    4.682  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_compensate_coordinator        avgt    5      8.628 ±    4.424  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_compensate_coordinator_batch  avgt    5      9.479 ±    5.131  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_compensate_participant        avgt    5      8.763 ±    3.617  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_compensate_participant_batch  avgt    5     10.293 ±    6.043  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_conflict_coordinator          avgt    5     10.711 ±    0.465  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_conflict_coordinator_batch    avgt    5     10.803 ±    0.462  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_conflict_participant          avgt    5      9.779 ±    0.571  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_conflict_participant_batch    avgt    5     10.318 ±    0.617  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_expire_coordinator            avgt    5      9.152 ±    1.473  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_expire_coordinator_batch      avgt    5     10.046 ±    3.673  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_expire_participant            avgt    5      9.352 ±    3.837  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_expire_participant_batch      avgt    5      9.268 ±    8.432  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_prepare_coordinator           avgt    5      8.836 ±    2.838  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_prepare_coordinator_batch     avgt    5      9.778 ±    4.400  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_prepare_participant           avgt    5      8.742 ±    3.398  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_prepare_participant_batch     avgt    5      8.669 ±    4.448  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_rollback_coordinator          avgt    5      8.376 ±    3.646  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_rollback_coordinator_batch    avgt    5     11.447 ±    1.169  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_rollback_participant          avgt    5      8.921 ±    4.788  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_rollback_participant_batch    avgt    5     10.190 ±    4.655  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_signal                        avgt    5      8.586 ±    3.611  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_signal_batch                  avgt    5      9.161 ±    3.155  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_start_coordinator             avgt    5      8.355 ±    3.276  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_start_coordinator_batch       avgt    5      9.311 ±    2.562  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_start_participant             avgt    5      9.267 ±    0.497  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_start_participant_batch       avgt    5      8.945 ±    3.765  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.transaction_from_conduit           avgt    5      1.877 ±    0.080  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.transaction_from_conduit_batch     avgt    5      1.660 ±    0.003  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.breaker_from_conduit                   avgt    5      1.871 ±    0.059  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.breaker_from_conduit_batch             avgt    5      1.661 ±    0.009  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_close                             avgt    5      8.842 ±    3.733  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_close_batch                       avgt    5      8.703 ±    7.422  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_half_open                         avgt    5      9.169 ±    5.422  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_half_open_batch                   avgt    5      9.591 ±    0.422  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_open                              avgt    5      9.792 ±    0.793  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_open_batch                        avgt    5      7.818 ±    6.797  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_probe                             avgt    5      9.648 ±    0.583  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_probe_batch                       avgt    5     10.351 ±    2.952  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_reset                             avgt    5     10.155 ±    0.871  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_reset_batch                       avgt    5      8.882 ±    0.642  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_sign                              avgt    5      8.570 ±    3.958  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_sign_batch                        avgt    5     10.652 ±    0.562  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_trip                              avgt    5      8.460 ±    4.829  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_trip_batch                        avgt    5      7.755 ±    8.367  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_corrupt                            avgt    5      9.211 ±    4.052  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_corrupt_batch                      avgt    5      7.871 ±    2.920  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_drop                               avgt    5      8.303 ±    2.807  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_drop_batch                         avgt    5      7.887 ±    6.055  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_forward                            avgt    5      8.440 ±    4.814  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_forward_batch                      avgt    5      8.563 ±    3.913  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_fragment                           avgt    5      9.035 ±    5.464  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_fragment_batch                     avgt    5     10.235 ±    0.645  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_reassemble                         avgt    5      8.992 ±    4.370  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_reassemble_batch                   avgt    5      8.048 ±    4.853  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_receive                            avgt    5      8.815 ±    5.350  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_receive_batch                      avgt    5     10.473 ±    0.659  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_reorder                            avgt    5     11.534 ±    0.644  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_reorder_batch                      avgt    5     10.637 ±    1.152  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_route                              avgt    5      8.706 ±    5.633  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_route_batch                        avgt    5      7.895 ±    5.060  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_send                               avgt    5     10.327 ±    0.665  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_send_batch                         avgt    5     10.189 ±    0.570  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_sign                               avgt    5      9.007 ±    6.082  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_sign_batch                         avgt    5      8.437 ±    7.820  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.router_from_conduit                     avgt    5      1.866 ±    0.044  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.router_from_conduit_batch               avgt    5      1.661 ±    0.011  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_contract                            avgt    5      8.992 ±    4.183  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_contract_batch                      avgt    5      9.690 ±    0.810  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_deny                                avgt    5      8.734 ±    5.356  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_deny_batch                          avgt    5     10.566 ±    0.498  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_drain                               avgt    5      9.225 ±    4.769  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_drain_batch                         avgt    5      8.156 ±    3.593  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_drop                                avgt    5      8.355 ±    2.659  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_drop_batch                          avgt    5      8.582 ±    4.005  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_expand                              avgt    5     10.664 ±    0.836  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_expand_batch                        avgt    5      8.799 ±    3.680  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_pass                                avgt    5      8.847 ±    3.106  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_pass_batch                          avgt    5      8.613 ±    3.863  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_sign                                avgt    5      8.409 ±    4.658  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_sign_batch                          avgt    5      9.517 ±    5.163  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.valve_from_conduit                       avgt    5      1.863 ±    0.051  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.valve_from_conduit_batch                 avgt    5      1.660 ±    0.010  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_contract_provider                avgt    5      9.116 ±    0.754  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_contract_provider_batch          avgt    5      9.734 ±    4.167  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_contract_receiver                avgt    5      8.847 ±    2.008  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_contract_receiver_batch          avgt    5     11.275 ±    0.976  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_full_exchange                    avgt    5     11.010 ±    1.120  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_full_exchange_batch              avgt    5     10.612 ±    1.130  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_signal                           avgt    5      8.845 ±    3.344  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_signal_batch                     avgt    5      9.232 ±    5.838  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_transfer_provider                avgt    5     10.285 ±    0.369  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_transfer_provider_batch          avgt    5      9.459 ±    4.618  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_transfer_receiver                avgt    5      8.779 ±    4.204  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_transfer_receiver_batch          avgt    5      8.376 ±    4.749  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.exchange_from_conduit                 avgt    5      1.864 ±    0.069  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.exchange_from_conduit_batch           avgt    5      1.660 ±    0.009  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_acquire                             avgt    5      9.788 ±    0.707  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_acquire_batch                       avgt    5      9.324 ±    0.509  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_acquired                            avgt    5      8.292 ±    4.101  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_acquired_batch                      avgt    5      9.119 ±    4.079  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_denied                              avgt    5      8.950 ±    3.405  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_denied_batch                        avgt    5     10.423 ±    1.199  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_deny                                avgt    5      9.830 ±    0.383  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_deny_batch                          avgt    5      9.552 ±    0.502  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_expire                              avgt    5      9.813 ±    0.549  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_expire_batch                        avgt    5     10.043 ±    0.424  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_expired                             avgt    5      8.839 ±    3.458  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_expired_batch                       avgt    5     10.792 ±    1.194  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_extend                              avgt    5      8.478 ±    4.308  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_extend_batch                        avgt    5      9.414 ±    3.009  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_extended                            avgt    5      8.724 ±    2.633  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_extended_batch                      avgt    5      9.611 ±    3.273  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_grant                               avgt    5      8.373 ±    3.366  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_grant_batch                         avgt    5      9.616 ±    6.367  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_granted                             avgt    5      9.436 ±    4.620  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_granted_batch                       avgt    5      7.438 ±    9.363  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_probe                               avgt    5     10.097 ±    0.679  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_probe_batch                         avgt    5     10.301 ±    1.203  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_probed                              avgt    5     10.446 ±    1.148  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_probed_batch                        avgt    5     10.825 ±    0.618  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_release                             avgt    5      8.642 ±    2.676  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_release_batch                       avgt    5      9.524 ±    0.371  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_released                            avgt    5      9.045 ±    0.911  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_released_batch                      avgt    5     10.107 ±    4.707  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_renew                               avgt    5      9.827 ±    0.887  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_renew_batch                         avgt    5      8.964 ±    3.530  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_renewed                             avgt    5      8.915 ±    3.761  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_renewed_batch                       avgt    5      9.923 ±    3.201  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_revoke                              avgt    5      8.930 ±    5.462  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_revoke_batch                        avgt    5     10.783 ±    0.819  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_revoked                             avgt    5     10.120 ±    0.429  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_revoked_batch                       avgt    5      8.633 ±    0.427  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_signal                              avgt    5      9.220 ±    5.346  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_signal_batch                        avgt    5      9.989 ±    5.426  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.lease_from_conduit                       avgt    5      1.861 ±    0.019  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.lease_from_conduit_batch                 avgt    5      1.660 ±    0.005  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_borrow                               avgt    5      9.802 ±    0.986  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_borrow_batch                         avgt    5      9.512 ±    0.695  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_contract                             avgt    5      9.887 ±    0.741  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_contract_batch                       avgt    5      9.259 ±    0.800  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_expand                               avgt    5      9.359 ±    5.455  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_expand_batch                         avgt    5      8.218 ±    4.220  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_reclaim                              avgt    5      9.835 ±    0.307  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_reclaim_batch                        avgt    5      9.239 ±    0.138  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_sign                                 avgt    5      8.627 ±    2.787  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_sign_batch                           avgt    5     11.031 ±   26.133  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.pool_from_conduit                         avgt    5      1.861 ±    0.068  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.pool_from_conduit_batch                   avgt    5      1.659 ±    0.006  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_acquire                          avgt    5      8.792 ±    4.042  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_acquire_batch                    avgt    5      9.556 ±    0.744  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_attempt                          avgt    5      8.898 ±    2.747  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_attempt_batch                    avgt    5      9.990 ±    1.484  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_deny                             avgt    5      9.445 ±    0.488  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_deny_batch                       avgt    5      8.410 ±    4.296  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_grant                            avgt    5     10.913 ±    0.980  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_grant_batch                      avgt    5     11.139 ±    0.800  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_release                          avgt    5     11.642 ±    1.437  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_release_batch                    avgt    5      7.996 ±    6.026  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_sign                             avgt    5      9.096 ±    5.222  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_sign_batch                       avgt    5      8.031 ±    7.100  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_timeout                          avgt    5      8.906 ±    6.442  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_timeout_batch                    avgt    5      9.793 ±    1.460  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.resource_from_conduit                 avgt    5      1.872 ±    0.070  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.resource_from_conduit_batch           avgt    5      1.659 ±    0.004  ns/op
i.h.serventis.jmh.opt.role.ActorOps.actor_from_conduit                       avgt    5      1.864 ±    0.054  ns/op
i.h.serventis.jmh.opt.role.ActorOps.actor_from_conduit_batch                 avgt    5      1.660 ±    0.012  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_acknowledge                         avgt    5      9.484 ±    1.046  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_acknowledge_batch                   avgt    5      9.667 ±    0.345  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_affirm                              avgt    5      9.181 ±    1.974  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_affirm_batch                        avgt    5      8.298 ±    8.191  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_ask                                 avgt    5      8.691 ±    4.880  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_ask_batch                           avgt    5     10.281 ±    0.474  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_clarify                             avgt    5     11.402 ±    0.999  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_clarify_batch                       avgt    5      8.142 ±    7.957  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_command                             avgt    5      9.731 ±    0.505  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_command_batch                       avgt    5     10.167 ±    0.653  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_deliver                             avgt    5      8.843 ±    4.532  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_deliver_batch                       avgt    5      8.754 ±    7.097  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_deny                                avgt    5     10.676 ±    0.448  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_deny_batch                          avgt    5     10.555 ±    0.312  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_explain                             avgt    5      8.980 ±    0.554  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_explain_batch                       avgt    5      8.349 ±    6.551  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_promise                             avgt    5      8.249 ±    3.050  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_promise_batch                       avgt    5     10.888 ±   10.606  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_report                              avgt    5      8.868 ±    6.528  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_report_batch                        avgt    5      8.103 ±    3.545  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_request                             avgt    5     11.800 ±    0.900  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_request_batch                       avgt    5      8.318 ±    8.959  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_sign                                avgt    5      9.802 ±    0.596  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_sign_batch                          avgt    5      8.188 ±    3.897  ns/op
i.h.serventis.jmh.opt.role.AgentOps.agent_from_conduit                       avgt    5      1.866 ±    0.066  ns/op
i.h.serventis.jmh.opt.role.AgentOps.agent_from_conduit_batch                 avgt    5      1.665 ±    0.012  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_accept                              avgt    5     10.370 ±    0.704  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_accept_batch                        avgt    5      8.772 ±    4.356  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_accepted                            avgt    5      8.752 ±    4.987  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_accepted_batch                      avgt    5      9.522 ±    4.099  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_breach                              avgt    5     11.652 ±    0.925  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_breach_batch                        avgt    5      9.565 ±    0.980  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_breached                            avgt    5      9.758 ±    0.860  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_breached_batch                      avgt    5      9.028 ±    1.080  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_depend                              avgt    5      9.498 ±    0.430  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_depend_batch                        avgt    5      9.088 ±    5.031  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_depended                            avgt    5      9.055 ±    3.569  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_depended_batch                      avgt    5      9.453 ±    4.032  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_fulfill                             avgt    5      9.559 ±    0.685  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_fulfill_batch                       avgt    5     10.198 ±    1.391  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_fulfilled                           avgt    5      8.845 ±    3.187  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_fulfilled_batch                     avgt    5     10.628 ±    1.482  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_inquire                             avgt    5      8.518 ±    2.258  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_inquire_batch                       avgt    5     10.324 ±    0.941  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_inquired                            avgt    5     10.397 ±    0.764  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_inquired_batch                      avgt    5      9.439 ±    3.765  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_observe                             avgt    5      8.830 ±    3.355  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_observe_batch                       avgt    5      7.645 ±    5.660  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_observed                            avgt    5      8.984 ±    3.889  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_observed_batch                      avgt    5     10.895 ±    0.591  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_offer                               avgt    5      8.830 ±    3.287  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_offer_batch                         avgt    5      9.598 ±    1.925  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_offered                             avgt    5      8.877 ±    4.111  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_offered_batch                       avgt    5     10.726 ±    0.517  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_promise                             avgt    5      9.910 ±    0.790  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_promise_batch                       avgt    5     11.250 ±    0.900  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_promised                            avgt    5      9.440 ±    0.732  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_promised_batch                      avgt    5     11.488 ±    1.646  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_retract                             avgt    5      9.620 ±    0.812  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_retract_batch                       avgt    5     10.452 ±    0.605  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_retracted                           avgt    5      8.903 ±    2.775  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_retracted_batch                     avgt    5     10.344 ±    5.547  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_signal                              avgt    5     10.350 ±    0.722  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_signal_batch                        avgt    5     10.155 ±    2.629  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_validate                            avgt    5      8.957 ±    3.217  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_validate_batch                      avgt    5      9.691 ±    3.250  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_validated                           avgt    5      8.437 ±    1.805  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_validated_batch                     avgt    5      9.741 ±    2.947  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.atomic_from_conduit                     avgt    5      1.858 ±    0.015  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.atomic_from_conduit_batch               avgt    5      1.661 ±    0.009  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_attempt                            avgt    5      8.798 ±    5.456  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_attempt_batch                      avgt    5     10.591 ±    1.022  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_backoff                            avgt    5      8.637 ±    4.235  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_backoff_batch                      avgt    5     10.713 ±    0.877  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_exhaust                            avgt    5      8.630 ±    2.827  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_exhaust_batch                      avgt    5      8.478 ±    7.218  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_fail                               avgt    5      9.113 ±    4.679  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_fail_batch                         avgt    5      8.177 ±    3.326  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_park                               avgt    5     10.961 ±    1.062  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_park_batch                         avgt    5      8.693 ±    7.126  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_sign                               avgt    5      9.084 ±    5.708  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_sign_batch                         avgt    5     10.182 ±    0.807  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_spin                               avgt    5      8.712 ±    5.271  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_spin_batch                         avgt    5      8.569 ±    6.745  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_success                            avgt    5      9.951 ±    0.672  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_success_batch                      avgt    5      8.350 ±    3.359  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_yield                              avgt    5      8.678 ±    3.860  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_yield_batch                        avgt    5      8.333 ±    6.728  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_abandon                             avgt    5      8.970 ±    3.221  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_abandon_batch                       avgt    5     10.335 ±    0.436  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_arrive                              avgt    5      9.075 ±    5.005  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_arrive_batch                        avgt    5      8.427 ±    0.480  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_await                               avgt    5      8.297 ±    2.483  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_await_batch                         avgt    5      9.004 ±    1.164  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_release                             avgt    5     10.594 ±    0.369  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_release_batch                       avgt    5      8.515 ±    6.798  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_reset                               avgt    5     10.379 ±    0.646  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_reset_batch                         avgt    5      9.678 ±    0.129  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_sign                                avgt    5      8.752 ±    3.006  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_sign_batch                          avgt    5      8.320 ±    7.089  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_timeout                             avgt    5      8.979 ±    3.317  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_timeout_batch                       avgt    5      9.351 ±    0.561  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.latch_from_conduit                       avgt    5      1.864 ±    0.043  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.latch_from_conduit_batch                 avgt    5      1.659 ±    0.002  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_abandon                              avgt    5      9.032 ±    0.493  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_abandon_batch                        avgt    5      8.600 ±    3.902  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_acquire                              avgt    5      8.505 ±    4.119  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_acquire_batch                        avgt    5     10.654 ±    0.423  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_attempt                              avgt    5      8.592 ±    4.158  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_attempt_batch                        avgt    5      8.147 ±    5.476  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_contest                              avgt    5     11.271 ±    0.934  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_contest_batch                        avgt    5      9.257 ±    0.969  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_deny                                 avgt    5      8.604 ±    4.942  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_deny_batch                           avgt    5      8.593 ±    8.010  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_downgrade                            avgt    5      8.698 ±    3.881  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_downgrade_batch                      avgt    5      8.342 ±    7.893  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_grant                                avgt    5      8.663 ±    4.638  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_grant_batch                          avgt    5     10.100 ±    0.830  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_release                              avgt    5      9.505 ±    1.117  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_release_batch                        avgt    5      8.285 ±    3.972  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_sign                                 avgt    5      8.734 ±    3.763  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_sign_batch                           avgt    5      8.312 ±    3.252  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_timeout                              avgt    5      8.549 ±    3.351  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_timeout_batch                        avgt    5      7.969 ±    4.873  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_upgrade                              avgt    5     11.864 ±    1.151  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_upgrade_batch                        avgt    5      8.496 ±    3.179  ns/op
i.h.serventis.jmh.opt.sync.LockOps.lock_from_conduit                         avgt    5      1.862 ±    0.063  ns/op
i.h.serventis.jmh.opt.sync.LockOps.lock_from_conduit_batch                   avgt    5      1.661 ±    0.004  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.counter_from_conduit                   avgt    5      1.879 ±    0.058  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.counter_from_conduit_batch             avgt    5      1.661 ±    0.011  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_increment                         avgt    5      9.970 ±    0.573  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_increment_batch                   avgt    5      8.506 ±    0.605  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_overflow                          avgt    5      8.433 ±    3.609  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_overflow_batch                    avgt    5      9.095 ±    1.065  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_reset                             avgt    5      9.172 ±    5.584  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_reset_batch                       avgt    5     10.743 ±    0.316  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_sign                              avgt    5      9.910 ±    0.633  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_sign_batch                        avgt    5     10.794 ±    0.766  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_decrement                           avgt    5      8.321 ±    5.156  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_decrement_batch                     avgt    5      8.733 ±    0.582  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_increment                           avgt    5      8.582 ±    4.425  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_increment_batch                     avgt    5      8.473 ±    0.607  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_overflow                            avgt    5     10.947 ±    0.882  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_overflow_batch                      avgt    5      7.830 ±    9.433  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_reset                               avgt    5     10.710 ±    0.316  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_reset_batch                         avgt    5      8.836 ±    1.236  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_sign                                avgt    5      9.852 ±    0.556  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_sign_batch                          avgt    5      8.749 ±    5.771  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_underflow                           avgt    5      8.530 ±    5.271  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_underflow_batch                     avgt    5     10.478 ±    0.734  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.gauge_from_conduit                       avgt    5      1.868 ±    0.069  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.gauge_from_conduit_batch                 avgt    5      1.661 ±    0.008  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_debug                                 avgt    5      8.790 ±    5.621  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_debug_batch                           avgt    5      8.193 ±    7.448  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_info                                  avgt    5      8.098 ±    3.692  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_info_batch                            avgt    5      9.534 ±    0.678  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_severe                                avgt    5      8.132 ±    2.878  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_severe_batch                          avgt    5      8.059 ±    2.510  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_sign                                  avgt    5      9.280 ±    4.887  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_sign_batch                            avgt    5      8.171 ±    0.498  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_warning                               avgt    5      8.343 ±    4.405  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_warning_batch                         avgt    5      8.145 ±    7.137  ns/op
i.h.serventis.jmh.opt.tool.LogOps.log_from_conduit                           avgt    5      1.874 ±    0.085  ns/op
i.h.serventis.jmh.opt.tool.LogOps.log_from_conduit_batch                     avgt    5      1.659 ±    0.002  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_connect                             avgt    5     10.475 ±    0.432  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_connect_batch                       avgt    5      8.457 ±    3.780  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_connected                           avgt    5     10.252 ±    1.282  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_connected_batch                     avgt    5     10.327 ±    1.591  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_disconnect                          avgt    5      8.975 ±    2.750  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_disconnect_batch                    avgt    5     10.748 ±    0.554  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_disconnected                        avgt    5     10.286 ±    0.585  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_disconnected_batch                  avgt    5      9.610 ±    3.043  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_fail                                avgt    5      9.769 ±    2.309  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_fail_batch                          avgt    5      9.631 ±    3.573  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_failed                              avgt    5     10.277 ±    1.274  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_failed_batch                        avgt    5     10.470 ±    0.715  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_process                             avgt    5     10.695 ±    0.640  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_process_batch                       avgt    5     10.323 ±    5.351  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_processed                           avgt    5     10.724 ±    0.470  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_processed_batch                     avgt    5      9.682 ±    4.375  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_receive_batch                       avgt    5     10.722 ±    1.406  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_received_batch                      avgt    5      8.862 ±    2.871  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_signal                              avgt    5      9.691 ±    0.353  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_signal_batch                        avgt    5     11.416 ±    1.333  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_succeed                             avgt    5     11.657 ±    1.458  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_succeed_batch                       avgt    5      9.796 ±    4.010  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_succeeded                           avgt    5      9.884 ±    0.505  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_succeeded_batch                     avgt    5     11.829 ±    1.059  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transfer                            avgt    5     10.667 ±    0.468  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transfer_inbound                    avgt    5     10.498 ±    0.496  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transfer_outbound                   avgt    5     10.666 ±    0.560  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transferred                         avgt    5     10.405 ±    1.142  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transmit_batch                      avgt    5      8.272 ±    3.593  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transmitted_batch                   avgt    5      8.862 ±    3.869  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.probe_from_conduit                       avgt    5      1.868 ±    0.055  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.probe_from_conduit_batch                 avgt    5      1.660 ±    0.001  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_baseline                     avgt    5     10.502 ±    0.725  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_baseline_batch               avgt    5      9.780 ±    0.802  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_target                       avgt    5     10.297 ±    0.535  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_target_batch                 avgt    5      8.832 ±    4.447  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_threshold                    avgt    5      8.782 ±    3.082  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_threshold_batch              avgt    5      8.609 ±    4.358  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_baseline                     avgt    5      8.645 ±    3.613  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_baseline_batch               avgt    5     10.414 ±    1.055  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_target                       avgt    5      8.579 ±    2.923  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_target_batch                 avgt    5     10.573 ±    0.755  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_threshold                    avgt    5      9.655 ±    0.905  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_threshold_batch              avgt    5      9.759 ±    3.572  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_baseline                   avgt    5      8.485 ±    3.883  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_baseline_batch             avgt    5      9.367 ±    0.688  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_target                     avgt    5      8.861 ±    2.763  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_target_batch               avgt    5      9.515 ±    4.170  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_threshold                  avgt    5      8.850 ±    1.728  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_threshold_batch            avgt    5     11.320 ±    1.656  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_signal                             avgt    5      9.685 ±    0.197  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_signal_batch                       avgt    5     11.092 ±    1.633  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.sensor_from_conduit                     avgt    5      1.870 ±    0.077  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.sensor_from_conduit_batch               avgt    5      1.659 ±    0.002  ns/op
i.h.serventis.jmh.sdk.OperationOps.emit_begin                                avgt    5      9.393 ±    5.273  ns/op
i.h.serventis.jmh.sdk.OperationOps.emit_begin_batch                          avgt    5      8.030 ±    5.516  ns/op
i.h.serventis.jmh.sdk.OperationOps.emit_end                                  avgt    5     11.058 ±    0.865  ns/op
i.h.serventis.jmh.sdk.OperationOps.emit_end_batch                            avgt    5      8.383 ±    3.676  ns/op
i.h.serventis.jmh.sdk.OperationOps.emit_sign                                 avgt    5      9.207 ±    5.995  ns/op
i.h.serventis.jmh.sdk.OperationOps.emit_sign_batch                           avgt    5      9.472 ±    0.725  ns/op
i.h.serventis.jmh.sdk.OperationOps.operation_from_conduit                    avgt    5      1.869 ±    0.069  ns/op
i.h.serventis.jmh.sdk.OperationOps.operation_from_conduit_batch              avgt    5      1.660 ±    0.003  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.emit_fail                                   avgt    5      8.821 ±    4.624  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.emit_fail_batch                             avgt    5      9.587 ±    1.167  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.emit_sign                                   avgt    5      8.641 ±    4.444  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.emit_sign_batch                             avgt    5     10.979 ±    0.966  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.emit_success                                avgt    5      8.909 ±    5.671  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.emit_success_batch                          avgt    5      8.794 ±    5.984  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.outcome_from_conduit                        avgt    5      1.865 ±    0.043  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.outcome_from_conduit_batch                  avgt    5      1.659 ±    0.003  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_mixed_pattern                         avgt    5      0.225 ±    0.001  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_single                                avgt    5      0.753 ±    0.022  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_single_batch                          avgt    5      0.019 ±    0.001  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_varied_batch                          avgt    5      1.514 ±    0.001  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_worst_case                            avgt    5      1.182 ±    0.002  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_critical                             avgt    5      9.798 ±    0.783  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_critical_batch                       avgt    5      9.696 ±    3.118  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_normal                               avgt    5      8.786 ±    4.667  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_normal_batch                         avgt    5      9.615 ±    3.714  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_signal                               avgt    5     10.354 ±    0.717  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_signal_batch                         avgt    5      8.411 ±    4.140  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_warning                              avgt    5      8.831 ±    3.103  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_warning_batch                        avgt    5      9.475 ±    3.231  ns/op
i.h.serventis.jmh.sdk.SituationOps.situation_from_conduit                    avgt    5      1.868 ±    0.067  ns/op
i.h.serventis.jmh.sdk.SituationOps.situation_from_conduit_batch              avgt    5      1.659 ±    0.004  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_converging_confirmed                    avgt    5      9.136 ±    6.371  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_converging_confirmed_batch              avgt    5      8.824 ±    5.725  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_defective_tentative                     avgt    5      9.751 ±    0.493  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_defective_tentative_batch               avgt    5      8.846 ±    3.346  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_degraded_measured                       avgt    5      9.085 ±    4.166  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_degraded_measured_batch                 avgt    5      7.108 ±    6.417  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_down_confirmed                          avgt    5     10.632 ±    0.894  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_down_confirmed_batch                    avgt    5      9.611 ±    0.693  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_signal                                  avgt    5      8.882 ±    2.312  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_signal_batch                            avgt    5     10.218 ±    1.289  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_stable_confirmed                        avgt    5     10.495 ±    0.798  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_stable_confirmed_batch                  avgt    5     10.940 ±    0.419  ns/op
i.h.serventis.jmh.sdk.StatusOps.status_from_conduit                          avgt    5      1.872 ±    0.079  ns/op
i.h.serventis.jmh.sdk.StatusOps.status_from_conduit_batch                    avgt    5      1.660 ±    0.010  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_alarm_flow                              avgt    5     10.304 ±    0.463  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_alarm_flow_batch                        avgt    5      9.016 ±    3.555  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_fault_link                              avgt    5      8.861 ±    2.490  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_fault_link_batch                        avgt    5      8.347 ±    3.500  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_limit_time                              avgt    5     10.391 ±    0.646  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_limit_time_batch                        avgt    5      9.492 ±    0.427  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_normal_space                            avgt    5      8.647 ±    3.116  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_normal_space_batch                      avgt    5      9.651 ±    2.192  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_signal                                  avgt    5      8.888 ±    5.817  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_signal_batch                            avgt    5      9.725 ±    2.647  ns/op
i.h.serventis.jmh.sdk.SystemOps.system_from_conduit                          avgt    5      1.869 ±    0.046  ns/op
i.h.serventis.jmh.sdk.SystemOps.system_from_conduit_batch                    avgt    5      1.660 ±    0.004  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_chaos                                    avgt    5      8.980 ±    4.834  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_chaos_batch                              avgt    5      9.543 ±    0.617  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_cycle                                    avgt    5      9.182 ±    5.181  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_cycle_batch                              avgt    5     10.429 ±    0.817  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_drift                                    avgt    5     10.535 ±    0.198  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_drift_batch                              avgt    5      8.262 ±    3.771  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_sign                                     avgt    5     10.238 ±    0.931  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_sign_batch                               avgt    5      8.480 ±    6.486  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_spike                                    avgt    5      8.901 ±    3.495  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_spike_batch                              avgt    5      8.095 ±    7.707  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_stable                                   avgt    5     11.197 ±    0.617  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_stable_batch                             avgt    5      7.577 ±    3.489  ns/op
i.h.serventis.jmh.sdk.TrendOps.trend_from_conduit                            avgt    5      1.863 ±    0.047  ns/op
i.h.serventis.jmh.sdk.TrendOps.trend_from_conduit_batch                      avgt    5      1.660 ±    0.003  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.cycle_from_conduit                       avgt    5      1.868 ±    0.074  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.cycle_from_conduit_batch                 avgt    5      1.660 ±    0.003  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_repeat                              avgt    5      8.897 ±    3.711  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_repeat_batch                        avgt    5      8.994 ±    2.964  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_return                              avgt    5      8.735 ±    3.896  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_return_batch                        avgt    5     10.232 ±    0.567  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_signal                              avgt    5      9.536 ±    0.761  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_signal_batch                        avgt    5      8.165 ±    4.311  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_single                              avgt    5      8.373 ±    2.583  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_single_batch                        avgt    5      8.351 ±    3.963  ns/op
i.h.substrates.jmh.CircuitOps.conduit_create_close                           avgt    5    250.367 ±  108.186  ns/op
i.h.substrates.jmh.CircuitOps.conduit_create_named                           avgt    5    285.204 ±  161.736  ns/op
i.h.substrates.jmh.CircuitOps.conduit_create_with_flow                       avgt    5    275.006 ±  130.464  ns/op
i.h.substrates.jmh.CircuitOps.create_and_close                               avgt    5    514.392 ±  980.440  ns/op
i.h.substrates.jmh.CircuitOps.create_await_close                             avgt    5  10183.889 ±  452.697  ns/op
i.h.substrates.jmh.CircuitOps.hot_await_queue_drain                          avgt    5   5321.201 ±  333.199  ns/op
i.h.substrates.jmh.CircuitOps.hot_conduit_create                             avgt    5     19.057 ±    0.093  ns/op
i.h.substrates.jmh.CircuitOps.hot_conduit_create_named                       avgt    5     19.048 ±    0.093  ns/op
i.h.substrates.jmh.CircuitOps.hot_conduit_create_with_flow                   avgt    5     21.865 ±    0.067  ns/op
i.h.substrates.jmh.CircuitOps.hot_pipe_async                                 avgt    5      8.628 ±    1.060  ns/op
i.h.substrates.jmh.CircuitOps.hot_pipe_async_with_flow                       avgt    5     11.422 ±    2.845  ns/op
i.h.substrates.jmh.CircuitOps.pipe_async                                     avgt    5    296.342 ±  241.822  ns/op
i.h.substrates.jmh.CircuitOps.pipe_async_with_flow                           avgt    5    288.477 ±  159.160  ns/op
i.h.substrates.jmh.ConduitOps.get_by_name                                    avgt    5      1.886 ±    0.030  ns/op
i.h.substrates.jmh.ConduitOps.get_by_name_batch                              avgt    5      1.657 ±    0.002  ns/op
i.h.substrates.jmh.ConduitOps.get_by_substrate                               avgt    5      2.041 ±    0.019  ns/op
i.h.substrates.jmh.ConduitOps.get_by_substrate_batch                         avgt    5      1.807 ±    0.003  ns/op
i.h.substrates.jmh.ConduitOps.get_cached                                     avgt    5      3.494 ±    0.199  ns/op
i.h.substrates.jmh.ConduitOps.get_cached_batch                               avgt    5      3.301 ±    0.010  ns/op
i.h.substrates.jmh.ConduitOps.subscribe                                      avgt    5    516.919 ±  375.422  ns/op
i.h.substrates.jmh.ConduitOps.subscribe_batch                                avgt    5    480.143 ±  491.924  ns/op
i.h.substrates.jmh.ConduitOps.subscribe_with_emission_await                  avgt    5   6961.594 ±  716.620  ns/op
i.h.substrates.jmh.CortexOps.circuit                                         avgt    5    268.616 ±  120.273  ns/op
i.h.substrates.jmh.CortexOps.circuit_batch                                   avgt    5    291.761 ±  286.998  ns/op
i.h.substrates.jmh.CortexOps.circuit_named                                   avgt    5    267.499 ±  197.495  ns/op
i.h.substrates.jmh.CortexOps.current                                         avgt    5      1.091 ±    0.028  ns/op
i.h.substrates.jmh.CortexOps.name_class                                      avgt    5      1.478 ±    0.026  ns/op
i.h.substrates.jmh.CortexOps.name_enum                                       avgt    5      2.834 ±    0.019  ns/op
i.h.substrates.jmh.CortexOps.name_iterable                                   avgt    5     11.254 ±    0.553  ns/op
i.h.substrates.jmh.CortexOps.name_path                                       avgt    5      1.893 ±    0.005  ns/op
i.h.substrates.jmh.CortexOps.name_path_batch                                 avgt    5      1.676 ±    0.001  ns/op
i.h.substrates.jmh.CortexOps.name_string                                     avgt    5      2.860 ±    0.041  ns/op
i.h.substrates.jmh.CortexOps.name_string_batch                               avgt    5      2.603 ±    0.002  ns/op
i.h.substrates.jmh.CortexOps.scope                                           avgt    5     10.134 ±    3.359  ns/op
i.h.substrates.jmh.CortexOps.scope_batch                                     avgt    5      7.647 ±    1.156  ns/op
i.h.substrates.jmh.CortexOps.scope_named                                     avgt    5      8.003 ±    0.051  ns/op
i.h.substrates.jmh.CortexOps.slot_boolean                                    avgt    5      2.392 ±    0.327  ns/op
i.h.substrates.jmh.CortexOps.slot_double                                     avgt    5      2.425 ±    0.023  ns/op
i.h.substrates.jmh.CortexOps.slot_int                                        avgt    5      2.266 ±    0.549  ns/op
i.h.substrates.jmh.CortexOps.slot_long                                       avgt    5      2.384 ±    0.368  ns/op
i.h.substrates.jmh.CortexOps.slot_string                                     avgt    5      2.390 ±    0.141  ns/op
i.h.substrates.jmh.CortexOps.state_empty                                     avgt    5      0.441 ±    0.002  ns/op
i.h.substrates.jmh.CortexOps.state_empty_batch                               avgt    5     ≈ 10⁻³             ns/op
i.h.substrates.jmh.FlowOps.baseline_no_flow_await                            avgt    5     17.679 ±    0.431  ns/op
i.h.substrates.jmh.FlowOps.flow_combined_diff_guard_await                    avgt    5     30.449 ±    1.493  ns/op
i.h.substrates.jmh.FlowOps.flow_combined_diff_sample_await                   avgt    5     19.475 ±    0.563  ns/op
i.h.substrates.jmh.FlowOps.flow_combined_guard_limit_await                   avgt    5     27.241 ±    0.287  ns/op
i.h.substrates.jmh.FlowOps.flow_diff_await                                   avgt    5     30.079 ±    0.836  ns/op
i.h.substrates.jmh.FlowOps.flow_guard_await                                  avgt    5     30.307 ±    2.389  ns/op
i.h.substrates.jmh.FlowOps.flow_limit_await                                  avgt    5     29.299 ±    2.854  ns/op
i.h.substrates.jmh.FlowOps.flow_sample_await                                 avgt    5     17.179 ±    0.435  ns/op
i.h.substrates.jmh.FlowOps.flow_sift_await                                   avgt    5     18.208 ±    0.269  ns/op
i.h.substrates.jmh.NameOps.name_chained_deep                                 avgt    5     16.950 ±    0.158  ns/op
i.h.substrates.jmh.NameOps.name_chaining                                     avgt    5      8.895 ±    0.474  ns/op
i.h.substrates.jmh.NameOps.name_chaining_batch                               avgt    5     10.238 ±    0.261  ns/op
i.h.substrates.jmh.NameOps.name_compare                                      avgt    5     33.509 ±    0.524  ns/op
i.h.substrates.jmh.NameOps.name_compare_batch                                avgt    5     32.897 ±    2.847  ns/op
i.h.substrates.jmh.NameOps.name_depth                                        avgt    5      1.713 ±    0.138  ns/op
i.h.substrates.jmh.NameOps.name_depth_batch                                  avgt    5      1.399 ±    0.442  ns/op
i.h.substrates.jmh.NameOps.name_enclosure                                    avgt    5      0.609 ±    0.168  ns/op
i.h.substrates.jmh.NameOps.name_from_enum                                    avgt    5      2.843 ±    0.029  ns/op
i.h.substrates.jmh.NameOps.name_from_iterable                                avgt    5     11.445 ±    0.010  ns/op
i.h.substrates.jmh.NameOps.name_from_iterator                                avgt    5     12.963 ±    0.067  ns/op
i.h.substrates.jmh.NameOps.name_from_mapped_iterable                         avgt    5     11.696 ±    0.004  ns/op
i.h.substrates.jmh.NameOps.name_from_name                                    avgt    5      4.219 ±    0.009  ns/op
i.h.substrates.jmh.NameOps.name_from_string                                  avgt    5      3.005 ±    0.499  ns/op
i.h.substrates.jmh.NameOps.name_from_string_batch                            avgt    5      2.811 ±    0.027  ns/op
i.h.substrates.jmh.NameOps.name_interning_chained                            avgt    5     12.271 ±    0.017  ns/op
i.h.substrates.jmh.NameOps.name_interning_same_path                          avgt    5      3.551 ±    0.015  ns/op
i.h.substrates.jmh.NameOps.name_interning_segments                           avgt    5      8.669 ±    0.627  ns/op
i.h.substrates.jmh.NameOps.name_iterate_hierarchy                            avgt    5      1.803 ±    0.007  ns/op
i.h.substrates.jmh.NameOps.name_parsing                                      avgt    5      1.882 ±    0.002  ns/op
i.h.substrates.jmh.NameOps.name_parsing_batch                                avgt    5      1.684 ±    0.001  ns/op
i.h.substrates.jmh.NameOps.name_path_generation                              avgt    5     36.429 ±    4.283  ns/op
i.h.substrates.jmh.NameOps.name_path_generation_batch                        avgt    5     31.249 ±    5.240  ns/op
i.h.substrates.jmh.PipeOps.async_emit_batch                                  avgt    5     13.007 ±    7.594  ns/op
i.h.substrates.jmh.PipeOps.async_emit_batch_await                            avgt    5     16.506 ±    1.490  ns/op
i.h.substrates.jmh.PipeOps.async_emit_chained_await                          avgt    5     18.014 ±    0.788  ns/op
i.h.substrates.jmh.PipeOps.async_emit_fanout_await                           avgt    5     19.836 ±    0.156  ns/op
i.h.substrates.jmh.PipeOps.async_emit_single                                 avgt    5     10.921 ±    0.850  ns/op
i.h.substrates.jmh.PipeOps.async_emit_single_await                           avgt    5   7886.519 ±  228.888  ns/op
i.h.substrates.jmh.PipeOps.async_emit_with_flow_await                        avgt    5     20.818 ±    0.939  ns/op
i.h.substrates.jmh.PipeOps.baseline_blackhole                                avgt    5      0.267 ±    0.002  ns/op
i.h.substrates.jmh.PipeOps.baseline_counter                                  avgt    5      1.632 ±    0.020  ns/op
i.h.substrates.jmh.PipeOps.baseline_receptor                                 avgt    5      0.264 ±    0.001  ns/op
i.h.substrates.jmh.PipeOps.pipe_create                                       avgt    5      8.961 ±    0.871  ns/op
i.h.substrates.jmh.PipeOps.pipe_create_chained                               avgt    5      0.844 ±    0.001  ns/op
i.h.substrates.jmh.PipeOps.pipe_create_with_flow                             avgt    5     12.710 ±    1.701  ns/op
i.h.substrates.jmh.ReservoirOps.baseline_emit_no_reservoir_await             avgt    5     94.775 ±    3.903  ns/op
i.h.substrates.jmh.ReservoirOps.baseline_emit_no_reservoir_await_batch       avgt    5     18.963 ±    1.120  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_burst_then_drain_await             avgt    5     88.622 ±   12.491  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_burst_then_drain_await_batch       avgt    5     28.694 ±    0.218  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_drain_await                        avgt    5     93.382 ±    7.405  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_drain_await_batch                  avgt    5     28.206 ±    0.284  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_emit_drain_cycles_await            avgt    5    346.526 ±   57.172  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_emit_with_capture_await            avgt    5     74.655 ±    4.811  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_emit_with_capture_await_batch      avgt    5     23.655 ±    0.247  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_process_emissions_await            avgt    5     89.711 ±    6.595  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_process_emissions_await_batch      avgt    5     26.482 ±    0.269  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_process_subjects_await             avgt    5     94.493 ±    2.711  ns/op
i.h.substrates.jmh.ScopeOps.scope_child_anonymous                            avgt    5     17.491 ±    2.055  ns/op
i.h.substrates.jmh.ScopeOps.scope_child_anonymous_batch                      avgt    5     16.694 ±    0.280  ns/op
i.h.substrates.jmh.ScopeOps.scope_child_named                                avgt    5     17.079 ±    0.109  ns/op
i.h.substrates.jmh.ScopeOps.scope_child_named_batch                          avgt    5     16.973 ±    0.337  ns/op
i.h.substrates.jmh.ScopeOps.scope_close_idempotent                           avgt    5      2.401 ±    0.038  ns/op
i.h.substrates.jmh.ScopeOps.scope_close_idempotent_batch                     avgt    5      0.033 ±    0.001  ns/op
i.h.substrates.jmh.ScopeOps.scope_closure                                    avgt    5    286.665 ±   92.518  ns/op
i.h.substrates.jmh.ScopeOps.scope_closure_batch                              avgt    5    287.085 ±   56.792  ns/op
i.h.substrates.jmh.ScopeOps.scope_complex                                    avgt    5    933.091 ±  135.964  ns/op
i.h.substrates.jmh.ScopeOps.scope_create_and_close                           avgt    5      2.439 ±    0.045  ns/op
i.h.substrates.jmh.ScopeOps.scope_create_and_close_batch                     avgt    5      0.034 ±    0.001  ns/op
i.h.substrates.jmh.ScopeOps.scope_create_named                               avgt    5      2.452 ±    0.027  ns/op
i.h.substrates.jmh.ScopeOps.scope_create_named_batch                         avgt    5      0.033 ±    0.001  ns/op
i.h.substrates.jmh.ScopeOps.scope_hierarchy                                  avgt    5     29.306 ±    5.578  ns/op
i.h.substrates.jmh.ScopeOps.scope_hierarchy_batch                            avgt    5     26.644 ±    0.066  ns/op
i.h.substrates.jmh.ScopeOps.scope_parent_closes_children                     avgt    5     42.944 ±    0.114  ns/op
i.h.substrates.jmh.ScopeOps.scope_parent_closes_children_batch               avgt    5     42.494 ±    4.097  ns/op
i.h.substrates.jmh.ScopeOps.scope_register_multiple                          avgt    5   1352.932 ±  155.513  ns/op
i.h.substrates.jmh.ScopeOps.scope_register_multiple_batch                    avgt    5   1449.264 ±  287.111  ns/op
i.h.substrates.jmh.ScopeOps.scope_register_single                            avgt    5    292.645 ±   40.303  ns/op
i.h.substrates.jmh.ScopeOps.scope_register_single_batch                      avgt    5    284.105 ±  100.704  ns/op
i.h.substrates.jmh.ScopeOps.scope_with_resources                             avgt    5    597.903 ±  126.396  ns/op
i.h.substrates.jmh.StateOps.slot_name                                        avgt    5      0.523 ±    0.001  ns/op
i.h.substrates.jmh.StateOps.slot_name_batch                                  avgt    5      0.001 ±    0.001  ns/op
i.h.substrates.jmh.StateOps.slot_type                                        avgt    5      0.454 ±    0.090  ns/op
i.h.substrates.jmh.StateOps.slot_value                                       avgt    5      0.639 ±    0.001  ns/op
i.h.substrates.jmh.StateOps.slot_value_batch                                 avgt    5      0.001 ±    0.001  ns/op
i.h.substrates.jmh.StateOps.state_compact                                    avgt    5     10.360 ±    0.169  ns/op
i.h.substrates.jmh.StateOps.state_compact_batch                              avgt    5     10.627 ±    0.154  ns/op
i.h.substrates.jmh.StateOps.state_iterate_slots                              avgt    5      2.156 ±    0.002  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_int                               avgt    5      4.570 ±    1.972  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_int_batch                         avgt    5      4.788 ±    1.021  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_long                              avgt    5      4.807 ±    0.274  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_object                            avgt    5      2.634 ±    0.309  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_object_batch                      avgt    5      2.421 ±    0.474  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_string                            avgt    5      4.515 ±    1.503  ns/op
i.h.substrates.jmh.StateOps.state_value_read                                 avgt    5      1.486 ±    0.001  ns/op
i.h.substrates.jmh.StateOps.state_value_read_batch                           avgt    5      1.266 ±    0.001  ns/op
i.h.substrates.jmh.StateOps.state_values_stream                              avgt    5      4.881 ±    0.552  ns/op
i.h.substrates.jmh.SubscriberOps.close_five_conduits_await                   avgt    5   8663.907 ± 1075.539  ns/op
i.h.substrates.jmh.SubscriberOps.close_five_subscriptions_await              avgt    5   8538.895 ±  498.564  ns/op
i.h.substrates.jmh.SubscriberOps.close_idempotent_await                      avgt    5   8094.628 ±  591.833  ns/op
i.h.substrates.jmh.SubscriberOps.close_idempotent_batch_await                avgt    5     16.443 ±    0.547  ns/op
i.h.substrates.jmh.SubscriberOps.close_no_subscriptions_await                avgt    5   8308.769 ±  336.507  ns/op
i.h.substrates.jmh.SubscriberOps.close_no_subscriptions_batch_await          avgt    5     14.170 ±    2.063  ns/op
i.h.substrates.jmh.SubscriberOps.close_one_subscription_await                avgt    5   8223.209 ±  368.506  ns/op
i.h.substrates.jmh.SubscriberOps.close_one_subscription_batch_await          avgt    5     33.751 ±    0.384  ns/op
i.h.substrates.jmh.SubscriberOps.close_ten_conduits_await                    avgt    5   8431.901 ±  287.422  ns/op
i.h.substrates.jmh.SubscriberOps.close_ten_subscriptions_await               avgt    5   8325.894 ±  126.763  ns/op
i.h.substrates.jmh.SubscriberOps.close_with_pending_emissions_await          avgt    5   8461.022 ±  648.959  ns/op
```

## Benchmark Implementation Details

### CircuitOps - Hot-Path Benchmarks

CircuitOps includes specialized hot-path benchmarks that isolate operation costs from circuit
lifecycle overhead:

- `hot_conduit_create()` - Create conduit on already-running circuit
- `hot_conduit_create_named()` - Create named conduit on running circuit
- `hot_conduit_with_flow()` - Create conduit with flow operators on running circuit
- `hot_pipe_async()` - Create async pipe on running circuit
- `hot_pipe_async_with_flow()` - Create async pipe with flows on running circuit

These use `@Setup(Level.Iteration)` to create a circuit once per iteration, then measure operations
without including circuit creation/teardown costs.

**Usage**: Compare against regular benchmarks to understand lifecycle overhead vs. operation cost.

### Batched Benchmarks Standard

All batched benchmarks follow a consistent pattern with `BATCH_SIZE = 1000`:

```java

@Benchmark
@OperationsPerInvocation(BATCH_SIZE)
public ReturnType operation_batch() {
    ReturnType result = null;
    for (var i = 0; i < BATCH_SIZE; i++) {
        result = operation();
    }
    return result;  // Prevent dead-code elimination
}
```

**Files with batched benchmarks**:

**Substrates Core**:

- `CircuitOps` - Circuit creation (6 batched benchmarks)
- `ConduitOps` - Channel pooling (4 batched benchmarks)
- `CortexOps` - Factory methods (7 batched benchmarks)
- `NameOps` - Name operations (6 batched benchmarks)
- `StateOps` - State/slot operations (6 batched benchmarks)
- `ReservoirOps` - Emission capture (5 batched benchmarks)
- `ScopeOps` - Lifecycle management (10 batched benchmarks)

**Serventis Extensions**:

- `CounterOps` - Counter signals (4 batched benchmarks)
- `MonitorOps` - Monitor signals (6 batched benchmarks)
- `GaugeOps` - Gauge signals (6 batched benchmarks)
- `QueueOps` - Queue signals (5 batched benchmarks)
- `CacheOps` - Cache signals (8 batched benchmarks)
- `ResourceOps` - Resource signals (7 batched benchmarks)
- `ActorOps` - Speech act signals (12 batched benchmarks)
- `AgentOps` - Promise theory signals (21 batched benchmarks)
- `ServiceOps` - Service lifecycle signals (27 batched benchmarks)
- `RouterOps` - Packet routing signals (10 batched benchmarks)
- `ProbeOps` - Communication signals (15 batched benchmarks)
- `ReporterOps` - Reporter signals (4 batched benchmarks)
- `TransactionOps` - Transaction coordination signals (18 batched benchmarks)

### Interpreting Results

#### Hot-Path vs. Cold-Path

Compare hot-path and cold-path benchmarks to understand lifecycle costs:

```
CircuitOps.create_and_close:        526 ns/op  (includes creation + close)
CircuitOps.hot_conduit_create:      21 ns/op   (operation only, no lifecycle)
```

This shows that circuit creation/teardown dominates total cost, while the actual conduit creation
operation is extremely fast on a running circuit.

#### Batched vs. Single-Operation

Batched benchmarks provide more stable measurements for fast operations:

```
NameOps.name_from_string:           3 ns/op    (single operation)
NameOps.name_from_string_batch:     3 ns/op    (amortized over 1000 ops)
```

The batched version provides more stable measurements by amortizing JMH framework overhead across
many operations, though the actual operation cost remains consistent at ~3 ns/op.

#### Emission Latency vs. Round-Trip Time

**CRITICAL DISTINCTION**: In production, emissions are asynchronous - the caller doesn't wait for
processing. The `await()` call is primarily for testing/benchmarking synchronization and is rarely
used in production.

**Emission Cost** (Production Hot Path):

- **Empty pipe**: ~0.4 ns/op (PipeOps.emit_to_empty_pipe) - Fastest, discards value
- **Receptor pipe**: ~0.6 ns/op (PipeOps.emit_to_receptor_pipe) - Synchronous callback
- **Transform pipe**: ~0.7 ns/op (PipeOps.emit_to_transform_pipe) - Inline transformation
- **Async pipe**: ~8 ns/op (PipeOps.emit_to_async_pipe) - Enqueue to circuit
- **Counter signal**: ~8 ns/op (CounterOps.emit_increment) - Serventis signal emission

These measure **what the caller pays** - the cost of calling `pipe.emit()` and returning
immediately.

**Round-Trip Cost** (Testing/Synchronization):

- **Emission + await**: ~6470 ns/op (PipeOps.emit_with_await) - Full synchronization
- **With counter**: ~6361 ns/op (PipeOps.emit_with_counter_await) - Including observer work

These measure **emission + circuit processing + await()** - used for testing but not representative
of production performance where emissions are fire-and-forget.

**Key Insight**: The async pipe emission (~8ns) is the production hot-path cost for crossing the
circuit boundary. The round-trip benchmarks (~6470ns) include queue draining and thread
synchronization overhead that doesn't occur in normal production flow.

#### Parameterized Topology Benchmarks

PipeOps includes parameterized benchmarks to measure how topology affects performance:

**Chain Depth** (1, 5, 10, 20 pipes):

- Measures how emission cost scales with chain length
- Each chain consists of identity transformation pipes: `v -> v`
- Tests synchronous pipe chaining overhead

```bash
# Compare scaling across different chain depths
substrates/jmh.sh "PipeOps.emit_chain_depth.*"
```

**Fan-out Width** (1, 5, 10, 20 targets):

- Measures how emission cost scales with broadcast width
- Each fan-out broadcasts a single emission to N empty pipes
- Tests one-to-many emission overhead

```bash
# Compare scaling across different fan-out widths
substrates/jmh.sh "PipeOps.emit_fanout_width.*"
```

**Expected Scaling**:

- **Chain depth**: Linear scaling O(n) - each pipe adds transformation overhead
- **Fan-out width**: Linear scaling O(n) - each target requires separate emit() call

These benchmarks help determine the performance impact of different pipe topologies in neural-like
network architectures.

#### Serventis Signals

All Serventis signal emissions have similar performance (~7-9 ns/op) because they follow the same
pattern:

1. Cache lookup for pre-computed Signal instance (~2 ns)
2. Pipe emission to circuit queue (~5-7 ns)

This consistency validates the zero-allocation signal caching strategy.

**Transaction Coordination Signals**: The TransactionOps benchmarks measure distributed transaction
coordination operations from both COORDINATOR (transaction manager) and PARTICIPANT (client)
perspectives. All transaction signals (START, PREPARE, COMMIT, ROLLBACK, ABORT, EXPIRE, CONFLICT,
COMPENSATE) have consistent ~8-9 ns/op emission cost across both dimensions, confirming that the
dual-perspective model adds no performance overhead. Transaction creation (~1.85 ns/op) matches
other Serventis instruments, enabling observation of distributed protocols (2PC, 3PC, Saga) with
minimal impact.

### Dead-Code Elimination Prevention

All benchmarks return values to prevent JVM dead-code elimination:

```java

@Benchmark
public Name name_from_string() {
    return cortex.name(FIRST);  // Return value used by JMH
}
```

Without returning the value, the JVM might optimize away the entire operation, producing unrealistic
results.

## Performance Guidelines

### When to Use Hot-Path Benchmarks

Use hot-path benchmarks when:

- Measuring operation performance in long-lived circuits
- Optimizing steady-state behavior
- Comparing algorithmic approaches without lifecycle noise
- Understanding the actual cost of an operation in production

### When to Use Batched Benchmarks

Use batched benchmarks when:

- Operations are extremely fast (< 10 ns)
- Single measurements have high variance
- Comparing relative performance of similar operations
- Measuring throughput-oriented scenarios

### When to Use Single-Operation Benchmarks

Use single-operation benchmarks when:

- Operations are slow enough for stable measurements (> 100 ns)
- Total cost including overhead is relevant
- Comparing different operation types
- Establishing baseline measurements

## Example: Comparing Benchmark Types

To understand the performance characteristics of a circuit conduit:

```bash
# 1. Measure full lifecycle cost
substrates/jmh.sh CircuitOps.conduit_create_close

# 2. Measure hot-path operation cost
substrates/jmh.sh CircuitOps.hot_conduit_create

# 3. Measure amortized cost
substrates/jmh.sh CortexOps.circuit_batch
```

This provides three perspectives:

- Full lifecycle cost: Creation + operation + cleanup
- Hot-path cost: Operation only (steady state)
- Amortized cost: Average over many operations

## Benchmark Configuration

All benchmarks use consistent JMH configuration:

```java
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
```

This configuration balances measurement accuracy with reasonable execution time.

## Contributing Benchmarks

When adding new benchmarks, follow these guidelines:

1. **Add batched variants** for operations < 10 ns
2. **Use BATCH_SIZE = 1000** consistently
3. **Return results** to prevent DCE
4. **Include hot-path variants** for lifecycle operations
5. **Document expected performance** in code comments
6. **Update BENCHMARKS.md** with new results

## See Also

- `CLAUDE.md` - Development conventions and performance best practices
- `USER_GUIDE.md` - Comprehensive API usage guide
- `substrates/jmh/` - Benchmark source code