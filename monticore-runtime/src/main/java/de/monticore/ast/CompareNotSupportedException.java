/* (c) https://github.com/MontiCore/monticore */

package de.monticore.ast;

public class CompareNotSupportedException extends RuntimeException {
  private static final long serialVersionUID = 1360314798474951220L;

  public CompareNotSupportedException(String message) {
    super(message);
  }
}
