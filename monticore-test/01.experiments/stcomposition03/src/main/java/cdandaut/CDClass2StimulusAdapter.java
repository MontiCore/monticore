/* (c) https://github.com/MontiCore/monticore */

package cdandaut;

import automata7._symboltable.StimulusSymbol;
import basiccd._symboltable.CDClassSymbol;

public class CDClass2StimulusAdapter extends StimulusSymbol {

  protected CDClassSymbol original;

  public CDClassSymbol getAdaptee() {
    return original;
  }

  public CDClass2StimulusAdapter(CDClassSymbol o){
    super(o.getName());
    this.original = o;
  }

  @Override public String getName() {
    return original.getName();
  }

}
