/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.cocos;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._parser.CombineExpressionsWithLiteralsParser;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._cocos.ExpressionsBasisCoCoChecker;
import de.monticore.grammar.cocos.CocoTest;
import de.monticore.runtime.junit.MCAssertions;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.FullDeriveFromCombineExpressionsWithLiterals;
import de.monticore.types.check.TypeCalculator;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExpressionValidTest extends CocoTest {

  protected ExpressionsBasisCoCoChecker checker;

  @BeforeEach
  public void init() {
    CombineExpressionsWithLiteralsMill.reset();
    CombineExpressionsWithLiteralsMill.init();
    BasicSymbolsMill.initializePrimitives();
    TypeCalculator typeCheck = new TypeCalculator(null, new FullDeriveFromCombineExpressionsWithLiterals());
    checker = new ExpressionsBasisCoCoChecker();
    checker.addCoCo(new ExpressionValid(typeCheck));
    new TypeCalculator(null, new FullDeriveFromCombineExpressionsWithLiterals());
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "7-4*2",
      "4/2*6%4",
      "(5<6)&&(1<=1)",
      "!true||false&&(5>=0)",
      "5.0/2.5%2"
  })
  public void testValid(String expressionString) throws IOException {
    CombineExpressionsWithLiteralsParser parser = CombineExpressionsWithLiteralsMill.parser();
    Optional<ASTExpression> optAST = parser.parse_StringExpression(expressionString);
    assertTrue(optAST.isPresent());
    Log.getFindings().clear();
    checker.checkAll(optAST.get());
  }
  
  static Stream<Arguments> testInvalidArgs() {
    return Stream.of(
        Arguments.of("5+false", List.of("0xA0168")),
        Arguments.of("true-true", List.of("0xA0168")),
        Arguments.of("!false!=5", List.of("0xA0166")),
        Arguments.of("5||7", List.of("0xA0167")),
        Arguments.of("true++", List.of("0xA0183")),
        Arguments.of("(true&&6)||(false>=37)", List.of("0xA0167", "0xA0167"))
    );
  }
  
  @ParameterizedTest
  @MethodSource("testInvalidArgs")
  public void testInvalid(String expressionString, List<String> expectedErrors) throws IOException {
    CombineExpressionsWithLiteralsParser parser = CombineExpressionsWithLiteralsMill.parser();
    Optional<ASTExpression> optAST = parser.parse_StringExpression(expressionString);
    assertTrue(optAST.isPresent());
    checker.checkAll(optAST.get());
    
    for (String expectedError : expectedErrors) {
      MCAssertions.assertHasFindingStartingWith(expectedError);
    }
  }
}