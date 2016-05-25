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

package mc.feature.expression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.antlr4.MCConcreteParser.ParserExecution;
import de.se_rwth.commons.logging.Log;
import mc.GeneratorIntegrationsTest;
import mc.feature.expression.expression._ast.ASTConstantsExpression;
import mc.feature.expression.expression._ast.ASTExpr;
import mc.feature.expression.expression._parser.ExpressionParser;

public class ExpressionTest extends GeneratorIntegrationsTest {
  
  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }
  
  public Optional<ASTExpr> parse(String input) throws IOException {
    ExpressionParser parser = new ExpressionParser();
    parser.setParserTarget(ParserExecution.EOF);
    Optional<ASTExpr> res = parser.parseExpr(new StringReader(input));
    return res;
  }
  
  @Test
  public void testPlus() {
    try {
      Optional<ASTExpr> res = parse("1+2");
      assertTrue(res.isPresent());
      ASTExpr ast = res.get();
      assertEquals(ASTConstantsExpression.PLUS, ast.getOp());
    }
    catch (Exception e) {
      fail(e.getMessage());
    }
  }
  
  @Test
  public void testLiteral() {
    try {
      Optional<ASTExpr> res = parse("1");
      assertTrue(res.isPresent());
      ASTExpr ast = res.get();
      assertTrue(ast.getNumericLiteral().isPresent());
      assertEquals("1", ast.getNumericLiteral().get());
    }
    catch (Exception e) {
      fail(e.getMessage());
    }
  }
  
  @Test
  public void testStar() {
    try {
      Optional<ASTExpr> res = parse("1*2");
      assertTrue(res.isPresent());
      ASTExpr ast = res.get();
      assertEquals(ASTConstantsExpression.STAR, ast.getOp());
    }
    catch (Exception e) {
      fail(e.getMessage());
    }
  }
  
  @Test
  public void testBracket() {
    try {
      Optional<ASTExpr> res = parse("(1*2)");
      assertTrue(res.isPresent());
      ASTExpr ast = res.get();
      assertTrue(ast.getExpr().isPresent());
    }
    catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testExpr1() {
    try {
      Optional<ASTExpr> res = parse("1*2+3");
      assertTrue(res.isPresent());
      ASTExpr ast = res.get();
      assertEquals(ASTConstantsExpression.PLUS, ast.getOp());
    }
    catch (Exception e) {
      fail(e.getMessage());
    }
  }
  
  @Test
  public void testExpr2() {
    try {
      Optional<ASTExpr> res = parse("1+2*3");
      assertTrue(res.isPresent());
      ASTExpr ast = res.get();
      assertEquals(ASTConstantsExpression.PLUS, ast.getOp());
    }
    catch (Exception e) {
      fail(e.getMessage());
    }
  }
  
  @Test
  public void testExpr3() {
    try {
      Optional<ASTExpr> res = parse("1-2-3");
      assertTrue(res.isPresent());
      ASTExpr ast = res.get();
      assertEquals(ASTConstantsExpression.MINUS, ast.getOp());
    }
    catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testPowerWithRightAssoc() {
    try {
      Optional<ASTExpr> res = parse("2^3^4");
      assertTrue(res.isPresent());
      Optional<ASTExpr> left = res.get().getLeft();
      assertTrue(left.isPresent());
      assertTrue(left.get().getNumericLiteral().isPresent());
    }
    catch (Exception e) {
      fail(e.getMessage());
    }
  }
  
}
