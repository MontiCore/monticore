/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.scope;

import de.monticore.cd.cd4analysis.CD4AnalysisMill;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCMapType;
import de.monticore.types.mccollectiontypes._ast.ASTMCOptionalType;
import de.se_rwth.commons.StringTransformations;

import java.util.*;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.*;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;

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

  /**
   * flag added to define if the GlobalScope interface was overwritten with the TOP mechanism
   * if top mechanism was used, must use setter to set flag true, before the decoration
   * is needed for different getRealThis method implementations
   */
  protected boolean isGlobalScopeTop = false;

  protected static final String LOAD_MODELS_FOR = "loadModelsFor%s";

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
    ASTMCQualifiedType globalScopeInterface = symbolTableService.getGlobalScopeInterfaceType();
    String definitionName = input.getCDDefinition().getName();
    String modelLoaderClassName = symbolTableService.getModelLoaderClassSimpleName();

    List<ASTCDType> symbolProds = symbolTableService.getSymbolDefiningProds(input.getCDDefinition());

    ASTCDAttribute modelPathAttribute = createModelPathAttribute();
    List<ASTCDMethod> modelPathMethods = accessorDecorator.decorate(modelPathAttribute);

    ASTCDAttribute modelLoaderAttribute = createModelLoaderAttribute(modelLoaderClassName);
    List<ASTCDMethod> modelLoaderMethods = accessorDecorator.decorate(modelLoaderAttribute);
    modelLoaderMethods.addAll(mutatorDecorator.decorate(modelLoaderAttribute));

    ASTCDAttribute fileExtensionAttribute = getCDAttributeFacade().createAttribute(PROTECTED,
        getMCTypeFacade().createStringType(), FILE_EXTENSION_VAR);
    List<ASTCDMethod> fileExtensionMethods = accessorDecorator.decorate(fileExtensionAttribute);
    fileExtensionMethods.addAll(mutatorDecorator.decorate(fileExtensionAttribute));

    Map<String, ASTCDAttribute> resolvingDelegateAttributes = createResolvingDelegateAttributes(symbolProds);
    resolvingDelegateAttributes.putAll(createResolvingDelegateSuperAttributes());

    List<ASTCDType> symbolClasses = symbolTableService.getSymbolDefiningProds(input.getCDDefinition());

    List<ASTCDMethod> resolvingDelegateMethods = createResolvingDelegateMethods(resolvingDelegateAttributes.values());

    return CD4AnalysisMill.cDClassBuilder()
        .setName(globalScopeName)
        .setModifier(PUBLIC.build())
        .setSuperclass(scopeType)
        .addInterface(globalScopeInterface)
        .addCDConstructors(createConstructor(globalScopeName))
        .addCDAttributes(modelPathAttribute)
        .addAllCDMethods(modelPathMethods)
        .addCDAttributes(modelLoaderAttribute)
        .addAllCDMethods(modelLoaderMethods)
        .addCDAttributes(fileExtensionAttribute)
        .addAllCDMethods(fileExtensionMethods)
        .addCDAttributes(createModelName2ModelLoaderCacheAttribute(modelLoaderClassName))
        .addAllCDAttributes(resolvingDelegateAttributes.values())
        .addCDMethods(createEnableModelLoader(globalScopeName))
        .addCDMethods(createDisableModelLoader())
        .addCDMethods(createCacheMethod(definitionName))
        .addCDMethods(createContinueWithModelLoaderMethod(modelLoaderClassName))
        .addAllCDMethods(resolvingDelegateMethods)
        .addAllCDMethods(createAlreadyResolvedMethods(symbolProds))
        .addAllCDMethods(createAlreadyResolvedSuperMethods())
        //
        .addCDMethods(createGetRealThisMethod(globalScopeName))
        .addAllCDMethods(createLoadModelsForMethod(symbolClasses, definitionName))
        .addAllCDMethods(createLoadModelsForSuperMethod(definitionName))
        .build();
  }

  protected ASTCDConstructor createConstructor(String globalScopeClassName) {
    StringBuilder sb = new StringBuilder();

    ASTMCType modelPathType = getMCTypeFacade().createQualifiedType(MODEL_PATH_TYPE);
    ASTCDParameter modelPathParameter = getCDParameterFacade().createParameter(modelPathType, MODEL_PATH_VAR);
    sb.append("this." + MODEL_PATH_VAR + " = Log.errorIfNull(" + MODEL_PATH_VAR + ");\n");

    ASTCDParameter fileExtensionParameter = getCDParameterFacade().createParameter(getMCTypeFacade().createStringType(), FILE_EXTENSION_VAR);
    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), globalScopeClassName, modelPathParameter, fileExtensionParameter);
    sb.append("this." + FILE_EXTENSION_VAR + " = Log.errorIfNull(" + FILE_EXTENSION_VAR + ");\n");

    if (!symbolTableService.hasComponentStereotype(symbolTableService.getCDSymbol().getAstNode())) {
      sb.append("this.enableModelLoader();");
    } else {
      sb.append("this." + MODEL_LOADER_VAR + " = Optional.empty();");
    }
    this.replaceTemplate(EMPTY_BODY, constructor, new StringHookPoint(sb.toString()));
    return constructor;
  }

  protected ASTCDMethod createEnableModelLoader(String globalScopeName) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), "enableModelLoader");
    if (symbolTableService.hasComponentStereotype(symbolTableService.getCDSymbol().getAstNode())) {
      this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint(
          "Log.error(\"0xA6102" + symbolTableService.getGeneratedErrorCode(globalScopeName)
              + " Global scopes of component grammars do not have model loaders.\");"));
    } else {
      this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH
          + "EnableModelLoader", symbolTableService.getCDName(), symbolTableService.getQualifiedCDName().toLowerCase()));
    }
    return method;
  }

  protected ASTCDMethod createDisableModelLoader() {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), "disableModelLoader");
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint(
        "this."+MODEL_LOADER_VAR+" = Optional.empty();"));
    return method;
  }

  protected ASTCDAttribute createModelPathAttribute() {
    return getCDAttributeFacade().createAttribute(PROTECTED, MODEL_PATH_TYPE, MODEL_PATH_VAR);
  }

  protected ASTCDAttribute createModelLoaderAttribute(String modelLoaderClassName) {
    ASTMCOptionalType optType = getMCTypeFacade().createOptionalTypeOf(modelLoaderClassName);
    return getCDAttributeFacade().createAttribute(PROTECTED, optType, "modelLoader");
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
        if (type.isPresentAstNode() && symbolTableService.hasSymbolStereotype(type.getAstNode())) {
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

  protected List<ASTCDMethod> createResolvingDelegateMethods(Collection<ASTCDAttribute> resolvingDelegateAttributes) {
    List<ASTCDMethod> resolvingDelegateMethods = new ArrayList<>();
    for (ASTCDAttribute attribute : resolvingDelegateAttributes) {
      String capAttributeName = StringTransformations.capitalize(attribute.getName());
      // add simple getter e.g. getXList()
      ASTCDMethod getter = getCDMethodFacade().createMethod(PUBLIC, attribute.getMCType(),
          "get" + capAttributeName + "List");
      replaceTemplate(EMPTY_BODY, getter, new StringHookPoint(
          "return this." + attribute.getName() + ";"));
      resolvingDelegateMethods.add(getter);
      // add simple setter e.g. setXList()
      ASTCDParameter parameter = getCDParameterFacade().createParameter(attribute.getMCType(), attribute.getName());
      ASTCDMethod setter = getCDMethodFacade().createMethod(PUBLIC,
          "set" + capAttributeName + "List", parameter);
      replaceTemplate(EMPTY_BODY, setter, new StringHookPoint(
          "this." + attribute.getName() +" = " + attribute.getName() + ";"));
      resolvingDelegateMethods.add(setter);
    }
    return resolvingDelegateMethods;
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
      List<ASTCDType> symbolProds = cdDefinitionSymbol.getTypes().stream().filter(t -> t.isPresentAstNode())
          .filter(t -> t.getAstNode().isPresentModifier())
          .filter(t -> symbolTableService.hasSymbolStereotype(t.getAstNode().getModifier()))
          .filter(CDTypeSymbol::isPresentAstNode)
          .map(CDTypeSymbol::getAstNode)
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

  ///////////////////////////////////////////////////////////////////////////////////////////////

  protected ASTCDMethod createGetRealThisMethod(String globalScopeName) {
    ASTMCType globalScopeInterfaceType = getMCTypeFacade().createQualifiedType(globalScopeName);
    ASTCDMethod getRealThis = getCDMethodFacade().createMethod(PUBLIC, globalScopeInterfaceType, "getRealThis");
    if (isGlobalScopeTop()) {
      getRealThis.getModifier().setAbstract(true);
    } else {
      this.replaceTemplate(EMPTY_BODY, getRealThis, new StringHookPoint("return this;"));
    }
    return getRealThis;
  }

  /**
   * creates all resolve methods
   * reuses the often used parameters, so that they only need to be created once
   */
  protected List<ASTCDMethod> createLoadModelsForMethod(List<? extends ASTCDType> symbolProds, String definitionName) {
    List<ASTCDMethod> loadMethods = new ArrayList<>();
    ASTCDParameter nameParameter = getCDParameterFacade().createParameter(String.class, NAME_VAR);

    for (ASTCDType symbolProd : symbolProds) {
      String className = symbolTableService.removeASTPrefix(symbolProd);
      String methodName = String.format(LOAD_MODELS_FOR, className);
      ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, methodName, nameParameter);
      this.replaceTemplate(EMPTY_BODY, method,
          new TemplateHookPoint(TEMPLATE_PATH + "LoadModelsFor", className, definitionName));
      loadMethods.add(method);
    }

    return loadMethods;
  }

  protected List<ASTCDMethod> createLoadModelsForSuperMethod(String definitionName) {
    List<ASTCDMethod> loadMethods = new ArrayList<>();
    ASTCDParameter nameParameter = getCDParameterFacade().createParameter(String.class, NAME_VAR);

    for (CDDefinitionSymbol cdDefinitionSymbol : symbolTableService.getSuperCDsTransitive()) {
      for (CDTypeSymbol type : cdDefinitionSymbol.getTypes()) {
        if (type.isPresentAstNode() && type.getAstNode().isPresentModifier()
            && symbolTableService.hasSymbolStereotype(type.getAstNode().getModifier())) {
          String className = symbolTableService.removeASTPrefix(type.getAstNode());
          String methodName = String.format(LOAD_MODELS_FOR, className);
          ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, methodName, nameParameter);
          this.replaceTemplate(EMPTY_BODY, method,
              new TemplateHookPoint(TEMPLATE_PATH + "LoadModelsFor", className, definitionName));
          loadMethods.add(method);
        }
      }
    }
    return loadMethods;
  }

  public boolean isGlobalScopeTop() {
    return isGlobalScopeTop;
  }

  public void setGlobalScopeTop(boolean globalScopeTop) {
    isGlobalScopeTop = globalScopeTop;
  }

}
