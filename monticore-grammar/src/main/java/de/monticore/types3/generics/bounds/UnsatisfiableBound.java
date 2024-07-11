// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.generics.bounds;

import de.monticore.types.check.SymTypeExpression;

import java.util.Collections;
import java.util.List;

public class UnsatisfiableBound extends Bound {

  protected String description;
  protected SymTypeExpression superType;

  public UnsatisfiableBound(String description) {
    this.description = description;
  }

  /**
   * The Description is suitable as part of a Log message.
   */
  public String getDescription() {
    return description;
  }

  @Override
  public boolean isUnsatisfiableBound() {
    return true;
  }

  @Override
  public boolean deepEquals(Bound other) {
    if (this == other) {
      return true;
    }
    if (!other.isUnsatisfiableBound()) {
      return false;
    }
    UnsatisfiableBound otherUnsat = (UnsatisfiableBound) other;
    return getDescription().equals(otherUnsat.getDescription());
  }

  @Override
  public String print() {
    StringBuilder result = new StringBuilder("unsatisfiable");
    // the description can be quite long (multi-line),
    // but is (usually) very helpful;
    // compromise: print the first line
    result.append(" (");
    result.append(description.split(System.lineSeparator())[0]);
    if (description.contains(System.lineSeparator())) {
      result.append(" ...");
    }
    result.append(")");
    return result.toString();
  }

  @Override
  public List<SymTypeExpression> getIncludedTypes() {
    return Collections.emptyList();
  }
}
