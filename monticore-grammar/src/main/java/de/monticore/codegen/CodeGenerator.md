<!-- (c) https://github.com/MontiCore/monticore -->

<!-- Alpha-version: This is intended to become a MontiCore stable explanation. -->

Most MontiCore languages contain expressions and types,
more often than not describing executable behavior.
One option to execute expressions of MontiCore models
is the generation of expressions in an executable target language,
such as Java.

Example:
The variable declaration

```
// adds 2 to the input number
(int, String) pair = (42, "Hello");
```

can be translated into a corresponding Java-compatible variable declaration

```java
Tuple2<Integer, String> pair = Tuple2.of(42, "Hello");
```

This CodeGenerator is not an alternative of CD-based code generation,
but an extension/part of it.
It is meant as a means to generate code of "small" model elements,
such as expressions, statements, literals, and types
and be used within CD-based code generation.

Each target language has its own code generator implementation
to accommodate the specifica of the language.
As of now, only Java code generation is supported.

[Generation of Java code](javagen/JavaCodeGenerator.md) is documented separately.

## Given infrastructure in MontiCore

* [CodeGenerator](CodeGenerator.java)
  (Provides the main interface to generate code from an AST node.)
   * [AbstractCodeGenVisitor](AbstractCodeGenVisitor.java)
     (Abstract SuperClass for CodeGenerators based on the visitor pattern)
   * [CodeGenPrintAction](CodeGenPrintAction.java)
     (represents unprinted code fragments, technical, internal class)
* [CodeGenSymTypeExpressionConverter](CodeGenSymTypeExpressionConverter.java)
  (converts SymTypeExpressions into corresponding types
  in the target language)
   * [ICodeGenSymTypeExpressionConversionHandler](ICodeGenSymTypeExpressionConversionHandler.java)
     (provides (parts of) the implementation of
    CodeGenSymTypeExpressionConverter)
* [Node2Name](util/Node2Name.java)
  (Utility; Provides unique names for ASTNodes)
