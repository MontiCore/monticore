/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcfullgenerictypes._ast;

import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;

public class ASTMCArrayType extends ASTMCArrayTypeTOP {
  public ASTMCArrayType() {
  }

  public String printTypeWithoutBrackets() {
    return this.getMCType().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter());
  }

}
