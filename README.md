# Humainary Substrates

**Low-latency runtime for neural-like computational networks**

Substrates is a performance-critical Java runtime that enables building **neural-like computational
networks** where values flow through circuits, conduits, and channels with deterministic ordering
and dynamic topology adaptation. Designed for extreme low-latency, Substrates provides the
foundation
for observability, adaptability, controllability, and operability of software services.

## Key Features

### Extreme Performance

- **Single digit nanosecond emission latency** (for transit signaling)
- Single-threaded circuit execution eliminates synchronization overhead
- Dual-queue architecture (ingress + transit) for optimized recursive emissions
- Lock-free operations in hot paths

### Deterministic Ordering

- **Depth-first execution** for recursive emissions
- Strict ordering guarantees: earlier emissions complete before later ones begin
- All subscribers see emissions in the same order
- Enables reproducible execution and digital twin synchronization

### Dynamic Topologies

- **Lazy subscription model** with version tracking
- Channels discovered dynamically by name
- Add/remove subscribers without stopping the system
- Safe cyclic topologies for recurrent networks

### Neural-Like Architecture

- Circuits, conduits, and channels form computational networks
- Asynchronous message passing via event queues
- Stack-safe hierarchical cells with arbitrary depth
- Feedback loops and recurrent connections

## Architecture

### Core Abstractions

- **Circuit**: Central processing engine with single-threaded execution
- **Conduit**: Routes emitted values from channels to subscribers
- **Channel**: Subject-based port into a conduit's pipeline
- **Percept**: Marker interface for all observable entities (pipes, instruments)
- **Pipe**: Emission carrier for passing typed values through pipelines
- **Receptor**: Callback interface for receiving emissions (domain alternative to Consumer)
- **Flow**: Type-preserving filtering and stateful operations (diff, guard, limit, etc.)
- **Subscriber**: Dynamically subscribes to channels and registers pipes
- **Subject**: Hierarchical reference with identity, name, and state

### Threading Model

Every circuit owns exactly **one processing thread** (virtual thread):

- **Caller threads**: Enqueue emissions, return immediately
- **Circuit thread**: Dequeues and processes emissions sequentially
- **No synchronization needed**: State accessed only from circuit thread

### Recursive Emission Ordering

Circuits use a **dual-queue architecture** for deterministic depth-first execution:

```
External thread emits: [A, B, C] → Ingress queue
Circuit processes A, which emits: [A1, A2] → Transit queue

Execution order: A, A1, A2, B, C (depth-first)
NOT: A, B, C, A1, A2 (breadth-first)
```

The transit queue takes priority over the ingress queue, ensuring:

- **Causality preservation**: Cascading effects complete before next external input
- **Atomic computations**: Recursive chains appear atomic to external observers
- **Neural-like dynamics**: Proper signal propagation in feedback networks

## Project Structure

```
substrates/
├── api/            - Core API interfaces (Substrates.java)
├── ext/            - Extensions built on substrates
│   └── serventis/  - Observability extension
├── jmh/            - JMH performance benchmarks
├── tck/            - Test Compliance Kit
```

## Building

**Requirements**: Java 25+, Maven 3.9.11+

### Standard Build

```bash
# Build without TCK tests (default)
./mvnw clean install

# Run specific module test
./mvnw test -Dtest=CircuitTest -pl spi/alpha

# Run JMH performance benchmarks
./jmh.sh

# Or manually build JMH benchmarks
./mvnw clean install -Pjmh
```

**Note**: Tests are skipped by default (`maven.test.skip=true` in `pom.xml`) to allow building
without requiring an SPI implementation.

### Running TCK Tests

The Test Compliance Kit (TCK) in `substrates/tck/` validates SPI implementations for specification
compliance. The TCK is optional and activated via the `tck` profile:

```bash
# Run TCK tests with default SPI (alpha implementation)
./tck.sh

# Or manually via Maven
./mvnw clean install -Dtck

# Test with a custom SPI implementation
./mvnw clean install -Dtck \
  -Dsubstrates.spi.groupId=com.example \
  -Dsubstrates.spi.artifactId=my-substrates-spi \
  -Dsubstrates.spi.version=1.0.0

# Run TCK for specific module only
./mvnw test -pl tck -Dtck
```

**Default TCK properties** (defined in `pom.xml`):

- `substrates.spi.groupId` = `io.humainary.substrates.spi`
- `substrates.spi.artifactId` = `humainary-substrates-spi-alpha`
- `substrates.spi.version` = `${revision}`

You can also override these properties in your local `settings.xml` or by modifying `pom.xml`
when forking the project.

### Performance Benchmarks

Comprehensive JMH benchmarks measure performance across all components:

```bash
# Run all benchmarks
./jmh.sh

# List available benchmarks
./jmh.sh -l

# Run specific benchmark
./jmh.sh PipeOps.emit_to_empty_pipe

# Run benchmarks matching pattern (e.g., all batched benchmarks)
./jmh.sh ".*batch"
```

**Benchmark categories**:

- **Hot-path benchmarks**: Isolate operation cost from lifecycle overhead using
  `@Setup(Level.Iteration)`
- **Batched benchmarks**: Measure amortized cost over 1000 operations using
  `@OperationsPerInvocation`
- **Single-operation benchmarks**: Baseline measurements including all overhead

See `BENCHMARKS.md` for detailed results, interpretation guidelines, and performance analysis.

## Performance Profile

Substrates is designed for **extreme performance** to enable neural-like network exploration:

- **Billions of emissions** through cyclic networks
- **Real-time adaptive topologies**
- **Spiking neural network** implementations
- **Massive parallelism** via multiple circuits with virtual threads

## Design Philosophy

The Humainary project seeks to restore essential qualities—sensibility, simplicity, and
sophistication—to systems engineering. Substrates serves as the fundamental building block for a
generic framework enabling situational awareness and seamless integration of human and machine-based
communication, coordination, and cooperation.

## License

Copyright © 2025 William David Louth. All rights reserved.

## Links

- [Humainary.io](https://humainary.io)