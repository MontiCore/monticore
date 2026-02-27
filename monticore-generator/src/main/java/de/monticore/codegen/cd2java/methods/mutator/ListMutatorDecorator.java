/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.methods.mutator;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.codegen.cd2java.AbstractService;
import de.monticore.codegen.cd2java.methods.ListMethodDecorator;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCListType;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.se_rwth.commons.logging.Log;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

  protected final AbstractService service;

  public ListMutatorDecorator(final GlobalExtensionManagement glex, AbstractService service) {
    super(glex);
    this.service = service;
  }

  @Override
  public List<ASTCDMethod> decorate(ASTCDAttribute ast) {
    List<ASTCDMethod> methods = createSetter(ast);
    methods.add(createSetListMethod(ast));
    return methods;
  }

  protected ASTCDMethod createSetListMethod(ASTCDAttribute astcdAttribute) {
    if(getDecorationHelper().isAstNode(astcdAttribute)){

      //we need to check here if the astcdAttribute is a generated type or not
      //if it is a generated type, we need to use the generic version of the setList method, otherwise we can use the non-generic version
      //important here is also that we need to look at all types from imported grammars
      //the problem is that printType() cannot be resolved.

      //System.out.println("ASTCDAttribute: " + astcdAttribute.getMCType().printType());
      //service.getAllCDTypes().forEach(System.out::println);
      //
      //ASTCDAttribute: java.util.List<de.monticore.aggregation.blah._ast.ASTBlub>
      //
      //CDTypeSymbol{fullName='de.monticore.aggregation.Blah.ASTBlahModel', sourcePosition=<0,0>}
      //CDTypeSymbol{fullName='de.monticore.aggregation.Blah.ASTBlub', sourcePosition=<0,0>}
      //CDTypeSymbol{fullName='de.monticore.aggregation.Blah.ASTDummy', sourcePosition=<0,0>}
      //CDTypeSymbol{fullName='de.monticore.aggregation.Blah.BlahLiterals', sourcePosition=<0,0>}
      //CDTypeSymbol{fullName='de.monticore.aggregation.Blah.ASTBlahNode', sourcePosition=<0,0>}

      //the attribute has the _ast package and only lowercase packages.
      // while the types that can be resolved after are not lowercase restricted.

      //CDTypeSymbol cdTypeSymbol =  service.resolveCDType(astcdAttribute.getMCType().printType());

      //ast.getMCType().prettyPrint for example automata._ast.State

      //service.resolveCDType() or service.resolveCD() only find Automata.State



//      List<CDTypeSymbol> type = service.getAllCDTypes();
//      for(CDTypeSymbol t: type){
//        System.out.println(t.getFullName());
//      }
//      astService.resolveCDType(type.get(0).getFullName());
//      service.isInheritedAttribute(ast);
//
//      //error not found
//      //service.resolveCDType(getAttributeType(ast));
//
//      String s = getAttributeType(ast);
//      s = s.replace("_ast.", "");
//      String finalS = s;
//      // does not work everytime and is obviously bad design
//      List<CDTypeSymbol> type2 = type.stream().filter(typ -> typ.getFullName().equalsIgnoreCase(finalS)).collect(Collectors.toList());
//      System.out.println(s);
//
//      if(type2.size()!=1){
//        for(CDTypeSymbol t: type2){
//          System.out.println(t.getFullName());
//        }
//        throw new IllegalStateException();
//      }

//      boolean extendsAClass = false;
//      List<ASTMCObjectType> list34343 = type2.get(0).getAstNode().getSuperclassList();
//      for(ASTMCObjectType o: list34343){
//        System.out.println(o.printType());
//        extendsAClass = true;
//        List<ASTCDClass> list = CDSymbolTables.getTransitiveSuperClasses((ASTCDClass) type2.get(0).getAstNode());
//        System.out.println(list.size());
//      }

      //TODO magic here

      if(!getDecorationHelper().isListType(astcdAttribute.getMCType().printType()) && !(astcdAttribute.getMCType().getClass() == ASTMCListType.class)
      && ((ASTMCBasicGenericType) astcdAttribute.getMCType()).getMCTypeArgumentList().isEmpty() && ((ASTMCBasicGenericType) astcdAttribute.getMCType()).getMCTypeArgumentList().get(0).getMCTypeOpt().isEmpty()){
        Log.error(ERROR_CODE + " The attribute " + astcdAttribute.getName() + " is marked as AST node list but does not provide a generic type argument.");
      }
      ASTMCType mcType = ((ASTMCBasicGenericType)astcdAttribute.getMCType()).getMCTypeArgumentList().get(0).getMCTypeOpt().get();
      String signature = String.format(SET_LIST_GENERIC, capitalizedAttributeNameWithOutS, attributeType, astcdAttribute.getName());
      ASTCDMethod setListMethod = getCDMethodFacade().createMethodByDefinition(signature);
      this.replaceTemplate(EMPTY_BODY, setListMethod, new TemplateHookPoint("mc.methods.ListSetGeneric", astcdAttribute, mcType.printType(), ERROR_CODE));
      return setListMethod;
    }else{
      String signature = String.format(SET_LIST, capitalizedAttributeNameWithOutS, attributeType, astcdAttribute.getName());
      ASTCDMethod setListMethod = getCDMethodFacade().createMethodByDefinition(signature);
      this.replaceTemplate(EMPTY_BODY, setListMethod, new TemplateHookPoint("methods.Set", astcdAttribute));
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
