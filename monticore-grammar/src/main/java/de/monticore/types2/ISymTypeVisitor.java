// (c) https://github.com/MontiCore/monticore
package de.monticore.types2;

import de.monticore.types.check.SymTypeArray;
import de.monticore.types.check.SymTypeObscure;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.check.SymTypeOfIntersection;
import de.monticore.types.check.SymTypeOfNull;
import de.monticore.types.check.SymTypeOfObject;
import de.monticore.types.check.SymTypeOfUnion;
import de.monticore.types.check.SymTypeOfWildcard;
import de.monticore.types.check.SymTypePrimitive;
import de.monticore.types.check.SymTypeVariable;
import de.monticore.types.check.SymTypeVoid;

public interface ISymTypeVisitor {

  default void visit(SymTypeArray symType) {
  }

  default void visit(SymTypeObscure symType) {
  }

  default void visit(SymTypeOfFunction symType) {
  }

  default void visit(SymTypeOfGenerics symType) {
  }

  default void visit(SymTypeOfIntersection symType) {
  }

  default void visit(SymTypeOfNull symType) {
  }

  default void visit(SymTypeOfObject symType) {
  }

  default void visit(SymTypeOfUnion symType) {
  }

  default void visit(SymTypePrimitive symType) {
  }

  default void visit(SymTypeVariable symType) {
  }

  default void visit(SymTypeVoid symType) {
  }

  default void visit(SymTypeOfWildcard symType) {
  }

}
