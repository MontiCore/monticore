package de.monticore.codegen.cd2java._symboltable.scope;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.se_rwth.commons.StringTransformations;

import java.util.*;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.ACCEPT_METHOD;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_INTERFACE;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.VISITOR_PREFIX;
import static de.monticore.codegen.cd2java.data.ListSuffixDecorator.LIST_SUFFIX_S;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class ScopeClassDecorator extends AbstractDecorator {

  protected final SymbolTableService symbolTableService;

  protected final VisitorService visitorService;

  protected final MethodDecorator methodDecorator;

  protected final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> accessorDecorator;

  protected final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> mutatorDecorator;

  protected boolean isScopeTop;

  protected static final String TEMPLATE_PATH = "_symboltable.scope.";

  public ScopeClassDecorator(final GlobalExtensionManagement glex,
                             final SymbolTableService symbolTableService,
                             final VisitorService visitorService,
                             final MethodDecorator methodDecorator) {
    super(glex);
    this.symbolTableService = symbolTableService;
    this.methodDecorator = methodDecorator;
    this.accessorDecorator = methodDecorator.getAccessorDecorator();
    this.mutatorDecorator = methodDecorator.getMutatorDecorator();
    this.visitorService = visitorService;
  }

  /**
   * uses a scopeCD for scopeRule attributes and methods
   * and a normalCD for Symbol Classes and Interfaces
   */
  public ASTCDClass decorate(ASTCDCompilationUnit scopeInput, ASTCDCompilationUnit symbolInput) {
    String scopeClassName = scopeInput.getCDDefinition().getName() + SCOPE_SUFFIX;
    ASTMCQualifiedType scopeInterfaceType = symbolTableService.getScopeInterfaceType();

    // attributes and methods from scope rule
    List<ASTCDAttribute> scopeRuleAttributeList = scopeInput.deepClone().getCDDefinition().getCDClassList()
        .stream()
        .map(ASTCDClassTOP::getCDAttributeList)
        .flatMap(List::stream)
        .collect(Collectors.toList());
    scopeRuleAttributeList.forEach(a -> symbolTableService.addAttributeDefaultValues(a, this.glex));

    List<ASTCDMethod> scopeRuleMethodList = scopeInput.deepClone().getCDDefinition().getCDClassList()
        .stream()
        .map(ASTCDClassTOP::getCDMethodList)
        .flatMap(List::stream)
        .collect(Collectors.toList());

    List<ASTCDMethod> scopeRuleAttributeMethods = scopeRuleAttributeList
        .stream()
        .map(methodDecorator::decorate)
        .flatMap(List::stream)
        .collect(Collectors.toList());

    Map<String, ASTCDAttribute> symbolAttributes = createSymbolAttributes(symbolInput.getCDDefinition().getCDClassList(), symbolTableService.getCDSymbol());
    symbolAttributes.putAll(createSymbolAttributes(symbolInput.getCDDefinition().getCDInterfaceList(), symbolTableService.getCDSymbol()));
    symbolAttributes.putAll(getSuperSymbolAttributes());

    List<ASTCDMethod> symbolMethods = createSymbolMethods(symbolAttributes.values());

    List<ASTCDAttribute> symbolAlreadyResolvedAttributes = createSymbolAlreadyResolvedAttributes(symbolAttributes.keySet());

    List<ASTCDMethod> symbolAlreadyResolvedMethods = symbolAlreadyResolvedAttributes
        .stream()
        .map(methodDecorator::decorate)
        .flatMap(List::stream).collect(Collectors.toList());

    ASTCDAttribute enclosingScopeAttribute = createEnclosingScopeAttribute();
    List<ASTCDMethod> enclosingScopeMethods = createEnclosingScopeMethods(enclosingScopeAttribute);

    ASTCDAttribute spanningSymbolAttribute = createSpanningSymbolAttribute();
    List<ASTCDMethod> spanningSymbolMethods = createSpanningSymbolMethods(spanningSymbolAttribute);

    ASTCDAttribute shadowingAttribute = createShadowingAttribute();
    List<ASTCDMethod> shadowingSymbolMethods = methodDecorator.decorate(shadowingAttribute);

    ASTCDAttribute exportSymbolsAttribute = createExportSymbolsAttribute();
    List<ASTCDMethod> exportSymbolsSymbolMethods = methodDecorator.decorate(exportSymbolsAttribute);

    ASTCDAttribute nameAttribute = createNameAttribute();
    List<ASTCDMethod> nameMethods = methodDecorator.decorate(nameAttribute);

    ASTCDAttribute astNodeAttribute = createASTNodeAttribute();
    List<ASTCDMethod> astNodeMethods = methodDecorator.decorate(astNodeAttribute);


    return CD4AnalysisMill.cDClassBuilder()
        .setName(scopeClassName)
        .setModifier(PUBLIC.build())
        .addInterface(scopeInterfaceType)
        .addAllCDConstructors(createConstructors(scopeClassName))
        .addAllCDAttributes(scopeRuleAttributeList)
        .addAllCDMethods(scopeRuleMethodList)
        .addAllCDMethods(scopeRuleAttributeMethods)
        .addAllCDAttributes(symbolAttributes.values())
        .addAllCDMethods(symbolMethods)
        .addAllCDAttributes(symbolAlreadyResolvedAttributes)
        .addAllCDMethods(symbolAlreadyResolvedMethods)
        .addCDMethod(createSymbolsSizeMethod(symbolAttributes.keySet()))
        .addCDAttribute(enclosingScopeAttribute)
        .addAllCDMethods(enclosingScopeMethods)
        .addCDAttribute(spanningSymbolAttribute)
        .addAllCDMethods(spanningSymbolMethods)
        .addCDAttribute(shadowingAttribute)
        .addAllCDMethods(shadowingSymbolMethods)
        .addCDAttribute(exportSymbolsAttribute)
        .addAllCDMethods(exportSymbolsSymbolMethods)
        .addCDAttribute(nameAttribute)
        .addAllCDMethods(nameMethods)
        .addCDAttribute(astNodeAttribute)
        .addAllCDMethods(astNodeMethods)
        .addCDAttribute(createSubScopesAttribute(scopeInterfaceType))
        .addAllCDMethods(createSubScopeMethods(scopeInterfaceType))
        .addAllCDMethods(createAcceptMethods(scopeClassName))
        .addAllCDMethods(createSuperScopeMethods(symbolTableService.getScopeInterfaceFullName()))
        .build();
  }

  protected List<ASTCDConstructor> createConstructors(String scopeClassName) {
    ASTCDConstructor defaultConstructor = createDefaultConstructor(scopeClassName);
    ASTCDConstructor isShadowingConstructor = createIsShadowingConstructor(scopeClassName);
    ASTCDConstructor enclosingScopeConstructor = createEnclosingScopeConstructor(scopeClassName);
    ASTCDConstructor isShadowingAndEnclosingScopeConstructor = createIsShadowingAndEnclosingScopeConstructor(scopeClassName);
    return new ArrayList<>(Arrays.asList(defaultConstructor, isShadowingConstructor, enclosingScopeConstructor, isShadowingAndEnclosingScopeConstructor));
  }

  protected ASTCDConstructor createDefaultConstructor(String scopeClassName) {
    ASTCDConstructor defaultConstructor = getCDConstructorFacade().createConstructor(PUBLIC, scopeClassName);
    this.replaceTemplate(EMPTY_BODY, defaultConstructor, new StringHookPoint("super();\n" +
        "    this." + NAME_VAR + " = Optional.empty();"));
    return defaultConstructor;
  }

  protected ASTCDConstructor createEnclosingScopeConstructor(String scopeClassName) {
    ASTCDParameter scopeParameter = getCDParameterFacade().createParameter(symbolTableService.getScopeInterfaceType(), ENCLOSING_SCOPE_VAR);
    ASTCDConstructor defaultConstructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), scopeClassName, scopeParameter);
    this.replaceTemplate(EMPTY_BODY, defaultConstructor, new StringHookPoint("this(" + ENCLOSING_SCOPE_VAR + ", false);"));
    return defaultConstructor;
  }

  protected ASTCDConstructor createIsShadowingConstructor(String scopeClassName) {
    ASTCDParameter shadowingParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createBooleanType(), SHADOWING_VAR);
    ASTCDConstructor defaultConstructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), scopeClassName, shadowingParameter);
    this.replaceTemplate(EMPTY_BODY, defaultConstructor, new StringHookPoint("this." + SHADOWING_VAR + " = " + SHADOWING_VAR + ";\n" +
        "    this." + NAME_VAR + " = Optional.empty();"));
    return defaultConstructor;
  }

  protected ASTCDConstructor createIsShadowingAndEnclosingScopeConstructor(String scopeClassName) {
    ASTCDParameter shadowingParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createBooleanType(), SHADOWING_VAR);
    ASTCDParameter scopeParameter = getCDParameterFacade().createParameter(symbolTableService.getScopeInterfaceType(), ENCLOSING_SCOPE_VAR);
    ASTCDConstructor defaultConstructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), scopeClassName, scopeParameter, shadowingParameter);
    this.replaceTemplate(EMPTY_BODY, defaultConstructor, new StringHookPoint("this.setEnclosingScope(" + ENCLOSING_SCOPE_VAR + ");\n" +
        "    this." + SHADOWING_VAR + " = " + SHADOWING_VAR + "; \n" +
        "    this." + NAME_VAR + " = Optional.empty();"));
    return defaultConstructor;
  }

  protected List<ASTCDMethod> createAcceptMethods(String scopeClassName) {
    List<ASTCDMethod> acceptMethods = new ArrayList<>();

    String ownScopeVisitor = visitorService.getScopeVisitorFullName();
    ASTCDParameter parameter = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(ownScopeVisitor), VISITOR_PREFIX);
    ASTCDMethod ownAcceptMethod = getCDMethodFacade().createMethod(PUBLIC, ACCEPT_METHOD, parameter);
    if (isScopeTop()) {
      String errorCode = DecorationHelper.getGeneratedErrorCode(ownAcceptMethod);
      this.replaceTemplate(EMPTY_BODY, ownAcceptMethod, new TemplateHookPoint(TEMPLATE_PATH + "AcceptOwn", scopeClassName, errorCode));
    } else {
      this.replaceTemplate(EMPTY_BODY, ownAcceptMethod, new StringHookPoint("visitor.handle(this);"));
    }
    acceptMethods.add(ownAcceptMethod);

    for (CDDefinitionSymbol cdDefinitionSymbol : symbolTableService.getSuperCDsTransitive()) {
      String superScopeVisitor = visitorService.getScopeVisitorFullName(cdDefinitionSymbol);
      ASTCDParameter SuperVisitorParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(superScopeVisitor), VISITOR_PREFIX);
      ASTCDMethod acceptMethod = getCDMethodFacade().createMethod(PUBLIC, ACCEPT_METHOD, SuperVisitorParameter);
      String errorCode = DecorationHelper.getGeneratedErrorCode(acceptMethod);
      this.replaceTemplate(EMPTY_BODY, acceptMethod, new TemplateHookPoint(TEMPLATE_PATH + "Accept", ownScopeVisitor, scopeClassName, superScopeVisitor, errorCode));
      acceptMethods.add(acceptMethod);
    }

    return acceptMethods;
  }

  protected ASTCDMethod createSymbolsSizeMethod(Collection<String> symbolAttributeNames) {
    ASTCDMethod getSymbolSize = getCDMethodFacade().createMethod(PUBLIC, getCDTypeFacade().createIntType(), "getSymbolsSize");
    if (symbolAttributeNames.isEmpty()) {
      this.replaceTemplate(EMPTY_BODY, getSymbolSize, new StringHookPoint("return 0;"));
    } else {
      this.replaceTemplate(EMPTY_BODY, getSymbolSize, new TemplateHookPoint(TEMPLATE_PATH + "GetSymbolSize", symbolAttributeNames));
    }
    return getSymbolSize;
  }

  protected Map<String, ASTCDAttribute> getSuperSymbolAttributes() {
    Map<String, ASTCDAttribute> symbolAttributes = new HashMap<>();
    for (CDDefinitionSymbol cdDefinitionSymbol : symbolTableService.getSuperCDsTransitive()) {
      for (CDTypeSymbol type : cdDefinitionSymbol.getTypes()) {
        if (type.getAstNode().isPresent() && type.getAstNode().get().getModifierOpt().isPresent()
            && symbolTableService.hasSymbolStereotype(type.getAstNode().get().getModifierOpt().get())) {
          Optional<ASTCDAttribute> symbolAttribute = createSymbolAttribute(type.getAstNode().get(), cdDefinitionSymbol);
          symbolAttribute.ifPresent(attr -> symbolAttributes.put(attr.getName(), attr));
        }
      }
    }
    return symbolAttributes;
  }

  protected Map<String, ASTCDAttribute> createSymbolAttributes(List<? extends ASTCDType> symbolClassList, CDDefinitionSymbol cdDefinitionSymbol) {
    Map<String, ASTCDAttribute> symbolAttributeList = new HashMap<>();
    for (ASTCDType astcdClass : symbolClassList) {
      Optional<ASTCDAttribute> symbolAttributes = createSymbolAttribute(astcdClass, cdDefinitionSymbol);
      symbolAttributes.ifPresent(attr -> symbolAttributeList.put(attr.getName(), attr));
    }
    return symbolAttributeList;
  }

  protected Optional<ASTCDAttribute> createSymbolAttribute(ASTCDType cdType, CDDefinitionSymbol cdDefinitionSymbol) {
    Optional<String> symbolSimpleName = symbolTableService.getDefiningSymbolSimpleName(cdType);
    Optional<String> symbolFullName = symbolTableService.getDefiningSymbolFullName(cdType, cdDefinitionSymbol);
    if (symbolFullName.isPresent() && symbolSimpleName.isPresent()) {
      String attrName = StringTransformations.uncapitalize(symbolSimpleName.get() + LIST_SUFFIX_S);
      ASTMCType symbolMultiMap = getCDTypeFacade().createTypeByDefinition(String.format(SYMBOL_MULTI_MAP, symbolFullName.get()));
      ASTCDAttribute symbolAttribute = getCDAttributeFacade().createAttribute(PROTECTED, symbolMultiMap, attrName);
      this.replaceTemplate(VALUE, symbolAttribute, new StringHookPoint("= com.google.common.collect.LinkedListMultimap.create()"));
      return Optional.ofNullable(symbolAttribute);
    }
    return Optional.empty();
  }


  protected List<ASTCDAttribute> createSymbolAlreadyResolvedAttributes(Collection<String> symbolAttributeNameList) {
    List<ASTCDAttribute> symbolAttributeList = new ArrayList<>();
    for (String attributeName : symbolAttributeNameList) {
      String attrName = attributeName + ALREADY_RESOLVED;
      ASTMCType booleanType = getCDTypeFacade().createBooleanType();
      ASTCDAttribute symbolAttribute = getCDAttributeFacade().createAttribute(PROTECTED, booleanType, attrName);
      this.replaceTemplate(VALUE, symbolAttribute, new StringHookPoint("= false"));
      symbolAttributeList.add(symbolAttribute);
    }
    return symbolAttributeList;
  }

  protected List<ASTCDMethod> createSymbolMethods(Collection<ASTCDAttribute> astcdAttributes) {
    List<ASTCDMethod> symbolMethodList = new ArrayList<>();
    for (ASTCDAttribute attribute : astcdAttributes) {
      if (attribute.getMCType() instanceof ASTMCBasicGenericType && ((ASTMCBasicGenericType) attribute.getMCType()).sizeMCTypeArguments() == 2) {
        Optional<ASTMCType> mcTypeArgument = ((ASTMCBasicGenericType) attribute.getMCType()).getMCTypeArgument(1).getMCTypeOpt();
        if (mcTypeArgument.isPresent()) {
          symbolMethodList.add(createAddSymbolMethod(mcTypeArgument.get(), attribute.getName()));
          symbolMethodList.add(createRemoveSymbolMethod(mcTypeArgument.get(), attribute.getName()));
          symbolMethodList.add(createGetSymbolListMethod(attribute));
        }
      }
    }
    return symbolMethodList;
  }

  protected ASTCDMethod createAddSymbolMethod(ASTMCType symbolType, String attributeName) {
    ASTCDParameter parameter = getCDParameterFacade().createParameter(symbolType, SYMBOL_VAR);
    ASTCDMethod addMethod = getCDMethodFacade().createMethod(PUBLIC, "add", parameter);
    this.replaceTemplate(EMPTY_BODY, addMethod, new StringHookPoint("this." + attributeName + ".put(" + SYMBOL_VAR + ".getName(), " + SYMBOL_VAR + ");\n" +
        "    " + SYMBOL_VAR + ".setEnclosingScope(this);"));
    return addMethod;
  }

  protected ASTCDMethod createRemoveSymbolMethod(ASTMCType symbolType, String attributeName) {
    ASTCDParameter parameter = getCDParameterFacade().createParameter(symbolType, StringTransformations.uncapitalize(SYMBOL_SUFFIX));
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, "remove", parameter);
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("this." + attributeName + ".remove(" + SYMBOL_VAR + ".getName(), " + SYMBOL_VAR + ");"));
    return method;
  }

  protected ASTCDMethod createGetSymbolListMethod(ASTCDAttribute astcdAttribute) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, astcdAttribute.getMCType(), "get" + StringTransformations.capitalize(astcdAttribute.getName()));
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return this." + astcdAttribute.getName() + ";"));
    return method;
  }

  protected ASTCDAttribute createEnclosingScopeAttribute() {
    return this.getCDAttributeFacade().createAttribute(PROTECTED, symbolTableService.getScopeInterfaceType(), ENCLOSING_SCOPE_VAR, this.glex);
  }

  protected List<ASTCDMethod> createEnclosingScopeMethods(ASTCDAttribute enclosingScopeAttribute) {
    List<ASTCDMethod> enclosingScopeMethods = accessorDecorator.decorate(enclosingScopeAttribute);
    mutatorDecorator.disableTemplates();
    List<ASTCDMethod> mutatorMethods = mutatorDecorator.decorate(enclosingScopeAttribute);
    mutatorDecorator.enableTemplates();
    // only one setter
    if (mutatorMethods.size() == 1) {
      this.replaceTemplate(EMPTY_BODY, mutatorMethods.get(0), new TemplateHookPoint(TEMPLATE_PATH + "SetEnclosingScope"));
    }
    enclosingScopeMethods.addAll(mutatorMethods);

    return enclosingScopeMethods;
  }

  protected ASTCDAttribute createSpanningSymbolAttribute() {
    return this.getCDAttributeFacade().createAttribute(PROTECTED,
        getCDTypeFacade().createOptionalTypeOf(I_SCOPE_SPANNING_SYMBOL), "spanningSymbol", this.glex);
  }

  protected List<ASTCDMethod> createSpanningSymbolMethods(ASTCDAttribute spanningSymbolAttr) {
    List<ASTCDMethod> methodList = accessorDecorator.decorate(spanningSymbolAttr);

    mutatorDecorator.disableTemplates();
    List<ASTCDMethod> mutatorMethods = mutatorDecorator.decorate(spanningSymbolAttr);
    mutatorDecorator.enableTemplates();
    for (ASTCDMethod mutatorMethod : mutatorMethods) {
      if (mutatorMethod.getName().equals("setSpanningSymbol")) {
        this.replaceTemplate(EMPTY_BODY, mutatorMethod, new TemplateHookPoint(TEMPLATE_PATH + "SetSpanningSymbol", spanningSymbolAttr));
      } else if (mutatorMethod.getName().equals("setSpanningSymbolOpt")) {
        this.replaceTemplate(EMPTY_BODY, mutatorMethod, new TemplateHookPoint(TEMPLATE_PATH + "SetSpanningSymbolOpt", spanningSymbolAttr));
      } else if (mutatorMethod.getName().equals("setSpanningSymbolAbsent")) {
        this.replaceTemplate(EMPTY_BODY, mutatorMethod, new TemplateHookPoint(TEMPLATE_PATH + "SetSpanningSymbolAbsent", spanningSymbolAttr));
      }
    }
    methodList.addAll(mutatorMethods);
    return methodList;
  }

  protected ASTCDAttribute createShadowingAttribute() {
    return this.getCDAttributeFacade().createAttribute(PROTECTED, getCDTypeFacade().createBooleanType(), SHADOWING_VAR);
  }

  protected ASTCDAttribute createExportSymbolsAttribute() {
    ASTCDAttribute attribute = this.getCDAttributeFacade().createAttribute(PROTECTED, getCDTypeFacade().createBooleanType(), "exportingSymbols");
    this.replaceTemplate(VALUE, attribute, new StringHookPoint("= true"));
    return attribute;
  }

  protected ASTCDAttribute createNameAttribute() {
    return this.getCDAttributeFacade().createAttribute(PROTECTED, getCDTypeFacade().createOptionalTypeOf(String.class),
        NAME_VAR, this.glex);
  }

  protected ASTCDAttribute createASTNodeAttribute() {
    return this.getCDAttributeFacade().createAttribute(PROTECTED, getCDTypeFacade().
        createOptionalTypeOf(AST_INTERFACE), AST_NODE_VAR, this.glex);
  }

  protected ASTCDAttribute createSubScopesAttribute(ASTMCQualifiedType scopeInterfaceType) {
    return this.getCDAttributeFacade().createAttribute(PROTECTED, getCDTypeFacade().createListTypeOf(scopeInterfaceType),
        "subScopes", this.glex);
  }

  protected List<ASTCDMethod> createSubScopeMethods(ASTMCType scopeInterface) {
    ASTCDMethod addSubScopeMethod = createAddSubScopeMethod(scopeInterface);
    this.replaceTemplate(EMPTY_BODY, addSubScopeMethod, new TemplateHookPoint(TEMPLATE_PATH + "AddSubScope"));
    ASTCDMethod removeSubScopeMethod = createRemoveSubScopeMethod(scopeInterface);
    this.replaceTemplate(EMPTY_BODY, removeSubScopeMethod, new TemplateHookPoint(TEMPLATE_PATH + "RemoveSubScope"));
    ASTCDMethod getSubScopesMethod = createGetSubScopesMethod(scopeInterface);
    this.replaceTemplate(EMPTY_BODY, getSubScopesMethod, new TemplateHookPoint(TEMPLATE_PATH + "GetSubScopes"));
    ASTCDMethod setSubScopesMethod = createSetSubScopesMethod(scopeInterface);
    this.replaceTemplate(EMPTY_BODY, setSubScopesMethod, new TemplateHookPoint(TEMPLATE_PATH + "SetSubScopes"));
    return new ArrayList<>(Arrays.asList(addSubScopeMethod, removeSubScopeMethod, getSubScopesMethod, setSubScopesMethod));
  }

  protected List<ASTCDMethod> createSuperScopeMethods(String originalScopeType) {
    List<ASTCDMethod> superScopeMethods = new ArrayList<>();
    for (CDDefinitionSymbol cdDefinitionSymbol : symbolTableService.getSuperCDsTransitive()) {
      ASTMCType scopeInterfaceType = symbolTableService.getScopeInterfaceType(cdDefinitionSymbol);

      ASTCDMethod addSubScopeMethod = createAddSubScopeMethod(scopeInterfaceType);
      this.replaceTemplate(EMPTY_BODY, addSubScopeMethod, new StringHookPoint("this.addSubScope((" + originalScopeType + ") subScope);"));
      superScopeMethods.add(addSubScopeMethod);

      ASTCDMethod removeSubScopeMethod = createRemoveSubScopeMethod(scopeInterfaceType);
      this.replaceTemplate(EMPTY_BODY, removeSubScopeMethod, new StringHookPoint("this.removeSubScope((" + originalScopeType + ") subScope);"));
      superScopeMethods.add(removeSubScopeMethod);

      ASTCDMethod setEnclosingScopeMethod = createSetEnclosingScopeMethod(scopeInterfaceType);
      this.replaceTemplate(EMPTY_BODY, setEnclosingScopeMethod, new StringHookPoint("this.setEnclosingScope((" + originalScopeType + ") newEnclosingScope);"));
      superScopeMethods.add(setEnclosingScopeMethod);
    }
    return superScopeMethods;
  }

  protected ASTCDMethod createAddSubScopeMethod(ASTMCType scopeType) {
    ASTCDParameter subScopeParameter = getCDParameterFacade().createParameter(scopeType, "subScope");
    return getCDMethodFacade().createMethod(PUBLIC, "addSubScope", subScopeParameter);
  }

  protected ASTCDMethod createRemoveSubScopeMethod(ASTMCType scopeType) {
    ASTCDParameter subScopeParameter = getCDParameterFacade().createParameter(scopeType, "subScope");
    return getCDMethodFacade().createMethod(PUBLIC, "removeSubScope", subScopeParameter);
  }

  protected ASTCDMethod createGetSubScopesMethod(ASTMCType scopeType) {
    return getCDMethodFacade().createMethod(PUBLIC, getCDTypeFacade().createListTypeOf(scopeType), "getSubScopes");
  }

  protected ASTCDMethod createSetSubScopesMethod(ASTMCType scopeType) {
    ASTCDParameter subScopeParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createListTypeOf(scopeType), "subScopes");
    return getCDMethodFacade().createMethod(PUBLIC, "setSubScopes", subScopeParameter);
  }

  protected ASTCDMethod createSetEnclosingScopeMethod(ASTMCType scopeType) {
    ASTCDParameter subScopeParameter = getCDParameterFacade().createParameter(scopeType, "newEnclosingScope");
    return getCDMethodFacade().createMethod(PUBLIC, "setEnclosingScope", subScopeParameter);
  }

  public boolean isScopeTop() {
    return isScopeTop;
  }

  public void setScopeTop(boolean scopeTop) {
    isScopeTop = scopeTop;
  }
}
