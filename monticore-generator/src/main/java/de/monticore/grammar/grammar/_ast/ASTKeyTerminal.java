/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.grammar._ast;

import java.util.List;
import java.util.Optional;

public  class ASTKeyTerminal extends ASTKeyTerminalTOP {

  public ASTKeyTerminal() {
  }

  public String getName() {
    return getString(0);
  }
}
