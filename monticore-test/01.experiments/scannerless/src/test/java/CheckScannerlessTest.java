/* (c) https://github.com/MontiCore/monticore */

import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.scannerless.ScannerlessMill;
import de.monticore.scannerless._ast.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(ScannerlessMill.class)
public class CheckScannerlessTest {
    
  
  // --------------------------------------------------------------------
  // Check Types, especially ">" and "> >"
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testType1() throws IOException {
    ASTType ast = ScannerlessMill.parser().parse_StringType( " Theo " )
            .orElseGet(MCAssertions::failAndPrintFindings);
    MCAssertions.assertNoFindings();
    assertEquals("Theo", ast.getName());
    MCAssertions.assertNoFindings();
  }
  
  // --------------------------------------------------------------------
  @Test
  public void testType2() throws IOException {
    ASTType ast = ScannerlessMill.parser().parse_StringType( " List < Theo > " )
            .orElseGet(MCAssertions::failAndPrintFindings);
    MCAssertions.assertNoFindings();
    assertEquals("List", ast.getName());
    ASTTypeArguments ta = ast.getTypeArguments();
    assertEquals("Theo", ta.getType(0).getName());
    MCAssertions.assertNoFindings();
  }
  
  // --------------------------------------------------------------------
  @Test
  public void testType3() throws IOException {
    ASTType ast = ScannerlessMill.parser().parse_StringType( "List<Theo>" )
            .orElseGet(MCAssertions::failAndPrintFindings);
    MCAssertions.assertNoFindings();
    assertEquals("List", ast.getName());
    ASTTypeArguments ta = ast.getTypeArguments();
    assertEquals("Theo", ta.getTypeList().get(0).getName());
    MCAssertions.assertNoFindings();
  }
  
  // --------------------------------------------------------------------
  @Test
  public void testType4() throws IOException {
    ASTType ast = ScannerlessMill.parser().parse_StringType( "List<Set<Theo>>" )
            .orElseGet(MCAssertions::failAndPrintFindings);
    MCAssertions.assertNoFindings();
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
    ASTExpression ast0 = ScannerlessMill.parser().parse_StringExpression(
    	"List<Set<Theo>>" ).orElseGet(MCAssertions::failAndPrintFindings);
    MCAssertions.assertNoFindings();
    assertEquals(ASTTypeAsExpression.class, ast0.getClass());

    ASTType ast1 = ((ASTTypeAsExpression)ast0).getType() ;
    assertEquals("List", ast1.getName());
    MCAssertions.assertNoFindings();
  }
  
  // --------------------------------------------------------------------
  @Test
  public void testType6() throws IOException {
    ASTExpression ast0 = ScannerlessMill.parser().parse_StringExpression(
    	"List<Set<Theo>>>>wert" ).orElseGet(MCAssertions::failAndPrintFindings);
    MCAssertions.assertNoFindings();
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
    ASTExpression ast0 = ScannerlessMill.parser().parse_StringExpression(
    	"List<Set<Theo> > >>wert" ).orElseGet(MCAssertions::failAndPrintFindings);
    MCAssertions.assertNoFindings();
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
    // This cannot be parsed as a Type >> wert
    // This cannot be parsed because of the illegal space in ">>"
    Optional<ASTExpression> ast0 = ScannerlessMill.parser().parse_StringExpression(
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
    ASTExpression ast = ScannerlessMill.parser().parse_StringExpression( " theo + theo " )
            .orElseGet(MCAssertions::failAndPrintFindings);
    MCAssertions.assertNoFindings();
    assertEquals(ASTAddExpression.class, ast.getClass());
    MCAssertions.assertNoFindings();
  }
  
  // --------------------------------------------------------------------
  @Test
  public void testExpr2() throws IOException {
    ASTExpression ast = ScannerlessMill.parser().parse_StringExpression(
    	" (theo < ox) > theo " ).orElseGet(MCAssertions::failAndPrintFindings);
    MCAssertions.assertNoFindings();
    assertEquals(ASTComparisonExpression.class, ast.getClass());
    MCAssertions.assertNoFindings();
  }
  
  // --------------------------------------------------------------------
  @Test
  public void testExpr3() throws IOException {
    ASTExpression ast = ScannerlessMill.parser().parse_StringExpression(
    	" theo >> theo " ).orElseGet(MCAssertions::failAndPrintFindings);
    MCAssertions.assertNoFindings();
    assertEquals(ASTShiftExpression.class, ast.getClass());
    MCAssertions.assertNoFindings();
  }
  
  // --------------------------------------------------------------------
  @Test
  public void testExpr4() throws IOException {
    ASTExpression ast = ScannerlessMill.parser().parse_StringExpression(
    	"theo > theo >> theo >>> theo >= theo" ).orElseGet(MCAssertions::failAndPrintFindings);
    MCAssertions.assertNoFindings();
    assertEquals(ASTComparisonExpression.class, ast.getClass());
    MCAssertions.assertNoFindings();
  }
  
  // --------------------------------------------------------------------
  @Test
  public void testExprNeg1() throws IOException {
    Optional<ASTExpression> ast = ScannerlessMill.parser().parse_StringExpression(
    	"theo > > theo " );
    assertFalse(ast.isPresent());
    MCAssertions.assertHasFinding(finding -> true);
  }
  
  // --------------------------------------------------------------------
  @Test
  public void testExprNeg2() throws IOException {
    Optional<ASTExpression> ast = ScannerlessMill.parser().parse_StringExpression(
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
    ASTA ast = ScannerlessMill.parser().parse_StringA( "  Theo " ).
            orElseGet(MCAssertions::failAndPrintFindings);
    MCAssertions.assertNoFindings();
    assertEquals("Theo", ast.getName());
    MCAssertions.assertNoFindings();
  }
  
  // --------------------------------------------------------------------
  @Test
  public void testB() throws IOException {
    ASTB ast = ScannerlessMill.parser().parse_StringB( "Otto \n Karo  " ).
            orElseGet(MCAssertions::failAndPrintFindings);
    MCAssertions.assertNoFindings();
    assertEquals("Otto", ast.getNameList().get(0));
    assertEquals("Karo", ast.getNameList().get(1));
    MCAssertions.assertNoFindings();
  }

  // --------------------------------------------------------------------
  @Test
  public void testC() throws IOException {
    ASTC ast = ScannerlessMill.parser().parse_StringC( "    Otto,Karo" ).
            orElseGet(MCAssertions::failAndPrintFindings);
    MCAssertions.assertNoFindings();
    assertEquals("Otto", ast.getNameList().get(0));
    assertEquals("Karo", ast.getNameList().get(1));
    MCAssertions.assertNoFindings();
  }

  // --------------------------------------------------------------------
  @Test
  public void testC2() throws IOException {
    Optional<ASTC> ast = ScannerlessMill.parser().parse_StringC( "    Otto ,Karo" );
    assertFalse(ast.isPresent());
    MCAssertions.assertHasFinding(finding -> true);
  }

}

