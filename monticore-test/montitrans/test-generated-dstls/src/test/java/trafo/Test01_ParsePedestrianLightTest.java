/* (c) https://github.com/MontiCore/monticore */
package trafo;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.testcases.statechart.statechart.StatechartMill;
import mc.testcases.statechart.statechart._ast.ASTStatechart;
import mc.testcases.statechart.statechart._parser.StatechartParser;

import java.io.IOException;
import java.util.Optional;

import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(StatechartMill.class)
public class Test01_ParsePedestrianLightTest {
  
  @Test
  public void testDoAll() throws IOException {
    StatechartParser px = StatechartMill.parser();
    Optional<ASTStatechart> sc = px.parse("src/test/resources/trafo/PedestrianLight.sc");

    assertTrue(sc.isPresent());
    assertFalse(px.hasErrors());
    assertTrue(Log.getFindings().isEmpty());
  }

}
