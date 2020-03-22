<!-- (c) https://github.com/MontiCore/monticore -->

# MontiCore Languages - an Overview

[MontiCore](http://www.monticore.de) is a language workbench
with an explicit notion of language components. It uses 
grammars to describe textual DSLs. MontiCore uses an extended 
grammar format that allows to compose language components, 
to inherit, extend, embed
and aggregate language components (see the
[**reference manual**](http://monticore.de/MontiCore_Reference-Manual.2017.pdf)
for details).

A **language component** is mainly represented through the grammar 
describing concrete and abstract syntax of the language plus 
Java-classes implementing specific functionalities plus 
Freemarker-Templates helping to print a model to text.
However, language components are often identified with their main 
component grammar.

Language components are currently organized in two levels:
In this list you find mainly grammars for 
**complete (but also reusable and adaptable) languages**.
A list of
[**grammar components**](../monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
with individual reusable nonterminals is also available in
the MontiCore core project.

The following list presents links to the language development projects, their
main grammars, and a short description 
of the language, available language tools and its development status.
The different development stati for grammars are explained 
[**here**](../00.org/Explanations/StatusOfGrammars.md).

The list covers the language grammars to be found in the several 
`MontiCore` projects, such as `cd4analysis/cd4analysis`
usually in folders like `src/main/grammars/` organized in packages 
`de.monticore.cd`.
MontiCore projects are hosted at

* [`https://git.rwth-aachen.de/monticore`](https://git.rwth-aachen.de/monticore), 
    and partially also at
* [`https://github.com/MontiCore/`](https://github.com/MontiCore/monticore)


## List of Languages 

<!--
### [Activity Diagrams](INSERT LINK HERE) (not adressed yet)
* TO be added
-->


### [Class Diagram For Analysis (CD4A)](https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis) (Beta: In Stabilization)
* Responsible: SVa, AGe
* CD4A is the textual representation to describe UML class diagrams 
  (it uses the [UML/P](http://mbse.se-rwth.de/) variant).
* CD4A covers **classes, interfaces, inheritance, attributes with types,
  visibilities**,
  and all kinds of **associations** and **composition**, including **qualified**
  and **ordered associations**. 
* It focusses on the analysis phase in typical data-driven development 
  projects and is therefore mainly for data modelling.
  Consequently, it omits method signatures and complex generics.
  CD4A primary use is therefore **data modelling**. It has various 
  possibilities for generation of data structures, database tables as well as 
  data transport infrastructures in cloud and distributed systems.
* [Main grammar artifact `de.monticore.cd.CD4Analysis`](https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/blob/develop/src/main/grammars/de/monticore/cd/CD4Analysis.mc4),
  ([*details*](https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/-/blob/develop/cd4analysis.md))
<!-- Status: ok, BR 20.03.22 -->


### [Class Diagram for Code (CD4Code)](https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis) (Beta: In Stabilization)
* Responsible: SVa, AGe
* CD4Code describes **UML class diagrams**.
* CD4Code is a conservative extension of **CD4A**, 
  which includes method signatures.
* CD4Code is often used as tool-internal intermediate AST that allows to
  map any kind of source models to a class/attribute/method/association based
  intermediate structure, before it is printed e.g. as Java code. 
  A typical path is e.g. Statechart -> State pattern encoded in CD4Code 
  -> Decoration by monitoring methods -> Java code.
* [**More details**](https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/-/blob/develop/cd4analysis.md) 
  (section *CD4Code*)
<!-- Status: ok, BR 20.03.22 -->


### [Feature Diagrams](https://git.rwth-aachen.de/monticore/languages/feature-diagram) (Beta: In Stabilization)
* Caretaker: AB, DS
* Language for textual feature models and feature configurations
* Feature diagrams are used to model (software) product lines
* Feature configurations select a subset of features of a feature model 
  to describe a product of the product line
<!-- Status: TODO: Teaser Erstellen -->


### [GUI DSL](https://git.rwth-aachen.de/macoco/gui-dsl) (Alpha: Intention to become stable)
* Caretaker: LN 
* Language for textual definition of Graphical User Interfaces of Web Applications
* Examples: [**MaCoCo**](https://git.rwth-aachen.de/macoco/implementation), 
       [**Ford**](https://git.rwth-aachen.de/ford/implementation/frontend/montigem)
* Documentation: [**here**](https://git.rwth-aachen.de/macoco/gui-dsl/wikis/home)
<!-- Status: TODO: Teaser Erstellen -->


### [MontiCore Grammar](https://git.rwth-aachen.de/monticore/monticore/blob/dev/monticore-generator) (MontiCore Stable)
* Caretaker: MB 
* Language for MontiCore Grammars itself. It can be understood as 
  *meta language*, but also used as ordinary language.
* Its main use currently: A MontiCore grammar defines the 
  **concrete syntax** and the **abstract syntax** of a textual language.
  Examples: All languages on this page are defined using MontiCore grammars.
* 


### [JSON](https://git.rwth-aachen.de/monticore/languages/json) (Beta: In Stabilization)
* Caretaker: NJ
* MontiCore language for parsing JSON artifacts.
<!-- Status: TODO: Teaser Erstellen -->


### [MontiArc](https://git.rwth-aachen.de/monticore/montiarc/core) (Beta: In Stabilization)
* Caretaker: DS 
* MontiArc is an architecture and behavior modeling language and framework 
    that provides an integrated, platform independent structure and behavior 
    modeling language with an extensible code generation framework.
* [Port Automata](https://git.rwth-aachen.de/monticore/montiarc/core) 
    are a certain type of state machines and utilized in component and 
    connector architecture description languages (e.g. MontiArc) for 
    behavior modeling. (Alpha: Intention to become stable)


### [OCL/P](https://git.rwth-aachen.de/monticore/languages/OCL) (Alpha: Intention to become stable)
* Caretaker: SVa
* Project: [`monticore/languages/OCL`](https://git.rwth-aachen.de/monticore/languages/OCL)
* Main grammars:
  * [`ocl.monticoreocl.OCL`](https://git.rwth-aachen.de/monticore/languages/OCL/-/blob/master/src/main/grammars/ocl/monticoreocl/OCL.mc4)
* OCL/P is the textual representation of the UML OCL standard, adapted 
  with Java-like syntax.
* It's main goal is the usage in combination with other languages like 
  CD4A or Object Diagrams.
* A Java generator can be found 
  [**here**](https://git.rwth-aachen.de/monticore/languages/OCL2Java).
* A generator for Embedded MontiArc in combination with OCL can be found 
  [**here**](https://git.rwth-aachen.de/monticore/EmbeddedMontiArc/generators/OCL_EMA2Java).
* [**More details**](https://git.rwth-aachen.de/monticore/languages/OCL/-/blob/master/OCL.md)


### [Object Diagrams](https://git.rwth-aachen.de/monticore/languages/od) (Beta: In Stabilization)
* Caretaker: SH
* Language for textual object diagrams.

### [Sequence Diagrams](https://git.rwth-aachen.de/monticore/statechart/sd-language)  (Beta: In Stabilization) )(50% to MC6)
* Caretaker: RE
* Grammar to parse Sequence Diagrams
* Can be used with testing generator to derive test cases

### [SI Units](https://git.rwth-aachen.de/monticore/languages/siunits) (Alpha: Intention to become stable)
* Caretaker: EK, NJ, DS
* allows a language developer to use physical units in a language

### [Statecharts](https://git.rwth-aachen.de/monticore/statechart/sc-language) (Beta: In Stabilization) (90% to MC6)
* Caretaker: RE supported by KH with two Hiwis 
* Language to parse Statecharts
* creates transformation language within SC and sc<->cd4a

### [SysML/P](https://git.rwth-aachen.de/monticore/sysml/sysml_2) (Alpha: Intention to become stable)
* Caretaker: NJ
* Project for SysML 2 languages. It is compatible with the general SysML 2 standard.

### [Tagging](https://git.rwth-aachen.de/monticore/EmbeddedMontiArc/languages/Tagging) (Alpha: Intention to become stable)
* Caretaker: SVa
* Project: [`monticore/EmbeddedMontiArc/languages/Tagging`](https://git.rwth-aachen.de/monticore/EmbeddedMontiArc/languages/Tagging)
* Main grammars:
  * [`ocl.monticore.lang.Tagging`](https://git.rwth-aachen.de/monticore/EmbeddedMontiArc/languages/Tagging/-/blob/master/src/main/grammars/de/monticore/lang/Tagging.mc4)
* The Tagging language offers the possibility to enrich existing models of any DSLs.
* The Tagging models can then be used as configuration, e.g. in a code generator.
* [**Examples can be found here**](https://git.rwth-aachen.de/monticore/EmbeddedMontiArc/languages/Tagging-Examples)
* [**More details**](https://git.rwth-aachen.de/monticore/EmbeddedMontiArc/languages/Tagging/-/blob/master/Tagging.md)

### [XML](https://git.rwth-aachen.de/monticore/languages/xml) (Alpha: Intention to become stable)
* Caretaker: NJ
* MontiCore language for parsing XML artifacts.

### [JavaLight](https://git.rwth-aachen.de/monticore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/JavaLight.mc4) (Beta: In Stabilization)
* Caretaker: MB
* This is a reduced version of the Java language which primarily provides 
    methods, statements and expressions 
* JavaLight is meant to be used to integrate simplified Java-like parts 
    in modeling languages but not 
  to parse complete Java implementations
  * One main usage of JavaLight is in the Grammar-language to model e.g. 
    Java methods there
* JavaLight is parameterized with:
  * Literals: through the Literal interface nonterminal, 
   users are free to choose the literals they need


### [Java](https://git.rwth-aachen.de/monticore/javaDSL) (Beta: In Stabilization) (30% to MC6)
* Caretaker: MB
* This is the full Java' Language (as Opposed to JavaLight).


## Further Information

* see also [**MontiCore Reference Manual**](http://www.monticore.de/)

* [MontiCore project](../README.md) - MontiCore


