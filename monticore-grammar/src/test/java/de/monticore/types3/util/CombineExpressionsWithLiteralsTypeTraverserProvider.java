/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3.util;

import de.monticore.expressions.assignmentexpressions.types3.AssignmentExpressionsTypeVisitor;
import de.monticore.expressions.bitexpressions.types3.BitExpressionsTypeVisitor;
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

/**
 * @deprecated use {@link CombineExpressionsWithLiteralsTypeTraverserFactory}
 */
@Deprecated(forRemoval = true)
public class CombineExpressionsWithLiteralsTypeTraverserProvider {

  // Expressions

  protected AssignmentExpressionsTypeVisitor derAssignmentExpressions;

  protected BitExpressionsTypeVisitor derBitExpressions;

  protected CommonExpressionsTypeVisitor derCommonExpressions;

  protected ExpressionBasisTypeVisitor derExpressionBasis;

  protected LambdaExpressionsTypeVisitor derLambdaExpressions;

  protected MCCommonLiteralsTypeVisitor derOfMCCommonLiterals;

  // MCTypes

  protected MCArrayTypesTypeVisitor synMCArrayTypes;

  protected MCBasicTypesTypeVisitor synMCBasicTypes;

  protected MCCollectionTypesTypeVisitor synMCCollectionTypes;

  protected MCFullGenericTypesTypeVisitor synMCFullGenericTypes;

  protected MCFunctionTypesTypeVisitor synMCFunctionTypes;

  protected MCSimpleGenericTypesTypeVisitor synMCSimpleGenericTypes;

  public CombineExpressionsWithLiteralsTypeTraverserProvider() {
    init();
  }

  protected void init() {
    // Expressions
    derAssignmentExpressions = new AssignmentExpressionsTypeVisitor();
    derBitExpressions = new BitExpressionsTypeVisitor();
    derCommonExpressions = new CommonExpressionsTypeVisitor();
    derExpressionBasis = new ExpressionBasisTypeVisitor();
    derLambdaExpressions = new LambdaExpressionsTypeVisitor();
    derOfMCCommonLiterals = new MCCommonLiteralsTypeVisitor();
    // MCTypes
    synMCArrayTypes = new MCArrayTypesTypeVisitor();
    synMCBasicTypes = new MCBasicTypesTypeVisitor();
    synMCCollectionTypes = new MCCollectionTypesTypeVisitor();
    synMCFullGenericTypes = new MCFullGenericTypesTypeVisitor();
    synMCFunctionTypes = new MCFunctionTypesTypeVisitor();
    synMCSimpleGenericTypes = new MCSimpleGenericTypesTypeVisitor();
  }

  /**
   * initializes additional logic for languages that have access to OO Symbols
   */
  public void initForOOSymbols() {
    init();
    WithinTypeBasicSymbolsResolver withinTypeBasicSymbolsResolver =
        new OOWithinTypeBasicSymbolsResolver();
    WithinScopeBasicSymbolsResolver withinScopeResolver =
        new OOWithinScopeBasicSymbolsResolver();
    derCommonExpressions.setWithinTypeBasicSymbolsResolver(
        withinTypeBasicSymbolsResolver
    );
    derCommonExpressions.setWithinScopeResolver(
        withinScopeResolver
    );
    derExpressionBasis.setWithinScopeResolver(
        withinScopeResolver
    );
    synMCBasicTypes.setWithinTypeResolver(
        withinTypeBasicSymbolsResolver
    );
    synMCBasicTypes.setWithinScopeResolver(
        withinScopeResolver
    );
  }

  public CombineExpressionsWithLiteralsTraverser init(
      CombineExpressionsWithLiteralsTraverser traverser) {
    // Expressions
    traverser.add4AssignmentExpressions(derAssignmentExpressions);
    traverser.add4BitExpressions(derBitExpressions);
    traverser.add4CommonExpressions(derCommonExpressions);
    traverser.setCommonExpressionsHandler(derCommonExpressions);
    traverser.add4ExpressionsBasis(derExpressionBasis);
    traverser.add4LambdaExpressions(derLambdaExpressions);
    traverser.add4MCCommonLiterals(derOfMCCommonLiterals);
    // MCTypes
    traverser.add4MCArrayTypes(synMCArrayTypes);
    traverser.add4MCBasicTypes(synMCBasicTypes);
    traverser.add4MCCollectionTypes(synMCCollectionTypes);
    traverser.add4MCFullGenericTypes(synMCFullGenericTypes);
    traverser.add4MCFunctionTypes(synMCFunctionTypes);
    traverser.add4MCSimpleGenericTypes(synMCSimpleGenericTypes);

    return traverser;
  }

  public void setType4Ast(Type4Ast type4Ast) {
    // Expressions
    derAssignmentExpressions.setType4Ast(type4Ast);
    derBitExpressions.setType4Ast(type4Ast);
    derCommonExpressions.setType4Ast(type4Ast);
    derExpressionBasis.setType4Ast(type4Ast);
    derLambdaExpressions.setType4Ast(type4Ast);
    derOfMCCommonLiterals.setType4Ast(type4Ast);
    // MCTypes
    synMCArrayTypes.setType4Ast(type4Ast);
    synMCBasicTypes.setType4Ast(type4Ast);
    synMCCollectionTypes.setType4Ast(type4Ast);
    synMCFullGenericTypes.setType4Ast(type4Ast);
    synMCFunctionTypes.setType4Ast(type4Ast);
    synMCSimpleGenericTypes.setType4Ast(type4Ast);
  }
}
