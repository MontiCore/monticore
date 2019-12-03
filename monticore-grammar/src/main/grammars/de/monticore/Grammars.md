<!-- (c) https://github.com/MontiCore/monticore -->

# MontiCore Core Grammars - an Overview

[MontiCore](http://www.monticore.de) is a language workbench. It uses 
grammars as primary mechanism to describe DSLs. The extended 
grammar format allows to **compose language components** by
(1) inheriting, (2) extending, (3) embedding 
and (4) aggregating grammars (see the reference manual for details).
From the grammars a lot of infrastructructure is generated, that is as well
composable, can be **extended with handwrittten code** and most imprtandly, these
extensions and the grammar composition are compatible, which
leads to optimal forms of **reuse**.

Here comes a list of language components, mainly defined through a 
primary grammar, available in the MontiCore core project 
together with short descriptions and their status ([Status of Grammars](00.org/Explanations/StatusOfGrammars.md)).

The list covers the core grammars to be found in the `MontiCore/monticore` 
project under `monticore-grammar/src/main/grammars/` in packages 

* `de.monticore`
* `de.monticore.expressions`
* `de.monticore.literals`
* `de.monticore.statements`
* `de.monticore.types`


## General: List of Grammars in package `de.monticore`

### [MCBasics.mc4](https://git.rwth-aachen.de/monticore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/MCBasics.mc4)  (stable)
* This grammar defines absolute basics, such as spaces, 
Java-like comments and Names. 
It should be useful in many languages.
  

## Types: List of Grammars in package `de.monticore.types`

These grammars generally deal with type definitions and build on each 
other:

### [MCBasicTypes.mc4](monticore-grammar/src/main/grammars/de/monticore/types/MCBasicTypes.mc4) (stable)
* This grammar defines basic types. This eases the reuse of type 
structures in languages similar to Java, that are somewhat 
simplified, e.g. without generics.
* The grammar contains types from Java, e.g., primitives, void, 
classes (also sometimes called "reference types").
 
### [MCCollectionTypes.mc4](monticore-grammar/src/main/grammars/de/monticore/types/MCCollectionTypes.mc4) (stable)
* This grammar defines four generics: `List<A>`, `Map<A,B>`, `Set<A>` and 
`Optional<A>` on top of basic types.
* These four generics correspond to a typical predefined set of generic 
types for example used in connection with UML class diagrams or the
OCL. UML associations typically have those association multiplicities and 
therefore these types are of interest.
* This eases the reuse of type structures in languages similar to Java,
that are somewhat simplified, e.g. without general generics.


### [MCSimpleGenericTypes.mc4](monticore-grammar/src/main/grammars/de/monticore/types/MCSimpleGenericTypes.mc4) (stable)
* This grammar introduces freely defined generic types
such as Blubb<A>, Bla<B,C>, Foo<Blubb<D>>
* These generics are covering a wide range of uses for generic types,
although they don't cover type restrictions on the arguments, like in 
Java. 


### [MCFullGenericTypes.mc4](monticore-grammar/src/main/grammars/de/monticore/types/MCFullGenericTypes.mc4) (Beta: In Stabilization)
* This grammar completes the type definitions to 
support the full Java type system including wildcards Blubb<? extends A>
* A general advice: When you are not sure that you need this kind of
types, then use a simpler version from above. Type checking ist tricky.



## Expressions: List of Grammars in package `de.monticore.expressions`

Expressions are defined in several grammars forming a (nonlinear) hierarchy,
so that developers can choose the optimal grammar they want to build on 
for their language and combine these with the appropriate typing 
infrastructure.

This modularity of expressions and associated types greatly eases 
the reuse of type structures in languages similar to Java.


### [ExpressionsBasis.mc4](monticore-grammar/src/main/grammars/de/monticore/expressions/ExpressionsBasis.mc4) (Beta: In Stabilization)
* This grammar defines core interfaces for expressions and imports the 
kinds of symbols necessary.
* The symbols are taken over from the TypeSymbols grammar (see below).
* A hierarchy of conservative extensions to this grammar realize
these interfaces in various forms.


### [CommonExpressions.mc4](monticore-grammar/src/main/grammars/de/monticore/expressions/CommonExpressions.mc4) (stable)
* This grammar defines a typical standard set of operations for
expressions. 
* This is a subset of Java as well as OCL/P, 
mainly for arithmetic, comparisons, variable use (v), 
attribute use (o.att), method call (foo(arg,arg2)) and brackets (exp).


### [BitExpressions.mc4](monticore-grammar/src/main/grammars/de/monticore/expressions/BitExpressions.mc4) (stable)
* This grammar defines a typical standard set of operations for
expressions. 
* This is a subset of Java for binary expressions 
like <<, >>, >>>, &, ^ and |


### [AssignmentExpressions.mc4](monticore-grammar/src/main/grammars/de/monticore/expressions/AssignmentExpressions.mc4) (stable)
* This grammar defines all Java expressions that have side effects.
* This includes assignment expressions like =, +=, etc. and 
suffix and prefix expressions like ++, --, etc.


### [JavaClassExpressions.mc4](monticore-grammar/src/main/grammars/de/monticore/expressions/JavaClassExpressions.mc4) (stable)
* This grammar defines Java specific class expressions like super, 
this, type cast, etc.
* This grammar should only be included, when a mapping to Java is
intended and the full power of Java should be available in the 
modelling language.


### [SetExpressions.mc4](monticore-grammar/src/main/grammars/de/monticore/expressions/SetExpressions.mc4) (Beta: In Stabilization)
* This grammar defines set expressions like union, intersection etc.
these operations are typical for a logic with set operations, like 
UML's OCL.


### [OCLExpressions.mc4](monticore-grammar/src/main/grammars/de/monticore/expressions/OCLExpressions.mc4) (Alpha: Needs restructuring)
* This grammar defines a expressions typical to UMLs OCL .
* This grammar will be restructured especially for the non expression part.



## Literals: List of Grammars in package `de.monticore.literals`

### [MCLiteralsBasis.mc4](monticore-grammar/src/main/grammars/de/monticore/literals/MCLiteralsBasis.mc4) (stable)
* This grammar defines core interface for literals.
* Several conservative extensions to this grammar realize
various forms of literals.

### [MCCommonLiterals.mc4](monticore-grammar/src/main/grammars/de/monticore/literals/MCCommonLiterals.mc4) (Beta: In Stabilization)

### [MCJavaLiterals.mc4](monticore-grammar/src/main/grammars/de/monticore/literals/MCJavaLiterals.mc4) (Beta: In Stabilization)



## Further grammars (status: to be handled):


### Beta: to become stable in the next iteration

* [UMLModifier.mc4](monticore-grammar/src/main/grammars/de/monticore/UMLModifier.mc4) (Beta: In Stabilization)
* [UMLStereotype.mc4](monticore-grammar/src/main/grammars/de/monticore/UMLStereotype.mc4) (Beta: In Stabilization)
* [JavaLight.mc4](monticore-grammar/src/main/grammars/de/monticore/JavaLight.mc4) 

### Statements: List of Grammars in package `de.monticore.statements`

* [MCAssertStatements.mc4](monticore-grammar/src/main/grammars/de/monticore/statements/MCAssertStatements.mc4)
* [MCCommonStatements.mc4](monticore-grammar/src/main/grammars/de/monticore/statements/MCCommonStatements.mc4)
* [MCExceptionStatements.mc4](monticore-grammar/src/main/grammars/de/monticore/statements/MCExceptionStatements.mc4)
* [MCFullJavaStatements.mc4](monticore-grammar/src/main/grammars/de/monticore/statements/MCFullJavaStatements.mc4)
* [MCLowLevelStatements.mc4](monticore-grammar/src/main/grammars/de/monticore/statements/MCLowLevelStatements.mc4)
* [MCReturnStatements.mc4](monticore-grammar/src/main/grammars/de/monticore/statements/MCReturnStatements.mc4)
* [MCStatementsBasis.mc4](monticore-grammar/src/main/grammars/de/monticore/statements/MCStatementsBasis.mc4)
* [MCSynchronizedStatements.mc4](monticore-grammar/src/main/grammars/de/monticore/statements/MCSynchronizedStatements.mc4)


### Alpha: also to become stable (one phase later)

* [Cardinality.mc4](monticore-grammar/src/main/grammars/de/monticore/Cardinality.mc4)
* [Completeness.mc4](monticore-grammar/src/main/grammars/de/monticore/Completeness.mc4)
* [MCCommon.mc4](monticore-grammar/src/main/grammars/de/monticore/MCCommon.mc4)

### Examples for Grammars under `monticore-grammar/src/main/examples`

These can also be used if someone is interested:

* [StringLiterals.mc4](monticore-grammar/src/main/examples/StringLiterals.mc4)
* [MCHexNumbers.mc4](monticore-grammar/src/main/examples/MCHexNumbers.mc4)
* [MCNumbers.mc4](monticore-grammar/src/main/examples/MCNumbers.mc4)





