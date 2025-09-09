/* (c) https://github.com/MontiCore/monticore */
package de.monticore.prettyprint;

import de.monticore.literals.mcjavaliterals._prettyprint.MCJavaLiteralsFullPrettyPrinter;
import de.monticore.literals.mcjavaliterals._ast.ASTDoubleLiteral;
import de.monticore.literals.mcjavaliterals._ast.ASTFloatLiteral;
import de.monticore.literals.mcjavaliterals._ast.ASTIntLiteral;
import de.monticore.literals.mcjavaliterals._ast.ASTLongLiteral;
import de.monticore.literals.testmcjavaliterals.TestMCJavaLiteralsMill;
import de.monticore.literals.testmcjavaliterals._parser.TestMCJavaLiteralsParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MCJavaLiteralsPrettyPrinterTest {

  private TestMCJavaLiteralsParser parser = new TestMCJavaLiteralsParser();

  private MCJavaLiteralsFullPrettyPrinter prettyPrinter = new MCJavaLiteralsFullPrettyPrinter(new IndentPrinter());

  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    TestMCJavaLiteralsMill.reset();
    TestMCJavaLiteralsMill.init();
    prettyPrinter.getPrinter().clearBuffer();
  }
  @Test
  public void testIntLiteral() throws IOException {
    Optional<ASTIntLiteral> result = parser.parse_StringIntLiteral("1110");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTIntLiteral ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringIntLiteral(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testLongLiteral() throws IOException {
    Optional<ASTLongLiteral> result = parser.parse_StringLongLiteral("109584763l");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTLongLiteral ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringLongLiteral(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testFloatLiteral() throws IOException {
    Optional<ASTFloatLiteral> result = parser.parse_StringFloatLiteral("93475.2434356677f");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTFloatLiteral ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringFloatLiteral(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testDoubleLiteral() throws IOException {
    Optional<ASTDoubleLiteral> result = parser.parse_StringDoubleLiteral("1110.45600233");
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    ASTDoubleLiteral ast = result.get();

    String output = prettyPrinter.prettyprint(ast);

    result = parser.parse_StringDoubleLiteral(output);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());

    Assertions.assertTrue(ast.deepEquals(result.get()));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
}
