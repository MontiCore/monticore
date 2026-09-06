/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.parser.antlr;

import de.monticore.grammar.PredicatePair;
import de.monticore.grammar.grammar._ast.ASTGrammarNode;
import de.monticore.grammar.grammar._ast.ASTProd;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;

/**
 * Used for inlining of interface productions.
 * Describes a production that is one of the replacements of an interface production.
 */
public class InterfaceInliningAlt {
  ASTGrammarNode alternative;
  ASTProd builderNode;
  PredicatePair pp;
  ProdSymbol prodSymbol;

  public InterfaceInliningAlt(ASTGrammarNode alternative, ASTProd builderNode, PredicatePair pp, ProdSymbol prodSymbol) {
    this.alternative = alternative;
    this.builderNode = builderNode;
    this.pp = pp;
    this.prodSymbol = prodSymbol;
  }

  public ASTGrammarNode getAlternative() {
    return this.alternative;
  }

  public ASTProd getBuilderNode() {
    return this.builderNode;
  }

  public PredicatePair getPredicatePair() {
    return this.pp;
  }

  public String getOriginalName() {
    return prodSymbol.getName();
  }
}
