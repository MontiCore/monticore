<!-- (c) https://github.com/MontiCore/monticore -->

<!-- Alpha-version: This is intended to become a MontiCore stable explanation. -->

This is a continuation of the documentation of
[expression to code generation](../CodeGenerator.md),
which should be read first.

This code generator is in an early stage of development
and does not support all features
of MontiCore expression/statement language components yet.
It's current focus are expressions,
statements ought to be added later in development.

## Given infrastructure in MontiCore

* Visitors/Traversers
    * [JavaGenVisitorState](JavaGenVisitorState.java)
      (shared state and functionality of the Java code generation visitors)
    * Literals
        * [MCCommonLiteralsJavaGenVisitor](../../literals/mccommonliterals/codegen/javagen/MCCommonLiteralsJavaGenVisitor.java)
          (JavaGenVisitor for MCCommonLiterals)
    * Expressions
        * [AssignmentExpressionsJavaGenVisitor](../../expressions/assignmentexpressions/codegen/javagen/AssignmentExpressionsJavaGenVisitor.java)
          (CodeGenVisitor for AssignmentExpressions, partially implemented)
        * [BitExpressionsJavaGenVisitor](../../expressions/bitexpressions/codegen/javagen/BitExpressionsJavaGenVisitor.java)
          (CodeGenVisitor for BitExpressions)
        * [CommonExpressionsJavaGenVisitor](../../expressions/commonexpressions/codegen/javagen/CommonExpressionsJavaGenVisitor.java)
          (CodeGenVisitor for CommonExpressions, partially implemented)
        * [ExpressionsBasisJavaGenVisitor](../../expressions/expressionsbasis/codegen/javagen/ExpressionsBasisJavaGenVisitor.java)
          (CodeGenVisitor for ExpressionsBasis, partially implemented)
        * [LambdaExpressionsJavaGenVisitor](../../expressions/lambdaexpressions/codegen/javagen/LambdaExpressionsJavaGenVisitor.java)
          (CodeGenVisitor for LambdaExpressions)
        * [OCLExpressionsJavaGenVisitor](../../ocl/oclexpressions/codegen/javagen/OCLExpressionsJavaGenVisitor.java)
          (CodeGenVisitor for OCLExpressions, partially implemented)
        * [OptionalOperatorsJavaGenVisitor](../../ocl/optionaloperators/codegen/javagen/OptionalOperatorsJavaGenVisitor.java)
          (CodeGenVisitor for OptionalOperators)
        * [SetExpressionsJavaGenVisitor](../../ocl/setexpressions/codegen/javagen/SetExpressionsJavaGenVisitor.java)
          (CodeGenVisitor for SetExpressions)
        * [StreamExpressionsJavaGenVisitor](../../expressions/streamexpressions/codegen/javagen/StreamExpressionsJavaGenVisitor.java)
          (CodeGenVisitor for StreamExpressions)
        * [TupleExpressionsJavaGenVisitor](../../expressions/tupleexpressions/codegen/javagen/TupleExpressionsJavaGenVisitor.java)
          (CodeGenVisitor for TupleExpressions)
        * [UglyExpressionsJavaGenVisitor](../../expressions/uglyexpressions/codegen/javagen/UglyExpressionsJavaGenVisitor.java)
          (CodeGenVisitor for UglyExpressions, only casts are implemented)
    * Statements
        * [MCAssertStatementsJavaGenVisitor](../../statements/mcassertstatements/codegen/javagen/MCAssertStatementsJavaGenVisitor.java)
          (CodeGenVisitor for MCAssertStatements)
        * [MCCommonStatementsJavaGenVisitor](../../statements/mccommonstatements/codegen/javagen/MCCommonStatementsJavaGenVisitor.java)
          (CodeGenVisitor for MCCommonStatements)
        * [MCLowLevelStatementsJavaGenVisitor](../../statements/mclowlevelstatements/codegen/javagen/MCLowLevelStatementsJavaGenVisitor.java)
          (CodeGenVisitor for MCLowLevelStatements)
        * [MCReturnStatementsJavaGenVisitor](../../statements/mcreturnstatements/codegen/javagen/MCReturnStatementsJavaGenVisitor.java)
          (CodeGenVisitor for MCReturnStatements)
        * [MCVarDeclarationStatementsJavaGenVisitor](../../statements/mcvardeclarationstatements/codegen/javagen/MCVarDeclarationStatementsJavaGenVisitor.java)
          (CodeGenVisitor for MCVarDeclarationStatements, simplified cases only)
    * Types
        * [MCBasicTypesJavaGenVisitor](../../types/mcbasictypes/codegen/javagen/MCBasicTypesJavaGenVisitor.java)
          (CodeGenVisitor for *all* `MCType`s)
* [JavaGenSymTypeExpressionConverter](JavaGenSymTypeExpressionConverter.java)
  (CodeGenSymTypeExpressionConverter for conversion of types in Java)
    * [AbstractJavaTypeConverter](typeconverter/AbstractJavaTypeConverter.java)
      (ICodeGenSymTypeExpressionConversionHandler with Java utility)
    * [JavaBooleanConversionHandler](typeconverter/JavaBooleanConversionHandler.java)
      (Conversion between boolean types (boxed and unboxed))
    * [JavaFunctionConversionHandler](typeconverter/JavaFunctionConversionHandler.java)
      (Conversion between function types)
    * [JavaNumericConversionHandler](typeconverter/JavaNumericConversionHandler.java)
      (Conversion between numeric types (boxed and unboxed))
    * [JavaObjectConversionHandler](typeconverter/JavaObjectConversionHandler.java)
      (Conversion between Object types)
    * [JavaTupleConversionHandler](typeconverter/JavaTupleConversionHandler.java)
      (Conversion between tuples)
* [JavaOperationPrinter](JavaOperationPrinter.java)
  (reusable printer for operators,
  based on several handler that are delegated to)
    * [JavaAssignmentOperationHandler](operationprinter/JavaAssignmentOperationHandler.java)
      (support for assignment operators)
    * [JavaEqualityOperationHandler](operationprinter/JavaEqualityOperationHandler.java)
      (support for equality operators)
    * [JavaNumericOperatorGeneratorHandler](operationprinter/JavaNumericOperationHandler.java)
      (support for Operators on numbers)
    * [JavaStringOperatorGeneratorHandler](operationprinter/JavaStringConcatenationOperationHandler.java)
      (support for String concatenation)
* [SymTypeExpression2JavaConverter](SymTypeExpression2JavaConverter.java)
  (Converts SymTypeExpression to Java types with or without type erasure)
    * [SymTypeExpressionJavaPrinterVisitor](SymTypeExpressionJavaPrinterVisitor.java)
      (prints SymTypeExpression as Java types)
    * [SymTypeExpressionBoxedJavaPrinterVisitor](SymTypeExpressionBoxedJavaPrinterVisitor.java)
      (prints SymTypeExpression as boxed Java types)
    * [SymTypeExpressionTypeErasedJavaPrinterVisitor](SymTypeExpressionTypeErasedJavaPrinterVisitor.java)
      (prints SymTypeExpression as Java types with type erasure)

## Java Runtime

As the Java standard library does offer all types/functionality
required by the generated code, a
[runtime library](../../../../../../../../monticore-libraries/javagen-runtime/JavaGen-Library.md)
is provided.
