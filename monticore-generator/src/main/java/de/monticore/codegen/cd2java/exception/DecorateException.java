/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.exception;

public class DecorateException extends RuntimeException {

  public DecorateException(DecoratorErrorCode errorCode, String definition) {
    super(errorCode.getError(definition));
  }

  public DecorateException(DecoratorErrorCode errorCode, String definition, Throwable t) {
    super(errorCode.getError(definition), t);
  }
}
