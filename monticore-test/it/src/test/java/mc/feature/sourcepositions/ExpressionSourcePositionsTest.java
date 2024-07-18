/* (c) https://github.com/MontiCore/monticore */

package mc.feature.sourcepositions;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import mc.GeneratorIntegrationsTest;
import mc.feature.expression.expression._ast.ASTExpr;
import mc.feature.expression.expression._parser.ExpressionParser;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;

/**
 * Tests the source position's computing for the AST nodes
 * Defined grammar: mc.feature.expression.Expression.mc
 * 
 */
public class ExpressionSourcePositionsTest extends GeneratorIntegrationsTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
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
      if (node.isPresentLeft()) {
        leftChild = node.getLeft();
        Assertions.assertTrue(node.get_SourcePositionStart().compareTo(leftChild.get_SourcePositionStart()) == 0);
        
        if (node.isPresentRight()) {
          ASTExpr rightChild = node.getRight();
          
          // End position of expression node coincides with the end position of
          // the right child
          Assertions.assertTrue(node.get_SourcePositionEnd().compareTo(rightChild.get_SourcePositionEnd()) == 0);
          
          // Start position of the right child is the next to the end position of
          // the left child
          Assertions.assertTrue(rightChild.get_SourcePositionStart().getColumn()
              - leftChild.get_SourcePositionEnd().getColumn() == 1);
        }
      }
      node = leftChild;
    }
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  private ASTExpr parse(String input) throws IOException {
    ExpressionParser parser = new ExpressionParser();
    Optional<ASTExpr> ast = parser.parseExpr(new StringReader(input));
    Assertions.assertTrue(ast.isPresent());
    return ast.get();
  }
  
}
