/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.serialization;

import de.monticore.ast.Comment;
import de.monticore.cdbasis._ast.*;
import de.monticore.cd4codebasis._ast.*;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.io.paths.IterablePath;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.utils.Names;
import de.se_rwth.commons.StringTransformations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.monticore.codegen.cd2java.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;

/**
 * creates a SymbolDeSer class from a grammar
 */
public class SymbolDeSerDecorator extends AbstractCreator<ASTCDType, ASTCDClass> {

  protected static final String SER_TEMPL = "_symboltable.serialization.symbolDeSer.Serialize4SymbolDeSer";

  protected static final String DESER_STR_TEMPL = "_symboltable.serialization.symbolDeSer.DeserializeString4SymbolDeSer";

  protected static final String DESER_SYM_TEMPL = "_symboltable.serialization.symbolDeSer.DeserializeSymbol4SymbolDeSer";

  protected ASTMCType string = getMCTypeFacade().createStringType();

  protected final SymbolTableService symbolTableService;

  protected BITSer bitser;

  protected boolean generateAbstractClass;

  protected IterablePath hw;

  public SymbolDeSerDecorator(final GlobalExtensionManagement glex,
      final SymbolTableService symbolTableService, final IterablePath hw) {
    super(glex);
    this.symbolTableService = symbolTableService;
    this.hw = hw;
    bitser = new BITSer();
    generateAbstractClass = false;
  }

  @Override
  public ASTCDClass decorate(ASTCDType symbolClass) {
    generateAbstractClass = false;
    String className = symbolTableService.getSymbolDeSerSimpleName(symbolClass);
    String symName = symbolTableService.getSymbolFullName(symbolClass);
    String iScopeName = symbolTableService.getScopeInterfaceFullName();
    String millName = symbolTableService.getMillFullName();
    String s2jName = symbolTableService.getSymbols2JsonFullName();
    String deSerName = symbolTableService.getScopeDeSerFullName();

    ASTMCQualifiedType iDeSerType = getMCTypeFacade().
        createQualifiedType(I_SYMBOL_DE_SER + "<" + symName + ", " + s2jName + ">");
    ASTMCQualifiedType symType = getMCTypeFacade().createQualifiedType(symName);

    ASTCDParameter symParam = getCDParameterFacade().createParameter(symType, "toSerialize");
    ASTCDParameter sym2Param = getCDParameterFacade().createParameter(symType, "symbol");
    ASTCDParameter s2jParam = getCDParameterFacade().createParameter(getMCTypeFacade()
        .createQualifiedType(s2jName), "s2j");
    ASTCDParameter jsonParam = getCDParameterFacade().createParameter(getMCTypeFacade()
        .createQualifiedType(JSON_OBJECT), "symbolJson");

    boolean spansScope = symbolTableService.hasSymbolSpannedScope(symbolClass);
    List<ASTCDAttribute> attr = symbolClass.getCDAttributeList();

    ASTCDClass clazz = CD4CodeMill.cDClassBuilder()
        .setName(className)
        .setModifier(PUBLIC.build())
        .addInterface(iDeSerType)
        .addCDMethod(createGetSerializedKindMethod(symName))

        //serialization
        .addCDMethod(createSerializeMethod(symbolClass, symParam, s2jParam, spansScope))
        .addAllCDMethods(createSerializeAttrMethods(attr, s2jParam))
        .addCDMethod(createSerializeAddonsMethod(symParam, s2jParam))

        //deserialization
        .addCDMethod(createDeserializeStringMethod(symType))
        .addCDMethod(
            createDeserializeJsonMethod(symType, millName, symName, jsonParam, attr, spansScope, iScopeName, deSerName))
        .addAllCDMethods(createDeserializeAttrMethods(attr, jsonParam))
        .addCDMethod(createDeserializeAddons(sym2Param, jsonParam))

        .build();

    if (generateAbstractClass) {
      clazz.getModifier().setAbstract(true);
      if (!TransformationHelper.existsHandwrittenClass(hw, symbolTableService.getSymbolDeSerFullName(symbolClass))) {
        AbstractDeSers.add(symbolTableService.getSymbolDeSerFullName(symbolClass));
      }
    }
    return clazz;
  }

  protected ASTCDMethod createGetSerializedKindMethod(String symbolName) {
    ASTCDMethod getSerializedKindMethod = getCDMethodFacade()
        .createMethod(PUBLIC, string, "getSerializedKind");
    this.replaceTemplate(EMPTY_BODY, getSerializedKindMethod,
        new StringHookPoint("return \"" + symbolName + "\";"));
    return getSerializedKindMethod;
  }

  ////////////////////////////// SERIALIZATON //////////////////////////////////////////////////////

  protected ASTCDMethod createSerializeMethod(ASTCDType cdType, ASTCDParameter symParam,
      ASTCDParameter s2jParam, boolean spansScope) {
    ASTCDMethod method = getCDMethodFacade()
        .createMethod(PUBLIC, string, "serialize", symParam, s2jParam);
    HookPoint hp = new TemplateHookPoint(SER_TEMPL, spansScope, cdType.getCDAttributeList());
    this.replaceTemplate(EMPTY_BODY, method, hp);
    return method;
  }

  protected List<ASTCDMethod> createSerializeAttrMethods(
      List<ASTCDAttribute> attributes, ASTCDParameter s2j) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDAttribute attr : attributes) {
      String methodName = "serialize" + StringTransformations.capitalize(attr.getName());
      ASTCDParameter toSerialize = getCDParameterFacade()
          .createParameter(attr.getMCType(), attr.getName());
      ASTCDMethod method = getCDMethodFacade()
          .createMethod(PROTECTED, methodName, toSerialize, s2j);

      // Check whether built-in serialization exists. If yes, use it and otherwise make method abstract
      Optional<HookPoint> impl = bitser.getSerialHook(attr.printType(), attr.getName());
      if (impl.isPresent()) {
        this.replaceTemplate(EMPTY_BODY, method, impl.get());
      }
      else {
        makeMethodAbstract(method, attr);
      }
      methodList.add(method);
    }
    return methodList;
  }

  protected ASTCDMethod createSerializeAddonsMethod(ASTCDParameter symbol, ASTCDParameter json) {
    return getCDMethodFacade().createMethod(PROTECTED, "serializeAddons", symbol, json);
  }

  ////////////////////////////// DESERIALIZATON ////////////////////////////////////////////////////

  protected ASTCDMethod createDeserializeStringMethod(ASTMCQualifiedType symType) {
    ASTCDParameter stringParam = getCDParameterFacade().createParameter(string, "serialized");
    ASTCDMethod deserializeMethod = getCDMethodFacade()
        .createMethod(PUBLIC, symType, DESERIALIZE, stringParam);
    this.replaceTemplate(EMPTY_BODY, deserializeMethod, new TemplateHookPoint(DESER_STR_TEMPL));
    return deserializeMethod;
  }

  protected ASTCDMethod createDeserializeJsonMethod(ASTMCQualifiedType type, String symTabMill,
      String symbolFullName, ASTCDParameter jsonParam,
      List<ASTCDAttribute> symbolRuleAttributes, boolean spansScope, String scopeName,
      String deSerFullName) {
    ASTCDMethod deserializeMethod = getCDMethodFacade()
        .createMethod(PUBLIC, type, DESERIALIZE, jsonParam);
    this.replaceTemplate(EMPTY_BODY, deserializeMethod,
        new TemplateHookPoint(DESER_SYM_TEMPL, symTabMill, symbolFullName,
            Names.getSimpleName(symbolFullName), symbolRuleAttributes, spansScope, scopeName, deSerFullName));
    return deserializeMethod;
  }

  protected List<ASTCDMethod> createDeserializeAttrMethods(
      List<ASTCDAttribute> attributeList, ASTCDParameter scopeJsonParam) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDAttribute attr : attributeList) {
      String methodName = DESERIALIZE + StringTransformations.capitalize(attr.getName());
      ASTCDMethod method = getCDMethodFacade()
          .createMethod(PROTECTED, attr.getMCType(), methodName, scopeJsonParam);

      // Check whether built-in serialization exists. If yes, use it and otherwise make method abstract
      Optional<HookPoint> impl = bitser
          .getDeserialHook(attr.printType(), attr.getName(), "symbolJson");
      if (impl.isPresent()) {
        this.replaceTemplate(EMPTY_BODY, method, impl.get());
      }
      else {
        makeMethodAbstract(method, attr);
      }
      methodList.add(method);
    }
    return methodList;
  }

  protected ASTCDMethod createDeserializeAddons(ASTCDParameter symbol, ASTCDParameter json) {
    return getCDMethodFacade().createMethod(PROTECTED, "deserializeAddons", symbol, json);
  }

  //////////////////////////////// internal calculations //////////////////////////////////

  private void makeMethodAbstract(ASTCDMethod method, ASTCDAttribute attr) {
    generateAbstractClass = true;
    method.getModifier().setAbstract(true);
    method.add_PreComment(new Comment("  /**\n"
        + "   * Extend the class with the TOP mechanism and implement this method to realize a serialization \n"
        + "   * strategy for the attribute '" + attr.getName() + "'\n"
        + "   * of type '" + attr.printType() + "'!\n"
        + "   */"));
  }

}
