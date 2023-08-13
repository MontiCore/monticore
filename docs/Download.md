<!-- (c) https://github.com/MontiCore/monticore -->

The following tools for MontiCore can be used from the command line and 
thus e.g. well be embedded in scripting. Their languages as well as 
related tooling are currently available for download:

<!-- Optimize table display -->
<style>
.md-typeset table:not([class]) {
  font-size:.75rem;
  box-sizing:content-box;
}
.md-typeset table:not([class]) th {
  padding:.3em .7em;
}
.md-typeset table:not([class]) td {
  padding:.3em .7em;
}
</style>

| Artifact                            | Description                     | Download     |
| ----------------------------------- | --------------------------------| :----------: |
| MontiCore Language Workbench        | Meta-tool for processing grammars and producing tool code: model-loading, model-management, AST, AST-building, AST-traversal, ccontext condition check, type check, symbol management, scope management, generator workflows, template engine, etc.  | [Download JAR](https://www.monticore.de/download/monticore.jar) |
| MontiCore Runtime                   | MontiCore's runtime library.                                                                                                                                                                                                                                              | [Download JAR](https://www.monticore.de/download/monticore-rt.jar)                  |
| Automaton Example Project           | Example project that can be used with the MontiCore CLI tool.                                                                                                                                                                                                             | [Download JAR](https://www.monticore.de/download/aut.tar.gz)                        |
| Automaton Example Project in Gradle | Example project that can be used with Gradle.                                                                                                                                                                                                                             | [Download JAR](https://www.monticore.de/download/Automaton.zip)                     |
| CD Tool                             | Parser, well-formedness checker, printer, semantic and syntactic differencing, merging for the [MontiCore Class Diagrams](https://github.com/MontiCore/cd4analysis#readme) using the command line.                                                                        | [Download JAR](https://www.monticore.de/download/MCCD.jar)                          |
| FACT Tool                           | Analyses against [MontiCore Feature Diagrams](https://github.com/MontiCore/feature-diagram#readme) and feature configuration both via command line and Java API.                                                                                                          | [Download JAR](https://www.monticore.de/download/MCFACT.jar)                        |
| FeatureConfiguration Tool           | Provides fine-grained options for processing [MontiCore Feature Configuration Models](https://github.com/MontiCore/feature-diagram#readme) both via command line and Java API.                                                                                            | [Download JAR](https://www.monticore.de/download/MCFeatureConfiguration.jar)        |
| FeatureConfigurationPartial Tool    | Provides fine-grained options for processing [partial MontiCore Feature Configuration Models](https://github.com/MontiCore/feature-diagram#readme) both via command line and Java API.                                                                                    | [Download JAR](https://www.monticore.de/download/MCFeatureConfigurationPartial.jar) |
| FeatureDiagram Tool                 | Provides fine-grained options for processing [MontiCore Feature Diagram Models](https://github.com/MontiCore/feature-diagram#readme) both via command line and Java API.                                                                                                  | [Download JAR](https://www.monticore.de/download/MCFeatureDiagram.jar)              |
| MLC Tool                            | Tool for grouping MontiCore language components.                                                                                                                                                                                                                          | [Download JAR](https://www.monticore.de/download/MCMLC.jar)                         |
| OCL Tool                            | Parser, well-formedness checker, printer for the [MontiCore Object Constraint Language](https://github.com/MontiCore/ocl#readme) using the command line.                                                                                                                  | [Download JAR](https://www.monticore.de/download/MCOCL.jar)                         |
| OD4Data Tool                        | Parser, well-formedness checker, printer for the [MontiCore Object Diagram language](https://github.com/MontiCore/object-diagram#readme) using the command line.                                                                                                          | [Download JAR](https://www.monticore.de/download/MCOD4Data.jar)                     |
| OD4Report Tool                      | Parser, well-formedness checker, printer for the extended [MontiCore Object Diagram language](https://github.com/MontiCore/object-diagram#readme), which focuses on generated reports and artifact-based analyses, using the command line.                                | [Download JAR](https://www.monticore.de/download/MCOD4Report.jar)                   |
| SD4Development Tool                 | Parser, well-formedness checker, printer, semantic differencing for the [MontiCore Sequence Diagram language](https://github.com/MontiCore/sequence-diagram#readme) using the command line.                                                                               | [Download JAR](https://www.monticore.de/download/MCSD4Development.jar)              |
| Statecharts Tool                    | Provides fine-grained options for processing [MontiCore Statechart Models](https://github.com/MontiCore/statecharts#readme) using the command line.                                                                                                                       | [Download JAR](https://www.monticore.de/download/MCStatecharts.jar)                 |
| JSON Tool                           | Parser, well-formedness checker, printer, object diagram exporter for the [MontiCore JSON language](https://github.com/MontiCore/json#readme) using the [command line](https://github.com/MontiCore/json/blob/develop/src/main/grammars/de/monticore/lang/json.md#usage). | [Download JAR](https://www.monticore.de/download/MCJSON.jar)                        |
| XML Tool                            | Tool for the [MontiCore XML language](https://github.com/MontiCore/xml#readme).                                                                                                                                                                                           | [Download JAR](https://www.monticore.de/download/MCXML.jar)                         |

Please note the [MontiCore 3-Level License](../00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md) of these tools.

## Further Information

* see also [**MontiCore handbook**](https://www.monticore.de/handbook.pdf)
* [MontiCore Reference Languages](https://monticore.github.io/monticore/docs/DevelopedLanguages/) - Languages Built Using MontiCore
* [Build MontiCore](https://monticore.github.io/monticore/docs/BuildMontiCore/) - How to Build MontiCore
* [Getting Started](https://monticore.github.io/monticore/docs/GettingStarted/) - How to start using MontiCore
* [Changelog](../00.org/Explanations/CHANGELOG.md) - Release Notes
* [FAQ](../00.org/Explanations/FAQ.md) - FAQ 
* [Licenses](../00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md) - MontiCore 3-Level License
* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [**List of languages**](https://monticore.github.io/monticore/docs/Languages/)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/opendev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://monticore.github.io/monticore/docs/BestPractices/)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
