<!-- (c) https://github.com/MontiCore/monticore -->

# MontiCore Best Practices - Symbols, Scopes, Symboltables

[[_TOC_]]

[MontiCore](http://www.monticore.de) provides a number of options to design 
languages, access and modify the abstract syntax tree, and produce output files.

The never MontiCore release gives powerful capabilities to define and 
use symbols. But symbols, scopes, and symboltables are somewhat complex 
to design, but powerful in their use.


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



## Further Information

* [Overview Best practices](BestPractices.md)
* [MontiCore project](../../README.md) - MontiCore
* see also [**MontiCore Reference Manual**](http://www.monticore.de/)

