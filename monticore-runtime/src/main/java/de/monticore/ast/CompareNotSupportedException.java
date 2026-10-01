/* (c) https://github.com/MontiCore/monticore */

package de.monticore.ast;

import java.io.Serial;

public class CompareNotSupportedException extends RuntimeException {
  @Serial
  private static final long serialVersionUID = 1360314798474951220L;

  public CompareNotSupportedException(String message) {
    super(message);
  }
}
