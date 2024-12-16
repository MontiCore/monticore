package de.monticore.regex.regularexpressions._ast;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import de.se_rwth.commons.logging.MCFatalError;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ASTCharRangeTest {
  @BeforeEach
  public void init() {
    CombineExpressionsWithLiteralsMill.reset();
    CombineExpressionsWithLiteralsMill.init();
    LogStub.init();
  }

  @AfterEach
  public void end() {
    assertEquals(0, Log.getErrorCount(), Log.getFindings().toString());
  }

  @Test
  public void testRangeStartAndEnd() throws IOException {
    // This returns the empty Optional:
    // CombineExpressionsWithLiteralsMill.parser().parse_StringCharRange("a-c").get();

    var ast = CombineExpressionsWithLiteralsMill.parser().parse_StringRegExLiteral("R\"[a-7 ]\"").get();
    var internal = (ASTBracketRegEx) ast.getRegularExpression().getRegExItemList().get(0);
    var range = (ASTCharRange) internal.getBracketRegExItemList().get(0);

    assertEquals('a', range.getStart());
    assertEquals('7', range.getEnd());
  }
}