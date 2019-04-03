/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.modifiers;

import com.google.common.base.Predicate;
import de.monticore.symboltable.ISymbol;
import de.monticore.symboltable.ISymbolPredicate;


public class IncludesAccessModifierSymbolPredicate implements ISymbolPredicate {

  private final AccessModifier modifier;

  public IncludesAccessModifierSymbolPredicate(AccessModifier modifier) {
    this.modifier = modifier;
  }

  /**
   * @see Predicate#apply(Object)
   */
  @Override
  public boolean test(ISymbol symbol) {
    return modifier.includes(symbol.getAccessModifier());
  }

}
