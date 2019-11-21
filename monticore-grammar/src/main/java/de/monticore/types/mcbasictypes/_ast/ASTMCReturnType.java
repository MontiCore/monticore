/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcbasictypes._ast;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import de.monticore.types.prettyprint.MCFullGenericTypesPrettyPrinter;

public class ASTMCReturnType extends ASTMCReturnTypeTOP {
  public ASTMCReturnType() {
  }

  /**
   * Conversion to a compact string, such as "int", "Person", "List< A >"
   */
  public String printType(MCBasicTypesPrettyPrinter pp) {
    return pp.prettyprint(this);
  }

}
