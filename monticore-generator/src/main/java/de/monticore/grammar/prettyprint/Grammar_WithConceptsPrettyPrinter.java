/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.prettyprint;


import de.monticore.MCCommonLiteralsPrettyPrinter;
import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpressionsNode;
import de.monticore.expressions.bitexpressions._ast.ASTBitExpressionsNode;
import de.monticore.expressions.commonexpressions._ast.ASTCommonExpressionsNode;
import de.monticore.expressions.expressionsbasis._ast.ASTExpressionsBasisNode;
import de.monticore.expressions.javaclassexpressions._ast.ASTExtReturnTypeExt;
import de.monticore.expressions.javaclassexpressions._ast.ASTJavaClassExpressionsNode;
import de.monticore.expressions.prettyprint2.*;
import de.monticore.grammar.concepts.antlr.antlr._ast.ASTAntlrNode;
import de.monticore.grammar.grammar._ast.ASTGrammarNode;
import de.monticore.grammar.grammar_withconcepts._ast.*;
import de.monticore.grammar.grammar_withconcepts._visitor.Grammar_WithConceptsDelegatorVisitor;
import de.monticore.grammar.grammar_withconcepts._visitor.Grammar_WithConceptsVisitor;
import de.monticore.javastatements._ast.ASTJavaStatementsNode;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.prettyprint.JavaStatementsPrettyPrinter;
import de.monticore.prettyprint.MCBasicsPrettyPrinter;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import de.monticore.types.prettyprint.MCCollectionTypesPrettyPrinter;
import de.monticore.types.prettyprint.MCSimpleGenericTypesPrettyPrinter;


public class Grammar_WithConceptsPrettyPrinter implements Grammar_WithConceptsVisitor {
    
  private IndentPrinter printer;
  
  private Grammar_WithConceptsVisitor realThis = this;
  
  public final Grammar_WithConceptsDelegatorVisitor visitor;

  public Grammar_WithConceptsPrettyPrinter(IndentPrinter out) {
    printer = out;
    out.setIndentLength(2);
    visitor = new Grammar_WithConceptsDelegatorVisitor();
    visitor.setGrammar_WithConceptsVisitor(this);
    visitor.setAntlrVisitor(new AntlrPrettyPrinter(out));
    visitor.setGrammarVisitor(new GrammarPrettyPrinter(out));
    visitor.setJavaStatementsVisitor(new JavaStatementsPrettyPrinter(out));
    visitor.setAssignmentExpressionsVisitor(new AssignmentExpressionsPrettyPrinter(out));
    visitor.setExpressionsBasisVisitor(new ExpressionsBasisPrettyPrinter(out));
    visitor.setCommonExpressionsVisitor(new CommonExpressionsPrettyPrinter(out));
    visitor.setMCBasicsVisitor(new MCBasicsPrettyPrinter(out));
    visitor.setJavaClassExpressionsVisitor(new JavaClassExpressionsPrettyPrinter(out));
    visitor.setBitExpressionsVisitor(new BitExpressionsPrettyPrinter(out));
    visitor.setMCCommonLiteralsVisitor(new MCCommonLiteralsPrettyPrinter(out));
    visitor.setMCBasicTypesVisitor(new MCBasicTypesPrettyPrinter(out));
    visitor.setMCCollectionTypesVisitor(new MCCollectionTypesPrettyPrinter(out));
    visitor.setMCSimpleGenericTypesVisitor(new MCSimpleGenericTypesPrettyPrinter(out));
    visitor.setMCCommonLiteralsVisitor(new MCCommonLiteralsPrettyPrinter(out));
  }
  
  @Override public void setRealThis(Grammar_WithConceptsVisitor realThis) {
    if (this.realThis != realThis) {
      this.realThis = realThis;
      visitor.setRealThis(realThis);
    }
  }

  @Override
  public Grammar_WithConceptsVisitor getRealThis() {
    return realThis;
  }
  
  public String prettyprint(ASTGrammar_WithConceptsNode a) {
    printer.clearBuffer();
    a.accept(getRealThis());
    return printer.getContent();
  }
  
  public String prettyprint(ASTGrammarNode a) {
    printer.clearBuffer();
    a.accept(getRealThis());
    return printer.getContent();
  }
  
  public String prettyprint(ASTAntlrNode a) {
    printer.clearBuffer();
    a.accept(getRealThis());
    return printer.getContent();
  }

  @Override
  public void handle(ASTEmptyTypeParameters node) {
    printer.print("<>");
  }

  @Override
  public void handle(ASTExtReturnType node) {
    CommentPrettyPrinter.printPreComments(node, printer);
    node.getMCReturnType().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, printer);
  }

  @Override
  public void handle(ASTExtType node) {
    CommentPrettyPrinter.printPreComments(node, printer);
    node.getMCType().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, printer);
  }
  @Override
  public void handle(ASTExtTypeArguments node) {
    CommentPrettyPrinter.printPreComments(node, printer);
    node.getEmptyTypeParameters().accept(getRealThis());
    CommentPrettyPrinter.printPostComments(node, printer);
  }

  public String prettyprint(ASTJavaStatementsNode a) {
    printer.clearBuffer();
    a.accept(getRealThis());
    return printer.getContent();
  }

  public String prettyprint(ASTExpressionsBasisNode a) {
    printer.clearBuffer();
    a.accept(getRealThis());
    return printer.getContent();
  }

  public String prettyprint(ASTBitExpressionsNode a) {
    printer.clearBuffer();
    a.accept(getRealThis());
    return printer.getContent();
  }

  public String prettyprint(ASTCommonExpressionsNode a) {
    printer.clearBuffer();
    a.accept(getRealThis());
    return printer.getContent();
  }

  public String prettyprint(ASTAssignmentExpressionsNode a) {
    printer.clearBuffer();
    a.accept(getRealThis());
    return printer.getContent();
  }

  public String prettyprint(ASTJavaClassExpressionsNode a) {
    printer.clearBuffer();
    a.accept(getRealThis());
    return printer.getContent();
  }
}
