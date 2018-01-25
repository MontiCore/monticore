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

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.monticore.mcliterals._ast.ASTIntLiteral;
import de.monticore.mcliterals._ast.ASTLiteral;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;

/**
 * @author Martin Schindler
 */
public class IntLiteralsTest {
  
  @BeforeClass
  public static void init() {
    // replace log by a sideffect free variant
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Before
  public void setUp() {
    Log.getFindings().clear();
  }
  
  private void checkIntLiteral(int i, String s) throws IOException {
    ASTLiteral lit = MCLiteralsTestHelper.getInstance().parseLiteral(s);
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
      
      //binary number
      checkIntLiteral(0b0, "0b0");
      checkIntLiteral(0b01010, "0b01010");
      checkIntLiteral(0b00001, "0b00001");
      checkIntLiteral(0b1111, "0b1111");
      
      //underscores 
      checkIntLiteral(0257, "02_57");
      checkIntLiteral(0370744, "0370__744");
      checkIntLiteral(123456, "123_456");
      checkIntLiteral(876543210, "87__65_43210");
      checkIntLiteral(0b010101, "0b010_101");
      checkIntLiteral(0x27af489, "0x27_af_489");
      checkIntLiteral(0X27d48e9, "0X27d4_8e9");
      
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
}
