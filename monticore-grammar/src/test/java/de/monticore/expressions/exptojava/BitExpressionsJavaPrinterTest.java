/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.exptojava;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.testbitexpressions.TestBitExpressionsMill;
import de.monticore.expressions.testbitexpressions._auxiliary.BitExpressionsMillForTestBitExpressions;
import de.monticore.expressions.testbitexpressions._parser.TestBitExpressionsParser;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(TestBitExpressionsMill.class)
public class BitExpressionsJavaPrinterTest {
  
  protected TestBitExpressionsParser parser;
  
  @BeforeEach
  public void init() {
    parser = TestBitExpressionsMill.parser();
  }
  
  @Test
  public void testLeftShiftExpression() throws IOException {
    Optional<ASTExpression> result = parser.parse_StringExpression("a<<b");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    ASTExpression ast = result.get();
    
    String output = TestBitExpressionsMill.prettyPrint(ast, false);
    
    result = parser.parse_StringExpression(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(ast.deepEquals(result.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testRightShiftExpression() throws IOException {
    Optional<ASTExpression> result = parser.parse_StringExpression("a>>b");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    ASTExpression ast = result.get();
    
    String output = TestBitExpressionsMill.prettyPrint(ast, false);
    
    result = parser.parse_StringExpression(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(ast.deepEquals(result.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testLogicalRightShiftExpression() throws IOException {
    Optional<ASTExpression> result = parser.parse_StringExpression("a>>>b");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    ASTExpression ast = result.get();
    
    String output = TestBitExpressionsMill.prettyPrint(ast, false);
    
    result = parser.parse_StringExpression(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(ast.deepEquals(result.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testBinaryOrOpExpression() throws IOException {
    Optional<ASTExpression> result = parser.parse_StringExpression("a|b");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    ASTExpression ast = result.get();
    
    String output = TestBitExpressionsMill.prettyPrint(ast, false);
    
    result = parser.parse_StringExpression(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(ast.deepEquals(result.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testBinaryXorExpression() throws IOException {
    Optional<ASTExpression> result = parser.parse_StringExpression("a^b");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    ASTExpression ast = result.get();
    
    String output = TestBitExpressionsMill.prettyPrint(ast, false);
    
    result = parser.parse_StringExpression(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(ast.deepEquals(result.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testBinaryAndExpression() throws IOException {
    Optional<ASTExpression> result = parser.parse_StringExpression("a&b");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    ASTExpression ast = result.get();
    
    String output = TestBitExpressionsMill.prettyPrint(ast, false);
    
    result = parser.parse_StringExpression(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(ast.deepEquals(result.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
}
