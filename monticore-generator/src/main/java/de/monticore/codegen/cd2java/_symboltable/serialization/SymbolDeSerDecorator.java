/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.serialization;

import de.monticore.ast.Comment;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.io.paths.MCPath;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.StringTransformations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;
import static de.monticore.generating.GeneratorEngine.existsHandwrittenClass;

/**
 * creates a SymbolDeSer class from a grammar
 */
public class SymbolDeSerDecorator extends AbstractCreator<ASTCDType, ASTCDClass> {

  protected static final String SER_TEMPL = "_symboltable.serialization.symbolDeSer.Serialize4SymbolDeSer";

  protected static final String DESER_TEMPL = "_symboltable.serialization.symbolDeSer.Deserialize4SymbolDeSer";

  protected static final String DESER_IS_TEMPL = "_symboltable.serialization.symbolDeSer.DeserializeIScope4SymbolDeSer";

  protected ASTMCType string = getMCTypeFacade().createStringType();

  protected final SymbolTableService symbolTableService;

  protected BITSer bitser;

  protected boolean generateAbstractClass;

  protected MCPath hw;

  public SymbolDeSerDecorator(final GlobalExtensionManagement glex,
      final SymbolTableService symbolTableService, final MCPath hw) {
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
    ASTCDParameter scopeParam = getCDParameterFacade().createParameter(
        getMCTypeFacade().createQualifiedType(iScopeName), "scope");

    boolean spansScope = symbolTableService.hasSymbolSpannedScope(symbolClass);
    List<ASTCDAttribute> attr = symbolClass.getCDAttributeList();

    ASTCDClass clazz = CD4CodeMill.cDClassBuilder()
        .setName(className)
        .setModifier(PUBLIC.build())
        .setCDInterfaceUsage(CD4CodeMill.cDInterfaceUsageBuilder().addInterface(iDeSerType).build())
        .addCDMember(createGetSerializedKindMethod(symName))

        //serialization
        .addCDMember(createSerializeMethod(symbolClass, symParam, s2jParam, spansScope))
        .addAllCDMembers(createSerializeAttrMethods(attr, s2jParam))
        .addCDMember(createSerializeAddonsMethod(symParam, s2jParam))

        //deserialization
        .addCDMember(createDeserializeMethod(symType, millName, symName, scopeParam, jsonParam, attr,
            spansScope, iScopeName, deSerName))
        .addCDMember(createDeserializeMethodWrapper(symType, millName, symName, scopeParam, jsonParam, attr,
            spansScope, iScopeName, deSerName))
        .addCDMember(createDeserializeMethodWrapper2(symType, millName, symName, scopeParam, jsonParam, attr,
            spansScope, iScopeName, deSerName))
        .addAllCDMembers(createDeserializeAttrMethods(attr, jsonParam, scopeParam))
        .addCDMember(createDeserializeAddons(sym2Param, jsonParam))

        .build();

    if (generateAbstractClass) {
      clazz.getModifier().setAbstract(true);
      if (!existsHandwrittenClass(hw, symbolTableService.getSymbolDeSerFullName(symbolClass))) {
        AbstractDeSers.add(symbolTableService.getSymbolDeSerFullName(symbolClass));
      }
    }
    CD4C.getInstance().addImport(clazz, "de.monticore.symboltable.*");
    return clazz;
  }

  protected ASTCDMethod createGetSerializedKindMethod(String symbolName) {
    ASTCDMethod getSerializedKindMethod = getCDMethodFacade()
        .createMethod(PUBLIC.build(), string, "getSerializedKind");
    this.replaceTemplate(EMPTY_BODY, getSerializedKindMethod,
        new StringHookPoint("return \"" + symbolName + "\";"));
    return getSerializedKindMethod;
  }

  ////////////////////////////// SERIALIZATON //////////////////////////////////////////////////////

  protected ASTCDMethod createSerializeMethod(ASTCDType cdType, ASTCDParameter symParam,
      ASTCDParameter s2jParam, boolean spansScope) {
    ASTCDMethod method = getCDMethodFacade()
        .createMethod(PUBLIC.build(), string, "serialize", symParam, s2jParam);
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
          .createMethod(PROTECTED.build(), methodName, toSerialize, s2j);

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
    return getCDMethodFacade().createMethod(PROTECTED.build(), "serializeAddons", symbol, json);
  }

  ////////////////////////////// DESERIALIZATON ////////////////////////////////////////////////////

  protected ASTCDMethod createDeserializeMethod(ASTMCQualifiedType type, String symTabMill,
      String symbolFullName, ASTCDParameter scopeParam, ASTCDParameter jsonParam,
      List<ASTCDAttribute> symbolRuleAttributes, boolean spansScope, String scopeName,
      String deSerFullName) {
    ASTCDMethod deserializeMethod = getCDMethodFacade()
        .createMethod(PUBLIC.build(), type, DESERIALIZE, scopeParam, jsonParam);
    this.replaceTemplate(EMPTY_BODY, deserializeMethod,
        new TemplateHookPoint(DESER_TEMPL, symTabMill, symbolFullName,
            Names.getSimpleName(symbolFullName), symbolRuleAttributes, spansScope, scopeName, deSerFullName));
    return deserializeMethod;
  }

  protected ASTCDMethod createDeserializeMethodWrapper(ASTMCQualifiedType type, String symTabMill,
      String symbolFullName, ASTCDParameter scopeParam, ASTCDParameter jsonParam,
      List<ASTCDAttribute> symbolRuleAttributes, boolean spansScope, String scopeName,
      String deSerFullName) {
    ASTCDMethod deserializeMethod = getCDMethodFacade()
        .createMethod(PUBLIC.build(), type, DESERIALIZE, jsonParam);
    this.replaceTemplate(EMPTY_BODY, deserializeMethod, new StringHookPoint(
        "return this." + DESERIALIZE + "(null, " + jsonParam.getName() + ");"
    ));
    return deserializeMethod;
  }

  protected ASTCDMethod createDeserializeMethodWrapper2(ASTMCQualifiedType type, String symTabMill,
      String symbolFullName, ASTCDParameter scopeParam, ASTCDParameter jsonParam,
      List<ASTCDAttribute> symbolRuleAttributes, boolean spansScope, String scopeName,
      String deSerFullName) {
    ASTCDParameter iScopeParam = getCDParameterFacade().createParameter(getMCTypeFacade()
        .createQualifiedType(I_SCOPE), "enclosingScope");
    ASTCDMethod deserializeMethod = getCDMethodFacade()
        .createMethod(PUBLIC.build(), type, DESERIALIZE, iScopeParam, jsonParam);
    String errorCode = symbolTableService.getGeneratedErrorCode(DESERIALIZE);
    this.replaceTemplate(EMPTY_BODY, deserializeMethod,
        new TemplateHookPoint(DESER_IS_TEMPL, scopeParam.getMCType().printType(), errorCode));
    return deserializeMethod;
  }

  protected List<ASTCDMethod> createDeserializeAttrMethods(
      List<ASTCDAttribute> attributeList, ASTCDParameter scopeJsonParam, ASTCDParameter scopeParam) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDAttribute attr : attributeList) {
      String methodName = DESERIALIZE + StringTransformations.capitalize(attr.getName());
      ASTCDMethod method = getCDMethodFacade()
          .createMethod(PROTECTED.build(), attr.getMCType(), methodName, scopeParam, scopeJsonParam);
      // create wrapper function offering the deprecated interface
      // this one does not take the enclosing scope
      ASTCDMethod wrapperMethod = getCDMethodFacade()
          .createMethod(PROTECTED.build(), attr.getMCType(), methodName, scopeJsonParam);

      // Check whether built-in serialization exists. If yes, use it and otherwise make method abstract
      Optional<HookPoint> impl = bitser
          .getDeserialHook(attr.printType(), attr.getName(), "symbolJson");
      if (impl.isPresent()) {
        this.replaceTemplate(EMPTY_BODY, method, impl.get());
        String deprecatedWrapperImpl = "return this." + methodName +
            "(null, " + scopeJsonParam.getName() + ");";
        this.replaceTemplate(EMPTY_BODY, wrapperMethod,
            new StringHookPoint(deprecatedWrapperImpl));
      }
      else {
        // keep the original behavior:
        // an abstract method without a scope parameter is created
        makeMethodAbstract(wrapperMethod, attr);
        String deprecatedImpl = "return this." + methodName +
            "(" + scopeJsonParam.getName() + ");";
        this.replaceTemplate(EMPTY_BODY, method,
            new StringHookPoint(deprecatedImpl));
      }
      methodList.add(wrapperMethod);
      methodList.add(method);
    }
    return methodList;
  }

  protected ASTCDMethod createDeserializeAddons(ASTCDParameter symbol, ASTCDParameter json) {
    return getCDMethodFacade().createMethod(PROTECTED.build(), "deserializeAddons", symbol, json);
  }

  //////////////////////////////// internal calculations //////////////////////////////////

  protected void makeMethodAbstract(ASTCDMethod method, ASTCDAttribute attr) {
    generateAbstractClass = true;
    method.getModifier().setAbstract(true);
    method.add_PreComment(new Comment("  /**\n"
        + "   * Extend the class with the TOP mechanism and implement this method to realize a serialization \n"
        + "   * strategy for the attribute '" + attr.getName() + "'\n"
        + "   * of type '" + attr.printType() + "'!\n"
        + "   */"));
  }

}
