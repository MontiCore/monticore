/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.statechart;

import de.monticore.symboltable.CommonSymbol;

public class StateSymbol extends CommonSymbol {

  public static final StateKind KIND = new StateKind();
  
  public StateSymbol(String name) {
    super(name, StateSymbol.KIND);
  }

}
