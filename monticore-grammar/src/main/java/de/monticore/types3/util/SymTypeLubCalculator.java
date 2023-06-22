// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.util;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeArray;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfIntersection;
import de.monticore.types.check.SymTypeOfUnion;
import de.monticore.types3.SymTypeRelations;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.monticore.types.check.SymTypeExpressionFactory.createIntersection;
import static de.monticore.types.check.SymTypeExpressionFactory.createObscureType;
import static de.monticore.types.check.SymTypeExpressionFactory.createPrimitive;
import static de.monticore.types.check.SymTypeExpressionFactory.createTypeArray;

/**
 * calculates the least upper bound of a collection of SymTypes
 * delegate of SymTypeRelations
 */
public class SymTypeLubCalculator {

  protected SymTypeRelations symTypeRelations;

  public SymTypeLubCalculator(SymTypeRelations symTypeRelations) {
    this.symTypeRelations = symTypeRelations;
  }

  SymTypeRelations getSymTypeRelations() {
    return symTypeRelations;
  }

  public Optional<SymTypeExpression> leastUpperBound(
      Collection<SymTypeExpression> types) {
    Optional<SymTypeExpression> lub;
    // the function may be extended to calculate further upper bounds
    // e.g. in Java:
    // lub(A, B[]) is lub(A, arraySuperType)

    // normalize the types
    // we rely on the types being structured
    // unions on first level, intersections on second
    // and arrays on the third
    Set<SymTypeExpression> typeSet = types.stream()
        .map(symTypeRelations::normalize)
        .collect(Collectors.toSet());

    // unpack unions
    Set<SymTypeExpression> typesSet_tmp = typeSet;
    typeSet = new HashSet<>();
    for (SymTypeExpression type : typesSet_tmp) {
      if (type.isUnionType()) {
        typeSet.addAll(((SymTypeOfUnion) type).getUnionizedTypeSet());
      }
      else {
        typeSet.add(type);
      }
    }

    // now that we removed the top level unions,
    // remove top level and intersection level variables,
    // without removing the top level intersections yet
    // we can do this as we do not plan to support constraint solving
    typesSet_tmp = typeSet;
    typeSet = new HashSet<>();
    for (SymTypeExpression type : typesSet_tmp) {
      if (type.isIntersectionType()) {
        SymTypeOfIntersection inter = (SymTypeOfIntersection) type;
        inter.getIntersectedTypeSet()
            .removeIf(SymTypeExpression::isTypeVariable);
        typeSet.add(symTypeRelations.normalize(inter));
      }
      // todo after discussing (and implementing) type contexts,
      // check if var is bound
      else if (type.isTypeVariable()) {
        // no-op
      }
      else {
        typeSet.add(type);
      }
    }

    // lub without any information
    if (typeSet.isEmpty()) {
      // lub of only variables (or nothing) is unbounded
      lub = Optional.empty();
    }
    // lub of all the same type
    else if (allDeepEquals(typeSet)) {
      lub = Optional.of(typeSet.stream().findFirst().get().deepClone());
    }
    // at least two different types, try (boxed) primitives first
    // lub of boolean
    else if (typeSet.stream().allMatch(t -> symTypeRelations.isBoolean(t))) {
      // note: at least one is not boxed, so unbox all
      lub = Optional.of(createPrimitive(BasicSymbolsMill.BOOLEAN));
    }
    // lub of number
    // based on Java Spec (20): Numeric Conditional Expressions
    else if (typeSet.stream().allMatch(t -> symTypeRelations.isNumericType(t))) {
      Stream<SymTypeExpression> numbers = typeSet.stream().map(symTypeRelations::unbox);
      // lub of byte
      if (numbers.allMatch(t -> symTypeRelations.isByte(t))) {
        lub = Optional.of(createPrimitive(BasicSymbolsMill.BYTE));
      }
      // lub of byte|short
      else if (numbers.allMatch(t -> symTypeRelations.isByte(t)
          || symTypeRelations.isShort(t))) {
        lub = Optional.of(createPrimitive(BasicSymbolsMill.SHORT));
      }
      // lub using numeric promotion
      else {
        lub = Optional.of(symTypeRelations.numericPromotion(
            numbers.collect(Collectors.toList())
        ));
      }
    }
    // lub of incompatible set of primitives
    else if (typeSet.stream().allMatch(t ->
        symTypeRelations.isNumericType(t) ||
            symTypeRelations.isBoolean(t))) {
      lub = Optional.of(createObscureType()); // or empty?
    }
    // here the function may be extended to calculate further upper bounds
    // cases involving subtyping
    else {
      // lub may be an intersection, e.g. two interfaces,
      // start with intersection of current types
      Set<SymTypeExpression> acceptedLubs = new HashSet<>();
      Set<SymTypeExpression> currentPotentialLubs;
      Set<SymTypeExpression> nextPotentialLubs = new HashSet<>();

      // unpack intersections
      typesSet_tmp = typeSet;
      typeSet = new HashSet<>();
      for (SymTypeExpression type : typesSet_tmp) {
        if (type.isUnionType()) {
          typeSet.addAll(((SymTypeOfUnion) type).getUnionizedTypeSet());
        }
        else {
          typeSet.add(type);
        }
      }

      // extract "arrayness"
      Set<Integer> arrayDims = new HashSet<>();
      if (typeSet.stream().allMatch(SymTypeExpression::isArrayType)) {
        typesSet_tmp = typeSet;
        typeSet = new HashSet<>();
        for (SymTypeExpression type : typesSet_tmp) {
          SymTypeArray array = (SymTypeArray) type;
          arrayDims.add(array.getDim());
          typeSet.add(array.getArgument());
        }
        // can only have lub if arrays have the same dimension
        if (arrayDims.size() == 1) {
          currentPotentialLubs = new HashSet<>(typeSet);
        }
        else {
          currentPotentialLubs = new HashSet<>();
        }
      }
      else if (typeSet.stream().noneMatch(SymTypeExpression::isArrayType)) {
        currentPotentialLubs = new HashSet<>(typeSet);
      }
      else {
        currentPotentialLubs = new HashSet<>();
      }

      // we have no unions, intersections or arrays on the top level
      // here the algorithm may be extended with regards to variables
      while (!currentPotentialLubs.isEmpty()) {
        for (SymTypeExpression type : typeSet) {
          for (Iterator<SymTypeExpression> i = currentPotentialLubs.iterator();
               i.hasNext(); ) {
            SymTypeExpression potentialLub = i.next();
            // if a type cannot be a subtype of a potential lub, it is not a lub
            if (!symTypeRelations.internal_isSubTypeOf(type, potentialLub, true)) {
              i.remove();
              // however, the supertypes could still be lubs
              Collection<SymTypeExpression> superTypes =
                  potentialLub.getTypeInfo().getSuperTypesList();
              nextPotentialLubs.addAll(superTypes);
            }
          }
        }
        acceptedLubs.addAll(currentPotentialLubs);
        currentPotentialLubs = nextPotentialLubs;
        nextPotentialLubs = new HashSet<>();
      }

      // re-add "arrayness"
      if (arrayDims.size() == 1) {
        int arrayDim = arrayDims.stream().findFirst().get();
        acceptedLubs = acceptedLubs.stream()
            .map(t -> createTypeArray(t, arrayDim))
            .collect(Collectors.toSet());
      }

      // lub is the intersection of the calculated lubs (minus superclasses)
      // exception being the empty intersection -> no lub
      if (acceptedLubs.size() > 1) {
        lub = Optional.of(symTypeRelations.normalize(createIntersection(acceptedLubs)));
      }
      else if (acceptedLubs.size() == 1) {
        lub = acceptedLubs.stream().findFirst();
      }
      else {
        lub = Optional.of(createObscureType());
      }
    }

    return lub;
  }

  // Helper

  protected boolean allDeepEquals(Collection<SymTypeExpression> types) {
    if (types.isEmpty()) {
      return true;
    }
    else {
      final SymTypeExpression typeNonUnionTmp =
          types.stream().findFirst().get();
      return types.stream().allMatch(typeNonUnionTmp::deepEquals);
    }
  }

}
