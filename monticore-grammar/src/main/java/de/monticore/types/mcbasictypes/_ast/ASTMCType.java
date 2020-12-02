/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcbasictypes._ast;

import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;

public interface ASTMCType extends ASTMCTypeTOP {

  /**
   * Conversion to a compact string, such as "int", "Person", "List< A >"
   */
  default String printType(MCBasicTypesFullPrettyPrinter pp) {
    return pp.prettyprint(this);
  }
}
