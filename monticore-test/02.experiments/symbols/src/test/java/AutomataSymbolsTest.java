/* (c) https://github.com/MontiCore/monticore */

import automata._ast.ASTAutomaton;
import automata._parser.AutomataParser;
import automata._symboltable.StateSymbol;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class AutomataSymbolsTest {
  
  // setup the language infrastructure
  AutomataParser parser = new AutomataParser() ;

  @BeforeEach
  public void init() {
    // replace log by a sideffect free variant
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @BeforeEach
  public void setUp() throws RecognitionException, IOException {
    Log.getFindings().clear();
  }
  
  // --------------------------------------------------------------------
  // Check Symbol Infrastucture
  // --------------------------------------------------------------------

  @Test
  public void testSymbol1() throws IOException {
    ASTAutomaton ast = parser.parse( "src/test/resources/example/HierarchyPingPong.aut" ).get();
    Assertions.assertEquals("PingPong", ast.getName());
    Assertions.assertEquals(2, ast.getTransitionList().size());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testStateSymbol1() throws IOException {
    StateSymbol s1 = new StateSymbol("ping");
    StateSymbol s2 = new StateSymbol("pong");
    Assertions.assertFalse(s1.isPresentAstNode());
    Assertions.assertEquals("ping", s1.getName());
    Assertions.assertEquals("", s1.getPackageName());
    Assertions.assertEquals("ping", s1.getFullName());
    // Test the generated toString method()
    Assertions.assertEquals("StateSymbol{fullName='ping', sourcePosition=<0,0>}", s1.toString());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  

  @Test
  public void testCount1() throws IOException {
    ASTAutomaton ast = parser.parse( "src/test/resources/example/CountPingPong.aut" ).get();
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  

}
