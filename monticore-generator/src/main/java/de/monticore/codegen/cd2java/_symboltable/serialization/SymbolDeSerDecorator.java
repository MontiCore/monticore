package de.monticore.codegen.cd2java._symboltable.serialization;

import com.google.common.collect.Lists;
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
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class SymbolDeSerDecorator extends AbstractCreator<ASTCDType, ASTCDClass> {

  protected final SymbolTableService symbolTableService;

  protected static final String TEMPLATE_PATH = "_symboltable.serialization.symbolDeSer.";

  public SymbolDeSerDecorator(final GlobalExtensionManagement glex,
                              final SymbolTableService symbolTableService) {
    super(glex);
    this.symbolTableService = symbolTableService;
  }

  @Override
  public ASTCDClass decorate(ASTCDType symbolClass) {
    String symbolDeSerName = symbolTableService.getSymbolDeSerSimpleName(symbolClass);
    String symbolFullName = symbolTableService.getSymbolFullName(symbolClass);
    String symbolSimpleName = symbolTableService.getSymbolSimpleName(symbolClass);
    String iDeSer = String.format(I_DE_SER_TYPE, symbolFullName);
    String symbolTablePrinterName = symbolTableService.getSymbolTablePrinterFullName();
    String symbolBuilderFullName = symbolTableService.getSymbolBuilderFullName(symbolClass);
    String symbolBuilderSimpleName = symbolTableService.getSymbolBuilderSimpleName(symbolClass);
    String symTabMillFullName = symbolTableService.getSymTabMillFullName();

    return CD4CodeMill.cDClassBuilder()
        .setName(symbolDeSerName)
        .setModifier(PUBLIC.build())
        .addInterface((ASTMCObjectType) getCDTypeFacade().createTypeByDefinition(iDeSer))
        .addCDMethod(createGetSerializedKindMethod(symbolFullName))
        .addCDMethod(createSerializeMethod(symbolFullName, symbolTablePrinterName))
        .addAllCDMethods(createDeserializeMethods(symbolFullName, symbolSimpleName, symbolBuilderFullName,
            symbolBuilderSimpleName, symTabMillFullName, symbolClass.deepClone().getCDAttributeList()))
        .addAllCDMethods(createDeserializeSymbolRuleAttributesMethod(symbolClass.deepClone().getCDAttributeList(), symbolDeSerName))
        .build();
  }

  protected List<ASTCDMethod> createDeserializeMethods(String symbolFullName, String symbolSimpleName,
                                                       String symbolBuilderFullName, String symbolBuilderSimpleName,
                                                       String symTabMill, List<ASTCDAttribute> symbolRuleAttributes) {

    ASTCDMethod deserializeStringMethod = createDeserializeStringMethod(symbolFullName);
    ASTCDParameter jsonParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(JSON_OBJECT), SYMBOL_JSON_VAR);
    ASTCDMethod deserializeJsonObjectMethod = createDeserializeJsonObjectMethod(symbolFullName, symbolSimpleName, jsonParam);
    ASTCDMethod deserializeSymbolMethod = createDeserializeSymbolMethod(symbolBuilderFullName, symbolBuilderSimpleName,
        symTabMill, symbolFullName, symbolSimpleName, jsonParam, symbolRuleAttributes);
    ASTCDMethod deserializeAdditionalAttributesSymbolMethod = createDeserializeAdditionalAttributesSymbolMethod(symbolFullName, jsonParam);

    return Lists.newArrayList(deserializeStringMethod, deserializeJsonObjectMethod,
        deserializeSymbolMethod, deserializeAdditionalAttributesSymbolMethod);
  }

  protected ASTCDMethod createGetSerializedKindMethod(String symbolName) {
    ASTCDMethod getSerializedKindMethod = getCDMethodFacade().createMethod(PUBLIC, getCDTypeFacade().createStringType(), "getSerializedKind");
    this.replaceTemplate(EMPTY_BODY, getSerializedKindMethod, new StringHookPoint("return \"" + symbolName + "\";"));
    return getSerializedKindMethod;
  }

  protected ASTCDMethod createSerializeMethod(String symbolName, String symbolTablePrinter) {
    ASTCDParameter toSerializeParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(symbolName), "toSerialize");
    ASTCDMethod serializeMethod = getCDMethodFacade().createMethod(PUBLIC, getCDTypeFacade().createStringType(), "serialize", toSerializeParam);
    this.replaceTemplate(EMPTY_BODY, serializeMethod, new TemplateHookPoint(TEMPLATE_PATH + "Serialize", symbolTablePrinter));
    return serializeMethod;
  }

  protected ASTCDMethod createDeserializeStringMethod(String symbolName) {
    ASTCDParameter stringParam = getCDParameterFacade().createParameter(getCDTypeFacade().createStringType(), "serialized");
    ASTCDMethod deserializeMethod = getCDMethodFacade().createMethod(PUBLIC, getCDTypeFacade().createOptionalTypeOf(symbolName), "deserialize", stringParam);
    this.replaceTemplate(EMPTY_BODY, deserializeMethod, new TemplateHookPoint(TEMPLATE_PATH + "DeserializeString"));
    return deserializeMethod;
  }

  protected ASTCDMethod createDeserializeJsonObjectMethod(String symbolFullName, String symbolSimpleName,
                                                          ASTCDParameter jsonParam) {
    ASTCDMethod deserializeMethod = getCDMethodFacade().createMethod(PUBLIC, getCDTypeFacade().createOptionalTypeOf(symbolFullName), "deserialize", jsonParam);
    this.replaceTemplate(EMPTY_BODY, deserializeMethod, new TemplateHookPoint(TEMPLATE_PATH + "DeserializeJsonObject", symbolSimpleName));
    return deserializeMethod;
  }


  protected ASTCDMethod createDeserializeSymbolMethod(String symbolBuilderFullName, String symbolBuilderSimpleName,
                                                      String symTabMill, String symbolFullName, String symbolSimpleName,
                                                      ASTCDParameter jsonParam, List<ASTCDAttribute> symbolRuleAttributes) {
    ASTCDMethod deserializeMethod = getCDMethodFacade().createMethod(PROTECTED, getCDTypeFacade().createQualifiedType(symbolFullName), "deserialize" + symbolSimpleName, jsonParam);
    this.replaceTemplate(EMPTY_BODY, deserializeMethod, new TemplateHookPoint(TEMPLATE_PATH + "DeserializeSymbol",
        symbolBuilderFullName, symbolBuilderSimpleName, symTabMill, symbolFullName, symbolRuleAttributes));
    return deserializeMethod;
  }

  protected List<ASTCDMethod> createDeserializeSymbolRuleAttributesMethod(List<ASTCDAttribute> attributeList, String deSerName) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDAttribute astcdAttribute : attributeList) {
      ASTCDParameter jsonParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(JSON_OBJECT), "symbolJson");
      ASTCDMethod deserializeMethod = getCDMethodFacade().createMethod(PROTECTED, astcdAttribute.getMCType(), "deserialize" +
          StringTransformations.capitalize(astcdAttribute.getName()), jsonParam);
      this.replaceTemplate(EMPTY_BODY, deserializeMethod, new TemplateHookPoint(
          TEMPLATE_PATH + "DeserializeSymbolRuleAttr", astcdAttribute, deSerName));
      methodList.add(deserializeMethod);
    }
    return methodList;
  }

  protected ASTCDMethod createDeserializeAdditionalAttributesSymbolMethod(String symbolFullName, ASTCDParameter jsonParam) {
    ASTCDParameter symbolParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(symbolFullName), SYMBOL_VAR);
    return getCDMethodFacade().createMethod(PROTECTED, "deserializeAdditionalAttributes", symbolParam, jsonParam);
  }
}
