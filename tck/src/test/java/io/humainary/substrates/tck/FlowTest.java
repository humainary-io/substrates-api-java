// Copyright (c) 2025 William David Louth

package io.humainary.substrates.tck;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.*;

/// Comprehensive tests for Flow operations.
///
/// This test class covers:
/// - Type-preserving filtering operations (diff, guard, sift)
/// - Backpressure and rate limiting (limit, sample)
/// - Stateful operations (reduce, diff with initial value)
/// - Pipeline composition (chaining multiple flow operators)
/// - Side effects (peek, forward)
/// - Value transformation within flows (replace)
///
/// Flow operators enable type-preserving transformations and filtering
/// while maintaining framework semantics. Unlike `Cortex.pipe(Function, Pipe)`
/// which changes types, flows keep the same type throughout while applying
/// stateful operations and filters.
///
/// @author William David Louth
/// @since 1.0
final class FlowTest
  extends TestSupport {

  private Cortex cortex;

  @BeforeEach
  void setup () {

    cortex = cortex ();

  }

  /// Validates diff and guard operators for deduplication and conditional filtering.
  ///
  /// This test demonstrates sequential filtering through a flow pipeline where
  /// each operator operates on the output of the previous operator:
  ///
  /// Pipeline Structure:
  /// ```
  /// emit → diff() → diff(0) → guard(even) → guard(increasing) → forward → reservoir
  /// ```
  ///
  /// Operator Semantics:
  /// 1. **diff()**: Removes consecutive duplicates (stateful)
  ///    - Maintains previous value, filters if previous == current
  ///    - Example: [2, 2, 4] → [2, 4]
  ///
  /// 2. **diff(0)**: Removes consecutive duplicates with initial previous value
  ///    - First emission compared against initial value (0)
  ///    - If first emission is 0, it would be filtered
  ///
  /// 3. **guard(even)**: Predicate filter - only even numbers pass
  ///    - Tests: (value & 1) == 0
  ///    - Example: [2, 3, 4] → [2, 4]
  ///
  /// 4. **guard(0, increasing)**: Stateful predicate with initial previous value
  ///    - Filters values that don't satisfy: next > previous
  ///    - Ensures monotonically increasing sequence
  ///
  /// 5. **forward**: Side-effect observer that doesn't affect downstream
  ///    - Captures values passing through for inspection
  ///
  /// Test Scenario:
  /// Emits: [2, 2, 4, 3, 6, 5, 8]
  ///
  /// After diff():           [2, 4, 3, 6, 5, 8]  (duplicate 2 removed)
  /// After diff(0):          [2, 4, 3, 6, 5, 8]  (2 != 0, all pass)
  /// After guard(even):      [2, 4, 6, 8]        (3, 5 filtered)
  /// After guard(increasing): [2, 4, 6, 8]        (all increasing)
  ///
  /// Why operator order matters:
  /// - diff() before guard() reduces guard evaluations (fewer values)
  /// - guard(even) before guard(increasing) further reduces stateful comparisons
  /// - forward() placed after filtering to only observe final values
  ///
  /// Critical for understanding:
  /// - Flow operators are sequential transformations (pipeline pattern)
  /// - Stateful operators (diff, guard with previous) maintain internal state
  /// - Order affects performance and semantics
  /// - forward() enables observation without affecting downstream
  ///
  /// Expected: Only [2, 4, 6, 8] reach both forward observer and reservoir
  @Test
  void testDiffAndGuardOperators () {

    final var circuit = cortex.circuit ();

    try {

      final List < Integer > forwarded = new ArrayList <> ();

      final var conduit =
        circuit.conduit (
          cortex.name ( "flow.diff.conduit" ),
          (Composer < Integer, Pipe < Integer > >) channel -> channel.pipe (
            flow -> flow
              .diff ()
              .diff ( 0 )
              .guard ( value -> ( value & 1 ) == 0 )
              .guard ( 0, ( previous, next ) -> next > previous )
              .forward ( cortex.pipe ( forwarded::add ) )
          )
        );

      final Reservoir < Integer > reservoir = cortex.reservoir ( conduit );

      final Pipe < Integer > pipe =
        conduit.percept ( cortex.name ( "flow.diff.channel" ) );

      pipe.emit ( 2 );
      pipe.emit ( 2 ); // filtered by diff()
      pipe.emit ( 4 );
      pipe.emit ( 3 ); // filtered by guard even
      pipe.emit ( 6 );
      pipe.emit ( 5 ); // filtered by guard even
      pipe.emit ( 8 );

      circuit.await ();

      final List < Integer > drained =
        reservoir.drain ()
          .map ( Capture::emission )
          .collect ( toList () );

      assertEquals (
        List.of ( 2, 4, 6, 8 ),
        forwarded
      );

      assertEquals (
        forwarded,
        drained
      );

      reservoir.close ();

    } finally {

      circuit.close ();

    }

  }

  /// Validates complex flow pipeline combining sampling, backpressure, aggregation, and transformation.
  ///
  /// This test demonstrates a sophisticated multi-stage pipeline that applies
  /// different operators in sequence, each modifying the flow of 200 emitted values:
  ///
  /// Pipeline Structure:
  /// ```
  /// emit(1..200) → sample(2) → peek(freq) → sample(0.5) → peek(rate) → forward
  ///              → limit(10) → limit(3) → reduce(sum) → peek(capture) → replace(+100) → reservoir
  /// ```
  ///
  /// Stage 1: Frequency Sampling
  /// - **sample(2)**: Pass every Nth value (frequency-based sampling)
  ///   - Input: 200 values → Output: 100 values (every 2nd)
  ///   - Deterministic: [1,2,3,4,5,6] → [2,4,6]
  /// - **peek(freq)**: Count values surviving frequency filter
  ///   - Side-effect only, doesn't affect flow
  ///
  /// Stage 2: Rate Sampling
  /// - **sample(0.5)**: Pass percentage of values (rate-based sampling)
  ///   - Input: 100 values → Output: ~50 values (50% rate)
  ///   - Probabilistic: actual count varies but ≤ frequencyCount
  /// - **peek(rate)**: Count values surviving rate filter
  ///
  /// Stage 3: Observation
  /// - **forward**: Capture all values passing rate filter
  ///   - forwarded.size() == rateCount (both observe same values)
  ///
  /// Stage 4: Backpressure (Multiple Limits)
  /// - **limit(10)**: Stop after 10 emissions pass through
  /// - **limit(3)**: Further restrict to 3 emissions
  ///   - Limits compose: min(10, 3) = 3 effective limit
  ///   - Once 3 values pass, all subsequent emissions blocked
  ///
  /// Stage 5: Aggregation
  /// - **reduce(0, sum)**: Accumulate running sum
  ///   - Stateful: maintains accumulator, emits updated sum on each value
  ///   - Example: [2,4,6] → [2,6,12] (running sum)
  /// - **peek(capture)**: Capture final accumulated sum
  ///
  /// Stage 6: Transformation
  /// - **replace(+100)**: Transform each value (add 100)
  ///   - Applied to reduce output, not original values
  ///   - Example: reduceValue=12 → reservoir receives 112
  ///
  /// Data Flow Example (simplified):
  /// ```
  /// Emit: 1,2,3,4,5,6,7,8,9,10,11,12,...200
  ///   ↓ sample(2): 2,4,6,8,10,12,...           [100 values]
  ///   ↓ sample(0.5): 4,8,12,...                 [~50 values]
  ///   ↓ limit(3): 4,8,12                        [3 values max]
  ///   ↓ reduce(sum): 4,12,24                    [running sum]
  ///   ↓ replace(+100): 104,112,124              [to reservoir]
  /// ```
  ///
  /// Why this test matters:
  /// - **Backpressure composition**: Multiple limits work together
  /// - **Stateful operators**: reduce maintains state across emissions
  /// - **Side-effects vs transforms**: peek observes, replace transforms
  /// - **Order dependency**: reduce must precede replace (operates on sums)
  /// - **Observable invariants**: forward size == rate count (both before limit)
  ///
  /// Critical behaviors verified:
  /// - Frequency sampling is deterministic (exactly 100 from 200)
  /// - Rate sampling is probabilistic (0 < count ≤ frequency count)
  /// - Forward observes everything before limit (unaffected by downstream)
  /// - Limit restricts final output (finalValues.size() ≤ 3)
  /// - Reduce accumulates correctly (running sum)
  /// - Replace transforms reduce output (sum + 100)
  ///
  /// Real-world applications:
  /// - Metrics pipelines (sample → aggregate → transform → export)
  /// - Signal processing (downsample → filter → accumulate → normalize)
  /// - Rate limiting (sample → limit → forward to slow consumer)
  ///
  /// Expected: frequencyCount=100, rateCount≤100, finalValues≤3, correct sum+100
  @Test
  void testLimitSamplePeekForwardReduceReplace () {

    final var circuit = cortex.circuit ();

    try {

      final var frequencyCount = new AtomicInteger ();
      final var rateCount = new AtomicInteger ();
      final var reduceCapture = new AtomicInteger ();

      final List < Integer > forwarded = new ArrayList <> ();

      final var conduit =
        circuit.conduit (
          cortex.name ( "flow.limit.conduit" ),
          (Composer < Integer, Pipe < Integer > >) channel -> channel.pipe (
            flow -> flow
              .sample ( 2 )
              .peek ( ignored -> frequencyCount.incrementAndGet () )
              .sample ( 0.5 )
              .peek ( ignored -> rateCount.incrementAndGet () )
              .forward ( cortex.pipe ( forwarded::add ) )
              .limit ( 10 )
              .limit ( 3L )
              .reduce ( 0, Integer::sum )
              .peek ( reduceCapture::set )
              .replace ( value -> value + 100 )
          )
        );

      final Reservoir < Integer > reservoir = cortex.reservoir ( conduit );

      final Pipe < Integer > pipe =
        conduit.percept ( cortex.name ( "flow.limit.channel" ) );

      for ( int i = 1; i <= 200; i++ ) {
        pipe.emit ( i );
      }

      circuit.await ();

      final List < Integer > finalValues =
        reservoir.drain ()
          .map ( Capture::emission )
          .toList ();

      assertEquals ( 100, frequencyCount.get (), "Frequency filter should pass half the emissions" );

      assertTrue (
        rateCount.get () > 0,
        "Rate filter should allow at least one emission"
      );

      assertTrue (
        rateCount.get () <= frequencyCount.get (),
        "Rate filter cannot allow more emissions than the frequency filter"
      );

      assertEquals (
        rateCount.get (),
        forwarded.size (),
        "Forward operator should observe every emission that passed the rate filter"
      );

      assertFalse ( finalValues.isEmpty (), "Limiter should still allow some emissions through" );
      assertTrue ( finalValues.size () <= 3, "Long limiter restricts the downstream emissions" );
      assertTrue (
        forwarded.size () >= finalValues.size (),
        "Limiters can only decrease the number of emissions"
      );

      assertEquals (
        reduceCapture.get () + 100,
        finalValues.getLast (),
        "Replacement happens after reduce execution"
      );

      reservoir.close ();

    } finally {

      circuit.close ();

    }

  }

  /// Validates sift operators for comparison-based range filtering and extrema tracking.
  ///
  /// Sift operators provide sophisticated filtering based on comparisons, tracking
  /// minimum/maximum values and filtering based on ranges and thresholds. This test
  /// chains multiple sift operations to progressively narrow the value set:
  ///
  /// Pipeline Structure:
  /// ```
  /// emit → high → min(2) → max(8) → range(3,7) → above(4) → below(7) → peek → low → reservoir
  /// ```
  ///
  /// Input: [1, 2, 2, 3, 4, 5, 6, 7, 8, 9]
  ///
  /// Sift Operator Semantics:
  ///
  /// 1. **sift(high)**: Pass only values > current maximum
  ///    - Tracks highest seen value, filters values ≤ current max
  ///    - Example: [1,3,2,5,4] → [1,3,5] (monotonically increasing)
  ///    - Result: [1, 2, 3, 4, 5, 6, 7, 8, 9]
  ///
  /// 2. **sift(min(2))**: Pass only values ≥ 2
  ///    - Absolute minimum threshold filter
  ///    - Result: [2, 3, 4, 5, 6, 7, 8, 9] (1 removed)
  ///
  /// 3. **sift(max(8))**: Pass only values ≤ 8
  ///    - Absolute maximum threshold filter
  ///    - Result: [2, 3, 4, 5, 6, 7, 8] (9 removed)
  ///
  /// 4. **sift(range(3, 7))**: Pass only values where 3 ≤ value ≤ 7
  ///    - Inclusive range filter
  ///    - Result: [3, 4, 5, 6, 7] (2, 8 removed)
  ///
  /// 5. **sift(above(4))**: Pass only values > 4
  ///    - Exclusive lower bound
  ///    - Result: [5, 6, 7] (3, 4 removed)
  ///
  /// 6. **sift(below(7))**: Pass only values < 7
  ///    - Exclusive upper bound
  ///    - Result: [5, 6] (7 removed)
  ///
  /// 7. **peek(preLow)**: Observe values before final sift
  ///    - Captures: [5, 6]
  ///
  /// 8. **sift(low)**: Pass only values < current minimum
  ///    - Tracks lowest seen value, filters values ≥ current min
  ///    - Example: [5,3,4,1,2] → [5,3,1] (monotonically decreasing)
  ///    - Applied to [5, 6]: 5 passes (first), 6 filtered (6 ≥ 5)
  ///    - Result: [5]
  ///
  /// Data Flow Visualization:
  /// ```
  /// Stage          Values                      Comment
  /// ─────          ──────                      ───────
  /// emit           [1,2,2,3,4,5,6,7,8,9]      Input
  /// high           [1,2,3,4,5,6,7,8,9]        Rising values only
  /// min(2)         [2,3,4,5,6,7,8,9]          1 filtered
  /// max(8)         [2,3,4,5,6,7,8]            9 filtered
  /// range(3,7)     [3,4,5,6,7]                2,8 filtered
  /// above(4)       [5,6,7]                    3,4 filtered
  /// below(7)       [5,6]                      7 filtered
  /// peek           [5,6]                      Observed
  /// low            [5]                        6 filtered (≥5)
  /// ```
  ///
  /// Why this test matters:
  /// - **Comparator-based filtering**: Works with any comparable type
  /// - **Stateful extrema tracking**: high/low operators maintain max/min state
  /// - **Range filtering composition**: Combining above/below creates ranges
  /// - **Order dependency**: Each sift operates on output of previous
  ///
  /// Critical behaviors verified:
  /// - high sift passes monotonically increasing values
  /// - low sift passes monotonically decreasing values
  /// - min/max create absolute bounds
  /// - range creates inclusive bounds
  /// - above/below create exclusive bounds
  /// - Multiple sifts compose correctly (AND logic)
  ///
  /// Real-world applications:
  /// - Signal filtering (pass rising edges, suppress noise)
  /// - Anomaly detection (values exceeding historical extrema)
  /// - Threshold monitoring (values entering/exiting ranges)
  /// - Peak/valley detection (extrema tracking)
  ///
  /// Expected: preLow=[5,6], reservoir=[5] (final after low sift)
  @Test
  void testSiftOperators () {

    final var circuit = cortex.circuit ();

    try {

      final List < Integer > preLow = new ArrayList <> ();

      final var conduit =
        circuit.conduit (
          cortex.name ( "flow.sift.conduit" ),
          (Composer < Integer, Pipe < Integer > >) channel -> channel.pipe (
            flow -> flow
              .sift (
                Integer::compareTo,
                Sift::high
              )
              .sift (
                Integer::compareTo,
                sift -> sift.min ( 2 )
              )
              .sift (
                Integer::compareTo,
                sift -> sift.max ( 8 )
              )
              .sift (
                Integer::compareTo,
                sift -> sift.range ( 3, 7 )
              )
              .sift (
                Integer::compareTo,
                sift -> sift.above ( 4 )
              )
              .sift (
                Integer::compareTo,
                sift -> sift.below ( 7 )
              )
              .peek ( preLow::add )
              .sift (
                Integer::compareTo,
                Sift::low
              )
          )
        );

      final Reservoir < Integer > reservoir = cortex.reservoir ( conduit );

      final Pipe < Integer > pipe =
        conduit.percept ( cortex.name ( "flow.sift.channel" ) );

      final int[] emissions = {1, 2, 2, 3, 4, 5, 6, 7, 8, 9};

      for ( final int emission : emissions ) {
        pipe.emit ( emission );
      }

      circuit.await ();

      assertEquals (
        List.of ( 5, 6 ),
        preLow,
        "Peek should observe the values surviving the preceding filters"
      );

      final List < Integer > finalValues =
        reservoir.drain ()
          .map ( Capture::emission )
          .collect ( toList () );

      assertEquals (
        List.of ( 5 ),
        finalValues
      );

      reservoir.close ();

    } finally {

      circuit.close ();

    }

  }

  /// Validates skip operator for dropping the first N emissions.
  ///
  /// The skip operator implements warm-up or initialization periods by discarding
  /// the first N values before allowing emissions to pass through. This is useful
  /// for:
  /// - Skipping startup transients in signal processing
  /// - Ignoring header rows in data streams
  /// - Implementing offset-based pagination
  /// - Warming up stateful operators before collecting results
  ///
  /// Pipeline Structure:
  /// ```
  /// emit(1..10) → skip(3) → forward → reservoir
  /// ```
  ///
  /// Operator Behavior:
  /// - **skip(3)**: Discard first 3 emissions, pass all subsequent ones
  ///   - Maintains counter: 0, 1, 2 (discard), 3+ (pass)
  ///   - Stateful: counter persists across all emissions
  ///   - One-time initialization: once N emissions skipped, all future pass
  ///
  /// Data Flow:
  /// ```
  /// Emit: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
  ///        ↓   ↓   ↓
  ///     skip skip skip
  ///                   ↓  ↓  ↓  ↓  ↓  ↓  ↓
  /// Result:          [4, 5, 6, 7, 8, 9, 10]
  /// ```
  ///
  /// Why this matters:
  /// - Clean offset implementation (no manual counter needed)
  /// - Pagination support (skip + limit = offset + count)
  /// - Initialization periods (discard unstable startup values)
  /// - Complementary to limit (skip drops prefix, limit drops suffix)
  ///
  /// Expected: First 3 values (1,2,3) discarded, remaining 7 values (4-10) pass
  @Test
  void testSkipOperator () {

    final var circuit = cortex.circuit ();

    try {

      final List < Integer > captured = new ArrayList <> ();

      final var conduit =
        circuit.conduit (
          cortex.name ( "flow.skip.conduit" ),
          (Composer < Integer, Pipe < Integer > >) channel -> channel.pipe (
            flow -> flow
              .skip ( 3L )
              .forward ( cortex.pipe ( captured::add ) )
          )
        );

      final Reservoir < Integer > reservoir = cortex.reservoir ( conduit );

      final Pipe < Integer > pipe =
        conduit.percept ( cortex.name ( "flow.skip.channel" ) );

      for ( int i = 1; i <= 10; i++ ) {
        pipe.emit ( i );
      }

      circuit.await ();

      final List < Integer > drained =
        reservoir.drain ()
          .map ( Capture::emission )
          .collect ( toList () );

      assertEquals (
        List.of ( 4, 5, 6, 7, 8, 9, 10 ),
        captured,
        "Skip should skip first 3 emissions"
      );

      assertEquals (
        captured,
        drained
      );

      reservoir.close ();

    } finally {

      circuit.close ();

    }

  }

  /// Validates skip(0) edge case: skipping zero emissions passes all values.
  ///
  /// This test verifies the boundary condition where skip count is zero, ensuring
  /// it behaves as a no-op (identity function) rather than blocking all emissions
  /// or throwing an exception.
  ///
  /// Pipeline Structure:
  /// ```
  /// emit(1..5) → skip(0) → forward → captured
  /// ```
  ///
  /// Operator Behavior:
  /// - **skip(0)**: Skip zero emissions, pass all immediately
  ///   - Counter starts at 0, condition (count < 0) always false
  ///   - All emissions pass through unchanged
  ///   - Equivalent to no skip operator at all
  ///
  /// Why this edge case matters:
  /// - **Dynamic skip counts**: When skip count comes from configuration/calculation
  ///   that might be zero, the operator must handle it gracefully
  /// - **No special casing needed**: Zero is a valid skip count (like limit(0))
  /// - **Composability**: skip(0).skip(N) should equal skip(N)
  /// - **Conditional pipelines**: Enables conditional skipping without branching
  ///
  /// Contrast with related edge cases:
  /// - `skip(0)` - pass all (identity, this test)
  /// - `limit(0)` - pass none (empty)
  /// - `sample(1)` - pass all (every value)
  ///
  /// Expected: All 5 values (1,2,3,4,5) pass through unchanged
  @Test
  void testSkipZeroPassesAll () {

    final var circuit = cortex.circuit ();

    try {

      final List < Integer > captured = new ArrayList <> ();

      final var conduit =
        circuit.conduit (
          cortex.name ( "flow.skip.zero.conduit" ),
          (Composer < Integer, Pipe < Integer > >) channel -> channel.pipe (
            flow -> flow
              .skip ( 0L )
              .forward ( cortex.pipe ( captured::add ) )
          )
        );

      final Pipe < Integer > pipe =
        conduit.percept ( cortex.name ( "flow.skip.zero.channel" ) );

      for ( int i = 1; i <= 5; i++ ) {
        pipe.emit ( i );
      }

      circuit.await ();

      assertEquals (
        List.of ( 1, 2, 3, 4, 5 ),
        captured,
        "Skip(0) should pass all emissions"
      );

    } finally {

      circuit.close ();

    }

  }

}
