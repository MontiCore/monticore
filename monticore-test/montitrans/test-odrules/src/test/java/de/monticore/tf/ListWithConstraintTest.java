/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.testcases.automaton.AutomatonMill;
import mc.testcases.automaton._ast.ASTAutomaton;
import mc.testcases.automaton._parser.AutomatonParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(AutomatonMill.class)
public class ListWithConstraintTest {
 
  @Test
  public void testPatternMatching() throws IOException {
    String inputFile = "src/main/models/automaton/AutomatonWithSubstatesOfSubstates.aut";
    AutomatonParser parser = AutomatonMill.parser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());

    // execute tested code and store result
    ListWithConstraint rule = new ListWithConstraint(aut.get());

    assertTrue(rule.doPatternMatching());
  }
}
