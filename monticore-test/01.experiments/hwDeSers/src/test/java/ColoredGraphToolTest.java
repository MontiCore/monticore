/* (c) https://github.com/MontiCore/monticore */

import coloredgraph.ColoredGraphTool;
import coloredgraph._symboltable.*;
import de.monticore.runtime.junit.MCAssertions;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class ColoredGraphToolTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  /**
   * Use the single argument for specifying the single input model file.
   * Then execute the tool and assert that a file has been created at the
   * expected location
   */
  @Test
  public void testTrafficLight() {
    LogStub.init();
    //    Log.enableFailQuick(false);
    ColoredGraphTool.main(new String[] { "-i", "src/test/resources/TrafficLight.cg" });
    File stFile = new File("target/src/test/resources/TrafficLight.cgsym");
    assertTrue(stFile.exists());
    MCAssertions.assertNoFindings();
  }

  @Test
  public void testBlinker() {
    LogStub.init();
    //    Log.enableFailQuick(false);

    // store scope and check that file exists
    ColoredGraphTool.main(new String[] { "-i", "src/test/resources/Blinker.cg" });
    File stFile = new File("target/src/test/resources/Blinker.cgsym");
    assertTrue(stFile.exists());

    // load the stored file and check that important things in the artifact scope are still there
    IColoredGraphArtifactScope as = new ColoredGraphSymbols2Json()
        .load("target/src/test/resources/Blinker.cgsym");
    assertEquals("Blinker", as.getName());
    assertEquals(2, as.getNumberOfColors());
    assertEquals(1, as.getSymbolsSize());
    assertEquals(1, as.getLocalGraphSymbols().size());

    GraphSymbol graphSymbol = as.getLocalGraphSymbols().get(0);
    assertEquals("Blinker", graphSymbol.getName());
    assertEquals(as, graphSymbol.getEnclosingScope());

    IColoredGraphScope graphScope = graphSymbol.getSpannedScope();
    assertEquals(2, graphScope.getSymbolsSize());
    assertEquals(2, graphScope.getLocalVertexSymbols().size());

    VertexSymbol offSymbol = graphScope.getLocalVertexSymbols().get(0);
    assertEquals("Off", offSymbol.getName());
    assertEquals(new Color(0, 0, 0), offSymbol.getColor());
    assertTrue(offSymbol.isInitial());

    VertexSymbol blinkSymbol = graphScope.getLocalVertexSymbols().get(1);
    assertEquals("BlinkBlue", blinkSymbol.getName());
    assertEquals(new Color(0, 0, 204), blinkSymbol.getColor());
    assertFalse(blinkSymbol.isInitial());
    MCAssertions.assertNoFindings();
  }

}
