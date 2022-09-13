/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.scope;

import com.google.common.collect.ListMultimap;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolKindHierarchies;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;

import java.util.*;
import java.util.stream.Collectors;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.codegen.CD2JavaTemplates.VALUE;
import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.ACCEPT_METHOD;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_INTERFACE;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;
import static de.monticore.codegen.cd2java._visitor.VisitorConstants.VISITOR_PREFIX;
import static de.monticore.codegen.cd2java.data.ListSuffixDecorator.LIST_SUFFIX_S;

/**
 * creates a Scope class from a grammar
 */
public class ScopeClassDecorator extends AbstractDecorator {

  protected final SymbolTableService symbolTableService;

  protected final VisitorService visitorService;

  protected final MethodDecorator methodDecorator;

  protected final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> accessorDecorator;

  protected final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> mutatorDecorator;

  /**
   * flag added to define if the Scope class was overwritten with the TOP mechanism
   * if top mechanism was used, must use setter to set flag true, before the decoration
   * is needed for different accept method implementations
   */
  protected boolean isScopeTop;

  protected static final String TEMPLATE_PATH = "_symboltable.scope.";

  protected static final String THIS = "this.";

  protected static final String ASSIGN_OPTIONAL_NAME =
      "    " + THIS + NAME_VAR + " = Optional.empty();";

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
   * @param scopeInput  for scopeRule attributes and methods
   * @param symbolInput for Symbol Classes and Interfaces
   */
  public ASTCDClass decorate(ASTCDCompilationUnit scopeInput, ASTCDCompilationUnit symbolInput) {
    String scopeClassName = symbolTableService.getCDName() + SCOPE_SUFFIX;
    ASTMCQualifiedType scopeInterfaceType = symbolTableService.getScopeInterfaceType();

    // attributes and methods from scope rule
    List<ASTCDAttribute> scopeRuleAttributeList = scopeInput.deepClone().getCDDefinition()
        .getCDClassesList()
        .stream()
        .map(ASTCDClass::getCDAttributeList)
        .flatMap(List::stream)
        .collect(Collectors.toList());
    scopeRuleAttributeList
        .forEach(a -> getDecorationHelper().addAttributeDefaultValues(a, this.glex));

    List<ASTCDMethod> scopeRuleMethodList = scopeInput.deepClone().getCDDefinition()
        .getCDClassesList()
        .stream()
        .map(ASTCDClass::getCDMethodList)
        .flatMap(List::stream)
        .collect(Collectors.toList());
    for (ASTCDMethod meth: scopeRuleMethodList) {
      if (symbolTableService.isMethodBodyPresent(meth)) {
        glex.replaceTemplate(EMPTY_BODY, meth, new StringHookPoint(symbolTableService.getMethodBody(meth)));
      }
    }

    List<ASTCDMethod> scopeRuleAttributeMethods = scopeRuleAttributeList
        .stream()
        .map(methodDecorator::decorate)
        .flatMap(List::stream)
        .collect(Collectors.toList());

    Map<String, ASTCDAttribute> symbolAttributes = createSymbolAttributes(
        symbolInput.getCDDefinition().getCDClassesList(), symbolTableService.getCDSymbol());
    symbolAttributes.putAll(getSuperSymbolAttributes());

    List<ASTCDMethod> symbolMethods = createSymbolMethods(symbolAttributes.values());

    List<ASTCDAttribute> symbolAlreadyResolvedAttributes = createSymbolAlreadyResolvedAttributes(
        symbolAttributes.keySet());

    List<ASTCDMethod> symbolAlreadyResolvedMethods = symbolAlreadyResolvedAttributes
        .stream()
        .map(methodDecorator::decorate)
        .flatMap(List::stream).collect(Collectors.toList());

    ASTCDAttribute enclosingScopeAttribute = createEnclosingScopeAttribute();
    List<ASTCDMethod> enclosingScopeMethods = createEnclosingScopeMethods(enclosingScopeAttribute);

    ASTCDAttribute spanningSymbolAttribute = createSpanningSymbolAttribute();
    List<ASTCDMethod> spanningSymbolMethods = createSpanningSymbolMethods(spanningSymbolAttribute);

    ASTCDAttribute shadowingAttribute = createShadowingAttribute();
    List<ASTCDMethod> shadowingMethods = methodDecorator.decorate(shadowingAttribute);

    ASTCDAttribute exportSymbolsAttribute = createExportSymbolsAttribute();
    List<ASTCDMethod> exportSymbolsMethods = methodDecorator.decorate(exportSymbolsAttribute);

    ASTCDAttribute orderedAttribute = createOrderedAttribute();
    List<ASTCDMethod> orderedMethods = methodDecorator.decorate(orderedAttribute);

    ASTCDAttribute nameAttribute = createNameAttribute();
    List<ASTCDMethod> nameMethods = methodDecorator.decorate(nameAttribute);

    ASTCDAttribute astNodeAttribute = createASTNodeAttribute();
    List<ASTCDMethod> astNodeMethods = methodDecorator.decorate(astNodeAttribute);

    Optional<ASTCDClass> scopeRuleSuperClass = scopeInput.deepClone().getCDDefinition()
            .getCDClassesList()
            .stream()
            .filter(ASTCDClass::isPresentCDExtendUsage)
            .findFirst();

    List<ASTCDMethod> resolveSubKindsMethods = createResolveSubKindsNameMethods(symbolInput.getCDDefinition());

    ASTCDClassBuilder builder = CD4AnalysisMill.cDClassBuilder()
        .setName(scopeClassName)
        .setModifier(PUBLIC.build())
        .setCDInterfaceUsage(CD4CodeMill.cDInterfaceUsageBuilder().addInterface(scopeInterfaceType).build())
        .addAllCDMembers(createConstructors(scopeClassName))
        .addAllCDMembers(scopeRuleAttributeList)
        .addAllCDMembers(scopeRuleMethodList)
        .addAllCDMembers(scopeRuleAttributeMethods)
        .addAllCDMembers(symbolAttributes.values())
        .addAllCDMembers(symbolMethods)
        .addAllCDMembers(symbolAlreadyResolvedAttributes)
        .addAllCDMembers(symbolAlreadyResolvedMethods)
        .addCDMember(enclosingScopeAttribute)
        .addAllCDMembers(enclosingScopeMethods)
        .addCDMember(spanningSymbolAttribute)
        .addAllCDMembers(spanningSymbolMethods)
        .addCDMember(shadowingAttribute)
        .addAllCDMembers(shadowingMethods)
        .addCDMember(exportSymbolsAttribute)
        .addAllCDMembers(exportSymbolsMethods)
        .addCDMember(orderedAttribute)
        .addAllCDMembers(orderedMethods)
        .addCDMember(nameAttribute)
        .addAllCDMembers(nameMethods)
        .addCDMember(astNodeAttribute)
        .addAllCDMembers(astNodeMethods)
        .addCDMember(createSubScopesAttribute(scopeInterfaceType))
        .addAllCDMembers(createSubScopeMethods(scopeInterfaceType))
        .addAllCDMembers(createAcceptTraverserMethods(scopeClassName))
        .addAllCDMembers(createSuperScopeMethods(symbolTableService.getScopeInterfaceFullName()))
        .addAllCDMembers(resolveSubKindsMethods);
    if (scopeRuleSuperClass.isPresent()) {
      builder.setCDExtendUsage(scopeRuleSuperClass.get().getCDExtendUsage().deepClone());
    }
    ASTCDClass clazz = builder.build();
    CD4C.getInstance().addImport(clazz, "de.monticore.symboltable.*");
    return clazz;
  }

  protected List<ASTCDConstructor> createConstructors(String scopeClassName) {
    ASTCDConstructor defaultConstructor = createDefaultConstructor(scopeClassName);
    ASTCDConstructor isShadowingConstructor = createIsShadowingConstructor(scopeClassName);
    ASTCDConstructor enclosingScopeConstructor = createEnclosingScopeConstructor(scopeClassName);
    ASTCDConstructor isShadowingAndEnclosingScopeConstructor = createIsShadowingAndEnclosingScopeConstructor(
        scopeClassName);
    return new ArrayList<>(Arrays
        .asList(defaultConstructor, isShadowingConstructor, enclosingScopeConstructor,
            isShadowingAndEnclosingScopeConstructor));
  }

  protected ASTCDConstructor createDefaultConstructor(String scopeClassName) {
    ASTCDConstructor defaultConstructor = getCDConstructorFacade()
        .createConstructor(PUBLIC.build(), scopeClassName);
    this.replaceTemplate(EMPTY_BODY, defaultConstructor,
        new StringHookPoint("super();\n" + ASSIGN_OPTIONAL_NAME));
    return defaultConstructor;
  }

  protected ASTCDConstructor createEnclosingScopeConstructor(String scopeClassName) {
    ASTCDParameter scopeParameter = getCDParameterFacade()
        .createParameter(symbolTableService.getScopeInterfaceType(), ENCLOSING_SCOPE_VAR);
    ASTCDConstructor defaultConstructor = getCDConstructorFacade()
        .createConstructor(PUBLIC.build(), scopeClassName, scopeParameter);
    this.replaceTemplate(EMPTY_BODY, defaultConstructor,
        new StringHookPoint("this(" + ENCLOSING_SCOPE_VAR + ", false);"));
    return defaultConstructor;
  }

  protected ASTCDConstructor createIsShadowingConstructor(String scopeClassName) {
    ASTCDParameter shadowingParameter = getCDParameterFacade()
        .createParameter(getMCTypeFacade().createBooleanType(), SHADOWING_VAR);
    ASTCDConstructor defaultConstructor = getCDConstructorFacade()
        .createConstructor(PUBLIC.build(), scopeClassName, shadowingParameter);
    this.replaceTemplate(EMPTY_BODY, defaultConstructor,
        new StringHookPoint(THIS + SHADOWING_VAR + " = " + SHADOWING_VAR + ";\n" +
            ASSIGN_OPTIONAL_NAME));
    return defaultConstructor;
  }

  protected ASTCDConstructor createIsShadowingAndEnclosingScopeConstructor(String scopeClassName) {
    ASTCDParameter shadowingParameter = getCDParameterFacade()
        .createParameter(getMCTypeFacade().createBooleanType(), SHADOWING_VAR);
    ASTCDParameter scopeParameter = getCDParameterFacade()
        .createParameter(symbolTableService.getScopeInterfaceType(), ENCLOSING_SCOPE_VAR);
    ASTCDConstructor defaultConstructor = getCDConstructorFacade()
        .createConstructor(PUBLIC.build(), scopeClassName, scopeParameter, shadowingParameter);
    this.replaceTemplate(EMPTY_BODY, defaultConstructor,
        new StringHookPoint("this.setEnclosingScope(" + ENCLOSING_SCOPE_VAR + ");\n" +
            "    " + THIS + SHADOWING_VAR + " = " + SHADOWING_VAR + "; \n" + ASSIGN_OPTIONAL_NAME));
    return defaultConstructor;
  }

  protected List<ASTCDMethod> createAcceptTraverserMethods(String scopeClassName) {
    List<ASTCDMethod> acceptMethods = new ArrayList<>();

    String visitor = visitorService.getTraverserInterfaceFullName();
    ASTCDParameter parameter = getCDParameterFacade()
        .createParameter(getMCTypeFacade().createQualifiedType(visitor), VISITOR_PREFIX);
    ASTCDMethod ownAcceptMethod = getCDMethodFacade()
        .createMethod(PUBLIC.build(), ACCEPT_METHOD, parameter);
    if (isScopeTop()) {
      String errorCode = symbolTableService.getGeneratedErrorCode(scopeClassName + ACCEPT_METHOD);
      this.replaceTemplate(EMPTY_BODY, ownAcceptMethod,
          new TemplateHookPoint(TEMPLATE_PATH + "AcceptOwn", scopeClassName, errorCode));
    }
    else {
      this.replaceTemplate(EMPTY_BODY, ownAcceptMethod,
          new StringHookPoint("visitor.handle(this);"));
    }
    acceptMethods.add(ownAcceptMethod);

    for (DiagramSymbol cdDefinitionSymbol : symbolTableService.getSuperCDsTransitive()) {
      String superVisitor = visitorService.getTraverserInterfaceFullName(cdDefinitionSymbol);
      ASTCDParameter superVisitorParameter = getCDParameterFacade()
          .createParameter(getMCTypeFacade().createQualifiedType(superVisitor), VISITOR_PREFIX);
      ASTCDMethod acceptMethod = getCDMethodFacade()
          .createMethod(PUBLIC.build(), ACCEPT_METHOD, superVisitorParameter);
      String errorCode = symbolTableService
          .getGeneratedErrorCode(scopeClassName + cdDefinitionSymbol.getFullName() + ACCEPT_METHOD);
      this.replaceTemplate(EMPTY_BODY, acceptMethod,
          new TemplateHookPoint(TEMPLATE_PATH + "AcceptScope", visitor, scopeClassName,
              superVisitor, errorCode));
      acceptMethods.add(acceptMethod);
    }

    return acceptMethods;
  }

  protected Map<String, ASTCDAttribute> getSuperSymbolAttributes() {
    Map<String, ASTCDAttribute> symbolAttributes = new LinkedHashMap<>();
    for (DiagramSymbol cdDefinitionSymbol : symbolTableService.getSuperCDsTransitive()) {
      for (CDTypeSymbol type : symbolTableService.getAllCDTypes(cdDefinitionSymbol)) {
        if (type.isPresentAstNode() && symbolTableService.hasSymbolStereotype(type.getAstNode().getModifier())) {
          Optional<ASTCDAttribute> symbolAttribute = createSymbolAttribute(type.getAstNode(),
              cdDefinitionSymbol);
          symbolAttribute.ifPresent(attr -> symbolAttributes.put(attr.getName(), attr));
        }
      }
    }
    return symbolAttributes;
  }

  protected Map<String, ASTCDAttribute> createSymbolAttributes(
      List<? extends ASTCDType> symbolClassList, DiagramSymbol cdDefinitionSymbol) {
    Map<String, ASTCDAttribute> symbolAttributeList = new LinkedHashMap<>();
    for (ASTCDType astcdClass : symbolClassList) {
      Optional<ASTCDAttribute> symbolAttributes = createSymbolAttribute(astcdClass,
          cdDefinitionSymbol);
      symbolAttributes.ifPresent(attr -> symbolAttributeList.put(attr.getName(), attr));
    }

    /*
     * In addition to the symbols included in the grammar, capabilities to parse symbols (potentially with scopes) of
     * unknown kinds is added here.
     *
     * See also: "de.monticore.symboltable.SymbolWithScopeOfUnknownKind" in monticore-runtime
     */
    ASTMCType symbolMultiMap = getMCTypeFacade()
        .createBasicGenericTypeOf(SYMBOL_MULTI_MAP, "String", "de.monticore.symboltable.SymbolWithScopeOfUnknownKind");
    ASTCDAttribute unknownSymbolsAttribute = getCDAttributeFacade()
        .createAttribute(PROTECTED.build(), symbolMultiMap, "unknownSymbols");
    this.replaceTemplate(VALUE, unknownSymbolsAttribute,
        new StringHookPoint("= com.google.common.collect.LinkedListMultimap.create()"));

    symbolAttributeList.put("unknownSymbols", unknownSymbolsAttribute);

    return symbolAttributeList;
  }

  /**
   * only returns a attribute if the cdType really defines a symbol
   */
  protected Optional<ASTCDAttribute> createSymbolAttribute(ASTCDType cdType,
      DiagramSymbol cdDefinitionSymbol) {
    Optional<String> symbolSimpleName = symbolTableService.getDefiningSymbolSimpleName(cdType);
    Optional<String> symbolFullName = symbolTableService
        .getDefiningSymbolFullName(cdType, cdDefinitionSymbol);
    if (symbolFullName.isPresent() && symbolSimpleName.isPresent()) {
      String attrName = StringTransformations.uncapitalize(symbolSimpleName.get() + LIST_SUFFIX_S);
      ASTMCType symbolMultiMap = getMCTypeFacade()
          .createBasicGenericTypeOf(SYMBOL_MULTI_MAP, "String", symbolFullName.get());
      ASTCDAttribute symbolAttribute = getCDAttributeFacade()
          .createAttribute(PROTECTED.build(), symbolMultiMap, attrName);
      this.replaceTemplate(VALUE, symbolAttribute,
          new StringHookPoint("= com.google.common.collect.LinkedListMultimap.create()"));
      return Optional.ofNullable(symbolAttribute);
    }
    return Optional.empty();
  }

  protected List<ASTCDAttribute> createSymbolAlreadyResolvedAttributes(
      Collection<String> symbolAttributeNameList) {
    List<ASTCDAttribute> symbolAttributeList = new ArrayList<>();
    for (String attributeName : symbolAttributeNameList) {
      String attrName = attributeName + ALREADY_RESOLVED;
      ASTMCType booleanType = getMCTypeFacade().createBooleanType();
      ASTCDAttribute symbolAttribute = getCDAttributeFacade()
          .createAttribute(PROTECTED.build(), booleanType, attrName);
      this.replaceTemplate(VALUE, symbolAttribute, new StringHookPoint("= false"));
      symbolAttributeList.add(symbolAttribute);
    }
    return symbolAttributeList;
  }

  protected List<ASTCDMethod> createSymbolMethods(Collection<ASTCDAttribute> astcdAttributes) {
    List<ASTCDMethod> symbolMethodList = new ArrayList<>();
    for (ASTCDAttribute attribute : astcdAttributes) {
      if (attribute.getMCType() instanceof ASTMCBasicGenericType
          && ((ASTMCBasicGenericType) attribute.getMCType()).sizeMCTypeArguments() == 2) {
        Optional<ASTMCType> mcTypeArgument = ((ASTMCBasicGenericType) attribute.getMCType())
            .getMCTypeArgument(1).getMCTypeOpt();
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
    ASTCDMethod addMethod = getCDMethodFacade().createMethod(PUBLIC.build(), "add", parameter);
    this.replaceTemplate(EMPTY_BODY, addMethod, new StringHookPoint(
        THIS + attributeName + ".put(" + SYMBOL_VAR + ".getName(), " + SYMBOL_VAR + ");\n" +
            "    " + SYMBOL_VAR + ".setEnclosingScope(this);"));
    return addMethod;
  }

  protected ASTCDMethod createRemoveSymbolMethod(ASTMCType symbolType, String attributeName) {
    ASTCDParameter parameter = getCDParameterFacade()
        .createParameter(symbolType, StringTransformations.uncapitalize(SYMBOL_SUFFIX));
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), "remove", parameter);
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint(
        THIS + attributeName + ".remove(" + SYMBOL_VAR + ".getName(), " + SYMBOL_VAR + ");"));
    return method;
  }

  protected ASTCDMethod createGetSymbolListMethod(ASTCDAttribute astcdAttribute) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), astcdAttribute.getMCType(),
        "get" + StringTransformations.capitalize(astcdAttribute.getName()));
    this.replaceTemplate(EMPTY_BODY, method,
        new StringHookPoint("return " + THIS + astcdAttribute.getName() + ";"));
    return method;
  }

  protected ASTCDAttribute createEnclosingScopeAttribute() {
    ASTCDAttribute attribute = this.getCDAttributeFacade()
        .createAttribute(PROTECTED.build(), symbolTableService.getScopeInterfaceType(),
            ENCLOSING_SCOPE_VAR);
    getDecorationHelper().addAttributeDefaultValues(attribute, glex);
    return attribute;
  }

  protected List<ASTCDMethod> createEnclosingScopeMethods(ASTCDAttribute enclosingScopeAttribute) {
    List<ASTCDMethod> enclosingScopeMethods = accessorDecorator.decorate(enclosingScopeAttribute);
    mutatorDecorator.disableTemplates();
    List<ASTCDMethod> mutatorMethods = mutatorDecorator.decorate(enclosingScopeAttribute);
    mutatorDecorator.enableTemplates();
    // only one setter, because the attribute is mandatory
    if (mutatorMethods.size() == 1) {
      this.replaceTemplate(EMPTY_BODY, mutatorMethods.get(0),
          new TemplateHookPoint(TEMPLATE_PATH + "SetEnclosingScope"));
    }
    enclosingScopeMethods.addAll(mutatorMethods);

    return enclosingScopeMethods;
  }

  protected ASTCDAttribute createSpanningSymbolAttribute() {
    ASTCDAttribute attribute = this.getCDAttributeFacade().createAttribute(PROTECTED.build(),
        getMCTypeFacade().createOptionalTypeOf(I_SCOPE_SPANNING_SYMBOL), "spanningSymbol");
    getDecorationHelper().addAttributeDefaultValues(attribute, glex);
    return attribute;
  }

  protected List<ASTCDMethod> createSpanningSymbolMethods(ASTCDAttribute spanningSymbolAttr) {
    List<ASTCDMethod> methodList = accessorDecorator.decorate(spanningSymbolAttr);

    mutatorDecorator.disableTemplates();
    List<ASTCDMethod> mutatorMethods = mutatorDecorator.decorate(spanningSymbolAttr);
    mutatorDecorator.enableTemplates();
    // needs to change the template for the setters
    for (ASTCDMethod mutatorMethod : mutatorMethods) {
      if ("setSpanningSymbol".equals(mutatorMethod.getName())) {
        this.replaceTemplate(EMPTY_BODY, mutatorMethod,
            new TemplateHookPoint(TEMPLATE_PATH + "SetSpanningSymbol", spanningSymbolAttr));
      }
      else if ("setSpanningSymbolOpt".equals(mutatorMethod.getName())) {
        this.replaceTemplate(EMPTY_BODY, mutatorMethod,
            new TemplateHookPoint(TEMPLATE_PATH + "SetSpanningSymbolOpt", spanningSymbolAttr));
      }
      else if ("setSpanningSymbolAbsent".equals(mutatorMethod.getName())) {
        this.replaceTemplate(EMPTY_BODY, mutatorMethod,
            new TemplateHookPoint(TEMPLATE_PATH + "SetSpanningSymbolAbsent", spanningSymbolAttr));
      }
    }
    methodList.addAll(mutatorMethods);
    return methodList;
  }

  protected ASTCDAttribute createShadowingAttribute() {
    return this.getCDAttributeFacade()
        .createAttribute(PROTECTED.build(), getMCTypeFacade().createBooleanType(), SHADOWING_VAR);
  }

  protected ASTCDAttribute createExportSymbolsAttribute() {
    ASTCDAttribute attribute = this.getCDAttributeFacade()
        .createAttribute(PROTECTED.build(), getMCTypeFacade().createBooleanType(), "exportingSymbols");
    this.replaceTemplate(VALUE, attribute, new StringHookPoint("= true"));
    return attribute;
  }

  protected ASTCDAttribute createOrderedAttribute() {
    return this.getCDAttributeFacade()
        .createAttribute(PROTECTED.build(), getMCTypeFacade().createBooleanType(), ORDERED_VAR);
  }

  protected ASTCDAttribute createNameAttribute() {
    ASTCDAttribute attribute = this.getCDAttributeFacade()
        .createAttribute(PROTECTED.build(), getMCTypeFacade().createOptionalTypeOf(String.class),
            NAME_VAR);
    getDecorationHelper().addAttributeDefaultValues(attribute, glex);
    return attribute;
  }

  protected ASTCDAttribute createASTNodeAttribute() {
    ASTCDAttribute attribute = this.getCDAttributeFacade()
        .createAttribute(PROTECTED.build(), getMCTypeFacade().
            createOptionalTypeOf(AST_INTERFACE), AST_NODE_VAR);
    getDecorationHelper().addAttributeDefaultValues(attribute, glex);
    return attribute;
  }

  protected ASTCDAttribute createSubScopesAttribute(ASTMCQualifiedType scopeInterfaceType) {
    ASTCDAttribute attribute = this.getCDAttributeFacade()
        .createAttribute(PROTECTED.build(), getMCTypeFacade().createListTypeOf(scopeInterfaceType),
            "subScopes");
    getDecorationHelper().addAttributeDefaultValues(attribute, glex);
    return attribute;
  }

  protected List<ASTCDMethod> createSubScopeMethods(ASTMCType scopeInterface) {
    ASTCDMethod addSubScopeMethod = createAddSubScopeMethod(scopeInterface);
    this.replaceTemplate(EMPTY_BODY, addSubScopeMethod,
        new TemplateHookPoint(TEMPLATE_PATH + "AddSubScope"));
    ASTCDMethod removeSubScopeMethod = createRemoveSubScopeMethod(scopeInterface);
    this.replaceTemplate(EMPTY_BODY, removeSubScopeMethod,
        new TemplateHookPoint(TEMPLATE_PATH + "RemoveSubScope"));
    ASTCDMethod getSubScopesMethod = createGetSubScopesMethod(scopeInterface);
    this.replaceTemplate(EMPTY_BODY, getSubScopesMethod,
        new TemplateHookPoint(TEMPLATE_PATH + "GetSubScopes"));
    ASTCDMethod setSubScopesMethod = createSetSubScopesMethod(scopeInterface);
    this.replaceTemplate(EMPTY_BODY, setSubScopesMethod,
        new TemplateHookPoint(TEMPLATE_PATH + "SetSubScopes"));
    return new ArrayList<>(Arrays
        .asList(addSubScopeMethod, removeSubScopeMethod, getSubScopesMethod, setSubScopesMethod));
  }

  protected List<ASTCDMethod> createSuperScopeMethods(String originalScopeType) {
    List<ASTCDMethod> superScopeMethods = new ArrayList<>();
    for (DiagramSymbol cdDefinitionSymbol : symbolTableService.getSuperCDsTransitive()) {
      ASTMCType scopeInterfaceType = symbolTableService.getScopeInterfaceType(cdDefinitionSymbol);

      ASTCDMethod addSubScopeMethod = createAddSubScopeMethod(scopeInterfaceType);
      this.replaceTemplate(EMPTY_BODY, addSubScopeMethod,
          new StringHookPoint("this.addSubScope((" + originalScopeType + ") subScope);"));
      superScopeMethods.add(addSubScopeMethod);

      ASTCDMethod removeSubScopeMethod = createRemoveSubScopeMethod(scopeInterfaceType);
      this.replaceTemplate(EMPTY_BODY, removeSubScopeMethod,
          new StringHookPoint("this.removeSubScope((" + originalScopeType + ") subScope);"));
      superScopeMethods.add(removeSubScopeMethod);

      ASTCDMethod setEnclosingScopeMethod = createSetEnclosingScopeMethod(scopeInterfaceType);
      this.replaceTemplate(EMPTY_BODY, setEnclosingScopeMethod, new StringHookPoint(
          "this.setEnclosingScope((" + originalScopeType + ") newEnclosingScope);"));
      superScopeMethods.add(setEnclosingScopeMethod);
    }
    return superScopeMethods;
  }

  protected ASTCDMethod createAddSubScopeMethod(ASTMCType scopeType) {
    ASTCDParameter subScopeParameter = getCDParameterFacade()
        .createParameter(scopeType, "subScope");
    return getCDMethodFacade().createMethod(PUBLIC.build(), "addSubScope", subScopeParameter);
  }

  protected ASTCDMethod createRemoveSubScopeMethod(ASTMCType scopeType) {
    ASTCDParameter subScopeParameter = getCDParameterFacade()
        .createParameter(scopeType, "subScope");
    return getCDMethodFacade().createMethod(PUBLIC.build(), "removeSubScope", subScopeParameter);
  }

  protected ASTCDMethod createGetSubScopesMethod(ASTMCType scopeType) {
    return getCDMethodFacade()
        .createMethod(PUBLIC.build(), getMCTypeFacade().createListTypeOf(scopeType), "getSubScopes");
  }

  protected ASTCDMethod createSetSubScopesMethod(ASTMCType scopeType) {
    ASTCDParameter subScopeParameter = getCDParameterFacade()
        .createParameter(getMCTypeFacade().createListTypeOf(scopeType), "subScopes");
    return getCDMethodFacade().createMethod(PUBLIC.build(), "setSubScopes", subScopeParameter);
  }

  protected ASTCDMethod createSetEnclosingScopeMethod(ASTMCType scopeType) {
    ASTCDParameter subScopeParameter = getCDParameterFacade()
        .createParameter(scopeType, "newEnclosingScope");
    return getCDMethodFacade().createMethod(PUBLIC.build(), "setEnclosingScope", subScopeParameter);
  }

  protected List<ASTCDMethod> createResolveSubKindsNameMethods(ASTCDDefinition symbolDefinition) {
    List<ASTCDMethod> result = new ArrayList<>();

    // initialize parameters that are equal for all symbol methods
    ASTCDParameter foundSymParam = getCDParameterFacade()
        .createParameter(getMCTypeFacade().createBooleanType(), FOUND_SYMBOLS_VAR);
    ASTCDParameter nameParam = getCDParameterFacade().createParameter(String.class, NAME_VAR);
    ASTCDParameter accModParam = getCDParameterFacade()
        .createParameter(getMCTypeFacade().createQualifiedType(ACCESS_MODIFIER), MODIFIER_VAR);

    List<ASTCDType> symbols = symbolTableService.getSymbolDefiningProds(symbolDefinition);
    symbols.addAll(symbolTableService.getSymbolDefiningSuperProds());

    ListMultimap<String, String> subKinds = SymbolKindHierarchies
        .calculateSubKinds(symbols, symbolTableService);

    //add methods for local symbols
    for (ASTCDType s : symbolTableService.getSymbolDefiningProds(symbolDefinition)) {
      String name = symbolTableService.getSymbolFullName(s);
      String simpleName = Names.getSimpleName(name);
      result.add(createResolveSubKindsMethod(name, s.getName(), subKinds.get(simpleName),
          foundSymParam, nameParam, accModParam));
    }

    //add symbols from super grammars
    for (DiagramSymbol cdDefinitionSymbol : symbolTableService.getSuperCDsTransitive()) {
      for (CDTypeSymbol type : symbolTableService.getAllCDTypes(cdDefinitionSymbol)) {
        if (type.isPresentAstNode() && symbolTableService.hasSymbolStereotype(type.getAstNode().getModifier())) {
          String name = symbolTableService.getSymbolFullName(type.getAstNode(), cdDefinitionSymbol);
          String ntName = type.getAstNode().getName().substring(3);
          result.add(createResolveSubKindsMethod(name, ntName, subKinds.get(ntName),
              foundSymParam, nameParam, accModParam));
        }
      }
    }
    return result;
  }

  protected ASTCDMethod createResolveSubKindsMethod(String symbolFullName, String symbolNTName,
      List<String> subKinds, ASTCDParameter foundSymbolsParameter, ASTCDParameter nameParameter,
      ASTCDParameter accessModifierParameter) {

    String methodName = String.format("resolve%sSubKinds", symbolNTName);

    ASTCDParameter predicateParameter = getCDParameterFacade().createParameter(getMCTypeFacade()
        .createBasicGenericTypeOf(PREDICATE, symbolFullName), PREDICATE_VAR);
    ASTMCType listSymbol = getMCTypeFacade().createListTypeOf(symbolFullName);

    ASTCDMethod method = getCDMethodFacade()
        .createMethod(PUBLIC.build(), listSymbol, methodName,
            foundSymbolsParameter, nameParameter, accessModifierParameter, predicateParameter);

    this.replaceTemplate(EMPTY_BODY, method,
        new TemplateHookPoint(TEMPLATE_PATH + "ResolveSubKinds", symbolFullName, subKinds));
    return method;
  }

  public boolean isScopeTop() {
    return isScopeTop;
  }

  public void setScopeTop(boolean scopeTop) {
    isScopeTop = scopeTop;
  }
}
