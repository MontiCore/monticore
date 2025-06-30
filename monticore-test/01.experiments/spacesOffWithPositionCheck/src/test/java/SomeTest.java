/* (c) https://github.com/MontiCore/monticore */

import java.io.IOException;
import java.util.Optional;

import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.spacefreechecks.SpaceFreeChecksMill;

import de.monticore.spacefreechecks._ast.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


@TestWithMCLanguage(SpaceFreeChecksMill.class)
public class SomeTest {

  // --------------------------------------------------------------------
  // Check Types, especially ">" and "> >"
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testType1() throws IOException {
    ASTType ast = SpaceFreeChecksMill.parser().parse_StringType( " Theo " ).get();
    assertEquals("Theo", ast.getName());
    MCAssertions.assertNoFindings();
  }
  
  // --------------------------------------------------------------------
  @Test
  public void testType2() throws IOException {
    ASTType ast = SpaceFreeChecksMill.parser().parse_StringType( " List < Theo > " ).get();
    assertEquals("List", ast.getName());
    ASTTypeArguments ta = ast.getTypeArguments();
    assertEquals("Theo", ta.getTypeList().get(0).getName());
    MCAssertions.assertNoFindings();
  }
  
  // --------------------------------------------------------------------
  @Test
  public void testType3() throws IOException {
    ASTType ast = SpaceFreeChecksMill.parser().parse_StringType( "List<Theo>" ).get();
    assertEquals("List", ast.getName());
    ASTTypeArguments ta = ast.getTypeArguments();
    assertEquals("Theo", ta.getTypeList().get(0).getName());
    MCAssertions.assertNoFindings();
  }
  
  // --------------------------------------------------------------------
  @Test
  public void testType4() throws IOException {
    ASTType ast = SpaceFreeChecksMill.parser().parse_StringType( "List<Set<Theo>>" ).get();
    assertEquals("List", ast.getName());
    ASTTypeArguments ta = ast.getTypeArguments();
    assertEquals("Set", ta.getTypeList().get(0).getName());
    ASTTypeArguments ta2 = ta.getTypeList().get(0).getTypeArguments();
    assertEquals("Theo", ta2.getTypeList().get(0).getName());
    MCAssertions.assertNoFindings();
  }
  
  // --------------------------------------------------------------------
  @Test
  public void testType5() throws IOException {
    ASTExpression ast0 = SpaceFreeChecksMill.parser().parse_StringExpression(
    	"List<Set<Theo>>" ).get();
    assertEquals(ASTTypeAsExpression.class, ast0.getClass());

    ASTType ast1 = ((ASTTypeAsExpression)ast0).getType() ;
    assertEquals("List", ast1.getName());
    MCAssertions.assertNoFindings();
  }
  
  // --------------------------------------------------------------------
  @Test
  public void testType6() throws IOException {
    ASTExpression ast0 = SpaceFreeChecksMill.parser().parse_StringExpression(
    	"List<Set<Theo>>>>wert" ).get();
    assertEquals(ASTShiftExpression.class, ast0.getClass());

    ASTExpression ast1 = ((ASTShiftExpression)ast0).getLeftExpression() ;

    assertEquals(ASTTypeAsExpression.class, ast1.getClass());
    ASTType ast2 = ((ASTTypeAsExpression)ast1).getType() ;
    assertEquals("List", ast2.getName());
    MCAssertions.assertNoFindings();
  }
  

  // --------------------------------------------------------------------
  @Test
  public void testType7() throws IOException {
    // This will be parsed as Type >> wert, because the
    // type has a higher precedence
    ASTExpression ast0 = SpaceFreeChecksMill.parser().parse_StringExpression(
    	"List<Set<Theo> > >>wert" ).get();
    assertEquals(ASTShiftExpression.class, ast0.getClass());

    ASTExpression ast1 = ((ASTShiftExpression)ast0).getLeftExpression() ;

    assertEquals(ASTTypeAsExpression.class, ast1.getClass());
    ASTType ast2 = ((ASTTypeAsExpression)ast1).getType() ;
    assertEquals("List", ast2.getName());
    MCAssertions.assertNoFindings();
  }
  

  // --------------------------------------------------------------------
  @Test
  public void testType8() throws IOException {
    // Thois cannot be parsed as a Type >> wert
    Optional<ASTExpression> ast0 = SpaceFreeChecksMill.parser().parse_StringExpression(
    	"List<Set<Theo>>> >wert" );
    assertFalse(ast0.isPresent());
    MCAssertions.assertHasFinding(finding -> true);
  }
  

  // --------------------------------------------------------------------
  // Check Expression, especially ">" and "> >"
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testExpr1() throws IOException {
    ASTExpression ast = SpaceFreeChecksMill.parser().parse_StringExpression( " theo + theo " ).get();
    assertEquals(ASTAddExpression.class, ast.getClass());
    MCAssertions.assertNoFindings();
  }
  
  // --------------------------------------------------------------------
  @Test
  public void testExpr2() throws IOException {
    ASTExpression ast = SpaceFreeChecksMill.parser().parse_StringExpression(
    	" (theo < ox) > theo " ).get();
    assertEquals(ASTComparisonExpression.class, ast.getClass());
    MCAssertions.assertNoFindings();
  }
  
  // --------------------------------------------------------------------
  @Test
  public void testExpr3() throws IOException {
    ASTExpression ast = SpaceFreeChecksMill.parser().parse_StringExpression(
    	" theo >> theo " ).get();
    assertEquals(ASTShiftExpression.class, ast.getClass());
    MCAssertions.assertNoFindings();
  }
  
  // --------------------------------------------------------------------
  @Test
  public void testExpr4() throws IOException {
    ASTExpression ast = SpaceFreeChecksMill.parser().parse_StringExpression(
    	"theo > theo >> theo >>> theo >= theo" ).get();
    assertEquals(ASTComparisonExpression.class, ast.getClass());
    MCAssertions.assertNoFindings();
  }
  
  // --------------------------------------------------------------------
  @Test
  public void testExprNeg1() throws IOException {
    Optional<ASTExpression> ast = SpaceFreeChecksMill.parser().parse_StringExpression(
    	"theo > > theo " );
    assertFalse(ast.isPresent());
    MCAssertions.assertHasFinding(finding -> true);
  }
  
  // --------------------------------------------------------------------
  @Test
  public void testExprNeg2() throws IOException {
    Optional<ASTExpression> ast = SpaceFreeChecksMill.parser().parse_StringExpression(
    	"theo < << theo " );
    assertFalse(ast.isPresent());
    MCAssertions.assertHasFinding(finding -> true);
  }
  

  // --------------------------------------------------------------------
  // Check whether S and S1 work well
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testA() throws IOException {
    ASTA ast = SpaceFreeChecksMill.parser().parse_StringA( "  Theo " ).get();
    assertEquals("Theo", ast.getName());
    MCAssertions.assertNoFindings();
  }
  
  // --------------------------------------------------------------------
  @Test
  public void testB() throws IOException {
    ASTB ast = SpaceFreeChecksMill.parser().parse_StringB( "Otto \n Karo  " ).get();
    assertEquals("Otto", ast.getNameList().get(0));
    assertEquals("Karo", ast.getNameList().get(1));
    MCAssertions.assertNoFindings();
  }

  // --------------------------------------------------------------------
  @Test
  public void testC() throws IOException {
    ASTC ast = SpaceFreeChecksMill.parser().parse_StringC( "    Otto.Karo" ).get();
    assertEquals("Otto", ast.getNameList().get(0));
    assertEquals("Karo", ast.getNameList().get(1));
    MCAssertions.assertNoFindings();
  }

  // --------------------------------------------------------------------
  @Test
  public void testC1() throws IOException {
    ASTC ast = SpaceFreeChecksMill.parser().parse_StringC( "    O.Karo" ).get();
    assertEquals("O", ast.getNameList().get(0));
    assertEquals("Karo", ast.getNameList().get(1));
    MCAssertions.assertNoFindings();
  }

  // --------------------------------------------------------------------
  @Test
  public void testC2() throws IOException {
    Optional<ASTC> ast = SpaceFreeChecksMill.parser().parse_StringC( "    O. Karo" );
    assertFalse(ast.isPresent());
    MCAssertions.assertHasFinding(finding -> true);
  }

  // --------------------------------------------------------------------
  @Test
  public void testD() throws IOException {
    ASTD ast = SpaceFreeChecksMill.parser().parse_StringD( "    Otto.Karo" ).get();
    assertEquals("Otto", ast.getNameList().get(0));
    assertEquals("Karo", ast.getNameList().get(1));
    MCAssertions.assertNoFindings();
  }

  // --------------------------------------------------------------------
  @Test
  public void testD1() throws IOException {
    ASTD ast = SpaceFreeChecksMill.parser().parse_StringD( "    O.Karo" ).get();
    assertEquals("O", ast.getNameList().get(0));
    assertEquals("Karo", ast.getNameList().get(1));
    MCAssertions.assertNoFindings();
  }

  // --------------------------------------------------------------------
  @Test
  public void testD2() throws IOException {
    Optional<ASTD> ast = SpaceFreeChecksMill.parser().parse_StringD( "    O. Karo" );
    assertFalse(ast.isPresent());
    MCAssertions.assertHasFinding(finding -> true);
  }

}

