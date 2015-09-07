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

package de.monticore.literals;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import de.monticore.literals.literals._ast.ASTBooleanLiteral;
import de.monticore.literals.literals._ast.ASTLiteral;
import de.monticore.literals.literals._ast.ASTNullLiteral;

/**
 * @author Martin Schindler
 */
public class NullAndBooleanLiteralsTest {
  
  @Test
  public void testNullLiteral() {
    try {
      ASTLiteral lit = LiteralsTestHelper.getInstance().parseLiteral("null");
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
      ASTLiteral lit = LiteralsTestHelper.getInstance().parseLiteral("true");
      assertTrue(lit instanceof ASTBooleanLiteral);
      assertTrue(((ASTBooleanLiteral) lit).getValue());
      
      // literal "false":
      lit = LiteralsTestHelper.getInstance().parseLiteral("false");
      assertTrue(lit instanceof ASTBooleanLiteral);
      assertFalse(((ASTBooleanLiteral) lit).getValue());
    }
    catch (Exception e) {
      fail(e.getMessage());
    }
  }
}
