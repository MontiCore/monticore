/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
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

  public static final String TYPESYMBOL_NAME = "function";

  protected static final String JSON_KIND = "de.monticore.types.check.SymTypeOfFunction";

  protected static final String JSON_RETURNTYPE = "returnType";

  protected static final String JSON_ARGUMENTTYPES = "argumentTypes";

  protected static final String JSON_ELLIPTIC = "elliptic";

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
   * Whether the function supports varargs
   * e.g. {@code Integer f(Float... t)}
   */
  protected boolean elliptic;

  /**
   * Constructor with all parameters that are stored:
   */
  public SymTypeOfFunction(SymTypeExpression returnType) {
    this(returnType, new LinkedList<>());
  }

  public SymTypeOfFunction(SymTypeExpression returnType, List<SymTypeExpression> argumentTypes) {
    this(returnType, argumentTypes, false);
  }

  public SymTypeOfFunction(SymTypeExpression returnType, List<SymTypeExpression> argumentTypes,
      boolean elliptic) {
    super.typeSymbol = new TypeSymbol(TYPESYMBOL_NAME);
    super.typeSymbol.setEnclosingScope(BasicSymbolsMill.scope());
    super.typeSymbol.setSpannedScope(BasicSymbolsMill.scope());
    this.returnType = returnType;
    this.argumentTypes = argumentTypes;
    this.elliptic = elliptic;
  }

  /**
   * print: Umwandlung in einen kompakten String
   */
  @Override
  public String print() {
    final StringBuilder r = new StringBuilder();
    r.append("(");
    for (int i = 0; i < argumentTypes.size(); i++) {
      r.append(argumentTypes.get(i).print());
      if(i < argumentTypes.size() - 1){
        r.append(", ");
      } else if (isElliptic()) {
        r.append("...");
      }
    }
    r.append(")");
    r.append(" -> ");
    r.append(returnType.print());
    return r.toString();
  }

  @Override
  public String printFullName() {
    final StringBuilder r = new StringBuilder();
    r.append("(");
    for (int i = 0; i < argumentTypes.size(); i++) {
      r.append(argumentTypes.get(i).printFullName());
      if(i < argumentTypes.size() - 1){
        r.append(", ");
      } else if (isElliptic()) {
        r.append("...");
      }
    }
    r.append(")");
    r.append(" -> ");
    r.append(returnType.printFullName());
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
    jp.member(JSON_ELLIPTIC, isElliptic());
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

  public boolean isElliptic() {
    return elliptic;
  }

  public void setElliptic(boolean elliptic) {
    this.elliptic = elliptic;
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
    if(this.isElliptic() && index >= getArgumentTypeList().size()) {
      return this.getArgumentTypeList().get(getArgumentTypeList().size()-1);
    }
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
