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

public class FlattenStateTest {
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  ASTAutomaton aut;

  @Test
  public void testAutomatonWithOneState() throws IOException {
    String inputFile = "src/main/models/automaton/AutomatonWithSingleState.aut";
    AutomatonParser parser = new AutomatonParser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());
    assertEquals(1, aut.get().getStateList().size());

    // execute tested code and store result
    FlattenState rule = new FlattenState(aut.get());

    // should not match
    assertFalse(rule.doPatternMatching());
  
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
    FlattenState rule = new FlattenState(aut.get());

    // pattern should match
    assertTrue(rule.doPatternMatching());
    rule.doReplacement();

    // state 1 should be flattened
    assertEquals(3, aut.get().getStateList().size());
    assertEquals(0, aut.get().getState(0).getStateList().size());
    assertEquals(0, aut.get().getState(1).getStateList().size());
    assertEquals(0, aut.get().getState(2).getStateList().size());

    // test undo replacement TODO: doesn't work
    /*rule.undoReplacement();
    assertEquals(2, aut.get().getStateList().size());
    assertEquals(1, aut.get().getState(0).getStateList().size());
    assertEquals(0, aut.get().getState(1).getStateList().size());*/
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testAutomatonWithTwoSubstates() throws IOException {
    String inputFile = "src/main/models/automaton/AutomatonTwoSubstates.aut";
    AutomatonParser parser = new AutomatonParser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());
    assertEquals(1, aut.get().getStateList().size());
    assertEquals(2, aut.get().getState(0).getStateList().size());

    // execute tested code and store result
    FlattenState rule = new FlattenState(aut.get());

    // pattern should match
    assertFalse(rule.doPatternMatching());
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
