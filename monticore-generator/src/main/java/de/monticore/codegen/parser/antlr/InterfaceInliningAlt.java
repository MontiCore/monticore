package de.monticore.codegen.parser.antlr;

import de.monticore.grammar.PredicatePair;
import de.monticore.grammar.grammar._ast.ASTGrammarNode;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;

/**
 * Used for inlining of interface productions.
 * Describes a production that is one of the replacements of an interface production.
 */
public class InterfaceInliningAlt {
  ASTGrammarNode alternative;
  PredicatePair pp;
  ProdSymbol prodSymbol;

  public InterfaceInliningAlt(ASTGrammarNode alternative, PredicatePair pp, ProdSymbol prodSymbol) {
    this.alternative = alternative;
    this.pp = pp;
    this.prodSymbol = prodSymbol;
  }

  public ASTGrammarNode getAlternative() {
    return this.alternative;
  }

  public PredicatePair getPredicatePair() {
    return this.pp;
  }

  public String getOriginalName() {
    return prodSymbol.getName();
  }
}
