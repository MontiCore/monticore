/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._ast.ASTFoo;
import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.CombineExpressionsWithLiteralsScopesGenitorDelegator;
import de.monticore.expressions.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsInterpreter;
import de.monticore.interpreter.Value;
import de.monticore.interpreter.values.NotAValue;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;

import java.io.IOException;
import java.util.Optional;

import static junit.framework.TestCase.*;
import static junit.framework.TestCase.assertEquals;

public abstract class AbstractInterpreterTest {

  protected static final double delta = 0.00001;

  protected static final int BOOL = 1;
  protected static final int INT = 2;
  protected static final int LONG = 4;
  protected static final int FLOAT = 8;
  protected static final int DOUBLE = 16;
  protected static final int CHAR = 32;
  protected static final int STRING = 64;

  protected CombineExpressionsWithLiteralsInterpreter interpreter;
  protected CombineExpressionsWithLiteralsParser parser;
  protected CombineExpressionsWithLiteralsScopesGenitorDelegator delegator;

  public void init(int values) {
    CombineExpressionsWithLiteralsMill.reset();
    CombineExpressionsWithLiteralsMill.init();
    BasicSymbolsMill.initializePrimitives();
    BasicSymbolsMill.initializeString();
    LogStub.init();
    Log.clearFindings();
    Log.enableFailQuick(false);

    parser = CombineExpressionsWithLiteralsMill.parser();
    delegator = CombineExpressionsWithLiteralsMill.scopesGenitorDelegator();
    interpreter = new CombineExpressionsWithLiteralsInterpreter();

    try {
      if ((values & BOOL) != 0) {
        initBool();
      }

      if ((values & INT) != 0) {
        initInt();
      }

      if ((values & LONG) != 0) {
        initLong();
      }

      if ((values & FLOAT) != 0) {
        initFloat();
      }

      if ((values & DOUBLE) != 0) {
        initDouble();
      }

      if ((values & CHAR) != 0) {
        initChar();
      }

      if ((values & STRING) != 0) {
        initString();
      }
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }

  protected void initBool() throws IOException {
    final Optional<ASTFoo> optAST = parser.parse_String("bar b = true");
    assertTrue(optAST.isPresent());
    final ASTFoo ast = optAST.get();
    delegator.createFromAST(ast);
    CombineExpressionsWithLiteralsMill.globalScope().getVariableSymbols().put("b",
        CombineExpressionsWithLiteralsMill.variableSymbolBuilder()
            .setType(SymTypeExpressionFactory.createPrimitive("boolean"))
            .setName("b")
            .setFullName("b")
            .setPackageName("")
            .setAccessModifier(AccessModifier.ALL_INCLUSION)
            .setEnclosingScope(CombineExpressionsWithLiteralsMill.globalScope())
            .build());
    interpreter.interpret(ast);
  }

  protected void initInt() throws IOException {
    final Optional<ASTFoo> optAST = parser.parse_String("bar i = 1");
    assertTrue(optAST.isPresent());
    final ASTFoo ast = optAST.get();
    delegator.createFromAST(ast);
    CombineExpressionsWithLiteralsMill.globalScope().getVariableSymbols().put("i",
        CombineExpressionsWithLiteralsMill.variableSymbolBuilder()
            .setType(SymTypeExpressionFactory.createPrimitive("int"))
            .setName("i")
            .setFullName("i")
            .setPackageName("")
            .setAccessModifier(AccessModifier.ALL_INCLUSION)
            .setEnclosingScope(CombineExpressionsWithLiteralsMill.globalScope())
            .build());
    interpreter.interpret(ast);
  }

  protected void initLong() throws IOException {
    final Optional<ASTFoo> optAST = parser.parse_String("bar l = 5L");
    assertTrue(optAST.isPresent());
    final ASTFoo ast = optAST.get();
    delegator.createFromAST(ast);
    CombineExpressionsWithLiteralsMill.globalScope().getVariableSymbols().put("l",
        CombineExpressionsWithLiteralsMill.variableSymbolBuilder()
            .setType(SymTypeExpressionFactory.createPrimitive("long"))
            .setName("l")
            .setFullName("l")
            .setPackageName("")
            .setAccessModifier(AccessModifier.ALL_INCLUSION)
            .setEnclosingScope(CombineExpressionsWithLiteralsMill.globalScope())
            .build());
    interpreter.interpret(ast);
  }

  protected void initFloat() throws IOException {
    final Optional<ASTFoo> optAST = parser.parse_String("bar f = 1.5f");
    assertTrue(optAST.isPresent());
    final ASTFoo ast = optAST.get();
    delegator.createFromAST(ast);
    CombineExpressionsWithLiteralsMill.globalScope().getVariableSymbols().put("f",
        CombineExpressionsWithLiteralsMill.variableSymbolBuilder()
            .setType(SymTypeExpressionFactory.createPrimitive("float"))
            .setName("f")
            .setFullName("f")
            .setPackageName("")
            .setAccessModifier(AccessModifier.ALL_INCLUSION)
            .setEnclosingScope(CombineExpressionsWithLiteralsMill.globalScope())
            .build());
    interpreter.interpret(ast);
  }

  protected void initDouble() throws IOException {
    final Optional<ASTFoo> optAST = parser.parse_String("bar d = 3.14");
    assertTrue(optAST.isPresent());
    final ASTFoo ast = optAST.get();
    delegator.createFromAST(ast);
    CombineExpressionsWithLiteralsMill.globalScope().getVariableSymbols().put("d",
        CombineExpressionsWithLiteralsMill.variableSymbolBuilder()
            .setType(SymTypeExpressionFactory.createPrimitive("double"))
            .setName("d")
            .setFullName("d")
            .setPackageName("")
            .setAccessModifier(AccessModifier.ALL_INCLUSION)
            .setEnclosingScope(CombineExpressionsWithLiteralsMill.globalScope())
            .build());
    interpreter.interpret(ast);
  }

  protected void initChar() throws IOException {
    final Optional<ASTFoo> optAST = parser.parse_String("bar c = 'a'");
    assertTrue(optAST.isPresent());
    final ASTFoo ast = optAST.get();
    delegator.createFromAST(ast);
    CombineExpressionsWithLiteralsMill.globalScope().getVariableSymbols().put("c",
        CombineExpressionsWithLiteralsMill.variableSymbolBuilder()
            .setType(SymTypeExpressionFactory.createPrimitive("char"))
            .setName("c")
            .setFullName("c")
            .setPackageName("")
            .setAccessModifier(AccessModifier.ALL_INCLUSION)
            .setEnclosingScope(CombineExpressionsWithLiteralsMill.globalScope())
            .build());
    interpreter.interpret(ast);
  }

  protected void initString() throws IOException {
    final Optional<ASTFoo> optAST = parser.parse_String("bar s = \"hello\"");
    assertTrue(optAST.isPresent());
    final ASTFoo ast = optAST.get();
    delegator.createFromAST(ast);
    CombineExpressionsWithLiteralsMill.globalScope().getVariableSymbols().put("s",
        CombineExpressionsWithLiteralsMill.variableSymbolBuilder()
            .setType(SymTypeExpressionFactory.createPrimitive("String"))
            .setName("s")
            .setFullName("s")
            .setPackageName("")
            .setAccessModifier(AccessModifier.ALL_INCLUSION)
            .setEnclosingScope(CombineExpressionsWithLiteralsMill.globalScope())
            .build());
    interpreter.interpret(ast);
  }

  protected void testValidExpression(String expr, Value expected) {
    Log.clearFindings();
    Value interpretationResult = null;
    try {
      interpretationResult = parseExpressionAndInterpret(expr);
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
    assertNotNull(interpretationResult);
    assertTrue(Log.getFindings().isEmpty());
    if (expected.isBoolean()) {
      assertTrue(interpretationResult.isBoolean());
      assertEquals(interpretationResult.asBoolean(), expected.asBoolean());
    } else if (expected.isInt()) {
      assertTrue(interpretationResult.isInt());
      assertEquals(interpretationResult.asInt(), expected.asInt());
    } else if (expected.isLong()) {
      assertTrue(interpretationResult.isLong());
      assertEquals(interpretationResult.asLong(), expected.asLong());
    } else if (expected.isFloat()) {
      assertTrue(interpretationResult.isFloat());
      assertEquals(interpretationResult.asFloat(), expected.asFloat(), delta);
    } else if (expected.isDouble()) {
      assertTrue(interpretationResult.isDouble());
      assertEquals(interpretationResult.asDouble(), expected.asDouble(), delta);
    } else if (expected.isChar()) {
      assertTrue(interpretationResult.isChar());
      assertEquals(interpretationResult.asChar(), expected.asChar());
    } else if (expected.isString()) {
      assertTrue(interpretationResult.isString());
      assertEquals(interpretationResult.asString(), expected.asString());
    } else if (expected.isObject()) {
      assertTrue(interpretationResult.isObject());
      assertEquals(interpretationResult.asObject(), expected.asObject());
    }
    assertTrue(Log.getFindings().isEmpty());
  }

  protected void testInvalidExpression(String expr) {
    Log.clearFindings();
    Value interpretationResult = null;
    try {
      interpretationResult = parseExpressionAndInterpret(expr);
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
    assertNotNull(interpretationResult);
    assertEquals(Log.getFindings().size(), 1);
    assertTrue(interpretationResult instanceof NotAValue);
  }

  protected Value parseExpressionAndInterpret(String expr) throws IOException {
    final Optional<ASTFoo> optAST = parser.parse_String("bar " + expr);
    assertTrue(optAST.isPresent());
    final ASTFoo ast = optAST.get();
    delegator.createFromAST(ast);
    return interpreter.interpret(ast);
  }

}
