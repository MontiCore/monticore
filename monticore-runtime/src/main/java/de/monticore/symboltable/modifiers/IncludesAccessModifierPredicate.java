/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.modifiers;

import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.SymbolPredicate;

/**
 * @author Pedram Mir Seyed Nazari
 *
 */
public class IncludesAccessModifierPredicate implements SymbolPredicate {
  
  private final AccessModifier modifier;

  public IncludesAccessModifierPredicate(AccessModifier modifier) {
    this.modifier = modifier;
  }
  
  /**
   * @see com.google.common.base.Predicate#apply(java.lang.Object)
   */
  @Override
  public boolean test(Symbol symbol) {
    return modifier.includes(symbol.getAccessModifier());
  }
  
}
