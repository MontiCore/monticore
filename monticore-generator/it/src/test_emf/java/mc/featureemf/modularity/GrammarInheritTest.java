package mc.featureemf.modularity;

import static org.junit.Assert.*;
import mc.featureemf.fautomaton.automatonwithaction._ast.*;
import mc.featureemf.fautomaton.automaton._ast.*;
import org.junit.Before;
import org.junit.Test;

public class GrammarInheritTest {
  
  @Before
  public void setUp() throws Exception {
  }
  
  @Test
  public void testMetaObjects() {
    
    assertTrue(FlatAutomatonPackage.Literals.AUTOMATON
        .isSuperTypeOf(ActionAutomatonPackage.Literals.AUTOMATON));
    assertTrue(FlatAutomatonPackage.Literals.TRANSITION
        .isSuperTypeOf(ActionAutomatonPackage.Literals.TRANSITIONWITHACTION));
    assertEquals(FlatAutomatonPackage.Literals.AUTOMATON__STATE.getEType(),
        FlatAutomatonPackage.Literals.STATE);
  }
  
  @Test
  public void testClasshierarchie() {
    mc.featureemf.fautomaton.automatonwithaction._ast.ASTAutomaton aut = ActionAutomatonFactory.eINSTANCE
        .createAutomaton();
    ASTTransitionWithAction trans = ActionAutomatonFactory.eINSTANCE
        .createTransitionWithAction();
    
    assertTrue(aut instanceof mc.featureemf.fautomaton.automaton._ast.ASTAutomaton);
    assertTrue(trans instanceof mc.featureemf.fautomaton.automaton._ast.ASTTransition);
  }
  
  @Test
  public void testMethods() {
    mc.featureemf.fautomaton.automaton._ast.ASTAutomaton aut = FlatAutomatonFactory.eINSTANCE
        .createAutomaton();
    mc.featureemf.fautomaton.automatonwithaction._ast.ASTAutomaton actAut = ActionAutomatonFactory.eINSTANCE
        .createAutomaton();
    
    ASTState state = FlatAutomatonFactory.eINSTANCE.createState();
    ASTTransitionWithAction actTrans = ActionAutomatonFactory.eINSTANCE
        .createTransitionWithAction();
    ASTTransition trans = FlatAutomatonFactory.eINSTANCE.createTransition();
    
    aut.getTransition().add(actTrans);
    
    actAut.setImpState(state);
    actAut.getTransition().add(trans);
    
    assertTrue(aut.getTransition().contains(actTrans));
    assertEquals(state, actAut.getImpState());
    assertTrue(actAut.getTransition().contains(trans));
  }
  
}
