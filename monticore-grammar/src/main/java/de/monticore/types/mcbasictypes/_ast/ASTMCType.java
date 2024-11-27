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

  /**
   * @deprecated part of TypeCheck1. instead use
   *     {@link de.monticore.types.check.SymTypeExpression#getSourceInfo()}.
   */
  @Deprecated
  Optional<? extends ISymbol> getDefiningSymbol();

  /**
   * @deprecated part of TypeCheck1. Not required in TypeCheck3.
   */
  @Deprecated
  void setDefiningSymbol(ISymbol symbol);

}
