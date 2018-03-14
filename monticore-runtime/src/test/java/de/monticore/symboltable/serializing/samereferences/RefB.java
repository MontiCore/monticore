/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.serializing.samereferences;

import java.io.Serializable;

public class RefB implements Serializable {
  
  private RefA a;
  private RefC c;
  
  public RefA getA() {
    return a;
  }
  
  public void setA(RefA a) {
    this.a = a;
  }
  
  public RefC getC() {
    return c;
  }
  
  public void setC(RefC c) {
    this.c = c;
  }
  
}
