# Changelog — Humainary Substrates (Public API)

All notable changes to the **Substrates** public API (`io.humainary.substrates`) are recorded here.
The format follows [Keep a Changelog](https://keepachangelog.com/), and the project adheres to
[Semantic Versioning](https://semver.org/).

Tracking begins at `3.0.0-SNAPSHOT-1`. Version `2.10.0` is the baseline; its history predates this
file.

## 3.0.0-SNAPSHOT-3 — 2026-06-30

Replaces the source-bound `Reservoir` with the circuit-owned `Basin` buffer. **Breaking.**

### Added

- **`Circuit.basin(int capacity)`** → `Basin<E>` — a circuit-owned, bounded in-memory buffer fed
  through `Basin.pipe()`; the multi-value sibling of `Cell`. Retains the most recent `capacity`
  values in emission order, evicting the oldest when full. The element type is recovered by target
  typing, so this form also serves nested types such as `Basin<Capture<E>>`. Throws
  `IllegalArgumentException` for capacity < 1 and `Fault` if the circuit is closed.
- **`Circuit.basin(Class<E> type, int capacity)`** → `Basin<E>` — class-token convenience for
  inference, mirroring `conduit(Class)`. A class token cannot express a parameterized element type;
  use `basin(int)` with target typing for those.
- **`Basin<E>`** — `@Provided`, a `Substrate` (not a `Resource`). `pipe()` returns the emit-only
  feed; `drain(Pipe<? super E>)` forwards the retained values to a target pipe on the circuit worker
  thread (same-circuit transit, else cross-circuit emission) and clears the buffer. To consume with
  arbitrary logic, drain into `Circuit.pipe(Receptor)`.

### Removed

- **`Source.reservoir(int)`** and the **`Reservoir<E>`** type. `Source` no longer offers buffering;
  it is reduced to subscription and lifecycle. The capture-from-a-source role is now composed from
  existing primitives — a `Sink` mints captures into a `Basin<Capture<E>>`, wired via
  `source.subscribe(circuit.subscriber(name, circuit.sink(basin.pipe())))`.

### Changed

- `Capture` documentation: captures are produced by a `Sink` (a `Basin` buffers them). `Reservoir`
  references throughout `Capture`, `Resource`, `Sink`, `Cortex`, and `Circuit` were repointed to
  `Basin`, and `Reservoir` was dropped from the `Resource` implementation list (a `Basin` is not a
  `Resource`).

### Compatibility

Breaking. `Source.reservoir(int)` and `Reservoir` are removed. Callers buffering a source's
emissions migrate to `circuit.basin(...)` + `circuit.sink(...)` + `subscribe`, reading via
`drain(Pipe)`. The synchronous, stream-returning drain is no longer in the API; a re-iterable
snapshot is obtained by draining into a collecting pipe and awaiting the circuit. Because captures
now flow through a `Sink`, `Capture.subject()` reports the sink channel (mirroring the source
channel's name with its own identity) and `Capture.current()` of a source-fed buffer is the circuit
(the sink mints during the source's transit cascade).

## 3.0.0-SNAPSHOT-2 — 2026-06-28

List fan-out and named fan-out overloads for `Circuit.pipe`. Additive and source/binary compatible
with `3.0.0-SNAPSHOT-1`.

### Added

- **`Circuit.pipe(List<? extends Pipe<? super E>>)`** → `Pipe<E>` — returns a circuit-owned pipe
  that fans each emission out to a fixed list of target pipes. Targets are snapshotted at creation
  (later mutation of the caller's list has no effect) and dispatched in list order; duplicate
  entries receive the emission once per occurrence. An empty list is equivalent to `pipe()` (no-op
  sink) and a single-element list is equivalent to `pipe(Pipe)`. A null list or null element is
  rejected synchronously; a non-provider target raises `Fault`.
- **`Circuit.pipe(Name, List<? extends Pipe<? super E>>)`** → `Pipe<E>` — the named form of
  `pipe(List)`. Shares the fan-out contract (list order, per-occurrence duplicates, snapshotting),
  but always mints a named pipe rather than collapsing the degenerate sizes: an empty list yields a
  named no-op pipe and a single-target list a named forwarder.

### Documentation

- `SPEC.md` — `Circuit.pipe(targets)` and `pipe(name, targets)` added to the Circuit execution
  operations, with the list fan-out contract (snapshotting, list order, duplicates, empty/single
  behavior, provider mismatch, and cross-circuit ordering) and the always-mint semantics of the
  named fan-out form.

### Compatibility

Non-breaking. No public type, method, or signature was removed or changed; existing
`3.0.0-SNAPSHOT-1` callers compile and run unchanged.

## 3.0.0-SNAPSHOT-1 — 2026-06-05

Run-length operators for `Flow`. Additive and source/binary compatible with `2.10.0`.

### Added

- **`Flow.run()`** → `Flow<I, Run<O>>` — turns each admission into a `Run` carrying the emission and
  the length of its consecutive run. The length is `1` on the first admission and after every
  change, and increments while the value repeats (by value equality).
- **`Flow.change()`** → `Flow<I, Change<O>>` — emits a `Change` only at a run boundary, i.e. an
  admission value-unequal to its predecessor: the value the closed run held, the value that opened
  the next run, and the closed run's terminal length. The first admission opens the first run and
  emits nothing; the open or final run is never reported (its length is observable only through
  `run`).
- **`Run<E>`** — immutable per-admission envelope with `emission()` and `length()`. Annotated
  `@Tenure(EPHEMERAL) @ReadOnly @Provided`: a fresh, retainable carrier (the `Capture` pattern), not
  callback-scoped.
- **`Change<E>`** — immutable run-boundary envelope with `from()`, `to()`, and `length()`. Same
  carrier contract as `Run`.

Both decide consecutive repetition by value equality; callers wanting repetition over a derived key
`map` to the key first. Both are stateful per materialization.

### Documentation

- `SPEC.md` — `run`/`change` added to the Flow operator family, with the full §16 conformance
  inventory for `Run`/`Change` (tenure table, required-types table, capability matrix, and operation
  blocks).

### Compatibility

Non-breaking. No public type, method, or signature was removed or changed; existing `2.10.0` callers
compile and run unchanged.
