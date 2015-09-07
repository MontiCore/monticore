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

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import junit.framework.TestCase;
import de.monticore.literals.literals._ast.ASTLiteral;
import de.monticore.literals.literals._ast.ASTSignedLiteral;
import de.monticore.literals.literals._parser.LiteralMCParser;
import de.monticore.literals.literals._parser.LiteralsParserFactory;
import de.monticore.literals.literals._parser.SignedLiteralMCParser;

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
   * @throws org.antlr.v4.runtime.RecognitionException
   */
  public ASTLiteral parseLiteral(String input) throws org.antlr.v4.runtime.RecognitionException,
      IOException {
    LiteralMCParser parser = LiteralsParserFactory.createLiteralMCParser();
    Optional<ASTLiteral> res = parser.parse(new StringReader(input));
    TestCase.assertTrue(res.isPresent());
    return res.get();
  }
  
  /**
   * This method parses a literal from a given string.
   * 
   * @param input Literal as a string.
   * @return The ASTLiteral or null.
   * @throws IOException
   * @throws org.antlr.v4.runtime.RecognitionException
   */
  public ASTSignedLiteral parseSignedLiteral(String input)
      throws org.antlr.v4.runtime.RecognitionException, IOException {
    SignedLiteralMCParser parser = LiteralsParserFactory.createSignedLiteralMCParser();
    Optional<ASTSignedLiteral> res = parser.parse(new StringReader(input));
    TestCase.assertTrue(res.isPresent());
    return res.get();
  }
  
}
