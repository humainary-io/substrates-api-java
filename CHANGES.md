# Changelog — Humainary Substrates (Public API)

All notable changes to the **Substrates** public API (`io.humainary.substrates`) are recorded here.
The format follows [Keep a Changelog](https://keepachangelog.com/), and the project adheres to
[Semantic Versioning](https://semver.org/).

Tracking begins at `3.0.0-SNAPSHOT-1`. Version `2.10.0` is the baseline; its history predates this
file.

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
