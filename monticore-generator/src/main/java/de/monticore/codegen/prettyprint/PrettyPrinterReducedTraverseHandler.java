// (c) https://github.com/MontiCore/monticore
package de.monticore.codegen.prettyprint;

import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._visitor.GrammarHandler;
import de.monticore.grammar.grammar._visitor.GrammarTraverser;


public class PrettyPrinterReducedTraverseHandler implements GrammarHandler {
  protected GrammarTraverser traverser;
  @Override
  public GrammarTraverser getTraverser() {
    return this.traverser;
  }

  @Override
  public void setTraverser(GrammarTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public void traverse(ASTMCGrammar node) {
    // Only traverse classProds and no other productions
    for (ASTClassProd astClassProd : node.getClassProdList()) {
      astClassProd.accept(getTraverser());
    }
  }
}
