<!-- (c) https://github.com/MontiCore/monticore -->

# MontiCore Best Practices - A Guide of Small Solutions

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
