/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.types3;

import de.monticore.expressions.assignmentexpressions.types3.AssignmentExpressionsCTTIVisitor;
import de.monticore.expressions.commonexpressions.types3.CommonExpressionsCTTIVisitor;
import de.monticore.expressions.commonexpressions.types3.util.CommonExpressionsLValueRelations;
import de.monticore.expressions.expressionsbasis.types3.ExpressionBasisCTTIVisitor;
import de.monticore.literals.mccommonliterals.types3.MCCommonLiteralsTypeVisitor;
import de.monticore.siunit.siunitliterals.types3.SIUnitLiteralsTypeVisitor;
import de.monticore.siunit.siunittypes4computing.types3.SIUnitTypes4ComputingTypeVisitor;
import de.monticore.siunit.siunittypes4math.types3.SIUnitTypes4MathTypeVisitor;
import de.monticore.temporal.detemporals.types3.DETemporalsTypeVisitor;
import de.monticore.temporal.escapedtemporalliterals.types3.EscapedTemporalLiteralsTypeVisitor;
import de.monticore.temporal.isotemporals.types3.ISOTemporalsTypeVisitor;
import de.monticore.temporal.temporalscript.TemporalScriptMill;
import de.monticore.temporal.temporalscript._visitor.TemporalScriptTraverser;
import de.monticore.types.mcbasictypes.types3.MCBasicTypesTypeVisitor;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.TypeCheck3;
import de.monticore.types3.generics.context.InferenceContext4Ast;
import de.monticore.types3.util.MapBasedTypeCheck3;
import de.monticore.types3.util.TypeContextCalculator;
import de.monticore.types3.util.TypeVisitorLifting;
import de.monticore.types3.util.TypeVisitorOperatorCalculator;
import de.monticore.types3.util.WithinScopeBasicSymbolsResolver;
import de.monticore.types3.util.WithinTypeBasicSymbolsResolver;
import de.monticore.visitor.ITraverser;

/**
 * TypeCheck3 of the TemporalScript test language,
 * an example of how to set up the types of the temporal languages.
 */
public class TemporalScriptTypeCheck3 extends MapBasedTypeCheck3 {

  public static void init() {
    SymTypeRelations.init();
    TemporalSymTypeRelations.init();
    WithinScopeBasicSymbolsResolver.init();
    WithinTypeBasicSymbolsResolver.init();
    TypeContextCalculator.init();
    // the temporal operators replace the default operators
    TemporalTypeVisitorOperatorCalculator.init();
    TypeVisitorLifting.init();
    CommonExpressionsLValueRelations.init();

    TemporalScriptTraverser traverser = TemporalScriptMill.traverser();
    Type4Ast type4Ast = new Type4Ast();
    InferenceContext4Ast ctx4Ast = new InferenceContext4Ast();

    // Literals

    MCCommonLiteralsTypeVisitor visMCCommonLiterals = new MCCommonLiteralsTypeVisitor();
    visMCCommonLiterals.setType4Ast(type4Ast);
    traverser.add4MCCommonLiterals(visMCCommonLiterals);

    SIUnitLiteralsTypeVisitor visSIUnitLiterals = new SIUnitLiteralsTypeVisitor();
    visSIUnitLiterals.setType4Ast(type4Ast);
    traverser.add4SIUnitLiterals(visSIUnitLiterals);

    EscapedTemporalLiteralsTypeVisitor visEscapedTemporalLiterals =
        new EscapedTemporalLiteralsTypeVisitor();
    visEscapedTemporalLiterals.setType4Ast(type4Ast);
    traverser.add4EscapedTemporalLiterals(visEscapedTemporalLiterals);

    ISOTemporalsTypeVisitor visISOTemporals = new ISOTemporalsTypeVisitor();
    visISOTemporals.setType4Ast(type4Ast);
    traverser.add4ISOTemporals(visISOTemporals);

    DETemporalsTypeVisitor visDETemporals = new DETemporalsTypeVisitor();
    visDETemporals.setType4Ast(type4Ast);
    traverser.add4DETemporals(visDETemporals);

    // Expressions

    ExpressionBasisCTTIVisitor visExpressionBasis = new ExpressionBasisCTTIVisitor();
    visExpressionBasis.setType4Ast(type4Ast);
    visExpressionBasis.setContext4Ast(ctx4Ast);
    traverser.add4ExpressionsBasis(visExpressionBasis);
    traverser.setExpressionsBasisHandler(visExpressionBasis);

    CommonExpressionsCTTIVisitor visCommonExpressions = new CommonExpressionsCTTIVisitor();
    visCommonExpressions.setType4Ast(type4Ast);
    visCommonExpressions.setContext4Ast(ctx4Ast);
    traverser.add4CommonExpressions(visCommonExpressions);
    traverser.setCommonExpressionsHandler(visCommonExpressions);

    AssignmentExpressionsCTTIVisitor visAssignmentExpressions =
        new AssignmentExpressionsCTTIVisitor();
    visAssignmentExpressions.setType4Ast(type4Ast);
    visAssignmentExpressions.setContext4Ast(ctx4Ast);
    traverser.add4AssignmentExpressions(visAssignmentExpressions);
    traverser.setAssignmentExpressionsHandler(visAssignmentExpressions);

    // Types

    MCBasicTypesTypeVisitor visMCBasicTypes = new MCBasicTypesTypeVisitor();
    visMCBasicTypes.setType4Ast(type4Ast);
    traverser.add4MCBasicTypes(visMCBasicTypes);

    SIUnitTypes4MathTypeVisitor visSIUnitTypes4Math = new SIUnitTypes4MathTypeVisitor();
    visSIUnitTypes4Math.setType4Ast(type4Ast);
    traverser.add4SIUnitTypes4Math(visSIUnitTypes4Math);

    SIUnitTypes4ComputingTypeVisitor visSIUnitTypes4Computing =
        new SIUnitTypes4ComputingTypeVisitor();
    visSIUnitTypes4Computing.setType4Ast(type4Ast);
    traverser.add4SIUnitTypes4Computing(visSIUnitTypes4Computing);

    new TemporalScriptTypeCheck3(traverser, type4Ast, ctx4Ast).setThisAsDelegate();
  }

  public static void reset() {
    TypeCheck3.resetDelegate();
    SymTypeRelations.reset();
    TemporalSymTypeRelations.reset();
    WithinScopeBasicSymbolsResolver.reset();
    WithinTypeBasicSymbolsResolver.reset();
    TypeContextCalculator.reset();
    TypeVisitorOperatorCalculator.reset();
    TypeVisitorLifting.reset();
    CommonExpressionsLValueRelations.reset();
  }

  protected TemporalScriptTypeCheck3(
      ITraverser typeTraverser, Type4Ast type4Ast, InferenceContext4Ast ctx4Ast) {
    super(typeTraverser, type4Ast, ctx4Ast);
  }

}
