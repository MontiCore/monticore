/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.collect.Lists;

import de.monticore.symboltable.serialization.JsonConstants;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.types.typesymbols._symboltable.TypeSymbol;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
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
   * A SymTypeExpression has
   *    a name (representing a TypeConstructor) and
   *    a list of Type Expressions
   * This is always the full qualified name (i.e. including package)
   */
  protected String typeConstructorFullName;
  
  /**
   * List of arguments of a type constructor
   */
  protected List<SymTypeExpression> arguments = new LinkedList<>();
  
  /**
   * Symbol corresponding to the type constructors's name (if loaded???)
   */
  protected TypeSymbol objTypeConstructorSymbol;
  
  
  @Deprecated // XXX bestezt nicht alle Attribute und kann wohl raus.
  public SymTypeOfGenerics(String typeConstructorFullName, List<SymTypeExpression> arguments) {
    this.typeConstructorFullName = typeConstructorFullName;
    this.arguments = arguments;
  }


  // TODO: besetzt nicht die geerbten Attribute
  public SymTypeOfGenerics(String typeConstructorFullName, List<SymTypeExpression> arguments,
                           TypeSymbol objTypeConstructorSymbol) {
    this.typeConstructorFullName = typeConstructorFullName;
    this.arguments = arguments;
    this.objTypeConstructorSymbol = objTypeConstructorSymbol;
  }
  
  
  public String getTypeConstructorFullName() {
    return typeConstructorFullName;
  }
  
  public void setTypeConstructorFullName(String typeConstructorFullName) {
    this.typeConstructorFullName = typeConstructorFullName;
  }
  
  public TypeSymbol getObjTypeConstructorSymbol() {
    return objTypeConstructorSymbol;
  }
  
  public void setObjTypeConstructorSymbol(TypeSymbol objTypeConstructorSymbol) {
    this.objTypeConstructorSymbol = objTypeConstructorSymbol;
  }
  
  /**
   * print: Umwandlung in einen kompakten String
   */
  public String print() {
    StringBuffer r = new StringBuffer(getTypeConstructorFullName()).append('<');
    for(int i = 0; i<arguments.size();i++){
      r.append(arguments.get(i).print());
      if(i<arguments.size()-1) { r.append(','); }
    }
    return r.append('>').toString();
  }
  
  /**
   * printAsJson: Umwandlung in einen kompakten Json String
   */
  protected String printAsJson() {
    JsonPrinter jp = new JsonPrinter();
    jp.beginObject();
    //TODO: anpassen, nachdem package umbenannt ist
    jp.member(JsonConstants.KIND, "de.monticore.types.check.SymTypeOfGenerics");
    jp.member("typeConstructorFullName", getTypeConstructorFullName());
    jp.beginArray("arguments");
    for(SymTypeExpression exp : getArgumentList()) {
      jp.valueJson(exp.printAsJson());
    }
    jp.endArray();
    //TODO: TypeSymbolDeSer implementieren
    jp.member("objTypeConstructorSymbol", "TODO");//new TypeSymbolDeSer().serialize(getObjTypeConstructorSymbol()));
    jp.endObject();
    return jp.getContent();
  }
  
  
  /**
   * getFullName: get the Qualified Name including Package
   */
  public String getFullName() {
    return getTypeConstructorFullName();
  }
  
  /**
   * getBaseName: get the unqualified Name (no ., no Package)
   */
  public String getBaseName() {
    String[] parts = getTypeConstructorFullName().split("\\.");
    return parts[parts.length - 1];
  }
  
  // --------------------------------------------------------------------------
  // From here on: Standard functionality to access the list of arguments
  // TODO: (was copied from a created class)
  // (and demonstrates that we still can optimize our generators)
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

  /* generated by template core.Method*/
  public  boolean removeIfArgument (Predicate<? super SymTypeExpression> filter)  {
    /* generated by template methods.MethodDelegate*/

    return this.getArgumentList().removeIf(filter);

  }

  /* generated by template core.Method*/
  public  void forEachArguments (Consumer<? super SymTypeExpression> action)  {
    /* generated by template methods.MethodDelegate*/

    this.getArgumentList().forEach(action);

  }

  /* generated by template core.Method*/
  public  void addArgument (int index,de.monticore.types.check.SymTypeExpression element)  {
    /* generated by template methods.MethodDelegate*/

    this.getArgumentList().add(index, element);

  }

  /* generated by template core.Method*/
  public  boolean addAllArguments (int index,Collection<? extends de.monticore.types.check.SymTypeExpression> collection)  {
    /* generated by template methods.MethodDelegate*/

    return this.getArgumentList().addAll(index, collection);

  }

  /* generated by template core.Method*/
  public  de.monticore.types.check.SymTypeExpression removeArgument (int index)  {
    /* generated by template methods.MethodDelegate*/

    return this.getArgumentList().remove(index);

  }

  /* generated by template core.Method*/
  public  de.monticore.types.check.SymTypeExpression setArgument (int index,de.monticore.types.check.SymTypeExpression element)  {
    /* generated by template methods.MethodDelegate*/

    return this.getArgumentList().set(index, element);

  }

  /* generated by template core.Method*/
  public  void replaceAllArguments (UnaryOperator<SymTypeExpression> operator)  {
    /* generated by template methods.MethodDelegate*/

    this.getArgumentList().replaceAll(operator);

  }

  /* generated by template core.Method*/
  public  void sortArguments (Comparator<? super de.monticore.types.check.SymTypeExpression> comparator)  {
    /* generated by template methods.MethodDelegate*/

    this.getArgumentList().sort(comparator);

  }

  /* generated by template core.Method*/
  public  void setArgumentList (List<de.monticore.types.check.SymTypeExpression> arguments)  {
    /* generated by template methods.Set*/

    this.arguments = arguments;

  }
  
  
}
