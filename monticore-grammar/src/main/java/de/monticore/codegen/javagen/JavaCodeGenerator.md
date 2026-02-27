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

* [AbstractJavaGenVisitor](AbstractJavaGenVisitor.java)
  (AbstractCodeGenVisitor with Java utility)
   * [AssignmenExpressionsJavaGenVisitor](../../expressions/assignmentexpressions/codegen/javagen/AssignmentExpressionsJavaGenVisitor.java)
     (CodeGenVisitor for AssignmentExpressions, partially implemented)
   * [BitExpressionsJavaGenVisitor](../../expressions/bitexpressions/codegen/javagen/BitExpressionsJavaGenVisitor.java)
     (CodeGenVisitor for BitExpressions, no implementation yet)
   * [CommonExpressionsJavaGenVisitor](../../expressions/commonexpressions/codegen/javagen/CommonExpressionsJavaGenVisitor.java)
     (CodeGenVisitor for CommonExpressions, partially implemented)
   * [ExpressionsBasisJavaGenVisitor](../../expressions/expressionsbasis/codegen/javagen/ExpressionsBasisJavaGenVisitor.java)
     (CodeGenVisitor for ExpressionsBasis, partially implemented)
   * [LambdaExpressionsJavaGenVisitor](../../expressions/lambdaexpressions/codegen/javagen/LambdaExpressionsJavaGenVisitor.java)
     (CodeGenVisitor for LambdaExpressions)
   * [TupleExpressionsJavaGenVisitor](../../expressions/tupleexpressions/codegen/javagen/TupleExpressionsJavaGenVisitor.java)
     (CodeGenVisitor for TupleExpressions)
   * [UglyExpressionsJavaGenVisitor](../../expressions/uglyexpressions/codegen/javagen/UglyExpressionsJavaGenVisitor.java)
     (CodeGenVisitor for UglyExpressions, only casts are implemented)
   * [MCCommonLiteralsJavaGenVisitor](../../literals/mccommonliterals/codegen/javagen/MCCommonLiteralsJavaGenVisitor.java)
     (CodeGenVisitor for MCCommonLiterals)
* [JavaGenSymTypeExpressionConverter](JavaGenSymTypeExpressionConverter.java)
  (CodeGenSymTypeExpressionConverter for conversion of types in Java)
    * [AbstractJavaTypeConverter](AbstractJavaTypeConverter.java)
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
   * [SymTypeExpression2JavaVisitor](SymTypeExpression2JavaVisitor.java)
     (used in SymTypeExpression2JavaConverter)


## Java Runtime

As the Java standard library does offer all types/functionality
required by the generated code, a 
[runtime library](../../../../../../../../monticore-libraries/javagen-runtime/JavaGen-Library.md)
is provided.
