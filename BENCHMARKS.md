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
i.h.serventis.jmh.opt.data.CacheOps.cache_from_conduit                       avgt    5      2.043 ±    0.337  ns/op
i.h.serventis.jmh.opt.data.CacheOps.cache_from_conduit_batch                 avgt    5      1.833 ±    0.314  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_evict                               avgt    5      8.837 ±    6.984  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_evict_batch                         avgt    5      7.239 ±    8.335  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_expire                              avgt    5      7.947 ±    5.078  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_expire_batch                        avgt    5      7.078 ±    3.558  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_hit                                 avgt    5      6.644 ±    7.208  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_hit_batch                           avgt    5      8.768 ±    6.677  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_lookup                              avgt    5      8.182 ±    3.676  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_lookup_batch                        avgt    5      9.191 ±    2.432  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_miss                                avgt    5      7.010 ±    9.613  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_miss_batch                          avgt    5      8.663 ±    2.289  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_remove                              avgt    5      7.684 ±    4.284  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_remove_batch                        avgt    5      8.313 ±    0.751  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_sign                                avgt    5      6.832 ±   10.336  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_sign_batch                          avgt    5      8.832 ±    0.728  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_store                               avgt    5      7.073 ±    7.777  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_store_batch                         avgt    5      6.857 ±    3.104  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_aggregate                        avgt    5      7.597 ±    0.775  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_aggregate_batch                  avgt    5      9.055 ±    2.149  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_backpressure                     avgt    5      7.790 ±    0.380  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_backpressure_batch               avgt    5      7.235 ±    7.059  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_buffer                           avgt    5      7.177 ±    2.934  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_buffer_batch                     avgt    5      5.811 ±    4.750  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_checkpoint                       avgt    5      7.060 ±    7.746  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_checkpoint_batch                 avgt    5      6.775 ±    9.837  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_filter                           avgt    5      8.809 ±    2.097  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_filter_batch                     avgt    5      6.660 ±    9.392  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_input                            avgt    5      7.716 ±    3.305  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_input_batch                      avgt    5      6.700 ±    3.249  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_lag                              avgt    5      6.639 ±    7.850  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_lag_batch                        avgt    5      7.596 ±    6.027  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_output                           avgt    5      7.672 ±    0.960  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_output_batch                     avgt    5      6.799 ±    9.562  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_overflow                         avgt    5      7.092 ±    9.245  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_overflow_batch                   avgt    5      7.157 ±    9.877  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_sign                             avgt    5      7.294 ±    8.377  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_sign_batch                       avgt    5      9.636 ±    1.083  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_skip                             avgt    5      9.387 ±    2.074  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_skip_batch                       avgt    5      7.810 ±    5.997  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_transform                        avgt    5      6.009 ±    7.948  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_transform_batch                  avgt    5      7.173 ±    8.568  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_watermark                        avgt    5      7.551 ±    3.467  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_watermark_batch                  avgt    5      7.157 ±    3.625  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_flow_etl                     avgt    5     39.445 ±   19.028  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_flow_stream                  avgt    5     37.167 ±    9.476  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_flow_windowed                avgt    5     40.449 ±   20.020  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_from_conduit                 avgt    5      2.079 ±    0.321  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_from_conduit_batch           avgt    5      1.828 ±    0.336  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_dequeue                             avgt    5      7.034 ±   10.142  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_dequeue_batch                       avgt    5      8.640 ±    2.782  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_enqueue                             avgt    5      6.934 ±    9.223  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_enqueue_batch                       avgt    5      6.528 ±    9.502  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_overflow                            avgt    5      7.193 ±    8.637  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_overflow_batch                      avgt    5      7.180 ±    4.527  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_sign                                avgt    5      6.902 ±    9.543  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_sign_batch                          avgt    5      6.157 ±    6.347  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_underflow                           avgt    5      9.455 ±    4.968  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_underflow_batch                     avgt    5      6.626 ±    6.746  ns/op
i.h.serventis.jmh.opt.data.QueueOps.queue_from_conduit                       avgt    5      2.057 ±    0.288  ns/op
i.h.serventis.jmh.opt.data.QueueOps.queue_from_conduit_batch                 avgt    5      1.828 ±    0.168  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_overflow                            avgt    5      6.701 ±    7.033  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_overflow_batch                      avgt    5      7.779 ±    0.533  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_pop                                 avgt    5      6.800 ±    6.557  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_pop_batch                           avgt    5      9.531 ±    1.892  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_push                                avgt    5      7.542 ±    3.495  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_push_batch                          avgt    5      7.274 ±    2.989  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_sign                                avgt    5      7.487 ±    3.899  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_sign_batch                          avgt    5      6.925 ±    3.547  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_underflow                           avgt    5      6.665 ±    9.539  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_underflow_batch                     avgt    5      6.980 ±    8.300  ns/op
i.h.serventis.jmh.opt.data.StackOps.stack_from_conduit                       avgt    5      2.052 ±    0.331  ns/op
i.h.serventis.jmh.opt.data.StackOps.stack_from_conduit_batch                 avgt    5      1.832 ±    0.239  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_crash                             avgt    5      8.648 ±    0.878  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_crash_batch                       avgt    5      8.414 ±    0.884  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_fail                              avgt    5      6.420 ±    7.802  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_fail_batch                        avgt    5      6.535 ±    8.307  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_kill                              avgt    5      6.100 ±    8.124  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_kill_batch                        avgt    5      7.152 ±    3.321  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_restart                           avgt    5      7.344 ±    2.154  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_restart_batch                     avgt    5      6.770 ±    3.450  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_resume                            avgt    5      7.560 ±    2.908  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_resume_batch                      avgt    5      7.198 ±    7.342  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_sign                              avgt    5      8.445 ±    0.572  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_sign_batch                        avgt    5      6.601 ±    1.128  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_spawn                             avgt    5      7.610 ±    3.071  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_spawn_batch                       avgt    5      7.839 ±    0.565  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_start                             avgt    5      7.457 ±    6.548  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_start_batch                       avgt    5      7.473 ±    9.072  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_stop                              avgt    5      7.559 ±    3.451  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_stop_batch                        avgt    5      7.131 ±    2.131  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_suspend                           avgt    5      7.332 ±    8.602  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_suspend_batch                     avgt    5      7.073 ±    2.362  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.process_from_conduit                   avgt    5      2.067 ±    0.249  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.process_from_conduit_batch             avgt    5      1.828 ±    0.238  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_call                              avgt    5      7.797 ±    1.065  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_call_batch                        avgt    5      6.278 ±    6.612  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_called                            avgt    5      7.507 ±    2.842  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_called_batch                      avgt    5      5.789 ±    7.265  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_delay                             avgt    5      6.959 ±    9.628  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_delay_batch                       avgt    5      8.949 ±    3.180  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_delayed                           avgt    5      6.991 ±    3.365  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_delayed_batch                     avgt    5      7.001 ±    2.583  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_discard                           avgt    5      6.437 ±    7.432  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_discard_batch                     avgt    5      5.821 ±    8.029  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_discarded                         avgt    5      7.312 ±    3.692  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_discarded_batch                   avgt    5      7.241 ±    2.770  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_disconnect                        avgt    5      6.774 ±    7.337  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_disconnect_batch                  avgt    5      6.968 ±    1.914  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_disconnected                      avgt    5      7.834 ±    0.661  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_disconnected_batch                avgt    5      7.884 ±    2.070  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_expire                            avgt    5      7.590 ±    2.937  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_expire_batch                      avgt    5      7.580 ±    0.952  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_expired                           avgt    5      7.771 ±    0.484  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_expired_batch                     avgt    5      7.064 ±    2.853  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_fail                              avgt    5      7.364 ±    2.692  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_fail_batch                        avgt    5      7.093 ±    3.245  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_failed                            avgt    5      7.630 ±    2.880  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_failed_batch                      avgt    5      8.741 ±    3.923  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_recourse                          avgt    5      7.354 ±    3.369  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_recourse_batch                    avgt    5      7.234 ±    2.046  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_recoursed                         avgt    5      6.272 ±    5.118  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_recoursed_batch                   avgt    5      7.227 ±    3.898  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_redirect                          avgt    5      8.374 ±    2.093  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_redirect_batch                    avgt    5      7.812 ±    5.759  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_redirected                        avgt    5      7.437 ±    3.786  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_redirected_batch                  avgt    5      7.392 ±    3.867  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_reject                            avgt    5      7.514 ±    2.531  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_reject_batch                      avgt    5      7.291 ±    0.531  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_rejected                          avgt    5      8.362 ±    0.848  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_rejected_batch                    avgt    5      7.894 ±    6.153  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_resume                            avgt    5      6.537 ±    2.271  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_resume_batch                      avgt    5      8.720 ±    0.505  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_resumed                           avgt    5      7.801 ±    3.592  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_resumed_batch                     avgt    5      8.175 ±    3.935  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_retried                           avgt    5      7.726 ±    2.870  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_retried_batch                     avgt    5      7.896 ±    4.016  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_retry                             avgt    5      7.670 ±    2.213  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_retry_batch                       avgt    5      7.291 ±    2.233  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_schedule                          avgt    5      8.351 ±    0.857  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_schedule_batch                    avgt    5      7.089 ±    3.794  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_scheduled                         avgt    5      7.690 ±    1.797  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_scheduled_batch                   avgt    5      7.768 ±    2.458  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_signal                            avgt    5      8.339 ±    0.667  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_signal_batch                      avgt    5      7.316 ±    2.937  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_start                             avgt    5      8.120 ±    0.588  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_start_batch                       avgt    5      7.423 ±    2.153  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_started                           avgt    5      7.194 ±    2.462  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_started_batch                     avgt    5      7.735 ±    4.817  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_stop                              avgt    5      7.986 ±    2.460  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_stop_batch                        avgt    5      7.203 ±    2.311  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_stopped                           avgt    5      7.279 ±    1.597  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_stopped_batch                     avgt    5      7.570 ±    4.195  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_succeeded                         avgt    5      8.691 ±    0.535  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_succeeded_batch                   avgt    5      8.888 ±    0.903  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_success                           avgt    5      7.846 ±    1.347  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_success_batch                     avgt    5      7.589 ±    3.006  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_suspend                           avgt    5      8.038 ±    0.628  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_suspend_batch                     avgt    5      9.064 ±    2.800  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_suspended                         avgt    5      7.676 ±    1.918  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_suspended_batch                   avgt    5      8.455 ±    0.997  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.service_from_conduit                   avgt    5      1.875 ±    0.045  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.service_from_conduit_batch             avgt    5      1.709 ±    0.039  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_cancel                               avgt    5      7.548 ±    2.477  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_cancel_batch                         avgt    5      7.398 ±    2.883  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_complete                             avgt    5      8.427 ±    2.270  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_complete_batch                       avgt    5      7.404 ±    4.815  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_fail                                 avgt    5      7.683 ±    3.871  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_fail_batch                           avgt    5      7.148 ±    2.746  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_progress                             avgt    5      7.353 ±    2.197  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_progress_batch                       avgt    5      7.176 ±    3.392  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_reject                               avgt    5      8.132 ±    0.430  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_reject_batch                         avgt    5      7.421 ±    2.996  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_resume                               avgt    5      7.271 ±    2.866  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_resume_batch                         avgt    5      8.783 ±    0.730  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_schedule                             avgt    5      7.826 ±    3.110  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_schedule_batch                       avgt    5      7.204 ±    3.086  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_sign                                 avgt    5      7.995 ±    3.461  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_sign_batch                           avgt    5      7.189 ±    3.389  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_start                                avgt    5      7.883 ±    2.829  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_start_batch                          avgt    5      8.654 ±    2.118  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_submit                               avgt    5      7.635 ±    3.954  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_submit_batch                         avgt    5      8.131 ±    1.328  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_suspend                              avgt    5      8.085 ±    4.819  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_suspend_batch                        avgt    5      7.234 ±    3.249  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_timeout                              avgt    5      7.909 ±    3.441  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_timeout_batch                        avgt    5      7.146 ±    3.352  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.task_from_conduit                         avgt    5      1.882 ±    0.082  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.task_from_conduit_batch                   avgt    5      1.708 ±    0.097  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_meet_deadline                       avgt    5      8.020 ±    5.531  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_meet_deadline_batch                 avgt    5      6.873 ±    2.776  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_meet_threshold                      avgt    5      9.138 ±    4.068  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_meet_threshold_batch                avgt    5      7.446 ±    2.178  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_miss_deadline                       avgt    5      7.425 ±    2.978  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_miss_deadline_batch                 avgt    5      7.452 ±    3.450  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_miss_threshold                      avgt    5      7.577 ±    1.695  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_miss_threshold_batch                avgt    5      7.169 ±    2.762  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_signal                              avgt    5      7.548 ±    2.795  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_signal_batch                        avgt    5      8.858 ±    3.797  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.timer_from_conduit                       avgt    5      1.892 ±    0.075  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.timer_from_conduit_batch                 avgt    5      1.683 ±    0.031  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_abort_coordinator             avgt    5      7.627 ±    4.885  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_abort_coordinator_batch       avgt    5      6.754 ±    2.513  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_abort_participant             avgt    5      6.971 ±    3.247  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_abort_participant_batch       avgt    5      7.399 ±    2.503  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_commit_coordinator            avgt    5      7.065 ±    2.851  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_commit_coordinator_batch      avgt    5      7.430 ±    3.144  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_commit_participant            avgt    5      7.948 ±    3.008  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_commit_participant_batch      avgt    5      7.258 ±    3.930  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_compensate_coordinator        avgt    5      8.008 ±    0.852  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_compensate_coordinator_batch  avgt    5      8.048 ±    1.139  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_compensate_participant        avgt    5      7.427 ±    1.509  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_compensate_participant_batch  avgt    5      8.498 ±    0.719  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_conflict_coordinator          avgt    5      7.890 ±    3.427  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_conflict_coordinator_batch    avgt    5      7.019 ±    3.301  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_conflict_participant          avgt    5      7.992 ±    0.559  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_conflict_participant_batch    avgt    5      7.374 ±    2.684  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_expire_coordinator            avgt    5      8.352 ±    1.459  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_expire_coordinator_batch      avgt    5      7.379 ±    4.712  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_expire_participant            avgt    5      7.665 ±    3.720  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_expire_participant_batch      avgt    5      8.713 ±    4.115  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_prepare_coordinator           avgt    5      7.308 ±    3.007  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_prepare_coordinator_batch     avgt    5      6.946 ±    2.729  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_prepare_participant           avgt    5      7.097 ±    3.039  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_prepare_participant_batch     avgt    5      6.435 ±    6.701  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_rollback_coordinator          avgt    5      7.358 ±    0.362  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_rollback_coordinator_batch    avgt    5      8.375 ±    2.655  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_rollback_participant          avgt    5      7.613 ±    0.989  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_rollback_participant_batch    avgt    5      7.571 ±    4.677  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_signal                        avgt    5      7.830 ±    3.757  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_signal_batch                  avgt    5      7.655 ±    4.290  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_start_coordinator             avgt    5      7.204 ±    4.161  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_start_coordinator_batch       avgt    5      8.438 ±    0.615  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_start_participant             avgt    5      8.499 ±    3.346  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_start_participant_batch       avgt    5      7.274 ±    4.258  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.transaction_from_conduit           avgt    5      1.883 ±    0.092  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.transaction_from_conduit_batch     avgt    5      1.688 ±    0.021  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.breaker_from_conduit                   avgt    5      1.875 ±    0.082  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.breaker_from_conduit_batch             avgt    5      1.674 ±    0.009  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_close                             avgt    5      8.183 ±    0.505  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_close_batch                       avgt    5      8.288 ±    2.587  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_half_open                         avgt    5      9.043 ±    1.310  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_half_open_batch                   avgt    5      6.799 ±    2.990  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_open                              avgt    5      8.083 ±    2.570  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_open_batch                        avgt    5      8.228 ±    1.916  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_probe                             avgt    5      8.272 ±    5.019  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_probe_batch                       avgt    5      6.614 ±    3.516  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_reset                             avgt    5      7.764 ±    3.382  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_reset_batch                       avgt    5      8.102 ±    0.445  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_sign                              avgt    5      7.563 ±    4.186  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_sign_batch                        avgt    5      8.310 ±    2.466  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_trip                              avgt    5      7.711 ±    2.906  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_trip_batch                        avgt    5      8.677 ±    0.746  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_fail_egress                          avgt    5      6.851 ±    2.311  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_fail_egress_batch                    avgt    5      6.959 ±    3.044  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_fail_ingress                         avgt    5      7.401 ±    2.278  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_fail_ingress_batch                   avgt    5     10.274 ±    2.976  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_fail_transit                         avgt    5      7.520 ±    3.281  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_fail_transit_batch                   avgt    5      7.357 ±    3.185  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_signal                               avgt    5      8.744 ±    1.688  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_signal_batch                         avgt    5      7.186 ±    3.095  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_success_egress                       avgt    5      7.237 ±    2.902  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_success_egress_batch                 avgt    5      7.385 ±    4.011  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_success_ingress                      avgt    5      7.420 ±    3.483  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_success_ingress_batch                avgt    5      8.868 ±    3.286  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_success_transit                      avgt    5      7.733 ±    3.191  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_success_transit_batch                avgt    5      8.820 ±    3.628  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.flow_from_conduit                         avgt    5      1.880 ±    0.075  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.flow_from_conduit_batch                   avgt    5      1.686 ±    0.042  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_corrupt                            avgt    5      7.356 ±    3.721  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_corrupt_batch                      avgt    5      7.301 ±    4.831  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_drop                               avgt    5      7.526 ±    3.590  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_drop_batch                         avgt    5      7.341 ±    3.386  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_forward                            avgt    5      7.434 ±    3.137  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_forward_batch                      avgt    5      7.258 ±    2.450  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_fragment                           avgt    5      7.556 ±    2.639  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_fragment_batch                     avgt    5      7.350 ±    2.082  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_reassemble                         avgt    5      8.100 ±    0.354  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_reassemble_batch                   avgt    5      7.019 ±    5.189  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_receive                            avgt    5      7.348 ±    2.368  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_receive_batch                      avgt    5      7.262 ±    3.248  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_reorder                            avgt    5      7.499 ±    2.874  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_reorder_batch                      avgt    5      6.880 ±    2.473  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_route                              avgt    5      8.360 ±    0.586  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_route_batch                        avgt    5      6.819 ±    2.810  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_send                               avgt    5      7.529 ±    4.510  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_send_batch                         avgt    5      7.289 ±    3.132  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_sign                               avgt    5      8.448 ±    1.194  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_sign_batch                         avgt    5      7.843 ±    3.539  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.router_from_conduit                     avgt    5      1.873 ±    0.063  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.router_from_conduit_batch               avgt    5      1.680 ±    0.026  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_contract                            avgt    5      7.629 ±    5.759  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_contract_batch                      avgt    5      6.516 ±    3.241  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_deny                                avgt    5      8.931 ±    0.950  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_deny_batch                          avgt    5      7.062 ±    1.878  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_drain                               avgt    5      7.759 ±    1.173  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_drain_batch                         avgt    5      7.049 ±    3.852  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_drop                                avgt    5      7.442 ±    3.446  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_drop_batch                          avgt    5      7.567 ±    5.174  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_expand                              avgt    5      7.493 ±    2.928  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_expand_batch                        avgt    5      7.094 ±    4.449  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_pass                                avgt    5      7.472 ±    2.597  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_pass_batch                          avgt    5      7.570 ±    2.979  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_sign                                avgt    5      8.654 ±    1.153  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_sign_batch                          avgt    5      7.066 ±    3.173  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.valve_from_conduit                       avgt    5      1.901 ±    0.078  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.valve_from_conduit_batch                 avgt    5      1.687 ±    0.041  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_contract_provider                avgt    5      7.375 ±    2.673  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_contract_provider_batch          avgt    5      7.152 ±    2.452  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_contract_receiver                avgt    5      7.378 ±    1.032  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_contract_receiver_batch          avgt    5      7.227 ±    3.443  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_full_exchange                    avgt    5      8.445 ±    1.896  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_full_exchange_batch              avgt    5      9.231 ±    5.834  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_signal                           avgt    5      7.584 ±    2.743  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_signal_batch                     avgt    5      7.020 ±    2.046  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_transfer_provider                avgt    5      7.507 ±    3.491  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_transfer_provider_batch          avgt    5      7.656 ±    1.125  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_transfer_receiver                avgt    5      8.650 ±    1.295  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_transfer_receiver_batch          avgt    5      7.609 ±    4.362  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.exchange_from_conduit                 avgt    5      1.884 ±    0.046  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.exchange_from_conduit_batch           avgt    5      1.679 ±    0.038  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_acquire                             avgt    5      8.955 ±    4.138  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_acquire_batch                       avgt    5      8.528 ±    2.782  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_acquired                            avgt    5      7.051 ±    2.718  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_acquired_batch                      avgt    5      8.553 ±    3.945  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_denied                              avgt    5      7.886 ±    0.749  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_denied_batch                        avgt    5      8.764 ±    1.392  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_deny                                avgt    5      7.169 ±    2.093  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_deny_batch                          avgt    5      8.618 ±    1.654  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_expire                              avgt    5      7.533 ±    3.174  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_expire_batch                        avgt    5      7.163 ±    2.370  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_expired                             avgt    5      8.032 ±    3.721  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_expired_batch                       avgt    5      8.015 ±    1.263  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_extend                              avgt    5      7.876 ±    0.523  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_extend_batch                        avgt    5      8.154 ±    2.436  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_extended                            avgt    5      7.683 ±    2.439  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_extended_batch                      avgt    5      9.010 ±    1.998  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_grant                               avgt    5      7.685 ±    0.699  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_grant_batch                         avgt    5      8.492 ±    1.755  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_granted                             avgt    5      7.874 ±    3.988  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_granted_batch                       avgt    5      7.113 ±    2.805  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_probe                               avgt    5      7.711 ±    3.157  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_probe_batch                         avgt    5      6.999 ±    2.319  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_probed                              avgt    5      7.195 ±    1.673  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_probed_batch                        avgt    5      7.393 ±    4.607  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_release                             avgt    5      7.352 ±    3.948  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_release_batch                       avgt    5      7.373 ±    4.666  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_released                            avgt    5      7.990 ±    5.577  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_released_batch                      avgt    5      7.743 ±    4.508  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_renew                               avgt    5      7.767 ±    2.661  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_renew_batch                         avgt    5      7.211 ±    2.375  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_renewed                             avgt    5      7.565 ±    2.702  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_renewed_batch                       avgt    5      7.128 ±    4.286  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_revoke                              avgt    5      7.860 ±    3.321  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_revoke_batch                        avgt    5      7.146 ±    1.660  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_revoked                             avgt    5      7.460 ±    1.620  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_revoked_batch                       avgt    5      7.278 ±    3.947  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_signal                              avgt    5      7.916 ±    3.138  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_signal_batch                        avgt    5      6.915 ±    2.242  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.lease_from_conduit                       avgt    5      1.862 ±    0.010  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.lease_from_conduit_batch                 avgt    5      1.679 ±    0.028  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_borrow                               avgt    5      7.411 ±    3.166  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_borrow_batch                         avgt    5      7.280 ±    2.663  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_contract                             avgt    5      9.119 ±    1.399  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_contract_batch                       avgt    5      7.272 ±    3.093  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_expand                               avgt    5      7.752 ±    4.064  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_expand_batch                         avgt    5      7.037 ±    3.691  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_reclaim                              avgt    5      8.284 ±    0.954  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_reclaim_batch                        avgt    5      6.363 ±    3.622  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_sign                                 avgt    5      7.230 ±    2.608  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_sign_batch                           avgt    5      7.630 ±    5.708  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.pool_from_conduit                         avgt    5      1.880 ±    0.097  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.pool_from_conduit_batch                   avgt    5      1.672 ±    0.011  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_acquire                          avgt    5      7.371 ±    3.174  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_acquire_batch                    avgt    5      7.482 ±    3.902  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_attempt                          avgt    5      7.483 ±    4.310  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_attempt_batch                    avgt    5      7.166 ±    3.187  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_deny                             avgt    5      7.477 ±    1.846  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_deny_batch                       avgt    5      6.916 ±    6.918  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_grant                            avgt    5      7.254 ±    2.537  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_grant_batch                      avgt    5      7.686 ±    4.034  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_release                          avgt    5      8.384 ±    0.751  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_release_batch                    avgt    5      7.187 ±    3.878  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_sign                             avgt    5      8.446 ±    1.131  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_sign_batch                       avgt    5      8.137 ±    2.825  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_timeout                          avgt    5      7.610 ±    1.953  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_timeout_batch                    avgt    5      7.087 ±    3.434  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.resource_from_conduit                 avgt    5      1.878 ±    0.054  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.resource_from_conduit_batch           avgt    5      1.671 ±    0.022  ns/op
i.h.serventis.jmh.opt.role.ActorOps.actor_from_conduit                       avgt    5      1.876 ±    0.033  ns/op
i.h.serventis.jmh.opt.role.ActorOps.actor_from_conduit_batch                 avgt    5      1.671 ±    0.013  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_acknowledge                         avgt    5      7.949 ±    4.575  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_acknowledge_batch                   avgt    5      7.007 ±    3.257  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_affirm                              avgt    5      8.070 ±    4.360  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_affirm_batch                        avgt    5      7.074 ±    3.476  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_ask                                 avgt    5      7.372 ±    2.943  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_ask_batch                           avgt    5      7.273 ±    4.256  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_clarify                             avgt    5      8.000 ±    3.792  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_clarify_batch                       avgt    5      8.252 ±    0.794  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_command                             avgt    5      7.310 ±    3.060  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_command_batch                       avgt    5      7.702 ±    3.336  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_deliver                             avgt    5      7.894 ±    3.353  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_deliver_batch                       avgt    5      7.985 ±    0.493  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_deny                                avgt    5      7.762 ±    7.430  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_deny_batch                          avgt    5      7.519 ±    3.981  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_explain                             avgt    5      7.573 ±    2.411  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_explain_batch                       avgt    5      7.894 ±    0.834  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_promise                             avgt    5      7.763 ±    3.592  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_promise_batch                       avgt    5      6.990 ±    3.013  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_report                              avgt    5      7.558 ±    2.393  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_report_batch                        avgt    5      7.329 ±    3.465  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_request                             avgt    5      7.467 ±    3.305  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_request_batch                       avgt    5      7.397 ±    0.508  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_sign                                avgt    5      7.373 ±    1.270  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_sign_batch                          avgt    5      7.300 ±    4.304  ns/op
i.h.serventis.jmh.opt.role.AgentOps.agent_from_conduit                       avgt    5      1.858 ±    0.026  ns/op
i.h.serventis.jmh.opt.role.AgentOps.agent_from_conduit_batch                 avgt    5      1.669 ±    0.014  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_accept                              avgt    5      7.714 ±    3.124  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_accept_batch                        avgt    5      7.965 ±    1.404  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_accepted                            avgt    5      8.804 ±    1.181  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_accepted_batch                      avgt    5      7.526 ±    3.013  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_breach                              avgt    5      7.676 ±    3.613  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_breach_batch                        avgt    5      7.686 ±    3.827  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_breached                            avgt    5      7.612 ±    3.184  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_breached_batch                      avgt    5      8.888 ±    3.051  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_depend                              avgt    5      7.797 ±    3.302  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_depend_batch                        avgt    5      7.290 ±    3.087  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_depended                            avgt    5      8.767 ±    1.732  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_depended_batch                      avgt    5      7.253 ±    2.947  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_fulfill                             avgt    5      7.661 ±    2.749  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_fulfill_batch                       avgt    5      7.427 ±    2.112  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_fulfilled                           avgt    5      7.525 ±    2.994  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_fulfilled_batch                     avgt    5      7.267 ±    2.209  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_inquire                             avgt    5      7.804 ±    1.125  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_inquire_batch                       avgt    5      7.440 ±    3.992  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_inquired                            avgt    5      7.664 ±    2.039  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_inquired_batch                      avgt    5      9.050 ±    3.489  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_observe                             avgt    5      8.427 ±    1.099  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_observe_batch                       avgt    5      7.974 ±    1.400  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_observed                            avgt    5      8.723 ±    1.042  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_observed_batch                      avgt    5      8.367 ±    2.290  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_offer                               avgt    5      7.373 ±    2.152  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_offer_batch                         avgt    5      7.395 ±    1.659  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_offered                             avgt    5      7.013 ±    2.988  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_offered_batch                       avgt    5      9.936 ±    1.701  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_promise                             avgt    5      7.827 ±    2.847  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_promise_batch                       avgt    5      7.253 ±    4.637  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_promised                            avgt    5      8.044 ±    2.148  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_promised_batch                      avgt    5      7.157 ±    1.970  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_retract                             avgt    5      7.048 ±    2.041  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_retract_batch                       avgt    5      7.267 ±    3.208  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_retracted                           avgt    5      7.737 ±    2.693  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_retracted_batch                     avgt    5      7.200 ±    2.598  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_signal                              avgt    5      8.785 ±    2.573  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_signal_batch                        avgt    5      8.171 ±    1.054  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_validate                            avgt    5      8.973 ±    1.388  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_validate_batch                      avgt    5      7.473 ±    2.386  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_validated                           avgt    5      7.396 ±    2.538  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_validated_batch                     avgt    5      8.636 ±    0.753  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.atomic_from_conduit                     avgt    5      1.876 ±    0.078  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.atomic_from_conduit_batch               avgt    5      1.671 ±    0.015  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_attempt                            avgt    5      7.665 ±    4.309  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_attempt_batch                      avgt    5      7.155 ±    2.849  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_backoff                            avgt    5      7.915 ±    3.634  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_backoff_batch                      avgt    5      6.903 ±    3.270  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_exhaust                            avgt    5      8.058 ±    0.701  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_exhaust_batch                      avgt    5      7.161 ±    3.608  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_fail                               avgt    5      7.547 ±    3.316  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_fail_batch                         avgt    5      6.732 ±    3.435  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_park                               avgt    5      7.668 ±    5.307  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_park_batch                         avgt    5      7.206 ±    3.309  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_sign                               avgt    5      8.033 ±    3.350  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_sign_batch                         avgt    5      8.296 ±    3.762  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_spin                               avgt    5      7.187 ±    2.926  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_spin_batch                         avgt    5      6.923 ±    2.795  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_success                            avgt    5      7.668 ±    5.761  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_success_batch                      avgt    5      7.048 ±    3.865  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_yield                              avgt    5      7.420 ±    2.925  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_yield_batch                        avgt    5      8.312 ±    2.173  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_abandon                             avgt    5      7.632 ±    2.401  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_abandon_batch                       avgt    5      6.893 ±    3.342  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_arrive                              avgt    5      7.753 ±    3.120  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_arrive_batch                        avgt    5      7.318 ±    2.941  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_await                               avgt    5      7.872 ±    3.058  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_await_batch                         avgt    5      6.915 ±    3.147  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_release                             avgt    5      7.455 ±    4.014  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_release_batch                       avgt    5      9.032 ±    3.580  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_reset                               avgt    5      8.839 ±    1.794  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_reset_batch                         avgt    5      6.998 ±    2.590  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_sign                                avgt    5      7.400 ±    2.589  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_sign_batch                          avgt    5      7.227 ±    2.897  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_timeout                             avgt    5      7.823 ±    3.970  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_timeout_batch                       avgt    5      6.968 ±    3.650  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.latch_from_conduit                       avgt    5      1.886 ±    0.068  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.latch_from_conduit_batch                 avgt    5      1.670 ±    0.015  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_abandon                              avgt    5      7.641 ±    3.288  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_abandon_batch                        avgt    5      7.364 ±    3.031  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_acquire                              avgt    5      7.595 ±    4.151  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_acquire_batch                        avgt    5      7.205 ±    5.030  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_attempt                              avgt    5      7.740 ±    3.479  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_attempt_batch                        avgt    5      7.023 ±    4.593  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_contest                              avgt    5      7.912 ±    4.477  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_contest_batch                        avgt    5      6.920 ±    2.843  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_deny                                 avgt    5      9.234 ±    4.953  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_deny_batch                           avgt    5      7.569 ±    5.270  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_downgrade                            avgt    5      7.297 ±    1.474  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_downgrade_batch                      avgt    5      8.092 ±    6.338  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_grant                                avgt    5      7.820 ±    3.895  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_grant_batch                          avgt    5      8.293 ±    1.054  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_release                              avgt    5      8.303 ±    1.234  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_release_batch                        avgt    5      7.204 ±    3.208  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_sign                                 avgt    5      7.596 ±    2.890  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_sign_batch                           avgt    5      7.666 ±    4.921  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_timeout                              avgt    5      7.818 ±    4.048  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_timeout_batch                        avgt    5      7.302 ±    3.168  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_upgrade                              avgt    5      7.730 ±    3.286  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_upgrade_batch                        avgt    5      7.075 ±    2.938  ns/op
i.h.serventis.jmh.opt.sync.LockOps.lock_from_conduit                         avgt    5      1.871 ±    0.056  ns/op
i.h.serventis.jmh.opt.sync.LockOps.lock_from_conduit_batch                   avgt    5      1.670 ±    0.016  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.counter_from_conduit                   avgt    5      1.862 ±    0.016  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.counter_from_conduit_batch             avgt    5      1.670 ±    0.020  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_increment                         avgt    5      7.493 ±    2.525  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_increment_batch                   avgt    5      7.175 ±    3.354  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_overflow                          avgt    5      7.821 ±    3.778  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_overflow_batch                    avgt    5      7.138 ±    4.054  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_reset                             avgt    5      7.426 ±    2.888  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_reset_batch                       avgt    5      7.323 ±    4.340  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_sign                              avgt    5      8.546 ±    0.581  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_sign_batch                        avgt    5      8.348 ±    3.879  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_decrement                           avgt    5      7.568 ±    4.142  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_decrement_batch                     avgt    5      6.892 ±    6.423  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_increment                           avgt    5      7.693 ±    1.572  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_increment_batch                     avgt    5      7.332 ±    3.397  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_overflow                            avgt    5      7.797 ±    5.272  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_overflow_batch                      avgt    5      8.556 ±    1.864  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_reset                               avgt    5      7.830 ±    2.426  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_reset_batch                         avgt    5      7.437 ±    3.321  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_sign                                avgt    5      9.105 ±    3.150  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_sign_batch                          avgt    5      8.383 ±    1.635  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_underflow                           avgt    5      7.798 ±    3.055  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_underflow_batch                     avgt    5      7.747 ±    0.349  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.gauge_from_conduit                       avgt    5      1.894 ±    0.080  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.gauge_from_conduit_batch                 avgt    5      1.669 ±    0.013  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_debug                                 avgt    5      8.440 ±    1.890  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_debug_batch                           avgt    5      7.068 ±    2.426  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_info                                  avgt    5      7.421 ±    2.465  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_info_batch                            avgt    5      6.900 ±    2.176  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_severe                                avgt    5      7.852 ±    2.235  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_severe_batch                          avgt    5      7.245 ±    3.045  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_sign                                  avgt    5      7.768 ±    2.434  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_sign_batch                            avgt    5      8.396 ±    0.687  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_warning                               avgt    5      7.506 ±    2.025  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_warning_batch                         avgt    5      7.719 ±    3.808  ns/op
i.h.serventis.jmh.opt.tool.LogOps.log_from_conduit                           avgt    5      1.867 ±    0.027  ns/op
i.h.serventis.jmh.opt.tool.LogOps.log_from_conduit_batch                     avgt    5      1.668 ±    0.010  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_connect                             avgt    5      7.530 ±    4.606  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_connect_batch                       avgt    5      8.895 ±    3.207  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_connected                           avgt    5      8.457 ±    1.704  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_connected_batch                     avgt    5      7.569 ±    4.435  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_disconnect                          avgt    5      7.345 ±    1.800  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_disconnect_batch                    avgt    5      9.056 ±    2.847  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_disconnected                        avgt    5      7.886 ±    3.914  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_disconnected_batch                  avgt    5      7.198 ±    2.381  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_fail                                avgt    5      8.426 ±    1.960  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_fail_batch                          avgt    5      7.505 ±    3.192  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_failed                              avgt    5      7.459 ±    1.925  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_failed_batch                        avgt    5      7.214 ±    2.248  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_process                             avgt    5      8.540 ±    3.546  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_process_batch                       avgt    5      9.344 ±    4.556  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_processed                           avgt    5      7.817 ±    1.866  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_processed_batch                     avgt    5      7.932 ±    4.733  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_receive_batch                       avgt    5      8.831 ±    3.109  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_received_batch                      avgt    5      7.197 ±    3.609  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_signal                              avgt    5      8.597 ±    0.642  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_signal_batch                        avgt    5      7.139 ±    2.594  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_succeed                             avgt    5      9.375 ±    4.454  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_succeed_batch                       avgt    5      8.553 ±    0.938  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_succeeded                           avgt    5      7.530 ±    3.084  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_succeeded_batch                     avgt    5      8.249 ±    2.425  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transfer                            avgt    5      8.053 ±    2.418  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transfer_inbound                    avgt    5      8.498 ±    1.240  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transfer_outbound                   avgt    5      8.233 ±    0.669  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transferred                         avgt    5      7.539 ±    2.025  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transmit_batch                      avgt    5      7.549 ±    3.343  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transmitted_batch                   avgt    5      7.127 ±    2.994  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.probe_from_conduit                       avgt    5      1.884 ±    0.050  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.probe_from_conduit_batch                 avgt    5      1.670 ±    0.018  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_baseline                     avgt    5      7.426 ±    2.687  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_baseline_batch               avgt    5      7.050 ±    2.035  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_target                       avgt    5      7.951 ±    4.553  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_target_batch                 avgt    5      8.431 ±    2.277  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_threshold                    avgt    5      7.782 ±    2.694  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_threshold_batch              avgt    5      7.298 ±    4.248  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_baseline                     avgt    5      7.479 ±    2.965  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_baseline_batch               avgt    5      7.570 ±    3.618  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_target                       avgt    5      7.625 ±    3.032  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_target_batch                 avgt    5      7.400 ±    2.600  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_threshold                    avgt    5      7.899 ±    2.562  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_threshold_batch              avgt    5      7.793 ±    3.406  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_baseline                   avgt    5      8.350 ±    0.918  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_baseline_batch             avgt    5      7.272 ±    2.920  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_target                     avgt    5      8.825 ±    0.763  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_target_batch               avgt    5      8.161 ±    1.661  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_threshold                  avgt    5      8.149 ±    3.701  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_threshold_batch            avgt    5      7.299 ±    2.933  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_signal                             avgt    5      8.311 ±    0.655  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_signal_batch                       avgt    5      7.854 ±    0.913  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.sensor_from_conduit                     avgt    5      1.881 ±    0.072  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.sensor_from_conduit_batch               avgt    5      1.669 ±    0.011  ns/op
i.h.serventis.jmh.sdk.OperationOps.emit_begin                                avgt    5      7.381 ±    3.098  ns/op
i.h.serventis.jmh.sdk.OperationOps.emit_begin_batch                          avgt    5      8.901 ±    2.255  ns/op
i.h.serventis.jmh.sdk.OperationOps.emit_end                                  avgt    5      7.669 ±    2.541  ns/op
i.h.serventis.jmh.sdk.OperationOps.emit_end_batch                            avgt    5      6.998 ±    3.493  ns/op
i.h.serventis.jmh.sdk.OperationOps.emit_sign                                 avgt    5      8.231 ±    0.655  ns/op
i.h.serventis.jmh.sdk.OperationOps.emit_sign_batch                           avgt    5      7.283 ±    2.568  ns/op
i.h.serventis.jmh.sdk.OperationOps.operation_from_conduit                    avgt    5      1.868 ±    0.068  ns/op
i.h.serventis.jmh.sdk.OperationOps.operation_from_conduit_batch              avgt    5      1.669 ±    0.014  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.emit_fail                                   avgt    5      7.694 ±    2.433  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.emit_fail_batch                             avgt    5      7.718 ±    3.748  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.emit_sign                                   avgt    5      7.452 ±    2.956  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.emit_sign_batch                             avgt    5      8.232 ±    3.188  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.emit_success                                avgt    5      8.028 ±    4.786  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.emit_success_batch                          avgt    5      7.173 ±    2.477  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.outcome_from_conduit                        avgt    5      1.884 ±    0.071  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.outcome_from_conduit_batch                  avgt    5      1.671 ±    0.014  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_mixed_pattern                         avgt    5      0.227 ±    0.003  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_single                                avgt    5      0.755 ±    0.008  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_single_batch                          avgt    5      0.020 ±    0.001  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_varied_batch                          avgt    5      1.522 ±    0.012  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_worst_case                            avgt    5      1.190 ±    0.006  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_critical                             avgt    5      8.706 ±    1.295  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_critical_batch                       avgt    5      7.749 ±    3.021  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_normal                               avgt    5      8.770 ±    1.670  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_normal_batch                         avgt    5      7.062 ±    2.094  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_signal                               avgt    5      7.526 ±    2.210  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_signal_batch                         avgt    5      8.825 ±    1.176  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_warning                              avgt    5      8.291 ±    5.363  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_warning_batch                        avgt    5      7.923 ±    1.961  ns/op
i.h.serventis.jmh.sdk.SituationOps.situation_from_conduit                    avgt    5      1.886 ±    0.065  ns/op
i.h.serventis.jmh.sdk.SituationOps.situation_from_conduit_batch              avgt    5      1.671 ±    0.018  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_converging_confirmed                    avgt    5      8.053 ±    4.362  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_converging_confirmed_batch              avgt    5      8.731 ±    1.848  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_defective_tentative                     avgt    5      8.432 ±    0.719  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_defective_tentative_batch               avgt    5      7.620 ±    2.937  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_degraded_measured                       avgt    5      7.780 ±    3.316  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_degraded_measured_batch                 avgt    5      7.590 ±    3.796  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_down_confirmed                          avgt    5      8.601 ±    1.253  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_down_confirmed_batch                    avgt    5      7.235 ±    1.391  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_signal                                  avgt    5      7.357 ±    2.702  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_signal_batch                            avgt    5      7.052 ±    3.439  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_stable_confirmed                        avgt    5      6.984 ±    2.541  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_stable_confirmed_batch                  avgt    5      7.277 ±    1.879  ns/op
i.h.serventis.jmh.sdk.StatusOps.status_from_conduit                          avgt    5      1.864 ±    0.025  ns/op
i.h.serventis.jmh.sdk.StatusOps.status_from_conduit_batch                    avgt    5      1.670 ±    0.017  ns/op
i.h.serventis.jmh.sdk.SurveyOps.emit_divided                                 avgt    5      6.069 ±    0.930  ns/op
i.h.serventis.jmh.sdk.SurveyOps.emit_divided_batch                           avgt    5      6.358 ±    1.139  ns/op
i.h.serventis.jmh.sdk.SurveyOps.emit_majority                                avgt    5      6.449 ±    0.734  ns/op
i.h.serventis.jmh.sdk.SurveyOps.emit_majority_batch                          avgt    5      8.131 ±    1.506  ns/op
i.h.serventis.jmh.sdk.SurveyOps.emit_signal                                  avgt    5      7.051 ±    0.951  ns/op
i.h.serventis.jmh.sdk.SurveyOps.emit_signal_batch                            avgt    5      7.045 ±    2.437  ns/op
i.h.serventis.jmh.sdk.SurveyOps.emit_unanimous                               avgt    5      8.371 ±    0.478  ns/op
i.h.serventis.jmh.sdk.SurveyOps.emit_unanimous_batch                         avgt    5      6.731 ±    0.940  ns/op
i.h.serventis.jmh.sdk.SurveyOps.survey_from_conduit                          avgt    5      1.862 ±    0.022  ns/op
i.h.serventis.jmh.sdk.SurveyOps.survey_from_conduit_batch                    avgt    5      1.669 ±    0.013  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_alarm_flow                              avgt    5      7.449 ±    2.032  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_alarm_flow_batch                        avgt    5      7.799 ±    0.693  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_fault_link                              avgt    5      8.079 ±    2.745  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_fault_link_batch                        avgt    5      8.401 ±    1.844  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_limit_time                              avgt    5      8.221 ±    1.717  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_limit_time_batch                        avgt    5      8.860 ±    3.863  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_normal_space                            avgt    5      7.223 ±    1.759  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_normal_space_batch                      avgt    5      8.352 ±    1.905  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_signal                                  avgt    5      8.123 ±    3.065  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_signal_batch                            avgt    5      7.103 ±    3.360  ns/op
i.h.serventis.jmh.sdk.SystemOps.system_from_conduit                          avgt    5      1.878 ±    0.073  ns/op
i.h.serventis.jmh.sdk.SystemOps.system_from_conduit_batch                    avgt    5      1.670 ±    0.029  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_chaos                                    avgt    5      8.344 ±    0.810  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_chaos_batch                              avgt    5      4.908 ±    4.161  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_cycle                                    avgt    5      7.941 ±    2.103  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_cycle_batch                              avgt    5      7.887 ±    0.744  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_drift                                    avgt    5      8.854 ±    1.892  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_drift_batch                              avgt    5      7.128 ±    3.044  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_sign                                     avgt    5      8.096 ±    3.945  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_sign_batch                               avgt    5      8.056 ±    1.515  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_spike                                    avgt    5      7.497 ±    4.955  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_spike_batch                              avgt    5      8.437 ±    1.421  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_stable                                   avgt    5      7.545 ±    2.537  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_stable_batch                             avgt    5      6.658 ±    7.044  ns/op
i.h.serventis.jmh.sdk.TrendOps.trend_from_conduit                            avgt    5      1.863 ±    0.030  ns/op
i.h.serventis.jmh.sdk.TrendOps.trend_from_conduit_batch                      avgt    5      1.669 ±    0.011  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.cycle_from_conduit                       avgt    5      1.878 ±    0.051  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.cycle_from_conduit_batch                 avgt    5      1.669 ±    0.018  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_repeat                              avgt    5      8.432 ±    0.345  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_repeat_batch                        avgt    5      6.516 ±    0.804  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_return                              avgt    5      7.825 ±    0.420  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_return_batch                        avgt    5      7.952 ±    1.348  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_signal                              avgt    5      7.339 ±    5.631  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_signal_batch                        avgt    5      6.827 ±    1.105  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_single                              avgt    5      6.462 ±    0.735  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_single_batch                        avgt    5      8.701 ±    1.832  ns/op
i.h.substrates.jmh.CircuitOps.conduit_create_close                           avgt    5    287.893 ±  135.044  ns/op
i.h.substrates.jmh.CircuitOps.conduit_create_named                           avgt    5    305.190 ±  191.197  ns/op
i.h.substrates.jmh.CircuitOps.conduit_create_with_flow                       avgt    5    269.213 ±   71.551  ns/op
i.h.substrates.jmh.CircuitOps.create_and_close                               avgt    5    603.844 ± 1564.697  ns/op
i.h.substrates.jmh.CircuitOps.create_and_close_batch                         avgt    5    350.970 ±  587.161  ns/op
i.h.substrates.jmh.CircuitOps.create_multiple_and_close                      avgt    5   2165.894 ± 4425.770  ns/op
i.h.substrates.jmh.CircuitOps.create_named_and_close                         avgt    5    447.401 ± 1026.293  ns/op
i.h.substrates.jmh.CircuitOps.hot_conduit_create                             avgt    5     19.176 ±    0.134  ns/op
i.h.substrates.jmh.CircuitOps.hot_conduit_create_named                       avgt    5     19.156 ±    0.126  ns/op
i.h.substrates.jmh.CircuitOps.hot_conduit_create_with_flow                   avgt    5     22.028 ±    0.114  ns/op
i.h.substrates.jmh.CircuitOps.hot_pipe_async                                 avgt    5      7.984 ±    0.863  ns/op
i.h.substrates.jmh.CircuitOps.hot_pipe_async_with_flow                       avgt    5      9.684 ±    0.234  ns/op
i.h.substrates.jmh.CircuitOps.pipe_async                                     avgt    5    305.778 ±  187.748  ns/op
i.h.substrates.jmh.CircuitOps.pipe_async_with_flow                           avgt    5    320.321 ±  309.339  ns/op
i.h.substrates.jmh.ConduitOps.get_by_name                                    avgt    5      1.889 ±    0.058  ns/op
i.h.substrates.jmh.ConduitOps.get_by_name_batch                              avgt    5      1.665 ±    0.007  ns/op
i.h.substrates.jmh.ConduitOps.get_by_substrate                               avgt    5      2.053 ±    0.033  ns/op
i.h.substrates.jmh.ConduitOps.get_by_substrate_batch                         avgt    5      1.819 ±    0.010  ns/op
i.h.substrates.jmh.ConduitOps.get_cached                                     avgt    5      3.443 ±    0.112  ns/op
i.h.substrates.jmh.ConduitOps.get_cached_batch                               avgt    5      3.322 ±    0.019  ns/op
i.h.substrates.jmh.ConduitOps.subscribe                                      avgt    5    472.597 ±  300.115  ns/op
i.h.substrates.jmh.ConduitOps.subscribe_batch                                avgt    5    502.543 ±  710.033  ns/op
i.h.substrates.jmh.ConduitOps.subscribe_with_emission_await                  avgt    5   5774.628 ±  610.436  ns/op
i.h.substrates.jmh.CortexOps.circuit                                         avgt    5    277.612 ±  239.538  ns/op
i.h.substrates.jmh.CortexOps.circuit_batch                                   avgt    5    266.403 ±   77.518  ns/op
i.h.substrates.jmh.CortexOps.circuit_named                                   avgt    5    283.948 ±  114.553  ns/op
i.h.substrates.jmh.CortexOps.current                                         avgt    5      1.077 ±    0.020  ns/op
i.h.substrates.jmh.CortexOps.name_class                                      avgt    5      1.485 ±    0.027  ns/op
i.h.substrates.jmh.CortexOps.name_enum                                       avgt    5      1.768 ±    0.012  ns/op
i.h.substrates.jmh.CortexOps.name_iterable                                   avgt    5      7.914 ±    0.066  ns/op
i.h.substrates.jmh.CortexOps.name_path                                       avgt    5      1.902 ±    0.011  ns/op
i.h.substrates.jmh.CortexOps.name_path_batch                                 avgt    5      1.681 ±    0.015  ns/op
i.h.substrates.jmh.CortexOps.name_string                                     avgt    5      2.696 ±    0.429  ns/op
i.h.substrates.jmh.CortexOps.name_string_batch                               avgt    5      2.524 ±    0.340  ns/op
i.h.substrates.jmh.CortexOps.scope                                           avgt    5      8.494 ±    0.971  ns/op
i.h.substrates.jmh.CortexOps.scope_batch                                     avgt    5      7.537 ±    0.074  ns/op
i.h.substrates.jmh.CortexOps.scope_named                                     avgt    5      8.023 ±    0.058  ns/op
i.h.substrates.jmh.CortexOps.slot_boolean                                    avgt    5      2.048 ±    0.360  ns/op
i.h.substrates.jmh.CortexOps.slot_double                                     avgt    5      2.055 ±    0.500  ns/op
i.h.substrates.jmh.CortexOps.slot_int                                        avgt    5      2.070 ±    0.455  ns/op
i.h.substrates.jmh.CortexOps.slot_long                                       avgt    5      2.030 ±    0.336  ns/op
i.h.substrates.jmh.CortexOps.slot_string                                     avgt    5      1.983 ±    0.475  ns/op
i.h.substrates.jmh.CortexOps.state_empty                                     avgt    5      0.467 ±    0.111  ns/op
i.h.substrates.jmh.CortexOps.state_empty_batch                               avgt    5     ≈ 10⁻³             ns/op
i.h.substrates.jmh.CyclicOps.cyclic_emit                                     avgt    5      1.192 ±    0.671  ns/op
i.h.substrates.jmh.CyclicOps.cyclic_emit_await                               avgt    5     10.371 ±    0.186  ns/op
i.h.substrates.jmh.CyclicOps.cyclic_emit_deep_await                          avgt    5      4.154 ±    0.097  ns/op
i.h.substrates.jmh.FlowOps.baseline_no_flow_await                            avgt    5     17.759 ±    0.708  ns/op
i.h.substrates.jmh.FlowOps.flow_combined_diff_guard_await                    avgt    5     26.091 ±    0.091  ns/op
i.h.substrates.jmh.FlowOps.flow_combined_diff_sample_await                   avgt    5     18.579 ±    1.352  ns/op
i.h.substrates.jmh.FlowOps.flow_combined_guard_limit_await                   avgt    5     25.955 ±    0.786  ns/op
i.h.substrates.jmh.FlowOps.flow_diff_await                                   avgt    5     27.743 ±    1.735  ns/op
i.h.substrates.jmh.FlowOps.flow_guard_await                                  avgt    5     26.823 ±    2.938  ns/op
i.h.substrates.jmh.FlowOps.flow_limit_await                                  avgt    5     24.081 ±    2.050  ns/op
i.h.substrates.jmh.FlowOps.flow_sample_await                                 avgt    5     17.445 ±    0.756  ns/op
i.h.substrates.jmh.FlowOps.flow_sift_await                                   avgt    5     17.957 ±    0.941  ns/op
i.h.substrates.jmh.IdOps.id_from_subject                                     avgt    5      0.534 ±    0.069  ns/op
i.h.substrates.jmh.IdOps.id_from_subject_batch                               avgt    5      0.001 ±    0.001  ns/op
i.h.substrates.jmh.IdOps.id_toString                                         avgt    5     12.190 ±    0.541  ns/op
i.h.substrates.jmh.IdOps.id_toString_batch                                   avgt    5     13.334 ±    0.552  ns/op
i.h.substrates.jmh.NameOps.name_chained_deep                                 avgt    5      5.264 ±    0.038  ns/op
i.h.substrates.jmh.NameOps.name_chaining                                     avgt    5      8.501 ±    0.441  ns/op
i.h.substrates.jmh.NameOps.name_chaining_batch                               avgt    5      7.726 ±    0.887  ns/op
i.h.substrates.jmh.NameOps.name_compare                                      avgt    5      0.771 ±    0.021  ns/op
i.h.substrates.jmh.NameOps.name_compare_batch                                avgt    5      0.001 ±    0.001  ns/op
i.h.substrates.jmh.NameOps.name_depth                                        avgt    5      0.518 ±    0.076  ns/op
i.h.substrates.jmh.NameOps.name_depth_batch                                  avgt    5      0.001 ±    0.001  ns/op
i.h.substrates.jmh.NameOps.name_enclosure                                    avgt    5      0.553 ±    0.064  ns/op
i.h.substrates.jmh.NameOps.name_from_enum                                    avgt    5      1.767 ±    0.013  ns/op
i.h.substrates.jmh.NameOps.name_from_iterable                                avgt    5      8.917 ±    0.071  ns/op
i.h.substrates.jmh.NameOps.name_from_iterator                                avgt    5      8.756 ±    0.041  ns/op
i.h.substrates.jmh.NameOps.name_from_mapped_iterable                         avgt    5      9.251 ±    0.077  ns/op
i.h.substrates.jmh.NameOps.name_from_name                                    avgt    5      3.594 ±    0.146  ns/op
i.h.substrates.jmh.NameOps.name_from_string                                  avgt    5      2.968 ±    0.544  ns/op
i.h.substrates.jmh.NameOps.name_from_string_batch                            avgt    5      2.873 ±    0.018  ns/op
i.h.substrates.jmh.NameOps.name_interning_chained                            avgt    5      9.996 ±    0.100  ns/op
i.h.substrates.jmh.NameOps.name_interning_same_path                          avgt    5      3.561 ±    0.030  ns/op
i.h.substrates.jmh.NameOps.name_interning_segments                           avgt    5      8.919 ±    1.160  ns/op
i.h.substrates.jmh.NameOps.name_iterate_hierarchy                            avgt    5      1.674 ±    0.025  ns/op
i.h.substrates.jmh.NameOps.name_parsing                                      avgt    5      1.902 ±    0.019  ns/op
i.h.substrates.jmh.NameOps.name_parsing_batch                                avgt    5      1.687 ±    0.020  ns/op
i.h.substrates.jmh.NameOps.name_path_generation                              avgt    5      0.553 ±    0.065  ns/op
i.h.substrates.jmh.NameOps.name_path_generation_batch                        avgt    5      0.001 ±    0.001  ns/op
i.h.substrates.jmh.PipeOps.async_emit_batch                                  avgt    5     10.157 ±    1.707  ns/op
i.h.substrates.jmh.PipeOps.async_emit_batch_await                            avgt    5     18.932 ±    0.772  ns/op
i.h.substrates.jmh.PipeOps.async_emit_chained_await                          avgt    5     18.021 ±    0.983  ns/op
i.h.substrates.jmh.PipeOps.async_emit_fanout_await                           avgt    5     19.575 ±    1.171  ns/op
i.h.substrates.jmh.PipeOps.async_emit_single                                 avgt    5      7.715 ±    2.475  ns/op
i.h.substrates.jmh.PipeOps.async_emit_single_await                           avgt    5   6613.322 ± 1314.108  ns/op
i.h.substrates.jmh.PipeOps.async_emit_with_flow_await                        avgt    5     19.977 ±    1.856  ns/op
i.h.substrates.jmh.PipeOps.baseline_blackhole                                avgt    5      0.270 ±    0.002  ns/op
i.h.substrates.jmh.PipeOps.baseline_counter                                  avgt    5      1.692 ±    0.039  ns/op
i.h.substrates.jmh.PipeOps.baseline_receptor                                 avgt    5      0.269 ±    0.002  ns/op
i.h.substrates.jmh.PipeOps.pipe_create                                       avgt    5      7.755 ±    0.173  ns/op
i.h.substrates.jmh.PipeOps.pipe_create_chained                               avgt    5      0.863 ±    0.107  ns/op
i.h.substrates.jmh.PipeOps.pipe_create_with_flow                             avgt    5     12.458 ±    0.139  ns/op
i.h.substrates.jmh.ReservoirOps.baseline_emit_no_reservoir_await             avgt    5     96.159 ±    9.090  ns/op
i.h.substrates.jmh.ReservoirOps.baseline_emit_no_reservoir_await_batch       avgt    5     17.238 ±    1.836  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_burst_then_drain_await             avgt    5     90.199 ±    9.159  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_burst_then_drain_await_batch       avgt    5     26.413 ±    0.417  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_drain_await                        avgt    5     89.609 ±   11.236  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_drain_await_batch                  avgt    5     25.500 ±    3.576  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_emit_drain_cycles_await            avgt    5    334.051 ±   45.980  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_emit_with_capture_await            avgt    5     82.316 ±   21.383  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_emit_with_capture_await_batch      avgt    5     18.509 ±    1.956  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_process_emissions_await            avgt    5     70.710 ±    8.263  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_process_emissions_await_batch      avgt    5     21.567 ±    1.181  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_process_subjects_await             avgt    5     73.344 ±    7.187  ns/op
i.h.substrates.jmh.ScopeOps.scope_child_anonymous                            avgt    5     17.338 ±    0.277  ns/op
i.h.substrates.jmh.ScopeOps.scope_child_anonymous_batch                      avgt    5     17.386 ±    0.284  ns/op
i.h.substrates.jmh.ScopeOps.scope_child_named                                avgt    5     17.053 ±    0.084  ns/op
i.h.substrates.jmh.ScopeOps.scope_child_named_batch                          avgt    5     17.033 ±    0.250  ns/op
i.h.substrates.jmh.ScopeOps.scope_close_idempotent                           avgt    5      2.498 ±    0.084  ns/op
i.h.substrates.jmh.ScopeOps.scope_close_idempotent_batch                     avgt    5      0.035 ±    0.001  ns/op
i.h.substrates.jmh.ScopeOps.scope_closure                                    avgt    5    280.979 ±  117.176  ns/op
i.h.substrates.jmh.ScopeOps.scope_closure_batch                              avgt    5    299.300 ±   44.783  ns/op
i.h.substrates.jmh.ScopeOps.scope_complex                                    avgt    5    897.603 ±  155.991  ns/op
i.h.substrates.jmh.ScopeOps.scope_create_and_close                           avgt    5      2.514 ±    0.060  ns/op
i.h.substrates.jmh.ScopeOps.scope_create_and_close_batch                     avgt    5      0.035 ±    0.001  ns/op
i.h.substrates.jmh.ScopeOps.scope_create_named                               avgt    5      2.516 ±    0.036  ns/op
i.h.substrates.jmh.ScopeOps.scope_create_named_batch                         avgt    5      0.035 ±    0.001  ns/op
i.h.substrates.jmh.ScopeOps.scope_hierarchy                                  avgt    5     27.306 ±    0.336  ns/op
i.h.substrates.jmh.ScopeOps.scope_hierarchy_batch                            avgt    5     26.676 ±    0.208  ns/op
i.h.substrates.jmh.ScopeOps.scope_parent_closes_children                     avgt    5     43.156 ±    0.446  ns/op
i.h.substrates.jmh.ScopeOps.scope_parent_closes_children_batch               avgt    5     42.428 ±    4.607  ns/op
i.h.substrates.jmh.ScopeOps.scope_register_multiple                          avgt    5   1412.671 ±  252.358  ns/op
i.h.substrates.jmh.ScopeOps.scope_register_multiple_batch                    avgt    5   1479.356 ±  287.257  ns/op
i.h.substrates.jmh.ScopeOps.scope_register_single                            avgt    5    301.056 ±   46.929  ns/op
i.h.substrates.jmh.ScopeOps.scope_register_single_batch                      avgt    5    290.439 ±   61.094  ns/op
i.h.substrates.jmh.ScopeOps.scope_with_resources                             avgt    5    597.582 ±   26.572  ns/op
i.h.substrates.jmh.StateOps.slot_name                                        avgt    5      0.534 ±    0.066  ns/op
i.h.substrates.jmh.StateOps.slot_name_batch                                  avgt    5      0.001 ±    0.001  ns/op
i.h.substrates.jmh.StateOps.slot_type                                        avgt    5      0.447 ±    0.006  ns/op
i.h.substrates.jmh.StateOps.slot_value                                       avgt    5      0.606 ±    0.005  ns/op
i.h.substrates.jmh.StateOps.slot_value_batch                                 avgt    5      0.001 ±    0.001  ns/op
i.h.substrates.jmh.StateOps.state_compact                                    avgt    5     10.244 ±    0.181  ns/op
i.h.substrates.jmh.StateOps.state_compact_batch                              avgt    5     10.912 ±    0.158  ns/op
i.h.substrates.jmh.StateOps.state_iterate_slots                              avgt    5      2.186 ±    0.042  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_int                               avgt    5      3.944 ±    0.982  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_int_batch                         avgt    5      4.022 ±    0.754  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_long                              avgt    5      4.076 ±    1.010  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_object                            avgt    5      2.468 ±    0.107  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_object_batch                      avgt    5      2.400 ±    0.012  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_string                            avgt    5      3.865 ±    0.832  ns/op
i.h.substrates.jmh.StateOps.state_value_read                                 avgt    5      1.494 ±    0.012  ns/op
i.h.substrates.jmh.StateOps.state_value_read_batch                           avgt    5      1.272 ±    0.009  ns/op
i.h.substrates.jmh.StateOps.state_values_stream                              avgt    5      4.021 ±    0.542  ns/op
i.h.substrates.jmh.SubjectOps.subject_compare                                avgt    5      3.931 ±    0.029  ns/op
i.h.substrates.jmh.SubjectOps.subject_compare_batch                          avgt    5      2.498 ±    0.021  ns/op
i.h.substrates.jmh.SubjectOps.subject_compare_same                           avgt    5      0.446 ±    0.006  ns/op
i.h.substrates.jmh.SubjectOps.subject_compare_same_batch                     avgt    5     ≈ 10⁻³             ns/op
i.h.substrates.jmh.SubjectOps.subject_compare_three_way                      avgt    5     11.101 ±    0.084  ns/op
i.h.substrates.jmh.SubscriberOps.close_five_conduits_await                   avgt    5   8645.387 ±  925.796  ns/op
i.h.substrates.jmh.SubscriberOps.close_five_subscriptions_await              avgt    5   8833.760 ±  770.463  ns/op
i.h.substrates.jmh.SubscriberOps.close_idempotent_await                      avgt    5   8695.030 ±  800.199  ns/op
i.h.substrates.jmh.SubscriberOps.close_idempotent_batch_await                avgt    5     17.404 ±    0.934  ns/op
i.h.substrates.jmh.SubscriberOps.close_no_subscriptions_await                avgt    5   8605.474 ± 1125.280  ns/op
i.h.substrates.jmh.SubscriberOps.close_no_subscriptions_batch_await          avgt    5     13.973 ±    1.355  ns/op
i.h.substrates.jmh.SubscriberOps.close_one_subscription_await                avgt    5   8468.347 ±  371.592  ns/op
i.h.substrates.jmh.SubscriberOps.close_one_subscription_batch_await          avgt    5     32.218 ±    0.343  ns/op
i.h.substrates.jmh.SubscriberOps.close_ten_conduits_await                    avgt    5   8827.119 ±  731.580  ns/op
i.h.substrates.jmh.SubscriberOps.close_ten_subscriptions_await               avgt    5   8614.266 ±  638.754  ns/op
i.h.substrates.jmh.SubscriberOps.close_with_pending_emissions_await          avgt    5   8883.411 ±  519.082  ns/op
i.h.substrates.jmh.TapOps.baseline_emit_batch_await                          avgt    5     19.687 ±    1.101  ns/op
i.h.substrates.jmh.TapOps.tap_close                                          avgt    5   7266.077 ±  591.976  ns/op
i.h.substrates.jmh.TapOps.tap_create_batch                                   avgt    5    565.587 ±  600.358  ns/op
i.h.substrates.jmh.TapOps.tap_create_identity                                avgt    5    514.879 ±  490.539  ns/op
i.h.substrates.jmh.TapOps.tap_create_string                                  avgt    5    597.193 ±  752.303  ns/op
i.h.substrates.jmh.TapOps.tap_emit_identity_batch_await                      avgt    5     27.892 ±    3.937  ns/op
i.h.substrates.jmh.TapOps.tap_emit_identity_single                           avgt    5     34.580 ±   27.621  ns/op
i.h.substrates.jmh.TapOps.tap_emit_identity_single_await                     avgt    5   5664.201 ±  669.013  ns/op
i.h.substrates.jmh.TapOps.tap_emit_multi_batch_await                         avgt    5     42.209 ±    3.075  ns/op
i.h.substrates.jmh.TapOps.tap_emit_string_batch_await                        avgt    5     35.152 ±    6.401  ns/op
i.h.substrates.jmh.TapOps.tap_lifecycle                                      avgt    5  16801.406 ± 1233.744  ns/op
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