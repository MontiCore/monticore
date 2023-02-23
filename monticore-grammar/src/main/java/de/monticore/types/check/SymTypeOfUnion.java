/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SymTypeOfUnion extends SymTypeExpression {

  public static final String DEFAULT_TYPESYMBOL_NAME = "union";

  /**
   * Set of types within the union
   */
  protected Set<SymTypeExpression> unionizedTypes;

  public SymTypeOfUnion(Set<SymTypeExpression> types) {
    super.typeSymbol = new TypeSymbol(DEFAULT_TYPESYMBOL_NAME);
    super.typeSymbol.setEnclosingScope(BasicSymbolsMill.globalScope());
    super.typeSymbol.setSpannedScope(BasicSymbolsMill.scope());
    this.unionizedTypes = types;
  }

  @Override
  public boolean isValidType() {
    return true;
  }

  @Override
  public boolean isUnionType() {
    return true;
  }

  @Override
  public String print() {
    final StringBuilder r = new StringBuilder();
    r.append("(");
    r.append(getUnionizedTypeSet().stream()
        .map(SymTypeExpression::print)
        // sorted to be deterministic
        .sorted()
        .collect(Collectors.joining(" | "))
    );
    r.append(")");
    return r.toString();
  }

  @Override
  public String printFullName() {
    final StringBuilder r = new StringBuilder();
    r.append("(");
    r.append(getUnionizedTypeSet().stream()
        .map(SymTypeExpression::printFullName)
        // sorted to be deterministic
        .sorted()
        .collect(Collectors.joining(" | "))
    );
    r.append(")");
    return r.toString();
  }

  @Override
  public SymTypeOfUnion deepClone() {
    Set<SymTypeExpression> clonedUnionizedTypes = new HashSet<>();
    for (SymTypeExpression exp : getUnionizedTypeSet()) {
      clonedUnionizedTypes.add(exp.deepClone());
    }
    return SymTypeExpressionFactory.createUnion(clonedUnionizedTypes);
  }

  @Override
  public boolean deepEquals(SymTypeExpression sym) {
    if (!sym.isUnionType()) {
      return false;
    }
    SymTypeOfUnion other = (SymTypeOfUnion) sym;
    if (other.sizeUnionizedTypes() != this.sizeUnionizedTypes()) {
      return false;
    }
    for (SymTypeExpression ownExpr : this.getUnionizedTypeSet()) {
      if (!other.parallelStreamUnionizedTypes().anyMatch(ownExpr::deepEquals)) {
        return false;
      }
    }
    return true;
  }

  // --------------------------------------------------------------------------
  // From here on: Functionality to access the set of types;
  // --------------------------------------------------------------------------

  public boolean containsUnionizedType(Object element) {
    return this.getUnionizedTypeSet().contains(element);
  }

  public boolean containsAllUnionizedTypes(Collection<?> collection) {
    return this.getUnionizedTypeSet().containsAll(collection);
  }

  public boolean isEmptyUnionizedTypes() {
    return this.getUnionizedTypeSet().isEmpty();
  }

  public Iterator<SymTypeExpression> iteratorUnionizedTypes() {
    return this.getUnionizedTypeSet().iterator();
  }

  public int sizeUnionizedTypes() {
    return this.getUnionizedTypeSet().size();
  }

  public SymTypeExpression[] toArrayUnionizedTypes(
      SymTypeExpression[] array) {
    return this.getUnionizedTypeSet().toArray(array);
  }

  public Object[] toArrayUnionizedTypes() {
    return this.getUnionizedTypeSet().toArray();
  }

  public Spliterator<SymTypeExpression> spliteratorUnionizedTypes() {
    return this.getUnionizedTypeSet().spliterator();
  }

  public Stream<SymTypeExpression> streamUnionizedTypes() {
    return this.getUnionizedTypeSet().stream();
  }

  public Stream<SymTypeExpression> parallelStreamUnionizedTypes() {
    return this.getUnionizedTypeSet().parallelStream();
  }

  public boolean equalsUnionizedTypeTypes(Object o) {
    return this.getUnionizedTypeSet().equals(o);
  }

  public int hashCodeUnionizedTypes() {
    return this.getUnionizedTypeSet().hashCode();
  }

  public Set<SymTypeExpression> getUnionizedTypeSet() {
    return this.unionizedTypes;
  }

  public void clearUnionizedTypes() {
    this.getUnionizedTypeSet().clear();
  }

  public boolean addUnionizedType(SymTypeExpression element) {
    return this.getUnionizedTypeSet().add(element);
  }

  public boolean addAllUnionizedTypes(Collection<? extends SymTypeExpression> collection) {
    return this.getUnionizedTypeSet().addAll(collection);
  }

  public boolean removeUnionizedType(Object element) {
    return this.getUnionizedTypeSet().remove(element);
  }

  public boolean removeAllUnionizedTypes(Collection<?> collection) {
    return this.getUnionizedTypeSet().removeAll(collection);
  }

  public boolean retainAllUnionizedTypes(Collection<?> collection) {
    return this.getUnionizedTypeSet().retainAll(collection);
  }

  public boolean removeIfUnionizedType(Predicate<? super SymTypeExpression> filter) {
    return this.getUnionizedTypeSet().removeIf(filter);
  }

  public void forEachUnionizedTypes(Consumer<? super SymTypeExpression> action) {
    this.getUnionizedTypeSet().forEach(action);
  }

  public void setUnionizedTypeSet(Set<SymTypeExpression> unionizedTypes) {
    this.unionizedTypes = unionizedTypes;
  }

}
