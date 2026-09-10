# Substrates 101: Pipes, Observers, and Processing

Imagine a small program that receives temperature readings from rooms in a building. At first,
we just want to observe a reading. Later, we want to distinguish rooms, connect additional
observers, discard implausible readings, turn the remaining values into something useful, and
let a reading feed back to produce the next one.

Substrates gives us the pieces to build that program incrementally. Producers send typed values
through pipes. Observers connect to those pipes. A circuit processes the resulting work in an
ordered, sequential context.

Use Substrates when producers and observers need to evolve independently, while the work triggered
by each input follows a predictable order within a circuit.

We will start with one value and add each piece as we need it. By the end, you will be able to
connect producers and observers, process their readings, and explain when the resulting state
is ready to read. Familiarity with Java lambdas and generics is enough to follow along.

§1, §6, and §7 are complete programs. The other sections show only the body that changes, so
keep the imports, circuit setup, and `finally` block from §1 as you go.

## 1. Send a reading through a pipe

The smallest useful idea is a `Pipe<Integer>`: something we can send an integer through.

```java
temperature.emit(21);
```

That is all a producer needs to know. It holds a pipe and emits a value. It does not need to
know which function eventually handles that value.

To create our first pipe, we need a little setup. A **cortex** is the runtime entry point and
factory. A **circuit** processes submitted work one item at a time. A **receptor** is the callback
that receives a value when the circuit processes it; here, it prints the temperature.

These examples target Java API version 3.1.2 on Java 26. A **provider** supplies the runtime
implementation needed to run them; see the [README](README.md#using-the-java-api) for setup
requirements.

Here is a complete first program:

```java
import io.humainary.substrates.api.Substrates;

public class Substrates101 {

  static void main(String[] args) {

    var cortex = Substrates.cortex();
    var circuit = cortex.circuit();

    try {

      var temperature = circuit.pipe(
        (Integer value) -> System.out.println(value)
      );

      temperature.emit(21);
      temperature.emit(22);

      circuit.await();

    } finally {

      circuit.closeAwait();

    }

  }

}
```

Output:

```text
21
22
```

The call to `emit` queues the reading and returns. The receptor runs on the circuit's processing
thread, so returning from `emit` does not mean the reading has been printed. `await` waits for
the work accepted before that call, including any further work it causes on the circuit. One
wait after both readings is enough.

The explicit `(Integer value)` makes `temperature` a `Pipe<Integer>`: trying to emit a string
would be a compile error. Using an untyped parameter in this particular printing lambda would
instead infer `Pipe<Object>`. The complete programs also use a non-public `main` entry point,
supported by the required JDK.

If a receptor throws, its exception does not propagate back to the caller of `emit`. Other
receptors attached to the same pipe still receive the emission, and the circuit continues
processing later work. The provider reports the failure through its documented reporting
mechanism.

In the `finally` block, `closeAwait` closes the circuit and waits for cleanup. Both waits belong
on the caller side: never wait for a circuit from inside one of its own callbacks.

Console output keeps these demonstrations visible. Real circuit callbacks should do lightweight,
non-blocking work; slow I/O would delay every other activity on the same circuit. Our emitted
integers are immutable, which also makes them safe to pass across the caller/circuit boundary.

## 2. Give each room a named pipe

Now the building has several rooms. We want a producer to obtain the pipe for a particular room
without constructing and managing a separate collection of pipes itself.

A **conduit** provides that collection. It is a name-indexed pool of pipes of one emission type:

```java
var temperatures = circuit.conduit(Integer.class);

var kitchen = temperatures.get(cortex.name("kitchen"));
var office = temperatures.get(cortex.name("office"));
```

This is the setup for the next example; we will emit readings after connecting an observer.
`cortex.name(...)` supplies the name objects used for lookup, and `Integer.class` tells the
compiler what the conduit emits. It is a type witness, read at compile time and not retained:
declaring `Conduit<Integer> temperatures = circuit.conduit()` yields the same conduit.

Both variables are pipes accepting integers. Within this conduit, looking up `"kitchen"` again
returns the same pipe instance. A name identifies the emission point; the integer carries the
reading.

This is useful when different parts of a program independently need access to the same named
pipe. Code producing kitchen readings can retain just the kitchen pipe.

We have created the emission points, but have not yet attached anything to receive their values.
That is the conduit's other role: it supports subscriptions to these named pipes, also called
**channels**.

## 3. Connect an observer

A **subscriber** discovers named channels and decides how to connect them. For each channel it
receives a subject, which carries identity information, and a registrar, which lets it attach
receptors or downstream pipes. Our display will use the subject's name to label each reading.

Keep the imports, circuit setup, and `finally` block from §1, and replace everything inside
`try` with this example:

```java
var temperatures = circuit.conduit(Integer.class);

var display =
  temperatures.subscribe(
    circuit.subscriber(
      cortex.name("display"),
      (subject, registrar) ->
        registrar.register(
          value -> System.out.println(subject.name() + ": " + value)
        )
    )
  );

var kitchen = temperatures.get(cortex.name("kitchen"));
var office = temperatures.get(cortex.name("office"));

kitchen.emit(21);
office.emit(19);
kitchen.emit(22);

circuit.await();
display.close();
```

Output:

```text
kitchen: 21
office: 19
kitchen: 22
```

There are two callbacks here, with different jobs. The outer callback receives a channel's
subject and wires that channel. The inner callback receives each temperature reading.

Creating the subscriber inside `temperatures.subscribe(...)` lets Java infer its type from the
conduit, so `value` is an `Integer` without another type annotation.

Discovery happens when a channel becomes active. For the first kitchen reading, the circuit
does this:

```text
First kitchen reading is processed
  → outer callback receives subject + registrar
  → outer callback registers the kitchen's receptor
  → receptor (inner callback) receives that same reading
```

The office gets its own connection when its first reading arrives. Later readings use those
connections; looking up a pipe alone does not trigger discovery.

Use the registrar within the discovery callback. Calling `register` after that callback returns
throws `IllegalStateException`.

The subscription itself is queued. In this example, one caller submits the subscription before
submitting the readings, so the circuit processes the registration first.

We can add another observer without modifying either room's producer. Insert this just before
`kitchen.emit(21)` above:

```java
temperatures.subscribe(
  circuit.subscriber(
    cortex.name("warm-rooms"),
    (subject, registrar) ->
      registrar.register(value -> {
        if (value >= 22) {
          System.out.println("Warm room: " + subject.name());
        }
      }
    )
  )
);
```

The second observer reports `Warm room: kitchen` for the final reading. Each subscription makes
its own connections; the producers still just call `emit`. Both observers receive the kitchen's
readings, and each decides what to do with them.

The returned subscription handle lets us remove the display independently. After waiting for the
readings, the example calls `display.close()` to queue removal of its connections. In the
two-observer version, the warm-room observer remains connected until the circuit closes; we do not
need to retain its handle unless we want to remove it earlier.

## 4. Refine readings with a fiber

Suppose sensors occasionally report implausible values, and repeated unchanged readings add
noise to the display. A **fiber** describes a sequence of operations that keeps the emission
type the same.

For this example, accept readings between −20 and 60 degrees Celsius, then suppress consecutive
duplicates:

```java
var cleanReadings =
  cortex.fiber(Integer.class)
    .guard(value -> value >= -20 && value <= 60)
    .diff();
```

This creates a recipe. Attaching it to a pipe creates the processing path and any operator state
that attachment needs. For one pipe, `cleanReadings.pipe(kitchen)` would return an input that
filters values before forwarding them to `kitchen`. We want that treatment for every room, so
we use `temperatures.pool(cleanReadings)` to create a name-indexed view of these filtered inputs.

Here is the replacement `try` body. It keeps the named-room display and puts the fiber ahead
of each room's conduit pipe:

```java
var temperatures = circuit.conduit(Integer.class);

temperatures.subscribe(
  circuit.subscriber(
    cortex.name("display"),
    (subject, registrar) ->
      registrar.register(
        value -> System.out.println(subject.name() + ": " + value)
      )
  )
);

var cleanReadings =
  cortex.fiber(Integer.class)
    .guard(value -> value >= -20 && value <= 60)
    .diff();

var inputs = temperatures.pool(cleanReadings);
var kitchen = inputs.get(cortex.name("kitchen"));
var office = inputs.get(cortex.name("office"));

kitchen.emit(21);
kitchen.emit(21);
kitchen.emit(200);
office.emit(21);
kitchen.emit(22);
kitchen.emit(21);

circuit.await();
```

Output:

```text
kitchen: 21
office: 21
kitchen: 22
kitchen: 21
```

The kitchen's repeated `21` is dropped, and `200` fails the guard. The office's first `21` passes:
each named attachment has its own duplicate-tracking state. The kitchen's final `21` also passes
because it follows `22`. `diff` suppresses consecutive duplicates. For suppression of every
previously seen value within an attachment, see `Fiber.distinct()`.

Each lookup in `inputs` returns the same filtered pipe for that name, preserving its history.
The path is now:

```text
room producer → guard → diff → named conduit pipe → observers
```

Give producers pipes from `inputs` when this filtering is required. A pipe obtained directly
from `temperatures` still accepts readings without applying the fiber.

## 5. Change the representation with a flow

A downstream consumer may need a structured reading with a room name and both temperature
scales. A **flow** can change the emission type. It also supports operations that accumulate
state across readings.

Keep the filtered room inputs and replace the display with a flow that creates a `Reading`.
Here is the replacement `try` body:

```java
record Reading(String room, int celsius, double fahrenheit) {
}

var temperatures = circuit.conduit(Integer.class);

var output =
  circuit.pipe((Reading reading) -> System.out.println(reading));

temperatures.subscribe(
  circuit.subscriber(
    cortex.name("display"),
    (subject, registrar) -> {

      var intake =
        cortex.flow(Integer.class)
          .map(value -> new Reading(
            subject.name().toString(), value, value * 9.0 / 5.0 + 32
          )).pipe(output);

      registrar.register(intake);

    }
  )
);

var cleanReadings =
  cortex.fiber(Integer.class)
    .guard(value -> value >= -20 && value <= 60)
    .diff();

var inputs = temperatures.pool(cleanReadings);
var kitchen = inputs.get(cortex.name("kitchen"));
var office = inputs.get(cortex.name("office"));

kitchen.emit(20);
kitchen.emit(20);
office.emit(25);

circuit.await();
```

Output:

```text
Reading[room=kitchen, celsius=20, fahrenheit=68.0]
Reading[room=office, celsius=25, fahrenheit=77.0]
```

The producers still emit integers, and the fiber still removes the kitchen's duplicate. For
each room, the subscriber attaches a flow that turns an integer into an immutable `Reading`.
The mapping captures that room's name; all the resulting records go to the shared output pipe.

Notice the two ends of `.pipe(output)`: `output` accepts `Reading`, while the returned `intake`
pipe accepts `Integer`. The flow connects those types, and the registrar connects its input to the
room's channel. The fiber and flow operations both execute on the circuit.

A flow can also remember what came before. To focus on that operation, this alternative `try`
body uses one stream. It reports the highest temperature seen so far:

```java
var output =
  circuit.pipe((Integer value) -> System.out.println(value));

var temperature =
  cortex.flow(Integer.class)
    .scan(
      () -> Integer.MIN_VALUE,
      (highest, value) -> Math.max(highest, value)
    ).pipe(output);

temperature.emit(21);
temperature.emit(19);
temperature.emit(23);

circuit.await();
```

Output:

```text
21
21
23
```

`scan` starts from a seed and emits the updated running state for each input. After seeing `21`,
the lower reading of `19` leaves the high at `21`; `23` raises it. The seed itself is not emitted.
Each attachment gets its own state, so this recipe can also produce independent running maxima
for different rooms.

## 6. Understand what the circuit orders

All these examples share one circuit. Its receptors and fiber/flow operations execute
sequentially within that circuit. When processing a reading causes more emissions on that
circuit, those emissions run before the circuit starts the next external input.

The last example printed the running maximum. Now let the caller read it after processing
finishes, and add an alert that can read it during processing. We will keep one stream here
to make the order easy to follow.

We need one new type: a **cell**, a holder for a current value. `highest.pipe().emit(value)`
queues a replacement on its owning circuit; `highest.get()` reads the latest published value.
The cell stores what we send it. We compute the maximum explicitly in a receptor this time so
we can see the cell update and alert being queued in order. Folding a stream into running state
is `scan`'s job, as in §5; the read-compute-emit written out here exposes the ordering rather
than proposing a pattern to copy.

Here is the complete program, including imports and cleanup:

```java
import io.humainary.substrates.api.Substrates;

public class Substrates101 {

  static void main(String[] args) {

    var cortex = Substrates.cortex();
    var circuit = cortex.circuit();

    try {

      var highest = circuit.cell(Integer.MIN_VALUE);

      var alerts =
        circuit.pipe((Integer value) ->
          System.out.println("alert: " + value + ", highest: " + highest.get())
        );

      var readings =
        circuit.pipe((Integer value) -> {
          System.out.println("reading: " + value);
          highest.pipe().emit(Math.max(highest.get(), value));
          if (value >= 22) {
            alerts.emit(value);
          }
        });

      readings.emit(21);
      readings.emit(23);
      readings.emit(19);

      circuit.await();
      System.out.println("highest: " + highest.get());

    } finally {

      circuit.closeAwait();

    }

  }

}
```

Output:

```text
reading: 21
reading: 23
alert: 23, highest: 23
reading: 19
highest: 23
```

Processing `21` queues a cell update, and that update completes before the callback for `23`
begins. Processing `23` queues the cell update first and the alert second. The alert therefore
reads the updated high of `23`, and it runs before the callback for `19`. That final reading
sees the earlier high and keeps it at `23`. The minimum integer seeds the calculation so the
first reading establishes the initial high.

The cell update is still queued: calling `highest.get()` on the next line after emitting its
replacement would read the previous value inside that callback. Our alert is a later queued
operation, so it sees the replacement. Only the circuit performs the read/compute/update
sequence in this example; doing that sequence from competing producer threads would not make
it atomic. For callers that need to submit an atomic read/compute/update operation, `Port.update`
queues the whole transformation on the owning circuit; a port does not expose a direct read
accessor.

A cell's `get` is also safe to call from the caller thread. The final `await` establishes
that all three readings and their cascades have completed before that caller reads the result.
Without the wait, it could observe an earlier published value. The cell's updates are confined
to the circuit, while its published integer is available to readers outside it.

Work emitted during circuit processing is called **transit** work. It drains in FIFO order before
the next external, or **ingress**, item. Further emissions from transit callbacks join the end
of that transit work; they also complete before the next ingress item. Callbacks do not invoke
one another recursively.

To extend the example's one-level cascade, suppose input `A` emits `A1` and `A2`, and processing
`A1` emits `A1a`, while external input `B` is waiting:

```text
A → A1 → A2 → A1a → B
```

`A1a` joins behind the already-queued `A2`; recursive dispatch would put it ahead of `A2`.
Both complete before `B` begins.

The guarantee is local to one circuit. Concurrent producers can race to establish the accepted
input order, and separate circuits can run in parallel. Substrates preserves the circuit's
accepted order; it does not make racing callers submit in the same order on every run.

Closing the circuit while work is still queued does not guarantee that work will finish.
The provider documents whether pending work completes or is dropped.

## 7. Let a reading loop back

The ordering rules in §6 exist to make one thing safe: a value can come back around. Suppose the
kitchen has a heater, and the thermostat responds to a cold reading by warming the room — which
produces another reading. The receptor emits into the very channel that fed it, and the readings
form a cycle.

Here is the complete program:

```java
import io.humainary.substrates.api.Substrates;

public class Substrates101 {

  static void main(String[] args) {

    var cortex = Substrates.cortex();
    var circuit = cortex.circuit();

    try {

      var temperatures = circuit.conduit(Integer.class);

      temperatures.subscribe(
        circuit.subscriber(
          cortex.name("thermostat"),
          (subject, registrar) ->
            registrar.register(value -> {
              System.out.println(subject.name() + ": " + value);
              if (value < 21) {
                temperatures.get(subject.name()).emit(value + 1);
              }
            })
        )
      );

      var kitchen = temperatures.get(cortex.name("kitchen"));
      var office = temperatures.get(cortex.name("office"));

      kitchen.emit(18);
      office.emit(20);

      circuit.await();

    } finally {

      circuit.closeAwait();

    }

  }

}
```

Output:

```text
kitchen: 18
kitchen: 19
kitchen: 20
kitchen: 21
office: 20
office: 21
```

Each step around the loop is another piece of queued work, not a nested call. A library that
invoked receptors directly would run this as re-entrant recursion, one stack frame per step, and
a long enough climb would overflow the stack. Here the length of the loop costs no stack at all,
which is what makes feedback topologies practical rather than hazardous.

The kitchen climbs from `18` to `21` before the office's reading is processed, whatever the
timing of the two submissions. That is §6's priority rule seen from the other side: the effects
of a reading, including the further readings they cause, complete before the next external
reading begins. A cycle runs to completion as a unit.

`subject.name()` names the channel being wired, so each room feeds back into itself rather than
into a shared loop. The two rooms run the same recipe over separate channels and separate values.

Nothing here detects a loop that never ends. Such a loop stays iterative and never overflows the
stack, but it also never runs out of work: the circuit keeps processing what the loop produces
and never returns to the readings waiting outside it, so `await` does not return. A terminating
condition is the program's responsibility; ours is `value < 21`.

Cycles can also span circuits. Emitting into another circuit's pipe hands the value to that
circuit as external input, so two circuits can feed each other and neither one waits for the
other; each keeps its own order, and they process in parallel. What does not cross the boundary
is the guarantee: one circuit's cascade is not a unit from the other's point of view, and values
arriving from several circuits can interleave. Wait for a circuit only from outside it, as §1
noted: calling `await` on a circuit from within one of its own callbacks throws
`IllegalStateException`.

## Where to go next

We now have producers holding pipes, named channels collected in a conduit, observers connected
through subscriptions, and reusable processing attached with fibers and flows. The circuit
provides the execution and ordering boundary for the network, and that boundary is what lets a
value loop back through it.

A useful next exercise is to give the rooms independent running maxima. Start from §5's flow
program and replace its mapping with the `scan` from the same section. Move the output pipe into
the discovery callback so it can print `subject.name()` alongside the integer maximum; the
`Reading` record is no longer needed. Emit `23`, then `19` in the kitchen and `20` in the office:
the kitchen should report `23` twice, and the office `20`.

For an ordering exercise, return to §6 and move `highest.pipe().emit(...)` below the entire `if`
block, so the alert is queued before the cell update. Predict the alert's output before running it.

Both operations still run before the next external reading, but FIFO transit ordering now puts
the alert first. It therefore reports `alert: 23, highest: 21`. The final caller-side maximum
remains `23`.

The examples deliberately submit from one caller so their output order is reproducible; the
callbacks still run on the circuit's processing thread. Multiple producers and hierarchical
names are follow-up topics once this admission-and-processing boundary is familiar.

For other forms of state access, explore ports and pins alongside cells. When groups of connections
need to come and go together, explore scopes. The [glossary](GLOSSARY.md) introduces those roles,
and [Substrates: Design Rationale](SUBSTRATES.md) explains the choices behind them.
