package de.monticore.javalight.types3;

import de.monticore.expressions.assignmentexpressions.types3.AssignmentExpressionsCTTIVisitor;
import de.monticore.expressions.commonexpressions.types3.CommonExpressionsCTTIVisitor;
import de.monticore.expressions.expressionsbasis.types3.ExpressionBasisCTTIVisitor;
import de.monticore.expressions.javaclassexpressions.types3.JavaClassExpressionsTypeVisitor;
import de.monticore.expressions.uglyexpressions.types3.UglyExpressionsCTTIVisitor;
import de.monticore.javalight.JavaLightMill;
import de.monticore.javalight._visitor.JavaLightTraverser;
import de.monticore.literals.mccommonliterals.types3.MCCommonLiteralsTypeVisitor;
import de.monticore.types.mcbasictypes.types3.MCBasicTypesTypeVisitor;
import de.monticore.types.mccollectiontypes.types3.MCCollectionTypesTypeVisitor;
import de.monticore.types.mcsimplegenerictypes.types3.MCSimpleGenericTypesTypeVisitor;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.generics.context.InferenceContext4Ast;
import de.monticore.types3.util.MapBasedTypeCheck3;
import de.monticore.types3.util.OOWithinScopeBasicSymbolsResolver;
import de.monticore.types3.util.OOWithinTypeBasicSymbolsResolver;
import de.monticore.types3.util.TypeContextCalculator;
import de.monticore.types3.util.TypeVisitorOperatorCalculator;
import de.monticore.visitor.ITraverser;
import de.se_rwth.commons.logging.Log;

/**
 * TypeCheck3 implementation for the JavaLight language.
 * After calling {@link #init()},
 * this implementation will be available through the TypeCheck3 interface.
 */
public class JavaLightTypeCheck3 extends MapBasedTypeCheck3 {

  public static void init() {
    Log.trace("init JavaLightTypeCheck3", "TypeCheck setup");

    // initialize static delegates
    OOWithinScopeBasicSymbolsResolver.init();
    OOWithinTypeBasicSymbolsResolver.init();
    TypeContextCalculator.init();
    TypeVisitorOperatorCalculator.init();

    JavaLightTraverser traverser = JavaLightMill.traverser();
    Type4Ast type4Ast = new Type4Ast();
    InferenceContext4Ast ctx4Ast = new InferenceContext4Ast();

    // Literals

    MCCommonLiteralsTypeVisitor visMCCommonLiterals = new MCCommonLiteralsTypeVisitor();
    visMCCommonLiterals.setType4Ast(type4Ast);
    traverser.add4MCCommonLiterals(visMCCommonLiterals);

    // Expressions

    AssignmentExpressionsCTTIVisitor visAssignmentExpressions = new AssignmentExpressionsCTTIVisitor();
    visAssignmentExpressions.setType4Ast(type4Ast);
    visAssignmentExpressions.setContext4Ast(ctx4Ast);
    traverser.add4AssignmentExpressions(visAssignmentExpressions);
    traverser.setAssignmentExpressionsHandler(visAssignmentExpressions);

    CommonExpressionsCTTIVisitor visCommonExpressions = new CommonExpressionsCTTIVisitor();
    visCommonExpressions.setType4Ast(type4Ast);
    visCommonExpressions.setContext4Ast(ctx4Ast);
    traverser.add4CommonExpressions(visCommonExpressions);
    traverser.setCommonExpressionsHandler(visCommonExpressions);

    ExpressionBasisCTTIVisitor visExpressionBasis = new ExpressionBasisCTTIVisitor();
    visExpressionBasis.setType4Ast(type4Ast);
    visExpressionBasis.setContext4Ast(ctx4Ast);
    traverser.add4ExpressionsBasis(visExpressionBasis);
    traverser.setExpressionsBasisHandler(visExpressionBasis);

    JavaClassExpressionsTypeVisitor visJavaClassExpressions = new JavaClassExpressionsTypeVisitor();
    visJavaClassExpressions.setType4Ast(type4Ast);
    traverser.add4JavaClassExpressions(visJavaClassExpressions);

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

    MCSimpleGenericTypesTypeVisitor visMCSimpleGenericTypes = new MCSimpleGenericTypesTypeVisitor();
    visMCSimpleGenericTypes.setType4Ast(type4Ast);
    traverser.add4MCSimpleGenericTypes(visMCSimpleGenericTypes);

    // create delegate
    JavaLightTypeCheck3 oclTC3 = new JavaLightTypeCheck3(traverser, type4Ast, ctx4Ast);
    oclTC3.setThisAsDelegate();
  }

  protected JavaLightTypeCheck3(
      ITraverser typeTraverser, Type4Ast type4Ast, InferenceContext4Ast ctx4Ast) {
    super(typeTraverser, type4Ast, ctx4Ast);
  }
}
