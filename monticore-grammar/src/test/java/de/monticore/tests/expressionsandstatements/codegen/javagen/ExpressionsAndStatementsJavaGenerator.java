// (c) https://github.com/MontiCore/monticore
package de.monticore.tests.expressionsandstatements.codegen.javagen;

import de.monticore.codegen.TraverserBasedCodeGenerator;
import de.monticore.codegen.javagen.JavaGenSymTypeExpressionConverter;
import de.monticore.codegen.javagen.JavaGenVisitorState;
import de.monticore.codegen.javagen.JavaOperationPrinter;
import de.monticore.codegen.javagen.SymTypeExpression2JavaConverter;
import de.monticore.expressions.assignmentexpressions.codegen.javagen.AssignmentExpressionsJavaGenVisitor;
import de.monticore.expressions.bitexpressions.codegen.javagen.BitExpressionsJavaGenVisitor;
import de.monticore.expressions.commonexpressions.codegen.javagen.CommonExpressionsJavaGenVisitor;
import de.monticore.expressions.expressionsbasis.codegen.javagen.ExpressionsBasisJavaGenVisitor;
import de.monticore.expressions.lambdaexpressions.codegen.javagen.LambdaExpressionsJavaGenVisitor;
import de.monticore.expressions.tupleexpressions.codegen.javagen.TupleExpressionsJavaGenVisitor;
import de.monticore.expressions.uglyexpressions.codegen.javagen.UglyExpressionsJavaGenVisitor;
import de.monticore.literals.mccommonliterals.codegen.javagen.MCCommonLiteralsJavaGenVisitor;
import de.monticore.ocl.oclexpressions.codegen.javagen.OCLExpressionsJavaGenVisitor;
import de.monticore.ocl.optionaloperators.codegen.javagen.OptionalOperatorsJavaGenVisitor;
import de.monticore.ocl.setexpressions.codegen.javagen.SetExpressionsJavaGenVisitor;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mcassertstatements.codegen.javagen.MCAssertStatementsJavaGenVisitor;
import de.monticore.statements.mccommonstatements.codegen.javagen.MCCommonStatementsJavaGenVisitor;
import de.monticore.statements.mclowlevelstatements.codegen.javagen.MCLowLevelStatementsJavaGenVisitor;
import de.monticore.statements.mcreturnstatements.codegen.javagen.MCReturnStatementsJavaGenVisitor;
import de.monticore.statements.mcvardeclarationstatements.codegen.javagen.MCVarDeclarationStatementsJavaGenVisitor;
import de.monticore.tests.expressionsandstatements.ExpressionsAndStatementsMill;
import de.monticore.tests.expressionsandstatements._visitor.ExpressionsAndStatementsTraverser;
import de.monticore.types.mcbasictypes.codegen.javagen.MCBasicTypesJavaGenVisitor;
import de.monticore.visitor.ITraverser;

public class ExpressionsAndStatementsJavaGenerator
    implements TraverserBasedCodeGenerator {

  protected ExpressionsAndStatementsTraverser traverser;

  protected JavaGenVisitorState state;

  public ExpressionsAndStatementsJavaGenerator() {
    init();
  }

  protected void init() {
    JavaGenSymTypeExpressionConverter.init();
    JavaOperationPrinter.init();
    SymTypeExpression2JavaConverter.init();

    this.state = new JavaGenVisitorState(new IndentPrinter());
    this.traverser = ExpressionsAndStatementsMill.inheritanceTraverser();

    ExpressionsAndStatementsJavaGenVisitor visExpressionsAndStatements =
        new ExpressionsAndStatementsJavaGenVisitor(state);
    traverser.setExpressionsAndStatementsHandler(visExpressionsAndStatements);

    // Literals

    MCCommonLiteralsJavaGenVisitor visMCCommonLiterals =
        new MCCommonLiteralsJavaGenVisitor(state);
    traverser.setMCCommonLiteralsHandler(visMCCommonLiterals);

    // Expressions

    AssignmentExpressionsJavaGenVisitor visAssignmentExpressions =
        new AssignmentExpressionsJavaGenVisitor(state);
    traverser.setAssignmentExpressionsHandler(visAssignmentExpressions);

    BitExpressionsJavaGenVisitor visBitExpressions =
        new BitExpressionsJavaGenVisitor(state);
    traverser.setBitExpressionsHandler(visBitExpressions);

    CommonExpressionsJavaGenVisitor visCommonExpressions =
        new CommonExpressionsJavaGenVisitor(state);
    traverser.setCommonExpressionsHandler(visCommonExpressions);

    ExpressionsBasisJavaGenVisitor visExpressionBasis =
        new ExpressionsBasisJavaGenVisitor(state);
    traverser.setExpressionsBasisHandler(visExpressionBasis);

    LambdaExpressionsJavaGenVisitor visLambdaExpressions =
        new LambdaExpressionsJavaGenVisitor(state);
    traverser.setLambdaExpressionsHandler(visLambdaExpressions);

    TupleExpressionsJavaGenVisitor visTupleExpressions =
        new TupleExpressionsJavaGenVisitor(state);
    traverser.setTupleExpressionsHandler(visTupleExpressions);

    OCLExpressionsJavaGenVisitor visOCLExpressions =
        new OCLExpressionsJavaGenVisitor(state);
    traverser.setOCLExpressionsHandler(visOCLExpressions);

    OptionalOperatorsJavaGenVisitor visOptionalOperators =
        new OptionalOperatorsJavaGenVisitor(state);
    traverser.setOptionalOperatorsHandler(visOptionalOperators);

    SetExpressionsJavaGenVisitor visSetExpressions =
        new SetExpressionsJavaGenVisitor(state);
    traverser.setSetExpressionsHandler(visSetExpressions);

    UglyExpressionsJavaGenVisitor visUglyExpressions =
        new UglyExpressionsJavaGenVisitor(state);
    traverser.setUglyExpressionsHandler(visUglyExpressions);

    // Statements

    MCAssertStatementsJavaGenVisitor visMCAssertStatements =
        new MCAssertStatementsJavaGenVisitor(state);
    traverser.setMCAssertStatementsHandler(visMCAssertStatements);

    MCCommonStatementsJavaGenVisitor visMCCommonStatements =
        new MCCommonStatementsJavaGenVisitor(state);
    traverser.setMCCommonStatementsHandler(visMCCommonStatements);

    MCLowLevelStatementsJavaGenVisitor visMCLowLevelStatements =
        new MCLowLevelStatementsJavaGenVisitor(state);
    traverser.setMCLowLevelStatementsHandler(visMCLowLevelStatements);

    MCReturnStatementsJavaGenVisitor visMCReturnStatements =
        new MCReturnStatementsJavaGenVisitor(state);
    traverser.setMCReturnStatementsHandler(visMCReturnStatements);

    MCVarDeclarationStatementsJavaGenVisitor visMCVarDeclarationStatements =
        new MCVarDeclarationStatementsJavaGenVisitor(state);
    traverser.setMCVarDeclarationStatementsHandler(visMCVarDeclarationStatements);

    // Types

    MCBasicTypesJavaGenVisitor visMCBasicTypes = new MCBasicTypesJavaGenVisitor(state);
    traverser.setMCBasicTypesHandler(visMCBasicTypes);
    traverser.add4MCBasicTypes(visMCBasicTypes);
  }

  @Override
  public ITraverser getTraverser() {
    return traverser;
  }

  @Override
  public IndentPrinter getPrinter() {
    return state.getPrinter();
  }

  public JavaGenVisitorState getSharedState() {
    return state;
  }

}
