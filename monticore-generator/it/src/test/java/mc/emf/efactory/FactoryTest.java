package mc.emf.efactory;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.monticore.emf._ast.ASTENode;
import mc.feature.fautomaton.automaton.flatautomaton._ast.ASTAutomaton;
import mc.feature.fautomaton.automaton.flatautomaton._ast.ASTState;
import mc.feature.fautomaton.automaton.flatautomaton._ast.FlatAutomatonNodeFactory;
import mc.feature.fautomaton.automaton.flatautomaton._ast.FlatAutomatonPackage;

public class FactoryTest {
  
  @Test
  public void testReflectiveCreate() {
    ASTENode ast = (ASTENode) FlatAutomatonNodeFactory.getFactory()
        .create(FlatAutomatonPackage.Literals.ASTAutomaton);
    
    assertNotNull(ast);
    assertTrue(ast instanceof ASTAutomaton);
  }
  
  @Test
  public void testCreate() {
    ASTState state = FlatAutomatonNodeFactory.createASTState();
    assertNotNull(state);
  }
  
}
