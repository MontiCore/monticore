/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable;

/**
 * 
 * @deprecated SymbolKind will be removed soon
 */
public interface SymbolKind {

  @Deprecated //will be removed in 5.0.3
  SymbolKind KIND = new SymbolKind() {
  };

  default String getName() {
    return SymbolKind.class.getName();
  }

  /**
   * Checks, whether this symbol kind is a kind of the given symbol kind.
   * By default, this is true, if this symbol kind
   * is a sub-type of <code>kind</code>.
   *
   * @param kind
   * @return true, if this symbol kind is a kind of the given symbol kind.
   */
  default boolean isKindOf(SymbolKind kind) {
    return (kind != null) && kind.getName().equals(this.getName());
  }

  default boolean isSame(SymbolKind kind) {
    return (kind != null) && isKindOf(kind) && kind.isKindOf(this);
  }
}
