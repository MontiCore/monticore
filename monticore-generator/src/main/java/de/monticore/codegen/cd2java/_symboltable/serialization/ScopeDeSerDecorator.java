/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.serialization;

import com.google.common.collect.Lists;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.cd4analysis._symboltable.CDTypeSymbol;
import de.monticore.cd.cd4code.CD4CodeMill;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.typesymbols._symboltable.BuiltInJavaTypeSymbolResolvingDelegate;
import de.monticore.utils.Names;
import de.se_rwth.commons.StringTransformations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.*;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.BUILDER_SUFFIX;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;

/**
 * creates a ScopeDeSer class from a grammar
 */
public class ScopeDeSerDecorator extends AbstractDecorator {

  protected final SymbolTableService symbolTableService;

  protected static final String TEMPLATE_PATH = "_symboltable.serialization.scopeDeSer.";

  protected MethodDecorator methodDecorator;

  protected VisitorService visitorService;

  public ScopeDeSerDecorator(final GlobalExtensionManagement glex,
      final SymbolTableService symbolTableService, final MethodDecorator methodDecorator,
      VisitorService visitorService) {
    super(glex);
    this.visitorService = visitorService;
    this.symbolTableService = symbolTableService;
    this.methodDecorator = methodDecorator;
  }

  /**
   * @param scopeInput  for scopeRule attributes and methods
   * @param symbolInput for Symbol Classes and Interfaces
   */
  public ASTCDClass decorate(ASTCDCompilationUnit scopeInput, ASTCDCompilationUnit symbolInput) {
    String scopeDeSerName = symbolTableService.getScopeDeSerSimpleName();
    String scopeInterfaceName = symbolTableService.getScopeInterfaceFullName();
    String artifactScopeFullName = symbolTableService.getArtifactScopeFullName();
    String globalScopeFullName = symbolTableService.getGlobalScopeFullName();
    String scopeClassFullName = symbolTableService.getScopeClassFullName();
    String simpleName = symbolTableService.getCDName();
    String symTabMillFullName = symbolTableService.getMillFullName();
    ASTCDParameter scopeJsonParam = getCDParameterFacade()
        .createParameter(getMCTypeFacade().createQualifiedType(JSON_OBJECT), SCOPE_JSON_VAR);

    Map<ASTCDType, ASTCDDefinition> symbolDefiningProds = createSymbolMap(symbolInput.getCDDefinition());

    ASTCDDefinition symbolDefinition = symbolInput.getCDDefinition().deepClone();

    // list of all scope rule attributes
    List<ASTCDAttribute> scopeRuleAttributeList = scopeInput.deepClone().getCDDefinition()
        .getCDClassList()
        .stream()
        .map(ASTCDClass::getCDAttributeList)
        .flatMap(List::stream)
        .collect(Collectors.toList());
    scopeRuleAttributeList
        .forEach(a -> getDecorationHelper().addAttributeDefaultValues(a, this.glex));

    ASTCDAttribute fileExtension = getCDAttributeFacade().createAttribute(PROTECTED,
        getMCTypeFacade().createStringType(), "symbolFileExtension");

    ASTCDAttribute jsonPrinter = getCDAttributeFacade()
        .createAttribute(PROTECTED, symbolTableService.getJsonPrinterType(), "printer");

    ASTCDAttribute symbolTablePrinter = getCDAttributeFacade().createAttribute(PROTECTED,
        getMCTypeFacade().createQualifiedType(visitorService.getDelegatorVisitorFullName()),
        "symbolTablePrinter");

    return CD4CodeMill.cDClassBuilder()
        .setName(scopeDeSerName)
        .setModifier(PUBLIC.build())
        .addCDConstructor(createConstructor(scopeDeSerName))
        .addAllCDAttributes(createSymbolDeSerAttributes(symbolDefinition))
        .addCDAttribute(fileExtension)
        .addCDAttribute(jsonPrinter)
        .addCDAttribute(symbolTablePrinter)
        .addAllCDMethods(methodDecorator.decorate(fileExtension))
        .addAllCDMethods(createLoadMethods(artifactScopeFullName))
        .addCDMethod(createLoadSymbolsIntoScopeMethod(globalScopeFullName))
        .addCDMethod(createStoreMethod(scopeDeSerName, artifactScopeFullName))
        .addCDMethod(createDeserializeStringMethod(artifactScopeFullName))
        .addCDMethod(createDeserializeScopeMethod(scopeClassFullName, simpleName, symTabMillFullName, scopeJsonParam, scopeRuleAttributeList))
        .addCDMethod(createDeserializeArtifactScopeMethod(artifactScopeFullName, simpleName, symTabMillFullName, scopeJsonParam, scopeRuleAttributeList))
        .addAllCDMethods(createDeserializeAdditionalAttributesMethods(scopeInterfaceName, scopeJsonParam))
        .addCDMethod(createAddSymbolsMethod())
        .addCDMethod(createAddSymbolMethod(symbolDefiningProds))
        .addAllCDMethods(createDeserializeSymbolMethods(symbolDefiningProds))
        .addCDMethod(createSerializeMethod(scopeInterfaceName))
        .addAllCDMethods(
            createDeserializeScopeRuleAttributesMethod(scopeRuleAttributeList, scopeDeSerName))
        .build();
  }

  protected Map<ASTCDType, ASTCDDefinition> createSymbolMap( ASTCDDefinition symbolInput) {
    Map<ASTCDType, ASTCDDefinition> result = new HashMap<>();
    //add local symbols
    symbolTableService.getSymbolDefiningProds(symbolInput).forEach(s -> result.put(s,symbolInput));

    //add symbols from super grammars
    for (CDDefinitionSymbol cdDefinitionSymbol : symbolTableService.getSuperCDsTransitive()) {
      for (CDTypeSymbol type : cdDefinitionSymbol.getTypes()) {
        if (type.isPresentAstNode() && type.getAstNode().isPresentModifier()
            && symbolTableService.hasSymbolStereotype(type.getAstNode().getModifier())) {
          result.put(type.getAstNode(), cdDefinitionSymbol.getAstNode());
        }
      }
    }
    return result;
  }

  protected ASTCDConstructor createConstructor(String scopeDeSerName) {
    ASTCDConstructor constructor = getCDConstructorFacade()
        .createConstructor(PUBLIC, scopeDeSerName);
    Map<String, String> symbolTablePrinters = new HashMap<>();
    CDDefinitionSymbol lang = symbolTableService.getCDSymbol();
    symbolTablePrinters.put(lang.getName(), symbolTableService.getSymbolTablePrinterFullName(lang));
    for (CDDefinitionSymbol superCD : symbolTableService.getSuperCDsTransitive()) {
      symbolTablePrinters
          .put(superCD.getName(), symbolTableService.getSymbolTablePrinterFullName(superCD));
    }

    this.replaceTemplate(EMPTY_BODY, constructor,
        new TemplateHookPoint("_symboltable.serialization.scopeDeSer.Constructor4ScopeDeSer",
            visitorService.getDelegatorVisitorFullName(), symbolTablePrinters));
    return constructor;
  }

  protected List<ASTCDAttribute> createSymbolDeSerAttributes(ASTCDDefinition astcdDefinition) {
    // collects symbolDeSer names from local symbol definitions
    List<String> symbolDeSerNames = symbolTableService
        .getSymbolDefiningProds(astcdDefinition).stream()
        .map(symbolTableService::getSymbolDeSerSimpleName)
        .collect(Collectors.toList());

    // collects symbolDeSer names form parent grammar symbol definitions
    for (CDDefinitionSymbol cdDefinitionSymbol : symbolTableService.getSuperCDsTransitive()) {
      for (CDTypeSymbol type : cdDefinitionSymbol.getTypes()) {
        if (type.isPresentAstNode() && type.getAstNode().isPresentModifier()
            && symbolTableService.hasSymbolStereotype(type.getAstNode().getModifier())) {
          String pckge = symbolTableService.getSerializationPackage(cdDefinitionSymbol);
          String deser = symbolTableService.getSymbolDeSerSimpleName(type.getAstNode());
          symbolDeSerNames.add(Names.getQualifiedName(pckge,deser));
        }
      }
    }

    // build attributes for all collected symbolDeSer names
    List<ASTCDAttribute> symbolDeSerAttributes = new ArrayList<>();
    for (String symbolDeSerName : symbolDeSerNames) {
      ASTCDAttribute attribute = getCDAttributeFacade()
          .createAttribute(PACKAGE_PRIVATE, symbolDeSerName,
              StringTransformations.uncapitalize(Names.getSimpleName(symbolDeSerName)));
      this.replaceTemplate(VALUE, attribute,
          new StringHookPoint("= new " + symbolDeSerName + "()"));
      symbolDeSerAttributes.add(attribute);
    }
    return symbolDeSerAttributes;
  }

  protected ASTCDMethod createLoadSymbolsIntoScopeMethod(String globalScopeName) {
    ASTCDParameter qualNameParam = getCDParameterFacade()
        .createParameter(getMCTypeFacade().createStringType(), "qualifiedModelName");
    ASTCDParameter globalScopeParam = getCDParameterFacade()
        .createParameter(getMCTypeFacade().createQualifiedType(globalScopeName), "enclosingScope");
    ASTCDParameter mpParam = getCDParameterFacade()
        .createParameter(getMCTypeFacade().createQualifiedType("de.monticore.io.paths.ModelPath"),
            "modelPath");
    ASTCDMethod method = getCDMethodFacade()
        .createMethod(PUBLIC, "loadSymbolsIntoScope", qualNameParam, globalScopeParam, mpParam);
    this.replaceTemplate(EMPTY_BODY, method,
        new TemplateHookPoint("_symboltable.serialization.scopeDeSer.LoadSymbolsIntoScope"));
    return method;
  }

  protected List<ASTCDMethod> createLoadMethods(String artifactScopeName) {
    ASTMCQualifiedType returnType = getMCTypeFacade().createQualifiedType(artifactScopeName);

    ASTCDParameter urlParam = getCDParameterFacade()
        .createParameter(getMCTypeFacade().createQualifiedType("java.net.URL"), "url");
    ASTCDMethod loadURLMethod = createLoadMethod(urlParam, "url", returnType);

    ASTCDParameter readerParam = getCDParameterFacade()
        .createParameter(getMCTypeFacade().createQualifiedType("java.io.Reader"), "reader");
    ASTCDMethod loadReaderMethod = createLoadMethod(readerParam, "reader", returnType);

    ASTCDParameter stringParam = getCDParameterFacade()
        .createParameter(getMCTypeFacade().createStringType(), "model");
    ASTCDMethod loadStringMethod = createLoadMethod(stringParam, "java.nio.file.Paths.get(model)",
        returnType);

    return Lists.newArrayList(loadURLMethod, loadReaderMethod, loadStringMethod);
  }

  protected ASTCDMethod createLoadMethod(ASTCDParameter parameter, String parameterInvocation,
      ASTMCQualifiedType returnType) {
    ASTCDMethod loadMethod = getCDMethodFacade()
        .createMethod(PUBLIC, returnType, "load", parameter);
    this.replaceTemplate(EMPTY_BODY, loadMethod,
        new TemplateHookPoint("_symboltable.serialization.scopeDeSer.Load", parameterInvocation));
    return loadMethod;
  }

  protected ASTCDMethod createStoreMethod(String scopeDeSerName, String artifactScopeName) {
    ASTCDParameter artifactScopeParam = getCDParameterFacade()
        .createParameter(getMCTypeFacade().createQualifiedType(artifactScopeName), "toSerialize");
    ASTCDParameter symbolPathParam = getCDParameterFacade()
        .createParameter(getMCTypeFacade().createQualifiedType("java.nio.file.Path"), "symbolPath");
    ASTCDMethod storeMethod = getCDMethodFacade()
        .createMethod(PUBLIC, "store", artifactScopeParam, symbolPathParam);
    String generatedError = symbolTableService.getGeneratedErrorCode(scopeDeSerName + "#store1");
    String generatedError2 = symbolTableService.getGeneratedErrorCode(scopeDeSerName + "#store2");
    this.replaceTemplate(EMPTY_BODY, storeMethod,
        new TemplateHookPoint("_symboltable.serialization.scopeDeSer.Store", scopeDeSerName,
            generatedError, generatedError2));
    return storeMethod;
  }

  protected ASTCDMethod createSerializeMethod(String scopeInterfaceName) {
    ASTCDParameter toSerializeParam = getCDParameterFacade()
        .createParameter(getMCTypeFacade().createQualifiedType(scopeInterfaceName), "toSerialize");
    ASTCDMethod serializeMethod = getCDMethodFacade()
        .createMethod(PUBLIC, getMCTypeFacade().createStringType(), "serialize", toSerializeParam);
    this.replaceTemplate(EMPTY_BODY, serializeMethod,
        new TemplateHookPoint("_symboltable.serialization.scopeDeSer.Serialize4ScopeDeSer"));
    return serializeMethod;
  }

  protected ASTCDMethod createDeserializeStringMethod(String artifactScopeName) {
    ASTCDParameter stringParam = getCDParameterFacade()
        .createParameter(getMCTypeFacade().createStringType(), "serialized");
    ASTCDMethod deserializeMethod = getCDMethodFacade()
        .createMethod(PUBLIC, getMCTypeFacade().createQualifiedType(artifactScopeName), DESERIALIZE,
            stringParam);
    this.replaceTemplate(EMPTY_BODY, deserializeMethod,
        new TemplateHookPoint("_symboltable.serialization.scopeDeSer.DeserializeString4ScopeDeSer",
            Names.getSimpleName(artifactScopeName)));
    return deserializeMethod;
  }

  protected ASTCDMethod createDeserializeScopeMethod(String scopeClassName, String simpleName,
      String symTabMill,
      ASTCDParameter jsonParam, List<ASTCDAttribute> scopeRuleAttributes) {
    ASTCDMethod deserializeMethod = getCDMethodFacade()
        .createMethod(PROTECTED, getMCTypeFacade().createQualifiedType(scopeClassName),
            DESERIALIZE + simpleName + SCOPE_SUFFIX, jsonParam);
    String scopeClassBuilder = symbolTableService.getScopeClassSimpleName() + BUILDER_SUFFIX;
    this.replaceTemplate(EMPTY_BODY, deserializeMethod, new TemplateHookPoint(
        TEMPLATE_PATH + "DeserializeScope", symTabMill, scopeClassName, scopeClassBuilder,
        scopeRuleAttributes));
    return deserializeMethod;
  }

  protected ASTCDMethod createDeserializeArtifactScopeMethod(String artifactScopeName,
      String simpleName, String symTabMill,
      ASTCDParameter jsonParam, List<ASTCDAttribute> scopeRuleAttributes) {
    ASTCDMethod deserializeMethod = getCDMethodFacade()
        .createMethod(PROTECTED, getMCTypeFacade().createQualifiedType(artifactScopeName),
            DESERIALIZE + simpleName + ARTIFACT_PREFIX + SCOPE_SUFFIX, jsonParam);
    String artifactScopeBuilder = symbolTableService.getArtifactScopeSimpleName() + BUILDER_SUFFIX;
    this.replaceTemplate(EMPTY_BODY, deserializeMethod, new TemplateHookPoint(
        TEMPLATE_PATH + "DeserializeArtifactScope", symTabMill, artifactScopeName,
        artifactScopeBuilder, scopeRuleAttributes));
    return deserializeMethod;
  }

  protected List<ASTCDMethod> createDeserializeAdditionalAttributesMethods(
      String scopeInterfaceName, ASTCDParameter jsonParam) {
    ASTCDParameter scopeParam = getCDParameterFacade()
        .createParameter(symbolTableService.getScopeInterfaceType(), SCOPE_VAR);
    ASTCDMethod scopeMethod = getCDMethodFacade()
        .createMethod(PROTECTED, "deserializeAdditionalScopeAttributes", scopeParam, jsonParam);

    ASTCDParameter artifactScopeParam = getCDParameterFacade()
        .createParameter(symbolTableService.getArtifactScopeType(), SCOPE_VAR);
    ASTCDMethod artifactScopeMethod = getCDMethodFacade()
        .createMethod(PROTECTED, "deserializeAdditionalArtifactScopeAttributes", artifactScopeParam,
            jsonParam);
    return Lists.newArrayList(scopeMethod, artifactScopeMethod);
  }

  protected ASTCDMethod createAddSymbolsMethod() {
    ASTCDParameter jsonParam = getCDParameterFacade()
        .createParameter(getMCTypeFacade().createQualifiedType(JSON_OBJECT), SCOPE_JSON_VAR);
    ASTCDParameter scopeParam = getCDParameterFacade()
        .createParameter(symbolTableService.getScopeInterfaceType(), SCOPE_VAR);
    String errorCode = symbolTableService.getGeneratedErrorCode(scopeParam+"#createAddSymbolsMethod");
    String errorCode2 = symbolTableService.getGeneratedErrorCode(scopeParam+"#createAddSymbolsMethod2");

    ASTCDMethod deserializeMethod = getCDMethodFacade()
        .createMethod(PROTECTED, "addSymbols", jsonParam, scopeParam);
    this.replaceTemplate(EMPTY_BODY, deserializeMethod,
        new TemplateHookPoint(TEMPLATE_PATH + "AddSymbols", errorCode, errorCode2));
    return deserializeMethod;
  }

  protected ASTCDMethod createAddSymbolMethod(Map<ASTCDType, ASTCDDefinition> symbolDefiningProds) {

    ASTCDParameter kindParam = getCDParameterFacade()
        .createParameter(getMCTypeFacade().createStringType(), "kind");
    ASTCDParameter symbolParam = getCDParameterFacade()
        .createParameter(getMCTypeFacade().createQualifiedType(JSON_OBJECT), "symbol");
    ASTCDParameter scopeParam = getCDParameterFacade()
        .createParameter(symbolTableService.getScopeInterfaceType(), SCOPE_VAR);

    Map<String, String> symbolMap = new HashMap<>();
    for (ASTCDType symbolDefiningProd : symbolDefiningProds.keySet()) {
      CDDefinitionSymbol grammarSymbol = symbolDefiningProds.get(symbolDefiningProd).getSymbol();
      String symbolFullName = symbolTableService.getSymbolFullName(symbolDefiningProd, grammarSymbol);
      String symbolSimpleName = symbolTableService.getSymbolSimpleName(symbolDefiningProd);
      symbolMap.put(symbolSimpleName, symbolFullName);
    }
    ASTCDMethod deserializeMethod = getCDMethodFacade()
        .createMethod(PROTECTED, getMCTypeFacade().createBooleanType(), "addSymbol", kindParam, symbolParam, scopeParam);
    if(!symbolMap.isEmpty()){
      this.replaceTemplate(EMPTY_BODY, deserializeMethod,
          new TemplateHookPoint(TEMPLATE_PATH + "AddSymbol", symbolMap));
    }
    else{
      this.replaceTemplate(EMPTY_BODY, deserializeMethod, new StringHookPoint("return false;"));
    }
    return deserializeMethod;
  }

  protected List<ASTCDMethod> createDeserializeSymbolMethods(
      Map<ASTCDType, ASTCDDefinition> symbolDefiningProds) {
    List<ASTCDMethod> deserializeMethodList = new ArrayList<>();
    ASTCDParameter jsonParam = getCDParameterFacade()
        .createParameter(getMCTypeFacade().createQualifiedType(JSON_OBJECT), SYMBOL_JSON_VAR);
    ASTCDParameter scopeParam = getCDParameterFacade()
        .createParameter(symbolTableService.getScopeInterfaceType(), SCOPE_VAR);
    String scopeFullName = symbolTableService.getScopeClassFullName();
    String scopeSimpleName = symbolTableService.getScopeClassSimpleName();
    String symTabMill = symbolTableService.getMillFullName();

    for (ASTCDType symbol : symbolDefiningProds.keySet()) {
      CDDefinitionSymbol grammarSymbol = symbolDefiningProds.get(symbol).getSymbol();
      String symbolFullName = symbolTableService.getSymbolFullName(symbol, grammarSymbol);
      String symbolSimpleName = symbolTableService.getSymbolSimpleName(symbol);
      boolean hasSpannedScope = symbolTableService.hasSymbolSpannedScope(symbol);
      ASTCDMethod deserializeMethod = getCDMethodFacade()
          .createMethod(PROTECTED, DESERIALIZE + symbolSimpleName, jsonParam, scopeParam);
      this.replaceTemplate(EMPTY_BODY, deserializeMethod,
          new TemplateHookPoint(TEMPLATE_PATH + "DeserializeSymbol4ScopeDeSer",
              symbolSimpleName, symbolFullName, hasSpannedScope, scopeSimpleName, scopeFullName,
              symTabMill));
      deserializeMethodList.add(deserializeMethod);
    }
    return deserializeMethodList;
  }

  protected List<ASTCDMethod> createDeserializeScopeRuleAttributesMethod(
      List<ASTCDAttribute> attributeList, String deSerName) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDAttribute astcdAttribute : attributeList) {
      String methodName = DESERIALIZE +
          StringTransformations.capitalize(astcdAttribute.getName());
      ASTCDParameter jsonParam = getCDParameterFacade()
          .createParameter(getMCTypeFacade().createQualifiedType(JSON_OBJECT), "scopeJson");
      ASTCDMethod deserializeMethod = getCDMethodFacade()
          .createMethod(PUBLIC, astcdAttribute.getMCType(), methodName, jsonParam);
      String generatedErrorCode = symbolTableService.getGeneratedErrorCode(methodName);
      HookPoint deserImplementation = DeSerMap
          .getDeserializationImplementation(astcdAttribute, methodName, "scopeJson",
              //          astcdAttribute.getEnclosingScope()); //TODO AB Replace line below with this line after release of 5.5.0-SNAPSHOT
              BuiltInJavaTypeSymbolResolvingDelegate.getScope(), generatedErrorCode);
      this.replaceTemplate(EMPTY_BODY, deserializeMethod, deserImplementation);
      methodList.add(deserializeMethod);
    }
    return methodList;
  }

}
