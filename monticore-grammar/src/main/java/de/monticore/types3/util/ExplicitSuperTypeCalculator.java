// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.util;

import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ExplicitSuperTypeCalculator {

  protected SymTypeVariableReplaceVisitor replaceVisitor;

  public ExplicitSuperTypeCalculator() {
    // default values
    replaceVisitor = new SymTypeVariableReplaceVisitor();
  }

  /**
   * supertypes, but modified according to type parameters.
   * Practically, this is meant to be used with object types including generics.
   * This returns the list of explicitly defined supertypes,
   * e.g., in Java using extends / implements
   * e.g. Collection<Integer> is an explicit super type of List<Integer>,
   * List<? super Integer> is a super type of List<Integer>,
   * but not an explicitly defined one.
   * We consider explicitly defined super types to be the ones
   * given by the list of super types in the type symbol.
   */
  public List<SymTypeExpression> getExplicitSuperTypes(SymTypeExpression thisType) {
    if (!thisType.hasTypeInfo()) {
      Log.error("0xFDA11 internal error: "
          + "tried to get list of explicit super types "
          + "of a type without symbol: " + thisType.printFullName());
      return Collections.emptyList();
    }
    List<SymTypeExpression> superTypes;
    List<SymTypeExpression> unmodifiedSuperTypes =
        thisType.getTypeInfo().getSuperTypesList();
    if (thisType.isGenericType()) {
      Map<TypeVarSymbol, SymTypeExpression> replaceMap =
          ((SymTypeOfGenerics) thisType).getTypeVariableReplaceMap();
      superTypes = new ArrayList<>();
      for (SymTypeExpression superType : unmodifiedSuperTypes) {
        superTypes.add(replaceVariables(superType, replaceMap));
      }
    }
    else {
      superTypes = unmodifiedSuperTypes;
    }
    return superTypes;
  }

  // Helper

  protected SymTypeExpression replaceVariables(
      SymTypeExpression type,
      Map<TypeVarSymbol, SymTypeExpression> replaceMap) {
    return replaceVisitor.calculate(type, replaceMap);
  }

}
