/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import de.monticore.cardinality._ast.ASTCardinality;
import de.monticore.completeness._ast.ASTCompleteness;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.literals.mccommonliterals._ast.ASTNatLiteral;
import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.testmccommon.TestMCCommonMill;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.monticore.umlstereotype._ast.ASTStereoValue;
import de.monticore.umlstereotype._ast.ASTStereotype;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(TestMCCommonMill.class)
public class MCCommonUnitTest {
  
  // --------------------------------------------------------------------
  // Numbers: Nat
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testNat1() throws IOException {
    Optional<ASTNatLiteral> astOpt = TestMCCommonMill.parser().parse_StringNatLiteral( " 9" );
    assertTrue(astOpt.isPresent());
    ASTNatLiteral ast = astOpt.get();
    assertEquals("9", ast.getSource());
    assertEquals(9, ast.getValue());
  }
  
  @Test
  public void testNat4() throws IOException {
    Optional<ASTNatLiteral> astOpt = TestMCCommonMill.parser().parse_StringNatLiteral( " 42 " );
    assertTrue(astOpt.isPresent());
    ASTNatLiteral ast = astOpt.get();
    assertEquals("42", ast.getSource());
    assertEquals(42, ast.getValue());
  }

  // --------------------------------------------------------------------
  // UMLStereotype
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testModifier() throws IOException {
    Optional<ASTModifier> astOpt = TestMCCommonMill.parser().parse_StringModifier( "# final" );
    assertTrue(astOpt.isPresent());
    ASTModifier ast = astOpt.get();
    assertTrue(ast.isProtected());
    assertTrue(ast.isFinal());
    assertFalse(ast.isLocal());
  }


  // --------------------------------------------------------------------
  @Test
  public void testModifierStereo() throws IOException {
    Optional<ASTModifier> astOpt = TestMCCommonMill.parser().parse_StringModifier( "<<bla=\"x1\">>#+?" );
    assertTrue(astOpt.isPresent());
    ASTModifier ast = astOpt.get();
    assertTrue(ast.isProtected());
    assertTrue(ast.isPublic());
    assertTrue(ast.isReadonly());
    assertFalse(ast.isFinal());
    assertTrue(ast.isPresentStereotype());
    ASTStereotype sty = ast.getStereotype();
    assertEquals("x1", sty.getValue("bla"));
  }



  // --------------------------------------------------------------------
  // UMLStereotype
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testStereoValue() throws IOException {
    Optional<ASTStereoValue> astOpt = TestMCCommonMill.parser().parse_StringStereoValue( "bla=\"17\"" );
    assertTrue(astOpt.isPresent());
    ASTStereoValue ast = astOpt.get();
    assertEquals("bla", ast.getName());
    assertTrue(ast.isPresentText());
    assertEquals("17", ast.getText().getValue());
    assertEquals("17", ast.getValue());
  
    assertTrue(Log.getFindings().isEmpty());
  }


  // --------------------------------------------------------------------
  @Test
  public void testStereoValue2() throws IOException {
    Optional<ASTStereoValue> astOpt = TestMCCommonMill.parser().parse_StringStereoValue( "cc" );
    assertTrue(astOpt.isPresent());
    ASTStereoValue ast = astOpt.get();
    assertEquals("cc", ast.getName());
    assertFalse(ast.isPresentText());
    assertEquals("", ast.getValue());
  }


  // --------------------------------------------------------------------
  @Test
  public void testStereoValueExpr() throws IOException {
    Optional<ASTStereoValue> astOpt = TestMCCommonMill.parser().parse_StringStereoValue( "bla=name1" );
    assertTrue(astOpt.isPresent());
    ASTStereoValue ast = astOpt.get();
    assertEquals("bla", ast.getName());
    assertFalse(ast.isPresentText());
    assertInstanceOf(ASTNameExpression.class, ast.getExpression());
    assertTrue(((ASTNameExpression) ast.getExpression()).getName().equals("name1"));
  }

  // --------------------------------------------------------------------
  @Test
  public void testStereotype() throws IOException {
    Optional<ASTStereotype> astOpt = TestMCCommonMill.parser().parse_StringStereotype( "<< a1 >>" );
    assertTrue(astOpt.isPresent());
    ASTStereotype ast = astOpt.get();
    List<ASTStereoValue> svl = ast.getValuesList();
    assertEquals(1, svl.size());
    assertTrue(ast.contains("a1"));
    assertFalse(ast.contains("bla"));
    assertTrue(ast.contains("a1", ""));
    assertFalse(ast.contains("a1", "wert1"));
  }


  // --------------------------------------------------------------------
  @Test
  public void testStereotype2() throws IOException {
    Optional<ASTStereotype> astOpt = TestMCCommonMill.parser().parse_StringStereotype(
    	"<< bla, a1=\"wert1\" >>" );
    assertTrue(astOpt.isPresent());
    ASTStereotype ast = astOpt.get();
    List<ASTStereoValue> svl = ast.getValuesList();
    assertEquals(2, svl.size());
    assertTrue(ast.contains("a1"));
    assertFalse(ast.contains("a1", ""));
    assertTrue(ast.contains("a1", "wert1"));
  }


  // --------------------------------------------------------------------
  @Test
  public void testStereotype3() throws IOException {
    Optional<ASTStereotype> astOpt = TestMCCommonMill.parser().parse_StringStereotype( "<< a1=name1 >>" );
    assertTrue(astOpt.isPresent());
    ASTStereotype ast = astOpt.get();
    List<ASTStereoValue> svl = ast.getValuesList();
    assertEquals(1, svl.size());
    assertTrue(ast.contains("a1"));
    assertFalse(ast.contains("bla"));
    assertTrue(ast.contains("a1", ""));
    assertFalse(ast.contains("a1", "name1"));
    assertInstanceOf(ASTNameExpression.class, ast.getValues(0).getExpression());
    assertEquals("name1", ((ASTNameExpression) ast.getValues(0).getExpression()).getName());
  }


  // --------------------------------------------------------------------
  @Test
  public void testGetValue() throws IOException {
    Optional<ASTStereotype> astOpt = TestMCCommonMill.parser().parse_StringStereotype(
        "<< bla, a1=\"wert1\" >>" );
    assertTrue(astOpt.isPresent());
    ASTStereotype ast = astOpt.get();
    assertEquals("wert1", ast.getValue("a1"));
    try {
      assertEquals("", ast.getValue("foo"));
      fail("Expected an Exception to be thrown");
    } catch (java.util.NoSuchElementException ignored) { }
    assertEquals("", ast.getValue("bla"));
  
    assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testEnding() throws IOException {
    Optional<ASTStereotype> oast = TestMCCommonMill.parser().parse_StringStereotype(
        "<< bla, a1=\"wert1\" > >" );
    assertFalse(oast.isPresent());
    
    MCAssertions.assertHasFindingStartingWith("no viable alternative at input '>'");
  }


  // --------------------------------------------------------------------
  // Completeness
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testBasics() throws IOException {
    Optional<ASTCompleteness> astOpt = TestMCCommonMill.parser().parse_StringCompleteness( "(c)"  );
    assertTrue(astOpt.isPresent());
    ASTCompleteness ast = astOpt.get();
    assertTrue(ast.isComplete());
    assertFalse(ast.isIncomplete());
  }


  // --------------------------------------------------------------------
  @Test
  public void testBasics2() throws IOException {
    Optional<ASTCompleteness> astOpt = TestMCCommonMill.parser().parse_StringCompleteness( "(...)"  );
    assertTrue(astOpt.isPresent());
    ASTCompleteness ast = astOpt.get();
    assertFalse(ast.isComplete());
    assertTrue(ast.isIncomplete());
    assertFalse(ast.isRightComplete());
    assertFalse(ast.isLeftComplete());
  }


  // --------------------------------------------------------------------
  @Test
  public void testBasics3() throws IOException {
    Optional<ASTCompleteness> astOpt = TestMCCommonMill.parser().parse_StringCompleteness( "(...,c)"  );
    assertTrue(astOpt.isPresent());
    ASTCompleteness ast = astOpt.get();
    assertFalse(ast.isComplete());
    assertFalse(ast.isIncomplete());
    assertTrue(ast.isRightComplete());
    assertFalse(ast.isLeftComplete());
  }


  // --------------------------------------------------------------------
  @Test
  public void testIllegalComplete() throws IOException {
    Optional<ASTCompleteness> ast =
    		TestMCCommonMill.parser().parse_StringCompleteness( "(...,d)"  );
    assertFalse(ast.isPresent());
    
    MCAssertions.assertHasFindingStartingWith("mismatched input ',' expecting ')' (found: COMMA)");
  }

  // --------------------------------------------------------------------
  // Cardinality
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testMany() throws IOException {
    Optional<ASTCardinality> astOpt = TestMCCommonMill.parser().parse_StringCardinality("[*]");
    assertTrue(astOpt.isPresent());
    ASTCardinality ast = astOpt.get();
    assertTrue(ast.isMany());
    assertEquals(0, ast.getLowerBound());
    assertEquals(0, ast.getUpperBound());
  }


  // --------------------------------------------------------------------
  @Test
  public void testLowAndStar() throws IOException {
    Optional<ASTCardinality> astOpt = TestMCCommonMill.parser().parse_StringCardinality("[7..*]");
    assertTrue(astOpt.isPresent());
    ASTCardinality ast = astOpt.get();
    assertFalse(ast.isMany());
    assertTrue(ast.isNoUpperLimit());
    assertEquals(7, ast.getLowerBound());
    assertEquals(0, ast.getUpperBound());
  }


  // --------------------------------------------------------------------
  @Test
  public void testLowAndUp() throws IOException {
    Optional<ASTCardinality> astOpt = TestMCCommonMill.parser().parse_StringCardinality("[17..235]");
    assertTrue(astOpt.isPresent());
    ASTCardinality ast = astOpt.get();
    assertFalse(ast.isMany());
    assertEquals(17, ast.getLowerBound());
    assertEquals(235, ast.getUpperBound());
  }


  // --------------------------------------------------------------------
  @Test
  public void testSpace() throws IOException {
    Optional<ASTCardinality> astOpt = TestMCCommonMill.parser().parse_StringCardinality(" [ 34 .. 15 ] ");
    assertTrue(astOpt.isPresent());
    ASTCardinality ast = astOpt.get();
    assertFalse(ast.isMany());
    assertEquals(34, ast.getLowerBound());
    assertEquals(15, ast.getUpperBound());
  }


  // --------------------------------------------------------------------
  // Nachweis dass Cardinality Hex und negatives als Cardinality nicht
  // akzeptiert
  @Test
  public void testHex() throws IOException {
    Optional<ASTCardinality> oast = TestMCCommonMill.parser().parse_StringCardinality(
    		"[0x34..0x15]");
    assertFalse(oast.isPresent());

    MCAssertions.assertHasFindingStartingWith("extraneous input 'x34'");
    MCAssertions.assertHasFindingStartingWith("extraneous input 'x15'");
  }
}
