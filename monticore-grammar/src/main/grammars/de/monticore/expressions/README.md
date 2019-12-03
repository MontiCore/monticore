# MontiCore - Expression-Language Modules

MC-Expressions are used to formulate mathematical and programmatic 
expressions of a set of literals. To achieve this, a system of modular and 
pluggable grammar parts are developed. 

### Given Expression languages in MontiCore

Currently, there are seven expression languages. These are
* [ExpressionsBasis](ExpressionsBasis.mc4) (basis for all of the expression languages, supports names and literals)
* [AssignmentExpressions](AssignmentExpressions.mc4) (extends ExpressionsBasis, basic assignments)
* [CommonExpressions](CommonExpressions.mc4) (extends ExpressionsBasis, common expressions like + and -)
* [BitExpressions](BitExpressions.mc4) (extends ExpressionsBasis, bit expressions like & or <<)
* [SetExpressions](SetExpressions.mc4) (extends ExpressionsBasis, ideal for working with sets)
* [OCLExpressions](OCLExpressions.mc4) (extends ExpressionsBasis, introduces OCL to MontiCore)
* [JavaClassExpressions](JavaClassExpressions.mc4) (extends CommonExpressions, adds Java expressions like new)

### Using Expressions

If you want to use one (or more) of the given expression languages in your
language, then all you have to do is extend it (or them) in your grammar. 
You are free to use any of them now.

### Creating your own Expression language

There are some expressions you need desperately and that are not covered 
in the given expression languages? <br/>
Create a new grammar that extends at least ExpressionsBasis. In this 
grammar, you can add your own expressions. These expressions must implement
the interface Expression in the ExpressionsBasis grammar. 
To include these expressions in your language, just extend the new grammar in your language.
See [here](CombineExpressionsWithLiterals.mc4) 
for an example.