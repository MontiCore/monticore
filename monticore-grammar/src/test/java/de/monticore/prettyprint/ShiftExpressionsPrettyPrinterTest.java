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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.expressions.prettyprint.ShiftExpressionsPrettyPrinter;
import de.monticore.expressionsbasis._ast.ASTExpression;
import de.monticore.testshiftexpressions._ast.ASTPrimaryExpression;
import de.monticore.testshiftexpressions._parser.TestShiftExpressionsParser;
import de.monticore.testshiftexpressions._visitor.TestShiftExpressionsVisitor;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;

/**
 * @author npichler
 */

public class ShiftExpressionsPrettyPrinterTest{
  
  @BeforeClass
  public static void init() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Before
  public void setUp() {
    Log.getFindings().clear();
  }
  
  class PrimaryPrettyPrinter extends ShiftExpressionsPrettyPrinter
      implements TestShiftExpressionsVisitor {
    
    private TestShiftExpressionsVisitor realThis;
    
    @Override
    public void visit(ASTPrimaryExpression node) {
      getPrinter().print((node.getName()));
    }
    
    public PrimaryPrettyPrinter(IndentPrinter printer) {
      super(printer);
      realThis = this;
    }
    
    @Override
    public TestShiftExpressionsVisitor getRealThis() {
      return realThis;
    }
  }
  
  @Test
  public void testQualifiedNameExpression() throws IOException {
    TestShiftExpressionsParser parser = new TestShiftExpressionsParser();
    Optional<ASTExpression> ast = parser.parseExpression(new StringReader("exp.Name"));
    assertTrue(ast.isPresent());
    assertFalse(parser.hasErrors());
    ASTExpression assignment = ast.get();
    PrimaryPrettyPrinter printer = new PrimaryPrettyPrinter(new IndentPrinter());
    String output = printer.prettyprint(ast.get());
    System.out.println(output);
    ast = parser.parseExpression(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertTrue(assignment.deepEquals(ast.get()));
  }
  
  @Test
  public void testThisExpression() throws IOException {
    TestShiftExpressionsParser parser = new TestShiftExpressionsParser();
    Optional<ASTExpression> ast = parser.parseExpression(new StringReader("exp.this"));
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
  public void testArrayExpression() throws IOException {
    TestShiftExpressionsParser parser = new TestShiftExpressionsParser();
    Optional<ASTExpression> ast = parser.parseExpression(new StringReader("exp[index]"));
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
  public void testLeftShiftExpression() throws IOException {
    TestShiftExpressionsParser parser = new TestShiftExpressionsParser();
    Optional<ASTExpression> ast = parser.parseExpression(new StringReader("a<<b"));
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
  public void testRightShiftExpression() throws IOException {
    TestShiftExpressionsParser parser = new TestShiftExpressionsParser();
    Optional<ASTExpression> ast = parser.parseExpression(new StringReader("a>>b"));
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
  public void testLogicalRightShiftExpression() throws IOException {
    TestShiftExpressionsParser parser = new TestShiftExpressionsParser();
    Optional<ASTExpression> ast = parser.parseExpression(new StringReader("a>>>b"));
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
  public void testPrimaryThisExpression() throws IOException {
    TestShiftExpressionsParser parser = new TestShiftExpressionsParser();
    Optional<ASTExpression> ast = parser.parseExpression(new StringReader("this"));
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
