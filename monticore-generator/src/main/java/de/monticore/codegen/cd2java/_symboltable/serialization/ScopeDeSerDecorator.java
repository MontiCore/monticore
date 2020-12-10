/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.serialization;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import de.monticore.ast.Comment;
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
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.StringTransformations;

import java.util.*;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;

/**
 * creates a ScopeDeSer class from a grammar
 */
public class ScopeDeSerDecorator extends AbstractDecorator {

  //The following is a complete list of templates used by this decorator:
  public static final String DESERIALIZE_AS_TEMPL = "_symboltable.serialization.scopeDeSer.DeserializeArtifactScope";

  public static final String DESERIALIZE_S_TEMPL = "_symboltable.serialization.scopeDeSer.DeserializeScope";

  public static final String DESERIALIZE_TEMPL = "_symboltable.serialization.scopeDeSer.DeserializeString4ScopeDeSer";

  public static final String DESERIALIZE_SYMBOLS_TEMPL = "_symboltable.serialization.scopeDeSer.DeserializeSymbols";

  public static final String SERIALIZE_TEMPL = "_symboltable.serialization.scopeDeSer.Serialize4ScopeDeSer";

  public static final String SERIALIZE_AS_TEMPL = "_symboltable.serialization.scopeDeSer.SerializeAS4ScopeDeSer";

  protected final SymbolTableService symbolTableService;

  protected MethodDecorator methodDecorator;

  protected VisitorService visitorService;

  protected BITSer bitser;

  protected boolean generateAbstractClass;

  public ScopeDeSerDecorator(final GlobalExtensionManagement glex,
      final SymbolTableService symbolTableService, final MethodDecorator methodDecorator,
      VisitorService visitorService) {
    super(glex);
    this.visitorService = visitorService;
    this.symbolTableService = symbolTableService;
    this.methodDecorator = methodDecorator;
    this.bitser = new BITSer();
    this.generateAbstractClass = false;
  }

  /**
   * @param scopeInput for scopeRule attributes and methods
   */
  public ASTCDClass decorate(ASTCDCompilationUnit scopeInput, ASTCDCompilationUnit symbolInput) {
    String scopeDeSerName = symbolTableService.getScopeDeSerSimpleName();
    String symbols2JsonName = symbolTableService.getSymbols2JsonFullName();
    String scopeInterfaceName = symbolTableService.getScopeInterfaceFullName();
    String asInterfaceName = symbolTableService.getArtifactScopeInterfaceFullName();
    String scopeClassName = symbolTableService.getScopeInterfaceFullName();
    String millName = symbolTableService.getMillFullName();

    ASTMCQualifiedType interfaceName = getMCTypeFacade().
        createQualifiedType(I_DE_SER + "<" + asInterfaceName + ", " + symbols2JsonName + ">");

    ASTCDParameter scopeParam = getCDParameterFacade().createParameter(getMCTypeFacade()
        .createQualifiedType(scopeInterfaceName), "toSerialize");
    ASTCDParameter scopeVarParam = getCDParameterFacade()
        .createParameter(symbolTableService.getScopeInterfaceType(), SCOPE_VAR);
    ASTCDParameter asParam = getCDParameterFacade().createParameter(getMCTypeFacade()
        .createQualifiedType(asInterfaceName), "toSerialize");
    ASTCDParameter s2jParam = getCDParameterFacade().createParameter(getMCTypeFacade()
        .createQualifiedType(symbols2JsonName), "s2j");
    ASTCDParameter scopeJsonParam = getCDParameterFacade()
        .createParameter(getMCTypeFacade().createQualifiedType(JSON_OBJECT), SCOPE_JSON_VAR);

    // list of all scope rule attributes
    List<ASTCDAttribute> scopeRuleAttrList = scopeInput.deepClone().getCDDefinition()
        .getCDClassList()
        .stream()
        .map(ASTCDClass::getCDAttributeList)
        .flatMap(List::stream)
        .collect(Collectors.toList());
    scopeRuleAttrList.forEach(a -> getDecorationHelper().addAttributeDefaultValues(a, this.glex));

    List<String> attrNameList = scopeRuleAttrList.stream().map(a -> a.getName()).collect(Collectors.toList());;

    // map with all symbol kinds  available in this scope
    Map<String, Boolean> symbolMap = createSymbolMap(symbolInput.getCDDefinition());

    ASTCDClass clazz = CD4CodeMill.cDClassBuilder()
        .setName(scopeDeSerName)
        .setModifier(PUBLIC.build())
        .addInterface(interfaceName)

        // add serialization methods
        .addCDMethod(createSerializeMethod(scopeParam, s2jParam, attrNameList))
        .addCDMethod(createSerializeASMethod(asParam, s2jParam, attrNameList))
        .addAllCDMethods(createSerializeAttrMethod(scopeRuleAttrList, scopeParam, s2jParam))
        .addCDMethod(createSerializeAddonsMethod(scopeParam, s2jParam))
        .addCDMethod(createSerializeAddonsMethod(asParam, s2jParam))

        // add deserialization methods
        .addCDMethod(createDeserializeStringMethod(asInterfaceName))
        .addCDMethod(createDeserializeScopeMethod(scopeClassName, millName, scopeJsonParam,
            scopeRuleAttrList))
        .addCDMethod(createDeserializeArtifactScopeMethod(asInterfaceName, millName, scopeJsonParam,
            scopeRuleAttrList))
        .addCDMethod(
            createDeserializeSymbolsMethods(scopeVarParam, scopeJsonParam, symbolMap, millName))
        .addAllCDMethods(createDeserializeAttrMethod(scopeRuleAttrList, scopeJsonParam))
        .addAllCDMethods(createDeserializeAddonsMethods(scopeVarParam, scopeJsonParam))
        .build();
    if(generateAbstractClass){
      clazz.getModifier().setAbstract(true);
    }
    return clazz;
  }

  ////////////////////////////// SERIALIZATON //////////////////////////////////////////////////////
  protected ASTCDMethod createSerializeMethod(ASTCDParameter toSerialize, ASTCDParameter s2j,
      List<String> scopeRuleAttrList) {
    ASTCDMethod method = getCDMethodFacade()
        .createMethod(PUBLIC, getMCTypeFacade().createStringType(), "serialize", toSerialize, s2j);
    this.replaceTemplate(EMPTY_BODY, method,
        new TemplateHookPoint(SERIALIZE_TEMPL, scopeRuleAttrList));
    return method;
  }

  protected ASTCDMethod createSerializeASMethod(ASTCDParameter toSerialize, ASTCDParameter s2j,
      List<String> scopeRuleAttrList) {
    ASTCDMethod method = getCDMethodFacade()
        .createMethod(PUBLIC, getMCTypeFacade().createStringType(), "serialize", toSerialize, s2j);
    this.replaceTemplate(EMPTY_BODY, method,
        new TemplateHookPoint(SERIALIZE_AS_TEMPL, scopeRuleAttrList));
    return method;
  }

  protected ASTCDMethod createSerializeAddonsMethod(ASTCDParameter toSerialize,
      ASTCDParameter s2j) {
    return getCDMethodFacade().createMethod(PROTECTED, "serializeAddons", toSerialize, s2j);
  }

  protected List<ASTCDMethod> createSerializeAttrMethod(
      List<ASTCDAttribute> attributeList, ASTCDParameter toSerialize, ASTCDParameter s2j) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDAttribute attr : attributeList) {
      String methodName = "serialize" + StringTransformations.capitalize(attr.getName());
      ASTCDMethod method = getCDMethodFacade().createMethod(PROTECTED, methodName, toSerialize, s2j);

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

  ////////////////////////////// DESERIALIZATON ////////////////////////////////////////////////////

  protected ASTCDMethod createDeserializeStringMethod(String artifactScopeInterfaceName) {
    ASTCDParameter stringParam = getCDParameterFacade()
        .createParameter(getMCTypeFacade().createStringType(), "serialized");
    ASTCDMethod method = getCDMethodFacade()
        .createMethod(PUBLIC, getMCTypeFacade().createQualifiedType(artifactScopeInterfaceName),
            DESERIALIZE,
            stringParam);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(DESERIALIZE_TEMPL));
    return method;
  }

  protected ASTCDMethod createDeserializeScopeMethod(String scopeClassName,
      String symTabMill, ASTCDParameter jsonParam, List<ASTCDAttribute> scopeRuleAttributes) {
    ASTCDMethod method = getCDMethodFacade()
        .createMethod(PROTECTED, getMCTypeFacade().createQualifiedType(scopeClassName),
            DESERIALIZE + SCOPE_SUFFIX, jsonParam);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(
        DESERIALIZE_S_TEMPL, symTabMill, scopeClassName, scopeRuleAttributes));
    return method;
  }

  protected ASTCDMethod createDeserializeArtifactScopeMethod(String artifactScopeName,
      String symTabMill, ASTCDParameter jsonParam, List<ASTCDAttribute> scopeRuleAttributes) {
    ASTCDMethod method = getCDMethodFacade()
        .createMethod(PROTECTED, getMCTypeFacade().createQualifiedType(artifactScopeName),
            DESERIALIZE + ARTIFACT_PREFIX + SCOPE_SUFFIX, jsonParam);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(
        DESERIALIZE_AS_TEMPL, symTabMill, artifactScopeName, scopeRuleAttributes));
    return method;
  }

  protected List<ASTCDMethod> createDeserializeAddonsMethods(ASTCDParameter scopeParam,
      ASTCDParameter jsonParam) {
    ASTCDMethod method = getCDMethodFacade()
        .createMethod(PROTECTED, "deserializeAddons", scopeParam, jsonParam);

    ASTCDParameter asParam = getCDParameterFacade()
        .createParameter(symbolTableService.getArtifactScopeInterfaceType(), SCOPE_VAR);
    ASTCDMethod asMethod = getCDMethodFacade()
        .createMethod(PROTECTED, "deserializeAddons", asParam, jsonParam);
    return Lists.newArrayList(method, asMethod);
  }

  protected ASTCDMethod createDeserializeSymbolsMethods(ASTCDParameter scopeParam,
      ASTCDParameter jsonParam, Map<String, Boolean> symbolMap, String millName) {

    ASTCDMethod method = getCDMethodFacade()
        .createMethod(PROTECTED, "deserializeSymbols", scopeParam, jsonParam);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(
        DESERIALIZE_SYMBOLS_TEMPL, symbolMap, millName));
    return method;
  }

  protected List<ASTCDMethod> createDeserializeAttrMethod(
      List<ASTCDAttribute> attributeList, ASTCDParameter scopeJsonParam) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDAttribute attr : attributeList) {
      String methodName = DESERIALIZE + StringTransformations.capitalize(attr.getName());
      ASTCDMethod method = getCDMethodFacade()
          .createMethod(PROTECTED, attr.getMCType(), methodName, scopeJsonParam);

      // Check whether built-in serialization exists. If yes, use it and otherwise make method abstract
      Optional<HookPoint> impl = bitser
          .getDeserialHook(attr.printType(), attr.getName(), "scopeJson");
      if (impl.isPresent()) {
        this.replaceTemplate(EMPTY_BODY, method, impl.get());
      }
      else {
        makeMethodAbstract(method,attr);
      }
      methodList.add(method);
    }
    return methodList;
  }

  //////////////////////////////// internal calculations //////////////////////////////////

  /**
   * Calculate a map from qualified symbol name to a boolean indicating whether the symbol spans a
   * scope
   *
   * @param symbolInput
   * @return
   */
  private Map<String, Boolean> createSymbolMap(ASTCDDefinition symbolInput) {
    Map<String, Boolean> symbolMap = new HashMap<>();

    //add local symbols
    for (ASTCDType prod : symbolTableService.getSymbolDefiningProds(symbolInput)) {
      String name = symbolTableService.getSymbolFullName(prod);
      boolean spansScope = symbolTableService.hasScopeStereotype(prod.getModifier());
      symbolMap.put(name, spansScope);
    }

    //add symbols from super grammars
    for (CDDefinitionSymbol cdDefinitionSymbol : symbolTableService.getSuperCDsTransitive()) {
      for (CDTypeSymbol type : cdDefinitionSymbol.getTypes()) {
        if (type.isPresentAstNode() && type.getAstNode().isPresentModifier()
            && symbolTableService.hasSymbolStereotype(type.getAstNode().getModifier())) {
          String name = symbolTableService
              .getSymbolFullName(type.getAstNode(), cdDefinitionSymbol);
          boolean spansScope = symbolTableService
              .hasScopeStereotype(type.getAstNode().getModifier());
          symbolMap.put(name, spansScope);
        }
      }
    }
    //sort the map based on the alphabetical order of keys, to always generate the same order of methods
    return symbolMap.entrySet().stream()
        .sorted(Ordering.natural().onResultOf(a -> a.getKey())).collect(Collectors
            .toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
  }

  private void makeMethodAbstract(ASTCDMethod method, ASTCDAttribute attr) {
    generateAbstractClass = true;
    method.getModifier().setAbstract(true);
    method.add_PreComment(new Comment("  /**\n"
        + "   * Extend the class with the TOP mechanism and implement this method to realize a serialization \n"
        + "   * strategy for the attribute '\"+attr.getName()+\"'\n"
        + "   * of type '\"+attr.printType()+\"'!\n"
        + "   */" ));
  }

}
