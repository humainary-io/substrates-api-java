# Serventis - Observability Extension for Substrates

**Semantic signaling framework for distributed service observability**

Serventis is an extension module built on [Humainary Substrates](../../README.md) that provides
comprehensive observability primitives for distributed services. It enables structured monitoring,
assessment, and reporting of service health, performance, and interaction patterns through semantic
signal emission.

## Overview

Serventis implements a **layered observability hierarchy** that transforms raw operational signals
into actionable situational awareness:

```
Raw Signs → Conditions → Situations → Actions
    ↓             ↓            ↓
Counters/    Statuses →   Situations
Gauges/   →              ↑
Caches/                  Actors (dialogue coordination)
Probes/                  Agents (promise coordination)
Routers
```

- **Counters**: Monotonic accumulation signs (requests, events)
- **Gauges**: Bidirectional signs (connections, queue depth, utilization)
- **Caches**: Cache interaction signs (lookup, hit, miss, store, evict, expire, remove)
- **Probes**: Low-level communication outcome observation (connect, send, receive, process)
- **Routers**: Packet routing operation signs (send, receive, forward, route, drop, fragment)
- **Pipelines**: Data pipeline processing signs (input, output, transform, filter, aggregate,
  buffer)
- **Actors**: Speech act communication for human-AI dialogue coordination
- **Agents**: Promise-based autonomous coordination (offer, promise, accept, fulfill)
- **Transactions**: Distributed transaction coordination (start, prepare, commit, rollback)
- **Breakers**: Circuit breaker state machine observation (close, open, half-open, trip)
- **Leases**: Time-bounded resource ownership (acquire, grant, deny, renew, extend, release, expire,
  revoke)
- **Locks**: Mutual exclusion and synchronization primitive observation
- **Atomics**: CAS contention dynamics observation (attempt, success, fail, spin, yield, backoff,
  park, exhaust)
- **Latches**: Coordination barrier observation (await, arrive, release, timeout, reset, abandon)
- **Logs**: Logging activity observation (severe, warning, info, debug)
- **Monitors**: Operational condition assessment (stable, degraded, defective, down)
- **Pools**: Resource pool capacity and utilization observation (expand, contract, borrow, reclaim)
- **Processes**: OS-level process lifecycle observation
- **Queues**: Queue occupancy and flow state monitoring
- **Resources**: Resource availability and exhaustion tracking
- **Services**: Rich semantic signaling for service lifecycle and coordination
- **Setpoints**: Positional measurement relative to configured reference values
- **Situations**: Situational significance assessment (normal, warning, critical)
- **Tasks**: Asynchronous work unit lifecycle observation
- **Timers**: Time constraint outcome observation (met/missed deadlines and thresholds)
- **Outcomes**: Binary success/failure verdict vocabulary for universal aggregation
- **Operations**: Binary begin/end action bracket vocabulary for universal span tracking
- **Trends**: Pattern detection vocabulary for statistical process control (stable, drift, spike,
  cycle, chaos)
- **Surveys**: Collective assessment agreement vocabulary (divided, majority, unanimous)
- **Valves**: Adaptive flow control observation (pass, deny, expand, contract, drop, drain)
- **Flows**: Data flow stage transition observation (success/fail at ingress/transit/egress)

## Architecture

### Module Structure

```
serventis/
├── api/
│   └── Serventis.java         - Common signal abstraction (Signal × Sign × Dimension)
├── sdk/                        - Universal languages (essential)
│   ├── SignalSet.java          - Signal caching utility
│   ├── Systems.java            - Universal constraint state vocabulary
│   ├── Statuses.java           - Universal operational condition vocabulary
│   ├── Situations.java         - Universal urgency/significance vocabulary
│   ├── Outcomes.java           - Binary SUCCESS/FAIL verdict vocabulary
│   ├── Operations.java         - Binary BEGIN/END action bracket vocabulary
│   ├── Trends.java             - Pattern detection vocabulary (STABLE/DRIFT/SPIKE/CYCLE/CHAOS)
│   ├── Surveys.java            - Collective assessment agreement vocabulary (DIVIDED/MAJORITY/UNANIMOUS)
│   └── meta/                   - Meta-level analytical vocabularies
│       └── Cycles.java         - Sign recurrence pattern observation
└── opt/                        - Domain vocabularies (optional)
    ├── exec/                   - Execution lifecycles
    │   ├── Services.java       - Service lifecycle signaling
    │   ├── Processes.java      - OS-level process lifecycle
    │   ├── Tasks.java          - Asynchronous work unit lifecycle
    │   ├── Timers.java         - Time constraint outcome observation
    │   └── Transactions.java   - Distributed transaction coordination
    ├── pool/                   - Resource management
    │   ├── Resources.java      - Resource availability tracking
    │   ├── Pools.java          - Resource pool capacity and utilization
    │   ├── Leases.java         - Lease lifecycle and coordination
    │   └── Exchanges.java      - Resource exchange between parties (REA model)
    ├── sync/                   - Synchronization primitives
    │   ├── Locks.java          - Mutual exclusion and synchronization
    │   ├── Atomics.java        - CAS contention dynamics observation
    │   └── Latches.java        - Coordination barrier observation
    ├── data/                   - Data structures
    │   ├── Queues.java         - Queue state monitoring
    │   ├── Stacks.java         - Stack state monitoring
    │   ├── Caches.java         - Cache interaction observation
    │   └── Pipelines.java      - Data pipeline stage operations
    ├── flow/                   - Flow control
    │   ├── Valves.java         - Adaptive flow control observation
    │   ├── Breakers.java       - Circuit breaker state machine
    │   ├── Routers.java        - Packet routing operation
    │   └── Flows.java          - Data flow stage transitions
    ├── role/                   - Role-based coordination
    │   ├── Agents.java         - Promise-based autonomous coordination
    │   └── Actors.java         - Speech act communication for dialogue
    └── tool/                   - Measurement tools
        ├── Probes.java         - Communication outcome observation
        ├── Counters.java       - Monotonic accumulation signs
        ├── Gauges.java         - Bidirectional measurement signs
        ├── Logs.java           - Logging activity observation
        └── Sensors.java      - Positional measurement relative to references
```

### Common Abstractions (Serventis API)

The `Serventis` class provides the foundational interfaces that all signal-based APIs implement:

**Core Pattern**: `Signal = Sign × Dimension`

- **Signal**: Observable event composed of a sign and dimension
- **Sign**: Primary semantic classification (what is being observed)
- **Dimension**: Secondary qualifier (perspective, confidence, or directionality)

All Serventis APIs follow this uniform structural pattern:

- **Probes**: Sign (CONNECT, TRANSMIT, etc.) × Dimension (RELEASE, RECEIPT)
- **Services**: Sign (START, CALL, SUCCESS, etc.) × Dimension (RELEASE, RECEIPT)
- **Agents**: Sign (OFFER, PROMISE, FULFILL, etc.) × Dimension (PROMISER, PROMISEE)
- **Systems**: Sign (NORMAL, LIMIT, ALARM, FAULT) × Dimension (SPACE, FLOW, LINK, TIME)
- **Statuses**: Sign (STABLE, DEGRADED, etc.) × Dimension (TENTATIVE, MEASURED, CONFIRMED)
- **Logs**: Sign (SEVERE, WARNING, INFO, DEBUG) × No Dimension
- **Pools**: Sign (EXPAND, CONTRACT, BORROW, RECLAIM) × No Dimension
- **Valves**: Sign (PASS, DENY, EXPAND, CONTRACT, DROP, DRAIN) × No Dimension
- **Flows**: Sign (SUCCESS, FAIL) × Dimension (INGRESS, TRANSIT, EGRESS)
- **Outcomes**: Sign (SUCCESS, FAIL) × No Dimension
- **Operations**: Sign (BEGIN, END) × No Dimension
- **Trends**: Sign (STABLE, DRIFT, SPIKE, CYCLE, CHAOS) × No Dimension
- **Surveys**: Sign (generic) × Dimension (DIVIDED, MAJORITY, UNANIMOUS)

This architectural consistency enables:

- Polymorphic handling of signals across different APIs
- Generic utilities that work with any signal type
- Type-safe composition while maintaining domain-specific semantics
- Consistent patterns across the entire framework

### Percept Interface

All Serventis instruments implement the **Percept** interface from `substrates.api`:

```java
public interface Percept {
    // Marker interface for all observable entities
}
```

**Key Concepts**:

- **Percept**: Marker interface for entities capable of emitting observations to the substrate
- **Instruments as Percepts**: All Serventis instruments (Counters, Gauges, Statuses, Pipelines,
  etc.)
  implement Percept
- **Type Safety**: The Composer, Conduit, and Circuit APIs use `P extends Percept` constraints
  to ensure type safety
- **Pipe Relationship**: `Pipe` also extends Percept, as it's the primitive emission mechanism

**Type Hierarchy**:

```
Percept (substrates.api)
├── Pipe<E> (primitive emission)
└── Instruments (semantic facades)
    ├── Counter (sign emitter)
    ├── Gauge (sign emitter)
    ├── Pipeline (sign emitter)
    ├── Monitor (signal emitter: Sign × Dimension)
    ├── Probe (signal emitter: Sign × Dimension)
    └── ... (all instruments)
```

This design ensures:

- **Intentionality**: Composers must return percepts (not arbitrary objects)
- **Semantic clarity**: Instruments provide domain semantics over raw Pipe emissions
- **Framework extensibility**: New percept types can be added while maintaining type safety

### Design Principles

1. **Separation of Concerns**: Probes observe, monitors assess, reporters interpret
2. **Semantic Richness**: Signals carry meaning beyond success/failure
3. **Dual Orientation**: RELEASE (self) vs RECEIPT (observed) perspectives
4. **Statistical Confidence**: Conditions include certainty measures (tentative, measured,
   confirmed)
5. **Substrate Integration**: Built on circuits, conduits, and channels for deterministic ordering

## Core APIs

### Actors API

**Purpose**: Speech act observation for human-AI dialogue coordination grounded in Speech Act Theory

**Speech Act Categories**:

1. **Questions & Inquiry** (1 sign): ASK
2. **Assertions & Explanations** (3 signs): ASSERT, EXPLAIN, REPORT
3. **Coordination** (3 signs): REQUEST, COMMAND, ACKNOWLEDGE
4. **Disagreement & Refinement** (2 signs): DENY, CLARIFY
5. **Commitment & Delivery** (2 signs): PROMISE, DELIVER

**Total Signs**: 11 speech acts for practical conversational coordination

**Key Concepts**:

- **Actor**: Any entity (human or machine) performing speech acts
- **Speech Act**: Communicative action with illocutionary force
- **Dialogue**: Sequences of speech acts between coordinating actors
- **Commitment Arc**: PROMISE → DELIVER tracking for reliability measurement

**Dialogue Patterns**:

- **Question-Answer**: ASK → EXPLAIN → ASSERT → ACKNOWLEDGE
- **Request-Delivery**: REQUEST → ACKNOWLEDGE → PROMISE → DELIVER → ACKNOWLEDGE
- **Correction-Clarification**: ASSERT → DENY → CLARIFY → ACKNOWLEDGE
- **Collaborative Refinement**: REQUEST → EXPLAIN → DENY → CLARIFY → ACKNOWLEDGE → DELIVER

**Use Cases**:

- Human-AI collaboration tracking and measurement
- Conversational quality assessment
- Commitment fulfillment monitoring (PROMISE → DELIVER)
- Coordination effectiveness metrics
- Dialogue pattern analysis (question response latency, correction cycles)

**Performance**: Conversational timescales (seconds to minutes), high semantic density

**Relationship to Other APIs**:

- **Statuses API**: Actors ASSERT status conditions about system state
- **Counters, Gauges, Caches**: Actors REPORT observations from instruments
- **Routers, Resources**: Actors REQUEST actions on system components
- Meta-percepts observe dialogue to measure collaboration effectiveness

---

### Agents API

**Purpose**: Promise-based autonomous agent coordination grounded in Promise Theory

**Promise Theory Model**:

Promise Theory (Mark Burgess) models systems as autonomous agents making voluntary commitments
rather than following imposed policies. This API enables observation of promise lifecycles in
distributed autonomous systems.

**Dual-Perspective Model**:

- **PROMISER**: Self-perspective signals ("I am promising", "I am fulfilling")
- **PROMISEE**: Other-perspective signals ("They promised", "They fulfilled")

**Total Signals**: 20 signals (10 signs × 2 perspectives)

**The 10 Promise Signs**:

1. **OFFER**: Agent advertises capability or intent (discovery phase)
2. **PROMISE**: Agent commits to deliver obligation (commitment phase)
3. **ACCEPT**: Agent acknowledges promise from another (commitment phase)
4. **FULFILL**: Agent completes promised obligation (delivery phase)
5. **RETRACT**: Agent withdraws promise before fulfillment (coordination phase)
6. **BREACH**: Agent fails to fulfill promise (failure mode)
7. **INQUIRE**: Agent requests information about capabilities (discovery phase)
8. **OBSERVE**: Agent witnesses another's state or behavior (assessment phase)
9. **DEPEND**: Agent declares dependency on another's promise (coordination phase)
10. **VALIDATE**: Agent confirms another's fulfillment meets expectations (verification phase)

**Key Concepts**:

- **Agent**: Autonomous entity making voluntary promises
- **Promise**: Voluntary commitment to maintain or deliver state
- **Signal**: Perspectival observation (PROMISER self-view, PROMISEE other-view)
- **Sign**: The type of promise-related action being performed
- **Perspective**: PROMISER (self) vs PROMISEE (observed other)

**Promise Lifecycle Patterns**:

- **Discovery**: INQUIRE → OFFERED → OBSERVE
- **Commitment**: OFFER → PROMISED → ACCEPTED
- **Dependency**: DEPEND → ACCEPTED → PROMISED
- **Fulfillment**: PROMISE → FULFILL
- **Breach**: PROMISE → BREACH
- **Retraction**: PROMISE → RETRACT → ACCEPTED
- **Complete Arc**: OFFER → PROMISE → ACCEPT → FULFILL → VALIDATE

**Use Cases**:

- Autonomous system coordination without central control
- Voluntary cooperation between independent services
- Trust and reliability measurement through promise fulfillment
- Decentralized resource allocation
- Capability discovery in dynamic environments
- Dependency tracking between autonomous components
- Contract-based coordination patterns

**Performance**: Coordination timescales (seconds to minutes), high semantic density

**Relationship to Other APIs**:

- **Actors API**: Complementary - Actors handle dialogue (ASK, EXPLAIN), Agents handle commitments (
  PROMISE, FULFILL)
- **Services API**: Agents PROMISE to provide service capabilities
- **Resources API**: Agents OFFER and PROMISE resource availability
- **Statuses API**: Agents OBSERVE conditions and VALIDATE fulfillment
- Meta-percepts track promise fulfillment rates, breach patterns, and autonomy effectiveness

**Promise Theory vs Speech Act Theory**:

- **Agents (Promise Theory)**: Autonomous entities making voluntary commitments - focus on *
  *voluntary cooperation**
- **Actors (Speech Acts)**: Conversational entities performing communicative acts - focus on *
  *dialogue coordination**

These APIs complement each other: Actors establish shared understanding through dialogue, Agents
establish commitments through promises. Both operate at conversational timescales but model
different aspects of coordination.

---

### Transactions API

**Purpose**: Distributed transaction coordination observation grounded in transaction processing
theory

**Transaction Model**:

Transaction processing theory provides the foundation for distributed coordination patterns (2PC,
3PC, Saga). This API enables observation of transaction lifecycles in distributed systems without
coupling to specific protocol implementations.

**Dual-Perspective Model**:

- **COORDINATOR**: Self-perspective signals ("I am coordinating", "I am committing")
- **PARTICIPANT**: Other-perspective signals ("Coordinator started", "I prepared")

**Total Signals**: 16 signals (8 signs × 2 perspectives)

**The 8 Transaction Signs**:

1. **START**: Transaction initiation (begin unit of work)
2. **PREPARE**: Voting phase (2PC prepare - can you commit?)
3. **COMMIT**: Final commitment (all voted yes, make changes permanent)
4. **ROLLBACK**: Transaction abort (rollback to initial state)
5. **ABORT**: Forced termination (deadlock, constraint violation)
6. **EXPIRE**: Transaction exceeded time budget (timeout)
7. **CONFLICT**: Write conflict or constraint violation detected
8. **COMPENSATE**: Compensating action (saga pattern rollback)

**Key Concepts**:

- **Transaction**: Unit of work with ACID properties
- **Coordinator**: Transaction manager orchestrating the protocol (2PC leader, saga orchestrator)
- **Participant**: Cohort executing local operations under transaction control
- **Signal**: Perspectival observation (COORDINATOR self-view, PARTICIPANT other-view)
- **Sign**: The type of transaction operation being performed
- **Perspective**: COORDINATOR (protocol leader) vs PARTICIPANT (protocol member)

**Transaction Lifecycle Patterns**:

- **Standard 2PC**: START → PREPARE → COMMIT (all yes) or ROLLBACK (any no)
- **With Expiration**: START → PREPARE → EXPIRE → ROLLBACK
- **With Conflict**: START → PREPARE → CONFLICT → ABORT
- **Saga Pattern**: START → [FAIL] → COMPENSATE → ROLLBACK

**Use Cases**:

- Distributed transaction coordination (2PC, 3PC)
- Saga pattern orchestration and compensation
- Consensus protocol observation (Paxos, Raft)
- Transaction failure mode analysis (conflicts, timeouts, deadlocks)
- Isolation level effectiveness measurement
- Distributed state consistency verification

**Performance**: Coordination timescales (milliseconds to seconds), high semantic density

**Relationship to Other APIs**:

- **Services API**: Transactions often span service boundaries (distributed transactions)
- **Resources API**: Transactions ACQUIRE/RELEASE locks or resources for isolation
- **Statuses API**: Transaction patterns (many ABORTs) inform condition assessment (DEGRADED)
- **Agents API**: Transactional coordination complements promise-based coordination
- Meta-percepts track commit rates, conflict patterns, and coordination reliability

**Important Note**: This API is for **observability** of transaction semantics, not implementation
of transaction protocols. When your database, coordinator, or distributed system performs
transactional operations, use this API to emit signals that enable meta-level reasoning about
transaction patterns and coordination effectiveness.

---

### Breakers API

**Purpose**: Circuit breaker state machine observation for resilience patterns

**Circuit Breaker Pattern**:

The circuit breaker pattern prevents cascading failures in distributed systems by breaking the
circuit when failure thresholds are exceeded. This API enables observation of breaker state
transitions without coupling to specific breaker implementations.

**The 6 Breaker Signs**:

1. **CLOSE**: Circuit closed - traffic flowing normally
2. **OPEN**: Circuit opened - traffic blocked, fail fast
3. **HALF_OPEN**: Circuit testing recovery - limited traffic allowed
4. **TRIP**: Failure threshold exceeded - circuit breaking now
5. **PROBE**: Test request sent in half-open state
6. **RESET**: Circuit manually reset to closed state

**Key Concepts**:

- **Breaker**: Observable circuit breaker emitting state transition signals
- **Sign**: The type of state or event (CLOSE, OPEN, HALF_OPEN, TRIP, PROBE, RESET)
- **State Machine**: CLOSED → TRIP → OPEN → HALF_OPEN → PROBE → (success: CLOSE | failure: OPEN)

**Breaker State Machine**:

- **Normal Operation**: CLOSE - requests pass through
- **Failure Detection**: TRIP - threshold exceeded
- **Protection**: OPEN - requests fail immediately
- **Recovery Testing**: HALF_OPEN - sampling requests
- **Test Request**: PROBE - individual test attempt
- **Manual Recovery**: RESET - operator intervention

**Use Cases**:

- Monitoring cascading failure prevention
- Tracking service degradation and recovery patterns
- Analyzing failure thresholds and recovery times
- Building adaptive resilience policies
- Observing circuit breaker effectiveness

**Performance**: Coordination timescales (milliseconds to seconds), infrequent transitions

**Relationship to Other APIs**:

- **Services API**: Service failures (FAIL) may trigger breaker TRIP and OPEN
- **Statuses API**: Breaker OPEN state may indicate DEGRADED or DOWN conditions
- **Resources API**: Resource DEPLETED may correlate with breaker OPEN
- **Situations API**: Sustained OPEN state may escalate to WARNING or CRITICAL
- Meta-percepts track breaker effectiveness, false trips, and recovery rates

---

### Flows API

**Purpose**: Data flow stage transition observation for staged processing systems

**Design Philosophy**: Flows models data movement through staged systems with three distinct phases:
entry (INGRESS), processing (TRANSIT), and exit (EGRESS). Each stage can succeed or fail, creating
a matrix of observable outcomes for throughput analysis, bottleneck detection, and failure
localization.

**Dual-Perspective Model**:

| Dimension   | Stage      | Description                    |
|-------------|------------|--------------------------------|
| **INGRESS** | Entry      | Item entering the system       |
| **TRANSIT** | Processing | Item moving through the system |
| **EGRESS**  | Exit       | Item leaving the system        |

**The 2 Flow Signs**:

| Sign        | Category | Description                                  |
|-------------|----------|----------------------------------------------|
| **SUCCESS** | Outcome  | Item successfully transitioned through stage |
| **FAIL**    | Outcome  | Item failed to transition through stage      |

**Total Signals**: 6 signals (2 signs × 3 dimensions)

**Signal Matrix**:

|         | INGRESS | TRANSIT | EGRESS |
|---------|---------|---------|--------|
| SUCCESS | entered | moved   | exited |
| FAIL    | refused | dropped | failed |

**Key Concepts**:

- **Flow**: Observable subject that emits signals describing stage transitions
- **Sign**: Outcome of a stage transition (SUCCESS or FAIL)
- **Dimension**: Stage where the transition occurred (INGRESS, TRANSIT, or EGRESS)
- **Signal**: Composition of Sign × Dimension representing a stage outcome

**Flow Patterns**:

- **Successful Flow**: SUCCESS(INGRESS) → SUCCESS(TRANSIT) → SUCCESS(EGRESS)
- **Entry Rejection**: FAIL(INGRESS) — item refused at entry
- **Processing Failure**: SUCCESS(INGRESS) → FAIL(TRANSIT) — item dropped mid-stream
- **Delivery Failure**: SUCCESS(INGRESS) → SUCCESS(TRANSIT) → FAIL(EGRESS) — item failed to exit

**Use Cases**:

1. **Message Queue Processing**:
   ```java
   var flow = circuit.conduit(Flows::composer)
     .percept(cortex.name("queue.orders"));

   // Message received
   if (queue.offer(message)) {
     flow.success(INGRESS);  // entered
   } else {
     flow.fail(INGRESS);     // refused
   }

   // Message processed
   try {
     process(message);
     flow.success(TRANSIT);  // moved
   } catch (Exception e) {
     flow.fail(TRANSIT);     // dropped
   }

   // Message delivered
   if (deliver(message)) {
     flow.success(EGRESS);   // exited
   } else {
     flow.fail(EGRESS);      // failed
   }
   ```

2. **Data Pipeline**:
   ```java
   var flow = circuit.conduit(Flows::composer)
     .percept(cortex.name("pipeline.etl"));

   flow.success(INGRESS);    // data ingested
   flow.success(TRANSIT);    // data transformed
   flow.success(EGRESS);     // data written
   ```

3. **Request Handler**:
   ```java
   var flow = circuit.conduit(Flows::composer)
     .percept(cortex.name("http.requests"));

   flow.success(INGRESS);    // request accepted
   flow.success(TRANSIT);    // processing succeeded
   flow.success(EGRESS);     // response delivered
   ```

**Performance**: High-throughput (100K-1M+ signals/sec), zero-allocation SignalSet caching

**Relationship to Other APIs**:

- **Pipelines API**: Pipelines models processing operations; Flows models stage transitions
- **Queues API**: Queue operations (ENQUEUE/DEQUEUE) may emit corresponding Flow signals
- **Valves API**: Valve DENY may correlate with Flow FAIL at INGRESS
- **Statuses API**: High FAIL rates at any stage inform Status conditions (DEGRADED)
- **Situations API**: Sustained failures → WARNING or CRITICAL situations

**Semiotic Ascent (Flows → Status → Situation)**:

| Flow Pattern            | Status    | Situation |
|-------------------------|-----------|-----------|
| High SUCCESS across all | HEALTHY   | NORMAL    |
| High FAIL at INGRESS    | SATURATED | CAPACITY  |
| High FAIL at TRANSIT    | DEGRADED  | OUTAGE    |
| High FAIL at EGRESS     | BLOCKED   | OUTAGE    |

---

### Leases API

**Purpose**: Semantic signaling for time-bounded resource ownership and distributed coordination

**Theoretical Foundation**: Lease semantics provide a fundamental mechanism for distributed
coordination through time-bounded ownership grants with automatic expiration (TTL). This API enables
observation of lease acquisition, renewal, expiration, and revocation patterns from both authority
and client perspectives.

**Dual-Perspective Model**:

| Dimension  | Perspective         | Role                  | Example Signals             |
|------------|---------------------|-----------------------|-----------------------------|
| **LESSOR** | Authority (acting)  | Grants/revokes leases | GRANT, DENY, REVOKE, EXPIRE |
| **LESSEE** | Client (requesting) | Acquires/holds leases | ACQUIRE, RENEW, RELEASE     |

**Who is reporting**:

- **LESSOR**: The coordination service, lock manager, or authority that grants, denies, extends, and
  revokes leases
- **LESSEE**: The application instance, service node, or client that requests, holds, renews, and
  releases leases

**The 9 Lease Signs**:

| Sign        | Description                                                   |
|-------------|---------------------------------------------------------------|
| **ACQUIRE** | Client attempting to obtain new lease                         |
| **GRANT**   | Lease successfully granted (positive ACQUIRE outcome)         |
| **DENY**    | Lease request denied (negative ACQUIRE outcome)               |
| **RENEW**   | Holder attempting to extend lease duration                    |
| **EXTEND**  | Lease duration successfully extended (positive RENEW outcome) |
| **RELEASE** | Holder voluntarily terminated lease before expiration         |
| **EXPIRE**  | Lease automatically terminated (TTL exhausted)                |
| **REVOKE**  | Lease forcefully revoked by authority                         |
| **PROBE**   | Status check on lease (validity, holder identity)             |

**Key Concepts**:

- **Lease**: Time-bounded ownership grant with automatic expiration (TTL)
- **TTL**: Time-to-live duration after which the lease automatically expires
- **Request/Response Pairing**: ACQUIRE → GRANT/DENY, RENEW → EXTEND
- **Termination Types**: RELEASE (voluntary), EXPIRE (timeout), REVOKE (forced)

**Lease Lifecycle Patterns**:

```
Request:  ACQUIRE (lessee) → GRANT (lessor) | DENY (lessor)
             ↓
Holding:  [lease active with TTL]
             ↓
Extension: RENEW (lessee) → EXTEND (lessor)
             ↓
Termination: RELEASE (lessee)     [voluntary]
         OR: EXPIRE (lessor)       [TTL exhausted]
         OR: REVOKE (lessor)       [forced revocation]
             ↓
Monitoring: PROBE (lessor/lessee) [status check]
```

**Use Cases**:

1. **Leader Election**:
   ```java
   lease.acquire(LESSEE);   // Candidate requests leadership
   lease.grant(LESSOR);     // Authority grants lease
   lease.renew(LESSEE);     // Leader maintains via heartbeat
   lease.extend(LESSOR);    // Authority extends TTL
   lease.release(LESSEE);   // Leader steps down
   ```

2. **Distributed Locking**:
   ```java
   lease.acquire(LESSEE);   // Client requests exclusive lock
   lease.deny(LESSOR);      // Lock held by another
   lease.acquire(LESSEE);   // Retry
   lease.grant(LESSOR);     // Lock acquired
   lease.release(LESSEE);   // Work complete, unlock
   ```

3. **Resource Reservation**:
   ```java
   lease.acquire(LESSEE);   // Reserve resource
   lease.grant(LESSOR);     // Reservation granted
   lease.renew(LESSEE);     // Need more time
   lease.extend(LESSOR);    // Extended
   lease.expire(LESSOR);    // Reservation timeout
   ```

4. **Split-Brain Prevention**:
   ```java
   lease.grant(LESSOR);     // Node A becomes leader
   // Network partition
   lease.expire(LESSOR);    // Node A lease expires (no heartbeat)
   lease.grant(LESSOR);     // Node B becomes new leader
   lease.revoke(LESSOR);    // System detects Node A still claims leadership
   ```

**Performance**: Coordination timescales (milliseconds to seconds), heartbeat-based renewals may be
frequent (~5 seconds)

**Relationship to Other APIs**:

- **Services API**: Service leadership requires lease (GRANT → service PRIMARY)
- **Resources API**: Lease controls resource access (GRANT lease → ACQUIRE resource)
- **Transactions API**: Leases protect transaction resources (EXPIRE → ROLLBACK)
- **Statuses API**: Lease expiration patterns inform conditions (many EXPIRE → DEGRADED)
- **Agents API**: Agents may promise lease availability, depend on lease grants

---

### Counters API

**Purpose**: Semantic signaling for monotonically increasing counters

**Signs**:

- **INCREMENT**: Normal accumulation (expected increase)
- **OVERFLOW**: Boundary violation (exceeded maximum value)
- **RESET**: Intentional zeroing (operator intervention)

**Use Cases**:

- Request counting (total requests processed)
- Event accumulation (errors encountered)
- Throughput tracking

**Performance**: 10M-50M Hz (zero-allocation sign emission)

---

### Gauges API

**Purpose**: Semantic signaling for bidirectional gauges with capacity awareness

**Signs**:

- **INCREMENT**: Resource acquisition, entry, allocation
- **DECREMENT**: Resource release, exit, deallocation
- **OVERFLOW**: Capacity exceeded (saturation or wrapping)
- **UNDERFLOW**: Below minimum (starvation, empty state)
- **RESET**: Return to baseline

**Use Cases**:

- Active connection tracking
- Queue depth monitoring
- Thread pool utilization
- Memory pressure tracking
- Resource capacity planning

**Performance**: 10M-50M Hz (zero-allocation sign emission)

**Relationship to Counters**: Gauges are the bidirectional superset; Counters are the monotonic
subset

---

### Caches API

**Purpose**: Semantic signaling for cache interaction observation

**Signs**:

- **LOOKUP**: An attempt to retrieve an entry from the cache
- **HIT**: A lookup succeeded - entry was found in cache
- **MISS**: A lookup failed - entry was not found in cache
- **STORE**: An entry was added or updated in the cache
- **EVICT**: An entry was automatically removed due to capacity/policy
- **EXPIRE**: An entry was removed due to TTL/expiration
- **REMOVE**: An entry was explicitly invalidated/removed

**Use Cases**:

- Tracking cache effectiveness through hit/miss patterns
- Monitoring cache capacity pressure via eviction frequency
- Detecting staleness issues through expiration patterns
- Understanding cache churn through removal and store frequency

**Performance**: 10M-50M Hz (zero-allocation sign emission)

---

### Probes API

**Purpose**: Structured observation of communication outcomes across distributed boundaries

**Key Concepts**:

- **Outcome**: SUCCESS or FAILURE
- **Origin**: CLIENT (requester) or SERVER (responder)
- **Operation**: CONNECT, SEND, RECEIVE, PROCESS

**Use Cases**:

- Network communication monitoring
- RPC call instrumentation
- Protocol-level diagnostics
- Distributed tracing foundation

---

### Routers API

**Purpose**: Semantic signaling for packet routing operations within network systems

**Signs**:

- **SEND**: A packet was transmitted (originated by this node)
- **RECEIVE**: A packet was received from the network
- **FORWARD**: A packet was forwarded to next hop
- **ROUTE**: A routing decision was made for a packet
- **DROP**: A packet was discarded (congestion, policy, TTL, etc.)
- **FRAGMENT**: A packet was fragmented due to MTU
- **REASSEMBLE**: Packet fragments were reassembled
- **CORRUPT**: Packet corruption was detected (checksum, malformed)
- **REORDER**: Out-of-order packet arrival was detected

**Key Distinctions**:

- **SEND vs FORWARD**: SEND originates packets (source node), FORWARD routes through (intermediary)
- **ROUTE**: Routing decision/table lookup, may precede FORWARD
- **DROP reasons**: Correlate with congestion, policy, TTL, or routing failures

**Use Cases**:

- Monitoring traffic flows through network infrastructure
- Detecting packet loss and corruption patterns
- Observing routing decisions and path changes
- Tracking fragmentation requirements and MTU issues
- Building congestion and performance models

**Performance**: 10M-50M Hz (zero-allocation sign emission)

**Relationship to Other APIs**:

- **Queues API**: Router buffers modeled as queues (packet enqueue/dequeue)
- **Gauges API**: In-flight packets, buffer occupancy tracked via gauges
- **Statuses API**: High drop rates may trigger DEGRADED or DEFECTIVE conditions

---

### Pipelines API

**Purpose**: Semantic signaling for data pipeline stage operations and flow control

**Pipeline Signs** (12 signs):

- **INPUT**: Pipeline received input data from upstream stage
- **OUTPUT**: Pipeline sent output data to downstream stage
- **TRANSFORM**: Pipeline transformed data (map, flatMap, enrich)
- **FILTER**: Pipeline filtered data (removed from flow)
- **AGGREGATE**: Pipeline aggregated data (window, group, reduce)
- **BUFFER**: Pipeline buffered data for flow control
- **BACKPRESSURE**: Pipeline signaled upstream to slow production
- **OVERFLOW**: Pipeline buffer exceeded capacity
- **CHECKPOINT**: Pipeline reached progress checkpoint
- **WATERMARK**: Pipeline advanced event-time watermark
- **LAG**: Pipeline detected processing lag
- **SKIP**: Pipeline skipped/dropped data anomalously

**Key Distinctions**:

- **INPUT vs OUTPUT**: Clear directional flow - input from upstream, output to downstream
- **FILTER vs SKIP**: FILTER is intentional removal, SKIP is anomalous dropping
- **BUFFER vs BACKPRESSURE**: BUFFER absorbs bursts, BACKPRESSURE slows source
- **CHECKPOINT vs WATERMARK**: CHECKPOINT is processing progress, WATERMARK is event-time progress

**Pipeline Patterns**:

- **ETL Flow**: INPUT → TRANSFORM → FILTER → AGGREGATE → OUTPUT
- **Stream Processing**: INPUT → BUFFER → TRANSFORM → BACKPRESSURE → OUTPUT
- **Windowed Aggregation**: INPUT → AGGREGATE → WATERMARK → CHECKPOINT → OUTPUT
- **Overflow Handling**: INPUT → BUFFER → BUFFER → OVERFLOW → SKIP

**Use Cases**:

- ETL/ELT pipeline monitoring
- Stream processing observability (Kafka Streams, Flink, Spark Streaming)
- Batch processing pipeline tracking
- ML pipeline stage monitoring (feature engineering, training)
- Data quality pipeline observation
- Apache Beam, Spring Cloud Stream, Akka Streams instrumentation

**Performance**: 10M-50M Hz (zero-allocation sign emission)

**Relationship to Other APIs**:

- **Queues API**: Pipeline buffers are implemented as queues (BUFFER maps to ENQUEUE)
- **Tasks API**: Pipeline stages may execute as tasks (TRANSFORM may trigger task SUBMIT)
- **Statuses API**: High LAG or OVERFLOW patterns trigger DEGRADED conditions
- **Situations API**: Pipeline bottlenecks escalate to WARNING or CRITICAL situations

---

### Pools API

**Purpose**: Semantic signaling for resource pool capacity management and utilization tracking

**Pool Signs** (4 signs):

- **EXPAND**: Pool capacity increased (resource created, pool size expanded)
- **CONTRACT**: Pool capacity decreased (resource destroyed, pool size contracted)
- **BORROW**: Resource borrowed from pool (utilization increased, availability decreased)
- **RECLAIM**: Resource reclaimed by pool (utilization decreased, availability increased)

**Key Concepts**:

- **Capacity**: Total number of resources the pool can manage (changes via EXPAND/CONTRACT)
- **Utilization**: Number of resources currently borrowed (changes via BORROW/RECLAIM)
- **Availability**: Capacity - Utilization (derived by subscribers)
- **Pool Perspective**: What the pool directly observes about its own state

**Pool Patterns**:

- **Initialization**: EXPAND → EXPAND → EXPAND (creating initial capacity)
- **Healthy Utilization**: BORROW → RECLAIM (balanced resource use)
- **Resource Leak**: BORROW → BORROW → BORROW (no corresponding RECLAIM)
- **Adaptive Scaling**: EXPAND when saturated, CONTRACT when idle
- **Capacity Oscillation**: EXPAND → CONTRACT → EXPAND → CONTRACT (unstable sizing)

**Use Cases**:

- JDBC connection pool monitoring (HikariCP, c3p0, DBCP)
- Thread pool observation (ExecutorService, ForkJoinPool)
- Object pool tracking (Apache Commons Pool)
- Memory pool management (buffer pools, arena allocators)
- Socket/channel pool observation (HTTP clients, gRPC, Netty)

**Performance**: 10K-100K Hz for utilization tracking (BORROW/RECLAIM), low frequency for capacity
changes (EXPAND/CONTRACT)

**Relationship to Other APIs**:

- **Resources API**: Resources manages acquisition requests (ATTEMPT/GRANT/DENY); Pools manages
  capacity and utilization
- **Tasks API**: Task REJECT may correlate with Pool BORROW when pool saturated
- **Statuses API**: Pool patterns (high BORROW rate, leak detection) inform Status conditions (
  SATURATED, LEAKING)
- **Valves API**: Pool capacity changes may trigger Valve adaptations (EXPAND/CONTRACT)

---

### Exchanges API

**Purpose**: Semantic signaling for resource exchanges between parties, supporting both economic
exchange patterns (REA model) and synchronization rendezvous patterns (Java Exchanger)

**Theoretical Foundation**: Based on the **REA (Resource-Event-Agent)** accounting model where
economic activity consists of dual exchanges - every give implies a take (conservation).

**Signs** (2 signs):

| Sign     | Meaning                |
|----------|------------------------|
| CONTRACT | Commit to participate  |
| TRANSFER | Resource changes hands |

**Dimensions** (2 perspectives):

| Dimension | Perspective              |
|-----------|--------------------------|
| PROVIDER  | Giving party perspective |
| RECEIVER  | Taking party perspective |

**Total Signals**: 4 signals (2 signs × 2 dimensions)

**Signal Matrix**:

| Sign     | PROVIDER              | RECEIVER              |
|----------|-----------------------|-----------------------|
| CONTRACT | I contract to provide | I contract to receive |
| TRANSFER | I transfer out        | I receive transfer    |

**Exchange Patterns**:

REA Economic Exchange:

```
CONTRACT × PROVIDER  →  CONTRACT × RECEIVER    (commit to exchange)
TRANSFER × PROVIDER  →  TRANSFER × RECEIVER    (fulfill exchange)
```

Java Exchanger Rendezvous:

```
Thread 1: CONTRACT × PROVIDER  (arrive with data)
Thread 2: CONTRACT × PROVIDER  (arrive with data)
Both:     TRANSFER × PROVIDER, TRANSFER × RECEIVER  (swap)
```

**Use Cases**:

- Trade/order matching systems
- Resource swaps between services
- Thread rendezvous synchronization (Exchanger pattern)
- Value transfer between agents
- Bilateral contract fulfillment

**Performance**: Medium frequency (1K-100K Hz), coordination timescales

**Relationship to Other APIs**:

- **Resources API**: Exchanges transfer resources; Resources tracks acquisition/release
- **Agents API**: Agents make promises; Exchanges fulfill transfers
- **Leases API**: Leases grant time-bounded access; Exchanges transfer ownership
- **Locks API**: Locks provide exclusive access; Exchanges coordinate bilateral transfer

---

### Systems API

**Purpose**: Universal constraint state observation using process control vocabulary

**Design Philosophy**: The Systems API models four fundamental constraints that apply universally to
any system, using process control vocabulary for signs. This provides a constraint-focused layer
between domain-specific APIs and high-level behavioral assessment (Statuses).

**Signs** (4 process control states):

| Sign   | State    | Meaning                                   |
|--------|----------|-------------------------------------------|
| NORMAL | Nominal  | Operating within standard parameters      |
| LIMIT  | Boundary | At constraint boundary, margins thin      |
| ALARM  | Violated | Beyond boundary, attention required       |
| FAULT  | Failed   | Constraint cannot be met, failover needed |

**Dimensions** (4 fundamental constraints):

| Dimension | Constraint | Meaning                                            |
|-----------|------------|----------------------------------------------------|
| SPACE     | Container  | Capacity, volume, room — "Running out of space"    |
| FLOW      | Movement   | Throughput, bandwidth, rate — "Flow is restricted" |
| LINK      | Structural | Connectivity, reachability — "The link is down"    |
| TIME      | Temporal   | Latency, responsiveness — "Time is critical"       |

**Total Signals**: 16 signals (4 signs × 4 dimensions)

**Signal Matrix**:

|            | SPACE           | FLOW              | LINK               | TIME                  |
|------------|-----------------|-------------------|--------------------|-----------------------|
| **NORMAL** | Adequate room   | Steady throughput | Connections stable | Response times normal |
| **LIMIT**  | Running tight   | Flow restricted   | Links saturated    | Timing tight          |
| **ALARM**  | Space critical  | Flow blocked      | Links failing      | Latency critical      |
| **FAULT**  | Space exhausted | Flow stopped      | Links down         | Timeouts total        |

**Use Cases**:

- Constraint-focused observability (which system aspect is stressed?)
- Process control integration (maps to SCADA/PLC vocabulary)
- Cross-domain translation layer (domain signs → constraint state → behavioral status)
- Resource exhaustion detection (FAULT × SPACE = capacity gone)
- Connectivity monitoring (ALARM × LINK = dependencies failing)

**Relationship to Other APIs**:

- **Domain APIs** (Resources, Queues, Services, etc.): Emit operational signs
- **Systems API**: Translates domain patterns into constraint state
- **Statuses API**: Assesses overall behavioral condition with confidence
- **Situations API**: Determines urgency and required response

```
Domain signs → Systems (what constraint?) → Statuses (how behaving?) → Situations (how urgent?)
```

**Performance**: Moderate frequency (1-1000 Hz), observation timescales

---

### Outcomes API (sdk)

**Purpose**: Minimal binary verdict vocabulary for universal success/failure aggregation.

**Design Philosophy**: Outcomes provides the simplest possible verdict vocabulary. As signals
ascend the semiotic hierarchy, they trade specificity for universality. The cause is lost; the
verdict remains. If you need to know *why* something failed, subscribe to the domain API directly.
Outcomes answers one question: **did it work?**

**Signs** (2 verdicts):

| Sign    | Meaning             |
|---------|---------------------|
| SUCCESS | Operation succeeded |
| FAIL    | Operation failed    |

**Total Signals**: 2 (no dimensions)

**Usage Example**:

```java
// Create an outcome instrument
var outcome = circuit
                .conduit(Outcomes::composer)
                .percept(cortex.name("payment.outcomes"));

// Emit verdicts
outcome.

success();  // operation succeeded
outcome.

fail();     // operation failed
```

**Use Cases**:

- Cross-vocabulary success/failure aggregation
- Simple success rate metrics
- Binary health signals for Status translation
- Universal rollups across diverse domain APIs

**Performance**: 10M-50M Hz (zero-allocation sign emission)

**Relationship to Other APIs**:

- **Domain APIs** (Services, Tasks, etc.): Provide rich outcome vocabularies
- **Outcomes**: Aggregates to binary verdict for universal consumption
- **Statuses/Situations**: Consume outcome streams for condition/urgency translation

---

### Operations API (sdk)

**Purpose**: Minimal action bracketing vocabulary for universal span tracking.

**Design Philosophy**: Every action has a start and a finish. Operations captures this universal
pattern. Combined with Outcomes (SUCCESS/FAIL), you get the complete picture:

- BEGIN → END + SUCCESS (action completed successfully)
- BEGIN → END + FAIL (action completed with failure)

**Signs** (2 brackets):

| Sign  | Meaning          |
|-------|------------------|
| BEGIN | Action starting  |
| END   | Action finishing |

**Total Signals**: 2 (no dimensions)

**Usage Example**:

```java
// Create an operation instrument
var operation = circuit
                .conduit(Operations::composer)
                .percept(cortex.name("db.query"));

// Bracket an action
operation.

begin();   // action starting
// ... perform work ...
operation.

end();     // action finished
```

**Use Cases**:

- Cross-vocabulary action span tracking
- Duration measurement (time between BEGIN and END)
- Nesting depth analysis
- Universal action counting

**Performance**: 10M-50M Hz (zero-allocation sign emission)

**Relationship to Other APIs**:

- **Domain APIs** (Services, Tasks, etc.): Provide rich operation vocabularies
- **Operations**: Aggregates to binary bracket for universal span tracking
- **Outcomes**: Complements with verdict - did it work?
- **Together**: "I began X, it ended, and it succeeded/failed"

---

### Trends API (sdk)

**Purpose**: Pattern detection vocabulary for statistical process control.

**Design Philosophy**: We are always watching processes unfold. Trends captures the patterns
detected in time-series observations, based on techniques like Nelson's 8 Rules for SPC.
Combined with Outcomes and Operations, you get the complete observability picture:

- **Operations**: BEGIN/END (when did it run?)
- **Outcomes**: SUCCESS/FAIL (did it work?)
- **Trends**: STABLE/DRIFT/SPIKE/CYCLE/CHAOS (how is it behaving?)

**Signs** (5 patterns):

| Sign   | Nelson Rules | Meaning                             |
|--------|--------------|-------------------------------------|
| STABLE | None         | In control, no significant pattern  |
| DRIFT  | 2, 3, 5, 6   | Sustained movement (shift or trend) |
| SPIKE  | 1            | Sudden extreme deviation (outlier)  |
| CYCLE  | 4            | Oscillating pattern (alternating)   |
| CHAOS  | 7, 8         | Erratic variation (abnormal)        |

**Total Signals**: 5 (no dimensions)

**Usage Example**:

```java
// Create a trend instrument
var trend = circuit
                .conduit(Trends::composer)
                .percept(cortex.name("api.latency"));

// Analyzer detects patterns and emits
trend.

stable();   // process in control
trend.

drift();    // sustained movement detected
trend.

spike();    // outlier detected
```

**Use Cases**:

- Statistical process control (SPC)
- Anomaly detection in time-series
- Trend analysis for capacity planning
- Pattern-based alerting

**Performance**: 10M-50M Hz (zero-allocation sign emission)

**Relationship to Other APIs**:

The semiotic flow:

```
Measurements → Trends (what pattern?) → Statuses (what condition?) → Situations (how urgent?)
```

- **Domain APIs**: Provide raw measurements/signals
- **Trends**: Pattern detection vocabulary
- **Statuses**: Condition assessment (STABLE, DEGRADED, etc.)
- **Situations**: Urgency classification (NORMAL, WARNING, CRITICAL)

---

### Cycles API (sdk/meta)

**Purpose**: Meta-level observation of sign recurrence patterns within signal streams

**Design Philosophy**: The Cycles API is the first **meta-level analytical vocabulary** in the
`sdk/meta` package. Unlike domain APIs that define their own signs, meta APIs are **generic over any
Sign type** and define only Dimensions. They provide vocabulary for expressing analytical
observations about patterns in sign streams from other APIs.

**Key Distinction**: Meta APIs don't define signs - they borrow signs from source APIs and add
analytical dimensions. The Sign comes from whatever API is being observed (Resources, Tasks, Gauges,
etc.); the Dimension describes the analytical observation (recurrence pattern).

**Dimensions** (3 recurrence patterns):

| Dimension | Pattern       | Meaning                                     |
|-----------|---------------|---------------------------------------------|
| SINGLE    | First seen    | First occurrence of this sign in the stream |
| REPEAT    | Consecutive   | Same sign as immediately previous emission  |
| RETURN    | Non-immediate | Seen before, but not immediately previous   |

**Total Signals**: N × 3 where N is the number of signs in the source API

**Generic Signal**: `Signal<S extends Sign>` carries any sign type with a Cycles dimension

**Usage Example**:

```java
// Create a cycle observer for Resources.Sign
var cycle = circuit
                .conduit(Cycles.composer(Resources.Sign.class))
                .percept(cortex.name("db.pool.cycles"));

// Stream: GRANT, GRANT, DENY, GRANT
cycle.

signal(GRANT, SINGLE);    // First GRANT
cycle.

signal(GRANT, REPEAT);    // GRANT immediately after GRANT
cycle.

signal(DENY, SINGLE);     // First DENY
cycle.

signal(GRANT, RETURN);    // GRANT returns after DENY
```

**Use Cases**:

- Detecting consecutive sign repetition (burst patterns, stuck states)
- Identifying sign recurrence after gaps (cyclic behavior)
- Tracking first-time vs returning signs (novelty detection)
- Building higher-order pattern analyzers (repetition → frequency → trend)

**Performance**: Moderate frequency (observation timescales), zero-allocation via SignalSet caching

**Relationship to Other APIs**:

- **Domain APIs** (Resources, Tasks, Gauges, etc.): Provide source signs
- **Cycles API**: Observes recurrence patterns in sign streams
- **Future Meta APIs**: Frequencies, Periodicities, Patterns (planned)
- **Universal APIs** (Statuses, Situations): May consume meta observations

```
Domain signs → Meta observations (Cycles) → Universal vocabularies → Actions
```

---

### Surveys API (sdk)

**Purpose**: Collective assessment agreement vocabulary for expressing consensus outcomes.

**Design Philosophy**: While Statuses expresses individual assessment with confidence (how sure
am I?), Surveys expresses collective assessment with agreement (how much do we agree?). These
are complementary dimensions of observability. Like Cycles, Surveys is **generic over any Sign
type** - the Sign comes from the source API being surveyed (typically Statuses.Sign), and
Surveys defines only the Dimension describing the agreement level.

**Dimensions** (3 agreement levels):

| Dimension | Agreement Level | Meaning                                    |
|-----------|-----------------|--------------------------------------------|
| DIVIDED   | No majority     | Observers split with no clear consensus    |
| MAJORITY  | Partial         | Clear majority but not complete agreement  |
| UNANIMOUS | Complete        | All observers agree on the same assessment |

**Total Signals**: N × 3 where N is the number of signs in the source API

**Generic Signal**: `Signal<S extends Sign>` carries any sign type with a Surveys dimension

**Usage Example**:

```java
// Create a survey instrument for Status signs
var survey = circuit
    .conduit(Surveys.composer(Statuses.Sign.class))
    .percept(cortex.name("cluster.health"));

// After polling cluster members:
survey.signal(DEGRADED, MAJORITY);   // 4 of 5 nodes say DEGRADED
survey.signal(STABLE, UNANIMOUS);    // All nodes agree STABLE
survey.signal(DIVERGING, DIVIDED);   // Observers split on assessment
```

**Use Cases**:

- Cluster health consensus reporting
- Distributed status agreement tracking
- Multi-observer voting outcomes
- Quorum-based decision observation
- Collective assessment pattern analysis

**Performance**: Moderate frequency (observation timescales), zero-allocation via SignalSet caching

**Relationship to Other APIs**:

- **Statuses API**: Individual assessment with confidence; Surveys adds collective agreement
- **Cycles API**: Both are generic over source Sign types
- **Situations API**: May consume survey signals to determine urgency based on consensus
- **Domain APIs**: Provide source signs that get surveyed

```
Individual: Status (what I assess) × Confidence (how sure I am)
Collective: Survey (what we assess) × Agreement (how much we agree)
```

---

### Statuses API

**Purpose**: Objective assessment of operational conditions based on signal pattern analysis

**Operational Conditions** (7 states):

| Condition  | Stability | Meaning                                    |
|------------|-----------|--------------------------------------------|
| CONVERGING | Improving | Stabilizing toward reliable operation      |
| STABLE     | Nominal   | Operating within expected parameters       |
| DIVERGING  | Degrading | Destabilizing with increasing variations   |
| ERRATIC    | Chaotic   | Unpredictable behavior, irregular patterns |
| DEGRADED   | Impaired  | Reduced performance, elevated errors       |
| DEFECTIVE  | Failing   | Predominantly failed operations            |
| DOWN       | Failed    | Entirely non-operational                   |

**Confidence Levels**: TENTATIVE → MEASURED → CONFIRMED

**Use Cases**:

- Service health dashboards
- Anomaly detection
- Adaptive circuit breakers
- Capacity planning alerts

---

### Services API

**Purpose**: Rich semantic signaling for service lifecycle, coordination, and work execution

**Dual-Orientation Model**:

- **RELEASE**: Self-perspective ("I am doing this now") - present tense
- **RECEIPT**: Other-perspective ("I observed that happened") - past tense

**Use Cases**:

- Distributed tracing
- Request lifecycle tracking
- Coordination pattern observation
- Failure mode detection

---

### Resources API

**Purpose**: Semantic signaling for resource acquisition and release patterns in bounded capacity
systems

**Resource Signs** (6 signs):

| Sign        | Category | Description                                    |
|-------------|----------|------------------------------------------------|
| **ATTEMPT** | Request  | Non-blocking acquisition request (try-acquire) |
| **ACQUIRE** | Request  | Blocking acquisition request (willing to wait) |
| **GRANT**   | Outcome  | Resource successfully obtained                 |
| **DENY**    | Outcome  | Non-blocking request denied (not available)    |
| **TIMEOUT** | Outcome  | Blocking request exceeded time limit           |
| **RELEASE** | Return   | Resource returned to pool                      |

**Resource Interaction Patterns**:

| Pattern      | Request Sign | Success Sign | Failure Signs | Typical Use Case         |
|--------------|--------------|--------------|---------------|--------------------------|
| Non-blocking | ATTEMPT      | GRANT        | DENY          | Try-acquire, fast-fail   |
| Blocking     | ACQUIRE      | GRANT        | TIMEOUT       | Wait-acquire, guaranteed |
| Release      | RELEASE      | -            | -             | Return units to pool     |

**Key Concepts**:

- **Resource**: Named entity with bounded capacity that can be requested, granted, and released
- **Requester perspective**: ATTEMPT, ACQUIRE, RELEASE (what I'm doing)
- **Provider perspective**: GRANT, DENY, TIMEOUT (what happened to the request)

**Use Cases**:

- Connection pool monitoring (JDBC, HTTP clients)
- Semaphore permit tracking
- Rate limiter observation
- Thread pool saturation tracking
- Quota enforcement and capacity planning

**Performance**: 10M-50M Hz (zero-allocation sign emission)

**Relationship to Other APIs**:

- **Locks API**: Locks ARE resources - vocabulary aligned (ATTEMPT, ACQUIRE, GRANT, DENY, TIMEOUT,
  RELEASE)
- **Latches API**: Latches ARE coordination resources - vocabulary aligned (AWAIT, RELEASE, TIMEOUT)
- **Pools API**: Pools manages capacity/utilization; Resources manages acquisition requests
- **Statuses API**: High DENY or TIMEOUT rates may indicate DEGRADED or DEFECTIVE conditions
- **Services API**: Resource exhaustion can trigger DELAY, REJECT, or FAIL service signals

---

### Locks API

**Purpose**: Mutual exclusion and synchronization primitive observation for concurrent systems

**Design Philosophy**: Locks ARE resources - vocabulary aligned with Resources API (ATTEMPT,
ACQUIRE, GRANT, DENY, TIMEOUT, RELEASE) with lock-specific extensions (UPGRADE, DOWNGRADE, CONTEST,
ABANDON)

**Lock Signs** (10 signs):

| Sign      | Category     | Description                             | Source        |
|-----------|--------------|-----------------------------------------|---------------|
| ATTEMPT   | Acquisition  | Non-blocking lock request (try-lock)    | Resources API |
| ACQUIRE   | Acquisition  | Blocking lock request (willing to wait) | Resources API |
| GRANT     | Acquisition  | Lock successfully obtained              | Resources API |
| DENY      | Acquisition  | Try-lock failed immediately             | Resources API |
| TIMEOUT   | Acquisition  | Timed acquisition exceeded limit        | Resources API |
| RELEASE   | Release      | Lock voluntarily released               | Resources API |
| UPGRADE   | Modification | Read lock → write lock (RW locks)       | Lock-specific |
| DOWNGRADE | Modification | Write lock → read lock (RW locks)       | Lock-specific |
| CONTEST   | Contention   | CAS failed / contention detected        | Lock-specific |
| ABANDON   | Release      | Lock holder crashed without release     | Lock-specific |

**Key Lock Patterns**:

- **Try-Lock (Non-blocking)**: ATTEMPT → GRANT → RELEASE or ATTEMPT → DENY
- **Blocking Lock**: ACQUIRE → GRANT → RELEASE
- **Timed Lock**: ACQUIRE → TIMEOUT or ACQUIRE → GRANT → RELEASE
- **CAS-based Optimistic**: ATTEMPT → CONTEST → CONTEST → GRANT (after retries)
- **Read-Write Lock**: Multiple GRANT (shared read) → Writer CONTEST (blocked) → GRANT (exclusive)
- **Lock Upgrade**: GRANT (read) → UPGRADE → DOWNGRADE → RELEASE
- **Abandoned Lock**: ACQUIRE → GRANT → ABANDON (crash/thread death)

**Use Cases**:

- Mutex synchronization monitoring (blocking locks)
- Try-lock pattern observation (non-blocking)
- Read-write lock coordination tracking (shared vs exclusive access)
- CAS-based optimistic locking (contention detection)
- Semaphore permit tracking (counting semaphores)
- Distributed lock coordination (etcd, Zookeeper, Redis)
- Deadlock detection (circular dependency analysis via blocked ACQUIREs)
- Contention hotspot identification (high CONTEST/DENY rates)
- Lock hold time analysis (GRANT to RELEASE duration)
- Crash detection (ABANDON signals indicate holder termination)

**Performance**: Synchronization timescales (nanoseconds to milliseconds), 10M-50M Hz emission
capacity

**Relationship to Other APIs**:

- **Resources API**: Locks ARE resources (vocabulary aligned: ATTEMPT, ACQUIRE, GRANT, DENY,
  TIMEOUT, RELEASE)
- **Leases API**: Distributed locks often backed by leases (lease EXPIRE → lock ABANDON)
- **Transactions API**: Locks provide isolation (transaction START → lock ACQUIRE/GRANT)
- **Tasks API**: Tasks acquire locks during execution (task START → lock ATTEMPT/ACQUIRE)
- **Statuses API**: Lock contention informs conditions (many CONTEST → DEGRADED)
- **Services API**: Lock availability affects service capacity (lock TIMEOUT → service DELAY)

---

### Atomics API

**Purpose**: CAS (Compare-And-Swap) contention dynamics observation for lock-free and wait-free
algorithms

**Design Philosophy**: While the Locks API covers CAS as one acquisition mechanism (via CONTEST),
Atomics focuses specifically on **contention management behaviors**. Locks is about ownership
(GRANT → RELEASE). Atomics is about contention dynamics (FAIL → SPIN → BACKOFF → SUCCESS).
These APIs complement each other: CAS-based lock acquisition can emit both Atomics signs
(for contention dynamics) and Locks signs (for ownership semantics).

**Atomics Signs** (8 signs):

| Sign    | Category   | Description                          |
|---------|------------|--------------------------------------|
| ATTEMPT | Operation  | CAS operation initiated              |
| SUCCESS | Outcome    | CAS succeeded                        |
| FAIL    | Contention | CAS failed (contention detected)     |
| SPIN    | Retry      | Busy-wait retry loop                 |
| YIELD   | Retry      | Thread.yield() scheduling hint       |
| BACKOFF | Retry      | Deliberate delay applied             |
| PARK    | Escalation | Transition from spinning to blocking |
| EXHAUST | Escalation | Retry budget exceeded, giving up     |

**Key Contention Patterns**:

- **Simple CAS Success**: ATTEMPT → SUCCESS
- **CAS with Spin Retry**: ATTEMPT → FAIL → SPIN → FAIL → SPIN → SUCCESS
- **CAS with Yield**: ATTEMPT → FAIL → SPIN → FAIL → YIELD → FAIL → SUCCESS
- **CAS with Adaptive Backoff**: ATTEMPT → FAIL → SPIN → FAIL → BACKOFF → FAIL → BACKOFF → SUCCESS
- **Spin-to-Park Escalation**: ATTEMPT → FAIL → SPIN → FAIL → SPIN → FAIL → PARK → SUCCESS
- **Retry Budget Exhaustion**: ATTEMPT → FAIL → SPIN → FAIL → BACKOFF → FAIL → EXHAUST

**Use Cases**:

- Lock-free data structure monitoring (Treiber stack, Michael-Scott queue)
- Spin lock contention analysis
- Adaptive spinning effectiveness measurement
- Hybrid lock behavior (spin-then-park) observation
- CAS-based counter/reference update tracking
- Contention hotspot identification (high FAIL rates)
- Backoff strategy tuning (BACKOFF effectiveness)
- Retry budget analysis (EXHAUST frequency)

**Performance**: Nanosecond timescales, 10M-50M Hz emission capacity

**Relationship to Other APIs**:

- **Locks API**: Complementary - Atomics tracks contention dynamics, Locks tracks ownership
  (CAS-based lock: Atomics FAIL → SPIN → SUCCESS, then Locks GRANT)
- **Resources API**: Atomic operations on pool availability counters
- **Queues API**: Lock-free queue implementations (CAS on head/tail pointers)
- **Statuses API**: Contention patterns inform conditions:
    - High FAIL/ATTEMPT ratio → SATURATED status
    - Frequent PARK → DEGRADED status
    - EXHAUST signals → OVERLOADED status

---

### Latches API

**Purpose**: Coordination barrier observation for thread synchronization in concurrent systems

**Design Philosophy**: Latches are coordination primitives. Vocabulary aligned with Resources API
where
appropriate (AWAIT, RELEASE, TIMEOUT) with latch-specific extensions (ARRIVE, RESET, ABANDON)

**Latch Signs** (6 signs):

| Sign    | Category  | Description                              |
|---------|-----------|------------------------------------------|
| AWAIT   | Operation | Thread waiting at barrier (blocking)     |
| ARRIVE  | Operation | Thread reached barrier / count decrement |
| RELEASE | Outcome   | Barrier satisfied, threads unblocked     |
| TIMEOUT | Error     | Wait exceeded time limit                 |
| RESET   | Operation | Barrier reset for next phase (cyclic)    |
| ABANDON | Error     | Participant terminated without arriving  |

**Key Latch Patterns**:

- **CountDownLatch**: AWAIT (threads block) → ARRIVE (count decrements) → RELEASE (barrier
  satisfied)
- **CyclicBarrier**: AWAIT → AWAIT → AWAIT (all arrive) → RELEASE → RESET (ready for next phase)
- **Phaser**: ARRIVE (dynamic participants) → AWAIT → RELEASE → RESET
- **Start Signal**: AWAIT (workers wait) → ARRIVE (coordinator signals) → RELEASE (all start)
- **Timed Wait**: AWAIT → TIMEOUT (condition not met within deadline)

**Use Cases**:

- CountDownLatch coordination monitoring (startup synchronization, completion aggregation)
- CyclicBarrier phase coordination tracking (multi-phase algorithms, parallel pipelines)
- Phaser dynamic participant observation (fork-join patterns with variable participants)
- Barrier duration analysis (time from first AWAIT to RELEASE)
- Coordination failure detection (TIMEOUT, ABANDON patterns)
- Phase transition tracking (RESET frequency and inter-phase duration)

**Performance**: Coordination timescales (microseconds to milliseconds), 10M-50M Hz emission
capacity

**Relationship to Other APIs**:

- **Locks API**: Latches complement locks - locks provide mutual exclusion, latches provide
  coordination barriers
- **Resources API**: Latches ARE coordination resources - vocabulary aligned (AWAIT, RELEASE,
  TIMEOUT)
- **Leases API**: Distributed latches may use leases for participant tracking (lease EXPIRE → latch
  ABANDON)
- **Tasks API**: Task groups use latches for completion coordination (all tasks complete → latch
  RELEASE)
- **Processes API**: Process coordination may use latches (all processes START → latch RELEASE)
- **Statuses API**: Latch timeout patterns inform conditions (many TIMEOUT → DEGRADED)

---

### Tasks API

**Purpose**: Asynchronous work unit lifecycle observation for concurrent and distributed systems

**Task Lifecycle Signs** (11 signs):

| Sign     | Category   | Description                                   |
|----------|------------|-----------------------------------------------|
| SUBMIT   | Submission | Task submitted to executor/queue              |
| REJECT   | Submission | Task submission rejected (queue full, policy) |
| SCHEDULE | Submission | Task scheduled for execution (dequeued)       |
| START    | Execution  | Task execution begins                         |
| PROGRESS | Execution  | Task reports progress (long-running)          |
| SUSPEND  | Execution  | Task paused/suspended                         |
| RESUME   | Execution  | Task resumed after suspension                 |
| COMPLETE | Completion | Task completed successfully                   |
| FAIL     | Completion | Task failed with error                        |
| CANCEL   | Completion | Task cancelled                                |
| TIMEOUT  | Completion | Task exceeded execution time budget           |

**Key Lifecycle Patterns**:

- **Successful Execution**: SUBMIT → SCHEDULE → START → COMPLETE
- **Failed Execution**: SUBMIT → SCHEDULE → START → FAIL
- **Long-Running with Progress**: SUBMIT → SCHEDULE → START → PROGRESS → PROGRESS → COMPLETE
- **Rejected Submission**: SUBMIT → REJECT
- **Cancelled Task**: SUBMIT → SCHEDULE → CANCEL or SUBMIT → SCHEDULE → START → CANCEL
- **Task Timeout**: SUBMIT → SCHEDULE → START → TIMEOUT
- **Suspended/Resumed**: SUBMIT → SCHEDULE → START → SUSPEND → RESUME → COMPLETE

**Use Cases**:

- Thread pool execution monitoring
- Async work queue analysis (queue depth, wait times)
- Task throughput and latency measurement (SUBMIT to COMPLETE)
- Failure rate tracking (FAIL ratio)
- Backpressure detection (REJECT frequency)
- Long-running task progress monitoring
- Actor message processing observation
- Reactive stream backpressure signaling
- Distributed task coordination tracking

**Performance**: Computational timescales (microseconds to seconds), 10M-50M Hz emission capacity

**Relationship to Other APIs**:

- **Queues API**: Tasks wait in queues (queue ENQUEUE → task SUBMIT, queue DEQUEUE → task SCHEDULE)
- **Resources API**: Tasks consume resources (resource GRANT → task START, resource DENY → task
  SUSPEND)
- **Services API**: Tasks implement service operations (service REQUEST → task SUBMIT)
- **Statuses API**: Task failure patterns inform conditions (many FAIL → DEGRADED)
- **Situations API**: Queue depth explosion + REJECT → CAPACITY situation (scale out needed)

---

### Timers API

**Purpose**: Time constraint outcome observation for deadline and threshold compliance

**Design Philosophy**: Timers provides a qualitative vocabulary for reporting whether execution
met or missed time constraints. Inspired by Apdex, the API focuses on binary outcomes (MEET/MISS)
rather than quantitative durations. The implementer determines the constraint; the API provides
the vocabulary for reporting the outcome. Pattern recognition and threshold interpretation happen
in subscribers, not at the emission point.

**Dual-Dimension Model**:

| Dimension     | Constraint Type | Description                              |
|---------------|-----------------|------------------------------------------|
| **DEADLINE**  | Absolute        | Specific point in time ("complete by X") |
| **THRESHOLD** | Relative        | Duration/performance target ("within X") |

**The 2 Timer Signs**:

| Sign | Category | Description               |
|------|----------|---------------------------|
| MEET | Outcome  | Time constraint satisfied |
| MISS | Outcome  | Time constraint violated  |

**Total Signals**: 4 signals (2 signs × 2 dimensions)

**Signal Matrix**:

|      | DEADLINE                  | THRESHOLD                   |
|------|---------------------------|-----------------------------|
| MEET | Completed before deadline | Completed within target     |
| MISS | Completed after deadline  | Exceeded performance target |

**Key Concepts**:

- **Timer**: Observable instrument for reporting time constraint outcomes
- **Sign**: The outcome of time constraint evaluation (MEET or MISS)
- **Dimension**: The type of time constraint (DEADLINE or THRESHOLD)
- **Qualitative**: No durations or timestamps in the API - binary classification only

**Timer Patterns**:

- **Performance Threshold (Apdex-style)**:
  ```java
  if (elapsed <= targetNanos) {
    timer.meet(THRESHOLD);    // Within performance target
  } else {
    timer.miss(THRESHOLD);    // Exceeded performance target
  }
  ```

- **Absolute Deadline**:
  ```java
  if (Instant.now().isBefore(deadline)) {
    timer.meet(DEADLINE);     // Completed before deadline
  } else {
    timer.miss(DEADLINE);     // Missed the deadline
  }
  ```

**Use Cases**:

- SLA compliance monitoring (latency thresholds)
- Deadline tracking for scheduled tasks
- Apdex-style performance classification
- Batch processing deadline compliance
- Request timeout analysis

**Performance**: Completion timescales (microseconds to hours depending on use case),
zero-allocation
SignalSet caching

**Relationship to Other APIs**:

- **Tasks API**: Task completion may emit timer signals (task COMPLETE → timer MEET/MISS THRESHOLD)
- **Services API**: Service calls have SLAs (call completes → timer MEET/MISS THRESHOLD)
- **Transactions API**: Transactions may have time limits (commit → timer MEET/MISS DEADLINE)
- **Statuses API**: Timer patterns inform status (many MISS → DEGRADED)

**Subscriber-Side Interpretation**:

```
MISS rate on THRESHOLD > 15%  → Translate to DEGRADED status
MISS rate on DEADLINE > 5%    → Translate to OVERLOADED situation
Sudden spike in MISS signals  → Trigger capacity alert
```

**Relationship to TIMEOUT and EXPIRE**:

- **TIMEOUT** (other APIs): Operation never completed (gave up waiting)
- **EXPIRE** (other APIs): Time budget exhausted during execution
- **MEET/MISS** (Timers): Operation completed, classifying whether it met the constraint

TIMEOUT and EXPIRE indicate failure to complete; MEET/MISS classify completions.

---

### Processes API

**Purpose**: OS-level process lifecycle observation for systems managing operating system processes

**Process Lifecycle Signs** (9 signs):

| Sign    | Category    | Description                                |
|---------|-------------|--------------------------------------------|
| SPAWN   | Creation    | New process created (fork/exec)            |
| START   | Execution   | Process execution begins                   |
| STOP    | Termination | Process stopped cleanly (exit 0)           |
| FAIL    | Termination | Process failed (non-zero exit code)        |
| CRASH   | Termination | Process crashed (segfault, panic)          |
| KILL    | Termination | Process forcibly terminated (SIGKILL)      |
| RESTART | Recovery    | Process being restarted by supervisor      |
| SUSPEND | Suspension  | Process paused (SIGSTOP, container pause)  |
| RESUME  | Suspension  | Process resumed after suspension (SIGCONT) |

**Key Lifecycle Patterns**:

- **Successful Execution**: SPAWN → START → STOP
- **Failed Execution**: SPAWN → START → FAIL
- **Crashed with Restart**: SPAWN → START → CRASH → RESTART → START → STOP
- **Forcible Termination**: START → KILL
- **Suspended Process**: START → SUSPEND → RESUME → STOP

**Use Cases**:

- Process supervisor monitoring (systemd, init systems)
- Container lifecycle tracking (Docker, Kubernetes pods)
- Daemon management observation
- Process stability analysis (crash rates, restart patterns)
- Supervision effectiveness measurement (restart latency, success rates)
- OS-level resource management tracking

**Performance**: System/coordination timescales (milliseconds to seconds), infrequent compared to
in-process operations

**Relationship to Other APIs**:

- **Tasks API**: Processes contain tasks (process START → task SUBMIT)
- **Services API**: Processes host services (process CRASH → service FAIL/DISCONNECT)
- **Resources API**: Process creation requires resources (resource GRANT → process SPAWN)
- **Statuses API**: Process crash patterns inform conditions (many CRASH → DEGRADED)
- **Situations API**: Process instability influences situations (crash loop → CRITICAL)
- **Locks API**: Process termination may abandon locks (process CRASH → lock ABANDON)

---

### Queues API

**Purpose**: Semantic signaling for queue operations and boundary conditions in flow-control systems

**Queue Signs** (4 signs):

| Sign          | Category  | Description                              |
|---------------|-----------|------------------------------------------|
| **ENQUEUE**   | Operation | Item successfully added to the queue     |
| **DEQUEUE**   | Operation | Item successfully removed from the queue |
| **OVERFLOW**  | Boundary  | Enqueue failed due to queue at capacity  |
| **UNDERFLOW** | Boundary  | Dequeue failed due to empty queue        |

**Key Concepts**:

- **Queue**: Named subject that emits signs describing operations performed against it
- **Sign**: Enumeration of distinct interaction types
- **FIFO Ordering**: First-In-First-Out semantics (distinguishes from Stacks LIFO)

**Queue Patterns**:

- **Healthy Flow**: ENQUEUE → DEQUEUE (balanced producer-consumer)
- **Backpressure**: ENQUEUE → OVERFLOW (producer outpacing consumer)
- **Starvation**: DEQUEUE → UNDERFLOW (consumer outpacing producer)
- **Burst Absorption**: ENQUEUE → ENQUEUE → ENQUEUE → DEQUEUE (queue buffering burst)

**Use Cases**:

- Modeling traffic flow in bounded queues or backpressure systems
- Instrumenting messaging systems, task queues, or pipelines
- Diagnosing latency and load in producer-consumer patterns
- Building higher-level abstractions such as buffers, schedulers, or mailboxes

**Performance**: 10M-50M Hz (zero-allocation sign emission)

**Relationship to Other APIs**:

- **Stacks API**: Complementary buffer abstraction (FIFO vs LIFO ordering)
- **Resources API**: Queue capacity can be modeled as a resource with GRANT/DENY signals
- **Statuses API**: Queue overflow patterns may indicate DEGRADED or DIVERGING conditions
- **Services API**: Queue operations can trigger DELAY or REJECT service signals

---

### Stacks API

**Purpose**: Semantic signaling for stack operations and boundary conditions in LIFO systems

**Stack Signs** (4 signs):

| Sign          | Category  | Description                                    |
|---------------|-----------|------------------------------------------------|
| **PUSH**      | Operation | Item successfully added to the stack (top)     |
| **POP**       | Operation | Item successfully removed from the stack (top) |
| **OVERFLOW**  | Boundary  | Push failed due to stack at capacity           |
| **UNDERFLOW** | Boundary  | Pop failed due to empty stack                  |

**Key Concepts**:

- **Stack**: Named subject that emits signs describing operations performed against it
- **Sign**: Enumeration of distinct interaction types
- **LIFO Ordering**: Last-In-First-Out semantics (distinguishes from Queues FIFO)

**Stack Patterns**:

- **Call Stack**: PUSH → PUSH → PUSH → POP → POP → POP (nested calls)
- **Undo Buffer**: PUSH (action) → POP (undo) → PUSH (redo)
- **Recursion Overflow**: PUSH → PUSH → ... → OVERFLOW (unbounded recursion)
- **Bracket Matching**: PUSH (open) → POP (close match) or UNDERFLOW (unmatched close)

**Use Cases**:

- Call stack monitoring (recursion depth, stack overflow detection)
- Undo/redo buffer observation in editors
- Parser state stacks (bracket matching, expression evaluation)
- Backtracking algorithms (search state stack)
- Work-stealing deques (stack mode for cache locality)
- RPN calculator stacks (operand management)

**Performance**: 10M-50M Hz (zero-allocation sign emission)

**Relationship to Other APIs**:

- **Queues API**: Complementary buffer abstraction (LIFO vs FIFO ordering)
- **Processes API**: Call stack depth relates to process execution depth
- **Tasks API**: Task execution stack for nested task spawning
- **Statuses API**: Stack overflow patterns may indicate DEGRADED or RECURSIVE conditions
- **Gauges API**: Stack depth can be modeled as a gauge (PUSH=increment, POP=decrement)

**Semiotic Ascent (Stacks → Status → Situation)**:

| Stack Pattern         | Status    | Situation |
|-----------------------|-----------|-----------|
| High PUSH without POP | DEEP      | RECURSION |
| Many OVERFLOW         | SATURATED | CAPACITY  |
| Many UNDERFLOW        | STARVED   | THRASHING |
| Balanced PUSH/POP     | STABLE    | NORMAL    |

---

### Sensors API

**Purpose**: Positional measurement relative to configured reference values (baselines, thresholds,
targets)

**Positional Signs** (3 signs):

| Sign    | Position       | Meaning                                  |
|---------|----------------|------------------------------------------|
| BELOW   | Under setpoint | Operating below the reference value      |
| NOMINAL | At setpoint    | Operating at or near the reference value |
| ABOVE   | Over setpoint  | Operating above the reference value      |

**Setpoint Types** (3 dimensions):

| Dimension | Type         | Meaning                                    |
|-----------|--------------|--------------------------------------------|
| BASELINE  | Normal level | Expected operating point under normal load |
| THRESHOLD | Limit        | Boundary that should not be exceeded       |
| TARGET    | Ideal goal   | Optimal or desired operating point         |

**Total Signals**: 9 signals (3 signs × 3 dimensions)

**Key Concepts**:

- **Setpoint**: A configured reference value for comparison
- **Position**: Where a measured value stands relative to a setpoint
- **Signal**: Composition of position (sign) and setpoint type (dimension)
- **Measurement**: Comparing observed values to reference points

**Measurement Patterns**:

- **Queue Depth Example**: baseline=100, threshold=1000
    - Depth 50: BELOW × BASELINE, BELOW × THRESHOLD (light load)
    - Depth 850: ABOVE × BASELINE, BELOW × THRESHOLD (busy but safe)
    - Depth 1200: ABOVE × BASELINE, ABOVE × THRESHOLD (overflow!)

- **Latency Example**: target=20ms, baseline=50ms, threshold=500ms
    - Latency 15ms: BELOW × TARGET, BELOW × BASELINE (excellent)
    - Latency 450ms: ABOVE × BASELINE, BELOW × THRESHOLD (approaching limit)

- **Error Rate Example**: target=0%, baseline=1%, threshold=5%
    - Rate 0.5%: ABOVE × TARGET, BELOW × BASELINE (acceptable)
    - Rate 7%: ABOVE × THRESHOLD (exceeded limit!)

**Use Cases**:

- Metric position reporting relative to SLO thresholds
- Queue depth monitoring against baseline and capacity
- Latency tracking relative to target and acceptable limits
- Error rate measurement against operational boundaries
- Resource utilization relative to scaling thresholds
- Foundational layer for monitor condition assessment

**Performance**: Moderate frequency (10-10000 Hz typical), metrics-based measurements

**Relationship to Other APIs**:

- **Domain APIs** (Counters, Gauges, etc.): Provide raw operational signals
- **Sensors API**: Translates metrics into positional measurements
- **Statuses API**: Uses setpoint positions to assess operational conditions
- **Situations API**: Escalates sustained threshold violations to urgency levels
- Forms measurement foundation: Metrics → Sensors (positions) → Statuses (assessments) →
  Situations (urgency)

---

### Situations API

**Purpose**: Situational significance assessment with operational urgency classification

**Signs** (3 significance levels):

| Sign     | Significance | Meaning                                     |
|----------|--------------|---------------------------------------------|
| NORMAL   | Nominal      | Routine operation, no intervention required |
| WARNING  | Elevated     | Attention needed, monitor closely           |
| CRITICAL | Urgent       | Immediate action required                   |

**Use Cases**:

- Alert routing and escalation
- Incident management
- On-call notification
- SLA violation handling

## Observability Flow

### Layered Metrics Architecture

Serventis provides a **complete metrics stack** from raw accumulation to situational assessment:

```
┌─────────────────────────────────────────────────────────┐
│  Actions (automated responses)                          │
└─────────────────────────────────────────────────────────┘
                     ▲
┌─────────────────────────────────────────────────────────┐
│  Situations (urgency assessment)                        │
│  - NORMAL, WARNING, CRITICAL                            │
└─────────────────────────────────────────────────────────┘
                     ▲
┌─────────────────────────────────────────────────────────┐
│  Statuses (behavioral condition)                        │
│  - STABLE, DEGRADED, ERRATIC, DOWN                      │
└─────────────────────────────────────────────────────────┘
                     ▲
┌─────────────────────────────────────────────────────────┐
│  Systems (constraint state)                             │
│  - NORMAL, LIMIT, ALARM, FAULT × SPACE, FLOW, LINK, TIME│
└─────────────────────────────────────────────────────────┘
                     ▲
┌─────────────────────────────────────────────────────────┐
│  Observer Agents (signal aggregation)                   │
│  - Rate calculations, threshold detection               │
└─────────────────────────────────────────────────────────┘
                     ▲
┌─────────────────────────────────────────────────────────┐
│  Raw Signs (instruments)                                │
│  - Counters (monotonic), Gauges (bidirectional)         │
│  - Caches (interactions), Probes (outcomes)             │
│  - Routers (packets), Pipelines (data processing)       │
│  - Services (lifecycle), Resources, Locks, Atomics, Tasks│
│  - Queues, Actors (dialogue), Agents (promises)         │
│  - Transactions (coordination), Breakers (resilience)   │
│  - Leases (time-bounded access), Latches, Processes     │
│  - Flows (stage transitions), Valves (flow control)     │
└─────────────────────────────────────────────────────────┘
```

### Complete Example: HTTP Service Monitoring

## Integration with Substrates

Serventis is built on Substrates primitives with type-safe Percept constraints:

- **Composers**: `Composer<E, P extends Percept>` - Create domain-specific percepts (Probe, Monitor,
  Service, etc.)
- **Conduits**: `Conduit<P extends Percept, E>` - Pool percepts by name with type safety
- **Circuits**: `Circuit.conduit<P extends Percept, E>()` - Create conduits with percept constraints
- **Channels**: Named signal sources organized hierarchically (factories for pipes)
- **Pipes**: `Pipe<E> extends Percept` - Primitive emission mechanism
- **Subscribers**: React to signals with aggregation logic

All signal emission leverages Substrates' **recursive emission ordering** to ensure cascading
effects complete atomically before external observations.

**Type Safety**: The framework enforces that composers return percepts, ensuring intentionality
in instrument design. Channels create pipes (which are percepts), but channels themselves are not
percepts - they're just plumbing. This architectural distinction maintains clear separation between
emission mechanisms (pipes) and semantic facades (instruments).

## Performance Characteristics

- **High-frequency counters/gauges**: 10M-50M Hz (zero-allocation sign emission)
- **High-frequency caches**: 10M-50M Hz (zero-allocation sign emission)
- **High-frequency services**: 10M-50M Hz (zero-allocation sign emission)
- **High-frequency probes**: 1M-10M Hz (per-request instrumentation)
- **Medium-frequency monitors**: 100K-1M Hz (condition assessment)
- **Low-frequency reporters**: 1K-100K Hz (situational updates)

Sign emissions leverage **zero-allocation enum emission** with ~10-20ns cost for non-transit emits.
Transit emits (cascading within circuit thread) achieve **sub-3ns latency** (currently ~2.98ns,
achieving 336M ops/sec on substrate benchmarks).

## Building

Serventis is built as part of the Substrates project:

```bash
# Build serventis extension (without TCK tests)
./mvnw clean install -pl substrates/ext/serventis

# Run specific test suite
./mvnw test -Dtest=ActorsTest -pl substrates/ext/serventis
./mvnw test -Dtest=AgentsTest -pl substrates/ext/serventis
./mvnw test -Dtest=TransactionsTest -pl substrates/ext/serventis
./mvnw test -Dtest=BreakersTest -pl substrates/ext/serventis
./mvnw test -Dtest=CountersTest -pl substrates/ext/serventis
./mvnw test -Dtest=GaugesTest -pl substrates/ext/serventis
./mvnw test -Dtest=CachesTest -pl substrates/ext/serventis
./mvnw test -Dtest=LeasesTest -pl substrates/ext/serventis
./mvnw test -Dtest=LocksTest -pl substrates/ext/serventis
./mvnw test -Dtest=AtomicsTest -pl substrates/ext/serventis
./mvnw test -Dtest=LatchesTest -pl substrates/ext/serventis
./mvnw test -Dtest=SystemsTest -pl substrates/ext/serventis
./mvnw test -Dtest=StatusesTest -pl substrates/ext/serventis
./mvnw test -Dtest=PipelinesTest -pl substrates/ext/serventis
./mvnw test -Dtest=ProcessesTest -pl substrates/ext/serventis
./mvnw test -Dtest=RoutersTest -pl substrates/ext/serventis
./mvnw test -Dtest=SensorsTest -pl substrates/ext/serventis
./mvnw test -Dtest=TasksTest -pl substrates/ext/serventis
./mvnw test -Dtest=TimersTest -pl substrates/ext/serventis
./mvnw test -Dtest=CyclesTest -pl substrates/ext/serventis
./mvnw test -Dtest=ExchangesTest -pl substrates/ext/serventis
./mvnw test -Dtest=OutcomesTest -pl substrates/ext/serventis
./mvnw test -Dtest=OperationsTest -pl substrates/ext/serventis
./mvnw test -Dtest=TrendsTest -pl substrates/ext/serventis
./mvnw test -Dtest=SurveysTest -pl substrates/ext/serventis
./mvnw test -Dtest=FlowsTest -pl substrates/ext/serventis
```

### Running TCK Tests

Serventis includes TCK tests that validate integration with SPI implementations. The TCK is optional
and activated via the `tck` profile:

```bash
# Run Serventis with TCK tests using default SPI (alpha implementation)
./mvnw clean install -pl substrates/ext/serventis -Dtck

# Test with a custom SPI implementation
./mvnw clean install -pl substrates/ext/serventis -Dtck \
  -Dsubstrates.spi.groupId=com.example \
  -Dsubstrates.spi.artifactId=my-substrates-spi \
  -Dsubstrates.spi.version=1.0.0
```

**Note**: Tests are skipped by default (`maven.test.skip=true` in `pom.xml`). The `tck` profile
overrides this to enable testing. See the [Substrates README](../../README.md) for more details on
TCK configuration.

## License

Copyright © 2025 William David Louth. All rights reserved.

## Links

- [Humainary.io](https://humainary.io)
