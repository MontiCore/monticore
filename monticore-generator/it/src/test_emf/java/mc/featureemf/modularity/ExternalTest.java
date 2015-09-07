package mc.featureemf.modularity;

import static org.junit.Assert.*;
import mc.ast.emf._ast.ASTENodePackage;
import mc.featureemf.fautomaton.automatonwithaction._ast.*;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.junit.Before;
import org.junit.Test;

public class ExternalTest {
  ASTTransitionWithAction transAct;
  
  @Before
  public void setUp() throws Exception {
    transAct = ActionAutomatonFactory.eINSTANCE
        .createTransitionWithAction();
  }
  
  @Test
  public void testMetaObject() {
    EReference action = ActionAutomatonPackage.Literals.TRANSITIONWITHACTION__ACTION;
    
    EClass expectedExternalType = ASTENodePackage.Literals.ENODE;
    
    assertFalse(action.isMany());
    assertEquals(expectedExternalType, action.getEReferenceType());
    assertEquals(ActionAutomatonPackage.TRANSITIONWITHACTION__ACTION,
        action.getFeatureID());
  }
  
  @Test
  public void testMethods() {
    ASTAutomaton aut = ActionAutomatonFactory.eINSTANCE.createAutomaton();
    transAct.setAction(aut);
    
    assertEquals(aut, transAct.getAction());
  }
  
}
