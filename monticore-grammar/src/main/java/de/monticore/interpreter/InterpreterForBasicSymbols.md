<!-- (c) https://github.com/MontiCore/monticore -->

<!-- Alpha-version: This is intended to become a MontiCore stable explanation. -->


This documentation is a technology-oriented extension of
[Interpreter.md](../../../../../../../monticore-runtime/src/main/java/de/monticore/interpreter/Interpreter.md)
which should be read first.
This documentation goes into technical details
and especially lists relevant classes and their purpose
about the concrete interpreter implementations.

## Given infrastructure in MontiCore 

The following classes contribute to the interpreter:

* [MCValue](../../../../../../../monticore-runtime/src/main/java/de/monticore/values/MCValue.md)
  represents interpreter values
  (encodes `int`, `long`, `String`, `List<T>`, etc.,
  higher order function types and all object types)
  in form of subclasses.
    * Functions are encoded the following way :
        * [MCValueFunctionOfModel](values/MCValueFunctionOfModel.java)
          (function declared within the model, e.g., lambdas)
        * [MCValueFunctionOfMethodHandle](values/MCValueFunctionOfMethodHandle.java)
          (native java method)
* MIFrame provides a calculation frame: It contains, e.g., local variables:
    * [MIFrameForBasicSymbols](../symbols/basicsymbols/interpreter/frames/MIFrameForBasicSymbols.java)
      (MIFrame with support for local variables)
    * [MIFrameLayoutForBasicSymbols](../symbols/basicsymbols/interpreter/frames/MIFrameLayoutForBasicSymbols.java)
      (MIFrameLayout with support for local variables;
      it stores the location of each variable in a frame)
* [AbstractInterpreterForBasicSymbols](../symbols/basicsymbols/interpreter/AbstractInterpreterForBasicSymbols.java):
  Interpreter API with support for local variables and functions
    * [ExpressionsInterpreter](../expressions/interpreter/ExpressionsInterpreter.java)
      (Example interpreter API for Expressions)
    * [StatementsInterpreter](../statements/interpreter/StatementsInterpreter.java)
      (Example interpreter API for Statements)
* [MCSignalFlowControl](signals/MCSignalFlowControl.java)
  signal that manipulates the stack according to the respective statement,
  e.g., a `return` will unroll the stack up until the function call.
    * [MCSignalBreak](signals/MCSignalBreak.java)
      (represents `break`)
    * [MCSignalContinue](signals/MCSignalContinue.java)
      (represents `continue`)
    * [MCSignalReturn](signals/MCSignalReturn.java)
      (represents `return`, may contain a return value)
* Traversers for the abstract syntax tree that is interpreted
  (serves as core infrastructure for the interpreter)
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
        * [MCLowLevelStatementsInterpreter](../statements/mclowlevelstatements/interpreter/MCLowLevelStatementsInterpreter.java)
          (handles `break`/`continue` and labels, e.g., `break outer;`)
        * [MCReturnStatementsInterpreter](../statements/mcreturnstatements/interpreter/MCReturnStatementsInterpreter.java)
          (handles return statements, e.g., `return x;`)
        * [MCVarDeclarationStatementsInterpreter](../statements/mcvardeclarationstatements/interpreter/MCVarDeclarationStatementsInterpreter.java)
          (handles variable declaration statements, e.g., `int x = 5;`)
    * [InterpreterDataForBasicSymbols](util/InterpreterDataForBasicSymbols.java)
      (data exchange between traversers with support for variables/functions)
* Utilities
    * [SymbolAccessHandler](util/SymbolAccessHandler.java)
      handles access to each type/variable/function symbol within an expression
    * [NativeStorageSelector](util/NativeStorageSelector.java)
      specifies which model type is represented in which internal format
    * [SymTypeExpression2JavaClassVisitor](util/SymTypeExpression2JavaClassVisitor.java)
      maps SymTypeExpressions to `Class<?>` objects for native Java types
    * [TypeSymbolNativityChecker](util/TypeSymbolNativityChecker.java)
      checks if a symbol is of a native Java type

## How to store/load variables/functions (to be) used in the interpreter

To use externally defined and maintained variables (respectively their values)
and functions during interpretation, two steps are needed:

1. Add the corresponding symbol
   (i.e., normally either a 'VariableSymbol' or a 'FunctionSymbol')
   to the symbol table.
   Then the symbol is available for the CoCos
   and thus can be used in, e.g., the expression that is to be interpreted.
2. Before the interpreter starts the values in the interpreter
   need to be set accordingly using `addVariable`. 
   The following code shows an example:

<!-- copy of InterpreterDocuTest.java -->
```java
// add Symbol "j" to the symbol table, e.g., in the global scope
VariableSymbol jSymbol = BasicSymbolsMill.variableSymbolBuilder()
        .setName("j")
        .setType(SymTypeExpressionFactory.createPrimitive("int"))
        .build();
BasicSymbolsMill.globalScope().add(jSymbol);

// initialize an interpreter
ASTMCBlockStatement stmt =
    parseAndCreateSymTabAndRunCoCos("int i = j++;");
StatementsInterpreter interpreter = getMyLangInterpreter();

// add the variable to the interpreters current scope
interpreter.addVariable(jSymbol, new MCValueInt(2));
interpreter.interpret(stmt);

// resolve for "i" in the model's scope
VariableSymbol iSymbol = BasicSymbolsMill.globalScope()
    .resolveVariable("i").get();
int i = interpreter.getVariable(iSymbol).asInt(); // i == 2
int j = interpreter.getVariable(jSymbol).asInt(); // j == 3, due to ++

// j is already in the interpreter's scope, use 'set' to set a new value
interpreter.setVariable(jSymbol, new MCValueInt(4));
```

## How to use Java classes in the interpreter

_Danger zone_: The interpreter is a shallow interpreter.
That means that while the abstract syntax tree of the interpreted
expression and the local variables are kept in a local environment,
it is in principle possible to access and manipulate other Java
objects in that virtual machine if access through respective symbols 
is granted.

For that simply initialize the 'Class2MC' symbol infrastructure,
which adds many symbols extracted from Java code to the symbol table.
Then the Java classes, publicly available attributes and methods
that are included in the 'Class2MC' infrastructure
can be fully used with the interpreter.
'Class2MC' can include handwritten code specifically written 
for the application as well as core frameworks
and only relies on `.class`-files.

_Is it safe?_   
__No! Adding Class2MC allows the execution of arbitrary code!__

To regain security one might consider:

* Setting a special version of the predicate of the 'Class2MCResolver'
  to filter out all classes not explicitly allowed.
    * per default, there is no explicit filter;
      all public classes are available until a filter is set.
* or: use a CoCo to check for whether the occurring model elements are indeed 
  allowed.
* or: _NOT_ initialize Class2MC
     1. explicitly add the allowed symbols to the symbol table
     2. use `addVariable`/`addFunction` to set the values

Regarding static symbols;   
if Java interoperability is used with static variables/methods,
then global values can be changed,
which in turn can influence the execution of the current program,
or different interpreters might influence each other.   
In most cases, it is recommended to not allow access to static symbols.

## Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](https://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/dev/docs/Languages.md)
* [**MontiCore Core Grammar Library
  **](https://github.com/MontiCore/monticore/blob/opendev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [TypeSystem documentation](../types3/TypeSystem3.md)
* [Best Practices](https://github.com/MontiCore/monticore/blob/dev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)

