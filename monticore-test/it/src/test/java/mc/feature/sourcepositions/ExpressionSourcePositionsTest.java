/* (c) https://github.com/MontiCore/monticore */

package mc.feature.sourcepositions;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.expression.expression.ExpressionMill;
import mc.feature.expression.expression._ast.ASTExpr;
import mc.feature.expression.expression._parser.ExpressionParser;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the source position's computing for the AST nodes
 * Defined grammar: mc.feature.expression.Expression.mc
 * 
 */
@TestWithMCLanguage(ExpressionMill.class)
public class ExpressionSourcePositionsTest {

  @ParameterizedTest
  @ValueSource(strings = {"1", "1+1", "1+2-3", "1+1+2+3-4", "1-1-2-3", "1*2+3", "1+2*3"})
  public void testExp(String value) throws IOException {
    doTestPExpSourcePositions(parse(value));
  }
  
  private void doTestPExpSourcePositions(ASTExpr node) {
    // test recursive so long as the left child was defined by astscript
    // constructor
    while (node != null) {
      
      // Start position of expression node coincides with the start position of
      // the left child
      ASTExpr leftChild = null;
      if (node.isPresentLeft()) {
        leftChild = node.getLeft();
        assertEquals(0,
            node.get_SourcePositionStart().compareTo(leftChild.get_SourcePositionStart()));
        
        if (node.isPresentRight()) {
          ASTExpr rightChild = node.getRight();
          
          // End position of expression node coincides with the end position of
          // the right child
          assertEquals(0,
              node.get_SourcePositionEnd().compareTo(rightChild.get_SourcePositionEnd()));
          
          // Start position of the right child is the next to the end position of
          // the left child
          assertEquals(1,
              rightChild.get_SourcePositionStart().getColumn() - leftChild.get_SourcePositionEnd()
                  .getColumn());
        }
      }
      node = leftChild;
    }
  }
  
  private ASTExpr parse(String input) throws IOException {
    ExpressionParser parser = ExpressionMill.parser();
    Optional<ASTExpr> ast = parser.parse_StringExpr(input);
    assertTrue(ast.isPresent());
    return ast.get();
  }
  
}
