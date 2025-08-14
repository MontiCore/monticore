<!-- (c) https://github.com/MontiCore/monticore -->

<!-- Alpha-version: This is intended to become a MontiCore stable explanation. -->

An Interpreter evaluates a model without the need of compilation.
E.g., an expression interpreter will evaluate the expression `1 + 3`
to the value `4`.

In MontiCore an Interpreter can be used for, e.g.,
* Read-eval-print loop (REPL)
* Custom interactions with model elements at runtime, e.g.,
  filtering a list of car objects to only display red cars.

## Given infrastructure in MontiCore

* [IModelInterpreter](../../../../../../../monticore-runtime/src/main/java/de/monticore/interpreter/IModelInterpreter.java)
  (offers `interpret` to evaluate an `ASTNode` to a value)
    * [ModelInterpreter](ModelInterpreter.java)
      (default Implementation;
      allows storing/loading of variables and functions)
* [MIValue](../../../../../../../monticore-runtime/src/main/java/de/monticore/interpreter/MIValue.java)
  (represents values as a result of interpretation)
    * [MIValueFactory](MIValueFactory.java)
      (Factory to create MIValues)
    * [BooleanMIValue](values/BooleanMIValue.java)
      (represents a `boolean` value)
    * [ByteMIValue](values/ByteMIValue.java)
      (represents a `byte` value)
    * [ShortMIValue](values/ShortMIValue.java)
      (represents a `short` value)
    * [CharMIValue](values/CharMIValue.java)
      (represents a `char` value)
    * [IntMIValue](values/IntMIValue.java)
      (represents an `int` value)
    * [LongMIValue](values/LongMIValue.java)
      (represents a `long` value)
    * [FloatMIValue](values/FloatMIValue.java)
      (represents a `float` value)
    * [DoubleMIValue](values/DoubleMIValue.java)
      (represents a `double` value)
    * [ObjectMIValue](values/ObjectMIValue.java)
      (represents a Java `Object` value, including subclasses)
    * [FunctionMIValue](../../../../../../../monticore-runtime/src/main/java/de/monticore/interpreter/values/FunctionMIValue.java)
      (represents function values, they can be executed)
        * [ModelFunctionMIValue](values/ModelFunctionMIValue.java)
          (represents a function defined by an ASTNode)
        * [JavaNonStaticMethodMIValue](values/JavaNonStaticMethodMIValue.java)
          (represents a non-static method of a Java class)
        * [JavaStaticMethodMIValue](values/JavaStaticMethodMIValue.java)
          (represents a static method of a Java class)
    * [WriteableMIValue](values/WriteableMIValue.java)
      (represents an LValue; a value can be assigned, e.g., `a` in `a = 2;`)
        * [VariableMIValue](values/VariableMIValue.java)
          (represents the value of a VariableSymbol)
        * [JavaAttributeMIValue](values/JavaAttributeMIValue.java)
          (represents the value of a Java `Object`'s attribute, e.g, `p.name`)
    * [VoidMIValue](../../../../../../../monticore-runtime/src/main/java/de/monticore/interpreter/values/VoidMIValue.java)
      (represents the lack of a value, e.g., interpretation of a statement)
    * [ErrorMIValue](../../../../../../../monticore-runtime/src/main/java/de/monticore/interpreter/values/ErrorMIValue.java)
      (represents an error during execution, e.g., interpretation of `1/0`)
    * [MIFlowControlSignal](../../../../../../../monticore-runtime/src/main/java/de/monticore/interpreter/values/MIFlowControlSignal.java)
      (represents transfer of control in the execution flow)
        * [ReturnMIValue](../../../../../../../monticore-runtime/src/main/java/de/monticore/interpreter/values/MIReturnSignal.java)
          (represents transfer of control due to a `return` statement,
          contains the value returned)
        * [BreakMIValue](../../../../../../../monticore-runtime/src/main/java/de/monticore/interpreter/values/MIBreakSignal.java)
          (represents transfer of control due to a `break` statement)
        * [ContinueMIValue](../../../../../../../monticore-runtime/src/main/java/de/monticore/interpreter/values/MIContinueSignal.java)
          (represents transfer of control due to a `continue` statement)
* [IMIScope](../../../../../../../monticore-runtime/src/main/java/de/monticore/interpreter/IMIScope.java)
  (represents a scope of execution of the interpreter)
    * [MIScope](MIScope.java)
      (implementation; contains local variables/functions)
* [MIForIterator](iterators/MIForIterator.java)
  (internal representation of an iterator of a for-loop,
  can execute the body of the loop)
    * [MICommonForIterator](iterators/MICommonForIterator.java)
      (represents the common for iterator, e.g, `int i = 5; i < 10; i++`)
    * [MIForEachIterator](iterators/MIForEachIterator.java)
      (represents the iterator of a for-each loop, e.g, `Person p : persons`)
* [InterpreterUtils](InterpreterUtils.java)
  (internal utility class for interpreter visitors; offers, e.g., casting of values)
* Interpreter Visitors contain the evaluation logic for each type of `ASTNode`
  of their corresponding grammar component.
    * Expressions
        * [AssignmentExpressionsInterpreter](../expressions/assignmentexpressions/_visitor/AssignmentExpressionsInterpreter.java)
        * BitExpressionsInterpreter is currently not implemented.
        * [CommonExpressionsInterpreter](../expressions/commonexpressions/_visitor/CommonExpressionsInterpreter.java)
        * [ExpressionBasisInterpreter](../expressions/expressionsbasis/_visitor/ExpressionsBasisInterpreter.java)
        * JavaClassExpressionsInterpreter is currently not implemented.
        * [LambdaExpressionsInterpreter](../expressions/lambdaexpressions/_visitor/LambdaExpressionsInterpreter.java)
        * [OCLExpressionsInterpreter](../ocl/oclexpressions/_visitor/OCLExpressionsInterpreter.java)
          is mostly unimplemented.
        * OptionalOperatorsInterpreter is currently not implemented.
        * RegularExpressionsInterpreter is currently not implemented.
        * [SetExpressionsInterpreter](../ocl/setexpressions/_visitor/SetExpressionsInterpreter.java)
        * StreamExpressionsInterpreter is currently no implemented.
        * TupleExpressionsInterpreter is currently not implemented.
        * [UglyExpressionsInterpreter](../expressions/uglyexpressions/_visitor/UglyExpressionsInterpreter.java)
          is currently not implemented fully.
    * Literals
        * [MCCommonLiteralsInterpreter](../literals/mccommonliterals/_visitor/MCCommonLiteralsInterpreter.java)
        * JavaLiteralsInterpreter is currently not implemented.
    * Statements
        * MCArrayStatementsInterpreter is currently not implemented.
        * [MCCommonStatementsInterpreter](../statements/mccommonstatements/_visitor/MCCommonStatementsInterpreter.java)
        * [MCLowLevelStatementsInterpreter](../statements/mclowlevelstatements/_visitor/MCLowLevelStatementsInterpreter.java)
          is mostly unimplemented
        * [MCReturnStatementsInterpreter](../statements/mcreturnstatements/_visitor/MCReturnStatementsInterpreter.java)
        * [MCVarDeclarationStatemensInterpreter](../statements/mcvardeclarationstatements/_visitor/MCVarDeclarationStatementsInterpreter.java)

## How to Interpret a Model

The interpreter is a collection of visitors traversing the AST.
The [ModelInterpreter](ModelInterpreter.java)
can be used to evaluate an `ASTNode`;   
The `ASTNode` has to have a corresponding symbol table,
and has to be of a valid model (check using CoCos).

Create a new Interpreter of your language and use
`ASTNode::evaluate` to calculate a value;
```java
ASTNode expr = parseAndCreateSymTabAndRunCoCos("1 + 3");
ModelInterpreter interpreter = new MyLangInterpreter();
MIValue result = expr.evaluate(interpreter);
// check that no error occurred with isError(),
// error will be logged already
if (result.isInt()) {
  System.out.println("1 + 3 = " + result.asInt());
}
```

## How to store/load variables/functions (to be) used in the interpreter

To use custom values during interpretation,
first, add the corresponding symbols (VariableSymbol/FunctionSymbol)
to the symbol table, as they have to be available for the CoCos to pass.

Afterwards, set the values in the interpreter accordingly;
```java
// add j to the symbol table
ModelInterpreter interpreter = new MyLangInterpreter();
// add the variable to the interpreters current scope
interpreter.declareVariable(jSymbol, MIValueFactory.createValue(2));
ASTNode statement = parseAndCreateSymTabAndRunCoCos("int i = j++;");
statement.evaluate(interpreter); // simply returns void
VariableSymbol iSymbol = // resolve for "i" in the model's scope
int i = interpreter.loadVariable(iSymbol).asInt(); // i == 2
int j = interpreter.loadVariable(jSymbol).asInt(); // j == 3
// j is already in the interpreter's scope,
// use 'store' to set a new value
interpreter.storeVariable(jSymbol, MIValueFactory.createValue(4));
```

## Sandboxing – How to use Java Classes in the Interpreter

Simply initialize Class2MC and Java classes can be used with the interpreter.

_But is it safe?_   
__No! Adding Class2MC allows the execution of arbitrary code!__

One should consider
* setting the predicate of the Class2MCResolver
  to filter out all classes not explicitly allowed.
    * currently, this cannot filter attributes/methods
* Use a CoCo to check for whether all model elements are allowed.

A safer Approach is to
1. _NOT_ initialize Class2MC
2. add all allowed symbols to the symbol table
3. use `declareVariable`/`declareFunction` to set the values
4. interpret using this limited, but safe(r), set of symbols

Regarding static symbols;   
If JavaInteroperability is used with static variables/methods,
then global values can be changed,
which in turn can influence the execution of the current program,
or different interpreters might influence each other.   
In most cases, it is recommended to not allow access to static symbols.

## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](https://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/opendev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/opendev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [TypeSystem documentation](../types3/TypeSystem3.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/opendev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)
