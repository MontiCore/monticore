// (c) https://github.com/MontiCore/monticore
package de.monticore.codegen.javagen;

import de.monticore.codegen.AbstractCodeGenVisitor;
import de.monticore.expressions.assignmentexpressions.codegen.javagen.AssignmentExpressionsJavaGenVisitor;
import de.monticore.expressions.bitexpressions.codegen.javagen.BitExpressionsJavaGenVisitor;
import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsTraverser;
import de.monticore.expressions.commonexpressions.codegen.javagen.CommonExpressionsJavaGenVisitor;
import de.monticore.expressions.expressionsbasis.codegen.javagen.ExpressionsBasisJavaGenVisitor;
import de.monticore.expressions.lambdaexpressions.codegen.javagen.LambdaExpressionsJavaGenVisitor;
import de.monticore.expressions.tupleexpressions.codegen.javagen.TupleExpressionsJavaGenVisitor;
import de.monticore.expressions.uglyexpressions.codegen.javagen.UglyExpressionsJavaGenVisitor;
import de.monticore.literals.mccommonliterals.codegen.javagen.MCCommonLiteralsJavaGenVisitor;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.visitor.ITraverser;

public class CombineExpressionWithLiteralsCodeGenerator
    extends AbstractCodeGenVisitor {

  CombineExpressionsWithLiteralsTraverser traverser;

  public CombineExpressionWithLiteralsCodeGenerator(IndentPrinter printer) {
    super(printer);
    init();
  }

  public void init() {
    this.traverser = CombineExpressionsWithLiteralsMill.traverser();

    JavaGenSymTypeExpressionConverter.init();
    JavaOperationPrinter.init();
    SymTypeExpression2JavaConverter.init();

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

    UglyExpressionsJavaGenVisitor visUglyExpressions =
        new UglyExpressionsJavaGenVisitor(getPrinter());
    traverser.setUglyExpressionsHandler(visUglyExpressions);

  }

  @Override
  public ITraverser getTraverser() {
    return traverser;
  }

}
