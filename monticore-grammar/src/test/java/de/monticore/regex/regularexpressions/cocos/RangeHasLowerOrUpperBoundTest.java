// (c) https://github.com/MontiCore/monticore
package de.monticore.regex.regularexpressions.cocos;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.regex.regularexpressions._ast.ASTRegExLiteral;
import de.monticore.regex.regularexpressions._cocos.RegularExpressionsCoCoChecker;
import de.se_rwth.commons.logging.Finding;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RangeHasLowerOrUpperBoundTest {

  protected RegularExpressionsCoCoChecker checker;

  @BeforeEach
  public void init() {
    CombineExpressionsWithLiteralsMill.reset();
    CombineExpressionsWithLiteralsMill.init();
    LogStub.init();
    Log.enableFailQuick(false);
    checker = new RegularExpressionsCoCoChecker();
    checker.addCoCo(new RangeHasLowerOrUpperBound());
  }

  @Test
  public void testValid() throws IOException {
    checkValid("R\"a{2,3}\"");
    checkValid("R\"a{,3}\"");
    checkValid("R\"a{2,}\"");
  }

  @Test
  public void testInvalid() throws IOException {
    checkInvalid("R\"a{,}\"");
  }

  // Helper

  protected void checkValid(String expressionString) throws IOException {
    CombineExpressionsWithLiteralsParser parser = new CombineExpressionsWithLiteralsParser();
    Optional<ASTRegExLiteral> optAST =
        parser.parse_StringRegExLiteral(expressionString);
    Assertions.assertTrue(optAST.isPresent());
    Log.getFindings().clear();
    checker.checkAll(optAST.get());
    Assertions.assertTrue(Log.getFindings().isEmpty(), Log.getFindings().stream()
            .map(Finding::buildMsg)
            .collect(Collectors.joining(System.lineSeparator())));
  }

  protected void checkInvalid(String expressionString) throws IOException {
    CombineExpressionsWithLiteralsParser parser = new CombineExpressionsWithLiteralsParser();
    Optional<ASTRegExLiteral> optAST =
        parser.parse_StringRegExLiteral(expressionString);
    Assertions.assertTrue(optAST.isPresent());
    Log.getFindings().clear();
    checker.checkAll(optAST.get());
    Assertions.assertFalse(Log.getFindings().isEmpty());
  }

}
