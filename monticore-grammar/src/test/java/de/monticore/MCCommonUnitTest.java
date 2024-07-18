/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import de.monticore.cardinality._ast.ASTCardinality;
import de.monticore.completeness._ast.ASTCompleteness;
import de.monticore.literals.mccommonliterals._ast.ASTNatLiteral;
import de.monticore.testmccommon.TestMCCommonMill;
import de.monticore.testmccommon._parser.TestMCCommonParser;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.monticore.umlstereotype._ast.ASTStereoValue;
import de.monticore.umlstereotype._ast.ASTStereotype;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class MCCommonUnitTest {
  
  // setup the language infrastructure
  TestMCCommonParser parser = new TestMCCommonParser() ;
  
  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    LogStub.getFindings().clear();
    TestMCCommonMill.reset();
    TestMCCommonMill.init();
  }
  
  // --------------------------------------------------------------------
  // Numbers: Nat
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testNat1() throws IOException {
    ASTNatLiteral ast = parser.parse_StringNatLiteral( " 9" ).get();
    Assertions.assertEquals("9", ast.getSource());
    Assertions.assertEquals(9, ast.getValue());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testNat4() throws IOException {
    ASTNatLiteral ast = parser.parse_StringNatLiteral( " 42 " ).get();
    Assertions.assertEquals("42", ast.getSource());
    Assertions.assertEquals(42, ast.getValue());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  // --------------------------------------------------------------------
  // UMLStereotype
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testModifier() throws IOException {
    ASTModifier ast = parser.parse_StringModifier( "# final" ).get();
    Assertions.assertEquals(true, ast.isProtected());
    Assertions.assertEquals(true, ast.isFinal());
    Assertions.assertEquals(false, ast.isLocal());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  // --------------------------------------------------------------------
  @Test
  public void testModifierStereo() throws IOException {
    ASTModifier ast = parser.parse_StringModifier( "<<bla=\"x1\">>#+?" ).get();
    Assertions.assertEquals(true, ast.isProtected());
    Assertions.assertEquals(true, ast.isPublic());
    Assertions.assertEquals(true, ast.isReadonly());
    Assertions.assertEquals(false, ast.isFinal());
    Assertions.assertEquals(true, ast.isPresentStereotype());
    ASTStereotype sty = ast.getStereotype();
    Assertions.assertEquals("x1", sty.getValue("bla"));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }



  // --------------------------------------------------------------------
  // UMLStereotype
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testStereoValue() throws IOException {
    ASTStereoValue ast = parser.parse_StringStereoValue( "bla=\"17\"" ).get();
    Assertions.assertEquals("bla", ast.getName());
    Assertions.assertEquals(true, ast.isPresentText());
    Assertions.assertEquals("17", ast.getText().getValue());
    Assertions.assertEquals("17", ast.getValue());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  // --------------------------------------------------------------------
  @Test
  public void testStereoValue2() throws IOException {
    ASTStereoValue ast = parser.parse_StringStereoValue( "cc" ).get();
    Assertions.assertEquals("cc", ast.getName());
    Assertions.assertEquals(false, ast.isPresentText());
    Assertions.assertEquals("", ast.getValue());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  // --------------------------------------------------------------------
  @Test
  public void testStereotype() throws IOException {
    ASTStereotype ast = parser.parse_StringStereotype( "<< a1 >>" ).get();
    List<ASTStereoValue> svl = ast.getValuesList();
    Assertions.assertEquals(1, svl.size());
    Assertions.assertEquals(true, ast.contains("a1"));
    Assertions.assertEquals(false, ast.contains("bla"));
    Assertions.assertEquals(true, ast.contains("a1",""));
    Assertions.assertEquals(false, ast.contains("a1","wert1"));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  // --------------------------------------------------------------------
  @Test
  public void testStereotype2() throws IOException {
    ASTStereotype ast = parser.parse_StringStereotype(
    	"<< bla, a1=\"wert1\" >>" ).get();
    List<ASTStereoValue> svl = ast.getValuesList();
    Assertions.assertEquals(2, svl.size());
    Assertions.assertEquals(true, ast.contains("a1"));
    Assertions.assertEquals(false, ast.contains("a1",""));
    Assertions.assertEquals(true, ast.contains("a1","wert1"));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  // --------------------------------------------------------------------
  @Test
  public void testGetValue() throws IOException {
    ASTStereotype ast = parser.parse_StringStereotype(
        "<< bla, a1=\"wert1\" >>" ).get();
    Assertions.assertEquals("wert1", ast.getValue("a1"));
    try {
      Assertions.assertEquals("", ast.getValue("foo"));
      Assertions.fail("Expected an Exception to be thrown");
    } catch (java.util.NoSuchElementException ex) { }
    Assertions.assertEquals("", ast.getValue("bla"));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testEnding() throws IOException {
    Optional<ASTStereotype> oast = parser.parse_StringStereotype(
        "<< bla, a1=\"wert1\" > >" );
    Assertions.assertEquals(false, oast.isPresent());
    
  }


  // --------------------------------------------------------------------
  // Completeness
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testBasics() throws IOException {
    ASTCompleteness ast = parser.parse_StringCompleteness( "(c)"  ).get();
    Assertions.assertEquals(true, ast.isComplete());
    Assertions.assertEquals(false, ast.isIncomplete());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  // --------------------------------------------------------------------
  @Test
  public void testBasics2() throws IOException {
    ASTCompleteness ast = parser.parse_StringCompleteness( "(...)"  ).get();
    Assertions.assertEquals(false, ast.isComplete());
    Assertions.assertEquals(true, ast.isIncomplete());
    Assertions.assertEquals(false, ast.isRightComplete());
    Assertions.assertEquals(false, ast.isLeftComplete());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  // --------------------------------------------------------------------
  @Test
  public void testBasics3() throws IOException {
    ASTCompleteness ast = parser.parse_StringCompleteness( "(...,c)"  ).get();
    Assertions.assertEquals(false, ast.isComplete());
    Assertions.assertEquals(false, ast.isIncomplete());
    Assertions.assertEquals(true, ast.isRightComplete());
    Assertions.assertEquals(false, ast.isLeftComplete());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  // --------------------------------------------------------------------
  @Test
  public void testIllegalComplete() throws IOException {
    Optional<ASTCompleteness> ast =
    		parser.parse_StringCompleteness( "(...,d)"  );
    Assertions.assertEquals(false, ast.isPresent());
  }

  // --------------------------------------------------------------------
  // Cardinality
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testMany() throws IOException {
    ASTCardinality ast = parser.parse_StringCardinality("[*]").get();
    Assertions.assertEquals(true, ast.isMany());
    Assertions.assertEquals(0, ast.getLowerBound());
    Assertions.assertEquals(0, ast.getUpperBound());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  // --------------------------------------------------------------------
  @Test
  public void testLowAndStar() throws IOException {
    ASTCardinality ast = parser.parse_StringCardinality("[7..*]").get();
    Assertions.assertEquals(false, ast.isMany());
    Assertions.assertEquals(true, ast.isNoUpperLimit());
    Assertions.assertEquals(7, ast.getLowerBound());
    Assertions.assertEquals(0, ast.getUpperBound());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  // --------------------------------------------------------------------
  @Test
  public void testLowAndUp() throws IOException {
    ASTCardinality ast = parser.parse_StringCardinality("[17..235]").get();
    Assertions.assertEquals(false, ast.isMany());
    Assertions.assertEquals(17, ast.getLowerBound());
    Assertions.assertEquals(235, ast.getUpperBound());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  // --------------------------------------------------------------------
  @Test
  public void testSpace() throws IOException {
    ASTCardinality ast = parser.parse_StringCardinality(" [ 34 .. 15 ] ").get();
    Assertions.assertEquals(false, ast.isMany());
    Assertions.assertEquals(34, ast.getLowerBound());
    Assertions.assertEquals(15, ast.getUpperBound());
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  // --------------------------------------------------------------------
  // Nachweis dass Cardinality Hex und negatives als Cardinality nicht
  // akzeptiert
  @Test
  public void testHex() throws IOException {
    Optional<ASTCardinality> oast = parser.parse_StringCardinality(
    		"[0x34..0x15]");
    Assertions.assertEquals(false, oast.isPresent());
  }


}
