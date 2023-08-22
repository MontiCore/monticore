/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.types3.ISymTypeVisitor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SymTypeOfIntersection extends SymTypeExpression {

  /**
   * Set of types within the intersection
   */
  protected Set<SymTypeExpression> intersectedTypes;

  public SymTypeOfIntersection(Collection<? extends SymTypeExpression> types) {
    this.intersectedTypes = new HashSet<>();
    this.intersectedTypes.addAll(types);
  }

  @Override
  public boolean isValidType() {
    return streamIntersectedTypes().allMatch(SymTypeExpression::isValidType);
  }

  public boolean isIntersectionType() {
    return true;
  }

  @Override
  public String print() {
    final StringBuilder r = new StringBuilder();
    r.append("(");
    r.append(getIntersectedTypeSet().stream()
        .map(SymTypeExpression::print)
        // sorted to be deterministic
        .sorted()
        .collect(Collectors.joining(" & "))
    );
    r.append(")");
    return r.toString();
  }

  @Override
  public String printFullName() {
    final StringBuilder r = new StringBuilder();
    r.append("(");
    r.append(getIntersectedTypeSet().stream()
        .map(SymTypeExpression::printFullName)
        // sorted to be deterministic
        .sorted()
        .collect(Collectors.joining(" & "))
    );
    r.append(")");
    return r.toString();
  }

  @Override
  public boolean deepEquals(SymTypeExpression sym) {
    if (!sym.isIntersectionType()) {
      return false;
    }
    SymTypeOfIntersection other = (SymTypeOfIntersection) sym;
    if (other.sizeIntersectedTypes() != this.sizeIntersectedTypes()) {
      return false;
    }
    for (SymTypeExpression ownExpr : this.getIntersectedTypeSet()) {
      if (!other.parallelStreamIntersectedTypes().anyMatch(ownExpr::deepEquals)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public void accept(ISymTypeVisitor visitor) {
    visitor.visit(this);
  }

  // --------------------------------------------------------------------------
  // From here on: Functionality to access the set of types;
  // --------------------------------------------------------------------------

  public boolean containsIntersectedType(Object element) {
    return this.getIntersectedTypeSet().contains(element);
  }

  public boolean containsAllIntersectedTypes(Collection<?> collection) {
    return this.getIntersectedTypeSet().containsAll(collection);
  }

  public boolean isEmptyIntersectedTypes() {
    return this.getIntersectedTypeSet().isEmpty();
  }

  public Iterator<SymTypeExpression> iteratorIntersectedTypes() {
    return this.getIntersectedTypeSet().iterator();
  }

  public int sizeIntersectedTypes() {
    return this.getIntersectedTypeSet().size();
  }

  public SymTypeExpression[] toArrayIntersectedTypes(
      SymTypeExpression[] array) {
    return this.getIntersectedTypeSet().toArray(array);
  }

  public Object[] toArrayIntersectedTypes() {
    return this.getIntersectedTypeSet().toArray();
  }

  public Spliterator<SymTypeExpression> spliteratorIntersectedTypes() {
    return this.getIntersectedTypeSet().spliterator();
  }

  public Stream<SymTypeExpression> streamIntersectedTypes() {
    return this.getIntersectedTypeSet().stream();
  }

  public Stream<SymTypeExpression> parallelStreamIntersectedTypes() {
    return this.getIntersectedTypeSet().parallelStream();
  }

  public boolean equalsIntersectedTypeTypes(Object o) {
    return this.getIntersectedTypeSet().equals(o);
  }

  public int hashCodeIntersectedTypes() {
    return this.getIntersectedTypeSet().hashCode();
  }

  public Set<SymTypeExpression> getIntersectedTypeSet() {
    return this.intersectedTypes;
  }

  public void clearIntersectedTypes() {
    this.getIntersectedTypeSet().clear();
  }

  public boolean addIntersectedType(SymTypeExpression element) {
    return this.getIntersectedTypeSet().add(element);
  }

  public boolean addAllIntersectedTypes(Collection<? extends SymTypeExpression> collection) {
    return this.getIntersectedTypeSet().addAll(collection);
  }

  public boolean removeIntersectedType(Object element) {
    return this.getIntersectedTypeSet().remove(element);
  }

  public boolean removeAllIntersectedTypes(Collection<?> collection) {
    return this.getIntersectedTypeSet().removeAll(collection);
  }

  public boolean retainAllIntersectedTypes(Collection<?> collection) {
    return this.getIntersectedTypeSet().retainAll(collection);
  }

  public boolean removeIfIntersectedType(Predicate<? super SymTypeExpression> filter) {
    return this.getIntersectedTypeSet().removeIf(filter);
  }

  public void forEachIntersectedTypes(Consumer<? super SymTypeExpression> action) {
    this.getIntersectedTypeSet().forEach(action);
  }

  public void setIntersectedTypeSet(Set<SymTypeExpression> intersectedTypes) {
    this.intersectedTypes = intersectedTypes;
  }

}
