/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.serializing.cycle;

import java.io.Serializable;

/**
 *
 * @author  Pedram Mir Seyed Nazari
 *
 */
public class CycleB implements Serializable {

  private static final long serialVersionUID = 5687822153183482920L;
  
  private CycleC c;

  public CycleC getC() {
    return c;
  }

  public void setC(CycleC c) {
    this.c = c;
  }
  
}
