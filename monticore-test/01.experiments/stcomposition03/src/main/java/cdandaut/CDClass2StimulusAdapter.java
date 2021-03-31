/* (c) https://github.com/MontiCore/monticore */

package cdandaut;

import automata7._symboltable.StimulusSymbol;
import basiccd._symboltable.CDClassSymbol;

public class CDClass2StimulusAdapter extends StimulusSymbol {

  protected CDClassSymbol delegate;

  public CDClass2StimulusAdapter(CDClassSymbol delegate){
    super(delegate.getName());
    this.delegate = delegate;
  }

  @Override public String getName() {
    return delegate.getName();
  }

}
