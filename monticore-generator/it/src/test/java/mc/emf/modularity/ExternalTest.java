/* (c) https://github.com/MontiCore/monticore */

package mc.emf.modularity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import mc.GeneratorIntegrationsTest;
import mc.feature.fautomaton.automaton.flatautomaton._ast.FlatAutomatonPackage;
import mc.feature.fautomaton.automatonwithaction.actionautomaton._ast.ASTAutomaton;
import mc.feature.fautomaton.automatonwithaction.actionautomaton._ast.ASTCounter;
import mc.feature.fautomaton.automatonwithaction.actionautomaton._ast.ActionAutomatonNodeFactory;

public class ExternalTest extends GeneratorIntegrationsTest {
  
  private ASTAutomaton aut;
  
  @Before
  public void setUp() throws Exception {
    aut = ActionAutomatonNodeFactory.createASTAutomaton();
  }
  
  @Test
  public void testMetaObject() {
    EReference transition = FlatAutomatonPackage.eINSTANCE.getASTAutomaton_Transitions();
    
    EClass expectedExternalType = FlatAutomatonPackage.eINSTANCE.getTransition();
    
    assertTrue(transition.isMany());
    assertEquals(expectedExternalType, transition.getEReferenceType());
    assertEquals(FlatAutomatonPackage.ASTAutomaton_Transitions,
        transition.getFeatureID());
  }
  
  @Test
  public void testMethods() {
    ASTCounter counter = ActionAutomatonNodeFactory.createASTCounter();
    aut.setCounterList(Lists.newArrayList(counter));
    
    assertTrue(aut.getCounterList().contains(counter));
  }
  
}
