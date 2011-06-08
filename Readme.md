A Scala IoC Container
---------------------

This is what could probably be considered a somewhat misguided attempt of a 
.Net/C# developer to come to terms with Scala.  To be honest I don't know 
if there is a real need for IoC in the Scala world.  The existing patterns 
(specifically the Cake pattern) and ability to use functional techniques such
as currying and closures provide a lot of compelling and interesting solutions
to the problem of Dependency Injection.  They do not, however, address issues
like Lifecycle management and contextual bindings.

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
