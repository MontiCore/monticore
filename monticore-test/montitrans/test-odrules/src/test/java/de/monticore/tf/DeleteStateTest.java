/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.testcases.automaton.AutomatonMill;
import mc.testcases.automaton._ast.ASTAutomaton;
import mc.testcases.automaton._parser.AutomatonParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(AutomatonMill.class)
public class DeleteStateTest {

  ASTAutomaton aut;

  @BeforeEach
  public void setUp() throws IOException {
    String inputFile = "src/main/models/automaton/AutomatonWithSingleState.aut";
    AutomatonParser parser = AutomatonMill.parser();
    Optional<ASTAutomaton> autOpt = parser.parse(inputFile);
    assertTrue(autOpt.isPresent());
    aut = autOpt.get();
  }

  @Test
  public void testDoReplacment() {
    int noOfStates_Before = aut.getStateList().size();
    new DeleteState(aut).doAll();
    assertEquals(noOfStates_Before - 1, aut.getStateList().size());
  }

  @Test
  public void testUndoReplacment() {
    int noOfStates_Before = aut.getStateList().size();
    DeleteState testee = new DeleteState(aut);
    testee.doAll();
    testee.undoReplacement();

    assertEquals(noOfStates_Before, aut.getStateList().size());
  }

}
