package de.monticore.codegen.cd2java.methods.accessor;

import de.monticore.codegen.cd2java.methods.ListMethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;

public class ListAccessorDecorator extends ListMethodDecorator {

  //TODO distinguish between Methodnames with "s" oder without at the end of the Attributename
  protected static final String GET_LIST = "public List<%s> get%sList();";
  private static final String CONTAINS = "public boolean contains%s(Object element);";
  private static final String CONTAINS_ALL = "public boolean containsAll%s(Collection<?> collection);";
  private static final String IS_EMPTY = "public boolean isEmpty%s();";
  private static final String ITERATOR = "public Iterator<%s> iterator%s();";
  private static final String SIZE = "public int size%s();";
  private static final String TO_ARRAY = "public %s[] toArray%s(%s[] array);";
  private static final String TO_ARRAY_ = "public Object[] toArray%s();";
  private static final String SPLITERATOR = "public Spliterator<%s> spliterator%s();";
  private static final String STREAM = "public Stream<%s> stream%s();";
  private static final String PARALLEL_STREAM = "public Stream<%s> parallelStream%s();";
  private static final String GET = "public %s get%s(int index);";
  private static final String INDEX_OF = "public int indexOf%s(Object element);";
  private static final String LAST_INDEX_OF = "public int lastIndexOf%s(Object element);";
  private static final String EQUALS = "public boolean equals%s(Object o);";
  private static final String HASHCODE = "public int hashCode%s();";
  private static final String LIST_ITERATOR = "public ListIterator<%s> listIterator%s();";
  private static final String LIST_ITERATOR_ = "public ListIterator<%s> listIterator%s(int index);";
  private static final String SUBLIST = "public List<%s> subList%s(int start, int end);";

  public ListAccessorDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public List<ASTCDMethod> decorate(ASTCDAttribute ast) {
    List<ASTCDMethod> methods = super.decorate(ast);
    methods.add(createGetListMethod(ast));
    return methods;
  }


  protected ASTCDMethod createGetListMethod(ASTCDAttribute ast) {
    String signature = String.format(GET_LIST, attributeType, capitalizedAttributeName);
    ASTCDMethod getList = this.getCDMethodFactory().createMethodByDefinition(signature);
    this.replaceTemplate(EMPTY_BODY, getList, new TemplateHookPoint("methods.Get", ast));
    return getList;
  }

  @Override
  protected List<String> getMethodSignatures() {
    return Arrays.asList(
      String.format(CONTAINS, capitalizedAttributeName),
      String.format(CONTAINS_ALL, capitalizedAttributeName),
      String.format(IS_EMPTY, capitalizedAttributeName),
      String.format(ITERATOR, attributeType, capitalizedAttributeName),
      String.format(SIZE, capitalizedAttributeName),
      String.format(TO_ARRAY, attributeType, capitalizedAttributeName, attributeType),
      String.format(TO_ARRAY_, capitalizedAttributeName),
      String.format(SPLITERATOR, attributeType, capitalizedAttributeName),
      String.format(STREAM, attributeType, capitalizedAttributeName),
      String.format(PARALLEL_STREAM, attributeType, capitalizedAttributeName),
      String.format(GET, attributeType, capitalizedAttributeName),
      String.format(INDEX_OF, capitalizedAttributeName),
      String.format(LAST_INDEX_OF, capitalizedAttributeName),
      String.format(EQUALS, capitalizedAttributeName),
      String.format(HASHCODE, capitalizedAttributeName),
      String.format(LIST_ITERATOR, attributeType, capitalizedAttributeName),
      String.format(LIST_ITERATOR_, attributeType, capitalizedAttributeName),
      String.format(SUBLIST, attributeType, capitalizedAttributeName)
    );
  }





}
