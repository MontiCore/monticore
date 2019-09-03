package de.monticore.codegen.cd2java._symboltable.symbol;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.se_rwth.commons.StringTransformations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class SymbolReferenceDecorator extends AbstractCreator<ASTCDType, ASTCDClass> {

  protected SymbolTableService symbolTableService;

  protected MethodDecorator methodDecorator;

  public SymbolReferenceDecorator(final GlobalExtensionManagement glex,
                                  final SymbolTableService symbolTableService,
                                  final MethodDecorator methodDecorator) {
    super(glex);
    this.methodDecorator = methodDecorator;
    this.symbolTableService = symbolTableService;
  }

  @Override
  public ASTCDClass decorate(ASTCDType input) {
    String symbolReferenceClassSimpleName = symbolTableService.getSymbolReferenceClassSimpleName(input);
    String symbolFullName = symbolTableService.getSymbolFullName(input);
    String scopeInterfaceType = symbolTableService.getScopeInterfaceFullName();
    String astNodeName = symbolTableService.getASTPackage() + "." + input.getName();
    String simpleName = symbolTableService.removeASTPrefix(input);

    ASTCDAttribute accessModifierAttribute = createAccessModifierAttribute();
    List<ASTCDMethod> accessModifierMethods = methodDecorator.decorate(accessModifierAttribute);

    ASTCDAttribute predicateAttribute = createPredicateAttribute(symbolFullName);
    List<ASTCDMethod> predicateMethods = methodDecorator.getMutatorDecorator().decorate(predicateAttribute);

    ASTCDAttribute astNodeAttribute = createAstNodeAttribute(astNodeName);
    List<ASTCDMethod> astNodeMethods = methodDecorator.decorate(astNodeAttribute);

    ASTCDAttribute referencedSymbolAttribute = createReferencedSymbolAttribute(symbolFullName);
    ASTCDMethod referencedSymbolMethod = createReferencedSymbolMethod(referencedSymbolAttribute, symbolReferenceClassSimpleName);

    return CD4AnalysisMill.cDClassBuilder()
        .setName(symbolReferenceClassSimpleName)
        .setModifier(PUBLIC.build())
        .setSuperclass(getCDTypeFacade().createQualifiedType(symbolFullName))
        .addInterface(getCDTypeFacade().createQualifiedType(I_SYMBOL_REFERENCE))
        .addCDConstructor(createConstructor(symbolReferenceClassSimpleName, scopeInterfaceType))
        .addCDAttribute(accessModifierAttribute)
        .addAllCDMethods(accessModifierMethods)
        .addCDAttribute(predicateAttribute)
        .addAllCDMethods(predicateMethods)
        .addCDAttribute(astNodeAttribute)
        .addAllCDMethods(astNodeMethods)
        .addCDAttribute(referencedSymbolAttribute)
        .addCDMethod(referencedSymbolMethod)
        .addCDMethod(createGetNameMethod())
        .addCDMethod(createGetFullNameMethod())
        .addAllCDMethods(createEnclosingScopeMethods(scopeInterfaceType))
        .addCDMethod(createIsReferencedSymbolLoadedMethod())
        .addCDMethod(createExistsReferencedSymbolMethod())
        .addCDMethod(createLoadReferencedSymbolMethod(symbolReferenceClassSimpleName, symbolFullName, simpleName))
        .build();
  }

  protected ASTCDConstructor createConstructor(String symbolReferenceClass, String scopeInterfaceType) {
    ASTCDParameter nameParameter = getCDParameterFacade().createParameter(String.class, "name");
    ASTCDParameter enclosingScopeParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(scopeInterfaceType), "enclosingScope");
    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), symbolReferenceClass, nameParameter, enclosingScopeParameter);
    this.replaceTemplate(EMPTY_BODY, constructor, new TemplateHookPoint("_symboltable.symbolreferece.Constructor"));
    return constructor;
  }

  protected ASTCDAttribute createAccessModifierAttribute() {
    ASTCDAttribute accessModifierAttribute = getCDAttributeFacade().createAttribute(PROTECTED, ACCESS_MODIFIER, "accessModifier");
    this.replaceTemplate(VALUE, accessModifierAttribute, new StringHookPoint("= " + ACCESS_MODIFIER + ".ALL_INCLUSION"));
    return accessModifierAttribute;
  }

  protected ASTCDAttribute createPredicateAttribute(String symbolType) {
    ASTCDAttribute predicateAttribute = getCDAttributeFacade().createAttribute(PROTECTED, String.format(PREDICATE, symbolType ), "predicate");
    this.replaceTemplate(VALUE, predicateAttribute, new StringHookPoint("= x -> true"));
    return predicateAttribute;
  }


  protected ASTCDAttribute createAstNodeAttribute(String astType) {
    return getCDAttributeFacade().createAttribute(PROTECTED, getCDTypeFacade().createOptionalTypeOf(astType), "astNode");
  }

  protected ASTCDAttribute createReferencedSymbolAttribute(String symbolType) {
    return getCDAttributeFacade().createAttribute(PROTECTED, symbolType, "referencedSymbol");
  }


  protected ASTCDMethod createReferencedSymbolMethod(ASTCDAttribute referencedSymbolAttribute, String symbolReferenceName) {
    ASTCDMethod getReferencedSymbolMethod = getCDMethodFacade().createMethod(PUBLIC, referencedSymbolAttribute.getMCType(),
        "get" + StringTransformations.capitalize(referencedSymbolAttribute.getName()));
    this.replaceTemplate(EMPTY_BODY, getReferencedSymbolMethod, new TemplateHookPoint(
        "_symboltable.symbolreferece.GetReferencedSymbol", symbolReferenceName, referencedSymbolAttribute.printType()));
    return getReferencedSymbolMethod;
  }

  protected ASTCDMethod createGetNameMethod() {
    ASTCDMethod getNameMethod = getCDMethodFacade().createMethod(PUBLIC, getCDTypeFacade().createQualifiedType(String.class), "getName");
    this.replaceTemplate(EMPTY_BODY, getNameMethod, new TemplateHookPoint("_symboltable.symbolreferece.GetName"));
    return getNameMethod;
  }

  protected ASTCDMethod createGetFullNameMethod() {
    ASTCDMethod getNameMethod = getCDMethodFacade().createMethod(PUBLIC, getCDTypeFacade().createQualifiedType(String.class), "getFullName");
    this.replaceTemplate(EMPTY_BODY, getNameMethod, new StringHookPoint(
        "return getReferencedSymbol().getFullName();"));
    return getNameMethod;
  }

  protected List<ASTCDMethod> createEnclosingScopeMethods(String scopeInterface) {
    ASTCDMethod getEnclosingScope = getCDMethodFacade().createMethod(PUBLIC, getCDTypeFacade().createQualifiedType(scopeInterface), "getEnclosingScope");
    this.replaceTemplate(EMPTY_BODY, getEnclosingScope, new StringHookPoint("return getReferencedSymbol().getEnclosingScope();"));

    ASTCDParameter scopeParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(scopeInterface), "scope");

    ASTCDMethod setEnclosingScope = getCDMethodFacade().createMethod(PUBLIC, "setEnclosingScope", scopeParam);
    this.replaceTemplate(EMPTY_BODY, setEnclosingScope, new StringHookPoint("getReferencedSymbol().setEnclosingScope(scope);"));

    return new ArrayList<>(Arrays.asList(getEnclosingScope, setEnclosingScope));
  }

  protected ASTCDMethod createExistsReferencedSymbolMethod() {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, getCDTypeFacade().createBooleanType(), "existsReferencedSymbol");
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint(
        "return isReferencedSymbolLoaded() || loadReferencedSymbol().isPresent();"));
    return method;
  }

  protected ASTCDMethod createIsReferencedSymbolLoadedMethod() {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, getCDTypeFacade().createBooleanType(), "isReferencedSymbolLoaded");
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return referencedSymbol != null;"));
    return method;
  }

  protected ASTCDMethod createLoadReferencedSymbolMethod(String symbolReferenceName, String symbolName, String simpleName) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, getCDTypeFacade().createOptionalTypeOf(symbolName), "loadReferencedSymbol");
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_symboltable.symbolreferece.LoadReferencedSymbol", symbolReferenceName,
        symbolName, simpleName));
    return method;
  }

}
