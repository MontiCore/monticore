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

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testConstants() throws IOException {
    StringReader s = new StringReader(
        "automaton a { constants public; constants +; constants private; spices1 garlic pepper;	spices2 none;}");
    
    FeatureDSLParser p = new FeatureDSLParser();
    Optional<ASTAutomaton> opt = p.parseAutomaton(s);
    Assertions.assertTrue(opt.isPresent());
    ASTAutomaton ast = opt.get();
    
    Assertions.assertEquals(false, p.hasErrors());
    Assertions.assertEquals("a", ast.getName());
    
    Assertions.assertEquals(true, ((ASTConstants) ast.getWiredList().get(0)).isPubblic());
    Assertions.assertEquals(false, ((ASTConstants) ast.getWiredList().get(0)).isPrivate());
    
    Assertions.assertEquals(true, ((ASTConstants) ast.getWiredList().get(1)).isPubblic());
    Assertions.assertEquals(false, ((ASTConstants) ast.getWiredList().get(1)).isPrivate());
    
    Assertions.assertEquals(false, ((ASTConstants) ast.getWiredList().get(2)).isPubblic());
    Assertions.assertEquals(true, ((ASTConstants) ast.getWiredList().get(2)).isPrivate());
    
    Assertions.assertEquals(true, ((ASTSpices1) ast.getWiredList().get(3)).isCarlique());
    Assertions.assertEquals(true, ((ASTSpices1) ast.getWiredList().get(3)).isPepper());
    
    Assertions.assertEquals(ASTConstantsFeatureDSL.NONE, ((ASTSpices2) ((ASTAutomaton) ast).getWiredList().get(4)).getSpicelevel());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testConstantsParseError() throws IOException {
    StringReader s = new StringReader(
        "automaton a { spices2 ;}");
    
    // Ignore std.err
    System.setOut(new PrintStream(new ByteArrayOutputStream()));
    
    FeatureDSLParser p = new FeatureDSLParser();
    p.parseAutomaton(s);
    
    Assertions.assertEquals(true, p.hasErrors());
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
    
    Assertions.assertEquals(false, p.hasErrors());
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertEquals(true, ast.get().getA() instanceof ASTA);
    Assertions.assertEquals(true, ast.get().getBList() instanceof List);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
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
    
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertEquals(false, p.hasErrors());
    Assertions.assertEquals(true, ast.get().getAList() instanceof List);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
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
    
    Assertions.assertFalse(ast.isPresent());
  }
  
}
