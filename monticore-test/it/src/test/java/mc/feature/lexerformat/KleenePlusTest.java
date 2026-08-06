/* (c) https://github.com/MontiCore/monticore */

package mc.feature.lexerformat;

import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.lexerformat.kleeneplus.KleenePlusMill;
import mc.feature.lexerformat.kleeneplus._ast.ASTKPStart;
import mc.feature.lexerformat.kleeneplus._parser.KleenePlusParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(KleenePlusMill.class)
public class KleenePlusTest {

  /**
   * Test the following lexer Production: token KLEENETOKEN = 'a' ('b')*;
   * 
   */
  @Test
  public void testKleeneStar() throws IOException {
    KleenePlusParser p = KleenePlusMill.parser();
    Optional<ASTKPStart> ast;
    
    ast = p.parse_StringKPStart("a");
    assertTrue(ast.isPresent());
    assertEquals("a", ast.get().getKleene());
    
    ast = p.parse_StringKPStart("ab");
    assertTrue(ast.isPresent());
    assertEquals("ab", ast.get().getKleene());
    
    ast = p.parse_StringKPStart("abb");
    assertTrue(ast.isPresent());
    assertEquals("abb", ast.get().getKleene());
    
    ast = p.parse_StringKPStart("abbbb");
    assertTrue(ast.isPresent());
    assertEquals("abbbb", ast.get().getKleene());
    
    ast = p.parse_StringKPStart("b");
    assertFalse(ast.isPresent());
    MCAssertions.assertHasFindingStartingWith("token recognition error at: 'b'");
    MCAssertions.assertHasFindingStartingWith("mismatched input '<EOF>'");
  }
  
  /**
   * Test the following lexer Production: token SIMPLEKLEENE = 'c' 'd'*;
   * 
   */
  @Test
  public void testSimpleKleene() throws IOException {
    KleenePlusParser p = KleenePlusMill.parser();
    Optional<ASTKPStart> ast;
    
    ast = p.parse_StringKPStart("c");
    assertTrue(ast.isPresent());
    assertEquals("c", ast.get().getSimpleKleene());
    
    ast = p.parse_StringKPStart("cd");
    assertTrue(ast.isPresent());
    assertEquals("cd", ast.get().getSimpleKleene());
    
    ast = p.parse_StringKPStart("cdd");
    assertTrue(ast.isPresent());
    assertEquals("cdd", ast.get().getSimpleKleene());
    
    ast = p.parse_StringKPStart("cdddd");
    assertTrue(ast.isPresent());
    assertEquals("cdddd", ast.get().getSimpleKleene());
    
    ast = p.parse_StringKPStart("d");
    assertFalse(ast.isPresent());
    MCAssertions.assertHasFindingStartingWith("token recognition error at: 'd'");
    MCAssertions.assertHasFindingStartingWith("mismatched input '<EOF>'");
  }
  
  /**
   * Test the following lexer Production: token SIMPLEKLEENESTRING = "ee" "fg"*;
   * 
   */
  @Test
  public void testSimpleKleeneString() throws IOException {
    KleenePlusParser p = KleenePlusMill.parser();
    Optional<ASTKPStart> ast;
    
    ast = p.parse_StringKPStart("ee");
    assertTrue(ast.isPresent());
    assertEquals("ee", ast.get().getSimpleKleeneString());
    
    ast = p.parse_StringKPStart("eefg");
    assertTrue(ast.isPresent());
    assertEquals("eefg", ast.get().getSimpleKleeneString());
    
    ast = p.parse_StringKPStart("eefgfg");
    assertTrue(ast.isPresent());
    assertEquals("eefgfg", ast.get().getSimpleKleeneString());
    
    ast = p.parse_StringKPStart("eefgfgfgfg");
    assertTrue(ast.isPresent());
    assertEquals("eefgfgfgfg", ast.get().getSimpleKleeneString());
    
    ast = p.parse_StringKPStart("fg");
    assertFalse(ast.isPresent());
    MCAssertions.assertHasFindingStartingWith("token recognition error at: 'f'");
    MCAssertions.assertHasFindingStartingWith("token recognition error at: 'g'");
    MCAssertions.assertHasFindingStartingWith("mismatched input '<EOF>'");
  }
  
  /**
   * Test the following lexer Production: token PLUSTOKEN = 'g' ('h')+;
   * 
   */
  @Test
  public void testPlus() throws IOException {
    KleenePlusParser p = KleenePlusMill.parser();
    Optional<ASTKPStart> ast;
    
    ast = p.parse_StringKPStart("g");
    assertFalse(ast.isPresent());
    MCAssertions.assertHasFindingStartingWith("token recognition error at: 'g'");
    MCAssertions.assertHasFindingStartingWith("mismatched input '<EOF>'");
    
    ast = p.parse_StringKPStart("gh");
    assertTrue(ast.isPresent());
    assertEquals("gh", ast.get().getPlus());
    
    ast = p.parse_StringKPStart("ghh");
    assertTrue(ast.isPresent());
    assertEquals("ghh", ast.get().getPlus());
    
    ast = p.parse_StringKPStart("ghhhh");
    assertTrue(ast.isPresent());
   assertEquals("ghhhh", ast.get().getPlus());
    
    ast = p.parse_StringKPStart("h");
    assertFalse(ast.isPresent());
    MCAssertions.assertHasFindingStartingWith("token recognition error at: 'h'");
    MCAssertions.assertHasFindingStartingWith("mismatched input '<EOF>'");
  }
  
  /**
   * Test the following lexer Production: token SIMPLEPLUS = 'i' ('j')+;
   * 
   */
  @Test
  public void testSimplePlus() throws IOException {
    KleenePlusParser p = KleenePlusMill.parser();
    Optional<ASTKPStart> ast;
    
    ast = p.parse_StringKPStart("i");
    assertFalse(ast.isPresent());
    MCAssertions.assertHasFindingStartingWith("token recognition error at: 'i'");
    MCAssertions.assertHasFindingStartingWith("mismatched input '<EOF>'");
    
    ast = p.parse_StringKPStart("ij");
    assertTrue(ast.isPresent());
    assertEquals("ij", ast.get().getSimplePlus());
    
    ast = p.parse_StringKPStart("ijj");
    assertTrue(ast.isPresent());
    assertEquals("ijj", ast.get().getSimplePlus());
    
    ast = p.parse_StringKPStart("ijjjj");
    assertTrue(ast.isPresent());
    assertEquals("ijjjj", ast.get().getSimplePlus());
    
    ast = p.parse_StringKPStart("j");
    assertFalse(ast.isPresent());
    MCAssertions.assertHasFindingStartingWith("token recognition error at: 'j'");
    MCAssertions.assertHasFindingStartingWith("mismatched input '<EOF>'");
  }
  
  /**
   * Test the following lexer Production: token SIMPLEPLUSSTRING = "kk" "lm"+;
   * 
   */
  @Test
  public void testSimplePlusString() throws IOException {
    KleenePlusParser p = KleenePlusMill.parser();
    Optional<ASTKPStart> ast;
    
    ast = p.parse_StringKPStart("kk");
    assertFalse(ast.isPresent());
    
    MCAssertions.assertHasFindingStartingWith("token recognition error at: 'kk'");
    MCAssertions.assertHasFindingStartingWith("mismatched input '<EOF>'");
    
    assertTrue(p.hasErrors());
    
    ast = p.parse_StringKPStart("kklm");
    assertTrue(ast.isPresent());
    assertEquals("kklm", ast.get().getSimplePlusString());
    
    ast = p.parse_StringKPStart("kklmlm");
    assertTrue(ast.isPresent());
    assertEquals("kklmlm", ast.get().getSimplePlusString());
    
    ast = p.parse_StringKPStart("kklmlmlmlm");
    assertTrue(ast.isPresent());
    assertEquals("kklmlmlmlm", ast.get().getSimplePlusString());
    
    ast = p.parse_StringKPStart("lm");
    assertFalse(ast.isPresent());
    MCAssertions.assertHasFindingStartingWith("token recognition error at: 'l'");
    MCAssertions.assertHasFindingStartingWith("token recognition error at: 'm'");
    MCAssertions.assertHasFindingStartingWith("mismatched input '<EOF>'");
  }
  
}
