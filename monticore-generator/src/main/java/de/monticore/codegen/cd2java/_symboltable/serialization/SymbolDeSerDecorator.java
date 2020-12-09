/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.serialization;

import com.google.common.collect.Lists;
import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4code.CD4CodeMill;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.StringTransformations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;

/**
 * creates a SymbolDeSer class from a grammar
 */
public class SymbolDeSerDecorator extends AbstractCreator<ASTCDType, ASTCDClass> {

  protected final SymbolTableService symbolTableService;

  protected static final String TEMPLATE_PATH = "_symboltable.serialization.symbolDeSer.";

  protected BITSer bitser;

  public SymbolDeSerDecorator(final GlobalExtensionManagement glex,
      final SymbolTableService symbolTableService) {
    super(glex);
    this.symbolTableService = symbolTableService;
    bitser = new BITSer();
  }

  @Override
  public ASTCDClass decorate(ASTCDType symbolClass) {
    String symbolDeSerName = symbolTableService.getSymbolDeSerSimpleName(symbolClass);
    String symbolFullName = symbolTableService.getSymbolFullName(symbolClass);
    String symbolSimpleName = symbolTableService.getSymbolSimpleName(symbolClass);
    String symbolBuilderFullName = symbolTableService.getSymbolBuilderFullName(symbolClass);
    String symbolBuilderSimpleName = symbolTableService.getSymbolBuilderSimpleName(symbolClass);
    String symTabMillFullName = symbolTableService.getMillFullName();
    String symbols2JsonFullName = symbolTableService.getSymbols2JsonFullName();

    ASTMCQualifiedType interfaceName = getMCTypeFacade().
            createQualifiedType(I_DE_SER+"<"+symbolFullName+", "+ symbols2JsonFullName +">");


    return CD4CodeMill.cDClassBuilder()
        .setName(symbolDeSerName)
        .setModifier(PUBLIC.build())
        .addInterface(interfaceName)
        .addCDMethod(createGetSerializedKindMethod(symbolFullName))
        .addCDMethod(createSerializeMethod(symbolClass, symbolFullName, symbols2JsonFullName))
        .addCDMethod(createSerializeAdditionalAttributesSymbolMethod(symbolFullName, symbols2JsonFullName))
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
    ASTCDMethod deserializeStringMethod = createDeserializeStringMethod(symbolFullName, symbolSimpleName);
    ASTCDParameter jsonParam = getCDParameterFacade()
        .createParameter(getMCTypeFacade().createQualifiedType(JSON_OBJECT), SYMBOL_JSON_VAR);
    ASTCDMethod deserializeSymbolMethod = createDeserializeSymbolMethod(symbolBuilderFullName,
        symbolBuilderSimpleName,
        symTabMill, symbolFullName, symbolSimpleName, jsonParam, symbolRuleAttributes);
    ASTCDMethod deserializeAdditionalAttributesSymbolMethod = createDeserializeAdditionalAttributesSymbolMethod(
        symbolFullName, jsonParam);

    return Lists.newArrayList(deserializeStringMethod, deserializeSymbolMethod,
        deserializeAdditionalAttributesSymbolMethod);
  }

  protected ASTCDMethod createGetSerializedKindMethod(String symbolName) {
    ASTCDMethod getSerializedKindMethod = getCDMethodFacade()
        .createMethod(PUBLIC, getMCTypeFacade().createStringType(), "getSerializedKind");
    this.replaceTemplate(EMPTY_BODY, getSerializedKindMethod,
        new StringHookPoint("return \"" + symbolName + "\";"));
    return getSerializedKindMethod;
  }

  protected ASTCDMethod createSerializeMethod(ASTCDType cdType, String symbolName, String jsonType) {
    ASTCDParameter toSerializeParam = getCDParameterFacade()
        .createParameter(getMCTypeFacade().createQualifiedType(symbolName), "toSerialize");
    ASTCDParameter jsonParam = getCDParameterFacade()
            .createParameter(getMCTypeFacade().createQualifiedType(jsonType), "symbols2Json");
    ASTCDMethod serializeMethod = getCDMethodFacade()
        .createMethod(PUBLIC, getMCTypeFacade().createStringType(), "serialize", toSerializeParam, jsonParam);
    this.replaceTemplate(EMPTY_BODY, serializeMethod,
            new TemplateHookPoint(TEMPLATE_PATH + "Serialize4SymbolDeSer",
                    symbolTableService.hasSymbolSpannedScope(cdType),
                    cdType.getCDAttributeList()));
    return serializeMethod;
  }

  protected ASTCDMethod createDeserializeStringMethod(String symbolFullName, String symbolSimpleName) {
    ASTCDParameter stringParam = getCDParameterFacade()
        .createParameter(getMCTypeFacade().createStringType(), "serialized");
    ASTCDMethod deserializeMethod = getCDMethodFacade()
        .createMethod(PUBLIC, getMCTypeFacade().createQualifiedType(symbolFullName), DESERIALIZE,
            stringParam);
    this.replaceTemplate(EMPTY_BODY, deserializeMethod,
        new TemplateHookPoint(TEMPLATE_PATH + "DeserializeString4SymbolDeSer", symbolSimpleName));
    return deserializeMethod;
  }

  protected ASTCDMethod createDeserializeSymbolMethod(String symbolBuilderFullName,
      String symbolBuilderSimpleName,
      String symTabMill, String symbolFullName, String symbolSimpleName,
      ASTCDParameter jsonParam, List<ASTCDAttribute> symbolRuleAttributes) {
    ASTCDMethod deserializeMethod = getCDMethodFacade()
        .createMethod(PUBLIC, getMCTypeFacade().createQualifiedType(symbolFullName),
            DESERIALIZE + symbolSimpleName, jsonParam);
    this.replaceTemplate(EMPTY_BODY, deserializeMethod,
        new TemplateHookPoint(TEMPLATE_PATH + "DeserializeSymbol4SymbolDeSer",
            symbolBuilderFullName, symbolBuilderSimpleName, symTabMill, symbolFullName,
            symbolSimpleName, symbolRuleAttributes));
    return deserializeMethod;
  }

  protected List<ASTCDMethod> createDeserializeSymbolRuleAttributesMethod(
      List<ASTCDAttribute> attributeList, String deSerName) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDAttribute attr : attributeList) {
      String methodName = DESERIALIZE +
          StringTransformations.capitalize(attr.getName());
      ASTCDParameter jsonParam = getCDParameterFacade()
          .createParameter(getMCTypeFacade().createQualifiedType(JSON_OBJECT), "symbolJson");
      ASTCDMethod method = getCDMethodFacade()
          .createMethod(PUBLIC, attr.getMCType(), methodName , jsonParam);
      Optional<HookPoint> impl = bitser.getDeserialHook(attr.printType(), attr.getName(), "scopeJson");
      if(impl.isPresent()){
        this.replaceTemplate(EMPTY_BODY, method, impl.get());
      }
      else{
        method.getModifier().setAbstract(true);
      }
      methodList.add(method);
    }
    return methodList;
  }

  protected ASTCDMethod createDeserializeAdditionalAttributesSymbolMethod(String symbolFullName,
      ASTCDParameter jsonParam) {
    ASTCDParameter symbolParam = getCDParameterFacade()
        .createParameter(getMCTypeFacade().createQualifiedType(symbolFullName), SYMBOL_VAR);
    String methodName = "deserializeAddons";
    return getCDMethodFacade()
        .createMethod(PROTECTED, methodName, symbolParam, jsonParam);
  }

  protected ASTCDMethod createSerializeAdditionalAttributesSymbolMethod(String symbolFullName,
                                                                        String jsonType) {
    ASTCDParameter symbolParam = getCDParameterFacade()
            .createParameter(getMCTypeFacade().createQualifiedType(symbolFullName), SYMBOL_VAR);
    ASTCDParameter jsonParam = getCDParameterFacade()
            .createParameter(getMCTypeFacade().createQualifiedType(jsonType), "symbols2Json");
    String methodName = "serializeAddons";
    return getCDMethodFacade()
            .createMethod(PROTECTED, methodName, symbolParam, jsonParam);
  }

}
