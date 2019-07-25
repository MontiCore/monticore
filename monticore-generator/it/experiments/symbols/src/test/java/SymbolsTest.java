/* (c) Monticore license: https://github.com/MontiCore/monticore */

import java.util.Optional;

import java.io.IOException;
import java.util.ArrayList;
import static org.junit.Assert.*;
import org.antlr.v4.runtime.RecognitionException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import automaton._ast.*;
import automaton._parser.*;
import automaton._symboltable.*;
import automaton._visitor.*;
import de.se_rwth.commons.logging.*;

public class SymbolsTest {
  
  // setup the language infrastructure
  AutomatonParser parser = new AutomatonParser() ;

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
    assertEquals(Optional.empty(),s1.getAstNode());
    assertEquals("ping",s1.getName());
    assertEquals("",s1.getPackageName());
    assertEquals("ping",s1.getFullName());
  }
  

  @Test
  public void testCount1() throws IOException {
    ASTAutomaton ast = parser.parse( "src/test/resources/example/CountPingPong.aut" ).get();
  }
  

}
