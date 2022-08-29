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

public class ConstraintForOptionalTest {
  
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
    ConstraintForOptional rule = new ConstraintForOptional(aut.get());

    // should not match
    assertFalse(rule.doPatternMatching());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAutomatonWithOneState() throws IOException {
    String inputFile = "src/main/models/automaton/AutomatonWithSingleState.aut";
    AutomatonParser parser = new AutomatonParser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());
    assertEquals(1, aut.get().getStateList().size());

    // execute tested code and store result
    ConstraintForOptional rule = new ConstraintForOptional(aut.get());

    // optional state is not present
    assertTrue(rule.doPatternMatching());
    assertEquals(rule.get_$A().getName(), "a");
    assertFalse(rule.get_$C().isPresent());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testIncorrectName() throws IOException {
    String inputFile = "src/main/models/automaton/AutomatonSubstateWithSubstate.aut";
    AutomatonParser parser = new AutomatonParser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());
    assertEquals(1, aut.get().getStateList().size());
    assertEquals(1, aut.get().getState(0).getStateList().size());
    assertEquals(1, aut.get().getState(0).getState(0).getStateList().size());

    // execute tested code and store result
    ConstraintForOptional rule = new ConstraintForOptional(aut.get());

    // substate has incorrect name, so it should not be matched
    assertTrue(rule.doPatternMatching());
    assertEquals(rule.get_$A().getName(), "a");
    assertFalse(rule.get_$C().isPresent());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCompleteMatch() throws IOException {
    String inputFile = "src/main/models/automaton/AutomatonTwoStatesAndSubstate.aut";
    AutomatonParser parser = new AutomatonParser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());
    assertEquals(2, aut.get().getStateList().size());
    assertEquals(1, aut.get().getState(0).getStateList().size());

    // execute tested code and store result
    ConstraintForOptional rule = new ConstraintForOptional(aut.get());

    // everything matches
    assertTrue(rule.doPatternMatching());
    assertEquals(rule.get_$A().getName(), "a");
    assertTrue(rule.get_$C().isPresent());
    assertEquals(rule.get_$C().get().getName(), "c");
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
