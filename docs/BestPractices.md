<!-- (c) https://github.com/MontiCore/monticore -->

# MontiCore Best Practices - A Guide of Small Solutions

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

## Designing Concrete and Abstract Syntax 


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
  * Use a normal nonterminal `X` and extend it in a sub-grammar.
  ```
  component grammar A {  
    X = "";
    N = "bla" X "blubb";
  }
  grammar B extends A {
    Y extends X = "this" 
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
    external X = "";
    N = "bla" X "blubb";
  }
  grammar B extends A {
    X = "your"
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
* Defined by: SVa.
  
 

## Designing Symbols, Scopes and SymbolTables 


## Generating Code with Templates 
