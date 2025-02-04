/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.cocos;

import de.monticore.statements.mccommonstatements._ast.ASTEnhancedForControl;
import de.monticore.statements.mccommonstatements.cocos.ForEachIsValid;
import de.monticore.statements.testmccommonstatements.TestMCCommonStatementsMill;
import de.monticore.statements.testmccommonstatements._cocos.TestMCCommonStatementsCoCoChecker;
import de.monticore.statements.testmccommonstatements._parser.TestMCCommonStatementsParser;
import de.monticore.statements.testmccommonstatements._symboltable.ITestMCCommonStatementsScope;
import de.monticore.statements.testmccommonstatements._visitor.TestMCCommonStatementsTraverser;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.*;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ForEachIsValidTest {

  protected TestMCCommonStatementsCoCoChecker checker;

  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);

    TestMCCommonStatementsMill.reset();
    TestMCCommonStatementsMill.init();
    BasicSymbolsMill.initializePrimitives();

    checker = new TestMCCommonStatementsCoCoChecker();
    checker.addCoCo(new ForEachIsValid(new TypeCalculator(new FullSynthesizeFromCombineExpressionsWithLiterals(), new FullDeriveFromCombineExpressionsWithLiterals())));

    SymTypeOfObject iterableType = SymTypeExpressionFactory.createTypeObject("java.lang.Iterable", TestMCCommonStatementsMill.globalScope());
    SymTypeOfObject aObjectType = SymTypeExpressionFactory.createTypeObject("A", TestMCCommonStatementsMill.globalScope());

    TestMCCommonStatementsMill.globalScope().add(TestMCCommonStatementsMill
        .oOTypeSymbolBuilder()
        .setName("A")
        .addSuperTypes(iterableType)
        .build());

    ITestMCCommonStatementsScope javaScope = TestMCCommonStatementsMill.scope();
    javaScope.setName("java");

    ITestMCCommonStatementsScope langScope = TestMCCommonStatementsMill.scope();
    langScope.setName("lang");

    TestMCCommonStatementsMill.globalScope().addSubScope(javaScope);
    javaScope.addSubScope(langScope);

    langScope.add(TestMCCommonStatementsMill
        .oOTypeSymbolBuilder()
        .setName("Iterable")
        .build());

    ITestMCCommonStatementsScope utilScope = TestMCCommonStatementsMill.scope();
    utilScope.setName("util");

    javaScope.addSubScope(utilScope);

    utilScope.add(TestMCCommonStatementsMill
        .oOTypeSymbolBuilder().setName("Arrays")
        .build());

    TestMCCommonStatementsMill.globalScope().add(TestMCCommonStatementsMill
        .fieldSymbolBuilder()
        .setName("a")
        .setType(aObjectType)
        .build());

    SymTypeOfObject objectType = SymTypeExpressionFactory.createTypeObject("Object", TestMCCommonStatementsMill.globalScope());
    TestMCCommonStatementsMill.globalScope().add(TestMCCommonStatementsMill
        .oOTypeSymbolBuilder()
        .setName("Object")
        .build());

    TestMCCommonStatementsMill.globalScope().add(TestMCCommonStatementsMill
        .fieldSymbolBuilder()
        .setName("o")
        .setType(objectType)
        .build());
  }

  private void addToTraverser(TestMCCommonStatementsTraverser traverser, ITestMCCommonStatementsScope enclosingScope) {
    FlatExpressionScopeSetter flatExpressionScopeSetter = new FlatExpressionScopeSetter(enclosingScope);
    traverser.add4ExpressionsBasis(flatExpressionScopeSetter);
    traverser.add4CommonExpressions(flatExpressionScopeSetter);
    traverser.add4MCBasicTypes(flatExpressionScopeSetter);
    traverser.add4MCCollectionTypes(flatExpressionScopeSetter);
    traverser.add4MCArrayTypes(flatExpressionScopeSetter);
    traverser.add4MCCommonLiterals(flatExpressionScopeSetter);
  }

  public void checkValid(String expressionString) throws IOException {
    TestMCCommonStatementsParser parser = TestMCCommonStatementsMill.parser();
    Optional<ASTEnhancedForControl> optAST = parser.parse_StringEnhancedForControl(expressionString);
    Assertions.assertTrue(optAST.isPresent());
    ASTEnhancedForControl ast = optAST.get();

    TestMCCommonStatementsTraverser traverser = TestMCCommonStatementsMill.traverser();
    addToTraverser(traverser, TestMCCommonStatementsMill.globalScope());
    ast.accept(traverser);
    ast.setEnclosingScope(TestMCCommonStatementsMill.globalScope());

    Log.getFindings().clear();
    checker.checkAll(optAST.get());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  public void checkInvalid(String expressionString) throws IOException {
    TestMCCommonStatementsParser parser = TestMCCommonStatementsMill.parser();
    Optional<ASTEnhancedForControl> optAST = parser.parse_StringEnhancedForControl(expressionString);
    Assertions.assertTrue(optAST.isPresent());
    ASTEnhancedForControl ast = optAST.get();

    TestMCCommonStatementsTraverser traverser = TestMCCommonStatementsMill.traverser();
    addToTraverser(traverser, TestMCCommonStatementsMill.globalScope());
    ast.accept(traverser);

    ast.setEnclosingScope(TestMCCommonStatementsMill.globalScope());
    Log.getFindings().clear();
    checker.checkAll(optAST.get());
    Assertions.assertFalse(Log.getFindings().isEmpty());
  }

  @Test
  public void testValid() throws IOException {
    checkValid("Object o : a");
  }

  @Test
  public void testInvalid() throws IOException {
    checkInvalid("Object o : 3");
  }

  @Test
  public void testInvalid2() throws IOException {
    checkInvalid("Object o : o");
  }

}