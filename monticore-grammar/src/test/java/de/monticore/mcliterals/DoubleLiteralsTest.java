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

import de.monticore.mcliterals._ast.ASTDoubleLiteral;
import de.monticore.mcliterals._ast.ASTLiteral;

/**
 * @author Martin Schindler
 */
public class DoubleLiteralsTest {
  
  private void checkDoubleLiteral(double d, String s) throws IOException {
    ASTLiteral lit = MCLiteralsTestHelper.getInstance().parseLiteral(s);
    assertTrue(lit instanceof ASTDoubleLiteral);
    assertEquals(d, ((ASTDoubleLiteral) lit).getValue(), 0);
  }
  
  @Test
  public void testDoubleLiterals() {
    try {
      // decimal number
      checkDoubleLiteral(0.0, "0.0");
      checkDoubleLiteral(.0, ".0");
      checkDoubleLiteral(0., "0.");
      checkDoubleLiteral(5d, "5d");
      checkDoubleLiteral(.4, ".4");
      checkDoubleLiteral(000009.3, "000009.3");
      checkDoubleLiteral(5., "5.");
      checkDoubleLiteral(009., "009.");
      checkDoubleLiteral(009e2, "009e2");
      checkDoubleLiteral(23.4, "23.4");
      checkDoubleLiteral(2e3, "2e3");
      checkDoubleLiteral(2E-3, "2E-3");
      checkDoubleLiteral(009d, "009d");
      checkDoubleLiteral(.1e1, ".1e1");
      checkDoubleLiteral(.1, ".1");
      checkDoubleLiteral(.11e12, ".11e12");
      checkDoubleLiteral(.11e+12, ".11e+12");
      checkDoubleLiteral(29.18e08, "29.18e08");
      checkDoubleLiteral(0029.0008e-00008, "0029.0008e-00008");
      checkDoubleLiteral(0029.0008e-00008D, "0029.0008e-00008D");
      
      // hexadezimal number
      checkDoubleLiteral(0x5.p1, "0x5.p1");
      checkDoubleLiteral(0x.5p1, "0x.5p1");
      checkDoubleLiteral(0xFp-9, "0xFp-9");
      checkDoubleLiteral(0xfP2, "0xfP2");
      checkDoubleLiteral(0xfp1, "0xfp1");
      checkDoubleLiteral(0x.fP1, "0x.fP1");
      checkDoubleLiteral(0x0p0, "0x0p0");
      checkDoubleLiteral(0x0.0p1, "0x0.0p1");
      checkDoubleLiteral(0x.0p1, "0x.0p1");
      checkDoubleLiteral(0x.5AFp1, "0x.5AFp1");
      checkDoubleLiteral(0x0050AF.CD9p-008, "0x0050AF.CD9p-008");
      checkDoubleLiteral(0x0050AF.CD9p-008d, "0x0050AF.CD9p-008d");
      checkDoubleLiteral(0x0p-5, "0x0p-5");
      checkDoubleLiteral(0x0p1, "0x0p1");
      checkDoubleLiteral(0x0p1d, "0x0p1d");
      
      // Examples from Java Language Specification
      checkDoubleLiteral(.3, ".3");
      checkDoubleLiteral(1e1, "1e1");
      checkDoubleLiteral(2., "2.");
      checkDoubleLiteral(0.0, "0.0");
      checkDoubleLiteral(3.14, "3.14");
      checkDoubleLiteral(1e-9d, "1e-9d");
      checkDoubleLiteral(1e137, "1e137");
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
}
