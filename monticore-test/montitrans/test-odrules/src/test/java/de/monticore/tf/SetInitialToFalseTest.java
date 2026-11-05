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
public class SetInitialToFalseTest {
  
  @Test
  public void testDoAll() throws IOException {
    AutomatonParser parser = AutomatonMill.parser();
    Optional<ASTState> sOpt = parser.parseState("src/main/models/automaton/initialState.aut");

    assertTrue(sOpt.isPresent());
    ASTState s = sOpt.get();

    assertTrue(s.isInitial());

    SetInitialToFalse sitf = new SetInitialToFalse(s);

    assertTrue(s.isInitial());

    sitf.doAll();

    assertFalse(s.isInitial());
  }

  @Test
  public void testDoAll2() throws IOException {
    AutomatonParser parser = AutomatonMill.parser();
    Optional<ASTAutomaton> aOpt = parser.parse("src/main/models/automaton/Testautomat.aut");

    assertTrue(aOpt.isPresent());
    ASTAutomaton a = aOpt.get();

    SetInitialToFalse sitf = new SetInitialToFalse(a);

    // first try
    sitf.doAll();
    ASTState match_1 = sitf.get_state_1();

    // backtracking
    sitf.undoReplacement();

    // second run
    assertTrue(sitf.doPatternMatching());
    sitf.doReplacement();
    ASTState match_2 = sitf.get_state_1();
    assertNotSame(match_1, match_2);
  }
}
