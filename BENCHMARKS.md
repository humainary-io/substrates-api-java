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
i.h.serventis.jmh.opt.data.CacheOps.cache_from_conduit                       avgt    5      1.780 ±   0.039  ns/op
i.h.serventis.jmh.opt.data.CacheOps.cache_from_conduit_batch                 avgt    5      1.606 ±   0.007  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_evict                               avgt    5      8.644 ±   5.000  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_evict_batch                         avgt    5      7.776 ±   6.486  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_expire                              avgt    5      8.945 ±   4.183  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_expire_batch                        avgt    5      8.808 ±   3.389  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_hit                                 avgt    5      9.142 ±   6.104  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_hit_batch                           avgt    5      8.633 ±   4.539  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_lookup                              avgt    5      9.051 ±   3.110  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_lookup_batch                        avgt    5     11.318 ±   0.822  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_miss                                avgt    5      9.196 ±   6.998  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_miss_batch                          avgt    5      8.181 ±   6.843  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_remove                              avgt    5      9.167 ±   4.207  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_remove_batch                        avgt    5      8.995 ±   8.153  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_sign                                avgt    5      9.243 ±   3.476  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_sign_batch                          avgt    5     10.006 ±   0.653  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_store                               avgt    5      9.074 ±   5.138  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_store_batch                         avgt    5      8.065 ±   5.493  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_aggregate                        avgt    5      8.899 ±   5.217  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_aggregate_batch                  avgt    5     10.979 ±   0.957  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_backpressure                     avgt    5      9.096 ±   5.546  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_backpressure_batch               avgt    5      9.880 ±   0.420  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_buffer                           avgt    5      9.091 ±   4.588  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_buffer_batch                     avgt    5      9.795 ±   0.625  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_checkpoint                       avgt    5      8.809 ±   4.722  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_checkpoint_batch                 avgt    5      9.207 ±   1.065  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_filter                           avgt    5      9.409 ±   6.053  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_filter_batch                     avgt    5     10.409 ±   0.611  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_input                            avgt    5      8.384 ±   3.006  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_input_batch                      avgt    5      9.356 ±   0.954  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_lag                              avgt    5      9.039 ±   5.874  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_lag_batch                        avgt    5      8.606 ±   6.654  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_output                           avgt    5     11.109 ±   0.861  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_output_batch                     avgt    5     10.747 ±   1.199  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_overflow                         avgt    5      9.487 ±   6.682  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_overflow_batch                   avgt    5      9.844 ±   0.764  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_sign                             avgt    5      8.451 ±   5.446  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_sign_batch                       avgt    5     10.799 ±   0.967  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_skip                             avgt    5     10.332 ±   0.413  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_skip_batch                       avgt    5      8.834 ±   6.439  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_transform                        avgt    5      9.912 ±   0.785  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_transform_batch                  avgt    5      8.611 ±   4.853  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_watermark                        avgt    5      8.367 ±   4.795  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_watermark_batch                  avgt    5     10.253 ±   0.474  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_flow_etl                     avgt    5     48.163 ±  34.611  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_flow_stream                  avgt    5     49.340 ±  30.742  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_flow_windowed                avgt    5     47.648 ±  24.948  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_from_conduit                 avgt    5      1.870 ±   0.063  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_from_conduit_batch           avgt    5      1.661 ±   0.010  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_dequeue                             avgt    5      8.563 ±   5.332  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_dequeue_batch                       avgt    5      8.702 ±   4.135  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_enqueue                             avgt    5      8.146 ±   2.951  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_enqueue_batch                       avgt    5      9.819 ±   0.409  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_overflow                            avgt    5      8.900 ±   4.228  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_overflow_batch                      avgt    5      8.950 ±   6.803  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_sign                                avgt    5     10.076 ±   0.638  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_sign_batch                          avgt    5      9.343 ±   6.472  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_underflow                           avgt    5      9.209 ±   6.651  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_underflow_batch                     avgt    5      8.439 ±   4.452  ns/op
i.h.serventis.jmh.opt.data.QueueOps.queue_from_conduit                       avgt    5      1.872 ±   0.053  ns/op
i.h.serventis.jmh.opt.data.QueueOps.queue_from_conduit_batch                 avgt    5      1.661 ±   0.011  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_overflow                            avgt    5      8.742 ±   3.019  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_overflow_batch                      avgt    5     10.431 ±   1.487  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_pop                                 avgt    5      8.990 ±   3.495  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_pop_batch                           avgt    5      8.917 ±   0.780  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_push                                avgt    5     11.399 ±   1.149  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_push_batch                          avgt    5      8.052 ±   5.145  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_sign                                avgt    5      8.427 ±   4.520  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_sign_batch                          avgt    5      9.728 ±   0.879  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_underflow                           avgt    5      9.805 ±   0.723  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_underflow_batch                     avgt    5      9.723 ±   0.362  ns/op
i.h.serventis.jmh.opt.data.StackOps.stack_from_conduit                       avgt    5      1.858 ±   0.025  ns/op
i.h.serventis.jmh.opt.data.StackOps.stack_from_conduit_batch                 avgt    5      1.661 ±   0.010  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_crash                             avgt    5      9.069 ±   6.860  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_crash_batch                       avgt    5      8.856 ±   7.323  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_fail                              avgt    5      8.841 ±   5.399  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_fail_batch                        avgt    5      8.505 ±   4.651  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_kill                              avgt    5      8.765 ±   4.551  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_kill_batch                        avgt    5      8.031 ±   3.137  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_restart                           avgt    5      8.973 ±   3.949  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_restart_batch                     avgt    5      7.893 ±   8.152  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_resume                            avgt    5     11.033 ±   2.882  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_resume_batch                      avgt    5      8.197 ±   8.703  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_sign                              avgt    5     10.685 ±   0.986  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_sign_batch                        avgt    5      7.808 ±   5.099  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_spawn                             avgt    5      8.808 ±   3.464  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_spawn_batch                       avgt    5      7.653 ±   5.318  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_start                             avgt    5      8.783 ±   2.902  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_start_batch                       avgt    5      8.553 ±   5.691  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_stop                              avgt    5      9.864 ±   0.484  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_stop_batch                        avgt    5      8.223 ±   5.141  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_suspend                           avgt    5      9.658 ±   0.819  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_suspend_batch                     avgt    5      8.180 ±   2.815  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.process_from_conduit                   avgt    5      1.869 ±   0.072  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.process_from_conduit_batch             avgt    5      1.660 ±   0.002  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_call                              avgt    5      8.818 ±   3.938  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_call_batch                        avgt    5      9.591 ±   3.880  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_called                            avgt    5      8.701 ±   3.749  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_called_batch                      avgt    5      9.675 ±   3.617  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_delay                             avgt    5      9.008 ±   2.798  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_delay_batch                       avgt    5      9.306 ±   2.480  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_delayed                           avgt    5      9.656 ±   1.110  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_delayed_batch                     avgt    5      8.392 ±   3.246  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_discard                           avgt    5      9.052 ±   3.112  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_discard_batch                     avgt    5      9.245 ±   8.273  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_discarded                         avgt    5      9.769 ±   0.505  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_discarded_batch                   avgt    5     11.335 ±   0.863  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_disconnect                        avgt    5      8.742 ±   3.902  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_disconnect_batch                  avgt    5      8.927 ±   1.035  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_disconnected                      avgt    5      9.477 ±   0.894  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_disconnected_batch                avgt    5     10.020 ±   3.393  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_expire                            avgt    5      9.609 ±   0.594  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_expire_batch                      avgt    5      7.552 ±   6.212  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_expired                           avgt    5      9.977 ±   0.653  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_expired_batch                     avgt    5      9.342 ±   3.532  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_fail                              avgt    5      9.522 ±   0.523  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_fail_batch                        avgt    5      9.420 ±   0.343  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_failed                            avgt    5     10.351 ±   0.792  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_failed_batch                      avgt    5      8.195 ±   1.073  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_recourse                          avgt    5     11.480 ±   0.761  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_recourse_batch                    avgt    5      9.404 ±   0.623  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_recoursed                         avgt    5      9.722 ±   0.724  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_recoursed_batch                   avgt    5     11.165 ±   0.987  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_redirect                          avgt    5     10.653 ±   0.804  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_redirect_batch                    avgt    5     11.805 ±   0.288  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_redirected                        avgt    5      9.044 ±   3.480  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_redirected_batch                  avgt    5      9.607 ±   2.921  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_reject                            avgt    5     10.594 ±   0.469  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_reject_batch                      avgt    5      8.470 ±   2.815  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_rejected                          avgt    5     10.291 ±   0.453  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_rejected_batch                    avgt    5     11.744 ±   1.203  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_resume                            avgt    5      9.946 ±   0.459  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_resume_batch                      avgt    5      9.052 ±   5.103  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_resumed                           avgt    5      8.695 ±   3.646  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_resumed_batch                     avgt    5      9.972 ±   0.592  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_retried                           avgt    5      9.003 ±   3.537  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_retried_batch                     avgt    5     10.437 ±   0.497  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_retry                             avgt    5     11.311 ±   0.598  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_retry_batch                       avgt    5      9.939 ±   0.608  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_schedule                          avgt    5      8.767 ±   4.140  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_schedule_batch                    avgt    5     11.818 ±   1.063  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_scheduled                         avgt    5      9.688 ±   0.820  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_scheduled_batch                   avgt    5      8.538 ±   3.539  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_signal                            avgt    5      8.613 ±   3.763  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_signal_batch                      avgt    5      9.661 ±   3.922  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_start                             avgt    5      8.826 ±   4.884  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_start_batch                       avgt    5     11.178 ±   0.742  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_started                           avgt    5      8.975 ±   7.016  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_started_batch                     avgt    5     10.784 ±   0.589  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_stop                              avgt    5     10.599 ±   0.608  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_stop_batch                        avgt    5      8.084 ±   3.178  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_stopped                           avgt    5      8.289 ±   2.326  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_stopped_batch                     avgt    5     10.966 ±   0.655  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_succeeded                         avgt    5     10.727 ±   0.747  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_succeeded_batch                   avgt    5      9.013 ±   0.919  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_success                           avgt    5      9.589 ±   0.750  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_success_batch                     avgt    5     10.354 ±   0.605  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_suspend                           avgt    5     10.169 ±   0.923  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_suspend_batch                     avgt    5     10.272 ±   0.352  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_suspended                         avgt    5      8.927 ±   6.066  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_suspended_batch                   avgt    5      7.694 ±   3.531  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.service_from_conduit                   avgt    5      1.860 ±   0.055  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.service_from_conduit_batch             avgt    5      1.657 ±   0.004  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_cancel                               avgt    5      9.064 ±   6.150  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_cancel_batch                         avgt    5      8.525 ±   4.521  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_complete                             avgt    5     10.171 ±   0.707  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_complete_batch                       avgt    5     10.944 ±   0.725  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_fail                                 avgt    5     10.648 ±   0.838  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_fail_batch                           avgt    5      7.879 ±   8.656  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_progress                             avgt    5      8.597 ±   4.257  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_progress_batch                       avgt    5      8.269 ±   7.083  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_reject                               avgt    5     10.142 ±   0.922  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_reject_batch                         avgt    5      9.639 ±   0.515  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_resume                               avgt    5      8.628 ±   5.225  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_resume_batch                         avgt    5      8.066 ±   3.009  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_schedule                             avgt    5      8.146 ±   4.367  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_schedule_batch                       avgt    5      9.526 ±   0.705  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_sign                                 avgt    5      8.815 ±   6.178  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_sign_batch                           avgt    5      9.241 ±   1.527  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_start                                avgt    5      9.049 ±   6.505  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_start_batch                          avgt    5      9.015 ±   0.739  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_submit                               avgt    5      8.964 ±   6.231  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_submit_batch                         avgt    5      7.884 ±   7.455  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_suspend                              avgt    5      8.351 ±   5.955  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_suspend_batch                        avgt    5     11.074 ±   0.384  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_timeout                              avgt    5      8.430 ±   4.975  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_timeout_batch                        avgt    5      8.178 ±   4.950  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.task_from_conduit                         avgt    5      1.847 ±   0.019  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.task_from_conduit_batch                   avgt    5      1.660 ±   0.003  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_abort_coordinator             avgt    5      8.828 ±   5.932  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_abort_coordinator_batch       avgt    5     10.488 ±   0.841  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_abort_participant             avgt    5      8.392 ±   5.596  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_abort_participant_batch       avgt    5     10.096 ±   2.388  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_commit_coordinator            avgt    5      9.594 ±   2.639  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_commit_coordinator_batch      avgt    5     10.130 ±   1.559  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_commit_participant            avgt    5     10.707 ±   0.890  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_commit_participant_batch      avgt    5      8.889 ±   6.993  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_compensate_coordinator        avgt    5      8.767 ±   2.908  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_compensate_coordinator_batch  avgt    5     10.234 ±   0.781  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_compensate_participant        avgt    5      8.641 ±   3.770  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_compensate_participant_batch  avgt    5     10.456 ±   5.027  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_conflict_coordinator          avgt    5      8.480 ±   4.788  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_conflict_coordinator_batch    avgt    5     10.265 ±   0.974  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_conflict_participant          avgt    5      9.047 ±   5.634  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_conflict_participant_batch    avgt    5     10.866 ±   0.858  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_expire_coordinator            avgt    5     10.310 ±   0.677  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_expire_coordinator_batch      avgt    5      8.699 ±   5.354  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_expire_participant            avgt    5      8.605 ±   3.475  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_expire_participant_batch      avgt    5     10.480 ±   0.613  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_prepare_coordinator           avgt    5     10.796 ±   0.919  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_prepare_coordinator_batch     avgt    5      9.739 ±   4.039  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_prepare_participant           avgt    5      8.848 ±   3.921  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_prepare_participant_batch     avgt    5      8.519 ±   3.440  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_rollback_coordinator          avgt    5      7.948 ±   5.520  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_rollback_coordinator_batch    avgt    5     11.292 ±   1.086  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_rollback_participant          avgt    5      8.772 ±   4.341  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_rollback_participant_batch    avgt    5      9.413 ±   3.961  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_signal                        avgt    5     10.811 ±   0.973  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_signal_batch                  avgt    5      9.411 ±   4.339  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_start_coordinator             avgt    5      8.160 ±   4.036  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_start_coordinator_batch       avgt    5      8.384 ±   3.127  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_start_participant             avgt    5      8.464 ±   4.981  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_start_participant_batch       avgt    5      7.749 ±   5.495  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.transaction_from_conduit           avgt    5      1.859 ±   0.021  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.transaction_from_conduit_batch     avgt    5      1.661 ±   0.009  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.breaker_from_conduit                   avgt    5      1.853 ±   0.025  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.breaker_from_conduit_batch             avgt    5      1.661 ±   0.010  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_close                             avgt    5      9.393 ±   0.660  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_close_batch                       avgt    5      9.244 ±   0.748  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_half_open                         avgt    5      8.808 ±   3.838  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_half_open_batch                   avgt    5      8.047 ±   7.553  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_open                              avgt    5      8.560 ±   4.142  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_open_batch                        avgt    5      8.066 ±   5.092  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_probe                             avgt    5      8.651 ±   3.442  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_probe_batch                       avgt    5      9.435 ±   0.599  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_reset                             avgt    5      9.324 ±   0.545  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_reset_batch                       avgt    5     10.309 ±   0.496  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_sign                              avgt    5     10.208 ±   0.529  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_sign_batch                        avgt    5      8.660 ±   0.969  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_trip                              avgt    5      8.229 ±   2.542  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_trip_batch                        avgt    5      8.116 ±   7.044  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_corrupt                            avgt    5      9.064 ±   0.617  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_corrupt_batch                      avgt    5      8.326 ±   3.147  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_drop                               avgt    5      8.693 ±   2.748  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_drop_batch                         avgt    5      8.976 ±   0.876  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_forward                            avgt    5      8.825 ±   6.162  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_forward_batch                      avgt    5      8.823 ±   7.280  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_fragment                           avgt    5      8.348 ±   4.105  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_fragment_batch                     avgt    5     10.493 ±   0.465  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_reassemble                         avgt    5      8.649 ±   4.511  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_reassemble_batch                   avgt    5     10.384 ±   0.373  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_receive                            avgt    5      8.624 ±   3.031  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_receive_batch                      avgt    5      7.663 ±   2.772  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_reorder                            avgt    5      8.220 ±   5.100  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_reorder_batch                      avgt    5      7.891 ±   4.130  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_route                              avgt    5      8.390 ±   4.251  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_route_batch                        avgt    5      7.940 ±   6.138  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_send                               avgt    5      8.287 ±   3.793  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_send_batch                         avgt    5      9.220 ±   1.119  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_sign                               avgt    5      8.478 ±   5.105  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_sign_batch                         avgt    5      8.538 ±   4.020  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.router_from_conduit                     avgt    5      1.869 ±   0.074  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.router_from_conduit_batch               avgt    5      1.660 ±   0.002  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_contract                            avgt    5      8.261 ±   4.334  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_contract_batch                      avgt    5      9.632 ±   0.409  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_deny                                avgt    5     10.654 ±   0.429  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_deny_batch                          avgt    5      8.296 ±   7.922  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_drain                               avgt    5      8.269 ±   4.075  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_drain_batch                         avgt    5      8.541 ±   6.995  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_drop                                avgt    5     10.563 ±   0.321  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_drop_batch                          avgt    5      8.017 ±   4.611  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_expand                              avgt    5      8.796 ±   4.072  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_expand_batch                        avgt    5      7.720 ±   5.032  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_pass                                avgt    5      8.893 ±   5.143  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_pass_batch                          avgt    5     10.366 ±   1.114  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_sign                                avgt    5      8.526 ±   3.806  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_sign_batch                          avgt    5      8.829 ±   0.587  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.valve_from_conduit                       avgt    5      1.846 ±   0.008  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.valve_from_conduit_batch                 avgt    5      1.662 ±   0.006  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_contract_provider                avgt    5      8.918 ±   0.503  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_contract_provider_batch          avgt    5      8.214 ±   3.142  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_contract_receiver                avgt    5      8.579 ±   3.334  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_contract_receiver_batch          avgt    5      9.807 ±   4.992  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_full_exchange                    avgt    5     10.096 ±   4.807  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_full_exchange_batch              avgt    5      9.691 ±   4.090  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_signal                           avgt    5      8.873 ±   3.709  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_signal_batch                     avgt    5      7.837 ±   4.190  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_transfer_provider                avgt    5      8.970 ±   3.336  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_transfer_provider_batch          avgt    5      9.254 ±   3.918  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_transfer_receiver                avgt    5      8.743 ±   3.199  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_transfer_receiver_batch          avgt    5      8.730 ±   5.743  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.exchange_from_conduit                 avgt    5      1.867 ±   0.061  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.exchange_from_conduit_batch           avgt    5      1.659 ±   0.004  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_acquire                             avgt    5      8.853 ±   3.861  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_acquire_batch                       avgt    5      9.529 ±   4.286  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_acquired                            avgt    5      7.873 ±   4.035  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_acquired_batch                      avgt    5     10.638 ±   0.737  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_denied                              avgt    5      8.543 ±   3.542  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_denied_batch                        avgt    5      9.852 ±   0.305  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_deny                                avgt    5      8.563 ±   3.439  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_deny_batch                          avgt    5      9.560 ±   4.792  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_expire                              avgt    5      8.160 ±   3.412  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_expire_batch                        avgt    5      9.982 ±   5.905  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_expired                             avgt    5      9.814 ±   1.588  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_expired_batch                       avgt    5      8.724 ±   4.226  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_extend                              avgt    5      8.482 ±   2.863  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_extend_batch                        avgt    5      9.296 ±   0.435  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_extended                            avgt    5      8.774 ±   3.496  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_extended_batch                      avgt    5      8.240 ±   3.822  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_grant                               avgt    5      8.668 ±   5.464  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_grant_batch                         avgt    5      8.635 ±   2.996  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_granted                             avgt    5      8.790 ±   5.014  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_granted_batch                       avgt    5      8.587 ±   3.104  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_probe                               avgt    5      8.742 ±   2.795  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_probe_batch                         avgt    5      9.172 ±   0.572  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_probed                              avgt    5      8.537 ±   2.034  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_probed_batch                        avgt    5      9.569 ±   2.852  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_release                             avgt    5      8.473 ±   3.226  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_release_batch                       avgt    5      8.904 ±   0.664  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_released                            avgt    5      8.637 ±   3.406  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_released_batch                      avgt    5      9.266 ±   0.410  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_renew                               avgt    5      8.292 ±   3.871  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_renew_batch                         avgt    5      8.537 ±   5.828  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_renewed                             avgt    5      8.734 ±   3.987  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_renewed_batch                       avgt    5      9.611 ±   5.330  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_revoke                              avgt    5      8.616 ±   3.104  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_revoke_batch                        avgt    5      9.191 ±   3.717  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_revoked                             avgt    5      8.003 ±   4.459  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_revoked_batch                       avgt    5      9.710 ±   0.461  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_signal                              avgt    5      9.756 ±   0.311  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_signal_batch                        avgt    5      9.307 ±   2.784  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.lease_from_conduit                       avgt    5      1.864 ±   0.075  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.lease_from_conduit_batch                 avgt    5      1.661 ±   0.009  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_borrow                               avgt    5      8.286 ±   3.907  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_borrow_batch                         avgt    5      8.365 ±   3.932  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_contract                             avgt    5      8.473 ±   5.287  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_contract_batch                       avgt    5     10.615 ±   0.863  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_expand                               avgt    5      8.671 ±   3.714  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_expand_batch                         avgt    5      9.102 ±   0.682  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_reclaim                              avgt    5      8.437 ±   3.870  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_reclaim_batch                        avgt    5      8.072 ±   4.533  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_sign                                 avgt    5      8.703 ±   3.409  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_sign_batch                           avgt    5      9.292 ±   0.515  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.pool_from_conduit                         avgt    5      1.867 ±   0.069  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.pool_from_conduit_batch                   avgt    5      1.661 ±   0.005  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_acquire                          avgt    5      9.219 ±   5.804  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_acquire_batch                    avgt    5      8.176 ±   6.880  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_attempt                          avgt    5      8.361 ±   4.049  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_attempt_batch                    avgt    5      9.323 ±   0.821  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_deny                             avgt    5      9.753 ±   0.518  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_deny_batch                       avgt    5     10.816 ±   0.497  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_grant                            avgt    5      8.903 ±   3.725  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_grant_batch                      avgt    5     11.083 ±   0.994  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_release                          avgt    5      9.644 ±   0.463  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_release_batch                    avgt    5      7.955 ±   8.504  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_sign                             avgt    5      8.661 ±   3.859  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_sign_batch                       avgt    5      8.146 ±   2.869  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_timeout                          avgt    5      8.666 ±   5.413  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_timeout_batch                    avgt    5      8.047 ±   7.463  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.resource_from_conduit                 avgt    5      1.860 ±   0.019  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.resource_from_conduit_batch           avgt    5      1.659 ±   0.005  ns/op
i.h.serventis.jmh.opt.role.ActorOps.actor_from_conduit                       avgt    5      1.866 ±   0.068  ns/op
i.h.serventis.jmh.opt.role.ActorOps.actor_from_conduit_batch                 avgt    5      1.660 ±   0.009  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_acknowledge                         avgt    5     10.885 ±   0.767  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_acknowledge_batch                   avgt    5      7.781 ±   9.011  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_affirm                              avgt    5      8.473 ±   3.891  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_affirm_batch                        avgt    5     10.078 ±   0.468  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_ask                                 avgt    5      8.603 ±   3.229  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_ask_batch                           avgt    5      9.530 ±   0.423  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_clarify                             avgt    5      8.306 ±   3.148  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_clarify_batch                       avgt    5      7.980 ±   6.159  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_command                             avgt    5      8.944 ±   5.297  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_command_batch                       avgt    5      8.265 ±   4.266  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_deliver                             avgt    5      8.668 ±   4.614  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_deliver_batch                       avgt    5      9.716 ±   0.820  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_deny                                avgt    5      8.394 ±   3.061  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_deny_batch                          avgt    5     10.153 ±   0.988  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_explain                             avgt    5     12.054 ±   1.045  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_explain_batch                       avgt    5      7.345 ±   6.799  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_promise                             avgt    5      8.775 ±   4.159  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_promise_batch                       avgt    5      8.402 ±   6.323  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_report                              avgt    5      8.421 ±   4.011  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_report_batch                        avgt    5      8.704 ±   5.944  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_request                             avgt    5      9.010 ±   3.169  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_request_batch                       avgt    5      7.883 ±   4.723  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_sign                                avgt    5     10.208 ±   0.818  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_sign_batch                          avgt    5      8.337 ±   3.875  ns/op
i.h.serventis.jmh.opt.role.AgentOps.agent_from_conduit                       avgt    5      1.880 ±   0.124  ns/op
i.h.serventis.jmh.opt.role.AgentOps.agent_from_conduit_batch                 avgt    5      1.666 ±   0.011  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_accept                              avgt    5      9.976 ±   1.074  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_accept_batch                        avgt    5      8.673 ±   3.127  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_accepted                            avgt    5      8.899 ±   3.740  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_accepted_batch                      avgt    5      8.663 ±   5.312  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_breach                              avgt    5     10.181 ±   0.520  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_breach_batch                        avgt    5      9.521 ±   4.006  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_breached                            avgt    5      8.724 ±   3.011  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_breached_batch                      avgt    5      8.205 ±   4.022  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_depend                              avgt    5     10.364 ±   1.171  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_depend_batch                        avgt    5      7.798 ±   4.789  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_depended                            avgt    5     10.290 ±   0.749  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_depended_batch                      avgt    5      9.384 ±   4.836  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_fulfill                             avgt    5      8.427 ±   3.807  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_fulfill_batch                       avgt    5      9.761 ±   0.758  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_fulfilled                           avgt    5      8.704 ±   5.110  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_fulfilled_batch                     avgt    5      8.517 ±   4.201  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_inquire                             avgt    5     10.248 ±   0.500  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_inquire_batch                       avgt    5      8.223 ±   4.473  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_inquired                            avgt    5     10.286 ±   0.835  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_inquired_batch                      avgt    5      8.628 ±   3.315  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_observe                             avgt    5      8.835 ±   3.171  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_observe_batch                       avgt    5      8.202 ±   4.389  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_observed                            avgt    5      9.063 ±   5.841  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_observed_batch                      avgt    5      8.233 ±   3.949  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_offer                               avgt    5      9.702 ±   1.165  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_offer_batch                         avgt    5     10.303 ±   1.250  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_offered                             avgt    5     10.559 ±   1.134  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_offered_batch                       avgt    5     11.495 ±   1.028  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_promise                             avgt    5     10.248 ±   0.443  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_promise_batch                       avgt    5      8.430 ±   5.174  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_promised                            avgt    5      8.790 ±   2.787  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_promised_batch                      avgt    5      9.213 ±   3.109  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_retract                             avgt    5     10.810 ±   0.506  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_retract_batch                       avgt    5      8.590 ±   3.517  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_retracted                           avgt    5      9.819 ±   0.611  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_retracted_batch                     avgt    5     11.083 ±   0.665  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_signal                              avgt    5      8.083 ±   3.407  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_signal_batch                        avgt    5     10.096 ±   0.794  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_validate                            avgt    5      9.331 ±   0.472  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_validate_batch                      avgt    5      9.763 ±   4.456  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_validated                           avgt    5      8.539 ±   2.981  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_validated_batch                     avgt    5      9.675 ±   4.536  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_abandon                             avgt    5      9.671 ±   0.525  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_abandon_batch                       avgt    5      9.144 ±   0.996  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_arrive                              avgt    5      9.327 ±   0.972  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_arrive_batch                        avgt    5     10.387 ±   0.796  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_await                               avgt    5      8.626 ±   5.134  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_await_batch                         avgt    5     10.525 ±   0.684  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_release                             avgt    5      9.507 ±   8.383  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_release_batch                       avgt    5      8.336 ±   3.783  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_reset                               avgt    5     10.446 ±   0.311  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_reset_batch                         avgt    5      8.202 ±   7.258  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_sign                                avgt    5      8.666 ±   2.956  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_sign_batch                          avgt    5      8.311 ±   7.355  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_timeout                             avgt    5      8.179 ±   3.420  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_timeout_batch                       avgt    5      7.940 ±   3.319  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.latch_from_conduit                       avgt    5      1.852 ±   0.023  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.latch_from_conduit_batch                 avgt    5      1.662 ±   0.004  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_abandon                              avgt    5      8.010 ±   2.759  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_abandon_batch                        avgt    5      9.268 ±   1.287  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_acquire                              avgt    5      8.833 ±   3.452  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_acquire_batch                        avgt    5     10.246 ±   1.010  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_attempt                              avgt    5      8.886 ±   3.457  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_attempt_batch                        avgt    5      8.293 ±   4.259  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_contest                              avgt    5      8.906 ±   5.938  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_contest_batch                        avgt    5      8.332 ±   4.425  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_deny                                 avgt    5      8.613 ±   3.823  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_deny_batch                           avgt    5      7.998 ±   4.294  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_downgrade                            avgt    5     10.080 ±   0.577  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_downgrade_batch                      avgt    5      8.298 ±   4.456  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_grant                                avgt    5     10.549 ±   0.649  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_grant_batch                          avgt    5      9.532 ±   1.540  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_release                              avgt    5     10.555 ±   0.945  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_release_batch                        avgt    5     10.120 ±   1.180  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_sign                                 avgt    5     10.006 ±   1.118  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_sign_batch                           avgt    5      9.489 ±   6.311  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_timeout                              avgt    5     10.933 ±   0.445  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_timeout_batch                        avgt    5      9.757 ±   0.530  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_upgrade                              avgt    5      8.893 ±   4.313  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_upgrade_batch                        avgt    5      8.331 ±   3.944  ns/op
i.h.serventis.jmh.opt.sync.LockOps.lock_from_conduit                         avgt    5      1.867 ±   0.055  ns/op
i.h.serventis.jmh.opt.sync.LockOps.lock_from_conduit_batch                   avgt    5      1.658 ±   0.005  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.counter_from_conduit                   avgt    5      1.861 ±   0.052  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.counter_from_conduit_batch             avgt    5      1.659 ±   0.006  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_increment                         avgt    5     10.392 ±   9.026  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_increment_batch                   avgt    5      8.287 ±   0.691  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_overflow                          avgt    5      8.856 ±   4.576  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_overflow_batch                    avgt    5      8.686 ±   7.704  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_reset                             avgt    5     10.626 ±   0.350  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_reset_batch                       avgt    5     10.058 ±   0.583  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_sign                              avgt    5      9.112 ±   5.001  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_sign_batch                        avgt    5      9.966 ±   0.881  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_decrement                           avgt    5      9.885 ±   0.210  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_decrement_batch                     avgt    5     10.059 ±   0.493  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_increment                           avgt    5     10.560 ±   0.372  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_increment_batch                     avgt    5     10.366 ±   0.372  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_overflow                            avgt    5      8.571 ±   4.627  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_overflow_batch                      avgt    5     10.089 ±   0.566  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_reset                               avgt    5      8.721 ±   6.183  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_reset_batch                         avgt    5      8.353 ±   7.477  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_sign                                avgt    5      8.406 ±   3.649  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_sign_batch                          avgt    5      8.811 ±   6.172  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_underflow                           avgt    5      8.876 ±   5.948  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_underflow_batch                     avgt    5     10.045 ±   0.959  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.gauge_from_conduit                       avgt    5      1.860 ±   0.043  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.gauge_from_conduit_batch                 avgt    5      1.661 ±   0.010  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_debug                                 avgt    5      9.383 ±   0.995  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_debug_batch                           avgt    5     10.081 ±   0.580  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_info                                  avgt    5     11.385 ±   0.874  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_info_batch                            avgt    5      9.947 ±   0.515  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_severe                                avgt    5      9.922 ±   0.593  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_severe_batch                          avgt    5     10.444 ±   0.840  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_sign                                  avgt    5      8.654 ±   4.805  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_sign_batch                            avgt    5      8.757 ±   3.898  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_warning                               avgt    5      8.642 ±   3.327  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_warning_batch                         avgt    5      9.255 ±   0.837  ns/op
i.h.serventis.jmh.opt.tool.LogOps.log_from_conduit                           avgt    5      1.876 ±   0.063  ns/op
i.h.serventis.jmh.opt.tool.LogOps.log_from_conduit_batch                     avgt    5      1.661 ±   0.011  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_connect                             avgt    5      8.350 ±   4.023  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_connect_batch                       avgt    5      9.466 ±   0.585  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_connected                           avgt    5     10.380 ±   0.314  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_connected_batch                     avgt    5      9.053 ±   4.312  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_disconnect                          avgt    5      8.365 ±   3.464  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_disconnect_batch                    avgt    5      9.820 ±   0.420  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_disconnected                        avgt    5     10.579 ±   0.315  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_disconnected_batch                  avgt    5      8.404 ±   3.434  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_fail                                avgt    5      9.753 ±   0.689  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_fail_batch                          avgt    5      9.720 ±   0.490  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_failed                              avgt    5      8.532 ±   4.435  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_failed_batch                        avgt    5      9.297 ±   0.392  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_process                             avgt    5      8.825 ±   5.356  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_process_batch                       avgt    5      9.452 ±   0.515  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_processed                           avgt    5     11.247 ±   0.652  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_processed_batch                     avgt    5      9.763 ±   0.605  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_receive_batch                       avgt    5      9.557 ±   4.078  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_received_batch                      avgt    5     10.173 ±   0.627  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_signal                              avgt    5      8.544 ±   3.476  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_signal_batch                        avgt    5      9.690 ±   3.236  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_succeed                             avgt    5      8.444 ±   3.519  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_succeed_batch                       avgt    5     10.509 ±   0.720  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_succeeded                           avgt    5      9.476 ±   0.725  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_succeeded_batch                     avgt    5      8.357 ±   3.534  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transfer                            avgt    5      9.919 ±   0.535  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transfer_inbound                    avgt    5      8.790 ±   4.013  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transfer_outbound                   avgt    5      8.654 ±   3.545  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transferred                         avgt    5      8.771 ±   4.029  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transmit_batch                      avgt    5      8.494 ±   5.017  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transmitted_batch                   avgt    5     10.400 ±   0.590  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.probe_from_conduit                       avgt    5      1.877 ±   0.048  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.probe_from_conduit_batch                 avgt    5      1.661 ±   0.010  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_baseline                     avgt    5     10.787 ±   0.932  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_baseline_batch               avgt    5     10.124 ±   0.351  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_target                       avgt    5      8.962 ±   4.648  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_target_batch                 avgt    5     10.244 ±   0.583  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_threshold                    avgt    5      8.897 ±   2.856  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_threshold_batch              avgt    5     10.033 ±   0.307  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_baseline                     avgt    5      8.324 ±   3.511  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_baseline_batch               avgt    5      9.185 ±   2.543  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_target                       avgt    5      9.718 ±   0.781  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_target_batch                 avgt    5      7.686 ±   3.980  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_threshold                    avgt    5      8.685 ±   4.001  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_threshold_batch              avgt    5     11.367 ±   0.985  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_baseline                   avgt    5      9.706 ±   0.495  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_baseline_batch             avgt    5      9.774 ±   3.063  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_target                     avgt    5      8.879 ±   3.076  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_target_batch               avgt    5     10.497 ±   1.180  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_threshold                  avgt    5      9.678 ±   1.001  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_threshold_batch            avgt    5     10.182 ±   0.433  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_signal                             avgt    5      9.728 ±   0.831  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_signal_batch                       avgt    5      8.354 ±   4.443  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.sensor_from_conduit                     avgt    5      1.864 ±   0.077  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.sensor_from_conduit_batch               avgt    5      1.659 ±   0.002  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_mixed_pattern                         avgt    5      0.226 ±   0.001  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_single                                avgt    5      0.753 ±   0.022  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_single_batch                          avgt    5      0.019 ±   0.001  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_varied_batch                          avgt    5      1.515 ±   0.001  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_worst_case                            avgt    5      1.182 ±   0.001  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_critical                             avgt    5      9.062 ±   3.776  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_critical_batch                       avgt    5     10.131 ±   5.687  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_normal                               avgt    5      8.348 ±   4.155  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_normal_batch                         avgt    5      8.513 ±   3.930  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_signal                               avgt    5      9.368 ±   0.518  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_signal_batch                         avgt    5      7.947 ±   4.591  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_warning                              avgt    5      9.928 ±   0.619  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_warning_batch                        avgt    5      8.462 ±   5.479  ns/op
i.h.serventis.jmh.sdk.SituationOps.situation_from_conduit                    avgt    5      1.861 ±   0.013  ns/op
i.h.serventis.jmh.sdk.SituationOps.situation_from_conduit_batch              avgt    5      1.662 ±   0.010  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_converging_confirmed                    avgt    5      8.941 ±   3.474  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_converging_confirmed_batch              avgt    5      9.566 ±   3.765  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_defective_tentative                     avgt    5      9.914 ±   0.416  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_defective_tentative_batch               avgt    5     11.426 ±   0.663  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_degraded_measured                       avgt    5      8.683 ±   3.708  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_degraded_measured_batch                 avgt    5      8.366 ±   3.793  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_down_confirmed                          avgt    5      8.736 ±   3.080  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_down_confirmed_batch                    avgt    5      8.392 ±   7.586  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_signal                                  avgt    5      9.630 ±   0.748  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_signal_batch                            avgt    5     10.482 ±   1.268  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_stable_confirmed                        avgt    5      9.679 ±   0.738  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_stable_confirmed_batch                  avgt    5     10.089 ±   0.413  ns/op
i.h.serventis.jmh.sdk.StatusOps.status_from_conduit                          avgt    5      1.860 ±   0.029  ns/op
i.h.serventis.jmh.sdk.StatusOps.status_from_conduit_batch                    avgt    5      1.661 ±   0.010  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_alarm_flow                              avgt    5     10.995 ±   0.946  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_alarm_flow_batch                        avgt    5      9.409 ±   2.795  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_fault_link                              avgt    5      8.622 ±   2.852  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_fault_link_batch                        avgt    5     11.058 ±   0.631  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_limit_time                              avgt    5      8.999 ±   3.150  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_limit_time_batch                        avgt    5      8.022 ±   3.627  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_normal_space                            avgt    5     10.749 ±   0.661  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_normal_space_batch                      avgt    5      9.629 ±   5.620  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_signal                                  avgt    5      9.746 ±   0.675  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_signal_batch                            avgt    5      9.338 ±   5.437  ns/op
i.h.serventis.jmh.sdk.SystemOps.system_from_conduit                          avgt    5      1.867 ±   0.070  ns/op
i.h.serventis.jmh.sdk.SystemOps.system_from_conduit_batch                    avgt    5      1.663 ±   0.020  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.cycle_from_conduit                       avgt    5      1.870 ±   0.053  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.cycle_from_conduit_batch                 avgt    5      1.660 ±   0.005  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_repeat                              avgt    5      8.788 ±   3.132  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_repeat_batch                        avgt    5      8.620 ±   3.175  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_return                              avgt    5      8.518 ±   3.098  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_return_batch                        avgt    5      9.857 ±   0.910  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_signal                              avgt    5      8.293 ±   4.021  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_signal_batch                        avgt    5      8.067 ±   3.700  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_single                              avgt    5      8.471 ±   3.364  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_single_batch                        avgt    5      9.999 ±   0.588  ns/op
i.h.substrates.jmh.CircuitOps.conduit_create_close                           avgt    5    281.708 ±  55.904  ns/op
i.h.substrates.jmh.CircuitOps.conduit_create_named                           avgt    5    282.005 ± 153.225  ns/op
i.h.substrates.jmh.CircuitOps.conduit_create_with_flow                       avgt    5    280.091 ± 113.715  ns/op
i.h.substrates.jmh.CircuitOps.create_and_close                               avgt    5    337.343 ±  54.952  ns/op
i.h.substrates.jmh.CircuitOps.create_await_close                             avgt    5  10730.724 ± 906.552  ns/op
i.h.substrates.jmh.CircuitOps.hot_await_queue_drain                          avgt    5   5798.639 ± 153.830  ns/op
i.h.substrates.jmh.CircuitOps.hot_conduit_create                             avgt    5     19.065 ±   0.082  ns/op
i.h.substrates.jmh.CircuitOps.hot_conduit_create_named                       avgt    5     19.071 ±   0.119  ns/op
i.h.substrates.jmh.CircuitOps.hot_conduit_create_with_flow                   avgt    5     21.887 ±   0.084  ns/op
i.h.substrates.jmh.CircuitOps.hot_pipe_async                                 avgt    5      8.531 ±   2.061  ns/op
i.h.substrates.jmh.CircuitOps.hot_pipe_async_with_flow                       avgt    5     10.679 ±   3.505  ns/op
i.h.substrates.jmh.CircuitOps.pipe_async                                     avgt    5    309.065 ± 136.596  ns/op
i.h.substrates.jmh.CircuitOps.pipe_async_with_flow                           avgt    5    320.440 ± 207.086  ns/op
i.h.substrates.jmh.ConduitOps.get_by_name                                    avgt    5      1.882 ±   0.082  ns/op
i.h.substrates.jmh.ConduitOps.get_by_name_batch                              avgt    5      1.659 ±   0.008  ns/op
i.h.substrates.jmh.ConduitOps.get_by_substrate                               avgt    5      1.991 ±   0.043  ns/op
i.h.substrates.jmh.ConduitOps.get_by_substrate_batch                         avgt    5      1.811 ±   0.006  ns/op
i.h.substrates.jmh.ConduitOps.get_cached                                     avgt    5      3.426 ±   0.022  ns/op
i.h.substrates.jmh.ConduitOps.get_cached_batch                               avgt    5      3.302 ±   0.008  ns/op
i.h.substrates.jmh.ConduitOps.subscribe                                      avgt    5    436.561 ± 240.814  ns/op
i.h.substrates.jmh.ConduitOps.subscribe_batch                                avgt    5    461.728 ± 112.402  ns/op
i.h.substrates.jmh.ConduitOps.subscribe_with_emission_await                  avgt    5   5644.470 ± 202.524  ns/op
i.h.substrates.jmh.CortexOps.circuit                                         avgt    5    279.172 ± 215.570  ns/op
i.h.substrates.jmh.CortexOps.circuit_batch                                   avgt    5    280.103 ± 115.893  ns/op
i.h.substrates.jmh.CortexOps.circuit_named                                   avgt    5    288.306 ± 182.175  ns/op
i.h.substrates.jmh.CortexOps.current                                         avgt    5      1.090 ±   0.028  ns/op
i.h.substrates.jmh.CortexOps.name_class                                      avgt    5      1.479 ±   0.026  ns/op
i.h.substrates.jmh.CortexOps.name_enum                                       avgt    5      2.805 ±   0.014  ns/op
i.h.substrates.jmh.CortexOps.name_iterable                                   avgt    5     11.234 ±   0.044  ns/op
i.h.substrates.jmh.CortexOps.name_path                                       avgt    5      1.893 ±   0.005  ns/op
i.h.substrates.jmh.CortexOps.name_path_batch                                 avgt    5      1.686 ±   0.001  ns/op
i.h.substrates.jmh.CortexOps.name_string                                     avgt    5      2.847 ±   0.005  ns/op
i.h.substrates.jmh.CortexOps.name_string_batch                               avgt    5      2.540 ±   0.333  ns/op
i.h.substrates.jmh.CortexOps.scope                                           avgt    5      9.284 ±   1.642  ns/op
i.h.substrates.jmh.CortexOps.scope_batch                                     avgt    5      7.582 ±   0.500  ns/op
i.h.substrates.jmh.CortexOps.scope_named                                     avgt    5      8.013 ±   0.027  ns/op
i.h.substrates.jmh.CortexOps.slot_boolean                                    avgt    5      2.413 ±   0.345  ns/op
i.h.substrates.jmh.CortexOps.slot_double                                     avgt    5      2.431 ±   0.116  ns/op
i.h.substrates.jmh.CortexOps.slot_int                                        avgt    5      2.121 ±   1.092  ns/op
i.h.substrates.jmh.CortexOps.slot_long                                       avgt    5      2.423 ±   0.090  ns/op
i.h.substrates.jmh.CortexOps.slot_string                                     avgt    5      2.429 ±   0.120  ns/op
i.h.substrates.jmh.CortexOps.state_empty                                     avgt    5      0.440 ±   0.004  ns/op
i.h.substrates.jmh.CortexOps.state_empty_batch                               avgt    5     ≈ 10⁻³            ns/op
i.h.substrates.jmh.FlowOps.baseline_no_flow_await                            avgt    5     17.812 ±   0.382  ns/op
i.h.substrates.jmh.FlowOps.flow_combined_diff_guard_await                    avgt    5     29.960 ±   2.233  ns/op
i.h.substrates.jmh.FlowOps.flow_combined_diff_sample_await                   avgt    5     19.352 ±   0.495  ns/op
i.h.substrates.jmh.FlowOps.flow_combined_guard_limit_await                   avgt    5     28.343 ±   1.135  ns/op
i.h.substrates.jmh.FlowOps.flow_diff_await                                   avgt    5     30.039 ±   2.665  ns/op
i.h.substrates.jmh.FlowOps.flow_guard_await                                  avgt    5     30.370 ±   4.408  ns/op
i.h.substrates.jmh.FlowOps.flow_limit_await                                  avgt    5     28.668 ±   1.403  ns/op
i.h.substrates.jmh.FlowOps.flow_sample_await                                 avgt    5     17.245 ±   0.235  ns/op
i.h.substrates.jmh.FlowOps.flow_sift_await                                   avgt    5     18.680 ±   0.320  ns/op
i.h.substrates.jmh.NameOps.name_chained_deep                                 avgt    5     16.929 ±   0.065  ns/op
i.h.substrates.jmh.NameOps.name_chaining                                     avgt    5      8.557 ±   0.608  ns/op
i.h.substrates.jmh.NameOps.name_chaining_batch                               avgt    5      9.043 ±   0.008  ns/op
i.h.substrates.jmh.NameOps.name_compare                                      avgt    5     33.066 ±   0.205  ns/op
i.h.substrates.jmh.NameOps.name_compare_batch                                avgt    5     31.976 ±   1.330  ns/op
i.h.substrates.jmh.NameOps.name_depth                                        avgt    5      1.698 ±   0.013  ns/op
i.h.substrates.jmh.NameOps.name_depth_batch                                  avgt    5      1.399 ±   0.196  ns/op
i.h.substrates.jmh.NameOps.name_enclosure                                    avgt    5      0.589 ±   0.001  ns/op
i.h.substrates.jmh.NameOps.name_from_enum                                    avgt    5      2.830 ±   0.030  ns/op
i.h.substrates.jmh.NameOps.name_from_iterable                                avgt    5     11.987 ±   1.252  ns/op
i.h.substrates.jmh.NameOps.name_from_iterator                                avgt    5     12.911 ±   0.065  ns/op
i.h.substrates.jmh.NameOps.name_from_mapped_iterable                         avgt    5     11.695 ±   0.016  ns/op
i.h.substrates.jmh.NameOps.name_from_name                                    avgt    5      4.218 ±   0.002  ns/op
i.h.substrates.jmh.NameOps.name_from_string                                  avgt    5      3.049 ±   0.011  ns/op
i.h.substrates.jmh.NameOps.name_from_string_batch                            avgt    5      2.803 ±   0.009  ns/op
i.h.substrates.jmh.NameOps.name_interning_chained                            avgt    5     12.413 ±   0.257  ns/op
i.h.substrates.jmh.NameOps.name_interning_same_path                          avgt    5      3.543 ±   0.007  ns/op
i.h.substrates.jmh.NameOps.name_interning_segments                           avgt    5     10.105 ±   0.866  ns/op
i.h.substrates.jmh.NameOps.name_iterate_hierarchy                            avgt    5      1.797 ±   0.010  ns/op
i.h.substrates.jmh.NameOps.name_parsing                                      avgt    5      1.889 ±   0.007  ns/op
i.h.substrates.jmh.NameOps.name_parsing_batch                                avgt    5      1.682 ±   0.009  ns/op
i.h.substrates.jmh.NameOps.name_path_generation                              avgt    5     31.341 ±   0.861  ns/op
i.h.substrates.jmh.NameOps.name_path_generation_batch                        avgt    5     30.005 ±   4.754  ns/op
i.h.substrates.jmh.PipeOps.async_emit_batch                                  avgt    5     11.836 ±   1.631  ns/op
i.h.substrates.jmh.PipeOps.async_emit_batch_await                            avgt    5     16.872 ±   0.527  ns/op
i.h.substrates.jmh.PipeOps.async_emit_chained_await                          avgt    5     16.932 ±   0.226  ns/op
i.h.substrates.jmh.PipeOps.async_emit_fanout_await                           avgt    5     18.715 ±   0.384  ns/op
i.h.substrates.jmh.PipeOps.async_emit_single                                 avgt    5     10.649 ±   0.862  ns/op
i.h.substrates.jmh.PipeOps.async_emit_single_await                           avgt    5   5477.579 ± 311.539  ns/op
i.h.substrates.jmh.PipeOps.async_emit_with_flow_await                        avgt    5     21.224 ±   0.959  ns/op
i.h.substrates.jmh.PipeOps.baseline_blackhole                                avgt    5      0.267 ±   0.001  ns/op
i.h.substrates.jmh.PipeOps.baseline_counter                                  avgt    5      1.621 ±   0.026  ns/op
i.h.substrates.jmh.PipeOps.baseline_receptor                                 avgt    5      0.263 ±   0.002  ns/op
i.h.substrates.jmh.PipeOps.pipe_create                                       avgt    5      8.747 ±   1.497  ns/op
i.h.substrates.jmh.PipeOps.pipe_create_chained                               avgt    5      0.855 ±   0.114  ns/op
i.h.substrates.jmh.PipeOps.pipe_create_with_flow                             avgt    5     13.230 ±   1.994  ns/op
i.h.substrates.jmh.ReservoirOps.baseline_emit_no_reservoir_await             avgt    5     96.224 ±   2.906  ns/op
i.h.substrates.jmh.ReservoirOps.baseline_emit_no_reservoir_await_batch       avgt    5     18.455 ±   1.228  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_burst_then_drain_await             avgt    5     90.187 ±   6.150  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_burst_then_drain_await_batch       avgt    5     28.812 ±   0.806  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_drain_await                        avgt    5     93.801 ±  10.149  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_drain_await_batch                  avgt    5     28.342 ±   0.270  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_emit_drain_cycles_await            avgt    5    328.063 ±  26.334  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_emit_with_capture_await            avgt    5     79.974 ±   1.536  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_emit_with_capture_await_batch      avgt    5     23.855 ±   0.312  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_process_emissions_await            avgt    5     89.081 ±  30.649  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_process_emissions_await_batch      avgt    5     26.140 ±   0.447  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_process_subjects_await             avgt    5     97.454 ±   2.443  ns/op
i.h.substrates.jmh.ScopeOps.scope_child_anonymous                            avgt    5     18.235 ±   8.403  ns/op
i.h.substrates.jmh.ScopeOps.scope_child_anonymous_batch                      avgt    5     17.698 ±   0.260  ns/op
i.h.substrates.jmh.ScopeOps.scope_child_named                                avgt    5     17.100 ±   0.200  ns/op
i.h.substrates.jmh.ScopeOps.scope_child_named_batch                          avgt    5     19.440 ±   3.778  ns/op
i.h.substrates.jmh.ScopeOps.scope_close_idempotent                           avgt    5      2.394 ±   0.013  ns/op
i.h.substrates.jmh.ScopeOps.scope_close_idempotent_batch                     avgt    5      0.033 ±   0.001  ns/op
i.h.substrates.jmh.ScopeOps.scope_closure                                    avgt    5    286.103 ±  48.792  ns/op
i.h.substrates.jmh.ScopeOps.scope_closure_batch                              avgt    5    307.355 ±  61.652  ns/op
i.h.substrates.jmh.ScopeOps.scope_complex                                    avgt    5    917.038 ± 164.623  ns/op
i.h.substrates.jmh.ScopeOps.scope_create_and_close                           avgt    5      2.426 ±   0.036  ns/op
i.h.substrates.jmh.ScopeOps.scope_create_and_close_batch                     avgt    5      0.034 ±   0.001  ns/op
i.h.substrates.jmh.ScopeOps.scope_create_named                               avgt    5      2.434 ±   0.024  ns/op
i.h.substrates.jmh.ScopeOps.scope_create_named_batch                         avgt    5      0.033 ±   0.001  ns/op
i.h.substrates.jmh.ScopeOps.scope_hierarchy                                  avgt    5     27.336 ±   0.307  ns/op
i.h.substrates.jmh.ScopeOps.scope_hierarchy_batch                            avgt    5     26.552 ±   0.170  ns/op
i.h.substrates.jmh.ScopeOps.scope_parent_closes_children                     avgt    5     43.455 ±   0.118  ns/op
i.h.substrates.jmh.ScopeOps.scope_parent_closes_children_batch               avgt    5     42.294 ±   2.868  ns/op
i.h.substrates.jmh.ScopeOps.scope_register_multiple                          avgt    5   1450.490 ± 612.415  ns/op
i.h.substrates.jmh.ScopeOps.scope_register_multiple_batch                    avgt    5   1397.956 ± 299.990  ns/op
i.h.substrates.jmh.ScopeOps.scope_register_single                            avgt    5    287.139 ±  56.546  ns/op
i.h.substrates.jmh.ScopeOps.scope_register_single_batch                      avgt    5    283.085 ± 148.326  ns/op
i.h.substrates.jmh.ScopeOps.scope_with_resources                             avgt    5    581.868 ± 105.389  ns/op
i.h.substrates.jmh.StateOps.slot_name                                        avgt    5      0.523 ±   0.001  ns/op
i.h.substrates.jmh.StateOps.slot_name_batch                                  avgt    5      0.001 ±   0.001  ns/op
i.h.substrates.jmh.StateOps.slot_type                                        avgt    5      0.443 ±   0.001  ns/op
i.h.substrates.jmh.StateOps.slot_value                                       avgt    5      0.662 ±   0.001  ns/op
i.h.substrates.jmh.StateOps.slot_value_batch                                 avgt    5      0.001 ±   0.001  ns/op
i.h.substrates.jmh.StateOps.state_compact                                    avgt    5     10.294 ±   0.114  ns/op
i.h.substrates.jmh.StateOps.state_compact_batch                              avgt    5     10.699 ±   0.197  ns/op
i.h.substrates.jmh.StateOps.state_iterate_slots                              avgt    5      2.156 ±   0.002  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_int                               avgt    5      4.759 ±   0.237  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_int_batch                         avgt    5      4.886 ±   0.401  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_long                              avgt    5      4.751 ±   0.794  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_object                            avgt    5      2.563 ±   0.528  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_object_batch                      avgt    5      2.427 ±   0.209  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_string                            avgt    5      4.728 ±   0.227  ns/op
i.h.substrates.jmh.StateOps.state_value_read                                 avgt    5      1.486 ±   0.001  ns/op
i.h.substrates.jmh.StateOps.state_value_read_batch                           avgt    5      1.267 ±   0.001  ns/op
i.h.substrates.jmh.StateOps.state_values_stream                              avgt    5      4.980 ±   0.346  ns/op
i.h.substrates.jmh.SubscriberOps.close_five_conduits_await                   avgt    5   8696.402 ± 415.382  ns/op
i.h.substrates.jmh.SubscriberOps.close_five_subscriptions_await              avgt    5   8630.902 ± 441.458  ns/op
i.h.substrates.jmh.SubscriberOps.close_idempotent_await                      avgt    5   8438.475 ± 621.175  ns/op
i.h.substrates.jmh.SubscriberOps.close_idempotent_batch_await                avgt    5     17.232 ±   0.575  ns/op
i.h.substrates.jmh.SubscriberOps.close_no_subscriptions_await                avgt    5   8450.164 ± 745.198  ns/op
i.h.substrates.jmh.SubscriberOps.close_no_subscriptions_batch_await          avgt    5     14.235 ±   1.423  ns/op
i.h.substrates.jmh.SubscriberOps.close_one_subscription_await                avgt    5   8437.659 ± 165.712  ns/op
i.h.substrates.jmh.SubscriberOps.close_one_subscription_batch_await          avgt    5     34.879 ±   0.184  ns/op
i.h.substrates.jmh.SubscriberOps.close_ten_conduits_await                    avgt    5   8514.559 ± 330.669  ns/op
i.h.substrates.jmh.SubscriberOps.close_ten_subscriptions_await               avgt    5   8726.960 ± 366.162  ns/op
i.h.substrates.jmh.SubscriberOps.close_with_pending_emissions_await          avgt    5   8713.313 ± 711.829  ns/op
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