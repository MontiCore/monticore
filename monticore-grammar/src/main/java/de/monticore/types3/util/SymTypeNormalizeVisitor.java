// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.util;

import de.monticore.types.check.SymTypeArray;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types.check.SymTypeOfIntersection;
import de.monticore.types.check.SymTypeOfNumericWithSIUnit;
import de.monticore.types.check.SymTypeOfRegEx;
import de.monticore.types.check.SymTypeOfSIUnit;
import de.monticore.types.check.SymTypeOfTuple;
import de.monticore.types.check.SymTypeOfUnion;
import de.monticore.types.check.SymTypeVariable;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.monticore.types.check.SymTypeExpressionFactory.createBottomType;
import static de.monticore.types.check.SymTypeExpressionFactory.createObscureType;
import static de.monticore.types.check.SymTypeExpressionFactory.createTuple;

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
      // except if it has been added deliberately
      // this is likely to change in the future
      // if we add a way to normalize into empty unions
      normalized = SymTypeExpressionFactory.createBottomType();
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
    // shortcut:
    if (intersection.isEmptyIntersectedTypes()) {
      return intersection.deepClone();
    }
    SymTypeExpression normalized;

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
    if (uniqueTypes.isEmpty()) {
      // this is not expected
      Log.error("0xFDA6B internal error: "
          + "normalized intersection is empty,"
          + "this was not expected");
    }

    normalized = intersect(uniqueTypes);
    return normalized;
  }

  /**
   * Intersects types and removes any redundant information
   * (for normalized types without unions or intersections)
   */
  protected SymTypeExpression intersect(
      Collection<SymTypeExpression> uniqueTypes
  ) {
    SymTypeExpression intersected;

    // iff not overloading, certain types are incompatible
    // (int&(int,int)) -> bot
    // this is optimized to NOT check every pair of types

    // skip obscure
    if (uniqueTypes.stream().anyMatch(SymTypeExpression::isObscureType)) {
      intersected = createObscureType();
    }
    // wildcards should already have been replaced
    else if (uniqueTypes.stream().anyMatch(SymTypeExpression::isWildcard)) {
      Log.error("0xFD314 internal error: "
          + " did not expect wildcard during glb calculation");
      intersected = SymTypeExpressionFactory.createObscureType();
    }
    // if there is void, there cannot be anything else
    else if (uniqueTypes.stream().anyMatch(SymTypeExpression::isVoidType)) {
      if (uniqueTypes.size() > 1) {
        intersected = createBottomType();
      }
      else {
        intersected = SymTypeExpressionFactory.createTypeVoid();
      }
    }
    else {
      // Split into groups of types
      Set<SymTypeExpression> uniqueSimples = uniqueTypes.stream()
          .filter(t ->
              t.isPrimitive()
                  || t.isSIUnitType()
                  || t.isNumericWithSIUnitType()
          )
          .collect(Collectors.toSet());
      Set<SymTypeOfRegEx> uniqueRegEx = uniqueTypes.stream()
          .filter(SymTypeExpression::isRegExType)
          .map(SymTypeExpression::asRegExType)
          .collect(Collectors.toSet());
      Set<SymTypeExpression> uniqueObjects = uniqueTypes.stream()
          .filter(t -> t.isObjectType() || t.isGenericType())
          .collect(Collectors.toSet());
      Set<SymTypeOfFunction> uniqueFunctions = uniqueTypes.stream()
          .filter(SymTypeExpression::isFunctionType)
          .map(SymTypeExpression::asFunctionType)
          .collect(Collectors.toSet());
      Set<SymTypeOfTuple> uniqueTuples = uniqueTypes.stream()
          .filter(SymTypeExpression::isTupleType)
          .map(SymTypeExpression::asTupleType)
          .collect(Collectors.toSet());
      Set<SymTypeArray> uniqueArrays = uniqueTypes.stream()
          .filter(SymTypeExpression::isArrayType)
          .map(SymTypeExpression::asArrayType)
          .collect(Collectors.toSet());
      Set<SymTypeVariable> uniqueVars = uniqueTypes.stream()
          .filter(SymTypeExpression::isTypeVariable)
          .map(SymTypeExpression::asTypeVariable)
          .collect(Collectors.toSet());
      boolean hasNull =
          uniqueTypes.stream().anyMatch(SymTypeExpression::isNullType);

      // check that everything has been taken
      if (Stream.of(
              uniqueSimples,
              uniqueRegEx,
              uniqueObjects,
              uniqueFunctions,
              uniqueTuples,
              uniqueArrays,
              uniqueVars
          )
          .map(Set::size)
          .reduce(0, (a, b) -> a + b)
          + (hasNull ? 1 : 0)
          != uniqueTypes.size()
      ) {
        Log.error("0xFD133 internal error: "
            + "Unexpected splitting of type groups."
        );
      }
      // most types of one of the above sets (except variables) can coincide
      // with a type of another set in an intersection
      // (if RegEx's are present, Strings will have been removed already)
      if (Stream.of(
              uniqueSimples,
              uniqueRegEx,
              uniqueObjects,
              uniqueFunctions,
              uniqueTuples,
              uniqueArrays
          )
          .filter(Predicate.not(Set::isEmpty))
          .count() > 1
      ) {
        intersected = createBottomType();
      }
      else {
        SymTypeExpression intersectedWithoutVars;
        if (!uniqueSimples.isEmpty()) {
          intersectedWithoutVars = intersectSimpleTypes(uniqueSimples);
        }
        else if (!uniqueRegEx.isEmpty()) {
          intersectedWithoutVars = intersectRegExTypes(uniqueRegEx);
        }
        else if (!uniqueObjects.isEmpty()) {
          intersectedWithoutVars = intersectObjectTypes(uniqueObjects);
        }
        else if (!uniqueFunctions.isEmpty()) {
          intersectedWithoutVars = intersectFunctionTypes(uniqueFunctions);
        }
        else if (!uniqueTuples.isEmpty()) {
          intersectedWithoutVars = intersectTupleTypes(uniqueTuples);
        }
        else if (!uniqueArrays.isEmpty()) {
          intersectedWithoutVars = intersectArrayTypes(uniqueArrays);
        }
        // null, type variables and wildcards are handled separately below
        else if (hasNull) {
          // ONLY null (and variables)
          intersectedWithoutVars = SymTypeExpressionFactory.createTypeOfNull();
        }
        else {
          // ONLY type variables, but at least one of those
          intersectedWithoutVars = SymTypeExpressionFactory.createTopType();
        }

        // handle null
        if (hasNull) {
          // todo currently not supported in subTypeOf.
          // https://git.rwth-aachen.de/monticore/monticore/-/issues/3462
          if (SymTypeRelations.isSubTypeOf(
              SymTypeExpressionFactory.createTypeOfNull(),
              intersectedWithoutVars)
          ) {
            intersectedWithoutVars = SymTypeExpressionFactory.createTypeOfNull();
          }
          else {
            intersectedWithoutVars = SymTypeExpressionFactory.createBottomType();
          }
        }

        // handle type variables
        intersected = intersectedWithoutVars;
        for (SymTypeVariable var : uniqueVars) {
          if (SymTypeRelations.isSubTypeOf(var, intersected)) {
            intersected = var.deepClone();
          }
          else if (SymTypeRelations.isSubTypeOf(intersected, var)) {
            // no-op, var is not added to intersection
          }
          else {
            if (intersected.isIntersectionType()) {
              intersected.asIntersectionType().addIntersectedType(var.deepClone());
            }
            else {
              intersected = SymTypeExpressionFactory.createIntersection(
                  var.deepClone(),
                  intersected
              );
            }
          }
        }
      }
    }
    return intersected;
  }

  /**
   * primitives, SIUnits
   */
  protected SymTypeExpression intersectSimpleTypes(
      Collection<SymTypeExpression> simpleTypes
  ) {
    errorIfEmpty(simpleTypes);
    SymTypeExpression intersected;
    // of primitives (and similar), only one can ever be present
    // normalization has removed all supertypes already, e.g., int&float
    if (simpleTypes.size() > 1) {
      intersected = SymTypeExpressionFactory.createBottomType();
    }
    else {
      intersected = simpleTypes.stream().findAny().get();
    }
    return intersected;
  }

  protected SymTypeExpression intersectTupleTypes(
      Collection<SymTypeOfTuple> tuples
  ) {
    errorIfEmpty(tuples);
    SymTypeExpression intersected;
    // tuples need to have the same length
    boolean allHaveSameSize = tuples.stream()
        .map(SymTypeOfTuple::sizeTypes)
        .collect(Collectors.toSet())
        .size() == 1;
    if (!allHaveSameSize) {
      intersected = SymTypeExpressionFactory.createBottomType();
    }
    else {
      // intersect inner types
      // (A,B)&(C&D) -> (A&C,B&D)
      int tupleSize = tuples.stream().findAny().get().sizeTypes();
      List<SymTypeExpression> intersectedInnerTypes =
          new ArrayList<>(tupleSize);
      for (int i = 0; i < tupleSize; i++) {
        List<SymTypeExpression> nthTupleTypes =
            new ArrayList<>(tuples.size());
        for (SymTypeOfTuple tuple : tuples) {
          nthTupleTypes.add(tuple.getType(i));
        }
        intersectedInnerTypes.add(intersect(nthTupleTypes));
      }
      // intersection of tuples inner types must exist, e.g.:
      // Given (A,B)&(C,D), The following may not be bottom: A&C and B&D
      if (intersectedInnerTypes.stream().anyMatch(SymTypeRelations::isBottom)) {
        intersected = SymTypeExpressionFactory.createBottomType();
      }
      else {
        intersected =
            SymTypeExpressionFactory.createTuple(intersectedInnerTypes);
      }
    }
    return intersected;
  }

  protected SymTypeExpression intersectFunctionTypes(
      Collection<SymTypeOfFunction> functions
  ) {
    // IMPORTANT: Here, we lose some Information:
    // intersect(A->B,C->D) = A&B->C|D
    // This contains the functions A->D und C->B,
    // which were not there beforehand
    // s.a. MLstruct: Principal Type Inference in a Boolean Algebra
    // of Structural Types (10.1145/3563304)
    errorIfEmpty(functions);
    SymTypeExpression intersected;
    // functions need to have a comparable amount of input/output
    boolean allReturn =
        functions.stream().allMatch(f -> f.getType().isVoidType());
    boolean noneReturn =
        functions.stream().noneMatch(f -> f.getType().isVoidType());
    int maxArgSize = functions.stream()
        .map(SymTypeOfFunction::sizeArgumentTypes)
        .max(Integer::compareTo)
        .get();
    boolean allCanTakeMaxArgSize = functions.stream()
        .allMatch(f -> f.isElliptic() || f.sizeArgumentTypes() == maxArgSize);
    if (!allCanTakeMaxArgSize || !(allReturn || noneReturn)) {
      intersected = createBottomType();
    }
    else {
      List<SymTypeOfFunction> functionsFixedArity = functions.stream()
          .map(f -> f.getWithFixedArity(maxArgSize))
          .collect(Collectors.toList());
      // reuse tuple implementation for arguments
      List<SymTypeOfTuple> argsAsTuples = functionsFixedArity.stream()
          .map(f -> createTuple(f.getArgumentTypeList()))
          .collect(Collectors.toList());
      SymTypeExpression intersectedArgsTuple = intersectTupleTypes(argsAsTuples);
      if (SymTypeRelations.isBottom(intersectedArgsTuple)) {
        intersected = createBottomType();
      }
      else {
        // todo move to simplified glb calculator
        SymTypeExpression returnType = SymTypeExpressionFactory.createUnionOrDefault(
            SymTypeExpressionFactory.createObscureType(),
            functions.stream()
                .map(SymTypeOfFunction::getType)
                .collect(Collectors.toList())
        );
        List<SymTypeExpression> intersectedArgs =
            intersectedArgsTuple.asTupleType().getTypeList();
        boolean allAreElliptic = functions.stream()
            .allMatch(SymTypeOfFunction::isElliptic);
        intersected = SymTypeExpressionFactory.createFunction(
            returnType, intersectedArgs, allAreElliptic
        );
      }
    }
    return intersected;
  }

  protected SymTypeExpression intersectRegExTypes(
      Collection<SymTypeOfRegEx> regExs
  ) {
    errorIfEmpty(regExs);
    if (regExs.size() == 1) {
      return regExs.stream().findAny().get();
    }
    // Intersections of regular languages are regular,
    // thus, a solution can be found using, e.g., NFAs
    // IFF the regExes are not extended (PCRE or other).
    Log.error("0xFD173 internal error: "
        + "The current operation requires to intersect regex's."
        + " This is (currently) not supported.");
    return SymTypeExpressionFactory.createBottomType();
  }

  protected SymTypeExpression intersectArrayTypes(
      Collection<SymTypeArray> arrays
  ) {
    // this depends on the language (e.g., OCL)
    errorIfEmpty(arrays);
    if (arrays.size() == 1) {
      return arrays.stream().findAny().get();
    }
    else {
      // since arrays have to have the same type (they are NOT covariant),
      // only one can be here, if not, they cannot be compatible.
      return SymTypeExpressionFactory.createBottomType();
    }
  }

  /**
   * Objects, Generics
   */
  protected SymTypeExpression intersectObjectTypes(
      Collection<? extends SymTypeExpression> objects
  ) {
    errorIfEmpty(objects);
    // supertypes have been removed by normalization
    // Given open world semantics, we cannot rule out any object types,
    // As for any objects types A,B where could be an objects type C
    // such that C is a subType of A and B.
    // Note, that given more restrictive inheritance rules,
    // this list can be further reduced, e.g.,
    // if A and B are Java classes not in a subTyping relationship,
    // then according to Java Spec there cannot be a C
    // such that C extends A and B (disregarding the null type at this point).
    return SymTypeExpressionFactory.createIntersectionOrDefault(
        SymTypeExpressionFactory.createObscureType(), objects);
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
    // A[] -> A[]
    else {
      normalized = SymTypeExpressionFactory.createTypeArray(
          normalizedArgument, array.getDim()
      );
    }

    pushTransformedSymType(normalized);
  }

  @Override
  public void visit(SymTypeOfTuple tuple) {
    List<SymTypeExpression> innerTypes = applyToCollection(tuple.getTypeList());
    SymTypeExpression normalized;
    // no support for empty tuples
    // () -> Obscure
    if (innerTypes.isEmpty()) {
      normalized = SymTypeExpressionFactory.createObscureType();
    }
    // (A) -> A
    else if (innerTypes.size() == 1) {
      normalized = innerTypes.get(0);
    }
    // (..., Obscure, ...) -> Obscure
    else if (innerTypes.stream().anyMatch(SymTypeExpression::isObscureType)) {
      normalized = SymTypeExpressionFactory.createObscureType();
    }
    else {
      // note, this is exponential!
      // (but required to create "simple" principal types)
      // ((A|(B&C)),D) -> (A,D)|((B,D)&(C,D))
      SymTypeOfTuple tupleNormalized =
          SymTypeExpressionFactory.createTuple(innerTypes);
      List<SymTypeExpression> union = new ArrayList<>();
      for (SymTypeOfTuple unionized : splitTupleByUnion(tupleNormalized)) {
        List<SymTypeOfTuple> intersection = new ArrayList<>();
        for (SymTypeOfTuple intersected : splitTupleByIntersection(unionized)) {
          intersection.add(intersected.deepClone());
        }
        union.add(SymTypeExpressionFactory.createIntersectionOrDefault(
            SymTypeExpressionFactory.createObscureType(),
            intersection
        ));
      }
      normalized = SymTypeExpressionFactory.createUnionOrDefault(
          SymTypeExpressionFactory.createObscureType(),
          union
      );
    }

    pushTransformedSymType(normalized);
  }

  @Override
  public void visit(SymTypeOfFunction function) {
    super.visit(function);
    SymTypeOfFunction functionWithNormalizedTypes =
        popTransformedSubSymType().asFunctionType();
    SymTypeExpression normalized;

    // if any types used are Obscure, the function is Obscure
    if (functionWithNormalizedTypes.streamArgumentTypes()
        .anyMatch(SymTypeExpression::isObscureType)
        || functionWithNormalizedTypes.getType().isObscureType()
    ) {
      normalized = SymTypeExpressionFactory.createObscureType();
    }
    else {
      normalized = functionWithNormalizedTypes;
    }

    pushTransformedSymType(normalized);
  }

  @Override
  public void visit(SymTypeOfNumericWithSIUnit numericWithSIUnit) {
    if (SIUnitTypeRelations.isOfDimensionOne(numericWithSIUnit.getSIUnitType())) {
      // remove the SIUnit part, if it holds no dimension other than "1"
      // e.g. [m^2/m^2]<int> -> int
      numericWithSIUnit.getNumericType().accept(this);
    }
    else {
      // otherwise, normalize SIUnit and numeric type parts
      super.visit(numericWithSIUnit);
    }
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

  /**
   * Split tuples by unions.
   * (A|B,C&D) -> (A,C&D), (B, C&D)
   * does not create deep copies.
   */
  protected List<SymTypeOfTuple> splitTupleByUnion(SymTypeOfTuple tuple) {
    return splitTupleByUnion(tuple, 0);
  }

  protected List<SymTypeOfTuple> splitTupleByUnion(
      SymTypeOfTuple tuple, int idx
  ) {
    List<SymTypeOfTuple> results = new ArrayList<>();
    if (idx >= tuple.sizeTypes()) {
      results.add(SymTypeExpressionFactory.createTuple());
    }
    else {
      Collection<SymTypeExpression> currentIdxTypes;
      if (tuple.getType(idx).isUnionType() &&
          !SymTypeRelations.isBottom(tuple.getType(idx))
      ) {
        currentIdxTypes =
            tuple.getType(idx).asUnionType().getUnionizedTypeSet();
      }
      else {
        currentIdxTypes = Collections.singleton(tuple.getType(idx));
      }
      for (SymTypeExpression type : currentIdxTypes) {
        for (SymTypeOfTuple postFix : splitTupleByUnion(tuple, idx + 1)) {
          SymTypeOfTuple currentTuple = SymTypeExpressionFactory.createTuple();
          currentTuple.addType(type);
          currentTuple.addAllTypes(postFix.getTypeList());
          results.add(currentTuple);
        }
      }
    }
    return results;
  }

  /**
   * Split tuples by intersections.
   * (A|B,C&D) -> (A|B,C), (A|B, D)
   * does not create deep copies.
   */
  protected List<SymTypeOfTuple> splitTupleByIntersection(SymTypeOfTuple tuple) {
    return splitTupleByIntersection(tuple, 0);
  }

  protected List<SymTypeOfTuple> splitTupleByIntersection(
      SymTypeOfTuple tuple, int idx
  ) {
    List<SymTypeOfTuple> results = new ArrayList<>();
    if (idx >= tuple.sizeTypes()) {
      results.add(SymTypeExpressionFactory.createTuple());
    }
    else {
      Collection<SymTypeExpression> currentIdxTypes;
      if (tuple.getType(idx).isIntersectionType() &&
          !SymTypeRelations.isTop(tuple.getType(idx))
      ) {
        currentIdxTypes =
            tuple.getType(idx).asIntersectionType().getIntersectedTypeSet();
      }
      else {
        currentIdxTypes = Collections.singleton(tuple.getType(idx));
      }
      for (SymTypeExpression type : currentIdxTypes) {
        for (SymTypeOfTuple postFix :
            splitTupleByIntersection(tuple, idx + 1)) {
          SymTypeOfTuple currentTuple = SymTypeExpressionFactory.createTuple();
          currentTuple.addType(type);
          currentTuple.addAllTypes(postFix.getTypeList());
          results.add(currentTuple);
        }
      }
    }
    return results;
  }

  protected void errorIfEmpty(Collection<? extends SymTypeExpression> types) {
    if (types.isEmpty()) {
      Log.error("0xFD232 internal error: expected non-empty collection");
    }
  }

}
