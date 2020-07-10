<!-- (c) https://github.com/MontiCore/monticore -->
# MontiCore - Getting Started

## IntelliJ

--------------------
For getting started with MontiCore using IntelliJ do the following:

### Setting up IntelliJ IDEA
1. Download and install IntelliJ IDEA (or use your existing installation)
    *  Hint for Students: You get the Ultimate version of IntelliJ for free
2. Open IntelliJ IDEA

### Importing the Example
1.  Clone the github project or download the zip for the Automata Example 
[here](https://github.com/MontiCore/automaton).
2.  Select
    * File
    * Open
    * Select the folder holding the pom.xml

### Running MontiCore
From the Maven Projects Menu on the right select

1.  Automata
2.  Lifecycle
3.  install (double click)

MontiCore will be launched and the following steps will be executed:

1.  The specified grammar will be parsed and processed by MontiCore.
2.  Java source files for the corresponding DSL infrastructure will be generated into the default output directory ‹../target/generated-sources/monticore/sourcecode›. This infrastructure consists of:
    1.  **/automata/_ast** containing the abstract syntax representation of the automata DSL.
    2.  **/automata/_cocos** containing infrastructure for context conditions of the automata DSL.
    3.  **/automata/_od** containing infrastructure for printing object diagrams of the automata DSL.
    4.  **/automata/_parser** containing the generated parsers which are based on [ANTLR](https://www.antlr.org/).
    5.  **/automata/_symboltable** containing infrastructure for the symbol table of the automata DSL.
    6.  **/automata/_visitor** containing infrastructure for visitors of the automata DSL.
    7.  **/reports/Automata** containing reports created during the processing of the automata grammar.
3.  The output directory will also contain a log file of the executed generation process ‹monticore.YYYY-MM-DD-HHmmss.log›.

## Troubleshooting

----------------------
*  If an error occurs and you cannot solve the problem you may take a look into the MontiCore 
log file located in the respectively specified output directory 
(e.g. ‹out/monticore.YYYY-MM-DD-HHmmss.log› by default). 
It contains more verbose and developer-oriented, technical output than the CLI output.
*  Please report any unknown issues to <bugreport@monticore.de>. 
Please include the processed grammar, model, and the log file.

## Downloads

---------------------------------
*  [MontiCore zip with automata DSL example](http://www.monticore.de/gettingstarted/monticore-cli-6.0.0.zip)
*  [Executable MontiCore Java archive (without example)](http://www.monticore.de/gettingstarted/monticore-cli-6.0.0.zip)
 	
## Further Information

* see also [**MontiCore Reference Manual**](http://www.monticore.de/)

* [MontiCore project](../README.md) - MontiCore
