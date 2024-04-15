<!-- (c) https://github.com/MontiCore/monticore -->

<!-- Alpha-version: This is intended to become a MontiCore stable explanation. -->

A type system is as set of rules that assign types to terms,
e.g., the type `int` can be assigned to the literal `2`. 
In Monticore, the type system implementations assign SymTypeExpressions to
expressions (e.g., `2`) and type identifiers (e.g., `int`).
This is made possible first and foremost by traversing the AST
of the expressions and type identifiers,
calculating the SymTypeExpressions of its subnodes,
and combining their information to the SymTypeExpression currently calculated.

## Given infrastructure in MontiCore

* [Type4Ast](Type4Ast.java)
  (maps ASTNodes to SymTypeExpressions, filled by the TypeVisitors)
* [SymTypeExpression](../types/check/SymTypeExpression.java)
  (calculated by the TypeVisitors, represents types and "pseudo-types")
    * [ISymTypeVisitor](ISymTypeVisitor.java)
      (interface for traversal of SymTypeExpressions)
    * [SymTypeArray](../types/check/SymTypeArray.java)
      (subclass of SymTypeExpression, represents arrays,
      e.g., `int[2]`)
    * [SymTypeObscure](../types/check/SymTypeObscure.java)
      (subclass of SymTypeExpression, pseudo-type representing typing errors)
    * [SymTypeOfFunction](../types/check/SymTypeOfFunction.java)
      (subclass of SymTypeExpression, represents functions,
      e.g., `int -> void`)
    * [SymTypeOfGenerics](../types/check/SymTypeOfGenerics.java)
      (subclass of SymTypeExpression,
      represents (non-primitive) generic nominal data types,
      e.g., `java.util.List<Person>`)
    * [SymTypeOfIntersection](../types/check/SymTypeOfIntersection.java)
      (subclass of SymTypeExpression, represents intersections of types,
      e.g., `Car & Ship`)
    * [SymTypeOfNull](../types/check/SymTypeOfNull.java)
      (subclass of SymTypeExpression, represents the null type)
    * [SymTypeOfObject](../types/check/SymTypeOfObject.java)
      (subclass of SymTypeExpression,
      represents non-primitive non-generic nominal data types,
      e.g., `java.lang.String`)
    * [SymTypeOfRegEx](../types/check/SymTypeOfRegEx.java)
      (subclass of SymTypeExpression, represents subsets of Strings,
      e.g., `R"gr(a|e)y"`)
    * [SymTypeOfUnion](../types/check/SymTypeOfUnion.java)
      (subclass of SymTypeExpression, represents unions of types,
      e.g., `TreeInnerNode | TreeLeaf`)
    * [SymTypePrimitive](../types/check/SymTypePrimitive.java)
      (subclass of SymTypeExpression, represents primitive types,
      e.g., `int`)
    * [SymTypeVariable](../types/check/SymTypeVariable.java)
      (subclass of SymTypeExpression, represents type variables,
      e.g., `T` in `List<T>`)
    * [SymTypeVoid](../types/check/SymTypeVoid.java)
      (subclass of SymTypeExpression, pseudo-type corresponding to `void`)
* [SymTypeExpressionFactory](../types/check/SymTypeExpressionFactory.java)
  (factory for creating instances of the subclasses of SymTypeExpression) 
    * [MCCollectionSymTypeFactory](../types/mccollectiontypes/types3/util/MCCollectionSymTypeFactory.java)
      (factory for CollectionTypes, convenience methods)
* [SymTypeRelations](SymTypeRelations.java)
  (relations over SymTypeExpressions, e.g., isSubTypeOf)
    * [MCCollectionSymTypeRelations](../types/mccollectiontypes/types3/MCCollectionSymTypeRelations.java)
      (relations over MCCollection SymTypeExpressions, e.g., isList)
* [WithinTypeBasicSymbolsResolver](util/WithinTypeBasicSymbolsResolver.java)
  (resolves contained fields, methods, etc. within a given type)
* TypeVisitors traverse the AST and
  store the calculated SymTypeExpression in the Type4Ast map
    * Expressions
        * [AssignmentExpressionsTypeVisitor](../expressions/assignmentexpressions/types3/AssignmentExpressionsTypeVisitor.java)
          (calculates the SymTypeExpressions for the expressions
          in the grammar AssignmentExpressions)
        * [BitExpressionsTypeVisitor](../expressions/bitexpressions/types3/BitExpressionsTypeVisitor.java)
          (calculates the SymTypeExpressions for the expressions
          in the grammar BitExpressions)
        * [CommonExpressionsTypeVisitor](../expressions/commonexpressions/types3/CommonExpressionsTypeVisitor.java)
          (calculates the SymTypeExpressions for the expressions
          in the grammar CommonExpressions)
        * [ExpressionsBasisTypeVisitor](../expressions/expressionsbasis/types3/ExpressionBasisTypeVisitor.java)
          (calculates the SymTypeExpressions for the expressions
          in the grammar ExpressionBasis)
        * [LambdaExpressionsTypeVisitor](../expressions/lambdaexpressions/types3/LambdaExpressionsTypeVisitor.java)
          (calculates the SymTypeExpressions for the expressions
          in the grammar LambdaExpressions)
    * Literals
        * [MCCommonLiteralsTypeVisitor](../literals/mccommonliterals/types3/MCCommonLiteralsTypeVisitor.java)
          (calculates the SymTypeExpressions for the literals
          in the grammar MCCommonLiterals)
        * [MCJavaLiteralsTypeVisitor](../literals/mcjavaliterals/types3/MCJavaLiteralsTypeVisitor.java)
          (calculates the SymTypeExpressions for the literals
          in the grammar MCJavaLiterals)
    * Type Identifiers
        * [MCArrayTypesTypeVisitor](../types/mcarraytypes/types3/MCArrayTypesTypeVisitor.java)
          (calculates the SymTypeExpressions for the type identifiers
          in the grammar MCArrayTypes)
        * [MCBasicTypesTypeVisitor](../types/mcbasictypes/types3/MCBasicTypesTypeVisitor.java)
          (calculates the SymTypeExpressions for the type identifiers
          in the grammar MCBasicTypes)
        * [MCCollectionTypesTypeVisitor](../types/mccollectiontypes/types3/MCCollectionTypesTypeVisitor.java)
          (calculates the SymTypeExpressions for the type identifiers
          in the grammar MCCollectionTypes)
        * [MCFullGenericTypeVisitor](../types/mcfullgenerictypes/types3/MCFullGenericTypesTypeVisitor.java)
          (calculates the SymTypeExpressions for the type identifiers
          in the grammar MCFullGenericTypes)
        * [MCFunctionTypesTypeVisitor](../types/mcfunctiontypes/types3/MCFunctionTypesTypeVisitor.java)
          (calculates the SymTypeExpressions for the type identifiers
          in the grammar MCFunctionTypes)
        * [MCSimpleGenericTypesTypeVisitor](../types/mcsimplegenerictypes/types3/MCSimpleGenericTypesTypeVisitor.java)
          (calculates the SymTypeExpressions for the type identifiers
          in the grammar MCArrayTypes)
* [TypeCheck1 Adapters](../types/check/types3wrapper/TypeCheck3AsTypeCalculator.java)
  (adapts the TypeSystem3 to the deprecated TypeCheck1 interface,
  offering implementations for IDerive and ISynthesize,
  s. [TypeCheck1 documentation](../types/check/TypeCheck.md))

## What is the difference between BasicSymbols and SymTypeExpressions?

The Type System uses the Symbols of the BasicSymbols grammar
and the handwritten SymTypeExpressions.
While they are very similar,
there is a big difference between them and when to use them.
The symbols represent definitions,
including nominal data type definitions (e.g., in Java: `class List<T>`),
while the SymTypeExpressions represent a type usage
(e.g., in Java: `List<String> listOfStrings;` or `List<T> tempList;`).
There is only one type definition,
but there can be many type usages.   
The SymTypeExpression knows its corresponding Symbol (if applicable):
* SymTypeOfGenerics, SymTypeOfObject, and SymTypePrimitive
  know their corresponding TypeSymbol
* SymTypeOfFunction _may_ have a corresponding FunctionSymbol
  (e.g., a named function declaration)
  or not (e.g., a lambda function definition)
* SymTypeVariable _may_ have a corresponding TypeVarSymbol
  (e.g., for a named type variable)
  or not (e.g., a SymTypeVariable calculated based on a wildcard)
* Other SymTypeExpressions do not have a corresponding symbol.

A type symbol,
as it _defines_ a nominal data type,
is present only once in the symbol table.
A SymTypeExpression is not stored in the symbol table
(except as an attribute of a symbol, s. e.g., VariableSymbol),
but, as far as applicable,
refers to the definitions / declarations in the symbol table.
Thus, multiple identical SymTypeExpressions can be used at the same time.

## How to use the type system implementation?

In MontiCore, the type system implementations have multiple usages.
For example:
* writing context conditions;
  The CoCos reduce a set of models to those,
  that adhere to the typing rules of the language.   
  An example would be a CoCo that checks
  for the condition of an `if`-statement to be a Boolean expression.
* printing dependent on the types;
  As an example, The model contains the expression `f(1)`
  with `f` being a variable of function type `int -> int`
  and the expression is to be printed to a Java expression.  
  In Java, functions are not first-class citizens.
  An option is to use Java's functional interfaces
  and print `f.apply(1)`.

To these ends, MontiCore's type system implementations
offer the following functionalities:

* Given an expression, the type of the expression is deduced
  (e.g., given expression `2+2`, a SymTypeExpression for `int` is created)
* Given a type identifier,
  the SymTypeExpression of the identifier is constructed
  (e.g., given MCType `int` in the model,
  a corresponding SymTypeExpression is created)
* Given one or more types, a relation is checked
  (e.g., whether an expression of type `int`
  can be assigned to a variable of type `double`)

In the first two cases,
SymTypeExpressions are assigned to ASTNodes by the use of TypeVisitors.
In the third case,
the SymTypeRelations class is queried using SymTypeExpressions.   
This implies how to select a specific type system implementation
in the first place:
To select a type system one selects a set of TypeVisitors 
and an implementation of SymTypeRelations to use.
This is described in detail further below.

### How to get the type of an ASTNode?

Types can be calculated for ASTNodes
representing either expressions (`2+2`)
or type identifiers (`String`).
First, a Type4Ast map has to be constructed to store the typing information,
thus avoiding recalculation if they are queried again,
e.g., by different CoCos.
One may assume that one could store 
Afterward creating the map,
a traverser is created with the TypeVisitors of the language,
the TypeVisitors are given the Type4Ast instance.
**Note:** Multiple type visitors,
which contain different typing rules,
may be available for a given sub-grammar,
the visitor to select is to be specified by the language.
An example of instantiating a traverser can be found 
[here](../../../../../test/java/de/monticore/types3/util/CombineExpressionsWithLiteralsTypeTraverserFactory.java).
Use the traverser to traverses the given AST and
it stores the calculated SymTypeExpressions in the map.
Finally, the map can be queried for the ASTNode in question.

As an alternativ to using the traverser and map directly,
the TypeCheck1 adapters can be used,
they can be created using the traverser and Type4Ast map.

### How to check relations on types?

To check relations of SymTypeExpressions,
the SymTypeExpressions are passed to the corresponding method
of SymTypeRelations or one of its subclasses.
A non-exhaustive List of relation methods:
* `boolean isCompatible(SymTypeExpression assignee, SymTypeExpression assigner)`
  (whether an assignment is allowed in the type system)
* `boolean isSubTypeOf(SymTypeExpression subType, SymTypeExpression)`
  (whether one type is considered a subtype of the other)
* `SymTypeExpression normalize(SymTypeExpression type)`
  (converts a SymTypeExpression into normal form)
* `boolean isInt(SymTypeExpression type)`
  (whether the type is an `int` or boxed version thereof)

It is strongly recommended to make oneself familiar
not just with the functionality offered by the
[SymTypeRelations](SymTypeRelations.java)
class,
but also its subclasses,
as they can offer further functionality like, e.g.:   
`boolean isList(SymTypeExpression type)`
(whether the type is considered a list)

As different languages have different typing rules,
the corresponding set of rules has to be selected.
While this is partially done by selecting the TypeVisitors,
relations between types are unrelated to the TypeVisitors
and have to be initialised accordingly.

As an example, the default type relations are initialized using
`SymTypeRelations.init()`.
The default typing relations are initialized per default
and calling, e.g., `OCLSymTypeRelations.init()` of the OCL language project
changes the relations according to the rules of the OCL language.
According to the default, java-inspired type relations
`List<Student>` is not a subtype of `List<Person>`
and after initializing the OCL type relations
`List<Student>` _is_ calculated to be a subtype of `List<Person>`.
Initializing the OCL type relations does not just allow
usage of `OCLSymTypeRelations`, 
but also changes the behavior of the methods in `SymTypeRelations`.
This is done to allow reuse of CoCos between languages.

## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](https://www.monticore.de/)
* [TypeCheck1 Readme](../types/check/TypeCheck.md)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/opendev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/opendev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/opendev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)
