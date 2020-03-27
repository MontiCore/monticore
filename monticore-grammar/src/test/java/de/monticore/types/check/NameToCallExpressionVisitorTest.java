/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class NameToCallExpressionVisitorTest {

  private CombineExpressionsWithLiteralsParser p = new CombineExpressionsWithLiteralsParser();

  /**
   * test if the visitor transforms a call expression with inner name expression correctly
   */
  @Test
  public void nameTest() throws IOException {
    Optional<ASTExpression> astex = p.parse_StringExpression("test()");
    NameToCallExpressionVisitor visitor = new NameToCallExpressionVisitor();
    astex.get().accept(visitor);
    assertEquals("test",((ASTCallExpression)astex.get()).getName());
  }

  /**
   * test if the visitor transforms a call expression with inner field access expression correctly
   */
  @Test
  public void fieldAccessTest() throws IOException{
    Optional<ASTExpression> astex = p.parse_StringExpression("a.b.test()");
    ASTExpression expr = ((ASTCallExpression)astex.get()).getExpression();
    ASTExpression innerExpr = ((ASTFieldAccessExpression)expr).getExpression();
    NameToCallExpressionVisitor visitor = new NameToCallExpressionVisitor();
    astex.get().accept(visitor);
    assertEquals("test",((ASTCallExpression)astex.get()).getName());
    assertEquals(((ASTCallExpression)astex.get()).getExpression(),innerExpr);
  }

}
