// Copyright (c) 2025 William David Louth

package io.humainary.substrates.tck;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.humainary.substrates.api.Substrates.Composer.pipe;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

/// Comprehensive tests for the Conduit interface.
///
/// This test class covers:
/// - Channel pooling identity and caching semantics
/// - Percept pooling across multiple percept() calls
/// - Pool isolation between different conduits
/// - Name-based lookup consistency
///
/// Conduits manage pools of percepts (channels, custom types) indexed by name.
/// The pooling behavior ensures that repeated lookups with the same name return
/// the same instance, enabling efficient reference sharing across the circuit.
///
/// @author William David Louth
/// @since 1.0
final class ConduitTest
  extends TestSupport {

  private Cortex cortex;

  @BeforeEach
  void setup () {

    cortex = cortex ();

  }

  // ===========================
  // Channel Pooling Identity Tests
  // ===========================

  /// Validates fundamental channel pooling: same name returns same instance.
  ///
  /// This test verifies the most critical pooling guarantee of conduits: when
  /// conduit.percept(name) is called multiple times with the same name, it ALWAYS
  /// returns the exact same channel instance. This pooling behavior is essential
  /// for identity consistency, efficient reference sharing, and proper subscriber
  /// attachment across the system.
  ///
  /// Test Scenario:
  /// ```
  /// conduit.percept("pooled.channel") → channel1
  /// conduit.percept("pooled.channel") → channel2 (should be channel1)
  /// conduit.percept("pooled.channel") → channel3 (should be channel1)
  /// ```
  ///
  /// Pooling Implementation (conceptual):
  /// ```
  /// Map<Name, Pipe<T>> channelPool = new ConcurrentHashMap<>();
  ///
  /// Pipe<T> get(Name name) {
  ///   return channelPool.computeIfAbsent(name, n -> {
  ///     Channel<T> channel = createChannel(n);
  ///     return composer.apply(channel);  // Create percept
  ///   });
  /// }
  /// ```
  ///
  /// Why pooling matters:
  /// - **Identity consistency**: All code uses same channel for same name
  /// - **Subscription attachment**: Subscribers attach to the one true channel
  /// - **Memory efficiency**: No duplicate channels for same logical entity
  /// - **Reference sharing**: Multiple call sites share same emission endpoint
  ///
  /// Without pooling (broken behavior):
  /// ```
  /// // BAD: Each get() creates NEW channel
  /// Pipe<Integer> pipe1 = conduit.percept(name("metrics"));
  /// Pipe<Integer> pipe2 = conduit.percept(name("metrics"));
  /// // pipe1 != pipe2 → emissions to pipe1 don't reach pipe2 subscribers!
  /// ```
  ///
  /// Real-world implications:
  /// - **Distributed systems**: Multiple threads emit to same logical channel
  ///   ```java
  ///   // Thread 1:
  ///   conduit.percept(name("errors")).emit(error1);
  ///   // Thread 2:
  ///   conduit.percept(name("errors")).emit(error2);
  ///   // Both emit to SAME channel, subscribers see both
  ///   ```
  ///
  /// - **Neural networks**: Multiple synapses connect to same neuron channel
  ///   ```java
  ///   Pipe<Signal> neuron = conduit.percept(name("layer2.neuron5"));
  ///   // All synapses emit to same neuron instance
  ///   synapse1.connect(neuron);
  ///   synapse2.connect(neuron);
  ///   ```
  ///
  /// - **Metrics aggregation**: Multiple code paths report to same counter
  ///   ```java
  ///   // Service A:
  ///   conduit.percept(name("requests")).emit(1);
  ///   // Service B:
  ///   conduit.percept(name("requests")).emit(1);
  ///   // Both increment same counter channel
  ///   ```
  ///
  /// Critical behaviors verified:
  /// - First get() creates and caches channel
  /// - Second get() returns cached channel (not new)
  /// - Third get() returns same cached channel
  /// - All three references are identical (===)
  ///
  /// Performance implications:
  /// - First access: O(1) creation + cache insertion
  /// - Subsequent accesses: O(1) cache lookup (no allocation)
  /// - Thread-safe: ConcurrentHashMap provides lock-free reads
  ///
  /// Expected: channel1 === channel2 === channel3 (all same instance)
  @Test
  void testChannelPoolingIdentity () {

    final var circuit = cortex.circuit ();

    try {

      final var conduit =
        circuit.conduit ( pipe ( Integer.class ) );

      final var name = cortex.name ( "pooled.channel" );

      // Get channel multiple times with same name
      final var channel1 = conduit.percept ( name );
      final var channel2 = conduit.percept ( name );
      final var channel3 = conduit.percept ( name );

      // All should be the SAME instance
      assertSame (
        channel1,
        channel2,
        "Same name must return same channel instance (1st vs 2nd)"
      );

      assertSame (
        channel2,
        channel3,
        "Same name must return same channel instance (2nd vs 3rd)"
      );

      assertSame (
        channel1,
        channel3,
        "Same name must return same channel instance (1st vs 3rd)"
      );

    } finally {

      circuit.close ();

    }

  }

  /// Validates pool isolation: different conduits maintain separate channel pools.
  ///
  /// This test verifies a critical boundary guarantee: each conduit maintains its
  /// OWN independent pool of channels. Even when two conduits use the SAME name,
  /// they return DIFFERENT channel instances. This isolation enables independent
  /// namespaces per conduit, preventing accidental channel sharing across different
  /// communication domains.
  ///
  /// Test Scenario:
  /// ```
  /// conduit1 = circuit.conduit(...)
  /// conduit2 = circuit.conduit(...)
  ///
  /// conduit1.percept("shared.name") → channel1
  /// conduit2.percept("shared.name") → channel2
  ///
  /// channel1 != channel2 (different instances)
  /// ```
  ///
  /// Pool Isolation Structure:
  /// ```
  /// Circuit
  ///   ├─ Conduit 1
  ///   │    └─ Pool {"shared.name" → channel1}
  ///   │
  ///   └─ Conduit 2
  ///        └─ Pool {"shared.name" → channel2}
  /// ```
  ///
  /// Why isolation matters:
  /// - **Independent namespaces**: Same name has different meaning per conduit
  /// - **Domain separation**: HTTP conduit vs WebSocket conduit with overlapping names
  /// - **Type safety**: Different conduits can have different types for same name
  /// - **Modularity**: Components can use natural names without coordination
  ///
  /// Real-world examples:
  ///
  /// 1. **Protocol separation**:
  /// ```java
  /// Conduit<Pipe<HttpRequest>> httpConduit = circuit.conduit(...);
  /// Conduit<Pipe<WsMessage>> wsConduit = circuit.conduit(...);
  ///
  /// // Same name, different channels, different types
  /// Pipe<HttpRequest> httpErrors = httpConduit.percept(name("errors"));
  /// Pipe<WsMessage> wsErrors = wsConduit.percept(name("errors"));
  /// // httpErrors != wsErrors (separate pools)
  /// ```
  ///
  /// 2. **Layer isolation in neural networks**:
  /// ```java
  /// Conduit<Neuron> layer1 = circuit.conduit(...);
  /// Conduit<Neuron> layer2 = circuit.conduit(...);
  ///
  /// // "neuron.5" means different neuron per layer
  /// Neuron layer1_n5 = layer1.percept(name("neuron.5"));
  /// Neuron layer2_n5 = layer2.percept(name("neuron.5"));
  /// // Independent neurons in different layers
  /// ```
  ///
  /// 3. **Service boundaries**:
  /// ```java
  /// Conduit<Metric> userServiceMetrics = circuit.conduit(...);
  /// Conduit<Metric> orderServiceMetrics = circuit.conduit(...);
  ///
  /// // Natural names per service without conflicts
  /// userServiceMetrics.percept(name("requests.total"));
  /// orderServiceMetrics.percept(name("requests.total"));
  /// // Separate metrics, no collision
  /// ```
  ///
  /// Without isolation (broken behavior):
  /// ```
  /// // BAD: If conduits shared pools globally
  /// httpConduit.percept(name("data")) → channel1
  /// wsConduit.percept(name("data")) → channel1 (SAME!)
  /// // Mixing HTTP and WebSocket data on same channel → type errors!
  /// ```
  ///
  /// Critical behaviors verified:
  /// - conduit1 and conduit2 created independently
  /// - Both queried with identical name ("shared.name")
  /// - Return values are DIFFERENT instances (!=)
  /// - Each conduit maintains its own pool
  ///
  /// Implementation implications:
  /// - Each conduit owns a `Map<Name, Percept>` pool
  /// - Pools are NOT shared between conduits
  /// - Circuit does NOT maintain global channel registry
  /// - Name collisions only occur WITHIN a single conduit
  ///
  /// Expected: channel1 !== channel2 (different conduits, different pools)
  @Test
  void testDifferentConduitsHaveSeparatePools () {

    final var circuit = cortex.circuit ();

    try {

      final var conduit1 =
        circuit.conduit ( pipe ( Integer.class ) );

      final var conduit2 =
        circuit.conduit ( pipe ( Integer.class ) );

      final var name = cortex.name ( "shared.name" );

      // Get channels from different conduits with same name
      final var channel1 = conduit1.percept ( name );
      final var channel2 = conduit2.percept ( name );

      // Should be different instances (separate pools)
      assertNotSame (
        channel1,
        channel2,
        "Different conduits should have separate channel pools"
      );

    } finally {

      circuit.close ();

    }

  }

  // ===========================
  // Percept Pooling Identity Tests
  // ===========================

  @Test
  void testDifferentNamesProduceDifferentChannels () {

    final var circuit = cortex.circuit ();

    try {

      final var conduit =
        circuit.conduit ( pipe ( Integer.class ) );

      final var name1 = cortex.name ( "channel.one" );
      final var name2 = cortex.name ( "channel.two" );

      final var channel1 = conduit.percept ( name1 );
      final var channel2 = conduit.percept ( name2 );

      // Different names should produce different channels
      assertNotSame (
        channel1,
        channel2,
        "Different names must return different channel instances"
      );

    } finally {

      circuit.close ();

    }

  }

  @Test
  void testDifferentNamesProduceDifferentPercepts () {

    final var circuit = cortex.circuit ();

    try {

      record Probe( Channel < Integer > channel )
        implements Percept {
      }

      final var conduit =
        circuit.conduit ( Probe::new );

      final var name1 = cortex.name ( "probe.alpha" );
      final var name2 = cortex.name ( "probe.beta" );

      final var probe1 = conduit.percept ( name1 );
      final var probe2 = conduit.percept ( name2 );

      // Different names should produce different percepts
      assertNotSame (
        probe1,
        probe2,
        "Different names must return different percept instances"
      );

      // With different underlying channels
      assertNotSame (
        probe1.channel (),
        probe2.channel (),
        "Different percepts should have different channels"
      );

    } finally {

      circuit.close ();

    }

  }

  @Test
  void testPerceptPoolingAcrossMultipleGets () {

    final var circuit = cortex.circuit ();

    try {

      record Monitor( Channel < String > channel )
        implements Percept {
      }

      final var conduit =
        circuit.conduit ( Monitor::new );

      final var name = cortex.name ( "system.monitor" );

      // Get percept 100 times
      final var firstMonitor = conduit.percept ( name );

      for ( int i = 0; i < 100; i++ ) {
        final var monitor = conduit.percept ( name );
        assertSame (
          firstMonitor,
          monitor,
          "Repeated get() must return same percept (iteration " + i + ")"
        );
      }

    } finally {

      circuit.close ();

    }

  }

  // ===========================
  // Lookup.percept() by Subject/Substrate Tests
  // ===========================

  /// Validates pooling with custom percept types beyond simple pipes.
  ///
  /// This test demonstrates that conduit pooling works for ANY percept type,
  /// not just Pipe<T>. Domain-specific wrappers (sensors, monitors, metrics,
  /// neurons, etc.) are pooled just like channels, ensuring identity consistency
  /// for custom abstractions. This enables building rich domain models on top
  /// of the substrate while maintaining efficient reference sharing.
  ///
  /// Test Scenario with Custom Percept:
  /// ```
  /// record Sensor(Channel<Double> channel) implements Percept {
  ///   void measure(double value) { channel.pipe().emit(value); }
  /// }
  ///
  /// conduit = circuit.conduit(Sensor::new);  // Composer creates Sensors
  ///
  /// conduit.percept("temperature.sensor") → sensor1
  /// conduit.percept("temperature.sensor") → sensor2 (should be sensor1)
  /// conduit.percept("temperature.sensor") → sensor3 (should be sensor1)
  /// ```
  ///
  /// Pooling with Custom Types:
  /// ```
  /// // Conduit maintains pool of custom percepts
  /// Map<Name, Sensor> sensorPool = ...;
  ///
  /// Sensor percept(Name name) {
  ///   return sensorPool.computeIfAbsent(name, n -> {
  ///     Channel<Double> channel = createChannel(n);
  ///     return new Sensor(channel);  // Composer creates wrapper
  ///   });
  /// }
  /// ```
  ///
  /// Why custom percept pooling matters:
  /// - **Domain modeling**: Build abstractions with business semantics
  /// - **Encapsulation**: Hide channel details behind domain API
  /// - **Type safety**: Compile-time guarantees for domain operations
  /// - **Identity**: Multiple call sites see same domain object
  ///
  /// Real-world custom percept examples:
  ///
  /// 1. **IoT sensors**:
  /// ```java
  /// record TemperatureSensor(Channel<Double> channel) implements Percept {
  ///   void measure(double celsius) { channel.pipe().emit(celsius); }
  ///   void measureFahrenheit(double f) { measure((f - 32) * 5/9); }
  /// }
  ///
  /// Conduit<TemperatureSensor> sensors = circuit.conduit(TemperatureSensor::new);
  /// TemperatureSensor kitchen = sensors.percept(name("sensors.kitchen"));
  /// kitchen.measureFahrenheit(72.0);  // Domain-specific API
  /// ```
  ///
  /// 2. **Metrics collectors**:
  /// ```java
  /// record Counter(Channel<Long> channel) implements Percept {
  ///   void increment() { channel.pipe().emit(1L); }
  ///   void add(long delta) { channel.pipe().emit(delta); }
  /// }
  ///
  /// Conduit<Counter> metrics = circuit.conduit(Counter::new);
  /// Counter requests = metrics.percept(name("http.requests"));
  /// requests.increment();  // Natural domain API
  /// ```
  ///
  /// 3. **Neural network neurons**:
  /// ```java
  /// record Neuron(Channel<Signal> channel, ActivationFn fn) implements Percept {
  ///   void activate(Signal input) {
  ///     channel.pipe().emit(fn.apply(input));
  ///   }
  /// }
  ///
  /// Conduit<Neuron> layer = circuit.conduit(ch -> new Neuron(ch, sigmoid));
  /// Neuron n5 = layer.percept(name("layer2.neuron5"));
  /// n5.activate(signal);  // Domain-specific propagation
  /// ```
  ///
  /// Benefits vs raw Pipe<T>:
  /// - **Encapsulation**: Internal channel hidden
  /// - **Validation**: Domain constraints enforced in percept
  /// - **Transformation**: Domain values converted to emissions
  /// - **Documentation**: Self-documenting domain API
  ///
  /// Critical behaviors verified:
  /// - Custom percept (Sensor) pooled correctly
  /// - Multiple gets return SAME Sensor instance
  /// - Underlying channels also identical (wrapped once)
  /// - Domain methods available on pooled instance
  ///
  /// Composer role in custom percepts:
  /// ```
  /// Composer<T, P extends Percept> composer = channel -> {
  ///   // Called ONCE per name (result cached)
  ///   return new CustomPercept(channel);  // Wrap channel
  /// };
  /// ```
  ///
  /// Expected: sensor1 === sensor2 === sensor3, all wrap same channel
  @Test
  void testPerceptPoolingIdentity () {

    final var circuit = cortex.circuit ();

    try {

      // Custom percept wrapper
      record Sensor( Channel < Double > channel )
        implements Percept {

      }

      final var conduit =
        circuit.conduit ( Sensor::new );

      final var name = cortex.name ( "temperature.sensor" );

      // Get percept multiple times
      final var sensor1 = conduit.percept ( name );
      final var sensor2 = conduit.percept ( name );
      final var sensor3 = conduit.percept ( name );

      // All should be same percept instance
      assertSame (
        sensor1,
        sensor2,
        "Same name must return same percept instance (1st vs 2nd)"
      );

      assertSame (
        sensor2,
        sensor3,
        "Same name must return same percept instance (2nd vs 3rd)"
      );

      // Underlying channels should also be same
      assertSame (
        sensor1.channel (),
        sensor2.channel (),
        "Same percept should wrap same channel"
      );

    } finally {

      circuit.close ();

    }

  }

}
