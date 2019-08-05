/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar;

import java.util.Optional;

import de.monticore.grammar.grammar._ast.ASTLexAlt;
import de.monticore.grammar.grammar._ast.ASTLexBlock;
import de.monticore.grammar.grammar._ast.ASTLexChar;
import de.monticore.grammar.grammar._ast.ASTLexCharRange;
import de.monticore.grammar.grammar._ast.ASTLexNonTerminal;
import de.monticore.grammar.grammar._ast.ASTLexProd;
import de.monticore.grammar.grammar._ast.ASTLexString;
import de.monticore.grammar.grammar_withconcepts._visitor.Grammar_WithConceptsVisitor;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.grammar.symboltable.MCProdSymbol;

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
    for (ASTLexAlt alt: a.getAltList()) {
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
    for (ASTLexAlt alt: a.getLexAltList()) {
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
        if (needsEscapeChar(x)) {
          x = "\\".concat(x);
        }
        b.append("[" + x + "]");
      }
    }
    
  }
  
  private boolean needsEscapeChar(String x) {
    return "^".equals(x);
  }
  
  @Override
  public void visit(ASTLexNonTerminal a) {
    Optional<MCProdSymbol> lexrule = st.getProd(a.getName());
    b.append(lexrule.isPresent()? lexrule.get().getName():"");
    
  }
  
}
