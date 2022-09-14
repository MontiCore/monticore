<!-- (c) https://github.com/MontiCore/monticore -->
# Languages and Language Components Developed with MontiCore

The MontiCore language workbench has been under development for a while 
already and of course has been used by our group to develop 
many languages. Not all of those languages are publicly 
available and some of these languages are equipped with tools based 
on MontiCore 5. 

Many of these languages are composed of sublanguages and 
thus potentially build on each
other. The available languages can be used as is, but 
also be adapted, extended and further composed. 

Please also have a look at our literature references for further 
information on many of the languages. 

## Github Available Languages
* [Class Diagrams](https://github.com/MontiCore/cd4analysis)
* [Sequence Diagrams](https://github.com/MontiCore/sequence-diagram)
* [JSON](https://github.com/MontiCore/json)
* [Feature Diagrams](https://github.com/MontiCore/feature-diagram)
* [SI Units](https://github.com/MontiCore/siunits)
* [automaton](https://github.com/MontiCore/automaton)
* [EmbeddedMontiArc](https://github.com/MontiCore/EmbeddedMontiArc)
* [Object Diagrams](https://github.com/MontiCore/object-diagram)

## Further Languages (e.g. used in scientific and industrial projects)

* **MontiArc ADL** is an architectural definition language for 
    component and connector models with enhanced connection facilities, 
    hierarchical decomposition etc. and provides a simulator 
    [[HRR12,BHH+17,Wor16,Hab16]](https://www.se-rwth.de/publications/).

* **MontiArcAutomaton ADL** is an extension of the 
    architectural definition language MontiArc using automata to describe
    behavior. 
    Some applications e.g. are of robotics, production, or InternetOfThings.
    [[BKRW17a,HKR+16,BRW16a,Wor16]](https://www.se-rwth.de/publications/).

* **UML/P** is a derivation from UML, especially suited for agile 
development. See language definition and usage method in 
[[Rum17,Rum16,Sch12]](https://mbse.se-rwth.de/). 

    * **UML/P Class Diagrams** for data structures
    * **UML/P Object Diagrams** for exemplaric situations: usable for
        constructive development as well as testing
    * **OCL/P** as Java-variant of the OCL with a nice logic, 
        set-comprehension etc.
    * **UML/P Statecharts** for behavior
    * **UML/P Sequence Diagrams** for interaction
    * **Activity Diagrams** for workflows and requirements 
        (an extension to the books)

* **Delta-MontiArc** [[HRRS12,HKR+11,HRRS11]](https://mbse.se-rwth.de/) 
 is a DSL for expressing deltas on MontiArc component definitions
 which allows to model software product lines in a bottom up way.

* **MontiArcHV** [[HRR+11]](https://mbse.se-rwth.de/) 
 allows specifying component variability fully integrated within the 
 component hierarchy located at variation points in component definitions.

* **Java** as full language as well as source for Java expressions, 
 statements, attribute or method definitions.

* **FeatureDSL** is a DSL for feature diagrams in software product line 
 approaches.

* **DeltaCD** is a DSL for expressing deltas on class diagrams
 which allows to model software product lines in a bottom up way

* **Aerospace Constraint Specification Language** is a DSL used to 
 specify critical situations in an airspace including airplanes, 
 weather, flight conditions and much more. [[ZPK+11]](https://mbse.se-rwth.de/)

* **clArc DSL Family:** [[PR13]](https://mbse.se-rwth.de/)

    * **Cloud Architecture Description Language:** used to model of 
            architectures of cloud-based systems; based on MontiArc.
    * **Target Description Language:** used to model the infrastructure 
            architecture of cloud-based systems.
    * **Mapping Description Language:** used to model deployments 
            between software and infrastructure architectures.
    * **Architecture Scenario Description Language:** used to model 
            scenario-based test cases for software architectures.

* **I/O-TestDSL**  
        for the definition of stream-based, input-output 
        related black-box tests for architecture definition languages 
        like MontiArc.

* **LightRocks**, a modelling language for robotic assembly processes.

* **cdViews** is a DSL used to model partial views on class diagrams

* **RBAC** for Role-Based Access Control in enterprise information systems.

* **MontiWis** [[RR13,Rei16]]
    is a family of DSLs for the model-based, generative 
    development of web information systems among others based on 
    class diagrams, activity diagrams and views.

* **HQL**: Hibernate Query Language that maps to hibernate based 
    executions.

* **SQL** the well known DB query language; used for embedding 
    e.g. into other languages.

* **XML** the basic infrastructure for all XML dialects

* **CarOLO DSLs** for autonomic driving. This among others 
    contains a DSL for defining road scenarios with moving vehicles 
    as well a obstacles suited for laser, lidar, radar and camera 
    sensors. This languages are part of the Darpa Urban Challenge 2007.
    [[BR12b,BR12,Ber10,BR09]](https://www.se-rwth.de/publications/).

* **ProcEd** a Web-based Editing Solution for Domain Specific 
    Process-Engineering
    [[BGR09]](https://www.se-rwth.de/publications/).

* **MontiWeb** a modular development approach for 
    Web Information Systems (which was later succeeded by MontiWIS)
    [[DRRS09]](https://www.se-rwth.de/publications/).

* **C++** and its sublanguages for expressions, statements and 
    definitions (but no generic types, no defines).

* **MontiCore** itself uses a family of DSLs for the definition of 
    DSLs, i.e., their grammars. [[HR17]](https://www.se-rwth.de/publications/)

## More Information about Languages and Language Components 

* [**MontiCore Reference Manual**](https://monticore.de/MontiCore_Reference-Manual.2017.pdf).
   The reference Manual describes how to use MontiCore as an out-of-the-box 
   *language workbench*), but also as a grey box *tooling framework*.
   It thus also gives an overview over a number of core mechanisms of MontiCore.

* [**List of MontiCore core Language Components**](../monticore-grammar/src/main/grammars/de/monticore/Grammars.md).
   MontiCore concentrates on reuse. It therefore offers a set of
   predefined *language components* where the main artifact is usually a
   *component grammar*. Reusing these language components allows 
   language developers to define their own language as a
   composition of reusable assets efficiently. Reusable assets describe among others 
   several sets of *literals*, *expressions* and *types*, which are relatively 
   freely composable.

* [**List of languages**](Languages.md).
   This is a another list of newer MontiCore
   languages that can be used out of the box or also composed. 
   Many of them already are rather stable, but some of them also undergo a
   lively development and enhancement. 
   These complete languages are usually composed of a number of language
   components.

* [**MontiCore topic list**](https://www.se-rwth.de/topics/) 
   Describes various research topics which MontiCore builds on or
   where MontiCore has been used as foundation.

## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](https://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/opendev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/opendev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/opendev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)

