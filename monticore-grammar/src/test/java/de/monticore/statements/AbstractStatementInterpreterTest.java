package de.monticore.statements;

import de.monticore.AbstractInterpreterTest;
import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._symboltable.ICombineExpressionsWithLiteralsArtifactScope;
import de.monticore.expressions.lambdaexpressions._symboltable.LambdaExpressionsSTCompleteTypes2;
import de.monticore.interpreter.MIValue;
import de.monticore.io.paths.MCPath;
import de.monticore.ocl.oclexpressions.symboltable.OCLExpressionsSymbolTableCompleter;
import de.monticore.ocl.setexpressions.symboltable.SetExpressionsSymbolTableCompleter;
import de.monticore.statements.combinestatementswithexpressions.CombineStatementsWithExpressionsMill;
import de.monticore.statements.combinestatementswithexpressions._parser.CombineStatementsWithExpressionsParser;
import de.monticore.statements.combinestatementswithexpressions._symboltable.CombineStatementsWithExpressionsScopesGenitorDelegator;
import de.monticore.statements.combinestatementswithexpressions._symboltable.ICombineStatementsWithExpressionsArtifactScope;
import de.monticore.statements.combinestatementswithexpressions._visitor.CombineStatementsWithExpressionsInterpreter;
import de.monticore.statements.combinestatementswithexpressions._visitor.CombineStatementsWithExpressionsTraverser;
import de.monticore.statements.mccommonstatements._symboltable.MCCommonStatementsSymTabCompletion;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCStatement;
import de.monticore.statements.mcvardeclarationstatements._symboltable.MCVarDeclarationStatementsSymTabCompletion;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.check.IDerive;
import de.monticore.types.check.ISynthesize;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.types3wrapper.TypeCheck3AsIDerive;
import de.monticore.types.check.types3wrapper.TypeCheck3AsISynthesize;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.TypeCheck3;
import de.monticore.types3.util.DefsTypesForTests;
import de.monticore.visitor.ITraverser;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public abstract class AbstractStatementInterpreterTest extends AbstractInterpreterTest {
  
  @Override
  @BeforeEach
  public void init() {
    parserSupplier = CombineStatementsWithExpressionsMill::parser;
    resetMill = CombineStatementsWithExpressionsMill::reset;
    initMill = CombineStatementsWithExpressionsMill::init;
    
    super.init();
    
    interpreter = new CombineStatementsWithExpressionsInterpreter();
  }
  
  protected MIValue testValidModel(String model) {
    return testValidModel(model, Collections.emptyList());
  }
  
  protected MIValue testValidModel(String model, List<ImportStatement> imports) {
    Log.clearFindings();
    Optional<ASTMCBlockStatement> astNodeOpt = Optional.empty();
    try {
      astNodeOpt = ((CombineStatementsWithExpressionsParser)parser).parse_String(model);
    } catch (IOException e) {
      System.out.println(e.getMessage());
      fail();
    }
    
    if (!Log.getFindings().isEmpty()) {
      Log.printFindings();
      fail();
    }
    assertTrue(astNodeOpt.isPresent());
    
    CombineStatementsWithExpressionsMill.artifactScope().setImportsList(imports);
    
    OOClass2MCResolver resolver = new OOClass2MCResolver();
    
    OOSymbolsMill.reset();
    OOSymbolsMill.init();
    CombineStatementsWithExpressionsMill.reset();
    CombineStatementsWithExpressionsMill.init();
    CombineStatementsWithExpressionsMill.globalScope().clear();
    
    DefsTypesForTests.setup();
    
    CombineStatementsWithExpressionsMill.globalScope().getSymbolPath().addEntry(OOClass2MCResolver.getJRTPath());
    CombineStatementsWithExpressionsMill.globalScope().addAdaptedTypeSymbolResolver(resolver);
    CombineStatementsWithExpressionsMill.globalScope().addAdaptedOOTypeSymbolResolver(resolver);
    
    CombineStatementsWithExpressionsMill.globalScope().setSymbolPath(new MCPath());
    
    ICombineStatementsWithExpressionsArtifactScope rootScope =
        CombineStatementsWithExpressionsMill.scopesGenitorDelegator()
            .createFromAST(astNodeOpt.get());
    
    rootScope.setName("root");
    
    astNodeOpt.get().accept(getSymbolTableCompleter());
    
    MIValue interpretationResult = astNodeOpt.get().evaluate(interpreter);
    
    assertNotNull(interpretationResult);
    if (!Log.getFindings().isEmpty()) {
      Log.printFindings();
      fail();
    }
    
    assertTrue(interpretationResult.isReturn() || interpretationResult.isVoid());
    if (interpretationResult.isReturn()) {
      interpretationResult = interpretationResult.asReturnValue();
    }
    
    return interpretationResult;
  }
  
  protected void addImports(List<ImportStatement> imports) {
    CombineStatementsWithExpressionsMill.artifactScope().setImportsList(imports);
  }
  
  protected void setupSymbolTableCompleter(
      ITraverser typeMapTraverser, Type4Ast type4Ast) {
    CombineStatementsWithExpressionsTraverser combinedScopesCompleter =
        CombineStatementsWithExpressionsMill.traverser();
    IDerive deriver = new TypeCheck3AsIDerive();
    ISynthesize synthesizer = new TypeCheck3AsISynthesize();
    combinedScopesCompleter.add4LambdaExpressions(
        new LambdaExpressionsSTCompleteTypes2(
            typeMapTraverser,
            getType4Ast()
        )
    );
    OCLExpressionsSymbolTableCompleter oclExprCompleter =
        new OCLExpressionsSymbolTableCompleter();
    oclExprCompleter.setDeriver(deriver);
    oclExprCompleter.setSynthesizer(synthesizer);
    combinedScopesCompleter.add4OCLExpressions(oclExprCompleter);
    combinedScopesCompleter.setOCLExpressionsHandler(oclExprCompleter);
    
    SetExpressionsSymbolTableCompleter setExprCompleter =
        new SetExpressionsSymbolTableCompleter();
    setExprCompleter.setDeriver(deriver);
    setExprCompleter.setSynthesizer(synthesizer);
    combinedScopesCompleter.add4SetExpressions(setExprCompleter);
    
    MCVarDeclarationStatementsSymTabCompletion varDeclCompleter =
        new MCVarDeclarationStatementsSymTabCompletion();
    combinedScopesCompleter.add4MCVarDeclarationStatements(varDeclCompleter);
    
    MCCommonStatementsSymTabCompletion commonStmtCompleter =
        new MCCommonStatementsSymTabCompletion();
    combinedScopesCompleter.add4MCCommonStatements(commonStmtCompleter);
    
    combinedScopesCompleter.setSetExpressionsHandler(setExprCompleter);
    symbolTableCompleter = combinedScopesCompleter;
    scopeGenitor = combinedScopesCompleter;
  }
  
}
