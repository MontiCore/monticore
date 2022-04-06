/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.cocos;

import de.monticore.statements.mccommonstatements.cocos.CatchIsValid;
import de.monticore.statements.mcexceptionstatements._ast.ASTCatchClause;
import de.monticore.statements.testmcexceptionstatements.TestMCExceptionStatementsMill;
import de.monticore.statements.testmcexceptionstatements._cocos.TestMCExceptionStatementsCoCoChecker;
import de.monticore.statements.testmcexceptionstatements._parser.TestMCExceptionStatementsParser;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.*;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CatchIsValidTest {
  
  private static final TestMCExceptionStatementsCoCoChecker checker = new TestMCExceptionStatementsCoCoChecker();
  
  @BeforeClass
  public static void disableFailQuick(){

    LogStub.init();
    Log.enableFailQuick(false);
    TestMCExceptionStatementsMill.reset();
    TestMCExceptionStatementsMill.init();
    BasicSymbolsMill.initializePrimitives();
    checker.setTraverser(TestMCExceptionStatementsMill.traverser());
    checker.addCoCo(new CatchIsValid(new TypeCalculator(null, new DeriveSymTypeOfCombineExpressionsDelegator())));
    
    SymTypeOfObject sType = SymTypeExpressionFactory.createTypeObject("java.lang.Throwable", TestMCExceptionStatementsMill.globalScope());
    SymTypeOfObject sTypeA = SymTypeExpressionFactory.createTypeObject("A", TestMCExceptionStatementsMill.globalScope());
    TestMCExceptionStatementsMill.globalScope().add(TestMCExceptionStatementsMill.oOTypeSymbolBuilder().setName("A").addSuperTypes(sType).build());
    TestMCExceptionStatementsMill.globalScope().add(TestMCExceptionStatementsMill.oOTypeSymbolBuilder().setName("java.lang.Throwable").build());
    TestMCExceptionStatementsMill.globalScope().add(TestMCExceptionStatementsMill.fieldSymbolBuilder().setName("a").setType(sTypeA).build());
  
    SymTypeOfObject symType = SymTypeExpressionFactory.createTypeObject("java.lang.Object", TestMCExceptionStatementsMill.globalScope());
    SymTypeOfObject symTypeB = SymTypeExpressionFactory.createTypeObject("B", TestMCExceptionStatementsMill.globalScope());
    TestMCExceptionStatementsMill.globalScope().add(TestMCExceptionStatementsMill.oOTypeSymbolBuilder().setName("B").addSuperTypes(symType).build());
    TestMCExceptionStatementsMill.globalScope().add(TestMCExceptionStatementsMill.oOTypeSymbolBuilder().setName("java.lang.Object").build());
    TestMCExceptionStatementsMill.globalScope().add(TestMCExceptionStatementsMill.fieldSymbolBuilder().setName("b").setType(symTypeB).build());
  }
  
  public void checkValid(String expressionString) throws IOException {
    
    TestMCExceptionStatementsParser parser = new TestMCExceptionStatementsParser();
    Optional<ASTCatchClause> optAST = parser.parse_StringCatchClause(expressionString);
    assertTrue(optAST.isPresent());
    ASTCatchClause ast = optAST.get();
    ast.setEnclosingScope(TestMCExceptionStatementsMill.globalScope());
    ast.getCatchTypeList().setEnclosingScope(TestMCExceptionStatementsMill.globalScope());
    ast.getCatchTypeList().forEachMCQualifiedNames(n -> n.setEnclosingScope(TestMCExceptionStatementsMill.globalScope()));
    Log.getFindings().clear();
    checker.checkAll(optAST.get());
    assertTrue(Log.getFindings().isEmpty());
    
  }
  
  public void checkInvalid(String expressionString) throws IOException {
    
    TestMCExceptionStatementsParser parser = new TestMCExceptionStatementsParser();
    Optional<ASTCatchClause> optAST = parser.parse_StringCatchClause(expressionString);
    assertTrue(optAST.isPresent());
    ASTCatchClause ast = optAST.get();
    ast.setEnclosingScope(TestMCExceptionStatementsMill.globalScope());
    ast.getCatchTypeList().setEnclosingScope(TestMCExceptionStatementsMill.globalScope());
    ast.getCatchTypeList().forEachMCQualifiedNames(n -> n.setEnclosingScope(TestMCExceptionStatementsMill.globalScope()));
    Log.getFindings().clear();
    checker.checkAll(optAST.get());
    assertFalse(Log.getFindings().isEmpty());
    
  }
  
  @Test
  public void testValid() throws IOException {
    checkValid("catch(A a){}");
  }
  
  @Test
  public void testInvalid() throws IOException {
    checkInvalid("catch (B b){}");
  }
  
}