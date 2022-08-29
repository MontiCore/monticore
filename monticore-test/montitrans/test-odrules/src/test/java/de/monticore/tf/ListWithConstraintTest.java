/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.testcases.automaton._ast.ASTAutomaton;
import mc.testcases.automaton._ast.ASTState;
import mc.testcases.automaton._parser.AutomatonParser;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import org.junit.Ignore;

public class ListWithConstraintTest {
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testPatternMatching() throws IOException {
    String inputFile = "src/main/models/automaton/AutomatonWithSubstatesOfSubstates.aut";
    AutomatonParser parser = new AutomatonParser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());

    // execute tested code and store result
    ListWithConstraint rule = new ListWithConstraint(aut.get());

    assertTrue(rule.doPatternMatching());

    assertTrue(true);
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
