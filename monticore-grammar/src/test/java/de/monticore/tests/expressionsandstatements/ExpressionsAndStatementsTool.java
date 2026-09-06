// (c) https://github.com/MontiCore/monticore
package de.monticore.tests.expressionsandstatements;

import de.monticore.expressions.assignmentexpressions._cocos.AssignmentExpressionsASTAssignmentExpressionCoCo;
import de.monticore.expressions.assignmentexpressions.cocos.AssignmentExpressionsOnlyAssignToLValuesCoCo;
import de.monticore.expressions.cocos.ExpressionValid;
import de.monticore.expressions.lambdaexpressions._symboltable.LambdaExpressionsSTCompleteTypes2;
import de.monticore.ocl.oclexpressions.cocos.IterateExpressionVariableUsageIsCorrect;
import de.monticore.ocl.oclexpressions.symboltable.OCLExpressionsSymbolTableCompleter;
import de.monticore.ocl.setexpressions.cocos.SetComprehensionHasGenerator;
import de.monticore.ocl.setexpressions.symboltable.SetExpressionsSymbolTableCompleter;
import de.monticore.regex.regularexpressions.cocos.RangeHasLowerOrUpperBound;
import de.monticore.statements.mccommonstatements._symboltable.MCCommonStatementsSymTabCompletion;
import de.monticore.statements.mccommonstatements.cocos.*;
import de.monticore.statements.mcvardeclarationstatements._cocos.VarDeclarationInitializationHasCorrectType;
import de.monticore.statements.mcvardeclarationstatements._cocos.VarDeclarationNameAlreadyDefinedInScope;
import de.monticore.statements.mcvardeclarationstatements._symboltable.MCVarDeclarationStatementsSymTabCompletion;
import de.monticore.tests.expressionsandstatements._ast.ASTBehaviorInput;
import de.monticore.tests.expressionsandstatements._cocos.ExpressionsAndStatementsCoCoChecker;
import de.monticore.tests.expressionsandstatements._visitor.ExpressionsAndStatementsTraverser;
import de.monticore.types.mcbasictypes.cocos.QualifiedTypeHasNoTypeParameters;

public class ExpressionsAndStatementsTool
    extends ExpressionsAndStatementsToolTOP {

  @Override
  public void completeSymbolTable(ASTBehaviorInput node) {
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

    node.accept(symTabCompleter);
  }

  public void runDefaultCoCos(ASTBehaviorInput ast) {
    ExpressionsAndStatementsCoCoChecker checker = new ExpressionsAndStatementsCoCoChecker();
    checker.addCoCo(new DoWhileConditionHasBooleanType());
    checker.addCoCo(new ExpressionStatementIsValid());
    checker.addCoCo(new ForConditionHasBooleanType());
    checker.addCoCo(new ForEachIsValid());
    checker.addCoCo(new IfConditionHasBooleanType());
    checker.addCoCo(new SwitchStatementValid());
    checker.addCoCo(new SynchronizedArgIsReftype());
    checker.addCoCo(new WhileConditionHasBooleanType());
    checker.addCoCo(new AssertIsValid());
    checker.addCoCo(new CatchIsValid());
    checker.addCoCo(new ThrowIsValid());
    checker.addCoCo(new ResourceInTryStatementCloseable());
    checker.addCoCo(new ExpressionValid());
    checker.addCoCo(new IterateExpressionVariableUsageIsCorrect());
    checker.addCoCo(new VarDeclarationInitializationHasCorrectType());
    checker.addCoCo(new VarDeclarationNameAlreadyDefinedInScope());
    checker.addCoCo((AssignmentExpressionsASTAssignmentExpressionCoCo) new AssignmentExpressionsOnlyAssignToLValuesCoCo());
    checker.addCoCo(new IterateExpressionVariableUsageIsCorrect());
    checker.addCoCo(new SetComprehensionHasGenerator());
    checker.addCoCo(new RangeHasLowerOrUpperBound());
    checker.addCoCo(new QualifiedTypeHasNoTypeParameters());
    checker.checkAll(ast);
  }

}
