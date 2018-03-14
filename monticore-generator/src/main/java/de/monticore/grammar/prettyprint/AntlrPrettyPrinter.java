/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.prettyprint;

import de.monticore.grammar.concepts.antlr.antlr._ast.ASTAntlrLexerAction;
import de.monticore.grammar.concepts.antlr.antlr._ast.ASTAntlrNode;
import de.monticore.grammar.concepts.antlr.antlr._ast.ASTAntlrParserAction;
import de.monticore.grammar.concepts.antlr.antlr._visitor.AntlrVisitor;
import de.monticore.prettyprint.CommentPrettyPrinter;
import de.monticore.prettyprint.IndentPrinter;

public class AntlrPrettyPrinter implements AntlrVisitor {
    
  // printer to use
  protected IndentPrinter printer = null;
  
  private AntlrVisitor realThis = this;
    
  /**
   * @return the printer
   */
  public IndentPrinter getPrinter() {
    return this.printer;
  }

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
    a.getText().accept(getRealThis());
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
    a.getText().accept(getRealThis());
    getPrinter().unindent();
    getPrinter().print("}"); 
    CommentPrettyPrinter.printPostComments(a, getPrinter());
  }
  
  public String prettyprint(ASTAntlrNode a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
  }

  /**
   * @see de.monticore.grammar.concepts.antlr.antlr._visitor.AntlrVisitor#setRealThis(de.monticore.grammar.concepts.antlr.antlr._visitor.AntlrVisitor)
   */
  @Override
  public void setRealThis(AntlrVisitor realThis) {
    this.realThis = realThis;
  }

  /**
   * @see de.monticore.grammar.concepts.antlr.antlr._visitor.AntlrVisitor#getRealThis()
   */
  @Override
  public AntlrVisitor getRealThis() {
    return realThis;
  }

}
