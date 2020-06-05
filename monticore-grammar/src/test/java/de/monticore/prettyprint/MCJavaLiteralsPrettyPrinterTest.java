/* (c) https://github.com/MontiCore/monticore */
package de.monticore.prettyprint;

import de.monticore.MCJavaLiteralsPrettyPrinter;
import de.monticore.literals.mcjavaliterals._ast.ASTDoubleLiteral;
import de.monticore.literals.mcjavaliterals._ast.ASTFloatLiteral;
import de.monticore.literals.mcjavaliterals._ast.ASTIntLiteral;
import de.monticore.literals.mcjavaliterals._ast.ASTLongLiteral;
import de.monticore.literals.testmcjavaliterals._parser.TestMCJavaLiteralsParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MCJavaLiteralsPrettyPrinterTest {

  private TestMCJavaLiteralsParser parser = new TestMCJavaLiteralsParser();

  private MCJavaLiteralsPrettyPrinter prettyPrinter = new MCJavaLiteralsPrettyPrinter(new IndentPrinter());

  @BeforeClass
  public static void setUp() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Before
  public void init() {
    prettyPrinter.getPrinter().clearBuffer();
  }

  @Test
  public void testIntLiteral() throws IOException {
    Optional<ASTIntLiteral> result = parser.parse_StringIntLiteral("1110");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTIntLiteral ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

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

    String output = prettyPrinter.prettyprint(ast);

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

    String output = prettyPrinter.prettyprint(ast);

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

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringDoubleLiteral(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());

    assertTrue(ast.deepEquals(result.get()));
  }
}
