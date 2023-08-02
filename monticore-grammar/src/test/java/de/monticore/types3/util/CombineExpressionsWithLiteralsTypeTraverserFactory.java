/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3.util;

import de.monticore.expressions.assignmentexpressions.types3.AssignmentExpressionsTypeVisitor;
import de.monticore.expressions.bitexpressions.types3.BitExpressionsTypeVisitor;
import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsTraverser;
import de.monticore.expressions.commonexpressions.types3.CommonExpressionsTypeVisitor;
import de.monticore.expressions.expressionsbasis.types3.ExpressionBasisTypeVisitor;
import de.monticore.expressions.lambdaexpressions.types3.LambdaExpressionsTypeVisitor;
import de.monticore.literals.mccommonliterals.types3.MCCommonLiteralsTypeVisitor;
import de.monticore.types.mcarraytypes.types3.MCArrayTypesTypeVisitor;
import de.monticore.types.mcbasictypes.types3.MCBasicTypesTypeVisitor;
import de.monticore.types.mccollectiontypes.types3.MCCollectionTypesTypeVisitor;
import de.monticore.types.mcfullgenerictypes.types3.MCFullGenericTypesTypeVisitor;
import de.monticore.types.mcfunctiontypes.types3.MCFunctionTypesTypeVisitor;
import de.monticore.types.mcsimplegenerictypes.types3.MCSimpleGenericTypesTypeVisitor;
import de.monticore.types3.Type4Ast;

public class CombineExpressionsWithLiteralsTypeTraverserFactory {

  public CombineExpressionsWithLiteralsTraverser createTraverser(
      Type4Ast type4Ast
  ) {
    CombineExpressionsWithLiteralsTraverser traverser =
        CombineExpressionsWithLiteralsMill.inheritanceTraverser();
    VisitorList visitors = constructVisitors();
    setType4Ast(visitors, type4Ast);
    populateTraverser(visitors, traverser);
    return traverser;
  }

  public CombineExpressionsWithLiteralsTraverser createTraverserForOO(
      Type4Ast type4Ast
  ) {
    CombineExpressionsWithLiteralsTraverser traverser =
        CombineExpressionsWithLiteralsMill.inheritanceTraverser();
    VisitorList visitors = constructVisitorsForOO();
    setType4Ast(visitors, type4Ast);
    populateTraverser(visitors, traverser);
    return traverser;
  }

  protected void setType4Ast(VisitorList visitors, Type4Ast type4Ast) {
    // Expressions
    visitors.derAssignmentExpressions.setType4Ast(type4Ast);
    visitors.derBitExpressions.setType4Ast(type4Ast);
    visitors.derCommonExpressions.setType4Ast(type4Ast);
    visitors.derExpressionBasis.setType4Ast(type4Ast);
    visitors.derLambdaExpressions.setType4Ast(type4Ast);
    visitors.derCombineExpressionsWithLiterals.setType4Ast(type4Ast);
    visitors.derOfMCCommonLiterals.setType4Ast(type4Ast);
    // MCTypes
    visitors.synMCArrayTypes.setType4Ast(type4Ast);
    visitors.synMCBasicTypes.setType4Ast(type4Ast);
    visitors.synMCCollectionTypes.setType4Ast(type4Ast);
    visitors.synMCFullGenericTypes.setType4Ast(type4Ast);
    visitors.synMCFunctionTypes.setType4Ast(type4Ast);
    visitors.synMCSimpleGenericTypes.setType4Ast(type4Ast);
  }

  // Expressions

  protected VisitorList constructVisitors() {
    VisitorList visitors = new VisitorList();
    // Expressions
    visitors.derAssignmentExpressions = new AssignmentExpressionsTypeVisitor();
    visitors.derBitExpressions = new BitExpressionsTypeVisitor();
    visitors.derCommonExpressions = new CommonExpressionsTypeVisitor();
    visitors.derExpressionBasis = new ExpressionBasisTypeVisitor();
    visitors.derLambdaExpressions = new LambdaExpressionsTypeVisitor();
    visitors.derCombineExpressionsWithLiterals =
        new CombineExpressionsWithLiteralsTypeVisitor();
    visitors.derOfMCCommonLiterals = new MCCommonLiteralsTypeVisitor();
    // MCTypes
    visitors.synMCArrayTypes = new MCArrayTypesTypeVisitor();
    visitors.synMCBasicTypes = new MCBasicTypesTypeVisitor();
    visitors.synMCCollectionTypes = new MCCollectionTypesTypeVisitor();
    visitors.synMCFullGenericTypes = new MCFullGenericTypesTypeVisitor();
    visitors.synMCFunctionTypes = new MCFunctionTypesTypeVisitor();
    visitors.synMCSimpleGenericTypes = new MCSimpleGenericTypesTypeVisitor();
    return visitors;
  }

  /**
   * initializes additional logic for languages that have access to OO Symbols
   *
   * @return
   */
  protected VisitorList constructVisitorsForOO() {
    VisitorList visitors = constructVisitors();
    WithinTypeBasicSymbolsResolver withinTypeBasicSymbolsResolver =
        new OOWithinTypeBasicSymbolsResolver();
    NameExpressionTypeCalculator nameExpressionTypeCalculator =
        new OONameExpressionTypeCalculator();
    visitors.derCommonExpressions.setWithinTypeBasicSymbolsResolver(
        withinTypeBasicSymbolsResolver
    );
    visitors.derCommonExpressions.setNameExpressionTypeCalculator(
        nameExpressionTypeCalculator
    );
    visitors.derExpressionBasis.setNameExpressionTypeCalculator(
        nameExpressionTypeCalculator
    );
    visitors.synMCBasicTypes.setWithinTypeResolver(
        withinTypeBasicSymbolsResolver
    );
    visitors.synMCBasicTypes.setNameExpressionTypeCalculator(
        nameExpressionTypeCalculator
    );
    return visitors;
  }

  protected void populateTraverser(
      VisitorList visitors,
      CombineExpressionsWithLiteralsTraverser traverser
  ) {
    // Expressions
    traverser.add4AssignmentExpressions(visitors.derAssignmentExpressions);
    traverser.add4BitExpressions(visitors.derBitExpressions);
    traverser.add4CommonExpressions(visitors.derCommonExpressions);
    traverser.setCommonExpressionsHandler(visitors.derCommonExpressions);
    traverser.add4ExpressionsBasis(visitors.derExpressionBasis);
    traverser.add4LambdaExpressions(visitors.derLambdaExpressions);
    traverser.add4CombineExpressionsWithLiterals(visitors.derCombineExpressionsWithLiterals);
    traverser.add4MCCommonLiterals(visitors.derOfMCCommonLiterals);
    // MCTypes
    traverser.add4MCArrayTypes(visitors.synMCArrayTypes);
    traverser.add4MCBasicTypes(visitors.synMCBasicTypes);
    traverser.add4MCCollectionTypes(visitors.synMCCollectionTypes);
    traverser.add4MCFullGenericTypes(visitors.synMCFullGenericTypes);
    traverser.add4MCFunctionTypes(visitors.synMCFunctionTypes);
    traverser.add4MCSimpleGenericTypes(visitors.synMCSimpleGenericTypes);
  }

  /**
   * POD
   */
  protected static class VisitorList {

    // Expressions

    public AssignmentExpressionsTypeVisitor derAssignmentExpressions;

    public BitExpressionsTypeVisitor derBitExpressions;

    public CommonExpressionsTypeVisitor derCommonExpressions;

    public ExpressionBasisTypeVisitor derExpressionBasis;

    public LambdaExpressionsTypeVisitor derLambdaExpressions;

    public CombineExpressionsWithLiteralsTypeVisitor derCombineExpressionsWithLiterals;

    public MCCommonLiteralsTypeVisitor derOfMCCommonLiterals;

    // MCTypes

    public MCArrayTypesTypeVisitor synMCArrayTypes;

    public MCBasicTypesTypeVisitor synMCBasicTypes;

    public MCCollectionTypesTypeVisitor synMCCollectionTypes;

    public MCFullGenericTypesTypeVisitor synMCFullGenericTypes;

    public MCFunctionTypesTypeVisitor synMCFunctionTypes;

    public MCSimpleGenericTypesTypeVisitor synMCSimpleGenericTypes;
  }
}