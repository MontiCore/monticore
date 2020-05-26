/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.grammar._ast;

public class ASTConstant extends ASTConstantTOP {

  @Override
  public String getName() {
    if (isPresentKeyConstant()) {
      return getKeyConstant().getString(0);
    }
    return getString();
  }
}
