package de.monticore.codegen.cd2java.exception;

public enum DecoratorErrorCode {
  CD_SYMBOL_NOT_FOUND(10, "Could not resolve CD symbol '%s'")
  ;

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
