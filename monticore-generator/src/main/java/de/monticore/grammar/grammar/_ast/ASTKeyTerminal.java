/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.grammar._ast;

public  class ASTKeyTerminal extends ASTKeyTerminalTOP {

  public ASTKeyTerminal() {
  }

  public String getName() {
    return getKeyConstant().getString(0);
  }
}
