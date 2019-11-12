/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.exception;

public enum DecoratorErrorCode {
  CD_SYMBOL_NOT_FOUND(10, "Could not resolve CD symbol '%s'"),
  AST_FOR_CD_TYPE_SYMBOL_NOT_FOUND(11, "Could find ASTCDClass for CDTypeSymbol '%s'"),
  AST_FOR_CD_FIELD_SYMBOL_NOT_FOUND(12, "Could find ASTCDAttribute for CDFieldSymbol '%s'"),
  CD_TYPE_NOT_FOUND(13, "Could find CDTypeSymbol '%s'"),
  EXPECTED_LIST_TYPE(14, "List type was expected for Type: '%s'"),
  WRONG_SCOPE_CLASS_ENCLOSING_SCOPE_METHODS(15, "In Scope Class setter '%s' is not allowed.");

  final int code;

  final String message;

  DecoratorErrorCode(final int code, final String message) {
    this.code = code;
    this.message = message;
  }

  public String getError(String definition) {
    return String.format("0x%04X", code) + ": " + String.format(message, definition);
  }
}
