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

  protected static final String LOAD = "load%s";

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
    String scopeDeSerName = symbolTableService.getScopeDeSerSimpleName();

    ASTCDAttribute cacheAttribute = createCacheAttribute();
    this.replaceTemplate(VALUE, cacheAttribute, new StringHookPoint("= new java.util.HashSet<>()"));

    ASTCDAttribute symbolFileExtensionAttribute = getCDAttributeFacade().createAttribute(PROTECTED,
        getMCTypeFacade().createStringType(), "symbolFileExtension");
    List<ASTCDMethod> symbolFileExtensionMethods = accessorDecorator.decorate(symbolFileExtensionAttribute);
    symbolFileExtensionMethods.addAll(mutatorDecorator.decorate(symbolFileExtensionAttribute));

    ASTCDAttribute scopeDeSerAttribute = createScopeDeSerAttribute(scopeDeSerName);
    List<ASTCDMethod> scopeDeSerMethods = accessorDecorator.decorate(scopeDeSerAttribute);
    scopeDeSerMethods.addAll(mutatorDecorator.decorate(scopeDeSerAttribute));

    List<ASTCDType> symbolProds = symbolTableService.getSymbolDefiningProds(input.getCDDefinition());

    ASTCDAttribute modelPathAttribute = createModelPathAttribute();
    List<ASTCDMethod> modelPathMethods = accessorDecorator.decorate(modelPathAttribute);

    ASTCDAttribute fileExtensionAttribute = getCDAttributeFacade().createAttribute(PROTECTED,
        getMCTypeFacade().createStringType(), FILE_EXTENSION_VAR);
    List<ASTCDMethod> fileExtensionMethods = accessorDecorator.decorate(fileExtensionAttribute);
    fileExtensionMethods.addAll(mutatorDecorator.decorate(fileExtensionAttribute));

    Map<String, ASTCDAttribute> resolvingDelegateAttributes = createResolvingDelegateAttributes(symbolProds);
    resolvingDelegateAttributes.putAll(createResolvingDelegateSuperAttributes());

    List<ASTCDType> symbolClasses = symbolTableService.getSymbolDefiningProds(input.getCDDefinition());
    symbolClasses.addAll(symbolTableService.getSymbolDefiningSuperProds());

    List<ASTCDMethod> resolvingDelegateMethods = createResolvingDelegateMethods(resolvingDelegateAttributes.values());

    return CD4AnalysisMill.cDClassBuilder()
        .setName(globalScopeName)
        .setModifier(PUBLIC.build())
        .setSuperclass(scopeType)
        .addInterface(globalScopeInterface)
        .addCDConstructor(createConstructor(globalScopeName))
        .addCDConstructor(createZeroArgsConstructor(globalScopeName))
        .addCDAttribute(modelPathAttribute)
        .addAllCDMethods(modelPathMethods)
        .addCDAttribute(fileExtensionAttribute)
        .addAllCDMethods(fileExtensionMethods)
        .addCDAttribute(symbolFileExtensionAttribute)
        .addAllCDMethods(symbolFileExtensionMethods)
        .addCDAttribute(scopeDeSerAttribute)
        .addAllCDMethods(scopeDeSerMethods)
        .addCDAttribute(cacheAttribute)
        .addCDMethod(createAddLoadedFileMethod())
        .addCDMethod(createClearLoadedFilesMethod())
        .addCDMethod(createIsFileLoadedMethod())
        .addAllCDAttributes(resolvingDelegateAttributes.values())
        .addAllCDMethods(resolvingDelegateMethods)
        .addAllCDMethods(createAlreadyResolvedMethods(symbolProds))
        .addAllCDMethods(createAlreadyResolvedSuperMethods())
        .addAllCDMethods(createLoadMethods(symbolClasses))
        .addCDMethod(createLoadFileForModelNameMethod(definitionName))
        //
        .addCDMethod(createGetRealThisMethod(globalScopeName))
        .build();
  }

  protected ASTCDConstructor createConstructor(String globalScopeClassName) {
    ASTMCType modelPathType = getMCTypeFacade().createQualifiedType(MODEL_PATH_TYPE);
    ASTCDParameter modelPathParameter = getCDParameterFacade().createParameter(modelPathType, MODEL_PATH_VAR);

    ASTCDParameter fileExtensionParameter = getCDParameterFacade().createParameter(getMCTypeFacade().createStringType(), FILE_EXTENSION_VAR);
    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), globalScopeClassName, modelPathParameter, fileExtensionParameter);
    String millFullName = symbolTableService.getMillFullName();
    this.replaceTemplate(EMPTY_BODY, constructor, new TemplateHookPoint(TEMPLATE_PATH + "ConstructorGlobalScope",
        millFullName, symbolTableService.getCDName()));
    return constructor;
  }

  protected ASTCDConstructor createZeroArgsConstructor(String className){
    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), className);
    String millFullName = symbolTableService.getMillFullName();
    String grammarName = symbolTableService.getCDName();
    this.replaceTemplate(EMPTY_BODY, constructor, new TemplateHookPoint(TEMPLATE_PATH + "ZeroArgsConstructorGlobalScope", millFullName, grammarName));
    return constructor;
  }

  protected ASTCDAttribute createModelPathAttribute() {
    return getCDAttributeFacade().createAttribute(PROTECTED, MODEL_PATH_TYPE, MODEL_PATH_VAR);
  }

  protected ASTCDAttribute createScopeDeSerAttribute(String scopeDeSerName){
    return getCDAttributeFacade().createAttribute(PROTECTED, getMCTypeFacade().createQualifiedType(scopeDeSerName), "scopeDeSer");
  }

  protected ASTCDAttribute createCacheAttribute(){
    return getCDAttributeFacade().createAttribute(PROTECTED, getMCTypeFacade().createSetTypeOf(getMCTypeFacade().createStringType()), "cache");
  }

  protected ASTCDAttribute createModelLoaderAttribute(String modelLoaderClassName) {
    ASTMCOptionalType optType = getMCTypeFacade().createOptionalTypeOf(modelLoaderClassName);
    return getCDAttributeFacade().createAttribute(PROTECTED, optType, "modelLoader");
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

  protected List<ASTCDMethod> createLoadMethods(List<? extends ASTCDType> symbolProds) {
    List<ASTCDMethod> loadMethods = new ArrayList<>();
    ASTCDParameter nameParameter = getCDParameterFacade().createParameter(String.class, NAME_VAR);

    for (ASTCDType symbolProd : symbolProds) {
      String className = symbolTableService.removeASTPrefix(symbolProd);
      String methodName = String.format(LOAD, className);
      ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, methodName, nameParameter);
      this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "Load", className));
      loadMethods.add(method);
    }

    return loadMethods;
  }

  protected ASTCDMethod createLoadFileForModelNameMethod(String definitionName){
    ASTCDParameter modelNameParam = getCDParameterFacade().createParameter(String.class, "modelName");
    ASTCDParameter symbolNameParam = getCDParameterFacade().createParameter(String.class, "symbolName");
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, "loadFileForModelName", modelNameParam, symbolNameParam);
    String errorCode = symbolTableService.getGeneratedErrorCode(definitionName);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "LoadFileForModelName", definitionName, errorCode));
    return method;
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

  protected ASTCDMethod createAddLoadedFileMethod() {
    ASTCDParameter parameter = getCDParameterFacade().createParameter(getMCTypeFacade().createStringType(), NAME_VAR);
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, "addLoadedFile", parameter);
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("cache.add(name);"));
    return method;
  }

  protected ASTCDMethod createClearLoadedFilesMethod(){
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, "clearLoadedFiles");
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("cache.clear();"));
    return method;
  }

  protected ASTCDMethod createIsFileLoadedMethod(){
    ASTCDParameter parameter = getCDParameterFacade().createParameter(getMCTypeFacade().createStringType(), NAME_VAR);
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, getMCTypeFacade().createBooleanType(), "isFileLoaded", parameter);
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return cache.contains(name);"));
    return method;
  }

  public boolean isGlobalScopeTop() {
    return isGlobalScopeTop;
  }

  public void setGlobalScopeTop(boolean globalScopeTop) {
    isGlobalScopeTop = globalScopeTop;
  }

}
