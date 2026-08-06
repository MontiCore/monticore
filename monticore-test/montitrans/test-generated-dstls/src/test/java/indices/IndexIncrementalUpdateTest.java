package indices;

import de.monticore.ast.ASTNode;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.tf.AddNewState;
import de.monticore.tf.CreateInnerStateWithInnerState;
import de.monticore.tf.RemoveInnerTransition;
import de.monticore.tf.RenameState;
import de.monticore.tf.runtime.inc.ModelAccessor;
import de.monticore.tf.runtime.inc.ModificationOp;
import mc.testcases.statechart.statechart.StatechartMill;
import mc.testcases.statechart.statechart._ast.*;
import mc.testcases.statechart.statechart._parser.StatechartParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(StatechartMill.class)
public class IndexIncrementalUpdateTest {
  
  @Test
  public void testAddition() throws IOException {
    StatechartParser px = StatechartMill.parser();
    Optional<ASTStatechart> sc = px.parse("src/test/resources/trafo/PedestrianLight.sc");
    
    assertTrue(sc.isPresent());
    assertFalse(px.hasErrors());
    
    ModelAccessor ma = new ModelAccessor(StatechartMill::inheritanceTraverser, sc.get());
    
    // Check CandidateIndex initial state
    assertEquals(37, ma.getCandidateIndex().getAllNodes().size());
    assertEquals(1, ma.getCandidateIndex().getCandidateNodes(ASTStatechart.class).size());
    assertEquals(4, ma.getCandidateIndex().getCandidateNodes(ASTState.class).size());
    assertEquals(4, ma.getCandidateIndex().getCandidateNodes(ASTTransition.class).size());
    
    // Record change notifications
    IncrementalTestListener testListener = new IncrementalTestListener();
    ma.attachListener(testListener);
    
    testListener.assertNumberOfNotifications(0);
    
    assertTrue(new AddNewState(ma).set_$newState("DefinitelyANewState").doAll());
    
    testListener.assertNumberOfNotifications(5);
    
    testListener.assertTransformationStartCall(0, call -> {
      assertEquals("de.monticore.tf.AddNewState", call.transformationName());
    });

    IncrementalTestListener.ASTNodeModificationCall nodeCreateCall =
        testListener.assertASTNodeModificationCall(1, call -> {
          assertEquals("name", call.attributeName());
          assertEquals(ModificationOp.SET, call.modificationType());
          assertNull(call.oldValue());
          assertEquals("DefinitelyANewState", call.newValue());
        });

    ASTNode createdNode = nodeCreateCall.node();

    IncrementalTestListener.ASTNodeListModificationCall listModCall =
        testListener.assertASTNodeListModificationCall(2, call -> {
          assertEquals("state", call.attributeName());
          assertEquals(2, call.idx());
          assertEquals(ModificationOp.SET, call.modificationType());
          assertNull(call.oldValue());
          assertEquals(createdNode, call.newValue());
        });

    ASTNode modifiedParent = listModCall.node();

    testListener.assertASTNodeAttachCall(3, call -> {
      assertEquals(createdNode, call.node());
      assertEquals(modifiedParent, call.parent());
    });

    testListener.assertTransformationEndCall(4, call -> {
      assertEquals("de.monticore.tf.AddNewState", call.transformationName());
    });

    // Check CandidateIndex modification
    // There should be one additional ASTState and therefore one additional total node
    assertEquals(38, ma.getCandidateIndex().getAllNodes().size());
    assertEquals(1, ma.getCandidateIndex().getCandidateNodes(ASTStatechart.class).size());
    assertEquals(5, ma.getCandidateIndex().getCandidateNodes(ASTState.class).size());
    assertEquals(4, ma.getCandidateIndex().getCandidateNodes(ASTTransition.class).size());
  }
  
  @Test
  public void testRemoval() throws IOException {
    StatechartParser px = StatechartMill.parser();
    Optional<ASTStatechart> sc = px.parse("src/test/resources/trafo/PedestrianLight.sc");
    
    assertTrue(sc.isPresent());
    assertFalse(px.hasErrors());
    
    ModelAccessor ma = new ModelAccessor(StatechartMill::inheritanceTraverser, sc.get());
    
    // Check CandidateIndex initial state
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
    
    // Record change notifications
    IncrementalTestListener testListener = new IncrementalTestListener();
    ma.attachListener(testListener);
    
    assertEquals(0, testListener.getNotifications().size());
    
    assertTrue(new RemoveInnerTransition(ma).set_$from("Red").set_$to("Green").doAll());
    
    testListener.assertNumberOfNotifications(4);
    
    testListener.assertTransformationStartCall(0, call -> {
      assertEquals("de.monticore.tf.RemoveInnerTransition", call.transformationName());
    });
    
    IncrementalTestListener.ASTNodeListModificationCall listModCall =
        testListener.assertASTNodeListModificationCall(1, call -> {
          assertEquals("transition", call.attributeName());
          assertEquals(0, call.idx());
          assertEquals(ModificationOp.UNSET, call.modificationType());
          assertNull(call.newValue());
          assertInstanceOf(ASTTransition.class, call.oldValue());
        });
    
    ASTNode modifiedNode = listModCall.node();
    ASTNode removedNode = (ASTTransition) listModCall.oldValue();
    
    testListener.assertASTNodeDetachCall(2, call -> {
      assertEquals(removedNode, call.node());
      assertEquals(modifiedNode, call.parent());
    });
    
    testListener.assertTransformationEndCall(3, call -> {
      assertEquals("de.monticore.tf.RemoveInnerTransition", call.transformationName());
    });
    
    // Check CandidateIndex modification
    // We removed one ASTTransition, together with its
    // children: ASTBlockStatement, ASTExpStatement, ASTMethodInvocationWithQualifiedName
    assertEquals(33, ma.getCandidateIndex().getAllNodes().size());
    assertEquals(1, ma.getCandidateIndex().getCandidateNodes(ASTStatechart.class).size());
    assertEquals(4, ma.getCandidateIndex().getCandidateNodes(ASTState.class).size());
    assertEquals(3, ma.getCandidateIndex().getCandidateNodes(ASTEntryAction.class).size());
    assertEquals(5, ma.getCandidateIndex().getCandidateNodes(ASTBlockStatement.class).size());
    assertEquals(5, ma.getCandidateIndex().getCandidateNodes(ASTExpStatement.class).size());
    assertEquals(5,
        ma.getCandidateIndex().getCandidateNodes(ASTMethodInvocationWithQualifiedName.class)
            .size());
    assertEquals(3, ma.getCandidateIndex().getCandidateNodes(ASTFieldAccess.class).size());
    assertEquals(2, ma.getCandidateIndex().getCandidateNodes(ASTEqualityExpression.class).size());
    assertEquals(2, ma.getCandidateIndex().getCandidateNodes(ASTDoAction.class).size());
    assertEquals(3, ma.getCandidateIndex().getCandidateNodes(ASTTransition.class).size());
  }
  
  @Test
  public void testModification() throws IOException {
    StatechartParser px = StatechartMill.parser();
    Optional<ASTStatechart> sc = px.parse("src/test/resources/trafo/PedestrianLight.sc");
    
    assertTrue(sc.isPresent());
    assertFalse(px.hasErrors());
    
    ModelAccessor ma = new ModelAccessor(StatechartMill::inheritanceTraverser, sc.get());
    
    // Record change notifications
    IncrementalTestListener testListener = new IncrementalTestListener();
    ma.attachListener(testListener);
    
    testListener.assertNumberOfNotifications(0);

    assertTrue(new RenameState(ma).set_$oldName("PedestrianLightOff").set_$newName("PedestrianLightDark").doAll());
    
    testListener.assertNumberOfNotifications(5);
    
    testListener.assertTransformationStartCall(0, call -> {
      assertEquals("de.monticore.tf.RenameState", call.transformationName());
    });
    
    testListener.assertASTNodeModificationCall(1, call -> {
      assertEquals("name", call.attributeName());
      assertEquals(ModificationOp.REPLACE, call.modificationType());
      assertEquals("PedestrianLightOff", call.oldValue());
      assertEquals("PedestrianLightDark", call.newValue());
    });

    testListener.assertASTNodeModificationCall(2, call -> {
      assertEquals("from", call.attributeName());
      assertEquals(ModificationOp.REPLACE, call.modificationType());
      assertEquals("PedestrianLightOff", call.oldValue());
      assertEquals("PedestrianLightDark", call.newValue());
    });
    
    testListener.assertASTNodeModificationCall(3, call -> {
      assertEquals("to", call.attributeName());
      assertEquals(ModificationOp.REPLACE, call.modificationType());
      assertEquals("PedestrianLightOff", call.oldValue());
      assertEquals("PedestrianLightDark", call.newValue());
    });
    
    testListener.assertTransformationEndCall(4, call -> {
      assertEquals("de.monticore.tf.RenameState", call.transformationName());
    });
  }
  
  @Test
  public void testModification2() throws IOException {
    StatechartParser px = StatechartMill.parser();
    Optional<ASTStatechart> sc = px.parse("src/test/resources/trafo/PedestrianLight.sc");
    
    assertTrue(sc.isPresent());
    assertFalse(px.hasErrors());
    
    ModelAccessor ma = new ModelAccessor(StatechartMill::inheritanceTraverser, sc.get());
    
    // Record change notifications
    IncrementalTestListener testListener = new IncrementalTestListener();
    ma.attachListener(testListener);
    
    testListener.assertNumberOfNotifications(0);
    
    assertTrue(new CreateInnerStateWithInnerState(ma).set_$outer("PedestrianLightOff")
        .set_$inner("PedestrianLightBroken").set_$inner2("Exploded").doAll());
    
    testListener.assertNumberOfNotifications(8);
    

    testListener.assertTransformationStartCall(0, call -> {
      assertEquals("de.monticore.tf.CreateInnerStateWithInnerState", call.transformationName());
    });
    
    IncrementalTestListener.ASTNodeModificationCall nodeCreateCall1 =
        testListener.assertASTNodeModificationCall(1, call -> {
          assertEquals("name", call.attributeName());
          assertEquals(ModificationOp.SET, call.modificationType());
          assertNull(call.oldValue());
          assertEquals("Exploded", call.newValue());
        });
    
    ASTNode createdNode1 = nodeCreateCall1.node();
    
    IncrementalTestListener.ASTNodeModificationCall nodeCreateCall2 =
        testListener.assertASTNodeModificationCall(2, call -> {
          assertEquals("name", call.attributeName());
          assertEquals(ModificationOp.SET, call.modificationType());
          assertNull(call.oldValue());
          assertEquals("PedestrianLightBroken", call.newValue());
        });
    
    ASTNode createdNode2 = nodeCreateCall2.node();
    
    IncrementalTestListener.ASTNodeListModificationCall nodeListModCall3 =
        testListener.assertASTNodeListModificationCall(3, call -> {
          assertEquals(createdNode2, call.node());
          assertEquals("state", call.attributeName());
          assertEquals(0, call.idx());
          assertEquals(ModificationOp.SET, call.modificationType());
          assertNull(call.oldValue());
          assertEquals(createdNode1, call.newValue());
    });
    
    testListener.assertASTNodeAttachCall(4, call -> {
      assertEquals(createdNode1, call.node());
      assertEquals(createdNode2, call.parent());
    });
    
    IncrementalTestListener.ASTNodeListModificationCall nodeModCall =
        testListener.assertASTNodeListModificationCall(5, call -> {
          assertEquals("state", call.attributeName());
          assertEquals(0, call.idx());
          assertEquals(ModificationOp.SET, call.modificationType());
          assertNull(call.oldValue());
          assertEquals(createdNode2, call.newValue());
        });
    
    ASTNode modifiedNode = nodeModCall.node();
    
    testListener.assertASTNodeAttachCall(6, call -> {
      assertEquals(createdNode2, call.node());
      assertEquals(modifiedNode, call.parent());
    });
    
    testListener.assertTransformationEndCall(7, call -> {
      assertEquals("de.monticore.tf.CreateInnerStateWithInnerState", call.transformationName());
    });
  }
}
