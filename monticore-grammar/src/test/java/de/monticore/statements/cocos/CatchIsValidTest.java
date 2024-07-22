/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.cocos;

import de.monticore.statements.mccommonstatements.cocos.CatchIsValid;
import de.monticore.statements.mcexceptionstatements._ast.ASTCatchClause;
import de.monticore.statements.testmccommonstatements.TestMCCommonStatementsMill;
import de.monticore.statements.testmccommonstatements._symboltable.ITestMCCommonStatementsScope;
import de.monticore.statements.testmcexceptionstatements.TestMCExceptionStatementsMill;
import de.monticore.statements.testmcexceptionstatements._cocos.TestMCExceptionStatementsCoCoChecker;
import de.monticore.statements.testmcexceptionstatements._parser.TestMCExceptionStatementsParser;
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

public class CatchIsValidTest {

  protected TestMCExceptionStatementsCoCoChecker checker;

  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);

    TestMCExceptionStatementsMill.reset();
    TestMCExceptionStatementsMill.init();
    BasicSymbolsMill.initializePrimitives();

    checker = new TestMCExceptionStatementsCoCoChecker();
    checker.setTraverser(TestMCExceptionStatementsMill.traverser());
    checker.addCoCo(new CatchIsValid(new TypeCalculator(null, new FullDeriveFromCombineExpressionsWithLiterals())));

    SymTypeOfObject sType = SymTypeExpressionFactory.createTypeObject("java.lang.Throwable", TestMCExceptionStatementsMill.globalScope());
    SymTypeOfObject sTypeA = SymTypeExpressionFactory.createTypeObject("A", TestMCExceptionStatementsMill.globalScope());

    TestMCExceptionStatementsMill.globalScope().add(
        TestMCExceptionStatementsMill.
            oOTypeSymbolBuilder()
            .setName("A")
            .addSuperTypes(sType)
            .build());

    ITestMCCommonStatementsScope javaScope = TestMCCommonStatementsMill.scope();
    javaScope.setName("java");

    ITestMCCommonStatementsScope langScope = TestMCCommonStatementsMill.scope();
    langScope.setName("lang");

    javaScope.addSubScope(langScope);
    TestMCCommonStatementsMill.globalScope().addSubScope(javaScope);

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

    SymTypeOfObject symType = SymTypeExpressionFactory.createTypeObject("java.lang.Object", TestMCExceptionStatementsMill.globalScope());
    SymTypeOfObject symTypeB = SymTypeExpressionFactory.createTypeObject("B", TestMCExceptionStatementsMill.globalScope());

    TestMCExceptionStatementsMill.globalScope().add(
        TestMCExceptionStatementsMill
            .oOTypeSymbolBuilder()
            .setName("B")
            .addSuperTypes(symType)
            .build());

    langScope.add(
        TestMCExceptionStatementsMill
            .oOTypeSymbolBuilder()
            .setName("Object")
            .build());

    TestMCExceptionStatementsMill.globalScope().add(
        TestMCExceptionStatementsMill
            .fieldSymbolBuilder()
            .setName("b")
            .setType(symTypeB)
            .build());
  }

  public void checkValid(String expressionString) throws IOException {
    TestMCExceptionStatementsParser parser = TestMCExceptionStatementsMill.parser();

    Optional<ASTCatchClause> optAST = parser.parse_StringCatchClause(expressionString);
    Assertions.assertTrue(optAST.isPresent());
    ASTCatchClause ast = optAST.get();

    ast.setEnclosingScope(TestMCExceptionStatementsMill.globalScope());
    ast.getCatchTypeList().setEnclosingScope(TestMCExceptionStatementsMill.globalScope());
    ast.getCatchTypeList().forEachMCQualifiedNames(n -> n.setEnclosingScope(TestMCExceptionStatementsMill.globalScope()));

    Log.getFindings().clear();
    checker.checkAll(optAST.get());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  public void checkInvalid(String expressionString) throws IOException {
    TestMCExceptionStatementsParser parser = new TestMCExceptionStatementsParser();

    Optional<ASTCatchClause> optAST = parser.parse_StringCatchClause(expressionString);
    Assertions.assertTrue(optAST.isPresent());
    ASTCatchClause ast = optAST.get();

    ast.setEnclosingScope(TestMCExceptionStatementsMill.globalScope());
    ast.getCatchTypeList().setEnclosingScope(TestMCExceptionStatementsMill.globalScope());
    ast.getCatchTypeList().forEachMCQualifiedNames(n -> n.setEnclosingScope(TestMCExceptionStatementsMill.globalScope()));

    Log.getFindings().clear();
    checker.checkAll(optAST.get());
    Assertions.assertFalse(Log.getFindings().isEmpty());
  }

  @Test
  public void testValid() throws IOException {
    checkValid("catch(A a) {}");
  }

  @Test
  public void testInvalid() throws IOException {
    checkInvalid("catch (B b) {}");
  }

}