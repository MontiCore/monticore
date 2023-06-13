/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.exptojava;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.javaclassexpressions.JavaClassExpressionsMill;
import de.monticore.expressions.javaclassexpressions._ast.*;
import de.monticore.expressions.javaclassexpressions._prettyprint.JavaClassExpressionsFullPrettyPrinter;
import de.monticore.expressions.testjavaclassexpressions.TestJavaClassExpressionsMill;
import de.monticore.expressions.testjavaclassexpressions._parser.TestJavaClassExpressionsParser;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class JavaClassExpressionsJavaPrinterTest {
  
  protected TestJavaClassExpressionsParser parser;
  protected JavaClassExpressionsFullPrettyPrinter javaPrinter;
  
  @Before
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    TestJavaClassExpressionsMill.reset();
    TestJavaClassExpressionsMill.init();
    parser = new TestJavaClassExpressionsParser();
    javaPrinter= new JavaClassExpressionsFullPrettyPrinter(new IndentPrinter());
    javaPrinter.getPrinter().clearBuffer();
  }
  
  @Test
  public void testPrimaryThisExpression() throws IOException {
    Optional<ASTPrimaryThisExpression> result = parser.parse_StringPrimaryThisExpression("this");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTPrimaryThisExpression ast = result.get();
    
    String output = javaPrinter.prettyprint(ast);
    
    result = parser.parse_StringPrimaryThisExpression(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(ast.deepEquals(result.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testPrimarySuperExpression() throws IOException {
    Optional<ASTPrimarySuperExpression> result = parser.parse_StringPrimarySuperExpression("super");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTPrimarySuperExpression ast = result.get();
    
    String output = javaPrinter.prettyprint(ast);
    
    result = parser.parse_StringPrimarySuperExpression(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(ast.deepEquals(result.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testThisExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    ASTThisExpression result = JavaClassExpressionsMill.thisExpressionBuilder()
      .setExpression(a.get())
      .build();
    
    String output = javaPrinter.prettyprint(result);
    
    assertEquals("a.this", output);
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testArrayExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTExpression> b = parser.parse_StringExpression("b");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTArrayExpression result = JavaClassExpressionsMill.arrayExpressionBuilder()
      .setExpression(a.get())
      .setIndexExpression(b.get())
      .build();
    
    String output = javaPrinter.prettyprint(result);
    
    assertEquals("a[b]", output);
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testSuperExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTSuperSuffix> b = parser.parse_StringSuperSuffix("(b)");
    assertFalse(parser.hasErrors());
    assertTrue(a.isPresent());
    assertTrue(b.isPresent());
    ASTSuperExpression result = JavaClassExpressionsMill.superExpressionBuilder()
      .setExpression(a.get())
      .setSuperSuffix(b.get())
      .build();
    
    String output = javaPrinter.prettyprint(result);
    
    assertEquals("a.super(b)", output);
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testGenericInvocationSuffixThis() throws IOException {
    Optional<ASTGenericInvocationSuffix> result = parser.parse_StringGenericInvocationSuffix("this(a)");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTGenericInvocationSuffix ast = result.get();
    
    String output = javaPrinter.prettyprint(ast);
    
    result = parser.parse_StringGenericInvocationSuffix(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(ast.deepEquals(result.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testGenericInvocationSuffixSuper() throws IOException {
    Optional<ASTGenericInvocationSuffix> result = parser.parse_StringGenericInvocationSuffix("super(b)");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTGenericInvocationSuffix ast = result.get();
    
    String output = javaPrinter.prettyprint(ast);
    
    result = parser.parse_StringGenericInvocationSuffix(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(ast.deepEquals(result.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testGenericInvocationSuffixSimple() throws IOException {
    Optional<ASTGenericInvocationSuffix> result = parser.parse_StringGenericInvocationSuffix("a(b)");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTGenericInvocationSuffix ast = result.get();
    
    String output = javaPrinter.prettyprint(ast);
    
    result = parser.parse_StringGenericInvocationSuffix(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(ast.deepEquals(result.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testTypePattern() throws IOException {
    Optional<ASTTypePattern> result = parser.parse_StringTypePattern("String s");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTTypePattern ast = result.get();

    String output = javaPrinter.prettyprint(ast);

    result = parser.parse_StringTypePattern(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));

    assertTrue(Log.getFindings().isEmpty());
  }

}
