package de.monticore.codegen.cd2java._symboltable.symbol;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.se_rwth.commons.Names;

import java.util.List;

import static de.monticore.cd.facade.CDModifier.*;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;

/**
 * creates a SymbolLoader class from a grammar
 */
public class SymbolLoaderDecorator extends AbstractCreator<ASTCDClass, ASTCDClass> {

  protected SymbolTableService symbolTableService;

  protected MethodDecorator methodDecorator;

  protected static final String TEMPLATE_PATH = "_symboltable.symbolloader.";

  public SymbolLoaderDecorator(final GlobalExtensionManagement glex,
                               final SymbolTableService symbolTableService,
                               final MethodDecorator methodDecorator) {
    super(glex);
    this.methodDecorator = methodDecorator;
    this.symbolTableService = symbolTableService;
  }

  @Override
  public ASTCDClass decorate(ASTCDClass symbolInput) {
    String symbolLoaderSimpleName = symbolTableService.getSymbolLoaderSimpleName(symbolInput);
    String symbolFullName = symbolTableService.getSymbolFullName(symbolInput);
    String scopeInterfaceType = symbolTableService.getScopeInterfaceFullName();
    String simpleName = symbolInput.getName();

    ASTCDAttribute loadedSymbolAttribute = createLoadedSymbolAttribute(symbolFullName);
    ASTCDMethod loadedSymbolMethod = createLoadedSymbolMethod(symbolFullName, symbolLoaderSimpleName);

    ASTCDAttribute nameAttribute = createNameAttribute();
    List<ASTCDMethod> nameMethods = methodDecorator.getAccessorDecorator().decorate(nameAttribute);

    ASTCDAttribute enclosingScopeAttribute = createEnclosingScopeAttribute(scopeInterfaceType);
    List<ASTCDMethod> enclosingScopeMethods = methodDecorator.getAccessorDecorator().decorate(enclosingScopeAttribute);

    ASTCDClass symbolLoaderClass = CD4AnalysisMill.cDClassBuilder()
            .setName(symbolLoaderSimpleName)
            .setModifier(PUBLIC.build())
            .addInterface(getMCTypeFacade().createQualifiedType(I_SYMBOL_LOADER))
            .addCDConstructor(createConstructor(symbolLoaderSimpleName, scopeInterfaceType))
            .addCDAttribute(loadedSymbolAttribute)
            .addCDMethod(loadedSymbolMethod)
            .addCDAttribute(nameAttribute)
            .addAllCDMethods(nameMethods)
            .addCDAttribute(enclosingScopeAttribute)
            .addAllCDMethods(enclosingScopeMethods)
            .addCDAttribute(createIsAlreadyLoadedAttribute())
            .addCDMethod(createIsSymbolLoadedMethod())
            .addCDMethod(createLoadSymbolMethod(symbolLoaderSimpleName, symbolFullName, simpleName))
            .build();
    return symbolLoaderClass;
  }

  protected ASTCDConstructor createConstructor(String symbolLoaderClass, String scopeInterfaceType) {
    ASTCDParameter nameParameter = getCDParameterFacade().createParameter(String.class, NAME_VAR);
    ASTCDParameter enclosingScopeParameter = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(scopeInterfaceType), ENCLOSING_SCOPE_VAR);
    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), symbolLoaderClass, nameParameter, enclosingScopeParameter);
    this.replaceTemplate(EMPTY_BODY, constructor, new TemplateHookPoint(TEMPLATE_PATH + "Constructor"));
    return constructor;
  }

  protected ASTCDAttribute createNameAttribute() {
    return getCDAttributeFacade().createAttribute(PROTECTED, "String", "name");
  }

  protected ASTCDAttribute createEnclosingScopeAttribute(String scopeType) {
    return getCDAttributeFacade().createAttribute(PROTECTED, scopeType, "enclosingScope");
  }

  protected ASTCDAttribute createLoadedSymbolAttribute(String symbolType) {
    ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PROTECTED, getMCTypeFacade().createOptionalTypeOf(symbolType), "loadedSymbol");
    symbolTableService.addAttributeDefaultValues(attribute, glex);
    return attribute;
  }

  protected ASTCDAttribute createIsAlreadyLoadedAttribute() {
    return getCDAttributeFacade().createAttribute(PRIVATE, "boolean", "isAlreadyLoaded");
  }

  protected ASTCDMethod createLoadedSymbolMethod(String symbolType, String symbolLoaderName) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, getMCTypeFacade().createQualifiedType(symbolType),
            "getLoadedSymbol");
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(
            TEMPLATE_PATH + "GetSymbol", symbolLoaderName, Names.getSimpleName(symbolType)));
    return method;
  }

  protected ASTCDMethod createIsSymbolLoadedMethod() {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, getMCTypeFacade().createBooleanType(), "isSymbolLoaded");
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "IsSymbolLoaded"));
    return method;
  }

  protected ASTCDMethod createLoadSymbolMethod(String symbolLoaderName, String symbolName, String simpleName) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, getMCTypeFacade().createOptionalTypeOf(symbolName), "loadSymbol");
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "LoadSymbol", symbolLoaderName,
            symbolName, simpleName));
    return method;
  }

}
