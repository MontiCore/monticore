/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tf;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import mc.testcases.petrinet.PetrinetMill;
import mc.testcases.petrinet._ast.ASTPetrinet;
import mc.testcases.petrinet._ast.ASTPlacedeclaration;
import mc.testcases.petrinet._parser.PetrinetParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Transformation of an explicitly named list (places:Placedeclaration*),
 * whose accessors keep the plural name (getPlacesList)
 */
public class DeletePlaceTest {

  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testDeletePlace() throws IOException {
    PetrinetParser parser = PetrinetMill.parser();
    ASTPetrinet petri = parser.parse("src/main/models/petrinet/TestPetriNet.pn").orElseThrow();

    DeletePlace rule = new DeletePlace(petri);
    assertTrue(rule.doPatternMatching());
    assertEquals("b", rule.get_place_1().getName());

    rule.doReplacement();
    assertEquals(List.of("a", "c"),
        petri.getPlacesList().stream().map(ASTPlacedeclaration::getName).toList());

    assertTrue(Log.getFindings().isEmpty());
  }

}
