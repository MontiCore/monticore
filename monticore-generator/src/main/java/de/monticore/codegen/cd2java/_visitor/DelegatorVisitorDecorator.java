package de.monticore.codegen.cd2java._visitor;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbolTOP;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._ast.MCBasicTypesMill;
import de.monticore.types.mccollectiontypes._ast.ASTMCOptionalType;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_INTERFACE;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.PRIVATE;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class DelegatorVisitorDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDClass> {

  private final VisitorService visitorService;

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
    String delegatorVisitorSimpleName = visitorService.getDelegatorVisitorSimpleTypeName();
    ASTMCQualifiedType visitorType = visitorService.getVisitorReferenceType();
    String simpleVisitorName = visitorService.getVisitorSimpleTypeName();

    // get visitor types and names of super cds and own cd
    List<CDDefinitionSymbol> superCDsTransitive = visitorService.getSuperCDsTransitive();

    List<String> visitorFullNameList = superCDsTransitive.stream()
        .map(visitorService::getVisitorFullTypeName)
        .collect(Collectors.toList());
    visitorFullNameList.add(visitorService.getVisitorFullTypeName());

    List<String> visitorSimpleNameList = superCDsTransitive.stream()
        .map(visitorService::getVisitorSimpleTypeName)
        .collect(Collectors.toList());
    visitorSimpleNameList.add(simpleVisitorName);

    // create list of cdDefinitions from superclass and own class
    List<ASTCDDefinition> definitionList = superCDsTransitive
        .stream()
        .map(CDDefinitionSymbolTOP::getAstNode)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(visitorService::calculateCDTypeNamesWithPackage)
        .collect(Collectors.toList());
    definitionList.add(compilationUnit.getCDDefinition());

    return CD4AnalysisMill.cDClassBuilder()
        .setName(delegatorVisitorSimpleName)
        .setModifier(PUBLIC.build())
        .addInterface(getCDTypeFacade().createQualifiedType(visitorService.getInheritanceVisitorSimpleTypeName()))
        .addCDAttribute(getRealThisAttribute(delegatorVisitorSimpleName))
        .addCDMethod(addGetRealThisMethod(delegatorVisitorSimpleName))
        .addCDMethod(addSetRealThisMethods(visitorType, delegatorVisitorSimpleName, simpleVisitorName))
        .addAllCDAttributes(getVisitorAttributes(visitorFullNameList))
        .addAllCDMethods(addVisitorMethods(visitorFullNameList))
        .addAllCDMethods(createVisitorDelegatorMethods(definitionList, simpleVisitorName))
        .addAllCDMethods(createVisitorDelegatorInterfaceMethod(getCDTypeFacade().createQualifiedType(visitorService.getLanguageInterfaceName()), simpleVisitorName))
        .addAllCDMethods(addASTNodeVisitorMethods(visitorSimpleNameList))
        .build();
  }


  protected ASTCDAttribute getRealThisAttribute(String delegatorVisitorSimpleName) {
    ASTCDAttribute realThisAttribute = getCDAttributeFacade().createAttribute(PRIVATE, delegatorVisitorSimpleName, REAL_THIS);
    this.replaceTemplate(VALUE, realThisAttribute, new StringHookPoint("= (" + delegatorVisitorSimpleName + ") this;"));
    return realThisAttribute;
  }

  protected ASTCDMethod addGetRealThisMethod(String delegatorVisitorSimpleName) {
    ASTMCQualifiedType visitorType = getCDTypeFacade().createQualifiedType(delegatorVisitorSimpleName);
    ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(visitorType).build();

    ASTCDMethod getRealThisMethod = this.getCDMethodFacade().createMethod(PUBLIC, returnType, GET_REAL_THIS);
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
        "_visitor.delegator.SetRealThis", delegatorVisitorSimpleName, simpleVisitorType,
        superVisitorNames));
    return getRealThisMethod;
  }

  protected List<ASTCDAttribute> getVisitorAttributes(List<String> fullVisitorNameList) {
    // generate a attribute for own visitor and all super visitors
    // e.g. private Optional<automata._visitor.AutomataVisitor> automataVisitor = Optional.empty();
    List<ASTCDAttribute> attributeList = new ArrayList<>();
    for (String fullName : fullVisitorNameList) {
      String simpleName = Names.getSimpleName(fullName);
      ASTCDAttribute visitorAttribute = getCDAttributeFacade().createAttribute(PRIVATE, getCDTypeFacade().createOptionalTypeOf(fullName),
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
      ASTMCQualifiedType visitorType = getCDTypeFacade().createQualifiedType(fullName);
      ASTCDParameter visitorParameter = getCDParameterFacade().createParameter(visitorType, StringTransformations.uncapitalize(simpleName));
      ASTCDMethod setVisitorMethod = getCDMethodFacade().createMethod(PUBLIC, "set" + simpleName, visitorParameter);
      this.replaceTemplate(EMPTY_BODY, setVisitorMethod, new TemplateHookPoint(
          "_visitor.delegator.SetVisitor", simpleName));
      methodList.add(setVisitorMethod);

      //add getter for visitor attribute
      // e.g. public Optional<automata._visitor.AutomataVisitor> getAutomataVisitor()
      ASTMCOptionalType optionalVisitorType = getCDTypeFacade().createOptionalTypeOf(visitorType);
      ASTMCReturnType returnType = MCBasicTypesMill.mCReturnTypeBuilder().setMCType(optionalVisitorType).build();
      ASTCDMethod getVisitorMethod = getCDMethodFacade().createMethod(PUBLIC, returnType, "get" + simpleName);
      this.replaceTemplate(EMPTY_BODY, getVisitorMethod,
          new StringHookPoint("return " + StringTransformations.uncapitalize(simpleName) + ";"));
      methodList.add(getVisitorMethod);
    }
    return methodList;
  }

  protected List<ASTCDMethod> createVisitorDelegatorMethods(List<ASTCDDefinition> definitionList, String simpleVisitorName) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    for (ASTCDDefinition astcdDefinition : definitionList) {
      visitorMethods.addAll(createVisitorDelegatorClassMethods(astcdDefinition.getCDClassList(), simpleVisitorName));
      visitorMethods.addAll(createVisitorDelegatorInterfaceMethods(astcdDefinition.getCDInterfaceList(), simpleVisitorName));
    }
    return visitorMethods;
  }

  protected List<ASTCDMethod> createVisitorDelegatorClassMethods(List<ASTCDClass> astcdClassList, String simpleVisitorName) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    for (ASTCDClass astcdClass : astcdClassList) {
      ASTMCType classType = getCDTypeFacade().createTypeByDefinition(astcdClass.getName());
      visitorMethods.addAll(createVisitorDelegatorClassMethod(classType, simpleVisitorName));
    }
    return visitorMethods;
  }

  protected List<ASTCDMethod> createVisitorDelegatorClassMethod(ASTMCType classType, String simpleVisitorName) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    visitorMethods.add(addVisitorMethod(classType, simpleVisitorName, VISIT));
    visitorMethods.add(addVisitorMethod(classType, simpleVisitorName, END_VISIT));
    visitorMethods.add(addVisitorMethod(classType, simpleVisitorName, HANDLE));
    visitorMethods.add(addVisitorMethod(classType, simpleVisitorName, TRAVERSE));
    return visitorMethods;
  }

  protected List<ASTCDMethod> createVisitorDelegatorInterfaceMethods(List<ASTCDInterface> astcdInterfaceList, String simpleVisitorName) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    for (ASTCDInterface astcdInterface : astcdInterfaceList) {
      ASTMCType interfaceType = getCDTypeFacade().createTypeByDefinition(astcdInterface.getName());
      visitorMethods.addAll(createVisitorDelegatorInterfaceMethod(interfaceType, simpleVisitorName));
    }
    return visitorMethods;
  }

  protected List<ASTCDMethod> createVisitorDelegatorInterfaceMethod(ASTMCType interfaceType, String simpleVisitorName) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
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
    ASTMCQualifiedType interfaceType = getCDTypeFacade().createQualifiedType(AST_INTERFACE);
    visitorMethods.add(addVisitorMethod(interfaceType, simpleVisitorNameList, VISIT));
    visitorMethods.add(addVisitorMethod(interfaceType, simpleVisitorNameList, END_VISIT));
    return visitorMethods;
  }

  protected ASTCDMethod addVisitorMethod(ASTMCType astType, List<String> simpleVisitorName, String methodName) {
    ASTCDMethod visitorMethod = visitorService.getVisitorMethod(methodName, astType);
    this.replaceTemplate(EMPTY_BODY, visitorMethod, new TemplateHookPoint(
        "_visitor.delegator.VisitorMethods", simpleVisitorName, methodName));
    return visitorMethod;
  }

}
