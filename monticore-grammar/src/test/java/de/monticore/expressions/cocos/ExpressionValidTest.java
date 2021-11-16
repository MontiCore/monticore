/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.cocos;

import de.monticore.expressions.cocos.ExpressionValid;
import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._cocos.CombineExpressionsWithLiteralsCoCoChecker;
import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._cocos.ExpressionsBasisCoCoChecker;
import de.monticore.grammar.cocos.CocoTest;
import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.DeriveSymTypeOfCombineExpressionsDelegator;
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
  public static void disableFailQuick(){
    LogStub.enableFailQuick(false);
    CombineExpressionsWithLiteralsMill.reset();
    CombineExpressionsWithLiteralsMill.init();
    BasicSymbolsMill.initializePrimitives();
    checker.addCoCo(new ExpressionValid(new DeriveSymTypeOfCombineExpressionsDelegator()));
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
    checkInvalid("5+false");
    checkInvalid("true-true");
    checkInvalid("!false!=5");
    checkInvalid("5||7");
    checkInvalid("true++");
    // Fehler im Typecheck
    //checkInvalid("(true&&6)||(false>=37)");
  }
}