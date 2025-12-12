/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.cocos;

import de.monticore.statements.mccommonstatements.cocos.ForConditionHasBooleanType;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.monticore.statements.testmccommonstatements.TestMCCommonStatementsMill;
import de.monticore.statements.testmccommonstatements._cocos.TestMCCommonStatementsCoCoChecker;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types3.util.CombineExpressionsWithLiteralsTypeTraverserFactory;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static de.monticore.statements.testmccommonstatements.TestMCCommonStatementsMill.parser;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ForConditionHasBooleanTypeTest {

  @BeforeEach
  void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    TestMCCommonStatementsMill.reset();
    TestMCCommonStatementsMill.init();
    CombineExpressionsWithLiteralsTypeTraverserFactory.initTypeCheck3();
    BasicSymbolsMill.initializePrimitives();
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "for(5; true; 5+1){}",
    "for(5; 5<6; 5+1){}",
    "for(5; !(false || true) && (6==7); 5+1){}"
  })
  void testValid(String expr) throws IOException {
    // Given
    TestMCCommonStatementsCoCoChecker checker = new TestMCCommonStatementsCoCoChecker();
    checker.addCoCo(new ForConditionHasBooleanType());

    ASTMCBlockStatement ast = parser().parse_StringMCBlockStatement(expr).orElseThrow();

    // When
    checker.checkAll(ast);

    // Then
    assertTrue(Log.getFindings().isEmpty(), () -> Log.getFindings().toString());
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "for(1+1; 2+2; 'x'){}"
  })
  void testInvalid(String expr) throws IOException {
    // Given
    TestMCCommonStatementsCoCoChecker checker = new TestMCCommonStatementsCoCoChecker();
    checker.addCoCo(new ForConditionHasBooleanType());

    ASTMCBlockStatement ast = parser().parse_StringMCBlockStatement(expr).orElseThrow();

    // When
    checker.checkAll(ast);

    // Then
    assertFalse(Log.getFindings().isEmpty(), () -> Log.getFindings().toString());
  }
}
