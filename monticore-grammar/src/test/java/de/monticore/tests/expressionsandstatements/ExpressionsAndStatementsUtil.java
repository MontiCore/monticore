// (c) https://github.com/MontiCore/monticore
package de.monticore.tests.expressionsandstatements;

import de.monticore.class2mc.OOClass2MCResolver;
import de.monticore.expressions.assignmentexpressions._cocos.AssignmentExpressionsASTAssignmentExpressionCoCo;
import de.monticore.expressions.assignmentexpressions.cocos.AssignmentExpressionsOnlyAssignToLValuesCoCo;
import de.monticore.expressions.cocos.ExpressionValid;
import de.monticore.expressions.lambdaexpressions._symboltable.LambdaExpressionsSTCompleteTypes2;
import de.monticore.ocl.oclexpressions.cocos.IterateExpressionVariableUsageIsCorrect;
import de.monticore.ocl.oclexpressions.symboltable.OCLExpressionsSymbolTableCompleter;
import de.monticore.ocl.setexpressions.cocos.SetComprehensionHasGenerator;
import de.monticore.ocl.setexpressions.symboltable.SetExpressionsSymbolTableCompleter;
import de.monticore.statements.mccommonstatements._symboltable.MCCommonStatementsSymTabCompletion;
import de.monticore.statements.mccommonstatements.cocos.AssertIsValid;
import de.monticore.statements.mccommonstatements.cocos.DoWhileConditionHasBooleanType;
import de.monticore.statements.mccommonstatements.cocos.ExpressionStatementIsValid;
import de.monticore.statements.mccommonstatements.cocos.ForConditionHasBooleanType;
import de.monticore.statements.mccommonstatements.cocos.ForEachIsValid;
import de.monticore.statements.mccommonstatements.cocos.IfConditionHasBooleanType;
import de.monticore.statements.mccommonstatements.cocos.SwitchStatementValid;
import de.monticore.statements.mccommonstatements.cocos.WhileConditionHasBooleanType;
import de.monticore.statements.mcvardeclarationstatements._cocos.VarDeclarationInitializationHasCorrectType;
import de.monticore.statements.mcvardeclarationstatements._cocos.VarDeclarationNameAlreadyDefinedInScope;
import de.monticore.statements.mcvardeclarationstatements._symboltable.MCVarDeclarationStatementsSymTabCompletion;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.oosymbols.OOSymbolsMill;
import de.monticore.symbols.oosymbols._symboltable.IOOSymbolsGlobalScope;
import de.monticore.symboltable.ImportStatement;
import de.monticore.tests.expressionsandstatements._ast.ASTBehaviorInput;
import de.monticore.tests.expressionsandstatements._cocos.ExpressionsAndStatementsCoCoChecker;
import de.monticore.tests.expressionsandstatements._symboltable.IExpressionsAndStatementsArtifactScope;
import de.monticore.tests.expressionsandstatements._visitor.ExpressionsAndStatementsTraverser;
import de.monticore.tests.expressionsandstatements.types3.ExpressionsAndStatementsTypeCheck3;
import de.monticore.types.mcbasictypes.cocos.QualifiedTypeHasNoTypeParameters;

import java.io.IOException;
import java.util.Optional;

import static de.monticore.runtime.junit.MCAssertions.assertNoFindings;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Provides simplified access to parsing/CoCo-checks/SymTab-Generierung/etc.
 * specifically for tests.
 */
public class ExpressionsAndStatementsUtil {

  public static void init() {
    // Mill
    ExpressionsAndStatementsMill.reset();
    ExpressionsAndStatementsMill.init();
    ExpressionsAndStatementsMill.globalScope().clear();

    // TypeCheck
    ExpressionsAndStatementsTypeCheck3.init();

    // Symbols
    BasicSymbolsMill.initializePrimitives();
    // Class2MC
    IOOSymbolsGlobalScope globalScope = OOSymbolsMill.globalScope();
    OOClass2MCResolver resolver = new OOClass2MCResolver();
    globalScope.addAdaptedOOTypeSymbolResolver(resolver);
    globalScope.addAdaptedTypeSymbolResolver(resolver);
  }

  /**
   * Given a model, this provides an AST with symbol table.
   * The CoCos are already checked.
   *
   * @param modelStr the model to create an AST of
   * @return the AST with corresponding symbol table
   */
  public static ASTBehaviorInput getPreparedAST(String modelStr) {
    ASTBehaviorInput ast = parse(modelStr);
    runSymTabGenitor(ast);
    runSymTabCompleter(ast);
    runContextConditionChecks(ast);
    return ast;
  }

  public static ASTBehaviorInput parse(String modelStr) {
    try {
      Optional<ASTBehaviorInput> astOpt =
          ExpressionsAndStatementsMill.parser().parse_String(modelStr);
      assertNoFindings();
      assertTrue(astOpt.isPresent());
      return astOpt.get();
    }
    catch (IOException e) {
      fail(e);
      return null;
    }
  }

  public static IExpressionsAndStatementsArtifactScope runSymTabGenitor(
      ASTBehaviorInput ast
  ) {
    IExpressionsAndStatementsArtifactScope scope =
        ExpressionsAndStatementsMill.scopesGenitorDelegator().createFromAST(ast);
    assertNoFindings();
    // default import
    scope.addImports(new ImportStatement("java.lang", true));
    scope.addImports(new ImportStatement("java.util", true));
    return scope;
  }

  public static void runSymTabCompleter(
      ASTBehaviorInput ast
  ) {
    ExpressionsAndStatementsTraverser symTabCompleter =
        ExpressionsAndStatementsMill.inheritanceTraverser();

    // Expressions

    OCLExpressionsSymbolTableCompleter oclExprCompleter =
        new OCLExpressionsSymbolTableCompleter();
    symTabCompleter.setOCLExpressionsHandler(oclExprCompleter);
    symTabCompleter.add4BasicSymbols(oclExprCompleter);
    symTabCompleter.add4OCLExpressions(oclExprCompleter);

    SetExpressionsSymbolTableCompleter setExprCompleter =
        new SetExpressionsSymbolTableCompleter();
    symTabCompleter.setSetExpressionsHandler(setExprCompleter);
    symTabCompleter.add4BasicSymbols(setExprCompleter);
    symTabCompleter.add4SetExpressions(setExprCompleter);

    LambdaExpressionsSTCompleteTypes2 lambdaExprCompleter =
        new LambdaExpressionsSTCompleteTypes2();
    symTabCompleter.add4LambdaExpressions(lambdaExprCompleter);

    // Statements

    MCCommonStatementsSymTabCompletion commonStatementsCompleter =
        new MCCommonStatementsSymTabCompletion();
    symTabCompleter.add4MCCommonStatements(commonStatementsCompleter);

    MCVarDeclarationStatementsSymTabCompletion mcVarDeclarationStatementsCompleter =
        new MCVarDeclarationStatementsSymTabCompletion();
    symTabCompleter.add4MCVarDeclarationStatements(mcVarDeclarationStatementsCompleter);

    ast.accept(symTabCompleter);
    assertNoFindings();
  }

  public static void runContextConditionChecks(
      ASTBehaviorInput ast
  ) {
    ExpressionsAndStatementsCoCoChecker checker = new ExpressionsAndStatementsCoCoChecker();
    checker.addCoCo(new DoWhileConditionHasBooleanType());
    checker.addCoCo(new ExpressionStatementIsValid());
    checker.addCoCo(new ForConditionHasBooleanType());
    checker.addCoCo(new ForEachIsValid());
    checker.addCoCo(new IfConditionHasBooleanType());
    checker.addCoCo(new SwitchStatementValid());
    //checker.addCoCo(new SynchronizedArgIsReftype());
    checker.addCoCo(new WhileConditionHasBooleanType());
    checker.addCoCo(new AssertIsValid());
    //checker.addCoCo(new CatchIsValid());
    //checker.addCoCo(new ThrowIsValid());
    //checker.addCoCo(new ResourceInTryStatementCloseable());
    checker.addCoCo(new ExpressionValid());
    checker.addCoCo(new IterateExpressionVariableUsageIsCorrect());
    checker.addCoCo(new VarDeclarationInitializationHasCorrectType());
    checker.addCoCo(new VarDeclarationNameAlreadyDefinedInScope());
    checker.addCoCo((AssignmentExpressionsASTAssignmentExpressionCoCo) new AssignmentExpressionsOnlyAssignToLValuesCoCo());
    checker.addCoCo(new IterateExpressionVariableUsageIsCorrect());
    checker.addCoCo(new SetComprehensionHasGenerator());
    //checker.addCoCo(new RangeHasLowerOrUpperBound());
    checker.addCoCo(new QualifiedTypeHasNoTypeParameters());
    //checker.addCoCo(new TypeParameterNoCyclicInheritance());
    //checker.addCoCo(new TypeParametersHaveUniqueNames());
    checker.checkAll(ast);
    assertNoFindings();
  }

}
