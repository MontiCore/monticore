# MontiCore - Language Workbench plus Tool Framework
[MontiCore](http://www.monticore.de) is a language workbench for an efficient development of domain-specific languages (DSLs). It processes an extended grammar format which defines the DSL and generates components for processing the documents written in the DSL. Examples for these components are parser, AST classes, symboltables or pretty printers.This enables a user to rapidly define a language and use it together with the MontiCore-framework to build domain specific tools.

#Licenses
* [LGPL V3.0](https://github.com/MontiCore/monticore/tree/master/00.org/Licenses/LICENSE-LGPL.md) (for handwritten Java code)
* [BSD-3-Clause](https://github.com/MontiCore/monticore/tree/master/00.org/Licenses/LICENSE-BSD3CLAUSE.md) (for templates and generated Java code)

#Release Notes
This product includes the following software:
* [AntLR](http://www.antlr.org/)
* [FreeMarker](http://freemarker.org/)

#Build
Please make sure that your complete workspace only uses UNIX line endings (LF) and all files are UTF-8 without BOM.
On Windows you should configure git to not automatically replace LF with CRLF during checkout by executing the following configuration:

    git config --global core.autocrlf input
