// Copyright (c) 2025 William David Louth
package io.humainary.substrates.ext.serventis.opt.data;

import io.humainary.substrates.ext.serventis.api.Serventis;

import static io.humainary.substrates.ext.serventis.opt.data.Queues.Sign.*;

/// # Queues API
///
/// The `Queues` API provides a structured and minimal interface for observing interactions
/// with queue-like systems. It enables systems to emit **semantic signals** representing
/// key queue operations such as enqueue, dequeue, and boundary violations.
///
/// ## Purpose
///
/// This API is designed to support **observability and reasoning** in systems that use
/// queues as flow-control or communication mechanisms. By modeling queue interactions
/// as composable signals, it enables introspection of system dynamics, pressure points,
/// and resource utilization without coupling to specific implementation details.
///
/// ## Important: Reporting vs Implementation
///
/// This API is for **reporting queue operation semantics**, not implementing queues.
/// If you have an actual queue implementation (BlockingQueue, message broker, task queue, etc.),
/// use this API to emit observability signals about operations performed on it.
/// Observer agents can then reason about throughput patterns, backpressure conditions,
/// and capacity dynamics without coupling to your implementation details.
///
/// **Example**: When your BlockingQueue successfully enqueues a task, you would call
/// `queue.enqueue()` to emit a signal. If the queue is full and rejects the operation,
/// call `queue.overflow()`. The signals enable meta-observability: observing the observability
/// instrumentation itself to understand queue behavior and system flow dynamics.
///
/// ## Key Concepts
///
/// - **Queue**: A named subject that emits signs describing operations performed against it
/// - **Sign**: An enumeration of distinct interaction types: `ENQUEUE`, `DEQUEUE`, `OVERFLOW`, `UNDERFLOW`
///
/// ## Signs and Semantics
///
/// | Sign        | Description                                               |
/// |-------------|-----------------------------------------------------------|
/// | `ENQUEUE`   | An item was added to the queue                            |
/// | `DEQUEUE`   | An item was removed from the queue                        |
/// | `OVERFLOW`  | An `ENQUEUE` failed due to capacity                       |
/// | `UNDERFLOW` | A `DEQUEUE` failed due to emptiness                       |
///
/// ## Use Cases
///
/// - Modeling traffic flow in bounded queues or backpressure systems
/// - Instrumenting messaging systems, task queues, or pipelines
/// - Diagnosing latency and load in producer-consumer patterns
/// - Building higher-level abstractions such as buffers, schedulers, or mailboxes
///
/// ## Relationship to Other APIs
///
/// `Queues` signals can inform higher-level abstractions:
///
/// - **Resources API**: Queue capacity can be modeled as a resource with GRANT/DENY signals
/// - **Statuses API**: Queue overflow patterns may indicate DEGRADED or DIVERGING conditions
/// - **Services API**: Queue operations can trigger DELAY or REJECT service signals
///
/// ## Performance Considerations
///
/// Queue sign emissions are designed for high-frequency operation (10M-50M Hz).
/// Zero-allocation enum emission with ~10-20ns cost for non-transit emits.
/// Signs flow asynchronously through the circuit's event queue.
///
/// @author William David Louth
/// @since 1.0

public final class Queues
  implements Serventis {

  private Queues () { }

  /// A static composer function for creating Queue instruments.
  ///
  /// This method can be used as a method reference with conduits as follows:
  ///
  /// Example usage:
  /// ```java
  /// final var cortex = Substrates.cortex();
  /// var queue = circuit.conduit(Queues::composer).percept(cortex.name("worker.queue"));
  /// ```
  ///
  /// @param channel the channel from which to create the queue
  /// @return a new Queue instrument for the specified channel
  /// @throws NullPointerException if the channel param is `null`

  @New
  @NotNull
  public static Queue composer (
    @NotNull final Channel < ? super Sign > channel
  ) {

    return
      new Queue (
        channel.pipe ()
      );

  }


  /// A [Sign] represents the kind of action being observed in a queue interaction.
  ///
  /// These signs form complementary pairs representing normal operations (ENQUEUE/DEQUEUE)
  /// and boundary violations (OVERFLOW/UNDERFLOW). The signs enable reasoning about
  /// queue health, capacity utilization, and flow control effectiveness.

  public enum Sign
    implements Serventis.Sign {

    /// Indicates an item was successfully added to the queue.
    ///
    /// This sign represents normal enqueue operations. High ENQUEUE rates indicate
    /// active producers. The ratio of ENQUEUE to DEQUEUE signals reveals queue fill rate.

    ENQUEUE,

    /// Indicates an item was successfully removed from the queue.
    ///
    /// This sign represents normal dequeue operations. High DEQUEUE rates indicate
    /// active consumers. Sustained DEQUEUE > ENQUEUE patterns indicate queue draining.

    DEQUEUE,

    /// Indicates the queue reached capacity and rejected an ENQUEUE operation.
    ///
    /// Overflow signals reveal backpressure conditions where producers are outpacing
    /// consumers or queue capacity is insufficient. Frequent overflows may indicate
    /// the need for capacity expansion, consumer scaling, or producer throttling.

    OVERFLOW,

    /// Indicates the queue was empty and could not satisfy a DEQUEUE operation.
    ///
    /// Underflow signals reveal starvation conditions where consumers are waiting
    /// for work. Frequent underflows may indicate oversized consumer pools, bursty
    /// production, or opportunities for consumer consolidation.

    UNDERFLOW

  }

  /// The [Queue] class represents a named, observable queue from which signs are emitted.
  ///
  /// ## Usage
  ///
  /// Use domain-specific methods: `queue.enqueue()`, `queue.dequeue()`, `queue.overflow()`
  ///
  /// Queues provide semantic methods for reporting queue operation events.

  @Queued
  @Provided
  public static final class Queue
    implements Signer < Sign > {

    private final Pipe < ? super Sign > pipe;

    private Queue (
      final Pipe < ? super Sign > pipe
    ) {

      this.pipe = pipe;

    }

    /// Emits a dequeue sign from this queue.

    public void dequeue () {

      pipe.emit (
        DEQUEUE
      );

    }

    /// Emits an enqueue sign from this queue.

    public void enqueue () {

      pipe.emit (
        ENQUEUE
      );

    }

    /// Emits an overflow sign from this queue.

    public void overflow () {

      pipe.emit (
        OVERFLOW
      );

    }

    /// Signs a queue event.
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

    /// Emits an underflow sign from this queue.

    public void underflow () {

      pipe.emit (
        UNDERFLOW
      );

    }

  }

}
