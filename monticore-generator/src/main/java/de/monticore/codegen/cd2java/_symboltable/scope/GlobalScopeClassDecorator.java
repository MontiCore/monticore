/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.scope;

import com.google.common.collect.Maps;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4codebasis._ast.*;
import de.monticore.cdbasis._ast.*;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis._symboltable.ICDBasisScope;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._symboltable.serialization.AbstractDeSers;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.StringTransformations;
import de.se_rwth.commons.logging.Log;

import java.util.*;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.CDModifier.PUBLIC;
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

  protected static final String ADAPTED_RESOLVING_DELEGATE = "adapted%sResolver";

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
    String scopeInterfaceFullName = symbolTableService.getScopeInterfaceFullName();
    String definitionName = input.getCDDefinition().getName();
    String scopeDeSerFullName = symbolTableService.getScopeDeSerFullName();
    String symbols2JsonName = symbolTableService.getSymbols2JsonSimpleName();

    ASTCDAttribute cacheAttribute = createCacheAttribute();
    this.replaceTemplate(VALUE, cacheAttribute, new StringHookPoint("= new java.util.HashSet<>()"));

    ASTCDAttribute deSerMapAttribute = createDeSerMapAttribute();

    ASTCDAttribute symbols2JsonAttribute = createSymbols2JsonAttribute(symbols2JsonName);
    List<ASTCDMethod> symbols2JsonMethods = mutatorDecorator.decorate(symbols2JsonAttribute);
    symbols2JsonMethods.add(createGetSymbols2JsonMethod(symbols2JsonAttribute));

    ASTCDAttribute deserAttribute = createScopeDeSerAttribute(I_DE_SER);
    List<ASTCDMethod> deserMethods = accessorDecorator.decorate(deserAttribute);
    deserMethods.addAll(mutatorDecorator.decorate(deserAttribute));

    List<ASTCDType> symbolProds = symbolTableService.getSymbolDefiningProds(input.getCDDefinition());

    ASTCDAttribute modelPathAttribute = createModelPathAttribute();
    List<ASTCDMethod> modelPathMethods = accessorDecorator.decorate(modelPathAttribute);
    modelPathMethods.addAll(mutatorDecorator.decorate(modelPathAttribute));

    ASTCDAttribute fileExtensionAttribute = getCDAttributeFacade().createAttribute(PROTECTED.build(),
        getMCTypeFacade().createStringType(), FILE_EXTENSION_VAR);
    List<ASTCDMethod> fileExtensionMethods = accessorDecorator.decorate(fileExtensionAttribute);
    fileExtensionMethods.addAll(mutatorDecorator.decorate(fileExtensionAttribute));

    Map<String, ASTCDAttribute> resolverAttributes = createResolverAttributes(symbolProds);
    resolverAttributes.putAll(createResolverSuperAttributes());

    List<ASTCDType> symbolClasses = symbolTableService.getSymbolDefiningProds(input.getCDDefinition());
    symbolClasses.addAll(symbolTableService.getSymbolDefiningSuperProds());

    List<ASTCDMethod> resolverMethods = createResolverMethods(resolverAttributes.values());

    List<String> symbolListString = symbolClasses.stream().map(symbolTableService::getSymbolSimpleName).collect(Collectors.toList());

    return CD4AnalysisMill.cDClassBuilder()
        .setName(globalScopeName)
        .setModifier(PUBLIC.build())
        .setSuperclass(scopeType)
        .addInterface(globalScopeInterface)
        .addCDMember(createConstructor(globalScopeName))
        .addCDMember(createZeroArgsConstructor(globalScopeName))
        .addCDMember(modelPathAttribute)
        .addAllCDMembers(modelPathMethods)
        .addCDMember(fileExtensionAttribute)
        .addAllCDMembers(fileExtensionMethods)
        .addCDMember(deserAttribute)
        .addAllCDMembers(deserMethods)
        .addCDMember(deSerMapAttribute)
        .addAllCDMembers(createDeSerMapMethods(deSerMapAttribute))
        .addCDMember(symbols2JsonAttribute)
        .addAllCDMembers(symbols2JsonMethods)
        .addCDMember(cacheAttribute)
        .addCDMember(createAddLoadedFileMethod())
        .addCDMember(createClearLoadedFilesMethod())
        .addCDMember(createIsFileLoadedMethod())
        .addCDMember(createInitMethod(scopeInterfaceFullName, scopeDeSerFullName, symbolProds))
        .addAllCDMembers(resolverAttributes.values())
        .addAllCDMembers(resolverMethods)
        .addAllCDMembers(createAlreadyResolvedMethods(symbolProds))
        .addAllCDMembers(createAlreadyResolvedSuperMethods())
        .addAllCDMembers(createLoadMethods(symbolClasses))
        .addCDMember(createLoadFileForModelNameMethod(definitionName))
        //
        .addCDMember(createGetRealThisMethod(globalScopeName))
        .addCDMember(createClearMethod(resolverMethods, symbolListString))
        .build();
  }

  protected ASTCDConstructor createConstructor(String globalScopeClassName) {
    ASTMCType modelPathType = getMCTypeFacade().createQualifiedType(MODEL_PATH_TYPE);
    ASTCDParameter modelPathParameter = getCDParameterFacade().createParameter(modelPathType, MODEL_PATH_VAR);

    ASTCDParameter fileExtensionParameter = getCDParameterFacade().createParameter(getMCTypeFacade().createStringType(), FILE_EXTENSION_VAR);
    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), globalScopeClassName, modelPathParameter, fileExtensionParameter);
    this.replaceTemplate(EMPTY_BODY, constructor, new TemplateHookPoint(TEMPLATE_PATH + "ConstructorGlobalScope"));
    return constructor;
  }

  protected ASTCDConstructor createZeroArgsConstructor(String className){
    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), className);
    this.replaceTemplate(EMPTY_BODY, constructor, new TemplateHookPoint(TEMPLATE_PATH + "ZeroArgsConstructorGlobalScope"));
    return constructor;
  }

  protected ASTCDAttribute createModelPathAttribute() {
    return getCDAttributeFacade().createAttribute(PROTECTED.build(), MODEL_PATH_TYPE, MODEL_PATH_VAR);
  }

  protected ASTCDAttribute createDeSerMapAttribute(){
    ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PROTECTED.build(),
            getMCTypeFacade().createQualifiedType("Map<String," + I_SYMBOL_DE_SER + ">"),
        SYM_DESERS_VAR);
    this.replaceTemplate(VALUE, attribute, new StringHookPoint(" = com.google.common.collect.Maps.newHashMap()"));
    return attribute;
  }

  protected Collection<? extends ASTCDMethod> createDeSerMapMethods(ASTCDAttribute deSerMapAttribute) {
    List<ASTCDMethod> deSerMapMethods = accessorDecorator.decorate(deSerMapAttribute);
    deSerMapMethods.addAll(mutatorDecorator.decorate(deSerMapAttribute));

    // Create simple putDeSer(String key, IDeSer value)
    ASTCDParameter key = getCDParameterFacade().createParameter(String.class, "key");
    ASTCDParameter value = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(I_SYMBOL_DE_SER), "value");
    ASTCDMethod putMethod = getCDMethodFacade().createMethod(PUBLIC.build(), "putSymbolDeSer", key, value);
    replaceTemplate(EMPTY_BODY, putMethod, new StringHookPoint(SYM_DESERS_VAR + ".put(key, value);"));
    deSerMapMethods.add(putMethod);

    // Create simple value getDeSer(String key)
    key = getCDParameterFacade().createParameter(String.class, "key");
    ASTMCQualifiedType returnType = getMCTypeFacade().createQualifiedType(I_SYMBOL_DE_SER);
    ASTCDMethod getMethod = getCDMethodFacade().createMethod(PUBLIC.build(), returnType, "getSymbolDeSer", key);
    replaceTemplate(EMPTY_BODY, getMethod, new StringHookPoint("return " + SYM_DESERS_VAR + ".get(key);"));
    deSerMapMethods.add(getMethod);

    return deSerMapMethods;
  }

  protected ASTCDAttribute createScopeDeSerAttribute(String scopeDeSerName){
    return getCDAttributeFacade().createAttribute(PUBLIC.build(), getMCTypeFacade().createQualifiedType(scopeDeSerName), "deSer");
  }

  protected ASTCDAttribute createSymbols2JsonAttribute(String s2jName){
    return getCDAttributeFacade().createAttribute(PROTECTED.build(), getMCTypeFacade().createQualifiedType(s2jName), "symbols2Json");
  }

  protected ASTCDAttribute createCacheAttribute(){
    return getCDAttributeFacade().createAttribute(PROTECTED.build(), getMCTypeFacade().createSetTypeOf(getMCTypeFacade().createStringType()), "cache");
  }

  protected Map<String, ASTCDAttribute> createResolverAttributes(List<? extends ASTCDType> symbolProds) {
    Map<String, ASTCDAttribute> attributeList = new HashMap<>();
    for (ASTCDType symbolProd : symbolProds) {
      Optional<ASTCDAttribute> symbolAttribute = createResolverAttribute(symbolProd, symbolTableService.getCDSymbol());
      symbolAttribute.ifPresent(attr -> attributeList.put(attr.getName(), attr));
    }
    return attributeList;
  }

  protected Map<String, ASTCDAttribute> createResolverSuperAttributes() {
    Map<String, ASTCDAttribute> symbolAttributes = new HashMap<>();
    for (DiagramSymbol cdDefinitionSymbol : symbolTableService.getSuperCDsTransitive()) {
      for (CDTypeSymbol type : ((ICDBasisScope) cdDefinitionSymbol.getEnclosingScope()).getLocalCDTypeSymbols()) {
        if (type.isPresentAstNode() && symbolTableService.hasSymbolStereotype(type.getAstNode())) {
          Optional<ASTCDAttribute> symbolAttribute = createResolverAttribute(type.getAstNode(), cdDefinitionSymbol);
          symbolAttribute.ifPresent(attr -> symbolAttributes.put(attr.getName(), attr));
        }
      }
    }
    return symbolAttributes;
  }

  protected Optional<ASTCDAttribute> createResolverAttribute(ASTCDType prod, DiagramSymbol cdSymbol) {
    Optional<String> simpleName = symbolTableService.getDefiningSymbolSimpleName(prod);
    if (simpleName.isPresent()) {
      String attrName = String.format(ADAPTED_RESOLVING_DELEGATE, simpleName.get());
      String symbolResolverInterfaceTypeName = symbolTableService.getSymbolResolverInterfaceFullName(prod, cdSymbol);
      ASTMCType listType = getMCTypeFacade().createListTypeOf(symbolResolverInterfaceTypeName);

      ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PROTECTED.build(), listType, attrName);
      this.replaceTemplate(VALUE, attribute, new StringHookPoint(" = new ArrayList<" + symbolResolverInterfaceTypeName + ">()"));
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
      ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), methodName, nameParameter);
      this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "Load", className));
      loadMethods.add(method);
    }

    return loadMethods;
  }

  protected ASTCDMethod createLoadFileForModelNameMethod(String definitionName){
    ASTCDParameter modelNameParam = getCDParameterFacade().createParameter(String.class, "modelName");
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), "loadFileForModelName", modelNameParam);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "LoadFileForModelName", definitionName));
    return method;
  }

    protected List<ASTCDMethod> createResolverMethods(Collection<ASTCDAttribute> resolverAttributes) {
    List<ASTCDMethod> resolverMethods = new ArrayList<>();
    for (ASTCDAttribute attribute : resolverAttributes) {
      String capAttributeName = StringTransformations.capitalize(attribute.getName());
      // add simple getter e.g. getXList()
      ASTCDMethod getter = getCDMethodFacade().createMethod(PUBLIC.build(), attribute.getMCType(),
          "get" + capAttributeName + "List");
      replaceTemplate(EMPTY_BODY, getter, new StringHookPoint(
          "return this." + attribute.getName() + ";"));
      resolverMethods.add(getter);
      // add simple setter e.g. setXList()
      ASTCDParameter parameter = getCDParameterFacade().createParameter(attribute.getMCType(), attribute.getName());
      ASTCDMethod setter = getCDMethodFacade().createMethod(PUBLIC.build(),
          "set" + capAttributeName + "List", parameter);
      replaceTemplate(EMPTY_BODY, setter, new StringHookPoint(
          "this." + attribute.getName() +" = " + attribute.getName() + ";"));
      resolverMethods.add(setter);
    }
    return resolverMethods;
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
    for (DiagramSymbol cdDefinitionSymbol : symbolTableService.getSuperCDsTransitive()) {
      // only types that define a symbol
      List<ASTCDType> symbolProds = ((ICDBasisScope) cdDefinitionSymbol.getEnclosingScope()).getLocalCDTypeSymbols().stream().filter(t -> t.isPresentAstNode())
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
      ASTCDAttribute symbolAttribute = getCDAttributeFacade().createAttribute(PROTECTED.build(), booleanType, attrName);
      this.replaceTemplate(VALUE, symbolAttribute, new StringHookPoint("= false;"));
      symbolAttributeList.add(symbolAttribute);
    }
    return symbolAttributeList;
  }

  ///////////////////////////////////////////////////////////////////////////////////////////////

  protected ASTCDMethod createGetRealThisMethod(String globalScopeName) {
    ASTMCType globalScopeInterfaceType = getMCTypeFacade().createQualifiedType(globalScopeName);
    ASTCDMethod getRealThis = getCDMethodFacade().createMethod(PUBLIC.build(), globalScopeInterfaceType, "getRealThis");
    if (isGlobalScopeTop()) {
      getRealThis.getModifier().setAbstract(true);
    } else {
      this.replaceTemplate(EMPTY_BODY, getRealThis, new StringHookPoint("return this;"));
    }
    return getRealThis;
  }

  protected ASTCDMethod createAddLoadedFileMethod() {
    ASTCDParameter parameter = getCDParameterFacade().createParameter(getMCTypeFacade().createStringType(), NAME_VAR);
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), "addLoadedFile", parameter);
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("cache.add(name);"));
    return method;
  }

  protected ASTCDMethod createClearLoadedFilesMethod(){
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), "clearLoadedFiles");
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("cache.clear();"));
    return method;
  }

  protected ASTCDMethod createIsFileLoadedMethod(){
    ASTCDParameter parameter = getCDParameterFacade().createParameter(getMCTypeFacade().createStringType(), NAME_VAR);
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), getMCTypeFacade().createBooleanType(), "isFileLoaded", parameter);
    this.replaceTemplate(EMPTY_BODY, method, new StringHookPoint("return cache.contains(name);"));
    return method;
  }

  protected ASTCDMethod createGetSymbols2JsonMethod(ASTCDAttribute s2j) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), s2j.getMCType(), "getSymbols2Json");
    String s2jClassName = symbolTableService.getSymbols2JsonFullName();
    HookPoint hookPoint = new TemplateHookPoint(TEMPLATE_PATH + "GetSymbols2Json", s2j.getName(), s2jClassName);
    this.replaceTemplate(EMPTY_BODY, method, hookPoint);
    return method;
  }

  protected ASTCDMethod createClearMethod(List<ASTCDMethod> getResolverMethodList, List<String> symbolList){
    List<String> resolverListString = getResolverMethodList.stream().map(ASTCDMethod::getName).filter(name -> name.startsWith("get")).collect(Collectors.toList());
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), "clear");
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "Clear", resolverListString, symbolList));
    return method;
  }

  protected ASTCDMethod createInitMethod(String scopeFullName, String scopeDeSerFullName, List<ASTCDType> symbolDefiningProds){
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), "init");
    Map<String, String> map = Maps.newHashMap();

    // add DeSers for locally defined symbols
    for(ASTCDType s :symbolDefiningProds) {
      String symbol = symbolTableService.getSymbolFullName(s);
      String deser = symbolTableService.getSymbolDeSerFullName(s);
      map.put(symbol, deser);
    }

    // add DeSers for symbols defined in inherited languages
    for (DiagramSymbol cdSymbol: symbolTableService.getSuperCDsTransitive()) {
      symbolTableService.getSymbolDefiningProds((ASTCDDefinition) cdSymbol.getAstNode()).forEach(s -> {
        String symbol = symbolTableService.getSymbolFullName(s, cdSymbol);
        String deser = symbolTableService.getSymbolDeSerFullName(s, cdSymbol);
        map.put(symbol, deser);
      });
    }

    // filter DeSers that are generated as abstract classes (or have the TOP suffix)
    List<String> removeDeSers = new ArrayList<>();
    for(Map.Entry<String, String> e : map.entrySet()){
      if(AbstractDeSers.contains(e.getValue())){
        Log.warn("The DeSer '"  + e.getValue()
            + "' is not added to the map in " + symbolTableService.getGlobalScopeFullName()
            + ", because it is abstract. Please extend the class with the TOP mechanism!");
        removeDeSers.add(e.getValue());
      }
    }
    map.entrySet().removeIf(entry -> removeDeSers.contains(entry.getValue()));

    AbstractDeSers.reset(); // reset for next generator invocation

    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(
        TEMPLATE_PATH + "Init", scopeDeSerFullName, map));
    return method;
  }
  public boolean isGlobalScopeTop() {
    return isGlobalScopeTop;
  }

  public void setGlobalScopeTop(boolean globalScopeTop) {
    isGlobalScopeTop = globalScopeTop;
  }

}
