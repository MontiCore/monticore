package de.monticore.codegen.cd2java._symboltable.serialization;

import com.google.common.collect.Lists;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.cd.cd4analysis._ast.ASTCDParameter;
import de.monticore.cd.cd4analysis._ast.ASTCDType;
import de.monticore.cd.cd4code._ast.CD4CodeMill;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;

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
  public ASTCDClass decorate(ASTCDType input) {
    String symbolDeSerName = symbolTableService.getSymbolDeSerSimpleName(input);
    String symbolFullName = symbolTableService.getSymbolFullName(input);
    String symbolSimpleName = symbolTableService.getSymbolSimpleName(input);
    String iDeSer = String.format(I_DE_SER_TYPE, symbolFullName);
    String symbolTablePrinterName = symbolTableService.getSymbolTablePrinterFullName();
    String symbolBuilderFullName = symbolTableService.getSymbolBuilderFullName(input);
    String symbolBuilderSimpleName = symbolTableService.getSymbolBuilderSimpleName(input);
    String symTabMillFullName = symbolTableService.getSymTabMillFullName();

    return CD4CodeMill.cDClassBuilder()
        .setName(symbolDeSerName)
        .setModifier(PUBLIC.build())
        .addInterface((ASTMCObjectType) getCDTypeFacade().createTypeByDefinition(iDeSer))
        .addCDMethod(createGetSerializedKindMethod(symbolFullName))
        .addCDMethod(createSerializeMethod(symbolFullName, symbolTablePrinterName))
        .addAllCDMethods(createDeserializeMethods(symbolFullName, symbolSimpleName, symbolBuilderFullName,
            symbolBuilderSimpleName, symTabMillFullName))
        .build();
  }

  protected List<ASTCDMethod> createDeserializeMethods(String symbolFullName, String symbolSimpleName,
                                                       String symbolBuilderFullName, String symbolBuilderSimpleName,
                                                       String symTabMill) {

    ASTCDMethod deserializeStringMethod = createDeserializeStringMethod(symbolFullName);
    ASTCDParameter jsonParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(JSON_OBJECT), SYMBOL_JSON_VAR);
    ASTCDMethod deserializeJsonObjectMethod = createDeserializeJsonObjectMethod(symbolFullName, symbolSimpleName, jsonParam);
    ASTCDMethod deserializeSymbolMethod = createDeserializeSymbolMethod(symbolBuilderFullName, symbolBuilderSimpleName,
        symTabMill, symbolFullName, symbolSimpleName, jsonParam);
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
                                                      ASTCDParameter jsonParam) {
    ASTCDMethod deserializeMethod = getCDMethodFacade().createMethod(PROTECTED, getCDTypeFacade().createQualifiedType(symbolFullName), "deserialize" + symbolSimpleName, jsonParam);
    this.replaceTemplate(EMPTY_BODY, deserializeMethod, new TemplateHookPoint(TEMPLATE_PATH + "DeserializeSymbol",
        symbolBuilderFullName, symbolBuilderSimpleName, symTabMill, symbolFullName));
    return deserializeMethod;
  }

  protected ASTCDMethod createDeserializeAdditionalAttributesSymbolMethod(String symbolFullName, ASTCDParameter jsonParam) {
    ASTCDParameter symbolParam = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(symbolFullName), SYMBOL_VAR);
    return getCDMethodFacade().createMethod(PROTECTED, "deserializeAdditionalAttributes", symbolParam, jsonParam);
  }
}
