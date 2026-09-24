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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(AutomatonMill.class)
public class DeleteTransitionsTest {

  @Test
  public void testDeleteTransitions() throws IOException {
    String inputFile = "src/main/models/automaton/Testautomat.aut";
    AutomatonParser parser = AutomatonMill.parser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());
    // execute tested code and store result
    DeleteTransitions dtr = new DeleteTransitions(aut.get());

    // definition of test input
    dtr.doAll();

    // assertions
    assertEquals(2, aut.get().getStateList().size());
    assertEquals(2, aut.get().getTransitionList().size());

    ASTState hierarchicalState = aut.get().getState(1);
    assertEquals(2, hierarchicalState.getStateList().size());
    assertEquals(0, hierarchicalState.getTransitionList().size());

    // and undo
    dtr.undoReplacement();

    // assertions
    assertEquals(2, aut.get().getStateList().size());
    assertEquals(2, aut.get().getTransitionList().size());

    assertEquals(2, hierarchicalState.getStateList().size());
    assertEquals(4, hierarchicalState.getTransitionList().size());
  }



}
