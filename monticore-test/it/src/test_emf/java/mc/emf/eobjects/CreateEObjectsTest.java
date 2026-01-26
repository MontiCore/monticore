/* (c) https://github.com/MontiCore/monticore */

package mc.emf.eobjects;

import de.monticore.emf._ast.ASTENode;
import mc.GeneratorIntegrationsTest;
import mc.feature.fautomaton.automaton.flatautomaton.FlatAutomatonMill;
import mc.feature.fautomaton.automaton.flatautomaton._ast.ASTAutomaton;
import mc.feature.fautomaton.automaton.flatautomaton._ast.ASTState;
import mc.feature.fautomaton.automaton.flatautomaton._ast.ASTTransition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CreateEObjectsTest extends GeneratorIntegrationsTest {
  @Test
  public void builderTest() {
    ASTENode ast = FlatAutomatonMill.automatonBuilder().setName(("A")).build();
    assertNotNull(ast);
    assertInstanceOf(ASTAutomaton.class, ast);
   
    ast = FlatAutomatonMill.stateBuilder().setName("s").build();
    assertNotNull(ast);
    assertInstanceOf(ASTState.class, ast);
    
    ast = FlatAutomatonMill.transitionBuilder().setActivate("t").setFrom("a").setTo("b").build();
    assertNotNull(ast);
    assertInstanceOf(ASTTransition.class, ast);
  }

}
