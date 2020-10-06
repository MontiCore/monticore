/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._visitor;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_INTERFACE;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.I_SCOPE;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.I_SYMBOL;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.END_VISIT;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.GET_REAL_THIS;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.HANDLE;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.HANDLE_TEMPLATE;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.REAL_THIS;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.SET_REAL_THIS;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.SET_VISITOR_DELEGATOR_TEMPLATE;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.TRAVERSE;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.TRAVERSE_SCOPE_TEMPLATE;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.TRAVERSE_TEMPLATE;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.VISIT;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.VISITOR_METHODS_DELEGATOR_TEMPLATE;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.VISITOR_METHODS_TRAVERSER_DELEGATING_TEMPLATE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDDefinition;
import de.monticore.cd.cd4analysis._ast.ASTCDEnum;
import de.monticore.cd.cd4analysis._ast.ASTCDInterface;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.cd.cd4analysis._ast.ASTCDParameter;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.cd4code.CD4CodeMill;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCOptionalType;
import de.monticore.types.prettyprint.MCSimpleGenericTypesPrettyPrinter;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;

/**
 * creates a Visitor interface from a grammar
 */
public class TraverserInterfaceDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDInterface> {
  
  protected final VisitorService visitorService;
  
  protected final SymbolTableService symbolTableService;
  
  protected boolean isTop;
  
  public TraverserInterfaceDecorator(final GlobalExtensionManagement glex,
                             final VisitorService visitorService,
                             final SymbolTableService symbolTableService) {
    super(glex);
    this.visitorService = visitorService;
    this.symbolTableService = symbolTableService;
  }

  @Override
  public ASTCDInterface decorate(ASTCDCompilationUnit ast) {
    ASTCDCompilationUnit compilationUnit = visitorService.calculateCDTypeNamesWithASTPackage(ast);
    
    String traverserSimpleName = visitorService.getTraverserInterfaceSimpleName();
    ASTMCQualifiedType traverserType = visitorService.getTraverserInterfaceType();
    
    // get visitor types and names of super cds and own cd
    List<CDDefinitionSymbol> superCDsTransitive = visitorService.getSuperCDsTransitive();
    List<String> visitorFullNameList = superCDsTransitive.stream()
        .map(visitorService::getVisitorFullName)
        .collect(Collectors.toList());
    visitorFullNameList.add(visitorService.getVisitorFullName());
    
    
    // create list of cdDefinitions from superclass and own class
    List<ASTCDDefinition> definitionList = new ArrayList<>();
    definitionList.add(compilationUnit.getCDDefinition());
    definitionList.addAll(superCDsTransitive
        .stream()
        .map(visitorService::calculateCDTypeNamesWithASTPackage)
        .collect(Collectors.toList()));
    
    List<String> visitorSimpleNameList =new ArrayList<>();
    visitorSimpleNameList.addAll(superCDsTransitive.stream()
        .map(visitorService::getVisitorSimpleName)
        .collect(Collectors.toList()));
    
    ASTMCType astNodeType = getMCTypeFacade().createQualifiedType(AST_INTERFACE);
    Set<String> symbolNames = symbolTableService.retrieveSymbolNamesFromCD(visitorService.getCDSymbol());

    ASTCDInterface visitorInterface = CD4CodeMill.cDInterfaceBuilder()
        .setName(traverserSimpleName)
        .addAllInterface(this.visitorService.getSuperVisitors())
        .setModifier(PUBLIC.build())
        .addCDMethod(addGetRealThisMethods(traverserType))
        .addCDMethod(addSetRealThisMethods(traverserType))
        .addAllCDMethods(addHandlerMethods(visitorFullNameList))
        .addAllCDMethods(createTraverserDelegatingMethods(definitionList))
        .addAllCDMethods(addDefaultVisitorMethods(visitorSimpleNameList))
        .build();
    

    // add visitor methods, but no double signatures
    List<ASTCDMethod> classMethods = addClassVisitorMethods(compilationUnit.getCDDefinition().getCDClassList());
    for (ASTCDMethod classMethod : classMethods) {
      if (!visitorService.isMethodAlreadyDefined(classMethod, visitorInterface.getCDMethodList())) {
        visitorInterface.addCDMethod(classMethod);
      }
    }
    List<ASTCDMethod> interfaceMethods = addInterfaceVisitorMethods(compilationUnit.getCDDefinition().getCDInterfaceList());
    for (ASTCDMethod interfaceMethod : interfaceMethods) {
      if (!visitorService.isMethodAlreadyDefined(interfaceMethod, visitorInterface.getCDMethodList())) {
        visitorInterface.addCDMethod(interfaceMethod);
      }
    }
    List<ASTCDMethod> enumMethods = addEnumVisitorMethods(compilationUnit.getCDDefinition().getCDEnumList(),
        compilationUnit.getCDDefinition().getName());
    for (ASTCDMethod enumMethod : enumMethods) {
      if (!visitorService.isMethodAlreadyDefined(enumMethod, visitorInterface.getCDMethodList())) {
        visitorInterface.addCDMethod(enumMethod);
      }
    }

    return visitorInterface;
  }

  protected ASTCDMethod addVisitASTNodeMethods(ASTMCType astNodeType) {
    return visitorService.getVisitorMethod(VisitorConstants.VISIT, astNodeType);
  }

  protected ASTCDMethod addEndVisitASTNodeMethods(ASTMCType astNodeType) {
    return visitorService.getVisitorMethod(VisitorConstants.END_VISIT, astNodeType);
  }

  protected ASTCDMethod addGetRealThisMethods(ASTMCType visitorType) {
    String hookPoint;
    if (!isTop()) {
      hookPoint = "return this;";
    } else {
      hookPoint = "return (" + visitorService.getVisitorSimpleName() + ")this;";
    }
    ASTCDMethod getRealThisMethod = this.getCDMethodFacade().createMethod(PUBLIC, visitorType, GET_REAL_THIS);
    this.replaceTemplate(EMPTY_BODY, getRealThisMethod, new StringHookPoint(hookPoint));
    return getRealThisMethod;
  }

  protected ASTCDMethod addSetRealThisMethods(ASTMCType visitorType) {
    ASTCDParameter visitorParameter = getCDParameterFacade().createParameter(visitorType, REAL_THIS);
    ASTCDMethod setRealThis = this.getCDMethodFacade().createMethod(PUBLIC, SET_REAL_THIS, visitorParameter);
    String generatedErrorCode = visitorService.getGeneratedErrorCode(visitorType.printType(
        new MCSimpleGenericTypesPrettyPrinter(new IndentPrinter())) + SET_REAL_THIS);
    this.replaceTemplate(EMPTY_BODY, setRealThis, new StringHookPoint(
        "    throw new UnsupportedOperationException(\"0xA7011"+generatedErrorCode+" The setter for realThis is " +
            "not implemented. You might want to implement a wrapper class to allow setting/getting realThis.\");\n"));
    return setRealThis;
  }

  protected List<ASTCDMethod> addClassVisitorMethods(List<ASTCDClass> astcdClassList) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    for (ASTCDClass astcdClass : astcdClassList) {
      boolean doTraverse = !(astcdClass.isPresentModifier() && astcdClass.getModifier().isAbstract());
      ASTMCType classType = getMCTypeFacade().createQualifiedType(astcdClass.getName());
      visitorMethods.add(addVisitMethod(classType));
      visitorMethods.add(addEndVisitMethod(classType));
      visitorMethods.add(addHandleMethod(classType, doTraverse));
      if (doTraverse){
        visitorMethods.add(addTraversMethod(classType, astcdClass));
      }
    }
    return visitorMethods;
  }

  protected List<ASTCDMethod> addEnumVisitorMethods(List<ASTCDEnum> astcdEnumList, String definitionName) {
    // no traverse method and not for literals Enum
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    for (ASTCDEnum astcdEnum : astcdEnumList) {
      if (!visitorService.isLiteralsEnum(astcdEnum, definitionName)) {
        ASTMCType enumType = getMCTypeFacade().createQualifiedType(astcdEnum.getName());
        visitorMethods.add(addVisitMethod(enumType));
        visitorMethods.add(addEndVisitMethod(enumType));
        visitorMethods.add(addHandleMethod(enumType, false));
      }
    }
    return visitorMethods;
  }

  protected List<ASTCDMethod> addInterfaceVisitorMethods(List<ASTCDInterface> astcdInterfaceList) {
    // no traverse method
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    for (ASTCDInterface astcdInterface : astcdInterfaceList) {
      ASTMCType interfaceType = getMCTypeFacade().createQualifiedType(astcdInterface.getName());
      visitorMethods.add(addVisitMethod(interfaceType));
      visitorMethods.add(addEndVisitMethod(interfaceType));
      visitorMethods.add(addHandleMethod(interfaceType, false));
    }
    return visitorMethods;
  }

  protected ASTCDMethod addVisitMethod(ASTMCType astType) {
    return visitorService.getVisitorMethod(VISIT, astType);
  }

  protected ASTCDMethod addEndVisitMethod(ASTMCType astType) {
    return visitorService.getVisitorMethod(END_VISIT, astType);
  }

  protected ASTCDMethod addHandleMethod(ASTMCType astType, boolean traverse) {
    ASTCDMethod handleMethod = visitorService.getVisitorMethod(HANDLE, astType);
    this.replaceTemplate(EMPTY_BODY, handleMethod, new TemplateHookPoint(HANDLE_TEMPLATE, traverse));
    return handleMethod;
  }

  protected ASTCDMethod addTraversMethod(ASTMCType astType, ASTCDClass astcdClass) {
    ASTCDMethod traverseMethod = visitorService.getVisitorMethod(TRAVERSE, astType);
    boolean isScopeSpanningSymbol = symbolTableService.hasScopeStereotype(astcdClass.getModifier()) ||
        symbolTableService.hasInheritedScopeStereotype(astcdClass.getModifier());
    this.replaceTemplate(EMPTY_BODY, traverseMethod, new TemplateHookPoint(TRAVERSE_TEMPLATE, astcdClass, isScopeSpanningSymbol));
    return traverseMethod;
  }
  
  protected List<ASTCDMethod> addHandlerMethods(List<String> fullVisitorNameList) {
    // add setter and getter for created attribute in 'getVisitorAttributes'
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (String fullName : fullVisitorNameList) {
      String simpleName = Names.getSimpleName(fullName);
      //add setter for visitor attribute
      //e.g. public void setAutomataVisitor(automata._visitor.AutomataVisitor AutomataVisitor)
      ASTMCQualifiedType visitorType = getMCTypeFacade().createQualifiedType(fullName);
      ASTCDParameter visitorParameter = getCDParameterFacade().createParameter(visitorType, StringTransformations.uncapitalize(simpleName));
      ASTCDMethod setVisitorMethod = getCDMethodFacade().createMethod(PUBLIC, "set" + simpleName, visitorParameter);
      methodList.add(setVisitorMethod);

      //add getter for visitor attribute
      // e.g. public Optional<automata._visitor.AutomataVisitor> getAutomataVisitor()
      ASTMCOptionalType optionalVisitorType = getMCTypeFacade().createOptionalTypeOf(visitorType);
      ASTCDMethod getVisitorMethod = getCDMethodFacade().createMethod(PUBLIC, optionalVisitorType, "get" + simpleName);
      this.replaceTemplate(EMPTY_BODY, getVisitorMethod, new StringHookPoint("return Optional.empty();"));
      methodList.add(getVisitorMethod);
    }
    return methodList;
  }
  
  protected List<ASTCDMethod> createTraverserMethods(List<ASTCDDefinition> definitionList) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    for (ASTCDDefinition astcdDefinition : definitionList) {
      String simpleVisitorName = visitorService.getVisitorSimpleName(astcdDefinition.getSymbol());
      visitorMethods.addAll(createVisitorDelegatorClassMethods(astcdDefinition.getCDClassList(), simpleVisitorName));
      visitorMethods.addAll(createVisitorDelegatorInterfaceMethods(astcdDefinition.getCDInterfaceList(), simpleVisitorName));
      visitorMethods.addAll(createVisitorDelegatorSymbolMethods(astcdDefinition, simpleVisitorName));
      visitorMethods.addAll(createVisitorDelegatorScopeMethods(astcdDefinition, simpleVisitorName));
    }
    return visitorMethods;
  }
  
  protected List<ASTCDMethod> createClassMethods(List<ASTCDDefinition> definitionList) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    for (ASTCDDefinition astcdDefinition : definitionList) {
      for (ASTCDClass astcdClass : astcdDefinition.getCDClassList()) {
        boolean doTraverse = !(astcdClass.isPresentModifier() && astcdClass.getModifier().isAbstract());
        ASTMCType classType = getMCTypeFacade().createQualifiedType(astcdClass.getName());
//        visitorMethods.add(addVisitMethod(classType));
//        visitorMethods.add(addEndVisitMethod(classType));
        visitorMethods.add(addHandleMethod(classType, doTraverse));
        if (doTraverse) {
          visitorMethods.add(addTraversMethod(classType, astcdClass));
        }
      }
    }
    return visitorMethods;
  }
  
  protected List<ASTCDMethod> createTraverserDelegatingMethods(List<ASTCDDefinition> definitionList) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    for (ASTCDDefinition astcdDefinition : definitionList) {
      String simpleVisitorName = visitorService.getVisitorSimpleName(astcdDefinition.getSymbol());
      visitorMethods.addAll(createVisitorDelegatorClassMethods(astcdDefinition.getCDClassList(), simpleVisitorName));
      visitorMethods.addAll(createVisitorDelegatorInterfaceMethods(astcdDefinition.getCDInterfaceList(), simpleVisitorName));
      visitorMethods.addAll(createVisitorDelegatorSymbolMethods(astcdDefinition, simpleVisitorName));
      visitorMethods.addAll(createVisitorDelegatorScopeMethods(astcdDefinition, simpleVisitorName));
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
    visitorMethods.add(addDelegatingMethod(classType, simpleVisitorName, VISIT));
    visitorMethods.add(addDelegatingMethod(classType, simpleVisitorName, END_VISIT));
    visitorMethods.add(addDelegatingMethod(classType, simpleVisitorName, HANDLE));
    if(astcdClass.isPresentModifier() && !astcdClass.getModifier().isAbstract()){
      visitorMethods.add(addDelegatingMethod(classType, simpleVisitorName, TRAVERSE));
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
    visitorMethods.add(addDelegatingMethod(interfaceType, simpleVisitorName, VISIT));
    visitorMethods.add(addDelegatingMethod(interfaceType, simpleVisitorName, END_VISIT));
    visitorMethods.add(addDelegatingMethod(interfaceType, simpleVisitorName, HANDLE));
    return visitorMethods;
  }

  /**
   * Iterates over all defined symbols and creates corresponding visit,
   * endVisit, handle, and traverse methods.
   * 
   * @param astcdDefinition The class diagram that contains the symbol
   *          definitions.
   * @param simpleVisitorName The name of the delegated visitor
   * @return The corresponding visitor methods for all symbols
   */
  protected List<ASTCDMethod> createVisitorDelegatorSymbolMethods(ASTCDDefinition astcdDefinition, String simpleVisitorName) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    Set<String> symbolNames = symbolTableService.retrieveSymbolNamesFromCD(astcdDefinition.getSymbol());
    for (String symbolName : symbolNames) {
      visitorMethods.addAll(createVisitorDelegatorSymbolMethod(symbolName, simpleVisitorName));
    }
    return visitorMethods;
  }

  /**
   * Creates corresponding visit, endVisit, handle, and traverse methods for a
   * given symbol name.
   * 
   * @param symbolName The qualified name of the input symbol
   * @param simpleVisitorName The name of the delegated visitor
   * @return The corresponding visitor methods for the given symbol
   */
  protected List<ASTCDMethod> createVisitorDelegatorSymbolMethod(String symbolName, String simpleVisitorName) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    ASTMCQualifiedType symbolType = getMCTypeFacade().createQualifiedType(symbolName);
    visitorMethods.add(addDelegatingMethod(symbolType, simpleVisitorName, VISIT));
    visitorMethods.add(addDelegatingMethod(symbolType, simpleVisitorName, END_VISIT));
    visitorMethods.add(addDelegatingMethod(symbolType, simpleVisitorName, HANDLE));
    visitorMethods.add(addDelegatingMethod(symbolType, simpleVisitorName, TRAVERSE));
    return visitorMethods;
  }

  /**
   * Iterates over all defined scopes and creates corresponding visit, endVisit,
   * handle, and traverse methods.
   * 
   * @param astcdDefinition The class diagram that contains the scope
   *          definitions.
   * @param simpleVisitorName The name of the delegated visitor
   * @return The corresponding visitor methods for all scopes
   */
  protected List<ASTCDMethod> createVisitorDelegatorScopeMethods(ASTCDDefinition astcdDefinition, String simpleVisitorName) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    CDDefinitionSymbol cdSymbol = astcdDefinition.getSymbol();
    ASTMCQualifiedType scopeType = getMCTypeFacade().createQualifiedType(symbolTableService.getScopeInterfaceFullName(cdSymbol));
    ASTMCQualifiedType artifactScopeType = getMCTypeFacade().createQualifiedType(symbolTableService.getArtifactScopeInterfaceFullName(cdSymbol));
    
    visitorMethods.addAll(createVisitorDelegatorScopeMethod(scopeType, simpleVisitorName));

    // only create artifact scope methods if grammar contains productions or
    // refers to a starting production of a super grammar
    if (symbolTableService.hasProd(astcdDefinition) || symbolTableService.hasStartProd(astcdDefinition)) {
      visitorMethods.addAll(createVisitorDelegatorScopeMethod(artifactScopeType, simpleVisitorName));
    }
    return visitorMethods;
  }

  /**
   * Creates corresponding visit, endVisit, handle, and traverse methods for a
   * given scope name.
   * 
   * @param scopeType The qualified type of the input scope
   * @param simpleVisitorName The name of the delegated visitor
   * @return The corresponding visitor methods for the given scope
   */
  protected List<ASTCDMethod> createVisitorDelegatorScopeMethod(ASTMCType scopeType, String simpleVisitorName) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    visitorMethods.add(addDelegatingMethod(scopeType, simpleVisitorName, VISIT));
    visitorMethods.add(addDelegatingMethod(scopeType, simpleVisitorName, END_VISIT));
    visitorMethods.add(addDelegatingMethod(scopeType, simpleVisitorName, HANDLE));
    visitorMethods.add(addDelegatingMethod(scopeType, simpleVisitorName, TRAVERSE));
    return visitorMethods;
  }

  protected ASTCDMethod addDelegatingMethod(ASTMCType astType, String simpleVisitorName, String methodName) {
    return addDelegatingMethod(astType, new ArrayList<>(Arrays.asList(simpleVisitorName)), methodName);
  }
  
  protected ASTCDMethod addDelegatingMethod(ASTMCType astType, List<String> simpleVisitorName, String methodName) {
    ASTCDMethod visitorMethod = visitorService.getVisitorMethod(methodName, astType);
    this.replaceTemplate(EMPTY_BODY, visitorMethod, new TemplateHookPoint(
        VISITOR_METHODS_TRAVERSER_DELEGATING_TEMPLATE, simpleVisitorName, methodName));
    return visitorMethod;
  }
  
  /**
   * Creates visit and endVisit methods for ASTNode, ISymbol, and IScope.
   * 
   * @param simpleVisitorNameList The list of all qualified (super) visitors
   * @return The corresponding visitor methods for default elements
   */
  protected List<ASTCDMethod> addDefaultVisitorMethods(List<String> simpleVisitorNameList) {
    // only visit and endVisit
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    ArrayList<String> reversedList = new ArrayList<>(simpleVisitorNameList);
    Collections.reverse(reversedList);
    
    // ASTNode methods
    ASTMCQualifiedType astInterfaceType = getMCTypeFacade().createQualifiedType(AST_INTERFACE);
    visitorMethods.add(addDelegatingMethod(astInterfaceType, simpleVisitorNameList, VISIT));
    visitorMethods.add(addDelegatingMethod(astInterfaceType, reversedList, END_VISIT));
    
    // ISymbol methods
    ASTMCQualifiedType symoblInterfaceType = getMCTypeFacade().createQualifiedType(I_SYMBOL);
    visitorMethods.add(addDelegatingMethod(symoblInterfaceType, simpleVisitorNameList, VISIT));
    visitorMethods.add(addDelegatingMethod(symoblInterfaceType, reversedList, END_VISIT));
    
    // IScope methods
    ASTMCQualifiedType scopeInterfaceType = getMCTypeFacade().createQualifiedType(I_SCOPE);
    visitorMethods.add(addDelegatingMethod(scopeInterfaceType, simpleVisitorNameList, VISIT));
    visitorMethods.add(addDelegatingMethod(scopeInterfaceType, reversedList, END_VISIT));
    
    return visitorMethods;
  }
  
  /**
   * Adds visit, endVisit, handle, and traverse methods for the general but
   * language specific symbol interface.
   * 
   * @return The corresponding visitor methods for the symbol interface
   */
  protected List<ASTCDMethod> addISymbolVisitorMethods() {
    List<ASTCDMethod> methodList = new ArrayList<>();
    ASTMCQualifiedType iScopeType = getMCTypeFacade().createQualifiedType(I_SYMBOL);
    methodList.add(visitorService.getVisitorMethod(VISIT, iScopeType));
    methodList.add(visitorService.getVisitorMethod(END_VISIT, iScopeType));
    return methodList;
  }
  
  /**
   * Adds visit, endVisit, handle, and traverse methods for a set of symbols.
   * 
   * @param symbolNames The input set of symbol names
   * @return Created visitor methods to visit the symbols
   */
  protected List<ASTCDMethod> addSymbolVisitorMethods(Set<String> symbolNames) {
    List<ASTCDMethod> visitorMethodList = new ArrayList<>();
    for (String symbolName : symbolNames) {
      ASTMCQualifiedType symbolType = getMCTypeFacade().createQualifiedType(symbolName);
      visitorMethodList.add(visitorService.getVisitorMethod(VISIT, symbolType));
      visitorMethodList.add(visitorService.getVisitorMethod(END_VISIT, symbolType));
      // add template for handle method
      ASTCDMethod handleMethod = visitorService.getVisitorMethod(HANDLE, symbolType);
      this.replaceTemplate(EMPTY_BODY, handleMethod, new TemplateHookPoint(HANDLE_TEMPLATE, true));
      visitorMethodList.add(handleMethod);
      visitorMethodList.add(visitorService.getVisitorMethod(TRAVERSE, symbolType));
    }
    return visitorMethodList;
  }
  
  /**
   * Adds visit, endVisit, handle, and traverse methods for the general but
   * language specific scope interface.
   * 
   * @return The corresponding visitor methods for the scope interface
   */
  protected List<ASTCDMethod> addIScopeVisitorMethods() {
    List<ASTCDMethod> methodList = new ArrayList<>();
    ASTMCQualifiedType iScopeType = getMCTypeFacade().createQualifiedType(I_SCOPE);
    methodList.add(visitorService.getVisitorMethod(VISIT, iScopeType));
    methodList.add(visitorService.getVisitorMethod(END_VISIT, iScopeType));
    return methodList;
  }
  
  /**
   * Adds visit, endVisit, handle, and traverse methods for scope, scope interface, and artifact
   * scope of a given class diagram definition by delegating the respective
   * names to createScopeVisitorMethods().
   * 
   * @param symbolsNameList The symbols to traverse
   * @param astcdDefinition The input class diagram
   * @return Created visitor methods to visit a (artifact) scope
   */
  protected List<ASTCDMethod> addScopeVisitorMethods(Set<String> symbolsNameList, ASTCDDefinition astcdDefinition) {
    ASTMCQualifiedType scopeType = symbolTableService.getScopeInterfaceType();
    ASTMCQualifiedType artifactScopeType = symbolTableService.getArtifactScopeInterfaceType();

    TemplateHookPoint traverseSymbolsBody = new TemplateHookPoint(TRAVERSE_SCOPE_TEMPLATE, symbolsNameList);
    StringHookPoint traverseDelegationBody = new StringHookPoint(TRAVERSE + "(("
        + symbolTableService.getScopeInterfaceFullName() + ") node);");

    List<ASTCDMethod> methodList = new ArrayList<>();
    methodList.addAll(createScopeVisitorMethods(scopeType, traverseSymbolsBody));
    // only create artifact scope methods if grammar contains productions or
    // refers to a starting production of a super grammar
    if (symbolTableService.hasProd(astcdDefinition) || symbolTableService.hasStartProd()) {
      methodList.addAll(createScopeVisitorMethods(artifactScopeType, traverseDelegationBody));
    }
    return methodList;
  }
  
  /**
   * Creates visit, endVisit, handle, and traverse methods for a given scope.
   * 
   * @param scopeName The scope name
   * @param traverseBody body of the traverse method, provided in form of hookpoint
   * @return A List of created methods to visit a scope
   */
  protected List<ASTCDMethod> createScopeVisitorMethods(ASTMCType scopeName, HookPoint traverseBody) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    methodList.add(visitorService.getVisitorMethod(VISIT, scopeName));
    methodList.add(visitorService.getVisitorMethod(END_VISIT, scopeName));
    ASTCDMethod handleMethod = visitorService.getVisitorMethod(HANDLE, scopeName);
    this.replaceTemplate(EMPTY_BODY, handleMethod, new TemplateHookPoint(HANDLE_TEMPLATE, true));
    methodList.add(handleMethod);
    ASTCDMethod traverseMethod = visitorService.getVisitorMethod(TRAVERSE, scopeName);
    methodList.add(traverseMethod);
    this.replaceTemplate(EMPTY_BODY, traverseMethod, traverseBody);
    return methodList;
  }
  
  /**
   * Returns a set of qualified symbol names. Considers the complete inheritance
   * hierarchy and thus, contains local symbols as well as inherited symbols.
   * 
   * @return The set of all qualified symbol names
   */
  protected Set<String> getSymbolsTransitive() {
    Set<String> superSymbolNames = new HashSet<String>();
    // add local symbols
    superSymbolNames.addAll(symbolTableService.retrieveSymbolNamesFromCD(visitorService.getCDSymbol()));
    
    // add symbols of super CDs
    List<CDDefinitionSymbol> superCDsTransitive = visitorService.getSuperCDsTransitive();
    for (CDDefinitionSymbol cdSymbol : superCDsTransitive) {
      superSymbolNames.addAll(symbolTableService.retrieveSymbolNamesFromCD(cdSymbol));
    }
    return superSymbolNames;
  }
  
  public boolean isTop() {
    return isTop;
  }

  public void setTop(boolean top) {
    isTop = top;
  }
}
