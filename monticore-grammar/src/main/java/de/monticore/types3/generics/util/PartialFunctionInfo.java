package de.monticore.types3.generics.util;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * describes partial SymTypeOfFunction information,
 * used during selection of generic overloaded functions
 */
public class PartialFunctionInfo {

  Optional<SymTypeExpression> returnTargetType = Optional.empty();

  Optional<Integer> parameterCount = Optional.empty();

  Map<Integer, ASTExpression> argumentExprs = new HashMap<>();

  Map<Integer, SymTypeExpression> argumentTypes = new HashMap<>();

  public PartialFunctionInfo() {
  }

  public PartialFunctionInfo(SymTypeOfFunction targetType) {
    returnTargetType = Optional.of(targetType.getType());
    parameterCount = Optional.of(targetType.sizeArgumentTypes());
    for (int i = 0; i < targetType.sizeArgumentTypes(); i++) {
      argumentTypes.put(i, targetType.getArgumentType(i));
    }
  }

  public void setReturnTargetType(SymTypeExpression returnTargetType) {
    this.returnTargetType = Optional.of(returnTargetType);
  }

  public boolean hasReturnTargetType() {
    return returnTargetType.isPresent();
  }

  public SymTypeExpression getReturnTargetType() {
    return returnTargetType.get();
  }

  public void setParameterCount(int parameterCount) {
    this.parameterCount = Optional.of(parameterCount);
  }

  public boolean hasParameterCount() {
    return parameterCount.isPresent();
  }

  public int getParameterCount() {
    return parameterCount.get();
  }

  /**
   * @param argumentType if null removes entry
   */
  public void setArgumentType(int idx, SymTypeExpression argumentType) {
    if (argumentType == null) {
      argumentTypes.remove(idx);
    }
    else {
      argumentTypes.put(idx, argumentType);
    }
  }

  public boolean hasArgumentType(int idx) {
    return argumentTypes.containsKey(idx);
  }

  public SymTypeExpression getArgumentType(int idx) {
    return argumentTypes.get(idx);
  }

  public boolean hasAllArgumentTypes() {
    if (!hasParameterCount()) {
      return false;
    }
    for (int i = 0; i < getParameterCount(); i++) {
      if (!hasArgumentType(i)) {
        return false;
      }
    }
    return true;
  }

  /**
   * @param argumentExpr if null removes entry
   */
  public void setArgumentExpr(int idx, ASTExpression argumentExpr) {
    if (argumentExpr == null) {
      argumentExprs.remove(idx);
    }
    else {
      argumentExprs.put(idx, argumentExpr);
    }
  }

  /**
   * QOL: sets all argument expressions + number of arguments at once
   */
  public void setArgumentExprs(List<? extends ASTExpression> argumentExprs) {
    setParameterCount(argumentExprs.size());
    for (int i = 0; i < argumentExprs.size(); i++) {
      setArgumentExpr(i, argumentExprs.get(i));
    }
  }

  public boolean hasArgumentExpr(int idx) {
    return argumentExprs.containsKey(idx);
  }

  public ASTExpression getArgumentExpr(int idx) {
    return argumentExprs.get(idx);
  }

  /**
   * @return the collected information as a function, iff complete.
   *     WARNING: the returned functions return type is a return target(!) type,
   *     NOT the actual expected return type!
   */
  public Optional<SymTypeOfFunction> getAsFunctionIfComplete() {
    Optional<SymTypeOfFunction> result;
    if (!hasAllArgumentTypes() || !hasReturnTargetType()) {
      result = Optional.empty();
    }
    else {
      List<SymTypeExpression> argTypes = new ArrayList<>();
      for (int i = 0; i < getParameterCount(); i++) {
        argTypes.add(getArgumentType(i));
      }
      result = Optional.of(SymTypeExpressionFactory.createFunction(
          getReturnTargetType(),
          argTypes
      ));
    }
    return result;
  }

  public PartialFunctionInfo deepClone() {
    PartialFunctionInfo clone = new PartialFunctionInfo();
    clone.returnTargetType = returnTargetType.map(SymTypeExpression::deepClone);
    clone.parameterCount = parameterCount.map(Function.identity());
    clone.argumentExprs = new HashMap<>(argumentExprs);
    clone.argumentTypes = new HashMap<>(argumentTypes);
    return clone;
  }

}
