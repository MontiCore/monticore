<!-- (c) https://github.com/MontiCore/monticore -->

# MontiCore Best Practices - A Guide For Small Solutions

[[_TOC_]]

[MontiCore](http://www.monticore.de) provides a number of options to design 
languages, access and modify the abstract syntax tree, and produce output files.

This (currently unsorted and evolving) list of practices discusses solutions 
that we identified and applied as well as alternatives and their specfic 
advantages and drawbacks. The list also mentions where the solutions have been
found and where they have been applied first.

This file is partially temporary and also contains compact (incomplete) solutions.
More detailed descriptions of best practices can be found in the 
[MontiCore reference manual](http://monticore.de/MontiCore_Reference-Manual.2017.pdf).
Some of the best practices here will also be incorporated in the next version
of the reference manual.

## **Designing A Language**

### Correct language vs. superset?
* When you know that the incoming model will be correct, because they are generated
  by algorithm, you can decide to pass a (slight) superset 
* This may simplify the development process for two reasons: 
  (a) you may derive a simpler grammar and (b) you may omit definition of 
  context conditions.
* But beware: (a) situations may change and manually changed models might come in
  or (b) the is adapted by an ill-behaving pre-processor or (c) the model
  may come in a wrong version.
* This applies mainly for unreadable languages, such as JSON or XML.
* Defined by: BR

### Versioning an evolving langauge?
* When languages evolve, models may become invalid, because 
  certain (now obligatory) parts are missing, or old keywords are used.
* We generally believe that a language that is made for long lasting 
  models should not embody its version in the models (i.e. like Java, C++ and 
  other GPLs and unlike XML dialects).
* When evolving a language, you should only evolve it in conservative form, i.e.
  * All new elements are optional by `.?`, `.*` or offer new alternatives `(old | new)`
  * Old elements or keywords are not simply removed, but 
    forbidden by coco warnings, marking them as deprecated for a while. 
* Downward compatibility of newer models, however, is not useful. 
  We can savely enforce developers should normally use the newest 
  versions of their tools.
* Defined by: BR

## **Designing Concrete and Abstract Syntax**

### **Specific keywords** that shall be used as normal words elsewhere
* `A = "foo" B` introduces `foo` as a keyword that cannot be used as an ordinary 
  (variable) name anymore. To prevent that we may use:
* `A = key("foo") B` instead, which introduces `foo` only at that specific point.
* In general, we use all Java keywords as permanent, but abstain from other
  permanent keywords, especially if they are only used for a specific purpose in a composable
  sublanguage, like `in` in the OCL.
* Defined by: BR


### **Extension** forms in a  component grammar
A component grammar is ment for extension. MontiCore therefore provides five(!) 
  mechanisms that can be used when a sub-grammer shall extend a super-grammar.
  The solutions are briefly discussed here: 
1. Interface in the super-grammar
  * Introduce an interface and allow building of sub-nonterminals in sub-grammars.
  ```
  component grammar A {  
    interface X;
    N = "bla" X "blubb";
  }
  grammar B extends A {
    Y implements X = "specific" "thing"
  }
  ```
  * Advantage: Multiple extensions are possible at the same time.
            An NT `Y` can also implement multiple interfaces (like in Java). 
  * Disadvantage: the designer of `A` explicitly has to design the *hole* 
  (extension point) `X` and add it it into the production.
2. Overriding (empty) nonterminal from the super-grammar
  * Use a normal nonterminal `X` and override it in a sub-grammar.
  ```
  component grammar A {  
    X = "";
    N = "bla" X "blubb";
  }
  grammar B extends A {
    @Override
    X = "my" "thing";
  }
  ```
  * Advantage: *Default* implementation "" exists, no explicit filling needed.
  * Disadvantage: 
    1. The designer of `A` explicitly has to design the *hole* (extension point) `X` 
      and inject it into other places. 
    2. Only one overriding alternative possible (i.e. multiple overriding in 
       subgrammars are allowed, but only the most specific resides) .
3. Extending nonterminal from the super-grammar.
  * Use an empty normal nonterminal `X` and extend it in a sub-grammar.
  ```
  component grammar A {  
    X = ;
    N = "bla" X "blubb";
  }
  grammar B extends A {
    Y extends X = "this";
  }
  ```
  * Advantage: *Default* implementation "" exists, no explicit filling needed.
  * Disadvantage: 
       The designer of `A` explicitly has to design the *hole* (extension point) `X` 
       and inject it into other places. 
  * Care: Extension still allows the (empty) alternative `X`.
4. Using `external` nonterminals in the super-grammar.
  * Mark nonterminal `X` as external.
  ```
  component grammar A {  
    external X;
    N = "bla" X "blubb";
  }
  grammar B extends A {
    X = "your";
  }
  ```
  * Advantage: Explctely marks a nonterminal as *hole* (extension point) in the grammar.
        Please observe that interface terminals may or not may be meant to be
        extended in sub-grammars. `external` is clearer here.
  * Disadvantage: 
    1. Leads to more objects in the AST. Both classes `a.X` and `b.X` are 
       instantiated and `a.X` only links to `b.X`.
    2. Only one filling of the `hole` is possible.

5. Overriding the whole production.
  * If you don't want to add a hole at any possible place of extension:
  ```
  component grammar A {  
    N = "bla" "blubb";
  }
  grammar B extends A {
    @Override
    N = "bla" "my" "blubb" "now";
  }
  ```
  * Advantage: Compact definition. No "*framework thinking*" needed (no need
    to forecast all potential extension points)
  * Disadvantage: 
    1. The entire production is overriden (some redundancy). 
    2. Only one overriding alternative possible. 
* Combinations are possible. Dependend on the anticipated forms of 
  adaptatations option 1, 2, 3 and 5 are in use.
* Defined by: BR


### Avoid **empty nonterminals** (if body is known)

* From the two variants:
  ```
    A = "bla" B? C*;
    B = "B's body" ;
    C = "C's body" ;
  ```
  and
  ```
    A = "bla" B C;
    B = ("B's body")? ;
    C = ("C's body")* ;
  ```
  we generally prefer the first one, i.e. add multiplicities when 
  using a nonterminal. 
* This is a matter of taste, but useful to keep this consistent.
* Sometimes exceptions are useful.
* Defined by: SVa, BR


### Avoid **complex tokens** (1)

* The token definitions can only define regular expressions.
  Furthermore, the token parser (i.e. the lexer) does not consider backtracking.
* If combinations of characters may be split into several token sequences
  this leads to problems. E.g. in `3-2` and `(-2)` the `-` has different roles.
  Unfortunately these problems also occur when composing languages
  that make excessive use of (conflicting) token definitions.
* Solution: instead of defining a complex token like
  ```
    token NegativeNat = "-" Digits;
  ```
  we split the token and allow individual parsing into nonterminals:
  ```
    NegativeNat = negative:["-"] Digits {noSpace()}? 
  ```
  (where we assume `Digits` is a given token).
* As a workaround, we use the semantic predicate `{noSpace()}?` that ensures 
  that between the two last processed token there is no space inbetween. 
  If one of the token is optional we have to split the alternatives:
  ```
  SignedNatLiteral = 
          (negative:["-"]) Digits {noSpace()}? 
        |                  Digits              ;  
  ```
* Adding a handcoded function like `getValue()` via `astrule` or the
  TOP-mechanism allows to use `SignedNatLiteral` like a token.
* *Scannerless parsing* is a principle where the tokens are reduced to simple
  characters (or character classes, such as `[a-z]`). Scannerless parsing
  generally avoids this kinds of problems, but is way slower.
  This kind of solution tries to mediate between the two extremes benefitting 
  from both approaches.
* Defined by: MB, in: `MCCommonLiterals.mc4` and other literals grammars.


### Avoid **complex tokens** (2)

* Same general problem. In language composition conflicting token may lead to issues.  
* For example Java allows `42.` as a literal of type float. 
  UML allows to define cardinalities like `[42..44]`. Composition clashes.
* Solution: In a Java sublanguage we split the token:
  ```
  SignedBasicFloatLiteral =
     ... 
     | Digits "." {noSpace()}? ... ;
  ```
* This will ensure that `[42..44]` will be parsed like `[ 42 .. 44 ]` 
  in a language composition as well.
* It generally seems that overly complex composed tokens may lead to issues 
  especially if the language allows compact models. Suboptimal tokens may be e.g.
  `"[["` (vs. nested lists), or
  `"<-"` (vs. `3 < -2`).
* Defined by: MC team.


### How to define **keyword enumerations** 

* A finite set of kewyword-based alternatives can be defined in several forms:  
* Standard thre keywords act as alternative:
  ```
  N = (["public"] | ["protected"] | ["private"]) ;
  ```
  * Effects: 
    1. not extensible without overriding and repetition
    2. introduces boolean flags, where only one can be true at a time
* Use an enumeration nonterminal
  ```
  enumeration E = "public" | "protected" | "private" ;
  N = E ;
  ```
  * Effects: 
    1. not extensible 
* Use an interface and subclasses with almost empty body:
  ```
  interface E ;
  P1 implements E = "public"    ;
  P2 implements E = "protected" ;
  P3 implements E = "private"   ;
  N = E ;
  ```
  * Effects: 
    1. very extensible in various ways (even beyond mere keywords) 
    2. visitor can easily adress the keywords (i.e. by `visit(P1)` ...)
    3. Disadvantage: Clumsy notation and visitors are always needed.
* Defined by: SVa, BR.

## **Handling Errors 0x.....** 

### How to use **Expressions** 

* `Expression` is a predefined nonterminal in the MontiCore basic grammars. 
  Because of the infix notation of some operators and similar challenges,
  it is usually not possible to use a subset of the expressions only. 
  For example use of `ConditionalExpression` may lead to a parser generation 
  error (i.e. `0xA0129`).
* Solutions:
  1. Use nonterminal `Expression` and forbid all unwanted alternatives through 
     context conditions.
  2. Think of allowing more general expressions?
  3. If especially the syntax of `if . then . else .` shall be reused, 
     why not defining this in a new nonterminal and ignoring that the same
     syntactic constructs were already available in another production.
* Defined by: CKi, BR.
  
 

## **Designing Symbols, Scopes and SymbolTables** 

### How to define a Symbol Usage without a given Symbol Definition
  ```
  grammar E { 
    A = Name@S; 
    symbol S = Name; 
  }
  ```

* If you want to use a sepcial form of symbol that shall neither be defined 
  inside the grammar of a language, nor shall it be imported.
* We can define symbols of kind `S` in the grammar in a grammar rule that 
  is never reached by the parser from the start production.
  Through this, MontiCores generates:
  * symbol table infrastructure for handling `S` symbols
  * symbol table infrastructure for resolving these in `E` scopes, and 
  * integration of `S` symbols with the AST of `A`.
* However, `S` symbols not automatically instantiated. 
  This has to be described manually, e.g., by extending the symbol table 
  creator or via providing an adapter translating a foreign symbol into an `S` symbol.
* This can be used, e.g., in these scenarios: 
  * A name of a certain kind is introduced automatically the first time it is occurs 
    in a model. If it occurs more than once, all other occurences of the name 
    do not introduce new symbols. (e.g. this happens with features in FDs,
    and works because features do not have a body.)
  * A name in a language `E` refers to a named element of another language, 
    but the language shall be decoupled from `E`. 
    Therefore, `E` introduces a symbol `S` and an adapter maps other symbols
    to `S` symbols.
* Defined by: AB, BR


## **Generating Code with Templates** 



## **Language Design in the Large**


### Making Transitively Inherited Grammars Explicit?
* When the grammar inclusion hierachy becomes larger, there will be redundancy.
  In:
  ```
    grammar A { .. } ;
    grammar B extends A { .. } ;
    grammar C extends A,B { .. } ;
    grammar D extends B { .. } ;
  ```
  Grammars `C` and `D` actually include the same nonterminals.
* If `A` is made explicit, you have more information right at hand, but also
  larger grammars. It is a matter of taste.
* A recommendation: when you use nonterminals from A explicitly, then also 
  make the extension explicit. However, be consistent.


### How to Achieve Modularity (in the Sense of Decoupling)
* Modularity in general is an important design principle.
  In the case of model-based code generation, modularity involves the following 
  dimensions:
  1. Modelling languages
  2. Models
  3. Generator
  4. Generated code
  5. Runtime-Environment (RTE) including imported standard libraries
  6. Software architecture (of the overal system), software stack
* These dimensions are not orthogonal, but also not completely interelated.
  The actual organisation will depend on the form of project.
* A weak form of modularity would be to organize things in
  well understood substructures such as packages. 
  A deeper form of modularity deals with possibility for individual *reuse* 
  and thus an explicit *decoupling* of individual components. We aim for 
  decoupling (even if developed in the same git project).
* Modularity also deals with *extensibility* and *adaptation*.
* A principle for *adaptation* for the *generator*, 
  the *generated code*, and the *RTE* is to design each of them
  like a *framework* with explicit extension points.
  Extension points may be (empty) hook methods to be filled, Java interfaces
  to be implemented and their objects injected to the code e.g. via 
  factories, builders od simply method parameters.
* A principle for *modularity* for the the *generator*, 
  the *generated code*, and the *RTE* is to design parts of them as 
  independent library functions (or larger: components) that can be used if needed.
* We recommend to modularize whenever complexity overwhelms or extensibility and
  adaptability are important:
  1. MontiCore has powerful techniques for adaptation, extension and 
    composition of *modelling languages* (through their grammars). See the
    [reference manual](http://monticore.de/MontiCore_Reference-Manual.2017.pdf).
  2. MontiCore has powerful techniques for the *aggregation of models* --
    using the same principles as programming languages, namely allowing to keep 
    the models independent (and thus storable, versionable, reusable) artifacts,
    while they are semantically and through the generator technology well integrated. 
    The appropriate approach is based on *using* foreign models, e.g. through 
    `import` statements and sharing *symbol* infrastructures as described in the
    [reference manual](http://monticore.de/MontiCore_Reference-Manual.2017.pdf).
  3. The generator provides (a) many Java classes and methods that can be overridden
    (b) Freemarker templates hook points to extend and replace templates, and (c)
    can be customized using a groovy script.
    The generator iteself is often structured along the software architecture / stack,
    e.g. in frontend, application backend, data base, transport layer, etc.
  4. The generated code must be designed appropriately by the generator designer, 
    by generating builders, mills, etc. for each form of product - quite similar 
    to MontiCore itself.
    The generated code is usually structured along the components or sub-systems
    that the software architecture defines.
  5. The RTE is probably well designed if it is usable a normal framework.
* Please note: it is not easy to design modularity and extensibility from beginning.
  Framework design has shown that this is an iterative optimizing process.
  It must be avoided to design too many extension elements into the system
  from the beginning, because this adds a lot of complexity.
* Defined by: BR  

### Realizing Embedding through an Interface Nonterminal Extension Point

Consider the following scenario: 
A language `Host` defines an extension point through an interface nonterminal.

```
grammar Host { A = I*; interface I; }
```

Another language `Embedded` that has no connection to the `Host` language, 
defines a class nonterminal `E`.

```
grammar Embedded { E = "something"; }
```

MontiCore provides alternative solutions to embed the language `Embedded`
into the language `Host` at the extension point `I`. All solutions presented here
require to implement a new grammar `G` that extends the grammars `Embedded` and `Host`, 
which reuses the start nonterminal of the `Host` grammar:

```
grammar G extends Host, Embedded { start A; }
```

The connection between extension point and extension is performed by an additional
grammar rule in the grammar `G`. This can be realized in one of the following ways, each 
of which has its own advantages and disadvantages:

1. Embedding through overriding of extension rule and implementing extension point rule:
  * `E implements I;`
  * Advantage: simple embedding rule
  * Disadvantage: does not work in combination with inheritance of extension rule
  * Should therefore only be used, it `E` is not used anywhere else (= in not other language that is potentially used in combination with this language) 
2. Embedding through extending extension rule and implementing extension point rule:
  * `IE extends E implements I = "something";`
  * Advantage: does work in combination with inheritance of extension rule
  * Disadvantage: cloning of RHS of the extension rule can produce inconsistencies if `E` is changed
  * Can be used if it is assured that this rule is adjusted whenever `E` is changed, e.g., by assuming that `E` is not modified at all
3. Embedding through implementing extension point rule and providing extension on right-hand side:
  * `IE implements I = E;`
  * Advantage: does work in combination with inheritance of extension rule
  * Disadvantage: introduces new level of indirection in AST that invalidates check whether required abstract syntax (RHS of interface nonterminal) is present
  * Should therefore not be used, if the interface has a right-hand side
* Defined by: AB



## Further Information

* [Overview Best practices](BestPractices.md)
* [MontiCore project](../../README.md) - MontiCore
* see also [**MontiCore Reference Manual**](http://www.monticore.de/)


