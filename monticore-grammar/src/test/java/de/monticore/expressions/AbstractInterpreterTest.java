/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.CombineExpressionsWithLiteralsScopesGenitorDelegator;
import de.monticore.expressions.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsInterpreter;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.interpreter.MIValue;
import de.monticore.interpreter.values.ErrorMIValue;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types3.AbstractTypeVisitorTest;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static de.monticore.interpreter.MIValueFactory.createValue;
import static de.monticore.types3.util.DefsTypesForTests.inScope;
import static de.monticore.types3.util.DefsTypesForTests.variable;
import static junit.framework.TestCase.*;
import static junit.framework.TestCase.assertEquals;

public abstract class AbstractInterpreterTest extends AbstractTypeVisitorTest {

  protected static final double delta = 0.00001;

  protected CombineExpressionsWithLiteralsInterpreter interpreter;
  protected CombineExpressionsWithLiteralsScopesGenitorDelegator delegator;

  @BeforeEach
  public void init() {
    LogStub.init();
    Log.clearFindings();
    Log.enableFailQuick(false);
    
    delegator = CombineExpressionsWithLiteralsMill.scopesGenitorDelegator();
    interpreter = new CombineExpressionsWithLiteralsInterpreter();

    try {
      initBool();
      initChar();
      initByte();
      initShort();
      initInt();
      initLong();
      initFloat();
      initDouble();
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }

  protected void initBool() throws IOException {
    VariableSymbol varSymbol = variable("b", SymTypeExpressionFactory.createPrimitive("boolean"));
    inScope(CombineExpressionsWithLiteralsMill.globalScope(), varSymbol);
    interpreter.declareVariable(varSymbol, createValue(true));
  }
  
  protected void initChar() throws IOException {
    VariableSymbol varSymbol = variable("c", SymTypeExpressionFactory.createPrimitive("char"));
    inScope(CombineExpressionsWithLiteralsMill.globalScope(), varSymbol);
    interpreter.declareVariable(varSymbol, createValue('a'));
  }
  
  protected void initByte() throws IOException {
    VariableSymbol varSymbol = variable("by", SymTypeExpressionFactory.createPrimitive("byte"));
    inScope(CombineExpressionsWithLiteralsMill.globalScope(), varSymbol);
    interpreter.declareVariable(varSymbol, createValue((byte)3));
  }
  
  protected void initShort() throws IOException {
    VariableSymbol varSymbol = variable("s", SymTypeExpressionFactory.createPrimitive("short"));
    inScope(CombineExpressionsWithLiteralsMill.globalScope(), varSymbol);
    interpreter.declareVariable(varSymbol, createValue((short)256));
  }

  protected void initInt() throws IOException {
    VariableSymbol varSymbol = variable("i", SymTypeExpressionFactory.createPrimitive("int"));
    inScope(CombineExpressionsWithLiteralsMill.globalScope(), varSymbol);
    interpreter.declareVariable(varSymbol, createValue(1));
  }

  protected void initLong() throws IOException {
    VariableSymbol varSymbol = variable("l", SymTypeExpressionFactory.createPrimitive("long"));
    inScope(CombineExpressionsWithLiteralsMill.globalScope(), varSymbol);
    interpreter.declareVariable(varSymbol, createValue(5L));
  }

  protected void initFloat() throws IOException {
    VariableSymbol varSymbol = variable("f", SymTypeExpressionFactory.createPrimitive("float"));
    inScope(CombineExpressionsWithLiteralsMill.globalScope(), varSymbol);
    interpreter.declareVariable(varSymbol, createValue(1.5f));
  }

  protected void initDouble() throws IOException {
    VariableSymbol varSymbol = variable("d", SymTypeExpressionFactory.createPrimitive("double"));
    inScope(CombineExpressionsWithLiteralsMill.globalScope(), varSymbol);
    interpreter.declareVariable(varSymbol, createValue(3.14));
  }
  
  protected void testValidExpression(String expr, MIValue expected) {
    testValidExpression(expr, expected, Collections.emptyList());
  }

  protected void testValidExpression(String expr, MIValue expected, List<ImportStatement> imports) {
    Log.clearFindings();
    MIValue interpretationResult = null;
    try {
      interpretationResult = parseExpressionAndInterpret(expr, imports);
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
    assertNotNull(interpretationResult);
    if (!Log.getFindings().isEmpty()) {
      Log.printFindings();
      fail();
    }
    if (expected.isBoolean()) {
      assertTrue(interpretationResult.isBoolean());
      assertEquals(expected.asBoolean(), interpretationResult.asBoolean());
    } else if (expected.isByte()) {
      assertTrue(interpretationResult.isByte());
      assertEquals(expected.asByte(), interpretationResult.asByte());
    } else if (expected.isShort()) {
      assertTrue(interpretationResult.isShort());
      assertEquals(expected.asShort(), interpretationResult.asShort());
    } else if (expected.isChar()) {
      assertTrue(interpretationResult.isChar());
      assertEquals(expected.asChar(), interpretationResult.asChar());
    } else if (expected.isInt()) {
      assertTrue(interpretationResult.isInt());
      assertEquals(expected.asInt(), interpretationResult.asInt());
    } else if (expected.isLong()) {
      assertTrue(interpretationResult.isLong());
      assertEquals(expected.asLong(), interpretationResult.asLong());
    } else if (expected.isFloat()) {
      assertTrue(interpretationResult.isFloat());
      assertEquals(expected.asFloat(), interpretationResult.asFloat(), delta);
    } else if (expected.isDouble()) {
      assertTrue(interpretationResult.isDouble());
      assertEquals(expected.asDouble(), interpretationResult.asDouble(), delta);
    } else if (expected.isObject()) {
      assertTrue(interpretationResult.isObject());
      assertEquals(expected.asObject(), interpretationResult.asObject());
    }
    assertTrue(Log.getFindings().isEmpty());
  }
  
  protected void testInvalidExpression(String expr) {
    testInvalidExpression(expr, Collections.emptyList());
  }
  
  protected void testInvalidExpression(String expr, List<ImportStatement> imports) {
    Log.clearFindings();
    MIValue interpretationResult;
    
    try {
      interpretationResult = parseExpressionAndInterpret(expr, imports);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    
    assertNotNull(interpretationResult);
    assertFalse(Log.getFindings().isEmpty());
    assertTrue(interpretationResult.isError());
  }
  
  protected MIValue parseExpressionAndInterpret(String expr, List<ImportStatement> imports) throws IOException {
    final ASTExpression ast = parseExpr(expr);
    generateScopes(ast);
    addImports(imports);
    SymTypeExpression type = TypeCheck3.typeOf(ast);
    if (type.isObscureType()) {
      String errorMsg = "Invalid Expression: " + expr;
      Log.error(errorMsg);
      return new ErrorMIValue(errorMsg);
    }
    return interpreter.interpret(ast);
  }
  
  protected void addImports(List<ImportStatement> imports) {
    CombineExpressionsWithLiteralsMill.artifactScope().setImportsList(imports);
  }

}
