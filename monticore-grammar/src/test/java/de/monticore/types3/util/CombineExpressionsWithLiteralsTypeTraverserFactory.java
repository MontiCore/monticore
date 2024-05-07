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
import de.monticore.ocl.oclexpressions.types3.OCLExpressionsTypeVisitor;
import de.monticore.ocl.optionaloperators.types3.OptionalOperatorsTypeVisitor;
import de.monticore.ocl.setexpressions.types3.SetExpressionsTypeVisitor;
import de.monticore.expressions.tupleexpressions.types3.TupleExpressionsTypeVisitor;
import de.monticore.regex.regextype.types3.RegExTypeTypeVisitor;
import de.monticore.siunit.siunitliterals.types3.SIUnitLiteralsTypeVisitor;
import de.monticore.siunit.siunittypes4computing.types3.SIUnitTypes4ComputingTypeVisitor;
import de.monticore.siunit.siunittypes4math.types3.SIUnitTypes4MathTypeVisitor;
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
    visitors.derOCLExpressions.setType4Ast(type4Ast);
    visitors.derOptionalOperators.setType4Ast(type4Ast);
    visitors.derSetExpressions.setType4Ast(type4Ast);
    visitors.derTupleExpressions.setType4Ast(type4Ast);
    visitors.derUglyExpressions.setType4Ast(type4Ast);
    visitors.derOfMCCommonLiterals.setType4Ast(type4Ast);
    visitors.derSIUnitLiterals.setType4Ast(type4Ast);
    // MCTypes
    visitors.synMCArrayTypes.setType4Ast(type4Ast);
    visitors.synMCBasicTypes.setType4Ast(type4Ast);
    visitors.synMCCollectionTypes.setType4Ast(type4Ast);
    visitors.synMCFullGenericTypes.setType4Ast(type4Ast);
    visitors.synMCFunctionTypes.setType4Ast(type4Ast);
    visitors.synMCSimpleGenericTypes.setType4Ast(type4Ast);
    visitors.synMCStructuralTypes.setType4Ast(type4Ast);
    visitors.synRegExType.setType4Ast(type4Ast);
    visitors.synSIUnitTypes4Computing.setType4Ast(type4Ast);
    visitors.synSIUnitTypes4Math.setType4Ast(type4Ast);
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
    visitors.derOCLExpressions = new OCLExpressionsTypeVisitor();
    visitors.derOptionalOperators = new OptionalOperatorsTypeVisitor();
    visitors.derSetExpressions = new SetExpressionsTypeVisitor();
    visitors.derTupleExpressions = new TupleExpressionsTypeVisitor();
    visitors.derUglyExpressions = new UglyExpressionsTypeVisitor();
    visitors.derOfMCCommonLiterals = new MCCommonLiteralsTypeVisitor();
    visitors.derSIUnitLiterals = new SIUnitLiteralsTypeVisitor();
    // MCTypes
    visitors.synMCArrayTypes = new MCArrayTypesTypeVisitor();
    visitors.synMCBasicTypes = new MCBasicTypesTypeVisitor();
    visitors.synMCCollectionTypes = new MCCollectionTypesTypeVisitor();
    visitors.synMCFullGenericTypes = new MCFullGenericTypesTypeVisitor();
    visitors.synMCFunctionTypes = new MCFunctionTypesTypeVisitor();
    visitors.synMCSimpleGenericTypes = new MCSimpleGenericTypesTypeVisitor();
    visitors.synMCStructuralTypes = new MCStructuralTypesTypeVisitor();
    visitors.synRegExType = new RegExTypeTypeVisitor();
    visitors.synSIUnitTypes4Computing = new SIUnitTypes4ComputingTypeVisitor();
    visitors.synSIUnitTypes4Math = new SIUnitTypes4MathTypeVisitor();
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
    traverser.add4OCLExpressions(visitors.derOCLExpressions);
    traverser.add4OptionalOperators(visitors.derOptionalOperators);
    traverser.add4SetExpressions(visitors.derSetExpressions);
    traverser.add4TupleExpressions(visitors.derTupleExpressions);
    traverser.add4UglyExpressions(visitors.derUglyExpressions);
    traverser.add4MCCommonLiterals(visitors.derOfMCCommonLiterals);
    traverser.add4SIUnitLiterals(visitors.derSIUnitLiterals);
    // MCTypes
    traverser.add4MCArrayTypes(visitors.synMCArrayTypes);
    traverser.add4MCBasicTypes(visitors.synMCBasicTypes);
    traverser.add4MCCollectionTypes(visitors.synMCCollectionTypes);
    traverser.add4MCFullGenericTypes(visitors.synMCFullGenericTypes);
    traverser.add4MCFunctionTypes(visitors.synMCFunctionTypes);
    traverser.add4MCSimpleGenericTypes(visitors.synMCSimpleGenericTypes);
    traverser.add4MCStructuralTypes(visitors.synMCStructuralTypes);
    traverser.add4RegExType(visitors.synRegExType);
    traverser.add4SIUnitTypes4Computing(visitors.synSIUnitTypes4Computing);
    traverser.add4SIUnitTypes4Math(visitors.synSIUnitTypes4Math);
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

    public OCLExpressionsTypeVisitor derOCLExpressions;

    public OptionalOperatorsTypeVisitor derOptionalOperators;

    public SetExpressionsTypeVisitor derSetExpressions;

    public TupleExpressionsTypeVisitor derTupleExpressions;

    public UglyExpressionsTypeVisitor derUglyExpressions;

    // Literals

    public MCCommonLiteralsTypeVisitor derOfMCCommonLiterals;

    public SIUnitLiteralsTypeVisitor derSIUnitLiterals;

    // MCTypes

    public MCArrayTypesTypeVisitor synMCArrayTypes;

    public MCBasicTypesTypeVisitor synMCBasicTypes;

    public MCCollectionTypesTypeVisitor synMCCollectionTypes;

    public MCFullGenericTypesTypeVisitor synMCFullGenericTypes;

    public MCFunctionTypesTypeVisitor synMCFunctionTypes;

    public MCSimpleGenericTypesTypeVisitor synMCSimpleGenericTypes;

    public MCStructuralTypesTypeVisitor synMCStructuralTypes;

    public RegExTypeTypeVisitor synRegExType;

    public SIUnitTypes4ComputingTypeVisitor synSIUnitTypes4Computing;

    public SIUnitTypes4MathTypeVisitor synSIUnitTypes4Math;
  }
}
