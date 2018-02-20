/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.cardinality._ast.ASTCardinality;
import de.monticore.completeness._ast.ASTCompleteness;
import de.monticore.mcnumbers._ast.ASTDecimal;
import de.monticore.stringliterals._ast.ASTStringLiteral;
import de.monticore.testmccommon._parser.TestMCCommonParser;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.monticore.umlstereotype._ast.ASTStereoValue;
import de.monticore.umlstereotype._ast.ASTStereotype;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;


public class MCCommonUnitTest {
    
  // setup the language infrastructure
  TestMCCommonParser parser = new TestMCCommonParser() ;
  
  @BeforeClass
  public static void init() {
    // replace log by a sideffect free variant
    LogStub.init();
    Log.enableFailQuick(false);
  }
  

  @Before
  public void setUp() { 
    Log.getFindings().clear();
  }
  
  // --------------------------------------------------------------------
  // Numbers: Nat
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testNat1() throws IOException {
    ASTDecimal ast = parser.parse_StringDecimal( " 9" ).get();
    assertEquals("9", ast.getSource());
    assertEquals(9, ast.getValue());
  }
  @Test
  public void testNat4() throws IOException {
    ASTDecimal ast = parser.parse_StringDecimal( " 42 " ).get();
    assertEquals("42", ast.getSource());
    assertEquals(42, ast.getValue());
  }

  // --------------------------------------------------------------------
  // UMLStereotype
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testModifier() throws IOException {
    ASTModifier ast = parser.parse_StringModifier( "# final" ).get();
    assertEquals(true, ast.isProtected());
    assertEquals(true, ast.isFinal());
    assertEquals(false, ast.isLocal());
  }


  // --------------------------------------------------------------------
  @Test
  public void testModifierStereo() throws IOException {
    ASTModifier ast = parser.parse_StringModifier( "<<bla=\"x1\">>#+?" ).get();
    assertEquals(true, ast.isProtected());
    assertEquals(true, ast.isPublic());
    assertEquals(true, ast.isReadonly());
    assertEquals(false, ast.isFinal());
    assertEquals(true, ast.isPresentStereotype());
    ASTStereotype sty = ast.getStereotype();
    assertEquals("x1", sty.getValue("bla"));
  }



  // --------------------------------------------------------------------
  // UMLStereotype
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testStereoValue() throws IOException {
    ASTStereoValue ast = parser.parse_StringStereoValue( "bla=\"17\"" ).get();
    assertEquals("bla", ast.getName());
    Optional<ASTStringLiteral> os = ast.getTextOpt();
    assertEquals(true, os.isPresent());
    assertEquals("17", os.get().getValue());
    assertEquals("17", ast.getValue());
  }


  // --------------------------------------------------------------------
  @Test
  public void testStereoValue2() throws IOException {
    ASTStereoValue ast = parser.parse_StringStereoValue( "cc" ).get();
    assertEquals("cc", ast.getName());
    Optional<ASTStringLiteral> os = ast.getTextOpt();
    assertEquals(false, os.isPresent());
    assertEquals("", ast.getValue());
  }


  // --------------------------------------------------------------------
  @Test
  public void testStereotype() throws IOException {
    ASTStereotype ast = parser.parse_StringStereotype( "<< a1 >>" ).get();
    List<ASTStereoValue> svl = ast.getValueList();
    assertEquals(1, svl.size());
    assertEquals(true, ast.contains("a1"));
    assertEquals(false, ast.contains("bla"));
    assertEquals(true, ast.contains("a1",""));
    assertEquals(false, ast.contains("a1","wert1"));
  }


  // --------------------------------------------------------------------
  @Test
  public void testStereotype2() throws IOException {
    ASTStereotype ast = parser.parse_StringStereotype(
    	"<< bla, a1=\"wert1\" >>" ).get();
    List<ASTStereoValue> svl = ast.getValueList();
    assertEquals(2, svl.size());
    assertEquals(true, ast.contains("a1"));
    assertEquals(false, ast.contains("a1",""));
    assertEquals(true, ast.contains("a1","wert1"));
  }


  // --------------------------------------------------------------------
  @Test
  public void testGetValue() throws IOException {
    ASTStereotype ast = parser.parse_StringStereotype(
        "<< bla, a1=\"wert1\" >>" ).get(); 
    assertEquals("wert1", ast.getValue("a1"));
    try {
      assertEquals("", ast.getValue("foo"));
      fail("Expected an Exception to be thrown");
    } catch (java.util.NoSuchElementException ex) { }
    assertEquals("", ast.getValue("bla"));
  }


  @Test
  public void testEnding() throws IOException {
    Optional<ASTStereotype> oast = parser.parse_StringStereotype(
        "<< bla, a1=\"wert1\" > >" ); 
    assertEquals(false, oast.isPresent());
  }


  // --------------------------------------------------------------------
  // Completeness
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testBasics() throws IOException {
    ASTCompleteness ast = parser.parse_StringCompleteness( "(c)"  ).get();
    assertEquals(true, ast.isComplete());
    assertEquals(false, ast.isIncomplete());
  }


  // --------------------------------------------------------------------
  @Test
  public void testBasics2() throws IOException {
    ASTCompleteness ast = parser.parse_StringCompleteness( "(  ... )"  ).get();
    assertEquals(false, ast.isComplete());
    assertEquals(true, ast.isIncomplete());
    assertEquals(false, ast.isRightComplete());
    assertEquals(false, ast.isLeftComplete());
  }


  // --------------------------------------------------------------------
  @Test
  public void testBasics3() throws IOException {
    ASTCompleteness ast = parser.parse_StringCompleteness( "(...,c)"  ).get();
    assertEquals(false, ast.isComplete());
    assertEquals(false, ast.isIncomplete());
    assertEquals(true, ast.isRightComplete());
    assertEquals(false, ast.isLeftComplete());
  }


  // --------------------------------------------------------------------
  @Test
  public void testIllegalComplete() throws IOException {
    Optional<ASTCompleteness> ast = 
    		parser.parse_StringCompleteness( "(...,d)"  );
    assertEquals(false, ast.isPresent());
  }

  // --------------------------------------------------------------------
  // Cardinality
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testMany() throws IOException {
    ASTCardinality ast = parser.parse_StringCardinality("[*]").get();
    assertEquals(true, ast.isMany());
    assertEquals(0, ast.getLowerBound());
    assertEquals(0, ast.getUpperBound());
  }


  // --------------------------------------------------------------------
  @Test
  public void testLowAndStar() throws IOException {
    ASTCardinality ast = parser.parse_StringCardinality("[7..*]").get();
    assertEquals(false, ast.isMany());
    assertEquals(true, ast.isNoUpperLimit());
    assertEquals(7, ast.getLowerBound());
    assertEquals(0, ast.getUpperBound());
  }


  // --------------------------------------------------------------------
  @Test
  public void testLowAndUp() throws IOException {
    ASTCardinality ast = parser.parse_StringCardinality("[17..235]").get();
    assertEquals(false, ast.isMany());
    assertEquals(17, ast.getLowerBound());
    assertEquals(235, ast.getUpperBound());
  }


  // --------------------------------------------------------------------
  @Test
  public void testSpace() throws IOException {
    ASTCardinality ast = parser.parse_StringCardinality(" [ 34 .. 15 ] ").get();
    assertEquals(false, ast.isMany());
    assertEquals(34, ast.getLowerBound());
    assertEquals(15, ast.getUpperBound());
  }


  // --------------------------------------------------------------------
  // Nachweis dass Cardinality Hex und negatives als Cardinality nicht
  // akzeptiert
  @Test
  public void testHex() throws IOException {
    Optional<ASTCardinality> oast = parser.parse_StringCardinality(
    		"[0x34..0x15]");
    assertEquals(false, oast.isPresent());
  }


}
