/* (c) https://github.com/MontiCore/monticore */

import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.spaceonoff.SpaceOnOffMill;
import de.monticore.spaceonoff._ast.*;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(SpaceOnOffMill.class)
public class CheckSpaceOnOffTest {
  
  // --------------------------------------------------------------------
  // Check Types, especially ">" and "> >"
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testType1() throws IOException {
    ASTType ast = SpaceOnOffMill.parser().parse_StringType( " Theo " ).get();
    assertEquals("Theo", ast.getName());
    MCAssertions.assertNoFindings();
  }
  
  // --------------------------------------------------------------------
  @Test
  public void testType2() throws IOException {
    ASTType ast = SpaceOnOffMill.parser().parse_StringType( " List < Theo > " ).get();
    assertEquals("List", ast.getName());
    ASTTypeArguments ta = ast.getTypeArguments();
    assertEquals("Theo", ta.getTypeList().get(0).getName());
    MCAssertions.assertNoFindings();
  }
  
  // --------------------------------------------------------------------
  @Test
  public void testType3() throws IOException {
    ASTType ast = SpaceOnOffMill.parser().parse_StringType( "List<Theo>" ).get();
    assertEquals("List", ast.getName());
    ASTTypeArguments ta = ast.getTypeArguments();
    assertEquals("Theo", ta.getTypeList().get(0).getName());
    MCAssertions.assertNoFindings();
  }
  
  // --------------------------------------------------------------------
  @Test
  public void testType4() throws IOException {
    ASTType ast = SpaceOnOffMill.parser().parse_StringType( "List<Set<Theo>>" ).get();
    assertEquals("List", ast.getName());
    ASTTypeArguments ta = ast.getTypeArguments();
    assertEquals("Set", ta.getTypeList().get(0).getName());
    ASTTypeArguments ta2 = ta.getTypeList().get(0).getTypeArguments();
    assertEquals("Theo", ta2.getTypeList().get(0).getName());
    MCAssertions.assertNoFindings();
  }
  

  // --------------------------------------------------------------------
  // Check Expression, especially ">" and "> >"
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testExpr1() throws IOException {
    ASTExpression ast = SpaceOnOffMill.parser().parse_StringExpression( " theo + theo " ).get();
    assertEquals(ASTAddExpression.class, ast.getClass());
    MCAssertions.assertNoFindings();
  }
  
  // --------------------------------------------------------------------
  @Test
  public void testExpr2() throws IOException {
    ASTExpression ast = SpaceOnOffMill.parser().parse_StringExpression(
    	" (theo < ox) > theo " ).get();
    assertEquals(ASTComparisonExpression.class, ast.getClass());
    MCAssertions.assertNoFindings();
  }
  
  // --------------------------------------------------------------------
  @Test
  public void testExpr3() throws IOException {
    ASTExpression ast = SpaceOnOffMill.parser().parse_StringExpression(
    	" theo :!>>!: theo " ).get();
    assertEquals(ASTShiftExpression.class, ast.getClass());
    MCAssertions.assertNoFindings();
  }
  
  // --------------------------------------------------------------------
  @Test
  public void testExpr4() throws IOException {
    ASTExpression ast = SpaceOnOffMill.parser().parse_StringExpression(
    	"theo > theo :!>>!: theo :!>>>!: theo :!>=!: theo" ).get();
    assertEquals(ASTComparisonExpression.class, ast.getClass());
    MCAssertions.assertNoFindings();
  }
  
  // --------------------------------------------------------------------
  @Test
  public void testExprNeg1() throws IOException {
    Optional<ASTExpression> ast = SpaceOnOffMill.parser().parse_StringExpression(
    	"theo > \n > theo " );
    assertFalse(ast.isPresent());
    MCAssertions.assertHasFinding(finding -> true);
  }
  
  // --------------------------------------------------------------------
  @Test
  public void testExprNeg2() throws IOException {
    Optional<ASTExpression> ast = SpaceOnOffMill.parser().parse_StringExpression(
    	"theo :!< <<!: theo " );
    assertFalse(ast.isPresent());
    MCAssertions.assertHasFinding(finding -> true);
  }
  

  // --------------------------------------------------------------------
  // Check whether S and S1 work well
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testA() throws IOException {
    ASTA ast = SpaceOnOffMill.parser().parse_StringA( "  Theo " ).get();
    assertEquals("Theo", ast.getName());
    MCAssertions.assertNoFindings();
  }
  
  // --------------------------------------------------------------------
  @Test
  public void testB() throws IOException {
    ASTB ast = SpaceOnOffMill.parser().parse_StringB( "Otto \n Karo  " ).get();
    assertEquals("Otto", ast.getNameList().get(0));
    assertEquals("Karo", ast.getNameList().get(1));
    MCAssertions.assertNoFindings();
  }

  // --------------------------------------------------------------------
  @Test
  public void testC() throws IOException {
    ASTC ast = SpaceOnOffMill.parser().parse_StringC( "    :!Otto,Karo!:" ).get();
    assertEquals("Otto", ast.getNameList().get(0));
    assertEquals("Karo", ast.getNameList().get(1));
    MCAssertions.assertNoFindings();
  }

  // --------------------------------------------------------------------
  @Test
  public void testC2() throws IOException {
    Optional<ASTC> ast = SpaceOnOffMill.parser().parse_StringC( "    :!Otto ,Karo!:" );
    assertFalse(ast.isPresent());
    MCAssertions.assertHasFinding(finding -> true);
  }

}

