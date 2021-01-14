/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.grammar_withconcepts;

//TODO: Delete when MC 6.7.0 is published
public class Grammar_WithConceptsMill extends Grammar_WithConceptsMillTOP {

  public static void initMe(Grammar_WithConceptsMill a){
    mill = a;
    millASTMCConceptBuilder = a;
    millASTActionBuilder = a;
    millASTExpressionPredicateBuilder = a;
    millASTJavaCodeBuilder = a;
    millASTExtTypeBuilder = a;
    millASTExtReturnTypeBuilder = a;
    millASTExtTypeArgumentBuilder = a;
    millASTExtTypeParametersBuilder = a;
    millGrammar_WithConceptsDelegatorVisitorBuilder = a;
    millGrammar_WithConceptsPhasedSymbolTableCreatorDelegator = a;
    millGrammar_WithConceptsSymbolTableCreatorDelegator = a;
    millGrammar_WithConceptsSymbolTableCreator = a;
    millGrammar_WithConceptsTraverserImplementation = a;
    millParser = a;
    millGrammar_WithConceptsScope = a;
    millGrammar_WithConceptsArtifactScope = a;
    millGrammar_WithConceptsGlobalScope = a;
    millGrammar_WithConceptsScopesGenitor = a;
    millGrammar_WithConceptsScopesGenitorDelegator = a;
  }

  public static void reset(){
    mill = null;
    millASTMCConceptBuilder = null;
    millASTActionBuilder = null;
    millASTExpressionPredicateBuilder = null;
    millASTJavaCodeBuilder = null;
    millASTExtTypeBuilder = null;
    millASTExtReturnTypeBuilder = null;
    millASTExtTypeArgumentBuilder = null;
    millASTExtTypeParametersBuilder = null;
    millGrammar_WithConceptsDelegatorVisitorBuilder = null;
    millGrammar_WithConceptsPhasedSymbolTableCreatorDelegator = null;
    millGrammar_WithConceptsSymbolTableCreatorDelegator = null;
    millGrammar_WithConceptsSymbolTableCreator = null;
    millGrammar_WithConceptsTraverserImplementation = null;
    millParser = null;
    millGrammar_WithConceptsScope = null;
    millGrammar_WithConceptsArtifactScope = null;
    millGrammar_WithConceptsGlobalScope = null;
    millGrammar_WithConceptsScopesGenitor = null;
    millGrammar_WithConceptsScopesGenitorDelegator = null;
    de.monticore.grammar.grammar.GrammarMill.reset();
    de.monticore.statements.mccommonstatements.MCCommonStatementsMill.reset();
    de.monticore.statements.mcreturnstatements.MCReturnStatementsMill.reset();
    de.monticore.statements.mcexceptionstatements.MCExceptionStatementsMill.reset();
    de.monticore.expressions.javaclassexpressions.JavaClassExpressionsMill.reset();
    de.monticore.javalight.JavaLightMill.reset();
    de.monticore.grammar.concepts.antlr.antlr.AntlrMill.reset();
    de.monticore.expressions.commonexpressions.CommonExpressionsMill.reset();
    de.monticore.expressions.bitexpressions.BitExpressionsMill.reset();
    de.monticore.cd.cd4analysis.CD4AnalysisMill.reset();
    de.monticore.literals.mccommonliterals.MCCommonLiteralsMill.reset();
    de.monticore.types.mcsimplegenerictypes.MCSimpleGenericTypesMill.reset();
    de.monticore.mcbasics.MCBasicsMill.reset();
    de.monticore.literals.mcliteralsbasis.MCLiteralsBasisMill.reset();
    de.monticore.types.mccollectiontypes.MCCollectionTypesMill.reset();
    de.monticore.types.mcbasictypes.MCBasicTypesMill.reset();
    de.monticore.statements.mcvardeclarationstatements.MCVarDeclarationStatementsMill.reset();
    de.monticore.statements.mcstatementsbasis.MCStatementsBasisMill.reset();
    de.monticore.expressions.expressionsbasis.ExpressionsBasisMill.reset();
    de.monticore.symbols.oosymbols.OOSymbolsMill.reset();
    de.monticore.symbols.basicsymbols.BasicSymbolsMill.reset();
    de.monticore.expressions.assignmentexpressions.AssignmentExpressionsMill.reset();
    de.monticore.statements.mcarraystatements.MCArrayStatementsMill.reset();
  }


}
