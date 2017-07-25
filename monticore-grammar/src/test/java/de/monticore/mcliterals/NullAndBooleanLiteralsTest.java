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

package de.monticore.mcliterals;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import de.monticore.mcliterals._ast.ASTBooleanLiteral;
import de.monticore.mcliterals._ast.ASTLiteral;
import de.monticore.mcliterals._ast.ASTNullLiteral;

/**
 * @author Martin Schindler
 */
public class NullAndBooleanLiteralsTest {
  
  @Test
  public void testNullLiteral() {
    try {
      ASTLiteral lit = MCLiteralsTestHelper.getInstance().parseLiteral("null");
      assertTrue(lit instanceof ASTNullLiteral);
    }
    catch (Exception e) {
      fail(e.getMessage());
    }
  }
  
  @Test
  public void testBooleanLiterals() {
    try {
      // literal "true":
      ASTLiteral lit = MCLiteralsTestHelper.getInstance().parseLiteral("true");
      assertTrue(lit instanceof ASTBooleanLiteral);
      assertTrue(((ASTBooleanLiteral) lit).getValue());
      
      // literal "false":
      lit = MCLiteralsTestHelper.getInstance().parseLiteral("false");
      assertTrue(lit instanceof ASTBooleanLiteral);
      assertFalse(((ASTBooleanLiteral) lit).getValue());
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
}
