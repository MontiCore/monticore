/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.prettyprint;

import de.monticore.grammar.concepts.antlr.antlr._ast.ASTAntlrLexerAction;
import de.monticore.grammar.concepts.antlr.antlr._ast.ASTAntlrNode;
import de.monticore.grammar.concepts.antlr.antlr._ast.ASTAntlrParserAction;
import de.monticore.grammar.concepts.antlr.antlr._visitor.AntlrHandler;
import de.monticore.grammar.concepts.antlr.antlr._visitor.AntlrTraverser;
import de.monticore.grammar.concepts.antlr.antlr._visitor.AntlrVisitor2;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

@Deprecated(forRemoval = true)
public class AntlrPrettyPrinter implements AntlrVisitor2, AntlrHandler {
    
  // printer to use
  protected IndentPrinter printer = null;
  
  protected AntlrTraverser traverser;

  public AntlrPrettyPrinter(IndentPrinter out) {
    printer = out;
  }
   
  @Override
  public void handle(ASTAntlrLexerAction a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("lexerjava ");
    getPrinter().print(" {");
    getPrinter().println();
    getPrinter().indent();
    a.getText().accept(getTraverser());
    getPrinter().unindent();
    getPrinter().print("}"); 
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }
  
  @Override
  public void handle(ASTAntlrParserAction a) {
    CommentPrettyPrinter.printPreComments(a, getPrinter());
    getPrinter().print("parserjava ");
    getPrinter().print(" {");
    getPrinter().println();
    getPrinter().indent();
    a.getText().accept(getTraverser());
    getPrinter().unindent();
    getPrinter().print("}"); 
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }
  
  public String prettyprint(ASTAntlrNode a) {
    getPrinter().clearBuffer();
    a.accept(getTraverser());
    return getPrinter().getContent();
  }

  public IndentPrinter getPrinter() {
    return printer;
  }

  public void setPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  @Override
  public AntlrTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(AntlrTraverser traverser) {
    this.traverser = traverser;
  }
}
