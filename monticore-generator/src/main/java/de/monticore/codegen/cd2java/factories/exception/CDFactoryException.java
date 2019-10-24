/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.factories.exception;

/**
 * @deprecated will be transfered into CD4A
 * first the deprecation of MCTypeFacade has to be removed, then the CDFactoryException can be tranfered to CD4A
 * after release of CD4A with CDFactoryException this class can be removed
 */
@Deprecated
public class CDFactoryException extends RuntimeException {

  public CDFactoryException(CDFactoryErrorCode errorCode, String definition) {
    super(errorCode.getError(definition));
  }

  public CDFactoryException(CDFactoryErrorCode errorCode, String definition, Throwable t) {
    super(errorCode.getError(definition), t);
  }
}
