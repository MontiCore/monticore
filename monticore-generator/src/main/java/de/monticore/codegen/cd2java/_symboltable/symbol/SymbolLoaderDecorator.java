/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.symbol;

import de.monticore.cd.cd4analysis.CD4AnalysisMill;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._symboltable.symbol.symbolloadermutator.MandatoryMutatorSymbolLoaderDecorator;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;

import java.util.List;

import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.NAME_VAR;

/**
 * creates a SymbolLoader class from a grammar
 */
public class SymbolLoaderDecorator extends AbstractCreator<ASTCDClass, ASTCDClass> {

  protected SymbolTableService symbolTableService;

  protected MethodDecorator methodDecorator;

  protected MandatoryMutatorSymbolLoaderDecorator symbolLoaderMethodDecorator;

  protected static final String TEMPLATE_PATH = "_symboltable.symbolloader.";

  public SymbolLoaderDecorator(final GlobalExtensionManagement glex,
                               final SymbolTableService symbolTableService,
                               final MethodDecorator methodDecorator,
                               final MandatoryMutatorSymbolLoaderDecorator symbolLoaderMethodDecorator) {
    super(glex);
    this.methodDecorator = methodDecorator;
    this.symbolTableService = symbolTableService;
    this.symbolLoaderMethodDecorator = symbolLoaderMethodDecorator;
  }

  @Override
  public ASTCDClass decorate(ASTCDClass symbolInput) {
    String symbolLoaderSimpleName = symbolTableService.getSymbolLoaderSimpleName(symbolInput);
    String scopeInterfaceType = symbolTableService.getScopeInterfaceFullName();
    String symbolFullName = symbolTableService.getSymbolFullName(symbolInput);
    String simpleName = symbolInput.getName();
    ASTModifier modifier = symbolInput.isPresentModifier() ?
            symbolTableService.createModifierPublicModifier(symbolInput.getModifier()) :
            PUBLIC.build();

    ASTCDAttribute delegateAttribute = createDelegateAttribute(symbolFullName);

    ASTCDAttribute nameAttribute = createNameAttribute();
    List<ASTCDMethod> nameMethods = methodDecorator.getAccessorDecorator().decorate(nameAttribute);
    nameMethods.addAll(symbolLoaderMethodDecorator.decorate(nameAttribute));

    ASTCDAttribute enclosingScopeAttribute = createEnclosingScopeAttribute(scopeInterfaceType);
    List<ASTCDMethod> enclosingScopeMethods = methodDecorator.getAccessorDecorator().decorate(enclosingScopeAttribute);
    enclosingScopeMethods.addAll(symbolLoaderMethodDecorator.decorate(enclosingScopeAttribute));

    ASTCDClassBuilder builder = CD4AnalysisMill.cDClassBuilder()
            .setName(symbolLoaderSimpleName)
            .setModifier(modifier)
            .setSuperclass(getMCTypeFacade()
            .createQualifiedType(symbolTableService.getSymbolFullName(symbolInput)))
            .addCDConstructor(createConstructor(symbolLoaderSimpleName))
            .addCDAttribute(nameAttribute)
            .addAllCDMethods(nameMethods);
    return builder
            .addCDAttribute(delegateAttribute)
            .addCDAttribute(enclosingScopeAttribute)
            .addAllCDMethods(enclosingScopeMethods)
            .addCDMethod(createLazyLoadDelegateMethod(symbolLoaderSimpleName, symbolFullName, simpleName))
            .build();
  }

  protected ASTCDConstructor createConstructor(String symbolLoaderClass) {
    ASTCDParameter nameParameter = getCDParameterFacade().createParameter(String.class, NAME_VAR);
    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), symbolLoaderClass, nameParameter);
    this.replaceTemplate(EMPTY_BODY, constructor, new TemplateHookPoint(TEMPLATE_PATH + "ConstructorSymbolLoader"));
    return constructor;
  }

  protected ASTCDAttribute createNameAttribute() {
    return getCDAttributeFacade().createAttribute(PROTECTED, "String", "name");
  }

  protected ASTCDAttribute createEnclosingScopeAttribute(String scopeType) {
    return getCDAttributeFacade().createAttribute(PROTECTED, scopeType, "enclosingScope");
  }

  protected ASTCDAttribute createDelegateAttribute(String symbolType) {
    ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PROTECTED, getMCTypeFacade().createOptionalTypeOf(symbolType), "delegate");
    getDecorationHelper().addAttributeDefaultValues(attribute, glex);
    return attribute;
  }

  protected ASTCDMethod createLazyLoadDelegateMethod(String symbolLoaderName, String symbolName, String simpleName) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, getMCTypeFacade().createQualifiedType(symbolName), "lazyLoadDelegate");
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "LazyLoadDelegate", symbolLoaderName,
            symbolName, simpleName));
    return method;
  }

}
