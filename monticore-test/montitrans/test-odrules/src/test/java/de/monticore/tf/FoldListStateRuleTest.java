/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.testcases.automaton.AutomatonMill;
import mc.testcases.automaton._ast.ASTAutomaton;
import mc.testcases.automaton._ast.ASTState;
import mc.testcases.automaton._parser.AutomatonParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(AutomatonMill.class)
public class FoldListStateRuleTest {

  @Test
  public void testEmptyAutomat() throws IOException {
    String inputFile = "src/main/models/automaton/AutomatonWithSingleState.aut";
    AutomatonParser parser = AutomatonMill.parser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());

    // execute tested code and store result
    FoldListStateRule rule = new FoldListStateRule(aut.get());

    // definition of test input
    assertTrue(rule.doPatternMatching());

    ASTState state_1 = rule.get_state_1();
    assertFalse(state_1.isInitial());
    List<ASTState> list_state_1 = rule.get_list_1_state_1();
    assertEquals(1, list_state_1.size());
    assertTrue(list_state_1.contains(state_1));
  }

}
