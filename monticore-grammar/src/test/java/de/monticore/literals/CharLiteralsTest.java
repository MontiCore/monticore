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

import de.monticore.literals.literals._ast.ASTCharLiteral;
import de.monticore.literals.literals._ast.ASTLiteral;

/**
 * @author Martin Schindler
 */
public class CharLiteralsTest {
  
  private void checkCharLiteral(char c, String s) throws IOException {
      ASTLiteral lit = LiteralsTestHelper.getInstance().parseLiteral(s);
      assertTrue(lit instanceof ASTCharLiteral);
      assertEquals(c, ((ASTCharLiteral) lit).getValue());
  }
  
  @Test
  public void testCharLiterals() {
    try {
      checkCharLiteral('a', "'a'");
      checkCharLiteral(' ', "' '");
      checkCharLiteral('@', "'@'");
      // checkCharLiteral('§', "'§'");
      
      // Escape Sequences:
      checkCharLiteral('\b', "'\\b'");
      checkCharLiteral('\t', "'\\t'");
      checkCharLiteral('\n', "'\\n'");
      checkCharLiteral('\f', "'\\f'");
      checkCharLiteral('\r', "'\\r'");
      checkCharLiteral('\"', "'\\\"'");
      checkCharLiteral('\'', "'\\\''");
      checkCharLiteral('\\', "'\\\\'");
      
      // Unicode:
      checkCharLiteral('\u00ef', "'\\u00ef'");
      checkCharLiteral('\u0000', "'\\u0000'");
      checkCharLiteral('\uffff', "'\\uffff'");
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
    
  }
}
