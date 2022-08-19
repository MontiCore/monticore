/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.testcases.automaton._ast.ASTAutomaton;
import mc.testcases.automaton._parser.AutomatonParser;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class FlattenStateWithAtMostTwoSubstatesTest {
  
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
    FlattenStateWithAtMostTwoSubstates rule = new FlattenStateWithAtMostTwoSubstates(aut.get());

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
    assertEquals(0, aut.get().getState(0).getStateList().size());

    // execute tested code and store result
    FlattenStateWithAtMostTwoSubstates rule = new FlattenStateWithAtMostTwoSubstates(aut.get());

    // one state should match
    assertTrue(rule.doPatternMatching());
    assertNotNull(rule.get_automaton_1());
    assertNotNull(rule.get_state_1());
    // the optionals were not found
    assertFalse(rule.get_state_2().isPresent());
    assertFalse(rule.get_state_3().isPresent());

    rule.doReplacement();

    // nothing changes
    assertEquals(1, aut.get().getStateList().size());
    assertEquals(0, aut.get().getState(0).getStateList().size());

    // test undo replacement
    rule.undoReplacement();
    assertEquals(1, aut.get().getStateList().size());
    assertEquals(0, aut.get().getState(0).getStateList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAutomatonWithTwoStatesAndSubstate() throws IOException {
    String inputFile = "src/main/models/automaton/AutomatonTwoStatesAndSubstate.aut";
    AutomatonParser parser = new AutomatonParser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());
    assertEquals(2, aut.get().getStateList().size());
    assertEquals(1, aut.get().getState(0).getStateList().size());
    assertEquals(0, aut.get().getState(1).getStateList().size());

    // execute tested code and store result
    FlattenStateWithAtMostTwoSubstates rule = new FlattenStateWithAtMostTwoSubstates(aut.get());

    // pattern should match
    assertTrue(rule.doPatternMatching());
    assertNotNull(rule.get_automaton_1());
    assertNotNull(rule.get_state_1());
    // one of the optionals should be found, the other not
    if (rule.get_state_2().isPresent()) {
      assertFalse(rule.get_state_3().isPresent());
    }
    else {
      assertTrue(rule.get_state_3().isPresent());
    }

    rule.doReplacement();

    // state 1 should be flattened
    assertEquals(3, aut.get().getStateList().size());
    assertEquals(0, aut.get().getState(0).getStateList().size());
    assertEquals(0, aut.get().getState(1).getStateList().size());
    assertEquals(0, aut.get().getState(2).getStateList().size());

    // test undo replacement
    rule.undoReplacement();
    assertEquals(2, aut.get().getStateList().size());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  // Todo: patternMatching doesnt terminate
  @Ignore
  @Test
  public void testAutomatonWithThreeSubstates() throws IOException {
    String inputFile = "src/main/models/automaton/AutomatonStateWithThreeSubstates.aut";
    AutomatonParser parser = new AutomatonParser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());
    assertEquals(1, aut.get().getStateList().size());
    assertEquals(3, aut.get().getState(0).getStateList().size());

    // execute tested code and store result
    FlattenStateWithAtMostTwoSubstates rule = new FlattenStateWithAtMostTwoSubstates(aut.get());

    // pattern should match
    assertFalse(rule.doPatternMatching());
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
