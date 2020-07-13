<!-- (c) https://github.com/MontiCore/monticore -->

# MontiCore Best Practices - Symbols, Scopes, Symboltables

[[_TOC_]]

[MontiCore](http://www.monticore.de) provides a number of options to design 
languages, access and modify the abstract syntax tree, and produce output files.

The newest MontiCore release gives powerful capabilities to define and 
use symbols. Symbols, scopes, and symboltables are somewhat complex 
to design, but powerful in their use.


## **Designing Symbols, Scopes and SymbolTables** 

### How to define a Symbol Usage without a given Symbol Definition
  ```
  grammar E { 
    A = Name@S; 
    symbol S = Name; 
  }
  ```

* If you want to use a special form of symbol that shall neither be defined 
  inside the grammar of a language, nor shall it be imported.
* We can define symbols of kind `S` in the grammar in a grammar rule that 
  is never reached by the parser from the start production.
  Through this, MontiCore generates:
  * symbol table infrastructure for handling `S` symbols
  * symbol table infrastructure for resolving these in `E` scopes, and 
  * integration of `S` symbols with the AST of `A`.
* However, `S` symbols are not automatically instantiated. 
  This has to be described manually, e.g., by extending the symbol table 
  creator or via providing an adapter translating a foreign symbol into an `S` symbol.
* This can be used, e.g., in these scenarios: 
  * A name of a certain kind is introduced automatically the first time it occurs 
    in a model. If it occurs more than once, all other occurences of the name 
    do not introduce new symbols. (e.g. this happens with features in FDs,
    and works because features do not have a body.)
  * A name in a language `E` refers to a named element of another language, 
    but the language shall be decoupled from `E`. 
    Therefore, `E` introduces a symbol `S` and an adapter maps other symbols
    to `S` symbols.
* Defined by: AB, BR


### Symbol Definition prepared for Reuse 

  ```
  grammar E { 
    symbol Bla = "bla" Name AnotherNT; 
  }
  ```
* has the effect that three things are defined: (a) concrete syntax, 
  (b) abstract syntax with nonterminal `Bla`
  and (c) a symbol `BlaSymbol`.
* Reuse of the symbol `BlaSymbol` currently only works together with a reuse
  of the syntax too, i.e.

  ```
  grammar F extends E { 
    Blubb extends Bla = "blubb" Name; 
  }
  ```
  would for example be illegal, because the conservative extension paradigm 
  enforces `AnotherNT` to be included in `Blubb` as well. 
* To allow individual reuse of symbol `BlaSymbol` we recommend to
  restructure its definition into an interface that does not preclude
  create syntax and only a minimal constraint on the abstract syntax:

  ```
  grammar E { 
    symbol interface Bla = Name; 
    Bla2 implements Bla = "bla" Name AnotherNT; 
  }
  grammar F extends E { 
    Blubb implements Bla = "blubb" Name; 
  }
  ```

* Please note that MontiCore allows that a nonterminal implements
  multiple interfaces. However, only one of them may carry the `symbol` 
  keyboard property, because the newly defined symbol then is also 
  a subclass of the inherited symbol (in Java).

## Further Information

* [Overview Best Practices](BestPractices.md)
* [MontiCore project](../README.md) - MontiCore
* see also [**MontiCore Reference Manual**](http://www.monticore.de/)

