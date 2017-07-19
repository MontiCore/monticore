/*
 * ******************************************************************************
 * MontiCore Language Workbench
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

import de.monticore.literals.literals._ast.ASTSignedFloatLiteral;
import de.monticore.literals.literals._ast.ASTSignedLiteral;

/**
 * @author Martin Schindler
 */
public class SignedFloatLiteralsTest {
  
  private void checkFloatLiteral(float f, String s) throws IOException {
    ASTSignedLiteral lit = LiteralsTestHelper.getInstance().parseSignedLiteral(s);
    assertTrue(lit instanceof ASTSignedFloatLiteral);
    assertEquals(f, ((ASTSignedFloatLiteral) lit).getValue(), 0);
  }
  
  @Test
  public void testFloatLiterals() {
    try {
      // decimal number
      checkFloatLiteral(5F, "5F");
      checkFloatLiteral(5F, "5F");
      checkFloatLiteral(-5F, "-5F");
      checkFloatLiteral(-0.0F, "-0.0F");
      checkFloatLiteral(.4F, ".4F");
      checkFloatLiteral(-.4F, "-.4F");
      checkFloatLiteral(0E-3F, "0E-3F");
      checkFloatLiteral(0E-3F, "0E-3F");
      checkFloatLiteral(-2E-3F, "-2E-3F");
      
      // hexadezimal number
      checkFloatLiteral(0x5.p1f, "0x5.p1f");
      checkFloatLiteral(-0x5.p1f, "-0x5.p1f");
      checkFloatLiteral(0x0050AF.CD9p-008f, "0x0050AF.CD9p-008f");
      checkFloatLiteral(-0x0050AF.CD9p-008f, "-0x0050AF.CD9p-008f");
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
}
