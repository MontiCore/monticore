/* (c) https://github.com/MontiCore/monticore */

package de.monticore.literals;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import de.monticore.literals.literals._ast.ASTLiteral;
import de.monticore.literals.literals._ast.ASTSignedLiteral;
import de.monticore.literals.testliterals._parser.TestLiteralsParser;
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
    TestLiteralsParser parser = new TestLiteralsParser();
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
    TestLiteralsParser parser = new TestLiteralsParser();
    Optional<ASTSignedLiteral> res = parser.parseSignedLiteral(new StringReader(input));
    TestCase.assertTrue(res.isPresent());
    return res.get();
  }
  
}
