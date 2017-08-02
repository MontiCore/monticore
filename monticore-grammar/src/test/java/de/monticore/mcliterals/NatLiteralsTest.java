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
import java.util.Optional;

import org.junit.Test;

import de.monticore.mcliterals._ast.ASTNatLiteral;
import de.monticore.testmcliterals._parser.TestMCLiteralsParser;

public class NatLiteralsTest {
  
  private void checkNatLiteral(int i, String s) throws IOException {
    TestMCLiteralsParser parser = new TestMCLiteralsParser();
    Optional<ASTNatLiteral> ast = parser.parseString_NatLiteral(s);
    assertTrue(!parser.hasErrors());
    assertEquals(i, ast.get().getValue());
  }
  
  @Test
  public void testDoubleLiterals() {
    try {
      // decimal number
      checkNatLiteral(0, "0");
      checkNatLiteral(123, "123");
      checkNatLiteral(10, "10");
      checkNatLiteral(5, "5");
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
}
