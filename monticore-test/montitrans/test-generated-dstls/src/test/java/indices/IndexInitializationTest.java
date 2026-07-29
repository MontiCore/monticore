package indices;

import de.monticore.ast.ASTCNode;
import de.monticore.ast.ASTNode;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.tf.runtime.inc.ModelAccessor;
import de.monticore.tf.runtime.inc.ParentIndex;
import mc.testcases.statechart.statechart.StatechartMill;
import mc.testcases.statechart.statechart._ast.*;
import mc.testcases.statechart.statechart._parser.StatechartParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(StatechartMill.class)
public class IndexInitializationTest {
  
  @Test
  public void testCorrectness() throws IOException {
    StatechartParser px = StatechartMill.parser();
    Optional<ASTStatechart> sc = px.parse("src/test/resources/trafo/PedestrianLight.sc");
    
    assertTrue(sc.isPresent());
    assertFalse(px.hasErrors());
    
    ModelAccessor ma = new ModelAccessor(StatechartMill::inheritanceTraverser, sc.get());
    
    // Check the candidate index for correctness
    assertEquals(37, ma.getCandidateIndex().getAllNodes().size());
    assertEquals(1, ma.getCandidateIndex().getCandidateNodes(ASTStatechart.class).size());
    assertEquals(4, ma.getCandidateIndex().getCandidateNodes(ASTState.class).size());
    assertEquals(3, ma.getCandidateIndex().getCandidateNodes(ASTEntryAction.class).size());
    assertEquals(6, ma.getCandidateIndex().getCandidateNodes(ASTBlockStatement.class).size());
    assertEquals(6, ma.getCandidateIndex().getCandidateNodes(ASTExpStatement.class).size());
    assertEquals(6,
        ma.getCandidateIndex().getCandidateNodes(ASTMethodInvocationWithQualifiedName.class)
            .size());
    assertEquals(3, ma.getCandidateIndex().getCandidateNodes(ASTFieldAccess.class).size());
    assertEquals(2, ma.getCandidateIndex().getCandidateNodes(ASTEqualityExpression.class).size());
    assertEquals(2, ma.getCandidateIndex().getCandidateNodes(ASTDoAction.class).size());
    assertEquals(4, ma.getCandidateIndex().getCandidateNodes(ASTTransition.class).size());
    
    assertEquals(37, ma.getCandidateIndex().getSubTypeCandidateNodes(ASTCNode.class).size());
    assertEquals(5, ma.getCandidateIndex().getSubTypeCandidateNodes(ASTSCStructure.class).size());
    assertEquals(37, ma.getCandidateIndex().getSubTypeCandidateNodes(ASTNode.class).size());
    assertEquals(37,
        ma.getCandidateIndex().getSubTypeCandidateNodes(ASTStatechartNode.class).size());
    assertEquals(12, ma.getCandidateIndex().getSubTypeCandidateNodes(ASTStatement.class).size());
    assertEquals(11, ma.getCandidateIndex().getSubTypeCandidateNodes(ASTExpression.class).size());
    
    // Check the parent index for correctness
    // Each node (besides the root) should have one PostComment
    ma.getCandidateIndex().getAllNodes().forEach(n -> {
      ;
      if (!(n instanceof ASTStatechart)) {
        assertEquals(1, n.get_PostCommentList().size());
        assertInstanceOf(ParentIndex.WComment.class, n.get_PostCommentList().getFirst());
      }
    });
  }
}
