// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.opt.exec;

import io.humainary.substrates.ext.serventis.api.Serventis;
import io.humainary.substrates.ext.serventis.sdk.SignalSet;

/// # Transactions API
///
/// The `Transactions` API provides a comprehensive framework for observing transactional
/// semantics and distributed coordination patterns. It enables fine-grained instrumentation
/// of transaction lifecycle, state consistency operations, and coordination protocols including
/// two-phase commit (2PC), three-phase commit (3PC), saga patterns, and consensus algorithms.
///
/// ## Purpose
///
/// This API enables systems to emit **rich semantic signals** about transactional operations
/// and coordination states, capturing the complete lifecycle from initiation through resolution
/// (commit or rollback). The dual-dimension model (COORDINATOR/PARTICIPANT) enables observation
/// from both coordinator and cohort perspectives, essential for understanding distributed
/// transaction behavior.
///
/// ## Important: Observability vs Implementation
///
/// This API is for **reporting transaction semantics**, not implementing transaction protocols.
/// When your database, coordinator, or distributed system performs transactional operations,
/// use this API to emit observability signals. Meta-level observers can then reason about
/// transaction patterns, failure modes, and coordination reliability without coupling to
/// your protocol implementation details.
///
/// **Example**: When your coordinator starts a transaction, call `transaction.start()`.
/// When a participant receives the prepare request, call `transaction.prepared()`. When the
/// coordinator commits, call `transaction.commit()`. These signals enable meta-observability
/// of the transaction network.
///
/// ## Key Concepts
///
/// - **Transaction**: A unit of work with ACID properties (Atomicity, Consistency, Isolation, Durability)
/// - **Signal**: A semantic event combining a **Sign** (what happened) and **Dimension** (perspective)
/// - **Sign**: The type of transaction operation (START, PREPARE, COMMIT, ROLLBACK, etc.)
/// - **Dimension**: The coordination perspective (COORDINATOR = transaction manager, PARTICIPANT = client)
/// - **Coordinator**: The initiator managing the transaction protocol (2PC leader, saga orchestrator)
/// - **Cohort**: A participant executing local operations under the transaction
///
/// ## Dual-Dimension Model
///
/// Every sign has two dimensions representing different roles in transaction coordination:
///
/// | Dimension   | Perspective         | Role           | Example Signals               |
/// |-------------|---------------------|----------------|-------------------------------|
/// | COORDINATOR | Coordinator (self)  | Protocol leader| START, PREPARE, COMMIT        |
/// | PARTICIPANT | Cohort (observed)   | Protocol member| STARTED, PREPARED, COMMITTED  |
///
/// **COORDINATOR** signals indicate "I am coordinating this transaction operation now" while
/// **PARTICIPANT** signals indicate "I observed/received this transaction operation from the
/// coordinator". This enables distributed transaction systems to observe coordination from
/// both coordinator and participant perspectives.
///
/// ## Transaction Lifecycle
///
/// ### Standard Transaction Flow (2PC)
/// ```
/// Initiation:    START → STARTED
///      ↓
/// Voting Phase:  PREPARE → PREPARED (all participants vote)
///      ↓
/// Decision:      COMMIT → COMMITTED (if all yes) OR ROLLBACK → ROLLBACKED (if any no)
///      ↓
/// Resolution:    (Transaction complete)
/// ```
///
/// ### Transaction with Expiration
/// ```
/// START → STARTED → PREPARE → PREPARED → [EXPIRE → EXPIRED] → ROLLBACK → ROLLBACKED
/// ```
///
/// ### Transaction with Conflict
/// ```
/// START → STARTED → PREPARE → [CONFLICT → CONFLICTED] → ABORT → ABORTED
/// ```
///
/// ### Saga Pattern (Compensation)
/// ```
/// START → STARTED → [FAIL] → COMPENSATE → COMPENSATED → ROLLBACK → ROLLBACKED
/// ```
///
/// ## Signal Categories
///
/// The API defines signals across transaction lifecycle phases:
///
/// ### Transaction Lifecycle
/// - **START/STARTED**: Transaction initiation
/// - **PREPARE/PREPARED**: Voting phase (2PC prepare, can you commit?)
/// - **COMMIT/COMMITTED**: Final commitment (all voted yes)
/// - **ROLLBACK/ROLLBACKED**: Transaction abort (explicit rollback)
///
/// ### Error Conditions
/// - **ABORT/ABORTED**: Forced termination (e.g., deadlock detected)
/// - **EXPIRE/EXPIRED**: Transaction exceeded time budget
/// - **CONFLICT/CONFLICTED**: Write conflict or constraint violation
///
/// ### Saga Pattern
/// - **COMPENSATE/COMPENSATED**: Compensating action (saga rollback)
///
/// ## Relationship to Other APIs
///
/// `Transactions` integrates with other Serventis APIs:
///
/// - **Services API**: Transactions often span service boundaries (distributed transactions)
/// - **Resources API**: Transactions may ACQUIRE/RELEASE locks or resources
/// - **Statuses API**: Transaction patterns (many ABORTs) inform condition assessment (DEGRADED)
/// - **Locks API**: Transactions coordinate with locking for isolation guarantees
///
/// ## Perspective Usage Patterns
///
/// ### Coordinator Perspective (COORDINATOR)
/// ```java
/// coordinator.start();       // I'm starting a transaction
/// coordinator.prepare();     // I'm asking participants to prepare
/// coordinator.commit();      // I'm committing the transaction
/// coordinator.rollback();    // I'm rolling back the transaction
/// ```
///
/// ### Participant Perspective (PARTICIPANT)
/// ```java
/// // Participant observes coordinator's operations
/// participant.started();     // Coordinator started transaction
/// participant.prepared();    // I prepared (voted yes)
/// participant.committed();   // Coordinator committed
/// participant.rollbacked();  // Coordinator rolled back
/// ```
///
/// ### Two-Phase Commit (2PC) Example
/// ```java
/// // Coordinator (Node A)
/// coordinator.start();       // COORDINATOR: Start transaction
/// coordinator.prepare();     // COORDINATOR: Send prepare to all participants
///
/// // Participant 1 (Node B)
/// participant1.started();    // PARTICIPANT: Received start
/// participant1.prepared();   // PARTICIPANT: Voted yes, ready to commit
///
/// // Participant 2 (Node C)
/// participant2.started();    // PARTICIPANT: Received start
/// participant2.prepared();   // PARTICIPANT: Voted yes, ready to commit
///
/// // Coordinator (Node A) - all voted yes
/// coordinator.commit();      // COORDINATOR: Send commit to all
///
/// // Participants acknowledge
/// participant1.committed();  // PARTICIPANT: Applied commit
/// participant2.committed();  // PARTICIPANT: Applied commit
/// ```
///
/// ### Saga Pattern Example (Compensation)
/// ```java
/// // Saga Orchestrator
/// saga.start();              // COORDINATOR: Start saga
/// // ... steps execute ...
/// saga.compensate();         // COORDINATOR: Step failed, compensating
/// saga.rollback();           // COORDINATOR: Rolling back saga
///
/// // Saga Participant
/// sagaStep.started();        // PARTICIPANT: Saga started
/// sagaStep.compensated();    // PARTICIPANT: Compensation applied
/// sagaStep.rollbacked();     // PARTICIPANT: Rollback complete
/// ```
///
/// ## Protocol Support
///
/// This API supports observation of multiple transaction protocols:
///
/// ### Two-Phase Commit (2PC)
/// - Coordinator: START → PREPARE → (all vote) → COMMIT/ROLLBACK
/// - Participants: STARTED → PREPARED → COMMITTED/ROLLBACKED
///
/// ### Three-Phase Commit (3PC)
/// - Same signals, but semantic interpretation includes pre-commit phase
/// - PREPARE phase is "can commit?", implicit pre-commit before COMMIT
///
/// ### Saga Pattern
/// - Coordinator: START → steps → (on failure) → COMPENSATE → ROLLBACK
/// - Participants: STARTED → (on compensation) → COMPENSATED → ROLLBACKED
///
/// ### Paxos/Raft Consensus
/// - Leader: START → PREPARE → (quorum) → COMMIT
/// - Followers: STARTED → PREPARED → COMMITTED
///
/// ## Performance Considerations
///
/// Transaction signal emissions operate at coordination timescales (milliseconds to seconds)
/// rather than computational timescales (microseconds). Transactions coordinate distributed
/// state changes with network communication and persistence. Signals flow asynchronously
/// through the circuit's event queue, adding minimal overhead.
///
/// Unlike high-frequency instruments (Counters at 10M-50M Hz), transaction coordination is
/// lower-rate but higher semantic density - each signal carries significant meaning about
/// distributed state consistency and coordination protocol progress.
///
/// Signal emissions leverage **zero-allocation enum emission** with ~10-20ns cost for
/// non-transit emits. The dual-direction model (16 signals from 8 signs × 2 dimensions)
/// provides complete observability of transaction coordination from both coordinator and
/// participant perspectives.
///
/// ## Error Handling and Failure Modes
///
/// Transaction failures can occur at multiple stages:
///
/// - **EXPIRE**: Participant took too long to prepare/commit
/// - **CONFLICT**: Write conflict, constraint violation, or deadlock
/// - **ABORT**: Explicit abort due to business logic or system condition
///
/// The coordinator typically responds to failures by initiating ROLLBACK to restore
/// consistency. Participants observe ROLLBACKED and undo their prepared changes.
///
/// ## Isolation Levels
///
/// This API is isolation-level agnostic. Whether your transaction uses:
/// - Read Uncommitted
/// - Read Committed
/// - Repeatable Read
/// - Serializable
///
/// ...the signal semantics remain the same. Higher-level analysis can correlate transaction
/// signals with CONFLICT rates to understand isolation behavior, but the API itself doesn't
/// encode isolation levels.
///
/// @author William David Louth
/// @since 1.0

public final class Transactions
  implements Serventis {

  private Transactions () { }

  /// A static composer function for creating Transaction instruments.
  ///
  /// This method can be used as a method reference with conduits as follows:
  ///
  /// Example usage:
  /// ```java
  /// final var cortex = Substrates.cortex();
  /// var transaction = circuit.conduit(Transactions::composer).percept(cortex.name("db.transaction"));
  /// ```
  ///
  /// @param channel the channel from which to create the transaction
  /// @return a new Transaction instrument for the specified channel
  /// @throws NullPointerException if the channel param is `null`

  @New
  @NotNull
  public static Transaction composer (
    @NotNull final Channel < ? super Signal > channel
  ) {

    return
      new Transaction (
        channel.pipe ()
      );

  }


  /// A [Sign] classifies transaction operations that occur during distributed coordination.
  /// These classifications enable analysis of transaction patterns, failure modes, and
  /// coordination protocol behavior in distributed systems.
  ///
  /// ## Sign Categories
  ///
  /// Signs are organized into functional categories representing different aspects
  /// of transaction coordination:
  ///
  /// - **Lifecycle**: START, PREPARE, COMMIT, ROLLBACK
  /// - **Error Conditions**: ABORT, EXPIRE, CONFLICT
  /// - **Compensation**: COMPENSATE

  public enum Sign
    implements Serventis.Sign {

    /// Indicates transaction initiation.
    ///
    /// START marks the start of a transactional unit of work. In distributed systems,
    /// the coordinator starts the transaction and participants observe the start.
    /// The transaction is now in-flight and will eventually reach a terminal state
    /// (COMMIT or ROLLBACK).
    ///
    /// **Typical usage**: Starting a database transaction, beginning a saga, initiating 2PC
    ///
    /// **Protocols**: All transaction protocols (2PC, 3PC, Saga, Paxos, Raft)

    START,

    /// Indicates the voting/prepare phase of two-phase commit.
    ///
    /// PREPARE represents the coordinator asking participants "Can you commit?" and
    /// participants responding with their vote (yes = prepared, no = abort). In 2PC,
    /// this is the critical synchronization point where participants durably record
    /// their intent to commit but have not yet committed.
    ///
    /// **Typical usage**: 2PC prepare phase, Paxos propose, Raft log replication
    ///
    /// **Protocols**: 2PC, 3PC, Paxos, Raft (voting/consensus building)

    PREPARE,

    /// Indicates transaction commitment.
    ///
    /// COMMIT represents the coordinator deciding to commit (all participants voted yes)
    /// and participants durably applying the transaction. After COMMIT, the transaction's
    /// effects are permanent and visible. This is the positive terminal state.
    ///
    /// **Typical usage**: 2PC commit phase, saga completion, Paxos/Raft commit
    ///
    /// **Protocols**: All transaction protocols (positive resolution)

    COMMIT,

    /// Indicates transaction rollback.
    ///
    /// ROLLBACK represents the coordinator deciding to abort (at least one participant
    /// voted no or expiration occurred) and participants undoing their prepared changes.
    /// After ROLLBACK, the transaction's effects are erased and the system returns to
    /// the state before START. This is the negative terminal state.
    ///
    /// **Typical usage**: 2PC abort phase, saga rollback, explicit transaction abort
    ///
    /// **Protocols**: All transaction protocols (negative resolution)

    ROLLBACK,

    /// Indicates forced transaction termination.
    ///
    /// ABORT represents an explicit abort condition detected by the coordinator or
    /// participant, such as deadlock detection, business logic rejection, or system
    /// constraint violation. Unlike ROLLBACK (which can be a normal response to a no vote),
    /// ABORT indicates an abnormal condition requiring immediate termination.
    ///
    /// **Typical usage**: Deadlock abort, constraint violation, business rule rejection
    ///
    /// **Protocols**: Database transactions, distributed deadlock detection

    ABORT,

    /// Indicates transaction expiration.
    ///
    /// EXPIRE represents the transaction exceeding its time budget. In distributed
    /// systems, expiration prevents indefinite blocking when participants fail or become
    /// unreachable. Coordinator typically responds to EXPIRE by initiating ROLLBACK.
    ///
    /// **Typical usage**: Participant prepare expiry, commit expiry, network partition
    ///
    /// **Protocols**: 2PC, 3PC (failure detection), distributed systems with time budgets

    EXPIRE,

    /// Indicates write conflict or constraint violation.
    ///
    /// CONFLICT represents a conflict detected during transaction execution, such as
    /// write-write conflict (optimistic locking), serialization failure, or constraint
    /// violation. Coordinator typically responds to CONFLICT by initiating ROLLBACK or
    /// ABORT. High CONFLICT rates may indicate contention or isolation level issues.
    ///
    /// **Typical usage**: Optimistic locking conflict, serialization failure, unique constraint
    ///
    /// **Protocols**: Database transactions, optimistic concurrency control, MVCC

    CONFLICT,

    /// Indicates compensating action in saga pattern.
    ///
    /// COMPENSATE represents a compensating transaction that undoes the effects of a
    /// previously committed local transaction within a saga. Unlike ROLLBACK (which undoes
    /// uncommitted changes), COMPENSATE semantically reverses committed changes through
    /// explicit compensation logic.
    ///
    /// **Typical usage**: Saga rollback, compensating transaction, semantic undo
    ///
    /// **Protocols**: Saga pattern, choreography-based sagas, orchestration-based sagas

    COMPENSATE

  }


  /// Dimension of transaction observation representing the role in distributed coordination.
  ///
  /// In distributed transaction protocols (2PC, 3PC, Paxos, Raft, Saga), there are two
  /// fundamental roles: the coordinator/initiator and the participants/cohorts. The dimension
  /// classifies whether signals represent operations initiated by the coordinator or observations
  /// by participants. This dual-perspective model enables complete observability of transaction
  /// coordination from both sides of the protocol.
  ///
  /// ## The Two Perspectives
  ///
  /// | Dimension   | Perspective        | Role           | Example                          |
  /// |-------------|--------------------|----------------|----------------------------------|
  /// | COORDINATOR | Coordinator (self) | Protocol leader| "I am committing"                |
  /// | PARTICIPANT | Cohort (observed)  | Protocol member| "Coordinator committed"          |
  ///
  /// ## COORDINATOR vs PARTICIPANT
  ///
  /// **COORDINATOR** signals represent **operations the coordinator is performing**:
  /// - Generated by the transaction coordinator/leader
  /// - Present-tense semantics ("I start", "I prepare", "I commit")
  /// - Used for protocol orchestration and decision reporting
  /// - Forms the basis for understanding coordinator behavior
  ///
  /// **PARTICIPANT** signals represent **operations participants observe/perform**:
  /// - Generated by transaction participants/cohorts
  /// - Past-tense semantics ("Coordinator started", "I prepared", "Coordinator committed")
  /// - Used for participant state tracking and vote reporting
  /// - Forms the basis for understanding participant behavior
  ///
  /// ## Protocol Flow Example: Two-Phase Commit
  ///
  /// ### Coordinator (Node A) - COORDINATOR perspective
  /// ```java
  /// coordinator.start();       // COORDINATOR: I'm starting transaction T1
  /// coordinator.prepare();     // COORDINATOR: I'm sending prepare to all participants
  /// // ... wait for votes ...
  /// coordinator.commit();      // COORDINATOR: All voted yes, I'm committing
  /// ```
  ///
  /// ### Participant (Node B) - PARTICIPANT perspective
  /// ```java
  /// participant.started();     // PARTICIPANT: Coordinator started transaction T1
  /// participant.prepared();    // PARTICIPANT: I prepared and voted yes
  /// participant.committed();   // PARTICIPANT: Coordinator committed, I applied changes
  /// ```
  ///
  /// ### Participant (Node C) - PARTICIPANT perspective with failure
  /// ```java
  /// participant.started();     // PARTICIPANT: Coordinator started transaction T1
  /// participant.conflict();    // PARTICIPANT: I detected a conflict, voting no
  /// participant.rollbacked();  // PARTICIPANT: Coordinator rolled back, I discarded changes
  /// ```
  ///
  /// ## Temporal Semantics
  ///
  /// - **COORDINATOR**: Present tense, happening **now**, coordinator decision/action
  /// - **PARTICIPANT**: Mixed tense, either observed (coordinator action) or self-reported (vote)
  ///
  /// The temporal distinction is crucial for understanding protocol causality. PARTICIPANT
  /// signals like BEGAN indicate the coordinator initiated something earlier, while PREPARED
  /// indicates the participant is reporting its own vote.
  ///
  /// ## Use in Distributed Protocols
  ///
  /// The dual-dimension model enables:
  /// - **Protocol observability**: Tracking coordinator decisions and participant responses
  /// - **Failure analysis**: Identifying which participants expire, conflict, or fail
  /// - **Latency analysis**: Measuring time between coordinator PREPARE and participant PREPARED
  /// - **Consistency verification**: Ensuring all participants observe coordinator decisions
  /// - **Deadlock detection**: Observing ABORT patterns across participants

  public enum Dimension
    implements Category {

    /// The emission of a transaction signal from the coordinator's perspective.
    ///
    /// COORDINATOR represents **operations the transaction coordinator is performing right now**.
    /// Use COORDINATOR when the local node is the transaction coordinator/leader orchestrating
    /// the protocol (2PC coordinator, Paxos leader, Raft leader, saga orchestrator, database engine).
    ///
    /// In transaction protocol terms, this node is the source of coordination decisions,
    /// even when those decisions are to ROLLBACK or ABORT.
    ///
    /// **Mental model**: "I am the transaction manager coordinating this operation now"
    /// **Examples**: START, PREPARE, COMMIT, ROLLBACK, ABORT
    /// **Usage**: Protocol orchestration, decision reporting, coordinator telemetry
    /// **Note**: Application code using JDBC would typically use PARTICIPANT, not COORDINATOR

    COORDINATOR,

    /// The reception of a transaction signal from a participant's perspective.
    ///
    /// PARTICIPANT represents **observations or actions from the participant/cohort perspective**.
    /// Use PARTICIPANT when observing coordinator operations or reporting participant actions
    /// within a transaction. This is the dimension most application code will use.
    ///
    /// In transaction protocol terms, this node is a cohort receiving coordinator messages
    /// or reporting its own vote/state within the distributed protocol.
    ///
    /// **Mental model**: "I am a client/participant in this transaction"
    /// **Examples**: STARTED, PREPARED, COMMITTED, ROLLBACKED, CONFLICTED
    /// **Usage**: Participant state tracking, vote reporting, cohort telemetry, application code
    /// **Note**: JDBC applications, REST clients, and most app code should use PARTICIPANT

    PARTICIPANT

  }

  /// The [Signal] record represents the assessed operational condition of a subject within some context.
  /// It includes the sign classification as well as the statistical certainty of that classification.
  ///
  /// @param sign      the operational sign classification
  /// @param dimension the statistical certainty of the sign assessment

  @Provided
  public record Signal(
    Sign sign,
    Dimension dimension
  ) implements Serventis.Signal < Sign, Dimension > { }

  /// The `Transaction` class represents a transactional unit of work in a distributed system.
  /// A transaction is an observable entity that emits signals about its lifecycle, coordination
  /// state, and resolution (commit or rollback).
  ///
  /// ## Usage
  ///
  /// Use domain-specific methods for all transaction lifecycle events, specifying the dimension
  /// (role) as a parameter:
  ///
  /// ```java
  /// // A coordinator (e.g., database engine, saga orchestrator) emits:
  /// transaction.start(Transactions.Dimension.COORDINATOR);
  /// transaction.prepare(Transactions.Dimension.COORDINATOR);
  /// transaction.commit(Transactions.Dimension.COORDINATOR);
  ///
  /// // A participant (e.g., JDBC application, REST client) emits:
  /// transaction.start(Transactions.Dimension.PARTICIPANT);
  /// transaction.prepare(Transactions.Dimension.PARTICIPANT);
  /// transaction.commit(Transactions.Dimension.PARTICIPANT);
  /// ```

  @Queued
  @Provided
  public static final class Transaction
    implements Signaler < Sign, Dimension > {

    private static final SignalSet < Sign, Dimension, Signal > SIGNALS =
      new SignalSet <> (
        Sign.class,
        Dimension.class,
        Signal::new
      );

    private final Pipe < ? super Signal > pipe;

    private Transaction (
      final Pipe < ? super Signal > pipe
    ) {

      this.pipe = pipe;

    }

    /// Emits an `ABORT` sign with the specified dimension.
    ///
    /// @param dimension the role perspective of the signal emission
    /// @throws NullPointerException if the dimension is `null`

    public void abort (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.ABORT,
          dimension
        )
      );

    }

    /// Emits a `COMMIT` sign with the specified dimension.
    ///
    /// @param dimension the role perspective of the signal emission
    /// @throws NullPointerException if the dimension is `null`

    public void commit (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.COMMIT,
          dimension
        )
      );

    }

    /// Emits a `COMPENSATE` sign with the specified dimension.
    ///
    /// @param dimension the role perspective of the signal emission
    /// @throws NullPointerException if the dimension is `null`

    public void compensate (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.COMPENSATE,
          dimension
        )
      );

    }

    /// Emits a `CONFLICT` sign with the specified dimension.
    ///
    /// @param dimension the role perspective of the signal emission
    /// @throws NullPointerException if the dimension is `null`

    public void conflict (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.CONFLICT,
          dimension
        )
      );


    }

    /// Emits an `EXPIRE` sign with the specified dimension.
    ///
    /// @param dimension the role perspective of the signal emission
    /// @throws NullPointerException if the dimension is `null`

    public void expire (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.EXPIRE,
          dimension
        )
      );


    }

    /// Emits a `PREPARE` sign with the specified dimension.
    ///
    /// @param dimension the role perspective of the signal emission
    /// @throws NullPointerException if the dimension is `null`

    public void prepare (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.PREPARE,
          dimension
        )
      );


    }

    /// Emits a `ROLLBACK` sign with the specified dimension.
    ///
    /// @param dimension the role perspective of the signal emission
    /// @throws NullPointerException if the dimension is `null`

    public void rollback (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.ROLLBACK,
          dimension
        )
      );


    }

    /// Signals a transaction event by composing sign and dimension.
    ///
    /// @param sign      the sign component
    /// @param dimension the dimension component

    @Override
    public void signal (
      @NotNull final Sign sign,
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          sign,
          dimension
        )
      );

    }

    /// Emits a `START` sign with the specified dimension.
    ///
    /// @param dimension the role perspective of the signal emission
    /// @throws NullPointerException if the dimension is `null`

    public void start (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.START,
          dimension
        )
      );


    }

  }

}
