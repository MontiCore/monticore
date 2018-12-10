/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable;

public interface SymbolKind {

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
    // TODO PN The following statement makes use of reflection, and hence, will be soon
    //         (i.e., after next bootstrapping) replaced by: kind.getName().equals(getName());
    return kind.equals(KIND) || kind.getClass().isAssignableFrom(this.getClass());
  }

  default boolean isSame(SymbolKind kind) {
    return (kind != null) && isKindOf(kind) && kind.isKindOf(this);
  }
}
