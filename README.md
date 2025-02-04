<!-- (c) https://github.com/MontiCore/monticore -->
<center>
  <div style="text-align:center" ><img src="mc-logo.png" /></div>
</center>

# MontiCore - Language Workbench and Development Tool Framework 

---

**NEWS**:
<div align="center">
  <a href="https://monticore.github.io/monticore/docs/MontiCoreSymposium/" target="_blank">
  <img src="https://github.com/MontiCore/monticore/raw/dev/docs/docs/MC_Symp_Banner.png">
  </a>  
</div>  
  
* ISW Stuttgart and RWTH Aachen organize the third **[MontiCore Symposium 2025](docs/MontiCoreSymposium.md)** March 23 - March 26 in Gemünd, Germany
  * Deadline for submission of papers or abstracts: January, 10th.

---

[MontiCore](https://www.monticore.de) is a language workbench for the efficient 
development of domain-specific languages (DSLs). It processes an extended 
grammar format which defines the DSL and generates Java components for processing 
the DSL documents. Examples for these components are parsers, 
AST classes, symboltables or pretty printers.
This enables a user to rapidly define a language and use it together 
with the MontiCore-framework to build domain-specific tools. 

Some MontiCore advantages are the reusability of predefined language 
components, conservative extension and composition mechanisms, and an 
optimal integration of handwritten code into the generated tools. Its 
grammar languages are comfortable to use. 

[**Start here for developing with MontiCore.**](docs/GettingStarted.md)

<div align="center">
  <a href="https://monticore.de/handbook.pdf" target="_blank">
  <img src="https://www.se-rwth.de/assets/img/covers/HKR21.png" width="350">
  </a>
  <br>[HKR21] Katrin Hölldobler, Oliver Kautz, Bernhard Rumpe: <br>
      MontiCore Language Workbench and Library Handbook: Edition 2021. <br>
      Shaker, 2021.
</div><br>


## A Teaser for MontiCore

To show a little of MontiCore's capabilities, the following (incomplete) 
grammar might help:

    grammar MyStatemachine extends Automata,                  // MontiCore grammar 
                                   MCBasicTypes, SetExpressions, MCCommonLiterals {     
      start Automaton;
    
      // overriding a nonterminal (to add optional conditions):
      Transition = from:Name@State ":" Expression? "->" to:Name@State;

      // add new variants of expressions
      LogicalNotExpr implements Expression = "!" Expression;

      XorExpr        implements Expression =
            left:Expression "xor" right:Expression;

      scope LetExpr  implements Expression =
            "let" (VarDeclaration || ",")+ "in" Expression;

      symbol VarDeclaration = MCType? Name "=" Expression ;
    }

The grammar language has a variety of mechanisms to define
new nonterminals using constants `"!"`, 
brackets `(..)`, optionals `?`, lists `*`, repetitions `(..||..)+`, etc. 
The grammar builds an extended version of Statemachines reusing
existing grammar components, here `Automata`, `MCBasicTypes`, `SetExpressions` and `MCCommonLiterals`.
The grammar has 5 productions introducing 4 new nonterminals
and overrides `Transition`,
which is inherited from `Automata`.
`Transition` additionally has an optional `Expression?` as firing condition.
`LogicalNotExpr`, `XorExpr`, and `LetExpr` extend the already existing
`Expression` nonterminal and add new forms of expressions.

`LetExpr` introduces a new local variable, which is
visible only in that _scope_ (indicated by keyword).
`VarDeclaration` defines the new place to define _symbols_ (that have a `Name`).
There is an extensive infrastructure to manage the definition of names, visibility, etc.

MontiCore compiles the above grammar 
into `78` classes with in 
total `18629` lines of code that define the complete
frontend and a larger part of the backend of
a statemachine processor.
We now can write statemachines like:

    statemachine PingPong {                                         // MyStatemachine
      state Ping, Pong;
      Ping : (speed > 14km/h && !missedBall) -> Pong
    }

MontiCore provides versions of expressions that use SI
Units like `240km/h` or `14.2 m/s^2`, but also Java 
expressions like `2_000_000` and other variants including
appropriate type checks.
We include these forms of expressions by importing their grammars.

Please note that in both cases (extension and
overwriting existing nonterminals), we do not 
touch nor copy/paste the predefined grammars,
but achieve an out-of-the-box reuse.
Out-of-the-box reuse also includes reuse of
predefined typechecks, code generation, etc. 
They only need to be extended to the added variants.
Please also note that `PlusExpr` is mutually left-recursive.
-- Yes, that works in MontiCore 6.


## Quick Start

```
$ cd /usr/local
$ wget www.monticore.de/download/aut.tar.gz
$ tar -xf aut.tar.gz
$ cd mc-workspace
$ wget www.monticore.de/download/monticore-cli.jar
$ java -jar monticore-cli.jar -g Automata.mc4 -hcp hwc/ -mp monticore-cli.jar
$ javac -cp monticore-cli.jar -sourcepath "src/;out/;hwc/" src/automata/AutomataTool.java
$ java -cp "src/;out/;hwc/;monticore-cli.jar" automata.AutomataTool example/PingPong.aut PingPong.autsym
```

## MontiCore has a Relaxed 3-Level License  

Informal summary: 
The MontiCore Language Workbench deals with three levels of code 
(MontiCore LWB, tool derivates, product code). Each has its own licenses: 
(1) **Product code generated by a MontiCore tool derivate 
is absolutely free** for each form of use 
including commercial use without any license restriction. 
(2) *Tool derivates* created using the MontiCore language 
workbench mention that it is built using MontiCore. There is 
no other restriction. (BSD 3 Clause license) 
(3) Adaptations of the MontiCore language workbench itself
should mention MontiCore and
results are published back into the MontiCore repository (LGPL license)
-- for other purposes an individual Dual License is available.

For details see [Licenses](00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md).


## More Information about MontiCore

* [**MontiCore handbook**](https://www.monticore.de/handbook.pdf).
   The handbook describes how to use MontiCore as an out-of-the-box 
   *language workbench*, but also as grey box *tooling framework*.
   It thus also gives an overview over a number of core mechanisms of MontiCore.

* [**List of core grammars**](monticore-grammar/src/main/grammars/de/monticore/Grammars.md).
   MontiCore concentrates on reuse. It therefore offers a set of
   predefined *language components*, usually identified through an appropriate 
   *component grammar* allowing to define your own language as a
   composition of reusable assets efficiently. reusable assets are among others: 
   several sets of *literals*, *expressions*, *types*, and *statements*, 
   which are freely composable.

* [**List of languages**](docs/Languages.md).
   This is a list of languages that can be used out of the box. Some of them
   are in development, others rather stable. Several of these languages
   are inspired by the UML/P (see [*[Rum16,Rum17]*](https://mbse.se-rwth.de/)).
   These complete languages are usually composed of a number of language
   components.

* This project is freely available software; you can redistribute 
  the MontiCore language workbench according to the rules described
  in the [**licensing section**](00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md).

* **Contributing to MontiCore:** MontiCore is originally developed by the 
  [Software Engineering group](https://www.se-rwth.de/) at RWTH. 
  Meanwhile, it is maintained by several groups and individual persons. 
  If you want to contribute to MontiCore, please create a 
  [fork](https://github.com/MontiCore/monticore/fork) 
  and issue corresponding pull requests. 
  The RWTH and the Stuttgart development teams will review and merge changes. 
  (A more open process for common development is currently in discussion and 
  depends on interests from further people/groups.)
  You may also ask to become a member of the project.

* If questions appear e.g. on building an interpreter, please contact 
  monticore@se-rwth.de. 
    
## Further Information

* see also [**MontiCore handbook**](https://www.monticore.de/handbook.pdf)
* [MontiCore Reference Languages](https://monticore.github.io/monticore/docs/DevelopedLanguages/) - Languages Built Using MontiCore
* [Build MontiCore](https://monticore.github.io/monticore/docs/BuildMontiCore/) - How to Build MontiCore
* [Getting Started](https://monticore.github.io/monticore/docs/GettingStarted/) - How to start using MontiCore
* [Changelog](00.org/Explanations/CHANGELOG.md) - Release Notes
* [FAQ](00.org/Explanations/FAQ.md) - FAQ 
* [Licenses](00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md) - MontiCore 3-Level License
* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [**List of languages**](https://monticore.github.io/monticore/docs/Languages/)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/opendev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://monticore.github.io/monticore/docs/BestPractices/)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)

