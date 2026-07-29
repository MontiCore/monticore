// (c) https://github.com/MontiCore/monticore
package de.monticore.regex.regularexpressions.cocos;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.regex.regularexpressions._ast.ASTRegExLiteral;
import de.monticore.regex.regularexpressions._cocos.RegularExpressionsCoCoChecker;
import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(CombineExpressionsWithLiteralsMill.class)
public class RangeHasLowerOrUpperBoundTest {

  protected RegularExpressionsCoCoChecker checker;

  @BeforeEach
  public void init() {
    checker = new RegularExpressionsCoCoChecker();
    checker.addCoCo(new RangeHasLowerOrUpperBound());
  }

  @ParameterizedTest
  @ValueSource(strings = {"R\"a{2,3}\"", "R\"a{,3}\"", "R\"a{2,}\""})
  public void testValid(String expr) throws IOException {
    checkValid(expr);
  }

  @ParameterizedTest
  @ValueSource(strings = {"R\"a{,}\""})
  public void testInvalid(String expr) throws IOException {
    checkInvalid(expr);
    
    Log.getFindings()
        .remove(MCAssertions.assertHasFindingStartingWith("0x2E20E"));
  }

  // Helper

  protected void checkValid(String expressionString) throws IOException {
    CombineExpressionsWithLiteralsParser parser = CombineExpressionsWithLiteralsMill.parser();
    Optional<ASTRegExLiteral> optAST =
        parser.parse_StringRegExLiteral(expressionString);
    assertTrue(optAST.isPresent());
    Log.getFindings().clear();
    checker.checkAll(optAST.get());
    assertTrue(Log.getFindings().isEmpty(), Log.getFindings().stream()
            .map(Finding::buildMsg)
            .collect(Collectors.joining(System.lineSeparator())));
  }

  protected void checkInvalid(String expressionString) throws IOException {
    CombineExpressionsWithLiteralsParser parser = CombineExpressionsWithLiteralsMill.parser();
    Optional<ASTRegExLiteral> optAST =
        parser.parse_StringRegExLiteral(expressionString);
    assertTrue(optAST.isPresent());
    Log.getFindings().clear();
    checker.checkAll(optAST.get());
    assertFalse(Log.getFindings().isEmpty());
  }

}
