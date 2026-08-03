/* (c) https://github.com/MontiCore/monticore */

package mc.feature.ast;

import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.featuredsl.FeatureDSLMill;
import mc.feature.featuredsl._ast.*;
import mc.feature.featuredsl._parser.FeatureDSLParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(FeatureDSLMill.class)
public class ParserTest {

  @Test
  public void testConstants() throws IOException {
    FeatureDSLParser p = FeatureDSLMill.parser();
    Optional<ASTAutomaton> opt = p.parse_StringAutomaton(
        "automaton a { constants public; constants +; constants private; spices1 garlic pepper;	spices2 none;}");
    assertTrue(opt.isPresent());
    ASTAutomaton ast = opt.get();
    
    assertFalse(p.hasErrors());
    assertEquals("a", ast.getName());
    
    assertTrue(((ASTConstants) ast.getWiredList().get(0)).isPubblic());
    assertFalse(((ASTConstants) ast.getWiredList().get(0)).isPrivate());
    
    assertTrue(((ASTConstants) ast.getWiredList().get(1)).isPubblic());
    assertFalse(((ASTConstants) ast.getWiredList().get(1)).isPrivate());
    
    assertFalse(((ASTConstants) ast.getWiredList().get(2)).isPubblic());
    assertTrue(((ASTConstants) ast.getWiredList().get(2)).isPrivate());
    
    assertTrue(((ASTSpices1) ast.getWiredList().get(3)).isCarlique());
    assertTrue(((ASTSpices1) ast.getWiredList().get(3)).isPepper());
    
    assertEquals(ASTConstantsFeatureDSL.NONE, ((ASTSpices2) ((ASTAutomaton) ast).getWiredList().get(4)).getSpicelevel());
  }
  
  @Test
  public void testConstantsParseError() throws IOException {
    FeatureDSLParser p = FeatureDSLMill.parser();
    p.parse_StringAutomaton("automaton a { spices2 ;}");
    
    assertTrue(p.hasErrors());
    MCAssertions.assertHasFindingStartingWith("mismatched input ';' expecting {'pepper', 'none', 'garlic', '%'}");
  }
  
  /*  Grammar:  B: A:A (B:A)*; 
   * 
   * In a previous version A and B were both lists (<- bug)
   * */
  @Test
  public void testListError() throws IOException {
    FeatureDSLParser p = FeatureDSLMill.parser();
    Optional<ASTB> ast = p.parse_StringB("private / private / private /");
    
    assertFalse(p.hasErrors());
    assertTrue(ast.isPresent());
    assertInstanceOf(ASTA.class, ast.get().getA());
    assertInstanceOf(List.class, ast.get().getBList());
  }
  
  /*  Grammar:  B: A:A (A:A)*; 
   * 
   *  A has to be of type ASTAList
   * */
  @Test
  public void testListError2() throws IOException {
    FeatureDSLParser p = FeatureDSLMill.parser();
    Optional<ASTC> ast = p.parse_StringC("private / private / private /");
    
    assertTrue(ast.isPresent());
    assertFalse(p.hasErrors());
    assertInstanceOf(List.class, ast.get().getAList());
  }
  
  /*  Grammar: 
   * 
   *  A has to be of type ASTAList
   * */
  @Test
  public void testListError3() throws IOException {
    FeatureDSLParser p = FeatureDSLMill.parser();
    Optional<ASTComplexname> ast = p.parse_StringComplexname("private / private / private /");
    
    assertFalse(ast.isPresent());
    MCAssertions.assertHasFindingStartingWith("mismatched input 'private' expecting '.'");
  }
  
}
