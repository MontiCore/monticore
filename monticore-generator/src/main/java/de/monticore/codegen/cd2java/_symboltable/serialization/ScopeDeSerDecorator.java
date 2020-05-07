/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.serialization;

import com.google.common.collect.Lists;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4code.CD4CodeMill;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
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

  protected ASTCDParameter enclosingScopeParameter;

  protected MethodDecorator methodDecorator;

  public ScopeDeSerDecorator(final GlobalExtensionManagement glex,
      final SymbolTableService symbolTableService, final MethodDecorator methodDecorator) {
    super(glex);
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
    String symbolTablePrinterFullName = symbolTableService.getSymbolTablePrinterFullName();
    String simpleName = symbolTableService.getCDName();
    this.enclosingScopeParameter = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(scopeInterfaceName), ENCLOSING_SCOPE_VAR);

    List<ASTCDType> symbolDefiningProds = symbolTableService.getSymbolDefiningProds(symbolInput.getCDDefinition());
    Map<String, String> symbolMap = new HashMap<>();
    for (ASTCDType symbolDefiningProd : symbolDefiningProds) {
      String symbolFullName = symbolTableService.getSymbolFullName(symbolDefiningProd);
      String symbolSimpleName = symbolTableService.getSymbolSimpleName(symbolDefiningProd);
      symbolMap.put(symbolSimpleName, symbolFullName);
    }
    ASTCDDefinition symbolDefinition = symbolInput.getCDDefinition().deepClone();

    // list of all scope rule attributes
    List<ASTCDAttribute> scopeRuleAttributeList = scopeInput.deepClone().getCDDefinition().getCDClassList()
        .stream()
        .map(ASTCDClass::getCDAttributeList)
        .flatMap(List::stream)
        .collect(Collectors.toList());
    scopeRuleAttributeList.forEach(a -> getDecorationHelper().addAttributeDefaultValues(a, this.glex));

    ASTCDAttribute fileExtension = getCDAttributeFacade().createAttribute(PRIVATE,
        getMCTypeFacade().createStringType(),"symbolFileExtension");

    return CD4CodeMill.cDClassBuilder()
        .setName(scopeDeSerName)
        .setModifier(PUBLIC.build())
        .addAllCDAttributes(createSymbolDeSerAttributes(symbolDefinition))
        .addCDAttribute(fileExtension)
        .addAllCDMethods(methodDecorator.decorate(fileExtension))
        .addCDMethod(createLoadMethod(scopeInterfaceName, artifactScopeFullName))
        .addCDMethod(createLoadSymbolsIntoScopeMethod(globalScopeFullName))
        .addCDMethod(createStoreMethod(scopeDeSerName, artifactScopeFullName))
        .addCDMethod(createSerializeMethod(scopeInterfaceName, symbolTablePrinterFullName))
        .addAllCDMethods(createDeserializeMethods(scopeInterfaceName, scopeClassFullName, artifactScopeFullName,
            simpleName, scopeRuleAttributeList))
        .addCDMethod(createAddSymbolsMethod(scopeInterfaceName, symbolMap))
        .addCDMethod(createAddAndLinkSubScopesMethod(scopeClassFullName, scopeInterfaceName))
        .addCDMethod(createAddAndLinkSpanningSymbolMethod(scopeInterfaceName, symbolDefinition))
        .addAllCDMethods(createDeserializeSymbolMethods(scopeInterfaceName, symbolMap))
        .addAllCDMethods(createDeserializeScopeRuleAttributesMethod(scopeRuleAttributeList, scopeDeSerName))
        .build();
  }

  protected List<ASTCDAttribute> createSymbolDeSerAttributes(ASTCDDefinition astcdDefinition) {
    // collects symbolDeSer names form all symbol definitions
    List<String> symbolDeSerNames = astcdDefinition.getCDClassList()
        .stream()
        .map(symbolTableService::getSymbolDeSerSimpleName)
        .collect(Collectors.toList());
    symbolDeSerNames.addAll(astcdDefinition.getCDInterfaceList()
        .stream()
        .map(symbolTableService::getSymbolDeSerSimpleName)
        .collect(Collectors.toList()));

    List<ASTCDAttribute> symbolDeSerAttributes = new ArrayList<>();
    for (String symbolDeSerName : symbolDeSerNames) {
      ASTCDAttribute attribute = getCDAttributeFacade().createAttribute(PACKAGE_PRIVATE, symbolDeSerName, StringTransformations.uncapitalize(symbolDeSerName));
      this.replaceTemplate(VALUE, attribute, new StringHookPoint("= new " + symbolDeSerName + "()"));
      symbolDeSerAttributes.add(attribute);
    }
    return symbolDeSerAttributes;
  }

  protected ASTCDMethod createLoadSymbolsIntoScopeMethod(String globalScopeName) {
    ASTCDParameter qualNameParam = getCDParameterFacade().createParameter(getMCTypeFacade().createStringType(), "qualifiedModelName");
    ASTCDParameter globalScopeParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(globalScopeName), "enclosingScope");
    ASTCDParameter mpParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType("de.monticore.io.paths.ModelPath"), "modelPath");
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, "loadSymbolsIntoScope", qualNameParam, globalScopeParam, mpParam);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint("_symboltable.serialization.scopeDeSer.LoadSymbolsIntoScope"));
    return method;
  }

  protected ASTCDMethod createLoadMethod(String scopeInterfaceName, String artifactScopeName) {
    ASTCDParameter iScopeParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(scopeInterfaceName), "enclosingScope");
    ASTCDParameter urlParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType("java.net.URL"), "url");
    ASTMCQualifiedType returnType = getMCTypeFacade().createQualifiedType(artifactScopeName);
    ASTCDMethod loadMethod = getCDMethodFacade().createMethod(PUBLIC, returnType, "load", urlParam, iScopeParam);
    this.replaceTemplate(EMPTY_BODY, loadMethod, new TemplateHookPoint("_symboltable.serialization.scopeDeSer.Load"));
    return loadMethod;
  }

  protected ASTCDMethod createStoreMethod(String scopeDeSerName, String artifactScopeName) {
    ASTCDParameter artifactScopeParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(artifactScopeName), "toSerialize");
    ASTCDParameter symbolPathParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType("java.nio.file.Path"), "symbolPath");
    ASTCDMethod storeMethod = getCDMethodFacade().createMethod(PUBLIC, "store", artifactScopeParam, symbolPathParam);
    this.replaceTemplate(EMPTY_BODY, storeMethod, new TemplateHookPoint("_symboltable.serialization.scopeDeSer.Store", scopeDeSerName));
    return storeMethod;
  }

  protected ASTCDMethod createSerializeMethod(String scopeInterfaceName, String symbolTablePrinter) {
    ASTCDParameter toSerializeParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(scopeInterfaceName), "toSerialize");
    ASTCDMethod serializeMethod = getCDMethodFacade().createMethod(PUBLIC, getMCTypeFacade().createStringType(), "serialize", toSerializeParam);
    this.replaceTemplate(EMPTY_BODY, serializeMethod, new TemplateHookPoint("_symboltable.serialization.symbolDeSer.Serialize", symbolTablePrinter));
    return serializeMethod;
  }


  protected List<ASTCDMethod> createDeserializeMethods(String scopeInterfaceName,
      String scopeClassName,
      String artifactScopeName, String simpleName,
      List<ASTCDAttribute> scopeRuleAttributes) {
    String symTabMillFullName = symbolTableService.getMillFullName();
    ASTCDParameter scopeJsonParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(JSON_OBJECT), SCOPE_JSON_VAR);

    ASTCDMethod deserializeStringMethod = createDeserializeStringMethod(artifactScopeName);
    ASTCDMethod deserializeScopeMethod = createDeserializeScopeMethod(scopeClassName, simpleName, symTabMillFullName, scopeJsonParam, scopeRuleAttributes);
    ASTCDMethod deserializeArtifactScopeMethod = createDeserializeArtifactScopeMethod(artifactScopeName, simpleName, symTabMillFullName, scopeJsonParam, scopeRuleAttributes);
    ASTCDMethod deserializeAdditionalAttributesMethod = createDeserializeAdditionalAttributesMethod(scopeInterfaceName, scopeJsonParam);

    return Lists.newArrayList(deserializeStringMethod, deserializeScopeMethod,
        deserializeArtifactScopeMethod, deserializeAdditionalAttributesMethod);
  }

  protected ASTCDMethod createDeserializeStringMethod(String artifactScopeName) {
    ASTCDParameter stringParam = getCDParameterFacade().createParameter(getMCTypeFacade().createStringType(), "serialized");
    ASTCDMethod deserializeMethod = getCDMethodFacade().createMethod(PUBLIC, getMCTypeFacade().createQualifiedType(artifactScopeName), DESERIALIZE, stringParam, enclosingScopeParameter);
    this.replaceTemplate(EMPTY_BODY, deserializeMethod, new TemplateHookPoint("_symboltable.serialization.scopeDeSer.DeserializeString", Names.getSimpleName(artifactScopeName)));
    return deserializeMethod;
  }

  protected ASTCDMethod createDeserializeScopeMethod(String scopeClassName, String simpleName, String symTabMill,
                                                     ASTCDParameter jsonParam, List<ASTCDAttribute> scopeRuleAttributes) {
    ASTCDMethod deserializeMethod = getCDMethodFacade().createMethod(PROTECTED, getMCTypeFacade().createQualifiedType(scopeClassName), DESERIALIZE + simpleName + SCOPE_SUFFIX, jsonParam, enclosingScopeParameter);
    String scopeClassBuilder = symbolTableService.getScopeClassSimpleName() + BUILDER_SUFFIX;
    this.replaceTemplate(EMPTY_BODY, deserializeMethod, new TemplateHookPoint(
        TEMPLATE_PATH + "DeserializeScope", symTabMill, scopeClassName, scopeClassBuilder, scopeRuleAttributes));
    return deserializeMethod;
  }

  protected ASTCDMethod createDeserializeArtifactScopeMethod(String artifactScopeName, String simpleName, String symTabMill,
                                                             ASTCDParameter jsonParam, List<ASTCDAttribute> scopeRuleAttributes) {
    ASTCDMethod deserializeMethod = getCDMethodFacade().createMethod(PROTECTED, getMCTypeFacade().createQualifiedType(artifactScopeName), DESERIALIZE + simpleName + ARTIFACT_PREFIX + SCOPE_SUFFIX, jsonParam, enclosingScopeParameter);
    String artifactScopeBuilder = symbolTableService.getArtifactScopeSimpleName() + BUILDER_SUFFIX;
    this.replaceTemplate(EMPTY_BODY, deserializeMethod, new TemplateHookPoint(
        TEMPLATE_PATH + "DeserializeArtifactScope", symTabMill, artifactScopeName, artifactScopeBuilder, scopeRuleAttributes));
    return deserializeMethod;
  }

  protected ASTCDMethod createDeserializeAdditionalAttributesMethod(String scopeInterfaceName, ASTCDParameter jsonParam) {
    ASTCDParameter scopeInterfaceParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(scopeInterfaceName), SCOPE_VAR);
    return getCDMethodFacade().createMethod(PROTECTED, "deserializeAdditionalAttributes", scopeInterfaceParam, jsonParam, enclosingScopeParameter);
  }

  protected ASTCDMethod createAddSymbolsMethod(String scopeName, Map<String, String> symbolMap) {
    ASTCDParameter jsonParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(JSON_OBJECT), SCOPE_JSON_VAR);
    ASTCDParameter scopeParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(scopeName), SCOPE_VAR);

    ASTCDMethod deserializeMethod = getCDMethodFacade().createMethod(PROTECTED, "addSymbols", jsonParam, scopeParam);
    this.replaceTemplate(EMPTY_BODY, deserializeMethod, new TemplateHookPoint(TEMPLATE_PATH + "AddSymbols", symbolMap));
    return deserializeMethod;
  }

  protected ASTCDMethod createAddAndLinkSubScopesMethod(String scopeClassName, String scopeInterfaceName) {
    ASTCDParameter jsonParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(JSON_OBJECT), SCOPE_JSON_VAR);
    ASTCDParameter scopeParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(scopeInterfaceName), SCOPE_VAR);

    ASTCDMethod deserializeMethod = getCDMethodFacade().createMethod(PROTECTED, "addAndLinkSubScopes", jsonParam, scopeParam);
    this.replaceTemplate(EMPTY_BODY, deserializeMethod, new TemplateHookPoint(
        TEMPLATE_PATH + "AddAndLinkSubScopes",
        scopeInterfaceName, Names.getSimpleName(scopeClassName)));
    return deserializeMethod;
  }

  protected ASTCDMethod createAddAndLinkSpanningSymbolMethod(String scopeInterfaceName, ASTCDDefinition astcdDefinition) {
    List<ASTCDType> symbolDefiningProds = symbolTableService.getSymbolDefiningProds(astcdDefinition);

    // finds all symbols that also define a scope or inherit the scope property
    List<ASTCDType> scopeSpanningSymbolNames = symbolDefiningProds.stream()
        .filter(c -> c.isPresentModifier())
        .filter(c -> symbolTableService.hasScopeStereotype(c.getModifier())
            || symbolTableService.hasInheritedScopeStereotype(c.getModifier()))
        .collect(Collectors.toList());

    // maps the simpleSymbol name to the fullSymbolName, to use both in the templates
    Map<String, String> symbolMap = new HashMap<>();
    for (ASTCDType symbolDefiningProd : scopeSpanningSymbolNames) {
      String symbolFullName = symbolTableService.getSymbolFullName(symbolDefiningProd);
      String symbolSimpleName = symbolTableService.getSymbolSimpleName(symbolDefiningProd);
      symbolMap.put(symbolSimpleName, symbolFullName);
    }

    ASTCDParameter jsonParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(JSON_OBJECT), "subScopeJson");
    ASTCDParameter scopeInterfaceParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(scopeInterfaceName), "subScope");
    ASTCDParameter scopeClassParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(scopeInterfaceName), SCOPE_VAR);

    ASTCDMethod deserializeMethod = getCDMethodFacade().createMethod(PROTECTED, "addAndLinkSpanningSymbol", jsonParam, scopeInterfaceParam, scopeClassParam);
    if (!symbolMap.isEmpty()) {
      this.replaceTemplate(EMPTY_BODY, deserializeMethod, new TemplateHookPoint(TEMPLATE_PATH + "AddAndLinkSpanningSymbol", symbolMap));
    }
    return deserializeMethod;
  }

  protected List<ASTCDMethod> createDeserializeSymbolMethods(String scopeInterfaceName, Map<String, String> symbolMap) {
    List<ASTCDMethod> deserializeMethodList = new ArrayList<>();
    ASTCDParameter jsonParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(JSON_OBJECT), SYMBOL_JSON_VAR);
    ASTCDParameter scopeParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(scopeInterfaceName), SCOPE_VAR);

    for (String symbolName : symbolMap.keySet()) {
      ASTCDMethod deserializeMethod = getCDMethodFacade().createMethod(PROTECTED, DESERIALIZE + symbolName, jsonParam, scopeParam);
      this.replaceTemplate(EMPTY_BODY, deserializeMethod, new TemplateHookPoint(TEMPLATE_PATH + "DeserializeSymbol", symbolName, symbolMap.get(symbolName)));
      deserializeMethodList.add(deserializeMethod);
    }
    return deserializeMethodList;
  }

  protected List<ASTCDMethod> createDeserializeScopeRuleAttributesMethod(List<ASTCDAttribute> attributeList, String deSerName) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDAttribute astcdAttribute : attributeList) {
      String methodName = DESERIALIZE +
          StringTransformations.capitalize(astcdAttribute.getName());
      ASTCDParameter jsonParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(JSON_OBJECT), "scopeJson");
      ASTCDMethod deserializeMethod = getCDMethodFacade().createMethod(PROTECTED, astcdAttribute.getMCType(), methodName, jsonParam, enclosingScopeParameter);
      String returnType = symbolTableService.determineReturnType(deserializeMethod.getMCReturnType().getMCType());

      HookPoint deserImplementation = DeSerMap.getDeserializationImplementation(astcdAttribute, methodName, "scopeJson",
          //          astcdAttribute.getEnclosingScope()); //TODO AB Replace line below with this line after release of 5.5.0-SNAPSHOT
          BuiltInJavaTypeSymbolResolvingDelegate.getScope());
      this.replaceTemplate(EMPTY_BODY, deserializeMethod, deserImplementation);
      methodList.add(deserializeMethod);
    }
    return methodList;
  }

}
