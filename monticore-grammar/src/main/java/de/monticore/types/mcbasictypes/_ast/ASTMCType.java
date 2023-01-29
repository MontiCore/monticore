/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcbasictypes._ast;

import de.monticore.symboltable.ISymbol;
import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;

import java.util.Optional;

public interface ASTMCType extends ASTMCTypeTOP {

  /**
   * Conversion to a compact string, such as "int", "Person", "List< A >"
   */
  @Deprecated(forRemoval = true)
  default String printType(MCBasicTypesFullPrettyPrinter pp) {
    return printType();
  }

  /**
   * Conversion to a compact string, such as "int", "Person", "List< A >"
   */
  default String printType() {
    return MCBasicTypesMill.prettyPrint(this, false);
  }

  Optional<? extends ISymbol> getDefiningSymbol();

  void setDefiningSymbol(ISymbol symbol);

}
