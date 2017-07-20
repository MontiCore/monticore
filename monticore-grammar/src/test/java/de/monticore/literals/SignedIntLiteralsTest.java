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

package de.monticore.literals;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import de.monticore.literals.literals._ast.ASTSignedIntLiteral;
import de.monticore.literals.literals._ast.ASTSignedLiteral;

/**
 * @author Martin Schindler
 */
public class SignedIntLiteralsTest {
  
  private void checkIntLiteral(int i, String s) throws IOException {
    ASTSignedLiteral lit = LiteralsTestHelper.getInstance().parseSignedLiteral(s);
    assertTrue(lit instanceof ASTSignedIntLiteral);
    assertEquals(i, ((ASTSignedIntLiteral) lit).getValue());
  }
  
  @Test
  public void testIntLiterals() {
    try {
      // decimal number
      checkIntLiteral(0, "0");
      checkIntLiteral(0, "-0");
      checkIntLiteral(123, "123");
      checkIntLiteral(-123, "-123");
      
      // hexadezimal number
      checkIntLiteral(0x0, "0x0");
      checkIntLiteral(-0x0, "-0x0");
      checkIntLiteral(0x12, "0x12");
      checkIntLiteral(-0x12, "-0x12");
      
      // octal number
      checkIntLiteral(00, "00");
      checkIntLiteral(00, "-00");
      checkIntLiteral(076543210, "076543210");
      checkIntLiteral(-076543210, "-076543210");
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
}
