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
i.h.serventis.jmh.opt.data.CacheOps.cache_from_conduit                       avgt    5      2.042 ±    0.299  ns/op
i.h.serventis.jmh.opt.data.CacheOps.cache_from_conduit_batch                 avgt    5      1.852 ±    0.315  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_evict                               avgt    5      6.842 ±    9.736  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_evict_batch                         avgt    5      6.772 ±    9.452  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_expire                              avgt    5      7.743 ±    3.109  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_expire_batch                        avgt    5      7.165 ±    2.241  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_hit                                 avgt    5      7.493 ±    1.587  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_hit_batch                           avgt    5      7.298 ±    8.777  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_lookup                              avgt    5      7.145 ±    6.419  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_lookup_batch                        avgt    5      7.575 ±    5.860  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_miss                                avgt    5      7.306 ±    0.967  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_miss_batch                          avgt    5      8.029 ±    0.798  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_remove                              avgt    5      7.573 ±    3.491  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_remove_batch                        avgt    5      7.241 ±    9.954  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_sign                                avgt    5      7.384 ±    2.904  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_sign_batch                          avgt    5      7.430 ±    9.908  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_store                               avgt    5      7.239 ±    3.405  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_store_batch                         avgt    5      6.737 ±    9.402  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_aggregate                        avgt    5      9.077 ±    2.650  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_aggregate_batch                  avgt    5      7.701 ±    0.959  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_backpressure                     avgt    5      7.644 ±    0.801  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_backpressure_batch               avgt    5      6.896 ±    8.809  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_buffer                           avgt    5      7.301 ±    1.626  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_buffer_batch                     avgt    5      6.793 ±    2.373  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_checkpoint                       avgt    5      8.633 ±    2.410  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_checkpoint_batch                 avgt    5      7.758 ±    0.758  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_filter                           avgt    5      7.087 ±    6.519  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_filter_batch                     avgt    5      8.669 ±    2.684  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_input                            avgt    5      8.915 ±    1.545  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_input_batch                      avgt    5      7.372 ±    3.575  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_lag                              avgt    5      7.318 ±    3.417  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_lag_batch                        avgt    5      7.092 ±    8.000  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_output                           avgt    5      8.496 ±    0.552  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_output_batch                     avgt    5      7.735 ±    6.913  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_overflow                         avgt    5      7.483 ±    4.503  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_overflow_batch                   avgt    5      7.038 ±    4.307  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_sign                             avgt    5      7.315 ±    1.892  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_sign_batch                       avgt    5      8.441 ±    0.708  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_skip                             avgt    5      7.840 ±    0.720  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_skip_batch                       avgt    5      6.929 ±    3.378  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_transform                        avgt    5      9.004 ±    0.939  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_transform_batch                  avgt    5      6.476 ±    7.444  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_watermark                        avgt    5      6.857 ±    5.383  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_watermark_batch                  avgt    5      6.947 ±    2.706  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_flow_etl                     avgt    5     38.441 ±   26.835  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_flow_stream                  avgt    5     36.756 ±   22.742  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_flow_windowed                avgt    5     34.886 ±   28.883  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_from_conduit                 avgt    5      2.067 ±    0.356  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_from_conduit_batch           avgt    5      1.851 ±    0.281  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_dequeue                             avgt    5      7.664 ±    3.358  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_dequeue_batch                       avgt    5      6.911 ±    1.818  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_enqueue                             avgt    5      7.673 ±    3.375  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_enqueue_batch                       avgt    5      6.780 ±    3.032  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_overflow                            avgt    5      6.640 ±    8.142  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_overflow_batch                      avgt    5      6.599 ±    9.504  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_sign                                avgt    5      7.880 ±    0.332  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_sign_batch                          avgt    5      7.524 ±    2.655  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_underflow                           avgt    5      5.661 ±    4.430  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_underflow_batch                     avgt    5      7.196 ±    3.326  ns/op
i.h.serventis.jmh.opt.data.QueueOps.queue_from_conduit                       avgt    5      2.068 ±    0.401  ns/op
i.h.serventis.jmh.opt.data.QueueOps.queue_from_conduit_batch                 avgt    5      1.849 ±    0.303  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_overflow                            avgt    5      7.946 ±    5.712  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_overflow_batch                      avgt    5      7.680 ±    1.245  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_pop                                 avgt    5      6.789 ±    5.219  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_pop_batch                           avgt    5      6.728 ±    9.675  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_push                                avgt    5      7.371 ±    2.917  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_push_batch                          avgt    5      7.060 ±    3.565  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_sign                                avgt    5      7.103 ±    5.988  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_sign_batch                          avgt    5      7.010 ±    2.585  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_underflow                           avgt    5      6.779 ±    9.032  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_underflow_batch                     avgt    5      6.765 ±    2.897  ns/op
i.h.serventis.jmh.opt.data.StackOps.stack_from_conduit                       avgt    5      2.080 ±    0.303  ns/op
i.h.serventis.jmh.opt.data.StackOps.stack_from_conduit_batch                 avgt    5      1.867 ±    0.317  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_crash                             avgt    5      7.313 ±    2.685  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_crash_batch                       avgt    5      7.157 ±    9.151  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_fail                              avgt    5      6.758 ±    8.106  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_fail_batch                        avgt    5      6.980 ±    3.828  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_kill                              avgt    5      7.142 ±    2.468  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_kill_batch                        avgt    5      7.862 ±    7.241  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_restart                           avgt    5      7.263 ±    2.358  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_restart_batch                     avgt    5      6.887 ±    2.774  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_resume                            avgt    5      7.234 ±    2.148  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_resume_batch                      avgt    5      6.923 ±    3.541  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_sign                              avgt    5      6.986 ±    2.664  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_sign_batch                        avgt    5      9.551 ±    3.661  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_spawn                             avgt    5      7.681 ±    3.383  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_spawn_batch                       avgt    5      6.214 ±    7.575  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_start                             avgt    5      7.545 ±    3.623  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_start_batch                       avgt    5      9.870 ±    5.478  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_stop                              avgt    5      7.362 ±    7.752  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_stop_batch                        avgt    5      6.699 ±    8.340  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_suspend                           avgt    5      7.152 ±    2.825  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_suspend_batch                     avgt    5      6.451 ±    6.116  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.process_from_conduit                   avgt    5      2.083 ±    0.368  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.process_from_conduit_batch             avgt    5      1.848 ±    0.300  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_call                              avgt    5      6.244 ±    7.005  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_call_batch                        avgt    5      6.583 ±    2.776  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_called                            avgt    5      7.053 ±    8.621  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_called_batch                      avgt    5      7.106 ±    2.209  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_delay                             avgt    5      7.589 ±    2.961  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_delay_batch                       avgt    5      9.103 ±    1.799  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_delayed                           avgt    5      7.223 ±    1.355  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_delayed_batch                     avgt    5      8.249 ±    1.356  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_discard                           avgt    5      6.416 ±    7.980  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_discard_batch                     avgt    5      6.822 ±    2.834  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_discarded                         avgt    5      7.873 ±    0.909  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_discarded_batch                   avgt    5      8.780 ±    3.048  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_disconnect                        avgt    5      7.021 ±    2.646  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_disconnect_batch                  avgt    5      7.130 ±    1.328  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_disconnected                      avgt    5      8.247 ±    0.890  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_disconnected_batch                avgt    5      6.291 ±    6.057  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_expire                            avgt    5      6.530 ±   10.010  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_expire_batch                      avgt    5      8.540 ±    2.823  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_expired                           avgt    5      7.233 ±    6.788  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_expired_batch                     avgt    5      7.209 ±    2.470  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_fail                              avgt    5      7.310 ±    2.469  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_fail_batch                        avgt    5      8.209 ±    3.239  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_failed                            avgt    5      6.646 ±    7.764  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_failed_batch                      avgt    5      8.669 ±    2.171  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_recourse                          avgt    5      6.536 ±    7.264  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_recourse_batch                    avgt    5      6.836 ±    2.698  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_recoursed                         avgt    5      8.367 ±    3.684  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_recoursed_batch                   avgt    5      8.485 ±    1.626  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_redirect                          avgt    5      6.770 ±    7.036  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_redirect_batch                    avgt    5      6.581 ±    8.906  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_redirected                        avgt    5      8.482 ±    2.972  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_redirected_batch                  avgt    5      7.303 ±    1.632  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_reject                            avgt    5      7.486 ±    3.971  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_reject_batch                      avgt    5      5.828 ±    7.144  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_rejected                          avgt    5      6.174 ±    3.300  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_rejected_batch                    avgt    5      8.126 ±    2.183  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_resume                            avgt    5      6.482 ±    7.155  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_resume_batch                      avgt    5      8.356 ±    1.953  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_resumed                           avgt    5      9.641 ±    3.656  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_resumed_batch                     avgt    5      8.718 ±    3.659  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_retried                           avgt    5      6.207 ±    6.279  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_retried_batch                     avgt    5      8.041 ±    1.578  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_retry                             avgt    5      6.722 ±    7.565  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_retry_batch                       avgt    5      6.406 ±    8.200  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_schedule                          avgt    5      7.654 ±    0.832  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_schedule_batch                    avgt    5      8.335 ±    2.684  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_scheduled                         avgt    5      6.905 ±    1.291  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_scheduled_batch                   avgt    5      5.907 ±    8.323  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_signal                            avgt    5      8.741 ±    2.263  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_signal_batch                      avgt    5      6.292 ±    7.400  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_start                             avgt    5      7.392 ±    1.207  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_start_batch                       avgt    5      7.022 ±    2.205  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_started                           avgt    5      7.365 ±    2.704  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_started_batch                     avgt    5      7.055 ±    2.560  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_stop                              avgt    5      7.413 ±    2.792  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_stop_batch                        avgt    5      9.121 ±    3.825  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_stopped                           avgt    5      5.652 ±    2.056  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_stopped_batch                     avgt    5      7.820 ±    0.746  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_succeeded                         avgt    5      6.615 ±    6.514  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_succeeded_batch                   avgt    5      6.299 ±    4.781  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_success                           avgt    5      7.484 ±    2.866  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_success_batch                     avgt    5      6.504 ±    9.279  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_suspend                           avgt    5      6.648 ±    5.340  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_suspend_batch                     avgt    5      9.135 ±    4.107  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_suspended                         avgt    5      7.437 ±    2.940  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_suspended_batch                   avgt    5      9.314 ±    4.686  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.service_from_conduit                   avgt    5      2.045 ±    0.314  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.service_from_conduit_batch             avgt    5      1.829 ±    0.261  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_cancel                               avgt    5      7.354 ±    2.944  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_cancel_batch                         avgt    5      7.567 ±    3.116  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_complete                             avgt    5      7.228 ±    7.252  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_complete_batch                       avgt    5      6.943 ±    9.564  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_fail                                 avgt    5      7.268 ±    2.809  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_fail_batch                           avgt    5      6.165 ±    6.698  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_progress                             avgt    5      5.896 ±    7.444  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_progress_batch                       avgt    5      6.816 ±    8.976  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_reject                               avgt    5      7.073 ±    3.268  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_reject_batch                         avgt    5      5.939 ±    7.370  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_resume                               avgt    5      7.061 ±    9.335  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_resume_batch                         avgt    5      7.017 ±    4.753  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_schedule                             avgt    5      7.172 ±    2.904  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_schedule_batch                       avgt    5      6.357 ±   10.003  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_sign                                 avgt    5      6.551 ±    6.835  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_sign_batch                           avgt    5      7.948 ±    3.369  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_start                                avgt    5      7.081 ±    3.368  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_start_batch                          avgt    5      7.133 ±    9.413  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_submit                               avgt    5      7.570 ±    2.967  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_submit_batch                         avgt    5      7.104 ±    4.136  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_suspend                              avgt    5      7.381 ±    3.210  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_suspend_batch                        avgt    5      7.014 ±    8.896  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_timeout                              avgt    5      7.528 ±    3.044  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_timeout_batch                        avgt    5      6.976 ±    4.062  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.task_from_conduit                         avgt    5      2.043 ±    0.360  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.task_from_conduit_batch                   avgt    5      1.846 ±    0.267  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_meet_deadline                       avgt    5      7.429 ±    2.740  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_meet_deadline_batch                 avgt    5      7.049 ±    3.087  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_meet_threshold                      avgt    5      7.309 ±    3.783  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_meet_threshold_batch                avgt    5      8.672 ±    4.440  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_miss_deadline                       avgt    5      7.379 ±    3.362  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_miss_deadline_batch                 avgt    5      6.785 ±    7.838  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_miss_threshold                      avgt    5      7.395 ±    2.292  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_miss_threshold_batch                avgt    5      7.333 ±    2.800  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_signal                              avgt    5      7.828 ±    0.959  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_signal_batch                        avgt    5      8.064 ±    2.319  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.timer_from_conduit                       avgt    5      2.062 ±    0.246  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.timer_from_conduit_batch                 avgt    5      1.838 ±    0.302  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_abort_coordinator             avgt    5      6.281 ±    9.152  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_abort_coordinator_batch       avgt    5      7.272 ±    2.770  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_abort_participant             avgt    5      7.559 ±    2.280  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_abort_participant_batch       avgt    5      6.182 ±    7.553  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_commit_coordinator            avgt    5      8.339 ±    1.026  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_commit_coordinator_batch      avgt    5      5.976 ±    6.306  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_commit_participant            avgt    5      7.418 ±    2.304  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_commit_participant_batch      avgt    5      8.962 ±    0.733  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_compensate_coordinator        avgt    5      6.651 ±    4.082  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_compensate_coordinator_batch  avgt    5      5.786 ±    4.427  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_compensate_participant        avgt    5      7.302 ±    7.412  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_compensate_participant_batch  avgt    5      7.158 ±    2.471  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_conflict_coordinator          avgt    5      7.000 ±    4.232  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_conflict_coordinator_batch    avgt    5      6.700 ±    7.307  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_conflict_participant          avgt    5      6.536 ±    6.185  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_conflict_participant_batch    avgt    5      8.458 ±    2.166  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_expire_coordinator            avgt    5      8.324 ±    3.389  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_expire_coordinator_batch      avgt    5      8.035 ±    2.558  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_expire_participant            avgt    5      8.002 ±    0.927  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_expire_participant_batch      avgt    5      8.401 ±    3.714  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_prepare_coordinator           avgt    5      7.418 ±    4.780  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_prepare_coordinator_batch     avgt    5      6.121 ±    7.782  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_prepare_participant           avgt    5      7.646 ±    2.839  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_prepare_participant_batch     avgt    5      6.770 ±    6.587  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_rollback_coordinator          avgt    5      7.251 ±    2.653  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_rollback_coordinator_batch    avgt    5      8.332 ±    3.977  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_rollback_participant          avgt    5      6.507 ±    7.045  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_rollback_participant_batch    avgt    5      8.388 ±    1.480  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_signal                        avgt    5      7.630 ±    3.183  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_signal_batch                  avgt    5      7.316 ±    3.016  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_start_coordinator             avgt    5      7.219 ±    1.888  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_start_coordinator_batch       avgt    5      7.136 ±    2.980  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_start_participant             avgt    5      8.173 ±    1.296  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_start_participant_batch       avgt    5      6.793 ±    8.960  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.transaction_from_conduit           avgt    5      2.097 ±    0.317  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.transaction_from_conduit_batch     avgt    5      1.868 ±    0.252  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.breaker_from_conduit                   avgt    5      2.085 ±    0.432  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.breaker_from_conduit_batch             avgt    5      1.850 ±    0.213  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_close                             avgt    5      8.581 ±    1.228  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_close_batch                       avgt    5      6.809 ±    3.434  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_half_open                         avgt    5      8.918 ±    7.015  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_half_open_batch                   avgt    5      9.297 ±    2.941  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_open                              avgt    5      7.599 ±    2.983  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_open_batch                        avgt    5      7.208 ±    8.193  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_probe                             avgt    5      7.211 ±    7.755  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_probe_batch                       avgt    5      6.912 ±    1.927  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_reset                             avgt    5      6.639 ±    8.043  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_reset_batch                       avgt    5      6.723 ±    2.961  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_sign                              avgt    5      7.321 ±    7.933  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_sign_batch                        avgt    5      6.978 ±    8.383  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_trip                              avgt    5      7.263 ±    3.094  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_trip_batch                        avgt    5      6.869 ±    5.949  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_fail_egress                          avgt    5      5.799 ±    5.761  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_fail_egress_batch                    avgt    5      7.293 ±    3.113  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_fail_ingress                         avgt    5      8.624 ±    0.871  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_fail_ingress_batch                   avgt    5      7.134 ±    1.979  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_fail_transit                         avgt    5      8.524 ±    1.622  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_fail_transit_batch                   avgt    5      6.645 ±    6.238  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_signal                               avgt    5      7.218 ±    6.424  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_signal_batch                         avgt    5      7.238 ±    4.481  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_success_egress                       avgt    5      7.306 ±    1.776  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_success_egress_batch                 avgt    5      6.953 ±    1.923  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_success_ingress                      avgt    5      8.062 ±    0.539  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_success_ingress_batch                avgt    5      6.810 ±    7.512  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_success_transit                      avgt    5      7.627 ±    2.755  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_success_transit_batch                avgt    5      8.369 ±    3.043  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.flow_from_conduit                         avgt    5      2.062 ±    0.351  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.flow_from_conduit_batch                   avgt    5      1.838 ±    0.303  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_corrupt                            avgt    5      6.187 ±    7.656  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_corrupt_batch                      avgt    5      7.385 ±    4.636  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_drop                               avgt    5      6.826 ±    8.386  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_drop_batch                         avgt    5      6.758 ±    8.060  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_forward                            avgt    5      6.886 ±    3.921  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_forward_batch                      avgt    5      8.883 ±    2.032  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_fragment                           avgt    5      7.298 ±    2.343  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_fragment_batch                     avgt    5      6.854 ±    2.827  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_reassemble                         avgt    5      7.166 ±    7.639  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_reassemble_batch                   avgt    5      7.617 ±    2.141  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_receive                            avgt    5      7.835 ±    0.817  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_receive_batch                      avgt    5      9.120 ±    2.217  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_reorder                            avgt    5      6.610 ±    4.716  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_reorder_batch                      avgt    5      7.288 ±    2.826  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_route                              avgt    5      7.462 ±    8.423  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_route_batch                        avgt    5      7.790 ±    8.181  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_send                               avgt    5      8.806 ±    0.981  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_send_batch                         avgt    5      7.640 ±    8.645  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_sign                               avgt    5      7.199 ±    2.686  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_sign_batch                         avgt    5      7.264 ±    3.701  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.router_from_conduit                     avgt    5      2.045 ±    0.351  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.router_from_conduit_batch               avgt    5      1.853 ±    0.280  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_contract                            avgt    5      7.382 ±    2.746  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_contract_batch                      avgt    5      6.951 ±    2.743  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_deny                                avgt    5      8.069 ±    0.550  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_deny_batch                          avgt    5      7.295 ±    3.825  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_drain                               avgt    5      7.187 ±    2.243  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_drain_batch                         avgt    5      7.277 ±    3.648  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_drop                                avgt    5      6.863 ±    1.759  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_drop_batch                          avgt    5      7.668 ±    6.400  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_expand                              avgt    5      8.586 ±    7.422  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_expand_batch                        avgt    5      8.536 ±    1.913  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_pass                                avgt    5      7.803 ±    0.386  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_pass_batch                          avgt    5      7.099 ±    8.615  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_sign                                avgt    5      7.399 ±    3.819  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_sign_batch                          avgt    5      7.420 ±    6.489  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.valve_from_conduit                       avgt    5      2.074 ±    0.335  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.valve_from_conduit_batch                 avgt    5      1.855 ±    0.216  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_contract_provider                avgt    5      6.510 ±    5.319  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_contract_provider_batch          avgt    5      7.181 ±    3.041  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_contract_receiver                avgt    5      5.966 ±    6.375  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_contract_receiver_batch          avgt    5      6.511 ±    9.763  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_full_exchange                    avgt    5      9.638 ±    1.484  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_full_exchange_batch              avgt    5      8.242 ±    3.719  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_signal                           avgt    5      7.631 ±    2.044  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_signal_batch                     avgt    5      8.266 ±    1.304  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_transfer_provider                avgt    5      9.601 ±    2.996  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_transfer_provider_batch          avgt    5      6.521 ±    2.978  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_transfer_receiver                avgt    5      8.667 ±    0.218  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_transfer_receiver_batch          avgt    5      7.273 ±    7.183  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.exchange_from_conduit                 avgt    5      2.083 ±    0.315  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.exchange_from_conduit_batch           avgt    5      1.847 ±    0.272  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_acquire                             avgt    5      7.505 ±    3.041  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_acquire_batch                       avgt    5      6.511 ±    6.719  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_acquired                            avgt    5      6.725 ±    8.295  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_acquired_batch                      avgt    5      6.950 ±    8.763  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_denied                              avgt    5      6.952 ±    5.849  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_denied_batch                        avgt    5      7.363 ±    3.053  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_deny                                avgt    5      6.931 ±    6.398  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_deny_batch                          avgt    5      6.361 ±    7.217  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_expire                              avgt    5      7.409 ±    3.501  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_expire_batch                        avgt    5      6.551 ±    7.453  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_expired                             avgt    5      7.660 ±    1.068  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_expired_batch                       avgt    5      8.744 ±    3.388  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_extend                              avgt    5      6.194 ±    9.174  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_extend_batch                        avgt    5     10.067 ±    0.781  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_extended                            avgt    5      8.345 ±    3.386  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_extended_batch                      avgt    5      7.013 ±    2.936  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_grant                               avgt    5      6.741 ±    7.383  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_grant_batch                         avgt    5      6.272 ±    9.169  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_granted                             avgt    5      7.721 ±    3.367  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_granted_batch                       avgt    5      7.099 ±    8.084  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_probe                               avgt    5      7.467 ±    7.290  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_probe_batch                         avgt    5      7.194 ±    4.143  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_probed                              avgt    5      8.564 ±    3.191  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_probed_batch                        avgt    5      7.200 ±    2.809  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_release                             avgt    5      9.451 ±    4.860  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_release_batch                       avgt    5      9.574 ±    0.665  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_released                            avgt    5      6.946 ±    6.065  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_released_batch                      avgt    5      6.001 ±    7.577  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_renew                               avgt    5      7.582 ±    2.942  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_renew_batch                         avgt    5      6.931 ±    1.937  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_renewed                             avgt    5      7.852 ±    0.809  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_renewed_batch                       avgt    5      7.308 ±    3.102  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_revoke                              avgt    5      7.412 ±    1.524  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_revoke_batch                        avgt    5      8.862 ±    4.103  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_revoked                             avgt    5      7.594 ±    2.386  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_revoked_batch                       avgt    5      6.859 ±    2.796  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_signal                              avgt    5      7.915 ±    3.971  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_signal_batch                        avgt    5      8.359 ±    1.687  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.lease_from_conduit                       avgt    5      2.077 ±    0.321  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.lease_from_conduit_batch                 avgt    5      1.845 ±    0.313  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_borrow                               avgt    5      5.998 ±    7.782  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_borrow_batch                         avgt    5      7.446 ±    8.474  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_contract                             avgt    5      7.892 ±    5.009  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_contract_batch                       avgt    5      6.804 ±    2.893  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_expand                               avgt    5      7.350 ±    6.032  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_expand_batch                         avgt    5      6.791 ±    3.054  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_reclaim                              avgt    5      7.158 ±    5.860  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_reclaim_batch                        avgt    5      6.848 ±   10.527  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_sign                                 avgt    5      7.314 ±   10.061  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_sign_batch                           avgt    5      7.208 ±    8.173  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.pool_from_conduit                         avgt    5      2.074 ±    0.301  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.pool_from_conduit_batch                   avgt    5      1.862 ±    0.282  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_acquire                          avgt    5      6.561 ±    8.747  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_acquire_batch                    avgt    5      7.538 ±    4.364  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_attempt                          avgt    5      7.185 ±    1.643  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_attempt_batch                    avgt    5      7.064 ±    8.088  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_deny                             avgt    5      6.593 ±    8.724  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_deny_batch                       avgt    5      7.242 ±    2.833  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_grant                            avgt    5      7.546 ±    3.403  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_grant_batch                      avgt    5      8.059 ±    1.099  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_release                          avgt    5      7.379 ±    1.354  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_release_batch                    avgt    5      7.132 ±    3.941  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_sign                             avgt    5      7.731 ±    3.097  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_sign_batch                       avgt    5      8.707 ±   14.477  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_timeout                          avgt    5      9.118 ±    1.823  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_timeout_batch                    avgt    5      7.037 ±    3.325  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.resource_from_conduit                 avgt    5      2.051 ±    0.393  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.resource_from_conduit_batch           avgt    5      1.827 ±    0.327  ns/op
i.h.serventis.jmh.opt.role.ActorOps.actor_from_conduit                       avgt    5      2.056 ±    0.294  ns/op
i.h.serventis.jmh.opt.role.ActorOps.actor_from_conduit_batch                 avgt    5      1.843 ±    0.265  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_acknowledge                         avgt    5      6.957 ±   11.117  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_acknowledge_batch                   avgt    5      7.153 ±    3.516  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_affirm                              avgt    5      7.213 ±    2.298  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_affirm_batch                        avgt    5      6.860 ±    2.021  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_ask                                 avgt    5      6.171 ±    6.228  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_ask_batch                           avgt    5      7.172 ±    3.664  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_clarify                             avgt    5      7.365 ±    6.809  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_clarify_batch                       avgt    5      9.459 ±    3.534  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_command                             avgt    5      7.235 ±    8.819  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_command_batch                       avgt    5      7.345 ±    3.744  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_deliver                             avgt    5      7.227 ±    6.910  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_deliver_batch                       avgt    5      9.309 ±    4.051  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_deny                                avgt    5      7.549 ±    4.571  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_deny_batch                          avgt    5      7.303 ±    4.005  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_explain                             avgt    5      7.386 ±    3.454  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_explain_batch                       avgt    5      7.624 ±    7.024  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_promise                             avgt    5      6.717 ±    6.926  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_promise_batch                       avgt    5      7.175 ±    4.494  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_report                              avgt    5      6.847 ±    8.208  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_report_batch                        avgt    5      7.212 ±    2.632  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_request                             avgt    5      9.287 ±    2.845  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_request_batch                       avgt    5      7.169 ±    3.799  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_sign                                avgt    5      7.218 ±    8.294  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_sign_batch                          avgt    5      8.178 ±    3.899  ns/op
i.h.serventis.jmh.opt.role.AgentOps.agent_from_conduit                       avgt    5      2.065 ±    0.309  ns/op
i.h.serventis.jmh.opt.role.AgentOps.agent_from_conduit_batch                 avgt    5      1.852 ±    0.232  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_accept                              avgt    5      6.350 ±    7.756  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_accept_batch                        avgt    5      7.075 ±    2.652  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_accepted                            avgt    5      7.213 ±    1.735  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_accepted_batch                      avgt    5      8.732 ±    3.493  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_breach                              avgt    5      7.565 ±    3.291  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_breach_batch                        avgt    5      8.720 ±    3.574  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_breached                            avgt    5      8.323 ±    2.930  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_breached_batch                      avgt    5      5.913 ±    7.163  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_depend                              avgt    5      6.830 ±    6.403  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_depend_batch                        avgt    5      6.165 ±    7.565  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_depended                            avgt    5      7.988 ±    0.578  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_depended_batch                      avgt    5      8.338 ±    0.718  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_fulfill                             avgt    5      7.044 ±    2.255  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_fulfill_batch                       avgt    5      6.687 ±    2.668  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_fulfilled                           avgt    5      7.781 ±    3.761  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_fulfilled_batch                     avgt    5      8.539 ±    2.630  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_inquire                             avgt    5      6.751 ±    8.698  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_inquire_batch                       avgt    5      6.340 ±    8.168  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_inquired                            avgt    5      7.886 ±    2.206  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_inquired_batch                      avgt    5      8.949 ±    1.263  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_observe                             avgt    5      7.527 ±    2.412  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_observe_batch                       avgt    5      8.263 ±    0.681  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_observed                            avgt    5      7.134 ±    7.142  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_observed_batch                      avgt    5      8.535 ±    3.492  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_offer                               avgt    5      7.252 ±    3.558  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_offer_batch                         avgt    5      7.093 ±    2.890  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_offered                             avgt    5      7.613 ±    3.234  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_offered_batch                       avgt    5      7.292 ±    2.234  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_promise                             avgt    5      6.903 ±    5.894  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_promise_batch                       avgt    5      8.222 ±    1.326  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_promised                            avgt    5      8.381 ±    3.872  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_promised_batch                      avgt    5      8.889 ±    4.486  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_retract                             avgt    5      6.716 ±    8.167  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_retract_batch                       avgt    5      8.123 ±    2.282  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_retracted                           avgt    5      6.742 ±    3.976  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_retracted_batch                     avgt    5      6.006 ±    7.698  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_signal                              avgt    5      7.432 ±    3.254  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_signal_batch                        avgt    5      6.436 ±    5.613  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_validate                            avgt    5      7.412 ±    2.776  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_validate_batch                      avgt    5      9.529 ±    0.783  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_validated                           avgt    5      8.399 ±    0.654  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_validated_batch                     avgt    5      8.399 ±    1.763  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.atomic_from_conduit                     avgt    5      2.070 ±    0.359  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.atomic_from_conduit_batch               avgt    5      1.844 ±    0.245  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_attempt                            avgt    5      7.209 ±    2.819  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_attempt_batch                      avgt    5      7.159 ±    2.421  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_backoff                            avgt    5      7.474 ±    4.280  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_backoff_batch                      avgt    5      7.410 ±    7.331  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_exhaust                            avgt    5      8.108 ±    5.397  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_exhaust_batch                      avgt    5      7.249 ±    4.223  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_fail                               avgt    5      7.123 ±    5.867  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_fail_batch                         avgt    5      7.113 ±    4.099  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_park                               avgt    5      8.835 ±    3.018  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_park_batch                         avgt    5      7.309 ±    3.454  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_sign                               avgt    5      7.721 ±    3.959  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_sign_batch                         avgt    5      5.988 ±    5.443  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_spin                               avgt    5      7.714 ±    2.853  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_spin_batch                         avgt    5      6.712 ±    9.505  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_success                            avgt    5      7.457 ±    8.329  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_success_batch                      avgt    5      7.057 ±    7.222  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_yield                              avgt    5      7.376 ±    1.658  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_yield_batch                        avgt    5      7.367 ±    8.845  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_abandon                             avgt    5      9.455 ±    4.010  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_abandon_batch                       avgt    5      6.896 ±    9.702  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_arrive                              avgt    5      7.355 ±    2.581  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_arrive_batch                        avgt    5      6.713 ±    6.950  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_await                               avgt    5      7.322 ±    3.809  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_await_batch                         avgt    5      5.700 ±    3.554  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_release                             avgt    5      6.513 ±    7.831  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_release_batch                       avgt    5      7.123 ±    3.831  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_reset                               avgt    5      6.414 ±    7.897  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_reset_batch                         avgt    5      7.113 ±   10.849  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_sign                                avgt    5      6.627 ±    4.297  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_sign_batch                          avgt    5      7.027 ±    6.926  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_timeout                             avgt    5      7.018 ±    9.341  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_timeout_batch                       avgt    5      6.039 ±    7.027  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.latch_from_conduit                       avgt    5      2.073 ±    0.369  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.latch_from_conduit_batch                 avgt    5      1.843 ±    0.301  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_abandon                              avgt    5      6.961 ±    2.853  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_abandon_batch                        avgt    5      7.208 ±    3.903  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_acquire                              avgt    5      8.615 ±    3.457  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_acquire_batch                        avgt    5      6.337 ±    6.330  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_attempt                              avgt    5      7.267 ±   10.192  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_attempt_batch                        avgt    5      7.022 ±    2.613  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_contest                              avgt    5      6.575 ±    9.598  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_contest_batch                        avgt    5      7.609 ±    0.568  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_deny                                 avgt    5      6.450 ±    7.965  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_deny_batch                           avgt    5      5.882 ±    2.585  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_downgrade                            avgt    5      6.670 ±    8.601  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_downgrade_batch                      avgt    5      7.143 ±    6.174  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_grant                                avgt    5      7.537 ±    3.192  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_grant_batch                          avgt    5      7.055 ±    2.624  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_release                              avgt    5      7.136 ±    8.056  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_release_batch                        avgt    5      6.540 ±    9.400  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_sign                                 avgt    5      7.918 ±    1.302  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_sign_batch                           avgt    5      7.761 ±    4.910  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_timeout                              avgt    5      6.852 ±    9.244  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_timeout_batch                        avgt    5      6.917 ±    9.514  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_upgrade                              avgt    5      8.461 ±    1.754  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_upgrade_batch                        avgt    5      7.021 ±    3.754  ns/op
i.h.serventis.jmh.opt.sync.LockOps.lock_from_conduit                         avgt    5      2.065 ±    0.401  ns/op
i.h.serventis.jmh.opt.sync.LockOps.lock_from_conduit_batch                   avgt    5      1.834 ±    0.271  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.counter_from_conduit                   avgt    5      2.043 ±    0.379  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.counter_from_conduit_batch             avgt    5      1.845 ±    0.304  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_increment                         avgt    5      7.264 ±    1.818  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_increment_batch                   avgt    5      6.914 ±    7.248  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_overflow                          avgt    5      8.496 ±    1.359  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_overflow_batch                    avgt    5      9.002 ±    3.096  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_reset                             avgt    5      7.351 ±    9.167  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_reset_batch                       avgt    5      6.942 ±    8.727  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_sign                              avgt    5      6.602 ±    4.081  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_sign_batch                        avgt    5      6.929 ±    3.473  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_decrement                           avgt    5      8.713 ±    2.155  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_decrement_batch                     avgt    5      7.067 ±    2.299  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_increment                           avgt    5      6.823 ±    9.495  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_increment_batch                     avgt    5      7.397 ±    4.337  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_overflow                            avgt    5      7.196 ±    9.228  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_overflow_batch                      avgt    5      6.929 ±    3.155  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_reset                               avgt    5      7.715 ±    3.136  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_reset_batch                         avgt    5      6.718 ±    8.813  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_sign                                avgt    5      6.983 ±    5.476  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_sign_batch                          avgt    5      8.260 ±    4.483  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_underflow                           avgt    5      7.964 ±    3.049  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_underflow_batch                     avgt    5      7.305 ±    3.983  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.gauge_from_conduit                       avgt    5      2.071 ±    0.282  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.gauge_from_conduit_batch                 avgt    5      1.839 ±    0.310  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_debug                                 avgt    5      7.238 ±    2.377  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_debug_batch                           avgt    5      7.342 ±    8.309  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_info                                  avgt    5      7.275 ±    7.980  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_info_batch                            avgt    5      7.406 ±    4.043  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_severe                                avgt    5      7.030 ±    1.720  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_severe_batch                          avgt    5      7.455 ±    6.287  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_sign                                  avgt    5      7.634 ±    3.766  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_sign_batch                            avgt    5      7.461 ±    2.330  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_warning                               avgt    5      7.657 ±    4.051  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_warning_batch                         avgt    5      7.002 ±    2.719  ns/op
i.h.serventis.jmh.opt.tool.LogOps.log_from_conduit                           avgt    5      2.094 ±    0.247  ns/op
i.h.serventis.jmh.opt.tool.LogOps.log_from_conduit_batch                     avgt    5      1.832 ±    0.239  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_connect                             avgt    5      7.339 ±    1.984  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_connect_batch                       avgt    5      8.692 ±    3.628  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_connected                           avgt    5      7.438 ±    3.386  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_connected_batch                     avgt    5      8.572 ±    4.216  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_disconnect                          avgt    5      6.551 ±    7.762  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_disconnect_batch                    avgt    5      6.103 ±    6.580  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_disconnected                        avgt    5      7.654 ±    3.231  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_disconnected_batch                  avgt    5      8.327 ±    1.906  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_fail                                avgt    5      7.238 ±    2.613  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_fail_batch                          avgt    5      6.195 ±    8.196  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_failed                              avgt    5      7.821 ±    0.530  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_failed_batch                        avgt    5      6.998 ±    8.948  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_process                             avgt    5      7.563 ±    3.114  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_process_batch                       avgt    5      6.568 ±    6.695  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_processed                           avgt    5      8.073 ±    1.804  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_processed_batch                     avgt    5      6.713 ±    7.568  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_receive_batch                       avgt    5      7.241 ±    2.541  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_received_batch                      avgt    5      7.111 ±    3.370  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_signal                              avgt    5      7.714 ±    3.662  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_signal_batch                        avgt    5      6.699 ±    7.215  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_succeed                             avgt    5      6.527 ±    7.351  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_succeed_batch                       avgt    5      6.342 ±    8.307  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_succeeded                           avgt    5      7.243 ±    2.276  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_succeeded_batch                     avgt    5      8.262 ±    2.642  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transfer                            avgt    5      6.244 ±    3.835  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transfer_inbound                    avgt    5      6.373 ±    6.605  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transfer_outbound                   avgt    5      6.118 ±    9.826  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transferred                         avgt    5      8.525 ±    2.164  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transmit_batch                      avgt    5      9.261 ±    4.385  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transmitted_batch                   avgt    5      8.639 ±    2.820  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.probe_from_conduit                       avgt    5      2.048 ±    0.288  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.probe_from_conduit_batch                 avgt    5      1.848 ±    0.272  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_baseline                     avgt    5      6.987 ±    2.494  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_baseline_batch               avgt    5      6.186 ±    8.724  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_target                       avgt    5      7.447 ±    2.805  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_target_batch                 avgt    5      7.249 ±    3.037  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_threshold                    avgt    5      7.351 ±    1.272  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_threshold_batch              avgt    5      8.626 ±    2.846  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_baseline                     avgt    5      7.754 ±    0.535  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_baseline_batch               avgt    5      7.019 ±    3.118  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_target                       avgt    5      7.502 ±    2.880  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_target_batch                 avgt    5      6.889 ±    2.000  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_threshold                    avgt    5      7.158 ±    3.098  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_threshold_batch              avgt    5      7.159 ±    2.445  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_baseline                   avgt    5      6.467 ±    3.654  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_baseline_batch             avgt    5      6.224 ±    8.656  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_target                     avgt    5      7.621 ±    3.020  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_target_batch               avgt    5      8.318 ±    1.395  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_threshold                  avgt    5      8.475 ±    2.493  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_threshold_batch            avgt    5      8.478 ±    3.648  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_signal                             avgt    5      7.408 ±    4.291  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_signal_batch                       avgt    5      6.904 ±    2.412  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.sensor_from_conduit                     avgt    5      2.061 ±    0.305  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.sensor_from_conduit_batch               avgt    5      1.830 ±    0.290  ns/op
i.h.serventis.jmh.sdk.OperationOps.emit_begin                                avgt    5      6.723 ±    6.288  ns/op
i.h.serventis.jmh.sdk.OperationOps.emit_begin_batch                          avgt    5      7.192 ±    3.430  ns/op
i.h.serventis.jmh.sdk.OperationOps.emit_end                                  avgt    5      6.730 ±    9.462  ns/op
i.h.serventis.jmh.sdk.OperationOps.emit_end_batch                            avgt    5      7.081 ±    1.981  ns/op
i.h.serventis.jmh.sdk.OperationOps.emit_sign                                 avgt    5      7.440 ±    2.139  ns/op
i.h.serventis.jmh.sdk.OperationOps.emit_sign_batch                           avgt    5      7.037 ±    3.735  ns/op
i.h.serventis.jmh.sdk.OperationOps.operation_from_conduit                    avgt    5      2.077 ±    0.271  ns/op
i.h.serventis.jmh.sdk.OperationOps.operation_from_conduit_batch              avgt    5      1.830 ±    0.266  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.emit_fail                                   avgt    5      7.721 ±    3.036  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.emit_fail_batch                             avgt    5      7.083 ±    8.276  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.emit_sign                                   avgt    5      7.390 ±    2.198  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.emit_sign_batch                             avgt    5      7.184 ±    3.455  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.emit_success                                avgt    5      7.410 ±    1.862  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.emit_success_batch                          avgt    5      6.480 ±    8.502  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.outcome_from_conduit                        avgt    5      2.044 ±    0.283  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.outcome_from_conduit_batch                  avgt    5      1.845 ±    0.311  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_mixed_pattern                         avgt    5      0.253 ±    0.036  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_single                                avgt    5      0.835 ±    0.154  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_single_batch                          avgt    5      0.022 ±    0.003  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_varied_batch                          avgt    5      1.671 ±    0.336  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_worst_case                            avgt    5      1.315 ±    0.254  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_critical                             avgt    5      7.333 ±    2.876  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_critical_batch                       avgt    5      5.868 ±    5.813  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_normal                               avgt    5      7.311 ±    2.564  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_normal_batch                         avgt    5      7.017 ±    2.662  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_signal                               avgt    5      6.708 ±    5.352  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_signal_batch                         avgt    5      7.147 ±    2.847  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_warning                              avgt    5      7.268 ±    2.974  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_warning_batch                        avgt    5      8.263 ±    3.636  ns/op
i.h.serventis.jmh.sdk.SituationOps.situation_from_conduit                    avgt    5      2.079 ±    0.265  ns/op
i.h.serventis.jmh.sdk.SituationOps.situation_from_conduit_batch              avgt    5      1.864 ±    0.294  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_converging_confirmed                    avgt    5      6.786 ±    8.340  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_converging_confirmed_batch              avgt    5      5.757 ±    8.373  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_defective_tentative                     avgt    5      7.378 ±    6.611  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_defective_tentative_batch               avgt    5      5.568 ±    6.642  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_degraded_measured                       avgt    5      7.358 ±    1.229  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_degraded_measured_batch                 avgt    5      8.111 ±    4.397  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_down_confirmed                          avgt    5      7.657 ±    3.445  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_down_confirmed_batch                    avgt    5      7.093 ±    3.219  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_signal                                  avgt    5      6.386 ±    6.729  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_signal_batch                            avgt    5      7.105 ±    4.495  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_stable_confirmed                        avgt    5      7.865 ±    3.057  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_stable_confirmed_batch                  avgt    5      8.539 ±    3.754  ns/op
i.h.serventis.jmh.sdk.StatusOps.status_from_conduit                          avgt    5      2.067 ±    0.358  ns/op
i.h.serventis.jmh.sdk.StatusOps.status_from_conduit_batch                    avgt    5      1.838 ±    0.239  ns/op
i.h.serventis.jmh.sdk.SurveyOps.emit_divided                                 avgt    5      6.740 ±    0.954  ns/op
i.h.serventis.jmh.sdk.SurveyOps.emit_divided_batch                           avgt    5      5.939 ±    8.409  ns/op
i.h.serventis.jmh.sdk.SurveyOps.emit_majority                                avgt    5      6.283 ±    7.720  ns/op
i.h.serventis.jmh.sdk.SurveyOps.emit_majority_batch                          avgt    5      6.967 ±    7.512  ns/op
i.h.serventis.jmh.sdk.SurveyOps.emit_signal                                  avgt    5      8.356 ±    0.743  ns/op
i.h.serventis.jmh.sdk.SurveyOps.emit_signal_batch                            avgt    5      7.783 ±    0.945  ns/op
i.h.serventis.jmh.sdk.SurveyOps.emit_unanimous                               avgt    5      6.746 ±    0.974  ns/op
i.h.serventis.jmh.sdk.SurveyOps.emit_unanimous_batch                         avgt    5      8.898 ±    0.756  ns/op
i.h.serventis.jmh.sdk.SurveyOps.survey_from_conduit                          avgt    5      2.066 ±    0.219  ns/op
i.h.serventis.jmh.sdk.SurveyOps.survey_from_conduit_batch                    avgt    5      1.841 ±    0.337  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_alarm_flow                              avgt    5      6.432 ±    4.025  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_alarm_flow_batch                        avgt    5      7.313 ±    2.172  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_fault_link                              avgt    5      6.951 ±    6.459  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_fault_link_batch                        avgt    5      6.084 ±    7.489  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_limit_time                              avgt    5      6.602 ±    8.318  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_limit_time_batch                        avgt    5      8.257 ±    1.470  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_normal_space                            avgt    5      7.183 ±    2.273  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_normal_space_batch                      avgt    5      7.209 ±    3.036  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_signal                                  avgt    5      7.059 ±    7.066  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_signal_batch                            avgt    5      7.340 ±    3.083  ns/op
i.h.serventis.jmh.sdk.SystemOps.system_from_conduit                          avgt    5      2.074 ±    0.282  ns/op
i.h.serventis.jmh.sdk.SystemOps.system_from_conduit_batch                    avgt    5      1.854 ±    0.288  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_chaos                                    avgt    5      7.255 ±    2.229  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_chaos_batch                              avgt    5      6.828 ±    2.200  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_cycle                                    avgt    5      7.530 ±    3.383  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_cycle_batch                              avgt    5      6.513 ±    8.594  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_drift                                    avgt    5      6.762 ±    6.615  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_drift_batch                              avgt    5      6.828 ±    9.174  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_sign                                     avgt    5      7.166 ±    9.728  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_sign_batch                               avgt    5      6.969 ±    3.213  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_spike                                    avgt    5      6.738 ±    7.708  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_spike_batch                              avgt    5      7.772 ±    7.290  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_stable                                   avgt    5      8.551 ±    3.937  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_stable_batch                             avgt    5      7.480 ±    4.803  ns/op
i.h.serventis.jmh.sdk.TrendOps.trend_from_conduit                            avgt    5      2.059 ±    0.303  ns/op
i.h.serventis.jmh.sdk.TrendOps.trend_from_conduit_batch                      avgt    5      1.840 ±    0.233  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.cycle_from_conduit                       avgt    5      2.081 ±    0.369  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.cycle_from_conduit_batch                 avgt    5      1.836 ±    0.233  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_repeat                              avgt    5      7.091 ±    1.723  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_repeat_batch                        avgt    5      8.118 ±    1.209  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_return                              avgt    5      6.673 ±    1.351  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_return_batch                        avgt    5      6.454 ±    4.459  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_signal                              avgt    5      5.531 ±    6.373  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_signal_batch                        avgt    5      6.427 ±    0.504  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_single                              avgt    5      7.128 ±    2.819  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_single_batch                        avgt    5      5.619 ±    7.141  ns/op
i.h.substrates.jmh.CircuitOps.conduit_create_close                           avgt    5    295.346 ±  111.509  ns/op
i.h.substrates.jmh.CircuitOps.conduit_create_named                           avgt    5    292.128 ±  190.985  ns/op
i.h.substrates.jmh.CircuitOps.conduit_create_with_flow                       avgt    5    279.859 ±   98.983  ns/op
i.h.substrates.jmh.CircuitOps.create_and_close                               avgt    5    459.049 ± 1039.228  ns/op
i.h.substrates.jmh.CircuitOps.create_and_close_batch                         avgt    5    442.932 ±  909.293  ns/op
i.h.substrates.jmh.CircuitOps.create_multiple_and_close                      avgt    5   2110.757 ± 3734.400  ns/op
i.h.substrates.jmh.CircuitOps.create_named_and_close                         avgt    5    413.748 ±  884.050  ns/op
i.h.substrates.jmh.CircuitOps.hot_conduit_create                             avgt    5     21.143 ±    3.087  ns/op
i.h.substrates.jmh.CircuitOps.hot_conduit_create_named                       avgt    5     21.025 ±    3.280  ns/op
i.h.substrates.jmh.CircuitOps.hot_conduit_create_with_flow                   avgt    5     24.231 ±    3.171  ns/op
i.h.substrates.jmh.CircuitOps.hot_pipe_async                                 avgt    5      8.604 ±    1.216  ns/op
i.h.substrates.jmh.CircuitOps.hot_pipe_async_with_flow                       avgt    5     10.658 ±    1.545  ns/op
i.h.substrates.jmh.CircuitOps.pipe_async                                     avgt    5    326.284 ±  320.506  ns/op
i.h.substrates.jmh.CircuitOps.pipe_async_with_flow                           avgt    5    320.705 ±  179.162  ns/op
i.h.substrates.jmh.ConduitOps.get_by_name                                    avgt    5      2.049 ±    0.226  ns/op
i.h.substrates.jmh.ConduitOps.get_by_name_batch                              avgt    5      1.824 ±    0.247  ns/op
i.h.substrates.jmh.ConduitOps.get_by_substrate                               avgt    5      2.241 ±    0.354  ns/op
i.h.substrates.jmh.ConduitOps.get_by_substrate_batch                         avgt    5      1.986 ±    0.272  ns/op
i.h.substrates.jmh.ConduitOps.get_cached                                     avgt    5      3.831 ±    0.705  ns/op
i.h.substrates.jmh.ConduitOps.get_cached_batch                               avgt    5      4.049 ±    0.531  ns/op
i.h.substrates.jmh.ConduitOps.subscribe                                      avgt    5    455.344 ±  539.054  ns/op
i.h.substrates.jmh.ConduitOps.subscribe_batch                                avgt    5    448.376 ±  454.258  ns/op
i.h.substrates.jmh.ConduitOps.subscribe_with_emission_await                  avgt    5   7218.967 ±  849.389  ns/op
i.h.substrates.jmh.CortexOps.circuit                                         avgt    5    291.191 ±  102.273  ns/op
i.h.substrates.jmh.CortexOps.circuit_batch                                   avgt    5    288.826 ±  128.915  ns/op
i.h.substrates.jmh.CortexOps.circuit_named                                   avgt    5    282.332 ±   85.835  ns/op
i.h.substrates.jmh.CortexOps.current                                         avgt    5      1.197 ±    0.164  ns/op
i.h.substrates.jmh.CortexOps.name_class                                      avgt    5      1.685 ±    0.237  ns/op
i.h.substrates.jmh.CortexOps.name_enum                                       avgt    5      1.957 ±    0.276  ns/op
i.h.substrates.jmh.CortexOps.name_iterable                                   avgt    5      8.742 ±    1.361  ns/op
i.h.substrates.jmh.CortexOps.name_path                                       avgt    5      2.070 ±    0.346  ns/op
i.h.substrates.jmh.CortexOps.name_path_batch                                 avgt    5      1.830 ±    0.283  ns/op
i.h.substrates.jmh.CortexOps.name_string                                     avgt    5      3.400 ±    0.549  ns/op
i.h.substrates.jmh.CortexOps.name_string_batch                               avgt    5      2.873 ±    0.571  ns/op
i.h.substrates.jmh.CortexOps.scope                                           avgt    5      9.409 ±    1.281  ns/op
i.h.substrates.jmh.CortexOps.scope_batch                                     avgt    5      8.263 ±    1.160  ns/op
i.h.substrates.jmh.CortexOps.scope_named                                     avgt    5      8.844 ±    1.314  ns/op
i.h.substrates.jmh.CortexOps.slot_boolean                                    avgt    5      1.968 ±    0.168  ns/op
i.h.substrates.jmh.CortexOps.slot_double                                     avgt    5      2.021 ±    0.311  ns/op
i.h.substrates.jmh.CortexOps.slot_int                                        avgt    5      1.956 ±    0.222  ns/op
i.h.substrates.jmh.CortexOps.slot_long                                       avgt    5      1.988 ±    0.270  ns/op
i.h.substrates.jmh.CortexOps.slot_string                                     avgt    5      2.012 ±    0.303  ns/op
i.h.substrates.jmh.CortexOps.state_empty                                     avgt    5      0.550 ±    0.071  ns/op
i.h.substrates.jmh.CortexOps.state_empty_batch                               avgt    5     ≈ 10⁻³             ns/op
i.h.substrates.jmh.CyclicOps.cyclic_emit                                     avgt    5      1.305 ±    0.592  ns/op
i.h.substrates.jmh.CyclicOps.cyclic_emit_await                               avgt    5     10.445 ±    1.981  ns/op
i.h.substrates.jmh.CyclicOps.cyclic_emit_deep_await                          avgt    5      4.400 ±    0.513  ns/op
i.h.substrates.jmh.FlowOps.baseline_no_flow_await                            avgt    5     17.899 ±    0.853  ns/op
i.h.substrates.jmh.FlowOps.flow_combined_diff_guard_await                    avgt    5     26.409 ±    0.687  ns/op
i.h.substrates.jmh.FlowOps.flow_combined_diff_sample_await                   avgt    5     21.347 ±    1.941  ns/op
i.h.substrates.jmh.FlowOps.flow_combined_guard_limit_await                   avgt    5     26.597 ±    1.836  ns/op
i.h.substrates.jmh.FlowOps.flow_diff_await                                   avgt    5     27.956 ±    2.962  ns/op
i.h.substrates.jmh.FlowOps.flow_guard_await                                  avgt    5     28.181 ±    3.388  ns/op
i.h.substrates.jmh.FlowOps.flow_limit_await                                  avgt    5     27.343 ±    1.857  ns/op
i.h.substrates.jmh.FlowOps.flow_sample_await                                 avgt    5     20.014 ±    0.926  ns/op
i.h.substrates.jmh.FlowOps.flow_sift_await                                   avgt    5     19.621 ±    0.753  ns/op
i.h.substrates.jmh.IdOps.id_from_subject                                     avgt    5      0.577 ±    0.106  ns/op
i.h.substrates.jmh.IdOps.id_from_subject_batch                               avgt    5      0.001 ±    0.001  ns/op
i.h.substrates.jmh.IdOps.id_toString                                         avgt    5     13.408 ±    1.983  ns/op
i.h.substrates.jmh.IdOps.id_toString_batch                                   avgt    5     14.756 ±    2.333  ns/op
i.h.substrates.jmh.NameOps.name_chained_deep                                 avgt    5      5.848 ±    0.888  ns/op
i.h.substrates.jmh.NameOps.name_chaining                                     avgt    5      9.669 ±    1.472  ns/op
i.h.substrates.jmh.NameOps.name_chaining_batch                               avgt    5      9.035 ±    1.745  ns/op
i.h.substrates.jmh.NameOps.name_compare                                      avgt    5      0.848 ±    0.159  ns/op
i.h.substrates.jmh.NameOps.name_compare_batch                                avgt    5      0.001 ±    0.001  ns/op
i.h.substrates.jmh.NameOps.name_depth                                        avgt    5      0.562 ±    0.057  ns/op
i.h.substrates.jmh.NameOps.name_depth_batch                                  avgt    5      0.001 ±    0.001  ns/op
i.h.substrates.jmh.NameOps.name_enclosure                                    avgt    5      0.613 ±    0.152  ns/op
i.h.substrates.jmh.NameOps.name_from_enum                                    avgt    5      1.980 ±    0.324  ns/op
i.h.substrates.jmh.NameOps.name_from_iterable                                avgt    5      9.851 ±    1.555  ns/op
i.h.substrates.jmh.NameOps.name_from_iterator                                avgt    5      9.774 ±    1.363  ns/op
i.h.substrates.jmh.NameOps.name_from_mapped_iterable                         avgt    5     10.193 ±    1.780  ns/op
i.h.substrates.jmh.NameOps.name_from_name                                    avgt    5      3.964 ±    0.459  ns/op
i.h.substrates.jmh.NameOps.name_from_string                                  avgt    5      3.357 ±    0.679  ns/op
i.h.substrates.jmh.NameOps.name_from_string_batch                            avgt    5      3.171 ±    0.531  ns/op
i.h.substrates.jmh.NameOps.name_interning_chained                            avgt    5     11.283 ±    1.770  ns/op
i.h.substrates.jmh.NameOps.name_interning_same_path                          avgt    5      3.972 ±    0.553  ns/op
i.h.substrates.jmh.NameOps.name_interning_segments                           avgt    5      9.917 ±    1.585  ns/op
i.h.substrates.jmh.NameOps.name_iterate_hierarchy                            avgt    5      1.829 ±    0.308  ns/op
i.h.substrates.jmh.NameOps.name_parsing                                      avgt    5      2.076 ±    0.385  ns/op
i.h.substrates.jmh.NameOps.name_parsing_batch                                avgt    5      1.851 ±    0.301  ns/op
i.h.substrates.jmh.NameOps.name_path_generation                              avgt    5      0.602 ±    0.109  ns/op
i.h.substrates.jmh.NameOps.name_path_generation_batch                        avgt    5      0.001 ±    0.001  ns/op
i.h.substrates.jmh.PipeOps.async_emit_batch                                  avgt    5     10.276 ±    1.210  ns/op
i.h.substrates.jmh.PipeOps.async_emit_batch_await                            avgt    5     18.389 ±    1.420  ns/op
i.h.substrates.jmh.PipeOps.async_emit_chained_await                          avgt    5     18.238 ±    1.125  ns/op
i.h.substrates.jmh.PipeOps.async_emit_fanout_await                           avgt    5     20.213 ±    1.703  ns/op
i.h.substrates.jmh.PipeOps.async_emit_single                                 avgt    5      7.203 ±    1.948  ns/op
i.h.substrates.jmh.PipeOps.async_emit_single_await                           avgt    5   7195.851 ±  817.834  ns/op
i.h.substrates.jmh.PipeOps.async_emit_with_flow_await                        avgt    5     19.999 ±    1.662  ns/op
i.h.substrates.jmh.PipeOps.baseline_blackhole                                avgt    5      0.295 ±    0.041  ns/op
i.h.substrates.jmh.PipeOps.baseline_counter                                  avgt    5      1.846 ±    0.250  ns/op
i.h.substrates.jmh.PipeOps.baseline_receptor                                 avgt    5      0.296 ±    0.048  ns/op
i.h.substrates.jmh.PipeOps.pipe_create                                       avgt    5      8.447 ±    1.117  ns/op
i.h.substrates.jmh.PipeOps.pipe_create_chained                               avgt    5      0.946 ±    0.246  ns/op
i.h.substrates.jmh.PipeOps.pipe_create_with_flow                             avgt    5     13.765 ±    2.127  ns/op
i.h.substrates.jmh.ReservoirOps.baseline_emit_no_reservoir_await             avgt    5    101.440 ±    6.438  ns/op
i.h.substrates.jmh.ReservoirOps.baseline_emit_no_reservoir_await_batch       avgt    5     19.228 ±    2.228  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_burst_then_drain_await             avgt    5     95.330 ±   20.620  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_burst_then_drain_await_batch       avgt    5     22.141 ±    3.427  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_drain_await                        avgt    5    100.741 ±   11.864  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_drain_await_batch                  avgt    5     22.083 ±    2.060  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_emit_drain_cycles_await            avgt    5    391.885 ±   31.722  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_emit_with_capture_await            avgt    5     93.499 ±   17.136  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_emit_with_capture_await_batch      avgt    5     19.525 ±    1.421  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_process_emissions_await            avgt    5    101.573 ±    5.914  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_process_emissions_await_batch      avgt    5     22.103 ±    1.212  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_process_subjects_await             avgt    5    102.832 ±    8.972  ns/op
i.h.substrates.jmh.ScopeOps.scope_child_anonymous                            avgt    5     19.097 ±    2.651  ns/op
i.h.substrates.jmh.ScopeOps.scope_child_anonymous_batch                      avgt    5     18.503 ±    2.819  ns/op
i.h.substrates.jmh.ScopeOps.scope_child_named                                avgt    5     19.630 ±    2.774  ns/op
i.h.substrates.jmh.ScopeOps.scope_child_named_batch                          avgt    5     18.865 ±    2.989  ns/op
i.h.substrates.jmh.ScopeOps.scope_close_idempotent                           avgt    5      2.744 ±    0.460  ns/op
i.h.substrates.jmh.ScopeOps.scope_close_idempotent_batch                     avgt    5      0.039 ±    0.005  ns/op
i.h.substrates.jmh.ScopeOps.scope_closure                                    avgt    5    302.408 ±   30.989  ns/op
i.h.substrates.jmh.ScopeOps.scope_closure_batch                              avgt    5    300.902 ±   64.490  ns/op
i.h.substrates.jmh.ScopeOps.scope_complex                                    avgt    5    923.988 ±  150.344  ns/op
i.h.substrates.jmh.ScopeOps.scope_create_and_close                           avgt    5      2.760 ±    0.458  ns/op
i.h.substrates.jmh.ScopeOps.scope_create_and_close_batch                     avgt    5      0.038 ±    0.004  ns/op
i.h.substrates.jmh.ScopeOps.scope_create_named                               avgt    5      2.797 ±    0.402  ns/op
i.h.substrates.jmh.ScopeOps.scope_create_named_batch                         avgt    5      0.039 ±    0.005  ns/op
i.h.substrates.jmh.ScopeOps.scope_hierarchy                                  avgt    5     30.137 ±    5.311  ns/op
i.h.substrates.jmh.ScopeOps.scope_hierarchy_batch                            avgt    5     29.060 ±    6.459  ns/op
i.h.substrates.jmh.ScopeOps.scope_parent_closes_children                     avgt    5     47.439 ±    6.633  ns/op
i.h.substrates.jmh.ScopeOps.scope_parent_closes_children_batch               avgt    5     47.104 ±    7.796  ns/op
i.h.substrates.jmh.ScopeOps.scope_register_multiple                          avgt    5   1547.991 ±  309.917  ns/op
i.h.substrates.jmh.ScopeOps.scope_register_multiple_batch                    avgt    5   1445.652 ±  418.767  ns/op
i.h.substrates.jmh.ScopeOps.scope_register_single                            avgt    5    290.013 ±   76.604  ns/op
i.h.substrates.jmh.ScopeOps.scope_register_single_batch                      avgt    5    303.680 ±   74.870  ns/op
i.h.substrates.jmh.ScopeOps.scope_with_resources                             avgt    5    614.638 ±   64.215  ns/op
i.h.substrates.jmh.StateOps.slot_name                                        avgt    5      0.573 ±    0.066  ns/op
i.h.substrates.jmh.StateOps.slot_name_batch                                  avgt    5      0.001 ±    0.001  ns/op
i.h.substrates.jmh.StateOps.slot_type                                        avgt    5      0.560 ±    0.087  ns/op
i.h.substrates.jmh.StateOps.slot_value                                       avgt    5      0.703 ±    0.077  ns/op
i.h.substrates.jmh.StateOps.slot_value_batch                                 avgt    5      0.001 ±    0.001  ns/op
i.h.substrates.jmh.StateOps.state_compact                                    avgt    5     11.192 ±    1.038  ns/op
i.h.substrates.jmh.StateOps.state_compact_batch                              avgt    5     11.576 ±    1.238  ns/op
i.h.substrates.jmh.StateOps.state_iterate_slots                              avgt    5      2.378 ±    0.469  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_int                               avgt    5      3.892 ±    0.432  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_int_batch                         avgt    5      3.936 ±    0.472  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_long                              avgt    5      3.986 ±    0.523  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_object                            avgt    5      2.702 ±    0.438  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_object_batch                      avgt    5      2.662 ±    0.354  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_string                            avgt    5      3.936 ±    0.446  ns/op
i.h.substrates.jmh.StateOps.state_value_read                                 avgt    5      1.656 ±    0.189  ns/op
i.h.substrates.jmh.StateOps.state_value_read_batch                           avgt    5      1.400 ±    0.202  ns/op
i.h.substrates.jmh.StateOps.state_values_stream                              avgt    5      4.020 ±    0.410  ns/op
i.h.substrates.jmh.SubjectOps.subject_compare                                avgt    5      4.310 ±    0.685  ns/op
i.h.substrates.jmh.SubjectOps.subject_compare_batch                          avgt    5      2.728 ±    0.494  ns/op
i.h.substrates.jmh.SubjectOps.subject_compare_same                           avgt    5      0.506 ±    0.155  ns/op
i.h.substrates.jmh.SubjectOps.subject_compare_same_batch                     avgt    5     ≈ 10⁻³             ns/op
i.h.substrates.jmh.SubjectOps.subject_compare_three_way                      avgt    5     12.099 ±    2.317  ns/op
i.h.substrates.jmh.SubscriberOps.close_five_conduits_await                   avgt    5   9252.680 ± 1202.076  ns/op
i.h.substrates.jmh.SubscriberOps.close_five_subscriptions_await              avgt    5   9036.223 ±  601.978  ns/op
i.h.substrates.jmh.SubscriberOps.close_idempotent_await                      avgt    5   9079.015 ±  763.182  ns/op
i.h.substrates.jmh.SubscriberOps.close_idempotent_batch_await                avgt    5     17.904 ±    0.812  ns/op
i.h.substrates.jmh.SubscriberOps.close_no_subscriptions_await                avgt    5   9080.265 ±  867.247  ns/op
i.h.substrates.jmh.SubscriberOps.close_no_subscriptions_batch_await          avgt    5     15.126 ±    4.280  ns/op
i.h.substrates.jmh.SubscriberOps.close_one_subscription_await                avgt    5   8928.578 ±  839.975  ns/op
i.h.substrates.jmh.SubscriberOps.close_one_subscription_batch_await          avgt    5     32.877 ±    3.156  ns/op
i.h.substrates.jmh.SubscriberOps.close_ten_conduits_await                    avgt    5   9066.990 ±  572.622  ns/op
i.h.substrates.jmh.SubscriberOps.close_ten_subscriptions_await               avgt    5   8828.337 ±  285.814  ns/op
i.h.substrates.jmh.SubscriberOps.close_with_pending_emissions_await          avgt    5   8920.998 ±  595.490  ns/op
i.h.substrates.jmh.TapOps.baseline_emit_batch_await                          avgt    5     20.277 ±    2.429  ns/op
i.h.substrates.jmh.TapOps.tap_close                                          avgt    5   8949.843 ±  764.041  ns/op
i.h.substrates.jmh.TapOps.tap_create_batch                                   avgt    5   1155.062 ± 6023.107  ns/op
i.h.substrates.jmh.TapOps.tap_create_identity                                avgt    5    470.664 ±  275.013  ns/op
i.h.substrates.jmh.TapOps.tap_create_string                                  avgt    5    544.429 ±  292.389  ns/op
i.h.substrates.jmh.TapOps.tap_emit_identity_batch_await                      avgt    5     28.651 ±    1.379  ns/op
i.h.substrates.jmh.TapOps.tap_emit_identity_single                           avgt    5     28.303 ±   22.640  ns/op
i.h.substrates.jmh.TapOps.tap_emit_identity_single_await                     avgt    5   8548.929 ±  964.188  ns/op
i.h.substrates.jmh.TapOps.tap_emit_multi_batch_await                         avgt    5     41.396 ±    6.795  ns/op
i.h.substrates.jmh.TapOps.tap_emit_string_batch_await                        avgt    5     35.390 ±    8.300  ns/op
i.h.substrates.jmh.TapOps.tap_lifecycle                                      avgt    5  13372.476 ± 2544.005  ns/op


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