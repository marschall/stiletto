An AOP weaver that uses source file generation.

AOP without runtime reflection.
AOP without runtime bytecode generation.

Declaration of Ideals

No
- hard to understand stack traces
- stepping through methods with no source
- ClassLoader tricks (#defineClass, Unsafe)
- runtime reflection
- framework or container dependencies (apart from annotations and interfaces)
- (runtime) bytecode generation -> no source
- ThreadLocal hacks
- runtime library
- not framework dependenices (other than annoations or interfaces) -> easy to migrate to something else

But
- instantiation where unavoidable

https://docs.spring.io/spring/docs/current/spring-framework-reference/html/aop.html#aop-introduction-defn

https://ptrthomas.wordpress.com/2006/06/06/java-call-stack-from-http-upto-jdbc-as-a-picture/
https://ptrthomas.files.wordpress.com/2006/06/jtrac-callstack.pdf
https://dzone.com/articles/filtering-stack-trace-hell

http://hannesdorfmann.com/annotation-processing/annotationprocessing101

Downsides of Spring AOP
- harder to read stack traces
- harder to debug code (step into)

Performance Downsides of Spring AOP
- more allocation
-- increased allocation pressure
-- more frequent garbage collection
- more stack usage
-- need for bigger stacks
-- increased root set scanning times
- code unlikely to inline -> key HotSpot optimization, gateway to other optimizations


events
https://spring.io/blog/2015/02/11/better-application-events-in-spring-framework-4-2

org.springframework.dao.support.PersistenceExceptionTranslationInterceptor

not proxied:
- common interfaces:
-- Cloneable, Serializable, Externalizable, Comparable
- Object methods
-- equals, hashCode -> can't guarantee symmetry
-- toString
-- clone
-- finalize, pointless

Unsure:
- use Supplier instead of ActualMethodCall
- use Function<Object[], R> instead of ChangeableMethodCall

org.springframework.aop.support.AopUtils.isAopProxy(Object)

Advisor
Advised
