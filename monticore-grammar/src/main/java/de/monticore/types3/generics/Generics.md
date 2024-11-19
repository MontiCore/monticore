<!-- (c) https://github.com/MontiCore/monticore -->

<!-- Alpha-version: This is intended to become a MontiCore stable explanation. -->

This is an extension of the
[TypeSystem3 documentation](../TypeSystem3.md)
focusing on the support of generics.

A generic type is a type which contains type parameters.
For example, `List<T>` is a generic type with the name `List`
and one type parameter `T`.
A generic type cannot be used in a model directly!
Instead, it needs to be instantiated by providing type arguments.
`List<int>` is an instantiation of the generic type `List<T>`,
and as such can be used in a model as, e.g., the type of a variable.
In other words, generic types are type-level functions,
that map a non-empty list of types to a new type.

For MCTypes, the type arguments have to be provided explicitly,
as with `List<int>`.
That is not the case for expressions;
`[]` is an expression that returns an empty `List`.
The type argument is implicitly inferred by using information
of the context the expression is used in.
An Example:

```
List<Person> persons = [];
List<Car> cars = [];
```

For `persons` a `List<Person>` is expected.
`List<Person>` is the _target type_ of the expression `[]`.
Therefore, (in languages that have side effects) the type argument
must be `Person`, thus resulting in the type `List<Person>` for `[]`.
The same goes for `cars`; Here, the type argument is required to be `Car`.
As can be seen, the same expression can have different types
depending on the context the expression is used in.

The implicit type arguments are inferred for

* checking the type-correctness of the expression
* providing additional information for potential CoCos
* providing additional information for code generators

To this end, MontiCore provides a type inference algorithm.

## Given infrastructure in MontiCore wrt. Type Inference

* [TypeParameterRelations](TypeParameterRelations.java)
  (relations over SymTypeExpressions wrt. generics, e.g., hasWildcards)
* [Constraint](constraints/Constraint.java)
  (statements over SymTypeExpression that have to hold
  for inference to succeed. Leads to Bounds.)
    * [SubTypingConstraint](constraints/SubTypingConstraint.java)
      (e.g., `<Student <: Person>`: one SymTypeExpression needs to be
      the subtype of another SymTypeExpression)
    * [TypeEqualityConstraint](constraints/TypeEqualityConstraint.java)
      (e.g., `<List<FV#0> = List<Person>>`: two SymTypeExpression need to be
      the same)
    * [TypeCompatibilityConstraint](constraints/TypeCompatibilityConstraint.java)
      (e.g., `<int --> FV#0>`: one SymTypeExpression needs to be compatible
      to another SymTypeExpression)
    * [ExpressionCompatibilityConstraint](constraints/ExpressionCompatibilityConstraint.java)
      (e.g., `<Expression --> float>`: the SymTypeExpression of an expression
      needs to be compatible to another SymTypeExpression)
    * [BoundWrapperConstraint](constraints/BoundWrapperConstraint.java)
      (e.g., `<Bound: FV#0 = int>`: a Bound in place of a Constraint)
* [Bound](bounds/Bound.java)
  (limits the range of inference variables, intermediate result of inference)
    * [SubTypingBound](bounds/SubTypingBound.java)
      (e.g., `FV#0 <: Person`: represents a variable to be
      a subtype/supertype of a SymTypeExpression)
    * [TypeEqualityBound](bounds/TypeEqualityBound.java)
      (e.g., `FV#0 = int`: represents a variable being a SymTypeExpression)
    * [CaptureBound](bounds/CaptureBound.java)
      (e.g., `List<CAP#1> = capture(List<FV#0>)`: represents that
      capture conversion needs to be done later)
    * [UnsatisfiableBound](bounds/UnsatisfiableBound.java)
      (`unsatisfiable`: represents that no instantiation can be found)
* Bounds and Constraints:
    * [ConstraintReduction](util/ConstraintReduction.java)
      (takes Constraints and reduces them to Bounds)
    * [BoundIncorporation](util/BoundIncorporation.java)
      (takes Bounds and finds new Constraints)
    * [BoundResolution](util/BoundResolution.java)
      (takes Bounds and resolves instantiations)
* [InferenceContext4Ast](context/InferenceContext4Ast.java)
  (maps ASTNodes to SymTypeExpressions, filled by the TypeVisitors)
    * [InferenceContext](context/InferenceContext.java)
      (stores information required for inference for one ASTNode)
    * [InferenceResult](context/InferenceResult.java)
      (stores partial results of inference)
    * [InferenceVisitorMode](context/InferenceVisitorMode.java)
      (the mode the type visitor is in,
      this changes the expected behavior wrt. inference)
* [CompileTimeTypeCalculator](util/CompileTimeTypeCalculator.java)
  (handles type inference for most visitors. Requires the inference problem
  to be representable as a function call.)
* CTTIVisitors are TypeVisitors extended with type inference,
  they only exist for expressions
    * [AssignmentExpressionsCTTIVisitor](../../expressions/assignmentexpressions/types3/AssignmentExpressionsCTTIVisitor.java)
      (calculates the SymTypeExpressions for the expressions
      in the grammar AssignmentExpressions)
    * [CommonExpressionsCTTIVisitor](../../expressions/commonexpressions/types3/CommonExpressionsCTTIVisitor.java)
      (calculates the SymTypeExpressions for the expressions
      in the grammar CommonExpressions)
    * [ExpressionsBasisCTTIVisitor](../../expressions/expressionsbasis/types3/ExpressionBasisCTTIVisitor.java)
      (calculates the SymTypeExpressions for the expressions
      in the grammar ExpressionBasis)
    * [SetExpressionsCTTIVisitor](../../ocl/setexpressions/types3/SetExpressionsCTTIVisitor.java)
      (calculates the SymTypeExpressions for the expressions
      in the grammar SetExpressions)
    * [UglyExpressionsCTTIVisitor](../../expressions/uglyexpressions/types3/UglyExpressionsCTTIVisitor.java)
      (calculates the SymTypeExpressions for the expressions
      in the grammar UglyExpressions)

## Extended SymTypeExpression World

When adding generics support, one can encounter new SymTypeExpressions;
First of all, there is
[SymTypeOfGenerics](../../types/check/SymTypeOfGenerics.java),
which directly corresponds to
[SymTypeOfObject](../../types/check/SymTypeOfObject.java)
and represents an object type with type arguments,
e.g., `List<int>` (s. above),

[SymTypeOfFunction](../../types/check/SymTypeOfFunction.java)
does not have a corresponding, e.g., SymTypeOfGenericFunction;
Instead, type arguments are calculated by comparing the
SymTypeOfFunction with the function defined by the FunctionSymbol.
S. `SymtypeOfFunction::getTypeArguments()`.

There are further types that can be encountered.
Types printed with a `#` are types that are used internally during
type calculations but cannot be explicitly used in a model.
As such, a modeler may only ever encounter them in error messages.

`"FV#{number}"` (and `CAP#{number}`, etc.) represent inference variables
(free type variables).
They are used in place of the actual type arguments,
until their instantiations are calculated using bounds.

`#TOP` (commonly written as ⊤) represents the top type,
the type that is the supertype of all types.
It is used internally to represent that there is no upper bound
that limits the set of types an inference variable can be instantiated to.
Example: `<T> T f()` here, the type variable `T` is has no upper bound
and a corresponding inference variable would use `#TOP` to represent this.

Inference variables _can_ be instantiated to `#TOP` in specific circumstances;
The return type of `f` in the statement `f();` has no bounds,
it can be any type. As such, `#TOP` is used to represent this.
More often than not, a type of `#TOP` suggests that a type/function
has not been used "to their full potential":
* some information goes unused (for `f` it is the return value), or
* e.g., in the comparison of an empty List and Set `[] == {}`
  the potential to store values in the collections goes unused.

`#BOTTOM` (commonly written as ⊥) represents the bottom type,
the type that is the subtype of all types.
Analogue to `#TOP`, `#BOTTOM` represents
the lack of a more specific lower bound.
`#Bottom` has no values.
Inference variables are never instantiated to `#BOTTOM`.

### How to get the type of an ASTNode with generics?

Note: This is an extension of the
[general documentation](../TypeSystem3.md#how-to-get-the-type-of-an-astnode),
which you should read first.

When calculating types in a language with support for generics,
additional steps have to be taken, due to the inference algorithm

* using different visitors (not necessarely, but that is usually the case)
* needing to store additional information for ASTNodes,
* and requiring access to the target types of expressions,

The additional information will be stored in the
[InferenceContext4Ast](context/InferenceContext4Ast.java)
that is shared between all visitors.

The visitors supporting type inference, so far they exist,
are usually called CTTIVisitors,
for "(extended) **c**ompile-**t**ime **t**ype **i**nference".

Example:
```java
MyLangTraverser traverser = MyLang.traverser();
Type4Ast type4Ast = new Type4Ast();
InferenceContext4Ast ctx4Ast = new InferenceContext4Ast();

// one of many type visitors
SetExpressionsCTTIVisitor visSetExpressions = new SetExpressionsCTTIVisitor();
visSetExpressions.setType4Ast(type4Ast);
visSetExpressions.setContext4Ast(ctx4Ast);
traverser.add4SetExpressions(visSetExpressions);
// due to more complex traversal during type inference,
// most CTTIVisitors need to control the order of traversal
traverser.setSetExpressionsHandler(visSetExpressions);

// create the TypeCheck3 delegate
new MapBasedTypeCheck3(traverser, type4Ast, ctx4Ast)
    .setThisAsDelegate();
```

Again, after initializing the TypeCheck3 delegate,
TypeCheck3 can be used to query SymTypeExpressions of expressions
`TypeCheck3.typeOf(expr)`,
as well as MCTypes
`TypeCheck3.symTypeFromAST(mcType)`.
However, for the expressions,
the target types are required, as far as they exist.
To pass this information to TypeCheck3,
the function `TypeCheck3.typeOf(expr, targetType)` can be used.
This, in addition to calculating the type of `expr`,
stores the target type information;
Any subsequent call to `TypeCheck3.typeOf(expr)` will have the type
according to the target type information stored by the first call.
This data is stored, as there are circumstances there the target type
information is not available (e.g., inside some CoCos).

It holds that
* Each ASTExpression has exactly one target type
* Each ASTExpression has exactly one type.
  In the case of a language with variability, special care has to be taken to
    * either reset the value cache accordingly,
    * or clone the ASTExpression
* The call to `TypeCheck3.typeOf` *with* the target type has to be the first.
  It is recommended to split the post-symbol-table-creation CoCos
  into two groups
    1. The group of CoCos that provide the target type information
    2. The group of CoCos that cannot provide the information,
       they must run after the CoCos of the first group
