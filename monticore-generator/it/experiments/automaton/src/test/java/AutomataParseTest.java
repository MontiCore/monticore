/* (c) https://github.com/MontiCore/monticore */
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import automata._ast.ASTAutomaton;
import automata._ast.ASTState;
import automata._parser.AutomataParser;
import org.junit.Test;

/**
 * Main class for the some Demonstration to Parse
 */
public class AutomataParseTest {
  
  /**
   * @throws IOException 
   * 
   */
  @Test
  public void testParseMethods() throws IOException {

    String filename = "src/test/resources/example/PingPong.aut";
    AutomataParser p = new AutomataParser();
    
    // parse from a file
    Optional<ASTAutomaton> at = p.parse(filename);
    assertTrue(at.isPresent());
    
    // parse from a Reader object
    String aut = "automaton PingPong {"
        + "state Ping;"
        + "}";
    at = p.parse(new StringReader(aut));
    assertTrue(at.isPresent());
    
    // another parse from a String
    at = p.parse_String(aut);
    assertTrue(at.isPresent());
    
    // parse for a sublanguage, here: a State
    Optional<ASTState> s = p.parse_StringState("state Ping;");
    assertTrue(s.isPresent());
    
  }
  
}
