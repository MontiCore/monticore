/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcbasictypes._ast;

import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;

public class ASTMCReturnType extends ASTMCReturnTypeTOP {
  public ASTMCReturnType() {
  }

  /**
   * Conversion to a compact string, such as "int", "Person", "List< A >"
   */
  @Deprecated(forRemoval = true)
  public String printType(MCBasicTypesFullPrettyPrinter pp) {
    return printType();
  }

  /**
   * Conversion to a compact string, such as "int", "Person", "List< A >"
   */
  public String printType() {
    return MCBasicTypesMill.prettyPrint(this, false);
  }

}
