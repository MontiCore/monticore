/* (c) https://github.com/MontiCore/monticore */
package de.monticore.regex.regularexpressions._ast;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.runtime.junit.TestWithMCLanguage;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestWithMCLanguage(CombineExpressionsWithLiteralsMill.class)
class ASTCharRangeTest {

  @Test
  public void testRangeStartAndEnd() throws IOException {
    // This returns the empty Optional:
    // CombineExpressionsWithLiteralsMill.parser().parse_StringCharRange("a-c").get();

    var ast = CombineExpressionsWithLiteralsMill.parser().parse_StringRegExLiteral("R\"[a-7 ]\"").get();
    var internal = (ASTBracketRegEx) ast.getRegularExpression().getRegExItemList().getFirst();
    var range = (ASTCharRange) internal.getBracketRegExItemList().getFirst();

    assertEquals('a', range.getStart());
    assertEquals('7', range.getEnd());
  }
}