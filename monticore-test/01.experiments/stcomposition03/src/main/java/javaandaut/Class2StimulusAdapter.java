/* (c) https://github.com/MontiCore/monticore */

package javaandaut;

import automata7._symboltable.StimulusSymbol;
import basiccd._symboltable.CDClassSymbol;
import basicjava._symboltable.ClassDeclarationSymbol;

public class Class2StimulusAdapter extends StimulusSymbol {

  protected ClassDeclarationSymbol delegate;

  public ClassDeclarationSymbol getAdaptee() {
    return delegate;
  }

  public Class2StimulusAdapter(ClassDeclarationSymbol delegate){
    super(delegate.getName());
    this.delegate = delegate;
  }

  @Override public String getName() {
    return delegate.getName();
  }

}
