/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.serialization;

import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import de.monticore.ast.Comment;
import de.monticore.cd.methodtemplates.CD4C;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cd4codebasis._ast.ASTCDParameter;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.io.paths.MCPath;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.se_rwth.commons.StringTransformations;

import java.util.*;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;
import static de.monticore.generating.GeneratorEngine.existsHandwrittenClass;

/**
 * creates a ScopeDeSer class from a grammar
 */
public class ScopeDeSerDecorator extends AbstractDecorator {

  //The following is a complete list of templates used by this decorator:
  public static final String DESERIALIZE_AS_TEMPL = "_symboltable.serialization.scopeDeSer.DeserializeArtifactScope";

  public static final String DESERIALIZE_S_TEMPL = "_symboltable.serialization.scopeDeSer.DeserializeScope";

  public static final String DESERIALIZE_IS_TEMPL = "_symboltable.serialization.scopeDeSer.DeserializeIScope";

  public static final String DESERIALIZE_SYMBOLS_TEMPL = "_symboltable.serialization.scopeDeSer.DeserializeSymbols";

  public static final String SERIALIZES2J_TEMPL = "_symboltable.serialization.scopeDeSer.SerializeS2J4ScopeDeSer";

  public static final String SERIALIZE_AS_TEMPL = "_symboltable.serialization.scopeDeSer.SerializeAS4ScopeDeSer";

  protected final SymbolTableService symbolTableService;

  protected MethodDecorator methodDecorator;

  protected VisitorService visitorService;

  protected BITSer bitser;

  protected boolean generateAbstractClass;

  protected MCPath hw;

  public ScopeDeSerDecorator(final GlobalExtensionManagement glex,
      final SymbolTableService symbolTableService, final MethodDecorator methodDecorator,
      VisitorService visitorService, final MCPath hw) {
    super(glex);
    this.visitorService = visitorService;
    this.symbolTableService = symbolTableService;
    this.methodDecorator = methodDecorator;
    this.bitser = new BITSer();
    this.generateAbstractClass = false;
    this.hw = hw;
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
        createQualifiedType(I_DE_SER + "<"  + scopeInterfaceName + ", " +
            asInterfaceName + ", " + symbols2JsonName + ">");

    ASTCDParameter scopeParam = getCDParameterFacade().createParameter(getMCTypeFacade()
        .createQualifiedType(scopeInterfaceName), "toSerialize");
    ASTCDParameter enclosingScopeParam = getCDParameterFacade().createParameter(getMCTypeFacade()
        .createQualifiedType(scopeInterfaceName), "enclosingScope");
    ASTCDParameter scopeVarParam = getCDParameterFacade()
        .createParameter(symbolTableService.getScopeInterfaceType(), SCOPE_VAR);
    ASTCDParameter asParam = getCDParameterFacade().createParameter(getMCTypeFacade()
        .createQualifiedType(asInterfaceName), "toSerialize");
    ASTCDParameter s2jParam = getCDParameterFacade().createParameter(getMCTypeFacade()
        .createQualifiedType(symbols2JsonName), "s2j");
    ASTCDParameter scopeJsonParam = getCDParameterFacade()
        .createParameter(getMCTypeFacade().createQualifiedType(JSON_OBJECT), SCOPE_JSON_VAR);

    // list of all scope rule attributes
    List<ASTCDAttribute> scopeRuleAttrList = scopeInput.getCDDefinition()
            .getCDClassesList()
            .stream()
            .map(ASTCDClass::getCDAttributeList)
            .flatMap(List::stream)
            .map(a -> a.deepClone())
            .collect(Collectors.toList());
    scopeRuleAttrList.forEach(a -> getDecorationHelper().addAttributeDefaultValues(a, this.glex));

    // map with all symbol kinds  available in this scope
    Map<String, Boolean> symbolMap = createSymbolMap(symbolInput.getCDDefinition());

    ASTCDClass clazz = CD4CodeMill.cDClassBuilder()
        .setName(scopeDeSerName)
        .setModifier(PUBLIC.build())
        .setCDInterfaceUsage(CD4CodeMill.cDInterfaceUsageBuilder().addInterface(interfaceName).build())

        // add serialization methods
        .addCDMember(createSerializeMethod(scopeParam, s2jParam, scopeRuleAttrList))
        .addCDMember(createSerializeASMethod(asParam, s2jParam, scopeRuleAttrList))
        .addAllCDMembers(createSerializeAttrMethods(scopeRuleAttrList, s2jParam))
        .addCDMember(createSerializeAddonsMethod(scopeParam, s2jParam))
        .addCDMember(createSerializeAddonsMethod(asParam, s2jParam))

        // add deserialization methods
        .addCDMember(createDeserializeScopeMethod(scopeClassName, millName, scopeJsonParam,
            scopeRuleAttrList))
        .addCDMember(createDeserializeArtifactScopeMethod(asInterfaceName, millName, scopeJsonParam,
            scopeRuleAttrList))
        .addCDMember(
            createDeserializeSymbolsMethods(scopeVarParam, scopeJsonParam, symbolMap, millName, scopeDeSerName, scopeInterfaceName))
        .addAllCDMembers(createDeserializeAttrMethods(scopeRuleAttrList, enclosingScopeParam, scopeJsonParam))
        .addAllCDMembers(createDeserializeAddonsMethods(scopeVarParam, scopeJsonParam))
        .build();
    if(generateAbstractClass){
      clazz.getModifier().setAbstract(true);
      if (!existsHandwrittenClass(hw, symbolTableService.getScopeDeSerFullName())) {
        AbstractDeSers.add(symbolTableService.getScopeDeSerFullName());
      }
    }
    CD4C.getInstance().addImport(clazz, "de.monticore.symboltable.*");
    CD4C.getInstance().addImport(clazz, "de.monticore.symboltable.serialization.*");
    return clazz;
  }

  ////////////////////////////// SERIALIZATION /////////////////////////////////////////////////////

  protected ASTCDMethod createSerializeMethod(ASTCDParameter toSerialize, ASTCDParameter s2j,
      List<ASTCDAttribute> scopeRuleAttrList) {
    ASTCDMethod method = getCDMethodFacade()
        .createMethod(PUBLIC.build(), getMCTypeFacade().createStringType(), "serialize", toSerialize, s2j);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(SERIALIZES2J_TEMPL, scopeRuleAttrList));
    return method;
  }

  protected ASTCDMethod createSerializeASMethod(ASTCDParameter toSerialize, ASTCDParameter s2j,
      List<ASTCDAttribute> scopeRuleAttrList) {
    ASTCDMethod method = getCDMethodFacade()
        .createMethod(PUBLIC.build(), getMCTypeFacade().createStringType(), "serialize", toSerialize, s2j);
    this.replaceTemplate(EMPTY_BODY, method,
        new TemplateHookPoint(SERIALIZE_AS_TEMPL, scopeRuleAttrList));
    return method;
  }

  protected ASTCDMethod createSerializeAddonsMethod(ASTCDParameter toSer, ASTCDParameter s2j) {
    return getCDMethodFacade().createMethod(PUBLIC.build(), "serializeAddons", toSer, s2j);
  }

  protected List<ASTCDMethod> createSerializeAttrMethods(
      List<ASTCDAttribute> attributeList, ASTCDParameter s2j) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDAttribute attr : attributeList) {
      String methodName = "serialize" + StringTransformations.capitalize(attr.getName());
      ASTCDParameter toSerialize = getCDParameterFacade()
          .createParameter(attr.getMCType(), attr.getName());
      ASTCDMethod method = getCDMethodFacade().createMethod(PROTECTED.build(), methodName, toSerialize, s2j);

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

  ////////////////////////////// DESERIALIZATION ///////////////////////////////////////////////////

  protected ASTCDMethod createDeserializeScopeMethod(String scopeClassName,
      String symTabMill, ASTCDParameter jsonParam, List<ASTCDAttribute> scopeRuleAttributes) {
    ASTCDMethod method = getCDMethodFacade()
        .createMethod(PUBLIC.build(), getMCTypeFacade().createQualifiedType(scopeClassName),
            DESERIALIZE + SCOPE_SUFFIX, jsonParam);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(
        DESERIALIZE_S_TEMPL, symTabMill, scopeClassName, scopeRuleAttributes));
    return method;
  }

  protected ASTCDMethod createDeserializeArtifactScopeMethod(String artifactScopeName,
      String symTabMill, ASTCDParameter jsonParam, List<ASTCDAttribute> scopeRuleAttributes) {
    ASTCDMethod method = getCDMethodFacade()
        .createMethod(PUBLIC.build(), getMCTypeFacade().createQualifiedType(artifactScopeName),
            DESERIALIZE + ARTIFACT_PREFIX + SCOPE_SUFFIX, jsonParam);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(
        DESERIALIZE_AS_TEMPL, symTabMill, artifactScopeName, scopeRuleAttributes));
    return method;
  }

  protected List<ASTCDMethod> createDeserializeAddonsMethods(ASTCDParameter scopeParam,
      ASTCDParameter jsonParam) {
    ASTCDMethod method = getCDMethodFacade()
        .createMethod(PUBLIC.build(), "deserializeAddons", scopeParam, jsonParam);

    ASTCDParameter asParam = getCDParameterFacade()
        .createParameter(symbolTableService.getArtifactScopeInterfaceType(), SCOPE_VAR);
    ASTCDMethod asMethod = getCDMethodFacade()
        .createMethod(PUBLIC.build(), "deserializeAddons", asParam, jsonParam);
    return Lists.newArrayList(method, asMethod);
  }

  protected ASTCDMethod createDeserializeSymbolsMethods(ASTCDParameter scopeParam,
      ASTCDParameter jsonParam, Map<String, Boolean> symbolMap, String millName,
      String scopeDeSerName, String scopeInterfaceName) {
    ASTCDMethod method = getCDMethodFacade()
        .createMethod(PROTECTED.build(), "deserializeSymbols", scopeParam, jsonParam);
    String errorCode = symbolTableService.getGeneratedErrorCode("deserializeSymbols"+millName);
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(
        DESERIALIZE_SYMBOLS_TEMPL, symbolMap, millName, errorCode, scopeDeSerName, scopeInterfaceName));
    return method;
  }

  protected List<ASTCDMethod> createDeserializeAttrMethods(
      List<ASTCDAttribute> attributeList, ASTCDParameter scopeParam, ASTCDParameter scopeJsonParam) {
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDAttribute attr : attributeList) {
      String methodName = DESERIALIZE + StringTransformations.capitalize(attr.getName());
      ASTCDMethod method = getCDMethodFacade()
          .createMethod(PROTECTED.build(), attr.getMCType(), methodName, scopeParam, scopeJsonParam);
      // create wrapper functions offering the deprecated interface
      // this one does not take the enclosing scope
      ASTCDMethod wrapperMethod = getCDMethodFacade()
          .createMethod(PROTECTED.build(), attr.getMCType(), methodName, scopeJsonParam);

      // Check whether built-in serialization exists. If yes, use it and otherwise make method abstract
      Optional<HookPoint> impl = bitser
          .getDeserialHook(attr.printType(), attr.getName(), "scopeJson");
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

      // this wrapper takes the IScope and checks its type
      ASTCDParameter iScopeParam = getCDParameterFacade().createParameter(getMCTypeFacade()
          .createQualifiedType(I_SCOPE), scopeParam.getName());
      ASTCDMethod wrapperMethod2 = getCDMethodFacade()
          .createMethod(PROTECTED.build(), attr.getMCType(), methodName, iScopeParam, scopeJsonParam);
      String errorCode = symbolTableService.getGeneratedErrorCode(methodName);
      this.replaceTemplate(EMPTY_BODY, wrapperMethod2, new TemplateHookPoint(
          DESERIALIZE_IS_TEMPL, methodName, scopeParam.getMCType().printType(), errorCode));

      methodList.add(wrapperMethod);
      methodList.add(wrapperMethod2);
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
  protected Map<String, Boolean> createSymbolMap(ASTCDDefinition symbolInput) {
    Map<String, Boolean> symbolMap = new LinkedHashMap<>();

    //add local symbols
    for (ASTCDType prod : symbolTableService.getSymbolDefiningProds(symbolInput)) {
      String name = symbolTableService.getSymbolFullName(prod);
      boolean spansScope = symbolTableService.hasScopeStereotype(prod.getModifier());
      symbolMap.put(name, spansScope);
    }

    //add symbols from super grammars
    for (DiagramSymbol cdDefinitionSymbol : symbolTableService.getSuperCDsTransitive()) {
      for (CDTypeSymbol type : symbolTableService.getAllCDTypes(cdDefinitionSymbol)) {
        if (type.isPresentAstNode() && symbolTableService.hasSymbolStereotype(type.getAstNode().getModifier())) {
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

  protected void makeMethodAbstract(ASTCDMethod method, ASTCDAttribute attr) {
    generateAbstractClass = true;
    method.getModifier().setAbstract(true);
    method.add_PreComment(new Comment("  /**\n"
        + "   * Extend the class with the TOP mechanism and implement this method to realize a serialization \n"
        + "   * strategy for the attribute '"+attr.getName()+"'\n"
        + "   * of type '"+attr.printType()+"'!\n"
        + "   */" ));
  }

}
