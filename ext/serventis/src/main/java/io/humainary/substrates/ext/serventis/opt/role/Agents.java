// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.opt.role;

import io.humainary.substrates.ext.serventis.api.Serventis;
import io.humainary.substrates.ext.serventis.sdk.SignalSet;

/// # Agents API
///
/// The `Agents` API provides a comprehensive framework for observing agent coordination
/// through **semantic signal emission** grounded in **Promise Theory** (Mark Burgess).
/// It enables fine-grained instrumentation of autonomous agent interaction, promise
/// lifecycle, and voluntary cooperation patterns.
///
/// ## Purpose
///
/// This API enables systems to emit **rich semantic signals** about agent promises and
/// dependencies, capturing the full lifecycle of autonomous coordination: offers,
/// promises, acceptances, fulfillments, and retractions. The dual-dimension model
/// (OUTBOUND/INBOUND) enables both self-reporting of promises made and observation
/// of promises received from others.
///
/// ## Important: Autonomous Coordination vs Command-Control
///
/// This API is fundamentally different from command-control models. In Promise Theory:
/// - **Agents only promise what they control** - no agent can command another
/// - **Cooperation is voluntary** - agents accept promises, not orders
/// - **Obligations arise from mutual promises** - not from hierarchical authority
/// - **Autonomy is preserved** - agents can retract promises they cannot keep
///
/// For command-control or conversational coordination, see the **Actors API** which
/// provides speech act semantics (ASK, COMMAND, REQUEST, DELIVER).
///
/// ## Important: Reporting vs Implementation
///
/// This API is for **reporting agent promise semantics**, not implementing agents.
/// When your agent offers a capability, makes a promise, or accepts another's promise,
/// use this API to emit observability signals. Meta-level observers can then reason
/// about promise networks, dependency graphs, and coordination reliability without
/// coupling to your agent implementation details.
///
/// **Example**: When your scaling agent promises to maintain capacity, call
/// `agent.promise()`. When a monitoring agent accepts that promise, it calls
/// `agent.accept()`. When the scaling agent fulfills, call `agent.fulfill()`.
/// These signals enable meta-observability of the promise network.
///
/// ## Key Concepts
///
/// - **Agent**: An autonomous entity that makes and accepts promises
/// - **Signal**: A semantic event combining a **Sign** (what happened) and **Dimension** (perspective)
/// - **Sign**: The type of promise operation (OFFER, PROMISE, ACCEPT, FULFILL, etc.)
/// - **Dimension**: The promise perspective (PROMISER = I act, PROMISEE = they acted)
/// - **Promise**: A voluntary commitment by an agent about its own future behavior
/// - **Dependency**: A relationship where one agent relies on another's promise
///
/// ## Dual-Dimension Model
///
/// Every sign has two dimensions representing different perspectives in the promise relationship:
///
/// | Dimension | Perspective         | Example Usage                                  |
/// |-----------|---------------------|------------------------------------------------|
/// | PROMISER  | Self (acting)       | `agent.offer(PROMISER)` = I offer              |
/// | PROMISEE  | Other (observed)    | `agent.offer(PROMISEE)` = I observe their offer|
///
/// **PROMISER** signals indicate "I am performing this action" (first-person) while **PROMISEE**
/// signals indicate "I observed them performing this action" (third-person). The same sign
/// (e.g., OFFER) is used with different dimensions to indicate perspective. This enables
/// distributed agents to coordinate based on both promises they make and promises they observe.
///
/// ## Promise Theory Foundation
///
/// **Mark Burgess' Promise Theory** provides the theoretical grounding:
///
/// ### Core Principles
/// 1. **Agents make promises about their own behavior** - not demands on others
/// 2. **Promises are voluntary** - agents only promise what they control
/// 3. **Obligations arise from accepting promises** - not from commands
/// 4. **Cooperation emerges** from voluntary promise exchange
/// 5. **Autonomy is fundamental** - agents can only be influenced, not controlled
///
/// ### Promise Lifecycle
/// ```
/// Discovery:   INQUIRE (promisee) → OFFER (promiser observed)
///                ↓
/// Commitment:  PROMISE (promiser) → PROMISE (promisee observes)
///                ↓
/// Dependency:  ACCEPT (promisee) → ACCEPT (promiser observes)
///                ↓
/// Tracking:    DEPEND (promisee) → OBSERVE (ongoing)
///                ↓
/// Validation:  VALIDATE (promisee checks) → VALIDATE (promiser confirms)
///                ↓
/// Fulfillment: FULFILL (promiser) OR BREACH (promiser)
///                ↓
/// Retraction:  RETRACT (promiser withdraws)
/// ```
///
/// ## Signal Categories
///
/// The API defines signals across promise lifecycle phases:
///
/// ### Discovery & Capability Advertisement
/// - **OFFER**: Agent advertises capability (PROMISER) or observes offer (PROMISEE)
/// - **INQUIRE**: Agent asks about capabilities (PROMISEE) or receives inquiry (PROMISER)
///
/// ### Commitment Formation
/// - **PROMISE**: Agent commits to behavior (PROMISER) or observes promise (PROMISEE)
/// - **ACCEPT**: Agent accepts promise (PROMISEE) or observes acceptance (PROMISER)
///
/// ### Dependency Management
/// - **DEPEND**: Agent declares dependency (PROMISEE) or observes dependency (PROMISER)
/// - **OBSERVE**: Agent monitors promise state from either perspective
/// - **VALIDATE**: Agent confirms promise validity from either perspective
///
/// ### Promise Resolution
/// - **FULFILL**: Agent keeps promise (PROMISER) or observes fulfillment (PROMISEE)
/// - **BREACH**: Agent fails promise (PROMISER) or observes breach (PROMISEE)
/// - **RETRACT**: Agent withdraws promise (PROMISER) or observes retraction (PROMISEE)
///
/// ## Relationship to Other APIs
///
/// `Agents` integrates with other Serventis APIs:
///
/// - **Actors API**: For conversational/command-control coordination (complementary)
/// - **Services API**: Agents may PROMISE service availability, ACCEPT service dependencies
/// - **Resources API**: Agents may PROMISE resource provision, DEPEND on resource grants
/// - **Statuses API**: Promise patterns (many BREACHes) inform condition assessment (DEGRADED)
///
/// ## Perspective Usage Patterns
///
/// ### Self-Promises (PROMISER perspective)
/// ```java
/// scalingAgent.offer(PROMISER);    // I offer scaling capability
/// scalingAgent.promise(PROMISER);  // I promise to scale when needed
/// scalingAgent.fulfill(PROMISER);  // I have scaled as promised
/// ```
///
/// ### Observing Others' Promises (PROMISEE perspective)
/// ```java
/// // Monitoring agent observes scaling agent's promises
/// monitoringAgent.offer(PROMISEE);    // I observed scaling agent offer
/// monitoringAgent.promise(PROMISEE);  // I observed scaling agent promise
/// monitoringAgent.accept(PROMISEE);   // I accept and depend on that promise
/// monitoringAgent.fulfill(PROMISEE);  // I observed scaling agent fulfill
/// ```
///
/// ### Promise Networks
/// ```java
/// // Agent A: Capacity Monitor
/// capacityMonitor.inquire();    // PROMISER: Who can provide scaling?
/// capacityMonitor.offered();    // PROMISEE: Scaler offered capability
/// capacityMonitor.accept();     // PROMISER: I accept scaler's promise
/// capacityMonitor.depend();     // PROMISER: I depend on scaler
///
/// // Agent B: Scaler
/// scaler.offer();               // PROMISER: I offer scaling
/// scaler.promise();             // PROMISER: I promise to scale
/// scaler.accepted();            // PROMISEE: Monitor accepted my promise
/// scaler.depended();            // PROMISEE: Monitor depends on me
/// scaler.fulfill();             // PROMISER: I fulfilled my promise
/// ```
///
/// ## Promise Theory vs Command-Control
///
/// ### Promise Theory (Agents API)
/// - Voluntary cooperation
/// - Agents promise own behavior
/// - Dependencies explicit via ACCEPT/DEPEND
/// - Can RETRACT promises
/// - Autonomy preserved
///
/// ### Command-Control (Actors API)
/// - Hierarchical authority
/// - Commands from above
/// - Compliance expected
/// - No retraction (except failure)
/// - Authority enforced
///
/// Both models are valid for different contexts. Use Agents for autonomous systems,
/// Actors for conversational or hierarchical coordination.
///
/// ## Performance Considerations
///
/// Agent signal emissions operate at coordination timescales (seconds to minutes) rather
/// than computational timescales (microseconds). Promises are formed and fulfilled over
/// longer periods than individual operations. Signals flow asynchronously through the
/// circuit's event queue, adding minimal overhead. Unlike high-frequency instruments
/// (Counters, Routers at 10M-50M Hz), agent coordination is lower-rate but higher
/// semantic density - each signal carries significant meaning about the promise network
/// structure.
///
/// Signal emissions leverage **zero-allocation enum emission** with ~10-20ns cost for
/// non-transit emits. The dual-direction model (20 signals from 10 signs × 2 directions)
/// provides complete observability of promise relationships from both promiser and
/// promisee perspectives.
///
/// @author William David Louth
/// @since 1.0

public final class Agents
  implements Serventis {

  private Agents () { }

  /// A static composer function for creating Agent instruments.
  ///
  /// This method can be used as a method reference with conduits as follows:
  ///
  /// Example usage:
  /// ```java
  /// final var cortex = Substrates.cortex();
  /// var agent = circuit.conduit(Agents::composer).percept(cortex.name("capacity.monitor"));
  /// ```
  ///
  /// @param channel the channel from which to create the agent
  /// @return a new Agent instrument for the specified channel
  /// @throws NullPointerException if the channel param is `null`

  @New
  @NotNull
  public static Agent composer (
    @NotNull final Channel < ? super Signal > channel
  ) {

    return
      new Agent (
        channel.pipe ()
      );

  }

  /// A [Sign] classifies promise operations that occur during agent coordination.
  /// These classifications enable analysis of promise networks, dependency graphs,
  /// and cooperation patterns in autonomous systems.
  ///
  /// ## Sign Categories
  ///
  /// Signs are organized into functional categories representing different aspects
  /// of promise-based coordination:
  ///
  /// - **Discovery**: OFFER, INQUIRE
  /// - **Commitment**: PROMISE, ACCEPT
  /// - **Dependency**: DEPEND, OBSERVE, VALIDATE
  /// - **Resolution**: FULFILL, BREACH, RETRACT

  public enum Sign
    implements Serventis.Sign {

    /// Indicates an agent is advertising a capability (promise available).
    ///
    /// OFFER represents capability advertisement - the agent signals it can provide
    /// something if others wish to depend on it. Offers precede promises in the
    /// coordination lifecycle. Unlike promises, offers carry no commitment until
    /// accepted and promised.
    ///
    /// **Typical usage**: Service discovery, capability advertisement, API publication
    ///
    /// **Promise Theory**: Agents broadcast what they *can* promise, enabling discovery

    OFFER,

    /// Indicates an agent is making a promise about its own behavior.
    ///
    /// PROMISE is the fundamental commitment in Promise Theory - an agent voluntarily
    /// commits to behave in a specific way. Promises are only made about behavior the
    /// agent controls. Other agents can observe and accept these promises to form
    /// dependencies.
    ///
    /// **Typical usage**: Committing to provide service, maintain capacity, uphold SLA
    ///
    /// **Promise Theory**: The core voluntary commitment enabling cooperation

    PROMISE,

    /// Indicates an agent is accepting another agent's promise.
    ///
    /// ACCEPT creates a dependency relationship - the accepting agent now relies on
    /// the promiser's commitment. Acceptance is voluntary and makes the dependency
    /// explicit. Both agents are aware of the relationship.
    ///
    /// **Typical usage**: Depending on service availability, relying on resource provision
    ///
    /// **Promise Theory**: Creates mutual obligation - promiser aware of dependents

    ACCEPT,

    /// Indicates an agent kept its promise.
    ///
    /// FULFILL represents successful promise completion. The agent did what it promised.
    /// Forms the basis for trust and reliability measurement. High fulfillment rates
    /// indicate reliable agents; low rates indicate unreliable agents.
    ///
    /// **Typical usage**: Completed promised action, maintained promised state, delivered
    ///
    /// **Promise Theory**: The positive outcome - cooperation succeeded

    FULFILL,

    /// Indicates an agent is retracting a promise before fulfillment.
    ///
    /// RETRACT represents voluntary promise withdrawal. The agent can no longer maintain
    /// the promised behavior and is explicitly releasing dependents. Different from BREACH
    /// (failure) - RETRACT is proactive notification enabling adaptation.
    ///
    /// **Typical usage**: Service shutdown, capacity reduction, policy change
    ///
    /// **Promise Theory**: Agents can only promise what they control; when control is lost,
    /// retraction is the honest response

    RETRACT,

    /// Indicates an agent failed to keep its promise.
    ///
    /// BREACH represents promise violation - the agent could not deliver on its commitment.
    /// Unlike RETRACT (proactive), BREACH is reactive - the promise failed. Forms the
    /// basis for unreliability detection and trust erosion.
    ///
    /// **Typical usage**: Service failure, SLA violation, capacity exhaustion
    ///
    /// **Promise Theory**: The negative outcome - cooperation failed, trust damaged

    BREACH,

    /// Indicates an agent is asking about available capabilities.
    ///
    /// INQUIRE represents capability discovery - the agent is seeking promises to depend
    /// upon. Initiates the coordination cycle by finding agents that OFFER capabilities.
    ///
    /// **Typical usage**: Service discovery, capability query, dependency search
    ///
    /// **Promise Theory**: Discovery mechanism - agents find promisers through inquiry

    INQUIRE,

    /// Indicates an agent is monitoring another agent's promise state.
    ///
    /// OBSERVE represents ongoing monitoring of promises. Unlike VALIDATE (explicit check),
    /// OBSERVE is passive monitoring of promise health and continuation. Enables detection
    /// of promise degradation or retraction.
    ///
    /// **Typical usage**: Health monitoring, promise state tracking, degradation detection
    ///
    /// **Promise Theory**: Enables trust verification through continuous observation

    OBSERVE,

    /// Indicates an agent is declaring explicit dependency on a promise.
    ///
    /// DEPEND makes the dependency relationship explicit and tracked. More formal than
    /// ACCEPT - DEPEND often involves registration, monitoring, and explicit coordination.
    /// Creates observable dependency graphs.
    ///
    /// **Typical usage**: Dependency registration, coordination protocol, explicit coupling
    ///
    /// **Promise Theory**: Makes hidden dependencies visible for reasoning and coordination

    DEPEND,

    /// Indicates an agent is confirming a promise is still held.
    ///
    /// VALIDATE is an explicit check that a promise remains valid and will be fulfilled.
    /// Unlike OBSERVE (passive), VALIDATE is active verification. Enables detection of
    /// stale or retracted promises before relying on them.
    ///
    /// **Typical usage**: Pre-action validation, health check, promise confirmation
    ///
    /// **Promise Theory**: Active trust verification - "Are you still promising this?"

    VALIDATE

  }

  /// Dimension of agent coordination observation based on Promise Theory (Burgess).
  ///
  /// In Promise Theory, autonomous agents make voluntary commitments about their own behavior.
  /// The dimension classifies whether signals represent promises made by this agent or promises
  /// received from other agents. This dual-perspective model enables complete observability of
  /// promise networks from both sides of each relationship.
  ///
  /// ## The Two Perspectives
  ///
  /// | Dimension | Perspective        | Timing  | Voice     | Example                          |
  /// |-----------|--------------------|---------|-----------|---------------------------------|
  /// | PROMISER  | Self (acting)      | Present | "I am"    | "I am promising to scale"        |
  /// | PROMISEE  | Other (observed)   | Past    | "They did"| "They promised to scale"         |
  ///
  /// ## PROMISER vs PROMISEE
  ///
  /// **PROMISER** signals represent **promises and actions this agent is making**:
  /// - Generated by the agent making the promise or taking action
  /// - Present-tense semantics ("I promise", "I offer", "I fulfill")
  /// - Used for self-reporting and promise advertisement
  /// - Forms the basis for promise graphs emanating from this agent
  ///
  /// **PROMISEE** signals represent **promises and actions other agents made**:
  /// - Generated when observing signals from other agents
  /// - Past-tense semantics ("They promised", "They offered", "They fulfilled")
  /// - Used for dependency tracking and promise network observation
  /// - Forms the basis for understanding what this agent depends upon
  ///
  /// ## Promise Theory Foundation
  ///
  /// **Example**: Service A promises 100ms response time to Service B
  ///   - Service A perspective: PROMISER dimension (tracking my commitments)
  ///   - Service B perspective: PROMISEE dimension (tracking received promises)
  ///
  /// This differs from imperative command-and-control: promises are voluntary
  /// declarations of intent, not obligations imposed by external authority.
  ///
  /// **Reference**: Burgess, M. "Promise Theory: Principles and Applications" (2015)
  ///
  /// ## Promise Flow Example
  ///
  /// ### Agent A (Capacity Monitor) - Mixed Perspectives
  /// ```java
  /// capacityMonitor.inquire();   // PROMISER: I'm asking who can scale
  /// capacityMonitor.offered();   // PROMISEE: Scaler offered capability
  /// capacityMonitor.accept();    // PROMISER: I accept their offer
  /// capacityMonitor.depend();    // PROMISER: I depend on their promise
  /// capacityMonitor.fulfilled(); // PROMISEE: They fulfilled their promise
  /// ```
  ///
  /// ### Agent B (Scaler) - Mixed Perspectives
  /// ```java
  /// scaler.offer();              // PROMISER: I offer scaling
  /// scaler.inquired();           // PROMISEE: Monitor asked about capabilities
  /// scaler.promise();            // PROMISER: I promise to scale
  /// scaler.accepted();           // PROMISEE: Monitor accepted my promise
  /// scaler.depended();           // PROMISEE: Monitor depends on me
  /// scaler.fulfill();            // PROMISER: I fulfilled my promise
  /// ```
  ///
  /// ## Temporal Semantics
  ///
  /// - **PROMISER**: Present tense, happening **now**, real-time commitment/action
  /// - **PROMISEE**: Past tense, happened **earlier**, observed promise/action
  ///
  /// The temporal distinction is crucial for understanding causality in promise networks.
  /// PROMISEE signals represent promises that were made at some point in the past and form
  /// the basis of current dependencies, while PROMISER signals represent current promises
  /// being made or fulfilled.
  ///
  /// ## Use in Promise Networks
  ///
  /// The dual-dimension model enables:
  /// - **Dependency graphs**: Tracking who depends on whom via ACCEPT/DEPEND
  /// - **Promise monitoring**: Observing fulfillment via OBSERVE/VALIDATE
  /// - **Trust metrics**: Measuring FULFILL/BREACH ratios per agent
  /// - **Network topology**: Understanding promise relationships through signal flow
  /// - **Cascade detection**: Seeing how breached promises propagate (observe BREACH → emit RETRACT)

  public enum Dimension
    implements Category {

    /// The emission of a promise signal from the agent's own perspective.
    ///
    /// PROMISER represents **promises and actions this agent is making/taking right now**.
    /// Use PROMISER when the local agent is offering, promising, accepting, fulfilling,
    /// or performing any other promise operation on its own behalf.
    ///
    /// In Promise Theory terms, this agent is the source of the voluntary commitment,
    /// even when that commitment is to accept or depend on another agent's promise.
    ///
    /// **Mental model**: "I am making this promise/action now"
    /// **Examples**: OFFER, PROMISE, ACCEPT, FULFILL, DEPEND
    /// **Usage**: Promise advertisement, commitment formation, fulfillment reporting

    PROMISER,

    /// The reception of a promise signal from another agent's perspective.
    ///
    /// PROMISEE represents **observations of promises and actions other agents made**.
    /// Use PROMISEE when observing promises from other agents, typically to form
    /// dependencies, track fulfillment, or monitor promise health.
    ///
    /// In Promise Theory terms, this agent is receiving information about commitments
    /// made by other autonomous agents in the promise network.
    ///
    /// **Mental model**: "I observed them make that promise/action"
    /// **Examples**: `offer(PROMISEE)`, `promise(PROMISEE)`, `fulfill(PROMISEE)`
    /// **Usage**: Dependency formation, promise observation, trust assessment

    PROMISEE

  }

  /// The `Agent` class represents an autonomous entity that participates in promise-based
  /// coordination. An agent is an observable entity that emits signals about its promises,
  /// dependencies, and observations of other agents' promises.
  ///
  /// ## Usage
  ///
  /// Use domain-specific paired methods for all promise lifecycle events:
  /// ```java
  /// agent.offer();      // PROMISER: I offer capability
  /// agent.offered();    // PROMISEE: They offered capability
  /// agent.promise();    // PROMISER: I promise behavior
  /// agent.promised();   // PROMISEE: They promised behavior
  /// agent.fulfill();    // PROMISER: I kept my promise
  /// agent.fulfilled();  // PROMISEE: They kept their promise
  /// ```
  ///
  /// Each sign has two methods representing the dual-perspective model, enabling agents
  /// to report both their own promises (PROMISER) and observed promises from others
  /// (PROMISEE).

  @Queued
  @Provided
  public static final class Agent
    implements Signaler < Sign, Dimension > {

    private static final SignalSet < Sign, Dimension, Signal > SIGNALS =
      new SignalSet <> (
        Sign.class,
        Dimension.class,
        Signal::new
      );

    private final Pipe < ? super Signal > pipe;

    private Agent (
      final Pipe < ? super Signal > pipe
    ) {

      this.pipe = pipe;

    }

    /// Emits an accept sign from this agent.
    ///
    /// @param dimension the perspective (PROMISER or PROMISEE)

    public void accept (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.ACCEPT,
          dimension
        )
      );

    }

    /// Emits a breach sign from this agent.
    ///
    /// @param dimension the perspective (PROMISER or PROMISEE)

    public void breach (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.BREACH,
          dimension
        )
      );

    }

    /// Emits a depend sign from this agent.
    ///
    /// @param dimension the perspective (PROMISER or PROMISEE)

    public void depend (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.DEPEND,
          dimension
        )
      );

    }

    /// Emits a fulfill sign from this agent.
    ///
    /// @param dimension the perspective (PROMISER or PROMISEE)

    public void fulfill (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.FULFILL,
          dimension
        )
      );

    }

    /// Emits an inquire sign from this agent.
    ///
    /// @param dimension the perspective (PROMISER or PROMISEE)

    public void inquire (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.INQUIRE,
          dimension
        )
      );

    }

    /// Emits an observe sign from this agent.
    ///
    /// @param dimension the perspective (PROMISER or PROMISEE)

    public void observe (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.OBSERVE,
          dimension
        )
      );

    }

    /// Emits an offer sign from this agent.
    ///
    /// @param dimension the perspective (PROMISER or PROMISEE)

    public void offer (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.OFFER,
          dimension
        )
      );

    }

    /// Emits a promise sign from this agent.
    ///
    /// @param dimension the perspective (PROMISER or PROMISEE)

    public void promise (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.PROMISE,
          dimension
        )
      );

    }

    /// Emits a retract sign from this agent.
    ///
    /// @param dimension the perspective (PROMISER or PROMISEE)

    public void retract (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.RETRACT,
          dimension
        )
      );

    }

    /// Signals an agent coordination event by composing sign and dimension.
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

    /// Emits a validate sign from this agent.
    ///
    /// @param dimension the perspective (PROMISER or PROMISEE)

    public void validate (
      @NotNull final Dimension dimension
    ) {

      pipe.emit (
        SIGNALS.get (
          Sign.VALIDATE,
          dimension
        )
      );

    }

  }

  /// The [Signal] record represents an agent promise signal composed of a sign and dimension.
  ///
  /// Signals are the composition of Sign (what promise operation) and Dimension (from whose perspective),
  /// enabling observation of agent coordination from both promiser and promisee perspectives.
  ///
  /// @param sign      the promise operation classification
  /// @param dimension the perspective from which the signal is emitted (promiser or promisee)

  @Queued
  @Provided
  public record Signal(
    Sign sign,
    Dimension dimension
  ) implements Serventis.Signal < Sign, Dimension > { }

}
