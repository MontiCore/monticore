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

import de.monticore.literals.literals._ast.ASTLiteral;
import de.monticore.literals.literals._ast.ASTStringLiteral;

/**
 * @author Martin Schindler
 */
public class StringLiteralsTest {
  
  private void checkStringLiteral(String expected, String actual) throws IOException {
    ASTLiteral lit = LiteralsTestHelper.getInstance().parseLiteral(actual);
    assertTrue(lit instanceof ASTStringLiteral);
    assertEquals(expected, ((ASTStringLiteral) lit).getValue());
  }
  
  @Test
  public void testStringLiterals() {
    try {
      checkStringLiteral("abc ABC", "\"abc ABC\"");
      checkStringLiteral("a", "\"a\"");
      checkStringLiteral(" ", "\" \"");
      checkStringLiteral(" a ", "\" a \"");
      checkStringLiteral("\n", "\"\\n\"");
      checkStringLiteral("\r", "\"\\r\"");
      checkStringLiteral("", "\"\"");
      checkStringLiteral("\\", "\"\\\\\"");
      checkStringLiteral("\"", "\"\\\"\"");
      checkStringLiteral("!\"§\\%&{([)]=}?´`*+~'#-_.:,;<>|^°@€",
          "\"!\\\"§\\\\%&{([)]=}?´`*+~'#-_.:,;<>|^°@€\"");
      
      // Escape Sequences:
      checkStringLiteral("\b\t\n\f\r\"\'\\", "\"\\b\\t\\n\\f\\r\\\"\\'\\\\\"");
      
      // Unicode:
      checkStringLiteral("\u00ef", "\"\\u00ef\"");
      checkStringLiteral("\u0000", "\"\\u0000\"");
      checkStringLiteral("\uffff", "\"\\uffff\"");
      checkStringLiteral("\u00aaf\u00dd1 123", "\"\\u00aaf\\u00dd1 123\"");
      checkStringLiteral("\u010000", "\"\\u010000\"");
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
}
