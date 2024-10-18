/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.base.Preconditions;
import de.monticore.expressions.assignmentexpressions._ast.ASTAssignmentExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis._ast.ASTNameExpression;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.ComponentSymbol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Represents all sorts of component kinds. E.g., a {@code ComponentExpression} can represent a generic component with
 * bound type arguments {@code MyComp<Integer>}. This is not representable by Symbols alone, as generic
 * components only have unspecific type parameters ({@code MyComp<T>}.
 */
public abstract class CompKindExpression {

  protected final ComponentSymbol component;
  protected LinkedHashMap<VariableSymbol, ASTExpression> parameterBindings;
  protected List<ASTExpression> arguments;

  /**
   * @return a {@code List} of the configuration arguments of this component.
   */
  public List<ASTExpression> getArguments() {
    return this.arguments;
  }

  /**
   * @param argument the configuration argument to add to this component.
   */
  public void addArgument(ASTExpression argument) {
    Preconditions.checkNotNull(argument);
    this.arguments.add(argument);
  }

  /**
   * @param arguments the configuration arguments to add to this component.
   * @see this#addArgument(ASTExpression)
   */
  public void addArgument(List<ASTExpression> arguments) {
    Preconditions.checkNotNull(arguments);
    Preconditions.checkArgument(!arguments.contains(null));
    for (ASTExpression argument : arguments) {
      this.addArgument(argument);
    }
  }

  public Optional<ASTExpression> getParamBindingFor(VariableSymbol var) {
    Preconditions.checkNotNull(var);
    return Optional.ofNullable(this.getParamBindings().get(var));
  }

  public Map<VariableSymbol, ASTExpression> getParamBindings() {
    return Collections.unmodifiableMap(this.parameterBindings);
  }

  public List<ASTExpression> getParamBindingsAsList() {
    return new ArrayList<>(this.getParamBindings().values());
  }

  public void bindParams() {
    List<ASTExpression> parameterArguments = this.getArguments();

    int firstKeywordArgument = 0;
    LinkedHashMap<String, ASTExpression> keywordExpressionMap = new LinkedHashMap<>();
    LinkedHashMap<VariableSymbol, ASTExpression> parameterBindings = new LinkedHashMap<>();
    // We know LinkedHashMaps are ordered by insertion time. As we rely on the fact that the ordering of the
    // arguments is consistent with the ordering in the map, the following iteration ensures it:
    for (int i = 0; i < this.getTypeInfo().getParameters().size(); i++) {
      if (i < parameterArguments.size()) // Deal with wrong number of parameters through cocos
        if (parameterArguments.get(i) instanceof ASTAssignmentExpression
          && ((ASTAssignmentExpression) parameterArguments.get(i)).getLeft() instanceof ASTNameExpression) {
          keywordExpressionMap.put(((ASTNameExpression) ((ASTAssignmentExpression) parameterArguments.get(i))
            .getLeft()).getName(), parameterArguments.get(i));
        } else {
          parameterBindings.put(this.getTypeInfo().getParameters().get(i), parameterArguments.get(i));
          firstKeywordArgument++;
        }
    }

    // iterate over keyword-based arguments (CoCo assures that no position-based argument occurs
    // after the first keyword-based argument)
    for (int j = firstKeywordArgument; j < this.getTypeInfo().getParameters().size(); j++) {
      if (keywordExpressionMap.containsKey(this.getTypeInfo().getParameters().get(j).getName()) &&
        !parameterBindings.containsKey(this.getTypeInfo().getParameters().get(j))) {
        parameterBindings.put(this.getTypeInfo().getParameters().get(j),
          keywordExpressionMap.get(this.getTypeInfo().getParameters().get(j).getName()));
      }
    }

    this.parameterBindings = parameterBindings;
  }
  protected CompKindExpression(ComponentSymbol component) {
    Preconditions.checkNotNull(component);
    this.component = component;
    this.arguments = new ArrayList<>();
    this.parameterBindings = new LinkedHashMap<>();
  }

  public ComponentSymbol getTypeInfo() {
    return this.component;
  }

  public abstract String printName();

  public abstract String printFullName();

  /**
   * @return The {@link CompKindExpression} that represents this component's super components. E.g., given
   * {@code Comp<T> extends Parent<List<T>>}, the returned list for component expression  {@code Comp<Person>}
   * contains a single entry representing {@code Parent<List<Person>>}. The List is empty if the component has
   * no super components.
   */
  public abstract List<CompKindExpression> getSuperComponents();

  /**
   * Returns the SymTypeExpression of the type of the port specified by {@code portName}. If the port's type depends on
   * type parameters which are assigned by this CompTypeExpression, they are resolved in the returned
   * SymTypeExpression. E.g., let assume this component's type expression is {@code Comp<Person>} and Comp is defined by
   * {@code Comp<T>}, having a port of type {@code T}. Then, as the type argument for {@code T} is {@code Person}, the
   * SymTypeExpression returned by this method will be {@code Person} for that port.
   *
   * @param portName The name of the port for whom the type is requested.
   * @return The {@code SymTypeExpressions} of the port's type enclosed in an {@code Optional}. An empty {@code
   * Optional} if the component has no such port.
   */
  public abstract Optional<SymTypeExpression> getTypeOfPort(String portName);

  /**
   * Returns the SymTypeExpression of the type of the parameter specified by {@code parameterName}. If the parameter's
   * type depends on type parameters which are assigned by this CompTypeExpression, they are resolved in the returned
   * SymTypeExpression. E.g., let assume this component's type expression is {@code Comp<Person>} and Comp is defined by
   * {@code Comp<T>}, having a parameter of type {@code T}. Then, as the type argument for {@code T} is {@code Person},
   * the SymTypeExpression returned by this method will be {@code Person} for that parameter.
   *
   * @param parameterName The name of the parameter for whom the type is requested.
   * @return The {@code SymTypeExpressions} of the parameter's type enclosed in an {@code Optional}. An empty {@code
   * Optional} if the component has no such parameter.
   */
  public abstract Optional<SymTypeExpression> getTypeOfParameter(String parameterName);

  public CompKindExpression deepClone() {
    return deepClone(getTypeInfo());
  }

  public abstract CompKindExpression deepClone(ComponentSymbol component);

  public abstract boolean deepEquals(CompKindExpression compSymType);
}

