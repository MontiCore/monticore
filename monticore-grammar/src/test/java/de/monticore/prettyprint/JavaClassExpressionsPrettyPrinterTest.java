/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
 * Copyright (c) 2017, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.prettyprint;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.expressions.prettyprint.JavaClassExpressionsPrettyPrinter;
import de.monticore.expressionsbasis._ast.ASTExpression;
import de.monticore.javaclassexpressions._ast.ASTArguments;
import de.monticore.javaclassexpressions._ast.ASTClassExpression;
import de.monticore.javaclassexpressions._ast.ASTGenericSuperInvocationSuffix;
import de.monticore.javaclassexpressions._ast.ASTLiteralExpression;
import de.monticore.javaclassexpressions._ast.ASTPrimaryGenericInvocationExpression;
import de.monticore.javaclassexpressions._ast.ASTPrimarySuperExpression;
import de.monticore.javaclassexpressions._ast.ASTSuperSuffix;
import de.monticore.javaclassexpressions._ast.ASTTypeCastExpression;
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

public class JavaClassExpressionsPrettyPrinterTest extends JavaClassExpressionsPrettyPrinter
    implements TestJavaClassExpressionsVisitor {
  
  @BeforeClass
  public static void init() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Before
  public void setUp() {
    Log.getFindings().clear();
  }
  
  public JavaClassExpressionsPrettyPrinterTest() {
    super();
  }
  
  @Override
  public void visit(ASTPrimaryExpression node) {
    sb.append(node.getName());
  }
  
  @Override
  public void visit(ASTELiteral node) {
    sb.append(node.getName());
  }
  
  @Override
  public void visit(ASTEType node) {
    if (node.getDouble().isPresent()) {
      sb.append(node.getDouble().get());
    }
    if (node.getInt().isPresent()) {
      sb.append(node.getInt().get());
    }
    if (node.getLong().isPresent()) {
      sb.append(node.getLong().get());
    }
    if (node.getFloat().isPresent()) {
      sb.append(node.getFloat().get());
    }
  }
  
  @Override
  public void handle(ASTEReturnType node) {
    if (node.getEType().isPresent()) {
      node.getEType().get().accept(this);
    }
    if (node.getVoid().isPresent()) {
      sb.append(node.getVoid().get());
    }
  }
  
  @Override
  public void visit(ASTETypeArguments node) {
    sb.append("<" + node.getName() + ">");
    
  }
  
  @Test
  public void testPrimarySuperExpression() throws IOException {
    TestJavaClassExpressionsParser parser = new TestJavaClassExpressionsParser();
    ASTPrimarySuperExpression ast = parser.parseString_PrimarySuperExpression("super").orElse(null);
    assertNotNull(ast);
    JavaClassExpressionsPrettyPrinterTest printer = new JavaClassExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("super", printer.toString());
  }
  
  @Test
  public void testArguments() throws IOException {
    TestJavaClassExpressionsParser parser = new TestJavaClassExpressionsParser();
    ASTArguments ast = parser.parseString_Arguments("(a,b,c)").orElse(null);
    assertNotNull(ast);
    JavaClassExpressionsPrettyPrinterTest printer = new JavaClassExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("(a,b,c)", printer.toString());
  }
  
  @Test
  public void testSuperSuffixArguments() throws IOException {
    TestJavaClassExpressionsParser parser = new TestJavaClassExpressionsParser();
    ASTSuperSuffix ast = parser.parseString_SuperSuffix("(a,b,c)").orElse(null);
    assertNotNull(ast);
    JavaClassExpressionsPrettyPrinterTest printer = new JavaClassExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("(a,b,c)", printer.toString());
  }
  
  @Test
  public void testSuperSuffixETypeArguments() throws IOException {
    TestJavaClassExpressionsParser parser = new TestJavaClassExpressionsParser();
    ASTSuperSuffix ast = parser.parseString_SuperSuffix(".<Arg>Name(a,b,c)").orElse(null);
    assertNotNull(ast);
    JavaClassExpressionsPrettyPrinterTest printer = new JavaClassExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals(".<Arg>Name(a,b,c)", printer.toString());
  }
  
  @Test
  public void testSuperExpression() throws IOException {
    TestJavaClassExpressionsParser parser = new TestJavaClassExpressionsParser();
    ASTExpression ast = parser.parseString_Expression("expression.super(a,b,c)").orElse(null);
    assertNotNull(ast);
    JavaClassExpressionsPrettyPrinterTest printer = new JavaClassExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("expression.super(a,b,c)", printer.toString());
  }
  
  @Test
  public void testLiteralExpression() throws IOException {
    TestJavaClassExpressionsParser parser = new TestJavaClassExpressionsParser();
    ASTLiteralExpression ast = parser.parseString_LiteralExpression("Ename").orElse(null);
    assertNotNull(ast);
    JavaClassExpressionsPrettyPrinterTest printer = new JavaClassExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("Ename", printer.toString());
  }
  
  @Test
  public void testClassExpression() throws IOException {
    TestJavaClassExpressionsParser parser = new TestJavaClassExpressionsParser();
    ASTClassExpression ast = parser.parseString_ClassExpression("void.class").orElse(null);
    assertNotNull(ast);
    JavaClassExpressionsPrettyPrinterTest printer = new JavaClassExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("void.class", printer.toString());
  }
  
  @Test
  public void testGenericSuperInvocationSuffix() throws IOException {
    TestJavaClassExpressionsParser parser = new TestJavaClassExpressionsParser();
    ASTGenericSuperInvocationSuffix ast = parser
        .parseString_GenericSuperInvocationSuffix("super(a,b,c)").orElse(null);
    assertNotNull(ast);
    JavaClassExpressionsPrettyPrinterTest printer = new JavaClassExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("super(a,b,c)", printer.toString());
  }
  
  @Test
  public void testTypeCastExpression() throws IOException {
    TestJavaClassExpressionsParser parser = new TestJavaClassExpressionsParser();
    ASTTypeCastExpression ast = parser.parseString_TypeCastExpression("(int)expression")
        .orElse(null);
    assertNotNull(ast);
    JavaClassExpressionsPrettyPrinterTest printer = new JavaClassExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("(int)expression", printer.toString());
  }
  
  @Test
  public void testPrimaryGenericInvocationExpression() throws IOException {
    TestJavaClassExpressionsParser parser = new TestJavaClassExpressionsParser();
    ASTPrimaryGenericInvocationExpression ast = parser
        .parseString_PrimaryGenericInvocationExpression("<Arg>super(a,b,c)").orElse(null);
    assertNotNull(ast);
    JavaClassExpressionsPrettyPrinterTest printer = new JavaClassExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("<Arg>super(a,b,c)", printer.toString());
  }
  
  @Test
  public void testGenericInvocationExpression() throws IOException {
    TestJavaClassExpressionsParser parser = new TestJavaClassExpressionsParser();
    ASTExpression ast = parser.parseString_Expression("exp.<Arg>super(a,b,c)").orElse(null);
    assertNotNull(ast);
    JavaClassExpressionsPrettyPrinterTest printer = new JavaClassExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("exp.<Arg>super(a,b,c)", printer.toString());
  }
}
