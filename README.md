# Substrates

**Deterministic signal circulation for Java**

Concurrent systems rarely struggle to move data. They struggle to make the resulting state changes
understandable: which change happened first, what it caused, and when its effects were complete.

Substrates gives signal-driven software an ordered place to evolve. Caller threads admit typed
values and return. A **circuit** processes those emissions, callbacks, and state transitions one at
a time on a single execution context. Every observer on that circuit sees the same accepted order,
without locks around circuit-confined state.

The defining guarantee is **causal completion**. If processing one input emits more values, that
transit work runs before the circuit processes the next external input:

```text
A emits A1 and A2; A1 emits A1a; B is already waiting

observed order:  A → A1 → A2 → A1a → B
```

Transit is FIFO and iterative, not recursive. Cascades preserve causality without growing the call
stack, including in feedback topologies.

## What This Model Provides

- **Sequential reasoning at the point of change.** Receptors and stateful operators on one circuit
  never execute concurrently.
- **A definitive history.** Once work is accepted, earlier emissions complete before later ones
  begin and every observer sees the same sequence.
- **Explicit, adaptive topology.** Named pipes are discovered and connected through subscriptions;
  wiring can change without stopping the circuit or relying on reflection.
- **Composable signal processing.** Fibers express type-preserving per-emission behavior. Flows add
  type changes, folds, windows, and reusable composition.
- **Structured ownership.** Circuits, scopes, subscriptions, and other resources make lifecycle and
  shutdown part of the model rather than cleanup left outside it.

Determinism is local to a circuit. Concurrent callers can race to establish the ingress order, and
independent circuits can execute in parallel. Substrates makes the order accepted by each circuit
unambiguous; applications obtain parallelism by partitioning work across circuits.

## The Small Core

Most applications can begin with five ideas:

| Type                      | Role                                                           |
|---------------------------|----------------------------------------------------------------|
| `Cortex`                  | Runtime entry point and factory                                |
| `Circuit`                 | Sequential processing, ordering, and lifecycle boundary        |
| `Pipe<E>`                 | Capability to admit a typed emission                           |
| `Conduit<E>`              | Name-indexed pool of pipes and source of dynamic subscriptions |
| `Fiber<E>` / `Flow<I, O>` | Reusable recipes attached ahead of a pipe or state cell        |

Subscribers add dynamic wiring. Cells, ports, and pins provide deliberately different forms of
circuit-owned state. Subjects and names give topology a stable, hierarchical identity. These types
are available when the system needs them; they are not prerequisites for the first circuit.

## A First Circuit

```java
import io.humainary.substrates.api.Substrates;
import io.humainary.substrates.api.Substrates.Receptor;

var cortex = Substrates.cortex();
var circuit = cortex.circuit();

try{
var output = circuit.pipe(
        Receptor.of(Integer.class, System.out::println)
);

var input = cortex.fiber(Integer.class)
        .guard(value -> value > 0)
        .distinct()
        .pipe(output);

  input.

emit(-1);
  input.

emit(3);
  input.

emit(3);
  input.

emit(5);

  circuit.

await(); // 3 and 5 have been observed before continuing
}finally{
        circuit.

close();
}
```

`emit` admits work and returns; it does not run the receptor on the caller thread. `await` is the
explicit barrier between asynchronous circuit work and caller-side observation.

## Where Substrates Fits

Substrates is useful when concurrent producers feed stateful coordination or control logic and the
order of change matters: instrumentation, adaptive service control, simulations, digital twins,
event-driven state machines, and recurrent signal networks.

It is not a transport, durable message broker, task executor, or Reactive Streams implementation. It
does not add a backpressure protocol, and a circuit is intentionally not a parallel execution pool.
Circuit work should remain lightweight and non-blocking; distribute independent work across multiple
circuits when parallel execution is required.

## Using the Java API

This repository contains the Java projection of the Substrates contract. It defines the public API
and provider SPI, but no runtime implementation. Applications must supply a conforming provider.

Substrates 3.0.2 requires Java 26.

```xml

<dependency>
    <groupId>io.humainary.substrates</groupId>
    <artifactId>humainary-substrates-api</artifactId>
    <version>3.0.2</version>
</dependency>
```

Provider discovery uses, in order:

1. The `io.humainary.substrates.spi.provider` system property naming a provider class.
2. `ServiceLoader` registration for `io.humainary.substrates.spi.CortexProvider`.

Provider implementations extend `CortexProvider` and return their `Cortex` from `create()`.

## Documentation

- [SUBSTRATES.md](SUBSTRATES.md) explains the Java API shape, ordering model, lifecycle, and
  performance design.
- [GLOSSARY.md](GLOSSARY.md) defines the complete public vocabulary.
- The [language-neutral specification](https://github.com/humainary-io/substrates-api-spec) is the
  normative source for portable behavior; its rationale records the design decisions behind it.
- The [Java TCK](https://github.com/humainary-io/substrates-api-java-tck) verifies providers against
  the portable contract and Java projection.

## Building

Run the pinned Maven wrapper from this repository root:

```bash
./mvnw clean verify
```

This module contains no tests by design. Provider behavior is verified by the separate TCK.

## License

Copyright © 2025–2026 William David Louth. Licensed under the Apache License, Version 2.0.
