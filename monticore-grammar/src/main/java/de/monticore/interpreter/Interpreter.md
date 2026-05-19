<!-- (c) https://github.com/MontiCore/monticore -->

<!-- Alpha-version: This is intended to become a MontiCore stable explanation. -->

Most MontiCore languages describe executable behavior.
One option to execute the behavior of MontiCore models
is by interpreting said description.
A model interpreter system is offered by MontiCore to this end.

In MontiCore an Interpreter can be used for, e.g.,
* Read-eval-print loops (REPLs)
* Custom interactions with model elements at runtime, e.g.,
  filtering a list of car objects to only display red cars.

## Given infrastructure in MontiCore

* [MIValue](values/MIValue.java)
  (represents values)
   * [MIValueFactory](values/MIValueFactory.java)
     (creates MIValues)
   * [MIValueBoolean](values/MIValueBoolean.java)
     (represents booleans)
   * [MIValueInt](values/MIValueInt.java)
     (represents integral values)
   * [MIValueDouble](values/MIValueDouble.java)
     (represents doubles)
   * [MIValueObject](values/MIValueObject.java)
     (represents native Java objects)
   * [MIValueFunction](values/MIValueFunction.java)
     (represents functions)
     * [MIValueFunctionOfModel](values/MIValueFunctionOfModel.java)
       (function declared within the model, e.g., lambdas)
     * [MIValueFunctionOfMethodHandle](values/MIValueFunctionOfMethodHandle.java)
       (native java method)
     * [MIValueVoid](values/MIValueVoid.java)
       (represents void, rarely used except for function returns)
   * [MIValueError](values/MIValueError.java)
     (An internal error occurred or an exception was thrown)
   * [MISignalFlowControl](values/MISignalFlowControl.java)
     (signal that unwinds the stack)
     * [MISignalBreak](values/MISignalBreak.java)
       (represents `break`)
     * [MISignalContinue](values/MISignalContinue.java)
       (represents `continue`)
     * [MISignalReturn](values/MISignalReturn.java)
       (represents `return`, may contain a return value)
* [MIFrame](frames/MIFrame.java)
  (runtime frame that stores variables, e.g.,
  calling a function creates a new frame)
    * [MIFrameLayout](frames/MIFrameLayout.java)
      (layouts and creates variable accessors for frames)
* [MICalculation](calculations/MICalculation.java)
  (represents the calculation done during execution;
  this is a callable function)
   * [MICalculationValue](calculations/MICalculationValue.java)
     (calculation for any value excluding the exceptions below)
   * [MICalculationBoolean](calculations/MICalculationBoolean.java)
     (specialized calculation returning `boolean` to avoid boxing)
   * [MICalculationInt](calculations/MICalculationInt.java)
     (specialized calculation returning `int` to avoid boxing)
   * [MICalculationDouble](calculations/MICalculationDouble.java)
     (specialized calculation returning `double` to avoid boxing)
   * [MICalculationVoid](calculations/MICalculationVoid.java)
     (specialized calculation without return value.
     Used for most cases that are not expressions)
* [MISetter](setters/MISetter.java)
  (represents the write access to an LValue,
  e.g., assigning to a variable)
   * [MISetterValue](setters/MISetterValue.java)
     (setter for any value excluding the exceptions below)
   * [MISetterBoolean](setters/MISetterBoolean.java)
     (specialized setter for `boolean` to avoid boxing)
   * [MISetterInt](setters/MISetterInt.java)
     (specialized setter for `int` to avoid boxing)
   * [MISetterDouble](setters/MISetterDouble.java)
     (specialized setter for `double` to avoid boxing)
* [ExpressionsInterpreter](../expressions/interpreter/ExpressionsInterpreter.java)
  (Class to execute interpretation of expressions
  without directly using the traverser)
   * [StatementsInterpreter](../statements/interpreter/StatementsInterpreter.java)
     (extension of the Expression Interpreter that supports statements)
* Traversers
   * Expressions
      * [AssignmentExpressionsInterpreter](../expressions/assignmentexpressions/interpreter/AssignmentExpressionsInterpreter.java)
        (handles assignment expressions, e.g., `a = 5`)
      * [BitExpressionsInterpreter](../expressions/bitexpressions/interpreter/BitExpressionsInterpreter.java)
        (handles bit expressions, e.g., `a & b`)
      * [CommonExpressionsInterpreter](../expressions/commonexpressions/interpreter/CommonExpressionsInterpreter.java)
        (handles common expressions, e.g., `a + b`, `myPerson.getAge()`)
      * [ExpressionsBasisInterpreter](../expressions/expressionsbasis/interpreter/ExpressionsBasisInterpreter.java)
        (handles basic expressions, e.g., `myPerson`)
         * [SymbolAccessHandler](../expressions/expressionsbasis/interpreter/SymbolAccessHandler.java)
           (handles access to any type/variable/function symbols
           within expressions)
      * [LambdaExpressionsInterpreter](../expressions/lambdaexpressions/interpreter/LambdaExpressionsInterpreter.java)
        (handles lambda expressions, e.g., `(int x) -> x + 1`)
      * [ExpressionCalculationLogVisitor](../expressions/expressionsbasis/interpreter/ExpressionCalculationLogVisitor.java)
        (Adds additional logging during execution. Slows down the interpreter.
        only add during development)
   * Literals
      * [MCCommonLiteralsInterpreter](../literals/mccommonliterals/interpreter/MCCommonLiteralsInterpreter.java)
        (handles common literals, e.g., `5`)
   * Statements
      * [MCAssertStatementsInterpreter](../statements/mcassertstatements/interpreter/MCAssertStatementsInterpreter.java)
        (handles assert statements, e.g., `assert x > 0;`)
      * [MCCommonStatementsInterpreter](../statements/mccommonstatements/interpreter/MCCommonStatementsInterpreter.java)
        (handles common statements, e.g., `if`, incomplete)
      * [MCReturnStatementsInterpreter](../statements/mcreturnstatements/interpreter/MCReturnStatementsInterpreter.java)
        (handles return statements, e.g., `return x;`)
      * [MCVarDeclarationStatementsInterpreter](../statements/mcvardeclarationstatements/interpreter/MCVarDeclarationStatementsInterpreter.java)
        (handles variable declaration statements, e.g., `int x = 5;`)
   * [InterpreterData](util/InterpreterData.java)
     (data exchange between traversers, e.g., MICalculations)
      * [TraverserAndIData](util/TraverserAndIData.java)
        (traverser + InterpreterData)
* Utility
   * [NativeStorageSelector](util/NativeStorageSelector.java)
     (specifies which model type is represented in which internal format)
   * [SymTypeExpression2JavaClassVisitor](util/SymTypeExpression2JavaClassVisitor.java)
     (maps SymTypeExpressions to `Class<?>` objects for native Java types)
   * [TypeSymbolNativityChecker](util/TypeSymbolNativityChecker.java)
     (checks if a symbol is of a native Java type)

## Basic Structure of execution

The interpreter is split into two phases:

0. Prerequisites:
   An AST with symbol table is created and CoCos are checked.
1. Creating an MICalculation:
   The AST is traversed to create an MICalculation
   that represents the behavior of the model.
2. Executing the Calculation:
   The MICalculation is executed;
   Side effects occur and a value is returned (depending on the model).

This concept is based on
"Efficient Hosted Interpreters on the JVM",
however, this is a static approach;
The AST is traversed only once to create a parallel structure
of calculation objects (usually a tree of lambdas).
Any static decision (e.g., which version of `+` to use)
is done only once, even if the same node is interpreted in a loop.
Thus, The calculation tree contains mostly constants
and simple(r) calculations
and can thus can be way better optimized by the JVM's JIT compiler.

Similarly, `MIFrame`s, instead of using `Map`s,
use fixed-size arrays to store variables.
The position of each variable within those arrays
is calculated once by `MIFrameLayouter`,
Using these fixed positions,
optimized `MICalculations` that load a variable are created.

TLDR: If you can do it once, only do it once.

## How to Interpret a Model

The interpreter is a collection of handlers traversing the AST.
The AST has to have a symbol table and must be of a valid model (CoCos).
Each language should provide its own class(es)
offering access to interpretation in a language specific way,
e.g., for `ExpressionsInterpreter`:

Combine all applicable Handlers above into one traverser of your language.
Initialize the `ExpressionsInterpreter` with said traverser.
Call `interpret` on a valid expression with symbol table.

```java
// setup, done once (simplified)
InterpreterData data = new InterpreterData();
MyLangTraverser traverser = new MyLangMill.inheritanceTraverser();
traverser.setExpressionsBasisHandler(new ExpressionsBasisHandler(data)); // etc.
ExpressionsInterpreter interpreter = new ExpressionsInterpreter(traverser, data);

// for each model
ASTNode expr = parseAndCreateSymTabAndRunCoCos("1 + 3");
MIValue result = interpreter.interpret(expr);
  // check that no error occured with isError()
if (result.isInt()) {
  System.out.println("1 + 3 = " + result.asInt());
}
```

## How to store/load variables/functions (to be) used in the interpreter

To use custom values during interpretation,
first, add the corresponding symbols (VariableSymbol/FunctionSymbol)
to the symbol table, as they have to be available for the CoCos to pass.

Afterwards, set the values in the interpreter accordingly;
E.g., for `StatementsInterpreter`:

```java
// add j to the symbol table
ASTNode stmt = parseAndCreateSymTabAndRunCoCos("int i = j++;");
ExpressionsInterpreter interpreter = getMyLangInterpreter();
// add the variable to the interpreters current scope
interpreter.addVariable(jSymbol, MIValueFactory.createMIValue(2));
interpreter.interpret(stmt);
VariableSymbol iSymbol = // resolve for "i" in the model's scope
int i = interpreter.getVariable(iSymbol).asInt(); // i == 2
int j = interpreter.getVariable(jSymbol).asInt(); // j == 3
// j is already in the interpreter's scope, use 'set' to set a new value
interpreter.setVariable(jSymbol, MIValueFactory.createValue(4));
```

## How to use Java Classes in the Interpreter

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
3. use `addVariable`/`addFunction` to set the values
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
