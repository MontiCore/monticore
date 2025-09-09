/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.types3.ISymTypeVisitor;
import de.se_rwth.commons.logging.Log;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class SymTypeOfTuple extends SymTypeExpression {

  /**
   * List of types within the tuple
   */
  protected List<SymTypeExpression> types;

  public SymTypeOfTuple(List<SymTypeExpression> types) {
    this.types = Log.errorIfNull(types);
  }

  @Override
  public boolean isTupleType() {
    return true;
  }

  @Override
  public SymTypeOfTuple asTupleType() {
    return this;
  }

  @Override
  public SymTypeOfTuple deepClone() {
    return super.deepClone().asTupleType();
  }

  @Override
  public boolean deepEquals(SymTypeExpression sym) {
    if (!sym.isTupleType()) {
      return false;
    }
    SymTypeOfTuple other = (SymTypeOfTuple) sym;
    if (other.sizeTypes() != this.sizeTypes()) {
      return false;
    }
    for (int i = 0; i < this.sizeTypes(); i++) {
      if (!this.getType(i).deepEquals(other.getType(i))) {
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
  // From here on: Standard functionality to access the list of types;
  // (was copied from a created class)
  // (and demonstrates that we still can optimize our generators & build processes)
  // --------------------------------------------------------------------------

  public boolean containsType(Object element) {
    return this.getTypeList().contains(element);
  }

  public boolean containsAllTypes(Collection<?> collection) {
    return this.getTypeList().containsAll(collection);
  }

  public boolean isEmptyTypes() {
    return this.getTypeList().isEmpty();
  }

  public Iterator<SymTypeExpression> iteratorTypes() {
    return this.getTypeList().iterator();
  }

  public int sizeTypes() {
    return this.getTypeList().size();
  }

  public SymTypeExpression[] toArrayTypes(SymTypeExpression[] array) {
    return this.getTypeList().toArray(array);
  }

  public Object[] toArrayTypes() {
    return this.getTypeList().toArray();
  }

  public Spliterator<SymTypeExpression> spliteratorTypes() {
    return this.getTypeList().spliterator();
  }

  public Stream<SymTypeExpression> streamTypes() {
    return this.getTypeList().stream();
  }

  public Stream<SymTypeExpression> parallelStreamTypes() {
    return this.getTypeList().parallelStream();
  }

  public SymTypeExpression getType(int index) {
    return this.getTypeList().get(index);
  }

  public int indexOfType(Object element) {
    return this.getTypeList().indexOf(element);
  }

  public int lastIndexOfType(Object element) {
    return this.getTypeList().lastIndexOf(element);
  }

  public boolean equalsTypeTypes(Object o) {
    return this.getTypeList().equals(o);
  }

  public int hashCodeTypes() {
    return this.getTypeList().hashCode();
  }

  public ListIterator<SymTypeExpression> listIteratorTypes() {
    return this.getTypeList().listIterator();
  }

  public ListIterator<SymTypeExpression> listIteratorTypes(int index) {
    return this.getTypeList().listIterator(index);
  }

  public List<SymTypeExpression> subListTypes(int start, int end) {
    return this.getTypeList().subList(start, end);
  }

  public List<SymTypeExpression> getTypeList() {
    return this.types;
  }

  public void clearTypes() {
    this.getTypeList().clear();
  }

  public boolean addType(SymTypeExpression element) {
    return this.getTypeList().add(element);
  }

  public boolean addAllTypes(Collection<? extends SymTypeExpression> collection) {
    return this.getTypeList().addAll(collection);
  }

  public boolean removeType(Object element) {
    return this.getTypeList().remove(element);
  }

  public boolean removeAllTypes(Collection<?> collection) {
    return this.getTypeList().removeAll(collection);
  }

  public boolean retainAllTypes(Collection<?> collection) {
    return this.getTypeList().retainAll(collection);
  }

  public boolean removeIfType(Predicate<? super SymTypeExpression> filter) {
    return this.getTypeList().removeIf(filter);
  }

  public void forEachTypes(Consumer<? super SymTypeExpression> action) {
    this.getTypeList().forEach(action);
  }

  public void addType(int index, SymTypeExpression element) {
    this.getTypeList().add(index, element);
  }

  public boolean addAllTypes(int index,
      Collection<? extends SymTypeExpression> collection) {
    return this.getTypeList().addAll(index, collection);
  }

  public SymTypeExpression removeType(int index) {
    return this.getTypeList().remove(index);
  }

  public SymTypeExpression setType(int index, SymTypeExpression element) {
    return this.getTypeList().set(index, element);
  }

  public void replaceAllTypes(UnaryOperator<SymTypeExpression> operator) {
    this.getTypeList().replaceAll(operator);
  }

  public void sortTypes(Comparator<? super SymTypeExpression> comparator) {
    this.getTypeList().sort(comparator);
  }

  public void setTypeList(List<SymTypeExpression> types) {
    this.types = types;
  }

}
