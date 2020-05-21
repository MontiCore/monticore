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
In this list you mainly find grammars for 
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
* CD4A is the textual representation to describe **UML class diagrams** 
  (it uses the [UML/P](http://mbse.se-rwth.de/) variant).
* CD4A covers **classes, interfaces, inheritance, attributes with types,
  visibilities**,
  and all kinds of **associations** and **composition**, including **qualified**
  and **ordered associations**. An example:
  ```
  classdiagram MyLife { 
    abstract class Person {
      int age;
      Date birthday;
      List<String> nickNames;
    }
    class Student extends Person {
      StudentStatus status;
    }
    enum StudentStatus { ENROLLED, FINISHED; }
    
    composition Person -> Address [*]  {ordered};
    association [0..2] Person (parent) <-> (child) Person [*];
    association phonebook Person [String] -> TelefoneNumber ;
  }
  ```
* It focusses on the analysis phase in typical data-driven development 
  projects and is therefore mainly for data modelling.
  Consequently, it omits method signatures and complex generics.
  CD4A primary use is therefore **data modelling**. It has various 
  possibilities for generation of data structures, database tables as well as 
  data transport infrastructures in cloud and distributed systems.
* [Main grammar `de.monticore.cd.CD4Analysis`](https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/blob/develop/src/main/grammars/de/monticore/cd/CD4Analysis.mc4)
  and 
  [*detailed description*](https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/-/blob/develop/src/main/grammars/de/monticore/cd/cd4analysis.md)


### [Class Diagram for Code (CD4Code)](https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/-/blob/develop/src/main/grammars/de/monticore/cd/cd4analysis.mds) (Beta: In Stabilization)
* Responsible: SVa, AGe
* CD4Code describes **UML class diagrams**.
* CD4Code is a conservative extension of **CD4A**, 
  which includes method signatures.
  ```
  classdiagram MyLife2 {
    // like CD4A but also allows:
    class Person {
      protected List<Person> closestFriends(int n);
      void addFriend(Person friends...);
    }
  }
  ```

* CD4Code is often used as tool-internal intermediate AST that allows to
  map any kind of source models to a class/attribute/method/association based
  intermediate structure, before it is printed e.g. as Java code. 
  A typical path is e.g. Statechart -> State pattern encoded in CD4Code 
  -> Decoration by monitoring methods -> Java code.
* Main grammar [`de.monticore.cd.CD4Code`](https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/blob/develop/src/main/grammars/de/monticore/cd/CD4Code.mc4)
  and 
  [*detailed description*](https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis/-/blob/develop/src/main/grammars/de/monticore/cd/cd4analysis.md) 
  (see Section *CD4Code*)


### [Feature Diagrams](https://git.rwth-aachen.de/monticore/languages/feature-diagram) (Beta: In Stabilization)
* Caretaker: AB, DS
* Language for textual feature models and feature configurations
* Feature diagrams are used to model (software) product lines
* Feature configurations select a subset of features of a feature model 
  to describe a product of the product line
* Main grammar [`FeatureDiagram`](https://git.rwth-aachen.de/monticore/languages/feature-diagram/-/blob/master/fd-lang/src/main/grammars/FeatureDiagram.mc4)
  and 
  [*detailed description*](https://git.rwth-aachen.de/monticore/languages/feature-diagram/-/blob/master/fd-lang/src/main/grammars/FeatureDiagram.md)
* A small teaser for the feature diagram syntax:
  ```
  featurediagram Phone {
    Phone -> Memory & OS & Camera? & Screen;
    Memory -> Internal & External?;
    Internal -> [1..2] of {Small, Medium, Large};
    OS -> iOS ^ Android;
    Screen -> Flexible | FullHD;

    External ? Flexible => Android : iOS && Android ;
  }
  ```
  Each feature model has a name and a body that is surrounded by curly brackets.
  The body contains rules that define the feature tree. Each rule describes a 
  feature group with a parent feature (left-hand side) followed by an arrow 
  (`->`) and children features (right-hand side). 
  The root of the feature tree is detected automatically. 
  Further, a feature model may define cross-tree constraints
  and use Java-like expressions to formulate these.

### [GUI DSL](https://git.rwth-aachen.de/macoco/gui-dsl) (Alpha: Intention to become stable)
* Caretaker: LN
* Language for textual definition of Graphical User Interfaces of Web
Applications
* GUI DSL covers GUI elements and relevant configuration, which include
**layout elements, widgets**, their **style definition** and **references to
data sources**.
* Language is mainly used to describe **GUI of Web Applications**. The models of
the language represent graphical views or their parts, omitting smaller details
of style definition and simplifying connection between graphical elements and
data sources.
* Examples: [**MaCoCo**](https://git.rwth-aachen.de/macoco/implementation),
[**Ford**](https://git.rwth-aachen.de/ford/implementation/frontend/montigem)
* [Main grammar `GUIDSL`](https://git.rwth-aachen.de/macoco/gui-dsl/-/blob/master/src/main/grammars/GUIDSL.mc4)
includes definitions of MontiGem visualisation components, which are based on
abstract concepts, described in
[core grammar `GUIDSLCore`](https://git.rwth-aachen.de/macoco/gui-dsl/-/blob/master/src/main/grammars/GUIDSLCore.mc4).
[*Detailed description*](https://git.rwth-aachen.de/macoco/gui-dsl/-/blob/master/src/main/grammars/GUIDSL.md)
and
[*documentation*](https://git.rwth-aachen.de/macoco/gui-dsl/wikis/home).


### [MontiCore Grammar](https://git.rwth-aachen.de/monticore/monticore/blob/dev/monticore-generator) (MontiCore Stable)
* Caretaker: MB 
* Language for MontiCore Grammars itself. It can be understood as 
  *meta language*, but also used as ordinary language.
* Its main use currently: A MontiCore grammar defines the 
  **concrete syntax** and the **abstract syntax** of a textual language.
  Examples: All languages on this page are defined using MontiCore grammars
  and thus conform to this Grammar.
* Main features: Define **nonterminals** and their **productions** in EBNF, 
  **lexical token** as regular expressions. 
* Most important extensions to standard grammars:
  * **Abstract**, **interface** and **external productions** allow to
    define extensible component grammars (object-oriented grammar style).
  * Inherited productions can be redefined (overwritten) as well
    as conservatively extended.
  * Symbol and scope infrastructure is defined by simple keywords.
  * **Symbols definition** places can be introduced and 
    **symbol referencing places** defined, such that for standard cases
    automatically symbol tables can be added.
  * Additional attributes and methods can be added to the abstract syntax only.
  * Various elements, such as **semantic predicates** and **actions**
    can be defined in the same style as the underlying ANTLR.
  * MontiCore grammars can be **left recursive** and even allow mutual recursion. 
    This is e.g. useful for expression hierarchies.
  * Additional elements, such as **enum productions** and comfortable 
    operations for grammar definitions exist.
* Main grammars 
  [`de.monticore.grammar.Grammar`](https://git.rwth-aachen.de/monticore/monticore/-/blob/dev/monticore-generator/src/main/grammars/de/monticore/grammar/Grammar.mc4)
  defines the language with some open parameters and
  [`de.monticore.grammar.Grammar_WithConcepts`](https://git.rwth-aachen.de/monticore/monticore/-/blob/dev/monticore-generator/src/main/grammars/de/monticore/grammar/Grammar_WithConcepts.mc4)
  binds the external, imported expressions, method bodies, etc.
* [*Detailed description*](http://monticore.de/MontiCore_Reference-Manual.2017.pdf)
  in the MontiCore Reference Manual.
  

### [JSON](https://git.rwth-aachen.de/monticore/languages/json) (MontiCore Stable)
* Responsible: NJ
* The MontiCore language for parsing JSON artifacts.
* The JSON grammar adheres to the common **JSON standard** and allows parsing 
  arbitrary JSON artifacts for further processing.
* Actually the grammar represents a slight superset to the official JSON standard. 
  It is intended for parsing JSON-compliant artifacts. Further well-formedness
  checks are not included, because we assume to parse correctly produced JSON 
  documents only.
* Please note that JSON (like XML or ASCII) is just a carrier language.
  The concrete JSON dialect and the question, how to recreate the
  real objects / data structures, etc. behind the JSON tree structure
  is beyond this grammar, but can be applied to the AST defined here.
* Main grammar 
  [`de.monticore.lang.JSON`](https://git.rwth-aachen.de/monticore/languages/json/-/blob/master/src/main/grammars/de/monticore/lang/JSON.mc4)
  and 
  [*detailed description*](https://git.rwth-aachen.de/monticore/languages/json/-/blob/master/src/main/grammars/de/monticore/lang/json.md)


### [MontiArc](https://git.rwth-aachen.de/monticore/montiarc/core) (Beta: In Stabilization)
* Caretaker: DS 
* MontiArc is an architecture and behavior modeling language and framework 
    that provides an integrated, platform independent structure and behavior 
    modeling language with an extensible code generation framework.
* The Documentation [MontiArc.md](https://git.rwth-aachen.de/monticore/montiarc/core/-/blob/modularization/languages/montiarc-fe/src/main/grammars/MontiArc.md)
* The MontiArc language family contains the following grammar
    * [Arc](https://git.rwth-aachen.de/monticore/montiarc/core/-/blob/modularization/languages/arc-fe/src/main/grammars/Arc.mc4) 
    for modeling architectural diagrams.
    * [IOAutomata](https://git.rwth-aachen.de/monticore/montiarc/core/-/blob/modularization/languages/automata-fe/src/main/grammars/IOAutomata.mc4)
    for component behavior description.
    * [MontiArc](https://git.rwth-aachen.de/monticore/montiarc/core/-/blob/modularization/languages/montiarc-fe/src/main/grammars/MontiArc.mc4)
    combining architectural diagrams with automata behavior descriptions.


### [OCL/P](https://git.rwth-aachen.de/monticore/languages/OCL) (Alpha: Intention to become stable)
* Caretaker: SVa
* OCL/P is the textual representation of the UML OCL standard, adapted 
  with Java-like syntax.
  It's main goal is the usage in combination with other languages like 
  CD4A or Object Diagrams as an integrated part of that languages.
* OCL/P allows to define **invariants** and **pre/post conditions** in 
  the known OCL style. Furthermore, it offers a large set **expressions**
  to model constraints. These expressions include **Java expressions**,
  **set operations**, **list operations** etc., completely covering the 
  OCL standard concepts, but extend it e.g. by **set comprehensions** 
  known from Haskell, a **typesafe cast** or a 
  **transitive closure operator**.
* OCL/P comes with an 
  [OCL to Java generator](https://git.rwth-aachen.de/monticore/languages/OCL2Java)
  and a second generator for OCL in combination with 
  [*Embedded MontiArc*](https://git.rwth-aachen.de/monticore/EmbeddedMontiArc/generators/OCL_EMA2Java).
* Main grammar 
  [`ocl.monticoreocl.OCL`](https://git.rwth-aachen.de/monticore/languages/OCL/-/blob/master/src/main/grammars/ocl/monticoreocl/OCL.mc4)
  and 
  [*detailed description*](https://git.rwth-aachen.de/monticore/languages/OCL/-/blob/master/src/main/grammars/ocl/monticoreocl/OCL.md)


### [Object Diagrams](https://git.rwth-aachen.de/monticore/languages/od) (Beta: In Stabilization)
* Caretaker: SH
* Language for textual object diagrams. In its current state the language is mostly used for (i) data structures in certain projects (e.g. artifact toolchain)
   and (ii) as a report format for languages developed with MontiCore. The OD language provides the possiblility to use expressions in its attributes.
* Main grammars:
    * [ODBasics](https://git.rwth-aachen.de/monticore/languages/od/-/blob/master/src/main/grammars/de/monticore/lang/ODBasics.mc4)
    * [OD4Report](https://git.rwth-aachen.de/monticore/languages/od/-/blob/master/src/main/grammars/de/monticore/lang/OD4Report.mc4)
    * [DateLiterals](https://git.rwth-aachen.de/monticore/languages/od/-/blob/master/src/main/grammars/de/monticore/lang/DateLiterals.mc4)
* [*Detailed description*](https://git.rwth-aachen.de/monticore/languages/od/-/blob/master/src/main/grammars/de/monticore/lang/ODBasics.md) 

### [Sequence Diagrams](https://git.rwth-aachen.de/monticore/statechart/sd-language)  (Beta: In Stabilization) )(50% to MC6)
* Caretaker: RE
* Grammar to parse Sequence Diagrams
* Can be used with testing generator to derive test cases


### [SI Units](https://git.rwth-aachen.de/monticore/languages/siunits) (Alpha: Intention to become stable)
* Caretaker: EK
* The international system of units (SI units) is a physical unit system widely used in the entire world. It is based on the seven basis units 
  `s, m, kg, A, K, mol, cd`, but
  also provides a variety of derived units, which, furthremore, can be refined using prefixes such as m(milli), k(kilo), etc.
* The SI Unit project aims to deliver SI units to MontiCore-based languages. It provides a grammar defining most relevant SI units 
  as well as the corresponding prefixes.
  The language developer can use SIUnitLiterals similar to standard literals to build assignments, expressions etc. 
  made from literals accompanied by units, e.g. 5 kg, 30 m, etc.
  Furthermore, the language extensions provides a facility for checking unit compatibility, e.g. in sums 
  (`5 kg + 5 m` is an invalid expression) and assignments (`a = 5 kg` only if a is declared with a unit compatible with kilogram)
* Main grammars:
    * [SI units](https://git.rwth-aachen.de/monticore/languages/siunits/-/blob/master/src/main/grammars/de/monticore/lang/SIUnits.mc4)
    * [SI unit literals](https://git.rwth-aachen.de/monticore/languages/siunits/-/blob/master/src/main/grammars/de/monticore/lang/literals/SIUnitLiterals.mc4)
    * [SI unit types](https://git.rwth-aachen.de/monticore/languages/siunits/-/blob/master/src/main/grammars/de/monticore/lang/types/SIUnitTypes.mc4)
    * [SI primitive type (exemplary syntax)](https://git.rwth-aachen.de/monticore/languages/siunits/-/blob/master/src/main/grammars/de/monticore/lang/types/PrimitiveWithSIUnitTypes.mc4)
* Example projects:
    * [SI Java](https://git.rwth-aachen.de/monticore/languages/siunits/-/blob/master/src/test/grammars/de/monticore/lang/testsijava/TestSIJava.mc4) 
* [*detailed description*](https://git.rwth-aachen.de/monticore/languages/siunits/-/blob/master/src/main/grammars/de/monticore/lang/SIUnits.md)  


### [Statecharts](https://git.rwth-aachen.de/monticore/statechart/sc-language) (Beta: In Stabilization) (90% to MC6)
* Caretaker: RE supported by KH  
* Language to parse Statecharts
* creates transformation language within SC and sc<->cd4a
* [*Detailed description*](https://git.rwth-aachen.de/monticore/statechart/sc-language/-/blob/develop/scgrammar/src/main/grammars/de/monticore/umlsc/Statechart.md) 
* A compact teaser for the Statechart language:
    ```
    statechart Door {
      state Opened
      initial state Closed
      state Locked
    
      Opened -> Closed close() /
      Closed -> Opened open() / {ringTheDoorBell();}
      Closed -> Locked timeOut() / { lockDoor(); } [doorIsLocked]
      Locked -> Closed [isAuthorized] unlock() /
    }
    ```
  This example models the different states of a door: `Opened`, `Closed`, and `Locked`.
  When the statechart is in state `Opened`, it is possible to close the door using `close()`.
  When the statechart is in state `Closed`, it is possible to open the door using `open()`. 
  In the latter case the action  `ringDoorBell()` is executed. 
  When the door is `Closed` it is automatically locked after some time due to a 
  `timeout()` event that triggers the `lockDoor()` action.
  Consequently, the post-condition `doorIsLocked` holds. In case the door is locked,
  it can be unlocked by using `unlock()` if the pre-condition `isAuthorized` is fulfilled.

### [SysML_2](https://git.rwth-aachen.de/monticore/languages/sysml2/sysml2official) (Alpha: Intention to become stable)
* Caretaker: NJ
* Project for the SysML 2 language famlily. It will be compatible with the 
  general upcoming SysML 2 specification.

* MontiCore's SysML 2 is a language familiy that comes with a textual 
  representation to describe SysML 2 diagrams with respect to the standard. 

* SysML 2 covers **ADs**, **BDDs**, **IBDs**, **PackageDiagrams**, 
  **ParametricDiagrams**, **RequirementDiagrams**, **SDs**, **SMDs**, 
  **UseCaseDiagrams**, and general **SysMLBasics**
* [Main grammars](https://git.rwth-aachen.de/monticore/languages/sysml2/sysml2official/-/tree/master/src%2Fmain%2Fgrammars%2Fde%2Fmonticore%2Flang%2Fsysml)
  and 
  [*detailed description*](https://git.rwth-aachen.de/monticore/languages/sysml2/sysml2official/-/blob/master/src/main/grammars/de/monticore/lang/sysml/sysml2.md)


### [Tagging](https://git.rwth-aachen.de/monticore/EmbeddedMontiArc/languages/Tagging) (Alpha: Intention to become stable)
* Caretaker: SVa
* **Tags** are known e.g. from the UML and SysML and mainly used to add
  extra information to a model element. 
  Normally tags (and **stereotypes**) are inserted within the models,
  which over time polutes the models, especially when different sets of
  tags are needed for different technical platforms.
* MontiCore offers a solution that **separates a model and its tags into
  distinct artifacts**. Several independent tagging artifacts
  can be added without any need to adapt the core model.
  This allows fo reuse even of fixed library models.
* The tagging artifacts are dependent on two factors:
  * First, **tags** can be added to named elements of the base model.
    It is of great help that we have an elegant symbol mechanism included 
    in the MontiCore generator.
  * Second, the set of allowed tags can be constrained, by an explicit
    definition of allowed **tag types** and **tag values** and an explicit 
    declaration on which **kinds of symbols** a tag may be attached to.
  * Consequently tagging is not a single language, but a method to 
  **automatically and schematically derive** languages:
    * A tagging schema language TSL (dependent on the available symbol types
      of the base grammar)
    * a tagging language TL (dependent on the tag schema models written in TSL)
* Because tagging models can e.g. be used as configuration techniques 
  in a code generator, appropriate infrastructure is generated as well.
* Some [**tagging language examples**](https://git.rwth-aachen.de/monticore/EmbeddedMontiArc/languages/Tagging-Examples)
* Although concrete languages (and their grammars) are themselves generated,
  there is a 
  [main grammar `ocl.monticore.lang.Tagging`](https://git.rwth-aachen.de/monticore/EmbeddedMontiArc/languages/Tagging/-/blob/master/src/main/grammars/de/monticore/lang/Tagging.mc4),
  where the tagging language is derived from.
  See also [*detailed description*](https://git.rwth-aachen.de/monticore/EmbeddedMontiArc/languages/Tagging/-/blob/master/src/main/grammars/de/monticore/lang/Tagging.md)


### [XML](https://git.rwth-aachen.de/monticore/languages/xml) (Alpha: Intention to become stable)
* Responsible: NJ
* The MontiCore language for parsing XML artifacts
* The XML grammar adheres to the common **standard** and allows parsing 
  arbitrary XML artifacts for further processing
* Main grammar 
  [`de.monticore.lang.XML`](https://git.rwth-aachen.de/monticore/languages/xml/-/blob/master/src/main/grammars/de/monticore/lang/XML.mc4)
  and 
  [*detailed description*](https://git.rwth-aachen.de/monticore/languages/xml/-/blob/master/src/main/grammars/de/monticore/lang/xml.md)


### [JavaLight](https://git.rwth-aachen.de/monticore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/JavaLight.mc4) (Beta: In Stabilization)
* Caretaker: MB
* This is a reduced version of the **Java language**.
  JavaLight is meant to be used to integrate simplified Java-like parts 
  in modeling languages but not to parse complete Java implementations.
* It provides Java's **attribute** and **method definitions**, 
  **statements** and **expressions**, but 
  does not provide class or interface definitions and
  also no wildcards in the type system.
* One main usage of JavaLight is in the Grammar-language to model e.g. 
  Java methods. 
* [Main grammar `de.monticore.JavaLight`]((https://git.rwth-aachen.de/monticore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/JavaLight.mc4)
  and 
  [*detailed description*](https://git.rwth-aachen.de/monticore/monticore/-/blob/dev/monticore-grammar/src/main/grammars/de/monticore/JavaLight.md).



### [Java](https://git.rwth-aachen.de/monticore/javaDSL) (Beta: In Stabilization) (30% to MC6)
* Caretaker: MB
* This is the full Java' Language (as Opposed to JavaLight).
* Main Grammar [`JavaDSL`](https://git.rwth-aachen.de/monticore/javaDSL/-/blob/dev/javaDSL/src/main/grammars/de/monticore/java/JavaDSL.mc4)
  and
  [*detailed description*](https://git.rwth-aachen.de/monticore/javaDSL/-/blob/dev/javaDSL/src/main/grammars/de/monticore/java/JavaDSL.md).


## Further Information

* see also [**MontiCore Reference Manual**](http://www.monticore.de/)

* [MontiCore project](../README.md) - MontiCore


