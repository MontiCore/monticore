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

import de.monticore.expressions.prettyprint.SetExpressionsPrettyPrinter;
import de.monticore.expressionsbasis._ast.ASTExpression;
import de.monticore.testsetexpressions._ast.ASTPrimaryExpression;
import de.monticore.testsetexpressions._parser.TestSetExpressionsParser;
import de.monticore.testsetexpressions._visitor.TestSetExpressionsVisitor;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;

/**
 * @author npichler
 */
public class SetExpressionsPrettyPrinterTest {
  
  @BeforeClass
  public static void init() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Before
  public void setUp() {
    Log.getFindings().clear();
  }
  
  static class PrimaryPrettyPrinter extends SetExpressionsPrettyPrinter
      implements TestSetExpressionsVisitor {
    
  private TestSetExpressionsVisitor realThis;

  @Override
  public void visit(ASTPrimaryExpression node) {
  getPrinter().print((node.getName()));
  }

  public PrimaryPrettyPrinter(IndentPrinter printer) {
    super(printer);
    realThis = this;
  }

  @Override
  public TestSetExpressionsVisitor getRealThis() {
  return realThis;
  }
  }
  
  
  
  @Test
  public void testIsInExpression() throws IOException {
    TestSetExpressionsParser parser = new TestSetExpressionsParser();
    Optional<ASTExpression> ast = parser.parseExpression(new StringReader("a in b"));
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
  public void testSetAndExpression() throws IOException {
    TestSetExpressionsParser parser = new TestSetExpressionsParser();
    Optional<ASTExpression> ast = parser.parseExpression(new StringReader("setand b"));
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
  public void testUnionExpression() throws IOException {
    TestSetExpressionsParser parser = new TestSetExpressionsParser();
    Optional<ASTExpression> ast = parser.parseExpression(new StringReader("a union b"));
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
  public void testSetInExpression() throws IOException {
    TestSetExpressionsParser parser = new TestSetExpressionsParser();
    Optional<ASTExpression> ast = parser.parseExpression(new StringReader("a in b"));
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
  public void testIntersectionExpression() throws IOException {
    TestSetExpressionsParser parser = new TestSetExpressionsParser();
    Optional<ASTExpression> ast = parser.parseExpression(new StringReader("a intersect b"));
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
  public void testSetOrExpression() throws IOException {
    TestSetExpressionsParser parser = new TestSetExpressionsParser();
    Optional<ASTExpression> ast = parser.parseExpression(new StringReader("setor b"));
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
  public void testSetXOrExpression() throws IOException {
    TestSetExpressionsParser parser = new TestSetExpressionsParser();
    Optional<ASTExpression> ast = parser.parseExpression(new StringReader("setxor b"));
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
}
