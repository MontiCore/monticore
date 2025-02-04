/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcbasictypes._ast;

import de.monticore.types.mcbasictypes.MCBasicTypesMill;

public class ASTMCReturnType extends ASTMCReturnTypeTOP {
  public ASTMCReturnType() {
  }

  /**
   * Conversion to a compact string, such as "int", "Person", "List< A >"
   */
  public String printType() {
    return MCBasicTypesMill.prettyPrint(this, false);
  }

}
