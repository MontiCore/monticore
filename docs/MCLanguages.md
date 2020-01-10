# Languages and Language Components
The MontiCore language workbench does have special mechanisms to extend, adapt and compose languages. Such mechanisms however live from available language definitions.

Therefore MontiCore provides a number of well defined languages, that can be used for various purposes on their own, but also as components for further language definitions. Actually, some of the languages are compositions and derivations of others.

The following list contains a usable subset of these language (components) developed using MontiCore. Please also have a look at our literature references for further information on many of the languages.

* UML/P is a derivation from UML, especially suited for agile development. See language definition and method in [Rum12,Rum11].
    * UML/P Class Diagrams for data structures
    * UML/P Object Diagrams for exemplaric situations: constructive as well as testing
    * OCL/P as Java-variant of the OCL with a nice logic, set-compresnsion etc.
    * UML/P Statecharts for behavior
    * UML/P Sequence Diagrams for interaction
    * Activity Diagrams for workflows and requirements (an extension to the book)
* MontiArc ADL is an architectural definition language for component and connector models with enhanced connection facilities, hierarchical decomposition etc. and provides a simulator [HRR12].
* Δ-MontiArc [[HRRS12]](https://www.se-rwth.de/publications/Evolving-Delta-oriented-Software-Product-Line-Architectures.pdf),
 [[HKR+11]](https://www.se-rwth.de/publications/AH.TK.HR.BR.IS.DeltaMontiArc.SAVA2011.pdf),
 [[HRRS11]](https://www.se-rwth.de/publications/Delta-Modeling-for-Software-Architectures.pdf) is a DSL for expressing deltas on MontiArc component definitions, which allows to model software product lines in a bottom up way.
* MontiArcHV [[HRR+11]](https://www.se-rwth.de/publications/Hierarchical-Variability-Modeling-for-Software-Architectures.pdf) allows specifying component variability fully integrated within the component hierarchy located at variation points in component definitions.
* Java as full language as well as source for Java expressions, statements, attribute or method definitions.
* FeatureDSL is a DSL for feature diagrams in software product line approaches
* DeltaCD is a DSL for expressing deltas on class diagrams, which allows to model software product lines in a bottom up way
* Airspace Constraint Specification Language is a DSL used to specify critical situations in an airspace including airplanes, weather, flight conditions and much more
* clArc DSL Family:
    * Cloud Architecture Description Language: used to model of architectures of cloud-based systems; based on MontiArc.
    * Target Description Language: used to model the infrastructure architecture of cloud-based systems.
    * Mapping Description Language: used to model deployments between software and infrastructure architectures.
    * Architecture Scenario Description Language: used to model scenario-based test cases for software architectures.
* I/O-TestDSL for the definition of stream-based, input-output related black-box tests for architecture definition languages like MontiArc.
* MontiArcAutomaton to model architecture and behavior for robotics applications based on MontiArc.
* LightRocks, a modeling language for robotic assembly processes.
* cdViews is a DSL used to model partial views on class diagrams
* RBAC for Role-Based Access Control in enterprise information systems.
* MontiWis is a family of DSLs for the model-based, generative development of web information systems among others based on class diagrams, activity diagrams and views.
* HQL (Hibernate Query Language)
* SQL the well known DB query language; used for embedding e.g. into other languages.
* XML the basic infrastructure for all XML dialects
* CarOLO DSLs for autonomic driving. This among others contains a DSL for defining road scenarios with moving vehicles as well a obstacles suited for laser, lidar, radar and camera sensors. (Part of the Darpa Urban Challenge 2007)
* MontiCore itself uses a family of DSLs for the definition of DSLs, i.e., their grammars.

Less well elaborated, but also available are the following components. E.g. checking of context conditions or the type system is not complete or the language has not been migrated to recent version of the MontiCore language workbench.

* C++ and its sublanguages for expressions, statements and definitions (but no generics, no defines, cocos are incomplete)