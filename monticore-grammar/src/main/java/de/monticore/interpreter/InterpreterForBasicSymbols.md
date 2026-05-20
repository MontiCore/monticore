<!-- (c) https://github.com/MontiCore/monticore -->

<!-- Alpha-version: This is intended to become a MontiCore stable explanation. -->

This documentation is an extension of
[Interpreter.md](../../../../../../../monticore-runtime/src/main/java/de/monticore/interpreter/Interpreter.md)
which should be read first.
This documentation goes into more detail
about the concrete interpreter implementations in monticore-grammar.

## Given infrastructure in MontiCore

* [MCValue](../../../../../../../monticore-runtime/src/main/java/de/monticore/values/MCValue.md)
  (represents values)
    * Functions
        * [MIValueFunctionOfModel](values/MCValueFunctionOfModel.java)
          (function declared within the model, e.g., lambdas)
        * [MIValueFunctionOfMethodHandle](values/MCValueFunctionOfMethodHandle.java)
          (native java method)
* MIFrame
    * [MIFrameForBasicSymbols](../symbols/basicsymbols/interpreter/frames/MIFrameForBasicSymbols.java)
      (MIFrame with support for Variables)
    * [MIFrameLayoutForBasicSymbols](../symbols/basicsymbols/interpreter/frames/MIFrameLayoutForBasicSymbols.java)
      (MIFrameLayout with support for Variables)
* [AbstractInterpreterForBasicSymbols](../symbols/basicsymbols/interpreter/AbstractInterpreterForBasicSymbols.java)
  (Interpreter API with support for Variables/Functions)
    * [ExpressionsInterpreter](../expressions/interpreter/ExpressionsInterpreter.java)
      (Example Interpreter API for Expressions)
    * [StatementsInterpreter](../statements/interpreter/StatementsInterpreter.java)
      (Example Interpreter API for Statements)
* [MISignalFlowControl](signals/MISignalFlowControl.java)
  (signal that unwinds the stack)
    * [MISignalBreak](signals/MISignalBreak.java)
      (represents `break`)
    * [MISignalContinue](signals/MISignalContinue.java)
      (represents `continue`)
    * [MISignalReturn](signals/MISignalReturn.java)
      (represents `return`, may contain a return value)
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
        * [LambdaExpressionsInterpreter](../expressions/lambdaexpressions/interpreter/LambdaExpressionsInterpreter.java)
          (handles lambda expressions, e.g., `(int x) -> x + 1`)
        * [ExpressionCalculationLogVisitor](../expressions/expressionsbasis/interpreter/ExpressionCalculationLogVisitor.java)
          (Adds additional logging during execution.
          Slows down the interpreter.
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
    * [InterpreterDataForBasicSymbols](util/InterpreterDataForBasicSymbols.java)
      (data exchange between traversers with support for variables/functions)
* Utility
    * [SymbolAccessHandler](util/SymbolAccessHandler.java)
    (handles access to any type/variable/function symbols
    within expressions)
    * [NativeStorageSelector](util/NativeStorageSelector.java)
      (specifies which model type is represented in which internal format)
    * [SymTypeExpression2JavaClassVisitor](util/SymTypeExpression2JavaClassVisitor.java)
      (maps SymTypeExpressions to `Class<?>` objects for native Java types)
    * [TypeSymbolNativityChecker](util/TypeSymbolNativityChecker.java)
      (checks if a symbol is of a native Java type)

## How to store/load variables/functions (to be) used in the interpreter

To use custom values during interpretation,
first, add the corresponding symbols (VariableSymbol/FunctionSymbol)
to the symbol table, as they have to be available for the CoCos to pass.

Afterwards, set the values in the interpreter accordingly;
E.g., for `StatementsInterpreter`:

```java
// add j to the symbol table
ASTNode stmt = parseAndCreateSymTabAndRunCoCos("int i = j++;");
StatementsInterpreter interpreter = getMyLangInterpreter();
// add the variable to the interpreters current scope
interpreter.addVariable(jSymbol, new MIValueInt(2));
interpreter.interpret(stmt);

VariableSymbol iSymbol = // resolve for "i" in the model's scope
int i = interpreter.getVariable(iSymbol).asInt(); // i == 2
int j = interpreter.getVariable(jSymbol).asInt(); // j == 3
// j is already in the interpreter's scope, use 'set' to set a new value
interpreter.setVariable(jSymbol, new MIValueInt(4));
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
* [**MontiCore Core Grammar Library
  **](https://github.com/MontiCore/monticore/blob/opendev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [TypeSystem documentation](../types3/TypeSystem3.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/opendev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)
