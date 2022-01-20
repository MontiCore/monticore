/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.cocos;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._cocos.ExpressionsBasisCoCoChecker;
import de.monticore.grammar.cocos.CocoTest;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.DeriveSymTypeOfCombineExpressionsDelegator;
import de.monticore.types.check.TypeCheck;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class ExpressionValidTest extends CocoTest {

  private static final ExpressionsBasisCoCoChecker checker = new ExpressionsBasisCoCoChecker();

  @BeforeClass
  public static void disableFailQuick() {
    LogStub.enableFailQuick(false);
    CombineExpressionsWithLiteralsMill.reset();
    CombineExpressionsWithLiteralsMill.init();
    BasicSymbolsMill.initializePrimitives();
    TypeCheck typeCheck = new TypeCheck(null, new DeriveSymTypeOfCombineExpressionsDelegator());
    checker.addCoCo(new ExpressionValid(typeCheck));
    new TypeCheck(null, new DeriveSymTypeOfCombineExpressionsDelegator());
  }

  public void checkValid(String expressionString) throws IOException {
    CombineExpressionsWithLiteralsParser parser = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> optAST = parser.parse_StringExpression(expressionString);
    assertTrue(optAST.isPresent());
    Log.getFindings().clear();
    checker.checkAll(optAST.get());
    assertTrue(Log.getFindings().isEmpty());
  }

  public void checkInvalid(String expressionString) throws IOException {
    CombineExpressionsWithLiteralsParser parser = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> optAST = parser.parse_StringExpression(expressionString);
    assertTrue(optAST.isPresent());
    Log.getFindings().clear();
    checker.checkAll(optAST.get());
    assertFalse(Log.getFindings().isEmpty());
  }

  @Test
  public void testValid() throws IOException {
    checkValid("7-4*2");
    checkValid("4/2*6%4");
    checkValid("(5<6)&&(1<=1)");
    checkValid("!true||false&&(5>=0)");
    checkValid("5.0/2.5%2");
    checkValid("5++--");
  }

  @Test
  public void testInvalid() throws IOException {
    try {
      checkInvalid("5+false");
      fail();
    } catch (Exception e) {
      // Ok
    }
    try {
      checkInvalid("true-true");
      fail();
    } catch (Exception e) {
      // Ok
    }
    try {
      checkInvalid("!false!=5");
      fail();
    } catch (Exception e) {
      // Ok
    }
    try {
      checkInvalid("5||7");
      fail();
    } catch (Exception e) {
      // Ok
    }
    try {
      checkInvalid("true++");
      fail();
    } catch (Exception e) {
      // Ok
    }
    try {
      checkInvalid("(true&&6)||(false>=37)");
      fail();
    } catch (Exception e) {
      // Ok
    }

  }
}