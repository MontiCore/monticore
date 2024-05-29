/* (c) https://github.com/MontiCore/monticore */

import automata._ast.ASTAutomaton;
import automata._ast.ASTState;
import automata._parser.AutomataParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Main class for the some Demonstration to Parse
 */
public class LexicalModesParseTest {
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
    Log.clearFindings();
    LogStub.clearPrints();
  }

  @Test
  public void testParse1() throws IOException {
    AutomataParser p = new AutomataParser();
    String aut = "automaton PingPong {"
            + "state Ping;"
            + "}";
    Optional<ASTAutomaton> at = p.parse_String(aut);
    assertTrue(at.isPresent());
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testParse2() throws IOException {
    AutomataParser p = new AutomataParser();
    String aut = "automaton PingPong {"
            + "state Ping;"
            + "<dies ist beliebiger Text mit state>"
            + "}";
    Optional<ASTAutomaton> at = p.parse_String(aut);
    assertTrue(at.isPresent());
    ASTAutomaton ast = at.get();
    assertEquals(1, ast.sizeStates());
    assertEquals(1, ast.sizeTags());
    assertEquals("dies ist beliebiger Text mit state", ast.getTag(0).getText2());
    assertTrue(Log.getFindings().isEmpty());
  }

}
