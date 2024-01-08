/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3.util;

import de.monticore.expressions.assignmentexpressions.types3.AssignmentExpressionsTypeVisitor;
import de.monticore.expressions.bitexpressions.types3.BitExpressionsTypeVisitor;
import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsTraverser;
import de.monticore.expressions.commonexpressions.types3.CommonExpressionsTypeIdAsConstructorTypeVisitor;
import de.monticore.expressions.commonexpressions.types3.CommonExpressionsTypeVisitor;
import de.monticore.expressions.expressionsbasis.types3.ExpressionBasisTypeIdAsConstructorTypeVisitor;
import de.monticore.expressions.expressionsbasis.types3.ExpressionBasisTypeVisitor;
import de.monticore.expressions.lambdaexpressions.types3.LambdaExpressionsTypeVisitor;
import de.monticore.expressions.uglyexpressions.types3.UglyExpressionsTypeVisitor;
import de.monticore.literals.mccommonliterals.types3.MCCommonLiteralsTypeVisitor;
import de.monticore.regex.regextype.types3.RegExTypeTypeVisitor;
import de.monticore.types.mcarraytypes.types3.MCArrayTypesTypeVisitor;
import de.monticore.types.mcbasictypes.types3.MCBasicTypesTypeVisitor;
import de.monticore.types.mccollectiontypes.types3.MCCollectionTypesTypeVisitor;
import de.monticore.types.mcfullgenerictypes.types3.MCFullGenericTypesTypeVisitor;
import de.monticore.types.mcfunctiontypes.types3.MCFunctionTypesTypeVisitor;
import de.monticore.types.mcsimplegenerictypes.types3.MCSimpleGenericTypesTypeVisitor;
import de.monticore.types.mcstructuraltypes.types3.MCStructuralTypesTypeVisitor;
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

  public CombineExpressionsWithLiteralsTraverser createTraverserForOOWithConstructors(
      Type4Ast type4Ast
  ) {
    CombineExpressionsWithLiteralsTraverser traverser =
        CombineExpressionsWithLiteralsMill.inheritanceTraverser();
    VisitorList visitors = constructVisitorsForOOWithConstructors();
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
    visitors.derUglyExpressions.setType4Ast(type4Ast);
    visitors.derOfMCCommonLiterals.setType4Ast(type4Ast);
    // MCTypes
    visitors.synMCArrayTypes.setType4Ast(type4Ast);
    visitors.synMCBasicTypes.setType4Ast(type4Ast);
    visitors.synMCCollectionTypes.setType4Ast(type4Ast);
    visitors.synMCFullGenericTypes.setType4Ast(type4Ast);
    visitors.synMCFunctionTypes.setType4Ast(type4Ast);
    visitors.synMCSimpleGenericTypes.setType4Ast(type4Ast);
    visitors.synMCStructuralTypes.setType4Ast(type4Ast);
    visitors.synRegExType.setType4Ast(type4Ast);
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
    visitors.derUglyExpressions = new UglyExpressionsTypeVisitor();
    visitors.derOfMCCommonLiterals = new MCCommonLiteralsTypeVisitor();
    // MCTypes
    visitors.synMCArrayTypes = new MCArrayTypesTypeVisitor();
    visitors.synMCBasicTypes = new MCBasicTypesTypeVisitor();
    visitors.synMCCollectionTypes = new MCCollectionTypesTypeVisitor();
    visitors.synMCFullGenericTypes = new MCFullGenericTypesTypeVisitor();
    visitors.synMCFunctionTypes = new MCFunctionTypesTypeVisitor();
    visitors.synMCSimpleGenericTypes = new MCSimpleGenericTypesTypeVisitor();
    visitors.synMCStructuralTypes = new MCStructuralTypesTypeVisitor();
    visitors.synRegExType = new RegExTypeTypeVisitor();
    return visitors;
  }

  /**
   * initializes additional logic for languages that have access to OO Symbols
   */
  protected VisitorList constructVisitorsForOO() {
    VisitorList visitors = constructVisitors();
    visitors.derCommonExpressions =
        new CommonExpressionsTypeIdAsConstructorTypeVisitor();
    OOWithinTypeBasicSymbolsResolver withinTypeBasicSymbolsResolver =
        new OOWithinTypeBasicSymbolsResolver();
    WithinScopeBasicSymbolsResolver withinScopeResolver =
        new OOWithinScopeBasicSymbolsResolver();
    visitors.derCommonExpressions.setWithinTypeBasicSymbolsResolver(
        withinTypeBasicSymbolsResolver
    );
    visitors.derCommonExpressions.setWithinScopeResolver(
        withinScopeResolver
    );
    visitors.derExpressionBasis.setWithinScopeResolver(
        withinScopeResolver
    );
    visitors.derUglyExpressions.setOOWithinTypeBasicSymbolsResolver(
        withinTypeBasicSymbolsResolver
    );
    visitors.synMCBasicTypes.setWithinTypeResolver(
        withinTypeBasicSymbolsResolver
    );
    visitors.synMCBasicTypes.setWithinScopeResolver(
        withinScopeResolver
    );
    return visitors;
  }

  /**
   * initializes additional logic for languages that have access to OO Symbols,
   * in addition to being able to search for constructors
   */
  protected VisitorList constructVisitorsForOOWithConstructors() {
    VisitorList visitors = constructVisitorsForOO();
    visitors.derCommonExpressions =
        new CommonExpressionsTypeIdAsConstructorTypeVisitor();
    visitors.derExpressionBasis =
        new ExpressionBasisTypeIdAsConstructorTypeVisitor();
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
    traverser.add4UglyExpressions(visitors.derUglyExpressions);
    traverser.add4MCCommonLiterals(visitors.derOfMCCommonLiterals);
    // MCTypes
    traverser.add4MCArrayTypes(visitors.synMCArrayTypes);
    traverser.add4MCBasicTypes(visitors.synMCBasicTypes);
    traverser.add4MCCollectionTypes(visitors.synMCCollectionTypes);
    traverser.add4MCFullGenericTypes(visitors.synMCFullGenericTypes);
    traverser.add4MCFunctionTypes(visitors.synMCFunctionTypes);
    traverser.add4MCSimpleGenericTypes(visitors.synMCSimpleGenericTypes);
    traverser.add4MCStructuralTypes(visitors.synMCStructuralTypes);
    traverser.add4RegExType(visitors.synRegExType);
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

    public UglyExpressionsTypeVisitor derUglyExpressions;

    public MCCommonLiteralsTypeVisitor derOfMCCommonLiterals;

    // MCTypes

    public MCArrayTypesTypeVisitor synMCArrayTypes;

    public MCBasicTypesTypeVisitor synMCBasicTypes;

    public MCCollectionTypesTypeVisitor synMCCollectionTypes;

    public MCFullGenericTypesTypeVisitor synMCFullGenericTypes;

    public MCFunctionTypesTypeVisitor synMCFunctionTypes;

    public MCSimpleGenericTypesTypeVisitor synMCSimpleGenericTypes;

    public MCStructuralTypesTypeVisitor synMCStructuralTypes;

    public RegExTypeTypeVisitor synRegExType;
  }
}
