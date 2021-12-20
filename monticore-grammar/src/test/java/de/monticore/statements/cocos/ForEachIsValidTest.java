/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.cocos;

import de.monticore.grammar.cocos.CocoTest;
import de.monticore.statements.mccommonstatements._ast.ASTEnhancedForControl;
import de.monticore.statements.mccommonstatements._cocos.MCCommonStatementsCoCoChecker;
import de.monticore.statements.mccommonstatements.cocos.ForEachIsValid;
import de.monticore.statements.testmccommonstatements.TestMCCommonStatementsMill;
import de.monticore.statements.testmccommonstatements._parser.TestMCCommonStatementsParser;
import de.monticore.statements.testmccommonstatements._symboltable.ITestMCCommonStatementsScope;
import de.monticore.statements.testmccommonstatements._visitor.TestMCCommonStatementsTraverser;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.*;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ForEachIsValidTest extends CocoTest {

  private static final MCCommonStatementsCoCoChecker checker = new MCCommonStatementsCoCoChecker();
  
  @BeforeClass
  public static void disableFailQuick(){
  
    Log.enableFailQuick(false);
    TestMCCommonStatementsMill.reset();
    TestMCCommonStatementsMill.init();
    BasicSymbolsMill.initializePrimitives();
    checker.addCoCo(new ForEachIsValid(new TypeCheck(new SynthesizeSymTypeFromCombineExpressionsWithLiteralsDelegator(), new DeriveSymTypeOfCombineExpressionsDelegator())));
  
    SymTypeOfObject sType = SymTypeExpressionFactory.createTypeObject("java.lang.Iterable", TestMCCommonStatementsMill.globalScope());
    SymTypeOfObject sTypeA = SymTypeExpressionFactory.createTypeObject("A", TestMCCommonStatementsMill.globalScope());
    TestMCCommonStatementsMill.globalScope().add(TestMCCommonStatementsMill.oOTypeSymbolBuilder().setName("A").addSuperTypes(sType).build());
    TestMCCommonStatementsMill.globalScope().add(TestMCCommonStatementsMill.oOTypeSymbolBuilder().setName("java.lang.Iterable").build());
    TestMCCommonStatementsMill.globalScope().add(TestMCCommonStatementsMill.oOTypeSymbolBuilder().setName("java.util.Arrays").build());
    TestMCCommonStatementsMill.globalScope().add(TestMCCommonStatementsMill.fieldSymbolBuilder().setName("a").setType(sTypeA).build());
  
    SymTypeOfObject sTypeS = SymTypeExpressionFactory.createTypeObject("Object", TestMCCommonStatementsMill.globalScope());
    TestMCCommonStatementsMill.globalScope().add(TestMCCommonStatementsMill.oOTypeSymbolBuilder().setName("Object").build());
    TestMCCommonStatementsMill.globalScope().add(TestMCCommonStatementsMill.fieldSymbolBuilder().setName("o").setType(sTypeS).build());
  
  }
  
  private void addToTraverser(TestMCCommonStatementsTraverser traverser, ITestMCCommonStatementsScope enclosingScope) {
    FlatExpressionScopeSetter flatExpressionScopeSetter = new FlatExpressionScopeSetter(enclosingScope);
    traverser.add4ExpressionsBasis(flatExpressionScopeSetter);
    traverser.add4CommonExpressions(flatExpressionScopeSetter);
    traverser.add4MCBasicTypes(flatExpressionScopeSetter);
    traverser.add4MCCollectionTypes(flatExpressionScopeSetter);
    traverser.add4MCArrayTypes(flatExpressionScopeSetter);
  }
  
  private TestMCCommonStatementsTraverser flatExpressionScopeSetterTraverser;
  
  public void checkValid(String expressionString) throws IOException {
    
    TestMCCommonStatementsParser parser = new TestMCCommonStatementsParser();
    Optional<ASTEnhancedForControl> optAST = parser.parse_StringEnhancedForControl(expressionString);
    assertTrue(optAST.isPresent());
    ASTEnhancedForControl ast = optAST.get();
  
    TestMCCommonStatementsTraverser traverser = TestMCCommonStatementsMill.traverser();
    addToTraverser(traverser, TestMCCommonStatementsMill.globalScope());
    ast.accept(traverser);
    ast.setEnclosingScope(TestMCCommonStatementsMill.globalScope());

    Log.getFindings().clear();
    checker.checkAll(optAST.get());
    assertTrue(Log.getFindings().isEmpty());
    
  }
  
  public void checkInvalid(String expressionString) throws IOException {
    
    TestMCCommonStatementsParser parser = new TestMCCommonStatementsParser();
    Optional<ASTEnhancedForControl> optAST = parser.parse_StringEnhancedForControl(expressionString);
    assertTrue(optAST.isPresent());
    ASTEnhancedForControl ast = optAST.get();
    
    TestMCCommonStatementsTraverser traverser = TestMCCommonStatementsMill.traverser();
    addToTraverser(traverser, TestMCCommonStatementsMill.globalScope());
    ast.accept(traverser);
  
    ast.setEnclosingScope(TestMCCommonStatementsMill.globalScope());
    Log.getFindings().clear();
    checker.checkAll(optAST.get());
    assertFalse(Log.getFindings().isEmpty());
    
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