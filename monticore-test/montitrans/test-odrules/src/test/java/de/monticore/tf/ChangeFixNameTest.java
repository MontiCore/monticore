/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.testcases.automaton.AutomatonMill;
import mc.testcases.automaton._ast.ASTAutomaton;
import mc.testcases.automaton._ast.ASTState;
import mc.testcases.automaton._parser.AutomatonParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(AutomatonMill.class)
public class ChangeFixNameTest {

  private ASTState state;

  @BeforeEach
  public void setUp() throws IOException {
    String inputFile = "src/main/models/automaton/AutomatonWithSingleState.aut";
    AutomatonParser parser = AutomatonMill.parser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());
    
    state = aut.get().getState(0);
  }

  @Test
  public void testDoReplacement() {
    new ChangeFixName(state).doAll();
    assertEquals("b", state.getName());
  }

  @Test
  public void testCheckConditions_state_1_1() {
    ChangeFixName testee = new ChangeFixName(state);
    assertTrue(testee.doPatternMatching());
  }

  @Test
  public void testCheckConditions_state_1_2() {
    ASTState state = AutomatonMill.stateBuilder().uncheckedBuild();
    state.setName("somename");
    state.setInitial(false);
    state.setFinal(false);
    ChangeFixName testee = new ChangeFixName(state);
    assertFalse(testee.doPatternMatching());
  }

}
