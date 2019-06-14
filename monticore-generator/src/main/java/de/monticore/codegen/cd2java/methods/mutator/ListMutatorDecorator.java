package de.monticore.codegen.cd2java.methods.mutator;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.methods.ListMethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;

import java.util.Arrays;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;

public class ListMutatorDecorator extends ListMethodDecorator {

  protected static final String SET_LIST = "public void set%sList(List<%s> %s);";
  protected static final String CLEAR = "public void clear%s();";
  protected static final String ADD = "public boolean add%s(%s element);";
  protected static final String ADD_ALL = "public boolean addAll%s(Collection<? extends %s> collection);";
  protected static final String REMOVE = "public boolean remove%s(Object element);";
  protected static final String REMOVE_ALL = "public boolean removeAll%s(Collection<?> collection);";
  protected static final String RETAIN_ALL = "public boolean retainAll%s(Collection<?> collection);";
  protected static final String REMOVE_IF = "public boolean removeIf%s(Predicate<? super %s> filter);";
  protected static final String FOR_EACH = "public void forEach%s(Consumer<? super %s> action);";
  protected static final String ADD_ = "public void add%s(int index, %s element);";
  protected static final String ADD_ALL_ = "public boolean addAll%s(int index, Collection<? extends %s> collection);";
  protected static final String REMOVE_ = "public %s remove%s(int index);";
  protected static final String SET = "public %s set%s(int index, %s element);";
  protected static final String REPLACE_ALL = "public void replaceAll%s(UnaryOperator<%s> operator);";
  protected static final String SORT = "public void sort%s(Comparator<? super %s> comparator);";

  public ListMutatorDecorator(final GlobalExtensionManagement glex) {
    super(glex);
  }

  @Override
  public List<ASTCDMethod> decorate(ASTCDAttribute ast) {
    List<ASTCDMethod> methods = createSetter(ast);
    methods.add(createSetListMethod(ast));
    return methods;
  }

  protected ASTCDMethod createSetListMethod(ASTCDAttribute ast) {
    String signature = String.format(SET_LIST, capitalizedAttributeNameWithOutS, attributeType, ast.getName());
    ASTCDMethod getList = this.getCDMethodFacade().createMethodByDefinition(signature);
    this.replaceTemplate(EMPTY_BODY, getList, new TemplateHookPoint("methods.Set", ast));
    return getList;
  }

  protected List<ASTCDMethod> createSetter(ASTCDAttribute ast){
    return super.decorate(ast);
  }


  @Override
  protected List<String> getMethodSignatures() {
    return Arrays.asList(
      String.format(CLEAR, capitalizedAttributeNameWithS),
      String.format(ADD, capitalizedAttributeNameWithOutS, attributeType),
      String.format(ADD_ALL, capitalizedAttributeNameWithS, attributeType),
      String.format(REMOVE, capitalizedAttributeNameWithOutS),
      String.format(REMOVE_ALL, capitalizedAttributeNameWithS),
      String.format(RETAIN_ALL, capitalizedAttributeNameWithS),
      String.format(REMOVE_IF, capitalizedAttributeNameWithOutS, attributeType),
      String.format(FOR_EACH, capitalizedAttributeNameWithS, attributeType),
      String.format(ADD_, capitalizedAttributeNameWithOutS, attributeType),
      String.format(ADD_ALL_, capitalizedAttributeNameWithS, attributeType),
      String.format(REMOVE_, attributeType, capitalizedAttributeNameWithOutS),
      String.format(SET, attributeType, capitalizedAttributeNameWithOutS, attributeType),
      String.format(REPLACE_ALL, capitalizedAttributeNameWithS, attributeType),
      String.format(SORT, capitalizedAttributeNameWithS, attributeType)
    );
  }
}
