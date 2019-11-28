/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.grammar._symboltable;

import java.util.Optional;


 public class RuleComponentSymbol extends RuleComponentSymbolTOP  {

  public RuleComponentSymbol(String name) {
    super(name);
  }

  /**
   * @return A reference to the defining production of this component, e.g., the
   * defining prod for the nonterminal <code>... = s:A</code> is the production
   * <code>A = ...</code>.
   */
  public Optional<ProdSymbolLoader> getReferencedProd() {
    if (isPresentReferencedType()) {
      return Optional.of(new ProdSymbolLoader(getReferencedType(), getEnclosingScope()));
    }
    return Optional.empty();
  }

}
