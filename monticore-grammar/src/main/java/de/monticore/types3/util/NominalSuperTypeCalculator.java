// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.util;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.check.SymTypeOfIntersection;
import de.monticore.types.check.SymTypeOfUnion;
import de.monticore.types.check.SymTypeVariable;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.generics.TypeParameterRelations;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class NominalSuperTypeCalculator {

  protected final static String LOG_NAME = "NominalSuperTypes";

  /**
   * supertypes, but modified according to type parameters.
   * Practically, this is meant to be used with object types including generics.
   * This returns the list of nominal supertypes,
   * e.g., in Java using extends / implements
   * e.g. Collection<Integer> is an explicit super type of List<Integer>,
   * List<? super Integer> is a super type of List<Integer>,
   * but not an explicitly defined one.
   * We consider explicitly defined super types to be the ones
   * given by the list of super types in the type symbol.
   */
  public List<SymTypeExpression> getNominalSuperTypes(SymTypeExpression thisType) {
    if (!isSupported(thisType)) {
      Log.debug("tried to get list of nominal super types "
              + "of unsupported type: " + thisType.printFullName(),
          LOG_NAME);
      return Collections.emptyList();
    }
    List<SymTypeExpression> superTypes;
    List<SymTypeExpression> unmodifiedSuperTypes =
        getUnmodifiedSuperTypesList(thisType);
    if (thisType.isGenericType()) {
      Map<SymTypeVariable, SymTypeExpression> replaceMap =
          ((SymTypeOfGenerics) thisType).getTypeVariableReplaceMap();
      superTypes = new ArrayList<>();
      for (SymTypeExpression superType : unmodifiedSuperTypes) {
        superTypes.add(TypeParameterRelations.replaceTypeVariables(superType, replaceMap));
      }
    }
    else {
      superTypes = unmodifiedSuperTypes;
    }
    return superTypes;
  }

  // Helper

  protected List<SymTypeExpression> getUnmodifiedSuperTypesList(SymTypeExpression thisType) {
    List<SymTypeExpression> unmodifiedSuperTypes;
    // object
    if (thisType.isObjectType() || thisType.isGenericType()) {
      unmodifiedSuperTypes = thisType.getTypeInfo().getSuperTypesList();
    }
    // type variable
    else if (thisType.isTypeVariable()) {
      SymTypeExpression upperBound = ((SymTypeVariable) thisType).getUpperBound();
      // directly split intersection / union
      if (upperBound.isIntersectionType() || upperBound.isUnionType()) {
        unmodifiedSuperTypes = getUnmodifiedSuperTypesList(upperBound);
      }
      else {
        unmodifiedSuperTypes = new ArrayList<>();
        unmodifiedSuperTypes.add(upperBound);
      }
    }
    // intersection
    // s. java spec 20 4.10.2:
    // The intersected types are the direct superTypes of the intersection.
    else if (thisType.isIntersectionType()) {
      unmodifiedSuperTypes = new ArrayList<>(
          ((SymTypeOfIntersection) thisType).getIntersectedTypeSet());
    }
    // union
    // The direct superTypes of a union are the direct supertypes
    // of the LuB of it's unionized types.
    // This is somewhat(!) similar to Java spec 20 14.20,
    // but Java barely supports union types in the first place.
    else if (thisType.isUnionType()) {
      Collection<SymTypeExpression> unionizedTypes =
          ((SymTypeOfUnion) thisType).getUnionizedTypeSet();
      Optional<SymTypeExpression> lubOpt =
          SymTypeRelations.leastUpperBound(unionizedTypes);
      unmodifiedSuperTypes = lubOpt
          .filter(lub -> isSupported(lub))
          .map(lub -> getNominalSuperTypes(lub))
          .orElse(Collections.emptyList());
    }
    // extension point
    else {
      Log.debug("tried to get nominal supertypes of "
              + thisType.printFullName()
              + " which is currently not supported",
          LOG_NAME
      );
      unmodifiedSuperTypes = Collections.emptyList();
    }
    return unmodifiedSuperTypes;
  }

  protected boolean isSupported(SymTypeExpression type) {
    return type.isObjectType() ||
        type.isGenericType() ||
        type.isTypeVariable() ||
        type.isIntersectionType() ||
        type.isUnionType();
  }

}
