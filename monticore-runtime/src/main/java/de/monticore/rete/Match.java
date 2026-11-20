/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete;

import de.monticore.ast.ASTNode;
import de.monticore.rete.nodes.ProductionNode;
import de.monticore.rete.tuple.Tuple;

import java.util.List;
import java.util.Optional;

/**
 * A match of a pattern, i.e. a binding of the pattern parameters (provided by
 * a {@link ProductionNode}) to AST nodes. Matches are immutable value objects.
 * <p>
 * {@linkplain ProductionNode#isOptionalParameter Optional parameters} are
 * absent if the optional pattern element was not matched: then
 * {@link #isPresent} is {@code false}, {@code get} returns {@code null} and
 * {@code find} an empty {@link Optional}.
 */
public final class Match {

  private final ProductionNode production;

  private final Tuple tuple;

  public Match(ProductionNode production, Tuple tuple) {
    this.production = production;
    this.tuple = tuple;
  }

  public ProductionNode getProduction() {
    return production;
  }

  public List<String> getParameterNames() {
    return production.getParameterNames();
  }

  public int size() {
    return tuple.size();
  }

  /**
   * @return the AST node bound to the parameter at the given position, or
   * {@code null} if the parameter is absent
   */
  public ASTNode get(int index) {
    return (ASTNode) tuple.get(index);
  }

  /**
   * @return the AST node bound to the parameter at the given position, cast
   * to the given type, or {@code null} if the parameter is absent
   * @throws ClassCastException if the node is not an instance of {@code type}
   */
  public <T extends ASTNode> T get(int index, Class<T> type) {
    return type.cast(tuple.get(index));
  }

  /**
   * @return the AST node bound to the parameter of the given name, or
   * {@code null} if the parameter is absent
   * @throws IllegalArgumentException if there is no parameter of that name
   */
  public ASTNode get(String parameterName) {
    return get(production.getParameterIndex(parameterName));
  }

  /**
   * @return the AST node bound to the parameter of the given name, cast to
   * the given type, or {@code null} if the parameter is absent
   * @throws IllegalArgumentException if there is no parameter of that name
   * @throws ClassCastException       if the node is not an instance of {@code type}
   */
  public <T extends ASTNode> T get(String parameterName, Class<T> type) {
    return get(production.getParameterIndex(parameterName), type);
  }

  /**
   * @return whether the parameter at the given position is bound to an AST
   * node; always {@code true} for parameters that are not optional
   */
  public boolean isPresent(int index) {
    return !tuple.isAbsent(index);
  }

  /**
   * @return whether the parameter of the given name is bound to an AST node;
   * always {@code true} for parameters that are not optional
   * @throws IllegalArgumentException if there is no parameter of that name
   */
  public boolean isPresent(String parameterName) {
    return isPresent(production.getParameterIndex(parameterName));
  }

  /**
   * @return the AST node bound to the parameter at the given position, or an
   * empty optional if the parameter is absent
   */
  public Optional<ASTNode> find(int index) {
    return Optional.ofNullable(get(index));
  }

  /**
   * @return the AST node bound to the parameter at the given position, cast
   * to the given type, or an empty optional if the parameter is absent
   * @throws ClassCastException if the node is not an instance of {@code type}
   */
  public <T extends ASTNode> Optional<T> find(int index, Class<T> type) {
    return Optional.ofNullable(get(index, type));
  }

  /**
   * @return the AST node bound to the parameter of the given name, or an
   * empty optional if the parameter is absent
   * @throws IllegalArgumentException if there is no parameter of that name
   */
  public Optional<ASTNode> find(String parameterName) {
    return find(production.getParameterIndex(parameterName));
  }

  /**
   * @return the AST node bound to the parameter of the given name, cast to
   * the given type, or an empty optional if the parameter is absent
   * @throws IllegalArgumentException if there is no parameter of that name
   * @throws ClassCastException       if the node is not an instance of {@code type}
   */
  public <T extends ASTNode> Optional<T> find(String parameterName, Class<T> type) {
    return find(production.getParameterIndex(parameterName), type);
  }

  public Tuple toTuple() {
    return tuple;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Match other)) {
      return false;
    }
    return production == other.production && tuple.equals(other.tuple);
  }

  @Override
  public int hashCode() {
    return tuple.hashCode();
  }

  /**
   * @return the bindings of the parameters, e.g. {@code {state=s1, tr=⊥}},
   * where {@code ⊥} marks absent parameters
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("{");
    List<String> names = getParameterNames();
    for (int i = 0; i < names.size(); i++) {
      if (i > 0) {
        sb.append(", ");
      }
      sb.append(names.get(i)).append('=').append(isPresent(i) ? tuple.get(i) : Tuple.ABSENT);
    }
    return sb.append('}').toString();
  }
}
