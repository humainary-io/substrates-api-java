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
| **Contention**       | Resource competition signals                              | CONTEST (CAS failures)                 |
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

- **Locks**: CONTEST (CAS failures)
- **Resources**: DENY (capacity unavailable)
- **Queues**: OVERFLOW (capacity violated)
- **Tasks**: REJECT (queue full)
- **Transactions**: CONFLICT (write conflict)

### Pattern 5: Time-Based Violations

Timeout/expiration signs:

- **Resources**: TIMEOUT
- **Services**: EXPIRE
- **Tasks**: TIMEOUT
- **Transactions**: EXPIRE
- **Locks**: TIMEOUT

### Pattern 6: Flow Control (Suspend/Resume)

Coordination pairs:

- **Services**: SUSPEND/RESUME, DELAY
- **Tasks**: SUSPEND/RESUME, SCHEDULE
- **Processes**: SUSPEND/RESUME

---

## Sign Categories: Summary Statistics

| Category               | Count | % of Total | APIs                                                             |
|------------------------|-------|------------|------------------------------------------------------------------|
| **Operation**          | 27    | 26%        | All except Logs, Valves, Systems, includes Latches, Exchanges    |
| **Outcome**            | 19    | 19%        | All except Queues, Logs, Valves, Pools, Stacks, Systems, Latches |
| **Flow Control**       | 13    | 13%        | Services, Tasks, Processes                                       |
| **State Transition**   | 10    | 10%        | Services, Tasks, Processes, Transactions                         |
| **Error**              | 11    | 11%        | All except Resources, Queues, Systems, includes Logs, Latches    |
| **Condition**          | 8     | 8%         | Queues, Stacks, Transactions, Logs (SEVERE, WARNING)             |
| **Process Control**    | 4     | 4%         | Systems (NORMAL, LIMIT, ALARM, FAULT)                            |
| **Flow Decision**      | 2     | 2%         | Valves (PASS, DENY)                                              |
| **Control Action**     | 2     | 2%         | Valves (EXPAND, CONTRACT)                                        |
| **State Observation**  | 5     | 5%         | Logs (INFO, DEBUG), Sensors (BELOW, NOMINAL, ABOVE)              |
| **Modification**       | 2     | 2%         | Locks                                                            |
| **Emergency Response** | 1     | 1%         | Valves (DROP)                                                    |
| **Recovery Response**  | 1     | 1%         | Valves (DRAIN)                                                   |
| **Contention**         | 1     | 1%         | Locks                                                            |
| **Progress**           | 1     | 1%         | Tasks                                                            |

**Total unique signs analyzed**: ~100 across 14 APIs (including Systems and Exchanges)

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
