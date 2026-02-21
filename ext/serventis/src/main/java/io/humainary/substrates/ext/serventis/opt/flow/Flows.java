// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.opt.flow;

import io.humainary.substrates.ext.serventis.api.Serventis;
import io.humainary.substrates.ext.serventis.sdk.SignalSet;

/// # Flows API
///
/// The `Flows` API provides a structured framework for observing data movement through
/// staged processing systems. It enables emission of semantic signals representing
/// successful and failed transitions at entry (ingress), processing (transit), and
/// exit (egress) stages, making data flow behavior observable for understanding
/// system dynamics, bottlenecks, and failure patterns.
///
/// ## Purpose
///
/// This API enables systems to emit **flow transition signals** representing the
/// success or failure of data movement through staged systems. By modeling flow
/// outcomes at each stage as observable signs, it enables pattern recognition for
/// throughput analysis, bottleneck detection, failure localization, and capacity
/// planning across the flow lifecycle.
///
/// ## Design Philosophy
///
/// Flows represents the movement of items/data/requests through a staged system with
/// three distinct phases:
///
/// - **INGRESS**: Entry point where items enter the system
/// - **TRANSIT**: Processing phase where items move through the system
/// - **EGRESS**: Exit point where items leave the system
///
/// Each stage can succeed or fail, creating a matrix of observable outcomes:
///
/// | Sign    | INGRESS  | TRANSIT | EGRESS |
/// |---------|----------|---------|--------|
/// | SUCCESS | entered  | moved   | exited |
/// | FAIL    | refused  | dropped | failed |
///
/// ## Important: Observability vs Implementation
///
/// This API is for **observing flow transitions**, not implementing data pipelines.
/// When your system processes data through stages (message queues, data pipelines,
/// request handlers), use this API to emit corresponding observability signals
/// about stage outcomes. Observer agents can then reason about flow patterns,
/// detect bottlenecks, and localize failures without coupling to your implementation.
///
/// **Example**: When data successfully enters your system, call `flow.success(INGRESS)`.
/// When processing fails mid-stream, call `flow.fail(TRANSIT)`. These signals enable
/// meta-observability of flow behavior across your staged processing system.
///
/// ## Key Concepts
///
/// - **Flow**: A named subject that emits signals describing stage transitions
/// - **Sign**: The outcome of a stage transition: `SUCCESS` or `FAIL`
/// - **Dimension**: The stage where the transition occurred: `INGRESS`, `TRANSIT`, or `EGRESS`
/// - **Signal**: The composition of Sign x Dimension representing a stage outcome
///
/// ## Signs and Semantics
///
/// | Sign      | Description                                    | Category |
/// |-----------|------------------------------------------------|----------|
/// | `SUCCESS` | Item successfully transitioned through stage   | Outcome  |
/// | `FAIL`    | Item failed to transition through stage        | Outcome  |
///
/// ## Dimensions and Semantics
///
/// | Dimension | Description                                    | Stage    |
/// |-----------|------------------------------------------------|----------|
/// | `INGRESS` | Entry point - item entering the system         | Entry    |
/// | `TRANSIT` | Processing phase - item moving through system  | Middle   |
/// | `EGRESS`  | Exit point - item leaving the system           | Exit     |
///
/// ## Signal Matrix
///
/// The combination of signs and dimensions creates the following semantic signals:
///
/// - **SUCCESS + INGRESS = entered**: Item successfully entered the system
/// - **SUCCESS + TRANSIT = moved**: Item successfully moved through processing
/// - **SUCCESS + EGRESS = exited**: Item successfully exited the system
/// - **FAIL + INGRESS = refused**: Item was refused entry to the system
/// - **FAIL + TRANSIT = dropped**: Item was dropped during processing
/// - **FAIL + EGRESS = failed**: Item failed to exit the system
///
/// ## Use Cases
///
/// - Message queue processing (enqueue, process, dequeue)
/// - Data pipeline stages (ingest, transform, output)
/// - Request processing (receive, handle, respond)
/// - Stream processing (source, operators, sink)
/// - Workflow transitions (start, execute, complete)
///
/// ## Relationship to Other APIs
///
/// `Flows` complements other Serventis APIs:
///
/// - **Pipelines API**: Pipelines models processing operations; Flows models stage transitions
/// - **Queues API**: Queue operations (ENQUEUE/DEQUEUE) may emit corresponding Flow signals
/// - **Valves API**: Valve DENY may correlate with Flow FAIL at INGRESS
/// - **Statuses API**: High FAIL rates at any stage inform Status conditions (DEGRADED)
/// - **Situations API**: Sustained failures → WARNING or CRITICAL situations
///
/// ## Usage Patterns
///
/// ### Message Queue Processing
///
/// ```java
/// var flow = circuit.conduit(Flows::composer)
///   .percept(cortex.name("queue.orders"));
///
/// // Message received
/// if (queue.offer(message)) {
///   flow.success(INGRESS);  // entered
/// } else {
///   flow.fail(INGRESS);     // refused
/// }
///
/// // Message processed
/// try {
///   process(message);
///   flow.success(TRANSIT);  // moved
/// } catch (Exception e) {
///   flow.fail(TRANSIT);     // dropped
/// }
///
/// // Message delivered
/// if (deliver(message)) {
///   flow.success(EGRESS);   // exited
/// } else {
///   flow.fail(EGRESS);      // failed
/// }
/// ```
///
/// ### Data Pipeline
///
/// ```java
/// var flow = circuit.conduit(Flows::composer)
///   .percept(cortex.name("pipeline.etl"));
///
/// // Data ingestion stage
/// flow.success(INGRESS);    // data ingested
///
/// // Transformation stage
/// if (transformSucceeded) {
///   flow.success(TRANSIT);  // data transformed
/// } else {
///   flow.fail(TRANSIT);     // transformation failed
/// }
///
/// // Output stage
/// flow.success(EGRESS);     // data written to destination
/// ```
///
/// ### Request Handler
///
/// ```java
/// var flow = circuit.conduit(Flows::composer)
///   .percept(cortex.name("http.requests"));
///
/// // Request received
/// flow.success(INGRESS);    // request accepted
///
/// // Request processing
/// try {
///   var result = handler.handle(request);
///   flow.success(TRANSIT);  // processing succeeded
///
///   // Response sent
///   flow.success(EGRESS);   // response delivered
/// } catch (Exception e) {
///   flow.fail(TRANSIT);     // processing failed
/// }
/// ```
///
/// ## Performance Considerations
///
/// Flow sign emissions operate at the frequency of data movement through your system:
/// - **High-throughput systems**: 100K-1M+ signals/sec
/// - **Request handlers**: Per-request emission
/// - **Batch pipelines**: Per-batch or sampled emission
///
/// Zero-allocation SignalSet caching ensures ~10-20ns emission cost.
/// Signs flow asynchronously through the circuit's event queue.
/// Consider sampling strategies for extremely high-throughput systems.
///
/// ## Semiotic Ascent: Flows -> Status -> Situation
///
/// Flow signs translate upward into universal languages:
///
/// ### Flows -> Status Translation
///
/// | Flow Pattern              | Status    | Rationale                       |
/// |---------------------------|-----------|----------------------------------|
/// | High SUCCESS across all   | HEALTHY   | Flow moving smoothly             |
/// | High FAIL at INGRESS      | SATURATED | System refusing incoming work    |
/// | High FAIL at TRANSIT      | DEGRADED  | Processing failures occurring    |
/// | High FAIL at EGRESS       | BLOCKED   | Output failures occurring        |
/// | FAIL rate increasing      | DEGRADING | System health declining          |
///
/// ### Status -> Situation Assessment
///
/// | Status Pattern            | Situation | Example                          |
/// |---------------------------|-----------|----------------------------------|
/// | SATURATED (sustained)     | CAPACITY  | Insufficient ingress capacity    |
/// | DEGRADED (processing)     | OUTAGE    | Processing pipeline broken       |
/// | BLOCKED (output)          | OUTAGE    | Downstream unavailable           |
///
/// This hierarchical meaning-making enables cross-domain reasoning: observers understand
/// flow behavior's impact on system health and service availability without needing to
/// understand your specific pipeline implementation.
///
/// @author William David Louth
/// @since 1.0

public final class Flows
  implements Serventis {

  private Flows () { }

  /// A static composer function for creating Flow instruments.
  ///
  /// This method can be used as a method reference with conduits as follows:
  ///
  /// Example usage:
  /// ```java
  /// final var cortex = Substrates.cortex();
  /// var flow = circuit.conduit(Flows::composer).percept(cortex.name("pipeline.orders"));
  /// ```
  ///
  /// @param channel the channel from which to create the flow
  /// @return a new Flow instrument for the specified channel
  /// @throws NullPointerException if the channel param is `null`

  @New
  @NotNull
  public static Flow composer (
    @NotNull final Channel < ? super Signal > channel
  ) {

    return
      new Flow (
        channel.pipe ()
      );

  }

  /// A [Sign] represents the outcome of a stage transition in a flow.
  ///
  /// Signs classify whether items successfully transitioned through a stage
  /// or failed to do so. When combined with a [Dimension], they form a complete
  /// picture of flow behavior at each stage.
  ///
  /// ## Sign Categories
  ///
  /// - **SUCCESS**: Item successfully transitioned through the stage
  /// - **FAIL**: Item failed to transition through the stage

  public enum Sign
    implements Serventis.Sign {

    /// Indicates successful transition through a stage.
    ///
    /// SUCCESS represents an item that successfully moved through the stage.
    /// High SUCCESS rates indicate healthy flow. The ratio of SUCCESS to FAIL
    /// at each stage reveals stage-specific reliability.
    ///
    /// **At INGRESS**: Item entered the system (accepted, queued)
    /// **At TRANSIT**: Item moved through processing (transformed, handled)
    /// **At EGRESS**: Item exited the system (delivered, completed)
    ///
    /// **Pattern analysis**: Sustained SUCCESS -> healthy flow, SUCCESS rate declining -> degradation

    SUCCESS,

    /// Indicates failed transition through a stage.
    ///
    /// FAIL represents an item that could not transition through the stage.
    /// High FAIL rates indicate problems at that stage. The location of failures
    /// (INGRESS, TRANSIT, EGRESS) reveals where bottlenecks or errors occur.
    ///
    /// **At INGRESS**: Item refused entry (rejected, throttled)
    /// **At TRANSIT**: Item dropped during processing (error, timeout)
    /// **At EGRESS**: Item failed to exit (delivery failure, output error)
    ///
    /// **Pattern analysis**: High FAIL at INGRESS -> saturation, FAIL at TRANSIT -> processing issues

    FAIL

  }

  /// The [Dimension] enum classifies the stage of flow where a transition occurred.
  ///
  /// Every sign in the Flows API has three dimensions, representing the three
  /// fundamental stages of data movement through a system. This tri-stage model
  /// enables tracking of flow health at entry, processing, and exit points.
  ///
  /// ## The Three Dimensions
  ///
  /// | Dimension | Stage      | Description                               |
  /// |-----------|------------|-------------------------------------------|
  /// | INGRESS   | Entry      | Item entering the system                  |
  /// | TRANSIT   | Processing | Item moving through the system            |
  /// | EGRESS    | Exit       | Item leaving the system                   |
  ///
  /// ## INGRESS vs TRANSIT vs EGRESS
  ///
  /// **INGRESS** signals represent **entry point transitions**:
  /// - Where items first enter your system boundary
  /// - Examples: queue enqueue, request received, data ingested
  /// - Failures here indicate admission/capacity problems
  ///
  /// **TRANSIT** signals represent **processing transitions**:
  /// - Where items move through internal processing stages
  /// - Examples: transformation, handling, routing
  /// - Failures here indicate processing/logic problems
  ///
  /// **EGRESS** signals represent **exit point transitions**:
  /// - Where items leave your system boundary
  /// - Examples: queue dequeue, response sent, data written
  /// - Failures here indicate output/delivery problems

  public enum Dimension
    implements Category {

    /// Signals emitted at the entry point of a flow.
    ///
    /// **INGRESS** is used when tracking items entering the system boundary.
    /// Success means the item was accepted; failure means it was refused.
    ///
    /// **Mental model**: "Did the item get into the system?"
    /// **Examples**: Queue accepts message, API accepts request, pipeline receives data
    /// **Failure implications**: Capacity issues, rate limiting, validation failures

    INGRESS,

    /// Signals emitted during processing/movement of a flow.
    ///
    /// **TRANSIT** is used when tracking items moving through internal stages.
    /// Success means the item was processed; failure means it was dropped.
    ///
    /// **Mental model**: "Did the item get processed correctly?"
    /// **Examples**: Message processed, request handled, data transformed
    /// **Failure implications**: Processing errors, timeouts, resource issues

    TRANSIT,

    /// Signals emitted at the exit point of a flow.
    ///
    /// **EGRESS** is used when tracking items leaving the system boundary.
    /// Success means the item was delivered; failure means it could not exit.
    ///
    /// **Mental model**: "Did the item get out of the system?"
    /// **Examples**: Response sent, message delivered, data written
    /// **Failure implications**: Downstream unavailable, output errors, delivery failures

    EGRESS

  }

  /// The `Flow` class represents a named, observable flow from which signals are emitted.
  ///
  /// A flow is a subject percept (instrument) that emits signals composed of Sign x Dimension,
  /// enabling observation of data movement through staged systems. Flow signals make
  /// stage transitions observable, enabling pattern recognition for throughput analysis,
  /// bottleneck detection, and failure localization.
  ///
  /// ## Usage
  ///
  /// Use dimension-parameterized methods for all flow stage transitions:
  /// ```java
  /// flow.success(INGRESS);   // Item entered
  /// flow.success(TRANSIT);   // Item processed
  /// flow.success(EGRESS);    // Item exited
  ///
  /// flow.fail(INGRESS);      // Item refused
  /// flow.fail(TRANSIT);      // Item dropped
  /// flow.fail(EGRESS);       // Item failed to exit
  /// ```
  ///
  /// Flows provide semantic methods that reflect stage outcomes,
  /// making code more expressive and self-documenting.

  @Queued
  @Provided
  public static final class Flow
    implements Signaler < Sign, Dimension > {

    private static final SignalSet < Sign, Dimension, Signal > SIGNALS =
      new SignalSet <> (
        Sign.class,
        Dimension.class,
        Signal::new
      );

    private final Pipe < ? super Signal > pipe;

    private Flow (
      final Pipe < ? super Signal > pipe
    ) {

      this.pipe = pipe;

    }

    /// Emits a fail sign from this flow.
    ///
    /// @param dimension the stage (INGRESS, TRANSIT, or EGRESS)

    public void fail (
      @NotNull final Dimension dimension
    ) {

      signal (
        Sign.FAIL,
        dimension
      );

    }

    /// Signals a flow event by composing sign and dimension.
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

    /// Emits a success sign from this flow.
    ///
    /// @param dimension the stage (INGRESS, TRANSIT, or EGRESS)

    public void success (
      @NotNull final Dimension dimension
    ) {

      signal (
        Sign.SUCCESS,
        dimension
      );

    }

  }

  /// The [Signal] record represents a flow signal composed of a sign and dimension.
  ///
  /// Signals are the composition of Sign (what happened) and Dimension (where it happened),
  /// enabling observation of flow transitions from entry through processing to exit.
  ///
  /// @param sign      the flow outcome classification (SUCCESS or FAIL)
  /// @param dimension the stage where the transition occurred (INGRESS, TRANSIT, or EGRESS)

  @Queued
  @Provided
  public record Signal(
    Sign sign,
    Dimension dimension
  ) implements Serventis.Signal < Sign, Dimension > { }

}
