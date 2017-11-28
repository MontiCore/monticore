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

import de.monticore.expressions.prettyprint.ShiftExpressionsPrettyPrinter;
import de.monticore.expressionsbasis._ast.ASTExpression;
import de.monticore.shiftexpressions._ast.ASTShiftExpression;
import de.monticore.testshiftexpressions._ast.ASTPrimaryExpression;
import de.monticore.testshiftexpressions._parser.TestShiftExpressionsParser;
import de.monticore.testshiftexpressions._visitor.TestShiftExpressionsVisitor;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;

/**
 * @author npichler
 */

public class ShiftExpressionsPrettyPrinterTest extends ShiftExpressionsPrettyPrinter
    implements TestShiftExpressionsVisitor {
  
  @BeforeClass
  public static void init() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Before
  public void setUp() {
    Log.getFindings().clear();
  }
  
  public ShiftExpressionsPrettyPrinterTest() {
    super();
  }
  
  @Override
  public void visit(ASTPrimaryExpression node) {
    sb.append(node.getName());
  }
  
  @Test
  public void testQualifiedNameExpression() throws IOException {
    TestShiftExpressionsParser parser = new TestShiftExpressionsParser();
    ASTExpression ast = parser.parseString_Expression("exp.Name").orElse(null);
    assertNotNull(ast);
    ShiftExpressionsPrettyPrinterTest printer = new ShiftExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("exp.Name", printer.toString());
  }
  
  @Test
  public void testThisExpression() throws IOException {
    TestShiftExpressionsParser parser = new TestShiftExpressionsParser();
    ASTExpression ast = parser.parseString_Expression("exp.this").orElse(null);
    assertNotNull(ast);
    ShiftExpressionsPrettyPrinterTest printer = new ShiftExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("exp.this", printer.toString());
  }
  
  @Test
  public void testArrayExpression() throws IOException {
    TestShiftExpressionsParser parser = new TestShiftExpressionsParser();
    ASTExpression ast = parser.parseString_Expression("exp[index]").orElse(null);
    assertNotNull(ast);
    ShiftExpressionsPrettyPrinterTest printer = new ShiftExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("exp[index]", printer.toString());
  }
  
  @Test
  public void testLeftShiftExpression() throws IOException {
    TestShiftExpressionsParser parser = new TestShiftExpressionsParser();
    ASTShiftExpression ast = parser.parseString_ShiftExpression("a<<b").orElse(null);
    assertNotNull(ast);
    ShiftExpressionsPrettyPrinterTest printer = new ShiftExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("a<<b", printer.toString());
  }
  
  @Test
  public void testRightShiftExpression() throws IOException {
    TestShiftExpressionsParser parser = new TestShiftExpressionsParser();
    ASTShiftExpression ast = parser.parseString_ShiftExpression("a>>b").orElse(null);
    assertNotNull(ast);
    ShiftExpressionsPrettyPrinterTest printer = new ShiftExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("a>>b", printer.toString());
  }
  
  @Test
  public void testLogiaclRightShiftExpression() throws IOException {
    TestShiftExpressionsParser parser = new TestShiftExpressionsParser();
    ASTShiftExpression ast = parser.parseString_ShiftExpression("a>>>b").orElse(null);
    assertNotNull(ast);
    ShiftExpressionsPrettyPrinterTest printer = new ShiftExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("a>>>b", printer.toString());
  }
  
  @Test
  public void testPrimaryThisExpression() throws IOException {
    TestShiftExpressionsParser parser = new TestShiftExpressionsParser();
    ASTExpression ast = parser.parseString_Expression("this").orElse(null);
    assertNotNull(ast);
    ShiftExpressionsPrettyPrinter printer = new ShiftExpressionsPrettyPrinter();
    ast.accept(printer);
    assertEquals("this", printer.toString());
  }
  
}
