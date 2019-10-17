package de.monticore.codegen.cd2java._symboltable.symbol;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._symboltable.symbol.symbolReferenceMethodDecorator.SymbolReferenceMethodDecorator;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.se_rwth.commons.StringTransformations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_PREFIX;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

/**
 * creates a SymbolReference class from a grammar
 */
public class SymbolReferenceDecorator extends AbstractCreator<ASTCDClass, ASTCDClass> {

  protected SymbolTableService symbolTableService;

  protected SymbolReferenceMethodDecorator symbolReferenceMethodDecorator;

  protected MethodDecorator methodDecorator;

  protected static final String TEMPLATE_PATH = "_symboltable.symbolreferece.";

  public SymbolReferenceDecorator(final GlobalExtensionManagement glex,
                                  final SymbolTableService symbolTableService,
                                  final SymbolReferenceMethodDecorator symbolReferenceMethodDecorator,
                                  final MethodDecorator methodDecorator) {
    super(glex);
    this.symbolReferenceMethodDecorator = symbolReferenceMethodDecorator;
    this.methodDecorator = methodDecorator;
    this.symbolTableService = symbolTableService;
  }

  @Override
  public ASTCDClass decorate(ASTCDClass symbolInput) {
    String symbolReferenceClassSimpleName = symbolTableService.getSymbolReferenceClassSimpleName(symbolInput);
    String symbolFullName = symbolTableService.getSymbolFullName(symbolInput);
    String scopeInterfaceType = symbolTableService.getScopeInterfaceFullName();
    String astNodeName = symbolTableService.getASTPackage() + "." + AST_PREFIX + symbolInput.getName();
    String simpleName = symbolInput.getName();

    // symbol rule methods and attributes
    List<ASTCDMethod> symbolRuleAttributeMethods = symbolInput.deepClone().getCDAttributeList()
        .stream()
        .map(symbolReferenceMethodDecorator::decorate)
        .flatMap(List::stream)
        .collect(Collectors.toList());
    List<ASTCDMethod> symbolRuleMethods = symbolInput.deepClone().getCDMethodList();

    ASTCDAttribute accessModifierAttribute = createAccessModifierAttribute();
    List<ASTCDMethod> accessModifierMethods = symbolReferenceMethodDecorator.decorate(accessModifierAttribute);

    ASTCDAttribute predicateAttribute = createPredicateAttribute(symbolFullName);
    List<ASTCDMethod> predicateMethods = methodDecorator.getMutatorDecorator().decorate(predicateAttribute);

    ASTCDAttribute astNodeAttribute = createAstNodeAttribute(astNodeName);
    List<ASTCDMethod> astNodeMethods = symbolReferenceMethodDecorator.decorate(astNodeAttribute);

    ASTCDAttribute referencedSymbolAttribute = createReferencedSymbolAttribute(symbolFullName);
    ASTCDMethod referencedSymbolMethod = createReferencedSymbolMethod(referencedSymbolAttribute, symbolReferenceClassSimpleName);

    ASTCDClass symbolReferenceClass = CD4AnalysisMill.cDClassBuilder()
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
        .addAllCDMethods(symbolRuleAttributeMethods)
        .addAllCDMethods(symbolRuleMethods)
        .build();
    if (symbolInput.getModifierOpt().isPresent() && (symbolTableService.hasScopeStereotype(symbolInput.getModifierOpt().get())
        || symbolTableService.hasInheritedScopeStereotype(symbolInput.getModifierOpt().get()))) {
      symbolReferenceClass.addCDMethod(createGetSpannedScopeMethod(scopeInterfaceType));
    }

    return symbolReferenceClass;
  }

  protected ASTCDConstructor createConstructor(String symbolReferenceClass, String scopeInterfaceType) {
    ASTCDParameter nameParameter = getCDParameterFacade().createParameter(String.class, NAME_VAR);
    ASTCDParameter enclosingScopeParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(scopeInterfaceType), ENCLOSING_SCOPE_VAR);
    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), symbolReferenceClass, nameParameter, enclosingScopeParameter);
    this.replaceTemplate(EMPTY_BODY, constructor, new TemplateHookPoint(TEMPLATE_PATH + "Constructor"));
    return constructor;
  }

  protected ASTCDAttribute createAccessModifierAttribute() {
    ASTCDAttribute accessModifierAttribute = getCDAttributeFacade().createAttribute(PROTECTED, ACCESS_MODIFIER, "accessModifier");
    this.replaceTemplate(VALUE, accessModifierAttribute, new StringHookPoint("= " + ACCESS_MODIFIER_ALL_INCLUSION));
    return accessModifierAttribute;
  }

  protected ASTCDAttribute createPredicateAttribute(String symbolType) {
    ASTCDAttribute predicateAttribute = getCDAttributeFacade().createAttribute(PROTECTED, String.format(PREDICATE, symbolType), PREDICATE_VAR);
    this.replaceTemplate(VALUE, predicateAttribute, new StringHookPoint("= x -> true"));
    return predicateAttribute;
  }

  protected ASTCDAttribute createAstNodeAttribute(String astType) {
    return getCDAttributeFacade().createAttribute(PROTECTED, getCDTypeFacade().createOptionalTypeOf(astType), AST_NODE_VAR);
  }

  protected ASTCDAttribute createReferencedSymbolAttribute(String symbolType) {
    return getCDAttributeFacade().createAttribute(PROTECTED, symbolType, "referencedSymbol");
  }

  protected ASTCDMethod createReferencedSymbolMethod(ASTCDAttribute referencedSymbolAttribute, String symbolReferenceName) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, referencedSymbolAttribute.getMCType(),
        "get" + StringTransformations.capitalize(referencedSymbolAttribute.getName()));
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(
        TEMPLATE_PATH + "GetReferencedSymbol", symbolReferenceName, referencedSymbolAttribute.printType()));
    return method;
  }

  protected ASTCDMethod createGetNameMethod() {
    ASTCDMethod getNameMethod = getCDMethodFacade().createMethod(PUBLIC, getCDTypeFacade().createQualifiedType(String.class), "getName");
    this.replaceTemplate(EMPTY_BODY, getNameMethod, new TemplateHookPoint(TEMPLATE_PATH + "GetName"));
    return getNameMethod;
  }

  protected ASTCDMethod createGetFullNameMethod() {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, getCDTypeFacade().createQualifiedType(String.class), "getFullName");
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint(
        "return getReferencedSymbol().getFullName();"));
    return method;
  }

  protected List<ASTCDMethod> createEnclosingScopeMethods(String scopeInterface) {
    ASTCDMethod getEnclosingScope = getCDMethodFacade().createMethod(PUBLIC, getCDTypeFacade().createQualifiedType(scopeInterface), "getEnclosingScope");
    this.replaceTemplate(EMPTY_BODY, getEnclosingScope, new StringHookPoint("return getReferencedSymbol().getEnclosingScope();"));

    ASTCDParameter scopeParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(scopeInterface), SCOPE_VAR);
    ASTCDMethod setEnclosingScope = getCDMethodFacade().createMethod(PUBLIC, "setEnclosingScope", scopeParam);
    this.replaceTemplate(EMPTY_BODY, setEnclosingScope, new StringHookPoint("getReferencedSymbol().setEnclosingScope(" + SCOPE_VAR + ");"));

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
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "LoadReferencedSymbol", symbolReferenceName,
        symbolName, simpleName));
    return method;
  }

  protected ASTCDMethod createGetSpannedScopeMethod(String scopeInterface) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, getCDTypeFacade().createQualifiedType(scopeInterface), "getSpannedScope");
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return getReferencedSymbol().getSpannedScope();"));
    return method;
  }
}
