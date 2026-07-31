/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tests.expressionsandstatements.types3;

import de.monticore.expressions.assignmentexpressions.types3.AssignmentExpressionsCTTIVisitor;
import de.monticore.expressions.bitexpressions.types3.BitExpressionsTypeVisitor;
import de.monticore.expressions.commonexpressions.types3.CommonExpressionsCTTIVisitor;
import de.monticore.expressions.commonexpressions.types3.util.CommonExpressionsLValueRelations;
import de.monticore.expressions.expressionsbasis.types3.ExpressionBasisCTTIVisitor;
import de.monticore.expressions.lambdaexpressions.types3.LambdaExpressionsTypeVisitor;
import de.monticore.expressions.tupleexpressions.types3.TupleExpressionsCTTIVisitor;
import de.monticore.expressions.uglyexpressions.types3.UglyExpressionsCTTIVisitor;
import de.monticore.literals.mccommonliterals.types3.MCCommonLiteralsTypeVisitor;
import de.monticore.ocl.oclexpressions.types3.OCLExpressionsTypeVisitor;
import de.monticore.ocl.optionaloperators.types3.OptionalOperatorsTypeVisitor;
import de.monticore.ocl.setexpressions.types3.SetExpressionsCTTIVisitor;
import de.monticore.regex.regextype.types3.RegExTypeTypeVisitor;
import de.monticore.siunit.siunitliterals.types3.SIUnitLiteralsTypeVisitor;
import de.monticore.siunit.siunittypes4computing.types3.SIUnitTypes4ComputingTypeVisitor;
import de.monticore.siunit.siunittypes4math.types3.SIUnitTypes4MathTypeVisitor;
import de.monticore.tests.expressionsandstatements.ExpressionsAndStatementsMill;
import de.monticore.tests.expressionsandstatements._visitor.ExpressionsAndStatementsTraverser;
import de.monticore.types.mcbasictypes.types3.MCBasicTypesTypeVisitor;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.monticore.types.mccollectiontypes.types3.MCCollectionTypesTypeVisitor;
import de.monticore.types.mcfunctiontypes.types3.MCFunctionTypesTypeVisitor;
import de.monticore.types.mcsimplegenerictypes.types3.MCSimpleGenericTypesTypeVisitor;
import de.monticore.types.mcstructuraltypes.types3.MCStructuralTypesTypeVisitor;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.TypeCheck3;
import de.monticore.types3.generics.TypeParameterRelations;
import de.monticore.types3.generics.context.InferenceContext4Ast;
import de.monticore.types3.util.*;
import de.monticore.visitor.ITraverser;
import de.se_rwth.commons.logging.Log;

public class ExpressionsAndStatementsTypeCheck3 extends MapBasedTypeCheck3 {

  public static void init() {
    Log.trace(
        "init " + ExpressionsAndStatementsTypeCheck3.class.getSimpleName(),
        "TypeCheck setup"
    );

    SymTypeRelations.init();
    MCCollectionSymTypeRelations.init();
    OOWithinTypeBasicSymbolsResolver.init();
    OOWithinScopeBasicSymbolsResolver.init();
    TypeContextCalculator.init();
    TypeVisitorOperatorCalculator.init();
    CommonExpressionsLValueRelations.init();
    TypeParameterRelations.init();

    ExpressionsAndStatementsTraverser traverser =
        ExpressionsAndStatementsMill.inheritanceTraverser();
    Type4Ast type4Ast = new Type4Ast();
    InferenceContext4Ast ctx4Ast = new InferenceContext4Ast();

    // Literals

    MCCommonLiteralsTypeVisitor visMCCommonLiterals = new MCCommonLiteralsTypeVisitor();
    visMCCommonLiterals.setType4Ast(type4Ast);
    traverser.add4MCCommonLiterals(visMCCommonLiterals);

    SIUnitLiteralsTypeVisitor visSIUnitLiterals = new SIUnitLiteralsTypeVisitor();
    visSIUnitLiterals.setType4Ast(type4Ast);
    traverser.add4SIUnitLiterals(visSIUnitLiterals);

    // Expressions

    AssignmentExpressionsCTTIVisitor visAssignmentExpressions = new AssignmentExpressionsCTTIVisitor();
    visAssignmentExpressions.setType4Ast(type4Ast);
    visAssignmentExpressions.setContext4Ast(ctx4Ast);
    traverser.add4AssignmentExpressions(visAssignmentExpressions);
    traverser.setAssignmentExpressionsHandler(visAssignmentExpressions);

    BitExpressionsTypeVisitor visBitExpressions = new BitExpressionsTypeVisitor();
    visBitExpressions.setType4Ast(type4Ast);
    traverser.add4BitExpressions(visBitExpressions);

    CommonExpressionsCTTIVisitor visCommonExpressions =
        new CommonExpressionsCTTIVisitor();
    visCommonExpressions.setType4Ast(type4Ast);
    visCommonExpressions.setContext4Ast(ctx4Ast);
    traverser.add4CommonExpressions(visCommonExpressions);
    traverser.setCommonExpressionsHandler(visCommonExpressions);

    ExpressionBasisCTTIVisitor visExpressionBasis =
        new ExpressionBasisCTTIVisitor();
    visExpressionBasis.setType4Ast(type4Ast);
    visExpressionBasis.setContext4Ast(ctx4Ast);
    traverser.add4ExpressionsBasis(visExpressionBasis);
    traverser.setExpressionsBasisHandler(visExpressionBasis);

    LambdaExpressionsTypeVisitor visLambdaExpressions = new LambdaExpressionsTypeVisitor();
    visLambdaExpressions.setType4Ast(type4Ast);
    traverser.add4LambdaExpressions(visLambdaExpressions);

    TupleExpressionsCTTIVisitor visTupleExpressions = new TupleExpressionsCTTIVisitor();
    visTupleExpressions.setType4Ast(type4Ast);
    visTupleExpressions.setContext4Ast(ctx4Ast);
    traverser.add4TupleExpressions(visTupleExpressions);
    traverser.setTupleExpressionsHandler(visTupleExpressions);

    OCLExpressionsTypeVisitor visOCLExpressions = new OCLExpressionsTypeVisitor();
    visOCLExpressions.setType4Ast(type4Ast);
    traverser.add4OCLExpressions(visOCLExpressions);

    OptionalOperatorsTypeVisitor visOptionalOperators = new OptionalOperatorsTypeVisitor();
    visOptionalOperators.setType4Ast(type4Ast);
    traverser.add4OptionalOperators(visOptionalOperators);

    SetExpressionsCTTIVisitor visSetExpressions = new SetExpressionsCTTIVisitor();
    visSetExpressions.setType4Ast(type4Ast);
    visSetExpressions.setContext4Ast(ctx4Ast);
    traverser.add4SetExpressions(visSetExpressions);
    traverser.setSetExpressionsHandler(visSetExpressions);

    UglyExpressionsCTTIVisitor visUglyExpressions = new UglyExpressionsCTTIVisitor();
    visUglyExpressions.setType4Ast(type4Ast);
    visUglyExpressions.setContext4Ast(ctx4Ast);
    traverser.add4UglyExpressions(visUglyExpressions);
    traverser.setUglyExpressionsHandler(visUglyExpressions);

    // MCTypes

    MCBasicTypesTypeVisitor visMCBasicTypes = new MCBasicTypesTypeVisitor();
    visMCBasicTypes.setType4Ast(type4Ast);
    traverser.add4MCBasicTypes(visMCBasicTypes);

    MCCollectionTypesTypeVisitor visMCCollectionTypes = new MCCollectionTypesTypeVisitor();
    visMCCollectionTypes.setType4Ast(type4Ast);
    traverser.add4MCCollectionTypes(visMCCollectionTypes);

    MCFunctionTypesTypeVisitor visMCFunctionTypes = new MCFunctionTypesTypeVisitor();
    visMCFunctionTypes.setType4Ast(type4Ast);
    traverser.add4MCFunctionTypes(visMCFunctionTypes);

    MCSimpleGenericTypesTypeVisitor visMCSimpleGenericTypes = new MCSimpleGenericTypesTypeVisitor();
    visMCSimpleGenericTypes.setType4Ast(type4Ast);
    traverser.add4MCSimpleGenericTypes(visMCSimpleGenericTypes);

    MCStructuralTypesTypeVisitor visMCStructuralTypes = new MCStructuralTypesTypeVisitor();
    visMCStructuralTypes.setType4Ast(type4Ast);
    traverser.add4MCStructuralTypes(visMCStructuralTypes);

    SIUnitTypes4ComputingTypeVisitor visSIUnitTypes4Computing = new SIUnitTypes4ComputingTypeVisitor();
    visSIUnitTypes4Computing.setType4Ast(type4Ast);
    traverser.add4SIUnitTypes4Computing(visSIUnitTypes4Computing);

    SIUnitTypes4MathTypeVisitor visSIUnitTypes4Math = new SIUnitTypes4MathTypeVisitor();
    visSIUnitTypes4Math.setType4Ast(type4Ast);
    traverser.add4SIUnitTypes4Math(visSIUnitTypes4Math);

    RegExTypeTypeVisitor visRegExType = new RegExTypeTypeVisitor();
    visRegExType.setType4Ast(type4Ast);
    traverser.add4RegExType(visRegExType);

    // create delegate
    ExpressionsAndStatementsTypeCheck3 oclTC3 = new ExpressionsAndStatementsTypeCheck3(traverser, type4Ast, ctx4Ast);
    oclTC3.setThisAsDelegate();
  }

  public static void reset() {
    TypeCheck3.resetDelegate();
    SymTypeRelations.reset();
    MCCollectionSymTypeRelations.reset();
    OOWithinTypeBasicSymbolsResolver.reset();
    OOWithinScopeBasicSymbolsResolver.reset();
    TypeContextCalculator.reset();
    TypeVisitorOperatorCalculator.reset();
    CommonExpressionsLValueRelations.reset();
    TypeParameterRelations.reset();
  }

  protected ExpressionsAndStatementsTypeCheck3(
      ITraverser typeTraverser, Type4Ast type4Ast, InferenceContext4Ast ctx4Ast) {
    super(typeTraverser, type4Ast, ctx4Ast);
  }

}
