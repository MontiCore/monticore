// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.generics.util;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeVariable;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.generics.TypeParameterRelations;
import de.monticore.types3.generics.bounds.Bound;
import de.monticore.types3.generics.bounds.CaptureBound;
import de.monticore.types3.generics.bounds.SubTypingBound;
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
import java.util.stream.Collectors;

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
  public static Optional<Map<SymTypeVariable, SymTypeExpression>> resolve(
      List<Bound> bounds
  ) {
    return resolve(bounds, Collections.emptyList());
  }

  /**
   * s. {@link #resolve(List)}
   */
  public static Optional<Map<SymTypeVariable, SymTypeExpression>> resolve(
      List<Bound> newBounds,
      List<Bound> oldBounds
  ) {
    return getDelegate().calculateResolve(
        newBounds, oldBounds, Collections.emptySet()
    );
  }

  /**
   * @param lastSetOfUninstantiated used to stop infinite recursion
   */
  protected Optional<Map<SymTypeVariable, SymTypeExpression>> calculateResolve(
      List<Bound> newBounds,
      List<Bound> oldBounds,
      Collection<SymTypeVariable> lastSetOfUninstantiated
  ) {
    // shortcut reducing log
    if (newBounds.isEmpty() && oldBounds.isEmpty()) {
      return Optional.of(new HashMap<>());
    }
    Optional<Map<SymTypeVariable, SymTypeExpression>> result = Optional.empty();

    // get all relevant bounds: reduced bounds
    List<Bound> reducedBounds = incorporateAndReduce(newBounds, oldBounds);
    if (reducedBounds.stream().anyMatch(Bound::isUnsatisfiableBound)) {
      return Optional.empty();
    }

    // sort the bounds according to the variable
    Map<SymTypeVariable, List<Bound>> var2BoundsIncomplete =
        getVarBoundDependencies(reducedBounds);
    Map<SymTypeVariable, List<Bound>> var2Bounds =
        completeVarBoundDependencies(var2BoundsIncomplete);

    Map<SymTypeVariable, List<SymTypeExpression>> var2Equal = createSymTypeExprMap();
    Map<SymTypeVariable, List<SymTypeExpression>> var2LowerBounds = createSymTypeExprMap();
    Map<SymTypeVariable, List<SymTypeExpression>> var2UpperBounds = createSymTypeExprMap();
    Map<SymTypeVariable, CaptureBound> var2CaptureBound = createSymTypeExprMap();
    for (Map.Entry<SymTypeVariable, List<Bound>> varBounds : var2Bounds.entrySet()) {
      SymTypeVariable var = varBounds.getKey();
      var2Equal.put(var, new ArrayList<>());
      var2LowerBounds.put(var, new ArrayList<>());
      var2UpperBounds.put(var, new ArrayList<>());
    }
    for (Map.Entry<SymTypeVariable, List<Bound>> varBounds : var2Bounds.entrySet()) {
      for (Bound bound : varBounds.getValue()) {
        if (bound.isSubTypingBound()) {
          SubTypingBound subTypingBound = (SubTypingBound) bound;
          if (TypeParameterRelations.isInferenceVariable(subTypingBound.getSubType())) {
            var2UpperBounds.get(subTypingBound.getSubType().asTypeVariable())
                .add(subTypingBound.getSuperType());
          }
          if (TypeParameterRelations.isInferenceVariable(subTypingBound.getSuperType())) {
            var2LowerBounds.get(subTypingBound.getSuperType().asTypeVariable())
                .add(subTypingBound.getSubType());
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
    Map<SymTypeVariable, List<SymTypeVariable>> varInterDependencies =
        createSymTypeExprMap();
    for (SymTypeVariable var : var2Bounds.keySet()) {
      varInterDependencies.put(var, new ArrayList<>());
    }
    for (SymTypeVariable var : var2Bounds.keySet()) {
      // get all the Expressions together and find all type variables
      List<SymTypeExpression> allTypes = new ArrayList<>();
      allTypes.addAll(var2Equal.get(var));
      allTypes.addAll(var2LowerBounds.get(var));
      allTypes.addAll(var2UpperBounds.get(var));
      List<SymTypeVariable> relatedInfVars =
          TypeParameterRelations.getIncludedInferenceVariables(allTypes);
      // if the inference variable is on the left side of a capture bound,
      // it needs to be resolved before the related variables.
      if (!var2CaptureBound.containsKey(var)) {
        varInterDependencies.get(var).addAll(relatedInfVars);
      }
      else {
        CaptureBound captureBound = var2CaptureBound.get(var);
        for (SymTypeVariable relatedInfVar : relatedInfVars) {
          varInterDependencies.get(relatedInfVar).add(var);
        }
        varInterDependencies.get(var).addAll(captureBound.getInferenceVariables());
        varInterDependencies.get(var).addAll(TypeParameterRelations
            .getIncludedInferenceVariables(captureBound.getToBeCaptured())
        );
      }
    }
    // remove skolem variables
    for (SymTypeVariable var : varInterDependencies.keySet()) {
      List<SymTypeVariable> dependencies = varInterDependencies.get(var);
      varInterDependencies.put(var, dependencies);
    }

    // get current instantiations
    List<SymTypeVariable> varsWithoutInstantiation = new ArrayList<>();
    Map<SymTypeVariable, SymTypeExpression> varInstantiations = createSymTypeExprMap();
    for (SymTypeVariable var : varInterDependencies.keySet()) {
      Optional<SymTypeExpression> instantiation = Optional.empty();
      for (SymTypeExpression potInstantiation : var2Equal.get(var)) {
        List<SymTypeVariable> includedInferenceVariables =
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
    List<SymTypeVariable> varsToResolveNext;
    List<SymTypeVariable> varsToResolveNextNotFinal =
        varsWithoutInstantiation;
    for (SymTypeVariable var : varsWithoutInstantiation) {
      Set<SymTypeVariable> deps =
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
            varsToResolveNext, var2LowerBounds, var2UpperBounds
        );
        // use the new-found instantiations to reiterate
        result = calculateResolve(
            new ArrayList<>(newEqualityBounds), reducedBounds,
            varsWithoutInstantiation
        );
      }
      // Complex method involving the creation of new inference variables
      // (s. JLS 21 18.4, lower part)
      if (result.isEmpty()) {
        List<SymTypeVariable> newInfVars = new ArrayList<>();
        List<SubTypingBound> newInfVarsBounds = new ArrayList<>();
        Map<SymTypeVariable, SymTypeVariable> origVar2NewInfVar =
            createSymTypeExprMap();
        for (SymTypeVariable var : varsToResolveNext) {
          // real bounds are set below explicitly
          SymTypeVariable newInfVar = SymTypeExpressionFactory.createTypeVariable(
              SymTypeExpressionFactory.createBottomType(),
              SymTypeExpressionFactory.createTopType()
          );
          newInfVars.add(newInfVar);
          origVar2NewInfVar.put(var, newInfVar);
        }
        for (int i = 0; i < newInfVars.size(); i++) {
          SymTypeVariable newInfVar = newInfVars.get(i);
          SymTypeVariable origVar = varsToResolveNext.get(i);
          Optional<SymTypeExpression> lowerBound =
              getLubOfProperLowerBounds(var2LowerBounds.get(origVar));
          if (lowerBound.isPresent()) {
            newInfVarsBounds.add(new SubTypingBound(lowerBound.get(), newInfVar));
          }
          List<SymTypeExpression> replacedUpperBounds =
              var2UpperBounds.get(origVar).stream()
                  .map(t -> TypeParameterRelations.replaceTypeVariables(t, origVar2NewInfVar))
                  .collect(Collectors.toList());
          Optional<SymTypeExpression> upperBound =
              getGlbOfProperUpperBounds(replacedUpperBounds);
          if (upperBound.isPresent()) {
            newInfVarsBounds.add(new SubTypingBound(newInfVar, upperBound.get()));
          }
          // check for bound consistency
          if (lowerBound.isPresent() && upperBound.isPresent()) {
            if (!SymTypeRelations.isSubTypeOf(lowerBound.get(), upperBound.get())) {
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
            List<SymTypeVariable> captureInfVars =
                ((CaptureBound) bound).getInferenceVariables();
            if (captureInfVars.stream().noneMatch(
                v -> varsToResolveNext.stream().anyMatch(v::denotesSameVar)
            )) {
              reducedBoundsFiltered.add(bound);
            }
          }
        }
        Optional<Map<SymTypeVariable, SymTypeExpression>> potentialResult =
            calculateResolve(new ArrayList<>(newInfVarsBounds), reducedBoundsFiltered, varsWithoutInstantiation);
        // remove the temporary inference variables from the result
        if (potentialResult.isPresent()) {
          result = Optional.of(createSymTypeExprMap());
          for (Map.Entry<SymTypeVariable, SymTypeExpression> e : potentialResult.get().entrySet()) {
            if (newInfVars.stream().noneMatch(e.getKey()::denotesSameVar)) {
              result.get().put(e.getKey(), e.getValue());
            }
          }
        }
      }
    }
    // or create the complete instantiation map
    else {
      Map<SymTypeVariable, SymTypeExpression> var2Instantiation = createSymTypeExprMap();
      for (SymTypeVariable var : var2Equal.keySet()) {
        // try finding an instantiation
        List<SymTypeExpression> instantiations = new ArrayList<>();
        for (SymTypeExpression equalType : var2Equal.get(var)) {
          List<SymTypeVariable> infVars = TypeParameterRelations.getIncludedInferenceVariables(equalType);
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
      List<SymTypeVariable> varsToResolve,
      Map<SymTypeVariable, List<SymTypeExpression>> var2LowerBounds,
      Map<SymTypeVariable, List<SymTypeExpression>> var2UpperBounds
  ) {
    List<TypeEqualityBound> newEqualityBounds = new ArrayList<>();
    for (SymTypeVariable var : varsToResolve) {
      TypeEqualityBound newBound;
      Optional<SymTypeExpression> lub =
          getLubOfProperLowerBounds(var2LowerBounds.get(var));
      Optional<SymTypeExpression> glb =
          getGlbOfProperUpperBounds(var2UpperBounds.get(var));
      if (lub.isPresent()) {
        newBound = new TypeEqualityBound(var, lub.get());
      }
      else if (glb.isPresent()) {
        newBound = new TypeEqualityBound(var, glb.get());
      }
      else {
        Log.error("0xFD319 internal error: "
            + var.printFullName() + " has no proper bounds");
        return Collections.emptyList();
      }
      newEqualityBounds.add(newBound);
    }
    Log.trace("resolved new instantiations:" + System.lineSeparator()
        + printBounds(new ArrayList<>(newEqualityBounds)), LOG_NAME);
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
  protected List<SymTypeVariable> getInferenceVariablesOfBounds(List<Bound> bounds) {
    List<SymTypeVariable> inferenceVariables = new ArrayList<>();
    for (Bound bound : bounds) {
      List<SymTypeVariable> varsToBeAdded = new ArrayList<>();
      if (bound.isSubTypingBound()) {
        SubTypingBound subTypingBound = (SubTypingBound) bound;
        if (TypeParameterRelations.isInferenceVariable(subTypingBound.getSubType())) {
          varsToBeAdded.add(subTypingBound.getSubType().asTypeVariable());
        }
        if (TypeParameterRelations.isInferenceVariable(subTypingBound.getSuperType())) {
          varsToBeAdded.add(subTypingBound.getSuperType().asTypeVariable());
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
      for (SymTypeVariable varToBeAdded : varsToBeAdded) {
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
  protected Map<SymTypeVariable, List<Bound>> getVarBoundDependencies(
      List<Bound> bounds
  ) {
    Map<SymTypeVariable, List<Bound>> dependencies =
        createSymTypeExprMap();
    List<SymTypeVariable> vars = getInferenceVariablesOfBounds(bounds);

    for (SymTypeVariable var : vars) {
      dependencies.put(var, new ArrayList<>());
    }
    for (Bound bound : bounds) {
      if (bound.isSubTypingBound()) {
        SubTypingBound subTypingBound = (SubTypingBound) bound;
        if (TypeParameterRelations.isInferenceVariable(subTypingBound.getSubType())) {
          dependencies.get(subTypingBound.getSubType().asTypeVariable())
              .add(subTypingBound);
        }
        if (TypeParameterRelations.isInferenceVariable(subTypingBound.getSuperType())) {
          dependencies.get(subTypingBound.getSuperType().asTypeVariable())
              .add(subTypingBound);
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
        for (SymTypeVariable capVar : captureBound.getInferenceVariables()) {
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
  protected Map<SymTypeVariable, List<Bound>> completeVarBoundDependencies(
      Map<SymTypeVariable, List<Bound>> varBoundDependencies
  ) {
    Map<SymTypeVariable, List<Bound>> completeDependencies =
        new TreeMap<>(new SymTypeExpressionComparator());
    completeDependencies.putAll(varBoundDependencies);
    List<SymTypeExpression> includedTypes = new ArrayList<>();
    for (List<Bound> bounds : varBoundDependencies.values()) {
      for (Bound bound : bounds) {
        includedTypes.addAll(bound.getIncludedTypes());
      }
    }
    Set<SymTypeVariable> includedVariables =
        new TreeSet<>(new SymTypeExpressionComparator());
    includedVariables.addAll(includedTypes.stream().flatMap(t ->
            TypeParameterRelations.getIncludedInferenceVariables(t).stream()
        ).collect(Collectors.toList())
    );
    for (SymTypeVariable var : includedVariables) {
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

  protected String printBounds(List<Bound> constraints) {
    return constraints.stream()
        .map(Bound::print)
        .collect(Collectors.joining(System.lineSeparator()));
  }
}
