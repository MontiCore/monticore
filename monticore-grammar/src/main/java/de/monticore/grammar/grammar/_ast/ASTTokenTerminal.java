/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.grammar._ast;

public  class ASTTokenTerminal extends ASTTokenTerminalTOP {

  public String getName() {
    return getTokenConstant().getString();
  }
}
