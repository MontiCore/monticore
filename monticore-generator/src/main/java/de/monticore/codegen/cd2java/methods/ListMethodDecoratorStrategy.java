package de.monticore.codegen.cd2java.methods;

import de.monticore.codegen.cd2java.factories.CDMethodFactory;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.types.types._ast.ASTTypeArgument;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;

public class ListMethodDecoratorStrategy implements MethodDecoratorStrategy {

  private static final String CLEAR           = "public void clear%s();";
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
  private static final String SUBLIST         = "public java.util.List<%s> subList%s(int start, int end);";

  private final GlobalExtensionManagement glex;

  private final CDMethodFactory cdMethodFactory;

  private final MandatoryMethodDecoratorStrategy mandatoryMethodDecoratorStrategy;

  private String capitalizedAttributeName;

  private String attributeType;

  protected ListMethodDecoratorStrategy(
      final GlobalExtensionManagement glex,
      final MandatoryMethodDecoratorStrategy mandatoryMethodDecoratorStrategy) {
    this.glex = glex;
    this.cdMethodFactory = CDMethodFactory.getInstance();
    this.mandatoryMethodDecoratorStrategy = mandatoryMethodDecoratorStrategy;
  }

  @Override
  public List<ASTCDMethod> decorate(final ASTCDAttribute ast) {
    this.capitalizedAttributeName = StringUtils.capitalize(ast.getName());
    this.attributeType = getGenericTypeFromListAttribute(ast.getType());
    List<ASTCDMethod> methods = new ArrayList<>(Arrays.asList(
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

    methods.forEach(this::addImplementation);

    methods.addAll(this.mandatoryMethodDecoratorStrategy.decorate(ast));
    return methods;
  }

  private String getGenericTypeFromListAttribute(ASTType type) {
    String typeString = TypesPrinter.printType(type);
    return typeString.substring("java.util.List<".length(), typeString.length() - 1);
  }

  private void addImplementation(final ASTCDMethod method) {
    String attributeName = StringUtils.uncapitalize(this.capitalizedAttributeName);
    String methodName = method.getName().substring(0, method.getName().length() - this.capitalizedAttributeName.length());
    String parameterCall = method.getCDParameterList().stream()
        .map(ASTCDParameter::getName)
        .collect(Collectors.joining(", "));
    String returnType = method.printReturnType();

    HookPoint impl = createImplementation(attributeName, methodName, parameterCall, returnType);

    this.glex.replaceTemplate(EMPTY_BODY, method, impl);
  }

  protected HookPoint createImplementation(String attributeName, String methodName, String parameterCall, String returnType) {
    return new TemplateHookPoint("methods.MethodDelegate", attributeName, methodName, parameterCall, returnType);
  }

  protected ASTCDMethod createClearMethod() {
    String signature = String.format(CLEAR, capitalizedAttributeName);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  protected ASTCDMethod createAddMethod() {
    String signature = String.format(ADD, capitalizedAttributeName, attributeType);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  protected ASTCDMethod createAddAllMethod() {
    String signature = String.format(ADD_ALL, capitalizedAttributeName, attributeType);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  protected ASTCDMethod createRemoveMethod() {
    String signature = String.format(REMOVE, capitalizedAttributeName);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  protected ASTCDMethod createRemoveAllMethod() {
    String signature = String.format(REMOVE_ALL, capitalizedAttributeName);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  protected ASTCDMethod createRetainAllMethod() {
    String signature = String.format(RETAIN_ALL, capitalizedAttributeName);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  protected ASTCDMethod createRemoveIfMethod() {
    String signature = String.format(REMOVE_IF, capitalizedAttributeName, attributeType);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  protected ASTCDMethod createForEachMethod() {
    String signature = String.format(FOR_EACH, capitalizedAttributeName, attributeType);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  protected ASTCDMethod createAdd_Method() {
    String signature = String.format(ADD_, capitalizedAttributeName, attributeType);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  protected ASTCDMethod createAddAll_Method() {
    String signature = String.format(ADD_ALL_, capitalizedAttributeName, attributeType);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  protected ASTCDMethod createRemove_Method() {
    String signature = String.format(REMOVE_, attributeType, capitalizedAttributeName);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  protected ASTCDMethod createSetMethod() {
    String signature = String.format(SET, attributeType, capitalizedAttributeName, attributeType);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  protected ASTCDMethod createReplaceAllMethod() {
    String signature = String.format(REPLACE_ALL, capitalizedAttributeName, attributeType);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  protected ASTCDMethod createSortMethod() {
    String signature = String.format(SORT, capitalizedAttributeName, attributeType);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }



  private ASTCDMethod createContainsMethod() {
    String signature = String.format(CONTAINS, capitalizedAttributeName);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  private ASTCDMethod createContainsAllMethod() {
    String signature = String.format(CONTAINS_ALL, capitalizedAttributeName);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  private ASTCDMethod createIsEmptyMethod() {
    String signature = String.format(IS_EMPTY, capitalizedAttributeName);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  private ASTCDMethod createIteratorMethod() {
    String signature = String.format(ITERATOR, attributeType, capitalizedAttributeName);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  private ASTCDMethod createSizeMethod() {
    String signature = String.format(SIZE, capitalizedAttributeName);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  private ASTCDMethod createToArrayMethod() {
    String signature = String.format(TO_ARRAY, attributeType, capitalizedAttributeName, attributeType);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  private ASTCDMethod createToArray_Method() {
    String signature = String.format(TO_ARRAY_, capitalizedAttributeName);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  private ASTCDMethod createSpliteratorMethod() {
    String signature = String.format(SPLITERATOR, attributeType, capitalizedAttributeName);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  private ASTCDMethod createStreamMethod() {
    String signature = String.format(STREAM, attributeType, capitalizedAttributeName);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  private ASTCDMethod createParallelStreamMethod() {
    String signature = String.format(PARALLEL_STREAM, attributeType, capitalizedAttributeName);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  private ASTCDMethod createGetMethod() {
    String signature = String.format(GET, attributeType, capitalizedAttributeName);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  private ASTCDMethod createIndexOfMethod() {
    String signature = String.format(INDEX_OF, capitalizedAttributeName);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  private ASTCDMethod createLastIndexOfMethod() {
    String signature = String.format(LAST_INDEX_OF, capitalizedAttributeName);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  private ASTCDMethod createEqualsMethod() {
    String signature = String.format(EQUALS, capitalizedAttributeName);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  private ASTCDMethod createHashCodeMethod() {
    String signature = String.format(HASHCODE, capitalizedAttributeName);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  private ASTCDMethod createListIteratorMethod() {
    String signature = String.format(LIST_ITERATOR, attributeType, capitalizedAttributeName);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  private ASTCDMethod createListIterator_Method() {
    String signature = String.format(LIST_ITERATOR_, attributeType, capitalizedAttributeName);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }

  private ASTCDMethod createSubListMethod() {
    String signature = String.format(SUBLIST, attributeType, capitalizedAttributeName);
    return this.cdMethodFactory.createMethodByDefinition(signature);
  }
}
