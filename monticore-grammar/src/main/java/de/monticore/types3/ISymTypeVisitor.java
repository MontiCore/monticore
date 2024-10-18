// (c) https://github.com/MontiCore/monticore
package de.monticore.types3;

import de.monticore.types.check.SymTypeArray;
import de.monticore.types.check.SymTypeObscure;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.check.SymTypeOfIntersection;
import de.monticore.types.check.SymTypeOfNull;
import de.monticore.types.check.SymTypeOfObject;
import de.monticore.types.check.SymTypeOfRegEx;
import de.monticore.types.check.SymTypeOfNumericWithSIUnit;
import de.monticore.types.check.SymTypeOfSIUnit;
import de.monticore.types.check.SymTypeOfTuple;
import de.monticore.types.check.SymTypeOfUnion;
import de.monticore.types.check.SymTypeOfWildcard;
import de.monticore.types.check.SymTypePrimitive;
import de.monticore.types.check.SymTypeVariable;
import de.monticore.types.check.SymTypeVoid;

public interface ISymTypeVisitor {

  default void visit(SymTypeArray array) {
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
