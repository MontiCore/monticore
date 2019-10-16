package de.monticore.codegen.cd2java._symboltable.serialization;

import com.google.common.collect.Lists;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4code._ast.CD4CodeMill;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.se_rwth.commons.StringTransformations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.*;

public class ScopeDeSerDecorator extends AbstractDecorator {

  protected final SymbolTableService symbolTableService;

  protected static final String TEMPLATE_PATH = "_symboltable.serialization.scopeDeSer.";

  public ScopeDeSerDecorator(final GlobalExtensionManagement glex,
                             final SymbolTableService symbolTableService) {
    super(glex);
    this.symbolTableService = symbolTableService;
  }

  public ASTCDClass decorate(ASTCDCompilationUnit scopeInput, ASTCDCompilationUnit symbolInput) {
    String scopeDeSerName = symbolTableService.getScopeDeSerSimpleName();
    String scopeInterfaceName = symbolTableService.getScopeInterfaceFullName();
    ASTMCBasicGenericType iDeSerType = getMCTypeFacade().createBasicGenericTypeOf(I_DE_SER_TYPE, scopeInterfaceName);
    String artifactScopeFullName = symbolTableService.getArtifactScopeFullName();
    String languageClassFullName = symbolTableService.getLanguageClassFullName();
    String scopeClassFullName = symbolTableService.getScopeClassFullName();
    String symbolTablePrinterFullName = symbolTableService.getSymbolTablePrinterFullName();
    String simpleName = symbolTableService.getCDName();

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
        .map(ASTCDClassTOP::getCDAttributeList)
        .flatMap(List::stream)
        .collect(Collectors.toList());
    scopeRuleAttributeList.forEach(a -> symbolTableService.addAttributeDefaultValues(a, this.glex));

    return CD4CodeMill.cDClassBuilder()
        .setName(scopeDeSerName)
        .setModifier(PUBLIC.build())
        .addInterface(iDeSerType)
        .addAllCDAttributes(createSymbolDeSerAttributes(symbolDefinition))
        .addCDMethod(createSoreMethod(artifactScopeFullName, languageClassFullName))
        .addCDMethod(createGetSerializedKindMethod(scopeClassFullName))
        .addCDMethod(createGetSerializedASKindMethod(artifactScopeFullName))
        .addCDMethod(createSerializeMethod(scopeInterfaceName, symbolTablePrinterFullName))
        .addAllCDMethods(createDeserializeMethods(scopeInterfaceName, scopeClassFullName, artifactScopeFullName,
            simpleName, scopeRuleAttributeList))
        .addCDMethod(createAddSymbolsMethod(scopeClassFullName, symbolMap))
        .addCDMethod(createAddAndLinkSubScopesMethod(scopeClassFullName, scopeInterfaceName))
        .addCDMethod(createAddAndLinkSpanningSymbolMethod(scopeClassFullName, scopeInterfaceName, symbolDefinition))
        .addAllCDMethods(createDeserializeSymbolMethods(scopeClassFullName, symbolMap))
        .addAllCDMethods(createDeserializeScopeRuleAttributesMethod(scopeRuleAttributeList, scopeDeSerName))
        .build();
  }

  protected List<ASTCDAttribute> createSymbolDeSerAttributes(ASTCDDefinition astcdDefinition) {
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

  protected ASTCDMethod createSoreMethod(String artifactScopeName, String languageName) {
    ASTCDParameter artifactScopeParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(artifactScopeName), "as");
    ASTCDParameter langParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(languageName), "lang");
    ASTCDParameter symbolPathParam = getCDParameterFacade().createParameter(String.class, "symbolPath");
    ASTCDMethod storeMethod = getCDMethodFacade().createMethod(PUBLIC, "store", artifactScopeParam, langParam, symbolPathParam);
    this.replaceTemplate(EMPTY_BODY, storeMethod, new StringHookPoint("store(as, java.nio.file.Paths.get(symbolPath, as.getFilePath(lang).toString()));"));
    return storeMethod;
  }

  protected ASTCDMethod createGetSerializedKindMethod(String scopeClassName) {
    ASTCDMethod getSerializedKindMethod = getCDMethodFacade().createMethod(PUBLIC, getMCTypeFacade().createStringType(), "getSerializedKind");
    this.replaceTemplate(EMPTY_BODY, getSerializedKindMethod, new StringHookPoint("return \"" + scopeClassName + "\";"));
    return getSerializedKindMethod;
  }

  protected ASTCDMethod createGetSerializedASKindMethod(String artifactScopeClassName) {
    ASTCDMethod getSerializedKindMethod = getCDMethodFacade().createMethod(PUBLIC, getMCTypeFacade().createStringType(), "getSerializedASKind");
    this.replaceTemplate(EMPTY_BODY, getSerializedKindMethod, new StringHookPoint("return \"" + artifactScopeClassName + "\";"));
    return getSerializedKindMethod;
  }

  protected ASTCDMethod createSerializeMethod(String scopeInterfaceName, String symbolTablePrinter) {
    ASTCDParameter toSerializeParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(scopeInterfaceName), "toSerialize");
    ASTCDMethod serializeMethod = getCDMethodFacade().createMethod(PUBLIC, getMCTypeFacade().createStringType(), "serialize", toSerializeParam);
    this.replaceTemplate(EMPTY_BODY, serializeMethod, new TemplateHookPoint("_symboltable.serialization.symbolDeSer.Serialize", symbolTablePrinter));
    return serializeMethod;
  }


  protected List<ASTCDMethod> createDeserializeMethods(String scopeInterfaceName, String scopeClassName,
                                                       String artifactScopeName, String simpleName,
                                                       List<ASTCDAttribute> scopeRuleAttributes) {
    ASTCDMethod deserializeStringMethod = createDeserializeStringMethod(scopeInterfaceName);

    ASTCDParameter scopeJsonParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(JSON_OBJECT), SCOPE_JSON_VAR);
    ASTCDMethod deserializeJsonObjectMethod = createDeserializeJsonObjectMethod(scopeInterfaceName, simpleName, scopeJsonParam);
    ASTCDMethod deserializeScopeMethod = createDeserializeScopeMethod(scopeClassName, simpleName, scopeJsonParam, scopeRuleAttributes);
    ASTCDMethod deserializeArtifactScopeMethod = createDeserializeArtifactScopeMethod(artifactScopeName, simpleName, scopeJsonParam, scopeRuleAttributes);
    ASTCDMethod deserializeAdditionalAttributesMethod = createDeserializeAdditionalAttributesMethod(scopeInterfaceName, scopeJsonParam);

    return Lists.newArrayList(deserializeStringMethod, deserializeJsonObjectMethod, deserializeScopeMethod,
        deserializeArtifactScopeMethod, deserializeAdditionalAttributesMethod);
  }

  protected ASTCDMethod createDeserializeStringMethod(String scopeInterfaceName) {
    ASTCDParameter stringParam = getCDParameterFacade().createParameter(getMCTypeFacade().createStringType(), "serialized");
    ASTCDMethod deserializeMethod = getCDMethodFacade().createMethod(PUBLIC, getMCTypeFacade().createOptionalTypeOf(scopeInterfaceName), DESERIALIZE, stringParam);
    this.replaceTemplate(EMPTY_BODY, deserializeMethod, new TemplateHookPoint("_symboltable.serialization.symbolDeSer.DeserializeString"));
    return deserializeMethod;
  }

  protected ASTCDMethod createDeserializeJsonObjectMethod(String scopeInterfaceName, String simpleName,
                                                          ASTCDParameter jsonParam) {
    ASTCDMethod deserializeMethod = getCDMethodFacade().createMethod(PUBLIC, getMCTypeFacade().createOptionalTypeOf(scopeInterfaceName), DESERIALIZE, jsonParam);
    this.replaceTemplate(EMPTY_BODY, deserializeMethod, new TemplateHookPoint(TEMPLATE_PATH + "DeserializeJsonObject", simpleName));
    return deserializeMethod;
  }

  protected ASTCDMethod createDeserializeScopeMethod(String scopeClassName, String simpleName,
                                                     ASTCDParameter jsonParam, List<ASTCDAttribute> scopeRuleAttributes) {
    ASTCDMethod deserializeMethod = getCDMethodFacade().createMethod(PROTECTED, getMCTypeFacade().createQualifiedType(scopeClassName), DESERIALIZE + simpleName + SCOPE_SUFFIX, jsonParam);
    this.replaceTemplate(EMPTY_BODY, deserializeMethod, new TemplateHookPoint(
        TEMPLATE_PATH + "DeserializeScope", scopeClassName, scopeRuleAttributes));
    return deserializeMethod;
  }

  protected ASTCDMethod createDeserializeArtifactScopeMethod(String artifactScopeName, String simpleName,
                                                             ASTCDParameter jsonParam, List<ASTCDAttribute> scopeRuleAttributes) {
    ASTCDMethod deserializeMethod = getCDMethodFacade().createMethod(PROTECTED, getMCTypeFacade().createQualifiedType(artifactScopeName), DESERIALIZE + simpleName + ARTIFACT_PREFIX + SCOPE_SUFFIX, jsonParam);
    this.replaceTemplate(EMPTY_BODY, deserializeMethod, new TemplateHookPoint(
        TEMPLATE_PATH + "DeserializeArtifactScope", artifactScopeName, scopeRuleAttributes));
    return deserializeMethod;
  }

  protected ASTCDMethod createDeserializeAdditionalAttributesMethod(String scopeInterfaceName, ASTCDParameter jsonParam) {
    ASTCDParameter scopeInterfaceParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(scopeInterfaceName), SCOPE_VAR);
    return getCDMethodFacade().createMethod(PROTECTED, "deserializeAdditionalAttributes", scopeInterfaceParam, jsonParam);
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
    ASTCDParameter scopeParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(scopeClassName), SCOPE_VAR);

    ASTCDMethod deserializeMethod = getCDMethodFacade().createMethod(PROTECTED, "addAndLinkSubScopes", jsonParam, scopeParam);
    this.replaceTemplate(EMPTY_BODY, deserializeMethod, new TemplateHookPoint(TEMPLATE_PATH + "AddAndLinkSubScopes", scopeInterfaceName));
    return deserializeMethod;
  }

  protected ASTCDMethod createAddAndLinkSpanningSymbolMethod(String scopeClassName, String scopeInterfaceName, ASTCDDefinition astcdDefinition) {
    List<ASTCDType> symbolDefiningProds = symbolTableService.getSymbolDefiningProds(astcdDefinition);

    List<ASTCDType> scopeSpanningSymbolNames = symbolDefiningProds.stream()
        .filter(c -> c.getModifierOpt().isPresent())
        .filter(c -> symbolTableService.hasScopeStereotype(c.getModifierOpt().get())
            || symbolTableService.hasInheritedScopeStereotype(c.getModifierOpt().get()))
        .collect(Collectors.toList());

    Map<String, String> symbolMap = new HashMap<>();
    for (ASTCDType symbolDefiningProd : scopeSpanningSymbolNames) {
      String symbolFullName = symbolTableService.getSymbolFullName(symbolDefiningProd);
      String symbolSimpleName = symbolTableService.getSymbolSimpleName(symbolDefiningProd);
      symbolMap.put(symbolSimpleName, symbolFullName);
    }

    ASTCDParameter jsonParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(JSON_OBJECT), "subScopeJson");
    ASTCDParameter scopeInterfaceParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(scopeInterfaceName), "subScope");
    ASTCDParameter scopeClassParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(scopeClassName), SCOPE_VAR);

    ASTCDMethod deserializeMethod = getCDMethodFacade().createMethod(PROTECTED, "addAndLinkSpanningSymbol", jsonParam, scopeInterfaceParam, scopeClassParam);
    this.replaceTemplate(EMPTY_BODY, deserializeMethod, new TemplateHookPoint(TEMPLATE_PATH + "AddAndLinkSpanningSymbol", symbolMap));
    return deserializeMethod;
  }

  protected List<ASTCDMethod> createDeserializeSymbolMethods(String scopeName, Map<String, String> symbolMap) {
    List<ASTCDMethod> deserializeMethodList = new ArrayList<>();
    ASTCDParameter jsonParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(JSON_OBJECT), SYMBOL_JSON_VAR);
    ASTCDParameter scopeParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(scopeName), SCOPE_VAR);

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
      ASTCDParameter jsonParam = getCDParameterFacade().createParameter(getMCTypeFacade().createQualifiedType(JSON_OBJECT), "symbolJson");
      ASTCDMethod deserializeMethod = getCDMethodFacade().createMethod(PROTECTED, astcdAttribute.getMCType(), DESERIALIZE +
          StringTransformations.capitalize(astcdAttribute.getName()), jsonParam);
      String returnType = symbolTableService.determineReturnType(deserializeMethod.getMCReturnType().getMCType());
      this.replaceTemplate(EMPTY_BODY, deserializeMethod, new TemplateHookPoint(
          "_symboltable.serialization.symbolDeSer.DeserializeSymbolRuleAttr", astcdAttribute, deSerName, returnType));
      methodList.add(deserializeMethod);
    }
    return methodList;
  }

}
