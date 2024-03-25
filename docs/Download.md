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
| Automaton Example Project           | Example language definition project that can be used with MontiCore.                                                                                                                                                                                                             | [Download JAR](https://www.monticore.de/download/aut.tar.gz)                        |
| Automaton Example Project in Gradle | Example language definition project that can be used with MontiCore's Gradle integration.                                                                                                                                                                                                                             | [Download JAR](https://www.monticore.de/download/Automaton.zip)                     |
| CD Tool                             | Parser, well-formedness checker, pretty printer, semantic and syntactic differencing, merging for the [MontiCore Class Diagrams](https://github.com/MontiCore/cd4analysis/blob/develop/README.md).                                                               | [Download JAR](https://www.monticore.de/download/MCCD.jar)                          |
| FACT Tool                           | Finds valid configurations for [MontiCore Feature Diagrams](https://github.com/MontiCore/feature-diagram/blob/develop/README.md), completes incomplete feature configurations and checks validity of complete feature configurations.                                                                                                          | [Download JAR](https://www.monticore.de/download/MCFACT.jar)                        |
| MLC Tool                            | Modelling and maintaining architectural drift in language components (especially usable in MontiCore language definitions).                                                                                                                                                                                                                          | [Download JAR](https://www.monticore.de/download/MCMLC.jar)                         |
| OCL Tool                            | Parser, well-formedness checker, pretty printer for the [MontiCore Object Constraint Language](https://github.com/MontiCore/ocl/blob/develop/README.md).                                                                                                                  | [Download JAR](https://www.monticore.de/download/MCOCL.jar)                         |
| OD4Data Tool                        | Parser, well-formedness checker, pretty printer for the [MontiCore Object Diagram language](https://github.com/MontiCore/object-diagram/blob/dev/README.md) describing data.                                                                                                          | [Download JAR](https://www.monticore.de/download/MCOD4Data.jar)                     |
| OD4Report Tool                      | Parser, well-formedness checker, pretty printer for the extended [MontiCore Object Diagram language](https://github.com/MontiCore/object-diagram/blob/dev/README.md), in the version which focuses on generated reports and artifact-based analyses.                                | [Download JAR](https://www.monticore.de/download/MCOD4Report.jar)                   |
| SD4Development Tool                 | Parser, well-formedness checker, pretty printer, semantic differencing for the [MontiCore Sequence Diagram language](https://github.com/MontiCore/sequence-diagram/blob/dev/README.md).                                                                               | [Download JAR](https://www.monticore.de/download/MCSD4Development.jar)              |
| Statecharts Tool                    | Parser, well-formedness checker, pretty printer for [MontiCore Statechart Models](https://github.com/MontiCore/statecharts/blob/dev/README.md), which are a rich variant of automata.                                                                                                                       | [Download JAR](https://www.monticore.de/download/MCStatecharts.jar)                 |
| SysML v2 Tool                    | Parser and well-formedness checker for SysML v2 models. | [Download JAR](https://www.monticore.de/download/MCSysMLv2.jar)                 |
| JSON Tool                           | Parser, well-formedness checker, pretty printer, object diagram exporter for the [MontiCore JSON language](https://github.com/MontiCore/json/blob/develop/README.md) using the <a href="https://github.com/MontiCore/json/blob/develop/src/main/grammars/de/monticore/lang/json.md">command line</a>. | [Download JAR](https://www.monticore.de/download/MCJSON.jar)                        |
| XML Tool                            | Parser and pretty printer for the [MontiCore XML language](https://github.com/MontiCore/xml/blob/develop/README.md).                                                                                                                                                                                           | [Download JAR](https://www.monticore.de/download/MCXML.jar)                         |

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
