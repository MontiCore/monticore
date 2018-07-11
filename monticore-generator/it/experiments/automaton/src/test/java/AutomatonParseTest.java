/* (c) Monticore license: https://github.com/MontiCore/monticore */
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import org.junit.Test;

import automaton._ast.ASTAutomaton;
import automaton._ast.ASTState;
import automaton._parser.AutomatonParser;

/**
 * Main class for the some Demonstration to Parse
 */
public class AutomatonParseTest {
  
  /**
   * @throws IOException 
   * 
   */
  @Test
  public void testParseMethods() throws IOException {
    
    String filename = "src/test/resources/example/PingPong.aut";
    AutomatonParser p = new AutomatonParser();
    
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
