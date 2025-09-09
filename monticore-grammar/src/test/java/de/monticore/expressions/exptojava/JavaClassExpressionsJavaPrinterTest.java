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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

public class JavaClassExpressionsJavaPrinterTest {
  
  protected TestJavaClassExpressionsParser parser;
  protected JavaClassExpressionsFullPrettyPrinter javaPrinter;
  
  @BeforeEach
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
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTPrimaryThisExpression ast = result.get();
    
    String output = javaPrinter.prettyprint(ast);
    
    result = parser.parse_StringPrimaryThisExpression(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    
    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testPrimarySuperExpression() throws IOException {
    Optional<ASTPrimarySuperExpression> result = parser.parse_StringPrimarySuperExpression("super");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTPrimarySuperExpression ast = result.get();
    
    String output = javaPrinter.prettyprint(ast);
    
    result = parser.parse_StringPrimarySuperExpression(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    
    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testThisExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(a.isPresent());
    ASTThisExpression result = JavaClassExpressionsMill.thisExpressionBuilder()
      .setExpression(a.get())
      .build();
    
    String output = javaPrinter.prettyprint(result);
    
    Assertions.assertEquals("a.this", output);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testSuperExpression() throws IOException {
    Optional<ASTExpression> a = parser.parse_StringExpression("a");
    Optional<ASTSuperSuffix> b = parser.parse_StringSuperSuffix("(b)");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(a.isPresent());
    Assertions.assertTrue(b.isPresent());
    ASTSuperExpression result = JavaClassExpressionsMill.superExpressionBuilder()
      .setExpression(a.get())
      .setSuperSuffix(b.get())
      .build();
    
    String output = javaPrinter.prettyprint(result);
    
    Assertions.assertEquals("a.super(b)", output);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testGenericInvocationSuffixThis() throws IOException {
    Optional<ASTGenericInvocationSuffix> result = parser.parse_StringGenericInvocationSuffix("this(a)");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTGenericInvocationSuffix ast = result.get();
    
    String output = javaPrinter.prettyprint(ast);
    
    result = parser.parse_StringGenericInvocationSuffix(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    
    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testGenericInvocationSuffixSuper() throws IOException {
    Optional<ASTGenericInvocationSuffix> result = parser.parse_StringGenericInvocationSuffix("super(b)");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTGenericInvocationSuffix ast = result.get();
    
    String output = javaPrinter.prettyprint(ast);
    
    result = parser.parse_StringGenericInvocationSuffix(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    
    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testGenericInvocationSuffixSimple() throws IOException {
    Optional<ASTGenericInvocationSuffix> result = parser.parse_StringGenericInvocationSuffix("a(b)");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTGenericInvocationSuffix ast = result.get();
    
    String output = javaPrinter.prettyprint(ast);
    
    result = parser.parse_StringGenericInvocationSuffix(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    
    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testTypePattern() throws IOException {
    Optional<ASTTypePattern> result = parser.parse_StringTypePattern("String s");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTTypePattern ast = result.get();

    String output = javaPrinter.prettyprint(ast);

    result = parser.parse_StringTypePattern(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));

    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

}
