// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.util;

import de.monticore.types.check.SymTypeArray;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfIntersection;
import de.monticore.types.check.SymTypeOfSIUnit;
import de.monticore.types.check.SymTypeOfUnion;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.logging.Log;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * tries to normalize SymTypeExpressions,
 * including, but not limited to, unions and intersections
 * e.g., (A|A|B) -> A, if A extends B
 * Usage:
 * calculate(symType)
 * <p>
 * normalization of regular expressions are not supported
 * <p>
 * note, this visitor does not box/unbox,
 * boxing/unboxing should be done before this visitor if required
 */
public class SymTypeNormalizeVisitor extends SymTypeDeepCloneVisitor {

  @Override
  public void visit(SymTypeOfUnion union) {
    // set of normalized unionized types
    Set<SymTypeExpression> types =
        applyToCollection(union.getUnionizedTypeSet());
    SymTypeExpression normalized = normalizeUnionWithNormalizedTypes(
        SymTypeExpressionFactory.createUnion(types)
    );
    pushTransformedSymType(normalized);
  }

  protected SymTypeExpression normalizeUnionWithNormalizedTypes(SymTypeOfUnion union) {
    // set of already normalized unionized types
    Set<SymTypeExpression> types = new HashSet<>(union.getUnionizedTypeSet());
    // remove all occurrences of obscure
    // (A|B|obscure) -> (A|B)
    types.removeIf(SymTypeExpression::isObscureType);
    // no union of unions
    // (A|(B|C)) -> (A|B|C)
    Set<SymTypeExpression> splittedTypes = splitUnions(types);
    // here would be an opportunity to unbox types,
    // however, we lose information, so we don't do so
    // z.B. (int|Float) -> float would be a Java-esque option

    // no type twice
    // (A|A|B) -> (A|B)
    // also no subtypes
    // (A|B|C) -> (B|C) if A extends B
    Set<SymTypeExpression> uniqueTypes = new HashSet<>();
    for (SymTypeExpression newType : splittedTypes) {
      boolean shouldAdd = true;
      // if A extends B, do not add A if B is in union
      for (SymTypeExpression addedType : uniqueTypes) {
        if (SymTypeRelations.internal_isSubTypeOfPreNormalized(newType, addedType, false)) {
          shouldAdd = false;
          break;
        }
      }
      // add to the union
      if (shouldAdd) {
        // remove all subtypes that have been added already
        // if A,B both extend C, adding C to (A|B|D) results in (C|D)
        // because A extending C can be represented by replacing A with (A&C)
        // thus (A|C) -> ((A&C)|C) -> C
        uniqueTypes.removeIf(
            addedType -> SymTypeRelations.internal_isSubTypeOfPreNormalized(addedType, newType, false));
        uniqueTypes.add(newType);
      }
    }

    SymTypeExpression normalized;
    // empty union is obscure
    if (uniqueTypes.isEmpty()) {
      // empty union is obscure,
      if (!union.getUnionizedTypeSet().isEmpty()) {
        normalized = SymTypeExpressionFactory.createObscureType();
      }
      // except if it has been added deliberately
      // this is likely to change in the future
      // if we add a way to normalize into empty unions
      else {
        normalized = SymTypeExpressionFactory.createBottomType();
      }
    }
    // union of one is no union
    // (A) = A
    else if (uniqueTypes.size() == 1) {
      normalized = uniqueTypes.stream().findFirst().get();
    }
    // we still have a union
    else {
      normalized = SymTypeExpressionFactory.createUnion(uniqueTypes);
    }
    return normalized;
  }

  @Override
  public void visit(SymTypeOfIntersection intersection) {
    SymTypeExpression normalized;
    // set of normalized intersected types
    Set<SymTypeExpression> types =
        applyToCollection(intersection.getIntersectedTypeSet());
    // if any element is obscure, the whole intersection is obscure
    // (A&B&obscure) -> obscure
    if (types.stream().anyMatch(SymTypeExpression::isObscureType)) {
      normalized = SymTypeExpressionFactory.createObscureType();
    }
    else {
      // we transform an intersection that may have unions
      // into a union of intersections
      // A&B&(C|D) -> (A&B&C)|(A&B&D)
      Set<SymTypeOfIntersection> intersectionsWithoutUnions
          = intersectionOfUnions2UnionOfIntersections(types);
      // normalize each intersection
      Set<SymTypeExpression> normalizedUnionTypes = new HashSet<>();
      for (SymTypeOfIntersection intersectionWithoutUnion
          : intersectionsWithoutUnions) {
        normalizedUnionTypes.add(
            normalizeIntersectionWithoutUnions(intersectionWithoutUnion)
        );
      }
      // normalize the new union
      normalized = normalizeUnionWithNormalizedTypes(
          SymTypeExpressionFactory.createUnion(normalizedUnionTypes)
      );
    }
    pushTransformedSymType(normalized);
  }

  /**
   * normalizes an intersection
   * the contained expressions are expected to be normalized and not unions
   * s. visit(SymTypeOfIntersection)
   */
  protected SymTypeExpression normalizeIntersectionWithoutUnions(
      SymTypeOfIntersection intersection
  ) {
    Set<SymTypeExpression> types = new HashSet<>(intersection.getIntersectedTypeSet());
    // no intersection of intersections
    // (A&(B&C)) -> (A&B&C)
    Set<SymTypeExpression> splittedTypes = splitIntersections(types);
    // arrays all have the same dimension
    // (A[]&B[][]) -> Obscure
    if (splittedTypes.stream().anyMatch(SymTypeExpression::isArrayType)) {
      if (!splittedTypes.stream().allMatch(SymTypeExpression::isArrayType)
          || splittedTypes.stream()
          .map(t -> ((SymTypeArray) t).getDim())
          .collect(Collectors.toSet())
          .size() > 1
      ) {
        splittedTypes = new HashSet<>();
        splittedTypes.add(SymTypeExpressionFactory.createObscureType());
      }
    }

    // no type twice
    // (A&A&B) -> (A&B)
    // also no supertypes
    // (A&B&C) -> (A&C) if A extends B
    Set<SymTypeExpression> uniqueTypes = new HashSet<>();
    for (SymTypeExpression newType : splittedTypes) {
      boolean shouldAdd = true;
      // if A extends B, do not add B if A is in intersection
      for (SymTypeExpression addedType : uniqueTypes) {
        if (SymTypeRelations.internal_isSubTypeOfPreNormalized(addedType, newType, false)) {
          shouldAdd = false;
          break;
        }
      }
      // add to the intersection
      if (shouldAdd) {
        // remove all supertypes that have been added already
        // if A extends B, adding A to (B|C) results in (A|C)
        // because A extending B can be represented by replacing A with (A&B)
        // thus (A&B&C) -> ((A&B)&B&C) -> (A&C)
        uniqueTypes.removeIf(addedType ->
            SymTypeRelations.internal_isSubTypeOfPreNormalized(newType, addedType, false));
        uniqueTypes.add(newType);
      }
    }

    SymTypeExpression normalized;
    // empty intersection contains all values
    if (uniqueTypes.isEmpty()) {
      // this is not expected
      if (!intersection.getIntersectedTypeSet().isEmpty()) {
        Log.error("0xFDA6B internal error: "
            + "normalized intersection is empty,"
            + "this was not expected");
        normalized = SymTypeExpressionFactory.createObscureType();
      }
      // except if it has been added deliberately
      // this is likely to change in the future if we ever
      // add ways to normalize into bottom / top types
      else {
        normalized = SymTypeExpressionFactory.createTopType();
      }
    }
    // intersection of one is not an intersection
    // (A) = A
    else if (uniqueTypes.size() == 1) {
      normalized = uniqueTypes.stream().findFirst().get();
    }
    // we still have an intersection
    else {
      normalized = SymTypeExpressionFactory.createIntersection(uniqueTypes);
    }
    return normalized;
  }

  @Override
  public void visit(SymTypeArray array) {
    array.getArgument().accept(this);
    SymTypeExpression normalizedArgument = popTransformedSubSymType();
    SymTypeExpression normalized;
    // A -> A (length of 0)
    if (array.getDim() == 0) {
      normalized = normalizedArgument;
    }
    // Obscure[] -> Obscure
    else if (normalizedArgument.isObscureType()) {
      normalized = SymTypeExpressionFactory.createObscureType();
    }
    // (A[][])[] -> A[][][]
    else if (normalizedArgument.isArrayType()) {
      SymTypeArray subArray = (SymTypeArray) normalizedArgument;
      normalized = SymTypeExpressionFactory.createTypeArray(
          subArray.getArgument(), array.getDim() + subArray.getDim()
      );
    }
    // move arrays below set operations
    // (A|(B&C))[] -> (A[]|(B[]&C[]))
    else if (normalizedArgument.isUnionType()) {
      Set<SymTypeExpression> unionizedTypes =
          ((SymTypeOfUnion) normalizedArgument).getUnionizedTypeSet();
      unionizedTypes = unionizedTypes.stream()
          .map(t -> SymTypeExpressionFactory.createTypeArray(t, array.getDim()))
          .collect(Collectors.toSet());
      unionizedTypes = applyToCollection(unionizedTypes);
      normalized = SymTypeExpressionFactory.createUnion(unionizedTypes);
    }
    else if (normalizedArgument.isIntersectionType()) {
      Set<SymTypeExpression> intersectedTypes =
          ((SymTypeOfIntersection) normalizedArgument).getIntersectedTypeSet();
      intersectedTypes = intersectedTypes.stream()
          .map(t -> SymTypeExpressionFactory.createTypeArray(t, array.getDim()))
          .collect(Collectors.toSet());
      intersectedTypes = applyToCollection(intersectedTypes);
      normalized = SymTypeExpressionFactory.createIntersection(intersectedTypes);
    }
    // A[] -> A[]
    else {
      normalized = SymTypeExpressionFactory.createTypeArray(
          normalizedArgument, array.getDim()
      );
    }

    pushTransformedSymType(normalized);
  }

  @Override
  public void visit(SymTypeOfSIUnit siUnit) {
    SymTypeOfSIUnit normalized =
        SIUnitTypeRelations.internal_normalize(siUnit);
    pushTransformedSymType(normalized);
  }

  // Helpers

  /**
   * splits up unions
   * e.g., {(A|(B|C)),D} -> {A,B,C,D}
   * used for normalization
   */
  protected Set<SymTypeExpression> splitUnions(Set<SymTypeExpression> types) {
    Set<SymTypeExpression> result = new HashSet<>();
    for (SymTypeExpression type : types) {
      if (type.isUnionType()) {
        SymTypeOfUnion union = (SymTypeOfUnion) type;
        result.addAll(splitUnions(union.getUnionizedTypeSet()));
      }
      else {
        result.add(type);
      }
    }
    return result;
  }

  /**
   * splits up intersections
   * e.g., {(A&(B&C)),D} -> {A,B,C,D}
   * used for normalization
   */
  protected Set<SymTypeExpression> splitIntersections(
      Set<SymTypeExpression> types) {
    Set<SymTypeExpression> result = new HashSet<>();
    for (SymTypeExpression type : types) {
      if (type.isIntersectionType()) {
        SymTypeOfIntersection intersection = (SymTypeOfIntersection) type;
        result.addAll(splitIntersections(intersection.getIntersectedTypeSet()));
      }
      else {
        result.add(type);
      }
    }
    return result;
  }

  /**
   * takes an intersection, which may contain unions
   * and creates a union which contains intersections
   * A&B&(C|D) -> (A&B&C)|(A&B&D)
   * Note that this only calculates the given intersection,
   * not the intersection contained within the given intersection.
   * An additional characteristic to mention is that
   * NO empty intersections / unions are created
   * if there have been none to begin with
   */
  protected Set<SymTypeOfIntersection> intersectionOfUnions2UnionOfIntersections(
      Set<SymTypeExpression> intersectedTypes) {
    Set<SymTypeOfIntersection> intersections = new HashSet<>();
    if (!intersectedTypes.isEmpty()) {
      //temporarily make every non-union type in the intersection a union type
      // (A|B)&C -> (A|B)&(C)
      Set<SymTypeOfUnion> unions = new HashSet<>();
      for (SymTypeExpression type : intersectedTypes) {
        if (type.isUnionType()) {
          unions.add((SymTypeOfUnion) type);
        }
        else {
          unions.add(SymTypeExpressionFactory.createUnion(type));
        }
      }
      // create a union of intersections from the intersection of unions
      // in rare cases, this can be exponential
      // (A|B)&(C|D) -> (A&C)|(A&D)|(B&C)|(B&D)
      // handle the first union differently to set up the set of intersections
      SymTypeOfUnion firstUnion = unions.stream().findAny().get();
      unions.remove(firstUnion);
      for (SymTypeExpression type : firstUnion.getUnionizedTypeSet()) {
        intersections.add(SymTypeExpressionFactory.createIntersection(type));
      }
      //now combine the other unions with the already existing intersections
      for (SymTypeOfUnion union : unions) {
        Set<SymTypeOfIntersection> currentIntersectionSets = intersections;
        intersections = new HashSet<>();
        for (SymTypeExpression unionizedType : union.getUnionizedTypeSet()) {
          for (SymTypeOfIntersection oldIntersection : currentIntersectionSets) {
            SymTypeOfIntersection newIntersection =
                (SymTypeOfIntersection) oldIntersection.deepClone();
            newIntersection.addIntersectedType(unionizedType);
            intersections.add(newIntersection);
          }
        }
      }
      // we now have a union of intersections
    }
    // given an empty intersection, we assume that this is the top type
    else {
      // note, this has to be changed if we make changes to the top type
      intersections.add((SymTypeOfIntersection)
          SymTypeExpressionFactory.createTopType()
      );
    }
    return intersections;
  }

}
