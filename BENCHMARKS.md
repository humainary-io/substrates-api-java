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
i.h.serventis.jmh.opt.data.CacheOps.cache_from_conduit                       avgt    5      1.779 ±    0.059  ns/op
i.h.serventis.jmh.opt.data.CacheOps.cache_from_conduit_batch                 avgt    5      1.606 ±    0.030  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_evict                               avgt    5     11.076 ±    0.536  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_evict_batch                         avgt    5      8.631 ±    6.964  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_expire                              avgt    5      9.054 ±    5.987  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_expire_batch                        avgt    5      8.432 ±    7.713  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_hit                                 avgt    5     11.454 ±    0.558  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_hit_batch                           avgt    5     10.891 ±    0.496  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_lookup                              avgt    5      9.127 ±    3.595  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_lookup_batch                        avgt    5      8.350 ±    5.303  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_miss                                avgt    5      8.940 ±    3.710  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_miss_batch                          avgt    5     10.948 ±    0.583  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_remove                              avgt    5      8.582 ±    4.695  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_remove_batch                        avgt    5      8.567 ±    4.190  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_sign                                avgt    5      8.545 ±    8.489  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_sign_batch                          avgt    5     11.172 ±    0.690  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_store                               avgt    5      9.165 ±    6.516  ns/op
i.h.serventis.jmh.opt.data.CacheOps.emit_store_batch                         avgt    5     10.016 ±    0.780  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_aggregate                        avgt    5      8.850 ±    4.877  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_aggregate_batch                  avgt    5      8.810 ±    3.500  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_backpressure                     avgt    5      8.508 ±    4.243  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_backpressure_batch               avgt    5      9.887 ±    0.673  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_buffer                           avgt    5     10.866 ±    0.587  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_buffer_batch                     avgt    5     10.816 ±    0.748  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_checkpoint                       avgt    5      8.777 ±    4.016  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_checkpoint_batch                 avgt    5     10.208 ±    0.528  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_filter                           avgt    5     10.637 ±    0.482  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_filter_batch                     avgt    5      8.359 ±    8.406  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_input                            avgt    5      8.987 ±    1.202  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_input_batch                      avgt    5      8.843 ±    0.887  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_lag                              avgt    5      8.599 ±    6.047  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_lag_batch                        avgt    5      7.893 ±    5.238  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_output                           avgt    5      9.161 ±    0.766  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_output_batch                     avgt    5      9.099 ±    0.568  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_overflow                         avgt    5     10.039 ±    0.435  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_overflow_batch                   avgt    5      8.324 ±    5.917  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_sign                             avgt    5      8.121 ±    3.994  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_sign_batch                       avgt    5      8.113 ±    4.093  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_skip                             avgt    5     10.978 ±    3.377  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_skip_batch                       avgt    5     10.459 ±    0.805  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_transform                        avgt    5      9.775 ±    0.733  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_transform_batch                  avgt    5      8.221 ±    9.328  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_watermark                        avgt    5     11.491 ±    1.557  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.emit_watermark_batch                  avgt    5      9.093 ±    0.359  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_flow_etl                     avgt    5     56.973 ±    4.918  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_flow_stream                  avgt    5     48.539 ±   31.478  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_flow_windowed                avgt    5     46.850 ±   26.870  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_from_conduit                 avgt    5      1.864 ±    0.072  ns/op
i.h.serventis.jmh.opt.data.PipelineOps.pipeline_from_conduit_batch           avgt    5      1.660 ±    0.005  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_dequeue                             avgt    5      8.045 ±    2.977  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_dequeue_batch                       avgt    5      8.763 ±    7.526  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_enqueue                             avgt    5     11.903 ±    0.965  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_enqueue_batch                       avgt    5      8.494 ±    4.616  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_overflow                            avgt    5     10.172 ±    0.515  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_overflow_batch                      avgt    5      9.837 ±    0.890  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_sign                                avgt    5      8.707 ±    5.056  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_sign_batch                          avgt    5     10.225 ±    0.686  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_underflow                           avgt    5      8.654 ±    3.328  ns/op
i.h.serventis.jmh.opt.data.QueueOps.emit_underflow_batch                     avgt    5     10.314 ±    0.659  ns/op
i.h.serventis.jmh.opt.data.QueueOps.queue_from_conduit                       avgt    5      1.854 ±    0.019  ns/op
i.h.serventis.jmh.opt.data.QueueOps.queue_from_conduit_batch                 avgt    5      1.661 ±    0.010  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_overflow                            avgt    5      8.766 ±    5.774  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_overflow_batch                      avgt    5     10.414 ±    0.722  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_pop                                 avgt    5     10.278 ±    0.835  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_pop_batch                           avgt    5      9.307 ±    0.452  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_push                                avgt    5     10.862 ±    0.768  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_push_batch                          avgt    5      9.291 ±    0.969  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_sign                                avgt    5      8.835 ±    5.283  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_sign_batch                          avgt    5      9.979 ±    1.437  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_underflow                           avgt    5      8.556 ±    4.195  ns/op
i.h.serventis.jmh.opt.data.StackOps.emit_underflow_batch                     avgt    5      8.233 ±    3.128  ns/op
i.h.serventis.jmh.opt.data.StackOps.stack_from_conduit                       avgt    5      1.876 ±    0.053  ns/op
i.h.serventis.jmh.opt.data.StackOps.stack_from_conduit_batch                 avgt    5      1.660 ±    0.002  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_crash                             avgt    5      9.577 ±    0.847  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_crash_batch                       avgt    5      7.684 ±    4.719  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_fail                              avgt    5      9.005 ±    4.337  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_fail_batch                        avgt    5      8.497 ±    8.077  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_kill                              avgt    5      8.433 ±    4.233  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_kill_batch                        avgt    5      9.366 ±    0.486  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_restart                           avgt    5     10.239 ±    0.708  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_restart_batch                     avgt    5     10.421 ±    0.731  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_resume                            avgt    5      8.314 ±    4.039  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_resume_batch                      avgt    5      9.446 ±    2.942  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_sign                              avgt    5      8.492 ±    3.820  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_sign_batch                        avgt    5      8.411 ±    4.625  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_spawn                             avgt    5      9.572 ±    0.643  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_spawn_batch                       avgt    5      8.424 ±    3.803  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_start                             avgt    5      9.937 ±    0.687  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_start_batch                       avgt    5      8.234 ±    3.684  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_stop                              avgt    5      8.684 ±    4.530  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_stop_batch                        avgt    5      8.185 ±    6.399  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_suspend                           avgt    5      9.674 ±    0.563  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.emit_suspend_batch                     avgt    5      8.101 ±    5.740  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.process_from_conduit                   avgt    5      1.864 ±    0.078  ns/op
i.h.serventis.jmh.opt.exec.ProcessOps.process_from_conduit_batch             avgt    5      1.660 ±    0.002  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_call                              avgt    5      9.884 ±    0.352  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_call_batch                        avgt    5      8.863 ±    5.785  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_called                            avgt    5     10.548 ±    0.512  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_called_batch                      avgt    5      7.411 ±    8.284  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_delay                             avgt    5     10.477 ±    0.718  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_delay_batch                       avgt    5      8.532 ±    6.496  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_delayed                           avgt    5      8.715 ±    4.751  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_delayed_batch                     avgt    5      9.822 ±    2.748  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_discard                           avgt    5     10.687 ±    1.032  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_discard_batch                     avgt    5      8.104 ±    2.809  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_discarded                         avgt    5      8.772 ±    3.259  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_discarded_batch                   avgt    5      9.611 ±    0.948  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_disconnect                        avgt    5      8.209 ±    3.390  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_disconnect_batch                  avgt    5      9.450 ±    3.362  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_disconnected                      avgt    5      9.828 ±    0.479  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_disconnected_batch                avgt    5      6.851 ±    9.322  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_expire                            avgt    5      8.447 ±    2.658  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_expire_batch                      avgt    5      8.564 ±    3.717  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_expired                           avgt    5      9.963 ±    0.381  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_expired_batch                     avgt    5      8.367 ±    3.599  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_fail                              avgt    5     11.884 ±    0.461  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_fail_batch                        avgt    5      8.650 ±    4.087  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_failed                            avgt    5      9.275 ±    0.566  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_failed_batch                      avgt    5      8.345 ±    4.493  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_recourse                          avgt    5     10.631 ±    0.885  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_recourse_batch                    avgt    5      9.054 ±    2.841  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_recoursed                         avgt    5      8.411 ±    4.330  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_recoursed_batch                   avgt    5     10.053 ±    0.222  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_redirect                          avgt    5      8.752 ±    3.362  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_redirect_batch                    avgt    5      9.400 ±    1.000  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_redirected                        avgt    5      9.001 ±    5.133  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_redirected_batch                  avgt    5      9.540 ±    3.139  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_reject                            avgt    5      8.165 ±    4.625  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_reject_batch                      avgt    5     11.656 ±    0.819  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_rejected                          avgt    5      9.014 ±    0.664  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_rejected_batch                    avgt    5     10.987 ±    0.292  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_resume                            avgt    5      8.172 ±    3.756  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_resume_batch                      avgt    5      8.726 ±    3.254  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_resumed                           avgt    5     10.635 ±    0.423  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_resumed_batch                     avgt    5     10.442 ±    0.619  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_retried                           avgt    5      9.536 ±    0.531  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_retried_batch                     avgt    5      9.523 ±    3.169  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_retry                             avgt    5      9.700 ±    0.476  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_retry_batch                       avgt    5      9.890 ±    5.726  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_schedule                          avgt    5      8.275 ±    4.127  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_schedule_batch                    avgt    5     10.453 ±    1.037  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_scheduled                         avgt    5      9.686 ±    0.605  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_scheduled_batch                   avgt    5     11.150 ±    1.144  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_signal                            avgt    5     10.651 ±    0.581  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_signal_batch                      avgt    5      8.324 ±    4.120  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_start                             avgt    5     10.638 ±    0.695  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_start_batch                       avgt    5      9.597 ±    5.501  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_started                           avgt    5      9.302 ±    0.142  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_started_batch                     avgt    5      6.659 ±    9.081  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_stop                              avgt    5      9.434 ±    0.336  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_stop_batch                        avgt    5     10.317 ±    0.964  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_stopped                           avgt    5     10.862 ±    3.317  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_stopped_batch                     avgt    5      9.141 ±    3.221  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_succeeded                         avgt    5      8.541 ±    3.983  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_succeeded_batch                   avgt    5      9.619 ±    0.929  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_success                           avgt    5      8.353 ±    7.725  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_success_batch                     avgt    5      8.396 ±    3.336  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_suspend                           avgt    5     10.252 ±    0.535  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_suspend_batch                     avgt    5     10.478 ±    3.362  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_suspended                         avgt    5      9.736 ±    0.673  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.emit_suspended_batch                   avgt    5      9.229 ±    4.505  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.service_from_conduit                   avgt    5      1.870 ±    0.069  ns/op
i.h.serventis.jmh.opt.exec.ServiceOps.service_from_conduit_batch             avgt    5      1.665 ±    0.012  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_cancel                               avgt    5      9.629 ±    0.697  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_cancel_batch                         avgt    5      7.273 ±    5.763  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_complete                             avgt    5      9.804 ±    0.660  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_complete_batch                       avgt    5      9.846 ±    0.522  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_fail                                 avgt    5      9.783 ±    0.630  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_fail_batch                           avgt    5      7.355 ±    5.656  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_progress                             avgt    5      7.934 ±    4.705  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_progress_batch                       avgt    5      7.967 ±    5.284  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_reject                               avgt    5      8.616 ±    4.060  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_reject_batch                         avgt    5     10.475 ±    0.650  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_resume                               avgt    5      8.372 ±    3.309  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_resume_batch                         avgt    5     10.165 ±    0.559  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_schedule                             avgt    5      9.988 ±    0.267  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_schedule_batch                       avgt    5      8.075 ±    9.438  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_sign                                 avgt    5     10.378 ±    0.567  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_sign_batch                           avgt    5      8.897 ±    0.594  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_start                                avgt    5      8.502 ±    4.089  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_start_batch                          avgt    5      8.102 ±    3.736  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_submit                               avgt    5      8.035 ±    4.548  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_submit_batch                         avgt    5     10.461 ±    0.565  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_suspend                              avgt    5      8.327 ±    4.637  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_suspend_batch                        avgt    5      9.428 ±    0.303  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_timeout                              avgt    5      8.341 ±    4.146  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.emit_timeout_batch                        avgt    5      9.233 ±    0.689  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.task_from_conduit                         avgt    5      1.863 ±    0.064  ns/op
i.h.serventis.jmh.opt.exec.TaskOps.task_from_conduit_batch                   avgt    5      1.665 ±    0.009  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_meet_deadline                       avgt    5      9.760 ±    0.381  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_meet_deadline_batch                 avgt    5      9.066 ±    6.730  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_meet_threshold                      avgt    5      8.209 ±    4.322  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_meet_threshold_batch                avgt    5      8.566 ±    4.362  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_miss_deadline                       avgt    5      8.127 ±    5.956  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_miss_deadline_batch                 avgt    5     10.505 ±    1.139  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_miss_threshold                      avgt    5      9.815 ±    0.528  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_miss_threshold_batch                avgt    5      8.414 ±    3.698  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_signal                              avgt    5      8.636 ±    3.785  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.emit_signal_batch                        avgt    5     11.108 ±    0.800  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.timer_from_conduit                       avgt    5      1.863 ±    0.065  ns/op
i.h.serventis.jmh.opt.exec.TimerOps.timer_from_conduit_batch                 avgt    5      1.664 ±    0.004  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_abort_coordinator             avgt    5      9.507 ±    0.377  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_abort_coordinator_batch       avgt    5     11.538 ±    0.890  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_abort_participant             avgt    5      8.189 ±    3.079  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_abort_participant_batch       avgt    5     11.617 ±    0.472  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_commit_coordinator            avgt    5      8.638 ±    3.897  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_commit_coordinator_batch      avgt    5      9.156 ±    3.352  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_commit_participant            avgt    5      9.821 ±    0.729  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_commit_participant_batch      avgt    5      8.514 ±    4.307  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_compensate_coordinator        avgt    5      8.307 ±    4.700  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_compensate_coordinator_batch  avgt    5      9.809 ±    6.067  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_compensate_participant        avgt    5      9.152 ±    8.648  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_compensate_participant_batch  avgt    5      8.505 ±    4.638  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_conflict_coordinator          avgt    5      9.512 ±    1.072  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_conflict_coordinator_batch    avgt    5      9.416 ±    3.149  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_conflict_participant          avgt    5     10.026 ±    0.860  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_conflict_participant_batch    avgt    5      8.216 ±    4.149  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_expire_coordinator            avgt    5      9.527 ±    1.407  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_expire_coordinator_batch      avgt    5      9.921 ±    5.946  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_expire_participant            avgt    5      9.738 ±    0.628  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_expire_participant_batch      avgt    5      9.389 ±    3.496  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_prepare_coordinator           avgt    5      9.392 ±    0.422  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_prepare_coordinator_batch     avgt    5      8.456 ±    3.737  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_prepare_participant           avgt    5     10.588 ±    0.887  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_prepare_participant_batch     avgt    5      9.584 ±    4.380  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_rollback_coordinator          avgt    5     10.327 ±    0.519  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_rollback_coordinator_batch    avgt    5      8.603 ±    3.257  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_rollback_participant          avgt    5      9.460 ±    0.749  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_rollback_participant_batch    avgt    5      9.830 ±    2.980  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_signal                        avgt    5      8.711 ±    3.416  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_signal_batch                  avgt    5     10.486 ±    1.044  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_start_coordinator             avgt    5      9.080 ±    0.684  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_start_coordinator_batch       avgt    5      9.553 ±    4.410  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_start_participant             avgt    5      8.390 ±    3.112  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.emit_start_participant_batch       avgt    5     10.074 ±    5.516  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.transaction_from_conduit           avgt    5      1.857 ±    0.018  ns/op
i.h.serventis.jmh.opt.exec.TransactionOps.transaction_from_conduit_batch     avgt    5      1.660 ±    0.003  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.breaker_from_conduit                   avgt    5      1.870 ±    0.048  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.breaker_from_conduit_batch             avgt    5      1.661 ±    0.004  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_close                             avgt    5      8.860 ±    6.061  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_close_batch                       avgt    5      8.572 ±    0.662  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_half_open                         avgt    5      8.505 ±    3.771  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_half_open_batch                   avgt    5      9.818 ±    1.137  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_open                              avgt    5      8.589 ±    3.856  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_open_batch                        avgt    5      9.369 ±    0.829  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_probe                             avgt    5      8.534 ±    4.411  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_probe_batch                       avgt    5      8.195 ±    4.183  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_reset                             avgt    5      8.287 ±    3.775  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_reset_batch                       avgt    5      7.646 ±    4.795  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_sign                              avgt    5      8.838 ±    3.517  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_sign_batch                        avgt    5      8.459 ±    3.429  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_trip                              avgt    5      8.818 ±    3.218  ns/op
i.h.serventis.jmh.opt.flow.BreakerOps.emit_trip_batch                        avgt    5      8.488 ±    6.594  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_fail_egress                          avgt    5      8.937 ±    3.508  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_fail_egress_batch                    avgt    5      8.355 ±    3.358  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_fail_ingress                         avgt    5      8.136 ±    3.711  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_fail_ingress_batch                   avgt    5      9.381 ±    3.044  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_fail_transit                         avgt    5      9.681 ±    0.450  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_fail_transit_batch                   avgt    5     10.629 ±    0.668  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_signal                               avgt    5      9.763 ±    0.547  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_signal_batch                         avgt    5      8.618 ±    2.809  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_success_egress                       avgt    5     10.575 ±    0.179  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_success_egress_batch                 avgt    5     10.217 ±    0.809  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_success_ingress                      avgt    5      8.880 ±    2.831  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_success_ingress_batch                avgt    5     10.555 ±    0.615  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_success_transit                      avgt    5      9.566 ±    0.285  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.emit_success_transit_batch                avgt    5      9.440 ±    0.410  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.flow_from_conduit                         avgt    5      1.865 ±    0.066  ns/op
i.h.serventis.jmh.opt.flow.FlowOps.flow_from_conduit_batch                   avgt    5      1.662 ±    0.009  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_corrupt                            avgt    5      8.736 ±    5.501  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_corrupt_batch                      avgt    5      9.321 ±    0.693  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_drop                               avgt    5      9.613 ±    1.191  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_drop_batch                         avgt    5      9.105 ±    0.461  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_forward                            avgt    5      8.431 ±    4.591  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_forward_batch                      avgt    5      8.097 ±    6.745  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_fragment                           avgt    5     10.794 ±    0.959  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_fragment_batch                     avgt    5     11.947 ±    0.726  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_reassemble                         avgt    5      8.020 ±    4.157  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_reassemble_batch                   avgt    5      8.935 ±    0.235  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_receive                            avgt    5      8.752 ±    6.019  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_receive_batch                      avgt    5     10.211 ±    0.688  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_reorder                            avgt    5      8.968 ±    5.852  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_reorder_batch                      avgt    5      7.983 ±    6.965  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_route                              avgt    5      9.586 ±    0.962  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_route_batch                        avgt    5     10.095 ±    0.891  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_send                               avgt    5     10.217 ±    0.904  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_send_batch                         avgt    5      9.394 ±    0.492  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_sign                               avgt    5      8.258 ±    4.565  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.emit_sign_batch                         avgt    5      7.974 ±    5.417  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.router_from_conduit                     avgt    5      1.865 ±    0.079  ns/op
i.h.serventis.jmh.opt.flow.RouterOps.router_from_conduit_batch               avgt    5      1.659 ±    0.005  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_contract                            avgt    5     10.030 ±    0.765  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_contract_batch                      avgt    5      8.415 ±    4.801  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_deny                                avgt    5      8.908 ±    5.367  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_deny_batch                          avgt    5      8.052 ±    3.023  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_drain                               avgt    5     10.222 ±    0.276  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_drain_batch                         avgt    5      7.589 ±    5.827  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_drop                                avgt    5      8.646 ±    3.396  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_drop_batch                          avgt    5      7.567 ±    2.926  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_expand                              avgt    5      8.556 ±    4.110  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_expand_batch                        avgt    5      8.334 ±    6.996  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_pass                                avgt    5      8.457 ±    4.317  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_pass_batch                          avgt    5      8.146 ±    3.201  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_sign                                avgt    5      8.767 ±    6.310  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.emit_sign_batch                          avgt    5     10.513 ±    0.921  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.valve_from_conduit                       avgt    5      1.861 ±    0.068  ns/op
i.h.serventis.jmh.opt.flow.ValveOps.valve_from_conduit_batch                 avgt    5      1.662 ±    0.010  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_contract_provider                avgt    5      8.900 ±    2.813  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_contract_provider_batch          avgt    5      9.903 ±    4.910  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_contract_receiver                avgt    5      8.639 ±    3.764  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_contract_receiver_batch          avgt    5      8.239 ±    3.682  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_full_exchange                    avgt    5     10.462 ±    0.313  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_full_exchange_batch              avgt    5      9.164 ±    3.647  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_signal                           avgt    5      8.563 ±    3.057  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_signal_batch                     avgt    5     11.518 ±    1.200  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_transfer_provider                avgt    5     10.577 ±    3.877  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_transfer_provider_batch          avgt    5      9.554 ±    3.546  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_transfer_receiver                avgt    5      9.679 ±    0.694  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.emit_transfer_receiver_batch          avgt    5      8.908 ±    5.091  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.exchange_from_conduit                 avgt    5      1.865 ±    0.069  ns/op
i.h.serventis.jmh.opt.pool.ExchangeOps.exchange_from_conduit_batch           avgt    5      1.659 ±    0.001  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_acquire                             avgt    5      9.341 ±    0.457  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_acquire_batch                       avgt    5     10.423 ±    0.604  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_acquired                            avgt    5      9.258 ±    0.746  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_acquired_batch                      avgt    5      7.943 ±    2.808  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_denied                              avgt    5      9.673 ±    0.598  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_denied_batch                        avgt    5      9.435 ±    2.375  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_deny                                avgt    5      8.868 ±    4.945  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_deny_batch                          avgt    5      9.462 ±    2.771  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_expire                              avgt    5      8.710 ±    3.445  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_expire_batch                        avgt    5      8.231 ±    4.300  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_expired                             avgt    5      8.779 ±    3.680  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_expired_batch                       avgt    5      8.179 ±    3.358  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_extend                              avgt    5      9.480 ±    0.361  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_extend_batch                        avgt    5      9.293 ±    1.272  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_extended                            avgt    5      9.690 ±    7.489  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_extended_batch                      avgt    5      8.319 ±    3.287  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_grant                               avgt    5     10.245 ±    0.745  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_grant_batch                         avgt    5     10.085 ±    2.689  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_granted                             avgt    5     10.292 ±    0.403  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_granted_batch                       avgt    5      8.300 ±    3.224  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_probe                               avgt    5      9.388 ±    0.585  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_probe_batch                         avgt    5      9.505 ±    1.611  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_probed                              avgt    5      8.526 ±    4.199  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_probed_batch                        avgt    5      8.642 ±    3.829  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_release                             avgt    5      9.571 ±    1.008  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_release_batch                       avgt    5      8.729 ±    3.899  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_released                            avgt    5      8.681 ±    3.147  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_released_batch                      avgt    5      9.485 ±    4.180  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_renew                               avgt    5     10.478 ±    0.647  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_renew_batch                         avgt    5      9.609 ±    3.855  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_renewed                             avgt    5      8.010 ±    4.373  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_renewed_batch                       avgt    5      8.435 ±    4.756  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_revoke                              avgt    5      8.882 ±    3.727  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_revoke_batch                        avgt    5      8.276 ±    4.314  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_revoked                             avgt    5      9.366 ±    0.289  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_revoked_batch                       avgt    5      9.489 ±    0.976  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_signal                              avgt    5      8.113 ±    2.720  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.emit_signal_batch                        avgt    5     10.241 ±    0.965  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.lease_from_conduit                       avgt    5      1.855 ±    0.028  ns/op
i.h.serventis.jmh.opt.pool.LeaseOps.lease_from_conduit_batch                 avgt    5      1.661 ±    0.010  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_borrow                               avgt    5      8.682 ±    3.913  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_borrow_batch                         avgt    5     10.035 ±    0.407  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_contract                             avgt    5     10.540 ±    0.581  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_contract_batch                       avgt    5     10.532 ±    1.080  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_expand                               avgt    5      8.543 ±    5.574  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_expand_batch                         avgt    5      8.362 ±    3.799  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_reclaim                              avgt    5      9.794 ±    1.170  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_reclaim_batch                        avgt    5      8.211 ±    4.545  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_sign                                 avgt    5      8.906 ±    5.926  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.emit_sign_batch                           avgt    5      9.979 ±    0.313  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.pool_from_conduit                         avgt    5      1.866 ±    0.065  ns/op
i.h.serventis.jmh.opt.pool.PoolOps.pool_from_conduit_batch                   avgt    5      1.759 ±    0.640  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_acquire                          avgt    5      8.711 ±    7.232  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_acquire_batch                    avgt    5      8.139 ±    4.816  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_attempt                          avgt    5      8.847 ±    6.433  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_attempt_batch                    avgt    5      9.070 ±    2.886  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_deny                             avgt    5      9.087 ±    4.813  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_deny_batch                       avgt    5      8.381 ±    7.451  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_grant                            avgt    5      8.828 ±    5.072  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_grant_batch                      avgt    5      8.245 ±    4.142  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_release                          avgt    5     10.750 ±    0.552  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_release_batch                    avgt    5      8.898 ±    0.185  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_sign                             avgt    5     10.888 ±    1.227  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_sign_batch                       avgt    5      8.902 ±    6.135  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_timeout                          avgt    5      9.004 ±    5.837  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.emit_timeout_batch                    avgt    5     10.353 ±    0.708  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.resource_from_conduit                 avgt    5      1.863 ±    0.057  ns/op
i.h.serventis.jmh.opt.pool.ResourceOps.resource_from_conduit_batch           avgt    5      1.661 ±    0.005  ns/op
i.h.serventis.jmh.opt.role.ActorOps.actor_from_conduit                       avgt    5      1.858 ±    0.028  ns/op
i.h.serventis.jmh.opt.role.ActorOps.actor_from_conduit_batch                 avgt    5      1.665 ±    0.011  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_acknowledge                         avgt    5      8.731 ±    6.174  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_acknowledge_batch                   avgt    5      7.893 ±    3.154  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_affirm                              avgt    5      8.560 ±    3.820  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_affirm_batch                        avgt    5     10.761 ±    0.983  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_ask                                 avgt    5     10.101 ±    0.789  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_ask_batch                           avgt    5      8.095 ±    3.743  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_clarify                             avgt    5      8.530 ±    4.262  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_clarify_batch                       avgt    5      9.451 ±    0.583  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_command                             avgt    5      9.346 ±    0.650  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_command_batch                       avgt    5      8.227 ±    3.341  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_deliver                             avgt    5     10.392 ±    0.344  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_deliver_batch                       avgt    5      9.376 ±   11.420  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_deny                                avgt    5      8.615 ±    3.317  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_deny_batch                          avgt    5      8.680 ±    0.996  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_explain                             avgt    5      8.433 ±    5.878  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_explain_batch                       avgt    5      7.887 ±    7.529  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_promise                             avgt    5      8.449 ±    3.911  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_promise_batch                       avgt    5      8.702 ±    1.393  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_report                              avgt    5     10.475 ±    0.574  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_report_batch                        avgt    5      9.522 ±    0.677  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_request                             avgt    5      8.456 ±    3.978  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_request_batch                       avgt    5      8.444 ±    5.646  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_sign                                avgt    5      9.043 ±    6.174  ns/op
i.h.serventis.jmh.opt.role.ActorOps.emit_sign_batch                          avgt    5      7.682 ±    6.616  ns/op
i.h.serventis.jmh.opt.role.AgentOps.agent_from_conduit                       avgt    5      1.853 ±    0.016  ns/op
i.h.serventis.jmh.opt.role.AgentOps.agent_from_conduit_batch                 avgt    5      1.661 ±    0.008  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_accept                              avgt    5     10.811 ±    0.613  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_accept_batch                        avgt    5      9.372 ±    3.864  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_accepted                            avgt    5      8.534 ±    3.385  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_accepted_batch                      avgt    5      9.418 ±    0.885  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_breach                              avgt    5      8.656 ±    3.502  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_breach_batch                        avgt    5      9.381 ±    0.903  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_breached                            avgt    5      9.528 ±    0.754  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_breached_batch                      avgt    5      8.316 ±    3.127  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_depend                              avgt    5      8.522 ±    3.506  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_depend_batch                        avgt    5      8.644 ±    3.448  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_depended                            avgt    5     10.984 ±    1.538  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_depended_batch                      avgt    5      8.529 ±    4.412  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_fulfill                             avgt    5      8.682 ±    3.144  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_fulfill_batch                       avgt    5      9.438 ±    3.323  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_fulfilled                           avgt    5      9.033 ±    1.241  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_fulfilled_batch                     avgt    5      9.732 ±    3.581  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_inquire                             avgt    5      8.987 ±    5.754  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_inquire_batch                       avgt    5      9.534 ±    5.614  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_inquired                            avgt    5      9.467 ±    0.557  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_inquired_batch                      avgt    5     10.164 ±    0.312  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_observe                             avgt    5      8.331 ±    2.834  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_observe_batch                       avgt    5      9.101 ±    6.177  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_observed                            avgt    5      8.515 ±    3.645  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_observed_batch                      avgt    5      9.375 ±    3.905  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_offer                               avgt    5     10.588 ±    0.664  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_offer_batch                         avgt    5     10.250 ±    0.536  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_offered                             avgt    5      8.210 ±    4.634  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_offered_batch                       avgt    5      9.266 ±    3.554  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_promise                             avgt    5      8.678 ±    3.717  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_promise_batch                       avgt    5      9.077 ±    6.236  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_promised                            avgt    5      8.810 ±    3.215  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_promised_batch                      avgt    5      9.258 ±    2.703  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_retract                             avgt    5      7.966 ±    4.586  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_retract_batch                       avgt    5      9.597 ±    4.304  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_retracted                           avgt    5      9.369 ±    0.293  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_retracted_batch                     avgt    5      9.563 ±    3.711  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_signal                              avgt    5      7.916 ±    3.174  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_signal_batch                        avgt    5      8.039 ±    1.940  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_validate                            avgt    5     10.557 ±    0.856  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_validate_batch                      avgt    5     10.038 ±    4.764  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_validated                           avgt    5      9.225 ±    5.321  ns/op
i.h.serventis.jmh.opt.role.AgentOps.emit_validated_batch                     avgt    5      9.470 ±    3.666  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.atomic_from_conduit                     avgt    5      1.859 ±    0.026  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.atomic_from_conduit_batch               avgt    5      1.661 ±    0.006  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_attempt                            avgt    5      9.810 ±    0.412  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_attempt_batch                      avgt    5      8.296 ±    7.552  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_backoff                            avgt    5      8.751 ±    3.293  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_backoff_batch                      avgt    5      7.981 ±    7.265  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_exhaust                            avgt    5      8.883 ±    6.256  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_exhaust_batch                      avgt    5      7.967 ±    4.949  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_fail                               avgt    5      8.479 ±    3.851  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_fail_batch                         avgt    5      7.868 ±    4.098  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_park                               avgt    5      8.929 ±    5.541  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_park_batch                         avgt    5      7.736 ±    6.625  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_sign                               avgt    5      8.956 ±    5.675  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_sign_batch                         avgt    5      8.933 ±    0.626  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_spin                               avgt    5      9.034 ±    5.648  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_spin_batch                         avgt    5      8.097 ±    2.798  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_success                            avgt    5     10.635 ±    0.442  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_success_batch                      avgt    5      9.365 ±    1.082  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_yield                              avgt    5      8.591 ±    4.447  ns/op
i.h.serventis.jmh.opt.sync.AtomicOps.emit_yield_batch                        avgt    5     10.071 ±    0.550  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_abandon                             avgt    5      8.975 ±    2.683  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_abandon_batch                       avgt    5      9.368 ±    0.462  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_arrive                              avgt    5      8.727 ±    3.601  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_arrive_batch                        avgt    5      8.168 ±    4.598  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_await                               avgt    5     12.024 ±    0.977  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_await_batch                         avgt    5      7.944 ±    3.300  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_release                             avgt    5      9.267 ±    0.424  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_release_batch                       avgt    5      8.108 ±    3.447  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_reset                               avgt    5     10.219 ±    0.267  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_reset_batch                         avgt    5     11.463 ±    0.757  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_sign                                avgt    5      9.735 ±    1.137  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_sign_batch                          avgt    5     10.713 ±    0.538  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_timeout                             avgt    5      8.654 ±    3.554  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.emit_timeout_batch                       avgt    5      8.328 ±    4.540  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.latch_from_conduit                       avgt    5      1.866 ±    0.078  ns/op
i.h.serventis.jmh.opt.sync.LatchOps.latch_from_conduit_batch                 avgt    5      1.660 ±    0.002  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_abandon                              avgt    5      9.025 ±    1.479  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_abandon_batch                        avgt    5      9.373 ±    0.462  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_acquire                              avgt    5      8.544 ±    3.673  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_acquire_batch                        avgt    5      7.831 ±    2.710  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_attempt                              avgt    5      8.649 ±    3.680  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_attempt_batch                        avgt    5     10.734 ±    0.499  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_contest                              avgt    5      8.638 ±    3.632  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_contest_batch                        avgt    5      7.957 ±    4.864  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_deny                                 avgt    5      8.393 ±    3.843  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_deny_batch                           avgt    5      9.444 ±    0.432  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_downgrade                            avgt    5      8.736 ±    4.260  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_downgrade_batch                      avgt    5     10.142 ±    0.425  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_grant                                avgt    5     10.411 ±    0.614  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_grant_batch                          avgt    5      9.463 ±    0.257  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_release                              avgt    5      7.974 ±    3.494  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_release_batch                        avgt    5      8.375 ±    3.754  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_sign                                 avgt    5     11.095 ±    0.349  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_sign_batch                           avgt    5      9.377 ±    0.992  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_timeout                              avgt    5      8.810 ±    3.349  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_timeout_batch                        avgt    5      8.249 ±    4.078  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_upgrade                              avgt    5      8.372 ±    3.685  ns/op
i.h.serventis.jmh.opt.sync.LockOps.emit_upgrade_batch                        avgt    5      7.755 ±    7.533  ns/op
i.h.serventis.jmh.opt.sync.LockOps.lock_from_conduit                         avgt    5      1.861 ±    0.045  ns/op
i.h.serventis.jmh.opt.sync.LockOps.lock_from_conduit_batch                   avgt    5      1.660 ±    0.003  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.counter_from_conduit                   avgt    5      1.862 ±    0.071  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.counter_from_conduit_batch             avgt    5      1.661 ±    0.010  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_increment                         avgt    5      8.583 ±    5.156  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_increment_batch                   avgt    5      9.404 ±    0.659  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_overflow                          avgt    5      8.040 ±    4.056  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_overflow_batch                    avgt    5     10.453 ±    0.630  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_reset                             avgt    5      8.229 ±    4.451  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_reset_batch                       avgt    5      9.384 ±    0.510  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_sign                              avgt    5      8.102 ±    3.118  ns/op
i.h.serventis.jmh.opt.tool.CounterOps.emit_sign_batch                        avgt    5      9.218 ±    6.574  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_decrement                           avgt    5     10.375 ±    0.257  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_decrement_batch                     avgt    5      8.007 ±    5.579  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_increment                           avgt    5      8.836 ±    6.292  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_increment_batch                     avgt    5      8.367 ±    4.226  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_overflow                            avgt    5     10.106 ±    0.680  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_overflow_batch                      avgt    5      8.084 ±    4.110  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_reset                               avgt    5      8.378 ±    4.454  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_reset_batch                         avgt    5      9.412 ±    1.027  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_sign                                avgt    5      8.434 ±    3.511  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_sign_batch                          avgt    5      9.083 ±    4.895  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_underflow                           avgt    5      8.839 ±    3.062  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.emit_underflow_batch                     avgt    5      9.381 ±    0.447  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.gauge_from_conduit                       avgt    5      1.855 ±    0.020  ns/op
i.h.serventis.jmh.opt.tool.GaugeOps.gauge_from_conduit_batch                 avgt    5      1.660 ±    0.004  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_debug                                 avgt    5      8.823 ±    4.292  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_debug_batch                           avgt    5     10.706 ±    0.439  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_info                                  avgt    5     10.339 ±    0.471  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_info_batch                            avgt    5      7.839 ±    5.033  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_severe                                avgt    5      9.643 ±    0.313  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_severe_batch                          avgt    5      9.436 ±    0.480  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_sign                                  avgt    5      8.587 ±    3.749  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_sign_batch                            avgt    5      9.952 ±    0.962  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_warning                               avgt    5     10.518 ±    0.702  ns/op
i.h.serventis.jmh.opt.tool.LogOps.emit_warning_batch                         avgt    5      7.527 ±    9.920  ns/op
i.h.serventis.jmh.opt.tool.LogOps.log_from_conduit                           avgt    5      1.870 ±    0.090  ns/op
i.h.serventis.jmh.opt.tool.LogOps.log_from_conduit_batch                     avgt    5      1.661 ±    0.007  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_connect                             avgt    5      8.435 ±    3.382  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_connect_batch                       avgt    5      9.451 ±    3.818  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_connected                           avgt    5      8.295 ±    3.468  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_connected_batch                     avgt    5     11.274 ±    1.014  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_disconnect                          avgt    5      8.713 ±    3.512  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_disconnect_batch                    avgt    5     10.721 ±    0.231  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_disconnected                        avgt    5      8.384 ±    3.738  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_disconnected_batch                  avgt    5      9.058 ±    1.085  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_fail                                avgt    5      9.661 ±    0.824  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_fail_batch                          avgt    5     10.160 ±    5.761  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_failed                              avgt    5      8.654 ±    3.520  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_failed_batch                        avgt    5     10.055 ±    1.182  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_process                             avgt    5      9.493 ±    0.551  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_process_batch                       avgt    5     10.568 ±    0.704  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_processed                           avgt    5      9.639 ±    0.551  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_processed_batch                     avgt    5      8.465 ±    4.406  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_receive_batch                       avgt    5      9.625 ±    1.187  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_received_batch                      avgt    5      9.617 ±    2.472  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_signal                              avgt    5      8.486 ±    4.288  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_signal_batch                        avgt    5     10.746 ±    0.763  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_succeed                             avgt    5      8.701 ±    4.192  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_succeed_batch                       avgt    5      9.384 ±    0.716  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_succeeded                           avgt    5     10.452 ±    0.748  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_succeeded_batch                     avgt    5      9.700 ±    3.068  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transfer                            avgt    5     10.376 ±    0.721  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transfer_inbound                    avgt    5      9.730 ±    1.320  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transfer_outbound                   avgt    5     10.078 ±    0.962  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transferred                         avgt    5      9.995 ±    0.423  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transmit_batch                      avgt    5      8.688 ±    4.539  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.emit_transmitted_batch                   avgt    5      9.509 ±    0.641  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.probe_from_conduit                       avgt    5      1.865 ±    0.069  ns/op
i.h.serventis.jmh.opt.tool.ProbeOps.probe_from_conduit_batch                 avgt    5      1.660 ±    0.003  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_baseline                     avgt    5      8.838 ±    4.583  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_baseline_batch               avgt    5     10.152 ±    4.280  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_target                       avgt    5     10.122 ±    0.364  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_target_batch                 avgt    5      9.477 ±    3.939  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_threshold                    avgt    5      9.589 ±    0.400  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_above_threshold_batch              avgt    5     11.221 ±    0.621  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_baseline                     avgt    5     10.246 ±    0.410  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_baseline_batch               avgt    5     10.505 ±    0.833  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_target                       avgt    5     10.333 ±    1.005  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_target_batch                 avgt    5      9.228 ±    3.614  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_threshold                    avgt    5      8.608 ±    4.487  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_below_threshold_batch              avgt    5     10.724 ±    0.639  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_baseline                   avgt    5      9.683 ±    0.320  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_baseline_batch             avgt    5     10.111 ±    0.927  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_target                     avgt    5      9.139 ±    4.763  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_target_batch               avgt    5     11.446 ±    0.920  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_threshold                  avgt    5      8.889 ±    3.108  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_nominal_threshold_batch            avgt    5      9.534 ±    0.285  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_signal                             avgt    5      8.926 ±    3.204  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.emit_signal_batch                       avgt    5     11.265 ±    0.509  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.sensor_from_conduit                     avgt    5      1.866 ±    0.053  ns/op
i.h.serventis.jmh.opt.tool.SensorOps.sensor_from_conduit_batch               avgt    5      1.661 ±    0.003  ns/op
i.h.serventis.jmh.sdk.OperationOps.emit_begin                                avgt    5     10.718 ±    0.348  ns/op
i.h.serventis.jmh.sdk.OperationOps.emit_begin_batch                          avgt    5     10.010 ±    1.063  ns/op
i.h.serventis.jmh.sdk.OperationOps.emit_end                                  avgt    5      8.784 ±    6.046  ns/op
i.h.serventis.jmh.sdk.OperationOps.emit_end_batch                            avgt    5      8.287 ±    6.297  ns/op
i.h.serventis.jmh.sdk.OperationOps.emit_sign                                 avgt    5     10.637 ±    0.599  ns/op
i.h.serventis.jmh.sdk.OperationOps.emit_sign_batch                           avgt    5      8.083 ±    7.814  ns/op
i.h.serventis.jmh.sdk.OperationOps.operation_from_conduit                    avgt    5      1.871 ±    0.075  ns/op
i.h.serventis.jmh.sdk.OperationOps.operation_from_conduit_batch              avgt    5      1.660 ±    0.004  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.emit_fail                                   avgt    5      9.564 ±    0.189  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.emit_fail_batch                             avgt    5      8.295 ±    4.304  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.emit_sign                                   avgt    5      8.769 ±    3.948  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.emit_sign_batch                             avgt    5      8.213 ±    7.158  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.emit_success                                avgt    5      8.393 ±    5.223  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.emit_success_batch                          avgt    5      8.181 ±    4.157  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.outcome_from_conduit                        avgt    5      1.861 ±    0.061  ns/op
i.h.serventis.jmh.sdk.OutcomeOps.outcome_from_conduit_batch                  avgt    5      1.659 ±    0.005  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_mixed_pattern                         avgt    5      0.226 ±    0.003  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_single                                avgt    5      0.750 ±    0.001  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_single_batch                          avgt    5      0.019 ±    0.001  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_varied_batch                          avgt    5      1.514 ±    0.002  ns/op
i.h.serventis.jmh.sdk.SignalSetOps.get_worst_case                            avgt    5      1.186 ±    0.026  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_critical                             avgt    5      8.474 ±    3.718  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_critical_batch                       avgt    5     10.411 ±    0.794  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_normal                               avgt    5      9.329 ±    0.474  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_normal_batch                         avgt    5     11.293 ±    1.300  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_signal                               avgt    5     10.479 ±    1.033  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_signal_batch                         avgt    5      7.817 ±    7.703  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_warning                              avgt    5     10.042 ±    0.341  ns/op
i.h.serventis.jmh.sdk.SituationOps.emit_warning_batch                        avgt    5      9.797 ±    3.596  ns/op
i.h.serventis.jmh.sdk.SituationOps.situation_from_conduit                    avgt    5      1.858 ±    0.048  ns/op
i.h.serventis.jmh.sdk.SituationOps.situation_from_conduit_batch              avgt    5      1.660 ±    0.004  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_converging_confirmed                    avgt    5      8.156 ±    4.112  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_converging_confirmed_batch              avgt    5     11.393 ±    0.710  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_defective_tentative                     avgt    5     10.307 ±    4.833  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_defective_tentative_batch               avgt    5     11.176 ±    2.015  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_degraded_measured                       avgt    5      8.551 ±    3.679  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_degraded_measured_batch                 avgt    5      9.358 ±    4.058  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_down_confirmed                          avgt    5      8.686 ±    2.821  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_down_confirmed_batch                    avgt    5      9.768 ±    2.635  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_signal                                  avgt    5      9.157 ±    0.728  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_signal_batch                            avgt    5      9.500 ±    0.308  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_stable_confirmed                        avgt    5     10.378 ±    0.523  ns/op
i.h.serventis.jmh.sdk.StatusOps.emit_stable_confirmed_batch                  avgt    5      9.449 ±    3.892  ns/op
i.h.serventis.jmh.sdk.StatusOps.status_from_conduit                          avgt    5      1.874 ±    0.066  ns/op
i.h.serventis.jmh.sdk.StatusOps.status_from_conduit_batch                    avgt    5      1.660 ±    0.003  ns/op
i.h.serventis.jmh.sdk.SurveyOps.emit_divided                                 avgt    5      9.047 ±    5.590  ns/op
i.h.serventis.jmh.sdk.SurveyOps.emit_divided_batch                           avgt    5     10.213 ±    5.114  ns/op
i.h.serventis.jmh.sdk.SurveyOps.emit_majority                                avgt    5      8.492 ±    3.144  ns/op
i.h.serventis.jmh.sdk.SurveyOps.emit_majority_batch                          avgt    5      8.575 ±    5.661  ns/op
i.h.serventis.jmh.sdk.SurveyOps.emit_signal                                  avgt    5      8.593 ±    3.473  ns/op
i.h.serventis.jmh.sdk.SurveyOps.emit_signal_batch                            avgt    5      9.632 ±    0.516  ns/op
i.h.serventis.jmh.sdk.SurveyOps.emit_unanimous                               avgt    5      8.985 ±    5.404  ns/op
i.h.serventis.jmh.sdk.SurveyOps.emit_unanimous_batch                         avgt    5      9.411 ±    3.648  ns/op
i.h.serventis.jmh.sdk.SurveyOps.survey_from_conduit                          avgt    5      1.872 ±    0.080  ns/op
i.h.serventis.jmh.sdk.SurveyOps.survey_from_conduit_batch                    avgt    5      1.660 ±    0.004  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_alarm_flow                              avgt    5      8.703 ±    4.427  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_alarm_flow_batch                        avgt    5      8.673 ±    0.404  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_fault_link                              avgt    5      9.573 ±    0.863  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_fault_link_batch                        avgt    5      9.264 ±    0.628  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_limit_time                              avgt    5      8.527 ±    3.849  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_limit_time_batch                        avgt    5     10.514 ±    0.675  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_normal_space                            avgt    5      9.816 ±    0.674  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_normal_space_batch                      avgt    5      9.208 ±    3.692  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_signal                                  avgt    5      8.734 ±    3.176  ns/op
i.h.serventis.jmh.sdk.SystemOps.emit_signal_batch                            avgt    5      7.964 ±    4.628  ns/op
i.h.serventis.jmh.sdk.SystemOps.system_from_conduit                          avgt    5      1.862 ±    0.060  ns/op
i.h.serventis.jmh.sdk.SystemOps.system_from_conduit_batch                    avgt    5      1.661 ±    0.010  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_chaos                                    avgt    5      8.774 ±    6.350  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_chaos_batch                              avgt    5     10.286 ±    1.687  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_cycle                                    avgt    5      8.785 ±    3.627  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_cycle_batch                              avgt    5      8.810 ±    0.707  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_drift                                    avgt    5      8.393 ±    3.563  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_drift_batch                              avgt    5      8.191 ±    6.683  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_sign                                     avgt    5      8.401 ±    4.161  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_sign_batch                               avgt    5      8.336 ±    7.299  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_spike                                    avgt    5      8.814 ±    4.167  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_spike_batch                              avgt    5      8.240 ±    3.950  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_stable                                   avgt    5      8.757 ±    3.892  ns/op
i.h.serventis.jmh.sdk.TrendOps.emit_stable_batch                             avgt    5      7.984 ±    3.456  ns/op
i.h.serventis.jmh.sdk.TrendOps.trend_from_conduit                            avgt    5      1.863 ±    0.074  ns/op
i.h.serventis.jmh.sdk.TrendOps.trend_from_conduit_batch                      avgt    5      1.661 ±    0.010  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.cycle_from_conduit                       avgt    5      1.857 ±    0.042  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.cycle_from_conduit_batch                 avgt    5      1.661 ±    0.010  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_repeat                              avgt    5      8.489 ±    3.388  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_repeat_batch                        avgt    5      9.995 ±    0.558  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_return                              avgt    5      8.464 ±    4.015  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_return_batch                        avgt    5      9.691 ±    0.765  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_signal                              avgt    5      8.683 ±    2.870  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_signal_batch                        avgt    5      9.740 ±    0.359  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_single                              avgt    5      9.205 ±    0.502  ns/op
i.h.serventis.jmh.sdk.meta.CycleOps.emit_single_batch                        avgt    5     10.569 ±    1.101  ns/op
i.h.substrates.jmh.CircuitOps.conduit_create_close                           avgt    5    291.411 ±   75.847  ns/op
i.h.substrates.jmh.CircuitOps.conduit_create_named                           avgt    5    281.294 ±   49.697  ns/op
i.h.substrates.jmh.CircuitOps.conduit_create_with_flow                       avgt    5    279.800 ±   62.353  ns/op
i.h.substrates.jmh.CircuitOps.create_and_close                               avgt    5    426.188 ±  821.803  ns/op
i.h.substrates.jmh.CircuitOps.create_await_close                             avgt    5  10433.334 ±  670.601  ns/op
i.h.substrates.jmh.CircuitOps.hot_await_queue_drain                          avgt    5   7743.411 ± 1168.831  ns/op
i.h.substrates.jmh.CircuitOps.hot_conduit_create                             avgt    5     19.070 ±    0.039  ns/op
i.h.substrates.jmh.CircuitOps.hot_conduit_create_named                       avgt    5     19.029 ±    0.102  ns/op
i.h.substrates.jmh.CircuitOps.hot_conduit_create_with_flow                   avgt    5     21.886 ±    0.073  ns/op
i.h.substrates.jmh.CircuitOps.hot_pipe_async                                 avgt    5      8.310 ±    1.499  ns/op
i.h.substrates.jmh.CircuitOps.hot_pipe_async_with_flow                       avgt    5     10.286 ±    0.777  ns/op
i.h.substrates.jmh.CircuitOps.pipe_async                                     avgt    5    390.081 ±  548.385  ns/op
i.h.substrates.jmh.CircuitOps.pipe_async_with_flow                           avgt    5    352.811 ±  522.261  ns/op
i.h.substrates.jmh.ConduitOps.get_by_name                                    avgt    5      1.879 ±    0.034  ns/op
i.h.substrates.jmh.ConduitOps.get_by_name_batch                              avgt    5      1.659 ±    0.007  ns/op
i.h.substrates.jmh.ConduitOps.get_by_substrate                               avgt    5      2.042 ±    0.047  ns/op
i.h.substrates.jmh.ConduitOps.get_by_substrate_batch                         avgt    5      1.809 ±    0.003  ns/op
i.h.substrates.jmh.ConduitOps.get_cached                                     avgt    5      3.450 ±    0.098  ns/op
i.h.substrates.jmh.ConduitOps.get_cached_batch                               avgt    5      3.304 ±    0.009  ns/op
i.h.substrates.jmh.ConduitOps.subscribe                                      avgt    5    473.815 ±  525.658  ns/op
i.h.substrates.jmh.ConduitOps.subscribe_batch                                avgt    5    458.645 ±   49.467  ns/op
i.h.substrates.jmh.ConduitOps.subscribe_with_emission_await                  avgt    5   5902.902 ±  321.799  ns/op
i.h.substrates.jmh.CortexOps.circuit                                         avgt    5    271.275 ±   41.260  ns/op
i.h.substrates.jmh.CortexOps.circuit_batch                                   avgt    5    255.126 ±  167.777  ns/op
i.h.substrates.jmh.CortexOps.circuit_named                                   avgt    5    277.922 ±   93.591  ns/op
i.h.substrates.jmh.CortexOps.current                                         avgt    5      1.087 ±    0.005  ns/op
i.h.substrates.jmh.CortexOps.name_class                                      avgt    5      1.476 ±    0.009  ns/op
i.h.substrates.jmh.CortexOps.name_enum                                       avgt    5      2.823 ±    0.022  ns/op
i.h.substrates.jmh.CortexOps.name_iterable                                   avgt    5     11.238 ±    0.081  ns/op
i.h.substrates.jmh.CortexOps.name_path                                       avgt    5      1.889 ±    0.007  ns/op
i.h.substrates.jmh.CortexOps.name_path_batch                                 avgt    5      1.685 ±    0.001  ns/op
i.h.substrates.jmh.CortexOps.name_string                                     avgt    5      2.850 ±    0.008  ns/op
i.h.substrates.jmh.CortexOps.name_string_batch                               avgt    5      2.587 ±    0.164  ns/op
i.h.substrates.jmh.CortexOps.scope                                           avgt    5      8.309 ±    0.085  ns/op
i.h.substrates.jmh.CortexOps.scope_batch                                     avgt    5      7.505 ±    0.208  ns/op
i.h.substrates.jmh.CortexOps.scope_named                                     avgt    5      7.989 ±    0.026  ns/op
i.h.substrates.jmh.CortexOps.slot_boolean                                    avgt    5      2.307 ±    0.638  ns/op
i.h.substrates.jmh.CortexOps.slot_double                                     avgt    5      2.325 ±    0.368  ns/op
i.h.substrates.jmh.CortexOps.slot_int                                        avgt    5      2.401 ±    0.265  ns/op
i.h.substrates.jmh.CortexOps.slot_long                                       avgt    5      2.178 ±    0.559  ns/op
i.h.substrates.jmh.CortexOps.slot_string                                     avgt    5      2.207 ±    0.815  ns/op
i.h.substrates.jmh.CortexOps.state_empty                                     avgt    5      0.443 ±    0.003  ns/op
i.h.substrates.jmh.CortexOps.state_empty_batch                               avgt    5     ≈ 10⁻³             ns/op
i.h.substrates.jmh.FlowOps.baseline_no_flow_await                            avgt    5     16.777 ±    2.287  ns/op
i.h.substrates.jmh.FlowOps.flow_combined_diff_guard_await                    avgt    5     29.513 ±    1.127  ns/op
i.h.substrates.jmh.FlowOps.flow_combined_diff_sample_await                   avgt    5     20.350 ±    0.358  ns/op
i.h.substrates.jmh.FlowOps.flow_combined_guard_limit_await                   avgt    5     28.440 ±    1.175  ns/op
i.h.substrates.jmh.FlowOps.flow_diff_await                                   avgt    5     28.087 ±    1.024  ns/op
i.h.substrates.jmh.FlowOps.flow_guard_await                                  avgt    5     29.235 ±    2.469  ns/op
i.h.substrates.jmh.FlowOps.flow_limit_await                                  avgt    5     24.560 ±    0.692  ns/op
i.h.substrates.jmh.FlowOps.flow_sample_await                                 avgt    5     17.571 ±    0.205  ns/op
i.h.substrates.jmh.FlowOps.flow_sift_await                                   avgt    5     17.225 ±    0.258  ns/op
i.h.substrates.jmh.NameOps.name_chained_deep                                 avgt    5     16.916 ±    0.088  ns/op
i.h.substrates.jmh.NameOps.name_chaining                                     avgt    5      9.022 ±    0.412  ns/op
i.h.substrates.jmh.NameOps.name_chaining_batch                               avgt    5      8.961 ±    0.460  ns/op
i.h.substrates.jmh.NameOps.name_compare                                      avgt    5     32.653 ±    0.590  ns/op
i.h.substrates.jmh.NameOps.name_compare_batch                                avgt    5     32.413 ±    2.005  ns/op
i.h.substrates.jmh.NameOps.name_depth                                        avgt    5      1.599 ±    0.013  ns/op
i.h.substrates.jmh.NameOps.name_depth_batch                                  avgt    5      1.260 ±    0.030  ns/op
i.h.substrates.jmh.NameOps.name_enclosure                                    avgt    5      0.543 ±    0.001  ns/op
i.h.substrates.jmh.NameOps.name_from_enum                                    avgt    5      2.811 ±    0.021  ns/op
i.h.substrates.jmh.NameOps.name_from_iterable                                avgt    5     11.461 ±    0.013  ns/op
i.h.substrates.jmh.NameOps.name_from_iterator                                avgt    5     12.878 ±    0.101  ns/op
i.h.substrates.jmh.NameOps.name_from_mapped_iterable                         avgt    5     11.715 ±    0.042  ns/op
i.h.substrates.jmh.NameOps.name_from_name                                    avgt    5      4.225 ±    0.006  ns/op
i.h.substrates.jmh.NameOps.name_from_string                                  avgt    5      3.205 ±    0.741  ns/op
i.h.substrates.jmh.NameOps.name_from_string_batch                            avgt    5      2.816 ±    0.091  ns/op
i.h.substrates.jmh.NameOps.name_interning_chained                            avgt    5     12.397 ±    0.181  ns/op
i.h.substrates.jmh.NameOps.name_interning_same_path                          avgt    5      3.557 ±    0.004  ns/op
i.h.substrates.jmh.NameOps.name_interning_segments                           avgt    5      9.282 ±    0.006  ns/op
i.h.substrates.jmh.NameOps.name_iterate_hierarchy                            avgt    5      1.662 ±    0.004  ns/op
i.h.substrates.jmh.NameOps.name_parsing                                      avgt    5      1.888 ±    0.007  ns/op
i.h.substrates.jmh.NameOps.name_parsing_batch                                avgt    5      1.681 ±    0.005  ns/op
i.h.substrates.jmh.NameOps.name_path_generation                              avgt    5     33.177 ±    6.457  ns/op
i.h.substrates.jmh.NameOps.name_path_generation_batch                        avgt    5     35.521 ±    7.769  ns/op
i.h.substrates.jmh.PipeOps.async_emit_batch                                  avgt    5     13.756 ±    1.009  ns/op
i.h.substrates.jmh.PipeOps.async_emit_batch_await                            avgt    5     18.093 ±    1.464  ns/op
i.h.substrates.jmh.PipeOps.async_emit_chained_await                          avgt    5     17.770 ±    0.702  ns/op
i.h.substrates.jmh.PipeOps.async_emit_fanout_await                           avgt    5     18.898 ±    0.151  ns/op
i.h.substrates.jmh.PipeOps.async_emit_single                                 avgt    5      9.903 ±    7.923  ns/op
i.h.substrates.jmh.PipeOps.async_emit_single_await                           avgt    5   6361.335 ±  795.561  ns/op
i.h.substrates.jmh.PipeOps.async_emit_with_flow_await                        avgt    5     20.778 ±    0.429  ns/op
i.h.substrates.jmh.PipeOps.baseline_blackhole                                avgt    5      0.268 ±    0.001  ns/op
i.h.substrates.jmh.PipeOps.baseline_counter                                  avgt    5      1.673 ±    0.044  ns/op
i.h.substrates.jmh.PipeOps.baseline_receptor                                 avgt    5      0.267 ±    0.002  ns/op
i.h.substrates.jmh.PipeOps.pipe_create                                       avgt    5      7.976 ±    0.919  ns/op
i.h.substrates.jmh.PipeOps.pipe_create_chained                               avgt    5      0.836 ±    0.081  ns/op
i.h.substrates.jmh.PipeOps.pipe_create_with_flow                             avgt    5     12.392 ±    0.088  ns/op
i.h.substrates.jmh.ReservoirOps.baseline_emit_no_reservoir_await             avgt    5     96.777 ±    6.264  ns/op
i.h.substrates.jmh.ReservoirOps.baseline_emit_no_reservoir_await_batch       avgt    5     17.174 ±    0.364  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_burst_then_drain_await             avgt    5     87.865 ±   17.108  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_burst_then_drain_await_batch       avgt    5     28.395 ±    0.041  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_drain_await                        avgt    5     91.249 ±    1.996  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_drain_await_batch                  avgt    5     28.433 ±    0.181  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_emit_drain_cycles_await            avgt    5    291.919 ±   15.034  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_emit_with_capture_await            avgt    5     76.516 ±    1.787  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_emit_with_capture_await_batch      avgt    5     23.845 ±    0.732  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_process_emissions_await            avgt    5     92.447 ±    2.398  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_process_emissions_await_batch      avgt    5     26.252 ±    0.147  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_process_subjects_await             avgt    5     94.066 ±   19.715  ns/op
i.h.substrates.jmh.ScopeOps.scope_child_anonymous                            avgt    5     17.249 ±    0.168  ns/op
i.h.substrates.jmh.ScopeOps.scope_child_anonymous_batch                      avgt    5     17.425 ±    0.102  ns/op
i.h.substrates.jmh.ScopeOps.scope_child_named                                avgt    5     17.058 ±    0.090  ns/op
i.h.substrates.jmh.ScopeOps.scope_child_named_batch                          avgt    5     16.948 ±    0.136  ns/op
i.h.substrates.jmh.ScopeOps.scope_close_idempotent                           avgt    5      2.501 ±    0.015  ns/op
i.h.substrates.jmh.ScopeOps.scope_close_idempotent_batch                     avgt    5      0.034 ±    0.001  ns/op
i.h.substrates.jmh.ScopeOps.scope_closure                                    avgt    5    301.936 ±   47.233  ns/op
i.h.substrates.jmh.ScopeOps.scope_closure_batch                              avgt    5    288.283 ±   54.057  ns/op
i.h.substrates.jmh.ScopeOps.scope_complex                                    avgt    5    912.254 ±  202.146  ns/op
i.h.substrates.jmh.ScopeOps.scope_create_and_close                           avgt    5      2.499 ±    0.024  ns/op
i.h.substrates.jmh.ScopeOps.scope_create_and_close_batch                     avgt    5      0.033 ±    0.001  ns/op
i.h.substrates.jmh.ScopeOps.scope_create_named                               avgt    5      2.457 ±    0.078  ns/op
i.h.substrates.jmh.ScopeOps.scope_create_named_batch                         avgt    5      0.033 ±    0.001  ns/op
i.h.substrates.jmh.ScopeOps.scope_hierarchy                                  avgt    5     27.529 ±    0.433  ns/op
i.h.substrates.jmh.ScopeOps.scope_hierarchy_batch                            avgt    5     27.637 ±    0.263  ns/op
i.h.substrates.jmh.ScopeOps.scope_parent_closes_children                     avgt    5     42.998 ±    0.164  ns/op
i.h.substrates.jmh.ScopeOps.scope_parent_closes_children_batch               avgt    5     42.401 ±    3.691  ns/op
i.h.substrates.jmh.ScopeOps.scope_register_multiple                          avgt    5   1527.964 ±  109.958  ns/op
i.h.substrates.jmh.ScopeOps.scope_register_multiple_batch                    avgt    5   1497.065 ±  153.384  ns/op
i.h.substrates.jmh.ScopeOps.scope_register_single                            avgt    5    306.027 ±   38.121  ns/op
i.h.substrates.jmh.ScopeOps.scope_register_single_batch                      avgt    5    302.497 ±   50.636  ns/op
i.h.substrates.jmh.ScopeOps.scope_with_resources                             avgt    5    579.521 ±   77.767  ns/op
i.h.substrates.jmh.StateOps.slot_name                                        avgt    5      0.524 ±    0.002  ns/op
i.h.substrates.jmh.StateOps.slot_name_batch                                  avgt    5      0.001 ±    0.001  ns/op
i.h.substrates.jmh.StateOps.slot_type                                        avgt    5      0.446 ±    0.001  ns/op
i.h.substrates.jmh.StateOps.slot_value                                       avgt    5      0.641 ±    0.001  ns/op
i.h.substrates.jmh.StateOps.slot_value_batch                                 avgt    5      0.001 ±    0.001  ns/op
i.h.substrates.jmh.StateOps.state_compact                                    avgt    5     10.472 ±    0.104  ns/op
i.h.substrates.jmh.StateOps.state_compact_batch                              avgt    5     10.834 ±    0.058  ns/op
i.h.substrates.jmh.StateOps.state_iterate_slots                              avgt    5      2.192 ±    0.019  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_int                               avgt    5      3.741 ±    0.047  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_int_batch                         avgt    5      3.703 ±    0.194  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_long                              avgt    5      3.739 ±    0.078  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_object                            avgt    5      2.464 ±    0.036  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_object_batch                      avgt    5      2.345 ±    0.020  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_string                            avgt    5      3.730 ±    0.052  ns/op
i.h.substrates.jmh.StateOps.state_value_read                                 avgt    5      1.490 ±    0.001  ns/op
i.h.substrates.jmh.StateOps.state_value_read_batch                           avgt    5      1.271 ±    0.001  ns/op
i.h.substrates.jmh.StateOps.state_values_stream                              avgt    5      3.764 ±    0.152  ns/op
i.h.substrates.jmh.SubscriberOps.close_five_conduits_await                   avgt    5   8689.053 ±  388.830  ns/op
i.h.substrates.jmh.SubscriberOps.close_five_subscriptions_await              avgt    5   8660.144 ±  287.759  ns/op
i.h.substrates.jmh.SubscriberOps.close_idempotent_await                      avgt    5   8098.585 ±  671.874  ns/op
i.h.substrates.jmh.SubscriberOps.close_idempotent_batch_await                avgt    5     16.627 ±    0.153  ns/op
i.h.substrates.jmh.SubscriberOps.close_no_subscriptions_await                avgt    5   8442.854 ±  608.240  ns/op
i.h.substrates.jmh.SubscriberOps.close_no_subscriptions_batch_await          avgt    5     14.000 ±    1.410  ns/op
i.h.substrates.jmh.SubscriberOps.close_one_subscription_await                avgt    5   8176.742 ±  943.702  ns/op
i.h.substrates.jmh.SubscriberOps.close_one_subscription_batch_await          avgt    5     34.726 ±    0.432  ns/op
i.h.substrates.jmh.SubscriberOps.close_ten_conduits_await                    avgt    5   8364.300 ±  325.009  ns/op
i.h.substrates.jmh.SubscriberOps.close_ten_subscriptions_await               avgt    5   8492.980 ±  628.219  ns/op
i.h.substrates.jmh.SubscriberOps.close_with_pending_emissions_await          avgt    5   8582.331 ± 1946.445  ns/op
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