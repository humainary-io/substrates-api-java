# All [Humainary.io](https://humainary.io) Projects

## Description

The Humainary Initiative is dedicated to revolutionizing the engineering of complex systems by
championing three core principles: sensibility, simplicity, and sophistication. Our mission is to
empower engineers and organizations to build, operate, and evolve systems that are not only highly
observable and adaptable but also intuitive and resilient in the face of increasing complexity.

The future of observability is all about creating new tools and taxonomies that let us understand
how systems work at various scales and across state spaces. Companies that invest in extensible
observability platforms get a big edge by learning more about their systems, which leads to making
them faster, more reliable, and exceedingly effective. We must radically rethink observability,
moving beyond the yesteryear three-pillar approach.

## Substrates

Substrates is a modern and forward-looking take on observability.

The Cortex is the bootstrap class into the Substrates runtime. From there we can create one or more
Circuits that represent the resource scaling management strategy – the more Circuits, the more
Queues, and parallelism.

A Circuit allows us to create one or more Conduits, where a Conduit is a named set of Channels of
the same emittance data type – a Conduit is limited to a single communication content type. We can
have many Conduits of the same type.

The Circuit is only playing the role of a factory in creating a Conduit and providing it access to
the underlying queued pipeline. It doesn’t manage the lifecycle of the Conduits, though it does
control the work Queue employed.

Please note that a Clock is a variant of a Conduit where the ability to create and access Channels
isn’t exposed directly.

A Conduit has a Source that clients can use to register one or more Subscribers, each having a
Subscription that’s managed by the Source but also allowing for cancellation by a client.
Subscribers don’t have access to Channels instead they’re provided the Subject of Channel and the
Registrar (not depicted here) associated with that Subject. Using the Registrar, a Subscriber can
register one or more Pipes with each Subject, though in practice it’s usually one.

The Subscription is at the Source level, whereas the Subscriber registers outbound Pipes at the
Subject level.

A Conduit creates multiple named Channels, which can be decorated by a Composer (not depicted here).
There’s only one Channel per Name supplied in each call to a Conduit’s get method. Channels are thus
managed within a container.

Each Channel has a Pipe that can be wrapped by a percept, created by a Composer, to emit data into a
Circuit’s Queue.