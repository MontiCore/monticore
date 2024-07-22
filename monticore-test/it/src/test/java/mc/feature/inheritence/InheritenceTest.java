/* (c) https://github.com/MontiCore/monticore */

package mc.feature.inheritence;

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
import mc.feature.inheritence.inheritence._ast.ASTA;
import mc.feature.inheritence.inheritence._ast.ASTB;
import mc.feature.inheritence.inheritence._ast.ASTC;
import mc.feature.inheritence.inheritence._ast.ASTD;
import mc.feature.inheritence.inheritence._ast.ASTIG;
import mc.feature.inheritence.inheritence._ast.ASTIH;
import mc.feature.inheritence.inheritence._ast.ASTIM;
import mc.feature.inheritence.inheritence._ast.ASTK;
import mc.feature.inheritence.inheritence._ast.ASTL;
import mc.feature.inheritence.inheritence._ast.ASTXAE;
import mc.feature.inheritence.inheritence._ast.ASTXAO;
import mc.feature.inheritence.inheritence._ast.ASTXF;
import mc.feature.inheritence.inheritence._ast.ASTXP;
import mc.feature.inheritence.inheritence._parser.InheritenceParser;

public class InheritenceTest extends GeneratorIntegrationsTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  // // Test1 : IG should parse all "a", "b", and "c"
  // A(IF) = "a" ;
  //
  // B(IF) = "b" ;
  //
  // C(IG) = "c";
  // interface IF (IG);
  
  @Test
  public void test1a() throws IOException {
    
    InheritenceParser parser = new InheritenceParser();    
    Optional<ASTIG> ast = parser.parseIG(new StringReader("a"));
    
    Assertions.assertTrue(ast.get() instanceof ASTA);
    Assertions.assertTrue(Log.getFindings().isEmpty());
    
  }
  
  @Test
  public void test1b() throws IOException {
    
    InheritenceParser parser = new InheritenceParser();
    Optional<ASTIG> ast = parser.parseIG(new StringReader("b"));
    
    Assertions.assertTrue(ast.get() instanceof ASTB);
    Assertions.assertTrue(Log.getFindings().isEmpty());
    
  }
  
  @Test
  public void test1c() throws IOException {
    
    InheritenceParser parser = new InheritenceParser();
    Optional<ASTIG> ast = parser.parseIG(new StringReader("c"));
    
    Assertions.assertTrue(ast.get() instanceof ASTC);
    Assertions.assertTrue(Log.getFindings().isEmpty());
    
  }
  
  // // Test 2 : IH should parse "d" (calls D with parameters null)
  // interface IH = A ;
  //
  // D [B:B] =
  // "d";
  @Test
  public void test2() throws IOException {
    
    InheritenceParser parser = new InheritenceParser();
    Optional<ASTIH> ast = parser.parseIH(new StringReader("d"));
    Assertions.assertTrue(ast.get() instanceof ASTD);
    Assertions.assertTrue(Log.getFindings().isEmpty());
    
  }
  
  // Test 3 : IM should parse "aa", "bb" and "ab" (predicate is necessary
  // for k=1)
  //
  // K(("a" "a" | "b" "b")=> IM) = "a" "a" | "b" "b";
  // L(IM) = "a" "b";
  // }
  @Test
  public void test3a() throws IOException {
    
    InheritenceParser parser = new InheritenceParser();
    Optional<ASTIM> ast = parser.parseIM(new StringReader("aa"));
    Assertions.assertTrue(ast.get() instanceof ASTK);
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void test3b() throws IOException {
    
    InheritenceParser parser = new InheritenceParser();
    Optional<ASTIM> ast = parser.parseIM(new StringReader("bb"));
    Assertions.assertTrue(ast.get() instanceof ASTK);
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void test3c() throws IOException {
    
    InheritenceParser parser = new InheritenceParser();
    Optional<ASTIM> ast = parser.parseIM(new StringReader("ab"));
    Assertions.assertTrue(ast.get() instanceof ASTL);
    Assertions.assertTrue(Log.getFindings().isEmpty());
    
  }
  
  // Test 4 : XAE should parse "f" and return an XF
  //
  @Test
  public void test4a() throws IOException {
    
    InheritenceParser parser = new InheritenceParser();
    Optional<ASTXAE> ast = parser.parseXAE(new StringReader("f"));
    Assertions.assertTrue(ast.get() instanceof ASTXF);
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  // Test 5 : XAO should parse "p" but not "q" and return an XP
  //
  @Test
  public void test5a() throws IOException {
    
    InheritenceParser parser = new InheritenceParser();
    Optional<ASTXAO> ast = parser.parseXAO(new StringReader("p"));
    Assertions.assertTrue(ast.get() instanceof ASTXP);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(Log.getFindings().isEmpty());
    
  }
  
  @Test
  public void test5b() throws IOException {
    
    InheritenceParser parser = new InheritenceParser();
    parser.parseXAO(new StringReader("q"));
    Assertions.assertTrue(parser.hasErrors());
  }
 
}
