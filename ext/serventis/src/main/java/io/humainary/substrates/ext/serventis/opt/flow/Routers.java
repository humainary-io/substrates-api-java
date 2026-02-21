// Copyright (c) 2025 William David Louth

package io.humainary.substrates.ext.serventis.opt.flow;

import io.humainary.substrates.ext.serventis.api.Serventis;

import static io.humainary.substrates.ext.serventis.opt.flow.Routers.Sign.*;

/// # Routers API
///
/// The `Routers` API provides a structured framework for observing packet routing
/// operations within network systems. It enables emission of semantic signs that
/// describe packet transmission, reception, forwarding, and various handling conditions.
///
/// ## Purpose
///
/// This API enables systems to **observe** router behavior through discrete sign emission.
/// Signs represent semantic events about packet handling - what happened to packets as
/// they traverse routing infrastructure. Magnitude and intensity are derived by percepts
/// through aggregation of sign streams.
///
/// ## Important: Reporting vs Implementation
///
/// This API is for **reporting routing semantics**, not implementing routers.
/// If you have actual routing infrastructure (software routers, network equipment,
/// packet processing pipelines), use this API to emit observability signs about
/// operations performed on packets. Observer agents can then reason about traffic
/// patterns, detect congestion, and derive performance metrics without coupling
/// to your implementation details.
///
/// **Example**: When your router forwards a packet, call `router.forward()` to emit
/// a sign that observers can process. The sign enables meta-observability: understanding
/// traffic flows, drop rates, and routing behavior across your network.
///
/// ## Key Concepts
///
/// - **Router**: A named subject that emits signs describing packet operations
/// - **Sign**: An enumeration of distinct packet handling types
///
/// ## Packet Lifecycle
///
/// Packets flow through routers with distinct phases:
///
/// ```
/// Entry: RECEIVE
///       ↓
/// Processing: ROUTE (decision), FRAGMENT/REASSEMBLE (if needed)
///       ↓
/// Exit: FORWARD (continue) or SEND (originate) or DROP (discard)
///       ↓
/// Anomalies: CORRUPT (detected errors), REORDER (sequencing violations)
/// ```
///
/// ## Signs and Semantics
///
/// | Sign          | Description                                               |
/// |---------------|-----------------------------------------------------------|
/// | `SEND`        | A packet was transmitted (originated by this node)        |
/// | `RECEIVE`     | A packet was received from the network                    |
/// | `FORWARD`     | A packet was forwarded to next hop                        |
/// | `ROUTE`       | A routing decision was made for a packet                  |
/// | `DROP`        | A packet was discarded (congestion, policy, TTL, etc.)    |
/// | `FRAGMENT`    | A packet was fragmented due to MTU                        |
/// | `REASSEMBLE`  | Packet fragments were reassembled                         |
/// | `CORRUPT`     | Packet corruption was detected (checksum, malformed)      |
/// | `REORDER`     | Out-of-order packet arrival was detected                  |
///
/// ## Semantic Distinctions
///
/// - **SEND vs FORWARD**: SEND originates packets (this node is source), FORWARD routes
///   packets through (this node is intermediary)
/// - **ROUTE**: The routing decision itself, may precede FORWARD. Useful for observing
///   routing table lookups and path selection separately from actual forwarding
/// - **DROP reasons**: Percepts can correlate DROP patterns with congestion, policy
///   violations, TTL expiration, or routing failures
/// - **CORRUPT vs DROP**: CORRUPT detects errors but packet may be recovered or dropped;
///   DROP is final discard decision
///
/// ## Use Cases
///
/// - Monitoring traffic flows through network infrastructure
/// - Detecting packet loss and corruption patterns
/// - Observing routing decisions and path changes
/// - Tracking fragmentation requirements and MTU issues
/// - Building congestion and performance models
///
/// ## Relationship to Other APIs
///
/// `Routers` signs inform higher-level observability:
///
/// - **Queues API**: Router buffers can be modeled as queues (packet enqueue/dequeue)
/// - **Gauges API**: In-flight packets, buffer occupancy tracked via gauges
/// - **Statuses API**: High drop rates may trigger DEGRADED or DEFECTIVE conditions
///
/// ## Usage Example
///
/// ```java
/// final var cortex = Substrates.cortex();
/// var router = circuit.conduit(Routers::composer).percept(cortex.name("edge01.eth0"));
///
/// // Packet arrives
/// router.receive();
///
/// // Routing decision
/// router.route();
///
/// // Forward to next hop
/// router.forward();
///
/// // Some packets dropped due to congestion
/// router.drop();
///
/// // Corruption detected
/// router.corrupt();
///
/// ```
///
/// ## Performance Considerations
///
/// Router sign emissions are designed for very high-frequency operation. Network
/// equipment can process millions of packets per second. Signs flow asynchronously
/// through the circuit to avoid impacting packet processing latency. Consider sampling
/// (emit signs for every Nth packet) for extremely high-throughput routers (>1M pps).
///
/// @author William David Louth
/// @since 1.0

public final class Routers
  implements Serventis {

  private Routers () { }

  /// A static composer function for creating Router instruments.
  ///
  /// This method can be used as a method reference with conduits as follows:
  ///
  /// Example usage:
  /// ```java
  /// final var cortex = Substrates.cortex();
  /// var router = circuit.conduit(Routers::composer).percept(cortex.name("edge01.eth0"));
  /// ```
  ///
  /// @param channel the channel from which to create the router
  /// @return a new Router instrument for the specified channel
  /// @throws NullPointerException if the channel param is `null`

  @New
  @NotNull
  public static Router composer (
    @NotNull final Channel < ? super Sign > channel
  ) {

    return
      new Router (
        channel.pipe ()
      );

  }

  /// A [Sign] represents the kind of action being observed in a packet routing operation.
  ///
  /// These signs distinguish between packet transmission (SEND), reception (RECEIVE),
  /// forwarding (FORWARD), routing decisions (ROUTE), discards (DROP), and various
  /// packet handling conditions (FRAGMENT, REASSEMBLE, CORRUPT, REORDER).

  public enum Sign
    implements Serventis.Sign {

    /// A packet was transmitted from this router.
    ///
    /// SEND indicates this router originated the packet (is the source). Distinct from
    /// FORWARD where the router is an intermediary. Common for locally-generated traffic
    /// like routing protocol messages, ICMP responses, or application-originated packets.

    SEND,

    /// A packet was received by this router.
    ///
    /// RECEIVE indicates a packet arrived at this router from the network. Entry point
    /// for packet processing. High receive rates indicate inbound traffic volume.

    RECEIVE,

    /// A packet was forwarded to the next hop.
    ///
    /// FORWARD indicates this router routed the packet toward its destination. This is
    /// the core routing function - intermediary packet handling. The ratio of FORWARD to
    /// RECEIVE reveals forwarding success rate (1 - drop rate).

    FORWARD,

    /// A routing decision was made for a packet.
    ///
    /// ROUTE indicates a routing table lookup or path selection occurred. May precede
    /// FORWARD. Useful for observing routing computation separately from forwarding
    /// action. High route rates indicate routing activity; route failures may precede
    /// DROP signs.

    ROUTE,

    /// A packet was discarded.
    ///
    /// DROP indicates the packet was intentionally discarded and will not be forwarded.
    /// Common causes: congestion (buffer full), policy (ACL deny, firewall), TTL expired,
    /// no route to destination, or intentional discard. High drop rates indicate capacity
    /// issues, policy enforcement, or routing problems. Percepts can correlate drops with
    /// congestion signs from other APIs.

    DROP,

    /// A packet was fragmented due to MTU constraints.
    ///
    /// FRAGMENT indicates a packet exceeded the maximum transmission unit of the outbound
    /// link and was split into smaller fragments. Fragmentation impacts performance and
    /// increases loss probability. High fragmentation rates may indicate MTU misconfiguration
    /// or path MTU discovery issues.

    FRAGMENT,

    /// Packet fragments were reassembled.
    ///
    /// REASSEMBLE indicates received fragments were successfully combined into the original
    /// packet. Reassembly consumes resources and increases latency. High reassembly rates
    /// indicate upstream fragmentation. Failed reassembly (missing fragments) may result
    /// in DROP.

    REASSEMBLE,

    /// Packet corruption was detected.
    ///
    /// CORRUPT indicates checksum failure, malformed headers, or other integrity violations.
    /// May result from transmission errors, hardware issues, or malicious activity. High
    /// corruption rates indicate link quality problems or equipment failures. Corrupt packets
    /// are typically dropped after detection.

    CORRUPT,

    /// Out-of-order packet arrival was detected.
    ///
    /// REORDER indicates packets arrived in a different sequence than transmitted. Common
    /// in multi-path routing or during route changes. Moderate reordering is normal; excessive
    /// reordering may indicate routing instability or path asymmetry. May impact TCP performance
    /// by triggering spurious retransmissions.

    REORDER

  }

  /// The [Router] class represents a named, observable router from which signs are emitted.
  ///
  /// ## Usage
  ///
  /// Use domain-specific methods: `router.send()`, `router.receive()`, `router.forward()`
  ///
  /// Routers provide semantic methods for reporting packet handling events.

  @Queued
  @Provided
  public static final class Router
    implements Signer < Sign > {

    private final Pipe < ? super Sign > pipe;

    private Router (
      final Pipe < ? super Sign > pipe
    ) {

      this.pipe = pipe;

    }

    /// Emits a corrupt sign from this router.

    public void corrupt () {

      pipe.emit (
        CORRUPT
      );

    }

    /// Emits a drop sign from this router.

    public void drop () {

      pipe.emit (
        DROP
      );

    }

    /// Emits a forward sign from this router.

    public void forward () {

      pipe.emit (
        FORWARD
      );

    }

    /// Emits a fragment sign from this router.

    public void fragment () {

      pipe.emit (
        FRAGMENT
      );

    }

    /// Emits a reassemble sign from this router.

    public void reassemble () {

      pipe.emit (
        REASSEMBLE
      );

    }

    /// Emits a receive sign from this router.

    public void receive () {

      pipe.emit (
        RECEIVE
      );

    }

    /// Emits a reorder sign from this router.

    public void reorder () {

      pipe.emit (
        REORDER
      );

    }

    /// Emits a route sign from this router.

    public void route () {

      pipe.emit (
        ROUTE
      );

    }

    /// Emits a send sign from this router.

    public void send () {

      pipe.emit (
        SEND
      );

    }

    /// Signs a router event.
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
