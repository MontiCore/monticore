/* (c) https://github.com/MontiCore/monticore */

package javaaut._symboltable;

import automata5._symboltable.AutomatonSymbol;

import basicjava._symboltable.MethodSymbol;

public class Automaton2MethodAdapter extends MethodSymbol {

  protected AutomatonSymbol delegate;

  public Automaton2MethodAdapter(AutomatonSymbol delegate){
    super(delegate.getName());
    this.delegate = delegate;
  }

  @Override public String getName() {
    return delegate.getName();
  }

}
