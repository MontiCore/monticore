/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.testcases.automaton.AutomatonMill;
import mc.testcases.automaton._ast.ASTAutomaton;
import mc.testcases.automaton._ast.ASTState;
import mc.testcases.automaton._parser.AutomatonParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(AutomatonMill.class)
public class ListPrototypeTest {

  ASTAutomaton aut;

  @BeforeEach
  public void setUp() throws IOException {
    String inputFile = "src/main/models/automaton/AutomatonSubstateWithSubstate.aut";
    AutomatonParser parser = AutomatonMill.parser();
    Optional<ASTAutomaton> parsedAut = parser.parse(inputFile);
    assertTrue(parsedAut.isPresent());
    aut = parsedAut.get();
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
