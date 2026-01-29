// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.opt.role;

import io.humainary.substrates.ext.serventis.api.Serventis;

import static io.humainary.substrates.ext.serventis.opt.role.Actors.Sign.*;

/// # Actors API
///
/// The `Actors` API provides a structured framework for observing speech acts
/// performed by actors within conversational coordination. Grounded in **Speech Act
/// Theory** (Austin, Searle), this API captures communicative acts as they occur in
/// dialogue between humans and machines - whether person-to-person, person-to-AI,
/// AI-to-AI, or any combination.
///
/// ## Purpose
///
/// Actors - human or machine - coordinate through conversation. This API enables
/// observation of **speech acts**: the communicative actions actors perform through
/// utterances. When you ask a question, make a request, deliver work, or acknowledge
/// understanding, you're performing a speech act. This API makes those acts observable,
/// enabling reasoning about conversational patterns, coordination effectiveness, and
/// dialogue quality.
///
/// ## Important: Reporting vs Implementation
///
/// This API is for **reporting speech acts**, not implementing actors.
/// When an actor (human or AI) performs a communicative act - asks a question,
/// makes a claim, delivers work - use this API to emit observability signs about
/// those acts. Meta-level observers can then reason about dialogue patterns,
/// coordination effectiveness, and conversational dynamics.
///
/// **Example**: In a human-AI collaboration where a person requests an API design
/// and the AI delivers it, both parties emit signs: the human `request()`, the AI
/// `acknowledge()`, then `promise()`, then `deliver()`. Observers track this arc
/// to measure collaboration effectiveness.
///
/// ## Key Concepts
///
/// - **Actor**: Any entity (human or machine) that performs speech acts
/// - **Speech Act**: A communicative action with illocutionary force
/// - **Sign**: The type of speech act being performed
/// - **Dialogue**: Sequences of speech acts between coordinating actors
///
/// ## Conversational Coordination
///
/// Actors coordinate through structured dialogues - sequences of speech acts:
///
/// ### Question-Answer Pattern
/// ```
/// Human -> ASK("What APIs should we create?")
/// AI -> EXPLAIN(computational constructs analysis)
/// AI -> AFFIRM(Transactions are fundamental)
/// Human -> ACKNOWLEDGE
/// ```
///
/// ### Request-Delivery Pattern
/// ```
/// Human -> REQUEST(write Cache API)
/// AI -> ACKNOWLEDGE
/// AI -> PROMISE(will deliver)
/// AI -> DELIVER(presents Cache API)
/// Human -> ACKNOWLEDGE
/// ```
///
/// ### Correction-Clarification Pattern
/// ```
/// AI -> AFFIRM(units enable aggregation)
/// Human -> DENY(no units needed)
/// AI -> CLARIFY(so pure signs only?)
/// Human -> ACKNOWLEDGE
/// ```
///
/// ### Collaborative Refinement Pattern
/// ```
/// Human -> REQUEST(design feature)
/// AI -> EXPLAIN(initial approach)
/// Human -> DENY(different direction needed)
/// Human -> CLARIFY(specific requirements)
/// AI -> ACKNOWLEDGE
/// AI -> DELIVER(refined design)
/// Human -> ACKNOWLEDGE
/// ```
///
/// ## Signs and Semantics
///
/// ### Questions & Inquiry (1)
///
/// | Sign        | Description                                               |
/// |-------------|-----------------------------------------------------------|
/// | `ASK`       | Seeking information, clarification, or guidance           |
///
/// ASK initiates information exchange. An actor asks when they need knowledge,
/// clarification, or input from another actor. Questions drive dialogue forward
/// and signal knowledge gaps that others can address.
///
/// ### Assertions & Explanations (3)
///
/// | Sign        | Description                                               |
/// |-------------|-----------------------------------------------------------|
/// | `AFFIRM`    | Making a claim or judgment with confidence                |
/// | `EXPLAIN`   | Providing reasoning, elaboration, or rationale            |
/// | `REPORT`    | Conveying factual observations or findings                |
///
/// These signs convey information with different epistemological stances:
/// - **AFFIRM**: Strong commitment to truth of proposition
/// - **EXPLAIN**: Providing understanding through reasoning
/// - **REPORT**: Neutral conveyance of facts or observations
///
/// EXPLAIN is particularly important in human-AI dialogue, where reasoning
/// transparency enables trust and learning.
///
/// ### Coordination (3)
///
/// | Sign          | Description                                               |
/// |---------------|-----------------------------------------------------------|
/// | `REQUEST`     | Asking another actor to perform action (peer-level)       |
/// | `COMMAND`     | Directing another actor to act (authority-level)          |
/// | `ACKNOWLEDGE` | Confirming receipt, understanding, or agreement           |
///
/// These signs manage coordination flow:
/// - **REQUEST**: Initiates coordination without presuming authority
/// - **COMMAND**: Directs action with authority (manager to employee, user to system)
/// - **ACKNOWLEDGE**: Closes communication loops, confirms understanding
///
/// ACKNOWLEDGE is critical - without it, speakers don't know if they were heard.
///
/// ### Disagreement & Refinement (2)
///
/// | Sign        | Description                                               |
/// |-------------|-----------------------------------------------------------|
/// | `DENY`      | Disagreeing with or correcting a proposition              |
/// | `CLARIFY`   | Refining, specifying, or disambiguating intent            |
///
/// Dialogue requires negotiation:
/// - **DENY**: Signals disagreement, initiates negotiation or correction
/// - **CLARIFY**: Refines understanding, resolves ambiguity
///
/// These enable course correction and convergence toward shared understanding.
///
/// ### Commitment & Delivery (2)
///
/// | Sign        | Description                                               |
/// |-------------|-----------------------------------------------------------|
/// | `PROMISE`   | Committing to perform future action or deliver work       |
/// | `DELIVER`   | Presenting completed work or fulfilled commitment         |
///
/// The commitment arc is fundamental to collaboration:
/// - **PROMISE**: Creates obligation, enables coordination around future work
/// - **DELIVER**: Fulfills commitment, presents artifact or outcome
///
/// Tracking PROMISE → DELIVER enables measurement of commitment fulfillment,
/// a key metric for actor reliability.
///
/// ## Human-AI Dialogue Example
///
/// A typical interaction between human and AI demonstrates the full sign vocabulary:
///
/// ```
/// Human:  ASK("What about packet networks?")
/// AI:     EXPLAIN(network semantic spaces)
/// AI:     AFFIRM(Routers API would be valuable)
/// Human:  ACKNOWLEDGE
///
/// Human:  REQUEST(write Routers API)
/// AI:     ACKNOWLEDGE
/// AI:     PROMISE(will deliver)
/// AI:     DELIVER(presents Routers.java)
/// Human:  ACKNOWLEDGE
///
/// Human:  DENY(Router not right term)
/// Human:  ASK(is Actor better?)
/// AI:     EXPLAIN(terminology analysis)
/// AI:     AFFIRM(Actor is preferable)
/// Human:  ACKNOWLEDGE
///
/// Human:  COMMAND(rewrite with Actor)
/// AI:     ACKNOWLEDGE
/// AI:     CLARIFY(same 11 signs?)
/// Human:  ACKNOWLEDGE
/// AI:     DELIVER(revised Actors API)
/// ```
///
/// Observers tracking this dialogue can measure:
/// - Question response latency (ASK → EXPLAIN/AFFIRM)
/// - Commitment fulfillment (PROMISE → DELIVER)
/// - Correction cycles (DENY → CLARIFY → ACKNOWLEDGE)
/// - Overall coordination efficiency
///
/// ## Relationship to Other APIs
///
/// `Actors` signs inform ecosystem-level observability:
///
/// - **Statuses API**: Actors AFFIRM monitor conditions about system state
/// - **Counters, Gauges, Caches**: Actors REPORT observations from instruments
/// - **Resources API**: Actors REQUEST actions on system components
/// - Meta-percepts observe dialogue patterns to detect coordination failures,
///   communication breakdowns, unfulfilled commitments, and conversational quality
///
/// ## Usage Example
///
/// ```java
/// final var cortex = Substrates.cortex();
/// var human = circuit.conduit(Actors::composer).percept(cortex.name("user.william"));
/// var ai = circuit.conduit(Actors::composer).percept(cortex.name("assistant.claude"));
///
/// // Human initiates collaboration
/// human.ask();        // "How should we design this?"
///
/// // AI responds with reasoning
/// ai.explain();       // Provides analysis
/// ai.affirm();        // Makes recommendation
///
/// // Human requests work
/// human.request();    // "Please implement that"
///
/// // AI commits and delivers
/// ai.acknowledge();   // Confirms request
/// ai.promise();       // Commits to deliver
/// // ... work happens ...
/// ai.deliver();       // Presents completed work
///
/// // Human confirms
/// human.acknowledge(); // "Perfect"
///
/// // Meta-observer analyzes:
/// // - Time from REQUEST to DELIVER (work latency)
/// // - PROMISE fulfillment rate (reliability)
/// // - Explanation clarity (communication quality)
/// ```
///
/// ## Performance Considerations
///
/// Actor speech act emissions operate at conversational timescales (seconds to minutes)
/// rather than computational timescales (microseconds). Signs flow asynchronously
/// through the circuit's event queue, adding minimal overhead. Unlike high-frequency
/// instruments (Counters, Routers at 10M-50M Hz), actor communication is lower-rate
/// but higher semantic density - each sign carries significant meaning about
/// coordination state and conversational dynamics.
///
/// Sign emissions leverage **zero-allocation enum emission** with ~10-20ns cost for
/// non-transit emits. The streamlined vocabulary (11 signs vs 24 in earlier designs)
/// reflects focus on practical conversational coordination rather than exhaustive
/// speech act taxonomy.
///
/// @author William David Louth
/// @since 1.0

public final class Actors
  implements Serventis {

  private Actors () { }

  /// A static composer function for creating Actor instruments.
  ///
  /// This method can be used as a method reference with conduits as follows:
  ///
  /// Example usage:
  /// ```java
  /// final var cortex = Substrates.cortex();
  /// var human = circuit.conduit(Actors::composer).percept(cortex.name("user.william"));
  /// var ai = circuit.conduit(Actors::composer).percept(cortex.name("assistant.claude"));
  /// ```
  ///
  /// @param channel the channel from which to create the actor
  /// @return a new Actor instrument for the specified channel
  /// @throws NullPointerException if the channel param is `null`

  @New
  @NotNull
  public static Actor composer (
    @NotNull final Channel < ? super Sign > channel
  ) {

    return
      new Actor (
        channel.pipe ()
      );

  }

  /// A [Sign] represents a speech act performed by an actor in conversation.
  ///
  /// These signs capture the illocutionary force of communicative acts - what
  /// the actor intends to accomplish through the utterance. Whether asking a
  /// question, making a claim, requesting action, or delivering work, each sign
  /// represents a distinct type of conversational move.

  public enum Sign
    implements Serventis.Sign {

    /// Seeking information, clarification, or guidance.
    ///
    /// ASK initiates information exchange. Use when an actor needs knowledge,
    /// clarification, or input from another actor. Questions drive dialogue
    /// forward and signal knowledge gaps requiring address.

    ASK,

    /// Making a claim or judgment with confidence.
    ///
    /// AFFIRM commits the actor to the truth of a proposition. Use for confident
    /// interpretations, recommendations, or conclusions. Strong assertive stance.

    AFFIRM,

    /// Providing reasoning, elaboration, or rationale.
    ///
    /// EXPLAIN offers understanding through reasoning chains. Critical in human-AI
    /// dialogue for transparency. Use when providing "why" behind assertions,
    /// walking through logic, or building shared understanding.

    EXPLAIN,

    /// Conveying factual observations or findings.
    ///
    /// REPORT neutrally conveys facts, observations, or findings without strong
    /// interpretive commitment. More objective than ASSERT. Use for presenting
    /// data, observations, or discovered information.

    REPORT,

    /// Asking another actor to perform action at peer level.
    ///
    /// REQUEST initiates coordination without presuming authority. The recipient
    /// may decline or negotiate. Use for peer-to-peer collaboration where
    /// cooperation is voluntary.

    REQUEST,

    /// Directing another actor to act with authority.
    ///
    /// COMMAND directs action, presuming authority relationship. Use when actor
    /// has legitimate authority over recipient (user over system, manager over
    /// employee). Presumes compliance.

    COMMAND,

    /// Confirming receipt, understanding, or agreement.
    ///
    /// ACKNOWLEDGE closes communication loops. Critical for coordination - without
    /// acknowledgment, speakers don't know if they were heard or understood. Use
    /// to confirm receipt of requests, understanding of explanations, or agreement
    /// with propositions.

    ACKNOWLEDGE,

    /// Disagreeing with or correcting a proposition.
    ///
    /// DENY signals disagreement or correction. Initiates negotiation or course
    /// correction. Use when rejecting assertions, correcting misunderstandings,
    /// or indicating different direction needed.

    DENY,

    /// Refining, specifying, or disambiguating intent.
    ///
    /// CLARIFY resolves ambiguity or refines understanding. Use when previous
    /// utterances were unclear, require specification, or need disambiguation.
    /// Enables convergence toward shared understanding.

    CLARIFY,

    /// Committing to perform future action or deliver work.
    ///
    /// PROMISE creates obligation enabling coordination around future work.
    /// Other actors can depend on this commitment. Use when undertaking work
    /// that others coordinate around. Tracking PROMISE → DELIVER enables
    /// reliability measurement.

    PROMISE,

    /// Presenting completed work or fulfilled commitment.
    ///
    /// DELIVER fulfills commitment and presents artifact or outcome. The completion
    /// speech act. Use when presenting completed work, finished deliverables, or
    /// fulfilled promises. Often follows PROMISE.

    DELIVER

  }

  /// The [Actor] class represents a named, observable actor from which speech act
  /// signs are emitted.
  ///
  /// Actors perform communicative acts with illocutionary force. Whether human or
  /// machine, actors coordinate through conversation. Each method represents a type
  /// of speech act the actor can perform.
  ///
  /// ## Usage
  ///
  /// Use speech act methods: `actor.ask()`, `actor.affirm()`, `actor.request()`, `actor.deliver()`
  ///
  /// Actors provide semantic methods for reporting communicative acts.

  @Queued
  @Provided
  public static final class Actor
    implements Signer < Sign > {

    private final Pipe < ? super Sign > pipe;

    private Actor (
      final Pipe < ? super Sign > pipe
    ) {

      this.pipe = pipe;

    }

    /// Emits an acknowledge sign from this actor.

    public void acknowledge () {

      pipe.emit (
        ACKNOWLEDGE
      );

    }

    /// Emits an affirm sign from this actor.

    public void affirm () {

      pipe.emit (
        AFFIRM
      );

    }

    /// Emits an ask sign from this actor.

    public void ask () {

      pipe.emit (
        ASK
      );

    }

    /// Emits a clarify sign from this actor.

    public void clarify () {

      pipe.emit (
        CLARIFY
      );

    }

    /// Emits a command sign from this actor.

    public void command () {

      pipe.emit (
        COMMAND
      );

    }

    /// Emits a deliver sign from this actor.

    public void deliver () {

      pipe.emit (
        DELIVER
      );

    }

    /// Emits a deny sign from this actor.

    public void deny () {

      pipe.emit (
        DENY
      );

    }

    /// Emits an explain sign from this actor.

    public void explain () {

      pipe.emit (
        EXPLAIN
      );

    }

    /// Emits a promise sign from this actor.

    public void promise () {

      pipe.emit (
        PROMISE
      );

    }

    /// Emits a report sign from this actor.

    public void report () {

      pipe.emit (
        REPORT
      );

    }

    /// Emits a request sign from this actor.

    public void request () {

      pipe.emit (
        REQUEST
      );

    }

    /// Signs a speech act event.
    ///
    /// @param sign the sign to make

    @Override
    public void sign (
      @NotNull final Sign sign
    ) {

      pipe.emit (
        sign
      );

    }

  }

}
