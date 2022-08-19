/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.testcases.automaton._ast.ASTAutomaton;
import mc.testcases.automaton._parser.AutomatonParser;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ConstraintEmbeddingTest {
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testDoPatternMatching() throws IOException {
    String inputFile = "src/main/models/automaton/Testautomat.aut";
    AutomatonParser parser = new AutomatonParser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());

    // execute tested code and store result
    ConstraintEmbedding ce = new ConstraintEmbedding(aut.get());

    ce.doPatternMatching();

    assertNotNull(ce.get_state_1());
  
    assertTrue(Log.getFindings().isEmpty());
  }

}
