/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.exptojava;

import de.monticore.expressions.bitexpressions._prettyprint.BitExpressionsFullPrettyPrinter;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.testbitexpressions._auxiliary.BitExpressionsMillForTestBitExpressions;
import de.monticore.expressions.testbitexpressions._parser.TestBitExpressionsParser;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BitExpressionsJavaPrinterTest {
  
  protected TestBitExpressionsParser parser;
  protected BitExpressionsFullPrettyPrinter javaPrinter;
  
  @Before
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    BitExpressionsMillForTestBitExpressions.reset();
    BitExpressionsMillForTestBitExpressions.init();
    parser = new TestBitExpressionsParser();
    javaPrinter= new BitExpressionsFullPrettyPrinter(new IndentPrinter());
    javaPrinter.getPrinter().clearBuffer();
  }
  
  @Test
  public void testLeftShiftExpression() throws IOException {
    Optional<ASTExpression> result = parser.parse_StringExpression("a<<b");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    ASTExpression ast = result.get();
    
    String output = javaPrinter.prettyprint(ast);
    
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
    
    String output = javaPrinter.prettyprint(ast);
    
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
    
    String output = javaPrinter.prettyprint(ast);
    
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
    
    String output = javaPrinter.prettyprint(ast);
    
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
    
    String output = javaPrinter.prettyprint(ast);
    
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
    
    String output = javaPrinter.prettyprint(ast);
    
    result = parser.parse_StringExpression(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(ast.deepEquals(result.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
}
