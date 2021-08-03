/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import com.google.common.collect.Lists;
import de.se_rwth.commons.logging.Log;
import mc.testcases.automaton._ast.ASTAutomaton;
import mc.testcases.automaton._ast.ASTState;
import mc.testcases.automaton._parser.AutomatonParser;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ListPrototypeTest {

  ASTAutomaton aut;

  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }

  @Before
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
  }

}
