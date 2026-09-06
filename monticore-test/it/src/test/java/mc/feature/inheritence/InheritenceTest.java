/* (c) https://github.com/MontiCore/monticore */

package mc.feature.inheritence;

import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.inheritence.inheritence.InheritenceMill;
import mc.feature.inheritence.inheritence._ast.*;
import mc.feature.inheritence.inheritence._parser.InheritenceParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(InheritenceMill.class)
public class InheritenceTest {

  // // Test1 : IG should parse all "a", "b", and "c"
  // A(IF) = "a" ;
  //
  // B(IF) = "b" ;
  //
  // C(IG) = "c";
  // interface IF (IG);
  
  @Test
  public void test1a() throws IOException {
    InheritenceParser parser = InheritenceMill.parser();
    Optional<ASTIG> ast = parser.parse_StringIG("a");
    
    assertTrue(ast.isPresent());
    assertInstanceOf(ASTA.class, ast.get());
  }
  
  @Test
  public void test1b() throws IOException {
    InheritenceParser parser = InheritenceMill.parser();
    Optional<ASTIG> ast = parser.parse_StringIG("b");
    
    assertTrue(ast.isPresent());
    assertInstanceOf(ASTB.class, ast.get());
  }
  
  @Test
  public void test1c() throws IOException {
    InheritenceParser parser = InheritenceMill.parser();
    Optional<ASTIG> ast = parser.parse_StringIG("c");
    
    assertTrue(ast.isPresent());
    assertInstanceOf(ASTC.class, ast.get());
  }
  
  // // Test 2 : IH should parse "d" (calls D with parameters null)
  // interface IH = A ;
  //
  // D [B:B] =
  // "d";
  @Test
  public void test2() throws IOException {
    InheritenceParser parser = InheritenceMill.parser();
    Optional<ASTIH> ast = parser.parse_StringIH("d");
    
    assertTrue(ast.isPresent());
    assertInstanceOf(ASTD.class, ast.get());
  }
  
  // Test 3 : IM should parse "aa", "bb" and "ab" (predicate is necessary
  // for k=1)
  //
  // K(("a" "a" | "b" "b")=> IM) = "a" "a" | "b" "b";
  // L(IM) = "a" "b";
  // }
  @Test
  public void test3a() throws IOException {
    InheritenceParser parser = InheritenceMill.parser();
    Optional<ASTIM> ast = parser.parse_StringIM("aa");
    
    assertTrue(ast.isPresent());
    assertInstanceOf(ASTK.class, ast.get());
  }
  
  @Test
  public void test3b() throws IOException {
    InheritenceParser parser = InheritenceMill.parser();
    Optional<ASTIM> ast = parser.parse_StringIM("bb");
    
    assertTrue(ast.isPresent());
    assertInstanceOf(ASTK.class, ast.get());
  }
  
  @Test
  public void test3c() throws IOException {
    InheritenceParser parser = InheritenceMill.parser();
    Optional<ASTIM> ast = parser.parse_StringIM("ab");
    
    assertTrue(ast.isPresent());
    assertInstanceOf(ASTL.class, ast.get());
  }
  
  // Test 4 : XAE should parse "f" and return an XF
  //
  @Test
  public void test4a() throws IOException {
    InheritenceParser parser = InheritenceMill.parser();
    Optional<ASTXAE> ast = parser.parse_StringXAE("f");
    
    assertTrue(ast.isPresent());
    assertInstanceOf(ASTXF.class, ast.get());
  }
  
  // Test 5 : XAO should parse "p" but not "q" and return an XP
  //
  @Test
  public void test5a() throws IOException {
    InheritenceParser parser = InheritenceMill.parser();
    Optional<ASTXAO> ast = parser.parse_StringXAO("p");
    
    assertTrue(ast.isPresent());
    assertInstanceOf(ASTXP.class, ast.get());
    assertFalse(parser.hasErrors());
  }
  
  @Test
  public void test5b() throws IOException {
    InheritenceParser parser = InheritenceMill.parser();
    parser.parse_StringXAO("q");
    assertTrue(parser.hasErrors());
    
    MCAssertions.assertHasFindingStartingWith("mismatched input 'q' expecting 'p'");
  }
 
}
