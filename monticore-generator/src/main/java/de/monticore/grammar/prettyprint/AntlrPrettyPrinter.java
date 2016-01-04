/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

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
    getPrinter().print("lexer java ");
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
    getPrinter().print("parser java ");
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
