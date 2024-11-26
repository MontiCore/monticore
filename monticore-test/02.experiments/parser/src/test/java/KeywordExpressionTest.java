/* (c) https://github.com/MontiCore/monticore */

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import keywordexpression.KeywordExpressionMill;
import keywordexpression._parser.KeywordExpressionASTBuildVisitor;
import keywordexpression._parser.KeywordExpressionAntlrLexer;
import keywordexpression._parser.KeywordExpressionAntlrParser;
import keywordexpression._parser.KeywordExpressionParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class KeywordExpressionTest {
  
  @BeforeEach
  public void before() {
    LogStub.initPlusLog();
    Log.enableFailQuick(false);
  }

  @Test
  public void testParseExpr() throws IOException {
    KeywordExpressionMill.init();
    KeywordExpressionParser parser = KeywordExpressionMill.parser();
    var opt = parser.parse_StringExpression("new ABC()");

    Assertions.assertTrue(opt.isPresent());
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testParseStartProd() throws IOException {
    KeywordExpressionMill.init();
    KeywordExpressionParser parser = KeywordExpressionMill.parser();
    var opt = parser.parse_StringStartProd("new ABC();");

    Assertions.assertTrue(opt.isPresent());
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }


  @Test
  public void testParseStartProdANTLR() {
    KeywordExpressionMill.init();
    var x = org.antlr.v4.runtime.CharStreams.fromString("new ABC();");
    KeywordExpressionAntlrLexer lexer = new KeywordExpressionAntlrLexer(x);
    org.antlr.v4.runtime.CommonTokenStream tokens = new org.antlr.v4.runtime.CommonTokenStream(lexer);

    KeywordExpressionAntlrParser parser = new KeywordExpressionAntlrParser(tokens);
    lexer.setMCParser(parser);

    var prc = parser.startProd();

    Assertions.assertNotNull(prc);
    Assertions.assertNotNull(prc);

    // Build ast
    KeywordExpressionASTBuildVisitor buildVisitor = new KeywordExpressionASTBuildVisitor(parser.getFilename(), (org.antlr.v4.runtime.CommonTokenStream)parser.getTokenStream());
    var astPV = (keywordexpression._ast.ASTStartProd)prc.accept(buildVisitor);
    buildVisitor.addFinalComments(astPV, prc);

    // Assert
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertNotNull(astPV);
    Assertions.assertTrue(Log.getFindings().isEmpty());

  }


}
