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

java version "25.0.2" 2026-01-20 LTS
Java(TM) SE Runtime Environment (build 25.0.2+10-LTS-69)
Java HotSpot(TM) 64-Bit Server VM (build 25.0.2+10-LTS-69, mixed mode, sharing)

Humainary's (Alpha) SPI: io.humainary.substrates.spi.alpha.Provider 

Benchmark                                                                    Mode  Cnt      Score      Error  Units
i.h.serventis.jmh.opt.data.CacheOps.cache_from_conduit                       avgt    5      1.341 ±    0.217  ns/op
i.h.serventis.jmh.opt.data.CacheOps.cache_from_conduit_batch                 avgt    5      1.144 ±    0.192  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_evict                               avgt    5      7.923 ±    3.720  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_evict_batch                         avgt    5      6.957 ±   11.451  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_expire                              avgt    5      7.154 ±    3.566  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_expire_batch                        avgt    5      7.701 ±    1.405  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_hit                                 avgt    5      6.762 ±    9.369  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_hit_batch                           avgt    5      6.794 ±    3.730  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_lookup                              avgt    5      7.553 ±    4.111  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_lookup_batch                        avgt    5      6.611 ±    4.793  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_miss                                avgt    5      6.189 ±    8.205  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_miss_batch                          avgt    5      8.471 ±    2.528  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_remove                              avgt    5      6.777 ±    9.032  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_remove_batch                        avgt    5      6.725 ±    2.759  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_sign                                avgt    5      6.334 ±    9.110  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_sign_batch                          avgt    5      8.562 ±    1.413  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_store                               avgt    5      7.832 ±    0.350  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_store_batch                         avgt    5      9.446 ±    4.217  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_aggregate                        avgt    5      7.583 ±    3.625  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_aggregate_batch                  avgt    5      6.822 ±    3.162  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_backpressure                     avgt    5      9.100 ±    2.542  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_backpressure_batch               avgt    5      6.863 ±    3.365  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_buffer                           avgt    5      8.101 ±    7.039  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_buffer_batch                     avgt    5      6.005 ±    8.423  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_checkpoint                       avgt    5      8.553 ±    6.185  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_checkpoint_batch                 avgt    5      6.899 ±    3.205  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_filter                           avgt    5      7.117 ±    2.872  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_filter_batch                     avgt    5      6.808 ±    4.113  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_input                            avgt    5      7.111 ±    2.767  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_input_batch                      avgt    5      7.556 ±    1.451  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_lag                              avgt    5      7.133 ±    2.607  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_lag_batch                        avgt    5      6.688 ±    9.606  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_output                           avgt    5      6.491 ±    6.375  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_output_batch                     avgt    5      6.885 ±    9.322  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_overflow                         avgt    5      7.004 ±    2.007  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_overflow_batch                   avgt    5      6.461 ±   10.879  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_sign                             avgt    5      7.773 ±    5.958  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_sign_batch                       avgt    5      6.942 ±    6.609  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_skip                             avgt    5      9.263 ±    2.440  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_skip_batch                       avgt    5      7.128 ±    2.852  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_transform                        avgt    5      7.792 ±    4.828  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_transform_batch                  avgt    5      7.024 ±   10.000  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_watermark                        avgt    5      7.422 ±    2.324  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_watermark_batch                  avgt    5      7.343 ±    3.716  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_flow_etl                     avgt    5     40.729 ±   19.761  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_flow_stream                  avgt    5     39.268 ±   17.469  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_flow_windowed                avgt    5     41.333 ±   21.741  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_from_conduit                 avgt    5      1.292 ±    0.158  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_from_conduit_batch           avgt    5      1.095 ±    0.134  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_dequeue                             avgt    5      6.937 ±    3.484  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_dequeue_batch                       avgt    5      7.205 ±    2.879  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_enqueue                             avgt    5      7.320 ±    4.241  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_enqueue_batch                       avgt    5      6.986 ±    3.399  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_overflow                            avgt    5      7.178 ±    3.970  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_overflow_batch                      avgt    5      6.223 ±    6.024  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_sign                                avgt    5      7.125 ±    4.336  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_sign_batch                          avgt    5      6.313 ±    6.092  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_underflow                           avgt    5      7.328 ±    5.616  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_underflow_batch                     avgt    5      6.954 ±    2.828  ns/op
i.h.serventis.jmh.opt.data.QueueOps.queue_from_conduit                       avgt    5      1.300 ±    0.089  ns/op
i.h.serventis.jmh.opt.data.QueueOps.queue_from_conduit_batch                 avgt    5      1.093 ±    0.046  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_overflow                            avgt    5      9.020 ±    1.928  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_overflow_batch                      avgt    5      6.440 ±    8.264  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_pop                                 avgt    5      7.361 ±    4.659  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_pop_batch                           avgt    5      9.023 ±    1.821  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_push                                avgt    5      7.472 ±    3.824  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_push_batch                          avgt    5      8.832 ±    0.791  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_sign                                avgt    5      7.824 ±    4.145  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_sign_batch                          avgt    5      7.546 ±    8.776  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_underflow                           avgt    5      7.449 ±    3.701  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_underflow_batch                     avgt    5      6.356 ±    6.553  ns/op
i.h.serventis.jmh.opt.data.StackOps.stack_from_conduit                       avgt    5      1.280 ±    0.145  ns/op
i.h.serventis.jmh.opt.data.StackOps.stack_from_conduit_batch                 avgt    5      1.093 ±    0.128  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_crash                             avgt    5      7.137 ±    4.244  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_crash_batch                       avgt    5      6.504 ±    7.581  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_fail                              avgt    5      7.272 ±    1.665  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_fail_batch                        avgt    5      7.948 ±    0.907  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_kill                              avgt    5      7.807 ±    5.638  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_kill_batch                        avgt    5      7.112 ±    8.684  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_restart                           avgt    5      9.096 ±    1.529  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_restart_batch                     avgt    5      7.081 ±    6.886  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_resume                            avgt    5      7.552 ±    2.723  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_resume_batch                      avgt    5      6.689 ±    5.313  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_sign                              avgt    5      7.455 ±    4.806  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_sign_batch                        avgt    5      6.993 ±    5.307  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_spawn                             avgt    5      7.253 ±    6.827  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_spawn_batch                       avgt    5      7.052 ±    6.501  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_start                             avgt    5      8.919 ±    5.126  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_start_batch                       avgt    5      7.133 ±    3.238  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_stop                              avgt    5      7.535 ±    2.828  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_stop_batch                        avgt    5      7.129 ±    2.398  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_suspend                           avgt    5      7.566 ±    3.028  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_suspend_batch                     avgt    5      7.374 ±    1.005  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.process_from_conduit                   avgt    5      1.293 ±    0.129  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.process_from_conduit_batch             avgt    5      1.095 ±    0.093  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_call                              avgt    5      7.841 ±    4.383  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_call_batch                        avgt    5      7.217 ±    2.786  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_called                            avgt    5      7.704 ±    1.154  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_called_batch                      avgt    5      8.972 ±    4.412  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_delay                             avgt    5      7.562 ±    2.272  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_delay_batch                       avgt    5      9.170 ±    1.791  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_delayed                           avgt    5      7.817 ±    4.186  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_delayed_batch                     avgt    5     10.730 ±    1.091  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_discard                           avgt    5      7.419 ±    2.978  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_discard_batch                     avgt    5      7.370 ±    3.067  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_discarded                         avgt    5      7.113 ±    2.059  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_discarded_batch                   avgt    5      7.342 ±    3.937  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_disconnect                        avgt    5      7.988 ±    3.893  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_disconnect_batch                  avgt    5      8.904 ±    3.162  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_disconnected                      avgt    5      7.335 ±    2.268  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_disconnected_batch                avgt    5      7.604 ±    4.847  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_expire                            avgt    5      8.075 ±    0.483  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_expire_batch                      avgt    5      6.896 ±    2.415  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_expired                           avgt    5      6.657 ±    2.421  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_expired_batch                     avgt    5      7.392 ±    3.100  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_fail                              avgt    5      6.949 ±    3.837  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_fail_batch                        avgt    5      8.319 ±    3.898  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_failed                            avgt    5      7.400 ±    4.540  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_failed_batch                      avgt    5      7.574 ±    3.328  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_recourse                          avgt    5      7.350 ±    2.890  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_recourse_batch                    avgt    5      8.231 ±    3.506  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_recoursed                         avgt    5      8.633 ±    2.258  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_recoursed_batch                   avgt    5      6.999 ±    3.894  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_redirect                          avgt    5      6.756 ±    6.037  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_redirect_batch                    avgt    5      7.250 ±    3.583  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_redirected                        avgt    5      7.190 ±    6.172  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_redirected_batch                  avgt    5      8.898 ±    3.037  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_reject                            avgt    5      6.628 ±    7.970  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_reject_batch                      avgt    5      6.916 ±    9.771  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_rejected                          avgt    5      6.348 ±    4.703  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_rejected_batch                    avgt    5      6.822 ±    3.009  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_resume                            avgt    5      6.404 ±    4.388  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_resume_batch                      avgt    5      9.871 ±    2.623  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_resumed                           avgt    5      7.534 ±    1.820  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_resumed_batch                     avgt    5      9.051 ±    2.532  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_retried                           avgt    5      7.132 ±    3.738  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_retried_batch                     avgt    5      6.512 ±    1.335  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_retry                             avgt    5      7.301 ±    2.842  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_retry_batch                       avgt    5      8.519 ±    4.752  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_schedule                          avgt    5      6.311 ±    7.400  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_schedule_batch                    avgt    5      7.805 ±    2.396  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_scheduled                         avgt    5      6.933 ±    2.953  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_scheduled_batch                   avgt    5      5.681 ±    7.297  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_signal                            avgt    5      7.016 ±    2.730  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_signal_batch                      avgt    5      6.581 ±    2.845  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_start                             avgt    5      7.053 ±    3.296  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_start_batch                       avgt    5      6.596 ±    1.007  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_started                           avgt    5      7.988 ±    0.659  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_started_batch                     avgt    5      6.738 ±    3.254  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_stop                              avgt    5      7.368 ±    2.880  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_stop_batch                        avgt    5      6.360 ±    8.119  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_stopped                           avgt    5      6.725 ±    6.938  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_stopped_batch                     avgt    5      8.320 ±    2.634  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_succeeded                         avgt    5      7.353 ±    3.145  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_succeeded_batch                   avgt    5      6.663 ±    1.678  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_success                           avgt    5      7.004 ±    2.507  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_success_batch                     avgt    5      7.251 ±    3.388  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_suspend                           avgt    5      8.582 ±    0.905  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_suspend_batch                     avgt    5      8.499 ±    2.762  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_suspended                         avgt    5      6.892 ±    5.200  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_suspended_batch                   avgt    5      9.369 ±    5.057  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.service_from_conduit                   avgt    5      1.348 ±    0.321  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.service_from_conduit_batch             avgt    5      1.120 ±    0.225  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_cancel                               avgt    5      6.283 ±    8.647  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_cancel_batch                         avgt    5      7.175 ±    3.680  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_complete                             avgt    5      9.060 ±    1.938  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_complete_batch                       avgt    5      6.762 ±    8.898  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_fail                                 avgt    5      6.928 ±   10.278  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_fail_batch                           avgt    5      7.572 ±    6.235  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_progress                             avgt    5      7.011 ±    7.893  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_progress_batch                       avgt    5      6.900 ±   10.688  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_reject                               avgt    5      6.655 ±    9.363  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_reject_batch                         avgt    5      5.944 ±    7.010  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_resume                               avgt    5      7.282 ±    1.600  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_resume_batch                         avgt    5      7.151 ±    4.339  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_schedule                             avgt    5      7.017 ±    7.711  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_schedule_batch                       avgt    5      9.298 ±    2.317  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_sign                                 avgt    5      8.299 ±    2.637  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_sign_batch                           avgt    5      6.653 ±    9.282  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_start                                avgt    5      6.522 ±    7.061  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_start_batch                          avgt    5      7.044 ±    2.658  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_submit                               avgt    5      9.243 ±    2.629  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_submit_batch                         avgt    5      6.813 ±    2.024  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_suspend                              avgt    5      7.523 ±    3.272  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_suspend_batch                        avgt    5      6.652 ±    2.801  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_timeout                              avgt    5      7.603 ±    7.154  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_timeout_batch                        avgt    5      9.162 ±    3.352  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.task_from_conduit                         avgt    5      1.334 ±    0.174  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.task_from_conduit_batch                   avgt    5      1.121 ±    0.147  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_meet_deadline                       avgt    5      8.198 ±    0.782  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_meet_deadline_batch                 avgt    5      7.467 ±    0.874  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_meet_threshold                      avgt    5      7.459 ±    2.928  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_meet_threshold_batch                avgt    5      8.325 ±    2.769  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_miss_deadline                       avgt    5      7.371 ±    2.292  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_miss_deadline_batch                 avgt    5      6.972 ±    3.772  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_miss_threshold                      avgt    5      6.845 ±    2.336  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_miss_threshold_batch                avgt    5      8.118 ±    5.826  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_signal                              avgt    5      6.561 ±    8.602  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_signal_batch                        avgt    5      9.223 ±    0.908  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.timer_from_conduit                       avgt    5      1.323 ±    0.237  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.timer_from_conduit_batch                 avgt    5      1.129 ±    0.174  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_abort_coordinator             avgt    5      6.434 ±    9.075  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_abort_coordinator_batch       avgt    5      6.755 ±    2.676  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_abort_participant             avgt    5      6.868 ±    5.332  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_abort_participant_batch       avgt    5      8.690 ±    3.174  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_commit_coordinator            avgt    5      7.521 ±    3.186  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_commit_coordinator_batch      avgt    5      9.730 ±    2.810  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_commit_participant            avgt    5      7.597 ±    2.771  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_commit_participant_batch      avgt    5      7.101 ±    3.290  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_compensate_coordinator        avgt    5      7.269 ±    3.001  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_compensate_coordinator_batch  avgt    5      8.186 ±    0.747  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_compensate_participant        avgt    5      7.230 ±    3.240  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_compensate_participant_batch  avgt    5      8.543 ±    3.524  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_conflict_coordinator          avgt    5      7.369 ±    3.265  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_conflict_coordinator_batch    avgt    5      5.885 ±    5.791  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_conflict_participant          avgt    5      7.249 ±    2.352  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_conflict_participant_batch    avgt    5      7.271 ±    1.916  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_expire_coordinator            avgt    5      6.858 ±    2.062  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_expire_coordinator_batch      avgt    5      6.313 ±    9.964  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_expire_participant            avgt    5      7.364 ±    2.930  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_expire_participant_batch      avgt    5      8.079 ±    2.959  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_prepare_coordinator           avgt    5      8.190 ±    1.212  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_prepare_coordinator_batch     avgt    5      8.443 ±    1.797  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_prepare_participant           avgt    5      7.051 ±    7.577  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_prepare_participant_batch     avgt    5      6.605 ±    6.828  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_rollback_coordinator          avgt    5      7.354 ±    2.977  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_rollback_coordinator_batch    avgt    5      8.112 ±    3.221  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_rollback_participant          avgt    5      7.300 ±    3.127  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_rollback_participant_batch    avgt    5      8.568 ±    4.565  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_signal                        avgt    5      7.277 ±    2.600  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_signal_batch                  avgt    5      8.868 ±    3.493  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_start_coordinator             avgt    5      7.130 ±    2.907  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_start_coordinator_batch       avgt    5      6.831 ±    3.040  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_start_participant             avgt    5      7.219 ±    7.563  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_start_participant_batch       avgt    5      9.344 ±    0.472  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.transaction_from_conduit           avgt    5      1.363 ±    0.197  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.transaction_from_conduit_batch     avgt    5      1.126 ±    0.220  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.breaker_from_conduit                   avgt    5      1.346 ±    0.182  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.breaker_from_conduit_batch             avgt    5      1.119 ±    0.169  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_close                             avgt    5      7.423 ±    3.299  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_close_batch                       avgt    5      7.134 ±    2.842  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_half_open                         avgt    5      7.306 ±    3.859  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_half_open_batch                   avgt    5      7.815 ±    1.122  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_open                              avgt    5      7.640 ±    4.021  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_open_batch                        avgt    5      7.673 ±    6.325  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_probe                             avgt    5      7.501 ±    7.798  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_probe_batch                       avgt    5      7.229 ±    9.025  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_reset                             avgt    5      6.102 ±    6.175  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_reset_batch                       avgt    5      6.743 ±    3.011  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_sign                              avgt    5      7.212 ±    3.351  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_sign_batch                        avgt    5      6.771 ±    4.026  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_trip                              avgt    5      7.075 ±    8.275  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_trip_batch                        avgt    5      6.770 ±    2.458  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_fail_egress                          avgt    5      7.239 ±    2.811  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_fail_egress_batch                    avgt    5      7.239 ±    4.479  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_fail_ingress                         avgt    5      7.292 ±    3.486  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_fail_ingress_batch                   avgt    5      7.257 ±    3.305  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_fail_transit                         avgt    5      7.470 ±    3.378  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_fail_transit_batch                   avgt    5      6.158 ±    6.149  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_signal                               avgt    5      8.150 ±    2.102  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_signal_batch                         avgt    5      6.091 ±    5.643  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_success_egress                       avgt    5      6.961 ±    3.516  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_success_egress_batch                 avgt    5      6.805 ±    2.872  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_success_ingress                      avgt    5      6.963 ±    6.313  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_success_ingress_batch                avgt    5      7.144 ±    3.736  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_success_transit                      avgt    5      6.783 ±    7.567  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_success_transit_batch                avgt    5      8.480 ±    3.846  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.flow_from_conduit                         avgt    5      1.336 ±    0.169  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.flow_from_conduit_batch                   avgt    5      1.144 ±    0.217  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_corrupt                            avgt    5      7.459 ±    3.799  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_corrupt_batch                      avgt    5      7.143 ±    6.386  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_drop                               avgt    5      8.094 ±    1.019  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_drop_batch                         avgt    5      7.180 ±    3.857  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_forward                            avgt    5      7.539 ±    3.768  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_forward_batch                      avgt    5      7.141 ±    9.945  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_fragment                           avgt    5      7.575 ±    3.269  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_fragment_batch                     avgt    5      8.316 ±    2.700  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_reassemble                         avgt    5      6.672 ±    9.812  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_reassemble_batch                   avgt    5      6.669 ±    1.518  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_receive                            avgt    5      7.289 ±    6.742  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_receive_batch                      avgt    5      8.987 ±    3.666  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_reorder                            avgt    5      6.891 ±    9.254  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_reorder_batch                      avgt    5      6.672 ±    3.211  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_route                              avgt    5      7.184 ±    8.425  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_route_batch                        avgt    5      8.638 ±    2.383  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_send                               avgt    5      7.636 ±    4.269  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_send_batch                         avgt    5      6.727 ±    8.926  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_sign                               avgt    5      7.639 ±    3.659  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_sign_batch                         avgt    5      6.571 ±    8.139  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.router_from_conduit                     avgt    5      1.350 ±    0.274  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.router_from_conduit_batch               avgt    5      1.130 ±    0.262  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_contract                            avgt    5      7.251 ±    2.964  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_contract_batch                      avgt    5      8.852 ±    5.542  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_deny                                avgt    5      7.412 ±    3.204  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_deny_batch                          avgt    5      6.659 ±    9.399  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_drain                               avgt    5      7.093 ±    9.811  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_drain_batch                         avgt    5      8.678 ±    3.805  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_drop                                avgt    5      6.823 ±    7.564  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_drop_batch                          avgt    5      6.107 ±    5.477  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_expand                              avgt    5      8.529 ±    1.835  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_expand_batch                        avgt    5      7.167 ±    3.276  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_pass                                avgt    5      7.478 ±    2.910  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_pass_batch                          avgt    5      6.942 ±    3.480  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_sign                                avgt    5      7.171 ±    3.541  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_sign_batch                          avgt    5      6.773 ±    3.407  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.valve_from_conduit                       avgt    5      1.347 ±    0.254  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.valve_from_conduit_batch                 avgt    5      1.122 ±    0.169  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_contract_provider                avgt    5      6.724 ±    7.822  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_contract_provider_batch          avgt    5      7.027 ±    2.974  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_contract_receiver                avgt    5      8.050 ±    1.679  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_contract_receiver_batch          avgt    5      7.419 ±    3.669  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_full_exchange                    avgt    5      8.956 ±    3.409  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_full_exchange_batch              avgt    5      8.299 ±    5.276  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_signal                           avgt    5      7.923 ±   11.904  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_signal_batch                     avgt    5      8.626 ±    3.717  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_transfer_provider                avgt    5      6.947 ±    8.585  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_transfer_provider_batch          avgt    5      6.959 ±    3.508  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_transfer_receiver                avgt    5      7.213 ±    1.815  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_transfer_receiver_batch          avgt    5      6.353 ±    8.664  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.exchange_from_conduit                 avgt    5      1.328 ±    0.261  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.exchange_from_conduit_batch           avgt    5      1.145 ±    0.190  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_acquire                             avgt    5      6.827 ±    7.173  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_acquire_batch                       avgt    5      6.305 ±    7.663  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_acquired                            avgt    5      7.372 ±    8.389  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_acquired_batch                      avgt    5      6.205 ±    9.303  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_denied                              avgt    5      7.458 ±    2.985  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_denied_batch                        avgt    5      5.716 ±    8.589  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_deny                                avgt    5      7.217 ±    2.591  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_deny_batch                          avgt    5      8.351 ±    4.822  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_expire                              avgt    5      7.891 ±    2.619  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_expire_batch                        avgt    5      8.336 ±    3.293  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_expired                             avgt    5      7.189 ±    1.765  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_expired_batch                       avgt    5      8.505 ±    1.011  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_extend                              avgt    5      7.365 ±    2.609  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_extend_batch                        avgt    5      7.281 ±    2.661  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_extended                            avgt    5      7.580 ±    2.938  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_extended_batch                      avgt    5      7.752 ±    1.431  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_grant                               avgt    5      7.590 ±    3.213  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_grant_batch                         avgt    5      8.784 ±    3.638  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_granted                             avgt    5      8.861 ±    4.675  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_granted_batch                       avgt    5      6.855 ±    2.850  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_probe                               avgt    5      7.369 ±    2.939  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_probe_batch                         avgt    5      7.115 ±    3.534  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_probed                              avgt    5      7.321 ±    3.646  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_probed_batch                        avgt    5      7.313 ±    3.032  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_release                             avgt    5      6.955 ±    2.768  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_release_batch                       avgt    5      7.532 ±    3.310  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_released                            avgt    5      7.822 ±    2.728  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_released_batch                      avgt    5      8.332 ±    2.101  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_renew                               avgt    5      7.543 ±    3.037  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_renew_batch                         avgt    5      9.110 ±    1.172  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_renewed                             avgt    5      8.100 ±    0.374  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_renewed_batch                       avgt    5      7.201 ±    3.138  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_revoke                              avgt    5      7.342 ±    3.607  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_revoke_batch                        avgt    5      7.488 ±    3.861  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_revoked                             avgt    5      7.216 ±    2.721  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_revoked_batch                       avgt    5      8.389 ±    3.280  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_signal                              avgt    5      7.919 ±    0.171  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_signal_batch                        avgt    5      8.404 ±    2.599  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.lease_from_conduit                       avgt    5      1.316 ±    0.273  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.lease_from_conduit_batch                 avgt    5      1.130 ±    0.266  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_borrow                               avgt    5      6.654 ±    9.188  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_borrow_batch                         avgt    5      6.922 ±    3.960  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_contract                             avgt    5      7.326 ±    2.053  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_contract_batch                       avgt    5      6.535 ±    7.842  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_expand                               avgt    5      6.558 ±    9.079  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_expand_batch                         avgt    5      6.827 ±    1.682  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_reclaim                              avgt    5      6.829 ±    9.109  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_reclaim_batch                        avgt    5      6.799 ±    2.420  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_sign                                 avgt    5      7.181 ±    1.679  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_sign_batch                           avgt    5      7.605 ±    1.390  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.pool_from_conduit                         avgt    5      1.317 ±    0.233  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.pool_from_conduit_batch                   avgt    5      1.130 ±    0.211  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_acquire                          avgt    5      6.184 ±    7.718  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_acquire_batch                    avgt    5      7.611 ±    7.957  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_attempt                          avgt    5      7.279 ±    3.395  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_attempt_batch                    avgt    5      6.757 ±    6.775  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_deny                             avgt    5      7.116 ±    9.039  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_deny_batch                       avgt    5      7.196 ±    3.764  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_grant                            avgt    5      6.658 ±    9.224  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_grant_batch                      avgt    5      8.790 ±    2.372  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_release                          avgt    5      7.695 ±    3.399  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_release_batch                    avgt    5      6.802 ±   10.050  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_sign                             avgt    5      7.613 ±    3.573  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_sign_batch                       avgt    5      6.922 ±    2.091  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_timeout                          avgt    5      7.462 ±    3.140  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_timeout_batch                    avgt    5     10.173 ±    1.069  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.resource_from_conduit                 avgt    5      1.309 ±    0.195  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.resource_from_conduit_batch           avgt    5      1.121 ±    0.184  ns/op
i.h.serventis.jmh.opt.role.ActorOps.actor_from_conduit                       avgt    5      1.313 ±    0.131  ns/op
i.h.serventis.jmh.opt.role.ActorOps.actor_from_conduit_batch                 avgt    5      1.113 ±    0.199  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_acknowledge                         avgt    5      7.134 ±    2.371  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_acknowledge_batch                   avgt    5      8.280 ±    1.992  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_affirm                              avgt    5      7.618 ±    3.446  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_affirm_batch                        avgt    5      6.470 ±    8.242  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_ask                                 avgt    5      8.326 ±    0.618  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_ask_batch                           avgt    5      6.789 ±    3.117  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_clarify                             avgt    5      7.897 ±    1.151  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_clarify_batch                       avgt    5      7.020 ±    3.471  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_command                             avgt    5      7.540 ±    2.417  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_command_batch                       avgt    5      6.566 ±    5.447  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_deliver                             avgt    5      7.813 ±    0.888  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_deliver_batch                       avgt    5      6.704 ±    8.761  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_deny                                avgt    5      7.133 ±    9.970  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_deny_batch                          avgt    5      7.002 ±    9.994  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_explain                             avgt    5      8.719 ±    4.179  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_explain_batch                       avgt    5      6.536 ±    8.204  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_promise                             avgt    5      6.898 ±    8.302  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_promise_batch                       avgt    5      6.133 ±    6.561  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_report                              avgt    5      6.507 ±   10.165  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_report_batch                        avgt    5      5.916 ±    8.106  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_request                             avgt    5      7.006 ±    9.102  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_request_batch                       avgt    5      7.113 ±    7.414  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_sign                                avgt    5      7.505 ±    3.884  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_sign_batch                          avgt    5      8.111 ±    1.044  ns/op
i.h.serventis.jmh.opt.role.AgentOps.agent_from_conduit                       avgt    5      1.346 ±    0.203  ns/op
i.h.serventis.jmh.opt.role.AgentOps.agent_from_conduit_batch                 avgt    5      1.151 ±    0.227  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_accept                              avgt    5      7.363 ±    2.937  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_accept_batch                        avgt    5      7.273 ±    2.802  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_accepted                            avgt    5      6.674 ±    7.591  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_accepted_batch                      avgt    5      8.427 ±    2.793  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_breach                              avgt    5      7.167 ±    2.751  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_breach_batch                        avgt    5      7.034 ±    2.913  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_breached                            avgt    5      7.409 ±    2.482  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_breached_batch                      avgt    5      6.987 ±    2.900  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_depend                              avgt    5      8.334 ±    1.197  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_depend_batch                        avgt    5      8.580 ±    3.537  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_depended                            avgt    5      7.893 ±    1.068  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_depended_batch                      avgt    5      8.679 ±    3.399  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_fulfill                             avgt    5      6.264 ±    6.169  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_fulfill_batch                       avgt    5      6.992 ±    3.433  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_fulfilled                           avgt    5      6.992 ±    2.941  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_fulfilled_batch                     avgt    5      5.880 ±    7.693  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_inquire                             avgt    5      7.256 ±    2.971  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_inquire_batch                       avgt    5      6.435 ±    6.884  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_inquired                            avgt    5      7.092 ±    8.609  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_inquired_batch                      avgt    5      7.418 ±    9.452  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_observe                             avgt    5      7.709 ±    0.361  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_observe_batch                       avgt    5      7.310 ±    3.891  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_observed                            avgt    5      6.071 ±    5.297  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_observed_batch                      avgt    5      9.422 ±    0.967  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_offer                               avgt    5      6.839 ±    5.709  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_offer_batch                         avgt    5      9.132 ±    4.263  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_offered                             avgt    5      6.924 ±    1.107  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_offered_batch                       avgt    5      6.691 ±    2.649  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_promise                             avgt    5      7.624 ±    3.205  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_promise_batch                       avgt    5      8.561 ±    6.479  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_promised                            avgt    5      7.659 ±    2.794  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_promised_batch                      avgt    5      6.784 ±    5.180  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_retract                             avgt    5      7.218 ±    5.941  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_retract_batch                       avgt    5      6.493 ±    7.752  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_retracted                           avgt    5      6.760 ±    6.068  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_retracted_batch                     avgt    5      8.319 ±    2.079  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_signal                              avgt    5      6.801 ±    6.889  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_signal_batch                        avgt    5      7.026 ±    4.027  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_validate                            avgt    5      7.135 ±    3.419  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_validate_batch                      avgt    5      7.792 ±    1.988  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_validated                           avgt    5      6.653 ±    3.856  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_validated_batch                     avgt    5      6.310 ±    8.927  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.atomic_from_conduit                     avgt    5      1.348 ±    0.239  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.atomic_from_conduit_batch               avgt    5      1.121 ±    0.135  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_attempt                            avgt    5      6.429 ±    6.937  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_attempt_batch                      avgt    5      8.203 ±    2.770  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_backoff                            avgt    5      8.555 ±    3.022  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_backoff_batch                      avgt    5      6.826 ±    3.472  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_exhaust                            avgt    5      7.378 ±    2.392  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_exhaust_batch                      avgt    5      8.893 ±    1.458  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_fail                               avgt    5      7.862 ±    0.587  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_fail_batch                         avgt    5      6.946 ±   10.944  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_park                               avgt    5      6.602 ±   10.386  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_park_batch                         avgt    5      6.574 ±    7.477  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_sign                               avgt    5      7.788 ±    3.218  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_sign_batch                         avgt    5      7.370 ±    2.396  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_spin                               avgt    5      7.499 ±    3.671  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_spin_batch                         avgt    5      9.552 ±    4.374  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_success                            avgt    5      8.763 ±    3.575  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_success_batch                      avgt    5      6.890 ±    3.278  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_yield                              avgt    5      7.537 ±    3.778  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_yield_batch                        avgt    5      7.163 ±    4.672  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_abandon                             avgt    5      7.529 ±    3.816  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_abandon_batch                       avgt    5      6.748 ±    1.490  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_arrive                              avgt    5     10.206 ±    0.697  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_arrive_batch                        avgt    5      7.156 ±   10.024  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_await                               avgt    5      6.982 ±   10.554  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_await_batch                         avgt    5      6.581 ±    8.565  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_release                             avgt    5      8.473 ±    0.402  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_release_batch                       avgt    5      8.736 ±    2.663  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_reset                               avgt    5      7.108 ±    3.272  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_reset_batch                         avgt    5      8.318 ±    0.939  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_sign                                avgt    5      7.275 ±    3.413  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_sign_batch                          avgt    5      8.231 ±    0.814  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_timeout                             avgt    5      7.461 ±    8.289  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_timeout_batch                       avgt    5      7.608 ±    1.570  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.latch_from_conduit                       avgt    5      1.340 ±    0.182  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.latch_from_conduit_batch                 avgt    5      1.129 ±    0.179  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_abandon                              avgt    5      7.384 ±    3.558  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_abandon_batch                        avgt    5      8.241 ±    2.083  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_acquire                              avgt    5      7.404 ±    2.988  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_acquire_batch                        avgt    5      6.800 ±    1.761  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_attempt                              avgt    5      7.413 ±    3.605  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_attempt_batch                        avgt    5      6.720 ±    3.110  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_contest                              avgt    5      7.763 ±    2.649  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_contest_batch                        avgt    5      6.464 ±    5.180  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_deny                                 avgt    5      6.418 ±    5.669  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_deny_batch                           avgt    5      6.173 ±    6.652  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_downgrade                            avgt    5      6.582 ±    9.731  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_downgrade_batch                      avgt    5      6.752 ±    7.752  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_grant                                avgt    5      6.519 ±    8.925  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_grant_batch                          avgt    5      6.327 ±    8.575  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_release                              avgt    5      9.196 ±    2.829  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_release_batch                        avgt    5      7.147 ±    3.405  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_sign                                 avgt    5      7.164 ±    2.461  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_sign_batch                           avgt    5      6.997 ±    2.688  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_timeout                              avgt    5      6.808 ±    9.830  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_timeout_batch                        avgt    5      9.232 ±    1.308  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_upgrade                              avgt    5      8.966 ±    2.165  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_upgrade_batch                        avgt    5      6.836 ±    9.281  ns/op
i.h.serventis.jmh.opt.sync.LockOps.lock_from_conduit                         avgt    5      1.302 ±    0.237  ns/op
i.h.serventis.jmh.opt.sync.LockOps.lock_from_conduit_batch                   avgt    5      1.125 ±    0.188  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.counter_from_conduit                   avgt    5      1.319 ±    0.257  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.counter_from_conduit_batch             avgt    5      1.109 ±    0.149  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_increment                         avgt    5      7.808 ±    0.712  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_increment_batch                   avgt    5      8.634 ±    3.841  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_overflow                          avgt    5      7.152 ±    1.479  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_overflow_batch                    avgt    5      6.498 ±    8.357  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_reset                             avgt    5      8.005 ±    0.996  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_reset_batch                       avgt    5      7.054 ±    3.001  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_sign                              avgt    5      8.119 ±    2.042  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_sign_batch                        avgt    5      6.899 ±    8.658  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_decrement                           avgt    5      7.551 ±    3.560  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_decrement_batch                     avgt    5      6.814 ±    6.134  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_increment                           avgt    5      7.393 ±    1.652  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_increment_batch                     avgt    5      6.461 ±    9.032  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_overflow                            avgt    5      6.898 ±    8.996  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_overflow_batch                      avgt    5      8.071 ±    1.391  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_reset                               avgt    5      7.582 ±    4.017  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_reset_batch                         avgt    5      6.995 ±    7.782  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_sign                                avgt    5      6.377 ±    8.044  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_sign_batch                          avgt    5      8.031 ±    3.182  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_underflow                           avgt    5      7.018 ±    7.290  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_underflow_batch                     avgt    5      6.891 ±    2.962  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.gauge_from_conduit                       avgt    5      1.352 ±    0.216  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.gauge_from_conduit_batch                 avgt    5      1.123 ±    0.158  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_debug                                 avgt    5      6.533 ±    9.634  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_debug_batch                           avgt    5      6.926 ±    2.496  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_info                                  avgt    5      7.466 ±    2.776  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_info_batch                            avgt    5      7.714 ±    4.363  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_severe                                avgt    5      6.076 ±    7.102  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_severe_batch                          avgt    5      6.442 ±    7.962  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_sign                                  avgt    5      7.216 ±    2.923  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_sign_batch                            avgt    5      6.014 ±    6.915  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_warning                               avgt    5      7.635 ±    0.936  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_warning_batch                         avgt    5      8.125 ±    2.879  ns/op
i.h.serventis.jmh.opt.tool.LogOps.log_from_conduit                           avgt    5      1.344 ±    0.254  ns/op
i.h.serventis.jmh.opt.tool.LogOps.log_from_conduit_batch                     avgt    5      1.129 ±    0.209  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_connect                             avgt    5      6.532 ±    6.886  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_connect_batch                       avgt    5      7.332 ±    1.287  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_connected                           avgt    5      6.915 ±    2.160  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_connected_batch                     avgt    5      6.731 ±    8.536  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_disconnect                          avgt    5      7.283 ±    1.862  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_disconnect_batch                    avgt    5      6.363 ±    8.878  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_disconnected                        avgt    5      6.844 ±    7.011  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_disconnected_batch                  avgt    5      5.943 ±    6.499  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_fail                                avgt    5      6.934 ±    5.402  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_fail_batch                          avgt    5      6.019 ±    8.358  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_failed                              avgt    5      7.262 ±    2.525  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_failed_batch                        avgt    5      6.819 ±    7.513  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_process                             avgt    5      7.075 ±    2.074  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_process_batch                       avgt    5      7.269 ±    3.024  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_processed                           avgt    5      6.674 ±    4.822  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_processed_batch                     avgt    5      6.030 ±    9.202  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_receive_batch                       avgt    5      8.550 ±    3.942  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_received_batch                      avgt    5      6.303 ±    6.901  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_signal                              avgt    5      6.229 ±    5.751  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_signal_batch                        avgt    5      6.752 ±    6.659  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_succeed                             avgt    5      6.110 ±    7.810  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_succeed_batch                       avgt    5      6.551 ±    6.346  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_succeeded                           avgt    5      7.422 ±    2.556  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_succeeded_batch                     avgt    5      6.404 ±    8.379  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transfer                            avgt    5      5.895 ±    6.268  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transfer_inbound                    avgt    5      6.587 ±    6.406  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transfer_outbound                   avgt    5      6.716 ±    7.591  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transferred                         avgt    5      6.624 ±    5.836  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transmit_batch                      avgt    5      8.324 ±    3.438  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transmitted_batch                   avgt    5      6.828 ±    7.825  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.probe_from_conduit                       avgt    5      1.330 ±    0.247  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.probe_from_conduit_batch                 avgt    5      1.122 ±    0.163  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_baseline                     avgt    5      7.180 ±    6.213  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_baseline_batch               avgt    5      7.842 ±    0.614  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_target                       avgt    5      7.545 ±    4.270  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_target_batch                 avgt    5      7.320 ±    7.310  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_threshold                    avgt    5      6.376 ±    4.769  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_threshold_batch              avgt    5      6.571 ±    3.327  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_baseline                     avgt    5      6.837 ±    2.418  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_baseline_batch               avgt    5      8.476 ±    0.562  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_target                       avgt    5      7.291 ±    2.894  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_target_batch                 avgt    5      6.617 ±    2.566  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_threshold                    avgt    5      6.326 ±    6.106  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_threshold_batch              avgt    5      7.281 ±    0.972  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_baseline                   avgt    5      7.902 ±    3.028  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_baseline_batch             avgt    5      8.185 ±    3.412  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_target                     avgt    5      6.430 ±    9.528  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_target_batch               avgt    5      7.245 ±    0.830  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_threshold                  avgt    5      6.985 ±    6.880  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_threshold_batch            avgt    5      6.112 ±    7.565  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_signal                             avgt    5      7.012 ±    2.973  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_signal_batch                       avgt    5      8.686 ±    2.836  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.sensor_from_conduit                     avgt    5      1.331 ±    0.184  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.sensor_from_conduit_batch               avgt    5      1.107 ±    0.170  ns/op
i.h.serventis.jmh.sdk.OperationOps.emit_begin                                avgt    5      7.525 ±    7.786  ns/op
i.h.serventis.jmh.sdk.OperationOps.emit_begin_batch                          avgt    5      6.439 ±    9.829  ns/op
i.h.serventis.jmh.sdk.OperationOps.emit_end                                  avgt    5      6.957 ±    9.396  ns/op
i.h.serventis.jmh.sdk.OperationOps.emit_end_batch                            avgt    5      7.359 ±    8.206  ns/op
i.h.serventis.jmh.sdk.OperationOps.emit_sign                                 avgt    5      7.188 ±    3.043  ns/op
i.h.serventis.jmh.sdk.OperationOps.emit_sign_batch                           avgt    5      9.076 ±    1.698  ns/op
i.h.serventis.jmh.sdk.OperationOps.operation_from_conduit                    avgt    5      1.330 ±    0.190  ns/op
i.h.serventis.jmh.sdk.OperationOps.operation_from_conduit_batch              avgt    5      1.125 ±    0.160  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.emit_fail                                   avgt    5      7.584 ±    3.006  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.emit_fail_batch                             avgt    5      7.054 ±    3.871  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.emit_sign                                   avgt    5      6.971 ±    7.677  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.emit_sign_batch                             avgt    5      6.402 ±    8.634  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.emit_success                                avgt    5      7.412 ±    3.420  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.emit_success_batch                          avgt    5      7.153 ±    9.025  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.outcome_from_conduit                        avgt    5      1.330 ±    0.240  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.outcome_from_conduit_batch                  avgt    5      1.126 ±    0.281  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_mixed_pattern                         avgt    5      0.199 ±    0.025  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_single                                avgt    5      0.652 ±    0.112  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_single_batch                          avgt    5      0.021 ±    0.003  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_varied_batch                          avgt    5      1.551 ±    0.247  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_worst_case                            avgt    5      1.033 ±    0.148  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_critical                             avgt    5      7.427 ±    3.262  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_critical_batch                       avgt    5      7.725 ±    1.536  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_normal                               avgt    5      6.962 ±    3.426  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_normal_batch                         avgt    5      7.001 ±    2.187  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_signal                               avgt    5      6.384 ±    7.759  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_signal_batch                         avgt    5      8.276 ±    2.697  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_warning                              avgt    5      7.482 ±    3.491  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_warning_batch                        avgt    5      9.296 ±    0.492  ns/op
i.h.serventis.jmh.sdk.SituationOps.situation_from_conduit                    avgt    5      1.315 ±    0.142  ns/op
i.h.serventis.jmh.sdk.SituationOps.situation_from_conduit_batch              avgt    5      1.139 ±    0.115  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_converging_confirmed                    avgt    5      6.419 ±    7.548  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_converging_confirmed_batch              avgt    5      8.574 ±    3.923  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_defective_tentative                     avgt    5      6.199 ±    5.368  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_defective_tentative_batch               avgt    5      8.146 ±    2.411  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_degraded_measured                       avgt    5      8.627 ±    3.908  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_degraded_measured_batch                 avgt    5      7.922 ±    1.753  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_down_confirmed                          avgt    5      6.401 ±    4.572  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_down_confirmed_batch                    avgt    5      7.034 ±    9.063  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_signal                                  avgt    5      7.158 ±    7.067  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_signal_batch                            avgt    5      7.341 ±    3.221  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_stable_confirmed                        avgt    5      6.907 ±    4.115  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_stable_confirmed_batch                  avgt    5      6.211 ±    6.646  ns/op
i.h.serventis.jmh.sdk.StatusOps.status_from_conduit                          avgt    5      1.307 ±    0.231  ns/op
i.h.serventis.jmh.sdk.StatusOps.status_from_conduit_batch                    avgt    5      1.122 ±    0.104  ns/op
i.h.serventis.jmh.sdk.SurveyOps.emit_divided                                 avgt    5      6.799 ±    1.041  ns/op
i.h.serventis.jmh.sdk.SurveyOps.emit_divided_batch                           avgt    5      6.443 ±    2.534  ns/op
i.h.serventis.jmh.sdk.SurveyOps.emit_majority                                avgt    5      8.181 ±    1.208  ns/op
i.h.serventis.jmh.sdk.SurveyOps.emit_majority_batch                          avgt    5      5.078 ±    2.300  ns/op
i.h.serventis.jmh.sdk.SurveyOps.emit_signal                                  avgt    5      5.786 ±    2.020  ns/op
i.h.serventis.jmh.sdk.SurveyOps.emit_signal_batch                            avgt    5      5.433 ±    2.305  ns/op
i.h.serventis.jmh.sdk.SurveyOps.emit_unanimous                               avgt    5      6.349 ±    6.795  ns/op
i.h.serventis.jmh.sdk.SurveyOps.emit_unanimous_batch                         avgt    5      8.564 ±    3.789  ns/op
i.h.serventis.jmh.sdk.SurveyOps.survey_from_conduit                          avgt    5      1.318 ±    0.146  ns/op
i.h.serventis.jmh.sdk.SurveyOps.survey_from_conduit_batch                    avgt    5      1.130 ±    0.117  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_alarm_flow                              avgt    5      8.052 ±    3.130  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_alarm_flow_batch                        avgt    5      6.093 ±    7.103  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_fault_link                              avgt    5      6.190 ±    7.931  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_fault_link_batch                        avgt    5      8.288 ±    2.923  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_limit_time                              avgt    5      7.081 ±    1.012  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_limit_time_batch                        avgt    5      6.708 ±    2.571  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_normal_space                            avgt    5      6.322 ±    5.079  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_normal_space_batch                      avgt    5      7.144 ±    4.063  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_signal                                  avgt    5      7.118 ±    3.302  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_signal_batch                            avgt    5      6.631 ±    7.918  ns/op
i.h.serventis.jmh.sdk.SystemOps.system_from_conduit                          avgt    5      1.326 ±    0.245  ns/op
i.h.serventis.jmh.sdk.SystemOps.system_from_conduit_batch                    avgt    5      1.116 ±    0.118  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_chaos                                    avgt    5      7.672 ±    3.473  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_chaos_batch                              avgt    5      6.639 ±    8.433  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_cycle                                    avgt    5      8.504 ±    1.755  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_cycle_batch                              avgt    5      8.833 ±    2.571  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_drift                                    avgt    5      7.176 ±    2.524  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_drift_batch                              avgt    5      8.505 ±    2.733  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_sign                                     avgt    5      7.238 ±    2.071  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_sign_batch                               avgt    5      7.767 ±    1.943  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_spike                                    avgt    5      6.816 ±    8.635  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_spike_batch                              avgt    5      7.308 ±    3.902  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_stable                                   avgt    5      7.497 ±    3.448  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_stable_batch                             avgt    5      6.755 ±    3.196  ns/op
i.h.serventis.jmh.sdk.TrendOps.trend_from_conduit                            avgt    5      1.324 ±    0.261  ns/op
i.h.serventis.jmh.sdk.TrendOps.trend_from_conduit_batch                      avgt    5      1.144 ±    0.190  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.cycle_from_conduit                       avgt    5      1.320 ±    0.181  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.cycle_from_conduit_batch                 avgt    5      1.109 ±    0.138  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_repeat                              avgt    5      7.483 ±    2.955  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_repeat_batch                        avgt    5      7.901 ±    0.764  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_return                              avgt    5      7.336 ±    7.398  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_return_batch                        avgt    5      6.450 ±    7.221  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_signal                              avgt    5      7.488 ±    1.086  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_signal_batch                        avgt    5      6.775 ±    4.849  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_single                              avgt    5      7.097 ±    2.199  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_single_batch                        avgt    5      6.210 ±    1.267  ns/op
i.h.substrates.jmh.CircuitOps.conduit_create_close                           avgt    5    276.307 ±   68.883  ns/op
i.h.substrates.jmh.CircuitOps.conduit_create_named                           avgt    5    287.133 ±  161.687  ns/op
i.h.substrates.jmh.CircuitOps.conduit_create_with_flow                       avgt    5    286.224 ±  145.243  ns/op
i.h.substrates.jmh.CircuitOps.create_and_close                               avgt    5    467.593 ±  915.697  ns/op
i.h.substrates.jmh.CircuitOps.create_and_close_batch                         avgt    5    329.530 ±  265.851  ns/op
i.h.substrates.jmh.CircuitOps.create_multiple_and_close                      avgt    5   1592.392 ±  871.374  ns/op
i.h.substrates.jmh.CircuitOps.create_named_and_close                         avgt    5    321.533 ±  214.899  ns/op
i.h.substrates.jmh.CircuitOps.hot_conduit_create                             avgt    5     16.225 ±    3.462  ns/op
i.h.substrates.jmh.CircuitOps.hot_conduit_create_named                       avgt    5     16.042 ±    1.465  ns/op
i.h.substrates.jmh.CircuitOps.hot_conduit_create_with_flow                   avgt    5     19.210 ±    3.728  ns/op
i.h.substrates.jmh.CircuitOps.hot_pipe_async                                 avgt    5      8.296 ±    1.269  ns/op
i.h.substrates.jmh.CircuitOps.hot_pipe_async_with_flow                       avgt    5     10.124 ±    1.601  ns/op
i.h.substrates.jmh.CircuitOps.pipe_async                                     avgt    5    269.648 ±   74.469  ns/op
i.h.substrates.jmh.CircuitOps.pipe_async_with_flow                           avgt    5    298.450 ±  235.098  ns/op
i.h.substrates.jmh.ConduitOps.get_by_name                                    avgt    5      1.203 ±    0.265  ns/op
i.h.substrates.jmh.ConduitOps.get_by_name_batch                              avgt    5      0.939 ±    0.140  ns/op
i.h.substrates.jmh.ConduitOps.get_by_substrate                               avgt    5      1.711 ±    0.225  ns/op
i.h.substrates.jmh.ConduitOps.get_by_substrate_batch                         avgt    5      1.500 ±    0.173  ns/op
i.h.substrates.jmh.ConduitOps.get_cached                                     avgt    5      2.300 ±    0.339  ns/op
i.h.substrates.jmh.ConduitOps.get_cached_batch                               avgt    5      2.115 ±    0.229  ns/op
i.h.substrates.jmh.ConduitOps.get_varied                                     avgt    5      3.113 ±    0.564  ns/op
i.h.substrates.jmh.ConduitOps.get_varied_batch                               avgt    5      3.021 ±    0.493  ns/op
i.h.substrates.jmh.ConduitOps.subscribe                                      avgt    5    478.425 ±  605.106  ns/op
i.h.substrates.jmh.ConduitOps.subscribe_batch                                avgt    5    475.076 ±  442.302  ns/op
i.h.substrates.jmh.ConduitOps.subscribe_with_emission_await                  avgt    5   7741.067 ±  838.566  ns/op
i.h.substrates.jmh.CortexOps.circuit                                         avgt    5    277.614 ±  105.516  ns/op
i.h.substrates.jmh.CortexOps.circuit_batch                                   avgt    5    275.356 ±  190.644  ns/op
i.h.substrates.jmh.CortexOps.circuit_named                                   avgt    5    283.140 ±  128.442  ns/op
i.h.substrates.jmh.CortexOps.current                                         avgt    5      1.186 ±    0.219  ns/op
i.h.substrates.jmh.CortexOps.name_class                                      avgt    5      1.648 ±    0.157  ns/op
i.h.substrates.jmh.CortexOps.name_enum                                       avgt    5      1.927 ±    0.178  ns/op
i.h.substrates.jmh.CortexOps.name_iterable                                   avgt    5      8.609 ±    1.603  ns/op
i.h.substrates.jmh.CortexOps.name_path                                       avgt    5      2.091 ±    0.351  ns/op
i.h.substrates.jmh.CortexOps.name_path_batch                                 avgt    5      1.827 ±    0.315  ns/op
i.h.substrates.jmh.CortexOps.name_string                                     avgt    5      3.062 ±    0.595  ns/op
i.h.substrates.jmh.CortexOps.name_string_batch                               avgt    5      2.807 ±    0.669  ns/op
i.h.substrates.jmh.CortexOps.scope                                           avgt    5      9.472 ±    1.519  ns/op
i.h.substrates.jmh.CortexOps.scope_batch                                     avgt    5      8.332 ±    1.212  ns/op
i.h.substrates.jmh.CortexOps.scope_named                                     avgt    5      8.740 ±    1.543  ns/op
i.h.substrates.jmh.CortexOps.slot_boolean                                    avgt    5      1.949 ±    0.065  ns/op
i.h.substrates.jmh.CortexOps.slot_double                                     avgt    5      1.958 ±    0.207  ns/op
i.h.substrates.jmh.CortexOps.slot_int                                        avgt    5      1.918 ±    0.172  ns/op
i.h.substrates.jmh.CortexOps.slot_long                                       avgt    5      1.908 ±    0.281  ns/op
i.h.substrates.jmh.CortexOps.slot_string                                     avgt    5      1.909 ±    0.299  ns/op
i.h.substrates.jmh.CortexOps.state_empty                                     avgt    5      0.512 ±    0.139  ns/op
i.h.substrates.jmh.CortexOps.state_empty_batch                               avgt    5     ≈ 10⁻³             ns/op
i.h.substrates.jmh.CyclicOps.cyclic_emit                                     avgt    5      1.187 ±    1.086  ns/op
i.h.substrates.jmh.CyclicOps.cyclic_emit_await                               avgt    5     10.372 ±    2.616  ns/op
i.h.substrates.jmh.CyclicOps.cyclic_emit_await_batch                         avgt    5     10.436 ±    3.306  ns/op
i.h.substrates.jmh.CyclicOps.cyclic_emit_batch                               avgt    5      1.183 ±    0.554  ns/op
i.h.substrates.jmh.CyclicOps.cyclic_emit_deep_await                          avgt    5      4.330 ±    0.952  ns/op
i.h.substrates.jmh.CyclicOps.cyclic_emit_deep_await_batch                    avgt    5      4.354 ±    0.971  ns/op
i.h.substrates.jmh.FlowOps.baseline_no_flow_await                            avgt    5     19.119 ±    1.189  ns/op
i.h.substrates.jmh.FlowOps.flow_combined_diff_guard_await                    avgt    5     25.978 ±    3.344  ns/op
i.h.substrates.jmh.FlowOps.flow_combined_diff_sample_await                   avgt    5     19.686 ±    2.823  ns/op
i.h.substrates.jmh.FlowOps.flow_combined_guard_limit_await                   avgt    5     27.265 ±    3.535  ns/op
i.h.substrates.jmh.FlowOps.flow_diff_await                                   avgt    5     26.535 ±    4.641  ns/op
i.h.substrates.jmh.FlowOps.flow_guard_await                                  avgt    5     27.685 ±    5.172  ns/op
i.h.substrates.jmh.FlowOps.flow_limit_await                                  avgt    5     26.687 ±    1.464  ns/op
i.h.substrates.jmh.FlowOps.flow_sample_await                                 avgt    5     19.865 ±    1.218  ns/op
i.h.substrates.jmh.FlowOps.flow_sift_await                                   avgt    5     20.637 ±    2.295  ns/op
i.h.substrates.jmh.IdOps.id_from_subject                                     avgt    5      0.586 ±    0.129  ns/op
i.h.substrates.jmh.IdOps.id_from_subject_batch                               avgt    5      0.001 ±    0.001  ns/op
i.h.substrates.jmh.IdOps.id_toString                                         avgt    5     13.185 ±    1.848  ns/op
i.h.substrates.jmh.IdOps.id_toString_batch                                   avgt    5     14.289 ±    2.477  ns/op
i.h.substrates.jmh.NameOps.name_chained_deep                                 avgt    5      5.687 ±    1.141  ns/op
i.h.substrates.jmh.NameOps.name_chaining                                     avgt    5      9.191 ±    0.940  ns/op
i.h.substrates.jmh.NameOps.name_chaining_batch                               avgt    5      8.919 ±    1.441  ns/op
i.h.substrates.jmh.NameOps.name_compare                                      avgt    5      0.847 ±    0.132  ns/op
i.h.substrates.jmh.NameOps.name_compare_batch                                avgt    5      0.001 ±    0.001  ns/op
i.h.substrates.jmh.NameOps.name_depth                                        avgt    5      0.616 ±    0.076  ns/op
i.h.substrates.jmh.NameOps.name_depth_batch                                  avgt    5      0.001 ±    0.001  ns/op
i.h.substrates.jmh.NameOps.name_enclosure                                    avgt    5      0.650 ±    0.125  ns/op
i.h.substrates.jmh.NameOps.name_from_enum                                    avgt    5      1.978 ±    0.218  ns/op
i.h.substrates.jmh.NameOps.name_from_iterable                                avgt    5      9.768 ±    1.363  ns/op
i.h.substrates.jmh.NameOps.name_from_iterator                                avgt    5      9.586 ±    1.544  ns/op
i.h.substrates.jmh.NameOps.name_from_mapped_iterable                         avgt    5      9.923 ±    1.278  ns/op
i.h.substrates.jmh.NameOps.name_from_name                                    avgt    5      3.815 ±    0.718  ns/op
i.h.substrates.jmh.NameOps.name_from_string                                  avgt    5      3.390 ±    0.644  ns/op
i.h.substrates.jmh.NameOps.name_from_string_batch                            avgt    5      3.157 ±    0.305  ns/op
i.h.substrates.jmh.NameOps.name_hashCode                                     avgt    5      0.655 ±    0.181  ns/op
i.h.substrates.jmh.NameOps.name_hashCode_batch                               avgt    5      0.001 ±    0.001  ns/op
i.h.substrates.jmh.NameOps.name_interning_chained                            avgt    5     11.168 ±    2.061  ns/op
i.h.substrates.jmh.NameOps.name_interning_same_path                          avgt    5      3.873 ±    0.706  ns/op
i.h.substrates.jmh.NameOps.name_interning_segments                           avgt    5      9.601 ±    1.586  ns/op
i.h.substrates.jmh.NameOps.name_iterate_hierarchy                            avgt    5      2.067 ±    0.286  ns/op
i.h.substrates.jmh.NameOps.name_parsing                                      avgt    5      2.065 ±    0.259  ns/op
i.h.substrates.jmh.NameOps.name_parsing_batch                                avgt    5      1.851 ±    0.314  ns/op
i.h.substrates.jmh.NameOps.name_path_generation                              avgt    5      0.643 ±    0.063  ns/op
i.h.substrates.jmh.NameOps.name_path_generation_batch                        avgt    5      0.001 ±    0.001  ns/op
i.h.substrates.jmh.NameOps.name_within                                       avgt    5      1.778 ±    0.176  ns/op
i.h.substrates.jmh.NameOps.name_within_batch                                 avgt    5      1.101 ±    0.153  ns/op
i.h.substrates.jmh.NameOps.name_within_false                                 avgt    5      2.225 ±    0.338  ns/op
i.h.substrates.jmh.NameOps.name_within_false_batch                           avgt    5      1.399 ±    0.244  ns/op
i.h.substrates.jmh.PipeOps.async_emit_batch                                  avgt    5     10.205 ±    1.974  ns/op
i.h.substrates.jmh.PipeOps.async_emit_batch_await                            avgt    5     19.529 ±    1.445  ns/op
i.h.substrates.jmh.PipeOps.async_emit_chained_await                          avgt    5     20.310 ±    2.339  ns/op
i.h.substrates.jmh.PipeOps.async_emit_fanout_await                           avgt    5     19.636 ±    3.146  ns/op
i.h.substrates.jmh.PipeOps.async_emit_single                                 avgt    5      7.564 ±    3.038  ns/op
i.h.substrates.jmh.PipeOps.async_emit_single_await                           avgt    5   9023.320 ± 2518.598  ns/op
i.h.substrates.jmh.PipeOps.async_emit_with_flow_await                        avgt    5     23.061 ±    7.214  ns/op
i.h.substrates.jmh.PipeOps.baseline_blackhole                                avgt    5      0.287 ±    0.031  ns/op
i.h.substrates.jmh.PipeOps.baseline_counter                                  avgt    5      1.868 ±    0.234  ns/op
i.h.substrates.jmh.PipeOps.baseline_receptor                                 avgt    5      0.293 ±    0.054  ns/op
i.h.substrates.jmh.PipeOps.pipe_create                                       avgt    5      8.414 ±    0.829  ns/op
i.h.substrates.jmh.PipeOps.pipe_create_chained                               avgt    5      0.904 ±    0.193  ns/op
i.h.substrates.jmh.PipeOps.pipe_create_with_flow                             avgt    5     14.061 ±    1.700  ns/op
i.h.substrates.jmh.ReservoirOps.baseline_emit_no_reservoir_await             avgt    5    104.180 ±    2.863  ns/op
i.h.substrates.jmh.ReservoirOps.baseline_emit_no_reservoir_await_batch       avgt    5     19.777 ±    2.015  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_burst_then_drain_await             avgt    5     97.509 ±   29.942  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_burst_then_drain_await_batch       avgt    5     22.292 ±    2.282  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_drain_await                        avgt    5    108.353 ±   11.668  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_drain_await_batch                  avgt    5     22.801 ±    2.020  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_emit_drain_cycles_await            avgt    5    455.689 ±  130.100  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_emit_with_capture_await            avgt    5    104.032 ±   15.234  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_emit_with_capture_await_batch      avgt    5     19.316 ±    4.831  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_process_emissions_await            avgt    5    106.777 ±    8.023  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_process_emissions_await_batch      avgt    5     21.525 ±    4.532  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_process_subjects_await             avgt    5    102.022 ±   17.016  ns/op
i.h.substrates.jmh.ScopeOps.scope_child_anonymous                            avgt    5     18.797 ±    2.942  ns/op
i.h.substrates.jmh.ScopeOps.scope_child_anonymous_batch                      avgt    5     18.152 ±    1.834  ns/op
i.h.substrates.jmh.ScopeOps.scope_child_named                                avgt    5     18.663 ±    3.072  ns/op
i.h.substrates.jmh.ScopeOps.scope_child_named_batch                          avgt    5     18.632 ±    3.706  ns/op
i.h.substrates.jmh.ScopeOps.scope_close_idempotent                           avgt    5      2.757 ±    0.265  ns/op
i.h.substrates.jmh.ScopeOps.scope_close_idempotent_batch                     avgt    5      0.038 ±    0.004  ns/op
i.h.substrates.jmh.ScopeOps.scope_closure                                    avgt    5    314.875 ±   40.278  ns/op
i.h.substrates.jmh.ScopeOps.scope_closure_batch                              avgt    5    303.683 ±   93.382  ns/op
i.h.substrates.jmh.ScopeOps.scope_complex                                    avgt    5    881.685 ±  146.447  ns/op
i.h.substrates.jmh.ScopeOps.scope_create_and_close                           avgt    5      2.749 ±    0.470  ns/op
i.h.substrates.jmh.ScopeOps.scope_create_and_close_batch                     avgt    5      0.038 ±    0.007  ns/op
i.h.substrates.jmh.ScopeOps.scope_create_named                               avgt    5      2.795 ±    0.212  ns/op
i.h.substrates.jmh.ScopeOps.scope_create_named_batch                         avgt    5      0.038 ±    0.005  ns/op
i.h.substrates.jmh.ScopeOps.scope_hierarchy                                  avgt    5     29.665 ±    4.624  ns/op
i.h.substrates.jmh.ScopeOps.scope_hierarchy_batch                            avgt    5     29.087 ±    5.889  ns/op
i.h.substrates.jmh.ScopeOps.scope_parent_closes_children                     avgt    5     47.246 ±    8.800  ns/op
i.h.substrates.jmh.ScopeOps.scope_parent_closes_children_batch               avgt    5     46.180 ±    8.347  ns/op
i.h.substrates.jmh.ScopeOps.scope_register_multiple                          avgt    5   1420.638 ±  366.906  ns/op
i.h.substrates.jmh.ScopeOps.scope_register_multiple_batch                    avgt    5   1522.071 ±  385.761  ns/op
i.h.substrates.jmh.ScopeOps.scope_register_single                            avgt    5    303.361 ±   48.105  ns/op
i.h.substrates.jmh.ScopeOps.scope_register_single_batch                      avgt    5    302.882 ±   48.070  ns/op
i.h.substrates.jmh.ScopeOps.scope_with_resources                             avgt    5    584.957 ±  130.650  ns/op
i.h.substrates.jmh.StateOps.slot_name                                        avgt    5      0.571 ±    0.107  ns/op
i.h.substrates.jmh.StateOps.slot_name_batch                                  avgt    5      0.001 ±    0.001  ns/op
i.h.substrates.jmh.StateOps.slot_type                                        avgt    5      0.467 ±    0.031  ns/op
i.h.substrates.jmh.StateOps.slot_value                                       avgt    5      0.628 ±    0.050  ns/op
i.h.substrates.jmh.StateOps.slot_value_batch                                 avgt    5      0.001 ±    0.001  ns/op
i.h.substrates.jmh.StateOps.state_compact                                    avgt    5      7.721 ±    1.434  ns/op
i.h.substrates.jmh.StateOps.state_compact_batch                              avgt    5      8.060 ±    0.913  ns/op
i.h.substrates.jmh.StateOps.state_iterate_slots                              avgt    5      2.385 ±    0.334  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_int                               avgt    5      3.872 ±    0.602  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_int_batch                         avgt    5      3.845 ±    0.495  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_long                              avgt    5      3.808 ±    0.674  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_object                            avgt    5      2.015 ±    0.196  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_object_batch                      avgt    5      1.955 ±    0.304  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_string                            avgt    5      3.847 ±    0.589  ns/op
i.h.substrates.jmh.StateOps.state_value_read                                 avgt    5      1.368 ±    0.202  ns/op
i.h.substrates.jmh.StateOps.state_value_read_batch                           avgt    5      0.001 ±    0.001  ns/op
i.h.substrates.jmh.StateOps.state_values_stream                              avgt    5      3.979 ±    0.710  ns/op
i.h.substrates.jmh.SubjectOps.subject_compare                                avgt    5      4.232 ±    0.384  ns/op
i.h.substrates.jmh.SubjectOps.subject_compare_batch                          avgt    5      2.719 ±    0.550  ns/op
i.h.substrates.jmh.SubjectOps.subject_compare_same                           avgt    5      0.487 ±    0.094  ns/op
i.h.substrates.jmh.SubjectOps.subject_compare_same_batch                     avgt    5     ≈ 10⁻³             ns/op
i.h.substrates.jmh.SubjectOps.subject_compare_three_way                      avgt    5     12.115 ±    1.522  ns/op
i.h.substrates.jmh.SubscriberOps.close_five_conduits_await                   avgt    5   9419.497 ±  632.397  ns/op
i.h.substrates.jmh.SubscriberOps.close_five_subscriptions_await              avgt    5   9334.676 ± 1014.684  ns/op
i.h.substrates.jmh.SubscriberOps.close_idempotent_await                      avgt    5   9223.779 ±  157.786  ns/op
i.h.substrates.jmh.SubscriberOps.close_idempotent_batch_await                avgt    5     19.116 ±    1.597  ns/op
i.h.substrates.jmh.SubscriberOps.close_no_subscriptions_await                avgt    5   9211.840 ± 1479.963  ns/op
i.h.substrates.jmh.SubscriberOps.close_no_subscriptions_batch_await          avgt    5     15.890 ±    3.977  ns/op
i.h.substrates.jmh.SubscriberOps.close_one_subscription_await                avgt    5   9176.935 ±  998.220  ns/op
i.h.substrates.jmh.SubscriberOps.close_one_subscription_batch_await          avgt    5     32.074 ±    3.227  ns/op
i.h.substrates.jmh.SubscriberOps.close_ten_conduits_await                    avgt    5   9420.414 ±  809.016  ns/op
i.h.substrates.jmh.SubscriberOps.close_ten_subscriptions_await               avgt    5   9206.131 ±  585.179  ns/op
i.h.substrates.jmh.SubscriberOps.close_with_pending_emissions_await          avgt    5   9266.718 ± 1173.580  ns/op
i.h.substrates.jmh.TapOps.baseline_emit_batch_await                          avgt    5     20.537 ±    2.319  ns/op
i.h.substrates.jmh.TapOps.tap_close                                          avgt    5   7444.696 ± 1258.803  ns/op
i.h.substrates.jmh.TapOps.tap_create_batch                                   avgt    5    524.004 ±  457.157  ns/op
i.h.substrates.jmh.TapOps.tap_create_identity                                avgt    5    652.330 ±  884.789  ns/op
i.h.substrates.jmh.TapOps.tap_create_string                                  avgt    5    625.148 ±  647.781  ns/op
i.h.substrates.jmh.TapOps.tap_emit_identity_batch_await                      avgt    5     28.872 ±    2.942  ns/op
i.h.substrates.jmh.TapOps.tap_emit_identity_single                           avgt    5     25.409 ±   15.566  ns/op
i.h.substrates.jmh.TapOps.tap_emit_identity_single_await                     avgt    5   8029.578 ± 1601.811  ns/op
i.h.substrates.jmh.TapOps.tap_emit_multi_batch_await                         avgt    5     41.240 ±    5.068  ns/op
i.h.substrates.jmh.TapOps.tap_emit_string_batch_await                        avgt    5     37.683 ±    4.896  ns/op
i.h.substrates.jmh.TapOps.tap_lifecycle                                      avgt    5  18756.077 ± 2853.639  ns/op
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