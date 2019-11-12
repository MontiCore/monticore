/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.grammar._symboltable;

import java.util.ArrayList;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class RuleComponentSymbol extends RuleComponentSymbolTOP  {

  /**
   * Only for nonterminals. E.g., in u:R R is the name of the referenced prod.
   */
  private ProdSymbolLoader referencedProd;

  public RuleComponentSymbol(String name) {
    super(name);
    setSubProdList(new ArrayList<>()); //TODO: Move to superclass
  }

  public void setReferencedProd(ProdSymbolLoader referencedProd) {
    this.referencedProd = referencedProd;
  }

  /**
   * @return A reference to the defining production of this component, e.g., the
   * defining prod for the nonterminal <code>... = s:A</code> is the production
   * <code>A = ...</code>.
   */
  public Optional<ProdSymbolLoader> getReferencedProd() {
    return ofNullable(referencedProd);
  }


  public void addSubProdComponent(String constantName) {
    getSubProdList().add(constantName);
  }

// TODO mache usageName optional
  @Override
  public String getUsageName() {
    if (usageName == null) {
      return "";
    }
    else {
      return usageName;
    }
  }
}
