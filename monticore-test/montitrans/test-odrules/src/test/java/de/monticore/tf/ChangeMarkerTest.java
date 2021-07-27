/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.se_rwth.commons.logging.Log;
import mc.testcases.petrinet._ast.ASTPetrinet;
import mc.testcases.petrinet._parser.PetrinetParser;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ChangeMarkerTest {

  ChangeMarker cm;
  ASTPetrinet petri;

  @BeforeClass
  public static void disableFailQuick() {

    Log.enableFailQuick(false);
  }

  @Before
  public void doBefore() throws IOException {
    String inputFile = "src/main/models/petrinet/TestPetriNet.pn";
    PetrinetParser parser = new PetrinetParser();
     petri = parser.parse(inputFile).get();

    // execute tested code and store result
    cm = new ChangeMarker(petri);
  }

  @Test
  public void testDoPatternMatching() {
    cm.doPatternMatching();

    assertEquals(cm.get_place_1().getMarker(), cm.get_marker_1());
    assertEquals(cm.get_place_2().getMarker(), cm.get_marker_2());
    assertEquals(cm.get_marker_2().getAmount(), "0");
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
