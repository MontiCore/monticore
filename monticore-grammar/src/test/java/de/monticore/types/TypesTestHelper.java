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

package de.monticore.types;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import org.antlr.runtime.RecognitionException;

import de.monticore.antlr4.MCConcreteParser.ParserExecution;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.TypesPrettyPrinterConcreteVisitor;
import de.monticore.types.types._ast.ASTPrimitiveType;
import de.monticore.types.types._ast.ASTReturnType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.types.types._ast.ASTTypeParameters;
import de.monticore.types.types._parser.TypesParser;
import junit.framework.TestCase;

/**
 * This class provides two methods that allow testing type grammar. The test
 * parses a given input string to an AST. The AST is printed via prettyprint and
 * parsed again. The resulting ASTs are compared. The TypeTestHelper is a
 * singleton.
 * 
 * @author Martin Schindler
 */
public class TypesTestHelper {
  
  private static TypesTestHelper instance;
  
  /**
   * We have a singleton.
   */
  private TypesTestHelper() {
  }
  
  /**
   * Returns the singleton instance.
   * 
   * @return The instance.
   */
  public static TypesTestHelper getInstance() {
    if (instance == null) {
      instance = new TypesTestHelper();
    }
    return instance;
  }
  
  
  /**
   * This method parses the given input string (a type) and calls prettyprint on
   * the given AST. The resulting output string is parsed too and the inputAST
   * and outputAST are compared with each other via deepEquals.
   * 
   * @param input The input type as a string.
   * @throws IOException
   * @throws RecognitionException This exception is thrown if something could
   * not be parsed.
   */
  public boolean testType(String input) throws RecognitionException, IOException {
    // Parse input
    ASTType inputAST = parseType(input);
    // Print the inputAST
    TypesPrettyPrinterConcreteVisitor printer = new TypesPrettyPrinterConcreteVisitor(new IndentPrinter());
    String output = printer.prettyprint(inputAST);
    // Parse output
    ASTType outputAST = parseType(output);
    // Compare both AST
    if (!inputAST.deepEquals(outputAST)) {
      return false;
    }
    return true;
  }
  
  /**
   * This method parses the given input string (a typeparameter) and calls
   * prettyprint on the given AST. The resulting output string is parsed too and
   * the inputAST and outputAST are compared with each other via deepEquals.
   * 
   * @param input The input type as a string.
   * @throws IOException
   * @throws RecognitionException This exception is thrown if something could
   * not be parsed.
   */
  public boolean testTypeParameter(String input) throws RecognitionException, IOException {
    // Parse input
    ASTTypeParameters inputAST = parseTypeParameters(input);
    // Print the inputAST
    TypesPrettyPrinterConcreteVisitor printer = new TypesPrettyPrinterConcreteVisitor(new IndentPrinter());
    String output = printer.prettyprint(inputAST);
    // Parse output
    ASTTypeParameters outputAST = parseTypeParameters(output);
    // Compare both AST
    if (!inputAST.deepEquals(outputAST)) {
      return false;
    }
    return true;
  }
  
  /**
   * This method parses a type from a given string.
   * 
   * @param input Type as a string.
   * @return The ASTType or null.
   * @throws RecognitionException This exception is thrown if something could
   * not be parsed.
   * @throws IOException
   */
  public ASTType parseType(String input) throws RecognitionException, IOException {
    TypesParser parser = new TypesParser();
    parser.setParserTarget(ParserExecution.EOF);
    Optional<ASTType> res = parser.parseType(new StringReader(input));
    if (parser.hasErrors()) {
      throw new RecognitionException();
    }
    TestCase.assertTrue(res.isPresent());
    TestCase.assertTrue(res.get() instanceof ASTType);
    return (ASTType) res.get();
  }
  
  /**
   * This method parses a return type from a given string.
   * 
   * @param input Return type as a string.
   * @return The ASTReturnType or null.
   * @throws IOException
   * @throws RecognitionException This exception is thrown if something could
   * not be parsed.
   */
  public ASTReturnType parseReturnType(String input)
      throws org.antlr.v4.runtime.RecognitionException, IOException {
    TypesParser parser = new TypesParser();
    Optional<ASTReturnType> res = parser.parseReturnType(new StringReader(input));
    TestCase.assertTrue(res.isPresent());
    TestCase.assertTrue(res.get() instanceof ASTReturnType);
    return (ASTReturnType) res.get();
  }
  
  /**
   * This method parses a type parameter from a given string.
   * 
   * @param input Type parameter as a string.
   * @return The ASTType or null.
   * @throws IOException
   * @throws RecognitionException This exception is thrown if something could
   * not be parsed.
   */
  public ASTTypeParameters parseTypeParameters(String input) throws RecognitionException,
      IOException {
    TypesParser parser = new TypesParser();
    Optional<ASTTypeParameters> res = parser.parseTypeParameters(new StringReader(input));
    if (parser.hasErrors()) {
      throw new RecognitionException();
    }
    TestCase.assertTrue(res.isPresent());
    TestCase.assertTrue(res.get() instanceof ASTTypeParameters);
    return (ASTTypeParameters) res.get();
  }
  
  /**
   * This method parses a boolean primitive type from a given string.
   * 
   * @param input Boolean primitive type as a string.
   * @return The ASTPrimitiveType or null.
   * @throws IOException
   * @throws RecognitionException This exception is thrown if something could
   * not be parsed.
   */
  public ASTPrimitiveType parseBooleanType(String input)
      throws org.antlr.v4.runtime.RecognitionException, IOException {
    TypesParser parser = new TypesParser();
    Optional<ASTPrimitiveType> res = parser.parsePrimitiveType(new StringReader(input));
    TestCase.assertTrue(res.isPresent());
    TestCase.assertTrue(res.get() instanceof ASTPrimitiveType);
    return (ASTPrimitiveType) res.get();
  }
  
  /**
   * This method parses an integral primitive type from a given string.
   * 
   * @param input Integral primitive type as a string.
   * @return The ASTPrimitiveType or null.
   * @throws IOException
   * @throws RecognitionException This exception is thrown if something could
   * not be parsed.
   */
  public ASTPrimitiveType parseIntegralType(String input)
      throws org.antlr.v4.runtime.RecognitionException, IOException {
    TypesParser parser = new TypesParser();
    Optional<ASTPrimitiveType> res = parser.parsePrimitiveType(new StringReader(input));
    TestCase.assertTrue(res.isPresent());
    TestCase.assertTrue(res.get() instanceof ASTPrimitiveType);
    return (ASTPrimitiveType) res.get();
  }
  
  /**
   * This method parses a floating point primitive type from a given string.
   * 
   * @param input Floating point primitive type as a string.
   * @return The ASTPrimitiveType or null.
   * @throws IOException
   * @throws RecognitionException This exception is thrown if something could
   * not be parsed.
   */
  public ASTPrimitiveType parseFloatingPointType(String input)
      throws org.antlr.v4.runtime.RecognitionException, IOException {
    TypesParser parser = new TypesParser();
    Optional<ASTPrimitiveType> res = parser.parsePrimitiveType(new StringReader(input));
    TestCase.assertTrue(res.isPresent());
    TestCase.assertTrue(res.get() instanceof ASTPrimitiveType);
    return (ASTPrimitiveType) res.get();
  }
  
  /**
   * This method parses a numeric primitive type from a given string.
   * 
   * @param input Numeric primitive type as a string.
   * @return The ASTPrimitiveType or null.
   * @throws IOException
   * @throws RecognitionException This exception is thrown if something could
   * not be parsed.
   */
  public ASTPrimitiveType parseNumericType(String input)
      throws org.antlr.v4.runtime.RecognitionException, IOException {
    TypesParser parser = new TypesParser();
    Optional<ASTPrimitiveType> res = parser.parsePrimitiveType(new StringReader(input));
    TestCase.assertTrue(res.isPresent());
    TestCase.assertTrue(res.get() instanceof ASTPrimitiveType);
    return (ASTPrimitiveType) res.get();
  }
  
}
