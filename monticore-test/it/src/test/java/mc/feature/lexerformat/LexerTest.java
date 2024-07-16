/* (c) https://github.com/MontiCore/monticore */

package mc.feature.lexerformat;

import static org.junit.Assert.assertEquals;
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
import mc.feature.lexerformat.lexerformat._ast.ASTTest;
import mc.feature.lexerformat.lexerformat._ast.ASTTest2;
import mc.feature.lexerformat.lexerformat._parser.LexerFormatParser;

public class LexerTest extends GeneratorIntegrationsTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void test0() throws IOException {
    
    LexerFormatParser p = new LexerFormatParser();
    Optional<ASTTest> ast = p.parseTest(new StringReader("007"));
    Assertions.assertTrue(ast.isPresent());
    
    int r = ast.get().getA();
    Assertions.assertEquals(7, r);
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void test1() throws IOException {
    
    LexerFormatParser p = new LexerFormatParser();
    Optional<ASTTest> ast = p.parseTest(new StringReader("on"));
    Assertions.assertTrue(ast.isPresent());

    boolean r = ast.get().isB();
    Assertions.assertEquals(true, r);
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void test1a() throws IOException { 
    LexerFormatParser p = new LexerFormatParser();
    Optional<ASTTest> ast = p.parseTest(new StringReader("start"));
    Assertions.assertTrue(ast.isPresent());
    
    boolean r = ast.get().isB();
    Assertions.assertEquals(true, r);
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void test1b() throws IOException { 
    LexerFormatParser p = new LexerFormatParser();
    Optional<ASTTest> ast = p.parseTest(new StringReader("stop"));
    Assertions.assertTrue(ast.isPresent());

    boolean r = ast.get().isB();
    Assertions.assertEquals(false, r);
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void test1c() throws IOException {   
    LexerFormatParser p = new LexerFormatParser();
    Optional<ASTTest> ast = p.parseTest(new StringReader("off"));
    Assertions.assertTrue(ast.isPresent());

    boolean r = ast.get().isB();
    Assertions.assertEquals(false, r);
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void test2() throws IOException {
    LexerFormatParser p = new LexerFormatParser();
    Optional<ASTTest> ast = p.parseTest(new StringReader("a"));
    Assertions.assertTrue(ast.isPresent());

    char r = ast.get().getC();
    Assertions.assertEquals('a', r);
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  

  @Test
  public void test3() throws IOException {
    LexerFormatParser p = new LexerFormatParser();
    Optional<ASTTest> ast = p.parseTest(new StringReader("99.5"));
    Assertions.assertTrue(ast.isPresent());

    float r = ast.get().getD();
    Assertions.assertEquals(99.5f, r, 0);
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void test4() throws IOException {    
    LexerFormatParser p = new LexerFormatParser();
    Optional<ASTTest> ast = p.parseTest(new StringReader("*"));
    Assertions.assertTrue(ast.isPresent());

    int r = ast.get().getE();
    Assertions.assertEquals(-1, r);
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void test5() throws IOException {    
    LexerFormatParser p = new LexerFormatParser();
    Optional<ASTTest2> ast = p.parseTest2(new StringReader("1;1"));
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void test6() throws IOException {    
    LexerFormatParser p = new LexerFormatParser();
    Optional<ASTTest> ast = p.parseTest(new StringReader("<<ddfdfd>>"));
    Assertions.assertTrue(ast.isPresent());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void test7() throws IOException {    
    LexerFormatParser p = new LexerFormatParser();
    Optional<ASTTest> ast = p.parseTest(new StringReader("<<ddfd>>fd>>"));
    Assertions.assertTrue(p.hasErrors());
  }
}
