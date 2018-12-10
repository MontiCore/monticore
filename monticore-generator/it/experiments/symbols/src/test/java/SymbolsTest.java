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
  public void testKinds1() throws IOException {
    StateKind k1 = new StateKind();
    StateKind k2 = new StateKind();
    // Nachfolgendes stimmt leider, sollte aber nicht sein,
    // wenn Kind's singleton sind, #2074
    assertNotSame(k1,k2);
    // Passt nicht: assertEquals(k1,k2);
  }

  @Test
  public void testKinds2() throws IOException {
    StateKind k1 = new StateKind();
    StateKind k2 = new StateKind();
    assertEquals("automaton._symboltable.StateKind", k1.getName());
    assertTrue(k1.isKindOf(k2));
    assertTrue(k2.isKindOf(k1));
  }
  
  @Test
  public void testStateSymbol1() throws IOException {
    StateSymbol s1 = new StateSymbol("ping");
    StateSymbol s2 = new StateSymbol("pong");
    StateKind k1 = new StateKind();
    assertEquals(Optional.empty(),s1.getStateNode());
    assertEquals("ping",s1.getName());
    assertEquals("",s1.getPackageName());
    assertEquals("ping",s1.getFullName());
    assertTrue(s2.getKind().isKindOf(k1));
    assertTrue(s2.isKindOf(k1));
  }
  

  @Test
  public void testCount1() throws IOException {
    ASTAutomaton ast = parser.parse( "src/test/resources/example/CountPingPong.aut" ).get();
  }
  

}
