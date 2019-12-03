In MontiCore, the TypeCheck is used to calculate the type of a set of expressions.
This happens by traversing the AST of an expression, calculating the types of its
sub-expressions and combining them to the type of the main expression.

### Given Classes in MontiCore

* [TypeCheck](TypeCheck.java) (facade for using the TypeCheck)
* [DeriveSymTypeOfExpression](DeriveSymTypeOfExpression.java) (calculate a type for the expressions in ExpressionsBasis)
* [DeriveSymTypeOfCommonExpressions](DeriveSymTypeOfCommonExpressions.java) (calculate a type for the expressions in CommonExpressions)
* [DeriveSymTypeOfAssignmentExpressions](DeriveSymTypeOfAssignmentExpressions.java) (calculate a type for the expressions in AssignmentExpressions)
* [DeriveSymTypeOfBitExpressions](DeriveSymTypeOfBitExpressions.java) (calculate a type for the expressions in BitExpressions)
* [DeriveSymTypeOfLiterals](DeriveSymTypeOfLiterals.java) (calculate a type for the literals in LiteralsBasis)
* [DeriveSymTypeOfMCCommonLiterals](DeriveSymTypeOfMCCommonLiterals.java) (calculate a type for the literals in MCCommonLiterals)
* [SynthesizeSymTypeFromMCBasicTypes](SynthesizeSymTypeFromMCBasicTypes.java) (calculate a type for the types in MCBasicTypes)
* [SynthesizeSymTypeFromMCCollectionTypes](SynthesizeSymTypeFromMCCollectionTypes.java) (calculate a type for the types in MCCollectionTypes)
* [SynthesizeSymTypeFromMCSimpleGenericTypes](SynthesizeSymTypeFromMCSimpleGenericTypes.java) (calculate a type for the types in MCSimpleGenericTypes)

### I want to write a CoCo for my language that uses the TypeCheck - How?

You can use the TypeCheck facade. The facade needs a Derive-Class (for expressions
and literals) and/or a Synthesize-Class (for types) and calculates the type of your
given expressions/literals/types.
<br/><br/>
Create a DelegatorVisitor which combines all expression grammars and literal grammars
used by your language. The DelegatorVisitor has to implement the Interface
ITypesCalculator. Use this Delegator as Derive-Class in the TypeCheck facade. The
Synthesize-Class depends on the types grammar you use (see above-mentioned classes).
For an example of the Delegator-Visitor see [here](../../../../../../test/java/de/monticore/types/check/DeriveSymTypeOfCombineExpressions.java).
<br/><br/>
If you want to create a Derive-Class for your expression/literal grammar, you have to
extend the Derive-Class of the supergrammar and implement the normal visitor of 
your language. There you can override the traverse methods for your expressions/literals
so that it calculates the type of the expression/literal (see implementation in one of the
above-mentioned classes). You can add your visitor to the DelegatorVisitor later on.
For an example of a Derive-Class see [here](DeriveSymTypeOfCommonExpressions.java).
<br/><br/>
Writing a CoCo to check the correctness of your type/expression/literal should be easy now that you
have the TypeCheck facade to use. Just use the correct Derive-Class and/or the correct
Synthesize-Class as parameters and check if the type of your expression or type is 
correctly calculated. <br/><br/>
Example for a CoCo:
```java
@Override
public void check(ASTExpression expr){
    YourDeriveClass deriveClass = new YourDeriveClass(...); //instance of your Derive-Class
    YourSynthesizeClass synthesizeClass = new YourSynthesizeClass(...); //instance of your Synthesize-Class
    TypeCheck check = new TypeCheck(synthesizeClass,deriveClass); //instance of the TypeCheck-facade, parameters are your Synthesize-Class and your Derive-Class
    if("double"!=check.typeOf(expr).print()){ //test if your expression is of the correct type (here: double)
        Log.error(...); //your specified error message
    }
}
```

