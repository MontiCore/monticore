/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mccomponenttypes._ast;

import de.monticore.symboltable.ISymbol;
import de.monticore.types.mccomponenttypes.MCComponentTypesMill;

import java.util.Optional;

public interface ASTMCComponentType extends ASTMCComponentTypeTOP {

  /**
   * Conversion to a compact string, such as {@code "Component"} or
   * {@code "pkg.Component<String>"}.
   */
  default String printType() {
    return MCComponentTypesMill.prettyPrint(this, false);
  }

  Optional<? extends ISymbol> getDefiningSymbol();

  void setDefiningSymbol(ISymbol symbol);
}
