/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.cocos;

import de.monticore.statements.mccommonstatements._symboltable.IMCCommonStatementsArtifactScope;
import de.monticore.statements.mccommonstatements._symboltable.MCCommonStatementsSymbols2Json;
import de.monticore.statements.mccommonstatements.cocos.SwitchStatementValid;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.monticore.statements.testmccommonstatements.TestMCCommonStatementsMill;
import de.monticore.statements.testmccommonstatements._cocos.TestMCCommonStatementsCoCoChecker;
import de.monticore.statements.testmccommonstatements._parser.TestMCCommonStatementsParser;
import de.monticore.statements.testmccommonstatements._visitor.TestMCCommonStatementsTraverser;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.types.check.FlatExpressionScopeSetter;
import de.monticore.types.check.FullDeriveFromCombineExpressionsWithLiterals;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.TypeCalculator;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

public class SwitchStatementValidTest {
  
  protected TestMCCommonStatementsCoCoChecker checker;
  
  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    TestMCCommonStatementsMill.reset();
    TestMCCommonStatementsMill.init();
    BasicSymbolsMill.initializePrimitives();
    checker = new TestMCCommonStatementsCoCoChecker();
    checker.addCoCo(new SwitchStatementValid(new TypeCalculator(null,new FullDeriveFromCombineExpressionsWithLiterals())));
  }
  
  public void checkValid(String expressionString) throws IOException {
    TestMCCommonStatementsParser parser = new TestMCCommonStatementsParser();
    Optional<ASTMCBlockStatement> optAST = parser.parse_StringMCBlockStatement(expressionString);
    Assertions.assertTrue(optAST.isPresent());
    Log.getFindings().clear();
    checker.checkAll(optAST.get());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  public void checkInvalid(String expressionString) throws IOException {
    TestMCCommonStatementsParser parser = new TestMCCommonStatementsParser();
    Optional<ASTMCBlockStatement> optAST = parser.parse_StringMCBlockStatement(expressionString);
    Assertions.assertTrue(optAST.isPresent());
    Log.getFindings().clear();
    checker.checkAll(optAST.get());
    Assertions.assertFalse(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testValid() throws IOException {
    checkValid("switch(5){}");
    checkValid("switch('c'){}");
  }
  
  @Test
  public void testInvalid() throws IOException {
    checkInvalid("switch(5.5){}");
    checkInvalid("switch(5.5F){}");
    checkInvalid("switch(false){}");
  }

  @Test
  public void testSwitchEnumConstants() throws IOException {
    IMCCommonStatementsArtifactScope imported
        = new MCCommonStatementsSymbols2Json().load("src/test/resources/de/monticore/statements/Enum.sym");

    TestMCCommonStatementsMill
        .globalScope()
        .addSubScope(imported);

    VariableSymbol variable1 = TestMCCommonStatementsMill.variableSymbolBuilder()
        .setName("c")
        .setType(SymTypeExpressionFactory.createTypeObject(imported.resolveOOType("A").get()))
        .build();
    TestMCCommonStatementsMill.globalScope().getVariableSymbols().put(variable1.getName() , variable1);

    VariableSymbol variable2 = TestMCCommonStatementsMill.variableSymbolBuilder()
        .setName("d")
        .setType(SymTypeExpressionFactory.createTypeObject(imported.resolveOOType("B").get()))
        .build();
    TestMCCommonStatementsMill.globalScope().getVariableSymbols().put(variable2.getName() , variable2);

    TestMCCommonStatementsParser parser = TestMCCommonStatementsMill.parser();
    Optional<ASTMCBlockStatement> optAST1 = parser.parse_StringMCBlockStatement("switch(c){}");
    Assertions.assertTrue(optAST1.isPresent());
    ASTMCBlockStatement ast1 = optAST1.get();

    TestMCCommonStatementsTraverser traverser = TestMCCommonStatementsMill.traverser();
    traverser.add4ExpressionsBasis(new FlatExpressionScopeSetter(TestMCCommonStatementsMill.globalScope()));
    ast1.accept(traverser);

    Log.getFindings().clear();
    checker.checkAll(ast1);
    Assertions.assertTrue(Log.getFindings().isEmpty());

    Optional<ASTMCBlockStatement> optAST2 = parser.parse_StringMCBlockStatement("switch(d){}");
    Assertions.assertTrue(optAST2.isPresent());
    ASTMCBlockStatement ast2 = optAST2.get();

    ast2.accept(traverser);

    checker.checkAll(ast2);
    Assertions.assertEquals(1, Log.getFindings().size());
    Assertions.assertTrue(Log.getFindings().get(0).getMsg().startsWith(SwitchStatementValid.ERROR_CODE));
  }
}