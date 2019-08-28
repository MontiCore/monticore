package de.monticore.codegen.cd2java._symboltable.scope;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
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
import static de.monticore.codegen.cd2java.factories.CDModifier.*;

public class GlobalScopeClassDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDClass> {

  protected final SymbolTableService symbolTableService;

  protected final MethodDecorator methodDecorator;

  protected final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> accessorDecorator;

  protected final AbstractCreator<ASTCDAttribute, List<ASTCDMethod>> mutatorDecorator;

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
    ASTMCQualifiedType globalScopeInterfaceType = getCDTypeFacade().createQualifiedType(symbolTableService.getGlobalScopeInterfaceFullName());
    String definitionName = input.getCDDefinition().getName();

    List<ASTCDType> symbolProds = symbolTableService.getSymbolProds(input.getCDDefinition());

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
        .addCDAttribute(createModelName2ModelLoaderCacheAttribute(definitionName))
        .addAllCDAttributes(resolvingDelegateAttributes.values())
        .addCDMethod(createGetNameMethod())
        .addCDMethod(createCacheMethod(definitionName))
        .addCDMethod(createContinueWithModelLoaderMethod(definitionName))
        .addAllCDMethods(resolvingDelegateMethods)
        .addAllCDMethods(createAddResolvingSymbolDelegateMethods(resolvingDelegateAttributes.values()))
        .addAllCDMethods(createAlreadyResolvedMethods(symbolProds))
        .build();
  }

  protected ASTCDConstructor createConstructor(String globalScopeClassName, String definitionName) {
    ASTMCType modelPathType = getCDTypeFacade().createQualifiedType(MODEL_PATH_TYPE);
    ASTCDParameter modelPathParameter = getCDParameterFacade().createParameter(modelPathType, MODEL_PATH_NAME);

    ASTMCType languageType = getCDTypeFacade().createQualifiedType(definitionName + LANGUAGE_SUFFIX);
    ASTCDParameter languageParameter = getCDParameterFacade().createParameter(languageType, "language");

    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), globalScopeClassName, modelPathParameter, languageParameter);

    this.replaceTemplate(EMPTY_BODY, constructor, new StringHookPoint("this.modelPath = Log.errorIfNull(modelPath);\n" +
        "    this." + StringTransformations.uncapitalize(definitionName) + "Language = Log.errorIfNull(language);"));
    return constructor;
  }

  protected ASTCDAttribute createModelPathAttribute() {
    return getCDAttributeFacade().createAttribute(PROTECTED, MODEL_PATH_TYPE, MODEL_PATH_NAME);
  }

  protected ASTCDAttribute createLanguageAttribute(String definitionName) {
    String languageName = definitionName + LANGUAGE_SUFFIX;
    return getCDAttributeFacade().createAttribute(PROTECTED, languageName, StringTransformations.uncapitalize(languageName));
  }

  protected ASTCDAttribute createModelName2ModelLoaderCacheAttribute(String definitionName) {
    ASTMCMapType mapType = getCDTypeFacade().createMapTypeOf("String", "Set<" + definitionName + MODEL_LOADER_SUFFIX + ">");
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
        if (type.getAstNode().isPresent() && type.getAstNode().get().getModifierOpt().isPresent()
            && symbolTableService.hasSymbolStereotype(type.getAstNode().get().getModifierOpt().get())) {
          Optional<ASTCDAttribute> symbolAttribute = createResolvingDelegateAttribute(type.getAstNode().get(), cdDefinitionSymbol);
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
      ASTMCType listType = getCDTypeFacade().createCollectionTypeOf(symbolResolvingDelegateInterfaceTypeName);

      ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PROTECTED, listType, attrName);
      this.replaceTemplate(VALUE, attribute, new StringHookPoint(" = new HashSet<" + symbolResolvingDelegateInterfaceTypeName + ">()"));
      return Optional.ofNullable(attribute);
    }
    return Optional.empty();
  }

  protected ASTCDMethod createGetNameMethod() {
    ASTMCObjectType optOfString = getCDTypeFacade().createOptionalTypeOf(String.class);
    ASTCDMethod getNameMethod = getCDMethodFacade().createMethod(PUBLIC, optOfString, "getNameOpt");
    this.replaceTemplate(EMPTY_BODY, getNameMethod, new StringHookPoint("return Optional.empty();"));
    return getNameMethod;
  }

  protected ASTCDMethod createCacheMethod(String definitionName) {
    ASTCDParameter parameter = getCDParameterFacade().createParameter(getCDTypeFacade().createStringType(), "calculatedModelName");
    ASTCDMethod cacheMethod = getCDMethodFacade().createMethod(PUBLIC, "cache", parameter);
    this.replaceTemplate(EMPTY_BODY, cacheMethod, new TemplateHookPoint("_symboltable.scope.globalscope.CacheMethod", definitionName));
    return cacheMethod;
  }

  protected ASTCDMethod createContinueWithModelLoaderMethod(String definitionName) {
    ASTCDParameter modelNameParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createStringType(), "calculatedModelName");
    ASTMCQualifiedType modelLoaderType = getCDTypeFacade().createQualifiedType(definitionName + MODEL_LOADER_SUFFIX);
    ASTCDParameter modelLoaderParameter = getCDParameterFacade().createParameter(modelLoaderType, "modelLoader");

    ASTCDMethod continueWithModelLoaderMethod = getCDMethodFacade().createMethod(PUBLIC, getCDTypeFacade().createBooleanType(), "continueWithModelLoader", modelNameParameter, modelLoaderParameter);
    this.replaceTemplate(EMPTY_BODY, continueWithModelLoaderMethod,
        new StringHookPoint("    return !modelName2ModelLoaderCache.containsKey(calculatedModelName)\n" +
            "      || !modelName2ModelLoaderCache.get(calculatedModelName).contains(modelLoader);"));
    return continueWithModelLoaderMethod;
  }

  protected List<ASTCDMethod> createAlreadyResolvedMethods(List<? extends ASTCDType> cdTypeList) {
    List<String> symbolAttributeNameList = cdTypeList
        .stream()
        .map(symbolTableService::getDefiningSymbolSimpleName)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(s -> s + "s")
        .map(StringTransformations::uncapitalize)
        .collect(Collectors.toList());
    List<ASTCDAttribute> symbolAlreadyResolvedAttributes = createSymbolAlreadyResolvedAttributes(symbolAttributeNameList);
    return symbolAlreadyResolvedAttributes
        .stream()
        .map(methodDecorator::decorate)
        .flatMap(List::stream)
        .collect(Collectors.toList());
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

  protected List<ASTCDMethod> createAddResolvingSymbolDelegateMethods(Collection<ASTCDAttribute> delegateAttributes) {
    List<ASTCDMethod> astcdMethodList = new ArrayList<>();
    for (ASTCDAttribute delegateAttribute : delegateAttributes) {
      ASTMCQualifiedType qualifiedType = getCDTypeFacade().createQualifiedType(symbolTableService.getNativeTypeName(delegateAttribute.getMCType()));
      ASTCDParameter parameter = getCDParameterFacade().createParameter(qualifiedType, delegateAttribute.getName());
      String methodName = "add" + StringTransformations.capitalize(delegateAttribute.getName()).substring(0, delegateAttribute.getName().length() - 4);

      ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, methodName, parameter);
      this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("this." + delegateAttribute.getName() + ".add(" + delegateAttribute.getName() + ");"));
      astcdMethodList.add(method);
    }
    return astcdMethodList;
  }
}
