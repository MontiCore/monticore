/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symboltable.serialization.JsonDeSers;
import de.monticore.symboltable.serialization.JsonPrinter;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * SymTypeOfFunction stores any kind of Function,
 * such as List<Person>::get, obj::<Integer>getX, i -> i + 2
 */
public class SymTypeOfFunction extends SymTypeExpression {

  protected static final String TYPESYMBOL_NAME = "function";

  protected static final String JSON_KIND = "de.monticore.types.check.SymTypeOfFunction";

  protected static final String JSON_RETURNTYPE = "returnValue";

  protected static final String JSON_ARGUMENTTYPES = "argumentTypes";

  /**
   * Type of return value
   * returned when the function is called
   */
  protected SymTypeExpression returnType;

  /**
   * List of argument types of the function
   * e.g. "Integer f(Float t)" has "Float" as its argument type
   * a this-pointer is the first argument
   */
  protected List<SymTypeExpression> argumentTypes;

  /**
   * Constructor with all parameters that are stored:
   */
  public SymTypeOfFunction(SymTypeExpression returnType) {
    this(returnType, new LinkedList<>());
  }

  public SymTypeOfFunction(SymTypeExpression returnType, List<SymTypeExpression> argumentTypes) {
    super.typeSymbol = new TypeSymbol(TYPESYMBOL_NAME);
    this.returnType = returnType;
    this.argumentTypes = argumentTypes;
  }

  /**
   * print: Umwandlung in einen kompakten String
   */
  @Override
  public String print() {
    final StringBuilder r = new StringBuilder();
    r.append("(");
    for (SymTypeExpression argType : argumentTypes) {
      r.append(argType.print());
      r.append(" -> ");
    }
    r.append(returnType.print());
    r.append(")");
    return r.toString();
  }

  @Override
  public String printFullName() {
    final StringBuffer r = new StringBuffer();
    r.append("(");
    for (SymTypeExpression argType : argumentTypes) {
      r.append(argType.printFullName());
      r.append(" -> ");
    }
    r.append(returnType.printFullName());
    r.append(")");
    return r.toString();
  }

  /**
   * printAsJson: Umwandlung in einen kompakten Json String
   */
  protected String printAsJson() {
    JsonPrinter jp = new JsonPrinter();
    jp.beginObject();
    jp.member(JsonDeSers.KIND, JSON_KIND);
    jp.memberJson(JSON_RETURNTYPE, getType().printAsJson());
    jp.beginArray(JSON_ARGUMENTTYPES);
    for (SymTypeExpression exp : getArgumentTypeList()) {
      jp.valueJson(exp.printAsJson());
    }
    jp.endArray();
    jp.endObject();
    return jp.getContent();
  }

  @Override
  public boolean isFunctionType() {
    return true;
  }

  @Override
  public SymTypeOfFunction deepClone() {
    List<SymTypeExpression> clonedArgTypes = new LinkedList<>();
    for (SymTypeExpression exp : getArgumentTypeList()) {
      clonedArgTypes.add(exp.deepClone());
    }
    return new SymTypeOfFunction(this.returnType.deepClone(), clonedArgTypes);
  }

  @Override
  public boolean deepEquals(SymTypeExpression sym) {
    if (!(sym instanceof SymTypeOfFunction)) {
      return false;
    }
    SymTypeOfFunction symFun = (SymTypeOfFunction) sym;
    if (this.typeSymbol == null || symFun.typeSymbol == null) {
      return false;
    }
    if (!this.typeSymbol.getEnclosingScope().equals(symFun.typeSymbol.getEnclosingScope())) {
      return false;
    }
    if (!this.typeSymbol.getName().equals(symFun.typeSymbol.getName())) {
      return false;
    }
    if (this.sizeArgumentTypes() != symFun.sizeArgumentTypes()) {
      return false;
    }
    for (int i = 0; i < this.sizeArgumentTypes(); i++) {
      if (!this.getArgumentType(i).deepEquals(symFun.getArgumentType(i))) {
        return false;
      }
    }

    return this.print().equals(symFun.print());
  }

  public SymTypeExpression getType() {
    return returnType;
  }

  // --------------------------------------------------------------------------
  // From here on: Standard functionality to access the list of arguments;
  // (was copied from a created class)
  // (and demonstrates that we still can optimize our generators & build processes)
  // --------------------------------------------------------------------------

  public boolean containsArgumentType(Object element) {
    return this.getArgumentTypeList().contains(element);
  }

  public boolean containsAllArgumentTypes(Collection<?> collection) {
    return this.getArgumentTypeList().containsAll(collection);
  }

  public boolean isEmptyArgumentTypes() {
    return this.getArgumentTypeList().isEmpty();
  }

  public Iterator<SymTypeExpression> iteratorArgumentTypes() {
    return this.getArgumentTypeList().iterator();
  }

  public int sizeArgumentTypes() {
    return this.getArgumentTypeList().size();
  }

  public SymTypeExpression[] toArrayArgumentTypes(SymTypeExpression[] array) {
    return this.getArgumentTypeList().toArray(array);
  }

  public Object[] toArrayArgumentTypes() {
    return this.getArgumentTypeList().toArray();
  }

  public Spliterator<SymTypeExpression> spliteratorArgumentTypes() {
    return this.getArgumentTypeList().spliterator();
  }

  public Stream<SymTypeExpression> streamArgumentTypes() {
    return this.getArgumentTypeList().stream();
  }

  public Stream<SymTypeExpression> parallelStreamArgumentTypes() {
    return this.getArgumentTypeList().parallelStream();
  }

  public SymTypeExpression getArgumentType(int index) {
    return this.getArgumentTypeList().get(index);
  }

  public int indexOfArgumentType(Object element) {
    return this.getArgumentTypeList().indexOf(element);
  }

  public int lastIndexOfArgumentType(Object element) {
    return this.getArgumentTypeList().lastIndexOf(element);
  }

  public boolean equalsArgumentTypeTypes(Object o) {
    return this.getArgumentTypeList().equals(o);
  }

  public int hashCodeArgumentTypes() {
    return this.getArgumentTypeList().hashCode();
  }

  public ListIterator<SymTypeExpression> listIteratorArgumentTypes() {
    return this.getArgumentTypeList().listIterator();
  }

  public ListIterator<SymTypeExpression> listIteratorArgumentTypes(int index) {
    return this.getArgumentTypeList().listIterator(index);
  }

  public List<SymTypeExpression> subListArgumentTypes(int start, int end) {
    return this.getArgumentTypeList().subList(start, end);
  }

  public List<SymTypeExpression> getArgumentTypeList() {
    return this.argumentTypes;
  }

  public void clearArgumentTypes() {
    this.getArgumentTypeList().clear();
  }

  public boolean addArgumentType(SymTypeExpression element) {
    return this.getArgumentTypeList().add(element);
  }

  public boolean addAllArgumentTypes(Collection<? extends SymTypeExpression> collection) {
    return this.getArgumentTypeList().addAll(collection);
  }

  public boolean removeArgumentType(Object element) {
    return this.getArgumentTypeList().remove(element);
  }

  public boolean removeAllArgumentTypes(Collection<?> collection) {
    return this.getArgumentTypeList().removeAll(collection);
  }

  public boolean retainAllArgumentTypes(Collection<?> collection) {
    return this.getArgumentTypeList().retainAll(collection);
  }

  public boolean removeIfArgumentType(Predicate<? super SymTypeExpression> filter) {
    return this.getArgumentTypeList().removeIf(filter);
  }

  public void forEachArgumentTypes(Consumer<? super SymTypeExpression> action) {
    this.getArgumentTypeList().forEach(action);
  }

  public void addArgumentType(int index, SymTypeExpression element) {
    this.getArgumentTypeList().add(index, element);
  }

  public boolean addAllArgumentTypes(int index,
      Collection<? extends SymTypeExpression> collection) {
    return this.getArgumentTypeList().addAll(index, collection);
  }

  public SymTypeExpression removeArgumentType(int index) {
    return this.getArgumentTypeList().remove(index);
  }

  public SymTypeExpression setArgumentType(int index, SymTypeExpression element) {
    return this.getArgumentTypeList().set(index, element);
  }

  public void replaceAllArgumentTypes(UnaryOperator<SymTypeExpression> operator) {
    this.getArgumentTypeList().replaceAll(operator);
  }

  public void sortArgumentTypes(Comparator<? super SymTypeExpression> comparator) {
    this.getArgumentTypeList().sort(comparator);
  }

  public void setArgumentTypeList(List<SymTypeExpression> argumentTypes) {
    this.argumentTypes = argumentTypes;
  }

}
