/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.se_rwth.commons.logging.Log;
import mc.testcases.automaton.AutomatonMill;
import mc.testcases.automaton._ast.ASTAutomaton;
import mc.testcases.automaton._ast.ASTState;
import mc.testcases.automaton._parser.AutomatonParser;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class ChangeFixNameTest {

  private ASTState state;

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }

  @Before
  public void setUp() throws IOException {
    String inputFile = "src/main/models/automaton/AutomatonWithSingleState.aut";
    AutomatonParser parser = new AutomatonParser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());


    state = aut.get().getState(0);
  }

  @Test
  public void testDoReplacment() {
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
