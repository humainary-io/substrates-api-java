/// Copyright © 2025 William David Louth
package io.humainary.substrates.api;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Member;
import java.time.Instant;
import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.SOURCE;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.Spliterator.*;

/// The Substrates API provides a flexible framework for building event-driven and observability systems
/// by combining concepts of circuits, conduits, channels, pipes, subscribers, subscriptions, and subjects.
///
/// ## Key Features
///
///     - **Circuit**: An interface representing a central processing engine that manages data flow across
///     channels and conduits, with precise ordering guarantees for emitted events.
///
///     - **Conduit**: An interface used for routing emitted values by channels (producers) to pipes (consumers).
///
///     - **Channel**: An interface representing a subject-based port into an owning conduit's pipeline.
///
///     - **Pipe**: An interface that serves as an abstraction for passing typed values along a pipeline.
///
///     - **Subscriber**: Dynamically subscribes to a source and registers pipes with the subjects of channels.
///
///     - **Subscription**: An interface for managing and unregistering subscriptions.
///
///     - **Subject**: A hierarchical reference system that allows entities to be observed and addressed within the processing framework.
///
///     - **Clock**: A timer utility that triggers events at specified intervals, supporting applications with time-driven behaviors.
///
/// ## Practical Applications
/// Substrates API is well-suited for:
///
///     - Real-time data processing pipelines
///
///     - Event-driven architectures with dynamic subscriptions
///
///     - Observability frameworks and instrumentation
///
///     - Digital Twin mirrored state and computational processing units
///
/// @author autoletics
/// @since 1.0

public interface Substrates {

  /// Indicates a type that serves primarily as an abstraction for other types.
  @Documented
  @Retention ( SOURCE )
  @Target ( TYPE )
  @interface Abstract {
  }

  /// Indicates a type that serves a role in the assembly of a pipeline.
  @Abstract
  interface Assembly {
  }


  /// A capture of an emitted value with its associated subject
  ///
  /// @param <E> the class type of emitted value
  /// @see Cortex#capture(Subject, Object)
  /// @see Sink#drain()

  @Provided
  interface Capture < E > {

    /// Returns the emitted value.
    ///
    /// @return The emitted value

    @NotNull
    E emission ();


    /// Returns the subject that emitted the value.
    ///
    /// @return The subject that emitted the value
    /// @see Subject

    @NotNull
    Subject subject ();

  }

  /// A (subject) named pipe managed by a conduit.
  ///
  /// @param <E> the class type of emitted value
  /// @see Conduit
  /// @see Inlet
  /// @see Circuit#conduit(Composer)

  @Provided
  non-sealed interface Channel < E >
    extends Substrate,
            Inlet < E > {


    /// Returns a pipe that will use this channel to emit values.
    ///
    /// @param sequencer The sequencer responsible for creating paths in the conduit.
    /// @return A pipe instance that will use this channel to emit values
    /// @throws NullPointerException if the specified sequencer is `null`
    /// @see Pipe
    /// @see Sequencer
    /// @see Path

    @NotNull
    Pipe < E > pipe (
      @NotNull Sequencer < ? super Path < E > > sequencer
    );

  }

  /// A computational network of conduits, containers, clocks, channels, and pipes.
  /// Circuit serves as the central processing engine that manages data flow across
  /// the system, providing precise ordering guarantees for emitted events and
  /// coordinating the interaction between various components.
  ///
  /// Circuits can create and manage clocks, conduits, and containers, allowing for
  /// complex event processing pipelines to be constructed and maintained.
  ///
  /// @see Cortex#circuit()
  /// @see Clock
  /// @see Conduit
  /// @see Container
  /// @see Queue

  @Provided
  non-sealed interface Circuit
    extends Component < State >,
            Tap < Circuit > {

    /// Returns a clock that will use this circuit to emit clock cycle events.
    ///
    /// @return A clock instance that will use this circuit to emit clock cycle events.
    /// @see Clock
    /// @see Clock.Cycle

    @NotNull
    Clock clock ();


    /// Returns a clock that will use this circuit to emit clock cycle events.
    ///
    /// @param name the name given to the clock's subject
    /// @return A clock instance that will use this circuit to emit clock cycle events.
    /// @throws NullPointerException if the specified name is `null`

    @NotNull
    Clock clock (
      @NotNull Name name
    );


    /// Returns a conduit that will use this circuit to process and transfer values emitted.
    ///
    /// @param composer The composer that forms percepts around a channel.
    /// @param <E>      The class type of emitted value.
    /// @param <P>      The class type of the percept.
    /// @return A conduit that will use this circuit to process and deliver values emitted.
    /// @throws NullPointerException if the specified composer is `null`
    /// @see Conduit
    /// @see Composer
    /// @see Channel

    @NotNull
    < P, E > Conduit < P, E > conduit (
      @NotNull Composer < ? extends P, E > composer
    );


    /// Returns a conduit that will use this circuit to process and transfer values emitted.
    ///
    /// @param name     the name given to the conduit's subject
    /// @param composer The composer that forms percepts around a channel.
    /// @param <P>      The class type of the percept.
    /// @param <E>      The class type of emitted value.
    /// @return A conduit that will use this circuit to process and deliver values emitted.
    /// @throws NullPointerException if the specified name or composer is `null`

    @NotNull
    < P, E > Conduit < P, E > conduit (
      @NotNull Name name,
      @NotNull Composer < ? extends P, E > composer
    );


    /// Returns a conduit that will use this circuit to process and transfer values emitted.
    ///
    /// @param name      the name given to the conduit's subject
    /// @param composer  The composer that forms percepts around a channel.
    /// @param sequencer The sequencer responsible for creating paths in the conduit.
    /// @param <P>       The class type of the percept.
    /// @param <E>       The class type of emitted value.
    /// @return A conduit that will use this circuit to process and deliver values emitted.
    /// @throws NullPointerException if the specified name or composer is `null`

    @NotNull
    < P, E > Conduit < P, E > conduit (
      @NotNull Name name,
      @NotNull Composer < ? extends P, E > composer,
      @NotNull Sequencer < Path < E > > sequencer
    );


    /// Returns a container that will use this circuit to create conduits
    ///
    /// @param composer The composer that forms percepts around a channel.
    /// @throws NullPointerException if the specified composer is `null`
    /// @see Container
    /// @see Pool
    /// @see Source

    @NotNull
    < P, E > Container < Pool < P >, Source < E > > container (
      @NotNull Composer < P, E > composer
    );


    /// Returns a container that will use this circuit to create conduits
    ///
    /// @param name     the name given to the container's subject.
    /// @param composer The composer that forms percepts around a channel.
    /// @throws NullPointerException if the specified name or composer is `null`

    @NotNull
    < P, E > Container < Pool < P >, Source < E > > container (
      @NotNull Name name,
      @NotNull Composer < P, E > composer
    );


    /// Returns a container that will use this circuit to create conduits
    ///
    /// @param name      the name given to the container's subject.
    /// @param composer  The composer that forms percepts around a channel.
    /// @param sequencer The sequencer responsible for creating paths in conduits.
    /// @throws NullPointerException if the specified name, composer, or sequencer is `null`

    @NotNull
    < P, E > Container < Pool < P >, Source < E > > container (
      @NotNull Name name,
      @NotNull Composer < P, E > composer,
      @NotNull Sequencer < Path < E > > sequencer
    );


    /// Returns a [Queue] that can be used to coordinate execution with the
    /// underlying pipeline processing as well as to execute scripts
    /// @see Queue
    /// @see Script

    @NotNull
    Queue queue ();

  }

  /// A component that emits clock ticks.
  /// @see Circuit#clock()
  /// @see Instant
  /// @see Clock.Cycle
  @Provided
  non-sealed interface Clock
    extends Component < Instant > {

    /// A utility method that subscribes a pipe to events of a particular cycle
    ///
    /// @param cycle the cycle value to be subscribed to
    /// @param pipe  the pipe that will consume ticks events
    /// @return A subscription that can be used to cancel further delivery of tick events
    /// @throws NullPointerException if the specified cycle or pipe are `null`
    /// @see Clock.Cycle
    /// @see Pipe
    /// @see Subscription

    @NotNull
    Subscription consume (
      @NotNull Name name,
      @NotNull Cycle cycle,
      @NotNull Pipe < Instant > pipe
    );


    /// Represents a published clock cycle.
    ///
    /// The enum value is used to define the [Name][Name] for a clock sensor [Subject][Subject].
    ///
    /// @see Name#name(Enum)

    enum Cycle {

      /// Emitted on every millisecond passing.
      MILLISECOND ( 1L ),

      /// Emitted on every second passing.
      SECOND ( 1000 ),

      /// Emitted on every minute passing.
      MINUTE ( 1000L * 60 );

      private final long units;

      /// Constructor for the Cycle enum.
      ///
      /// @param units the number of time units this cycle represents

      Cycle (
        final long units
      ) {

        this.units
          = units;

      }

      /// Returns the number of time units this cycle represents.
      ///
      /// @return the number of time units

      public long units () {
        return units;
      }

    }

  }

  /// A utility interface for scoping the work performed against a resource.

  @Utility
  @Temporal
  interface Closure < R extends Resource > {

    /// Calls a consumer, with an acquired resource, within an automatic resource management (ARM) scope.
    ///
    /// @param consumer the consumer to be executed within an automatic resource (ARM) management scope.

    void consume (
      @NotNull Consumer < ? super R > consumer
    );

  }

  /// An abstraction that represents a managed event-sourcing component
  ///
  /// @param <E> the class type of emitted value

  @Abstract
  sealed interface Component < E >
    extends Substrate,
            Context < E >,
            Resource
    permits Circuit,
            Clock,
            Container {

  }

  /// A composer that forms percepts around a channel.
  ///
  /// @param <E> the class type of emitted value
  /// @param <P> The class type of the percept
  /// @see Channel
  /// @see Pipe
  /// @see Circuit#conduit(Composer)

  @Abstract
  @Extension
  interface Composer < P, E > {


    /// Returns the identity composer.
    ///
    /// @param <E> the class type of emitted value
    /// @return the identity composer

    @NotNull
    static < E > Composer < Channel < E >, E > channel () {

      return input -> input;

    }


    /// Returns a composer that returns a typed channel
    ///
    /// @param <E>  the class type of emitted value
    /// @param type the class type of the emitted value
    /// @return a composer that returns a typed channel
    /// @throws NullPointerException if the specified type param is `null`

    @NotNull
    static < E > Composer < Channel < E >, E > channel (
      @NotNull final Class < E > type
    ) {

      requireNonNull ( type );

      return channel ();

    }


    /// Returns a composer that returns a channel's pipe
    ///
    /// @param <E>       the class type of emitted value
    /// @param sequencer The sequencer responsible for creating paths in the conduit.
    /// @return a composer that returns a channel's pipe
    /// @throws NullPointerException if the specified sequencer is `null`

    @NotNull
    static < E > Composer < Pipe < E >, E > pipe (
      @NotNull final Sequencer < ? super Path < E > > sequencer
    ) {

      requireNonNull ( sequencer );

      return channel ->
        channel.pipe (
          sequencer
        );

    }


    /// Returns a composer that returns a channel's pipe
    ///
    /// @param <E>  the class type of emitted value
    /// @param type the class type of the emitted value
    /// @return a composer that returns a typed channel's pipe
    /// @throws NullPointerException if the specified type param is `null`

    @NotNull
    static < E > Composer < Pipe < E >, E > pipe (
      @NotNull final Class < E > type
    ) {

      requireNonNull ( type );

      return Inlet::pipe;

    }


    /// Returns a composer that returns a channel's pipe
    ///
    /// @param <E> the class type of emitted value
    /// @return a composer that returns a channel's pipe

    @NotNull
    static < E > Composer < Pipe < E >, E > pipe () {

      return Inlet::pipe;

    }


    /// Composes a channel into a percept.
    ///
    /// @param channel the channel to be composed
    /// @return the composition from the channel
    /// @throws NullPointerException if the specified channel is `null`

    @NotNull
    P compose (
      @NotNull Channel < E > channel
    );


    /// Returns a new composer that applies a function the results of this composer
    ///
    /// @param <R> the class type of resulting percept
    /// @return a composer that applies a function the results of this composer
    /// @throws NullPointerException if the specified mapper param is `null`

    @NotNull
    default < R > Composer < R, E > map (
      @NotNull final Function < ? super P, ? extends R > mapper
    ) {

      requireNonNull ( mapper );

      return channel -> mapper.apply (
        compose ( channel )
      );

    }

  }


  /// Creates percepts that emit captured data into pipes.
  ///
  /// @param <P> the class type of the percept
  /// @param <E> the class type of emitted value
  /// @see Channel
  /// @see Pipe
  /// @see Container
  /// @see Circuit#conduit(Composer)

  @Provided
  interface Conduit < P, E >
    extends Container < P, E >,
            Tap < Conduit < P, E > > {

  }

  /// Creates and manages an instance pool and notifies of events associated with such instances
  ///
  /// @param <P> the class type of the pooled object
  /// @param <E> the class type of component (source) emittance
  /// @see Pool
  /// @see Component
  /// @see Circuit#container(Composer)

  @Provided
  non-sealed interface Container < P, E >
    extends Pool < P >,
            Component < E > {

  }

  /// A type that serves to provide access to a source.
  ///
  /// @param <E> the class type of emitted value
  /// @see Source
  /// @see Component

  @Abstract
  sealed interface Context < E >
    permits Component {

    /// Returns the source provided by this context.
    ///
    /// @return A non-null reference to the (emitting) source of this context.
    /// @see Source

    @NotNull
    Source < E > source ();

  }

  /// The main entry point into the underlying substrates runtime.
  /// @see Circuit
  /// @see Name
  /// @see Scope
  /// @see Sink
  /// @see State
  /// @see Subscriber

  @Provided
  interface Cortex {


    /// Creates a capture of an emitted value with its associated subject.
    ///
    /// @param subject  the subject that emitted the value
    /// @param emission the emitted value
    /// @param <E>      the class type of emitted value
    /// @return a new capture instance containing the subject and emission
    /// @throws NullPointerException if the subject or emission params are `null`

    @NotNull
    < E > Capture < E > capture (
      @NotNull final Subject subject,
      @NotNull final E emission
    );


    /// Returns a newly created circuit instance
    ///
    /// @return A non-null circuit instance

    @NotNull
    Circuit circuit ();


    /// Returns a newly created circuit instance
    ///
    /// @param name the name assigned to the circuit's subject
    /// @return A newly created circuit instance
    /// @throws NullPointerException if the specified name is `null`

    @NotNull
    Circuit circuit (
      @NotNull Name name
    );


    /// Returns a new name that has this name as a direct or indirect prefix.
    ///
    /// @param path the string to be parsed and appended to this name
    /// @return A new name with the path appended as one or more name parts.
    /// @throws NullPointerException if the path parameter is `null`

    @NotNull
    Name name (
      @NotNull String path
    );


    /// Returns a new name that has this name as a direct prefix and a path of the enum name.
    ///
    /// @param path the enum to be appended to this name
    /// @return A new name with the enum name appended a name part.
    /// @throws NullPointerException if the path parameter is `null`

    @NotNull
    Name name (
      @NotNull Enum < ? > path
    );


    /// Returns a name from iterating over string values.
    ///
    /// @param it the iterable to be iterated over
    /// @return The name following concatenation of name parts
    /// @throws NullPointerException if the iterable is `null` or one of the values returned is `null`

    @NotNull
    Name name (
      @NotNull Iterable < String > it
    );


    /// Returns a name from iterating over values mapped to strings.
    ///
    /// @param <T>    the type of each value iterated over
    /// @param it     the iterable to be iterated over
    /// @param mapper the function to be used to map the iterable value type to a string
    /// @return The name following concatenation of name parts
    /// @throws NullPointerException if the iterable is `null` or one of the values returned is `null`

    @NotNull
    < T > Name name (
      @NotNull Iterable < ? extends T > it,
      @NotNull Function < T, String > mapper
    );


    /// Returns a name from iterating over string values.
    ///
    /// @param it the [Iterator] to be iterated over
    /// @return The name following concatenation of name parts
    /// @throws NullPointerException if the [Iterable] is `null` or one of the values returned is `null`
    /// @see #name(Iterable)

    @NotNull
    Name name (
      @NotNull Iterator < String > it
    );


    /// Returns a name from iterating over values mapped to strings.
    ///
    /// @param <T>    the type of each value iterated over
    /// @param it     the iterator to be iterated over
    /// @param mapper the function to be used to map the iterator value type to a string
    /// @return The name following concatenation of name parts
    /// @throws NullPointerException if the iterator is `null` or one of the values returned is `null`
    /// @see #name(Iterable, Function)

    @NotNull
    < T > Name name (
      @NotNull Iterator < ? extends T > it,
      @NotNull Function < T, String > mapper
    );


    /// Creates a name from a class.
    ///
    /// @param type the class to be mapped to a name
    /// @return A name where the string representation is `name.toString().equals(cls.getName())`
    /// @throws NullPointerException if the type param is `null`

    @NotNull
    Name name (
      @NotNull Class < ? > type
    );


    /// Creates a name from a member
    ///
    /// @param member the member to be mapped to a name
    /// @return A name mapped to the member
    /// @throws NullPointerException if the member param is `null`

    @NotNull
    Name name (
      @NotNull Member member
    );


    /// Creates a pool that always returns the same singleton instance.
    ///
    /// @param singleton the singleton instance to be returned by the pool
    /// @param <T>       the class type of the singleton
    /// @return a pool that always returns the same singleton instance
    /// @throws NullPointerException if the singleton param is `null`

    @NotNull
    < T > Pool < T > pool (
      @NotNull final T singleton
    );


    /// Returns a new scope instance that manages a provided resource.
    ///
    /// @return A scope that calls close on the underlying resource when it is closed
    /// @throws NullPointerException if the name param is `null`

    @NotNull
    Scope scope (
      @NotNull Name name
    );


    /// Returns a new scope instance that manages a provided resource.
    ///
    /// @return A scope that calls close on the underlying resource when it is closed

    @NotNull
    Scope scope ();


    /// Creates a Sink instance for the given context's source.
    ///
    /// @param context the context providing the source
    /// @return a new Sink instance associated with the provided context's source
    /// @throws NullPointerException if context is null

    @NotNull
    default < E > Sink < E > sink (
      @NotNull final Context < E > context
    ) {

      return sink (
        context.source ()
      );

    }


    /// Creates a Sink object from the given Source object.
    ///
    /// @param source the source from which the sink will capture emissions
    /// @param <E>    the class type of emitted value
    /// @return a new Sink object of the same type as the given source
    /// @throws NullPointerException if the source param is null

    @NotNull
    < E > Sink < E > sink (
      @NotNull final Source < E > source
    );


    /// Creates a slot with a class type of `boolean`.
    ///
    /// @param name  the name for the slot
    /// @param value the value for the slot
    /// @return A slot with a class type of `boolean`.
    /// @throws NullPointerException if the name param is `null`

    @NotNull
    Slot < Boolean > slot (
      @NotNull Name name,
      boolean value
    );


    /// Creates a slot with a class type of `int`.
    ///
    /// @param name  the name for the slot
    /// @param value the value for the slot
    /// @return A slot with a class type of `int`.
    /// @throws NullPointerException if the name param is `null`

    @NotNull
    Slot < Integer > slot (
      @NotNull Name name,
      int value
    );


    /// Creates a slot with a class type of `long`.
    ///
    /// @param name  the name for the slot
    /// @param value the value for the slot
    /// @return A slot with a class type of `long`.
    /// @throws NullPointerException if the name param is `null`

    @NotNull
    Slot < Long > slot (
      @NotNull Name name,
      long value
    );


    /// Creates a slot with a class type `int`.
    ///
    /// @param name  the name for the slot
    /// @param value the value for the slot
    /// @return A slot with a class type of `int`.
    /// @throws NullPointerException if the name param is `null`

    @NotNull
    Slot < Double > slot (
      @NotNull Name name,
      double value
    );


    /// Creates a slot with a class type of `float`.
    ///
    /// @param name  the name for the slot
    /// @param value the value for the slot
    /// @return A slot with a class type of `float`.
    /// @throws NullPointerException if the name param is `null`

    @NotNull
    Slot < Float > slot (
      @NotNull Name name,
      float value
    );


    /// Creates a slot with a class type of `String`.
    ///
    /// @param name  the name for the slot
    /// @param value the value for the slot
    /// @return A slot with a class type of `String`.
    /// @throws NullPointerException if name or value params are `null`

    @NotNull
    Slot < String > slot (
      @NotNull Name name,
      @NotNull String value
    );


    /// Creates a slot with a class type of name.
    ///
    /// @param name  the name for the slot
    /// @param value the value for the slot
    /// @return A slot with a class type of name.
    /// @throws NullPointerException if name or value params are `null`

    @NotNull
    Slot < Name > slot (
      @NotNull Name name,
      @NotNull Name value
    );


    /// Creates a slot with a class type of state.
    ///
    /// @param name  the name for the slot
    /// @param value the value for the slot
    /// @return A slot with a class type of state.
    /// @throws NullPointerException if name or value params are `null`

    @NotNull
    Slot < State > slot (
      @NotNull Name name,
      @NotNull State value
    );


    /// Creates an empty state.
    ///
    /// @return An empty state.

    @NotNull
    State state ();


    /// Creates a state with a single slot
    ///
    /// @param name  The name of the slot
    /// @param value The value of the slot
    /// @return A state holding a single slot.
    /// @throws NullPointerException if the name param is `null`

    @NotNull
    State state (
      @NotNull Name name,
      int value
    );


    /// Creates a state with a single slot
    ///
    /// @param name  The name of the slot
    /// @param value The value of the slot
    /// @return A state holding a single slot.
    /// @throws NullPointerException if the name param is `null`

    @NotNull
    State state (
      @NotNull Name name,
      long value
    );


    /// Creates a state with a single slot
    ///
    /// @param name  The name of the slot
    /// @param value The value of the slot
    /// @return A state holding a single slot.
    /// @throws NullPointerException if the name param is `null`

    @NotNull
    State state (
      @NotNull Name name,
      float value
    );


    /// Creates a state with a single slot
    ///
    /// @param name  The name of the slot
    /// @param value The value of the slot
    /// @return A state holding a single slot.
    /// @throws NullPointerException if the name param is `null`

    @NotNull
    State state (
      @NotNull Name name,
      double value
    );


    /// Creates a state with a single slot
    ///
    /// @param name  The name of the slot
    /// @param value The value of the slot
    /// @return A state holding a single slot.
    /// @throws NullPointerException if the name param is `null`

    @NotNull
    State state (
      @NotNull Name name,
      boolean value
    );


    /// Creates a state with a single slot
    ///
    /// @param name  The name of the slot
    /// @param value The value of the slot
    /// @return A state holding a single slot.
    /// @throws NullPointerException if the name or value params are `null`

    @NotNull
    State state (
      @NotNull Name name,
      @NotNull String value
    );


    /// Creates a state with a single slot
    ///
    /// @param name  The name of the slot
    /// @param value The value of the slot
    /// @return A state holding a single slot.
    /// @throws NullPointerException if the name or value params are `null`

    @NotNull
    State state (
      @NotNull Name name,
      @NotNull Name value
    );


    /// Creates a state with a single slot
    ///
    /// @param name  The name of the slot
    /// @param value The value of the slot
    /// @return A state holding a single slot.
    /// @throws NullPointerException if the name or value params are `null`

    @NotNull
    State state (
      @NotNull Name name,
      @NotNull State value
    );


    /// Creates a subscriber with the specified name and subscribing behavior.
    ///
    /// @param name       the name to be used by the subject assigned to the subscriber
    /// @param subscriber the subscribing behavior to be applied when a new subject emits as an emission
    /// @return a new non-null subscriber instance configured with the provided subscribing behavior

    @NotNull
    < E > Subscriber < E > subscriber (
      @NotNull final Name name,
      @NotNull final BiConsumer < Subject, Registrar < E > > subscriber
    );


    /// Creates a subscriber with the specified name and pool of pipes.
    ///
    /// @param name the name to be used by the subject assigned to the subscriber
    /// @param pool the pool of pipes to be used when a subscription is requested
    /// @param <E>  the class type of emitted value
    /// @return a new non-null subscriber instance configured with the provided pool
    /// @throws NullPointerException if the name or pool params are `null`

    @NotNull
    < E > Subscriber < E > subscriber (
      @NotNull final Name name,
      @NotNull Pool < ? extends Pipe < E > > pool
    );

  }


  /// An interface that provides efficient access to a circuit's work queue.

  @Provided
  @Temporal
  interface Current
    extends Substrate {

    /// Posts a runnable to be executed asynchronously
    ///
    /// @throws NullPointerException if the runnable param is `null`

    void post (
      @NotNull Runnable runnable
    );

  }


  /// Indicates a type used by callers or instrumentation kits to extend capabilities.

  @Documented
  @Retention ( SOURCE )
  @Target ( TYPE )
  @interface Extension {
  }

  /// An abstraction of a hierarchically nested structure of enclosed whole-parts
  ///
  /// @param <T> The self-referencing class type

  @Abstract
  @Extension
  interface Extent < T extends Extent < T > >
    extends Iterable < T >,
            Comparable < T > {


    /// Compares this object with the specified object for order.
    ///
    /// Returns a negative integer, zero, or a positive integer as this
    /// object is less than, equal to, or greater than the specified object.
    ///
    /// @return a negative integer, zero, or a positive integer as this object
    ///  is less than, equal to, or greater than the specified object.
    /// @throws NullPointerException if the `other` param is `null`

    @Override
    default int compareTo (
      final T other
    ) {

      requireNonNull ( other );

      return CharSequence.compare (
        path ( '\u0001' ),
        other.path ( '\u0001' )
      );

    }


    /// Returns the depth of this extent within all enclosures.
    ///
    /// @return the depth of this extent within all enclosures.

    default int depth () {

      return fold (
        _ -> 1,
        ( depth, _ ) -> depth + 1
      );

    }


    /// Returns the (parent/prefix) extent wrapped in an optional that encloses this extent.
    ///
    /// @return An optional holding a reference to the enclosing extent or [#empty()].

    @NotNull
    default Optional < T > enclosure () {

      return empty ();

    }


    /// Applies the given consumer to the enclosing extent if it exists.
    ///
    /// @param consumer the consumer to be applied to the enclosing extent
    /// @throws NullPointerException if the consumer is `null`

    default void enclosure (
      @NotNull final Consumer < ? super T > consumer
    ) {

      enclosure ()
        .ifPresent ( consumer );

    }


    /// Returns the extent instance referenced by `this`.
    ///
    /// @return A reference to this extent instance

    @NotNull
    @SuppressWarnings ( "unchecked" )
    default T extent () {

      return (T) this;

    }


    /// Returns the outermost (extreme) extent.
    ///
    /// @return The outermost extent.

    @NotNull
    default T extremity () {

      T prev;

      var current
        = extent ();

      do {

        prev
          = current;

        current
          = current
          .enclosure ()
          .orElse ( null );

      } while ( current != null );

      return prev;

    }


    /// Produces an accumulated value moving from the right (this) to the left (root) in the extent.
    ///
    /// @param <R>         the return type of the accumulated value
    /// @param initializer the function called to initialize the accumulator
    /// @param accumulator the function used to add a value to the accumulator
    /// @return The accumulated result of performing the seed once and the accumulator.
    /// @throws NullPointerException if either initializer or accumulator params are `null`

    default < R > R fold (
      @NotNull final Function < ? super T, ? extends R > initializer,
      @NotNull final BiFunction < ? super R, ? super T, R > accumulator
    ) {

      requireNonNull ( initializer );
      requireNonNull ( accumulator );

      final var it
        = iterator ();

      var result
        = initializer.apply (
        it.next ()
      );

      while ( it.hasNext () ) {

        result
          = accumulator.apply (
          result,
          it.next ()
        );

      }

      return result;

    }


    /// Produces an accumulated value moving from the left (root) to the right (this) in the namespace.
    ///
    /// @param <R>         the return type of the accumulated value
    /// @param initializer the function called to seed the accumulator
    /// @param accumulator the function used to add a value to the accumulator
    /// @return The accumulated result of performing the seed once and the accumulator.
    /// @throws NullPointerException if either initializer or accumulator params are `null`

    default < R > R foldTo (
      @NotNull final Function < ? super T, ? extends R > initializer,
      @NotNull final BiFunction < ? super R, ? super T, R > accumulator
    ) {

      requireNonNull ( initializer );
      requireNonNull ( accumulator );

      return enclosure ()
        .map (
          enclosure
            -> accumulator.apply (
            enclosure.foldTo (
              initializer,
              accumulator
            ),
            extent ()
          )
        ).orElse (
          initializer.apply (
            extent ()
          )
        );

    }


    /// Returns an iterator over the extents moving from the right (`this`) to the left ([#extremity()]).
    ///
    /// @return An iterator for traversing over the nested extents, starting with `this` extent instance.

    @NotNull
    default Iterator < T > iterator () {

      //noinspection ReturnOfInnerClass
      return new Iterator <> () {

        private T extent
          = extent ();

        @Override
        public boolean hasNext () {

          return extent != null;

        }

        @Override
        public T next () {

          final var result = extent;

          if ( result == null ) {
            throw new NoSuchElementException ();
          }

          extent
            = result
            .enclosure ()
            .orElse ( null );

          return result;

        }
      };

    }


    /// Returns a `CharSequence` representation of just this extent.
    ///
    /// @return A non-`null` `CharSequence` representation.

    @NotNull
    CharSequence part ();


    /// Returns a `CharSequence` representation of the subject, including enclosing subjects.
    ///
    /// @return A non-`null` `CharSequence` representation and this subject and its enclosure.

    @NotNull
    default CharSequence path () {

      return
        path (
          '/'
        );

    }


    /// Returns a `CharSequence` representation of this extent and its enclosures.
    ///
    /// @return A non-`null` `String` representation.

    @NotNull
    default CharSequence path (
      @NotNull final char separator
    ) {

      return path (
        Extent::part,
        separator
      );

    }


    /// Returns a `CharSequence` representation of this extent and its enclosures.
    ///
    /// @return A non-`null` `String` representation.
    /// @throws NullPointerException if separator param is `null`

    @NotNull
    default CharSequence path (
      @NotNull final String separator
    ) {

      requireNonNull ( separator );

      return path (
        Extent::part,
        separator
      );

    }


    /// Returns a `CharSequence` representation of this extent and its enclosures.
    ///
    /// @return A non-`null` `String` representation.
    /// @throws NullPointerException if mapper param is `null`

    @NotNull
    default CharSequence path (
      @NotNull final Function < ? super T, ? extends CharSequence > mapper,
      @NotNull final char separator
    ) {

      requireNonNull ( mapper );

      return foldTo (
        first
          -> new StringBuilder ()
          .append (
            mapper.apply (
              first
            )
          ),
        ( result, next )
          -> result.append ( separator )
          .append (
            mapper.apply (
              next
            )
          )
      );

    }


    /// Returns a `CharSequence` representation of this extent and its enclosures.
    ///
    /// @return A non-`null` `String` representation.
    /// @throws NullPointerException if either mapper or separator params are `null`

    @NotNull
    default CharSequence path (
      @NotNull final Function < ? super T, ? extends CharSequence > mapper,
      @NotNull final String separator
    ) {

      requireNonNull ( mapper );
      requireNonNull ( separator );

      return foldTo (
        first
          -> new StringBuilder ()
          .append (
            mapper.apply (
              first
            )
          ),
        ( result, next )
          -> result.append ( separator )
          .append (
            mapper.apply (
              next
            )
          )
      );

    }


    /// Returns a `Stream` containing each enclosing extent starting with `this`.
    ///
    /// @return A `Stream` in which the first element is `this` extent instance, and the
    /// last is the value returned from the [#extremity()] method.
    /// @see #iterator()
    /// @see #extremity()

    @NotNull
    default Stream < T > stream () {

      return StreamSupport.stream (
        Spliterators.spliterator (
          iterator (),
          fold (
            _ -> 1,
            ( sum, _ )
              -> sum + 1
          ).longValue (),
          DISTINCT | NONNULL | IMMUTABLE
        ),
        false
      );

    }


    /// Returns true if this `Extent` is directly or indirectly enclosed within the extent parameter.
    ///
    /// @param enclosure the extent to be checked whether it enclosed this extent
    /// @return true if the extent parameter encloses this extent, directly or indirectly
    /// @throws NullPointerException if `enclosure` param is `null`

    default boolean within (
      @NotNull final Extent < T > enclosure
    ) {

      requireNonNull ( enclosure );

      var current
        = extent ();

      do {

        current
          = current
          .enclosure ()
          .orElse ( null );

        if ( current == enclosure ) {
          return true;
        }

      } while ( current != null );

      return false;

    }

  }

  /// Indicates a method that returns this for method chaining.

  @Documented
  @Retention ( SOURCE )
  @Target ( METHOD )
  @interface Fluent {
  }

  /// An activity that returns a result.
  ///
  /// @param <R> The output of the function
  /// @param <T> The throwable class type

  @Utility
  @Extension
  @FunctionalInterface
  interface Fn < R, T extends Throwable > {

    /// Use this function to force typing of an overloaded method handle at compile-time.
    ///
    /// @param fn  the function to be cast
    /// @param <R> the return type of the [#eval()]
    /// @param <T> the throwable class type thrown by the operation
    /// @return A casting of the specified function
    /// @throws NullPointerException if `fn` param is `null`

    static < R, T extends Throwable > Fn < R, T > of (
      @NotNull final Fn < R, T > fn
    ) {

      requireNonNull ( fn );

      return fn;

    }


    /// Invokes the underlying function.
    ///
    /// @return The result from calling of the function
    /// @throws T The derived throwable type thrown

    R eval () throws T;

  }

  /// A unique identifier interface used to distinguish between instances.
  ///
  /// This interface serves as a marker for unique identifiers within the system,
  /// particularly for subjects and other entities that require distinct identification.

  @Provided
  @Identity
  interface Id {
  }

  /// Indicates a method expected to be idempotent.
  @Documented
  @Retention ( SOURCE )
  @Target ( METHOD )
  @interface Idempotent {
  }

  /// Indicates a type whose instances can be compared by (object) reference for equality.

  @Documented
  @Retention ( SOURCE )
  @Target ( TYPE )
  @interface Identity {
  }


  /// An interface that provides access to a pipe for emitting values
  /// @see Channel
  /// @see Pipe

  @Abstract
  sealed interface Inlet < E >
    permits Channel {

    /// Returns a pipe that this inlet holds.
    ///
    /// @return a non-`null` pipe instance
    /// @see Pipe

    @NotNull
    Pipe < E > pipe ();

  }

  /// Represents one or more name (string) parts, much like a namespace.
  /// @see Subject#name()
  /// @see Extent
  /// @see Cortex#name(String)

  @Identity
  @Provided
  interface Name
    extends Extent < Name > {

    /// The separator used for parsing a string into name tokens.
    char SEPARATOR = '.';


    /// Returns a new name that has this name as a direct or indirect prefix.
    ///
    /// @param suffix the name to be appended to this name
    /// @return A new name with the suffix appended.

    @NotNull
    Name name (
      @NotNull Name suffix
    );


    /// Returns a new name that has this name as a direct or indirect prefix.
    ///
    /// @param path the string to be parsed and appended to this name
    /// @return A new name with the path appended as one or more name parts.

    @NotNull
    Name name (
      @NotNull String path
    );


    /// Returns a new name that has this name as a direct prefix and a path of the enum name.
    ///
    /// @param path the enum to be appended to this name
    /// @return A new name with the enum name appended a name part.
    /// @throws NullPointerException if the path parameter is null

    @NotNull
    Name name (
      @NotNull Enum < ? > path
    );


    /// Returns a new extension of this name from iterating over a specified [Iterable] of [String] values.
    ///
    /// @param parts the [Iterable] to be iterated over
    /// @return The name following concatenation of name parts
    /// @throws NullPointerException if the [Iterable] is `null` or one of the values returned is `null`

    @NotNull
    Name name (
      @NotNull Iterable < String > parts
    );


    /// Returns a new extension of this name from iterating over the specified [Iterable] and applying a transformation function.
    ///
    /// @param <T>    the type of each value iterated over
    /// @param parts  the [Iterable] to be iterated over
    /// @param mapper the function to be used to map the iterable value type to a String type
    /// @return The name following concatenation of name parts
    /// @throws NullPointerException if the [Iterable] is `null` or one of the values returned is `null`

    @NotNull
    < T > Name name (
      @NotNull Iterable < ? extends T > parts,
      @NotNull Function < T, String > mapper
    );


    /// Returns a new extension of this name from iterating over the specified [Iterator] of [String] values.
    ///
    /// @param parts the [Iterator] to be iterated over
    /// @return The name following concatenation of name parts
    /// @throws NullPointerException if the [Iterable] is `null` or one of the values returned is `null`

    @NotNull
    Name name (
      @NotNull Iterator < String > parts
    );


    /// Returns a new extension of this name from iterating over the specified [Iterator] and applying a transformation function.
    ///
    /// @param <T>    the type of each value iterated over
    /// @param parts  the [Iterator] to be iterated over
    /// @param mapper the function to be used to map the iterator value type
    /// @return The name following concatenation of name parts
    /// @throws NullPointerException if the [Iterator] is `null` or one of the values returned is `null`
    /// @see #name(Iterable, Function)

    @NotNull
    < T > Name name (
      @NotNull Iterator < ? extends T > parts,
      @NotNull Function < T, String > mapper
    );


    /// Creates a `Name` from a [Class].
    ///
    /// @param klass the [Class] to be mapped to a `Name`
    /// @return A `Name` where `name.toString().equals(cls.getName())`
    /// @throws NullPointerException if the [Class] typed parameter is `null`

    @NotNull
    Name name (
      @NotNull Class < ? > klass
    );


    /// Creates a `Name` from a [Member].
    ///
    /// @param member the [Member] to be mapped to a `Name`
    /// @return A `Name` mapped to the [Member]
    /// @throws NullPointerException if the [Member] typed parameter is `null`

    @NotNull
    Name name (
      @NotNull Member member
    );


    /// Returns a `CharSequence` representation of this name and its enclosing names.
    ///
    /// @param mapper the function used to convert the [#value()] to a character sequence
    /// @return A non-`null` `CharSequence` representation.

    @NotNull
    CharSequence path (
      @NotNull Function < ? super String, ? extends CharSequence > mapper
    );


    /// The value for this name part.
    ///
    /// @return A non `null` string value.

    @NotNull
    String value ();

  }

  /// Indicates a parameter should never be passed a null argument value

  @Documented
  @Retention ( SOURCE )
  @Target ( {PARAMETER, METHOD} )
  @interface NotNull {
  }

  /// An activity without a return value.
  ///
  /// @param <T> The throwable class type

  @Utility
  @Extension
  @FunctionalInterface
  interface Op < T extends Throwable > {

    /// Use this function to force typing of an overloaded method handle at compile-time.
    ///
    /// @param op  the operation to be cast
    /// @param <T> the throwable class type thrown by the operation
    /// @return A casting of the specified operation
    /// @throws NullPointerException if `op` param is `null`

    static < T extends Throwable > Op < T > of (
      @NotNull final Op < T > op
    ) {

      requireNonNull ( op );

      return op;

    }


    /// Converts a [Fn] into an Op.
    ///
    /// @param fn  the [Fn] to be transformed
    /// @param <R> the return type of the [#eval()]
    /// @param <T> the throwable class type thrown by the operation
    /// @return An operation that wraps the function
    /// @throws NullPointerException if `fn` param is `null`

    @NotNull
    static < R, T extends Throwable > Op < T > of (
      @NotNull final Fn < R, ? extends T > fn
    ) {

      requireNonNull ( fn );

      return fn::eval;

    }


    /// Invokes the underlying operation.
    ///
    /// @throws T The derived throwable type thrown

    void exec () throws T;

  }

  /// Represents a configurable processing pipeline for data transformation.
  /// @see Assembly
  /// @see Sequencer
  /// @see Channel#pipe(Sequencer)
  /// @see Sift
  @Provided
  interface Path < E >
    extends Assembly {

    /// Returns a new path that extends the current pipe with a differencing pipeline operation

    @NotNull
    Path < E > diff ();


    /// Returns a new path that extends the current pipeline with a differencing operation.
    ///
    /// @param initial the initial value used for differencing
    /// @throws NullPointerException if the initial value is `null`

    @NotNull
    Path < E > diff (
      @NotNull E initial
    );


    /// Returns a new path that forwards emissions to the specified pipe.
    ///
    /// @param pipe the pipe to forward emissions to
    /// @return A new path with the forwarding operation added
    /// @throws NullPointerException if the pipe is `null`

    @NotNull
    Path < E > forward (
      @NotNull Pipe < E > pipe
    );


    /// Returns a new path that extends the current pipeline with a guard operation.
    ///
    /// @param predicate the initial value used for guarding
    /// @throws NullPointerException if the predicate is `null`

    @NotNull
    Path < E > guard (
      @NotNull Predicate < ? super E > predicate
    );


    /// Returns a new path that extends the current pipeline with a guard operation.
    ///
    /// @param initial   the initial value to compare against
    /// @param predicate the predicate used for guarding
    /// @throws NullPointerException if the predicate is `null`

    @NotNull
    Path < E > guard (
      E initial,
      @NotNull BiPredicate < ? super E, ? super E > predicate
    );


    /// Returns a new path that limits the throughput of the current pipeline to a maximum number of emitted values
    ///
    /// @param limit the initial maximum number of emitted values
    /// @throws NullPointerException if the predicate is `null`
    @NotNull
    Path < E > limit (
      long limit
    );


    /// Returns a new path that allows inspection of emissions without modifying them.
    ///
    /// @param consumer the consumer that will be called for each emission passing through
    /// @return A new path with the peek operation added
    /// @throws NullPointerException if the consumer is `null`

    @NotNull
    Path < E > peek (
      @NotNull Consumer < E > consumer
    );


    /// Returns a new path that extends the current pipeline with a reduction operation.
    ///
    /// @param initial  the initial value used for reduction
    /// @param operator the operation applied for each reduction

    @NotNull
    Path < E > reduce (
      E initial,
      @NotNull BinaryOperator < E > operator
    );


    /// Returns a new path that extends the current pipeline with a replacement operation
    ///
    /// @param transformer the operation applied for each possible replacement

    @NotNull
    Path < E > replace (
      @NotNull UnaryOperator < E > transformer
    );


    /// Returns a new path that extends the current pipeline with a sampling operation
    ///
    /// @param sample the number of emittances between samples

    @NotNull
    Path < E > sample (
      int sample
    );


    /// Returns a new path that extends the current pipeline with a sampling operation
    ///
    /// @param sample the sampling rate that samples occur at

    @NotNull
    Path < E > sample (
      double sample
    );


    /// Returns a new path that extends the current pipeline with a sampling operation
    ///
    /// @param comparator the comparator used by a sift subassembly line
    /// @param sequencer  the sequencer that configures the sift assembly
    /// @throws NullPointerException if the comparator or sequencer are `null`

    @NotNull
    Path < E > sift (
      @NotNull Comparator < E > comparator,
      @NotNull Sequencer < ? super Sift < E > > sequencer
    );

  }

  /// An abstraction that serves to pass typed values along a pipeline.
  ///
  /// @param <E> the class type of the emitted values
  /// @see Channel
  /// @see Path
  /// @see Subscriber
  /// @see Registrar

  @Abstract
  @Extension
  interface Pipe < E > {

    /// Returns an empty pipe that ignores all emissions.
    ///
    /// @param <E> the class type of the emitted values
    /// @return A pipe that does nothing when emissions are received

    @NotNull
    static < E > Pipe < E > empty () {

      return _ -> {
      };

    }


    /// A method for passing a data value along a pipeline.
    ///
    /// @param emission the value to be emitted

    void emit (
      @NotNull E emission
    );


  }

  /// Manages instances of a pooled type by name, creating them on demand.
  ///
  /// @param <T> The instance type
  /// @see Container
  /// @see Cortex#pool(Object)
  /// @see Name
  /// @see Subject
  @Abstract
  interface Pool < T > {

    /// Returns an instance of the pooled type for a given substrate.
    ///
    /// @param substrate The substrate used to determine the name lookup
    /// @return A newly created instance or a previously pooled instance
    /// @throws NullPointerException if the substrate is null

    @NotNull
    default T get (
      @NotNull final Substrate substrate
    ) {

      requireNonNull ( substrate );

      return get ( substrate.subject () );

    }


    /// Returns an instance of the pooled type for a given subject.
    ///
    /// @param subject the subject used to determine the name lookup
    /// @return A newly created instance or a previously pooled instance
    /// @throws NullPointerException if the subject is null

    @NotNull
    default T get (
      @NotNull final Subject subject
    ) {

      requireNonNull ( subject );

      return get ( subject.name () );

    }


    /// Returns an instance of the pooled type for a given name.
    ///
    /// @param name The name of the instance
    /// @return A newly created instance or a previously pooled instance
    /// @throws NullPointerException if the name is null

    @NotNull
    T get (
      @NotNull Name name
    );

  }

  /// Indicates a type exclusively provided by the runtime.

  @Documented
  @Retention ( SOURCE )
  @Target ( TYPE )
  @interface Provided {
  }

  /// An interface used to coordinate the processing of queued events.
  /// @see Circuit#queue()
  /// @see Script

  @Provided
  interface Queue {

    /// Suspends the current thread of execution until the queue is empty.

    void await ();


    /// Posts a [Script] to the queue.
    ///
    /// @param script the [Script] to be posted

    void post (
      @NotNull Script script
    );


    /// Posts a named Script to the queue.
    ///
    /// @param name   the name of the Script
    /// @param script the Script to be posted

    void post (
      @NotNull Name name,
      @NotNull Script script
    );

  }

  /// Links a [Subject] to a [Pipe]
  /// @see Subject
  /// @see Pipe
  /// @see Subscriber

  @Temporal
  @Provided
  interface Registrar < E > {

    /// Registers a [Pipe] with a `Registrar` associated with a [Source].
    ///
    /// @param pipe The [Pipe] to be associated with a [Subject]

    void register (
      @NotNull Pipe < E > pipe
    );

  }

  /// An interface that serves to explicitly dispose of resources.
  /// @see Component
  /// @see Sink
  /// @see Subscription
  /// @see Scope

  @Abstract
  sealed interface Resource
    permits Component,
            Sink,
            Subscription {

    /// A method that is called to indicate that no more usage will be made
    /// of the instance and that underlying resources held can be released.

    @Idempotent
    default void close () {
    }

  }

  /// Represents a resource management scope.
  /// @see Resource
  /// @see Closure
  /// @see Cortex#scope()
  /// @see AutoCloseable

  @Utility
  @Provided
  @Temporal
  interface Scope
    extends Substrate,
            Extent < Scope >,
            AutoCloseable {

    @Idempotent
    @Override
    void close ();


    /// Creates a closure for the specified resource within this scope.
    ///
    /// @param <R>      the type of resource
    /// @param resource the resource to be managed within the closure
    /// @return A closure that manages the specified resource
    /// @throws NullPointerException if the resource is `null`

    @NotNull
    < R extends Resource > Closure < R > closure (
      @NotNull R resource
    );


    /// Registers a resource with this scope for lifecycle management.
    ///
    /// @param <R>      the type of resource
    /// @param resource the resource to be registered with this scope
    /// @return The registered resource (same as input)
    /// @throws NullPointerException if the resource is `null`

    @NotNull
    < R extends Resource > R register (
      @NotNull R resource
    );


    /// Creates a new named child scope within this scope.
    ///
    /// @param name the name for the new child scope
    /// @return A new scope that is a child of this scope
    /// @throws NullPointerException if the name is `null`

    @NotNull
    Scope scope ( @NotNull Name name );


    /// Creates a new anonymous child scope within this scope.
    ///
    /// @return A new scope that is a child of this scope

    @NotNull
    Scope scope ();


    /// Returns the subject associated with this scope.
    ///
    /// @return The subject that identifies this scope

    @NotNull
    Subject subject ();

  }

  /// An executable unit of work that can be scheduled for execution in a circuit's queue.
  ///
  /// Scripts provide a way to execute code within the context of a circuit's
  /// processing pipeline, ensuring proper coordination with other operations.
  /// @see Queue
  /// @see Current
  /// @see Circuit#queue()

  @Extension
  interface Script {

    /// Executes this script within the context of the provided current.
    ///
    /// @param current the execution context for this script
    /// @throws NullPointerException if the current is `null`

    void exec (
      @NotNull Current current
    );

  }

  /// Responsible for configuring and sequencing assembly components in a pipeline.
  ///
  /// Sequencers define how data flows through a pipeline by applying configurations
  /// to assembly components like paths and sifts.
  ///
  /// @param <A> the type of assembly this sequencer configures
  /// @see Assembly
  /// @see Path
  /// @see Sift
  /// @see Channel#pipe(Sequencer)

  @Extension
  interface Sequencer < A extends Assembly > {

    /// Applies configuration to the provided assembly.
    ///
    /// @param assembly the assembly to be configured
    /// @throws NullPointerException if the assembly is `null`

    void apply (
      @NotNull A assembly
    );

  }

  /// A filtering mechanism for values in a pipeline based on comparison criteria.
  ///
  /// Sift provides various methods to filter values based on thresholds, ranges,
  /// and relative positions in a sorted sequence.
  ///
  /// @param <E> the type of elements being filtered
  /// @see Path#sift(Comparator, Sequencer)
  /// @see Assembly
  /// @see Sequencer

  @Temporal
  @Provided
  interface Sift < E >
    extends Assembly {

    /// Creates a sift that only passes values above the specified lower bound.
    ///
    /// @param lower the lower bound (exclusive)
    /// @return A new sift with the above filter applied
    /// @throws NullPointerException if the lower bound is `null`

    @NotNull
    Sift < E > above (
      @NotNull E lower
    );


    /// Creates a sift that only passes values below the specified upper bound.
    ///
    /// @param upper the upper bound (exclusive)
    /// @return A new sift with the below filter applied
    /// @throws NullPointerException if the upper bound is `null`

    @NotNull
    Sift < E > below (
      @NotNull E upper
    );


    /// Creates a sift that only passes values that represent a new high value.
    ///
    /// @return A new sift that filters for high values

    @NotNull
    Sift < E > high ();


    /// Creates a sift that only passes values that represent a new low value.
    ///
    /// @return A new sift that filters for low values

    @NotNull
    Sift < E > low ();


    /// Creates a sift that only passes values up to the specified maximum.
    ///
    /// @param max the maximum value (inclusive)
    /// @return A new sift with the maximum filter applied
    /// @throws NullPointerException if the max value is `null`

    @NotNull
    Sift < E > max (
      @NotNull E max
    );


    /// Creates a sift that only passes values from the specified minimum.
    ///
    /// @param min the minimum value (inclusive)
    /// @return A new sift with the minimum filter applied
    /// @throws NullPointerException if the min value is `null`

    @NotNull
    Sift < E > min (
      @NotNull E min
    );


    /// Creates a sift that only passes values within the specified range.
    ///
    /// @param lower the lower bound (inclusive)
    /// @param upper the upper bound (inclusive)
    /// @return A new sift with the range filter applied
    /// @throws NullPointerException if either bound is `null`

    @NotNull
    Sift < E > range (
      @NotNull E lower,
      @NotNull E upper
    );

  }

  /// An in-memory buffer of captures.
  ///
  /// @param <E> the class type of the emitted value
  /// @see Capture
  /// @see Cortex#sink(Source)
  /// @see Resource

  @Provided
  non-sealed interface Sink < E >
    extends Substrate,
            Resource {

    /// Returns a stream representing the events that have accumulated since the
    /// sink was created or the last call to this method.
    ///
    /// @return A stream consisting of stored events.
    /// @see Capture

    @NotNull
    Stream < Capture < E > > drain ();

  }

  /// An opaque interface representing a variable (slot) within a state chain.
  ///
  /// @param <T> the class type of the value extracted from the target
  /// @see State
  /// @see Name
  /// @see Cortex#slot

  @Utility
  @Provided
  interface Slot < T > {

    /// Returns the name of this slot.
    ///
    /// @return The name that identifies this slot

    @NotNull
    Name name ();


    /// Returns the class type of this slot's value.
    ///
    /// @return The class type of the value stored in this slot

    @NotNull
    Class < T > type ();


    /// Returns the value stored in this slot.
    ///
    /// @return The value stored in this slot

    @NotNull
    T value ();

  }

  /// An interface for subscribing to source events.
  ///
  /// @param <E> the class type of emitted value
  /// @see Subscriber
  /// @see Subscription
  /// @see Context
  /// @see Component

  @Provided
  interface Source < E >
    extends Substrate {

    /// Subscribes a [Subscriber] to receive subject registrations from this source
    ///
    /// @param subscriber the subscriber to be subscribed
    /// @return The subscription used to control future delivery
    /// @throws NullPointerException if subscriber is `null`
    /// @see Subscriber
    /// @see Subscription

    @NotNull
    Subscription subscribe (
      @NotNull Subscriber < E > subscriber
    );

  }

  /// Represents an immutable collection of named slots containing typed values.
  /// @see Slot
  /// @see Subject#state()
  /// @see Cortex#state()
  @Provided
  interface State
    extends Iterable < Slot < ? > > {

    /// Returns a new [State][State] with duplicates removed.
    ///
    /// @return A new [State][State] with duplicates removed.
    @NotNull
    State compact ();


    /// Creates a new [State][State] with the specified name and value added as a [Slot][Slot]
    ///
    /// @param name  the name of the slot
    /// @param value the value of the slot
    /// @throws NullPointerException if name is `null`

    @NotNull
    State state (
      @NotNull Name name,
      int value
    );


    /// Creates a new [State][State] with the specified name and value added as a [Slot][Slot]
    ///
    /// @param name  the name of the slot
    /// @param value the value of the slot
    /// @throws NullPointerException if name is `null`

    @NotNull
    State state (
      @NotNull Name name,
      long value
    );


    /// Creates a new [State][State] with the specified name and value added as a [Slot][Slot]
    ///
    /// @param name  the name of the slot
    /// @param value the value of the slot
    /// @throws NullPointerException if name is `null`

    @NotNull
    State state (
      @NotNull Name name,
      float value
    );


    /// Creates a new [State][State] with the specified name and value added as a [Slot][Slot]
    ///
    /// @param name  the name of the slot
    /// @param value the value of the slot
    /// @throws NullPointerException if name is `null`

    @NotNull
    State state (
      @NotNull Name name,
      double value
    );


    /// Creates a new [State][State] with the specified name and value added as a [Slot][Slot]
    ///
    /// @param name  the name of the slot
    /// @param value the value of the slot
    /// @throws NullPointerException if name is `null`

    @NotNull
    State state (
      @NotNull Name name,
      boolean value
    );


    /// Creates a new [State][State] with the specified name and value added as a [Slot][Slot]
    ///
    /// @param name  the name of the slot
    /// @param value the value of the slot
    /// @throws NullPointerException if name or value is `null`

    @NotNull
    State state (
      @NotNull Name name,
      @NotNull String value
    );


    /// Creates a new [State][State] with the specified name and value added as a [Slot][Slot]
    ///
    /// @param name  the name of the slot
    /// @param value the value of the slot
    /// @throws NullPointerException if name or value is `null`

    @NotNull
    State state (
      @NotNull Name name,
      @NotNull Name value
    );


    /// Creates a new [State][State] with the specified name and value added as a [Slot][Slot]
    ///
    /// @param name  the name of the slot
    /// @param value the value of the slot
    /// @throws NullPointerException if name or value is `null`

    @NotNull
    State state (
      @NotNull Name name,
      @NotNull State value
    );


    /// Returns a [Stream][Stream] of all [Slots][Slot] in this state

    @NotNull
    Stream < Slot < ? > > stream ();


    /// Returns the value of a slot matching the specified slot
    /// or the value of the specified slot when not found.
    ///
    /// @param slot the slot to lookup and fallback onto for the source of a value
    /// @throws NullPointerException if slot is `null`

    < T > T value (
      @NotNull Slot < T > slot
    );


    /// Returns a [Stream][Stream] of all [Slots][Slot] in this state that match the specified slot.
    ///
    /// @param slot the slot to lookup
    /// @throws NullPointerException if slot is `null`

    @NotNull
    < T > Stream < T > values (
      @NotNull Slot < ? extends T > slot
    );

  }

  /// A [Subject][Subject] represents a referent that maintains an identity as well as [State][State].
  /// @see Id
  /// @see Name
  /// @see State
  /// @see Substrate

  @Identity
  @Provided
  interface Subject
    extends Extent < Subject > {

    /// Returns a unique identifier for this subject.
    ///
    /// @return A unique identifier for this subject
    /// @see Id

    @NotNull
    Id id ();


    /// The [Name] associated with this reference.
    ///
    /// @return a non-null name reference
    /// @see Name

    @NotNull
    Name name ();


    /// Returns a `CharSequence` representation of just this subject.
    ///
    /// @return A non-`null` `String` representation.

    @Override
    @NotNull
    default CharSequence part () {

      return "Subject[name=%s,type=%s,id=%s]"
        .formatted (
          name (),
          type (),
          id ()
        );

    }


    /// Returns the current state of this subject.
    /// @see State

    @NotNull
    State state ();


    /// Returns the string representation returned from [#path()].
    ///
    /// @return A non-`null` string representation.
    /// @see #path()

    @Override
    @NotNull
    String toString ();


    @NotNull
    Type type ();


    enum Type {
      CHANNEL,
      CIRCUIT,
      CLOCK,
      CONDUIT,
      CONTAINER,
      SOURCE,
      SCOPE,
      SCRIPT,
      SINK,
      SUBSCRIBER,
      SUBSCRIPTION
    }

  }

  /// Connects outlet pipes with emitting subjects within a source.
  ///
  /// @param <E> the class type of the emitted value
  /// @see Source
  /// @see Subject
  /// @see Registrar
  /// @see Pipe
  /// @see Cortex#subscriber(Name, BiConsumer)

  @Provided
  interface Subscriber < E >
    extends BiConsumer < Subject, Registrar < E > >,
            Substrate {

    /// @param subject   the [Subject] of the [Channel]
    /// @param registrar a registrar used for registering a pipe to capture sourced events
    /// @throws NullPointerException if subject or registrar are `null`

    @Override
    void accept (
      @NotNull Subject subject,
      @Temporal @NotNull Registrar < E > registrar
    );

  }


  /// An interface used for unregistering interest in receiving subscribed events.
  /// @see Source#subscribe(Subscriber)
  /// @see Resource
  /// @see Clock#consume(Name, Clock.Cycle, Pipe)

  @Provided
  non-sealed interface Subscription
    extends Substrate,
            Resource {

  }


  /// Base interface for all substrate components that have an associated subject.
  /// @see Subject
  @Abstract
  @Extension
  interface Substrate {

    /// Returns the subject identifying this substrate
    ///
    /// @return The subject associated with this substrate.
    /// @see Subject

    @NotNull
    Subject subject ();

  }

  @SuppressWarnings ( "unchecked" )
  interface Tap < T extends Tap < T > > {

    @Fluent
    default T tap (
      final Consumer < ? super T > consumer
    ) {

      requireNonNull ( consumer );

      consumer.accept ( (T) this );

      //noinspection unchecked
      return (T) this;

    }
  }

  /// Indicates a type that is transient and whose reference should not be retained.

  @Documented
  @Retention ( SOURCE )
  @Target ( {PARAMETER, TYPE} )
  @interface Temporal {
  }

  /// Indicates a type that serves a utility purpose.

  @Documented
  @Retention ( SOURCE )
  @Target ( TYPE )
  @interface Utility {
  }

}
