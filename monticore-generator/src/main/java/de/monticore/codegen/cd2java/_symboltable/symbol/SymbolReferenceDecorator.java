package de.monticore.codegen.cd2java._symboltable.symbol;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.se_rwth.commons.StringTransformations;

import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.PRIVATE;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

/**
 * creates a SymbolReference class from a grammar
 */
public class SymbolReferenceDecorator extends AbstractCreator<ASTCDClass, ASTCDClass> {

  protected SymbolTableService symbolTableService;

  protected MethodDecorator methodDecorator;

  protected static final String TEMPLATE_PATH = "_symboltable.symbolreference.";

  public SymbolReferenceDecorator(final GlobalExtensionManagement glex,
                                  final SymbolTableService symbolTableService,
                                  final MethodDecorator methodDecorator) {
    super(glex);
    this.methodDecorator = methodDecorator;
    this.symbolTableService = symbolTableService;
  }

  @Override
  public ASTCDClass decorate(ASTCDClass symbolInput) {
    String symbolReferenceClassSimpleName = symbolTableService.getSymbolReferenceClassSimpleName(symbolInput);
    String symbolFullName = symbolTableService.getSymbolFullName(symbolInput);
    String scopeInterfaceType = symbolTableService.getScopeInterfaceFullName();
    String simpleName = symbolInput.getName();

    ASTCDAttribute referencedSymbolAttribute = createReferencedSymbolAttribute(symbolFullName);
    ASTCDMethod referencedSymbolMethod = createReferencedSymbolMethod(referencedSymbolAttribute, symbolReferenceClassSimpleName);

    ASTCDAttribute nameAttribute = createNameAttribute();
    List<ASTCDMethod> nameMethods = methodDecorator.getAccessorDecorator().decorate(nameAttribute);

    ASTCDAttribute enclosingScopeAttribute = createEnclosingScopeAttribute(scopeInterfaceType);
    List<ASTCDMethod> enclosingScopeMethods = methodDecorator.getAccessorDecorator().decorate(enclosingScopeAttribute);

    ASTCDAttribute isAlreadyLoadedAttribute = createisAlreadyLoadedAttribute();

    ASTCDClass symbolReferenceClass = CD4AnalysisMill.cDClassBuilder()
            .setName(symbolReferenceClassSimpleName)
            .setModifier(PUBLIC.build())
            .addInterface(getMCTypeFacade().createQualifiedType(I_SYMBOL_REFERENCE))
            .addCDConstructor(createConstructor(symbolReferenceClassSimpleName, scopeInterfaceType))
            .addCDAttribute(referencedSymbolAttribute)
            .addCDMethod(referencedSymbolMethod)
            .addCDAttribute(nameAttribute)
            .addAllCDMethods(nameMethods)
            .addCDAttribute(enclosingScopeAttribute)
            .addAllCDMethods(enclosingScopeMethods)
            .addCDAttribute(createisAlreadyLoadedAttribute())
            .addCDMethod(createIsReferencedSymbolLoadedMethod())
            .addCDMethod(createLoadReferencedSymbolMethod(symbolReferenceClassSimpleName, symbolFullName, simpleName))
            .build();
    return symbolReferenceClass;
  }

  protected ASTCDConstructor createConstructor(String symbolReferenceClass, String scopeInterfaceType) {
    ASTCDParameter nameParameter = getCDParameterFacade().createParameter(String.class, NAME_VAR);
    ASTCDParameter enclosingScopeParameter = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(scopeInterfaceType), ENCLOSING_SCOPE_VAR);
    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), symbolReferenceClass, nameParameter, enclosingScopeParameter);
    this.replaceTemplate(EMPTY_BODY, constructor, new TemplateHookPoint(TEMPLATE_PATH + "Constructor"));
    return constructor;
  }

  protected ASTCDAttribute createNameAttribute() {
    return getCDAttributeFacade().createAttribute(PROTECTED, "String", "name");
  }

  protected ASTCDAttribute createEnclosingScopeAttribute(String scopeType) {
    return getCDAttributeFacade().createAttribute(PROTECTED, scopeType, "enclosingScope");
  }

  protected ASTCDAttribute createReferencedSymbolAttribute(String symbolType) {
    return getCDAttributeFacade().createAttribute(PROTECTED, symbolType, "loadedSymbol");
  }

  protected ASTCDAttribute createisAlreadyLoadedAttribute() {
    return getCDAttributeFacade().createAttribute(PRIVATE, "boolean", "isAlreadyLoaded");
  }

  protected ASTCDMethod createReferencedSymbolMethod(ASTCDAttribute referencedSymbolAttribute, String symbolReferenceName) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, referencedSymbolAttribute.getMCType(),
            "get" + StringTransformations.capitalize(referencedSymbolAttribute.getName()));
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(
            TEMPLATE_PATH + "GetReferencedSymbol", symbolReferenceName, referencedSymbolAttribute.printType()));
    return method;
  }

  protected ASTCDMethod createIsReferencedSymbolLoadedMethod() {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, getMCTypeFacade().createBooleanType(), "isSymbolLoaded");
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return isAlreadyLoaded;"));
    return method;
  }

  protected ASTCDMethod createLoadReferencedSymbolMethod(String symbolReferenceName, String symbolName, String simpleName) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, getMCTypeFacade().createOptionalTypeOf(symbolName), "loadSymbol");
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "LoadReferencedSymbol", symbolReferenceName,
            symbolName, simpleName));
    return method;
  }

}
