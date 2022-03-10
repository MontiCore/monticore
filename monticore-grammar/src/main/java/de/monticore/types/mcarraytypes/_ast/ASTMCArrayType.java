/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcarraytypes._ast;

import de.monticore.types.mcarraytypes.MCArrayTypesMill;

public class ASTMCArrayType extends ASTMCArrayTypeTOP {
  public ASTMCArrayType() {
  }

  public String printTypeWithoutBrackets() {
    return this.getMCType().printType(MCArrayTypesMill.mcArrayTypesPrettyPrinter());
  }

}
