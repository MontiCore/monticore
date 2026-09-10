package indices;

import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.tf.*;
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

    testListener.assertNumberOfNotifications(6);

    testListener.assertTransformationStartCall(0, call -> {
      assertEquals("de.monticore.tf.AddNewState", call.transformationName());
    });

    IncrementalTestListener.ASTNodeCreationCall nodeCreationCall =
        testListener.assertASTNodeCreationCall(1, call -> {
          assertInstanceOf(ASTState.class, call.node());
        });

    IncrementalTestListener.ASTNodeModificationCall nodeModificationCall =
        testListener.assertASTNodeModificationCall(2, call -> {
          assertEquals(nodeCreationCall.node(), call.node());
          assertEquals("name", call.attributeName());
          assertEquals(ModificationOp.SET, call.modificationType());
          assertNull(call.oldValue());
          assertEquals("DefinitelyANewState", call.newValue());
        });

    IncrementalTestListener.ASTNodeListModificationCall listModCall =
        testListener.assertASTNodeListModificationCall(3, call -> {
          assertEquals("state", call.attributeName());
          assertEquals(2, call.idx());
          assertEquals(ModificationOp.SET, call.modificationType());
          assertNull(call.oldValue());
          assertEquals(nodeCreationCall.node(), call.newValue());
        });

    testListener.assertASTNodeAttachCall(4, call -> {
      assertEquals(nodeCreationCall.node(), call.node());
      assertEquals(listModCall.node(), call.parent());
    });

    testListener.assertTransformationEndCall(5, call -> {
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

    testListener.assertASTNodeDetachCall(2, call -> {
      assertEquals(listModCall.oldValue(), call.node());
      assertEquals(listModCall.node(), call.parent());
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

    assertTrue(
        new RenameState(ma).set_$oldName("PedestrianLightOff").set_$newName("PedestrianLightDark")
            .doAll());

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

    testListener.assertNumberOfNotifications(10);


    testListener.assertTransformationStartCall(0, call -> {
      assertEquals("de.monticore.tf.CreateInnerStateWithInnerState", call.transformationName());
    });

    IncrementalTestListener.ASTNodeCreationCall nodeCreationCall1 =
        testListener.assertASTNodeCreationCall(1, call -> {
          assertInstanceOf(ASTState.class, call.node());
        });

    testListener.assertASTNodeModificationCall(2, call -> {
      assertEquals(nodeCreationCall1.node(), call.node());
      assertEquals("name", call.attributeName());
      assertEquals(ModificationOp.SET, call.modificationType());
      assertNull(call.oldValue());
      assertEquals("Exploded", call.newValue());
    });

    IncrementalTestListener.ASTNodeCreationCall nodeCreationCall2 =
        testListener.assertASTNodeCreationCall(3, call -> {
          assertInstanceOf(ASTState.class, call.node());
        });

    testListener.assertASTNodeModificationCall(4, call -> {
      assertEquals(nodeCreationCall2.node(), call.node());
      assertEquals("name", call.attributeName());
      assertEquals(ModificationOp.SET, call.modificationType());
      assertNull(call.oldValue());
      assertEquals("PedestrianLightBroken", call.newValue());
    });

    testListener.assertASTNodeListModificationCall(5, call -> {
      assertEquals(nodeCreationCall2.node(), call.node());
      assertEquals("state", call.attributeName());
      assertEquals(0, call.idx());
      assertEquals(ModificationOp.SET, call.modificationType());
      assertNull(call.oldValue());
      assertEquals(nodeCreationCall1.node(), call.newValue());
    });

    testListener.assertASTNodeAttachCall(6, call -> {
      assertEquals(nodeCreationCall1.node(), call.node());
      assertEquals(nodeCreationCall2.node(), call.parent());
    });

    IncrementalTestListener.ASTNodeListModificationCall nodeListModCall2 =
        testListener.assertASTNodeListModificationCall(7, call -> {
          assertEquals("state", call.attributeName());
          assertEquals(0, call.idx());
          assertEquals(ModificationOp.SET, call.modificationType());
          assertNull(call.oldValue());
          assertEquals(nodeCreationCall2.node(), call.newValue());
        });

    testListener.assertASTNodeAttachCall(8, call -> {
      assertEquals(nodeCreationCall2.node(), call.node());
      assertEquals(nodeListModCall2.node(), call.parent());
    });

    testListener.assertTransformationEndCall(9, call -> {
      assertEquals("de.monticore.tf.CreateInnerStateWithInnerState", call.transformationName());
    });
  }

  /**
   * Tests the notifications emitted for a transition copy into a substate,
   * with a special focus on notifications that should be triggered by deep-copy behavior.
   */
  @Test
  public void testModification3() throws IOException {
    StatechartParser px = StatechartMill.parser();
    Optional<ASTStatechart> sc = px.parse("src/test/resources/trafo/PedestrianLight.sc");

    assertTrue(sc.isPresent());
    assertFalse(px.hasErrors());

    ModelAccessor ma = new ModelAccessor(StatechartMill::inheritanceTraverser, sc.get());

    // Record change notifications
    IncrementalTestListener testListener = new IncrementalTestListener();
    ma.attachListener(testListener);

    testListener.assertNumberOfNotifications(0);

    assertTrue(new CopyTransitionToSubstate(ma)
        .set_$superstate("PedestrianLightOn").set_$substate("Green").doAll());

    testListener.assertNumberOfNotifications(12);

    testListener.assertTransformationStartCall(0, call -> {
      assertEquals("de.monticore.tf.CopyTransitionToSubstate", call.transformationName());
    });

    IncrementalTestListener.ASTNodeCreationCall creationCall1 =
        testListener.assertASTNodeCreationCall(1, call -> {
          assertInstanceOf(ASTTransition.class, call.node());
        });

    IncrementalTestListener.ASTNodeCreationCall creationCall2 =
        testListener.assertASTNodeCreationCall(2, call -> {
          assertInstanceOf(ASTBlockStatement.class, call.node());
        });

    IncrementalTestListener.ASTNodeCreationCall creationCall3 =
        testListener.assertASTNodeCreationCall(3, call -> {
          assertInstanceOf(ASTExpStatement.class, call.node());
        });

    IncrementalTestListener.ASTNodeCreationCall creationCall4 =
        testListener.assertASTNodeCreationCall(4, call -> {
          assertInstanceOf(ASTMethodInvocationWithQualifiedName.class, call.node());
        });

    testListener.assertASTNodeAttachCall(5, call -> {
      assertEquals(creationCall4.node(), call.node());
      assertEquals(creationCall3.node(), call.parent());
    });

    testListener.assertASTNodeAttachCall(6, call -> {
      assertEquals(creationCall3.node(), call.node());
      assertEquals(creationCall2.node(), call.parent());
    });

    testListener.assertASTNodeAttachCall(7, call -> {
      assertEquals(creationCall2.node(), call.node());
      assertEquals(creationCall1.node(), call.parent());
    });

    testListener.assertASTNodeAttachCall(8, call -> {
      assertEquals(creationCall1.node(), call.node());
      // This indicates the finalization of the deep copy
      // The ASTTransition is a detached subtree at this point, indicated by the null parent
      assertNull(call.parent());
    });

    IncrementalTestListener.ASTNodeListModificationCall nodeListModCall =
        testListener.assertASTNodeListModificationCall(9, call -> {
          assertEquals("transition", call.attributeName());
          assertEquals(0, call.idx());
          assertEquals(ModificationOp.SET, call.modificationType());
          assertNull(call.oldValue());
          assertEquals(creationCall1.node(), call.newValue());
        });

    testListener.assertASTNodeAttachCall(10, call -> {
      // here we attach the detached subtree from the deep-copy to the original AST
      assertEquals(creationCall1.node(), call.node());
      assertEquals(nodeListModCall.node(), call.parent());
    });

    testListener.assertTransformationEndCall(11, call -> {
      assertEquals("de.monticore.tf.CopyTransitionToSubstate", call.transformationName());
    });
  }
}
