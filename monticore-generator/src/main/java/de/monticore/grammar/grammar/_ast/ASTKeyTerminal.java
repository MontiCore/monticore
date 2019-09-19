/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.grammar._ast;

import java.util.List;
import java.util.Optional;

public  class ASTKeyTerminal extends ASTKeyTerminalTOP {


  public ASTKeyTerminal() {
  }

  public ASTKeyTerminal(Optional<String> usageName, List<String> strings, String name, int iteration) {
    super(usageName, strings, name, iteration);
  }

  public String getName() {
    return getString(0);
  }
}
