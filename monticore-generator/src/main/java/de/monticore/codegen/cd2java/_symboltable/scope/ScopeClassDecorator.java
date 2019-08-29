package de.monticore.codegen.cd2java._symboltable.scope;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.codegen.cd2java.AbstractCreator;
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

public class ScopeClassDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDClass> {

  protected final SymbolTableService symbolTableService;

  protected final VisitorService visitorService;

  protected final MethodDecorator methodDecorator;

  protected final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> accessorDecorator;

  protected final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> mutatorDecorator;

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

  @Override
  public ASTCDClass decorate(ASTCDCompilationUnit input) {
    String scopeClassName = input.getCDDefinition().getName() + SCOPE_SUFFIX;
    ASTMCQualifiedType scopeInterfaceType = symbolTableService.getScopeInterfaceType();

    List<ASTCDType> symbolClasses = symbolTableService.getSymbolDefiningProds(input.getCDDefinition());

    Map<String, ASTCDAttribute> symbolAttributes = createSymbolAttributes(symbolClasses, symbolTableService.getCDSymbol());
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
    List<ASTCDConstructor> constructors = new ArrayList<>();
    ASTCDConstructor defaultConstructor = createDefaultConstructor(scopeClassName);
    ASTCDConstructor isShadowingConstructor = createIsShadowingConstructor(scopeClassName);
    ASTCDConstructor enclosingScopeConstructor = createEnclosingScopeConstructor(scopeClassName);
    ASTCDConstructor isShadowingAndEnclosingScopeConstructor = createIsShadowingAndEnclosingScopeConstructor(scopeClassName);
    return new ArrayList<>(Arrays.asList(defaultConstructor, isShadowingConstructor, enclosingScopeConstructor, isShadowingAndEnclosingScopeConstructor));
  }

  protected ASTCDConstructor createDefaultConstructor(String scopeClassName) {
    ASTCDConstructor defaultConstructor = getCDConstructorFacade().createConstructor(PUBLIC, scopeClassName);
    this.replaceTemplate(EMPTY_BODY, defaultConstructor, new StringHookPoint("super();\n" +
        "    this.name = Optional.empty();"));
    return defaultConstructor;
  }

  protected ASTCDConstructor createEnclosingScopeConstructor(String scopeClassName) {
    ASTCDParameter scopeParameter = getCDParameterFacade().createParameter(symbolTableService.getScopeInterfaceType(), ENCLOSING_SCOPE);
    ASTCDConstructor defaultConstructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), scopeClassName, scopeParameter);
    this.replaceTemplate(EMPTY_BODY, defaultConstructor, new StringHookPoint("this(enclosingScope, false);"));
    return defaultConstructor;
  }

  protected ASTCDConstructor createIsShadowingConstructor(String scopeClassName) {
    ASTCDParameter shadowingParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createBooleanType(), SHADOWING);
    ASTCDConstructor defaultConstructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), scopeClassName, shadowingParameter);
    this.replaceTemplate(EMPTY_BODY, defaultConstructor, new StringHookPoint("this." + SHADOWING + " = " + SHADOWING + ";\n" +
        "    this.name = Optional.empty();"));
    return defaultConstructor;
  }

  protected ASTCDConstructor createIsShadowingAndEnclosingScopeConstructor(String scopeClassName) {
    ASTCDParameter shadowingParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createBooleanType(), SHADOWING);
    ASTCDParameter scopeParameter = getCDParameterFacade().createParameter(symbolTableService.getScopeInterfaceType(), ENCLOSING_SCOPE);
    ASTCDConstructor defaultConstructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), scopeClassName, scopeParameter, shadowingParameter);
    this.replaceTemplate(EMPTY_BODY, defaultConstructor, new StringHookPoint("this.setEnclosingScope(enclosingScope);\n" +
        "    this." + SHADOWING + " = " + SHADOWING + "; \n" +
        "    this.name = Optional.empty();"));
    return defaultConstructor;
  }

  protected List<ASTCDMethod> createAcceptMethods(String scopeClassName) {
    List<ASTCDMethod> acceptMethods = new ArrayList<>();

    String ownScopeVisitor = visitorService.getScopeVisitorFullName();
    ASTCDParameter parameter = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(ownScopeVisitor), VISITOR_PREFIX);
    ASTCDMethod ownAcceptMethod = getCDMethodFacade().createMethod(PUBLIC, ACCEPT_METHOD, parameter);
    this.replaceTemplate(EMPTY_BODY, ownAcceptMethod, new StringHookPoint("visitor.handle(this);"));
    acceptMethods.add(ownAcceptMethod);

    for (CDDefinitionSymbol cdDefinitionSymbol : symbolTableService.getSuperCDsTransitive()) {
      String superScopeVisitor = visitorService.getScopeVisitorFullName(cdDefinitionSymbol);
      ASTCDParameter SuperVisitorParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(superScopeVisitor), VISITOR_PREFIX);
      ASTCDMethod acceptMethod = getCDMethodFacade().createMethod(PUBLIC, ACCEPT_METHOD, SuperVisitorParameter);
      String errorCode = DecorationHelper.getGeneratedErrorCode(acceptMethod);
      this.replaceTemplate(EMPTY_BODY, acceptMethod, new TemplateHookPoint("_symboltable.scope.clazz.Accept", ownScopeVisitor, scopeClassName, superScopeVisitor, errorCode));
      acceptMethods.add(acceptMethod);
    }

    return acceptMethods;
  }

  protected ASTCDMethod createSymbolsSizeMethod(Collection<String> symbolAttributeNames) {
    ASTCDMethod getSymbolSize = getCDMethodFacade().createMethod(PUBLIC, getCDTypeFacade().createIntType(), "getSymbolsSize");
    if (symbolAttributeNames.isEmpty()) {
      this.replaceTemplate(EMPTY_BODY, getSymbolSize, new StringHookPoint("return 0;"));
    } else {
      this.replaceTemplate(EMPTY_BODY, getSymbolSize, new TemplateHookPoint("_symboltable.scope.clazz.GetSymbolSize", symbolAttributeNames));
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
      ASTMCType symbolMultiMap = getCDTypeFacade().createTypeByDefinition(String.format(SYMBOLS_MULTI_MAP, symbolFullName.get()));
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
      this.replaceTemplate(VALUE, symbolAttribute, new StringHookPoint("= false;"));
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
    ASTCDParameter parameter = getCDParameterFacade().createParameter(symbolType, "symbol");
    ASTCDMethod addMethod = getCDMethodFacade().createMethod(PUBLIC, "add", parameter);
    this.replaceTemplate(EMPTY_BODY, addMethod, new StringHookPoint("this." + attributeName + ".put(symbol.getName(), symbol);\n" +
        "    symbol.setEnclosingScope(this);"));
    return addMethod;
  }

  protected ASTCDMethod createRemoveSymbolMethod(ASTMCType symbolType, String attributeName) {
    ASTCDParameter parameter = getCDParameterFacade().createParameter(symbolType, StringTransformations.uncapitalize(SYMBOL_SUFFIX));
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, "remove", parameter);
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("this." + attributeName + ".remove(symbol.getName(), symbol);"));
    return method;
  }

  protected ASTCDMethod createGetSymbolListMethod(ASTCDAttribute astcdAttribute) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, astcdAttribute.getMCType(), "get" + StringTransformations.capitalize(astcdAttribute.getName()));
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return this." + astcdAttribute.getName() + ";"));
    return method;
  }

  protected ASTCDAttribute createEnclosingScopeAttribute() {
    ASTCDAttribute enclosingScope = this.getCDAttributeFacade().createAttribute(PROTECTED,
        getCDTypeFacade().createOptionalTypeOf(symbolTableService.getScopeInterfaceType()), "enclosingScope");
    this.replaceTemplate(VALUE, enclosingScope, new StringHookPoint("= Optional.empty();"));
    return enclosingScope;
  }

  protected List<ASTCDMethod> createEnclosingScopeMethods(ASTCDAttribute enclosingScopeAttribute) {
    List<ASTCDMethod> enclosingScopeMethods = accessorDecorator.decorate(enclosingScopeAttribute);

    mutatorDecorator.disableTemplates();
    List<ASTCDMethod> mutatorMethods = mutatorDecorator.decorate(enclosingScopeAttribute);
    mutatorDecorator.enableTemplates();
    for (ASTCDMethod mutatorMethod : mutatorMethods) {
      if (mutatorMethod.getName().equals("setEnclosingScope")) {
        this.replaceTemplate(EMPTY_BODY, mutatorMethod, new TemplateHookPoint("methods.opt.Set", enclosingScopeAttribute));
      } else if (mutatorMethod.getName().equals("setEnclosingScopeOpt")) {
        this.replaceTemplate(EMPTY_BODY, mutatorMethod, new TemplateHookPoint("_symboltable.scope.clazz.SetEnclosingScopeOpt"));
      } else if (mutatorMethod.getName().equals("setEnclosingScopeAbsent")) {
        this.replaceTemplate(EMPTY_BODY, mutatorMethod, new TemplateHookPoint("methods.opt.SetAbsent", enclosingScopeAttribute));
      }
    }
    enclosingScopeMethods.addAll(mutatorMethods);
    return enclosingScopeMethods;
  }

  protected ASTCDAttribute createSpanningSymbolAttribute() {
    ASTCDAttribute spanningSymbol = this.getCDAttributeFacade().createAttribute(PROTECTED, getCDTypeFacade().createOptionalTypeOf(I_SCOPE_SPANNING_SYMBOL), "spanningSymbol");
    this.replaceTemplate(VALUE, spanningSymbol, new StringHookPoint("= Optional.empty();"));
    return spanningSymbol;
  }

  protected List<ASTCDMethod> createSpanningSymbolMethods(ASTCDAttribute spanningSymbolAttr) {
    List<ASTCDMethod> methodList = accessorDecorator.decorate(spanningSymbolAttr);

    mutatorDecorator.disableTemplates();
    List<ASTCDMethod> mutatorMethods = mutatorDecorator.decorate(spanningSymbolAttr);
    mutatorDecorator.enableTemplates();
    for (ASTCDMethod mutatorMethod : mutatorMethods) {
      if (mutatorMethod.getName().equals("setSpanningSymbol")) {
        this.replaceTemplate(EMPTY_BODY, mutatorMethod, new TemplateHookPoint("_symboltable.scope.clazz.SetSpanningSymbol", spanningSymbolAttr));
      } else if (mutatorMethod.getName().equals("setSpanningSymbolOpt")) {
        this.replaceTemplate(EMPTY_BODY, mutatorMethod, new TemplateHookPoint("_symboltable.scope.clazz.SetSpanningSymbolOpt", spanningSymbolAttr));
      } else if (mutatorMethod.getName().equals("setSpanningSymbolAbsent")) {
        this.replaceTemplate(EMPTY_BODY, mutatorMethod, new TemplateHookPoint("_symboltable.scope.clazz.SetSpanningSymbolAbsent", spanningSymbolAttr));
      }
    }
    methodList.addAll(mutatorMethods);
    return methodList;
  }

  protected ASTCDAttribute createShadowingAttribute() {
    return this.getCDAttributeFacade().createAttribute(PROTECTED, getCDTypeFacade().createBooleanType(), SHADOWING);
  }

  protected ASTCDAttribute createExportSymbolsAttribute() {
    ASTCDAttribute attribute = this.getCDAttributeFacade().createAttribute(PROTECTED, getCDTypeFacade().createBooleanType(), "exportsSymbols");
    this.replaceTemplate(VALUE, attribute, new StringHookPoint("= true;"));
    return attribute;
  }

  protected ASTCDAttribute createNameAttribute() {
    ASTCDAttribute name = this.getCDAttributeFacade().createAttribute(PROTECTED, getCDTypeFacade().createOptionalTypeOf(String.class), "name");
    this.replaceTemplate(VALUE, name, new StringHookPoint("= Optional.empty();"));
    return name;
  }

  protected ASTCDAttribute createASTNodeAttribute() {
    ASTCDAttribute astNode = this.getCDAttributeFacade().createAttribute(PROTECTED, getCDTypeFacade().createOptionalTypeOf(AST_INTERFACE), "astNode");
    this.replaceTemplate(VALUE, astNode, new StringHookPoint("= Optional.empty();"));
    return astNode;
  }

  protected ASTCDAttribute createSubScopesAttribute(ASTMCQualifiedType scopeInterfaceType) {
    ASTCDAttribute subScopes = this.getCDAttributeFacade().createAttribute(PROTECTED, getCDTypeFacade().createListTypeOf(scopeInterfaceType), "subScopes");
    this.replaceTemplate(VALUE, subScopes, new StringHookPoint("= new java.util.ArrayList<>();"));
    return subScopes;
  }

  protected List<ASTCDMethod> createSubScopeMethods(ASTMCType scopeInterface) {
    ASTCDMethod addSubScopeMethod = createAddSubScopeMethod(scopeInterface);
    this.replaceTemplate(EMPTY_BODY, addSubScopeMethod, new TemplateHookPoint("_symboltable.scope.clazz.AddSubScope"));
    ASTCDMethod removeSubScopeMethod = createRemoveSubScopeMethod(scopeInterface);
    this.replaceTemplate(EMPTY_BODY, removeSubScopeMethod, new TemplateHookPoint("_symboltable.scope.clazz.RemoveSubScope"));
    ASTCDMethod getSubScopesMethod = createGetSubScopesMethod(scopeInterface);
    this.replaceTemplate(EMPTY_BODY, getSubScopesMethod, new TemplateHookPoint("_symboltable.scope.clazz.GetSubScopes"));
    ASTCDMethod setSubScopesMethod = createSetSubScopesMethod(scopeInterface);
    this.replaceTemplate(EMPTY_BODY, setSubScopesMethod, new TemplateHookPoint("_symboltable.scope.clazz.SetSubScopes"));
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
}
