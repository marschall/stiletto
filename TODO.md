- intermediate class with generic interface that binds a type parameter
- inheritable annotations getAllAnnotationMirrors <-> Elements.getAllAnnotationMirrors(Element)

- EL expression for proxy name
- exclude Cloneable, Serializable, Externalizable, Comparable
- reconsider proxing Object methods and common interfaces
- reconsider visibilty of proxy class and proxy class constructors
- add javadoc to proxy class constructors
- better advice selection for primitive types
- can @AfterReturning change return value?
- @Around should probably can change return value

- method selection on annotation
- @OnlyWithAnnotationPresent(Transactional.class)
- @OnlyWithAnnotationPresentAndAllMathingValues(Transactional.class) @Transactional(readonly = true, a1 = v1, a2 = v2)
- @OnlyWithAnnotationPresentAndNonDefaultMathingValues(Transactional.class) @Transactional(readonly = true, /* ignored */)