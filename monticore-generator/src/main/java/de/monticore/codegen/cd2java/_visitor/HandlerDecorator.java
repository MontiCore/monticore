/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._visitor;

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
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import de.se_rwth.commons.Joiners;

import java.util.*;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.cd.facade.CDModifier.PUBLIC_ABSTRACT;
import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.codegen.CD2JavaTemplatesFix.JAVADOC;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.*;

/**
 * creates a Visitor interface from a grammar
 */
public class HandlerDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDInterface> {
  
  protected final VisitorService visitorService;
  
  protected final SymbolTableService symbolTableService;
  
  protected boolean isTop;
  
  public HandlerDecorator(final GlobalExtensionManagement glex,
                             final VisitorService visitorService,
                             final SymbolTableService symbolTableService) {
    super(glex);
    this.visitorService = visitorService;
    this.symbolTableService = symbolTableService;
  }

  @Override
  public ASTCDInterface decorate(ASTCDCompilationUnit ast) {
    String handlerSimpleName = visitorService.getHandlerSimpleName();
    ASTMCQualifiedType traverserType = visitorService.getTraverserInterfaceType();

    ASTCDInterface visitorInterface = CD4CodeMill.cDInterfaceBuilder()
        .setName(handlerSimpleName)
        .setModifier(PUBLIC.build())
        .setCDExtendUsage(CD4CodeMill.cDExtendUsageBuilder()
                .addSuperclass(getMCTypeFacade().createQualifiedType(IHANDLER_FULL_NAME)).build())
        .addCDMember(addGetTraverserMethod(traverserType))
        .addCDMember(addSetTraverserMethod(traverserType))
        .addAllCDMembers(createHandlerMethods(ast.getCDDefinition()))
        .build();
    
    return visitorInterface;
  }

  /**
   * Adds the getter method for the traverser.
   * 
   * @param visitorType The return type of the method
   * @return The decorated getRealThis method
   */
  protected ASTCDMethod addGetTraverserMethod(ASTMCType visitorType) {
    ASTCDMethod getRealThisMethod = this.getCDMethodFacade().createMethod(PUBLIC_ABSTRACT.build(), visitorType, GET_TRAVERSER);
    return getRealThisMethod;
  }

  /**
   * Adds the setter method for the traverser.
   * 
   * @param visitorType The input parameter type
   * @return The decorated setRealThis method
   */
  protected ASTCDMethod addSetTraverserMethod(ASTMCType visitorType) {
    ASTCDParameter visitorParameter = getCDParameterFacade().createParameter(visitorType, TRAVERSER);
    ASTCDMethod setRealThis = this.getCDMethodFacade().createMethod(PUBLIC_ABSTRACT.build(), SET_TRAVERSER, visitorParameter);
    return setRealThis;
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
    this.replaceTemplate(EMPTY_BODY, handleMethod, new TemplateHookPoint(HANDLER_HANDLE_TEMPLATE, traverse));
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
    this.replaceTemplate(EMPTY_BODY, traverseMethod, new TemplateHookPoint(HANDLER_TRAVERSE_TEMPLATE, astcdClass, isScopeSpanningSymbol));
    return traverseMethod;
  }
  
  /**
   * Controls the creation of all visitor related methods, such as visit,
   * endVisit, handle, and traverse for all visitable entities.
   * 
   * @param cdDefinition The input class diagram from which all visitable
   *          entities are derived
   * @return The decorated visitor methods
   */
  protected List<ASTCDMethod> createHandlerMethods(ASTCDDefinition cdDefinition) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    String simpleVisitorName = visitorService.getVisitorSimpleName(cdDefinition.getSymbol());
    String packageName = visitorService.getASTPackage(cdDefinition.getSymbol());

    // add methods for classes, interfaces, enumerations, symbols, and scopes
    visitorMethods.addAll(createHandlerClassMethods(cdDefinition.getCDClassesList(), packageName, simpleVisitorName));
    visitorMethods.addAll(createHandlerInterfaceMethods(cdDefinition.getCDInterfacesList(),packageName,  simpleVisitorName));
    visitorMethods.addAll(createHandlerEnumMethods(cdDefinition.getCDEnumsList(), packageName, simpleVisitorName, cdDefinition.getName()));
    visitorMethods.addAll(createHandlerSymbolMethods(cdDefinition, simpleVisitorName));
    visitorMethods.addAll(createHandlerScopeMethods(cdDefinition, simpleVisitorName));
    
    return visitorMethods;
  }

  /**
   * Creates handle and traverse methods for a list of classes.
   * 
   * @param astcdClassList The input list of classes
   * @param simpleVisitorName The name of the visited entity
   * @return The decorated visitor methods
   */
  protected List<ASTCDMethod> createHandlerClassMethods(List<ASTCDClass> astcdClassList, String packageName, String simpleVisitorName) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    for (ASTCDClass astcdClass : astcdClassList) {
      visitorMethods.addAll(createHandlerClassMethod(astcdClass, packageName, simpleVisitorName));
    }
    return visitorMethods;
  }
  
  /**
   * Creates handle and traverse methods for a given class.
   * 
   * @param astcdClass The input class
   * @param simpleVisitorName The name of the visited entity
   * @return The decorated visitor methods
   */
  protected List<ASTCDMethod> createHandlerClassMethod(ASTCDClass astcdClass, String packageName, String simpleVisitorName) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    boolean doTraverse = !astcdClass.getModifier().isAbstract();
    ASTMCType classType = getMCTypeFacade().createQualifiedType(Joiners.DOT.join(packageName, astcdClass.getName()));
    
    // handle and traverser methods
    visitorMethods.add(addHandleMethod(classType, doTraverse));
    if (doTraverse) {
      visitorMethods.add(addTraversMethod(classType, astcdClass));
    }
    
    return visitorMethods;
  }

  /**
   * Creates the handle method for a list of interfaces.
   * 
   * @param astcdInterfaceList The input list of interfaces
   * @param simpleVisitorName The name of the visited entity
   * @return The decorated visitor methods
   */
  protected List<ASTCDMethod> createHandlerInterfaceMethods(List<ASTCDInterface> astcdInterfaceList, String packageName,
                                                            String simpleVisitorName) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    for (ASTCDInterface astcdInterface : astcdInterfaceList) {
      visitorMethods.addAll(createHandlerInterfaceMethod(astcdInterface, packageName, simpleVisitorName));
    }
    return visitorMethods;
  }

  /**
   * Creates the handle method for a given interface.
   * 
   * @param astcdInterface The input interface
   * @param simpleVisitorName The name of the visited entity
   * @return The decorated visitor methods
   */
  protected List<ASTCDMethod> createHandlerInterfaceMethod(ASTCDInterface astcdInterface, String packageName, String simpleVisitorName) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    ASTMCType interfaceType = getMCTypeFacade().createQualifiedType(Joiners.DOT.join(packageName, astcdInterface.getName()));
    
    // handle method
    visitorMethods.add(addHandleMethod(interfaceType, false));
    return visitorMethods;
  }
  
  /**
   * Creates the handle method for a list of enumerations.
   * 
   * @param astcdEnumList The input list of enumerations
   * @param simpleVisitorName The name of the visited entity
   * @return The decorated visitor methods
   */
  protected List<ASTCDMethod> createHandlerEnumMethods(List<ASTCDEnum> astcdEnumList, String packageName, String simpleVisitorName,  String definitionName) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    for (ASTCDEnum astcdEnum : astcdEnumList) {
      if (!visitorService.isLiteralsEnum(astcdEnum, definitionName)) {
        visitorMethods.addAll(createHandlerEnumMethod(astcdEnum, packageName, simpleVisitorName));
      }
    }
    return visitorMethods;
  }

  /**
   * Creates the handle method for a given enumeration.
   * 
   * @param astcdEnum The input enumeration
   * @param simpleVisitorName The name of the visited entity
   * @return The decorated visitor methods
   */
  protected List<ASTCDMethod> createHandlerEnumMethod(ASTCDEnum astcdEnum, String packageName, String simpleVisitorName) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    ASTMCType enumType = getMCTypeFacade().createQualifiedType(Joiners.DOT.join(packageName, astcdEnum.getName()));
    
    // handle method
    visitorMethods.add(addHandleMethod(enumType, false));
    return visitorMethods;
  }

  /**
   * Iterates over all defined symbols and creates corresponding handle and
   * traverse methods.
   * 
   * @param astcdDefinition The class diagram that contains the symbol
   *          definitions.
   * @param simpleVisitorName The name of the delegated visitor
   * @return The corresponding visitor methods for all symbols
   */
  protected List<ASTCDMethod> createHandlerSymbolMethods(ASTCDDefinition astcdDefinition, String simpleVisitorName) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    Set<String> symbolNames = symbolTableService.retrieveSymbolNamesFromCD(astcdDefinition.getSymbol());
    for (String symbolName : symbolNames) {
      visitorMethods.addAll(createHandlerSymbolMethod(symbolName, simpleVisitorName));
    }
    visitorMethods.addAll(createHandlerSymbolMethod(symbolTableService.getCommonSymbolInterfaceFullName(), simpleVisitorName));
    return visitorMethods;
  }

  /**
   * Creates corresponding handle and traverse methods for a given symbol name.
   * 
   * @param symbolName The qualified name of the input symbol
   * @param simpleVisitorName The name of the delegated visitor
   * @return The corresponding visitor methods for the given symbol
   */
  protected List<ASTCDMethod> createHandlerSymbolMethod(String symbolName, String simpleVisitorName) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    ASTMCQualifiedType symbolType = getMCTypeFacade().createQualifiedType(symbolName);
    
    // handle and traverser methods
    ASTCDMethod handleMethod = visitorService.getVisitorMethod(HANDLE, symbolType);
    this.replaceTemplate(EMPTY_BODY, handleMethod, new TemplateHookPoint(HANDLER_HANDLE_TEMPLATE, true));
    visitorMethods.add(handleMethod);
    visitorMethods.add(visitorService.getVisitorMethod(TRAVERSE, symbolType));
    
    return visitorMethods;
  }
  
  /**
   * Iterates over all defined scopes and creates corresponding handle and
   * traverse methods.
   * 
   * @param astcdDefinition The class diagram that contains the scope
   *          definitions.
   * @param simpleVisitorName The name of the delegated visitor
   * @return The corresponding visitor methods for all scopes
   */
  protected List<ASTCDMethod> createHandlerScopeMethods(ASTCDDefinition astcdDefinition, String simpleVisitorName) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    DiagramSymbol cdSymbol = astcdDefinition.getSymbol();
    ASTMCQualifiedType scopeType = getMCTypeFacade().createQualifiedType(symbolTableService.getScopeInterfaceFullName(cdSymbol));
    String scopeTypeAsString = scopeType.getMCQualifiedName().getQName();
    ASTMCQualifiedType artifactScopeType = getMCTypeFacade().createQualifiedType(symbolTableService.getArtifactScopeInterfaceFullName(cdSymbol));
    ASTMCQualifiedType globalScopeType = getMCTypeFacade().createQualifiedType(symbolTableService.getGlobalScopeInterfaceFullName(cdSymbol));

    TemplateHookPoint traverseSymbolsBody = new TemplateHookPoint(HANDLER_TRAVERSE_SCOPE_TEMPLATE, getSymbolsTransitive(), scopeTypeAsString);
    StringHookPoint traverseDelegationBody = new StringHookPoint(TRAVERSE + "(("
        + symbolTableService.getScopeInterfaceFullName() + ") node);");
    
    visitorMethods.addAll(createHandlerScopeMethod(scopeType, simpleVisitorName, traverseSymbolsBody));

    visitorMethods.addAll(createHandlerScopeMethod(artifactScopeType, simpleVisitorName, traverseDelegationBody));

    visitorMethods.addAll(createHandlerScopeMethod(globalScopeType, simpleVisitorName, traverseDelegationBody));

    return visitorMethods;
  }

  /**
   * Creates corresponding handle and traverse methods for a given scope name.
   * 
   * @param scopeType The qualified type of the input scope
   * @param simpleVisitorName The name of the delegated visitor
   * @param traverseBody body of the traverse method, provided in form of
   *          hookpoint
   * @return The corresponding visitor methods for the given scope
   */
  protected List<ASTCDMethod> createHandlerScopeMethod(ASTMCType scopeType, String simpleVisitorName, HookPoint traverseBody) {
    List<ASTCDMethod> visitorMethods = new ArrayList<>();
    
    // handle and traverser methods
    ASTCDMethod handleMethod = visitorService.getVisitorMethod(HANDLE, scopeType);
    this.replaceTemplate(EMPTY_BODY, handleMethod, new TemplateHookPoint(HANDLER_HANDLE_TEMPLATE, true));
    visitorMethods.add(handleMethod);
    ASTCDMethod traverseMethod = visitorService.getVisitorMethod(TRAVERSE, scopeType);
    visitorMethods.add(traverseMethod);
    this.replaceTemplate(EMPTY_BODY, traverseMethod, traverseBody);
    
    return visitorMethods;
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
