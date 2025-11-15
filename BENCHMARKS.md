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

Benchmark                                                       Mode  Cnt      Score     Error  Units
i.h.serventis.jmh.ActorOps.actor_from_conduit                            avgt    5      1.788 ±    0.041  ns/op
i.h.serventis.jmh.ActorOps.actor_from_conduit_batch                      avgt    5      1.614 ±    0.005  ns/op
i.h.serventis.jmh.ActorOps.emit_acknowledge                              avgt    5      8.890 ±    1.326  ns/op
i.h.serventis.jmh.ActorOps.emit_acknowledge_batch                        avgt    5      9.405 ±    0.515  ns/op
i.h.serventis.jmh.ActorOps.emit_affirm                                   avgt    5      8.691 ±    1.079  ns/op
i.h.serventis.jmh.ActorOps.emit_affirm_batch                             avgt    5      9.521 ±    0.791  ns/op
i.h.serventis.jmh.ActorOps.emit_ask                                      avgt    5      8.757 ±    1.163  ns/op
i.h.serventis.jmh.ActorOps.emit_ask_batch                                avgt    5      9.030 ±    1.064  ns/op
i.h.serventis.jmh.ActorOps.emit_clarify                                  avgt    5      8.641 ±    0.670  ns/op
i.h.serventis.jmh.ActorOps.emit_clarify_batch                            avgt    5      9.248 ±    0.316  ns/op
i.h.serventis.jmh.ActorOps.emit_command                                  avgt    5      8.750 ±    1.135  ns/op
i.h.serventis.jmh.ActorOps.emit_command_batch                            avgt    5      8.110 ±    0.923  ns/op
i.h.serventis.jmh.ActorOps.emit_deliver                                  avgt    5      8.443 ±    1.630  ns/op
i.h.serventis.jmh.ActorOps.emit_deliver_batch                            avgt    5      9.066 ±    0.473  ns/op
i.h.serventis.jmh.ActorOps.emit_deny                                     avgt    5      8.382 ±    1.528  ns/op
i.h.serventis.jmh.ActorOps.emit_deny_batch                               avgt    5      9.232 ±    0.665  ns/op
i.h.serventis.jmh.ActorOps.emit_explain                                  avgt    5      8.040 ±    0.596  ns/op
i.h.serventis.jmh.ActorOps.emit_explain_batch                            avgt    5      9.259 ±    0.968  ns/op
i.h.serventis.jmh.ActorOps.emit_promise                                  avgt    5      8.387 ±    1.377  ns/op
i.h.serventis.jmh.ActorOps.emit_promise_batch                            avgt    5      9.038 ±    0.465  ns/op
i.h.serventis.jmh.ActorOps.emit_report                                   avgt    5      8.474 ±    1.184  ns/op
i.h.serventis.jmh.ActorOps.emit_report_batch                             avgt    5      9.191 ±    0.941  ns/op
i.h.serventis.jmh.ActorOps.emit_request                                  avgt    5      8.289 ±    1.631  ns/op
i.h.serventis.jmh.ActorOps.emit_request_batch                            avgt    5      8.991 ±    0.665  ns/op
i.h.serventis.jmh.ActorOps.emit_sign                                     avgt    5      8.286 ±    1.134  ns/op
i.h.serventis.jmh.ActorOps.emit_sign_batch                               avgt    5      8.212 ±    0.791  ns/op
i.h.serventis.jmh.AgentOps.agent_from_conduit                            avgt    5      1.860 ±    0.049  ns/op
i.h.serventis.jmh.AgentOps.agent_from_conduit_batch                      avgt    5      1.661 ±    0.002  ns/op
i.h.serventis.jmh.AgentOps.emit_accept                                   avgt    5      8.764 ±    1.540  ns/op
i.h.serventis.jmh.AgentOps.emit_accept_batch                             avgt    5      8.855 ±    1.214  ns/op
i.h.serventis.jmh.AgentOps.emit_accepted                                 avgt    5      7.880 ±    0.656  ns/op
i.h.serventis.jmh.AgentOps.emit_accepted_batch                           avgt    5      9.481 ±    1.422  ns/op
i.h.serventis.jmh.AgentOps.emit_breach                                   avgt    5      8.593 ±    0.460  ns/op
i.h.serventis.jmh.AgentOps.emit_breach_batch                             avgt    5      8.694 ±    1.381  ns/op
i.h.serventis.jmh.AgentOps.emit_breached                                 avgt    5      8.285 ±    0.817  ns/op
i.h.serventis.jmh.AgentOps.emit_breached_batch                           avgt    5      9.272 ±    0.688  ns/op
i.h.serventis.jmh.AgentOps.emit_depend                                   avgt    5      8.779 ±    1.177  ns/op
i.h.serventis.jmh.AgentOps.emit_depend_batch                             avgt    5      9.357 ±    1.288  ns/op
i.h.serventis.jmh.AgentOps.emit_depended                                 avgt    5      7.938 ±    1.302  ns/op
i.h.serventis.jmh.AgentOps.emit_depended_batch                           avgt    5      9.483 ±    0.468  ns/op
i.h.serventis.jmh.AgentOps.emit_fulfill                                  avgt    5      8.090 ±    1.279  ns/op
i.h.serventis.jmh.AgentOps.emit_fulfill_batch                            avgt    5      8.776 ±    1.170  ns/op
i.h.serventis.jmh.AgentOps.emit_fulfilled                                avgt    5      8.569 ±    0.608  ns/op
i.h.serventis.jmh.AgentOps.emit_fulfilled_batch                          avgt    5      8.784 ±    0.614  ns/op
i.h.serventis.jmh.AgentOps.emit_inquire                                  avgt    5      8.287 ±    0.885  ns/op
i.h.serventis.jmh.AgentOps.emit_inquire_batch                            avgt    5      9.504 ±    1.314  ns/op
i.h.serventis.jmh.AgentOps.emit_inquired                                 avgt    5      8.804 ±    0.926  ns/op
i.h.serventis.jmh.AgentOps.emit_inquired_batch                           avgt    5      9.318 ±    0.849  ns/op
i.h.serventis.jmh.AgentOps.emit_observe                                  avgt    5      8.081 ±    0.947  ns/op
i.h.serventis.jmh.AgentOps.emit_observe_batch                            avgt    5      9.239 ±    1.053  ns/op
i.h.serventis.jmh.AgentOps.emit_observed                                 avgt    5      8.621 ±    1.493  ns/op
i.h.serventis.jmh.AgentOps.emit_observed_batch                           avgt    5      9.668 ±    0.810  ns/op
i.h.serventis.jmh.AgentOps.emit_offer                                    avgt    5      8.355 ±    0.600  ns/op
i.h.serventis.jmh.AgentOps.emit_offer_batch                              avgt    5      9.229 ±    0.878  ns/op
i.h.serventis.jmh.AgentOps.emit_offered                                  avgt    5      7.766 ±    1.710  ns/op
i.h.serventis.jmh.AgentOps.emit_offered_batch                            avgt    5      9.040 ±    1.652  ns/op
i.h.serventis.jmh.AgentOps.emit_promise                                  avgt    5      8.157 ±    1.278  ns/op
i.h.serventis.jmh.AgentOps.emit_promise_batch                            avgt    5      9.806 ±    0.523  ns/op
i.h.serventis.jmh.AgentOps.emit_promised                                 avgt    5      8.490 ±    0.918  ns/op
i.h.serventis.jmh.AgentOps.emit_promised_batch                           avgt    5      9.266 ±    1.447  ns/op
i.h.serventis.jmh.AgentOps.emit_retract                                  avgt    5      8.622 ±    0.402  ns/op
i.h.serventis.jmh.AgentOps.emit_retract_batch                            avgt    5      9.365 ±    1.557  ns/op
i.h.serventis.jmh.AgentOps.emit_retracted                                avgt    5      8.378 ±    1.010  ns/op
i.h.serventis.jmh.AgentOps.emit_retracted_batch                          avgt    5      9.398 ±    1.295  ns/op
i.h.serventis.jmh.AgentOps.emit_signal                                   avgt    5      8.249 ±    0.694  ns/op
i.h.serventis.jmh.AgentOps.emit_signal_batch                             avgt    5      9.695 ±    0.968  ns/op
i.h.serventis.jmh.AgentOps.emit_validate                                 avgt    5      8.004 ±    1.716  ns/op
i.h.serventis.jmh.AgentOps.emit_validate_batch                           avgt    5      8.824 ±    1.089  ns/op
i.h.serventis.jmh.AgentOps.emit_validated                                avgt    5      8.768 ±    1.461  ns/op
i.h.serventis.jmh.AgentOps.emit_validated_batch                          avgt    5      8.615 ±    0.865  ns/op
i.h.serventis.jmh.BreakerOps.breaker_from_conduit                        avgt    5      1.858 ±    0.036  ns/op
i.h.serventis.jmh.BreakerOps.breaker_from_conduit_batch                  avgt    5      1.662 ±    0.009  ns/op
i.h.serventis.jmh.BreakerOps.emit_close                                  avgt    5      8.047 ±    0.577  ns/op
i.h.serventis.jmh.BreakerOps.emit_close_batch                            avgt    5      7.615 ±    1.047  ns/op
i.h.serventis.jmh.BreakerOps.emit_half_open                              avgt    5      7.981 ±    1.619  ns/op
i.h.serventis.jmh.BreakerOps.emit_half_open_batch                        avgt    5      8.954 ±    0.634  ns/op
i.h.serventis.jmh.BreakerOps.emit_open                                   avgt    5      7.910 ±    0.936  ns/op
i.h.serventis.jmh.BreakerOps.emit_open_batch                             avgt    5      8.207 ±    0.982  ns/op
i.h.serventis.jmh.BreakerOps.emit_probe                                  avgt    5      8.017 ±    1.546  ns/op
i.h.serventis.jmh.BreakerOps.emit_probe_batch                            avgt    5      8.786 ±    0.665  ns/op
i.h.serventis.jmh.BreakerOps.emit_reset                                  avgt    5      8.036 ±    0.872  ns/op
i.h.serventis.jmh.BreakerOps.emit_reset_batch                            avgt    5      9.078 ±    0.563  ns/op
i.h.serventis.jmh.BreakerOps.emit_sign                                   avgt    5      7.363 ±    0.753  ns/op
i.h.serventis.jmh.BreakerOps.emit_sign_batch                             avgt    5      8.772 ±    0.716  ns/op
i.h.serventis.jmh.BreakerOps.emit_trip                                   avgt    5      7.855 ±    1.326  ns/op
i.h.serventis.jmh.BreakerOps.emit_trip_batch                             avgt    5      8.775 ±    0.880  ns/op
i.h.serventis.jmh.CacheOps.cache_from_conduit                            avgt    5      1.849 ±    0.014  ns/op
i.h.serventis.jmh.CacheOps.cache_from_conduit_batch                      avgt    5      1.662 ±    0.008  ns/op
i.h.serventis.jmh.CacheOps.emit_evict                                    avgt    5      8.515 ±    0.699  ns/op
i.h.serventis.jmh.CacheOps.emit_evict_batch                              avgt    5      9.095 ±    0.618  ns/op
i.h.serventis.jmh.CacheOps.emit_expire                                   avgt    5      8.350 ±    0.786  ns/op
i.h.serventis.jmh.CacheOps.emit_expire_batch                             avgt    5      8.925 ±    0.729  ns/op
i.h.serventis.jmh.CacheOps.emit_hit                                      avgt    5      8.242 ±    0.138  ns/op
i.h.serventis.jmh.CacheOps.emit_hit_batch                                avgt    5      8.147 ±    0.759  ns/op
i.h.serventis.jmh.CacheOps.emit_lookup                                   avgt    5      8.199 ±    0.785  ns/op
i.h.serventis.jmh.CacheOps.emit_lookup_batch                             avgt    5      8.981 ±    0.509  ns/op
i.h.serventis.jmh.CacheOps.emit_miss                                     avgt    5      7.986 ±    1.135  ns/op
i.h.serventis.jmh.CacheOps.emit_miss_batch                               avgt    5      7.712 ±    1.243  ns/op
i.h.serventis.jmh.CacheOps.emit_remove                                   avgt    5      8.278 ±    0.730  ns/op
i.h.serventis.jmh.CacheOps.emit_remove_batch                             avgt    5      7.629 ±    1.508  ns/op
i.h.serventis.jmh.CacheOps.emit_sign                                     avgt    5      7.734 ±    1.233  ns/op
i.h.serventis.jmh.CacheOps.emit_sign_batch                               avgt    5      7.872 ±    0.851  ns/op
i.h.serventis.jmh.CacheOps.emit_store                                    avgt    5      8.134 ±    0.731  ns/op
i.h.serventis.jmh.CacheOps.emit_store_batch                              avgt    5      8.117 ±    0.629  ns/op
i.h.serventis.jmh.CounterOps.counter_from_conduit                        avgt    5      1.859 ±    0.043  ns/op
i.h.serventis.jmh.CounterOps.counter_from_conduit_batch                  avgt    5      1.661 ±    0.005  ns/op
i.h.serventis.jmh.CounterOps.emit_increment                              avgt    5      8.201 ±    0.791  ns/op
i.h.serventis.jmh.CounterOps.emit_increment_batch                        avgt    5      8.929 ±    0.723  ns/op
i.h.serventis.jmh.CounterOps.emit_overflow                               avgt    5      7.911 ±    1.758  ns/op
i.h.serventis.jmh.CounterOps.emit_overflow_batch                         avgt    5      8.267 ±    0.452  ns/op
i.h.serventis.jmh.CounterOps.emit_reset                                  avgt    5      8.371 ±    0.590  ns/op
i.h.serventis.jmh.CounterOps.emit_reset_batch                            avgt    5      8.888 ±    1.128  ns/op
i.h.serventis.jmh.CounterOps.emit_sign                                   avgt    5      8.287 ±    0.963  ns/op
i.h.serventis.jmh.CounterOps.emit_sign_batch                             avgt    5      8.854 ±    0.637  ns/op
i.h.serventis.jmh.GaugeOps.emit_decrement                                avgt    5      8.238 ±    0.976  ns/op
i.h.serventis.jmh.GaugeOps.emit_decrement_batch                          avgt    5      8.260 ±    0.317  ns/op
i.h.serventis.jmh.GaugeOps.emit_increment                                avgt    5      8.092 ±    1.267  ns/op
i.h.serventis.jmh.GaugeOps.emit_increment_batch                          avgt    5      8.735 ±    0.736  ns/op
i.h.serventis.jmh.GaugeOps.emit_overflow                                 avgt    5      8.265 ±    0.927  ns/op
i.h.serventis.jmh.GaugeOps.emit_overflow_batch                           avgt    5      8.902 ±    0.896  ns/op
i.h.serventis.jmh.GaugeOps.emit_reset                                    avgt    5      8.220 ±    1.082  ns/op
i.h.serventis.jmh.GaugeOps.emit_reset_batch                              avgt    5      8.979 ±    0.677  ns/op
i.h.serventis.jmh.GaugeOps.emit_sign                                     avgt    5      8.232 ±    0.989  ns/op
i.h.serventis.jmh.GaugeOps.emit_sign_batch                               avgt    5      8.286 ±    0.764  ns/op
i.h.serventis.jmh.GaugeOps.emit_underflow                                avgt    5      8.221 ±    1.223  ns/op
i.h.serventis.jmh.GaugeOps.emit_underflow_batch                          avgt    5      9.074 ±    0.767  ns/op
i.h.serventis.jmh.GaugeOps.gauge_from_conduit                            avgt    5      1.861 ±    0.051  ns/op
i.h.serventis.jmh.GaugeOps.gauge_from_conduit_batch                      avgt    5      1.661 ±    0.004  ns/op
i.h.serventis.jmh.LeaseOps.emit_acquire                                  avgt    5      8.638 ±    1.280  ns/op
i.h.serventis.jmh.LeaseOps.emit_acquire_batch                            avgt    5      9.568 ±    1.362  ns/op
i.h.serventis.jmh.LeaseOps.emit_acquired                                 avgt    5      8.761 ±    1.269  ns/op
i.h.serventis.jmh.LeaseOps.emit_acquired_batch                           avgt    5      9.056 ±    0.820  ns/op
i.h.serventis.jmh.LeaseOps.emit_denied                                   avgt    5      8.502 ±    0.259  ns/op
i.h.serventis.jmh.LeaseOps.emit_denied_batch                             avgt    5      9.851 ±    0.973  ns/op
i.h.serventis.jmh.LeaseOps.emit_deny                                     avgt    5      8.220 ±    0.690  ns/op
i.h.serventis.jmh.LeaseOps.emit_deny_batch                               avgt    5      9.526 ±    1.102  ns/op
i.h.serventis.jmh.LeaseOps.emit_expire                                   avgt    5      7.986 ±    0.851  ns/op
i.h.serventis.jmh.LeaseOps.emit_expire_batch                             avgt    5      8.797 ±    0.783  ns/op
i.h.serventis.jmh.LeaseOps.emit_expired                                  avgt    5      8.626 ±    0.767  ns/op
i.h.serventis.jmh.LeaseOps.emit_expired_batch                            avgt    5      8.647 ±    1.078  ns/op
i.h.serventis.jmh.LeaseOps.emit_extend                                   avgt    5      9.156 ±    0.483  ns/op
i.h.serventis.jmh.LeaseOps.emit_extend_batch                             avgt    5      8.636 ±    0.929  ns/op
i.h.serventis.jmh.LeaseOps.emit_extended                                 avgt    5      8.091 ±    1.197  ns/op
i.h.serventis.jmh.LeaseOps.emit_extended_batch                           avgt    5      9.830 ±    0.263  ns/op
i.h.serventis.jmh.LeaseOps.emit_grant                                    avgt    5      8.424 ±    0.997  ns/op
i.h.serventis.jmh.LeaseOps.emit_grant_batch                              avgt    5      9.235 ±    1.971  ns/op
i.h.serventis.jmh.LeaseOps.emit_granted                                  avgt    5      8.427 ±    0.483  ns/op
i.h.serventis.jmh.LeaseOps.emit_granted_batch                            avgt    5      9.661 ±    0.445  ns/op
i.h.serventis.jmh.LeaseOps.emit_probe                                    avgt    5      8.898 ±    0.487  ns/op
i.h.serventis.jmh.LeaseOps.emit_probe_batch                              avgt    5      7.860 ±    1.254  ns/op
i.h.serventis.jmh.LeaseOps.emit_probed                                   avgt    5      7.809 ±    0.634  ns/op
i.h.serventis.jmh.LeaseOps.emit_probed_batch                             avgt    5      9.744 ±    0.916  ns/op
i.h.serventis.jmh.LeaseOps.emit_release                                  avgt    5      7.584 ±    0.677  ns/op
i.h.serventis.jmh.LeaseOps.emit_release_batch                            avgt    5      9.795 ±    0.908  ns/op
i.h.serventis.jmh.LeaseOps.emit_released                                 avgt    5      8.182 ±    0.419  ns/op
i.h.serventis.jmh.LeaseOps.emit_released_batch                           avgt    5      9.368 ±    0.709  ns/op
i.h.serventis.jmh.LeaseOps.emit_renew                                    avgt    5      8.173 ±    1.352  ns/op
i.h.serventis.jmh.LeaseOps.emit_renew_batch                              avgt    5      9.283 ±    0.950  ns/op
i.h.serventis.jmh.LeaseOps.emit_renewed                                  avgt    5      8.768 ±    0.735  ns/op
i.h.serventis.jmh.LeaseOps.emit_renewed_batch                            avgt    5      9.065 ±    1.394  ns/op
i.h.serventis.jmh.LeaseOps.emit_revoke                                   avgt    5      8.102 ±    0.986  ns/op
i.h.serventis.jmh.LeaseOps.emit_revoke_batch                             avgt    5      9.246 ±    1.293  ns/op
i.h.serventis.jmh.LeaseOps.emit_revoked                                  avgt    5     10.091 ±    0.376  ns/op
i.h.serventis.jmh.LeaseOps.emit_revoked_batch                            avgt    5      7.893 ±    0.521  ns/op
i.h.serventis.jmh.LeaseOps.emit_signal                                   avgt    5      8.854 ±    1.223  ns/op
i.h.serventis.jmh.LeaseOps.emit_signal_batch                             avgt    5      8.715 ±    0.816  ns/op
i.h.serventis.jmh.LeaseOps.lease_from_conduit                            avgt    5      1.859 ±    0.061  ns/op
i.h.serventis.jmh.LeaseOps.lease_from_conduit_batch                      avgt    5      1.666 ±    0.005  ns/op
i.h.serventis.jmh.MonitorOps.emit_converging_confirmed                   avgt    5      8.503 ±    1.749  ns/op
i.h.serventis.jmh.MonitorOps.emit_converging_confirmed_batch             avgt    5      8.398 ±    0.703  ns/op
i.h.serventis.jmh.MonitorOps.emit_defective_tentative                    avgt    5      8.152 ±    1.027  ns/op
i.h.serventis.jmh.MonitorOps.emit_defective_tentative_batch              avgt    5      8.435 ±    0.799  ns/op
i.h.serventis.jmh.MonitorOps.emit_degraded_measured                      avgt    5      9.247 ±    1.462  ns/op
i.h.serventis.jmh.MonitorOps.emit_degraded_measured_batch                avgt    5      9.956 ±    0.428  ns/op
i.h.serventis.jmh.MonitorOps.emit_down_confirmed                         avgt    5      8.763 ±    0.602  ns/op
i.h.serventis.jmh.MonitorOps.emit_down_confirmed_batch                   avgt    5      9.439 ±    0.604  ns/op
i.h.serventis.jmh.MonitorOps.emit_signal                                 avgt    5      8.145 ±    1.269  ns/op
i.h.serventis.jmh.MonitorOps.emit_signal_batch                           avgt    5      8.569 ±    0.614  ns/op
i.h.serventis.jmh.MonitorOps.emit_stable_confirmed                       avgt    5      8.809 ±    1.105  ns/op
i.h.serventis.jmh.MonitorOps.emit_stable_confirmed_batch                 avgt    5      9.536 ±    1.271  ns/op
i.h.serventis.jmh.MonitorOps.monitor_from_conduit                        avgt    5      1.866 ±    0.129  ns/op
i.h.serventis.jmh.MonitorOps.monitor_from_conduit_batch                  avgt    5      1.666 ±    0.013  ns/op
i.h.serventis.jmh.ProbeOps.emit_connect                                  avgt    5      8.531 ±    2.320  ns/op
i.h.serventis.jmh.ProbeOps.emit_connect_batch                            avgt    5      8.800 ±    1.401  ns/op
i.h.serventis.jmh.ProbeOps.emit_connected                                avgt    5      8.149 ±    1.329  ns/op
i.h.serventis.jmh.ProbeOps.emit_connected_batch                          avgt    5      9.425 ±    1.056  ns/op
i.h.serventis.jmh.ProbeOps.emit_disconnect                               avgt    5      8.377 ±    0.697  ns/op
i.h.serventis.jmh.ProbeOps.emit_disconnect_batch                         avgt    5      9.680 ±    0.889  ns/op
i.h.serventis.jmh.ProbeOps.emit_disconnected                             avgt    5      8.329 ±    0.721  ns/op
i.h.serventis.jmh.ProbeOps.emit_disconnected_batch                       avgt    5      9.821 ±    0.999  ns/op
i.h.serventis.jmh.ProbeOps.emit_fail                                     avgt    5      7.598 ±    0.385  ns/op
i.h.serventis.jmh.ProbeOps.emit_fail_batch                               avgt    5      9.574 ±    0.556  ns/op
i.h.serventis.jmh.ProbeOps.emit_failed                                   avgt    5      8.364 ±    0.571  ns/op
i.h.serventis.jmh.ProbeOps.emit_failed_batch                             avgt    5      7.432 ±    1.209  ns/op
i.h.serventis.jmh.ProbeOps.emit_process                                  avgt    5      7.479 ±    0.500  ns/op
i.h.serventis.jmh.ProbeOps.emit_process_batch                            avgt    5      9.273 ±    0.770  ns/op
i.h.serventis.jmh.ProbeOps.emit_processed                                avgt    5      8.369 ±    0.524  ns/op
i.h.serventis.jmh.ProbeOps.emit_processed_batch                          avgt    5      8.620 ±    0.585  ns/op
i.h.serventis.jmh.ProbeOps.emit_receive_batch                            avgt    5      8.980 ±    0.381  ns/op
i.h.serventis.jmh.ProbeOps.emit_received_batch                           avgt    5      9.716 ±    1.279  ns/op
i.h.serventis.jmh.ProbeOps.emit_signal                                   avgt    5      7.910 ±    1.229  ns/op
i.h.serventis.jmh.ProbeOps.emit_signal_batch                             avgt    5      9.574 ±    0.612  ns/op
i.h.serventis.jmh.ProbeOps.emit_succeed                                  avgt    5      8.038 ±    1.418  ns/op
i.h.serventis.jmh.ProbeOps.emit_succeed_batch                            avgt    5      9.638 ±    0.606  ns/op
i.h.serventis.jmh.ProbeOps.emit_succeeded                                avgt    5      8.985 ±    0.556  ns/op
i.h.serventis.jmh.ProbeOps.emit_succeeded_batch                          avgt    5      9.375 ±    0.563  ns/op
i.h.serventis.jmh.ProbeOps.emit_transfer                                 avgt    5      8.418 ±    1.340  ns/op
i.h.serventis.jmh.ProbeOps.emit_transfer_inbound                         avgt    5      8.217 ±    1.764  ns/op
i.h.serventis.jmh.ProbeOps.emit_transfer_outbound                        avgt    5      7.710 ±    0.247  ns/op
i.h.serventis.jmh.ProbeOps.emit_transferred                              avgt    5      8.095 ±    1.327  ns/op
i.h.serventis.jmh.ProbeOps.emit_transmit_batch                           avgt    5      8.619 ±    0.871  ns/op
i.h.serventis.jmh.ProbeOps.emit_transmitted_batch                        avgt    5      9.491 ±    1.067  ns/op
i.h.serventis.jmh.ProbeOps.probe_from_conduit                            avgt    5      1.861 ±    0.064  ns/op
i.h.serventis.jmh.ProbeOps.probe_from_conduit_batch                      avgt    5      1.666 ±    0.009  ns/op
i.h.serventis.jmh.QueueOps.emit_dequeue                                  avgt    5      7.844 ±    0.488  ns/op
i.h.serventis.jmh.QueueOps.emit_dequeue_batch                            avgt    5      8.764 ±    0.516  ns/op
i.h.serventis.jmh.QueueOps.emit_enqueue                                  avgt    5      7.913 ±    1.658  ns/op
i.h.serventis.jmh.QueueOps.emit_enqueue_batch                            avgt    5      8.976 ±    0.901  ns/op
i.h.serventis.jmh.QueueOps.emit_overflow                                 avgt    5      8.296 ±    0.901  ns/op
i.h.serventis.jmh.QueueOps.emit_overflow_batch                           avgt    5      9.129 ±    0.493  ns/op
i.h.serventis.jmh.QueueOps.emit_sign                                     avgt    5      8.132 ±    1.495  ns/op
i.h.serventis.jmh.QueueOps.emit_sign_batch                               avgt    5      9.182 ±    0.803  ns/op
i.h.serventis.jmh.QueueOps.emit_underflow                                avgt    5      7.861 ±    1.268  ns/op
i.h.serventis.jmh.QueueOps.emit_underflow_batch                          avgt    5      7.246 ±    0.929  ns/op
i.h.serventis.jmh.QueueOps.queue_from_conduit                            avgt    5      1.861 ±    0.043  ns/op
i.h.serventis.jmh.QueueOps.queue_from_conduit_batch                      avgt    5      1.660 ±    0.004  ns/op
i.h.serventis.jmh.ReporterOps.emit_critical                              avgt    5      8.074 ±    0.800  ns/op
i.h.serventis.jmh.ReporterOps.emit_critical_batch                        avgt    5      9.079 ±    0.614  ns/op
i.h.serventis.jmh.ReporterOps.emit_normal                                avgt    5      8.290 ±    0.957  ns/op
i.h.serventis.jmh.ReporterOps.emit_normal_batch                          avgt    5      8.776 ±    0.753  ns/op
i.h.serventis.jmh.ReporterOps.emit_sign                                  avgt    5      8.049 ±    0.766  ns/op
i.h.serventis.jmh.ReporterOps.emit_sign_batch                            avgt    5      7.476 ±    1.264  ns/op
i.h.serventis.jmh.ReporterOps.emit_warning                               avgt    5      7.826 ±    1.129  ns/op
i.h.serventis.jmh.ReporterOps.emit_warning_batch                         avgt    5      8.448 ±    1.432  ns/op
i.h.serventis.jmh.ReporterOps.reporter_from_conduit                      avgt    5      1.852 ±    0.026  ns/op
i.h.serventis.jmh.ReporterOps.reporter_from_conduit_batch                avgt    5      1.661 ±    0.009  ns/op
i.h.serventis.jmh.ResourceOps.emit_acquire                               avgt    5      7.743 ±    1.384  ns/op
i.h.serventis.jmh.ResourceOps.emit_acquire_batch                         avgt    5      8.804 ±    1.110  ns/op
i.h.serventis.jmh.ResourceOps.emit_attempt                               avgt    5      8.199 ±    0.792  ns/op
i.h.serventis.jmh.ResourceOps.emit_attempt_batch                         avgt    5      7.245 ±    0.536  ns/op
i.h.serventis.jmh.ResourceOps.emit_deny                                  avgt    5      8.201 ±    0.795  ns/op
i.h.serventis.jmh.ResourceOps.emit_deny_batch                            avgt    5      8.983 ±    0.755  ns/op
i.h.serventis.jmh.ResourceOps.emit_grant                                 avgt    5      7.473 ±    0.741  ns/op
i.h.serventis.jmh.ResourceOps.emit_grant_batch                           avgt    5      8.663 ±    1.166  ns/op
i.h.serventis.jmh.ResourceOps.emit_release                               avgt    5      8.261 ±    0.998  ns/op
i.h.serventis.jmh.ResourceOps.emit_release_batch                         avgt    5      8.711 ±    0.244  ns/op
i.h.serventis.jmh.ResourceOps.emit_sign                                  avgt    5      7.973 ±    0.914  ns/op
i.h.serventis.jmh.ResourceOps.emit_sign_batch                            avgt    5      7.968 ±    1.074  ns/op
i.h.serventis.jmh.ResourceOps.emit_timeout                               avgt    5      8.081 ±    0.619  ns/op
i.h.serventis.jmh.ResourceOps.emit_timeout_batch                         avgt    5      8.911 ±    0.564  ns/op
i.h.serventis.jmh.ResourceOps.resource_from_conduit                      avgt    5      1.853 ±    0.046  ns/op
i.h.serventis.jmh.ResourceOps.resource_from_conduit_batch                avgt    5      1.662 ±    0.015  ns/op
i.h.serventis.jmh.RouterOps.emit_corrupt                                 avgt    5      8.236 ±    1.015  ns/op
i.h.serventis.jmh.RouterOps.emit_corrupt_batch                           avgt    5      9.119 ±    0.523  ns/op
i.h.serventis.jmh.RouterOps.emit_drop                                    avgt    5      9.634 ±    1.763  ns/op
i.h.serventis.jmh.RouterOps.emit_drop_batch                              avgt    5      8.801 ±    0.364  ns/op
i.h.serventis.jmh.RouterOps.emit_forward                                 avgt    5      7.982 ±    0.619  ns/op
i.h.serventis.jmh.RouterOps.emit_forward_batch                           avgt    5      7.065 ±    1.525  ns/op
i.h.serventis.jmh.RouterOps.emit_fragment                                avgt    5      8.276 ±    1.234  ns/op
i.h.serventis.jmh.RouterOps.emit_fragment_batch                          avgt    5      9.135 ±    1.068  ns/op
i.h.serventis.jmh.RouterOps.emit_reassemble                              avgt    5      8.395 ±    0.764  ns/op
i.h.serventis.jmh.RouterOps.emit_reassemble_batch                        avgt    5      8.637 ±    0.870  ns/op
i.h.serventis.jmh.RouterOps.emit_receive                                 avgt    5      7.834 ±    1.128  ns/op
i.h.serventis.jmh.RouterOps.emit_receive_batch                           avgt    5      8.994 ±    0.532  ns/op
i.h.serventis.jmh.RouterOps.emit_reorder                                 avgt    5      7.966 ±    1.643  ns/op
i.h.serventis.jmh.RouterOps.emit_reorder_batch                           avgt    5      9.184 ±    0.561  ns/op
i.h.serventis.jmh.RouterOps.emit_route                                   avgt    5      8.197 ±    0.823  ns/op
i.h.serventis.jmh.RouterOps.emit_route_batch                             avgt    5      8.692 ±    0.888  ns/op
i.h.serventis.jmh.RouterOps.emit_send                                    avgt    5      8.034 ±    1.335  ns/op
i.h.serventis.jmh.RouterOps.emit_send_batch                              avgt    5      8.751 ±    0.375  ns/op
i.h.serventis.jmh.RouterOps.emit_sign                                    avgt    5      7.578 ±    1.202  ns/op
i.h.serventis.jmh.RouterOps.emit_sign_batch                              avgt    5      8.804 ±    0.466  ns/op
i.h.serventis.jmh.RouterOps.router_from_conduit                          avgt    5      1.858 ±    0.050  ns/op
i.h.serventis.jmh.RouterOps.router_from_conduit_batch                    avgt    5      1.661 ±    0.007  ns/op
i.h.serventis.jmh.ServiceOps.emit_call                                   avgt    5      8.191 ±    1.554  ns/op
i.h.serventis.jmh.ServiceOps.emit_call_batch                             avgt    5      8.627 ±    1.014  ns/op
i.h.serventis.jmh.ServiceOps.emit_called                                 avgt    5      8.532 ±    1.148  ns/op
i.h.serventis.jmh.ServiceOps.emit_called_batch                           avgt    5      7.137 ±    0.797  ns/op
i.h.serventis.jmh.ServiceOps.emit_delay                                  avgt    5      8.422 ±    1.040  ns/op
i.h.serventis.jmh.ServiceOps.emit_delay_batch                            avgt    5      9.178 ±    1.036  ns/op
i.h.serventis.jmh.ServiceOps.emit_delayed                                avgt    5      8.518 ±    1.303  ns/op
i.h.serventis.jmh.ServiceOps.emit_delayed_batch                          avgt    5      9.703 ±    1.036  ns/op
i.h.serventis.jmh.ServiceOps.emit_discard                                avgt    5      7.962 ±    1.020  ns/op
i.h.serventis.jmh.ServiceOps.emit_discard_batch                          avgt    5      9.501 ±    1.852  ns/op
i.h.serventis.jmh.ServiceOps.emit_discarded                              avgt    5      8.672 ±    1.193  ns/op
i.h.serventis.jmh.ServiceOps.emit_discarded_batch                        avgt    5      9.576 ±    0.665  ns/op
i.h.serventis.jmh.ServiceOps.emit_disconnect                             avgt    5     10.136 ±    0.760  ns/op
i.h.serventis.jmh.ServiceOps.emit_disconnect_batch                       avgt    5      9.560 ±    0.303  ns/op
i.h.serventis.jmh.ServiceOps.emit_disconnected                           avgt    5      8.370 ±    1.609  ns/op
i.h.serventis.jmh.ServiceOps.emit_disconnected_batch                     avgt    5      9.296 ±    1.059  ns/op
i.h.serventis.jmh.ServiceOps.emit_expire                                 avgt    5      8.239 ±    1.194  ns/op
i.h.serventis.jmh.ServiceOps.emit_expire_batch                           avgt    5      9.468 ±    0.723  ns/op
i.h.serventis.jmh.ServiceOps.emit_expired                                avgt    5      8.589 ±    0.941  ns/op
i.h.serventis.jmh.ServiceOps.emit_expired_batch                          avgt    5      9.362 ±    1.222  ns/op
i.h.serventis.jmh.ServiceOps.emit_fail                                   avgt    5      8.633 ±    1.533  ns/op
i.h.serventis.jmh.ServiceOps.emit_fail_batch                             avgt    5      9.186 ±    1.268  ns/op
i.h.serventis.jmh.ServiceOps.emit_failed                                 avgt    5      8.412 ±    1.141  ns/op
i.h.serventis.jmh.ServiceOps.emit_failed_batch                           avgt    5      8.729 ±    0.394  ns/op
i.h.serventis.jmh.ServiceOps.emit_recourse                               avgt    5      8.836 ±    1.225  ns/op
i.h.serventis.jmh.ServiceOps.emit_recourse_batch                         avgt    5      8.921 ±    1.015  ns/op
i.h.serventis.jmh.ServiceOps.emit_recoursed                              avgt    5      7.873 ±    0.402  ns/op
i.h.serventis.jmh.ServiceOps.emit_recoursed_batch                        avgt    5      8.634 ±    2.020  ns/op
i.h.serventis.jmh.ServiceOps.emit_redirect                               avgt    5      7.924 ±    1.157  ns/op
i.h.serventis.jmh.ServiceOps.emit_redirect_batch                         avgt    5      9.647 ±    1.227  ns/op
i.h.serventis.jmh.ServiceOps.emit_redirected                             avgt    5      8.895 ±    0.252  ns/op
i.h.serventis.jmh.ServiceOps.emit_redirected_batch                       avgt    5      9.489 ±    0.949  ns/op
i.h.serventis.jmh.ServiceOps.emit_reject                                 avgt    5      8.068 ±    1.090  ns/op
i.h.serventis.jmh.ServiceOps.emit_reject_batch                           avgt    5      8.802 ±    1.350  ns/op
i.h.serventis.jmh.ServiceOps.emit_rejected                               avgt    5      8.063 ±    0.592  ns/op
i.h.serventis.jmh.ServiceOps.emit_rejected_batch                         avgt    5      8.778 ±    0.543  ns/op
i.h.serventis.jmh.ServiceOps.emit_resume                                 avgt    5      8.696 ±    0.745  ns/op
i.h.serventis.jmh.ServiceOps.emit_resume_batch                           avgt    5      8.812 ±    1.362  ns/op
i.h.serventis.jmh.ServiceOps.emit_resumed                                avgt    5      8.113 ±    1.535  ns/op
i.h.serventis.jmh.ServiceOps.emit_resumed_batch                          avgt    5      9.272 ±    1.206  ns/op
i.h.serventis.jmh.ServiceOps.emit_retried                                avgt    5      8.720 ±    1.393  ns/op
i.h.serventis.jmh.ServiceOps.emit_retried_batch                          avgt    5      8.712 ±    1.463  ns/op
i.h.serventis.jmh.ServiceOps.emit_retry                                  avgt    5      8.723 ±    0.759  ns/op
i.h.serventis.jmh.ServiceOps.emit_retry_batch                            avgt    5      9.748 ±    0.227  ns/op
i.h.serventis.jmh.ServiceOps.emit_schedule                               avgt    5      8.378 ±    1.056  ns/op
i.h.serventis.jmh.ServiceOps.emit_schedule_batch                         avgt    5      9.207 ±    0.549  ns/op
i.h.serventis.jmh.ServiceOps.emit_scheduled                              avgt    5      8.303 ±    0.906  ns/op
i.h.serventis.jmh.ServiceOps.emit_scheduled_batch                        avgt    5      8.902 ±    1.028  ns/op
i.h.serventis.jmh.ServiceOps.emit_signal                                 avgt    5      8.061 ±    0.911  ns/op
i.h.serventis.jmh.ServiceOps.emit_signal_batch                           avgt    5      9.281 ±    0.883  ns/op
i.h.serventis.jmh.ServiceOps.emit_start                                  avgt    5      8.092 ±    1.339  ns/op
i.h.serventis.jmh.ServiceOps.emit_start_batch                            avgt    5      9.415 ±    0.953  ns/op
i.h.serventis.jmh.ServiceOps.emit_started                                avgt    5      8.292 ±    0.623  ns/op
i.h.serventis.jmh.ServiceOps.emit_started_batch                          avgt    5      8.616 ±    1.204  ns/op
i.h.serventis.jmh.ServiceOps.emit_stop                                   avgt    5      8.802 ±    1.037  ns/op
i.h.serventis.jmh.ServiceOps.emit_stop_batch                             avgt    5      9.345 ±    2.674  ns/op
i.h.serventis.jmh.ServiceOps.emit_stopped                                avgt    5      8.543 ±    1.300  ns/op
i.h.serventis.jmh.ServiceOps.emit_stopped_batch                          avgt    5      9.449 ±    1.012  ns/op
i.h.serventis.jmh.ServiceOps.emit_succeeded                              avgt    5      7.938 ±    0.700  ns/op
i.h.serventis.jmh.ServiceOps.emit_succeeded_batch                        avgt    5      9.045 ±    1.407  ns/op
i.h.serventis.jmh.ServiceOps.emit_success                                avgt    5      7.919 ±    1.210  ns/op
i.h.serventis.jmh.ServiceOps.emit_success_batch                          avgt    5      9.078 ±    1.513  ns/op
i.h.serventis.jmh.ServiceOps.emit_suspend                                avgt    5      8.130 ±    0.986  ns/op
i.h.serventis.jmh.ServiceOps.emit_suspend_batch                          avgt    5      9.602 ±    0.556  ns/op
i.h.serventis.jmh.ServiceOps.emit_suspended                              avgt    5      8.744 ±    1.191  ns/op
i.h.serventis.jmh.ServiceOps.emit_suspended_batch                        avgt    5      9.846 ±    1.901  ns/op
i.h.serventis.jmh.ServiceOps.service_from_conduit                        avgt    5      1.857 ±    0.047  ns/op
i.h.serventis.jmh.ServiceOps.service_from_conduit_batch                  avgt    5      1.661 ±    0.005  ns/op
i.h.serventis.jmh.TransactionOps.emit_abort_coordinator                  avgt    5      8.196 ±    1.051  ns/op
i.h.serventis.jmh.TransactionOps.emit_abort_coordinator_batch            avgt    5      9.466 ±    1.604  ns/op
i.h.serventis.jmh.TransactionOps.emit_abort_participant                  avgt    5      8.875 ±    0.392  ns/op
i.h.serventis.jmh.TransactionOps.emit_abort_participant_batch            avgt    5      7.767 ±    0.909  ns/op
i.h.serventis.jmh.TransactionOps.emit_commit_coordinator                 avgt    5      8.275 ±    0.921  ns/op
i.h.serventis.jmh.TransactionOps.emit_commit_coordinator_batch           avgt    5      7.670 ±    0.956  ns/op
i.h.serventis.jmh.TransactionOps.emit_commit_participant                 avgt    5      7.962 ±    0.743  ns/op
i.h.serventis.jmh.TransactionOps.emit_commit_participant_batch           avgt    5      8.621 ±    0.962  ns/op
i.h.serventis.jmh.TransactionOps.emit_compensate_coordinator             avgt    5      8.514 ±    0.800  ns/op
i.h.serventis.jmh.TransactionOps.emit_compensate_coordinator_batch       avgt    5      8.964 ±    0.944  ns/op
i.h.serventis.jmh.TransactionOps.emit_compensate_participant             avgt    5      8.336 ±    0.852  ns/op
i.h.serventis.jmh.TransactionOps.emit_compensate_participant_batch       avgt    5      9.411 ±    1.440  ns/op
i.h.serventis.jmh.TransactionOps.emit_conflict_coordinator               avgt    5      8.778 ±    0.690  ns/op
i.h.serventis.jmh.TransactionOps.emit_conflict_coordinator_batch         avgt    5      7.714 ±    1.081  ns/op
i.h.serventis.jmh.TransactionOps.emit_conflict_participant               avgt    5      8.115 ±    0.542  ns/op
i.h.serventis.jmh.TransactionOps.emit_conflict_participant_batch         avgt    5      7.968 ±    1.091  ns/op
i.h.serventis.jmh.TransactionOps.emit_expire_coordinator                 avgt    5      8.204 ±    0.615  ns/op
i.h.serventis.jmh.TransactionOps.emit_expire_coordinator_batch           avgt    5      9.446 ±    0.819  ns/op
i.h.serventis.jmh.TransactionOps.emit_expire_participant                 avgt    5      7.990 ±    0.858  ns/op
i.h.serventis.jmh.TransactionOps.emit_expire_participant_batch           avgt    5      9.503 ±    0.475  ns/op
i.h.serventis.jmh.TransactionOps.emit_prepare_coordinator                avgt    5      8.205 ±    1.120  ns/op
i.h.serventis.jmh.TransactionOps.emit_prepare_coordinator_batch          avgt    5      8.715 ±    0.987  ns/op
i.h.serventis.jmh.TransactionOps.emit_prepare_participant                avgt    5      7.538 ±    1.501  ns/op
i.h.serventis.jmh.TransactionOps.emit_prepare_participant_batch          avgt    5      9.422 ±    1.281  ns/op
i.h.serventis.jmh.TransactionOps.emit_rollback_coordinator               avgt    5      7.964 ±    1.180  ns/op
i.h.serventis.jmh.TransactionOps.emit_rollback_coordinator_batch         avgt    5      8.441 ±    1.124  ns/op
i.h.serventis.jmh.TransactionOps.emit_rollback_participant               avgt    5      7.788 ±    0.822  ns/op
i.h.serventis.jmh.TransactionOps.emit_rollback_participant_batch         avgt    5      9.554 ±    1.457  ns/op
i.h.serventis.jmh.TransactionOps.emit_signal                             avgt    5      8.793 ±    1.280  ns/op
i.h.serventis.jmh.TransactionOps.emit_signal_batch                       avgt    5      8.459 ±    0.773  ns/op
i.h.serventis.jmh.TransactionOps.emit_start_coordinator                  avgt    5      8.002 ±    1.062  ns/op
i.h.serventis.jmh.TransactionOps.emit_start_coordinator_batch            avgt    5      7.931 ±    0.706  ns/op
i.h.serventis.jmh.TransactionOps.emit_start_participant                  avgt    5      8.326 ±    1.105  ns/op
i.h.serventis.jmh.TransactionOps.emit_start_participant_batch            avgt    5      9.192 ±    1.241  ns/op
i.h.serventis.jmh.TransactionOps.transaction_from_conduit                avgt    5      1.852 ±    0.022  ns/op
i.h.serventis.jmh.TransactionOps.transaction_from_conduit_batch          avgt    5      1.660 ±    0.002  ns/op
i.h.substrates.jmh.CircuitOps.conduit_create_close                       avgt    5    285.932 ±  112.778  ns/op
i.h.substrates.jmh.CircuitOps.conduit_create_named                       avgt    5    298.431 ±  170.724  ns/op
i.h.substrates.jmh.CircuitOps.conduit_create_with_flow                   avgt    5    289.820 ±  175.950  ns/op
i.h.substrates.jmh.CircuitOps.create_and_close                           avgt    5    328.073 ±  475.403  ns/op
i.h.substrates.jmh.CircuitOps.create_await_close                         avgt    5  11067.216 ± 1282.993  ns/op
i.h.substrates.jmh.CircuitOps.hot_await_queue_drain                      avgt    5   5479.859 ±  317.373  ns/op
i.h.substrates.jmh.CircuitOps.hot_conduit_create                         avgt    5     19.950 ±    0.038  ns/op
i.h.substrates.jmh.CircuitOps.hot_conduit_create_named                   avgt    5     19.844 ±    0.275  ns/op
i.h.substrates.jmh.CircuitOps.hot_conduit_create_with_flow               avgt    5     22.837 ±    0.034  ns/op
i.h.substrates.jmh.CircuitOps.hot_pipe_async                             avgt    5      1.479 ±    0.128  ns/op
i.h.substrates.jmh.CircuitOps.hot_pipe_async_with_flow                   avgt    5      3.820 ±    0.600  ns/op
i.h.substrates.jmh.CircuitOps.pipe_async                                 avgt    5    488.380 ±  987.368  ns/op
i.h.substrates.jmh.CircuitOps.pipe_async_with_flow                       avgt    5    394.530 ±  634.665  ns/op
i.h.substrates.jmh.ConduitOps.get_by_name                                avgt    5      1.856 ±    0.047  ns/op
i.h.substrates.jmh.ConduitOps.get_by_name_batch                          avgt    5      1.658 ±    0.004  ns/op
i.h.substrates.jmh.ConduitOps.get_by_substrate                           avgt    5      1.997 ±    0.058  ns/op
i.h.substrates.jmh.ConduitOps.get_by_substrate_batch                     avgt    5      1.815 ±    0.003  ns/op
i.h.substrates.jmh.ConduitOps.get_cached                                 avgt    5      3.443 ±    0.083  ns/op
i.h.substrates.jmh.ConduitOps.get_cached_batch                           avgt    5      3.313 ±    0.008  ns/op
i.h.substrates.jmh.ConduitOps.subscribe                                  avgt    5    463.744 ±  446.688  ns/op
i.h.substrates.jmh.ConduitOps.subscribe_batch                            avgt    5    443.540 ±  419.552  ns/op
i.h.substrates.jmh.ConduitOps.subscribe_with_emission_await              avgt    5   5685.859 ±  184.250  ns/op
i.h.substrates.jmh.CortexOps.circuit                                     avgt    5    278.517 ±   75.424  ns/op
i.h.substrates.jmh.CortexOps.circuit_batch                               avgt    5    271.316 ±   85.014  ns/op
i.h.substrates.jmh.CortexOps.circuit_named                               avgt    5    278.589 ±   37.607  ns/op
i.h.substrates.jmh.CortexOps.current                                     avgt    5      1.072 ±    0.026  ns/op
i.h.substrates.jmh.CortexOps.name_class                                  avgt    5      1.509 ±    0.001  ns/op
i.h.substrates.jmh.CortexOps.name_enum                                   avgt    5      2.825 ±    0.015  ns/op
i.h.substrates.jmh.CortexOps.name_iterable                               avgt    5     11.167 ±    0.130  ns/op
i.h.substrates.jmh.CortexOps.name_path                                   avgt    5      1.893 ±    0.010  ns/op
i.h.substrates.jmh.CortexOps.name_path_batch                             avgt    5      1.685 ±    0.002  ns/op
i.h.substrates.jmh.CortexOps.name_string                                 avgt    5      2.748 ±    0.356  ns/op
i.h.substrates.jmh.CortexOps.name_string_batch                           avgt    5      2.605 ±    0.003  ns/op
i.h.substrates.jmh.CortexOps.pipe_empty                                  avgt    5      0.443 ±    0.002  ns/op
i.h.substrates.jmh.CortexOps.pipe_empty_batch                            avgt    5     ≈ 10⁻³             ns/op
i.h.substrates.jmh.CortexOps.pipe_observer                               avgt    5      5.212 ±    2.823  ns/op
i.h.substrates.jmh.CortexOps.pipe_observer_batch                         avgt    5      1.296 ±    0.617  ns/op
i.h.substrates.jmh.CortexOps.pipe_transform                              avgt    5      4.091 ±    1.713  ns/op
i.h.substrates.jmh.CortexOps.scope                                       avgt    5      8.807 ±    0.522  ns/op
i.h.substrates.jmh.CortexOps.scope_batch                                 avgt    5      7.505 ±    0.108  ns/op
i.h.substrates.jmh.CortexOps.scope_named                                 avgt    5      7.957 ±    0.042  ns/op
i.h.substrates.jmh.CortexOps.slot_boolean                                avgt    5      2.294 ±    0.981  ns/op
i.h.substrates.jmh.CortexOps.slot_double                                 avgt    5      2.064 ±    0.752  ns/op
i.h.substrates.jmh.CortexOps.slot_int                                    avgt    5      2.229 ±    0.835  ns/op
i.h.substrates.jmh.CortexOps.slot_long                                   avgt    5      2.314 ±    0.568  ns/op
i.h.substrates.jmh.CortexOps.slot_string                                 avgt    5      2.297 ±    0.791  ns/op
i.h.substrates.jmh.CortexOps.state_empty                                 avgt    5      0.447 ±    0.065  ns/op
i.h.substrates.jmh.CortexOps.state_empty_batch                           avgt    5     ≈ 10⁻³             ns/op
i.h.substrates.jmh.FlowOps.baseline_no_flow_await                        avgt    5     18.848 ±    0.408  ns/op
i.h.substrates.jmh.FlowOps.flow_combined_diff_guard_await                avgt    5     21.361 ±    0.357  ns/op
i.h.substrates.jmh.FlowOps.flow_combined_diff_sample_await               avgt    5     21.248 ±    0.409  ns/op
i.h.substrates.jmh.FlowOps.flow_combined_guard_limit_await               avgt    5     19.234 ±    0.399  ns/op
i.h.substrates.jmh.FlowOps.flow_diff_await                               avgt    5     18.345 ±    0.926  ns/op
i.h.substrates.jmh.FlowOps.flow_guard_await                              avgt    5     16.851 ±    0.771  ns/op
i.h.substrates.jmh.FlowOps.flow_limit_await                              avgt    5     18.903 ±    0.721  ns/op
i.h.substrates.jmh.FlowOps.flow_sample_await                             avgt    5     16.793 ±    0.399  ns/op
i.h.substrates.jmh.FlowOps.flow_sift_await                               avgt    5     17.338 ±    0.835  ns/op
i.h.substrates.jmh.NameOps.name_chained_deep                             avgt    5     16.913 ±    0.095  ns/op
i.h.substrates.jmh.NameOps.name_chaining                                 avgt    5      9.074 ±    0.053  ns/op
i.h.substrates.jmh.NameOps.name_chaining_batch                           avgt    5      9.070 ±    0.095  ns/op
i.h.substrates.jmh.NameOps.name_compare                                  avgt    5     32.478 ±    0.682  ns/op
i.h.substrates.jmh.NameOps.name_compare_batch                            avgt    5     32.490 ±    1.937  ns/op
i.h.substrates.jmh.NameOps.name_depth                                    avgt    5      1.678 ±    0.001  ns/op
i.h.substrates.jmh.NameOps.name_depth_batch                              avgt    5      1.283 ±    0.149  ns/op
i.h.substrates.jmh.NameOps.name_enclosure                                avgt    5      0.589 ±    0.001  ns/op
i.h.substrates.jmh.NameOps.name_from_enum                                avgt    5      2.833 ±    0.014  ns/op
i.h.substrates.jmh.NameOps.name_from_iterable                            avgt    5     11.070 ±    0.004  ns/op
i.h.substrates.jmh.NameOps.name_from_iterator                            avgt    5     12.852 ±    0.016  ns/op
i.h.substrates.jmh.NameOps.name_from_mapped_iterable                     avgt    5     11.703 ±    0.090  ns/op
i.h.substrates.jmh.NameOps.name_from_name                                avgt    5      4.216 ±    0.005  ns/op
i.h.substrates.jmh.NameOps.name_from_string                              avgt    5      2.998 ±    0.493  ns/op
i.h.substrates.jmh.NameOps.name_from_string_batch                        avgt    5      2.804 ±    0.011  ns/op
i.h.substrates.jmh.NameOps.name_interning_chained                        avgt    5     12.450 ±    0.832  ns/op
i.h.substrates.jmh.NameOps.name_interning_same_path                      avgt    5      3.559 ±    0.001  ns/op
i.h.substrates.jmh.NameOps.name_interning_segments                       avgt    5     32.677 ±  135.923  ns/op
i.h.substrates.jmh.NameOps.name_iterate_hierarchy                        avgt    5     18.323 ±   14.367  ns/op
i.h.substrates.jmh.NameOps.name_parsing                                  avgt    5      1.905 ±    0.109  ns/op
i.h.substrates.jmh.NameOps.name_parsing_batch                            avgt    5      1.676 ±    0.008  ns/op
i.h.substrates.jmh.NameOps.name_path_generation                          avgt    5     32.917 ±    1.741  ns/op
i.h.substrates.jmh.NameOps.name_path_generation_batch                    avgt    5     27.449 ±    1.134  ns/op
i.h.substrates.jmh.PipeOps.emit_chain_depth_1                            avgt    5      0.051 ±    0.007  ns/op
i.h.substrates.jmh.PipeOps.emit_chain_depth_10                           avgt    5      8.671 ±    0.954  ns/op
i.h.substrates.jmh.PipeOps.emit_chain_depth_20                           avgt    5     24.286 ±    0.881  ns/op
i.h.substrates.jmh.PipeOps.emit_chain_depth_5                            avgt    5      4.070 ±    0.351  ns/op
i.h.substrates.jmh.PipeOps.emit_fanout_width_1                           avgt    5      0.050 ±    0.001  ns/op
i.h.substrates.jmh.PipeOps.emit_fanout_width_10                          avgt    5      0.270 ±    0.022  ns/op
i.h.substrates.jmh.PipeOps.emit_fanout_width_20                          avgt    5      5.969 ±    0.022  ns/op
i.h.substrates.jmh.PipeOps.emit_fanout_width_5                           avgt    5      0.188 ±    0.002  ns/op
i.h.substrates.jmh.PipeOps.emit_no_await                                 avgt    5      0.043 ±    0.002  ns/op
i.h.substrates.jmh.PipeOps.emit_to_async_pipe                            avgt    5      8.052 ±    1.149  ns/op
i.h.substrates.jmh.PipeOps.emit_to_chained_pipes                         avgt    5      1.636 ±    0.001  ns/op
i.h.substrates.jmh.PipeOps.emit_to_double_transform                      avgt    5      0.499 ±    0.002  ns/op
i.h.substrates.jmh.PipeOps.emit_to_empty_pipe                            avgt    5      0.436 ±    0.002  ns/op
i.h.substrates.jmh.PipeOps.emit_to_fanout                                avgt    5      0.899 ±    0.040  ns/op
i.h.substrates.jmh.PipeOps.emit_to_receptor_pipe                         avgt    5      0.632 ±    0.005  ns/op
i.h.substrates.jmh.PipeOps.emit_to_transform_pipe                        avgt    5      0.711 ±    0.055  ns/op
i.h.substrates.jmh.PipeOps.emit_with_await                               avgt    5   5803.194 ±  287.242  ns/op
i.h.substrates.jmh.PipeOps.emit_with_counter_await                       avgt    5   7899.478 ±  123.508  ns/op
i.h.substrates.jmh.ReservoirOps.baseline_emit_no_reservoir_await         avgt    5     95.416 ±    3.378  ns/op
i.h.substrates.jmh.ReservoirOps.baseline_emit_no_reservoir_await_batch   avgt    5     17.022 ±    0.897  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_burst_then_drain_await         avgt    5     88.677 ±   32.596  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_burst_then_drain_await_batch   avgt    5     28.440 ±    0.490  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_drain_await                    avgt    5     96.936 ±    1.813  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_drain_await_batch              avgt    5     28.254 ±    0.214  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_emit_drain_cycles_await        avgt    5    443.579 ±   14.369  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_emit_with_capture_await        avgt    5     80.194 ±   10.951  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_emit_with_capture_await_batch  avgt    5     23.298 ±    0.080  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_process_emissions_await        avgt    5     96.824 ±    1.923  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_process_emissions_await_batch  avgt    5     25.521 ±    0.293  ns/op
i.h.substrates.jmh.ReservoirOps.reservoir_process_subjects_await         avgt    5     99.436 ±   11.426  ns/op
i.h.substrates.jmh.ScopeOps.scope_child_anonymous                        avgt    5     18.348 ±    2.058  ns/op
i.h.substrates.jmh.ScopeOps.scope_child_anonymous_batch                  avgt    5     17.392 ±    0.277  ns/op
i.h.substrates.jmh.ScopeOps.scope_child_named                            avgt    5     17.007 ±    0.125  ns/op
i.h.substrates.jmh.ScopeOps.scope_child_named_batch                      avgt    5     16.961 ±    0.127  ns/op
i.h.substrates.jmh.ScopeOps.scope_close_idempotent                       avgt    5      2.402 ±    0.020  ns/op
i.h.substrates.jmh.ScopeOps.scope_close_idempotent_batch                 avgt    5      0.033 ±    0.001  ns/op
i.h.substrates.jmh.ScopeOps.scope_closure                                avgt    5    295.941 ±   33.244  ns/op
i.h.substrates.jmh.ScopeOps.scope_closure_batch                          avgt    5    302.919 ±   93.008  ns/op
i.h.substrates.jmh.ScopeOps.scope_complex                                avgt    5    951.796 ±  154.722  ns/op
i.h.substrates.jmh.ScopeOps.scope_create_and_close                       avgt    5      2.437 ±    0.037  ns/op
i.h.substrates.jmh.ScopeOps.scope_create_and_close_batch                 avgt    5      0.034 ±    0.001  ns/op
i.h.substrates.jmh.ScopeOps.scope_create_named                           avgt    5      2.432 ±    0.030  ns/op
i.h.substrates.jmh.ScopeOps.scope_create_named_batch                     avgt    5      0.033 ±    0.001  ns/op
i.h.substrates.jmh.ScopeOps.scope_hierarchy                              avgt    5     27.239 ±    0.080  ns/op
i.h.substrates.jmh.ScopeOps.scope_hierarchy_batch                        avgt    5     26.565 ±    0.113  ns/op
i.h.substrates.jmh.ScopeOps.scope_parent_closes_children                 avgt    5     42.035 ±    0.219  ns/op
i.h.substrates.jmh.ScopeOps.scope_parent_closes_children_batch           avgt    5     41.309 ±    1.604  ns/op
i.h.substrates.jmh.ScopeOps.scope_register_multiple                      avgt    5   1465.261 ±  466.682  ns/op
i.h.substrates.jmh.ScopeOps.scope_register_multiple_batch                avgt    5   1491.902 ±  125.028  ns/op
i.h.substrates.jmh.ScopeOps.scope_register_single                        avgt    5    303.762 ±   38.663  ns/op
i.h.substrates.jmh.ScopeOps.scope_register_single_batch                  avgt    5    298.504 ±   59.730  ns/op
i.h.substrates.jmh.ScopeOps.scope_with_resources                         avgt    5    563.617 ±   69.863  ns/op
i.h.substrates.jmh.StateOps.slot_name                                    avgt    5      0.530 ±    0.055  ns/op
i.h.substrates.jmh.StateOps.slot_name_batch                              avgt    5      0.001 ±    0.001  ns/op
i.h.substrates.jmh.StateOps.slot_type                                    avgt    5      0.443 ±    0.001  ns/op
i.h.substrates.jmh.StateOps.slot_value                                   avgt    5      0.662 ±    0.001  ns/op
i.h.substrates.jmh.StateOps.slot_value_batch                             avgt    5      0.001 ±    0.001  ns/op
i.h.substrates.jmh.StateOps.state_compact                                avgt    5     10.565 ±    0.500  ns/op
i.h.substrates.jmh.StateOps.state_compact_batch                          avgt    5     10.869 ±    0.423  ns/op
i.h.substrates.jmh.StateOps.state_iterate_slots                          avgt    5      2.156 ±    0.002  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_int                           avgt    5      4.483 ±    1.057  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_int_batch                     avgt    5      4.572 ±    1.665  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_long                          avgt    5      4.439 ±    1.758  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_object                        avgt    5      2.507 ±    0.391  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_object_batch                  avgt    5      2.335 ±    0.006  ns/op
i.h.substrates.jmh.StateOps.state_slot_add_string                        avgt    5      4.433 ±    0.578  ns/op
i.h.substrates.jmh.StateOps.state_value_read                             avgt    5      1.487 ±    0.001  ns/op
i.h.substrates.jmh.StateOps.state_value_read_batch                       avgt    5      1.267 ±    0.003  ns/op
i.h.substrates.jmh.StateOps.state_values_stream                          avgt    5      4.393 ±    1.558  ns/op
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