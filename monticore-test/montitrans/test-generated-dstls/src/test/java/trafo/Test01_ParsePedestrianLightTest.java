/* (c) https://github.com/MontiCore/monticore */
package trafo;

import de.se_rwth.commons.logging.LogStub;
import junit.framework.TestCase;
import mc.testcases.statechart.statechart._ast.ASTStatechart;
import mc.testcases.statechart.statechart._parser.StatechartParser;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.IOException;
import de.se_rwth.commons.logging.Log;

public class Test01_ParsePedestrianLightTest extends TestCase {
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  public void testDoAll() throws IOException {
    StatechartParser px = new StatechartParser();
    ASTStatechart sc =px.parse("src/test/resources/trafo/PedestrianLight.sc").get();

    assertNotNull(sc);
    assertFalse(px.hasErrors());
    assertTrue(Log.getFindings().isEmpty());
  }

}
