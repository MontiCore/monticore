<!-- (c) https://github.com/MontiCore/monticore -->

<!-- This is a MontiCore stable explanation. -->

# MontiCore - Expression-Language Modules

MC Expressions can be used to formulate mathematical and programmatic
expressions from a set of literals. MC Expressions are based on a system of
modular and pluggable grammar parts.

### Given Expression languages in MontiCore

Currently, MontiCore comprises the following expression languages:

* [ExpressionsBasis](ExpressionsBasis.mc4): Basis for all other expression
languages. Supports names and literals.
* [AssignmentExpressions](AssignmentExpressions.mc4): Extends `ExpressionsBasis` 
with basic assignments.
* [CommonExpressions](CommonExpressions.mc4): Extends `ExpressionsBasis` 
with common expressions like `+` and `-`.
* [BitExpressions](BitExpressions.mc4): Extends `ExpressionsBasis` 
with bit expressions like `&` or `<<`.
* [LambdaExpressions](LambdaExpressions.mc4): Extends `ExpressionBasis` 
with lambda expressions like `a -> a + 2`.
* [JavaClassExpressions](JavaClassExpressions.mc4): Extends `CommonExpressions` 
with Java expressions like `new`.

The OCL project defines additional expression languages:

* [OCL-OCLExpressions]: Extends `ExpressionsBasis` to introduce OCL to
MontiCore.
* [OCL-SetExpressions]: Extends `ExpressionsBasis` for working with sets using
OCL.

Furthermore, composite SI unit expressions are defined in the SI Units project:

* [SI Units](https://github.com/MontiCore/siunits/blob/master/src/main/grammars/de/monticore/SIUnits.mc4) (can be used to parse primitive units as well as their products, quotients, and powers)

### Using Expressions

To use one or more of the existing expression languages in your MontiCore-based
language its grammar needs to extend those expression languages.

### Creating your own Expression language

There are some expressions you need desperately and that are not covered
by the existing expression languages? <br/>
In this case, you can create a new grammar that extends at least
`ExpressionsBasis`. In the extending grammar, you are now free to add your own
expressions which however must implement the `Expression` interface from
`ExpressionsBasis` grammar. To then include the new expressions in a language
let it extend the corresponding grammar.
See [here](../../../../../test/grammars/de/monticore/expressions/CombineExpressionsWithLiterals.mc4)
for an example.

## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](https://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/opendev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/opendev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/opendev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)

<!-- Links to other sites-->
[OCL-OCLExpressions]: https://git.rwth-aachen.de/monticore/languages/OCL/-/blob/develop/src/main/grammars/de/monticore/ocl/OCLExpressions.mc4
[OCL-SetExpressions]: https://git.rwth-aachen.de/monticore/languages/OCL/-/blob/develop/src/main/grammars/de/monticore/ocl/SetExpressions.mc4
