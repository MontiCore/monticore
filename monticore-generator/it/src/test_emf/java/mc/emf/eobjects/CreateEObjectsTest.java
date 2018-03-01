/* (c) https://github.com/MontiCore/monticore */

package mc.emf.eobjects;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.monticore.emf._ast.ASTENode;
import mc.GeneratorIntegrationsTest;
import mc.feature.fautomaton.automaton.flatautomaton._ast.ASTAutomaton;
import mc.feature.fautomaton.automaton.flatautomaton._ast.ASTState;
import mc.feature.fautomaton.automaton.flatautomaton._ast.ASTTransition;
import mc.feature.fautomaton.automaton.flatautomaton._ast.FlatAutomatonNodeFactory;
import mc.feature.fautomaton.automaton.flatautomaton._ast.FlatAutomatonPackage;

public class CreateEObjectsTest extends GeneratorIntegrationsTest {
  
  @Test
  public void factoryTest() {
    ASTENode ast = (ASTENode) FlatAutomatonNodeFactory.getFactory()
        .create(FlatAutomatonPackage.Literals.ASTAutomaton);
    assertNotNull(ast);
    assertTrue(ast instanceof ASTAutomaton);
   
    ast = (ASTENode) FlatAutomatonNodeFactory.getFactory()
        .create(FlatAutomatonPackage.Literals.ASTState);
    assertNotNull(ast);
    assertTrue(ast instanceof ASTState);
    
    ast = (ASTENode) FlatAutomatonNodeFactory.getFactory()
        .create(FlatAutomatonPackage.Literals.ASTTransition);
    assertNotNull(ast);
    assertTrue(ast instanceof ASTTransition);
  }
  
  @Test
  public void testCreate() {
    ASTState state = FlatAutomatonNodeFactory.createASTState();
    assertNotNull(state);
    // TODO GV
  }
  
}
