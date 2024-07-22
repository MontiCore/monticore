/* (c) https://github.com/MontiCore/monticore */

package mc.feature.lexerformat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.lexerformat.kleeneplus._ast.ASTKPStart;
import mc.feature.lexerformat.kleeneplus._parser.KleenePlusParser;

public class KleenePlusTest extends GeneratorIntegrationsTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  /**
   * Test the following lexer Production: token KLEENETOKEN = 'a' ('b')*;
   * 
   */
  @Test
  public void testKleeneStar() throws IOException {
    KleenePlusParser p = new KleenePlusParser ();
    Optional<ASTKPStart> ast;
    
    ast = p.parseKPStart(new StringReader("a"));
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertEquals("a", ast.get().getKleene());
    
    ast = p.parseKPStart(new StringReader("ab"));
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertEquals("ab", ast.get().getKleene());
    
    ast = p.parseKPStart(new StringReader("abb"));
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertEquals("abb", ast.get().getKleene());
    
    ast = p.parseKPStart(new StringReader("abbbb"));
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertEquals("abbbb", ast.get().getKleene());
    
    ast = p.parseKPStart(new StringReader("b"));
    Assertions.assertFalse(ast.isPresent());
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
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertEquals("c", ast.get().getSimpleKleene());
    
    ast = p.parseKPStart(new StringReader("cd"));
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertEquals("cd", ast.get().getSimpleKleene());
    
    ast = p.parseKPStart(new StringReader("cdd"));
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertEquals("cdd", ast.get().getSimpleKleene());
    
    ast = p.parseKPStart(new StringReader("cdddd"));
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertEquals("cdddd", ast.get().getSimpleKleene());
    
    ast = p.parseKPStart(new StringReader("d"));
    Assertions.assertFalse(ast.isPresent());
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
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertEquals("ee", ast.get().getSimpleKleeneString());
    
    ast = p.parseKPStart(new StringReader("eefg"));
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertEquals("eefg", ast.get().getSimpleKleeneString());
    
    ast = p.parseKPStart(new StringReader("eefgfg"));
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertEquals("eefgfg", ast.get().getSimpleKleeneString());
    
    ast = p.parseKPStart(new StringReader("eefgfgfgfg"));
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertEquals("eefgfgfgfg", ast.get().getSimpleKleeneString());
    
    ast = p.parseKPStart(new StringReader("fg"));
    Assertions.assertFalse(ast.isPresent());
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
    Assertions.assertFalse(ast.isPresent());
    
    ast = p.parseKPStart(new StringReader("gh"));
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertEquals("gh", ast.get().getPlus());
    
    ast = p.parseKPStart(new StringReader("ghh"));
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertEquals("ghh", ast.get().getPlus());
    
    ast = p.parseKPStart(new StringReader("ghhhh"));
    Assertions.assertTrue(ast.isPresent());
   Assertions.assertEquals("ghhhh", ast.get().getPlus());
    
    ast = p.parseKPStart(new StringReader("h"));
    Assertions.assertFalse(ast.isPresent());
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
    Assertions.assertFalse(ast.isPresent());
    
    ast = p.parseKPStart(new StringReader("ij"));
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertEquals("ij", ast.get().getSimplePlus());
    
    ast = p.parseKPStart(new StringReader("ijj"));
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertEquals("ijj", ast.get().getSimplePlus());
    
    ast = p.parseKPStart(new StringReader("ijjjj"));
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertEquals("ijjjj", ast.get().getSimplePlus());
    
    ast = p.parseKPStart(new StringReader("j"));
    Assertions.assertFalse(ast.isPresent());
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
    
    Assertions.assertTrue(p.hasErrors());
    
    ast = p.parseKPStart(new StringReader("kklm"));
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertEquals("kklm", ast.get().getSimplePlusString());
    
    ast = p.parseKPStart(new StringReader("kklmlm"));
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertEquals("kklmlm", ast.get().getSimplePlusString());
    
    ast = p.parseKPStart(new StringReader("kklmlmlmlm"));
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertEquals("kklmlmlmlm", ast.get().getSimplePlusString());
    
    ast = p.parseKPStart(new StringReader("lm"));
    Assertions.assertFalse(ast.isPresent());
  }
  
}
