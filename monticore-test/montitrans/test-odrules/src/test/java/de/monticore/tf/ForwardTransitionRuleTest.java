/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.se_rwth.commons.logging.Log;
import mc.testcases.automaton._ast.ASTAutomaton;
import mc.testcases.automaton._ast.ASTState;
import mc.testcases.automaton._parser.AutomatonParser;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ForwardTransitionRuleTest {

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }

  @Test
  public void testForwardTransition() throws IOException {
    String inputFile = "src/main/models/automaton/Testautomat.aut";
    AutomatonParser parser = new AutomatonParser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());
    // execute tested code and store result
    ForwardTransitionRule ftr = new ForwardTransitionRule(aut.get());

    // definition of test input
    ftr.doAll();

    // assertions
    assertEquals(2, aut.get().getStateList().size());
    assertEquals(2, aut.get().getTransitionList().size());

    ASTState hierarchicalState = aut.get().getState(1);
    assertEquals(2, hierarchicalState.getStateList().size());
    assertEquals(4, hierarchicalState.getTransitionList().size());

    assertEquals("c", aut.get().getTransition(0).getTo());
  }


  @Test
  public void testGetReplacementCanditates() throws IOException {
    String inputFile = "src/main/models/automaton/Testautomat.aut";
    AutomatonParser parser = new AutomatonParser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());

    // execute tested code and store result
    ForwardTransitionRule ftr = new ForwardTransitionRule(aut.get());

    ftr.doPatternMatching();

    List<?> allChanges = ftr.getMatches();
    assertEquals(1, allChanges.size());
  }

  @Test
  public void testDoPatternMatching() throws IOException {
    String inputFile = "src/main/models/automaton/Testautomat.aut";
    AutomatonParser parser = new AutomatonParser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());

    // execute tested code and store result
    ForwardTransitionRule ftr = new ForwardTransitionRule(aut.get());

    ftr.doPatternMatching();

    assertTrue(ftr.get_state_2().getStateList().contains(ftr.get_state_3()));
    assertTrue(ftr.get_state_3().isInitial());
    assertEquals(ftr.get_transition_1().getFrom(), ftr.get_state_1().getName());
    assertEquals(ftr.get_transition_1().getTo(), ftr.get_state_2().getName());
  }

  @Test
  public void testSet_state_2_1() throws IOException {
    String inputFile = "src/main/models/automaton/AutomatonWithTwoMatches.aut";
    AutomatonParser parser = new AutomatonParser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());

    // execute tested code and store result
    ForwardTransitionRule ftr = new ForwardTransitionRule(aut.get());

    ftr.set_state_2(aut.get().getState(1));
    ftr.doPatternMatching();
    assertEquals(1, ftr.getMatches().size());
    assertEquals(aut.get().getState(1), ftr.get_state_2());
  }

  @Test
  public void testSet_state_2_2() throws IOException {
    String inputFile = "src/main/models/automaton/AutomatonWithTwoMatches.aut";
    AutomatonParser parser = new AutomatonParser();
    Optional<ASTAutomaton> aut = parser.parse(inputFile);

    assertTrue(aut.isPresent());

    // execute tested code and store result
    ForwardTransitionRule ftr = new ForwardTransitionRule(aut.get());

    ftr.set_state_2(aut.get().getState(2));
    ftr.doPatternMatching();
    assertEquals(1, ftr.getMatches().size());
    assertEquals(aut.get().getState(2), ftr.get_state_2());
  }

}
