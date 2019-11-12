/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.factories.exception;

/**
 * @deprecated will be transfered into CD4A
 * first the deprecation of MCTypeFacade has to be removed, then the CDFactoryErrorCode enum can be transfered to CD4A
 * after release of CD4A with CDFactoryErrorCode this enum can be removed
 */
@Deprecated
public enum CDFactoryErrorCode {

  COULD_NOT_CREATE_ATTRIBUTE(10, "Could not create CD attribute: '%s'"),
  COULD_NOT_CREATE_METHOD(20, "Could not create CD method: '%s'"),
  COULD_NOT_CREATE_TYPE(30, "Could not create CD type: '%s'");

  final int code;

  final String message;

  CDFactoryErrorCode(final int code, final String message) {
    this.code = code;
    this.message = message;
  }

  public String getError(String definition) {
    return String.format("0x%04X", code) + ": " + String.format(message, definition);
  }
}
