/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.serializing.cycle;

import java.io.Serializable;

public class CycleA implements Serializable{
  
  private static final long serialVersionUID = -4407341358814369723L;
  
  private CycleB b;

  public CycleB getB() {
    return b;
  }

  public void setB(CycleB b) {
    this.b = b;
  }
  
}
