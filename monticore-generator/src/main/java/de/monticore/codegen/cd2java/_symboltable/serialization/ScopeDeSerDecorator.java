package de.monticore.codegen.cd2java._symboltable.serialization;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4code._ast.CD4CodeMill;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
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

public class ScopeDeSerDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDClass> {

  protected final SymbolTableService symbolTableService;

  public ScopeDeSerDecorator(final GlobalExtensionManagement glex,
                             final SymbolTableService symbolTableService) {
    super(glex);
    this.symbolTableService = symbolTableService;
  }

  @Override
  public ASTCDClass decorate(ASTCDCompilationUnit input) {
    String scopeDeSerName = symbolTableService.getScopeDeSerSimpleName();
    String scopeInterfaceName = symbolTableService.getScopeInterfaceFullName();
    String iDeSer = String.format(I_DE_SER_TYPE, scopeInterfaceName);
    String artifactScopeFullName = symbolTableService.getArtifactScopeFullName();
    String languageClassFullName = symbolTableService.getLanguageClassFullName();
    String scopeClassFullName = symbolTableService.getScopeClassFullName();
    String symbolTablePrinterFullName = symbolTableService.getSymbolTablePrinterFullName();
    String simpleName = symbolTableService.getCDName();

    List<ASTCDType> symbolDefiningProds = symbolTableService.getSymbolDefiningProds(input.getCDDefinition());
    Map<String, String> symbolMap = new HashMap<>();
    for (ASTCDType symbolDefiningProd : symbolDefiningProds) {
      String symbolFullName = symbolTableService.getSymbolFullName(symbolDefiningProd);
      String symbolSimpleName = symbolTableService.getSymbolSimpleName(symbolDefiningProd);
      symbolMap.put(symbolSimpleName, symbolFullName);
    }
    ASTCDDefinition astcdDefinition = input.getCDDefinition().deepClone();

    return CD4CodeMill.cDClassBuilder()
        .setName(scopeDeSerName)
        .setModifier(PUBLIC.build())
        .addInterface((ASTMCObjectType) getCDTypeFacade().createTypeByDefinition(iDeSer))
        .addAllCDAttributes(createSymbolDeSerAttributes(astcdDefinition))
        .addCDMethod(createSoreMethod(artifactScopeFullName, languageClassFullName))
        .addCDMethod(createGetSerializedKindMethod(scopeClassFullName))
        .addCDMethod(createGetSerializedASKindMethod(artifactScopeFullName))
        .addCDMethod(createSerializeMethod(scopeInterfaceName, symbolTablePrinterFullName))
        .addCDMethod(createDeserializeStringMethod(scopeInterfaceName))
        .addCDMethod(createDeserializeJsonObjectMethod(scopeInterfaceName, simpleName))
        .addCDMethod(createDeserializeScopeMethod(scopeClassFullName, simpleName))
        .addCDMethod(createDeserializeArtifactScopeMethod(artifactScopeFullName, simpleName))
        .addCDMethod(createAddSymbolsMethod(scopeClassFullName, symbolMap))
        .addCDMethod(createAddAndLinkSubScopesMethod(scopeClassFullName, scopeInterfaceName))
        .addCDMethod(createAddAndLinkSpanningSymbolMethod(scopeClassFullName, scopeInterfaceName, astcdDefinition))
        .addAllCDMethods(createDeserializeSymbolMethods(scopeClassFullName, symbolMap))
        .addCDMethod(createDeserializeAdditionalAttributesMethod(scopeInterfaceName))
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
      this.replaceTemplate(VALUE, attribute, new StringHookPoint("= new " + symbolDeSerName + "();"));
      symbolDeSerAttributes.add(attribute);
    }
    return symbolDeSerAttributes;
  }

  protected ASTCDMethod createSoreMethod(String artifactScopeName, String languageName) {
    ASTCDParameter artifactScopeParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(artifactScopeName), "as");
    ASTCDParameter langParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(languageName), "lang");
    ASTCDParameter symbolPathParam = getCDParameterFacade().createParameter(String.class, "symbolPath");
    ASTCDMethod storeMethod = getCDMethodFacade().createMethod(PUBLIC, "store", artifactScopeParam, langParam, symbolPathParam);
    this.replaceTemplate(EMPTY_BODY, storeMethod, new StringHookPoint("store(as, java.nio.file.Paths.get(symbolPath, as.getFilePath(lang).toString()));"));
    return storeMethod;
  }

  protected ASTCDMethod createGetSerializedKindMethod(String scopeClassName) {
    ASTCDMethod getSerializedKindMethod = getCDMethodFacade().createMethod(PUBLIC, getCDTypeFacade().createStringType(), "getSerializedKind");
    this.replaceTemplate(EMPTY_BODY, getSerializedKindMethod, new StringHookPoint("return \"" + scopeClassName + "\";"));
    return getSerializedKindMethod;
  }

  protected ASTCDMethod createGetSerializedASKindMethod(String artifactScopeClassName) {
    ASTCDMethod getSerializedKindMethod = getCDMethodFacade().createMethod(PUBLIC, getCDTypeFacade().createStringType(), "getSerializedASKind");
    this.replaceTemplate(EMPTY_BODY, getSerializedKindMethod, new StringHookPoint("return \"" + artifactScopeClassName + "\";"));
    return getSerializedKindMethod;
  }

  protected ASTCDMethod createSerializeMethod(String scopeInterfaceName, String symbolTablePrinter) {
    ASTCDParameter toSerializeParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(scopeInterfaceName), "toSerialize");
    ASTCDMethod serializeMethod = getCDMethodFacade().createMethod(PUBLIC, getCDTypeFacade().createStringType(), "serialize", toSerializeParam);
    this.replaceTemplate(EMPTY_BODY, serializeMethod, new TemplateHookPoint("_symboltable.serialization.symbolDeSer.Serialize", symbolTablePrinter));
    return serializeMethod;
  }

  protected ASTCDMethod createDeserializeStringMethod(String scopeInterfaceName) {
    ASTCDParameter stringParam = getCDParameterFacade().createParameter(getCDTypeFacade().createStringType(), "serialized");
    ASTCDMethod deserializeMethod = getCDMethodFacade().createMethod(PUBLIC, getCDTypeFacade().createOptionalTypeOf(scopeInterfaceName), "deserialize", stringParam);
    this.replaceTemplate(EMPTY_BODY, deserializeMethod, new TemplateHookPoint("_symboltable.serialization.symbolDeSer.DeserializeString"));
    return deserializeMethod;
  }

  protected ASTCDMethod createDeserializeJsonObjectMethod(String scopeInterfaceName, String simpleName) {
    ASTCDParameter jsonParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(JSON_OBJECT), SCOPE_JSON_VAR);
    ASTCDMethod deserializeMethod = getCDMethodFacade().createMethod(PUBLIC, getCDTypeFacade().createOptionalTypeOf(scopeInterfaceName), "deserialize", jsonParam);
    this.replaceTemplate(EMPTY_BODY, deserializeMethod, new TemplateHookPoint("_symboltable.serialization.scopeDeSer.DeserializeJsonObject", simpleName));
    return deserializeMethod;
  }

  protected ASTCDMethod createDeserializeScopeMethod(String scopeClassName, String simpleName) {
    ASTCDParameter jsonParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(JSON_OBJECT), SCOPE_JSON_VAR);
    ASTCDMethod deserializeMethod = getCDMethodFacade().createMethod(PROTECTED, getCDTypeFacade().createQualifiedType(scopeClassName), "deserialize" + simpleName + SCOPE_SUFFIX, jsonParam);
    this.replaceTemplate(EMPTY_BODY, deserializeMethod, new TemplateHookPoint("_symboltable.serialization.scopeDeSer.DeserializeScope", scopeClassName));
    return deserializeMethod;
  }

  protected ASTCDMethod createDeserializeArtifactScopeMethod(String artifactScopeName, String simpleName) {
    ASTCDParameter jsonParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(JSON_OBJECT), SCOPE_JSON_VAR);
    ASTCDMethod deserializeMethod = getCDMethodFacade().createMethod(PROTECTED, getCDTypeFacade().createQualifiedType(artifactScopeName), "deserialize" + simpleName + ARTIFACT_PREFIX + SCOPE_SUFFIX, jsonParam);
    this.replaceTemplate(EMPTY_BODY, deserializeMethod, new TemplateHookPoint("_symboltable.serialization.scopeDeSer.DeserializeArtifactScope", artifactScopeName));
    return deserializeMethod;
  }

  protected ASTCDMethod createAddSymbolsMethod(String scopeName, Map<String, String> symbolMap) {

    ASTCDParameter jsonParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(JSON_OBJECT), SCOPE_JSON_VAR);
    ASTCDParameter scopeParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(scopeName), SCOPE_VAR);

    ASTCDMethod deserializeMethod = getCDMethodFacade().createMethod(PROTECTED, "addSymbols", jsonParam, scopeParam);
    this.replaceTemplate(EMPTY_BODY, deserializeMethod, new TemplateHookPoint("_symboltable.serialization.scopeDeSer.AddSymbols", symbolMap));
    return deserializeMethod;
  }

  protected ASTCDMethod createAddAndLinkSubScopesMethod(String scopeClassName, String scopeInterfaceName) {
    ASTCDParameter jsonParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(JSON_OBJECT), SCOPE_JSON_VAR);
    ASTCDParameter scopeParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(scopeClassName), SCOPE_VAR);

    ASTCDMethod deserializeMethod = getCDMethodFacade().createMethod(PROTECTED, "addAndLinkSubScopes", jsonParam, scopeParam);
    this.replaceTemplate(EMPTY_BODY, deserializeMethod, new TemplateHookPoint("_symboltable.serialization.scopeDeSer.AddAndLinkSubScopes", scopeInterfaceName));
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

    ASTCDParameter jsonParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(JSON_OBJECT), "subScopeJson");
    ASTCDParameter scopeInterfaceParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(scopeInterfaceName), "subScope");
    ASTCDParameter scopeClassParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(scopeClassName), SCOPE_VAR);

    ASTCDMethod deserializeMethod = getCDMethodFacade().createMethod(PROTECTED, "addAndLinkSpanningSymbol", jsonParam, scopeInterfaceParam, scopeClassParam);
    this.replaceTemplate(EMPTY_BODY, deserializeMethod, new TemplateHookPoint("_symboltable.serialization.scopeDeSer.AddAndLinkSpanningSymbol", symbolMap));
    return deserializeMethod;
  }

  protected List<ASTCDMethod> createDeserializeSymbolMethods(String scopeName, Map<String, String> symbolMap) {
    List<ASTCDMethod> deserializeMethodList = new ArrayList<>();
    ASTCDParameter jsonParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(JSON_OBJECT), SYMBOL_JSON_VAR);
    ASTCDParameter scopeParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(scopeName), SCOPE_VAR);

    for (String symbolName : symbolMap.keySet()) {
      ASTCDMethod deserializeMethod = getCDMethodFacade().createMethod(PROTECTED, "deserialize" + symbolName, jsonParam, scopeParam);
      this.replaceTemplate(EMPTY_BODY, deserializeMethod, new TemplateHookPoint("_symboltable.serialization.scopeDeSer.DeserializeSymbol", symbolName, symbolMap.get(symbolName)));
      deserializeMethodList.add(deserializeMethod);
    }
    return deserializeMethodList;
  }

  protected ASTCDMethod createDeserializeAdditionalAttributesMethod(String scopeInterfaceName) {
    ASTCDParameter jsonParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(JSON_OBJECT), SCOPE_JSON_VAR);
    ASTCDParameter scopeInterfaceParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(scopeInterfaceName), SCOPE_VAR);

    return getCDMethodFacade().createMethod(PROTECTED, "deserializeAdditionalAttributes", scopeInterfaceParam, jsonParam);
  }

}
