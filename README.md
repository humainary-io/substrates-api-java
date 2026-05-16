# Humainary Substrates API

**API contract for deterministic emission networks**

Substrates defines the core interfaces for building computational networks where values flow through
circuits and conduits with deterministic ordering and dynamic topology adaptation. This module is
the **API specification only** — it contains no implementation. A runtime SPI provider (such as
`humainary-substrates-spi-alpha`) is required at runtime.

## Mental Model

Building with Substrates follows a simple workflow:

1. **Get a Cortex** — the singleton entry point (`Substrates.cortex()`)
2. **Create a Circuit** — where work runs in deterministic order
3. **Create a Conduit** — groups named pipes for a given emission type
4. **Get a Pipe by Name** — `conduit.get(cortex.name("sensor"))` returns a named emission carrier
5. **Emit values** — `pipe.emit(value)` enqueues to the circuit
6. **Filter or transform** — attach a `Fiber` recipe (`cortex.fiber(T.class).guard(...).diff()`)
   for type-preserving per-emission operators, or a `Flow`
   (`cortex.flow(I.class).map(...).scan(...).window(...).fiber(...)`) for type-changing composition
7. **Observe** — attach a callback (Receptor) or a recorder (Reservoir) for testing
8. **Close what you open** — circuits own resources; close the circuit when done

Everything else — dynamic subscribers, hierarchical identity, temporal contracts, scoped lifecycle
management — is available when you need it, but not required to get started.

## Core Abstractions

The types most users interact with directly:

- **Cortex**: Factory entry point for circuits, names, scopes, fibers, and flows
- **Circuit**: Central processing engine with single-threaded execution
- **Conduit**: Pipe factory; pools named pipes and supports subscriptions
- **Pipe**: Emission carrier — call `emit(value)` to send typed values
- **Name**: Hierarchical address for routing — interned, O (1) identity comparison
- **Fiber**: Type-preserving per-emission operator pipeline (diff, guard, limit, every, chance,
  reduce, above, below, range, ...)
- **Flow**: Type-changing composition pipeline (`map`, `scan`, `window`, `fiber`, `flow`, `pipe`)
- **Window**: Callback-scoped rolling view emitted by `Flow.window(int)`
- **Receptor**: Callback for receiving emissions (non-null contract)
- **Reservoir**: Buffered emission recorder for testing and diagnostics

For dynamic topology, lifecycle management, identity internals, and framework extension, see the
full [GLOSSARY.md](GLOSSARY.md) which organizes the API types into core and advanced layers.

## Core Guarantees

**Deterministic Ordering**:

- Emissions are observed in strict enqueue order
- Earlier emissions complete before later ones begin
- All subscribers see emissions in the same order

**Eventual Consistency**:

- Subscription changes use lazy rebuild with version tracking
- Named pipes detect changes on next emission (not immediately)
- No blocking or global coordination required

**State Isolation**:

- Per-attachment Fiber operators, Flow `scan` state slots, and Flow `window` rolling buffers
  maintain independent state per materialization, ensuring stateful operators are isolated per named
  pipe
- Subscriber state accessed only from circuit thread (no sync needed)
- No shared mutable state between circuits

## Installation

### Maven

```xml

<dependency>
    <groupId>io.humainary.substrates</groupId>
    <artifactId>humainary-substrates-api</artifactId>
    <version>2.6.0</version>
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

## Testing

This module contains no tests by design — it is a pure API contract. Specification compliance is
verified by a separate **Test Compliance Kit (TCK)** that validates SPI providers against the API's
ordering, temporal, lifecycle, and threading guarantees. The TCK is not yet published.

## Design Philosophy

The Humainary project seeks to restore essential qualities — sensibility, simplicity, and
sophistication — to systems engineering. Substrates serves as the fundamental building block for a
generic framework enabling situational awareness and seamless integration of human and machine-based
communication, coordination, and cooperation.

## Design Documentation

- **[SUBSTRATES.md](SUBSTRATES.md)**: Design rationale — why determinism over throughput, interface
  segregation, the performance model, temporal contracts, and the ingress/transit ordering model.
- **[GLOSSARY.md](GLOSSARY.md)**: Term definitions for all core concepts.
- **[RATIONALE.md](../substrates-api-spec/RATIONALE.md)**: Specification companion — formal design
  decisions positioned against related work (Reactive Streams, Actor Model, Berkeley Reactor).
- **[SPEC.md](../substrates-api-spec/SPEC.md)**: Formal specification with conformance requirements.

## License

Copyright © 2025 William David Louth. Licensed under the Apache License, Version 2.0.
