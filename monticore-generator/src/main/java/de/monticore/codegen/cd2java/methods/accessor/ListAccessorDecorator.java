/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.methods.accessor;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.methods.ListMethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;

import java.util.Arrays;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;

public class ListAccessorDecorator extends ListMethodDecorator {

  protected static final String GET_LIST = "public List<%s> get%sList();";
  protected static final String CONTAINS = "public boolean contains%s(Object element);";
  protected static final String CONTAINS_ALL = "public boolean containsAll%s(Collection<?> collection);";
  protected static final String IS_EMPTY = "public boolean isEmpty%s();";
  protected static final String ITERATOR = "public Iterator<%s> iterator%s();";
  protected static final String SIZE = "public int size%s();";
  protected static final String TO_ARRAY = "public %s[] toArray%s(%s[] array);";
  protected static final String TO_ARRAY_ = "public Object[] toArray%s();";
  protected static final String SPLITERATOR = "public Spliterator<%s> spliterator%s();";
  protected static final String STREAM = "public Stream<%s> stream%s();";
  protected static final String PARALLEL_STREAM = "public Stream<%s> parallelStream%s();";
  protected static final String GET = "public %s get%s(int index);";
  protected static final String INDEX_OF = "public int indexOf%s(Object element);";
  protected static final String LAST_INDEX_OF = "public int lastIndexOf%s(Object element);";
  protected static final String EQUALS = "public boolean equals%s(Object o);";
  protected static final String HASHCODE = "public int hashCode%s();";
  protected static final String LIST_ITERATOR = "public ListIterator<%s> listIterator%s();";
  protected static final String LIST_ITERATOR_ = "public ListIterator<%s> listIterator%s(int index);";
  protected static final String SUBLIST = "public List<%s> subList%s(int start, int end);";

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
    String signature = String.format(GET_LIST, attributeType, capitalizedAttributeNameWithOutS);
    ASTCDMethod getList = this.getCDMethodFacade().createMethodByDefinition(signature);
    this.replaceTemplate(EMPTY_BODY, getList, new TemplateHookPoint("methods.Get", ast));
    return getList;
  }

  @Override
  protected List<String> getMethodSignatures() {
    return Arrays.asList(
        String.format(CONTAINS, capitalizedAttributeNameWithOutS),
        String.format(CONTAINS_ALL, capitalizedAttributeNameWithS),
        String.format(IS_EMPTY, capitalizedAttributeNameWithS),
        String.format(ITERATOR, attributeType, capitalizedAttributeNameWithS),
        String.format(SIZE, capitalizedAttributeNameWithS),
        String.format(TO_ARRAY, attributeType, capitalizedAttributeNameWithS, attributeType),
        String.format(TO_ARRAY_, capitalizedAttributeNameWithS),
        String.format(SPLITERATOR, attributeType, capitalizedAttributeNameWithS),
        String.format(STREAM, attributeType, capitalizedAttributeNameWithS),
        String.format(PARALLEL_STREAM, attributeType, capitalizedAttributeNameWithS),
        String.format(GET, attributeType, capitalizedAttributeNameWithOutS),
        String.format(INDEX_OF, capitalizedAttributeNameWithOutS),
        String.format(LAST_INDEX_OF, capitalizedAttributeNameWithOutS),
        String.format(EQUALS, capitalizedAttributeNameWithS),
        String.format(HASHCODE, capitalizedAttributeNameWithS),
        String.format(LIST_ITERATOR, attributeType, capitalizedAttributeNameWithS),
        String.format(LIST_ITERATOR_, attributeType, capitalizedAttributeNameWithS),
        String.format(SUBLIST, attributeType, capitalizedAttributeNameWithS)
    );
  }
}
