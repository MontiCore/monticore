package de.monticore.codegen.cd2java._symboltable.serialization;

import com.google.common.collect.Lists;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4code._ast.CD4CodeMill;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types.typesymbols._symboltable.TypeSymbolsScope;
import de.se_rwth.commons.StringTransformations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

/**
 * creates a SymbolDeSer class from a grammar
 */
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
    ASTMCBasicGenericType iDeSerType = getMCTypeFacade()
        .createBasicGenericTypeOf(I_DE_SER_TYPE, symbolFullName);
    String symbolTablePrinterName = symbolTableService.getSymbolTablePrinterFullName();
    String symbolBuilderFullName = symbolTableService.getSymbolBuilderFullName(symbolClass);
    String symbolBuilderSimpleName = symbolTableService.getSymbolBuilderSimpleName(symbolClass);
    String symTabMillFullName = symbolTableService.getSymTabMillFullName();

    return CD4CodeMill.cDClassBuilder()
        .setName(symbolDeSerName)
        .setModifier(PUBLIC.build())
        .addInterface(iDeSerType)
        .addCDMethod(createGetSerializedKindMethod(symbolFullName))
        .addCDMethod(createSerializeMethod(symbolFullName, symbolTablePrinterName))
        .addAllCDMethods(
            createDeserializeMethods(symbolFullName, symbolSimpleName, symbolBuilderFullName,
                symbolBuilderSimpleName, symTabMillFullName,
                symbolClass.deepClone().getCDAttributeList()))
        .addAllCDMethods(createDeserializeSymbolRuleAttributesMethod(
            symbolClass.deepClone().getCDAttributeList(), symbolDeSerName))
        .build();
  }

  protected List<ASTCDMethod> createDeserializeMethods(String symbolFullName,
      String symbolSimpleName,
      String symbolBuilderFullName, String symbolBuilderSimpleName,
      String symTabMill, List<ASTCDAttribute> symbolRuleAttributes) {
    ASTCDMethod deserializeStringMethod = createDeserializeStringMethod(symbolFullName);
    ASTCDParameter jsonParam = getCDParameterFacade()
        .createParameter(getMCTypeFacade().createQualifiedType(JSON_OBJECT), SYMBOL_JSON_VAR);
    ASTCDMethod deserializeJsonObjectMethod = createDeserializeJsonObjectMethod(symbolFullName,
        symbolSimpleName, jsonParam);
    ASTCDMethod deserializeSymbolMethod = createDeserializeSymbolMethod(symbolBuilderFullName,
        symbolBuilderSimpleName,
        symTabMill, symbolFullName, symbolSimpleName, jsonParam, symbolRuleAttributes);
    ASTCDMethod deserializeAdditionalAttributesSymbolMethod = createDeserializeAdditionalAttributesSymbolMethod(
        symbolFullName, jsonParam);

    return Lists.newArrayList(deserializeStringMethod, deserializeJsonObjectMethod,
        deserializeSymbolMethod, deserializeAdditionalAttributesSymbolMethod);
  }

  protected ASTCDMethod createGetSerializedKindMethod(String symbolName) {
    ASTCDMethod getSerializedKindMethod = getCDMethodFacade()
        .createMethod(PUBLIC, getMCTypeFacade().createStringType(), "getSerializedKind");
    this.replaceTemplate(EMPTY_BODY, getSerializedKindMethod,
        new StringHookPoint("return \"" + symbolName + "\";"));
    return getSerializedKindMethod;
  }

  protected ASTCDMethod createSerializeMethod(String symbolName, String symbolTablePrinter) {
    ASTCDParameter toSerializeParam = getCDParameterFacade()
        .createParameter(getMCTypeFacade().createQualifiedType(symbolName), "toSerialize");
    ASTCDMethod serializeMethod = getCDMethodFacade()
        .createMethod(PUBLIC, getMCTypeFacade().createStringType(), "serialize", toSerializeParam);
    this.replaceTemplate(EMPTY_BODY, serializeMethod,
        new TemplateHookPoint(TEMPLATE_PATH + "Serialize", symbolTablePrinter));
    return serializeMethod;
  }

  protected ASTCDMethod createDeserializeStringMethod(String symbolName) {
    ASTCDParameter stringParam = getCDParameterFacade()
        .createParameter(getMCTypeFacade().createStringType(), "serialized");
    ASTCDMethod deserializeMethod = getCDMethodFacade()
        .createMethod(PUBLIC, getMCTypeFacade().createQualifiedType(symbolName), DESERIALIZE,
            stringParam);
    this.replaceTemplate(EMPTY_BODY, deserializeMethod,
        new TemplateHookPoint(TEMPLATE_PATH + "DeserializeString"));
    return deserializeMethod;
  }

  protected ASTCDMethod createDeserializeJsonObjectMethod(String symbolFullName,
      String symbolSimpleName,
      ASTCDParameter jsonParam) {
    ASTCDMethod deserializeMethod = getCDMethodFacade()
        .createMethod(PUBLIC, getMCTypeFacade().createQualifiedType(symbolFullName), DESERIALIZE,
            jsonParam);
    this.replaceTemplate(EMPTY_BODY, deserializeMethod,
        new TemplateHookPoint(TEMPLATE_PATH + "DeserializeJsonObject", symbolSimpleName));
    return deserializeMethod;
  }

  protected ASTCDMethod createDeserializeSymbolMethod(String symbolBuilderFullName,
      String symbolBuilderSimpleName,
      String symTabMill, String symbolFullName, String symbolSimpleName,
      ASTCDParameter jsonParam, List<ASTCDAttribute> symbolRuleAttributes) {
    ASTCDMethod deserializeMethod = getCDMethodFacade()
        .createMethod(PROTECTED, getMCTypeFacade().createQualifiedType(symbolFullName),
            DESERIALIZE + symbolSimpleName, jsonParam);
    this.replaceTemplate(EMPTY_BODY, deserializeMethod,
        new TemplateHookPoint(TEMPLATE_PATH + "DeserializeSymbol",
            symbolBuilderFullName, symbolBuilderSimpleName, symTabMill, symbolFullName,
            symbolRuleAttributes));
    return deserializeMethod;
  }

  protected List<ASTCDMethod> createDeserializeSymbolRuleAttributesMethod(
      List<ASTCDAttribute> attributeList, String deSerName) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDAttribute astcdAttribute : attributeList) {
      String methodName = DESERIALIZE +
          StringTransformations.capitalize(astcdAttribute.getName());
      ASTCDParameter jsonParam = getCDParameterFacade()
          .createParameter(getMCTypeFacade().createQualifiedType(JSON_OBJECT), "symbolJson");
      ASTCDMethod deserializeMethod = getCDMethodFacade()
          .createMethod(PROTECTED, astcdAttribute.getMCType(), methodName , jsonParam);
      String returnType = symbolTableService
          .determineReturnType(deserializeMethod.getMCReturnType().getMCType());
      HookPoint deserImplementation = DeSerMap.getDeserializationImplementation(astcdAttribute, methodName, "symbolJson",
//          astcdAttribute.getEnclosingScope()); //TODO AB Replace line below with this line after release of 5.4.0-SNAPSHOT
          BuiltInJavaTypeSymbolResolvingDelegate.gs);
        this.replaceTemplate(EMPTY_BODY, deserializeMethod, deserImplementation);
      methodList.add(deserializeMethod);
    }
    return methodList;
  }

  protected ASTCDMethod createDeserializeAdditionalAttributesSymbolMethod(String symbolFullName,
      ASTCDParameter jsonParam) {
    ASTCDParameter symbolParam = getCDParameterFacade()
        .createParameter(getMCTypeFacade().createQualifiedType(symbolFullName), SYMBOL_VAR);
    return getCDMethodFacade()
        .createMethod(PROTECTED, "deserializeAdditionalAttributes", symbolParam, jsonParam);
  }
}
