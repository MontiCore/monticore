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

import de.monticore.commonexpressions._ast.ASTArguments;
import de.monticore.commonexpressions._ast.ASTBooleanNotExpression;
import de.monticore.commonexpressions._ast.ASTBracketExpression;
import de.monticore.commonexpressions._ast.ASTInfixExpression;
import de.monticore.commonexpressions._ast.ASTLogicalNotExpression;
import de.monticore.expressions.prettyprint.CommonExpressionsPrettyPrinter;
import de.monticore.expressionsbasis._ast.ASTExpression;
import de.monticore.testcommonexpressions._ast.ASTPrimaryExpression;
import de.monticore.testcommonexpressions._parser.TestCommonExpressionsParser;
import de.monticore.testcommonexpressions._visitor.TestCommonExpressionsVisitor;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;

/**
 * @author npichler
 */

public class CommonExpressionsPrettyPrinterTest extends CommonExpressionsPrettyPrinter
    implements TestCommonExpressionsVisitor {
  
  @BeforeClass
  public static void init() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Before
  public void setUp() {
    Log.getFindings().clear();
  }
  
  public CommonExpressionsPrettyPrinterTest() {
    super();
  }
  
  @Override
  public void visit(ASTPrimaryExpression node) {
    sb.append(node.getName());
  }
  
  @Test
  public void testInfixPrettyPrintAnd() throws IOException {
    TestCommonExpressionsParser parser = new TestCommonExpressionsParser();
    ASTInfixExpression ast = parser.parseString_InfixExpression("a && b").orElse(null);
    assertNotNull(ast);
    CommonExpressionsPrettyPrinterTest printer = new CommonExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("a && b", printer.toString());
  }
  
  @Test
  public void testInfixPrettyPrintOr() throws IOException {
    TestCommonExpressionsParser parser = new TestCommonExpressionsParser();
    ASTInfixExpression ast = parser.parseString_InfixExpression("a || b").orElse(null);
    assertNotNull(ast);
    CommonExpressionsPrettyPrinterTest printer = new CommonExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("a || b", printer.toString());
  }
  
  @Test
  public void testInfixPrettyPrintAndOr() throws IOException {
    TestCommonExpressionsParser parser = new TestCommonExpressionsParser();
    ASTInfixExpression ast = parser.parseString_InfixExpression("a || b && c").orElse(null);
    assertNotNull(ast);
    CommonExpressionsPrettyPrinterTest printer = new CommonExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("a || b && c", printer.toString());
  }
  
  @Test
  public void testInfixPrettyPrintMult() throws IOException {
    TestCommonExpressionsParser parser = new TestCommonExpressionsParser();
    ASTInfixExpression ast = parser.parseString_InfixExpression("a  *  b").orElse(null);
    assertNotNull(ast);
    CommonExpressionsPrettyPrinterTest printer = new CommonExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("a * b", printer.toString());
  }
  
  @Test
  public void testInfixPrettyPrintDivide() throws IOException {
    TestCommonExpressionsParser parser = new TestCommonExpressionsParser();
    ASTInfixExpression ast = parser.parseString_InfixExpression("a / b").orElse(null);
    assertNotNull(ast);
    CommonExpressionsPrettyPrinterTest printer = new CommonExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("a / b", printer.toString());
  }
  
  @Test
  public void testInfixPrettyPrintPlus() throws IOException {
    TestCommonExpressionsParser parser = new TestCommonExpressionsParser();
    ASTInfixExpression ast = parser.parseString_InfixExpression("a + b").orElse(null);
    assertNotNull(ast);
    CommonExpressionsPrettyPrinterTest printer = new CommonExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("a + b", printer.toString());
  }
  
  @Test
  public void testInfixPrettyPrintMinus() throws IOException {
    TestCommonExpressionsParser parser = new TestCommonExpressionsParser();
    ASTInfixExpression ast = parser.parseString_InfixExpression("a - b").orElse(null);
    assertNotNull(ast);
    CommonExpressionsPrettyPrinterTest printer = new CommonExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("a - b", printer.toString());
  }
  
  @Test
  public void testInfixPrettyPrintLessEquals() throws IOException {
    TestCommonExpressionsParser parser = new TestCommonExpressionsParser();
    ASTInfixExpression ast = parser.parseString_InfixExpression("a <= b").orElse(null);
    assertNotNull(ast);
    CommonExpressionsPrettyPrinterTest printer = new CommonExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("a <= b", printer.toString());
  }
  
  @Test
  public void testInfixPrettyPrintGreaterEquals() throws IOException {
    TestCommonExpressionsParser parser = new TestCommonExpressionsParser();
    ASTInfixExpression ast = parser.parseString_InfixExpression("a >= b").orElse(null);
    assertNotNull(ast);
    CommonExpressionsPrettyPrinterTest printer = new CommonExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("a >= b", printer.toString());
  }
  
  @Test
  public void testInfixPrettyPrintGreaterThan() throws IOException {
    TestCommonExpressionsParser parser = new TestCommonExpressionsParser();
    ASTInfixExpression ast = parser.parseString_InfixExpression("a > b").orElse(null);
    assertNotNull(ast);
    CommonExpressionsPrettyPrinterTest printer = new CommonExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("a > b", printer.toString());
  }
  
  @Test
  public void testInfixPrettyPrintLessThan() throws IOException {
    TestCommonExpressionsParser parser = new TestCommonExpressionsParser();
    ASTInfixExpression ast = parser.parseString_InfixExpression("a < b").orElse(null);
    assertNotNull(ast);
    CommonExpressionsPrettyPrinterTest printer = new CommonExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("a < b", printer.toString());
  }
  
  @Test
  public void testInfixPrettyPrintEquals() throws IOException {
    TestCommonExpressionsParser parser = new TestCommonExpressionsParser();
    ASTInfixExpression ast = parser.parseString_InfixExpression("a == b").orElse(null);
    assertNotNull(ast);
    CommonExpressionsPrettyPrinterTest printer = new CommonExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("a == b", printer.toString());
  }
  
  @Test
  public void testInfixPrettyPrintNotEquals() throws IOException {
    TestCommonExpressionsParser parser = new TestCommonExpressionsParser();
    ASTInfixExpression ast = parser.parseString_InfixExpression("a != b").orElse(null);
    assertNotNull(ast);
    CommonExpressionsPrettyPrinterTest printer = new CommonExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("a != b", printer.toString());
  }
  
  @Test
  public void testInfixPrettyPrintSimpleAssignment() throws IOException {
    TestCommonExpressionsParser parser = new TestCommonExpressionsParser();
    ASTInfixExpression ast = parser.parseString_InfixExpression("a += b").orElse(null);
    assertNotNull(ast);
    CommonExpressionsPrettyPrinterTest printer = new CommonExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("a += b", printer.toString());
  }
  
  @Test
  public void testInfixPrettyPrintCalculation() throws IOException {
    TestCommonExpressionsParser parser = new TestCommonExpressionsParser();
    ASTInfixExpression ast = parser.parseString_InfixExpression("a * b - c + d").orElse(null);
    assertNotNull(ast);
    CommonExpressionsPrettyPrinterTest printer = new CommonExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("a * b - c + d", printer.toString());
  }
  
  @Test
  public void testBracketExpression() throws IOException {
    TestCommonExpressionsParser parser = new TestCommonExpressionsParser();
    ASTBracketExpression ast = parser.parseString_BracketExpression("(expression)").orElse(null);
    assertNotNull(ast);
    CommonExpressionsPrettyPrinterTest printer = new CommonExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("(expression)", printer.toString());
  }
  
  @Test
  public void testBooleanNotExpression() throws IOException {
    TestCommonExpressionsParser parser = new TestCommonExpressionsParser();
    ASTBooleanNotExpression ast = parser.parseString_BooleanNotExpression("~expression")
        .orElse(null);
    assertNotNull(ast);
    CommonExpressionsPrettyPrinterTest printer = new CommonExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("~expression", printer.toString());
  }
  
  @Test
  public void testLogicalNotExpression() throws IOException {
    TestCommonExpressionsParser parser = new TestCommonExpressionsParser();
    ASTLogicalNotExpression ast = parser.parseString_LogicalNotExpression("!expression")
        .orElse(null);
    assertNotNull(ast);
    CommonExpressionsPrettyPrinterTest printer = new CommonExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("!expression", printer.toString());
  }
  
  @Test
  public void testArguments() throws IOException {
    TestCommonExpressionsParser parser = new TestCommonExpressionsParser();
    ASTArguments ast = parser.parseString_Arguments("(a,b,c)").orElse(null);
    assertNotNull(ast);
    CommonExpressionsPrettyPrinterTest printer = new CommonExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("(a,b,c)", printer.toString());
  }
  
  @Test
  public void testCallExpression() throws IOException {
    TestCommonExpressionsParser parser = new TestCommonExpressionsParser();
    ASTExpression ast = parser.parseString_Expression("exp(a,b,c)").orElse(null);
    assertNotNull(ast);
    CommonExpressionsPrettyPrinterTest printer = new CommonExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("exp(a,b,c)", printer.toString());
  }
  
}
