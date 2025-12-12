/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.cocos;

import de.monticore.statements.mccommonstatements._symboltable.IMCCommonStatementsArtifactScope;
import de.monticore.statements.mccommonstatements._symboltable.MCCommonStatementsSymbols2Json;
import de.monticore.statements.mccommonstatements.cocos.SwitchStatementValid;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.monticore.statements.testmccommonstatements.TestMCCommonStatementsMill;
import de.monticore.statements.testmccommonstatements._cocos.TestMCCommonStatementsCoCoChecker;
import de.monticore.statements.testmccommonstatements._visitor.TestMCCommonStatementsTraverser;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.FlatExpressionScopeSetter;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types3.util.CombineExpressionsWithLiteralsTypeTraverserFactory;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.statements.testmccommonstatements.TestMCCommonStatementsMill.parser;
import static org.junit.jupiter.api.Assertions.*;

class SwitchStatementValidTest {

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
    "switch(5){}",
    "switch('c'){}"
  })
  void testValid(String expr) throws IOException {
    // Given
    TestMCCommonStatementsCoCoChecker checker = new TestMCCommonStatementsCoCoChecker();
    checker.addCoCo(new SwitchStatementValid());

    ASTMCBlockStatement ast = parser().parse_StringMCBlockStatement(expr).orElseThrow();

    // When
    checker.checkAll(ast);

    // Then
    assertTrue(Log.getFindings().isEmpty(), () -> Log.getFindings().toString());
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "switch(5.5){}",
    "switch(5.5F){}",
    "switch(false){}"
  })
  void testInvalid(String expr) throws IOException {
    // Given
    TestMCCommonStatementsCoCoChecker checker = new TestMCCommonStatementsCoCoChecker();
    checker.addCoCo(new SwitchStatementValid());

    ASTMCBlockStatement ast = parser().parse_StringMCBlockStatement(expr).orElseThrow();

    // When
    checker.checkAll(ast);

    // Then
    assertEquals(List.of(SwitchStatementValid.ERROR_CODE), Log.getFindings()
      .stream().map(f -> f.getMsg().substring(0, 7)).collect(Collectors.toList())
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {"switch(c){}"})
  void testSwitchEnumConstantsValid(String expr) throws IOException {
    // Given
    IMCCommonStatementsArtifactScope imported =
      new MCCommonStatementsSymbols2Json().load("src/test/resources/de/monticore/statements/Enum.sym");
    TestMCCommonStatementsMill.globalScope().addSubScope(imported);

    VariableSymbol variable = TestMCCommonStatementsMill.variableSymbolBuilder()
      .setName("c")
      .setType(SymTypeExpressionFactory.createTypeObject(imported.resolveOOType("A").orElseThrow()))
      .build();

    TestMCCommonStatementsMill.globalScope().add(variable);

    TestMCCommonStatementsCoCoChecker checker = new TestMCCommonStatementsCoCoChecker();
    checker.addCoCo(new SwitchStatementValid());

    ASTMCBlockStatement ast = parser().parse_StringMCBlockStatement(expr).orElseThrow();

    TestMCCommonStatementsTraverser traverser = TestMCCommonStatementsMill.traverser();
    traverser.add4ExpressionsBasis(new FlatExpressionScopeSetter(TestMCCommonStatementsMill.globalScope()));
    ast.accept(traverser);

    // When
    checker.checkAll(ast);

    // Then
    assertTrue(Log.getFindings().isEmpty(), () -> Log.getFindings().toString());
  }

  @ParameterizedTest
  @ValueSource(strings = {"switch(d){}"})
  void testSwitchEnumConstantsInvalid(String expr) throws IOException {
    // Given
    IMCCommonStatementsArtifactScope imported =
      new MCCommonStatementsSymbols2Json().load("src/test/resources/de/monticore/statements/Enum.sym");
    TestMCCommonStatementsMill.globalScope().addSubScope(imported);

    VariableSymbol variable = TestMCCommonStatementsMill.variableSymbolBuilder()
      .setName("d")
      .setType(SymTypeExpressionFactory.createTypeObject(imported.resolveOOType("B").orElseThrow()))
      .build();

    TestMCCommonStatementsMill.globalScope().add(variable);

    TestMCCommonStatementsCoCoChecker checker = new TestMCCommonStatementsCoCoChecker();
    checker.addCoCo(new SwitchStatementValid());

    ASTMCBlockStatement ast = parser().parse_StringMCBlockStatement(expr).orElseThrow();

    TestMCCommonStatementsTraverser traverser = TestMCCommonStatementsMill.traverser();
    traverser.add4ExpressionsBasis(new FlatExpressionScopeSetter(TestMCCommonStatementsMill.globalScope()));
    ast.accept(traverser);

    // When
    checker.checkAll(ast);

    // Then
    assertEquals(List.of(SwitchStatementValid.ERROR_CODE), Log.getFindings()
      .stream().map(f -> f.getMsg().substring(0, 7)).collect(Collectors.toList())
    );
  }
}
