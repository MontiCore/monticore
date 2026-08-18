/* (c) https://github.com/MontiCore/monticore */
package de.monticore.prettyprint;

import de.monticore.literals.mcjavaliterals._ast.ASTDoubleLiteral;
import de.monticore.literals.mcjavaliterals._ast.ASTFloatLiteral;
import de.monticore.literals.mcjavaliterals._ast.ASTIntLiteral;
import de.monticore.literals.mcjavaliterals._ast.ASTLongLiteral;
import de.monticore.literals.testmcjavaliterals.TestMCJavaLiteralsMill;
import de.monticore.literals.testmcjavaliterals._parser.TestMCJavaLiteralsParser;
import de.monticore.runtime.junit.TestWithMCLanguage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(TestMCJavaLiteralsMill.class)
public class MCJavaLiteralsPrettyPrinterTest {

  private TestMCJavaLiteralsParser parser;

  @BeforeEach
  public void init() {
    parser = TestMCJavaLiteralsMill.parser();
  }
  
  @Test
  public void testIntLiteral() throws IOException {
    Optional<ASTIntLiteral> result = parser.parse_StringIntLiteral("1110");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTIntLiteral ast = result.get();

    String output = TestMCJavaLiteralsMill.prettyPrint(ast, false);

    result = parser.parse_StringIntLiteral(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }


  @Test
  public void testLongLiteral() throws IOException {
    Optional<ASTLongLiteral> result = parser.parse_StringLongLiteral("109584763l");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTLongLiteral ast = result.get();

    String output = TestMCJavaLiteralsMill.prettyPrint(ast, false);

    result = parser.parse_StringLongLiteral(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }


  @Test
  public void testFloatLiteral() throws IOException {
    Optional<ASTFloatLiteral> result = parser.parse_StringFloatLiteral("93475.2434356677f");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTFloatLiteral ast = result.get();

    String output = TestMCJavaLiteralsMill.prettyPrint(ast, false);

    result = parser.parse_StringFloatLiteral(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }


  @Test
  public void testDoubleLiteral() throws IOException {
    Optional<ASTDoubleLiteral> result = parser.parse_StringDoubleLiteral("1110.45600233");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTDoubleLiteral ast = result.get();

    String output = TestMCJavaLiteralsMill.prettyPrint(ast, false);

    result = parser.parse_StringDoubleLiteral(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }
}
