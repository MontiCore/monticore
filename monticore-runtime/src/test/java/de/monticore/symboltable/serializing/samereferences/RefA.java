/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.serializing.samereferences;

import java.io.Serializable;

public class RefA implements Serializable {
  
  private RefB b;
  private RefC c;
  
  public RefB getB() {
    return b;
  }
  
  public void setB(RefB b) {
    this.b = b;
  }
  
  public RefC getC() {
    return c;
  }
  
  public void setC(RefC c) {
    this.c = c;
  }
  
}
