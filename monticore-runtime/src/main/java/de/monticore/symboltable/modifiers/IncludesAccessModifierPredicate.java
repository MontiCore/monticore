/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.modifiers;

import com.google.common.base.Predicate;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.SymbolPredicate;

public class IncludesAccessModifierPredicate implements SymbolPredicate {

  private final AccessModifier modifier;

  public IncludesAccessModifierPredicate(AccessModifier modifier) {
    this.modifier = modifier;
  }

  /**
   * @see Predicate#apply(Object)
   */
  @Override
  public boolean test(Symbol symbol) {
    return modifier.includes(symbol.getAccessModifier());
  }

}
