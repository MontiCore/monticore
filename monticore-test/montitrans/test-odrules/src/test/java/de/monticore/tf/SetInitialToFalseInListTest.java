/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.testcases.automaton.AutomatonMill;
import mc.testcases.automaton._ast.ASTState;
import mc.testcases.automaton._parser.AutomatonParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(AutomatonMill.class)
public class SetInitialToFalseInListTest {

  @Test
  public void testDoAll() throws IOException {
    AutomatonParser parser = AutomatonMill.parser();
    Optional<ASTState> sOpt = parser.parseState("src/main/models/automaton/initialState.aut");

    assertTrue(sOpt.isPresent());
    ASTState s = sOpt.get();

    assertTrue(s.isInitial());

    SetInitialToFalseInList sitfl = new SetInitialToFalseInList(s);

    assertTrue(s.isInitial());

    sitfl.doAll();
    assertFalse(s.isInitial());
  }

  @Test
  public void testUndoReplacement() throws IOException {
    AutomatonParser parser = AutomatonMill.parser();
    Optional<ASTState> sOpt = parser.parseState("src/main/models/automaton/initialState.aut");
    
    assertTrue(sOpt.isPresent());
    ASTState s = sOpt.get();

    assertTrue(s.isInitial());

    SetInitialToFalseInList sitfl = new SetInitialToFalseInList(s);

    assertTrue(s.isInitial());

    sitfl.doAll();
    sitfl.undoReplacement();
    assertTrue(s.isInitial());
  }

}
