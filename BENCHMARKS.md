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
Model Name: Mac mini
Model Identifier: Mac16,10
Model Number: MU9D3LL/A

Chip: Apple M4
Total Number of Cores: 10 (4 performance and 6 efficiency)
Memory: 16 GB

java version "25.0.1" 2025-10-21 LTS
Java(TM) SE Runtime Environment (build 25.0.1+8-LTS-27)
Java HotSpot(TM) 64-Bit Server VM (build 25.0.1+8-LTS-27, mixed mode, sharing)

Humainary's (Alpha) SPI: io.humainary.substrates.spi.alpha.Provider 

Benchmark                                                                    Mode  Cnt      Score      Error  Units
i.h.serventis.jmh.opt.data.CacheOps.cache_from_conduit                       avgt    5      1.840 ±    0.091  ns/op
i.h.serventis.jmh.opt.data.CacheOps.cache_from_conduit_batch                 avgt    5      1.633 ±    0.012  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_evict                               avgt    5      8.627 ±    2.841  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_evict_batch                         avgt    5      8.260 ±    3.377  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_expire                              avgt    5      8.863 ±    3.735  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_expire_batch                        avgt    5      7.969 ±    2.672  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_hit                                 avgt    5      8.252 ±    2.602  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_hit_batch                           avgt    5      7.751 ±    2.421  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_lookup                              avgt    5      8.591 ±    3.799  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_lookup_batch                        avgt    5      8.068 ±    3.467  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_miss                                avgt    5      8.099 ±    1.008  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_miss_batch                          avgt    5      7.790 ±    2.031  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_remove                              avgt    5      7.624 ±    0.943  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_remove_batch                        avgt    5      7.422 ±    2.444  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_sign                                avgt    5      8.008 ±    2.689  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_sign_batch                          avgt    5      8.683 ±    3.138  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_store                               avgt    5      8.656 ±    3.390  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_store_batch                         avgt    5      8.205 ±    0.445  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_aggregate                        avgt    5      8.471 ±    4.366  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_aggregate_batch                  avgt    5      8.103 ±    4.935  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_backpressure                     avgt    5      8.615 ±    3.109  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_backpressure_batch               avgt    5      8.179 ±    5.162  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_buffer                           avgt    5      8.001 ±    0.658  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_buffer_batch                     avgt    5      7.297 ±    1.740  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_checkpoint                       avgt    5      8.367 ±    3.405  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_checkpoint_batch                 avgt    5      8.301 ±    8.620  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_filter                           avgt    5      8.676 ±    3.091  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_filter_batch                     avgt    5      7.730 ±    2.616  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_input                            avgt    5      8.056 ±    1.285  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_input_batch                      avgt    5      7.524 ±    3.058  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_lag                              avgt    5      8.734 ±    3.317  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_lag_batch                        avgt    5      7.596 ±    0.821  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_output                           avgt    5      8.428 ±    2.323  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_output_batch                     avgt    5      8.268 ±    3.144  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_overflow                         avgt    5      8.236 ±    2.483  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_overflow_batch                   avgt    5      7.560 ±    3.290  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_sign                             avgt    5      8.703 ±    5.475  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_sign_batch                       avgt    5      9.049 ±    0.491  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_skip                             avgt    5      8.509 ±    0.672  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_skip_batch                       avgt    5      7.672 ±    1.519  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_transform                        avgt    5      7.568 ±    1.986  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_transform_batch                  avgt    5      8.179 ±    4.990  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_watermark                        avgt    5      8.015 ±    1.074  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_watermark_batch                  avgt    5     14.720 ±   25.940  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_flow_etl                     avgt    5     43.104 ±   19.001  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_flow_stream                  avgt    5     42.744 ±   18.179  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_flow_windowed                avgt    5     40.924 ±    7.135  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_from_conduit                 avgt    5      1.878 ±    0.016  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_from_conduit_batch           avgt    5      1.662 ±    0.005  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_dequeue                             avgt    5      8.391 ±    2.744  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_dequeue_batch                       avgt    5      7.678 ±    2.431  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_enqueue                             avgt    5      8.000 ±    0.965  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_enqueue_batch                       avgt    5      8.394 ±    0.446  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_overflow                            avgt    5      7.860 ±    1.584  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_overflow_batch                      avgt    5      7.363 ±    2.528  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_sign                                avgt    5      7.870 ±    2.015  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_sign_batch                          avgt    5      8.873 ±    0.880  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_underflow                           avgt    5      7.729 ±    1.400  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_underflow_batch                     avgt    5      9.185 ±    0.480  ns/op
i.h.serventis.jmh.opt.data.QueueOps.queue_from_conduit                       avgt    5      1.873 ±    0.070  ns/op
i.h.serventis.jmh.opt.data.QueueOps.queue_from_conduit_batch                 avgt    5      1.662 ±    0.010  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_overflow                            avgt    5      7.697 ±    1.729  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_overflow_batch                      avgt    5      9.288 ±    1.552  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_pop                                 avgt    5      8.465 ±    2.693  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_pop_batch                           avgt    5      8.040 ±    3.594  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_push                                avgt    5      8.393 ±    2.942  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_push_batch                          avgt    5      7.693 ±    2.757  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_sign                                avgt    5      9.034 ±    1.311  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_sign_batch                          avgt    5      8.521 ±    1.403  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_underflow                           avgt    5      8.259 ±    3.689  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_underflow_batch                     avgt    5      7.174 ±    5.771  ns/op
i.h.serventis.jmh.opt.data.StackOps.stack_from_conduit                       avgt    5      1.875 ±    0.060  ns/op
i.h.serventis.jmh.opt.data.StackOps.stack_from_conduit_batch                 avgt    5      1.661 ±    0.005  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_crash                             avgt    5      7.549 ±    2.699  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_crash_batch                       avgt    5      7.068 ±    4.888  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_fail                              avgt    5      8.928 ±    1.401  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_fail_batch                        avgt    5      7.299 ±    1.562  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_kill                              avgt    5      7.492 ±    0.992  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_kill_batch                        avgt    5      8.374 ±    0.881  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_restart                           avgt    5      8.243 ±    2.603  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_restart_batch                     avgt    5      7.566 ±    1.263  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_resume                            avgt    5      8.575 ±    3.499  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_resume_batch                      avgt    5      8.243 ±    5.305  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_sign                              avgt    5      9.043 ±    0.538  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_sign_batch                        avgt    5      7.823 ±    3.840  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_spawn                             avgt    5      8.451 ±    0.589  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_spawn_batch                       avgt    5      8.061 ±    0.562  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_start                             avgt    5      8.461 ±    3.050  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_start_batch                       avgt    5      7.999 ±    3.089  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_stop                              avgt    5      7.838 ±    1.806  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_stop_batch                        avgt    5      7.629 ±    2.704  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_suspend                           avgt    5      7.911 ±    1.312  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_suspend_batch                     avgt    5      7.588 ±    0.807  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.process_from_conduit                   avgt    5      1.878 ±    0.060  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.process_from_conduit_batch             avgt    5      1.662 ±    0.006  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_call                              avgt    5      8.097 ±    2.723  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_call_batch                        avgt    5      9.577 ±    3.018  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_called                            avgt    5      8.200 ±    2.916  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_called_batch                      avgt    5      7.656 ±    2.408  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_delay                             avgt    5      7.902 ±    1.888  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_delay_batch                       avgt    5      9.240 ±    0.449  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_delayed                           avgt    5      8.569 ±    0.440  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_delayed_batch                     avgt    5      7.417 ±    1.917  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_discard                           avgt    5      7.745 ±    2.811  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_discard_batch                     avgt    5      7.688 ±    2.277  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_discarded                         avgt    5      8.168 ±    2.263  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_discarded_batch                   avgt    5      8.964 ±    0.634  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_disconnect                        avgt    5      8.703 ±    2.763  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_disconnect_batch                  avgt    5      7.665 ±    1.836  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_disconnected                      avgt    5      9.152 ±    0.993  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_disconnected_batch                avgt    5      7.877 ±    3.383  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_expire                            avgt    5      8.450 ±    2.961  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_expire_batch                      avgt    5      7.326 ±    1.640  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_expired                           avgt    5      8.364 ±    2.138  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_expired_batch                     avgt    5      9.496 ±    0.746  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_fail                              avgt    5      8.444 ±    0.344  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_fail_batch                        avgt    5      7.748 ±    1.991  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_failed                            avgt    5      7.990 ±    1.669  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_failed_batch                      avgt    5      8.147 ±    3.147  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_recourse                          avgt    5      8.025 ±    1.420  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_recourse_batch                    avgt    5      7.878 ±    2.757  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_recoursed                         avgt    5      8.875 ±    0.506  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_recoursed_batch                   avgt    5      8.159 ±    3.367  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_redirect                          avgt    5      7.672 ±    2.151  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_redirect_batch                    avgt    5      9.842 ±    1.799  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_redirected                        avgt    5      9.061 ±    1.486  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_redirected_batch                  avgt    5      9.393 ±    0.480  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_reject                            avgt    5      8.492 ±    1.966  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_reject_batch                      avgt    5      7.534 ±    1.871  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_rejected                          avgt    5      8.580 ±    3.507  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_rejected_batch                    avgt    5      7.660 ±    2.048  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_resume                            avgt    5      7.981 ±    2.783  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_resume_batch                      avgt    5      9.142 ±    1.649  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_resumed                           avgt    5      8.133 ±    2.072  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_resumed_batch                     avgt    5      9.129 ±    1.226  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_retried                           avgt    5      8.454 ±    0.260  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_retried_batch                     avgt    5      8.089 ±    0.464  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_retry                             avgt    5      8.188 ±    3.571  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_retry_batch                       avgt    5      7.635 ±    3.843  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_schedule                          avgt    5      8.614 ±    0.663  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_schedule_batch                    avgt    5      8.290 ±    3.151  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_scheduled                         avgt    5      8.823 ±    1.047  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_scheduled_batch                   avgt    5      9.294 ±    3.168  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_signal                            avgt    5      8.363 ±    2.410  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_signal_batch                      avgt    5      8.130 ±    3.273  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_start                             avgt    5      8.562 ±    0.331  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_start_batch                       avgt    5      8.962 ±    2.191  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_started                           avgt    5      8.654 ±    3.337  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_started_batch                     avgt    5      9.373 ±    0.436  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_stop                              avgt    5      8.939 ±    0.777  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_stop_batch                        avgt    5      9.506 ±    2.910  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_stopped                           avgt    5      8.032 ±    1.754  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_stopped_batch                     avgt    5      9.589 ±    3.468  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_succeeded                         avgt    5      8.796 ±    2.354  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_succeeded_batch                   avgt    5      9.144 ±    1.255  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_success                           avgt    5      9.341 ±    1.281  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_success_batch                     avgt    5      9.195 ±    1.539  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_suspend                           avgt    5      8.431 ±    0.513  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_suspend_batch                     avgt    5      8.201 ±    3.115  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_suspended                         avgt    5      8.860 ±    1.312  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_suspended_batch                   avgt    5      7.210 ±    1.699  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.service_from_conduit                   avgt    5      1.863 ±    0.060  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.service_from_conduit_batch             avgt    5      1.662 ±    0.011  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_cancel                               avgt    5      8.095 ±    1.098  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_cancel_batch                         avgt    5      7.585 ±    1.888  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_complete                             avgt    5      7.866 ±    2.378  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_complete_batch                       avgt    5      7.726 ±    3.390  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_fail                                 avgt    5      8.225 ±    2.660  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_fail_batch                           avgt    5      7.734 ±    2.213  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_progress                             avgt    5      7.905 ±    2.083  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_progress_batch                       avgt    5      7.592 ±    2.256  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_reject                               avgt    5      8.073 ±    1.968  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_reject_batch                         avgt    5      8.883 ±    1.687  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_resume                               avgt    5      8.048 ±    2.320  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_resume_batch                         avgt    5      7.498 ±    2.117  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_schedule                             avgt    5      8.564 ±    0.503  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_schedule_batch                       avgt    5      7.965 ±    2.548  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_sign                                 avgt    5      8.006 ±    3.146  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_sign_batch                           avgt    5      8.705 ±    2.343  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_start                                avgt    5      8.623 ±    2.971  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_start_batch                          avgt    5      7.333 ±    1.779  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_submit                               avgt    5      8.035 ±    1.615  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_submit_batch                         avgt    5      8.060 ±    2.499  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_suspend                              avgt    5      7.624 ±    2.906  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_suspend_batch                        avgt    5      7.571 ±    2.343  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_timeout                              avgt    5      8.490 ±    2.285  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_timeout_batch                        avgt    5      7.892 ±    1.922  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.task_from_conduit                         avgt    5      1.878 ±    0.073  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.task_from_conduit_batch                   avgt    5      1.661 ±    0.008  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_meet_deadline                       avgt    5      8.420 ±    0.509  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_meet_deadline_batch                 avgt    5      7.631 ±    3.071  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_meet_threshold                      avgt    5      7.923 ±    1.918  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_meet_threshold_batch                avgt    5      9.580 ±    3.289  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_miss_deadline                       avgt    5      8.471 ±    2.833  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_miss_deadline_batch                 avgt    5      9.449 ±    2.786  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_miss_threshold                      avgt    5      8.502 ±    2.941  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_miss_threshold_batch                avgt    5      7.731 ±    2.493  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_signal                              avgt    5      8.112 ±    1.733  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_signal_batch                        avgt    5      9.568 ±    3.117  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.timer_from_conduit                       avgt    5      1.864 ±    0.024  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.timer_from_conduit_batch                 avgt    5      1.661 ±    0.004  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_abort_coordinator             avgt    5      8.915 ±    0.780  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_abort_coordinator_batch       avgt    5     10.253 ±    0.913  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_abort_participant             avgt    5      8.315 ±    2.594  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_abort_participant_batch       avgt    5      8.470 ±    3.127  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_commit_coordinator            avgt    5      8.644 ±    2.854  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_commit_coordinator_batch      avgt    5      8.099 ±    3.327  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_commit_participant            avgt    5      8.736 ±    1.025  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_commit_participant_batch      avgt    5      9.521 ±    2.828  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_compensate_coordinator        avgt    5      7.451 ±    1.905  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_compensate_coordinator_batch  avgt    5      8.030 ±    3.554  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_compensate_participant        avgt    5      8.102 ±    2.016  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_compensate_participant_batch  avgt    5      8.779 ±    1.667  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_conflict_coordinator          avgt    5      7.897 ±    2.207  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_conflict_coordinator_batch    avgt    5      8.219 ±    2.914  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_conflict_participant          avgt    5      8.642 ±    3.488  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_conflict_participant_batch    avgt    5      8.064 ±    0.535  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_expire_coordinator            avgt    5      8.962 ±    0.529  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_expire_coordinator_batch      avgt    5      8.817 ±    1.001  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_expire_participant            avgt    5      8.214 ±    3.246  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_expire_participant_batch      avgt    5      9.445 ±    3.363  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_prepare_coordinator           avgt    5      8.671 ±    2.729  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_prepare_coordinator_batch     avgt    5      7.488 ±    1.926  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_prepare_participant           avgt    5      8.733 ±    0.809  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_prepare_participant_batch     avgt    5      8.815 ±    1.916  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_rollback_coordinator          avgt    5      8.500 ±    2.438  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_rollback_coordinator_batch    avgt    5      9.974 ±    0.230  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_rollback_participant          avgt    5      9.177 ±    1.007  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_rollback_participant_batch    avgt    5      8.071 ±    2.702  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_signal                        avgt    5      9.145 ±    0.781  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_signal_batch                  avgt    5      8.904 ±    1.993  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_start_coordinator             avgt    5      8.570 ±    1.340  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_start_coordinator_batch       avgt    5      9.400 ±    2.763  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_start_participant             avgt    5      8.019 ±    1.734  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_start_participant_batch       avgt    5      8.782 ±    1.482  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.transaction_from_conduit           avgt    5      1.864 ±    0.059  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.transaction_from_conduit_batch     avgt    5      1.661 ±    0.006  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.breaker_from_conduit                   avgt    5      1.867 ±    0.064  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.breaker_from_conduit_batch             avgt    5      1.661 ±    0.002  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_close                             avgt    5      9.077 ±    1.509  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_close_batch                       avgt    5      7.304 ±    1.672  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_half_open                         avgt    5      8.117 ±    0.495  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_half_open_batch                   avgt    5      7.663 ±    2.143  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_open                              avgt    5      7.635 ±    1.991  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_open_batch                        avgt    5      8.637 ±    1.761  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_probe                             avgt    5      7.893 ±    2.706  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_probe_batch                       avgt    5      7.637 ±    2.680  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_reset                             avgt    5      7.698 ±    0.907  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_reset_batch                       avgt    5      7.414 ±    2.659  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_sign                              avgt    5      8.313 ±    2.023  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_sign_batch                        avgt    5      7.242 ±    1.952  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_trip                              avgt    5      8.306 ±    0.690  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_trip_batch                        avgt    5      7.714 ±    2.608  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_fail_egress                          avgt    5      8.004 ±    2.185  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_fail_egress_batch                    avgt    5      9.850 ±    0.947  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_fail_ingress                         avgt    5      8.500 ±    4.014  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_fail_ingress_batch                   avgt    5      9.267 ±    1.573  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_fail_transit                         avgt    5      8.128 ±    3.766  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_fail_transit_batch                   avgt    5      9.159 ±    1.037  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_signal                               avgt    5      8.610 ±    2.966  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_signal_batch                         avgt    5      8.975 ±    1.850  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_success_egress                       avgt    5      7.812 ±    2.000  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_success_egress_batch                 avgt    5      9.151 ±    1.549  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_success_ingress                      avgt    5      9.255 ±    1.413  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_success_ingress_batch                avgt    5      7.466 ±    2.430  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_success_transit                      avgt    5      9.056 ±    0.578  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_success_transit_batch                avgt    5     10.333 ±    1.297  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.flow_from_conduit                         avgt    5      1.880 ±    0.056  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.flow_from_conduit_batch                   avgt    5      1.662 ±    0.006  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_corrupt                            avgt    5      8.013 ±    2.277  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_corrupt_batch                      avgt    5      7.667 ±    2.954  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_drop                               avgt    5      8.049 ±    2.900  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_drop_batch                         avgt    5      8.022 ±    3.428  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_forward                            avgt    5      8.678 ±    0.964  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_forward_batch                      avgt    5      8.400 ±    0.655  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_fragment                           avgt    5      8.414 ±    3.149  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_fragment_batch                     avgt    5      7.799 ±    2.574  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_reassemble                         avgt    5      8.518 ±    3.030  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_reassemble_batch                   avgt    5      7.495 ±    2.347  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_receive                            avgt    5      8.754 ±    2.686  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_receive_batch                      avgt    5      8.238 ±    3.122  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_reorder                            avgt    5      8.244 ±    3.019  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_reorder_batch                      avgt    5      8.136 ±    2.437  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_route                              avgt    5      8.627 ±    2.734  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_route_batch                        avgt    5      7.840 ±    3.676  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_send                               avgt    5      7.827 ±    0.302  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_send_batch                         avgt    5      7.633 ±    2.469  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_sign                               avgt    5      8.011 ±    4.650  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_sign_batch                         avgt    5      7.624 ±    2.545  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.router_from_conduit                     avgt    5      1.867 ±    0.039  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.router_from_conduit_batch               avgt    5      1.662 ±    0.004  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_contract                            avgt    5      7.925 ±    2.898  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_contract_batch                      avgt    5      6.996 ±    6.778  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_deny                                avgt    5      7.745 ±    1.834  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_deny_batch                          avgt    5      7.623 ±    3.379  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_drain                               avgt    5      9.348 ±    0.629  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_drain_batch                         avgt    5      8.350 ±    4.913  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_drop                                avgt    5      8.434 ±    3.447  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_drop_batch                          avgt    5      7.611 ±    2.344  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_expand                              avgt    5      7.999 ±    3.021  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_expand_batch                        avgt    5      7.935 ±    1.671  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_pass                                avgt    5      8.252 ±    2.027  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_pass_batch                          avgt    5      7.690 ±    2.801  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_sign                                avgt    5      8.007 ±    1.896  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_sign_batch                          avgt    5      8.376 ±    2.314  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.valve_from_conduit                       avgt    5      1.884 ±    0.078  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.valve_from_conduit_batch                 avgt    5      1.660 ±    0.004  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_contract_provider                avgt    5      7.991 ±    0.563  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_contract_provider_batch          avgt    5      7.410 ±    2.650  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_contract_receiver                avgt    5      8.068 ±    1.927  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_contract_receiver_batch          avgt    5      7.819 ±    0.619  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_full_exchange                    avgt    5      9.971 ±    3.108  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_full_exchange_batch              avgt    5      9.935 ±    0.878  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_signal                           avgt    5      8.127 ±    1.416  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_signal_batch                     avgt    5      8.045 ±    2.808  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_transfer_provider                avgt    5      8.296 ±    3.897  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_transfer_provider_batch          avgt    5      7.855 ±    0.402  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_transfer_receiver                avgt    5     11.216 ±    0.776  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_transfer_receiver_batch          avgt    5      7.722 ±    2.205  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.exchange_from_conduit                 avgt    5      1.857 ±    0.006  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.exchange_from_conduit_batch           avgt    5      1.660 ±    0.002  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_acquire                             avgt    5      7.779 ±    1.209  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_acquire_batch                       avgt    5     10.154 ±    1.121  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_acquired                            avgt    5      8.076 ±    1.862  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_acquired_batch                      avgt    5      9.290 ±    1.654  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_denied                              avgt    5      8.514 ±    3.504  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_denied_batch                        avgt    5      7.745 ±    1.002  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_deny                                avgt    5      8.267 ±    2.172  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_deny_batch                          avgt    5      7.623 ±    1.957  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_expire                              avgt    5      8.451 ±    0.574  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_expire_batch                        avgt    5      7.738 ±    2.657  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_expired                             avgt    5      8.476 ±    3.046  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_expired_batch                       avgt    5      7.673 ±    1.963  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_extend                              avgt    5      8.140 ±    3.267  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_extend_batch                        avgt    5      9.366 ±    3.029  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_extended                            avgt    5      8.463 ±    2.359  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_extended_batch                      avgt    5      7.659 ±    2.676  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_grant                               avgt    5      9.305 ±    0.980  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_grant_batch                         avgt    5      9.451 ±    3.024  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_granted                             avgt    5      8.424 ±    2.959  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_granted_batch                       avgt    5      7.849 ±    2.353  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_probe                               avgt    5      8.523 ±    0.794  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_probe_batch                         avgt    5      8.009 ±    0.493  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_probed                              avgt    5      8.444 ±    0.943  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_probed_batch                        avgt    5     10.140 ±    0.664  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_release                             avgt    5      8.537 ±    2.996  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_release_batch                       avgt    5      9.853 ±    0.900  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_released                            avgt    5      8.473 ±    0.626  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_released_batch                      avgt    5      6.990 ±    1.424  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_renew                               avgt    5      8.183 ±    3.857  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_renew_batch                         avgt    5      7.568 ±    1.766  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_renewed                             avgt    5      8.083 ±    1.662  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_renewed_batch                       avgt    5      7.618 ±    2.929  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_revoke                              avgt    5      8.436 ±    2.792  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_revoke_batch                        avgt    5      7.582 ±    2.386  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_revoked                             avgt    5      7.929 ±    2.121  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_revoked_batch                       avgt    5     10.268 ±    1.536  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_signal                              avgt    5      8.189 ±    0.446  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_signal_batch                        avgt    5      9.309 ±    0.411  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.lease_from_conduit                       avgt    5      1.871 ±    0.098  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.lease_from_conduit_batch                 avgt    5      1.661 ±    0.006  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_borrow                               avgt    5      8.739 ±    0.782  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_borrow_batch                         avgt    5      8.224 ±    3.380  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_contract                             avgt    5      8.213 ±    1.715  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_contract_batch                       avgt    5      8.125 ±    3.180  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_expand                               avgt    5      7.659 ±    1.660  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_expand_batch                         avgt    5      7.845 ±    2.315  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_reclaim                              avgt    5      8.936 ±    1.584  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_reclaim_batch                        avgt    5      7.518 ±    3.162  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_sign                                 avgt    5      8.163 ±    2.106  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_sign_batch                           avgt    5      8.602 ±    2.287  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.pool_from_conduit                         avgt    5      1.885 ±    0.058  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.pool_from_conduit_batch                   avgt    5      1.662 ±    0.009  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_acquire                          avgt    5      9.164 ±    0.529  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_acquire_batch                    avgt    5      8.746 ±    0.833  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_attempt                          avgt    5      8.310 ±    2.779  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_attempt_batch                    avgt    5      9.436 ±    3.848  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_deny                             avgt    5      8.351 ±    2.774  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_deny_batch                       avgt    5      7.498 ±    2.246  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_grant                            avgt    5      8.054 ±    3.029  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_grant_batch                      avgt    5      7.583 ±    2.546  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_release                          avgt    5      9.306 ±    0.899  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_release_batch                    avgt    5      7.439 ±    2.410  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_sign                             avgt    5      7.838 ±    1.546  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_sign_batch                       avgt    5      8.625 ±    1.926  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_timeout                          avgt    5      7.634 ±    1.774  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_timeout_batch                    avgt    5      7.540 ±    2.710  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.resource_from_conduit                 avgt    5      1.861 ±    0.036  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.resource_from_conduit_batch           avgt    5      1.661 ±    0.004  ns/op
i.h.serventis.jmh.opt.role.ActorOps.actor_from_conduit                       avgt    5      1.869 ±    0.056  ns/op
i.h.serventis.jmh.opt.role.ActorOps.actor_from_conduit_batch                 avgt    5      1.665 ±    0.001  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_acknowledge                         avgt    5      8.172 ±    2.174  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_acknowledge_batch                   avgt    5      7.532 ±    2.970  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_affirm                              avgt    5      7.925 ±    2.563  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_affirm_batch                        avgt    5      8.067 ±    3.020  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_ask                                 avgt    5      9.298 ±    5.757  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_ask_batch                           avgt    5      7.715 ±    2.273  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_clarify                             avgt    5      8.597 ±    1.059  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_clarify_batch                       avgt    5      6.742 ±    5.958  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_command                             avgt    5      8.428 ±    0.856  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_command_batch                       avgt    5      7.580 ±    3.831  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_deliver                             avgt    5      8.413 ±    2.971  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_deliver_batch                       avgt    5      7.687 ±    2.937  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_deny                                avgt    5      8.283 ±    0.470  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_deny_batch                          avgt    5      8.232 ±    3.982  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_explain                             avgt    5      8.308 ±    3.339  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_explain_batch                       avgt    5      7.518 ±    3.171  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_promise                             avgt    5      8.057 ±    0.721  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_promise_batch                       avgt    5      8.872 ±    1.623  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_report                              avgt    5      8.309 ±    4.149  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_report_batch                        avgt    5      7.594 ±    2.623  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_request                             avgt    5      8.297 ±    2.580  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_request_batch                       avgt    5      8.357 ±    0.744  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_sign                                avgt    5      8.514 ±    4.615  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_sign_batch                          avgt    5      8.254 ±    0.684  ns/op
i.h.serventis.jmh.opt.role.AgentOps.agent_from_conduit                       avgt    5      1.866 ±    0.073  ns/op
i.h.serventis.jmh.opt.role.AgentOps.agent_from_conduit_batch                 avgt    5      1.666 ±    0.012  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_accept                              avgt    5      8.224 ±    2.572  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_accept_batch                        avgt    5      8.283 ±    3.316  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_accepted                            avgt    5      8.172 ±    0.875  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_accepted_batch                      avgt    5      8.732 ±    0.799  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_breach                              avgt    5      9.108 ±    1.097  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_breach_batch                        avgt    5      9.303 ±    2.599  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_breached                            avgt    5      8.519 ±    0.663  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_breached_batch                      avgt    5      9.521 ±    3.388  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_depend                              avgt    5      8.206 ±    2.796  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_depend_batch                        avgt    5      8.749 ±    1.585  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_depended                            avgt    5      8.418 ±    3.024  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_depended_batch                      avgt    5      9.408 ±    0.361  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_fulfill                             avgt    5      8.305 ±    2.840  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_fulfill_batch                       avgt    5      8.945 ±    0.399  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_fulfilled                           avgt    5      8.374 ±    0.802  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_fulfilled_batch                     avgt    5      9.497 ±    3.353  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_inquire                             avgt    5      8.681 ±    0.641  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_inquire_batch                       avgt    5      9.220 ±    1.265  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_inquired                            avgt    5      8.325 ±    3.681  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_inquired_batch                      avgt    5      8.101 ±    2.550  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_observe                             avgt    5      8.330 ±    0.969  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_observe_batch                       avgt    5      8.697 ±    1.884  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_observed                            avgt    5      8.521 ±    2.196  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_observed_batch                      avgt    5      7.927 ±    2.275  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_offer                               avgt    5      8.798 ±    0.338  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_offer_batch                         avgt    5      8.954 ±    1.617  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_offered                             avgt    5      9.273 ±    1.334  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_offered_batch                       avgt    5      8.756 ±    2.746  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_promise                             avgt    5      8.349 ±    0.638  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_promise_batch                       avgt    5      8.016 ±    2.780  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_promised                            avgt    5      8.432 ±    2.574  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_promised_batch                      avgt    5      9.193 ±    1.278  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_retract                             avgt    5      8.513 ±    4.172  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_retract_batch                       avgt    5      9.234 ±    0.733  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_retracted                           avgt    5      8.040 ±    3.477  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_retracted_batch                     avgt    5      9.546 ±    3.115  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_signal                              avgt    5      8.383 ±    3.089  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_signal_batch                        avgt    5      8.116 ±    2.801  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_validate                            avgt    5      8.651 ±    4.432  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_validate_batch                      avgt    5      9.761 ±    0.872  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_validated                           avgt    5     10.741 ±    8.682  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_validated_batch                     avgt    5      8.353 ±    0.829  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.atomic_from_conduit                     avgt    5      1.862 ±    0.066  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.atomic_from_conduit_batch               avgt    5      1.660 ±    0.003  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_attempt                            avgt    5      7.895 ±    0.733  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_attempt_batch                      avgt    5      7.616 ±    3.347  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_backoff                            avgt    5      8.329 ±    3.079  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_backoff_batch                      avgt    5      7.726 ±    2.360  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_exhaust                            avgt    5      8.392 ±    2.509  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_exhaust_batch                      avgt    5      7.555 ±    2.430  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_fail                               avgt    5      8.225 ±    0.618  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_fail_batch                         avgt    5      7.715 ±    1.975  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_park                               avgt    5      7.793 ±    2.546  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_park_batch                         avgt    5      7.747 ±    2.213  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_sign                               avgt    5      8.330 ±    0.555  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_sign_batch                         avgt    5     10.482 ±    1.266  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_spin                               avgt    5      9.398 ±    2.988  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_spin_batch                         avgt    5      6.606 ±    3.836  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_success                            avgt    5      8.498 ±    3.103  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_success_batch                      avgt    5      7.724 ±    2.534  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_yield                              avgt    5      8.005 ±    1.731  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_yield_batch                        avgt    5      7.352 ±    2.576  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_abandon                             avgt    5      7.804 ±    1.764  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_abandon_batch                       avgt    5      7.683 ±    2.099  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_arrive                              avgt    5      8.742 ±    0.798  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_arrive_batch                        avgt    5      9.153 ±    2.049  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_await                               avgt    5      8.020 ±    2.551  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_await_batch                         avgt    5      7.195 ±    2.351  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_release                             avgt    5      8.274 ±    0.623  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_release_batch                       avgt    5      9.504 ±    2.112  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_reset                               avgt    5      8.179 ±    1.749  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_reset_batch                         avgt    5      8.013 ±    3.980  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_sign                                avgt    5      8.265 ±    0.711  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_sign_batch                          avgt    5      9.011 ±    0.734  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_timeout                             avgt    5      8.331 ±    2.406  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_timeout_batch                       avgt    5      7.874 ±    2.449  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.latch_from_conduit                       avgt    5      1.884 ±    0.044  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.latch_from_conduit_batch                 avgt    5      1.660 ±    0.002  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_abandon                              avgt    5      8.350 ±    2.845  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_abandon_batch                        avgt    5      7.672 ±    1.776  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_acquire                              avgt    5      7.835 ±    2.292  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_acquire_batch                        avgt    5      7.393 ±    5.249  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_attempt                              avgt    5      8.085 ±    1.932  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_attempt_batch                        avgt    5      7.520 ±    2.865  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_contest                              avgt    5      8.452 ±    3.102  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_contest_batch                        avgt    5      7.625 ±    1.605  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_deny                                 avgt    5      8.054 ±    2.756  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_deny_batch                           avgt    5      7.979 ±    3.297  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_downgrade                            avgt    5      8.459 ±    3.455  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_downgrade_batch                      avgt    5      7.671 ±    2.772  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_grant                                avgt    5      7.438 ±    1.336  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_grant_batch                          avgt    5      7.766 ±    2.614  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_release                              avgt    5      8.280 ±    2.472  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_release_batch                        avgt    5      7.175 ±    2.703  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_sign                                 avgt    5      7.618 ±    2.050  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_sign_batch                           avgt    5      8.140 ±    3.119  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_timeout                              avgt    5      8.219 ±    0.245  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_timeout_batch                        avgt    5      8.861 ±    1.012  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_upgrade                              avgt    5      7.766 ±    1.779  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_upgrade_batch                        avgt    5      7.873 ±    3.046  ns/op
i.h.serventis.jmh.opt.sync.LockOps.lock_from_conduit                         avgt    5      1.856 ±    0.012  ns/op
i.h.serventis.jmh.opt.sync.LockOps.lock_from_conduit_batch                   avgt    5      1.661 ±    0.010  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.counter_from_conduit                   avgt    5      1.881 ±    0.097  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.counter_from_conduit_batch             avgt    5      1.660 ±    0.002  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_increment                         avgt    5      7.767 ±    1.949  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_increment_batch                   avgt    5      7.407 ±    3.222  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_overflow                          avgt    5      9.875 ±    0.939  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_overflow_batch                    avgt    5      8.332 ±    0.316  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_reset                             avgt    5      8.325 ±    2.940  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_reset_batch                       avgt    5      7.503 ±    2.993  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_sign                              avgt    5      7.878 ±    2.535  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_sign_batch                        avgt    5      7.415 ±    2.835  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_decrement                           avgt    5      7.660 ±    1.991  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_decrement_batch                     avgt    5      7.550 ±    2.151  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_increment                           avgt    5      8.168 ±    2.812  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_increment_batch                     avgt    5      8.130 ±    0.947  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_overflow                            avgt    5      7.795 ±    3.617  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_overflow_batch                      avgt    5      7.520 ±    2.640  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_reset                               avgt    5      8.673 ±    2.891  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_reset_batch                         avgt    5      8.279 ±    3.276  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_sign                                avgt    5      7.848 ±    2.432  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_sign_batch                          avgt    5      8.184 ±    2.058  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_underflow                           avgt    5      7.890 ±    2.031  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_underflow_batch                     avgt    5      7.538 ±    1.612  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.gauge_from_conduit                       avgt    5      1.870 ±    0.008  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.gauge_from_conduit_batch                 avgt    5      1.661 ±    0.006  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_debug                                 avgt    5      7.799 ±    1.134  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_debug_batch                           avgt    5      7.687 ±    2.268  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_info                                  avgt    5      7.944 ±    1.987  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_info_batch                            avgt    5      7.362 ±    1.756  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_severe                                avgt    5      8.155 ±    2.181  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_severe_batch                          avgt    5      8.161 ±    3.652  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_sign                                  avgt    5      9.877 ±    1.159  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_sign_batch                            avgt    5      7.563 ±    3.136  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_warning                               avgt    5      7.744 ±    1.664  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_warning_batch                         avgt    5      7.439 ±    2.645  ns/op
i.h.serventis.jmh.opt.tool.LogOps.log_from_conduit                           avgt    5      1.878 ±    0.020  ns/op
i.h.serventis.jmh.opt.tool.LogOps.log_from_conduit_batch                     avgt    5      1.660 ±    0.002  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_connect                             avgt    5      8.370 ±    2.470  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_connect_batch                       avgt    5      9.425 ±    3.494  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_connected                           avgt    5      8.300 ±    1.273  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_connected_batch                     avgt    5      7.543 ±    2.640  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_disconnect                          avgt    5      8.538 ±    1.723  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_disconnect_batch                    avgt    5      9.184 ±    1.011  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_disconnected                        avgt    5      8.128 ±    1.496  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_disconnected_batch                  avgt    5      9.497 ±    4.188  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_fail                                avgt    5      7.973 ±    2.391  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_fail_batch                          avgt    5      7.822 ±    2.828  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_failed                              avgt    5      9.402 ±    3.078  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_failed_batch                        avgt    5      9.098 ±    1.648  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_process                             avgt    5      7.559 ±    2.096  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_process_batch                       avgt    5      8.229 ±    1.253  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_processed                           avgt    5      7.602 ±    2.263  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_processed_batch                     avgt    5      9.613 ±    2.858  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_receive_batch                       avgt    5      9.999 ±    1.465  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_received_batch                      avgt    5      9.153 ±    2.986  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_signal                              avgt    5      8.219 ±    1.795  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_signal_batch                        avgt    5      8.794 ±    1.734  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_succeed                             avgt    5      8.135 ±    1.171  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_succeed_batch                       avgt    5      8.159 ±    2.356  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_succeeded                           avgt    5      8.569 ±    2.624  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_succeeded_batch                     avgt    5      9.430 ±    2.495  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transfer                            avgt    5      8.089 ±    2.389  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transfer_inbound                    avgt    5      8.503 ±    3.531  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transfer_outbound                   avgt    5      8.193 ±    1.830  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transferred                         avgt    5      8.133 ±    2.369  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transmit_batch                      avgt    5      8.053 ±    0.629  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transmitted_batch                   avgt    5      9.227 ±    4.071  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.probe_from_conduit                       avgt    5      1.891 ±    0.054  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.probe_from_conduit_batch                 avgt    5      1.660 ±    0.002  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_baseline                     avgt    5      7.850 ±    2.319  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_baseline_batch               avgt    5      7.662 ±    1.111  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_target                       avgt    5      8.538 ±    1.212  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_target_batch                 avgt    5      7.482 ±    2.197  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_threshold                    avgt    5      8.510 ±    3.513  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_threshold_batch              avgt    5      9.081 ±    1.281  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_baseline                     avgt    5      8.098 ±    1.798  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_baseline_batch               avgt    5      9.662 ±    1.025  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_target                       avgt    5      8.643 ±    1.516  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_target_batch                 avgt    5      8.724 ±    1.973  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_threshold                    avgt    5      9.134 ±    1.117  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_threshold_batch              avgt    5      8.913 ±    0.933  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_baseline                   avgt    5      7.624 ±    2.112  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_baseline_batch             avgt    5      9.740 ±    0.895  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_target                     avgt    5      8.557 ±    2.445  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_target_batch               avgt    5      8.026 ±    2.459  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_threshold                  avgt    5      8.403 ±    3.247  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_threshold_batch            avgt    5      9.040 ±    2.220  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_signal                             avgt    5      8.296 ±    0.786  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_signal_batch                       avgt    5      8.101 ±    0.271  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.sensor_from_conduit                     avgt    5      1.867 ±    0.065  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.sensor_from_conduit_batch               avgt    5      1.661 ±    0.002  ns/op
i.h.serventis.jmh.sdk.OperationOps.emit_begin                                avgt    5      7.616 ±    1.581  ns/op
i.h.serventis.jmh.sdk.OperationOps.emit_begin_batch                          avgt    5      7.452 ±    2.829  ns/op
i.h.serventis.jmh.sdk.OperationOps.emit_end                                  avgt    5      8.114 ±    0.988  ns/op
i.h.serventis.jmh.sdk.OperationOps.emit_end_batch                            avgt    5      9.357 ±    1.689  ns/op
i.h.serventis.jmh.sdk.OperationOps.emit_sign                                 avgt    5      8.596 ±    0.429  ns/op
i.h.serventis.jmh.sdk.OperationOps.emit_sign_batch                           avgt    5      8.897 ±    2.272  ns/op
i.h.serventis.jmh.sdk.OperationOps.operation_from_conduit                    avgt    5      1.875 ±    0.083  ns/op
i.h.serventis.jmh.sdk.OperationOps.operation_from_conduit_batch              avgt    5      1.662 ±    0.010  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.emit_fail                                   avgt    5      8.542 ±    3.659  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.emit_fail_batch                             avgt    5      8.416 ±    0.556  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.emit_sign                                   avgt    5      8.540 ±    2.937  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.emit_sign_batch                             avgt    5      8.401 ±    3.388  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.emit_success                                avgt    5      7.852 ±    2.430  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.emit_success_batch                          avgt    5      8.384 ±    1.534  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.outcome_from_conduit                        avgt    5      1.859 ±    0.044  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.outcome_from_conduit_batch                  avgt    5      1.661 ±    0.013  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_mixed_pattern                         avgt    5      0.226 ±    0.001  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_single                                avgt    5      0.750 ±    0.001  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_single_batch                          avgt    5      0.019 ±    0.001  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_varied_batch                          avgt    5      1.515 ±    0.001  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_worst_case                            avgt    5      1.185 ±    0.028  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_critical                             avgt    5      8.918 ±    3.323  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_critical_batch                       avgt    5      7.949 ±    1.000  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_normal                               avgt    5      9.030 ±    0.963  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_normal_batch                         avgt    5      7.896 ±    3.442  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_signal                               avgt    5      8.423 ±    2.157  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_signal_batch                         avgt    5      9.578 ±    3.855  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_warning                              avgt    5      8.619 ±    3.090  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_warning_batch                        avgt    5      9.881 ±    1.257  ns/op
i.h.serventis.jmh.sdk.SituationOps.situation_from_conduit                    avgt    5      1.867 ±    0.066  ns/op
i.h.serventis.jmh.sdk.SituationOps.situation_from_conduit_batch              avgt    5      1.662 ±    0.010  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_converging_confirmed                    avgt    5      8.076 ±    1.561  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_converging_confirmed_batch              avgt    5      9.399 ±    3.334  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_defective_tentative                     avgt    5      9.050 ±    0.597  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_defective_tentative_batch               avgt    5      9.171 ±    0.912  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_degraded_measured                       avgt    5      8.172 ±    0.783  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_degraded_measured_batch                 avgt    5      9.255 ±    2.847  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_down_confirmed                          avgt    5      8.120 ±    1.980  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_down_confirmed_batch                    avgt    5      9.336 ±    3.027  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_signal                                  avgt    5      9.383 ±    0.302  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_signal_batch                            avgt    5      8.950 ±    1.640  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_stable_confirmed                        avgt    5      9.152 ±    0.748  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_stable_confirmed_batch                  avgt    5      8.240 ±    1.136  ns/op
i.h.serventis.jmh.sdk.StatusOps.status_from_conduit                          avgt    5      1.866 ±    0.041  ns/op
i.h.serventis.jmh.sdk.StatusOps.status_from_conduit_batch                    avgt    5      1.661 ±    0.007  ns/op
i.h.serventis.jmh.sdk.SurveyOps.emit_divided                                 avgt    5      7.699 ±    3.791  ns/op
i.h.serventis.jmh.sdk.SurveyOps.emit_divided_batch                           avgt    5      8.560 ±    1.023  ns/op
i.h.serventis.jmh.sdk.SurveyOps.emit_majority                                avgt    5      7.443 ±    0.839  ns/op
i.h.serventis.jmh.sdk.SurveyOps.emit_majority_batch                          avgt    5      9.319 ±    3.803  ns/op
i.h.serventis.jmh.sdk.SurveyOps.emit_signal                                  avgt    5      8.352 ±    2.731  ns/op
i.h.serventis.jmh.sdk.SurveyOps.emit_signal_batch                            avgt    5      7.548 ±    1.809  ns/op
i.h.serventis.jmh.sdk.SurveyOps.emit_unanimous                               avgt    5      6.974 ±    0.607  ns/op
i.h.serventis.jmh.sdk.SurveyOps.emit_unanimous_batch                         avgt    5      8.782 ±    1.870  ns/op
i.h.serventis.jmh.sdk.SurveyOps.survey_from_conduit                          avgt    5      1.855 ±    0.017  ns/op
i.h.serventis.jmh.sdk.SurveyOps.survey_from_conduit_batch                    avgt    5      1.662 ±    0.011  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_alarm_flow                              avgt    5      8.557 ±    3.310  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_alarm_flow_batch                        avgt    5      9.411 ±    3.534  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_fault_link                              avgt    5      7.957 ±    1.773  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_fault_link_batch                        avgt    5      9.773 ±    0.302  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_limit_time                              avgt    5      8.343 ±    3.232  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_limit_time_batch                        avgt    5      9.498 ±    3.046  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_normal_space                            avgt    5      9.070 ±    1.170  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_normal_space_batch                      avgt    5      9.334 ±    2.793  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_signal                                  avgt    5      8.062 ±    2.455  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_signal_batch                            avgt    5      9.755 ±    1.082  ns/op
i.h.serventis.jmh.sdk.SystemOps.system_from_conduit                          avgt    5      1.864 ±    0.034  ns/op
i.h.serventis.jmh.sdk.SystemOps.system_from_conduit_batch                    avgt    5      1.661 ±    0.003  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_chaos                                    avgt    5      8.262 ±    3.014  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_chaos_batch                              avgt    5      7.534 ±    2.491  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_cycle                                    avgt    5      8.500 ±    3.077  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_cycle_batch                              avgt    5      7.979 ±    3.281  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_drift                                    avgt    5      8.402 ±    3.443  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_drift_batch                              avgt    5      7.092 ±    3.703  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_sign                                     avgt    5      7.881 ±    1.175  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_sign_batch                               avgt    5      8.940 ±    3.809  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_spike                                    avgt    5      8.375 ±    2.872  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_spike_batch                              avgt    5      8.051 ±    3.130  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_stable                                   avgt    5      7.715 ±    2.012  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_stable_batch                             avgt    5      7.661 ±    2.611  ns/op
i.h.serventis.jmh.sdk.TrendOps.trend_from_conduit                            avgt    5      1.864 ±    0.060  ns/op
i.h.serventis.jmh.sdk.TrendOps.trend_from_conduit_batch                      avgt    5      1.662 ±    0.010  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.cycle_from_conduit                       avgt    5      1.859 ±    0.062  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.cycle_from_conduit_batch                 avgt    5      1.660 ±    0.002  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_repeat                              avgt    5      8.389 ±    0.448  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_repeat_batch                        avgt    5      9.172 ±    3.129  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_return                              avgt    5      8.724 ±    2.778  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_return_batch                        avgt    5      8.306 ±    0.974  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_signal                              avgt    5      8.409 ±    2.493  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_signal_batch                        avgt    5      7.515 ±    1.794  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_single                              avgt    5      8.177 ±    3.606  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_single_batch                        avgt    5      9.163 ±    0.462  ns/op
i.h.substrates.jmh.CircuitOps.conduit_create_close                           avgt    5    276.695 ±   84.993  ns/op
i.h.substrates.jmh.CircuitOps.conduit_create_named                           avgt    5    284.169 ±  148.803  ns/op
i.h.substrates.jmh.CircuitOps.conduit_create_with_flow                       avgt    5    283.898 ±  121.872  ns/op
i.h.substrates.jmh.CircuitOps.create_and_close                               avgt    5    620.792 ± 1697.048  ns/op
i.h.substrates.jmh.CircuitOps.create_and_close_batch                         avgt    5    322.202 ±  181.531  ns/op
i.h.substrates.jmh.CircuitOps.create_multiple_and_close                      avgt    5   1251.771 ±  234.659  ns/op
i.h.substrates.jmh.CircuitOps.create_named_and_close                         avgt    5    553.868 ± 1572.083  ns/op
i.h.substrates.jmh.CircuitOps.hot_conduit_create                             avgt    5     19.073 ±    0.054  ns/op
i.h.substrates.jmh.CircuitOps.hot_conduit_create_named                       avgt    5     19.060 ±    0.019  ns/op
i.h.substrates.jmh.CircuitOps.hot_conduit_create_with_flow                   avgt    5     21.944 ±    0.045  ns/op
i.h.substrates.jmh.CircuitOps.hot_pipe_async                                 avgt    5      8.261 ±    1.991  ns/op
i.h.substrates.jmh.CircuitOps.hot_pipe_async_with_flow                       avgt    5     10.591 ±    3.676  ns/op
i.h.substrates.jmh.CircuitOps.pipe_async                                     avgt    5    293.202 ±  262.212  ns/op
i.h.substrates.jmh.CircuitOps.pipe_async_with_flow                           avgt    5    301.853 ±  158.337  ns/op
i.h.substrates.jmh.ConduitOps.get_by_name                                    avgt    5      1.897 ±    0.132  ns/op
i.h.substrates.jmh.ConduitOps.get_by_name_batch                              avgt    5      1.658 ±    0.004  ns/op
i.h.substrates.jmh.ConduitOps.get_by_substrate                               avgt    5      2.048 ±    0.050  ns/op
i.h.substrates.jmh.ConduitOps.get_by_substrate_batch                         avgt    5      1.809 ±    0.002  ns/op
i.h.substrates.jmh.ConduitOps.get_cached                                     avgt    5      3.431 ±    0.093  ns/op
i.h.substrates.jmh.ConduitOps.get_cached_batch                               avgt    5      3.304 ±    0.010  ns/op
i.h.substrates.jmh.ConduitOps.subscribe                                      avgt    5    476.808 ±   66.424  ns/op
i.h.substrates.jmh.ConduitOps.subscribe_batch                                avgt    5    467.546 ±   48.207  ns/op
i.h.substrates.jmh.ConduitOps.subscribe_with_emission_await                  avgt    5   8359.588 ±  582.788  ns/op
i.h.substrates.jmh.CortexOps.circuit                                         avgt    5    255.908 ±  166.851  ns/op
i.h.substrates.jmh.CortexOps.circuit_batch                                   avgt    5    277.084 ±  111.948  ns/op
i.h.substrates.jmh.CortexOps.circuit_named                                   avgt    5    291.105 ±  197.477  ns/op
i.h.substrates.jmh.CortexOps.current                                         avgt    5      1.083 ±    0.001  ns/op
i.h.substrates.jmh.CortexOps.name_class                                      avgt    5      1.476 ±    0.014  ns/op
i.h.substrates.jmh.CortexOps.name_enum                                       avgt    5      1.784 ±    0.001  ns/op
i.h.substrates.jmh.CortexOps.name_iterable                                   avgt    5      7.869 ±    0.007  ns/op
i.h.substrates.jmh.CortexOps.name_path                                       avgt    5      1.890 ±    0.008  ns/op
i.h.substrates.jmh.CortexOps.name_path_batch                                 avgt    5      1.683 ±    0.001  ns/op
i.h.substrates.jmh.CortexOps.name_string                                     avgt    5      2.797 ±    0.436  ns/op
i.h.substrates.jmh.CortexOps.name_string_batch                               avgt    5      2.583 ±    0.159  ns/op
i.h.substrates.jmh.CortexOps.scope                                           avgt    5      8.857 ±    2.034  ns/op
i.h.substrates.jmh.CortexOps.scope_batch                                     avgt    5      7.514 ±    0.016  ns/op
i.h.substrates.jmh.CortexOps.scope_named                                     avgt    5      8.003 ±    0.029  ns/op
i.h.substrates.jmh.CortexOps.slot_boolean                                    avgt    5      2.431 ±    0.253  ns/op
i.h.substrates.jmh.CortexOps.slot_double                                     avgt    5      2.405 ±    0.100  ns/op
i.h.substrates.jmh.CortexOps.slot_int                                        avgt    5      2.342 ±    0.350  ns/op
i.h.substrates.jmh.CortexOps.slot_long                                       avgt    5      2.419 ±    0.308  ns/op
i.h.substrates.jmh.CortexOps.slot_string                                     avgt    5      2.428 ±    0.101  ns/op
i.h.substrates.jmh.CortexOps.state_empty                                     avgt    5      0.441 ±    0.004  ns/op
i.h.substrates.jmh.CortexOps.state_empty_batch                               avgt    5     ≈ 10⁻³             ns/op
i.h.substrates.jmh.CyclicOps.cyclic_emit                                     avgt    5      1.137 ±    0.820  ns/op
i.h.substrates.jmh.CyclicOps.cyclic_emit_await                               avgt    5     10.336 ±    0.056  ns/op
i.h.substrates.jmh.CyclicOps.cyclic_emit_deep_await                          avgt    5      4.294 ±    0.017  ns/op
i.h.substrates.jmh.FlowOps.baseline_no_flow_await                            avgt    5     17.447 ±    0.441  ns/op
i.h.substrates.jmh.FlowOps.flow_combined_diff_guard_await                    avgt    5     26.985 ±    0.473  ns/op
i.h.substrates.jmh.FlowOps.flow_combined_diff_sample_await                   avgt    5     18.650 ±    0.276  ns/op
i.h.substrates.jmh.FlowOps.flow_combined_guard_limit_await                   avgt    5     29.212 ±    1.686  ns/op
i.h.substrates.jmh.FlowOps.flow_diff_await                                   avgt    5     28.855 ±    0.510  ns/op
i.h.substrates.jmh.FlowOps.flow_guard_await                                  avgt    5     28.441 ±    3.076  ns/op
i.h.substrates.jmh.FlowOps.flow_limit_await                                  avgt    5     25.073 ±    1.050  ns/op
i.h.substrates.jmh.FlowOps.flow_sample_await                                 avgt    5     19.294 ±    0.409  ns/op
i.h.substrates.jmh.FlowOps.flow_sift_await                                   avgt    5     18.741 ±    0.581  ns/op
i.h.substrates.jmh.IdOps.id_from_subject                                     avgt    5      0.520 ±    0.027  ns/op
i.h.substrates.jmh.IdOps.id_from_subject_batch                               avgt    5      0.001 ±    0.001  ns/op
i.h.substrates.jmh.IdOps.id_toString                                         avgt    5     13.804 ±    6.292  ns/op
i.h.substrates.jmh.IdOps.id_toString_batch                                   avgt    5     15.525 ±    6.153  ns/op
i.h.substrates.jmh.NameOps.name_chained_deep                                 avgt    5      5.211 ±    0.013  ns/op
i.h.substrates.jmh.NameOps.name_chaining                                     avgt    5      8.306 ±    0.587  ns/op
i.h.substrates.jmh.NameOps.name_chaining_batch                               avgt    5      8.146 ±    0.844  ns/op
i.h.substrates.jmh.NameOps.name_compare                                      avgt    5      0.766 ±    0.001  ns/op
i.h.substrates.jmh.NameOps.name_compare_batch                                avgt    5      0.001 ±    0.001  ns/op
i.h.substrates.jmh.NameOps.name_depth                                        avgt    5      0.506 ±    0.051  ns/op
i.h.substrates.jmh.NameOps.name_depth_batch                                  avgt    5     ≈ 10⁻³             ns/op
i.h.substrates.jmh.NameOps.name_enclosure                                    avgt    5      0.549 ±    0.061  ns/op
i.h.substrates.jmh.NameOps.name_from_enum                                    avgt    5      1.758 ±    0.001  ns/op
i.h.substrates.jmh.NameOps.name_from_iterable                                avgt    5      8.858 ±    0.070  ns/op
i.h.substrates.jmh.NameOps.name_from_iterator                                avgt    5      8.687 ±    0.022  ns/op
i.h.substrates.jmh.NameOps.name_from_mapped_iterable                         avgt    5      9.108 ±    0.062  ns/op
i.h.substrates.jmh.NameOps.name_from_name                                    avgt    5      3.822 ±    0.038  ns/op
i.h.substrates.jmh.NameOps.name_from_string                                  avgt    5      3.105 ±    0.106  ns/op
i.h.substrates.jmh.NameOps.name_from_string_batch                            avgt    5      2.849 ±    0.005  ns/op
i.h.substrates.jmh.NameOps.name_interning_chained                            avgt    5     10.370 ±    0.085  ns/op
i.h.substrates.jmh.NameOps.name_interning_same_path                          avgt    5      3.546 ±    0.007  ns/op
i.h.substrates.jmh.NameOps.name_interning_segments                           avgt    5      8.736 ±    0.701  ns/op
i.h.substrates.jmh.NameOps.name_iterate_hierarchy                            avgt    5      1.662 ±    0.001  ns/op
i.h.substrates.jmh.NameOps.name_parsing                                      avgt    5      1.894 ±    0.001  ns/op
i.h.substrates.jmh.NameOps.name_parsing_batch                                avgt    5      1.682 ±    0.001  ns/op
i.h.substrates.jmh.NameOps.name_path_generation                              avgt    5      0.550 ±    0.078  ns/op
i.h.substrates.jmh.NameOps.name_path_generation_batch                        avgt    5      0.001 ±    0.001  ns/op
i.h.substrates.jmh.PipeOps.async_emit_batch                                  avgt    5      9.740 ±    1.565  ns/op
i.h.substrates.jmh.PipeOps.async_emit_batch_await                            avgt    5     17.523 ±    0.516  ns/op
i.h.substrates.jmh.PipeOps.async_emit_chained_await                          avgt    5     17.218 ±    1.294  ns/op
i.h.substrates.jmh.PipeOps.async_emit_fanout_await                           avgt    5     19.794 ±    1.879  ns/op
i.h.substrates.jmh.PipeOps.async_emit_single                                 avgt    5      8.290 ±    0.469  ns/op
i.h.substrates.jmh.PipeOps.async_emit_single_await                           avgt    5   5663.857 ±  161.665  ns/op
i.h.substrates.jmh.PipeOps.async_emit_with_flow_await                        avgt    5     18.128 ±    0.366  ns/op
i.h.substrates.jmh.PipeOps.baseline_blackhole                                avgt    5      0.267 ±    0.001  ns/op
i.h.substrates.jmh.PipeOps.baseline_counter                                  avgt    5      1.685 ±    0.025  ns/op
i.h.substrates.jmh.PipeOps.baseline_receptor                                 avgt    5      0.267 ±    0.001  ns/op
i.h.substrates.jmh.PipeOps.pipe_create                                       avgt    5      8.571 ±    2.192  ns/op
i.h.substrates.jmh.PipeOps.pipe_create_chained                               avgt    5      0.856 ±    0.105  ns/op
i.h.substrates.jmh.PipeOps.pipe_create_with_flow                             avgt    5     12.446 ±    0.052  ns/op
i.h.substrates.jmh.ReservoirOps.baseline_emit_no_reservoir_await             avgt    5     99.233 ±    4.695  ns/op
i.h.substrates.jmh.ReservoirOps.baseline_emit_no_reservoir_await_batch       avgt    5     16.907 ±    0.527  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_burst_then_drain_await             avgt    5     92.103 ±   19.042  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_burst_then_drain_await_batch       avgt    5     21.985 ±    0.891  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_drain_await                        avgt    5     93.077 ±   12.772  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_drain_await_batch                  avgt    5     21.379 ±    2.545  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_emit_drain_cycles_await            avgt    5    298.029 ±   12.812  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_emit_with_capture_await            avgt    5     75.153 ±   22.091  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_emit_with_capture_await_batch      avgt    5     24.405 ±    0.907  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_process_emissions_await            avgt    5     90.261 ±   29.018  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_process_emissions_await_batch      avgt    5     26.741 ±    2.283  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_process_subjects_await             avgt    5     95.772 ±   11.440  ns/op
i.h.substrates.jmh.ScopeOps.scope_child_anonymous                            avgt    5     19.606 ±    2.318  ns/op
i.h.substrates.jmh.ScopeOps.scope_child_anonymous_batch                      avgt    5     16.650 ±    0.095  ns/op
i.h.substrates.jmh.ScopeOps.scope_child_named                                avgt    5     20.662 ±    6.085  ns/op
i.h.substrates.jmh.ScopeOps.scope_child_named_batch                          avgt    5     18.199 ±    0.691  ns/op
i.h.substrates.jmh.ScopeOps.scope_close_idempotent                           avgt    5      2.437 ±    0.053  ns/op
i.h.substrates.jmh.ScopeOps.scope_close_idempotent_batch                     avgt    5      0.033 ±    0.001  ns/op
i.h.substrates.jmh.ScopeOps.scope_closure                                    avgt    5    274.657 ±   71.934  ns/op
i.h.substrates.jmh.ScopeOps.scope_closure_batch                              avgt    5    292.857 ±   59.318  ns/op
i.h.substrates.jmh.ScopeOps.scope_complex                                    avgt    5    927.206 ±  108.167  ns/op
i.h.substrates.jmh.ScopeOps.scope_create_and_close                           avgt    5      2.492 ±    0.016  ns/op
i.h.substrates.jmh.ScopeOps.scope_create_and_close_batch                     avgt    5      0.034 ±    0.001  ns/op
i.h.substrates.jmh.ScopeOps.scope_create_named                               avgt    5      2.468 ±    0.116  ns/op
i.h.substrates.jmh.ScopeOps.scope_create_named_batch                         avgt    5      0.034 ±    0.001  ns/op
i.h.substrates.jmh.ScopeOps.scope_hierarchy                                  avgt    5     27.059 ±    0.132  ns/op
i.h.substrates.jmh.ScopeOps.scope_hierarchy_batch                            avgt    5     26.528 ±    0.139  ns/op
i.h.substrates.jmh.ScopeOps.scope_parent_closes_children                     avgt    5     42.945 ±    0.377  ns/op
i.h.substrates.jmh.ScopeOps.scope_parent_closes_children_batch               avgt    5     42.169 ±    4.353  ns/op
i.h.substrates.jmh.ScopeOps.scope_register_multiple                          avgt    5   1466.219 ±  454.416  ns/op
i.h.substrates.jmh.ScopeOps.scope_register_multiple_batch                    avgt    5   1388.596 ±  148.708  ns/op
i.h.substrates.jmh.ScopeOps.scope_register_single                            avgt    5    283.107 ±   72.707  ns/op
i.h.substrates.jmh.ScopeOps.scope_register_single_batch                      avgt    5    280.978 ±   78.512  ns/op
i.h.substrates.jmh.ScopeOps.scope_with_resources                             avgt    5    576.095 ±  124.917  ns/op
i.h.substrates.jmh.StateOps.slot_name                                        avgt    5      0.521 ±    0.017  ns/op
i.h.substrates.jmh.StateOps.slot_name_batch                                  avgt    5      0.001 ±    0.001  ns/op
i.h.substrates.jmh.StateOps.slot_type                                        avgt    5      0.495 ±    0.001  ns/op
i.h.substrates.jmh.StateOps.slot_value                                       avgt    5      0.639 ±    0.001  ns/op
i.h.substrates.jmh.StateOps.slot_value_batch                                 avgt    5      0.001 ±    0.001  ns/op
i.h.substrates.jmh.StateOps.state_compact                                    avgt    5     10.303 ±    0.191  ns/op
i.h.substrates.jmh.StateOps.state_compact_batch                              avgt    5     10.692 ±    0.119  ns/op
i.h.substrates.jmh.StateOps.state_iterate_slots                              avgt    5      2.169 ±    0.013  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_int                               avgt    5      4.522 ±    1.305  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_int_batch                         avgt    5      4.913 ±    0.248  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_long                              avgt    5      4.765 ±    0.447  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_object                            avgt    5      2.589 ±    0.598  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_object_batch                      avgt    5      2.386 ±    0.002  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_string                            avgt    5      4.682 ±    0.651  ns/op
i.h.substrates.jmh.StateOps.state_value_read                                 avgt    5      1.486 ±    0.001  ns/op
i.h.substrates.jmh.StateOps.state_value_read_batch                           avgt    5      1.266 ±    0.001  ns/op
i.h.substrates.jmh.StateOps.state_values_stream                              avgt    5      4.784 ±    0.656  ns/op
i.h.substrates.jmh.SubjectOps.subject_compare                                avgt    5      3.902 ±    0.002  ns/op
i.h.substrates.jmh.SubjectOps.subject_compare_batch                          avgt    5      2.487 ±    0.001  ns/op
i.h.substrates.jmh.SubjectOps.subject_compare_same                           avgt    5      0.494 ±    0.008  ns/op
i.h.substrates.jmh.SubjectOps.subject_compare_same_batch                     avgt    5     ≈ 10⁻³             ns/op
i.h.substrates.jmh.SubjectOps.subject_compare_three_way                      avgt    5     11.005 ±    0.070  ns/op
i.h.substrates.jmh.SubscriberOps.close_five_conduits_await                   avgt    5   8744.114 ±  486.782  ns/op
i.h.substrates.jmh.SubscriberOps.close_five_subscriptions_await              avgt    5   8694.724 ±  379.348  ns/op
i.h.substrates.jmh.SubscriberOps.close_idempotent_await                      avgt    5   8478.961 ±  262.104  ns/op
i.h.substrates.jmh.SubscriberOps.close_idempotent_batch_await                avgt    5     17.023 ±    0.610  ns/op
i.h.substrates.jmh.SubscriberOps.close_no_subscriptions_await                avgt    5   8509.750 ±  837.759  ns/op
i.h.substrates.jmh.SubscriberOps.close_no_subscriptions_batch_await          avgt    5     14.201 ±    1.890  ns/op
i.h.substrates.jmh.SubscriberOps.close_one_subscription_await                avgt    5   8425.655 ±  570.224  ns/op
i.h.substrates.jmh.SubscriberOps.close_one_subscription_batch_await          avgt    5     33.328 ±    0.519  ns/op
i.h.substrates.jmh.SubscriberOps.close_ten_conduits_await                    avgt    5   8389.116 ±  372.590  ns/op
i.h.substrates.jmh.SubscriberOps.close_ten_subscriptions_await               avgt    5   8548.523 ±  317.388  ns/op
i.h.substrates.jmh.SubscriberOps.close_with_pending_emissions_await          avgt    5   8701.859 ± 1174.809  ns/op
i.h.substrates.jmh.TapOps.baseline_emit_batch_await                          avgt    5     18.999 ±    0.978  ns/op
i.h.substrates.jmh.TapOps.tap_close                                          avgt    5   8272.484 ±  379.513  ns/op
i.h.substrates.jmh.TapOps.tap_create_batch                                   avgt    5    662.873 ±  847.144  ns/op
i.h.substrates.jmh.TapOps.tap_create_identity                                avgt    5    704.780 ±  870.750  ns/op
i.h.substrates.jmh.TapOps.tap_create_string                                  avgt    5    689.249 ±  662.325  ns/op
i.h.substrates.jmh.TapOps.tap_emit_identity_batch_await                      avgt    5     29.145 ±    4.186  ns/op
i.h.substrates.jmh.TapOps.tap_emit_identity_single                           avgt    5     36.221 ±    8.827  ns/op
i.h.substrates.jmh.TapOps.tap_emit_identity_single_await                     avgt    5   7902.243 ±  809.399  ns/op
i.h.substrates.jmh.TapOps.tap_emit_multi_batch_await                         avgt    5     42.771 ±    2.323  ns/op
i.h.substrates.jmh.TapOps.tap_emit_string_batch_await                        avgt    5     39.313 ±    2.717  ns/op
i.h.substrates.jmh.TapOps.tap_lifecycle                                      avgt    5  16192.630 ± 2429.759  ns/op


Fullerstack Substrates (RC1) SPI: https://github.com/fullerstack-io/fullerstack-humainary

Benchmark                                                                    Mode  Cnt     Score     Error  Units
i.h.serventis.jmh.opt.data.CacheOps.cache_from_conduit                       avgt    5     0.818 ±   0.030  ns/op
i.h.serventis.jmh.opt.data.CacheOps.cache_from_conduit_batch                 avgt    5     0.628 ±   0.013  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_evict                               avgt    5    19.351 ±  20.216  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_evict_batch                         avgt    5    12.636 ±   8.520  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_expire                              avgt    5    18.307 ±  12.175  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_expire_batch                        avgt    5    17.069 ±   8.306  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_hit                                 avgt    5    16.708 ±  13.652  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_hit_batch                           avgt    5    20.632 ±  14.591  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_lookup                              avgt    5    18.806 ±  24.789  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_lookup_batch                        avgt    5    21.403 ±  31.472  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_miss                                avgt    5    15.889 ±  22.848  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_miss_batch                          avgt    5    19.171 ±  32.105  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_remove                              avgt    5    20.676 ±  10.093  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_remove_batch                        avgt    5     6.045 ±   2.507  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_sign                                avgt    5    17.292 ±  13.808  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_sign_batch                          avgt    5    12.077 ±   6.589  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_store                               avgt    5    15.483 ±  26.227  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_store_batch                         avgt    5     8.478 ±  12.064  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_aggregate                        avgt    5    16.484 ±  16.144  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_aggregate_batch                  avgt    5    19.125 ±  18.470  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_backpressure                     avgt    5    16.334 ±  18.511  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_backpressure_batch               avgt    5    19.780 ±  28.656  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_buffer                           avgt    5    13.509 ±   7.034  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_buffer_batch                     avgt    5    20.549 ±  18.006  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_checkpoint                       avgt    5    16.847 ±  17.985  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_checkpoint_batch                 avgt    5    15.161 ±  12.507  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_filter                           avgt    5    13.707 ±  12.577  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_filter_batch                     avgt    5    21.361 ±  25.247  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_input                            avgt    5    20.002 ±  16.924  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_input_batch                      avgt    5    15.642 ±  18.889  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_lag                              avgt    5    13.643 ±  11.849  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_lag_batch                        avgt    5    19.217 ±  14.950  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_output                           avgt    5    16.809 ±  21.599  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_output_batch                     avgt    5    14.832 ±  11.313  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_overflow                         avgt    5    20.312 ±  24.713  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_overflow_batch                   avgt    5    15.042 ±  12.260  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_sign                             avgt    5    17.304 ±  22.363  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_sign_batch                       avgt    5    13.007 ±  14.738  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_skip                             avgt    5    25.766 ±  21.205  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_skip_batch                       avgt    5    13.491 ±   3.720  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_transform                        avgt    5    18.972 ±  11.143  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_transform_batch                  avgt    5    11.726 ±   7.827  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_watermark                        avgt    5    23.187 ±   9.776  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_watermark_batch                  avgt    5    15.097 ±  20.861  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_flow_etl                     avgt    5    52.348 ±  38.561  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_flow_stream                  avgt    5    46.174 ±  17.618  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_flow_windowed                avgt    5    51.562 ±  32.346  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_from_conduit                 avgt    5     0.847 ±   0.055  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_from_conduit_batch           avgt    5     0.646 ±   0.001  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_dequeue                             avgt    5    16.740 ±  11.674  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_dequeue_batch                       avgt    5    19.692 ±  25.601  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_enqueue                             avgt    5    20.452 ±  25.762  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_enqueue_batch                       avgt    5    10.186 ±  11.559  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_overflow                            avgt    5    25.490 ±  21.664  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_overflow_batch                      avgt    5     9.080 ±  18.615  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_sign                                avgt    5    18.098 ±  18.529  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_sign_batch                          avgt    5    16.742 ±  11.870  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_underflow                           avgt    5    15.065 ±  12.377  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_underflow_batch                     avgt    5     8.666 ±  11.630  ns/op
i.h.serventis.jmh.opt.data.QueueOps.queue_from_conduit                       avgt    5     0.842 ±   0.001  ns/op
i.h.serventis.jmh.opt.data.QueueOps.queue_from_conduit_batch                 avgt    5     0.646 ±   0.003  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_overflow                            avgt    5    20.170 ±  23.933  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_overflow_batch                      avgt    5    11.284 ±  12.566  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_pop                                 avgt    5    15.551 ±  12.836  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_pop_batch                           avgt    5    16.535 ±  18.596  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_push                                avgt    5    14.424 ±   7.721  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_push_batch                          avgt    5    12.815 ±  11.790  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_sign                                avgt    5    12.904 ±  11.825  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_sign_batch                          avgt    5    17.210 ±  23.760  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_underflow                           avgt    5    15.507 ±   8.619  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_underflow_batch                     avgt    5    12.238 ±   8.196  ns/op
i.h.serventis.jmh.opt.data.StackOps.stack_from_conduit                       avgt    5     0.854 ±   0.062  ns/op
i.h.serventis.jmh.opt.data.StackOps.stack_from_conduit_batch                 avgt    5     0.646 ±   0.001  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_crash                             avgt    5    16.779 ±  24.185  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_crash_batch                       avgt    5    21.459 ±  31.677  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_fail                              avgt    5    14.545 ±   9.987  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_fail_batch                        avgt    5    13.557 ±  11.328  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_kill                              avgt    5    17.611 ±  26.134  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_kill_batch                        avgt    5    10.900 ±  12.163  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_restart                           avgt    5    20.705 ±  22.334  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_restart_batch                     avgt    5    22.224 ±  22.457  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_resume                            avgt    5    16.096 ±  17.922  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_resume_batch                      avgt    5    13.228 ±  12.229  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_sign                              avgt    5    26.567 ±   7.435  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_sign_batch                        avgt    5    16.037 ±  11.388  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_spawn                             avgt    5    14.265 ±  14.543  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_spawn_batch                       avgt    5    17.136 ±   6.361  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_start                             avgt    5    20.192 ±  24.933  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_start_batch                       avgt    5    15.837 ±  14.958  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_stop                              avgt    5     9.492 ±  11.522  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_stop_batch                        avgt    5    17.224 ±  17.763  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_suspend                           avgt    5    19.479 ±  23.386  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_suspend_batch                     avgt    5    13.200 ±  12.608  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.process_from_conduit                   avgt    5     0.860 ±   0.061  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.process_from_conduit_batch             avgt    5     0.769 ±   0.001  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_call                              avgt    5    15.494 ±   8.620  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_call_batch                        avgt    5    12.576 ±   9.610  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_called                            avgt    5    12.656 ±   9.462  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_called_batch                      avgt    5    14.305 ±  11.778  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_delay                             avgt    5    14.613 ±   7.958  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_delay_batch                       avgt    5    16.125 ±  37.039  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_delayed                           avgt    5    11.243 ±  11.643  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_delayed_batch                     avgt    5    19.945 ±  21.034  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_discard                           avgt    5     9.899 ±   8.821  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_discard_batch                     avgt    5    19.118 ±  24.822  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_discarded                         avgt    5    15.056 ±  18.553  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_discarded_batch                   avgt    5    19.801 ±  10.368  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_disconnect                        avgt    5    10.967 ±  10.860  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_disconnect_batch                  avgt    5    19.132 ±  23.301  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_disconnected                      avgt    5    14.522 ±  14.158  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_disconnected_batch                avgt    5    16.633 ±  25.180  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_expire                            avgt    5    13.513 ±   9.174  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_expire_batch                      avgt    5     9.511 ±  11.592  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_expired                           avgt    5    12.523 ±  13.180  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_expired_batch                     avgt    5    23.427 ±  12.945  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_fail                              avgt    5    16.524 ±  17.618  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_fail_batch                        avgt    5     8.891 ±  10.772  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_failed                            avgt    5    13.858 ±  12.234  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_failed_batch                      avgt    5    20.110 ±  27.006  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_recourse                          avgt    5    14.357 ±   7.662  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_recourse_batch                    avgt    5    13.529 ±  12.011  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_recoursed                         avgt    5    13.027 ±  19.624  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_recoursed_batch                   avgt    5    13.374 ±  16.498  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_redirect                          avgt    5    12.440 ±   9.067  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_redirect_batch                    avgt    5    15.279 ±  12.028  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_redirected                        avgt    5    12.956 ±   7.353  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_redirected_batch                  avgt    5    13.875 ±  18.937  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_reject                            avgt    5    11.782 ±   5.429  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_reject_batch                      avgt    5    14.690 ±  19.221  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_rejected                          avgt    5    14.254 ±  23.117  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_rejected_batch                    avgt    5    16.479 ±   7.933  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_resume                            avgt    5    13.451 ±  16.849  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_resume_batch                      avgt    5    16.874 ±   5.468  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_resumed                           avgt    5    20.371 ±  26.574  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_resumed_batch                     avgt    5    12.205 ±   9.989  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_retried                           avgt    5    14.607 ±   5.166  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_retried_batch                     avgt    5    15.511 ±   3.495  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_retry                             avgt    5    14.401 ±   5.258  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_retry_batch                       avgt    5    18.541 ±  16.713  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_schedule                          avgt    5    17.979 ±  19.222  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_schedule_batch                    avgt    5    16.873 ±  20.394  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_scheduled                         avgt    5    14.708 ±   4.056  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_scheduled_batch                   avgt    5    15.325 ±   2.631  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_signal                            avgt    5    17.180 ±  25.109  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_signal_batch                      avgt    5    12.777 ±  25.949  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_start                             avgt    5    14.656 ±  12.839  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_start_batch                       avgt    5    13.336 ±  12.097  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_started                           avgt    5    12.239 ±   8.133  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_started_batch                     avgt    5    10.179 ±   9.104  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_stop                              avgt    5    15.112 ±   8.714  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_stop_batch                        avgt    5    18.373 ±  24.172  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_stopped                           avgt    5    13.034 ±   9.944  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_stopped_batch                     avgt    5    21.606 ±  25.879  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_succeeded                         avgt    5    25.198 ±  27.032  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_succeeded_batch                   avgt    5    17.561 ±  15.578  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_success                           avgt    5    12.031 ±   6.465  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_success_batch                     avgt    5    18.687 ±  18.953  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_suspend                           avgt    5    15.527 ±  14.531  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_suspend_batch                     avgt    5    17.774 ±  25.789  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_suspended                         avgt    5    13.845 ±   2.447  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_suspended_batch                   avgt    5    15.474 ±  25.143  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.service_from_conduit                   avgt    5     0.863 ±   0.089  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.service_from_conduit_batch             avgt    5     0.647 ±   0.003  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_cancel                               avgt    5    16.063 ±  23.202  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_cancel_batch                         avgt    5    16.815 ±  20.272  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_complete                             avgt    5    20.590 ±  31.627  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_complete_batch                       avgt    5    20.005 ±  26.411  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_fail                                 avgt    5    20.386 ±  26.481  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_fail_batch                           avgt    5    16.423 ±   7.590  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_progress                             avgt    5    13.852 ±  16.448  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_progress_batch                       avgt    5    22.502 ±  28.648  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_reject                               avgt    5    16.419 ±  18.318  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_reject_batch                         avgt    5    12.642 ±   8.279  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_resume                               avgt    5    17.104 ±  22.071  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_resume_batch                         avgt    5    11.792 ±   8.833  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_schedule                             avgt    5    21.727 ±  33.486  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_schedule_batch                       avgt    5    14.031 ±   4.042  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_sign                                 avgt    5    17.725 ±  27.744  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_sign_batch                           avgt    5    12.764 ±  16.662  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_start                                avgt    5    14.524 ±  13.065  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_start_batch                          avgt    5    15.181 ±  16.675  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_submit                               avgt    5    14.068 ±  11.110  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_submit_batch                         avgt    5    14.210 ±  20.876  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_suspend                              avgt    5    20.073 ±  27.187  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_suspend_batch                        avgt    5    13.241 ±  11.520  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_timeout                              avgt    5    12.389 ±  10.343  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_timeout_batch                        avgt    5    11.402 ±   8.652  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.task_from_conduit                         avgt    5     0.853 ±   0.045  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.task_from_conduit_batch                   avgt    5     0.645 ±   0.003  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_meet_deadline                       avgt    5    15.690 ±  10.195  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_meet_deadline_batch                 avgt    5    19.202 ±  28.252  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_meet_threshold                      avgt    5    12.836 ±  19.046  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_meet_threshold_batch                avgt    5    13.984 ±  15.113  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_miss_deadline                       avgt    5    14.488 ±   4.243  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_miss_deadline_batch                 avgt    5    14.884 ±   8.593  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_miss_threshold                      avgt    5    12.984 ±  12.234  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_miss_threshold_batch                avgt    5    23.999 ±  25.861  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_signal                              avgt    5    15.771 ±  29.014  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_signal_batch                        avgt    5    18.429 ±  17.221  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.timer_from_conduit                       avgt    5     0.860 ±   0.064  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.timer_from_conduit_batch                 avgt    5     0.648 ±   0.003  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_abort_coordinator             avgt    5    12.027 ±  13.701  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_abort_coordinator_batch       avgt    5    14.883 ±  11.723  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_abort_participant             avgt    5    13.344 ±  13.650  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_abort_participant_batch       avgt    5    12.543 ±   5.593  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_commit_coordinator            avgt    5    11.574 ±   8.162  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_commit_coordinator_batch      avgt    5    20.744 ±  33.060  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_commit_participant            avgt    5    14.879 ±  14.890  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_commit_participant_batch      avgt    5    16.820 ±  13.460  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_compensate_coordinator        avgt    5    12.789 ±  15.100  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_compensate_coordinator_batch  avgt    5    17.160 ±  24.809  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_compensate_participant        avgt    5    15.561 ±  14.261  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_compensate_participant_batch  avgt    5    15.486 ±   7.438  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_conflict_coordinator          avgt    5     9.825 ±   4.832  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_conflict_coordinator_batch    avgt    5     6.506 ±   9.331  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_conflict_participant          avgt    5    13.384 ±   6.427  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_conflict_participant_batch    avgt    5    15.413 ±  11.635  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_expire_coordinator            avgt    5    12.250 ±  11.855  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_expire_coordinator_batch      avgt    5    17.438 ±  11.473  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_expire_participant            avgt    5    16.688 ±  19.761  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_expire_participant_batch      avgt    5    15.073 ±   9.487  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_prepare_coordinator           avgt    5    10.921 ±   7.480  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_prepare_coordinator_batch     avgt    5    14.417 ±   5.854  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_prepare_participant           avgt    5    10.499 ±   2.833  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_prepare_participant_batch     avgt    5     6.260 ±   3.399  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_rollback_coordinator          avgt    5    12.484 ±  10.632  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_rollback_coordinator_batch    avgt    5    15.972 ±  23.814  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_rollback_participant          avgt    5    15.198 ±   4.109  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_rollback_participant_batch    avgt    5    17.239 ±  27.240  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_signal                        avgt    5    13.092 ±  13.611  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_signal_batch                  avgt    5    18.107 ±  14.264  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_start_coordinator             avgt    5    14.528 ±   8.699  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_start_coordinator_batch       avgt    5    23.756 ±  17.787  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_start_participant             avgt    5    14.176 ±  12.846  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_start_participant_batch       avgt    5    19.522 ±  21.649  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.transaction_from_conduit           avgt    5     0.853 ±   0.066  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.transaction_from_conduit_batch     avgt    5     0.646 ±   0.004  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.breaker_from_conduit                   avgt    5     0.854 ±   0.060  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.breaker_from_conduit_batch             avgt    5     0.646 ±   0.001  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_close                             avgt    5     9.535 ±   8.957  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_close_batch                       avgt    5    10.729 ±  11.199  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_half_open                         avgt    5    13.455 ±  23.140  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_half_open_batch                   avgt    5    15.828 ±  10.839  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_open                              avgt    5    22.430 ±  22.079  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_open_batch                        avgt    5     8.415 ±  14.565  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_probe                             avgt    5     9.929 ±  13.286  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_probe_batch                       avgt    5    16.368 ±   6.964  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_reset                             avgt    5    13.180 ±   6.862  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_reset_batch                       avgt    5    15.362 ±   5.045  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_sign                              avgt    5    13.328 ±  12.854  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_sign_batch                        avgt    5    16.429 ±  13.643  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_trip                              avgt    5    19.638 ±  25.703  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_trip_batch                        avgt    5    22.222 ±  22.453  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_fail_egress                          avgt    5    12.866 ±   8.706  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_fail_egress_batch                    avgt    5    12.813 ±   8.027  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_fail_ingress                         avgt    5    15.585 ±   3.187  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_fail_ingress_batch                   avgt    5    10.300 ±   8.866  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_fail_transit                         avgt    5    14.077 ±   3.380  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_fail_transit_batch                   avgt    5    13.915 ±   9.584  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_signal                               avgt    5    10.315 ±  14.428  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_signal_batch                         avgt    5    20.724 ±  20.896  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_success_egress                       avgt    5    13.962 ±   9.134  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_success_egress_batch                 avgt    5    14.675 ±   2.449  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_success_ingress                      avgt    5    17.612 ±  12.177  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_success_ingress_batch                avgt    5    18.164 ±  22.015  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_success_transit                      avgt    5    13.383 ±   8.154  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_success_transit_batch                avgt    5    17.953 ±  21.834  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.flow_from_conduit                         avgt    5     0.860 ±   0.061  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.flow_from_conduit_batch                   avgt    5     0.647 ±   0.009  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_corrupt                            avgt    5    19.275 ±  14.868  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_corrupt_batch                      avgt    5    14.511 ±  10.592  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_drop                               avgt    5    15.606 ±   7.984  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_drop_batch                         avgt    5    16.720 ±  29.944  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_forward                            avgt    5    17.855 ±  35.716  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_forward_batch                      avgt    5    17.247 ±  18.682  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_fragment                           avgt    5    18.462 ±  26.894  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_fragment_batch                     avgt    5    12.220 ±   8.750  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_reassemble                         avgt    5    19.678 ±  18.013  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_reassemble_batch                   avgt    5    17.020 ±  26.324  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_receive                            avgt    5    17.775 ±  29.405  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_receive_batch                      avgt    5    14.967 ±  15.824  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_reorder                            avgt    5    16.025 ±  21.325  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_reorder_batch                      avgt    5    19.344 ±  31.845  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_route                              avgt    5    17.554 ±  11.847  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_route_batch                        avgt    5    16.226 ±   8.894  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_send                               avgt    5    14.366 ±   3.197  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_send_batch                         avgt    5    18.012 ±  20.956  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_sign                               avgt    5    17.236 ±  19.009  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_sign_batch                         avgt    5    22.392 ±  31.042  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.router_from_conduit                     avgt    5     0.842 ±   0.001  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.router_from_conduit_batch               avgt    5     0.647 ±   0.004  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_contract                            avgt    5    21.040 ±  28.507  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_contract_batch                      avgt    5    20.609 ±  25.935  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_deny                                avgt    5    18.481 ±  30.271  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_deny_batch                          avgt    5    19.536 ±  30.545  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_drain                               avgt    5    14.218 ±  21.206  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_drain_batch                         avgt    5    16.936 ±  11.627  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_drop                                avgt    5    16.173 ±  14.590  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_drop_batch                          avgt    5    18.773 ±  19.848  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_expand                              avgt    5    19.132 ±  23.750  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_expand_batch                        avgt    5     9.770 ±  10.230  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_pass                                avgt    5    13.265 ±  18.367  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_pass_batch                          avgt    5    23.628 ±  28.910  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_sign                                avgt    5    12.857 ±  13.318  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_sign_batch                          avgt    5    19.922 ±  13.282  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.valve_from_conduit                       avgt    5     0.863 ±   0.076  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.valve_from_conduit_batch                 avgt    5     0.646 ±   0.001  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_contract_provider                avgt    5    14.244 ±  20.132  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_contract_provider_batch          avgt    5    16.426 ±  14.682  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_contract_receiver                avgt    5    15.188 ±  10.436  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_contract_receiver_batch          avgt    5    19.575 ±  19.976  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_full_exchange                    avgt    5     8.046 ±   6.722  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_full_exchange_batch              avgt    5    12.662 ±   4.891  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_signal                           avgt    5    14.165 ±   3.975  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_signal_batch                     avgt    5    15.113 ±   9.346  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_transfer_provider                avgt    5    11.749 ±   6.858  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_transfer_provider_batch          avgt    5    17.095 ±  15.321  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_transfer_receiver                avgt    5    13.190 ±  22.470  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_transfer_receiver_batch          avgt    5    12.043 ±  14.601  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.exchange_from_conduit                 avgt    5     0.865 ±   0.048  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.exchange_from_conduit_batch           avgt    5     0.646 ±   0.001  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_acquire                             avgt    5    15.309 ±   5.477  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_acquire_batch                       avgt    5    18.777 ±  30.019  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_acquired                            avgt    5    14.276 ±   7.472  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_acquired_batch                      avgt    5    16.683 ±  28.087  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_denied                              avgt    5    14.790 ±   0.858  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_denied_batch                        avgt    5    11.156 ±   7.841  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_deny                                avgt    5    13.288 ±   4.800  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_deny_batch                          avgt    5     9.489 ±  10.145  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_expire                              avgt    5    12.315 ±  12.374  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_expire_batch                        avgt    5     4.905 ±   0.650  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_expired                             avgt    5    13.014 ±  10.341  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_expired_batch                       avgt    5    16.168 ±   9.874  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_extend                              avgt    5    17.943 ±  16.760  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_extend_batch                        avgt    5    14.335 ±  10.753  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_extended                            avgt    5    15.022 ±  18.455  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_extended_batch                      avgt    5    16.240 ±   5.023  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_grant                               avgt    5     9.904 ±   8.858  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_grant_batch                         avgt    5    20.144 ±  26.131  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_granted                             avgt    5    16.422 ±  11.651  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_granted_batch                       avgt    5    12.126 ±  18.768  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_probe                               avgt    5    14.214 ±   6.791  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_probe_batch                         avgt    5    13.025 ±  11.684  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_probed                              avgt    5    17.230 ±   7.906  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_probed_batch                        avgt    5    17.031 ±  20.535  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_release                             avgt    5    17.743 ±  17.689  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_release_batch                       avgt    5    17.692 ±  13.933  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_released                            avgt    5    18.071 ±  32.375  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_released_batch                      avgt    5    17.693 ±  22.208  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_renew                               avgt    5    13.375 ±   8.585  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_renew_batch                         avgt    5    21.350 ±  27.243  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_renewed                             avgt    5    11.842 ±  10.021  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_renewed_batch                       avgt    5    15.645 ±  21.688  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_revoke                              avgt    5    17.778 ±  26.864  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_revoke_batch                        avgt    5    12.131 ±  13.460  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_revoked                             avgt    5    23.213 ±  28.742  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_revoked_batch                       avgt    5    15.943 ±  21.261  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_signal                              avgt    5    15.703 ±   6.398  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_signal_batch                        avgt    5    15.396 ±  21.178  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.lease_from_conduit                       avgt    5     0.842 ±   0.001  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.lease_from_conduit_batch                 avgt    5     0.646 ±   0.002  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_borrow                               avgt    5    17.774 ±   5.988  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_borrow_batch                         avgt    5    10.214 ±  13.957  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_contract                             avgt    5    19.345 ±  21.151  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_contract_batch                       avgt    5    20.941 ±  21.775  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_expand                               avgt    5    20.803 ±  32.343  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_expand_batch                         avgt    5    14.763 ±  28.231  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_reclaim                              avgt    5    20.474 ±  22.530  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_reclaim_batch                        avgt    5    10.714 ±  14.904  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_sign                                 avgt    5     8.760 ±  10.590  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_sign_batch                           avgt    5    13.700 ±   6.632  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.pool_from_conduit                         avgt    5     0.842 ±   0.001  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.pool_from_conduit_batch                   avgt    5     0.645 ±   0.001  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_acquire                          avgt    5    20.530 ±  24.474  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_acquire_batch                    avgt    5    14.697 ±  29.356  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_attempt                          avgt    5    25.881 ±  21.347  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_attempt_batch                    avgt    5    16.838 ±  11.543  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_deny                             avgt    5    16.587 ±  16.348  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_deny_batch                       avgt    5    16.227 ±  21.012  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_grant                            avgt    5    18.222 ±  17.504  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_grant_batch                      avgt    5    19.032 ±  35.781  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_release                          avgt    5    22.720 ±  20.967  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_release_batch                    avgt    5    17.204 ±  17.471  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_sign                             avgt    5    17.970 ±   9.922  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_sign_batch                       avgt    5    17.153 ±   7.263  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_timeout                          avgt    5    17.499 ±  21.633  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_timeout_batch                    avgt    5    10.976 ±  10.823  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.resource_from_conduit                 avgt    5     0.845 ±   0.034  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.resource_from_conduit_batch           avgt    5     0.646 ±   0.003  ns/op
i.h.serventis.jmh.opt.role.ActorOps.actor_from_conduit                       avgt    5     0.841 ±   0.009  ns/op
i.h.serventis.jmh.opt.role.ActorOps.actor_from_conduit_batch                 avgt    5     0.645 ±   0.001  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_acknowledge                         avgt    5    17.103 ±  11.741  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_acknowledge_batch                   avgt    5    18.510 ±  19.613  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_affirm                              avgt    5    20.886 ±  24.778  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_affirm_batch                        avgt    5    12.056 ±  14.097  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_ask                                 avgt    5    16.442 ±  23.874  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_ask_batch                           avgt    5    22.720 ±  17.576  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_clarify                             avgt    5    20.079 ±  23.436  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_clarify_batch                       avgt    5    12.037 ±  15.059  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_command                             avgt    5    19.599 ±  15.675  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_command_batch                       avgt    5    17.690 ±  22.094  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_deliver                             avgt    5    19.749 ±  14.347  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_deliver_batch                       avgt    5    17.092 ±  22.870  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_deny                                avgt    5    21.106 ±  16.920  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_deny_batch                          avgt    5    20.787 ±  28.976  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_explain                             avgt    5    14.852 ±   7.678  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_explain_batch                       avgt    5    12.181 ±  10.369  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_promise                             avgt    5    18.604 ±  29.911  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_promise_batch                       avgt    5    16.577 ±   8.624  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_report                              avgt    5    15.277 ±   6.252  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_report_batch                        avgt    5   111.548 ± 342.088  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_request                             avgt    5    18.982 ±  29.625  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_request_batch                       avgt    5    17.557 ±  12.534  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_sign                                avgt    5    17.165 ±  21.660  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_sign_batch                          avgt    5    19.956 ±   8.312  ns/op
i.h.serventis.jmh.opt.role.AgentOps.agent_from_conduit                       avgt    5     0.846 ±   0.029  ns/op
i.h.serventis.jmh.opt.role.AgentOps.agent_from_conduit_batch                 avgt    5     0.646 ±   0.004  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_accept                              avgt    5    19.889 ±  10.334  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_accept_batch                        avgt    5    13.149 ±  30.239  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_accepted                            avgt    5    12.370 ±  10.694  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_accepted_batch                      avgt    5    14.223 ±   7.632  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_breach                              avgt    5    23.020 ±  26.611  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_breach_batch                        avgt    5    13.158 ±  20.089  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_breached                            avgt    5    14.335 ±  15.113  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_breached_batch                      avgt    5    15.728 ±  16.208  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_depend                              avgt    5    13.206 ±   9.420  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_depend_batch                        avgt    5    17.788 ±  18.602  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_depended                            avgt    5    17.204 ±   8.965  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_depended_batch                      avgt    5    12.398 ±   9.187  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_fulfill                             avgt    5     7.873 ±   6.296  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_fulfill_batch                       avgt    5    16.100 ±  17.561  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_fulfilled                           avgt    5    11.847 ±  10.707  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_fulfilled_batch                     avgt    5    11.652 ±  12.146  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_inquire                             avgt    5    13.241 ±   5.706  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_inquire_batch                       avgt    5    17.055 ±  32.447  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_inquired                            avgt    5    10.379 ±   8.163  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_inquired_batch                      avgt    5    17.549 ±  11.411  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_observe                             avgt    5    13.389 ±  10.347  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_observe_batch                       avgt    5    17.694 ±   7.421  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_observed                            avgt    5    13.955 ±  19.540  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_observed_batch                      avgt    5    16.600 ±   6.133  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_offer                               avgt    5    16.101 ±  13.626  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_offer_batch                         avgt    5    20.695 ±  20.391  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_offered                             avgt    5    15.380 ±  22.150  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_offered_batch                       avgt    5    20.093 ±   6.522  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_promise                             avgt    5    14.230 ±   9.715  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_promise_batch                       avgt    5    14.813 ±   2.035  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_promised                            avgt    5     9.802 ±  14.037  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_promised_batch                      avgt    5    20.921 ±   9.538  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_retract                             avgt    5    11.677 ±  12.798  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_retract_batch                       avgt    5    14.785 ±  10.611  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_retracted                           avgt    5    15.037 ±  18.759  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_retracted_batch                     avgt    5    13.826 ±  21.906  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_signal                              avgt    5    16.456 ±  19.523  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_signal_batch                        avgt    5    16.134 ±  28.291  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_validate                            avgt    5    11.057 ±   8.106  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_validate_batch                      avgt    5    13.668 ±  13.491  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_validated                           avgt    5    20.453 ±  31.147  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_validated_batch                     avgt    5    19.801 ±  17.924  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.atomic_from_conduit                     avgt    5     0.853 ±   0.054  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.atomic_from_conduit_batch               avgt    5     0.646 ±   0.005  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_attempt                            avgt    5    14.564 ±  10.746  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_attempt_batch                      avgt    5    15.345 ±   8.064  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_backoff                            avgt    5    18.203 ±   9.378  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_backoff_batch                      avgt    5    14.452 ±  27.084  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_exhaust                            avgt    5    19.168 ±  27.333  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_exhaust_batch                      avgt    5    22.271 ±  16.848  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_fail                               avgt    5    18.154 ±  27.370  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_fail_batch                         avgt    5    16.953 ±  19.670  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_park                               avgt    5    22.170 ±  17.251  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_park_batch                         avgt    5    16.485 ±  24.310  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_sign                               avgt    5    19.486 ±  27.119  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_sign_batch                         avgt    5    15.594 ±  19.688  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_spin                               avgt    5    15.973 ±  26.927  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_spin_batch                         avgt    5    11.400 ±   9.210  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_success                            avgt    5    15.346 ±  25.364  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_success_batch                      avgt    5    16.635 ±  16.540  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_yield                              avgt    5    15.556 ±  13.281  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_yield_batch                        avgt    5    23.659 ±  26.221  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_abandon                             avgt    5    15.060 ±   6.792  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_abandon_batch                       avgt    5    18.276 ±  11.197  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_arrive                              avgt    5    22.206 ±  25.689  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_arrive_batch                        avgt    5    17.126 ±   3.994  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_await                               avgt    5    15.401 ±  16.927  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_await_batch                         avgt    5    14.321 ±  20.620  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_release                             avgt    5    15.504 ±   6.316  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_release_batch                       avgt    5    15.041 ±  16.243  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_reset                               avgt    5    17.241 ±  40.007  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_reset_batch                         avgt    5    17.626 ±  13.892  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_sign                                avgt    5    23.330 ±  33.475  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_sign_batch                          avgt    5    16.835 ±  20.540  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_timeout                             avgt    5    19.817 ±  20.473  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_timeout_batch                       avgt    5    19.338 ±  35.270  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.latch_from_conduit                       avgt    5     0.853 ±   0.087  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.latch_from_conduit_batch                 avgt    5     0.645 ±   0.001  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_abandon                              avgt    5    25.167 ±  31.147  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_abandon_batch                        avgt    5    13.437 ±   2.652  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_acquire                              avgt    5    13.821 ±  12.853  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_acquire_batch                        avgt    5    17.906 ±  15.757  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_attempt                              avgt    5    24.928 ±  15.360  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_attempt_batch                        avgt    5    20.656 ±  26.810  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_contest                              avgt    5    15.264 ±  21.098  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_contest_batch                        avgt    5    22.122 ±  19.607  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_deny                                 avgt    5    14.476 ±  10.750  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_deny_batch                           avgt    5    18.218 ±  21.898  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_downgrade                            avgt    5    23.012 ±  28.481  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_downgrade_batch                      avgt    5    19.431 ±  29.656  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_grant                                avgt    5    18.110 ±  24.152  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_grant_batch                          avgt    5     9.010 ±  21.103  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_release                              avgt    5    17.780 ±   9.873  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_release_batch                        avgt    5    15.224 ±   7.465  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_sign                                 avgt    5    13.213 ±  15.584  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_sign_batch                           avgt    5    19.237 ±  13.521  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_timeout                              avgt    5    16.800 ±  31.792  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_timeout_batch                        avgt    5    19.532 ±  29.373  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_upgrade                              avgt    5    14.118 ±  10.415  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_upgrade_batch                        avgt    5    23.357 ±  17.738  ns/op
i.h.serventis.jmh.opt.sync.LockOps.lock_from_conduit                         avgt    5     0.843 ±   0.001  ns/op
i.h.serventis.jmh.opt.sync.LockOps.lock_from_conduit_batch                   avgt    5     0.646 ±   0.001  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.counter_from_conduit                   avgt    5     0.854 ±   0.060  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.counter_from_conduit_batch             avgt    5     0.646 ±   0.003  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_increment                         avgt    5    13.973 ±   7.927  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_increment_batch                   avgt    5    10.139 ±  13.802  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_overflow                          avgt    5    20.014 ±  21.642  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_overflow_batch                    avgt    5    25.622 ±  12.081  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_reset                             avgt    5    21.693 ±  21.365  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_reset_batch                       avgt    5    23.183 ±  11.098  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_sign                              avgt    5     7.796 ±   6.926  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_sign_batch                        avgt    5    17.307 ±  18.716  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_decrement                           avgt    5    21.480 ±  32.361  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_decrement_batch                     avgt    5    14.669 ±  26.880  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_increment                           avgt    5    18.512 ±  24.226  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_increment_batch                     avgt    5    18.676 ±  16.153  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_overflow                            avgt    5    19.584 ±  30.238  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_overflow_batch                      avgt    5    11.576 ±  14.182  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_reset                               avgt    5    13.119 ±  12.603  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_reset_batch                         avgt    5    13.635 ±   7.796  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_sign                                avgt    5    19.801 ±  26.410  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_sign_batch                          avgt    5    15.818 ±  28.705  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_underflow                           avgt    5    15.710 ±  17.905  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_underflow_batch                     avgt    5    11.033 ±   9.582  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.gauge_from_conduit                       avgt    5     0.847 ±   0.031  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.gauge_from_conduit_batch                 avgt    5     0.646 ±   0.003  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_debug                                 avgt    5    19.820 ±  21.986  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_debug_batch                           avgt    5    18.562 ±  13.652  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_info                                  avgt    5    26.381 ±  24.041  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_info_batch                            avgt    5    12.691 ±  13.997  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_severe                                avgt    5    11.622 ±  16.493  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_severe_batch                          avgt    5    22.285 ±  17.493  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_sign                                  avgt    5    19.466 ±  35.552  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_sign_batch                            avgt    5     6.041 ±  11.283  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_warning                               avgt    5    17.192 ±  17.890  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_warning_batch                         avgt    5    22.100 ±  22.786  ns/op
i.h.serventis.jmh.opt.tool.LogOps.log_from_conduit                           avgt    5     0.862 ±   0.096  ns/op
i.h.serventis.jmh.opt.tool.LogOps.log_from_conduit_batch                     avgt    5     0.647 ±   0.001  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_connect                             avgt    5     6.677 ±   2.128  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_connect_batch                       avgt    5    10.110 ±  11.992  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_connected                           avgt    5    10.357 ±   6.292  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_connected_batch                     avgt    5    17.099 ±  18.484  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_disconnect                          avgt    5    15.290 ±  20.515  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_disconnect_batch                    avgt    5    16.987 ±  30.393  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_disconnected                        avgt    5    15.792 ±   5.543  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_disconnected_batch                  avgt    5    15.766 ±  22.964  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_fail                                avgt    5    11.862 ±   8.076  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_fail_batch                          avgt    5    19.273 ±  17.083  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_failed                              avgt    5    15.749 ±  39.638  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_failed_batch                        avgt    5    14.709 ±  21.529  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_process                             avgt    5    12.934 ±  19.385  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_process_batch                       avgt    5     8.985 ±  11.504  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_processed                           avgt    5    18.696 ±  15.017  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_processed_batch                     avgt    5    14.404 ±  10.365  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_receive_batch                       avgt    5    20.813 ±   8.726  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_received_batch                      avgt    5    12.108 ±  16.506  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_signal                              avgt    5    15.579 ±  18.986  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_signal_batch                        avgt    5    20.159 ±  30.432  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_succeed                             avgt    5    10.725 ±  11.199  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_succeed_batch                       avgt    5    18.450 ±  27.827  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_succeeded                           avgt    5    15.052 ±  12.025  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_succeeded_batch                     avgt    5    18.822 ±  26.551  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transfer                            avgt    5    15.641 ±  19.380  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transfer_inbound                    avgt    5    17.123 ±  17.277  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transfer_outbound                   avgt    5    11.273 ±  10.805  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transferred                         avgt    5    14.535 ±  21.256  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transmit_batch                      avgt    5    17.312 ±  17.130  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transmitted_batch                   avgt    5    16.589 ±   5.805  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.probe_from_conduit                       avgt    5     0.856 ±   0.041  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.probe_from_conduit_batch                 avgt    5     0.647 ±   0.005  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_baseline                     avgt    5    10.456 ±   7.825  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_baseline_batch               avgt    5    18.433 ±  25.567  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_target                       avgt    5    15.605 ±  15.978  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_target_batch                 avgt    5    16.046 ±  29.995  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_threshold                    avgt    5    15.310 ±   5.447  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_threshold_batch              avgt    5    11.550 ±  10.603  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_baseline                     avgt    5     8.776 ±  12.106  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_baseline_batch               avgt    5    22.209 ±  11.588  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_target                       avgt    5    15.885 ±   9.933  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_target_batch                 avgt    5    13.846 ±  13.941  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_threshold                    avgt    5    11.393 ±   4.388  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_threshold_batch              avgt    5     9.181 ±  10.655  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_baseline                   avgt    5    12.364 ±  12.006  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_baseline_batch             avgt    5    23.532 ±  18.791  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_target                     avgt    5    15.768 ±  15.509  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_target_batch               avgt    5    17.510 ±   9.359  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_threshold                  avgt    5    14.534 ±  11.928  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_threshold_batch            avgt    5    16.546 ±   7.259  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_signal                             avgt    5    11.019 ±  13.806  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_signal_batch                       avgt    5    20.743 ±  17.483  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.sensor_from_conduit                     avgt    5     0.845 ±   0.001  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.sensor_from_conduit_batch               avgt    5     0.648 ±   0.001  ns/op
i.h.serventis.jmh.sdk.OperationOps.emit_begin                                avgt    5    19.063 ±  23.579  ns/op
i.h.serventis.jmh.sdk.OperationOps.emit_begin_batch                          avgt    5    12.294 ±  12.652  ns/op
i.h.serventis.jmh.sdk.OperationOps.emit_end                                  avgt    5    14.728 ±   8.613  ns/op
i.h.serventis.jmh.sdk.OperationOps.emit_end_batch                            avgt    5    11.668 ±  11.190  ns/op
i.h.serventis.jmh.sdk.OperationOps.emit_sign                                 avgt    5    18.345 ±  19.669  ns/op
i.h.serventis.jmh.sdk.OperationOps.emit_sign_batch                           avgt    5     9.446 ±   8.740  ns/op
i.h.serventis.jmh.sdk.OperationOps.operation_from_conduit                    avgt    5     0.854 ±   0.115  ns/op
i.h.serventis.jmh.sdk.OperationOps.operation_from_conduit_batch              avgt    5     0.648 ±   0.001  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.emit_fail                                   avgt    5     7.932 ±   8.340  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.emit_fail_batch                             avgt    5    16.079 ±  35.838  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.emit_sign                                   avgt    5    11.428 ±  11.821  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.emit_sign_batch                             avgt    5    22.574 ±  15.964  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.emit_success                                avgt    5    22.184 ±  20.224  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.emit_success_batch                          avgt    5    17.761 ±  18.681  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.outcome_from_conduit                        avgt    5     0.857 ±   0.055  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.outcome_from_conduit_batch                  avgt    5     0.646 ±   0.001  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_mixed_pattern                         avgt    5     0.226 ±   0.003  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_single                                avgt    5     0.750 ±   0.001  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_single_batch                          avgt    5     0.019 ±   0.001  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_varied_batch                          avgt    5     1.515 ±   0.001  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_worst_case                            avgt    5     1.183 ±   0.001  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_critical                             avgt    5    12.405 ±  13.805  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_critical_batch                       avgt    5    17.820 ±  24.479  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_normal                               avgt    5    16.418 ±  18.255  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_normal_batch                         avgt    5    17.887 ±  14.320  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_signal                               avgt    5    11.858 ±  16.898  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_signal_batch                         avgt    5    21.656 ±  20.226  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_warning                              avgt    5    13.010 ±  26.945  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_warning_batch                        avgt    5    15.901 ±   4.087  ns/op
i.h.serventis.jmh.sdk.SituationOps.situation_from_conduit                    avgt    5     0.842 ±   0.067  ns/op
i.h.serventis.jmh.sdk.SituationOps.situation_from_conduit_batch              avgt    5     0.646 ±   0.003  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_converging_confirmed                    avgt    5    11.051 ±  10.169  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_converging_confirmed_batch              avgt    5    21.881 ±  25.731  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_defective_tentative                     avgt    5    12.827 ±   1.549  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_defective_tentative_batch               avgt    5    12.449 ±  12.464  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_degraded_measured                       avgt    5    13.986 ±  14.067  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_degraded_measured_batch                 avgt    5    13.685 ±  17.121  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_down_confirmed                          avgt    5    12.861 ±  13.395  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_down_confirmed_batch                    avgt    5    20.994 ±  24.557  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_signal                                  avgt    5    15.926 ±   9.050  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_signal_batch                            avgt    5    17.853 ±  12.505  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_stable_confirmed                        avgt    5    12.557 ±   7.809  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_stable_confirmed_batch                  avgt    5    17.424 ±  21.957  ns/op
i.h.serventis.jmh.sdk.StatusOps.status_from_conduit                          avgt    5     0.860 ±   0.074  ns/op
i.h.serventis.jmh.sdk.StatusOps.status_from_conduit_batch                    avgt    5     0.646 ±   0.004  ns/op
i.h.serventis.jmh.sdk.SurveyOps.emit_divided                                 avgt    5    10.189 ±   9.633  ns/op
i.h.serventis.jmh.sdk.SurveyOps.emit_divided_batch                           avgt    5    11.956 ±  15.140  ns/op
i.h.serventis.jmh.sdk.SurveyOps.emit_majority                                avgt    5    10.731 ±  14.213  ns/op
i.h.serventis.jmh.sdk.SurveyOps.emit_majority_batch                          avgt    5    23.031 ±  16.165  ns/op
i.h.serventis.jmh.sdk.SurveyOps.emit_signal                                  avgt    5    10.735 ±   7.981  ns/op
i.h.serventis.jmh.sdk.SurveyOps.emit_signal_batch                            avgt    5    15.076 ±  17.986  ns/op
i.h.serventis.jmh.sdk.SurveyOps.emit_unanimous                               avgt    5    13.296 ±  16.482  ns/op
i.h.serventis.jmh.sdk.SurveyOps.emit_unanimous_batch                         avgt    5    12.732 ±  18.627  ns/op
i.h.serventis.jmh.sdk.SurveyOps.survey_from_conduit                          avgt    5     0.847 ±   0.041  ns/op
i.h.serventis.jmh.sdk.SurveyOps.survey_from_conduit_batch                    avgt    5     0.645 ±   0.002  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_alarm_flow                              avgt    5    21.365 ±  51.082  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_alarm_flow_batch                        avgt    5    16.596 ±  19.884  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_fault_link                              avgt    5    13.759 ±  22.032  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_fault_link_batch                        avgt    5    23.912 ±  27.805  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_limit_time                              avgt    5    15.275 ±   6.069  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_limit_time_batch                        avgt    5    16.269 ±  14.772  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_normal_space                            avgt    5     9.770 ±  14.300  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_normal_space_batch                      avgt    5    16.969 ±  18.209  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_signal                                  avgt    5    13.170 ±   8.509  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_signal_batch                            avgt    5    15.240 ±  26.648  ns/op
i.h.serventis.jmh.sdk.SystemOps.system_from_conduit                          avgt    5     0.855 ±   0.083  ns/op
i.h.serventis.jmh.sdk.SystemOps.system_from_conduit_batch                    avgt    5     0.646 ±   0.001  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_chaos                                    avgt    5    21.460 ±  17.834  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_chaos_batch                              avgt    5    23.767 ±  15.242  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_cycle                                    avgt    5    16.027 ±   6.557  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_cycle_batch                              avgt    5    24.172 ±  25.568  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_drift                                    avgt    5    15.566 ±  18.454  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_drift_batch                              avgt    5    13.040 ±  13.009  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_sign                                     avgt    5    17.394 ±   5.478  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_sign_batch                               avgt    5    13.768 ±  14.550  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_spike                                    avgt    5    14.458 ±  17.545  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_spike_batch                              avgt    5    18.386 ±  19.807  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_stable                                   avgt    5    17.922 ±  24.566  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_stable_batch                             avgt    5    20.440 ±  21.014  ns/op
i.h.serventis.jmh.sdk.TrendOps.trend_from_conduit                            avgt    5     0.863 ±   0.080  ns/op
i.h.serventis.jmh.sdk.TrendOps.trend_from_conduit_batch                      avgt    5     0.646 ±   0.001  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.cycle_from_conduit                       avgt    5     0.853 ±   0.055  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.cycle_from_conduit_batch                 avgt    5     0.646 ±   0.006  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_repeat                              avgt    5    10.956 ±   7.518  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_repeat_batch                        avgt    5    13.158 ±  14.774  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_return                              avgt    5    13.955 ±  24.131  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_return_batch                        avgt    5    15.120 ±   5.034  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_signal                              avgt    5     9.072 ±   4.665  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_signal_batch                        avgt    5    15.633 ±  14.068  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_single                              avgt    5     9.619 ±   5.873  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_single_batch                        avgt    5    17.335 ±  25.278  ns/op
i.h.substrates.jmh.CircuitOps.conduit_create_close                           avgt    5   337.875 ±  86.157  ns/op
i.h.substrates.jmh.CircuitOps.conduit_create_named                           avgt    5   328.418 ±  66.893  ns/op
i.h.substrates.jmh.CircuitOps.conduit_create_with_flow                       avgt    5   337.910 ± 122.981  ns/op
i.h.substrates.jmh.CircuitOps.create_and_close                               avgt    5   340.015 ±  49.444  ns/op
i.h.substrates.jmh.CircuitOps.hot_conduit_create                             avgt    5    29.385 ±  13.997  ns/op
i.h.substrates.jmh.CircuitOps.hot_conduit_create_named                       avgt    5    31.764 ±  19.902  ns/op
i.h.substrates.jmh.CircuitOps.hot_conduit_create_with_flow                   avgt    5    33.912 ±  15.568  ns/op
i.h.substrates.jmh.CircuitOps.hot_pipe_async                                 avgt    5    34.418 ±   0.125  ns/op
i.h.substrates.jmh.CircuitOps.hot_pipe_async_with_flow                       avgt    5    36.037 ±   0.097  ns/op
i.h.substrates.jmh.CircuitOps.pipe_async                                     avgt    5   304.019 ± 101.002  ns/op
i.h.substrates.jmh.CircuitOps.pipe_async_with_flow                           avgt    5   304.779 ±  32.277  ns/op
i.h.substrates.jmh.ConduitOps.get_by_name                                    avgt    5     0.743 ±   0.030  ns/op
i.h.substrates.jmh.ConduitOps.get_by_name_batch                              avgt    5     0.638 ±   0.001  ns/op
i.h.substrates.jmh.ConduitOps.get_by_substrate                               avgt    5     1.298 ±   0.001  ns/op
i.h.substrates.jmh.ConduitOps.get_by_substrate_batch                         avgt    5     1.147 ±   0.076  ns/op
i.h.substrates.jmh.ConduitOps.get_cached                                     avgt    5     1.348 ±   0.038  ns/op
i.h.substrates.jmh.ConduitOps.get_cached_batch                               avgt    5     1.276 ±   0.006  ns/op
i.h.substrates.jmh.ConduitOps.subscribe                                      avgt    5   234.942 ±  56.629  ns/op
i.h.substrates.jmh.ConduitOps.subscribe_batch                                avgt    5   237.595 ±  93.353  ns/op
i.h.substrates.jmh.ConduitOps.subscribe_with_emission_await                  avgt    5  6641.483 ± 108.491  ns/op
i.h.substrates.jmh.CortexOps.circuit                                         avgt    5   311.625 ±  53.266  ns/op
i.h.substrates.jmh.CortexOps.circuit_batch                                   avgt    5   329.785 ±  27.533  ns/op
i.h.substrates.jmh.CortexOps.circuit_named                                   avgt    5   326.520 ±  29.985  ns/op
i.h.substrates.jmh.CortexOps.current                                         avgt    5     1.390 ±   0.013  ns/op
i.h.substrates.jmh.CortexOps.name_class                                      avgt    5     3.071 ±   0.001  ns/op
i.h.substrates.jmh.CortexOps.name_enum                                       avgt    5     1.493 ±   0.023  ns/op
i.h.substrates.jmh.CortexOps.name_iterable                                   avgt    5     3.756 ±   0.006  ns/op
i.h.substrates.jmh.CortexOps.name_path                                       avgt    5     2.463 ±   0.006  ns/op
i.h.substrates.jmh.CortexOps.name_path_batch                                 avgt    5     2.290 ±   0.001  ns/op
i.h.substrates.jmh.CortexOps.name_string                                     avgt    5     1.510 ±   0.009  ns/op
i.h.substrates.jmh.CortexOps.name_string_batch                               avgt    5     1.183 ±   0.001  ns/op
i.h.substrates.jmh.CortexOps.scope                                           avgt    5    32.437 ±   0.676  ns/op
i.h.substrates.jmh.CortexOps.scope_batch                                     avgt    5    32.107 ±   0.617  ns/op
i.h.substrates.jmh.CortexOps.scope_named                                     avgt    5    33.349 ±   0.247  ns/op
i.h.substrates.jmh.CortexOps.slot_boolean                                    avgt    5     1.804 ±   0.114  ns/op
i.h.substrates.jmh.CortexOps.slot_double                                     avgt    5     2.780 ±   0.166  ns/op
i.h.substrates.jmh.CortexOps.slot_int                                        avgt    5     1.679 ±   0.925  ns/op
i.h.substrates.jmh.CortexOps.slot_long                                       avgt    5     1.799 ±   0.378  ns/op
i.h.substrates.jmh.CortexOps.slot_string                                     avgt    5     1.694 ±   0.449  ns/op
i.h.substrates.jmh.CortexOps.state_empty                                     avgt    5     5.987 ±   3.364  ns/op
i.h.substrates.jmh.CortexOps.state_empty_batch                               avgt    5     1.465 ±   0.064  ns/op
i.h.substrates.jmh.CyclicOps.cyclic_emit                                     avgt    5     1.660 ±   1.243  ns/op
i.h.substrates.jmh.CyclicOps.cyclic_emit_await                               avgt    5    13.733 ±   0.141  ns/op
i.h.substrates.jmh.CyclicOps.cyclic_emit_deep_await                          avgt    5     5.337 ±   0.139  ns/op
i.h.substrates.jmh.FlowOps.baseline_no_flow_await                            avgt    5    17.565 ±   0.313  ns/op
i.h.substrates.jmh.FlowOps.flow_combined_diff_guard_await                    avgt    5    19.695 ±   0.171  ns/op
i.h.substrates.jmh.FlowOps.flow_combined_diff_sample_await                   avgt    5    19.960 ±   1.390  ns/op
i.h.substrates.jmh.FlowOps.flow_combined_guard_limit_await                   avgt    5    16.266 ±   0.516  ns/op
i.h.substrates.jmh.FlowOps.flow_diff_await                                   avgt    5    21.198 ±   1.712  ns/op
i.h.substrates.jmh.FlowOps.flow_guard_await                                  avgt    5    17.001 ±   0.366  ns/op
i.h.substrates.jmh.FlowOps.flow_limit_await                                  avgt    5    18.425 ±   0.734  ns/op
i.h.substrates.jmh.FlowOps.flow_sample_await                                 avgt    5    20.336 ±   0.170  ns/op
i.h.substrates.jmh.FlowOps.flow_sift_await                                   avgt    5    17.397 ±   0.482  ns/op
i.h.substrates.jmh.NameOps.name_chained_deep                                 avgt    5     4.757 ±   0.025  ns/op
i.h.substrates.jmh.NameOps.name_chaining                                     avgt    5     6.196 ±   0.053  ns/op
i.h.substrates.jmh.NameOps.name_chaining_batch                               avgt    5     5.869 ±   0.341  ns/op
i.h.substrates.jmh.NameOps.name_compare                                      avgt    5     1.536 ±   0.017  ns/op
i.h.substrates.jmh.NameOps.name_compare_batch                                avgt    5     0.002 ±   0.001  ns/op
i.h.substrates.jmh.NameOps.name_depth                                        avgt    5     0.541 ±   0.004  ns/op
i.h.substrates.jmh.NameOps.name_depth_batch                                  avgt    5     0.001 ±   0.001  ns/op
i.h.substrates.jmh.NameOps.name_enclosure                                    avgt    5     0.589 ±   0.005  ns/op
i.h.substrates.jmh.NameOps.name_from_enum                                    avgt    5     1.534 ±   0.024  ns/op
i.h.substrates.jmh.NameOps.name_from_iterable                                avgt    5     3.820 ±   0.010  ns/op
i.h.substrates.jmh.NameOps.name_from_iterator                                avgt    5     3.788 ±   0.013  ns/op
i.h.substrates.jmh.NameOps.name_from_mapped_iterable                         avgt    5     3.919 ±   0.004  ns/op
i.h.substrates.jmh.NameOps.name_from_name                                    avgt    5     2.683 ±   0.008  ns/op
i.h.substrates.jmh.NameOps.name_from_string                                  avgt    5     1.495 ±   0.010  ns/op
i.h.substrates.jmh.NameOps.name_from_string_batch                            avgt    5     1.180 ±   0.005  ns/op
i.h.substrates.jmh.NameOps.name_interning_chained                            avgt    5     9.396 ±   0.006  ns/op
i.h.substrates.jmh.NameOps.name_interning_same_path                          avgt    5     4.806 ±   0.021  ns/op
i.h.substrates.jmh.NameOps.name_interning_segments                           avgt    5     4.458 ±   0.003  ns/op
i.h.substrates.jmh.NameOps.name_iterate_hierarchy                            avgt    5     1.720 ±   0.126  ns/op
i.h.substrates.jmh.NameOps.name_parsing                                      avgt    5     2.468 ±   0.018  ns/op
i.h.substrates.jmh.NameOps.name_parsing_batch                                avgt    5     2.263 ±   0.002  ns/op
i.h.substrates.jmh.NameOps.name_path_generation                              avgt    5     0.519 ±   0.003  ns/op
i.h.substrates.jmh.NameOps.name_path_generation_batch                        avgt    5     0.001 ±   0.001  ns/op
i.h.substrates.jmh.PipeOps.async_emit_batch                                  avgt    5     8.469 ±   3.574  ns/op
i.h.substrates.jmh.PipeOps.async_emit_batch_await                            avgt    5    18.010 ±   0.523  ns/op
i.h.substrates.jmh.PipeOps.async_emit_chained_await                          avgt    5    20.715 ±   0.365  ns/op
i.h.substrates.jmh.PipeOps.async_emit_fanout_await                           avgt    5    19.836 ±   0.163  ns/op
i.h.substrates.jmh.PipeOps.async_emit_single                                 avgt    5    32.021 ±   9.757  ns/op
i.h.substrates.jmh.PipeOps.async_emit_single_await                           avgt    5  6201.161 ± 239.532  ns/op
i.h.substrates.jmh.PipeOps.async_emit_with_flow_await                        avgt    5    20.655 ±   0.095  ns/op
i.h.substrates.jmh.PipeOps.baseline_blackhole                                avgt    5     0.280 ±   0.135  ns/op
i.h.substrates.jmh.PipeOps.baseline_counter                                  avgt    5     1.610 ±   0.042  ns/op
i.h.substrates.jmh.PipeOps.baseline_receptor                                 avgt    5     0.264 ±   0.004  ns/op
i.h.substrates.jmh.PipeOps.pipe_create                                       avgt    5    33.861 ±   0.170  ns/op
i.h.substrates.jmh.PipeOps.pipe_create_chained                               avgt    5     2.129 ±   0.909  ns/op
i.h.substrates.jmh.PipeOps.pipe_create_with_flow                             avgt    5    51.752 ±   0.414  ns/op
i.h.substrates.jmh.ReservoirOps.baseline_emit_no_reservoir_await             avgt    5    71.595 ±   6.121  ns/op
i.h.substrates.jmh.ReservoirOps.baseline_emit_no_reservoir_await_batch       avgt    5    12.364 ±   0.874  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_burst_then_drain_await             avgt    5    87.032 ±   1.138  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_burst_then_drain_await_batch       avgt    5    19.345 ±   0.238  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_drain_await                        avgt    5    85.035 ±   2.993  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_drain_await_batch                  avgt    5    19.385 ±   0.285  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_emit_drain_cycles_await            avgt    5   356.335 ±   3.521  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_emit_with_capture_await            avgt    5    89.043 ±   0.921  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_emit_with_capture_await_batch      avgt    5    18.869 ±   0.084  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_process_emissions_await            avgt    5    93.481 ±   0.357  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_process_emissions_await_batch      avgt    5    21.643 ±   0.069  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_process_subjects_await             avgt    5    87.544 ±   3.221  ns/op
i.h.substrates.jmh.ScopeOps.scope_child_anonymous                            avgt    5    71.261 ±   0.901  ns/op
i.h.substrates.jmh.ScopeOps.scope_child_anonymous_batch                      avgt    5    70.819 ±   0.863  ns/op
i.h.substrates.jmh.ScopeOps.scope_child_named                                avgt    5    71.379 ±   0.540  ns/op
i.h.substrates.jmh.ScopeOps.scope_child_named_batch                          avgt    5    71.517 ±   0.741  ns/op
i.h.substrates.jmh.ScopeOps.scope_close_idempotent                           avgt    5    31.525 ±   0.354  ns/op
i.h.substrates.jmh.ScopeOps.scope_close_idempotent_batch                     avgt    5    31.037 ±   0.651  ns/op
i.h.substrates.jmh.ScopeOps.scope_closure                                    avgt    5   300.842 ±  64.834  ns/op
i.h.substrates.jmh.ScopeOps.scope_closure_batch                              avgt    5   291.830 ±   8.002  ns/op
i.h.substrates.jmh.ScopeOps.scope_complex                                    avgt    5   915.134 ± 183.421  ns/op
i.h.substrates.jmh.ScopeOps.scope_create_and_close                           avgt    5    32.300 ±   0.219  ns/op
i.h.substrates.jmh.ScopeOps.scope_create_and_close_batch                     avgt    5    30.869 ±   0.538  ns/op
i.h.substrates.jmh.ScopeOps.scope_create_named                               avgt    5    31.178 ±   0.291  ns/op
i.h.substrates.jmh.ScopeOps.scope_create_named_batch                         avgt    5    31.379 ±   0.481  ns/op
i.h.substrates.jmh.ScopeOps.scope_hierarchy                                  avgt    5   112.288 ±   1.853  ns/op
i.h.substrates.jmh.ScopeOps.scope_hierarchy_batch                            avgt    5   108.985 ±   0.915  ns/op
i.h.substrates.jmh.ScopeOps.scope_parent_closes_children                     avgt    5   146.121 ±   1.680  ns/op
i.h.substrates.jmh.ScopeOps.scope_parent_closes_children_batch               avgt    5   143.403 ±   1.470  ns/op
i.h.substrates.jmh.ScopeOps.scope_register_multiple                          avgt    5  1543.865 ± 206.729  ns/op
i.h.substrates.jmh.ScopeOps.scope_register_multiple_batch                    avgt    5  1543.013 ± 219.445  ns/op
i.h.substrates.jmh.ScopeOps.scope_register_single                            avgt    5   290.782 ±  13.190  ns/op
i.h.substrates.jmh.ScopeOps.scope_register_single_batch                      avgt    5   286.149 ±   4.813  ns/op
i.h.substrates.jmh.ScopeOps.scope_with_resources                             avgt    5   626.615 ±  84.514  ns/op
i.h.substrates.jmh.StateOps.slot_name                                        avgt    5     0.523 ±   0.001  ns/op
i.h.substrates.jmh.StateOps.slot_name_batch                                  avgt    5     0.001 ±   0.001  ns/op
i.h.substrates.jmh.StateOps.slot_type                                        avgt    5     0.527 ±   0.061  ns/op
i.h.substrates.jmh.StateOps.slot_value                                       avgt    5     0.638 ±   0.001  ns/op
i.h.substrates.jmh.StateOps.slot_value_batch                                 avgt    5     0.001 ±   0.001  ns/op
i.h.substrates.jmh.StateOps.state_compact                                    avgt    5    39.363 ±  19.104  ns/op
i.h.substrates.jmh.StateOps.state_compact_batch                              avgt    5    44.813 ±  31.749  ns/op
i.h.substrates.jmh.StateOps.state_iterate_slots                              avgt    5     2.393 ±   0.003  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_int                               avgt    5     5.302 ±   0.668  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_int_batch                         avgt    5     5.077 ±   1.266  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_long                              avgt    5     5.185 ±   1.798  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_object                            avgt    5     2.908 ±   0.366  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_object_batch                      avgt    5     3.596 ±   1.091  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_string                            avgt    5     4.771 ±   1.948  ns/op
i.h.substrates.jmh.StateOps.state_value_read                                 avgt    5     1.566 ±   0.016  ns/op
i.h.substrates.jmh.StateOps.state_value_read_batch                           avgt    5     0.025 ±   0.001  ns/op
i.h.substrates.jmh.StateOps.state_values_stream                              avgt    5    11.021 ±   0.131  ns/op
i.h.substrates.jmh.SubjectOps.subject_compare                                avgt    5     2.238 ±   0.020  ns/op
i.h.substrates.jmh.SubjectOps.subject_compare_batch                          avgt    5     2.198 ±   0.001  ns/op
i.h.substrates.jmh.SubjectOps.subject_compare_same                           avgt    5     0.438 ±   0.007  ns/op
i.h.substrates.jmh.SubjectOps.subject_compare_same_batch                     avgt    5    ≈ 10⁻³            ns/op
i.h.substrates.jmh.SubjectOps.subject_compare_three_way                      avgt    5     6.590 ±   0.035  ns/op
i.h.substrates.jmh.SubscriberOps.close_five_conduits_await                   avgt    5  4262.694 ±  67.131  ns/op
i.h.substrates.jmh.SubscriberOps.close_five_subscriptions_await              avgt    5  4184.760 ±  58.977  ns/op
i.h.substrates.jmh.SubscriberOps.close_idempotent_await                      avgt    5  4555.338 ± 217.271  ns/op
i.h.substrates.jmh.SubscriberOps.close_idempotent_batch_await                avgt    5     7.003 ±   0.322  ns/op
i.h.substrates.jmh.SubscriberOps.close_no_subscriptions_await                avgt    5  4522.777 ±  61.467  ns/op
i.h.substrates.jmh.SubscriberOps.close_no_subscriptions_batch_await          avgt    5     8.963 ±   0.267  ns/op
i.h.substrates.jmh.SubscriberOps.close_one_subscription_await                avgt    5  4365.184 ±  77.134  ns/op
i.h.substrates.jmh.SubscriberOps.close_one_subscription_batch_await          avgt    5    95.194 ±   1.707  ns/op
i.h.substrates.jmh.SubscriberOps.close_ten_conduits_await                    avgt    5  4729.985 ± 213.520  ns/op
i.h.substrates.jmh.SubscriberOps.close_ten_subscriptions_await               avgt    5  4484.674 ±  78.973  ns/op
i.h.substrates.jmh.SubscriberOps.close_with_pending_emissions_await          avgt    5  4377.388 ±  38.182  ns/op
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