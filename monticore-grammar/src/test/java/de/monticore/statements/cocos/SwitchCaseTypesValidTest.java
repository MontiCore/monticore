/* (c) [https://github.com/MontiCore/monticore](https://github.com/MontiCore/monticore) */
package de.monticore.statements.cocos;

import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.statements.mccommonstatements.cocos.SwitchCaseTypesValid;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.monticore.statements.testmccommonstatements.TestMCCommonStatementsMill;
import de.monticore.statements.testmccommonstatements._cocos.TestMCCommonStatementsCoCoChecker;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types3.util.CombineExpressionsWithLiteralsTypeTraverserFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.stream.Stream;

import static de.monticore.statements.testmccommonstatements.TestMCCommonStatementsMill.parser;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@TestWithMCLanguage(TestMCCommonStatementsMill.class)
class SwitchCaseTypesValidTest {

  static Stream<Arguments> invalidCaseLabelProvider() {
    return Stream.of(
      arguments("switch(5){case false:}")
    );
  }

  @BeforeEach
  void init() {
    CombineExpressionsWithLiteralsTypeTraverserFactory.initTypeCheck3();
    BasicSymbolsMill.initializePrimitives();
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "switch(5){}",
    "switch('c'){}",
    "switch(5){case 1:}"
  })
  void testValidSwitchCases(String expr) throws IOException {
    // Given
    TestMCCommonStatementsCoCoChecker checker = new TestMCCommonStatementsCoCoChecker();
    checker.addCoCo(new SwitchCaseTypesValid());

    ASTMCBlockStatement ast = parser().parse_StringMCBlockStatement(expr).orElseThrow();

    // When
    checker.checkAll(ast);
  }

  @ParameterizedTest
  @MethodSource("invalidCaseLabelProvider")
  void testInvalidCaseLabels(String expr) throws IOException {
    // Given
    TestMCCommonStatementsCoCoChecker checker = new TestMCCommonStatementsCoCoChecker();
    checker.addCoCo(new SwitchCaseTypesValid());

    ASTMCBlockStatement ast = parser().parse_StringMCBlockStatement(expr).orElseThrow();

    // When
    checker.checkAll(ast);
    
    // Then
    MCAssertions.assertHasFindingStartingWith(SwitchCaseTypesValid.CASE_ERROR_CODE);
  }

  @Test
  void testInvalidSwitchCaseNotCheckedIfSwitchTypeIsObscure() throws IOException {
    // Given
    TestMCCommonStatementsCoCoChecker checker = new TestMCCommonStatementsCoCoChecker();
    checker.addCoCo(new SwitchCaseTypesValid());

    ASTMCBlockStatement ast = parser().parse_StringMCBlockStatement("switch(5.5){case 1:}").orElseThrow();

    // When
    checker.checkAll(ast);
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "switch(5){ case 1: switch('c'){} }",
    "switch(5){ case 1: switch(2){ case 1: } }",
    "switch(5){ case 1: switch(\"a\"){ case \"a\": } }"
  })
  void testNestedSwitchCasesValid(String expr) throws IOException {
    // Given
    TestMCCommonStatementsCoCoChecker checker = new TestMCCommonStatementsCoCoChecker();
    checker.addCoCo(new SwitchCaseTypesValid());

    ASTMCBlockStatement ast = parser().parse_StringMCBlockStatement(expr).orElseThrow();

    // When
    checker.checkAll(ast);
  }

  @Test
  void testNestedSwitchInnerCaseInvalidType() throws IOException {
    // Given
    TestMCCommonStatementsCoCoChecker checker = new TestMCCommonStatementsCoCoChecker();
    checker.addCoCo(new SwitchCaseTypesValid());

    ASTMCBlockStatement ast = parser().parse_StringMCBlockStatement("switch(5){ case 1: switch(2){ case false: } }").orElseThrow();

    // When
    checker.checkAll(ast);
    
    // Then
    MCAssertions.assertHasFindingStartingWith(SwitchCaseTypesValid.CASE_ERROR_CODE);
  }
}
