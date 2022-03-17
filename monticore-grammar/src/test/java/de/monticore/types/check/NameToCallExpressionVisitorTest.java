/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.expressions.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsTraverser;
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
    CombineExpressionsWithLiteralsTraverser traverser = getTraverser();
    astex.get().accept(traverser);
    assertEquals("test",((ASTCallExpression)astex.get()).getName());
  }

  /**
   * test if the visitor transforms a call expression with inner field access expression correctly
   */
  @Test
  public void fieldAccessTest() throws IOException{
    Optional<ASTExpression> astex = p.parse_StringExpression("a.b.test()");
    ASTExpression expr = ((ASTCallExpression)astex.get()).getExpression();
    CombineExpressionsWithLiteralsTraverser traverser = getTraverser();
    astex.get().accept(traverser);
    assertEquals("test",((ASTCallExpression)astex.get()).getName());
    assertEquals(((ASTCallExpression)astex.get()).getExpression(),expr);
  }

  private CombineExpressionsWithLiteralsTraverser getTraverser(){
    CombineExpressionsWithLiteralsTraverser traverser = CombineExpressionsWithLiteralsMill.traverser();
    NameToCallExpressionVisitor visitor = new NameToCallExpressionVisitor();
    traverser.setCommonExpressionsHandler(visitor);
    traverser.add4CommonExpressions(visitor);
    traverser.setExpressionsBasisHandler(visitor);
    traverser.add4ExpressionsBasis(visitor);
    return traverser;
  }

}
