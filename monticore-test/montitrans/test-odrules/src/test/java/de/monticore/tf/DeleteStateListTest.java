/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import com.google.common.collect.Lists;
import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.testcases.automaton.AutomatonMill;
import mc.testcases.automaton._ast.ASTAutomaton;
import mc.testcases.automaton._ast.ASTState;
import mc.testcases.automaton._parser.AutomatonParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(AutomatonMill.class)
public class DeleteStateListTest {

  ASTAutomaton aut;

  @BeforeEach
  public void setUp() throws IOException {
    String inputFile = "src/main/models/automaton/AutomatonTwoStatesAndSubstate.aut";
    AutomatonParser parser = AutomatonMill.parser();
    Optional<ASTAutomaton> autOpt = parser.parse(inputFile);
    assertTrue(autOpt.isPresent());
    aut = autOpt.get();
  }

  @Test
  public void testDoReplacement() {
    DeleteStateList testee = new DeleteStateList(aut);
    assertTrue(testee.doPatternMatching());
    testee.doReplacement();
    assertTrue(aut.getState(0).getStateList().isEmpty());
  }

  @Test
  public void testUndoReplacement() {
    List<ASTState> list_before = Lists.newArrayList(aut.getStateList());

    DeleteStateList testee = new DeleteStateList(aut);
    testee.doAll();
    testee.undoReplacement();

    assertEquals(2, aut.getStateList().size());
    assertTrue(list_before.containsAll(aut.getStateList()));
    assertTrue(aut.getStateList().containsAll(list_before));
  }

}
