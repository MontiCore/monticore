/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar;

import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.grammar.grammar._visitor.GrammarHandler;
import de.monticore.grammar.grammar._visitor.GrammarTraverser;
import de.monticore.grammar.grammar._visitor.GrammarVisitor2;

import java.util.Optional;

public class RegExpBuilder implements GrammarVisitor2, GrammarHandler {

  protected StringBuilder b;

  protected MCGrammarSymbol st;

  protected GrammarTraverser traverser;

  @Override
  public GrammarTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(GrammarTraverser traverser) {
    this.traverser = traverser;
  }

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
      alt.accept(getTraverser());
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
      alt.accept(getTraverser());
      del = "|";
    }

    // Start of Block with iteration
    b.append(")");
    b.append(printIteration(a.getIteration()));

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

      if ("[".equals(a.getChar()) || "]".equals(a.getChar())) {

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

  protected boolean needsEscapeChar(String x) {
    return "^".equals(x);
  }

  @Override
  public void visit(ASTLexNonTerminal a) {
    Optional<ProdSymbol> lexrule = st.getProd(a.getName());
    b.append(lexrule.isPresent()? lexrule.get().getName():"");

  }

  protected String printIteration(int i) {
    switch (i) {
      case ASTConstantsGrammar.PLUS:
        return "+";
      case ASTConstantsGrammar.STAR:
        return "*";
      case ASTConstantsGrammar.QUESTION:
        return "?";
      default:
        return "";
    }
  }
}
