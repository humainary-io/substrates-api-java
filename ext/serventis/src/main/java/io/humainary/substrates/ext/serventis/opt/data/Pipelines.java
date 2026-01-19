// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.opt.data;

import io.humainary.substrates.ext.serventis.api.Serventis;

import static io.humainary.substrates.ext.serventis.opt.data.Pipelines.Sign.*;

/// # Pipelines API
///
/// The `Pipelines` API provides a structured framework for observing data pipeline
/// operations within processing systems. It enables emission of semantic signs that
/// describe stage execution, transformations, and flow control in pipeline architectures.
///
/// ## Purpose
///
/// This API enables systems to **observe** pipeline behavior through discrete sign emission.
/// Signs represent semantic events about pipeline stages - what operations are performed
/// as data flows through processing stages. Throughput and latency metrics are derived by
/// percepts through aggregation of sign streams.
///
/// ## Important: Reporting vs Implementation
///
/// This API is for **reporting pipeline processing semantics**, not implementing pipelines.
/// If you have actual pipeline infrastructure (Apache Beam, Flink, Spark Streaming,
/// Spring Cloud Stream), use this API to emit observability signs about stage operations.
/// Observer agents can then reason about processing patterns, detect bottlenecks, and derive
/// performance metrics without coupling to your implementation details.
///
/// **Example**: When your pipeline transforms data, call `pipeline.transform()` to emit
/// a sign that observers can process. The sign enables meta-observability: understanding
/// processing patterns, bottlenecks, and flow control across your pipeline stages.
///
/// ## Key Concepts
///
/// - **Pipeline**: A named subject that emits signs describing stage operations
/// - **Sign**: An enumeration of distinct pipeline processing operations
///
/// ## Data Flow Through Pipeline
///
/// Data flows through pipelines with distinct operations:
///
/// ```
/// Data Flow: INPUT → OUTPUT
///       ↓
/// Processing: TRANSFORM → FILTER → AGGREGATE
///       ↓
/// Flow Control: BUFFER → BACKPRESSURE → OVERFLOW
///       ↓
/// Progress: CHECKPOINT → WATERMARK
///       ↓
/// Anomalies: LAG → SKIP
/// ```
///
/// ## Signs and Semantics
///
/// | Sign          | Description                                               |
/// |---------------|-----------------------------------------------------------|
/// | `INPUT`       | Pipeline received input data from upstream stage          |
/// | `OUTPUT`      | Pipeline sent output data to downstream stage             |
/// | `TRANSFORM`   | Pipeline transformed data (map, flatMap, enrich)          |
/// | `FILTER`      | Pipeline filtered data (removed from flow)                |
/// | `AGGREGATE`   | Pipeline aggregated data (window, group, reduce)          |
/// | `BUFFER`      | Pipeline buffered data for flow control                   |
/// | `BACKPRESSURE`| Pipeline signaled upstream to slow production             |
/// | `OVERFLOW`    | Pipeline buffer exceeded capacity                         |
/// | `CHECKPOINT`  | Pipeline reached progress checkpoint                      |
/// | `WATERMARK`   | Pipeline advanced event-time watermark                    |
/// | `LAG`         | Pipeline detected processing lag                          |
/// | `SKIP`        | Pipeline skipped/dropped data anomalously                 |
///
/// ## Semantic Distinctions
///
/// - **INPUT vs OUTPUT**: Stage receives data vs stage sends data
/// - **FILTER vs SKIP**: Intentional removal vs anomalous dropping
/// - **BUFFER vs BACKPRESSURE**: Absorb bursts vs slow source
/// - **CHECKPOINT vs WATERMARK**: Processing progress vs event-time progress
/// - **TRANSFORM types**: Map (1:1), FlatMap (1:N), Enrich (augment) all use TRANSFORM
///
/// ## Use Cases
///
/// - ETL/ELT pipeline monitoring
/// - Stream processing pipeline observability (Kafka Streams, Flink)
/// - Batch processing pipeline tracking (Spark, Dataflow)
/// - ML pipeline stage monitoring (feature engineering, training)
/// - Data quality pipeline observation
///
/// ## Relationship to Other APIs
///
/// `Pipelines` signs inform higher-level observability:
///
/// - **Queues API**: Pipeline buffers are implemented as queues
/// - **Tasks API**: Pipeline stages may execute as tasks
/// - **Statuses API**: High LAG or OVERFLOW patterns → DEGRADED condition
/// - **Situations API**: Pipeline bottlenecks → WARNING or CRITICAL situations
///
/// ## Usage Example
///
/// ```java
/// final var cortex = Substrates.cortex();
/// var pipeline = circuit.conduit(Pipelines::composer).percept(cortex.name("etl.customer.enrichment"));
///
/// // Data flow
/// pipeline.input();
/// pipeline.output();
///
/// // Processing stages
/// pipeline.transform();  // Enrich customer data
/// pipeline.filter();     // Remove invalid records
/// pipeline.aggregate();  // Group by region
///
/// // Flow control
/// pipeline.buffer();
/// pipeline.backpressure();
///
/// // Progress tracking
/// pipeline.checkpoint();
/// pipeline.watermark();
/// ```
///
/// ## Performance Considerations
///
/// Pipeline sign emissions are designed for high-frequency operation. Data pipelines
/// can process millions of records per second. Signs flow asynchronously through the
/// circuit to avoid impacting processing latency. Consider sampling strategies:
/// - Sample signs: Emit every Nth record processed
/// - Time-based: Emit aggregated counts every second
/// - Adaptive: Increase sampling during normal flow, emit all during anomalies
///
/// @author William David Louth
/// @since 1.0

public final class Pipelines
  implements Serventis {

  private Pipelines () { }

  /// A static composer function for creating Pipeline instruments.
  ///
  /// This method can be used as a method reference with conduits as follows:
  ///
  /// Example usage:
  /// ```java
  /// final var cortex = Substrates.cortex();
  /// var pipeline = circuit.conduit(Pipelines::composer).percept(cortex.name("etl.orders"));
  /// ```
  ///
  /// @param channel the channel from which to create the pipeline
  /// @return a new Pipeline instrument for the specified channel
  /// @throws NullPointerException if the channel param is `null`

  @New
  @NotNull
  public static Pipeline composer (
    @NotNull final Channel < ? super Sign > channel
  ) {

    return
      new Pipeline (
        channel.pipe ()
      );

  }

  /// A [Sign] represents the kind of operation being observed in a data pipeline.
  ///
  /// These signs distinguish between data flow (INPUT, OUTPUT),
  /// transformations (TRANSFORM, FILTER, AGGREGATE), flow control (BUFFER, BACKPRESSURE),
  /// and progress tracking (CHECKPOINT, WATERMARK).

  public enum Sign
    implements Serventis.Sign {

    /// Data was received from upstream stage.
    ///
    /// INPUT indicates this pipeline stage received data from its input. High input rates
    /// indicate active data ingestion. The ratio of INPUT to OUTPUT across stages
    /// reveals pipeline flow characteristics.

    INPUT,

    /// Data was sent to downstream stage.
    ///
    /// OUTPUT indicates this pipeline stage sent data to its output. The lag
    /// between INPUT and OUTPUT reveals processing delays within the stage.

    OUTPUT,

    /// Data was transformed.
    ///
    /// TRANSFORM indicates a map, flatMap, or enrichment operation modified the data.
    /// Core pipeline operation. May be 1:1 (map), 1:N (flatMap), or augmentation (enrich).

    TRANSFORM,

    /// Data was filtered out.
    ///
    /// FILTER indicates data was intentionally removed based on predicates. Common for
    /// data quality, compliance, or business rules. High filter rates may indicate
    /// data quality issues upstream.

    FILTER,

    /// Data was aggregated.
    ///
    /// AGGREGATE indicates windowing, grouping, or reduction operations combined multiple
    /// records. Common in analytics pipelines. May introduce latency for window completion.

    AGGREGATE,

    /// Data was buffered.
    ///
    /// BUFFER indicates temporary storage to handle rate mismatches between stages.
    /// High buffer rates may indicate downstream bottleneck. Monitor for overflow risk.

    BUFFER,

    /// Backpressure was applied.
    ///
    /// BACKPRESSURE indicates flow control signaling to slow upstream production.
    /// Prevents overflow in bounded systems. Critical for system stability.

    BACKPRESSURE,

    /// Buffer overflow occurred.
    ///
    /// OVERFLOW indicates buffer capacity exceeded, data may be lost. Serious condition
    /// indicating inadequate flow control or capacity. May trigger data replay.

    OVERFLOW,

    /// Processing checkpoint reached.
    ///
    /// CHECKPOINT indicates progress marker for fault tolerance and recovery. Used for
    /// exactly-once processing guarantees. Frequency affects recovery granularity.

    CHECKPOINT,

    /// Event-time watermark advanced.
    ///
    /// WATERMARK indicates progress in event-time (not wall-clock). Critical for
    /// windowing operations and handling late data. Watermark lag indicates delayed events.

    WATERMARK,

    /// Processing lag detected.
    ///
    /// LAG indicates this stage is behind in processing. May be time-based (latency)
    /// or count-based (records pending). High lag indicates bottleneck.

    LAG,

    /// Data was skipped or dropped.
    ///
    /// SKIP indicates anomalous data dropping (not intentional filtering). May indicate
    /// errors, corrupted data, or schema violations. Investigate high skip rates.

    SKIP

  }

  /// The [Pipeline] class represents a named, observable pipeline from which signs are emitted.
  ///
  /// ## Usage
  ///
  /// Use domain-specific methods: `pipeline.input()`, `pipeline.transform()`, `pipeline.buffer()`
  ///
  /// Pipelines provide semantic methods for reporting stage operations.

  @Queued
  @Provided
  public static final class Pipeline
    implements Signer < Sign > {

    private final Pipe < ? super Sign > pipe;

    private Pipeline (
      final Pipe < ? super Sign > pipe
    ) {

      this.pipe = pipe;

    }

    /// Emits an aggregate sign from this pipeline.

    public void aggregate () {

      pipe.emit (
        AGGREGATE
      );

    }

    /// Emits a backpressure sign from this pipeline.

    public void backpressure () {

      pipe.emit (
        BACKPRESSURE
      );

    }

    /// Emits a buffer sign from this pipeline.

    public void buffer () {

      pipe.emit (
        BUFFER
      );

    }

    /// Emits a checkpoint sign from this pipeline.

    public void checkpoint () {

      pipe.emit (
        CHECKPOINT
      );

    }

    /// Emits a filter sign from this pipeline.

    public void filter () {

      pipe.emit (
        FILTER
      );

    }

    /// Emits an input sign from this pipeline.

    public void input () {

      pipe.emit (
        INPUT
      );

    }

    /// Emits a lag sign from this pipeline.

    public void lag () {

      pipe.emit (
        LAG
      );

    }

    /// Emits an output sign from this pipeline.

    public void output () {

      pipe.emit (
        OUTPUT
      );

    }

    /// Emits an overflow sign from this pipeline.

    public void overflow () {

      pipe.emit (
        OVERFLOW
      );

    }

    /// Signs a pipeline event.
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

    /// Emits a skip sign from this pipeline.

    public void skip () {

      pipe.emit (
        SKIP
      );

    }

    /// Emits a transform sign from this pipeline.

    public void transform () {

      pipe.emit (
        TRANSFORM
      );

    }

    /// Emits a watermark sign from this pipeline.

    public void watermark () {

      pipe.emit (
        WATERMARK
      );

    }

  }

}