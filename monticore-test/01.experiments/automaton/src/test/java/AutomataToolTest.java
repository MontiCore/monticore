/* (c) https://github.com/MontiCore/monticore */

import automata.AutomataMill;
import automata.AutomataTool;
import de.monticore.runtime.junit.MCAssertions;
import org.junit.*;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.regex.Pattern;

import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class AutomataToolTest {

  @BeforeEach
  public void setUp() {
    LogStub.init();
    Log.enableFailQuick(false);
    Log.clearFindings();
    LogStub.clearPrints();
    AutomataMill.reset();
  }
  
  @Test
  public void executePingPong() {
    new AutomataTool().run(new String[] { "-i", "src/test/resources/example/PingPong.aut", "-s", "target/PingPong.autsym" });
    Log.printFindings();
    assertEquals(0, Log.getFindings().size());
    // LogStub.printPrints();  // for manual testing purpose only

    List<String> p = LogStub.getPrints();
    assertEquals(6, p.size());

    // Check some "[INFO]" outputs
    assertTrue(p.get(0).matches(".*.INFO.  AutomataTool Automata DSL Tool.*(\r)?\n"), p.get(0));
    assertTrue(p.get(4).matches(".*.INFO.  AutomataTool Pretty printing automaton into console.*(\r)?\n"), p.get(4));
  
    // Check resulting pretty print:
    String res = p.get(p.size()-1).replaceAll("[\r\n]", " ");
    assertTrue(res.matches(".*state.*"), res);
    assertTrue(res.matches(".*state NoGame <<initial>>.*"), res);
    assertTrue(res.matches(".*Pong - returnBall > Ping;.*"), res);
    MCAssertions.assertNoFindings();
  }
  
  @Test
  public void executeSimple12() {
    new AutomataTool().run(new String[] { "-i", "src/test/resources/example/Simple12.aut", "-s", "target/Simple12.autsym" });
    Log.printFindings();
    assertEquals(0, Log.getFindings().size());
    // LogStub.printPrints();
    List<String> p = LogStub.getPrints();
    assertEquals(6, p.size());
    MCAssertions.assertNoFindings();
  }
  
  @Test
  public void executeHierarchyPingPong() {
    new AutomataTool().run(new String[] { "-i", "src/test/resources/example/HierarchyPingPong.aut", "-s", "target/very/very/very/deep/HierarchyPingPong.autsym" });
    Log.printFindings();
    assertEquals(0, Log.getFindings().size());
    // LogStub.printPrints();
    List<String> p = LogStub.getPrints();
    assertEquals(6, p.size());
    MCAssertions.assertNoFindings();
  }

  @Test
  public void testPrintVersion() {
   new AutomataTool().run(new String[] {"-v"});
    MCAssertions.assertNoFindings();
  }

}
