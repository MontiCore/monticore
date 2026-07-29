/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.cocos;

import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.statements.mccommonstatements.cocos.AssertIsValid;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.monticore.statements.testmcassertstatements.TestMCAssertStatementsMill;
import de.monticore.statements.testmcassertstatements._cocos.TestMCAssertStatementsCoCoChecker;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types3.util.CombineExpressionsWithLiteralsTypeTraverserFactory;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.monticore.statements.testmcassertstatements.TestMCAssertStatementsMill.parser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@TestWithMCLanguage(TestMCAssertStatementsMill.class)
class AssertIsValidTest {

  @BeforeEach
  void init() {
    CombineExpressionsWithLiteralsTypeTraverserFactory.initTypeCheck3();
    BasicSymbolsMill.initializePrimitives();
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "assert 5 >= 0;",
    "assert !(true||false)&&(5<6);",
    // todo enable after https://git.rwth-aachen.de/monticore/monticore/-/issues/4423
    //"assert 5 >= 0: 1+1;"
  })
  void testValid(String expr) throws IOException {
    // Given
    TestMCAssertStatementsCoCoChecker checker = new TestMCAssertStatementsCoCoChecker();
    checker.addCoCo(new AssertIsValid());

    ASTMCBlockStatement ast = parser().parse_StringMCBlockStatement(expr).orElseThrow();

    // When
    checker.checkAll(ast);
  }

  @ParameterizedTest
  @MethodSource("exprAndErrorProvider")
  void testInvalid(String expr, String error) throws IOException {
    // Given
    TestMCAssertStatementsCoCoChecker checker = new TestMCAssertStatementsCoCoChecker();
    checker.addCoCo(new AssertIsValid());

    ASTMCBlockStatement ast = parser().parse_StringMCBlockStatement(expr).orElseThrow();

    // When
    checker.checkAll(ast);

    // Then
    Log.getFindings().remove(
        MCAssertions.assertHasFindingStartingWith(error));
  }

  static Stream<Arguments> exprAndErrorProvider() {
    return Stream.of(
      arguments("assert 4;", AssertIsValid.ERROR_CODE),
      arguments("assert 'c';", AssertIsValid.ERROR_CODE),
      arguments("assert true + 1; ", "0xB0163")
    );
  }
}
