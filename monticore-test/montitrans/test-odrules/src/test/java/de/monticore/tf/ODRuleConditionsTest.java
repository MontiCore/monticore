/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.testcases.automaton.AutomatonMill;
import mc.testcases.automaton._ast.ASTAutomaton;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the conditions generated for special values and object names
 */
public class ODRuleConditionsTest {

  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  protected ASTAutomaton parse() throws IOException {
    return AutomatonMill.parser().parse("src/main/models/automaton/AutomatonWithSingleState.aut").orElseThrow();
  }

  @Test
  public void testStringValueWithDot() throws IOException {
    ASTAutomaton aut = parse();
    assertFalse(new StateNameWithDot(aut).doPatternMatching());

    aut.getState(0).setName("a.b");
    StateNameWithDot rule = new StateNameWithDot(aut);
    assertTrue(rule.doPatternMatching());
    assertEquals("a.b", rule.get_state_1().getName());

    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testSetStringValueWithDot() throws IOException {
    ASTAutomaton aut = parse();
    RenameStateWithDot rule = new RenameStateWithDot(aut.getState(0));
    assertTrue(rule.doPatternMatching());
    rule.doReplacement();
    assertEquals("a.b", aut.getState(0).getName());

    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testCapitalizedObjectName() throws IOException {
    ASTAutomaton aut = parse();
    CapitalizedObjectName rule = new CapitalizedObjectName(aut);
    assertTrue(rule.doPatternMatching());
    assertEquals(aut, rule.get_Automaton_1());
    assertEquals("a", rule.get_state_1().getName());

    assertTrue(Log.getFindings().isEmpty());
  }

}
