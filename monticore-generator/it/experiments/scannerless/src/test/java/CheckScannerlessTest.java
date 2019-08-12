package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

import org.antlr.v4.runtime.RecognitionException;
import org.junit.*;

import de.monticore.scannerless._ast.*;
import de.monticore.scannerless._cocos.*;
import de.monticore.scannerless._symboltable.*;
import de.monticore.scannerless._parser.*;

import de.monticore.ModelingLanguage;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.*;
import de.monticore.ast.*;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.*;


public class CheckScannerlessTest {
    
  // setup the language infrastructure
  ScannerlessParser parser = new ScannerlessParser() ;
  
  @BeforeClass
  public static void init() {
    // replacing log by a side effect free variant
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
    assertEquals("Theo", ta.getType(0).getName());
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
  @Test
  public void testType5() throws IOException {
    ASTExpression ast0 = parser.parse_StringExpression(
    	"List<Set<Theo>>" ).get();
    assertEquals(ASTTypeAsExpression.class, ast0.getClass());

    ASTType ast1 = ((ASTTypeAsExpression)ast0).getType() ;
    assertEquals("List", ast1.getName());
  }
  
  // --------------------------------------------------------------------
  @Test
  public void testType6() throws IOException {
    ASTExpression ast0 = parser.parse_StringExpression(
    	"List<Set<Theo>>>>wert" ).get();
    assertEquals(ASTShiftExpression.class, ast0.getClass());

    ASTExpression ast1 = ((ASTShiftExpression)ast0).getLeftExpression() ;

    assertEquals(ASTTypeAsExpression.class, ast1.getClass());
    ASTType ast2 = ((ASTTypeAsExpression)ast1).getType() ;
    assertEquals("List", ast2.getName());
  }
  

  // --------------------------------------------------------------------
  @Test
  public void testType7() throws IOException {
    // This will be parsed as Type >> wert, because the
    // type has a higher precedence
    ASTExpression ast0 = parser.parse_StringExpression(
    	"List<Set<Theo> > >>wert" ).get();
    assertEquals(ASTShiftExpression.class, ast0.getClass());

    ASTExpression ast1 = ((ASTShiftExpression)ast0).getLeftExpression() ;

    assertEquals(ASTTypeAsExpression.class, ast1.getClass());
    ASTType ast2 = ((ASTTypeAsExpression)ast1).getType() ;
    assertEquals("List", ast2.getName());
  }
  

  // --------------------------------------------------------------------
  @Test
  public void testType8() throws IOException {
    // This cannot be parsed as a Type >> wert
    // This cannot be parsed because of the illegal space in ">>"
    Optional<ASTExpression> ast0 = parser.parse_StringExpression(
    	"List<Set<Theo>>> >wert" );
    assertFalse(ast0.isPresent());
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
    	" theo >> theo " ).get();
    assertEquals(ASTShiftExpression.class, ast.getClass());
  }
  
  // --------------------------------------------------------------------
  @Test
  public void testExpr4() throws IOException {
    ASTExpression ast = parser.parse_StringExpression(
    	"theo > theo >> theo >>> theo >= theo" ).get();
    assertEquals(ASTComparisonExpression.class, ast.getClass());
  }
  
  // --------------------------------------------------------------------
  @Test
  public void testExprNeg1() throws IOException {
    Optional<ASTExpression> ast = parser.parse_StringExpression(
    	"theo > > theo " );
    assertFalse(ast.isPresent());
  }
  
  // --------------------------------------------------------------------
  @Test
  public void testExprNeg2() throws IOException {
    Optional<ASTExpression> ast = parser.parse_StringExpression(
    	"theo < << theo " );
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
    ASTC ast = parser.parse_StringC( "    Otto,Karo" ).get();
    assertEquals("Otto", ast.getNameList().get(0));
    assertEquals("Karo", ast.getNameList().get(1));
  }

  // --------------------------------------------------------------------
  @Test
  public void testC2() throws IOException {
    Optional<ASTC> ast = parser.parse_StringC( "    Otto ,Karo" );
    assertFalse(ast.isPresent());
  }

}

