/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbolSurrogate;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.types3.ISymTypeVisitor;
import de.se_rwth.commons.logging.Log;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * SymTypeOfGenerics stores any kind of TypeConstructor applied
 * to Arguments, such as Map< int,Person >
 * List<Person>, List< Set< List< a >>>.
 * This subsumes all kinds of generic Types from several of the
 * MC-Type grammars.
 */
public class SymTypeOfGenerics extends SymTypeExpression {

  /**
   * Map for unboxing generic types (e.g. "java.util.Collection" -> "Collection")
   */
  @Deprecated
  public static final Map<String, String> unboxMap;

  /**
   * Map for boxing generic types (e.g. "Collection" -> "java.util.Collection")
   * Results are fully qualified.
   */
  @Deprecated
  public static final Map<String, String> boxMap;

  /**
   * initializing the maps
   */
  static {
    Map<String, String> unboxMap_temp = new HashMap<String, String>();
    unboxMap_temp.put("java.util.Optional", "Optional");
    unboxMap_temp.put("java.util.Set", "Set");
    unboxMap_temp.put("java.util.List", "List");
    unboxMap_temp.put("java.util.Map","Map");
    unboxMap = Collections.unmodifiableMap(unboxMap_temp);

    Map<String, String> boxMap_temp = new HashMap<String, String>();
    boxMap_temp.put("Optional", "java.util.Optional");
    boxMap_temp.put("Set", "java.util.Set");
    boxMap_temp.put("List", "java.util.List");
    boxMap_temp.put("Map","java.util.Map");
    boxMap = Collections.unmodifiableMap(boxMap_temp);
  }

  /**
   * unboxing generic types (e.g. "java.util.Collection" -> "Collection").
   * otherwise return is unchanged
   * @deprecated use SymTypeUnboxingVisitor
   * @param type
   * @return
   */
  @Deprecated
  public static String unbox(SymTypeOfGenerics type){
    List<SymTypeExpression> arguments = type.getArgumentList();
    StringBuilder r = new StringBuilder().append('<');
    for (int i = 0; i < arguments.size(); i++) {
      if (arguments.get(i).isGenericType()) {
        r.append(unbox((SymTypeOfGenerics) arguments.get(i)));
      }
      else {
        r.append(SymTypePrimitive.unbox(arguments.get(i).print()));
      }
      if (i < arguments.size() - 1) {
        r.append(',');
      }
    }
    r.append(">");
    if (unboxMap.containsKey(type.printTypeWithoutTypeArgument())) {
      return unboxMap.get(type.printTypeWithoutTypeArgument()) + r.toString();
    }
    else {
      return type.printTypeWithoutTypeArgument() + r.toString();
    }
  }

  /**
   * Boxing generic types (e.g. "Collection" -> "java.util.Collection")
   * Results are fully qualified.
   * Otherwise return is unchanged
   * @deprecated use SymtypeBoxingVisitor
   * @param type
   * @return
   */
  @Deprecated
  public static String box(SymTypeOfGenerics type){
    List<SymTypeExpression> arguments = type.getArgumentList();
    StringBuilder r = new StringBuilder().append('<');
    for (int i = 0; i < arguments.size(); i++) {
      if (arguments.get(i).isGenericType()) {
        r.append(box((SymTypeOfGenerics) arguments.get(i)));
      }
      else {
        r.append(SymTypePrimitive.box(arguments.get(i).print()));
      }
      if (i < arguments.size() - 1) {
        r.append(',');
      }
    }
    r.append(">");
    if (boxMap.containsKey(type.printTypeWithoutTypeArgument())) {
      return boxMap.get(type.printTypeWithoutTypeArgument()) + r.toString();
    }
    else {
      return type.printTypeWithoutTypeArgument() + r.toString();
    }
  }

  protected TypeSymbol typeSymbol;

  /**
   * List of arguments of a type constructor
   */
  protected List<SymTypeExpression> arguments;

  /**
   * @deprecated use SymTypeExpressionFactory
   * The Factory then uses the constructor below
   */
  @Deprecated
  public SymTypeOfGenerics(TypeSymbol typeSymbol) {
    this(typeSymbol, new LinkedList<>());
  }

  /**
   * Constructor with all parameters that are stored:
   */
  public SymTypeOfGenerics(TypeSymbol typeSymbol, List<SymTypeExpression> arguments) {
    this.typeSymbol = typeSymbol;
    this.arguments = arguments;
  }

  @Override
  public boolean hasTypeInfo() {
    return typeSymbol != null;
  }

  @Override
  public TypeSymbol getTypeInfo() {
    return typeSymbol;
  }

  public String getTypeConstructorFullName() {
    return getTypeInfo().getFullName();
  }

  /**
   * @deprecated same as the the other 2 methods even in spec?
   */
  @Deprecated
  public String printTypeWithoutTypeArgument(){
    return this.getFullName();
  }

  /**
   * @deprecated same as the the other 2 methods even in spec?
   */
  @Deprecated
  public String getFullName() {
    return getTypeConstructorFullName();
  }

  /**
   * getBaseName: get the unqualified Name (no ., no Package)
   * @deprecated unused outside of tests, but not required for tests
   */
  @Deprecated
  public String getBaseName() {
    String[] parts = getTypeConstructorFullName().split("\\.");
    return parts[parts.length - 1];
  }

  @Override
  public boolean isGenericType() {
    return true;
  }

  @Override
  public SymTypeOfGenerics asGenericType() {
    return this;
  }

  public boolean deepEqualsWithoutArguments(SymTypeExpression sym) {
    if(!sym.isGenericType()){
      return false;
    }
    SymTypeOfGenerics symGen = (SymTypeOfGenerics) sym;
    if(!this.typeSymbol.getEnclosingScope().equals(symGen.typeSymbol.getEnclosingScope())){
      return false;
    }
    if(!this.typeSymbol.getName().equals(symGen.typeSymbol.getName())){
      return false;
    }
    return true;
  }

  @Override
  public boolean deepEquals(SymTypeExpression sym){
    if(!sym.isGenericType()){
      return false;
    }
    SymTypeOfGenerics symGen = (SymTypeOfGenerics) sym;
    if (!deepEqualsWithoutArguments(symGen)) {
      return false;
    }
    if(this.sizeArguments()!=symGen.sizeArguments()){
      return false;
    }
    for(int i = 0;i<this.sizeArguments();i++){
      if(!this.getArgument(i).deepEquals(symGen.getArgument(i))){
        return false;
      }
    }
    return true;
  }

  /**
   * returns the declared type,
   * e.g., for List<int>, this will return List<T> where T is a type variable.
   */
  public SymTypeOfGenerics getDeclaredType() {
    List<SymTypeExpression> typeParams = getTypeInfo().getTypeParameterList()
        .stream().map(SymTypeExpressionFactory::createTypeVariable)
        .collect(Collectors.toList());
    return SymTypeExpressionFactory.createGenerics(getTypeInfo(), typeParams);
  }

  public Map<SymTypeVariable, SymTypeExpression> getTypeVariableReplaceMap() {
    List<TypeVarSymbol> typeVars = getTypeInfo().getTypeParameterList();
    List<SymTypeExpression> arguments = getArgumentList();
    Map<SymTypeVariable, SymTypeExpression> replaceMap = new HashMap<>();
    // empty List, e.g. new HashMap<>();
    if (arguments.size() == 0) {
      // no-op
    }
    // otherwise, we expect the same amount of parameters as there are variables
    // Java (and we) currently (Java Spec 20) do not support varargs type variables
    else if (arguments.size() != typeVars.size()) {
      Log.error("0xFD672 expected " + typeVars.size() + " parameters "
          + "for " + getTypeInfo().getFullName() + "<" + typeVars.stream()
          .map(TypeSymbol::getFullName)
          .collect(Collectors.joining(", "))
          + ">, but got " + arguments.size()
          + ": " + printFullName()
      );
    }
    else {
      for (int i = 0; i < typeVars.size(); i++) {
        replaceMap.put(
            SymTypeExpressionFactory.createTypeVariable(typeVars.get(i)),
            arguments.get(i)
        );
      }
    }
    return replaceMap;
  }

  @Override
  public void replaceTypeVariables(Map<TypeVarSymbol, SymTypeExpression> replaceMap) {
    for(int i = 0; i<this.getArgumentList().size(); i++){
      SymTypeExpression type = this.getArgument(i);
      TypeSymbol realTypeInfo;
      TypeSymbol typeInfo = type.getTypeInfo();
      if(typeInfo instanceof TypeSymbolSurrogate){
        realTypeInfo = ((TypeSymbolSurrogate) type.getTypeInfo()).lazyLoadDelegate();
      }else{
        realTypeInfo = typeInfo;
      }
      if(type.isTypeVariable() && realTypeInfo instanceof TypeVarSymbol){
        Optional<TypeVarSymbol> typeVar =  replaceMap.keySet().stream().filter(t -> t.getName().equals(realTypeInfo.getName())).findAny();
        if(typeVar.isPresent()){
          List<SymTypeExpression> args = new ArrayList<>(getArgumentList());
          args.remove(type);
          args.add(i, replaceMap.get(typeVar.get()));
          this.setArgumentList(args);
        }
      }else{
        type.replaceTypeVariables(replaceMap);
      }
    }
  }

  @Override
  public void accept(ISymTypeVisitor visitor) {
    visitor.visit(this);
  }

  // --------------------------------------------------------------------------
  // From here on: Standard functionality to access the list of arguments
  // (was copied from a created class)
  // (and demonstrates that we still can optimize our generators & build processes)
  // --------------------------------------------------------------------------

  public  boolean containsArgument (Object element)  {
    return this.getArgumentList().contains(element);
  }

  public  boolean containsAllArguments (Collection<?> collection)  {
    return this.getArgumentList().containsAll(collection);
  }

  public  boolean isEmptyArguments ()  {
    return this.getArgumentList().isEmpty();
  }

  public Iterator<SymTypeExpression> iteratorArguments ()  {
    return this.getArgumentList().iterator();
  }

  public  int sizeArguments ()  {
    return this.getArgumentList().size();
  }

  public  de.monticore.types.check.SymTypeExpression[] toArrayArguments (de.monticore.types.check.SymTypeExpression[] array)  {
    return this.getArgumentList().toArray(array);
  }

  public  Object[] toArrayArguments ()  {
    return this.getArgumentList().toArray();
  }

  public  Spliterator<de.monticore.types.check.SymTypeExpression> spliteratorArguments ()  {
    return this.getArgumentList().spliterator();
  }

  public Stream<SymTypeExpression> streamArguments ()  {
    return this.getArgumentList().stream();
  }

  public  Stream<de.monticore.types.check.SymTypeExpression> parallelStreamArguments ()  {
    return this.getArgumentList().parallelStream();
  }

  public  de.monticore.types.check.SymTypeExpression getArgument (int index)  {
    return this.getArgumentList().get(index);
  }

  public  int indexOfArgument (Object element)  {
    return this.getArgumentList().indexOf(element);
  }

  public  int lastIndexOfArgument (Object element)  {
    return this.getArgumentList().lastIndexOf(element);
  }

  public  boolean equalsArguments (Object o)  {
    return this.getArgumentList().equals(o);
  }

  public  int hashCodeArguments ()  {
    return this.getArgumentList().hashCode();
  }

  public  ListIterator<de.monticore.types.check.SymTypeExpression> listIteratorArguments ()  {
    return this.getArgumentList().listIterator();
  }

  public  ListIterator<de.monticore.types.check.SymTypeExpression> listIteratorArguments (int index)  {
    return this.getArgumentList().listIterator(index);
  }

  public  List<de.monticore.types.check.SymTypeExpression> subListArguments (int start,int end)  {
    return this.getArgumentList().subList(start, end);
  }

  public  List<de.monticore.types.check.SymTypeExpression> getArgumentList ()  {
    return this.arguments;
  }

  public  void clearArguments ()  {
    this.getArgumentList().clear();
  }

  public  boolean addArgument (de.monticore.types.check.SymTypeExpression element)  {
    return this.getArgumentList().add(element);
  }

  public  boolean addAllArguments (Collection<? extends de.monticore.types.check.SymTypeExpression> collection)  {
    return this.getArgumentList().addAll(collection);
  }
  public  boolean removeArgument (Object element)  {
    return this.getArgumentList().remove(element);
  }

  public  boolean removeAllArguments (Collection<?> collection)  {
    return this.getArgumentList().removeAll(collection);
  }
  public  boolean retainAllArguments (Collection<?> collection)  {
    return this.getArgumentList().retainAll(collection);
  }

  public  boolean removeIfArgument (Predicate<? super SymTypeExpression> filter)  {
    return this.getArgumentList().removeIf(filter);
  }

  public  void forEachArguments (Consumer<? super SymTypeExpression> action)  {
    this.getArgumentList().forEach(action);
  }

  public  void addArgument (int index,de.monticore.types.check.SymTypeExpression element)  {
    this.getArgumentList().add(index, element);
  }

  public  boolean addAllArguments (int index,Collection<? extends de.monticore.types.check.SymTypeExpression> collection)  {
    return this.getArgumentList().addAll(index, collection);
  }

  public  de.monticore.types.check.SymTypeExpression removeArgument (int index)  {
    return this.getArgumentList().remove(index);
  }

  public  de.monticore.types.check.SymTypeExpression setArgument (int index,de.monticore.types.check.SymTypeExpression element)  {
    return this.getArgumentList().set(index, element);
  }

  public  void replaceAllArguments (UnaryOperator<SymTypeExpression> operator)  {
    this.getArgumentList().replaceAll(operator);
  }

  public  void sortArguments (Comparator<? super de.monticore.types.check.SymTypeExpression> comparator)  {
    this.getArgumentList().sort(comparator);
  }

  public  void setArgumentList (List<de.monticore.types.check.SymTypeExpression> arguments)  {
    this.arguments = arguments;
  }
}
