A Scala Composition Tool
-------------------------

This is what could probably be considered a somewhat misguided attempt of a 
.Net/C# developer to come to terms with Scala.  To be honest I don't know 
if there is a real need for a "Compisition Framework" in the Scala world.
The existing patterns for Dependency Injection (specifically the Cake pattern)
and the ability to use functional techniques such as currying and closures
provide a lot of compelling and interesting solutions to the problem.  They do not,
however, address issues like Lifecycle management, contextual bindings, and component
discovery.

One thing I have been wanting more and more when working with .Net code, is 
some form of "smart factory", that would provide some way to provide runtime
type resolution based on contextual configuration.  Scala has a lot of language
features that seem to have potential to make interacting with the container 
more "natural" and expressive.  The obvious example being a DSL for registering
type bindings, but I think there are a lot of other things that can be done to
reduce the footprint of the container within the code.  I think it would also
be quite interesting to look at resolution of higher-order functions within a
container.  

Needless to say, right now this seems like a good idea.  I'm willing to accept
the fact that it is solving a problem that doesn't exist and just call it an
interesting learning experience.

----
== Currently Does:==
* Basic Type->Type resolution (typically dealing with Traits -> Classes, but should work for Class -> Class as well)
* Constructor selection based on simple heuristic which uses the constructor with the fewest dependencies
* Nested type construction.  As long as the types used by the constructor are bound, it will resolve them
* Open generics support, with the added trick of using the more specific bindings before the more general

== Still to do: ==
* Explode nicely when circular dependencies show up (currently it will blow the stack)
* Context-based resolutions (so you can say things like if I'm being asked for by class X use Y)
* Some form of nifty binding syntax/dsl that can crawl types and auto-register
* Ability to bind to the Scala duck-typ-ish expressions ([{ def IDoSomething:Unit }])
* Ability to add traits at time of request (not sure if this is possible, but it would be cool)
* Other stuff I'm not thinking of