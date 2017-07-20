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

import de.monticore.literals.literals._ast.ASTSignedDoubleLiteral;
import de.monticore.literals.literals._ast.ASTSignedLiteral;

/**
 * @author Martin Schindler
 */
public class SignedDoubleLiteralsTest {
  
  private void checkDoubleLiteral(double d, String s) throws IOException {
    ASTSignedLiteral lit = LiteralsTestHelper.getInstance().parseSignedLiteral(s);
    assertTrue(lit instanceof ASTSignedDoubleLiteral);
    assertEquals(d, ((ASTSignedDoubleLiteral) lit).getValue(), 0);
  }
  
  @Test
  public void testDoubleLiterals() {
    try {
      // decimal number
      checkDoubleLiteral(0.0, "0.0");
      checkDoubleLiteral(-0.0, "-0.0");
      checkDoubleLiteral(5d, "5d");
      checkDoubleLiteral(-5d, "-5d");
      checkDoubleLiteral(.4, ".4");
      checkDoubleLiteral(-.4, "-.4");
      checkDoubleLiteral(2E-3, "2E-3");
      checkDoubleLiteral(-2E-3, "-2E-3");
      
      // hexadezimal number
      checkDoubleLiteral(0x5.p1, "0x5.p1");
      checkDoubleLiteral(-0x5.p1, "-0x5.p1");
      checkDoubleLiteral(1e-9d, "1e-9d");
      checkDoubleLiteral(-1e-9d, "-1e-9d");
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
}
