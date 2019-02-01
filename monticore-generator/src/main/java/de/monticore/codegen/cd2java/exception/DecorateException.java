package de.monticore.codegen.cd2java.exception;

public class DecorateException extends RuntimeException {

  private final DecoratorErrorCode errorCode;

  public DecorateException() {
    this(null);
  }

  public DecorateException(DecoratorErrorCode errorCode) {
    this.errorCode = errorCode;
  }
}
