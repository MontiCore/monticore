// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.generics.util;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeInferenceVariable;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.generics.TypeParameterRelations;
import de.monticore.types3.generics.bounds.Bound;
import de.monticore.types3.generics.bounds.CaptureBound;
import de.monticore.types3.generics.bounds.SubTypingBound;
import de.monticore.types3.generics.bounds.TypeCompatibilityBound;
import de.monticore.types3.generics.bounds.TypeEqualityBound;
import de.monticore.types3.generics.constraints.Constraint;
import de.monticore.types3.util.SymTypeExpressionComparator;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static de.monticore.types.check.SymTypeExpressionFactory.createInferenceVariable;
import static de.monticore.types3.SymTypeRelations.isCompatible;
import static de.monticore.types3.SymTypeRelations.isSubTypeOf;

public class BoundResolution {

  /**
   * the name to be used for Log.info, etc.
   */
  protected static final String LOG_NAME = "BoundResolution";

  protected static BoundResolution delegate;

  public static void init() {
    Log.trace("init default BoundResolution", "TypeCheck setup");
    BoundResolution.delegate = new BoundResolution();
  }

  protected static BoundResolution getDelegate() {
    if (delegate == null) {
      init();
    }
    return delegate;
  }

  /**
   * Aims to find instantiations for inference variables
   * given a set of bounds.
   * This will also take care of bound incorporation and constraint reduction.
   *
   * @return empty iff there is no instantiation found such that the bounds
   *     can be satisfied
   */
  public static Optional<Map<SymTypeInferenceVariable, SymTypeExpression>> resolve(
      List<Bound> bounds
  ) {
    return resolve(bounds, Collections.emptyList());
  }

  /**
   * s. {@link #resolve(List)}
   *
   * @param toBeResolved only resolves the listed variables, or all if empty
   */
  public static Optional<Map<SymTypeInferenceVariable, SymTypeExpression>> resolve(
      List<Bound> bounds,
      List<SymTypeInferenceVariable> toBeResolved
  ) {
    return resolve(bounds, Collections.emptyList(), toBeResolved);
  }

  protected static Optional<Map<SymTypeInferenceVariable, SymTypeExpression>> resolve(
      List<Bound> newBounds,
      List<Bound> oldBounds,
      List<SymTypeInferenceVariable> toBeResolved
  ) {
    return getDelegate().calculateResolve(
        newBounds, oldBounds, toBeResolved, Collections.emptySet()
    );
  }

  /**
   * @param lastSetOfUninstantiated used to stop infinite recursion
   */
  protected Optional<Map<SymTypeInferenceVariable, SymTypeExpression>> calculateResolve(
      List<Bound> newBounds,
      List<Bound> oldBounds,
      List<SymTypeInferenceVariable> toBeResolved,
      Collection<SymTypeInferenceVariable> lastSetOfUninstantiated
  ) {
    // shortcut reducing log
    if (newBounds.isEmpty() && oldBounds.isEmpty()) {
      return Optional.of(new HashMap<>());
    }
    Optional<Map<SymTypeInferenceVariable, SymTypeExpression>> result = Optional.empty();

    // get all relevant bounds: reduced bounds
    List<Bound> reducedBounds = incorporateAndReduce(newBounds, oldBounds);
    if (reducedBounds.stream().anyMatch(Bound::isUnsatisfiableBound)) {
      return Optional.empty();
    }

    // sort the bounds according to the variable
    Map<SymTypeInferenceVariable, List<Bound>> var2BoundsIncomplete =
        getVarBoundDependencies(reducedBounds);
    Map<SymTypeInferenceVariable, List<Bound>> var2Bounds =
        completeVarBoundDependencies(var2BoundsIncomplete);

    // bounds listed in order of priority which is used to find new instantiations
    // a = T, T <: a, a <: T, T --> a, a --> T
    Map<SymTypeInferenceVariable, List<SymTypeExpression>> var2Equal = createSymTypeExprMap();
    Map<SymTypeInferenceVariable, List<SymTypeExpression>> var2LowerBounds = createSymTypeExprMap();
    Map<SymTypeInferenceVariable, List<SymTypeExpression>> var2UpperBounds = createSymTypeExprMap();
    Map<SymTypeInferenceVariable, List<SymTypeExpression>> var2SourceBounds = createSymTypeExprMap();
    Map<SymTypeInferenceVariable, List<SymTypeExpression>> var2TargetBounds = createSymTypeExprMap();
    Map<SymTypeInferenceVariable, CaptureBound> var2CaptureBound = createSymTypeExprMap();
    for (Map.Entry<SymTypeInferenceVariable, List<Bound>> varBounds : var2Bounds.entrySet()) {
      SymTypeInferenceVariable var = varBounds.getKey();
      var2Equal.put(var, new ArrayList<>());
      var2LowerBounds.put(var, new ArrayList<>());
      var2UpperBounds.put(var, new ArrayList<>());
      var2SourceBounds.put(var, new ArrayList<>());
      var2TargetBounds.put(var, new ArrayList<>());
    }
    for (Map.Entry<SymTypeInferenceVariable, List<Bound>> varBounds : var2Bounds.entrySet()) {
      for (Bound bound : varBounds.getValue()) {
        if (bound.isSubTypingBound()) {
          SubTypingBound subTypingBound = (SubTypingBound) bound;
          if (subTypingBound.getSubType().isInferenceVariable()) {
            var2UpperBounds.get(subTypingBound.getSubType().asInferenceVariable())
                .add(subTypingBound.getSuperType());
          }
          if (subTypingBound.getSuperType().isInferenceVariable()) {
            var2LowerBounds.get(subTypingBound.getSuperType().asInferenceVariable())
                .add(subTypingBound.getSubType());
          }
        }
        if (bound.isTypeCompatibilityBound()) {
          TypeCompatibilityBound typeCompatibilityBound = (TypeCompatibilityBound) bound;
          if (typeCompatibilityBound.getSourceType().isInferenceVariable()) {
            var2TargetBounds.get(typeCompatibilityBound.getSourceType().asInferenceVariable())
                .add(typeCompatibilityBound.getTargetType());
          }
          if (typeCompatibilityBound.getTargetType().isInferenceVariable()) {
            var2SourceBounds.get(typeCompatibilityBound.getTargetType().asInferenceVariable())
                .add(typeCompatibilityBound.getSourceType());
          }
        }
        else if (bound.isTypeEqualityBound()) {
          TypeEqualityBound typeEqualityBound = (TypeEqualityBound) bound;
          var2Equal.get(typeEqualityBound.getFirstType())
              .add(typeEqualityBound.getSecondType());
          Optional<TypeEqualityBound> flipped = typeEqualityBound.getFlipped();
          if (flipped.isPresent()) {
            var2Equal.get(flipped.get().getFirstType())
                .add(flipped.get().getSecondType());
          }
        }
        else if (bound.isCaptureBound()) {
          if (var2CaptureBound.containsKey(varBounds.getKey())) {
            Log.error("0xFD211 internal error: multiple capture bound "
                + "left hand sides for same variable encountered..."
            );
          }
          var2CaptureBound.put(varBounds.getKey(), (CaptureBound) bound);
        }
      }
    }

    // get dependencies between variables
    // java spec 21 18.4 -> dependencies are only relevant for capture bounds
    Map<SymTypeInferenceVariable, List<SymTypeInferenceVariable>> varInterDependencies =
        createSymTypeExprMap();
    for (SymTypeInferenceVariable var : var2Bounds.keySet()) {
      varInterDependencies.put(var, new ArrayList<>());
    }
    for (SymTypeInferenceVariable var : var2Bounds.keySet()) {
      // get all the Expressions together and find all type variables
      List<SymTypeExpression> allTypes = new ArrayList<>();
      allTypes.addAll(var2Equal.get(var));
      allTypes.addAll(var2LowerBounds.get(var));
      allTypes.addAll(var2UpperBounds.get(var));
      allTypes.addAll(var2SourceBounds.get(var));
      allTypes.addAll(var2TargetBounds.get(var));
      List<SymTypeInferenceVariable> relatedInfVars =
          TypeParameterRelations.getIncludedInferenceVariables(allTypes);
      // if the inference variable is on the left side of a capture bound,
      // it needs to be resolved before the related variables.
      if (!var2CaptureBound.containsKey(var)) {
        varInterDependencies.get(var).addAll(relatedInfVars);
      }
      else {
        CaptureBound captureBound = var2CaptureBound.get(var);
        for (SymTypeInferenceVariable relatedInfVar : relatedInfVars) {
          varInterDependencies.get(relatedInfVar).add(var);
        }
        varInterDependencies.get(var).addAll(captureBound.getInferenceVariables());
        varInterDependencies.get(var).addAll(TypeParameterRelations
            .getIncludedInferenceVariables(captureBound.getToBeCaptured())
        );
      }
    }
    // remove skolem variables
    for (SymTypeInferenceVariable var : varInterDependencies.keySet()) {
      List<SymTypeInferenceVariable> dependencies = varInterDependencies.get(var);
      varInterDependencies.put(var, dependencies);
    }

    // get current instantiations
    List<SymTypeInferenceVariable> varsWithoutInstantiation = new ArrayList<>();
    Map<SymTypeInferenceVariable, SymTypeExpression> varInstantiations = createSymTypeExprMap();
    for (SymTypeInferenceVariable var : varInterDependencies.keySet()) {
      Optional<SymTypeExpression> instantiation = Optional.empty();
      for (SymTypeExpression potInstantiation : var2Equal.get(var)) {
        List<SymTypeInferenceVariable> includedInferenceVariables =
            TypeParameterRelations.getIncludedInferenceVariables(potInstantiation);
        // only skolem variables?
        if (includedInferenceVariables.isEmpty()) {
          instantiation = Optional.of(potInstantiation);
        }
      }
      if (instantiation.isPresent()) {
        varInstantiations.put(var, instantiation.get());
      }
      else {
        varsWithoutInstantiation.add(var);
      }
    }

    // stop infinite recursion after an attempt of JLS 21 18.4, lower part.
    // s.a. org.eclipse.jdt.internal.compiler.lookup.InferenceContext18
    // ::resolve
    if (!lastSetOfUninstantiated.isEmpty()) {
      if (varsWithoutInstantiation.containsAll(lastSetOfUninstantiated)) {
        // no progress, give up.
        return Optional.empty();
      }
    }

    // find variables to resolve next
    // based on JLS 21 18.4
    // s.a. org.eclipse.jdt.internal.compiler.lookupInferenceContext18
    // ::getSmallestVariableSet
    List<SymTypeInferenceVariable> varsToResolveNext;
    List<SymTypeInferenceVariable> varsToResolveNextNotFinal;
    // do not consider vars that should not be instantiated (yet)
    if (toBeResolved.isEmpty()) {
      varsToResolveNextNotFinal = varsWithoutInstantiation;
    }
    else {
      varsToResolveNextNotFinal = varsWithoutInstantiation.stream()
          .filter(toBeResolved::contains)
          .collect(Collectors.toList());
    }
    for (SymTypeInferenceVariable var : varsWithoutInstantiation) {
      Set<SymTypeInferenceVariable> deps =
          new TreeSet<>(new SymTypeExpressionComparator());
      deps.add(var);// should not be necessary, just in case
      deps.addAll(varInterDependencies.get(var));
      if (deps.size() < varsToResolveNextNotFinal.size()) {
        varsToResolveNextNotFinal = new ArrayList<>(deps);
      }
    }
    if (varsToResolveNextNotFinal.isEmpty() &&
        !varsWithoutInstantiation.isEmpty()) {
      Log.error("0xFD318 internal error: "
          + "found no set of vars to resolve next?"
          + System.lineSeparator() + printBounds(reducedBounds)
      );
      return Optional.empty();
    }
    varsToResolveNext = varsToResolveNextNotFinal;

    // find a new instantiation
    if (!varsToResolveNext.isEmpty()) {
      // Simple method based on LuBs/GlBs
      if (varsToResolveNext.stream().noneMatch(var2CaptureBound::containsKey)) {
        List<TypeEqualityBound> newEqualityBounds = findInstantiationsSimple(
            varsToResolveNext, var2LowerBounds, var2UpperBounds, var2SourceBounds, var2TargetBounds
        );
        // use the new-found instantiations to reiterate
        result = calculateResolve(
            new ArrayList<>(newEqualityBounds), reducedBounds, toBeResolved,
            varsWithoutInstantiation
        );
      }
      // Complex method involving the creation of new inference variables
      // (s. JLS 21 18.4, lower part)
      if (result.isEmpty()) {
        List<SymTypeInferenceVariable> newInfVars = new ArrayList<>();
        List<Bound> newInfVarsBounds = new ArrayList<>();
        Map<SymTypeInferenceVariable, SymTypeInferenceVariable> origVar2NewInfVar =
            createSymTypeExprMap();
        for (SymTypeInferenceVariable var : varsToResolveNext) {
          // real bounds are set below explicitly
          SymTypeInferenceVariable newInfVar = createInferenceVariable();
          newInfVars.add(newInfVar);
          origVar2NewInfVar.put(var, newInfVar);
        }
        for (int i = 0; i < newInfVars.size(); i++) {
          SymTypeInferenceVariable newInfVar = newInfVars.get(i);
          SymTypeInferenceVariable origVar = varsToResolveNext.get(i);
          Optional<SymTypeExpression> lowerBound =
              getLubOfProperLowerBounds(var2LowerBounds.get(origVar));
          if (lowerBound.isPresent()) {
            newInfVarsBounds.add(new SubTypingBound(lowerBound.get(), newInfVar));
          }
          List<SymTypeExpression> replacedUpperBounds =
              var2UpperBounds.get(origVar).stream()
                  .map(t -> TypeParameterRelations.replaceInferenceVariables(t, origVar2NewInfVar))
                  .collect(Collectors.toList());
          Optional<SymTypeExpression> upperBound =
              getGlbOfProperUpperBounds(replacedUpperBounds);
          if (upperBound.isPresent()) {
            newInfVarsBounds.add(new SubTypingBound(newInfVar, upperBound.get()));
          }
          Optional<SymTypeExpression> sourceBound =
              getLubOfProperLowerBounds(var2SourceBounds.get(origVar));
          if (sourceBound.isPresent()) {
            newInfVarsBounds.add(new TypeCompatibilityBound(sourceBound.get(), newInfVar));
          }
          // FDr: need to check if this replacement is fine
          // currently no reason to assume otherwise
          List<SymTypeExpression> replacedTargetBounds =
              var2TargetBounds.get(origVar).stream()
                  .map(t -> TypeParameterRelations.replaceInferenceVariables(t, origVar2NewInfVar))
                  .collect(Collectors.toList());
          Optional<SymTypeExpression> targetBound =
              getGlbOfProperUpperBounds(replacedTargetBounds);
          if (targetBound.isPresent()) {
            newInfVarsBounds.add(new TypeCompatibilityBound(newInfVar, targetBound.get()));
          }
          // check for bound consistency
          if (lowerBound.isPresent() && upperBound.isPresent()) {
            if (!isSubTypeOf(lowerBound.get(), upperBound.get())) {
              Log.info("inconsistent bounds for fresh inference variable: "
                      + lowerBound.get().printFullName() + " is not a subtype of "
                      + upperBound.get().printFullName() + "."
                      + " Will stop resolution."
                  , LOG_NAME
              );
              return Optional.empty();
            }
          }
        }
        // create a new list of bounds that do not contain the capture bounds
        List<Bound> reducedBoundsFiltered = new ArrayList<>();
        for (Bound bound : reducedBounds) {
          if (!bound.isCaptureBound()) {
            reducedBoundsFiltered.add(bound);
          }
          else {
            List<SymTypeInferenceVariable> captureInfVars =
                ((CaptureBound) bound).getInferenceVariables();
            if (captureInfVars.stream().noneMatch(
                v -> varsToResolveNext.stream().anyMatch(v::denotesSameVar)
            )) {
              reducedBoundsFiltered.add(bound);
            }
          }
        }
        Optional<Map<SymTypeInferenceVariable, SymTypeExpression>> potentialResult =
            calculateResolve(
                new ArrayList<>(newInfVarsBounds),
                reducedBoundsFiltered,
                toBeResolved,
                varsWithoutInstantiation
            );
        // remove the temporary inference variables from the result
        if (potentialResult.isPresent()) {
          result = Optional.of(createSymTypeExprMap());
          for (Map.Entry<SymTypeInferenceVariable, SymTypeExpression> e : potentialResult.get().entrySet()) {
            if (newInfVars.stream().noneMatch(e.getKey()::denotesSameVar)) {
              result.get().put(e.getKey(), e.getValue());
            }
          }
        }
      }
    }
    // or create the complete instantiation map
    else {
      Map<SymTypeInferenceVariable, SymTypeExpression> var2Instantiation = createSymTypeExprMap();
      for (SymTypeInferenceVariable var : var2Equal.keySet()) {
        // try finding an instantiation
        List<SymTypeExpression> instantiations = new ArrayList<>();
        for (SymTypeExpression equalType : var2Equal.get(var)) {
          List<SymTypeInferenceVariable> infVars = TypeParameterRelations.getIncludedInferenceVariables(equalType);
          if (infVars.isEmpty()) {
            instantiations.add(equalType);
          }
        }
        if (instantiations.isEmpty()) {
          Log.error("0xFD410 internal error: "
              + "expected to find instantiation for " + var.printFullName()
              + " within bounds" + System.lineSeparator()
              + printBounds(reducedBounds)
          );
          return Optional.empty();
        }
        // any instantiation is OK, as at this point the constraints hold
        // that all the instantiations are pairwise equal.
        else {
          var2Instantiation.put(var, instantiations.get(0));
        }
      }
      Log.trace("resolution finished with instantiations:"
              + var2Instantiation.keySet().stream()
              .map(k -> System.lineSeparator() + k.printFullName()
                  + " = " + varInstantiations.get(k).printFullName()
              )
              .collect(Collectors.joining()),
          LOG_NAME
      );
      result = Optional.of(var2Instantiation);
    }

    return result;
  }

  protected List<Bound> incorporateAndReduce(
      List<Bound> newBoundsIn,
      List<Bound> oldBoundsIn
  ) {
    List<Bound> oldBounds = new ArrayList<>(oldBoundsIn);
    List<Bound> newBounds = newBoundsIn;
    List<Constraint> newConstraints = BoundIncorporation.incorporate(newBounds, oldBounds);
    // list of constraints COULD be simplified, but,
    // most likely does not have a big impact anyway
    while (!newConstraints.isEmpty()) {
      addAllIfNotDuplicate(oldBounds, newBounds);
      newBounds = ConstraintReduction.reduce(newConstraints);
      // here, bounds could be simplified
      // do not add bounds twice
      List<Bound> newBoundsNoDuplicates = new ArrayList<>();
      for (Bound newBound : newBounds) {
        if (oldBounds.stream().noneMatch(ob -> ob.deepEquals(newBound)) &&
            newBoundsNoDuplicates.stream().noneMatch(nb2 -> nb2.deepEquals(newBound))
        ) {
          newBoundsNoDuplicates.add(newBound);
        }
      }
      newConstraints = BoundIncorporation.incorporate(newBoundsNoDuplicates, oldBounds);
    }
    addAllIfNotDuplicate(oldBounds, newBounds);
    // here, bounds could be simplified
    return oldBounds;
  }

  /**
   * Finds new instantiations for a given minimal set of inference variables
   * with no further dependencies.
   * if there are no CaptureBounds, this tries a simple LuB/GlB approach.
   * For this approach, the inference variables must not be in a CapureBound!
   * s. Java Spec 21 18.4.
   */
  protected List<TypeEqualityBound> findInstantiationsSimple(
      List<SymTypeInferenceVariable> varsToResolve,
      Map<SymTypeInferenceVariable, List<SymTypeExpression>> var2LowerBounds,
      Map<SymTypeInferenceVariable, List<SymTypeExpression>> var2UpperBounds,
      Map<SymTypeInferenceVariable, List<SymTypeExpression>> var2SourceBounds,
      Map<SymTypeInferenceVariable, List<SymTypeExpression>> var2TargetBounds
  ) {
    List<TypeEqualityBound> newEqualityBounds = new ArrayList<>();
    for (SymTypeInferenceVariable var : varsToResolve) {
      final String logBoundInfo = "Subtypes: "
          + var2LowerBounds.get(var).stream()
          .map(SymTypeExpression::printFullName)
          .collect(Collectors.joining(", "))
          + System.lineSeparator() + "Types compatible to it: "
          + var2SourceBounds.get(var).stream()
          .map(SymTypeExpression::printFullName)
          .collect(Collectors.joining(", "))
          + System.lineSeparator() + "Supertypes: "
          + var2UpperBounds.get(var).stream()
          .map(SymTypeExpression::printFullName)
          .collect(Collectors.joining(", "))
          + System.lineSeparator() + "It is compatible to these types: "
          + var2UpperBounds.get(var).stream()
          .map(SymTypeExpression::printFullName)
          .collect(Collectors.joining(", "));
      Log.trace("START finding instantiation for " + var.printFullName()
              + "." + System.lineSeparator() + logBoundInfo,
          LOG_NAME
      );
      TypeEqualityBound newBound;
      Optional<SymTypeExpression> lubSubtyping =
          getLubOfProperLowerBounds(var2LowerBounds.get(var));
      Optional<SymTypeExpression> glbSubtyping =
          getGlbOfProperUpperBounds(var2UpperBounds.get(var));

      List<SymTypeExpression> properSources = var2SourceBounds.get(var).stream()
          .filter(Predicate.not(TypeParameterRelations::hasInferenceVariables))
          .collect(Collectors.toList());
      List<SymTypeExpression> properTargets = var2TargetBounds.get(var).stream()
          .filter(Predicate.not(TypeParameterRelations::hasInferenceVariables))
          .collect(Collectors.toList());

      // search for better lower bound in the source bounds
      List<SymTypeExpression> sourcesThatAreInSubTypingRelation = new ArrayList<>();
      for (SymTypeExpression source : properSources) {
        if (lubSubtyping.isEmpty() || isSubTypeOf(lubSubtyping.get(), source)) {
          if (glbSubtyping.isEmpty() || isSubTypeOf(source, glbSubtyping.get())) {
            sourcesThatAreInSubTypingRelation.add(source);
          }
        }
      }
      // This ought to be a better lower bound,
      // as they are supertypes of the current lower bound
      // Alternative: create a Lub using these sources
      // and the current lower bound? -> This should not change anything
      Optional<SymTypeExpression> newLubSubtyping =
          getLubOfProperLowerBounds(sourcesThatAreInSubTypingRelation);
      if (newLubSubtyping.isPresent()) {
        Log.trace("Using source bounds to replace lower bound "
                + lubSubtyping.map(SymTypeExpression::printFullName)
                .orElse("[none]")
                + " with " + newLubSubtyping.get().printFullName(),
            LOG_NAME
        );
        lubSubtyping = newLubSubtyping;
      }

      // need to check other sources,
      // as this is something not done during bound incorporation;
      // E.g., short <: a, Integer --> a:
      // here, it has to be checked that the LuB short is an actual lower bound,
      // it is not, as the source Integer is not compatible to it.
      // As Integer is not a superType of short, the LuB has not been replaced above.
      // The solution would have been int, but int cannot be found in the types given,
      // (one could add special cases for boxing, unboxing, but this is a general problem)
      // as such, we fail to find a lower bound.
      if (lubSubtyping.isPresent()) {
        for (SymTypeExpression source : properSources) {
          if (!isCompatible(lubSubtyping.get(), source)) {
            Log.trace("source bound " + source.printFullName()
                    + " is not compatible to (assumed) lower bound "
                    + lubSubtyping.get()
                    + ". Thus the lower bound is not valid and cannot be used.",
                LOG_NAME
            );
            lubSubtyping = Optional.empty();
          }
        }
      }

      // search for better upper bound in the target bounds
      // more often than not, the current upper bound is just Top
      List<SymTypeExpression> targetsThatAreInSubTypingRelation = new ArrayList<>();
      for (SymTypeExpression target : properTargets) {
        if (glbSubtyping.isEmpty() || isSubTypeOf(target, glbSubtyping.get())) {
          if (lubSubtyping.isEmpty() || isSubTypeOf(lubSubtyping.get(), target)) {
            targetsThatAreInSubTypingRelation.add(target);
          }
        }
      }
      // find better upper bound using applicable targets
      Optional<SymTypeExpression> newGlbSubtyping =
          getGlbOfProperUpperBounds(targetsThatAreInSubTypingRelation);
      if (newGlbSubtyping.isPresent()) {
        Log.trace("Using source bounds to replace upper bound "
                + glbSubtyping.map(SymTypeExpression::printFullName)
                .orElse("[none]")
                + " with " + newGlbSubtyping.get().printFullName(),
            LOG_NAME
        );
        glbSubtyping = newGlbSubtyping;
      }
      // check other targets
      if (glbSubtyping.isPresent()) {
        for (SymTypeExpression target : properTargets) {
          if (!isCompatible(target, glbSubtyping.get())) {
            Log.trace("(Assumed) upper bound"
                    + glbSubtyping.get().printFullName()
                    + " is not compatible to target bound "
                    + target.printFullName()
                    + ". Thus the upper bound is not valid and cannot be used.",
                LOG_NAME
            );
            glbSubtyping = Optional.empty();
          }
        }
      }

      if (lubSubtyping.isPresent()) {
        newBound = new TypeEqualityBound(var, lubSubtyping.get());
      }
      else if (glbSubtyping.isPresent()) {
        newBound = new TypeEqualityBound(var, glbSubtyping.get());
      }
      else {
        Log.error("0xFD319 error: For " + var.printFullName()
            + " no type fulfilling the bounds can be found."
            + " This usually occurs if the type"
            + " that would be fulfill the bounds cannot be found"
            + " within the bounds itself."
            + " Your expression is probably too complex wrt. bounds."
            + " Recommended solution: add type-casts to the expected type."
            + " Bounds:" + System.lineSeparator()
            + logBoundInfo
        );
        return Collections.emptyList();
      }
      newEqualityBounds.add(newBound);
    }
    Log.trace("resolved new instantiations:" + System.lineSeparator()
        + printBounds(newEqualityBounds), LOG_NAME);
    return newEqualityBounds;
  }

  // HookPoints

  /**
   * calculates the instantiation calculated using proper lower bounds,
   * s.a. JLS 21 18.4.
   * Can be overridden, e.g., by an implementation of JLS 21 4.10.4.
   */
  SymTypeExpression getLub(Collection<? extends SymTypeExpression> types) {
    return SymTypeRelations.normalize(SymTypeExpressionFactory.createUnionOrDefault(
        SymTypeExpressionFactory.createObscureType(), types
    ));
  }

  /**
   * calculates the instantiation calculated using proper upper bounds,
   * s.a. JLS 21 18.4.
   * Can be overridden, e.g., by an implementation of JLS 21 5.1.10.
   */
  SymTypeExpression getGlb(Collection<? extends SymTypeExpression> upperBounds) {
    return SymTypeRelations.normalize(SymTypeExpressionFactory.createIntersectionOrDefault(
        SymTypeExpressionFactory.createObscureType(), upperBounds
    ));
  }

  // Helper

  /**
   * to be used after incorporation/reduction.
   * Does only include top-most inference variables,
   * e.g., List<a1> <: a2, with a1,a2 being inference variables, returns a2.
   */
  protected List<SymTypeInferenceVariable> getInferenceVariablesOfBounds(List<Bound> bounds) {
    List<SymTypeInferenceVariable> inferenceVariables = new ArrayList<>();
    for (Bound bound : bounds) {
      List<SymTypeInferenceVariable> varsToBeAdded = new ArrayList<>();
      if (bound.isSubTypingBound()) {
        SubTypingBound subTypingBound = (SubTypingBound) bound;
        if (subTypingBound.getSubType().isInferenceVariable()) {
          varsToBeAdded.add(subTypingBound.getSubType().asInferenceVariable());
        }
        if (subTypingBound.getSuperType().isInferenceVariable()) {
          varsToBeAdded.add(subTypingBound.getSuperType().asInferenceVariable());
        }
      }
      if (bound.isTypeCompatibilityBound()) {
        TypeCompatibilityBound compatibilityBound = (TypeCompatibilityBound) bound;
        if (compatibilityBound.getSourceType().isInferenceVariable()) {
          varsToBeAdded.add(compatibilityBound.getSourceType().asInferenceVariable());
        }
        if (compatibilityBound.getTargetType().isInferenceVariable()) {
          varsToBeAdded.add(compatibilityBound.getTargetType().asInferenceVariable());
        }
      }
      else if (bound.isTypeEqualityBound()) {
        TypeEqualityBound typeEqualityBound = (TypeEqualityBound) bound;
        varsToBeAdded.add(typeEqualityBound.getFirstType());
        Optional<TypeEqualityBound> flipped = typeEqualityBound.getFlipped();
        if (flipped.isPresent()) {
          varsToBeAdded.add(flipped.get().getFirstType());
        }
      }
      else if (bound.isCaptureBound()) {
        CaptureBound captureBound = (CaptureBound) bound;
        varsToBeAdded.addAll(captureBound.getInferenceVariables());
      }
      for (SymTypeInferenceVariable varToBeAdded : varsToBeAdded) {
        // note: this could be made faster using a comparator or similar
        if (inferenceVariables.stream().noneMatch(varToBeAdded::deepEquals)) {
          inferenceVariables.add(varToBeAdded);
        }
      }
    }
    return inferenceVariables;
  }

  /**
   * Dependency matrix of inference variables
   */
  protected Map<SymTypeInferenceVariable, List<Bound>> getVarBoundDependencies(
      List<Bound> bounds
  ) {
    Map<SymTypeInferenceVariable, List<Bound>> dependencies =
        createSymTypeExprMap();
    List<SymTypeInferenceVariable> vars = getInferenceVariablesOfBounds(bounds);

    for (SymTypeInferenceVariable var : vars) {
      dependencies.put(var, new ArrayList<>());
    }
    for (Bound bound : bounds) {
      if (bound.isSubTypingBound()) {
        SubTypingBound subTypingBound = (SubTypingBound) bound;
        if (subTypingBound.getSubType().isInferenceVariable()) {
          dependencies.get(subTypingBound.getSubType().asInferenceVariable())
              .add(subTypingBound);
        }
        if (subTypingBound.getSuperType().isInferenceVariable()) {
          dependencies.get(subTypingBound.getSuperType().asInferenceVariable())
              .add(subTypingBound);
        }
      }
      if (bound.isTypeCompatibilityBound()) {
        TypeCompatibilityBound compatibilityBound = (TypeCompatibilityBound) bound;
        if (compatibilityBound.getSourceType().isInferenceVariable()) {
          dependencies.get(compatibilityBound.getSourceType().asInferenceVariable())
              .add(compatibilityBound);
        }
        if (compatibilityBound.getTargetType().isInferenceVariable()) {
          dependencies.get(compatibilityBound.getTargetType().asInferenceVariable())
              .add(compatibilityBound);
        }
      }
      else if (bound.isTypeEqualityBound()) {
        TypeEqualityBound typeEqualityBound = (TypeEqualityBound) bound;
        dependencies.get(typeEqualityBound.getFirstType())
            .add(typeEqualityBound);
        Optional<TypeEqualityBound> flipped = typeEqualityBound.getFlipped();
        if (flipped.isPresent()) {
          dependencies.get(flipped.get().getFirstType())
              .add(typeEqualityBound);
        }
      }
      else if (bound.isCaptureBound()) {
        CaptureBound captureBound = (CaptureBound) bound;
        for (SymTypeInferenceVariable capVar : captureBound.getInferenceVariables()) {
          dependencies.get(capVar).add(captureBound);
        }
      }
    }
    return dependencies;
  }

  /**
   * fills the dependency matrix.
   * any inferenceVariable, which does not have a bound yet,
   * has the bound added: TV <: #Top
   */
  protected Map<SymTypeInferenceVariable, List<Bound>> completeVarBoundDependencies(
      Map<SymTypeInferenceVariable, List<Bound>> varBoundDependencies
  ) {
    Map<SymTypeInferenceVariable, List<Bound>> completeDependencies =
        new TreeMap<>(new SymTypeExpressionComparator());
    completeDependencies.putAll(varBoundDependencies);
    List<SymTypeExpression> includedTypes = new ArrayList<>();
    for (List<Bound> bounds : varBoundDependencies.values()) {
      for (Bound bound : bounds) {
        includedTypes.addAll(bound.getIncludedTypes());
      }
    }
    Set<SymTypeInferenceVariable> includedVariables =
        new TreeSet<>(new SymTypeExpressionComparator());
    includedVariables.addAll(includedTypes.stream().flatMap(t ->
            TypeParameterRelations.getIncludedInferenceVariables(t).stream()
        ).collect(Collectors.toList())
    );
    for (SymTypeInferenceVariable var : includedVariables) {
      List<Bound> bounds = completeDependencies
          .getOrDefault(var, Collections.emptyList());
      boolean hasProperBound = false;
      for (Bound bound : bounds) {
        for (SymTypeExpression type : bound.getIncludedTypes()) {
          if (!TypeParameterRelations.hasInferenceVariables(type)) {
            hasProperBound = true;
          }
        }
      }
      if (!hasProperBound) {
        if (!completeDependencies.containsKey(var)) {
          completeDependencies.put(var, new ArrayList<>(1));
        }
        SubTypingBound newBound = new SubTypingBound(var, SymTypeExpressionFactory.createTopType());
        completeDependencies.get(var).add(newBound);
      }
    }
    return completeDependencies;
  }

  /**
   * uses only proper(!) types to create a LuB
   */
  protected Optional<SymTypeExpression> getLubOfProperLowerBounds(
      List<SymTypeExpression> lowerBounds
  ) {
    List<SymTypeExpression> properLowerBounds = new ArrayList<>();
    for (SymTypeExpression lowerBound : lowerBounds) {
      if (!TypeParameterRelations.hasInferenceVariables(lowerBound)) {
        properLowerBounds.add(lowerBound);
      }
    }
    if (!properLowerBounds.isEmpty()) {
      return Optional.of(getLub(properLowerBounds));
    }
    else {
      return Optional.empty();
    }
  }

  /**
   * uses only proper(!) types to create a GlB
   */
  protected Optional<SymTypeExpression> getGlbOfProperUpperBounds(
      List<SymTypeExpression> upperBounds
  ) {
    List<SymTypeExpression> properUpperBounds = new ArrayList<>();
    for (SymTypeExpression upperBound : upperBounds) {
      if (!TypeParameterRelations.hasInferenceVariables(upperBound)) {
        properUpperBounds.add(upperBound);
      }
    }
    if (!properUpperBounds.isEmpty()) {
      return Optional.of(getGlb(properUpperBounds));
    }
    else {
      return Optional.empty();
    }
  }

  /**
   * returns a map that does not rely on hashes
   * (which does not work well with SymTypeExpressions)
   */
  protected <S extends SymTypeExpression, T>
  Map<S, T> createSymTypeExprMap() {
    return new TreeMap<>(new SymTypeExpressionComparator());
  }

  /**
   * Helper; adds all newBounds to oldBounds as long as they are not included.
   *
   * @param oldBounds is modified!
   */
  protected void addAllIfNotDuplicate(
      List<Bound> oldBounds,
      List<Bound> newBounds
  ) {
    for (Bound newBound : newBounds) {
      if (oldBounds.stream().noneMatch(newBound::deepEquals)) {
        oldBounds.add(newBound);
      }
    }
  }

  protected String printBounds(List<? extends Bound> constraints) {
    return constraints.stream()
        .map(Bound::print)
        .collect(Collectors.joining(System.lineSeparator()));
  }
}
