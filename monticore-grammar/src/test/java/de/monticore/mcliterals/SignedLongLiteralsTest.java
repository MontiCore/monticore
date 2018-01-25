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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import de.monticore.mcliterals._ast.ASTSignedLiteral;
import de.monticore.mcliterals._ast.ASTSignedLongLiteral;

/**
 * @author Martin Schindler
 */
public class SignedLongLiteralsTest {
  
  private void checkLongLiteral(long l, String s) throws IOException {
    ASTSignedLiteral lit = MCLiteralsTestHelper.getInstance().parseSignedLiteral(s);
    assertTrue(lit instanceof ASTSignedLongLiteral);
    assertEquals(l, ((ASTSignedLongLiteral) lit).getValue());
  }
  
  @Test
  public void testLongLiterals() {
    try {
      // decimal number
      checkLongLiteral(0L, "0L");
      checkLongLiteral(-0L, "-0L");
      checkLongLiteral(123L, "123L");
      checkLongLiteral(-123L, "-123L");
      
      // hexadezimal number
      checkLongLiteral(0x0L, "0x0L");
      checkLongLiteral(-0x0L, "-0x0L");
      checkLongLiteral(0x12L, "0x12L");
      checkLongLiteral(-0x12L, "-0x12L");
      checkLongLiteral(0XeffL, "0XeffL");
      checkLongLiteral(-0XeffL, "-0XeffL");
      
      // octal number
      checkLongLiteral(00L, "00L");
      checkLongLiteral(-00L, "-00L");
      checkLongLiteral(02L, "02L");
      checkLongLiteral(-02L, "-02L");
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
}
