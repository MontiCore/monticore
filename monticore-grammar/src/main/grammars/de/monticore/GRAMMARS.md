
# MontiCore Grammars - an Overview

[MontiCore](http://www.monticore.de) is a language workbench. It uses 
grammars to describe DSLs. The extended 
grammar format allows to compose grammars, to inherit, extend, embedd 
and aggregate grammars (see the reference manual for details).

Here comes a list of grammars available in the MontiCore core project 
together with short descriptions and their status:


## Status of Grammars 

The typical status of a grammar is:

1. **MontiCore stable**:
Such a grammar is meant to be stable in the further development of 
MontiCore. The grammar is tested and assumed to be of high quality.
It may rarely happen that smaller extensions are made in a conservative 
form, which means that (1) composition with any other grammars,
(2) extensions and adaptations and (3) handwritten extensions will 
still work.

1. **Intended to become stable (Beta)**:
Such a grammar is in the process of becoming stable. One might already 
include the grammar, but some changes may still appear.
(See task list for potential changes.)

1. **Deprecated**:
The grammar should not be used anymore, it is deprecated and a newer
version of the content exists in another grammar.

1. **Unclarified**:
Some of the grammars are just there to be used for example as
tests or as inspirations for your own definitions. It may be that 
such a grammar becomes stable, if enough interest exists.

The deprecated grammars are typically not listed in this overview.
There may also be further unclarfied grammars around.


## List of Grammars in package de.monticore

### MCBasics.mc4  (stable)
* This grammar defines absolute basics, such as spaces, 
Java-like comments and Names. 
It should be useful in many languages.
  
  
## List of Grammars in package de.monticore.types

These grammars generally deal with type definitions and build on each 
other:

### MCBasicTypes.mc4 (stable)
* This grammar defines basic types. This eases the reuse of type 
structures in languages similar to Java, that are somewhat 
simplified, e.g. without generics.
* The grammar contains types from Java, e.g., primitives, void, 
classes (also sometimes called "reference types").
 
### MCCollectionTypes.mc4 (stable)
* This grammar defines four generics: List<>, Map<,>, Set<> and 
Optional<> on top of basic types.
* These four generics correspond to a typical predefined set of generic 
types for example used in connection with UML class diagrams or the
OCL. UML associations typically have those association multiplicities and 
therefore these types are of interest.
* This eases the reuse of type structures in languages similar to Java,
that are somewhat simplified, e.g. without general generics.


### MCSimpleGenericTypes.mc4 (stable)
* This grammar introduces freely defined generic types
such as Blubb<A>, Bla<B,C>, Foo<Blubb<D>>
* These generics are covering a wide range of uses for generic types,
although they don't cover type restrictions on the arguments, like in 
Java. 


### MCFullGenericTypes.mc4 (stable)
* This grammar completes the type definitions to 
support the full Java type system including wildcards Blubb<? extends A>
* A general advice: When you are not sure that you need this kind of
types, then use a simpler version from above. Type checking ist tricky.



## List of Grammars in package de.monticore.expressions

* expressions/AssignmentExpressions.mc4
* expressions/BitExpressions.mc4
* expressions/CommonExpressions.mc4
* expressions/ExpressionsBasis.mc4
* expressions/JavaClassExpressions.mc4
* expressions/OCLExpressions.mc4
* expressions/SetExpressions.mc4



## Further grammars (status: unclarified):

* Cardinality.mc4
* Completeness.mc4
* lexicals/Lexicals.mc4
* literals/Literals.mc4
* MCBasicLiterals.mc4
* MCBasics.mc4
* MCCommon.mc4
* MCHexNumbers.mc4
* MCJavaLiterals.mc4
* MCNumbers.mc4
* StringLiterals.mc4
* UMLModifier.mc4
* UMLStereotype.mc4






