/* (c) https://github.com/MontiCore/monticore */

package mc.feature.lexerformat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import org.junit.Test;

import mc.GeneratorIntegrationsTest;
import mc.feature.lexerformat.lexerformat._ast.ASTTest;
import mc.feature.lexerformat.lexerformat._ast.ASTTest2;
import mc.feature.lexerformat.lexerformat._parser.LexerFormatParser;

public class LexerTest extends GeneratorIntegrationsTest {
  
  @Test
  public void test0() throws IOException {
    
    LexerFormatParser p = new LexerFormatParser();
    Optional<ASTTest> ast = p.parseTest(new StringReader("007"));
    assertTrue(ast.isPresent());
    
    int r = ast.get().getA();
    assertEquals(7, r);
  }
  
  @Test
  public void test1() throws IOException {
    
    LexerFormatParser p = new LexerFormatParser();
    Optional<ASTTest> ast = p.parseTest(new StringReader("on"));
    assertTrue(ast.isPresent());

    boolean r = ast.get().isB();
    assertEquals(true, r);
  }
  
  @Test
  public void test1a() throws IOException { 
    LexerFormatParser p = new LexerFormatParser();
    Optional<ASTTest> ast = p.parseTest(new StringReader("start"));
    assertTrue(ast.isPresent());
    
    boolean r = ast.get().isB();
    assertEquals(true, r);
  }
  
  @Test
  public void test1b() throws IOException { 
    LexerFormatParser p = new LexerFormatParser();
    Optional<ASTTest> ast = p.parseTest(new StringReader("stop"));
    assertTrue(ast.isPresent());

    boolean r = ast.get().isB();
    assertEquals(false, r);
  }
  
  @Test
  public void test1c() throws IOException {   
    LexerFormatParser p = new LexerFormatParser();
    Optional<ASTTest> ast = p.parseTest(new StringReader("off"));
    assertTrue(ast.isPresent());

    boolean r = ast.get().isB();
    assertEquals(false, r);
  }
  
  @Test
  public void test2() throws IOException {
    LexerFormatParser p = new LexerFormatParser();
    Optional<ASTTest> ast = p.parseTest(new StringReader("a"));
    assertTrue(ast.isPresent());

    char r = ast.get().getC();
    assertEquals('a', r);
  }
  

  @Test
  public void test3() throws IOException {
    LexerFormatParser p = new LexerFormatParser();
    Optional<ASTTest> ast = p.parseTest(new StringReader("99.5"));
    assertTrue(ast.isPresent());

    float r = ast.get().getD();
    assertEquals(99.5f, r, 0);   
  }
  
  @Test
  public void test4() throws IOException {    
    LexerFormatParser p = new LexerFormatParser();
    Optional<ASTTest> ast = p.parseTest(new StringReader("*"));
    assertTrue(ast.isPresent());

    int r = ast.get().getE();
    assertEquals(-1, r);
  }
  
  @Test
  public void test5() throws IOException {    
    LexerFormatParser p = new LexerFormatParser();
    Optional<ASTTest2> ast = p.parseTest2(new StringReader("1;1"));
    assertTrue(ast.isPresent());
  }
  
  @Test
  public void test6() throws IOException {    
    LexerFormatParser p = new LexerFormatParser();
    Optional<ASTTest> ast = p.parseTest(new StringReader("<<ddfdfd>>"));
    assertTrue(ast.isPresent());
  }
  
  @Test
  public void test7() throws IOException {    
    LexerFormatParser p = new LexerFormatParser();
    Optional<ASTTest> ast = p.parseTest(new StringReader("<<ddfd>>fd>>"));
    assertTrue(p.hasErrors());
  }
}
