/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.TypesPrettyPrinterConcreteVisitor;
import de.monticore.types.testtypes._parser.TestTypesParser;
import de.monticore.types.types._ast.ASTPrimitiveType;
import de.monticore.types.types._ast.ASTReturnType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.types.types._ast.ASTTypeParameters;
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
   * not be parsed.
   */
  public boolean testType(String input) throws IOException {
    // Parse input
    ASTType inputAST = parseType(input);
    TestCase.assertNotNull(inputAST);
    // Print the inputAST
    TypesPrettyPrinterConcreteVisitor printer = new TypesPrettyPrinterConcreteVisitor(new IndentPrinter());
    String output = printer.prettyprint(inputAST);
    // Parse output
    ASTType outputAST = parseType(output);
    TestCase.assertNotNull(outputAST);
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
   * not be parsed.
   */
  public boolean testTypeParameter(String input) throws IOException {
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
   * @throws IOException
   */
  public ASTType parseType(String input) throws IOException {
    TestTypesParser parser = new TestTypesParser();
    Optional<ASTType> res = parser.parseType(new StringReader(input));
    if (parser.hasErrors()) {
      return null;
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
   * not be parsed.
   */
  public ASTReturnType parseReturnType(String input) throws IOException {
    TestTypesParser parser = new TestTypesParser();
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
   */
  public ASTTypeParameters parseTypeParameters(String input) throws IOException {
    TestTypesParser parser = new TestTypesParser();
    Optional<ASTTypeParameters> res = parser.parseTypeParameters(new StringReader(input));
    if (parser.hasErrors()) {
      return null;
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
   */
  public ASTPrimitiveType parseBooleanType(String input) throws IOException {
    TestTypesParser parser = new TestTypesParser();
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
   */
  public ASTPrimitiveType parseIntegralType(String input) throws IOException {
    TestTypesParser parser = new TestTypesParser();
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
   */
  public ASTPrimitiveType parseFloatingPointType(String input) throws IOException {
    TestTypesParser parser = new TestTypesParser();
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
   */
  public ASTPrimitiveType parseNumericType(String input) throws IOException {
    TestTypesParser parser = new TestTypesParser();
    Optional<ASTPrimitiveType> res = parser.parsePrimitiveType(new StringReader(input));
    TestCase.assertTrue(res.isPresent());
    TestCase.assertTrue(res.get() instanceof ASTPrimitiveType);
    return (ASTPrimitiveType) res.get();
  }
  
}
