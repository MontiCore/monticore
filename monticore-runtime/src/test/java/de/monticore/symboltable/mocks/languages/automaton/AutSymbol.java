/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.automaton;

import de.monticore.symboltable.CommonSymbol;

public class AutSymbol extends CommonSymbol {

  public static final StateKind KIND = new StateKind();
  
  public AutSymbol(String name) {
    super(name, AutSymbol.KIND);
  }

}
