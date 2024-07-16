/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.cocos;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._cocos.ExpressionsBasisCoCoChecker;
import de.monticore.grammar.cocos.CocoTest;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.FullDeriveFromCombineExpressionsWithLiterals;
import de.monticore.types.check.TypeCalculator;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

public class ExpressionValidTest extends CocoTest {

  protected ExpressionsBasisCoCoChecker checker;

  @BeforeEach
  public void init() {
    CombineExpressionsWithLiteralsMill.reset();
    CombineExpressionsWithLiteralsMill.init();
    BasicSymbolsMill.initializePrimitives();
    TypeCalculator typeCheck = new TypeCalculator(null, new FullDeriveFromCombineExpressionsWithLiterals());
    checker = new ExpressionsBasisCoCoChecker();
    checker.addCoCo(new ExpressionValid(typeCheck));
    new TypeCalculator(null, new FullDeriveFromCombineExpressionsWithLiterals());
  }

  public void checkValid(String expressionString) throws IOException {
    CombineExpressionsWithLiteralsParser parser = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> optAST = parser.parse_StringExpression(expressionString);
    Assertions.assertTrue(optAST.isPresent());
    Log.getFindings().clear();
    checker.checkAll(optAST.get());
    Assertions.assertTrue(Log.getFindings().isEmpty(), Log.getFindings().toString());
  }

  public void checkInvalid(String expressionString) throws IOException {
    CombineExpressionsWithLiteralsParser parser = new CombineExpressionsWithLiteralsParser();
    Optional<ASTExpression> optAST = parser.parse_StringExpression(expressionString);
    Assertions.assertTrue(optAST.isPresent());
    Log.getFindings().clear();
    checker.checkAll(optAST.get());
    Assertions.assertFalse(Log.getFindings().isEmpty());
  }

  @Test
  public void testValid() throws IOException {
    checkValid("7-4*2");
    checkValid("4/2*6%4");
    checkValid("(5<6)&&(1<=1)");
    checkValid("!true||false&&(5>=0)");
    checkValid("5.0/2.5%2");
  }

  @Test
  public void testInvalid() throws IOException {
    checkInvalid("5+false");
    checkInvalid("true-true");
    checkInvalid("!false!=5");
    checkInvalid("5||7");
    checkInvalid("true++");
    checkInvalid("(true&&6)||(false>=37)");
  }
}