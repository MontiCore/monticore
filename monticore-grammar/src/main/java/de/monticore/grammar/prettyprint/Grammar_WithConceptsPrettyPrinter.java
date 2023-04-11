/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.prettyprint;


import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpressionsNode;
import de.monticore.expressions.bitexpressions._ast.ASTBitExpressionsNode;
import de.monticore.expressions.commonexpressions._ast.ASTCommonExpressionsNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpressionsBasisNode;
import de.monticore.expressions.javaclassexpressions._ast.ASTJavaClassExpressionsNode;
import de.monticore.grammar.grammar_withconcepts._ast.*;
import de.monticore.grammar.grammar_withconcepts._visitor.Grammar_WithConceptsHandler;
import de.monticore.grammar.grammar_withconcepts._visitor.Grammar_WithConceptsTraverser;
import de.monticore.grammar.grammar_withconcepts._visitor.Grammar_WithConceptsVisitor2;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.statements.mccommonstatements._ast.ASTMCCommonStatementsNode;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCStatementsBasisNode;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;


@Deprecated(forRemoval = true)
public class Grammar_WithConceptsPrettyPrinter implements Grammar_WithConceptsVisitor2,
        Grammar_WithConceptsHandler {
    
  protected IndentPrinter printer;
  
  protected Grammar_WithConceptsTraverser traverser;
  

  public Grammar_WithConceptsPrettyPrinter(IndentPrinter out) {
    printer = out;
    out.setIndentLength(2);
  }
  
  public String prettyprint(ASTGrammar_WithConceptsNode a) {
    printer.clearBuffer();
    a.accept(getTraverser());
    return printer.getContent();
  }

  @Override
  public void handle(ASTExtReturnType node) {
    CommentPrettyPrinter.printPreComments(node, printer);
    node.getMCReturnType().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, printer);
  }

  @Override
  public void handle(ASTExtType node) {
    CommentPrettyPrinter.printPreComments(node, printer);
    node.getMCType().accept(getTraverser());
    CommentPrettyPrinter.printPostComments(node, printer);
  }

  @Override
  public void handle(ASTAction node) {
    CommentPrettyPrinter.printPreComments(node, printer);
    node.getMCBlockStatementList().stream().forEach(a -> a.accept(getTraverser()));
    CommentPrettyPrinter.printPostComments(node, printer);
  }

  @Override
  public void handle(ASTExtTypeArgument node) {
    CommentPrettyPrinter.printPreComments(node, printer);
    printer.print("<");
    String sep = "";
    for (ASTMCTypeArgument t :node.getMCTypeArgumentList()) {
      printer.print(sep);
      t.accept(getTraverser());
      sep = ", ";
    }
    CommentPrettyPrinter.printPostComments(node, printer);
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

  public IndentPrinter getPrinter() {
    return printer;
  }

  public void setPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  @Override
  public Grammar_WithConceptsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(Grammar_WithConceptsTraverser traverser) {
    this.traverser = traverser;
  }
}
