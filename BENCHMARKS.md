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
substrates/jmh.sh

# List available benchmarks
substrates/jmh.sh -l

# Run specific benchmark
substrates/jmh.sh PipeOps.emit_to_empty_pipe

# Run benchmarks matching pattern
substrates/jmh.sh ".*batch"

# Custom JMH parameters
substrates/jmh.sh -wi 5 -i 10 -f 2
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
i.h.serventis.jmh.opt.data.CacheOps.cache_from_conduit                       avgt    5      1.814 ±    0.071  ns/op
i.h.serventis.jmh.opt.data.CacheOps.cache_from_conduit_batch                 avgt    5      1.624 ±    0.021  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_evict                               avgt    5      9.245 ±    6.946  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_evict_batch                         avgt    5      8.154 ±    6.090  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_expire                              avgt    5      9.227 ±    3.584  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_expire_batch                        avgt    5      7.982 ±    2.930  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_hit                                 avgt    5      8.830 ±    6.632  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_hit_batch                           avgt    5      8.986 ±    0.503  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_lookup                              avgt    5      8.767 ±    4.595  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_lookup_batch                        avgt    5      8.446 ±    7.516  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_miss                                avgt    5      8.746 ±    3.699  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_miss_batch                          avgt    5     10.294 ±    0.830  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_remove                              avgt    5      8.994 ±    3.746  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_remove_batch                        avgt    5      9.308 ±    0.545  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_sign                                avgt    5      9.173 ±    6.527  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_sign_batch                          avgt    5      9.482 ±    0.159  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_store                               avgt    5     10.677 ±    0.676  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_store_batch                         avgt    5      8.526 ±    7.513  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_aggregate                        avgt    5     10.151 ±    0.787  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_aggregate_batch                  avgt    5      7.811 ±    9.428  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_backpressure                     avgt    5     11.959 ±    0.548  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_backpressure_batch               avgt    5      8.380 ±    4.694  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_buffer                           avgt    5      8.119 ±    3.177  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_buffer_batch                     avgt    5     10.846 ±    0.991  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_checkpoint                       avgt    5     11.355 ±    0.573  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_checkpoint_batch                 avgt    5     10.765 ±    0.509  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_filter                           avgt    5      9.007 ±    5.933  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_filter_batch                     avgt    5      8.177 ±    3.760  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_input                            avgt    5     11.235 ±   10.147  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_input_batch                      avgt    5      9.711 ±    0.569  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_lag                              avgt    5      8.441 ±    3.854  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_lag_batch                        avgt    5      8.583 ±    8.634  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_output                           avgt    5      9.120 ±    4.491  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_output_batch                     avgt    5      9.837 ±    0.349  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_overflow                         avgt    5     10.671 ±    0.528  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_overflow_batch                   avgt    5     10.320 ±    1.010  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_sign                             avgt    5      8.650 ±    3.218  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_sign_batch                       avgt    5      9.086 ±    0.536  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_skip                             avgt    5     10.724 ±    0.780  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_skip_batch                       avgt    5      9.782 ±    0.502  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_transform                        avgt    5      8.827 ±    5.975  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_transform_batch                  avgt    5      7.878 ±    7.290  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_watermark                        avgt    5     11.381 ±    0.563  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_watermark_batch                  avgt    5     10.819 ±    0.955  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_flow_etl                     avgt    5     54.883 ±    8.898  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_flow_stream                  avgt    5     51.419 ±    5.236  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_flow_windowed                avgt    5     49.081 ±   34.369  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_from_conduit                 avgt    5      1.871 ±    0.069  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_from_conduit_batch           avgt    5      1.662 ±    0.012  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_dequeue                             avgt    5      9.115 ±    7.488  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_dequeue_batch                       avgt    5      8.499 ±    8.882  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_enqueue                             avgt    5      8.602 ±    5.169  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_enqueue_batch                       avgt    5      8.368 ±    6.598  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_overflow                            avgt    5      9.004 ±    6.409  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_overflow_batch                      avgt    5      7.706 ±    3.721  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_sign                                avgt    5      9.415 ±    6.762  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_sign_batch                          avgt    5      9.613 ±    1.136  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_underflow                           avgt    5      8.601 ±    4.189  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_underflow_batch                     avgt    5     10.535 ±    0.680  ns/op
i.h.serventis.jmh.opt.data.QueueOps.queue_from_conduit                       avgt    5      1.865 ±    0.038  ns/op
i.h.serventis.jmh.opt.data.QueueOps.queue_from_conduit_batch                 avgt    5      1.662 ±    0.009  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_overflow                            avgt    5      8.811 ±    4.892  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_overflow_batch                      avgt    5      8.109 ±    8.023  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_pop                                 avgt    5      9.090 ±    4.509  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_pop_batch                           avgt    5      8.821 ±    0.956  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_push                                avgt    5     10.038 ±    0.581  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_push_batch                          avgt    5      8.671 ±    0.800  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_sign                                avgt    5      8.745 ±    4.645  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_sign_batch                          avgt    5      8.136 ±    8.296  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_underflow                           avgt    5      9.891 ±    1.598  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_underflow_batch                     avgt    5      8.332 ±    8.160  ns/op
i.h.serventis.jmh.opt.data.StackOps.stack_from_conduit                       avgt    5      1.876 ±    0.070  ns/op
i.h.serventis.jmh.opt.data.StackOps.stack_from_conduit_batch                 avgt    5      1.662 ±    0.012  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_crash                             avgt    5      8.803 ±    3.596  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_crash_batch                       avgt    5      8.435 ±    7.692  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_fail                              avgt    5      9.089 ±    6.842  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_fail_batch                        avgt    5     10.800 ±    1.326  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_kill                              avgt    5      8.895 ±    6.980  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_kill_batch                        avgt    5      8.297 ±    7.528  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_restart                           avgt    5      9.019 ±    5.475  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_restart_batch                     avgt    5     10.412 ±    1.610  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_resume                            avgt    5      8.320 ±    2.774  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_resume_batch                      avgt    5      8.197 ±    5.299  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_sign                              avgt    5      9.152 ±    3.688  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_sign_batch                        avgt    5      9.369 ±    6.357  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_spawn                             avgt    5     10.716 ±    0.668  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_spawn_batch                       avgt    5      9.433 ±    0.617  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_start                             avgt    5      8.988 ±    6.861  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_start_batch                       avgt    5      8.344 ±    3.865  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_stop                              avgt    5     10.915 ±    0.455  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_stop_batch                        avgt    5     10.426 ±    0.418  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_suspend                           avgt    5      9.194 ±    5.795  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_suspend_batch                     avgt    5     10.433 ±    0.833  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.process_from_conduit                   avgt    5      1.863 ±    0.056  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.process_from_conduit_batch             avgt    5      1.661 ±    0.012  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_call                              avgt    5      8.719 ±    3.540  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_call_batch                        avgt    5     10.298 ±    0.737  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_called                            avgt    5      9.043 ±    2.915  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_called_batch                      avgt    5      9.720 ±    3.248  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_delay                             avgt    5     10.781 ±    3.848  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_delay_batch                       avgt    5     10.789 ±    0.862  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_delayed                           avgt    5     10.651 ±    0.450  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_delayed_batch                     avgt    5      9.778 ±    0.624  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_discard                           avgt    5     10.193 ±    0.373  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_discard_batch                     avgt    5      8.347 ±    3.439  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_discarded                         avgt    5      8.886 ±    3.762  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_discarded_batch                   avgt    5      9.653 ±    0.363  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_disconnect                        avgt    5      9.815 ±    0.457  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_disconnect_batch                  avgt    5     10.403 ±    0.932  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_disconnected                      avgt    5     10.175 ±    0.380  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_disconnected_batch                avgt    5     10.237 ±    1.565  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_expire                            avgt    5      8.209 ±    2.990  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_expire_batch                      avgt    5      8.890 ±    3.226  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_expired                           avgt    5      8.755 ±    3.178  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_expired_batch                     avgt    5      9.708 ±    3.788  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_fail                              avgt    5      8.787 ±    5.205  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_fail_batch                        avgt    5      9.334 ±    3.908  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_failed                            avgt    5      9.184 ±    0.696  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_failed_batch                      avgt    5      8.453 ±    5.968  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_recourse                          avgt    5      8.688 ±    6.031  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_recourse_batch                    avgt    5      9.564 ±    3.112  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_recoursed                         avgt    5      8.546 ±    3.532  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_recoursed_batch                   avgt    5      9.963 ±    6.115  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_redirect                          avgt    5      8.979 ±    5.712  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_redirect_batch                    avgt    5      8.595 ±    3.431  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_redirected                        avgt    5     10.321 ±    0.433  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_redirected_batch                  avgt    5      8.736 ±    0.585  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_reject                            avgt    5      9.576 ±    0.667  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_reject_batch                      avgt    5     11.051 ±    0.748  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_rejected                          avgt    5      8.626 ±    4.651  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_rejected_batch                    avgt    5      8.657 ±    3.987  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_resume                            avgt    5      8.904 ±    5.623  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_resume_batch                      avgt    5      9.708 ±    3.761  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_resumed                           avgt    5     10.897 ±    0.588  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_resumed_batch                     avgt    5      9.733 ±    0.621  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_retried                           avgt    5      8.746 ±    3.768  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_retried_batch                     avgt    5      9.198 ±    4.156  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_retry                             avgt    5      9.248 ±    4.035  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_retry_batch                       avgt    5     10.561 ±    0.531  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_schedule                          avgt    5      9.353 ±    0.329  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_schedule_batch                    avgt    5      8.263 ±    4.066  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_scheduled                         avgt    5      9.494 ±    0.459  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_scheduled_batch                   avgt    5      8.501 ±    3.896  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_signal                            avgt    5      8.674 ±    3.595  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_signal_batch                      avgt    5     10.051 ±    0.979  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_start                             avgt    5      8.489 ±    2.844  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_start_batch                       avgt    5      9.407 ±    0.772  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_started                           avgt    5      9.550 ±    0.505  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_started_batch                     avgt    5     10.692 ±    0.440  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_stop                              avgt    5      9.855 ±    0.940  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_stop_batch                        avgt    5     10.255 ±    0.858  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_stopped                           avgt    5      9.621 ±    0.923  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_stopped_batch                     avgt    5      8.437 ±    4.818  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_succeeded                         avgt    5      9.389 ±    0.686  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_succeeded_batch                   avgt    5      8.400 ±    3.772  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_success                           avgt    5      9.373 ±    5.957  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_success_batch                     avgt    5      8.811 ±    0.581  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_suspend                           avgt    5      9.673 ±    0.692  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_suspend_batch                     avgt    5      8.523 ±    0.500  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_suspended                         avgt    5      9.599 ±    0.701  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_suspended_batch                   avgt    5     11.316 ±    0.291  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.service_from_conduit                   avgt    5      1.871 ±    0.075  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.service_from_conduit_batch             avgt    5      1.660 ±    0.004  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_cancel                               avgt    5      9.012 ±    6.916  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_cancel_batch                         avgt    5      8.739 ±    6.803  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_complete                             avgt    5      8.862 ±    1.298  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_complete_batch                       avgt    5      7.629 ±    4.766  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_fail                                 avgt    5      9.035 ±    3.551  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_fail_batch                           avgt    5      8.644 ±    7.100  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_progress                             avgt    5      8.646 ±    4.535  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_progress_batch                       avgt    5      8.213 ±    6.260  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_reject                               avgt    5      9.726 ±    0.545  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_reject_batch                         avgt    5     10.174 ±    0.256  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_resume                               avgt    5     10.916 ±    0.515  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_resume_batch                         avgt    5      9.551 ±    0.315  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_schedule                             avgt    5     10.035 ±    0.636  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_schedule_batch                       avgt    5      8.953 ±    0.785  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_sign                                 avgt    5      9.055 ±    0.668  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_sign_batch                           avgt    5     10.859 ±    0.631  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_start                                avgt    5      8.870 ±    3.736  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_start_batch                          avgt    5      8.305 ±    3.436  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_submit                               avgt    5      9.050 ±    6.642  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_submit_batch                         avgt    5      9.040 ±    0.676  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_suspend                              avgt    5      8.055 ±    4.535  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_suspend_batch                        avgt    5      7.901 ±    4.638  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_timeout                              avgt    5      9.323 ±    0.793  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_timeout_batch                        avgt    5      8.404 ±    7.735  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.task_from_conduit                         avgt    5      1.868 ±    0.045  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.task_from_conduit_batch                   avgt    5      1.660 ±    0.003  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_abort_coordinator             avgt    5      9.256 ±    5.513  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_abort_coordinator_batch       avgt    5      9.283 ±    0.514  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_abort_participant             avgt    5      8.672 ±    3.229  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_abort_participant_batch       avgt    5      9.133 ±    4.286  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_commit_coordinator            avgt    5      8.805 ±    5.625  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_commit_coordinator_batch      avgt    5      7.834 ±    3.105  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_commit_participant            avgt    5      9.609 ±    0.516  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_commit_participant_batch      avgt    5      8.628 ±    5.031  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_compensate_coordinator        avgt    5      8.239 ±    3.746  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_compensate_coordinator_batch  avgt    5      9.701 ±    3.961  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_compensate_participant        avgt    5      8.537 ±    3.560  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_compensate_participant_batch  avgt    5      8.643 ±    4.268  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_conflict_coordinator          avgt    5      8.099 ±    4.442  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_conflict_coordinator_batch    avgt    5      9.254 ±    3.559  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_conflict_participant          avgt    5      9.406 ±    0.612  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_conflict_participant_batch    avgt    5      8.545 ±    5.435  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_expire_coordinator            avgt    5      8.463 ±    5.854  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_expire_coordinator_batch      avgt    5     10.735 ±    0.822  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_expire_participant            avgt    5      8.474 ±    2.526  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_expire_participant_batch      avgt    5     10.463 ±    1.445  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_prepare_coordinator           avgt    5      9.222 ±    5.502  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_prepare_coordinator_batch     avgt    5     10.015 ±    0.393  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_prepare_participant           avgt    5      9.156 ±    4.569  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_prepare_participant_batch     avgt    5      9.615 ±    4.155  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_rollback_coordinator          avgt    5      9.297 ±    0.526  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_rollback_coordinator_batch    avgt    5      8.337 ±    4.160  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_rollback_participant          avgt    5      9.576 ±    0.391  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_rollback_participant_batch    avgt    5      9.284 ±    2.265  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_signal                        avgt    5      9.351 ±    0.746  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_signal_batch                  avgt    5     10.851 ±    0.679  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_start_coordinator             avgt    5      8.879 ±    3.761  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_start_coordinator_batch       avgt    5     11.547 ±    0.875  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_start_participant             avgt    5      8.754 ±    4.364  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_start_participant_batch       avgt    5      8.666 ±    5.101  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.transaction_from_conduit           avgt    5      1.867 ±    0.074  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.transaction_from_conduit_batch     avgt    5      1.660 ±    0.003  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.breaker_from_conduit                   avgt    5      1.866 ±    0.058  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.breaker_from_conduit_batch             avgt    5      1.661 ±    0.004  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_close                             avgt    5     10.047 ±    0.522  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_close_batch                       avgt    5      8.268 ±    8.617  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_half_open                         avgt    5      9.261 ±    1.237  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_half_open_batch                   avgt    5      8.155 ±    7.115  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_open                              avgt    5      8.841 ±    4.864  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_open_batch                        avgt    5      9.624 ±    1.166  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_probe                             avgt    5      8.857 ±    5.398  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_probe_batch                       avgt    5      9.517 ±    0.573  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_reset                             avgt    5      8.419 ±    3.157  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_reset_batch                       avgt    5     10.238 ±    0.567  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_sign                              avgt    5      8.706 ±    5.286  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_sign_batch                        avgt    5      8.741 ±    6.613  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_trip                              avgt    5      8.546 ±    3.281  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_trip_batch                        avgt    5     10.501 ±    0.575  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_corrupt                            avgt    5      8.861 ±    6.132  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_corrupt_batch                      avgt    5      8.080 ±    4.312  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_drop                               avgt    5      8.879 ±    5.924  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_drop_batch                         avgt    5      7.992 ±    7.590  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_forward                            avgt    5      8.484 ±    3.251  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_forward_batch                      avgt    5      8.859 ±    6.811  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_fragment                           avgt    5      8.928 ±    5.418  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_fragment_batch                     avgt    5      7.627 ±    4.500  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_reassemble                         avgt    5      9.736 ±    1.105  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_reassemble_batch                   avgt    5     10.374 ±    0.870  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_receive                            avgt    5     10.311 ±    0.292  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_receive_batch                      avgt    5      9.624 ±    0.627  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_reorder                            avgt    5     10.485 ±    0.716  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_reorder_batch                      avgt    5      8.007 ±    7.072  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_route                              avgt    5     10.935 ±    1.162  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_route_batch                        avgt    5     10.584 ±    0.209  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_send                               avgt    5     10.533 ±    0.647  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_send_batch                         avgt    5      8.353 ±    7.711  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_sign                               avgt    5      9.084 ±    6.098  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_sign_batch                         avgt    5      8.185 ±    7.229  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.router_from_conduit                     avgt    5      1.867 ±    0.077  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.router_from_conduit_batch               avgt    5      1.660 ±    0.006  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_contract                            avgt    5      8.640 ±    5.329  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_contract_batch                      avgt    5      7.779 ±    3.381  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_deny                                avgt    5      8.007 ±    3.095  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_deny_batch                          avgt    5     10.461 ±    1.088  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_drain                               avgt    5      9.046 ±    6.555  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_drain_batch                         avgt    5      8.627 ±    7.334  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_drop                                avgt    5      8.436 ±    4.602  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_drop_batch                          avgt    5     10.634 ±    1.473  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_expand                              avgt    5     10.654 ±    0.915  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_expand_batch                        avgt    5     10.212 ±    0.393  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_pass                                avgt    5      9.278 ±    5.524  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_pass_batch                          avgt    5      9.477 ±    0.313  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_sign                                avgt    5      9.417 ±    0.766  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_sign_batch                          avgt    5      8.679 ±    9.032  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.valve_from_conduit                       avgt    5      1.872 ±    0.068  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.valve_from_conduit_batch                 avgt    5      1.660 ±    0.002  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_contract_provider                avgt    5      9.625 ±    1.082  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_contract_provider_batch          avgt    5      9.577 ±    3.504  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_contract_receiver                avgt    5      9.748 ±    0.938  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_contract_receiver_batch          avgt    5      8.324 ±    4.320  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_full_exchange                    avgt    5      9.738 ±    4.774  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_full_exchange_batch              avgt    5      9.382 ±    3.199  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_signal                           avgt    5      8.856 ±    2.942  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_signal_batch                     avgt    5      9.307 ±    0.484  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_transfer_provider                avgt    5      9.591 ±    0.621  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_transfer_provider_batch          avgt    5      8.513 ±    3.993  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_transfer_receiver                avgt    5     10.268 ±    0.497  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_transfer_receiver_batch          avgt    5      8.912 ±    0.690  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.exchange_from_conduit                 avgt    5      1.865 ±    0.050  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.exchange_from_conduit_batch           avgt    5      1.659 ±    0.001  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_acquire                             avgt    5      9.840 ±    0.707  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_acquire_batch                       avgt    5     10.204 ±    0.988  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_acquired                            avgt    5      9.932 ±    0.532  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_acquired_batch                      avgt    5      8.416 ±    3.302  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_denied                              avgt    5      8.165 ±    3.361  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_denied_batch                        avgt    5     10.640 ±    0.384  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_deny                                avgt    5      8.713 ±    3.244  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_deny_batch                          avgt    5      9.753 ±    3.365  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_expire                              avgt    5      8.815 ±    3.456  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_expire_batch                        avgt    5     11.398 ±    0.862  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_expired                             avgt    5      9.970 ±    0.469  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_expired_batch                       avgt    5      9.682 ±    3.500  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_extend                              avgt    5      9.172 ±    6.355  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_extend_batch                        avgt    5     10.471 ±    0.775  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_extended                            avgt    5      8.874 ±    3.767  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_extended_batch                      avgt    5      9.284 ±    0.427  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_grant                               avgt    5      8.750 ±    2.791  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_grant_batch                         avgt    5     10.130 ±    4.972  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_granted                             avgt    5      8.719 ±    2.761  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_granted_batch                       avgt    5      7.985 ±    3.918  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_probe                               avgt    5     10.236 ±    0.805  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_probe_batch                         avgt    5      8.158 ±    4.128  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_probed                              avgt    5      8.543 ±    3.585  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_probed_batch                        avgt    5     10.592 ±    0.317  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_release                             avgt    5      9.321 ±    0.147  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_release_batch                       avgt    5      9.980 ±    0.849  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_released                            avgt    5      8.545 ±    2.718  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_released_batch                      avgt    5      9.516 ±    3.815  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_renew                               avgt    5     10.483 ±    0.246  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_renew_batch                         avgt    5     11.170 ±    0.921  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_renewed                             avgt    5     10.272 ±    0.640  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_renewed_batch                       avgt    5      8.514 ±    4.269  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_revoke                              avgt    5     10.506 ±    1.260  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_revoke_batch                        avgt    5     10.519 ±    1.343  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_revoked                             avgt    5      8.770 ±    3.327  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_revoked_batch                       avgt    5      9.709 ±    3.448  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_signal                              avgt    5      8.381 ±    3.585  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_signal_batch                        avgt    5     11.296 ±    1.129  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.lease_from_conduit                       avgt    5      1.866 ±    0.085  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.lease_from_conduit_batch                 avgt    5      1.661 ±    0.005  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_borrow                               avgt    5      8.669 ±    5.184  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_borrow_batch                         avgt    5      7.510 ±    3.084  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_contract                             avgt    5      8.375 ±    4.526  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_contract_batch                       avgt    5     10.269 ±    0.433  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_expand                               avgt    5      8.688 ±    3.488  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_expand_batch                         avgt    5      7.927 ±    5.979  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_reclaim                              avgt    5     10.410 ±    1.473  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_reclaim_batch                        avgt    5      8.064 ±    8.387  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_sign                                 avgt    5      9.063 ±    5.649  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_sign_batch                           avgt    5      9.076 ±    0.786  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.pool_from_conduit                         avgt    5      1.858 ±    0.044  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.pool_from_conduit_batch                   avgt    5      1.660 ±    0.002  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_acquire                          avgt    5      8.859 ±    4.604  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_acquire_batch                    avgt    5      7.905 ±    2.973  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_attempt                          avgt    5      8.725 ±    5.620  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_attempt_batch                    avgt    5      7.943 ±    3.370  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_deny                             avgt    5      8.698 ±    3.387  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_deny_batch                       avgt    5     10.360 ±    0.833  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_grant                            avgt    5      8.519 ±    3.665  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_grant_batch                      avgt    5      8.763 ±    6.871  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_release                          avgt    5     10.635 ±    0.589  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_release_batch                    avgt    5     10.280 ±    1.240  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_sign                             avgt    5      8.462 ±    4.853  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_sign_batch                       avgt    5      8.309 ±    3.817  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_timeout                          avgt    5      8.389 ±    4.037  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_timeout_batch                    avgt    5      8.039 ±    4.618  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.resource_from_conduit                 avgt    5      1.865 ±    0.065  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.resource_from_conduit_batch           avgt    5      1.661 ±    0.013  ns/op
i.h.serventis.jmh.opt.role.ActorOps.actor_from_conduit                       avgt    5      1.869 ±    0.083  ns/op
i.h.serventis.jmh.opt.role.ActorOps.actor_from_conduit_batch                 avgt    5      1.660 ±    0.002  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_acknowledge                         avgt    5      8.970 ±    5.083  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_acknowledge_batch                   avgt    5      9.311 ±    0.850  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_affirm                              avgt    5      8.675 ±    3.119  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_affirm_batch                        avgt    5      8.669 ±    7.664  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_ask                                 avgt    5      8.996 ±    4.564  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_ask_batch                           avgt    5      7.449 ±    6.534  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_clarify                             avgt    5      9.772 ±    0.415  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_clarify_batch                       avgt    5      7.909 ±    5.932  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_command                             avgt    5     10.455 ±    1.253  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_command_batch                       avgt    5      7.886 ±    2.983  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_deliver                             avgt    5      8.551 ±    4.818  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_deliver_batch                       avgt    5      9.223 ±    0.721  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_deny                                avgt    5      9.125 ±    6.715  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_deny_batch                          avgt    5     10.340 ±    1.755  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_explain                             avgt    5      8.588 ±    4.804  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_explain_batch                       avgt    5     10.225 ±    0.687  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_promise                             avgt    5      8.759 ±    6.231  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_promise_batch                       avgt    5     10.775 ±    1.732  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_report                              avgt    5      8.792 ±    5.737  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_report_batch                        avgt    5      7.810 ±    5.787  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_request                             avgt    5     10.247 ±    0.857  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_request_batch                       avgt    5      8.645 ±    7.262  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_sign                                avgt    5      8.608 ±    6.665  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_sign_batch                          avgt    5      7.965 ±    8.336  ns/op
i.h.serventis.jmh.opt.role.AgentOps.agent_from_conduit                       avgt    5      1.858 ±    0.020  ns/op
i.h.serventis.jmh.opt.role.AgentOps.agent_from_conduit_batch                 avgt    5      1.665 ±    0.009  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_accept                              avgt    5      8.985 ±    4.164  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_accept_batch                        avgt    5      9.436 ±    4.004  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_accepted                            avgt    5      8.575 ±    3.946  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_accepted_batch                      avgt    5      9.293 ±    2.010  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_breach                              avgt    5      7.928 ±    2.272  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_breach_batch                        avgt    5      9.571 ±    0.739  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_breached                            avgt    5      9.626 ±    0.562  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_breached_batch                      avgt    5      8.758 ±    4.031  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_depend                              avgt    5      8.047 ±    3.204  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_depend_batch                        avgt    5      9.437 ±    3.480  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_depended                            avgt    5      8.814 ±    3.405  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_depended_batch                      avgt    5      8.418 ±    4.309  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_fulfill                             avgt    5      8.335 ±    3.254  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_fulfill_batch                       avgt    5      8.089 ±    2.860  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_fulfilled                           avgt    5     10.867 ±    1.051  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_fulfilled_batch                     avgt    5     10.279 ±    1.052  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_inquire                             avgt    5      9.193 ±    1.117  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_inquire_batch                       avgt    5     10.512 ±    0.800  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_inquired                            avgt    5      8.617 ±    3.661  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_inquired_batch                      avgt    5      9.985 ±    5.710  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_observe                             avgt    5     10.359 ±    0.763  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_observe_batch                       avgt    5     10.551 ±    0.494  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_observed                            avgt    5      9.408 ±    0.336  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_observed_batch                      avgt    5      7.565 ±    8.851  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_offer                               avgt    5      8.412 ±    3.609  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_offer_batch                         avgt    5     10.706 ±    0.580  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_offered                             avgt    5      9.224 ±    0.555  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_offered_batch                       avgt    5      9.761 ±    0.466  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_promise                             avgt    5      9.551 ±    0.298  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_promise_batch                       avgt    5     11.593 ±    1.369  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_promised                            avgt    5      8.730 ±    2.744  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_promised_batch                      avgt    5      8.788 ±    0.671  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_retract                             avgt    5      8.653 ±    3.951  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_retract_batch                       avgt    5      8.597 ±    3.361  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_retracted                           avgt    5      8.656 ±    3.485  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_retracted_batch                     avgt    5      9.221 ±    4.247  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_signal                              avgt    5     10.349 ±    0.280  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_signal_batch                        avgt    5     10.667 ±    0.676  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_validate                            avgt    5      8.659 ±    3.626  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_validate_batch                      avgt    5      8.473 ±    3.478  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_validated                           avgt    5      8.693 ±    3.639  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_validated_batch                     avgt    5      9.997 ±    3.078  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_abandon                             avgt    5      8.860 ±    5.386  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_abandon_batch                       avgt    5      7.759 ±    3.092  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_arrive                              avgt    5      9.760 ±    0.636  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_arrive_batch                        avgt    5     10.156 ±    0.684  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_await                               avgt    5      8.662 ±    5.262  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_await_batch                         avgt    5      8.276 ±    3.811  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_release                             avgt    5      8.744 ±    3.684  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_release_batch                       avgt    5      9.032 ±    1.080  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_reset                               avgt    5      8.842 ±    6.539  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_reset_batch                         avgt    5      8.306 ±    6.363  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_sign                                avgt    5      9.623 ±    0.394  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_sign_batch                          avgt    5      9.657 ±    0.756  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_timeout                             avgt    5      9.101 ±    6.099  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_timeout_batch                       avgt    5      8.163 ±    7.015  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.latch_from_conduit                       avgt    5      1.860 ±    0.024  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.latch_from_conduit_batch                 avgt    5      1.661 ±    0.012  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_abandon                              avgt    5      9.824 ±    0.285  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_abandon_batch                        avgt    5      8.357 ±    8.434  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_acquire                              avgt    5      8.512 ±    4.658  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_acquire_batch                        avgt    5      8.275 ±    4.728  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_attempt                              avgt    5      8.488 ±    5.957  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_attempt_batch                        avgt    5      8.352 ±    6.333  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_contest                              avgt    5      8.613 ±    5.485  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_contest_batch                        avgt    5     10.096 ±    0.959  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_deny                                 avgt    5     10.963 ±    8.347  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_deny_batch                           avgt    5      7.888 ±    3.283  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_downgrade                            avgt    5      8.645 ±    3.984  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_downgrade_batch                      avgt    5      7.703 ±    7.296  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_grant                                avgt    5      8.435 ±    4.407  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_grant_batch                          avgt    5     10.466 ±    0.551  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_release                              avgt    5      8.517 ±    3.658  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_release_batch                        avgt    5      8.543 ±    7.893  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_sign                                 avgt    5      8.320 ±    4.847  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_sign_batch                           avgt    5      9.741 ±    1.229  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_timeout                              avgt    5      8.967 ±    5.603  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_timeout_batch                        avgt    5      9.368 ±    0.358  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_upgrade                              avgt    5      8.787 ±    5.716  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_upgrade_batch                        avgt    5      8.780 ±    0.449  ns/op
i.h.serventis.jmh.opt.sync.LockOps.lock_from_conduit                         avgt    5      1.874 ±    0.049  ns/op
i.h.serventis.jmh.opt.sync.LockOps.lock_from_conduit_batch                   avgt    5      1.660 ±    0.003  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.counter_from_conduit                   avgt    5      1.865 ±    0.067  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.counter_from_conduit_batch             avgt    5      1.661 ±    0.012  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_increment                         avgt    5      8.896 ±    6.143  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_increment_batch                   avgt    5      9.637 ±    0.978  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_overflow                          avgt    5      8.467 ±    3.191  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_overflow_batch                    avgt    5      8.448 ±    0.592  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_reset                             avgt    5      9.268 ±    1.127  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_reset_batch                       avgt    5      8.156 ±    9.078  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_sign                              avgt    5      8.748 ±    3.757  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_sign_batch                        avgt    5     11.330 ±    1.089  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_decrement                           avgt    5      8.606 ±    2.305  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_decrement_batch                     avgt    5      8.483 ±    7.425  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_increment                           avgt    5      8.126 ±    7.371  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_increment_batch                     avgt    5      7.944 ±    4.704  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_overflow                            avgt    5      8.699 ±    6.406  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_overflow_batch                      avgt    5     10.469 ±    0.855  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_reset                               avgt    5      8.733 ±    3.675  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_reset_batch                         avgt    5      9.135 ±    1.039  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_sign                                avgt    5      8.494 ±    4.125  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_sign_batch                          avgt    5      9.156 ±    5.682  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_underflow                           avgt    5      7.956 ±    2.599  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_underflow_batch                     avgt    5      9.518 ±    0.520  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.gauge_from_conduit                       avgt    5      1.864 ±    0.063  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.gauge_from_conduit_batch                 avgt    5      1.660 ±    0.004  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_debug                                 avgt    5     10.541 ±    1.200  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_debug_batch                           avgt    5      9.341 ±    0.934  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_info                                  avgt    5      9.472 ±    9.260  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_info_batch                            avgt    5      8.267 ±    6.829  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_severe                                avgt    5      8.408 ±    2.994  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_severe_batch                          avgt    5      8.963 ±    0.420  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_sign                                  avgt    5     10.362 ±    0.645  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_sign_batch                            avgt    5      9.968 ±    0.529  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_warning                               avgt    5      8.975 ±    5.371  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_warning_batch                         avgt    5      8.392 ±    4.141  ns/op
i.h.serventis.jmh.opt.tool.LogOps.log_from_conduit                           avgt    5      1.866 ±    0.059  ns/op
i.h.serventis.jmh.opt.tool.LogOps.log_from_conduit_batch                     avgt    5      1.661 ±    0.011  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_connect                             avgt    5     10.326 ±    0.567  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_connect_batch                       avgt    5      8.069 ±    2.896  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_connected                           avgt    5      8.800 ±    4.004  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_connected_batch                     avgt    5      9.472 ±    0.744  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_disconnect                          avgt    5      9.596 ±    0.609  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_disconnect_batch                    avgt    5      8.645 ±    3.848  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_disconnected                        avgt    5      9.981 ±    4.810  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_disconnected_batch                  avgt    5     10.112 ±    0.906  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_fail                                avgt    5      9.713 ±    0.939  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_fail_batch                          avgt    5      9.777 ±    0.501  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_failed                              avgt    5      8.546 ±    4.947  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_failed_batch                        avgt    5      9.308 ±    4.110  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_process                             avgt    5      8.848 ±    3.771  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_process_batch                       avgt    5     10.539 ±    1.471  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_processed                           avgt    5      9.728 ±    0.957  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_processed_batch                     avgt    5      8.726 ±    3.673  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_receive_batch                       avgt    5      9.777 ±    3.182  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_received_batch                      avgt    5      8.822 ±    6.554  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_signal                              avgt    5      8.508 ±    2.484  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_signal_batch                        avgt    5      8.694 ±    4.155  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_succeed                             avgt    5      8.363 ±    4.103  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_succeed_batch                       avgt    5      9.278 ±    4.009  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_succeeded                           avgt    5      9.240 ±    5.293  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_succeeded_batch                     avgt    5     10.426 ±    0.801  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transfer                            avgt    5     10.965 ±    1.202  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transfer_inbound                    avgt    5     10.198 ±    0.443  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transfer_outbound                   avgt    5     10.771 ±    0.727  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transferred                         avgt    5      9.932 ±    1.030  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transmit_batch                      avgt    5     10.525 ±    0.961  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transmitted_batch                   avgt    5      9.527 ±    4.462  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.probe_from_conduit                       avgt    5      1.852 ±    0.016  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.probe_from_conduit_batch                 avgt    5      1.657 ±    0.004  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_baseline                     avgt    5     10.711 ±    1.107  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_baseline_batch               avgt    5      9.086 ±    7.421  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_target                       avgt    5      9.530 ±    0.782  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_target_batch                 avgt    5      9.778 ±    0.421  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_threshold                    avgt    5     10.213 ±    0.551  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_threshold_batch              avgt    5      8.781 ±    8.304  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_baseline                     avgt    5      9.879 ±    0.866  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_baseline_batch               avgt    5      8.401 ±    5.047  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_target                       avgt    5     10.839 ±    0.653  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_target_batch                 avgt    5      8.787 ±    3.878  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_threshold                    avgt    5      8.991 ±    3.122  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_threshold_batch              avgt    5     10.170 ±    0.553  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_baseline                   avgt    5      8.678 ±    3.534  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_baseline_batch             avgt    5     10.237 ±    2.552  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_target                     avgt    5      9.723 ±    0.548  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_target_batch               avgt    5     10.637 ±    0.774  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_threshold                  avgt    5      9.185 ±    3.246  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_threshold_batch            avgt    5      9.660 ±    4.223  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_signal                             avgt    5     10.544 ±    0.600  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_signal_batch                       avgt    5      9.475 ±    0.748  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.sensor_from_conduit                     avgt    5      1.871 ±    0.080  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.sensor_from_conduit_batch               avgt    5      1.683 ±    0.079  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_mixed_pattern                         avgt    5      0.225 ±    0.004  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_single                                avgt    5      0.753 ±    0.027  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_single_batch                          avgt    5      0.019 ±    0.001  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_varied_batch                          avgt    5      1.513 ±    0.001  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_worst_case                            avgt    5      1.184 ±    0.022  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_critical                             avgt    5      8.753 ±    3.691  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_critical_batch                       avgt    5     12.073 ±    0.352  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_normal                               avgt    5      8.731 ±    2.977  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_normal_batch                         avgt    5      9.452 ±    0.820  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_signal                               avgt    5      8.956 ±    4.978  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_signal_batch                         avgt    5      9.754 ±    4.155  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_warning                              avgt    5      9.092 ±    3.135  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_warning_batch                        avgt    5      8.633 ±    6.469  ns/op
i.h.serventis.jmh.sdk.SituationOps.situation_from_conduit                    avgt    5      1.867 ±    0.061  ns/op
i.h.serventis.jmh.sdk.SituationOps.situation_from_conduit_batch              avgt    5      1.658 ±    0.003  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_converging_confirmed                    avgt    5     10.283 ±    0.700  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_converging_confirmed_batch              avgt    5     10.775 ±    0.376  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_defective_tentative                     avgt    5      9.196 ±    5.345  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_defective_tentative_batch               avgt    5      8.872 ±    3.771  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_degraded_measured                       avgt    5      8.950 ±    3.367  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_degraded_measured_batch                 avgt    5      8.874 ±    2.693  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_down_confirmed                          avgt    5      9.232 ±    4.599  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_down_confirmed_batch                    avgt    5     10.058 ±    0.570  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_signal                                  avgt    5     10.909 ±    0.437  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_signal_batch                            avgt    5     10.123 ±    0.421  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_stable_confirmed                        avgt    5      9.096 ±    0.895  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_stable_confirmed_batch                  avgt    5     10.373 ±    5.027  ns/op
i.h.serventis.jmh.sdk.StatusOps.status_from_conduit                          avgt    5      1.852 ±    0.019  ns/op
i.h.serventis.jmh.sdk.StatusOps.status_from_conduit_batch                    avgt    5      1.657 ±    0.005  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_alarm_flow                              avgt    5      8.935 ±    3.584  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_alarm_flow_batch                        avgt    5      9.899 ±    3.469  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_fault_link                              avgt    5      8.976 ±    2.659  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_fault_link_batch                        avgt    5      9.497 ±    7.536  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_limit_time                              avgt    5      9.858 ±    1.169  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_limit_time_batch                        avgt    5      9.756 ±    3.183  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_normal_space                            avgt    5     10.028 ±    0.719  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_normal_space_batch                      avgt    5      9.203 ±    4.508  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_signal                                  avgt    5      9.830 ±    0.721  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_signal_batch                            avgt    5      9.525 ±    3.539  ns/op
i.h.serventis.jmh.sdk.SystemOps.system_from_conduit                          avgt    5      1.869 ±    0.097  ns/op
i.h.serventis.jmh.sdk.SystemOps.system_from_conduit_batch                    avgt    5      1.659 ±    0.004  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.cycle_from_conduit                       avgt    5      1.854 ±    0.069  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.cycle_from_conduit_batch                 avgt    5      1.658 ±    0.009  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_repeat                              avgt    5      9.580 ±    0.510  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_repeat_batch                        avgt    5     10.240 ±    0.547  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_return                              avgt    5     11.106 ±    0.413  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_return_batch                        avgt    5      9.569 ±    3.799  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_signal                              avgt    5     10.411 ±    0.804  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_signal_batch                        avgt    5      8.130 ±    4.015  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_single                              avgt    5      8.400 ±    3.696  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_single_batch                        avgt    5      8.170 ±    3.617  ns/op
i.h.substrates.jmh.CircuitOps.conduit_create_close                           avgt    5    289.857 ±  162.280  ns/op
i.h.substrates.jmh.CircuitOps.conduit_create_named                           avgt    5    257.017 ±  140.445  ns/op
i.h.substrates.jmh.CircuitOps.conduit_create_with_flow                       avgt    5    286.288 ±  147.345  ns/op
i.h.substrates.jmh.CircuitOps.create_and_close                               avgt    5    506.979 ± 1451.481  ns/op
i.h.substrates.jmh.CircuitOps.create_await_close                             avgt    5  10491.195 ±  990.721  ns/op
i.h.substrates.jmh.CircuitOps.hot_await_queue_drain                          avgt    5   7562.190 ±  576.759  ns/op
i.h.substrates.jmh.CircuitOps.hot_conduit_create                             avgt    5     18.952 ±    0.033  ns/op
i.h.substrates.jmh.CircuitOps.hot_conduit_create_named                       avgt    5     18.919 ±    0.020  ns/op
i.h.substrates.jmh.CircuitOps.hot_conduit_create_with_flow                   avgt    5     21.758 ±    0.035  ns/op
i.h.substrates.jmh.CircuitOps.hot_pipe_async                                 avgt    5      8.480 ±    1.660  ns/op
i.h.substrates.jmh.CircuitOps.hot_pipe_async_with_flow                       avgt    5     10.411 ±    1.408  ns/op
i.h.substrates.jmh.CircuitOps.pipe_async                                     avgt    5    367.387 ±  540.182  ns/op
i.h.substrates.jmh.CircuitOps.pipe_async_with_flow                           avgt    5    369.801 ±  802.541  ns/op
i.h.substrates.jmh.ConduitOps.get_by_name                                    avgt    5      1.883 ±    0.074  ns/op
i.h.substrates.jmh.ConduitOps.get_by_name_batch                              avgt    5      1.657 ±    0.009  ns/op
i.h.substrates.jmh.ConduitOps.get_by_substrate                               avgt    5      1.998 ±    0.047  ns/op
i.h.substrates.jmh.ConduitOps.get_by_substrate_batch                         avgt    5      1.810 ±    0.002  ns/op
i.h.substrates.jmh.ConduitOps.get_cached                                     avgt    5      3.433 ±    0.071  ns/op
i.h.substrates.jmh.ConduitOps.get_cached_batch                               avgt    5      3.301 ±    0.009  ns/op
i.h.substrates.jmh.ConduitOps.subscribe                                      avgt    5    480.144 ±  467.464  ns/op
i.h.substrates.jmh.ConduitOps.subscribe_batch                                avgt    5    479.679 ±  411.758  ns/op
i.h.substrates.jmh.ConduitOps.subscribe_with_emission_await                  avgt    5   5434.153 ±  182.289  ns/op
i.h.substrates.jmh.CortexOps.circuit                                         avgt    5    278.849 ±  114.328  ns/op
i.h.substrates.jmh.CortexOps.circuit_batch                                   avgt    5    277.572 ±  134.936  ns/op
i.h.substrates.jmh.CortexOps.circuit_named                                   avgt    5    274.462 ±  129.059  ns/op
i.h.substrates.jmh.CortexOps.current                                         avgt    5      1.088 ±    0.017  ns/op
i.h.substrates.jmh.CortexOps.name_class                                      avgt    5      1.503 ±    0.001  ns/op
i.h.substrates.jmh.CortexOps.name_enum                                       avgt    5      2.823 ±    0.021  ns/op
i.h.substrates.jmh.CortexOps.name_iterable                                   avgt    5     11.269 ±    0.177  ns/op
i.h.substrates.jmh.CortexOps.name_path                                       avgt    5      1.890 ±    0.008  ns/op
i.h.substrates.jmh.CortexOps.name_path_batch                                 avgt    5      1.683 ±    0.004  ns/op
i.h.substrates.jmh.CortexOps.name_string                                     avgt    5      2.787 ±    0.475  ns/op
i.h.substrates.jmh.CortexOps.name_string_batch                               avgt    5      2.585 ±    0.009  ns/op
i.h.substrates.jmh.CortexOps.scope                                           avgt    5      8.642 ±    1.965  ns/op
i.h.substrates.jmh.CortexOps.scope_batch                                     avgt    5      7.604 ±    0.461  ns/op
i.h.substrates.jmh.CortexOps.scope_named                                     avgt    5      7.994 ±    0.034  ns/op
i.h.substrates.jmh.CortexOps.slot_boolean                                    avgt    5      2.439 ±    0.039  ns/op
i.h.substrates.jmh.CortexOps.slot_double                                     avgt    5      2.392 ±    0.097  ns/op
i.h.substrates.jmh.CortexOps.slot_int                                        avgt    5      2.307 ±    0.898  ns/op
i.h.substrates.jmh.CortexOps.slot_long                                       avgt    5      2.306 ±    0.915  ns/op
i.h.substrates.jmh.CortexOps.slot_string                                     avgt    5      2.402 ±    0.184  ns/op
i.h.substrates.jmh.CortexOps.state_empty                                     avgt    5      0.437 ±    0.002  ns/op
i.h.substrates.jmh.CortexOps.state_empty_batch                               avgt    5      0.001 ±    0.001  ns/op
i.h.substrates.jmh.FlowOps.baseline_no_flow_await                            avgt    5     17.634 ±    0.493  ns/op
i.h.substrates.jmh.FlowOps.flow_combined_diff_guard_await                    avgt    5     30.100 ±    1.010  ns/op
i.h.substrates.jmh.FlowOps.flow_combined_diff_sample_await                   avgt    5     21.784 ±    0.208  ns/op
i.h.substrates.jmh.FlowOps.flow_combined_guard_limit_await                   avgt    5     29.313 ±    1.727  ns/op
i.h.substrates.jmh.FlowOps.flow_diff_await                                   avgt    5     29.503 ±    1.228  ns/op
i.h.substrates.jmh.FlowOps.flow_guard_await                                  avgt    5     28.289 ±    0.911  ns/op
i.h.substrates.jmh.FlowOps.flow_limit_await                                  avgt    5     29.115 ±    2.302  ns/op
i.h.substrates.jmh.FlowOps.flow_sample_await                                 avgt    5     17.654 ±    0.429  ns/op
i.h.substrates.jmh.FlowOps.flow_sift_await                                   avgt    5     18.681 ±    0.072  ns/op
i.h.substrates.jmh.NameOps.name_chained_deep                                 avgt    5     16.910 ±    0.046  ns/op
i.h.substrates.jmh.NameOps.name_chaining                                     avgt    5      8.999 ±    0.345  ns/op
i.h.substrates.jmh.NameOps.name_chaining_batch                               avgt    5      9.039 ±    0.004  ns/op
i.h.substrates.jmh.NameOps.name_compare                                      avgt    5     31.959 ±    0.339  ns/op
i.h.substrates.jmh.NameOps.name_compare_batch                                avgt    5     31.850 ±    1.291  ns/op
i.h.substrates.jmh.NameOps.name_depth                                        avgt    5      1.604 ±    0.001  ns/op
i.h.substrates.jmh.NameOps.name_depth_batch                                  avgt    5      1.399 ±    0.050  ns/op
i.h.substrates.jmh.NameOps.name_enclosure                                    avgt    5      0.585 ±    0.003  ns/op
i.h.substrates.jmh.NameOps.name_from_enum                                    avgt    5      2.835 ±    0.024  ns/op
i.h.substrates.jmh.NameOps.name_from_iterable                                avgt    5     11.723 ±    0.340  ns/op
i.h.substrates.jmh.NameOps.name_from_iterator                                avgt    5     12.971 ±    0.029  ns/op
i.h.substrates.jmh.NameOps.name_from_mapped_iterable                         avgt    5     11.675 ±    0.032  ns/op
i.h.substrates.jmh.NameOps.name_from_name                                    avgt    5      4.211 ±    0.007  ns/op
i.h.substrates.jmh.NameOps.name_from_string                                  avgt    5      3.032 ±    0.010  ns/op
i.h.substrates.jmh.NameOps.name_from_string_batch                            avgt    5      2.776 ±    0.015  ns/op
i.h.substrates.jmh.NameOps.name_interning_chained                            avgt    5     12.221 ±    0.093  ns/op
i.h.substrates.jmh.NameOps.name_interning_same_path                          avgt    5      3.551 ±    0.026  ns/op
i.h.substrates.jmh.NameOps.name_interning_segments                           avgt    5      9.075 ±    0.651  ns/op
i.h.substrates.jmh.NameOps.name_iterate_hierarchy                            avgt    5      1.714 ±    0.130  ns/op
i.h.substrates.jmh.NameOps.name_parsing                                      avgt    5      1.885 ±    0.002  ns/op
i.h.substrates.jmh.NameOps.name_parsing_batch                                avgt    5      1.680 ±    0.002  ns/op
i.h.substrates.jmh.NameOps.name_path_generation                              avgt    5     32.637 ±    2.025  ns/op
i.h.substrates.jmh.NameOps.name_path_generation_batch                        avgt    5     30.383 ±    6.281  ns/op
i.h.substrates.jmh.PipeOps.async_emit_batch                                  avgt    5     11.474 ±    7.312  ns/op
i.h.substrates.jmh.PipeOps.async_emit_batch_await                            avgt    5     18.084 ±    0.548  ns/op
i.h.substrates.jmh.PipeOps.async_emit_chained_await                          avgt    5     16.647 ±    0.893  ns/op
i.h.substrates.jmh.PipeOps.async_emit_fanout_await                           avgt    5     18.550 ±    0.943  ns/op
i.h.substrates.jmh.PipeOps.async_emit_single                                 avgt    5      9.724 ±    5.751  ns/op
i.h.substrates.jmh.PipeOps.async_emit_single_await                           avgt    5   7909.424 ±  317.248  ns/op
i.h.substrates.jmh.PipeOps.async_emit_with_flow_await                        avgt    5     17.588 ±    0.248  ns/op
i.h.substrates.jmh.PipeOps.baseline_blackhole                                avgt    5      0.280 ±    0.132  ns/op
i.h.substrates.jmh.PipeOps.baseline_counter                                  avgt    5      1.601 ±    0.013  ns/op
i.h.substrates.jmh.PipeOps.baseline_receptor                                 avgt    5      0.262 ±    0.002  ns/op
i.h.substrates.jmh.PipeOps.pipe_create                                       avgt    5      8.433 ±    1.306  ns/op
i.h.substrates.jmh.PipeOps.pipe_create_chained                               avgt    5      0.849 ±    0.039  ns/op
i.h.substrates.jmh.PipeOps.pipe_create_with_flow                             avgt    5     12.706 ±    1.370  ns/op
i.h.substrates.jmh.ReservoirOps.baseline_emit_no_reservoir_await             avgt    5     90.042 ±   15.820  ns/op
i.h.substrates.jmh.ReservoirOps.baseline_emit_no_reservoir_await_batch       avgt    5     18.019 ±    0.439  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_burst_then_drain_await             avgt    5     89.529 ±   10.271  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_burst_then_drain_await_batch       avgt    5     28.638 ±    0.783  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_drain_await                        avgt    5     92.676 ±    1.416  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_drain_await_batch                  avgt    5     28.773 ±    2.366  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_emit_drain_cycles_await            avgt    5    329.113 ±   23.212  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_emit_with_capture_await            avgt    5     78.659 ±    0.703  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_emit_with_capture_await_batch      avgt    5     23.611 ±    0.301  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_process_emissions_await            avgt    5     93.037 ±    4.171  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_process_emissions_await_batch      avgt    5     26.863 ±    0.314  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_process_subjects_await             avgt    5     92.222 ±   21.456  ns/op
i.h.substrates.jmh.ScopeOps.scope_child_anonymous                            avgt    5     17.175 ±    0.373  ns/op
i.h.substrates.jmh.ScopeOps.scope_child_anonymous_batch                      avgt    5     16.528 ±    0.205  ns/op
i.h.substrates.jmh.ScopeOps.scope_child_named                                avgt    5     17.009 ±    0.188  ns/op
i.h.substrates.jmh.ScopeOps.scope_child_named_batch                          avgt    5     16.955 ±    0.050  ns/op
i.h.substrates.jmh.ScopeOps.scope_close_idempotent                           avgt    5      2.368 ±    0.030  ns/op
i.h.substrates.jmh.ScopeOps.scope_close_idempotent_batch                     avgt    5      0.033 ±    0.001  ns/op
i.h.substrates.jmh.ScopeOps.scope_closure                                    avgt    5    285.932 ±   76.891  ns/op
i.h.substrates.jmh.ScopeOps.scope_closure_batch                              avgt    5    285.651 ±   73.903  ns/op
i.h.substrates.jmh.ScopeOps.scope_complex                                    avgt    5    903.082 ±   92.936  ns/op
i.h.substrates.jmh.ScopeOps.scope_create_and_close                           avgt    5      2.413 ±    0.022  ns/op
i.h.substrates.jmh.ScopeOps.scope_create_and_close_batch                     avgt    5      0.033 ±    0.001  ns/op
i.h.substrates.jmh.ScopeOps.scope_create_named                               avgt    5      2.410 ±    0.034  ns/op
i.h.substrates.jmh.ScopeOps.scope_create_named_batch                         avgt    5      0.033 ±    0.001  ns/op
i.h.substrates.jmh.ScopeOps.scope_hierarchy                                  avgt    5     27.066 ±    0.110  ns/op
i.h.substrates.jmh.ScopeOps.scope_hierarchy_batch                            avgt    5     26.483 ±    0.073  ns/op
i.h.substrates.jmh.ScopeOps.scope_parent_closes_children                     avgt    5     41.964 ±    0.250  ns/op
i.h.substrates.jmh.ScopeOps.scope_parent_closes_children_batch               avgt    5     42.459 ±    3.356  ns/op
i.h.substrates.jmh.ScopeOps.scope_register_multiple                          avgt    5   1516.054 ±  255.879  ns/op
i.h.substrates.jmh.ScopeOps.scope_register_multiple_batch                    avgt    5   1276.985 ±  739.496  ns/op
i.h.substrates.jmh.ScopeOps.scope_register_single                            avgt    5    294.792 ±   43.125  ns/op
i.h.substrates.jmh.ScopeOps.scope_register_single_batch                      avgt    5    287.072 ±   93.278  ns/op
i.h.substrates.jmh.ScopeOps.scope_with_resources                             avgt    5    571.170 ±  127.886  ns/op
i.h.substrates.jmh.StateOps.slot_name                                        avgt    5      0.530 ±    0.055  ns/op
i.h.substrates.jmh.StateOps.slot_name_batch                                  avgt    5      0.001 ±    0.001  ns/op
i.h.substrates.jmh.StateOps.slot_type                                        avgt    5      0.443 ±    0.002  ns/op
i.h.substrates.jmh.StateOps.slot_value                                       avgt    5      0.639 ±    0.001  ns/op
i.h.substrates.jmh.StateOps.slot_value_batch                                 avgt    5      0.001 ±    0.001  ns/op
i.h.substrates.jmh.StateOps.state_compact                                    avgt    5     10.383 ±    0.199  ns/op
i.h.substrates.jmh.StateOps.state_compact_batch                              avgt    5     10.793 ±    0.196  ns/op
i.h.substrates.jmh.StateOps.state_iterate_slots                              avgt    5      2.162 ±    0.075  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_int                               avgt    5      4.545 ±    1.201  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_int_batch                         avgt    5      4.737 ±    1.148  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_long                              avgt    5      4.650 ±    0.507  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_object                            avgt    5      2.512 ±    0.289  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_object_batch                      avgt    5      2.400 ±    0.285  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_string                            avgt    5      4.535 ±    1.396  ns/op
i.h.substrates.jmh.StateOps.state_value_read                                 avgt    5      1.486 ±    0.001  ns/op
i.h.substrates.jmh.StateOps.state_value_read_batch                           avgt    5      1.267 ±    0.001  ns/op
i.h.substrates.jmh.StateOps.state_values_stream                              avgt    5      4.866 ±    0.187  ns/op
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