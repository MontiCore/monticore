/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import de.monticore.cardinality._ast.ASTCardinality;
import de.monticore.completeness._ast.ASTCompleteness;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.literals.mccommonliterals._ast.ASTNatLiteral;
import de.monticore.testmccommon.TestMCCommonMill;
import de.monticore.testmccommon._parser.TestMCCommonParser;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.monticore.umlstereotype._ast.ASTStereoValue;
import de.monticore.umlstereotype._ast.ASTStereotype;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

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
    assertEquals("9", ast.getSource());
    assertEquals(9, ast.getValue());
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testNat4() throws IOException {
    ASTNatLiteral ast = parser.parse_StringNatLiteral( " 42 " ).get();
    assertEquals("42", ast.getSource());
    assertEquals(42, ast.getValue());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  // --------------------------------------------------------------------
  // UMLStereotype
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testModifier() throws IOException {
    ASTModifier ast = parser.parse_StringModifier( "# final" ).get();
    assertTrue(ast.isProtected());
    assertTrue(ast.isFinal());
    assertFalse(ast.isLocal());
  
    assertTrue(Log.getFindings().isEmpty());
  }


  // --------------------------------------------------------------------
  @Test
  public void testModifierStereo() throws IOException {
    ASTModifier ast = parser.parse_StringModifier( "<<bla=\"x1\">>#+?" ).get();
    assertTrue(ast.isProtected());
    assertTrue(ast.isPublic());
    assertTrue(ast.isReadonly());
    assertFalse(ast.isFinal());
    assertTrue(ast.isPresentStereotype());
    ASTStereotype sty = ast.getStereotype();
    assertEquals("x1", sty.getValue("bla"));
  
    assertTrue(Log.getFindings().isEmpty());
  }



  // --------------------------------------------------------------------
  // UMLStereotype
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testStereoValue() throws IOException {
    ASTStereoValue ast = parser.parse_StringStereoValue( "bla=\"17\"" ).get();
    assertEquals("bla", ast.getName());
    assertTrue(ast.isPresentText());
    assertEquals("17", ast.getText().getValue());
    assertEquals("17", ast.getValue());
  
    assertTrue(Log.getFindings().isEmpty());
  }


  // --------------------------------------------------------------------
  @Test
  public void testStereoValue2() throws IOException {
    ASTStereoValue ast = parser.parse_StringStereoValue( "cc" ).get();
    assertEquals("cc", ast.getName());
    assertFalse(ast.isPresentText());
    assertEquals("", ast.getValue());
  
    assertTrue(Log.getFindings().isEmpty());
  }


  // --------------------------------------------------------------------
  @Test
  public void testStereoValueExpr() throws IOException {
    ASTStereoValue ast = parser.parse_StringStereoValue( "bla=name1" ).get();
    assertEquals("bla", ast.getName());
    assertFalse(ast.isPresentText());
    assertTrue(ast.getExpression() instanceof ASTNameExpression);
    assertTrue(((ASTNameExpression) ast.getExpression()).getName().equals("name1"));

    assertTrue(Log.getFindings().isEmpty());
  }

  // --------------------------------------------------------------------
  @Test
  public void testStereotype() throws IOException {
    ASTStereotype ast = parser.parse_StringStereotype( "<< a1 >>" ).get();
    List<ASTStereoValue> svl = ast.getValuesList();
    assertEquals(1, svl.size());
    assertTrue(ast.contains("a1"));
    assertFalse(ast.contains("bla"));
    assertTrue(ast.contains("a1", ""));
    assertFalse(ast.contains("a1", "wert1"));
  
    assertTrue(Log.getFindings().isEmpty());
  }


  // --------------------------------------------------------------------
  @Test
  public void testStereotype2() throws IOException {
    ASTStereotype ast = parser.parse_StringStereotype(
    	"<< bla, a1=\"wert1\" >>" ).get();
    List<ASTStereoValue> svl = ast.getValuesList();
    assertEquals(2, svl.size());
    assertTrue(ast.contains("a1"));
    assertFalse(ast.contains("a1", ""));
    assertTrue(ast.contains("a1", "wert1"));
  
    assertTrue(Log.getFindings().isEmpty());
  }


  // --------------------------------------------------------------------
  @Test
  public void testStereotype3() throws IOException {
    ASTStereotype ast = parser.parse_StringStereotype( "<< a1=name1 >>" ).get();
    List<ASTStereoValue> svl = ast.getValuesList();
    assertEquals(1, svl.size());
    assertTrue(ast.contains("a1"));
    assertFalse(ast.contains("bla"));
    assertTrue(ast.contains("a1", ""));
    assertFalse(ast.contains("a1", "name1"));
    assertInstanceOf(ASTNameExpression.class, ast.getValues(0).getExpression());
    assertEquals("name1", ((ASTNameExpression) ast.getValues(0).getExpression()).getName());

    assertTrue(Log.getFindings().isEmpty());
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
  
    assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testEnding() throws IOException {
    Optional<ASTStereotype> oast = parser.parse_StringStereotype(
        "<< bla, a1=\"wert1\" > >" );
    assertFalse(oast.isPresent());
    
  }


  // --------------------------------------------------------------------
  // Completeness
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testBasics() throws IOException {
    ASTCompleteness ast = parser.parse_StringCompleteness( "(c)"  ).get();
    assertTrue(ast.isComplete());
    assertFalse(ast.isIncomplete());
  
    assertTrue(Log.getFindings().isEmpty());
  }


  // --------------------------------------------------------------------
  @Test
  public void testBasics2() throws IOException {
    ASTCompleteness ast = parser.parse_StringCompleteness( "(...)"  ).get();
    assertFalse(ast.isComplete());
    assertTrue(ast.isIncomplete());
    assertFalse(ast.isRightComplete());
    assertFalse(ast.isLeftComplete());
  
    assertTrue(Log.getFindings().isEmpty());
  }


  // --------------------------------------------------------------------
  @Test
  public void testBasics3() throws IOException {
    ASTCompleteness ast = parser.parse_StringCompleteness( "(...,c)"  ).get();
    assertFalse(ast.isComplete());
    assertFalse(ast.isIncomplete());
    assertTrue(ast.isRightComplete());
    assertFalse(ast.isLeftComplete());
  
    assertTrue(Log.getFindings().isEmpty());
  }


  // --------------------------------------------------------------------
  @Test
  public void testIllegalComplete() throws IOException {
    Optional<ASTCompleteness> ast =
    		parser.parse_StringCompleteness( "(...,d)"  );
    assertFalse(ast.isPresent());
  }

  // --------------------------------------------------------------------
  // Cardinality
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testMany() throws IOException {
    ASTCardinality ast = parser.parse_StringCardinality("[*]").get();
    assertTrue(ast.isMany());
    assertEquals(0, ast.getLowerBound());
    assertEquals(0, ast.getUpperBound());
  
    assertTrue(Log.getFindings().isEmpty());
  }


  // --------------------------------------------------------------------
  @Test
  public void testLowAndStar() throws IOException {
    ASTCardinality ast = parser.parse_StringCardinality("[7..*]").get();
    assertFalse(ast.isMany());
    assertTrue(ast.isNoUpperLimit());
    assertEquals(7, ast.getLowerBound());
    assertEquals(0, ast.getUpperBound());
  
    assertTrue(Log.getFindings().isEmpty());
  }


  // --------------------------------------------------------------------
  @Test
  public void testLowAndUp() throws IOException {
    ASTCardinality ast = parser.parse_StringCardinality("[17..235]").get();
    assertFalse(ast.isMany());
    assertEquals(17, ast.getLowerBound());
    assertEquals(235, ast.getUpperBound());
  
    assertTrue(Log.getFindings().isEmpty());
  }


  // --------------------------------------------------------------------
  @Test
  public void testSpace() throws IOException {
    ASTCardinality ast = parser.parse_StringCardinality(" [ 34 .. 15 ] ").get();
    assertFalse(ast.isMany());
    assertEquals(34, ast.getLowerBound());
    assertEquals(15, ast.getUpperBound());
  
    assertTrue(Log.getFindings().isEmpty());
  }


  // --------------------------------------------------------------------
  // Nachweis dass Cardinality Hex und negatives als Cardinality nicht
  // akzeptiert
  @Test
  public void testHex() throws IOException {
    Optional<ASTCardinality> oast = parser.parse_StringCardinality(
    		"[0x34..0x15]");
    assertFalse(oast.isPresent());
  }


}
