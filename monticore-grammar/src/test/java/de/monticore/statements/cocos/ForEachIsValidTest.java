/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.cocos;

import de.monticore.grammar.cocos.CocoTest;
import de.monticore.statements.mccommonstatements._ast.ASTEnhancedForControl;
import de.monticore.statements.mccommonstatements._cocos.MCCommonStatementsCoCoChecker;
import de.monticore.statements.mccommonstatements.cocos.ForEachIsValid;
import de.monticore.statements.testmccommonstatements.TestMCCommonStatementsMill;
import de.monticore.statements.testmccommonstatements._parser.TestMCCommonStatementsParser;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.DeriveSymTypeOfCombineExpressionsDelegator;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfObject;
import de.monticore.types.check.TypeCheck;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

public class ForEachIsValidTest extends CocoTest {

  private static final MCCommonStatementsCoCoChecker checker = new MCCommonStatementsCoCoChecker();
  
  @BeforeClass
  public static void disableFailQuick(){
  
    Log.enableFailQuick(false);
    TestMCCommonStatementsMill.reset();
    TestMCCommonStatementsMill.init();
    BasicSymbolsMill.initializePrimitives();
    checker.addCoCo(new ForEachIsValid(new TypeCheck(null,new DeriveSymTypeOfCombineExpressionsDelegator())));
  
    SymTypeOfObject sType = SymTypeExpressionFactory.createTypeObject("java.util.ArrayList", TestMCCommonStatementsMill.globalScope());
    SymTypeOfObject sTypeA = SymTypeExpressionFactory.createTypeObject("A", TestMCCommonStatementsMill.globalScope());
    TestMCCommonStatementsMill.globalScope().add(TestMCCommonStatementsMill.oOTypeSymbolBuilder().setName("A").addSuperTypes(sType).build());
    TestMCCommonStatementsMill.globalScope().add(TestMCCommonStatementsMill.oOTypeSymbolBuilder().setName("java.util.ArrayList").build());
    TestMCCommonStatementsMill.globalScope().add(TestMCCommonStatementsMill.fieldSymbolBuilder().setName("a").setType(sTypeA).build());
  
    SymTypeOfObject sTypeS = SymTypeExpressionFactory.createTypeObject("java.lang.Object", TestMCCommonStatementsMill.globalScope());
    TestMCCommonStatementsMill.globalScope().add(TestMCCommonStatementsMill.oOTypeSymbolBuilder().setName("java.lang.Object").build());
    TestMCCommonStatementsMill.globalScope().add(TestMCCommonStatementsMill.fieldSymbolBuilder().setName("o").setType(sTypeS).build());
  
  }
  
  public void checkValid(String expressionString) throws IOException {
    
      TestMCCommonStatementsParser parser = new TestMCCommonStatementsParser();
      Optional<ASTEnhancedForControl> optAST = parser.parse_StringEnhancedForControl(expressionString);
      assertTrue(optAST.isPresent());
      ASTEnhancedForControl ast = optAST.get();
      ast.setEnclosingScope(TestMCCommonStatementsMill.globalScope());
      ast.getExpression().setEnclosingScope(TestMCCommonStatementsMill.globalScope());
      Log.getFindings().clear();
      checker.checkAll(optAST.get());
      assertTrue(Log.getFindings().isEmpty());
    
  }
  
  @Test
  public void testValid() throws IOException {
  
    // checkValid("Object o : a");
    
  }
}