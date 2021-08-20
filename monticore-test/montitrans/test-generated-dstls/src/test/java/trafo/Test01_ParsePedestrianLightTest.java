/* (c) https://github.com/MontiCore/monticore */
package trafo;

import junit.framework.TestCase;
import mc.testcases.statechart.statechart._ast.ASTStatechart;
import mc.testcases.statechart.statechart._parser.StatechartParser;

import java.io.IOException;

public class Test01_ParsePedestrianLightTest extends TestCase {

  public void testDoAll() throws IOException {
    StatechartParser px = new StatechartParser();
    ASTStatechart sc =px.parse("src/test/resources/trafo/PedestrianLight.sc").get();

    assertNotNull(sc);
    assertFalse(px.hasErrors());
  }

}
