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