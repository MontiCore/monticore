package mc.featureemf.efactory;

import static org.junit.Assert.*;
import mc.ast.emf._ast.ASTENode;
import mc.featureemf.fautomaton.automaton._ast.ASTAutomaton;
import mc.featureemf.fautomaton.automaton._ast.FlatAutomatonFactory;

import org.junit.Test;
import mc.featureemf.fautomaton.automaton._ast.*;

public class FactoryTest {
  
  @Test
  public void testReflectiveCreate() {
    ASTENode aut = (ASTENode) FlatAutomatonFactory.eINSTANCE
        .create(FlatAutomatonPackage.Literals.AUTOMATON);
    
    assertNotNull(aut);
    assertTrue(aut instanceof ASTAutomaton);
  }
  
  @Test
  public void testCreate() {
    ASTState state = FlatAutomatonFactory.eINSTANCE.createState();
    
    assertNotNull(state);
  }
  
}
