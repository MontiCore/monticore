package de.monticore.codegen.cd2java.methods.mutator;

import de.monticore.codegen.cd2java.methods.ListMethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;

import java.util.Arrays;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;

public class ListMutatorDecorator extends ListMethodDecorator {

  //TODO distinguish between Methodnames with "s" oder without at the end of the Attributename
  private static final String SET_LIST = "public void set%sList(List<%s> element);";
  private static final String CLEAR = "public void clear%s();";
  private static final String ADD = "public boolean add%s(%s element);";
  private static final String ADD_ALL = "public boolean addAll%s(Collection<? extends %s> collection);";
  private static final String REMOVE = "public boolean remove%s(Object element);";
  private static final String REMOVE_ALL = "public boolean removeAll%s(Collection<?> collection);";
  private static final String RETAIN_ALL = "public boolean retainAll%s(Collection<?> collection);";
  private static final String REMOVE_IF = "public boolean removeIf%s(Predicate<? super %s> filter);";
  private static final String FOR_EACH = "public void forEach%s(Consumer<? super %s> action);";
  private static final String ADD_ = "public void add%s(int index, %s element);";
  private static final String ADD_ALL_ = "public boolean addAll%s(int index, Collection<? extends %s> collection);";
  private static final String REMOVE_ = "public %s remove%s(int index);";
  private static final String SET = "public %s set%s(int index, %s element);";
  private static final String REPLACE_ALL = "public void replaceAll%s(UnaryOperator<%s> operator);";
  private static final String SORT = "public void sort%s(Comparator<? super %s> comparator);";

  public ListMutatorDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public List<ASTCDMethod> decorate(ASTCDAttribute ast) {
    List<ASTCDMethod> methods = super.decorate(ast);
    methods.add(createSetListMethod(ast));
    return methods;
  }

  private ASTCDMethod createSetListMethod(ASTCDAttribute ast) {
    String signature = String.format(SET_LIST, capitalizedAttributeName, attributeType);
    ASTCDMethod getList = this.getCDMethodFacade().createMethodByDefinition(signature);
    this.replaceTemplate(EMPTY_BODY, getList, new TemplateHookPoint("methods.Set", ast));
    return getList;
  }

  @Override
  protected List<String> getMethodSignatures() {
    return Arrays.asList(
      String.format(CLEAR, capitalizedAttributeName),
      String.format(ADD, capitalizedAttributeName, attributeType),
      String.format(ADD_ALL, capitalizedAttributeName, attributeType),
      String.format(REMOVE, capitalizedAttributeName),
      String.format(REMOVE_ALL, capitalizedAttributeName),
      String.format(RETAIN_ALL, capitalizedAttributeName),
      String.format(REMOVE_IF, capitalizedAttributeName, attributeType),
      String.format(FOR_EACH, capitalizedAttributeName, attributeType),
      String.format(ADD_, capitalizedAttributeName, attributeType),
      String.format(ADD_ALL_, capitalizedAttributeName, attributeType),
      String.format(REMOVE_, attributeType, capitalizedAttributeName),
      String.format(SET, attributeType, capitalizedAttributeName, attributeType),
      String.format(REPLACE_ALL, capitalizedAttributeName, attributeType),
      String.format(SORT, capitalizedAttributeName, attributeType)
    );
  }
}
