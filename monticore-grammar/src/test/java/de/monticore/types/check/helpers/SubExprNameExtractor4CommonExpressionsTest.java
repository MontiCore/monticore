/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check.helpers;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

public class SubExprNameExtractor4CommonExpressionsTest {

  private CombineExpressionsWithLiteralsParser parser = new CombineExpressionsWithLiteralsParser();

  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    CombineExpressionsWithLiteralsMill.reset();
    CombineExpressionsWithLiteralsMill.init();
  }

  /**
   * test if the name calculator extracts the name of the name expression correctly
   */
  @Test
  public void nameTest() throws IOException {
    // Given
    ASTExpression astExpr = parser.parse_StringExpression("test()").get();
    ASTExpression methodNameExpr = ((ASTCallExpression) astExpr).getExpression();
    SubExprNameExtractor4CommonExpressions nameCalculator = new SubExprNameExtractor4CommonExpressions();

    // When
    SubExprNameExtractionResult result = nameCalculator.calculateNameParts(methodNameExpr);

    // Then
    Assertions.assertTrue(result.resultIsValidName());
    Assertions.assertEquals(1, result.getNamePartsRaw().size());
    Assertions.assertEquals("test", result.getLastName().get());
    Assertions.assertEquals(methodNameExpr, result.getNamePartsIfValid().get().get(0).getExpression());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  /**
   * test if the visitor transforms a call expression with inner field access expression correctly
   */
  @Test
  public void fieldAccessTest() throws IOException {
    // Given
    ASTExpression astExpr = parser.parse_StringExpression("Foo.b.test()").get();
    ASTExpression methodNameExpr = ((ASTCallExpression) astExpr).getExpression();
    SubExprNameExtractor4CommonExpressions nameCalculator = new SubExprNameExtractor4CommonExpressions();

    // When
    SubExprNameExtractionResult result = nameCalculator.calculateNameParts(methodNameExpr);

    // Then
    Assertions.assertTrue(result.resultIsValidName());
    List<ExprToNamePair> nameParts = result.getNamePartsIfValid().get();

    Assertions.assertEquals(3, result.getNamePartsRaw().size());
    Assertions.assertEquals("test", result.getLastName().get());
    Assertions.assertEquals(methodNameExpr, nameParts.get(nameParts.size() - 1).getExpression());
    Assertions.assertEquals(((ASTFieldAccessExpression) methodNameExpr).getExpression(), nameParts.get(nameParts.size() - 2).getExpression());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void methodChainTest() throws IOException {
    // Given
    ASTExpression astExpr = parser.parse_StringExpression("tezt().test()").get();
    ASTExpression methodNameExpr = ((ASTCallExpression) astExpr).getExpression();
    SubExprNameExtractor4CommonExpressions nameCalculator = new SubExprNameExtractor4CommonExpressions();

    // When
    SubExprNameExtractionResult result = nameCalculator.calculateNameParts(methodNameExpr);

    // Then
    Assertions.assertFalse(result.resultIsValidName());
    Assertions.assertTrue(result.getLastName().isPresent());

    List<ExprToOptNamePair> subExprs = result.getNamePartsRaw();

    Assertions.assertEquals(3, result.getNamePartsRaw().size());  // tezt NameExpr, tezt() CallExpr, tezt().test fAccExpr
    Assertions.assertEquals("test", result.getLastName().get());
    Assertions.assertEquals(methodNameExpr, subExprs.get(subExprs.size() - 1).getExpression());
    Assertions.assertEquals(((ASTFieldAccessExpression) methodNameExpr).getExpression(), subExprs.get(subExprs.size() - 2).getExpression());

    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

}
