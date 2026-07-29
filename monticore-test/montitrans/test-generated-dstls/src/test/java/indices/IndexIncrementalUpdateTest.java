package indices;

import de.monticore.ast.ASTNode;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.tf.AddNewState;
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
    
    assertEquals(0, testListener.getNotifications().size());
    
    assertTrue(new AddNewState(ma).set_$newState("DefinitelyANewState").doAll());
    
    assertEquals(5, testListener.getNotifications().size());
    
    assertInstanceOf(IncrementalTestListener.TransformationStartCall.class,
        testListener.getNotification(0));
    IncrementalTestListener.TransformationStartCall n1 =
        (IncrementalTestListener.TransformationStartCall) testListener.getNotification(0);
    assertEquals("de.monticore.tf.AddNewState", n1.transformationName());
    
    assertInstanceOf(IncrementalTestListener.ASTNodeModificationCall.class,
        testListener.getNotification(1));
    IncrementalTestListener.ASTNodeModificationCall n2 =
        (IncrementalTestListener.ASTNodeModificationCall) testListener.getNotification(1);
    ASTNode createdNode = n2.node();
    assertEquals("name", n2.attributeName());
    assertEquals(ModificationOp.SET, n2.modificationType());
    assertNull(n2.oldValue());
    assertEquals("DefinitelyANewState", n2.newValue());
    
    assertInstanceOf(IncrementalTestListener.ASTNodeListModificationCall.class,
        testListener.getNotification(2));
    IncrementalTestListener.ASTNodeListModificationCall n3 =
        (IncrementalTestListener.ASTNodeListModificationCall) testListener.getNotification(2);
    ASTNode modifiedParent = n3.node();
    assertEquals("state", n3.attributeName());
    assertEquals(2, n3.idx());
    assertEquals(ModificationOp.SET, n3.modificationType());
    assertNull(n3.oldValue());
    assertEquals(createdNode, n3.newValue());
    
    assertInstanceOf(IncrementalTestListener.ASTNodeAttachCall.class,
        testListener.getNotification(3));
    IncrementalTestListener.ASTNodeAttachCall n4 =
        (IncrementalTestListener.ASTNodeAttachCall) testListener.getNotification(3);
    assertEquals(createdNode, n4.node());
    assertEquals(modifiedParent, n4.parent());
    
    assertInstanceOf(IncrementalTestListener.TransformationEndCall.class,
        testListener.getNotification(4));
    IncrementalTestListener.TransformationEndCall n5 =
        (IncrementalTestListener.TransformationEndCall) testListener.getNotification(4);
    assertEquals("de.monticore.tf.AddNewState", n5.transformationName());
    
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
    
    assertEquals(4, testListener.getNotifications().size());
    
    assertInstanceOf(IncrementalTestListener.TransformationStartCall.class,
        testListener.getNotification(0));
    IncrementalTestListener.TransformationStartCall n1 =
        (IncrementalTestListener.TransformationStartCall) testListener.getNotification(0);
    assertEquals("de.monticore.tf.RemoveInnerTransition", n1.transformationName());
    
    assertInstanceOf(IncrementalTestListener.ASTNodeListModificationCall.class,
        testListener.getNotification(1));
    IncrementalTestListener.ASTNodeListModificationCall n2 =
        (IncrementalTestListener.ASTNodeListModificationCall) testListener.getNotification(1);
    ASTNode modifiedNode = n2.node();
    assertEquals("transition", n2.attributeName());
    assertEquals(ModificationOp.UNSET, n2.modificationType());
    assertNull(n2.newValue());
    assertInstanceOf(ASTTransition.class, n2.oldValue());
    ASTNode removedNode = (ASTTransition) n2.oldValue();
    
    assertInstanceOf(IncrementalTestListener.ASTNodeDetachCall.class,
        testListener.getNotification(2));
    IncrementalTestListener.ASTNodeDetachCall n3 =
        (IncrementalTestListener.ASTNodeDetachCall) testListener.getNotification(2);
    assertEquals(removedNode, n3.node());
    assertEquals(modifiedNode, n3.parent());
    
    assertInstanceOf(IncrementalTestListener.TransformationEndCall.class,
        testListener.getNotification(3));
    IncrementalTestListener.TransformationEndCall n4 =
        (IncrementalTestListener.TransformationEndCall) testListener.getNotification(3);
    assertEquals("de.monticore.tf.RemoveInnerTransition", n4.transformationName());
    
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
    
    assertEquals(0, testListener.getNotifications().size());
    
    assertTrue(new RenameState(ma).set_$oldName("PedestrianLightOff").set_$newName("PedestrianLightDark").doAll());
    
    assertEquals(5, testListener.getNotifications().size());
    
    assertInstanceOf(IncrementalTestListener.TransformationStartCall.class,
        testListener.getNotification(0));
    IncrementalTestListener.TransformationStartCall n1 =
        (IncrementalTestListener.TransformationStartCall) testListener.getNotification(0);
    assertEquals("de.monticore.tf.RenameState", n1.transformationName());
    
    assertInstanceOf(IncrementalTestListener.ASTNodeModificationCall.class,
        testListener.getNotification(1));
    IncrementalTestListener.ASTNodeModificationCall n2 =
        (IncrementalTestListener.ASTNodeModificationCall) testListener.getNotification(1);
    assertInstanceOf(ASTState.class, n2.node());
    assertEquals("name", n2.attributeName());
    assertEquals(ModificationOp.REPLACE, n2.modificationType());
    assertEquals("PedestrianLightOff", n2.oldValue());
    assertEquals("PedestrianLightDark", n2.newValue());
    
    assertInstanceOf(IncrementalTestListener.ASTNodeModificationCall.class,
        testListener.getNotification(2));
    IncrementalTestListener.ASTNodeModificationCall n3 =
        (IncrementalTestListener.ASTNodeModificationCall) testListener.getNotification(2);
    assertInstanceOf(ASTTransition.class, n3.node());
    assertEquals("from", n3.attributeName());
    assertEquals(ModificationOp.REPLACE, n3.modificationType());
    assertEquals("PedestrianLightOff", n3.oldValue());
    assertEquals("PedestrianLightDark", n3.newValue());
    
    assertInstanceOf(IncrementalTestListener.ASTNodeModificationCall.class,
        testListener.getNotification(3));
    IncrementalTestListener.ASTNodeModificationCall n4 =
        (IncrementalTestListener.ASTNodeModificationCall) testListener.getNotification(3);
    assertInstanceOf(ASTTransition.class, n4.node());
    assertEquals("to", n4.attributeName());
    assertEquals(ModificationOp.REPLACE, n4.modificationType());
    assertEquals("PedestrianLightOff", n4.oldValue());
    assertEquals("PedestrianLightDark", n4.newValue());
    
    assertInstanceOf(IncrementalTestListener.TransformationEndCall.class,
        testListener.getNotification(4));
    IncrementalTestListener.TransformationEndCall n5 =
        (IncrementalTestListener.TransformationEndCall) testListener.getNotification(4);
    assertEquals("de.monticore.tf.RenameState", n5.transformationName());
  }
}
