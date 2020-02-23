# Languages and Language Components that have been developed with MontiCore

The MontiCore language workbench has been under development already 
for a while and of course has old been used by our group to develop 
many other languages. Not all of those languages are of publicly 
available and some of these languages are equipped with tools based 
on MontiCore 5-. 

Some of these languages are monolithic, others are composed of each 
other. The available languages can also be used for adaptation, 
extension, and composition 

Please also have a look at our literature references for further 
information on many of the languages. 

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
[[Rum17,Rum16,Sch12]](http://mbse.se-rwth.de/). 

    * **UML/P Class Diagrams** for data structures
    * **UML/P Object Diagrams** for exemplaric situations: usable for
        constructive development as well as testing
    * **OCL/P** as Java-variant of the OCL with a nice logic, 
        set-comprehension etc.
    * **UML/P Statecharts** for behavior
    * **UML/P Sequence Diagrams** for interaction
    * **Activity Diagrams** for workflows and requirements 
        (an extension to the books)

* **Delta-MontiArc** [[HRRS12,HKR+11,HRRS11]](http://mbse.se-rwth.de/) 
 is a DSL for expressing deltas on MontiArc component definitions, 
 which allows to model software product lines in a bottom up way.

* **MontiArcHV** [[HRR+11]]http://mbse.se-rwth.de/) 
 allows specifying component variability fully integrated within the 
 component hierarchy located at  variation points in component definitions.

* **Java** as full language as well as source for Java expressions, 
 statements, attribute or method definitions.

* **FeatureDSL** is a DSL for feature diagrams in software product line 
 approaches

* **DeltaCD** is a DSL for expressing deltas on class diagrams, 
 which allows to model software product lines in a bottom up way

* **Aerospace Constraint Specification Language** is a DSL used to 
 specify critical situations in an airspace including airplanes, 
 weather, flight conditions and much more. [[ZPK+11]](http://mbse.se-rwth.de/)

* **clArc DSL Family:** [[PR13]](http://mbse.se-rwth.de/)

    * **Cloud Architecture Description Language:** used to model of 
            architectures of cloud-based systems; based on MontiArc.
    * **Target Description Language:** used to model the infrastructure 
            architecture of cloud-based systems.
    * **Mapping Description Language:** used to model deployments 
            between software and infrastructure architectures.
    * **Architecture Scenario Description Language:** used to model 
            scenario-based test cases for software architectures.
<p>

* **I/O-TestDSL**  
        for the definition of stream-based, input-output 
        related black-box tests for architecture definition languages 
        like MontiArc.

* **LightRocks**, a modeling language for robotic assembly processes.

* **cdViews** is a DSL used to model partial views on class diagrams

* **RBAC** for Role-Based Access Control in enterprise information systems.

* **MontiWis** [RR13,Rei16]
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
    [BR12b,BR12,Ber10,BR09](https://www.se-rwth.de/publications/).

* **ProcEd** a Web-based Editing Solution for Domain Specific 
    Process-Engineering
    [BGR09](https://www.se-rwth.de/publications/).

* **MontiWeb** a modular development approach for 
    Web Information Systems (which was later succeded by MontiWIS)
    [DRRS09](https://www.se-rwth.de/publications/).

* **C++** and its sublanguages for expressions, statements and 
    definitions (but no generic types, no defines)

* **MontiCore** itself uses a family of DSLs for the definition of 
    DSLs, i.e., their grammars. [HR17](https://www.se-rwth.de/publications/)


## Further Languages and Information 

* [**MontiCore Online Demonstrator**]().
   (TODO: needs to be released)

* [**MontiCore Reference Manual**](http://monticore.de/MontiCore_Reference-Manual.2017.pdf).
   The reference Manual describes how to use MontiCore as a out-of-the-box 
   *language workbench*), but also as grey box *tooling framework*.
   It thus also gives an overview over a number of core mechanisms of MontiCore.

* [**List of core grammars**](monticore-grammar/src/main/grammars/de/monticore/Grammars.md).
   MontiCore concentrates on reuse. It therefore offers a set of
   predefined *language components*, usually identified through an appropriate 
   *component grammar* allowing to define your own language as a
   composition of reusable assets efficiently. reusable assets are among others: 
   several sets of *literals*, *expressions* and *types*, which are relatively 
   freely composable.

* [**List of languages**](docs/Languages.md).
   This is a list of newer MontiCore 6
   languages that can be used out of the box. Some of them
   are in development, others rather stable. 
   These complete languages are usually composed of a number of language
   components.

* see also [**our literature list**](https://www.se-rwth.de/publications/) 

* see also [**our topic list**](https://www.se-rwth.de/topics/) 

* [MontiCore project](README.md) - MontiCore

