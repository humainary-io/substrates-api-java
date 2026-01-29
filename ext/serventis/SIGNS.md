# Serventis Sign Classification Analysis

## Purpose

This document classifies all signs across Serventis APIs to understand design patterns and determine
where new APIs fit architecturally.

## Classification Framework

| Category             | Definition                                                | Example Signs                          |
|----------------------|-----------------------------------------------------------|----------------------------------------|
| **State Transition** | Lifecycle state changes the implementer directly observes | START, STOP, SPAWN                     |
| **Outcome**          | Results/verdicts of operations                            | SUCCESS, FAIL, GRANT, DENY             |
| **Operation**        | Actions being performed                                   | ENQUEUE, DEQUEUE, CALL                 |
| **Condition**        | Detectable system states/violations                       | OVERFLOW, UNDERFLOW, CONFLICT, CORRUPT |
| **Error**            | Exceptional/abnormal termination                          | CRASH, TIMEOUT, EXPIRE, ABANDON        |
| **Flow Control**     | Coordination/scheduling actions                           | SUSPEND, RESUME, DELAY, SCHEDULE       |
| **Contention**       | Resource competition signals                              | CONTEST, FAIL (CAS failures)           |
| **Retry**            | Contention management behaviors                           | SPIN, YIELD, BACKOFF                   |
| **Escalation**       | Contention escalation behaviors                           | PARK, EXHAUST                          |
| **Progress**         | Ongoing work indicators                                   | PROGRESS (heartbeat)                   |
| **Modification**     | State transformations                                     | UPGRADE, DOWNGRADE                     |

---

## API-by-API Classification

### Resources API (6 signs, no dimensions)

| Sign    | Category  | Notes                       |
|---------|-----------|-----------------------------|
| ATTEMPT | Operation | Non-blocking request action |
| ACQUIRE | Operation | Blocking request action     |
| GRANT   | Outcome   | Request succeeded           |
| DENY    | Outcome   | Try-lock failed             |
| TIMEOUT | Error     | Wait exceeded limit         |
| RELEASE | Operation | Return units to pool        |

**Pattern**: Request operations (ATTEMPT/ACQUIRE) + outcomes (GRANT/DENY/TIMEOUT) + release
operation

### Services API (16 signs × 2 dimensions = 32 signals)

| Sign       | Category         | Notes                   |
|------------|------------------|-------------------------|
| START      | State Transition | Execution begins        |
| STOP       | State Transition | Execution ends          |
| CALL       | Operation        | Remote request          |
| SUCCESS    | Outcome          | Completed successfully  |
| FAIL       | Outcome          | Failed to complete      |
| RECOURSE   | Flow Control     | Degraded mode activated |
| REDIRECT   | Flow Control     | Forwarded elsewhere     |
| EXPIRE     | Error            | Exceeded time budget    |
| RETRY      | Operation        | Reattempt after failure |
| REJECT     | Outcome          | Refused at boundary     |
| DISCARD    | Operation        | Intentionally dropped   |
| DELAY      | Flow Control     | Postponed               |
| SCHEDULE   | Flow Control     | Queued for later        |
| SUSPEND    | Flow Control     | Paused                  |
| RESUME     | Flow Control     | Restarted               |
| DISCONNECT | Error            | Cannot reach service    |

**Pattern**: Rich lifecycle (START/STOP/CALL) + outcomes (SUCCESS/FAIL/REJECT) + extensive flow
control

### Actors API (11 signs, no dimensions)

| Sign        | Category     | Notes                                     |
|-------------|--------------|-------------------------------------------|
| ASK         | Operation    | Seeking information or clarification      |
| AFFIRM      | Assertion    | Making a claim or judgment                |
| EXPLAIN     | Assertion    | Providing reasoning or rationale          |
| REPORT      | Assertion    | Conveying factual observations            |
| REQUEST     | Coordination | Asking another actor to act (peer-level)  |
| COMMAND     | Coordination | Directing another actor (authority-level) |
| ACKNOWLEDGE | Coordination | Confirming receipt or understanding       |
| DENY        | Disagreement | Disagreeing or correcting proposition     |
| CLARIFY     | Disagreement | Refining or disambiguating intent         |
| PROMISE     | Commitment   | Committing to future action               |
| DELIVER     | Commitment   | Presenting completed work                 |

**Pattern**: Speech act theory - conversational coordination between actors (human or machine).
Questions (ASK) + assertions (AFFIRM/EXPLAIN/REPORT) + coordination (REQUEST/COMMAND/ACKNOWLEDGE)

+ disagreement (DENY/CLARIFY) + commitment (PROMISE/DELIVER).

**Key Characteristics**:

- **Speech Act Theory**: Grounded in Austin/Searle's linguistic philosophy
- **Human-AI Dialogue**: Enables observation of human-machine conversations
- **Commitment Arc**: REQUEST → ACKNOWLEDGE → PROMISE → DELIVER tracks work delivery
- **No Dimensions**: Speech acts are perspective-independent

**Use cases**: Human-AI collaboration monitoring, conversational agents, commitment tracking,
dialogue quality assessment, collaboration effectiveness

**Translation to Status**: Many DENY → CONFLICT, low ACKNOWLEDGE rate → DISCONNECTED,
PROMISE without DELIVER → UNRELIABLE

### Agents API (10 signs × 2 dimensions = 20 signals)

| Sign     | Category   | Notes                                 |
|----------|------------|---------------------------------------|
| OFFER    | Discovery  | Agent advertising capability          |
| INQUIRE  | Discovery  | Agent asking about capabilities       |
| PROMISE  | Commitment | Agent committing to behavior          |
| ACCEPT   | Commitment | Agent accepting another's promise     |
| DEPEND   | Dependency | Agent declaring dependency on promise |
| OBSERVE  | Dependency | Agent monitoring promise state        |
| VALIDATE | Dependency | Agent confirming promise validity     |
| FULFILL  | Resolution | Agent kept its promise                |
| BREACH   | Resolution | Agent failed to keep promise          |
| RETRACT  | Resolution | Agent withdrawing promise             |

**Dimensions** (promise perspective):

| Dimension | Perspective | Notes                                    |
|-----------|-------------|------------------------------------------|
| PROMISER  | Self        | I am making this promise/action          |
| PROMISEE  | Other       | I observed them make that promise/action |

**Pattern**: Promise theory (Mark Burgess) - autonomous agent coordination through voluntary
promises. Discovery (OFFER/INQUIRE) + commitment (PROMISE/ACCEPT) + dependency (DEPEND/OBSERVE/
VALIDATE) + resolution (FULFILL/BREACH/RETRACT).

**Key Characteristics**:

- **Promise Theory**: Grounded in Burgess's work on autonomous systems
- **Voluntary Cooperation**: Agents only promise what they control
- **Dual Perspective**: PROMISER (acting) vs PROMISEE (observing) perspectives
- **Different from Actors**: Agents use voluntary promises, Actors use speech acts

**Use cases**: Distributed system coordination, autonomous agents, promise networks, dependency
tracking, trust measurement

**Translation to Status**: Many BREACH → DEGRADED, high FULFILL rate → RELIABLE,
RETRACT signals → UNSTABLE

### Processes API (9 signs, no dimensions)

| Sign    | Category         | Notes                        |
|---------|------------------|------------------------------|
| SPAWN   | State Transition | Process created              |
| START   | State Transition | Execution begins             |
| STOP    | Outcome          | Clean exit (exit 0)          |
| FAIL    | Outcome          | Error exit (non-zero)        |
| CRASH   | Error            | Abnormal termination         |
| KILL    | Operation        | Forced termination           |
| RESTART | Operation        | Restarting after termination |
| SUSPEND | Flow Control     | Process paused               |
| RESUME  | Flow Control     | Process resumed              |

**Pattern**: Creation (SPAWN) + execution (START) + termination outcomes (STOP/FAIL/CRASH/KILL) +
supervision (RESTART)

### Tasks API (11 signs, no dimensions)

| Sign     | Category         | Notes                  |
|----------|------------------|------------------------|
| SUBMIT   | Operation        | Task submitted         |
| REJECT   | Outcome          | Submission refused     |
| SCHEDULE | Flow Control     | Dequeued, ready to run |
| START    | State Transition | Execution begins       |
| PROGRESS | Progress         | Heartbeat/indicator    |
| SUSPEND  | Flow Control     | Task paused            |
| RESUME   | Flow Control     | Task resumed           |
| COMPLETE | Outcome          | Successful completion  |
| FAIL     | Outcome          | Failed with error      |
| CANCEL   | Operation        | Cancelled explicitly   |
| TIMEOUT  | Error            | Exceeded time budget   |

**Pattern**: Submission (SUBMIT/REJECT) + scheduling (SCHEDULE) + execution (START) + outcomes (
COMPLETE/FAIL/CANCEL/TIMEOUT)

### Timers API (2 signs × 2 dimensions = 4 signals)

| Sign | Category | Notes                     |
|------|----------|---------------------------|
| MEET | Outcome  | Time constraint satisfied |
| MISS | Outcome  | Time constraint violated  |

**Dimensions** (constraint types):

| Dimension | Constraint | Notes                                    |
|-----------|------------|------------------------------------------|
| DEADLINE  | Absolute   | Specific point in time ("complete by X") |
| THRESHOLD | Relative   | Duration/performance target ("within X") |

**Signal Matrix**:

|      | DEADLINE                  | THRESHOLD                   |
|------|---------------------------|-----------------------------|
| MEET | Completed before deadline | Completed within target     |
| MISS | Completed after deadline  | Exceeded performance target |

**Pattern**: Binary outcome (MEET/MISS) × constraint type (DEADLINE/THRESHOLD). Inspired by
Apdex, provides qualitative vocabulary for time constraint outcomes without encoding quantitative
durations. Implementer determines the constraint; subscribers interpret patterns.

**Key Characteristics**:

- **Implementer-Observable**: Developer knows if constraint was met (value comparison)
- **Qualitative Vocabulary**: No durations or timestamps in signs
- **Binary Outcome**: Simple MEET/MISS, pattern recognition deferred to subscribers
- **Dimension as Constraint Type**: DEADLINE (absolute) vs THRESHOLD (relative)

**Use cases**: SLA compliance, deadline tracking, Apdex-style performance classification,
batch processing deadlines, request timeout analysis

**Translation to Status**: High MISS rate on THRESHOLD → DEGRADED, high MISS rate on DEADLINE →
OVERLOADED, sustained MEET → OPTIMAL

**Relationship to TIMEOUT/EXPIRE**: TIMEOUT and EXPIRE indicate failure to complete; MEET/MISS
classify completions. Timers answers "did it meet the constraint?" not "did it complete?"

### Locks API (10 signs, no dimensions)

| Sign      | Category     | Notes                                 |
|-----------|--------------|---------------------------------------|
| ATTEMPT   | Operation    | Try-lock (from Resources)             |
| ACQUIRE   | Operation    | Blocking acquire (from Resources)     |
| GRANT     | Outcome      | Lock obtained (from Resources)        |
| DENY      | Outcome      | Try-lock failed (from Resources)      |
| TIMEOUT   | Error        | Timed acquire failed (from Resources) |
| RELEASE   | Operation    | Lock released (from Resources)        |
| UPGRADE   | Modification | Read → write lock                     |
| DOWNGRADE | Modification | Write → read lock                     |
| CONTEST   | Contention   | CAS failure / contention detected     |
| ABANDON   | Error        | Holder crashed without release        |

**Pattern**: Reuses Resources vocabulary (ATTEMPT/ACQUIRE/GRANT/DENY/TIMEOUT/RELEASE) +
lock-specific extensions (UPGRADE/DOWNGRADE/CONTEST/ABANDON)

### Atomics API (8 signs, no dimensions)

| Sign    | Category   | Notes                                |
|---------|------------|--------------------------------------|
| ATTEMPT | Operation  | CAS operation initiated              |
| SUCCESS | Outcome    | CAS succeeded                        |
| FAIL    | Contention | CAS failed (contention detected)     |
| SPIN    | Retry      | Busy-wait retry loop                 |
| YIELD   | Retry      | Thread.yield() scheduling hint       |
| BACKOFF | Retry      | Deliberate delay applied             |
| PARK    | Escalation | Transition from spinning to blocking |
| EXHAUST | Escalation | Retry budget exceeded, giving up     |

**Pattern**: Operation initiation (ATTEMPT) + outcomes (SUCCESS/FAIL) + retry strategies
(SPIN/YIELD/BACKOFF) + escalation behaviors (PARK/EXHAUST). Focuses on contention dynamics
rather than ownership semantics. Complements Locks API: Atomics tracks *how* contention is
managed, Locks tracks *what* ownership state results.

**Key Distinction from Locks**: Locks.CONTEST indicates CAS failure in context of lock
acquisition. Atomics provides finer-grained observability of the retry/backoff behaviors
that follow contention detection. Use together: Atomics for contention dynamics, Locks for
ownership lifecycle.

**Use cases**: Lock-free data structures (Treiber stack, Michael-Scott queue), spin locks,
adaptive spinning, hybrid locks (spin-then-park), CAS-based counters, atomic reference updates

**Translation to Status**: High FAIL/ATTEMPT ratio → SATURATED, frequent PARK → DEGRADED,
EXHAUST signals → OVERLOADED, low FAIL rate → OPERATIONAL

### Transactions API (8 signs × 2 dimensions = 16 signals)

| Sign       | Category         | Notes                   |
|------------|------------------|-------------------------|
| START      | State Transition | Transaction initiated   |
| PREPARE    | Operation        | Voting phase (2PC)      |
| COMMIT     | Outcome          | Transaction committed   |
| ROLLBACK   | Outcome          | Transaction aborted     |
| ABORT      | Error            | Forced termination      |
| EXPIRE     | Error            | Exceeded time budget    |
| CONFLICT   | Condition        | Write conflict detected |
| COMPENSATE | Operation        | Saga rollback           |

**Pattern**: Lifecycle (START/PREPARE) + resolution (COMMIT/ROLLBACK) + error conditions (
ABORT/EXPIRE/CONFLICT) + saga support (COMPENSATE)

### Queues API (4 signs, no dimensions)

| Sign      | Category  | Notes              |
|-----------|-----------|--------------------|
| ENQUEUE   | Operation | Item added         |
| DEQUEUE   | Operation | Item removed       |
| OVERFLOW  | Condition | Capacity violated  |
| UNDERFLOW | Condition | Emptiness violated |

**Pattern**: Symmetric operations (ENQUEUE/DEQUEUE) + symmetric boundary violations (
OVERFLOW/UNDERFLOW)

### Caches API (7 signs, no dimensions)

| Sign   | Category  | Notes                                |
|--------|-----------|--------------------------------------|
| LOOKUP | Operation | Attempt to retrieve entry from cache |
| HIT    | Outcome   | Lookup succeeded - entry found       |
| MISS   | Outcome   | Lookup failed - entry not found      |
| STORE  | Operation | Entry added or updated in cache      |
| EVICT  | Condition | Entry removed due to capacity/policy |
| EXPIRE | Error     | Entry removed due to TTL expiration  |
| REMOVE | Operation | Entry explicitly invalidated/removed |

**Pattern**: Lookup operation (LOOKUP) + outcomes (HIT/MISS) + storage (STORE) + removal
conditions (EVICT/EXPIRE/REMOVE). Cache lifecycle from access through removal.

**Key Characteristics**:

- **Implementer-Observable**: Cache directly knows lookup result, storage operations
- **Outcome-Focused**: HIT/MISS are outcomes of LOOKUP
- **Removal Distinctions**: EVICT (capacity), EXPIRE (time), REMOVE (explicit)

**Use cases**: Session caches, content caches, query caches, object pools with TTL

**Translation to Status**: High MISS rate → COLD/INEFFECTIVE, many EVICT → SATURATED,
many EXPIRE → STALE

### Pipelines API (12 signs, no dimensions)

| Sign         | Category     | Notes                                   |
|--------------|--------------|-----------------------------------------|
| INPUT        | Operation    | Data received from upstream stage       |
| OUTPUT       | Operation    | Data sent to downstream stage           |
| TRANSFORM    | Operation    | Data transformed (map, flatMap, enrich) |
| FILTER       | Operation    | Data filtered out (removed from flow)   |
| AGGREGATE    | Operation    | Data aggregated (window, group, reduce) |
| BUFFER       | Flow Control | Data buffered for flow control          |
| BACKPRESSURE | Flow Control | Upstream signaled to slow production    |
| OVERFLOW     | Condition    | Buffer capacity exceeded                |
| CHECKPOINT   | Progress     | Processing checkpoint reached           |
| WATERMARK    | Progress     | Event-time watermark advanced           |
| LAG          | Condition    | Processing lag detected                 |
| SKIP         | Error        | Data skipped/dropped anomalously        |

**Pattern**: Data flow (INPUT/OUTPUT) + transformations (TRANSFORM/FILTER/AGGREGATE) +
flow control (BUFFER/BACKPRESSURE/OVERFLOW) + progress tracking (CHECKPOINT/WATERMARK) +
anomalies (LAG/SKIP)

**Key Characteristics**:

- **Stage-Oriented**: Signs describe what happens at pipeline stages
- **Rich Flow Control**: Explicit buffering and backpressure vocabulary
- **Progress Tracking**: Both processing (CHECKPOINT) and event-time (WATERMARK) progress

**Use cases**: ETL/ELT pipelines, stream processing (Kafka Streams, Flink), batch processing
(Spark), ML pipelines, data quality pipelines

**Translation to Status**: High LAG → DEGRADED, OVERFLOW → SATURATED, many SKIP → DEFECTIVE

### Logs API (4 signs, no dimensions)

| Sign    | Category          | Notes                                |
|---------|-------------------|--------------------------------------|
| SEVERE  | Condition/Error   | Serious failures requiring attention |
| WARNING | Condition         | Potential problems/concerns          |
| INFO    | State Observation | Normal operational information       |
| DEBUG   | State Observation | Diagnostic/tracing output            |

**Pattern**: Semantic health signals representing log level severity. Client perspective (
application code) directly observes and reports conditions through logging. Enables pattern
recognition for error rates, warning crescendos, and log volume analysis.

**Mapping from java.util.logging**: SEVERE (direct), WARNING (direct), INFO (direct), CONFIG→INFO,
FINE/FINER/FINEST→DEBUG

**Translation to Status**: Many SEVERE → CRITICAL/DEGRADED, many WARNING → WARNING, high DEBUG →
VERBOSE

### Counters API (3 signs, no dimensions)

| Sign      | Category  | Notes                                |
|-----------|-----------|--------------------------------------|
| INCREMENT | Operation | Counter increased                    |
| OVERFLOW  | Condition | Counter exceeded maximum and wrapped |
| RESET     | Operation | Counter explicitly reset to zero     |

**Pattern**: Monotonic accumulation (INCREMENT) + boundary condition (OVERFLOW) + intentional
reset (RESET). Counters enforce monotonic increase - they never decrease legitimately.

**Key Characteristics**:

- **Monotonic**: Only INCREMENT, never decrement
- **Boundary Handling**: OVERFLOW indicates numeric domain exceeded
- **Subset of Gauges**: Counters are gauges without DECREMENT

**Use cases**: Request counts, bytes processed, events handled, cumulative metrics

**Translation to Status**: Many OVERFLOW → SATURATED/ERRATIC, high increment rate → ACTIVE

### Gauges API (5 signs, no dimensions)

| Sign      | Category  | Notes                              |
|-----------|-----------|------------------------------------|
| INCREMENT | Operation | Gauge increased                    |
| DECREMENT | Operation | Gauge decreased                    |
| OVERFLOW  | Condition | Gauge exceeded maximum value       |
| UNDERFLOW | Condition | Gauge fell below minimum value     |
| RESET     | Operation | Gauge explicitly reset to baseline |

**Pattern**: Bidirectional operations (INCREMENT/DECREMENT) + symmetric boundary violations
(OVERFLOW/UNDERFLOW) + intentional reset (RESET). Gauges track fluctuating values.

**Key Characteristics**:

- **Bidirectional**: Both INCREMENT and DECREMENT allowed
- **Symmetric Boundaries**: OVERFLOW (max) and UNDERFLOW (min) violations
- **Superset of Counters**: Gauges add DECREMENT and UNDERFLOW

**Use cases**: Active connections, queue depth, in-flight requests, resource utilization

**Translation to Status**: OVERFLOW → SATURATED, UNDERFLOW → STARVED, high RESET rate → UNSTABLE

### Probes API (6 signs × 2 dimensions = 12 signals)

| Sign       | Category  | Notes                            |
|------------|-----------|----------------------------------|
| CONNECT    | Operation | Connection establishment         |
| DISCONNECT | Operation | Connection closure               |
| TRANSFER   | Operation | Data transfer (send or receive)  |
| PROCESS    | Operation | Data processing                  |
| SUCCEED    | Outcome   | Operation completed successfully |
| FAIL       | Outcome   | Operation failed                 |

**Dimensions** (communication direction):

| Dimension | Direction | Notes                           |
|-----------|-----------|---------------------------------|
| OUTBOUND  | Sending   | I am initiating/sending outward |
| INBOUND   | Receiving | I received/coming inward        |

**Pattern**: Communication operations (CONNECT/DISCONNECT/TRANSFER/PROCESS) + outcomes
(SUCCEED/FAIL) from both directions (OUTBOUND/INBOUND). Network communication observability.

**Key Characteristics**:

- **Directional**: Every operation has OUTBOUND and INBOUND perspectives
- **Operation + Outcome**: Both the action and its result
- **Connection Lifecycle**: CONNECT → TRANSFER/PROCESS → DISCONNECT

**Use cases**: HTTP clients, RPC frameworks, message brokers, network protocols

**Translation to Status**: Many FAIL → DEGRADED, DISCONNECT without CONNECT → DEFECTIVE

### Valves API (6 signs, no dimensions)

| Sign     | Category           | Notes                         |
|----------|--------------------|-------------------------------|
| PASS     | Flow Decision      | Request allowed through valve |
| DENY     | Flow Decision      | Request blocked by valve      |
| EXPAND   | Control Action     | Capacity increased (adaptive) |
| CONTRACT | Control Action     | Capacity decreased (adaptive) |
| DROP     | Emergency Response | Load shedding (overload)      |
| DRAIN    | Recovery Response  | Clearing backlog (recovery)   |

**Pattern**: Adaptive flow control - captures both flow decisions (PASS/DENY) and capacity
adaptations (EXPAND/CONTRACT). Instrument perspective: what the valve directly observes and does.
Enables pattern recognition for throttling intensity, adaptation effectiveness, saturation, and
control stability.

**Use cases**: Rate limiters, auto-scalers, semaphores, bulkheads, token buckets, circuit breakers
with dynamic thresholds

**Translation to Status**: High DENY → SATURATED, EXPAND activity → SCALING, CONTRACT + DENY →
CONSTRAINING, DROP → OVERLOAD, DRAIN → RECOVERING

### Routers API (9 signs, no dimensions)

| Sign       | Category  | Notes                                   |
|------------|-----------|-----------------------------------------|
| SEND       | Operation | Packet transmitted (originated by node) |
| RECEIVE    | Operation | Packet received from network            |
| FORWARD    | Operation | Packet forwarded to next hop            |
| ROUTE      | Operation | Routing decision made for packet        |
| DROP       | Operation | Packet discarded (congestion, policy)   |
| FRAGMENT   | Operation | Packet fragmented due to MTU            |
| REASSEMBLE | Operation | Packet fragments reassembled            |
| CORRUPT    | Condition | Packet corruption detected              |
| REORDER    | Condition | Out-of-order packet arrival detected    |

**Pattern**: Packet lifecycle - entry (RECEIVE) + routing (ROUTE) + exit (FORWARD/SEND/DROP) +
handling (FRAGMENT/REASSEMBLE) + anomalies (CORRUPT/REORDER). Network packet handling observability.

**Key Characteristics**:

- **Packet-Oriented**: Signs describe packet-level operations
- **SEND vs FORWARD**: SEND originates packets, FORWARD routes through
- **Anomaly Detection**: CORRUPT and REORDER as distinct conditions

**Use cases**: Software routers, network equipment, packet processing pipelines, traffic analysis

**Translation to Status**: High DROP → DEGRADED, many CORRUPT → DEFECTIVE, high REORDER → UNSTABLE

### Breakers API (6 signs, no dimensions)

| Sign      | Category         | Notes                                |
|-----------|------------------|--------------------------------------|
| CLOSE     | State Transition | Circuit closed - traffic flowing     |
| OPEN      | State Transition | Circuit opened - traffic blocked     |
| HALF_OPEN | State Transition | Circuit testing recovery             |
| TRIP      | Operation        | Failure threshold exceeded           |
| PROBE     | Operation        | Test request sent in half-open state |
| RESET     | Operation        | Circuit manually reset to closed     |

**Pattern**: Circuit breaker state machine - states (CLOSE/OPEN/HALF_OPEN) + events
(TRIP/PROBE/RESET). Resilience pattern observability for preventing cascading failures.

**Key Characteristics**:

- **State Machine**: Clear state transitions (CLOSE → OPEN → HALF_OPEN → CLOSE)
- **Event vs State**: TRIP/PROBE/RESET are events, CLOSE/OPEN/HALF_OPEN are states
- **Protection Pattern**: Fail fast when backend unhealthy

**Use cases**: Service protection, cascading failure prevention, resilience patterns
(Resilience4j, Hystrix)

**Translation to Status**: OPEN state → DEGRADED/DOWN, many TRIP → DEFECTIVE, sustained CLOSE →
HEALTHY

### Flows API (2 signs × 3 dimensions = 6 signals)

| Sign    | Category | Notes                                        |
|---------|----------|----------------------------------------------|
| SUCCESS | Outcome  | Item successfully transitioned through stage |
| FAIL    | Outcome  | Item failed to transition through stage      |

**Dimensions** (stage perspective):

| Dimension | Stage      | Notes                          |
|-----------|------------|--------------------------------|
| INGRESS   | Entry      | Item entering the system       |
| TRANSIT   | Processing | Item moving through the system |
| EGRESS    | Exit       | Item leaving the system        |

**Signal Matrix**:

|         | INGRESS | TRANSIT | EGRESS |
|---------|---------|---------|--------|
| SUCCESS | entered | moved   | exited |
| FAIL    | refused | dropped | failed |

**Pattern**: Stage transition outcomes - captures success/failure at each stage of data movement
through a system. Flow perspective: what the staged system directly observes about item transitions.
Enables pattern recognition for throughput analysis, bottleneck detection, and failure localization.

**Key Characteristics**:

- **Implementer-Observable**: System knows "item entered" (SUCCESS × INGRESS), "item dropped" (
  FAIL × TRANSIT)
- **Stage-Focused**: Dimensions represent stages of flow (entry, processing, exit), not locations
- **Outcome-Balanced**: Each stage has both success and failure outcomes
- **Minimal Vocabulary**: Just 2 signs × 3 dimensions = 6 total signals

**Use cases**: Message queue processing, data pipeline stages, request handling, stream processing,
workflow transitions, ETL monitoring

**Translation to Status**:

- High SUCCESS across all stages → HEALTHY
- High FAIL at INGRESS → SATURATED (refusing work)
- High FAIL at TRANSIT → DEGRADED (processing failures)
- High FAIL at EGRESS → BLOCKED (output failures)

**Relationship to Other APIs**:

- **Pipelines API**: Pipelines models operations (TRANSFORM, FILTER); Flows models stage outcomes
- **Queues API**: Queue ENQUEUE/DEQUEUE may trigger Flow INGRESS/EGRESS signals
- **Valves API**: Valve DENY at entry may correlate with Flow FAIL × INGRESS

### Pools API (4 signs, no dimensions)

| Sign     | Category  | Notes                                        |
|----------|-----------|----------------------------------------------|
| EXPAND   | Operation | Pool capacity increased (resource created)   |
| CONTRACT | Operation | Pool capacity decreased (resource destroyed) |
| BORROW   | Operation | Resource borrowed from pool (utilization ↑)  |
| RECLAIM  | Operation | Resource reclaimed by pool (utilization ↓)   |

**Pattern**: Capacity management (EXPAND/CONTRACT) + utilization tracking (BORROW/RECLAIM). Pool
perspective: what the pool directly observes about capacity and utilization. Enables pattern
recognition for saturation, resource leaks, capacity oscillation, and efficiency analysis.

**Use cases**: JDBC connection pools, thread pools, object pools, memory pools, buffer pools

**Translation to Status**: High BORROW without RECLAIM → LEAKING, rapid EXPAND/CONTRACT → UNSTABLE,
high BORROW rate → SATURATED

### Leases API (9 signs × 2 dimensions = 18 signals)

| Sign    | Category  | Notes                                      |
|---------|-----------|--------------------------------------------|
| ACQUIRE | Operation | Client attempting to obtain new lease      |
| GRANT   | Outcome   | Lease successfully granted                 |
| DENY    | Outcome   | Lease request denied                       |
| RENEW   | Operation | Holder attempting to extend lease duration |
| EXTEND  | Outcome   | Lease duration successfully extended       |
| RELEASE | Operation | Holder voluntarily terminated lease        |
| EXPIRE  | Error     | Lease automatically terminated (TTL)       |
| REVOKE  | Operation | Lease forcefully revoked by authority      |
| PROBE   | Operation | Status check on lease validity             |

**Dimensions** (lease perspective):

| Dimension | Perspective | Notes                                    |
|-----------|-------------|------------------------------------------|
| LESSOR    | Authority   | Grants, denies, extends, revokes leases  |
| LESSEE    | Client      | Requests, holds, renews, releases leases |

**Pattern**: Acquisition (ACQUIRE → GRANT/DENY) + renewal (RENEW → EXTEND) + termination
(RELEASE/EXPIRE/REVOKE) + monitoring (PROBE). Time-bounded resource ownership with
automatic expiration.

**Key Characteristics**:

- **Dual Perspective**: LESSOR (authority) and LESSEE (client) perspectives
- **Request/Response Pairs**: ACQUIRE → GRANT/DENY, RENEW → EXTEND
- **TTL-Based**: Automatic EXPIRE when time-to-live exhausted

**Use cases**: Leader election, distributed locking, resource reservation, split-brain
prevention, coordination services (etcd, Zookeeper)

**Translation to Status**: Many EXPIRE without RENEW → UNRELIABLE, high DENY → SATURATED,
REVOKE signals → DEFECTIVE

### Stacks API (4 signs, no dimensions)

| Sign      | Category  | Notes                         |
|-----------|-----------|-------------------------------|
| PUSH      | Operation | Item added to stack (top)     |
| POP       | Operation | Item removed from stack (top) |
| OVERFLOW  | Condition | Push failed due to capacity   |
| UNDERFLOW | Condition | Pop failed due to emptiness   |

**Pattern**: Symmetric operations (PUSH/POP) + symmetric boundary violations (OVERFLOW/UNDERFLOW).
Mirrors Queues API structure but represents LIFO semantics instead of FIFO. Stack perspective: what
the stack directly observes about depth changes and boundary conditions.

**Use cases**: Call stack monitoring, undo/redo buffers, parser state stacks, backtracking
algorithms, work-stealing deques (stack mode), expression evaluation

**Translation to Status**: High PUSH without POP → DEEP/DIVERGING, many OVERFLOW → SATURATED, many
UNDERFLOW → STARVED, rapid PUSH/POP → CHURNING

### Latches API (6 signs, no dimensions)

| Sign    | Category  | Notes                                    |
|---------|-----------|------------------------------------------|
| AWAIT   | Operation | Thread waiting at barrier (blocking)     |
| ARRIVE  | Operation | Thread reached barrier / count decrement |
| RELEASE | Outcome   | Barrier satisfied, threads unblocked     |
| TIMEOUT | Error     | Wait exceeded time limit                 |
| RESET   | Operation | Barrier reset for next phase             |
| ABANDON | Error     | Participant died without arriving        |

**Pattern**: Coordination barrier - captures thread arrivals (AWAIT/ARRIVE) and barrier resolution (
RELEASE). Latch perspective: what the coordination primitive directly observes about participant
arrivals and barrier satisfaction. Enables pattern recognition for phase transitions, coordination
bottlenecks, and synchronization failures.

**Use cases**: CountDownLatch, CyclicBarrier, Phaser, start signals, completion aggregation,
multi-phase coordination

**Translation to Status**: Many TIMEOUT → DEGRADED, ABANDON signals → DEFECTIVE, long barrier
durations → WARNING, frequent RESET with short phases → UNSTABLE

### Exchanges API (2 signs × 2 dimensions = 4 signals)

| Sign     | Category  | Notes                             |
|----------|-----------|-----------------------------------|
| CONTRACT | Operation | Commit to participate in exchange |
| TRANSFER | Operation | Resource changes hands            |

**Dimensions** (party perspective):

| Dimension | Perspective | Notes                            |
|-----------|-------------|----------------------------------|
| PROVIDER  | Giving      | Party transferring resources out |
| RECEIVER  | Taking      | Party receiving resources in     |

**Signal Matrix**:

| Sign     | PROVIDER              | RECEIVER              |
|----------|-----------------------|-----------------------|
| CONTRACT | I contract to provide | I contract to receive |
| TRANSFER | I transfer out        | I receive transfer    |

**Pattern**: Bilateral exchange - captures dual-party resource transfers with symmetric
perspectives.
Based on REA (Resource-Event-Agent) accounting model where economic activity consists of dual
exchanges (every give implies a take). Also supports Java Exchanger synchronization rendezvous
pattern.

**Key Characteristics**:

- **Dual Perspective**: Both PROVIDER and RECEIVER perspectives captured
- **Minimal Vocabulary**: Just CONTRACT (commitment) and TRANSFER (execution)
- **REA Foundation**: Grounded in economic exchange theory
- **Exchanger Support**: Models thread rendezvous synchronization

**Use cases**: Trade/order matching, resource swaps between services, thread rendezvous (Exchanger),
value transfer between agents, bilateral contract fulfillment

**Translation to Status**: Many CONTRACT without TRANSFER → PENDING/BLOCKED, high TRANSFER rate →
ACTIVE, asymmetric CONTRACT (one side only) → WAITING

### Sensors API (3 signs × 3 dimensions = 9 signals)

| Sign    | Category          | Notes                           |
|---------|-------------------|---------------------------------|
| BELOW   | State Observation | Measured value under setpoint   |
| NOMINAL | State Observation | Measured value at/near setpoint |
| ABOVE   | State Observation | Measured value over setpoint    |

**Dimensions** (setpoint types):

| Dimension | Type      | Notes                           |
|-----------|-----------|---------------------------------|
| BASELINE  | Reference | Normal/expected operating level |
| THRESHOLD | Reference | Limit/boundary not to exceed    |
| TARGET    | Reference | Ideal/optimal operating point   |

**Pattern**: Positional measurement signs (BELOW/NOMINAL/ABOVE) × setpoint type dimensions (
BASELINE/THRESHOLD/TARGET). Value-neutral observations - position reporting without judgment. The
implementer directly observes metric positions relative to configured reference values.

**Key Characteristics**:

- **Implementer-Observable**: Metrics collector compares observed values to configured setpoints
- **No Calculation Required**: Position is directly determined by comparison (value < threshold,
  value > baseline)
- **Multiple Simultaneous Signals**: Systems emit multiple setpoint signals (e.g., ABOVE × BASELINE
    + BELOW × THRESHOLD)
- **Foundation for Assessment**: Setpoint positions feed into Statuses API for condition assessment

**Use cases**: SLO threshold monitoring, queue depth baselines, latency targets, error rate
boundaries, resource utilization thresholds, capacity planning setpoints

**Translation to Status**: Sustained ABOVE × THRESHOLD → SATURATED/CRITICAL, ABOVE × BASELINE +
BELOW × THRESHOLD → ELEVATED/WARNING, BELOW × TARGET → OPTIMAL, oscillating around BASELINE →
NORMAL

**Relationship to Statuses**: Sensors provide raw positional measurements, Statuses translate
position patterns into operational assessments. Example: Many ABOVE × THRESHOLD signals → Status
emits DEGRADED condition.

---

### Systems API (4 signs × 4 dimensions = 16 signals)

| Sign   | Category        | Notes                                |
|--------|-----------------|--------------------------------------|
| NORMAL | Process Control | Operating within standard parameters |
| LIMIT  | Process Control | At constraint boundary               |
| ALARM  | Process Control | Beyond boundary, attention required  |
| FAULT  | Process Control | Constraint failed                    |

**Dimensions** (constraint types):

| Dimension | Constraint | Notes                             |
|-----------|------------|-----------------------------------|
| SPACE     | Container  | Capacity, volume, room            |
| FLOW      | Movement   | Throughput, bandwidth, rate       |
| LINK      | Structural | Connectivity, reachability, edges |
| TIME      | Temporal   | Latency, responsiveness, duration |

**Pattern**: Process control signs (NORMAL/LIMIT/ALARM/FAULT) × fundamental constraint dimensions (
SPACE/FLOW/LINK/TIME). Universal vocabulary that works across all system types regardless of whether
the underlying metric polarity is "high is good" or "low is good". Dimensions represent the four
fundamental constraints of any computational system.

**Key Characteristics**:

- **Process Control Vocabulary**: Signs map directly to industrial control terminology (
  green/yellow/red/fault)
- **Polarity-Agnostic**: Signs describe constraint health, not raw values (avoids "low time"
  ambiguity)
- **Universal Constraints**: SPACE, FLOW, LINK, TIME apply to any system component
- **Translation Layer**: Bridges domain-specific signs to behavioral assessment (Statuses)

**Use cases**: Constraint-focused observability, process control integration (SCADA/PLC),
cross-domain aggregation, resource exhaustion detection, connectivity monitoring

**Translation to Status**:

- ALARM × SPACE → DEGRADED (capacity pressure)
- FAULT × LINK → DOWN (connectivity lost)
- LIMIT × TIME → DIVERGING (latency approaching threshold)
- Multiple ALARM signals → DEFECTIVE

**Relationship to Statuses**: Systems provides constraint-specific health (which aspect is
affected),
Statuses provides overall behavioral assessment (how is the system behaving). Example: ALARM ×
FLOW +
ALARM × SPACE → Status emits DEGRADED × CONFIRMED.

---

### Statuses API (7 signs × 3 dimensions = 21 signals)

| Sign       | Category         | Notes                                         |
|------------|------------------|-----------------------------------------------|
| CONVERGING | State Transition | Stabilizing toward reliable operation         |
| STABLE     | State Transition | Operating within expected parameters          |
| DIVERGING  | State Transition | Destabilizing with increasing variations      |
| ERRATIC    | State Transition | Unpredictable behavior, irregular transitions |
| DEGRADED   | State Transition | Reduced performance, elevated errors          |
| DEFECTIVE  | State Transition | Predominantly failed operations               |
| DOWN       | State Transition | Entirely non-operational                      |

**Dimensions** (confidence levels):

| Dimension | Confidence  | Notes                                         |
|-----------|-------------|-----------------------------------------------|
| TENTATIVE | Preliminary | Initial observations, limited evidence        |
| MEASURED  | Established | Strong evidence, consistent observations      |
| CONFIRMED | Definitive  | Unambiguous evidence, beyond reasonable doubt |

**Pattern**: Universal status vocabulary translating domain-specific signs upward into operational
condition assessments. The Sign describes **what is happening** (operational state), while the
Dimension describes **how certain we are** (statistical confidence). Enables cross-domain reasoning
about system health without coupling to domain specifics.

**Key Characteristics**:

- **Universal Translation Layer**: Domain signs (Resources, Tasks, Services) translate upward into
  status signs
- **Confidence Progression**: TENTATIVE → MEASURED → CONFIRMED as evidence accumulates
- **Objective Conditions**: Reports observable state, not subjective urgency (see Situations for
  that)
- **Semiotic Ascent**: Bridges domain-specific vocabularies into common operational language

**Use cases**: System health monitoring, SLO tracking, operational dashboards, cross-service health
aggregation, automated remediation triggers, alerting systems

**Translation Examples**:

- Lock CONTEST → DEGRADED × MEASURED
- Resource TIMEOUT → DEGRADED × TENTATIVE
- Service FAIL → DEFECTIVE × CONFIRMED
- Many DENY → DEGRADED × MEASURED
- Sustained SUCCESS → STABLE × CONFIRMED

**Relationship to Semiotic Hierarchy**:

```
Domain signs → STATUS (objective condition) → SITUATION (subjective urgency) → Actions
```

Statuses sits in the middle layer: above domain-specific APIs, below situational assessment.

---

### Situations API (3 signs × 3 dimensions = 9 signals)

| Sign     | Category           | Notes                                       |
|----------|--------------------|---------------------------------------------|
| NORMAL   | Urgency Assessment | No concern, no intervention required        |
| WARNING  | Urgency Assessment | Requires attention, not yet critical        |
| CRITICAL | Urgency Assessment | Demands prompt intervention, serious impact |

**Dimensions** (variability characteristics):

| Dimension | Variability | Notes                                       |
|-----------|-------------|---------------------------------------------|
| CONSTANT  | None        | Unchanging, fixed level, predictable        |
| VARIABLE  | Moderate    | Fluctuating, inconsistent, some uncertainty |
| VOLATILE  | High        | Chaotic, rapid swings, highly unpredictable |

**Pattern**: Situational assessment vocabulary expressing **operational urgency** (Sign) and
**variability behavior** (Dimension). The Sign answers "how seriously should this be treated?" while
the Dimension answers "how much is it changing?" This two-dimensional model enables appropriate
response strategies based on both severity and stability.

**Key Characteristics**:

- **Subjective Assessment**: Interpretive judgment about urgency, not objective conditions
- **Dual Dimensions**: Sign (urgency) × Dimension (variability) = response strategy
- **Value-Neutral Variability**: CONSTANT can be good (stable normal) or bad (stuck critical)
- **Highest Abstraction**: Top of semiotic hierarchy, furthest from domain specifics

**Use cases**: Incident severity classification, escalation policies, SLA evaluation, alerting
prioritization, operational response coordination, on-call routing

**Translation Examples**:

- Status STABLE × CONFIRMED (unchanging) → Situation NORMAL × CONSTANT
- Status DEGRADED × MEASURED (fluctuating) → Situation WARNING × VARIABLE
- Status DEFECTIVE × CONFIRMED (chaotic) → Situation CRITICAL × VOLATILE
- Status DIVERGING × TENTATIVE → Situation WARNING × VARIABLE

**Relationship to Semiotic Hierarchy**:

```
Domain signs → Status (objective) → SITUATION (subjective urgency + variability) → Actions/Response
```

Situations sits at the highest level: translates objective status into actionable urgency with
variability context for response planning.

**Response Strategy Matrix**:

|          | CONSTANT              | VARIABLE               | VOLATILE                     |
|----------|-----------------------|------------------------|------------------------------|
| NORMAL   | Baseline, no action   | Monitor fluctuations   | Watch for pattern changes    |
| WARNING  | Plan measured action  | Adaptive response      | Continuous monitoring        |
| CRITICAL | Execute response plan | Immediate intervention | Emergency response readiness |

---

### Outcomes API (2 signs, no dimensions)

| Sign    | Category | Notes               |
|---------|----------|---------------------|
| SUCCESS | Outcome  | Operation succeeded |
| FAIL    | Outcome  | Operation failed    |

**Pattern**: Minimal binary verdict vocabulary. No source context carried - if you need to know
*why* something failed, subscribe to the domain API. Outcomes answers one question: did it work?

**Key Insight**: Semiotic ascent trades specificity for universality. The cause is lost; the
verdict remains. This enables universal aggregation across diverse domain vocabularies.

**Use cases**: Cross-vocabulary success/failure aggregation, simple success rate metrics, binary
health signals for Status translation.

---

### Operations API (2 signs, no dimensions)

| Sign  | Category  | Notes            |
|-------|-----------|------------------|
| BEGIN | Operation | Action starting  |
| END   | Operation | Action finishing |

**Pattern**: Minimal action bracketing vocabulary. Every action has a start and a finish.
Combined with Outcomes, you get the complete picture: BEGIN → END + SUCCESS/FAIL.

**Key Insight**: Universal span tracking. If you need to know *what kind* of action,
subscribe to the domain API. Operations answers: when did it start and stop?

**Use cases**: Cross-vocabulary action span tracking, duration measurement, nesting depth
analysis, universal action counting.

---

### Trends API (5 signs, no dimensions)

| Sign   | Category | Notes                               |
|--------|----------|-------------------------------------|
| STABLE | Pattern  | In control, no significant pattern  |
| DRIFT  | Pattern  | Sustained movement (shift or trend) |
| SPIKE  | Pattern  | Sudden extreme deviation (outlier)  |
| CYCLE  | Pattern  | Oscillating pattern (alternating)   |
| CHAOS  | Pattern  | Erratic variation (abnormal)        |

**Pattern**: Statistical process control vocabulary based on Nelson's 8 Rules.
Abstracts pattern detection into universal categories.

**Nelson Rules Mapping**:

- STABLE: No rules triggered
- DRIFT: Rules 2, 3, 5, 6 (shift/trend patterns)
- SPIKE: Rule 1 (point beyond 3σ)
- CYCLE: Rule 4 (14 alternating)
- CHAOS: Rules 7, 8 (variation anomalies)

**Key Insight**: We are always watching processes unfold. Trends answers: how is it behaving?

**Use cases**: Statistical process control (SPC), anomaly detection, trend analysis,
pattern-based alerting, capacity planning.

---

### Surveys API (generic signs × 3 dimensions)

| Dimension | Category  | Notes                                     |
|-----------|-----------|-------------------------------------------|
| DIVIDED   | Agreement | No clear majority among observers         |
| MAJORITY  | Agreement | Clear majority but not complete agreement |
| UNANIMOUS | Agreement | Complete agreement among all observers    |

**Pattern**: Collective assessment vocabulary - generic over any Sign type (like Cycles).
The Sign comes from the source API being surveyed (typically Statuses.Sign, but could be
any domain sign). Surveys defines only the Dimension, which describes the agreement level.

**Key Insight**: While Statuses expresses individual assessment with confidence (how sure am I?),
Surveys expresses collective assessment with agreement (how much do we agree?). These are
complementary dimensions of observability.

**Dimension Type**: Spectrum (ordered: DIVIDED → MAJORITY → UNANIMOUS). This enables reasoning
about agreement strengthening or weakening over time.

**Use cases**: Cluster health consensus, distributed status agreement, multi-observer voting,
quorum-based decision reporting, collective assessment patterns.

**Translation to Status**: High UNANIMOUS rate → CONFIRMED confidence, DIVIDED assessments →
TENTATIVE confidence, mixed signals across cluster → may indicate network partition or
inconsistent state.

**Relationship to Statuses**:

| Layer  | Sign Source  | Dimension  | Question Answered                  |
|--------|--------------|------------|------------------------------------|
| Status | Status signs | Confidence | How sure am I? (individual)        |
| Survey | Any signs    | Agreement  | How much do we agree? (collective) |

---

### Cycles API (generic signs × 3 dimensions)

| Dimension | Category         | Notes                              |
|-----------|------------------|------------------------------------|
| SINGLE    | Pattern Analysis | First occurrence of sign in stream |
| REPEAT    | Pattern Analysis | Same sign as immediately previous  |
| RETURN    | Pattern Analysis | Seen before, but not immediately   |

**Pattern**: Meta-level sign repetition pattern observation. Cycles is generic over any Sign
type - the Sign comes from the source API being observed. Cycles defines only the Dimension,
describing repetition characteristics.

**Key Characteristics**:

- **Generic Over Signs**: Works with any Sign enum from any domain API
- **Meta-Level**: Expresses observations about sign patterns, not raw operations
- **Pattern Vocabulary**: SINGLE (novel), REPEAT (consecutive), RETURN (cycled back)
- **Located in sdk/meta**: Part of meta-level observation vocabulary

**Example Usage**:

```
Stream: GRANT, GRANT, DENY, GRANT
Output: (GRANT, SINGLE), (GRANT, REPEAT), (DENY, SINGLE), (GRANT, RETURN)
```

**Use cases**: Pattern recognition, state machine analysis, repetition detection,
sign stream analytics, cycle detection in observability data

**Translation to Status**: Long REPEAT sequences → STUCK/STABLE, rapid RETURN patterns →
OSCILLATING, many SINGLE without REPEAT → CHAOTIC

---

## Cross-API Patterns

### Pattern 1: Request-Outcome Pairs

Many APIs follow: **Operation → Outcome** pattern

**Resources**:

- ATTEMPT → GRANT/DENY
- ACQUIRE → GRANT/TIMEOUT

**Services**:

- CALL → SUCCESS/FAIL
- START → SUCCESS/FAIL

**Tasks**:

- SUBMIT → SCHEDULE/REJECT

### Pattern 2: Lifecycle Triplets

State transitions follow: **Start → Execution → Terminal** pattern

**Processes**: SPAWN → START → STOP/FAIL/CRASH
**Tasks**: SUBMIT → START → COMPLETE/FAIL
**Transactions**: START → PREPARE → COMMIT/ROLLBACK

### Pattern 3: Dual Outcomes (Success/Failure)

Terminal states have positive and negative forms:

| API          | Success  | Failure         |
|--------------|----------|-----------------|
| Services     | SUCCESS  | FAIL            |
| Tasks        | COMPLETE | FAIL            |
| Processes    | STOP     | FAIL, CRASH     |
| Transactions | COMMIT   | ROLLBACK, ABORT |

### Pattern 4: Resource Competition

Contention signs indicate competition:

- **Atomics**: FAIL (CAS contention), SPIN/BACKOFF/PARK (contention response)
- **Locks**: CONTEST (CAS failures)
- **Resources**: DENY (capacity unavailable)
- **Queues**: OVERFLOW (capacity violated)
- **Tasks**: REJECT (queue full)
- **Transactions**: CONFLICT (write conflict)

### Pattern 5: Time-Based Violations

Timeout/expiration signs (failure to complete):

- **Resources**: TIMEOUT
- **Services**: EXPIRE
- **Tasks**: TIMEOUT
- **Transactions**: EXPIRE
- **Locks**: TIMEOUT

Time constraint outcome signs (completion classification):

- **Timers**: MEET/MISS (did completion satisfy the constraint?)

### Pattern 6: Flow Control (Suspend/Resume)

Coordination pairs:

- **Services**: SUSPEND/RESUME, DELAY
- **Tasks**: SUSPEND/RESUME, SCHEDULE
- **Processes**: SUSPEND/RESUME

---

## Sign Categories: Summary Statistics

| Category               | Count | % of Total | APIs                                                                                                                    |
|------------------------|-------|------------|-------------------------------------------------------------------------------------------------------------------------|
| **Operation**          | 55    | 27%        | All domain APIs; includes Latches, Exchanges, Operations, Atomics, Caches, Pipelines, Leases, Routers, Breakers, Probes |
| **Outcome**            | 35    | 17%        | Services, Tasks, Resources, Transactions, Flows, Timers, Caches, Leases, Probes, Agents                                 |
| **Flow Control**       | 15    | 7%         | Services, Tasks, Processes, Pipelines                                                                                   |
| **State Transition**   | 20    | 9%         | Services, Tasks, Processes, Transactions, Breakers, Statuses                                                            |
| **Urgency Assessment** | 3     | 1%         | Situations (NORMAL, WARNING, CRITICAL)                                                                                  |
| **Error**              | 14    | 7%         | Services, Tasks, Locks, Latches, Leases, Pipelines, Caches                                                              |
| **Condition**          | 12    | 6%         | Queues, Stacks, Transactions, Logs, Gauges, Counters, Pipelines, Routers                                                |
| **Assertion**          | 3     | 1%         | Actors (AFFIRM, EXPLAIN, REPORT)                                                                                        |
| **Coordination**       | 3     | 1%         | Actors (REQUEST, COMMAND, ACKNOWLEDGE)                                                                                  |
| **Disagreement**       | 2     | 1%         | Actors (DENY, CLARIFY)                                                                                                  |
| **Commitment**         | 4     | 2%         | Actors (PROMISE, DELIVER), Agents (PROMISE, ACCEPT)                                                                     |
| **Discovery**          | 2     | 1%         | Agents (OFFER, INQUIRE)                                                                                                 |
| **Dependency**         | 3     | 1%         | Agents (DEPEND, OBSERVE, VALIDATE)                                                                                      |
| **Resolution**         | 3     | 1%         | Agents (FULFILL, BREACH, RETRACT)                                                                                       |
| **Retry**              | 3     | 1%         | Atomics (SPIN, YIELD, BACKOFF)                                                                                          |
| **Escalation**         | 2     | 1%         | Atomics (PARK, EXHAUST)                                                                                                 |
| **Contention**         | 2     | 1%         | Locks (CONTEST), Atomics (FAIL)                                                                                         |
| **Process Control**    | 4     | 2%         | Systems (NORMAL, LIMIT, ALARM, FAULT)                                                                                   |
| **Flow Decision**      | 2     | 1%         | Valves (PASS, DENY)                                                                                                     |
| **Control Action**     | 2     | 1%         | Valves (EXPAND, CONTRACT)                                                                                               |
| **State Observation**  | 5     | 2%         | Logs (INFO, DEBUG), Sensors (BELOW, NOMINAL, ABOVE)                                                                     |
| **Pattern Analysis**   | 3     | 1%         | Cycles (SINGLE, REPEAT, RETURN)                                                                                         |
| **Modification**       | 2     | 1%         | Locks (UPGRADE, DOWNGRADE)                                                                                              |
| **Progress**           | 3     | 1%         | Tasks (PROGRESS), Pipelines (CHECKPOINT, WATERMARK)                                                                     |
| **Emergency/Recovery** | 2     | 1%         | Valves (DROP, DRAIN)                                                                                                    |

**Total unique signs analyzed**: ~210+ across 33 APIs (including: Actors, Agents, Caches,
Pipelines, Leases, Gauges, Counters, Probes, Routers, Breakers, Statuses, Situations, Cycles)

### Universal SDK APIs

The `sdk/` package contains universal vocabularies that abstract over domain-specific signs:

| API        | Signs                                                             | Dimensions                     | Purpose                                      |
|------------|-------------------------------------------------------------------|--------------------------------|----------------------------------------------|
| Statuses   | CONVERGING, STABLE, DIVERGING, ERRATIC, DEGRADED, DEFECTIVE, DOWN | TENTATIVE, MEASURED, CONFIRMED | Operational condition - what is happening?   |
| Situations | NORMAL, WARNING, CRITICAL                                         | CONSTANT, VARIABLE, VOLATILE   | Urgency assessment - how serious is it?      |
| Outcomes   | SUCCESS, FAIL                                                     | —                              | Binary verdict - did it work?                |
| Operations | BEGIN, END                                                        | —                              | Action brackets - when did it run?           |
| Trends     | STABLE, DRIFT, SPIKE, CYCLE, CHAOS                                | —                              | Pattern detection - how is it behaving?      |
| Surveys    | (generic over source Sign)                                        | DIVIDED, MAJORITY, UNANIMOUS   | Collective agreement - how much do we agree? |
| Cycles     | (generic over source Sign)                                        | SINGLE, REPEAT, RETURN         | Repetition patterns - how are signs cycling? |

These complement domain APIs by providing cross-vocabulary aggregation points.

**The Universal Septet** (ordered by semiotic hierarchy):

1. **Statuses**: CONVERGING/STABLE/DIVERGING/ERRATIC/DEGRADED/DEFECTIVE/DOWN ×
   TENTATIVE/MEASURED/CONFIRMED (what is happening? how certain?)
2. **Situations**: NORMAL/WARNING/CRITICAL × CONSTANT/VARIABLE/VOLATILE (how serious? how stable?)
3. **Operations**: BEGIN/END (when did it run?)
4. **Outcomes**: SUCCESS/FAIL (did it work?)
5. **Trends**: STABLE/DRIFT/SPIKE/CYCLE/CHAOS (how is it behaving?)
6. **Surveys**: (Sign × DIVIDED/MAJORITY/UNANIMOUS) (how much do we agree?)
7. **Cycles**: (Sign × SINGLE/REPEAT/RETURN) (how are signs cycling?)

**Hierarchy**: Statuses and Situations form the **semiotic ascent layer**, translating domain signs
upward into universal operational vocabulary. The remaining five provide **meta-level observation**
capabilities.

---

## Design Principles Revealed

### 1. **Implementer-Observable Semantics**

All signs represent what the implementer can **directly observe**:

- ✅ Connection pool knows: EXPAND (created connection), BORROW (connection taken), RECLAIM (
  connection returned)
- ✅ Task executor knows: REJECT (queue full)
- ✅ Lock knows: CONTEST (CAS failed)
- ✅ Application code knows: SEVERE (error occurred)
- ✅ Rate limiter knows: PASS (request allowed), DENY (request blocked)
- ✅ Auto-scaler knows: EXPAND (increased capacity), CONTRACT (decreased capacity)
- ❌ **No pattern recognition required**: Signs are not inferred from multiple observations

### 2. **Outcomes Over Operations**

APIs favor **outcome signs** over operational signs:

- Not: "tried to acquire" → "tried again" → "tried again" → "got it"
- But: ATTEMPT → GRANT (outcome is the sign)

### 3. **Symmetry in Boundary Conditions**

Violations come in pairs:

- OVERFLOW/UNDERFLOW (Queues)
- GRANT/DENY (Resources)
- SUCCESS/FAIL (Services)

### 4. **Reuse Vocabulary Where Possible**

Locks API explicitly reuses Resources vocabulary rather than inventing new terms for the same
semantics.

### 5. **Dimensions Capture Perspective, Not Location**

When dimensions exist (Services, Transactions):

- CALLER/CALLEE: **who is acting** (calling vs serving)
- COORDINATOR/PARTICIPANT: **who is leading** (coordinating vs participating)
- NOT: where data is (LOG vs STATE)
