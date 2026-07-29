/* (c) https://github.com/MontiCore/monticore */

package de.monticore.mcjavaliterals;

import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.literals.testmcjavaliterals.TestMCJavaLiteralsMill;
import de.monticore.literals.testmcjavaliterals._parser.TestMCJavaLiteralsParser;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This class provides two methods that allow testing type grammar. The test
 * parses a given input string to an AST. The AST is printed via prettyprint and
 * parsed again. The resulting ASTs are compared. The TypeTestHelper is a
 * singleton.
 */
public class MCJavaLiteralsTestHelper {

  private static MCJavaLiteralsTestHelper instance;

  /**
   * We have a singleton.
   */
  private MCJavaLiteralsTestHelper() {
  }

  /**
   * Returns the singleton instance.
   *
   * @return The instance.
   */
  public static MCJavaLiteralsTestHelper getInstance() {
    if (instance == null) {
      instance = new MCJavaLiteralsTestHelper();
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
    TestMCJavaLiteralsParser parser = TestMCJavaLiteralsMill.parser();
    Optional<ASTLiteral> res = parser.parseLiteral(new StringReader(input));
    assertTrue(res.isPresent());
    return res.get();
  }
  

}
