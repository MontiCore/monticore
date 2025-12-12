/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.cocos;

import de.monticore.statements.mccommonstatements.cocos.IfConditionHasBooleanType;
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

class IfConditionHasBooleanTypeTest {

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
    "if(true){}",
    "if(1<2){}",
    "if(!true&&(5==6)){}",
    "if((1<2)||(5%2==1)){}"
  })
  void testValid(String expr) throws IOException {
    // Given
    TestMCCommonStatementsCoCoChecker checker = new TestMCCommonStatementsCoCoChecker();
    checker.addCoCo(new IfConditionHasBooleanType());

    ASTMCBlockStatement ast = parser().parse_StringMCBlockStatement(expr).orElseThrow();

    // When
    checker.checkAll(ast);

    // Then
    assertTrue(Log.getFindings().isEmpty(), () -> Log.getFindings().toString());
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "if(1+1){}",
    "if('c'+10){}",
    "if(1.2-5.5){}"
  })
  void testInvalid(String expr) throws IOException {
    // Given
    TestMCCommonStatementsCoCoChecker checker = new TestMCCommonStatementsCoCoChecker();
    checker.addCoCo(new IfConditionHasBooleanType());

    ASTMCBlockStatement ast = parser().parse_StringMCBlockStatement(expr).orElseThrow();

    // When
    checker.checkAll(ast);

    // Then
    assertFalse(Log.getFindings().isEmpty(), () -> Log.getFindings().toString());
  }
}
