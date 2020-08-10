<!-- (c) https://github.com/MontiCore/monticore -->
# Documentation of the Symbol Table Infrastructure
## Conceptual Model of Symbol Tables
* What is a symbol? 
* What is a symbol kind? 
* What is a scope? 
* What are properties of scopes? 
* What is symbol resolution?

## Define the Symbol Table of a Language via its Grammar
* Indicate that a nonterminal defines a symbol
* Indicate that a nonterminal spans a scope
* Indicate that a nonterminal uses the name of a symbol

## Generated Symbol Table Infrastructure
### Scope Interface
### Scope Class
### ArtifactScope Interface
### ArtifactScope Class
### GlobalScope Interface
### GlobalScope Class
### SymbolTableCreator Interface
### SymbolTableCreator Class
### SymbolTablePrinter
### ScopeDeSer
### ResolvingDelegates
### ModelLoader
### Symbol Class
### Symbol DeSer

## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](http://www.monticore.de/)

* [**List of languages**](https://git.rwth-aachen.de/monticore/monticore/-/blob/dev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://git.rwth-aachen.de/monticore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)

