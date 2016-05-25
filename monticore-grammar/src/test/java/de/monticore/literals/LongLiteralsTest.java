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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import de.monticore.literals.literals._ast.ASTLiteral;
import de.monticore.literals.literals._ast.ASTLongLiteral;

/**
 * @author Martin Schindler
 */
public class LongLiteralsTest {
  
  private void checkLongLiteral(long l, String s) throws IOException {
    ASTLiteral lit = LiteralsTestHelper.getInstance().parseLiteral(s);
    assertTrue(lit instanceof ASTLongLiteral);
    assertEquals(l, ((ASTLongLiteral) lit).getValue());
    
  }
  
  @Test
  public void testLongLiterals() {
    try {
      // decimal number
      checkLongLiteral(0L, "0L");
      checkLongLiteral(123L, "123L");
      checkLongLiteral(10L, "10L");
      checkLongLiteral(5L, "5L");
      
      // hexadezimal number
      checkLongLiteral(0x12L, "0x12L");
      checkLongLiteral(0XeffL, "0XeffL");
      checkLongLiteral(0x1234567890L, "0x1234567890L");
      checkLongLiteral(0xabcdefL, "0xabcdefL");
      checkLongLiteral(0x0L, "0x0L");
      checkLongLiteral(0xaL, "0xaL");
      checkLongLiteral(0xC0FFEEL, "0xC0FFEEL");
      checkLongLiteral(0x005fL, "0x005fL");
      
      // octal number
      checkLongLiteral(02L, "02L");
      checkLongLiteral(07L, "07L");
      checkLongLiteral(00L, "00L");
      checkLongLiteral(076543210L, "076543210L");
      checkLongLiteral(00017L, "00017L");
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
}
