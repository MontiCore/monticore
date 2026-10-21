/* (c) https://github.com/MontiCore/monticore */
package de.monticore.rete;

import de.monticore.ast.ASTNode;
import de.monticore.rete.nodes.ProductionNode;
import de.monticore.rete.tuple.Tuple;

import java.util.List;

/**
 * A match of a pattern, i.e. a binding of the pattern parameters (provided by
 * a {@link ProductionNode}) to AST nodes. Matches are immutable value objects.
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

  public ASTNode get(int index) {
    return (ASTNode) tuple.get(index);
  }

  /**
   * @return the AST node bound to the parameter at the given position, cast
   * to the given type
   * @throws ClassCastException if the node is not an instance of {@code type}
   */
  public <T extends ASTNode> T get(int index, Class<T> type) {
    return type.cast(tuple.get(index));
  }

  /**
   * @return the AST node bound to the parameter of the given name
   * @throws IllegalArgumentException if there is no parameter of that name
   */
  public ASTNode get(String parameterName) {
    return get(production.getParameterIndex(parameterName));
  }

  /**
   * @return the AST node bound to the parameter of the given name, cast to
   * the given type
   * @throws IllegalArgumentException if there is no parameter of that name
   * @throws ClassCastException       if the node is not an instance of {@code type}
   */
  public <T extends ASTNode> T get(String parameterName, Class<T> type) {
    return get(production.getParameterIndex(parameterName), type);
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

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("{");
    List<String> names = getParameterNames();
    for (int i = 0; i < names.size(); i++) {
      if (i > 0) {
        sb.append(", ");
      }
      sb.append(names.get(i)).append('=').append(tuple.get(i));
    }
    return sb.append('}').toString();
  }
}
