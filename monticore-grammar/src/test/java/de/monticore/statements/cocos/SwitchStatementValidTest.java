/* (c) [https://github.com/MontiCore/monticore](https://github.com/MontiCore/monticore) */
package de.monticore.statements.cocos;

import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
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
class SwitchStatementValidTest {

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
  void testValidSwitchExpressions(String expr) throws IOException {
    // Given
    TestMCCommonStatementsCoCoChecker checker = new TestMCCommonStatementsCoCoChecker();
    checker.addCoCo(new SwitchStatementValid());

    ASTMCBlockStatement ast = parser().parse_StringMCBlockStatement(expr).orElseThrow();

    // When
    checker.checkAll(ast);
  }

  @ParameterizedTest
  @MethodSource("invalidSwitchExpressionsProvider")
  void testInvalidSwitchExpressions(String expr) throws IOException {
    // Given
    TestMCCommonStatementsCoCoChecker checker = new TestMCCommonStatementsCoCoChecker();
    checker.addCoCo(new SwitchStatementValid());

    ASTMCBlockStatement ast = parser().parse_StringMCBlockStatement(expr).orElseThrow();

    // When
    checker.checkAll(ast);

    // Then
    MCAssertions.assertHasFindingStartingWith(SwitchStatementValid.ERROR_CODE);
  }

  static Stream<Arguments> invalidSwitchExpressionsProvider() {
    return Stream.of(
      arguments("switch(5.5){}"),
      arguments("switch(5.5F){}"),
      arguments("switch(false){}")
    );
  }

  @ParameterizedTest
  @ValueSource(strings = {"switch(c){}", "switch(c){case Foo:}"})
  void testSwitchEnumConstantsValid(String expr) throws IOException {
    // Given
    IMCCommonStatementsArtifactScope imported =
      new MCCommonStatementsSymbols2Json().load("target/resources/test/de/monticore/statements/Enum.sym");
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
  }

  @Test
  void testSwitchEnumConstantsInvalidSwitchType() throws IOException {
    // Given
    IMCCommonStatementsArtifactScope imported =
      new MCCommonStatementsSymbols2Json().load("target/resources/test/de/monticore/statements/Enum.sym");
    TestMCCommonStatementsMill.globalScope().addSubScope(imported);

    VariableSymbol variable = TestMCCommonStatementsMill.variableSymbolBuilder()
      .setName("d")
      .setType(SymTypeExpressionFactory.createTypeObject(imported.resolveOOType("B").orElseThrow()))
      .build();

    TestMCCommonStatementsMill.globalScope().add(variable);

    TestMCCommonStatementsCoCoChecker checker = new TestMCCommonStatementsCoCoChecker();
    checker.addCoCo(new SwitchStatementValid());

    ASTMCBlockStatement ast = parser().parse_StringMCBlockStatement("switch(d){}").orElseThrow();

    TestMCCommonStatementsTraverser traverser = TestMCCommonStatementsMill.traverser();
    traverser.add4ExpressionsBasis(new FlatExpressionScopeSetter(TestMCCommonStatementsMill.globalScope()));
    ast.accept(traverser);

    // When
    checker.checkAll(ast);

    // Then
    MCAssertions.assertHasFindingStartingWith(SwitchStatementValid.ERROR_CODE);
    
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "switch(5){ case 1: switch('c'){} }",
    "switch(5){ case 1: switch(2){ case 1: } }"
  })
  void testNestedSwitchStatementsValid(String expr) throws IOException {
    // Given
    TestMCCommonStatementsCoCoChecker checker = new TestMCCommonStatementsCoCoChecker();
    checker.addCoCo(new SwitchStatementValid());

    ASTMCBlockStatement ast = parser().parse_StringMCBlockStatement(expr).orElseThrow();

    // When
    checker.checkAll(ast);
  }

  @Test
  void testNestedSwitchInvalidInner() throws IOException {
    // Given
    TestMCCommonStatementsCoCoChecker checker = new TestMCCommonStatementsCoCoChecker();
    checker.addCoCo(new SwitchStatementValid());

    ASTMCBlockStatement ast = parser().parse_StringMCBlockStatement("switch(5){ case 1: switch(5.5){} }").orElseThrow();

    // When
    checker.checkAll(ast);

    // Then
    MCAssertions.assertHasFindingStartingWith(SwitchStatementValid.ERROR_CODE);
  }
}
