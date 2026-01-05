/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.methods.mutator;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.methods.ListMethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCListType;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.se_rwth.commons.logging.Log;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;

public class ListMutatorDecorator extends ListMethodDecorator {
  String ERROR_CODE = "0xA3232";

  protected static final String SET_LIST = "public void set%sList(List<%s> %s);";
  protected static final String SET_LIST_GENERIC = "public void set%sList(List<? extends %s> %s);";
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
    if(getDecorationHelper().isAstNode(ast)){
      if(!getDecorationHelper().isListType(ast.getMCType().printType()) && !(ast.getMCType().getClass() == ASTMCListType.class)
      && ((ASTMCBasicGenericType) ast.getMCType()).getMCTypeArgumentList().isEmpty() && ((ASTMCBasicGenericType) ast.getMCType()).getMCTypeArgumentList().get(0).getMCTypeOpt().isEmpty()){
        Log.error(ERROR_CODE + " The attribute " + ast.getName() + " is marked as AST node list but does not provide a generic type argument.");
      }
      ASTMCType mcType = ((ASTMCBasicGenericType)ast.getMCType()).getMCTypeArgumentList().get(0).getMCTypeOpt().get();
      String signature = String.format(SET_LIST_GENERIC, capitalizedAttributeNameWithOutS, attributeType, ast.getName());
      ASTCDMethod setListMethod = getCDMethodFacade().createMethodByDefinition(signature);
      this.replaceTemplate(EMPTY_BODY, setListMethod, new TemplateHookPoint("mc.methods.ListSetGeneric", ast, mcType.printType(), ERROR_CODE));
      return setListMethod;
    }else{
      String signature = String.format(SET_LIST, capitalizedAttributeNameWithOutS, attributeType, ast.getName());
      ASTCDMethod setListMethod = getCDMethodFacade().createMethodByDefinition(signature);
      this.replaceTemplate(EMPTY_BODY, setListMethod, new TemplateHookPoint("methods.Set", ast));
      return setListMethod;
    }
  }

  protected List<ASTCDMethod> createSetter(ASTCDAttribute ast) {
    return super.decorate(ast);
  }


  @Override
  protected Map<String, String> getMethodSignatures() {
    Map<String, String> signatures = new LinkedHashMap<>();
    signatures.put("clear", String.format(CLEAR, capitalizedAttributeNameWithS));
    signatures.put("add", String.format(ADD, capitalizedAttributeNameWithOutS, attributeType));
    signatures.put("addAll", String.format(ADD_ALL, capitalizedAttributeNameWithS, attributeType));
    signatures.put("remove", String.format(REMOVE, capitalizedAttributeNameWithOutS));
    signatures.put("removeAll", String.format(REMOVE_ALL, capitalizedAttributeNameWithS));
    signatures.put("retainAll", String.format(RETAIN_ALL, capitalizedAttributeNameWithS));
    signatures.put("removeIf", String.format(REMOVE_IF, capitalizedAttributeNameWithOutS, attributeType));
    signatures.put("forEach", String.format(FOR_EACH, capitalizedAttributeNameWithS, attributeType));
    signatures.put("add_", String.format(ADD_, capitalizedAttributeNameWithOutS, attributeType));
    signatures.put("addAll_", String.format(ADD_ALL_, capitalizedAttributeNameWithS, attributeType));
    signatures.put("remove_", String.format(REMOVE_, attributeType, capitalizedAttributeNameWithOutS));
    signatures.put("set", String.format(SET, attributeType, capitalizedAttributeNameWithOutS, attributeType));
    signatures.put("replaceAll", String.format(REPLACE_ALL, capitalizedAttributeNameWithS, attributeType));
    signatures.put("sort", String.format(SORT, capitalizedAttributeNameWithS, attributeType));
    return signatures;
  }

  @Override
  protected Map<String, String> getMethodSignaturesGeneric() {
    Map<String, String> signatures = new LinkedHashMap<>();
    signatures.put("clear", String.format(CLEAR, capitalizedAttributeNameWithS));
    signatures.put("add", String.format(ADD, capitalizedAttributeNameWithOutS, attributeType));
    signatures.put("addAll", String.format(ADD_ALL, capitalizedAttributeNameWithS, attributeType));
    signatures.put("remove", String.format(REMOVE, capitalizedAttributeNameWithOutS));
    signatures.put("removeAll", String.format(REMOVE_ALL, capitalizedAttributeNameWithS));
    signatures.put("retainAll", String.format(RETAIN_ALL, capitalizedAttributeNameWithS));
    signatures.put("removeIf", String.format(REMOVE_IF, capitalizedAttributeNameWithOutS, attributeType));
    signatures.put("forEach", String.format(FOR_EACH, capitalizedAttributeNameWithS, attributeType));
    signatures.put("add_", String.format(ADD_, capitalizedAttributeNameWithOutS, attributeType));
    signatures.put("addAll_", String.format(ADD_ALL_, capitalizedAttributeNameWithS, attributeType));
    signatures.put("remove_", String.format(REMOVE_, attributeType, capitalizedAttributeNameWithOutS));
    signatures.put("set", String.format(SET, attributeType, capitalizedAttributeNameWithOutS, attributeType));
    signatures.put("replaceAll", String.format(REPLACE_ALL, capitalizedAttributeNameWithS, attributeType));
    signatures.put("sort", String.format(SORT, capitalizedAttributeNameWithS, attributeType));
    return signatures;
  }
}
