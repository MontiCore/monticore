/* (c) https://github.com/MontiCore/monticore */

package mc.feature.lexerformat;

import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.lexerformat.lexerformat.LexerFormatMill;
import mc.feature.lexerformat.lexerformat._ast.ASTTest;
import mc.feature.lexerformat.lexerformat._ast.ASTTest2;
import mc.feature.lexerformat.lexerformat._parser.LexerFormatParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(LexerFormatMill.class)
public class LexerTest {

  @Test
  public void test0() throws IOException {
    
    LexerFormatParser p = LexerFormatMill.parser();
    Optional<ASTTest> ast = p.parse_StringTest("007");
    assertTrue(ast.isPresent());
    
    int r = ast.get().getA();
    assertEquals(7, r);
  }
  
  @Test
  public void test1() throws IOException {
    
    LexerFormatParser p = LexerFormatMill.parser();
    Optional<ASTTest> ast = p.parse_StringTest("on");
    assertTrue(ast.isPresent());

    boolean r = ast.get().isB();
    assertTrue(r);
  }
  
  @Test
  public void test1a() throws IOException { 
    LexerFormatParser p = LexerFormatMill.parser();
    Optional<ASTTest> ast = p.parse_StringTest("start");
    assertTrue(ast.isPresent());
    
    boolean r = ast.get().isB();
    assertTrue(r);
  }
  
  @Test
  public void test1b() throws IOException { 
    LexerFormatParser p = LexerFormatMill.parser();
    Optional<ASTTest> ast = p.parse_StringTest("stop");
    assertTrue(ast.isPresent());

    boolean r = ast.get().isB();
    assertFalse(r);
  }
  
  @Test
  public void test1c() throws IOException {   
    LexerFormatParser p = LexerFormatMill.parser();
    Optional<ASTTest> ast = p.parse_StringTest("off");
    assertTrue(ast.isPresent());

    boolean r = ast.get().isB();
    assertFalse(r);
  }
  
  @Test
  public void test2() throws IOException {
    LexerFormatParser p = LexerFormatMill.parser();
    Optional<ASTTest> ast = p.parse_StringTest("a");
    assertTrue(ast.isPresent());

    char r = ast.get().getC();
    assertEquals('a', r);
  }
  

  @Test
  public void test3() throws IOException {
    LexerFormatParser p = LexerFormatMill.parser();
    Optional<ASTTest> ast = p.parse_StringTest("99.5");
    assertTrue(ast.isPresent());

    float r = ast.get().getD();
    assertEquals(99.5f, r, 0);
  }
  
  @Test
  public void test4() throws IOException {    
    LexerFormatParser p = LexerFormatMill.parser();
    Optional<ASTTest> ast = p.parse_StringTest("*");
    assertTrue(ast.isPresent());

    int r = ast.get().getE();
    assertEquals(-1, r);
  }
  
  @Test
  public void test5() throws IOException {    
    LexerFormatParser p = LexerFormatMill.parser();
    Optional<ASTTest2> ast = p.parseTest2(new StringReader("1;1"));
    assertTrue(ast.isPresent());
  }
  
  @Test
  public void test6() throws IOException {    
    LexerFormatParser p = LexerFormatMill.parser();
    Optional<ASTTest> ast = p.parse_StringTest("<<ddfdfd>>");
    assertTrue(ast.isPresent());
  }
  
  @Test
  public void test7() throws IOException {    
    LexerFormatParser p = LexerFormatMill.parser();
    Optional<ASTTest> ast = p.parse_StringTest("<<ddfd>>fd>>");
    assertTrue(p.hasErrors());
    MCAssertions.assertHasFindingStartingWith("Expected EOF but found token");
  }
}
