/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.testcases.automaton._ast.ASTAutomaton;
import mc.testcases.automaton._ast.ASTState;
import mc.testcases.automaton._parser.AutomatonParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ListStateTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testEmptyAutomat() throws IOException {
    String inputFile = "src/main/models/automaton/AutomatonWithTwoMatches.aut";
    AutomatonParser parser = new AutomatonParser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());

    // execute tested code and store result
    ListStateRule rule = new ListStateRule(aut.get());

    // definition of test input
    assertTrue(rule.doPatternMatching());

    ASTState state_1 = rule.get_state_1();
    assertFalse(state_1.isInitial());
    List<ASTState> list_state_1 = rule.get_list_1_state_1();
    assertEquals(2, list_state_1.size());
    for (ASTState s : list_state_1) {
      assertTrue(s.isInitial(), s.getName() + "is not initial");
    }
  
    assertTrue(Log.getFindings().isEmpty());
  }

}
