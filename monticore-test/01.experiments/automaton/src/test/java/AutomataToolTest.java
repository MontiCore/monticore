/* (c) https://github.com/MontiCore/monticore */

import automata.AutomataMill;
import automata.AutomataTool;
import org.junit.*;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.regex.Pattern;

import de.se_rwth.commons.logging.LogStub;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;


public class AutomataToolTest {
  
  @BeforeClass
  public static void init() {
    LogStub.init();         // replace log by a sideffect free variant
    // LogStub.initPlusLog();  // for manual testing purpose only
    Log.enableFailQuick(false);
  }
  
  @Before
  public void setUp() {
    Log.clearFindings();
    LogStub.clearPrints();
    AutomataMill.globalScope().clear();
  }
  
  @Test
  public void executePingPong() {
    AutomataTool.main(new String[] { "src/test/resources/example/PingPong.aut", "target/PingPong.autsym" });
    Log.printFindings();
    assertEquals(0, Log.getFindings().size());
    // LogStub.printPrints();  // for manual testing purpose only

    List<String> p = LogStub.getPrints();
    assertEquals(6, p.size());

    // Check some "[INFO]" outputs
    assertTrue(p.get(0), p.get(0).matches(".*.INFO.  AutomataTool Automata DSL Tool.*\n"));
    assertTrue(p.get(4), p.get(4).matches(".*.INFO.  AutomataTool Pretty printing automaton into console.*\n"));
  
    // Check resulting pretty print:
    String res = p.get(p.size()-1).replaceAll("[\r\n]", " ");
    assertEquals(231, res.length());
    assertTrue(res, res.matches(".*state.*"));
    assertTrue(res, res.matches(".*state NoGame <<initial>>.*"));
    assertTrue(res, res.matches(".*Pong - returnBall > Ping;.*"));
  }
  
  @Test
  public void executeSimple12() {
    AutomataTool.main(new String[] { "src/test/resources/example/Simple12.aut", "target/Simple12.autsym" });
    Log.printFindings();
    assertEquals(0, Log.getFindings().size());
    // LogStub.printPrints();
    List<String> p = LogStub.getPrints();
    assertEquals(6, p.size());
  }
  
  @Test
  public void executeHierarchyPingPong() {
    AutomataTool.main(new String[] { "src/test/resources/example/HierarchyPingPong.aut", "target/very/very/very/deep/HierarchyPingPong.autsym" });
    Log.printFindings();
    assertEquals(0, Log.getFindings().size());
    // LogStub.printPrints();
    List<String> p = LogStub.getPrints();
    assertEquals(6, p.size());
  }

  @Test
  public void testPrintVersion() {


    AutomataTool.main(new String[] {"-v"});
  }
  
}
