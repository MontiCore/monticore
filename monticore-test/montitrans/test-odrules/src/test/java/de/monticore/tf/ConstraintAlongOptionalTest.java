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

import static org.junit.Assert.*;

public class ConstraintAlongOptionalTest {
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  ASTAutomaton aut;

  @Test
  public void testEmptyAutomaton() throws IOException {
    String inputFile = "src/main/models/automaton/EmptyAutomaton.aut";
    AutomatonParser parser = new AutomatonParser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());
    assertEquals(0, aut.get().getStateList().size());

    // execute tested code and store result
    ConstraintAlongOptional rule = new ConstraintAlongOptional(aut.get());

    // should not match
    assertFalse(rule.doPatternMatching());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testConstraintDoesNotHold() throws IOException {
    String inputFile = "src/main/models/automaton/AutomatonWithSingleState.aut";
    AutomatonParser parser = new AutomatonParser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());
    assertEquals(1, aut.get().getStateList().size());
    assertEquals("a", aut.get().getState(0).getName());

    // execute tested code and store result
    ConstraintAlongOptional rule = new ConstraintAlongOptional(aut.get());

    // the state does not have the correct name
    assertFalse(rule.doPatternMatching());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testConstraintHolds() throws IOException {
    String inputFile = "src/main/models/automaton/AutomatonTwoStatesAndSubstate_2.aut";
    AutomatonParser parser = new AutomatonParser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());
    assertEquals(2, aut.get().getStateList().size());
    assertEquals(1, aut.get().getState(1).getStateList().size());
    assertEquals("b", aut.get().getState(1).getName());

    // execute tested code and store result
    ConstraintAlongOptional rule = new ConstraintAlongOptional(aut.get());

    // one state should match
    assertTrue(rule.doPatternMatching());
    assertEquals("b", rule.get_$State().getName());
    assertTrue(rule.get_state_2().isPresent());
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
