<!-- (c) https://github.com/MontiCore/monticore -->

<!-- This is a MontiCore stable explanation. -->

# MontiCore Core Grammars - an Overview

[[_TOC_]]

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

The following is a library of language components
that the core MontiCore project provides, mainly defined through a 
primary grammar plus associated Java- and Template-Files. 
These are available in the *MontiCore* core project 
together with short descriptions and their status 
([Status of Grammars](../../../../../../00.org/Explanations/StatusOfGrammars.md)).

The list covers mainly the core grammars to be found in the `MontiCore/monticore` 
project under `monticore-grammar/src/main/grammars/` in packages 

* `de.monticore`
* `de.monticore.expressions`
* `de.monticore.literals`
* `de.monticore.statements`
* `de.monticore.symbols`
* `de.monticore.types`

For [more langauges and language components, see here](../../../../../../docs/Languages.md).

## General: List of Grammars in package `de.monticore`

### [MCBasics.mc4](MCBasics.mc4)  (stable)
* This grammar defines absolute basics, such as spaces, 
Java-like comments and Names. 
It should be useful in many languages.
  

## Types: List of Grammars in package `de.monticore.types`

These grammars generally deal with type definitions and build on each 
other. Some snipets for type definitions:

    grammars          some examples
    MCBasicTypes      boolean  byte  short  int
                      long  char  float  double
                      void  Person  a.b.Person
                      import a.b.Foo.*;
    MCCollectionTypes List<.>   Set<.>
                      Optional<.>   Map<.,.>
    MCSimpleGenericTypes
                      Foo<.>  a.b.Bar<.,..,.>
    MCFullGenericTypes
                      Foo<? extends .>
                      Foo<? super .>
    MCArrayTypes      Person[]  int[][]
    SI Unit types     km/h  km/h<long>
  
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


### [MCFullGenericTypes.mc4](types/MCFullGenericTypes.mc4) (stable)
* This grammar completes the type definitions to 
support the full Java type system including wildcards Blubb<? extends A>
* A general advice: When you are not sure that you need this kind of
types, then use a simpler version from above. Type checking ist tricky.


### [MCArrayTypes.mc4](types/MCArrayTypes.mc4) (stable)

Arrays are orthogonal to the generic extensions and
thus be combined with any of the above variants.
Language component MCArrayTypes provides
possibilities to add arrays, such as `Person[]` or `int[][]`.

### [SIUnitTypes4Math.mc4](https://git.rwth-aachen.de/monticore/languages/siunits) for Physical SI Units (stable)

The known units `s, m, kg, A, K, mol, cd` from the international system of 
units (SI Units) and  their kombinations, such as `km/h` or `mg`, etc. can 
be used as ordinary types (instead of only numbers without concrete). 
The typecheck is extended to prevent e.g. assignment of a weight to a length 
variable, or to add appropriate conversion, when a `km/h`-based velocity is 
e.g. stored in a `m/s`-based variable.

The grammar resides in the [MontiCore/SIunits](https://github.com/MontiCore/siunits/blob/master/src/main/grammars/de/monticore/SIUnits.md) project.

### [SIUnitTypes4Computing.mc4.mc4](https://git.rwth-aachen.de/monticore/languages/siunits) for Physical SI Units (stable)

Includes the types from `SIUnitTypes4Math`(see above), like `km/h`, but also allows to add a
resolution, such as `km/h<int>`. Here SI Unit types, 
like `km/h<.>`, are used as generic type constructor that may take a number type,
such as `int`, `long`, `double`, `float` as argument.

The grammar resides in the [MontiCore/SIunits](https://github.com/MontiCore/siunits/blob/master/src/main/grammars/de/monticore/SIUnits.md) project.



## Symbols: List of Grammars in package `de.monticore.symbols`

These two grammars do not provide syntax themselves, but 
characterize important forms of symbols, that will be used
in the type and the expression grammars to define shared 
kinds of symbols. 

### [BasicSymbols.mc4](symbols/BasicSymbols.mc4) (stable)
* This grammar defines symbols for *Types* (of all kinds), *Functions*, 
  *Variables* and *TypeVariables*.
* The defined symbols are of general form and can be used in functional, OO
  and other contexts. They do not preculde a concrete syntax and do not yet 
  embody OO specifics.
* Remark: This grammar is not intended to define concrete or abstract syntax, but the
  infrastructure for symbols. 

### [OOSymbols.mc4](symbols/OOSymbols.mc4) (stable)
* This grammar defines symbols for *objectoriented Types*, *Methods*, and
  *Fields* by mainly extending the symbols defined in `BasicTypeSymbols`.
* The newly defined symbols extend the general ones by typical 
  objectoriented features, such as private, static, etc.
  Again they do not preculde a concrete syntax.
* Remark: This grammar is not intended to define concrete or abstract syntax, but the
  infrastructure for symbols in objectoriented context. 



## Expressions: List of Grammars in package `de.monticore.expressions`

Expressions are defined in several grammars forming a (nonlinear) hierarchy,
so that developers can choose the optimal grammar they want to build on 
for their language and combine these with the appropriate typing 
infrastructure.

This modularity of expressions and associated types greatly eases 
the reuse of type structures in languages similar to Java.
Some snipets for operators defined in expressions:

    grammar        operators and examples in this grammar
    CommonExp:     /  %  +  -  <=  >=  ==  >  <  !=  ~.  !.  .?.:.
                   &&  ||  ~. 
    AssigementExp: ++  --  =  +=  -=  *=  /=  &=  |=  ^=  >>=  >>>=  <<=  %=
    BitExp:        &  |  ^  <<  >>  >>>
    OclExp:        implies  <=>  |  &  forall  exists  let.in. .@pre  .[.]  .**
                   Set{.|.}
    SetExp:        .isin.  .in.  union  intersect  setand  setor
                   { item | specifier }
    OptionalOps:   ?:  ?<=  ?>=  ?<  ?>  ?==  ?!=  ?~~   ?!~ 
    SIUnits:       5km  3,2m/s  22l  2.400J  
    JavaClass:     this  .[.]  (.).  super  .instanceof.


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


### [AssignmentExpressions.mc4](expressions/AssignmentExpressions.mc4) (stable)
* This grammar defines all Java expressions that have side effects.
* This includes assignment expressions like =, +=, etc. and 
suffix and prefix expressions like ++, --, etc.


### [BitExpressions.mc4](expressions/BitExpressions.mc4) (stable)
* This grammar defines a typical standard set of operations for
expressions. 
* This is a subset of Java for binary expressions 
like <<, >>, >>>, &, ^ and |


### [OCLExpressions.mc4][OCL-OCLExpressions] (stable)
* This grammar defines expressions typical to UMLs OCL .
  OCL expressions can savely be composed if with other forms of expressions  
  given in the MontiCore core project (i.e. as conservative extension).
* It contains various logical operations, such as quantifiers, 
  the `let` and the `@pre` construct, and a transitive closure for 
  associations, as discussed in [Rum17,Rum17].
* This grammar resides in the [MontiCore/OCL][OCL] project.

### [SetExpressions.mc4][OCL-SetExpressions] (stable)
* This grammar defines set expressions like set union, intersection etc.
these operations are typical for a logic with set operations, like 
UML's OCL. These operators are usually infix and are thus more intuitive
as they allow math oriented style of specification.
* Most of these operators are in principle executable, so it might be interesting to include them in a high level programming language (see e.g. Haskell)
* This grammar resides in the [MontiCore/OCL][OCL] project.


### [OptionalOperators.mc4][OCL-OptionalOperators] (stable)
* This grammar defines nine operators dealing with optional values, e.g. defined by 
  `java.lang.Optional`. The operators are also called *Elvis operators*.
* E.g.: `val ?: 0W`     equals to   `val.isPresent ? val.get : 0W`
* `x ?>= y` equals `x.isPresent && x.get >= y` 
* This grammar resides in the [MontiCore/OCL][OCL] project.


### [SIUnits.mc4](https://git.rwth-aachen.de/monticore/languages/siunits) for Physical SI Units (stable)
* This grammar the international system of units (SI units), based on 
  the basis units `s, m, kg, A, K, mol, cd`, 
  provides a variety of derived units, and can be refined using prefixes such 
  as `m`(milli), `k`(kilo), etc.
* The SI Unit grammar provides an extension to expressions, but also to the 
  typing system, e.g. types such as `km/h` or `km/h<long>`,
  and literals, such as e.g. `5.3 km/h`.
* The grammars reside in the [MontiCore/SIunits](https://github.com/MontiCore/siunits/blob/master/src/main/grammars/de/monticore/SIUnits.md) project


### [JavaClassExpressions.mc4](expressions/JavaClassExpressions.mc4) (stable)
* This grammar defines Java specific class expressions like super, 
this, type cast, etc.
* This grammar should only be included, when a mapping to Java is
intended and the full power of Java should be available in the 
modelling language.



## Literals: List of Grammars in package `de.monticore.literals`

Literals are the basic elements of expressions, such as numbers, strings, 
truth values. Some snipets:

    grammar           examples of this grammar
    MCCommonLit       3  -3  2.17  -4  true  false  'c' 
                      3L  2.17d  2.17f  0xAF  "string"  
                      "str\uAF01\u0001"  null
    MCJavaLiterals    999_999  0x3F2A  0b0001_0101  0567  1.2e-7F

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

### [MCJavaLiterals.mc4](literals/MCJavaLiterals.mc4) (stable)
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
is inspired by Java (actually subset of Java). Some example statements:

    int i;   int j = 2;                     Person p[] = { foo(3+7), p2, ...}
    if (.) then . else .                    for ( i = .; .; .) {.}
    while (.) .                             do . while (.)
    switch (.) { case .: .; default: .}
    foo(1,2,3)                              return .                                
    assert . : "..."
    try {.} catch (.) {.} finally {.}       throw .           
    break .                                 continue .
    label:                                  private  static  final  native ...

### [MCStatementsBasis.mc4](statements/MCStatementsBasis.mc4) (stable)
* This grammar defines the core interface for statements.
* A hierarchy of conservative extensions to this grammar is provided below.

### [MCCommonStatements.mc4](statements/MCCommonStatements.mc4) (stable)
* This grammar defines typical statements, such as method calls
  (which are actually expressions),
  assignment of variables, if, for, while, switch statements, and blocks.
* This embodies a complete structured statement language, however does not
  provide return, assert, exceptions, and low-level constructs like break.

### [MCAssertStatements.mc4](statements/MCAssertStatements.mc4) (stable)
* This grammar defines exactly the assert statement as known from Java.
* It can be used independently of other Java statements.

### [MCExceptionStatements.mc4](statements/MCExceptionStatements.mc4) (stable)
* This grammar defines the exception statements.
* This includes Java try with catch and finally, as well as throw.

### [MCSynchronizedStatements.mc4](statements/MCSynchronizedStatements.mc4) (stable)
* This grammar defines the Java-like synchronized statement.

### [MCLowLevelStatements.mc4](statements/MCLowLevelStatements.mc4) (stable)
* This grammar defines three low-level statements that Java provides.
* It contains the break and continue statements and the possibility to label a statement.

### [MCReturnStatements.mc4](statements/MCReturnStatements.mc4) (stable)
* This grammar defines the Java-like return statement.

### [MCFullJavaStatements.mc4](statements/MCFullJavaStatements.mc4) (stable)
* This grammar defines all Java statements.
* This is neither a generalized approximation nor a restricted overapproximation,
  but exact.

## Further grammars in package `de.monticore`

several other grammars are also available:

### [RegularExpressions.mc4](https://git.rwth-aachen.de/monticore/languages/regex) (stable)
* This grammar defines regular expressions (RegEx) as used in Java (see e.g. `java.util.regex.Pattern`).
* It provides common regex tokens such as 
  * character classes, e.g., lowercase letters (`[a-z]`), the letters a, b, 
    and c (`[abc]`)
  * anchors, e.g., start of line (`^`), end of line (`$`), word boundary (`\b`),
  * quantifiers, e.g., zero or one (`?`), zero or more (`*`), exactly 3 (`{3}`),
  * RegEx also supports to capture groups and referencing these captured groups 
  in replacements.  
* For example, `^([01][0-9]|2[0-3]):[0-5][0-9]$` matches all valid timestamps in 
  `HH:MM` format.
* The main nonterminal `RegularExpression` is not part of the expression hierarchy and 
  thus regular expressions are not used as ordinary values. Instead 
  the nonterminal `RegularExpression` is can be used in aother places of a language
  e.g. we do that as additional 
  restriction for String values in input/output channels in architectural langages.
* This grammar resides in the [MontiCore/RegEx][RegEx] project

### [Cardinality.mc4](Cardinality.mc4) (stable)
* This grammar defines UML Cardinalities of forms ``*``, ``[n..m]`` or ``[n..*]``.

### [Completeness.mc4](Completeness.mc4) (stable)
* This grammar defines completeness information in UML
  like ``...``, ``(c)``, but also ``(...,c)``.

### [UMLModifier.mc4](UMLModifier.mc4) (stable)
* The grammar contains the modifiers that UML provides.
* This includes ``public`` ``private``, ``protected``, ``final``, ``abstract``, ``local``,
          ``derived``, ``readonly``, and ``static``, but also the 
          compact syntactic versions ``+``, ``#``, ``-``, ``/`` and ``?`` (for readonly).
* UML modifiers are not identical to Java modifiers (e.g. ``native`` or 
  ``threadsafe`` are missing.)

### [UMLStereotype.mc4](UMLStereotype.mc4) (stable)
* This grammars defines Stereotypes like *``<<val1,val2="text",...>>``*
* Methods contains(name), getValue(name) assist Stereotype retrieval.
* Values may only be of type String.
  The real value unfortunately in UML is only encoded as String.
* We suggest to use a tagging infrastructure that even allows to
  type the possible forms of tags.

### [MCCommon.mc4](MCCommon.mc4) (stable)
 * This grammar composes typical UML like grammar components.
 * This includes Cardinality, Completeness, UMLModifier, and UMLStereotype.


### [JavaLight.mc4](JavaLight.mc4) (stable)
* JavaLight is a subset of Java that MontiCore itself
  uses as intermediate language for the code generation process.
* JavaLight doesn't provide all forms of classes (e.g. inner classes)
  and reduces the type system to normal generic types.  
  However, that is sufficient for representation of all generated
  pieces of code that MontiCore wants to make.
* Included are: the full Java expressions (without anonymous classes),
  the relevant Java statements, declaration of methods, constructors,
  constants, interface methods, and annotations.
* JavaLight composes from CommonExpressions,
                                    AssignmentExpressions,
                                    JavaClassExpressions,
                                    MCCommonStatements,
                                    MCBasicTypes, and
                                    OOSymbols.
* JavaLight can be used for other generator tools as well,
  especially as its core templates are reusable and new templates
  for specific method bodies can be added using MontiCore's
  Hook-Mechanisms.


## Examples for Grammars under `monticore-grammar/src/main/examples`

These can also be used if someone is interested:

* [StringLiterals.mc4](../../../examples/StringLiterals.mc4)
* [MCHexNumbers.mc4](../../../examples/MCHexNumbers.mc4)
* [MCNumbers.mc4](../../../examples/MCNumbers.mc4)


## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](http://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/dev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/dev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)

<!-- Links to other sites-->
[RegEx]: https://git.rwth-aachen.de/monticore/languages/regex
[OCL]: https://git.rwth-aachen.de/monticore/languages/OCL
[OCL-OCLExpressions]: https://git.rwth-aachen.de/monticore/languages/OCL/-/blob/develop/src/main/grammars/de/monticore/ocl/OCLExpressions.mc4
[OCL-OptionalOperators]: https://git.rwth-aachen.de/monticore/languages/OCL/-/blob/develop/src/main/grammars/de/monticore/ocl/OptionalOperators.mc4
[OCL-SetExpressions]: https://git.rwth-aachen.de/monticore/languages/OCL/-/blob/develop/src/main/grammars/de/monticore/ocl/SetExpressions.mc4
