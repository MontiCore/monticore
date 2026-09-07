/* (c) https://github.com/MontiCore/monticore */
package trafo;

import de.se_rwth.commons.logging.LogStub;
import mc.testcases.statechart.statechart._ast.ASTStatechart;
import mc.testcases.statechart.statechart._parser.StatechartParser;

import java.io.IOException;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Test01_ParsePedestrianLightTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testDoAll() throws IOException {
    StatechartParser px = new StatechartParser();
    ASTStatechart sc =px.parse("src/test/resources/trafo/PedestrianLight.sc").get();

    assertNotNull(sc);
    assertFalse(px.hasErrors());
    assertTrue(Log.getFindings().isEmpty());
  }

}
