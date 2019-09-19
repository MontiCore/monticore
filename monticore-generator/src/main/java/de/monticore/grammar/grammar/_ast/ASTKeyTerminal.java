/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.grammar._ast;

import de.monticore.ast.ASTCNode;
import de.monticore.ast.ASTNode;
import de.monticore.ast.Comment;
import de.se_rwth.commons.logging.Log;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

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
