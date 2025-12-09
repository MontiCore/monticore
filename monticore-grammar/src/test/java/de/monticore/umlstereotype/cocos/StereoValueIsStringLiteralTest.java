package de.monticore.umlstereotype.cocos;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.testmccommon._parser.TestMCCommonParser;
import de.monticore.umlstereotype._ast.ASTStereotype;
import de.monticore.umlstereotype._cocos.UMLStereotypeCoCoChecker;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

/**
 * Holds tests for {@link  StereoValueIsStringLiteral}
 */
public class StereoValueIsStringLiteralTest {

  protected UMLStereotypeCoCoChecker checker;
  protected TestMCCommonParser parser;

  @BeforeEach
  public void init() {
    checker = new UMLStereotypeCoCoChecker();
    parser = new TestMCCommonParser();
    checker.addCoCo(new StereoValueIsStringLiteral());
  }

  public void checkValid(String expressionString) throws IOException {
    Optional<ASTStereotype> optAST = parser.parse_StringStereotype(expressionString);
    Assertions.assertTrue(optAST.isPresent());
    Log.getFindings().clear();
    checker.checkAll(optAST.get());
    Assertions.assertTrue(Log.getFindings().isEmpty(), Log.getFindings().toString());
  }

  public void checkInvalid(String expressionString) throws IOException {
    Optional<ASTStereotype> optAST = parser.parse_StringStereotype(expressionString);
    Assertions.assertTrue(optAST.isPresent());
    Log.getFindings().clear();
    Log.enableFailQuick(false);
    checker.checkAll(optAST.get());
    Assertions.assertFalse(Log.getFindings().isEmpty());
  }

  @Test
  public void testValid() throws IOException {
    checkValid("<<bla>>");
    checkValid("<<bla=\"\">>");
    checkValid("<<bla=\"blu\">>");
    checkValid("<<bla=\"17\">>");
  }

  @Test
  public void testInvalid() throws IOException {
    checkInvalid("<<bla=1>>");
    checkInvalid("<<bla=blu>>");
  }
}
