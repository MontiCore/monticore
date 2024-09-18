/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcbasictypes._ast;

import de.monticore.symboltable.ISymbol;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;

import java.util.Optional;

public interface ASTMCType extends ASTMCTypeTOP {

  /**
   * Conversion to a compact string, such as "int", "Person", "List< A >"
   */
  default String printType() {
    return MCBasicTypesMill.prettyPrint(this, false);
  }

  Optional<? extends ISymbol> getDefiningSymbol();

  void setDefiningSymbol(ISymbol symbol);

}
