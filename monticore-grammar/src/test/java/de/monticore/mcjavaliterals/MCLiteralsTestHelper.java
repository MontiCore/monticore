/* (c) https://github.com/MontiCore/monticore */

package de.monticore.mcjavaliterals;

import de.monticore.literals.testliterals._parser.TestLiteralsParser;
import de.monticore.mcbasicliterals._ast.ASTLiteral;
import de.monticore.testmcjavaliterals._parser.TestMCJavaLiteralsParser;
import junit.framework.TestCase;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

/**
 * This class provides two methods that allow testing type grammar. The test
 * parses a given input string to an AST. The AST is printed via prettyprint and
 * parsed again. The resulting ASTs are compared. The TypeTestHelper is a
 * singleton.
 *
 */
public class MCLiteralsTestHelper {

  private static MCLiteralsTestHelper instance;

  /**
   * We have a singleton.
   */
  private MCLiteralsTestHelper() {
  }
  
  /**
   * Returns the singleton instance.
   * 
   * @return The instance.
   */
  public static MCLiteralsTestHelper getInstance() {
    if (instance == null) {
      instance = new MCLiteralsTestHelper();
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
    TestMCJavaLiteralsParser parser = new TestMCJavaLiteralsParser();
    Optional<ASTLiteral> res = parser.parseLiteral(new StringReader(input));
    TestCase.assertTrue(res.isPresent());
    return res.get();
  }
  

}
