<!-- (c) https://github.com/MontiCore/monticore -->

<!-- Alpha-version: This is intended to become a MontiCore stable explanation. -->

In MontiCore, the TypeCheck is used to calculate the SymTypeExpression of a set of expressions, types and literals.
This is made possible by traversing the AST of an expression, type or literal, calculating the SymTypeExpression of its
subexpressions, -types or -literals and combining them to the SymTypeExpression of the main expression, type or literal.

### Given infrastructure in MontiCore

* [TypeCheck](TypeCheck.java) (facade for using the TypeCheck)
* [DeriveSymTypeOfExpression](DeriveSymTypeOfExpression.java) (calculate a SymTypeExpression for the expressions in the grammar ExpressionsBasis)
* [DeriveSymTypeOfCommonExpressions](DeriveSymTypeOfCommonExpressions.java) (calculate a SymTypeExpression for the expressions in the grammar CommonExpressions)
* [DeriveSymTypeOfAssignmentExpressions](DeriveSymTypeOfAssignmentExpressions.java) (calculate a SymTypeExpression for the expressions in the grammar AssignmentExpressions)
* [DeriveSymTypeOfBitExpressions](DeriveSymTypeOfBitExpressions.java) (calculate a SymTypeExpression for the expressions in the grammar BitExpressions)
* [DeriveSymTypeOfLiterals](DeriveSymTypeOfLiterals.java) (calculate a SymTypeExpression for the literals in the grammar LiteralsBasis)
* [DeriveSymTypeOfMCCommonLiterals](DeriveSymTypeOfMCCommonLiterals.java) (calculate a SymTypeExpression for the literals in the grammar MCCommonLiterals)
* [DeriveSymTypeOfMCJavaLiterals](DeriveSymTypeOfMCJavaLiterals.java) (calculate a SymTypeExpression for the literals in the grammar MCJavaLiterals)
* [SynthesizeSymTypeFromMCBasicTypes](SynthesizeSymTypeFromMCBasicTypes.java) (calculate a SymTypeExpression for the types in the grammar MCBasicTypes)
* [SynthesizeSymTypeFromMCCollectionTypes](SynthesizeSymTypeFromMCCollectionTypes.java) (calculate a SymTypeExpression for the types in the grammar MCCollectionTypes)
* [SynthesizeSymTypeFromMCSimpleGenericTypes](SynthesizeSymTypeFromMCSimpleGenericTypes.java) (calculate a SymTypeExpression for the types in the grammar MCSimpleGenericTypes)
* [SynthesizeSymTypeFromMCFullGenericTypes](SynthesizeSymTypeFromMCFullGenericTypes.java) (calculate a SymTypeExpression for the types in the grammar MCFullGenericTypes)
* [BasicSymbols](../../../../../grammars/de/monticore/symbols/BasicSymbols.mc4) (defines the symbols needed for the symboltable)
* [OOSymbols](../../../../../grammars/de/monticore/symbols/OOSymbols.mc4) (specialization of the BasicSymbols for object-oriented languages)
* [SymTypeExpression](SymTypeExpression.java) (result of the TypeCheck, represents type usage)
* [SymTypeArray](SymTypeArray.java) (subclass of SymTypeExpression, represents arrays)
* [SymTypeConstant](SymTypeConstant.java) (subclass of SymTypeExpression, represents primitive types)
* [SymTypeOfGenerics](SymTypeOfGenerics.java) (subclass of SymTypeExpression, represents generic types)
* [SymTypeOfObject](SymTypeOfObject.java) (subclass of SymTypeExpression, represents non-primitive types without type arguments)
* [SymTypeVariable](SymTypeVariable.java) (subclass of SymTypeExpression, represents type variables)
* [SymTypeOfNull](SymTypeOfNull.java) (subclass of SymTypeExpression, represents the null type)
* [SymTypeVoid](SymTypeVoid.java) (subclass of SymTypeExpression, represents the void type)
* [SymTypeOfWildcard](SymTypeOfWildcard.java) (subclass of SymTypeExpression, represents wildcard types)
* [SymTypeExpressionFactory](SymTypeExpressionFactory.java) (factory for creating the subclasses of SymTypeExpression)


### What is the difference between TypeSymbols and SymTypeExpressions?

The TypeCheck uses the TypeSymbols of the BasicSymbols grammar and the handwritten SymTypeExpressions.
While they are very similar, there is a big difference between them and when to use them.
The TypeSymbols represent a type definition (example in Java: class List<T>) while the SymTypeExpressions
represent a type usage (example in Java: List<String>). There is only one type definition, but there can
be many type usages. The SymTypeExpression knows its corresponding 
TypeSymbol (like the type usage knows its definition) and can refer to its methods and fields.

So when talking about a type definition, a type symbol, which can be stored and is
present only once in the symboltable, has to be used. A SymTypeExpression is not stored in the symboltable,
but refers to the definition of its type (its corresponding TypeSymbol) in the symboltable. Thus, a
SymTypeExpression can be used multiple times and represents the usage of a type.


### The TypeCheck facade

The TypeCheck class of MontiCore is used as a facade to make TypeChecking easier for the user.
It needs a Derive-Class and/or a Synthesize-Class to be instantiated.
Here is a list of the methods and functions of the TypeCheck.
* SymTypeExpression typeOf(ASTExpression expr) (uses the Derive-Class to derive a SymTypeExpression from an ASTExpression)
* SymTypeExpression typeOf(ASTLiteral lit) (uses the Derive-Class to derive a SymTypeExpression from an ASTLiteral)
* SymTypeExpression symTypeFromAST(ASTType type) (uses the Synthesize-Class to derive a SymTypeExpression from an ASTType)
* static boolean compatible(SymTypeExpression left, SymTypeExpression right) (checks if the type of the right SymTypeExpression would be assignable to a variable with the type of the left SymTypeExpression)
* static boolean isSubTypeOf(SymTypeExpression sub, SymTypeExpression sup) (checks if the type of sub is a subtype of the type of sup)
* static boolean isInt(SymTypeExpression sym) (there are methods like this for all primitive types, checks if the SymTypeExpression represents the type 'int')


### How does the TypeCheck work?

The TypeCheck can be used to derive a SymTypeExpression from an expression or type.
For example, when using an ASTPlusExpression '3+4', the TypeCheck returns a SymTypeExpression
representing the type 'int'. You can use the ASTPlusExpression as a parameter for the 
method typeOf of the TypeCheck facade. This method delegates the calculation to your Derive-Class.
First it derives the SymTypeExpression of the subexpressions '3' and '4' (the LiteralExpressions
used in the PlusExpression) and then it calculates the SymTypeExpression of the whole PlusExpression
by combining the SymTypeExpressions of its subexpressions in the context of the '+' operator.

In general, the derivation of SymTypeExpressions for expressions first calculates the 
SymTypeExpressions of its subexpressions and (depending on the results) then combines these
SymTypeExpression adequately to one SymTypeExpression for the whole expression.

Deriving the SymTypeExpression of a type is often easier than deriving the SymTypeExpression
of an expression. The ASTMCPrimitiveType 'int' will result in a SymTypeExpression 'int', the 
ASTMCBasicGenericType 'java.util.Map<int, String>' will result in a SymTypeExpression 
'java.util.Map<int, String>'.

The derivation of types is very similar to the derivation of expressions with regard to 
subtypes and subexpressions. If a type has subtypes (like type arguments), then the SymTypeExpressions representing
these subtypes will be calculated first. They can be used to put together the SymTypeExpression
of the whole type.

Because both types and expressions are converted into SymTypeExpressions, you can compare them.
This is useful in the next example:

```java
int a = "Hello";
```

The SymTypeExpression derived from the ASTPrimitiveType 'int' will be 'int', the SymTypeExpression
derived from the ASTLiteralExpression "Hello" will be 'String'. In a CoCo, you can check if they are compatible by
using the function 'compatible' of the TypeCheck facade.


### I want to write a CoCo for my language that uses the TypeCheck - How?

You can use the TypeCheck facade. The facade needs a Derive-Class (for expressions
and literals) and/or a Synthesize-Class (for types) and calculates the SymTypeExpression of your
given expressions/literals/types.
<br/><br/>
Create a DelegatorVisitor which combines all expression grammars and literal grammars
used by your language. The DelegatorVisitor needs to implement the Interface
IDerive. Use this Delegator as Derive-Class in the TypeCheck facade. The
Synthesize-Class depends on the types grammar you use (see above-mentioned classes).
For an example of the Delegator-Visitor see 
[here](../../../../../../test/java/de/monticore/types/check/DeriveSymTypeOfCombineExpressionsDelegator.java).
<br/><br/>
If you want to create a Derive-Class for your expression/literal grammar, you have to
extend the Derive-Class of the supergrammar and implement the standard visitor of 
your language. There you can override the traverse methods for your expressions/literals
so that it calculates the SymTypeExpression of the expression/literal (see implementation in one of the
above-mentioned classes). You can add your visitor to the DelegatorVisitor later on.
For an example of the Derive-Class for one language see [here](DeriveSymTypeOfCommonExpressions.java).
For an example of the Synthesize-Class for one language see [here](SynthesizeSymTypeFromMCCollectionTypes.java)
<br/><br/>
Writing a CoCo to check the correctness of your type/expression/literal should be easy now that you
have the TypeCheck facade to use. Just use the correct Derive-Class and/or the correct
Synthesize-Class as parameters and check if the SymTypeExpression of your expression or type is 
correctly calculated. <br/><br/>
Example for a CoCo:
```java
@Override
public void check(ASTExpression expr){
    YourDeriveClass deriveClass = new YourDeriveClass(...); //instance of your Derive-Class
    YourSynthesizeClass synthesizeClass = new YourSynthesizeClass(...); //instance of your Synthesize-Class
    TypeCheck check = new TypeCheck(synthesizeClass,deriveClass); //instance of the TypeCheck-facade, parameters are your Synthesize-Class and your Derive-Class
    if(!"double".equals(check.typeOf(expr).print())){ //test if your expression is of the correct type (here: double)
        Log.error(...); //your specified error message
    }
}
```

An example for the case that a plus expression needs to return 'int' can be found
[here](https://github.com/MontiCore/monticore/blob/dev/monticore-test/monticore-grammar-it/src/main/java/mc/typescalculator/myownlanguage/_cocos/PlusExpressionReturnsInt.java).

## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](http://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/dev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/dev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)


