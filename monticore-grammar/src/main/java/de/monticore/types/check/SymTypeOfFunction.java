/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.FunctionSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.types3.ISymTypeVisitor;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.generics.TypeParameterRelations;
import de.monticore.types3.generics.bounds.Bound;
import de.monticore.types3.generics.util.BoundResolution;
import de.monticore.types3.util.SymTypeExpressionComparator;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.Spliterator;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
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
      List<? extends SymTypeExpression> argumentTypes,
      boolean elliptic) {
    super.typeSymbol = new TypeSymbol(TYPESYMBOL_NAME);
    super.typeSymbol.setEnclosingScope(BasicSymbolsMill.scope());
    super.typeSymbol.setSpannedScope(BasicSymbolsMill.scope());
    this.functionSymbol = Optional.ofNullable(functionSymbol);
    this.returnType = returnType;
    this.argumentTypes = new ArrayList<>(argumentTypes);
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

  @Override
  public boolean isFunctionType() {
    return true;
  }

  @Override
  public SymTypeOfFunction asFunctionType() {
    return this;
  }

  @Override
  public SymTypeOfFunction deepClone() {
    return (SymTypeOfFunction) super.deepClone();
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
   *     NOT the actual type of the function itself
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
  public boolean canHaveArity(int arity) {
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
      if (isElliptic()) {
        SymTypeExpression ellipticArgument = getArgumentType(sizeArgumentTypes() - 1);
        clone.removeArgumentType(sizeArgumentTypes() - 1);
        for (int i = sizeArgumentTypes() - 1; i < arity; i++) {
          clone.addArgumentType(ellipticArgument.deepClone());
        }
        clone.setElliptic(false);
      }
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

  /**
   * returns the declared type
   */
  public SymTypeOfFunction getDeclaredType() {
    return getSymbol().getFunctionType();
  }

  protected Map<SymTypeVariable, SymTypeExpression> getTypeVariableReplaceMap() {

    Map<SymTypeVariable, SymTypeExpression> replaceMap =
        new TreeMap<>(new SymTypeExpressionComparator());
    if (hasSymbol()) {
      // skolem variables:
      List<SymTypeVariable> infVars = TypeParameterRelations.getIncludedInferenceVariables(this);
      Map<SymTypeVariable, SymTypeExpression> infVar2Skolem =
          new TreeMap<>(new SymTypeExpressionComparator());
      for (SymTypeVariable infVar : infVars) {
        infVar2Skolem.put(infVar,
            SymTypeExpressionFactory.createTypeObject(
                "Skolem#" + infVar.getFreeVarIdentifier(),
                BasicSymbolsMill.scope()
            )
        );
      }
      Map<SymTypeExpression, SymTypeVariable> skolem2infVar =
          new TreeMap<>(new SymTypeExpressionComparator());
      infVar2Skolem.forEach((k, v) -> skolem2infVar.put(v, k));
      SymTypeOfFunction thisWithSkolems = TypeParameterRelations.replaceTypeVariables(this, infVar2Skolem).asFunctionType();

      SymTypeOfFunction declType = isElliptic() ?
          getDeclaredType() :
          getDeclaredType().getWithFixedArity(sizeArgumentTypes());
      Map<SymTypeVariable, SymTypeVariable> typePar2FreeVar
          = TypeParameterRelations.getFreeVariableReplaceMap(
          declType, BasicSymbolsMill.scope()
      );
      Map<SymTypeVariable, SymTypeVariable> freeVar2TypePar =
          new TreeMap<>(new SymTypeExpressionComparator());
      typePar2FreeVar.forEach((k, v) -> freeVar2TypePar.put(v, k));
      SymTypeOfFunction declTypeWithFreeVars = TypeParameterRelations
          .replaceTypeVariables(declType, typePar2FreeVar)
          .asFunctionType();
      List<Bound> boundsOnDeclType = SymTypeRelations.constrainSameType(
          declTypeWithFreeVars, thisWithSkolems
      );
      Optional<Map<SymTypeVariable, SymTypeExpression>> freeVar2InstTypeOpt =
          BoundResolution.resolve(boundsOnDeclType);
      if (freeVar2InstTypeOpt.isPresent()) {
        Map<SymTypeVariable, SymTypeExpression> freeVar2InstType =
            freeVar2InstTypeOpt.get();
        for (SymTypeVariable freeVar : typePar2FreeVar.values()) {
          SymTypeExpression calculatedReplacement = freeVar2InstType.get(freeVar);
          SymTypeExpression replacement =
              skolem2infVar.containsKey(calculatedReplacement) ?
                  skolem2infVar.get(calculatedReplacement) :
                  calculatedReplacement;
          replaceMap.put(freeVar2TypePar.get(freeVar), replacement);
        }
        for (TypeVarSymbol varSym : getSymbol().getTypeVariableList()) {
          SymTypeVariable var = SymTypeExpressionFactory
              .createTypeVariable(varSym);
          if (!replaceMap.containsKey(var)) {
            replaceMap.put(var, var);
          }
        }
      }
      else {
        Log.error("0xFD235 internal error: could not get type arguments"
            + " of function with type " + this.printFullName()
            + " with declared type " + declType.printFullName()
        );
      }
    }
    return replaceMap;
  }

  /**
   * returns the type arguments for a generic function.
   * E.g., given asList, which has the declared Type <T> (T...) -> List<T>
   * and this is the instantiation (int, int) -> List<int>,
   * This will return the argument list [int].
   * <p>
   * Warning: if the instantiation is, e.g., (Person, Car) -> List<int>,
   * no correct list of arguments can be calculated.
   * <p>
   * Not to be confused with getArgumentTypes,
   * which in turn returns the parameter types
   * (a.k.a. the types the arguments need to be compatible to).
   * Naming is confusing due to legacy reasons.
   */
  public List<SymTypeExpression> getTypeArguments() {
    if (!hasSymbol()) {
      return Collections.emptyList();
    }
    // Heuristic: If a method is generic,
    // its type parameters can be found in its signature.
    // This cannot find cases like (<T> () -> String),
    // there the parameter is used solely inside the implementation.
    // It is debatable, how useful that parameter even is...
    // Another assumption: The type variables have no name.
    Map<SymTypeVariable, SymTypeExpression> paramReplaceMap =
        getTypeVariableReplaceMap();
    List<SymTypeExpression> typeArguments = getSymbol().getTypeVariableList()
        .stream()
        .map(SymTypeExpressionFactory::createTypeVariable)
        .map(paramReplaceMap::get)
        .collect(Collectors.toList());
    return typeArguments;
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
