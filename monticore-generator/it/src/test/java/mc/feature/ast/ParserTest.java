/* (c) https://github.com/MontiCore/monticore */

package mc.feature.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.featuredsl._ast.ASTA;
import mc.feature.featuredsl._ast.ASTAutomaton;
import mc.feature.featuredsl._ast.ASTB;
import mc.feature.featuredsl._ast.ASTC;
import mc.feature.featuredsl._ast.ASTComplexname;
import mc.feature.featuredsl._ast.ASTConstants;
import mc.feature.featuredsl._ast.ASTConstantsFeatureDSL;
import mc.feature.featuredsl._ast.ASTSpices1;
import mc.feature.featuredsl._ast.ASTSpices2;
import mc.feature.featuredsl._parser.FeatureDSLParser;

public class ParserTest extends GeneratorIntegrationsTest {
  
  @Test
  public void testConstants() throws IOException {
    StringReader s = new StringReader(
        "automaton a { constants public; constants +; constants private; spices1 garlic pepper;	spices2 none;}");
    
    FeatureDSLParser p = new FeatureDSLParser();
    Optional<ASTAutomaton> opt = p.parseAutomaton(s);
    assertTrue(opt.isPresent());
    ASTAutomaton ast = opt.get();
    
    assertEquals(false, p.hasErrors());
    assertEquals("a", ast.getName());
    
    assertEquals(true, ((ASTConstants) ast.getWiredList().get(0)).isPubblic());
    assertEquals(false, ((ASTConstants) ast.getWiredList().get(0)).isPrivate());
    
    assertEquals(true, ((ASTConstants) ast.getWiredList().get(1)).isPubblic());
    assertEquals(false, ((ASTConstants) ast.getWiredList().get(1)).isPrivate());
    
    assertEquals(false, ((ASTConstants) ast.getWiredList().get(2)).isPubblic());
    assertEquals(true, ((ASTConstants) ast.getWiredList().get(2)).isPrivate());
    
    assertEquals(true, ((ASTSpices1) ast.getWiredList().get(3)).isCarlique());
    assertEquals(true, ((ASTSpices1) ast.getWiredList().get(3)).isPepper());
    
    assertEquals(ASTConstantsFeatureDSL.NONE, ((ASTSpices2) ((ASTAutomaton) ast).getWiredList().get(4)).getSpicelevel());
    
  }
  
  @Test
  public void testConstantsParseError() throws IOException {
    StringReader s = new StringReader(
        "automaton a { spices2 ;}");
    
    // Ignore std.err
    System.setOut(new PrintStream(new ByteArrayOutputStream()));
    
    FeatureDSLParser p = new FeatureDSLParser();
    p.parseAutomaton(s);
    
    assertEquals(true, p.hasErrors());
    
  }
  
  /*  Grammar:  B: A:A (B:A)*; 
   * 
   * In a previous version A and B were both lists (<- bug)
   * */
  @Test
  public void testListError() throws IOException {
    
    StringReader s = new StringReader(
        "private / private / private /");
    
    // Ignore std.err
    System.setErr(new PrintStream(new ByteArrayOutputStream()));
    
    FeatureDSLParser p = new FeatureDSLParser();
    Optional<ASTB> ast = p.parseB(s);
    
    assertEquals(false, p.hasErrors());
    assertTrue(ast.isPresent());
    assertEquals(true, ast.get().getA() instanceof ASTA);
    assertEquals(true, ast.get().getBList() instanceof List);
    
  }
  
  /*  Grammar:  B: A:A (A:A)*; 
   * 
   *  A has to be of type ASTAList
   * */
  @Test
  public void testListError2() throws IOException {
    
    StringReader s = new StringReader(
        "private / private / private /");
    
    // Ignore std.err
    System.setErr(new PrintStream(new ByteArrayOutputStream()));
    
    FeatureDSLParser p = new FeatureDSLParser();
    Optional<ASTC> ast = p.parseC(s);
    
    assertTrue(ast.isPresent());
    assertEquals(false, p.hasErrors());
    assertEquals(true, ast.get().getAList() instanceof List);
    
  }
  
  /*  Grammar: 
   * 
   *  A has to be of type ASTAList
   * */
  @Test
  public void testListError3() throws IOException {
    
    StringReader s = new StringReader(
        "private / private / private /");
    
    // Ignore std.err
    System.setErr(new PrintStream(new ByteArrayOutputStream()));
    
    FeatureDSLParser p = new FeatureDSLParser();
    Optional<ASTComplexname> ast = p.parseComplexname(s);
    
    assertFalse(ast.isPresent());    
  }
  
}
