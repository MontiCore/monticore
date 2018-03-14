/* (c) https://github.com/MontiCore/monticore */

package mc.feature.lexerformat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.lexerformat.kleeneplus._ast.ASTKPStart;
import mc.feature.lexerformat.kleeneplus._parser.KleenePlusParser;

public class KleenePlusTest extends GeneratorIntegrationsTest {
  
  /**
   * Test the following lexer Production: token KLEENETOKEN = 'a' ('b')*;
   * 
   */
  @Test
  public void testKleeneStar() throws IOException {
    KleenePlusParser p = new KleenePlusParser ();
    Optional<ASTKPStart> ast;
    
    ast = p.parseKPStart(new StringReader("a"));
    assertTrue(ast.isPresent());
    assertEquals("a", ast.get().getKleeneOpt().get());
    
    ast = p.parseKPStart(new StringReader("ab"));
    assertTrue(ast.isPresent());
    assertEquals("ab", ast.get().getKleeneOpt().get());
    
    ast = p.parseKPStart(new StringReader("abb"));
    assertTrue(ast.isPresent());
    assertEquals("abb", ast.get().getKleeneOpt().get());
    
    ast = p.parseKPStart(new StringReader("abbbb"));
    assertTrue(ast.isPresent());
    assertEquals("abbbb", ast.get().getKleeneOpt().get());
    
    ast = p.parseKPStart(new StringReader("b"));
    assertFalse(ast.isPresent());
  }
  
  /**
   * Test the following lexer Production: token SIMPLEKLEENE = 'c' 'd'*;
   * 
   */
  @Test
  public void testSimpleKleene() throws IOException {
    KleenePlusParser p = new KleenePlusParser ();
    Optional<ASTKPStart> ast;
    
    ast = p.parseKPStart(new StringReader("c"));
    assertTrue(ast.isPresent());
    assertEquals("c", ast.get().getSimpleKleeneOpt().get());
    
    ast = p.parseKPStart(new StringReader("cd"));
    assertTrue(ast.isPresent());
    assertEquals("cd", ast.get().getSimpleKleeneOpt().get());
    
    ast = p.parseKPStart(new StringReader("cdd"));
    assertTrue(ast.isPresent());
    assertEquals("cdd", ast.get().getSimpleKleeneOpt().get());
    
    ast = p.parseKPStart(new StringReader("cdddd"));
    assertTrue(ast.isPresent());
    assertEquals("cdddd", ast.get().getSimpleKleeneOpt().get());
    
    ast = p.parseKPStart(new StringReader("d"));
    assertFalse(ast.isPresent());
  }
  
  /**
   * Test the following lexer Production: token SIMPLEKLEENESTRING = "ee" "fg"*;
   * 
   */
  @Test
  public void testSimpleKleeneString() throws IOException {
    KleenePlusParser p = new KleenePlusParser ();
    Optional<ASTKPStart> ast;
    
    ast = p.parseKPStart(new StringReader("ee"));
    assertTrue(ast.isPresent());
    assertEquals("ee", ast.get().getSimpleKleeneStringOpt().get());
    
    ast = p.parseKPStart(new StringReader("eefg"));
    assertTrue(ast.isPresent());
    assertEquals("eefg", ast.get().getSimpleKleeneStringOpt().get());
    
    ast = p.parseKPStart(new StringReader("eefgfg"));
    assertTrue(ast.isPresent());
    assertEquals("eefgfg", ast.get().getSimpleKleeneStringOpt().get());
    
    ast = p.parseKPStart(new StringReader("eefgfgfgfg"));
    assertTrue(ast.isPresent());
    assertEquals("eefgfgfgfg", ast.get().getSimpleKleeneStringOpt().get());
    
    ast = p.parseKPStart(new StringReader("fg"));
    assertFalse(ast.isPresent());
  }
  
  /**
   * Test the following lexer Production: token PLUSTOKEN = 'g' ('h')+;
   * 
   */
  @Test
  public void testPlus() throws IOException {
    KleenePlusParser p = new KleenePlusParser ();
    Optional<ASTKPStart> ast;
    
    ast = p.parseKPStart(new StringReader("g"));
    assertFalse(ast.isPresent());
    
    ast = p.parseKPStart(new StringReader("gh"));
    assertTrue(ast.isPresent());
    assertEquals("gh", ast.get().getPlusOpt().get());
    
    ast = p.parseKPStart(new StringReader("ghh"));
    assertTrue(ast.isPresent());
    assertEquals("ghh", ast.get().getPlusOpt().get());
    
    ast = p.parseKPStart(new StringReader("ghhhh"));
    assertTrue(ast.isPresent());
   assertEquals("ghhhh", ast.get().getPlusOpt().get());
    
    ast = p.parseKPStart(new StringReader("h"));
    assertFalse(ast.isPresent());
  }
  
  /**
   * Test the following lexer Production: token SIMPLEPLUS = 'i' ('j')+;
   * 
   */
  @Test
  public void testSimplePlus() throws IOException {
    KleenePlusParser p = new KleenePlusParser ();
    Optional<ASTKPStart> ast;
    
    ast = p.parseKPStart(new StringReader("i"));
    assertFalse(ast.isPresent());
    
    ast = p.parseKPStart(new StringReader("ij"));
    assertTrue(ast.isPresent());
    assertEquals("ij", ast.get().getSimplePlusOpt().get());
    
    ast = p.parseKPStart(new StringReader("ijj"));
    assertTrue(ast.isPresent());
    assertEquals("ijj", ast.get().getSimplePlusOpt().get());
    
    ast = p.parseKPStart(new StringReader("ijjjj"));
    assertTrue(ast.isPresent());
    assertEquals("ijjjj", ast.get().getSimplePlusOpt().get());
    
    ast = p.parseKPStart(new StringReader("j"));
    assertFalse(ast.isPresent());
  }
  
  /**
   * Test the following lexer Production: token SIMPLEPLUSSTRING = "kk" "lm"+;
   * 
   */
  @Test
  public void testSimplePlusString() throws IOException {
    KleenePlusParser p = new KleenePlusParser ();
    Optional<ASTKPStart> ast;
    
    ast = p.parseKPStart(new StringReader("kk"));
    ast = null;
    
    assertTrue(p.hasErrors());
    
    ast = p.parseKPStart(new StringReader("kklm"));
    assertTrue(ast.isPresent());
    assertEquals("kklm", ast.get().getSimplePlusStringOpt().get());
    
    ast = p.parseKPStart(new StringReader("kklmlm"));
    assertTrue(ast.isPresent());
    assertEquals("kklmlm", ast.get().getSimplePlusStringOpt().get());
    
    ast = p.parseKPStart(new StringReader("kklmlmlmlm"));
    assertTrue(ast.isPresent());
    assertEquals("kklmlmlmlm", ast.get().getSimplePlusStringOpt().get());
    
    ast = p.parseKPStart(new StringReader("lm"));
    assertFalse(ast.isPresent());
  }
  
}
