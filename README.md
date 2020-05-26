<!-- (c) https://github.com/MontiCore/monticore -->
<center><div style="text-align:center" ><img src="mc-logo.png" /></div></center>

# MontiCore - Language Workbench And Development Tool Framework 

[MontiCore](http://www.monticore.de) is a language workbench for an efficient 
development of domain-specific languages (DSLs). It processes an extended 
grammar format which defines the DSL and generates Java components for processing 
the DSL documents. Examples for these components are parser, 
AST classes, symboltables or pretty printers.
This enables a user to rapidly define a language and use it together 
with the MontiCore-framework to build domain specific tools. 

Some MontiCore advantages are the reusability of predefined language 
components, conservative extension and composition mechanisms and an 
optimal integration of hand-written code into the generated tools. Its 
grammar languages are rather comfortable. 

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

## More Information about MontiCore

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
   This is a list of languages that can be used out of the box. Some of them
   are in development, others rather stable. Several of these languages
   are inspired by the UML/P (see [*[Rum16,Rum17]*](http://mbse.se-rwth.de/).
   These complete languages are usually composed of a number of language
   components.


## MontiCore 3-Level License on files (informal description)

Summary: This project is freely available software; you can redistribute 
the MontiCore language workbench according to the following rules.

Details: The MontiCore Languag Workbench deals with three levels of code 
(MontiCore, tool derivates, product code). Each with different 
licenses: 

* *Product code*: when you use a MontiCore tool derivate to generate 
code, the generated code is absolutely free for each form of use 
including commercial use without any license. 

* *Tool derivate*: when you derive a tool using the MontiCore language 
workbench, then you mention that it is a MontiCore derivate. There is 
no other restriction. (BSD 3 Clause license) 

* *MontiCore adaptations*: you may also freely adapt MontiCore itself, 
but then you have to mention MontiCore AND the resulting code is to be 
pushed back into this LPGL repository (LGPL license). 

As a consequence using MontiCore during development is rather flexible 
and the final products do not have any restriction.

If questions appear e.g. on building an interpreter, please contact 
monticore@se-rwth.de. 


## General disclaimer

(Repeated from the the BSD 3 Clause license): 

This software is provided by the copyright holders and contributors
"as is" and any expressed or implied warranties, including, but not limited
to, the implied warranties of merchantability and fitness for a particular
purpose are disclaimed. In no event shall the copyright holder or
contributors be liable for any direct, indirect, incidental, special,
exemplary, or consequential damages (including, but not limited to,
procurement of substitute goods or services; loss of use, data, or
profits; or business interruption) however caused and on any theory of
liability, whether in contract, strict liability, or tort (including
negligence or otherwise) arising in any way out of the use of this
software, even if advised of the possibility of such damage.


## Included Software

This product includes the following software:
* [AntLR](http://www.antlr.org/)
* [FreeMarker](http://freemarker.org/)

## Contribution 

When you want to contribute: Please make sure that your complete workspace only 
uses UNIX line endings (LF) and all files are UTF-8 without BOM. On Windows you should 
configure git to not automatically replace LF with CRLF during checkout 
by executing the following configuration: 

    git config --global core.autocrlf input
    
## Further Information

* see also [**MontiCore Reference Manual**](http://www.monticore.de/)

* [MontiCore Reference Languages](docs/DevelopedLanguages.md) - Languages Built Using MontiCore

* [Build MontiCore](docs/BuildMontiCore.md) - How to Build MontiCore

* [Getting Started](docs/GettingStarted.md) - How to Build MontiCore

* [Changelog](00.org/Explanations/CHANGELOG.md) - Release Notes

* [FAQ](00.org/Explanations/FAQ.md) - FAQ 

* [Licenses](00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md) - MontiCore 3-Level License

