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
    Therefore, `E` introduces a symbol `S` and an adapter maps other, foreign 
    symbols to `S` symbols.
* Defined by: AB, BR


### Symbol Definition prepared for Reuse 

  ```
  grammar E { 
    symbol Bla = "bla" Name AnotherNT; 
  }
  ```
* has the effect that three things are defined: (a) concrete syntax, 
  abstract syntax with (b) AST element `ASTBla`
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

## Loading (DeSerializing) Symbols of Unknown Symbol Kinds

Specific languages (e.g. `CD`) may provide specific symbols, of specific kinds.
A symbol import of these symbols into another language `L1` has to cope with 
potentially unkown kinds of symbols, even though the superclass could be known. 
E.g. `TypeSymbol` is extended by `CDTypeSymbol` providing e.g. additional 
visibility information.
Upon loading an `CD`-symboltable into an `L1`-tool
it may be that neither AST-class `CDTypeSymbol` nor superclass information about 
it is available.
But, the symbols of the unknown kind should (and can) be loaded as symbols of a more abstract kind. 

Loading the symbols of the unknown kind as symbols of the specific known kind is possible in multiple ways.
Options would be 
  1. adapt the `L1`-tool to know about the new symbols, or 
  2. the `L1`-tool has been written in such a way that new classes can be added 
     through appropriate class loading, or 
  3. the `L1`-tool is configurable in handling unknown symbol kinds as explained below.

### Loading Symbols as Symbols of Another Kind

Symbols of an unknown, source kind (e.g., `CDTypeSymbol`) may easily be loaded as 
symbols of a known kind (e.g., `TypeSymbol`), when the source kind provides
all mandatory attributes (i.e. those without defaults) of the symbol class.
This is especially the case if the source kind is a subclass of the known 
kind.

This behavior can be configured in the global scope by calling the method ```putSymbolDeser(String, ISymbolDeser)```,
where the unknown source kind is encoded as string (here: `CDTypeSymbol`) and is mapped to
an appropriate DeSer (here for `TypeSymbol`). 
For instance the call would be 
```putSymbolDeSer("de.monticore.cdbasis._symboltable.CDTypeSymbol", new TypeSymbolDeSer())```.

Because the global scope is a singleton, this configuration can be e.g. called in or shortly 
after constructing the global scope. However, this would still encode the name of the unkown
symbol kind in the `L1`-tool, although it prevents any actual dependency to the imported tools.

The method can also be called from a CLI for dynamically configuring the deserialization,
e.g. the information be fed to the `L1`-tool via parameters, e.g. like
```
  java L2Tool --typeSymbol=de.monticore.cdbasis._symboltable.CDTypeSymbol
              --functionSymbol=de.monticore.cdbasis._symboltable.CDMethodSymbol
```

### Converting Stored Symbol Tables

If the unkown symbol kinds do have different attributes or some extra information
needs to be calculated in the new symbols, then either the `L1`-tool needs to be adapted or
the serialized symbol table can be transformed to another 
serialized symbol table where the kind information is transformed as required as an
intermediate step between the tools providing and reading the symbol tables.

## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](http://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/dev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/dev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)

