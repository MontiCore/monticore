/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import com.google.common.collect.Lists;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.testcases.automaton._ast.ASTAutomaton;
import mc.testcases.automaton._ast.ASTState;
import mc.testcases.automaton._parser.AutomatonParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ListPrototypeTest {

  ASTAutomaton aut;
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @BeforeEach
  public void setUp() throws IOException {
    String inputFile = "src/main/models/automaton/AutomatonSubstateWithSubstate.aut";
    AutomatonParser parser = new AutomatonParser();
    aut = parser.parse(inputFile).get();

  }

  @Test
  public void testDoPatternMatching() {
    ListPrototype testee = new ListPrototype(aut);
    ArrayList<ASTState> states = new ArrayList<>();
    assertTrue(testee.doPatternMatching());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDoAll() {
    ListPrototype testee = new ListPrototype(aut);
    assertTrue(testee.doPatternMatching());

    testee.doReplacement();
    assertEquals(1, aut.getStateList().size());
    assertEquals("a", aut.getStateList().get(0).getName());
    assertEquals(2, aut.getStateList().get(0).getStateList().size());
    assertEquals("b", aut.getStateList().get(0).getStateList().get(0).getName());
    assertEquals("c", aut.getStateList().get(0).getStateList().get(1).getName());

    testee.undoReplacement();
    assertEquals(1, aut.getStateList().size());
    assertEquals("a", aut.getStateList().get(0).getName());
    assertEquals(1, aut.getStateList().get(0).getStateList().size());
    assertEquals("b", aut.getStateList().get(0).getStateList().get(0).getName());
    assertEquals(1, aut.getStateList().get(0).getStateList().get(0).getStateList().size());
    assertEquals("c", aut.getStateList().get(0).getStateList().get(0).getStateList().get(0).getName());
  
    assertTrue(Log.getFindings().isEmpty());
  }

}
