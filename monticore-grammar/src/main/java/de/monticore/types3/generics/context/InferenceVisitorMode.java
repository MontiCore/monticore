package de.monticore.types3.generics.context;

/**
 * Multiple phases are required to calculate the final (compile-time)
 * type of some expressions.
 * This lists the different phases.
 */
public enum InferenceVisitorMode {

  /**
   * Checks if an expression is pertinent to applicability.
   * S. Java language specification 21 15.12.2.2.
   */
  APPLICABILITY_TEST,

  /**
   * Checks for expression compatibility with a target type.
   * The target type may contain free type variables, thus,
   * it cannot be used (in every case) to calculate the compile-time type.
   * S. Java language specification 21 18.5.2.1
   */
  EXPRESSION_COMPATIBILITY_REDUCTION,

  /**
   * Tries to calculate the compile-time type.
   * This is the default mode.
   */
  TYPE_CHECKING
}
