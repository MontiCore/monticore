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
[MontiCore reference manual](http://monticore.de/MontiCore_Reference-Manual.2017.pdf).

## Designing Concrete and Abstract Syntax 


### Specific keywords that shall be used as normal words elsewhere
* `A = "foo" B` introduces `foo` as a keyword that can be used as an ordinary 
  (variable) name anymore. To prevent that we may use:
* `A = key("foo") B` instead, which introduces `foo` only at that specific point.
* In general, we use all Java keywords as permanent, but abstain from other
  permanent keywords, especially if only used for a specific purpose in a composable
  sublanguage, like `in` in the OCL.
* Defined by: BR


## Designing Symbols, Scopes and SymbolTables 


## Generating Code with Templates 
