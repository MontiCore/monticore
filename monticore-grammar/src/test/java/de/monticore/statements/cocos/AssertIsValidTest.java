/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.cocos;

import de.monticore.statements.mccommonstatements.cocos.AssertIsValid;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.monticore.statements.testmcassertstatements.TestMCAssertStatementsMill;
import de.monticore.statements.testmcassertstatements._cocos.TestMCAssertStatementsCoCoChecker;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.FullDeriveFromCombineExpressionsWithLiterals;
import de.monticore.types.check.TypeCalculator;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static de.monticore.statements.testmcassertstatements.TestMCAssertStatementsMill.parser;

class AssertIsValidTest {

  @BeforeEach
  void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    TestMCAssertStatementsMill.reset();
    TestMCAssertStatementsMill.init();
    BasicSymbolsMill.initializePrimitives();
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "assert 5 >= 0;",
    "assert !(true||false)&&(5<6);",
    "assert 5 >= 0: 1+1;"
  })
  void testValid(String expr) throws IOException {
    // Given
    TestMCAssertStatementsCoCoChecker checker = new TestMCAssertStatementsCoCoChecker();
    checker.addCoCo(new AssertIsValid(new TypeCalculator(null, new FullDeriveFromCombineExpressionsWithLiterals())));

    ASTMCBlockStatement ast = parser().parse_StringMCBlockStatement(expr).orElseThrow();

    // When
    checker.checkAll(ast);

    // Then
    assertTrue(Log.getFindings().isEmpty(), () -> Log.getFindings().toString());
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "assert 4;",
    "assert 'c';"
  })
  void testInvalid(String expr) throws IOException {
    // Given
    TestMCAssertStatementsCoCoChecker checker = new TestMCAssertStatementsCoCoChecker();
    checker.addCoCo(new AssertIsValid(new TypeCalculator(null, new FullDeriveFromCombineExpressionsWithLiterals())));

    ASTMCBlockStatement ast = parser().parse_StringMCBlockStatement(expr).orElseThrow();

    // When
    checker.checkAll(ast);

    // Then
    assertFalse(Log.getFindings().isEmpty(), () -> Log.getFindings().toString());
  }
}
