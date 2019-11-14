package de.monticore.codegen.cd2java._symboltable.scope;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbolTOP;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCMapType;
import de.se_rwth.commons.StringTransformations;

import java.util.*;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;
import static de.monticore.cd.facade.CDModifier.*;

/**
 * creates a globalScope class from a grammar
 */
public class GlobalScopeClassDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDClass> {

  protected final SymbolTableService symbolTableService;

  protected final MethodDecorator methodDecorator;

  protected final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> accessorDecorator;

  protected final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> mutatorDecorator;

  protected static final String ADAPTED_RESOLVING_DELEGATE = "adapted%sResolvingDelegate";

  protected static final String TEMPLATE_PATH = "_symboltable.globalscope.";

  public GlobalScopeClassDecorator(final GlobalExtensionManagement glex,
                                   final SymbolTableService symbolTableService,
                                   final MethodDecorator methodDecorator) {
    super(glex);
    this.symbolTableService = symbolTableService;
    this.methodDecorator = methodDecorator;
    this.accessorDecorator = methodDecorator.getAccessorDecorator();
    this.mutatorDecorator = methodDecorator.getMutatorDecorator();
  }

  @Override
  public ASTCDClass decorate(ASTCDCompilationUnit input) {
    String globalScopeName = symbolTableService.getGlobalScopeSimpleName();
    ASTMCQualifiedType scopeType = symbolTableService.getScopeType();
    ASTMCQualifiedType globalScopeInterfaceType = getMCTypeFacade().createQualifiedType(symbolTableService.getGlobalScopeInterfaceFullName());
    String definitionName = input.getCDDefinition().getName();
    String modelLoaderClassName = symbolTableService.getModelLoaderClassSimpleName();

    List<ASTCDType> symbolProds = symbolTableService.getSymbolDefiningProds(input.getCDDefinition());

    ASTCDAttribute modelPathAttribute = createModelPathAttribute();
    List<ASTCDMethod> modelPathMethods = accessorDecorator.decorate(modelPathAttribute);

    ASTCDAttribute languageAttribute = createLanguageAttribute(definitionName);
    List<ASTCDMethod> languageMethods = accessorDecorator.decorate(languageAttribute);

    Map<String, ASTCDAttribute> resolvingDelegateAttributes = createResolvingDelegateAttributes(symbolProds);
    resolvingDelegateAttributes.putAll(createResolvingDelegateSuperAttributes());

    List<ASTCDMethod> resolvingDelegateMethods = resolvingDelegateAttributes.values()
        .stream()
        .map(methodDecorator::decorate)
        .flatMap(List::stream)
        .collect(Collectors.toList());

    return CD4AnalysisMill.cDClassBuilder()
        .setName(globalScopeName)
        .setModifier(PUBLIC.build())
        .setSuperclass(scopeType)
        .addInterface(globalScopeInterfaceType)
        .addCDConstructor(createConstructor(globalScopeName, definitionName))
        .addCDAttribute(modelPathAttribute)
        .addAllCDMethods(modelPathMethods)
        .addCDAttribute(languageAttribute)
        .addAllCDMethods(languageMethods)
        .addCDAttribute(createModelName2ModelLoaderCacheAttribute(modelLoaderClassName))
        .addAllCDAttributes(resolvingDelegateAttributes.values())
        .addCDMethod(createGetNameMethod())
        .addCDMethod(createCacheMethod(definitionName))
        .addCDMethod(createContinueWithModelLoaderMethod(modelLoaderClassName))
        .addAllCDMethods(resolvingDelegateMethods)
        .addAllCDMethods(createAlreadyResolvedMethods(symbolProds))
        .addAllCDMethods(createAlreadyResolvedSuperMethods())
        .addAllCDMethods(createEnclosingScopeMethods(globalScopeName))
        .addAllCDMethods(createResolveAdaptedSuperMethods())
        .addAllCDMethods(createResolveAdaptedMethods(symbolProds))
        .build();
  }

  protected ASTCDConstructor createConstructor(String globalScopeClassName, String definitionName) {
    ASTMCType modelPathType = getMCTypeFacade().createQualifiedType(MODEL_PATH_TYPE);
    ASTCDParameter modelPathParameter = getCDParameterFacade().createParameter(modelPathType, MODEL_PATH_VAR);

    ASTMCType languageType = getMCTypeFacade().createQualifiedType(definitionName + LANGUAGE_SUFFIX);
    ASTCDParameter languageParameter = getCDParameterFacade().createParameter(languageType, "language");

    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), globalScopeClassName, modelPathParameter, languageParameter);

    this.replaceTemplate(EMPTY_BODY, constructor, new StringHookPoint("this." + MODEL_PATH_VAR + " = Log.errorIfNull(" + MODEL_PATH_VAR + ");\n" +
        "    this." + StringTransformations.uncapitalize(definitionName) + "Language = Log.errorIfNull(language);"));
    return constructor;
  }

  protected ASTCDAttribute createModelPathAttribute() {
    return getCDAttributeFacade().createAttribute(PROTECTED, MODEL_PATH_TYPE, MODEL_PATH_VAR);
  }

  protected ASTCDAttribute createLanguageAttribute(String definitionName) {
    String languageName = definitionName + LANGUAGE_SUFFIX;
    return getCDAttributeFacade().createAttribute(PROTECTED, languageName, StringTransformations.uncapitalize(languageName));
  }

  protected ASTCDAttribute createModelName2ModelLoaderCacheAttribute(String modelLoaderClassName) {
    ASTMCMapType mapType = getMCTypeFacade().createMapTypeOf("String", "Set<" + modelLoaderClassName + ">");
    ASTCDAttribute modelName2ModelLoaderCache = getCDAttributeFacade().createAttribute(PROTECTED_FINAL, mapType, "modelName2ModelLoaderCache");
    this.replaceTemplate(VALUE, modelName2ModelLoaderCache, new StringHookPoint(" = new HashMap<>()"));
    return modelName2ModelLoaderCache;
  }


  protected Map<String, ASTCDAttribute> createResolvingDelegateAttributes(List<? extends ASTCDType> symbolProds) {
    Map<String, ASTCDAttribute> attributeList = new HashMap<>();
    for (ASTCDType symbolProd : symbolProds) {
      Optional<ASTCDAttribute> symbolAttribute = createResolvingDelegateAttribute(symbolProd, symbolTableService.getCDSymbol());
      symbolAttribute.ifPresent(attr -> attributeList.put(attr.getName(), attr));
    }
    return attributeList;
  }

  protected Map<String, ASTCDAttribute> createResolvingDelegateSuperAttributes() {
    Map<String, ASTCDAttribute> symbolAttributes = new HashMap<>();
    for (CDDefinitionSymbol cdDefinitionSymbol : symbolTableService.getSuperCDsTransitive()) {
      for (CDTypeSymbol type : cdDefinitionSymbol.getTypes()) {
        if (type.isPresentAstNode() && type.getAstNode().getModifierOpt().isPresent()
            && symbolTableService.hasSymbolStereotype(type.getAstNode().getModifierOpt().get())) {
          Optional<ASTCDAttribute> symbolAttribute = createResolvingDelegateAttribute(type.getAstNode(), cdDefinitionSymbol);
          symbolAttribute.ifPresent(attr -> symbolAttributes.put(attr.getName(), attr));
        }
      }
    }
    return symbolAttributes;
  }

  protected Optional<ASTCDAttribute> createResolvingDelegateAttribute(ASTCDType prod, CDDefinitionSymbol cdSymbol) {
    Optional<String> simpleName = symbolTableService.getDefiningSymbolSimpleName(prod);
    if (simpleName.isPresent()) {
      String attrName = String.format(ADAPTED_RESOLVING_DELEGATE, simpleName.get());
      String symbolResolvingDelegateInterfaceTypeName = symbolTableService.getSymbolResolvingDelegateInterfaceFullName(prod, cdSymbol);
      ASTMCType listType = getMCTypeFacade().createListTypeOf(symbolResolvingDelegateInterfaceTypeName);

      ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PROTECTED, listType, attrName);
      this.replaceTemplate(VALUE, attribute, new StringHookPoint(" = new ArrayList<" + symbolResolvingDelegateInterfaceTypeName + ">()"));
      return Optional.ofNullable(attribute);
    }
    return Optional.empty();
  }

  protected ASTCDMethod createGetNameMethod() {
    ASTMCObjectType optOfString = getMCTypeFacade().createOptionalTypeOf(String.class);
    ASTCDMethod getNameMethod = getCDMethodFacade().createMethod(PUBLIC, optOfString, "getNameOpt");
    this.replaceTemplate(EMPTY_BODY, getNameMethod, new StringHookPoint("return Optional.empty();"));
    return getNameMethod;
  }

  protected ASTCDMethod createCacheMethod(String definitionName) {
    ASTCDParameter parameter = getCDParameterFacade().createParameter(getMCTypeFacade().createStringType(), CALCULATED_MODEL_NAME);
    ASTCDMethod cacheMethod = getCDMethodFacade().createMethod(PUBLIC, "cache", parameter);
    this.replaceTemplate(EMPTY_BODY, cacheMethod, new TemplateHookPoint(TEMPLATE_PATH + "CacheMethod", definitionName));
    return cacheMethod;
  }

  protected ASTCDMethod createContinueWithModelLoaderMethod(String modelLoaderClassName) {
    ASTCDParameter modelNameParameter = getCDParameterFacade().createParameter(getMCTypeFacade().createStringType(), CALCULATED_MODEL_NAME);
    ASTMCQualifiedType modelLoaderType = getMCTypeFacade().createQualifiedType(modelLoaderClassName);
    ASTCDParameter modelLoaderParameter = getCDParameterFacade().createParameter(modelLoaderType, MODEL_LOADER_VAR);

    ASTCDMethod continueWithModelLoaderMethod = getCDMethodFacade().createMethod(PUBLIC, getMCTypeFacade().createBooleanType(), "continueWithModelLoader", modelNameParameter, modelLoaderParameter);
    this.replaceTemplate(EMPTY_BODY, continueWithModelLoaderMethod,
        new StringHookPoint("    return !modelName2ModelLoaderCache.containsKey(" + CALCULATED_MODEL_NAME + ")\n" +
            "      || !modelName2ModelLoaderCache.get(" + CALCULATED_MODEL_NAME + ").contains(" + MODEL_LOADER_VAR + ");"));
    return continueWithModelLoaderMethod;
  }

  protected List<ASTCDMethod> createAlreadyResolvedMethods(List<? extends ASTCDType> cdTypeList) {
    List<ASTCDAttribute> symbolAlreadyResolvedAttributes = createSymbolAlreadyResolvedAttributes(cdTypeList);
    return symbolAlreadyResolvedAttributes
        .stream()
        .map(methodDecorator::decorate)
        .flatMap(List::stream)
        .collect(Collectors.toList());
  }


  protected List<ASTCDMethod> createAlreadyResolvedSuperMethods() {
    List<ASTCDAttribute> symbolAlreadyResolvedAttributes = new ArrayList<>();
    for (CDDefinitionSymbol cdDefinitionSymbol : symbolTableService.getSuperCDsTransitive()) {
      // only types that define a symbol
      List<ASTCDType> symbolProds = cdDefinitionSymbol.getTypes().stream().filter(t -> t.getAstNodeOpt().isPresent())
          .filter(t -> t.getAstNode().getModifierOpt().isPresent())
          .filter(t -> symbolTableService.hasSymbolStereotype(t.getAstNode().getModifierOpt().get()))
          .map(CDTypeSymbolTOP::getAstNodeOpt)
          .map(Optional::get)
          .collect(Collectors.toList());
      symbolAlreadyResolvedAttributes.addAll(createSymbolAlreadyResolvedAttributes(symbolProds));
    }
    return symbolAlreadyResolvedAttributes
        .stream()
        .map(methodDecorator::decorate)
        .flatMap(List::stream)
        .collect(Collectors.toList());
  }

  protected List<ASTCDAttribute> createSymbolAlreadyResolvedAttributes(List<? extends ASTCDType> astcdTypeList) {

    List<String> symbolAttributeNameList = astcdTypeList
        .stream()
        .map(symbolTableService::getDefiningSymbolSimpleName)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(s -> s + "s")
        .map(StringTransformations::uncapitalize)
        .collect(Collectors.toList());

    List<ASTCDAttribute> symbolAttributeList = new ArrayList<>();
    for (String attributeName : symbolAttributeNameList) {
      String attrName = attributeName + ALREADY_RESOLVED;
      ASTMCType booleanType = getMCTypeFacade().createBooleanType();
      ASTCDAttribute symbolAttribute = getCDAttributeFacade().createAttribute(PROTECTED, booleanType, attrName);
      this.replaceTemplate(VALUE, symbolAttribute, new StringHookPoint("= false;"));
      symbolAttributeList.add(symbolAttribute);
    }
    return symbolAttributeList;
  }

  protected List<ASTCDMethod> createEnclosingScopeMethods(String globalScopeName) {
    // create attribute just for method generation purposes
    ASTCDAttribute enclosingScopeAttribute = this.getCDAttributeFacade()
            .createAttribute(PROTECTED,
        symbolTableService.getScopeInterfaceType(), ENCLOSING_SCOPE_VAR);
    symbolTableService.addAttributeDefaultValues(enclosingScopeAttribute, glex);

    methodDecorator.disableTemplates();
    List<ASTCDMethod> enclosingScopeMethods = methodDecorator.decorate(enclosingScopeAttribute);
    methodDecorator.enableTemplates();
    for (ASTCDMethod enclosingScopeMethod : enclosingScopeMethods) {
      String generatedErrorCode = DecorationHelper.getGeneratedErrorCode(enclosingScopeMethod);
      // add return null if method has return type
      if (enclosingScopeMethod.getMCReturnType().isPresentMCType()) {
        this.replaceTemplate(EMPTY_BODY, enclosingScopeMethod, new StringHookPoint(
            "Log.error(\"0xA6100" + generatedErrorCode + " GlobalScope " + globalScopeName +
                " has no EnclosingScope, so you cannot call method" + enclosingScopeMethod.getName() + ".\");\n" +
                "    return null;"));
      } else {
        // no return if method is void type
        this.replaceTemplate(EMPTY_BODY, enclosingScopeMethod, new StringHookPoint(
            "Log.error(\"0xA6100" + generatedErrorCode + " GlobalScope " + globalScopeName +
                " has no EnclosingScope, so you cannot call method" + enclosingScopeMethod.getName() + ".\");"));
      }
    }
    return enclosingScopeMethods;
  }

  protected List<ASTCDMethod> createResolveAdaptedMethods(List<ASTCDType> symbolProds) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    ASTCDParameter nameParameter = getCDParameterFacade().createParameter(String.class, NAME_VAR);
    ASTCDParameter accessModifierParameter = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(ACCESS_MODIFIER), MODIFIER_VAR);
    ASTCDParameter foundSymbolsParameter = getCDParameterFacade().createParameter(getMCTypeFacade().createBooleanType(), FOUND_SYMBOLS_VAR);

    for (ASTCDType symbolProd : symbolProds) {
      methodList.add(createResolveAdaptedMethod(symbolProd, symbolTableService.getCDSymbol(), foundSymbolsParameter, nameParameter,
          accessModifierParameter));
    }
    return methodList;
  }


  protected ASTCDMethod createResolveAdaptedMethod(ASTCDType symbolProd, CDDefinitionSymbol cdDefinitionSymbol,
                                                   ASTCDParameter foundSymbolsParameter, ASTCDParameter nameParameter,
                                                   ASTCDParameter accessModifierParameter) {
    String symbolFullName = symbolTableService.getSymbolFullName(symbolProd, cdDefinitionSymbol);
    String symbolSimpleName = symbolTableService.getSymbolSimpleName(symbolProd);
    String symbolResolvingDelegateInterfaceFullName = symbolTableService.getSymbolResolvingDelegateInterfaceFullName(symbolProd, cdDefinitionSymbol);
    ASTCDParameter predicateParameter = getCDParameterFacade().createParameter(
        getMCTypeFacade().createBasicGenericTypeOf(PREDICATE, symbolFullName), PREDICATE_VAR);
    String methodName = String.format(RESOLVE_ADAPTED, symbolTableService.removeASTPrefix(symbolProd.getName()));

    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, getMCTypeFacade().createListTypeOf(symbolFullName), methodName,
        foundSymbolsParameter, nameParameter, accessModifierParameter, predicateParameter);

    this.replaceTemplate(EMPTY_BODY, method,
        new TemplateHookPoint(TEMPLATE_PATH + "ResolveAdapted", symbolFullName, symbolResolvingDelegateInterfaceFullName, symbolSimpleName));
    return method;
  }

  protected List<ASTCDMethod> createResolveAdaptedSuperMethods() {
    ASTCDParameter nameParameter = getCDParameterFacade().createParameter(String.class, NAME_VAR);
    ASTCDParameter accessModifierParameter = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(ACCESS_MODIFIER), MODIFIER_VAR);
    ASTCDParameter foundSymbolsParameter = getCDParameterFacade().createParameter(getMCTypeFacade().createBooleanType(), FOUND_SYMBOLS_VAR);

    List<ASTCDMethod> methodList = new ArrayList<>();
    for (CDDefinitionSymbol cdDefinitionSymbol : symbolTableService.getSuperCDsTransitive()) {
      for (CDTypeSymbol type : cdDefinitionSymbol.getTypes()) {
        if (type.isPresentAstNode() && type.getAstNode().getModifierOpt().isPresent()
            && symbolTableService.hasSymbolStereotype(type.getAstNode().getModifierOpt().get())) {
          methodList.add(createResolveAdaptedMethod(type.getAstNode(), cdDefinitionSymbol, foundSymbolsParameter, nameParameter,
              accessModifierParameter));
        }
      }
    }
    return methodList;
  }
}
