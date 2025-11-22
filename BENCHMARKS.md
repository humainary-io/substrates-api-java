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

Benchmark                                                                    Mode  Cnt      Score     Error  Units
i.h.serventis.jmh.opt.data.CacheOps.cache_from_conduit                       avgt    5      1.746 ±   0.065  ns/op
i.h.serventis.jmh.opt.data.CacheOps.cache_from_conduit_batch                 avgt    5      1.583 ±   0.015  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_evict                               avgt    5      9.532 ±   1.129  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_evict_batch                         avgt    5      9.764 ±   2.093  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_expire                              avgt    5      8.896 ±   2.810  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_expire_batch                        avgt    5     10.033 ±   0.852  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_hit                                 avgt    5      9.713 ±   0.640  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_hit_batch                           avgt    5      9.485 ±   0.777  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_lookup                              avgt    5      9.572 ±   1.099  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_lookup_batch                        avgt    5      8.442 ±   4.280  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_miss                                avgt    5      9.594 ±   0.376  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_miss_batch                          avgt    5      9.342 ±   2.595  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_remove                              avgt    5      9.575 ±   0.738  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_remove_batch                        avgt    5      9.499 ±   2.599  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_sign                                avgt    5      8.585 ±   3.474  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_sign_batch                          avgt    5      9.346 ±   0.913  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_store                               avgt    5      8.314 ±   3.136  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_store_batch                         avgt    5      9.375 ±   0.268  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_aggregate                        avgt    5      8.825 ±   3.568  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_aggregate_batch                  avgt    5      8.480 ±   1.862  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_backpressure                     avgt    5      8.405 ±   3.256  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_backpressure_batch               avgt    5     10.408 ±   0.920  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_buffer                           avgt    5      8.785 ±   0.494  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_buffer_batch                     avgt    5      9.760 ±   2.931  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_checkpoint                       avgt    5      9.456 ±   1.064  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_checkpoint_batch                 avgt    5      8.800 ±   2.984  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_filter                           avgt    5      9.565 ±   0.604  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_filter_batch                     avgt    5     10.021 ±   0.841  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_input                            avgt    5      8.708 ±   3.705  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_input_batch                      avgt    5      9.952 ±   0.726  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_lag                              avgt    5      9.168 ±   0.680  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_lag_batch                        avgt    5      9.990 ±   0.495  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_output                           avgt    5     10.291 ±   1.181  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_output_batch                     avgt    5      9.464 ±   0.528  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_overflow                         avgt    5      9.476 ±   0.752  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_overflow_batch                   avgt    5      8.316 ±   3.208  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_sign                             avgt    5      9.063 ±   1.250  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_sign_batch                       avgt    5      9.207 ±   0.571  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_skip                             avgt    5      8.769 ±   1.003  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_skip_batch                       avgt    5      8.377 ±   3.040  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_transform                        avgt    5      9.240 ±   1.224  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_transform_batch                  avgt    5      8.850 ±   3.098  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_watermark                        avgt    5      9.383 ±   0.664  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_watermark_batch                  avgt    5      9.570 ±   2.660  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_flow_etl                     avgt    5     48.151 ±   3.150  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_flow_stream                  avgt    5     42.920 ±  21.584  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_flow_windowed                avgt    5     46.108 ±   3.254  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_from_conduit                 avgt    5      1.850 ±   0.027  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_from_conduit_batch           avgt    5      1.661 ±   0.004  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_dequeue                             avgt    5      8.551 ±   3.605  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_dequeue_batch                       avgt    5     10.008 ±   0.140  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_enqueue                             avgt    5      9.367 ±   0.681  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_enqueue_batch                       avgt    5      9.553 ±   2.613  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_overflow                            avgt    5      8.496 ±   2.710  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_overflow_batch                      avgt    5     10.180 ±   0.677  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_sign                                avgt    5      9.206 ±   0.985  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_sign_batch                          avgt    5      9.174 ±   2.018  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_underflow                           avgt    5      9.792 ±   2.680  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_underflow_batch                     avgt    5      8.822 ±   2.444  ns/op
i.h.serventis.jmh.opt.data.QueueOps.queue_from_conduit                       avgt    5      1.851 ±   0.041  ns/op
i.h.serventis.jmh.opt.data.QueueOps.queue_from_conduit_batch                 avgt    5      1.662 ±   0.011  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_overflow                            avgt    5      9.682 ±   0.608  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_overflow_batch                      avgt    5     10.015 ±   1.249  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_pop                                 avgt    5      8.760 ±   2.198  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_pop_batch                           avgt    5      9.966 ±   0.931  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_push                                avgt    5      8.576 ±   3.114  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_push_batch                          avgt    5      9.186 ±   2.978  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_sign                                avgt    5      9.437 ±   0.383  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_sign_batch                          avgt    5      8.735 ±   2.964  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_underflow                           avgt    5      9.335 ±   0.740  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_underflow_batch                     avgt    5      8.467 ±   2.142  ns/op
i.h.serventis.jmh.opt.data.StackOps.stack_from_conduit                       avgt    5      1.853 ±   0.026  ns/op
i.h.serventis.jmh.opt.data.StackOps.stack_from_conduit_batch                 avgt    5      1.660 ±   0.001  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_crash                             avgt    5      9.245 ±   1.166  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_crash_batch                       avgt    5     10.059 ±   0.348  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_fail                              avgt    5      8.238 ±   3.370  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_fail_batch                        avgt    5      9.854 ±   0.802  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_kill                              avgt    5      8.542 ±   1.916  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_kill_batch                        avgt    5      9.543 ±   2.775  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_restart                           avgt    5      9.557 ±   0.800  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_restart_batch                     avgt    5      9.288 ±   2.112  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_resume                            avgt    5      8.458 ±   3.022  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_resume_batch                      avgt    5      9.938 ±   0.637  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_sign                              avgt    5      8.202 ±   3.939  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_sign_batch                        avgt    5      9.111 ±   2.861  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_spawn                             avgt    5      8.977 ±   0.322  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_spawn_batch                       avgt    5      9.130 ±   1.893  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_start                             avgt    5      8.106 ±   3.877  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_start_batch                       avgt    5      9.411 ±   0.538  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_stop                              avgt    5      9.314 ±   0.831  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_stop_batch                        avgt    5     10.125 ±   0.941  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_suspend                           avgt    5      7.945 ±   2.792  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_suspend_batch                     avgt    5     10.248 ±   0.879  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.process_from_conduit                   avgt    5      1.854 ±   0.044  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.process_from_conduit_batch             avgt    5      1.661 ±   0.007  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_call                              avgt    5      8.648 ±   2.944  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_call_batch                        avgt    5     11.409 ±   1.165  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_called                            avgt    5      9.719 ±   1.120  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_called_batch                      avgt    5      9.653 ±   0.718  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_delay                             avgt    5      9.708 ±   0.805  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_delay_batch                       avgt    5      9.904 ±   2.288  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_delayed                           avgt    5      9.035 ±   2.216  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_delayed_batch                     avgt    5     10.805 ±   1.447  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_discard                           avgt    5      8.594 ±   0.527  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_discard_batch                     avgt    5      9.107 ±   2.582  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_discarded                         avgt    5      9.819 ±   0.779  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_discarded_batch                   avgt    5      8.934 ±   2.217  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_disconnect                        avgt    5      7.833 ±   3.221  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_disconnect_batch                  avgt    5      9.682 ±   1.163  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_disconnected                      avgt    5      9.408 ±   0.745  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_disconnected_batch                avgt    5     10.037 ±   2.101  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_expire                            avgt    5      9.146 ±   1.792  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_expire_batch                      avgt    5      9.770 ±   0.775  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_expired                           avgt    5      9.776 ±   1.192  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_expired_batch                     avgt    5      8.481 ±   3.385  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_fail                              avgt    5      8.300 ±   2.589  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_fail_batch                        avgt    5      8.465 ±   2.887  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_failed                            avgt    5      9.589 ±   0.822  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_failed_batch                      avgt    5      9.630 ±   0.537  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_recourse                          avgt    5      9.630 ±   0.506  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_recourse_batch                    avgt    5      9.145 ±   2.004  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_recoursed                         avgt    5      9.372 ±   2.238  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_recoursed_batch                   avgt    5      9.666 ±   0.945  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_redirect                          avgt    5      9.515 ±   0.550  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_redirect_batch                    avgt    5      9.067 ±   2.239  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_redirected                        avgt    5      9.825 ±   0.737  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_redirected_batch                  avgt    5      9.360 ±   1.232  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_reject                            avgt    5      9.130 ±   2.658  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_reject_batch                      avgt    5      9.412 ±   0.760  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_rejected                          avgt    5      9.585 ±   1.296  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_rejected_batch                    avgt    5     10.535 ±   1.023  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_resume                            avgt    5      9.785 ±   0.816  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_resume_batch                      avgt    5     10.216 ±   1.589  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_resumed                           avgt    5      9.762 ±   0.692  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_resumed_batch                     avgt    5     11.014 ±   1.331  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_retried                           avgt    5      9.694 ±   0.849  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_retried_batch                     avgt    5     10.037 ±   0.852  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_retry                             avgt    5      8.736 ±   2.983  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_retry_batch                       avgt    5      8.732 ±   1.843  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_schedule                          avgt    5      9.788 ±   0.333  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_schedule_batch                    avgt    5     10.001 ±   1.625  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_scheduled                         avgt    5      9.745 ±   0.378  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_scheduled_batch                   avgt    5     10.460 ±   0.862  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_signal                            avgt    5      9.832 ±   0.679  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_signal_batch                      avgt    5      8.608 ±   2.202  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_start                             avgt    5      9.134 ±   0.922  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_start_batch                       avgt    5      9.961 ±   2.002  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_started                           avgt    5      7.832 ±   1.878  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_started_batch                     avgt    5      8.839 ±   2.038  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_stop                              avgt    5      8.636 ±   2.111  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_stop_batch                        avgt    5      9.460 ±   1.004  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_stopped                           avgt    5     10.877 ±   0.829  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_stopped_batch                     avgt    5     10.571 ±   0.982  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_succeeded                         avgt    5      8.896 ±   1.609  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_succeeded_batch                   avgt    5      9.550 ±   0.675  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_success                           avgt    5      9.418 ±   0.891  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_success_batch                     avgt    5      9.605 ±   0.643  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_suspend                           avgt    5      9.857 ±   1.147  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_suspend_batch                     avgt    5     10.472 ±   2.140  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_suspended                         avgt    5      8.718 ±   2.620  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_suspended_batch                   avgt    5     10.509 ±   1.435  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.service_from_conduit                   avgt    5      1.855 ±   0.028  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.service_from_conduit_batch             avgt    5      1.665 ±   0.003  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_cancel                               avgt    5      7.555 ±   2.712  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_cancel_batch                         avgt    5      9.788 ±   1.100  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_complete                             avgt    5      8.829 ±   1.560  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_complete_batch                       avgt    5      9.646 ±   0.750  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_fail                                 avgt    5      9.259 ±   1.191  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_fail_batch                           avgt    5      9.319 ±   0.682  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_progress                             avgt    5      8.438 ±   3.107  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_progress_batch                       avgt    5      9.182 ±   0.621  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_reject                               avgt    5      9.423 ±   0.618  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_reject_batch                         avgt    5      9.399 ±   3.620  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_resume                               avgt    5      9.202 ±   0.558  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_resume_batch                         avgt    5      8.664 ±   0.752  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_schedule                             avgt    5      8.551 ±   3.041  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_schedule_batch                       avgt    5      9.156 ±   2.811  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_sign                                 avgt    5      9.274 ±   1.768  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_sign_batch                           avgt    5      7.531 ±   2.839  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_start                                avgt    5      9.554 ±   0.458  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_start_batch                          avgt    5      7.885 ±   4.568  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_submit                               avgt    5      7.827 ±   2.865  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_submit_batch                         avgt    5      9.567 ±   2.986  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_suspend                              avgt    5      8.333 ±   2.685  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_suspend_batch                        avgt    5      9.912 ±   1.174  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_timeout                              avgt    5      8.814 ±   2.334  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_timeout_batch                        avgt    5      8.514 ±   2.090  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.task_from_conduit                         avgt    5      1.852 ±   0.062  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.task_from_conduit_batch                   avgt    5      1.665 ±   0.004  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_abort_coordinator             avgt    5      9.039 ±   0.283  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_abort_coordinator_batch       avgt    5      8.968 ±   2.143  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_abort_participant             avgt    5      9.803 ±   0.548  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_abort_participant_batch       avgt    5      9.563 ±   0.715  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_commit_coordinator            avgt    5      8.707 ±   3.549  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_commit_coordinator_batch      avgt    5     10.496 ±   2.534  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_commit_participant            avgt    5      8.659 ±   2.902  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_commit_participant_batch      avgt    5     10.181 ±   1.457  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_compensate_coordinator        avgt    5      9.321 ±   2.975  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_compensate_coordinator_batch  avgt    5      9.104 ±   2.252  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_compensate_participant        avgt    5     10.009 ±   0.421  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_compensate_participant_batch  avgt    5     10.322 ±   1.193  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_conflict_coordinator          avgt    5      9.362 ±   1.577  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_conflict_coordinator_batch    avgt    5     10.399 ±   1.872  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_conflict_participant          avgt    5      9.877 ±   0.829  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_conflict_participant_batch    avgt    5      9.358 ±   1.041  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_expire_coordinator            avgt    5      9.625 ±   0.832  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_expire_coordinator_batch      avgt    5      9.901 ±   0.590  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_expire_participant            avgt    5      9.568 ±   1.116  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_expire_participant_batch      avgt    5      7.922 ±   3.046  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_prepare_coordinator           avgt    5      8.871 ±   2.222  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_prepare_coordinator_batch     avgt    5      9.474 ±   0.439  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_prepare_participant           avgt    5      9.397 ±   0.598  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_prepare_participant_batch     avgt    5     10.631 ±   2.656  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_rollback_coordinator          avgt    5      9.309 ±   1.170  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_rollback_coordinator_batch    avgt    5     10.753 ±   2.536  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_rollback_participant          avgt    5      9.246 ±   2.124  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_rollback_participant_batch    avgt    5      9.467 ±   0.677  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_signal                        avgt    5      9.258 ±   2.476  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_signal_batch                  avgt    5     10.629 ±   1.816  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_start_coordinator             avgt    5      9.512 ±   1.268  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_start_coordinator_batch       avgt    5      8.972 ±   2.131  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_start_participant             avgt    5      9.338 ±   0.944  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_start_participant_batch       avgt    5     10.954 ±   1.611  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.transaction_from_conduit           avgt    5      1.848 ±   0.008  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.transaction_from_conduit_batch     avgt    5      1.660 ±   0.001  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.breaker_from_conduit                   avgt    5      1.851 ±   0.046  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.breaker_from_conduit_batch             avgt    5      1.661 ±   0.003  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_close                             avgt    5      8.500 ±   0.372  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_close_batch                       avgt    5      9.624 ±   0.760  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_half_open                         avgt    5      9.350 ±   1.235  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_half_open_batch                   avgt    5     10.263 ±   0.641  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_open                              avgt    5      8.098 ±   4.733  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_open_batch                        avgt    5      9.050 ±   0.795  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_probe                             avgt    5      8.599 ±   3.849  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_probe_batch                       avgt    5      9.115 ±   0.694  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_reset                             avgt    5      9.458 ±   0.906  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_reset_batch                       avgt    5     10.077 ±   0.772  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_sign                              avgt    5      8.525 ±   1.769  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_sign_batch                        avgt    5      9.621 ±   3.275  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_trip                              avgt    5      8.481 ±   4.406  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_trip_batch                        avgt    5      9.081 ±   0.963  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_corrupt                            avgt    5      9.150 ±   0.976  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_corrupt_batch                      avgt    5      7.818 ±   3.506  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_drop                               avgt    5      8.365 ±   3.559  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_drop_batch                         avgt    5      9.023 ±   2.902  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_forward                            avgt    5      8.243 ±   4.028  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_forward_batch                      avgt    5      8.488 ±   2.465  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_fragment                           avgt    5      9.149 ±   1.665  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_fragment_batch                     avgt    5      9.434 ±   3.424  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_reassemble                         avgt    5      8.235 ±   4.014  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_reassemble_batch                   avgt    5      9.556 ±   0.886  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_receive                            avgt    5      9.142 ±   1.224  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_receive_batch                      avgt    5      9.431 ±   0.512  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_reorder                            avgt    5      8.883 ±   0.740  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_reorder_batch                      avgt    5     10.239 ±   0.848  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_route                              avgt    5      9.355 ±   0.133  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_route_batch                        avgt    5      9.791 ±   0.677  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_send                               avgt    5      8.395 ±   4.204  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_send_batch                         avgt    5      9.235 ±   3.007  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_sign                               avgt    5      8.115 ±   3.596  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_sign_batch                         avgt    5      9.325 ±   2.664  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.router_from_conduit                     avgt    5      1.856 ±   0.037  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.router_from_conduit_batch               avgt    5      1.660 ±   0.006  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_contract                            avgt    5      8.687 ±   3.239  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_contract_batch                      avgt    5      9.987 ±   0.675  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_deny                                avgt    5      9.322 ±   0.304  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_deny_batch                          avgt    5     10.374 ±   0.771  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_drain                               avgt    5      9.113 ±   1.086  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_drain_batch                         avgt    5      8.906 ±   0.753  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_drop                                avgt    5      8.978 ±   1.138  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_drop_batch                          avgt    5      9.837 ±   0.913  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_expand                              avgt    5      8.462 ±   3.537  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_expand_batch                        avgt    5      8.479 ±   2.269  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_pass                                avgt    5      9.078 ±   0.679  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_pass_batch                          avgt    5     10.046 ±   0.707  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_sign                                avgt    5      8.775 ±   1.757  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_sign_batch                          avgt    5      9.890 ±   1.233  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.valve_from_conduit                       avgt    5      1.853 ±   0.025  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.valve_from_conduit_batch                 avgt    5      1.661 ±   0.004  ns/op
i.h.serventis.jmh.opt.pool.LatchOps.emit_abandon                             avgt    5      8.939 ±   0.928  ns/op
i.h.serventis.jmh.opt.pool.LatchOps.emit_abandon_batch                       avgt    5      8.467 ±   2.310  ns/op
i.h.serventis.jmh.opt.pool.LatchOps.emit_arrive                              avgt    5      9.041 ±   1.474  ns/op
i.h.serventis.jmh.opt.pool.LatchOps.emit_arrive_batch                        avgt    5      9.864 ±   0.864  ns/op
i.h.serventis.jmh.opt.pool.LatchOps.emit_await                               avgt    5      8.195 ±   4.311  ns/op
i.h.serventis.jmh.opt.pool.LatchOps.emit_await_batch                         avgt    5      9.482 ±   3.056  ns/op
i.h.serventis.jmh.opt.pool.LatchOps.emit_release                             avgt    5      9.085 ±   0.364  ns/op
i.h.serventis.jmh.opt.pool.LatchOps.emit_release_batch                       avgt    5      8.742 ±   0.900  ns/op
i.h.serventis.jmh.opt.pool.LatchOps.emit_reset                               avgt    5      8.902 ±   1.516  ns/op
i.h.serventis.jmh.opt.pool.LatchOps.emit_reset_batch                         avgt    5      8.902 ±   2.510  ns/op
i.h.serventis.jmh.opt.pool.LatchOps.emit_sign                                avgt    5      9.220 ±   0.816  ns/op
i.h.serventis.jmh.opt.pool.LatchOps.emit_sign_batch                          avgt    5     10.190 ±   0.804  ns/op
i.h.serventis.jmh.opt.pool.LatchOps.emit_timeout                             avgt    5      8.717 ±   0.467  ns/op
i.h.serventis.jmh.opt.pool.LatchOps.emit_timeout_batch                       avgt    5      9.756 ±   0.197  ns/op
i.h.serventis.jmh.opt.pool.LatchOps.latch_from_conduit                       avgt    5      1.854 ±   0.038  ns/op
i.h.serventis.jmh.opt.pool.LatchOps.latch_from_conduit_batch                 avgt    5      1.663 ±   0.009  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_acquire                             avgt    5      9.790 ±   1.337  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_acquire_batch                       avgt    5     10.244 ±   2.204  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_acquired                            avgt    5      9.595 ±   0.957  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_acquired_batch                      avgt    5      8.454 ±   2.966  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_denied                              avgt    5      9.411 ±   0.625  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_denied_batch                        avgt    5      9.749 ±   0.885  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_deny                                avgt    5      8.642 ±   3.725  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_deny_batch                          avgt    5     10.740 ±   0.884  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_expire                              avgt    5      8.857 ±   2.871  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_expire_batch                        avgt    5      8.855 ±   2.083  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_expired                             avgt    5      9.618 ±   0.553  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_expired_batch                       avgt    5      9.582 ±   0.800  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_extend                              avgt    5      9.308 ±   0.661  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_extend_batch                        avgt    5      9.649 ±   0.919  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_extended                            avgt    5      9.225 ±   1.924  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_extended_batch                      avgt    5      9.563 ±   0.771  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_grant                               avgt    5      8.481 ±   3.172  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_grant_batch                         avgt    5      8.935 ±   2.407  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_granted                             avgt    5      8.959 ±   0.517  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_granted_batch                       avgt    5      9.027 ±   2.580  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_probe                               avgt    5      9.127 ±   0.953  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_probe_batch                         avgt    5      9.653 ±   0.842  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_probed                              avgt    5      8.556 ±   3.555  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_probed_batch                        avgt    5      9.922 ±   1.804  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_release                             avgt    5      9.762 ±   0.902  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_release_batch                       avgt    5      9.700 ±   0.269  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_released                            avgt    5      8.571 ±   1.744  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_released_batch                      avgt    5      9.582 ±   0.894  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_renew                               avgt    5      8.179 ±   2.423  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_renew_batch                         avgt    5      8.963 ±   2.517  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_renewed                             avgt    5      8.922 ±   2.161  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_renewed_batch                       avgt    5      8.954 ±   3.882  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_revoke                              avgt    5      9.617 ±   0.417  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_revoke_batch                        avgt    5     10.800 ±   0.802  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_revoked                             avgt    5      8.806 ±   3.127  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_revoked_batch                       avgt    5     10.696 ±   1.456  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_signal                              avgt    5      8.635 ±   2.876  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_signal_batch                        avgt    5      9.638 ±   0.305  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.lease_from_conduit                       avgt    5      1.856 ±   0.062  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.lease_from_conduit_batch                 avgt    5      1.660 ±   0.003  ns/op
i.h.serventis.jmh.opt.pool.LockOps.emit_abandon                              avgt    5      8.917 ±   0.752  ns/op
i.h.serventis.jmh.opt.pool.LockOps.emit_abandon_batch                        avgt    5     10.231 ±   0.469  ns/op
i.h.serventis.jmh.opt.pool.LockOps.emit_acquire                              avgt    5      8.310 ±   2.879  ns/op
i.h.serventis.jmh.opt.pool.LockOps.emit_acquire_batch                        avgt    5      8.556 ±   2.742  ns/op
i.h.serventis.jmh.opt.pool.LockOps.emit_attempt                              avgt    5      9.143 ±   0.719  ns/op
i.h.serventis.jmh.opt.pool.LockOps.emit_attempt_batch                        avgt    5      9.255 ±   0.718  ns/op
i.h.serventis.jmh.opt.pool.LockOps.emit_contest                              avgt    5      8.574 ±   2.115  ns/op
i.h.serventis.jmh.opt.pool.LockOps.emit_contest_batch                        avgt    5      9.854 ±   0.970  ns/op
i.h.serventis.jmh.opt.pool.LockOps.emit_deny                                 avgt    5      8.427 ±   3.796  ns/op
i.h.serventis.jmh.opt.pool.LockOps.emit_deny_batch                           avgt    5      9.295 ±   3.288  ns/op
i.h.serventis.jmh.opt.pool.LockOps.emit_downgrade                            avgt    5      9.365 ±   0.784  ns/op
i.h.serventis.jmh.opt.pool.LockOps.emit_downgrade_batch                      avgt    5      9.460 ±   3.346  ns/op
i.h.serventis.jmh.opt.pool.LockOps.emit_grant                                avgt    5      7.690 ±   3.111  ns/op
i.h.serventis.jmh.opt.pool.LockOps.emit_grant_batch                          avgt    5      7.884 ±   3.520  ns/op
i.h.serventis.jmh.opt.pool.LockOps.emit_release                              avgt    5      9.396 ±   0.523  ns/op
i.h.serventis.jmh.opt.pool.LockOps.emit_release_batch                        avgt    5      9.305 ±   0.698  ns/op
i.h.serventis.jmh.opt.pool.LockOps.emit_sign                                 avgt    5      7.768 ±   3.529  ns/op
i.h.serventis.jmh.opt.pool.LockOps.emit_sign_batch                           avgt    5      9.240 ±   2.580  ns/op
i.h.serventis.jmh.opt.pool.LockOps.emit_timeout                              avgt    5      8.636 ±   2.900  ns/op
i.h.serventis.jmh.opt.pool.LockOps.emit_timeout_batch                        avgt    5      8.267 ±   2.893  ns/op
i.h.serventis.jmh.opt.pool.LockOps.emit_upgrade                              avgt    5     10.308 ±   0.540  ns/op
i.h.serventis.jmh.opt.pool.LockOps.emit_upgrade_batch                        avgt    5      9.455 ±   1.120  ns/op
i.h.serventis.jmh.opt.pool.LockOps.lock_from_conduit                         avgt    5      1.863 ±   0.068  ns/op
i.h.serventis.jmh.opt.pool.LockOps.lock_from_conduit_batch                   avgt    5      1.661 ±   0.015  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_borrow                               avgt    5      8.012 ±   2.897  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_borrow_batch                         avgt    5      9.908 ±   1.249  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_contract                             avgt    5      9.234 ±   0.943  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_contract_batch                       avgt    5      8.617 ±   1.248  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_expand                               avgt    5      9.329 ±   0.857  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_expand_batch                         avgt    5      8.936 ±   0.915  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_reclaim                              avgt    5      7.789 ±   2.745  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_reclaim_batch                        avgt    5      8.323 ±   3.014  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_sign                                 avgt    5      8.172 ±   2.209  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_sign_batch                           avgt    5      9.269 ±   0.747  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.pool_from_conduit                         avgt    5      1.855 ±   0.059  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.pool_from_conduit_batch                   avgt    5      1.661 ±   0.008  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_acquire                          avgt    5      8.911 ±   0.606  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_acquire_batch                    avgt    5      9.884 ±   0.825  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_attempt                          avgt    5      8.682 ±   3.372  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_attempt_batch                    avgt    5      7.686 ±   3.295  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_deny                             avgt    5      9.255 ±   1.177  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_deny_batch                       avgt    5      9.215 ±   0.779  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_grant                            avgt    5      9.174 ±   1.116  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_grant_batch                      avgt    5      9.476 ±   1.986  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_release                          avgt    5      9.094 ±   0.738  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_release_batch                    avgt    5     10.211 ±   0.512  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_sign                             avgt    5      8.397 ±   2.313  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_sign_batch                       avgt    5      9.320 ±   0.806  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_timeout                          avgt    5      9.285 ±   0.766  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_timeout_batch                    avgt    5      9.166 ±   2.662  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.resource_from_conduit                 avgt    5      1.855 ±   0.059  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.resource_from_conduit_batch           avgt    5      1.661 ±   0.005  ns/op
i.h.serventis.jmh.opt.role.ActorOps.actor_from_conduit                       avgt    5      1.854 ±   0.047  ns/op
i.h.serventis.jmh.opt.role.ActorOps.actor_from_conduit_batch                 avgt    5      1.661 ±   0.008  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_acknowledge                         avgt    5      8.099 ±   2.655  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_acknowledge_batch                   avgt    5      9.250 ±   0.950  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_affirm                              avgt    5      8.498 ±   2.295  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_affirm_batch                        avgt    5     10.197 ±   1.055  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_ask                                 avgt    5      8.579 ±   3.247  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_ask_batch                           avgt    5      9.931 ±   0.748  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_clarify                             avgt    5      9.264 ±   0.991  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_clarify_batch                       avgt    5     10.058 ±   0.688  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_command                             avgt    5      9.466 ±   3.241  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_command_batch                       avgt    5      7.985 ±   3.156  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_deliver                             avgt    5      8.261 ±   3.959  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_deliver_batch                       avgt    5      9.771 ±   0.932  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_deny                                avgt    5      9.066 ±   0.993  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_deny_batch                          avgt    5      9.262 ±   0.441  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_explain                             avgt    5      9.252 ±   1.091  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_explain_batch                       avgt    5      9.574 ±   2.300  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_promise                             avgt    5      8.820 ±   1.797  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_promise_batch                       avgt    5     10.182 ±   0.488  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_report                              avgt    5      8.237 ±   2.951  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_report_batch                        avgt    5     10.417 ±   0.432  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_request                             avgt    5      8.345 ±   3.589  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_request_batch                       avgt    5      9.116 ±   3.087  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_sign                                avgt    5      8.485 ±   3.817  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_sign_batch                          avgt    5      9.063 ±   3.432  ns/op
i.h.serventis.jmh.opt.role.AgentOps.agent_from_conduit                       avgt    5      1.852 ±   0.031  ns/op
i.h.serventis.jmh.opt.role.AgentOps.agent_from_conduit_batch                 avgt    5      1.662 ±   0.014  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_accept                              avgt    5      9.668 ±   0.306  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_accept_batch                        avgt    5      9.696 ±   1.190  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_accepted                            avgt    5      9.659 ±   0.758  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_accepted_batch                      avgt    5      8.971 ±   2.504  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_breach                              avgt    5      9.883 ±   0.394  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_breach_batch                        avgt    5     10.526 ±   0.137  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_breached                            avgt    5      9.450 ±   0.480  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_breached_batch                      avgt    5      9.557 ±   0.902  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_depend                              avgt    5      9.136 ±   0.540  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_depend_batch                        avgt    5      9.428 ±   1.216  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_depended                            avgt    5      8.984 ±   2.478  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_depended_batch                      avgt    5      9.027 ±   2.319  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_fulfill                             avgt    5      9.199 ±   0.626  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_fulfill_batch                       avgt    5      9.295 ±   0.835  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_fulfilled                           avgt    5      9.139 ±   1.760  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_fulfilled_batch                     avgt    5      9.440 ±   0.987  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_inquire                             avgt    5      9.260 ±   1.190  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_inquire_batch                       avgt    5      8.887 ±   2.206  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_inquired                            avgt    5      9.904 ±   0.738  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_inquired_batch                      avgt    5      9.419 ±   1.088  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_observe                             avgt    5      9.747 ±   0.834  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_observe_batch                       avgt    5     10.573 ±   0.326  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_observed                            avgt    5      9.089 ±   0.650  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_observed_batch                      avgt    5      8.644 ±   2.392  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_offer                               avgt    5      9.750 ±   0.751  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_offer_batch                         avgt    5     11.092 ±   0.565  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_offered                             avgt    5      9.600 ±   0.662  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_offered_batch                       avgt    5      9.074 ±   2.003  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_promise                             avgt    5      9.141 ±   0.710  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_promise_batch                       avgt    5      7.992 ±   3.387  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_promised                            avgt    5      8.845 ±   2.304  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_promised_batch                      avgt    5     10.589 ±   1.681  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_retract                             avgt    5      9.657 ±   0.808  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_retract_batch                       avgt    5      9.890 ±   1.654  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_retracted                           avgt    5      9.570 ±   0.821  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_retracted_batch                     avgt    5      9.648 ±   0.813  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_signal                              avgt    5      9.237 ±   2.424  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_signal_batch                        avgt    5      9.073 ±   2.282  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_validate                            avgt    5      9.213 ±   0.926  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_validate_batch                      avgt    5      9.700 ±   0.898  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_validated                           avgt    5      9.443 ±   1.844  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_validated_batch                     avgt    5     11.166 ±   0.841  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.counter_from_conduit                   avgt    5      1.857 ±   0.041  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.counter_from_conduit_batch             avgt    5      1.660 ±   0.011  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_increment                         avgt    5      7.759 ±   3.027  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_increment_batch                   avgt    5      9.857 ±   0.922  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_overflow                          avgt    5      8.265 ±   3.543  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_overflow_batch                    avgt    5      9.786 ±   0.817  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_reset                             avgt    5      8.012 ±   4.284  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_reset_batch                       avgt    5      9.423 ±   0.832  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_sign                              avgt    5      8.229 ±   3.336  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_sign_batch                        avgt    5      9.835 ±   1.147  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_decrement                           avgt    5      7.712 ±   3.218  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_decrement_batch                     avgt    5      8.021 ±   3.924  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_increment                           avgt    5      8.389 ±   2.971  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_increment_batch                     avgt    5      9.177 ±   0.999  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_overflow                            avgt    5      8.337 ±   3.025  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_overflow_batch                      avgt    5      9.828 ±   1.010  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_reset                               avgt    5      9.261 ±   0.925  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_reset_batch                         avgt    5      9.284 ±   0.693  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_sign                                avgt    5      8.568 ±   3.887  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_sign_batch                          avgt    5      7.771 ±   3.635  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_underflow                           avgt    5      9.396 ±   1.246  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_underflow_batch                     avgt    5     10.131 ±   1.322  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.gauge_from_conduit                       avgt    5      1.857 ±   0.060  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.gauge_from_conduit_batch                 avgt    5      1.662 ±   0.014  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_debug                                 avgt    5      9.299 ±   1.097  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_debug_batch                           avgt    5      9.334 ±   0.904  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_info                                  avgt    5      9.339 ±   0.462  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_info_batch                            avgt    5      9.767 ±   0.917  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_severe                                avgt    5      8.269 ±   3.978  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_severe_batch                          avgt    5      9.880 ±   0.942  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_sign                                  avgt    5      8.471 ±   3.527  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_sign_batch                            avgt    5      8.625 ±   2.795  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_warning                               avgt    5      8.100 ±   4.331  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_warning_batch                         avgt    5     10.260 ±   0.236  ns/op
i.h.serventis.jmh.opt.tool.LogOps.log_from_conduit                           avgt    5      1.861 ±   0.005  ns/op
i.h.serventis.jmh.opt.tool.LogOps.log_from_conduit_batch                     avgt    5      1.661 ±   0.003  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_connect                             avgt    5      9.611 ±   1.196  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_connect_batch                       avgt    5     10.397 ±   4.517  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_connected                           avgt    5      8.201 ±   2.627  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_connected_batch                     avgt    5     10.683 ±   0.934  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_disconnect                          avgt    5      9.032 ±   1.801  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_disconnect_batch                    avgt    5      9.668 ±   1.024  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_disconnected                        avgt    5      8.782 ±   3.735  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_disconnected_batch                  avgt    5      9.123 ±   1.500  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_fail                                avgt    5      9.308 ±   1.701  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_fail_batch                          avgt    5      9.776 ±   0.591  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_failed                              avgt    5      9.696 ±   0.747  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_failed_batch                        avgt    5      9.488 ±   0.815  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_process                             avgt    5      8.948 ±   0.780  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_process_batch                       avgt    5     10.452 ±   1.280  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_processed                           avgt    5      9.638 ±   1.219  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_processed_batch                     avgt    5      9.881 ±   0.440  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_receive_batch                       avgt    5     10.566 ±   1.188  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_received_batch                      avgt    5     10.724 ±   1.196  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_signal                              avgt    5      9.626 ±   0.551  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_signal_batch                        avgt    5      9.060 ±   2.320  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_succeed                             avgt    5      8.815 ±   2.534  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_succeed_batch                       avgt    5      8.540 ±   3.615  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_succeeded                           avgt    5      9.746 ±   1.004  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_succeeded_batch                     avgt    5     10.789 ±   1.122  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transfer                            avgt    5      8.637 ±   3.518  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transfer_inbound                    avgt    5      9.353 ±   0.196  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transfer_outbound                   avgt    5      9.000 ±   0.592  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transferred                         avgt    5      9.475 ±   0.636  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transmit_batch                      avgt    5     11.320 ±   0.987  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transmitted_batch                   avgt    5     10.610 ±   1.066  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.probe_from_conduit                       avgt    5      1.861 ±   0.040  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.probe_from_conduit_batch                 avgt    5      1.662 ±   0.009  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_baseline                     avgt    5      9.787 ±   0.875  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_baseline_batch               avgt    5     10.419 ±   0.758  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_target                       avgt    5      9.687 ±   0.560  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_target_batch                 avgt    5      9.198 ±   0.486  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_threshold                    avgt    5      9.525 ±   1.169  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_threshold_batch              avgt    5     10.567 ±   0.452  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_baseline                     avgt    5      8.721 ±   0.374  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_baseline_batch               avgt    5      9.091 ±   2.460  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_target                       avgt    5      9.872 ±   0.482  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_target_batch                 avgt    5      9.193 ±   2.803  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_threshold                    avgt    5     10.902 ±   1.327  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_threshold_batch              avgt    5      9.348 ±   0.656  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_baseline                   avgt    5      9.171 ±   0.482  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_baseline_batch             avgt    5     10.083 ±   2.226  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_target                     avgt    5      9.030 ±   2.131  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_target_batch               avgt    5     10.136 ±   1.521  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_threshold                  avgt    5      9.195 ±   2.518  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_threshold_batch            avgt    5     10.775 ±   1.909  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_signal                             avgt    5      8.560 ±   1.130  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_signal_batch                       avgt    5      9.757 ±   1.316  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.sensor_from_conduit                     avgt    5      1.856 ±   0.052  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.sensor_from_conduit_batch               avgt    5      1.663 ±   0.012  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_mixed_pattern                         avgt    5      0.229 ±   0.003  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_single                                avgt    5      0.767 ±   0.001  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_single_batch                          avgt    5      0.019 ±   0.001  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_varied_batch                          avgt    5      1.515 ±   0.001  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_worst_case                            avgt    5      1.197 ±   0.001  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_critical                             avgt    5      9.215 ±   0.991  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_critical_batch                       avgt    5     10.665 ±   0.803  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_normal                               avgt    5      9.034 ±   0.719  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_normal_batch                         avgt    5      8.161 ±   3.310  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_signal                               avgt    5      9.518 ±   0.514  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_signal_batch                         avgt    5     10.341 ±   0.617  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_warning                              avgt    5      9.282 ±   1.800  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_warning_batch                        avgt    5      8.064 ±   3.310  ns/op
i.h.serventis.jmh.sdk.SituationOps.situation_from_conduit                    avgt    5      1.855 ±   0.057  ns/op
i.h.serventis.jmh.sdk.SituationOps.situation_from_conduit_batch              avgt    5      1.661 ±   0.007  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_converging_confirmed                    avgt    5      9.144 ±   2.011  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_converging_confirmed_batch              avgt    5     10.536 ±   1.596  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_defective_tentative                     avgt    5      8.295 ±   3.074  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_defective_tentative_batch               avgt    5     10.670 ±   1.469  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_degraded_measured                       avgt    5      8.658 ±   2.747  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_degraded_measured_batch                 avgt    5      8.735 ±   2.183  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_down_confirmed                          avgt    5      9.977 ±   1.449  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_down_confirmed_batch                    avgt    5     10.679 ±   0.922  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_signal                                  avgt    5      9.650 ±   0.764  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_signal_batch                            avgt    5      9.829 ±   1.228  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_stable_confirmed                        avgt    5      8.159 ±   3.000  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_stable_confirmed_batch                  avgt    5      8.345 ±   3.382  ns/op
i.h.serventis.jmh.sdk.StatusOps.status_from_conduit                          avgt    5      1.856 ±   0.052  ns/op
i.h.serventis.jmh.sdk.StatusOps.status_from_conduit_batch                    avgt    5      1.661 ±   0.005  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_alarm_flow                              avgt    5      9.146 ±   2.115  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_alarm_flow_batch                        avgt    5     10.472 ±   0.273  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_fault_link                              avgt    5      9.238 ±   1.540  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_fault_link_batch                        avgt    5     10.231 ±   1.895  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_limit_time                              avgt    5      9.868 ±   0.988  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_limit_time_batch                        avgt    5      9.837 ±   2.130  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_normal_space                            avgt    5      9.774 ±   0.592  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_normal_space_batch                      avgt    5      8.697 ±   2.210  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_signal                                  avgt    5      8.516 ±   3.360  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_signal_batch                            avgt    5      9.676 ±   0.815  ns/op
i.h.serventis.jmh.sdk.SystemOps.system_from_conduit                          avgt    5      1.860 ±   0.075  ns/op
i.h.serventis.jmh.sdk.SystemOps.system_from_conduit_batch                    avgt    5      1.662 ±   0.014  ns/op
i.h.substrates.jmh.CircuitOps.conduit_create_close                           avgt    5    283.992 ± 137.122  ns/op
i.h.substrates.jmh.CircuitOps.conduit_create_named                           avgt    5    276.253 ±  92.147  ns/op
i.h.substrates.jmh.CircuitOps.conduit_create_with_flow                       avgt    5    283.346 ± 102.459  ns/op
i.h.substrates.jmh.CircuitOps.create_and_close                               avgt    5    322.816 ± 148.295  ns/op
i.h.substrates.jmh.CircuitOps.create_await_close                             avgt    5  10864.904 ± 421.577  ns/op
i.h.substrates.jmh.CircuitOps.hot_await_queue_drain                          avgt    5   5471.948 ± 248.321  ns/op
i.h.substrates.jmh.CircuitOps.hot_conduit_create                             avgt    5     19.978 ±   0.055  ns/op
i.h.substrates.jmh.CircuitOps.hot_conduit_create_named                       avgt    5     19.919 ±   0.029  ns/op
i.h.substrates.jmh.CircuitOps.hot_conduit_create_with_flow                   avgt    5     22.839 ±   0.029  ns/op
i.h.substrates.jmh.CircuitOps.hot_pipe_async                                 avgt    5      1.586 ±   0.442  ns/op
i.h.substrates.jmh.CircuitOps.hot_pipe_async_with_flow                       avgt    5      3.896 ±   0.727  ns/op
i.h.substrates.jmh.CircuitOps.pipe_async                                     avgt    5    320.743 ± 176.545  ns/op
i.h.substrates.jmh.CircuitOps.pipe_async_with_flow                           avgt    5    280.882 ± 305.099  ns/op
i.h.substrates.jmh.ConduitOps.get_by_name                                    avgt    5      1.876 ±   0.114  ns/op
i.h.substrates.jmh.ConduitOps.get_by_name_batch                              avgt    5      1.659 ±   0.013  ns/op
i.h.substrates.jmh.ConduitOps.get_by_substrate                               avgt    5      2.000 ±   0.051  ns/op
i.h.substrates.jmh.ConduitOps.get_by_substrate_batch                         avgt    5      1.807 ±   0.004  ns/op
i.h.substrates.jmh.ConduitOps.get_cached                                     avgt    5      3.448 ±   0.088  ns/op
i.h.substrates.jmh.ConduitOps.get_cached_batch                               avgt    5      3.310 ±   0.004  ns/op
i.h.substrates.jmh.ConduitOps.subscribe                                      avgt    5    458.141 ± 454.662  ns/op
i.h.substrates.jmh.ConduitOps.subscribe_batch                                avgt    5    451.169 ± 456.409  ns/op
i.h.substrates.jmh.ConduitOps.subscribe_with_emission_await                  avgt    5   5730.156 ± 235.284  ns/op
i.h.substrates.jmh.CortexOps.circuit                                         avgt    5    279.283 ±  85.749  ns/op
i.h.substrates.jmh.CortexOps.circuit_batch                                   avgt    5    288.040 ± 142.680  ns/op
i.h.substrates.jmh.CortexOps.circuit_named                                   avgt    5    278.316 ± 143.346  ns/op
i.h.substrates.jmh.CortexOps.current                                         avgt    5      1.067 ±   0.001  ns/op
i.h.substrates.jmh.CortexOps.name_class                                      avgt    5      1.495 ±   0.024  ns/op
i.h.substrates.jmh.CortexOps.name_enum                                       avgt    5      2.822 ±   0.023  ns/op
i.h.substrates.jmh.CortexOps.name_iterable                                   avgt    5     11.165 ±   0.083  ns/op
i.h.substrates.jmh.CortexOps.name_path                                       avgt    5      1.893 ±   0.001  ns/op
i.h.substrates.jmh.CortexOps.name_path_batch                                 avgt    5      1.685 ±   0.001  ns/op
i.h.substrates.jmh.CortexOps.name_string                                     avgt    5      2.843 ±   0.013  ns/op
i.h.substrates.jmh.CortexOps.name_string_batch                               avgt    5      2.584 ±   0.021  ns/op
i.h.substrates.jmh.CortexOps.pipe_empty                                      avgt    5      0.438 ±   0.002  ns/op
i.h.substrates.jmh.CortexOps.pipe_empty_batch                                avgt    5     ≈ 10⁻³            ns/op
i.h.substrates.jmh.CortexOps.pipe_observer                                   avgt    5      6.090 ±   2.107  ns/op
i.h.substrates.jmh.CortexOps.pipe_observer_batch                             avgt    5      1.360 ±   0.513  ns/op
i.h.substrates.jmh.CortexOps.pipe_transform                                  avgt    5      4.397 ±   0.867  ns/op
i.h.substrates.jmh.CortexOps.scope                                           avgt    5      9.114 ±   0.040  ns/op
i.h.substrates.jmh.CortexOps.scope_batch                                     avgt    5      7.474 ±   0.061  ns/op
i.h.substrates.jmh.CortexOps.scope_named                                     avgt    5      7.989 ±   0.040  ns/op
i.h.substrates.jmh.CortexOps.slot_boolean                                    avgt    5      2.418 ±   0.159  ns/op
i.h.substrates.jmh.CortexOps.slot_double                                     avgt    5      2.388 ±   0.078  ns/op
i.h.substrates.jmh.CortexOps.slot_int                                        avgt    5      2.319 ±   0.792  ns/op
i.h.substrates.jmh.CortexOps.slot_long                                       avgt    5      2.411 ±   0.156  ns/op
i.h.substrates.jmh.CortexOps.slot_string                                     avgt    5      2.341 ±   0.485  ns/op
i.h.substrates.jmh.CortexOps.state_empty                                     avgt    5      0.436 ±   0.001  ns/op
i.h.substrates.jmh.CortexOps.state_empty_batch                               avgt    5     ≈ 10⁻³            ns/op
i.h.substrates.jmh.FlowOps.baseline_no_flow_await                            avgt    5     18.251 ±   3.265  ns/op
i.h.substrates.jmh.FlowOps.flow_combined_diff_guard_await                    avgt    5     21.880 ±   0.634  ns/op
i.h.substrates.jmh.FlowOps.flow_combined_diff_sample_await                   avgt    5     22.078 ±   0.495  ns/op
i.h.substrates.jmh.FlowOps.flow_combined_guard_limit_await                   avgt    5     18.489 ±   1.639  ns/op
i.h.substrates.jmh.FlowOps.flow_diff_await                                   avgt    5     19.033 ±   0.446  ns/op
i.h.substrates.jmh.FlowOps.flow_guard_await                                  avgt    5     19.022 ±   0.972  ns/op
i.h.substrates.jmh.FlowOps.flow_limit_await                                  avgt    5     19.079 ±   0.388  ns/op
i.h.substrates.jmh.FlowOps.flow_sample_await                                 avgt    5     17.809 ±   0.518  ns/op
i.h.substrates.jmh.FlowOps.flow_sift_await                                   avgt    5     17.853 ±   0.385  ns/op
i.h.substrates.jmh.NameOps.name_chained_deep                                 avgt    5     16.907 ±   0.066  ns/op
i.h.substrates.jmh.NameOps.name_chaining                                     avgt    5      8.992 ±   0.289  ns/op
i.h.substrates.jmh.NameOps.name_chaining_batch                               avgt    5      9.033 ±   0.044  ns/op
i.h.substrates.jmh.NameOps.name_compare                                      avgt    5     32.323 ±   0.497  ns/op
i.h.substrates.jmh.NameOps.name_compare_batch                                avgt    5     31.295 ±   1.197  ns/op
i.h.substrates.jmh.NameOps.name_depth                                        avgt    5      1.614 ±   0.098  ns/op
i.h.substrates.jmh.NameOps.name_depth_batch                                  avgt    5      1.261 ±   0.358  ns/op
i.h.substrates.jmh.NameOps.name_enclosure                                    avgt    5      0.547 ±   0.059  ns/op
i.h.substrates.jmh.NameOps.name_from_enum                                    avgt    5      2.827 ±   0.034  ns/op
i.h.substrates.jmh.NameOps.name_from_iterable                                avgt    5     11.386 ±   0.045  ns/op
i.h.substrates.jmh.NameOps.name_from_iterator                                avgt    5     12.903 ±   0.049  ns/op
i.h.substrates.jmh.NameOps.name_from_mapped_iterable                         avgt    5     11.665 ±   0.013  ns/op
i.h.substrates.jmh.NameOps.name_from_name                                    avgt    5      4.204 ±   0.022  ns/op
i.h.substrates.jmh.NameOps.name_from_string                                  avgt    5      3.029 ±   0.027  ns/op
i.h.substrates.jmh.NameOps.name_from_string_batch                            avgt    5      2.777 ±   0.023  ns/op
i.h.substrates.jmh.NameOps.name_interning_chained                            avgt    5     12.238 ±   0.051  ns/op
i.h.substrates.jmh.NameOps.name_interning_same_path                          avgt    5      3.558 ±   0.004  ns/op
i.h.substrates.jmh.NameOps.name_interning_segments                           avgt    5      9.192 ±   0.412  ns/op
i.h.substrates.jmh.NameOps.name_iterate_hierarchy                            avgt    5      1.708 ±   0.021  ns/op
i.h.substrates.jmh.NameOps.name_parsing                                      avgt    5      1.892 ±   0.002  ns/op
i.h.substrates.jmh.NameOps.name_parsing_batch                                avgt    5      1.675 ±   0.001  ns/op
i.h.substrates.jmh.NameOps.name_path_generation                              avgt    5     29.105 ±   1.882  ns/op
i.h.substrates.jmh.NameOps.name_path_generation_batch                        avgt    5     29.354 ±   8.478  ns/op
i.h.substrates.jmh.PipeOps.emit_chain_depth_1                                avgt    5      0.049 ±   0.001  ns/op
i.h.substrates.jmh.PipeOps.emit_chain_depth_10                               avgt    5      9.082 ±   0.339  ns/op
i.h.substrates.jmh.PipeOps.emit_chain_depth_20                               avgt    5     24.409 ±   1.043  ns/op
i.h.substrates.jmh.PipeOps.emit_chain_depth_5                                avgt    5      4.309 ±   0.918  ns/op
i.h.substrates.jmh.PipeOps.emit_fanout_width_1                               avgt    5      0.049 ±   0.001  ns/op
i.h.substrates.jmh.PipeOps.emit_fanout_width_10                              avgt    5      0.266 ±   0.008  ns/op
i.h.substrates.jmh.PipeOps.emit_fanout_width_20                              avgt    5      5.923 ±   0.034  ns/op
i.h.substrates.jmh.PipeOps.emit_fanout_width_5                               avgt    5      0.187 ±   0.006  ns/op
i.h.substrates.jmh.PipeOps.emit_no_await                                     avgt    5      0.049 ±   0.006  ns/op
i.h.substrates.jmh.PipeOps.emit_to_async_pipe                                avgt    5      8.236 ±   0.723  ns/op
i.h.substrates.jmh.PipeOps.emit_to_chained_pipes                             avgt    5      1.635 ±   0.002  ns/op
i.h.substrates.jmh.PipeOps.emit_to_double_transform                          avgt    5      0.502 ±   0.066  ns/op
i.h.substrates.jmh.PipeOps.emit_to_empty_pipe                                avgt    5      0.489 ±   0.005  ns/op
i.h.substrates.jmh.PipeOps.emit_to_fanout                                    avgt    5      0.905 ±   0.100  ns/op
i.h.substrates.jmh.PipeOps.emit_to_receptor_pipe                             avgt    5      0.674 ±   0.121  ns/op
i.h.substrates.jmh.PipeOps.emit_to_transform_pipe                            avgt    5      0.711 ±   0.080  ns/op
i.h.substrates.jmh.PipeOps.emit_with_await                                   avgt    5   5973.488 ± 193.203  ns/op
i.h.substrates.jmh.PipeOps.emit_with_counter_await                           avgt    5   6392.455 ± 222.553  ns/op
i.h.substrates.jmh.ReservoirOps.baseline_emit_no_reservoir_await             avgt    5     95.134 ±  17.242  ns/op
i.h.substrates.jmh.ReservoirOps.baseline_emit_no_reservoir_await_batch       avgt    5     16.670 ±   1.772  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_burst_then_drain_await             avgt    5     93.380 ±   4.524  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_burst_then_drain_await_batch       avgt    5     28.342 ±   0.100  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_drain_await                        avgt    5     90.059 ±  11.932  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_drain_await_batch                  avgt    5     28.402 ±   1.028  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_emit_drain_cycles_await            avgt    5    340.208 ±  11.889  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_emit_with_capture_await            avgt    5     84.596 ±   2.099  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_emit_with_capture_await_batch      avgt    5     23.202 ±   0.144  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_process_emissions_await            avgt    5     98.528 ±   1.213  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_process_emissions_await_batch      avgt    5     26.442 ±   0.079  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_process_subjects_await             avgt    5     99.116 ±   7.356  ns/op
i.h.substrates.jmh.ScopeOps.scope_child_anonymous                            avgt    5     17.834 ±   2.789  ns/op
i.h.substrates.jmh.ScopeOps.scope_child_anonymous_batch                      avgt    5     17.449 ±   0.194  ns/op
i.h.substrates.jmh.ScopeOps.scope_child_named                                avgt    5     17.025 ±   0.135  ns/op
i.h.substrates.jmh.ScopeOps.scope_child_named_batch                          avgt    5     16.982 ±   0.156  ns/op
i.h.substrates.jmh.ScopeOps.scope_close_idempotent                           avgt    5      2.339 ±   0.033  ns/op
i.h.substrates.jmh.ScopeOps.scope_close_idempotent_batch                     avgt    5      0.033 ±   0.001  ns/op
i.h.substrates.jmh.ScopeOps.scope_closure                                    avgt    5    274.327 ± 103.684  ns/op
i.h.substrates.jmh.ScopeOps.scope_closure_batch                              avgt    5    277.888 ±  73.697  ns/op
i.h.substrates.jmh.ScopeOps.scope_complex                                    avgt    5    966.185 ± 116.198  ns/op
i.h.substrates.jmh.ScopeOps.scope_create_and_close                           avgt    5      2.384 ±   0.061  ns/op
i.h.substrates.jmh.ScopeOps.scope_create_and_close_batch                     avgt    5      0.033 ±   0.001  ns/op
i.h.substrates.jmh.ScopeOps.scope_create_named                               avgt    5      2.391 ±   0.027  ns/op
i.h.substrates.jmh.ScopeOps.scope_create_named_batch                         avgt    5      0.033 ±   0.001  ns/op
i.h.substrates.jmh.ScopeOps.scope_hierarchy                                  avgt    5     27.108 ±   0.197  ns/op
i.h.substrates.jmh.ScopeOps.scope_hierarchy_batch                            avgt    5     26.444 ±   0.077  ns/op
i.h.substrates.jmh.ScopeOps.scope_parent_closes_children                     avgt    5     42.491 ±   2.813  ns/op
i.h.substrates.jmh.ScopeOps.scope_parent_closes_children_batch               avgt    5     41.181 ±   2.151  ns/op
i.h.substrates.jmh.ScopeOps.scope_register_multiple                          avgt    5   1415.696 ± 534.244  ns/op
i.h.substrates.jmh.ScopeOps.scope_register_multiple_batch                    avgt    5   1468.358 ±  80.110  ns/op
i.h.substrates.jmh.ScopeOps.scope_register_single                            avgt    5    297.277 ±  73.294  ns/op
i.h.substrates.jmh.ScopeOps.scope_register_single_batch                      avgt    5    304.484 ±  43.082  ns/op
i.h.substrates.jmh.ScopeOps.scope_with_resources                             avgt    5    597.431 ± 142.875  ns/op
i.h.substrates.jmh.StateOps.slot_name                                        avgt    5      0.531 ±   0.067  ns/op
i.h.substrates.jmh.StateOps.slot_name_batch                                  avgt    5      0.001 ±   0.001  ns/op
i.h.substrates.jmh.StateOps.slot_type                                        avgt    5      0.441 ±   0.002  ns/op
i.h.substrates.jmh.StateOps.slot_value                                       avgt    5      0.639 ±   0.001  ns/op
i.h.substrates.jmh.StateOps.slot_value_batch                                 avgt    5      0.001 ±   0.001  ns/op
i.h.substrates.jmh.StateOps.state_compact                                    avgt    5     10.308 ±   0.068  ns/op
i.h.substrates.jmh.StateOps.state_compact_batch                              avgt    5     10.705 ±   0.100  ns/op
i.h.substrates.jmh.StateOps.state_iterate_slots                              avgt    5      2.175 ±   0.018  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_int                               avgt    5      4.698 ±   0.654  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_int_batch                         avgt    5      4.689 ±   1.514  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_long                              avgt    5      4.641 ±   0.592  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_object                            avgt    5      2.518 ±   0.213  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_object_batch                      avgt    5      2.565 ±   0.908  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_string                            avgt    5      4.745 ±   0.159  ns/op
i.h.substrates.jmh.StateOps.state_value_read                                 avgt    5      1.486 ±   0.002  ns/op
i.h.substrates.jmh.StateOps.state_value_read_batch                           avgt    5      1.266 ±   0.001  ns/op
i.h.substrates.jmh.StateOps.state_values_stream                              avgt    5      4.571 ±   1.077  ns/op
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
@OperationsPerInvocation ( BATCH_SIZE )
public ReturnType operation_batch () {
    ReturnType result = null;
    for ( var i = 0; i < BATCH_SIZE; i++ ) {
        result = operation ();
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
public Name name_from_string () {
    return cortex.name ( FIRST );  // Return value used by JMH
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
@BenchmarkMode ( Mode.AverageTime )
@OutputTimeUnit ( TimeUnit.NANOSECONDS )
@Fork ( 1 )
@Warmup ( iterations = 3, time = 1 )
@Measurement ( iterations = 5, time = 1 )
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