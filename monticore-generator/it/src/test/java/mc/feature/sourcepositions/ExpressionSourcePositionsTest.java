/* (c) https://github.com/MontiCore/monticore */

package mc.feature.sourcepositions;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.expression.expression._ast.ASTExpr;
import mc.feature.expression.expression._parser.ExpressionParser;

/**
 * Tests the source position's computing for the AST nodes
 * Defined grammar: mc.feature.expression.Expression.mc
 * 
 * @author volkova
 */
public class ExpressionSourcePositionsTest extends GeneratorIntegrationsTest {
  
  @Test
  public void testExp() throws IOException {
    
    doTestPExpSourcePositions(parse("1"));
    doTestPExpSourcePositions(parse("1+1"));
    doTestPExpSourcePositions(parse("1+2-3"));
    doTestPExpSourcePositions(parse("1+1+2+3-4"));
    doTestPExpSourcePositions(parse("1-1-2-3"));
    doTestPExpSourcePositions(parse("1*2+3"));
    doTestPExpSourcePositions(parse("1+2*3"));
  }
  
  private void doTestPExpSourcePositions(ASTExpr node) {
    // test recursive so long as the left child was defined by astscript
    // constructor
    while (node != null) {
      
      // Start position of expression node coincides with the start position of
      // the left child
      ASTExpr leftChild = null;
      if (node.getLeftOpt().isPresent()) {
        leftChild = node.getLeftOpt().get();
        assertTrue(node.get_SourcePositionStart().compareTo(leftChild.get_SourcePositionStart()) == 0);
        
        if (node.getRightOpt().isPresent()) {
          ASTExpr rightChild = node.getRightOpt().get();
          
          // End position of expression node coincides with the end position of
          // the right child
          assertTrue(node.get_SourcePositionEnd().compareTo(rightChild.get_SourcePositionEnd()) == 0);
          
          // Start position of the right child is the next to the end position of
          // the left child
          assertTrue(rightChild.get_SourcePositionStart().getColumn()
              - leftChild.get_SourcePositionEnd().getColumn() == 1);
        }
      }
      node = leftChild;
    }
  }
  
  private ASTExpr parse(String input) throws IOException {
    ExpressionParser parser = new ExpressionParser();
    Optional<ASTExpr> ast = parser.parseExpr(new StringReader(input));
    assertTrue(ast.isPresent());
    return ast.get();
  }
  
}
