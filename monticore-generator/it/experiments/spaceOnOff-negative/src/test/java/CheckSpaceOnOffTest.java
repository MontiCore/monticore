/* (c) https://github.com/MontiCore/monticore */

import de.monticore.spaceonoff._ast.*;
import de.monticore.spaceonoff._parser.SpaceOnOffParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;


public class CheckSpaceOnOffTest {
    
  // setup the language infrastructure
  SpaceOnOffParser parser = new SpaceOnOffParser() ;
  
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
  // Check Types, especially ">" and "> >"
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testType1() throws IOException {
    ASTType ast = parser.parse_StringType( " Theo " ).get();
    assertEquals("Theo", ast.getName());
  }
  
  // --------------------------------------------------------------------
  @Test
  public void testType2() throws IOException {
    ASTType ast = parser.parse_StringType( " List < Theo > " ).get();
    assertEquals("List", ast.getName());
    ASTTypeArguments ta = ast.getTypeArguments();
    assertEquals("Theo", ta.getTypeList().get(0).getName());
  }
  
  // --------------------------------------------------------------------
  @Test
  public void testType3() throws IOException {
    ASTType ast = parser.parse_StringType( "List<Theo>" ).get();
    assertEquals("List", ast.getName());
    ASTTypeArguments ta = ast.getTypeArguments();
    assertEquals("Theo", ta.getTypeList().get(0).getName());
  }
  
  // --------------------------------------------------------------------
  @Test
  public void testType4() throws IOException {
    ASTType ast = parser.parse_StringType( "List<Set<Theo>>" ).get();
    assertEquals("List", ast.getName());
    ASTTypeArguments ta = ast.getTypeArguments();
    assertEquals("Set", ta.getTypeList().get(0).getName());
    ASTTypeArguments ta2 = ta.getTypeList().get(0).getTypeArguments();
    assertEquals("Theo", ta2.getTypeList().get(0).getName());
  }
  

  // --------------------------------------------------------------------
  // Check Expression, especially ">" and "> >"
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testExpr1() throws IOException {
    ASTExpression ast = parser.parse_StringExpression( " theo + theo " ).get();
    assertEquals(ASTAddExpression.class, ast.getClass());
  }
  
  // --------------------------------------------------------------------
  @Test
  public void testExpr2() throws IOException {
    ASTExpression ast = parser.parse_StringExpression(
    	" (theo < ox) > theo " ).get();
    assertEquals(ASTComparisonExpression.class, ast.getClass());
  }
  
  // --------------------------------------------------------------------
  @Test
  public void testExpr3() throws IOException {
    ASTExpression ast = parser.parse_StringExpression(
    	" theo :!>>!: theo " ).get();
    assertEquals(ASTShiftExpression.class, ast.getClass());
  }
  
  // --------------------------------------------------------------------
  @Test
  public void testExpr4() throws IOException {
    ASTExpression ast = parser.parse_StringExpression(
    	"theo > theo :!>>!: theo :!>>>!: theo :!>=!: theo" ).get();
    assertEquals(ASTComparisonExpression.class, ast.getClass());
  }
  
  // --------------------------------------------------------------------
  @Test
  public void testExprNeg1() throws IOException {
    Optional<ASTExpression> ast = parser.parse_StringExpression(
    	"theo > \n > theo " );
    assertFalse(ast.isPresent());
  }
  
  // --------------------------------------------------------------------
  @Test
  public void testExprNeg2() throws IOException {
    Optional<ASTExpression> ast = parser.parse_StringExpression(
    	"theo :!< <<!: theo " );
    assertFalse(ast.isPresent());
  }
  

  // --------------------------------------------------------------------
  // Check whether S and S1 work well
  // --------------------------------------------------------------------

  // --------------------------------------------------------------------
  @Test
  public void testA() throws IOException {
    ASTA ast = parser.parse_StringA( "  Theo " ).get();
    assertEquals("Theo", ast.getName());
  }
  
  // --------------------------------------------------------------------
  @Test
  public void testB() throws IOException {
    ASTB ast = parser.parse_StringB( "Otto \n Karo  " ).get();
    assertEquals("Otto", ast.getNameList().get(0));
    assertEquals("Karo", ast.getNameList().get(1));
  }

  // --------------------------------------------------------------------
  @Test
  public void testC() throws IOException {
    ASTC ast = parser.parse_StringC( "    :!Otto,Karo!:" ).get();
    assertEquals("Otto", ast.getNameList().get(0));
    assertEquals("Karo", ast.getNameList().get(1));
  }

  // --------------------------------------------------------------------
  @Test
  public void testC2() throws IOException {
    Optional<ASTC> ast = parser.parse_StringC( "    :!Otto ,Karo!:" );
    assertFalse(ast.isPresent());
  }

}

