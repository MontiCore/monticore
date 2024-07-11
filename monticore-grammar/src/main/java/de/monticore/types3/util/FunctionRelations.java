// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.util;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types.check.SymTypeVariable;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.generics.TypeParameterRelations;
import de.monticore.types3.generics.bounds.Bound;
import de.monticore.types3.generics.util.BoundResolution;
import de.monticore.types3.generics.util.PartialFunctionInfo;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * calculates, e.g., whether a function can be called given specified arguments,
 * one may assume this functionality ought to be in SymTypeOfFunction,
 * however, it relies on other functionality in SymTypeRelations,
 * and the behaviour of SymTypeClasses
 * should not be dependent on the current type system
 * (or one would need to pass the SymTypeRelations to the SymTypes)
 * delegate of SymTypeRelations
 */
public class FunctionRelations {

  protected static final String LOG_NAME = "FunctionRelations";

  // static delegate

  protected static FunctionRelations delegate;

  public static void init() {
    Log.trace("init default FunctionRelations", "TypeCheck setup");
    FunctionRelations.delegate = new FunctionRelations();
  }

  static {
    init();
  }

  /**
   * whether the function can be called with the given arguments.
   */
  public static boolean canBeCalledWith(
      SymTypeOfFunction func,
      List<SymTypeExpression> args) {
    return delegate.calculateCanBeCalledWith(func, args);
  }

  protected boolean calculateCanBeCalledWith(
      SymTypeOfFunction func,
      List<SymTypeExpression> args) {
    if (!internal_canPotentiallyBeCalledWith(func, args)) {
      return false;
    }
    // check the arguments themselves, even for generic methods
    else {
      for (int i = 0; i < args.size(); i++) {
        SymTypeExpression argType = args.get(i);
        SymTypeExpression paramType = func.getArgumentType(
            Math.min(i, func.getArgumentTypeList().size() - 1));
        SymTypeExpression capArgType = TypeParameterRelations.getCaptureConverted(argType);
        if (!TypeParameterRelations.hasInferenceVariables(paramType)) {
          List<Bound> bounds = SymTypeRelations.constrainCompatible(paramType, capArgType);
          Optional<Map<SymTypeVariable, SymTypeExpression>> resolution =
              BoundResolution.resolve(bounds);
          if (resolution.isEmpty()) {
            return false;
          }
        }
      }
    }
    return true;
  }

  /**
   * for overloaded and applicable(!) functions, selects the best fitting one
   * we expect inferred arities (no elliptic functions)
   * simplified version (which can be extended) of:
   * * Java spec v.20 15.12.2.5
   * * Java spec v.20 18.5.4
   */
  public static Optional<SymTypeOfFunction> getMostSpecificFunctionOrLogError(
      Collection<SymTypeOfFunction> funcs) {
    return delegate.calculateGetMostSpecificFunctionOrLogError(funcs);
  }

  /**
   * s. {@link #getMostSpecificFunctionOrLogError(Collection)},
   * does not Log errors
   */
  public static Optional<SymTypeOfFunction> getMostSpecificFunction(
      Collection<SymTypeOfFunction> funcs) {
    return delegate.calculateGetMostSpecificFunction(funcs);
  }

  protected Optional<SymTypeOfFunction> calculateGetMostSpecificFunctionOrLogError(
      Collection<SymTypeOfFunction> funcs) {
    Optional<SymTypeOfFunction> mostSpecificFunction;
    if (funcs.isEmpty()) {
      mostSpecificFunction = Optional.empty();
    }
    else {
      List<SymTypeOfFunction> potentialFuncs =
          getMostSpecificFunctions(funcs);
      if (potentialFuncs.isEmpty()) {
        Log.error("0xFDCBA could not determine most specific function of:"
            + System.lineSeparator()
            + funcs.stream()
            .map(this::printFunctionForLog)
            .collect(Collectors.joining(System.lineSeparator()))
        );
        mostSpecificFunction = Optional.empty();
      }
      else if (potentialFuncs.size() > 1) {
        Log.error("0xFDCBB could not determine most specific function of:"
            + System.lineSeparator()
            + potentialFuncs.stream()
            .map(this::printFunctionForLog)
            .collect(Collectors.joining(System.lineSeparator()))
        );
        mostSpecificFunction = Optional.empty();
      }
      else {
        mostSpecificFunction = potentialFuncs.stream().findAny();
      }
    }
    return mostSpecificFunction;
  }

  protected Optional<SymTypeOfFunction> calculateGetMostSpecificFunction(
      Collection<SymTypeOfFunction> funcs) {
    if (funcs.isEmpty()) {
      return Optional.empty();
    }
    List<SymTypeOfFunction> potentialFuncs = getMostSpecificFunctions(funcs);
    return potentialFuncs.size() == 1 ?
        Optional.of(potentialFuncs.get(0)) :
        Optional.empty();
  }

  /**
   * calculates the most specific functionS(!).
   * This is never required (one only ever needs THE most specific function),
   * except to give better log messages.
   */
  protected List<SymTypeOfFunction> getMostSpecificFunctions(
      Collection<SymTypeOfFunction> funcs) {
    List<SymTypeOfFunction> mostSpecificFunctions;
    int arity = funcs.stream().findAny().get().sizeArgumentTypes();
    if (funcs.stream().anyMatch(SymTypeOfFunction::isElliptic)
        || funcs.stream().anyMatch(f -> f.sizeArgumentTypes() != arity)) {
      Log.error("0xFD11D internal error:"
          + "expected a set of functions with same arity");
      mostSpecificFunctions = Collections.emptyList();
    }
    else {
      List<SymTypeOfFunction> potentialFuncs = new ArrayList<>(funcs);
      // if a potential function has a parameter type which is not a subType
      // of all the corresponding parameter types of the other functions,
      // the function is not as specific as the other functions
      for (int i = 0; i < arity; i++) {
        int it = i;
        potentialFuncs.removeIf(
            potFunc -> funcs.stream().anyMatch(
                func -> !SymTypeRelations.isSubTypeOf(
                    SymTypeRelations.box(potFunc.getArgumentType(it)),
                    SymTypeRelations.box(func.getArgumentType(it))
                )
            )
        );
      }
      mostSpecificFunctions = potentialFuncs;
    }

    return mostSpecificFunctions;
  }

  /**
   * does not check the argument types for generic methods
   * s.a. Java spec 21 15.12.2.1
   * ignores "potential compatibility" poly expressions
   */
  public static boolean internal_canPotentiallyBeCalledWith(
      SymTypeOfFunction func,
      List<SymTypeExpression> args
  ) {
    // does not set the return type
    PartialFunctionInfo target = new PartialFunctionInfo();
    target.setParameterCount(args.size());
    for (int i = 0; i < args.size(); i++) {
      target.setArgumentType(i, args.get(i));
    }
    return delegate.calculateCanPotentiallyBeCalledWith(func, target);
  }

  public static boolean internal_canPotentiallyBeCalledWith(
      SymTypeOfFunction func,
      PartialFunctionInfo target
  ) {
    return delegate.calculateCanPotentiallyBeCalledWith(func, target);
  }

  protected boolean calculateCanPotentiallyBeCalledWith(
      SymTypeOfFunction func,
      PartialFunctionInfo target
  ) {
    // too few information
    if (!target.hasParameterCount()) {
      Log.error("0xFD332 internal error: expected amount of parameters.");
      return false;
    }
    // check the number of arguments
    int argsSize = target.getParameterCount();
    if (!func.isElliptic() && argsSize != func.sizeArgumentTypes()) {
      return false;
    }
    else if (func.isElliptic() && argsSize < func.sizeArgumentTypes() - 1) {
      return false;
    }
    // check the arguments themselves IFF
    // the function's parameter has no free type variables
    for (int i = 0; i < argsSize; i++) {
      if (target.hasArgumentType(i)) {
        SymTypeExpression argType = target.getArgumentType(i);
        SymTypeExpression paramType = func.getArgumentType(
            Math.min(i, func.getArgumentTypeList().size() - 1));
        SymTypeExpression capArgType = TypeParameterRelations.getCaptureConverted(argType);
        if (!TypeParameterRelations.hasInferenceVariables(paramType)) {
          List<Bound> bounds = SymTypeRelations.constrainCompatible(paramType, capArgType);
          Optional<Map<SymTypeVariable, SymTypeExpression>> resolution =
              BoundResolution.resolve(bounds);
          if (resolution.isEmpty()) {
            return false;
          }
        }
      }
    }
    return true;
  }

  // Helper

  protected String printFunctionForLog(SymTypeOfFunction func) {
    String result = "";
    result += func.printFullName();
    if (func.hasSymbol()) {
      result += " (symbol: "
          + func.getSymbol().getFullName()
          + ")"
      ;
    }
    return result;
  }
}
