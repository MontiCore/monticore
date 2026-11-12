/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.testcases.automaton.AutomatonMill;
import mc.testcases.automaton._ast.ASTAutomaton;
import mc.testcases.automaton._parser.AutomatonParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(AutomatonMill.class)
public class ConstraintEmbeddingTest {

  @Test
  public void testDoPatternMatching() throws IOException {
    String inputFile = "src/main/models/automaton/Testautomat.aut";
    AutomatonParser parser = AutomatonMill.parser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());

    // execute tested code and store result
    ConstraintEmbedding ce = new ConstraintEmbedding(aut.get());

    ce.doPatternMatching();

    assertNotNull(ce.get_state_1());
  }

}
