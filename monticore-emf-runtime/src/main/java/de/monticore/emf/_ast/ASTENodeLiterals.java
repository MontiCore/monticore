/* (c) https://github.com/MontiCore/monticore */

package de.monticore.emf._ast;

import org.eclipse.emf.common.util.Enumerator;

/**
 * 
 * Literals of the model object {@link ASTENode}
 *
 */
public enum ASTENodeLiterals implements Enumerator {
  // Literal Object DEFAULT
  DEFAULT(0);
  
  public static final int DEFAULT_VALUE = 0;
  
  protected int intValue;
  
  private ASTENodeLiterals(int intValue) {
    this.intValue = intValue;
  }
  
  public int intValue() {
    return intValue;
  }
  
  public String getName() {
    return toString();
  }
  
  public String getLiteral() {
    return toString();
  }
  
  public int getValue() {
    return intValue;
  }
  
}
