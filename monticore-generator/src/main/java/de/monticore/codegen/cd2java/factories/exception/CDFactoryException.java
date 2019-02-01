package de.monticore.codegen.cd2java.factories.exception;

public class CDFactoryException extends RuntimeException {

  public CDFactoryException(CDFactoryErrorCode errorCode, String definition) {
    super(errorCode.getError(definition));
  }

  public CDFactoryException(CDFactoryErrorCode errorCode, String definition, Throwable t) {
    super(errorCode.getError(definition), t);
  }
}
