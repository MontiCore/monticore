/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable;


/**
 * @author Pedram Mir Seyed Nazari
 *
 */
public class SymbolNameAndKindPredicate implements SymbolPredicate {
  
  private final String symbolName;
  private final SymbolKind symbolKind;
  
  public SymbolNameAndKindPredicate(final String symbolName, final SymbolKind symbolKind) {
    this.symbolName = symbolName;
    this.symbolKind = symbolKind;
  }
  
  /**
   * @see com.google.common.base.Predicate#apply(java.lang.Object)
   */
  @Override
  public boolean test(final Symbol symbol) {
    return (symbol != null)
        && symbol.getName().equals(symbolName)
        && symbol.getKind().isKindOf(symbolKind);
  }
  
}
