/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.testcases.automaton.AutomatonMill;
import mc.testcases.automaton._ast.ASTAutomaton;
import mc.testcases.automaton._ast.ASTState;
import mc.testcases.automaton._parser.AutomatonParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(AutomatonMill.class)
public class FoldStateRuleTest {

  @Test
  public void testEmptyAutomat() throws IOException {
    String inputFile = "src/main/models/automaton/AutomatonWithSingleState.aut";
    AutomatonParser parser = AutomatonMill.parser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());

    // execute tested code and store result
    FoldStateRule rule = new FoldStateRule(aut.get());

    // definition of test input
    assertTrue(rule.doPatternMatching());

    ASTState state_1 = rule.get_state_1();
    assertFalse(state_1.isInitial());
    ASTState state_2 = rule.get_state_2();
    // compare by object identity
    assertSame(state_1, state_2);
  }

}
