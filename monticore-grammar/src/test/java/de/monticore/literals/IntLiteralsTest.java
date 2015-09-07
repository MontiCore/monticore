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

import org.antlr.v4.runtime.RecognitionException;
import org.junit.Test;

import de.monticore.literals.literals._ast.ASTIntLiteral;
import de.monticore.literals.literals._ast.ASTLiteral;

/**
 * @author Martin Schindler
 */
public class IntLiteralsTest {
  
  private void checkIntLiteral(int i, String s) throws RecognitionException, IOException {
    ASTLiteral lit = LiteralsTestHelper.getInstance().parseLiteral(s);
    assertTrue(lit instanceof ASTIntLiteral);
    assertEquals(i, ((ASTIntLiteral) lit).getValue());
  }
  
  @Test
  public void testIntLiterals() {
    try {
      // decimal number
      checkIntLiteral(0, "0");
      checkIntLiteral(123, "123");
      checkIntLiteral(10, "10");
      checkIntLiteral(5, "5");
      
      // hexadezimal number
      checkIntLiteral(0x12, "0x12");
      checkIntLiteral(0Xeff, "0Xeff");
      checkIntLiteral(0x34567890, "0x34567890");
      checkIntLiteral(0xabcdef, "0xabcdef");
      checkIntLiteral(0x0, "0x0");
      checkIntLiteral(0xa, "0xa");
      checkIntLiteral(0xC0FFEE, "0xC0FFEE");
      checkIntLiteral(0x005f, "0x005f");
      
      // octal number
      checkIntLiteral(02, "02");
      checkIntLiteral(07, "07");
      checkIntLiteral(00, "00");
      checkIntLiteral(076543210, "076543210");
      checkIntLiteral(00017, "00017");
    }
    catch (Exception e) {
      fail(e.getMessage());
    }
  }
}
