/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
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
public class SetExpressionsPrettyPrinterTest extends SetExpressionsPrettyPrinter
    implements TestSetExpressionsVisitor {
  
  @BeforeClass
  public static void init() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Before
  public void setUp() {
    Log.getFindings().clear();
  }
  
  public SetExpressionsPrettyPrinterTest() {
    super();
  }
  
  @Override
  public void visit(ASTPrimaryExpression node) {
    sb.append(node.getName());
  }
  
  @Test
  public void testIsInExpression() throws IOException {
    TestSetExpressionsParser parser = new TestSetExpressionsParser();
    ASTExpression ast = parser.parseString_Expression("a isin b").orElse(null);
    assertNotNull(ast);
    SetExpressionsPrettyPrinterTest printer = new SetExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("a isin b", printer.toString());
  }
  
  @Test
  public void testSetAndExpression() throws IOException {
    TestSetExpressionsParser parser = new TestSetExpressionsParser();
    ASTExpression ast = parser.parseString_Expression("a setand b").orElse(null);
    assertNotNull(ast);
    SetExpressionsPrettyPrinterTest printer = new SetExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("a setand b", printer.toString());
  }
  
  @Test
  public void testUnionExpression() throws IOException {
    TestSetExpressionsParser parser = new TestSetExpressionsParser();
    ASTExpression ast = parser.parseString_Expression("a union b").orElse(null);
    assertNotNull(ast);
    SetExpressionsPrettyPrinterTest printer = new SetExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("a union b", printer.toString());
  }
  
  @Test
  public void testSetInExpression() throws IOException {
    TestSetExpressionsParser parser = new TestSetExpressionsParser();
    ASTExpression ast = parser.parseString_Expression("a in b").orElse(null);
    assertNotNull(ast);
    SetExpressionsPrettyPrinterTest printer = new SetExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("a in b", printer.toString());
  }
  
  @Test
  public void testIntersectionExpression() throws IOException {
    TestSetExpressionsParser parser = new TestSetExpressionsParser();
    ASTExpression ast = parser.parseString_Expression("a intersect b").orElse(null);
    assertNotNull(ast);
    SetExpressionsPrettyPrinterTest printer = new SetExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("a intersect b", printer.toString());
  }
  
  @Test
  public void testSetOrExpression() throws IOException {
    TestSetExpressionsParser parser = new TestSetExpressionsParser();
    ASTExpression ast = parser.parseString_Expression("a setor b").orElse(null);
    assertNotNull(ast);
    SetExpressionsPrettyPrinterTest printer = new SetExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("a setor b", printer.toString());
  }
  
  @Test
  public void testSetXOrExpression() throws IOException {
    TestSetExpressionsParser parser = new TestSetExpressionsParser();
    ASTExpression ast = parser.parseString_Expression("a setxor b").orElse(null);
    assertNotNull(ast);
    SetExpressionsPrettyPrinterTest printer = new SetExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("a setxor b", printer.toString());
  }
}
