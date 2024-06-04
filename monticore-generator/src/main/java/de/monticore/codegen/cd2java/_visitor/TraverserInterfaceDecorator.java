/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._visitor;

import com.google.common.collect.Lists;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnum;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java.JavaDoc;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCListType;
import de.monticore.types.mccollectiontypes._ast.ASTMCOptionalType;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;

import java.util.*;
import java.util.stream.Collectors;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.CD2JavaTemplatesFix.JAVADOC;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.*;

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

    String traverserSimpleName = visitorService.getTraverserInterfaceSimpleName();
    
    // get visitor types and names of super cds and own cd
    List<DiagramSymbol> superCDsTransitive = visitorService.getSuperCDsTransitive();
    List<String> visitorFullNameList = superCDsTransitive.stream()
        .map(visitorService::getVisitor2FullName)
        .collect(Collectors.toList());
    visitorFullNameList.add(visitorService.getVisitor2FullName());
    
    // create list of cdDefinitions from superclass and own class
    List<ASTCDDefinition> definitionList = new ArrayList<>();
    definitionList.add(ast.getCDDefinition());
    definitionList.addAll(superCDsTransitive.stream().map(x -> (ASTCDDefinition)x.getAstNode()).collect(
        Collectors.toList()));

    List<String> visitorSimpleNameList = Lists.newArrayList(visitorService.getVisitorSimpleName());
    visitorSimpleNameList.addAll(superCDsTransitive.stream()
        .map(visitorService::getVisitorSimpleName)
        .collect(Collectors.toList()));

    List<ASTMCObjectType> superInterfaces = this.visitorService.getSuperTraverserInterfaces();
    if (superInterfaces.isEmpty()) {
      superInterfaces.add(getMCTypeFacade().createQualifiedType(ITRAVERSER_FULL_NAME));
    }

    ASTCDInterface visitorInterface = CD4CodeMill.cDInterfaceBuilder()
        .setName(traverserSimpleName)
        .setCDExtendUsage(CD4CodeMill.cDExtendUsageBuilder().addAllSuperclass(superInterfaces).build())
        .setModifier(PUBLIC.build())
        .addAllCDMembers(addVisitor2Methods(definitionList))
        .addAllCDMembers(addHanlderMethods(definitionList))
        .addAllCDMembers(createTraverserDelegatingMethods(ast.getCDDefinition()))
        .build();
    
    return visitorInterface;
  }

  /**
   * Adds the non-delegating handle method.
   * 
   * @param astType Type of the handled node
   * @param traverse Flag if the node should be traversed
   * @return The decorated handle method
   */
  protected ASTCDMethod addHandleMethod(ASTMCType astType, boolean traverse) {
    ASTCDMethod handleMethod = visitorService.getVisitorMethod(HANDLE, astType);
    this.replaceTemplate(EMPTY_BODY, handleMethod, new TemplateHookPoint(TRAVERSER_HANDLE_TEMPLATE, visitorService.getHandlerSimpleName(), traverse, false));
    this.replaceTemplate(JAVADOC, handleMethod,
            JavaDoc.of("NOTE: You are most likely looking for " +
                    "{@link de.monticore.ast.ASTNode#accept(ITraverser)} instead!").asHP());
    return handleMethod;
  }

  /**
   * Adds the non-delegating traverse method.
   * 
   * @param astType Type of the handled node
   * @param astcdClass The class, which attributes are traversed
   * @return The decorated traverse method
   */
  protected ASTCDMethod addTraversMethod(ASTMCType astType, ASTCDClass astcdClass) {
    ASTCDMethod traverseMethod = visitorService.getVisitorMethod(TRAVERSE, astType);
    boolean isScopeSpanningSymbol = symbolTableService.hasScopeStereotype(astcdClass.getModifier()) ||
        symbolTableService.hasInheritedScopeStereotype(astcdClass.getModifier());
    String handlerName = visitorService.getHandlerSimpleName();
    String topCast = isTop() ? "(" + visitorService.getTraverserInterfaceSimpleName() + ") " : "";
    this.replaceTemplate(EMPTY_BODY, traverseMethod, new TemplateHookPoint(TRAVERSER_TRAVERSE_TEMPLATE, astcdClass, isScopeSpanningSymbol, handlerName, topCast));
    return traverseMethod;
  }
  
  /**
   * Adds the getter and setter methods for the attached visitors.
   * 
   * @param definitionList List of class diagrams to retrieve available visitors
   * @return The decorated getter and setter methods
   */
  protected List<ASTCDMethod> addVisitor2Methods(List<ASTCDDefinition> definitionList) {
    // add setter and getter for created attribute in 'getVisitorAttributes'
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDDefinition cd : definitionList) {
      String simpleName = Names.getSimpleName(visitorService.getVisitorSimpleName(cd.getSymbol()));
      // add setter for visitor attribute
      // e.g. public void setAutomataVisitor(automata._visitor.AutomataVisitor2 automataVisitor)
      ASTMCQualifiedType visitorType = visitorService.getVisitor2Type(cd.getSymbol());
      ASTCDParameter visitorParameter = getCDParameterFacade().createParameter(visitorType, StringTransformations.uncapitalize(simpleName));
      ASTCDMethod addVisitorMethod = getCDMethodFacade().createMethod(PUBLIC.build(), "add4" + cd.getName(), visitorParameter);
      methodList.add(addVisitorMethod);

      // add getter for visitor attribute
      // e.g. public Optional<automata._visitor.AutomataVisitor2> getAutomataVisitor()
      ASTMCListType listVisitorType = getMCTypeFacade().createListTypeOf(visitorType);
      ASTCDMethod getVisitorsMethod = getCDMethodFacade().createMethod(PUBLIC.build(), listVisitorType, "get" + simpleName + "List");
      this.replaceTemplate(EMPTY_BODY, getVisitorsMethod, new StringHookPoint("return new ArrayList<>();"));
      methodList.add(getVisitorsMethod);
    }
    return methodList;
  }
  
  /**
   * Adds the getter and setter methods for the attached handlers.
   * 
   * @param definitionList List of class diagrams to retrieve available visitors
   * @return The decorated getter and setter methods
   */
  protected List<ASTCDMethod> addHanlderMethods(List<ASTCDDefinition> definitionList) {
    // add setter and getter for created attribute in 'getVisitorAttributes'
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDDefinition cd : definitionList) {
      String simpleName = Names.getSimpleName(visitorService.getHandlerSimpleName(cd.getSymbol()));
      // add setter for handler attribute
      // e.g. public void setAutomataHandler(automata._visitor.AutomataHandler automataHandler)
      ASTMCQualifiedType handlerType = visitorService.getHandlerType(cd.getSymbol());
      ASTCDParameter handlerParameter = getCDParameterFacade().createParameter(handlerType, StringTransformations.uncapitalize(simpleName));
      ASTCDMethod setVisitorMethod = getCDMethodFacade().createMethod(PUBLIC.build(), "set" + simpleName, handlerParameter);
      methodList.add(setVisitorMethod);

      // add getter for visitor attribute
      // e.g. public Optional<automata._visitor.AutomataHandler> getAutomataHandler()
      ASTMCOptionalType optionalHandlerType = getMCTypeFacade().createOptionalTypeOf(handlerType);
      ASTCDMethod getVisitorMethod = getCDMethodFacade().createMethod(PUBLIC.build(), optionalHandlerType, "get" + simpleName);
      this.replaceTemplate(EMPTY_BODY, getVisitorMethod, new StringHookPoint("return Optional.empty();"));
      methodList.add(getVisitorMethod);
    }
    return methodList;
  }
  
  /**
   * Controls the creation of all visitor related methods, such as visit,
   * endVisit, handle, and traverse for all visitable entities.
   * 
   * @param cdDefinition The input class diagram from which all visitable
   *          entities are derived
   * @return The decorated visitor methods
   */
  protected List<ASTCDMethod> createTraverserDelegatingMethods(ASTCDDefinition cdDefinition) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    String simpleVisitorName = visitorService.getVisitorSimpleName(cdDefinition.getSymbol());
    String packageName = visitorService.getASTPackage(cdDefinition.getSymbol());
    // add methods for classes, interfaces, enumerations, symbols, and scopes
    visitorMethods.addAll(createVisitorDelegatorClassMethods(cdDefinition.getCDClassesList(), packageName, simpleVisitorName));
    visitorMethods.addAll(createVisitorDelegatorInterfaceMethods(cdDefinition.getCDInterfacesList(), packageName, simpleVisitorName));
    visitorMethods.addAll(createVisitorDelegatorEnumMethods(cdDefinition.getCDEnumsList(), packageName, simpleVisitorName, cdDefinition.getName()));
    visitorMethods.addAll(createVisitorDelegatorSymbolMethods(cdDefinition, simpleVisitorName));
    visitorMethods.addAll(createVisitorDelegatorScopeMethods(cdDefinition, simpleVisitorName));
    
    return visitorMethods;
  }

  /**
   * Creates visit, endVisit, handle, and traverse methods for a list of
   * classes.
   * 
   * @param astcdClassList The input list of classes
   * @param simpleVisitorName The name of the visited entity
   * @return The decorated visitor methods
   */
  protected List<ASTCDMethod> createVisitorDelegatorClassMethods(List<ASTCDClass> astcdClassList,
                                                                 String packageName, String simpleVisitorName) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    for (ASTCDClass astcdClass : astcdClassList) {
      visitorMethods.addAll(createVisitorDelegatorClassMethod(astcdClass, packageName, simpleVisitorName));
    }
    return visitorMethods;
  }
  
  /**
   * Creates visit, endVisit, handle, and traverse methods for a given class.
   * 
   * @param astcdClass The input class
   * @param simpleVisitorName The name of the visited entity
   * @return The decorated visitor methods
   */
  protected List<ASTCDMethod> createVisitorDelegatorClassMethod(ASTCDClass astcdClass, String packageName, String simpleVisitorName) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    boolean doTraverse = !astcdClass.getModifier().isAbstract();
    ASTMCType classType = getMCTypeFacade().createQualifiedType(Joiners.DOT.join(packageName, astcdClass.getName()));
    
    // delegating visitor methods
    visitorMethods.add(addDelegatingMethod(classType, simpleVisitorName, VISIT));
    visitorMethods.add(addDelegatingMethod(classType, simpleVisitorName, END_VISIT));
    
    // non-delegating traverser methods
    visitorMethods.add(addHandleMethod(classType, doTraverse));
    if (doTraverse) {
      visitorMethods.add(addTraversMethod(classType, astcdClass));
    }
    
    return visitorMethods;
  }

  /**
   * Creates visit, endVisit, handle, and traverse methods for a list of
   * interfaces.
   * 
   * @param astcdInterfaceList The input list of interfaces
   * @param simpleVisitorName The name of the visited entity
   * @return The decorated visitor methods
   */
  protected List<ASTCDMethod> createVisitorDelegatorInterfaceMethods(List<ASTCDInterface> astcdInterfaceList,
                                                                     String packageName,
                                                                     String simpleVisitorName) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    for (ASTCDInterface astcdInterface : astcdInterfaceList) {
      visitorMethods.addAll(createVisitorDelegatorInterfaceMethod(astcdInterface, packageName, simpleVisitorName));
    }
    return visitorMethods;
  }

  /**
   * Creates visit, endVisit, handle, and traverse methods for a given
   * interface.
   * 
   * @param astcdInterface The input interface
   * @param simpleVisitorName The name of the visited entity
   * @return The decorated visitor methods
   */
  protected List<ASTCDMethod> createVisitorDelegatorInterfaceMethod(ASTCDInterface astcdInterface,
                                                                    String packageName,
                                                                    String simpleVisitorName) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    ASTMCType interfaceType = getMCTypeFacade().createQualifiedType(Joiners.DOT.join(packageName, astcdInterface.getName()));
    
    // delegating visitor methods
    visitorMethods.add(addDelegatingMethod(interfaceType, simpleVisitorName, VISIT));
    visitorMethods.add(addDelegatingMethod(interfaceType, simpleVisitorName, END_VISIT));
    
    // non-delegating traverser methods
    visitorMethods.add(addHandleMethod(interfaceType, false));
    
    return visitorMethods;
  }
  
  /**
   * Creates visit, endVisit, handle, and traverse methods for a list of
   * enumerations.
   * 
   * @param astcdEnumList The input list of enumerations
   * @param simpleVisitorName The name of the visited entity
   * @return The decorated visitor methods
   */
  protected List<ASTCDMethod> createVisitorDelegatorEnumMethods(List<ASTCDEnum> astcdEnumList, String packageName,
                                                                String simpleVisitorName,  String definitionName) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    for (ASTCDEnum astcdEnum : astcdEnumList) {
      if (!visitorService.isLiteralsEnum(astcdEnum, definitionName)) {
        visitorMethods.addAll(createVisitorDelegatorEnumMethod(astcdEnum, packageName, simpleVisitorName));
      }
    }
    return visitorMethods;
  }

  /**
   * Creates visit, endVisit, handle, and traverse methods for a given
   * enumeration.
   * 
   * @param astcdEnum The input enumeration
   * @param simpleVisitorName The name of the visited entity
   * @return The decorated visitor methods
   */
  protected List<ASTCDMethod> createVisitorDelegatorEnumMethod(ASTCDEnum astcdEnum, String packageName,
                                                               String simpleVisitorName) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    ASTMCType enumType = getMCTypeFacade().createQualifiedType(Joiners.DOT.join(packageName, astcdEnum.getName()));
    
    // delegating visitor methods
    visitorMethods.add(addDelegatingMethod(enumType, simpleVisitorName, VISIT));
    visitorMethods.add(addDelegatingMethod(enumType, simpleVisitorName, END_VISIT));
    
    // non-delegating traverser methods
    visitorMethods.add(addHandleMethod(enumType, false));
    
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
    visitorMethods.addAll(createVisitorDelegatorSymbolMethod(symbolTableService.getCommonSymbolInterfaceFullName(), simpleVisitorName));
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
    
    // delegating visitor methods
    visitorMethods.add(addDelegatingMethod(symbolType, simpleVisitorName, VISIT));
    visitorMethods.add(addDelegatingMethod(symbolType, simpleVisitorName, END_VISIT));
    
    // non-delegating traverser methods
    ASTCDMethod handleMethod = visitorService.getVisitorMethod(HANDLE, symbolType);
    this.replaceTemplate(EMPTY_BODY, handleMethod, new TemplateHookPoint(TRAVERSER_HANDLE_TEMPLATE, visitorService.getHandlerSimpleName(), true, true));
    this.replaceTemplate(JAVADOC, handleMethod,
            JavaDoc.of("NOTE: You are most likely looking for " +
                    "{@link de.monticore.ast.ASTNode#accept(ITraverser)} instead!").asHP());
    visitorMethods.add(handleMethod);
    visitorMethods.add(visitorService.getVisitorMethod(TRAVERSE, symbolType));
    
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
    DiagramSymbol cdSymbol = astcdDefinition.getSymbol();
    ASTMCQualifiedType scopeType = getMCTypeFacade().createQualifiedType(symbolTableService.getScopeInterfaceFullName(cdSymbol));
    ASTMCQualifiedType artifactScopeType = getMCTypeFacade().createQualifiedType(symbolTableService.getArtifactScopeInterfaceFullName(cdSymbol));
    ASTMCQualifiedType globalScopeType = getMCTypeFacade().createQualifiedType(symbolTableService.getGlobalScopeInterfaceFullName(cdSymbol));
    String handlerName = visitorService.getHandlerSimpleName();
    String scopeTypeAsString = scopeType.getMCQualifiedName().getQName();
    String topCast = isTop() ? "(" + visitorService.getTraverserInterfaceSimpleName() + ") " : "";
    
    TemplateHookPoint traverseSymbolsBody = new TemplateHookPoint(TRAVERSER_TRAVERSE_SCOPE_TEMPLATE, getSymbolsTransitive(), handlerName, scopeTypeAsString, topCast);
    StringHookPoint traverseDelegationBody = new StringHookPoint(TRAVERSE + "(("
        + symbolTableService.getScopeInterfaceFullName() + ") node);");
    
    visitorMethods.addAll(createVisitorDelegatorScopeMethod(scopeType, simpleVisitorName, traverseSymbolsBody));

    visitorMethods.addAll(createVisitorDelegatorScopeMethod(artifactScopeType, simpleVisitorName, traverseDelegationBody));

    visitorMethods.addAll(createVisitorDelegatorScopeMethod(globalScopeType, simpleVisitorName, traverseDelegationBody));

    return visitorMethods;
  }

  /**
   * Creates corresponding visit, endVisit, handle, and traverse methods for a
   * given scope name.
   * 
   * @param scopeType The qualified type of the input scope
   * @param simpleVisitorName The name of the delegated visitor
   * @param traverseBody body of the traverse method, provided in form of
   *          hookpoint
   * @return The corresponding visitor methods for the given scope
   */
  protected List<ASTCDMethod> createVisitorDelegatorScopeMethod(ASTMCType scopeType, String simpleVisitorName, HookPoint traverseBody) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    
    // delegating visitor methods
    visitorMethods.add(addDelegatingMethod(scopeType, simpleVisitorName, VISIT));
    visitorMethods.add(addDelegatingMethod(scopeType, simpleVisitorName, END_VISIT));
    
    // non-delegating traverser methods
    ASTCDMethod handleMethod = visitorService.getVisitorMethod(HANDLE, scopeType);
    this.replaceTemplate(EMPTY_BODY, handleMethod, new TemplateHookPoint(TRAVERSER_HANDLE_TEMPLATE, visitorService.getHandlerSimpleName(), true, true));
    this.replaceTemplate(JAVADOC, handleMethod,
            JavaDoc.of("NOTE: You are most likely looking for " +
                    "{@link de.monticore.ast.ASTNode#accept(ITraverser)} instead!").asHP());
    visitorMethods.add(handleMethod);
    ASTCDMethod traverseMethod = visitorService.getVisitorMethod(TRAVERSE, scopeType);
    visitorMethods.add(traverseMethod);
    this.replaceTemplate(EMPTY_BODY, traverseMethod, traverseBody);
    
    return visitorMethods;
  }

  /**
   * Creates a visitor method (e.g., visit and endVisit) that delegates to the
   * corresponding attached sub-visitor for the actual computation. Works for
   * all types of visitor methods as long as available in the target visitor.
   * 
   * @param astType The qualified type of the input entity
   * @param simpleVisitorName The name of the visitor
   * @param methodName The name of the method to create
   * @return The decorated method
   */
  protected ASTCDMethod addDelegatingMethod(ASTMCType astType, String simpleVisitorName, String methodName) {
    return addDelegatingMethod(astType, new ArrayList<>(Arrays.asList(simpleVisitorName)), methodName);
  }
  
  /**
   * Creates a visitor method (e.g., visit and endVisit) that delegates to the
   * corresponding attached sub-visitor for the actual computation. Works for
   * all types of visitor methods as long as available in the target visitor.
   * 
   * @param astType The qualified type of the input entity
   * @param simpleVisitorName A list of names for the visitors to generate
   * @param methodName The name of the method to create
   * @return The decorated method
   */
  protected ASTCDMethod addDelegatingMethod(ASTMCType astType, List<String> simpleVisitorName, String methodName) {
    ASTCDMethod visitorMethod = visitorService.getVisitorMethod(methodName, astType);
    this.replaceTemplate(EMPTY_BODY, visitorMethod, new TemplateHookPoint(
        VISITOR_METHODS_TRAVERSER_DELEGATING_TEMPLATE, simpleVisitorName, methodName));
    return visitorMethod;
  }

  
  /**
   * Returns a set of qualified symbol names. Considers the complete inheritance
   * hierarchy and thus, contains local symbols as well as inherited symbols.
   * 
   * @return The set of all qualified symbol names
   */
  protected Set<String> getSymbolsTransitive() {
    Set<String> superSymbolNames = new LinkedHashSet<>();
    // add local symbols
    superSymbolNames.addAll(symbolTableService.retrieveSymbolNamesFromCD(visitorService.getCDSymbol()));
    
    // add symbols of super CDs
    List<DiagramSymbol> superCDsTransitive = visitorService.getSuperCDsTransitive();
    for (DiagramSymbol cdSymbol : superCDsTransitive) {
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
