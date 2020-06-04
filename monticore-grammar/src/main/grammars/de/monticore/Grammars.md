<!-- (c) https://github.com/MontiCore/monticore -->

<!-- Alpha-version: This is intended to become a MontiCore stable explanation. -->

# MontiCore Core Grammars - an Overview

[MontiCore](http://www.monticore.de) is a language workbench. It uses 
grammars as primary mechanism to describe DSLs. The extended 
grammar format allows to **compose language components** by
(1) **inheriting**, (2) **extending**, (3) **embedding** 
and (4) **aggregating** grammars (see the reference manual for details).
From the grammars a lot of infrastructructure is generated, that is as well
**composable**, can be **extended with handwrittten code**
and most importandly, these
extensions and the grammar composition are compatible, which
leads to optimal forms of **reuse**.

The following is a list of language components, mainly defined through a 
primary grammar plus associated Java- and Template-Files, 
available in the *MontiCore* core project 
together with short descriptions and their status 
([Status of Grammars](../../../../../../00.org/Explanations/StatusOfGrammars.md)).

The list covers the core grammars to be found in the `MontiCore/monticore` 
project under `monticore-grammar/src/main/grammars/` in packages 

* `de.monticore`
* `de.monticore.expressions`
* `de.monticore.literals`
* `de.monticore.statements`
* `de.monticore.types`


## General: List of Grammars in package `de.monticore`

### [MCBasics.mc4](MCBasics.mc4)  (stable)
* This grammar defines absolute basics, such as spaces, 
Java-like comments and Names. 
It should be useful in many languages.
  

## Types: List of Grammars in package `de.monticore.types`

These grammars generally deal with type definitions and build on each 
other:

### [MCBasicTypes.mc4](types/MCBasicTypes.mc4) (stable)
* This grammar defines basic types. This eases the reuse of type 
structures in languages similar to Java, that are somewhat 
simplified, e.g. without generics.
* The grammar contains types from Java, e.g., primitives, void, 
classes (also sometimes called "reference types").
 
### [MCCollectionTypes.mc4](types/MCCollectionTypes.mc4) (stable)
* This grammar defines four generics: `List<A>`, `Map<A,B>`, `Set<A>` and 
`Optional<A>` on top of basic types.
* These four generics correspond to a typical predefined set of generic 
types for example used in connection with UML class diagrams or the
OCL. UML associations typically have those association multiplicities and 
therefore these types are of interest.
* This eases the reuse of type structures in languages similar to Java,
that are somewhat simplified, e.g. without general generics.


### [MCSimpleGenericTypes.mc4](types/MCSimpleGenericTypes.mc4) (stable)
* This grammar introduces freely defined generic types
such as `Blubb<A>`, `Bla<B,C>`, `Foo<Blubb<D>>`
* These generics are covering a wide range of uses for generic types,
although they don't cover type restrictions on the arguments, like in 
Java. 


### [MCFullGenericTypes.mc4](types/MCFullGenericTypes.mc4) (Beta: In Stabilization)
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


### [ExpressionsBasis.mc4](expressions/ExpressionsBasis.mc4) (stable)
* This grammar defines core interfaces for expressions and imports the 
kinds of symbols necessary.
* The symbols are taken over from the TypeSymbols grammar (see below).
* A hierarchy of conservative extensions to this grammar realize
these interfaces in various forms.


### [CommonExpressions.mc4](expressions/CommonExpressions.mc4) (stable)
* This grammar defines a typical standard set of operations for
expressions. 
* This is a subset of Java as well as OCL/P, 
mainly for arithmetic, comparisons, variable use (v), 
attribute use (o.att), method call (foo(arg,arg2)) and brackets (exp).


### [BitExpressions.mc4](expressions/BitExpressions.mc4) (stable)
* This grammar defines a typical standard set of operations for
expressions. 
* This is a subset of Java for binary expressions 
like <<, >>, >>>, &, ^ and |


### [AssignmentExpressions.mc4](expressions/AssignmentExpressions.mc4) (stable)
* This grammar defines all Java expressions that have side effects.
* This includes assignment expressions like =, +=, etc. and 
suffix and prefix expressions like ++, --, etc.


### [JavaClassExpressions.mc4](expressions/JavaClassExpressions.mc4) (stable)
* This grammar defines Java specific class expressions like super, 
this, type cast, etc.
* This grammar should only be included, when a mapping to Java is
intended and the full power of Java should be available in the 
modelling language.


### [SetExpressions.mc4](expressions/SetExpressions.mc4) (Beta: In Stabilization)
* This grammar defines set expressions like {..|..}, set union, intersection etc.
these operations are typical for a logic with set operations, like 
UML's OCL.


### [OCLExpressions.mc4](expressions/OCLExpressions.mc4) (Alpha: Needs restructuring)
* This grammar defines a expressions typical to UMLs OCL .
* This grammar will be restructured. Especially the non expression 
  part of the OCL will be separated.



## Literals: List of Grammars in package `de.monticore.literals`

Literals are the basic elements of expressions, such as numbers, strings, 
truth values:

### [MCLiteralsBasis.mc4](literals/MCLiteralsBasis.mc4) (stable)
* This grammar defines core interface for literals.
* Several conservative extensions to this grammar realize
various forms of literals.

### [MCCommonLiterals.mc4](literals/MCCommonLiterals.mc4) (stable)
* This grammar defines the typical literals for an expression language, such as 
  characters: 'c', Strings "text", booleans: "true", "null", or numbers 10, 
  -23, 48l, 23.1f.
* Strings and characters use the Java-like escapes like "\n".
* Each defined nonterminal is extended by a conversion function `getValue()`
  of appropriate type and a retrieve function `getSource()` for a text representation
  of the literal.

### [MCJavaLiterals.mc4](literals/MCJavaLiterals.mc4) (Beta: In Stabilization)
* This grammar defines Java compliant literals and builds on MCCommonLiterals.
* The scope of this grammar is to
  ease the reuse of literals structures in Java-like sublanguages.
* The grammar contains literals from Java, e.g., Boolean, Char, String, ....
* Please note that Java (and this grammar) 
  has an extended syntax e.g. for integers using underscores
  or other kinds of encodings. They parse e.g. 999_999, 0x3F2A, or 0b10100.
* Like above `getValue()` and `getSource()` allow to retrive the content
  as value resp. as text string.



## Statements: List of Grammars in package `de.monticore.statements`

Statements are the constructive part of programs: They allow to 
change variables, call functions, send messages etc.
The following hierarchy of statement definitions should allow
the developers to choose needed forms of statements and extend it 
by their own additional needs. The provided list of statements
is inspired by Java (actually subset of Java):

### [MCStatementsBasis.mc4](statements/MCStatementsBasis.mc4) (stable)
* This grammar defines the core interface for statements.
* A hierarchy of conservative extensions to this grammar is provided below.

### [MCCommonStatements.mc4](statements/MCCommonStatements.mc4) (Beta: In Stabilization)
* This grammar defines typical statements, such as method calls
  (which are actually expressions),
  assignment of variables, if, for, while, switch statements, and blocks.
* This embodies a complete structured statement language, however does not
  provide return, assert, exceptions, and low-level constructs like break.

### [MCAssertStatements.mc4](statements/MCAssertStatements.mc4) (stable)
* This grammar defines exactly the assert statement as known from Java.
* It can be used independently of other Java statements.

### [MCExceptionStatements.mc4](statements/MCExceptionStatements.mc4) (Beta: In Stabilization)
* This grammar defines the exception statements.
* This includes Java try with catch and finally, as well as throw.

### [MCSynchronizedStatements.mc4](statements/MCSynchronizedStatements.mc4) (stable)
* This grammar defines the Java-like synchronized statement.

### [MCLowLevelStatements.mc4](statements/MCLowLevelStatements.mc4) (Beta: In Stabilization)
* This grammar defines three low-level statements that Java provides.
* It contains the break and continue statements and the possibility to label a statement.

### [MCReturnStatements.mc4](statements/MCReturnStatements.mc4) (stable)
* This grammar defines the Java-like return statement.

### [MCFullJavaStatements.mc4](statements/MCFullJavaStatements.mc4) (stable)
* This grammar defines all Java statements.
* This is neither a generalized approximation nor a restricted overapproximation,
  but exact.




## Further grammars in package `de.monticore`

several smaller grammars are also available:

### [Cardinality.mc4](Cardinality.mc4) (stable)
* This grammar defines UML Cardinalities of forms "*", "[n..m]" "[n..*]".

### [Completeness.mc4](Completeness.mc4) (stable)
* This grammar defines completeness information in UML
  like "...", "(c)", but also "(...,c)".

### [UMLModifier.mc4](UMLModifier.mc4) (stable)
* The grammar contains the modifiers that UML provides.
* This includes "public" "private", "protected", "final", "abstract", "local",
          "derived", "readonly", and "static", but also the 
          compact syntactic versions "+", "#", "-", "/" and "?" (for readonly).
* UML modifiers are not identical to Java modifiers (e.g. "native" or 
  "threadsafe" are missing.)

### [UMLStereotype.mc4](UMLStereotype.mc4) (stable)
* This grammars defines Stereotypes like *<<val1,val2="text",...>>*
* Methods contains(name), getValue(name) assist Stereotype retrieval.
* Values may only be of type String.
  The real value unfortunately in UML is only encoded as String.
* We suggest to use a tagging infrastructure that even allows to
  type the possible forms of tags.

### [MCCommon.mc4](MCCommon.mc4) (stable)
 * This grammar composes typical UML like grammar components.
 * This includes Cardinality, Completeness, UMLModifier, and UMLStereotype.


### [JavaLight.mc4](JavaLight.mc4) (Beta: In Stabilization)
* JavaLight is a subset of Java that MontiCore itself
  uses as intermediate language for the code generation process.
* JavaLight doesn't provide all forms of classes (e.g. inner classes)
  and reduces the type system to normal generic types.  
  However, that is suffiecient for representation of all generated
  pieces of code that MontiCore wants to make.
* JavaLight can be used for other generator tools as well,
  especially as core template are reusable and new templates
  for specific method bodies can be added using MontiCore's
  Hook-Mechanisms.


## Examples for Grammars under `monticore-grammar/src/main/examples`

These can also be used if someone is interested:

* [StringLiterals.mc4](../../../examples/StringLiterals.mc4)
* [MCHexNumbers.mc4](../../../examples/MCHexNumbers.mc4)
* [MCNumbers.mc4](../../../examples/MCNumbers.mc4)


## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [**List of languages**](docs/Languages.md).
* [MontiCore documentation](http://www.monticore.de/)


