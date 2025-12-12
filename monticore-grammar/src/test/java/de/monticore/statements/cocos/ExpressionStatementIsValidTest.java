/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.cocos;

import de.monticore.statements.mccommonstatements.cocos.ExpressionStatementIsValid;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.monticore.statements.testmccommonstatements.TestMCCommonStatementsMill;
import de.monticore.statements.testmccommonstatements._cocos.TestMCCommonStatementsCoCoChecker;
import de.monticore.statements.testmccommonstatements._visitor.TestMCCommonStatementsTraverser;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.check.FlatExpressionScopeSetter;
import de.monticore.types3.util.CombineExpressionsWithLiteralsTypeTraverserFactory;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static de.monticore.statements.testmccommonstatements.TestMCCommonStatementsMill.*;
import static de.monticore.types.check.SymTypeExpressionFactory.createPrimitive;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExpressionStatementIsValidTest {

  @BeforeEach
  void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    TestMCCommonStatementsMill.reset();
    TestMCCommonStatementsMill.init();
    CombineExpressionsWithLiteralsTypeTraverserFactory.initTypeCheck3();
    BasicSymbolsMill.initializePrimitives();
    initSymbols();
  }

  private static void initSymbols() {
    FieldSymbol anInt = fieldSymbolBuilder().setName("anInt")
      .setType(createPrimitive(BasicSymbolsMill.INT))
      .setEnclosingScope(globalScope())
      .setAstNodeAbsent()
      .build();
    globalScope().add(anInt);
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "anInt = 5;"
  })
  void testValid(String expr) throws IOException {
    // Given
    TestMCCommonStatementsCoCoChecker checker = new TestMCCommonStatementsCoCoChecker();
    checker.addCoCo(new ExpressionStatementIsValid());

    ASTMCBlockStatement ast = parser().parse_StringMCBlockStatement(expr).orElseThrow();

    TestMCCommonStatementsTraverser traverser = inheritanceTraverser();
    traverser.add4ExpressionsBasis(new FlatExpressionScopeSetter(globalScope()));
    ast.accept(traverser);

    // When
    checker.checkAll(ast);

    // Then
    assertTrue(Log.getFindings().isEmpty(), () -> Log.getFindings().toString());
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "anInt = true;"
  })
  void testInvalid(String expr) throws IOException {
    // Given
    TestMCCommonStatementsCoCoChecker checker = new TestMCCommonStatementsCoCoChecker();
    checker.addCoCo(new ExpressionStatementIsValid());

    ASTMCBlockStatement ast = parser().parse_StringMCBlockStatement(expr).orElseThrow();

    TestMCCommonStatementsTraverser traverser = inheritanceTraverser();
    traverser.add4ExpressionsBasis(new FlatExpressionScopeSetter(globalScope()));
    ast.accept(traverser);

    // When
    checker.checkAll(ast);

    // Then
    assertFalse(Log.getFindings().isEmpty(), () -> Log.getFindings().toString());
  }
}
