/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.base.Preconditions;
import de.monticore.ast.ASTNode;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._ast.ASTSubcomponentArgument;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Represents all sorts of component kinds. E.g., a {@code ComponentExpression} can represent a generic component with
 * bound type arguments {@code MyComp<Integer>}. This is not representable by Symbols alone, as generic
 * components only have unspecific type parameters ({@code MyComp<T>}.
 */
public abstract class CompKindExpression {

  protected final ComponentTypeSymbol component;
  protected LinkedHashMap<VariableSymbol, ASTSubcomponentArgument> parameterBindings;
  protected List<ASTSubcomponentArgument> arguments;
  protected Optional<ASTNode> sourceNode;

  /**
   * @return a {@code List} of the configuration arguments of this component.
   */
  public List<ASTSubcomponentArgument> getArguments() {
    return this.arguments;
  }

  /**
   * @param argument the configuration argument to add to this component.
   */
  public void addArgument(ASTSubcomponentArgument argument) {
    Preconditions.checkNotNull(argument);
    this.arguments.add(argument);
  }

  /**
   * Am I simple component type? (such as "C")
   * (default: no)
   */
  public boolean isComponentType() {
    return false;
  }

  /**
   * Logs an error if this is not a component type
   * @return this expression as a component type
   */
  public CompKindOfComponentType asComponentType() {
    Log.error("0xFDAB1 internal error: "
      + "tried to convert non-component to a component."
      + " Actual: " + this.printFullName());
    return null;
  }

  /**
   * Am I a generic component type? (such as "C<int>")
   * (default: no)
   */
  public boolean isGenericComponentType() {
    return false;
  }

  /**
   * Logs an error if this is not a generic component type
   * @return this expression as a generic component type
   */
  public CompKindOfGenericComponentType asGenericComponentType() {
    Log.error("0xFDAB2 internal error: "
      + "tried to convert non-generic-component to a generic-component."
      + " Actual: " + this.printFullName());
    return null;
  }

  /**
   * @param arguments the configuration arguments to add to this component.
   * @see this#addArgument(ASTSubcomponentArgument)
   */
  public void addArgument(List<? extends ASTSubcomponentArgument> arguments) {
    Preconditions.checkNotNull(arguments);
    Preconditions.checkArgument(!arguments.contains(null));
    for (ASTSubcomponentArgument argument : arguments) {
      this.addArgument(argument);
    }
  }

  public Optional<ASTSubcomponentArgument> getParamBindingFor(VariableSymbol var) {
    Preconditions.checkNotNull(var);
    return Optional.ofNullable(this.getParamBindings().get(var));
  }

  public Map<VariableSymbol, ASTSubcomponentArgument> getParamBindings() {
    return Collections.unmodifiableMap(this.parameterBindings);
  }

  public List<ASTSubcomponentArgument> getParamBindingsAsList() {
    return new ArrayList<>(this.getParamBindings().values());
  }

  public void bindParams() {
    List<ASTSubcomponentArgument> parameterArguments = this.getArguments();

    int firstKeywordArgument = 0;
    LinkedHashMap<String, ASTSubcomponentArgument> keywordExpressionMap = new LinkedHashMap<>();
    LinkedHashMap<VariableSymbol, ASTSubcomponentArgument> parameterBindings = new LinkedHashMap<>();
    // We know LinkedHashMaps are ordered by insertion time. As we rely on the fact that the ordering of the
    // arguments is consistent with the ordering in the map, the following iteration ensures it:
    for (int i = 0; i < this.getTypeInfo().getParameterList().size(); i++) {
      if (i < parameterArguments.size()) // Deal with wrong number of parameters through cocos
        if (!parameterArguments.get(i).isPresentName()) {
          parameterBindings.put(this.getTypeInfo().getParameterList().get(i), parameterArguments.get(i));
          firstKeywordArgument++;
        } else {
          keywordExpressionMap.put(parameterArguments.get(i).getName(), parameterArguments.get(i));
        }
    }

    // iterate over keyword-based arguments (CoCo assures that no position-based argument occurs
    // after the first keyword-based argument)
    for (int j = firstKeywordArgument; j < this.getTypeInfo().getParameterList().size(); j++) {
      if (keywordExpressionMap.containsKey(this.getTypeInfo().getParameterList().get(j).getName()) &&
        !parameterBindings.containsKey(this.getTypeInfo().getParameterList().get(j))) {
        parameterBindings.put(this.getTypeInfo().getParameterList().get(j),
          keywordExpressionMap.get(this.getTypeInfo().getParameterList().get(j).getName()));
      }
    }

    this.parameterBindings = parameterBindings;
  }

  /**
   * The ast node on which this CompKindExpression is based, if present.
   * <p>
   * This is ONLY meant to be used to create better log messages!
   * As CompKindExpressions are moved around, cloned, and modified, it cannot be
   * assumed that the reference to the AST node holds reliable.
   */
  public Optional<ASTNode> getSourceNode() {
    return this.sourceNode;
  }

  /**
   * @param sourceNode Must not be null
   * @see CompKindExpression#getSourceNode
   */
  public void setSourceNode(ASTNode sourceNode) {
    Preconditions.checkNotNull(sourceNode);
    this.sourceNode = Optional.of(sourceNode);
  }

  /** @see CompKindExpression#getSourceNode */
  public void setSourceNodeAbsent() {
    this.sourceNode = Optional.empty();
  }

  protected CompKindExpression(ComponentTypeSymbol component) {
    Preconditions.checkNotNull(component);
    this.component = component;
    this.arguments = new ArrayList<>();
    this.parameterBindings = new LinkedHashMap<>();
    this.sourceNode = Optional.empty();
  }

  public ComponentTypeSymbol getTypeInfo() {
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

  public abstract List<Optional<SymTypeExpression>> getParameterTypes();

  public CompKindExpression deepClone() {
    return deepClone(getTypeInfo());
  }

  public abstract CompKindExpression deepClone(ComponentTypeSymbol component);

  public abstract boolean deepEquals(CompKindExpression compSymType);
}

