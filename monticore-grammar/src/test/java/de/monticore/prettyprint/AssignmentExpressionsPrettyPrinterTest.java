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

import de.monticore.assignmentexpressions._ast.ASTDecPrefixExpression;
import de.monticore.assignmentexpressions._ast.ASTIncPrefixExpression;
import de.monticore.assignmentexpressions._ast.ASTMinusPrefixExpression;
import de.monticore.assignmentexpressions._ast.ASTPlusPrefixExpression;
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

public class AssignmentExpressionsPrettyPrinterTest extends AssignmentExpressionsPrettyPrinter
    implements TestAssignmentExpressionsVisitor {
  
  @BeforeClass
  public static void init() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Before
  public void setUp() {
    Log.getFindings().clear();
  }
  
  public AssignmentExpressionsPrettyPrinterTest() {
    super();
  }
  
  @Override
  public void visit(ASTPrimaryExpression node) {
    sb.append(node.getName());
  }
  
  @Test
  public void testIncSuffixExpression() throws IOException {
    TestAssignmentExpressionsParser parser = new TestAssignmentExpressionsParser();
    ASTExpression ast = parser.parseString_Expression("//Commentar\nexp++").orElse(null);
    assertNotNull(ast);
    AssignmentExpressionsPrettyPrinterTest printer = new AssignmentExpressionsPrettyPrinterTest();
    ast.accept(printer);
    System.out.println(printer.toString());
    assertEquals("//Commentar\nexp++", printer.toString());
  }
  
  @Test
  public void testDecSuffixExpression() throws IOException {
    TestAssignmentExpressionsParser parser = new TestAssignmentExpressionsParser();
    ASTExpression ast = parser.parseString_Expression("exp--").orElse(null);
    assertNotNull(ast);
    AssignmentExpressionsPrettyPrinterTest printer = new AssignmentExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("exp--", printer.toString());
  }
  
  @Test
  public void testPlusPrefixExpression() throws IOException {
    TestAssignmentExpressionsParser parser = new TestAssignmentExpressionsParser();
    ASTPlusPrefixExpression ast = parser.parseString_PlusPrefixExpression("+exp").orElse(null);
    assertNotNull(ast);
    AssignmentExpressionsPrettyPrinterTest printer = new AssignmentExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("+exp", printer.toString());
  }
  
  @Test
  public void testMinusPrefixExpression() throws IOException {
    TestAssignmentExpressionsParser parser = new TestAssignmentExpressionsParser();
    ASTMinusPrefixExpression ast = parser.parseString_MinusPrefixExpression("-exp").orElse(null);
    assertNotNull(ast);
    AssignmentExpressionsPrettyPrinterTest printer = new AssignmentExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("-exp", printer.toString());
  }
  
  @Test
  public void testIncPrefixExpression() throws IOException {
    TestAssignmentExpressionsParser parser = new TestAssignmentExpressionsParser();
    ASTIncPrefixExpression ast = parser.parseString_IncPrefixExpression("++exp").orElse(null);
    assertNotNull(ast);
    AssignmentExpressionsPrettyPrinterTest printer = new AssignmentExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("++exp", printer.toString());
  }
  
  @Test
  public void testDecPrefixExpression() throws IOException {
    TestAssignmentExpressionsParser parser = new TestAssignmentExpressionsParser();
    ASTDecPrefixExpression ast = parser.parseString_DecPrefixExpression("--exp").orElse(null);
    assertNotNull(ast);
    AssignmentExpressionsPrettyPrinterTest printer = new AssignmentExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("--exp", printer.toString());
  }
  
  @Test
  public void testBinaryXorExpression() throws IOException {
    TestAssignmentExpressionsParser parser = new TestAssignmentExpressionsParser();
    ASTExpression ast = parser.parseString_Expression("a^b").orElse(null);
    assertNotNull(ast);
    AssignmentExpressionsPrettyPrinterTest printer = new AssignmentExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("a^b", printer.toString());
  }
  
  @Test
  public void testBinaryOrOpExpression() throws IOException {
    TestAssignmentExpressionsParser parser = new TestAssignmentExpressionsParser();
    ASTExpression ast = parser.parseString_Expression("a|b").orElse(null);
    assertNotNull(ast);
    AssignmentExpressionsPrettyPrinterTest printer = new AssignmentExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("a|b", printer.toString());
  }
  
  @Test
  public void testPlusAssignmentExpression() throws IOException {
    TestAssignmentExpressionsParser parser = new TestAssignmentExpressionsParser();
    ASTExpression ast = parser.parseString_Expression("a+=b").orElse(null);
    assertNotNull(ast);
    AssignmentExpressionsPrettyPrinterTest printer = new AssignmentExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("a+=b", printer.toString());
  }
  
  @Test
  public void testMultAssignmentExpression() throws IOException {
    TestAssignmentExpressionsParser parser = new TestAssignmentExpressionsParser();
    ASTExpression ast = parser.parseString_Expression("a*=b").orElse(null);
    assertNotNull(ast);
    AssignmentExpressionsPrettyPrinterTest printer = new AssignmentExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("a*=b", printer.toString());
  }
  
  @Test
  public void testDivideAssignmentExpression() throws IOException {
    TestAssignmentExpressionsParser parser = new TestAssignmentExpressionsParser();
    ASTExpression ast = parser.parseString_Expression("a/=b").orElse(null);
    assertNotNull(ast);
    AssignmentExpressionsPrettyPrinterTest printer = new AssignmentExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("a/=b", printer.toString());
  }
  
  @Test
  public void testAndAssignmentExpression() throws IOException {
    TestAssignmentExpressionsParser parser = new TestAssignmentExpressionsParser();
    ASTExpression ast = parser.parseString_Expression("a&=b").orElse(null);
    assertNotNull(ast);
    AssignmentExpressionsPrettyPrinterTest printer = new AssignmentExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("a&=b", printer.toString());
  }
  
  @Test
  public void testOrAssignmentExpression() throws IOException {
    TestAssignmentExpressionsParser parser = new TestAssignmentExpressionsParser();
    ASTExpression ast = parser.parseString_Expression("a|=b").orElse(null);
    assertNotNull(ast);
    AssignmentExpressionsPrettyPrinterTest printer = new AssignmentExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("a|=b", printer.toString());
  }
  
  @Test
  public void testBinaryAndExpression() throws IOException {
    TestAssignmentExpressionsParser parser = new TestAssignmentExpressionsParser();
    ASTExpression ast = parser.parseString_Expression("a&b").orElse(null);
    assertNotNull(ast);
    AssignmentExpressionsPrettyPrinterTest printer = new AssignmentExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("a&b", printer.toString());
  }
  
  @Test
  public void testBinaryXorAssignmentExpression() throws IOException {
    TestAssignmentExpressionsParser parser = new TestAssignmentExpressionsParser();
    ASTExpression ast = parser.parseString_Expression("a^=b").orElse(null);
    assertNotNull(ast);
    AssignmentExpressionsPrettyPrinterTest printer = new AssignmentExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("a^=b", printer.toString());
  }
  
  @Test
  public void testRightShiftAssignmentExpression() throws IOException {
    TestAssignmentExpressionsParser parser = new TestAssignmentExpressionsParser();
    ASTExpression ast = parser.parseString_Expression("a>>=b").orElse(null);
    assertNotNull(ast);
    AssignmentExpressionsPrettyPrinterTest printer = new AssignmentExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("a>>=b", printer.toString());
  }
  
  @Test
  public void testLogiaclRightShiftExpression() throws IOException {
    TestAssignmentExpressionsParser parser = new TestAssignmentExpressionsParser();
    ASTExpression ast = parser.parseString_Expression("a>>>=b").orElse(null);
    assertNotNull(ast);
    AssignmentExpressionsPrettyPrinterTest printer = new AssignmentExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("a>>>=b", printer.toString());
  }
  
  @Test
  public void testLeftShiftAssignmentExpression() throws IOException {
    TestAssignmentExpressionsParser parser = new TestAssignmentExpressionsParser();
    ASTExpression ast = parser.parseString_Expression("a<<=b").orElse(null);
    assertNotNull(ast);
    AssignmentExpressionsPrettyPrinterTest printer = new AssignmentExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("a<<=b", printer.toString());
  }
  
  @Test
  public void testModuloAssignmentExpression() throws IOException {
    TestAssignmentExpressionsParser parser = new TestAssignmentExpressionsParser();
    ASTExpression ast = parser.parseString_Expression("a%=b").orElse(null);
    assertNotNull(ast);
    AssignmentExpressionsPrettyPrinterTest printer = new AssignmentExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("a%=b", printer.toString());
  }
  
}
