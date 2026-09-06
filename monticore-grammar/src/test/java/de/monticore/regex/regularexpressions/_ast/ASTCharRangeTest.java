/* (c) https://github.com/MontiCore/monticore */
package de.monticore.regex.regularexpressions._ast;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.runtime.junit.TestWithMCLanguage;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(CombineExpressionsWithLiteralsMill.class)
class ASTCharRangeTest {

  @Test
  public void testRangeStartAndEnd() throws IOException {
    // This returns the empty Optional:
    // CombineExpressionsWithLiteralsMill.parser().parse_StringCharRange("a-c").get();

    Optional<ASTRegExLiteral>
        astOpt = CombineExpressionsWithLiteralsMill.parser().parse_StringRegExLiteral("R\"[a-7 ]\"");
    assertTrue(astOpt.isPresent());
    ASTRegExLiteral ast = astOpt.get();
    var internal = (ASTBracketRegEx) ast.getRegularExpression().getRegExItemList().getFirst();
    var range = (ASTCharRange) internal.getBracketRegExItemList().getFirst();

    assertEquals('a', range.getStart());
    assertEquals('7', range.getEnd());
  }
}