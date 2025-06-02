// (c) https://github.com/MontiCore/monticore
package de.monticore.types3;

import de.monticore.types.check.*;

public interface ISymTypeVisitor {

  default void visit(SymTypeArray array) {
  }

  default void visit(SymTypeInferenceVariable infVar) {
  }

  default void visit(SymTypeObscure obscure) {
  }

  default void visit(SymTypeOfFunction function) {
  }

  default void visit(SymTypeOfGenerics generic) {
  }

  default void visit(SymTypeOfIntersection intersection) {
  }

  default void visit(SymTypeOfNull nullSymType) {
  }

  default void visit(SymTypeOfObject object) {
  }

  default void visit(SymTypeOfRegEx regex) {

  }

  default void visit(SymTypeOfTuple tuple) {
  }

  default void visit(SymTypeOfUnion union) {
  }

  default void visit(SymTypePrimitive primitive) {
  }

  default void visit(SymTypeOfSIUnit siUnit) {
  }

  default void visit(SymTypeOfNumericWithSIUnit numericWithSIUnit) {
  }

  default void visit(SymTypeVariable typeVar) {
  }

  default void visit(SymTypeVoid voidSymType) {
  }

  default void visit(SymTypeOfWildcard wildcard) {
  }

}
