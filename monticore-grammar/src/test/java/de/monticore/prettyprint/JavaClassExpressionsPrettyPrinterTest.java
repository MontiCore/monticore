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

import de.monticore.expressions.prettyprint.JavaClassExpressionsPrettyPrinter;
import de.monticore.expressionsbasis._ast.ASTExpression;
import de.monticore.javaclassexpressions._ast.ASTGenericInvocationSuffix;
import de.monticore.javaclassexpressions._ast.ASTSuperSuffix;
import de.monticore.testjavaclassexpressions._ast.ASTELiteral;
import de.monticore.testjavaclassexpressions._ast.ASTEReturnType;
import de.monticore.testjavaclassexpressions._ast.ASTEType;
import de.monticore.testjavaclassexpressions._ast.ASTETypeArguments;
import de.monticore.testjavaclassexpressions._ast.ASTPrimaryExpression;
import de.monticore.testjavaclassexpressions._parser.TestJavaClassExpressionsParser;
import de.monticore.testjavaclassexpressions._visitor.TestJavaClassExpressionsVisitor;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;

/**
 * @author npichler
 */

public class JavaClassExpressionsPrettyPrinterTest{
  
  @BeforeClass
  public static void init() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Before
  public void setUp() {
    Log.getFindings().clear();
  }
  
  static class PrimaryPrettyPrinter extends JavaClassExpressionsPrettyPrinter
      implements TestJavaClassExpressionsVisitor {
    
    private TestJavaClassExpressionsVisitor realThis;
    
    @Override
    public void visit(ASTPrimaryExpression node) {
      getPrinter().print((node.getName()));
    }
    
    public PrimaryPrettyPrinter(IndentPrinter printer) {
      super(printer);
      realThis = this;
    }
    
    @Override
    public TestJavaClassExpressionsVisitor getRealThis() {
      return realThis;
    }
    
    @Override
    public void visit(ASTELiteral node) {
      getPrinter().print(node.getName());
    }
    
    @Override
    public void visit(ASTEType node) {
      if (node.isPresentDouble()) {
        getPrinter().print(node.getDouble());
      }
      if (node.isPresentInt()) {
        getPrinter().print(node.getInt());
      }
      if (node.isPresentLong()) {
        getPrinter().print(node.getLong());
      }
      if (node.isPresentFloat()) {
        getPrinter().print(node.getFloat());
      }
    }
    
    @Override
    public void handle(ASTEReturnType node) {
      if (node.isPresentEType()) {
        node.getEType().accept(this);
      }
      if (node.isPresentVoid()) {
        getPrinter().print(node.getVoid());
      }
    }
    
    @Override
    public void visit(ASTETypeArguments node) {
      getPrinter().print("<");
      for (String s : node.getNameList()) {
        getPrinter().print(s);
      }
      getPrinter().print(">");
    }
  }
  
 
  
  @Test
  public void testPrimarySuperExpression() throws IOException {
    TestJavaClassExpressionsParser parser = new TestJavaClassExpressionsParser();
    Optional<ASTExpression> ast = parser.parseExpression(new StringReader("super"));
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
  public void testSuperSuffixArguments() throws IOException {
    TestJavaClassExpressionsParser parser = new TestJavaClassExpressionsParser();
    Optional<ASTSuperSuffix> ast = parser.parseSuperSuffix(new StringReader("(a,b,c)"));
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTSuperSuffix assignment = ast.get();
    PrimaryPrettyPrinter printer = new PrimaryPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parseSuperSuffix(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(assignment.deepEquals(ast.get()));
  }
  
  @Test
  public void testSuperSuffixETypeArguments() throws IOException {
    TestJavaClassExpressionsParser parser = new TestJavaClassExpressionsParser();
    Optional<ASTSuperSuffix> ast = parser.parseSuperSuffix(new StringReader(".<Arg>Name(a,b,c)"));
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTSuperSuffix assignment = ast.get();
    PrimaryPrettyPrinter printer = new PrimaryPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parseSuperSuffix(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(assignment.deepEquals(ast.get()));
  }
  
  @Test
  public void testSuperExpression() throws IOException {
    TestJavaClassExpressionsParser parser = new TestJavaClassExpressionsParser();
    Optional<ASTExpression> ast = parser.parseExpression(new StringReader("expression.super(a,b,c)"));
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
  public void testLiteralExpression() throws IOException {
    TestJavaClassExpressionsParser parser = new TestJavaClassExpressionsParser();
    Optional<ASTExpression> ast = parser.parseExpression(new StringReader("Ename"));
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
  public void testClassExpression() throws IOException {
    TestJavaClassExpressionsParser parser = new TestJavaClassExpressionsParser();
    Optional<ASTExpression> ast = parser.parseExpression(new StringReader("void.class"));
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
  public void testGenericSuperInvocationSuffix() throws IOException {
    TestJavaClassExpressionsParser parser = new TestJavaClassExpressionsParser();
    Optional<ASTExpression> ast = parser.parseExpression(new StringReader("super(a,b,c)"));
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
  public void testGenericThisInvocationSuffix() throws IOException {
    TestJavaClassExpressionsParser parser = new TestJavaClassExpressionsParser();
    Optional<ASTGenericInvocationSuffix> ast = parser.parseGenericInvocationSuffix(new StringReader("this(a,b,c)"));
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTGenericInvocationSuffix assignment = ast.get();
    PrimaryPrettyPrinter printer = new PrimaryPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    ast = parser.parseGenericInvocationSuffix(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(assignment.deepEquals(ast.get()));
  }
  
  @Test
  public void testGenericNameInvocationSuffix() throws IOException {
    TestJavaClassExpressionsParser parser = new TestJavaClassExpressionsParser();
    Optional<ASTExpression> ast = parser.parseExpression(new StringReader("Name(a,b,c)"));
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
  public void testTypeCastExpression() throws IOException {
    TestJavaClassExpressionsParser parser = new TestJavaClassExpressionsParser();
    Optional<ASTExpression> ast = parser.parseExpression(new StringReader("(int)expression"));
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
  public void testPrimaryGenericInvocationExpression() throws IOException {    
    TestJavaClassExpressionsParser parser = new TestJavaClassExpressionsParser();
    Optional<ASTExpression> ast = parser.parseExpression(new StringReader("<Arg>super(a,b,c)"));
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
  public void testGenericInvocationExpression() throws IOException {
    TestJavaClassExpressionsParser parser = new TestJavaClassExpressionsParser();
    Optional<ASTExpression> ast = parser.parseExpression(new StringReader("exp.<Arg>super(a,b,c)"));
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
  public void testNameExpression() throws IOException {
    TestJavaClassExpressionsParser parser = new TestJavaClassExpressionsParser();
    Optional<ASTExpression> ast = parser.parseExpression(new StringReader("Name"));
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
  public void testInstanceofExpression() throws IOException {
    TestJavaClassExpressionsParser parser = new TestJavaClassExpressionsParser();
    Optional<ASTExpression> ast = parser.parseExpression(new StringReader("a instanceof int"));
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
