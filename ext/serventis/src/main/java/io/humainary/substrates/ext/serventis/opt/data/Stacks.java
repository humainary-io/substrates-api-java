// Copyright (c) 2025 William David Louth
package io.humainary.substrates.ext.serventis.opt.data;

import io.humainary.substrates.ext.serventis.api.Serventis;

import static io.humainary.substrates.ext.serventis.opt.data.Stacks.Sign.*;

/// # Stacks API
///
/// The `Stacks` API provides a structured and minimal interface for observing interactions
/// with stack-like systems. It enables systems to emit **semantic signals** representing
/// key stack operations such as push, pop, and boundary violations.
///
/// ## Purpose
///
/// This API is designed to support **observability and reasoning** in systems that use
/// stacks as control flow, backtracking, or buffer mechanisms. By modeling stack interactions
/// as composable signals, it enables introspection of depth patterns, recursion behavior,
/// and capacity utilization without coupling to specific implementation details.
///
/// ## Important: Reporting vs Implementation
///
/// This API is for **reporting stack operation semantics**, not implementing stacks.
/// If you have an actual stack implementation (call stack monitoring, undo buffer, parser
/// state stack, etc.), use this API to emit observability signals about operations performed
/// on it. Observer agents can then reason about depth patterns, overflow risks, and
/// underflow conditions without coupling to your implementation details.
///
/// **Example**: When your parser pushes a new context onto the state stack, you would call
/// `stack.push()` to emit a signal. If the stack is full and rejects the operation,
/// call `stack.overflow()`. The signals enable meta-observability: observing the observability
/// instrumentation itself to understand stack behavior and control flow dynamics.
///
/// ## Key Concepts
///
/// - **Stack**: A named subject that emits signs describing operations performed against it
/// - **Sign**: An enumeration of distinct interaction types: `PUSH`, `POP`, `OVERFLOW`, `UNDERFLOW`
/// - **LIFO Ordering**: Last-In-First-Out semantics (distinguishes from Queues FIFO)
///
/// ## Signs and Semantics
///
/// | Sign        | Description                                               |
/// |-------------|-----------------------------------------------------------|
/// | `PUSH`      | An item was added to the stack (top)                      |
/// | `POP`       | An item was removed from the stack (top)                  |
/// | `OVERFLOW`  | A `PUSH` failed due to capacity                           |
/// | `UNDERFLOW` | A `POP` failed due to emptiness                           |
///
/// ## Stacks vs Queues
///
/// While structurally similar (both have symmetric operations and boundary violations),
/// Stacks and Queues represent fundamentally different ordering semantics:
///
/// | Aspect | Stacks (LIFO) | Queues (FIFO) |
/// |--------|---------------|---------------|
/// | **Ordering** | Last-In-First-Out | First-In-First-Out |
/// | **Operations** | PUSH/POP | ENQUEUE/DEQUEUE |
/// | **Semantics** | Recursion, backtracking, nesting | Flow control, buffering, ordering |
/// | **Depth** | Grows with nesting | Grows with backpressure |
///
/// **When to use Stacks API**: Call stacks, undo buffers, parser states, backtracking algorithms
/// **When to use Queues API**: Work queues, message buffers, pipeline stages, producer-consumer
///
/// ## Use Cases
///
/// - **Call Stack Monitoring**: Tracking recursion depth, detecting stack overflow risks
/// - **Undo/Redo Buffers**: Observing undo stack operations in editors, applications
/// - **Parser State Stacks**: Monitoring bracket matching, expression evaluation depth
/// - **Backtracking Algorithms**: Observing search state stack in constraint solvers
/// - **Thread Pool Work Stealing**: Work-stealing deques used as stacks
/// - **Expression Evaluation**: RPN calculator stacks, operator precedence parsing
///
/// ## Stack Patterns
///
/// ### Call Stack Depth Monitoring
///
/// ```java
/// var stack = circuit.conduit(Stacks::composer)
///   .percept(cortex.name("thread.callstack"));
///
/// // Method entry
/// public void recursiveMethod(int depth) {
///   stack.push();    // Call frame pushed
///
///   if (depth > MAX_DEPTH) {
///     stack.overflow();  // Stack too deep
///     throw new StackOverflowError();
///   }
///
///   recursiveMethod(depth + 1);
///
///   stack.pop();     // Call frame popped (return)
/// }
/// ```
///
/// ### Undo/Redo Buffer
///
/// ```java
/// var undoStack = circuit.conduit(Stacks::composer)
///   .percept(cortex.name("editor.undo"));
/// var redoStack = circuit.conduit(Stacks::composer)
///   .percept(cortex.name("editor.redo"));
///
/// // User performs action
/// public void performAction(Action action) {
///   if (undoStack.size() == MAX_UNDO) {
///     undoStack.overflow();  // Undo buffer full
///     undoStack.removeOldest();
///   }
///   undoStack.push(action);
///   undoStack.push();  // Signal emitted
///
///   // Clear redo stack
///   while (!redoStack.isEmpty()) {
///     redoStack.pop();
///   }
/// }
///
/// // User undoes
/// public void undo() {
///   if (undoStack.isEmpty()) {
///     undoStack.underflow();  // Nothing to undo
///     return;
///   }
///   Action action = undoStack.pop();
///   undoStack.pop();  // Signal emitted
///
///   action.undo();
///   redoStack.push(action);
///   redoStack.push();  // Signal emitted
/// }
/// ```
///
/// ### Parser State Stack (Bracket Matching)
///
/// ```java
/// var stack = circuit.conduit(Stacks::composer)
///   .percept(cortex.name("parser.brackets"));
///
/// // Parse expression with bracket matching
/// public void parseExpression(String expr) {
///   for (char c : expr.toCharArray()) {
///     if (isOpenBracket(c)) {
///       stack.push();    // Open bracket pushed
///       bracketStack.push(c);
///     } else if (isCloseBracket(c)) {
///       if (bracketStack.isEmpty()) {
///         stack.underflow();  // Unmatched close bracket
///         throw new ParseException("Unmatched bracket");
///       }
///       stack.pop();     // Matched bracket popped
///       bracketStack.pop();
///     }
///   }
/// }
/// ```
///
/// ### RPN Calculator Stack
///
/// ```java
/// var stack = circuit.conduit(Stacks::composer)
///   .percept(cortex.name("calculator.operands"));
///
/// // Reverse Polish Notation evaluation
/// public double evaluateRPN(String[] tokens) {
///   for (String token : tokens) {
///     if (isNumber(token)) {
///       operandStack.push(parseDouble(token));
///       stack.push();    // Operand pushed
///     } else {
///       if (operandStack.size() < 2) {
///         stack.underflow();  // Not enough operands
///         throw new IllegalStateException();
///       }
///       double b = operandStack.pop();
///       stack.pop();
///       double a = operandStack.pop();
///       stack.pop();
///
///       operandStack.push(apply(token, a, b));
///       stack.push();    // Result pushed
///     }
///   }
///   return operandStack.pop();
/// }
/// ```
///
/// ### Work-Stealing Deque (Stack Mode)
///
/// ```java
/// var stack = circuit.conduit(Stacks::composer)
///   .percept(cortex.name("worker.localqueue"));
///
/// // Worker pushes tasks locally (LIFO for cache locality)
/// public void pushTask(Task task) {
///   if (localQueue.size() == capacity) {
///     stack.overflow();  // Local queue full
///     globalQueue.offer(task);
///     return;
///   }
///   localQueue.push(task);
///   stack.push();    // Task added to local stack
/// }
///
/// // Worker pops own tasks (LIFO)
/// public Task popTask() {
///   if (localQueue.isEmpty()) {
///     stack.underflow();  // No local work
///     return stealFromOthers();
///   }
///   Task task = localQueue.pop();
///   stack.pop();     // Popped own task
///   return task;
/// }
/// ```
///
/// ### Backtracking Search Stack
///
/// ```java
/// var stack = circuit.conduit(Stacks::composer)
///   .percept(cortex.name("search.states"));
///
/// // Depth-first search with backtracking
/// public boolean dfs(State initial) {
///   stateStack.push(initial);
///   stack.push();
///
///   while (!stateStack.isEmpty()) {
///     State current = stateStack.pop();
///     stack.pop();
///
///     if (current.isGoal()) return true;
///
///     for (State next : current.successors()) {
///       stateStack.push(next);
///       stack.push();  // New search state
///     }
///   }
///
///   stack.underflow();  // Search exhausted
///   return false;
/// }
/// ```
///
/// ## Relationship to Other APIs
///
/// `Stacks` signals can inform higher-level abstractions:
///
/// - **Queues API**: Complementary buffer abstraction (LIFO vs FIFO ordering)
/// - **Processes API**: Call stack depth relates to process execution depth
/// - **Tasks API**: Task execution stack for nested task spawning
/// - **Statuses API**: Stack overflow patterns may indicate DEGRADED or RECURSIVE conditions
/// - **Gauges API**: Stack depth can be modeled as a gauge (PUSH=increment, POP=decrement)
///
/// ## Performance Considerations
///
/// Stack sign emissions are designed for high-frequency operation (10M-50M Hz).
/// Zero-allocation enum emission with ~10-20ns cost for non-transit emits.
/// Signs flow asynchronously through the circuit's event queue.
///
/// ## Semiotic Ascent: Stacks → Status → Situation
///
/// Stack signs translate upward into universal languages:
///
/// ### Stacks → Status Translation
///
/// Pattern-based translation through subscriber observation:
///
/// | Stack Pattern              | Status      | Rationale                              |
/// |----------------------------|-------------|----------------------------------------|
/// | High PUSH without POP      | DEEP        | Stack depth increasing (recursion)     |
/// | Many OVERFLOW              | SATURATED   | Bounded stack at capacity              |
/// | Many UNDERFLOW             | STARVED     | Excessive pops on empty stack          |
/// | Rapid PUSH/POP             | CHURNING    | High turnover, shallow operations      |
/// | Balanced PUSH/POP          | STABLE      | Healthy stack usage                    |
/// | PUSH rate increasing       | DIVERGING   | Unbounded recursion risk               |
///
/// ### Status → Situation Assessment
///
/// | Status Pattern       | Situation   | Example                                    |
/// |----------------------|-------------|--------------------------------------------|
/// | DEEP (sustained)     | RECURSION   | Deep call stack, overflow risk             |
/// | SATURATED (bounded)  | CAPACITY    | Stack buffer too small                     |
/// | CHURNING (rapid)     | THRASHING   | Excessive push/pop, cache thrashing        |
/// | DIVERGING (growth)   | RUNAWAY     | Unbounded recursion, memory leak           |
///
/// This hierarchical meaning-making enables cross-domain reasoning: observers understand
/// stack behavior and control flow dynamics without needing to understand stack implementation
/// details or specific algorithms.
///
/// @author William David Louth
/// @since 1.0

public final class Stacks
  implements Serventis {

  private Stacks () { }

  /// A static composer function for creating Stack instruments.
  ///
  /// This method can be used as a method reference with conduits as follows:
  ///
  /// Example usage:
  /// ```java
  /// final var cortex = Substrates.cortex();
  /// var stack = circuit.conduit(Stacks::composer).percept(cortex.name("parser.states"));
  /// ```
  ///
  /// @param channel the channel from which to create the stack
  /// @return a new Stack instrument for the specified channel
  /// @throws NullPointerException if the channel param is `null`

  @New
  @NotNull
  public static Stack composer (
    @NotNull final Channel < ? super Sign > channel
  ) {

    return
      new Stack (
        channel.pipe ()
      );

  }


  /// A [Sign] represents the kind of action being observed in a stack interaction.
  ///
  /// These signs form complementary pairs representing normal operations (PUSH/POP)
  /// and boundary violations (OVERFLOW/UNDERFLOW). The signs enable reasoning about
  /// stack depth, recursion patterns, and capacity utilization.

  public enum Sign
    implements Serventis.Sign {

    /// Indicates an item was successfully added to the stack (top).
    ///
    /// This sign represents normal push operations. High PUSH rates without corresponding
    /// POP signals indicate stack growth (recursion depth, nesting levels). The ratio
    /// of PUSH to POP reveals stack fill rate.

    PUSH,

    /// Indicates an item was successfully removed from the stack (top).
    ///
    /// This sign represents normal pop operations. High POP rates indicate unwinding
    /// (returning from calls, backtracking). Sustained POP > PUSH patterns indicate
    /// stack draining.

    POP,

    /// Indicates the stack reached capacity and rejected a PUSH operation.
    ///
    /// Overflow signals reveal capacity violations where the stack reached its maximum
    /// depth. Frequent overflows may indicate insufficient capacity, unbounded recursion,
    /// or the need for iterative algorithms instead of recursive ones.

    OVERFLOW,

    /// Indicates the stack was empty and could not satisfy a POP operation.
    ///
    /// Underflow signals reveal underflow conditions where operations attempt to pop
    /// from an empty stack. This often indicates algorithm errors, unmatched brackets
    /// in parsing, or state machine bugs.

    UNDERFLOW

  }

  /// The [Stack] class represents a named, observable stack from which signs are emitted.
  ///
  /// ## Usage
  ///
  /// Use domain-specific methods: `stack.push()`, `stack.pop()`, `stack.overflow()`
  ///
  /// Stacks provide semantic methods for reporting stack operation events.

  @Queued
  @Provided
  public static final class Stack
    implements Signer < Sign > {

    private final Pipe < ? super Sign > pipe;

    private Stack (
      final Pipe < ? super Sign > pipe
    ) {

      this.pipe = pipe;

    }

    /// Emits an overflow sign from this stack.

    public void overflow () {

      pipe.emit (
        OVERFLOW
      );

    }

    /// Emits a pop sign from this stack.

    public void pop () {

      pipe.emit (
        POP
      );

    }

    /// Emits a push sign from this stack.

    public void push () {

      pipe.emit (
        PUSH
      );

    }

    /// Signs a stack event.
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

    /// Emits an underflow sign from this stack.

    public void underflow () {

      pipe.emit (
        UNDERFLOW
      );

    }

  }

}
