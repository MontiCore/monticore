package de.monticore.codegen.cd2java.methods;

import de.monticore.codegen.cd2java.factories.CDMethodFactory;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

public class ListMethodGeneratorStrategy implements MethodGeneratorStrategy {

  private static final String CLEAR           = "public void clear%s()";
  private static final String ADD             = "public boolean add%s(%s element);";
  private static final String ADD_ALL         = "public boolean addAll%s(Collection<? extends %s> collection);";
  private static final String REMOVE          = "public boolean remove%s(Object element);";
  private static final String REMOVE_ALL      = "public boolean removeAll%s(Collection<?> collection);";
  private static final String RETAIN_ALL      = "public boolean retainAll%s(Collection<?> collection);";
  private static final String REMOVE_IF       = "public boolean removeIf%s(Predicate<? super %s> filter);";
  private static final String FOR_EACH        = "public void forEach%s(Consumer<? super %s> action);";
  private static final String ADD_            = "public void add%s(int index, %s element);";
  private static final String ADD_ALL_        = "public boolean addAll%s(int index, Collection<? extends %s> collection);";
  private static final String REMOVE_         = "public %s remove%s(int index);";
  private static final String SET             = "public %s set%s(int index, %s element);";
  private static final String REPLACE_ALL     = "public void replaceAll%s(UnaryOperator<%s> operator);";
  private static final String SORT            = "public void sort%s(Comparator<? super %s> comparator);";

  private static final String CONTAINS        = "public boolean contains%s(Object element);";
  private static final String CONTAINS_ALL    = "public boolean containsAll%s(Collection<?> collection);";
  private static final String IS_EMPTY        = "public boolean isEmpty%s();";
  private static final String ITERATOR        = "public Iterator<%s> iterator%s();";
  private static final String SIZE            = "public int size%s();";
  private static final String TO_ARRAY        = "public %s[] toArray%s(%s[] array);";
  private static final String TO_ARRAY_       = "public Object[] toArray%s();";
  private static final String SPLITERATOR     = "public Spliterator<%s> spliterator%s();";
  private static final String STREAM          = "public Stream<%s> stream%s();";
  private static final String PARALLEL_STREAM = "public Stream<%s> parallelStream%s();";
  private static final String GET             = "public %s get%s(int index);";
  private static final String INDEX_OF        = "public int indexOf%s(Object element);";
  private static final String LAST_INDEX_OF   = "public int lastIndexOf%s(Object element);";
  private static final String EQUALS          = "public boolean equals%s(Object o);";
  private static final String HASHCODE        = "public int hashCode%s();";
  private static final String LIST_ITERATOR   = "public ListIterator<%s> listIterator%s();";
  private static final String LIST_ITERATOR_  = "public ListIterator<%s> listIterator%s(int index);";
  private static final String SUBLIST         = "public List<%s> subList%s(int start, int end);";

  private final CDMethodFactory cdMethodFactory;

  private final MandatoryMethodGeneratorStrategy mandatoryMethodGeneratorStrategy;

  private String attributeName;

  private String attributeType;

  protected ListMethodGeneratorStrategy(CDMethodFactory cdMethodFactory, MandatoryMethodGeneratorStrategy mandatoryMethodGeneratorStrategy) {
    this.cdMethodFactory = cdMethodFactory;
    this.mandatoryMethodGeneratorStrategy = mandatoryMethodGeneratorStrategy;
  }

  @Override
  public List<ASTCDMethod> generate(final ASTCDAttribute ast) {
    this.attributeName = StringUtils.capitalize(ast.getName());
    this.attributeType = ast.printType();
    List<ASTCDMethod> methods = this.mandatoryMethodGeneratorStrategy.generate(ast);
    methods.addAll(Arrays.asList(
        createClearMethod(),
        createAddMethod(),
        createAddAllMethod(),
        createRemoveMethod(),
        createRemoveAllMethod(),
        createRetainAllMethod(),
        createRemoveIfMethod(),
        createForEachMethod(),
        createAdd_Method(),
        createAddAll_Method(),
        createRemove_Method(),
        createSetMethod(),
        createReplaceAllMethod(),
        createSortMethod(),

        createContainsMethod(),
        createContainsAllMethod(),
        createIsEmptyMethod(),
        createIteratorMethod(),
        createSizeMethod(),
        createToArrayMethod(),
        createToArray_Method(),
        createSpliteratorMethod(),
        createStreamMethod(),
        createParallelStreamMethod(),
        createGetMethod(),
        createIndexOfMethod(),
        createLastIndexOfMethod(),
        createEqualsMethod(),
        createHashCodeMethod(),
        createListIteratorMethod(),
        createListIterator_Method(),
        createSubListMethod()));
    return methods;
  }

  protected ASTCDMethod createClearMethod() {
    String signature = String.format(CLEAR, attributeName);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  protected ASTCDMethod createAddMethod() {
    String signature = String.format(ADD, attributeName, attributeType);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  protected ASTCDMethod createAddAllMethod() {
    String signature = String.format(ADD_ALL, attributeName, attributeType);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  protected ASTCDMethod createRemoveMethod() {
    String signature = String.format(REMOVE, attributeName);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  protected ASTCDMethod createRemoveAllMethod() {
    String signature = String.format(REMOVE_ALL, attributeName);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  protected ASTCDMethod createRetainAllMethod() {
    String signature = String.format(RETAIN_ALL, attributeName);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  protected ASTCDMethod createRemoveIfMethod() {
    String signature = String.format(REMOVE_IF, attributeName, attributeType);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  protected ASTCDMethod createForEachMethod() {
    String signature = String.format(FOR_EACH, attributeName, attributeType);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  protected ASTCDMethod createAdd_Method() {
    String signature = String.format(ADD_, attributeName, attributeType);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  protected ASTCDMethod createAddAll_Method() {
    String signature = String.format(ADD_ALL_, attributeName, attributeType);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  protected ASTCDMethod createRemove_Method() {
    String signature = String.format(REMOVE_, attributeType, attributeName);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  protected ASTCDMethod createSetMethod() {
    String signature = String.format(SET, attributeType, attributeName, attributeType);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  protected ASTCDMethod createReplaceAllMethod() {
    String signature = String.format(REPLACE_ALL, attributeName, attributeType);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  protected ASTCDMethod createSortMethod() {
    String signature = String.format(SORT, attributeName, attributeType);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }



  private ASTCDMethod createContainsMethod() {
    String signature = String.format(CONTAINS, attributeName);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  private ASTCDMethod createContainsAllMethod() {
    String signature = String.format(CONTAINS_ALL, attributeName);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  private ASTCDMethod createIsEmptyMethod() {
    String signature = String.format(IS_EMPTY, attributeName);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  private ASTCDMethod createIteratorMethod() {
    String signature = String.format(ITERATOR, attributeType, attributeName);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  private ASTCDMethod createSizeMethod() {
    String signature = String.format(SIZE, attributeName);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  private ASTCDMethod createToArrayMethod() {
    String signature = String.format(TO_ARRAY, attributeType, attributeName, attributeType);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  private ASTCDMethod createToArray_Method() {
    String signature = String.format(TO_ARRAY_, attributeName);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  private ASTCDMethod createSpliteratorMethod() {
    String signature = String.format(SPLITERATOR, attributeType, attributeName);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  private ASTCDMethod createStreamMethod() {
    String signature = String.format(STREAM, attributeType, attributeName);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  private ASTCDMethod createParallelStreamMethod() {
    String signature = String.format(PARALLEL_STREAM, attributeType, attributeName);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  private ASTCDMethod createGetMethod() {
    String signature = String.format(GET, attributeType, attributeName);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  private ASTCDMethod createIndexOfMethod() {
    String signature = String.format(INDEX_OF, attributeName);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  private ASTCDMethod createLastIndexOfMethod() {
    String signature = String.format(LAST_INDEX_OF, attributeName);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  private ASTCDMethod createEqualsMethod() {
    String signature = String.format(EQUALS, attributeName);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  private ASTCDMethod createHashCodeMethod() {
    String signature = String.format(HASHCODE, attributeName);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  private ASTCDMethod createListIteratorMethod() {
    String signature = String.format(LIST_ITERATOR, attributeType, attributeName);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  private ASTCDMethod createListIterator_Method() {
    String signature = String.format(LIST_ITERATOR_, attributeType, attributeName);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  private ASTCDMethod createSubListMethod() {
    String signature = String.format(SUBLIST, attributeType, attributeName);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }
}
