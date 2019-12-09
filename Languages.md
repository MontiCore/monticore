<!-- (c) https://github.com/MontiCore/monticore -->

# MontiCore Languages - an Overview

[MontiCore](http://www.monticore.de) is a language workbench. It uses 
grammars to describe DSLs. The extended 
grammar format allows to compose grammars, to inherit, extend, embed
and aggregate grammars (see the reference manual for details).

A list of grammars available in the MontiCore core project can be found 
[**here**](monticore-grammar/src/main/grammars/de/monticore/Grammars.md).

The following presents a list of links to the development projects 
of various languages (including grammars and tools) developed with 
MontiCore. For each language, the list includes a short description 
of the language and its development status.

The different development statuses of grammars are explained 
[**here**](00.org/Explanations/StatusOfGrammars.md).

## List of Languages 

### [Activity Diagrams](INSERT LINK HERE) (INSERT STATUS HERE)
* Author: Not available
* INSERT DESCRIPTION HERE

### [Class Diagram 4 Analysis](https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis) (MontiCore stable)
* Author: SVa, AGe
* CD4A is the textual (UML/P) representation of a UML class diagram.
* Its primary feature are the description and analysis of a class diagram.
* CD4Code is an extension which allows for function signatures and implementations with the [JavaDSL](https://git.rwth-aachen.de/monticore/javaDSL).

### [Feature Diagrams](https://git.rwth-aachen.de/monticore/languages/feature-diagram) (alpha)
* Author: AB, DS
* Language for textual feature models and feature configurations
* Feature diagrams are used to model (software) product lines
* Feature configurations select a subset of features of a feature model to describe a product of the product line

### [GUI DSL](https://git.rwth-aachen.de/macoco/gui-dsl) (Alpha)
* Author: LN 
* Language for textual definition of Graphical User Interfaces of Web Applications
* Examples: [**MaCoCo**](https://git.rwth-aachen.de/macoco/implementation), [**Ford**](https://git.rwth-aachen.de/ford/implementation/frontend/montigem)
* Docuemtation: [**here**](https://git.rwth-aachen.de/macoco/gui-dsl/wikis/home)

### [JSON](https://git.rwth-aachen.de/monticore/languages/json) (Beta: In Stabilization)
* Author: NJ
* MontiCore language for parsing JSON artifacts.

### [MontiArc](https://git.rwth-aachen.de/monticore/montiarc/core) (5% to MC6)
* Author: DS 
* MontiArc is an architecture and behavior modeling language and framework that provides an integrated, platform independent structure and behavior modeling language with an extensible code generation framework.

### [OCL/P](https://git.rwth-aachen.de/monticore/languages/OCL) (Beta: In Stabilization)
* Author: SVa
* OCL/P is the textual representation of the UML OCL standard, adapted with Java-like syntax.
* A Java generator can be found [**here**](https://git.rwth-aachen.de/monticore/languages/OCL2Java).
* A generator for Embedded MontiArc in combination with OCL can be found [**here**](https://git.rwth-aachen.de/monticore/EmbeddedMontiArc/generators/OCL_EMA2Java).

### [Object Diagrams](https://git.rwth-aachen.de/monticore/languages/od) (Started updating to MC6, rearranging language to a basic version and several extensions)
* Author: SH
* Language for textual object diagrams.

### [Port Automata](https://git.rwth-aachen.de/monticore/montiarc/core) (Not started with MC6 changes)
* Author: DS
* Port automata are a certain type of state machines and utilized in component and connector architecture description languages (e.g. MontiArc) for behavior modeling.

### [Sequence Diagrams](https://git.rwth-aachen.de/monticore/statechart/sd-language) (Not started with MC6 changes)
* Author: RE
* Grammar to parse Sequence Diagrams
* Can be used with testing generator to derive test cases

### [SI Units](INSERT LINK HERE) (Alpha)
* Author: EK, NJ, DS
* allows a language developer to use physical units in a language

### [Statecharts](https://git.rwth-aachen.de/monticore/statechart/sc-language) (90% to MC6)
* Author: RE
* Language to parse Statecharts
* creates transformation language within SC and sc<->cd4a

### [SysML/P](INSERT LINK HERE) (INSERT STATUS HERE)
* Author: NJ
* INSERT DESCRIPTION HERE

### [Tagging](https://git.rwth-aachen.de/monticore/EmbeddedMontiArc/languages/Tagging) (Beta: In Stabilization)
* Author: SVa
* The Tagging language offers the possibility to enrich existing models of any DSLs.
* The Tagging models can then be used as configuration, e.g. in a code generator.
* Examples can be found [**here**](https://git.rwth-aachen.de/monticore/EmbeddedMontiArc/languages/Tagging-Examples)

### [XML](INSERT LINK HERE) (INSERT STATUS HERE)
* Author: NJ
* INSERT DESCRIPTION HERE

