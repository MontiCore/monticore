<!-- (c) https://github.com/MontiCore/monticore -->

<!-- This is a MontiCore stable explanation. -->

# MontiCore Grammars for Expressions, Literals and Types - an Overview

[[_TOC_]]

[MontiCore](https://www.monticore.de) is a language workbench. It uses an
extended grammar format as primary mechanism to describe DSLs. This format
allows to **compose language components** by grammar
(1) **inheritance**, (2) **extension**, (3) **embedding**
and (4) **aggregating**. Please refer to the
[MontiCore handbook](https://www.monticore.de/handbook.pdf) for more details.

MontiCore bundles a **generator** that can produce lots of infrastructure from
MontiCore grammars. Like grammars, this infrastructure is **composable** and
can be **extended with handwritten code**. Most importantly, these
extensions and the grammar composition are compatible which
leads to flexible forms of **reuse**.

This documentation presents a library of language components provided by the
core of the MontiCore project together with short descriptions and status
information
([Status of Grammars](../../../../../../00.org/Explanations/StatusOfGrammars.md)).
The components are mainly defined by a primary grammar plus associated Java
and template files.

The presented components are mainly based on the grammars in the
`MontiCore/monticore` project. They are organized in the following
packages under the `monticore-grammar/src/main/grammars/` folder hierarchy:

* `de.monticore`
* `de.monticore.expressions`
* `de.monticore.literals`
* `de.monticore.regex`
* `de.monticore.siunit`
* `de.monticore.statements`
* `de.monticore.symbols`
* `de.monticore.types`

Additionally, the documentation presents some expression/type related language
components in projects that extend MontiCore's core languages.
For more languages and language components, follow
[this link](../../../../../../docs/Languages.md).

## General: List of Grammars in package `de.monticore`

### [MCBasics.mc4](MCBasics.mc4)  (stable)
* This grammar defines basic rules for naming, spacing, and
Java-like comments being useful in many languages.
  
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
    MCFunctionTypes   Foo->Bar  (Foo, Bar)->Foo
    MCStructuralTypes Foo|Bar  Foo&Bar
                      (Foo, Bar)  (Foo)
    SI Unit types     [km/h]  [km/h]<long>
    RegExType         R"[a-z][0-9*]"
  

### [MCBasicTypes.mc4](types/MCBasicTypes.mc4) (stable)
* This grammar defines basic types and thus eases the reuse of type
structures in languages similar to Java. However, the type definitions in the
grammar are somewhat simplified, e.g., they do not comprise generics.
* The grammar contains types from Java, e.g., primitives, `void`, and
classes (sometimes referred to as "reference types").
* Example type definitions: `boolean`, `byte`, `short`, `int`, `long`, `char`,
`float`, `double`, `void`, `Person`, `a.b.Person`, `import a.b.Foo.*;`.

### [MCCollectionTypes.mc4](types/MCCollectionTypes.mc4) (stable)
* This grammar defines four generic types: `List<A>`, `Map<A,B>`, `Set<A>` and
`Optional<A>` on top of basic types.
* These four generic types correspond to a typical predefined set of generic
types used, e.g., in connection with UML class diagrams or the
OCL. UML associations typically have those association multiplicities which is
why the generic types are of general interest.
* The grammar eases the reuse of type structures in languages similar to Java
that are somewhat simplified, e.g., by omitting general generics.
* Example type definitions: `List<.>`, `Set<.>`, `Optional<.>`, `Map<.,.>`.

### [MCSimpleGenericTypes.mc4](types/MCSimpleGenericTypes.mc4) (stable)
* This grammar provides rules for the definition of custom generic types
such as `Blubb<A>`, `Bla<B,C>`, `Foo<Blubb<D>>`.
* The grammar covers a wide range of uses for generic types. Unlike Java, it
does however not cover type restrictions on arguments.
* Example type definitions: `Foo<.>`, `a.b.Bar<.,..,.>`.

### [MCFullGenericTypes.mc4](types/MCFullGenericTypes.mc4) (stable)
* This grammar completes type definitions to support the full Java type system
including wildcards on generic types like `Blubb<? extends A>`.
* A general advice: When you are not sure that you need this kind of
types, use a simpler version from above. Type checking is tricky.
* Example type definitions: `Foo<? extends .>`, `Foo<? super .>`.


### [MCArrayTypes.mc4](types/MCArrayTypes.mc4) (stable)

* The grammar provides means to define arrays.
* Arrays are orthogonal to the generic extensions and may
thus be combined with any of the above grammars.
* Example type definitions: `Person[]`, `int[][]`.

### [MCFunctionTypes.mc4](types/MCFunctionTypes.mc4) (stable)

* This grammar introduces function types like `int -> int`.
* They are useful when functions shall be passed around
  as arguments or stored in a data structure (for later use).
* The syntax is inspired by the functional programming language Haskell.
* This grammar may be combined with any of the grammars above.

### [MCStructuralTypes.mc4](types/MCStructuralTypes.mc4) (stable)

* The grammar introduces additional structural types
  as known partially from modern programming languages, like Kotlin.
* The grammar offers three ways to combine types: union types `A|B`,
  intersection types `A&B`, and tuple types `(A,B)`.
* Additionally, bracket types `(A)` are semantically 
  isomorphic to their argument (here `A`).
  They allow to represent types not representable otherwise, e.g., `(A|B) -> C`.
* They may be combined with any of the grammars above.

### [SIUnitTypes4Math.mc4](siunit/SIUnitTypes4Math.mc4) for Physical SI Units (stable)

The known units `s, m, kg, A, K, mol, cd` from the international system of 
units (SI Units) and their combinations, such as `km/h` or `mg`, etc. can 
be used as ordinary types (instead of only numbers). 
The typecheck is extended to prevent, e.g., assignment of a weight to a length 
variable or to add appropriate conversion, e.g., when a `km/h`-based velocity is, 
e.g., stored in a `m/s`-based variable.

* Example type definitions: `[km/h]`

### [SIUnitTypes4Computing.mc4](siunit/SIUnitTypes4Computing.mc4) for Physical SI Units (stable)

Includes the types from `SIUnitTypes4Math`(see above), like `[km/h]`, but also allows to add a
resolution, such as `[km/h]<int>`. Here SI Unit types, 
like `[km/h]<.>`, are used as generic type constructor that may take a number type,
such as `int`, `long`, `double`, `float` as argument.

* Example type definitions: `[km/h]<long>`

### [RegExType.mc4](regex/RegExType.mc4) (stable)

Embedded in `R"..."` a regular expressions
can be used as ordinary type to constrain the values allowed for stored variables, attributes, 
parameters. Types are e.g. , such as `R"[a-z]"` (single character) or `R"^([01][0-9]|2[0-3])$"` (hours).
A typecheck for these types can only be executed at runtime and e.g. issue
exceptions (or trigger repair functions) if violated. The static typecheck only uses `String` as 
underlying carrier type.


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
    LambdaExp:     i->2*i   (a,b)->a+b
    TupleExp:      (.,.)  (.,.,.,.)
    SIUnits:       5km  3,2m/s  22l  2.400J  
    UglyExp:       .instanceof.  (.).  new
    JavaClass:     this  .[.]  super


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
attribute use (o.att), method call (foo(arg,arg2)),
array access (v[i]), and brackets (exp).


### [AssignmentExpressions.mc4](expressions/AssignmentExpressions.mc4) (stable)
* This grammar defines all Java expressions that have side effects.
* This includes assignment expressions like =, +=, etc. and 
suffix and prefix expressions like ++, --, etc.


### [BitExpressions.mc4](expressions/BitExpressions.mc4) (stable)
* This grammar defines a typical standard set of operations for
expressions. 
* This is a subset of Java for binary expressions 
like <<, >>, >>>, &, ^ and |


### [OCLExpressions.mc4](ocl/OCLExpressions.mc4) (stable)
* This grammar defines expressions typical to UMLs OCL .
  OCL expressions can savely be composed if with other forms of expressions  
  given in the MontiCore core project (i.e. as conservative extension).
* It contains various logical operations, such as quantifiers, 
  the `let` and the `@pre` construct, and a transitive closure for 
  associations, as discussed in [Rum17,Rum17].

### [SetExpressions.mc4](ocl/SetExpressions.mc4) (stable)
* This grammar defines set expressions like set union, intersection etc.
these operations are typical for a logic with set operations, like 
UML's OCL. These operators are usually infix and are thus more intuitive
as they allow math oriented style of specification.
* Most of these operators are in principle executable, so it might be interesting to include them in a high level programming language (see e.g. Haskell)


### [OptionalOperators.mc4](ocl/OptionalOperators.mc4) (stable)
* This grammar defines nine operators dealing with optional values, e.g. defined by 
  `java.lang.Optional`. The operators are also called *Elvis operators*.
* E.g.: `val ?: 42`     equals to   `val.isPresent() ? val.get() : 42`
* `x ?>= y` equals `x.isPresent() ? x.get() >= y : false` 


### [LambdaExpressions.mc4](expressions/LambdaExpressions.mc4) (stable)
* This grammar defines lambda expressions.
  Lambda expression define anonymous functions that can be passed around 
  as values e.g. to higher-order functions 
  and that can be evaluated on arguments of appropriate types.
* Lambda expressions are fully typed (see e.g. in Haskell) and can be nested.
* This is only the subset of Java's lambda expressions that allows to define 
  a functional programming style (thus preventing side effects).

### [TupleExpressions.mc4](expressions/TupleExpressions.mc4) (stable)
* This grammar defines tuple expressions like `(1, "Hello")`.
* Tuple Expressions are tuples of expressions that can be used to, e.g.,
  return multiple results from a function.
* The syntax is inspired by the functional programming language Haskell.

### [SIUnits.mc4](https://git.rwth-aachen.de/monticore/languages/siunits) for Physical SI Units (stable)
* This grammar the international system of units (SI units), based on 
  the basis units `s, m, kg, A, K, mol, cd`, 
  provides a variety of derived units, and can be refined using prefixes such 
  as `m`(milli), `k`(kilo), etc.
* The SI Unit grammar provides an extension to expressions, but also to the 
  typing system, e.g. types such as `km/h` or `km/h<long>`,
  and literals, such as e.g. `5.3 km/h`.


### [JavaClassExpressions.mc4](expressions/JavaClassExpressions.mc4) (stable)
* This grammar defines Java specific class expressions like super, 
this, etc.
* This grammar should only be included, when a mapping to Java is
intended and the full power of Java should be available in the 
modeling language.

### [UglyExpressions.mc4](expressions/UglyExpressions.mc4) (stable)
* This grammar defines expressions deemed 'ugly' like
  'instanceof', type cast, 'new', because they have some reflective behavior
  that is convenient when developing, but not so easy to test.
  'new' e.g. should be avoided, because in tests one might use
  a mock (from a subclass) instead.
* The ugly expressions are not included in the usual logic and other
  expressions, and only there to complete the Java expression syntax.

## Literals: List of Grammars in package `de.monticore.literals`

Literals are the basic elements of expressions, such as numbers, strings, 
truth values. Some snipets:

    grammar           examples of this grammar
    MCCommonLit       3  -3  2.17  -4  true  false  'c' 
                      3L  2.17d  2.17f  0xAF  "string"  
                      "str\uAF01\u0001"  null
    MCJavaLiterals    999_999  0x3F2A  0b0001_0101  0567  1.2e-7F
    SIUnitLiterals    5.3km/h  7mg

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

### [SIUnitLiterals.mc4](siunit/SIUnitLiterals.mc4) for Physical SI Units (stable)

Provides concrete values, such as `5.3 km/h`or `7 mg` for the international system of 
units (SI Units). 


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

### [RegularExpressions.mc4](regex/RegularExpressions.mc4) (stable)
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
 * This grammar composes typical UML-like grammar components.
 * This includes Cardinality, Completeness, UMLModifier, and UMLStereotype.


### [JavaLight.mc4](JavaLight.mc4) (stable)

```
int age = 3+x; 
List<Person> myParents;

@Override
public int print(String name, Set<Person> p) {
  int a = 2 + name.length();
  if(a < p.size()) {
    System.out.println("Hello " + name);
  }
  return a;
}
```

* JavaLight is a subset of Java that MontiCore itself
  uses as intermediate language for the code generation process.
* JavaLight doesn't provide all forms of classes (e.g., inner classes)
  and reduces the type system to normal generic types.  
  However, that is sufficient for all code generated by MontiCore.
* JavaLight supports Java expressions (including anonymous classes),
  Java statements as relevant to MontiCore code generation, method declaration,
  constructors, constants, interface methods, and annotations.
* JavaLight composes the grammars `CommonExpressions`,
                                    `AssignmentExpressions`,
                                    `JavaClassExpressions`,
                                    `MCCommonStatements`,
                                    `MCBasicTypes`, and
                                    `OOSymbols`.
* JavaLight can be used for other generator tools than MontiCore,
  especially as its core templates are reusable and new templates
  for customized method bodies can be added using MontiCore's
  Hook-Mechanisms.


## MontiCore Example Grammars for the Interested Reader

The  `monticore-grammar/src/main/examples` folder hosts the following example
grammars:

* [StringLiterals.mc4](../../../examples/StringLiterals.mc4)
* [MCHexNumbers.mc4](../../../examples/MCHexNumbers.mc4)
* [MCNumbers.mc4](../../../examples/MCNumbers.mc4)


## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](https://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/opendev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/opendev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/opendev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)
