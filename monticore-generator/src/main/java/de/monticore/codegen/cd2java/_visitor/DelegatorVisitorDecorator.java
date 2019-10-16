/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._visitor;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.cd4code._ast.CD4CodeMill;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCOptionalType;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_INTERFACE;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.PRIVATE;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class DelegatorVisitorDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDClass> {

  protected final VisitorService visitorService;

  public DelegatorVisitorDecorator(final GlobalExtensionManagement glex,
                                   final VisitorService visitorService) {
    super(glex);
    this.visitorService = visitorService;
  }

  @Override
  public ASTCDClass decorate(ASTCDCompilationUnit input) {
    // change class names to qualified name
    ASTCDCompilationUnit compilationUnit = visitorService.calculateCDTypeNamesWithPackage(input);
    // get visitor names of current node
    String delegatorVisitorSimpleName = visitorService.getDelegatorVisitorSimpleName();
    ASTMCQualifiedType visitorType = visitorService.getVisitorType();
    String simpleVisitorName = visitorService.getVisitorSimpleName();

    // get visitor types and names of super cds and own cd
    List<CDDefinitionSymbol> superCDsTransitive = visitorService.getSuperCDsTransitive();

    List<String> visitorFullNameList = superCDsTransitive.stream()
        .map(visitorService::getVisitorFullName)
        .collect(Collectors.toList());
    visitorFullNameList.add(visitorService.getVisitorFullName());

    List<String> visitorSimpleNameList =new ArrayList<>();
    visitorSimpleNameList.add(simpleVisitorName);
    visitorSimpleNameList.addAll(superCDsTransitive.stream()
        .map(visitorService::getVisitorSimpleName)
        .collect(Collectors.toList()));

    // create list of cdDefinitions from superclass and own class
    List<ASTCDDefinition> definitionList = new ArrayList<>();
    definitionList.add(compilationUnit.getCDDefinition());
    definitionList.addAll(superCDsTransitive
        .stream()
        .map(visitorService::calculateCDTypeNamesWithPackage)
        .collect(Collectors.toList()));

    return CD4CodeMill.cDClassBuilder()
        .setName(delegatorVisitorSimpleName)
        .setModifier(PUBLIC.build())
        .addInterface(getMCTypeFacade().createQualifiedType(visitorService.getInheritanceVisitorSimpleName()))
        .addCDAttribute(getRealThisAttribute(delegatorVisitorSimpleName))
        .addCDMethod(addGetRealThisMethod(delegatorVisitorSimpleName))
        .addCDMethod(addSetRealThisMethods(visitorType, delegatorVisitorSimpleName, simpleVisitorName))
        .addAllCDAttributes(getVisitorAttributes(visitorFullNameList))
        .addAllCDMethods(addVisitorMethods(visitorFullNameList))
        .addAllCDMethods(createVisitorDelegatorMethods(definitionList))
        .addAllCDMethods(addASTNodeVisitorMethods(visitorSimpleNameList))
        .build();
  }


  protected ASTCDAttribute getRealThisAttribute(String delegatorVisitorSimpleName) {
    ASTCDAttribute realThisAttribute = getCDAttributeFacade().createAttribute(PRIVATE, delegatorVisitorSimpleName, REAL_THIS);
    this.replaceTemplate(VALUE, realThisAttribute, new StringHookPoint("= (" + delegatorVisitorSimpleName + ") this;"));
    return realThisAttribute;
  }

  protected ASTCDMethod addGetRealThisMethod(String delegatorVisitorSimpleName) {
    ASTMCQualifiedType visitorType = getMCTypeFacade().createQualifiedType(delegatorVisitorSimpleName);

    ASTCDMethod getRealThisMethod = this.getCDMethodFacade().createMethod(PUBLIC, visitorType, GET_REAL_THIS);
    this.replaceTemplate(EMPTY_BODY, getRealThisMethod, new StringHookPoint("return realThis;"));
    return getRealThisMethod;
  }

  protected ASTCDMethod addSetRealThisMethods(ASTMCType visitorType, String delegatorVisitorSimpleName,
                                              String simpleVisitorType) {
    ASTCDParameter visitorParameter = getCDParameterFacade().createParameter(visitorType, "realThis");

    List<ASTMCQualifiedType> superVisitors = visitorService.getSuperVisitors();
    List<String> superVisitorNames = superVisitors
        .stream()
        .map(ASTMCType::printType)
        .filter(s -> s.contains("."))
        .map(s -> s = s.substring(s.lastIndexOf(".") + 1))
        .collect(Collectors.toList());

    ASTCDMethod getRealThisMethod = this.getCDMethodFacade().createMethod(PUBLIC, SET_REAL_THIS, visitorParameter);
    this.replaceTemplate(EMPTY_BODY, getRealThisMethod, new TemplateHookPoint(
        SET_REAL_THIS_DELEGATOR_TEMPLATE, delegatorVisitorSimpleName, simpleVisitorType,
        superVisitorNames));
    return getRealThisMethod;
  }

  protected List<ASTCDAttribute> getVisitorAttributes(List<String> fullVisitorNameList) {
    // generate a attribute for own visitor and all super visitors
    // e.g. private Optional<automata._visitor.AutomataVisitor> automataVisitor = Optional.empty();
    List<ASTCDAttribute> attributeList = new ArrayList<>();
    for (String fullName : fullVisitorNameList) {
      String simpleName = Names.getSimpleName(fullName);
      ASTCDAttribute visitorAttribute = getCDAttributeFacade().createAttribute(PRIVATE, getMCTypeFacade().createOptionalTypeOf(fullName),
          StringTransformations.uncapitalize(simpleName));
      this.replaceTemplate(VALUE, visitorAttribute, new StringHookPoint("= Optional.empty();"));
      attributeList.add(visitorAttribute);
    }
    return attributeList;
  }

  protected List<ASTCDMethod> addVisitorMethods(List<String> fullVisitorNameList) {
    // add setter and getter for created attribute in 'getVisitorAttributes'
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (String fullName : fullVisitorNameList) {
      String simpleName = Names.getSimpleName(fullName);
      //add setter for visitor attribute
      //e.g. public void setAutomataVisitor(automata._visitor.AutomataVisitor AutomataVisitor)
      ASTMCQualifiedType visitorType = getMCTypeFacade().createQualifiedType(fullName);
      ASTCDParameter visitorParameter = getCDParameterFacade().createParameter(visitorType, StringTransformations.uncapitalize(simpleName));
      ASTCDMethod setVisitorMethod = getCDMethodFacade().createMethod(PUBLIC, "set" + simpleName, visitorParameter);
      this.replaceTemplate(EMPTY_BODY, setVisitorMethod, new TemplateHookPoint(
          SET_VISITOR_DELEGATOR_TEMPLATE, simpleName));
      methodList.add(setVisitorMethod);

      //add getter for visitor attribute
      // e.g. public Optional<automata._visitor.AutomataVisitor> getAutomataVisitor()
      ASTMCOptionalType optionalVisitorType = getMCTypeFacade().createOptionalTypeOf(visitorType);
      ASTCDMethod getVisitorMethod = getCDMethodFacade().createMethod(PUBLIC, optionalVisitorType, "get" + simpleName);
      this.replaceTemplate(EMPTY_BODY, getVisitorMethod,
          new StringHookPoint("return " + StringTransformations.uncapitalize(simpleName) + ";"));
      methodList.add(getVisitorMethod);
    }
    return methodList;
  }

  protected List<ASTCDMethod> createVisitorDelegatorMethods(List<ASTCDDefinition> definitionList) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    for (ASTCDDefinition astcdDefinition : definitionList) {
      String simpleVisitorName = visitorService.getVisitorSimpleName(astcdDefinition.getSymbol());
      visitorMethods.addAll(createVisitorDelegatorClassMethods(astcdDefinition.getCDClassList(), simpleVisitorName));
      visitorMethods.addAll(createVisitorDelegatorInterfaceMethods(astcdDefinition.getCDInterfaceList(), simpleVisitorName));
    }
    return visitorMethods;
  }

  protected List<ASTCDMethod> createVisitorDelegatorClassMethods(List<ASTCDClass> astcdClassList, String simpleVisitorName) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    for (ASTCDClass astcdClass : astcdClassList) {
      visitorMethods.addAll(createVisitorDelegatorClassMethod(astcdClass, simpleVisitorName));
    }
    return visitorMethods;
  }

  protected List<ASTCDMethod> createVisitorDelegatorClassMethod(ASTCDClass astcdClass, String simpleVisitorName) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    ASTMCType classType = getMCTypeFacade().createQualifiedType(astcdClass.getName());
    visitorMethods.add(addVisitorMethod(classType, simpleVisitorName, VISIT));
    visitorMethods.add(addVisitorMethod(classType, simpleVisitorName, END_VISIT));
    visitorMethods.add(addVisitorMethod(classType, simpleVisitorName, HANDLE));
    if(astcdClass.isPresentModifier() && !astcdClass.getModifier().isAbstract()){
      visitorMethods.add(addVisitorMethod(classType, simpleVisitorName, TRAVERSE));
    }
    return visitorMethods;
  }

  protected List<ASTCDMethod> createVisitorDelegatorInterfaceMethods(List<ASTCDInterface> astcdInterfaceList, String simpleVisitorName) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    for (ASTCDInterface astcdInterface : astcdInterfaceList) {
      visitorMethods.addAll(createVisitorDelegatorInterfaceMethod(astcdInterface, simpleVisitorName));
    }
    return visitorMethods;
  }

  protected List<ASTCDMethod> createVisitorDelegatorInterfaceMethod(ASTCDInterface astcdInterface, String simpleVisitorName) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    ASTMCType interfaceType = getMCTypeFacade().createQualifiedType(astcdInterface.getName());
    visitorMethods.add(addVisitorMethod(interfaceType, simpleVisitorName, VISIT));
    visitorMethods.add(addVisitorMethod(interfaceType, simpleVisitorName, END_VISIT));
    visitorMethods.add(addVisitorMethod(interfaceType, simpleVisitorName, HANDLE));
    return visitorMethods;
  }

  protected ASTCDMethod addVisitorMethod(ASTMCType astType, String simpleVisitorName, String methodName) {
    return addVisitorMethod(astType, new ArrayList<>(Arrays.asList(simpleVisitorName)), methodName);
  }

  protected List<ASTCDMethod> addASTNodeVisitorMethods(List<String> simpleVisitorNameList) {
    //only visit and endVisit
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    ASTMCQualifiedType interfaceType = getMCTypeFacade().createQualifiedType(AST_INTERFACE);
    visitorMethods.add(addVisitorMethod(interfaceType, simpleVisitorNameList, VISIT));
    ArrayList<String> reversedList = new ArrayList<>(simpleVisitorNameList);
    Collections.reverse(reversedList);
    visitorMethods.add(addVisitorMethod(interfaceType, reversedList, END_VISIT));
    return visitorMethods;
  }

  protected ASTCDMethod addVisitorMethod(ASTMCType astType, List<String> simpleVisitorName, String methodName) {
    ASTCDMethod visitorMethod = visitorService.getVisitorMethod(methodName, astType);
    this.replaceTemplate(EMPTY_BODY, visitorMethod, new TemplateHookPoint(
        VISITOR_METHODS_DELEGATOR_TEMPLATE, simpleVisitorName, methodName));
    return visitorMethod;
  }

}
