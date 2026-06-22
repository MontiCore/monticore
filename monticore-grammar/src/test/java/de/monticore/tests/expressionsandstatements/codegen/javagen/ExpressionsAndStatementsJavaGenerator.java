// (c) https://github.com/MontiCore/monticore
package de.monticore.tests.expressionsandstatements.codegen.javagen;

import de.monticore.codegen.javagen.AbstractJavaGenVisitor;
import de.monticore.codegen.javagen.JavaGenSymTypeExpressionConverter;
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
import de.monticore.statements.mcvardeclarationstatements.codegen.javagen.MCVarDeclarationStatementsJavaGenVisitor;
import de.monticore.tests.expressionsandstatements.ExpressionsAndStatementsMill;
import de.monticore.tests.expressionsandstatements._visitor.ExpressionsAndStatementsTraverser;
import de.monticore.visitor.ITraverser;

public class ExpressionsAndStatementsJavaGenerator
    extends AbstractJavaGenVisitor {

  ExpressionsAndStatementsTraverser traverser;

  public ExpressionsAndStatementsJavaGenerator(IndentPrinter printer) {
    super(printer);
    init();
  }

  protected void init() {
    JavaGenSymTypeExpressionConverter.init();
    JavaOperationPrinter.init();
    SymTypeExpression2JavaConverter.init();

    this.traverser = ExpressionsAndStatementsMill.inheritanceTraverser();

    ExpressionsAndStatementsJavaGenVisitor visExpressionsAndStatements =
        new ExpressionsAndStatementsJavaGenVisitor(getPrinter());
    traverser.setExpressionsAndStatementsHandler(visExpressionsAndStatements);

    // Literals

    MCCommonLiteralsJavaGenVisitor visMCCommonLiterals =
        new MCCommonLiteralsJavaGenVisitor(getPrinter());
    traverser.setMCCommonLiteralsHandler(visMCCommonLiterals);

    // Expressions

    AssignmentExpressionsJavaGenVisitor visAssignmentExpressions =
        new AssignmentExpressionsJavaGenVisitor(getPrinter());
    traverser.setAssignmentExpressionsHandler(visAssignmentExpressions);

    BitExpressionsJavaGenVisitor visBitExpressions =
        new BitExpressionsJavaGenVisitor(getPrinter());
    traverser.setBitExpressionsHandler(visBitExpressions);

    CommonExpressionsJavaGenVisitor visCommonExpressions =
        new CommonExpressionsJavaGenVisitor(getPrinter());
    traverser.setCommonExpressionsHandler(visCommonExpressions);

    ExpressionsBasisJavaGenVisitor visExpressionBasis =
        new ExpressionsBasisJavaGenVisitor(getPrinter());
    traverser.setExpressionsBasisHandler(visExpressionBasis);

    LambdaExpressionsJavaGenVisitor visLambdaExpressions =
        new LambdaExpressionsJavaGenVisitor(getPrinter());
    traverser.setLambdaExpressionsHandler(visLambdaExpressions);

    TupleExpressionsJavaGenVisitor visTupleExpressions =
        new TupleExpressionsJavaGenVisitor(getPrinter());
    traverser.setTupleExpressionsHandler(visTupleExpressions);

    OCLExpressionsJavaGenVisitor visOCLExpressions =
        new OCLExpressionsJavaGenVisitor(getPrinter());
    traverser.setOCLExpressionsHandler(visOCLExpressions);

    OptionalOperatorsJavaGenVisitor visOptionalOperators =
        new OptionalOperatorsJavaGenVisitor(getPrinter());
    traverser.setOptionalOperatorsHandler(visOptionalOperators);

    SetExpressionsJavaGenVisitor visSetExpressions =
        new SetExpressionsJavaGenVisitor(getPrinter());
    traverser.setSetExpressionsHandler(visSetExpressions);

    UglyExpressionsJavaGenVisitor visUglyExpressions =
        new UglyExpressionsJavaGenVisitor(getPrinter());
    traverser.setUglyExpressionsHandler(visUglyExpressions);

    // Statements

    MCVarDeclarationStatementsJavaGenVisitor visMCVarDeclarationStatements =
        new MCVarDeclarationStatementsJavaGenVisitor(getPrinter());
    traverser.setMCVarDeclarationStatementsHandler(visMCVarDeclarationStatements);
  }

  @Override
  public ITraverser getTraverser() {
    return traverser;
  }

}
