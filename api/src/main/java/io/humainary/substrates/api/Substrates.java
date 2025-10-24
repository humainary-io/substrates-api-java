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
/// @author William David Louth
/// @since 1.0

public final class Substrates {

  /// The system property key used to specify the Cortex provider class.
  /// The specified class must have a static method with signature: {@code public static Cortex cortex()}
  private static final String PROVIDER_PROPERTY = "io.humainary.substrates.spi.provider";

  /// The singleton Cortex instance, lazily initialized via the provider class.
  private static volatile Cortex cortex;

  private Substrates () { }


  /// Indicates a type that serves primarily as an abstraction for other types.
  @Documented
  @Retention ( SOURCE )
  @Target ( TYPE )
  public @interface Abstract {
  }


  /// Indicates a type that serves a role in the assembly of a pipeline.
  @Abstract
  public sealed interface Assembly
    permits Flow,
            Sift {
  }


  /// A capture of an emitted value with its associated typed subject
  ///
  /// @param <E> the class type of emitted value
  /// @param <S> the substrate type that emitted the value
  /// @see Sink#drain()

  @Provided
  public interface Capture < E, S extends Substrate < S > > {

    /// Returns the emitted value.
    ///
    /// @return The emitted value

    @NotNull
    E emission ();


    /// Returns the typed subject that emitted the value.
    ///
    /// @return The typed subject that emitted the value
    /// @see Subject

    @NotNull
    Subject < S > subject ();

  }

  /// A combined pipe and container for advanced data flow patterns.
  /// This interface is experimental and may undergo significant changes in future releases.
  ///
  /// @param <I> the class type of input values
  /// @param <E> the class type of emitted values
  /// @see Pipe
  /// @see Container
  /// @see Circuit#cell(Composer, Consumer, Pipe)

  @Experimental
  @Provided
  non-sealed public interface Cell < I, E >
    extends Pipe < I >,
            Container < Cell < I, E >, E, Cell < I, E > >,
            Extent < Cell < I, E >, Cell < I, E > > {

  }


  /// A (subject) named pipe managed by a conduit.
  ///
  /// @param <E> the class type of emitted value
  /// @see Conduit
  /// @see Inlet
  /// @see Circuit#conduit(Composer)

  @Provided
  non-sealed public interface Channel < E >
    extends Substrate < Channel < E > >,
            Inlet < E > {


    /// Returns a pipe that will use this channel to emit values.
    ///
    /// @param configurer A consumer responsible for configuring flow in the conduit.
    /// @return A pipe instance that will use this channel to emit values
    /// @throws NullPointerException if the specified configurer is `null`
    /// @see Pipe
    /// @see Flow

    @NotNull
    Pipe < E > pipe (
      @NotNull Consumer < ? super Flow < E > > configurer
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

  @Provided
  non-sealed public interface Circuit
    extends Component < State, Circuit >,
            Resource,
            Tap < Circuit > {

    /// Suspends the current thread of execution until the circuit's queue is empty.
    ///
    /// This method blocks the calling thread until all currently queued events
    /// in the circuit have been processed. It cannot be called from within the
    /// circuit's own thread.
    ///
    /// After the circuit has been closed, this method returns immediately without
    /// blocking, providing a fast-path for shutdown scenarios.
    ///
    /// @throws IllegalStateException if called from within the circuit's thread

    void await ();


    @Experimental
    @NotNull
    < I, E > Cell < I, E > cell (
      @NotNull Composer < Pipe < I >, E > composer,
      @NotNull Consumer < Flow < E > > configurer,
      @NotNull Pipe < E > pipe
    );


    @Experimental
    @NotNull
    < I, E > Cell < I, E > cell (
      @NotNull Composer < Pipe < I >, E > composer,
      @NotNull Pipe < E > pipe
    );


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
    /// @param name       the name given to the conduit's subject
    /// @param composer   the composer that composes percepts around a channel.
    /// @param configurer the consumer responsible for configuring the flow in the conduit.
    /// @param <P>        The class type of the percept.
    /// @param <E>        The class type of emitted value.
    /// @return A conduit that will use this circuit to process and deliver values emitted.
    /// @throws NullPointerException if the specified name or composer is `null`

    @NotNull
    < P, E > Conduit < P, E > conduit (
      @NotNull Name name,
      @NotNull Composer < ? extends P, E > composer,
      @NotNull Consumer < Flow < E > > configurer
    );


    /// Returns a conduit that will use this circuit to process and transfer values emitted.
    ///
    /// @param composer a composer that composes percepts around a channel.
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
    /// @param composer the composer that composes percepts around a channel.
    /// @param <P>      The class type of the percept.
    /// @param <E>      The class type of emitted value.
    /// @return A conduit that will use this circuit to process and deliver values emitted.
    /// @throws NullPointerException if the specified name or composer is `null`

    @NotNull
    < P, E > Conduit < P, E > conduit (
      @NotNull Name name,
      @NotNull Composer < ? extends P, E > composer
    );

  }

  /// A component that emits clock ticks.
  ///
  /// @see Circuit#clock()
  /// @see Instant
  /// @see Clock.Cycle

  @Provided
  non-sealed public interface Clock
    extends Component < Instant, Clock >,
            Resource {

    /// A utility method that subscribes a pipe to events of a particular cycle
    ///
    /// @param name  the name to be used for the subscription
    /// @param cycle the cycle value to be subscribed to
    /// @param pipe  the pipe that will consume ticks events
    /// @return A subscription that can be used to cancel further delivery of tick events
    /// @throws NullPointerException if the specified name, cycle, or pipe are `null`
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
    /// The enum value is used to define the [Name] for a clock sensor [Subject].
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
      /// @return The number of time units

      public long units () {

        return units;

      }

    }

  }

  /// A utility interface for scoping the work performed against a resource.

  @Utility
  @Temporal
  public interface Closure < R extends Resource > {

    /// Calls a consumer, with an acquired resource, within an automatic resource management (ARM) scope.
    ///
    /// @param consumer the consumer to be executed within an automatic resource (ARM) management scope.

    void consume (
      @NotNull Consumer < ? super R > consumer
    );

  }


  /// An abstraction that represents a managed event-sourcing component.
  ///
  /// A Component is the base type for all event-sourcing objects in the
  /// framework, inheriting its behavior from the {@link Context} interface.
  ///
  /// @param <E> the class type of emitted value
  /// @param <S> the self-referential component type

  @Abstract
  public sealed interface Component < E, S extends Component < E, S > >
    extends Context < E, S >
    permits Circuit,
            Clock,
            Container {

  }

  /// A composer that composes percepts around a channel.
  ///
  /// @param <P> the class type of the percept
  /// @param <E> the class type of emitted value
  /// @see Channel
  /// @see Pipe
  /// @see Circuit#conduit(Composer)

  @Abstract
  @Extension
  public interface Composer < P, E > {


    /// Returns the identity composer.
    ///
    /// @param <E> the class type of emitted value
    /// @return The identity composer

    @NotNull
    static < E > Composer < Channel < E >, E > channel () {

      return input -> input;

    }


    /// Returns a composer that returns a typed channel
    ///
    /// @param <E>  the class type of emitted value
    /// @param type the class type of the emitted value
    /// @return A composer that returns a typed channel
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
    /// @param <E>        the class type of emitted value
    /// @param configurer a consumer responsible for configuring a flow in the conduit.
    /// @return A composer that returns a channel's pipe
    /// @throws NullPointerException if the specified configurer is `null`

    @NotNull
    static < E > Composer < Pipe < E >, E > pipe (
      @NotNull final Consumer < ? super Flow < E > > configurer
    ) {

      requireNonNull ( configurer );

      return channel ->
        channel.pipe (
          configurer
        );

    }


    /// Returns a composer that returns a channel's pipe
    ///
    /// @param <E>  the class type of emitted value
    /// @param type the class type of the emitted value
    /// @return A composer that returns a typed channel's pipe
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
    /// @return A composer that returns a channel's pipe

    @NotNull
    static < E > Composer < Pipe < E >, E > pipe () {

      return Inlet::pipe;

    }


    /// Composes a channel into a percept.
    ///
    /// @param channel the channel to be composed
    /// @return The composition from the channel
    /// @throws NullPointerException if the specified channel is `null`

    @NotNull
    P compose (
      @NotNull Channel < E > channel
    );


    /// Returns a new composer that applies a function the results of this composer
    ///
    /// @param <R> the class type of resulting percept
    /// @return A composer that applies a function the results of this composer
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
  non-sealed public interface Conduit < P, E >
    extends Container < P, E, Conduit < P, E > >,
            Tap < Conduit < P, E > > {

  }

  /// Creates and manages an instance pool and notifies of events associated with such instances
  ///
  /// @param <P> the class type of the pooled object
  /// @param <E> the class type of component (source) emittance
  /// @param <S> the self-referential container type
  /// @see Pool
  /// @see Component

  @Abstract
  public sealed interface Container < P, E, S extends Container < P, E, S > >
    extends Pool < P >,
            Component < E, S >
    permits Conduit,
            Cell {

  }

  /// A type that is both a source of events and a substrate with an identity.
  ///
  /// This interface combines the {@link Source} and {@link Substrate} interfaces,
  /// serving as the fundamental building block for components.
  ///
  /// @param <E> the class type of emitted value
  /// @param <S> the self-referential context type
  /// @see Source
  /// @see Substrate
  /// @see Component

  @Abstract
  sealed public interface Context < E, S extends Context < E, S > >
    extends Source < E >,
            Substrate < S >
    permits Component {

  }


  /// The main entry point into the underlying substrates runtime.
  ///
  /// The Cortex serves as the primary factory for creating runtime components including
  /// circuits, names, scopes, sinks, slots, states, and subscribers. Each component type
  /// can be created with or without an explicit name, and the Cortex manages the interning
  /// of names to ensure identity-based equality for equivalent hierarchies.
  ///
  /// @see Circuit
  /// @see Name
  /// @see Pool
  /// @see Scope
  /// @see Sink
  /// @see Slot
  /// @see State
  /// @see Subscriber

  @Provided
  public interface Cortex {

    /// Returns a newly created circuit instance with a generated name.
    ///
    /// Each circuit maintains its own event processing queue and guarantees ordering
    /// of emissions. The returned circuit must be closed when no longer needed to
    /// release resources.
    ///
    /// @return A non-null circuit instance
    /// @see Circuit#close()
    /// @see Circuit#await()

    @NotNull
    Circuit circuit ();


    /// Returns a newly created circuit instance with the specified name.
    ///
    /// Each circuit maintains its own event processing queue and guarantees ordering
    /// of emissions. The returned circuit must be closed when no longer needed to
    /// release resources.
    ///
    /// @param name the name assigned to the circuit's subject
    /// @return A newly created circuit instance
    /// @throws NullPointerException if the specified name is `null`
    /// @see Circuit#close()
    /// @see Circuit#await()

    @NotNull
    Circuit circuit (
      @NotNull Name name
    );


    /// Parses the supplied path into an interned name.
    /// The path uses `.` as the separator; empty segments are rejected and cached segments are reused.
    /// Paths must not begin or end with `.` nor contain consecutive separators (for example: `.foo`, `foo.`, `foo..bar`).
    ///
    /// @param path the string to be parsed into one or more name parts
    /// @return A name representing the parsed hierarchy
    /// @throws NullPointerException     if the path parameter is `null`
    /// @throws IllegalArgumentException if the path is empty or contains empty segments

    @NotNull
    Name name (
      @NotNull String path
    );


    /// Returns an interned name that appends the enum's [Enum#name()] value to the constant's declaring type.
    ///
    /// @param path the enum to be appended to this name
    /// @return A name representing the enum constant hierarchy joined to its declaring type
    /// @throws NullPointerException if the path parameter is `null`

    @NotNull
    Name name (
      @NotNull Enum < ? > path
    );


    /// Returns an interned name from iterating over string values.
    /// Each value may contain dot-separated segments; at least one value must be provided.
    ///
    /// @param it the iterable to be iterated over
    /// @return The name following concatenation of name parts
    /// @throws NullPointerException     if the iterable is `null` or one of the values returned is `null`
    /// @throws IllegalArgumentException if the iterable yields no values or an invalid path segment

    @NotNull
    Name name (
      @NotNull Iterable < String > it
    );


    /// Returns an interned name from iterating over values mapped to strings.
    /// Each mapped value may contain dot-separated segments; at least one value must be provided.
    ///
    /// @param <T>    the type of each value iterated over
    /// @param it     the iterable to be iterated over
    /// @param mapper the function to be used to map the iterable value type to a string
    /// @return The name following concatenation of name parts
    /// @throws NullPointerException     if the iterable, mapper, or one of the mapped values is `null`
    /// @throws IllegalArgumentException if the iterable yields no values or an invalid path segment

    @NotNull
    < T > Name name (
      @NotNull Iterable < ? extends T > it,
      @NotNull Function < T, String > mapper
    );


    /// Returns an interned name from iterating over string values.
    /// Each value may contain dot-separated segments; at least one value must be provided.
    ///
    /// @param it the [Iterator] to be iterated over
    /// @return The name following concatenation of name parts
    /// @throws NullPointerException     if the iterator is `null` or one of the values returned is `null`
    /// @throws IllegalArgumentException if the iterator yields no values or an invalid path segment
    /// @see #name(Iterable)

    @NotNull
    Name name (
      @NotNull Iterator < String > it
    );


    /// Returns an interned name from iterating over values mapped to strings.
    /// Each mapped value may contain dot-separated segments; at least one value must be provided.
    ///
    /// @param <T>    the type of each value iterated over
    /// @param it     the iterator to be iterated over
    /// @param mapper the function to be used to map the iterator value type to a string
    /// @return The name following concatenation of name parts
    /// @throws NullPointerException     if the iterator, mapper, or one of the mapped values is `null`
    /// @throws IllegalArgumentException if the iterator yields no values or an invalid path segment
    /// @see #name(Iterable, Function)

    @NotNull
    < T > Name name (
      @NotNull Iterator < ? extends T > it,
      @NotNull Function < T, String > mapper
    );


    /// Creates an interned name from a class.
    /// The package and enclosing class simple names are joined with dots (matching [Class#getCanonicalName()]
    /// when available); local or anonymous classes fall back to the runtime name.
    ///
    /// @param type the class to be mapped to a name
    /// @return A name whose string representation matches `type.getCanonicalName()` when available, otherwise `type.getName()`
    /// @throws NullPointerException if the type param is `null`

    @NotNull
    Name name (
      @NotNull Class < ? > type
    );


    /// Creates an interned name from a member.
    /// The declaring class hierarchy is extended with the member name.
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
    /// The returned pool will return the same singleton instance regardless of
    /// the name, subject, or substrate used to query it. This is useful for
    /// providing a shared resource across all components.
    ///
    /// @param singleton the singleton instance to be returned by the pool
    /// @param <T>       the class type of the singleton
    /// @return A pool that always returns the same singleton instance
    /// @throws NullPointerException if the singleton param is `null`
    /// @see Pool#get(Name)

    @NotNull
    < T > Pool < T > pool (
      @NotNull final T singleton
    );


    /// Returns a new scope instance with the specified name for managing resources.
    ///
    /// Scopes provide hierarchical resource lifecycle management. When a scope is closed,
    /// all resources registered with it (via {@link Scope#register(Resource)}) are automatically
    /// closed. Child scopes can be created from parent scopes, forming a tree structure.
    ///
    /// @param name the name assigned to the scope's subject
    /// @return A scope that calls close on registered resources when it is closed
    /// @throws NullPointerException if the name param is `null`
    /// @see Scope#register(Resource)
    /// @see Scope#close()

    @NotNull
    Scope scope (
      @NotNull Name name
    );


    /// Returns a new scope instance with a generated name for managing resources.
    ///
    /// Scopes provide hierarchical resource lifecycle management. When a scope is closed,
    /// all resources registered with it (via {@link Scope#register(Resource)}) are automatically
    /// closed. Child scopes can be created from parent scopes, forming a tree structure.
    ///
    /// @return A scope that calls close on registered resources when it is closed
    /// @see Scope#register(Resource)
    /// @see Scope#close()

    @NotNull
    Scope scope ();


    /// Creates a Sink instance for the given context.
    ///
    /// @param context the context to create a sink for
    /// @return A new Sink instance associated with the provided context

    @NotNull
    < E, S extends Context < E, S > > Sink < E > sink (
      @NotNull final Context < E, S > context
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


    /// Creates a slot with a class type `double`.
    ///
    /// @param name  the name for the slot
    /// @param value the value for the slot
    /// @return A slot with a class type of `double`.
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


    /// Creates a slot with a class type of [Name] from an enum.
    /// The slot's name is derived from the enum's declaring class,
    /// and the slot's value is a [Name] created from the enum constant's name.
    ///
    /// @param value the enum to create a slot from
    /// @return A slot with a class type of [Name] containing the enum constant's name.
    /// @throws NullPointerException if value is `null`

    @NotNull
    Slot < Name > slot (
      @NotNull Enum < ? > value
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


    /// Creates a subscriber with the specified name and subscribing behavior.
    ///
    /// When subscribed to a source, the subscriber's behavior is invoked each time a new
    /// channel or emitting subject is created within that source. The behavior receives
    /// the subject and a registrar, allowing it to dynamically register pipes to receive
    /// emissions from that specific subject.
    ///
    /// @param name       the name to be used by the subject assigned to the subscriber
    /// @param subscriber the subscribing behavior to be applied when a new subject emits as an emission
    /// @return A new non-null subscriber instance configured with the provided subscribing behavior
    /// @throws NullPointerException if the name or subscriber params are `null`
    /// @see Source#subscribe(Subscriber)
    /// @see Registrar#register(Pipe)

    @NotNull
    < E > Subscriber < E > subscriber (
      @NotNull final Name name,
      @NotNull final BiConsumer < Subject < Channel < E > >, Registrar < E > > subscriber
    );


    /// Creates a subscriber with the specified name and pool of pipes.
    ///
    /// When subscribed to a source, the subscriber will automatically obtain a pipe from
    /// the pool for each new channel or emitting subject, and register that pipe to receive
    /// emissions. This is convenient when you want all subjects to use the same pipe instance
    /// (singleton pool) or follow a specific pooling strategy.
    ///
    /// @param name the name to be used by the subject assigned to the subscriber
    /// @param pool the pool of pipes to be used when a subscription is requested
    /// @param <E>  the class type of emitted value
    /// @return A new non-null subscriber instance configured with the provided pool
    /// @throws NullPointerException if the name or pool params are `null`
    /// @see Source#subscribe(Subscriber)
    /// @see Pool#get(Name)

    @NotNull
    < E > Subscriber < E > subscriber (
      @NotNull final Name name,
      @NotNull Pool < ? extends Pipe < E > > pool
    );

  }

  /// Indicates an API that is experimental and subject to change in future releases.
  ///
  /// Experimental APIs are not considered stable and may be modified or removed
  /// without following normal deprecation policies. Use with caution in production code.
  ///
  /// @since 1.0

  @Documented
  @Retention ( SOURCE )
  @Target ( {TYPE, METHOD, CONSTRUCTOR, FIELD} )
  public @interface Experimental {
  }

  /// Indicates a type used by callers or instrumentation kits to extend capabilities.

  @Documented
  @Retention ( SOURCE )
  @Target ( TYPE )
  public @interface Extension {
  }

  /// An abstraction of a hierarchically nested structure of enclosed whole-parts.
  ///
  /// @param <S> the concrete extent type (usually `this`) returned from [#extent()]
  /// @param <P> the enclosing extent type iterated during traversal and comparison

  @Abstract
  @Extension
  public interface Extent < S extends Extent < S, P >, P extends Extent < ?, P > >
    extends Iterable < P >,
            Comparable < P > {


    @Override
    default int compareTo (
      @NotNull final P other
    ) {

      requireNonNull ( other );

      return CharSequence.compare (
        path ( '\u0001' ),
        other.path ( '\u0001' )
      );

    }


    /// Returns the depth of this extent within all enclosures.
    ///
    /// @return The depth of this extent within all enclosures.

    default int depth () {

      return
        fold (
          _ -> 1,
          ( depth, _ ) -> depth + 1
        );

    }


    /// Returns the (parent/prefix) extent wrapped in an optional that encloses this extent.
    ///
    /// @return An optional holding a reference to the enclosing extent or [Optional#empty()].
    @NotNull
    default Optional < P > enclosure () {
      return Optional.empty ();
    }


    /// Applies the given consumer to the enclosing extent if it exists.
    ///
    /// @param consumer the consumer to be applied to the enclosing extent
    /// @throws NullPointerException if the consumer is `null`

    default void enclosure (
      @NotNull final Consumer < ? super P > consumer
    ) {

      enclosure ()
        .ifPresent ( consumer );

    }


    /// Returns the extent instance referenced by `this`.
    ///
    /// @return A reference to this extent instance

    @NotNull
    @SuppressWarnings ( "unchecked" )
    default S extent () {
      return (S) this;
    }


    /// Returns the outermost (extreme) extent.
    ///
    /// @return The outermost extent.

    @NotNull
    @SuppressWarnings ( "unchecked" )
    default P extremity () {

      P prev;

      var current
        = (P) extent ();

      do {

        prev = current;

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
      @NotNull final Function < ? super P, ? extends R > initializer,
      @NotNull final BiFunction < ? super R, ? super P, R > accumulator
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


    /// Produces an accumulated value moving from the left (root) to the right (this) in the extent.
    ///
    /// @param <R>         the return type of the accumulated value
    /// @param initializer the function called to seed the accumulator
    /// @param accumulator the function used to add a value to the accumulator
    /// @return The accumulated result of performing the seed once and the accumulator.
    /// @throws NullPointerException if either initializer or accumulator params are `null`

    @SuppressWarnings ( "unchecked" )
    default < R > R foldTo (
      @NotNull final Function < ? super P, ? extends R > initializer,
      @NotNull final BiFunction < ? super R, ? super P, R > accumulator
    ) {

      requireNonNull ( initializer );
      requireNonNull ( accumulator );

      return enclosure ()
        .map (
          ( P enclosure ) ->
            accumulator.apply (
              enclosure.foldTo (
                initializer,
                accumulator
              ),
              (P) extent ()
            )
        ).orElse (
          initializer.apply (
            (P) extent ()
          )
        );

    }


    /// Returns an iterator over the extents moving from the right (`this`) to the left ([#extremity()]).
    ///
    /// @return An iterator for traversing over the nested extents, starting with `this` extent instance.

    @NotNull
    default Iterator < P > iterator () {

      //noinspection ReturnOfInnerClass,unchecked
      return new Iterator <> () {

        private P extent
          = (P) extent ();

        @Override
        public boolean hasNext () {

          return extent != null;

        }

        @Override
        public P next () {

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
        path ( '/' );

    }


    /// Returns a `CharSequence` representation of this extent and its enclosures.
    ///
    /// @return A non-`null` `String` representation.

    @NotNull
    default CharSequence path (
      final char separator
    ) {

      return
        path (
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

      return
        path (
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
      @NotNull final Function < ? super P, ? extends CharSequence > mapper,
      final char separator
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
      @NotNull final Function < ? super P, ? extends CharSequence > mapper,
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
    default Stream < P > stream () {

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


    /// Returns true if this `Extent` is directly or indirectly enclosed within the supplied extent.
    ///
    /// @param enclosure the extent to check against, regardless of its generic specialization
    /// @return `true` if the supplied extent encloses this extent, directly or indirectly
    /// @throws NullPointerException if `enclosure` param is `null`

    default boolean within (
      @NotNull final Extent < ?, ? > enclosure
    ) {

      requireNonNull ( enclosure );

      for (
        var current = enclosure ();
        current.isPresent ();
        current = current.get ().enclosure ()
      ) {
        if ( current.get () == enclosure ) {
          return true;
        }
      }

      return false;

    }

  }


  /// Represents a configurable processing pipeline for data transformation.
  ///
  /// @see Assembly
  /// @see Channel#pipe(Consumer)
  /// @see Sift

  @Provided
  non-sealed public interface Flow < E >
    extends Assembly,
            Tap < Flow < E > > {

    /// Returns a flow that extends the current pipeline with a differencing operation.
    /// The initial value is implicitly `null`.

    @NotNull
    @Fluent
    Flow < E > diff ();


    /// Returns a flow that extends the current pipeline with a differencing operation.
    ///
    /// @param initial the initial value used for differencing
    /// @throws NullPointerException if the initial value is `null`

    @NotNull
    @Fluent
    Flow < E > diff (
      @NotNull E initial
    );


    /// Returns a flow that forwards emissions to the specified pipe.
    ///
    /// @param pipe the pipe to forward emissions to
    /// @return A flow with the forwarding operation added
    /// @throws NullPointerException if the pipe is `null`

    @NotNull
    @Fluent
    Flow < E > forward (
      @NotNull Pipe < E > pipe
    );


    /// Returns a flow that extends the current pipeline with a guard operation.
    ///
    /// @param predicate the initial value used for guarding
    /// @throws NullPointerException if the predicate is `null`

    @NotNull
    @Fluent
    Flow < E > guard (
      @NotNull Predicate < ? super E > predicate
    );


    /// Returns a flow that extends the current pipeline with a guard operation.
    ///
    /// @param initial   the initial value to compare against
    /// @param predicate the predicate used for guarding
    /// @throws NullPointerException if the predicate is `null`

    @NotNull
    @Fluent
    Flow < E > guard (
      E initial,
      @NotNull BiPredicate < ? super E, ? super E > predicate
    );


    /// Returns a flow that limits the throughput of the current pipeline to a maximum number of emitted values
    ///
    /// @param limit the maximum number of emitted values

    @NotNull
    @Fluent
    Flow < E > limit (
      int limit
    );


    /// Returns a flow that limits the throughput of the current pipeline to a maximum number of emitted values
    ///
    /// @param limit the maximum number of emitted values

    @NotNull
    @Fluent
    Flow < E > limit (
      long limit
    );


    /// Returns a flow that allows inspection of emissions without modifying them.
    ///
    /// @param consumer the consumer that will be called for each emission passing through
    /// @return A flow with the peek operation added
    /// @throws NullPointerException if the consumer is `null`

    @NotNull
    @Fluent
    Flow < E > peek (
      @NotNull Consumer < E > consumer
    );


    /// Returns a flow that extends the current pipeline with a reduction operation.
    ///
    /// @param initial  the initial value used for reduction
    /// @param operator the operation applied for each reduction

    @NotNull
    @Fluent
    Flow < E > reduce (
      E initial,
      @NotNull BinaryOperator < E > operator
    );


    /// Returns a flow that extends the current pipeline with a replacement operation
    ///
    /// @param transformer the operation applied for each possible replacement

    @NotNull
    @Fluent
    Flow < E > replace (
      @NotNull UnaryOperator < E > transformer
    );


    /// Returns a flow that extends the current pipeline with a sampling operation
    ///
    /// @param sample the number of emittances between samples

    @NotNull
    @Fluent
    Flow < E > sample (
      int sample
    );


    /// Returns a flow that extends the current pipeline with a sampling operation
    ///
    /// @param sample the sampling rate that samples occur at

    @NotNull
    @Fluent
    Flow < E > sample (
      double sample
    );


    /// Returns a flow that extends the current pipeline with a sampling operation
    ///
    /// @param comparator the comparator used by a sift subassembly line
    /// @param configurer the consumer used to configure the sift assembly
    /// @throws NullPointerException if the comparator or configurer are `null`

    @NotNull
    @Fluent
    Flow < E > sift (
      @NotNull Comparator < E > comparator,
      @NotNull Consumer < ? super Sift < E > > configurer
    );


    /// Returns a flow that skips the first n emissions in the pipeline.
    ///
    /// @param n the number of emissions to skip
    /// @return A flow that skips the first n emissions
    /// @throws IllegalArgumentException if n is negative

    @NotNull
    @Fluent
    Flow < E > skip (
      long n
    );

  }

  /// Indicates a method that returns `this` instance for method chaining.

  @Documented
  @Retention ( SOURCE )
  @Target ( METHOD )
  public @interface Fluent {
  }

  /// An activity that returns a result.
  ///
  /// @param <R> The output of the function
  /// @param <T> The throwable class type

  @Utility
  @Extension
  @FunctionalInterface
  public interface Fn < R, T extends Throwable > {

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
  public interface Id {
  }

  /// Indicates a method expected to be idempotent.
  @Documented
  @Retention ( SOURCE )
  @Target ( METHOD )
  public @interface Idempotent {
  }

  /// Indicates a type whose instances can be compared by (object) reference for equality.

  @Documented
  @Retention ( SOURCE )
  @Target ( TYPE )
  public @interface Identity {
  }


  /// An interface that provides access to a pipe for emitting values
  ///
  /// @see Channel
  /// @see Pipe

  @Abstract
  sealed public interface Inlet < E >
    permits Channel {

    /// Returns a pipe that this inlet holds.
    ///
    /// @return A non-`null` pipe instance
    /// @see Pipe

    @NotNull
    Pipe < E > pipe ();

  }

  /// Represents one or more name (string) parts, much like a namespace.
  /// Instances are interned so equal hierarchies share the same reference and
  /// can be compared by identity.
  ///
  /// @see Subject#name()
  /// @see Extent
  /// @see Cortex#name(String)

  @Identity
  @Provided
  public interface Name
    extends Extent < Name, Name > {

    /// The separator used for parsing a string into name tokens.
    char SEPARATOR = '.';


    /// Returns a name that has this name as a direct or indirect prefix.
    /// Reuses interned segments so invoking this method with the same suffix
    /// yields the same instance.
    ///
    /// @param suffix the name to be appended to this name
    /// @return A name with the suffix appended
    /// @throws NullPointerException if the suffix parameter is `null`

    @NotNull
    Name name (
      @NotNull Name suffix
    );


    /// Returns a name whose hierarchy includes this name and the supplied path.
    /// The path is parsed using the `.` separator; empty segments are rejected
    /// and interned segments are reused when possible. Paths must not begin or
    /// end with `.` nor contain consecutive separators (for example: `.foo`,
    /// `foo.`, `foo..bar`).
    ///
    /// @param path the string to be parsed and appended to this name
    /// @return A name with the path appended as one or more name parts
    /// @throws NullPointerException     if the path parameter is `null`
    /// @throws IllegalArgumentException if the path is empty or contains empty segments

    @NotNull
    Name name (
      @NotNull String path
    );


    /// Returns a name that has this name as a direct prefix and appends the enum's [Enum#name()] value.
    /// The declaring type of the enum constant is used (per [Enum#getDeclaringClass()]);
    /// cached segments ensure repeated extensions with the same enum reuse the same instance.
    ///
    /// @param path the enum to be appended to this name
    /// @return A name with the enum name appended as a name part
    /// @throws NullPointerException if the path parameter is `null`

    @NotNull
    Name name (
      @NotNull Enum < ? > path
    );


    /// Returns an extension of this name from iterating over a specified [Iterable] of [String] values.
    /// When the iterable is empty this name is returned unchanged; interned segments are reused when possible.
    ///
    /// @param parts the [Iterable] to be iterated over
    /// @return The name following concatenation of name parts
    /// @throws NullPointerException if the [Iterable] is `null` or one of the values returned is `null`

    @NotNull
    Name name (
      @NotNull Iterable < String > parts
    );


    /// Returns an extension of this name from iterating over the specified [Iterable] and applying a transformation function.
    /// When the iterable is empty this name is returned unchanged; interned segments are reused when possible.
    ///
    /// @param <T>    the type of each value iterated over
    /// @param parts  the [Iterable] to be iterated over
    /// @param mapper the function to be used to map the iterable value type to a String type
    /// @return The name following concatenation of name parts
    /// @throws NullPointerException if the [Iterable], mapper, or one of the mapped values is `null`

    @NotNull
    < T > Name name (
      @NotNull Iterable < ? extends T > parts,
      @NotNull Function < T, String > mapper
    );


    /// Returns an extension of this name from iterating over the specified [Iterator] of [String] values.
    /// When the iterator is empty this name is returned unchanged; interned segments are reused when possible.
    ///
    /// @param parts the [Iterator] to be iterated over
    /// @return The name following concatenation of name parts
    /// @throws NullPointerException if the [Iterator] is `null` or one of the values returned is `null`

    @NotNull
    Name name (
      @NotNull Iterator < String > parts
    );


    /// Returns an extension of this name from iterating over the specified [Iterator] and applying a transformation function.
    /// When the iterator is empty this name is returned unchanged; interned segments are reused when possible.
    ///
    /// @param <T>    the type of each value iterated over
    /// @param parts  the [Iterator] to be iterated over
    /// @param mapper the function to be used to map the iterator value type
    /// @return The name following concatenation of name parts
    /// @throws NullPointerException if the [Iterator], mapper, or one of the mapped values is `null`
    /// @see #name(Iterable, Function)

    @NotNull
    < T > Name name (
      @NotNull Iterator < ? extends T > parts,
      @NotNull Function < T, String > mapper
    );


    /// Creates a `Name` from a [Class].
    /// The package and enclosing class simple names are used for the appended segments
    /// (matching [Class#getCanonicalName()] when available); local or anonymous classes
    /// fall back to the runtime name for their appended value.
    ///
    /// @param type the `Class` to be mapped to a `Name`
    /// @return A `Name` that appends the class hierarchy to this name using canonical segments when available
    /// @throws NullPointerException if the `Class` typed parameter is `null`

    @NotNull
    Name name (
      @NotNull Class < ? > type
    );


    /// Creates a `Name` from a `Member`.
    /// The declaring class hierarchy and member name are appended, reusing interned segments.
    ///
    /// @param member the `Member` to be mapped to a `Name`
    /// @return A `Name` mapped to the `Member`
    /// @throws NullPointerException if the `Member` typed parameter is `null`

    @NotNull
    Name name (
      @NotNull Member member
    );


    /// Returns a `CharSequence` representation of this name and its enclosing names.
    /// Applies the mapper to each [#value()] and joins results using the dot separator.
    ///
    /// @param mapper the function used to convert the [#value()] to a character sequence
    /// @return A non-`null` `CharSequence` representation.
    /// @throws NullPointerException if the mapper is `null`

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
  @Target ( {PARAMETER, METHOD, FIELD} )
  public @interface NotNull {
  }

  /// An activity without a return value.
  ///
  /// @param <T> The throwable class type

  @Utility
  @Extension
  @FunctionalInterface
  public interface Op < T extends Throwable > {

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

  /// An abstraction that serves to pass typed values along a pipeline.
  ///
  /// ## Threading Model
  ///
  /// Pipe emissions within a circuit are queued and processed sequentially on
  /// the circuit's processing thread, ensuring deterministic ordering. This means:
  ///
  /// - Emissions from the same pipe are processed in the order they were emitted
  /// - Emissions run exclusively on the circuit thread; state accessed only there
  ///   does not require additional synchronization
  /// - The circuit guarantees that emissions do not execute concurrently
  /// - Coordination with threads outside the circuit still requires explicit
  ///   synchronization or lifecycle barriers
  ///
  /// @param <E> the class type of the emitted values
  /// @see Channel
  /// @see Flow
  /// @see Subscriber
  /// @see Registrar

  @Abstract
  @Extension
  public interface Pipe < E > {

    /// A singleton empty pipe instance that ignores all emissions.
    Pipe < ? > EMPTY = _ -> {
    };


    /// Returns an empty pipe that ignores all emissions.
    ///
    /// @param <E> the class type of the emitted values
    /// @return A pipe that does nothing when emissions are received

    @NotNull
    @SuppressWarnings ( "unchecked" )
    static < E > Pipe < E > empty () {

      return (Pipe < E >) EMPTY;

    }


    /// A method for passing a data value along a pipeline.
    ///
    /// Emissions are queued by the circuit and processed sequentially on the
    /// circuit's processing thread, ensuring no concurrent execution occurs.
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
  @Extension
  public interface Pool < T > {

    /// Returns an instance of the pooled type for a given substrate.
    ///
    /// @param substrate The substrate used to determine the name lookup
    /// @return A newly created instance or a previously pooled instance
    /// @throws NullPointerException if the substrate is null

    @NotNull
    default T get (
      @NotNull final Substrate < ? > substrate
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
      @NotNull final Subject < ? > subject
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
  public @interface Provided {
  }

  /// Links a [Subject] to a [Pipe]
  ///
  /// @see Subject
  /// @see Pipe
  /// @see Subscriber

  @Temporal
  @Provided
  public interface Registrar < E > {

    /// Registers a [Pipe] with a `Registrar` associated with a [Source].
    ///
    /// @param pipe The [Pipe] to be associated with a [Subject]

    void register (
      @NotNull Pipe < E > pipe
    );

  }

  /// An interface that serves to explicitly dispose of resources.
  ///
  /// @see Sink
  /// @see Subscription
  /// @see Scope

  @Abstract
  sealed public interface Resource
    permits Circuit,
            Clock,
            Sink,
            Subscription {

    /// A method that is called to indicate that no more usage will be made
    /// of the instance and that underlying resources held can be released.

    @Idempotent
    default void close () {
    }

  }

  /// Represents a resource management scope.
  ///
  /// @see Resource
  /// @see Closure
  /// @see Cortex#scope()
  /// @see AutoCloseable

  @Provided
  non-sealed public interface Scope
    extends Substrate < Scope >,
            Extent < Scope, Scope >,
            AutoCloseable {

    /// Operations on a scope are only valid while it remains open.
    /// Once [close()][Scope#close()] has been invoked, requesting closures, registering resources,
    /// or creating child scopes will throw an [IllegalStateException].
    ///
    @Idempotent
    @Override
    void close ();


    /// Creates a closure for the specified resource within this scope.
    ///
    /// @param <R>      the type of resource
    /// @param resource the resource to be managed within the closure
    /// @return A closure that manages the specified resource
    /// @throws NullPointerException  if the resource is `null`
    /// @throws IllegalStateException if this scope has been closed
    /// @implNote Repeated calls with the same resource return the same closure until the closure has been consumed.

    @NotNull
    < R extends Resource > Closure < R > closure (
      @NotNull R resource
    );


    /// Registers a resource with this scope for lifecycle management.
    ///
    /// @param <R>      the type of resource
    /// @param resource the resource to be registered with this scope
    /// @return The registered resource (same as input)
    /// @throws NullPointerException  if the resource is `null`
    /// @throws IllegalStateException if this scope has been closed

    @NotNull
    < R extends Resource > R register (
      @NotNull R resource
    );


    /// Creates a new anonymous child scope within this scope.
    ///
    /// @return A new scope that is a child of this scope
    /// @throws IllegalStateException if this scope has been closed

    @NotNull
    Scope scope ();


    /// Creates a new named child scope within this scope.
    ///
    /// @param name the name for the new child scope
    /// @return A new scope that is a child of this scope
    /// @throws NullPointerException  if the name is `null`
    /// @throws IllegalStateException if this scope has been closed

    @NotNull
    Scope scope ( @NotNull Name name );

  }

  /// A filtering mechanism for values in a pipeline based on comparison criteria.
  ///
  /// Sift provides various methods to filter values based on thresholds, ranges,
  /// and relative positions in a sorted sequence.
  ///
  /// @param <E> the type of elements being filtered
  /// @see Flow#sift(Comparator, Consumer)
  /// @see Assembly

  @Temporal
  @Provided
  non-sealed public interface Sift < E >
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

  /// An in-memory buffer of captures that is also a Substrate.
  ///
  /// A Sink is created from a Context and is given its own identity (Subject)
  /// that is a child of the Context it was created from. It subscribes to its
  /// source to capture all emissions into an internal buffer.
  ///
  /// @param <E> the class type of the emitted value
  /// @see Capture
  /// @see Cortex#sink(Context)
  /// @see Resource

  @Provided
  non-sealed public interface Sink < E >
    extends Substrate < Sink < E > >,
            Resource {

    /// Returns a stream representing the events that have accumulated since the
    /// sink was created or the last call to this method.
    ///
    /// @return A stream consisting of stored events captured from channels.
    /// @see Capture

    @NotNull
    Stream < Capture < E, Channel < E > > > drain ();

  }

  /// An opaque interface representing a variable (slot) within a state chain.
  ///
  /// Slots serve as templates for state lookups, providing both a query key
  /// (name + type) and a fallback value. Matching occurs on slot name identity
  /// AND slot type - multiple slots may share the same name if they have different types.
  ///
  /// For primitive types (boolean, int, long, float, double), the [#type()] method
  /// returns the primitive class (e.g., `int.class`, not `Integer.class`).
  ///
  /// Slot instances are immutable - the value returned by [#value()] never changes
  /// across multiple invocations on the same slot instance.
  ///
  /// @param <T> the class type of the value extracted from the target
  /// @see State#value(Slot)
  /// @see State#values(Slot)
  /// @see Name
  /// @see Cortex#slot

  @Utility
  @Provided
  public interface Slot < T > {

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
  /// This interface represents the subscription capability of components,
  /// allowing subscribers to receive notifications of new channels and emissions.
  /// Source is implemented by components through the Context interface.
  ///
  /// @param <E> the class type of emitted value
  /// @see Subscriber
  /// @see Subscription
  /// @see Context
  /// @see Component

  @Abstract
  sealed public interface Source < E >
    permits Context {

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
  /// Slots iterate from the most recently added entry to the oldest, supporting persistent updates.
  ///
  /// @see Slot
  /// @see Subject#state()
  /// @see Cortex#state()

  @Provided
  public interface State
    extends Iterable < Slot < ? > > {

    /// Returns a new [State][State] with duplicates removed.
    /// Matching occurs on slot name identity and slot type; the retained value is
    /// always the most recently added slot while iteration order runs from the
    /// earliest retained slot to the most recent.
    ///
    /// @return A new [State][State] with duplicates removed

    @NotNull
    State compact ();


    /// Returns a state that includes a [Slot][Slot] mapped to the specified value.
    /// The slot is inserted before existing entries; if an equal slot (same name and value)
    /// already exists this state instance is returned.
    ///
    /// @param name  the name of the slot
    /// @param value the value of the slot
    /// @return A state containing the specified slot
    /// @throws NullPointerException if name is `null`

    @NotNull
    State state (
      @NotNull Name name,
      int value
    );


    /// Returns a state that includes a [Slot][Slot] mapped to the specified value.
    /// Semantics match [#state(Name, int)] for a `long` value.
    ///
    /// @param name  the name of the slot
    /// @param value the value of the slot
    /// @return A state containing the specified slot
    /// @throws NullPointerException if name is `null`

    @NotNull
    State state (
      @NotNull Name name,
      long value
    );


    /// Returns a state that includes a [Slot][Slot] mapped to the specified value.
    /// Semantics match [#state(Name, int)] for a `float` value.
    ///
    /// @param name  the name of the slot
    /// @param value the value of the slot
    /// @return A state containing the specified slot
    /// @throws NullPointerException if name is `null`

    @NotNull
    State state (
      @NotNull Name name,
      float value
    );


    /// Returns a state that includes a [Slot][Slot] mapped to the specified value.
    /// Semantics match [#state(Name, int)] for a `double` value.
    ///
    /// @param name  the name of the slot
    /// @param value the value of the slot
    /// @return A state containing the specified slot
    /// @throws NullPointerException if name is `null`

    @NotNull
    State state (
      @NotNull Name name,
      double value
    );


    /// Returns a state that includes a [Slot][Slot] mapped to the specified value.
    /// Semantics match [#state(Name, int)] for a `boolean` value.
    ///
    /// @param name  the name of the slot
    /// @param value the value of the slot
    /// @return A state containing the specified slot
    /// @throws NullPointerException if name is `null`

    @NotNull
    State state (
      @NotNull Name name,
      boolean value
    );


    /// Returns a state that includes a [Slot][Slot] mapped to the specified value.
    /// The slot is inserted before existing entries; if an equal slot (same name and value)
    /// already exists this state instance is returned.
    ///
    /// @param name  the name of the slot
    /// @param value the value of the slot
    /// @return A state containing the specified slot
    /// @throws NullPointerException if name or value is `null`

    @NotNull
    State state (
      @NotNull Name name,
      @NotNull String value
    );


    /// Returns a state that includes a [Slot][Slot] mapped to the specified value.
    /// The slot is inserted before existing entries; if an equal slot (same name and value)
    /// already exists this state instance is returned.
    ///
    /// @param name  the name of the slot
    /// @param value the value of the slot
    /// @return A state containing the specified slot
    /// @throws NullPointerException if name or value is `null`

    @NotNull
    State state (
      @NotNull Name name,
      @NotNull Name value
    );


    /// Returns a state that includes a [Slot][Slot] mapped to the specified value.
    /// The slot is inserted before existing entries; if an equal slot (same name and value)
    /// already exists this state instance is returned.
    ///
    /// @param name  the name of the slot
    /// @param value the value of the slot
    /// @return A state containing the specified slot
    /// @throws NullPointerException if name or value is `null`

    @NotNull
    State state (
      @NotNull Name name,
      @NotNull State value
    );


    /// Returns a state that includes the [Slot][Slot] specified.
    /// The slot is inserted before existing entries; if an equal slot (same name and value)
    /// already exists this state instance is returned.
    ///
    /// @param slot the slot to be added
    /// @return A state containing the specified slot
    /// @throws NullPointerException if slot is `null`

    @NotNull
    State state (
      @NotNull Slot < ? > slot
    );


    /// Returns a state that includes a [Slot][Slot] with a [Name] value derived from an enum.
    /// The slot's name is derived from the enum's declaring class,
    /// and the slot's value is a [Name] created from the enum constant's name.
    /// The slot is inserted before existing entries; if an equal slot (same name and value)
    /// already exists this state instance is returned.
    ///
    /// @param value the enum to create a slot from
    /// @return A state containing the specified slot
    /// @throws NullPointerException if value is `null`

    @NotNull
    State state (
      @NotNull Enum < ? > value
    );


    /// Returns a sequential [Stream][Stream] of all [Slots][Slot] in this state,
    /// iterating from the most recently added slot to the oldest.
    ///
    /// @return A non-`null` sequential stream of slots

    @NotNull
    Stream < Slot < ? > > stream ();


    /// Returns the value of a slot matching the specified template
    /// or the template's own value when no slot is present.
    /// Matching occurs on slot name identity and slot type.
    ///
    /// @param slot the slot to lookup and fallback onto for the source of a value
    /// @return The stored value when present; otherwise the value returned by the supplied slot
    /// @throws NullPointerException if slot is `null`

    < T > T value (
      @NotNull Slot < T > slot
    );


    /// Returns a sequential [Stream][Stream] of all values from slots that match the specified template.
    /// Matching occurs on slot name identity and slot type, and results are ordered from the most recent slot to the oldest.
    ///
    /// @param slot the slot to lookup
    /// @return A non-`null` stream of matching values
    /// @throws NullPointerException if slot is `null`

    @NotNull
    < T > Stream < T > values (
      @NotNull Slot < ? extends T > slot
    );

  }

  /// A [Subject][Subject] represents a referent that maintains an identity as well as [State][State].
  /// Subject is parameterized by its owning substrate type, enabling type-safe subject extraction.
  ///
  /// @param <S> the substrate type this subject represents
  /// @see Id
  /// @see Name
  /// @see State
  /// @see Substrate

  @Identity
  @Provided
  public interface Subject < S extends Substrate < S > >
    extends Extent < Subject < S >, Subject < ? > > {

    /// Returns a unique identifier for this subject.
    ///
    /// @return A unique identifier for this subject
    /// @see Id

    @NotNull
    Id id ();


    /// The {@link Name} associated with this reference.
    ///
    /// @return A non-null name reference
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
          type ().getSimpleName (),
          id ()
        );

    }


    /// Returns the current state of this subject.
    ///
    /// @see State

    @NotNull
    State state ();


    /// Returns the string representation returned from [#path()].
    ///
    /// @return A non-`null` string representation.

    @Override
    @NotNull
    String toString ();


    /// Returns the class of the substrate this subject represents.
    /// This enables type-safe pattern matching and discrimination.
    ///
    /// @return The substrate class type
    /// @see Substrate

    @NotNull
    Class < S > type ();

  }

  /// Connects outlet pipes with emitting subjects within a source.
  ///
  /// ## Threading Model
  ///
  /// Subscriber callbacks are always executed on the circuit's processing thread,
  /// ensuring sequential processing and deterministic ordering. This means:
  ///
  /// - State touched only from the circuit thread does not require additional
  ///   synchronization
  /// - All subscriber invocations for a given circuit happen sequentially
  /// - The callback cannot be interrupted or executed concurrently by multiple threads
  /// - Coordination with other threads must still be handled explicitly by callers
  ///
  /// @param <E> the class type of the emitted value
  /// @see Source
  /// @see Subject
  /// @see Registrar
  /// @see Pipe
  /// @see Cortex#subscriber(Name, BiConsumer)

  @Provided
  non-sealed public interface Subscriber < E >
    extends Substrate < Subscriber < E > > {

    /// Invoked when a new subject becomes available from the subscribed source.
    ///
    /// This method is always called on the circuit's processing thread, ensuring
    /// sequential execution and eliminating the need for synchronization.
    ///
    /// The subject parameter uses a wildcard to allow subscribers to dynamically
    /// handle any substrate type. Use {@code subject.type()} for pattern matching.
    ///
    /// @param subject   the [Subject] of the [Channel] (wildcard allows any substrate type)
    /// @param registrar a registrar used for registering a pipe to capture sourced events
    /// @throws NullPointerException if subject or registrar are `null`

    void accept (
      @NotNull Subject < Channel < E > > subject,
      @Temporal @NotNull Registrar < E > registrar
    );

  }

  /// An interface used for unregistering interest in receiving subscribed events.
  ///
  /// @see Source#subscribe(Subscriber)
  /// @see Resource
  /// @see Clock#consume(Name, Clock.Cycle, Pipe)

  @Provided
  non-sealed public interface Subscription
    extends Substrate < Subscription >,
            Resource {

  }


  /// Base interface for all substrate components that have an associated subject.
  /// Substrate is self-referential and parameterized by its own type, enabling
  /// typed subject extraction where {@code substrate.subject()} returns {@code Subject<ThisSubstrateType>}.
  ///
  /// @param <S> the self-referential substrate type
  /// @see Subject
  @Abstract
  @Extension
  sealed public interface Substrate < S extends Substrate < S > >
    permits Channel,
            Context,
            Scope,
            Sink,
            Subscriber,
            Subscription {

    /// Returns the typed subject identifying this substrate.
    /// The subject is parameterized by this substrate's type for type-safe access.
    ///
    /// @return The typed subject associated with this substrate
    /// @see Subject

    @NotNull
    Subject < S > subject ();

  }


  /// A self-typed interface that provides a fluent tap operation.
  ///
  /// This interface enables method chaining by providing a tap method that
  /// applies a consumer to the instance and returns the instance itself.
  ///
  /// @param <T> the self-type of the implementing class

  @SuppressWarnings ( "unchecked" )
  @Utility
  @Extension
  public interface Tap < T extends Tap < T > > {

    /// Applies the specified consumer to this instance and returns this instance.
    ///
    /// @param consumer the consumer to apply to this instance
    /// @return This instance
    /// @throws NullPointerException if the consumer is `null`

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
  public @interface Temporal {
  }

  /// Indicates a type that serves a utility purpose.

  @Documented
  @Retention ( SOURCE )
  @Target ( TYPE )
  public @interface Utility {
  }

  /// Returns the singleton Cortex instance.
  ///
  /// The Cortex implementation is determined by the system property {@value #PROVIDER_PROPERTY}.
  /// The specified class must have a public static method: {@code public static Cortex cortex()}
  /// which will be invoked reflectively to obtain the singleton instance.
  ///
  /// For the default alpha SPI implementation, set the system property to:
  /// {@code io.humainary.substrates.spi.alpha.Provider}
  ///
  /// @return The singleton Cortex instance
  /// @throws IllegalStateException if the provider class cannot be loaded, the method cannot be found,
  ///                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               or the method invocation fails
  /// @see Cortex

  @NotNull
  public static Cortex cortex () {

    if ( cortex == null ) {

      synchronized ( Substrates.class ) {

        if ( cortex == null ) {

          cortex = install ();

        }

      }

    }

    return cortex;

  }

  /// Loads the Cortex instance from the provider class specified by system property.
  ///
  /// @return A Cortex instance from the provider
  /// @throws IllegalStateException if loading fails

  @NotNull
  private static Cortex install () {

    final var provider =
      System.getProperty (
        PROVIDER_PROPERTY
      );

    if ( provider == null ) {

      throw new IllegalStateException (
        "System property '%s' not set. Please specify a Cortex provider class."
          .formatted ( PROVIDER_PROPERTY )
      );

    }

    try {

      final var instance =
        Class.forName (
          provider
        ).getMethod (
          "cortex"
        ).invoke (
          null
        );

      if ( instance instanceof Cortex ctx ) {

        return ctx;

      }

      throw new IllegalStateException (
        "Provider method '%s.cortex()' did not return a Cortex instance"
          .formatted ( provider )
      );

    } catch (
      final ReflectiveOperationException e
    ) {

      throw new IllegalStateException (
        "Failed to load substrates spi provider class '%s': %s"
          .formatted (
            provider,
            e.getMessage ()
          ),
        e
      );

    }

  }

}
