# Humainary Substrates API

**Low-latency runtime for neural-like computational networks**

Substrates is a performance-critical Java API that enables building **neural-like computational
networks** where values flow through circuits, conduits, and channels with deterministic ordering
and dynamic topology adaptation. Designed for extreme low-latency, Substrates provides the
foundation for observability, adaptability, controllability, and operability of software services.

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

## Core Abstractions

- **Circuit**: Central processing engine with single-threaded execution
- **Conduit**: Routes emitted values from channels to subscribers
- **Channel**: Subject-based port into a conduit's pipeline
- **Percept**: Marker interface for all observable entities (pipes, instruments)
- **Pipe**: Emission carrier for passing typed values through pipelines
- **Receptor**: Callback interface for receiving emissions (domain alternative to Consumer)
- **Flow**: Type-preserving filtering and stateful operations (diff, guard, limit, etc.)
- **Subscriber**: Dynamically subscribes to channels and registers pipes
- **Subject**: Hierarchical reference with identity, name, and state

See `GLOSSARY.md` for detailed definitions.

## Threading Model

Every circuit owns exactly **one processing thread** (virtual thread):

- **Caller threads**: Enqueue emissions, return immediately
- **Circuit thread**: Dequeues and processes emissions sequentially
- **No synchronization needed**: State accessed only from circuit thread

### Recursive Emission Ordering

Circuits use a **dual-queue architecture** for deterministic depth-first execution:

```
External thread emits: [A, B, C] -> Ingress queue
Circuit processes A, which emits: [A1, A2] -> Transit queue

Execution order: A, A1, A2, B, C (depth-first)
NOT: A, B, C, A1, A2 (breadth-first)
```

The transit queue takes priority over the ingress queue, ensuring:

- **Causality preservation**: Cascading effects complete before next external input
- **Atomic computations**: Recursive chains appear atomic to external observers
- **Neural-like dynamics**: Proper signal propagation in feedback networks

## Installation

### Maven

```xml

<dependency>
    <groupId>io.humainary.substrates</groupId>
    <artifactId>humainary-substrates-api</artifactId>
    <version>1.0.0</version>
</dependency>
```

## SPI Provider Discovery

The API uses the Service Provider Interface (SPI) pattern. An implementation is resolved at runtime
via:

1. **System property** (primary): Set `io.humainary.substrates.spi.provider` to the provider class
2. **ServiceLoader** (fallback): Register via
   `META-INF/services/io.humainary.substrates.spi.CortexProvider`

SPI providers must extend `io.humainary.substrates.spi.CortexProvider` and implement the `create()`
method to return a `Cortex` implementation.

## Design Philosophy

The Humainary project seeks to restore essential qualities -- sensibility, simplicity, and
sophistication -- to systems engineering. Substrates serves as the fundamental building block for a
generic framework enabling situational awareness and seamless integration of human and machine-based
communication, coordination, and cooperation.

## License

Copyright © 2025 William David Louth, Humainary. All rights reserved.
