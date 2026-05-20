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

* [MCValues](../values/MCValue.md)
  (used to represent values)
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
* [AbstractInterpreter](AbstractInterpreter.java)
  (Class to execute interpretation of AST-nodes without directly using the traverser)
* [InterpreterData](util/InterpreterData.java)
  (data exchange between traversers, e.g., MICalculations)

## Basic Structure of execution

The interpreter is split into two phases:

0. Prerequisites:
   An AST (usually with symbol table) is created and CoCos are checked.
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
use fixed-size arrays to store values, e.g., variables.
The position of each value within those arrays
is calculated once,
Using these fixed positions,
optimized `MICalculations` that load a value are created.

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
InterpreterData IData = new InterpreterData();
MyLangTraverser traverser = new MyLangMill.inheritanceTraverser();
traverser.setExpressionsBasisHandler(new ExpressionsBasisHandler(iData)); // etc.
ExpressionsInterpreter interpreter = new ExpressionsInterpreter(traverser, IData);

// for each model
ASTNode expr = parseAndCreateSymTabAndRunCoCos("1 + 3");
MCValue result = interpreter.interpret(expr);
// check that no error occured with isError()
if (result.isInt()) {
    System.out.println("1 + 3 = "+result.asInt());
}
```

## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](https://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/opendev/docs/Languages.md)
* [**MontiCore Core Grammar Library
  **](https://github.com/MontiCore/monticore/blob/opendev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [TypeSystem documentation](../types3/TypeSystem3.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/opendev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)
