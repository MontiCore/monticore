# MontiCore - The TypeCheck

In MontiCore, the TypeCheck is used to calculate the type of a set of expressions.
This happens by traversing the AST of an expression, calculating the types of its
sub-expressions and combining them to the type of the main expression.

### Given Classes in MontiCore

* [TypeCheck](monticore-grammar/src/main/java/de/monticore/types/check/TypeCheck.java) (facade for using the TypeCheck)
* [DeriveSymTypeOfExpression](monticore-grammar/src/main/java/de/monticore/types/check/DeriveSymTypeOfExpression) (calculate a type for the expressions in ExpressionsBasis)
* [DeriveSymTypeOfCommonExpressions](monticore-grammar/src/main/java/de/monticore/types/check/DeriveSymTypeOfCommonExpressions) (calculate a type for the expressions in CommonExpressions)
* [DeriveSymTypeOfAssignmentExpressions](monticore-grammar/src/main/java/de/monticore/types/check/DeriveSymTypeOfAssignmentExpressions) (calculate a type for the expressions in AssignmentExpressions)
* [DeriveSymTypeOfBitExpressions](monticore-grammar/src/main/java/de/monticore/types/check/DeriveSymTypeOfBitExpressions) (calculate a type for the expressions in BitExpressions)
* [DeriveSymTypeOfLiterals](monticore-grammar/src/main/java/de/monticore/types/check/DeriveSymTypeOfLiterals) (calculate a type for the literals in LiteralsBasis)
* [DeriveSymTypeOfMCCommonLiterals](monticore-grammar/src/main/java/de/monticore/types/check/DeriveSymTypeOfMCCommonLiterals) (calculate a type for the literals in MCCommonLiterals)
* [SynthesizeSymTypeFromMCBasicTypes](monticore-grammar/src/main/java/de/monticore/types/check/SynthesizeSymTypeFromMCBasicTypes) (calculate a type for the types in MCBasicTypes)
* [SynthesizeSymTypeFromMCCollectionTypes](monticore-grammar/src/main/java/de/monticore/types/check/SynthesizeSymTypeFromMCCollectionTypes) (calculate a type for the types in MCCollectionTypes)
* [SynthesizeSymTypeFromMCSimpleGenericTypes](monticore-grammar/src/main/java/de/monticore/types/check/SynthesizeSymTypesFromMCSimpleGenericTypes) (calculate a type for the types in MCSimpleGenericTypes)

### I want to write a CoCo for my language that uses the TypeCheck - How?

You can use the TypeCheck facade. The facade needs a Derive-Class (for expressions
and literals) and/or a Synthesize-Class (for types) and calculates the type of your
given expressions/literals/types.
<br/><br/>
Create a DelegatorVisitor which combines all expression grammars and literal grammars
used by your language. The DelegatorVisitor has to implement the Interface
ITypesCalculator. Use this Delegator as Derive-Class in the TypeCheck facade. The
Synthesize-Class depends on the types grammar you use (see above-mentioned classes).
<br/><br/>
If you want to create a Derive-Class for your expression/literal grammar, you have to
extend the Derive-Class of the supergrammar and implement the normal visitor of 
your language. There you can override the traverse methods for your expressions/literals
so that it calculates the type of the expression/literal (see implementation in one of the
above-mentioned classes). You can add your visitor to the DelegatorVisitor later on.
<br/><br/>
Writing a CoCo to check the correctness of the results should be easy now that you
have the TypeCheck facade to use. Just use the correct Derive-Class and the correct
Synthesize-Class as parameters and check if the type of your expression or type is 
correctly calculated.

