/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.types3.ISymTypeVisitor;
import de.se_rwth.commons.logging.Log;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
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

  /**
   * @deprecated only required for the deprecated type symbol
   */
  @Deprecated
  public static final String TYPESYMBOL_NAME = "function";

  /**
   * Symbol of the function
   * may not be present (e.g., for lambdas)
   */
  protected Optional<FunctionSymbol> functionSymbol;

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
   * FunctionSymbol can be null
   */
  public SymTypeOfFunction(
      FunctionSymbol functionSymbol,
      SymTypeExpression returnType,
      List<SymTypeExpression> argumentTypes,
      boolean elliptic) {
    super.typeSymbol = new TypeSymbol(TYPESYMBOL_NAME);
    super.typeSymbol.setEnclosingScope(BasicSymbolsMill.scope());
    super.typeSymbol.setSpannedScope(BasicSymbolsMill.scope());
    this.functionSymbol = Optional.ofNullable(functionSymbol);
    this.returnType = returnType;
    this.argumentTypes = argumentTypes;
    this.elliptic = elliptic;
  }

  /**
   * @deprecated the other constructor is to be used
   */
  @Deprecated
  public SymTypeOfFunction(SymTypeExpression returnType, List<SymTypeExpression> argumentTypes,
      boolean elliptic) {
    this(null, returnType, argumentTypes, elliptic);
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
      if (i < argumentTypes.size() - 1) {
        r.append(", ");
      }
      else if (isElliptic()) {
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
      if (i < argumentTypes.size() - 1) {
        r.append(", ");
      }
      else if (isElliptic()) {
        r.append("...");
      }
    }
    r.append(")");
    r.append(" -> ");
    r.append(returnType.printFullName());
    return r.toString();
  }

  @Override
  public boolean isFunctionType() {
    return true;
  }

  @Override
  public SymTypeOfFunction asFunctionType() {
    return this;
  }

  @Override
  public boolean deepEquals(SymTypeExpression sym) {
    if (!deepEqualsSignature(sym)) {
      return false;
    }
    SymTypeOfFunction symFun = (SymTypeOfFunction) sym;
    if (!getType().deepEquals(symFun.getType())) {
      return false;
    }
    return true;
  }

  /**
   * deepEquals, but ignoring the return type
   * s.a. Java Spec 20 8.4.2
   */
  public boolean deepEqualsSignature(SymTypeExpression other) {
    if (!other.isFunctionType()) {
      return false;
    }
    SymTypeOfFunction symFun = (SymTypeOfFunction) other;
    if (this.sizeArgumentTypes() != symFun.sizeArgumentTypes()) {
      return false;
    }
    for (int i = 0; i < this.sizeArgumentTypes(); i++) {
      if (!this.getArgumentType(i).deepEquals(symFun.getArgumentType(i))) {
        return false;
      }
    }
    if (isElliptic() != symFun.isElliptic()) {
      return false;
    }
    return true;
  }

  /**
   * @return the return type of the function
   * NOT the actual type of the function itself
   */
  public SymTypeExpression getType() {
    return returnType;
  }

  /**
   * iff true, the last argument type is accepted any amount of times
   */
  public boolean isElliptic() {
    return elliptic;
  }

  public void setElliptic(boolean elliptic) {
    this.elliptic = elliptic;
  }

  public boolean hasSymbol() {
    return functionSymbol.isPresent();
  }

  public FunctionSymbol getSymbol() {
    if (!hasSymbol()) {
      Log.error("0xFD712 internal error: "
          + "tried to get non existing function symbol"
      );
    }
    return functionSymbol.get();
  }

  /**
   * returns whether the specified amount of arguments can be accepted
   * E.g., (P, int...) -> int can accept 1,2,3,... arguments, but not 0
   */
  protected boolean canHaveArity(int arity) {
    return ((isElliptic() && sizeArgumentTypes() - 1 <= arity)
        || sizeArgumentTypes() == arity);
  }

  /**
   * returns a clone, with the arity fixed to the specified number
   * E.g., (P, int...) -> int with arity of 3 is (P, int, int) -> int
   */
  public SymTypeOfFunction getWithFixedArity(int arity) {
    SymTypeOfFunction clone = (SymTypeOfFunction) deepClone();
    if (canHaveArity(arity)) {
      for (int i = sizeArgumentTypes(); i < arity; i++) {
        clone.addArgumentType(
            getArgumentType(sizeArgumentTypes() - 1).deepClone()
        );
      }
      clone.setElliptic(false);
    }
    else {
      Log.error("0xFD2A1 internal error: "
          + "the arity of a function of type "
          + clone.printFullName()
          + " cannot be fixed to " + arity
      );
    }
    return clone;
  }

  public Map<TypeVarSymbol, SymTypeExpression> getTypeVariableReplaceMap() {
    Map<TypeVarSymbol, SymTypeExpression> replaceMap = new HashMap<>();
    if (hasSymbol()) {
      Map<SymTypeExpression, SymTypeExpression> symbol2instance
          = new HashMap<>();
      symbol2instance.put(getSymbol().getType(), getType());
      for (int i = 0; i < getSymbol().getFunctionType().sizeArgumentTypes()
          && i < sizeArgumentTypes(); i++) {
        symbol2instance.put(
            getSymbol().getFunctionType().getArgumentType(i),
            getArgumentType(i)
        );
      }
      for (SymTypeExpression symbolExpr : symbol2instance.keySet()) {
        if (symbolExpr.isTypeVariable()
            && !symbolExpr.deepEquals(symbol2instance.get(symbolExpr))
        ) {
          replaceMap.put(
              ((SymTypeVariable) symbolExpr).getTypeVarSymbol(),
              symbol2instance.get(symbolExpr)
          );
        }
      }
    }
    return replaceMap;
  }

  @Override
  public void accept(ISymTypeVisitor visitor) {
    visitor.visit(this);
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
    if (this.isElliptic() && index >= getArgumentTypeList().size()) {
      return this.getArgumentTypeList().get(getArgumentTypeList().size() - 1);
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
