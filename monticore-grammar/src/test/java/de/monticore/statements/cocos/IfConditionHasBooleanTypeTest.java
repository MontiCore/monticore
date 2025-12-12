/* (c) [https://github.com/MontiCore/monticore](https://github.com/MontiCore/monticore) */
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
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.monticore.statements.testmccommonstatements.TestMCCommonStatementsMill.parser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

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
  @MethodSource("exprAndErrorProvider")
  void testInvalid(String expr, String error) throws IOException {
    // Given
    TestMCCommonStatementsCoCoChecker checker = new TestMCCommonStatementsCoCoChecker();
    checker.addCoCo(new IfConditionHasBooleanType());

    ASTMCBlockStatement ast = parser().parse_StringMCBlockStatement(expr).orElseThrow();

    // When
    checker.checkAll(ast);

    // Then
    assertEquals(List.of(error), Log.getFindings()
      .stream().map(f -> f.getMsg().substring(0, 7)).collect(Collectors.toList())
    );
  }

  static Stream<Arguments> exprAndErrorProvider() {
    return Stream.of(
      arguments("if(1+1){}", IfConditionHasBooleanType.ERROR_CODE),
      arguments("if('c'+10){}", IfConditionHasBooleanType.ERROR_CODE),
      arguments("if(1.2-5.5){}", IfConditionHasBooleanType.ERROR_CODE)
    );
  }
}
