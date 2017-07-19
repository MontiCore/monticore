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

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import de.monticore.literals.literals._ast.ASTLiteral;
import de.monticore.literals.literals._ast.ASTSignedLiteral;
import de.monticore.literals.literals._parser.LiteralsParser;
import junit.framework.TestCase;

/**
 * This class provides two methods that allow testing type grammar. The test
 * parses a given input string to an AST. The AST is printed via prettyprint and
 * parsed again. The resulting ASTs are compared. The TypeTestHelper is a
 * singleton.
 * 
 * @author Martin Schindler
 */
public class LiteralsTestHelper {
  
  private static LiteralsTestHelper instance;
  
  /**
   * We have a singleton.
   */
  private LiteralsTestHelper() {
  }
  
  /**
   * Returns the singleton instance.
   * 
   * @return The instance.
   */
  public static LiteralsTestHelper getInstance() {
    if (instance == null) {
      instance = new LiteralsTestHelper();
    }
    return instance;
  }
    
  /**
   * This method parses a literal from a given string.
   * 
   * @param input Literal as a string.
   * @return The ASTLiteral or null.
   * @throws IOException
   */
  public ASTLiteral parseLiteral(String input) throws IOException {
    LiteralsParser parser = new LiteralsParser();
    Optional<ASTLiteral> res = parser.parseLiteral(new StringReader(input));
    TestCase.assertTrue(res.isPresent());
    return res.get();
  }
  
  /**
   * This method parses a literal from a given string.
   * 
   * @param input Literal as a string.
   * @return The ASTLiteral or null.
   * @throws IOException
   */
  public ASTSignedLiteral parseSignedLiteral(String input)
      throws IOException {
    LiteralsParser parser = new LiteralsParser();
    Optional<ASTSignedLiteral> res = parser.parseSignedLiteral(new StringReader(input));
    TestCase.assertTrue(res.isPresent());
    return res.get();
  }
  
}
