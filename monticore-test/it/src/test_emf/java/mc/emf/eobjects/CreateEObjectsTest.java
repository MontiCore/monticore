/* (c) https://github.com/MontiCore/monticore */

package mc.emf.eobjects;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import mc.GeneratorIntegrationsTest;

public class CreateEObjectsTest extends GeneratorIntegrationsTest {
 /* TODO Brauchen wir den Test noch?
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
  */
}
