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

import de.monticore.expressions.prettyprint.OCLLogicExpressionsPrettyPrinter;
import de.monticore.expressionsbasis._ast.ASTExpression;
import de.monticore.testocllogicexpressions._ast.ASTOCLCollectionVarDeclaration;
import de.monticore.testocllogicexpressions._ast.ASTOCLDeclaration;
import de.monticore.testocllogicexpressions._ast.ASTOCLNestedContainer;
import de.monticore.testocllogicexpressions._ast.ASTPrimaryExpression;
import de.monticore.testocllogicexpressions._parser.TestOCLLogicExpressionsParser;
import de.monticore.testocllogicexpressions._visitor.TestOCLLogicExpressionsVisitor;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;

/**
 * @author npichler
 */
public class OCLLogicExpressionsPrettyPrinterTest extends OCLLogicExpressionsPrettyPrinter
    implements TestOCLLogicExpressionsVisitor {
  
  @BeforeClass
  public static void init() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Before
  public void setUp() {
    Log.getFindings().clear();
  }
  
  public OCLLogicExpressionsPrettyPrinterTest() {
    super();
  }
  
  @Override
  public void visit(ASTPrimaryExpression node) {
    sb.append(node.getName());
  }
  
  @Override
  public void visit(ASTOCLCollectionVarDeclaration node) {
    sb.append(node.getVarName() + " in " + node.getCollection());
  }
  
  @Override
  public void visit(ASTOCLNestedContainer node) {
    sb.append(node.getVarName() + " in List <" + node.getImput() + ">");
  }
  
  @Override
  public void visit(ASTOCLDeclaration node) {
    if (node.getPublic().isPresent()) {
      sb.append(node.getPublic().get() + " ");
    }
    if (node.getPrivate().isPresent()) {
      sb.append(node.getPrivate().get() + " ");
    }
    sb.append(node.getType() + " " + node.getVarName());
  }
  
  @Test
  public void testImpliesExpression() throws IOException {
    TestOCLLogicExpressionsParser parser = new TestOCLLogicExpressionsParser();
    ASTExpression ast = parser.parseString_Expression("a implies b").orElse(null);
    assertNotNull(ast);
    OCLLogicExpressionsPrettyPrinterTest printer = new OCLLogicExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("a implies b", printer.toString());
  }
  
  @Test
  public void testSingleLogicalORExpr() throws IOException {
    TestOCLLogicExpressionsParser parser = new TestOCLLogicExpressionsParser();
    ASTExpression ast = parser.parseString_Expression("a | b").orElse(null);
    assertNotNull(ast);
    OCLLogicExpressionsPrettyPrinterTest printer = new OCLLogicExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("a | b", printer.toString());
  }
  
  @Test
  public void testForallExpr1() throws IOException {
    TestOCLLogicExpressionsParser parser = new TestOCLLogicExpressionsParser();
    ASTExpression ast = parser.parseString_Expression("forall : exp").orElse(null);
    assertNotNull(ast);
    OCLLogicExpressionsPrettyPrinterTest printer = new OCLLogicExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("forall :exp", printer.toString());
  }
  
  @Test
  public void testForallExpr2() throws IOException {
    TestOCLLogicExpressionsParser parser = new TestOCLLogicExpressionsParser();
    ASTExpression ast = parser.parseString_Expression("forall a in A: exp").orElse(null);
    assertNotNull(ast);
    OCLLogicExpressionsPrettyPrinterTest printer = new OCLLogicExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("forall a in A:exp", printer.toString());
  }
  
  @Test
  public void testForallExpr3() throws IOException {
    TestOCLLogicExpressionsParser parser = new TestOCLLogicExpressionsParser();
    ASTExpression ast = parser.parseString_Expression("forall a in List <Abc>: exp").orElse(null);
    assertNotNull(ast);
    OCLLogicExpressionsPrettyPrinterTest printer = new OCLLogicExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("forall a in List <Abc>:exp", printer.toString());
  }
  
  @Test
  public void testExistsExpr1() throws IOException {
    TestOCLLogicExpressionsParser parser = new TestOCLLogicExpressionsParser();
    ASTExpression ast = parser.parseString_Expression("exists : exp").orElse(null);
    assertNotNull(ast);
    OCLLogicExpressionsPrettyPrinterTest printer = new OCLLogicExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("exists :exp", printer.toString());
  }
  
  @Test
  public void testExistsExpr2() throws IOException {
    TestOCLLogicExpressionsParser parser = new TestOCLLogicExpressionsParser();
    ASTExpression ast = parser.parseString_Expression("exists a in A: exp").orElse(null);
    assertNotNull(ast);
    OCLLogicExpressionsPrettyPrinterTest printer = new OCLLogicExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("exists a in A:exp", printer.toString());
  }
  
  @Test
  public void testExistsExpr3() throws IOException {
    TestOCLLogicExpressionsParser parser = new TestOCLLogicExpressionsParser();
    ASTExpression ast = parser.parseString_Expression("exists a in List <Abc>: exp").orElse(null);
    assertNotNull(ast);
    OCLLogicExpressionsPrettyPrinterTest printer = new OCLLogicExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("exists a in List <Abc>:exp", printer.toString());
  }
  
  @Test
  public void testAnyExpr() throws IOException {
    TestOCLLogicExpressionsParser parser = new TestOCLLogicExpressionsParser();
    ASTExpression ast = parser.parseString_Expression("any exp").orElse(null);
    assertNotNull(ast);
    OCLLogicExpressionsPrettyPrinterTest printer = new OCLLogicExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("any exp", printer.toString());
  }
  
  @Test
  public void testLetinExpr1() throws IOException {
    TestOCLLogicExpressionsParser parser = new TestOCLLogicExpressionsParser();
    ASTExpression ast = parser.parseString_Expression("let public Int A; private Double B; in exp")
        .orElse(null);
    assertNotNull(ast);
    OCLLogicExpressionsPrettyPrinterTest printer = new OCLLogicExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("let public Int A; private Double B; in exp", printer.toString());
  }
  
  @Test
  public void testLetDeclaration() throws IOException {
    TestOCLLogicExpressionsParser parser = new TestOCLLogicExpressionsParser();
    ASTExpression ast = parser.parseString_Expression("let public Int A; private Double B;")
        .orElse(null);
    assertNotNull(ast);
    OCLLogicExpressionsPrettyPrinterTest printer = new OCLLogicExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("let public Int A;private Double B;", printer.toString());
  }
  
  @Test
  public void testIterateExpr() throws IOException {
    TestOCLLogicExpressionsParser parser = new TestOCLLogicExpressionsParser();
    ASTExpression ast = parser
        .parseString_Expression("iterate { B in A; public Int A : Name = Value }").orElse(null);
    assertNotNull(ast);
    OCLLogicExpressionsPrettyPrinterTest printer = new OCLLogicExpressionsPrettyPrinterTest();
    ast.accept(printer);
    assertEquals("iterate { B in A; public Int A : Name = Value }", printer.toString());
  }
  
}
