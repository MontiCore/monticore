/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.types;

import de.monticore.symboltable.SymbolKind;

/**
 * @author Pedram Mir Seyed Nazari
 */
public class JTypeSymbolKind extends TypeSymbolKind {

  private static final String NAME = JTypeSymbolKind.class.getName();

  protected JTypeSymbolKind() {
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean isKindOf(SymbolKind kind) {
    return NAME.equals(kind.getName()) || super.isKindOf(kind);
  }

}
