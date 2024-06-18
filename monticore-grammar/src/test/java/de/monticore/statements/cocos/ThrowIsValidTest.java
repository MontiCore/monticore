/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.cocos;

import de.monticore.statements.mccommonstatements.cocos.ThrowIsValid;
import de.monticore.statements.mcexceptionstatements._ast.ASTMCExceptionStatementsNode;
import de.monticore.statements.mcexceptionstatements._ast.ASTThrowStatement;
import de.monticore.statements.testmccommonstatements.TestMCCommonStatementsMill;
import de.monticore.statements.testmccommonstatements._symboltable.ITestMCCommonStatementsScope;
import de.monticore.statements.testmcexceptionstatements.TestMCExceptionStatementsMill;
import de.monticore.statements.testmcexceptionstatements._cocos.TestMCExceptionStatementsCoCoChecker;
import de.monticore.statements.testmcexceptionstatements._parser.TestMCExceptionStatementsParser;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.*;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ThrowIsValidTest {

  protected TestMCExceptionStatementsCoCoChecker checker;

  @Before
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);

    TestMCExceptionStatementsMill.reset();
    TestMCExceptionStatementsMill.init();
    BasicSymbolsMill.initializePrimitives();

    checker = new TestMCExceptionStatementsCoCoChecker();
    checker.setTraverser(TestMCExceptionStatementsMill.traverser());
    checker.addCoCo(new ThrowIsValid(new TypeCalculator(null, new FullDeriveFromCombineExpressionsWithLiterals())));

    SymTypeOfObject sType = SymTypeExpressionFactory.createTypeObject("java.lang.Throwable", TestMCExceptionStatementsMill.globalScope());
    SymTypeOfObject sTypeA = SymTypeExpressionFactory.createTypeObject("A", TestMCExceptionStatementsMill.globalScope());

    TestMCExceptionStatementsMill.globalScope().add(
        TestMCExceptionStatementsMill
            .oOTypeSymbolBuilder()
            .setName("A")
            .addSuperTypes(sType)
            .build());

    ITestMCCommonStatementsScope javaScope = TestMCCommonStatementsMill.scope();
    javaScope.setName("java");

    ITestMCCommonStatementsScope langScope = TestMCCommonStatementsMill.scope();
    langScope.setName("lang");

    TestMCCommonStatementsMill.globalScope().addSubScope(javaScope);
    javaScope.addSubScope(langScope);

    langScope.add(
        TestMCExceptionStatementsMill
            .oOTypeSymbolBuilder()
            .setName("Throwable")
            .build());

    TestMCExceptionStatementsMill.globalScope().add(
        TestMCExceptionStatementsMill
            .fieldSymbolBuilder()
            .setName("a")
            .setType(sTypeA)
            .build());

    SymTypeOfObject sTypeB = SymTypeExpressionFactory.createTypeObject("B", TestMCExceptionStatementsMill.globalScope());

    TestMCExceptionStatementsMill.globalScope().add(
        TestMCExceptionStatementsMill
            .oOTypeSymbolBuilder()
            .setName("B")
            .build());

    TestMCExceptionStatementsMill.globalScope().add(
        TestMCExceptionStatementsMill
            .fieldSymbolBuilder()
            .setName("b")
            .setType(sTypeB)
            .build());
  }

  public void checkValid(String expressionString) throws IOException {

    TestMCExceptionStatementsParser parser = new TestMCExceptionStatementsParser();
    Optional<ASTThrowStatement> optAST = parser.parse_StringThrowStatement(expressionString);
    assertTrue(optAST.isPresent());
    ASTThrowStatement ast = optAST.get();
    ast.setEnclosingScope(TestMCExceptionStatementsMill.globalScope());
    ast.getExpression().setEnclosingScope(TestMCExceptionStatementsMill.globalScope());
    Log.getFindings().clear();
    checker.checkAll((ASTMCExceptionStatementsNode) optAST.get());
    assertTrue(Log.getFindings().isEmpty());

  }

  public void checkInvalid(String expressionString) throws IOException {

    TestMCExceptionStatementsParser parser = new TestMCExceptionStatementsParser();
    Optional<ASTThrowStatement> optAST = parser.parse_StringThrowStatement(expressionString);
    assertTrue(optAST.isPresent());
    ASTThrowStatement ast = optAST.get();
    ast.setEnclosingScope(TestMCExceptionStatementsMill.globalScope());
    ast.getExpression().setEnclosingScope(TestMCExceptionStatementsMill.globalScope());
    Log.getFindings().clear();
    checker.checkAll((ASTMCExceptionStatementsNode) optAST.get());
    assertFalse(Log.getFindings().isEmpty());

  }

  @Test
  public void testValid() throws IOException {
    checkValid("throw a;");
  }

  @Test
  public void testInvalid() throws IOException {
    checkInvalid("throw b;");
  }
}