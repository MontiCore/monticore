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
import org.junit.Before;

import java.io.IOException;
import java.util.Optional;

import static junit.framework.TestCase.*;
import static junit.framework.TestCase.assertEquals;

public abstract class AbstractInterpreterTest {

  protected static final double delta = 0.00001;

  protected CombineExpressionsWithLiteralsParser parser;
  protected CombineExpressionsWithLiteralsScopesGenitorDelegator delegator;

  protected CombineExpressionsWithLiteralsInterpreter interpreter;

  @Before
  public void before() throws IOException {
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

    final Optional<ASTFoo> optIntAssignment = parser.parse_StringFoo("bar i = 1");
    assertTrue(optIntAssignment.isPresent());
    final ASTFoo intAssignment = optIntAssignment.get();
    delegator.createFromAST(intAssignment);
    CombineExpressionsWithLiteralsMill.globalScope().getVariableSymbols().put("i",
        CombineExpressionsWithLiteralsMill.variableSymbolBuilder()
            .setType(SymTypeExpressionFactory.createPrimitive("int"))
            .setName("i")
            .setFullName("i")
            .setPackageName("")
            .setAccessModifier(AccessModifier.ALL_INCLUSION)
            .setEnclosingScope(CombineExpressionsWithLiteralsMill.globalScope())
            .build());
    interpreter.interpret(intAssignment);

    final Optional<ASTFoo> optLongAssignment = parser.parse_StringFoo("bar l = 5L");
    assertTrue(optLongAssignment.isPresent());
    final ASTFoo longAssignment = optLongAssignment.get();
    delegator.createFromAST(longAssignment);
    CombineExpressionsWithLiteralsMill.globalScope().getVariableSymbols().put("l",
        CombineExpressionsWithLiteralsMill.variableSymbolBuilder()
            .setType(SymTypeExpressionFactory.createPrimitive("long"))
            .setName("l")
            .setFullName("l")
            .setPackageName("")
            .setAccessModifier(AccessModifier.ALL_INCLUSION)
            .setEnclosingScope(CombineExpressionsWithLiteralsMill.globalScope())
            .build());
    interpreter.interpret(longAssignment);

    final Optional<ASTFoo> optCharAssignment = parser.parse_StringFoo("bar c = 'a'");
    assertTrue(optCharAssignment.isPresent());
    final ASTFoo charAssignment = optCharAssignment.get();
    delegator.createFromAST(charAssignment);
    CombineExpressionsWithLiteralsMill.globalScope().getVariableSymbols().put("c",
        CombineExpressionsWithLiteralsMill.variableSymbolBuilder()
            .setType(SymTypeExpressionFactory.createPrimitive("char"))
            .setName("c")
            .setFullName("c")
            .setPackageName("")
            .setAccessModifier(AccessModifier.ALL_INCLUSION)
            .setEnclosingScope(CombineExpressionsWithLiteralsMill.globalScope())
            .build());
    interpreter.interpret(charAssignment);

    final Optional<ASTFoo> optStringAssignment = parser.parse_StringFoo("bar s = \"hello\"");
    assertTrue(optStringAssignment.isPresent());
    final ASTFoo stringAssignment = optStringAssignment.get();
    delegator.createFromAST(stringAssignment);
    CombineExpressionsWithLiteralsMill.globalScope().getVariableSymbols().put("s",
        CombineExpressionsWithLiteralsMill.variableSymbolBuilder()
            .setType(SymTypeExpressionFactory.createPrimitive("String"))
            .setName("s")
            .setFullName("s")
            .setPackageName("")
            .setAccessModifier(AccessModifier.ALL_INCLUSION)
            .setEnclosingScope(CombineExpressionsWithLiteralsMill.globalScope())
            .build());
    interpreter.interpret(stringAssignment);

    final Optional<ASTFoo> optFloatAssignment = parser.parse_StringFoo("bar f = 1.5f");
    assertTrue(optFloatAssignment.isPresent());
    final ASTFoo floatAssignment = optFloatAssignment.get();
    delegator.createFromAST(floatAssignment);
    CombineExpressionsWithLiteralsMill.globalScope().getVariableSymbols().put("f",
        CombineExpressionsWithLiteralsMill.variableSymbolBuilder()
            .setType(SymTypeExpressionFactory.createPrimitive("float"))
            .setName("f")
            .setFullName("f")
            .setPackageName("")
            .setAccessModifier(AccessModifier.ALL_INCLUSION)
            .setEnclosingScope(CombineExpressionsWithLiteralsMill.globalScope())
            .build());
    interpreter.interpret(floatAssignment);

    final Optional<ASTFoo> optDoubleAssignment = parser.parse_StringFoo("bar d = 3.14");
    assertTrue(optDoubleAssignment.isPresent());
    final ASTFoo doubleAssignment = optDoubleAssignment.get();
    delegator.createFromAST(doubleAssignment);
    CombineExpressionsWithLiteralsMill.globalScope().getVariableSymbols().put("d",
        CombineExpressionsWithLiteralsMill.variableSymbolBuilder()
            .setType(SymTypeExpressionFactory.createPrimitive("double"))
            .setName("d")
            .setFullName("d")
            .setPackageName("")
            .setAccessModifier(AccessModifier.ALL_INCLUSION)
            .setEnclosingScope(CombineExpressionsWithLiteralsMill.globalScope())
            .build());
    interpreter.interpret(doubleAssignment);

    final Optional<ASTFoo> optBooleanAssignment = parser.parse_StringFoo("bar b = true");
    assertTrue(optBooleanAssignment.isPresent());
    final ASTFoo booleanAssignment = optBooleanAssignment.get();
    delegator.createFromAST(booleanAssignment);
    CombineExpressionsWithLiteralsMill.globalScope().getVariableSymbols().put("b",
        CombineExpressionsWithLiteralsMill.variableSymbolBuilder()
            .setType(SymTypeExpressionFactory.createPrimitive("boolean"))
            .setName("b")
            .setFullName("b")
            .setPackageName("")
            .setAccessModifier(AccessModifier.ALL_INCLUSION)
            .setEnclosingScope(CombineExpressionsWithLiteralsMill.globalScope())
            .build());
    interpreter.interpret(booleanAssignment);
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
    final Optional<ASTFoo> optAST = parser.parse_StringFoo("bar " + expr);
    assertTrue(optAST.isPresent());
    final ASTFoo ast = optAST.get();
    delegator.createFromAST(ast);
    return interpreter.interpret(ast);
  }

}
