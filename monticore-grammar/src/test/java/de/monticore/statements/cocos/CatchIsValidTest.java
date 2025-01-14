/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.cocos;

import de.monticore.statements.mccommonstatements.cocos.CatchIsValid;
import de.monticore.statements.mcexceptionstatements._ast.ASTCatchClause;
import de.monticore.statements.testmcexceptionstatements.TestMCExceptionStatementsMill;
import de.monticore.statements.testmcexceptionstatements._cocos.TestMCExceptionStatementsCoCoChecker;
import de.monticore.statements.testmcexceptionstatements._parser.TestMCExceptionStatementsParser;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.*;
import de.monticore.types3.util.DefsTypesForTests;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static de.monticore.types3.util.DefsTypesForTests._objectSymType;
import static de.monticore.types3.util.DefsTypesForTests._throwableSymType;

public class CatchIsValidTest {

  protected TestMCExceptionStatementsCoCoChecker checker;

  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);

    TestMCExceptionStatementsMill.reset();
    TestMCExceptionStatementsMill.init();
    BasicSymbolsMill.initializePrimitives();
    DefsTypesForTests.setup();

    checker = new TestMCExceptionStatementsCoCoChecker();
    checker.setTraverser(TestMCExceptionStatementsMill.traverser());
    checker.addCoCo(new CatchIsValid(new TypeCalculator(null, new FullDeriveFromCombineExpressionsWithLiterals())));

    SymTypeOfObject sTypeA = SymTypeExpressionFactory.createTypeObject("A", TestMCExceptionStatementsMill.globalScope());

    TestMCExceptionStatementsMill.globalScope().add(
        TestMCExceptionStatementsMill.
            oOTypeSymbolBuilder()
            .setName("A")
            .addSuperTypes(_throwableSymType)
            .build());

    TestMCExceptionStatementsMill.globalScope().add(
        TestMCExceptionStatementsMill
            .fieldSymbolBuilder()
            .setName("a")
            .setType(sTypeA)
            .build());

    SymTypeOfObject symTypeB = SymTypeExpressionFactory.createTypeObject("B", TestMCExceptionStatementsMill.globalScope());

    TestMCExceptionStatementsMill.globalScope().add(
        TestMCExceptionStatementsMill
            .oOTypeSymbolBuilder()
            .setName("B")
            .addSuperTypes(_objectSymType)
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