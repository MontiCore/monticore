/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.prettyprint;


import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpressionsNode;
import de.monticore.expressions.bitexpressions._ast.ASTBitExpressionsNode;
import de.monticore.expressions.commonexpressions._ast.ASTCommonExpressionsNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpressionsBasisNode;
import de.monticore.expressions.javaclassexpressions._ast.ASTJavaClassExpressionsNode;
import de.monticore.expressions.prettyprint.*;
import de.monticore.grammar.concepts.antlr.antlr._ast.ASTAntlrNode;
import de.monticore.grammar.grammar._ast.ASTGrammarNode;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.grammar.grammar_withconcepts._ast.ASTGrammar_WithConceptsNode;
import de.monticore.grammar.grammar_withconcepts._visitor.Grammar_WithConceptsTraverser;
import de.monticore.javalight._ast.ASTJavaLightNode;
import de.monticore.literals.prettyprint.MCCommonLiteralsPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.JavaLightPrettyPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import de.monticore.statements.mccommonstatements._ast.ASTMCCommonStatementsNode;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCStatementsBasisNode;
import de.monticore.statements.prettyprint.*;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import de.monticore.types.prettyprint.MCCollectionTypesPrettyPrinter;
import de.monticore.types.prettyprint.MCSimpleGenericTypesPrettyPrinter;

@Deprecated(forRemoval = true)
public class  Grammar_WithConceptsFullPrettyPrinter {


  protected IndentPrinter printer;

  protected Grammar_WithConceptsTraverser traverser;

  public Grammar_WithConceptsFullPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
    printer.setIndentLength(2);
    traverser = Grammar_WithConceptsMill.traverser();

    AntlrPrettyPrinter antlr = new AntlrPrettyPrinter(printer);
    traverser.add4Antlr(antlr);
    traverser.setAntlrHandler(antlr);

    GrammarPrettyPrinter grammar = new GrammarPrettyPrinter(printer);
    traverser.add4Grammar(grammar);
    traverser.setGrammarHandler(grammar);

    AssignmentExpressionsPrettyPrinter assigments = new AssignmentExpressionsPrettyPrinter(printer);
    traverser.add4AssignmentExpressions(assigments);
    traverser.setAssignmentExpressionsHandler(assigments);

    ExpressionsBasisPrettyPrinter basisExpr = new ExpressionsBasisPrettyPrinter(printer);
    traverser.add4ExpressionsBasis(basisExpr);
    traverser.setExpressionsBasisHandler(basisExpr);

    CommonExpressionsPrettyPrinter commonExpr = new CommonExpressionsPrettyPrinter(printer);
    traverser.add4CommonExpressions(commonExpr);
    traverser.setCommonExpressionsHandler(commonExpr);

    JavaClassExpressionsPrettyPrinter classExpr = new JavaClassExpressionsPrettyPrinter(printer);
    traverser.add4JavaClassExpressions(classExpr);
    traverser.setJavaClassExpressionsHandler(classExpr);

    BitExpressionsPrettyPrinter bitExpr = new BitExpressionsPrettyPrinter(printer);
    traverser.add4BitExpressions(bitExpr);
    traverser.setBitExpressionsHandler(bitExpr);

    MCBasicsPrettyPrinter basics = new MCBasicsPrettyPrinter(printer);
    traverser.add4MCBasics(basics);

    MCCommonLiteralsPrettyPrinter commonLiterals = new MCCommonLiteralsPrettyPrinter(printer);
    traverser.add4MCCommonLiterals(commonLiterals);
    traverser.setMCCommonLiteralsHandler(commonLiterals);

    MCBasicTypesPrettyPrinter basicTypes = new MCBasicTypesPrettyPrinter(printer);
    traverser.add4MCBasicTypes(basicTypes);
    traverser.setMCBasicTypesHandler(basicTypes);

    MCCollectionTypesPrettyPrinter collectionTypes = new MCCollectionTypesPrettyPrinter(printer);
    traverser.add4MCCollectionTypes(collectionTypes);
    traverser.setMCCollectionTypesHandler(collectionTypes);

    MCSimpleGenericTypesPrettyPrinter simpleGenerics = new MCSimpleGenericTypesPrettyPrinter(printer);
    traverser.add4MCSimpleGenericTypes(simpleGenerics);
    traverser.setMCSimpleGenericTypesHandler(simpleGenerics);

    MCExceptionStatementsPrettyPrinter exceptionStS = new MCExceptionStatementsPrettyPrinter(printer);
    traverser.add4MCExceptionStatements(exceptionStS);
    traverser.setMCExceptionStatementsHandler(exceptionStS);

    MCReturnStatementsPrettyPrinter returnSts = new MCReturnStatementsPrettyPrinter(printer);
    traverser.add4MCReturnStatements(returnSts);
    traverser.setMCReturnStatementsHandler(returnSts);

    MCCommonStatementsPrettyPrinter commonSts = new MCCommonStatementsPrettyPrinter(printer);
    traverser.add4MCCommonStatements(commonSts);
    traverser.setMCCommonStatementsHandler(commonSts);

    MCVarDeclarationStatementsPrettyPrinter varDeclSts = new MCVarDeclarationStatementsPrettyPrinter(printer);
    traverser.add4MCVarDeclarationStatements(varDeclSts);
    traverser.setMCVarDeclarationStatementsHandler(varDeclSts);

    MCArrayStatementsPrettyPrinter arraySts = new MCArrayStatementsPrettyPrinter(printer);
    traverser.add4MCArrayStatements(arraySts);
    traverser.setMCArrayStatementsHandler(arraySts);

    JavaLightPrettyPrinter javaLight = new JavaLightPrettyPrinter(printer);
    traverser.add4JavaLight(javaLight);
    traverser.setJavaLightHandler(javaLight);
  }

  public String prettyprint(ASTGrammar_WithConceptsNode a) {
    printer.clearBuffer();
    a.accept(getTraverser());
    return printer.getContent();
  }

  public String prettyprint(ASTGrammarNode a) {
    printer.clearBuffer();
    a.accept(getTraverser());
    return printer.getContent();
  }

  public String prettyprint(ASTAntlrNode a) {
    printer.clearBuffer();
    a.accept(getTraverser());
    return printer.getContent();
  }

  public String prettyprint(ASTJavaLightNode a) {
    printer.clearBuffer();
    a.accept(getTraverser());
    return printer.getContent();
  }

  public IndentPrinter getPrinter() {
    return printer;
  }

  public void setPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  public Grammar_WithConceptsTraverser getTraverser() {
    return traverser;
  }

  public void setTraverser(Grammar_WithConceptsTraverser traverser) {
    this.traverser = traverser;
  }

  public String prettyprint(ASTMCCommonStatementsNode a) {
    printer.clearBuffer();
    a.accept(getTraverser());
    return printer.getContent();
  }

  public String prettyprint(ASTExpressionsBasisNode a) {
    printer.clearBuffer();
    a.accept(getTraverser());
    return printer.getContent();
  }

  public String prettyprint(ASTBitExpressionsNode a) {
    printer.clearBuffer();
    a.accept(getTraverser());
    return printer.getContent();
  }

  public String prettyprint(ASTCommonExpressionsNode a) {
    printer.clearBuffer();
    a.accept(getTraverser());
    return printer.getContent();
  }

  public String prettyprint(ASTAssignmentExpressionsNode a) {
    printer.clearBuffer();
    a.accept(getTraverser());
    return printer.getContent();
  }

  public String prettyprint(ASTJavaClassExpressionsNode a) {
    printer.clearBuffer();
    a.accept(getTraverser());
    return printer.getContent();
  }

  public String prettyprint(ASTMCStatementsBasisNode a) {
    printer.clearBuffer();
    a.accept(getTraverser());
    return printer.getContent();
  }

}
