<!-- (c) https://github.com/MontiCore/monticore -->

# MontiCore Best Practices - A Guide of Small Solutions

[MontiCore](http://www.monticore.de) provides a number of options to design 
languages, access and modify the abstract syntax tree and produce output files.

This (currently unsorted and evolving) list of practices discusses solutions 
that we identified and applied as well as alternatives and their specfic 
advantages and drawbacks. They also mention, where the solution have been
found and the applied first.

This file is partially temporary and also contains compact (incomplete) solutions.
More detailed descriptions of best practices can be found in the 
[MontiCore reference manual](http://monticore.de/MontiCore_Reference-Manual.2017.pdf)
and some of the best practices here will also be incorporated in the next version
of the reference manual.

## Designing Concrete and Abstract Syntax 


### **Specific keywords** that shall be used as normal words elsewhere
* `A = "foo" B` introduces `foo` as a keyword that can be used as an ordinary 
  (variable) name anymore. To prevent that we may use:
* `A = key("foo") B` instead, which introduces `foo` only at that specific point.
* In general, we use all Java keywords as permanent, but abstain from other
  permanent keywords, especially if only used for a specific purpose in a composable
  sublanguage, like `in` in the OCL.
* Defined by: BR


### **Extension** forms in a  component grammar
A component grammar is ment for extension. MontiCore therefore provides four(!) 
  mechanisms that can be used when a sub-grammer shall extend a super-grammar
  briefly discussed here: 
* Interface in the super-grammar
  * Introduce an interface, and allow building of sub-nonterminals in sub-grammars.
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
  * Disadvantage: the designer of `A` explicitly has to design the *hole* `X` 
    and add it it into the production.
* Overriding (empty) nonterminal in the super-grammar
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
    1. The designer of `A` explicitly has to design the *hole* `X` 
      and inject it into other places. 
    1. Only one overriding alternative possible (i.e. multiple overriding in 
       subgrammars are allowed, but only the most specific resides) .
* Extending nonterminal in the super-grammar.
  * Use a normal nonterminal `X` and extend it in a sub-grammar.
  ```
  component grammar A {  
    X = "";
    N = "bla" X "blubb";
  }
  grammar B extends A {
    Y implements X = "this" 
  }
  ```
  * Advantage: *Default* implementation "" exists, no explicit filling needed.
  * Disadvantage: 
    1. The designer of `A` explicitly has to design the *hole* `X` 
       and inject it into other places. 
    1. `Y` can only adapt one nonterminal.
* Using `external` nonterminals in the super-grammar.
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
  * Advantage: Explctely marks a nonterminal as *hole* in the grammar.
        Please observe that interface terminals may or not may be meant to be
        extended in sub-grammars. `external` is clearer here.
  * Disadvantage: 
    1. Leads to more objects in the AST. Both classes `a.X` and `b.X` are 
       instantiated and `a.X` only links to `b.X`.
    2. Only one filling of the `hole` is possible.

* Overriding the whole production.
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
* Combinations are possible.
* Defined by: BR



## Designing Symbols, Scopes and SymbolTables 


## Generating Code with Templates 
