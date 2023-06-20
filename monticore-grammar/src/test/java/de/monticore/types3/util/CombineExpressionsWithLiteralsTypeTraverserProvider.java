/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3.util;

import de.monticore.expressions.assignmentexpressions.types3.AssignmentExpressionsTypeVisitor;
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

public class CombineExpressionsWithLiteralsTypeTraverserProvider {

  //todo Bit Expressions

  // Expressions

  protected AssignmentExpressionsTypeVisitor derAssignmentExpressions;

  protected CommonExpressionsTypeVisitor derCommonExpressions;

  protected ExpressionBasisTypeVisitor derExpressionBasis;

  protected LambdaExpressionsTypeVisitor derLambdaExpressions;

  protected CombineExpressionsWithLiteralsTypeVisitor derCombineExpressionsWithLiterals;

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
    derCommonExpressions = new CommonExpressionsTypeVisitor();
    derExpressionBasis = new ExpressionBasisTypeVisitor();
    derLambdaExpressions = new LambdaExpressionsTypeVisitor();
    derCombineExpressionsWithLiterals = new CombineExpressionsWithLiteralsTypeVisitor();
    derOfMCCommonLiterals = new MCCommonLiteralsTypeVisitor();
    // MCTypes
    synMCArrayTypes = new MCArrayTypesTypeVisitor();
    synMCBasicTypes = new MCBasicTypesTypeVisitor();
    synMCCollectionTypes = new MCCollectionTypesTypeVisitor();
    synMCFullGenericTypes = new MCFullGenericTypesTypeVisitor();
    synMCFunctionTypes = new MCFunctionTypesTypeVisitor();
    synMCSimpleGenericTypes = new MCSimpleGenericTypesTypeVisitor();
  }

  public CombineExpressionsWithLiteralsTraverser init(CombineExpressionsWithLiteralsTraverser traverser) {
    // Expressions
    traverser.add4AssignmentExpressions(derAssignmentExpressions);
    traverser.add4CommonExpressions(derCommonExpressions);
    traverser.setCommonExpressionsHandler(derCommonExpressions);
    traverser.add4ExpressionsBasis(derExpressionBasis);
    traverser.add4LambdaExpressions(derLambdaExpressions);
    traverser.add4CombineExpressionsWithLiterals(derCombineExpressionsWithLiterals);
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
}
