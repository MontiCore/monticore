/* (c) https://github.com/MontiCore/monticore */

import automata._ast.ASTAutomaton;
import automata._parser.AutomataParser;
import automata._symboltable.StateSymbol;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class AutomataSymbolsTest {
  
  // setup the language infrastructure
  AutomataParser parser = new AutomataParser() ;

  @BeforeClass
  public static void init() {
    // replace log by a sideffect free variant
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Before
  public void setUp() throws RecognitionException, IOException {
    Log.getFindings().clear();
  }
  
  // --------------------------------------------------------------------
  // Check Symbol Infrastucture
  // --------------------------------------------------------------------

  @Test
  public void testSymbol1() throws IOException {
    ASTAutomaton ast = parser.parse( "src/test/resources/example/HierarchyPingPong.aut" ).get();
    assertEquals("PingPong", ast.getName());
    assertEquals(2, ast.getTransitionList().size());
  }
  
  @Test
  public void testStateSymbol1() throws IOException {
    StateSymbol s1 = new StateSymbol("ping");
    StateSymbol s2 = new StateSymbol("pong");
    assertFalse(s1.isPresentAstNode());
    assertEquals("ping",s1.getName());
    assertEquals("",s1.getPackageName());
    assertEquals("ping",s1.getFullName());
  }
  

  @Test
  public void testCount1() throws IOException {
    ASTAutomaton ast = parser.parse( "src/test/resources/example/CountPingPong.aut" ).get();
  }
  

}
