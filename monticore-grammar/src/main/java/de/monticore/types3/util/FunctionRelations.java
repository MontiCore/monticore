// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.util;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types3.ISymTypeRelations;
import de.se_rwth.commons.logging.Log;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

  protected ISymTypeRelations symTypeRelations;

  public FunctionRelations(ISymTypeRelations symTypeRelations) {
    this.symTypeRelations = symTypeRelations;
  }

  protected ISymTypeRelations getSymTypeRelations() {
    if (symTypeRelations == null) {
      Log.error("0xFD81A internal error:"
          + "SymTypeNormalizeVisitor not set up correctly");
    }
    return symTypeRelations;
  }

  public boolean canBeCalledWith(
      SymTypeOfFunction func,
      List<SymTypeExpression> args) {
    // check amount of arguments
    if (!func.isElliptic() && args.size() != func.sizeArgumentTypes()) {
      return false;
    }
    else if (func.isElliptic() && args.size() < func.sizeArgumentTypes() - 1) {
      return false;
    }
    // check the arguments themselves
    else {
      for (int i = 0; i < args.size(); i++) {
        SymTypeExpression paramType = func.getArgumentType(
            Math.min(i, func.getArgumentTypeList().size() - 1));
        if (!getSymTypeRelations().isCompatible(paramType, args.get(i))) {
          // todo extend when adding generic support
          return false;
        }
      }
    }
    return true;
  }

  /**
   * for overloaded functions, selects the best fitting one
   * we expect inferred arities (no elliptic functions)
   * simplified version (which can be extended) of:
   * * Java spec v.20 15.12.2.5
   * * Java spec v.20 18.5.4
   */
  public Optional<SymTypeOfFunction> getMostSpecificFunction(
      Collection<SymTypeOfFunction> funcs) {
    Optional<SymTypeOfFunction> mostSpecificFunction;
    if (funcs.isEmpty()) {
      mostSpecificFunction = Optional.empty();
    }
    else {
      int arity = funcs.stream().findAny().get().sizeArgumentTypes();
      if (funcs.stream().anyMatch(SymTypeOfFunction::isElliptic)
          || funcs.stream().anyMatch(f -> f.sizeArgumentTypes() != arity)) {
        Log.error("0xFD11D internal error:"
            + "expected a set of functions with same arity");
        mostSpecificFunction = Optional.empty();
      }
      else {
        Set<SymTypeOfFunction> potentialFuncs = new HashSet<>(funcs);
        // if a potential function has a parameter type which is not a subType
        // of all the corresponding parameter types of the other functions,
        // the function is not as specific as the other functions
        for (int i = 0; i < arity; i++) {
          int it = i;
          potentialFuncs.removeIf(
              potFunc -> funcs.stream().anyMatch(
                  func -> !getSymTypeRelations().isSubTypeOf(
                      getSymTypeRelations().box(potFunc.getArgumentType(it)),
                      getSymTypeRelations().box(func.getArgumentType(it))
                  )
              )
          );
        }
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
    }

    return mostSpecificFunction;
  }

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
