/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3.util;

import de.monticore.expressions.assignmentexpressions.types3.AssignmentExpressionsCTTIVisitor;
import de.monticore.expressions.assignmentexpressions.types3.AssignmentExpressionsTypeVisitor;
import de.monticore.expressions.bitexpressions.types3.BitExpressionsTypeVisitor;
import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.expressions.combineexpressionswithliterals._visitor.CombineExpressionsWithLiteralsTraverser;
import de.monticore.expressions.commonexpressions.types3.CommonExpressionsCTTIVisitor;
import de.monticore.expressions.commonexpressions.types3.CommonExpressionsTypeIdAsConstructorTypeVisitor;
import de.monticore.expressions.commonexpressions.types3.CommonExpressionsTypeVisitor;
import de.monticore.expressions.expressionsbasis.types3.ExpressionBasisCTTIVisitor;
import de.monticore.expressions.expressionsbasis.types3.ExpressionBasisTypeIdAsConstructorTypeVisitor;
import de.monticore.expressions.expressionsbasis.types3.ExpressionBasisTypeVisitor;
import de.monticore.expressions.lambdaexpressions.types3.LambdaExpressionsTypeVisitor;
import de.monticore.expressions.tupleexpressions.types3.TupleExpressionsTypeVisitor;
import de.monticore.expressions.uglyexpressions.types3.UglyExpressionsCTTIVisitor;
import de.monticore.expressions.uglyexpressions.types3.UglyExpressionsTypeVisitor;
import de.monticore.literals.mccommonliterals.types3.MCCommonLiteralsTypeVisitor;
import de.monticore.ocl.oclexpressions.types3.OCLExpressionsTypeVisitor;
import de.monticore.ocl.optionaloperators.types3.OptionalOperatorsTypeVisitor;
import de.monticore.ocl.setexpressions.types3.SetExpressionsCTTIVisitor;
import de.monticore.ocl.setexpressions.types3.SetExpressionsTypeVisitor;
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
import de.monticore.types3.generics.context.InferenceContext4Ast;
import de.monticore.visitor.ITraverser;

public class CombineExpressionsWithLiteralsTypeTraverserFactory {

  /**
   * @deprecated use version with InferenceContext4Ast
   */
  @Deprecated
  public CombineExpressionsWithLiteralsTraverser createTraverser(
      Type4Ast type4Ast
  ) {
    return createTraverser(type4Ast, new InferenceContext4Ast());
  }

  public CombineExpressionsWithLiteralsTraverser createTraverser(
      Type4Ast type4Ast, InferenceContext4Ast ctx4Ast
  ) {
    CombineExpressionsWithLiteralsTraverser traverser =
        CombineExpressionsWithLiteralsMill.inheritanceTraverser();
    VisitorList visitors = constructVisitorsCTTI();
    setType4Ast(visitors, type4Ast);
    setContext4Ast(visitors, ctx4Ast);
    populateTraverser(visitors, traverser);
    return traverser;
  }

  public MapBasedTypeCheck3 initTypeCheck3() {
    Type4Ast type4Ast = new Type4Ast();
    InferenceContext4Ast ctx4Ast = new InferenceContext4Ast();
    ITraverser traverser = createTraverser(type4Ast, ctx4Ast);
    // sets itself as delegate
    return new TypeCheck3Impl(traverser, type4Ast, ctx4Ast);
  }

  /**
   * @deprecated use version with InferenceContext4Ast
   */
  @Deprecated
  public CombineExpressionsWithLiteralsTraverser createTraverserForOO(
      Type4Ast type4Ast
  ) {
    return createTraverserForOO(type4Ast, new InferenceContext4Ast());
  }

  public CombineExpressionsWithLiteralsTraverser createTraverserForOO(
      Type4Ast type4Ast, InferenceContext4Ast ctx4Ast
  ) {
    CombineExpressionsWithLiteralsTraverser traverser =
        CombineExpressionsWithLiteralsMill.inheritanceTraverser();
    VisitorList visitors = constructVisitorsForOO();
    setType4Ast(visitors, type4Ast);
    setContext4Ast(visitors, ctx4Ast);
    populateTraverser(visitors, traverser);
    return traverser;
  }

  public MapBasedTypeCheck3 initTypeCheck3ForOO() {
    Type4Ast type4Ast = new Type4Ast();
    InferenceContext4Ast ctx4Ast = new InferenceContext4Ast();
    ITraverser traverser = createTraverserForOO(type4Ast, ctx4Ast);
    // sets itself as delegate
    return new TypeCheck3Impl(traverser, type4Ast, ctx4Ast);
  }

  /**
   * @deprecated use version with InferenceContext4Ast
   */
  @Deprecated
  public CombineExpressionsWithLiteralsTraverser createTraverserForOOWithConstructors(
      Type4Ast type4Ast
  ) {
    return createTraverserForOOWithConstructors(type4Ast, new InferenceContext4Ast());
  }

  public CombineExpressionsWithLiteralsTraverser createTraverserForOOWithConstructors(
      Type4Ast type4Ast, InferenceContext4Ast ctx4Ast
  ) {
    CombineExpressionsWithLiteralsTraverser traverser =
        CombineExpressionsWithLiteralsMill.inheritanceTraverser();
    VisitorList visitors = constructVisitorsForOOWithConstructors();
    setType4Ast(visitors, type4Ast);
    setContext4Ast(visitors, ctx4Ast);
    populateTraverser(visitors, traverser);
    return traverser;
  }

  public MapBasedTypeCheck3 initTypeCheck3ForOOWithConstructors() {
    Type4Ast type4Ast = new Type4Ast();
    InferenceContext4Ast ctx4Ast = new InferenceContext4Ast();
    ITraverser traverser = createTraverserForOOWithConstructors(type4Ast, ctx4Ast);
    // sets itself as delegate
    return new TypeCheck3Impl(traverser, type4Ast, ctx4Ast);
  }

  protected void setType4Ast(VisitorList visitors, Type4Ast type4Ast) {
    // Expressions
    if (visitors.derAssignmentExpressions != null) {
      visitors.derAssignmentExpressions.setType4Ast(type4Ast);
    }
    else if (visitors.cTTIAssignmentExpressions != null) {
      visitors.cTTIAssignmentExpressions.setType4Ast(type4Ast);
    }
    if (visitors.derBitExpressions != null) {
      visitors.derBitExpressions.setType4Ast(type4Ast);
    }
    if (visitors.derCommonExpressions != null) {
      visitors.derCommonExpressions.setType4Ast(type4Ast);
    }
    else if (visitors.cTTICommonExpressions != null) {
      visitors.cTTICommonExpressions.setType4Ast(type4Ast);
    }
    if (visitors.derExpressionBasis != null) {
      visitors.derExpressionBasis.setType4Ast(type4Ast);
    }
    else if (visitors.cTTIExpressionBasis != null) {
      visitors.cTTIExpressionBasis.setType4Ast(type4Ast);
    }
    if (visitors.derLambdaExpressions != null) {
      visitors.derLambdaExpressions.setType4Ast(type4Ast);
    }
    if (visitors.derOCLExpressions != null) {
      visitors.derOCLExpressions.setType4Ast(type4Ast);
    }
    if (visitors.derOptionalOperators != null) {
      visitors.derOptionalOperators.setType4Ast(type4Ast);
    }
    if (visitors.derSetExpressions != null) {
      visitors.derSetExpressions.setType4Ast(type4Ast);
    }
    else if (visitors.cTTISetExpressions != null) {
      visitors.cTTISetExpressions.setType4Ast(type4Ast);
    }
    if (visitors.derTupleExpressions != null) {
      visitors.derTupleExpressions.setType4Ast(type4Ast);
    }
    if (visitors.derUglyExpressions != null) {
      visitors.derUglyExpressions.setType4Ast(type4Ast);
    }
    else if (visitors.cTTIUglyExpressions != null) {
      visitors.cTTIUglyExpressions.setType4Ast(type4Ast);
    }
    if (visitors.derOfMCCommonLiterals != null) {
      visitors.derOfMCCommonLiterals.setType4Ast(type4Ast);
    }
    if (visitors.derSIUnitLiterals != null) {
      visitors.derSIUnitLiterals.setType4Ast(type4Ast);
    }
    // MCTypes
    if (visitors.synMCArrayTypes != null) {
      visitors.synMCArrayTypes.setType4Ast(type4Ast);
    }
    if (visitors.synMCBasicTypes != null) {
      visitors.synMCBasicTypes.setType4Ast(type4Ast);
    }
    if (visitors.synMCCollectionTypes != null) {
      visitors.synMCCollectionTypes.setType4Ast(type4Ast);
    }
    if (visitors.synMCFullGenericTypes != null) {
      visitors.synMCFullGenericTypes.setType4Ast(type4Ast);
    }
    if (visitors.synMCFunctionTypes != null) {
      visitors.synMCFunctionTypes.setType4Ast(type4Ast);
    }
    if (visitors.synMCSimpleGenericTypes != null) {
      visitors.synMCSimpleGenericTypes.setType4Ast(type4Ast);
    }
    if (visitors.synMCStructuralTypes != null) {
      visitors.synMCStructuralTypes.setType4Ast(type4Ast);
    }
    if (visitors.synRegExType != null) {
      visitors.synRegExType.setType4Ast(type4Ast);
    }
    if (visitors.synSIUnitTypes4Computing != null) {
      visitors.synSIUnitTypes4Computing.setType4Ast(type4Ast);
    }
    if (visitors.synSIUnitTypes4Math != null) {
      visitors.synSIUnitTypes4Math.setType4Ast(type4Ast);
    }
  }

  protected void setContext4Ast(VisitorList visitors, InferenceContext4Ast ctx4Ast) {
    // Expressions
    if (visitors.cTTIAssignmentExpressions != null) {
      visitors.cTTIAssignmentExpressions.setContext4Ast(ctx4Ast);
    }
    if (visitors.cTTICommonExpressions != null) {
      visitors.cTTICommonExpressions.setContext4Ast(ctx4Ast);
    }
    if (visitors.cTTIExpressionBasis != null) {
      visitors.cTTIExpressionBasis.setContext4Ast(ctx4Ast);
    }
    if (visitors.cTTISetExpressions != null) {
      visitors.cTTISetExpressions.setContext4Ast(ctx4Ast);
    }
    if (visitors.cTTIUglyExpressions != null) {
      visitors.cTTIUglyExpressions.setContext4Ast(ctx4Ast);
    }
  }

  protected VisitorList constructVisitorsCTTI() {
    VisitorList visitors = new VisitorList();
    // Expressions
    visitors.cTTIAssignmentExpressions = new AssignmentExpressionsCTTIVisitor();
    visitors.derBitExpressions = new BitExpressionsTypeVisitor();
    visitors.cTTICommonExpressions = new CommonExpressionsCTTIVisitor();
    visitors.cTTIExpressionBasis = new ExpressionBasisCTTIVisitor();
    visitors.derLambdaExpressions = new LambdaExpressionsTypeVisitor();
    visitors.derOCLExpressions = new OCLExpressionsTypeVisitor();
    visitors.derOptionalOperators = new OptionalOperatorsTypeVisitor();
    visitors.cTTISetExpressions = new SetExpressionsCTTIVisitor();
    visitors.derTupleExpressions = new TupleExpressionsTypeVisitor();
    visitors.cTTIUglyExpressions = new UglyExpressionsCTTIVisitor();
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

  protected VisitorList constructVisitorsNoCTTI() {
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
    VisitorList visitors = constructVisitorsNoCTTI();
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
    if (visitors.derAssignmentExpressions != null) {
      traverser.add4AssignmentExpressions(visitors.derAssignmentExpressions);
    }
    else if (visitors.cTTIAssignmentExpressions != null) {
      traverser.add4AssignmentExpressions(visitors.cTTIAssignmentExpressions);
      traverser.setAssignmentExpressionsHandler(visitors.cTTIAssignmentExpressions);
    }
    if (visitors.derBitExpressions != null) {
      traverser.add4BitExpressions(visitors.derBitExpressions);
    }
    if (visitors.derCommonExpressions != null) {
      traverser.add4CommonExpressions(visitors.derCommonExpressions);
      traverser.setCommonExpressionsHandler(visitors.derCommonExpressions);
    }
    else if (visitors.cTTICommonExpressions != null) {
      traverser.add4CommonExpressions(visitors.cTTICommonExpressions);
      traverser.setCommonExpressionsHandler(visitors.cTTICommonExpressions);
    }
    if (visitors.derExpressionBasis != null) {
      traverser.add4ExpressionsBasis(visitors.derExpressionBasis);
    }
    else if (visitors.cTTIExpressionBasis != null) {
      traverser.add4ExpressionsBasis(visitors.cTTIExpressionBasis);
      traverser.setExpressionsBasisHandler(visitors.cTTIExpressionBasis);
    }
    if (visitors.derLambdaExpressions != null) {
      traverser.add4LambdaExpressions(visitors.derLambdaExpressions);
    }
    if (visitors.derOCLExpressions != null) {
      traverser.add4OCLExpressions(visitors.derOCLExpressions);
    }
    if (visitors.derOptionalOperators != null) {
      traverser.add4OptionalOperators(visitors.derOptionalOperators);
    }
    if (visitors.derSetExpressions != null) {
      traverser.add4SetExpressions(visitors.derSetExpressions);
    }
    else if (visitors.cTTISetExpressions != null) {
      traverser.add4SetExpressions(visitors.cTTISetExpressions);
      traverser.setSetExpressionsHandler(visitors.cTTISetExpressions);
    }
    if (visitors.derTupleExpressions != null) {
      traverser.add4TupleExpressions(visitors.derTupleExpressions);
    }
    if (visitors.derUglyExpressions != null) {
      traverser.add4UglyExpressions(visitors.derUglyExpressions);
    }
    else if (visitors.cTTIUglyExpressions != null) {
      traverser.add4UglyExpressions(visitors.cTTIUglyExpressions);
      traverser.setUglyExpressionsHandler(visitors.cTTIUglyExpressions);
    }
    if (visitors.derOfMCCommonLiterals != null) {
      traverser.add4MCCommonLiterals(visitors.derOfMCCommonLiterals);
    }
    if (visitors.derSIUnitLiterals != null) {
      traverser.add4SIUnitLiterals(visitors.derSIUnitLiterals);
    }
    // MCTypes
    if (visitors.synMCArrayTypes != null) {
      traverser.add4MCArrayTypes(visitors.synMCArrayTypes);
    }
    if (visitors.synMCBasicTypes != null) {
      traverser.add4MCBasicTypes(visitors.synMCBasicTypes);
    }
    if (visitors.synMCCollectionTypes != null) {
      traverser.add4MCCollectionTypes(visitors.synMCCollectionTypes);
    }
    if (visitors.synMCFullGenericTypes != null) {
      traverser.add4MCFullGenericTypes(visitors.synMCFullGenericTypes);
    }
    if (visitors.synMCFunctionTypes != null) {
      traverser.add4MCFunctionTypes(visitors.synMCFunctionTypes);
    }
    if (visitors.synMCSimpleGenericTypes != null) {
      traverser.add4MCSimpleGenericTypes(visitors.synMCSimpleGenericTypes);
    }
    if (visitors.synMCStructuralTypes != null) {
      traverser.add4MCStructuralTypes(visitors.synMCStructuralTypes);
    }
    if (visitors.synRegExType != null) {
      traverser.add4RegExType(visitors.synRegExType);
    }
    if (visitors.synSIUnitTypes4Computing != null) {
      traverser.add4SIUnitTypes4Computing(visitors.synSIUnitTypes4Computing);
    }
    if (visitors.synSIUnitTypes4Math != null) {
      traverser.add4SIUnitTypes4Math(visitors.synSIUnitTypes4Math);
    }
  }

  /**
   * POD
   */
  protected static class VisitorList {

    // Expressions

    public AssignmentExpressionsTypeVisitor derAssignmentExpressions;

    public AssignmentExpressionsCTTIVisitor cTTIAssignmentExpressions;

    public BitExpressionsTypeVisitor derBitExpressions;

    public CommonExpressionsTypeVisitor derCommonExpressions;

    public CommonExpressionsCTTIVisitor cTTICommonExpressions;

    public ExpressionBasisTypeVisitor derExpressionBasis;

    public ExpressionBasisCTTIVisitor cTTIExpressionBasis;

    public LambdaExpressionsTypeVisitor derLambdaExpressions;

    public OCLExpressionsTypeVisitor derOCLExpressions;

    public OptionalOperatorsTypeVisitor derOptionalOperators;

    public SetExpressionsTypeVisitor derSetExpressions;

    public SetExpressionsCTTIVisitor cTTISetExpressions;

    public TupleExpressionsTypeVisitor derTupleExpressions;

    public UglyExpressionsTypeVisitor derUglyExpressions;

    public UglyExpressionsCTTIVisitor cTTIUglyExpressions;

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

  /**
   * during construction, sets this as the delegate
   */
  protected static class TypeCheck3Impl extends MapBasedTypeCheck3 {

    protected TypeCheck3Impl(ITraverser typeTraverser, Type4Ast type4Ast, InferenceContext4Ast ctx4Ast) {
      super(typeTraverser, type4Ast, ctx4Ast);
      setThisAsDelegate();
    }
  }
}
