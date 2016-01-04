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

package de.monticore.languages.grammar.lexpatterns;

import de.monticore.grammar.HelperGrammar;
import de.monticore.grammar.grammar._ast.ASTLexAlt;
import de.monticore.grammar.grammar._ast.ASTLexBlock;
import de.monticore.grammar.grammar._ast.ASTLexChar;
import de.monticore.grammar.grammar._ast.ASTLexCharRange;
import de.monticore.grammar.grammar._ast.ASTLexNonTerminal;
import de.monticore.grammar.grammar._ast.ASTLexProd;
import de.monticore.grammar.grammar._ast.ASTLexString;
import de.monticore.grammar.grammar_withconcepts._visitor.Grammar_WithConceptsVisitor;
import de.monticore.languages.grammar.MCGrammarSymbol;
import de.monticore.languages.grammar.MCLexRuleSymbol;

public class RegExpBuilder implements Grammar_WithConceptsVisitor {
  
  private StringBuilder b;
  
  private MCGrammarSymbol st;
  
  public RegExpBuilder(StringBuilder b, MCGrammarSymbol st) {
    this.b = b;
    this.st = st;
    
  }
  
  /**
   * Prints Lexer Rule
   * 
   * @param a
   */
  @Override
  public void handle(ASTLexProd a) {
    String del = "";
    for (ASTLexAlt alt: a.getAlts()) {
      b.append(del);
      alt.accept(getRealThis());
      del = "|";
    }
  }
  
   
  @Override
  public void handle(ASTLexBlock a) {
    
    if (a.isNegate()) {
      b.append("^");
    }
    
    b.append("(");
    
    // Visit all alternatives
    String del = "";
    for (ASTLexAlt alt: a.getLexAlts()) {
      b.append(del);
      alt.accept(getRealThis());
      del = "|";
    }
    
    // Start of Block with iteration
    b.append(")");
    b.append(HelperGrammar.printIteration(a.getIteration()));
    
  }
  
  @Override
  public void visit(ASTLexCharRange a) {
    
    b.append("[");
    if (a.isNegate()) {
      b.append("^");
    }
    b.append(a.getLowerChar());
    b.append("-");
    b.append(a.getUpperChar() + "]");
    
  }
  
  @Override
  public void visit(ASTLexChar a) {
    
    if (a.getChar().startsWith("\\")) {
      b.append("(");
      if (a.isNegate()) {
        b.append("^");
      }
      b.append(a.getChar() + ")");
    }
    else {
      
      if (a.getChar().equals("[") || a.getChar().equals("]")) {
        
        if (a.isNegate()) {
          b.append("^");
        }
        b.append(a.getChar());
        
      }
      else {
        b.append("[");
        if (a.isNegate()) {
          b.append("^");
        }
        b.append(a.getChar() + "]");
      }
      ;
    }
  }
  
  @Override
  public void visit(ASTLexString a) {
    
    for (int i = 0; i < a.getString().length(); i++) {
      
      String x = a.getString().substring(i, i + 1);
      if (x.startsWith("\\")) {
        
        b.append("(" + a.getString().substring(i, i + 2) + ")");
        i++;
      }
      else {
        b.append("[" + x + "]");
      }
    }
    
  }
  
  @Override
  public void visit(ASTLexNonTerminal a) {
    
    MCLexRuleSymbol lexrule = (MCLexRuleSymbol) st.getRule(a.getName());
    
    b.append(lexrule);
    
  }
  
}
