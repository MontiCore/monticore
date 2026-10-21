/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.testcases.automaton.AutomatonMill;
import mc.testcases.automaton._ast.ASTAutomaton;
import mc.testcases.automaton._parser.AutomatonParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(AutomatonMill.class)
public class ExpandInitialTest {

  @Test
  public void testDoAll() throws IOException {
    String inputFile = "src/main/models/automaton/AutomatonTwoStatesAndSubstate.aut";
    AutomatonParser parser = AutomatonMill.parser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());
    // execute tested code and store result
    ExpandInitial testee = new ExpandInitial(aut.get());

    // definition of test input
    testee.doAll();

    // assertions
    assertFalse(aut.get().getState(0).isInitial());
    assertFalse(aut.get().getState(1).isInitial());
    assertTrue(aut.get().getState(0).getState(0).isInitial());
  }

  @Test
  public void testUndoReplacement() throws IOException {
    String inputFile = "src/main/models/automaton/AutomatonTwoStatesAndSubstate.aut";
    AutomatonParser parser = AutomatonMill.parser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());
    // execute tested code and store result
    ExpandInitial testee = new ExpandInitial(aut.get());

    // definition of test input
    testee.doAll();
    testee.undoReplacement();

    // assertions
    assertFalse(aut.get().getState(0).isInitial());
    assertFalse(aut.get().getState(1).isInitial());
    assertFalse(aut.get().getState(0).getState(0).isInitial());
  }

}
