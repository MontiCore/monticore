/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.se_rwth.commons.logging.Log;
import mc.testcases.automaton._ast.ASTAutomaton;
import mc.testcases.automaton._ast.ASTState;
import mc.testcases.automaton._parser.AutomatonParser;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import org.junit.Ignore;

public class ListWithAssignTest {
  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }

  @Test
  public void testEmptyAutomat() throws  IOException {
    String inputFile = "src/main/models/automaton/EmptyAutomaton.aut";
    AutomatonParser parser = new AutomatonParser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());

    ListWithAssign rule = new ListWithAssign(aut.get());

    assertFalse(rule.doPatternMatching());
  }

  @Test
  public void testPatternMatching() throws IOException {
    String inputFile = "src/main/models/automaton/AutomatonWithSubstatesOfSubstates.aut";
    AutomatonParser parser = new AutomatonParser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());

    // execute tested code and store result
    ListWithAssign rule = new ListWithAssign(aut.get());

    assertTrue(rule.doPatternMatching());
    rule.doReplacement();

    assertTrue(true);
  }
}
