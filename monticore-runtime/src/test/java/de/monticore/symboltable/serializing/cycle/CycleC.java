/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.serializing.cycle;

import java.io.Serializable;

public class CycleC implements Serializable {

  private static final long serialVersionUID = 3878590063907476760L;

  private CycleA a;

  public CycleA getA() {
    return a;
  }

  public void setA(CycleA a) {
    this.a = a;
  }
  
  
}
