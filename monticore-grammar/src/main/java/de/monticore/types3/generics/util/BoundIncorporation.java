// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.generics.util;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.check.SymTypeOfIntersection;
import de.monticore.types.check.SymTypeOfTuple;
import de.monticore.types.check.SymTypeOfWildcard;
import de.monticore.types.check.SymTypeVariable;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.generics.TypeParameterRelations;
import de.monticore.types3.generics.bounds.Bound;
import de.monticore.types3.generics.bounds.CaptureBound;
import de.monticore.types3.generics.bounds.SubTypingBound;
import de.monticore.types3.generics.bounds.TypeCompatibilityBound;
import de.monticore.types3.generics.bounds.TypeEqualityBound;
import de.monticore.types3.generics.bounds.UnsatisfiableBound;
import de.monticore.types3.generics.constraints.BoundWrapperConstraint;
import de.monticore.types3.generics.constraints.Constraint;
import de.monticore.types3.generics.constraints.SubTypingConstraint;
import de.monticore.types3.generics.constraints.TypeCompatibilityConstraint;
import de.monticore.types3.generics.constraints.TypeEqualityConstraint;
import de.monticore.types3.util.SymTypeCollectionVisitor;
import de.monticore.types3.util.SymTypeExpressionComparator;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static de.monticore.types.check.SymTypeExpressionFactory.createIntersection;

public class BoundIncorporation {

  /**
   * the name to be used for Log.info, etc.
   */
  protected static final String LOG_NAME = "BoundIncorporation";

  protected static BoundIncorporation delegate;

  public static void init() {
    Log.trace("init default BoundIncorporation", "TypeCheck setup");
    BoundIncorporation.delegate = new BoundIncorporation();
  }

  protected static BoundIncorporation getDelegate() {
    if (delegate == null) {
      init();
    }
    return delegate;
  }

  /**
   * returns the constraints resulting from complementary bound-pairs
   * s. JLS 21 18.3.1
   *
   * @param newBounds newly added through, e.g., constraint reduction
   * @param oldBounds have already been used to get constraints
   */
  public static List<Constraint> incorporate(
      List<Bound> newBounds,
      List<Bound> oldBounds
  ) {
    return getDelegate().calculateIncorporate(newBounds, oldBounds);
  }

  public static List<Constraint> incorporate(List<Bound> bounds) {
    return incorporate(bounds, Collections.emptyList());
  }

  protected List<Constraint> calculateIncorporate(
      List<Bound> newBounds,
      List<Bound> oldBounds
  ) {
    // shortcut to reduce log:
    if (newBounds.isEmpty()) {
      return Collections.emptyList();
    }
    Log.trace("incorporating bounds:" + System.lineSeparator()
            + this.printBounds(newBounds)
            + (oldBounds.isEmpty() ? "" : System.lineSeparator())
            + this.printBounds(oldBounds),
        LOG_NAME
    );
    List<Constraint> constraints = new ArrayList<>();

    // iterate over each pair, except (oldBound1, oldBound2)
    for (int i = 0; i < newBounds.size(); i++) {
      Bound b1 = newBounds.get(i);
      for (int j = i + 1; j < newBounds.size(); j++) {
        Bound b2 = newBounds.get(j);
        constraints.addAll(incorporate(b1, b2));
      }
      for (Bound oldBound : oldBounds) {
        constraints.addAll(incorporate(b1, oldBound));
      }
    }
    // get the additional bounds given by CaptureBounds
    for (int i = 0; i < newBounds.size(); i++) {
      if (newBounds.get(i).isCaptureBound()) {
        CaptureBound cB = (CaptureBound) newBounds.get(i);
        for (Bound impliedBound : cB.getImpliedBounds()) {
          constraints.add(new BoundWrapperConstraint(impliedBound));
        }
      }
    }

    if (constraints.isEmpty()) {
      Log.trace("incorporating bounds lead to no constraints.", LOG_NAME);
    }
    else {
      Log.trace("incorporating bounds lead to constraints:"
              + System.lineSeparator()
              + printConstraints(constraints),
          LOG_NAME
      );
    }
    return constraints;
  }

  /**
   * returns the constraints resulting from a complementary bound-pair
   * s. JLS 21 18.3.1
   */
  protected List<Constraint> incorporate(Bound b1, Bound b2) {
    List<Constraint> constraints;
    // Bound ordering is SubTyping, Compatibility, Equality, Capture
    // SubTyping
    if (b1.isSubTypingBound() && b2.isSubTypingBound()) {
      constraints = incorporate((SubTypingBound) b1, (SubTypingBound) b2);
    }
    // SubTyping, Compatibility
    else if (b1.isTypeCompatibilityBound() && b2.isTypeCompatibilityBound()) {
      constraints = incorporate((TypeCompatibilityBound) b1, (TypeCompatibilityBound) b2);
    }
    else if (b1.isSubTypingBound() && b2.isTypeCompatibilityBound()) {
      constraints = incorporate((SubTypingBound) b1, (TypeCompatibilityBound) b2);
    }
    else if (b1.isTypeCompatibilityBound() && b2.isSubTypingBound()) {
      constraints = incorporate((SubTypingBound) b2, (TypeCompatibilityBound) b1);
    }
    // SubTyping, Compatibility, Equality
    else if (b1.isTypeEqualityBound() && b2.isTypeEqualityBound()) {
      constraints = incorporate((TypeEqualityBound) b1, (TypeEqualityBound) b2);
    }
    else if (b1.isSubTypingBound() && b2.isTypeEqualityBound()) {
      constraints = incorporate((TypeEqualityBound) b2, (SubTypingBound) b1);
    }
    else if (b1.isTypeEqualityBound() && b2.isSubTypingBound()) {
      constraints = incorporate((TypeEqualityBound) b1, (SubTypingBound) b2);
    }
    else if (b1.isTypeCompatibilityBound() && b2.isTypeEqualityBound()) {
      constraints = incorporate((TypeEqualityBound) b2, (TypeCompatibilityBound) b1);
    }
    else if (b1.isTypeEqualityBound() && b2.isTypeCompatibilityBound()) {
      constraints = incorporate((TypeEqualityBound) b1, (TypeCompatibilityBound) b2);
    }
    // SubTyping, Compatibility, Equality, Capture
    else if (b1.isCaptureBound() && b2.isCaptureBound()) {
      constraints = Collections.emptyList();
    }
    else if (b1.isCaptureBound() && b2.isSubTypingBound()) {
      constraints = incorporate((CaptureBound) b1, (SubTypingBound) b2);
    }
    else if (b1.isSubTypingBound() && b2.isCaptureBound()) {
      constraints = incorporate((CaptureBound) b2, (SubTypingBound) b1);
    }
    else if (b1.isCaptureBound() && b2.isTypeCompatibilityBound()) {
      constraints = incorporate((CaptureBound) b1, (TypeCompatibilityBound) b2);
    }
    else if (b1.isTypeCompatibilityBound() && b2.isCaptureBound()) {
      constraints = incorporate((CaptureBound) b2, (TypeCompatibilityBound) b1);
    }
    else if (b1.isCaptureBound() && b2.isTypeEqualityBound()) {
      constraints = incorporate((CaptureBound) b1, (TypeEqualityBound) b2);
    }
    else if (b1.isTypeEqualityBound() && b2.isCaptureBound()) {
      constraints = incorporate((CaptureBound) b2, (TypeEqualityBound) b1);
    }
    // unsat
    else if (b1.isUnsatisfiableBound() || b2.isUnsatisfiableBound()) {
      // at least one bound is unsatisfiable
      constraints = Collections.emptyList();
    }
    else {
      // not expected
      Log.error("0xFD236 internal error: unimplemented bounds case?");
      constraints = Collections.emptyList();
    }
    return constraints;
  }

  protected List<Constraint> incorporate(
      TypeEqualityBound b1, TypeEqualityBound b2
  ) {
    List<Constraint> constraints = new ArrayList<>();
    Optional<TypeEqualityBound> b1f = b1.getFlipped();
    Optional<TypeEqualityBound> b2f = b2.getFlipped();

    incorporateNonSymmetrical(b1, b2).ifPresent(constraints::add);
    incorporateNonSymmetrical(b2, b1).ifPresent(constraints::add);
    if (b1f.isPresent()) {
      incorporateNonSymmetrical(b1f.get(), b2).ifPresent(constraints::add);
      incorporateNonSymmetrical(b2, b1f.get()).ifPresent(constraints::add);
    }
    if (b2f.isPresent()) {
      incorporateNonSymmetrical(b1, b2f.get()).ifPresent(constraints::add);
      incorporateNonSymmetrical(b2f.get(), b1).ifPresent(constraints::add);
    }
    if (b1f.isPresent() && b2f.isPresent()) {
      incorporateNonSymmetrical(b1f.get(), b2f.get()).ifPresent(constraints::add);
      incorporateNonSymmetrical(b2f.get(), b1f.get()).ifPresent(constraints::add);
    }

    return constraints;
  }

  protected Optional<Constraint> incorporateNonSymmetrical(
      TypeEqualityBound b1, TypeEqualityBound b2
  ) {
    Optional<Constraint> result;
    // a = S and a = T implies <S = T>
    if (b1.getFirstType().denotesSameVar(b2.getFirstType())) {
      result = Optional.of(new TypeEqualityConstraint(b1.getSecondType(), b2.getSecondType()));
    }
    // a = U and b = T (with b != a) implies <b[a:=U] = T[a:=U]>
    else {
      Map<SymTypeVariable, SymTypeExpression> replaceMap
          = Map.of(b1.getFirstType(), b1.getSecondType());
      SymTypeVariable typeVar =
          TypeParameterRelations.replaceTypeVariables(b2.getFirstType(), replaceMap)
              .asTypeVariable();
      SymTypeExpression secondType =
          TypeParameterRelations.replaceTypeVariables(b2.getSecondType(), replaceMap);
      if (
          !SymTypeRelations.normalize(typeVar)
              .deepEquals(SymTypeRelations.normalize(b2.getFirstType())) ||
              !SymTypeRelations.normalize(secondType)
                  .deepEquals(SymTypeRelations.normalize(b2.getSecondType()))
      ) {
        result = Optional.of(new TypeEqualityConstraint(typeVar, secondType));
      }
      else {
        result = Optional.empty();
      }
    }
    return result;
  }

  protected List<Constraint> incorporate(SubTypingBound b1, SubTypingBound b2) {
    List<Constraint> constraints = new ArrayList<>();
    constraints.addAll(incorporateNonSymmetrical(b1, b2));
    constraints.addAll(incorporateNonSymmetrical(b2, b1));
    return constraints;
  }

  protected List<Constraint> incorporateNonSymmetrical(
      SubTypingBound b1, SubTypingBound b2
  ) {
    List<Constraint> result;
    // S <: a and a <: T implies <S <: T>
    if (TypeParameterRelations.isInferenceVariable(b1.getSubType()) &&
        TypeParameterRelations.isInferenceVariable(b2.getSuperType()) &&
        b1.getSubType().asTypeVariable()
            .denotesSameVar(b2.getSuperType().asTypeVariable())
    ) {
      result = Collections.singletonList(
          new SubTypingConstraint(b2.getSubType(), b1.getSuperType())
      );
    }
    // a <: S and a <: T and
    // exists G with S <: G<S1,...,Sn> and T <: G<T1,...,Tn> implies
    // <Si = Ti> for i in {1,...,n}
    // NOTE: this may need to be extent further for this typeSystem,
    // e.g. for funcs, tuple?

    // object types only ever have object types as supertypes
    // function types only ever have function types as supertypes
    // tuple types only ever have tuple types as supertypes
    // in union and intersection types, never combine any of the above(?) (-overloading, which has already been handled)
    else if (TypeParameterRelations.isInferenceVariable(b1.getSubType()) &&
        b1.getSubType().asTypeVariable().denotesSameVar(b2.getSubType()) &&
        // if S or T are inference variables,
        // their supertypes have been/will be added to the bounds of a,
        // thus, they don't need to be checked here.
        // Same goes for non-inference variables
        // (which only ever have upper bounds),
        // their upper bounds should be added to a elsewhere (s. resolution)
        !hasTypeVariables(b1.getSuperType()) &&
        !hasTypeVariables(b2.getSuperType())
    ) {
      result = getConstraintsFromCommonSuperTypes(
          b1.getSubType().asTypeVariable(),
          b1.getSuperType(), b2.getSuperType(),
          b1, b2
      );
    }
    // S < a, T < a
    // not in Java Spec, this can be required for, e.g., functions
    else if (
        TypeParameterRelations.isInferenceVariable(b1.getSuperType()) &&
            b1.getSuperType().asTypeVariable().denotesSameVar(b2.getSuperType()) &&
            !hasTypeVariables(b1.getSubType()) &&
            !hasTypeVariables(b2.getSubType())
    ) {
      // deliberately empty,
      // as, e.g., A | B <: a, C <: a is equivalent to A | B | C <: a
      // which does not allow us to extract more constraints.
      // this can be extended
      // if we do not want to allow the inference of union types
      Log.trace("Bound combination " + b1.print() + ", "
              + b2.print() + " is deliberately ignored.",
          LOG_NAME
      );
      result = Collections.emptyList();
    }
    else {
      result = Collections.emptyList();
    }
    return result;
  }

  protected List<Constraint> incorporate(
      TypeCompatibilityBound b1, TypeCompatibilityBound b2
  ) {
    List<Constraint> constraints = new ArrayList<>();
    constraints.addAll(incorporateNonSymmetrical(b1, b2));
    constraints.addAll(incorporateNonSymmetrical(b2, b1));
    return constraints;
  }

  protected List<Constraint> incorporateNonSymmetrical(
      TypeCompatibilityBound b1, TypeCompatibilityBound b2
  ) {
    List<Constraint> result;
    // S --> a and a --> T implies <S --> T>
    // note that this implies that compatibility is transitive;
    // Forall types a,b,c. a-->b and b-->c implies a-->c.
    if (TypeParameterRelations.isInferenceVariable(b1.getSourceType()) &&
        b1.getSourceType().asTypeVariable().denotesSameVar(b2.getTargetType())
    ) {
      result = Collections.singletonList(
          new TypeCompatibilityConstraint(b2.getSourceType(), b1.getTargetType())
      );
    }
    // a --> S and a --> T
    // compare a <: S and a <: T!
    // As S could be int and T could be Float,
    // it is unclear what further constraints follow.
    // For now, we simply assume that there are none
    // that are not checked later after
    // A. either getting the same bounds as subtyping bounds,
    // B. or finding the instantiation of a.
    // Currently, we hope that this information is not required in some way,
    // which SHOULD hold true for any/most reasonable complex expressions?
    // Exception: we still use the heuristic/restrictive interpretation(!) of
    // common super types having the same type arguments.
    else if (TypeParameterRelations.isInferenceVariable(b1.getSourceType()) &&
        b1.getSourceType().asTypeVariable().denotesSameVar(b2.getSourceType()) &&
        !hasTypeVariables(b1.getTargetType()) &&
        !hasTypeVariables(b2.getTargetType())
    ) {
      result = getConstraintsFromCommonSuperTypes(
          b1.getSourceType().asTypeVariable(),
          b1.getTargetType(), b2.getTargetType(),
          b1, b2
      );
    }
    // S --> a, T --> a
    // compare S <: a, T <: a
    else if (
        TypeParameterRelations.isInferenceVariable(b1.getTargetType()) &&
            b1.getTargetType().asTypeVariable().denotesSameVar(b2.getTargetType()) &&
            !hasTypeVariables(b1.getSourceType()) &&
            !hasTypeVariables(b2.getSourceType())
    ) {
      // deliberately empty due to union types existing
      Log.trace("Bound combination " + b1.print() + ", "
              + b2.print() + " is deliberately ignored.",
          LOG_NAME
      );
      result = Collections.emptyList();
    }
    else {
      result = Collections.emptyList();
    }
    return result;
  }

  protected List<Constraint> incorporate(SubTypingBound bS, TypeCompatibilityBound bComp) {
    List<Constraint> result;
    // S <: a and a --> T implies <S --> T>
    if (TypeParameterRelations.isInferenceVariable(bS.getSuperType()) &&
        bS.getSuperType().asTypeVariable().denotesSameVar(bComp.getSourceType())
    ) {
      result = Collections.singletonList(
          new TypeCompatibilityConstraint(bS.getSubType(), bComp.getTargetType())
      );
    }
    // S --> a and a <: T implies <S --> T>
    else if (TypeParameterRelations.isInferenceVariable(bS.getSubType()) &&
        bS.getSubType().asTypeVariable().denotesSameVar(bComp.getTargetType())
    ) {
      result = Collections.singletonList(
          new TypeCompatibilityConstraint(bComp.getSourceType(), bS.getSuperType())
      );
    }
    // a <: S and a --> T, S is proper, T is proper
    // compare a --> S and a <: T
    else if (TypeParameterRelations.isInferenceVariable(bS.getSubType()) &&
        bS.getSubType().asTypeVariable().denotesSameVar(bComp.getSourceType()) &&
        !hasTypeVariables(bS.getSuperType()) &&
        !hasTypeVariables(bComp.getTargetType())
    ) {
      // FDr NOTE: can be extended with a similar check as a <: S and a <: T,
      // but need to check the implications:
      // it does not seem unreasonable, though?

      // S <: T implies a <: T, which is important if S is not a proper type
      // (this SHOULD currently be checked in BoundResolution as well,
      // but one expects this to happen in incorporation, thus here)
      if (SymTypeRelations.isSubTypeOf(bS.getSuperType(), bComp.getTargetType())) {
        result = Collections.singletonList(
            new SubTypingConstraint(bS.getSubType(), bComp.getSourceType())
        );
      }
      // T <: S implies a --> S, but that is a given since a <: S.
      // S --> T implies a --> T which is already known.
      // T --> S implies a --> S, but that is a given since a <: S.
      // One COULD add these information to increase the information exchange
      // between the types of Bounds, but it does not seem useful
      else {
        Log.trace("Bound combination " + bS.print() + ", "
                + bComp.print() + " is deliberately ignored.",
            LOG_NAME
        );
        result = Collections.emptyList();
      }
    }
    // a <: S and a --> T, S or T is not proper
    // compare a --> S and a <: T
    else if (TypeParameterRelations.isInferenceVariable(bS.getSubType()) &&
        bS.getSubType().asTypeVariable().denotesSameVar(bComp.getSourceType())) {
      result = getConstraintsFromCommonSuperTypes(
          bS.getSubType().asTypeVariable(),
          bS.getSuperType(), bComp.getTargetType(),
          bS, bComp
      );
    }
    // S <: a, T --> a
    // compare S <: a, T <: a
    else if (
        TypeParameterRelations.isInferenceVariable(bS.getSuperType()) &&
            bS.getSuperType().asTypeVariable().denotesSameVar(bComp.getTargetType()) &&
            !hasTypeVariables(bS.getSubType()) &&
            !hasTypeVariables(bComp.getSourceType())
    ) {
      // deliberately empty due to union types existing
      Log.trace("Bound combination " + bS.print() + ", "
              + bComp.print() + " is deliberately ignored.",
          LOG_NAME
      );
      result = Collections.emptyList();
    }
    else {
      result = Collections.emptyList();
    }
    return result;
  }

  protected List<Constraint> incorporate(TypeEqualityBound bE, SubTypingBound bS) {
    List<Constraint> constraints = new ArrayList<>();
    incorporateNonSymmetrical(bE, bS).ifPresent(constraints::add);
    Optional<TypeEqualityBound> flipped = bE.getFlipped();
    if (flipped.isPresent()) {
      incorporateNonSymmetrical(flipped.get(), bS).ifPresent(constraints::add);
    }
    return constraints;
  }

  protected Optional<Constraint> incorporateNonSymmetrical(
      TypeEqualityBound bE,
      SubTypingBound bS
  ) {
    Optional<Constraint> result;
    // a = S and a <: T implies <S <: T>
    if (bE.getFirstType().denotesSameVar(bS.getSubType())) {
      result = Optional.of(new SubTypingConstraint(bE.getSecondType(), bS.getSuperType()));
    }
    // a = S and T <: a implies <T <: S>
    else if (bE.getFirstType().denotesSameVar(bS.getSuperType())) {
      result = Optional.of(new SubTypingConstraint(bS.getSubType(), bE.getSecondType()));
    }
    // a = U and S <: T (with S != a and T != a) implies <S[a:=U] <: T[a:=U]>
    else if (!TypeParameterRelations.hasInferenceVariables(bE.getSecondType())) {
      Map<SymTypeVariable, SymTypeExpression> replaceMap
          = Map.of(bE.getFirstType(), bE.getSecondType());
      SymTypeExpression subType =
          TypeParameterRelations.replaceTypeVariables(bS.getSubType(), replaceMap);
      SymTypeExpression superType =
          TypeParameterRelations.replaceTypeVariables(bS.getSuperType(), replaceMap);
      if (
          !SymTypeRelations.normalize(subType)
              .deepEquals(SymTypeRelations.normalize(bS.getSubType())) ||
              !SymTypeRelations.normalize(subType)
                  .deepEquals(SymTypeRelations.normalize(bS.getSuperType()))
      ) {
        result = Optional.of(new SubTypingConstraint(subType, superType));
      }
      else {
        result = Optional.empty();
      }
    }
    else {
      result = Optional.empty();
    }
    return result;
  }

  protected List<Constraint> incorporate(TypeEqualityBound bE, TypeCompatibilityBound bComp) {
    List<Constraint> constraints = new ArrayList<>();
    incorporateNonSymmetrical(bE, bComp).ifPresent(constraints::add);
    Optional<TypeEqualityBound> flipped = bE.getFlipped();
    if (flipped.isPresent()) {
      incorporateNonSymmetrical(flipped.get(), bComp).ifPresent(constraints::add);
    }
    return constraints;
  }

  /**
   * basically identical to
   * {@link #incorporateNonSymmetrical(TypeEqualityBound, SubTypingBound)}
   */
  protected Optional<Constraint> incorporateNonSymmetrical(
      TypeEqualityBound bE,
      TypeCompatibilityBound bComp
  ) {
    Optional<Constraint> result;
    // a = S and a --> T implies <S --> T>
    if (bE.getFirstType().denotesSameVar(bComp.getSourceType())) {
      result = Optional.of(new TypeCompatibilityConstraint(bE.getSecondType(), bComp.getTargetType()));
    }
    // a = S and T --> a implies <T --> S>
    else if (bE.getFirstType().denotesSameVar(bComp.getTargetType())) {
      result = Optional.of(new TypeCompatibilityConstraint(bComp.getSourceType(), bE.getSecondType()));
    }
    // a = U and S --> T (with S != a and T != a) implies <S[a:=U] --> T[a:=U]>
    else if (!TypeParameterRelations.hasInferenceVariables(bE.getSecondType())) {
      Map<SymTypeVariable, SymTypeExpression> replaceMap
          = Map.of(bE.getFirstType(), bE.getSecondType());
      SymTypeExpression sourceType =
          TypeParameterRelations.replaceTypeVariables(bComp.getSourceType(), replaceMap);
      SymTypeExpression targetType =
          TypeParameterRelations.replaceTypeVariables(bComp.getTargetType(), replaceMap);
      if (
          !SymTypeRelations.normalize(sourceType)
              .deepEquals(SymTypeRelations.normalize(bComp.getSourceType())) ||
              !SymTypeRelations.normalize(sourceType)
                  .deepEquals(SymTypeRelations.normalize(bComp.getTargetType()))
      ) {
        result = Optional.of(new TypeCompatibilityConstraint(sourceType, targetType));
      }
      else {
        result = Optional.empty();
      }
    }
    else {
      result = Optional.empty();
    }
    return result;
  }

  protected List<Constraint> incorporate(CaptureBound bCap, SubTypingBound bS) {
    // s. Java Spec 21 18.3.2
    Optional<Constraint> constraint = Optional.empty();
    List<SymTypeVariable> infVars = bCap.getInferenceVariables();

    int argIdx = -1;
    for (int i = 0; i < infVars.size(); i++) {
      if (infVars.get(i).denotesSameVar(bS.getSubType()) ||
          infVars.get(i).denotesSameVar(bS.getSuperType())
      ) {
        argIdx = i;
      }
    }

    if (argIdx != -1 && bCap.getTypeArguments().get(argIdx).isWildcard()) {
      SymTypeOfWildcard wc = bCap.getTypeArguments().get(argIdx).asWildcard();
      // G<...,ai,...>  = capture(G<...,Ai,...>) and R <: ai
      // (with R is not an inference variable and Ai is a wildcard)
      if (!TypeParameterRelations.isInferenceVariable(bS.getSubType())) {
        SymTypeExpression subType = bS.getSubType();
        if (!wc.hasBound() || wc.isUpper()) {
          constraint = Optional.of(
              new BoundWrapperConstraint(new UnsatisfiableBound(
                  subType.printFullName() + " cannot be included in "
                      + wc.printFullName()
              ))
          );
        }
        else {
          constraint = Optional.of(
              new SubTypingConstraint(subType, wc.getBound())
          );
        }
      }
      // G<...,ai,...>  = capture(G<...,Ai,...>) and ai <: R
      // (with R is not an inference variable and Ai is a wildcard)
      if (!TypeParameterRelations.isInferenceVariable(bS.getSuperType())) {
        SymTypeExpression superType = bS.getSuperType();
        SymTypeExpression modifiedBound =
            bCap.getModifiedDeclaredBounds().get(argIdx);
        if (!wc.hasBound() || !wc.isUpper()) {
          constraint = Optional.of(
              new SubTypingConstraint(modifiedBound, superType)
          );
        }
        else {
          if (SymTypeRelations.isTop(wc.getBound())) {
            constraint = Optional.of(
                new SubTypingConstraint(modifiedBound, superType)
            );
          }
          else if (SymTypeRelations.isTop(modifiedBound)) {
            constraint = Optional.of(
                new SubTypingConstraint(wc.getBound(), superType)
            );
          }
          else {
            // deliberate no-op (s. Java Spec 21 18.3.2)
            // albeit, this seems to ignore the constraints...
          }
        }
      }
    }

    return constraint.stream().collect(Collectors.toList());
  }

  /**
   * basically a copy of
   * {@link #incorporate(CaptureBound, SubTypingBound)}
   */
  protected List<Constraint> incorporate(CaptureBound bCap, TypeCompatibilityBound bComp) {
    Optional<Constraint> constraint = Optional.empty();
    List<SymTypeVariable> infVars = bCap.getInferenceVariables();

    int argIdx = -1;
    for (int i = 0; i < infVars.size(); i++) {
      if (infVars.get(i).denotesSameVar(bComp.getSourceType()) ||
          infVars.get(i).denotesSameVar(bComp.getTargetType())
      ) {
        argIdx = i;
      }
    }

    if (argIdx != -1 && bCap.getTypeArguments().get(argIdx).isWildcard()) {
      SymTypeOfWildcard wc = bCap.getTypeArguments().get(argIdx).asWildcard();
      // G<...,ai,...>  = capture(G<...,Ai,...>) and R  ai
      // (with R is not an inference variable and Ai is a wildcard)
      if (!TypeParameterRelations.isInferenceVariable(bComp.getSourceType())) {
        SymTypeExpression sourceType = bComp.getSourceType();
        if (!wc.hasBound() || wc.isUpper()) {
          constraint = Optional.of(
              new BoundWrapperConstraint(new UnsatisfiableBound(
                  sourceType.printFullName() + " cannot be included in "
                      + wc.printFullName()
              ))
          );
        }
        else {
          constraint = Optional.of(
              new TypeCompatibilityConstraint(sourceType, wc.getBound())
          );
        }
      }
      // G<...,ai,...>  = capture(G<...,Ai,...>) and ai --> R
      // (with R is not an inference variable and Ai is a wildcard)
      if (!TypeParameterRelations.isInferenceVariable(bComp.getTargetType())) {
        SymTypeExpression targetType = bComp.getTargetType();
        SymTypeExpression modifiedBound =
            bCap.getModifiedDeclaredBounds().get(argIdx);
        if (!wc.hasBound() || !wc.isUpper()) {
          constraint = Optional.of(
              new TypeCompatibilityConstraint(modifiedBound, targetType)
          );
        }
        else {
          if (SymTypeRelations.isTop(wc.getBound())) {
            constraint = Optional.of(
                new TypeCompatibilityConstraint(modifiedBound, targetType)
            );
          }
          else if (SymTypeRelations.isTop(modifiedBound)) {
            constraint = Optional.of(
                new TypeCompatibilityConstraint(wc.getBound(), targetType)
            );
          }
          else {
            // deliberate no-op (s. subtyping version)
          }
        }
      }
    }

    return constraint.stream().collect(Collectors.toList());
  }

  protected List<Constraint> incorporate(CaptureBound bCap, TypeEqualityBound bE) {
    List<Constraint> constraints = new ArrayList<>();
    incorporateNonSymmetrical(bCap, bE).ifPresent(constraints::add);
    if (bE.getFlipped().isPresent()) {
      incorporateNonSymmetrical(bCap, bE.getFlipped().get()).ifPresent(constraints::add);
    }
    return constraints;
  }

  protected Optional<Constraint> incorporateNonSymmetrical(CaptureBound bCap, TypeEqualityBound bE) {
    // s. Java Spec 21 18.3.2
    Optional<Constraint> constraint = Optional.empty();

    if (!TypeParameterRelations.isInferenceVariable(bE.getSecondType())) {
      SymTypeExpression typeArg = null;
      List<SymTypeVariable> infVars = bCap.getInferenceVariables();
      for (int i = 0; i < infVars.size(); i++) {
        if (infVars.get(i).denotesSameVar(bE.getFirstType())
        ) {
          typeArg = bCap.getTypeArguments().get(i);
        }
      }

      if (typeArg != null && typeArg.isWildcard()) {
        constraint = Optional.of(new BoundWrapperConstraint(
            new UnsatisfiableBound(bE.getSecondType().printFullName()
                + " cannot be contained in " + typeArg.printFullName()
            )
        ));
      }
    }

    return constraint;
  }

  // Helper: get constraints from common super types

  /**
   * Helper for
   * {@link #incorporateNonSymmetrical(SubTypingBound, SubTypingBound)},
   * {@link #incorporateNonSymmetrical(TypeCompatibilityBound, TypeCompatibilityBound)},
   * {@link #incorporate(SubTypingBound, TypeCompatibilityBound)}.
   * Given a <: A and a <: B, due to them being supertypes of the same type,
   * constraints follow,
   * e.g., List<a> and List<B> lead to the constraint <a = B>.
   * If different rules apply (e.g., in OCL),
   * then this method could be overwritten.
   * However, as of now it is simply assumed that
   * 1. One does not derive from build in collection types (this should hold!)
   * 2. Java-Collection types simply fulfill the constraint above,
   * which may or may not get unintuitive in combination with boxing.
   * <p>
   * For TypeCompatibilityBounds, we assume the same rules,
   * e.g. a --> A<b> and a <: A<int> results in <b = int>.
   * This may need to be overridden for type systems with more flexible
   * compatibility rules, but we currently do not assume there to be a need
   * for such a system, this seems to be a sane restriction to make.
   */
  protected List<Constraint> getConstraintsFromCommonSuperTypes(
      SymTypeVariable infVar,
      SymTypeExpression superType1, SymTypeExpression superType2,
      Bound b1, Bound b2
  ) {
    // a <: A & B, a <: C is equivalent to a <: A & B & C
    // normalizing A & B & C filters out occurrences
    // which do not have a solution, e.g., (() -> int) & Person
    SymTypeExpression intersectedSuperTypes = SymTypeRelations.normalize(
        SymTypeExpressionFactory.createIntersection(
            superType1, superType2
        )
    );

    // todo does this remove the info that nothing is possible?
    // https://git.rwth-aachen.de/monticore/monticore/-/issues/4179
    // -> size==0 as check correct?
    // currently, for cases without two SubTypingBounds,
    // we may not have an intersection, e.g.,
    // a <: int and a --> Integer
    // simply ignore them for now (this could be optimized further)
    if (SymTypeRelations.isBottom(intersectedSuperTypes)) {
      Log.trace("potentially ignoring constraints with regard to"
              + " bounds" + b1.print() + ", " + b2.print(),
          LOG_NAME
      );
      return Collections.emptyList();
    }

    // unpack union
    List<SymTypeExpression> unionizedTypes;
    if (intersectedSuperTypes.isUnionType()) {
      unionizedTypes = new ArrayList<>(
          intersectedSuperTypes.asUnionType().getUnionizedTypeSet()
      );
      Log.debug("Encountered union type in bounds: "
              + b1.print() + ", " + b2.print()
              + ", which may lead to false negatives",
          LOG_NAME
      );
    }
    else {
      unionizedTypes = Collections.singletonList(intersectedSuperTypes);
    }

    // make all unionized types that are not intersections intersections.
    // A -> (A), this is for simplicity
    List<SymTypeOfIntersection> unionizedIntersections = new ArrayList<>();
    for (SymTypeExpression unionizedType : unionizedTypes) {
      if (unionizedType.isIntersectionType()) {
        unionizedIntersections.add(unionizedType.asIntersectionType());
      }
      else {
        unionizedIntersections.add(createIntersection(unionizedType));
      }
    }

    // handle each intersection individually
    // these collections of constraints are 'or'ed together
    List<List<Constraint>> oredConstraints =
        new ArrayList<>(unionizedIntersections.size());
    for (SymTypeOfIntersection intersection : unionizedIntersections) {
      List<Constraint> constraints;
      // due to the normalization before,
      // the following types can all be supertypes of a single type
      List<SymTypeExpression> superTypes =
          new ArrayList<>(intersection.getIntersectedTypeSet());
      // nothing to do (e.g. a <: A, a <: B with B <: A)
      if (superTypes.size() <= 1) {
        constraints = Collections.emptyList();
      }
      else {
        constraints = getConstraintsFromFilteredCommonSuperTypes(
            infVar, superTypes
        );
      }
      oredConstraints.add(constraints);
    }

    // at least remove clearly wrong constraints:
    List<List<Constraint>> oredConstraintsFiltered = new ArrayList<>();
    for (List<Constraint> constraints : oredConstraints) {
      // very simplified way to filter some of the incorrect options:
      List<Bound> bounds = ConstraintReduction.reduce(constraints);
      if (bounds.stream().noneMatch(Bound::isUnsatisfiableBound)) {
        oredConstraintsFiltered.add(constraints);
      }
    }
    List<Constraint> constraints;
    if (oredConstraintsFiltered.size() == 1) {
      constraints = oredConstraintsFiltered.get(0);
    }
    else {
      if (oredConstraintsFiltered.isEmpty()) {
        constraints = oredConstraints.get(0);
      }
      else {
        constraints = oredConstraintsFiltered.get(0);
      }
      Log.debug("given Bounds " + b1.print() + ", " + b2.print()
              + " arbitrarily choosing Constraints" + System.lineSeparator()
              + printConstraints(constraints),
          LOG_NAME
      );
    }
    return constraints;
  }

  /**
   * Helper for
   * {@link #getConstraintsFromCommonSuperTypes},
   * may ONLY EVER be called from this method,
   * as it relies on a lot of assumptions.
   * It is only a separate method as it needs to be called recursively.
   *
   * @param commonSuperTypes may not contain any type variables
   */
  protected List<Constraint> getConstraintsFromFilteredCommonSuperTypes(
      SymTypeVariable typeVar,
      List<? extends SymTypeExpression> commonSuperTypes
  ) {
    List<Constraint> constraints;
    if (commonSuperTypes.isEmpty()) {
      Log.error("0xFD441 internal error: "
          + "did not expect empty collection at this point.");
      return Collections.emptyList();
    }
    // all are tuples (and necessarily have the same length)
    if (commonSuperTypes.stream().allMatch(SymTypeExpression::isTupleType)) {
      List<SymTypeOfTuple> tuples = commonSuperTypes.stream()
          .map(SymTypeExpression::asTupleType)
          .collect(Collectors.toList());
      int length = tuples.get(0).sizeTypes();
      if (!tuples.stream().allMatch(t -> t.sizeTypes() == length)) {
        Log.error("0xFD442 internal error: unexpected (impossible)"
            + " collection of common super types: " + System.lineSeparator()
            + commonSuperTypes.stream()
            .map(SymTypeExpression::printFullName)
            .collect(Collectors.joining(System.lineSeparator()))
        );
        return Collections.emptyList();
      }
      // create fresh type variables:
      // a <: (A, B), a <: (C, D) --> <a = (b, c)>, <b <: A>, ...
      // note: this can explode in complexity (tuple of functions of tuples)
      // I do not expect this to be relevant in pretty much ANY expected model,
      // however, if it turns out to be relevant,
      // this is a part that can be optimized,
      // as a lot (but not necessarily all) of information
      // (of the fresh type variables) are here in one place already
      SymTypeOfTuple freshVarTuple = SymTypeExpressionFactory.createTuple();
      constraints = new ArrayList<>();
      for (int i = 0; i < length; i++) {
        SymTypeVariable freshVar = createFreshVariable();
        freshVarTuple.addType(freshVar);
        for (SymTypeOfTuple tuple : tuples) {
          constraints.add(new SubTypingConstraint(freshVar, tuple.getType(i)));
        }
      }
      constraints.add(new TypeEqualityConstraint(typeVar, freshVarTuple));
    }
    // all are functions (and necessarily have the same number of parameters)
    else if (commonSuperTypes.stream().allMatch(SymTypeExpression::isFunctionType)) {
      // this can be optimized if required (s. tuples comment)
      List<SymTypeOfFunction> functions = commonSuperTypes.stream()
          .map(SymTypeExpression::asFunctionType)
          .collect(Collectors.toList());
      int parLength = functions.get(0).sizeArgumentTypes();
      boolean isElliptic = functions.get(0).isElliptic();
      if (!functions.stream().allMatch(f ->
          f.sizeArgumentTypes() != parLength
              || f.isElliptic() != isElliptic
      )) {
        Log.error("0xFD443 internal error: unexpected (impossible)"
            + " collection of common super types: " + System.lineSeparator()
            + commonSuperTypes.stream()
            .map(SymTypeExpression::printFullName)
            .collect(Collectors.joining(System.lineSeparator()))
        );
        return Collections.emptyList();
      }
      constraints = new ArrayList<>();
      SymTypeVariable freshReturnVar = createFreshVariable();
      // return types
      for (SymTypeOfFunction func : functions) {
        constraints.add(new SubTypingConstraint(freshReturnVar, func.getType()));
      }
      // parameter types
      List<SymTypeVariable> freshParVars = new ArrayList<>(parLength);
      for (int i = 0; i < parLength; i++) {
        SymTypeVariable freshVar = createFreshVariable();
        freshParVars.add(freshVar);
        for (SymTypeOfFunction func : functions) {
          constraints.add(new SubTypingConstraint(func.getArgumentType(i), freshVar));
        }
      }
      SymTypeOfFunction freshVarFunc = SymTypeExpressionFactory.createFunction(
          createFreshVariable(), freshParVars, isElliptic
      );
      constraints.add(new TypeEqualityConstraint(typeVar, freshVarFunc));
    }
    // now, there ought to be neither tuples nor functions
    else if (commonSuperTypes.stream().anyMatch(
        t -> t.isTupleType() || t.isFunctionType()
    )) {
      // this should never happen, probably incorrect normalization
      Log.error("0xFD440 internal error: unexpected (impossible)" +
          " collection of common super types: " + System.lineSeparator()
          + commonSuperTypes.stream()
          .map(SymTypeExpression::printFullName)
          .collect(Collectors.joining(System.lineSeparator()))
      );
      return Collections.emptyList();
    }
    // there are not many combinations left.
    // this should, in most cases, be nominal types (e.g., objects, generics)
    // in any case, these are types "without (relevant) structure" like tuples
    else {
      constraints = getCommonSuperTypeConstraints(commonSuperTypes);
    }
    return constraints;
  }

  /**
   * Gets the constraints that exists due to common nominal super types,
   * e.g., {@code a <: A<B>, a <: A<C> --> <B = C>}.
   * The (nominal) supertype relationship,
   * given the common super types as start nodes,
   * spans a directed acyclic graph.
   * For each pair of common super types;
   * For each path starting at one of the common super types,
   * there has to be at least on node common with a path starting at
   * the other common super type.
   * Currently, we use a simple brute-force solution.
   * <p>
   * note: this allows combinations without common super types,
   * e.g., A and B (each without any supertypes).
   * This is due to there being the type (A & B),
   * which may exist in a modeling language,
   * e.g., A and B being interfaces in Java-esque languages.
   * One could add rules like A & B cannot exists
   * if they are classes (and not interfaces)
   * here (by overriding this method);
   * However, alternatively (and probably the simpler choice),
   * one can create a CoCo checking all intersections of calculated
   * ASTMCTypes/ASTExpressions after type inference
   * and check if they can exist according to the languages rules.
   * S.a. SymTypeNormalizeVisitor::intersectObjectTypes
   */
  protected List<Constraint> getCommonSuperTypeConstraints(
      List<? extends SymTypeExpression> startTypes
  ) {
    List<Constraint> constraints = new ArrayList<>();
    // get all paths
    Map<SymTypeExpression, List<List<SymTypeExpression>>> startType2Paths =
        new TreeMap<>(new SymTypeExpressionComparator());
    for (SymTypeExpression startType : startTypes) {
      startType2Paths.put(startType, getNominalSuperTypePaths(startType));
    }
    for (int i = 0; i < startTypes.size(); i++) {
      for (int j = i + 1; j < startTypes.size(); j++) {
        SymTypeExpression startTypeA = startTypes.get(i);
        SymTypeExpression startTypeB = startTypes.get(j);
        List<List<SymTypeExpression>> pathsA = startType2Paths.get(startTypeA);
        List<List<SymTypeExpression>> pathsB = startType2Paths.get(startTypeB);
        for (List<SymTypeExpression> pathA : pathsA) {
          for (List<SymTypeExpression> pathB : pathsB) {
            // As soon as we find a common super type,
            // all super types of that type will be the same
            // (due to Java's acyclic inheritance rules),
            // thus, we can stop searching after finding one.
            // To be more precise, we are not checking for multiple occurrences
            // of the same generic with different arguments.
            boolean foundCommonSuperType = false;
            int k = 0;
            while (!foundCommonSuperType && k < pathA.size()) {
              SymTypeExpression typeA = pathA.get(k);
              int l = 0;
              while (!foundCommonSuperType && l < pathB.size()) {
                SymTypeExpression typeB = pathB.get(l);
                if (typeA == typeB || SymTypeRelations.normalize(typeA)
                    .deepEquals(SymTypeRelations.normalize(typeB))
                ) {
                  foundCommonSuperType = true;
                }
                // generics' arguments need to be the same
                // needs to be extended for OCL collection types
                if (typeA.isGenericType() && typeA.asGenericType()
                    .deepEqualsWithoutArguments(typeB)
                ) {
                  SymTypeOfGenerics genA = typeA.asGenericType();
                  SymTypeOfGenerics genB = typeB.asGenericType();
                  if (genA.sizeArguments() != genB.sizeArguments()) {
                    // should have been found using CoCo,
                    // iff it's from the model, thus internal error
                    Log.error("0xFD339 internal error: "
                        + "encountered same generic with different amount"
                        + "of type parameters: " + genA.printFullName()
                        + " and " + genB.printFullName()
                    );
                    return Collections.emptyList();
                  }
                  for (int m = 0; m < genA.sizeArguments(); m++) {
                    constraints.add(new TypeEqualityConstraint(
                        genA.getArgument(m), genB.getArgument(m)
                    ));
                  }
                  foundCommonSuperType = true;
                }
                l++;
              }
              k++;
            }
          }
        }
      }
    }
    return constraints;
  }

  /**
   * Helper for {@link #getCommonSuperTypeConstraints(List)}.
   * Does NOT deepclone SymTypeExpressions.
   */
  protected List<List<SymTypeExpression>> getNominalSuperTypePaths(
      SymTypeExpression startType
  ) {
    List<List<SymTypeExpression>> paths = new ArrayList<>();
    List<SymTypeExpression> superTypes =
        SymTypeRelations.getNominalSuperTypes(startType);
    if (superTypes.isEmpty()) {
      paths.add(Collections.singletonList(startType));
    }
    else {
      for (SymTypeExpression superType : superTypes) {
        List<List<SymTypeExpression>> superTypePaths =
            getNominalSuperTypePaths(superType);
        for (List<SymTypeExpression> superTypePath : superTypePaths) {
          List<SymTypeExpression> path = new ArrayList<>();
          path.add(startType);
          path.addAll(superTypePath);
          paths.add(path);
        }
      }
    }
    return paths;
  }

  // Helper

  protected boolean hasTypeVariables(SymTypeExpression symType) {
    return !new SymTypeCollectionVisitor()
        .calculate(symType, SymTypeExpression::isTypeVariable)
        .isEmpty();
  }

  protected SymTypeVariable createFreshVariable() {
    return SymTypeExpressionFactory.createTypeVariable(
        SymTypeExpressionFactory.createBottomType(),
        SymTypeExpressionFactory.createTopType()
    );
  }

  protected String printConstraints(List<Constraint> constraints) {
    return constraints.stream()
        .map(Constraint::print)
        .collect(Collectors.joining(System.lineSeparator()));
  }

  protected String printBounds(List<Bound> bounds) {
    return bounds.stream()
        .map(Bound::print)
        .collect(Collectors.joining(System.lineSeparator()));
  }
}
