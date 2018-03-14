/* (c) https://github.com/MontiCore/monticore */

package de.monticore.prettyprint;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.expressions.prettyprint.AssignmentExpressionsPrettyPrinter;
import de.monticore.expressionsbasis._ast.ASTExpression;
import de.monticore.testassignmentexpressions._ast.ASTPrimaryExpression;
import de.monticore.testassignmentexpressions._parser.TestAssignmentExpressionsParser;
import de.monticore.testassignmentexpressions._visitor.TestAssignmentExpressionsVisitor;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;

/**
 * @author npichler
 */

public class AssignmentExpressionsPrettyPrinterTest {
  
  @BeforeClass
  public static void init() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Before
  public void setUp() {
    Log.getFindings().clear();
  }
  
  static class PrimaryPrettyPrinter extends AssignmentExpressionsPrettyPrinter
      implements TestAssignmentExpressionsVisitor {
    
    private TestAssignmentExpressionsVisitor realThis;
    
    @Override
    public void visit(ASTPrimaryExpression node) {
      getPrinter().print((node.getName()));
    }
    
    public PrimaryPrettyPrinter(IndentPrinter printer) {
      super(printer);
      realThis = this;
    }
    
    @Override
    public TestAssignmentExpressionsVisitor getRealThis() {
      return realThis;
    }
  }
  
  @Test
  public void testIncSuffixExpression() throws IOException {
    TestAssignmentExpressionsParser parser = new TestAssignmentExpressionsParser();
    Optional<ASTExpression> ast = parser.parseExpression(new StringReader("exp++"));
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTExpression assignment = ast.get();
    PrimaryPrettyPrinter printer = new PrimaryPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parseExpression(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(assignment.deepEquals(ast.get()));
  }
  
  @Test
  public void testDecSuffixExpression() throws IOException {
    TestAssignmentExpressionsParser parser = new TestAssignmentExpressionsParser();
    Optional<ASTExpression> ast = parser.parse_StringExpression("exp--");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTExpression assignment = ast.get();
    PrimaryPrettyPrinter printer = new PrimaryPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    
    ast = parser.parse_StringExpression(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    
    assertTrue(assignment.deepEquals(ast.get()));
  }
  
  @Test
  public void testPlusPrefixExpression() throws IOException {
    TestAssignmentExpressionsParser parser = new TestAssignmentExpressionsParser();
    Optional<ASTExpression> ast = parser.parse_StringExpression("+exp");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTExpression assignment = ast.get();
    PrimaryPrettyPrinter printer = new PrimaryPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    
    ast = parser.parse_StringExpression(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    
    assertTrue(assignment.deepEquals(ast.get()));
  }
  
  @Test
  public void testMinusPrefixExpression() throws IOException {
    TestAssignmentExpressionsParser parser = new TestAssignmentExpressionsParser();
    Optional<ASTExpression> ast = parser.parse_StringExpression("-exp");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTExpression assignment = ast.get();
    PrimaryPrettyPrinter printer = new PrimaryPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    
    ast = parser.parse_StringExpression(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    
    assertTrue(assignment.deepEquals(ast.get()));
  }
  
  @Test
  public void testIncPrefixExpression() throws IOException {
    TestAssignmentExpressionsParser parser = new TestAssignmentExpressionsParser();
    Optional<ASTExpression> ast = parser.parse_StringExpression("++exp");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTExpression assignment = ast.get();
    PrimaryPrettyPrinter printer = new PrimaryPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    
    ast = parser.parse_StringExpression(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    
    assertTrue(assignment.deepEquals(ast.get()));
  }
  
  @Test
  public void testDecPrefixExpression() throws IOException {
    TestAssignmentExpressionsParser parser = new TestAssignmentExpressionsParser();
    Optional<ASTExpression> ast = parser.parse_StringExpression("--exp");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTExpression assignment = ast.get();
    PrimaryPrettyPrinter printer = new PrimaryPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    
    ast = parser.parse_StringExpression(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    
    assertTrue(assignment.deepEquals(ast.get()));
  }
  
  @Test
  public void testBinaryXorExpression() throws IOException {
    TestAssignmentExpressionsParser parser = new TestAssignmentExpressionsParser();
    Optional<ASTExpression> ast = parser.parse_StringExpression("a^b");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTExpression assignment = ast.get();
    PrimaryPrettyPrinter printer = new PrimaryPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringExpression(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    
    assertTrue(assignment.deepEquals(ast.get()));
  }
  
  @Test
  public void testBinaryOrOpExpression() throws IOException {
    TestAssignmentExpressionsParser parser = new TestAssignmentExpressionsParser();
    Optional<ASTExpression> ast = parser.parse_StringExpression("a|b");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTExpression assignment = ast.get();
    PrimaryPrettyPrinter printer = new PrimaryPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    
    ast = parser.parse_StringExpression(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    
    assertTrue(assignment.deepEquals(ast.get()));
  }
  
  @Test
  public void testRegularAssignmentExpression() throws IOException {
    TestAssignmentExpressionsParser parser = new TestAssignmentExpressionsParser();
    Optional<ASTExpression> ast = parser.parse_StringExpression("a=b");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTExpression assignment = ast.get();
    PrimaryPrettyPrinter printer = new PrimaryPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    
    ast = parser.parse_StringExpression(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    
    assertTrue(assignment.deepEquals(ast.get()));
  }
  
  @Test
  public void testPlusAssignmentExpression() throws IOException {
    TestAssignmentExpressionsParser parser = new TestAssignmentExpressionsParser();
    Optional<ASTExpression> ast = parser.parse_StringExpression("a+=b");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTExpression assignment = ast.get();
    PrimaryPrettyPrinter printer = new PrimaryPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    
    ast = parser.parse_StringExpression(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    
    assertTrue(assignment.deepEquals(ast.get()));
  }
  
  @Test
  public void testMinusAssignmentExpression() throws IOException {
    TestAssignmentExpressionsParser parser = new TestAssignmentExpressionsParser();
    Optional<ASTExpression> ast = parser.parse_StringExpression("a-=b");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTExpression assignment = ast.get();
    PrimaryPrettyPrinter printer = new PrimaryPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    
    ast = parser.parse_StringExpression(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    
    assertTrue(assignment.deepEquals(ast.get()));
  }
  
  @Test
  public void testMultAssignmentExpression() throws IOException {
    TestAssignmentExpressionsParser parser = new TestAssignmentExpressionsParser();
    Optional<ASTExpression> ast = parser.parse_StringExpression("a*=b");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTExpression assignment = ast.get();
    PrimaryPrettyPrinter printer = new PrimaryPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    
    ast = parser.parse_StringExpression(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    
    assertTrue(assignment.deepEquals(ast.get()));
  }
  
  @Test
  public void testDivideAssignmentExpression() throws IOException {
    TestAssignmentExpressionsParser parser = new TestAssignmentExpressionsParser();
    Optional<ASTExpression> ast = parser.parse_StringExpression("a/=b");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTExpression assignment = ast.get();
    PrimaryPrettyPrinter printer = new PrimaryPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    
    ast = parser.parse_StringExpression(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    
    assertTrue(assignment.deepEquals(ast.get()));
  }
  
  @Test
  public void testAndAssignmentExpression() throws IOException {
    TestAssignmentExpressionsParser parser = new TestAssignmentExpressionsParser();
    Optional<ASTExpression> ast = parser.parse_StringExpression("a&=b");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTExpression assignment = ast.get();
    PrimaryPrettyPrinter printer = new PrimaryPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    
    ast = parser.parse_StringExpression(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    
    assertTrue(assignment.deepEquals(ast.get()));
  }
  
  @Test
  public void testOrAssignmentExpression() throws IOException {
    TestAssignmentExpressionsParser parser = new TestAssignmentExpressionsParser();
    Optional<ASTExpression> ast = parser.parse_StringExpression("a|=b");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTExpression assignment = ast.get();
    PrimaryPrettyPrinter printer = new PrimaryPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    
    ast = parser.parse_StringExpression(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    
    assertTrue(assignment.deepEquals(ast.get()));
  }
  
  @Test
  public void testBinaryAndExpression() throws IOException {
    TestAssignmentExpressionsParser parser = new TestAssignmentExpressionsParser();
    Optional<ASTExpression> ast = parser.parse_StringExpression("a&b");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTExpression assignment = ast.get();
    PrimaryPrettyPrinter printer = new PrimaryPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    
    ast = parser.parse_StringExpression(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    
    assertTrue(assignment.deepEquals(ast.get()));
  }
  
  @Test
  public void testBinaryXorAssignmentExpression() throws IOException {
    TestAssignmentExpressionsParser parser = new TestAssignmentExpressionsParser();
    Optional<ASTExpression> ast = parser.parse_StringExpression("a^=b");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTExpression assignment = ast.get();
    PrimaryPrettyPrinter printer = new PrimaryPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parse_StringExpression(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    
    assertTrue(assignment.deepEquals(ast.get()));
  }
  
  @Test
  public void testRightShiftAssignmentExpression() throws IOException {
    TestAssignmentExpressionsParser parser = new TestAssignmentExpressionsParser();
    Optional<ASTExpression> ast = parser.parse_StringExpression("a>>=b");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTExpression assignment = ast.get();
    PrimaryPrettyPrinter printer = new PrimaryPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    
    ast = parser.parse_StringExpression(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    
    assertTrue(assignment.deepEquals(ast.get()));
  }
  
  @Test
  public void testLogicalRightAssignmentExpression() throws IOException {
    TestAssignmentExpressionsParser parser = new TestAssignmentExpressionsParser();
    Optional<ASTExpression> ast = parser.parse_StringExpression("a>>>=b");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTExpression assignment = ast.get();
    PrimaryPrettyPrinter printer = new PrimaryPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    
    ast = parser.parse_StringExpression(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    
    assertTrue(assignment.deepEquals(ast.get()));
  }
  
  @Test
  public void testLeftShiftAssignmentExpression() throws IOException {
    TestAssignmentExpressionsParser parser = new TestAssignmentExpressionsParser();
    Optional<ASTExpression> ast = parser.parse_StringExpression("a<<=b");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTExpression assignment = ast.get();
    PrimaryPrettyPrinter printer = new PrimaryPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    
    ast = parser.parse_StringExpression(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    
    assertTrue(assignment.deepEquals(ast.get()));
  }
  
  @Test
  public void testModuloAssignmentExpression() throws IOException {
    TestAssignmentExpressionsParser parser = new TestAssignmentExpressionsParser();
    Optional<ASTExpression> ast = parser.parse_StringExpression("a%=b");
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTExpression assignment = ast.get();
    PrimaryPrettyPrinter printer = new PrimaryPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    
    ast = parser.parse_StringExpression(output);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    
    assertTrue(assignment.deepEquals(ast.get()));
  }
  
}
