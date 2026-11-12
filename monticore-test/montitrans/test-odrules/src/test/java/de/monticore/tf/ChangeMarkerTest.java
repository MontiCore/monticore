/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.testcases.petrinet.PetrinetMill;
import mc.testcases.petrinet._ast.ASTPetrinet;
import mc.testcases.petrinet._parser.PetrinetParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(PetrinetMill.class)
public class ChangeMarkerTest {

  ChangeMarker cm;
  ASTPetrinet petri;
  
  @BeforeEach
  public void doBefore() throws IOException {
    String inputFile = "src/main/models/petrinet/TestPetriNet.pn";
    PetrinetParser parser = PetrinetMill.parser();
    
    Optional<ASTPetrinet> pOpt = parser.parse(inputFile);
    assertTrue(pOpt.isPresent());
    petri = pOpt.get();

    // execute tested code and store result
    cm = new ChangeMarker(petri);
  }

  @Test
  public void testDoPatternMatching() {
    cm.doPatternMatching();

    assertEquals(cm.get_place_1().getMarker(), cm.get_marker_1());
    assertEquals(cm.get_place_2().getMarker(), cm.get_marker_2());
    assertEquals("0", cm.get_marker_2().getAmount());
    assertEquals(cm.get_connection_1().getName(), cm.get_place_2().getName());
    assertEquals(cm.get_connection_2().getName(), cm.get_place_1().getName());
    assertTrue(cm.get_transition_1().getFromList().contains(cm.get_connection_1()));
    assertTrue(cm.get_transition_1().getToList().contains(cm.get_connection_2()));
  }

  @Test
  public void testDoReplacement() {
    cm.doPatternMatching();
    cm.doReplacement();

    assertEquals("44", cm.get_marker_1().getAmount());
    assertEquals("0", cm.get_marker_2().getAmount());
  }

}
